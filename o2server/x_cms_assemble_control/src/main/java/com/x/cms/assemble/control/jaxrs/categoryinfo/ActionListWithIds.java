package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.CategoryInfo;

public class ActionListWithIds extends BaseAction {

  ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
    ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
    Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
    List<Wo> wos = new ArrayList<>();
    if (wi.getCategoryIdList() == null || wi.getCategoryIdList().isEmpty()) {
      result.setData(wos);
      return result;
    }
    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
      Business business = new Business(emc);
      List<CategoryInfo> list = business.getCategoryInfoFactory().listCategoryByIds(wi.getCategoryIdList());
      if (list != null && !list.isEmpty()) {
        wos = Wo.copier.copy(list);
      }
    }
    result.setData(wos);
    return result;
  }

  public static class Wi extends GsonPropertyObject {

    @FieldDescribe(" 分类 id 列表(多值逗号隔开).")
    private List<String> categoryIdList;

    public List<String> getCategoryIdList() {
      return categoryIdList;
    }

    public void setCategoryIdList(List<String> categoryIdList) {
      this.categoryIdList = categoryIdList;
    }

  }

  public static class Wo extends CategoryInfo {

    static WrapCopier<CategoryInfo, Wo> copier = WrapCopierFactory.wo(CategoryInfo.class, Wo.class, null,
        ListTools.toList(JpaObject.FieldsInvisible));

  }

}
