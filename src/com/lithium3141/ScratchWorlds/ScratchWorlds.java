package com.lithium3141.ScratchWorlds;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class ScratchWorlds extends JavaPlugin {
	
	public static final Logger LOG = Logger.getLogger("Minecraft");
	public static final String LOG_PREFIX = "[ScratchWorlds]";
	
	public static final String COMMAND_NAME = "scratch";
	
	public static final String CONFIG_FILE_NAME = "ScratchWorlds.yml";
	public static final String CONFIG_WORLD_LIST_KEY = "worlds";
	
	public Configuration swConfig;
	public List<String> scratchWorldNames = new ArrayList<String>();

	@Override
	public void onDisable() {
		// Save config
		this.swConfig.setProperty(CONFIG_WORLD_LIST_KEY, this.scratchWorldNames);
		if(!this.swConfig.save()) {
			LOG.warning(LOG_PREFIX + " - Couldn't save configuration file! Continuing anyway...");
		}
		
		LOG.info(LOG_PREFIX + " - Version " + this.getDescription().getVersion() + " disabled");
	}

	@Override
	public void onEnable() {
		// Set up configuration file
		this.getDataFolder().mkdirs();
		
		// Read configuration file
		this.swConfig = new Configuration(new File(this.getDataFolder(), CONFIG_FILE_NAME));
		this.swConfig.load();
		this.scratchWorldNames = swConfig.getStringList("worlds", new ArrayList<String>());
		
		LOG.info(LOG_PREFIX + " - Version " + this.getDescription().getVersion() + " enabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase(COMMAND_NAME)) {
			if(args.length == 0) {
				// TODO be more verbose about this error
				sender.sendMessage(ChatColor.RED + "No subcommand given");
				return true;
			}
			
			String subcommand = args[0];
			String[] subargs = new String[args.length - 1];
			for(int i = 0; i < subargs.length; i++) {
				subargs[i] = args[i + 1];
			}
			
			try {
				Class<?> klass = Class.forName("com.lithium3141.ScratchWorlds.SWCommands");
				for(Method m : klass.getMethods()) {
					if(m.getName().equalsIgnoreCase(subcommand)) {
						m.invoke(new SWCommands(this), sender, subargs);
						return true;
					}
				}
				LOG.warning("Unknown /" + COMMAND_NAME + " subcommand: " + subcommand);
			} catch (ClassNotFoundException e) {
				LOG.severe(LOG_PREFIX + " - Could not find command implementations! Is your plugin JAR corrupted?");
			} catch (IllegalArgumentException e) {
				LOG.severe(LOG_PREFIX + " - Error passing arguments to command invocation! Is your plugin JAR corrupted?");
			} catch (IllegalAccessException e) {
				LOG.severe(LOG_PREFIX + " - Access error invoking command! Is your plugin JAR corrupted?");
			} catch (InvocationTargetException e) {
				LOG.severe(LOG_PREFIX + " - Target error invoking command! Is your plugin JAR corrupted?");
			}
			return true;
		}
		
		return false;
	}

}
