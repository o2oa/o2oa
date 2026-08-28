package com.x.query.core.express.plan;

import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjian
 * @date 2026/08/25 15:33
 **/
public class FilterEntryGroup extends GsonPropertyObject {

    private static final long serialVersionUID = 6583371134241799968L;
    public String logic = "or";
    public List<FilterEntry> entryList = new ArrayList<>();
    public List<FilterEntryGroup> groupList = new ArrayList<>();
}
