package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.FileInfo;

@Wrap( FileInfo.class)
public class WrapInFileInfo extends FileInfo
{
  private static final long serialVersionUID = -5076990764713538973L;
  public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);
}