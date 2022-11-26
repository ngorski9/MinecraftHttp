package sample;

import org.bukkit.plugin.java.JavaPlugin;

public class SamplePlugin extends JavaPlugin{
	
    @Override
    public void onEnable() {
    	this.getCommand("diamonds").setExecutor(new GiveDiamonds());
    	this.getCommand("liquidexample").setExecutor(new LiquidExample());
    }
    
    @Override
    public void onDisable() {
    	
    }
	
}
