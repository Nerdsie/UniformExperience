package me.richardcollins.universal.commands;

import me.richardcollins.tools.custom.particles.ParticleType;
import me.richardcollins.tools.events.icons.IconClickEvent;
import me.richardcollins.tools.events.menus.EmptyClickEvent;
import me.richardcollins.tools.handlers.IconHandler;
import me.richardcollins.tools.handlers.MenuHandler;
import me.richardcollins.tools.objects.elements.Icon;
import me.richardcollins.tools.objects.elements.Menu;
import me.richardcollins.universal.Helper;
import me.richardcollins.universal.Universal;
import me.richardcollins.universal.Settings;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class OptionCommand implements CommandExecutor {
    String overrideCommand = "";
    boolean requireConsole = false;
    boolean requirePlayer = true;
    boolean requirePermission = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (shouldCancel(sender, command)) {
            return true;
        }

        // -------------------------- //
        // Insert command stuff here! //
        // -------------------------- //

        final Player player = (Player) sender;

        if (args.length == 0) {
            Menu menu = new Menu(player.getName(), "Options", 6);
            menu.setOrganizeBeforeOpen(true);

            if (player.hasPermission("myuniversal.toggle.trail")) {
                Icon icon = new Icon(0, 0, ChatColor.AQUA + " Trail Settings", new ItemStack(Material.FIREWORK_CHARGE)).setHandler(new IconHandler() {
                    @Override
                    public void onClick(IconClickEvent event) {
                        super.onClick(event);

                        if (event.getOriginalEvent().getClick() == ClickType.RIGHT) {
                            Universal.getPlayerStats().set(getIcon().getParent().getOwnerName(), "trail.enabled", !(Universal.getPlayerStats().getBoolean(getIcon().getParent().getOwnerName(), "trail.enabled", false)));

                            event.setCloseInventory(false);
                            update();
                        } else {
                            Menu next = getTrailMenu(getIcon().getOwner());

                            next.setMeta("previous", event.getMenu());
                            event.setNewMenu(next);
                        }
                    }

                    @Override
                    public void update() {
                        super.update();

                        if (Universal.getPlayerStats().getBoolean(getIcon().getParent().getOwnerName(), "trail.enabled", false)) {
                            getIcon().setItemStack(new ItemStack(Material.NETHER_STAR, 1));
                            getIcon().setName(ChatColor.AQUA + " Choose your Trail! " + ChatColor.GREEN + ChatColor.BOLD + "(ENABLED)");

                            Universal.trails.put(getIcon().getOwner().getUniqueId(), ParticleType.fromName(Universal.getPlayerStats().getString(getIcon().getOwner().getName(), "trail.type", "hearts")));
                        } else {
                            getIcon().setItemStack(new ItemStack(Material.FIREWORK_CHARGE, 1));
                            getIcon().setName(ChatColor.AQUA + " Choose your Trail! " + ChatColor.RED + ChatColor.BOLD + "(DISABLED)");

                            if (Universal.trails.containsKey(getIcon().getOwner().getUniqueId())) {
                                Universal.trails.remove(getIcon().getOwner().getUniqueId());
                            }
                        }

                        getIcon().getParent().updateIcon(getIcon());
                    }
                });
                icon.addLore("");
                icon.addLore(ChatColor.GRAY + "  * Change particle effect following you.");
                icon.addLore("");
                icon.addLore(ChatColor.GREEN + " Left Click:");
                icon.addLore(ChatColor.GRAY + "   - Show trail selection screen..");
                icon.addLore("");
                icon.addLore(ChatColor.RED + " Right Click:");
                icon.addLore(ChatColor.GRAY + "   - Toggle Enabled/Disabled.");
                menu.addIcon(icon);
                icon.getHandler().update();
            }

            if (player.hasPermission("myuniversal.toggle.particles")) {
                Icon icon = new Icon(1, 0, ChatColor.AQUA + " Particle Effects", new ItemStack(Material.WOOD_SWORD)).setHandler(new IconHandler() {
                    @Override
                    public void onClick(IconClickEvent event) {
                        super.onClick(event);

                        event.getPlayer().sendMessage(ChatColor.RED + "This feature is coming soon!");
                    }

                    @Override
                    public void update() {
                        super.update();
                    }
                });
                icon.addLore(ChatColor.GRAY + "  * Spawn custom particle effects.");
                menu.addIcon(icon);
            }

            if (player.hasPermission("myuniversal.toggle.vsafety")) {
                Icon icon = new Icon(2, 0, ChatColor.AQUA + " Vanish Trail Safety", new ItemStack(Material.WOOL, 1, DyeColor.LIME.getData())).setHandler(new IconHandler() {
                    @Override
                    public void onClick(IconClickEvent event) {
                        super.onClick(event);

                        Universal.getPlayerStats().set(getIcon().getParent().getOwnerName(), "vs", !(Universal.getPlayerStats().getBoolean(getIcon().getParent().getOwnerName(), "vs", true)));

                        event.setCloseInventory(false);
                        update();
                    }

                    @Override
                    public void update() {
                        super.update();

                        if (Universal.getPlayerStats().getBoolean(getIcon().getParent().getOwnerName(), "vs", true)) {
                            getIcon().setItemStack(new ItemStack(Material.WOOL, 1, DyeColor.LIME.getData()));
                            getIcon().setName(ChatColor.AQUA + " Vanish Trail Safety " + ChatColor.GREEN + ChatColor.BOLD + "(ENABLED)");
                        } else {
                            getIcon().setItemStack(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()));
                            getIcon().setName(ChatColor.AQUA + " Vanish Trail Safety " + ChatColor.RED + ChatColor.BOLD + "(DISABLED)");
                        }

                        getIcon().getParent().updateIcon(getIcon());
                    }
                });
                icon.addLore("");
                icon.addLore(ChatColor.GREEN + " Enabled:");
                icon.addLore(ChatColor.GRAY + "   - Do not show trail while vanished.");
                icon.addLore("");
                icon.addLore(ChatColor.RED + " Disabled:");
                icon.addLore(ChatColor.GRAY + "   - Show trail while vanished (trolol)");
                menu.addIcon(icon);
                icon.getHandler().update();
            }

            if (player.hasPermission("myuniversal.toggle.stacker")) {
                Icon icon = new Icon(3, 0, ChatColor.AQUA + " Stacker", new ItemStack(Material.SADDLE)).setHandler(new IconHandler() {
                    @Override
                    public void onClick(IconClickEvent event) {
                        super.onClick(event);

                        Universal.getPlayerStats().set(getIcon().getParent().getOwnerName(), "stacker", !(Universal.getPlayerStats().getBoolean(getIcon().getParent().getOwnerName(), "stacker", true)));

                        event.setCloseInventory(false);
                        update();
                    }

                    @Override
                    public void update() {
                        super.update();

                        if (Universal.getPlayerStats().getBoolean(getIcon().getParent().getOwnerName(), "stacker", true)) {
                            ItemStack newStack = getIcon().getItemStack();
                            newStack.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 42);

                            getIcon().setItemStack(newStack);
                            getIcon().setName(ChatColor.AQUA + " Stacker " + ChatColor.GREEN + ChatColor.BOLD + "(ENABLED)");
                        } else {
                            ItemStack newStack = getIcon().getItemStack();
                            newStack.removeEnchantment(Enchantment.SILK_TOUCH);

                            getIcon().setItemStack(newStack);
                            getIcon().setName(ChatColor.AQUA + " Stacker " + ChatColor.RED + ChatColor.BOLD + "(DISABLED)");
                        }

                        getIcon().getParent().updateIcon(getIcon());
                    }
                });
                icon.addLore("");
                icon.addLore(ChatColor.GREEN + " Enabled:");
                icon.addLore(ChatColor.GRAY + "   - Join in on the stacking fun.");
                icon.addLore("");
                icon.addLore(ChatColor.RED + " Disabled:");
                icon.addLore(ChatColor.GRAY + "   - Do not allow stacking.");
                menu.addIcon(icon);
                icon.getHandler().update();
            }

            if (player.hasPermission("myuniversal.toggle.announcer")) {
                Icon icon = new Icon(4, 0, ChatColor.AQUA + " Announcer", new ItemStack(Material.BOOK)).setHandler(new IconHandler() {
                    @Override
                    public void onClick(IconClickEvent event) {
                        super.onClick(event);

                        Universal.getPlayerStats().set(getIcon().getParent().getOwnerName(), "announcer.visible", !(Universal.getPlayerStats().getBoolean(getIcon().getParent().getOwnerName(), "announcer.visible", true)));

                        if (Universal.getPlayerStats().getBoolean(getIcon().getParent().getOwnerName(), "announcer.visible", true)) {
                        } else {
                            //TODO: This
                            //BarManager.removeBar(event.getPlayer());
                        }

                        event.setCloseInventory(false);
                        update();
                    }

                    @Override
                    public void update() {
                        super.update();

                        if (Universal.getPlayerStats().getBoolean(getIcon().getParent().getOwnerName(), "announcer.visible", true)) {
                            ItemStack newStack = getIcon().getItemStack();
                            newStack.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 42);

                            getIcon().setItemStack(newStack);
                            getIcon().setName(ChatColor.AQUA + " Announcer " + ChatColor.GREEN + ChatColor.BOLD + "(ENABLED)");
                        } else {
                            ItemStack newStack = getIcon().getItemStack();
                            newStack.removeEnchantment(Enchantment.SILK_TOUCH);

                            getIcon().setItemStack(newStack);
                            getIcon().setName(ChatColor.AQUA + " Announcer " + ChatColor.RED + ChatColor.BOLD + "(DISABLED)");
                        }

                        getIcon().getParent().updateIcon(getIcon());
                    }
                });
                icon.addLore("");
                icon.addLore(ChatColor.GREEN + " Enabled:");
                icon.addLore(ChatColor.GRAY + "   - View announcements.");
                icon.addLore(ChatColor.GRAY + "        At the top of your screen.");
                icon.addLore("");
                icon.addLore(ChatColor.RED + " Disabled:");
                icon.addLore(ChatColor.GRAY + "   - Disable announcements.");
                icon.addLore("");
                icon.addLore(ChatColor.YELLOW + "" + ChatColor.BOLD + " Click " + ChatColor.RESET + ChatColor.WHITE + "To Toggle Status.");
                menu.addIcon(icon);
                icon.getHandler().update();
            }

            menu.open();
            return true;
        }

        if (args[0].equalsIgnoreCase("trail")) {
            if (!player.hasPermission("toggle." + args[0])) {
                player.sendMessage(ChatColor.RED + "Error: You do not have permission to do this.");
                return true;
            }

            getTrailMenu(player).open();
        }

        return true;
    }

    public Menu getTrailMenu(final Player player) {
        Menu menu = new Menu(player, "Choose Your Trail!", 54).setHandler(new MenuHandler() {
            @Override
            public void onBlankClick(EmptyClickEvent e) {
                super.onBlankClick(e);

                if (e.getOriginalEvent().getSlotType() == InventoryType.SlotType.OUTSIDE) {
                    Helper.safeInventoryClose(player);
                }
            }
        });

        menu.setCloseOnInteract(true);

        Icon icon = new Icon(8, 0, ChatColor.AQUA + " Trail", new ItemStack(Material.WOOL, 1, DyeColor.RED.getData())).setHandler(new IconHandler() {
            @Override
            public void onClick(IconClickEvent event) {
                super.onClick(event);

                Universal.getPlayerStats().set(getIcon().getParent().getOwnerName(), "trail.enabled", !(Universal.getPlayerStats().getBoolean(getIcon().getParent().getOwnerName(), "trail.enabled", false)));

                event.setCloseInventory(false);
                update();

                getIcon().getParent().updateOthers(getIcon());
            }

            @Override
            public void update() {
                super.update();

                if (Universal.getPlayerStats().getBoolean(getIcon().getParent().getOwnerName(), "trail.enabled", false)) {
                    getIcon().setItemStack(new ItemStack(Material.WOOL, 1, DyeColor.LIME.getData()));
                    getIcon().setName(ChatColor.AQUA + " Trail Status " + ChatColor.GREEN + ChatColor.BOLD + "(ENABLED)");

                    Universal.trails.put(getIcon().getOwner().getUniqueId(), ParticleType.fromName(Universal.getPlayerStats().getString(getIcon().getOwner().getName(), "trail.type", "hearts")));
                } else {
                    getIcon().setItemStack(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()));
                    getIcon().setName(ChatColor.AQUA + " Trail Status " + ChatColor.RED + ChatColor.BOLD + "(DISABLED)");

                    if (Universal.trails.containsKey(getIcon().getOwner().getUniqueId())) {
                        Universal.trails.remove(getIcon().getOwner().getUniqueId());
                    }
                }
            }
        });
        menu.addIcon(icon);

        icon = new Icon(0, 0, ChatColor.GREEN + "Return to Options", new ItemStack(Material.CHEST, 1)).setHandler(new IconHandler() {
            @Override
            public void onClick(IconClickEvent event) {
                super.onClick(event);

                Menu next = (Menu) getIcon().getParent().getMeta("previous");

                if (next != null) {
                    event.setNewMenu(next);
                }
            }

            @Override
            public void update() {
                super.update();

                Menu next = (Menu) getIcon().getParent().getMeta("previous");

                if (next != null) {
                    if (!getIcon().isVisible()) {
                        getIcon().setVisible(true);

                        getIcon().getParent().updateIcon(getIcon());
                    }
                } else {
                    if (getIcon().isVisible()) {
                        getIcon().setVisible(false);

                        getIcon().getParent().updateIcon(getIcon());
                    }
                }
            }
        });

        menu.addIcon(icon);
        icon.setVisible(false);

        for (int i = 0; i < ParticleType.values().length; i++) {
            ParticleType type = ParticleType.values()[i];

            ItemStack show = type.getItemStack().clone();

            if (Universal.trails.containsKey(player.getUniqueId())) {
                if (type == Universal.trails.get(player.getUniqueId())) {
                    show.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 42);
                }
            }

            icon = new Icon(i + 18, ChatColor.GREEN + type.getDisplayName(), show).setHandler(new IconHandler() {
                @Override
                public void onClick(IconClickEvent event) {
                    super.onClick(event);

                    event.setCloseInventory(false);
                    Universal.getPlayerStats().set(player.getName(), "trail.type", getIcon().getMetaString("id"));
                    Universal.trails.put(player.getUniqueId(), ParticleType.fromName(getIcon().getMetaString("id")));

                    getIcon().getParent().updateAllIcons();
                }

                @Override
                public void update() {
                    super.update();

                    if (Universal.trails.containsKey(player.getUniqueId())) {
                        if (getIcon().getMeta("type") == Universal.trails.get(player.getUniqueId())) {
                            ItemStack newStack = getIcon().getItemStack();
                            newStack.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 42);

                            getIcon().setItemStack(newStack);
                        } else {
                            ItemStack newStack = getIcon().getItemStack();
                            newStack.removeEnchantment(Enchantment.SILK_TOUCH);

                            getIcon().setItemStack(newStack);
                        }
                    } else {
                        ItemStack newStack = getIcon().getItemStack();
                        newStack.removeEnchantment(Enchantment.SILK_TOUCH);

                        getIcon().setItemStack(newStack);
                    }
                }
            });

            icon.addLore("");
            icon.addLore(ChatColor.GRAY + "  ID: " + ChatColor.GOLD + i);

            icon.setMeta("id", type.getName());
            icon.setMeta("type", type);
            menu.addIcon(icon);
        }

        return menu;
    }

    public Menu getPowerToolMenu(final Player player, final ParticleType type) {
        Menu menu = new Menu(player, "Choose Your Trail!", 54).setHandler(new MenuHandler() {
            @Override
            public void onBlankClick(EmptyClickEvent e) {
                super.onBlankClick(e);

                if (e.getOriginalEvent().getSlotType() == InventoryType.SlotType.OUTSIDE) {
                    Helper.safeInventoryClose(player);
                }
            }
        });

        Icon icon = new Icon(8, 0, ChatColor.RED + "Remove Trail", new ItemStack(Material.WOOL, 1, DyeColor.RED.getData())).setHandler(new IconHandler() {
            @Override
            public void onClick(IconClickEvent event) {
                super.onClick(event);

                event.setCloseInventory(false);
                Universal.getPlayerStats().set(player.getName(), "trail", -1);

                if (Universal.trails.containsKey(player.getUniqueId())) {
                    Universal.trails.remove(player.getUniqueId());
                }

                getIcon().getParent().updateAllIcons();
            }
        });

        menu.addIcon(icon);

        menu.setCloseOnInteract(true);
        return menu;
    }


    // Simply check for all required permissions

    public boolean shouldCancel(CommandSender sender, Command command) {
        if (requireConsole && requirePlayer) {
            requireConsole = false;
            requirePlayer = false;
        }

        if (sender instanceof Player) {
            if (requireConsole) {
                sender.sendMessage(ChatColor.RED + "You must be the console to do this.");
                return true;
            } else {
                if (requirePermission) {
                    String requiredPermission = (Settings.basePerms + ((overrideCommand.equalsIgnoreCase("")) ? command.getName() : overrideCommand)).toLowerCase();

                    if (!sender.hasPermission(requiredPermission)) {
                        sender.sendMessage(ChatColor.RED + "Error: You need permission '" + requiredPermission + "' to do this.");
                        return true;
                    }
                }
            }
        } else {
            if (requirePlayer) {
                sender.sendMessage(ChatColor.RED + "You must be in game to do this.");
                return true;
            }
        }

        return false;
    }
}
