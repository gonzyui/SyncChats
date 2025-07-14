package xyz.gonzyui.syncchats.commands
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.gonzyui.syncchats.discord.Bot
import xyz.gonzyui.syncchats.discord.DiscordMessageSender
import xyz.gonzyui.syncchats.messaging.MessageManager
import xyz.gonzyui.syncchats.config.ConfigManager
class ServerStatusCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("syncchats.serverstatus")) {
            val message = MessageManager.getCommandMessage("no_permission")
            sender.sendMessage(MessageManager.formatMinecraftMessage(message))
            return true
        }
        if (!Bot.isRunning()) {
            val message = MessageManager.getErrorMessage("bot_not_running")
            sender.sendMessage(MessageManager.formatMinecraftMessage(message))
            return true
        }
        val config = ConfigManager.getConfig()
        val channelId = config.getString("discord.channel_id")
        if (channelId.isNullOrEmpty()) {
            val message = MessageManager.getErrorMessage("invalid_config")
            sender.sendMessage(MessageManager.formatMinecraftMessage(message))
            return true
        }
        val channel = Bot.jda?.getTextChannelById(channelId)
        if (channel == null) {
            val message = MessageManager.getErrorMessage("invalid_config")
            sender.sendMessage(MessageManager.formatMinecraftMessage(message))
            return true
        }
        try {
            val statusInfo = getServerStatusInfo()
            val onlinePlayers = org.bukkit.Bukkit.getOnlinePlayers()
            val playerList = if (onlinePlayers.size <= 10) {
                onlinePlayers.joinToString(", ") { it.name }
            } else {
                onlinePlayers.take(10).joinToString(", ") { it.name } + " and ${onlinePlayers.size - 10} more..."
            }
            val placeholders = mapOf(
                "players" to onlinePlayers.size.toString(),
                "max_players" to org.bukkit.Bukkit.getMaxPlayers().toString(),
                "player_list" to (playerList.ifEmpty { "No players online" })
            )
            DiscordMessageSender.sendEventMessage("server_status", placeholders)
            sender.sendMessage("${ChatColor.GREEN}Server status sent to Discord!")
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}Error sending server status: ${e.message}")
        }
        return true
    }
    fun getServerStatusInfo(): String {
        val onlinePlayers = org.bukkit.Bukkit.getOnlinePlayers()
        val playerList = if (onlinePlayers.size <= 10) {
            onlinePlayers.joinToString(", ") { it.name }
        } else {
            onlinePlayers.take(10).joinToString(", ") { it.name } + " and ${onlinePlayers.size - 10} more..."
        }
        return """
            Online Players: ${onlinePlayers.size}/${org.bukkit.Bukkit.getMaxPlayers()}
            Players: ${playerList.ifEmpty { "No players online" }}
        """.trimIndent()
    }
}
