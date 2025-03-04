package xyz.gonzyui.syncchats.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import xyz.gonzyui.syncchats.discord.DiscordBot

class ChatListener : Listener {

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        DiscordBot.sendMessageToDiscord(event.player.name, event.message)
    }
}