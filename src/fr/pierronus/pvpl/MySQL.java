package fr.pierronus.pvpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

	private String host = "localhost";
	private String port = "3306";
	private String database = "";
	private String username = "";
	private String password = "";
	
	private Connection connection;
	
	public boolean isConnected() {
		return(connection == null ? false : true);
	}
	
	public void connect() throws ClassNotFoundException, SQLException {
		if(!isConnected()) {
		connection = DriverManager.getConnection("jdbc:mysql://" +
			     host + ":" + port + "/" + database + "?autoReconnect=true&cmaxReconnets=5&initialTimeout=1&useSSL=false",
			     username, password);
		}
		
	}
	
	public void disconnect() {
		if(isConnected()) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
}
