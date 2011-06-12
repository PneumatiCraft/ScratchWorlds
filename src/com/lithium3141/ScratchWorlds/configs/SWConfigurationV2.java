package com.lithium3141.ScratchWorlds.configs;

import java.io.File;

import org.bukkit.util.config.Configuration;

import com.lithium3141.ScratchWorlds.SWConfiguration;

public class SWConfigurationV2 extends SWConfiguration {

	public SWConfigurationV2(Configuration config) {
		super(config);
		this.version = 2;
	}
	
	public SWConfigurationV2(File file) {
		super(file);
		this.version = 2;
	}

	@Override
	public SWConfiguration upgrade() {
		// Version 2 is currently the most recent config version known
		return null;
	}

}
