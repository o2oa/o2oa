package com.x.processplatform.assemble.surface.copier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.content.Work;

public class TestClient {
	@Test
	public void test1() throws Exception {
		Work o = new Work();
		System.out.println(Wo.copier.copy(o));

	}

	public static class Wo extends Work {

		private static final long serialVersionUID = 1954637399762611493L;

		static WrapCopier<Work, Wo> copier = WrapCopierFactory.wo(Work.class, Wo.class, null, null, true);

		public static List<String> Excludes = new ArrayList<>(Arrays.asList("id"));

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}

}
