package com.x.ai.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.ExceptionFileNameInvalid;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
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
}
