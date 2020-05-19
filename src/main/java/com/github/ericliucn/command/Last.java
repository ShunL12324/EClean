package com.github.ericliucn.command;

import com.github.ericliucn.Main;
import com.github.ericliucn.utils.Utils;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;

import java.util.ArrayList;
import java.util.List;

public class Last implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        View view = View.builder()
                .archetype(InventoryArchetypes.CHEST)
                .onOpen(openAction -> {
                    Player player = openAction.getPlayer();
                    player.playSound(SoundTypes.BLOCK_CHEST_OPEN, player.getPosition(), 1D);
                })
                .onClose(closeAction -> {
                    Player player = closeAction.getPlayer();
                    player.playSound(SoundTypes.BLOCK_CHEST_OPEN, player.getPosition(), 1D);
                })
                .build(Main.instance.pluginContainer);

        List<Element> elements = new ArrayList<>();
        Utils.LAST_CLEAN_ITEM_STACKS.forEach(itemStack -> {
            Element element = Element.builder()
                    .item(itemStack)
                    .build();
            elements.add(element);
        });

        Layout layout = Layout.builder()
                .page(elements)
                .build();

        view.define(layout);

        view.open((Player) src);

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
