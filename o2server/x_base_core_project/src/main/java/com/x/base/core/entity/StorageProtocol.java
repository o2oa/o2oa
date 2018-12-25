package com.x.base.core.entity;

public enum StorageProtocol {
	bzip2, file, ftp, ftps, gzip, hdfs, http, https, jar, ram, res, sftp, tar, temp, webdav, zip, cifs, mime;
	public static final int length = JpaObject.length_16B;
}
