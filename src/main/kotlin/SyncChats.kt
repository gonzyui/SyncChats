package xyz.gonzyui.syncchats

import xyz.gonzyui.syncchats.commands.StatusCommand
import xyz.gonzyui.syncchats.commands.ReloadCommand
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.update.UpdateChecker
import xyz.gonzyui.syncchats.minecraft.Listeners
import org.bukkit.plugin.java.JavaPlugin
import xyz.gonzyui.syncchats.discord.Bot

class SyncChats : JavaPlugin() {

    override fun onEnable() {
        logger.info("✅ SyncChats enabled! 🌐")

        runCatching { ConfigManager.init(this) }
            .onFailure {
                logger.warning("⚠️ Failed to load config! Default settings will be used.")
            }

        UpdateChecker().checkForUpdates()

        listOf(
            "syncchatsreload" to ReloadCommand(),
            "syncchatsstatus" to StatusCommand()
        ).forEach { (name, executor) ->
            getCommand(name)?.setExecutor(executor)
        }

        val config = ConfigManager.getConfig()
        initializeDiscord(config)

        server.pluginManager.registerEvents(Listeners(), this)
    }

    override fun onDisable() {
        logger.info("❌ SyncChats disabled!")
        Bot.shutdown()
    }

    private fun initializeDiscord(config: org.bukkit.configuration.Configuration) {
        val webhookUrl = config.getString("discord.webhook_url")
        val discordToken = config.getString("discord.token")
        val discordChannelId = config.getString("discord.channel_id")

        if (webhookUrl.isNullOrEmpty() && discordToken.isNullOrEmpty()) {
            logger.warning("⚠️ No Discord token or webhook configured! Set them in config.yml.")
            return
        }

        if (!discordToken.isNullOrEmpty() && !discordChannelId.isNullOrEmpty()) {
            logger.info("🤖 Starting Discord bot...")
            Bot.start()
        } else {
            logger.warning("⚠️ Discord bot token or channel ID is missing! Messages from Discord won't be received.")
        }
    }
}