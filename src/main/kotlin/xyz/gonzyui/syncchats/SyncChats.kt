package xyz.gonzyui.syncchats
import xyz.gonzyui.syncchats.commands.*
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.update.UpdateChecker
import xyz.gonzyui.syncchats.minecraft.Listeners
import xyz.gonzyui.syncchats.filter.MessageFilter
import xyz.gonzyui.syncchats.ratelimit.RateLimiter
import xyz.gonzyui.syncchats.utils.Logger
import xyz.gonzyui.syncchats.logging.ConsoleLogger
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import xyz.gonzyui.syncchats.discord.Bot
class SyncChats : JavaPlugin() {
    override fun onEnable() {
        Logger.info("SyncChats v${description.version} enabled")
        runCatching { ConfigManager.init(this) }
            .onFailure { Logger.error("Failed to initialize configuration", it) }
        MessageFilter.loadFilters()
        UpdateChecker().checkForUpdates()
        registerCommands()
        registerListeners()
        initializeDiscord()
        startCleanupTasks()
        ConsoleLogger.initialize()
    }
    override fun onDisable() {
        Logger.info("SyncChats disabled")
        ConsoleLogger.shutdown()
        Bot.shutdown()
    }
    private fun registerCommands() {
        listOf(
            "syncchatsreload" to ReloadCommand(),
            "syncchatsstatus" to StatusCommand(),
            "syncchatsstats" to StatsCommand(),
            "syncchatsserverstatus" to ServerStatusCommand()
        ).forEach { (name, executor) ->
            getCommand(name)?.setExecutor(executor)
        }
    }
    private fun registerListeners() {
        server.pluginManager.apply {
            registerEvents(Listeners(), this@SyncChats)
        }
    }
    private fun initializeDiscord() {
        val config = ConfigManager.getConfig()
        val webhookUrl = config.getString("discord.webhook_url")
        val discordToken = config.getString("discord.token")
        val discordChannelId = config.getString("discord.channel_id")
        if (webhookUrl.isNullOrEmpty() && discordToken.isNullOrEmpty()) {
            Logger.warning("No Discord configuration found. Plugin will work in offline mode.")
            return
        }
        if (!discordToken.isNullOrEmpty() && !discordChannelId.isNullOrEmpty()) {
            Logger.info("Starting Discord bot...")
            Bot.start()
        } else {
            Logger.warning("Discord bot configuration incomplete. Only webhook functionality available.")
        }
    }
    private fun startCleanupTasks() {
        val cleanupInterval = ConfigManager.getConfig().getLong("performance.cache_cleanup_interval", 300) * 20L
        object : BukkitRunnable() {
            override fun run() {
                Logger.debug("Running cleanup tasks...")
                RateLimiter.cleanup()
            }
        }.runTaskTimer(this, cleanupInterval, cleanupInterval)
    }
}