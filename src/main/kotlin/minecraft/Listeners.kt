package xyz.gonzyui.syncchats.minecraft

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.discord.Bot

class Listeners : Listener {

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val config = ConfigManager.getConfig()
        if (config.getString("discord.token").isNullOrEmpty()) {
            Bukkit.getLogger().warning("[SyncChats] Discord bot is not configured. No message sent to Discord.")
            return
        }
        Bot.sendMessageToDiscord(event.player.name, event.message)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        sendMinecraftEventMessage(
            playerName = event.player.name,
            messageKey = "join_message",
            defaultMessage = "{player} joined the server!"
        )
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        sendMinecraftEventMessage(
            playerName = event.player.name,
            messageKey = "left_message",
            defaultMessage = "{player} left the server!"
        )
    }

    private fun sendMinecraftEventMessage(playerName: String, messageKey: String, defaultMessage: String) {
        val config = ConfigManager.getConfig()
        if (!config.getBoolean("minecraft_events.enabled")) return

        val serverName = config.getString("minecraft_events.server_name", "Server")
        val template = config.getString("minecraft_events.$messageKey", defaultMessage)
        val message = template?.replace("{player}", playerName)

        if (!serverName.isNullOrEmpty() && !message.isNullOrEmpty()) {
            Bot.sendMessageToDiscord(serverName, message, Bot.jda?.selfUser?.avatarUrl)
        }
    }
}