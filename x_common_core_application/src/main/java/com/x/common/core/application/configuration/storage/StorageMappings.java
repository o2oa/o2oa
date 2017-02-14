package com.x.common.core.application.configuration.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

public class StorageMappings extends ConcurrentHashMap<StorageType, CopyOnWriteArrayList<StorageMapping>> {

	private static final long serialVersionUID = 2056028069887353406L;

	public StorageMapping get(StorageType storageType, String name) {
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

	public StorageMapping randomWithWeight(StorageType storageType) throws Exception {
		List<StorageMapping> availables = new ArrayList<>();
		CopyOnWriteArrayList<StorageMapping> list = this.get(storageType);
		if (null != list) {
			for (StorageMapping o : list) {
				if (o.getEnable()) {
					availables.add(o);
				}
			}
		}
		if (availables.isEmpty()) {
			throw new Exception("storageType{:" + storageType + " has none available storage.");
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