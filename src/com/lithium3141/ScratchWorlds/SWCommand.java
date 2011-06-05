package com.lithium3141.ScratchWorlds;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class SWCommand {
	protected ScratchWorlds plugin;
	
	public SWCommand(ScratchWorlds sw) {
		this.plugin = sw;
	}
	
	/**
	 * Check the length of the arguments array passed to an individual command.
	 * 
	 * @param sender The sender of the command
	 * @param args The list of arguments sent
	 * @param expected The number of arguments expected
	 * @return true if the argument list length matches the expected argument; false otherwise
	 */
	protected boolean checkArgLength(CommandSender sender, String[] args, int expected) {
		if(args.length != expected) {
			sender.sendMessage("Wrong number of arguments; expected " + expected + ", got " + args.length);
			return false;
		}
		return true;
	}
	
	/**
	 * Validate that a given world name exists on-disk
	 * 
	 * @param sender The sender of the command
	 * @param worldName The name to validate
	 * @return true if the world exists as a folder on disk; false otherwise
	 */
	protected boolean validateWorld(CommandSender sender, String worldName) {
		// Check that the world name is an actual world
		if(this.plugin.getServer().getWorld(worldName) == null) {
			sender.sendMessage(ChatColor.RED + "World " + worldName + " is not a loaded world");
			return false;
		}
		
		return true;
	}
	
	public abstract void execute(CommandSender sender, String[] args);
}
