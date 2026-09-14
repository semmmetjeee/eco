package nl.marsdevelopment.eco;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;

public final class EcoEconomy extends AbstractEconomy {
    private final EcoPlugin plugin; private final BalanceStore balances;
    EcoEconomy(EcoPlugin plugin, BalanceStore balances) { this.plugin = plugin; this.balances = balances; }
    @Override public boolean isEnabled() { return plugin.isEnabled(); }
    @Override public String getName() { return "Eco"; }
    @Override public boolean hasBankSupport() { return false; }
    @Override public List<String> getBanks() { return List.of(); }
    @Override public int fractionalDigits() { return 2; }
    @Override public String format(double amount) { return plugin.format(amount); }
    @Override public String currencyNamePlural() { return "coins"; }
    @Override public String currencyNameSingular() { return "coin"; }

    @Override public boolean hasAccount(String playerName) { return true; }
    @Override public boolean hasAccount(String playerName, String worldName) { return true; }
    @Override public double getBalance(String playerName) { return getBalance(org.bukkit.Bukkit.getOfflinePlayer(playerName)); }
    @Override public double getBalance(String playerName, String worldName) { return getBalance(playerName); }
    @Override public boolean has(String playerName, double amount) { return getBalance(playerName) >= amount; }
    @Override public boolean has(String playerName, String worldName, double amount) { return has(playerName, amount); }
    @Override public EconomyResponse withdrawPlayer(String playerName, double amount) { return withdrawPlayer(org.bukkit.Bukkit.getOfflinePlayer(playerName), amount); }
    @Override public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) { return withdrawPlayer(playerName, amount); }
    @Override public EconomyResponse depositPlayer(String playerName, double amount) { return depositPlayer(org.bukkit.Bukkit.getOfflinePlayer(playerName), amount); }
    @Override public EconomyResponse depositPlayer(String playerName, String worldName, double amount) { return depositPlayer(playerName, amount); }

    @Override public EconomyResponse createBank(String name, String player) { return bankUnsupported(); }
    @Override public EconomyResponse deleteBank(String name) { return bankUnsupported(); }
    @Override public EconomyResponse bankBalance(String name) { return bankUnsupported(); }
    @Override public EconomyResponse bankHas(String name, double amount) { return bankUnsupported(); }
    @Override public EconomyResponse bankWithdraw(String name, double amount) { return bankUnsupported(); }
    @Override public EconomyResponse bankDeposit(String name, double amount) { return bankUnsupported(); }
    @Override public EconomyResponse isBankOwner(String name, String playerName) { return bankUnsupported(); }
    @Override public EconomyResponse isBankMember(String name, String playerName) { return bankUnsupported(); }
    private EconomyResponse bankUnsupported() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }
    @Override public boolean hasAccount(OfflinePlayer player) { return true; }
    @Override public boolean hasAccount(OfflinePlayer player, String world) { return true; }
    @Override public double getBalance(OfflinePlayer player) { return balances.get(player.getUniqueId()); }
    @Override public double getBalance(OfflinePlayer player, String world) { return getBalance(player); }
    @Override public boolean has(OfflinePlayer player, double amount) { return getBalance(player) >= amount; }
    @Override public boolean has(OfflinePlayer player, String world, double amount) { return has(player, amount); }
    @Override public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) { return change(player, -amount); }
    @Override public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) { return withdrawPlayer(player, amount); }
    @Override public EconomyResponse depositPlayer(OfflinePlayer player, double amount) { return change(player, amount); }
    @Override public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) { return depositPlayer(player, amount); }
    private EconomyResponse change(OfflinePlayer player, double delta) {
        if (Double.isNaN(delta) || Double.isInfinite(delta) || delta < 0 && getBalance(player) < -delta)
            return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Insufficient funds or invalid amount");
        balances.set(player.getUniqueId(), getBalance(player) + delta);
        return new EconomyResponse(delta, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
    }
    @Override public boolean createPlayerAccount(OfflinePlayer player) { return true; }
    @Override public boolean createPlayerAccount(OfflinePlayer player, String world) { return true; }
    @Override public boolean createPlayerAccount(String playerName) { return true; }
    @Override public boolean createPlayerAccount(String playerName, String worldName) { return true; }
}
