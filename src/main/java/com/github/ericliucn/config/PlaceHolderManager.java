package com.github.ericliucn.config;

import com.github.ericliucn.Main;
import com.github.ericliucn.utils.Utils;
import me.rojo8399.placeholderapi.Listening;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import me.rojo8399.placeholderapi.Token;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

public class PlaceHolderManager {

    private final String LAST_CLEAN_NUM = "last_items";
    private final String NEXT_CLEAN_TIME = "next_time";

    public PlaceHolderManager(){
        Main.instance.service.loadAll(this, Main.instance).forEach(builder -> {
            if (builder.getId().equals("eclean")){
                try {
                    builder.description("For Eclean")
                            .addTokens(LAST_CLEAN_NUM, NEXT_CLEAN_TIME)
                            .version("1.0")
                            .author("EricLiu")
                            .plugin(Main.instance)
                            .buildAndRegister();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Placeholder(id = "eclean")
    public int getInfo(@Token String string){
        switch (string){
            case LAST_CLEAN_NUM:
                return Utils.LAST_CLEAN_ITEM_COUNT;
            case NEXT_CLEAN_TIME:
                return 30;
            default:
                return 0;
        }
    }

}
