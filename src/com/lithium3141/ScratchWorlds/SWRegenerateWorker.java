package com.lithium3141.ScratchWorlds;

import java.io.File;
import java.io.FileFilter;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class SWRegenerateWorker implements Runnable {
	private World world;
	private CommandSender sender;
	
	public SWRegenerateWorker(World world, CommandSender sender) {
		this.world = world;
		this.sender = sender;
	}
	
	public void run() {
		ScratchWorlds.LOG.info(ScratchWorlds.LOG_PREFIX + " - Regenerating world " + this.world.getName());
		
		for(Chunk c : this.world.getLoadedChunks()) {
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + " - Unloading chunk (" + c.getX() + "," + c.getZ() + ")");
			this.world.unloadChunk(c.getX(), c.getZ());
			this.world.regenerateChunk(c.getX(), c.getZ());
		}

		// Locate world folder. Assumes world exists in CB root
		File worldFolder = new File(this.world.getName());
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
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + " - Emptying folder " + subdirectory.getName() + " for world " + this.world.getName());
			File[] subdirFiles = subdirectory.listFiles();
			for(File subdirFile : subdirFiles) {
				subdirFile.delete();
			}
		}
	}
}
