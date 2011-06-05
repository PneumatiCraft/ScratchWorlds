package com.lithium3141.ScratchWorlds;

import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class SWCommands {
	private ScratchWorlds plugin;
	
	public SWCommands(ScratchWorlds sw) {
		this.plugin = sw;
	}
	
	public void list(CommandSender sender, String[] args) {
		// TODO finish implementing
		for(World world : this.plugin.getServer().getWorlds()) {
			sender.sendMessage(world.getName());
		}
	}
}
