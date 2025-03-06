package xyz.gonzyui.syncchats.discord

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import xyz.gonzyui.syncchats.config.ConfigManager
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.entities.Activity
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.Bukkit

class DiscordListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val channelId = ConfigManager.getConfig().getString("discord.channel_id") ?: return

        if (event.author.isBot || event.channel.id != channelId) return

        val username = event.member?.effectiveName ?: event.author.name
        val message = event.message.contentDisplay

        Bukkit.getLogger().info("[SyncChats] Received Discord message from $username: $message")

        val formattedMessage = ConfigManager.getFormattedMessage(
            "chat_format.discord_to_minecraft",
            mapOf("user" to username, "message" to message)
        )

        Bukkit.broadcastMessage(formattedMessage)
    }

    fun updateBotStatus() {
        val onlinePlayers = Bukkit.getOnlinePlayers().size
        val statusType = ConfigManager.getConfig().getString("discord.status.type", "WATCHING")
        val statusContent = ConfigManager.getConfig().getString("discord.status.content", "Watching {players} players in Minecraft")

        val formattedContent = statusContent?.replace("{players}", onlinePlayers.toString())

        val activity = when (statusType?.uppercase()) {
            "PLAYING" -> formattedContent?.let { Activity.playing(it) }
            "WATCHING" -> formattedContent?.let { Activity.watching(it) }
            "LISTENING" -> formattedContent?.let { Activity.listening(it) }
            else -> formattedContent?.let { Activity.playing(it) }
        }

        Bot.jda?.presence?.activity = activity
    }

    fun startStatusUpdateTask() {
        Bukkit.getPluginManager().getPlugin("SyncChats")?.let {
            object : BukkitRunnable() {
                override fun run() {
                    updateBotStatus()
                }
            }.runTaskTimer(it, 0L, 600L)
        }
    }
}