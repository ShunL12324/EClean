package com.github.ericliucn.utils;

import com.flowpowered.math.vector.Vector3d;
import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.task.cleanitem.CleanItemTaskScheduler;
import com.mcsimonflash.sponge.teslalibs.inventory.Action;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.Page;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class Utils {

    public static int LAST_CLEAN_ITEM_COUNT = 0;
    public static int NEXT_CLEAN_ITEM_TIME = 0;
    public static List<Element> CLEANED_ITEM_STACK = new ArrayList<>();
    public static Map<Location<World>, Integer> BLOCK_TICK_COUNT = new HashMap<>();
    public static boolean IS_CHECK_TASK_CURRENTLY_ON = false;

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

    public static void playSoundForEveryone(SoundType soundType){
        for (Player player:Sponge.getServer().getOnlinePlayers()){
            player.playSound(soundType, player.getPosition(), 1D);
        }
    }

    public static void spawnParticle(World world, Vector3d position){
        world.spawnParticles(ParticleEffect.builder().type(ParticleTypes.BREAK_BLOCK).build(), position);
    }

    public static void setLastCleanItemStacks(List<ItemStack> itemStacks){
        CLEANED_ITEM_STACK.clear();
        itemStacks.forEach(itemStack -> {
            Element element = Element.builder()
                    .item(itemStack)
                    .onClick(Utils::elementClick)
                    .build();
            CLEANED_ITEM_STACK.add(element);
        });
    }

    public static Page getPage(Player player){
        Layout layout = Layout.builder()
                .set(Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).build()), 45, 46, 52, 53)
                .set(Page.FIRST, 47)
                .set(Page.PREVIOUS, 48)
                .set(Page.CURRENT, 49)
                .set(Page.NEXT, 50)
                .set(Page.LAST, 51)
                .build();

        return Page.builder()
                .layout(layout)
                .archetype(InventoryArchetypes.DOUBLE_CHEST)
                .onOpen(openAction -> {
                    openAction.getPlayer().playSound(SoundTypes.BLOCK_CHEST_OPEN, player.getPosition(), 1D);
                })
                .onClose(closeAction -> {
                    closeAction.getPlayer().playSound(SoundTypes.BLOCK_CHEST_CLOSE, player.getPosition(), 1D);
                })
                .property(InventoryTitle.of(papiReplace(Config.msg_inv_title, player, player)))
                .build(Main.instance.pluginContainer)
                .define(CLEANED_ITEM_STACK);
    }

    private static void elementClick(Action.Click<ClickInventoryEvent> click){
        Player player = click.getPlayer();
        Element elementClick = click.getElement();
        click.getSlot().getInventoryProperty(SlotIndex.class).ifPresent(slotIndex -> {
            Optional<Integer> optionalInteger = Optional.ofNullable(slotIndex.getValue());
            optionalInteger.ifPresent(index -> {

                ItemStackSnapshot stackSnapshot = click.getEvent().getTransactions().get(0).getFinal();

                if (index >= 0 && index < 45 && stackSnapshot.isEmpty()){
                    if (CLEANED_ITEM_STACK.contains(elementClick)) {
                        click.getEvent().setCancelled(false);
                        CLEANED_ITEM_STACK.remove(elementClick);
                    }else {
                        player.sendMessage(Utils.papiReplace(Config.msg_item_has_taken, player, player));
                    }
                }
            });
        });
    }

    public static <T extends Event> void registerListener(Class<T> eventClass, EventListener<? super T> listener){
        Sponge.getEventManager().registerListener(Main.instance, eventClass, listener);
    }

    public static void unregisterListener(Object listener){
        Sponge.getEventManager().unregisterListeners(listener);
    }

}
