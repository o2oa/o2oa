package com.x.base.core.project.config;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.project.config.StorageServer.Account;
import com.x.base.core.project.tools.ListTools;

public class StorageMappings extends ConcurrentHashMap<StorageType, CopyOnWriteArrayList<StorageMapping>> {

	private static final long serialVersionUID = 2056028069887353406L;

	private Random random = new SecureRandom();

	public StorageMappings() {
		super();
	}

	public StorageMappings(ExternalStorageSources externalStorageSources) {
		for (StorageType o : StorageType.values()) {
			CopyOnWriteArrayList<StorageMapping> list = new CopyOnWriteArrayList<>();
			for (ExternalStorageSource externalStorageSource : externalStorageSources.get(o)) {
				if (BooleanUtils.isTrue(externalStorageSource.getEnable())) {
					fillWithStore(externalStorageSources, externalStorageSource);
					StorageMapping storageMapping = new StorageMapping(externalStorageSource);
					list.add(storageMapping);
				}
			}
			this.put(o, list);
		}
	}

	private void fillWithStore(ExternalStorageSources externalStorageSources,
			ExternalStorageSource externalStorageSource) {
		String key = externalStorageSource.getStore();
		if (StringUtils.isNotBlank(key)) {
			ExternalStorageSources.Store store = externalStorageSources.getStore().get(key);
			if (null != store) {
				externalStorageSource.setHost(store.getHost());
				externalStorageSource.setName(store.getName());
				externalStorageSource.setPassword(store.getPassword());
				externalStorageSource.setUsername(store.getUsername());
				externalStorageSource.setPort(store.getPort());
				externalStorageSource.setProtocol(store.getProtocol());
			}
		}
	}

	public StorageMappings(Nodes nodeConfigs) throws Exception {
		super();
		/** 填充空值 */
		for (StorageType o : StorageType.values()) {
			this.put(o, new CopyOnWriteArrayList<>());
		}
		StorageServers storageServers = nodeConfigs.storageServers();
		for (Entry<String, StorageServer> en : storageServers.entrySet()) {
			String node = en.getKey();
			StorageServer server = en.getValue();
			for (Account account : server.getCalculatedAccounts()) {
				StorageMapping o = new StorageMapping();
				o.setHost(node);
				o.setDeepPath(server.getDeepPath());
				o.setPrefix(server.getPrefix());
				o.setName(server.getName());
				o.setPassword(account.getPassword());
				o.setPort(en.getValue().getPort());
				o.setUsername(account.getUsername());
				o.setProtocol(account.getProtocol());
				o.setWeight(account.getWeight());
				StorageType type = StorageType.valueOf(account.getUsername());
				this.get(type).add(o);
			}
		}
	}

	private <T extends StorageObject> StorageType getStorageType(Class<T> clz) {
		Storage o = clz.getAnnotation(Storage.class);
		if (null == o) {
			throw new IllegalStateException(
					"can not find " + Storage.class.getName() + " annotation in class: " + clz.getName() + ".");
		}
		StorageType type = o.type();
		if (null == type) {
			throw new IllegalStateException("can not find storageType in class: " + clz.getName() + ".");
		}
		return type;
	}

	public <T extends StorageObject> StorageMapping get(Class<T> clz, String name) {
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

	public <T extends StorageObject> StorageMapping random(Class<T> clz) {
		StorageType type = this.getStorageType(clz);
		return this.random(type);
	}

	private StorageMapping random(StorageType type) {
		CopyOnWriteArrayList<StorageMapping> list = this.get(type);
		if (ListTools.isEmpty(list)) {
			throw new IllegalStateException("can not get storage of " + type);
		}
		int total = 0;
		for (StorageMapping o : list) {
			total += o.getWeight();
		}
		int rdm = random.nextInt(total);
		int current = 0;
		for (StorageMapping o : list) {
			current += o.getWeight();
			if (rdm <= current) {
				return o;
			}
		}
		throw new IllegalStateException("randomWithWeight error can not get available storage.");
	}
}