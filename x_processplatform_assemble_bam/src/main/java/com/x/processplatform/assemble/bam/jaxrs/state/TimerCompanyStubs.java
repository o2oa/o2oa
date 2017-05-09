package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.utils.SortTools;
import com.x.organization.core.express.Organization;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.CompanyStub;
import com.x.processplatform.assemble.bam.stub.CompanyStubs;

public class TimerCompanyStubs extends ActionBase {

	public CompanyStubs execute() throws Exception {
		Organization organization = new Organization(ThisApplication.context());
		List<WrapCompany> os = organization.company().listAll();
		CompanyStubs stubs = new CompanyStubs();
		List<CompanyStub> list = new ArrayList<>();
		for (WrapCompany o : os) {
			CompanyStub stub = new CompanyStub();
			stub.setName(o.getName());
			stub.setValue(o.getName());
			list.add(stub);
		}
		SortTools.asc(list, "name");
		stubs.addAll(list);
		return stubs;
	}

}