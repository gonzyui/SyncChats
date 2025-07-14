package xyz.gonzyui.syncchats.commands
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.gonzyui.syncchats.discord.Bot
import xyz.gonzyui.syncchats.messaging.MessageManager
class StatusCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("syncchats.status")) {
            val message = MessageManager.getCommandMessage("no_permission")
            sender.sendMessage(MessageManager.formatMinecraftMessage(message))
            return true
        }
        val statusInfo = getStatusInfo()
        val header = MessageManager.getCommandMessage("status_header")
        sender.sendMessage(MessageManager.formatMinecraftMessage("$header\n$statusInfo"))
        return true
    }
    fun getStatusInfo(): String {
        val botStatus = if (Bot.isRunning()) {
            MessageManager.getCommandMessage("bot_status_connected")
        } else {
            MessageManager.getCommandMessage("bot_status_disconnected")
        }
        return MessageManager.getCommandMessage("status_info", mapOf(
            "plugin_status" to MessageManager.getCommandMessage("plugin_status_active"),
            "bot_status" to botStatus
        ))
    }
}