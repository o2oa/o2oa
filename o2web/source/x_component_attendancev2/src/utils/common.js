import { exec } from "@o2oa/util";
import { lp } from "@o2oa/component";

/**
 * 是否为空
 * 数字0也是不为空
 * @param {*} data
 * @returns
 */
function isEmpty(data) {
  if (data === 0) {
    return false;
  }
  if (!data) {
    return true;
  }
  if (typeof data == "string") {
    return data === "" || data.replace(/(^s*)|(s*$)/g, "").length == 0;
  } else {
    return false;
  }
}

/**
 * 是否整数 包含正负
 * @param {*} inputData
 * @returns
 */
function isInt(inputData) {
  if (isPositiveInt(inputData) || isNegtiveInt(inputData)) {
    return true;
  } else {
    return false;
  }
}
/**
 * 是否负整数 包含0
 * @param {*} inputData
 * @returns
 */
function isNegtiveInt(inputData) {
  const regNeg = /^\-[1-9][0-9]*$/; // 负整数
  if (regNeg.test(inputData)) {
    return true;
  } else {
    return false;
  }
}
/**
 * 是否正整数
 * @param {*} inputData
 * @returns
 */
function isPositiveInt(inputData) {
  const regPos = /^\d+$/; // 正整数
  if (regPos.test(inputData)) {
    return true;
  } else {
    return false;
  }
}

/**
 * 换取多语言文字
 * @param {*} lp 语言包
 * @param {*} key 文字的key值，多层用.隔开，如 form.name
 * @param {*} options 变量替换对象，如{name: 'abc'}, 会把 form.name里面的 {name} 替换成 abc
 * @returns
 */
function lpFormat(lp, key, options) {
  let v = (exec(`return this.${key}`, lp) || key).toString();
  if (options) {
    Object.keys(options).forEach((n) => {
      const reg = new RegExp(`\{${n}\}`, "g");
      v = v.replace(reg, options[n]);
    });
  }
  return v;
}

/**
 * 给对象设置值，也可以是数组
 * 比如 key='a.b' v=1 就会给data对象的a属性下的b属性设置值为1 或者 key='0.b' v=1 会给data数组的第一个对象中的b属性设置值为1
 * @param {*} key 多层用.分割
 * @param {*} v  存储的值
 * @param {*} data 存储对象
 * @returns
 */
function setJSONValue(key, v, data) {
  var idList = key.split(".");
  idList = idList.map(function (d) {
    return d.test(/^\d+$/) ? d.toInt() : d;
  });
  var lastIndex = idList.length - 1;
  for (var i = 0; i <= lastIndex; i++) {
    var id = idList[i];
    if (!id && id !== 0) return;
    if (i === lastIndex) {
      data[id] = v;
    } else {
      var nexId = idList[i + 1];
      if (o2.typeOf(nexId) === "number") {
        //下一个ID是数字
        if (!data[id] && o2.typeOf(data[id]) !== "array") {
          data[id] = [];
        }
        if (nexId > data[id].length) {
          //超过了最大下标，丢弃
          return;
        }
      } else {
        //下一个ID是字符串
        if (!data[id] || o2.typeOf(data[id]) !== "object") {
          data[id] = {};
        }
      }
      data = data[id];
    }
  }
}

// 格式化日期为 YYYY-MM-DD 格式
const formatDate = (date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  return `${year}-${month < 10 ? "0" + month : month}-${
    day < 10 ? "0" + day : day
  }`;
};
// 格式化日期为  YYYY-MM
const formatMonth = (date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  return `${year}-${month < 10 ? "0" + month : month}`;
}

// 格式化分钟数为 xx小时xx分钟
const convertMinutesToHoursAndMinutes = (minutes) => {
  var hours = Math.floor(minutes / 60); // 取得小时数
  var remainingMinutes = Math.floor(minutes % 60); // 取得剩余的分钟数
  var result = "";

  if (hours > 0) {
    result += hours + " " + lp.hour;
  }

  if (remainingMinutes > 0 || result === "") {
    result += remainingMinutes + " " + lp.minute;
  }

  return result;
};

