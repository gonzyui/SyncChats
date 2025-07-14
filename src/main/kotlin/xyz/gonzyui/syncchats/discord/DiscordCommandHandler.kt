package xyz.gonzyui.syncchats.discord
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import xyz.gonzyui.syncchats.commands.*
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.utils.Logger
class DiscordCommandHandler : ListenerAdapter() {
    private val commands = mapOf(
        "reload" to ReloadCommand(),
        "status" to StatusCommand(),
        "stats" to StatsCommand(),
        "serverstatus" to ServerStatusCommand()
    )
    fun registerSlashCommands() {
        val jda = Bot.jda ?: return
        val config = ConfigManager.getConfig()
        val slashCommands = mutableListOf<net.dv8tion.jda.api.interactions.commands.build.SlashCommandData>()
        listOf("reload", "status", "stats", "serverstatus").forEach { commandName ->
            val commandConfig = config.getConfigurationSection("discord.commands.slash_commands.$commandName")
            if (commandConfig?.getBoolean("enabled", true) == true) {
                val name = commandConfig.getString("name", commandName) ?: commandName
                val description = commandConfig.getString("description", "Plugin command") ?: "Plugin command"
                slashCommands.add(
                    Commands.slash(name, description)
                        .setGuildOnly(true)
                )
            }
        }
        if (slashCommands.isNotEmpty()) {
            jda.updateCommands().addCommands(slashCommands).queue(
                { Logger.info("Slash commands registered successfully (${slashCommands.size} commands)") },
                { error -> Logger.error("Failed to register slash commands", error) }
            )
        } else {
            Logger.warning("No slash commands enabled in configuration")
        }
    }
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val config = ConfigManager.getConfig()
        if (!config.getBoolean("discord.commands.enabled", true)) {
            event.reply("❌ Discord commands are disabled.").setEphemeral(true).queue()
            return
        }
        val commandName = event.name
        Logger.debug("Processing Discord slash command: /$commandName")
        val actualCommand = findCommandByName(commandName)
        if (actualCommand == null) {
            event.reply("❌ Unknown command.").setEphemeral(true).queue()
            return
        }
        if (!hasPermission(event, actualCommand)) {
            Logger.debug("Permission denied for user ${event.user.id} on command $actualCommand")
            val noPermMessage = config.getString("discord.commands.slash_commands.$actualCommand.responses.no_permission") 
                ?: "❌ You don't have permission to use this command"
            event.reply(noPermMessage).setEphemeral(true).queue()
            return
        }
        try {
            event.deferReply().queue()
            val result = executeCommand(commands[actualCommand]!!, actualCommand)
            Logger.debug("Command result: $result")
            event.getHook().editOriginal(result).queue()
        } catch (e: Exception) {
            Logger.error("Error executing Discord slash command: $commandName", e)
            val errorMessage = config.getString("discord.commands.slash_commands.$actualCommand.responses.error")
                ?: "❌ An error occurred while executing the command"
            if (event.isAcknowledged) {
                event.getHook().editOriginal(errorMessage).queue()
            } else {
                event.reply(errorMessage).setEphemeral(true).queue()
            }
        }
    }
    private fun findCommandByName(slashName: String): String? {
        val config = ConfigManager.getConfig()
        return commands.keys.find { commandKey ->
            val configuredName = config.getString("discord.commands.slash_commands.$commandKey.name", commandKey)
            configuredName == slashName
        }
    }
    private fun hasPermission(event: SlashCommandInteractionEvent, commandName: String): Boolean {
        val config = ConfigManager.getConfig()
        val userId = event.user.id
        val userRoles = event.member?.roles?.map { it.id } ?: emptyList()
        val adminRoles = config.getStringList("discord.commands.admin_roles")
        if (userRoles.any { it in adminRoles }) {
            return true
        }
        val adminUsers = config.getStringList("discord.commands.admin_users")
        if (userId in adminUsers) {
            return true
        }
        val commandPermissions = config.getConfigurationSection("discord.commands.permissions")
        val allowedRoles = commandPermissions?.getStringList("$commandName.roles") ?: emptyList()
        val allowedUsers = commandPermissions?.getStringList("$commandName.users") ?: emptyList()
        return userId in allowedUsers || userRoles.any { it in allowedRoles }
    }
    private fun executeCommand(command: Any, commandName: String): String {
        val config = ConfigManager.getConfig()
        return when (command) {
            is ReloadCommand -> {
                xyz.gonzyui.syncchats.config.ConfigManager.reloadConfig()
                xyz.gonzyui.syncchats.filter.MessageFilter.loadFilters()
                DiscordLogger.logEvent("Plugin configuration reloaded", "reload")
                config.getString("discord.commands.slash_commands.reload.responses.success") 
                    ?: "✅ Configuration reloaded successfully!"
            }
            is StatusCommand -> {
                val status = command.getStatusInfo()
                val title = config.getString("discord.commands.slash_commands.status.responses.title") 
                    ?: "📊 **Plugin Status**"
                "$title\n```\n$status\n```"
            }
            is StatsCommand -> {
                val stats = command.getStatsInfo()
                val title = config.getString("discord.commands.slash_commands.stats.responses.title") 
                    ?: "📈 **Plugin Statistics**"
                "$title\n```\n$stats\n```"
            }
            is ServerStatusCommand -> {
                val serverStatus = command.getServerStatusInfo()
                val title = config.getString("discord.commands.slash_commands.serverstatus.responses.title") 
                    ?: "🖥️ **Server Status**"
                "$title\n```\n$serverStatus\n```"
            }
            else -> "❌ Unknown command"
        }
    }
}
