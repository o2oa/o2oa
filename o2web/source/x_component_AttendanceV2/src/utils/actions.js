import {o2} from '@o2oa/component';


/**
 * 查询公共配置
 * @param {*} name 
 * @returns 
 */
function getPublicData(name){
  return new Promise((resolve)=>{
      o2.UD.getPublicData(name, dData=>resolve(dData));
  });
}

/**
 * 调用后端api
 * @param {*} content 
 * @param {*} action 
 * @param {*} method 
 * @param  {...any} args 
 * @returns 
 */
async function doAction(content, action, method, ...args) {
  debugger;
  const m = o2.Actions.load(content)[action][method];
  const json = await m.apply(m, ...args);
  return json.data;
}
/**
 * 考勤班次API
 * @param {*} method 
 * @param  {...any} args 
 * @returns 
 */
function attendanceShiftAction(method, ...args) {
  return doAction('x_attendance_assemble_control', 'ShiftAction', method, args);
}
/**
 * 考勤工作地址API
 * @param {*} method 
 * @param  {...any} args 
 * @returns 
 */
function attendanceWorkPlaceV2Action(method, ...args) {
  debugger;
  return doAction('x_attendance_assemble_control', 'WorkPlaceV2Action', method, args);
}

export { getPublicData, attendanceShiftAction, attendanceWorkPlaceV2Action }