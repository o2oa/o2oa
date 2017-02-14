package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.utils.SortTools;
import com.x.organization.core.express.Organization;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.processplatform.assemble.bam.stub.DepartmentStub;
import com.x.processplatform.assemble.bam.stub.DepartmentStubs;

public class TimerDepartmentStubs extends ActionBase {

	public DepartmentStubs execute() throws Exception {
		Organization organization = new Organization();
		List<WrapDepartment> os = organization.department().listAll();
		DepartmentStubs stubs = new DepartmentStubs();
		List<DepartmentStub> list = new ArrayList<>();
		for (WrapDepartment o : os) {
			DepartmentStub stub = new DepartmentStub();
			stub.setName(o.getName());
			stub.setValue(o.getName());
			list.add(stub);
		}
		SortTools.asc(list, "name");
		stubs.addAll(list);
		return stubs;
	}

}