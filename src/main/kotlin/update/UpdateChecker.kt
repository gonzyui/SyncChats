package xyz.gonzyui.syncchats.update

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import xyz.gonzyui.syncchats.config.ConfigManager
import java.net.HttpURLConnection
import org.json.simple.JSONObject
import okhttp3.OkHttpClient
import org.bukkit.Bukkit
import okhttp3.Request
import java.net.URL

class UpdateChecker {

    fun checkForUpdates() {
        val checkUpdates = ConfigManager.getConfig().getBoolean("updates.check")
        val discordNotify = ConfigManager.getConfig().getBoolean("updates.discordNotify")

        if (checkUpdates) {
            val pluginId = "123042"

            try {
                val url = URL("https://api.spigotmc.org/legacy/update.php?resource=$pluginId")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val latestVersion = connection.inputStream.bufferedReader().readText()

                val currentVersion = Bukkit.getPluginManager().getPlugin("SyncChats")?.description?.version

                if (currentVersion != latestVersion) {
                    val message = "A new version of SyncChats is available: $latestVersion!"

                    if (discordNotify) {
                        notifyDiscord(message)
                    }

                    logToMinecraft(message)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun logToMinecraft(message: String) {
        Bukkit.getLogger().info("§6[SyncChats] §r$message")
    }

    private fun createDiscordMessage(message: String): JSONObject {
        val webhookUsername = "SyncChats"
        val avatarUrl = "https://www.spigotmc.org/data/resource_icons/123/123042.jpg?1741111607"

        return JSONObject().apply {
            put("username", webhookUsername)
            put("avatar_url", avatarUrl)
            put("content", message)
        }
    }

    private fun notifyDiscord(message: String) {
        val webhookUrl = ConfigManager.getConfig().getString("discord.webhook_url")

        if (!webhookUrl.isNullOrEmpty()) {
            val json = createDiscordMessage(message)
            val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder().url(webhookUrl).post(requestBody).build()

            OkHttpClient().newCall(request).execute()
        }
    }
}
