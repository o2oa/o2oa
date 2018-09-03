package o2.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.AssembleA;
import com.x.base.core.project.Compilable;
import com.x.base.core.project.x_base_core_project;

public class o2_collect_assemble extends AssembleA {

	public static final String name = "云服务";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("o2.collect.core.entity.Account");
		containerEntities.add("o2.collect.core.entity.Code");
		containerEntities.add("o2.collect.core.entity.Device");
		containerEntities.add("o2.collect.core.entity.Module");
		containerEntities.add("o2.collect.core.entity.Unit");
		containerEntities.add("o2.collect.core.entity.log.PromptErrorLog");
		containerEntities.add("collect.core.entity.log.UnexpectedErrorLog");
		containerEntities.add("o2.collect.core.entity.log.WarnLog");
		dependents.add(x_base_core_project.class);
		dependents.add(o2_base_core_project.class);
		dependents.add(o2_collect_core_entity.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
	}

}
