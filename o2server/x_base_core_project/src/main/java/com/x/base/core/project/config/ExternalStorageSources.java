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

	private static final Boolean DEFAULT_ENABLE = false;

	public static ExternalStorageSources defaultInstance() {

		ExternalStorageSources externalStorages = new ExternalStorageSources();
		externalStorages.getFile();
		externalStorages.getMind();
		externalStorages.getMeeting();
		externalStorages.getProcessPlatform();
		externalStorages.getCalendar();
		externalStorages.getCms();
		externalStorages.getBbs();
		externalStorages.getTeamwork();
		externalStorages.getStructure();
		externalStorages.getIm();
		externalStorages.getGeneral();
		externalStorages.getCustom();
		externalStorages.getStore();
		externalStorages.enable = DEFAULT_ENABLE;
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
	private CopyOnWriteArrayList<ExternalStorageSource> cms;
	private CopyOnWriteArrayList<ExternalStorageSource> bbs;
	private CopyOnWriteArrayList<ExternalStorageSource> teamwork;
	private CopyOnWriteArrayList<ExternalStorageSource> structure;
	private CopyOnWriteArrayList<ExternalStorageSource> im;
	private CopyOnWriteArrayList<ExternalStorageSource> general;
	private CopyOnWriteArrayList<ExternalStorageSource> custom;

	private ConcurrentHashMap<String, Store> store;

	private Boolean enable;

	public Map<String, Store> getStore() {
		if (null == this.store) {
			this.store = new ConcurrentHashMap<>();
			Store s = new Store();
			s.setHost("127.0.0.1");
			s.setName("");
			s.setUsername("");
			s.setPassword("");
			s.setProtocol(StorageProtocol.hdfs);
			s.setPort(9000);
			this.store.put("demo", s);
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
		case cms:
			return this.getCms();
		case bbs:
			return this.getBbs();
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

	public Boolean getEnable() {
		// 兼容7.2之前版本
		return (null == this.enable) ? Boolean.TRUE : this.enable;
	}

	public List<ExternalStorageSource> getFile() {
		if (null == this.file) {
			this.file = new CopyOnWriteArrayList<>();
			this.file.add(ExternalStorageSource.defaultInstance());
		}
		return this.file;
	}

	public List<ExternalStorageSource> getMind() {
		if (null == this.mind) {
			this.mind = new CopyOnWriteArrayList<>();
			this.mind.add(ExternalStorageSource.defaultInstance());
		}
		return this.mind;
	}

	public List<ExternalStorageSource> getMeeting() {
		if (null == this.meeting) {
			this.meeting = new CopyOnWriteArrayList<>();
			this.meeting.add(ExternalStorageSource.defaultInstance());
		}
		return this.meeting;
	}

	public List<ExternalStorageSource> getProcessPlatform() {
		if (null == this.processPlatform) {
			this.processPlatform = new CopyOnWriteArrayList<>();
			this.processPlatform.add(ExternalStorageSource.defaultInstance());
		}
		return this.processPlatform;
	}

	public List<ExternalStorageSource> getCalendar() {
		if (null == this.calendar) {
			this.calendar = new CopyOnWriteArrayList<>();
			this.calendar.add(ExternalStorageSource.defaultInstance());
		}
		return this.calendar;
	}

	public List<ExternalStorageSource> getCms() {
		if (null == this.cms) {
			this.cms = new CopyOnWriteArrayList<>();
			this.cms.add(ExternalStorageSource.defaultInstance());
		}
		return this.cms;
	}

	public List<ExternalStorageSource> getBbs() {
		if (null == this.bbs) {
			this.bbs = new CopyOnWriteArrayList<>();
			this.bbs.add(ExternalStorageSource.defaultInstance());
		}
		return this.bbs;
	}

	public List<ExternalStorageSource> getTeamwork() {
		if (null == this.teamwork) {
			this.teamwork = new CopyOnWriteArrayList<>();
			this.teamwork.add(ExternalStorageSource.defaultInstance());
		}
		return this.teamwork;
	}

	public List<ExternalStorageSource> getStructure() {
		if (null == this.structure) {
			this.structure = new CopyOnWriteArrayList<>();
			this.structure.add(ExternalStorageSource.defaultInstance());
		}
		return this.structure;
	}

	public List<ExternalStorageSource> getIm() {
		if (null == this.im) {
			this.im = new CopyOnWriteArrayList<>();
			this.im.add(ExternalStorageSource.defaultInstance());
		}
		return this.im;
	}

	public List<ExternalStorageSource> getGeneral() {
		if (null == this.general) {
			this.general = new CopyOnWriteArrayList<>();
			this.general.add(ExternalStorageSource.defaultInstance());
		}
		return this.general;
	}

	public List<ExternalStorageSource> getCustom() {
		if (null == this.custom) {
			this.custom = new CopyOnWriteArrayList<>();
			this.custom.add(ExternalStorageSource.defaultInstance());
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
