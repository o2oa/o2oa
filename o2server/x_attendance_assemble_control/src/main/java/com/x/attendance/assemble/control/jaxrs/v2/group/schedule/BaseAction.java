package com.x.attendance.assemble.control.jaxrs.v2.group.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;

abstract class BaseAction extends StandardJaxrsAction {

  private static final String DATE_PATTERN = "yyyy-MM";

  /**
   * 字符串是不是 月份格式yyyy-MM
   * @param dateString
   * @return
   */
  protected boolean isValidMonthString(String dateString) {
    // 正则表达式用于检查格式是否匹配
    Pattern pattern = Pattern.compile("\\d{4}-\\d{2}");
    Matcher matcher = pattern.matcher(dateString);
    if (!matcher.matches()) {
      return false; // 格式不匹配
    }
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
    sdf.setLenient(false);
    try {
      // 使用SimpleDateFormat尝试解析日期
      sdf.parse(dateString);
      return true; // 解析成功，日期格式正确
    } catch (ParseException e) {
      return false; // 解析失败，日期格式不正确
    }
  }
}
