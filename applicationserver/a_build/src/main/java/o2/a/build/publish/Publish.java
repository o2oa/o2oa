package o2.a.build.publish;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

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

public class Publish {

	private static final String endPoint = "obs.cn-east-2.myhwclouds.com";

	private static final String ak = "67CEL8RL5I3QP20IYBIE";

	private static final String sk = "eyWMRgA2rxhZS4uPcZ4sFGX2T0wAercYgXRKcXEX";

	private static ObsClient obsClient;

	private static String bucketName = "obs-o2public";

	public static void main(String... args) throws Exception {
		String version = args[0];
		String dir = args[1];
		Date now = new Date();
		File file_update = new File(dir, version + ".zip");

		String update_url = updateToObs(file_update.getName(), file_update.getParent());
		File update_dir = new File("D:/o2collect/servers/webServer/o2server/update");
		FileUtils.copyFile(file_update, new File(update_dir, file_update.getName()));
		FileUtils.write(new File(update_dir, FilenameUtils.getBaseName(file_update.getName()) + ".txt"),
				"o2server update pack version:" + version, false);
		FileUtils.write(new File(update_dir, FilenameUtils.getBaseName(file_update.getName()) + ".url"), update_url,
				false);

		File file_windows = new File(dir, "o2server_" + version + "_windows.zip");
		File file_linux = new File(dir, "o2server_" + version + "_linux.zip");
		File file_macos = new File(dir, "o2server_" + version + "_macos.zip");
		File file_aix = new File(dir, "o2server_" + version + "_aix.zip");

		LinkedHashMap<String, Object> map_windows = new LinkedHashMap<>();
		LinkedHashMap<String, Object> map_linux = new LinkedHashMap<>();
		LinkedHashMap<String, Object> map_macos = new LinkedHashMap<>();
		LinkedHashMap<String, Object> map_aix = new LinkedHashMap<>();
		map_windows.put("system", "windows");
		map_windows.put("name", "o2server_" + version + "_windows");
		map_windows.put("fileName", FilenameUtils.getName(file_windows.getName()));
		map_windows.put("fileSize", file_windows.length() / 1024 / 1024 + "MB");
		map_windows.put("updateTime", DateTools.format(now));
		map_windows.put("url", updateToObs(file_windows.getName(), file_windows.getParent()));
		map_windows.put("sha256", sha256(file_windows));
		map_linux.put("system", "linux");
		map_linux.put("name", "o2server_" + version + "_linux");
		map_linux.put("fileName", FilenameUtils.getName(file_linux.getName()));
		map_linux.put("fileSize", file_linux.length() / 1024 / 1024 + "MB");
		map_linux.put("updateTime", DateTools.format(now));
		map_linux.put("url", updateToObs(file_linux.getName(), file_linux.getParent()));
		map_linux.put("sha256", sha256(file_linux));
		map_macos.put("system", "macos");
		map_macos.put("name", FilenameUtils.getBaseName(file_macos.getName()));
		map_macos.put("fileName", FilenameUtils.getName(file_macos.getName()));
		map_macos.put("fileSize", file_macos.length() / 1024 / 1024 + "MB");
		map_macos.put("updateTime", DateTools.format(now));
		map_macos.put("url", updateToObs(file_macos.getName(), file_macos.getParent()));
		map_macos.put("sha256", sha256(file_macos));
		map_aix.put("system", "aix");
		map_aix.put("name", FilenameUtils.getBaseName(file_aix.getName()));
		map_aix.put("fileName", FilenameUtils.getName(file_aix.getName()));
		map_aix.put("fileSize", file_aix.length() / 1024 / 1024 + "MB");
		map_aix.put("updateTime", DateTools.format(now));
		map_aix.put("url", updateToObs(file_aix.getName(), file_aix.getParent()));
		map_aix.put("sha256", sha256(file_aix));
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("windows", map_windows);
		map.put("linux", map_linux);
		map.put("macos", map_macos);
		map.put("aix", map_aix);
		File file_download = new File("Z:/www.o2oa.io/o2server/servers/webServer/webSite", "download.json");
		FileUtils.writeStringToFile(file_download, XGsonBuilder.toJson(map));
	}

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

	private static String sha256(File file) throws Exception {
		MessageDigest messageDigest;
		messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] hash = messageDigest.digest(FileUtils.readFileToByteArray(file));
		return Hex.encodeHexString(hash);
	}

	@Test
	public void test1() throws Exception {
		System.out.println(sha256(new File("d:/o2server_20180615160351_x86.zip")));
	}
}
