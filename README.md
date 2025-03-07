<div align="center">
  <img src="https://www.spigotmc.org/data/resource_icons/123/123042.jpg?1741111607" alt="logo" />
</div>

# 🌐 SyncChats - Minecraft & Discord Chat Synchronization

SyncChats is a Minecraft plugin that synchronizes chat between your **Minecraft server** and a **Discord channel** using a webhook or a bot.

## 📥 Installation

### **1️⃣ Download the Plugin**
1. Download the latest release from **[GitHub Releases](https://github.com/your-repo/releases)** or compile it yourself.
2. Place the `SyncChats.jar` file into your **server's `plugins/` folder**.

### **2️⃣ Configure the Plugin**
1. Start your server once to generate the `config.yml` file.
2. Edit the `config.yml` inside `plugins/SyncChats/`.

### **3️⃣ Setup Discord Bot**
1. [Create a Discord bot](https://discord.com/developers/applications).
2. Enable **"MESSAGE CONTENT INTENT"** in **Bot Settings**.
3. Copy your **Bot Token** and paste it into `config.yml`.
4. **Invite the bot** to your Discord server with this URL: `https://discord.com/oauth2/authorize?client_id=YOUR_BOT_ID&scope=bot&permissions=68608`


### **4️⃣ Setup Webhook (Optional)**
1. Go to your Discord server settings.
2. Navigate to **"Integrations" > "Webhooks"**.
3. Create a new webhook, copy its URL, and paste it into `config.yml`.

## ⚙️ Configuration (`config.yml`)

```yaml
# ============================================
# 🔧 SyncChats Configuration File
# This file controls how Minecraft chat syncs with Discord.
# Customize this file to configure how your Minecraft server communicates with Discord!
# ============================================
version: 1.0.2

updates:
  # 📥 Enable automatic update checks for the plugin.
  # When enabled, the plugin will notify you if a new version is available!
  check: true
  # 📲 If enabled, SyncChats will send a Discord notification when a new update is available.
  discordNotify: true

minecraft_events:
  # ✅ Enable or disable sending events (player joining/leaving) to Discord.
  enabled: true
  # 📝 Customize the message sent to Discord when a player joins the server.
  # The placeholder {player} will be replaced by the player's name.
  join_message: "{player} joined the server."
  # 📝 Customize the message sent to Discord when a player leaves the server.
  # The placeholder {player} will be replaced by the player's name.
  left_message: "{player} left the server."
  # Customize the name of the Webhook sender (e.g., "Server" or "Minecraft Server").
  server_name: "SyncChats"

discord:
  # 🔑 Discord Bot Token
  # This token is required for the bot to send and receive messages in Discord.
  # 🚨 Never share your bot token! Keep it secret to avoid misuse.
  token: "YOUR_DISCORD_BOT_TOKEN_HERE"

  # 🏷️ Discord Channel ID
  # This is the ID of the Discord channel where the bot will listen for messages.
  # To find this, enable Developer Mode in Discord and right-click the channel.
  channel_id: "YOUR_DISCORD_CHANNEL_ID_HERE"

  # 🌍 Webhook URL
  # If you want to send messages from Minecraft to Discord without using the bot, enter the webhook URL here.
  # If left empty, the bot itself will send messages to Discord.
  webhook_url: "YOUR_DISCORD_WEBHOOK_URL_HERE"

  # 📊 Bot Status Configuration
  # Customize the activity/status that your Discord bot shows.
  status:
    # 🎮 Type of status: WATCHING, PLAYING, or STREAMING.
    # WATCHING: Bot is watching something (e.g., number of players in Minecraft).
    # PLAYING: Bot is playing something (e.g., Minecraft).
    # LISTENING: Bot is listening something (e.g., Spotify).
    type: "WATCHING"

    # 📝 Customize the message the bot shows.
    # Use the placeholder {players} to display the number of players online in Minecraft.
    content: "{players} players in Minecraft."

chat_format:
  # 📤 Minecraft ➝ Discord Message Format
  # This controls how Minecraft messages appear in Discord.
  # Available placeholders:
  #   {player}  - The player's name
  #   {message} - The player's message
  minecraft_to_discord: "{message}"

  # 📥 Discord ➝ Minecraft Message Format
  # This controls how Discord messages appear in Minecraft.
  # Available placeholders:
  #   {user}    - The Discord user's name
  #   {message} - The Discord message
  discord_to_minecraft: "§9[Discord] §r{user}: {message}"

# ============================================
# ✅ End of Configuration
# Once you've made changes, restart or reload your server for them to take effect!
# ============================================
```
