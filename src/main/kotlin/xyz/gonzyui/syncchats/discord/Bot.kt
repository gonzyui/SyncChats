package xyz.gonzyui.syncchats.discord
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import org.bukkit.Bukkit
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.messaging.MessageManager
import xyz.gonzyui.syncchats.utils.Logger
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
object Bot : ListenerAdapter() {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    var jda: JDA? = null
        private set
    private val discordListener = DiscordListener()
    private val commandHandler = DiscordCommandHandler()
    fun start() {
        val config = ConfigManager.getConfig()
        val token = config.getString("discord.token")
        val channelId = config.getString("discord.channel_id")
        if (token.isNullOrEmpty() || channelId.isNullOrEmpty()) {
            Bukkit.getLogger().warning(MessageManager.getSystemMessage("bot_token_missing"))
            return
        }
        try {
            jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(discordListener, commandHandler)
                .build().also { jdaInstance ->
                    jdaInstance.addEventListener(object : ListenerAdapter() {
                        override fun onReady(event: ReadyEvent) {
                            discordListener.startStatusUpdateTask()
                            commandHandler.registerSlashCommands()
                        }
                    })
                }
        } catch (e: Exception) {
            Bukkit.getLogger().warning(MessageManager.getSystemMessage("bot_start_failed", mapOf("error" to (e.message ?: "Unknown error"))))
        }
    }
    fun shutdown() {
        runCatching {
            jda?.shutdown()
            Bukkit.getLogger().info(MessageManager.getSystemMessage("bot_shutdown_success"))
        }.onFailure {
            Bukkit.getLogger().warning(MessageManager.getSystemMessage("bot_shutdown_failed", mapOf("error" to (it.message ?: "Unknown error"))))
        }
    }
    fun isRunning(): Boolean = jda?.status == JDA.Status.CONNECTED
    fun sendMessageToDiscord(playerName: String, message: String, avatarUrl: String? = null) {
        if (!isRunning()) {
            Bukkit.getLogger().warning(MessageManager.getSystemMessage("bot_not_running_message"))
            return
        }
        val config = ConfigManager.getConfig()
        val webhookUrl = config.getString("discord.webhook_url")
        if (webhookUrl.isNullOrEmpty()) {
            Bukkit.getLogger().warning(MessageManager.getSystemMessage("webhook_url_missing"))
            return
        }
        val formattedMessage = xyz.gonzyui.syncchats.messaging.MessageManager.getChatMessage(
            "minecraft_to_discord",
            mapOf("player" to playerName, "message" to message)
        )
        val finalAvatarUrl = avatarUrl ?: "https://mc-heads.net/avatar/$playerName"
        val json = JSONObject().apply {
            put("username", playerName)
            put("avatar_url", finalAvatarUrl)
            put("content", formattedMessage)
        }
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder().url(webhookUrl).post(requestBody).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Bukkit.getLogger().warning(MessageManager.getSystemMessage("webhook_error", mapOf("error" to (e.message ?: "Unknown error"))))
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Bukkit.getLogger().warning(MessageManager.getSystemMessage("webhook_response_error", mapOf(
                            "code" to response.code.toString(),
                            "message" to (response.body?.string() ?: "Unknown error")
                        )))
                    } else {
                        Bukkit.getLogger().info(MessageManager.getSystemMessage("webhook_success"))
                    }
                }
            }
        })
    }
}