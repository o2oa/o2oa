import {dom, exec} from "@o2oa/util";
import { lp } from "@o2oa/component";
import {EventName} from "./eventBus.js";

// 防抖
const debounce = (fn, delay = 300) => {
  let timer = null
  return (...args) => {
    if (timer) clearTimeout(timer)
    timer = window.setTimeout(() => {
      fn(...args)
    }, delay)
  }
}

/**
 * 是否是正确的 url
 * @param input
 * @returns {boolean}
 */
const isHttpUrl = (input) => {
  try {
    const u = new URL(input);
    return u.protocol === "http:" || u.protocol === "https:";
  } catch {
    return false;
  }
}

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
//
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

// 格式化日期为  YYYY-MM
const formatMonth = (date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  return `${year}-${month < 10 ? "0" + month : month}`;
}

const toDate = (str) => {
  const tmpArr = str.split(' ')
  if (!tmpArr[1]) tmpArr.push('0:0:0')
  const dateArr = tmpArr[0].split('-')
  const timeArr = tmpArr[1].split(':')
  return new Date(
      dateArr[0],
      parseInt(dateArr[1]) - 1,
      dateArr[2],
      timeArr[0],
      timeArr[1],
      timeArr[2]
  )
}

// 格式化日期为 YYYY-MM-DD 格式
const ymd = (date) => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const d = date.getDate()
  const monthStr = month > 9 ? `${month}` : `0${month}`
  const dStr = d > 9 ? `${d}` : `0${d}`
  return `${year}-${monthStr}-${dStr}`
}
// 格式化日期为 YYYY-MM-DD HH:mm:ss 格式
const ymdhms = (date) => {
  const hour = `${date.getHours() > 9 ? '' + date.getHours() : '0' + date.getHours()}`
  const m = `${date.getMinutes() > 9 ? '' + date.getMinutes() : '0' + date.getMinutes()}`
  const s = `${date.getSeconds() > 9 ? '' + date.getSeconds() : '0' + date.getSeconds()}`
  return `${ymd(date)} ${hour}:${m}:${s}`
}

// 会话列表显示时间
const friendlyTime = (date) => {
  let input
  if (typeof date === 'string') {
    if (date === '') {
      return ''
    }
    input = new Date(date)
  } else if (date instanceof Date) {
    input = date
  } else {
    return ''
  }
  const now = new Date()
  const timeDifference = now.getTime() - input.getTime()
  if (timeDifference < 0) {
    return '未来'
  }
  const second = timeDifference / 1000
  if (second < 60) {
    return '刚刚'
  }
  const minutes = second / 60
  if (minutes < 60) {
    return `${Math.floor(minutes)} 分钟前`
  }
  const hours = minutes / 60
  if (hours < 24) {
    return `${Math.floor(hours)} 小时前`
  }
  const days = hours / 24
  if (Math.floor(days) === 1) {
    const yesterday = new Date(new Date().setDate(new Date().getDate() - 1))
    if (input.getDate() === yesterday.getDate()) {
      return '昨天'
    } else {
      return '前天'
    }
  } else if (Math.floor(days) === 2) {
    const beforeYesterday = new Date(new Date().setDate(new Date().getDate() - 2))
    if (input.getDate() === beforeYesterday.getDate()) {
      return '前天'
    } else {
      return '大前天'
    }
  } else if (days < 31) {
    return `${Math.floor(days)} 天前`
  } else if (days >= 31 && days <= 2 * 31) {
    return '一个月前'
  } else if (days >= 2 * 31 && days <= 3 * 31) {
    return '2个月前'
  } else {
    return ymd(input)
  }
}


// 消息列表显示时间
const chatMsgShowTimeFormat = (date) => {
  let input
  if (typeof date === 'string') {
    if (date === '') {
      return ''
    }
    input = new Date(date)
  } else if (date instanceof Date) {
    input = date
  } else {
    return ''
  }
  const now = new Date()
  const hour = `${input.getHours() > 9 ? '' + input.getHours() : '0' + input.getHours()}`
  const m = `${input.getMinutes() > 9 ? '' + input.getMinutes() : '0' + input.getMinutes()}`
  const d = `${input.getDate() > 9 ? '' + input.getDate() : '0' + input.getDate()}`
  const month = `${input.getMonth() + 1 > 9 ? '' + (input.getMonth() + 1) : '0' + (input.getMonth() + 1)}`
  if (
      now.getFullYear() === input.getFullYear() &&
      now.getMonth() === input.getMonth() &&
      now.getDate() === input.getDate()
  ) {
    return `${hour}:${m}`
  } else {
    const timeDifference = now.getTime() - input.getTime()
    const days = timeDifference / 1000 / 60 / 60 / 24
    if (Math.floor(days) === 1) {
      const yesterday = new Date(new Date().setDate(new Date().getDate() - 1))
      if (input.getDate() === yesterday.getDate()) {
        return `昨天 ${hour}:${m}`
      } else {
        return `前天 ${hour}:${m}`
      }
    } else if (Math.floor(days) === 2) {
      const beforeYesterday = new Date(new Date().setDate(new Date().getDate() - 2))
      if (input.getDate() === beforeYesterday.getDate()) {
        return `前天 ${hour}:${m}`
      } else {
        return `大前天 ${hour}:${m}`
      }
    } else if (now.getFullYear() === input.getFullYear()) {
      return `${month}-${d} ${hour}:${m}`
    } else {
      return `${input.getFullYear()}-${month}-${d} ${hour}:${m}`
    }
  }
}

