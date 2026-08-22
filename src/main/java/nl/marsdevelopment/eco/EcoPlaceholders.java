package nl.marsdevelopment.eco;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class EcoPlaceholders extends PlaceholderExpansion {
    private final EcoPlugin plugin;
    private final BalanceStore balances;

    EcoPlaceholders(EcoPlugin plugin, BalanceStore balances) {
        this.plugin = plugin;
        this.balances = balances;
    }

    @Override public @NotNull String getIdentifier() { return "eco"; }
    @Override public @NotNull String getAuthor() { return "Mars Development"; }
    @Override public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";
        double balance = balances.get(player.getUniqueId());
        return switch (params.toLowerCase()) {
            case "balance" -> Double.toString(balance);
            case "balance_formatted" -> plugin.format(balance);
            default -> null;
        };
    }
}
