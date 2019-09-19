package com.x.base.core.project.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class JarTools {

	private JarTools() {
	};

	public static void unjar(byte[] bytes, String sub, String dist, boolean force) {
		unjar(bytes, sub, new File(dist), force);
	}

	public static void unjar(byte[] bytes, String sub, File dist, boolean force) {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				JarInputStream jis = new JarInputStream(bais)) {
			FileUtils.forceMkdir(dist);
			JarEntry entry = null;
			while ((entry = jis.getNextJarEntry()) != null) {
				if (!StringUtils.startsWith(entry.getName(), sub)) {
					continue;
				}
				if (StringUtils.replace(entry.getName(), sub, "").length() < 2) {
					continue;
				}
				if (entry.isDirectory()) {
					FileUtils.forceMkdir(new File(dist, entry.getName()));
				} else {
					File file = new File(dist, entry.getName());
					if (file.exists() && force) {
						FileUtils.forceDelete(file);
					}
					if (!file.exists()) {
						try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
							int size;
							byte[] buffer = new byte[2048];
							while ((size = jis.read(buffer, 0, buffer.length)) != -1) {
								baos.write(buffer, 0, size);
							}
							FileUtils.writeByteArrayToFile(new File(dist, entry.getName()), baos.toByteArray());
						}
					}
				}
				jis.closeEntry();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unjar(String source, String sub, String dist, boolean force) {
		unjar(new File(source), sub, new File(dist), force);
	}

	public static void unjar(File source, String sub, File dist, boolean force) {
		try (JarFile jarFile = new JarFile(source)) {
			FileUtils.forceMkdir(dist);
			Enumeration<? extends JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry entry = entrys.nextElement();
				String name = entry.getName();
				if (!StringUtils.startsWith(name, sub)) {
					continue;
				}
				name = name.replace(sub, "").trim();
				if (name.length() < 2) {
					continue;
				}
				if (entry.isDirectory()) {
					File dir = new File(dist, name);
					FileUtils.forceMkdir(dir);
				} else {
					File file = new File(dist, name);
					if (file.exists() && force) {
						file.delete();
					}
					if (!file.exists()) {
						try (InputStream in = jarFile.getInputStream(entry)) {
							FileUtils.copyInputStreamToFile(in, file);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] jar(File source) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				JarOutputStream jos = new JarOutputStream(baos)) {
			jos.setMethod(JarOutputStream.DEFLATED);
			jos.setLevel(5);
			for (File o : source.listFiles()) {
				write(o, "", jos);
			}
			/* 这样可以避免flush,会导致少最后的2K数据 */
			jos.close();
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] jar(String source) {
		return jar(new File(source));
	}

	public static void jar(File source, File dist) {
		try (FileOutputStream fos = new FileOutputStream(dist); JarOutputStream jos = new JarOutputStream(fos)) {
			jos.setMethod(JarOutputStream.DEFLATED);
			jos.setLevel(5);
			for (File o : source.listFiles()) {
				write(o, "", jos);
			}
			jos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public static void jar(File source, File dist, String[] excludes) {
//		try (FileOutputStream fos = new FileOutputStream(dist); JarOutputStream jos = new JarOutputStream(fos)) {
//			jos.setMethod(JarOutputStream.DEFLATED);
//			jos.setLevel(5);
//			for (File o : source.listFiles()) {
//				System.out.println(FilenameUtils.equals(filename1, filename2));
//				write(o, "", jos);
//			}
//			jos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static void jar(String source, String dist) {
		jar(new File(source), new File(dist));
	}

	public static byte[] jar(List<File> files) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				JarOutputStream jos = new JarOutputStream(baos)) {
			jos.setMethod(JarOutputStream.DEFLATED);
			jos.setLevel(5);
			for (File o : files) {
				write(o, "", jos);
			}
			/* 这样可以避免flush,会导致少最后的2K数据 */
			jos.close();
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void jar(List<File> files, File file) {
		try (FileOutputStream fos = new FileOutputStream(file); JarOutputStream jos = new JarOutputStream(fos)) {
			jos.setMethod(JarOutputStream.DEFLATED);
			jos.setLevel(5);
			for (File o : files) {
				write(o, "", jos);
			}
			/* 这样可以避免flush,会导致少最后的2K数据 */
			jos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void write(File file, String parentPath, JarOutputStream jos) {
		if (file.exists()) {
			if (file.isDirectory()) {
				parentPath += file.getName() + File.separator;
				for (File f : file.listFiles()) {
					write(f, parentPath, jos);
				}
			} else {
				try (FileInputStream fis = new FileInputStream(file)) {
					String name = parentPath + file.getName();
					/* 必须，否则打出来的包无法部署在Tomcat上 */
					name = StringUtils.replace(name, "\\", "/");
					JarEntry entry = new JarEntry(name);
					entry.setMethod(JarEntry.DEFLATED);
					jos.putNextEntry(entry);
					byte[] content = new byte[2048];
					int len;
					while ((len = fis.read(content)) != -1) {
						jos.write(content, 0, len);
						jos.flush();
					}
					jos.closeEntry();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(StringUtils.startsWith("asdfasdf", ""));
		System.out.println(StringUtils.startsWith("asdfasdf", null));
	}

}
