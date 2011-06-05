package com.lithium3141.ScratchWorlds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
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
	
	public void regenerate(CommandSender sender, String[] args) {
		if(!this.checkArgLength(sender, args, 0)) return;
		
		// Notify
		ScratchWorlds.LOG.info("Regenerating scratch worlds...");
		
		// Remove all players from each scratch world
		for(Player player : this.plugin.getServer().getOnlinePlayers()) {
			if(this.plugin.scratchWorldNames.contains(player.getWorld().getName())) {
				player.kickPlayer("Regenerating scratch world: " + player.getWorld().getName());
			}
		}
		
		// TODO continue
	}
}
