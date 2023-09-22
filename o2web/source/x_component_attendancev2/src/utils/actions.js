import { o2 } from "@o2oa/component";

/**
 * 查询公共配置
 * @param {*} name
 * @returns
 */
function getPublicData(name) {
  return new Promise((resolve) => {
    o2.UD.getPublicData(name, (dData) => resolve(dData));
  });
}
/**
 * 设置公共配置
 * @param {*} name
 * @param {*} value
 * @returns
 */
function putPublicData(name, value) {
  return new Promise((resolve) => {
    o2.UD.putPublicData(name, value, (dData) => resolve(dData));
  });
}

/**
 * 调用后端api 只返回data
 * @param {*} content
 * @param {*} action
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
async function doAction(content, action, method, ...args) {
  const m = o2.Actions.load(content)[action][method];
  const json = await m.apply(m, ...args);
  return json.data;
}
/**
 * 调用后端api 返回整个response对象
 * @param {*} content
 * @param {*} action
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
async function doActionBackResult(content, action, method, ...args) {
  const m = o2.Actions.load(content)[action][method];
  return await m.apply(m, ...args);
}
/**
 * 考勤班次API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function attendanceShiftAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "ShiftAction", method, args);
}
/**
 * 考勤班次分页查询
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function shiftActionListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "ShiftAction",
    "listByPaging",
    args
  );
}
/**
 * 考勤工作地址API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function attendanceWorkPlaceV2Action(method, ...args) {
  return doAction(
    "x_attendance_assemble_control",
    "WorkPlaceV2Action",
    method,
    args
  );
}

/**
 * 考勤组分页查询
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function groupActionListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "GroupAction",
    "listByPaging",
    args
  );
}
/**
 * 考勤组API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function groupAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "GroupAction", method, args);
}

/**
 * 排班API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function  groupScheduleAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "GroupScheduleAction", method, args);
}


/**
 * 考勤详细分页查询
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function detailActionListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "DetailAction",
    "listByPaging",
    args
  );
}

function detailAction(method, ...args) {
  return doAction(
    "x_attendance_assemble_control",
    "DetailAction",
    method,
    args
  );
}


/**
 * 请假数据详细分页查询
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function leaveActionListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "LeaveAction",
    "listByPaging",
    args
  );
}

function leaveAction(method, ...args) {
  return doAction(
    "x_attendance_assemble_control",
    "LeaveAction",
    method,
    args
  );
}


/**
 * 考勤配置API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function configAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "ConfigAction", method, args);
}



/**
 * 申诉分页查询
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function appealInfoActionListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "AppealInfoAction",
    "listByPaging",
    args
  );
}
/**
 * 申诉分页查询
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function appealInfoActionManagerListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "AppealInfoAction",
    "managerListByPaging",
    args
  );
}

/**
 * 原始记录分页查询
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function recordActionListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "RecordAction",
    "listByPaging",
    args
  );
}


/**
 * 申诉API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function appealInfoAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "AppealInfoAction", method, args);
}
/**
 * 流程API
 * @param {*} method 
 * @param  {...any} args 
 * @returns 
 */
function processAction(method, ...args) {
  return doAction("x_processplatform_assemble_surface", "ProcessAction", method, args);
}
/**
 * Job API
 * @param {*} method 
 * @param  {...any} args 
 * @returns 
 */
function jobAction(method, ...args) {
  return doAction("x_processplatform_assemble_surface", "JobAction", method, args);
}


/**
 * 我的考勤API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function myAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "MyAction", method, args);
}
/**
 * 打卡API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function mobileAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "MobileAction", method, args);
}

/**
 * 组织管理 人员 API
 * x_organization_assemble_control
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function personAction(method, ...args) {
  return doAction("x_organization_assemble_control", "PersonAction", method, args);
}

/**
 * 个人 API
 * x_organization_assemble_personal
 * @param {*} method 
 * @param  {...any} args 
 * @returns 
 */
function personalAction(method, ...args) {
  return doAction("x_organization_assemble_personal", "PersonAction", method, args);
}


export {
  getPublicData,
  putPublicData,
  attendanceShiftAction,
  shiftActionListByPaging,
  attendanceWorkPlaceV2Action,
  groupActionListByPaging,
  groupAction,
  detailActionListByPaging,
  detailAction,
  configAction,
  appealInfoActionListByPaging,
  appealInfoActionManagerListByPaging,
  appealInfoAction,
  processAction,
  jobAction,
  myAction,
  mobileAction,
  personAction,
  personalAction,
  leaveActionListByPaging,
  leaveAction,
  recordActionListByPaging,
  groupScheduleAction,
};
