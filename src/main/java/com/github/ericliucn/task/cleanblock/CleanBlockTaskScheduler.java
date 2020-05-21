package com.github.ericliucn.task.cleanblock;

import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.task.cleanitem.CleanItemTaskScheduler;
import com.github.ericliucn.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public class CleanBlockTaskScheduler {

    public Task task;

    public CleanBlockTaskScheduler(){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!Utils.IS_CHECK_TASK_CURRENTLY_ON){
                    new CleanBlockTask(Sponge.getServer().getConsole());
                }
            }
        };

        task = Sponge.getScheduler().createTaskBuilder()
                .delay(Config.cleanBlockInterval, TimeUnit.SECONDS)
                .execute(runnable)
                .interval(Config.cleanBlockInterval, TimeUnit.SECONDS)
                .name("EClean Clean Block Task")
                .submit(Main.instance);
    }
}
