package com.x.pan.assemble.control.jaxrs.config;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileConfig3;
import java.net.URL;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.http.HttpHeaders;

class ActionIsEnableOfficePreview extends BaseAction {

    private static final String ONLY_OFFICE_PREVIEW = "onlyoffice";

    ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            Wo wo = new Wo();
            wo.setValue(business.getSystemConfig().getOfficePreviewTools());
            wo.setOpenOfficeEdit(business.getSystemConfig().getOfficeOpenOfficeEdit());
            String referer = request.getHeader(HttpHeaders.REFERER);
            if (StringUtils.isNotBlank(referer) && ONLY_OFFICE_PREVIEW.equals(wo.getValue()) && effectivePerson.isManager()) {
				this.initPreviewUrl(referer, business);
            }
            result.setData(wo);
            return result;
        }
    }

    private synchronized void initPreviewUrl(String referer, Business business) throws Exception {
		String serverName = "product.o2oa.net";
		String previewUrl = business.getSystemConfig().getOfficeViewDownloadUrl();
		if(referer.contains(serverName) && StringUtils.isNotBlank(previewUrl)){
			return;
		}
		if(StringUtils.isBlank(previewUrl) || previewUrl.contains(serverName)){
			URL url = new URL(referer);
			int port = url.getPort();
			previewUrl = url.getProtocol() + "://" + url.getHost() + (
					(port < 0 || port == 80 || port == 443) ? "" : ":" + port)
					+ "/x_pan_assemble_control/jaxrs";
			EntityManagerContainer emc = business.entityManagerContainer();
			FileConfig3 fileConfig = emc.firstEqual(FileConfig3.class, FileConfig3.person_FIELDNAME, Business.SYSTEM_CONFIG);
			if(fileConfig != null){
				emc.beginTransaction(FileConfig3.class);
				fileConfig.setUpdateTime(new Date());
				fileConfig.getProperties().setViewDownLoadUrl(previewUrl);
				emc.commit();
				CacheManager.notify(FileConfig3.class);
			}
		}
    }

    public static class Wo extends GsonPropertyObject {

        public Wo() {

        }

        public Wo(String value, Boolean isOpenOfficeEdit) throws Exception {
            this.value = value;
            this.isOpenOfficeEdit = isOpenOfficeEdit;
        }

        @FieldDescribe("预览工具")
        private String value;

        @FieldDescribe("打开Office时是否直接进入编辑状态(true表示进入编辑状态|false表示进入只读状态，默认为false)")
        private Boolean isOpenOfficeEdit;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Boolean getOpenOfficeEdit() {
            return isOpenOfficeEdit;
        }

        public void setOpenOfficeEdit(Boolean openOfficeEdit) {
            isOpenOfficeEdit = openOfficeEdit;
        }
    }
}
