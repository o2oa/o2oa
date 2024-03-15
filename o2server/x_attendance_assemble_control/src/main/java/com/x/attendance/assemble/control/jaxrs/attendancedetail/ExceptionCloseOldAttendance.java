package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCloseOldAttendance extends PromptException {

  private static final long serialVersionUID = 82779070886073828L;
  
  public ExceptionCloseOldAttendance() {
    super("旧版考勤已经关闭，请使用新考勤");
  }
}
