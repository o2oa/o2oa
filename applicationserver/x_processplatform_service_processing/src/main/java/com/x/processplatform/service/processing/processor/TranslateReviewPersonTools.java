package com.x.processplatform.service.processing.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;

/**
 * 在Manual环节计算所有的参阅人的Identity
 * 
 * @author Rui
 *
 */
public class TranslateReviewPersonTools {

	private static Logger logger = LoggerFactory.getLogger(TranslateReviewPersonTools.class);

	/* 计算manual节点中所有的待办，全部翻译成Identity */
	public static List<String> translate(AeiObjects aeiObjects) throws Exception {
		List<String> identities = SetUniqueList.setUniqueList(new ArrayList<String>());
		identities.addAll(identity(aeiObjects.getActivity()));
		identities.addAll(data(aeiObjects.business(), aeiObjects.getData(), aeiObjects.getActivity()));
		identities.addAll(unit(aeiObjects.business(), aeiObjects.getActivity()));
		identities.addAll(script(aeiObjects));
		identities.addAll(duty(aeiObjects));
		identities = ListTools.trim(identities, true, true);
		List<String> os = aeiObjects.business().organization().identity().list(identities);
		os = aeiObjects.business().organization().person().listWithIdentity(os);
		return os;
	}

	/* 取到指定职务的identity */
	private static List<String> duty(AeiObjects aeiObjects) throws Exception {
		List<String> list = new ArrayList<>();
		if (StringUtils.isNotEmpty(aeiObjects.getActivity().getReviewDuty())) {
			JsonArray array = XGsonBuilder.instance().fromJson(aeiObjects.getActivity().getReviewDuty(),
					JsonArray.class);
			Iterator<JsonElement> iterator = array.iterator();
			while (iterator.hasNext()) {
				JsonObject o = iterator.next().getAsJsonObject();
				String name = o.get("name").getAsString();
				ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects);
				List<String> ds = scriptHelper.evalExtrectDistinguishedName(aeiObjects.getWork().getApplication(),
						null, o.get("code").getAsString());
				if (ListTools.isNotEmpty(ds)) {
					for (String str : ds) {
						List<String> os = aeiObjects.business().organization().unitDuty()
								.listIdentityWithUnitWithName(str, name);
						if (ListTools.isNotEmpty(os)) {
							list.addAll(os);
						}
					}
				}
			}
		}
		return ListTools.trim(list, true, true);
	}

	/* 取到script指定的identity */
	private static List<String> script(AeiObjects aeiObjects) throws Exception {
		List<String> list = new ArrayList<>();
		if ((StringUtils.isNotEmpty(aeiObjects.getActivity().getReviewScript()))
				|| (StringUtils.isNotEmpty(aeiObjects.getActivity().getReviewScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects);
			List<String> os = scriptHelper.evalExtrectDistinguishedName(aeiObjects.getWork().getApplication(),
					aeiObjects.getActivity().getReviewScript(), aeiObjects.getActivity().getReviewScriptText());
			if (ListTools.isNotEmpty(os)) {
				list.addAll(os);
			}
		}
		return list;
	}

	/* 取得指定部门的identity */
	private static List<String> unit(Business business, Activity activity) throws Exception {
		List<String> list = new ArrayList<>();
		if (ListTools.isNotEmpty(activity.getReviewUnitList())) {
			list.addAll(business.organization().identity().listWithUnitSubDirect(activity.getReviewUnitList()));
		}
		return list;
	}

	private static List<String> data(Business business, Data data, Activity activity) throws Exception {
		List<String> list = new ArrayList<>();
		if (ListTools.isNotEmpty(activity.getReviewDataPathList())) {
			for (String str : ListTools.trim(activity.getReviewDataPathList(), true, true)) {
				if (StringUtils.isNotEmpty(str)) {
					list.addAll(data.extractDistinguishedName(str));
				}
			}
		}
		return list;
	}

	private static List<String> identity(Activity activity) throws Exception {
		List<String> list = new ArrayList<>();
		if (ListTools.isNotEmpty(activity.getReviewIdentityList())) {
			list.addAll(activity.getReviewIdentityList());
		}
		return list;
	}

}
