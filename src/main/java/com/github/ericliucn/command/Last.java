package com.github.ericliucn.command;

import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.utils.Utils;
import com.mcsimonflash.sponge.teslalibs.inventory.Page;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.property.InventoryTitle;

public class Last implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player){
            Player player = ((Player) src);
            Utils.getPage(player).open(player);
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
