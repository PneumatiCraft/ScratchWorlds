package com.lithium3141.ScratchWorlds.commands;

import java.io.File;
import java.io.FilenameFilter;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.ScratchWorlds.SWCommand;
import com.lithium3141.ScratchWorlds.ScratchWorlds;

/**
 * Forcibly kick all players in a scratch world; remove all chunks outside
 * the (0,0) chunk in each scratch world; and regenerate all unloaded chunks
 * in scratch worlds.
 */
public class SWRegenerateCommand extends SWCommand {

	public SWRegenerateCommand(ScratchWorlds sw) {
		super(sw);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!this.checkArgLength(sender, args, 0)) return;
		
		// Notify
		ScratchWorlds.LOG.info(ScratchWorlds.LOG_PREFIX + "Regenerating scratch worlds...");
		if(sender instanceof Player) {
			sender.sendMessage("Regenerating scratch worlds...");
		}
		
		// Remove all players from each scratch world
		for(Player player : this.plugin.getServer().getOnlinePlayers()) {
			if(this.plugin.scratchWorldNames.contains(player.getWorld().getName())) {
				ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Kicking player " + player.getName() + " from world " + player.getWorld().getName());
				player.kickPlayer("Regenerating scratch world: " + player.getWorld().getName());
			}
		}
		
		// Unload all chunks except spawn
		for(World world : this.plugin.getServer().getWorlds()) {
			if(this.plugin.scratchWorldNames.contains(world.getName())) {
				String worldName = world.getName();
				
				if(!this.unloadChunks(world, sender)) {
					sender.sendMessage(ChatColor.RED + "Failed to unload chunks for world " + worldName);
				}
				
				if(!this.recreateWorld(world, sender)) {
					sender.sendMessage(ChatColor.RED + "Failed to recreate world " + worldName);
				}
			}
		}
		
		// Notify
		ScratchWorlds.LOG.info(ScratchWorlds.LOG_PREFIX + "Done!");
		if(sender instanceof Player) {
			sender.sendMessage("Done regenerating");
		}
	}
	
	private boolean recreateWorld(World world, CommandSender sender) {
		String worldName = world.getName();
		Environment env = world.getEnvironment();
		
		for(Chunk c : world.getLoadedChunks()) {
			world.unloadChunk(c.getX(), c.getZ());
		}
		if(!this.plugin.getServer().unloadWorld(worldName, false)) {
			sender.sendMessage(ChatColor.RED + "Couldn't unload world being regenerated"); return false;
		}
		
		// Locate world folder. Assumes world exists in CB root
		File worldFolder = new File(world.getName());
		if(!worldFolder.exists() || !worldFolder.isDirectory() || !worldFolder.canRead()) {
			sender.sendMessage(ChatColor.RED + "World folder does not exist or is not readable"); return false;
		}
		
		// Delete world folder
		for(File file : worldFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				return (!arg1.equals("session.lock") && !arg1.equals("players"));
			}
			
		})) {
			if(!this.recursiveDelete(file)) {
				sender.sendMessage(ChatColor.RED + "Failed to delete world file: " + file.toString()); return false;
			}
		}
		
		// Regenerate world
		this.plugin.getServer().createWorld(worldName, env);
		
		return (this.plugin.getServer().getWorld(worldName) != null);
	}

	private boolean unloadChunks(World world, CommandSender sender) {
		for(Chunk c : world.getLoadedChunks()) {
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Unloading chunk (" + c.getX() + "," + c.getZ() + ")");
			world.unloadChunk(c.getX(), c.getZ());
			world.regenerateChunk(c.getX(), c.getZ());
		}
		return true;
	}
	
	private boolean recursiveDelete(File file) {
		if(file.isDirectory()) {
			for(File f : file.listFiles()) {
				this.recursiveDelete(f);
			}
		} else {
			file.delete();
		}
		return true;
	}
	
	/*
	private boolean alterSeed(World world, CommandSender sender) {
		if(world instanceof CraftWorld) {
			CraftWorld craftWorld = (CraftWorld)world;
			Class<?> craftWorldClass = craftWorld.getClass();
			try {
				Field worldField = craftWorldClass.getDeclaredField("world");
				worldField.setAccessible(true);
				WorldServer worldServer = (WorldServer) worldField.get(craftWorld);
				
				Field aField = worldServer.worldData.getClass().getDeclaredField("a");
				aField.setAccessible(true);
				long newSeed = (new Random()).nextLong();
				aField.setLong(worldServer.worldData, newSeed);
				ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Set seed for world " + world.getName() + " to " + newSeed);
			} catch (SecurityException e) {
				sender.sendMessage(ChatColor.YELLOW + "Security exception accessing seed field");
				return false;
			} catch (NoSuchFieldException e) {
				sender.sendMessage(ChatColor.YELLOW + "Could not find seed field; is your plugin up to date?");
				return false;
			} catch (IllegalArgumentException e) {
				sender.sendMessage(ChatColor.YELLOW + "Illegal argument finding seed field; is your plugin up to date?");
				return false;
			} catch (IllegalAccessException e) {
				sender.sendMessage(ChatColor.YELLOW + "Access exception finding seed field; is your plugin up to date?");
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.YELLOW + "Could not cast World object");
			return false;
		}
		
		return true;
	}
	*/

}
