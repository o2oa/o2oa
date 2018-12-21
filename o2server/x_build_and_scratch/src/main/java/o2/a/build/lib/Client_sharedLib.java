package o2.a.build.lib;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.Test;

public class Client_sharedLib {

	@Test
	public void copyJar() throws Exception {
		String root = FilenameUtils.getFullPathNoEndSeparator(new File("").getAbsolutePath());
		File lib = new File(root, "lib");
		File sharedLib = new File(root, "shared/lib");
		FileUtils.cleanDirectory(sharedLib);
		FileUtils.copyDirectory(new File(lib, "apache/ant"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "apache/commons/beanutils"), sharedLib,
				this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "apache/commons/codec"), sharedLib,
				this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "apache/commons/collections4"), sharedLib,
				this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "apache/commons/dbcp2"), sharedLib,
				this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "apache/commons/pool2"), sharedLib,
				this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "apache/commons/fileupload"), sharedLib,
				this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "apache/commons/io"), sharedLib,
				this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "apache/commons/lang3"), sharedLib,
				this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "apache/commons/logging"), sharedLib,
				this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "apache/commons/net"), sharedLib,
				this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "apache/poi"), sharedLib,
				this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "sourceforge/dom4j"), sharedLib,
				this.notJavadocTestJarFilter());

		FileUtils.copyDirectory(new File(lib, "google/gson"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "github/fast-classpath-scanner"), sharedLib,
				this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "ehcache"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "slf4j"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "javax"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "mysql"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "postgresql"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "ibm/informix"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "ibm/db2"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "oracle"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "cargo"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "jersey"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "imgscalr"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "jpinyin"), sharedLib, this.notJavadocTestJarFilter());
		FileUtils.copyDirectory(new File(lib, "sourceforge/dom4j"), sharedLib, this.notJavadocTestJarFilter());
	}

	private FileFilter notJavadocTestJarFilter() {
		IOFileFilter filter = new AndFileFilter(new NotFileFilter(new WildcardFileFilter("*javadoc.jar")),
				new NotFileFilter(new WildcardFileFilter("*tests.jar")));
		filter = new AndFileFilter(filter, new NotFileFilter(new WildcardFileFilter("*sources.jar")));
		return new AndFileFilter(filter, new WildcardFileFilter("*.jar"));
	}
}
