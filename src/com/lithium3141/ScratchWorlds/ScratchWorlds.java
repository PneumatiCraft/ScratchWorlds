package com.lithium3141.ScratchWorlds;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class ScratchWorlds extends JavaPlugin {
	
	public static final Logger LOG = Logger.getLogger("Minecraft");
	public static final String LOG_PREFIX = "[ScratchWorlds]";

	@Override
	public void onDisable() {
		LOG.info(LOG_PREFIX + " - Version " + this.getDescription().getVersion() + " disabled");
	}

	@Override
	public void onEnable() {
		LOG.info(LOG_PREFIX + " - Version " + this.getDescription().getVersion() + " enabled");
	}

}
