package com.x.attendance.assemble.control.schedule.v2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.x.attendance.entity.v2.AttendanceV2Holiday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;

public class AttendanceV2HolidaySyncTask extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceV2HolidaySyncTask.class);
    private static final String HOLIDAY_URL = "https://cdn.jsdelivr.net/gh/NateScarlet/holiday-cn@master/%d.json";
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("======================新版考勤中国节假日数据同步定时器开始执行==============================");
        }
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        syncYear(currentYear);
        syncYear(currentYear + 1);
        if (logger.isDebugEnabled()) {
            logger.debug("======================新版考勤中国节假日数据同步定时器执行完成==============================");
        }
    }

    private void syncYear(int year) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            if (existsYearData(emc, year)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("本地已经存在 {} 年中国节假日数据，跳过远程读取。", year);
                }
                return;
            }
            HolidaySource source = fetchHolidaySource(year);
            if (source == null || source.getDays() == null || source.getDays().isEmpty()) {
                logger.warn("{} 年中国节假日数据为空，跳过写入。", year);
                return;
            }
            int persistCount = persistDays(emc, year, source.getDays());
            logger.info("{} 年中国节假日数据同步完成，写入 {} 条。", year, persistCount);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private boolean existsYearData(EntityManagerContainer emc, int year) throws Exception {
        List<AttendanceV2Holiday> list = emc.listEqualAndEqual(AttendanceV2Holiday.class,
                AttendanceV2Holiday.year_FIELDNAME, year, AttendanceV2Holiday.source_FIELDNAME,
                AttendanceV2Holiday.SOURCE_SYNC);
        return list != null && !list.isEmpty();
    }

    private HolidaySource fetchHolidaySource(int year) throws Exception {
        String url = String.format(HOLIDAY_URL, year);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestMethod("GET");
            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                logger.warn("读取中国节假日数据失败，年份: {}, 响应码: {}, 地址: {}", year, code, url);
                return null;
            }
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
            if (builder.length() == 0) {
                return null;
            }
            return XGsonBuilder.instance().fromJson(builder.toString(), HolidaySource.class);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private int persistDays(EntityManagerContainer emc, int year, List<HolidaySourceDay> days) throws Exception {
        int count = 0;
        Set<String> dateSet = new HashSet<>();
        List<AttendanceV2Holiday> existingList = emc.listEqual(AttendanceV2Holiday.class,
                AttendanceV2Holiday.year_FIELDNAME, year);
        if (existingList != null) {
            for (AttendanceV2Holiday holiday : existingList) {
                if (StringUtils.isNotBlank(holiday.getDateString())) {
                    dateSet.add(holiday.getDateString());
                }
            }
        }
        emc.beginTransaction(AttendanceV2Holiday.class);
        for (HolidaySourceDay day : days) {
            if (day == null || StringUtils.isBlank(day.getDate()) || day.getIsOffDay() == null
                    || !dateSet.add(day.getDate())) {
                continue;
            }
            AttendanceV2Holiday holiday = new AttendanceV2Holiday();
            holiday.setYear(year);
            holiday.setDateString(day.getDate());
            holiday.setName(day.getName());
            holiday.setOffDay(day.getIsOffDay());
            holiday.setSource(AttendanceV2Holiday.SOURCE_SYNC);
            emc.persist(holiday, CheckPersistType.all);
            count++;
        }
        emc.commit();
        return count;
    }

    public static class HolidaySource extends GsonPropertyObject {

        private static final long serialVersionUID = 3741752635515386138L;

        private Integer year;
        private List<HolidaySourceDay> days;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public List<HolidaySourceDay> getDays() {
            return days;
        }

        public void setDays(List<HolidaySourceDay> days) {
            this.days = days;
        }
    }

    public static class HolidaySourceDay extends GsonPropertyObject {

        private static final long serialVersionUID = 7082737076206215952L;

        private String name;
        private String date;
        private Boolean isOffDay;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Boolean getIsOffDay() {
            return isOffDay;
        }

        public void setIsOffDay(Boolean isOffDay) {
            this.isOffDay = isOffDay;
        }
    }
}
