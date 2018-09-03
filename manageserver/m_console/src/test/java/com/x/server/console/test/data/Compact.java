package com.x.server.console.test.data;

import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.RunScript;
import org.h2.tools.Script;
import org.junit.Test;

public class Compact {
	@Test
	public void compact() throws SQLException {
		String url = "jdbc:h2:D:/o2server/local/repository/data/X";
		String file = "D:/o2server/local/repository/data/test.sql";
		Script.process(url, "sa","1", file, "", "");
		DeleteDbFiles.execute("D:/o2server/local/repository/data","X", true);
		RunScript.execute(url,"sa","1", file, null, false);
		//FileUtils.delete(file);
	}

}
