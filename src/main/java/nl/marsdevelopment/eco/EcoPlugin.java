package nl.marsdevelopment.eco;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class EcoPlugin extends JavaPlugin {
    private BalanceStore balances;
    @Override public void onEnable() {
        saveDefaultConfig();
        balances = new BalanceStore(this);
        getServer().getServicesManager().register(Economy.class, new EcoEconomy(this, balances), this, ServicePriority.Normal);
        EcoCommand command = new EcoCommand(balances);
        getCommand("eco").setExecutor(command);
        getCommand("eco").setTabCompleter(command);
    }
    @Override public void onDisable() { balances.save(); }
}
