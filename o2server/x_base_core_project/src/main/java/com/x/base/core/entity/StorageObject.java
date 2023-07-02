package com.x.base.core.entity;

import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.tools.DefaultCharset;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.cache.NullFilesCache;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftp.FtpFileType;
import org.apache.commons.vfs2.provider.ftps.FtpsFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.webdav4.Webdav4FileSystemConfigBuilder;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.FileNotFoundException;
import java.io.*;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

@MappedSuperclass
public abstract class StorageObject extends SliceJpaObject {

	private static FileSystemManager fileSystemManagerInstance;

	private synchronized FileSystemManager getFileSystemManager() throws FileSystemException {
		if (fileSystemManagerInstance == null) {
			StandardFileSystemManager fs = new StandardFileSystemManager();
			fs.setFilesCache(new NullFilesCache());
			fs.setCacheStrategy(CacheStrategy.ON_RESOLVE);
			fs.init();
			fileSystemManagerInstance = fs;

		}
		return fileSystemManagerInstance;
	}

	private static final long serialVersionUID = 7823729771901802653L;

	public static final String PATHSEPARATOR = "/";

	public static final String DELETE_OPERATE = "delete";

	public abstract String path() throws Exception;

	public abstract String getStorage();

	public abstract void setStorage(String storage);

	public abstract Long getLength();

	public abstract void setLength(Long length);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getExtension();

	public abstract void setExtension(String extension);

	public abstract Date getLastUpdateTime();

	public abstract void setLastUpdateTime(Date lastUpdateTime);

	public abstract Boolean getDeepPath();

	public abstract void setDeepPath(Boolean deepPath);

