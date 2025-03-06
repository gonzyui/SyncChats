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

        try {
            ConfigManager.init(this)
        } catch (e: Exception) {
            logger.warning("⚠️ Failed to load config! Default settings will be used.")
        }

        UpdateChecker().checkForUpdates()

        val reloadCommand = getCommand("syncchatsreload")
        reloadCommand?.setExecutor(ReloadCommand())

        val statusCommand = getCommand("syncchatsstatus")
        statusCommand?.setExecutor(StatusCommand())

        val config = ConfigManager.getConfig()
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

        server.pluginManager.registerEvents(Listeners(), this)
    }

    override fun onDisable() {
        logger.info("❌ SyncChats disabled!")

        Bot.shutdown()
    }
}