// 格式化分钟数为 xx小时xx分钟
const convertMinutesToHoursAndMinutes = (minutes) => {
  const hours = Math.floor(minutes / 60); // 取得小时数
  const remainingMinutes = Math.floor(minutes % 60); // 取得剩余的分钟数
  let result = "";

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

// /**
//  * loading 展示
//  * @param {*} component 组件本身
//  * @param newText loading 文字 可为空
//  */
// const showLoading = async (component, newText) => {
//   const loading = (await import(`../components/o-loading/index.js`)).default;
//   let bind = {}
//   if (newText) {
//     bind = { text: newText }
//   }
//   component.loadingVm = await loading.generate(document.body, {bind: bind}, component);
// }
// /**
//  * 关闭 loading
//  * 需要和 showLoading 一起使用
//  * @param {*} component 组件本身
//  */
// const hideLoading = async (component) => {
//   if (component.loadingVm) {
//     component.loadingVm.destroy();
//   }
// }

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
  return originalString.replace(new RegExp(searchString, 'g'), replacement);
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



let _contextMenuNode
let _closeContextMenuClickEvent
/**
 *
 * @param options { callback, menuList: [{text: "名称", id: "菜单 id"}], // 菜单列表， top, left, target : 绑定对象, bodyDom: 绑定对象所在的 body 元素 }
 */
const createContextMenu = (options) => {
  const callback = options.callback
  const menuList = options.menuList

  if (!menuList || !callback) {
    console.error('没有传入正确的参数 ！')
    return
  }
  closeContextMenu()
  let top = options.top
  let left = options.left
  if (!top || !left) {
    const target = options.target
    if  (target) {
      const rect = target.getBoundingClientRect()
      const width = rect.width;
      const height = rect.height;
      top = rect.top + height/2;    // 元素中点位置
      left = rect.left + width/2;  // 元素到页面左边的距离
    }
  }
  const bodyDom =  options.bodyDom ? options.bodyDom : document.body;
  // 创建菜单
  _contextMenuNode = dom('div.chat-menu-list', {
    style: 'position: fixed; z-index: 999; top: ' + top + 'px;  left: ' + left  + 'px;'
  })
  bodyDom.insertAdjacentElement('beforeend', _contextMenuNode)
  for (let i = 0; i < menuList.length; i++) {
    const menu = menuList[i]
    const liNode = dom('li', { text: menu.text })
    _contextMenuNode.insertAdjacentElement('beforeend', liNode)
    liNode.dataset.menu = JSON.stringify(menu) // 绑定数据
    liNode.addEventListener('click', (event) => {
      event.preventDefault()
      // 获取绑定的数据
      const menu = JSON.parse(event.target.dataset.menu)
      console.debug('=====> 点击了菜单', menu)
      callback(menu)
      closeContextMenu()
    })
  }
  // 绑定关闭事件
  _closeContextMenuClickEvent = (event) => {
    event.preventDefault()
    closeContextMenu()
    if (_closeContextMenuClickEvent) {
      bodyDom.removeEventListener('click', _closeContextMenuClickEvent)
    }
  }
  bodyDom.addEventListener('click', _closeContextMenuClickEvent)
}
const closeContextMenu = () => {
  if (_contextMenuNode) {
    _contextMenuNode.remove()
    _contextMenuNode = null
  }
}

/**
 * 会话选择器
 * @returns {Promise<unknown>}
 */
const conversationPicker = async (eventBus) => {
  return new Promise( (res, _) => {
    const name = 'im-main-forward'
    eventBus.subscribe(name, (data) => {
      console.debug("选择返回", data)
      res(data)
      eventBus.unsubscribe(name)
    })
    eventBus.publish(EventName.openChooseConversation, {eventName: name})
  })
}

const fileExtIcon = (ext) => {
  if (ext) {
    if (ext === "jpg" || ext === "jpeg") {
      return new URL( "../assets/file_icons/icon_file_jpeg.png", import.meta.url).href;
    } else if (ext === "gif") {
      return new URL( "../assets/file_icons/icon_file_gif.png", import.meta.url).href;
    } else if (ext === "png") {
      return new URL( "../assets/file_icons/icon_file_png.png", import.meta.url).href;
    } else if (ext === "tiff") {
      return new URL( "../assets/file_icons/icon_file_tiff.png", import.meta.url).href;
    } else if (ext === "bmp" || ext === "webp") {
      return new URL( "../assets/file_icons/icon_file_img.png", import.meta.url).href;
    } else if (ext === "ogg" || ext === "mp3" || ext === "wav" || ext === "wma") {
      return new URL( "../assets/file_icons/icon_file_mp3.png", import.meta.url).href;
    } else if (ext === "mp4") {
      return new URL( "../assets/file_icons/icon_file_mp4.png", import.meta.url).href;
    } else if (ext === "avi") {
      return new URL( "../assets/file_icons/icon_file_avi.png", import.meta.url).href;
    } else if (ext === "mov" || ext === "rm" || ext === "mkv") {
      return new URL( "../assets/file_icons/icon_file_rm.png", import.meta.url).href;
    } else if (ext === "doc" || ext === "docx") {
      return new URL( "../assets/file_icons/icon_file_word.png", import.meta.url).href;
    } else if (ext === "xls" || ext === "xlsx") {
      return new URL( "../assets/file_icons/icon_file_excel.png", import.meta.url).href;
    } else if (ext === "ppt" || ext === "pptx") {
      return new URL( "../assets/file_icons/icon_file_ppt.png", import.meta.url).href;
    } else if (ext === "html") {
      return new URL( "../assets/file_icons/icon_file_html.png", import.meta.url).href;
    } else if (ext === "pdf") {
      return new URL( "../assets/file_icons/icon_file_pdf.png", import.meta.url).href;
    } else if (ext === "txt" || ext === "json") {
      return new URL( "../assets/file_icons/icon_file_txt.png", import.meta.url).href;
    } else if (ext === "zip") {
      return new URL( "../assets/file_icons/icon_file_zip.png", import.meta.url).href;
    } else if (ext === "rar") {
      return new URL( "../assets/file_icons/icon_file_rar.png", import.meta.url).href;
    } else if (ext === "7z") {
      return new URL( "../assets/file_icons/icon_file_arch.png", import.meta.url).href;
    } else if (ext === "ai") {
      return new URL( "../assets/file_icons/icon_file_ai.png", import.meta.url).href;
    } else if (ext === "att") {
      return new URL( "../assets/file_icons/icon_file_att.png", import.meta.url).href;
    } else if (ext === "au") {
      return new URL( "../assets/file_icons/icon_file_au.png", import.meta.url).href;
    } else if (ext === "cad") {
      return new URL( "../assets/file_icons/icon_file_cad.png", import.meta.url).href;
    } else if (ext === "cdr") {
      return new URL( "../assets/file_icons/icon_file_cdr.png", import.meta.url).href;
    } else if (ext === "eps") {
      return new URL( "../assets/file_icons/icon_file_eps.png", import.meta.url).href;
    } else if (ext === "exe") {
      return new URL( "../assets/file_icons/icon_file_exe.png", import.meta.url).href;
    } else if (ext === "iso") {
      return new URL( "../assets/file_icons/icon_file_iso.png", import.meta.url).href;
    } else if (ext === "link") {
      return new URL( "../assets/file_icons/icon_file_link.png", import.meta.url).href;
    } else if (ext === "swf") {
      return new URL( "../assets/file_icons/icon_file_flash.png", import.meta.url).href;
    } else if (ext === "psd") {
      return new URL( "../assets/file_icons/icon_file_psd.png", import.meta.url).href;
    } else if (ext === "tmp") {
      return new URL( "../assets/file_icons/icon_file_tmp.png", import.meta.url).href;
    } else {
      return new URL( "../assets/file_icons/icon_file_unkown.png", import.meta.url).href;
    }
  } else {
    return new URL( "../assets/file_icons/icon_file_unkown.png", import.meta.url).href;
  }
}


// 检测浏览器是否支持WebP
const canUseWebP = () => {
  const elem = document.createElement('canvas');
  if (elem.getContext && elem.getContext('2d')) {
    return elem.toDataURL('image/webp').indexOf('data:image/webp') === 0;
  }
  return false
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
  toDate,
    ymd,
    ymdhms,
  friendlyTime,
  chatMsgShowTimeFormat,
  formatMonth,
  convertMinutesToHoursAndMinutes,
  convertTo2DArray,
  // showLoading,
  // hideLoading,
  storageSet,
  storageGet,
  replaceCustomString,
  generateExcelColumnNames,
  chooseSingleFile,
  createContextMenu,
  conversationPicker,
  fileExtIcon,
  canUseWebP,
  isHttpUrl,
  debounce
};
