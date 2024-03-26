package fr.pierronus.pvpl;
import java.sql.SQLException;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener, CommandExecutor{

		public static Economy econ;
		public MySQL SQL;
		public SQLGetter data;
		
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				
		@Override
		public void onEnable() {
			this.SQL = new MySQL();
			this.data = new SQLGetter(this);
			
			try {
				SQL.connect();
				
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				Bukkit.getLogger().info("BDD non connectee");
			}
			if(SQL.isConnected()) {
				Bukkit.getLogger().info("BDD connectee");
				data.createTable();
			}
		    
			if (!setupEconomy()) {
	            this.getLogger().severe("Disabled due to no Vault dependency found!");
	            Bukkit.getPluginManager().disablePlugin(this);
	            return;
	        }
			getServer().getPluginManager().registerEvents(this, this);
			System.out.println("Le plugin GCA s'est bien active");
			
		}
		private boolean setupEconomy() {
	        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
	            return false;
	        }

	        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	        if (rsp == null) {
	            return false;
	        }
	        econ = rsp.getProvider();
	        return econ != null;
	    }
		
		@Override
		public void onDisable() {
			System.out.println("Le plugin GCA s'est bien desactive");
			SQL.disconnect();
		}
		
		@EventHandler
		public void onJoin(PlayerJoinEvent event) {
			Player player = event.getPlayer();
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			String command = "title " + player.getName() + " title {\"text\":\"Bienvenue sur le PvpBox\"}";
			Bukkit.dispatchCommand(console, command);
			data.createPlayer(player);
			this.setScoreBoard(player);
			Location spawn = new Location(player.getWorld(), 126.527, 26, -114.510, 0.3f, 1.3f);
			player.teleport(spawn);
			for(Player p: Bukkit.getServer().getOnlinePlayers()) {
				this.setScoreBoard(p);
			}
			
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

	            @Override
	            public void run() {
	            	setScoreBoard(player);
	                }
	               
	            }
	           
	        , 0 * 20, 3 * 20);
			
			Bukkit.broadcastMessage("§a§l+ §7" + player.getName());
		}
		
		@EventHandler
		public void onQuit(PlayerQuitEvent event) {
		    Player player = event.getPlayer();
		    Bukkit.broadcastMessage("§c§l- §7" + player.getName());
		    data.resetKS(player.getUniqueId());
		}
		@EventHandler
		public void onDeath(PlayerDeathEvent event) {
			event.setDeathMessage(null);
			final Player player = event.getEntity();
		    final Player killer = player.getKiller();
		    data.addKills(killer.getUniqueId());
		    data.addMorts(player.getUniqueId());
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			String command = "eco give " + killer.getName() + " 10";
			Bukkit.dispatchCommand(console, command);
		    this.setScoreBoard(player);
		    this.setScoreBoard(killer);
		    killer.sendMessage("§cVous avez tué §c§l" + killer.getName() + " §cet vous gagnez §a§l10$");
		    player.sendMessage("§cVous avez été tué par §c§l" + killer.getName());
		    Random rand = new Random();
    		int rdmint = rand.nextInt(5);
    		switch (rdmint) {
    		case 0: Bukkit.broadcastMessage("§c§l " + killer.getName() + " §ca pété le cube de §c§l" + player.getName());
    		case 1: Bukkit.broadcastMessage("§c§l " + killer.getName() + " §ca cassé le bloc de §c§l" + player.getName());
    		case 2: Bukkit.broadcastMessage("§c§l " + killer.getName() + " §ca cassé le bloc de §c§l" + player.getName());
    		case 3: Bukkit.broadcastMessage("§c§l " + killer.getName() + " §ca cassé le bloc de §c§l" + player.getName());
    		case 4: Bukkit.broadcastMessage("§c§l " + killer.getName() + " §ca cassé le bloc de §c§l" + player.getName());;
    		case 5: Bukkit.broadcastMessage("§c§l " + killer.getName() + " §ca cassé le bloc de §c§l" + player.getName());
    		}
		    data.resetKS(player.getUniqueId());
		    data.addKS(killer.getUniqueId());
		    int ks = data.getKS(killer.getUniqueId());
		    if(ks > 3) {
		    	Bukkit.broadcastMessage("§6§l " + killer.getName() + " §6est sur un killstreak de §b§l " + String.valueOf(ks) + " §6kills!");
		    }
		    
		}
		
		@EventHandler
		public void onRespawn(PlayerRespawnEvent event) {
			Player player = event.getPlayer();
			Location spawn = new Location(player.getWorld(), 126.527, 26, -114.510, 0.3f, 1.3f);
			event.setRespawnLocation(spawn);
			this.setScoreBoard(player);
		}
		
		public void setScoreBoard(Player player) {
			
			int kills = data.getKills(player.getUniqueId());
			int morts;
			double kdra;
			morts = data.getMorts(player.getUniqueId());
			if(morts == 0) {
				kdra = kills / (morts + 1);
			} else {
				kdra = kills / morts;
			}
			double kdr = Math.round(kdra * 100.0) / 100.0;
            Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective obj = board.registerNewObjective("§8§lPVPBOX", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.getScore("").setScore(15);
            Score money = obj.getScore(ChatColor.GRAY + "» Argent : ");
            money.setScore(14);
            Score moneyy = obj.getScore("§a" + econ.getBalance(player) + "$");
            moneyy.setScore(13);
            
            
            obj.getScore(ChatColor.BLACK + "" + ChatColor.WHITE).setScore(12);
            Score statis = obj.getScore(ChatColor.GRAY + "» Statistiques :");
            statis.setScore(11);
            obj.getScore("§aKills : " + String.valueOf(kills)).setScore(10);
            obj.getScore("§cMorts : " + String.valueOf(morts)).setScore(9);
            obj.getScore("§6Ratio : " + String.valueOf(kdr)).setScore(8);
            
            Score space = obj.getScore("");
            space.setScore(7);
            
            Score spadce = obj.getScore("§7§nplay.ordcraft.ovh");
            spadce.setScore(6);
            
            player.setScoreboard(board);
      }
		
		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if(label.equalsIgnoreCase("points")) {
				if(args.length == 0) {
				if(sender instanceof Player) {
					Player player = (Player) sender;
				if(player.hasPermission("ord.points")) {
					player.sendMessage("§6§lVos OrdCoins : §6" + String.valueOf(data.getPoints(player.getUniqueId())) + " OrdCoins");
					}
				}
				}
				if(args.length == 1) {
					if(sender instanceof Player) {
						Player player = (Player) sender;
					if(player.hasPermission("ord.points.other")) {
						String targetname = args[0];
						if(Bukkit.getPlayer(targetname) != null) {
							if(targetname != player.getName()) {
							Player target = Bukkit.getPlayer(targetname);
							player.sendMessage("§6§lOrdCoins de " + target.getName() + " : §6" + String.valueOf(data.getPoints(target.getUniqueId())) + " OrdCoins");
							
						}
						}
						else {
							player.sendMessage("§6§l[GCA] §f§cCe joueur n'existe pas");
						}
					
					}
				}else {
					
					String targetname = args[0];
					if(Bukkit.getPlayer(targetname) != null) {
						Player target = Bukkit.getPlayer(targetname);
						String pts = String.valueOf(data.getPoints(target.getUniqueId()));
						System.out.println(target.getName() + " a " + pts + " OrdCoins");
						
					
					}
					else {
						System.out.println("Ce joueur n'existe pas (" + targetname + ")");
					}
					
					
				}
				}
				
				return false;
				}
				if(label.equalsIgnoreCase("addpoints")) {
					if(args.length == 2) {
						if(sender instanceof Player) {
							Player player = (Player) sender;
						if(player.getName().equals("Scrym6") || player.getName().equals("Pierronus")) {
							String targetname = args[0];
							int points = Integer.valueOf(args[1]);
							if(Bukkit.getPlayer(targetname) != null) {
								if(targetname != player.getName()) {
								Player target = Bukkit.getPlayer(targetname);
								data.addPoints(target.getUniqueId(), points);
								String nowpts = String.valueOf(data.getPoints(target.getUniqueId())); 
								player.sendMessage("§cAjouté " + points + " OrdCoins à " + target.getName() + "\n§cIl a maintenant " + nowpts);
								
							}
							}
							else {
								player.sendMessage("§6§l[GCA] §f§cCe joueur n'existe pas");
							}
						}
					}else {
						String targetname = args[0];
						int points = Integer.valueOf(args[1]);
						if(Bukkit.getPlayer(targetname) != null) {
							Player target = Bukkit.getPlayer(targetname);
							data.addPoints(target.getUniqueId(), points);
							String nowpts = String.valueOf(data.getPoints(target.getUniqueId())); 
							System.out.println("Ajouté " + points + " OrdCoins à " + target.getName() + ". Il a maintenant " + nowpts);
							
						
						}
						else {
							System.out.println("Ce joueur n'existe pas (" + targetname + ")");
						}
						
						
					}
					}
			}
				if(label.equalsIgnoreCase("removepoints")) {
					if(args.length == 2) {
						if(sender instanceof Player) {
							Player player = (Player) sender;
						if(player.getName().equals("Scrym6") || player.getName().equals("Pierronus")) { 
							String targetname = args[0];
							int points = Integer.valueOf(args[1]);
							if(Bukkit.getPlayer(targetname) != null) {
								if(targetname != player.getName()) {
								Player target = Bukkit.getPlayer(targetname);
								data.removePoints(target.getUniqueId(), points);
								String nowpts = String.valueOf(data.getPoints(target.getUniqueId())); 
								player.sendMessage("§cRetiré " + points + " OrdCoins à " + target.getName() + "\n§cIl a maintenant " + nowpts);
								
							}
							}
							else {
								player.sendMessage("§6§l[GCA] §f§cCe joueur n'existe pas");
							}
						}
					}else {
						String targetname = args[0];
						int points = Integer.valueOf(args[1]);
						if(Bukkit.getPlayer(targetname) != null) {
							Player target = Bukkit.getPlayer(targetname);
							data.removePoints(target.getUniqueId(), points);
							String nowpts = String.valueOf(data.getPoints(target.getUniqueId())); 
							System.out.println("Retiré " + points + " OrdCoins à " + target.getName() + ". Il a maintenant " + nowpts);
							
						
						}
						else {
							System.out.println("Ce joueur n'existe pas (" + targetname + ")");
						}
						
						
					}
					}
			}
				if(label.equalsIgnoreCase("resetpoints")) {
					if(args.length == 1) {
						if(sender instanceof Player) {
							Player player = (Player) sender;
						if(player.getName().equals("Scrym6") || player.getName().equals("Pierronus")) {
							String targetname = args[0];
							if(Bukkit.getPlayer(targetname) != null) {
								if(targetname != player.getName()) {
								Player target = Bukkit.getPlayer(targetname);
								data.resetPoints(target.getUniqueId());
								String nowpts = String.valueOf(data.getPoints(target.getUniqueId()));
								player.sendMessage("§cReset le compteur de OrdCoins de " + target.getName() + "\n§cIl a maintenant " + nowpts);
								
								
							}
							}
							else {
								player.sendMessage("§6§l[GCA] §f§cCe joueur n'existe pas");
							}
						}
					}
						else {
							String targetname = args[0];
							if(Bukkit.getPlayer(targetname) != null) {
								Player target = Bukkit.getPlayer(targetname);
								data.resetPoints(target.getUniqueId());
								String nowpts = String.valueOf(data.getPoints(target.getUniqueId())); 
								System.out.println("Remis à 0 le compteur de points de " + target.getName() + ". Il a maintenant " + nowpts);
								
								
							
							}
							
							
						}
					}
			}
			return false;
			
		}
		
		
}
