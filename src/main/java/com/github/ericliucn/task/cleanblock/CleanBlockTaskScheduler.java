package com.github.ericliucn.task.cleanblock;

import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.task.cleanitem.CleanItemTaskScheduler;
import com.github.ericliucn.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public class CleanBlockTaskScheduler {

    private final Task task;

    public CleanBlockTaskScheduler(){

        Runnable runnable = () -> new CleanBlockTask(Sponge.getServer().getConsole());

        task = Sponge.getScheduler().createTaskBuilder()
                .delay(Config.cleanBlockInterval, TimeUnit.SECONDS)
                .execute(runnable)
                .interval(Config.cleanBlockInterval, TimeUnit.SECONDS)
                .name("EClean Clean Block Task")
                .submit(Main.instance);
    }

    public void cancel(){
        this.task.cancel();
    }
}
