package com.lithium3141.ScratchWorlds.commands;

import java.util.Timer;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.lithium3141.ScratchWorlds.SWCommand;
import com.lithium3141.ScratchWorlds.SWScheduleTask;
import com.lithium3141.ScratchWorlds.ScratchWorlds;

/**
 * Command to mark a particular world as a scratch (i.e. temporary) world.
 */
public class SWMarkCommand extends SWCommand {

	public SWMarkCommand(ScratchWorlds sw) {
		super(sw);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!this.checkArgLength(sender, args, 1)) return;
		
		String worldName = args[0];
		if(!this.validateWorld(sender, worldName)) return;
		
		// Check that this world is not already marked
		if(this.plugin.scratchWorldNames.contains(worldName)) {
			sender.sendMessage(ChatColor.RED + "World " + worldName + " is already a scratch world!");
			return;
		}
		
		this.plugin.scratchWorldNames.add(worldName);
		
		Timer timer = new Timer();
		World world = this.plugin.getServer().getWorld(worldName);
		timer.schedule(new SWScheduleTask(world, sender), ScratchWorlds.REGEN_INTERVAL, ScratchWorlds.REGEN_INTERVAL);
		this.plugin.timers.put(world, timer);
		
		sender.sendMessage("World " + worldName + " is now a scratch world.");
	}

}
