package org.dimasik.litebinds.listeners;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.dimasik.litebinds.LiteBinds;
import org.dimasik.litebinds.database.ActionType;
import org.dimasik.litebinds.database.DatabaseManager;
import org.dimasik.litebinds.database.PlayerActions;

import java.util.Optional;

public class EventListener implements Listener {
    @EventHandler
    public void on(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if(event.getItemDrop().getItemStack().getType() == Material.NETHERITE_SWORD){
            try {
                Optional<PlayerActions> playerActionsOptional = LiteBinds.getInstance().getDatabaseManager().getPlayerActions(player.getName()).get();
                PlayerActions playerActions;
                playerActions = playerActionsOptional.orElseGet(() -> new PlayerActions(
                        player.getName(),
                        ActionType.NONE,
                        ActionType.NONE,
                        ActionType.NONE
                ));

                ActionType actionType = playerActions.getActionDrop();
                trigger(event, player, actionType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @EventHandler
    public void on(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();
        if(event.getOffHandItem() != null && event.getOffHandItem().getType() == Material.NETHERITE_SWORD){
            try {
                Optional<PlayerActions> playerActionsOptional = LiteBinds.getInstance().getDatabaseManager().getPlayerActions(player.getName()).get();
                PlayerActions playerActions;
                playerActions = playerActionsOptional.orElseGet(() -> new PlayerActions(
                        player.getName(),
                        ActionType.NONE,
                        ActionType.NONE,
                        ActionType.NONE
                ));

                ActionType actionType = playerActions.getActionSwap();
                trigger(event, player, actionType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }
        if(event.getItem() != null && event.getItem().getType() == Material.NETHERITE_SWORD){
            try {
                Optional<PlayerActions> playerActionsOptional = LiteBinds.getInstance().getDatabaseManager().getPlayerActions(player.getName()).get();
                PlayerActions playerActions;
                playerActions = playerActionsOptional.orElseGet(() -> new PlayerActions(
                        player.getName(),
                        ActionType.NONE,
                        ActionType.NONE,
                        ActionType.NONE
                ));

                ActionType actionType = playerActions.getActionInteract();
                trigger(event, player, actionType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void trigger(Cancellable event, Player player, ActionType actionType){
        switch (actionType){
            case SNOWBALL -> {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("HolyLiteItems");
                if(plugin == null){
                    return;
                }
                event.setCancelled(true);
                for(ItemStack itemStack : player.getInventory()) {
                    if(itemStack != null && itemStack.hasItemMeta()) {
                        if(itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "item-type"), PersistentDataType.STRING, "none").equalsIgnoreCase("snowball")) {
                            Snowball snowball = player.launchProjectile(Snowball.class);
                            snowball.setItem(itemStack);
                            snowball.setShooter(player);
                            PlayerLaunchProjectileEvent ev = new PlayerLaunchProjectileEvent(player, itemStack, snowball);
                            Bukkit.getServer().getPluginManager().callEvent(ev);
                            if(!ev.isCancelled()) {
                                itemStack.setAmount(itemStack.getAmount() - 1);
                            }
                            return;
                        }
                    }
                }
            }
            case JAKE -> {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("HolyLiteItems");
                if(plugin == null){
                    return;
                }
                event.setCancelled(true);
                for(ItemStack itemStack : player.getInventory()) {
                    if(itemStack != null && itemStack.hasItemMeta()) {
                        if(itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "item-type"), PersistentDataType.STRING, "none").equalsIgnoreCase("jake")) {
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(
                                    player,
                                    Action.RIGHT_CLICK_AIR,
                                    itemStack,
                                    null, BlockFace.DOWN
                            ));
                            return;
                        }
                    }
                }
            }
            case ALTERNATIVE_TRAP -> {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("HolyLiteItems");
                if(plugin == null){
                    return;
                }
                event.setCancelled(true);
                for(ItemStack itemStack : player.getInventory()) {
                    if(itemStack != null && itemStack.hasItemMeta()) {
                        if(itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "item-type"), PersistentDataType.STRING, "none").equalsIgnoreCase("alternative_trap")) {
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(
                                    player,
                                    Action.RIGHT_CLICK_AIR,
                                    itemStack,
                                    null, BlockFace.DOWN
                            ));
                            return;
                        }
                    }
                }
            }
            case STAN -> {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("HolyLiteItems");
                if(plugin == null){
                    return;
                }
                event.setCancelled(true);
                for(ItemStack itemStack : player.getInventory()) {
                    if(itemStack != null && itemStack.hasItemMeta()) {
                        if(itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "item-type"), PersistentDataType.STRING, "none").equalsIgnoreCase("stan")) {
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(
                                    player,
                                    Action.RIGHT_CLICK_AIR,
                                    itemStack,
                                    null, BlockFace.DOWN
                            ));
                            return;
                        }
                    }
                }
            }
            case TRAP -> {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("HolyLiteItems");
                if(plugin == null){
                    return;
                }
                event.setCancelled(true);
                for(ItemStack itemStack : player.getInventory()) {
                    if(itemStack != null && itemStack.hasItemMeta()) {
                        if(itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "item-type"), PersistentDataType.STRING, "none").equalsIgnoreCase("explosive_trap")) {
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(
                                    player,
                                    Action.RIGHT_CLICK_AIR,
                                    itemStack,
                                    null, BlockFace.DOWN
                            ));
                            return;
                        }
                    }
                }
            }
            case EXPLOSIVE -> {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("HolyLiteItems");
                if(plugin == null){
                    return;
                }
                event.setCancelled(true);
                for(ItemStack itemStack : player.getInventory()) {
                    if(itemStack != null && itemStack.hasItemMeta()) {
                        if(itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "item-type"), PersistentDataType.STRING, "none").equalsIgnoreCase("explosive_stuff")) {
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(
                                    player,
                                    Action.RIGHT_CLICK_AIR,
                                    itemStack,
                                    null, BlockFace.DOWN
                            ));
                            return;
                        }
                    }
                }
            }
            case BACKPACK -> {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("HolyBackPack");
                if(plugin == null){
                    return;
                }
                event.setCancelled(true);
                for(ItemStack itemStack : player.getInventory()) {
                    if(itemStack != null && itemStack.hasItemMeta()) {
                        if(itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "backpack-level"), PersistentDataType.INTEGER, -1) == 4) {
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(
                                    player,
                                    Action.RIGHT_CLICK_AIR,
                                    itemStack,
                                    null, BlockFace.DOWN
                            ));
                            return;
                        }
                    }
                }
            }
        }
    }
}
