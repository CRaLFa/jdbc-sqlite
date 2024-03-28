package company;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConvertData {

	private static final String DB_URL = "jdbc:sqlite:sample.db";
	private static final String IMPORT_DIR = "import";
//	private static final String EXPORT_DIR = "export";

	private Connection con = null;
	private PreparedStatement ps = null;

	public static void main(String[] args) throws Exception {
//		Files.list(Path.of("data")).map(Path::toAbsolutePath).forEach(System.out::println);
		Files.list(Path.of(IMPORT_DIR)).forEach(path -> {
			try {
				Files.readAllLines(path, StandardCharsets.UTF_8).forEach(System.out::println);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		ConvertData cd = new ConvertData();
		try {
			cd.executeUpdate("drop table if exists sample");
			cd.executeUpdate("create table sample ( id integer, name text )");
			cd.executeUpdate("insert into sample values ( ?, ? )", 1, "A");
			cd.executeUpdate("insert into sample values ( ?, ? )", 2, "B");
			cd.executeUpdate("insert into sample values ( ?, ? )", 3, "C");
			ResultSet rs = cd.executeQuery("select * from sample where id > ?", 1);
			while (rs.next()) {
				System.out.println(String.format("id: %d, name: %s", rs.getInt("id"), rs.getString("name")));
			}
		} finally {
			cd.closeConnection();
		}
	}

	public ConvertData() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		this.con = DriverManager.getConnection(DB_URL);
	}

	private ResultSet executeQuery(String sql, Object... params) throws SQLException {
		closeStatement();
		this.ps = this.con.prepareStatement(sql);
		for (int i = 0; i < params.length; i++) {
			this.ps.setObject(i + 1, params[i]);
		}
		return this.ps.executeQuery();
	}

	private int executeUpdate(String sql, Object... params) throws SQLException {
		closeStatement();
		this.ps = this.con.prepareStatement(sql);
		for (int i = 0; i < params.length; i++) {
			this.ps.setObject(i + 1, params[i]);
		}
		return this.ps.executeUpdate();
	}

	private void closeConnection() throws SQLException {
		closeStatement();
		if (this.con != null) {
			this.con.close();
		}
	}

	private void closeStatement() throws SQLException {
		if (this.ps != null) {
			this.ps.close();
		}
	}

}
