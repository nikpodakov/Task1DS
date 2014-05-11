package root.ccfit.podakov;

import java.sql.*;
import java.util.Date;

public class App {
	public static void main(String[] args) {
		try {
			Connection conn = DriverManager.getConnection("jdbc:h2:mem:~/test;DB_CLOSE_DELAY=-1", "sa", "");
			Statement statement = conn.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS Task1Table (ID NUMBER, Name VARCHAR(50), CREATION_DATE TIMESTAMP);");
			conn.setAutoCommit(false);
			Date date = new Date();
			for(int i = 0; i < 800; i++)
				statement.execute(String.format("INSERT INTO Task1Table (ID, Name, CREATION_DATE) VALUES(%d, '%d', NOW())", i, i));
			conn.commit();
			System.out.println("Statement: " + ((Long)(new Date().getTime() - date.getTime())));
			statement.executeUpdate("TRUNCATE TABLE Task1Table");
			PreparedStatement ps = conn.prepareStatement("INSERT INTO Task1Table (ID, Name, CREATION_DATE) VALUES(?, ?, NOW())");
			date = new Date();
			for(int i = 0; i < 800; i++) {
				ps.setInt(1, i);
				ps.setString(2, ((Integer)i).toString());
				ps.execute();
			}
			conn.commit();
			System.out.println("PreparedStatement: " + ((Long)(new Date().getTime() - date.getTime())));
			statement.executeUpdate("TRUNCATE TABLE Task1Table");
			date = new Date();
			for(int i = 0; i < 16; i++) {
				for(int j = 0; j < 50; j++) {
					int k = i * 50 + j;
					ps.setInt(1, k);
					ps.setString(2, ((Integer) k).toString());
					ps.addBatch();
				}
				ps.executeBatch();
			}
			conn.commit();
			System.out.println("PreparedStatement + Batch: " + ((Long)(new Date().getTime() - date.getTime())));
			statement.executeUpdate("TRUNCATE TABLE Task1Table");
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}