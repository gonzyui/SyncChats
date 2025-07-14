package xyz.gonzyui.syncchats.minecraft
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import xyz.gonzyui.syncchats.discord.DiscordMessageSender
import xyz.gonzyui.syncchats.discord.DiscordLogger
import xyz.gonzyui.syncchats.filter.MessageFilter
import xyz.gonzyui.syncchats.messaging.MessageManager
import xyz.gonzyui.syncchats.ratelimit.RateLimiter
import xyz.gonzyui.syncchats.stats.Statistics
import xyz.gonzyui.syncchats.utils.Logger
import org.bukkit.ChatColor
class Listeners : Listener {
    private val firstJoinPlayers = mutableSetOf<String>()
    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val playerName = event.player.name
        val message = event.message
        if (!RateLimiter.isAllowed(playerName)) {
            val remaining = RateLimiter.getRemainingCooldown(playerName)
            val remainingSeconds = remaining / 1000
            val errorMessage = MessageManager.getErrorMessage("rate_limit", mapOf(
                "seconds" to remainingSeconds.toString()
            ))
            event.player.sendMessage(MessageManager.formatMinecraftMessage(errorMessage))
            event.isCancelled = true
            return
        }
        if (!MessageFilter.isMessageAllowed(message, playerName)) {
            val errorMessage = MessageManager.getErrorMessage("filtered_message")
            event.player.sendMessage(MessageManager.formatMinecraftMessage(errorMessage))
            event.isCancelled = true
            return
        }
        val filteredMessage = MessageFilter.filterMessage(message, playerName)
        Statistics.incrementMinecraftMessages()
        Statistics.incrementUserMessages(playerName)
        DiscordMessageSender.sendChatMessage(playerName, filteredMessage)
    }
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val playerName = event.player.name
        val isFirstJoin = !event.player.hasPlayedBefore()
        if (isFirstJoin) {
            firstJoinPlayers.add(playerName)
            DiscordMessageSender.sendEventMessage("first_join", mapOf("player" to playerName))
        } else {
            DiscordMessageSender.sendEventMessage("join", mapOf("player" to playerName))
        }
        DiscordLogger.logPlayerJoin(event.player.name)
        DiscordMessageSender.updateBotStatus()
    }
    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val playerName = event.player.name
        firstJoinPlayers.remove(playerName)
        DiscordMessageSender.sendEventMessage("leave", mapOf("player" to playerName))
        DiscordLogger.logPlayerLeave(event.player.name)
        org.bukkit.Bukkit.getScheduler().runTaskLater(
            org.bukkit.Bukkit.getPluginManager().getPlugin("SyncChats")!!,
            Runnable { DiscordMessageSender.updateBotStatus() },
            20L // 1 second delay
        )
    }
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val playerName = event.entity.name
        val deathMessage = event.deathMessage ?: "$playerName died"
        DiscordLogger.logPlayerDeath(event.entity.name, deathMessage)
        DiscordMessageSender.sendEventMessage("death", mapOf(
            "player" to playerName,
            "message" to deathMessage
        ))
    }
    @EventHandler
    fun onPlayerAdvancement(event: PlayerAdvancementDoneEvent) {
        val advancement = event.advancement
        val player = event.player
        val advancementName = advancement.key.key
        if (advancementName.startsWith("recipes/") || advancementName.startsWith("husbandry/")) {
            return
        }
        DiscordLogger.logPlayerAdvancement(player.name, advancementName)
        DiscordMessageSender.sendEventMessage("advancement", mapOf(
            "player" to player.name,
            "advancement" to advancementName
        ))
    }
}