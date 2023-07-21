//package com.x.processplatform.assemble.designer.jaxrs.input;
//
//import org.apache.commons.lang3.StringUtils;
//import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
//
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.project.gson.XGsonBuilder;
//import com.x.base.core.project.http.ActionResult;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.base.core.project.tools.DefaultCharset;
//import com.x.base.core.project.tools.StringTools;
//import com.x.processplatform.assemble.designer.Business;
//import com.x.processplatform.assemble.designer.CompareWoApplication;
//import com.x.processplatform.assemble.designer.WrapApplication;
//import com.x.processplatform.core.entity.element.Application;
//
//import net.sf.ehcache.Element;
//
//class ActionUpload extends BaseAction {
//
//	private static Logger logger = LoggerFactory.getLogger(ActionUpload.class);
//
//	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
//			throws Exception {
//		logger.debug(effectivePerson, "name: {}.", disposition.getName());
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			if (!StringUtils.endsWithIgnoreCase(disposition.getFileName(), ".xapp")) {
//				throw new ExceptionFileIncorrect(disposition.getFileName());
//			}
//			Business business = new Business(emc);
//			ActionResult<Wo> result = new ActionResult<>();
//			String json = new String(bytes, DefaultCharset.charset);
//			WrapApplication wrapApplication = XGsonBuilder.instance().fromJson(json, WrapApplication.class);
//			Application exist = this.getApplication(business, wrapApplication.getId(), wrapApplication.getName(),
//					wrapApplication.getAlias());
//			Wo wo = new Wo();
//			wo.setId(wrapApplication.getId());
//			wo.setName(wrapApplication.getName());
//			wo.setAlias(wrapApplication.getAlias());
//			wo.setExist(false);
//			if (null != exist) {
//				wo.setExist(true);
//				wo.setExistName(exist.getName());
//				wo.setExistAlias(exist.getAlias());
//				wo.setExistId(exist.getId());
//			}
//			result.setData(wo);
//			return result;
//		}
//	}
//
//	public static class Wo extends CompareWoApplication {
//	}
//
//}