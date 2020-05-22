package com.github.ericliucn.utils;

import com.flowpowered.math.vector.Vector3d;
import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

public class Utils {

    public static Text formatStr(String string){
        return TextSerializers.FORMATTING_CODE.deserialize(string);
    }

    public static void broadCastWithPapi(String string, boolean canOpenLastClean){
        Sponge.getServer().getOnlinePlayers().forEach(player -> {
            Text text = papiReplace(string, player, player);
            if (canOpenLastClean){
                Text hoverMsg = papiReplace(Config.msg_clean_finished_hover, player, player);
                HoverAction.ShowText showText = TextActions.showText(hoverMsg);
                ClickAction.RunCommand runCommand = TextActions.runCommand("/eclean last");
                text = text.toBuilder()
                        .onHover(showText)
                        .onClick(runCommand)
                        .build();
            }
            player.sendMessage(text);
        });
    }

    public static Text papiReplace(String string, CommandSource source, Player player){
        return Main.instance.service.replacePlaceholders(formatStr(string), source, player);
    }

    public static Text papiReplace(Text text, CommandSource source, Player player){
        return Main.instance.service.replacePlaceholders(text, source, player);
    }

    public static void playSoundForEveryone(SoundType soundType){
        for (Player player:Sponge.getServer().getOnlinePlayers()){
            player.playSound(soundType, player.getPosition(), 1D);
        }
    }

    public static void spawnParticle(World world, Vector3d position){
        world.spawnParticles(ParticleEffect.builder().type(ParticleTypes.BREAK_BLOCK).build(), position);
    }

    public static <T extends Event> void registerListener(Class<T> eventClass, EventListener<? super T> listener){
        Sponge.getEventManager().registerListener(Main.instance, eventClass, listener);
    }

    public static void unregisterListener(Object listener){
        Sponge.getEventManager().unregisterListeners(listener);
    }

}
