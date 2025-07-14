package xyz.gonzyui.syncchats.discord
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.Activity
import xyz.gonzyui.syncchats.messaging.MessageManager
import xyz.gonzyui.syncchats.messaging.DiscordMode
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.utils.Logger
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.awt.Color
import java.time.Instant
object DiscordMessageSender {
    fun sendChatMessage(playerName: String, message: String) {
        val mode = MessageManager.getDiscordMode()
        when (mode) {
            DiscordMode.BOT_ONLY -> sendViaBotOnly(playerName, message)
            DiscordMode.WEBHOOK_ONLY -> sendViaWebhookOnly(playerName, message)
            DiscordMode.BOTH -> sendViaBoth(playerName, message)
        }
    }
    fun sendEventMessage(event: String, placeholders: Map<String, String>) {
        if (!MessageManager.isEventEnabled(event)) return
        val message = MessageManager.getEventMessage(event, placeholders)
        val serverName = ConfigManager.getConfig().getString("minecraft_events.server_name", "Server")
        when (MessageManager.getDiscordMode()) {
            DiscordMode.BOT_ONLY -> {
                if (MessageManager.useEmbeds()) {
                    sendEmbedMessage(createEventEmbed(event, message, placeholders))
                } else {
                    sendSimpleMessage(serverName ?: "Server", message)
                }
            }
            DiscordMode.WEBHOOK_ONLY, DiscordMode.BOTH -> {
                sendViaWebhook(serverName ?: "Server", message)
            }
        }
    }
    private fun sendViaBotOnly(playerName: String, message: String) {
        if (!Bot.isRunning()) {
            Logger.warning("Bot is not running, cannot send message")
            return
        }
        val formattedMessage = MessageManager.getChatMessage("minecraft_to_discord", mapOf(
            "player" to playerName,
            "message" to message
        ))
        if (MessageManager.useEmbeds()) {
            val embed = createChatEmbed(playerName, formattedMessage)
            sendEmbedMessage(embed)
        } else {
            sendSimpleMessage(playerName, formattedMessage)
        }
    }
    private fun sendViaWebhookOnly(playerName: String, message: String) {
        val formattedMessage = MessageManager.getChatMessage("minecraft_to_discord", mapOf(
            "player" to playerName,
            "message" to message
        ))
        sendViaWebhook(playerName, formattedMessage)
    }
    private fun sendViaBoth(playerName: String, message: String) {
        sendViaWebhookOnly(playerName, message)
    }
    private fun sendViaWebhook(playerName: String, message: String) {
        val avatarUrl = if (MessageManager.useAvatars()) {
            "https://mc-heads.net/avatar/$playerName"
        } else null
        Bot.sendMessageToDiscord(playerName, message, avatarUrl)
    }
    private fun sendSimpleMessage(username: String, message: String) {
        if (!Bot.isRunning()) return
        val config = ConfigManager.getConfig()
        val channelId = config.getString("discord.channel_id") ?: return
        val channel = Bot.jda?.getTextChannelById(channelId) ?: return
        channel.sendMessage(message).queue()
    }
    private fun sendEmbedMessage(embed: MessageEmbed) {
        if (!Bot.isRunning()) return
        val config = ConfigManager.getConfig()
        val channelId = config.getString("discord.channel_id") ?: return
        val channel = Bot.jda?.getTextChannelById(channelId) ?: return
        channel.sendMessageEmbeds(embed).queue()
    }
    private fun createChatEmbed(playerName: String, message: String): MessageEmbed {
        val builder = EmbedBuilder()
            .setAuthor(playerName, null, if (MessageManager.useAvatars()) "https://mc-heads.net/avatar/$playerName" else null)
            .setDescription(message)
            .setColor(MessageManager.getEmbedColor())
        if (ConfigManager.getConfig().getBoolean("display.show_timestamps", true)) {
            builder.setTimestamp(Instant.now())
        }
        return builder.build()
    }
    private fun createEventEmbed(event: String, message: String, placeholders: Map<String, String>): MessageEmbed {
        val builder = EmbedBuilder()
            .setDescription(message)
            .setColor(getEventColor(event))
        when (event) {
            "join", "leave", "first_join" -> {
                val playerName = placeholders["player"] ?: "Unknown"
                builder.setThumbnail("https://mc-heads.net/avatar/$playerName")
                builder.addField("Online Players", "${Bukkit.getOnlinePlayers().size}/${Bukkit.getMaxPlayers()}", true)
            }
            "achievement" -> {
                val playerName = placeholders["player"] ?: "Unknown"
                val achievement = placeholders["achievement"] ?: "Unknown"
                builder.setThumbnail("https://mc-heads.net/avatar/$playerName")
                builder.addField("Achievement", achievement, false)
            }
        }
        if (ConfigManager.getConfig().getBoolean("display.show_timestamps", true)) {
            builder.setTimestamp(Instant.now())
        }
        builder.setFooter("SyncChats", null)
        return builder.build()
    }
    private fun getEventColor(event: String): Color {
        return when (event) {
            "join", "first_join" -> Color.GREEN
            "leave" -> Color.RED
            "death" -> Color.DARK_GRAY
            "achievement" -> Color.YELLOW
            else -> Color.BLUE
        }
    }
    fun updateBotStatus() {
        if (!Bot.isRunning()) return
        val config = ConfigManager.getConfig()
        val statusType = config.getString("discord.status.type", "WATCHING")?.uppercase()
        val statusContent = config.getString("discord.status.content", "{players} players online")
        val onlinePlayers = Bukkit.getOnlinePlayers().size
        val formattedContent = MessageManager.applyPlaceholders(statusContent ?: "{players} players online", 
            mapOf("players" to onlinePlayers.toString()))
        val activity = when (statusType) {
            "PLAYING" -> Activity.playing(formattedContent)
            "LISTENING" -> Activity.listening(formattedContent)
            "WATCHING" -> Activity.watching(formattedContent)
            else -> Activity.watching(formattedContent)
        }
        Bot.jda?.presence?.activity = activity
    }
}
