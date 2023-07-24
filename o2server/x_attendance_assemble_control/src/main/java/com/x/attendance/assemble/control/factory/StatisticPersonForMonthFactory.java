package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.CriteriaQueryTools;
import com.x.attendance.assemble.control.jaxrs.attendancestatistic.WrapInFilterStatisticPersonForMonth;
import com.x.attendance.entity.StatisticPersonForMonth;
import com.x.attendance.entity.StatisticPersonForMonth_;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class StatisticPersonForMonthFactory extends AbstractFactory {

	private static Logger logger = LoggerFactory.getLogger(StatisticPersonForMonthFactory.class);

	public StatisticPersonForMonthFactory(Business business) throws Exception {
		super(business);
	}

	// @MethodDescribe("获取指定Id的StatisticPersonForMonth信息对象")
	public StatisticPersonForMonth get(String id) throws Exception {
		return this.entityManagerContainer().find(id, StatisticPersonForMonth.class, ExceptionWhen.none);
	}

	// @MethodDescribe("列示全部的StatisticPersonForMonth信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		cq.select(root.get(StatisticPersonForMonth_.id));
		return em.createQuery(cq).getResultList();
	}

	// @MethodDescribe("列示指定Id的StatisticPersonForMonth信息列表")
	public List<StatisticPersonForMonth> list(List<String> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
			return new ArrayList<StatisticPersonForMonth>();
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticPersonForMonth> cq = cb.createQuery(StatisticPersonForMonth.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		Predicate p = root.get(StatisticPersonForMonth_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByUserYearAndMonth(String employeeName, String sYear, String sMonth) throws Exception {
		if (employeeName == null || employeeName.isEmpty()) {
			logger.error(new EmployeeNamesEmptyException());
			return null;
		}

		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		Predicate p = cb.equal(root.get(StatisticPersonForMonth_.employeeName), employeeName);
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		cq.select(root.get(StatisticPersonForMonth_.id));
		return em.createQuery(cq.where(p)).setMaxResults(60).getResultList();
	}

	public List<String> listByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {

		if (unitNameList == null || unitNameList.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}

		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitNameList);
		if (year == null || year.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), year));
		}
		if (month == null || month.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), month));
		}
		cq.select(root.get(StatisticPersonForMonth_.id));
		return em.createQuery(cq.where(p)).setMaxResults(60).getResultList();
	}

	// 排除不需要的组织和人员
	public List<String> listPersonForMonthByUnitYearMonthAndUn(List<String> unitNameList, List<String> unUnitNameList,
			List<String> personNameList, String year, String month) throws Exception {

		if (unitNameList == null || unitNameList.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}

		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitNameList);
		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.unitName), cb.literal(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.employeeName), cb.literal(personNameList)));
		}
		if (year == null || year.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), year));
		}
		if (month == null || month.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), month));
		}
		cq.select(root.get(StatisticPersonForMonth_.id));
		// return em.createQuery(cq.where(p)).setMaxResults(60).getResultList();
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 查询下一页的信息数据
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<StatisticPersonForMonth> listIdsNextWithFilter(String id, Integer count, Object sequence,
			WrapInFilterStatisticPersonForMonth wrapIn) throws Exception {
		// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticPersonForMonth> cq = cb.createQuery(StatisticPersonForMonth.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);

		String order = wrapIn.getOrder();// 排序方式
		if (order == null || order.isEmpty()) {
			order = "DESC";
		}
		String orderFieldName = "";
		if (StringUtils.isNotEmpty(wrapIn.getKey())) {
			orderFieldName = wrapIn.getKey();
		} else {
			orderFieldName = "sequence";
		}
		Order _order = CriteriaQueryTools.setOrder(cb, root, StatisticPersonForMonth_.class, orderFieldName, order);
		Predicate p = cb.isNotNull(root.get(StatisticPersonForMonth_.id));
		if ((null != sequence)) {
			if (StringUtils.equalsIgnoreCase(order, "DESC")) {
				p = cb.and(p, cb.lessThan(root.get(StatisticPersonForMonth_.sequence), sequence.toString()));
			} else {
				p = cb.and(p, cb.greaterThan(root.get(StatisticPersonForMonth_.sequence), sequence.toString()));
			}
		}
		if ((null != wrapIn.getEmployeeName()) && wrapIn.getEmployeeName().size() > 0) {
			p = cb.and(p, root.get(StatisticPersonForMonth_.employeeName).in(wrapIn.getEmployeeName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0) {
			p = cb.and(p, root.get(StatisticPersonForMonth_.unitName).in(wrapIn.getUnitName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p, root.get(StatisticPersonForMonth_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if (StringUtils.isNotEmpty(wrapIn.getStatisticYear())) {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), wrapIn.getStatisticYear()));
		}
		if (StringUtils.isNotEmpty(wrapIn.getStatisticMonth())) {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), wrapIn.getStatisticMonth()));
		}
		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order));
		return query.setMaxResults(count).getResultList();
	}

	/**
	 * 查询上一页的文档信息数据
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<StatisticPersonForMonth> listIdsPrevWithFilter(String id, Integer count, Object sequence,
			WrapInFilterStatisticPersonForMonth wrapIn) throws Exception {
		// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticPersonForMonth> cq = cb.createQuery(StatisticPersonForMonth.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);

		String order = wrapIn.getOrder();// 排序方式
		if (order == null || order.isEmpty()) {
			order = "DESC";
		}
		String orderFieldName = "";
		if (StringUtils.isNotEmpty(wrapIn.getKey())) {
			orderFieldName = wrapIn.getKey();
		} else {
			orderFieldName = "sequence";
		}
		Order _order = CriteriaQueryTools.setOrder(cb, root, StatisticPersonForMonth_.class, orderFieldName, order);
		Predicate p = cb.isNotNull(root.get(StatisticPersonForMonth_.id));
		if ((null != sequence)) {
			if (StringUtils.equalsIgnoreCase(order, "DESC")) {
				p = cb.and(p, cb.greaterThan(root.get(StatisticPersonForMonth_.sequence), sequence.toString()));
			} else {
				p = cb.and(p, cb.lessThan(root.get(StatisticPersonForMonth_.sequence), sequence.toString()));
			}
		}
		if ((null != wrapIn.getEmployeeName()) && wrapIn.getEmployeeName().size() > 0) {
			p = cb.and(p, root.get(StatisticPersonForMonth_.employeeName).in(wrapIn.getEmployeeName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0) {
			p = cb.and(p, root.get(StatisticPersonForMonth_.unitName).in(wrapIn.getUnitName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p, root.get(StatisticPersonForMonth_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if (StringUtils.isNotEmpty(wrapIn.getStatisticYear())) {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), wrapIn.getStatisticYear()));
		}
		if (StringUtils.isNotEmpty(wrapIn.getStatisticMonth())) {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), wrapIn.getStatisticMonth()));
		}
		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order));
		return query.setMaxResults(20).getResultList();
	}

	/**
	 * 查询符合的文档信息总数
	 * 
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter(WrapInFilterStatisticPersonForMonth wrapIn) throws Exception {
		// 先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		Predicate p = cb.isNotNull(root.get(StatisticPersonForMonth_.id));

		if ((null != wrapIn.getEmployeeName()) && wrapIn.getEmployeeName().size() > 0) {
			p = cb.and(p, root.get(StatisticPersonForMonth_.employeeName).in(wrapIn.getEmployeeName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0) {
			p = cb.and(p, root.get(StatisticPersonForMonth_.unitName).in(wrapIn.getUnitName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p, root.get(StatisticPersonForMonth_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if (StringUtils.isNotEmpty(wrapIn.getStatisticYear())) {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), wrapIn.getStatisticYear()));
		}
		if (StringUtils.isNotEmpty(wrapIn.getStatisticMonth())) {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), wrapIn.getStatisticMonth()));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public StatisticPersonForMonth get(String employeeName, String cycleYear, String cycleMonth) throws Exception {
		if (employeeName == null) {
			logger.error(new EmployeeNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticPersonForMonth> cq = cb.createQuery(StatisticPersonForMonth.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		Predicate p = cb.equal(root.get(StatisticPersonForMonth_.employeeName), employeeName);
		if (cycleYear == null || cycleYear.isEmpty()) {
			logger.error(new CycleYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), cycleYear));
		}

		if (cycleMonth == null || cycleMonth.isEmpty()) {
			logger.error(new CycleMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), cycleMonth));
		}
		try {
			return em.createQuery(cq.where(p)).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * 根据组织名称，年份月份，统计员工数量
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long countEmployeeCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth)
			throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		// 查询总数
		cq.select(cb.count(root));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织，统计年月，计算组织内所有员工迟到数总和
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLateCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.lateTimes)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织，统计年月，计算组织内所有员工迟到数总和(排除不参加考勤的员工)
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLateCountByUnitYearAndMonthUn(List<String> unitName, List<String> unUnitNameList,
			List<String> personNameList, String sYear, String sMonth) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.lateTimes)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);

		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.unitName), cb.literal(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.employeeName), cb.literal(personNameList)));
		}
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工出勤天数总和
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAttendanceDayCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth)
			throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.onDutyDayCount)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工出勤天数总和(排除不参加考勤的员工)
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAttendanceDayCountByUnitYearAndMonthUn(List<String> unitName, List<String> unUnitNameList,
			List<String> personNameList, String sYear, String sMonth) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.onDutyDayCount)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);
		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.unitName), cb.literal(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.employeeName), cb.literal(personNameList)));
		}
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工异常打卡数总和
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumAbNormalDutyCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth)
			throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.abNormalDutyCount)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工异常打卡数总和(排除不参加考勤的员工)
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumAbNormalDutyCountByUnitYearAndMonthUn(List<String> unitName, List<String> unUnitNameList,
			List<String> personNameList, String sYear, String sMonth) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.abNormalDutyCount)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);

		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.unitName), cb.literal(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.employeeName), cb.literal(personNameList)));
		}
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工工时不足次数总和
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLackOfTimeCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth)
			throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.lackOfTimeCount)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工工时不足次数总和(排除不参加考勤的员工)
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLackOfTimeCountByUnitYearAndMonthUn(List<String> unitName, List<String> unUnitNameList,
			List<String> personNameList, String sYear, String sMonth) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.lackOfTimeCount)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);

		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.unitName), cb.literal(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.employeeName), cb.literal(personNameList)));
		}
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工早退次数总和
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLeaveEarlyCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth)
			throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.leaveEarlyTimes)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工早退次数总和(排除不参加考勤的员工)
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLeaveEarlyCountByUnitYearAndMonthUn(List<String> unitName, List<String> unUnitNameList,
			List<String> personNameList, String sYear, String sMonth) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.leaveEarlyTimes)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);

		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.unitName), cb.literal(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.employeeName), cb.literal(personNameList)));
		}
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工签退次数总和
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOffDutyCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.offDutyTimes)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工签退次数总和
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOffDutyCountByUnitYearAndMonthUn(List<String> unitName, List<String> unUnitNameList,
			List<String> personNameList, String sYear, String sMonth) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.offDutyTimes)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);

		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.unitName), cb.literal(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.employeeName), cb.literal(personNameList)));
		}
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工签到退次数总和
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOnDutyCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.onDutyTimes)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工签到退次数总和(排除不参加考勤的员工)
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOnDutyCountByUnitYearAndMonthUn(List<String> unitName, List<String> unUnitNameList,
			List<String> personNameList, String sYear, String sMonth) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.onDutyTimes)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);

		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.unitName), cb.literal(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.employeeName), cb.literal(personNameList)));
		}
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工请假天数总和
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth)
			throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.onSelfHolidayCount)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工请假天数总和(排除不参加考勤的员工)
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayCountByUnitYearAndMonthUn(List<String> unitName, List<String> unUnitNameList,
			List<String> personNameList, String sYear, String sMonth) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.onSelfHolidayCount)));
		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);

		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.unitName), cb.literal(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.employeeName), cb.literal(personNameList)));
		}
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工缺勤天数总和
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDayCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth)
			throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.absenceDayCount)));

		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);

		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}

		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据组织列表，统计年月，计算组织内所有员工缺勤天数总和(排除不参加考勤的员工)
	 * 
	 * @param unitName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDayCountByUnitYearAndMonthUn(List<String> unitName, List<String> unUnitNameList,
			List<String> personNameList, String sYear, String sMonth) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		// 查询总数
		cq.select(cb.sum(root.get(StatisticPersonForMonth_.absenceDayCount)));

		Predicate p = root.get(StatisticPersonForMonth_.unitName).in(unitName);

		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.unitName), cb.literal(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p, cb.isNotMember(root.get(StatisticPersonForMonth_.employeeName), cb.literal(personNameList)));
		}
		if (sYear == null || sYear.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}

		if (sMonth == null || sMonth.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
}