package com.github.ericliucn.task.cleanblock;

import com.github.ericliucn.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.concurrent.TimeUnit;

public class CleanBlockTask {

    public EventListener<ChangeBlockEvent.Modify> listener = new BlockListener();

    public CleanBlockTask(){

        Sponge.getEventManager().registerListener(Main.instance, ChangeBlockEvent.Modify.class, listener);
        Sponge.getScheduler().createTaskBuilder()
                .delay(30, TimeUnit.SECONDS)
                .execute(()->{
                    Sponge.getEventManager().unregisterListeners(listener);
                })
                .submit(Main.instance);

    }

}
