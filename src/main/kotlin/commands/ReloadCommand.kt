package xyz.gonzyui.syncchats.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.gonzyui.syncchats.SyncChats
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.discord.DiscordBot

class ReloadCommand(private val plugin: SyncChats) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("syncchats.reload")) {
            sender.sendMessage("§cYou don't have permission to use this command!")
            return true
        }

        sender.sendMessage("§e[SyncChats] Reloading configuration...")

        try {
            ConfigManager.init(plugin)
            sender.sendMessage("§a[SyncChats] Configuration reloaded successfully!")

            val token = ConfigManager.getConfig().getString("discord.token")
            val channelId = ConfigManager.getConfig().getString("discord.channel")

            if (!token.isNullOrEmpty() && !channelId.isNullOrEmpty()) {
                sender.sendMessage("§e[SyncChats] Restarting Discord bot...")
                DiscordBot.shutdown()
                DiscordBot.start()
                sender.sendMessage("§a[SyncChats] Discord bot restarted successfully!")
            } else {
                sender.sendMessage("§c[SyncChats] Discord bot configuration is missing! Bot won't start.")
            }

        } catch (e: Exception) {
            sender.sendMessage("§c[SyncChats] Failed to reload configuration: ${e.message}")
        }

        return true
    }
}
