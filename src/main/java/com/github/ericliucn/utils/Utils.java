package com.github.ericliucn.utils;

import com.flowpowered.math.vector.Vector3d;
import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.Page;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;
import org.spongepowered.common.mixin.api.mcp.entity.player.EntityPlayerMPMixin_API;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Utils {

    public static int LAST_CLEAN_ITEM_COUNT = 0;
    public static int NEXT_CLEAN_ITEM_TIME = 0;
    public static List<Element> CLEANED_ITEM_STACK = new ArrayList<>();

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
                    .onClick(click -> {
                        Element thisElement = click.getElement();
                        Player player = click.getPlayer();
                        Optional<SlotIndex> optionalSlotIndex = click.getSlot().getInventoryProperty(SlotIndex.class);

                        optionalSlotIndex.ifPresent(slotIndex -> {
                            int index = slotIndex.getValue();
                            if (index < 45 && index >= 0){
                                click.getEvent().setCancelled(false);
                                CLEANED_ITEM_STACK.remove(thisElement);
                            }
                        });

                    })
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

}
