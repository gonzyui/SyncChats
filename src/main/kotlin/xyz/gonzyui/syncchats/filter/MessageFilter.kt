package xyz.gonzyui.syncchats.filter
import xyz.gonzyui.syncchats.config.ConfigManager
import xyz.gonzyui.syncchats.utils.Logger
object MessageFilter {
    private val bannedWords = mutableSetOf<String>()
    private val allowedUsers = mutableSetOf<String>()
    fun loadFilters() {
        val config = ConfigManager.getConfig()
        bannedWords.clear()
        config.getStringList("filter.words").forEach { word ->
            bannedWords.add(word.lowercase())
        }
        allowedUsers.clear()
        config.getStringList("filter.allowed_users").forEach { user ->
            allowedUsers.add(user.lowercase())
        }
        Logger.info("Loaded ${bannedWords.size} banned words and ${allowedUsers.size} allowed users")
    }
    fun isMessageAllowed(message: String, username: String): Boolean {
        if (!ConfigManager.getConfig().getBoolean("filter.enabled", false)) {
            Logger.debug("Message filter is disabled")
            return true
        }
        Logger.debug("Checking message from $username: $message")
        if (allowedUsers.contains(username.lowercase())) {
            Logger.debug("User $username is in allowed list, bypassing filter")
            return true
        }
        val lowerMessage = message.lowercase()
        val containsBannedWord = bannedWords.any { bannedWord ->
            val contains = lowerMessage.contains(bannedWord)
            if (contains) {
                Logger.debug("Message contains banned word: $bannedWord")
            }
            contains
        }
        val action = ConfigManager.getConfig().getString("filter.action", "filter")
        if (containsBannedWord && action == "block") {
            Logger.debug("Message blocked due to banned word, action: $action")
            return false
        }
        Logger.debug("Message allowed, no banned words found")
        return true
    }
    fun filterMessage(message: String, username: String): String {
        if (!ConfigManager.getConfig().getBoolean("filter.enabled", false)) {
            return message
        }
        if (allowedUsers.contains(username.lowercase())) {
            return message
        }
        var filteredMessage = message
        bannedWords.forEach { bannedWord ->
            val replacement = "*".repeat(bannedWord.length)
            filteredMessage = filteredMessage.replace(bannedWord, replacement, ignoreCase = true)
        }
        return filteredMessage
    }
}
