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
        return config ?: throw IllegalStateException(
            "ConfigManager has not been initialized! Call `ConfigManager.init(plugin)` in `onEnable()`."
        )
    }

    fun reloadConfig() {
        runCatching {
            val configFile = File(plugin.dataFolder, "config.yml")
            if (!configFile.exists()) {
                plugin.saveDefaultConfig()
                plugin.logger.warning("⚠️ config.yml was missing, default config has been created.")
            }
            config = YamlConfiguration.loadConfiguration(configFile)
            plugin.logger.info("🔄 Configuration reloaded successfully!")
        }.onFailure { e ->
            plugin.logger.warning("❌ Failed to reload configuration: ${e.message}")
        }
    }

    fun getFormattedMessage(path: String, placeholders: Map<String, String>): String {
        return getConfig().getString(path, "{message}")?.let { baseMessage ->
            placeholders.entries.fold(baseMessage) { message, (key, value) ->
                message.replace("{$key}", value)
            }
        } ?: "{message}"
    }
}