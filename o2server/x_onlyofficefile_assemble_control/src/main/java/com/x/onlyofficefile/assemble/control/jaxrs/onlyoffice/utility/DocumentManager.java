package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.x_onlyofficefile_assemble_control;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileType;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.glassfish.jersey.http.HttpHeaders;
import org.primeframework.jwt.Signer;
import org.primeframework.jwt.Verifier;
import org.primeframework.jwt.domain.JWT;
import org.primeframework.jwt.hmac.HMACSigner;
import org.primeframework.jwt.hmac.HMACVerifier;

public class DocumentManager {
	private static final Logger logger = LoggerFactory.getLogger(DocumentManager.class);
	private static HttpServletRequest request;
	private static FileSystemManager fsManager = null;

	static {
		try {
			fsManager = VFS.getManager();
		} catch (FileSystemException e) {
			logger.error( e);
		}
	}

	public static void Init(HttpServletRequest req, HttpServletResponse resp) {
		request = req;
	}

	public static void Init(HttpServletRequest req) {
		request = req;
	}

	public static List<String> getEditedExts() {
		try {
			String exts = ConfigManager.init(Config.base()).getDocserviceEditedDocs();
			return Arrays.asList(exts.split("\\|"));
		} catch (Exception ex) {
			return null;
		}
	}

	public static List<String> getConvertExts() {
		try {
			String exts = ConfigManager.init(Config.base()).getDocserviceConvertDocs();
			return Arrays.asList(exts.split("\\|"));
		} catch (Exception ex) {
			return null;
		}
	}

	public static String curUserHostAddress(String userAddress) {
		if (userAddress == null) {
			try {
				userAddress = InetAddress.getLocalHost().getHostAddress();
			} catch (Exception ex) {
				userAddress = "";
			}
		}

		return userAddress.replaceAll("[^0-9a-zA-Z.=]", "_");
	}

