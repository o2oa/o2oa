package o2server.build.pom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import com.x.base.core.project.AssembleA;
import com.x.base.core.project.CoreA;
import com.x.base.core.project.Packages;
import com.x.base.core.project.ServiceA;
import com.x.base.core.project.jaxrs.logger.LoggerAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.FileTools;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class ReplicateMavenConfig {

	private static Logger logger = LoggerFactory.getLogger(ReplicateMavenConfig.class);

	public static void main(String[] args) throws Exception {
		core();
		assemble();
		service();
	}

	private static void core() throws Exception {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		MavenXpp3Writer writer = new MavenXpp3Writer();
		File file = new File("src/main/resources/pom_template_core.xml");
		Model model = reader.read(new FileReader(file));
		File parent = FileTools.parent(new File(""));
		for (String str : listCore()) {
			model.setArtifactId(str);
			File f = new File(parent, str + "/pom.xml");
			logger.print("create pom: {}.", f.getAbsolutePath());
			FileUtils.touch(f);
			try (FileOutputStream out = new FileOutputStream(f)) {
				writer.write(out, model);
			}
		}
	}

	private static void assemble() throws Exception {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		MavenXpp3Writer writer = new MavenXpp3Writer();
		File file = new File("src/main/resources/pom_template_assemble.xml");
		Model model = reader.read(new FileReader(file));
		File parent = FileTools.parent(new File(""));
		for (String str : listAssemble()) {
			model.setArtifactId(str);
			File f = new File(parent, str + "/pom.xml");
			logger.print("create pom: {}.", f.getAbsolutePath());
			FileUtils.touch(f);
			try (FileOutputStream out = new FileOutputStream(f)) {
				writer.write(out, model);
			}
		}
	}

	private static void service() throws Exception {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		MavenXpp3Writer writer = new MavenXpp3Writer();
		File file = new File("src/main/resources/pom_template_service.xml");
		Model model = reader.read(new FileReader(file));
		File parent = FileTools.parent(new File(""));
		for (String str : listService()) {
			model.setArtifactId(str);
			File f = new File(parent, str + "/pom.xml");
			logger.print("create pom: {}.", f.getAbsolutePath());
			FileUtils.touch(f);
			try (FileOutputStream out = new FileOutputStream(f)) {
				writer.write(out, model);
			}
		}
	}

	private static List<String> listAssemble() throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<String> list = new ArrayList<>();
		for (String str : scanResult.getNamesOfSubclassesOf(AssembleA.class)) {
			Class<?> clz = Class.forName(str);
			list.add(clz.getSimpleName());
		}
		Collections.sort(list, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return s1.compareTo(s2);
			}
		});
		return list;
	}

	private static List<String> listService() throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<String> list = new ArrayList<>();
		for (String str : scanResult.getNamesOfSubclassesOf(ServiceA.class)) {
			Class<?> clz = Class.forName(str);
			list.add(clz.getSimpleName());
		}
		Collections.sort(list, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return s1.compareTo(s2);
			}
		});
		return list;
	}

	private static List<String> listCore() throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<String> list = new ArrayList<>();
		for (String str : scanResult.getNamesOfSubclassesOf(CoreA.class)) {
			Class<?> clz = Class.forName(str);
			list.add(clz.getSimpleName());
		}
		Collections.sort(list, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return s1.compareTo(s2);
			}
		});
		return list;
	}
}
