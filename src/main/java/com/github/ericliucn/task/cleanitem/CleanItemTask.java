package com.github.ericliucn.task.cleanitem;

import com.flowpowered.math.vector.Vector3d;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.utils.Utils;
import com.google.common.reflect.TypeToken;
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

//清理掉落物任务
public class CleanItemTask {

    //清理掉的物品
    private final List<ItemStack> cleanedItemStack = new ArrayList<>();
    //清理掉的物品计数
    private int cleanedItemCount = 0;

    //清理任务constructor
    public CleanItemTask() throws ObjectMappingException {
        Sponge.getServer().getWorlds().forEach(
                world -> {
                    if (!Config.skipWorlds.contains(world.getName())){
                        this.cleanItem(world);
                    }
                }
        );
        //写入数据
        Utils.LAST_CLEAN_ITEM_COUNT = this.cleanedItemCount;
        Utils.LAST_CLEAN_ITEM_STACKS.clear();
        Utils.LAST_CLEAN_ITEM_STACKS = this.cleanedItemStack;
        //广播消息
        this.broadCast();
    }

    private void cleanItem(World world){
        world.getEntities().forEach(
                entity -> {
                    if (entity.getType().equals(EntityTypes.ITEM)){
                        Item item = (Item)entity;
                        ItemStack itemStack = ItemStack.builder().fromSnapshot(item.item().get()).build();
                        if (isSkipItem(itemStack)) return;
                        if (Config.skipItemHasNBT && itemHasNBT(itemStack)) return;
                        if (Config.skipItemHasLore && itemHansLore(itemStack)) return;
                        try {
                            if (Config.loreMatch.size() != 0 && itemHasTargetLore(itemStack)) return;
                        } catch (ObjectMappingException e) {
                            e.printStackTrace();
                        }
                        Vector3d position = entity.getLocation().getPosition();
                        if (Config.particleEffect) Utils.spawnParticle(world, position);
                        item.remove();
                        this.cleanedItemCount += 1;
                        this.cleanedItemStack.add(itemStack);
                    }
                }
        );
    }

    private void broadCast(){
        Utils.broadCastWithPapi(Config.msg_clean_finished, true);
        Utils.playSoundForEveryone(SoundTypes.BLOCK_ANVIL_LAND);
    }

    //获取清理物品数量
    public int getCleanedItemCount() {
        return cleanedItemCount;
    }

    //获取清理的物品
    public List<ItemStack> getCleanedItemStack() {
        return cleanedItemStack;
    }
    
    private static boolean itemHasNBT(ItemStack itemStack){
        return ItemStackUtil.toNative(itemStack).hasTagCompound();
    }

    private static boolean itemHansLore(ItemStack itemStack){
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
