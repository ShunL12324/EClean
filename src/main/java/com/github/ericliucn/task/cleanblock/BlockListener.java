package com.github.ericliucn.task.cleanblock;

import com.github.ericliucn.config.Config;
import com.github.ericliucn.task.cleanitem.CleanItemTask;
import com.github.ericliucn.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;


public class BlockListener implements EventListener<ChangeBlockEvent.Modify> {

    @Override
    public void handle(ChangeBlockEvent.Modify event) throws Exception {
        event.getTransactions().forEach(
                transaction -> {
                    BlockSnapshot blockSnapshot = transaction.getDefault();
                    String id = blockSnapshot.getState().getType().getId();
                    if (Config.blocksNeedWatch.contains(id)){
                        if(Utils.BLOCK_TICK_COUNT.containsKey(blockSnapshot)){
                            int count = Utils.BLOCK_TICK_COUNT.get(blockSnapshot) + 1;
                            Utils.BLOCK_TICK_COUNT.put(blockSnapshot, count);
                        }else {
                            Utils.BLOCK_TICK_COUNT.put(blockSnapshot, 0);
                        }
                    }
                }
        );
    }
}
