package com.lithium3141.ScratchWorlds.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.lithium3141.ScratchWorlds.SWCommand;
import com.lithium3141.ScratchWorlds.ScratchWorlds;

/**
 * Command to list all worlds currently existing, separating by 
 * scratch and non-scratch worlds.
 */
public class SWListCommand extends SWCommand {

	public SWListCommand(ScratchWorlds sw) {
		super(sw);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!this.checkArgLength(sender, args, 0)) return;
		
		// Figure out worlds
		List<World> scratchWorlds = new ArrayList<World>();
		List<World> otherWorlds = new ArrayList<World>();
		for(World world : this.plugin.getServer().getWorlds()) {
			if(this.plugin.getScratchWorldNames().contains(world.getName())) {
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
