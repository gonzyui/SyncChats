package xyz.gonzyui.syncchats.cache
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
data class CacheEntry<T>(
    val value: T,
    val timestamp: Long,
    val ttl: Long
) {
    fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > ttl
}
class MessageCache<T> {
    private val cache = ConcurrentHashMap<String, CacheEntry<T>>()
    private val defaultTtl = TimeUnit.MINUTES.toMillis(5)
    fun put(key: String, value: T, ttl: Long = defaultTtl) {
        cache[key] = CacheEntry(value, System.currentTimeMillis(), ttl)
    }
    fun get(key: String): T? {
        val entry = cache[key] ?: return null
        return if (entry.isExpired()) {
            cache.remove(key)
            null
        } else {
            entry.value
        }
    }
    fun remove(key: String) {
        cache.remove(key)
    }
    fun clear() {
        cache.clear()
    }
    fun cleanup() {
        val now = System.currentTimeMillis()
        cache.entries.removeIf { it.value.timestamp + it.value.ttl < now }
    }
    fun size(): Int = cache.size
}
