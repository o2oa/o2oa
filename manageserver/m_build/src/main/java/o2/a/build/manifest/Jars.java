package o2.a.build.manifest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class Jars {

	@Test
	public void run() throws Exception {
		File dir = new File("d:/o2server/store/jars");
		if ((!dir.exists()) || (!dir.isDirectory())) {
			throw new Exception("error");
		}
		List<String> names = new ArrayList<>();
		for (File o : dir.listFiles()) {
			if (!StringUtils.equals(o.getName(), "manifest.cfg")) {
				if (StringUtils.isNotEmpty(o.getName())) {
					names.add(o.getName());
				}
			}
		}
		File file = new File(dir, "manifest.cfg");
		FileUtils.writeLines(file, names);
	}

}
