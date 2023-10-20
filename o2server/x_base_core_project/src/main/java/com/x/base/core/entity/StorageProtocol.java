package com.x.base.core.entity;

public enum StorageProtocol {
	ftp, ftps, webdav, cifs, file, sftp, hdfs, ali, s3, min, cos;

	public static final int length = JpaObject.length_16B;
}
