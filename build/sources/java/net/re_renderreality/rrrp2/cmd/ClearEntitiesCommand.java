package net.re_renderreality.rrrp2.cmd;

import net.re_renderreality.rrrp2.main.Registry;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

public class ClearEntitiesCommand {
	 
	private final CommandContext args;
	private final CommandSource src;
	
	public ClearEntitiesCommand(CommandSource src, CommandContext args) { this.src = src; this.args = args; }
	
	public void run() {
		
		int count = 0;
		String entity = args.<String>getOne("Entity").get();
		
		if (entity.equalsIgnoreCase("player")) {
			src.sendMessage(Text.of("[WARNING] Do not use \"player\" as a parameter for this command. Command aborted."));
			return;
		}
		
		for (World w : Registry.getServer().getWorlds()) {
			for (Entity e : w.getEntities()) {
				if (e.getType().getName().equals(entity)) {
					e.remove();
					count += 1;
				}
			}
		}
		
		Text result = (count > 0) ? Text.of("[SUCCESS] Removed: " + count + " of entity: " + entity) : Text.of("[ERROR] Could not find entities of: " + entity);
		src.sendMessage(result);
	}
}