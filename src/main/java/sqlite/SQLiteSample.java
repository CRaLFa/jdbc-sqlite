package sqlite;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.ThrowingConsumer;

public class SQLiteSample {

	private static final String DB_URL = "jdbc:sqlite:sample.db";
	private static final String IMPORT_DIR = "import";
	private static final String EXPORT_DIR = "export";
	private static final String EXPORT_FILE = "result.txt";

	private Connection con = null;
	private PreparedStatement ps = null;

	public static void main(String[] args) throws Exception {
		SQLiteSample cd = new SQLiteSample();
		try {
			cd.executeUpdate("drop table if exists sample");
			cd.executeUpdate("create table sample ( id integer, name text )");
			int[] id = { 0 };
			Files.list(Path.of(IMPORT_DIR)).forEach((ThrowingConsumer<Path>) path -> {
				Files.readAllLines(path, StandardCharsets.UTF_8).forEach(line -> {
					cd.executeUpdate("insert into sample values ( ?, ? )", ++id[0], line);
				});
			});
			ResultSet rs = cd.executeQuery("select * from sample where id > ?", 1);
			List<String> strBuf = new ArrayList<>();
			while (rs.next()) {
				strBuf.add(String.format("{ id: %d, name: %s }", rs.getInt("id"), rs.getString("name")));
			}
			Files.write(Path.of(EXPORT_DIR, EXPORT_FILE), strBuf, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} finally {
			cd.closeConnection();
		}
	}

	public SQLiteSample() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		con = DriverManager.getConnection(DB_URL);
	}

	private ResultSet executeQuery(String sql, Object... params) {
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

	private int executeUpdate(String sql, Object... params) {
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

	private void closeConnection() {
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
