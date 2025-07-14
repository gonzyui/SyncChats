package xyz.gonzyui.syncchats.messaging
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.utils.Logger
import org.bukkit.ChatColor
object MessageManager {
    fun getConfigMessage(path: String, placeholders: Map<String, String> = emptyMap()): String {
        val config = ConfigManager.getConfig()
        val message = config.getString("messages.$path", "Missing message: $path")
        return applyPlaceholders(message ?: "Missing message: $path", placeholders)
    }
    fun applyPlaceholders(message: String, placeholders: Map<String, String>): String {
        var result = message
        placeholders.forEach { (key, value) ->
            result = result.replace("{$key}", value)
        }
        return result
    }
    fun formatMinecraftMessage(message: String): String {
        return ChatColor.translateAlternateColorCodes('&', message)
    }
    fun stripMinecraftColors(message: String): String {
        return ChatColor.stripColor(message) ?: message
    }
    fun getChatMessage(direction: String, placeholders: Map<String, String>): String {
        return getConfigMessage("chat.$direction", placeholders)
    }
    fun getEventMessage(event: String, placeholders: Map<String, String>): String {
        return getConfigMessage("events.$event", placeholders)
    }
    fun getSystemMessage(type: String, placeholders: Map<String, String> = emptyMap()): String {
        return getConfigMessage("system.$type", placeholders)
    }
    fun getCommandMessage(type: String, placeholders: Map<String, String> = emptyMap()): String {
        return getConfigMessage("commands.$type", placeholders)
    }
    fun getErrorMessage(type: String, placeholders: Map<String, String> = emptyMap()): String {
        return getConfigMessage("errors.$type", placeholders)
    }
    fun isEventEnabled(event: String): Boolean {
        val config = ConfigManager.getConfig()
        return config.getBoolean("minecraft_events.enabled", true) && 
               config.getBoolean("minecraft_events.events.$event", true)
    }
    fun getDiscordMode(): DiscordMode {
        val config = ConfigManager.getConfig()
        return when (config.getString("discord.mode", "bot")?.lowercase()) {
            "webhook" -> DiscordMode.WEBHOOK_ONLY
            "both" -> DiscordMode.BOTH
            else -> DiscordMode.BOT_ONLY
        }
    }
    fun useEmbeds(): Boolean {
        return ConfigManager.getConfig().getBoolean("display.use_embeds", true)
    }
    fun useAvatars(): Boolean {
        return ConfigManager.getConfig().getBoolean("display.use_avatars", true)
    }
    fun getEmbedColor(): Int {
        val config = ConfigManager.getConfig()
        val colorHex = config.getString("display.embed_color", "00ff00")
        return try {
            Integer.parseInt(colorHex, 16)
        } catch (e: NumberFormatException) {
            Logger.warning("Invalid embed color: $colorHex, using default")
            0x00ff00
        }
    }
}
enum class DiscordMode {
    BOT_ONLY,
    WEBHOOK_ONLY,
    BOTH
}
