package nl.marsdevelopment.eco;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.*;

final class EcoCommand extends Command {
    private final EcoPlugin plugin;
    private final BalanceStore balances;

    EcoCommand(EcoPlugin plugin, BalanceStore balances, String name, List<String> aliases) {
        super(name, "Manage player balances.", "/" + name + " [balance|give|set|take|reset] [player] [amount]", aliases);
        this.plugin = plugin; this.balances = balances;
    }

    @Override public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("balance")) {
            if (!sender.hasPermission("eco.balance")) { sender.sendMessage(plugin.message("no-permission")); return true; }
            OfflinePlayer target = args.length > 1 ? Bukkit.getOfflinePlayer(args[1]) : sender instanceof OfflinePlayer p ? p : null;
            if (target == null) { sender.sendMessage(usage(label)); return true; }
            sender.sendMessage(format("balance", target, balances.get(target.getUniqueId())));
            return true;
        }
        if (!sender.hasPermission("eco.admin")) { sender.sendMessage(plugin.message("no-permission")); return true; }
        if (args.length < 2 || !Set.of("give", "set", "take", "reset").contains(args[0].toLowerCase(Locale.ROOT))) { sender.sendMessage(usage(label)); return true; }

        String action = args[0].toLowerCase(Locale.ROOT);
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        double amount = 0;
        if (!action.equals("reset")) {
            if (args.length != 3) { sender.sendMessage(usage(label)); return true; }
            try { amount = Double.parseDouble(args[2]); } catch (NumberFormatException e) { sender.sendMessage(plugin.message("invalid-amount")); return true; }
            if (amount < 0 || !Double.isFinite(amount)) { sender.sendMessage(plugin.message("invalid-amount")); return true; }
        }
        double before = balances.get(target.getUniqueId());
        double after = switch (action) {
            case "give" -> before + amount;
            case "take" -> Math.max(0, before - amount);
            case "set" -> amount;
            default -> 0;
        };
        balances.set(target.getUniqueId(), after);
        balances.save();
        sender.sendMessage(format("admin-" + action, target, amount));
        if (target.isOnline()) target.getPlayer().sendMessage(format("player-" + action, target, amount));
        return true;
    }

    @Override public List<String> tabComplete(CommandSender sender, String alias, String[] args, org.bukkit.Location location) {
        if (args.length == 1) return prefix(args[0], List.of("balance", "give", "set", "take", "reset"));
        if (args.length == 2) return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        return List.of();
    }

    private String usage(String label) { return plugin.message("usage").replace("{command}", label); }
    private String format(String message, OfflinePlayer player, double amount) {
        return plugin.message(message).replace("{player}", player.getName() == null ? "Unknown" : player.getName()).replace("{amount}", plugin.format(amount)).replace("{balance}", plugin.format(balances.get(player.getUniqueId())));
    }
    private List<String> prefix(String input, List<String> values) { return values.stream().filter(value -> value.startsWith(input.toLowerCase())).toList(); }
}
