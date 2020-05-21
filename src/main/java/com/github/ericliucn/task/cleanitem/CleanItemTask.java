package com.github.ericliucn.task.cleanitem;

import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.inventory.CleanedItemInv;
import com.github.ericliucn.utils.Utils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;

//clean dropped item task
public class CleanItemTask {

    //item has been cleaned
    private final List<ItemStack> cleanedItemStack = new ArrayList<>();
    //item cleaned count
    private int cleanedItemCount = 0;

    //constructor
    public CleanItemTask() throws ObjectMappingException {
        Sponge.getServer().getWorlds().forEach(
                world -> {
                    if (!Config.skipWorlds.contains(world.getName())){
                        this.cleanItem(world);
                    }
                }
        );
        //写入数据
        Main.LAST_CLEANED_ITEM_COUNT = this.cleanedItemCount;
        CleanedItemInv.genInvElements(this.cleanedItemStack);
        //广播消息
        this.message();
    }

    private void cleanItem(World world){
        world.getEntities().forEach(
                entity -> {
                    if (entity.getType().equals(EntityTypes.ITEM)){
                        //item entity
                        Item item = (Item)entity;
                        //item item stack
                        ItemStack itemStack = item.item().get().createStack();
                        //check item through item's id
                        if (isSkipItem(itemStack)) return;
                        //check item whether it has NBT and whether item with nbt need to be remove
                        if (Config.skipItemHasNBT && itemHasNBT(itemStack)) return;
                        //check whether item has lore need to be remove and whether the item has lore
                        if (Config.skipItemHasLore && itemHasLore(itemStack)) return;
                        //check whether item has target lore (won't be remove)
                        try {
                            if (Config.loreMatch.size() != 0 && itemHasTargetLore(itemStack)) return;
                        } catch (ObjectMappingException e) {
                            e.printStackTrace();
                        }
                        //if config effect is on, spawn effect
                        if (Config.particleEffect) {
                            Utils.spawnParticle(world, item.getLocation().getPosition());
                        }
                        //remove item and add count
                        item.remove();
                        this.cleanedItemCount += itemStack.getQuantity();
                        this.cleanedItemStack.add(itemStack);
                    }
                }
        );
    }


    private void message(){
        Utils.broadCastWithPapi(Config.msg_clean_finished, true);
        if (Config.soundWhenNotify) {
            Utils.playSoundForEveryone(SoundTypes.BLOCK_ANVIL_LAND);
        }
    }
    
    private static boolean itemHasNBT(ItemStack itemStack){
        return ItemStackUtil.toNative(itemStack).hasTagCompound();
    }

    private static boolean itemHasLore(ItemStack itemStack){
        return itemStack.get(Keys.ITEM_LORE).isPresent();
    }

    private static boolean itemHasTargetLore(ItemStack itemStack) throws ObjectMappingException {
        if (!itemStack.get(Keys.ITEM_LORE).isPresent()){
            return false;
        }
        List<Text> loreList = itemStack.get(Keys.ITEM_LORE).get();
        for (Text text:loreList){
            if (Config.loreMatch.contains(text.toPlain())) return true;
        }
        return false;
    }

    private static boolean isSkipItem(ItemStack itemStack){
        String id = itemStack.getType().getId();
        String meta = String.valueOf(ItemStackUtil.toNative(itemStack).getMetadata());
        return Config.skipItems.contains(id + ":" + meta);
    }

}
