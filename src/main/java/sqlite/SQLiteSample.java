package sqlite;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.ThrowingConsumer;

public class SQLiteSample {

	private static final String DB_URL = "jdbc:sqlite:sample.db";
	private static final String IMPORT_DIR = "import";
	private static final String EXPORT_DIR = "export";
	private static final String EXPORT_FILE = "result.txt";

	public static void main(String[] args) throws Exception {
		SQLiteClient client = new SQLiteClient(DB_URL);
		try {
			client.executeUpdate("drop table if exists sample");
			client.executeUpdate("create table sample ( id integer, name text )");
			int[] id = { 0 };
			Files.list(Path.of(IMPORT_DIR)).forEach((ThrowingConsumer<Path>) path -> {
				Files.readAllLines(path, StandardCharsets.UTF_8).forEach((ThrowingConsumer<String>) line -> {
					client.executeUpdate("insert into sample values ( ?, ? )", ++id[0], line);
				});
			});
			ResultSet rs = client.executeQuery("select * from sample where id > ?", 1);
			List<String> strBuf = new ArrayList<>();
			while (rs.next()) {
				strBuf.add(String.format("{ id: %d, name: %s }", rs.getInt("id"), rs.getString("name")));
			}
			Files.write(Path.of(EXPORT_DIR, EXPORT_FILE), strBuf, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} finally {
			client.closeConnection();
		}
	}

}
