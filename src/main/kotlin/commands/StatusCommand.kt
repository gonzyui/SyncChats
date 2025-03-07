package xyz.gonzyui.syncchats.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.gonzyui.syncchats.discord.Bot

class StatusCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("syncchats.status")) {
            sender.sendMessage("§cYou don't have permission to use this command!")
            return true
        }

        val botStatus = if (Bot.isRunning()) "Running" else "Stopped"
        val statusMessage = """
            §e[SyncChats] Plugin Status:
            §7Plugin: §aActive
            §7Discord Bot: §b$botStatus
        """.trimIndent()

        sender.sendMessage(statusMessage)
        return true
    }
}