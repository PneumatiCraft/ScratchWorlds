package com.lithium3141.ScratchWorlds;

import java.util.TimerTask;

import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class SWScheduleTask extends TimerTask {
	
	private World world;
	private CommandSender sender;
	
	public SWScheduleTask(World world, CommandSender sender) {
		super();

		this.world = world;
		this.sender = sender;
	}

	@Override
	public void run() {
		(new SWRegenerateWorker(this.world, this.sender)).execute();
	}

}
