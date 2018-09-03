package testdata;

import java.time.LocalTime;

import com.x.crm.core.entity.Clue;
import com.x.crm.core.entity.CrmBaseConfig;
import com.x.crm.core.entity.CustomerBaseInfo;
import com.x.crm.core.entity.Opportunity;

public class CreateTestData {
	public CustomerBaseInfo createData(CustomerBaseInfo BaseInfo) {
		LocalTime time = LocalTime.now();
		String TestString = "test" + time;
		BaseInfo.setAddresslatitude(TestString);
		BaseInfo.setAddresslongitude(TestString);
		BaseInfo.setArea(TestString);
		BaseInfo.setCity(TestString);
		BaseInfo.setCountry(TestString);
		BaseInfo.setCountry(TestString);
		BaseInfo.setCounty(TestString);
		BaseInfo.setEmail(TestString);
		BaseInfo.setHouseno(TestString);
		BaseInfo.setIndustry(TestString);
		BaseInfo.setIndustryfirst(TestString);
		BaseInfo.setIndustrysecond(TestString);
		BaseInfo.setLevel(TestString);
		BaseInfo.setCustomername("Name" + TestString);
		BaseInfo.setProvince(TestString);
		BaseInfo.setQqno(TestString);
		BaseInfo.setCustomerrank(TestString);
		BaseInfo.setRemark(TestString);
		BaseInfo.setSource(TestString);
		BaseInfo.setState(TestString);
		BaseInfo.setTelno(TestString);
		BaseInfo.setUrl(TestString);
		BaseInfo.setWebchat(TestString);
		return BaseInfo;
	}

	public Opportunity creatDataOpportunity(Opportunity opportunity) {
		LocalTime time = LocalTime.now();
		String TestString = "商机：" + time;
		opportunity.setBelongerid(TestString);
		opportunity.setCustomerid(TestString);
		opportunity.setExpecteddealtime(TestString);
		opportunity.setExpectedsalesamount(TestString);
		opportunity.setOpportunityname("Name" + TestString);
		opportunity.setRemark(TestString);
		opportunity.setSaleactionid(TestString);
		opportunity.setUddate1(TestString);
		opportunity.setUdmtext1(TestString);
		opportunity.setUdmtext2(TestString);
		opportunity.setUdssel1(TestString);
		opportunity.setUdssel2(TestString);
		opportunity.setUdssel3(TestString);
		return opportunity;

	}

	public Clue CreatDataClue(Clue clue) {
		LocalTime time = LocalTime.now();
		String TestString = "线索：" + time;
		clue.setAddress(TestString);
		clue.setCompany(TestString);
		clue.setContactway(TestString);
		clue.setDepartment(TestString);
		clue.setEmail(TestString);
		clue.setMarketingeventid(TestString);
		clue.setMobile(TestString);
		clue.setPosition(TestString);
		clue.setRemark(TestString);
		clue.setSalescluename(TestString);
		clue.setSalescluepoolid(TestString);
		clue.setSource(TestString);
		clue.setUrl(TestString);

		return clue;
	}

	public CrmBaseConfig CreatDataBaseConfig(CrmBaseConfig crmbaseconfig) {
		crmbaseconfig.setBaseconfigtype("客户");
		crmbaseconfig.setConfigname("客户类型");
		crmbaseconfig.setConfigvalue("测试1");
		crmbaseconfig.setDescription("创建用户测试");
		crmbaseconfig.setOrdernumber(1);
		crmbaseconfig.setValuetype("text");

		return crmbaseconfig;
	}

}
