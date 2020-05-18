package com.github.ericliucn;

import com.github.ericliucn.command.Base;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.config.PlaceHolderManager;
import com.google.inject.Inject;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;

@Plugin(
        id = "eclean",
        name = "Eclean",
        description = "Used to clean dropped item",
        authors = {
                "EricLiu"
        },
        dependencies = {
                @Dependency(id = "placeholderapi")
        }
)
public class Main {

    public static Main instance;

    @Inject
    public Logger logger;

    @Inject
    public PluginContainer pluginContainer;

    @Inject
    @ConfigDir(sharedRoot = false)
    public File file;

    public PlaceholderService service;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        service = Sponge.getServiceManager().provideUnchecked(PlaceholderService.class);

        PlaceHolderManager placeHolderManager = new PlaceHolderManager();

        try {
            Config.init();
        }catch (IOException e){
            e.printStackTrace();
        }

        Sponge.getCommandManager().register(this, Base.build(), "eclean");
    }
}
