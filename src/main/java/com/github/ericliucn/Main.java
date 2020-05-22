package com.github.ericliucn;

import com.github.ericliucn.command.Base;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.config.PlaceHolderManager;
import com.github.ericliucn.task.cleanblock.CleanBlockTaskScheduler;
import com.github.ericliucn.task.cleanitem.CleanItemTaskScheduler;
import com.google.inject.Inject;
import me.rojo8399.placeholderapi.PlaceholderService;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Plugin(
        id = "eclean",
        name = "Eclean",
        description = "Used to clean dropped item",
        authors = {
                "EricLiu"
        },
        dependencies = {
                @Dependency(id = "placeholderapi")
        }
)
public class Main {

    public static Main instance;

    @Inject
    public Logger logger;

    @Inject
    public PluginContainer pluginContainer;

    @Inject
    @ConfigDir(sharedRoot = false)
    public File file;

    public PlaceholderService service;

    public CleanItemTaskScheduler cleanItemTaskScheduler;
    public CleanBlockTaskScheduler cleanBlockTaskScheduler;
    public static int LAST_CLEANED_ITEM_COUNT = 0;
    public static int NEXT_CLEAN_ITEM_TIME = 0;
    public static Map<Location<World>, Map<String, Double>> BLOCK_TICK_COUNT = new HashMap<>();
    public static boolean IS_CHECK_TASK_CURRENTLY_ON = false;

    @Listener
    public void onServerStart(GameStartedServerEvent event) throws ObjectMappingException {
        //初始化实例
        instance = this;

        //加载配置
        try {
            Config.init();
        }catch (IOException e){
            e.printStackTrace();
        }

        //获取Placeholder
        service = Sponge.getServiceManager().provideUnchecked(PlaceholderService.class);
        PlaceHolderManager placeHolderManager = new PlaceHolderManager();

        //开始计划任务
        cleanItemTaskScheduler = new CleanItemTaskScheduler();
        if (Config.isEnableCleanBlock) {
            cleanBlockTaskScheduler = new CleanBlockTaskScheduler();
        }

        Sponge.getCommandManager().register(this, Base.build(), "eclean");
    }
}
