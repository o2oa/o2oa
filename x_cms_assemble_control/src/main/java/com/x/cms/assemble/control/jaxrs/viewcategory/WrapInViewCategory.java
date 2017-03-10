package com.x.cms.assemble.control.jaxrs.viewcategory;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.element.ViewCategory;

@Wrap( ViewCategory.class )
public class WrapInViewCategory extends ViewCategory
{
  private static final long serialVersionUID = -5076990764713538973L;
  public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);
}