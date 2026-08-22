package nl.marsdevelopment.eco;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.UUID;

final class BalanceStore {
    private final JavaPlugin plugin;
    BalanceStore(JavaPlugin plugin) { this.plugin = plugin; }
    double get(UUID id) { return plugin.getConfig().getDouble("balances." + id, 0D); }
    void set(UUID id, double amount) { plugin.getConfig().set("balances." + id, Math.max(0D, amount)); }
    void save() { plugin.saveConfig(); }
}
