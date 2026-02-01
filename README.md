# FoodSizePlugin

**A Minecraft Paper plugin that changes player size based on eaten food!**

## Functionality

The plugin tracks what food players eat and automatically changes their size. All edible items in the game affect player size - some increase it, some decrease it.

Key features:
- **Automatic size change**: Every time a player eats food, their size changes
- **All food works**: Both increasing and decreasing food items are included
- **Cumulative effect**: Multiple food items stack (can become 30%-500% of normal size)
- **Configurable**: Food effects can be customized in config.yml
- **Custom messages**: Text messages can be changed in messages.yml

## Commands
/size - Show current size

/size [percent] - Set size in percent (30-500)

/resetsize - Reset size to 100%

/sizereload - Reload configuration

## Configuration

**config.yml**
Contains food effects. Each item has a value:
- Positive value = increases size
- Negative value = decreases size
- Zero = no change

Example:
```yaml
GOLDEN_APPLE: 0.20     # +20% size
APPLE: -0.10           # -10% size
settings:
  min-scale: 0.3       # Minimum 30%
  max-scale: 5.0       # Maximum 500%
```

**messages.yml**
Contains customizable text messages that players see when size changes.

## How it works

When enabled: Loads configuration files and registers events
Player tracking: When a player eats food, the plugin checks the food type and calculates the new size
Size application: Uses vanilla `/attribute` command to change player size
Data storage: Current size is stored in memory and persists while player is online
