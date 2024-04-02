package sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteClient {

	private Connection con = null;
	private PreparedStatement ps = null;

	public SQLiteClient(String dbUrl) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		con = DriverManager.getConnection(dbUrl);
	}

	public ResultSet executeQuery(String sql, Object... params) {
		closeStatement();
		try {
			ps = con.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			return ps.executeQuery();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public int executeUpdate(String sql, Object... params) {
		closeStatement();
		try {
			ps = con.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void closeConnection() {
		closeStatement();
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void closeStatement() {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
