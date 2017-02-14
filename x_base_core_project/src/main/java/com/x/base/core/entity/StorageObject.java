package com.x.base.core.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.cache.NullFilesCache;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftp.FtpFileType;

import com.x.base.core.project.server.StorageMapping;

@MappedSuperclass
public abstract class StorageObject extends SliceJpaObject {

	private static FileSystemManager FILESYSTEMANAGERINSTANCE;

	private FileSystemManager getFileSystemManager() throws Exception {
		if (FILESYSTEMANAGERINSTANCE == null) {
			synchronized (StorageObject.class) {
				if (FILESYSTEMANAGERINSTANCE == null) {
					StandardFileSystemManager fs = new StandardFileSystemManager();
					// DefaultFileSystemManager fs = new
					// DefaultFileSystemManager();
					// File file = new File(Config.base(), "local/temp/vfs");
					// FileUtils.forceMkdir(file);
					// fs.setTemporaryFileStore(new
					// DefaultFileReplicator(file));
					fs.setFilesCache(new NullFilesCache());
					fs.setCacheStrategy(CacheStrategy.ON_RESOLVE);
					fs.init();
					FILESYSTEMANAGERINSTANCE = fs;
				}
			}
		}
		return FILESYSTEMANAGERINSTANCE;
	}

	private static final long serialVersionUID = 7823729771901802653L;

	public static final String PATHSEPARATOR = "/";

	abstract public String path() throws Exception;

	abstract public String getStorage();

	abstract public void setStorage(String storage);

	abstract public Long getLength();

	abstract public void setLength(Long length);

	abstract public String getName();

	abstract public void setName(String name);

	abstract public String getExtension();

	abstract public void setExtension(String extension);

	abstract public Date getLastUpdateTime();

	abstract public void setLastUpdateTime(Date lastUpdateTime);

	@Transient
	private byte[] bytes;

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	/** 将内容导入到bytes字段，用于进行导入导出 */
	public void dumpContent(StorageMapping mapping) throws Exception {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			this.readContent(mapping, output);
			this.setBytes(output.toByteArray());
		}
	}

	/** 将导入的流进行保存 */
	public void saveContent(StorageMapping mapping, InputStream input, String name) throws Exception {
		this.setName(name);
		this.setExtension(StringUtils.lowerCase(FilenameUtils.getExtension(name)));
		this.updateContent(mapping, input);
	}

	/** 更新Content内容 */
	public void updateContent(StorageMapping mapping, InputStream input) throws Exception {
		FileSystemManager manager = this.getFileSystemManager();
		String prefix = this.getPrefix(mapping);
		String path = this.path();
		if (StringUtils.isEmpty(path)) {
			throw new Exception("path can not be empty.");
		}
		FileSystemOptions options = this.getOptions(mapping);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			/* 由于可以在传输过程中取消传输,先拷贝到内存 */
			IOUtils.copy(input, baos);
			try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options);
					OutputStream output = fo.getContent().getOutputStream()) {
				int length = IOUtils.copy(new ByteArrayInputStream(baos.toByteArray()), output);
				this.setLength((long) length);
				manager.closeFileSystem(fo.getFileSystem());
			}
		}
		this.setStorage(mapping.getName());
		this.setLastUpdateTime(new Date());
	}

	/** 将内容流出到output */
	public void readContent(StorageMapping mapping, OutputStream output) throws Exception {
		FileSystemManager manager = this.getFileSystemManager();
		String prefix = this.getPrefix(mapping);
		String path = this.path();
		FileSystemOptions options = this.getOptions(mapping);
		try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options)) {
			if (fo.exists() && fo.isFile()) {
				try (InputStream input = fo.getContent().getInputStream()) {
					IOUtils.copy(input, output);
				}
			}
			manager.closeFileSystem(fo.getFileSystem());
		}
	}

	/** 删除内容 */
	public void deleteContent(StorageMapping mapping) throws Exception {
		FileSystemManager manager = this.getFileSystemManager();
		String prefix = this.getPrefix(mapping);
		String path = this.path();
		FileSystemOptions options = this.getOptions(mapping);
		try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options)) {
			if (fo.exists() && fo.isFile()) {
				fo.delete();
				if ((!StringUtils.startsWith(path, PATHSEPARATOR)) && (StringUtils.contains(path, PATHSEPARATOR))) {
					FileObject parent = fo.getParent();
					if ((null != parent) && parent.exists() && parent.isFolder()) {
						if (parent.getChildren().length == 0) {
							parent.delete();
						}
					}
				}
			}
			manager.closeFileSystem(fo.getFileSystem());
		}
	}

	private String getPrefix(StorageMapping mapping) throws Exception {
		String prefix = "";
		if (null == mapping.getProtocol()) {
			throw new Exception("storage protocol is null.");
		}
		switch (mapping.getProtocol()) {
		// bzip2,file, ftp, ftps, gzip, hdfs, http, https, jar, ram, res, sftp,
		// tar, temp, webdav, zip, cifs, mime;
		case ftp:
			// ftp://[ username[: password]@] hostname[: port][ relative-path]
			prefix = "ftp://" + mapping.getUsername() + ":" + mapping.getPassword() + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case cifs:
			// smb://[ username[: password]@] hostname[: port][ absolute-path]
			prefix = "smb://" + mapping.getUsername() + ":" + mapping.getPassword() + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		default:
			break;
		}
		return prefix;
	}

	private FileSystemOptions getOptions(StorageMapping mapping) throws Exception {
		FileSystemOptions opts = new FileSystemOptions();
		if (null == mapping.getProtocol()) {
			throw new Exception("storage protocol is null.");
		}
		switch (mapping.getProtocol()) {
		// bzip2,file, ftp, ftps, gzip, hdfs, http, https, jar, ram, res, sftp,
		// tar, temp, webdav, zip, cifs, mime;
		case ftp:
			FtpFileSystemConfigBuilder builder = FtpFileSystemConfigBuilder.getInstance();
			builder.setPassiveMode(opts, true);
			// FtpFileType.BINARY is the default
			builder.setFileType(opts, FtpFileType.BINARY);
			builder.setConnectTimeout(opts, 5000);
			builder.setControlEncoding(opts, "UTF-8");
			break;
		case cifs:
			break;
		default:
			break;
		}
		return opts;
	}

}