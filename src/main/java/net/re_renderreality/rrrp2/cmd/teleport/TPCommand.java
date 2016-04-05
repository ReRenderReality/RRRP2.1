package net.re_renderreality.rrrp2.cmd.teleport;

import javax.annotation.Nonnull;

import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.re_renderreality.rrrp2.RRRP2;
import net.re_renderreality.rrrp2.backend.AsyncCommandExecutorBase;
import net.re_renderreality.rrrp2.events.TPEvent;

public class TPCommand extends AsyncCommandExecutorBase {
	private Game game = RRRP2.getRRRP2().getGame();

	@Override
	public void executeAsync(CommandSource src, CommandContext ctx) {
		Player recipient = ctx.<Player> getOne("player").get();

		if (src instanceof Player) {
			Player player = (Player) src; 
			
			if (recipient == player) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot teleport to yourself!"));
			}
			else {
				src.sendMessage(Text.of(TextColors.GREEN, "Success! ", TextColors.YELLOW, "Requested to be teleported to " + recipient.getName() + "."));
				game.getEventManager().post(new TPEvent(player, recipient));
			}
		}
		else if (src instanceof ConsoleSource) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /tp!"));
		}
		else if (src instanceof CommandBlockSource) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /tp!"));
		}
	}

	@Nonnull
	@Override
	public String[] getAliases() {
		return new String[] { "tp", "TP" };
	}

	@Nonnull
	@Override
	public CommandSpec getSpec() {
		return CommandSpec
				.builder()
				.description(Text.of("TPA Command"))
				.permission("rrr.general.tp")
				.arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
				.executor(this)
				.build();
	}
}