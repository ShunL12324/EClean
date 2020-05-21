package com.github.ericliucn.command;

import com.github.ericliucn.inventory.CleanedItemInv;
import com.github.ericliucn.utils.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;

public class Last implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player){
            Player player = ((Player) src);
            CleanedItemInv.getPage(player).open(player);
        }

        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec
                .builder()
                .permission("eclean.last")
                .executor(new Last())
                .build();
    }
}
