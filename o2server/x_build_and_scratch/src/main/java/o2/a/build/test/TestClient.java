package o2.a.build.test;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.openjpa.jdbc.meta.MappingTool;
import org.junit.Test;

public class TestClient {
	@Test
	public void test() throws IOException, SQLException {
		String[] args = new String[] { "-schemaAction", "build", "-sql", "e:/create.sql" };
		MappingTool.main(args);
	}
}
