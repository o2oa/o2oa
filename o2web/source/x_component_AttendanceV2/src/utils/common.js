import {exec} from "@o2oa/util";

/**
 * 换取多语言文字
 * @param {*} lp 语言包
 * @param {*} key 文字的key值，多层用.隔开，如 form.name
 * @param {*} options 变量替换对象，如{name: 'abc'}, 会把 form.name里面的 {name} 替换成 abc
 * @returns 
 */
function lpT(lp, key, options){
    let v = (exec(`return this.${key}`, lp) || key).toString();
    if (options){
        Object.keys(options).forEach((n)=>{
            const reg = new RegExp(`\{${n}\}`, 'g');
            v = v.replace(reg, options[n]);
        })
    }
    return v;
}

/**
 * 给对象设置值
 * @param {*} key 多层用.分割 
 * @param {*} v  存储的值
 * @param {*} data 存储对象
 * @returns 
 */
function setJSONValue(key, v, data) {
  var idList = key.split(".");
  idList = idList.map( function(d){ return d.test(/^\d+$/) ? d.toInt() : d; });
  var lastIndex = idList.length - 1;
  for(var i=0; i<=lastIndex; i++){
      var id = idList[i];
      if( !id && id !== 0 )return;
      if( i === lastIndex ){
          data[id] = v;
      }else{
          var nexId = idList[i+1];
          if(o2.typeOf(nexId) === "number"){ //下一个ID是数字
              if( !data[id] && o2.typeOf(data[id]) !== "array" ){
                  data[id] = [];
              }
              if( nexId > data[id].length ){ //超过了最大下标，丢弃
                  return;
              }
          }else{ //下一个ID是字符串
              if( !data[id] || o2.typeOf(data[id]) !== "object"){
                  data[id] = {};
              }
          }
          data = data[id];
      }
  }
}

export {
  setJSONValue,
  lpT
}