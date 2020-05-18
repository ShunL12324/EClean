package com.github.ericliucn.config;

import com.github.ericliucn.Main;
import me.rojo8399.placeholderapi.Listening;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;

public class PlaceHolderManager {

    private final String LAST_CLEAN_NUM = "last_clean_items";
    private final String NEXT_CLEAN_TIME = "next_clean_time";

    public PlaceholderService service = Main.instance.service;

    public void init(){
        service.loadAll(this, Main.instance)
                .stream()
                .map(builder -> {
                    switch (builder.getId()){
                        case LAST_CLEAN_NUM:
                            return builder.description("返回上一次的清理数量");
                        case NEXT_CLEAN_TIME:
                            return builder.description("返回距离下一次清理的时间");
                    }
                    return builder;
                })
                .map(builder -> builder.version("1.0").plugin(Main.instance).author("EricLiu").description("用于EClean"))
                .forEach(builder -> {
                    try {
                        builder.buildAndRegister();
                    }catch (Exception e){
                        Main.instance.logger.error("[EClean] 尝试注册PAPI变量失败");
                    }
                });
    }

    @Placeholder(id = "last_clean_items")
    public int getLastCleanItemCount(){
        return Config.LAST_CLEAN_ITEM_COUNT;
    }

    @Placeholder(id = "next_clean_time")
    public int getNextCleanTime(){
        return 30;
    }

}
