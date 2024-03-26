package fr.pierronus.pvpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SQLGetter {

	private Main plugin;
	private static final SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	public SQLGetter(Main plugin) {
		this.plugin = plugin;
	}
	
	public void createTable() {
		PreparedStatement ps;
		try {
			ps = plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS pvpbox_joueurs " 
		+ "(USERNAME VARCHAR(100),UUID VARCHAR(100),KILLS INT(11),MORTS INT(11),DATE_REG VARCHAR(255),IP VARCHAR(255),KILLSTREAK INT(11),PRIMARY KEY (USERNAME))");
		    ps.executeUpdate();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public void createPlayer(Player player) {
		try {
			UUID uuid = player.getUniqueId();
			if(!exists(uuid)) {
				PreparedStatement ps2 = plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO pvpbox_joueurs" +
			" (USERNAME,UUID,KILLS,MORTS,DATE_REG) VALUES (?,?,?,?,?)");
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				ps2.setString(1, player.getName());
				ps2.setString(2, uuid.toString());
				ps2.setInt(3, 0);
				ps2.setInt(4, 0);
				ps2.setString(5, sdf3.format(timestamp));
				
				ps2.executeUpdate();
				return;
			}else {
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean exists(UUID uuid) {
		try {
			PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM pvpbox_joueurs WHERE UUID=?");
			ps.setString(1, uuid.toString());
			ResultSet results = ps.executeQuery();
			if(results.next()) {
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public int getKills(UUID uuid) {
		try {
			PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT KILLS FROM pvpbox_joueurs WHERE UUID=?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			int kills = 0;
			if(rs.next()) {
				kills = rs.getInt("KILLS");
				return kills;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
		
	}
	public int getMorts(UUID uuid) {
		try {
			PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT MORTS FROM pvpbox_joueurs WHERE UUID=?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			int morts = 0;
			if(rs.next()) {
				morts = rs.getInt("MORTS");
				return morts;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
		
	}
	public void addKills(UUID uuid) {
		try {
		PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE pvpbox_joueurs SET KILLS=? WHERE UUID=?");
		ps.setInt(1, (getKills(uuid) + 1));
		ps.setString(2, uuid.toString());
		ps.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void addMorts(UUID uuid) {
		try {
		PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE pvpbox_joueurs SET MORTS=? WHERE UUID=?");
		ps.setInt(1, (getMorts(uuid) + 1));
		ps.setString(2, uuid.toString());
		ps.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setIp(UUID uuid) {
		try {
			Player player = Bukkit.getPlayer(uuid);
			String ip = String.valueOf(player.getAddress().getHostString());
		PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE pvpbox_joueurs SET IP=? WHERE UUID=?");
		ps.setString(1, ip);
		ps.setString(2, uuid.toString());
		ps.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addKS(UUID uuid) {
		try {
		PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE pvpbox_joueurs SET KILLSTREAK=? WHERE UUID=?");
		ps.setInt(1, (getKS(uuid) + 1));
		ps.setString(2, uuid.toString());
		ps.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void resetKS(UUID uuid) {
		try {
		PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE pvpbox_joueurs SET KILLSTREAK=? WHERE UUID=?");
		ps.setInt(1, 0);
		ps.setString(2, uuid.toString());
		ps.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getKS(UUID uuid) {
		try {
			PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT KILLSTREAK FROM pvpbox_joueurs WHERE UUID=?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			int ks = 0;
			if(rs.next()) {
				ks = rs.getInt("KILLSTREAK");
				return ks;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
		
	}
	
	public int getPoints(UUID uuid) {
		try {
			PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT POINTS FROM points WHERE UUID=?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			int points = 0;
			if(rs.next()) {
				points = rs.getInt("POINTS");
				return points;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
		
	}
	
	public void addPoints(UUID uuid,int points) {
		try {
		PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE points SET POINTS=? WHERE UUID=?");
		ps.setInt(1, (getPoints(uuid) + points));
		ps.setString(2, uuid.toString());
		ps.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removePoints(UUID uuid,int points) {
		try {
		PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE points SET POINTS=? WHERE UUID=?");
		ps.setInt(1, (getPoints(uuid) - points));
		ps.setString(2, uuid.toString());
		ps.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void resetPoints(UUID uuid) {
		try {
		PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE points SET POINTS=0 WHERE UUID=?");
		ps.setString(1, uuid.toString());
		ps.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}

	
	

