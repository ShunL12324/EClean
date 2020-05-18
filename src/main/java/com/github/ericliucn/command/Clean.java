package com.github.ericliucn.command;

import com.github.ericliucn.task.cleanitem.CleanItemTask;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class Clean implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<World> optionalWorld = args.getOne("world");
        if(optionalWorld.isPresent()){
            CleanItemTask cleanItemTask = new CleanItemTask(optionalWorld.get());

        }else {
            int chunkCount = 0;
            int itemCount = 0;
            for (World world:Sponge.getServer().getWorlds()
                 ) {
                CleanItemTask cleanItemTask = new CleanItemTask(world);
                chunkCount += cleanItemTask.getCleanedChunk();
                itemCount += cleanItemTask.getCleanedItemCount();
            }

            src.sendMessage(Text.of(String.format("共清理了%d个区块, %d个物品", chunkCount, itemCount)));
        }

        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .permission("eclean.clean")
                .executor(new Clean())
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.world(Text.of("world"))
                        )
                )
                .build();
    }
}
