# OZTools (shared library)
This is a set of shared classes and jar's used by different Plugins.

## Core Features
- Plugin Translation (i18n)
- Logger for console output
- Plugin-Change-Watcher for watching file changes in the Plugin folder
- Standard colors
- WebSocket Client
- SQLite DB Initializer (per Plugin)

## Merged Features
- Miwarre's rwgui Plugin (now its just a lib not a Plugin) drop in replacement

## External libs
All librarys that were added to this shared lib can be used for all plugins without shipping them included to the plugin.
- GSON
- javax.websocket-api
- tyrus-standalone-client

# Installation
Just extract the shared folder into your `plugins` folder. The jar path should look like `plugins/shared/lib/tools.jar`

```css
    ── RisingWorld
        ├── plugins
        │    ├── shared
        │    │    ├── assets...
        │    │    └── lib
        │    │         ├── gson-2.8.6.jar
        │    │         ├── HISTORY.md
        │    │         ├── javax.websocket-api-1.1.jar
        │    │         ├── README.md
        │    │         ├── tools.jar
        │    │         └── tyrus-standalone-client-1.15.jar
        :    :
```

# Feature details/examples

## Plugin translation (i18n)
This feature can be used to translate your plugin based on the current users language. Every player will see the plugin in his system language as long as the translation file exists.
### Integration:

```java
import de.omegazirkel.risingworld.tools.I18n;

public class NewPlugin extends Plugin{

    // Init
    private static I18n t = null;
    @Override
	public void onEnable() {
		t = t != null ? t : new I18n(this);
    }

    // Usage example
    @EventMethod
	public void onPlayerSpawn(PlayerSpawnEvent event) {
		if (sendPluginWelcome) {
			Player player = event.getPlayer();
			String lang = player.getSystemLanguage();
			player.sendTextMessage(t.get("MSG_PLUGIN_WELCOME", lang));
		}
	}
}
```

### Translation files:
```
_/plugins/YourPluigin/i18n/en.properties
_/plugins/YourPluigin/i18n/de.properties
_/plugins/YourPluigin/i18n/__anyotherlanguage__.properties

```

### Example File content:
```bash
MSG_PLUGIN_WELCOME=This Server uses [#F00000]NewPlugin[#FFFFFF] Plugin.\n\
Type [#997d4a]/np help[#FFFFFF] in chat for help.
```

## Logger
This feature is for logging stuff to your (server) game console, you can send messages to the logger with different levels. If your plugin has a settings file you can then change logging level for your needs. It will always prepend your plugin-log-tag in front of each message so you can see which message is sent from your plugin.

```java
public class NewPlugin extends Plugin{
    static final String pluginName = "NewPlugin"; // just for the example

    // Init
    static final de.omegazirkel.risingworld.tools.Logger log = new de.omegazirkel.risingworld.tools.Logger("[YOURTAG]");

    // Usage example
    @Override
	public void onEnable() {
		log.out(pluginName + " Plugin is enabled", 10);
	}
}
```

## Colors
This is just a singleton class that holds some color values. The idea behind this is to have a default set of colors for the same stuff in different plugins (and not each plugin having its own colors for errors, warnings, infos, etc.)

```java
import de.omegazirkel.risingworld.tools.Colors;

public class NewPlugin extends Plugin{

    // Init
    static final Colors c = Colors.getInstance();

    // Usage example
	@EventMethod
	public void onPlayerCommand(PlayerCommandEvent event) {
        Player player = event.getPlayer();
        player.sendTextMessage(c.okay + pluginName + ":> " + c.text + "Your command was successfully ignored!");
    }
}
```



## Plugin-Change-Watcher
This static helper class creates 2 new threads to watch the filesystem for changes. To use it you have to implement `FileChangeListener` int your plugin and register your plugin to start watching changes.

### Example code
```java
import de.omegazirkel.risingworld.tools.FileChangeListener;
import de.omegazirkel.risingworld.tools.PluginChangeWatcher;

public class NewPlugin extends Plugin implements FileChangeListener{

    static final de.omegazirkel.risingworld.tools.Logger log = new de.omegazirkel.risingworld.tools.Logger("[MY.Plugin]");
	static boolean flagRestart = false;

    @Override
	public void onEnable() {

        // register this plugin for watching changes
		try {
			File f = new File(getPath());
			PluginChangeWatcher.registerFileChangeListener(this, f);
		} catch (Exception ex) {
			log.out(ex.toString(), 911);
		}
    }

    @Override
	public void onFileChangeEvent(Path file) {
		if (file.toString().endsWith("jar")) {
			if (restartOnUpdate) {
				Server server = getServer();

				if (server.getPlayerCount() > 0) {
					flagRestart = true;
				} else {
					log.out("onFileCreateEvent: <" + file + "> changed, restarting now (no players online)", 100);
				}

			} else {
				log.out("onFileCreateEvent: <" + file + "> changed but restartOnUpdate is false", 0);
			}
		} else {
			log.out("onFileCreateEvent: <" + file + ">", 0);
		}
	}

	@Override
	public void onFileCreateEvent(Path file) {
		if (file.toString().endsWith("settings.properties")) {
			// this.initSettings();
		} else {
			log.out(file.toString() + " was changed", 0);
		}
	}

}

```

## SQLite helper
... description coming soon ...

## WebSocket
... description coming soon ...
