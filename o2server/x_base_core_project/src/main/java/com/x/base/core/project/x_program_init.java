package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.INIT, category = ModuleCategory.OFFICIAL, name = "初始服务器", packageName = "com.x.program.init", containerEntities = {})
public class x_program_init extends Deployable {

}
