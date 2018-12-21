package o2.a.build.publish;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.CompleteMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.PartEtag;
import com.obs.services.model.UploadPartResult;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DateTools;

import o2.a.build.lib.SFtpFileHelper;

/**
 * This sample demonstrates how to upload multiparts to OBS using the OBS SDK
 * for Java.
 */
public class ObsMultipartUploadSample {
	private static final String endPoint = "obs.cn-east-2.myhwclouds.com";

	private static final String ak = "67CEL8RL5I3QP20IYBIE";

	private static final String sk = "eyWMRgA2rxhZS4uPcZ4sFGX2T0wAercYgXRKcXEX";

	private static ObsClient obsClient;

	private static String bucketName = "obs-o2public";

	private static String ftpIp = "122.112.215.151";
	private static String ftpUser = "root";
	private static String ftpPass = "Zone2018";
	private static int ftpPort = 22;
	private static String filePath = "/data/www.zoneland.net/o2server/servers/webServer";

	public static void main(String... args) throws Exception {
		if (args != null && args.length == 2) {
			String day = args[0];
			String dir = args[1];
			String file1 = "o2server_" + day + "_x86.zip";
			File file_x86 = new File(dir, file1);
			String url1 = ObsMultipartUploadSample.updateToObs(file1, dir);
			String file2 = "o2server_" + day + "_macos.zip";
			File file_macos = new File(dir, file2);
			String url2 = ObsMultipartUploadSample.updateToObs(file2, dir);
			String file3 = "o2server_" + day + "_aix.zip";
			File file_aix = new File(dir, file3);
			String url3 = ObsMultipartUploadSample.updateToObs(file3, dir);

			Date now = new Date();
			LinkedHashMap<String, Object> map_windows_linux = new LinkedHashMap<>();
			LinkedHashMap<String, Object> map_macos = new LinkedHashMap<>();
			LinkedHashMap<String, Object> map_aix = new LinkedHashMap<>();
			map_windows_linux.put("system", "x86");
			map_windows_linux.put("name", "o2server_" + day + "_windows/linux");
			map_windows_linux.put("fileName", FilenameUtils.getName(file_x86.getName()));
			map_windows_linux.put("fileSize", file_x86.length() / 1024 / 1024 + "MB");
			map_windows_linux.put("fileUrl", url1);
			map_windows_linux.put("updateTime", DateTools.format(now));
			map_macos.put("system", "macos");
			map_macos.put("name", FilenameUtils.getBaseName(file_macos.getName()));
			map_macos.put("fileName", FilenameUtils.getName(file_macos.getName()));
			map_macos.put("fileSize", file_macos.length() / 1024 / 1024 + "MB");
			map_macos.put("fileUrl", url2);
			map_macos.put("updateTime", DateTools.format(now));
			map_aix.put("system", "aix");
			map_aix.put("name", FilenameUtils.getBaseName(file_aix.getName()));
			map_aix.put("fileName", FilenameUtils.getName(file_aix.getName()));
			map_aix.put("fileSize", file_aix.length() / 1024 / 1024 + "MB");
			map_aix.put("fileUrl", url3);
			map_aix.put("updateTime", DateTools.format(now));
			List<LinkedHashMap<String, Object>> list = new ArrayList<>();
			list.add(map_windows_linux);
			list.add(map_macos);
			list.add(map_aix);
			try {
				File file = new File(dir, "server_download.json");
				FileUtils.writeStringToFile(file, XGsonBuilder.toJson(list));
				SFtpFileHelper ftpHelper = new SFtpFileHelper(ftpIp, ftpUser, ftpPass, ftpPort);
				boolean flag = ftpHelper.updloadFile(getByteFromFile(file), "server_download.json", filePath);
				if (flag) {
					System.out.println("server_download.json sftp上传到" + ftpIp + "#" + filePath + "成功！");
				} else {
					System.out.println("sftp上传失败");
				}
			} catch (Exception e) {
				System.out.println("sftp上传失败！" + e.getMessage());
			}
		} else {
			// test("C:/tools","apache-tomcat-7.0.23.rar");
			System.out.println("上传失败，参数错误=======");
		}

	}

