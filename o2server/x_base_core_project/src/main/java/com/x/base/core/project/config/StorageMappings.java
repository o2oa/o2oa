package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.project.config.StorageServer.Account;
import com.x.base.core.project.tools.ListTools;

public class StorageMappings extends ConcurrentHashMap<StorageType, CopyOnWriteArrayList<StorageMapping>> {

	private static final long serialVersionUID = 2056028069887353406L;

	public StorageMappings() {
		super();
	}

	public StorageMappings(Nodes nodeConfigs) throws Exception {
		super();
		/** 填充空值 */
		for (StorageType o : StorageType.values()) {
			this.put(o, new CopyOnWriteArrayList<StorageMapping>());
		}
		StorageServers storageServers = nodeConfigs.storageServers();
		for (Entry<String, StorageServer> en : storageServers.entrySet()) {
			String node = en.getKey();
			StorageServer server = en.getValue();
			for (Account account : server.getCalculatedAccounts()) {
				StorageMapping o = new StorageMapping();
				o.setEnable(true);
				o.setHost(node);
				o.setPassword(account.getPassword());
				// o.setName(account.getName());
				o.setPort(en.getValue().getPort());
				o.setUsername(account.getUsername());
				o.setProtocol(account.getProtocol());
				o.setWeight((account.getWeight() == null) ? 100 : account.getWeight());
				o.setName(server.getName());
				StorageType type = StorageType.valueOf(account.getUsername());
				this.get(type).add(o);
			}
		}
	}

	private <T extends StorageObject> StorageType getStorageType(Class<T> clz) throws Exception {
		Storage o = clz.getAnnotation(Storage.class);
		if (null == o) {
			throw new Exception(
					"can not find " + Storage.class.getName() + " annotation in class: " + clz.getName() + ".");
		}
		StorageType type = o.type();
		if (null == type) {
			throw new Exception("can not find storageType in class: " + clz.getName() + ".");
		}
		return type;
	}

	public <T extends StorageObject> StorageMapping get(Class<T> clz, String name) throws Exception {
		StorageType type = this.getStorageType(clz);
		return this.get(type, name);
	}

	private StorageMapping get(StorageType storageType, String name) {
		StorageMapping storageMapping = null;
		CopyOnWriteArrayList<StorageMapping> list = this.get(storageType);
		if (null != list) {
			for (StorageMapping o : list) {
				if (StringUtils.equals(o.getName(), name)) {
					storageMapping = o;
					break;
				}
			}
		}
		return storageMapping;
	}

	public <T extends StorageObject> StorageMapping random(Class<T> clz) throws Exception {
		StorageType type = this.getStorageType(clz);
		return this.random(type);
	}

	private StorageMapping random(StorageType type) throws Exception {
		CopyOnWriteArrayList<StorageMapping> list = this.get(type);
		if (ListTools.isEmpty(list)) {
			throw new Exception("can not get storage of " + type);
		}
		List<StorageMapping> availables = new ArrayList<>();
		for (StorageMapping o : list) {
			if (o.getEnable()) {
				availables.add(o);
			}
		}
		if (availables.isEmpty()) {
			throw new Exception("storageType{:" + type + " has none available storage.");
		}
		int total = 0;
		Random random = new Random();
		for (StorageMapping o : availables) {
			total += o.getWeight();
		}
		int rdm = random.nextInt(total);
		int current = 0;
		for (StorageMapping o : availables) {
			current += o.getWeight();
			if (rdm <= current) {
				return o;
			}
		}
		throw new Exception("randomWithWeight error can not get available storage.");
	}
}