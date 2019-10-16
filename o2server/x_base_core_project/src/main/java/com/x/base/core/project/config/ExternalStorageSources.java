package com.x.base.core.project.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.x.base.core.entity.StorageType;

public class ExternalStorageSources extends ConcurrentHashMap<StorageType, CopyOnWriteArrayList<ExternalStorageSource>> {

	private static final long serialVersionUID = 8229115124625865737L;

	public static ExternalStorageSources defaultInstance() {

		ExternalStorageSources externalStorages = new ExternalStorageSources();

		return externalStorages;
	}

	public ExternalStorageSources() {
		super();
	}

}
