package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.element.ViewFieldConfig;

@Wrap( ViewFieldConfig.class)
public class WrapInViewFieldConfig extends ViewFieldConfig
{
  private static final long serialVersionUID = -5076990764713538973L;
  public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);
}