package nl.marsdevelopment.eco;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class EcoPlugin extends JavaPlugin {
    private BalanceStore balances;
    private Command economyCommand;

    @Override public void onEnable() {
        saveDefaultConfig();
        balances = new BalanceStore(this);
        getServer().getServicesManager().register(Economy.class, new EcoEconomy(this, balances), this, ServicePriority.Normal);
        registerEconomyCommand();
        getCommand("ecoreload").setExecutor((sender, command, label, args) -> {
            if (!sender.hasPermission("eco.admin")) { sender.sendMessage(message("no-permission")); return true; }
            reloadConfig();
            registerEconomyCommand();
            sender.sendMessage(message("reload"));
            return true;
        });
    }

    private void registerEconomyCommand() {
        if (economyCommand != null) economyCommand.unregister(Bukkit.getCommandMap());
        String name = getConfig().getString("command", "economy").toLowerCase();
        String aliasesValue = getConfig().getString("aliases", "eco");
        economyCommand = new EcoCommand(this, balances, name, aliasesValue.isBlank() ? java.util.List.of() : java.util.Arrays.stream(aliasesValue.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
        Bukkit.getCommandMap().register(getDescription().getName().toLowerCase(), economyCommand);
    }

    String message(String key) { return org.bukkit.ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages." + key, "&cMissing message: " + key)); }
    @Override public void onDisable() {
        if (economyCommand != null) economyCommand.unregister(Bukkit.getCommandMap());
        balances.save();
    }
}
