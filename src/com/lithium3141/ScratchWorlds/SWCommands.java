package com.lithium3141.ScratchWorlds;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SWCommands {
	private ScratchWorlds plugin;
	
	public SWCommands(ScratchWorlds sw) {
		this.plugin = sw;
	}
	
	private boolean checkArgLength(CommandSender sender, String[] args, int expected) {
		if(args.length != expected) {
			sender.sendMessage("Wrong number of arguments; expected " + expected + ", got " + args.length);
			return false;
		}
		return true;
	}
	
	/**
	 * List all worlds currently existing, separating by scratch and
	 * non-scratch worlds.
	 * 
	 * @param sender The sender of the list command
	 * @param args Any arguments to the list command. Expected to be empty
	 */
	public void list(CommandSender sender, String[] args) {
		if(!this.checkArgLength(sender, args, 0)) return;
		
		// Figure out worlds
		List<World> scratchWorlds = new ArrayList<World>();
		List<World> otherWorlds = new ArrayList<World>();
		for(World world : this.plugin.getServer().getWorlds()) {
			if(this.plugin.scratchWorldNames.contains(world.getName())) {
				scratchWorlds.add(world);
			} else {
				otherWorlds.add(world);
			}
		}
		
		// Print worlds to command sender
		sender.sendMessage("Scratch worlds:");
		for(World w : scratchWorlds) {
			sender.sendMessage("    " + w.getName());
		}
		sender.sendMessage("Other worlds:");
		for(World w : otherWorlds) {
			sender.sendMessage("    " + w.getName());
		}
	}
	
	private boolean validateWorld(CommandSender sender, String worldName) {
		// Check that the world name is an actual world
		if(this.plugin.getServer().getWorld(worldName) == null) {
			sender.sendMessage(ChatColor.RED + "World " + worldName + " is not a loaded world");
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param sender The sender of the mark command
	 * @param args Any arguments to the mark command. Expected to contain
	 *             only the name of the world to mark
	 */
	public void mark(CommandSender sender, String[] args) {
		if(!this.checkArgLength(sender, args, 1)) return;
		
		String worldName = args[0];
		if(!this.validateWorld(sender, worldName)) return;
		
		// Check that this world is not already marked
		if(this.plugin.scratchWorldNames.contains(worldName)) {
			sender.sendMessage(ChatColor.RED + "World " + worldName + " is already a scratch world!");
			return;
		}
		
		this.plugin.scratchWorldNames.add(worldName);
		sender.sendMessage("World " + worldName + " is now a scratch world.");
	}
	
	/**
	 * Mark a particular world as a non-scratch (i.e. permanent) world.
	 * 
	 * @param sender The sender of the unmark command
	 * @param args Any arguments to the unmark command. Expected to contain
	 *             only the name of the world to unmark
	 */
	public void unmark(CommandSender sender, String[] args) {
		if(!this.checkArgLength(sender, args, 1)) return;
		
		String worldName = args[0];
		if(!this.validateWorld(sender, worldName)) return;
		
		// Check that this world is currently marked
		if(!this.plugin.scratchWorldNames.contains(worldName)) {
			sender.sendMessage(ChatColor.RED + "World " + worldName + " is not currently a scratch world!");
			return;
		}
		
		this.plugin.scratchWorldNames.remove(worldName);
		sender.sendMessage("World " + worldName + " is no longer a scratch world.");
	}
	
	/**
	 * Forcibly kick all players in a scratch world; remove all chunks outside
	 * the (0,0) chunk in each scratch world; and regenerate all unloaded chunks
	 * in scratch worlds.
	 * 
	 * @param sender The sender of the regenerate command
	 * @param args Any arguments to the regenerate command. Expected to be emtpy
	 */
	public void regenerate(CommandSender sender, String[] args) {
		if(!this.checkArgLength(sender, args, 0)) return;
		
		// Notify
		ScratchWorlds.LOG.info(ScratchWorlds.LOG_PREFIX + " - Regenerating scratch worlds...");
		
		// Remove all players from each scratch world
		for(Player player : this.plugin.getServer().getOnlinePlayers()) {
			if(this.plugin.scratchWorldNames.contains(player.getWorld().getName())) {
				ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + " - Kicking player " + player.getName() + " from world " + player.getWorld().getName());
				player.kickPlayer("Regenerating scratch world: " + player.getWorld().getName());
			}
		}
		
		// Unload all chunks except spawn
		for(World world : this.plugin.getServer().getWorlds()) {
			if(this.plugin.scratchWorldNames.contains(world.getName())) {
				for(Chunk c : world.getLoadedChunks()) {
					ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + " - Unloading chunk (" + c.getX() + "," + c.getZ() + ")");
					world.unloadChunk(c.getX(), c.getZ());
					world.regenerateChunk(c.getX(), c.getZ());
				}
				
				// Locate world folder. Assumes world exists in CB root
				File worldFolder = new File(world.getName());
				if(!worldFolder.exists() || !worldFolder.isDirectory() || !worldFolder.canRead()) {
					sender.sendMessage(ChatColor.RED + "World folder does not exist or is not readable");
				}
				
				// Find subdirs - should be {data, players, region}
				File[] subdirectories = worldFolder.listFiles(new FileFilter() {
					@Override
					public boolean accept(File arg0) {
						return arg0.isDirectory();
					}
				});
				
				// Empty each subdir
				for(File subdirectory : subdirectories) {
					ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + " - Emptying folder " + subdirectory.getName() + " for world " + world.getName());
					File[] subdirFiles = subdirectory.listFiles();
					for(File subdirFile : subdirFiles) {
						subdirFile.delete();
					}
				}
			}
		}
		
		// Notify
		ScratchWorlds.LOG.info(ScratchWorlds.LOG_PREFIX + " - Done!");
	}
}
