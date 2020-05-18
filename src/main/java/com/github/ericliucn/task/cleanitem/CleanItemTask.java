package com.github.ericliucn.task.cleanitem;

import com.flowpowered.math.vector.Vector3f;
import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.utils.Utils;
import com.google.common.reflect.TypeToken;
import com.mcsimonflash.sponge.teslalibs.animation.AnimUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.server.FMLServerHandler;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.World;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.awt.*;
import java.util.*;
import java.util.List;

//清理掉落物任务
public class CleanItemTask {

    //清理掉的物品
    private final List<ItemStack> cleanedItemStack = new ArrayList<>();
    //清理掉的物品计数
    private int cleanedItemCount = 0;
    //需要清理的世界
    List<World> worlds = new ArrayList<>();

    //清理任务constructor
    public CleanItemTask() throws ObjectMappingException {
        //从配置文件读取需要清理的世界
        Config.rootNode.getNode("Worlds").getList(TypeToken.of(String.class))
                .forEach(s -> {
                    Optional<World> optionalWorld = Sponge.getServer().getWorld(s);
                    if(optionalWorld.isPresent()){
                        worlds.add(optionalWorld.get());
                    }else {
                        Main.instance.logger.error("世界 " + s + "不存在，请检查配置");
                    }
                });
        //清理物品
        this.worlds.forEach(this::cleanItem);
        //写入数据
        Utils.LAST_CLEAN_ITEM_COUNT = this.cleanedItemCount;
        //广播消息
        this.broadCast();
    }

    private void cleanItem(World world){
        world.getEntities().forEach(
                entity -> {
                    if (entity.getType().equals(EntityTypes.ITEM)){
                        Item item = (Item)entity;
                        ItemStack itemStack = ItemStack.builder().fromSnapshot(item.item().get()).build();
                        item.remove();
                        this.cleanedItemCount += 1;
                        this.cleanedItemStack.add(itemStack);
                    }
                }
        );
    }

    private void broadCast(){
        String msg = Config.msg.getProperty("CleanFinished");
        Sponge.getServer().getBroadcastChannel().send(Utils.placeHolderWrap(msg,null,null));
    }

    //获取清理物品数量
    public int getCleanedItemCount() {
        return cleanedItemCount;
    }

    //获取清理的物品
    public List<ItemStack> getCleanedItemStack() {
        return cleanedItemStack;
    }

}
