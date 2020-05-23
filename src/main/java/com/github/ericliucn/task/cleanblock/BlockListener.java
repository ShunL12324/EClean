package com.github.ericliucn.task.cleanblock;

import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.*;


public class BlockListener implements EventListener<ChangeBlockEvent.Modify> {

    @Override
    public void handle(ChangeBlockEvent.Modify event) {
        event.getTransactions().forEach(
                transaction -> {
                    BlockSnapshot blockSnapshot = transaction.getOriginal();
                    String id = blockSnapshot.getState().getType().getId();
                    blockSnapshot.getLocation().ifPresent(location -> {
                        Map<String, Double> map = new HashMap<>();
                        if (Main.BLOCK_TICK_COUNT.containsKey(location)){
                            map = Main.BLOCK_TICK_COUNT.get(location);
                            map.put("count", map.get("count") + 1D);
                        }else {
                            if (!Config.blackListMode && Config.blocksNeedWatch.containsKey(id)){
                                map.put("limit", Config.blocksNeedWatch.get(id));
                                map.put("count", 1D);
                                Main.BLOCK_TICK_COUNT.put(location, map);
                            }

                            if (Config.blackListMode && !Config.blocksNeedWatch.containsKey(id)){
                                map.put("limit", Config.blackListModeTickRate);
                                map.put("count", 1D);
                                Main.BLOCK_TICK_COUNT.put(location, map);
                            }
                        }
                    });

                }
        );
    }

}
