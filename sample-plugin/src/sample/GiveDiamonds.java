package sample;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GiveDiamonds implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// send command giving everybody diamonds
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "give @a diamond");
		
		// open up template diamonds.html with no 
		sender.sendMessage("file:diamonds.html");
		return true;
	}
	
}