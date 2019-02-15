package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.StatisticTopUnitForMonth;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListStmForTopUnitNextWithFilter extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionListStmForTopUnitNextWithFilter.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<StatisticTopUnitForMonth> statisticList = null;
		WrapInFilterStatisticTopUnitForMonth wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterStatisticTopUnitForMonth.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if(check ){
			try {
				EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Business business = new Business(emc);

				// 查询出ID对应的记录的sequence
				Object sequence = null;
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
				} else {
					if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
						sequence = PropertyUtils.getProperty(
								emc.find(id, StatisticTopUnitForMonth.class ),  JpaObject.sequence_FIELDNAME);
					}
				}

				//将下级组织的数据纳入组织统计数据查询范围
				List<String> unitNameList = getUnitNameList(wrapIn.getTopUnitName(), wrapIn.getUnitName(), effectivePerson.getDebugger() );			
				wrapIn.setUnitName(unitNameList);
				// 从数据库中查询符合条件的一页数据对象
				statisticList = business.getStatisticTopUnitForMonthFactory().listIdsNextWithFilter(id, count, sequence,
						wrapIn);

				// 从数据库中查询符合条件的对象总数
				total = business.getStatisticTopUnitForMonthFactory().getCountWithFilter(wrapIn);

				// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = Wo.copier.copy( statisticList );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		result.setCount(total);
		result.setData(wraps);
		return result;
	}

	public static class Wo extends StatisticTopUnitForMonth  {
		
		private static final long serialVersionUID = -5076990764713538973L;		
		
		public static WrapCopier<StatisticTopUnitForMonth, Wo> copier = 
				WrapCopierFactory.wo( StatisticTopUnitForMonth.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}