package com.x.ai.assemble.control.jaxrs.file;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.jaxrs.file.ActionUpload.WoFile;
import com.x.ai.assemble.control.util.HttpUtil;
import com.x.ai.core.entity.File;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.FilePart;
import com.x.base.core.project.exception.ExceptionFileNameInvalid;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

abstract class BaseAction extends StandardJaxrsAction {

    private static final List<String> accessFileTypeList = List.of("pdf", "ofd", "txt", "md", "docx", "doc",
            "wps", "pptx", "ppt", "pub", "vsd", "xlsx", "xls", "png", "jpg", "jpeg", "html", "htm");

    protected void verifyConstraint(String fileName) throws ExceptionFileNameInvalid {
        if (!StringTools.isFileName(fileName)) {
            throw new ExceptionFileNameInvalid(fileName);
        } else {
            String fileType = FilenameUtils.getExtension(fileName);
            if (StringUtils.isBlank(fileType)) {
                throw new ExceptionFileNameInvalid(fileName);
            }else{
                fileType = fileType.toLowerCase();
            }
            if(!ListTools.contains(accessFileTypeList, fileType)){
                throw new ExceptionFileNameInvalid(fileName);
            }
        }
    }

    protected String uploadToO2Ai(File f, byte[] bytes) throws Exception{
        AiConfig aiConfig = Business.getConfig();
        if (BooleanUtils.isTrue(aiConfig.getO2AiEnable())
                && StringUtils.isNotBlank(aiConfig.getO2AiBaseUrl())
                && StringUtils.isNotBlank(aiConfig.getO2AiToken())) {
            List<NameValuePair> heads = List.of(
                    new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
            String url = aiConfig.getO2AiBaseUrl() + "/infra-gateway-material/create";
            List<FilePart> filePartList = new ArrayList<>();
            FilePart filePart = new FilePart(f.getName(), bytes, Config.mimeTypes(f.getExtension()), "file");
            filePartList.add(filePart);
            ActionResponse resp = HttpUtil.postMultiPartBinary(url, heads, null, filePartList);
            if (Type.success.equals(resp.getType())) {
                List<WoFile> woFileList = resp.getDataAsList(WoFile.class);
                if(ListTools.isNotEmpty(woFileList)){
                    return woFileList.get(0).getId();
                }
            }
        }
        return "";
    }
}
