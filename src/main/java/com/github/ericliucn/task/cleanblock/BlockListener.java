package com.github.ericliucn.task.cleanblock;

import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.HashMap;
import java.util.Map;


public class BlockListener implements EventListener<ChangeBlockEvent.Modify> {

    @Override
    public void handle(ChangeBlockEvent.Modify event) {
        event.getTransactions().forEach(
                transaction -> {
                    BlockSnapshot blockSnapshot = transaction.getOriginal();
                    String id = blockSnapshot.getState().getType().getDefaultState().getType().getId();

                    blockSnapshot.getLocation().ifPresent(location -> {
                        Map<String, Double> map;
                        if (Main.BLOCK_TICK_COUNT.containsKey(location)){
                            map = Main.BLOCK_TICK_COUNT.get(location);
                            map.put("count", map.get("count") + 1D);
                        }else {
                            map = new HashMap<>();
                            map.put("limit", Config.blocksNeedWatch.get(id));
                            if (Config.blackListMode){
                                map.put("limit", Config.blackListModeTickRate);
                            }
                            map.put("count", 1D);
                        }
                        if(Config.blackListMode){
                            if (!Config.blocksNeedWatch.containsKey(id)){
                                Main.BLOCK_TICK_COUNT.put(location, map);
                            }
                        }else if (Config.blocksNeedWatch.containsKey(id)){
                            Main.BLOCK_TICK_COUNT.put(location, map);
                        }
                    });

                }
        );
    }
}
