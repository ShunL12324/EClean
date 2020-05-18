package com.github.ericliucn.utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class Utils {

    public static Text formatStr(String string){
        return TextSerializers.FORMATTING_CODE.deserialize(string);
    }

    public static void broadCast(String string){
        Sponge.getServer().getBroadcastChannel().send(formatStr(string));
    }

}
