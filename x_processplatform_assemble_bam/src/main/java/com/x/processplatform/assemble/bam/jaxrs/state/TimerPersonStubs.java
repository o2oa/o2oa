package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.List;

import com.x.organization.core.express.Organization;
import com.x.organization.core.express.wrap.WrapPerson;
import com.x.processplatform.assemble.bam.stub.PersonStub;
import com.x.processplatform.assemble.bam.stub.PersonStubs;

public class TimerPersonStubs extends ActionBase {

	public PersonStubs execute() throws Exception {
		Organization organization = new Organization();
		List<WrapPerson> os = organization.person().listLoginRecent(100);
		PersonStubs stubs = new PersonStubs();
		List<PersonStub> list = new ArrayList<>();
		for (WrapPerson o : os) {
			PersonStub stub = new PersonStub();
			stub.setName(o.getName());
			stub.setValue(o.getName());
			list.add(stub);
		}
		stubs.addAll(list);
		return stubs;
	}

}