package com.x.program.center.dingding;

import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class DingdingUserPageResult extends GsonPropertyObject {
  
  private static final long serialVersionUID = -4243592617446474567L;
  private Boolean has_more;
  private Integer next_cursor;

  private List<User> list;

  public Boolean getHas_more() {
    return has_more;
  }

  public void setHas_more(Boolean has_more) {
    this.has_more = has_more;
  }

  public Integer getNext_cursor() {
    return next_cursor;
  }

  public void setNext_cursor(Integer next_cursor) {
    this.next_cursor = next_cursor;
  }

  public List<User> getList() {
    return list;
  }

  public void setList(List<User> list) {
    this.list = list;
  }
 
  

  
}
