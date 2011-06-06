package com.lithium3141.ScratchWorlds.commands;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.server.WorldData;
import net.minecraft.server.WorldServer;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.jnbt.CompoundTag;
import org.jnbt.LongTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

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
		if(sender instanceof Player) {
			sender.sendMessage("Regenerating scratch worlds...");
		}
		
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
				if(!this.redoChunks(world, sender)) {
					sender.sendMessage(ChatColor.RED + "Failed to redo chunks for world " + world.getName());
				}
				
				if(!this.emptyWorldFolder(world, sender)) {
					sender.sendMessage(ChatColor.RED + "Failed to empty folder for world " + world.getName());
				}
				
				if(!this.alterSeed(world, sender)) {
					sender.sendMessage(ChatColor.YELLOW + "Failed to modify level data file; " + world.getName() + " seed unchanged");
				}
			}
		}
		
		// Notify
		ScratchWorlds.LOG.info(ScratchWorlds.LOG_PREFIX + " - Done!");
		if(sender instanceof Player) {
			sender.sendMessage("Done regenerating");
		}
	}
	
	private boolean redoChunks(World world, CommandSender sender) {
		for(Chunk c : world.getLoadedChunks()) {
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + " - Unloading chunk (" + c.getX() + "," + c.getZ() + ")");
			world.unloadChunk(c.getX(), c.getZ());
			world.regenerateChunk(c.getX(), c.getZ());
		}
		return true;
	}
	
	private boolean emptyWorldFolder(World world, CommandSender sender) {
		// Locate world folder. Assumes world exists in CB root
		File worldFolder = new File(world.getName());
		if(!worldFolder.exists() || !worldFolder.isDirectory() || !worldFolder.canRead()) {
			sender.sendMessage(ChatColor.RED + "World folder does not exist or is not readable"); return false;
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
		
		return true;
	}
	
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
				e.printStackTrace();
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
		
		/*
		NBTInputStream nbtin = null;
		try {
			// Get file for reading
			File levelDatFile = new File(world.getName(), "level.dat");
			if(!levelDatFile.exists() || !levelDatFile.canRead()) {
				sender.sendMessage(ChatColor.RED + "Cannot locate level.dat file for reading; aborting..."); return false;
			}
			nbtin = new NBTInputStream(new GZIPInputStream(new FileInputStream(levelDatFile)));
			
			// Parse tags
			Tag rootTag = nbtin.readTag();
			if(!rootTag.getName().equals("Data")) {
				sender.sendMessage(ChatColor.RED + "Root NBT tag has unexpected name; is your plugin up to date?"); return false;
			}
			CompoundTag dataTag = (CompoundTag)rootTag;
			Map<String,Tag> childTags = dataTag.getValue();
			LongTag seedTag = (LongTag)childTags.get("RandomSeed");
			
			// Verify seed and generate new
			if(seedTag == null || seedTag.getValue() != world.getSeed()) {
				sender.sendMessage(ChatColor.YELLOW + "Seed from NBT (" + seedTag.getValue() + ") differs from CB seed (" + world.getSeed() + "); ignoring...");
			}
			long seed = (new Random()).nextLong();
			ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + " - Modifying world seed; new seed is " + seed);
			
			// Update seed in tag tree
			childTags.put("RandomSeed", new LongTag("RandomSeed", seed));
			dataTag = new CompoundTag("Data", childTags);
			
			// Write tag tree back to level.dat
			NBTOutputStream nbtout = new NBTOutputStream(new GZIPOutputStream(new FileOutputStream(levelDatFile)));
			nbtout.writeTag(dataTag);
			nbtout.close();
			nbtin.close();
		} catch (FileNotFoundException e) {
			sender.sendMessage(ChatColor.RED + "level.dat file does not exist or is not readable");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			sender.sendMessage(ChatColor.RED + "I/O error altering level.dat");
			return false;
		}
		*/
		
		return true;
	}

}
