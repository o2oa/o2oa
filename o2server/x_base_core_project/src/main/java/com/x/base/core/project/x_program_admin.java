package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ADMIN, category = ModuleCategory.OFFICIAL, name = "管理服务器", packageName = "com.x.program.admin", containerEntities = {})
public class x_program_admin extends Deployable {

}
