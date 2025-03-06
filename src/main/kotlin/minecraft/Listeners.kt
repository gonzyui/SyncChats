package xyz.gonzyui.syncchats.minecraft

import org.bukkit.event.player.AsyncPlayerChatEvent
import xyz.gonzyui.syncchats.config.ConfigManager
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerJoinEvent
import xyz.gonzyui.syncchats.discord.Bot
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.Bukkit

class Listeners : Listener {

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val discordToken = event.player.server.pluginManager.getPlugin("SyncChats")?.config?.getString("discord.token")

        if (discordToken.isNullOrEmpty()) {
            Bukkit.getLogger().warning("[SyncChats] Discord bot is not configured. No message sent to Discord.")
            return
        }

        Bot.sendMessageToDiscord(event.player.name, event.message)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (ConfigManager.getConfig().getBoolean("minecraft_events.enabled")) {
            val playerName = event.player.name
            val serverName = ConfigManager.getConfig().getString("minecraft_events.server_name", "Server")
            val joinMessage = ConfigManager.getConfig().getString("minecraft_events.join_message", "{player} joined the server!")
                ?.replace("{player}", playerName)

            if (serverName != null) {
                if (joinMessage != null) {
                    Bot.sendMessageToDiscord(serverName, joinMessage, Bot.jda?.selfUser?.avatarUrl)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        if (ConfigManager.getConfig().getBoolean("minecraft_events.enabled")) {
            val playerName = event.player.name
            val serverName = ConfigManager.getConfig().getString("minecraft_events.server_name", "Server")
            val leftMessage = ConfigManager.getConfig().getString("minecraft_events.left_message", "{player} left the server!")
                ?.replace("{player}", playerName)

            if (serverName != null) {
                if (leftMessage != null) {
                    Bot.sendMessageToDiscord(serverName, leftMessage, Bot.jda?.selfUser?.avatarUrl)
                }
            }
        }
    }
}
