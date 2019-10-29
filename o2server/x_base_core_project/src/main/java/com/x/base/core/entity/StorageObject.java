package com.x.base.core.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Objects;

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
import org.apache.commons.vfs2.provider.ftps.FtpsFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.webdav.WebdavFileSystemConfigBuilder;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.tools.DefaultCharset;

@MappedSuperclass
public abstract class StorageObject extends SliceJpaObject {

	private static FileSystemManager FILESYSTEMANAGERINSTANCE;

	private FileSystemManager getFileSystemManager() throws Exception {
		if (FILESYSTEMANAGERINSTANCE == null) {
			synchronized (StorageObject.class) {
				if (FILESYSTEMANAGERINSTANCE == null) {
					StandardFileSystemManager fs = new StandardFileSystemManager();
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

	abstract public Boolean getDeepPath();

	abstract public void setDeepPath(Boolean deepPath);

	@Transient
	private byte[] bytes;

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	/** 将内容导入到bytes字段，用于进行导入导出 */
	public Long dumpContent(StorageMapping mapping) throws Exception {
		long length = -1L;
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			length = this.readContent(mapping, output);
			if (length < 0) {
				this.setBytes(new byte[] {});
			} else {
				this.setBytes(output.toByteArray());
			}
		}
		return length;
	}

	/** 将导入的字节进行保存 */
	public Long saveContent(StorageMapping mapping, byte[] bytes, String name) throws Exception {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
			return saveContent(mapping, bais, name);
		}
	}

	/** 将导入的流进行保存 */
	public Long saveContent(StorageMapping mapping, InputStream input, String name) throws Exception {
		this.setName(name);
		this.setDeepPath(mapping.getDeepPath());
		this.setExtension(StringUtils.lowerCase(FilenameUtils.getExtension(name)));
		return this.updateContent(mapping, input);
	}

	/** 更新Content内容 */
	public Long updateContent(StorageMapping mapping, byte[] bytes) throws Exception {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
			return updateContent(mapping, bais);
		}
	}

	/** 更新Content内容 */
	public Long updateContent(StorageMapping mapping, byte[] bytes, String name) throws Exception {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
			if (StringUtils.isNotEmpty(name)) {
				this.setName(name);
				this.setExtension(StringUtils.lowerCase(FilenameUtils.getExtension(name)));
			}
			return updateContent(mapping, bais);
		}
	}

	/** 更新Content内容 */
	public Long updateContent(StorageMapping mapping, InputStream input, String name) throws Exception {
		if (StringUtils.isNotEmpty(name)) {
			this.setName(name);
			this.setExtension(StringUtils.lowerCase(FilenameUtils.getExtension(name)));
		}
		return updateContent(mapping, input);
	}

