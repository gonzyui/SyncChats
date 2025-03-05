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
discord:
token: "YOUR_DISCORD_BOT_TOKEN"  # Required for bot-based chat sync
channel_id: "DISCORD_CHANNEL_ID"  # ID of the channel where messages will be sent/received
webhook_url: "DISCORD_WEBHOOK_URL"  # Optional - Use webhook instead of bot

chat_format:
minecraft_to_discord: "**{player}**: {message}"  # Format for Minecraft → Discord
discord_to_minecraft: "[Discord] {user}: {message}"  # Format for Discord → Minecraft
```