/**
 * 数组转化为二维数组
 * @param {*} arr 一维数组
 * @param {*} subSize 子数组长度
 * @returns 
 */
const  convertTo2DArray = (arr, subSize) => {
  const result = [];
  for (let i = 0; i < arr.length; i += subSize) {
    result.push(arr.slice(i, i + subSize));
  }
  return result;
}

/**
 * loading 展示
 * @param {*} component 组件本身
 */
const showLoading = async (component, newText) => {
  const loading = (await import(`../components/o-loading/index.js`)).default;
  let bind = {}
  if (newText) {
    bind = { text: newText }
  }
  component.loadingVm = await loading.generate(document.body, {bind: bind}, component);
}
/**
 * 关闭 loading 
 * 需要和 showLoading 一起使用
 * @param {*} component 组件本身
 */
const hideLoading = async (component) => {
  if (component.loadingVm) {
    component.loadingVm.destroy();
  }
}

/**
 * 
 * @param {*} person 
 * @returns 
 */
const formatPersonName = (person) => {
  if (person && person.indexOf("@") > -1) {
    return person.split("@")[0];
  }
  return person;
}
/**
 * 获取传入日期所在的月份所有日期
 * @param {Date} inputDate 
 * @returns []
 */
const getAllDatesInMonth = (inputDate) => {
  const result = [];
  const currentDate = new Date(inputDate);
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();
  // 确定月份的第一天
  const firstDayOfMonth = new Date(year, month, 1);
  // 从第一天开始，递增日期直到月份变化
  let currentDateInMonth = firstDayOfMonth;
  while (currentDateInMonth.getMonth() === month) {
    result.push(new Date(currentDateInMonth));
    currentDateInMonth.setDate(currentDateInMonth.getDate() + 1);
  }
  return result;
}
/**
 * localStorage 存储
 * @param {*} key 
 * @param {*} item 
 */
const storageSet = (key, item) => {
  localStorage.setItem(key, JSON.stringify(item));
}
/**
 * localStorage 获取存储数据
 * @param {*} key 
 * @returns 
 */
const storageGet = (key) => {
  const item = localStorage.getItem(key);
  // 使用JSON.parse将字符串还原为JavaScript对象
  return JSON.parse(item);
}

/**
 * 替换字符串特定内容
 * @param {*} originalString 原字符串
 * @param {*} searchString  被替换的内容
 * @param {*} replacement 替换成的内容
 * @returns 
 */
const replaceCustomString = (originalString, searchString, replacement) => {
  var replacedString = originalString.replace(new RegExp(searchString, 'g'), replacement);
  return replacedString;
}

/**
 * 根据传入的数字个数，生成Excel列名数组，如：A,B,C,....,AA,AB,AC,....
 * @param len 数量
 * @returns {*[]}
 */
const generateExcelColumnNames = (len) =>{
  const columnNames = [];
  for (let i = 1; i <= len; i++) {
    let columnName = "";
    let quotient = i;
    while (quotient > 0) {
      const remainder = (quotient - 1) % 26;
      columnName = String.fromCharCode(65 + remainder) + columnName;
      quotient = Math.floor((quotient - 1) / 26);
    }
    columnNames.push(columnName);
  }
  return columnNames;
}

/**
 * 选择单个文件功能，并触发回调函数
 * @param callback 回调函数，返回选中的文件
 */
const chooseSingleFile = (callback) => {
  const input = document.createElement('input');
  input.type = 'file';
  input.style.display = 'none';
  // 添加 change 事件监听器，接收上传的文件
  input.addEventListener('change', (event) => {
    const files = event.target.files;
    // 处理上传的文件
    if (files && files.length > 0) {
      const file = files[0]
      if (callback) {
        callback(file)
      }
    }
  });
  input.click();
}

export {
  getAllDatesInMonth,
  formatPersonName,
  setJSONValue,
  lpFormat,
  isInt,
  isNegtiveInt,
  isPositiveInt,
  isEmpty,
  formatDate,
  formatMonth,
  convertMinutesToHoursAndMinutes,
  convertTo2DArray,
  showLoading,
  hideLoading,
  storageSet,
  storageGet,
  replaceCustomString,
  generateExcelColumnNames,
  chooseSingleFile
};
