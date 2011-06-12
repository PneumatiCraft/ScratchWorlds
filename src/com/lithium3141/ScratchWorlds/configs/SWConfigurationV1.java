package com.lithium3141.ScratchWorlds.configs;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		// Read worlds from v1 config
		List<String> worlds = this.configuration.getStringList(WORLD_LIST_KEY, new ArrayList<String>());
		if(worlds == null) {
			return null;
		}
		
		// Grab the current configuration file for the new configuration
		File configFile;
		try {
			Field fileField = Configuration.class.getField("file");
			configFile = (File) fileField.get(this.configuration);
		} catch (SecurityException e) {
			return null;
		} catch (NoSuchFieldException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
		
		if(configFile == null) {
			return null;
		}
		Configuration newBackingConfig = new Configuration(configFile);
		
		// Map each world name to a new node for v2 config
		Map<String, Object> worldsMap = new HashMap<String, Object>();
		for(String world : worlds) {
			Map<String, Object> worldMap = new HashMap<String, Object>();
			worldMap.put(RESEED_KEY, new Boolean(true));
			
			// Save this world to overall map
			worldsMap.put(world, worldMap);
		}
		
		// Set top-level properties on new config
		newBackingConfig.setProperty(VERSION_KEY, new Integer(2));
		newBackingConfig.setProperty(WORLD_LIST_KEY, worldsMap);
		if(!newBackingConfig.save()) {
			return null;
		}
		
		// Log and return
		ScratchWorlds.LOG.info(ScratchWorlds.LOG_PREFIX + "Successfully upgraded configuration file to version 2");
		return new SWConfigurationV2(newBackingConfig);
	}

	@Override
	public boolean write(ScratchWorlds plugin) {
		List<String> worldNames = new ArrayList<String>();
		worldNames.addAll(plugin.getWorlds().keySet());
		
		this.configuration.setProperty(VERSION_KEY, new Integer(1));
		this.configuration.setProperty(WORLD_LIST_KEY, worldNames);
		
		return this.configuration.save();
	}

}
