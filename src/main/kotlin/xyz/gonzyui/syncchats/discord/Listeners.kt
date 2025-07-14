package xyz.gonzyui.syncchats.discord
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.filter.MessageFilter
import xyz.gonzyui.syncchats.messaging.MessageManager
import xyz.gonzyui.syncchats.messaging.DiscordMode
import xyz.gonzyui.syncchats.ratelimit.RateLimiter
import xyz.gonzyui.syncchats.stats.Statistics
import xyz.gonzyui.syncchats.utils.Logger
class DiscordListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val mode = MessageManager.getDiscordMode()
        if (mode == DiscordMode.WEBHOOK_ONLY) return
        val config = ConfigManager.getConfig()
        val channelId = config.getString("discord.channel_id") ?: return
        if (event.author.isBot || event.channel.id != channelId) return
        val username = event.member?.effectiveName ?: event.author.name
        val message = event.message.contentDisplay
        Logger.debug("Received Discord message from $username: $message")
        if (!RateLimiter.isAllowed("discord:$username")) {
            Logger.debug("Rate limit exceeded for Discord user: $username")
            return
        }
        if (!MessageFilter.isMessageAllowed(message, username)) {
            Logger.debug("Message from Discord user $username was filtered")
            return
        }
        val filteredMessage = MessageFilter.filterMessage(message, username)
        Statistics.incrementDiscordMessages()
        Statistics.incrementUserMessages("discord:$username")
        val formattedMessage = MessageManager.getChatMessage("discord_to_minecraft", mapOf(
            "user" to username,
            "message" to filteredMessage
        ))
        Bukkit.broadcastMessage(MessageManager.formatMinecraftMessage(formattedMessage))
        if (config.getBoolean("features.mentions", true)) {
            handleMentions(event, filteredMessage)
        }
    }
    private fun handleMentions(event: MessageReceivedEvent, message: String) {
        val mentionPattern = "@(\\w+)".toRegex()
        val mentions = mentionPattern.findAll(message)
        for (mention in mentions) {
            val playerName = mention.groupValues[1]
            val player = Bukkit.getPlayer(playerName)
            if (player != null && player.isOnline) {
                val mentionMessage = "§e[Discord] You were mentioned by ${event.author.name}!"
                player.sendMessage(mentionMessage)
                Logger.debug("Notified player $playerName about Discord mention")
            }
        }
    }
    fun startStatusUpdateTask() {
        val plugin = Bukkit.getPluginManager().getPlugin("SyncChats")
        if (plugin == null) {
            Logger.warning("Plugin not found! Cannot start Discord status update task.")
            return
        }
        val config = ConfigManager.getConfig()
        val updateInterval = config.getLong("discord.status.update_interval", 600) * 20L // Convert to ticks
        object : BukkitRunnable() {
            override fun run() {
                DiscordMessageSender.updateBotStatus()
            }
        }.runTaskTimer(plugin, 0L, updateInterval)
    }
}