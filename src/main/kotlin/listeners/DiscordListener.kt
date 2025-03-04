package xyz.gonzyui.syncchats.listeners

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import xyz.gonzyui.syncchats.config.ConfigManager

class DiscordListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val channelId = ConfigManager.getConfig().getString("discord.channel_id") ?: return

        if (event.author.isBot || event.channel.id != channelId) return

        val username = event.member?.effectiveName ?: event.author.name // ✅ Vérifie si le pseudo est bien récupéré
        val message = event.message.contentDisplay

        Bukkit.getLogger().info("[SyncChats] Received Discord message from $username: $message") // ✅ Debug log

        val formattedMessage = ConfigManager.getFormattedMessage(
            "chat_format.discord_to_minecraft",
            mapOf("user" to username, "message" to message)
        )

        Bukkit.broadcastMessage(formattedMessage)
    }
}