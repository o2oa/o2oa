package com.x.ai.assemble.control.jaxrs.file;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.util.AliUtil;
import com.x.ai.core.entity.File;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.exception.ExceptionFileNameInvalid;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
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
    protected void uploadToDmx(byte[] bytes, String fileName, String id) throws Exception{
        AiConfig config = Business.getConfig();
        Pair<Boolean, String> pair = AliUtil.applyFileUpload(config, fileName, bytes);
        if(BooleanUtils.isTrue(pair.first())){
            boolean hasIndex = AliUtil.submitIndexAddDoc(config, pair.second(), fileName);
            String status = hasIndex ? File.STATUS_INDEXED : File.STATUS_NO_INDEX;
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                File file = emc.find(id, File.class);
                file.setFileId(pair.second());
                file.setStatus(status);
                emc.beginTransaction(File.class);
                emc.commit();
            }
        }
    }
}
