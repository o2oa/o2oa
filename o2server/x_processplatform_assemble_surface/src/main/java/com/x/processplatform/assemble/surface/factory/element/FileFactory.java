package com.x.processplatform.assemble.surface.factory.element;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.File;

public class FileFactory extends ElementFactory {

	public FileFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public List<File> pick(List<String> flags) throws Exception {
		return this.pick(flags, File.class);
	}

	public File pick(String flag) throws Exception {
		return this.pick(flag, File.class);
	}

	public <T extends File> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(File::getAlias, Comparator.nullsLast(String::compareTo))
						.thenComparing(File::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}
}