package com.github.ericliucn.command;

import com.github.ericliucn.utils.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Base implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        List<Text> texts = new ArrayList<>();
        Text clean = Utils.formatStr("&e/eclean clean &d-> &b手动执行一次清理任务");
        Text check = Utils.formatStr("&e/eclean check &d-> &b手动执行一次高频方块检测任务");
        Text last = Utils.formatStr("&e/eclean last &d-> &b查看并取回上一次清理掉的物品");
        Text reload = Utils.formatStr("&e/eclean reload &d-> &b重载插件配置");
        texts.add(clean);
        texts.add(check);
        texts.add(last);
        texts.add(reload);

        PaginationList.builder()
                .padding(Utils.formatStr("&a="))
                .title(Utils.formatStr("&d[ECLEAN]"))
                .contents(texts)
                .sendTo(src);

        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .permission("eclean.base")
                .executor(new Base())
                .child(Clean.build(), "c", "clean")
                .child(Reload.build(), "reload")
                .child(Last.build(), "last")
                .child(Check.build(), "check")
                .build();
    }
}
