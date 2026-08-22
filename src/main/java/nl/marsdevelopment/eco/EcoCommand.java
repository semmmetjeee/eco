package nl.marsdevelopment.eco;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import java.util.*;

final class EcoCommand implements CommandExecutor, TabCompleter {
    private final BalanceStore balances;
    EcoCommand(BalanceStore balances) { this.balances = balances; }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("balance")) {
            if (!sender.hasPermission("eco.balance")) { sender.sendMessage("§cNo permission."); return true; }
            OfflinePlayer target = args.length > 1 ? Bukkit.getOfflinePlayer(args[1]) : sender instanceof OfflinePlayer p ? p : null;
            if (target == null) { sender.sendMessage("§cUsage: /eco balance <player>"); return true; }
            sender.sendMessage("§a" + target.getName() + " has §e" + String.format("%.2f", balances.get(target.getUniqueId())) + "§a coins."); return true;
        }
        if (!sender.hasPermission("eco.admin")) { sender.sendMessage("§cNo permission."); return true; }
        if (args.length != 3 || !Set.of("give", "set", "take").contains(args[0].toLowerCase(Locale.ROOT))) { sender.sendMessage("§cUsage: /eco <give|set|take> <player> <amount>"); return true; }
        double amount; try { amount = Double.parseDouble(args[2]); } catch (NumberFormatException e) { sender.sendMessage("§cAmount must be a number."); return true; }
        if (amount < 0 || !Double.isFinite(amount)) { sender.sendMessage("§cAmount must be positive and finite."); return true; }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]); double before = balances.get(target.getUniqueId());
        double after = switch (args[0].toLowerCase(Locale.ROOT)) { case "give" -> before + amount; case "take" -> Math.max(0, before - amount); default -> amount; };
        balances.set(target.getUniqueId(), after); balances.save(); sender.sendMessage("§aSet " + target.getName() + "'s balance to §e" + String.format("%.2f", after) + "§a coins."); return true;
    }
    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return prefix(args[0], List.of("balance", "give", "set", "take"));
        if (args.length == 2) return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        return List.of();
    }
    private List<String> prefix(String value, List<String> choices) { return choices.stream().filter(s -> s.startsWith(value.toLowerCase())).toList(); }
}
