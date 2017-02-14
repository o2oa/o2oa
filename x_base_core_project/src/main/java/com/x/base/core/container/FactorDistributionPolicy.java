package com.x.base.core.container;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.openjpa.slice.DistributionPolicy;

import com.x.base.core.entity.SliceJpaObject;

public class FactorDistributionPolicy implements DistributionPolicy {

	// private static String distributeFactor_attribute = "distributeFactor";
	//
	// public String distribute(Object pc, List<String> slices, Object context)
	// {
	// try {
	// String str = BeanUtils.getProperty(pc, distributeFactor_attribute);
	// Integer factor = Integer.valueOf(str);
	// return slices.get(factor % slices.size());
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// private static String distributeFactor_attribute = "distributeFactor";

	public String distribute(Object pc, List<String> slices, Object context) {
		try {
			Integer factor = (Integer) PropertyUtils.getProperty(pc, SliceJpaObject.DISTRIBUTEFACTOR);
			return slices.get(factor % slices.size());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}