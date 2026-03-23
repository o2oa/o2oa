package com.x.pan.assemble.control.jaxrs.attachment3;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;

class ActionSaveByThird extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionSaveByThird.class);

    String execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        logger.info("{}用户通过第三方编辑器保存文档:{}", effectivePerson.getDistinguishedName(),
                wi.getFileId());
        Map<String, Object> hashMap = new HashMap<>(2);

        byte[] bytes = Base64.decodeBase64(wi.getContent());
        this.saveFile(wi.getFileId(), bytes, null, null, effectivePerson, hashMap);
        return gson.toJson(hashMap);
    }

    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("文件Id.")
        private String fileId;

        @FieldDescribe("文件base64内容.")
        private String content;

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class Wo extends WoId {

    }
}
