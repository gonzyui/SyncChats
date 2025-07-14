package xyz.gonzyui.syncchats.utils
import org.bukkit.Bukkit
import java.util.logging.Level
object Logger {
    private const val PREFIX = "[SyncChats]"
    fun info(message: String) {
        Bukkit.getLogger().info("$PREFIX $message")
    }
    fun warning(message: String) {
        Bukkit.getLogger().warning("$PREFIX $message")
    }
    fun error(message: String, throwable: Throwable? = null) {
        Bukkit.getLogger().log(Level.SEVERE, "$PREFIX $message", throwable)
    }
    fun debug(message: String) {
        if (isDebugEnabled()) {
            Bukkit.getLogger().info("$PREFIX [DEBUG] $message")
        }
    }
    private fun isDebugEnabled(): Boolean {
        return try {
            val config = xyz.gonzyui.syncchats.config.ConfigManager.getConfig()
            config.getBoolean("debug", false)
        } catch (e: Exception) {
            false
        }
    }
}
