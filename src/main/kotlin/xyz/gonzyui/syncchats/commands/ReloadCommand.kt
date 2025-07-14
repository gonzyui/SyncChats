package xyz.gonzyui.syncchats.commands
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.filter.MessageFilter
import xyz.gonzyui.syncchats.messaging.MessageManager
import xyz.gonzyui.syncchats.utils.Logger
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.gonzyui.syncchats.discord.Bot
class ReloadCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("syncchats.reload")) {
            val message = MessageManager.getCommandMessage("no_permission")
            sender.sendMessage(MessageManager.formatMinecraftMessage(message))
            return true
        }
        sender.sendMessage(MessageManager.formatMinecraftMessage(MessageManager.getSystemMessage("reload_starting")))
        try {
            ConfigManager.reloadConfig()
            MessageFilter.loadFilters()
            val message = MessageManager.getCommandMessage("reload_success")
            sender.sendMessage(MessageManager.formatMinecraftMessage(message))
            val config = ConfigManager.getConfig()
            val token = config.getString("discord.token")
            val channelId = config.getString("discord.channel_id")
            if (!token.isNullOrEmpty() && !channelId.isNullOrEmpty()) {
                sender.sendMessage(MessageManager.formatMinecraftMessage(MessageManager.getSystemMessage("bot_restarting")))
                Bot.shutdown()
                Thread.sleep(2000)
                Bot.start()
                sender.sendMessage(MessageManager.formatMinecraftMessage(MessageManager.getSystemMessage("bot_restart_success")))
            } else {
                sender.sendMessage(MessageManager.formatMinecraftMessage(MessageManager.getSystemMessage("bot_token_missing")))
            }
            Logger.info("Configuration reloaded by ${sender.name}")
        } catch (e: Exception) {
            val message = MessageManager.getCommandMessage("reload_error", mapOf("error" to (e.message ?: "Unknown error")))
            sender.sendMessage(MessageManager.formatMinecraftMessage(message))
            Logger.error("Failed to reload configuration", e)
        }
        return true
    }
}