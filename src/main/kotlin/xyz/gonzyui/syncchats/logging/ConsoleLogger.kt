package xyz.gonzyui.syncchats.logging
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.discord.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import org.bukkit.ChatColor
import java.awt.Color
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Level
object ConsoleLogger {
    private val messageQueue = ConcurrentLinkedQueue<LogMessage>()
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private var consoleHandler: ConsoleHandler? = null
    private val excludePatterns = listOf(
        Pattern.compile(".*\\[DEBUG\\].*"),
        Pattern.compile(".*\\[TRACE\\].*"),
        Pattern.compile(".*Can't keep up!.*"),
        Pattern.compile(".*Skipping.*ticks.*"),
        Pattern.compile(".*moved too quickly.*"),
        Pattern.compile(".*moved wrongly.*")
    )
    private val levelColors = mapOf(
        Level.SEVERE to Color.RED,
        Level.WARNING to Color.ORANGE,
        Level.INFO to Color.GREEN,
        Level.CONFIG to Color.BLUE,
        Level.FINE to Color.GRAY,
        Level.FINER to Color.GRAY,
        Level.FINEST to Color.GRAY
    )
    data class LogMessage(
        val level: Level,
        val message: String,
        val timestamp: Instant,
        val source: String?
    )
    fun initialize() {
        if (!isEnabled()) return
        consoleHandler = ConsoleHandler()
        val rootLogger = java.util.logging.Logger.getLogger("")
        rootLogger.addHandler(consoleHandler)
        startMessageProcessor()
        xyz.gonzyui.syncchats.utils.Logger.info("Console logging to Discord initialized")
    }
    fun shutdown() {
        consoleHandler?.let { handler ->
            val rootLogger = java.util.logging.Logger.getLogger("")
            rootLogger.removeHandler(handler)
            handler.close()
        }
        executor.shutdown()
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
    private fun isEnabled(): Boolean {
        val config = ConfigManager.getConfig()
        return config.getBoolean("discord.logs.enabled", false) &&
               config.getBoolean("discord.logs.console.enabled", false)
    }
    private fun startMessageProcessor() {
        executor.scheduleWithFixedDelay({
            processLogMessages()
        }, 0, 5, TimeUnit.SECONDS)
    }
    private fun processLogMessages() {
        if (!isEnabled() || messageQueue.isEmpty()) return
        try {
            val config = ConfigManager.getConfig()
            val channelId = config.getString("discord.logs.channel_id") ?: return
            val channel = Bot.jda?.getTextChannelById(channelId) ?: return
            val maxMessages = config.getInt("discord.logs.console.batch_size", 10)
            val messages = mutableListOf<LogMessage>()
            repeat(maxMessages) {
                val message = messageQueue.poll() ?: return@repeat
                messages.add(message)
            }
            if (messages.isEmpty()) return
            val groupedMessages = groupMessages(messages)
            groupedMessages.forEach { (level, messageList) ->
                sendLogEmbed(channel, level, messageList)
            }
        } catch (e: Exception) {
            xyz.gonzyui.syncchats.utils.Logger.error("Error processing console logs", e)
        }
    }
    private fun groupMessages(messages: List<LogMessage>): Map<Level, List<LogMessage>> {
        return messages.groupBy { it.level }
    }
    private fun sendLogEmbed(channel: TextChannel, level: Level, messages: List<LogMessage>) {
        val config = ConfigManager.getConfig()
        val embed = EmbedBuilder()
            .setTitle("🖥️ Console Logs - ${level.name}")
            .setColor(levelColors[level] ?: Color.GRAY)
            .setTimestamp(Instant.now())
            .setFooter("SyncChats Console Logger", null)
        val description = StringBuilder()
        messages.take(10).forEach { message ->
            val cleanMessage = cleanMessage(message.message)
            val truncated = if (cleanMessage.length > 200) {
                cleanMessage.substring(0, 200) + "..."
            } else cleanMessage
            description.append("```")
            description.append(truncated)
            description.append("```\n")
        }
        if (messages.size > 10) {
            description.append("*... and ${messages.size - 10} more messages*")
        }
        embed.setDescription(description.toString())
        if (config.getBoolean("discord.logs.format.server_name", true)) {
            val serverName = config.getString("minecraft_events.server_name") ?: "Minecraft Server"
            embed.addField("Server", serverName, true)
        }
        if (config.getBoolean("discord.logs.format.message_count", true)) {
            embed.addField("Message Count", messages.size.toString(), true)
        }
        channel.sendMessageEmbeds(embed.build()).queue()
    }
    private fun cleanMessage(message: String): String {
        var cleaned = message.replace(Regex("\\u001B\\[[;\\d]*m"), "")
        cleaned = ChatColor.stripColor(cleaned) ?: cleaned
        cleaned = cleaned.trim()
        return cleaned
    }
    private fun shouldLog(record: LogRecord): Boolean {
        val config = ConfigManager.getConfig()
        val message = record.message ?: return false
        val minLevel = config.getString("discord.logs.console.min_level", "INFO")
        val minLevelEnum = try {
            Level.parse(minLevel)
        } catch (e: IllegalArgumentException) {
            Level.INFO
        }
        if (record.level.intValue() < minLevelEnum.intValue()) {
            return false
        }
        excludePatterns.forEach { pattern ->
            if (pattern.matcher(message).matches()) {
                return false
            }
        }
        val enabledLoggers = config.getStringList("discord.logs.console.enabled_loggers")
        if (enabledLoggers.isNotEmpty()) {
            val loggerName = record.loggerName ?: ""
            if (enabledLoggers.none { loggerName.startsWith(it) }) {
                return false
            }
        }
        return true
    }
    fun logCustomMessage(level: Level, message: String, source: String? = null) {
        if (!isEnabled()) return
        val logMessage = LogMessage(
            level = level,
            message = message,
            timestamp = Instant.now(),
            source = source
        )
        messageQueue.offer(logMessage)
    }
    private class ConsoleHandler : Handler() {
        override fun publish(record: LogRecord?) {
            if (record == null || !shouldLog(record)) return
            val message = LogMessage(
                level = record.level,
                message = record.message ?: "",
                timestamp = Instant.ofEpochMilli(record.millis),
                source = record.loggerName
            )
            messageQueue.offer(message)
        }
        override fun flush() {}
        override fun close() {}
    }
}