	public static String filesRootPath(String filePath) {
		if (filePath == null) {
			filePath = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		}
		String directory = "";
		try {
			String storagePath;
			storagePath = ConfigManager.init(Config.base()).getStorageFolder();
			if (storagePath.trim().equalsIgnoreCase("")) {
				File file = new File(Config.base(), "local/repository/storage/onlyofficeFile");
				storagePath = file.getAbsolutePath();
			}
			directory = storagePath + File.separator + filePath + File.separator;
			FileObject fileObj = fsManager.resolveFile(directory);
			if (!fileObj.exists()) {
				fileObj.createFolder();
				fileObj.close();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return directory;
	}

	public static String storagePath(String fileName, String userfilepath) {
		String directory = filesRootPath(userfilepath);
		return directory + fileName;
	}

	public static String historyDir(String storagePath) {
		return storagePath += "-hist";
	}

	public static String versionDir(String histPath, Integer version) {
		return histPath + File.separator + Integer.toString(version);
	}

	public static String versionDir(String fileName, String userAddress, Integer version) {
		return versionDir(historyDir(storagePath(fileName, userAddress)), version);
	}

	public static long getFileSize(String filePath) {
		FileObject fileObj = null;
		try {
			fileObj = fsManager.resolveFile(filePath);
			if (fileObj == null) {
				return -1;
			} else {
				return fileObj.getContent().getSize();
			}

		} catch (FileSystemException e) {
			logger.error(e);
		}
		return -1;
	}

	public static String getServerUrl(){
		String sep = "/";
		String serverPath = Objects.toString(ConfigManager.init("").getDownLoadUrl(), "");
		if(StringUtils.isNotBlank(serverPath)){
			if(serverPath.endsWith(sep)){
				serverPath = serverPath.substring(0, serverPath.length()-1);
			}
			if(!serverPath.contains(x_onlyofficefile_assemble_control.class.getSimpleName())){
				serverPath = serverPath + sep + x_onlyofficefile_assemble_control.class.getSimpleName();
			}
		}else{
			String referer = request.getHeader(HttpHeaders.REFERER);
			if(StringUtils.isNotBlank(referer)) {
				try {
					URL url = new URL(referer);
					int port = url.getPort();
					serverPath = url.getProtocol() + "://" + url.getHost() + (
							(port < 0 || port == 80 || port == 443) ? "" : ":" + port)
							+ sep + x_onlyofficefile_assemble_control.class.getSimpleName();
				} catch (MalformedURLException e) {
					logger.debug(e.getMessage());
				}
			}
		}
		return serverPath;
	}


	public static String getFileUriById(String id) {
		String serverPath = getServerUrl();
		return serverPath + "/jaxrs/onlyofficefile/file/" + id;
	}

	public static String getFileDiffUriById(String id) {
		String serverPath = getServerUrl();
		return serverPath + "/jaxrs/onlyofficefile/file/diff/" + id;
	}


	public static String getFilePathById(String fileName, String version) {
		return versionDir(fileName, null, Integer.valueOf(version));
	}

	public static String getCallback(String fileName) {
		String serverPath = getServerUrl();
		return serverPath + "/IndexServlet?type=track&appId=" + x_onlyofficefile_assemble_control.class.getSimpleName() +
				"&fileName=" + URLEncoder.encode(fileName, DefaultCharset.charset);
	}

	public static String getCallBack(String fileName, String appId, String authToken) {
		String serverPath = getServerUrl();
		String query = serverPath + "/IndexServlet?type=track&appId=" + URLEncoder.encode(appId, DefaultCharset.charset) +
				"&fileName=" + URLEncoder.encode(fileName, DefaultCharset.charset) +
				"&authToken=" + URLEncoder.encode(authToken, DefaultCharset.charset);
		return query;
	}

	public static String getInternalExtension(FileType fileType) {
		switch (fileType){
			case Text:
				return ".docx";
			case Spreadsheet:
				return ".xlsx";
			case Presentation:
				return ".pptx";
			default:
				return ".docx";
		}
	}

	public static String markFilesRootPath() {
		String storagePath;
		try {
			storagePath = ConfigManager.init(Config.base()).getStorageFolderMark();
			String directory = storagePath + File.separator;
			createFolder(directory);
			return directory;
		} catch (Exception e) {
			logger.warn("jwt加密错误：{}", e.getMessage());
			return "";
		}

	}

	public static String createToken(Map<String, Object> payloadClaims) {
		try {
			Signer signer = HMACSigner.newSHA256Signer(getTokenSecret());
			JWT jwt = new JWT();
			for (String key : payloadClaims.keySet()) {
				jwt.addClaim(key, payloadClaims.get(key));
			}
			return JWT.getEncoder().encode(jwt, signer);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static JWT readToken(String token) {
		try {
			Verifier verifier = HMACVerifier.newVerifier(getTokenSecret());
			return JWT.getDecoder().decode(token, verifier);
		} catch (Exception exception) {
			return null;
		}
	}

	public static Boolean tokenEnabled() {
		String secret = getTokenSecret();
		return secret != null && !secret.isEmpty();
	}

	public static String getTokenSecret() {
		try {
			return ConfigManager.init(Config.base()).getSecret();
		} catch (Exception e) {
			return "";
		}
	}

	public static void writeStringToFile(String filePath, String data, String encoding) throws IOException {
		if (StringUtils.isEmpty(filePath)) {
			throw new IOException("File '" + filePath + "' is empty.");
		}
		FileObject fileObj = null;
		OutputStream out = null;
		try {

			fileObj = fsManager.resolveFile(filePath);
			if (!fileObj.exists()) {
				fileObj.createFile();
			} else {
				if (org.apache.commons.vfs2.FileType.FOLDER.equals(fileObj.getType())) {
					throw new IOException("Write fail. File '" + filePath + "' exists but is a directory");
				}
			}
			out = fileObj.getContent().getOutputStream();
			IOUtils.write(data, out, encoding);
		} catch (FileSystemException e) {
			throw new IOException("File '" + filePath + "' resolveFile fail.");
		} finally {
			out.flush();
			out.close();
			IOUtils.closeQuietly(out);
			if (fileObj != null) {
				fileObj.close();
			}
		}

	}

	public static FileObject createFolder(String directory) {
		FileObject fileObj = null;
		try {
			fileObj = fsManager.resolveFile(directory);
			if (!fileObj.exists()) {
				fileObj.createFolder();
			}

		} catch (FileSystemException e) {
			e.printStackTrace();

		}
		return fileObj;
	}

}
