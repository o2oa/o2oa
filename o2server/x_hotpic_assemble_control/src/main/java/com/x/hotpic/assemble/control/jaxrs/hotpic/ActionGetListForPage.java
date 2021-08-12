package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.entity.HotPictureInfo;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;

public class ActionGetListForPage extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson,Integer page,  Integer count, JsonElement jsonElement) throws Exception {		
		ActionResult<List<Wo>> result = new ActionResult<>();
		Ehcache cache = ApplicationCache.instance().getCache(HotPictureInfo.class);
		HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();
		
		List<Wo> wraps_out = new ArrayList<Wo>();
		List<Wo> wraps = new ArrayList<Wo>();
		List<HotPictureInfo> hotPictureInfoList = null;
		Integer selectTotal = 0;
		Long total = 0L;
		WrapInFilter wrapIn = null;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WrapInFilter.class);
		} catch (Exception e) {
			throw new WrapInConvertException(e, jsonElement);
		}

		if (wrapIn == null) {
			wrapIn = new WrapInFilter();
		}


		if (page == null) {
			page = 1;
		}
		if (page <= 0) {
			page = 1;
		}


		if (count == null) {
			count = 20;
		}
		if (count <= 0) {
			count = 20;
		}
	
     selectTotal = page * count;
     String cacheKey1 = "filter#" + page + "#" + count + "#" + wrapIn.getApplication() + "#" + wrapIn.getInfoId()
		+ "#" + wrapIn.getTitle();
     Element element1 = cache.get(cacheKey1);
      String cacheKey2 = "total#" + page + "#" + count + "#" + wrapIn.getApplication() + "#" + wrapIn.getInfoId()
		+ "#" + wrapIn.getTitle();
     Element element2 = cache.get(cacheKey2);

	if (null != element1 && null != element2) {
		wraps = (List<Wo>) element1.getObjectValue();
		result.setCount(Long.parseLong(element2.getObjectValue().toString()));
		result.setData(wraps);
	} else {
	
		if (selectTotal > 0) {
			try {
				total = hotPictureInfoService.count(wrapIn.getApplication(), wrapIn.getInfoId(),wrapIn.getTitle());
			} catch (Exception e) {
				throw new InfoListByFilterException(e);
			}
		 }

		if (selectTotal > 0 && total > 0) {
			int startIndex = (page - 1) * count;
			int endIndex = count;
				try {
					hotPictureInfoList = hotPictureInfoService.listForPage(wrapIn.getApplication(),
							wrapIn.getInfoId(), wrapIn.getTitle(), startIndex ,endIndex);
					if (hotPictureInfoList != null) {
						try {
							wraps_out = Wo.copier.copy(hotPictureInfoList);
							//SortTools.desc(wraps_out, JpaObject.sequence_FIELDNAME);
						} catch (Exception e) {
							throw new InfoWrapOutException(e);
						}
					}
				} catch (Exception e) {
					throw new InfoListByFilterException(e);
				}
			}

	       /*
			int startIndex = (page - 1) * count;
			int endIndex = page * count;
			int i = 0;
			for (i = 0; i < wraps_out.size(); i++) {
				if (i >= startIndex && i < endIndex) {
					wraps.add(wraps_out.get(i));
				}
			}*/
			
			cache.put(new Element(cacheKey1, wraps_out));
			cache.put(new Element(cacheKey2, total.toString()));
			result.setData(wraps_out);
			result.setCount(total);
		}

	return result;
}

public static class Wi {

}

public static class Wo extends HotPictureInfo {
	public static List<String> Excludes = new ArrayList<String>();
	public static WrapCopier<HotPictureInfo, Wo> copier = WrapCopierFactory.wo(HotPictureInfo.class, Wo.class, null,
			JpaObject.FieldsInvisible);

}}
