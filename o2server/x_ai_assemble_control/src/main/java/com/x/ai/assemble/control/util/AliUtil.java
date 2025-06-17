package com.x.ai.assemble.control.util;

import com.aliyun.bailian20231229.Client;
import com.aliyun.bailian20231229.models.AddFileRequest;
import com.aliyun.bailian20231229.models.AddFileResponse;
import com.aliyun.bailian20231229.models.ApplyFileUploadLeaseRequest;
import com.aliyun.bailian20231229.models.ApplyFileUploadLeaseResponse;
import com.aliyun.bailian20231229.models.ApplyFileUploadLeaseResponseBody.ApplyFileUploadLeaseResponseBodyData;
import com.aliyun.bailian20231229.models.DeleteFileResponse;
import com.aliyun.bailian20231229.models.DeleteIndexDocumentRequest;
import com.aliyun.bailian20231229.models.DeleteIndexDocumentResponse;
import com.aliyun.bailian20231229.models.SubmitIndexAddDocumentsJobRequest;
import com.aliyun.bailian20231229.models.SubmitIndexAddDocumentsJobResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.google.gson.Gson;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 * @author chengjian
 * @date 2024/12/30 15:57
 **/
public class AliUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(AliUtil.class);

    private static final String ENDPOINT = "bailian.cn-beijing.aliyuncs.com";
    private static final String SUCCESS_CODE = "Success";
    private static final String workspaceId = "llm-dd8d9h4eipqq76c1";
    private static final String indexId = "n13tnqi21r";
    private static final String appId = "8b3e580eada34a91a0c8cc4788f581da";
    private static final String categoryId = "default";
    private static final String apiKey = "sk-4b2b18296ba74587afd12082190b97b0";
    private static final String accessKeyId = "LTAI5tJTmjiPtSEBJHMCVNEu";
    private static final String accessKeySecret = "W1NsxhCZ9yduSHeSWkffb1oJgwm7FT";
    private static final String openAiBaseUrl = "https://dashscope.aliyuncs.com/api/v1";

    public static Client createClient(AiConfig aiConfig) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = ENDPOINT;
        return new Client(config);
    }

    public static Pair<Boolean, String> applyFileUpload(AiConfig config, String fileName, byte[] bytes){
        try {
            String fileMd5;
            try(InputStream is = new ByteArrayInputStream(bytes)){
                fileMd5 = FileUtil.getFileMD5(is);
            }
            Client client = AliUtil.createClient(config);
            Map<String, String> headers = new HashMap<>();
            RuntimeOptions runtime = new RuntimeOptions();
            ApplyFileUploadLeaseRequest request = new ApplyFileUploadLeaseRequest();
            request.setFileName(fileName);
            request.setMd5(fileMd5);
            request.setSizeInBytes(bytes.length+"");
            ApplyFileUploadLeaseResponse response = client.applyFileUploadLeaseWithOptions("default", "llm-dd8d9h4eipqq76c1", request, headers, runtime);
            if(response!=null && response.getBody()!=null && SUCCESS_CODE.equals(response.getBody().getCode())){
                ApplyFileUploadLeaseResponseBodyData applyFileInfo = response.getBody().getData();
                LOGGER.info("to uploadFile file={}", fileName);
                if(uploadFile(applyFileInfo, bytes)){
                    LOGGER.info("to addFile file={}", fileName);
                    return addFile(client, config, applyFileInfo, fileName);
                }else{
                    LOGGER.warn("uploadFile file={} fail", fileName);
                }
            }else{
                LOGGER.warn("applyFileUpload file={} error：{}", fileName, XGsonBuilder.toJson(response));
            }
        } catch (Exception e){
            LOGGER.error(e);
        }
        return Pair.of(false, "");
    }

    public static Pair<Boolean, String> addFile(Client client, AiConfig config, ApplyFileUploadLeaseResponseBodyData applyFileInfo, String fileName){
        AddFileRequest addFileRequest = new AddFileRequest();
        addFileRequest.setLeaseId(applyFileInfo.getFileUploadLeaseId());
        addFileRequest.setCategoryId("default");
        String parser = "DASHSCOPE_DOCMIND";
        addFileRequest.setParser(parser);
        RuntimeOptions runtime = new RuntimeOptions();
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        try {
            AddFileResponse response = client.addFileWithOptions("llm-dd8d9h4eipqq76c1", addFileRequest, headers, runtime);
            if(response!=null && response.getBody()!=null && SUCCESS_CODE.equals(response.getBody().getCode())){
                return Pair.of(true, response.getBody().getData().getFileId());
            }else {
                LOGGER.warn("addFile file={} error：{}", fileName, XGsonBuilder.toJson(response));
            }
        } catch (Exception e){
            LOGGER.error(e);
        }

        return Pair.of(false, "");
    }

    private static boolean uploadFile(ApplyFileUploadLeaseResponseBodyData applyFileInfo, byte[] bytes) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(applyFileInfo.getParam().getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(applyFileInfo.getParam().getMethod());
            connection.setDoOutput(true);
            Map<String, String> map = XGsonBuilder.convert(applyFileInfo.getParam().getHeaders(), Map.class);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            try (DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
                    InputStream in = new ByteArrayInputStream(bytes)) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                outStream.flush();
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return false;
    }

    public static boolean submitIndexAddDoc(AiConfig config, String fileId, String fileName){
        try {
            LOGGER.info("to submitIndexAddDoc file={}", fileName);
            Client client = AliUtil.createClient(config);
            Map<String, String> headers = new HashMap<>();
            RuntimeOptions runtime = new RuntimeOptions();
            SubmitIndexAddDocumentsJobRequest request = new SubmitIndexAddDocumentsJobRequest();
            request.setIndexId("n13tnqi21r");
            request.setDocumentIds(List.of(fileId));
            String sourceType = "DATA_CENTER_FILE";
            request.setSourceType(sourceType);
            SubmitIndexAddDocumentsJobResponse response = client.submitIndexAddDocumentsJobWithOptions(workspaceId, request, headers, runtime);
            if(response!=null && response.getBody()!=null && SUCCESS_CODE.equals(response.getBody().getCode())){
                return true;
            }else{
                LOGGER.warn("submitIndexAddDoc file={} error：{}", fileName, XGsonBuilder.toJson(response));
            }
        } catch (Exception e){
            LOGGER.error(e);
        }

        return false;
    }

    public static boolean deleteFile(AiConfig config, String fileId, String fileName){
        try {
            LOGGER.info("to deleteFile file={}", fileName);
            Client client = AliUtil.createClient(config);
            Map<String, String> headers = new HashMap<>();
            RuntimeOptions runtime = new RuntimeOptions();
            DeleteFileResponse response = client.deleteFileWithOptions(fileId, workspaceId, headers, runtime);
            if(response!=null && response.getBody()!=null && SUCCESS_CODE.equals(response.getBody().getCode())){
                return true;
            }else{
                LOGGER.warn("deleteFile file={} error：{}", fileName, XGsonBuilder.toJson(response));
            }
        } catch (Exception e){
            LOGGER.error(e);
        }
        return false;
    }

    public static boolean deleteIndexDoc(AiConfig config, String fileId, String fileName){
        try {
            LOGGER.info("to deleteIndexDoc file={}", fileName);
            Client client = AliUtil.createClient(config);
            Map<String, String> headers = new HashMap<>();
            RuntimeOptions runtime = new RuntimeOptions();
            DeleteIndexDocumentRequest request = new DeleteIndexDocumentRequest();
            request.setIndexId(indexId);
            request.setDocumentIds(List.of(fileId));
            DeleteIndexDocumentResponse response = client.deleteIndexDocumentWithOptions(workspaceId, request, headers, runtime);
            if(response!=null && response.getBody()!=null && SUCCESS_CODE.equals(response.getBody().getCode())){
                return true;
            }else{
                LOGGER.warn("deleteIndexDoc file={} error：{}", fileName, XGsonBuilder.toJson(response));
            }
        } catch (Exception e){
            LOGGER.error(e);
        }
        return false;
    }

    public static void main(String[] args) throws Exception{
        Gson gson = new Gson();
        AiConfig config = new AiConfig();
        File file = new File("/Users/chengjian/Downloads/客户管理需求文档.docx");
        Pair<Boolean, String> pair = applyFileUpload(config, file.getName(),
                FileUtils.readFileToByteArray(file));
        if(BooleanUtils.isTrue(pair.first())){
            if(submitIndexAddDoc(config, pair.second(), file.getName())){
                LOGGER.info("submitIndexAddDoc success");
            }else{
                LOGGER.warn("submitIndexAddDoc fail");
            }
        }
    }
}
