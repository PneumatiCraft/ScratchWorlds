package com.lithium3141.ScratchWorlds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class SWCommands {
	private ScratchWorlds plugin;
	
	public SWCommands(ScratchWorlds sw) {
		this.plugin = sw;
	}
	
	public void list(CommandSender sender, String[] args) {
		// TODO finish implementing
		
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
}
