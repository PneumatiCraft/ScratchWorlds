package com.lithium3141.ScratchWorlds.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.lithium3141.ScratchWorlds.SWCommand;
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
		if(this.plugin.getScratchWorldNames().contains(worldName)) {
			sender.sendMessage(ChatColor.RED + "World " + worldName + " is already a scratch world!");
			return;
		}
		
		World world = this.plugin.getServer().getWorld(worldName);
		this.plugin.addScratchWorld(world);
		sender.sendMessage("World " + worldName + " is now a scratch world.");
	}

}