	/**
	 * 上传文件到华为OBS
	 * 
	 * @param objectKeyName
	 * @param directory
	 * @return
	 */
	public static String updateToObs(String objectKeyName, String directory) {
		ObsConfiguration config = new ObsConfiguration();
		config.setSocketTimeout(30000);
		config.setConnectionTimeout(10000);
		config.setEndPoint(endPoint);
		config.setHttpsOnly(true);
		try {
			/*
			 * Constructs a obs client instance with your account for accessing OBS
			 */
			obsClient = new ObsClient(ak, sk, config);

			/*
			 * Create bucket
			 */
			// obsClient.createBucket(bucketName);

			/*
			 * Step 1: initiate multipart upload
			 */
			File file = new File(directory, objectKeyName);
			System.out.println("上传附件到obs：" + objectKeyName);
			InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest();
			request.setBucketName(bucketName);
			request.setObjectKey(objectKeyName);
			InitiateMultipartUploadResult result = obsClient.initiateMultipartUpload(request);

			/*
			 * Step 2: upload a part
			 */
			// System.out.println("Step 2: upload part \n");
			UploadPartResult uploadPartResult = obsClient.uploadPart(bucketName, objectKeyName, result.getUploadId(), 1,
					new FileInputStream(file));

			/*
			 * Step 3: complete multipart upload
			 */
			// System.out.println("Step 3: complete multipart upload \n");
			CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest();
			completeMultipartUploadRequest.setBucketName(bucketName);
			completeMultipartUploadRequest.setObjectKey(objectKeyName);
			completeMultipartUploadRequest.setUploadId(result.getUploadId());
			PartEtag partEtag = new PartEtag();
			partEtag.setPartNumber(uploadPartResult.getPartNumber());
			partEtag.seteTag(uploadPartResult.getEtag());
			completeMultipartUploadRequest.getPartEtag().add(partEtag);
			obsClient.completeMultipartUpload(completeMultipartUploadRequest);
			System.out.println(objectKeyName + "附件上传成功！");
			System.out.println("obs地址：https://obs-o2public.obs.cn-east-2.myhwclouds.com/" + objectKeyName);
			return "https://obs-o2public.obs.cn-east-2.myhwclouds.com/" + objectKeyName;

		} catch (ObsException e) {
			System.out.println(objectKeyName + " Response Code: " + e.getResponseCode());
			System.out.println(objectKeyName + " Error Message: " + e.getErrorMessage());
			System.out.println(objectKeyName + " Error Code:       " + e.getErrorCode());
			System.out.println(objectKeyName + " Request ID:      " + e.getErrorRequestId());
			System.out.println(objectKeyName + " Host ID:           " + e.getErrorHostId());
		} catch (Exception e) {
			System.out.println(objectKeyName + " error: " + e.getMessage());
		} finally {
			if (obsClient != null) {
				try {
					obsClient.close();
				} catch (IOException e) {
				}
			}
		}
		return "";
	}

	/**
	 * 读取二进制数据从文件
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static byte[] getByteFromFile(File file) throws Exception {
		byte b[] = new byte[(int) file.length()];
		InputStream is = new FileInputStream(file);
		BufferedInputStream bi = new BufferedInputStream(is);
		bi.read(b);
		bi.close();
		is.close();
		return b;
	}

	public static void test(String dir, String file1) {
		File file_x86 = new File(dir, file1);
		if (file_x86.isFile()) {
			String url1 = ObsMultipartUploadSample.updateToObs(file1, dir);

			Date now = new Date();
			LinkedHashMap<String, Object> map_windows_linux = new LinkedHashMap<>();
			map_windows_linux.put("system", "x86");
			map_windows_linux.put("name", "o2server_" + "5555" + "_windows/linux");
			map_windows_linux.put("fileName", FilenameUtils.getName(file_x86.getName()));
			map_windows_linux.put("fileSize", file_x86.length() / 1024 / 1024 + "MB");
			map_windows_linux.put("fileUrl", url1);
			map_windows_linux.put("updateTime", DateTools.format(now));
			List<LinkedHashMap<String, Object>> list = new ArrayList<>();
			list.add(map_windows_linux);
			try {
				File file = new File(dir, "server_download.json");
				FileUtils.writeStringToFile(file, XGsonBuilder.toJson(list));
				SFtpFileHelper ftpHelper = new SFtpFileHelper(ftpIp, ftpUser, ftpPass, ftpPort);
				boolean flag = ftpHelper.updloadFile(getByteFromFile(file), "server_download.json", filePath);
				if (flag) {
					System.out.println("server_download.json sftp上传到" + ftpIp + "#" + filePath + "成功！");
				} else {
					System.out.println("sftp上传失败");
				}
			} catch (Exception e) {
				System.out.println("sftp上传失败！" + e.getMessage());
			}
		}
	}

}