	public String path(String operate) throws Exception {
		return this.path();
	}

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
		this.setName(name);
		this.setDeepPath(mapping.getDeepPath());
		this.setExtension(StringUtils.lowerCase(StringUtils.substringAfterLast(name, ".")));
		return this.updateContent(mapping, bytes);
	}

	/** 将导入的流进行保存 */
	public Long saveContent(StorageMapping mapping, InputStream input, String name) throws Exception {
		this.setName(name);
		this.setDeepPath(mapping.getDeepPath());
		this.setExtension(StringUtils.lowerCase(StringUtils.substringAfterLast(name, ".")));
		return this.updateContent(mapping, input);
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
		if (Objects.equals(StorageProtocol.hdfs, mapping.getProtocol())) {
			return this.hdfsUpdateContent(mapping, IOUtils.toByteArray(input));
		} else {
			return this.vfsUpdateContent(mapping, input);
		}
	}

	/** 更新Content内容 */
	public Long updateContent(StorageMapping mapping, byte[] bytes) throws Exception {
		if (Objects.equals(StorageProtocol.hdfs, mapping.getProtocol())) {
			return this.hdfsUpdateContent(mapping, bytes);
		} else {
			return this.vfsUpdateContent(mapping, new ByteArrayInputStream(bytes));
		}
	}

	/**
	 * 读出内容
	 *
	 * @param mapping
	 * @return
	 * @throws Exception
	 */
	public byte[] readContent(StorageMapping mapping) throws Exception {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			readContent(mapping, baos);
			return baos.toByteArray();
		}
	}

	/**
	 * 将内容流出到output
	 *
	 * @param mapping
	 * @param output
	 * @return
	 * @throws Exception
	 */
	public Long readContent(StorageMapping mapping, OutputStream output) throws Exception {
		if (Objects.equals(mapping.getProtocol(), StorageProtocol.hdfs)) {
			return hdfsReadContent(mapping, output);
		} else {
			return vfsReadContent(mapping, output);
		}
	}

	/**
	 * 检查是否存在内容
	 *
	 * @param mapping
	 * @return
	 * @throws Exception
	 */
	public boolean existContent(StorageMapping mapping) throws Exception {
		if (Objects.equals(mapping.getProtocol(), StorageProtocol.hdfs)) {
			return hdfsExistContent(mapping);
		} else {
			return vfsExistContent(mapping);
		}
	}

	public void deleteContent(StorageMapping mapping) throws Exception {
		if (Objects.equals(mapping.getProtocol(), StorageProtocol.hdfs)) {
			hdfsDeleteContent(mapping);
		} else {
			vfsDeleteContent(mapping);
		}
	}

	/**
	 * 取得完整访问路径的前半部分
	 *
	 * @param mapping
	 * @return
	 * @throws IllegalStateException
	 * @throws UnsupportedEncodingException
	 */
	private String getPrefix(StorageMapping mapping) throws IllegalStateException, UnsupportedEncodingException {
		String prefix = "";
		if (null == mapping.getProtocol()) {
			throw new IllegalStateException("storage protocol is null.");
		}
		switch (mapping.getProtocol()) {
		case ftp:
			prefix = "ftp://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case ftps:
			prefix = "ftps://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case sftp:
			prefix = "sftp://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case cifs:
			prefix = "smb://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case webdav:
			prefix = "webdav4://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + ":"
					+ mapping.getPort();
			break;
		case ali:
			prefix = "ali://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost() + "/"
					+ mapping.getName();
			break;
		case s3:
			prefix = "s3://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" + mapping.getHost();
			if(StringUtils.isNotBlank(mapping.getName()) && !mapping.getHost().startsWith(mapping.getName())) {
				prefix = prefix + "/" + mapping.getName();
			}
			break;
		case min:
			prefix = "min://" + URLEncoder.encode(mapping.getUsername(), DefaultCharset.name) + ":"
					+ URLEncoder.encode(mapping.getPassword(), DefaultCharset.name) + "@" ;
			String split = "//";
			if(mapping.getHost().indexOf(split) > -1) {
				prefix = prefix + StringUtils.substringAfter(mapping.getHost(), split);
			}else{
				prefix = prefix + mapping.getHost();
			}
			prefix = prefix.equals("/") ? prefix + mapping.getName() : prefix + "/" + mapping.getName();
			break;
		case file:
			prefix = "file://";
			break;
		case hdfs:
			// 路径不采用带用户名的homeDirctory,直接返回
			return StringUtils.isEmpty(mapping.getPrefix()) ? "/" : ("/" + mapping.getPrefix());
		default:
			break;
		}
		String mappingPrefix = "";
		if (StringUtils.isNotBlank(mapping.getPrefix())) {
			if (mapping.getPrefix().startsWith("/")) {
				mappingPrefix = mapping.getPrefix();
			} else {
				mappingPrefix = "/" + mapping.getPrefix();
			}
			if (mappingPrefix.endsWith("/")) {
				mappingPrefix = mappingPrefix.substring(0, mappingPrefix.length() - 1);
			}
		}
		return prefix + mappingPrefix;
	}

	private FileSystemOptions getOptions(StorageMapping mapping) throws Exception {
		FileSystemOptions opts = new FileSystemOptions();
		if (null == mapping.getProtocol()) {
			throw new IllegalStateException("storage protocol is null.");
		}
		switch (mapping.getProtocol()) {
		case sftp:
			SftpFileSystemConfigBuilder sftpBuilder = SftpFileSystemConfigBuilder.getInstance();
			sftpBuilder.setConnectTimeout(opts, Duration.ofMillis(10 * 1000));
			sftpBuilder.setSessionTimeout(opts, Duration.ofMillis(30 * 1000));
			sftpBuilder.setFileNameEncoding(opts, DefaultCharset.name);
			// By default, the path is relative to the user's home directory. This can be
			// changed with:
			sftpBuilder.setStrictHostKeyChecking(opts, "no");
			sftpBuilder.setUserDirIsRoot(opts, false);
			break;
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
			ftpBuilder.setPassiveMode(opts, true);
			// 强制不校验IP
			ftpBuilder.setRemoteVerification(opts, false);
			// FtpFileType.BINARY is the default
			ftpBuilder.setFileType(opts, FtpFileType.BINARY);
			ftpBuilder.setConnectTimeout(opts, Duration.ofMillis(10 * 1000));
			ftpBuilder.setSoTimeout(opts, Duration.ofMillis(10 * 1000));
			ftpBuilder.setControlEncoding(opts, DefaultCharset.name);
			break;
		case ftps:
			FtpsFileSystemConfigBuilder ftpsBuilder = FtpsFileSystemConfigBuilder.getInstance();
			ftpsBuilder.setPassiveMode(opts, true);
			// 强制不校验IP
			ftpsBuilder.setRemoteVerification(opts, false);
			// FtpFileType.BINARY is the default
			ftpsBuilder.setFileType(opts, FtpFileType.BINARY);
			ftpsBuilder.setConnectTimeout(opts, Duration.ofMillis(10 * 1000));
			ftpsBuilder.setSoTimeout(opts, Duration.ofMillis(10 * 1000));
			ftpsBuilder.setControlEncoding(opts, DefaultCharset.name);
			break;
		case webdav:
			Webdav4FileSystemConfigBuilder webdavBuilder = Webdav4FileSystemConfigBuilder.getInstance();
			webdavBuilder.setConnectionTimeout(opts, Duration.ofMillis(10 * 1000));
			webdavBuilder.setSoTimeout(opts, Duration.ofMillis(10 * 1000));
			webdavBuilder.setUrlCharset(opts, DefaultCharset.name);
			webdavBuilder.setMaxConnectionsPerHost(opts, 200);
			webdavBuilder.setMaxTotalConnections(opts, 200);
			webdavBuilder.setFollowRedirect(opts, true);
			break;
		case min:
			MinFileSystemConfigBuilder minBuilder = MinFileSystemConfigBuilder.getInstance();
			minBuilder.setTaskTimeOut(opts, 10 * 1000L);
			String https = "https";
			if(mapping.getHost().startsWith(https)){
				minBuilder.setUseHttps(opts, true);
			}else{
				minBuilder.setUseHttps(opts, false);
			}
		default:
			break;
		}
		return opts;
	}

	private Long vfsUpdateContent(StorageMapping mapping, InputStream inputStream) throws Exception {
		String prefix = this.getPrefix(mapping);
		String path = this.path();
		if (StringUtils.isEmpty(path)) {
			throw new IllegalStateException("path can not be empty.");
		}
		FileSystemOptions options = this.getOptions(mapping);
		long length = -1L;
		FileSystemManager manager = this.getFileSystemManager();
		/*
		 * 需要进行两次判断，在前端使用nginx分发的情况下，可能同时触发多个文件的上传，多个文件同时上传可能会同时创建文件的存储目录，会在后台导致错误
		 * org.apache.commons.vfs2.FileSystemException: Could not create folder
		 * "ftp://processPlatform:***@o2.server01.com:20040/20200601/1beb018a-5009-4baa-a9ef-7e903f9d48ef".
		 * 这种情况下再次发起请求尝试获取文件可以解决这个问题.
		 */
		for (int i = 0; i < 2; i++) {
			try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options);
					OutputStream output = fo.getContent().getOutputStream()) {
				length = IOUtils.copyLarge(inputStream, output);
				this.setLength(length);
				if ((!Objects.equals(StorageProtocol.webdav, mapping.getProtocol()))
						&& (!Objects.equals(StorageProtocol.sftp, mapping.getProtocol()))
						&& (!Objects.equals(StorageProtocol.ali, mapping.getProtocol()))
						&& (!Objects.equals(StorageProtocol.s3, mapping.getProtocol()))) {
					/* webdav关闭会试图去关闭commons.httpClient */
					manager.closeFileSystem(fo.getFileSystem());
				}
				this.setStorage(mapping.getName());
				this.setLastUpdateTime(new Date());
				break;
			} catch (FileSystemException fse) {
				if (i != 0) {
					// 第一次错误先跳过,直接执行第二次.如果第二次错误那么报错.
					throw fse;
				}
			}
		}
		return length;
	}

	/**
	 * vfs读取数据
	 *
	 * @param mapping
	 * @param output
	 * @return
	 * @throws Exception
	 */
	private Long vfsReadContent(StorageMapping mapping, OutputStream output) throws Exception {
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
				throw new FileNotFoundException(
						fo.getPublicURIString() + " not existed, object:" + this.toString() + ".");
			}
			if (!Objects.equals(StorageProtocol.webdav, mapping.getProtocol())
					&& (!Objects.equals(StorageProtocol.ali, mapping.getProtocol()))
					&& (!Objects.equals(StorageProtocol.s3, mapping.getProtocol()))) {
				/* webdav关闭会试图去关闭commons.httpClient */
				manager.closeFileSystem(fo.getFileSystem());
			}
		}
		return length;
	}

	private boolean vfsExistContent(StorageMapping mapping) throws Exception {
		FileSystemManager manager = this.getFileSystemManager();
		String prefix = this.getPrefix(mapping);
		String path = this.path();
		FileSystemOptions options = this.getOptions(mapping);
		try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options)) {
			return (fo.exists() && fo.isFile());
		}
	}

	/**
	 * 删除内容,同时判断上一级目录(只判断一级)是否为空,为空则删除上一级目录
	 *
	 * @param mapping
	 * @throws Exception
	 */
	private void vfsDeleteContent(StorageMapping mapping) throws Exception {
		FileSystemManager manager = this.getFileSystemManager();
		String prefix = this.getPrefix(mapping);
		String path = this.path(DELETE_OPERATE);
		FileSystemOptions options = this.getOptions(mapping);
		try (FileObject fo = manager.resolveFile(prefix + PATHSEPARATOR + path, options)) {
			if (fo.exists() && fo.isFile()) {
				fo.delete();
				if ((!StringUtils.startsWith(path, PATHSEPARATOR)) && (StringUtils.contains(path, PATHSEPARATOR))) {
					FileObject parent = fo.getParent();
					if ((null != parent) && parent.exists() && parent.isFolder()
							&& (parent.getChildren().length == 0)) {
						parent.delete();
					}
				}
			}
			if (!Objects.equals(StorageProtocol.webdav, mapping.getProtocol())
					&& (!Objects.equals(StorageProtocol.ali, mapping.getProtocol()))
					&& (!Objects.equals(StorageProtocol.s3, mapping.getProtocol()))) {
				// webdav关闭会试图去关闭commons.httpClient
				manager.closeFileSystem(fo.getFileSystem());
			}
		}
	}

	private long hdfsUpdateContent(StorageMapping mapping, byte[] bytes) throws Exception {
		try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem
				.get(hdfsConfiguration(mapping))) {
			org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(getPrefix(mapping), this.path());
			if (fileSystem.exists(path)) {
				fileSystem.delete(path, false);
			}
			try (org.apache.hadoop.fs.FSDataOutputStream out = fileSystem.create(path)) {
				out.write(bytes);
				this.setStorage(mapping.getName());
				this.setLastUpdateTime(new Date());
				this.setLength((long) bytes.length);
			}
		}
		return bytes.length;
	}

	private Long hdfsReadContent(StorageMapping mapping, OutputStream output) throws Exception {
		long length = -1L;
		try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem
				.get(hdfsConfiguration(mapping))) {
			org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(getPrefix(mapping), this.path());
			if (fileSystem.exists(path)) {
				try (org.apache.hadoop.fs.FSDataInputStream inputStream = fileSystem.open(path)) {
					length = IOUtils.copyLarge(inputStream, output);
				}
			} else {
				throw new FileNotFoundException(path + " not existed, object:" + this.toString() + ".");
			}
		}
		return length;
	}

	private boolean hdfsExistContent(StorageMapping mapping) throws Exception {
		try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem
				.get(hdfsConfiguration(mapping))) {
			org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(getPrefix(mapping), this.path());
			return fileSystem.exists(path);
		}
	}

	private void hdfsDeleteContent(StorageMapping mapping) throws Exception {
		try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem
				.get(hdfsConfiguration(mapping))) {
			org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(getPrefix(mapping), this.path());
			if (fileSystem.exists(path)) {
				fileSystem.delete(path, false);
			}
		}
	}

	private org.apache.hadoop.conf.Configuration hdfsConfiguration(StorageMapping mapping) {
		if ((!StringUtils.equals(System.getProperty("HADOOP_USER_NAME"), mapping.getUsername()))
				&& StringUtils.isNotBlank(mapping.getUsername())) {
			System.setProperty("HADOOP_USER_NAME", mapping.getUsername());
		}
		org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
		configuration.set("fs.default.name",
				StorageProtocol.hdfs + "://" + mapping.getHost() + ":" + mapping.getPort());
		configuration.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
		return configuration;
	}

}
