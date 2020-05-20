package com.github.ericliucn.task.cleanblock;

import com.flowpowered.math.vector.Vector3i;
import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.utils.Utils;
import com.google.inject.internal.cglib.core.$DefaultNamingPolicy;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CleanBlockTask {

    public EventListener<ChangeBlockEvent.Modify> listener = new BlockListener();

    public CleanBlockTask(){
        Utils.BLOCK_TICK_COUNT.clear();
        Utils.registerListener(ChangeBlockEvent.Modify.class, listener);
        this.unregister();
    }

    private void unregister(){
        Sponge.getScheduler().createTaskBuilder()
                .delay(5, TimeUnit.SECONDS)
                .execute(()->{
                    Utils.unregisterListener(listener);
                    outputResult();
                })
                .submit(Main.instance);
    }

    private void outputResult(){
        Utils.BLOCK_TICK_COUNT.forEach(((blockSnapshot, integer) -> {
            Vector3i position = blockSnapshot.getPosition();
            Text name = blockSnapshot.get(Keys.DISPLAY_NAME).orElse(Text.of("UNKNOW"));
            Sponge.getServer().getBroadcastChannel().send(
                    Text.join(Text.of(position.getX(),",", position.getY(), ",", position.getZ(),
                            "的", name, "在5秒内刷新了", integer, "次"))
            );
        }));
    }

}