	/** 更新Content内容 */
	public Long updateContent(StorageMapping mapping, InputStream input) throws Exception {
		long length = -1L;
		FileSystemManager manager = this.getFileSystemManager();
		String prefix = this.getPrefix(mapping);
		String path = this.path();
		if (StringUtils.isEmpty(path)) {
			throw new Exception("path can not be empty.");
		}
		FileSystemOptions options = this.getOptions(mapping);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			/* 由于可以在传输过程中取消传输,先拷贝到内存 */
			IOUtils.copyLarge(input, baos);
			try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options);
					OutputStream output = fo.getContent().getOutputStream()) {
				length = IOUtils.copyLarge(new ByteArrayInputStream(baos.toByteArray()), output);
				this.setLength(length);
				if (!Objects.equals(StorageProtocol.webdav, mapping.getProtocol())) {
					/* webdav关闭会试图去关闭commons.httpClient */
					manager.closeFileSystem(fo.getFileSystem());
				}
			}
		}
		this.setStorage(mapping.getName());
		this.setLastUpdateTime(new Date());
		return length;
	}

	/** 读出内容 */
	public byte[] readContent(StorageMapping mapping) throws Exception {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			readContent(mapping, baos);
			return baos.toByteArray();
		}
	}

	/** 将内容流出到output */
	public Long readContent(StorageMapping mapping, OutputStream output) throws Exception {
		long length = -1L;
		FileSystemManager manager = this.getFileSystemManager();
		String prefix = this.getPrefix(mapping);
		String path = this.path();
		FileSystemOptions options = this.getOptions(mapping);
		try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options)) {
			if (fo.exists() && fo.isFile()) {
				try (InputStream input = fo.getContent().getInputStream()) {
					length = IOUtils.copyLarge(input, output);
				}
			} else {
				throw new Exception(fo.getPublicURIString() + " not existed, object:" + this.toString() + ".");
			}
			manager.closeFileSystem(fo.getFileSystem());
		}
		return length;
	}

	/** 检查是否存在内容 */
	public boolean existContent(StorageMapping mapping) throws Exception {
		FileSystemManager manager = this.getFileSystemManager();
		String prefix = this.getPrefix(mapping);
		String path = this.path();
		FileSystemOptions options = this.getOptions(mapping);
		try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options)) {
			if (fo.exists() && fo.isFile()) {
				return true;
			}
			return false;
		}
	}

	/** 删除内容,同时判断上一级目录(只判断一级)是否为空,为空则删除上一级目录 */
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

	/* 取得完整访问路径的前半部分 */
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
			prefix = "ftp://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case ftps:
			// ftps://[ username[: password]@] hostname[: port][ relative-path]
			prefix = "ftps://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case cifs:
			// smb://[ username[: password]@] hostname[: port][ absolute-path]
			prefix = "smb://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case webdav:
			// webdav://[ username[: password]@] hostname[: port][ absolute-path]
			prefix = "webdav://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		default:
			break;
		}
		return prefix + (StringUtils.isEmpty(mapping.getPrefix()) ? "" : ("/" + mapping.getPrefix()));
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
			FtpFileSystemConfigBuilder ftpBuilder = FtpFileSystemConfigBuilder.getInstance();
			/*
			 * 如果使用被动模式在阿里云centos7下会经常性出现无法连接 Caused by: java.net.ConnectException:
			 * Connection timed out (Connection timed out) at
			 * java.net.PlainSocketImpl.socketConnect(Native Method) at
			 * java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:350)
			 * at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.
			 * java:206) at
			 * java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188) at
			 * java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392) at
			 * java.net.Socket.connect(Socket.java:589)
			 */
			ftpBuilder.setPassiveMode(opts, Config.vfs().getFtp().getPassive());
			// builder.setPassiveMode(opts, false);
			// builder.setPassiveMode(opts, true);
			/** 强制不校验IP */
			ftpBuilder.setRemoteVerification(opts, false);
			// FtpFileType.BINARY is the default
			ftpBuilder.setFileType(opts, FtpFileType.BINARY);
			ftpBuilder.setConnectTimeout(opts, 10000);
			ftpBuilder.setSoTimeout(opts, 10000);
			ftpBuilder.setControlEncoding(opts, DefaultCharset.name);
			break;
		case ftps:
			FtpsFileSystemConfigBuilder ftpsBuilder = FtpsFileSystemConfigBuilder.getInstance();
			ftpsBuilder.setPassiveMode(opts, Config.vfs().getFtp().getPassive());
			/** 强制不校验IP */
			ftpsBuilder.setRemoteVerification(opts, false);
			// FtpFileType.BINARY is the default
			ftpsBuilder.setFileType(opts, FtpFileType.BINARY);
			ftpsBuilder.setConnectTimeout(opts, 10000);
			ftpsBuilder.setSoTimeout(opts, 10000);
			ftpsBuilder.setControlEncoding(opts, DefaultCharset.name);
			break;
		case cifs:
			break;
		case webdav:
			WebdavFileSystemConfigBuilder webdavBuilder = (WebdavFileSystemConfigBuilder) WebdavFileSystemConfigBuilder
					.getInstance();
			webdavBuilder.setConnectionTimeout(opts, 10000);
			webdavBuilder.setSoTimeout(opts, 10000);
			webdavBuilder.setUrlCharset(opts, DefaultCharset.name);
			// webdavBuilder.setVersioning(opts, true);
			break;
		default:
			break;
		}
		return opts;
	}

}