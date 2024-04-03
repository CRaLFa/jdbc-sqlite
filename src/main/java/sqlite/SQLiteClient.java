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

	public ResultSet executeQuery(String sql, Object... params) throws SQLException {
		closeStatement();
		ps = con.prepareStatement(sql);
		for (int i = 0; i < params.length; i++) {
			ps.setObject(i + 1, params[i]);
		}
		return ps.executeQuery();
	}

	public int executeUpdate(String sql, Object... params) throws SQLException {
		closeStatement();
		ps = con.prepareStatement(sql);
		for (int i = 0; i < params.length; i++) {
			ps.setObject(i + 1, params[i]);
		}
		return ps.executeUpdate();
	}

	public void closeConnection() throws SQLException {
		closeStatement();
		if (con != null) {
			con.close();
		}
	}

	private void closeStatement() throws SQLException {
		if (ps != null) {
			ps.close();
		}
	}

}
