package com.github.ericliucn.task.cleanblock;

import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;


public class BlockListener implements EventListener<ChangeBlockEvent.Modify> {

    @Override
    public void handle(ChangeBlockEvent.Modify event) throws Exception {
        event.getTransactions().forEach(
                transaction -> {
                    BlockSnapshot blockSnapshot = transaction.getOriginal();
                    String id = blockSnapshot.getState().getType().getId();
                    if (Config.blocksNeedWatch.containsKey(id)){
                        blockSnapshot.getLocation().ifPresent(location -> {
                            if (Main.BLOCK_TICK_COUNT.containsKey(location)){
                                Main.BLOCK_TICK_COUNT.put(location, Main.BLOCK_TICK_COUNT.get(location) + 1);
                            }else {
                                Main.BLOCK_TICK_COUNT.put(location, 1);
                            }
                        });
                    }
                }
        );
    }
}
