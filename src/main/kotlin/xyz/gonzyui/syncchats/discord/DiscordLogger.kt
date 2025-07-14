package xyz.gonzyui.syncchats.discord
import net.dv8tion.jda.api.EmbedBuilder
import org.bukkit.Bukkit
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.utils.Logger
import java.awt.Color
import java.time.Instant
import java.time.format.DateTimeFormatter
object DiscordLogger {
    fun logEvent(message: String, eventType: String, player: String? = null) {
        val config = ConfigManager.getConfig()
        if (!config.getBoolean("discord.logs.enabled", false)) {
            return
        }
        val logChannelId = config.getString("discord.logs.channel_id")
        if (logChannelId.isNullOrEmpty()) {
            Logger.debug("Logs channel ID not configured")
            return
        }
        val jda = Bot.jda
        if (jda == null) {
            Logger.debug("JDA not available for logging")
            return
        }
        val logChannel = jda.getTextChannelById(logChannelId)
        if (logChannel == null) {
            Logger.warning("Log channel not found: $logChannelId")
            return
        }
        val eventEnabled = config.getBoolean("discord.logs.events.$eventType", true)
        if (!eventEnabled) {
            return
        }
        try {
            val embed = createLogEmbed(message, eventType, player)
            logChannel.sendMessageEmbeds(embed).queue(
                { Logger.debug("Log sent to Discord: $eventType") },
                { error -> Logger.error("Failed to send log to Discord", error) }
            )
        } catch (e: Exception) {
            Logger.error("Error creating log embed", e)
        }
    }
    private fun createLogEmbed(message: String, eventType: String, player: String?): net.dv8tion.jda.api.entities.MessageEmbed {
        val config = ConfigManager.getConfig()
        val builder = EmbedBuilder()
        val color = when (eventType) {
            "player_join" -> Color.GREEN
            "player_leave" -> Color.ORANGE
            "player_death" -> Color.RED
            "server_start" -> Color.CYAN
            "server_stop" -> Color.GRAY
            "plugin_reload" -> Color.BLUE
            "errors" -> Color.RED
            "reload" -> Color.BLUE
            else -> Color.LIGHT_GRAY
        }
        builder.setColor(color)
        val emoji = when (eventType) {
            "player_join" -> "🟢"
            "player_leave" -> "🔴"
            "player_death" -> "💀"
            "server_start" -> "🚀"
            "server_stop" -> "🛑"
            "plugin_reload", "reload" -> "🔄"
            "errors" -> "❌"
            else -> "📝"
        }
        builder.setTitle("$emoji Server Log")
        builder.setDescription(message)
        if (!player.isNullOrEmpty()) {
            builder.addField("Player", player, true)
        }
        if (config.getBoolean("discord.logs.format.server_name", true)) {
            val serverName = config.getString("minecraft_events.server_name") ?: "Minecraft Server"
            builder.addField("Server", serverName, true)
        }
        if (config.getBoolean("discord.logs.format.timestamp", true)) {
            builder.setTimestamp(Instant.now())
        }
        builder.setFooter("SyncChats", null)
        return builder.build()
    }
    fun logPlayerJoin(playerName: String) {
        logEvent("$playerName joined the server", "player_join", playerName)
    }
    fun logPlayerLeave(playerName: String) {
        logEvent("$playerName left the server", "player_leave", playerName)
    }
    fun logPlayerDeath(playerName: String, deathMessage: String) {
        logEvent(deathMessage, "player_death", playerName)
    }
    fun logServerStart() {
        val playerCount = Bukkit.getOnlinePlayers().size
        logEvent("Server started with $playerCount players online", "server_start")
    }
    fun logServerStop() {
        logEvent("Server is stopping", "server_stop")
    }
    fun logError(errorMessage: String, throwable: Throwable? = null) {
        val fullMessage = if (throwable != null) {
            "$errorMessage: ${throwable.message}"
        } else {
            errorMessage
        }
        logEvent(fullMessage, "errors")
    }
    fun logPlayerAdvancement(playerName: String, advancement: String) {
        val config = ConfigManager.getConfig()
        if (!config.getBoolean("discord.logs.enabled", false)) return
        val enabledEvents = config.getStringList("discord.logs.events")
        if ("advancement" !in enabledEvents) return
        logEvent("Player **$playerName** got advancement: $advancement", "advancement")
    }
}
