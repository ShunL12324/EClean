package com.github.ericliucn.command;

import com.github.ericliucn.task.cleanitem.CleanItemTask;
import com.github.ericliucn.utils.Utils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class Clean implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try {
            new CleanItemTask();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .permission("eclean.clean")
                .executor(new Clean())
                .description(Utils.formatStr("&b手动执行一次清理任务"))
                .build();
    }
}
