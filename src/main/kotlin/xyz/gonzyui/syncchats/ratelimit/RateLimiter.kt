package xyz.gonzyui.syncchats.ratelimit
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.utils.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
data class RateLimitEntry(
    val messageCount: Int,
    val lastMessageTime: Long,
    val cooldownUntil: Long
)
object RateLimiter {
    private val userLimits = ConcurrentHashMap<String, RateLimitEntry>()
    fun isAllowed(username: String): Boolean {
        val config = ConfigManager.getConfig()
        if (!config.getBoolean("rate_limiting.enabled", true)) {
            return true
        }
        val maxMessages = config.getInt("rate_limiting.max_messages_per_minute", 10)
        val cooldownSeconds = config.getLong("rate_limiting.cooldown_seconds", 60)
        val currentTime = System.currentTimeMillis()
        val entry = userLimits[username]
        if (entry == null) {
            userLimits[username] = RateLimitEntry(1, currentTime, 0)
            return true
        }
        if (currentTime < entry.cooldownUntil) {
            Logger.debug("User $username is in cooldown")
            return false
        }
        val oneMinuteAgo = currentTime - TimeUnit.MINUTES.toMillis(1)
        if (entry.lastMessageTime < oneMinuteAgo) {
            userLimits[username] = RateLimitEntry(1, currentTime, 0)
            return true
        }
        if (entry.messageCount >= maxMessages) {
            val cooldownUntil = currentTime + TimeUnit.SECONDS.toMillis(cooldownSeconds)
            userLimits[username] = entry.copy(cooldownUntil = cooldownUntil)
            Logger.debug("User $username exceeded rate limit, cooldown until $cooldownUntil")
            return false
        }
        userLimits[username] = entry.copy(
            messageCount = entry.messageCount + 1,
            lastMessageTime = currentTime
        )
        return true
    }
    fun cleanup() {
        val currentTime = System.currentTimeMillis()
        val oneHourAgo = currentTime - TimeUnit.HOURS.toMillis(1)
        userLimits.entries.removeIf { (_, entry) ->
            entry.lastMessageTime < oneHourAgo && entry.cooldownUntil < currentTime
        }
    }
    fun getRemainingCooldown(username: String): Long {
        val entry = userLimits[username] ?: return 0
        val remaining = entry.cooldownUntil - System.currentTimeMillis()
        return if (remaining > 0) remaining else 0
    }
}
