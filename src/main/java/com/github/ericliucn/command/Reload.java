package com.github.ericliucn.command;

import com.github.ericliucn.config.Config;
import com.github.ericliucn.task.cleanitem.CleanItemTaskScheduler;
import com.github.ericliucn.utils.Utils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.scheduler.Task;

import java.io.IOException;

public class Reload implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        try { Config.load(); } catch (IOException | ObjectMappingException e) { e.printStackTrace(); }
        Sponge.getScheduler().getTasksByName("EClean Clean Items Task").forEach(Task::cancel);
        try {new CleanItemTaskScheduler(); } catch (ObjectMappingException e) {e.printStackTrace(); }
        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .executor(new Reload())
                .permission("eclean.reload")
                .description(Utils.formatStr("&b重载插件配置"))
                .build();
    }
}
