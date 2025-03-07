package xyz.gonzyui.syncchats.discord

import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import xyz.gonzyui.syncchats.config.ConfigManager

class DiscordListener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val config = ConfigManager.getConfig()
        val channelId = config.getString("discord.channel_id") ?: return

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
        val config = ConfigManager.getConfig()

        val statusType = config.getString("discord.status.type", "WATCHING")?.uppercase()
        val statusTemplate = config.getString("discord.status.content", "Watching {players} players in Minecraft") ?: ""
        val formattedContent = statusTemplate.replace("{players}", onlinePlayers.toString())

        val activity = when (statusType) {
            "PLAYING" -> Activity.playing(formattedContent)
            "LISTENING" -> Activity.listening(formattedContent)
            "WATCHING" -> Activity.watching(formattedContent)
            else -> Activity.playing(formattedContent)
        }

        Bot.jda?.presence?.activity = activity
    }

    fun startStatusUpdateTask() {
        val plugin = Bukkit.getPluginManager().getPlugin("SyncChats")
        if (plugin == null) {
            Bukkit.getLogger().warning("[SyncChats] Plugin not found! Cannot start Discord status update task.")
            return
        }
        object : BukkitRunnable() {
            override fun run() {
                updateBotStatus()
            }
        }.runTaskTimer(plugin, 0L, 600L)
    }
}