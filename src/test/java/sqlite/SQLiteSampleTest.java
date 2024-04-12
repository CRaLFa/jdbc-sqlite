package sqlite;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SQLiteSampleTest {

	private static final Path EXPORT_FILE_PATH = getPath("export", "test_result.txt");

	private static Path getPath(String first, String... more) {
		return FileSystems.getDefault().getPath(first, more);
	}

	private SQLiteSample ss;

	@BeforeEach
	void setUp() throws Exception {
		ss = new SQLiteSample("sample_test.db");
	}

	@AfterEach
	void tearDown() throws Exception {
		ss.closeConnection();
		Files.deleteIfExists(EXPORT_FILE_PATH);
	}

	@Test
	void testCase1() throws Exception {
		ss.importFile(getPath("testdata", "case1"));
		ss.exportFile(EXPORT_FILE_PATH);

		List<String> lines = Files.readAllLines(EXPORT_FILE_PATH);
		assertEquals(4, lines.size());
		assertEquals("{ id: 2, name: 222 }", lines.get(0));
		assertEquals("{ id: 3, name: 333 }", lines.get(1));
		assertEquals("{ id: 4, name: 444 }", lines.get(2));
		assertEquals("{ id: 5, name: 555 }", lines.get(3));
	}

}
