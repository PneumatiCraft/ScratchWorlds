package com.lithium3141.ScratchWorlds.commands;

import java.util.Timer;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.lithium3141.ScratchWorlds.SWCommand;
import com.lithium3141.ScratchWorlds.ScratchWorlds;

/**
 * Command to mark a particular world as a non-scratch (i.e. permanent) world.
 */
public class SWUnmarkCommand extends SWCommand {

	public SWUnmarkCommand(ScratchWorlds sw) {
		super(sw);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!this.checkArgLength(sender, args, 1)) return;
		
		String worldName = args[0];
		if(!this.validateWorld(sender, worldName)) return;
		
		// Check that this world is currently marked
		if(!this.plugin.scratchWorldNames.contains(worldName)) {
			sender.sendMessage(ChatColor.RED + "World " + worldName + " is not currently a scratch world!");
			return;
		}
		
		this.plugin.scratchWorldNames.remove(worldName);
		
		World world = this.plugin.getServer().getWorld(worldName);
		Timer timer = this.plugin.timers.remove(world);
		if(timer != null) {
			timer.cancel();
		}
		
		sender.sendMessage("World " + worldName + " is no longer a scratch world.");
	}

}
