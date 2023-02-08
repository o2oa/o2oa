package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle.UnitCycleInfoEntity;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class AttendanceStatisticalCycleService {

    private CacheCategory cache_AttendanceStatisticalCycle = new CacheCategory(AttendanceStatisticalCycle.class);

    private static Logger logger = LoggerFactory.getLogger(AttendanceStatisticalCycleService.class);
    private UserManagerService userManagerService = new UserManagerService();
    private DateOperation dateOperation = new DateOperation();

    /**
     * 从缓存中获取所有的考勤周期配置
     * 
     * @return
     * @throws Exception
     */
    public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getAllStatisticalCycleMapWithCache(
            Boolean debugger) throws Exception {
        CacheKey cacheKey = new CacheKey(this.getClass(), "map", "all");

        Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap = null;
        Optional<?> optional = CacheManager.get(cache_AttendanceStatisticalCycle, cacheKey);
        if (optional.isPresent()) {
            return ((Map<String, Map<String, List<AttendanceStatisticalCycle>>>) optional.get());
        } else {
            return getCycleMapFormAllCycles(false);
        }
    }

    public List<AttendanceStatisticalCycle> listAll(EntityManagerContainer emc) throws Exception {
        Business business = new Business(emc);
        return business.getAttendanceStatisticalCycleFactory().listAll();
    }

    public List<AttendanceStatisticalCycle> list(EntityManagerContainer emc, List<String> ids) throws Exception {
        Business business = new Business(emc);
        return business.getAttendanceStatisticalCycleFactory().list(ids);
    }

    public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getCycleMapFormAllCycles(Boolean debugger)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return getCycleMapFormAllCycles(emc, debugger);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 将所有的统计周期配置信息组织成一个大的Map
     * 
     * @param emc
     * @param debugger
     * @return
     * @throws Exception
     */
    public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getCycleMapFormAllCycles(
            EntityManagerContainer emc, Boolean debugger) throws Exception {
        return getCycleMapFormAllCycles(emc, listAll(emc), debugger);
    }

    /**
     * 将指定的统计周期配置信息列表组织成一个大的Map
     * 
     * @param emc
     * @param cycles
     * @param debugger
     * @return
     * @throws Exception
     */
    public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getCycleMapFormAllCycles(
            EntityManagerContainer emc, List<AttendanceStatisticalCycle> cycles, Boolean debugger) throws Exception {
        Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = new ConcurrentHashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
        Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
        List<AttendanceStatisticalCycle> unitCycles = null;
        if (cycles != null && cycles.size() > 0) {
            for (AttendanceStatisticalCycle cycle : cycles) {
                unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get(cycle.getTopUnitName());
                if (unitAttendanceStatisticalCycleMap == null) {
                    unitAttendanceStatisticalCycleMap = new ConcurrentHashMap<String, List<AttendanceStatisticalCycle>>();
                    topUnitAttendanceStatisticalCycleMap.put(cycle.getTopUnitName(), unitAttendanceStatisticalCycleMap);
                }
                unitCycles = unitAttendanceStatisticalCycleMap.get(cycle.getUnitName());
                if (unitCycles == null) {
                    unitCycles = new ArrayList<AttendanceStatisticalCycle>();
                    unitAttendanceStatisticalCycleMap.put(cycle.getUnitName(), unitCycles);
                }
                putDistinctCycleInList(cycle,
                        new UnitCycleInfoEntity(cycle.getUnitName(), cycle.getTopUnitName(), unitCycles),
                        topUnitAttendanceStatisticalCycleMap);
            }
        } else {
            logger.debug(debugger, ">>>>>>>>>>查询所有的统计周期配置，未查询到任何配置。");
        }
        return topUnitAttendanceStatisticalCycleMap;
    }

    /**
     * 根据顶层组织，组织，年月获取一个统计周期配置 如果不存在，则新建一个周期配置
     * 
     * @param q_topUnitName
     * @param q_unitName
     * @param recordDate
     * @param topUnitAttendanceStatisticalCycleMap
     * @param debugger
     * @return
     * @throws Exception
     */
    public synchronized AttendanceStatisticalCycle getStatisticCycleWithStartAndEnd(String q_topUnitName,
            String q_unitName, Date recordDate,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
            Boolean debugger) throws Exception {
        AttendanceStatisticalCycle attendanceStatisticalCycle = null;
        UnitCycleInfoEntity unitCycleInfoEntity = null;
        Date cycleStartDate = null;
        Date cycleEndDate = null;
        Boolean hasConfig = false;

        if (topUnitAttendanceStatisticalCycleMap == null) {
            topUnitAttendanceStatisticalCycleMap = getCycleMapFormAllCycles(debugger);
        }

        // 从Map里查询与顶层组织和组织相应的周期配置信息
        if (topUnitAttendanceStatisticalCycleMap != null) {
            unitCycleInfoEntity = analyseStatisticCycleWithUnitName(q_topUnitName, q_unitName,
                    topUnitAttendanceStatisticalCycleMap, debugger);
        } else {
            logger.debug(debugger, "统计周期配置为空，顶层组织为*组织为*，系统中没有任何配置");
            unitCycleInfoEntity = new UnitCycleInfoEntity();
            unitCycleInfoEntity.setTopUnitName("*");
            unitCycleInfoEntity.setUnitName("*");
        }
        if (unitCycleInfoEntity.getUnitCycles() != null && unitCycleInfoEntity.getUnitCycles().size() > 0) {
            // 说明配置信息里有配置，看看配置信息里的数据是否满足打卡信息需要的周期
            for (AttendanceStatisticalCycle temp : unitCycleInfoEntity.getUnitCycles()) {
                // 如果年份为*
                cycleStartDate = temp.getCycleStartDate();
                cycleEndDate = temp.getCycleEndDate();
                if (recordDate.getTime() >= cycleStartDate.getTime()
                        && recordDate.getTime() <= cycleEndDate.getTime()) {
                    logger.debug(debugger,
                            "根据时间对比获取到合适的周期：" + recordDate + "," + cycleStartDate + " ~ " + cycleEndDate);
                    return temp;
                }
            }
        }
        if (!hasConfig) {
            logger.debug(debugger, "未查询到合适的周期，根据打卡信息创建一条自然月的周期");
            // 说明没有找到任何相关的配置，那么新创建一条配置
            // 创建并且持久化一条统计周期配置
            attendanceStatisticalCycle = createNewCycleInfo(recordDate, q_topUnitName, q_unitName);
            putDistinctCycleInList(attendanceStatisticalCycle, unitCycleInfoEntity,
                    topUnitAttendanceStatisticalCycleMap);
            return attendanceStatisticalCycle;
        }
        return null;
    }

    /**
     * TODO 根据顶层组织，组织，年月获取一个统计周期配置
     * 如果不存在，则新建一个周期配置
     * 
     * @param q_topUnitName
     * @param q_unitName
     * @param cycleYear
     * @param cycleMonth
     * @param topUnitAttendanceStatisticalCycleMap
     * @return
     * @throws Exception
     */
    public synchronized AttendanceStatisticalCycle getStatisticCycle(String q_topUnitName, String q_unitName,
            String cycleYear, String cycleMonth,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
            Boolean debugger) throws Exception {
        AttendanceStatisticalCycle attendanceStatisticalCycle = null;
        UnitCycleInfoEntity unitCycleInfoEntity = null;
        Boolean hasConfig = false;
        String topUnitName = null, unitName = null;

        if (Integer.parseInt(cycleMonth) < 10) {
            cycleMonth = "0" + Integer.parseInt(cycleMonth);
        }

        // 从Map里查询与顶层组织和组织相应的周期配置信息
        if (topUnitAttendanceStatisticalCycleMap != null) {
            unitCycleInfoEntity = analyseStatisticCycleWithUnitName(q_topUnitName, q_unitName,
                    topUnitAttendanceStatisticalCycleMap, debugger);
        } else {
            logger.debug(debugger, "统计周期配置为空，顶层组织为*组织为*，系统中没有任何配置");
            unitCycleInfoEntity = new UnitCycleInfoEntity();
            unitCycleInfoEntity.setTopUnitName("*");
            unitCycleInfoEntity.setUnitName("*");
        }

        if (unitCycleInfoEntity.getUnitCycles() != null && unitCycleInfoEntity.getUnitCycles().size() > 0) {
            // 说明配置信息里有配置，看看配置信息里的数据是否满足打卡信息需要的周期
            for (AttendanceStatisticalCycle temp : unitCycleInfoEntity.getUnitCycles()) {
                if (temp.getCycleYear().equals(cycleYear) && temp.getCycleMonth().equals(cycleMonth)) {
                    hasConfig = true;
                    return temp;
                }
            }
        } else {
            logger.debug(debugger, "根据顶层组织[" + topUnitName + "]和组织[" + unitName + "]未获取到任何周期数据，需要创建新的统计周期数据......");
        }

        if (!hasConfig) {
            logger.debug(debugger, "未查询到合适的周期，根据打卡信息创建一条自然月的周期");
            // 说明没有找到任何相关的配置，那么新创建一条配置
            Date day = dateOperation.getDateFromString(cycleYear + "-" + cycleMonth + "-01");
            // 创建并且持久化一条统计周期配置
            attendanceStatisticalCycle = createNewCycleInfo(day, topUnitName, unitName);
            putDistinctCycleInList(attendanceStatisticalCycle, unitCycleInfoEntity,
                    topUnitAttendanceStatisticalCycleMap);
            return attendanceStatisticalCycle;
        }
        return null;
    }

    /**
     * 根据顶层组织，组织，年月获取一个统计周期配置 如果不存在，则新建一个周期配置
     * 
     * @param attendanceStatisticRequireLog
     * @param topUnitAttendanceStatisticalCycleMap
     * @param debugger
     * @return
     * @throws Exception
     */
    public AttendanceStatisticalCycle getStatisticCycleByEmployee(
            AttendanceStatisticRequireLog attendanceStatisticRequireLog,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
            Boolean debugger) throws Exception {
        if (attendanceStatisticRequireLog == null) {
            return null;
        }
        return getStatisticCycleByEmployee(attendanceStatisticRequireLog.getStatisticKey(),
                attendanceStatisticRequireLog.getStatisticYear(), attendanceStatisticRequireLog.getStatisticMonth(),
                topUnitAttendanceStatisticalCycleMap, debugger);
    }

    public synchronized AttendanceStatisticalCycle getStatisticCycleByEmployee(String employeeName, String cycleYear,
            String cycleMonth,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
            Boolean debugger) throws Exception {
        AttendanceStatisticalCycle attendanceStatisticalCycle = null;
        Boolean hasConfig = false;
        List<String> identities = null;
        UnitCycleInfoEntity unitCycleInfoEntity = null;
        String unitName = null;

        if (Integer.parseInt(cycleMonth) < 10) {
            cycleMonth = "0" + Integer.parseInt(cycleMonth);
        }

        // 从Map里查询与顶层组织和组织相应的周期配置信息
        if (topUnitAttendanceStatisticalCycleMap != null) {
            // 需要查询用户的身份
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                identities = userManagerService.listIdentitiesWithPerson(employeeName);
                if (identities == null || identities.size() == 0) {// 该员工目前没有分配身份
                    throw new Exception("can not get identity of person:" + employeeName + ".");
                }
            } catch (Exception e) {
                logger.warn("系统在查询员工[" + employeeName + "]在系统中存在的身份时发生异常！");
                // modify by ray 20221101 这个异常会导致后面的程序不执行下去
                // throw e;
            }

            if (identities != null && identities.size() > 0) {
                for (String identity : identities) {
                    unitName = userManagerService.getUnitNameWithIdentity(identity);
                    unitCycleInfoEntity = analyseStatisticCycleWithUnitName(unitName,
                            topUnitAttendanceStatisticalCycleMap, debugger);

                    if (unitCycleInfoEntity.getUnitCycles() != null && unitCycleInfoEntity.getUnitCycles().size() > 0) {
                        // 说明配置信息里有配置，看看配置信息里的数据是否满足打卡信息需要的周期
                        for (AttendanceStatisticalCycle temp : unitCycleInfoEntity.getUnitCycles()) {
                            if (temp.getCycleYear().equals(cycleYear) && temp.getCycleMonth().equals(cycleMonth)) {
                                hasConfig = true;
                                return temp;
                            }
                        }
                    }
                }
            }
        } else {
            unitCycleInfoEntity = new UnitCycleInfoEntity();
            unitCycleInfoEntity.setTopUnitName("*");
            unitCycleInfoEntity.setUnitName("*");
        }

        if (!hasConfig) {
            logger.debug(debugger, ">>>>>>>>>>未查询到合适的周期，根据打卡信息创建一条自然月的周期");
            // 说明没有找到任何相关的配置，那么新创建一条配置
            Date day = dateOperation.getDateFromString(cycleYear + "-" + cycleMonth + "-01");

            // 创建并且持久化一条统计周期配置
            attendanceStatisticalCycle = createNewCycleInfo(day, unitCycleInfoEntity.getTopUnitName(),
                    unitCycleInfoEntity.getUnitName());

            putDistinctCycleInList(attendanceStatisticalCycle, unitCycleInfoEntity,
                    topUnitAttendanceStatisticalCycleMap);

            return attendanceStatisticalCycle;
        }
        return null;
    }

    private UnitCycleInfoEntity analyseStatisticCycleWithUnitName(String unitName,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
            Boolean debugger) throws Exception {
        String topUnitName = null;
        if (unitName == null) {
            throw new Exception("unitName is null!");
        }
        topUnitName = userManagerService.getTopUnitNameWithUnitName(unitName);
        if (topUnitName == null || topUnitName.isEmpty()) {
            topUnitName = unitName;
        }

        return analyseStatisticCycleWithUnitName(topUnitName, unitName, topUnitAttendanceStatisticalCycleMap, debugger);
    }

    private UnitCycleInfoEntity analyseStatisticCycleWithUnitName(String topUnitName, String unitName,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
            Boolean debugger) throws Exception {
        Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
        List<AttendanceStatisticalCycle> unitCycles = null;

        if (unitName == null) {
            throw new Exception("unitName is null!");
        }
        if (topUnitName == null) {
            throw new Exception("topUnitName is null!");
        }

        unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get(topUnitName);

        if (unitAttendanceStatisticalCycleMap != null) {
            logger.debug(debugger, ">>>>>>>>>>查询到顶层组织[" + topUnitName + "]的统计周期配置");
            // 存在当前顶层组织的配置信息，再根据组织查询组织的配置列表是否存在[topUnit - unit]
            unitCycles = unitAttendanceStatisticalCycleMap.get(topUnitName);
            if (unitCycles == null) {
                logger.debug(debugger, ">>>>>>>>>>未查询到顶层组织[" + topUnitName + "]的统计周期配置， 组织设置为*");
                unitName = "*";
                unitCycles = unitAttendanceStatisticalCycleMap.get("*");
            }
        } else {
            logger.debug(debugger, ">>>>>>>>>>没有顶层组织[" + topUnitName + "]的统计周期配置，顶层组织设置为*");
            // 找顶层组织为*的Map看看是否存在
            unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get("*");
            topUnitName = "*";
            if (unitAttendanceStatisticalCycleMap != null) {
                logger.debug(debugger, ">>>>>>>>>>查询到顶层组织[*]的统计周期配置，再查询组织为[*]的配置列表");
                // 存在当前顶层组织的配置信息，再根据组织查询组织的配置列表是否存在[topUnit - unit]
                unitCycles = unitAttendanceStatisticalCycleMap.get(unitName);
                if (unitCycles == null) {
                    logger.debug(debugger, ">>>>>>>>>>未查询到顶层组织[*]组织[" + unitName + "]的统计周期配置， 组织设置为*,查询组织[*]的配置");
                    unitName = "*";
                    unitCycles = unitAttendanceStatisticalCycleMap.get("*");
                }
            } else {
                logger.debug(debugger, ">>>>>>>>>>未查询到顶层组织[*]的统计周期配置，顶层组织设置为*，系统中没有任何配置");
                unitName = "*";
            }
        }

        return new UnitCycleInfoEntity(unitName, topUnitName, unitCycles);
    }

    /**
     * TODO 根据日期和组织信息，创建一个新的统计周期信息对象
     * 
     * @param date
     * @param topUnitName
     * @param unitName
     * @return
     * @throws Exception
     */
    private AttendanceStatisticalCycle createNewCycleInfo(Date date, String topUnitName, String unitName)
            throws Exception {
        if (date == null) {
            throw new Exception("date is null!");
        }
        if (topUnitName == null) {
            throw new Exception("topUnitName is null!");
        }
        if (unitName == null) {
            throw new Exception("unitName is null!");
        }

        String cycleMonth = dateOperation.getMonth(date);
        AttendanceStatisticalCycle attendanceStatisticalCycle = null;

        if (Integer.parseInt(cycleMonth) < 10) {
            cycleMonth = "0" + Integer.parseInt(cycleMonth);
        }
        attendanceStatisticalCycle = new AttendanceStatisticalCycle();
        attendanceStatisticalCycle.setTopUnitName(topUnitName);
        attendanceStatisticalCycle.setUnitName(unitName);
        attendanceStatisticalCycle.setCycleMonth(cycleMonth);
        attendanceStatisticalCycle.setCycleYear(dateOperation.getYear(date));
        attendanceStatisticalCycle.setCycleStartDate(dateOperation.getFirstDateInMonth(date));
        attendanceStatisticalCycle.setCycleStartDateString(dateOperation.getFirstDateStringInMonth(date));
        attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(date));
        attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(date));
        attendanceStatisticalCycle.setDescription("系统自动创建");
        try {
            attendanceStatisticalCycle = saveCycleInfo(attendanceStatisticalCycle);
        } catch (Exception e) {
            logger.warn("系统在保存新的统计周期信息时发生异常！");
            throw e;
        }
        return attendanceStatisticalCycle;
    }

    /**
     * TODO 向数据库保存统计周期信息（synchronized）
     * 
     * @param attendanceStatisticalCycle
     * @return
     * @throws Exception
     */
    private synchronized AttendanceStatisticalCycle saveCycleInfo(AttendanceStatisticalCycle attendanceStatisticalCycle)
            throws Exception {
        if (attendanceStatisticalCycle == null) {
            throw new Exception("attendanceStatisticalCycle is null!");
        }
        AttendanceStatisticalCycle temp = null;
        Business business = null;
        List<String> ids = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            business = new Business(emc);
            ids = business.getAttendanceStatisticalCycleFactory().listByParameter(
                    attendanceStatisticalCycle.getTopUnitName(),
                    attendanceStatisticalCycle.getUnitName(),
                    attendanceStatisticalCycle.getCycleYear(),
                    attendanceStatisticalCycle.getCycleMonth());
            if (ids != null && !ids.isEmpty()) {
                for (int i = 0; i < ids.size(); i++) {
                    temp = emc.find(ids.get(i), AttendanceStatisticalCycle.class);
                    if (i > 0) {
                        emc.beginTransaction(AttendanceStatisticalCycle.class);
                        emc.remove(temp, CheckRemoveType.all);
                        emc.commit();
                    }
                }
            } else {
                emc.beginTransaction(AttendanceStatisticalCycle.class);
                emc.persist(attendanceStatisticalCycle, CheckPersistType.all);
                emc.commit();
            }
        } catch (Exception e) {
            logger.warn("系统在保存新的统计周期信息时发生异常！");
            throw e;
        }
        return attendanceStatisticalCycle;
    }

    /**
     * TODO 将单个对象放到一个List里，并且去重复（synchronized）
     * 
     * @param cycle
     * @param topUnitCycles
     * @return
     */
    public synchronized List<AttendanceStatisticalCycle> putDistinctCycleInList(AttendanceStatisticalCycle cycle,
            UnitCycleInfoEntity unitCycleInfoEntity,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap) {
        List<AttendanceStatisticalCycle> unitCycles = null;
        Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
        // 把对象放到Map里进行返回
        if (topUnitAttendanceStatisticalCycleMap == null) {
            topUnitAttendanceStatisticalCycleMap = new ConcurrentHashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
        }

        unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap
                .get(unitCycleInfoEntity.getTopUnitName());
        if (unitAttendanceStatisticalCycleMap == null) {
            unitAttendanceStatisticalCycleMap = new ConcurrentHashMap<String, List<AttendanceStatisticalCycle>>();
            topUnitAttendanceStatisticalCycleMap.put(unitCycleInfoEntity.getTopUnitName(),
                    unitAttendanceStatisticalCycleMap);
        }

        unitCycles = unitAttendanceStatisticalCycleMap.get(unitCycleInfoEntity.getUnitName());

        if (unitCycles == null) {
            unitCycles = new ArrayList<AttendanceStatisticalCycle>();
            unitAttendanceStatisticalCycleMap.put(unitCycleInfoEntity.getUnitName(), unitCycles);
        }

        for (AttendanceStatisticalCycle attendanceStatisticalCycle : unitCycles) {
            if (attendanceStatisticalCycle.getTopUnitName() != null
                    && attendanceStatisticalCycle.getUnitName() != null
                    && attendanceStatisticalCycle.getCycleYear() != null
                    && attendanceStatisticalCycle.getCycleMonth() != null) {
                if (attendanceStatisticalCycle.getTopUnitName().equals(cycle.getTopUnitName())
                        && attendanceStatisticalCycle.getUnitName().equals(cycle.getUnitName())
                        && attendanceStatisticalCycle.getCycleYear().equals(cycle.getCycleYear())
                        && attendanceStatisticalCycle.getCycleMonth().equals(cycle.getCycleMonth())

                ) {
                    return unitCycles;
                }
            }
        }
        unitCycles.add(cycle);
        return unitCycles;
    }
}
