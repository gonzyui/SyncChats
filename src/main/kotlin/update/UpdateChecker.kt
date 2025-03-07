package xyz.gonzyui.syncchats.update

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import xyz.gonzyui.syncchats.config.ConfigManager
import org.json.simple.JSONObject
import java.net.HttpURLConnection
import okhttp3.OkHttpClient
import org.bukkit.Bukkit
import okhttp3.Request
import java.net.URL

class UpdateChecker {

    companion object {
        private val httpClient = OkHttpClient()
        private const val PLUGIN_ID = "123042"
    }

    fun checkForUpdates() {
        val config = ConfigManager.getConfig()
        if (!config.getBoolean("updates.check")) return

        try {
            val latestVersion = fetchLatestVersion() ?: return
            val currentVersion = Bukkit.getPluginManager().getPlugin("SyncChats")?.description?.version

            if (currentVersion != latestVersion) {
                val message = "A new version of SyncChats is available: $latestVersion!"

                if (config.getBoolean("updates.discordNotify")) {
                    notifyDiscord(message)
                }
                logToMinecraft(message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fetchLatestVersion(): String? {
        val url = URL("https://api.spigotmc.org/legacy/update.php?resource=$PLUGIN_ID")
        val connection = url.openConnection() as HttpURLConnection
        return connection.run {
            requestMethod = "GET"
            inputStream.bufferedReader().use { it.readText().trim() }
        }
    }

    private fun logToMinecraft(message: String) {
        Bukkit.getLogger().info("§6[SyncChats] §r$message")
    }

    private fun createDiscordMessage(message: String): JSONObject {
        return JSONObject().apply {
            put("username", "SyncChats")
            put("avatar_url", "https://www.spigotmc.org/data/resource_icons/123/123042.jpg?1741111607")
            put("content", message)
        }
    }

    private fun notifyDiscord(message: String) {
        val webhookUrl = ConfigManager.getConfig().getString("discord.webhook_url")
        if (webhookUrl.isNullOrEmpty()) return

        val json = createDiscordMessage(message)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder().url(webhookUrl).post(requestBody).build()

        runCatching {
            httpClient.newCall(request).execute().use {}
        }
    }
}