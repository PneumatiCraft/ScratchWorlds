package com.lithium3141.ScratchWorlds.commands;

import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.lithium3141.ScratchWorlds.SWCommand;
import com.lithium3141.ScratchWorlds.ScratchWorlds;

public class SWScheduleCommand extends SWCommand {

	public SWScheduleCommand(ScratchWorlds sw) {
		super(sw);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!this.checkArgLength(sender, args, 0)) return;
		
		boolean messaged = false;
		for(String worldName : this.plugin.scratchWorldNames) {
			World world = this.plugin.getServer().getWorld(worldName);
			if(world != null) {
				sender.sendMessage("World " + world.getName() + " is scheduled to regenerate every " + ScratchWorlds.REGEN_INTERVAL / 1000L + " seconds");
				messaged = true;
			} else {
				this.plugin.scratchWorldNames.remove(worldName);
			}
		}
		
		if(!messaged) sender.sendMessage("No worlds currently scheduled for scratch regeneration");
	}

}
