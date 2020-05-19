package com.github.ericliucn.config;

import com.github.ericliucn.Main;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.asset.Asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Config {

    private final static File conf = new File(Main.instance.file,"eclean.conf");
    private final static File message = new File(Main.instance.file,"message.properties");
    private final static ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(conf).build();
    public static Properties msg = new Properties();
    public static CommentedConfigurationNode rootNode;

    public static void init() throws IOException {
        if (!Main.instance.file.exists()){
            Main.instance.file.mkdir();
        }

        Asset assetConf = Main.instance.pluginContainer.getAsset("eclean.conf").get();
        Asset assetMessage = Main.instance.pluginContainer.getAsset("message.properties").get();

        if (!conf.exists()){
            assetConf.copyToDirectory(Main.instance.file.toPath());
        }

        if (!message.exists()){
            assetMessage.copyToDirectory(Main.instance.file.toPath());
        }

        load();
    }

    public static void load() throws IOException{
        rootNode = loader.load();
        msg.load(new InputStreamReader(new FileInputStream(message), StandardCharsets.UTF_8));
    }

    public static void save() throws IOException{
        loader.save(rootNode);
    }
}
