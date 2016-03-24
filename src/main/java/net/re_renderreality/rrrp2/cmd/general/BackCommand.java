package net.re_renderreality.rrrp2.cmd.general;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.re_renderreality.rrrp2.PluginInfo;
import net.re_renderreality.rrrp2.RRRP2;
import net.re_renderreality.rrrp2.api.util.config.readers.ReadConfigTeleport;
import net.re_renderreality.rrrp2.backend.CommandExecutorBase;
import net.re_renderreality.rrrp2.database.Database;
import net.re_renderreality.rrrp2.database.core.PlayerCore;
import net.re_renderreality.rrrp2.utils.Utilities;

public class BackCommand extends CommandExecutorBase
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			int id = Database.getID(player.getUniqueId().toString());
			PlayerCore playerz = RRRP2.getRRRP2().getOnlinePlayer().getPlayer(id);
			String lastLocation = Utilities.convertLocation(player);

			if (!(playerz.getLastlocation() == null))
			{
				Location<World> location = Utilities.convertLocation(playerz.getLastlocation());

				if (ReadConfigTeleport.isTeleportCooldownEnabled() && !player.hasPermission("rrp2.teleport.cooldown.override"))
				{
					RRRP2.teleportingPlayers.add(playerz.getID());
					src.sendMessage(Text.of(TextColors.GREEN, "Success! ", TextColors.YELLOW, "Teleporting to Last Location. Please wait " + ReadConfigTeleport.getTeleportCooldown() + " seconds."));
					
					Sponge.getScheduler().createTaskBuilder().execute(() -> {
						if(RRRP2.teleportingPlayers.contains(playerz.getID()))
						{
							if (player.getLocation().getExtent().getUniqueId().equals(location.getExtent().getUniqueId()))
								player.setLocation(location);
							else
								player.transferToWorld(location.getExtent().getUniqueId(), location.getPosition());
							RRRP2.teleportingPlayers.remove(playerz.getID());
						}
					}).delay(ReadConfigTeleport.getTeleportCooldown(), TimeUnit.SECONDS).name("RRRP2 - Back Timer").submit(Sponge.getGame().getPluginManager().getPlugin(PluginInfo.ID).get().getInstance().get());
				}
				else
				{
					if (player.getLocation().getExtent().getUniqueId().equals(location.getExtent().getUniqueId()))
						player.setLocation(location);
					else
						player.transferToWorld(location.getExtent().getUniqueId(), location.getPosition());
					src.sendMessage(Text.of(TextColors.GREEN, "Success! ", TextColors.YELLOW, "Teleported to Last Location."));
				}
			}
			else
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Last death location not found!"));
				return CommandResult.empty();
			}
			playerz.setLastlocation(lastLocation);
		}
		else
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /back!"));
		}
		
		return CommandResult.success();
	}

	@Nonnull
	@Override
	public String[] getAliases()
	{
		return new String[] { "back" };
	}

	@Nonnull
	@Override
	public CommandSpec getSpec()
	{
		return CommandSpec.builder()
				.description(Text.of("Back Command"))
				.permission("rrrp2.general.back")
				.executor(this).build();
	}
}
