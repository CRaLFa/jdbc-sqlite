package sqlite;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.ThrowingConsumer;

public class SQLiteSample {

	public static void main(String[] args) throws Exception {
		if (args.length < 4) {
			System.err.println("Usage: <program> dbFile importDir exportDir exportFile");
			return;
		}
		SQLiteSample ss = new SQLiteSample(args[0]);
		FileSystem fs = FileSystems.getDefault();
		try {
			ss.importFile(fs.getPath(args[1]));
			ss.exportFile(fs.getPath(args[2], args[3]));
		} finally {
			ss.closeConnection();
		}
	}

	private final SQLiteClient client;

	public SQLiteSample(String dbFileName) throws Exception {
		this.client = new SQLiteClient("jdbc:sqlite:" + dbFileName);
	}

	public void importFile(Path importDirPath) throws Exception {
		client.executeUpdate("drop table if exists sample");
		client.executeUpdate("create table sample ( id integer, name text )");
		int[] id = { 0 };
		Files.list(importDirPath).forEach((ThrowingConsumer<Path>) path -> {
			Files.lines(path).forEach((ThrowingConsumer<String>) line -> {
				client.executeUpdate("insert into sample values ( ?, ? )", ++id[0], line);
			});
			System.out.printf("Successfully imported file %s\n", path.toAbsolutePath().toString());
		});
	}

	public void exportFile(Path exportFilePath) throws Exception {
		ResultSet rs = client.executeQuery("select * from sample where id > ?", 1);
		List<String> strBuf = new ArrayList<>();
		while (rs.next()) {
			strBuf.add(String.format("{ id: %d, name: %s }", rs.getInt("id"), rs.getString("name")));
		}
		Files.write(exportFilePath, strBuf, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		System.out.printf("Successfully exported file %s\n", exportFilePath.toAbsolutePath().toString());
	}

	public void closeConnection() throws SQLException {
		client.closeConnection();
	}

}
