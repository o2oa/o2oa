package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Department;

@Wrap(Department.class)
public class WrapOutDepartment extends Department {

}
