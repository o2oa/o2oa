package com.x.base.core.project.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ExtraDataRecord;
import net.lingala.zip4j.model.FileHeader;

/**
 * zip工具类
 * 
 * @author sword
 */
public class ZipTools {

	private static final int ZIP_MAGIC_NUMBER_1 = 0x50;
	private static final int ZIP_MAGIC_NUMBER_2 = 0x4B;

	private static final int BUFFER_SIZE = 2 * 1024;
	private static final List<String> DEFAULT_IGNORE_LIST = List.of("__MACOSX", ".DS_Store");

	public static boolean isZipFile(Path path) {
		if (Files.exists(path) && Files.isRegularFile(path)) {
			try (InputStream in = Files.newInputStream(path)) {
				int magicNumber1 = in.read();
				int magicNumber2 = in.read();
				return magicNumber1 == ZIP_MAGIC_NUMBER_1 && magicNumber2 == ZIP_MAGIC_NUMBER_2;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void unZip(File source, List<String> ignoreList, File dist, boolean asNew, Charset charset) {
		try {
			if (ignoreList == null) {
				ignoreList = new ArrayList<>(DEFAULT_IGNORE_LIST);
			} else {
				ignoreList.addAll(DEFAULT_IGNORE_LIST);
			}
			ZipFile zipFile = new ZipFile(source);
			if (charset == null) {
				charset = DefaultCharset.charset;
			}
			zipFile.setCharset(charset);
			List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
			for (FileHeader fileHeader : fileHeaderList) {
				if (isFromExtraData(fileHeader) && DefaultCharset.charset.name() == charset.name()) {
					unZip(source, ignoreList, dist, asNew, DefaultCharset.charset_gbk);
					return;
				}
				String name = fileHeader.getFileName();
				if (name.length() < 2) {
					continue;
				}
				if (isMember(name, ignoreList)) {
					continue;
				}
				if (fileHeader.isDirectory()) {
					File dir = new File(dist, name);
					if (dir.exists() && !dist.getAbsolutePath().startsWith(dir.getAbsolutePath())
							&& name.indexOf("/") == name.lastIndexOf("/") && asNew) {
						FileUtils.cleanDirectory(dir);
					}
					FileUtils.forceMkdir(dir);
				} else {
					zipFile.extractFile(fileHeader, dist.getAbsolutePath());
				}
			}
			fileHeaderList.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unZip(File source, List<String> ignoreList, File dist, List<String> dist2List, File dist2,
			boolean asNew, Charset charset) {
		try {
			if (ignoreList == null) {
				ignoreList = new ArrayList<>(DEFAULT_IGNORE_LIST);
			} else {
				ignoreList.addAll(DEFAULT_IGNORE_LIST);
			}
			ZipFile zipFile = new ZipFile(source);
			if (charset == null) {
				charset = DefaultCharset.charset;
			}
			zipFile.setCharset(charset);
			List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
			for (FileHeader fileHeader : fileHeaderList) {
				if (isFromExtraData(fileHeader) && DefaultCharset.charset.name() == charset.name()) {
					unZip(source, ignoreList, dist, dist2List, dist2, asNew, DefaultCharset.charset_gbk);
					return;
				}
				String name = fileHeader.getFileName();
				if (name.length() < 2) {
					continue;
				}
				if (isMember(name, ignoreList)) {
					continue;
				}
				if (fileHeader.isDirectory()) {
					if (name.indexOf("/") == name.lastIndexOf("/") && asNew) {
						File dir = new File(dist, name);
						if (isMember(name, dist2List)) {
							dir = new File(dist2, name);
						}
						if (dir.exists()) {
							FileUtils.cleanDirectory(dir);
						}
						FileUtils.forceMkdir(dir);
					}
				} else {
					String path = dist.getAbsolutePath();
					if (isMember(name, dist2List)) {
						path = dist2.getAbsolutePath();
					}
					zipFile.extractFile(fileHeader, path);
				}
			}
			fileHeaderList.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isMember(final String name, List<String> list) {
		if (ListTools.isEmpty(list)) {
			return true;
		}
		if (StringUtils.isBlank(name)) {
			return false;
		}
		return list.stream().filter(s -> (name.startsWith(s) || name.startsWith("/" + s))).findFirst().isPresent();
	}

	public static boolean isFromExtraData(FileHeader fileHeader) {
		if (fileHeader.getExtraDataRecords() != null) {
			for (ExtraDataRecord extraDataRecord : fileHeader.getExtraDataRecords()) {
				long identifier = extraDataRecord.getHeader();
				if (identifier == 0x7075) {
					byte[] bytes = extraDataRecord.getData();
					ByteBuffer buffer = ByteBuffer.wrap(bytes);
					byte version = buffer.get();
					assert (version == 1);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 压缩成ZIP 方法1
	 * 
	 * @param sourceFile 待压缩文件夹
	 * @param out        压缩包输出流
	 * @param fileList   压缩的文件列表，可以为null
	 * @return boolean
	 */
	public static boolean toZip(File sourceFile, OutputStream out, List<String> fileList) {
		try (ZipOutputStream zos = new ZipOutputStream(out)) {
			compress(sourceFile, zos, "", fileList);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 压缩成ZIP 方法2
	 * 
	 * @param srcFiles 需要压缩的文件列表
	 * @param out      压缩文件输出流
	 * @throws RuntimeException 压缩失败会抛出运行时异常
	 */
	public static void toZip(List<File> srcFiles, OutputStream out) throws RuntimeException {
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(out);
			for (File srcFile : srcFiles) {
				byte[] buf = new byte[BUFFER_SIZE];
				zos.putNextEntry(new ZipEntry(srcFile.getName()));
				int len;
				FileInputStream in = new FileInputStream(srcFile);
				while ((len = in.read(buf)) != -1) {
					zos.write(buf, 0, len);
				}
				zos.closeEntry();
				in.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("zip error from ZipUtils", e);
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 递归压缩方法
	 * 
	 * @param sourceFile 源文件
	 * @param zos        zip输出流
	 * @param name       压缩后的名称
	 * @throws Exception
	 */
	private static void compress(File sourceFile, ZipOutputStream zos, String name, List<String> fileList)
			throws Exception {
		byte[] buf = new byte[BUFFER_SIZE];
		if (sourceFile.isFile()) {
			// 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
			if (StringUtils.isBlank(name)) {
				name = sourceFile.getName();
			}
			if (sourceFile != null) {
				if (name.startsWith(File.separator)) {
					fileList.add(name.substring(1));
				} else {
					fileList.add(name);
				}
			}
			zos.putNextEntry(new ZipEntry(name));
			// copy文件到zip输出流中
			int len;
			FileInputStream in = new FileInputStream(sourceFile);
			while ((len = in.read(buf)) != -1) {
				zos.write(buf, 0, len);
			}
			zos.closeEntry();
			in.close();
		} else {
			File[] listFiles = sourceFile.listFiles();
			if (listFiles == null || listFiles.length == 0) {
				if (StringUtils.isNotBlank(name)) {
					zos.putNextEntry(new ZipEntry(name + File.separator));
					zos.closeEntry();
				}
			} else {
				for (File file : listFiles) {
					compress(file, zos, name + File.separator + file.getName(), fileList);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("===================");
		File source = new File("/Users/chengjian/dev/tmp/test/测试文件夹2");
		File dist = new File("/Users/chengjian/dev/tmp/test/测试文件夹1");
		dist = new File(dist, source.getName());
		FileUtils.copyDirectory(source, dist);
		System.out.println("===================");
	}
}
