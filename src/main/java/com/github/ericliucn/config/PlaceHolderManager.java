package com.github.ericliucn.config;

import com.github.ericliucn.Main;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.Token;

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
                return Main.LAST_CLEANED_ITEM_COUNT;
            case NEXT_CLEAN_TIME:
                return Main.NEXT_CLEAN_ITEM_TIME;
            default:
                return 0;
        }
    }

}
