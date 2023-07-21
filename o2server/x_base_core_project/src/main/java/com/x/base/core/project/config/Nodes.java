package com.x.base.core.project.config;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.io.FileUtils;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class Nodes extends ConcurrentSkipListMap<String, Node> {

	private static final long serialVersionUID = 1915588470385018748L;

	public Nodes() throws Exception {
		super();
	}

	private CenterServers centerServers;

	public CenterServers centerServers() throws Exception {
		if (centerServers == null) {
			synchronized (Nodes.class) {
				if (centerServers == null) {
					centerServers = new CenterServers(this);
				}
			}
		}
		return centerServers;
	}

	private ApplicationServers applicationServers;

	public ApplicationServers applicationServers() throws Exception {
		if (applicationServers == null) {
			synchronized (Nodes.class) {
				if (applicationServers == null) {
					applicationServers = new ApplicationServers(this);
				}
			}
		}
		return applicationServers;
	}

	private WebServers webServers;

	public WebServers webServers() throws Exception {
		if (webServers == null) {
			synchronized (Nodes.class) {
				if (webServers == null) {
					webServers = new WebServers(this);
				}
			}
		}
		return webServers;
	}

	private DataServers dataServers;

	public DataServers dataServers() throws Exception {
		if (dataServers == null) {
			synchronized (Nodes.class) {
				if (dataServers == null) {
					dataServers = new DataServers(this);
				}
			}
		}
		return dataServers;
	}

	private StorageServers storageServers;

	public StorageServers storageServers() throws Exception {
		if (storageServers == null) {
			synchronized (Nodes.class) {
				if (storageServers == null) {
					storageServers = new StorageServers(this);
				}
			}
		}
		return storageServers;
	}

	public String primaryCenterNode() {
		Optional<Entry<String, Node>> optional = this.entrySet().stream().findFirst();
		if (optional.isPresent()) {
			return optional.get().getKey();
		}
		return null;
	}

	public void save() throws Exception {
		for (Entry<String, Node> en : this.entrySet()) {
			File file = new File(Config.dir_config(), "node_" + en.getKey() + ".json");
			FileUtils.write(file, XGsonBuilder.toJson(en.getValue()), DefaultCharset.charset);
		}
	}

}
