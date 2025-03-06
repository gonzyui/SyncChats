package xyz.gonzyui.syncchats.commands

import org.bukkit.command.CommandExecutor
import xyz.gonzyui.syncchats.discord.Bot
import org.bukkit.command.CommandSender
import org.bukkit.command.Command

class StatusCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("syncchats.status")) {
            sender.sendMessage("§cYou don't have permission to use this command!")
            return true
        }

        val botStatus = if (Bot.isRunning()) "Running" else "Stopped"

        val statusMessage = StringBuilder()
            .append("§e[SyncChats] Plugin Status:\n")
            .append("§7Plugin: §aActive\n")
            .append("§7Discord Bot: §b$botStatus")
            .toString()

        sender.sendMessage(statusMessage)
        return true
    }
}