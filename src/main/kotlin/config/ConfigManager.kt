package xyz.gonzyui.syncchats.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import xyz.gonzyui.syncchats.SyncChats
import java.io.File

object ConfigManager {
    private lateinit var plugin: SyncChats
    private var config: FileConfiguration? = null

    fun init(pluginInstance: SyncChats) {
        plugin = pluginInstance
        val configFile = File(plugin.dataFolder, "config.yml")

        if (!configFile.exists()) {
            plugin.saveDefaultConfig()
            plugin.logger.info("🛠 Generating default config.yml...")
        }

        config = YamlConfiguration.loadConfiguration(configFile)
    }

    fun getConfig(): FileConfiguration {
        return config ?: throw IllegalStateException("ConfigManager has not been initialized! Call `ConfigManager.init(plugin)` in `onEnable()`.")
    }

    fun getFormattedMessage(path: String, placeholders: Map<String, String>): String {
        var message = getConfig().getString(path, "{message}") ?: "{message}"
        placeholders.forEach { (key, value) ->
            message = message.replace("{$key}", value)
        }
        return message
    }
}