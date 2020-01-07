package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import java.util.List;

/**
 * x_organization_assemble_express/jaxrs/complex/person/
 * 查询个人信息接口 返回的json对象中的data对象
 *
 *
 * Created by FancyLou on 2015/10/20.
 */
public class PersonInfoData extends PersonalData {


    private List<PersonInfoDataIdentity> identityList;

    private List<PersonInfoDataCompanyDuty> companyDutyList;

    private List<PersonInfoDataDepartmentDuty> departmentDutyList;


    public List<PersonInfoDataIdentity> getIdentityList() {
        return identityList;
    }

    public void setIdentityList(List<PersonInfoDataIdentity> identityList) {
        this.identityList = identityList;
    }

    public List<PersonInfoDataCompanyDuty> getCompanyDutyList() {
        return companyDutyList;
    }

    public void setCompanyDutyList(List<PersonInfoDataCompanyDuty> companyDutyList) {
        this.companyDutyList = companyDutyList;
    }

    public List<PersonInfoDataDepartmentDuty> getDepartmentDutyList() {
        return departmentDutyList;
    }

    public void setDepartmentDutyList(List<PersonInfoDataDepartmentDuty> departmentDutyList) {
        this.departmentDutyList = departmentDutyList;
    }
}
