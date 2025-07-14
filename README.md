# SyncChats

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.10-purple.svg)](https://kotlinlang.org/)
[![Spigot](https://img.shields.io/badge/Spigot-1.20.1-orange.svg)](https://www.spigotmc.org/)

**SyncChats** is a professional Minecraft plugin that enables bidirectional synchronization between Minecraft chat and Discord. Designed for production servers, it offers advanced filtering, rate limiting, and statistics tracking features.

## ✨ Features

### 🔄 Chat Synchronization
- **Bidirectional**: Minecraft ↔ Discord messages
- **Flexible modes**: Webhook and Discord Bot
- **Rich embeds**: Formatted messages with avatars
- **Mentions**: Discord @mentions support

### 🛡️ Security & Performance
- **Advanced filtering**: Blocked words, custom regex
- **Rate limiting**: Anti-spam protection
- **Smart caching**: Performance optimization
- **Console logging**: Full server console capture to Discord

### 🎮 Discord Integration
- **Slash commands**: Native Discord commands
- **Rich embeds**: Beautiful formatted messages
- **Event logging**: Player join/leave, deaths, achievements
- **Bot presence**: Dynamic status updates

### 📊 Statistics & Monitoring
- **Real-time stats**: Message counts, active users
- **Performance metrics**: Response times, cache hits
- **Event tracking**: Server events and player actions
- **Discord logs**: Complete server monitoring

## 📦 Installation

1. Download the latest release
2. Place the `.jar` file in your server's `plugins` folder
3. Configure the plugin in `config.yml`
4. Set up your Discord bot and obtain a token
5. Configure Discord channel IDs and permissions
6. Restart your server

## ⚙️ Configuration

```yaml
discord:
  token: ""
  channel_id: ""
  webhook_url: ""
  
  # Communication mode: "bot", "webhook", or "both"
  mode: "both"
  
  # Slash Commands
  commands:
    enabled: true
    admin_users:
      - ""
  
  # Console Logging
  logs:
    enabled: false
    channel_id: ""
    console:
      enabled: false
      min_level: "INFO"

# Message filtering system
filter:
  enabled: true
  words: []
  allowed_users: []
  action: "block"

# Rate limiting
rate_limiting:
  enabled: true
  max_messages_per_minute: 10
  cooldown_seconds: 60
```
and more..

## 🔧 Commands

### Discord Slash Commands
- `/reload` - Reload plugin configuration
- `/status` - Show plugin status
- `/stats` - Display plugin statistics
- `/serverstatus` - Show server status and player count

### Minecraft Commands
- `/syncchatsreload` - Reload plugin configuration
- `/syncchatsstatus` - Show plugin status
- `/syncchatsstats` - Display statistics
- `/syncchatsserverstatus` - Show server status

## 🛡️ Permissions

Configure permissions in the `config.yml` file using Discord user IDs and role IDs.

## 📋 Requirements

- Minecraft Server (Spigot/Paper 1.13+)
- Java 17 or higher
- Discord Bot Token
- Discord Server with appropriate permissions

## 🚀 Build Information

- **Build System**: Gradle 8.10
- **Language**: Kotlin 2.1.10
- **Target**: Java 17
- **Framework**: Spigot API 1.20.1
- **Discord Library**: JDA 5.0.0-beta.12
- **Output**: Fat JAR (6.4MB) with all dependencies included

## 🤝 Support

For support, please open an issue on the GitHub repository.

## 📄 License

This project is licensed under the MIT License.
