package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2Holiday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;

public class ActionHolidayImport extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }

            Wi wi = parseWi(jsonElement);
            List<HolidayItem> items = listHolidayItems(wi, jsonElement);
            if (items == null || items.isEmpty()) {
                throw new ExceptionEmptyParameter("节假日数据");
            }

            boolean overwrite = BooleanUtils.isTrue(wi.getOverwrite());
            Wo wo = new Wo();
            wo.setErrorItems(new ArrayList<>());
            Set<String> dateSet = new HashSet<>();

            emc.beginTransaction(AttendanceV2Holiday.class);
            for (int i = 0; i < items.size(); i++) {
                HolidayItem item = items.get(i);
                try {
                    AttendanceV2Holiday holiday = buildHoliday(wi, item, dateSet);
                    List<AttendanceV2Holiday> existingList = emc.listEqual(AttendanceV2Holiday.class,
                            AttendanceV2Holiday.dateString_FIELDNAME, holiday.getDateString());
                    if (existingList == null || existingList.isEmpty()) {
                        emc.persist(holiday, CheckPersistType.all);
                        wo.setInserted(wo.getInserted() + 1);
                    } else if (overwrite) {
                        AttendanceV2Holiday existing = existingList.get(0);
                        existing.setYear(holiday.getYear());
                        existing.setDateString(holiday.getDateString());
                        existing.setName(holiday.getName());
                        existing.setOffDay(holiday.getOffDay());
                        existing.setSource(AttendanceV2Holiday.SOURCE_API);
                        wo.setUpdated(wo.getUpdated() + 1);
                    } else {
                        wo.setSkipped(wo.getSkipped() + 1);
                    }
                } catch (Exception e) {
                    ImportError error = new ImportError();
                    error.setIndex(i + 1);
                    error.setDateString(item == null ? null : item.dateValue());
                    error.setMessage(e.getLocalizedMessage());
                    wo.getErrorItems().add(error);
                }
            }
            emc.commit();

            ActionResult<Wo> result = new ActionResult<>();
            wo.setTotal(items.size());
            wo.setErrors(wo.getErrorItems().size());
            result.setCount((long) (wo.getInserted() + wo.getUpdated()));
            result.setData(wo);
            return result;
        }
    }

    private Wi parseWi(JsonElement jsonElement) throws Exception {
        if (jsonElement != null && jsonElement.isJsonArray()) {
            Wi wi = new Wi();
            wi.setHolidayList(new ArrayList<>());
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                wi.getHolidayList().add(gson.fromJson(element, HolidayItem.class));
            }
            return wi;
        }
        return this.convertToWrapIn(jsonElement, Wi.class);
    }

    private List<HolidayItem> listHolidayItems(Wi wi, JsonElement jsonElement) {
        if (wi == null) {
            return new ArrayList<>();
        }
        if (wi.getDays() != null && !wi.getDays().isEmpty()) {
            return wi.getDays();
        }
        if (wi.getHolidayList() != null && !wi.getHolidayList().isEmpty()) {
            return wi.getHolidayList();
        }
        if (wi.getHolidays() != null && !wi.getHolidays().isEmpty()) {
            return wi.getHolidays();
        }
        if (jsonElement != null && jsonElement.isJsonObject()) {
            HolidayItem item = gson.fromJson(jsonElement, HolidayItem.class);
            if (StringUtils.isNotBlank(item.dateValue()) || item.offDayValue() != null) {
                List<HolidayItem> list = new ArrayList<>();
                list.add(item);
                return list;
            }
        }
        return new ArrayList<>();
    }

    private AttendanceV2Holiday buildHoliday(Wi wi, HolidayItem item, Set<String> dateSet) throws Exception {
        if (item == null) {
            throw new ExceptionWithMessage("节假日数据不能为空");
        }
        String dateString = item.dateValue();
        Date date = null;
        try {
            date = StringUtils.isBlank(dateString) ? null : DateTools.parseDate(dateString);
        } catch (Exception e) {
            date = null;
        }
        if (date == null) {
            throw new ExceptionWithMessage("日期不能为空，格式为 yyyy-MM-dd");
        }
        if (item.offDayValue() == null) {
            throw new ExceptionEmptyParameter("是否放假日");
        }
        dateString = DateTools.format(date, DateTools.format_yyyyMMdd);
        if (!dateSet.add(dateString)) {
            throw new ExceptionWithMessage("导入数据中日期重复");
        }

        Integer year = Integer.parseInt(DateTools.format(date, DateTools.format_yyyy));
        if (wi.getYear() != null && !wi.getYear().equals(year)) {
            throw new ExceptionWithMessage("日期年份与导入年份不一致");
        }

        AttendanceV2Holiday holiday = new AttendanceV2Holiday();
        holiday.setDateString(dateString);
        holiday.setYear(year);
        holiday.setName(item.getName());
        holiday.setOffDay(item.offDayValue());
        holiday.setSource(AttendanceV2Holiday.SOURCE_API);
        return holiday;
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 1676427194467963748L;

        @FieldDescribe("年份")
        private Integer year;

        @FieldDescribe("是否覆盖已存在日期的数据，默认 false")
        private Boolean overwrite;

        @FieldDescribe("节假日数据数组，兼容 date/isOffDay 或 dateString/offDay 字段")
        private List<HolidayItem> days;

        @FieldDescribe("节假日数据数组，兼容 date/isOffDay 或 dateString/offDay 字段")
        private List<HolidayItem> holidayList;

        @FieldDescribe("节假日数据数组，兼容 date/isOffDay 或 dateString/offDay 字段")
        private List<HolidayItem> holidays;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Boolean getOverwrite() {
            return overwrite;
        }

        public void setOverwrite(Boolean overwrite) {
            this.overwrite = overwrite;
        }

        public List<HolidayItem> getDays() {
            return days;
        }

        public void setDays(List<HolidayItem> days) {
            this.days = days;
        }

        public List<HolidayItem> getHolidayList() {
            return holidayList;
        }

        public void setHolidayList(List<HolidayItem> holidayList) {
            this.holidayList = holidayList;
        }

        public List<HolidayItem> getHolidays() {
            return holidays;
        }

        public void setHolidays(List<HolidayItem> holidays) {
            this.holidays = holidays;
        }
    }

    public static class HolidayItem extends GsonPropertyObject {

        private static final long serialVersionUID = -1165449910556373978L;

        private String name;
        private String date;
        private String dateString;
        private Boolean isOffDay;
        private Boolean offDay;

        String dateValue() {
            return StringUtils.defaultIfBlank(dateString, date);
        }

        Boolean offDayValue() {
            return offDay != null ? offDay : isOffDay;
        }

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

        public String getDateString() {
            return dateString;
        }

        public void setDateString(String dateString) {
            this.dateString = dateString;
        }

        public Boolean getIsOffDay() {
            return isOffDay;
        }

        public void setIsOffDay(Boolean isOffDay) {
            this.isOffDay = isOffDay;
        }

        public Boolean getOffDay() {
            return offDay;
        }

        public void setOffDay(Boolean offDay) {
            this.offDay = offDay;
        }
    }

    public static class ImportError extends GsonPropertyObject {

        private static final long serialVersionUID = 5517026127348862685L;

        @FieldDescribe("导入数据序号，从 1 开始")
        private Integer index;

        @FieldDescribe("日期")
        private String dateString;

        @FieldDescribe("错误信息")
        private String message;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getDateString() {
            return dateString;
        }

        public void setDateString(String dateString) {
            this.dateString = dateString;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 7548481497673133785L;

        @FieldDescribe("导入总数")
        private int total;

        @FieldDescribe("新增数量")
        private int inserted;

        @FieldDescribe("更新数量")
        private int updated;

        @FieldDescribe("跳过数量")
        private int skipped;

        @FieldDescribe("错误数量")
        private int errors;

        @FieldDescribe("错误明细")
        private List<ImportError> errorItems;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getInserted() {
            return inserted;
        }

        public void setInserted(int inserted) {
            this.inserted = inserted;
        }

        public int getUpdated() {
            return updated;
        }

        public void setUpdated(int updated) {
            this.updated = updated;
        }

        public int getSkipped() {
            return skipped;
        }

        public void setSkipped(int skipped) {
            this.skipped = skipped;
        }

        public int getErrors() {
            return errors;
        }

        public void setErrors(int errors) {
            this.errors = errors;
        }

        public List<ImportError> getErrorItems() {
            return errorItems;
        }

        public void setErrorItems(List<ImportError> errorItems) {
            this.errorItems = errorItems;
        }
    }
}
