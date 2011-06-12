package com.lithium3141.ScratchWorlds.commands;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.ScratchWorlds.SWCommand;
import com.lithium3141.ScratchWorlds.SWWorld;
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
		if(!this.checkArgLength(sender, args, 0, 1)) return;
		
		List<String> toRegenerate = new ArrayList<String>();
		if(args.length == 0) {
			toRegenerate.addAll(this.plugin.getScratchWorldNames());
		} else {
			if(this.plugin.getScratchWorldNames().contains(args[0])) {
				toRegenerate.add(args[0]);
			} else {
				sender.sendMessage(ChatColor.RED + "Argument '" + args[0] + "' is not a scratch world");
				return;
			}
		}
		
		// Notify
		ScratchWorlds.LOG.info(ScratchWorlds.LOG_PREFIX + "Regenerating scratch worlds...");
		if(sender instanceof Player) {
			sender.sendMessage("Regenerating scratch worlds...");
		}
		
		// Remove all players from each scratch world
		for(Player player : this.plugin.getServer().getOnlinePlayers()) {
			if(this.plugin.getScratchWorldNames().contains(player.getWorld().getName())) {
				ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + "Kicking player " + player.getName() + " from world " + player.getWorld().getName());
				player.kickPlayer("Regenerating scratch world: " + player.getWorld().getName());
			}
		}
		
		// Unload all chunks except spawn
		for(World world : this.plugin.getServer().getWorlds()) {
			if(this.plugin.getScratchWorldNames().contains(world.getName())) {
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
		SWWorld scratchWorld = this.plugin.getScratchWorlds().get(worldName);
		long seed = world.getSeed();
		if(scratchWorld.getShouldReseed()) {
			seed = (new Random()).nextLong();
		}
		this.plugin.getServer().createWorld(worldName, env, seed);
		
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

}
