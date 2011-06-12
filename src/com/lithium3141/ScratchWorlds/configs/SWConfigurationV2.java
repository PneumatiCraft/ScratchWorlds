package com.lithium3141.ScratchWorlds.configs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.util.config.Configuration;

import com.lithium3141.ScratchWorlds.SWConfiguration;
import com.lithium3141.ScratchWorlds.SWWorld;
import com.lithium3141.ScratchWorlds.ScratchWorlds;

public class SWConfigurationV2 extends SWConfiguration {

	public SWConfigurationV2(Configuration config) {
		super(config);
		this.version = 2;
	}
	
	public SWConfigurationV2(File file) {
		super(file);
	}

	@Override
	public SWConfiguration upgrade() {
		// Version 2 is currently the most recent config version known
		return null;
	}

	@Override
	public boolean write(ScratchWorlds plugin) {
		Map<String, Object> worlds = new HashMap<String, Object>();
		
		for(Entry<String,SWWorld> entry : plugin.getWorlds().entrySet()) {
			Map<String, Object> world = new HashMap<String, Object>();
			world.put(RESEED_KEY, entry.getValue().shouldReseed());
			worlds.put(entry.getKey(), world);
		}
		
		this.configuration.setProperty(VERSION_KEY, new Integer(2));
		this.configuration.setProperty(WORLD_LIST_KEY, worlds);
		
		return this.configuration.save();
	}

}
