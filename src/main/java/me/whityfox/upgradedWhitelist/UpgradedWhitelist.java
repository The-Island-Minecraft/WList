package me.whityfox.upgradedWhitelist;
import me.whityfox.upgradedWhitelist.database.WhiteListDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import java.sql.SQLException;


public final class UpgradedWhitelist extends JavaPlugin implements Listener  {
    private WhiteListDatabase wldatabase;

    @Override
    public void onEnable() {
        // Register the plugin's event listener

        if (!getDataFolder().exists()){
            getDataFolder().mkdirs();
        }
        try {
            wldatabase = new WhiteListDatabase(getDataFolder().getAbsolutePath() + "/whitylist.db");

        } catch (SQLException ex){
            ex.printStackTrace();
            System.out.println("Failed to fuck Ruslan | SQLite Connection Exception");
            Bukkit.getPluginManager().disablePlugin(this);

        }
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("wlist").setExecutor(this);


    }
    @EventHandler
    public void OnPlayerJoin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        try{
            boolean wlsted = wldatabase.isWhitelisted(player.getName());
            if (!wlsted) {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST,"You're not whitelisted. | Вы не в вайтлисте.");
            } else{
                event.allow();
            }
        } catch (SQLException ex ){
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("wlist")) {
            if (args.length < 1) {
                sender.sendMessage("/wlist <add|remove|list> [player]");
                return true;
            }
            if (!sender.isOp() && !(sender instanceof ConsoleCommandSender)){
                sender.sendMessage("You have no rights to do this!");
                return false;
            }
            String action = args[0];
            String playerName = null;
            try {
                playerName = args[1];
            } catch (Exception exception){
                //pass
            }


            if (action.equalsIgnoreCase("add")) {
                if (playerName == null){
                    sender.sendMessage("/wlist <add|remove|list> [player]");
                    return false;
                }
                try {
                    wldatabase.AddPlayerToWhileList(playerName);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                sender.sendMessage("Added " + playerName + " to the whitelist.");
            } else if (action.equalsIgnoreCase("remove")) {
                if (playerName == null){
                    sender.sendMessage("/wlist <add|remove|list> [player]");
                    return false;
                }
                try {
                    wldatabase.RemoveFromWhiteList(playerName);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                sender.sendMessage("Removed " + playerName + " from the whitelist.");
            } else if (action.equalsIgnoreCase("list")) {
                List<String> whitelistedPlayers = wldatabase.getWhitelistedPlayers();
                sender.sendMessage(ChatColor.GREEN + "=== Список игроков в вайтлисте ===");
                if (whitelistedPlayers.isEmpty()) {
                    sender.sendMessage(ChatColor.YELLOW + "Список пуст");
                } else {
                    // Соединяем все имена в одну строку через запятую
                    StringBuilder playerList = new StringBuilder();
                    for (int i = 0; i < whitelistedPlayers.size(); i++) {
                        playerList.append(ChatColor.YELLOW).append(whitelistedPlayers.get(i));
                        // Добавляем запятую и пробел после каждого имени, кроме последнего
                        if (i < whitelistedPlayers.size() - 1) {
                            playerList.append(ChatColor.WHITE).append(", ");
                        }
                    }
                    sender.sendMessage(playerList.toString());
                }
                sender.sendMessage(ChatColor.GREEN + "Всего игроков: " + whitelistedPlayers.size());

            } else {
                sender.sendMessage("/wlist <add|remove|list> [player]");
            } 
            return true;
        }
        return false;
    }
    public void onDisable(){
        try {
            wldatabase.closeConnection();
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }
}