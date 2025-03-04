package xyz.gonzyui.syncchats.discord

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.bukkit.Bukkit
import org.json.simple.JSONObject
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.listeners.DiscordListener
import java.io.IOException
import java.util.concurrent.TimeUnit

object DiscordBot : ListenerAdapter() {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private var jda: net.dv8tion.jda.api.JDA? = null

    fun start() {
        val token = ConfigManager.getConfig().getString("discord.token")
        val channelId = ConfigManager.getConfig().getString("discord.channel_id")

        if (token.isNullOrEmpty() || channelId.isNullOrEmpty()) {
            Bukkit.getLogger().warning("[SyncChats] Discord bot token or channel ID is missing in config.yml! Bot won't start.")
            return
        }

        try {
            jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(DiscordListener())
                .build()
        } catch (e: Exception) {
            Bukkit.getLogger().warning("[SyncChats] Failed to start Discord bot: ${e.message}")
        }
    }

    fun shutdown() {
        jda?.shutdown()
    }

    fun sendMessageToDiscord(playerName: String, message: String) {
        val webhookUrl = ConfigManager.getConfig().getString("discord.webhook_url")
        if (webhookUrl.isNullOrEmpty()) {
            Bukkit.getLogger().warning("[SyncChats] Webhook URL is not set in config!")
            return
        }

        val formattedMessage = ConfigManager.getFormattedMessage(
            "chat_format.minecraft_to_discord",
            mapOf("player" to playerName, "message" to message)
        )

        val avatarUrl = "https://mc-heads.net/avatar/$playerName"
        val json = JSONObject().apply {
            put("username", playerName)
            put("avatar_url", avatarUrl)
            put("content", formattedMessage)
        }

        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder().url(webhookUrl).post(requestBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Bukkit.getLogger().warning("[SyncChats] Failed to send message to Discord Webhook: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Bukkit.getLogger().warning("[SyncChats] Webhook error (${response.code}): ${response.body?.string()}")
                    }
                }
            }
        })
    }
}