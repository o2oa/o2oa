package o2.a.build.mapping;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import com.x.base.core.container.LogLevel;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.Packages;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.ClassTools;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class Client_single {

	@Test
	public void demo_xplatform_tech() throws Exception {
		this.create_datas("demo.xplatform.tech");
	}

	@Test
	public void xc01_mss_sx_com() throws Exception {
		this.create_datas("xc01.mss.sx.com");
	}

	@Test
	public void dev_xplatform_tech() throws Exception {
		this.create_datas("dev.xplatform.tech");
	}

	@Test
	public void xa01_ray_local() throws Exception {
		this.create_datas("xa01.ray.local");
	}

	@Test
	public void xc01_vsettan_com_cn() throws Exception {
		this.create_datas("xc01.vsettan.com.cn");
	}

	private void create_datas(String dir) throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<Class<?>> list = ClassTools.forName(true, scanResult.getNamesOfClassesWithAnnotation(ContainerEntity.class));
		Datas datas = new Datas();
		for (Class<?> clz : list) {
			CopyOnWriteArrayList<Data> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
			Data data = new Data();
			data.setDataServer("xd01");
			data.setOrder(1);
			data.setDataCacheLevel(LogLevel.WARN);
			data.setEnhanceLevel(LogLevel.WARN);
			data.setJdbcLevel(LogLevel.WARN);
			data.setMetaDataLevel(LogLevel.WARN);
			data.setQueryLevel(LogLevel.WARN);
			data.setRuntimeLevel(LogLevel.WARN);
			data.setSqlLevel(LogLevel.WARN);
			data.setToolLevel(LogLevel.WARN);
			copyOnWriteArrayList.add(data);
			datas.put(clz.getName(), copyOnWriteArrayList);
		}
		File file = new File("");
		File path = new File(FilenameUtils.getFullPathNoEndSeparator(file.getAbsolutePath()),
				"configuration/" + dir + "/datas.json");
		FileUtils.writeStringToFile(path, XGsonBuilder.toJson(datas));
	}

}
