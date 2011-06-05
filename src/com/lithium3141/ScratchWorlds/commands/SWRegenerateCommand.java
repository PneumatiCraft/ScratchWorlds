package com.lithium3141.ScratchWorlds.commands;

import java.io.File;
import java.io.FileFilter;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
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
