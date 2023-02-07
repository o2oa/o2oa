
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
  setJSONValue
}