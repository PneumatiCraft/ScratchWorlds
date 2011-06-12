package com.lithium3141.ScratchWorlds.configs;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.util.config.Configuration;

import com.lithium3141.ScratchWorlds.SWConfiguration;
import com.lithium3141.ScratchWorlds.ScratchWorlds;

public class SWConfigurationV1 extends SWConfiguration {

	public SWConfigurationV1(Configuration config) {
		super(config);
		this.version = 1;
	}
	
	public SWConfigurationV1(File file) {
		super(file);
	}

	@Override
	public SWConfiguration upgrade() {
		Level oldLevel = ScratchWorlds.LOG.getLevel();
		ScratchWorlds.LOG.setLevel(Level.FINE);
		
		// Read worlds from v1 config
		List<String> worlds = this.configuration.getStringList(WORLD_LIST_KEY, new ArrayList<String>());
		if(worlds == null) {
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Config 1=>2 upgrade: Reading existing worlds returned null");
			return null;
		} else {
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Config 1=>2 upgrade: located " + worlds.size() + " worlds to migrate");
		}
		
		// Grab the current configuration file for the new configuration
		File configFile = null;
		try {
			Field fileField = Class.forName("org.bukkit.util.config.Configuration").getDeclaredField("file");
			fileField.setAccessible(true);
			configFile = (File) fileField.get(this.configuration);
		} catch (SecurityException e) {
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Config 1=>2 upgrade: Security exception");
			return null;
		} catch (NoSuchFieldException e) {
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Config 1=>2 upgrade: No such field exception");
			return null;
		} catch (IllegalArgumentException e) {
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Config 1=>2 upgrade: Illegal argument exception");
			return null;
		} catch (IllegalAccessException e) {
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Config 1=>2 upgrade: Illegal access exception");
			return null;
		} catch (ClassNotFoundException e) {
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Config 1=>2 upgrade: Class not found exception");
			return null;
		}
		
		if(configFile == null) {
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Config 1=>2 upgrade: Reflecting existing config file returned null");
			return null;
		}
		Configuration newBackingConfig = new Configuration(configFile);
		
		// Map each world name to a new node for v2 config
		for(String worldName : worlds) {
			newBackingConfig.setProperty(SWConfiguration.createPath(WORLD_LIST_KEY, worldName, RESEED_KEY), new Boolean(true));
		}
		
		// Set top-level properties on new config
		newBackingConfig.setProperty(VERSION_KEY, new Integer(2));
		
		// Write config
		if(!newBackingConfig.save()) {
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Config 1=>2 upgrade: Failed to save new backing configuration");
			return null;
		}
		
		// Log and return
		ScratchWorlds.LOG.info(ScratchWorlds.LOG_PREFIX + "Successfully upgraded configuration file to version 2");
		ScratchWorlds.LOG.setLevel(oldLevel);
		return new SWConfigurationV2(newBackingConfig);
	}

	@Override
	public boolean write(ScratchWorlds plugin) {
		List<String> worldNames = new ArrayList<String>();
		worldNames.addAll(plugin.getScratchWorldNames());
		
		this.configuration.setProperty(VERSION_KEY, new Integer(1));
		this.configuration.setProperty(WORLD_LIST_KEY, worldNames);
		
		return this.configuration.save();
	}
	
	@Override
	public boolean readShouldReseed(String worldName) {
		// All worlds under v1 config reseed on regenerate
		return true;
	}

	@Override
	public List<String> readScratchWorldNames() {
		return this.configuration.getStringList(WORLD_LIST_KEY, new ArrayList<String>());
	}

}
