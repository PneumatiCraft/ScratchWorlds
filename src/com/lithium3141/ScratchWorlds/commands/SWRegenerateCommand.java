package com.lithium3141.ScratchWorlds.commands;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lithium3141.ScratchWorlds.SWCommand;
import com.lithium3141.ScratchWorlds.SWRegenerateWorker;
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
		
		// Remove all players from each scratch world
		for(Player player : this.plugin.getServer().getOnlinePlayers()) {
			if(this.plugin.scratchWorldNames.contains(player.getWorld().getName())) {
				ScratchWorlds.LOG.fine(ScratchWorlds.LOG_PREFIX + " - Kicking player " + player.getName() + " from world " + player.getWorld().getName());
				player.kickPlayer("Regenerating scratch world: " + player.getWorld().getName());
			}
		}
		
		// Dispatch regenerate command
		for(World world : this.plugin.getServer().getWorlds()) {
			if(this.plugin.scratchWorldNames.contains(world.getName())) {
				this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, new SWRegenerateWorker(world, null));
			}
		}
		
		// Notify
		ScratchWorlds.LOG.info(ScratchWorlds.LOG_PREFIX + " - Done!");
	}

}
