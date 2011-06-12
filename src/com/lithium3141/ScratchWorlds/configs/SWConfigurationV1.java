package com.lithium3141.ScratchWorlds.configs;

import java.io.File;

import org.bukkit.util.config.Configuration;

import com.lithium3141.ScratchWorlds.SWConfiguration;

public class SWConfigurationV1 extends SWConfiguration {

	public SWConfigurationV1(Configuration config) {
		super(config);
		this.version = 1;
	}
	
	public SWConfigurationV1(File file) {
		super(file);
		this.version = 1;
	}

	@Override
	public SWConfiguration upgrade() {
		// TODO Auto-generated method stub
		return null;
	}

}
