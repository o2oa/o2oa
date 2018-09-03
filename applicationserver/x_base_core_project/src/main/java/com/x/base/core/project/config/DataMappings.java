package com.x.base.core.project.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataMappings extends ConcurrentHashMap<String, CopyOnWriteArrayList<DataMapping>> {

	private static final long serialVersionUID = -2013392322121523454L;

	public DataMappings() {
		super();
	}

	// public DataMappings(Nodes nodes) throws Exception {
	// super();
	// String base = BaseTools.getBasePath();
	// File file = new File(base, "config/externalDataSourceEnable.cfg");
	// if (!file.exists()) {
	// throw new Exception("file not existed path:" + file.getAbsolutePath() + ".");
	// }
	// String str = StringUtils.trim(FileUtils.readFileToString(file,
	// DefaultCharset.charset));
	// List<Class<?>> classes = this.scanEntities();
	// // 初始化,填充List
	// for (Class<?> cls : classes) {
	// this.put(cls.getName(), new CopyOnWriteArrayList<DataMapping>());
	// }
	// if (BooleanUtils.toBoolean(str)) {
	// this.initExternal(classes);
	// } else {
	// this.initInternal(nodes.dataServers(), classes);
	// }
	// }
	//
	// private void initExternal(List<Class<?>> classes) throws Exception {
	// ExternalDataSources externalDataSources =
	// BaseTools.readObject("config/externalDataSources.json",
	// ExternalDataSources.class);
	//
	// if (externalDataSources.size() == 0) {
	// throw new Exception("externalDataSources is empty.");
	// }
	// if (externalDataSources.size() == 1) {
	// // 如果只有一个数据源那么不用考虑includes 和 excludes
	// ExternalDataSource source = externalDataSources.get(0);
	// for (Class<?> cls : classes) {
	// DataMapping o = new DataMapping();
	// o.setUrl(source.getUrl());
	// o.setUsername(source.getUsername());
	// o.setPassword(source.getPassword());
	// this.get(cls.getName()).add(o);
	// }
	// } else {
	// // 如果有多个数据源那么要考虑includes 和 excludes
	// for (ExternalDataSource source : externalDataSources) {
	// List<String> names = new ArrayList<>();
	// for (Class<?> cls : classes) {
	// names.add(cls.getName());
	// }
	// if (ListTools.isNotEmpty(source.getIncludes())) {
	// names = ListUtils.intersection(names, source.getIncludes());
	// }
	// if (ListTools.isNotEmpty(source.getExcludes())) {
	// names = ListUtils.subtract(names, source.getExcludes());
	// }
	// for (String str : names) {
	// DataMapping o = new DataMapping();
	// o.setUrl(source.getUrl());
	// o.setUsername(source.getUsername());
	// o.setPassword(source.getPassword());
	// this.get(str).add(o);
	// }
	// }
	// }
	// }
	//
	// private void initInternal(DataServers dataServers, List<Class<?>> classes)
	// throws Exception {
	// if (dataServers.size() == 0) {
	// throw new Exception("dataServers is empty.");
	// }
	// if (dataServers.size() == 1) {
	// for (Class<?> cls : classes) {
	// DataMapping o = new DataMapping();
	// String url = "jdbc:h2:tcp://" + dataServers.firstKey() + ":"
	// + dataServers.firstEntry().getValue().getTcpPort() + "/X;JMX="
	// +
	// StringUtils.upperCase(dataServers.firstEntry().getValue().getJmxEnable().toString())
	// + ";CACHE_SIZE=" + (dataServers.firstEntry().getValue().getCacheSize() *
	// 1024);
	// o.setUrl(url);
	// o.setUsername("sa");
	// o.setPassword(Config.token().getPassword());
	// this.get(cls.getName()).add(o);
	// }
	// } else {
	// for (Entry<String, DataServer> entry : dataServers.entrySet()) {
	// String node = entry.getKey();
	// DataServer server = entry.getValue();
	// List<String> names = new ArrayList<>();
	// for (Class<?> cls : classes) {
	// names.add(cls.getName());
	// }
	// if (ListTools.isNotEmpty(server.getIncludes())) {
	// names = ListUtils.intersection(names, server.getIncludes());
	// }
	// if (ListTools.isNotEmpty(server.getExcludes())) {
	// names = ListUtils.subtract(names, server.getExcludes());
	// }
	// for (String str : names) {
	// DataMapping o = new DataMapping();
	// String url = "jdbc:h2:tcp://" + node + ":" + server.getTcpPort() + "/X;JMX="
	// + StringUtils.upperCase(server.getJmxEnable().toString()) + ";CACHE_SIZE="
	// + (server.getCacheSize() * 1024);
	// o.setUrl(url);
	// o.setUsername("sa");
	// o.setPassword(Config.token().getPassword());
	// this.get(str).add(o);
	// }
	// }
	// }
	// }
	//
	// private List<Class<?>> scanEntities() throws Exception {
	// ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
	// List<String> names =
	// scanResult.getNamesOfClassesWithAnnotation(ContainerEntity.class);
	// List<Class<?>> list = new ArrayList<>();
	// for (String str : names) {
	// list.add(Class.forName(str));
	// }
	// return list;
	// }

}