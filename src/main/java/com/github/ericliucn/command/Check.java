package com.github.ericliucn.command;

import com.github.ericliucn.config.Config;
import com.github.ericliucn.task.cleanblock.CleanBlockTask;
import com.github.ericliucn.utils.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;

public class Check implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        new CleanBlockTask(src);
        src.sendMessage(Utils.formatStr(Config.msg_block_check_task_start));
        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .permission("eclean.check")
                .description(Utils.formatStr("&b手动执行一次高频方块检测任务"))
                .executor(new Check())
                .build();
    }
}
