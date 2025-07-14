package xyz.gonzyui.syncchats.config
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import xyz.gonzyui.syncchats.SyncChats
import xyz.gonzyui.syncchats.utils.Logger
import java.io.File
object ConfigManager {
    private lateinit var plugin: SyncChats
    private var config: FileConfiguration? = null
    fun init(pluginInstance: SyncChats) {
        plugin = pluginInstance
        val configFile = File(plugin.dataFolder, "config.yml")
        if (!configFile.exists()) {
            plugin.saveDefaultConfig()
            Logger.info("Generated default config.yml")
        }
        config = YamlConfiguration.loadConfiguration(configFile)
        Logger.info("Configuration initialized successfully")
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
                Logger.warning("config.yml was missing, default config has been created")
            }
            config = YamlConfiguration.loadConfiguration(configFile)
            Logger.info("Configuration reloaded successfully")
        }.onFailure { e ->
            Logger.error("Failed to reload configuration", e)
        }
    }
}