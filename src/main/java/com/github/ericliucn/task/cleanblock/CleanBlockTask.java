package com.github.ericliucn.task.cleanblock;

import com.flowpowered.math.vector.Vector3d;
import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.world.World;

import java.util.concurrent.TimeUnit;

public class CleanBlockTask {

    public EventListener<ChangeBlockEvent.Modify> listener = new BlockListener();

    public CleanBlockTask(CommandSource source){
        if (!Utils.IS_CHECK_TASK_CURRENTLY_ON) {
            Utils.BLOCK_TICK_COUNT.clear();
            Utils.IS_CHECK_TASK_CURRENTLY_ON = true;
            Utils.registerListener(ChangeBlockEvent.Modify.class, listener);
            this.unregister();
        }else {
            source.sendMessage(Utils.formatStr(Config.msg_last_task_not_finished));
        }
    }

    private void unregister(){
        Sponge.getScheduler().createTaskBuilder()
                .delay(5, TimeUnit.SECONDS)
                .execute(()->{
                    Utils.unregisterListener(listener);
                    processResult();
                    Utils.IS_CHECK_TASK_CURRENTLY_ON = false;
                })
                .submit(Main.instance);
    }

    private void processResult(){
        Utils.BLOCK_TICK_COUNT.forEach(((location, integer) -> {
            World world = location.getExtent();
            Vector3d position = location.getPosition();
            String name = location.getBlock().getType().getTranslation().get();
            String id = location.getBlockType().getId();
            int limit = Config.blocksNeedWatch.get(id);
            float tickRate = integer/5F;
            if (tickRate > limit){
                Text report = Utils.formatStr(
                        Config.msg_block_report
                                .replace("{world_name}", world.getName())
                                .replace("{block_position}", position.toString())
                                .replace("{block_name}", name)
                                .replace("{tick_rate}", String.valueOf(tickRate))
                );

                Text actionReport = report.toBuilder()
                        .onHover(TextActions.showText(Utils.formatStr("&b点击传送到该位置")))
                        .onClick(TextActions.runCommand("/tppos "+ position.getX() + " "+ position.getY() + " " + position.getZ()))
                        .build();

                Sponge.getServer().getBroadcastChannel().send(actionReport);

                if (Config.cleanBlock){
                    location.removeBlock();
                    Utils.broadCastWithPapi(Config.msg_block_removed.replace("{block_name}", name), false);
                }
            }
        }));
    }

}
