package com.x.attendance.assemble.control.jaxrs.v2.my;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Identity;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.DefaultCharset;

public class ActionMyControl extends BaseAction {

  ActionResult<Wo> execute(EffectivePerson person) throws Exception {
    ActionResult<Wo> result = new ActionResult<>();
    Wo wo = new Wo();
    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
      Business business = new Business(emc);
      if (business.isManager(person)) { // 管理员直接返回 不需要查询个人信息
        wo.setAdmin(true);
        result.setData(wo);
        return result;
      }
      // 查询个人信息 判断是否有[考勤管理员]职务
      String encodePerson = URLEncoder.encode(person.getDistinguishedName(), DefaultCharset.name);
      WoPerson p = ThisApplication.context().applications().getQuery(x_organization_assemble_control.class, "person/" + encodePerson).getData(WoPerson.class);
      if (p != null && p.getWoIdentityList() != null && !p.getWoIdentityList().isEmpty()) {
        List<WoUnitDuty> woUnitDutyList = new ArrayList<>();
        for (WoIdentity identity : p.getWoIdentityList()) {
          if (identity.getWoUnitDutyList() != null && !identity.getWoUnitDutyList().isEmpty() ) {
            //todo 考勤管理员 可配置
            woUnitDutyList.addAll(identity.getWoUnitDutyList().stream().filter((d) -> d.getName().equals("考勤管理员")).collect(Collectors.toList()));
          }
        }
        if (!woUnitDutyList.isEmpty()) {
          wo.setReadAdmin(true);
          wo.setUnitDutyList(woUnitDutyList);
        }
      }
      // 查询考勤组 协助管理员
      wo.setAssistAdmin(business.getAttendanceV2ManagerFactory().isAssistAdmin(person.getDistinguishedName()));
      result.setData(wo);
      return result;
    }
  }

  public static class Wo extends GsonPropertyObject {

    private static final long serialVersionUID = 6260606941234950656L;
    @FieldDescribe("是否管理员或考勤管理员")
    private Boolean admin;
    @FieldDescribe("是否有[考勤管理员]职务")
    private Boolean readAdmin;
    @FieldDescribe("考勤管理员职务列表")
    private List<WoUnitDuty> unitDutyList;
    @FieldDescribe("是否考勤组协助管理员")
    private Boolean assistAdmin;

    public Boolean getAdmin() {
      return admin;
    }

    public void setAdmin(Boolean admin) {
      this.admin = admin;
    }

    public Boolean getReadAdmin() {
      return readAdmin;
    }

    public void setReadAdmin(Boolean readAdmin) {
      this.readAdmin = readAdmin;
    }

    public Boolean getAssistAdmin() {
      return assistAdmin;
    }

    public void setAssistAdmin(Boolean assistAdmin) {
      this.assistAdmin = assistAdmin;
    }

    public List<WoUnitDuty> getUnitDutyList() {
      return unitDutyList;
    }

    public void setUnitDutyList(List<WoUnitDuty> unitDutyList) {
      this.unitDutyList = unitDutyList;
    }
 

  }

  public static class WoUnit extends Unit {

    private static final long serialVersionUID = 5521625368318946265L;
    

  }

  public static class WoUnitDuty extends GsonPropertyObject {

    private static final long serialVersionUID = 8887183560643765452L;


    @FieldDescribe("职务名称")
	  private String name;
    @FieldDescribe("唯一标识")
    private String unique;
    @FieldDescribe("识别名")
    private String distinguishedName;

    @FieldDescribe("组织对象")
    private WoUnit woUnit;
 

    public WoUnit getWoUnit() {
      return woUnit;
    }

    public void setWoUnit(WoUnit woUnit) {
      this.woUnit = woUnit;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getUnique() {
      return unique;
    }

    public void setUnique(String unique) {
      this.unique = unique;
    }

    public String getDistinguishedName() {
      return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
      this.distinguishedName = distinguishedName;
    }

    
  }

  public static class WoIdentity extends Identity {

    private static final long serialVersionUID = -6779245805216952773L;
 

    @FieldDescribe("组织对象")
    private WoUnit woUnit;

    @FieldDescribe("组织职务对象")
    private List<WoUnitDuty> woUnitDutyList;

    public WoUnit getWoUnit() {
      return woUnit;
    }

    public void setWoUnit(WoUnit woUnit) {
      this.woUnit = woUnit;
    }

    public List<WoUnitDuty> getWoUnitDutyList() {
      return woUnitDutyList;
    }

    public void setWoUnitDutyList(List<WoUnitDuty> woUnitDutyList) {
      this.woUnitDutyList = woUnitDutyList;
    }

  }

  public static class WoPerson extends Person {

    private static final long serialVersionUID = -6787590791993664117L;
 
    @FieldDescribe("身份对象")
    private List<WoIdentity> woIdentityList;

    public List<WoIdentity> getWoIdentityList() {
      return woIdentityList;
    }

    public void setWoIdentityList(List<WoIdentity> woIdentityList) {
      this.woIdentityList = woIdentityList;
    }

  }
}
