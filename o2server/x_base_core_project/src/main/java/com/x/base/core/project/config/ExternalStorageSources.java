package com.x.base.core.project.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.x.base.core.entity.StorageProtocol;
import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.FieldDescribe;

public class ExternalStorageSources extends ConfigObject {

	private static final long serialVersionUID = 8229115124625865737L;

	public static ExternalStorageSources defaultInstance() {

		ExternalStorageSources externalStorages = new ExternalStorageSources();
		externalStorages.getFile();
		externalStorages.getMind();
		externalStorages.getMeeting();
		externalStorages.getProcessPlatform();
		externalStorages.getCalendar();
		externalStorages.getOkr();
		externalStorages.getCms();
		externalStorages.getBbs();
		externalStorages.getReport();
		externalStorages.getStrategyDeploy();
		externalStorages.getTeamwork();
		externalStorages.getStructure();
		externalStorages.getIm();
		externalStorages.getGeneral();
		externalStorages.getCustom();
		externalStorages.getStore();
		return externalStorages;
	}

	public ExternalStorageSources() {
		super();

	}

	private CopyOnWriteArrayList<ExternalStorageSource> file;
	private CopyOnWriteArrayList<ExternalStorageSource> processPlatform;
	private CopyOnWriteArrayList<ExternalStorageSource> mind;
	private CopyOnWriteArrayList<ExternalStorageSource> meeting;
	private CopyOnWriteArrayList<ExternalStorageSource> calendar;
	private CopyOnWriteArrayList<ExternalStorageSource> okr;
	private CopyOnWriteArrayList<ExternalStorageSource> cms;
	private CopyOnWriteArrayList<ExternalStorageSource> bbs;
	private CopyOnWriteArrayList<ExternalStorageSource> report;
	private CopyOnWriteArrayList<ExternalStorageSource> strategyDeploy;
	private CopyOnWriteArrayList<ExternalStorageSource> teamwork;
	private CopyOnWriteArrayList<ExternalStorageSource> structure;
	private CopyOnWriteArrayList<ExternalStorageSource> im;
	private CopyOnWriteArrayList<ExternalStorageSource> general;
	private CopyOnWriteArrayList<ExternalStorageSource> custom;

	private ConcurrentHashMap<String, Store> store;

	public Map<String, Store> getStore() {
		if (null == this.store) {
			this.store = new ConcurrentHashMap<>();
		}
		return this.store;
	}

	public List<ExternalStorageSource> get(StorageType storageType) {
		switch (storageType) {
		case file:
			return this.getFile();
		case processPlatform:
			return this.getProcessPlatform();
		case mind:
			return this.getMind();
		case meeting:
			return this.getMeeting();
		case calendar:
			return this.getCalendar();
		case okr:
			return this.getOkr();
		case cms:
			return this.getCms();
		case bbs:
			return this.getBbs();
		case report:
			return this.getReport();
		case strategyDeploy:
			return this.getStrategyDeploy();
		case teamwork:
			return this.getTeamwork();
		case structure:
			return this.getStructure();
		case im:
			return this.getIm();
		case general:
			return this.getGeneral();
		case custom:
		default:
			return this.getCustom();
		}
	}

	public List<ExternalStorageSource> getFile() {
		if (null == this.file) {
			this.file = new CopyOnWriteArrayList<>();
			this.file.add(new ExternalStorageSource());
		}
		return this.file;
	}

	public List<ExternalStorageSource> getMind() {
		if (null == this.mind) {
			this.mind = new CopyOnWriteArrayList<>();
			this.mind.add(new ExternalStorageSource());
		}
		return this.mind;
	}

	public List<ExternalStorageSource> getMeeting() {
		if (null == this.meeting) {
			this.meeting = new CopyOnWriteArrayList<>();
			this.meeting.add(new ExternalStorageSource());
		}
		return this.meeting;
	}

	public List<ExternalStorageSource> getProcessPlatform() {
		if (null == this.processPlatform) {
			this.processPlatform = new CopyOnWriteArrayList<>();
			this.processPlatform.add(new ExternalStorageSource());
		}
		return this.processPlatform;
	}

	public List<ExternalStorageSource> getCalendar() {
		if (null == this.calendar) {
			this.calendar = new CopyOnWriteArrayList<>();
			this.calendar.add(new ExternalStorageSource());
		}
		return this.calendar;
	}

	public List<ExternalStorageSource> getOkr() {
		if (null == this.okr) {
			this.okr = new CopyOnWriteArrayList<>();
			this.okr.add(new ExternalStorageSource());
		}
		return this.okr;
	}

	public List<ExternalStorageSource> getCms() {
		if (null == this.cms) {
			this.cms = new CopyOnWriteArrayList<>();
			this.cms.add(new ExternalStorageSource());
		}
		return this.cms;
	}

	public List<ExternalStorageSource> getBbs() {
		if (null == this.bbs) {
			this.bbs = new CopyOnWriteArrayList<>();
			this.bbs.add(new ExternalStorageSource());
		}
		return this.bbs;
	}

	public List<ExternalStorageSource> getReport() {
		if (null == this.report) {
			this.report = new CopyOnWriteArrayList<>();
			this.report.add(new ExternalStorageSource());
		}
		return this.report;
	}

	public List<ExternalStorageSource> getStrategyDeploy() {
		if (null == this.strategyDeploy) {
			this.strategyDeploy = new CopyOnWriteArrayList<>();
			this.strategyDeploy.add(new ExternalStorageSource());
		}
		return this.strategyDeploy;
	}

	public List<ExternalStorageSource> getTeamwork() {
		if (null == this.teamwork) {
			this.teamwork = new CopyOnWriteArrayList<>();
			this.teamwork.add(new ExternalStorageSource());
		}
		return this.teamwork;
	}

	public List<ExternalStorageSource> getStructure() {
		if (null == this.structure) {
			this.structure = new CopyOnWriteArrayList<>();
			this.structure.add(new ExternalStorageSource());
		}
		return this.structure;
	}

	public List<ExternalStorageSource> getIm() {
		if (null == this.im) {
			this.im = new CopyOnWriteArrayList<>();
			this.im.add(new ExternalStorageSource());
		}
		return this.im;
	}

	public List<ExternalStorageSource> getGeneral() {
		if (null == this.general) {
			this.general = new CopyOnWriteArrayList<>();
			this.general.add(new ExternalStorageSource());
		}
		return this.general;
	}

	public List<ExternalStorageSource> getCustom() {
		if (null == this.custom) {
			this.custom = new CopyOnWriteArrayList<>();
			this.custom.add(new ExternalStorageSource());
		}
		return this.custom;
	}

	public static class Store {

		private StorageProtocol protocol;
		@FieldDescribe("登录用户名.")
		private String username;
		@FieldDescribe("登录密码.")
		private String password;
		@FieldDescribe("主机地址或阿里云endpoint.")
		private String host;
		@FieldDescribe("端口.")
		private Integer port;
		@FieldDescribe("存储节点名,对应存储名称,阿里云为bucket(桶)名称.")
		private String name;

		public StorageProtocol getProtocol() {
			return protocol;
		}

		public void setProtocol(StorageProtocol protocol) {
			this.protocol = protocol;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
}
