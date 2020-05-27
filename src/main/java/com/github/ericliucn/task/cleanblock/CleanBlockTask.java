package com.github.ericliucn.task.cleanblock;

import com.flowpowered.math.vector.Vector3d;
import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CleanBlockTask {

    public EventListener<ChangeBlockEvent.Modify> listener = new BlockListener();

    public CleanBlockTask(CommandSource source){
        if (!Main.IS_CHECK_TASK_CURRENTLY_ON) {
            Main.BLOCK_TICK_COUNT.clear();
            Main.IS_CHECK_TASK_CURRENTLY_ON = true;
            Utils.registerListener(ChangeBlockEvent.Modify.class, listener);
            this.closeChecker();
        }else {
            source.sendMessage(Utils.formatStr(Config.msg_last_task_not_finished));
        }
    }

    private void closeChecker(){
        Sponge.getScheduler().createTaskBuilder()
                .delayTicks(40)
                .execute(()->{
                    Utils.unregisterListener(listener);
                    if (!Main.IS_CHECK_TASK_CURRENTLY_ON) return;
                    this.processResult();
                    Main.IS_CHECK_TASK_CURRENTLY_ON = false;
                })
                .submit(Main.instance);
    }

    private void processResult(){
        Main.BLOCK_TICK_COUNT.forEach(((location, integer) -> {
            World world = location.getExtent();
            Vector3d position = location.getPosition();
            String name = location.getBlock().getType().getId();
            String strPosition = position.getX() + "/" + position.getY() + "/" + position.getZ();

            double limit = Main.BLOCK_TICK_COUNT.get(location).get("limit");
            double count = Main.BLOCK_TICK_COUNT.get(location).get("count");
            double tickRate = count/2D;
            if (tickRate > limit){
                String blockReport = Config.msg_block_report
                        .replace("{world_name}", world.getName())
                        .replace("{block_position}", strPosition)
                        .replace("{block_name}", name)
                        .replace("{tick_rate}", String.valueOf(tickRate));

                //get around player if present
                Player aroundPlayer = getPlayerAround(location);
                if (aroundPlayer!=null){
                    blockReport = blockReport.replace("{around_player}", aroundPlayer.getName());
                }else {
                    blockReport = blockReport.replace("{around_player}", "");
                }

                //check if it needed to remove block
                if (Config.cleanBlock){
                    blockReport = blockReport.replace("{is_cleaned}", "YES");
                    location.removeBlock();
                }else {
                    blockReport = blockReport.replace("{is_cleaned}", "NO");
                }

                //create List of text
                List<Text> reports = new ArrayList<>();
                String[] blockReports = blockReport.split(",");
                Arrays.stream(blockReports).forEach(s -> {
                    reports.add(Utils.formatStr(s));
                });

                Text tpButton = Utils.formatStr("&d[ECLEAN] &e>>>&a[TP]&e<<<")
                        .toBuilder()
                        .onHover(TextActions.showText(Utils.formatStr("&b点击传送到该位置")))
                        .onClick(TextActions.runCommand("/tppos "+ position.getX() + " "+ position.getY() + " " + position.getZ()))
                        .build();

                reports.add(tpButton);

                Sponge.getServer().getOnlinePlayers().forEach(player -> {

                    PaginationList.builder()
                            .contents(reports)
                            .title(Utils.formatStr("&d[ECLEAN]"))
                            .padding(Utils.formatStr("&a="))
                            .sendTo(player);

                });
            }
        }));
    }

    private static Player getPlayerAround(Location<World> location){
        World world = location.getExtent();
        for (Player player:world.getPlayers()
             ) {
            double PX = player.getPosition().getX();
            double PZ = player.getPosition().getZ();
            double LX = location.getX();
            double LZ = location.getZ();

            if (Math.abs(PX - LX) < 10 && Math.abs(PZ - LZ) < 10){
                return player;
            }
        }
        return null;
    }

}
