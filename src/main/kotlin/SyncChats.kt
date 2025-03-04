package xyz.gonzyui.syncchats

import org.bukkit.plugin.java.JavaPlugin
import xyz.gonzyui.syncchats.commands.ReloadCommand
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.discord.DiscordBot
import xyz.gonzyui.syncchats.listeners.ChatListener

class SyncChats : JavaPlugin() {
    override fun onEnable() {
        logger.info("✅ SyncChats enabled! 🌐")

        try {
            ConfigManager.init(this)
        } catch (e: Exception) {
            logger.warning("⚠️ Failed to load config! Default settings will be used.")
        }

        getCommand("syncchatsreload")?.setExecutor(ReloadCommand(this))

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
            DiscordBot.start()
        } else {
            logger.warning("⚠️ Discord bot token or channel ID is missing! Messages from Discord won't be received.")
        }

        server.pluginManager.registerEvents(ChatListener(), this)
    }

    override fun onDisable() {
        logger.info("❌ SyncChats disabled!")
        DiscordBot.shutdown()
    }
}