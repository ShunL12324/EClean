package com.github.ericliucn.inventory;

import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.utils.Utils;
import com.mcsimonflash.sponge.teslalibs.inventory.Action;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.Page;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CleanedItemInv {

    public static List<Element> CLEANED_ITEM_STACK_ELEMENT = new ArrayList<>();

    public static void genInvElements(List<ItemStack> itemStacks){
        CLEANED_ITEM_STACK_ELEMENT.clear();
        itemStacks.forEach(itemStack -> {
            Element element = Element.builder()
                    .item(itemStack)
                    .onClick(CleanedItemInv::elementClick)
                    .build();
            CLEANED_ITEM_STACK_ELEMENT.add(element);
        });
    }

    private static void elementClick(Action.Click<ClickInventoryEvent> click){
        Player player = click.getPlayer();
        Element clickElement = click.getElement();
        click.getSlot().getInventoryProperty(SlotIndex.class).ifPresent(slotIndex -> {
            Optional<Integer> optionalInteger = Optional.ofNullable(slotIndex.getValue());
            optionalInteger.ifPresent(index -> {

                ItemStackSnapshot stackSnapshot = click.getEvent().getTransactions().get(0).getFinal();

                if (index >= 0 && index < 45 && stackSnapshot.isEmpty()){
                    if (CLEANED_ITEM_STACK_ELEMENT.contains(clickElement)) {
                        click.getEvent().setCancelled(false);
                        CLEANED_ITEM_STACK_ELEMENT.remove(clickElement);
                    }else {
                        player.sendMessage(Utils.papiReplace(Config.msg_item_has_taken, player, player));
                    }
                }
            });
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
                .property(InventoryTitle.of(Utils.papiReplace(Config.msg_inv_title, player, player)))
                .build(Main.instance.pluginContainer)
                .define(CLEANED_ITEM_STACK_ELEMENT);
    }
}
