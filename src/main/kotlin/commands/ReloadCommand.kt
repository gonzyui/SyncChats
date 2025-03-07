package xyz.gonzyui.syncchats.commands

import xyz.gonzyui.syncchats.config.ConfigManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.gonzyui.syncchats.discord.Bot

class ReloadCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("syncchats.reload")) {
            sender.sendMessage("§cYou don't have permission to use this command!")
            return true
        }

        sender.sendMessage("§e[SyncChats] Reloading configuration...")

        try {
            ConfigManager.reloadConfig()
            sender.sendMessage("§a[SyncChats] Configuration reloaded successfully!")

            val config = ConfigManager.getConfig()
            val token = config.getString("discord.token")
            val channelId = config.getString("discord.channel_id")

            if (!token.isNullOrEmpty() && !channelId.isNullOrEmpty()) {
                sender.sendMessage("§e[SyncChats] Restarting Discord bot...")
                Bot.shutdown()
                Bot.start()
                sender.sendMessage("§a[SyncChats] Discord bot restarted successfully!")
            } else {
                sender.sendMessage("§c[SyncChats] Discord bot configuration is missing! Bot won't start.")
            }
        } catch (e: Exception) {
            sender.sendMessage("§c[SyncChats] Failed to reload configuration: ${e.message}")
            e.printStackTrace()
        }

        return true
    }
}