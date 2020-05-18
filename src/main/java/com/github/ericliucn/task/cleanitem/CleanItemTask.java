package com.github.ericliucn.task.cleanitem;

import com.flowpowered.math.vector.Vector3f;
import com.mcsimonflash.sponge.teslalibs.animation.AnimUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.server.FMLServerHandler;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

//清理掉落物任务
public class CleanItemTask {

    //清理掉的物品
    private final List<ItemStack> cleanedItemStack = new ArrayList<>();
    //清理掉的物品计数
    private int cleanedItemCount = 0;
    //清理过的区块计数
    private int cleanedChunk = 0;

    //清理任务constructor
    public CleanItemTask(World world){
        world.getLoadedChunks().forEach(this::cleanItem);
    }

    private void cleanItem(Chunk chunk){
        chunk.getEntities(entity -> entity.getType().equals(EntityTypes.ITEM))
                .forEach(entity -> {
                    Item item = (Item)entity;
                    ItemStackSnapshot stackSnapshot = item.item().get();
                    ItemStack itemStack = ItemStack.builder().fromSnapshot(stackSnapshot).build();
                    this.cleanedItemStack.add(itemStack);
                    this.cleanedItemCount += 1;
                    entity.remove();
                });
        this.cleanedChunk += 1;
    }

    //获取清理物品数量
    public int getCleanedItemCount() {
        return cleanedItemCount;
    }

    //获取清理的物品
    public List<ItemStack> getCleanedItemStack() {
        return cleanedItemStack;
    }

    //获取清理区块数量
    public int getCleanedChunk() {
        return cleanedChunk;
    }
}
