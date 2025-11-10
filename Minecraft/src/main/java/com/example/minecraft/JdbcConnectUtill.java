package com.example.minecraft;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcConnectUtill {
	public static Connection getConnection() {
		Connection con = null;
		try {
			//커넥터 로딩
			Class.forName("com.mysql.cj.jdbc.Driver");
			//DB서버 커넥트
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jjap", "root", "3274");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;

	}//1, 2
	
	public static void close(Connection con, PreparedStatement pstmt) {
		try {
			con.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//4
	
	public static void close(Connection con, PreparedStatement pstmt, ResultSet rs) {
		try {
			con.close();
			pstmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//4
}
