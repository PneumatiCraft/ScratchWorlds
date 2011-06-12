package com.lithium3141.ScratchWorlds;

import java.io.File;

import org.bukkit.util.config.Configuration;

import com.lithium3141.ScratchWorlds.configs.*;

public abstract class SWConfiguration {
	// Actual data
	protected Configuration configuration;
	protected int version;
	
	// Config info
	public static final int LATEST_CONFIG_VERSION = 2;
	public static final String CONFIG_FILE_NAME = "ScratchWorlds.yml";
	
	// Config keys
	public static final String VERSION_KEY = "configversion";
	public static final String WORLD_LIST_KEY = "worlds";
	public static final String RESEED_KEY = "reseed";
	
	/**
	 * Create a new SWConfiguration backed by the given Configuration.
	 * 
	 * @param config The Configuration backing this SWConfiguration
	 */
	public SWConfiguration(Configuration config) {
		this.configuration = config;
		this.configuration.load();
	}
	
	/**
	 * Create a new SWConfiguration backed by the configuration stored
	 * in the given file. The file is used to create a new Configuration
	 * instance.
	 * 
	 * @param file The file with configuration backing this SWConfiguration
	 */
	public SWConfiguration(File file) {
		this(new Configuration(file));
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
		return LATEST_CONFIG_VERSION;
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
		// If no version key, default to 1 (other configs have version key)
		int detectedVersion = c.getInt(VERSION_KEY, 1);
		
		// Instantiate concrete subclass as detected
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
	 *         details and an incremented version number, if all goes well.
	 *         Returns null if an error occurs during upgrade, or if the
	 *         configuration is already the latest version
	 */
	public abstract SWConfiguration upgrade();
	
	/**
	 * Write this SWConfiguration out to file. Uses the File object stored
	 * in the backing Configuration instance.
	 * 
	 * @param plugin The ScratchWorlds plugin instance from which to pull data
	 * @return true if the write succeeded; false otherwise
	 */
	public abstract boolean write(ScratchWorlds plugin);
}
