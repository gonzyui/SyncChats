package xyz.gonzyui.syncchats.stats
import xyz.gonzyui.syncchats.cache.MessageCache
import xyz.gonzyui.syncchats.utils.Logger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicLong
data class MessageStats(
    val messagesFromMinecraft: Long,
    val messagesToMinecraft: Long,
    val totalMessages: Long,
    val topUsers: Map<String, Int>,
    val startTime: String
)
object Statistics {
    private val messagesFromMinecraft = AtomicLong(0)
    private val messagesToMinecraft = AtomicLong(0)
    private val userMessageCount = MessageCache<AtomicLong>()
    private val startTime = LocalDateTime.now()
    fun incrementMinecraftMessages() {
        messagesFromMinecraft.incrementAndGet()
    }
    fun incrementDiscordMessages() {
        messagesToMinecraft.incrementAndGet()
    }
    fun incrementUserMessages(username: String) {
        val count = userMessageCount.get(username) ?: AtomicLong(0)
        count.incrementAndGet()
        userMessageCount.put(username, count)
    }
    fun getStats(): MessageStats {
        val topUsers = mutableMapOf<String, Int>()
        return MessageStats(
            messagesFromMinecraft = messagesFromMinecraft.get(),
            messagesToMinecraft = messagesToMinecraft.get(),
            totalMessages = messagesFromMinecraft.get() + messagesToMinecraft.get(),
            topUsers = topUsers,
            startTime = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )
    }
    fun reset() {
        messagesFromMinecraft.set(0)
        messagesToMinecraft.set(0)
        userMessageCount.clear()
        Logger.info("Statistics reset")
    }
}
