package com.lithium3141.ScratchWorlds.commands;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.ScratchWorlds.SWCommand;
import com.lithium3141.ScratchWorlds.ScratchWorlds;

public class SWSeedCommand extends SWCommand {

	public SWSeedCommand(ScratchWorlds sw) {
		super(sw);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!this.checkArgLength(sender, args, 0)) return;
		
		if(sender instanceof Player) {
			World world = ((Player)sender).getWorld();
			sender.sendMessage("Seed for world " + world.getName() + ": " + world.getSeed());
		} else {
			// Console - list all seeds
			ScratchWorlds.LOG.info(ScratchWorlds.LOG_PREFIX + "World seeds:");
			for(World world : this.plugin.getServer().getWorlds()) {
				ScratchWorlds.LOG.info(ScratchWorlds.LOG_PREFIX + "- " + world.getName() + ": " + world.getSeed());
			}
		}
	}

}
