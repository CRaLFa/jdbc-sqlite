package sqlite;

import java.nio.file.FileSystems;
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
			importFile(client);
			exportFile(client);
		} finally {
			client.closeConnection();
		}
	}

	private static void importFile(SQLiteClient client) throws Exception {
		client.executeUpdate("drop table if exists sample");
		client.executeUpdate("create table sample ( id integer, name text )");
		Path importDirPath = FileSystems.getDefault().getPath(IMPORT_DIR);
		int[] id = { 0 };
		Files.list(importDirPath).forEach((ThrowingConsumer<Path>) path -> {
			Files.readAllLines(path).forEach((ThrowingConsumer<String>) line -> {
				client.executeUpdate("insert into sample values ( ?, ? )", ++id[0], line);
			});
		});
	}

	private static void exportFile(SQLiteClient client) throws Exception {
		ResultSet rs = client.executeQuery("select * from sample where id > ?", 1);
		List<String> strBuf = new ArrayList<>();
		while (rs.next()) {
			strBuf.add(String.format("{ id: %d, name: %s }", rs.getInt("id"), rs.getString("name")));
		}
		Path exportFilePath = FileSystems.getDefault().getPath(EXPORT_DIR, EXPORT_FILE);
		Files.write(exportFilePath, strBuf, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	}

}
