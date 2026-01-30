package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;


class ActionToken extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(ActionToken.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        logger.debug("execute:{}", effectivePerson::getDistinguishedName);
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setValue("");
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if(StringUtils.isNotBlank(wi.getTokenText()) && DocumentManager.tokenEnabled()){
            Map<String, Object> map = gson.fromJson(wi.getTokenText(), new TypeToken<Map<String, Object>>(){}.getType());
            wo.setValue(DocumentManager.createToken(map));
        }
        result.setData(wo);
        return result;
    }


    public static class Wo extends WrapString {

    }


    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("加密文本，json字符串")
        private String tokenText;

        public String getTokenText() {
            return tokenText;
        }

        public void setTokenText(String tokenText) {
            this.tokenText = tokenText;
        }
    }
}
