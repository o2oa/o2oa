package o2.a.build.jest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.x.base.core.project.AssembleA;
import com.x.base.core.project.Packages;
import com.x.base.core.project.ServiceA;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class CopyJest {

	@Test
	public void copyDescribeJs() throws Exception {
		File root = new File(new File("").getAbsolutePath());
		File template = new File(root, "jest/describe.js");
		for (String str : this.listAssemble()) {
			File dir = new File(root.getParent(), str);
			File dest = new File(dir, "jest/describe.js");
			System.out.println("copy to:" + dest.getAbsolutePath());
			FileUtils.copyFile(template, dest);
		}
		for (String str : this.listService()) {
			File dir = new File(root.getParent(), str);
			File dest = new File(dir, "jest/describe.js");
			System.out.println("copy to:" + dest.getAbsolutePath());
			FileUtils.copyFile(template, dest);
		}
	}

	@Test
	public void copyJqueryJs() throws Exception {
		File root = new File(new File("").getAbsolutePath());
		File template = new File(root, "jest/jquery.min.js");
		for (String str : this.listAssemble()) {
			File dir = new File(root.getParent(), str);
			File dest = new File(dir, "jest/jquery.min.js");
			System.out.println("copy to:" + dest.getAbsolutePath());
			FileUtils.copyFile(template, dest);
		}
		for (String str : this.listService()) {
			File dir = new File(root.getParent(), str);
			File dest = new File(dir, "jest/jquery.min.js");
			System.out.println("copy to:" + dest.getAbsolutePath());
			FileUtils.copyFile(template, dest);
		}
	}

	@Test
	public void copyIndexHtml() throws Exception {
		File root = new File(new File("").getAbsolutePath());
		File template = new File(root, "jest/index.html");
		for (String str : this.listAssemble()) {
			File dir = new File(root.getParent(), str);
			File dest = new File(dir, "jest/index.html");
			System.out.println("copy to:" + dest.getAbsolutePath());
			FileUtils.copyFile(template, dest);
		}
		for (String str : this.listService()) {
			File dir = new File(root.getParent(), str);
			File dest = new File(dir, "jest/index.html");
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

}
