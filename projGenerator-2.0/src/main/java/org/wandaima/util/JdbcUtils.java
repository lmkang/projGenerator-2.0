package org.wandaima.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class JdbcUtils {

	private static String url;
	private static String driverClassName;
	private static String username;
	private static String password;
	
	static {
		Map<String, String> databaseInfo = XmlUtils.getDatabaseInfo();
		url = databaseInfo.get("database.url");
		driverClassName = databaseInfo.get("database.driverClassName");
		username = databaseInfo.get("database.username");
		password = databaseInfo.get("database.password");
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void release(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
