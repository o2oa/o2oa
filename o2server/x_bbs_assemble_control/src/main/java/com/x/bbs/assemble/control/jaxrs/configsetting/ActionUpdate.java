package com.x.bbs.assemble.control.jaxrs.configsetting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingCodeEmpty;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingInfoProcess;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingNotExists;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionConfigSettingValueEmpty;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ExceptionInsufficientPermissions;
import com.x.bbs.entity.BBSConfigSetting;

public class ActionUpdate extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionUpdate.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		BBSConfigSetting configSetting = null;
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionConfigSettingInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				if (!userManagerService.isHasPlatformRole( effectivePerson.getDistinguishedName(),
						ThisApplication.BBSMANAGER ) && !effectivePerson.isManager()) {
					check = false;
					logger.warn("用户没有BBSManager角色，并且也不是系统管理员！USER：" + effectivePerson.getDistinguishedName());
					Exception exception = new ExceptionInsufficientPermissions(effectivePerson.getDistinguishedName(),
							ThisApplication.BBSMANAGER);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionInsufficientPermissions(effectivePerson.getDistinguishedName(),
						ThisApplication.BBSMANAGER);
				result.error(exception);
				result.error(e);
			}
		}
		if (check) {
			if (wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty()) {
				check = false;
				Exception exception = new ExceptionConfigSettingCodeEmpty();
				result.error(exception);
			}
		}

		if (check) {
			if (wrapIn.getConfigValue() == null || wrapIn.getConfigValue().isEmpty()) {
				check = false;
				Exception exception = new ExceptionConfigSettingValueEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				configSetting = configSettingService.getWithConfigCode(wrapIn.getConfigCode());
				if (configSetting == null) {
					check = false;
					Exception exception = new ExceptionConfigSettingNotExists(wrapIn.getConfigCode());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionConfigSettingInfoProcess(e,
						"系统在根据编码获取BBS系统设置信息时发生异常！Code:" + wrapIn.getConfigCode());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				configSetting.setConfigValue(wrapIn.getConfigValue());
				configSetting = configSettingService.update(configSetting);
			} catch (Exception e) {
				Exception exception = new ExceptionConfigSettingInfoProcess(e,
						"根据ID更新BBS系统设置信息时发生异常.ID:" + wrapIn.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (configSetting != null) {
				try {
					wrap = Wo.copier.copy(configSetting);
					result.setData(wrap);
				} catch (Exception e) {
					Exception exception = new ExceptionConfigSettingInfoProcess(e, "系统在转换所有BBS系统设置信息为输出对象时发生异常.");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		notifyCache();
		return result;
	}

	private void notifyCache() throws Exception {
		CacheManager.notify(BBSConfigSetting.class);
	}

	public static class Wi extends BBSConfigSetting {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

	}

	public static class Wo extends BBSConfigSetting {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<BBSConfigSetting, Wo> copier = WrapCopierFactory.wo(BBSConfigSetting.class, Wo.class,
				null, JpaObject.FieldsInvisible);

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}