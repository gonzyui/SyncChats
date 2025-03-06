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
        plugin.logger.info("✅ Configuration initialized successfully.")
    }

    fun getConfig(): FileConfiguration {
        return config ?: throw IllegalStateException("ConfigManager has not been initialized! Call `ConfigManager.init(plugin)` in `onEnable()`.")
    }

    fun reloadConfig() {
        try {
            val configFile = File(plugin.dataFolder, "config.yml")
            if (!configFile.exists()) {
                plugin.saveDefaultConfig() // Ensures default config exists if missing
                plugin.logger.warning("⚠️ config.yml was missing, default config has been created.")
            }

            config = YamlConfiguration.loadConfiguration(configFile)
            plugin.logger.info("🔄 Configuration reloaded successfully!")
        } catch (e: Exception) {
            plugin.logger.warning("❌ Failed to reload configuration: ${e.message}")
        }
    }

    fun getFormattedMessage(path: String, placeholders: Map<String, String>): String {
        var message = getConfig().getString(path, "{message}") ?: "{message}"
        placeholders.forEach { (key, value) ->
            message = message.replace("{$key}", value)
        }
        return message
    }
}