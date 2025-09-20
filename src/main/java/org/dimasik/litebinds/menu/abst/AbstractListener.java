package org.dimasik.litebinds.menu.abst;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.dimasik.litebinds.LiteBinds;

public abstract class AbstractListener implements Listener {
    @EventHandler
    public abstract void onClick(InventoryClickEvent event);

    @EventHandler
    public void onClose(InventoryCloseEvent event) { }

    private void register(JavaPlugin plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void register(){
        this.register(LiteBinds.getInstance());
    }
}