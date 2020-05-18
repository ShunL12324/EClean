package com.github.ericliucn.utils;

import com.github.ericliucn.Main;
import me.rojo8399.placeholderapi.Source;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class Utils {

    public static int LAST_CLEAN_ITEM_COUNT = 0;

    public static Text formatStr(String string){
        return TextSerializers.FORMATTING_CODE.deserialize(string);
    }

    public static void broadCast(String string){
        Sponge.getServer().getBroadcastChannel().send(formatStr(string));
    }

    public static Text placeHolderWrap(String string, Object source, Object receiver){
        return Main.instance.service.replacePlaceholders(formatStr(string), source, receiver);
    }

}
