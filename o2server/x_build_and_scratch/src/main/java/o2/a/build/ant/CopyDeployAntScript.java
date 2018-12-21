package o2.a.build.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.base.core.project.AssembleA;
import com.x.base.core.project.CoreA;
import com.x.base.core.project.Packages;
import com.x.base.core.project.ServiceA;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class CopyDeployAntScript {

	@Test
	public void copyAssembleScript() throws Exception {
		File root = new File(new File("").getAbsolutePath());
		File template = new File(root, "assemble_template_build.xml");
		for (String str : this.listAssemble()) {
			File dir = new File(root.getParent(), str);
			String name = StringUtils.replace(str, ".", "_") + "_build.xml";
			File dest = new File(dir, name);
			System.out.println("copy to:" + dest.getAbsolutePath());
			FileUtils.copyFile(template, dest);
		}
	}

	@Test
	public void copyServiceScript() throws Exception {
		File root = new File(new File("").getAbsolutePath());
		File template = new File(root, "service_template_build.xml");
		for (String str : this.listService()) {
			File dir = new File(root.getParent(), str);
			String name = StringUtils.replace(str, ".", "_") + "_build.xml";
			File dest = new File(dir, name);
			System.out.println("copy to:" + dest.getAbsolutePath());
			FileUtils.copyFile(template, dest);
		}
	}

	@Test
	public void copyCoreScript() throws Exception {
		File root = new File(new File("").getAbsolutePath());
		File template = new File(root, "core_template_build.xml");
		for (String str : this.listCore()) {
			File dir = new File(root.getParent(), str);
			String name = StringUtils.replace(str, ".", "_") + "_build.xml";
			File dest = new File(dir, name);
			System.out.println("copy to:" + dest.getAbsolutePath());
			FileUtils.copyFile(template, dest);
		}
	}

	private List<String> listAssemble() throws Exception {
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

	private List<String> listService() throws Exception {
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

	private List<String> listCore() throws Exception {
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
