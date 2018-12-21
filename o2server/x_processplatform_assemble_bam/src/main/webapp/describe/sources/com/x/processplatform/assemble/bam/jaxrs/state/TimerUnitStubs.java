package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.project.organization.Unit;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.UnitStub;
import com.x.processplatform.assemble.bam.stub.UnitStubs;

public class TimerUnitStubs extends ActionBase {

	public void execute(Business business) throws Exception {
		List<Unit> os = business.organization().unit().listAllObject();
		UnitStubs stubs = new UnitStubs();
		List<UnitStub> list = new ArrayList<>();
		for (Unit o : os) {
			UnitStub stub = new UnitStub();
			stub.setName(o.getName());
			stub.setValue(o.getDistinguishedName());
			list.add(stub);
		}
		list = list.stream().sorted(Comparator.comparing(UnitStub::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		stubs.addAll(list);
		ThisApplication.state.setUnitStubs(stubs);
	}

}