package com.memz.discord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
	
	public static void init() {
		if(!tableUsersExists()) {
			tableUsersCreate();
		}
		if(!usersTableHasPoolName()) {
			tableUsersAddPoolName();
		}
		if(!usersTableHasLastPending()) {
			tableUsersAddLastPending();
		}
		if(!tablePoolsExists()) {
			tablePoolsCreate();
		}
		
		System.out.println();
	}
	
	public static String getJson(String poolName) {
		Map<String, String> pools = new HashMap<String, String>();
		pools.put("RO", "http://pool.burstcoin.ro/pending2.json");
		pools.put("BTFG", "http://pool.burstcoin.ro/pending2.json");
		return pools.get(poolName);
	}

	/**
	 * Check whether the users database table exists, and if not, create it.
	 */
	public static boolean tableUsersExists() {
		System.out.println("Checking users table");
		String testSQL = "select * from TB_USERS;";
		return executeStatement(testSQL, false);
	}
	
	/**
	 * Checks whether the users table has the poolname column
	 * @return
	 */
	public static boolean usersTableHasPoolName() {
		System.out.println("Checking for poolname column");
		String testSQL = "select POOLNAME from TB_USERS;";
		return executeStatement(testSQL, false);
	}
	
	/**
	 * Checks whether the users table has the lastpending column
	 * @return
	 */
	public static boolean usersTableHasLastPending() {
		System.out.println("Checking for lastpending column");
		String testSQL = "select LASTPENDING from TB_USERS;";
		return executeStatement(testSQL, false);
	}
	
	/**
	 * Check whether the pools database table exists, and if not, create it.
	 */
	public static boolean tablePoolsExists() {
		String testSQL = "select * from TB_POOLS;";
		return executeStatement(testSQL, false);
	}
	
	/**
	 * Creates the users table
	 */
	public static void tableUsersCreate() {
		System.out.println("Creating users table");			
		String createSQL = "create table TB_USERS (USERID varchar(255),BURSTADDRESS varchar(255), BURSTNUMERIC varchar(255);";			
		executeStatement(createSQL, true);
	}
	
	/**
	 * Adds the poolname column to users table
	 */
	public static void tableUsersAddPoolName() {
		System.out.println("Adding poolname column");			
		String addSQL = "alter table TB_USERS add POOLNAME varchar(255);";			
		executeStatement(addSQL, true);
	}
	
	/**
	 * Adds the lastpending column to users table
	 */
	public static void tableUsersAddLastPending() {
		System.out.println("Adding lastpending column");			
		String addSQL = "alter table TB_USERS add LASTPENDING varchar(255);";			
		executeStatement(addSQL, true);
	}
	
	/**
	 * Creates the pools table
	 */
	public static void tablePoolsCreate() {
		System.out.println("Creating pools table");			
		String createSQL = "create table TB_POOLS (POOLNAME varchar(255), JSON varchar(255));";			
		executeStatement(createSQL, true);
	}
	
	public static boolean executeStatement(String sql, boolean showStackTrace) {
		try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:burst_db", "SA", "")) {
			try(Statement s = c.createStatement()) {
				try {
					s.execute(sql);
					return true;
				} catch (SQLException e) {
					if(showStackTrace) {e.printStackTrace();}
				}
			} catch (SQLException e) {
				if(showStackTrace) {e.printStackTrace();}
			}
		} catch (SQLException e) {
			if(showStackTrace) {e.printStackTrace();}
		}
		return false;
	}
	
	public static List<String> executeQuery(String table, String column) {
		String sql = "select "+column+" from "+table+";";
		List<String> results = new ArrayList<String>();
		try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:burst_db", "SA", "")) {
			try(PreparedStatement s = c.prepareStatement(sql)) {
				try(ResultSet rs = s.executeQuery()) {
					while(rs.next()) {					
						results.add(rs.getString(1));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	public static String executeUnique(String table, String column, String pkcolumn, String pk) {
		String sql = "select "+column+" from "+table+" where "+pkcolumn+" = '"+pk+"';";
		try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:burst_db", "SA", "")) {
			try(PreparedStatement s = c.prepareStatement(sql)) {
				try(ResultSet rs = s.executeQuery()) {
					while(rs.next()) {					
						return rs.getString(1);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean executeInsert(String table, String pkcolumn, String pk) {
		String sql = "insert into "+table+" ("+pkcolumn+") values ('"+pk+"');";
		try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:burst_db", "SA", "")) {
			try(Statement s = c.createStatement()) {
				try {
					s.execute(sql);
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean executeUpdate(String table, String column, String value, String pkcolumn, String pk) {
		String sql = "update "+table+" set "+column+" = '"+value+"' where "+pkcolumn+" = '"+pk+"';";
		try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:burst_db", "SA", "")) {
			try(Statement s = c.createStatement()) {
				try {
					s.execute(sql);
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean executeDelete(String table, String pkcolumn, String pk) {
		String sql = "delete from "+table+" where "+pkcolumn+" = '"+pk+"';";
		try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:burst_db", "SA", "")) {
			try(Statement s = c.createStatement()) {
				try {
					s.execute(sql);
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
