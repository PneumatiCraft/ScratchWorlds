package com.lithium3141.ScratchWorlds;

import java.io.File;

import org.bukkit.util.config.Configuration;

import com.lithium3141.ScratchWorlds.configs.*;

public abstract class SWConfiguration {
	protected Configuration configuration;
	protected int version;
	
	public static final String VERSION_KEY = "configversion";
	
	/**
	 * Create a new SWConfiguration backed by the given Configuration.
	 * 
	 * @param config The Configuration backing this SWConfiguration
	 */
	public SWConfiguration(Configuration config) {
		this.configuration = config;
	}
	
	/**
	 * Create a new SWConfiguration backed by the configuration stored
	 * in the given file. The file is used to create a new Configuration
	 * instance.
	 * 
	 * @param file The file with configuration backing this SWConfiguration
	 */
	public SWConfiguration(File file) {
		this.configuration = new Configuration(file);
	}
	
	/**
	 * Get the Configuration backing this SWConfiguration.
	 * 
	 * @return The Configuration backing this SWConfiguration
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}
	
	/**
	 * Get the version of this SWConfiguration.
	 * 
	 * @return The version of this SWConfiguration
	 */
	public int getVersion() {
		return this.version;
	}
	
	/**
	 * Get the latest known configuration version.
	 * 
	 * @return The most up-to-date configuration version number
	 */
	public static int getLatestVersion() {
		return ScratchWorlds.LATEST_CONFIG_VERSION;
	}
	
	/**
	 * Attempt to create a SWConfiguration from the given Configuration.
	 * Serves as a factory method to detect the appropriate configuration
	 * version and instantiate the proper concrete SWConfiguration subclass.
	 * 
	 * @return A new SWConfiguration based on the given Configuration, or
	 *         null if an error occurred during creation.
	 */
	public static SWConfiguration detectConfiguration(Configuration c) {
		int detectedVersion = c.getInt(VERSION_KEY, 1);
		switch(detectedVersion) {
		case 1: return new SWConfigurationV1(c);
		case 2: return new SWConfigurationV2(c);
		default: return null;
		}
	}
	
	/**
	 * Attempt to create a SWConfiguration from the given File.
	 * 
	 * @return A new SWConfiguration based on the given File, or
	 *         null if an error occurred during creation.
	 */
	public static SWConfiguration detectConfiguration(File file) {
		return SWConfiguration.detectConfiguration(new Configuration(file));
	}
	
	/**
	 * Upgrade this SWConfiguration to the next version.
	 * 
	 * @return A new SWConfiguration instance with updated configuration
	 *         details and an incremented version number, or null if
	 *         this SWConfiguration is the latest version.
	 */
	abstract public SWConfiguration upgrade();
}
