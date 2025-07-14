package xyz.gonzyui.syncchats.commands
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.gonzyui.syncchats.discord.Bot
import xyz.gonzyui.syncchats.messaging.MessageManager
import xyz.gonzyui.syncchats.stats.Statistics
class StatsCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("syncchats.stats")) {
            val message = MessageManager.getCommandMessage("no_permission")
            sender.sendMessage(MessageManager.formatMinecraftMessage(message))
            return true
        }
        if (args.isNotEmpty() && args[0].equals("reset", ignoreCase = true)) {
            if (!sender.hasPermission("syncchats.stats.reset")) {
                val message = MessageManager.getCommandMessage("no_permission")
                sender.sendMessage(MessageManager.formatMinecraftMessage(message))
                return true
            }
            Statistics.reset()
            val message = MessageManager.getCommandMessage("stats_reset")
            sender.sendMessage(MessageManager.formatMinecraftMessage(message))
            return true
        }
        val statsInfo = getStatsInfo()
        sender.sendMessage(MessageManager.getCommandMessage("stats_title"))
        sender.sendMessage(statsInfo)
        sender.sendMessage(MessageManager.getCommandMessage("stats_separator"))
        return true
    }
    fun getStatsInfo(): String {
        val stats = Statistics.getStats()
        val botStatus = if (Bot.isRunning()) {
            MessageManager.getCommandMessage("bot_status_connected")
        } else {
            MessageManager.getCommandMessage("bot_status_disconnected")
        }
        return """
            ${MessageManager.getCommandMessage("stats_bot_status", mapOf("status" to MessageManager.formatMinecraftMessage(botStatus)))}
            ${MessageManager.getCommandMessage("stats_started", mapOf("time" to stats.startTime))}
            ${MessageManager.getCommandMessage("stats_messages_from_minecraft", mapOf("count" to stats.messagesFromMinecraft.toString()))}
            ${MessageManager.getCommandMessage("stats_messages_to_minecraft", mapOf("count" to stats.messagesToMinecraft.toString()))}
            ${MessageManager.getCommandMessage("stats_total_messages", mapOf("count" to stats.totalMessages.toString()))}
            ${MessageManager.getCommandMessage("stats_discord_mode", mapOf("mode" to MessageManager.getDiscordMode().toString()))}
            ${MessageManager.getCommandMessage("stats_embeds_enabled", mapOf("enabled" to MessageManager.useEmbeds().toString()))}
        """.trimIndent()
    }
}
