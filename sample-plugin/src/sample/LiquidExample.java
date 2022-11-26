package sample;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LiquidExample implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		/*
		 *  here, we are redirecting to the liquid_example template.
		 *  Since args[1] stores the post parameters in a JSON format, we will
		 *  forward those for use in the liquid template. (args[0] stores the
		 *  get parameters).
		 */
		sender.sendMessage("template:liquid_example.html," + args[1]);
		
		return true;
	}
	
}