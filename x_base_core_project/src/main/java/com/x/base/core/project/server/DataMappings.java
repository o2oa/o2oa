package com.x.base.core.project.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.BaseTools;
import com.x.base.core.DefaultCharset;
import com.x.base.core.Packages;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.utils.ListTools;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class DataMappings extends ConcurrentHashMap<String, CopyOnWriteArrayList<DataMapping>> {

	private static final long serialVersionUID = -2013392322121523454L;

	public DataMappings() {
		super();
	}

	public DataMappings(Nodes nodes) throws Exception {
		super();
		String base = BaseTools.getBasePath();
		File file = new File(base, "config/externalDataSourceEnable.cfg");
		if (!file.exists()) {
			throw new Exception("file not existed path:" + file.getAbsolutePath() + ".");
		}
		String str = StringUtils.trim(FileUtils.readFileToString(file, DefaultCharset.charset));
		List<Class<?>> classes = this.scanEntities();
		// 初始化,填充List
		for (Class<?> cls : classes) {
			this.put(cls.getName(), new CopyOnWriteArrayList<DataMapping>());
		}
		if (BooleanUtils.toBoolean(str)) {
			this.initExternal(classes);
		} else {
			this.initInternal(nodes.dataServers(), classes);
		}
	}

	private void initExternal(List<Class<?>> classes) throws Exception {
		ExternalDataSources externalDataSources = BaseTools.readObject("config/externalDataSources.json",
				ExternalDataSources.class);

		if (externalDataSources.size() == 0) {
			throw new Exception("externalDataSources is empty.");
		}
		if (externalDataSources.size() == 1) {
			// 如果只有一个数据源那么不用考虑includes 和 excludes
			ExternalDataSource source = externalDataSources.get(0);
			for (Class<?> cls : classes) {
				DataMapping o = new DataMapping();
				o.setUrl(source.getUrl());
				o.setUsername(source.getUsername());
				o.setPassword(source.getPassword());
				this.get(cls.getName()).add(o);
			}
		} else {
			// 如果有多个数据源那么要考虑includes 和 excludes
			for (ExternalDataSource source : externalDataSources) {
				List<String> names = new ArrayList<>();
				for (Class<?> cls : classes) {
					names.add(cls.getName());
				}
				if (ListTools.isNotEmpty(source.getIncludes())) {
					names = ListUtils.intersection(names, source.getIncludes());
				}
				if (ListTools.isNotEmpty(source.getExcludes())) {
					names = ListUtils.subtract(names, source.getExcludes());
				}
				for (String str : names) {
					DataMapping o = new DataMapping();
					o.setUrl(source.getUrl());
					o.setUsername(source.getUsername());
					o.setPassword(source.getPassword());
					this.get(str).add(o);
				}
			}
		}
	}

	private void initInternal(DataServers dataServers, List<Class<?>> classes) throws Exception {
		if (dataServers.size() == 0) {
			throw new Exception("dataServers is empty.");
		}
		if (dataServers.size() == 1) {
			for (Class<?> cls : classes) {
				DataMapping o = new DataMapping();
				String url = "jdbc:h2:tcp://" + dataServers.firstKey() + ":"
						+ dataServers.firstEntry().getValue().getTcpPort() + "/X";
				o.setUrl(url);
				o.setUsername("sa");
				String password = dataServers.firstEntry().getValue().getCalculatedPassword();
				if (StringUtils.isEmpty(password)) {
					password = Config.token().getPassword();
				}
				o.setPassword(password);
				this.get(cls.getName()).add(o);
			}
		} else {
			for (Entry<String, DataServer> entry : dataServers.entrySet()) {
				String node = entry.getKey();
				DataServer server = entry.getValue();
				List<String> names = new ArrayList<>();
				for (Class<?> cls : classes) {
					names.add(cls.getName());
				}
				if (ListTools.isNotEmpty(server.getIncludes())) {
					names = ListUtils.intersection(names, server.getIncludes());
				}
				if (ListTools.isNotEmpty(server.getExcludes())) {
					names = ListUtils.subtract(names, server.getExcludes());
				}
				for (String str : names) {
					DataMapping o = new DataMapping();
					String url = "jdbc:h2:tcp://" + node + ":" + server.getTcpPort() + "/X";
					o.setUrl(url);
					o.setUsername("sa");
					String password = server.getCalculatedPassword();
					if (StringUtils.isEmpty(password)) {
						password = Config.token().getPassword();
					}
					o.setPassword(password);
					this.get(str).add(o);
				}
			}
		}
	}

	private List<Class<?>> scanEntities() throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<String> names = scanResult.getNamesOfClassesWithAnnotation(ContainerEntity.class);
		List<Class<?>> list = new ArrayList<>();
		for (String str : names) {
			list.add(Class.forName(str));
		}
		return list;
	}

}