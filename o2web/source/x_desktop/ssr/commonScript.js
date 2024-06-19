//mootools
var GLOBAL_ITEMS=function(){var t=[];for(var e in this)t.push(e);return t}();!function(){this.MooTools={version:"1.5.0dev",build:"%build%"};var t=this.typeOf=function(t){if(null==t)return"null";if(null!=t.$family)return t.$family();if(t.nodeName){if(1==t.nodeType)return"element";if(3==t.nodeType)return/\S/.test(t.nodeValue)?"textnode":"whitespace"}else if("number"==typeof t.length){if(t.callee)return"arguments";if("item"in t)return"collection"}return typeof t},e=(this.instanceOf=function(t,e){if(null==t)return!1;for(var n=t.$constructor||t.constructor;n;){if(n===e)return!0;n=n.parent}return!!t.hasOwnProperty&&t instanceof e},this.Function),n=!0;for(var r in{toString:1})n=null;n&&(n=["hasOwnProperty","valueOf","isPrototypeOf","propertyIsEnumerable","toLocaleString","toString","constructor"]),e.prototype.overloadSetter=function(t){var e=this;return function(r,i){if(null==r)return this;if(t||"string"!=typeof r){for(var a in r)e.call(this,a,r[a]);if(n)for(var o=n.length;o--;)a=n[o],r.hasOwnProperty(a)&&e.call(this,a,r[a])}else e.call(this,r,i);return this}},e.prototype.overloadGetter=function(t){var e=this;return function(n){var r,i;if("string"!=typeof n?r=n:arguments.length>1?r=arguments:t&&(r=[n]),r){i={};for(var a=0;a<r.length;a++)i[r[a]]=e.call(this,r[a])}else i=e.call(this,n);return i}},e.prototype.extend=function(t,e){this[t]=e}.overloadSetter(),e.prototype.implement=function(t,e){this.prototype[t]=e}.overloadSetter();var i=Array.prototype.slice;e.from=function(e){return"function"==t(e)?e:function(){return e}},e.convert=e.from,Array.from=function(e){return null==e?[]:a.isEnumerable(e)&&"string"!=typeof e?"array"==t(e)?e:i.call(e):[e]},Array.convert=Array.from,Number.from=function(t){var e=parseFloat(t);return isFinite(e)?e:null},Number.convert=Number.from,String.from=function(t){return t+""},String.convert=String.from,e.implement({hide:function(){return this.$hidden=!0,this},protect:function(){return this.$protected=!0,this}});var a=this.Type=function(e,n){if(e){var r=e.toLowerCase();a["is"+e]=function(e){return t(e)==r},null!=n&&(n.prototype.$family=function(){return r}.hide())}return null==n?null:(n.extend(this),n.$constructor=a,n.prototype.$constructor=n,n)},o=Object.prototype.toString;a.isEnumerable=function(t){return null!=t&&"number"==typeof t.length&&"[object Function]"!=o.call(t)};var s={},u=function(e){var n=t(e.prototype);return s[n]||(s[n]=[])},c=function(e,n){if(!n||!n.$hidden){for(var r=u(this),a=0;a<r.length;a++){var o=r[a];"type"==t(o)?c.call(o,e,n):o.call(this,e,n)}var s=this.prototype[e];null!=s&&s.$protected||(this.prototype[e]=n),null==this[e]&&"function"==t(n)&&l.call(this,e,(function(t){return n.apply(t,i.call(arguments,1))}))}},l=function(t,e){if(!e||!e.$hidden){var n=this[t];null!=n&&n.$protected||(this[t]=e)}};a.implement({implement:c.overloadSetter(),extend:l.overloadSetter(),alias:function(t,e){c.call(this,t,this.prototype[e])}.overloadSetter(),mirror:function(t){return u(this).push(t),this}}),new a("Type",a);var h=function(t,e,n){var r=e!=Object,i=e.prototype;r&&(e=new a(t,e));for(var o=0,s=n.length;o<s;o++){var u=n[o],c=e[u],l=i[u];c&&c.protect(),r&&l&&e.implement(u,l.protect())}if(r){var f=i.propertyIsEnumerable(n[0]);e.forEachMethod=function(t){if(!f)for(var e=0,r=n.length;e<r;e++)t.call(i,i[n[e]],n[e]);for(var a in i)t.call(i,i[a],a)}}return h};h("String",String,["charAt","charCodeAt","concat","indexOf","lastIndexOf","match","quote","replace","search","slice","split","substr","substring","trim","toLowerCase","toUpperCase"])("Array",Array,["pop","push","reverse","shift","sort","splice","unshift","concat","join","slice","indexOf","lastIndexOf","filter","forEach","every","map","some","reduce","reduceRight"])("Number",Number,["toExponential","toFixed","toLocaleString","toPrecision"])("Function",e,["apply","call","bind"])("RegExp",RegExp,["exec","test"])("Object",Object,["create","defineProperty","defineProperties","keys","getPrototypeOf","getOwnPropertyDescriptor","getOwnPropertyNames","preventExtensions","isExtensible","seal","isSealed","freeze","isFrozen"])("Date",Date,["now"]),Object.extend=l.overloadSetter(),Date.extend("now",(function(){return+new Date})),new a("Boolean",Boolean),Number.prototype.$family=function(){return isFinite(this)?"number":"null"}.hide(),Number.extend("random",(function(t,e){return Math.floor(Math.random()*(e-t+1)+t)}));var f=Object.prototype.hasOwnProperty;Object.extend("forEach",(function(t,e,n){for(var r in t)f.call(t,r)&&e.call(n,t[r],r,t)})),Object.each=Object.forEach,Array.implement({forEach:function(t,e){for(var n=0,r=this.length;n<r;n++)n in this&&t.call(e,this[n],n,this)},each:function(t,e){return Array.forEach(this,t,e),this}});var d=function(e){switch(t(e)){case"array":return e.clone();case"object":return Object.clone(e);default:return e}};Array.implement("clone",(function(){for(var t=this.length,e=new Array(t);t--;)e[t]=d(this[t]);return e}));var g=function(e,n,r){switch(t(r)){case"object":"object"==t(e[n])?Object.merge(e[n],r):e[n]=Object.clone(r);break;case"array":e[n]=r.clone();break;default:e[n]=r}return e};Object.extend({merge:function(e,n,r){if("string"==t(n))return g(e,n,r);for(var i=1,a=arguments.length;i<a;i++){var o=arguments[i];for(var s in o)g(e,s,o[s])}return e},clone:function(t){var e={};for(var n in t)e[n]=d(t[n]);return e},append:function(t){for(var e=1,n=arguments.length;e<n;e++){var r=arguments[e]||{};for(var i in r)t[i]=r[i]}return t}}),["Object","WhiteSpace","TextNode","Collection","Arguments"].each((function(t){new a(t)}));var p=Date.now();String.extend("uniqueID",(function(){return(p++).toString(36)}))}(),Array.implement({every:function(t,e){for(var n=0,r=this.length>>>0;n<r;n++)if(n in this&&!t.call(e,this[n],n,this))return!1;return!0},filter:function(t,e){for(var n,r=[],i=0,a=this.length>>>0;i<a;i++)i in this&&(n=this[i],t.call(e,n,i,this)&&r.push(n));return r},indexOf:function(t,e){for(var n=this.length>>>0,r=e<0?Math.max(0,n+e):e||0;r<n;r++)if(this[r]===t)return r;return-1},map:function(t,e){for(var n=this.length>>>0,r=Array(n),i=0;i<n;i++)i in this&&(r[i]=t.call(e,this[i],i,this));return r},some:function(t,e){for(var n=0,r=this.length>>>0;n<r;n++)if(n in this&&t.call(e,this[n],n,this))return!0;return!1},clean:function(){return this.filter((function(t){return null!=t}))},invoke:function(t){var e=Array.slice(arguments,1);return this.map((function(n){return n[t].apply(n,e)}))},associate:function(t){for(var e={},n=Math.min(this.length,t.length),r=0;r<n;r++)e[t[r]]=this[r];return e},link:function(t){for(var e={},n=0,r=this.length;n<r;n++)for(var i in t)if(t[i](this[n])){e[i]=this[n],delete t[i];break}return e},contains:function(t,e){return-1!=this.indexOf(t,e)},append:function(t){return this.push.apply(this,t),this},getLast:function(){return this.length?this[this.length-1]:null},getRandom:function(){return this.length?this[Number.random(0,this.length-1)]:null},include:function(t){return this.contains(t)||this.push(t),this},combine:function(t){for(var e=0,n=t.length;e<n;e++)this.include(t[e]);return this},erase:function(t){for(var e=this.length;e--;)this[e]===t&&this.splice(e,1);return this},empty:function(){return this.length=0,this},flatten:function(){for(var t=[],e=0,n=this.length;e<n;e++){var r=typeOf(this[e]);"null"!=r&&(t=t.concat("array"==r||"collection"==r||"arguments"==r||instanceOf(this[e],Array)?Array.flatten(this[e]):this[e]))}return t},pick:function(){for(var t=0,e=this.length;t<e;t++)if(null!=this[t])return this[t];return null},hexToRgb:function(t){if(3!=this.length)return null;var e=this.map((function(t){return 1==t.length&&(t+=t),t.toInt(16)}));return t?e:"rgb("+e+")"},rgbToHex:function(t){if(this.length<3)return null;if(4==this.length&&0==this[3]&&!t)return"transparent";for(var e=[],n=0;n<3;n++){var r=(this[n]-0).toString(16);e.push(1==r.length?"0"+r:r)}return t?e:"#"+e.join("")}}),String.implement({test:function(t,e){return("regexp"==typeOf(t)?t:new RegExp(""+t,e)).test(this)},contains:function(t,e){return e?(e+this+e).indexOf(e+t+e)>-1:String(this).indexOf(t)>-1},trim:function(){return String(this).replace(/^\s+|\s+$/g,"")},clean:function(){return String(this).replace(/\s+/g," ").trim()},camelCase:function(){return String(this).replace(/-\D/g,(function(t){return t.charAt(1).toUpperCase()}))},hyphenate:function(){return String(this).replace(/[A-Z]/g,(function(t){return"-"+t.charAt(0).toLowerCase()}))},capitalize:function(){return String(this).replace(/\b[a-z]/g,(function(t){return t.toUpperCase()}))},escapeRegExp:function(){return String(this).replace(/([-.*+?^${}()|[\]\/\\])/g,"\\$1")},toInt:function(t){return parseInt(this,t||10)},toFloat:function(){return parseFloat(this)},hexToRgb:function(t){var e=String(this).match(/^#?(\w{1,2})(\w{1,2})(\w{1,2})$/);return e?e.slice(1).hexToRgb(t):null},rgbToHex:function(t){var e=String(this).match(/\d{1,3}/g);return e?e.rgbToHex(t):null},substitute:function(t,e){return String(this).replace(e||/\\?\{([^{}]+)\}/g,(function(e,n){return"\\"==e.charAt(0)?e.slice(1):null!=t[n]?t[n]:""}))}}),Function.extend({attempt:function(){for(var t=0,e=arguments.length;t<e;t++)try{return arguments[t]()}catch(t){}return null}}),Function.implement({attempt:function(t,e){try{return this.apply(e,Array.from(t))}catch(t){}return null},bind:function(t){var e=this,n=arguments.length>1?Array.slice(arguments,1):null,r=function(){},i=function(){var a=t,o=arguments.length;this instanceof i&&(r.prototype=e.prototype,a=new r);var s=n||o?e.apply(a,n&&o?n.concat(Array.slice(arguments)):n||arguments):e.call(a);return a==t?s:a};return i},pass:function(t,e){var n=this;return null!=t&&(t=Array.from(t)),function(){return n.apply(e,t||arguments)}},delay:function(t,e,n){return setTimeout(this.pass(null==n?[]:n,e),t)},periodical:function(t,e,n){return setInterval(this.pass(null==n?[]:n,e),t)}}),Number.implement({limit:function(t,e){return Math.min(e,Math.max(t,this))},round:function(t){return t=Math.pow(10,t||0).toFixed(t<0?-t:0),Math.round(this*t)/t},times:function(t,e){for(var n=0;n<this;n++)t.call(e,n,this)},toFloat:function(){return parseFloat(this)},toInt:function(t){return parseInt(this,t||10)}}),Number.alias("each","times"),function(t){var e={};["abs","acos","asin","atan","atan2","ceil","cos","exp","floor","log","max","min","pow","sin","sqrt","tan"].each((function(t){Number[t]||(e[t]=function(){return Math[t].apply(null,[this].concat(Array.from(arguments)))})})),Number.implement(e)}(),function(){var t=this.Class=new Type("Class",(function(r){instanceOf(r,Function)&&(r={initialize:r});var i=function(){if(n(this),i.$prototyping)return this;this.$caller=null;var t=this.initialize?this.initialize.apply(this,arguments):this;return this.$caller=this.caller=null,t}.extend(this).implement(r);return i.$constructor=t,i.prototype.$constructor=i,i.prototype.parent=e,i})),e=function(){if(!this.$caller)throw new Error('The method "parent" cannot be called.');var t=this.$caller.$name,e=this.$caller.$owner.parent,n=e?e.prototype[t]:null;if(!n)throw new Error('The method "'+t+'" has no parent.');return n.apply(this,arguments)},n=function(t){for(var e in t){var r=t[e];switch(typeOf(r)){case"object":var i=function(){};i.prototype=r,t[e]=n(new i);break;case"array":t[e]=r.clone()}}return t},r=function(e,n,r){if(t.Mutators.hasOwnProperty(e)&&null==(n=t.Mutators[e].call(this,n)))return this;if("function"==typeOf(n)){if(n.$hidden)return this;this.prototype[e]=r?n:function(t,e,n){n.$origin&&(n=n.$origin);var r=function(){if(n.$protected&&null==this.$caller)throw new Error('The method "'+e+'" cannot be called.');var t=this.caller,i=this.$caller;this.caller=i,this.$caller=r;var a=n.apply(this,arguments);return this.$caller=i,this.caller=t,a}.extend({$owner:t,$origin:n,$name:e});return r}(this,e,n)}else Object.merge(this.prototype,e,n);return this};t.implement("implement",r.overloadSetter()),t.Mutators={Extends:function(t){this.parent=t,this.prototype=function(t){t.$prototyping=!0;var e=new t;return delete t.$prototyping,e}(t)},Implements:function(t){Array.from(t).each((function(t){var e=new t;for(var n in e)r.call(this,n,e[n],!0)}),this)}}}(),function(){this.Chain=new Class({$chain:[],chain:function(){return this.$chain.append(Array.flatten(arguments)),this},callChain:function(){return!!this.$chain.length&&this.$chain.shift().apply(this,arguments)},clearChain:function(){return this.$chain.empty(),this}});var t=function(t){return t.replace(/^on([A-Z])/,(function(t,e){return e.toLowerCase()}))};this.Events=new Class({$events:{},addEvent:function(e,n,r){return e=t(e),this.$events[e]=(this.$events[e]||[]).include(n),r&&(n.internal=!0),this},addEvents:function(t){for(var e in t)this.addEvent(e,t[e]);return this},fireEvent:function(e,n,r){e=t(e);var i=this.$events[e];return i?(n=Array.from(n),i.each((function(t){r?t.delay(r,this,n):t.apply(this,n)}),this),this):this},removeEvent:function(e,n){e=t(e);var r=this.$events[e];if(r&&!n.internal){var i=r.indexOf(n);-1!=i&&delete r[i]}return this},removeEvents:function(e){var n;if("object"==typeOf(e)){for(n in e)this.removeEvent(n,e[n]);return this}for(n in e&&(e=t(e)),this.$events)if(!e||e==n)for(var r=this.$events[n],i=r.length;i--;)i in r&&this.removeEvent(n,r[i]);return this}}),this.Options=new Class({setOptions:function(){var t=this.options=Object.merge.apply(null,[{},this.options].append(arguments));if(this.addEvent)for(var e in t)"function"==typeOf(t[e])&&/^on[A-Z]/.test(e)&&(this.addEvent(e,t[e]),delete t[e]);return this}})}(),function(){var t=Object.prototype.hasOwnProperty;Object.extend({subset:function(t,e){for(var n={},r=0,i=e.length;r<i;r++){var a=e[r];a in t&&(n[a]=t[a])}return n},map:function(e,n,r){var i={};for(var a in e)t.call(e,a)&&(i[a]=n.call(r,e[a],a,e));return i},filter:function(e,n,r){var i={};for(var a in e){var o=e[a];t.call(e,a)&&n.call(r,o,a,e)&&(i[a]=o)}return i},every:function(e,n,r){for(var i in e)if(t.call(e,i)&&!n.call(r,e[i],i))return!1;return!0},some:function(e,n,r){for(var i in e)if(t.call(e,i)&&n.call(r,e[i],i))return!0;return!1},keys:function(e){var n=[];for(var r in e)t.call(e,r)&&n.push(r);return n},values:function(e){var n=[];for(var r in e)t.call(e,r)&&n.push(e[r]);return n},getLength:function(t){return Object.keys(t).length},keyOf:function(e,n){for(var r in e)if(t.call(e,r)&&e[r]===n)return r;return null},contains:function(t,e){return null!=Object.keyOf(t,e)},toQueryString:function(t,e){var n=[];return Object.each(t,(function(t,r){var i;switch(e&&(r=e+"["+r+"]"),typeOf(t)){case"object":i=Object.toQueryString(t,r);break;case"array":var a={};t.each((function(t,e){a[e]=t})),i=Object.toQueryString(a,r);break;default:i=r+"="+encodeURIComponent(t)}null!=t&&n.push(i)})),n.join("&")}})}(),"undefined"!=typeof exports&&function(){for(var t in this)GLOBAL_ITEMS.contains(t)||(exports[t]=this[t]);exports.apply=function(t){Object.append(t,exports)}}(),MooTools.More={version:"1.6.1-dev",build:"%build%"},function(){var t=function(t){return null!=t},e=Object.prototype.hasOwnProperty;Object.extend({getFromPath:function(t,n){"string"==typeof n&&(n=n.split("."));for(var r=0,i=n.length;r<i;r++){if(!e.call(t,n[r]))return null;t=t[n[r]]}return t},cleanValues:function(e,n){for(var r in n=n||t,e)n(e[r])||delete e[r];return e},erase:function(t,n){return e.call(t,n)&&delete t[n],t},run:function(t){var e=Array.slice(arguments,1);for(var n in t)t[n].apply&&t[n].apply(t,e);return t}})}(),function(){var t=null,e={},n=function(t){return instanceOf(t,r.Set)?t:e[t]},r=this.Locale={define:function(n,i,a,o){var s;return instanceOf(n,r.Set)?(s=n.name)&&(e[s]=n):(e[s=n]||(e[s]=new r.Set(s)),n=e[s]),i&&n.define(i,a,o),t||(t=n),n},use:function(e){return(e=n(e))&&(t=e,this.fireEvent("change",e)),this},getCurrent:function(){return t},get:function(e,n){return t?t.get(e,n):""},inherit:function(t,e,r){return(t=n(t))&&t.inherit(e,r),this},list:function(){return Object.keys(e)}};Object.append(r,new Events),r.Set=new Class({sets:{},inherits:{locales:[],sets:{}},initialize:function(t){this.name=t||""},define:function(t,e,n){var r=this.sets[t];return r||(r={}),e&&("object"==typeOf(e)?r=Object.merge(r,e):r[e]=n),this.sets[t]=r,this},get:function(t,n,r){var i=Object.getFromPath(this.sets,t);if(null!=i){var a=typeOf(i);return"function"==a?i=i.apply(null,Array.convert(n)):"object"==a&&(i=Object.clone(i)),i}var o=t.indexOf("."),s=o<0?t:t.substr(0,o),u=(this.inherits.sets[s]||[]).combine(this.inherits.locales).include("en-US");r||(r=[]);for(var c=0,l=u.length;c<l;c++)if(!r.contains(u[c])){r.include(u[c]);var h=e[u[c]];if(h&&null!=(i=h.get(t,n,r)))return i}return""},inherit:function(t,e){t=Array.convert(t),e&&!this.inherits.sets[e]&&(this.inherits.sets[e]=[]);for(var n=t.length;n--;)(e?this.inherits.sets[e]:this.inherits.locales).unshift(t[n]);return this}})}(),Locale.define("en-US","Date",{months:["January","February","March","April","May","June","July","August","September","October","November","December"],months_abbr:["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],days:["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"],days_abbr:["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],dateOrder:["month","date","year"],shortDate:"%m/%d/%Y",shortTime:"%I:%M%p",AM:"AM",PM:"PM",firstDayOfWeek:0,ordinal:function(t){return t>3&&t<21?"th":["th","st","nd","rd","th"][Math.min(t%10,4)]},lessThanMinuteAgo:"less than a minute ago",minuteAgo:"about a minute ago",minutesAgo:"{delta} minutes ago",hourAgo:"about an hour ago",hoursAgo:"about {delta} hours ago",dayAgo:"1 day ago",daysAgo:"{delta} days ago",weekAgo:"1 week ago",weeksAgo:"{delta} weeks ago",monthAgo:"1 month ago",monthsAgo:"{delta} months ago",yearAgo:"1 year ago",yearsAgo:"{delta} years ago",lessThanMinuteUntil:"less than a minute from now",minuteUntil:"about a minute from now",minutesUntil:"{delta} minutes from now",hourUntil:"about an hour from now",hoursUntil:"about {delta} hours from now",dayUntil:"1 day from now",daysUntil:"{delta} days from now",weekUntil:"1 week from now",weeksUntil:"{delta} weeks from now",monthUntil:"1 month from now",monthsUntil:"{delta} months from now",yearUntil:"1 year from now",yearsUntil:"{delta} years from now"}),function(){var t=this.Date,e=t.Methods={ms:"Milliseconds",year:"FullYear",min:"Minutes",mo:"Month",sec:"Seconds",hr:"Hours"};["Date","Day","FullYear","Hours","Milliseconds","Minutes","Month","Seconds","Time","TimezoneOffset","Week","Timezone","GMTOffset","DayOfYear","LastMonth","LastDayOfMonth","UTCDate","UTCDay","UTCFullYear","AMPM","Ordinal","UTCHours","UTCMilliseconds","UTCMinutes","UTCMonth","UTCSeconds","UTCMilliseconds"].each((function(e){t.Methods[e.toLowerCase()]=e}));var n=function(t,e,r){return 1==e?t:t<Math.pow(10,e-1)?(r||"0")+n(t,e-1,r):t};t.implement({set:function(t,n){t=t.toLowerCase();var r=e[t]&&"set"+e[t];return r&&this[r]&&this[r](n),this}.overloadSetter(),get:function(t){t=t.toLowerCase();var n=e[t]&&"get"+e[t];return n&&this[n]?this[n]():null}.overloadGetter(),clone:function(){return new t(this.get("time"))},increment:function(e,n){switch(n=null!=n?n:1,e=e||"day"){case"year":return this.increment("month",12*n);case"month":var r=this.get("date");return this.set("date",1).set("mo",this.get("mo")+n),this.set("date",r.min(this.get("lastdayofmonth")));case"week":return this.increment("day",7*n);case"day":return this.set("date",this.get("date")+n)}if(!t.units[e])throw new Error(e+" is not a supported interval");return this.set("time",this.get("time")+n*t.units[e]())},decrement:function(t,e){return this.increment(t,-1*(null!=e?e:1))},isLeapYear:function(){return t.isLeapYear(this.get("year"))},clearTime:function(){return this.set({hr:0,min:0,sec:0,ms:0})},diff:function(e,n){return"string"==typeOf(e)&&(e=t.parse(e)),((e-this)/t.units[n||"day"](3,3)).round()},getLastDayOfMonth:function(){return t.daysInMonth(this.get("mo"),this.get("year"))},getDayOfYear:function(){return(t.UTC(this.get("year"),this.get("mo"),this.get("date")+1)-t.UTC(this.get("year"),0,1))/t.units.day()},setDay:function(e,n){null==n&&""===(n=t.getMsg("firstDayOfWeek"))&&(n=1),e=(7+t.parseDay(e,!0)-n)%7;var r=(7+this.get("day")-n)%7;return this.increment("day",e-r)},getWeek:function(e){null==e&&""===(e=t.getMsg("firstDayOfWeek"))&&(e=1);var n,r=this,i=(7+r.get("day")-e)%7,a=0;if(1==e){var o=r.get("month"),s=r.get("date")-i;if(11==o&&s>28)return 1;0==o&&s<-2&&(r=new t(r).decrement("day",i),i=0),(n=new t(r.get("year"),0,1).get("day")||7)>4&&(a=-7)}else n=new t(r.get("year"),0,1).get("day");return a+=r.get("dayofyear"),a+=6-i,(a+=(7+n-e)%7)/7},getOrdinal:function(e){return t.getMsg("ordinal",e||this.get("date"))},getTimezone:function(){return this.toString().replace(/^.*? ([A-Z]{3}).[0-9]{4}.*$/,"$1").replace(/^.*?\(([A-Z])[a-z]+ ([A-Z])[a-z]+ ([A-Z])[a-z]+\)$/,"$1$2$3")},getGMTOffset:function(){var t=this.get("timezoneOffset");return(t>0?"-":"+")+n((t.abs()/60).floor(),2)+n(t%60,2)},setAMPM:function(t){t=t.toUpperCase();var e=this.get("hr");return e>11&&"AM"==t?this.decrement("hour",12):e<12&&"PM"==t?this.increment("hour",12):this},getAMPM:function(){return this.get("hr")<12?"AM":"PM"},parse:function(e){return this.set("time",t.parse(e)),this},isValid:function(t){return t||(t=this),"date"==typeOf(t)&&!isNaN(t.valueOf())},format:function(e){if(!this.isValid())return"invalid date";if(e||(e="%x %X"),"string"==typeof e&&(e=a[e.toLowerCase()]||e),"function"==typeof e)return e(this);var r=this;return e.replace(/%([a-z%])/gi,(function(e,i){switch(i){case"a":return t.getMsg("days_abbr")[r.get("day")];case"A":return t.getMsg("days")[r.get("day")];case"b":return t.getMsg("months_abbr")[r.get("month")];case"B":return t.getMsg("months")[r.get("month")];case"c":return r.format("%a %b %d %H:%M:%S %Y");case"d":return n(r.get("date"),2);case"e":return n(r.get("date"),2," ");case"H":return n(r.get("hr"),2);case"I":return n(r.get("hr")%12||12,2);case"j":return n(r.get("dayofyear"),3);case"k":return n(r.get("hr"),2," ");case"l":return n(r.get("hr")%12||12,2," ");case"L":return n(r.get("ms"),3);case"m":return n(r.get("mo")+1,2);case"M":return n(r.get("min"),2);case"o":return r.get("ordinal");case"p":return t.getMsg(r.get("ampm"));case"s":return Math.round(r/1e3);case"S":return n(r.get("seconds"),2);case"T":return r.format("%H:%M:%S");case"U":return n(r.get("week"),2);case"w":return r.get("day");case"x":return r.format(t.getMsg("shortDate"));case"X":return r.format(t.getMsg("shortTime"));case"y":return r.get("year").toString().substr(2);case"Y":return r.get("year");case"z":return r.get("GMTOffset");case"Z":return r.get("Timezone")}return i}))},toISOString:function(){return this.format("iso8601")}}).alias({toJSON:"toISOString",compare:"diff",strftime:"format"});var r=["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],i=["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],a={db:"%Y-%m-%d %H:%M:%S",compact:"%Y%m%dT%H%M%S",short:"%d %b %H:%M",long:"%B %d, %Y %H:%M",rfc822:function(t){return r[t.get("day")]+t.format(", %d ")+i[t.get("month")]+t.format(" %Y %H:%M:%S %Z")},rfc2822:function(t){return r[t.get("day")]+t.format(", %d ")+i[t.get("month")]+t.format(" %Y %H:%M:%S %z")},iso8601:function(t){return t.getUTCFullYear()+"-"+n(t.getUTCMonth()+1,2)+"-"+n(t.getUTCDate(),2)+"T"+n(t.getUTCHours(),2)+":"+n(t.getUTCMinutes(),2)+":"+n(t.getUTCSeconds(),2)+"."+n(t.getUTCMilliseconds(),3)+"Z"}},o=[],s=t.parse,u=function(e,n,r){var i=-1,a=t.getMsg(e+"s");switch(typeOf(n)){case"object":i=a[n.get(e)];break;case"number":if(!(i=a[n]))throw new Error("Invalid "+e+" index: "+n);break;case"string":var o=a.filter((function(t){return this.test(t)}),new RegExp("^"+n,"i"));if(!o.length)throw new Error("Invalid "+e+" string");if(o.length>1)throw new Error("Ambiguous "+e);i=o[0]}return r?a.indexOf(i):i},c=1900,l=70;t.extend({getMsg:function(t,e){return Locale.get("Date."+t,e)},units:{ms:Function.convert(1),second:Function.convert(1e3),minute:Function.convert(6e4),hour:Function.convert(36e5),day:Function.convert(864e5),week:Function.convert(6084e5),month:function(e,n){var r=new t;return 864e5*t.daysInMonth(null!=e?e:r.get("mo"),null!=n?n:r.get("year"))},year:function(e){return e=e||(new t).get("year"),t.isLeapYear(e)?316224e5:31536e6}},daysInMonth:function(e,n){return[31,t.isLeapYear(n)?29:28,31,30,31,30,31,31,30,31,30,31][e]},isLeapYear:function(t){return t%4==0&&t%100!=0||t%400==0},parse:function(e){var n,r=typeOf(e);return"number"==r?new t(e):"string"!=r?e:(e=e.clean()).length?(o.some((function(t){var r=t.re.exec(e);return!!r&&(n=t.handler(r))})),n&&n.isValid()||(n=new t(s(e)))&&n.isValid()||(n=new t(e.toInt())),n):null},parseDay:function(t,e){return u("day",t,e)},parseMonth:function(t,e){return u("month",t,e)},parseUTC:function(e){var n=new t(e),r=t.UTC(n.get("year"),n.get("mo"),n.get("date"),n.get("hr"),n.get("min"),n.get("sec"),n.get("ms"));return new t(r)},orderIndex:function(e){return t.getMsg("dateOrder").indexOf(e)+1},defineFormat:function(t,e){return a[t]=e,this},defineParser:function(t){return o.push(t.re&&t.handler?t:g(t)),this},defineParsers:function(){return Array.flatten(arguments).each(t.defineParser),this},define2DigitYearStart:function(t){return c=t-(l=t%100),this}}).extend({defineFormats:t.defineFormat.overloadSetter()});var h,f=function(e){return new RegExp("(?:"+t.getMsg(e).map((function(t){return t.substr(0,3)})).join("|")+")[a-z]*")},d={d:/[0-2]?[0-9]|3[01]/,H:/[01]?[0-9]|2[0-3]/,I:/0?[1-9]|1[0-2]/,M:/[0-5]?\d/,s:/\d+/,o:/[a-z]*/,p:/[ap]\.?m\.?/,y:/\d{2}|\d{4}/,Y:/\d{4}/,z:/Z|[+-]\d{2}(?::?\d{2})?/};d.m=d.I,d.S=d.M;var g=function(e){if(!h)return{format:e};var n=[],r=(e.source||e).replace(/%([a-z])/gi,(function(e,n){return function(e){switch(e){case"T":return"%H:%M:%S";case"x":return(1==t.orderIndex("month")?"%m[-./]%d":"%d[-./]%m")+"([-./]%y)?";case"X":return"%H([.:]%M)?([.:]%S([.:]%s)?)? ?%p? ?%z?"}return null}(n)||e})).replace(/\((?!\?)/g,"(?:").replace(/ (?!\?|\*)/g,",? ").replace(/%([a-z%])/gi,(function(t,e){var r=d[e];return r?(n.push(e),"("+r.source+")"):e})).replace(/\[a-z\]/gi,"[a-z\\u00c0-\\uffff;&]");return{format:e,re:new RegExp("^"+r+"$","i"),handler:function(e){e=e.slice(1).associate(n);var r=(new t).clearTime(),i=e.y||e.Y;for(var a in null!=i&&p.call(r,"y",i),"d"in e&&p.call(r,"d",1),("m"in e||e.b||e.B)&&p.call(r,"m",1),e)p.call(r,a,e[a]);return r}}},p=function(e,n){if(!n)return this;switch(e){case"a":case"A":return this.set("day",t.parseDay(n,!0));case"b":case"B":return this.set("mo",t.parseMonth(n,!0));case"d":return this.set("date",n);case"H":case"I":return this.set("hr",n);case"m":return this.set("mo",n-1);case"M":return this.set("min",n);case"p":return this.set("ampm",n.replace(/\./g,""));case"S":return this.set("sec",n);case"s":return this.set("ms",1e3*("0."+n));case"w":return this.set("day",n);case"Y":return this.set("year",n);case"y":return(n=+n)<100&&(n+=c+(n<l?100:0)),this.set("year",n);case"z":"Z"==n&&(n="+00");var r=n.match(/([+-])(\d{2}):?(\d{2})?/);return r=(r[1]+"1")*(60*r[2]+(+r[3]||0))+this.getTimezoneOffset(),this.set("time",this-6e4*r)}return this};t.defineParsers("%Y([-./]%m([-./]%d((T| )%X)?)?)?","%Y%m%d(T%H(%M%S?)?)?","%x( %X)?","%d%o( %b( %Y)?)?( %X)?","%b( %d%o)?( %Y)?( %X)?","%Y %b( %d%o( %X)?)?","%o %b %d %X %z %Y","%T","%H:%M( ?%p)?"),Locale.addEvent("change",(function(t){Locale.get("Date")&&function(t){h=t,d.a=d.A=f("days"),d.b=d.B=f("months"),o.each((function(t,e){t.format&&(o[e]=g(t.format))}))}(t)})).fireEvent("change",Locale.getCurrent())}(),Locale.define("zh-CHS","Date",{months:["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],months_abbr:["一","二","三","四","五","六","七","八","九","十","十一","十二"],days:["星期日","星期一","星期二","星期三","星期四","星期五","星期六"],days_abbr:["日","一","二","三","四","五","六"],dateOrder:["year","month","date"],shortDate:"%Y-%m-%d",shortTime:"%I:%M%p",AM:"AM",PM:"PM",firstDayOfWeek:1,ordinal:"",lessThanMinuteAgo:"不到1分钟前",minuteAgo:"大约1分钟前",minutesAgo:"{delta}分钟之前",hourAgo:"大约1小时前",hoursAgo:"大约{delta}小时前",dayAgo:"1天前",daysAgo:"{delta}天前",weekAgo:"1星期前",weeksAgo:"{delta}星期前",monthAgo:"1个月前",monthsAgo:"{delta}个月前",yearAgo:"1年前",yearsAgo:"{delta}年前",lessThanMinuteUntil:"从现在开始不到1分钟",minuteUntil:"从现在开始約1分钟",minutesUntil:"从现在开始约{delta}分钟",hourUntil:"从现在开始1小时",hoursUntil:"从现在开始约{delta}小时",dayUntil:"从现在开始1天",daysUntil:"从现在开始{delta}天",weekUntil:"从现在开始1星期",weeksUntil:"从现在开始{delta}星期",monthUntil:"从现在开始一个月",monthsUntil:"从现在开始{delta}个月",yearUntil:"从现在开始1年",yearsUntil:"从现在开始{delta}年"}),Locale.define("zh-CHT","Date",{months:["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],months_abbr:["一","二","三","四","五","六","七","八","九","十","十一","十二"],days:["星期日","星期一","星期二","星期三","星期四","星期五","星期六"],days_abbr:["日","一","二","三","四","五","六"],dateOrder:["year","month","date"],shortDate:"%Y-%m-%d",shortTime:"%I:%M%p",AM:"AM",PM:"PM",firstDayOfWeek:1,ordinal:"",lessThanMinuteAgo:"不到1分鐘前",minuteAgo:"大約1分鐘前",minutesAgo:"{delta}分鐘之前",hourAgo:"大約1小時前",hoursAgo:"大約{delta}小時前",dayAgo:"1天前",daysAgo:"{delta}天前",weekAgo:"1星期前",weeksAgo:"{delta}星期前",monthAgo:"1个月前",monthsAgo:"{delta}个月前",yearAgo:"1年前",yearsAgo:"{delta}年前",lessThanMinuteUntil:"從現在開始不到1分鐘",minuteUntil:"從現在開始約1分鐘",minutesUntil:"從現在開始約{delta}分鐘",hourUntil:"從現在開始1小時",hoursUntil:"從現在開始約{delta}小時",dayUntil:"從現在開始1天",daysUntil:"從現在開始{delta}天",weekUntil:"從現在開始1星期",weeksUntil:"從現在開始{delta}星期",monthUntil:"從現在開始一個月",monthsUntil:"從現在開始{delta}個月",yearUntil:"從現在開始1年",yearsUntil:"從現在開始{delta}年"}),"undefined"==typeof JSON&&(this.JSON={}),function(){var special={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},escape=function(t){return special[t]||"\\u"+("0000"+t.charCodeAt(0).toString(16)).slice(-4)};JSON.validate=function(t){return t=t.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,"@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]").replace(/(?:^|:|,)(?:\s*\[)+/g,""),/^[\],:{}\s]*$/.test(t)},JSON.encode=JSON.stringify?function(t){return JSON.stringify(t)}:function(t){switch(t&&t.toJSON&&(t=t.toJSON()),typeOf(t)){case"string":return'"'+t.replace(/[\x00-\x1f\\"]/g,escape)+'"';case"array":return"["+t.map(JSON.encode).clean()+"]";case"object":case"hash":var e=[];return Object.each(t,(function(t,n){var r=JSON.encode(t);r&&e.push(JSON.encode(n)+":"+r)})),"{"+e+"}";case"number":case"boolean":return""+t;case"null":return"null"}return null},JSON.decode=function(string,secure){if(!string||"string"!=typeOf(string))return null;if(secure||JSON.secure){if(JSON.parse)return JSON.parse(string);if(!JSON.validate(string))throw new Error("JSON could not decode the input; security is enabled and the value is not secure.")}return eval("("+string+")")}}();

//fetch方法
const _getBytes = function(str){
    const buffer = new ArrayBuffer(str.length);
    const uint8Array = new Uint8Array(buffer);
    for (let i = 0; i < str.length; i++) {
        uint8Array[i] = str.charCodeAt(i);
    }
    return uint8Array;
}
/**
 * this.fetch是一个方法，用于发起http请求。<br/>
 * fetch方法模拟浏览器环境中Fetch API，但要简化一些。fetch方法调用java.net.HttpURLConnection类发起请求。
 * @module fetch()
 * @o2category server.common
 * @o2ordernumber 150
 * @o2cn 方法定义
 * @param {(String)} url 要获取资源的 URL。
 * @param {Object} options  一个配置项对象，包括对请求的设置。可选的参数有：method, headers, body
 * @return {Promise} resolve 时回传 Response 对象。
 * @o2syntax
 * const resPromise = fetch(url, options);
 * @example
 * <caption>
 *    <b>样例一：<b> 发起一个GET请求，获取响应文本：
 * </caption>
 * fetch('http://hostname/resource').then((res)=>{
 *      return res.text();
 * }).then((text)=>{
 *      console.log(`响应的文本信息是：${text}`);
 * });
 * @example
 * <caption>
 *    <b>样例二一：<b> 发起get请求。获取json数据，并在header中附带认证信息。
 * </caption>
 * fetch('http://hostname/resource', {
 *     headers: {
 *         "Authorization": "xxxxxxxxxxxxx"
 *     }
 * }).then((res)=>{
 *      return res.json();
 * }).then((json)=>{
 *      console.log(`响应的json对象是：${JSON.stringify(json, null, '\t')}`);
 * });
 *
 * @example
 * <caption>
 *    <b>样例三：<b> 发起一个POST请求，获取响应json：
 * </caption>
 * fetch('http://hostname/resource', {
 *     method: 'post',
 *     body: '{"postData": "this is post data"}',
 *     headers: {
 *         "Content-Type": "application/json; charset=UTF-8"
 *     }
 * }).then((res)=>{
 *      return res.json();
 * }).then((json)=>{
 *      console.log(`响应的json对象是：${JSON.stringify(json, null, '\t')}`);
 * });
 *
 * @example
 * <caption>
 *    <b>样例四：<b> 使用res.blob()方法获取Response流（java.io.ByteArrayInputStream）；或使用res.arrayBuffer()方法获取Response的ArrayBuffer对象
 * </caption>
 * fetch('http://hostname/resource').then((res)=>{
 *      return res.blob();
 * }).then((blob)=>{
 *      //获取到 blob，实际上是一个（java.io.ByteArrayInputStream）对象
 * });
 *
 * fetch('http://hostname/resource').then((res)=>{
 *      return res.arrayBuffer();
 * }).then((arrayBuffer)=>{
 *      //获取到 arrayBuffer对象
 * });
 *
 * @example
 * <caption>
 *    <b>样例五：<b> 错误处理。如果遇到网络错误，fetch将会reject，而如果是服务器响应的错误，如:500, 415等不是一个网络故障，所以想要精确的判断 fetch() 是否成功，需要包含 promise resolved 的情况下，再判断 res.ok 是否为 true。
 * </caption>
 * fetch('http://hostname/resource').then((res)=>{
 *      if (!res.ok){
 *          throw new Error("response was not OK");
 *      }
 *      return res.json();
 * }).then((json)=>{
 *      console.log(`响应的json对象是：${JSON.stringify(json, null, '\t')}`);
 * }).catch((e)=>{
 *     //处理请求错误
 * });
 *
 * @example
 * <caption>
 *    <b>样例六：<b> 获取响应的信息。
 * </caption>
 * fetch('http://hostname/resource').then((res)=>{
 *      res.ok          //请求是否成功
 *      res.status      //请求响应的状态码
 *      res.statusText  //请求响应的状态文本
 *      res.headers     //响应头
 *      res.error       //java.net.HttpURLConnection的错误流
 *      res.connection  //java.net.HttpURLConnection对象
 *
 *      res.text()          //响应内容文本
 *      res.json()          //响应内容json对象
 *      res.blob()          //响应内容Response流（java.io.ByteArrayInputStream）
 *      res.arrayBuffer()   //响应内容Response的ArrayBuffer对象
 * });
 *
 * @example
 * <caption>
 *    <b>样例七：<b> 使用await。
 * </caption>
 * try{
 *      const res = await fetch('http://hostname/resource');
 *      if (res.ok){
 *          const json = await res.json();
 *          //获取到json对象
 *      }else{
 *          log.error(`请求出现错误：${res.statusText}`);
 *      }
 * }catch(e){
 *      log.error(`请求出现错误：${e.message}`);
 * }
 */
const fetch = function(url, options = {}) {
    const JavaURL = Java.type('java.net.URL');
    const urlObj = new JavaURL(url);
    return new Promise((resolve, reject) => {
        try{
            const httpURLConnection = urlObj.openConnection();
            // 设置请求方法
            httpURLConnection.setRequestMethod(options.method ? options.method.toUpperCase() : 'GET');
            // 设置请求头
            if (options.headers) {
                for (const [header, value] of Object.entries(options.headers)) {
                    httpURLConnection.setRequestProperty(header, value);
                }
            }

            // 处理请求体
            if (options.body) {
                httpURLConnection.setDoOutput(true);
                const output = httpURLConnection.getOutputStream();
                output.write(_getBytes(options.body));
                output.flush();
                output.close();
            }

            // 获取响应码
            const responseCode = httpURLConnection.getResponseCode();

            const getResponseBody = function(inputStream){
                const JavaInputStreamReader = Java.type('java.io.InputStreamReader');
                const JavaBufferedReader = Java.type('java.io.BufferedReader');
                const reader = new JavaBufferedReader(new JavaInputStreamReader(inputStream));
                let inputLine;
                const responseData = [];
                while ((inputLine = reader.readLine()) !== null) {
                    responseData.push(inputLine);
                }
                reader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return responseData.join('\n');
            }

            if (responseCode >= 200 && responseCode < 300) {};
            const isOk = (responseCode >= 200 && responseCode < 300);
            const inputStream = (isOk) ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream();

            const responseObject = {
                ok: isOk,
                status: responseCode,
                statusText: httpURLConnection.getResponseMessage(),
                headers: httpURLConnection.getHeaderFields(),
                error: httpURLConnection.getErrorStream(),

                text: async () => getResponseBody(inputStream),
                json: async () => JSON.parse(getResponseBody(inputStream)),

                blob: async () => {
                    const byteArray = inputStream.readAllBytes();
                    const JavaByteArrayInputStream = Java.type('java.io.ByteArrayInputStream');
                    return new JavaByteArrayInputStream(byteArray);
                },

                arrayBuffer: async () => {
                    const byteArray = inputStream.readAllBytes();
                    return Uint8Array.from(byteArray).buffer;
                },

                body: {
                    getReader: () => ({
                        read: () => {
                            const value = inputStream.read();
                            return { value, done: value === -1 };
                        },
                    }),
                    cancel: () => {
                        httpURLConnection.disconnect();
                    },
                    // add other body related properties/methods as needed
                },
                connection: httpURLConnection
            };
            resolve(responseObject);
        }catch (e){
            reject(e);
        }
    });
}

//执行JS脚本
/**
 * 执行代码片段，类似eval。使用Function包装。
 * @module exec()
 * @o2category server.common
 * @o2ordernumber 151
 * @o2cn 方法定义
 * @param text {String} 需要执行的代码片段
 * @param bind {Object} 代码片段中this的指向
 * @param arg {Object} 传入参数。如：{x:1, y:'5'}，在代码片段中可使用 x 和 y 变量。
 * @param throwError {boolean} 当代码片段运行错误时，是否抛出错误
 * @returns {any} 返回执行的代码片段的返回值
 */
const exec = function(text, bind, arg=null, throwError=true){
    const b = bind || globalThis;
    const p = (arg) ? Object.values(arg) : [];
    const k = (arg) ? Object.keys(arg).join(',') : '';
    try {
        return Function('return function('+k+'){' + text + '}')().apply(b, p);
    }catch(e){
        if (throwError) throw e;
        console.error(e);
        return '';
    }
}

//方法定义
/**
 * this.define是一个方法，您在脚本中您可以通过this.define()来定义自己的方法。<br/>
 * 通过这种方式定义方法，在不同的应用使用相同的方法名称也不会造成冲突。
 * @module define()
 * @o2category server.common
 * @o2ordernumber 160
 * @o2cn 方法定义
 * @param {(String)} name 定义的方法名称。
 * @param {Function} fun  定义的方法
 * @param {Boolean} [overwrite] 定义的方法是否能被覆盖重写。默认值为true。
 * @o2syntax
 * this.define(name, fun, overwrite)
 * @example
 * <caption>
 *    <b>样例一：</b>在通用脚本中定义一个通用的方法去获取公文管理所有的文种，在查询语句中根据该方法来拼接JPQL。<br/>
 *     1、在内容管理应用中有一个fileRes的应用，在该应用中创建一个脚本，命名为FileSql，并定义方法。
 *     <img src='img/module/include/server_define1.png' />
 * </caption>
 * //定义一个方法
 * this.define("getFileSQL",function(){
 *   const application = ["公司发文","部门发文","党委发文"];
 *   const appSql = " ( ";
 *   for (let i=0; i<application.length; i++){
 *       if (i==application.length-1){
 *           appSql = appSql + " o.applicationName = '"+application[i]+"' "
 *       }else{
 *           appSql = appSql + " o.applicationName = '"+application[i]+"' OR "
 *       }
 *   }
 *   appSql = appSql + " ) ";
 *   return appSql;
 *});
 * @example
 * <caption>
 *      2、在查询语句中使用该方法。
 *     <img src='img/module/include/server_define2.png'/>
 * </caption>
 * this.include({
 *   type : "cms",
 *   application : "fileRes",
 *   name : "FileSql"
 * })
 *
 * var sql = this.getFileSQL();
 *
 * return "SELECT o FROM com.x.processplatform.core.entity.content.Task o WHERE "+sql
 */
const define = function(name, fun, overwrite){
    const over = overwrite===false;
    const o = {};
    o[name] = {"value": fun, "configurable": over};
    Object.defineProperties(globalThis, o);
}

//restful服务Action
const Action = function(root, json){
    this.actions = json;
    this.root = root;

    var invokeFunction = function(service, parameters, name){
        var _self = this;
        return function(...pars){
            let success, failure, async, data, file;
            const parameter = {};
            const firstType = typeof pars[0];

            if (firstType==='function'){
                success = pars.shift();
                failure = pars.shift();
            }

            parameters.each((p)=>{
                parameter[p] = pars.shift();
            });
            if (['post', 'put'].includes(service['method']?.toLowerCase())){
                data = pars.shift();
            }

            if (firstType!=='function'){
                success = pars.shift();
                failure = pars.shift();
            }

            return _self.invoke({name, data, parameter, success, failure});
        };
    };
    var createMethod = function(service, key){
        const jaxrsUri = service.uri;
        const re = new RegExp("(?<={).+?(?=})", "g");
        const parameters = jaxrsUri.match(re) || [];
        this[key] = invokeFunction.call(this, service, parameters, key);
    };

    this.invoke = function(option){
        if (this.actions[option.name]){
            let uri = this.actions[option.name].uri;
            const method = this.actions[option.name].method || "get";
            if (option.parameter){
                Object.keys(option.parameter).forEach(function(key){
                    var v = option.parameter[key];
                    uri = uri.replace("{"+key+"}", v);
                });
            }
            let res = null;
            try{
                switch (method.toLowerCase()){
                    case "get":
                        res = globalThis.applications.getQuery(this.root, uri);
                        break;
                    case "post":
                        res = globalThis.applications.postQuery(this.root, uri, JSON.stringify(option.data));
                        break;
                    case "put":
                        res = globalThis.applications.putQuery(this.root, uri, JSON.stringify(option.data));
                        break;
                    case "delete":
                        res = globalThis.applications.deleteQuery(this.root, uri);
                        break;
                    default:
                        res = globalThis.applications.getQuery(this.root, uri);
                }
                if (res && res.getType().toString()==="success"){
                    const json = JSON.parse(res.toString());
                    if (option.success) option.success(json);
                    return json;
                }else{
                    const json = ((res) ? JSON.parse(res.toString()) : null)
                    if (option.failure) option.failure(json);
                    return json;
                }
            }catch(e){
                if (option.failure) option.failure(e);
            }
            return res;
        }
    };

    Object.keys(this.actions).forEach((key)=>{
        const service = this.actions[key];
        if (service.uri) if (!this[key]) createMethod.call(this, service, key);
    });
}
Object.assign(Action, {
    get applications(){
        return (globalThis.java_resources) ? globalThis.java_resources.getApplications() : null;
    }
});

/**
 * 本文档说明如何在后台脚本中使用Actions调用平台的RESTful服务。<br/>
 * 通过访问以下地址来查询服务列表：http://server/x_program_center/jest/list.html (v7.2之前版本需要加端口20030)
 * @module server.Actions
 * @o2cn 服务调用
 * @o2category server.common
 * @o2ordernumber 165
 * @o2syntax
 * //获取Actions
 * this.Actions
 */
const Actions = {
    "loadedActions": {},
    /**
     * 平台预置了Actions对象用于调用平台提供的服务，您可以使用this.Actions.load来获取这些方法。由于是运行在服务器端，服务都是同步调用。
     * @method load
     * @methodOf module:server.Actions
     * @instance
     * @param {String} root 平台RESTful服务根，具体服务列表参见:http://server/x_program_center/jest/list.html。(v7.2之前版本需要加端口20030)
     * 如:
     *<pre><code class='language-js'>
     * "x_processplatform_assemble_surface" //流程平台相关服务根
     * </code></pre>
     * @return {Object} 返回action对象，用于后续服务调用
     * @o2syntax
     * var actions = this.Actions.load( root );
     * @o2syntax
     * //获取流程平台服务对象。
     * var processAction = this.Actions.load("x_processplatform_assemble_surface");
     * @o2syntax
     * <caption>
     *     通过this.Actions.load(root)方法得到action对象，就可以访问此服务下的方法了。<br/>
     *     访问方法的规则如下：
     *  </caption>
     *  var requestString = this.Actions.load( root )[actionName][methodName]( arguements );
     *
     *  requestString : 服务返回的响应数据，字符串格式，可以通过 requestObjest = JSON.parse(requestString);解析成对象
     *
     *  root : 平台服务根名称，如果 x_processplatform_assemble_surface
     *
     *  actionName : 服务下的Action分类名称，如 TaskAction
     *
     *  methodName : Action分类下的方法名称，如 get
     *
     *  arguements : 需调用的RESTful服务的相关参数。这些参数需要按照先后顺序传入。根据实际情况可以省略某些参数。参数序列分别是:
     *
     *      uri的参数, data(Post, Put方法), success, failure, async。
     *
     *      uri参数：如果有uri有多个参数，需要按先后顺序传入。
     *
     *      data参数：要提交到后台的数据。POST 和 PUT 方法需要传入，GET方法和DELETE方法省略。
     *
     *      success参数：服务执行成功时的回调方法，形如 function(json){
     *          json为后台服务传回的数据
     *      }。
     *
     *      failure 参数：服务执行失败时的回调方法，形如 function(xhr){
     *          xhr XmlHttpRequest对象，服务器请求失败时有值
     *       }
     *      此参数可以省略，如果省略，系统会自动弹出错误信息。
     *  @o2syntax
     *  <caption>
     *  处理返回的数据有两种方式，二选一即可：<br/>
     *  1、该方法返回的结果是响应的 json对象。<br/>
     *  2、通过success方法作为第一个参数来处理结果。
     *  </caption>
     *  //success：arguements中的第一个function对象
     *  function(json){
     *    //json为后台服务传回的数据
     *  }
     *  @example
     * <caption>
     *     <b>样例1:</b>
     *     根据x_processplatform_assemble_surface服务获取当前用户的待办列表：<br/>
     *     可以通过对应服务的查询页面，http://server/x_processplatform_assemble_surface/jest/index.html (v7.2之前版本需要加端口20020)<br/>
     *     可以看到以下界面：<img src="img/module/Actions/Actions.png"/>
     *     我们可以找到TaskAction的V2ListPaging服务是列式当前用户待办的服务。<br/>
     *     该服务有以下信息：<br/>
     *     1、actionName是：TaskAction<br/>
     *     2、methodName是：V2ListPaging<br/>
     *     3、有两个url参数，分别是 page(分页), size(每页数量)<br/>
     *     4、有一系列的body参数<br/>
     *     5、该服务方法类型是POST<br/>
     *     根据这些信息我们可以组织出下面的方法：
     * </caption>
     * const processAction = this.Actions.load("x_processplatform_assemble_surface"); //获取action
     * const method = processAction.TaskAction.V2ListPaging; //获取列式方法
     * //执行方法1
     * method(
     *  1,  //uri 第1个参数，如果无uri参数，可以省略
     *  20, //uri 第2个参数，如果无uri参数，可以省略，如果还有其他uri参数，可以用逗号, 分隔
     *  {   //body 参数，对POST和PUT请求，该参数必须传，可以为空对象
     *      processList : [xxx] //具体参数
     *  },
     *  function(json){ //正确调用的回调
     *       //json.data得到服务返回数据
     *  },
     *  function(responseJSON){ //可选，错误信息, json格式
     *      print( JSON.stringify(responseJSON) )
     *  }
     * );
     *
     * //执行方法2
     * const responsJSON = method( 1, 20, {processList : [xxx]} );
     * @example
     * <caption>出错信息responseJSON的格式</caption>
     * {
     *       "type": "error", //类型为错误
     *       "message": "标识为:343434 的 Task 对象不存在.", //提示文本
     *       "date": "2020-12-29 17:02:13", //出错时间
     *       "prompt": "com.x.base.core.project.exception.ExceptionEntityNotExist" //后台错误类
     *}
     * @example
     * <caption>
     *     <b>样例2:</b>
     *      已知流程实例的workid，在脚本中获取数据，修改后进行保存。
     * </caption>
     * //查询服务列表找到获取data数据服务为DataAction的getWithWork方法
     * //查询服务列表找到更新data数据服务为DataAction的updateWithWork方法
     *
     * const workid = "cce8bc22-225a-4f85-8132-7374d546886e";
     * const json = this.Actions.load("x_processplatform_assemble_surface").DataAction.getWithWork( //平台封装好的方法
     *      workid, //uri的参数
     * );
     * const data = json.data;
     *
     * data.subject = "新标题"; //修改数据
     * const result = this.Actions.load("x_processplatform_assemble_surface").DataAction.updateWithWork(
     *      workid, //uri的参数
     *      data, //保存的数据
     * );
     */
    "load": function(root){
        if (this.loadedActions[root]) return this.loadedActions[root];
        const jaxrsString = globalThis.applications.describeApi(root);
        const json = JSON.parse(jaxrsString.toString());
        if (json && json.jaxrs){
            const actionObj = {};
            json.jaxrs.forEach((o)=>{
                if (o.methods && o.methods.length){
                    const actions = {};
                    o.methods.forEach((m)=>{
                        const o = {"uri": "/"+m.uri};
                        if (m.method) o.method = m.method;
                        if (m.enctype) o.enctype = m.enctype;
                        actions[m.name] = o;
                    });
                    actionObj[o.name] = new Action(root, actions);
                }
            });
            this.loadedActions[root] = actionObj;
            return actionObj;
        }
        return null;
    }
};

//log输出
const _parsePrint = function(str, ...pars){
    if (!str && str!==0 && str!==false) return str;
    let text = (typeof str!=="string") ? str.toString() : str;
    try{
        text = text.replace(/%s/g, ()=>pars.shift()).toString();
        text += ' '+pars.map((p)=>p.toString()).join(' ')
    }catch(e){}
    return text;
}

/**
 * this.print是一个方法，在服务器控制台输出信息。<br/>
 * @module print()
 * @o2cn 控制台输出打印
 * @o2category server.common
 * @o2ordernumber 152
 *
 * @param {(String)} text 要输出的文本信息。</b>
 * @param {(String)} type 要输出的文本信息的类型，会添加到输出信息的前面，默认为“PRINT”。</b>
 * @example
 * this.print("这是我要输出的信息");
 * //2021-12-20 13:26:24.739 [script] PRINT 这是我要输出的信息
 *
 * this.print("这是一个自定义类型的信息", "MYTYPE");
 * //2021-12-20 13:26:24.765 [script] MYTYPE 这是一个自定义类型的信息
 */
const print = function(text, type){
    // const d = new Intl.DateTimeFormat(undefined, {
    //     year: "numeric",
    //     month: "2-digit",
    //     day: "2-digit",
    //     hour: "2-digit",
    //     minute: "2-digit",
    //     second: "2-digit",
    //     fractionalSecondDigits: 3
    // }).format(new Date());
    var t = (type || "PRINT").toUpperCase();
    console[(t==='ERROR') ? 'error' : 'log'](`[script] ${t} ${text}`);
}

/**
 * this.log是一个服务器控制台输出方法，可使用log, log.error, log.info, log.warn方法在服务器控制台输入信息。<br/>
 * @module log
 * @o2category server.common
 * @o2ordernumber 155
 * @o2cn 分等级控制台输出
 * @example
 * log("这是我要输出的信息");
 * //2021-12-20 13:26:24.739 [script] PRINT 这是我要输出的信息
 *
 * log.error("这是一个错误信息");
 * //2021-12-20 13:26:24.765 [script] ERROR 这是一个错误信息
 *
 * log.info("这是一个普通信息");
 * //2021-12-20 13:26:24.765 [script] INFO 这是一个普通信息
 *
 * log.warn("这是一个警告信息");
 * //2021-12-20 13:26:24.765 [script] WARN 这是一个警告信息
 */
const log = function(...arg){
    print(_parsePrint(...arg));
}
const keys = ['error', 'info', 'warn'];
keys.forEach((k)=>{
    log[k] = function(...arg){
        print(_parsePrint(...arg), k);
    }
});

//组织相关服务
const _getNameFlag = function(name){
    if (Array.isArray(name)){
        return name.map(n=>((typeof n)==="object" ? (n.distinguishedName || n.id || n.unique || n.name) : n));
    }else{
        return [((typeof name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name];
    }
}

/**
 * 您可以通过this.org获取组织中的人员、人员属性、组织、组织属性、身份、群组和角色。后端调用都是同步的。
 * @module server.org
 * @o2cn 组织查询
 * @o2category server.common
 * @o2ordernumber 170
 * @since v9.0.4
 * @property    {GroupFactory}  group   后端的GroupFactory实例，可用于获取group群组相关数据
 * @property    {IdentityFactory}  identity   后端的IdentityFactory实例，可用于获取identity身份相关数据
 * @property    {PersonFactory}  person   后端的PersonFactory实例，可用于获取person人员相关数据
 * @property    {PersonAttributeFactory}  personAttribute   后端的GroupFactory实例，可用于获取personAttribute人员属性相关数据
 * @property    {RoleFactory}  role   后端的RoleFactory实例，可用于获取role角色相关数据
 * @property    {UnitFactory}  unit   后端的UnitFactory实例，可用于获取unit相关数据
 * @property    {UnitAttributeFactory}  unitAttribute   后端的UnitAttributeFactory实例，可用于获取unitAttribute组织属性相关数据
 * @property    {UnitDutyFactory}  unitDuty   后端的UnitDutyFactory实例，可用于获取unitDuty组织属性相关数据
 * @o2syntax
 * //您可以通过以下引用来获取当前实例的org对象，如下：
 * this.org;
 * globalThis.org;
 * org
 *
 *@example
 * //通过后端java类，来获取当前人所在的部门名称
 * const unit = org.unit();
 * const unitNames = unit.listWithPerson("张三@xxx@P");
 */
const org = {}
const _javaOrg = function(){
    return globalThis.java_resources.getOrganization();
}
Object.defineProperties(org, {
    oGroup:             { get: ()=>_javaOrg().group() },
    oIdentity:          { get: ()=>_javaOrg().identity() },
    oPerson:            { get: ()=>_javaOrg().person() },
    oPersonAttribute:   { get: ()=>_javaOrg().personAttribute() },
    oRole:              { get: ()=>_javaOrg().role() },
    oUnit:              { get: ()=>_javaOrg().unit() },
    oUnitAttribute:     { get: ()=>_javaOrg().unitAttribute() },
    oUnitDuty:          { get: ()=>_javaOrg().unitDuty() }
});
Object.assign(org,  {
    group: ()=> this.org.oGroup,
    identity: ()=> this.org.oIdentity,
    person: ()=> this.org.oPerson,
    personAttribute: ()=> this.org.oPersonAttribute,
    role: ()=> this.org.oRole,
    unit: ()=> this.org.oUnit,
    unitAttribute: ()=> this.org.oUnitAttribute,
    unitDuty: ()=> this.org.oUnitDuty,

    getObject(o, v){
        if (v && Array.isArray(v)){
            return v.map(name=>JSON.parse(o.getObject(name).toString()));
        }
        return null;
    },

    //群组***************
    //获取群组--返回群组的对象数组
    /**
     根据群组标识获取对应的群组对象或数组：group对象或数组
     * @method getGroup
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 群组的distinguishedName、name、id、unique属性值，群组对象，或上述属性值和对象的数组。
     * @return {GroupData|GroupData[]} 返回群组，单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.GroupAction.listObject|example=Group
     * @o2syntax
     * //返回群组，单个是Object，多个是Array。
     * const groupList = this.org.getGroup( name );
     */
    getGroup: function(name){
        const v = this.oGroup.listObject(_getNameFlag(name));
        const v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //查询下级群组--返回群组的对象数组
    //nested  布尔  true嵌套下级；false直接下级；默认false；
    /**
     根据群组标识获取下级群组的对象数组：group对象数组。
     * @method listSubGroup
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 群组的distinguishedName、name、id、unique属性值，群组对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有下级群组；false直接下级群组；默认false。
     * @return {GroupData[]} 返回群组数组。
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithGroupSubDirectObject|example=Group
     * @o2syntax
     * //返回嵌套下级群组数组。
     * const groupList = this.org.listSubGroup( name, true );
     */
    listSubGroup: function(name, nested){
        return this.getObject(this.oGroup, this.oGroup[(nested) ? 'listWithGroupSubNested' : 'listWithGroupSubDirect'](_getNameFlag(name)));
    },


    //查询上级群组--返回群组的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    /**
     根据群组标识获取上级群组的对象数组：group对象数组。
     * @method listSupGroup
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 群组的distinguishedName、name、id、unique属性值，群组对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有上级群组；false直接上级群组；默认false。
     * @return {GroupData[]} 返回群组数组。
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithGroupSupDirectObject|example=Group
     * @o2syntax
     * //返回嵌套上级群组数组。
     * const groupList = this.org.listSupGroup( name, true );
     */
    listSupGroup:function(name, nested){
        return this.getObject(this.oGroup, this.oGroup[(nested) ? 'listWithGroupSupNested' : 'listWithGroupSupDirect'](_getNameFlag(name)));
    },

    //人员所在群组（嵌套）--返回群组的对象数组
    /**
     * 根据人员标识获取所有的群组对象数组。如果群组具有群组（group）成员，且群组成员中包含该人员，那么该群组也被返回。
     * @method listGroupWithPerson
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {GroupData[]} 返回群组对象数组。
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithPersonObject|example=Group
     * @o2syntax
     * //返回群组数组。
     * const groupList = this.org.listGroupWithPerson( name );
     */
    listGroupWithPerson:function(name){
        return this.getObject(this.oGroup, this.oGroup.listWithPerson(_getNameFlag(name)));
    },

    //群组是否拥有角色--返回true, false
    /**
     * 群组是否拥有角色。
     * @method groupHasRole
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag} name - 群组的distinguishedName、name、id、unique属性值，群组对象。
     * @param {RoleFlag|RoleFlag[]} roleList - 角色的distinguishedName、name、id、unique属性值，角色对象；或上述属性值和对象的数组。
     * @return {Boolean} 如果群组拥有角色返回true, 否则返回false。
     * @o2syntax
     * //返回判断结果。
     * const groupList = this.org.groupHasRole( name, roleList );
     */
    groupHasRole: function(name, roleList){
        const nameFlag = (typeof name==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
        return this.oGroup.hasRole(nameFlag, _getNameFlag(roleList));
    },

    //角色***************
    //获取角色--返回角色的对象数组
    /**
     * 根据角色标识获取对应的角色对象或数组。
     * @method getRole
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {RoleFlag|RoleFlag[]} name - 角色的distinguishedName、name、id、unique属性值，角色对象；或上述属性值和对象的数组。
     * @return {RoleData|RoleData[]} 返回角色，单个为Object，多个为Array。
     * @o2ActionOut x_organization_assemble_express.RoleAction.listObject|example=Role
     * @o2syntax
     * //返回角色，单个为对象，多个为数组。
     * const roleList = this.org.getRole( name );
     */
    getRole: function(name){
        const v = this.oRole.listObject(_getNameFlag(name));
        const v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //人员所有角色（嵌套）--返回角色的对象数组
    /**
     * 根据人员标识获取所有的角色对象数组。如果角色具有群组（group）成员，且群组中包含该人员，那么该角色也被返回。
     * @method listRoleWithPerson
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {RoleData[]} 返回角色对象数组。
     * @o2ActionOut x_organization_assemble_express.RoleAction.listWithPersonObject|example=Role
     * @o2syntax
     * //返回角色数组。
     * const roleList = this.org.listRoleWithPerson( name );
     */
    listRoleWithPerson:function(name){
        return this.getObject(this.oRole, this.oRole.listWithPerson(_getNameFlag(name)));
    },

    //人员***************
    //人员是否拥有角色--返回true, false
    /**
     * 人员是否拥有角色。
     * @method personHasRole
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag} name - 人员的distinguishedName、id、unique属性值，人员对象。
     * @param {RoleFlag|RoleFlag[]} roleList - 角色的distinguishedName、name、id、unique属性值，角色对象；或上述属性值和对象的数组。
     * @return {Boolean} 如果人员拥有角色返回true, 否则返回false。
     * @o2syntax
     * //返回判断结果。
     * const groupList = this.org.personHasRole( name, roleList );
     */
    personHasRole: function(name, roleList){
        const nameFlag = (typeof name==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
        return this.oPerson.hasRole(nameFlag, _getNameFlag(roleList));
    },

    //获取人员,附带身份,身份所在的组织,个人所在群组,个人拥有角色.
    /**
     根据人员标识获取对应的人员对象,附带身份,身份所在的组织,个人所在群组,个人拥有角色.
     * @method getPersonData
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {String} name - 人员的distinguishedName、id、unique属性值，人员名称。
     * @return {PersonData} 返回人员对象。
     * @o2ActionOut x_organization_assemble_express.PersonAction.get|example=PersonData
     * @o2syntax
     * //返回人员对象。
     * const person = this.org.getPersonData( name );
     */
    getPersonData: function(name){
        const v = this.oPerson.getExt((typeof name==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name);
        return (!v) ? null : JSON.parse(v.toString());
    },

    //获取人员--返回人员的对象数组
    /**
     根据人员标识获取对应的人员对象或数组：person对象或数组
     * @method getPerson
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @param {(Boolean)} [findCN] 是否需要额外查找中文名称（如张三），默认false。如果为true，除匹配unique和distingiushedName外，还会在名称的第一段中查找所有匹配到的人（精确匹配）。
     * @return {PersonData|PersonData[]} 返回人员，单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listObject|example=Person
     * @o2syntax
     * //返回人员，单个是对象，多个是数组。
     * const personList = this.org.getPerson( name );
     */
    getPerson: function(name, findCN){
        const v = this.oPerson.listObject(_getNameFlag(name), !!findCN);
        const v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //查询下级人员--返回人员的对象数组
    //nested  布尔  true嵌套下级；false直接下级；默认false；
    /**
     根据人员标识获取下级人员的对象数组：person对象数组。该上下级关系被人员的汇报对象值（superior）决定。
     * @method listSubPerson
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有下级人员；false直接下级人员；默认false。
     * @return {PersonData[]} 返回人员数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonSubDirectObject|example=Person
     * @o2syntax
     * //返回嵌套下级人员数组。
     * const personList = this.org.listSubPerson( name, true );
     */
    listSubPerson: function(name, nested){
        return this.getObject(this.oPerson, this.oPerson[(nested) ? 'listWithPersonSubNested' : 'listWithPersonSubDirect'](_getNameFlag(name)));
    },

    //查询上级人员--返回人员的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    /**
     *根据人员标识获取上级人员的对象数组：person对象数组。该上下级关系被人员的汇报对象值（superior）决定。
     * @method listSupPerson
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有上级人员；false直接上级人员；默认false。
     * @return {PersonData[]} 返回人员数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonSupDirectObject|example=Person
     * @o2syntax
     * //返回嵌套上级人员数组。
     * const personList = this.org.listSupPerson( name, true );
     */
    listSupPerson: function(name, nested){
        return this.getObject(this.oPerson, this.oPerson[(nested) ? 'listWithPersonSupNested' : 'listWithPersonSupDirect'](_getNameFlag(name)));
    },

    //获取群组的所有人员--返回人员的对象数组
    /**
     * 根据群组标识获取人员对象成员：person对象数组。
     * @method listPersonWithGroup
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 群组的distinguishedName、name、id、unique属性值，群组对象，或上述属性值和对象的数组。
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithGroupObject|example=Person
     * @o2syntax
     * //返回人员数组。
     * const personList = this.org.listPersonWithGroup( group );
     */
    listPersonWithGroup: function(name){
        return this.getObject(this.oPerson, this.oPerson.listWithGroup(_getNameFlag(name)));
    },

    //获取角色的所有人员--返回人员的对象数组
    /**
     * 根据角色标识获取人员对象数组：person对象数组。
     * @method listPersonWithRole
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {RoleFlag|RoleFlag[]} name - 角色的distinguishedName、name、id、unique属性值，角色对象，或上述属性值和对象的数组。
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithRoleObject|example=Person
     * @o2syntax
     * //返回人员数组。
     * const personList = this.org.listPersonWithRole( role );
     */
    listPersonWithRole: function(name){
        return this.getObject(this.oPerson, this.oPerson.listWithRole(_getNameFlag(name)));
    },

    //获取身份的所有人员--返回人员的对象数组
    /**
     * 根据身份标识获取人员对象成员：person对象数组。
     * @method listPersonWithIdentity
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 身份的distinguishedName、id、unique属性值，身份对象，或上述属性值和对象的数组。
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithIdentityObject|example=Person
     * @o2syntax
     * //返回人员数组。
     * const personList = this.org.listPersonWithIdentity( identity );
     */
    listPersonWithIdentity: function(name){
        return this.getObject(this.oPerson, this.oPerson.listWithIdentity(_getNameFlag(name)));
    },

    //获取身份的所有人员--返回人员的对象数组
    /**
     * 根据身份标识获取人员对象：person对象数组。
     * @method getPersonWithIdentity
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 身份的distinguishedName、id、unique属性值，身份对象，或上述属性值和对象的数组。
     * @return {PersonData|PersonData[]} 返回人员对象，单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithIdentityObject|example=Person
     * @o2syntax
     * //返回人员，单个是Object，多个是Array。
     * const personList = this.org.listPersonWithIdentity( identity );
     */
    getPersonWithIdentity: function(name){
        const v = this.oPerson.listWithIdentity(_getNameFlag(name));
        const arr = this.getObject(this.oPerson, v);
        return (arr && arr.length===1) ? arr[0] : arr;
    },

    //查询组织成员的人员--返回人员的对象数组
    //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
    /**
     * 根据组织标识获取人员对象成员：person对象数组。
     * @method listPersonWithUnit
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested] 是否嵌套获取组织以及下级组织的人员，true表示嵌套，flase表示获取直接组织。默认为false
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithUnitSubDirectObject|example=Person
     * @o2syntax
     * //返回组织的直接人员数组。
     * const personList = this.org.listPersonWithUnit( unit );
     *
     * //返回组织的以及嵌套下级组织所有的人员数组。
     * const personList = this.org.listPersonWithUnit( unit, true );
     */
    listPersonWithUnit: function(name, nested){
        return this.getObject(this.oPerson, this.oPerson[(nested) ? 'listWithUnitSubNested' : 'listWithUnitSubDirect'](_getNameFlag(name)));
    },

    //人员属性************
    //添加人员属性值(在属性中添加values值，如果没有此属性，则创建一个)
    /**
     * 添加人员属性值(在属性中添加values值，如果没有此属性，则创建一个)
     * @method appendPersonAttribute
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag} person - 人员的distinguishedName、id、unique属性值，人员对象。
     * @param {String} attr 属性名称。
     * @param {String[]} values 属性值，必须为数组。
     * @o2syntax
     * //添加人员属性值
     * this.org.appendPersonAttribute( person, attribute, valueArray);
     */
    appendPersonAttribute: function(person, attr, values){
        const personFlag = (typeof person==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        return this.oPersonAttribute.appendWithPersonWithName(personFlag, attr, values);
    },

    //设置人员属性值(将属性值修改为values，如果没有此属性，则创建一个)
    /**
     * 设置人员属性值(将属性值修改为values，如果没有此属性，则创建一个)
     * @method setPersonAttribute
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag} person - 人员的distinguishedName、id、unique属性值，人员对象。
     * @param {String} attr 属性名称。
     * @param {String[]} values 属性值，必须为数组。
     * @o2syntax
     * //添加人员属性值
     * this.org.setPersonAttribute( person, attribute, valueArray);
     */
    setPersonAttribute: function(person, attr, values){
        const personFlag = (typeof person==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        return this.oPersonAttribute.setWithPersonWithName(personFlag, attr, values);
    },

    //获取人员属性值
    /**
     根据人员和属性名称获取属性值数组。
     * @method getPersonAttribute
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag} person - 人员的distinguishedName、id、unique属性值，人员对象。
     * @param {String} attr 属性名称。
     * @return {String[]} 返回属性值数组，
     * 如：<pre><code class='language-js'>[ value1, value2 ]</code></pre>
     * @o2syntax
     * //返回该人员的属性值数组。
     * const attributeList = this.org.getPersonAttribute( person, attr );
     */
    getPersonAttribute: function(person, attr){
        const personFlag = (typeof person==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        const v = this.oPersonAttribute.listAttributeWithPersonWithName(personFlag, attr);
        if (v && v.length){
            return v.map(value=>value.toString())
        }
        return [];
    },

    //列出人员所有属性的名称
    /**
     列出人员所有属性的名称数组。
     * @method listPersonAttributeName
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {String[]} 返回人员属性名称数组，
     * 如：<pre><code class='language-js'>[ attributeName1, attributeName2 ]</code></pre>
     * @o2syntax
     * //返回人员所有属性的名称数组。
     * const attributeNameList = this.org.listPersonAttributeName( person );
     */
    listPersonAttributeName: function(name){
        const p = _getNameFlag(name);
        const nameList = [];
        for (let i=0; i<p.length; i++){
            const v = this.oPersonAttribute.listNameWithPerson(p[i]);
            if (v && v.length){
                for (let j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])===-1) nameList.push(v[j].toString());
                }
            }
        }
        return nameList;
    },

    //身份**********
    //获取身份
    /**
     根据身份标识获取对应的身份对象或数组
     * @method getIdentity
     * @o2membercategory identity
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 身份的distinguishedName、id、unique属性值，身份对象，或上述属性值和对象的数组。
     * @return {IdentityData|IdentityData[]} 返回身份，单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.IdentityAction.listObject|example=Identity|ignoreNoDescr=true|ignoreProps=[woUnitDutyList,woUnit,woGroupList]
     * @o2syntax
     * //返回身份，单个是对象，多个是数组。
     * const identityList = this.org.getIdentity( name );
     */
    getIdentity: function(name){
        const v = this.oIdentity.listObject(_getNameFlag(name));
        const v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //列出人员的身份
    /**
     * 根据人员标识获取对应的身份对象数组。
     * @method listIdentityWithPerson
     * @o2membercategory identity
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {IdentityData[]} 返回身份对象数组。
     * @o2ActionOut x_organization_assemble_express.IdentityAction.listWithPersonObject|example=Identity
     * @o2syntax
     * //返回身份对象数组。
     * const identityList = this.org.listIdentityWithPerson( person );
     */
    listIdentityWithPerson: function(name){
        return this.getObject(this.oIdentity, this.oIdentity.listWithPerson(_getNameFlag(name)));
    },

    //查询组织成员身份--返回身份的对象数组
    //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
    /**
     * 根据组织标识获取对应的身份对象数组：identity对象数组。
     * @method listIdentityWithUnit
     * @o2membercategory identity
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested] true嵌套的所有身份成员；false直接身份成员；默认false。
     * @return {IdentityData[]} 返回身份对象数组。
     * @o2ActionOut x_organization_assemble_express.IdentityAction.listWithUnitSubNestedObject|example=Identity
     * @o2syntax
     * //返回直接组织身份对象数组。
     * const identityList = this.org.listIdentityWithUnit( unit );
     *
     * //返回嵌套组织身份对象数组。
     * const identityList = this.org.listIdentityWithUnit( unit, true );
     */
    listIdentityWithUnit: function(name, nested){
        return this.getObject(this.oIdentity, this.oIdentity[(nested) ? 'listWithUnitSubNested' : 'listWithUnitSubDirect'](_getNameFlag(name)));
    },

    //组织**********
    //获取组织
    /**
     根据组织标识获取对应的组织：unit对象或数组
     * @method getUnit
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、id、unique属性值，组织对象，或上述属性值和对象的数组。]
     * @param {(Boolean)} [findCN] 是否需要额外查找中文名称（如综合部），默认false。如果为true，除匹配unique和distingiushedName外，还会在名称的第一段中查找所有匹配到的部门（精确匹配）。
     * @return {UnitData|UnitData[]} 单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listObject|example=Unit
     * @o2syntax
     * //返回组织，单个是对象，多个是数组。
     * const unitList = this.org.getUnit( name );
     */
    getUnit: function(name, findCN){
        const v = this.oUnit.listObject(_getNameFlag(name), !!findCN);
        const v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //查询组织的下级--返回组织的对象数组
    //nested  布尔  true嵌套下级；false直接下级；默认false；
    /**
     根据组织标识获取下级组织的对象数组：unit对象数组。
     * @method listSubUnit
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有下级组织；false直接下级组织；默认false。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitSubNestedObject|example=Unit
     * @o2syntax
     * //返回嵌套下级组织数组。
     * const unitList = this.org.listSubUnit( name, true );
     */
    listSubUnit: function(name, nested){
        return this.getObject(this.oUnit, this.oUnit[(nested) ? 'listWithUnitSubNested' : 'listWithUnitSubDirect'](_getNameFlag(name)));
    },

    //查询组织的上级--返回组织的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    /**
     根据组织标识批量获取上级组织的对象数组：unit对象数组。
     * @method listSupUnit
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有上级组织；false直接上级组织；默认false。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitSupNestedObject|example=Unit
     * @o2syntax
     * //返回嵌套上级组织数组。
     * const unitList = this.org.listSupUnit( name, true );
     */
    listSupUnit: function(name, nested){
        return this.getObject(this.oUnit, this.oUnit[(nested) ? 'listWithUnitSupNested' : 'listWithUnitSupDirect'](_getNameFlag(name)));
    },

    //根据个人身份获取组织
    //flag 数字    表示获取第几层的组织
    //     字符串  表示获取指定类型的组织
    //     空     表示获取直接所在的组织
    /**
     根据个人身份获取组织：unit对象或数组。
     * @method getUnitByIdentity
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag} name - 身份的distinguishedName、id、unique属性值，身份对象。
     * @param {String|Number} [flag]  当值为数字的时候， 表示获取第几层的组织。<br/> 当值为字符串的时候，表示获取指定类型的组织。<br/> 当值为空的时候，表示获取直接所在组织。
     * @return {UnitData} 返回对应的组织对象。
     * @o2ActionOut x_organization_assemble_express.UnitAction.getWithIdentityWithLevelObject|example=Unit
     * @o2syntax
     * //返回直接所在组织。
     * const unit = this.org.getUnitByIdentity( name );
     *
     * //返回第一层组织。
     * const unit = this.org.getUnitByIdentity( name, 1 );
     *
     * * //返回类型为company的最近上级组织。
     * const unit = this.org.getUnitByIdentity( name, "company" );
     */
    getUnitByIdentity: function(name, flag){
        let getUnitMethod = "current";
        let v;
        if (flag){
            if (typeof flag ==="string") getUnitMethod = "type";
            if (typeof flag ==="number") getUnitMethod = "level";
        }
        const n = _getNameFlag(name)[0];
        switch (getUnitMethod){
            case "current":
                v = this.oUnit.getWithIdentity(n);
                break;
            case "type":
                v = this.oUnit.getWithIdentityWithType(n, flag);
                break;
            case "level":
                v = this.oUnit.getWithIdentityWithLevel(n, flag);
                break;
        }
        const o = this.getObject(this.oUnit, [v]);
        return (o && o.length===1) ? o[0] : o;
    },

    //列出身份所在组织的所有上级组织
    /**
     * 批量查询身份所在的组织,并递归查找其上级组织对象.
     * @method listAllSupUnitWithIdentity
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 身份的distinguishedName、id、unique属性值，身份对象，或上述属性值和对象的数组。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithIdentitySupNestedObject|example=Unit
     * @o2syntax
     * //返回组织数组。
     * const unitList = this.org.listAllSupUnitWithIdentity( name );
     */
    listAllSupUnitWithIdentity: function(name){
        return this.getObject(this.oUnit, this.oUnit.listWithIdentitySupNested(_getNameFlag(name)));
    },

    //获取人员所在的所有组织（直接所在组织）
    /**
     * 根据个人标识批量获取组织对象成员：Unit对象数组。
     * @method listUnitWithPerson
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithPersonObject|example=Unit
     * @o2syntax
     * //返回组织数组。
     * const unitList = this.org.listUnitWithPerson( name );
     */
    listUnitWithPerson: function(name){
        return this.getObject(this.oUnit, this.oUnit.listWithPerson(_getNameFlag(name)));
    },

    //列出人员所在组织的所有上级组织
    /**
     * 根据个人标识批量查询所在组织及所有上级组织：Unit对象数组。
     * @method listAllSupUnitWithPerson
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {UnitData[]} 返回个人所在组织及所有上级组织。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithPersonSupNestedObject|example=Unit
     * @o2syntax
     * //返回组织数组。
     * const unitList = this.org.listAllSupUnitWithPerson( name );
     */
    listAllSupUnitWithPerson: function(name){
        return this.getObject(this.oUnit, this.oUnit.listWithPersonSupNested(_getNameFlag(name)));
    },

    //根据组织属性，获取所有符合的组织
    /**
     * 根据组织属性，获取所有符合的组织。
     * @method listUnitWithAttribute
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {String} name 组织属性名称。
     * @param {String} attribute 组织属性值。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitAttributeObject|example=Unit
     * @o2syntax
     * //返回组织数组。
     * const unitList = this.org.listUnitWithAttribute( attributeName, attributeName );
     */
    listUnitWithAttribute: function(name, attribute){
        return this.getObject(this.oUnit, this.oUnit.listWithUnitAttribute(name, attribute));
    },

    //根据组织职务，获取所有符合的组织
    /**
     * 根据组织职务，获取所有符合的组织。
     * @method listUnitWithDuty
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {String} name 组织职务名称。
     * @param {IdentityFlag} id 身份的distinguishedName、id、unique属性值，身份对象。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitDutyObject|example=Unit
     * @o2syntax
     * //返回组织数组。
     * const unitList = this.org.listUnitWithDuty( dutyName, identity );
     */
    listUnitWithDuty: function(name, id){
        const idflag = (typeof id==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id;
        return this.getObject(this.oUnit, this.oUnit.listWithUnitDuty(name, idflag));
    },

    //组织职务***********
    //获取指定的组织职务的身份
    /**
     * 根据职务名称和组织名称获取身份。
     * @method getDuty
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {String} duty 组织职务名称。
     * @param {UnitFlag} id 组织的distinguishedName、id、unique属性值，组织对象。
     * @return {IdentityData[]} 返回身份数组。
     * @o2ActionOut x_organization_assemble_express.UnitDutyAction.getWithUnitWithName|example=Identity
     * @o2syntax
     * //返回身份数组。
     * const identityList = this.org.getDuty( dutyName, unit );
     */
    getDuty: function(duty, id){
        const unit = (typeof id==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id;
        return this.getObject(this.oIdentity, this.oUnitDuty.listIdentityWithUnitWithName(unit, duty));
    },

    //获取身份的所有职务名称
    /**
     * 批量获取身份的所有职务名称。
     * @method listDutyNameWithIdentity
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} id - 身份的distinguishedName、id、unique属性值，身份对象，或上述属性值和对象的数组。
     * @return {String[]} 返回职务名称数组。
     * @o2syntax
     * //返回职务名称数组。
     * const dutyNameList = this.org.listDutyNameWithIdentity( identity );
     */
    listDutyNameWithIdentity: function(id){
        const ids = _getNameFlag(id);
        const nameList = [];
        for (let i=0; i<ids.length; i++){
            const v = this.oUnitDuty.listNameWithIdentity(ids[i]);
            if (v && v.length){
                for (let j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])===-1) nameList.push(v[j].toString());
                }
            }
        }
        return nameList;
    },

    //获取组织的所有职务名称
    /**
     * 批量获取组织的所有职务名称。
     * @method listDutyNameWithUnit
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @return {String[]} 返回职务名称数组。
     * @o2syntax
     * //返回职务名称数组。
     * const dutyNameList = this.org.listDutyNameWithUnit( unit );
     */
    listDutyNameWithUnit: function(name){
        const ids = _getNameFlag(name);
        const nameList = [];
        for (let i=0; i<ids.length; i++){
            const v = this.oUnitDuty.listNameWithUnit(ids[i]);
            if (v && v.length){
                for (let j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])===-1) nameList.push(v[j].toString());
                }
            }
        }
        return nameList;
    },

    //获取组织的所有职务
    /**
     * 批量获取组织的所有职务。
     * @method listUnitAllDuty
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag} name - 组织的distinguishedName、id、unique属性值，组织对象。
     * @return {Object[]} 返回职务数组
     * @o2ActionOut x_organization_assemble_express.UnitDutyAction.listWithUnitObject|example=Duty
     * @o2syntax
     * //返回职务数组。
     * const dutyList = this.org.listUnitAllDuty( unit );
     */
    listUnitAllDuty: function(name){
        const u = _getNameFlag(name)[0];
        const ds = this.oUnitDuty.listNameWithUnit(u);
        const o = []
        for (let i=0; i<ds.length; i++){
            const v = this.oUnitDuty.listIdentityWithUnitWithName(u, ds[i]);
            o.push({"name": ds[i], "identityList": this.getObject(this.oIdentity, v)});
        }
        return o;
    },

    //组织属性**************
    //添加组织属性值(在属性中添加values值，如果没有此属性，则创建一个)
    /**
     * 添加组织属性值(在属性中添加values值，如果没有此属性，则创建一个)
     * @method appendUnitAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag} unit - 组织的distinguishedName、id、unique属性值，组织对象。
     * @param {String} attr 属性名称。
     * @param {String[]} values 属性值，必须为数组。
     * @o2syntax
     * this.org.appendUnitAttribute( unit, attribute, valueArray);
     */
    appendUnitAttribute: function(unit, attr, values){
        const unitFlag = (typeof unit==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        return this.oUnitAttribute.appendWithUnitWithName(unitFlag, attr, values);
    },

    //设置组织属性值(将属性值修改为values，如果没有此属性，则创建一个)
    /**
     * 设置组织属性值(将属性值修改为values，如果没有此属性，则创建一个)
     * @method setUnitAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag} unit - 组织的distinguishedName、id、unique属性值，组织对象。
     * @param {String} attr 属性名称。
     * @param {String[]} values 属性值，必须为数组。
     * @o2syntax
     * this.org.setUnitAttribute( unit, attribute, valueArray);
     */
    setUnitAttribute: function(unit, attr, values){
        const unitFlag = (typeof unit==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        return this.oUnitAttribute.setWithUnitWithName(unitFlag, attr, values);
    },

    //获取组织属性值
    /**
     根据组织标识和属性名称获取对应属性值。
     * @method getUnitAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag} unit - 组织的distinguishedName、id、unique属性值，组织对象。
     * @param {String} attr 属性名称。
     * @return {String[]} 返回属性值数组，
     * 如：<pre><code class='language-js'>[ value1, value2 ]</code></pre>
     * @o2syntax
     * //返回该组织的属性值数组。
     * const attributeList = this.org.getUnitAttribute( unit, attr );
     */
    getUnitAttribute: function(unit, attr){
        const unitFlag = (typeof unit==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        const v = this.oUnitAttribute.listAttributeWithUnitWithName(unitFlag, attr);
        if (v && v.length){
            return v.map(value=>value.toString());
        }
        return [];
    },

    //列出组织所有属性的名称
    /**
     列出组织所有属性的名称数组。
     * @method listUnitAttributeName
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @return {String[]} 返回组织属性名称数组，
     * 如：<pre><code class='language-js'>[ attributeName1, attributeName2 ]</code></pre>
     * @o2syntax
     * //返回组织所有属性的名称数组。
     * const attributeNameList = this.org.listUnitAttributeName( unit );
     */
    listUnitAttributeName: function(name){
        const p = _getNameFlag(name);
        const nameList = [];
        for (let i=0; i<p.length; i++){
            const v = this.oUnitAttribute.listNameWithUnit(p[i]);
            if (v && v.length){
                for (let j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])===-1) nameList.push(v[j]);
                }
            }
        }
        return nameList;
    },

    //列出组织的所有属性
    /**
     列出组织的所有属性对象数组。
     * @method listUnitAllAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @return {Object[]} 返回组织属性对象数组，如：
     * <pre><code class='language-js'>[{
     *    "name": "部门类别",
     *    "unit": "开发部@kfb@U",
     *    "attributeList": [
     *        "生产部门",
     *        "二级部门"
     *    ]
     * }]</code></pre>
     * @o2syntax
     * //返回组织所有属性的对象数组。
     * const attributeObjectList = this.org.listUnitAllAttribute( unit );
     */
    listUnitAllAttribute: function(name){
        const u = _getNameFlag(name)[0];
        const ds = this.oUnitAttribute.listNameWithUnit(u);
        if (ds && ds.length){
            return ds.map( value=>{
                return {name: value, valueList: this.getUnitAttribute(u, value)};
            });
        }
        return [];
    }
})


//定义所需的服务
const processActions = new Action("x_processplatform_assemble_surface", {
    "getDictionary": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{applicationFlag}"},
    "getDictRoot": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/data"},
    "getDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "DELETE"},
    "getScript": {"uri": "/jaxrs/script/{flag}/application/{applicationFlag}", "method": "POST"},
});
const cmsActions = new Action("x_cms_assemble_control", {
    "getDictionary": {"uri": "/jaxrs/design/appdict/{appDictId}"},
    "getDictRoot": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/data"},
    "getDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data", "method": "DELETE"},
    "getDictRootAnonymous" : {"uri": "/jaxrs/anonymous/surface/appdict/{appDictId}/appInfo/{appId}/data"},
    "getDictDataAnonymous" : {"uri": "/jaxrs/anonymous/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data"},
    "getScript": {"uri": "/jaxrs/script/{flag}/appInfo/{appInfoFlag}", "method": "POST"},
});
const portalActions = new Action("x_portal_assemble_surface", {
    "getDictionary": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}"},
    "getDictRoot": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/data"},
    "getDictData": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/{path}/data", "method": "DELETE"},
    "getScript":  {"uri": "/jaxrs/script/portal/{portal}/name/{name}","method": "POST"}
});
const serviceActions = new Action("x_program_center", {
    "getDictionary": {"uri": "/jaxrs/dict/{id}"},
    "getDictRoot": {"uri": "/jaxrs/dict/{dictFlag}/data"},
    "getDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data", "method": "DELETE"},
    "getScript":  {"uri": "/jaxrs/script/name/{name}/imported","method": "GET"}
});


//include 引用脚本
//optionsOrName : {
//  type : "", 默认为process, 可以为 portal  process  cms
//  application : "", 门户/流程/CMS的名称/别名/id, 默认为当前应用
//  name : "" // 脚本名称/别名/id
//}
//或者name: "" // 脚本名称/别名/id
const includedScripts = {};
/**
 * this.include是一个方法，当您在流程、门户、内容管理或服务管理中创建了脚本配置，可以使用this.include()用来引用脚本配置。<br/>
 * v8.0及以后版本中增加了服务管理的脚本配置。<br/>
 * @module include()
 * @o2cn 脚本引用
 * @o2category server.common
 * @o2ordernumber 175
 *
 * @param {(String|Object)} optionsOrName 可以是脚本标识字符串或者是对象。
 * <pre><code class='language-js'>
 *
 * //如果需要引用其他应用的脚本配置，将options设置为Object;
 * this.include({
 *       //type: 应用类型。可以为 portal  process  cms  service。流程脚本默认为process，服务管理中默认为service
 *       type : "portal",
 *       application : "首页", // 门户、流程、CMS的名称、别名、id。 引用服务管理的脚本则忽略该参数。
 *       name : "initScript" // 脚本配置的名称、别名或id
 * });
 *
 * //引用服务管理中的脚本
 * this.include({
 *   "type": "service",
 *   "name": "scriptName"
 * });
 *
 * //引用流程管理中的脚本
 * this.include({
 *   "type": "process",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 *
 * //引用内容管理中的脚本
 * this.include({
 *   "type": "cms",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 *
 * //引用门户管理中的脚本
 * this.include({
 *   "type": "portal",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 * </code></pre>
 * @param {Function} [callback] 加载后执行的回调方法。
 *
 * @o2syntax
 * //您可以在表单、流程、视图和查询视图的各个嵌入脚本中，通过this.include()来引用本应用或其他应用的脚本配置，如下：
 * this.include( optionsOrName, callback )
 * @example
 * <caption>
 *    <b>样例一：</b>在通用脚本中定义一个通用的方法去获取公文管理所有的文种，在查询语句中根据该方法来拼接JPQL。<br/>
 *     1、在内容管理应用中有一个fileRes的应用，在该应用中创建一个脚本，命名为FileSql，并定义方法。
 *     <img src='img/module/include/server_define1.png' />
 * </caption>
 * //定义一个方法
 * this.define('getFileSQL',function(){
 *   const application = ['公司发文','部门发文','党委发文'];
 *   const apps = application.map((app)=>{
 *       return `o.applicationName = '${app}'`
 *   });
 *   return `(${apps.join(' OR ')})`;
 *});
 * @example
 * <caption>
 *      2、在查询语句中使用该方法。
 *     <img src='img/module/include/server_define2.png'/>
 * </caption>
 * this.include({
 *   type : 'cms',
 *   application : 'fileRes',
 *   name : 'FileSql'
 * });
 * const sql = this.getFileSQL();
 * return `SELECT o FROM com.x.processplatform.core.entity.content.Task o WHERE ${sql}`;
 */
const include = function( optionsOrName , callback ){
    const options = (typeof optionsOrName) == "string" ? { name : optionsOrName } : optionsOrName;
    const name = options.name;

    const type = (!options.type) ? 'process' : options.type;
    const application = type === "service" ? "service" : options.application;

    if (!name || !type || !application){
        log.error("can not find script. missing script name or application");
        return false;
    }

    if (!includedScripts[application]) includedScripts[application] = [];

    if (includedScripts[application].indexOf( name )> -1){
        if (callback) callback.apply(globalThis);
        return;
    }

    const actionsMap = {
        portal: portalActions,
        process: processActions,
        cms: cmsActions,
        service: serviceActions,
    }

    const json = (type==='service') ? actionsMap[type].getScript(name) : actionsMap[type].getScript(name, application, {"importedList":includedScripts[application]});
    includedScripts[application] = includedScripts[application].concat(json.data.importedList);
    includedScripts[application].push(name);
    if (json.data && json.data.text){
        exec(json.data.text, globalThis);
        if (callback) callback.apply(globalThis);
    }
}

//Dict数据字典
/**
 * this.Dict是一个工具类，如果您在流程、内容管理、门户和服务管理中创建了数据字典，可以使用this.Dict类对数据字典进行增删改查操作。<br/>
 * 从v8.0版本开始，支持在门户和服务管理中创建数据字典。
 * @module server.Dict
 * @o2cn 数据字典
 * @o2category server.common
 * @o2ordernumber 180
 * @o2syntax
 * //您可以通过this.Dict()对本应用或其他应用的数据字典中的数据进行增删改查，如下：
 * const dict = new this.Dict( options )
 * @example
 * const dict = new this.Dict({
 *     //type: 应用类型。可以为process  cms portal service。流程脚本默认为process，服务管理中默认为service。
 *    type : "cms",
 *    application : "bulletin", //流程、CMS、门户管理的名称、别名、id。引用服务管理的数组字典则忽略该参数。
 *    name : "bulletinDictionary", // 数据字典的名称、别名、id
 * });
 *
 * //引用服务管理中的数据字典
 * const dict = new this.Dict({
 *   "type": "service",
 *   "name": "dictName"
 * });
 *
 * //引用流程管理中的数据字典
 * const dict = new this.Dict({
 *   "type": "process",
 *   "application": "appName",
 *   "name": "dictName"
 * });
 *
 * //引用内容管理中的数据字典
 * const dict = new this.Dict({
 *   "type": "cms",
 *   "application": "appName",
 *   "name": "dictName"
 * });
 *
 * //引用门户管理中的数据字典
 * const dict = new this.Dict({
 *   "type": "portal",
 *   "application": "appName",
 *   "name": "dictName"
 * });
 */
const Dict = function(optionsOrName){
    const options = (typeof optionsOrName) == "string" ? { name : optionsOrName } : optionsOrName;
    const name = this.name = options.name;
    const type = (!options.type) ? 'process' : options.type;
    const applicationId = options.application || ((globalThis.java_workContext) ? globalThis.java_workContext.getWork().application : "");
    const enableAnonymous = options.enableAnonymous || false;

    const actionMap = {
        cms: cmsActions,
        service: serviceActions,
        portal: portalActions,
        process: processActions
    }
    const action = actionMap[type]

    const encodePath = function( path ){
        const arr = path.split(/\./g);
        const ar = arr.map(function(v){
            return encodeURIComponent(v);
        });
        return ( type === "portal" || type === "service" ) ? ar.join(".") : ar.join("/");
    };
    /**
     * 根据路径获取数据字典中的数据。
     * @method get
     * @methodOf module:server.Dict
     * @static
     * @param {String} [path] 数据字典中的数据路径，允许使用中文。当路径为多级时，用点号(.)分隔。当值为空的时候，表示获取数据字典中的所有数据。
     * @param {Function} [success] 获取数据成功时的回调函数。<b>流程设计后台脚本中无此参数。</b>
     * @param {Function} [failure] 获取数据失败时的回调。<b>流程设计后台脚本中无此参数。</b>
     * @return {(Object|Array|String|Number|Boolean)}
     * 返回数据字典的数据，类型和配置数据字典时候指定的一致。
     * @o2syntax
     * const data = dict.get( path, success, failure )
     * @example
     * <caption>
     *     已经配置好了如下图所示的数据字典
     * <img src='img/module/Dict/dict.png' />
     * </caption>
     * const dict = new this.Dict({
     *     //type: 应用类型。可以为process  cms portal service。默认为process。
     *    type : "cms",
     *    application : "bulletin", //流程、CMS、门户管理的名称、别名、id。引用服务管理的数组字典则忽略该参数。
     *    name : "bulletinDictionary", // 数据字典的名称、别名、id
     * });
     *
     * const data = dict.get();
     * //data的值为
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "公司公告",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 2.0,
     *            "text": "部门公告",
     *            "value": "department"
     *        }
     *    ]
     * }
     *
     *  const category = dict.get("category");
     *  //category的值为
     *  [
     *     {
     *        "enable": true,
     *        "sequence": 1.0,
     *        "text": "公司公告",
     *        "value": "company"
     *    },
     *     {
     *       "enable": "false",
     *       "sequence": 2.0,
     *        "text": "部门公告",
     *        "value": "department"
     *    }
     *  ]
     *
     *  const array0 = dict.get("category.0");
     *  //array0 的值为
     *  {
     *    "enable": true,
     *    "sequence": 1.0,
     *    "text": "公司公告",
     *    "value": "company"
     * }
     *
     * const enable = dict.get("category.0.eanble");
     * //enable 的值为 true
     */
    this.get = function(path, success, failure){
        const getDataMethodName = (enableAnonymous && type === 'cms') ? 'getDictDataAnonymous' : 'getDictData';
        const getRootMethodName = (enableAnonymous && type === 'cms') ? 'getDictRootAnonymous' : 'getDictRoot';
        const methodName = (path) ? getDataMethodName : getRootMethodName;
        const successFun = json=>{if (success) success(json.data);}
        const failureFun = json=>{if (failure) failure(json.data);}

        const par = [encodeURIComponent(this.name)];
        if (type !== "service") par.push(applicationId);
        if (path) par.push(encodePath(path));

        const json = action[methodName](...par, successFun, failureFun);
        return json.data;
    };
    /**
     * 根据路径修改数据字典的数据。
     * @method set
     * @methodOf module:Dict
     * @instance
     * @param {String} path 数据字典中的数据路径，允许使用中文。当路径为多级时，用点号(.)分隔。如果数据路径不存在，则报错。
     * @param {(Object|Array|String|Number|Boolean)} value 修改后的数据
     * @param {Function} [success] 设置数据成功时的回调函数。<b>流程设计后台脚本中无此参数。</b>
     * @param {Function} [failure] 设置数据错误时的回调函数。<b>流程设计后台脚本中无此参数。</b>
     * @o2syntax
     * dict.set( path, data, success, failure )
     * @example
     * const dict = new this.Dict({
     *     //type: 应用类型。可以为process  cms portal service。默认为process。
     *    type : "cms",
     *    application : "bulletin", //流程、CMS、门户管理的名称、别名、id。引用服务管理的数组字典则忽略该参数。
     *    name : "bulletinDictionary", // 数据字典的名称、别名、id
     * });
     *
     * dict.set( "category", { text : "系统公告", value : "system" }, function(data){
     *    //data 形如
     *    //{
     *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id为数据字典ID
     *    //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     * @example
     * <caption>
     *     对Example add的数据字典进行赋值，如下：
     * </caption>
     * const dict = new this.Dict({
     *     //type: 应用类型。可以为process  cms portal service。默认为process。
     *    type : "cms",
     *    application : "bulletin", //流程、CMS、门户管理的名称、别名、id。引用服务管理的数组字典则忽略该参数。
     *    name : "bulletinDictionary", // 数据字典的名称、别名、id
     * });
     *
     * dict.set( "archiveOptions", [ { text : "是" }, { text : "否" } ]);
     *      //数据字典的值变为
     *  {
     *     "category": [
     *         {
     *             "enable": true,
     *             "sequence": 1.0,
     *             "text": "公司公告",
     *             "value": "company"
     *         },
     *         {
     *             "enable": "false",
     *             "sequence": 2.0,
     *             "text": "部门公告",
     *             "value": "department"
     *         },
     *         {
     *             "sequence" : 3.0,
     *             "text": "系统公告",
     *             "value": "system"
     *         }
     *
     *     ],
     *     "archiveOptions" : [ { text : "是" }, { text : "否" } ]
     * }
     *
     * dict.set( "category.2", { text : "县级公告", value : "county" }, function(data){
     *     //data 形如
     *     //{
     *     //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id为数据字典ID
     *     //}
     *  }, function(xhr){
     *     //xhr 为 xmlHttpRequest
     *  });
     *
     *   /数据字典的值变为
     *  {
     *     "category": [
     *         {
     *             "enable": true,
     *             "sequence": 1.0,
     *             "text": "公司公告",
     *             "value": "company"
     *         },
     *         {
     *             "enable": "false",
     *             "sequence": 2.0,
     *             "text": "部门公告",
     *             "value": "department"
     *         },
     *         {
     *             "text": "县级公告",
     *             "value": "county"
     *         }
     *     ],
     *     "archiveOptions" : [ { text : "是" }, { text : "否" } ]
     * }
     *
     * dict.set( "category.1.sequence", 3 );
     * dict.set( "category.2.sequence", 2 );
     *      //数据字典的值变为
     *      {
     *     "category": [
     *         {
     *             "enable": true,
     *             "sequence": 1.0,
     *             "text": "公司公告",
     *             "value": "company"
     *         },
     *         {
     *             "enable": "false",
     *             "sequence": 3.0,
     *             "text": "部门公告",
     *             "value": "department"
     *         },
     *         {
     *             "sequence": 2.0,
     *             "text": "县级公告",
     *             "value": "county"
     *         }
     *     ],
     *     "archiveOptions" : [ { text : "是" }, { text : "否" } ]
     * }
     * @example
     * <caption>
     *     下面是错误的赋值：
     * </caption>
     * dict.set( "category_1", { text : "公司公告" } ); //出错，因为category_1在数据字典中不存在
     */
    this.set = function(path, value, success, failure){
        const successFun = json=>{if (success) success(json.data);}
        const failureFun = json=>{if (failure) failure(json.data);}

        const par = [encodeURIComponent(this.name)];
        if (type !== "service") par.push(applicationId);

        const json = action.setDictData(...par, encodePath(path), value, successFun, failureFun);
        return json.data;
    };

    /**
     * 根据路径新增数据字典的数据。
     * @method add
     * @methodOf module:Dict
     * @instance
     * @param {String} path 数据字典中的数据路径，允许使用中文。当路径为多级时，用点号(.)分隔。如果path在数据字典中已有数据，且原有数据是数组，则数组添加一项；如果原有数据不是数组，则报错。
     * @param {(Object|Array|String|Number|Boolean)} data 需要新增的数据
     * @param {Function} [success] 增加数据成功时的回调函数。<b>流程设计后台脚本中无此参数。</b>
     * @param {Function} [failure] 增加数据错误时的回调函数。<b>流程设计后台脚本中无此参数。</b>
     * @o2syntax
     * dict.add( path, data, success, failure )
     * @example
     * const dict = new this.Dict({
     *     //type: 应用类型。可以为process  cms portal service。默认为process。
     *    type : "cms",
     *    application : "bulletin", //流程、CMS、门户管理的名称、别名、id。引用服务管理的数组字典则忽略该参数。
     *    name : "bulletinDictionary", // 数据字典的名称、别名、id
     * });
     *
     * dict.add( "category", { text : "系统公告", value : "system" }, function(data){
     *    //data 形如
     *    //{
     *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id为数据字典ID
     *    //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     * @example
     * <caption>
     *     对get方法样例的数据字典进行赋值，如下：
     * </caption>
     * const dict = new this.Dict({
     *     //type: 应用类型。可以为process  cms portal service。默认为process。
     *    type : "cms",
     *    application : "bulletin", //流程、CMS、门户管理的名称、别名、id。引用服务管理的数组字典则忽略该参数。
     *    name : "bulletinDictionary", // 数据字典的名称、别名、id
     * });
     *
     * dict.add( "category", { text : "系统公告", value : "system" }, function(data){
     *    //data 形如
     *    //{
     *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id为数据字典ID
     *    //}
     * }, function(xhr, text, error){
     *    //xhr 为 xmlHttpRequest, text 为错误文本， error为Error对象
     * });
     *     //数据字典的值变为
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "公司公告",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 2.0,
     *            "text": "部门公告",
     *            "value": "department"
     *        },
     *        {
     *            "text": "系统公告",
     *            "value": "system"
     *        }
     *    ]
     * }
     *
     *  dict.add( "category.2.sequence", 3 );
     *     //数据字典的值变为
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "公司公告",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 2.0,
     *            "text": "部门公告",
     *            "value": "department"
     *        },
     *        {
     *            "sequence" : 3.0,
     *            "text": "系统公告",
     *            "value": "system"
     *        }
     *    ]
     * }

     * dict.add( "archiveOptions", {
     *    "yes" : "是",
     *    "no" : "否"
     * });
     *     //数据字典的值变为
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "公司公告",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 2.0,
     *            "text": "部门公告",
     *            "value": "department"
     *        },
     *        {
     *            "sequence" : 3.0,
     *            "text": "系统公告",
     *            "value": "system"
     *        }
     *
     *    ],
     *    "archiveOptions" : {
     *        "yes" : "是",
     *        "no" : "否"
     *    }
     * }
     * @example
     * <caption>下面是错误的赋值，如下：</caption>
     * dict.add( "category.3", { text : "系统公告", value : "system" }); //出错，因为不能对数组下标直接赋值
     *
     * dict.add( "category.1.value", { text : "系统公告" } ); //出错，因为不能对已经存在的非数组路径赋值
     */
    this.add = function(path, value, success, failure){
        const successFun = json=>{if (success) success(json.data);}
        const failureFun = json=>{if (failure) failure(json.data);}

        const par = [encodeURIComponent(this.name)];
        if (type !== "service") par.push(applicationId);

        const json = action.addDictData(...par, encodePath(path), value, successFun, failureFun);
        return json.data;
    };
    /**
     * 根据路径删除数据字典的数据。<b>流程设计后台脚本中无此方法。</b>
     * @method delete
     * @methodOf module:Dict
     * @instance
     * @param {String} path 数据字典中的数据路径，允许使用中文。当路径为多级时，用点号(.)分隔。如果数据路径不存在，则报错。
     * @param {Function} [success] 删除数据成功时的回调函数。
     * @param {Function} [failure] 删除数据错误时的回调函数。
     * @o2syntax
     * dict.delete( path, success, failure )
     * @example
     * const dict = new this.Dict({
     *    //type: 应用类型。可以为process  cms portal service。默认为process。
     *    type : "cms",
     *    application : "bulletin", //流程、CMS的名称、别名、id, 默认为当前应用
     *    name : "bulletinDictionary", //流程、CMS、门户管理的名称、别名、id。引用服务管理的数组字典则忽略该参数。
     * });
     *
     * dict.delete( "category", function(){
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     * @example
     * <caption>
     *     对Example set的数据字典进行赋值，如下：
     * </caption>
     * const dict = new this.Dict("bulletinDictionary");
     *
     * dict.delete( "archiveOptions");
     * //数据字典的值变为
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "公司公告",
     *     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 3.0,
     *            "text": "部门公告",
     *            "value": "department"
     *        },
     *        {
     *            "sequence": 2.0,
     *            "text": "县级公告",
     *            "value": "county"
     *        }
     *    ]
     * }
     *
     * dict.delete( "category.2.sequence", function(data){
     *    //data 形如
     *    //{
     *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id为数据字典ID
     *    //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     * //数据字典的值变为
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "公司公告",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 3.0,
     *            "text": "部门公告",
     *            "value": "department"
     *        },
     *        {
     *            "text": "县级公告",
     *            "value": "county"
     *        }
     *    ]
     * }
     *
     * dict.delete( "category.2");
     * //数据字典的值变为
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "公司公告",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 3.0,
     *            "text": "部门公告",
     *            "value": "department"
     *        }
     *    ]
     * }
     * @example
     * <caption>
     *     下面是错误的删除：
     * </caption>
     * dict.delete( "category_1" ); //出错，因为category_1在数据字典中不存在
     */
    this["delete"] = function(path, success, failure){
        const successFun = json=>{if (success) success(json.data);}
        const failureFun = json=>{if (failure) failure(json.data);}

        const par = [encodeURIComponent(this.name)];
        if (type !== "service") par.push(applicationId);

        const json = action.deleteDictData(...par, encodePath(path), successFun, failureFun);
        return json.data;
    };
    this.destory = this["delete"];
}

/**
 * this.Table是一个工具类，您可以使用这个类对数据中心的数据表进行增删改查操作。
 * @module server.Table
 * @o2cn 数据表执行
 * @o2category server.common
 * @o2ordernumber 185
 * @param {String} name 数据表的id、名称或别名。
 * @return {Object} table对象
 * @o2syntax
 * //您可以在脚本中，通过this.Table()来返回Table的对象，如下：
 * const table = new this.Table( tableName )
 */
const Table = function(name){
    this.name = name;
    this.action = Actions.load("x_query_assemble_surface").TableAction;

    /**
     * 列示表中的行对象,下一页。
     * @method listRowNext
     * @methodOf module:server.Table
     * @instance
     * @param {String} id  当前页最后一条数据的Id，如果是第一页使用"(0)"或 "0"。
     * @param {String|Number} count 下一页的行数
     * @param {Function} [success] 调用成功时的回调函数。
     * @param {Function} [failure] 调用错误时的回调函数。
     * @o2syntax
     * table.listRowNext( id, count, success, failure )
     * @example
     * const table = new this.Table("table1");
     *
     * table.listRowNext( "0", 20, function(data){
     *    //data 形如
     *    //{
     *    //    "type": "success",
     *    //    "data":[
     *    //       {
     *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //数据的id
     *    //        "createTime": "2021-11-01 16:23:41", //数据创建时间
     *    //        "updateTime": "2021-11-01 16:23:41", //数据更新时间
     *    //         ... //定义的字段（列）和值
     *    //        }
     *   //     ],
     *   //       "message": "",
     *   //     "date": "2021-11-01 18:34:19",
     *   //     "spent": 13,
     *   //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     */
    this.listRowNext = function(id, count, success, failure){
        return this.action.listRowNext(this.name, id, count, success, failure);
    };
    /**
     * 列示表中的行对象,上一页。
     * @method listRowPrev
     * @methodOf module:server.Table
     * @instance
     * @param {String} id  当前页第一条数据的Id，如果是最后一页使用"(0)"或 "0"。
     * @param {String|Number} count 上一页的行数
     * @param {Function} [success] 调用成功时的回调函数。
     * @param {Function} [failure] 调用错误时的回调函数。
     * @o2syntax
     * table.listRowPrev( id, count, success, failure )
     * @example
     * const table = new this.Table("table1");
     *
     * table.listRowPrev( "0", 20, function(data){
     *    //data 形如
     *    //{
     *    //    "type": "success",
     *    //    "data":[
     *    //       {
     *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //数据的id
     *    //        "createTime": "2021-11-01 16:23:41", //数据创建时间
     *    //        "updateTime": "2021-11-01 16:23:41", //数据更新时间
     *    //         ... //定义的字段（列）和值
     *    //        }
     *   //     ],
     *   //       "message": "",
     *   //     "date": "2021-11-01 18:34:19",
     *   //     "spent": 13,
     *   //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     */
    this.listRowPrev = function(id, count, success, failure){
        return this.action.listRowPrev(this.name, id, count, success, failure);
    };
    /**
     * 根据条件获取表中的数据。
     * @method listRowSelect
     * @methodOf module:server.Table
     * @instance
     * @param {String} [where] 查询条件，格式为jpql语法,o.name='zhangsan'，允许为空。
     * @param {String} [orderBy] 排序条件，格式为：o.updateTime desc，允许为空
     * @param {String|Number} [size] 返回结果集数量,允许为空。
     * @param {Function} [success] 调用成功时的回调函数。
     * @param {Function} [failure] 调用错误时的回调函数。
     * @o2syntax
     * table.listRowSelect( where, orderBy, size, success, failure )
     * @example
     * const table = new this.Table("table1");
     *
     * //查询字段name等于zhangsan的数据，结果按updateTime倒序
     * table.listRowSelect( "o.name='zhangsan'", "o.updateTime desc", 20, function(data){
     *    //data 形如
     *    //{
     *    //    "type": "success",
     *    //    "data":[
     *    //       {
     *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //数据的id
     *    //        "createTime": "2021-11-01 16:23:41", //数据创建时间
     *    //        "updateTime": "2021-11-01 16:23:41", //数据更新时间
     *    //         ... //定义的字段（列）和值
     *    //        }
     *   //     ],
     *   //       "message": "",
     *   //     "date": "2021-11-01 18:34:19",
     *   //     "spent": 13,
     *   //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     */
    this.listRowSelect = function(where, orderBy, size, success, failure){
        return this.action.listRowSelect(this.name, {"where": where, "orderBy": orderBy, "size": size || ""}, success, failure);
    };
    this.listRowSelectWhere = function(where, success, failure){
        return this.action.listRowSelectWhere(this.name, where, success, failure);
    };
    /**
     * 通过where 统计数量。
     * @method rowCountWhere
     * @methodOf module:server.Table
     * @instance
     * @param {String} where 查询条件，格式为jpql语法,o.name='zhangsan'，允许为空。
     * @param {Function} [success] 调用成功时的回调函数。
     * @param {Function} [failure] 调用错误时的回调函数。
     * @o2syntax
     * table.rowCountWhere( where, success, failure )
     * @example
     * const table = new this.Table("table1");
     *
     * //查询字段name等于zhangsan的数据，结果按updateTime倒序
     * table.rowCountWhere( "o.name='zhangsan'", function(data){
     *    //data 形如
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": 5 //符合条件数据的总条数
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     */
    this.rowCountWhere = function(where, success, failure){
        return this.action.rowCountWhere(this.name, where, success, failure);
    };
    /**
     * 删除数据表中指定id的记录。
     * @method deleteRow
     * @methodOf module:server.Table
     * @instance
     * @param {id} [id] 需要删除记录的id。
     * @param {Function} [success] 调用成功时的回调函数。
     * @param {Function} [failure] 调用错误时的回调函数。
     * @o2syntax
     * table.deleteRow( id, success, failure )
     * @example
     * const table = new this.Table("table1");
     *
     * table.deleteRow( "e1f89185-d8b0-4b66-9e34-aed3323d0d79", function(data){
     *    //data 形如
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": true //true表示删除成功，false表示无此数据
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     */
    this.deleteRow = function(id, success, failure){
        return this.action.rowDelete(this.name, id, success, failure);
    };
    /**
     * 删除指定表中所有行的数据。
     * @method deleteAllRow
     * @methodOf module:server.Table
     * @instance
     * @param {Function} [success] 调用成功时的回调函数。
     * @param {Function} [failure] 调用错误时的回调函数。
     * @o2syntax
     * table.deleteAllRow( success, failure, async )
     * @example
     * const table = new this.Table("table1");
     *
     * table.deleteAllRow( function(data){
     *    //data 形如
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": 1 //表示删除的条数，0表示无数据
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     */
    this.deleteAllRow = function(success, failure){
        return this.action.rowDeleteAll(this.name, success, failure);
    };
    /**
     * 获取数据表中指定id的记录。
     * @method getRow
     * @methodOf module:server.Table
     * @instance
     * @param {id} [id] 需要获取记录的id。
     * @param {Function} [success] 调用成功时的回调函数。
     * @param {Function} [failure] 调用错误时的回调函数。
     * @o2syntax
     * table.getRow( id, success, failure )
     * @example
     * const table = new this.Table("table1");
     *
     * table.getRow( "e1f89185-d8b0-4b66-9e34-aed3323d0d79", function(data){
     *    //data 形如
     *    //{
     *    //    "type": "success",
     *    //    "data":{
     *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //数据的id
     *    //        "createTime": "2021-11-01 16:23:41", //数据创建时间
     *    //        "updateTime": "2021-11-01 16:23:41", //数据更新时间
     *    //         ... //定义的字段（列）和值
     *    //     },
     *   //     "message": "",
     *   //     "date": "2021-11-01 18:34:19",
     *   //     "spent": 13,
     *   //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     */
    this.getRow = function(id, success, failure){
        return this.action.rowGet(this.name, id, success, failure);
    };
    /**
     * 往数据表中批量插入数据。
     * @method insertRow
     * @methodOf module:server.Table
     * @instance
     * @param {Object[]} data 需要插入的数据。
     * @param {Function} [success] 调用成功时的回调函数。
     * @param {Function} [failure] 调用错误时的回调函数。
     * @o2syntax
     * table.insertRow( data, success, failure )
     * @example
     * const table = new this.Table("table1");
     * const data = [
     *  {
     *    "subject": "标题一",
     *    ... //其他字段
     *  },
     *  ...
     * ];
     * table.insertRow( data, function(data){
     *    //data 形如
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": true //true表示插入成功
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     */
    this.insertRow = function(data, success, failure){
        return this.action.rowInsert(this.name, data, success, failure);
    };
    /**
     * 往数据表中插入单条数据。
     * @method addRow
     * @methodOf module:Table
     * @instance
     * @param {Object} data 需要插入的数据。
     * @param {Function} [success] 调用成功时的回调函数。
     * @param {Function} [failure] 调用错误时的回调函数。
     * @o2syntax
     * table.addRow( data, success, failure, async )
     * @example
     * const table = new this.Table("table1");
     * const data = {
     *    "subject": "标题一",
     *    ... //其他字段
     *  };
     * table.addRow( data, function(data){
     *    //data 形如
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "id": 2cf3a20d-b166-490b-8d29-05544db3d79b //true表示修改成功
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     */
    this.addRow = function(data, success, failure){
        return this.action.rowInsertOne(this.name, data, success, failure);
    };
    /**
     * 往数据表中修改单条数据。
     * @method updateRow
     * @methodOf module:server.Table
     * @instance
     * @param {String} id 需要修改的数据id。
     * @param {Object} data 需要修改的数据。
     * @param {Function} [success] 调用成功时的回调函数。
     * @param {Function} [failure] 调用错误时的回调函数。
     * @o2syntax
     * table.updateRow( id, data, success, failure )
     * @example
     * const table = new this.Table("table1");
     * const data = {
     *    "id" : "2cf3a20d-b166-490b-8d29-05544db3d79b",
     *    "subject": "标题一",
     *    ... //其他字段
     *  };
     * table.updateRow( "2cf3a20d-b166-490b-8d29-05544db3d79b", data, function(data){
     *    //data 形如
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": true //true表示修改成功
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr 为 xmlHttpRequest
     * });
     */
    this.updateRow = function(id, data, success, failure){
        return this.action.rowUpdate(this.name, id, data, success, failure);
    };
}

/**
 * 您可以通过statement对象，获取执行查询语句或者对查询结果进行选择。<br/>
 * @module server.statement
 * @o2cn 查询视图执行
 * @o2category server.common
 * @o2ordernumber 190
 * @o2syntax
 * //您可以在流程表单、内容管理表单、门户页面或视图中，通过this来获取statement对象，如下：
 * const statement = this.statement;
 */
const statement = {
    _parseParameter: function(obj, format){
        if(typeof obj !== 'object')return {};
        const parameter = {};
        //传入的参数
        for (const [key, value] of Object.entries(obj)) {
            parameter[key] = (typeOf(value) === 'date') ? (['sql', 'sqlScript'].includes(format) ? value.format('db') : `{ts '${value.format('db')}'}`) : value;
        }
        return parameter;
    },
    _parseFilter: function(filter=[], parameter={}, format){
        return filter.map((d)=>{
            let pName = d.path.replace(/\./g, '_');

            pName = (parameter[pName]) ?
                (function unique(name, i){
                    return (parameter[name+'_'+i]) ? unique(name, ++i) : name+'_'+i;
                })(pName, 1) :
                pName;

            parameter[pName] = (d.comparison === 'like' || d.comparison === 'notLike') ?
                `%${d.value.replace(/^%|%$/g, '')}%` :
                (() => {
                    if (d.formatType === "numberValue") {
                        return parseFloat(d.value);
                    } else if ( ["dateTimeValue","dateValue","timeValue"].includes( d.formatType ) && !["sql", "sqlScript"].includes(format)) {
                        const prefix = {dateTimeValue: 'ts', datetimeValue: 'ts', dateValue: 'd', timeValue: 't'}[d.formatType];
                        return `{${prefix} '${d.value}'}`;
                    }
                    return d.value;
                })();

            d.value = pName;

            return d;
        });
    },
    _execute: function (statement, callback, format) {
        const parameter = this._parseParameter(statement.parameter, format);
        const filterList = this._parseFilter(statement.filter, parameter, format);
        const obj = {
            "filterList": filterList,
            "parameter" : parameter
        };

        return Actions.load("x_query_assemble_surface").StatementAction.executeV2(
            statement.name, statement.mode || "data", statement.page || 1, statement.pageSize || 20, obj,
            callback
        );
    },
    _needCheckFormat: function(s){
        if (s.format) return false;

        if (typeof s.parameter === "object"){
            if (Object.values(s.parameter).findIndex(p=>typeOf(p)==='date')!==-1){
                return true;
            }
        }
        if( Array.isArray(s.filter)){
            if (s.filter.findIndex(f=>["dateTimeValue", "datetimeValue", "dateValue", "timeValue"].includes(f))!==-1){
                return true;
            }
        }
        return false;
    },
    /**
     * 执行指定的查询语句。
     * @method execute
     * @static
     * @param {Object} statement - 要执行的查询语句的信息。数据格式如下：
     * <div>以下的filter参数参考<a href='global.html#StatementFilter'>StatementFilter</a>,
     * parameter参数参考<a href='global.html#StatementParameter'>StatementParameter</a></div>
     * <pre><code class='language-js'>
     * {
     *  "name" : "tesStatement", //（String）必选，查询配置的名称、别名或ID
     *  "mode" : "all", //（String）必选，“all”、“data”或者“count”，all表示同时执行查询语句和总数语句，data表示执行查询语句，count表示执行总数语句
     *  "page" : 1, //（number）可选，当前页码，默认为1
     *  "pageSize" : 20, //（number）可选，每页的数据条数，默认为20
     *  "filter": [ //（Array）可选，对查询进行过滤的条件。json数组格式，每个数组元素描述一个过滤条件，每个元素数据格式如下：
     *       {
     *           "path":"o.title",  //查询语句格式为jpql使用o.title，为原生sql中使用xtitle
     *           "comparison":"like",
     *           "value":"关于",
     *           "formatType":"textValue"
     *       }
     *  ],
     *  parameter : {
     *       "person" : "", //参数名称为下列值时，后台默认赋值，person(当前人),identityList(当前人身份列表),unitList(当前人所在直接组织), unitAllList(当前人所在所有组织), groupList(当前人所在群组)
     *       "startTime" : (new Date("2020-01-01")), //如果对比的是日期，需要传入 Date 类型
     *       "applicationName" : "%test%", //如果运算符用的是 like, noLike，模糊查询
     *       "processName" : "test流程", //其他写确定的值
     *       "?1": "关于" //v8.0后查询语句支持问号加数字的传参
     *     }
     * }
     * </code></pre>
     * @param {Function} callback - 访问成功后的回调函数
     * @o2syntax
     * this.statement.execute(statement, callback, async);
     * @example
     * //获取“task”查询中的数据
     * //查询语句为 select o from Task o where (o.person = :person) and (o.startTime > :startTime) and (o.applicationName like :applicationName) and (o.processName = :processName)
     * //总数语句为 select count(o.id) from Task o where (o.person = :person) and (o.startTime > :startTime) and (o.applicationName like :applicationName) and (o.processName = :processName)
     * //过滤条件为标题o.title包含包含（like））“7月”。
     * this.statement.execute({
     *  "name": "task",
     *  "mode" : "all",
     *  "filter": [
     *      {
     *      "path":"o.title", //查询语句格式为jpql使用o.title，为原生sql中使用xtitle
     *      "comparison":"like",
     *      "value":"7月",
     *      "formatType":"textValue"
     *      }
     * ],
     * "parameter" : {
     *     "person" : "", //参数名称为下列值时，后台默认赋值，person(当前人),identityList(当前人身份列表),unitList(当前人所在直接组织), unitAllList(当前人所在所有组织), groupList(当前人所在群组)
     *     "startTime" : (new Date("2020-01-01")), //如果对比的是日期，需要传入 Date 类型
     *     "applicationName" : "%test%", //如果运算符用的是 like, noLike，模糊查询
     *     "processName" : "test流程", //其他写确定的值
     *     "?1": "关于" //v8.0后查询语句支持问号加数字的传参
     *   }
     * }, function(json){
     *  const count = json.count; //总数语句执行后返回的数字
     *  const list = json.data; //查询语句后返回的数组
     *   //......
     * });
     */
    execute: function (statement, callback) {
        const jsonFormat = (!statement.format && this._needCheckFormat(statement)) ? Actions.load("x_query_assemble_surface").StatementAction.getFormat(statement.name).data : statement;
        return this._execute(statement, callback, (jsonFormat?.format || ""));
    },
    select: function () {}
};

/**
 * 您可以通过view对象，获取视图数据或选择视图数据。<br/>
 * @module server.view
 * @o2cn 视图执行
 * @o2category server.common
 * @o2ordernumber 195
 * @o2syntax
 * //您可以在流程表单、内容管理表单或门户页面中，通过this来获取view对象，如下：
 * const view = this.view;
 */
const view = {
    /**
     * 获取指定视图的数据。
     * @method lookup
     * @static
     * @param {Object} view - 要访问的视图信息。数据格式如下：<br/>
     * <caption>以下的filter参数参考<a href='global.html#ViewFilter'>ViewFilter</a></caption>
     * <pre><code class='language-js'>
     * {
     *  "view" : "testView", //（String）必选，视图的名称、别名或ID
     *  "application" : "test数据中心应用", //（String）必选，视图所在数据应用的名称、别名或ID
     *  "filter": [ //（Array of Object）可选，对视图进行过滤的条件。json数组格式，每个数组元素描述一个过滤条件。
     *       {
     *           "logic":"and",
     *           "path":"$work.title",
     *           "comparison":"like",
     *           "value":"7月",
     *           "formatType":"textValue"
     *       }
     *  ]
     * }
     * </code></pre>
     * @param {Function} callback - 访问成功后的回调函数
     * @param {Boolean} [async] - 同步或异步调用。true：异步；false：同步。默认为true。
     * @o2syntax
     * this.view.lookup(view, callback, async);
     * @example
     * //获取“财务管理”应用中“报销审批数据”视图中的数据
     * //过滤条件为标题（$work.title）包含包含（like））“7月”。
     * this.view.lookup({
     *   "view": "报销审批数据",
     *   "application": "财务管理",
     *   "filter": [
     *       {
     *           "logic":"and",
     *           "path":"$work.title",
     *           "comparison":"like",
     *           "value":"7月",
     *           "formatType":"textValue"
     *       }
     *   ]
     *}, function(data){
     *   const grid = data.grid; //得到过滤后的数据
     *   const groupGrid = data.groupGrid; //如果有分类，得到带分类的数据
     *   //......
     *});
     * @example
     * //获取“财务管理”应用中“报销审批数据”视图中的数据
     * //过滤条件为标题（$work.title）包含包含（like））“7月”，并且总金额大于500小于5000
     * this.view.lookup({
     *   "view": "报销审批数据",
     *   "application": "财务管理",
     *   "filter": [
     *       {
     *           "logic":"and",
     *           "path":"$work.title",
     *           "comparison":"like",
     *           "value":"7月",
     *           "formatType":"textValue"
     *       },
     *       {
     *           "logic":"and",
     *           "path":"amount",
     *           "comparison":"range",
     *           "value":500,
     *           "otherValue":5000,
     *           "formatType":"numberValue"
     *       },
     *   ]
     *}, function(data){
     *   const grid = data.grid; //得到过滤后的数据
     *   const groupGrid = data.groupGrid; //如果有分类，得到带分类的数据
     *   //......
     *});
     */
    "lookup": function(view, callback){
        const filterList = {"filterList": (view.filter || null)};

        const json = Actions.load("x_query_assemble_surface").ViewAction.executeWithQuery(view.view, view.application, filterList);

        const data = {
            "grid": json.data.grid || json.data.groupGrid,
            "groupGrid": json.data.groupGrid
        };
        if (callback) callback(data);

        return data;
    },
    "select": function(view, callback, options){}
};

/**
 * 可以通过service对象发起restful请求，或soap协议的webservice调用。
 * @module service
 * @o2cn 通用service调用
 * @o2category server.common
 * @o2syntax
 * const service = this.service;
 * @example
 * //通过get方法发起restful请求，获取json数据
 * const res = this.service.restful("get", "config/myjson.json");
 * if (res.responseCode>=200 && responseCode<300){
 *     const jsonData = res.json;
 * }
 */
const service = {
    /**
     * 发起restful请求。
     * @method restful
     * @o2category server.common
     * @param {String} [method] - restful请求方法：get、post、put、delete ...
     * @param {String} [url] - restful请求地址
     * @param {Object} [headers] - 可选，json对象，请求的header，默认content-type为：“application/json charset=utf-8”
     * @param {String|Object} [body] - 可选，post、put请求的消息体,传入文本或json对象
     * @param {Number} [connectTimeout] - 可选，连接超时时间（毫秒），默认是2000。
     * @param {Number} [readTimeout] - 可选，传输超时时间（毫秒），默认是300000。
     * @return {Object} 返回json格式的请求结果对象，格式如下：
     * <pre><code class='language-js'>
     * {
     *  "responseCode" : 200,   //请求返回的code
     *  "headers" : {},         //响应头
     *  "body": "",             //响应的body文本内容
     *  "json": {}              //响应的body的json格式内容
     * }
     * </code></pre>
     * @o2syntax
     * var res = this.service.restful(method, url, headers, body, connectTimeout, readTimeout);
     * @example
     * //通过get方法发起restful请求，获取json数据
     * var res = this.service.restful("get", "config/myjson.json");
     * if (res.responseCode>=200 && res.responseCode<300){
     *     var jsonData = res.json;
     * }
     */
    restful: function(method, url, headers, body, connectTimeout=2000, readTimeout=300000){
        const service = globalThis.java_resources.getWebservicesClient();
        const bodyData = ((typeof body)==="object") ? JSON.stringify(body) : (body||"");
        const res = service.restful(method, url, (headers||null), bodyData, (connectTimeout||2000), (readTimeout||300000));

        const o = {
            "responseCode" : res.getResponseCode(),
            "headers" : res.getHeaders(),
            "body": res.getBody()
        }
        try {
            o.json = JSON.parse(o.body);
        }catch(e){}
        return o;
    },

    /**
     * 通过get方法发起restful请求。
     * @method get
     * @methodOf restful
     * @static
     * @param {String} [url] - restful请求地址
     * @param {Object} [headers] - 可选，json对象，请求的header，默认content-type为：“application/json charset=utf-8”
     * @param {Number} [connectTimeout] - 可选，连接超时时间（毫秒），默认是2000。
     * @param {Number} [readTimeout] - 可选，传输超时时间（毫秒），默认是300000。
     * @return {Object} 返回json格式的请求结果对象，格式如下：
     * <pre><code class='language-js'>
     * {
     *  "responseCode" : 200,   //请求返回的code
     *  "headers" : {},         //响应头
     *  "body": "",             //响应的body文本内容
     *  "json": {}              //响应的body的json格式内容
     * }
     * </code></pre>
     * @o2syntax
     * var res = this.service.get(url, headers, connectTimeout, readTimeout);
     */
    "get": function(url, headers, connectTimeout, readTimeout){
        return this.restful("get", url, headers, "", connectTimeout, readTimeout);
    },

    /**
     * 通过post方法发起restful请求。
     * @method post
     * @static
     * @param {String} [url] - restful请求地址
     * @param {Object} [headers] - 可选，json对象，请求的header，默认content-type为：“application/json charset=utf-8”
     * @param {String|Object} [body] - 可选，post、put请求的消息体,传入文本或json对象
     * @param {Number} [connectTimeout] - 可选，连接超时时间（毫秒），默认是2000。
     * @param {Number} [readTimeout] - 可选，传输超时时间（毫秒），默认是300000。
     * @return {Object} 返回json格式的请求结果对象，格式如下：
     * <pre><code class='language-js'>
     * {
     *  "responseCode" : 200,   //请求返回的code
     *  "headers" : {},         //响应头
     *  "body": "",             //响应的body文本内容
     *  "json": {}              //响应的body的json格式内容
     * }
     * </code></pre>
     * @o2syntax
     * var res = this.service.post(url, headers, body, connectTimeout, readTimeout);
     */
    "post": function(url, headers, body, connectTimeout, readTimeout){
        return this.restful("post", url, headers, body, connectTimeout, readTimeout);
    },

    /**
     * 发起soap协议的webservice请求。
     * @method soap
     * @o2category server.common
     * @param {String} [wsdl] - wsdl文件地址
     * @param {String} [method] - 要调用的方法名称
     * @param {Array} [pars] - 方法所需要的参数
     * @return {Object} 与服务返回的类型有关：
     * @o2syntax
     * var res = this.service.soap(wsdl, method, pars);
     * @example
     * //模拟通过webservice获取用户
     * var res = this.service.soap("wsdl/mywsdl.wsdl", "getPerson", ["张三", "李四"]);
     */
    soap: function(wsdl, method, pars){
        const service = globalThis.java_resources.getWebservicesClient();
        return service.soap(wsdl, method, pars);
    },
    soapXml: function(wsdl, xml){
        const service = globalThis.java_resources.getWebservicesClient();
        return service.jaxwsXml(wsdl, xml);
    }
}

//=========================================================================================/
//====以上为公共对象和方法，在服务端脚本中通用================================================/
//=========================================================================================/

/*****************************************************************/
/* response - 服务调用活动，响应脚本，获取响应内容 ******************/
/*            服务管理 invoke，用于设置响应内容  *******************/
/*    - 接口响应对象，服务端注入对象 java_customResponse  **********/
/*    - java_jaxrsResponse, type()  ******************************/
/*      java_jaxrsResponse, get() 字符串  ************************/
/*      java_jaxwsResponse  直接返回不做处理  *********************/
/*****************************************************************/
const responseDescriptor = {
    get: function(){
        if (globalThis.java_customResponse){   //注入 java_customResponse 对象，表示脚本运行于 服务管理 invoke 中
            /**
             * 用于服务管理的接口脚本，描述服务的响应对象。<br>
             * @o2range 服务管理-接口
             * @o2cn 服务的响应对象
             * @module server.service.response
             * @o2category server.service
             * @o2ordernumber 245
             * @o2syntax
             * const res = this.response;
             */
            return {
                /**
                 * @summary 服务返回一个303跳转。
                 * @method seeOther
                 * @methodOf service.service.module:response
                 * @static
                 * @param {String} [url] 跳转的url。
                 * @o2syntax
                 * this.response.seeOther(url);
                 */
                seeOther: function(url){
                    globalThis.java_customResponse.seeOther(url);
                },
                /**
                 * @summary 服务返回一个301跳转。
                 * @method redirect
                 * @methodOf service.service.module:response
                 * @static
                 * @param {String} [url] 跳转的url。
                 * @o2syntax
                 * this.response.redirect(url);
                 */
                redirect: function(url){
                    globalThis.java_customResponse.temporaryRedirect(url);
                },
                /**
                 * @summary 服务正常返回数据（200）。
                 * @method setBody
                 * @methodOf service.service.module:response
                 * @static
                 * @param {String|Object} [body] 响应内容，文本或json对象。
                 * @param {String} [contentType] 响应头的Content-Type。
                 * @o2syntax
                 * this.response.setBody(body, contentType);
                 * @example
                 * //设置json格式的响应内容
                 * this.response.setBody({
                 *     "key1": "value1",
                 *     "key2": "value2"
                 * }, "application/json");
                 */
                setBody: function(body, contentType=''){
                    const o = (typeof body==='object') ? JSON.stringify(body) : body;
                    globalThis.java_customResponse.setBody(o, contentType);
                },
                customResponse: globalThis.java_customResponse
            }
        }
        if (globalThis.java_jaxrsResponse) {   //注入 java_jaxrsResponse 对象，表示脚本运行于 服务调用活动，响应脚本 中，并且调用类型为 jaxrs
            /**
             * 用于流程配置的服务调用活动中的“响应脚本”，描述调用服务后得到的响应。(注意：调用方式为异步时，“响应脚本”不会执行)<br>
             * @o2range 流程配置-服务调用活动中的“响应脚本”
             * @module server.response
             * @o2cn 服务调用的响应
             * @o2category server.process
             * @o2ordernumber 230
             * @o2syntax
             * const res = this.response;
             */
            return {
                /**
                 * @summary jaxrs方式的服务调用活动，请求返回的状态。有四个可能的值：
                 * <pre><code class="language-js">success : 表示请求成功
                 * warn : 当调用的是平台内部服务时，虽然请求成功，但产生了警告
                 * error : 请求调用错误
                 * connectFatal : 网络连接错误</code></pre>
                 * @member {String} status
                 * @memberOf server.module:response
                 * @o2syntax
                 * const status = this.response.status;
                 */
                get status(){
                    return globalThis.java_jaxrsResponse.type() || '';
                },
                /**
                 * @summary 请求返回的值，如果是jaxrs服务，得到的是响应的文本，如果是jaxws服务，响应类型取决于服务的返回。
                 * @member {String|Any} value
                 * @memberOf server.module:response
                 * @o2syntax
                 * const value = this.response.value;
                 * @example
                 * //处理json对象的返回信息
                 * const value = this.response.value;
                 * const resData = JSON.parse(value);
                 */
                get value(){
                    return globalThis.java_jaxrsResponse.get();
                },
                /**
                 * @summary 获取请求返回的值，如果是jaxrs服务，会尽可能将响应数据转换为json对象，如果不能解析为json对象的，则返回文本，如果是jaxws服务，响应类型取决于服务的返回。
                 * @method get
                 * @methodOf server.module:response
                 * @static
                 * @return {Object|String|Any} 响应的内容.
                 * @o2syntax
                 * const res = this.response.get();
                 */
                get(){
                    const value = globalThis.java_jaxrsResponse.get();
                    if (JSON.validate(value)){
                        return JSON.decode(value);
                    }
                    return value;
                }
            }
        }
        if (globalThis.java_jaxwsResponse) {   //注入 java_jaxwsResponse 对象，表示脚本运行于 服务调用活动，响应脚本 中，并且调用类型为 jaxws
            return {
                value: globalThis.java_jaxwsResponse,
                get(){
                    return globalThis.java_jaxwsResponse || null;
                }
            }
        }
        return null;
    }
}

/***************************************************************/
/* workContext -流程脚本中的 workcontext 对象，用于获取流程实例的相关数据  *****/
/**************************************************************/
/**
 * 您可以在流程事件、流程路由事件、流程活动事件中通过workContext获取和流程相关的流程实例对象数据。
 * @module server.workContext
 * @o2cn 流程实例
 * @o2category server.process
 * @o2range {Process} 所有流程配置中的脚本可以使用此对象
 * @o2ordernumber 200
 * @o2syntax
 * //您可以在表单或流程的各个嵌入脚本中，通过this来获取当前流程实例数据，如下：
 * var context = this.workContext;
 */
const workContext = {
    /**
     * 当前流程实例正在流转中，并且当前用户有待办，则返回当前用户的待办对象。
     * 事实上在后端的活动事件中，大部分的情况下，此方法获取到的是taskCompleted对象，因为在人工活动的事件中，除了“待办处理前”事件，其他事件处理时，当前用户的待办都已经转为已办了。
     * @summary 获取当前流程与当前用户相关的待办或已办对象：task对象或taskCompleted对象。
     * @methodOf module:server.workContext
     * @o2ActionOut x_processplatform_assemble_surface.TaskAction.get|example=Task|Task对象:
     * @method getTask
     * @static
     * @return {(Task|TaskCompleted|Null)} 当前用户的待办任务对象：task。当前用户没有对此流程实例的待办时，或流程实例已经流转结束，返回null。
     * @o2syntax
     * const task = this.workContext.getTask();
     */
    getTask: function(){  //根据work获取当前处理的的task或者TaskCompleted  json字符串 只能在流转的过程中获取到
        var taskString = globalThis.java_workContext.getTaskOrTaskCompleted();
        var task = (taskString) ? JSON.parse(taskString) : null;
        if (task){
            task.personDn = task.person || "";
            task.unitDn = task.unit || "";
            task.unitDnList = task.unitList || "";
            task.identityDn = task.identity || "";
            task.creatorPersonDn = task.creatorPerson ||"";
            task.creatorUnitDn = task.creatorUnit ||"";
            task.creatorUnitDnList = task.creatorUnitList ||"";
            task.creatorIdentityDn = task.creatorIdentity ||"";
        }
        return task;
    },
    /**
     * 获取当前流程实例对象：work对象或workCompleted对象。
     * @method getWork
     * @methodOf module:server.workContext
     * @static
     * @return {(Work|WorkCompleted)} 流程实例对象；如果流程已结束，返回已结束的流程实例对象。
     * @o2ActionOut x_processplatform_assemble_surface.WorkAction.manageGet|example=Work|ignoreNoDescr=true|ignoreProps=[properties,manualTaskIdentityMatrix]|Work对象:
     * @o2ActionOut x_processplatform_assemble_surface.WorkCompletedAction.get|example=WorkCompleted|ignoreProps=[properties,data,taskCompletedList,readCompletedList,reviewList,recordList,workLogList,storeForm,mobileStoreForm]|WorkCompleted对象:
     * @o2syntax
     * const work = this.workContext.getWork();
     */
    "getWork": function(){  //work的字符串
        var work = JSON.parse(globalThis.java_workContext.getWork());
        work.creatorPersonDn = work.creatorPerson ||"";
        work.creatorUnitDn = work.creatorUnit ||"";
        work.creatorUnitDnList = work.creatorUnitList ||"";
        work.creatorIdentityDn = work.creatorIdentity ||"";
        return work;
    },

    /**
     * 获取当前流程实例的所有待办对象。如果流程实例已流转完成，则返回一个空数组。
     * @method getTaskList
     * @methodOf module:server.workContext
     * @o2ActionOut x_processplatform_assemble_surface.TaskAction.listWithWork|example=Task
     * @static
     * @return {(Task[])} 待办任务列表.
     * @o2syntax
     * const taskList = this.workContext.getTaskList();
     */
    "getTaskList": function(){return JSON.parse(globalThis.java_workContext.getTaskList());},                       //根据work获取所有的task数组  json字符串

    /**
     * 获取当前流程实例的所有已办对象。如果流程实例没有任何人处理过，则返回一个空数组。
     * @method getTaskCompletedList
     * @methodOf module:server.workContext
     * @static
     * @return {(TaskCompleted[])} 已办任务列表.
     * @o2ActionOut x_processplatform_assemble_surface.TaskCompletedAction.listWithWork|example=Task
     * @o2syntax
     * const taskCompletedList = this.workContext.getTaskCompletedList();
     */
    "getTaskCompletedList": function(){return JSON.parse(globalThis.java_workContext.getTaskCompletedList());},     //根据work获取所有的TaskCompleted数组  json字符串

    /**
     * @summary 获取当前流程实例的所有待阅对象数组。如果流程实例无待阅，则返回一个空数组。
     * @method getReadList
     * @methodOf module:server.workContext
     * @static
     * @return {(Read[])} 当前流程实例的所有待阅对象数组.
     * @o2ActionOut x_processplatform_assemble_surface.ReadAction.get|example=Read
     * @o2syntax
     * const readList = this.workContext.getReadList();
     */
    "getReadList": function(){return JSON.parse(globalThis.java_workContext.getReadList());},                       //根据work获取所有的read数组  json字符串

    /**
     * @summary 获取当前流程实例的所有已阅对象。如果流程实例没有已阅，则返回一个空数组。
     * @method getReadCompletedList
     * @methodOf module:server.workContext
     * @static
     * @return {(ReadCompleted[])} 当前流程实例的所有已阅对象数组.
     * @o2ActionOut x_processplatform_assemble_surface.ReadCompletedAction.listWithWork|example=Read
     * @o2syntax
     * const readCompletedList = this.workContext.getReadCompletedList();
     */
    "getReadCompletedList": function(){return JSON.parse(globalThis.java_workContext.getReadCompletedList());},     //根据work获取所有的ReadCompleted数组  json字符串

    /**
     * @summary 获取当前流程实例的所有review对象。如果流程实例没有review，则返回一个空数组。
     * @method getReviewList
     * @alias getReviewListByJob
     * @methodOf module:server.workContext
     * @static
     * @return {(Review[])} 当前流程实例的所有review对象数组.
     * @o2ActionOut x_processplatform_assemble_surface.ReviewAction.listWithJob|example=Review
     * @o2syntax
     * const reviewList = this.workContext.getReviewList();
     */
    "getReviewList": function(){return JSON.parse(globalThis.java_workContext.getJobReviewList());},

    /**
     * @summary getTaskListByJob方法的别名。
     * @method getJobTaskList
     * @static
     * @see server.module:workContext.getTaskListByJob
     */
    "getJobTaskList": function(){return JSON.parse(globalThis.java_workContext.getJobTaskList());},                 //根据jobid获取所有的task数组  json字符串

    /**
     * @summary getTaskCompletedListByJob方法的别名。
     * @method getJobTaskCompletedList
     * @static
     * @see server.module:workContext.getTaskCompletedListByJob
     */
    "getJobTaskCompletedList": function(){return JSON.parse(globalThis.java_workContext.getJobTaskCompletedList());},   //根据jobid获取所有的TaskCompleted数组  json字符串
    /**
     * @summary getReadListByJob方法的别名。
     * @method getJobReadList
     * @static
     * @see server.module:workContext.getReadListByJob
     */
    "getJobReadList": function(){return JSON.parse(globalThis.java_workContext.getJobReadList());},                     //根据jobid获取所有的read数组  json字符串
    /**
     * @summary getReadCompletedListByJob方法的别名。
     * @method getJobReadCompletedList
     * @static
     * @see server.module:workContext.getReadCompletedListByJob
     */
    "getJobReadCompletedList": function(){return JSON.parse(globalThis.java_workContext.getJobReadCompletedList());},   //根据jobid获取所有的ReadCompleted数组  json字符串
    /**
     * @summary getReviewList方法的别名。
     * @method getJobReviewList
     * @static
     * @see server.module:workContext.getReviewList
     */
    "getJobReviewList": function (){return this.getReviewList();},                                                                        //根据jobid获取所有的Review数组  json字符串

    /**
     * 根据当前工作的job获取当前流程实例的所有待办对象。如果流程实例已流转完成，则返回一个空数组。
     * @method getTaskListByJob
     * @methodOf module:server.workContext
     * @o2ActionOut x_processplatform_assemble_surface.TaskAction.listWithJob|example=Task
     * @static
     * @return {(Task[])} 待办任务列表.
     * @o2syntax
     * const taskList = this.workContext.getTaskListByJob();
     */
    "getTaskListByJob": function(){return this.getJobTaskList();},
    /**
     * 根据当前工作的job获取当前流程实例的所有已办对象。如果流程实例没有任何人处理过，则返回一个空数组。
     * @method getTaskCompletedListByJob
     * @methodOf module:server.workContext
     * @static
     * @return {(TaskCompleted[])} 已办任务列表.
     * @o2ActionOut x_processplatform_assemble_surface.TaskCompletedAction.listWithJob|example=Task
     * @o2syntax
     * const taskCompletedList = this.workContext.getTaskCompletedListByJob();
     */
    "getTaskCompletedListByJob": function(){return this.getJobTaskCompletedList();},
    /**
     * @summary 根据当前工作的job获取当前流程实例的所有待阅对象。如果流程实例无待阅，则返回一个空数组。
     * @method getReadListByJob
     * @methodOf module:server.workContext
     * @static
     * @return {(Read[])} 当前流程实例的所有待阅对象数组.
     * @o2ActionOut x_processplatform_assemble_surface.ReadAction.listWithJob|example=Read
     * @o2syntax
     * const readList = this.workContext.getReadListByJob();
     */
    "getReadListByJob": function(){return this.getJobReadList();},
    /**
     * @summary 根据当前工作的job获取当前流程实例的所有已阅对象。如果流程实例没有已阅，则返回一个空数组。
     * @method getReadCompletedListByJob
     * @methodOf module:server.workContext
     * @static
     * @return {(ReadCompleted[])} 当前流程实例的所有已阅对象数组.
     * @o2ActionOut x_processplatform_assemble_surface.ReadCompletedAction.listWithJob|example=Read
     * @o2syntax
     * const readCompletedList = this.workContext.getReadCompletedListByJob();
     */
    "getReadCompletedListByJob": function(){return this.getJobReadCompletedList();},
    /**
     * @summary getReviewList方法的别名。
     * @method getReviewListByJob
     * @static
     * @see server.module:workContext.getReviewList
     */
    "getReviewListByJob": function(){return this.getJobReviewList();},

    /**
     * 获取当前流程实例所在的活动节点对象：activity对象。
     * @method getActivity
     * @static
     * @return {(Activity|Null)} 当前流程实例所在的活动节点对象，如果当前流程实例已流转完成，则返回null.
     * <pre><code class='language-js'>{
     *      "id": "801087c5-a4e6-4b91-bf4d-a81cdaa04471", //节点ID
     *      "name": "办理",  //节点名称
     *      "description": "", //节点描述
     *      "alias": "",  //节点别名
     *      "resetRange": "department", //重置处理人范围
     *      "resetCount": 0,  //重置处理人数字
     *      "allowReset": true, //是否允许重置
     *      "manualMode": "single", //处理方式 单人single, 并行parallel, 串行queue, grab抢办
     *      "customData": { //节点上的自定义属性，如果没有设置，不输出该值
     *
     *      }
     * }</code></pre>
     * @o2syntax
     * const activity = this.workContext.getActivity();
     */
    "getActivity": function(){return JSON.parse(globalThis.java_workContext.getActivity());},       //活动对象字符串

    /**
     * @summary 获取当前流程实例的所有流程记录(WorkLog)。
     * @method getWorkLogList
     * @static
     * @return {WorkLog[]} 流程记录对象.
     * @o2ActionOut x_processplatform_assemble_surface.WorkLogAction.listWithJob|example=WorkLog|ignoreProps=[properties,goBackFromActivityType]
     * @o2syntax
     * const workLogList = this.workContext.getWorkLogList();
     */
    "getWorkLogList": function(){return JSON.parse(globalThis.java_workContext.getWorkLogList());}, //WorkLogList对象数组的字符串

    /**
     * @summary 获取当前流程实例的所有流程记录(Record)。
     * @method getRecordList
     * @o2ActionOut x_processplatform_assemble_surface.RecordAction.listWithJob|example=Record
     * @static
     * @return {Record[]} 流程记录(Record)对象.
     * @o2syntax
     * const recordList = this.workContext.getRecordList();
     */
    "getRecordList": function(){return JSON.parse(globalThis.java_workContext.getRecordList());}, //RecordList对象数组的字符串，需要新增

    /**
     * @summary 最后一条Record对象，在活动流转完成事件中，获取本次流转的record；在其它事件中获取的是整个job的最后一条record，并不是本次流转的record。
     * @method getRecord
     * @o2ActionOut x_processplatform_assemble_surface.RecordAction.listWithJob|example=Record
     * @static
     * @return {Record[]} 流程记录(Record)对象.
     * @o2syntax
     * const record = this.workContext.getRecord();
     */
    "getRecord": function(){return JSON.parse(globalThis.java_workContext.getRecord());}, //最后一条Record对象，（在活动流转完成事件中，获取本次流转的record；在其它事件中获取的是整个job的最后一条record，并不是本次流转的record）

    /**
     * @summary 获取当前流程实例的附件对象列表。
     * @method getAttachmentList
     * @static
     * @return {WorkAttachmentData[]} 附件数据.
     * @o2ActionOut x_processplatform_assemble_surface.AttachmentAction.getWithWorkOrWorkCompleted|example=Attachment|ignoreProps=[properties]
     * @o2syntax
     * //获取附件列表
     * const attachmentList = this.workContext.getAttachmentList();
     */
    "getAttachmentList": function(){return JSON.parse(globalThis.java_workContext.getAttachmentList());},   //附件对象数组的字符串

    /**
     * @summary 获取可选路由对象数组的字符串（流转事件中，获取到流转中的可选路由列表，根据当前work状态获取）。
     * @method getRouteList
     * @static
     * @return {Route[]} 路由字符串数组.
     * @o2syntax
     * const routeList = this.workContext.getRouteList();
     */
    "getRouteList": function(){return JSON.parse(globalThis.java_workContext.getRouteList());},      //可选路由对象数组的字符串（流转事件中，获取到流转中的可选路由列表，根据当前work状态获取）

    /**
     * @summary 重新设置流程实例标题
     * @method setTitle
     * @static
     * @param {String} title - 标题字符串.
     * @o2syntax
     * this.workContext.setTitle(title);
     * @example
     * this.workContext.setTitle("标题");
     */
    "setTitle": function(title){globalThis.java_workContext.setTitle(title);},                       //设置title


    "getControl": function(){return null;},
    "getInquiredRouteList": function(){return null;}
};
const workContent = workContext;

/***************************************************************/
/* assignData - 流程调用脚本，数据脚本
{
	data:{},
	application:"",
	process:"",
	identity: "",
	attachmentList: [],
	title: "",
	processing: true
}
可通过return 返回assignData,  **********************************/
/**************************************************************/
/**
 * 用于流程配置的流程调用活动中的“数据脚本”和“标题脚本”，可以通过assignData对象获取要调用的流程的相关信息，以及要传递给被调用流程实例的业务数据。<br>
 * 也可以修改业务数据，并通过assignData的set方法，将业务数据传递到被调用的流程实例。<br/>
 * @o2range 流程配置-流程调用活动中的“数据脚本”中可用
 * @module server.assignData
 * @o2cn 流程实例业务数据
 * @o2category server.process
 * @o2ordernumber 210
 * @example
 * //在流程调用活动中的“数据脚本”，通过下面的代码修改业务数据，并传递给被调用流程的实例：
 * const data = this.assignData.get();
 * data.data.parentProcessData = "父流程实例的信息";
 * this.assignData.set(data);
 * @example
 * <caption>
 *    assignData.set方法是为了兼容以前的版本。<br>
 *    <b>建议通过return一个json对象的方式来设置data内容</b>
 * </caption>
 * //也可以通过return一个json对象的方式来代替assignData.set方法
 * const data = this.assignData.get();
 * data.data.parentProcessData = "父流程实例的信息";
 * return data;
 */
const assignData = {     //java_assignData 应用调用活动的创建的流程实例的业务数据处理对象，get set 方法
    _data: null,
    /**
     * @summary 获取要调用的流程的相关信息，以及要传递给被调用流程实例的业务数据。
     * @method get
     * @methodOf module:server.assignData
     * @static
     * @return {Object} 描述被调用的流程的信息，及要传递的业务数据.
     * <pre><code class='language-js'>{
     *        "application": "application id",  //被调用的应用id
     *        "process": "process id",          //被调用的流程id
     *        "identity": "xxx@xxx@I",          //被调用流程的启动这身份
     *        "title": "title",                 //被调用流程实例的标题
     *        "attachmentList": [],             //要传递到被调用的流程实例的附件对象
     *        "data": {},                       //要传递到被调用的流程实例的业务数据
     *        "attachmentSoftCopy"              //如果为true，不拷贝附件文件。默认false  @todo
     * }</code></pre>
     * @o2syntax
     * const data = this.assignData.get();
     */
    "get": function(){
        this.data = JSON.parse(globalThis.java_assignData.get());
        return this.data;
    },
    /**
     * @summary 设置修改后的assignData对象。（set方法为了兼容早期的版本。建议使用 return data; 方式直接返回json对象）
     * @method set
     * @methodOf module:server.assignData
     * @static
     * @param {Object} [data] 要设置的assignData对象，一般情况都是通过assignData.get()获取并做必要修改的对象。
     * @o2syntax
     * this.assignData.set(data);
     * @deprecated set方法已不建议使用了。建议return一个json对象或数组的方式来设置data。
     * @example
     * const data = this.assignData.get();
     * data.data.parentProcessData = "父流程实例的信息";
     * return data;
     */
    "set": function(data){
        globalThis.java_assignData.set(JSON.stringify(data || this.data));
    }
};
Object.defineProperties(assignData, {"data": {
        "get": function(){
            if (this._data) return this._data;
            return JSON.parse(globalThis.java_assignData.get());
        },
        "set": function(v){this._data = v;}
    }}
);


/***************************************************************/
/* parameters - 服务调用活动，参数脚本，要传入的参数 ******************/
/*              数据中心查询语句的脚本中，获取语句参数。***************/
/*   - 调用活动中，如果是webservice调用，则注入 java_jaxwsParameters 对象，parameters 返回一个对象，包含 add 和 remove方法;***************/
/*   - 调用活动中，如果是restfyl调用，则注入 java_jaxrsParameters 对象，parameters 返回一个对象，包含 put 和 remove方法;******************/
/*   - 数据中心查询语句的脚本中，则注入 java_parameters json文本，parameters 返回一个json对象，用于获取调用查询语句的参数******************/
/***************************************************************/
/**
 * 用于流程配置的服务调用活动中的“参数脚本”，为jaxrs和jaxws两种服务调用方式传递参数。<br>
 * 当调用类型为jaxrs时：可使用parameters.put和parameters.remove方法<br/>
 * 当调用类型为jaxws时：可使用parameters.add和parameters.remove方法<br/>
 * @o2range 流程配置-服务调用活动中的“参数脚本”中可用
 * @module server.parameters
 * @o2cn 服务调用参数
 * @o2category server.process
 * @o2ordernumber 215
 * @deprecated parameters对象已经不建议使用了。建议return一个json对象或数组的方式来设置参数。
 * @example
 * //使用jaxrs方式的服务调用活动的参数脚本中
 * //如果rest服务地址为：xxx/{id}/xx/{name},则需要传入两个参数：id和name，可使用如下代码：
 * this.parameters.put("id", "id value");
 * this.parameters.put("name", "name value");
 *
 * //或者
 * this.parameters.put({
 *     "id": "id value",
 *     "name": "name value"
 * });
 *
 * @example
 * //使用jaxws方式的服务调用活动的参数脚本中
 * //如果需要传入三个参数，可使用如下代码：
 * this.parameters.add("参数1");
 * this.parameters.add("参数2");
 * this.parameters.add("参数3");
 *
 * //或者
 * this.parameters.add(["参数1", "参数2", "参数3"]);
 *
 * @example
 * <caption>
 *    以上两个例子中使用了parameters对象来收集参数，这主要是为了兼容以前的版本。<br>
 *    <b>我们更建议通过return一个json对象或数组的方式来设置参数</b>
 * </caption>
 * //以上两个例子中使用了parameters对象来收集参数
 * //更好的方式是：通过return一个json对象或数组的方式来设置参数
 * //对于jaxrs方式：
 * return {
 *     "id": "id value",
 *     "name": "name value"
 * }
 *
 * //对于jaxws方式：
 * return ["参数1", "参数2", "参数3"];
 */
/**
 * 用于数据中心查询语句的脚本中，可获取语句参数。json对象，在调用此语句的时候传入<br>
 * @o2range 数据中心-查询配置-通过脚本创建查询语句
 * @o2cn 数据中心查询语句参数
 * @module server.service.parameters
 * @o2category server.service
 * @o2ordernumber 255
 * @o2syntax
 * var pars = this.parameters;
 * @example
 * <caption>
 *     通过this.statement.execute调用一个名为“task”的查询配置，并传入parameters，代码如下：
 *     <pre><code class='language-js'>//用一个名为“task”的查询配置，并传入parameters
 * this.statement.execute({
 *  "name": "task",
 *  "mode" : "all",
 *  "parameter" : {
 *     "person" : "xxx@xxx@p",  //传入人员参数
 *     "startTime" : (new Date("2020-01-01")) //传入时间参数
 *  }
 * }, function(json){
 *  var count = json.count; //总数语句执行后返回的数字
 *  var list = json.data; //查询语句后返回的数组
 *   //......
 * });
 *     </code></pre>
 *
 *     在task查询配置的脚本中，parameters对象就可获取到执行查询时传入的parameters对象，以便于动态创建查询语句
 * </caption>
 * //动态生成查询指定用户，在指定时间之后的所有待办数据
 * var user = this.parameters.person;
 * var startTime = (new Date(this.parameters.startTime)).format("db");  //格式化为yyyy-mm-dd hh:mm:ss
 * return "SELECT o FROM Task o WHERE o.person='"+user+"' AND o.startTime>{ts '"+startTime+"'}"
 */
const parametersDescriptor = {
    get: function(){
        if (globalThis.java_jaxwsParameters){   //注入 java_jaxwsParameters 对象，表示脚本运行于 服务调用活动，参数脚本 中，并且调用类型为 jaxws
            return {
                /**
                 * @summary jaxws方式的服务调用活动，“参数脚本”中，使用parameters.add设置参数。
                 * @method add
                 * @methodOf module:server.parameters
                 * @static
                 * @param {Any|Array} [value] 要设置的参数值。
                 * @o2syntax
                 * this.parameters.add(value);
                 * @deprecated 不建议使用，建议return一个数组的方式来设置参数。如：
                 * <pre><code class='language-js'>return ["参数1", "参数2", "参数3"];</code></pre>
                 */
                add(value){
                    if (Array.isArray(value)){
                        value.forEach(function(v){
                            globalThis.java_jaxwsParameters.add(v);
                        });
                    }else{
                        globalThis.java_jaxwsParameters.add(value);
                    }
                },
                /**
                 * @summary 服务调用活动，“参数脚本”中，删除已设置参数。
                 * @method remove
                 * @methodOf module:server.parameters
                 * @static
                 * @param {String|Number} [name] jaxrs方式的服务调用活动，传入要删除参数的key；jaxws方式的服务调用活动，传入要删除的参数的索引。
                 * @o2syntax
                 * this.parameters.remove(name);
                 * @deprecated 不建议使用
                 */
                remove(name){
                    globalThis.java_jaxwsParameters.remove(name);
                }
            }
        }
        if (globalThis.java_jaxrsParameters){   //注入 java_jaxrsParameters 对象，表示脚本运行于 服务调用活动，参数脚本 中，并且调用类型为 jaxrs
            return {
                /**
                 * @summary jaxrs方式的服务调用活动，“参数脚本”中，使用parameters.put设置参数。
                 * @method put
                 * @methodOf module:server.parameters
                 * @static
                 * @param {String|Object} [name] 要设置的参数key，或参数json对象。
                 * @param {String|Number|boolean} [value] 要设置的参数值。
                 * @o2syntax
                 * this.parameters.put(name, value);
                 * this.parameters.put(obj);
                 * @deprecated 不建议使用，建议return一个json对象的方式来设置参数。如：
                 * <pre><code class='language-js'>return {
                 *     "id": "id value",
                 *     "name": "name value"
                 * };</code></pre>
                 */
                put(name, value){
                    if ((typeof name)==="object"){
                        var _keys = Object.keys(name);
                        for (var i=0; i<_keys.length; i++){
                           globalThis.java_jaxrsParameters.put(_keys[i], name[_keys[i]]);
                        }
                    }else{
                        globalThis.java_jaxrsParameters.put(name, value);
                    }
                },
                remove(name){
                    globalThis.java_jaxrsParameters.remove(name);
                }
            }
        }
        if (globalThis.java_parameters){        //注入 java_parameters 对象，表示脚本运行于 数据中心查询语句的脚本 中
            return JSON.parse(globalThis.java_parameters)
        }
        return null;
    }
}


/***************************************************************/
/* body - 服务调用活动，消息体脚本，设置调用消息体 ******************/
/***************************************************************/
/**
 * 用于流程配置的服务调用活动中的“消息体脚本”，仅在jaxrs方式的服务调用活动中可用。<br>
 * @o2range 流程配置-服务调用活动中的“消息体脚本”，服务协议为jaxrs
 * @module server.body
 * @o2cn 服务调用消息体
 * @o2category server.process
 * @o2ordernumber 220
 * @deprecated body对象已经不建议使用了。建议return一个json对象的方式来设置body。如：
 * <pre><code class='language-js'>return {
 *     "key1": "value1",
 *     "key2": "value2",
 *     "key3": "value3"
 * };</code></pre>
 * @example
 * //设置jaxrs服务调用的消息体
 * this.body.set({
 *     "key1": "value1",
 *     "key2": "value2",
 *     "key3": "value3"
 * })
 *
 * //或者设置
 * this.body.set("this is body data");
 *
 * @example
 * <caption>
 *    body.set方法是为了兼容以前的版本。<br>
 *    <b>建议通过return一个json对象的方式来设置消息体内容</b>
 * </caption>
 * return {
 *      "key1": "value1",
 *     "key2": "value2",
 *     "key3": "value3"
 * };
 *
 * //或
 * return "this is body data";
 */
const body = {
    /**
     * @summary 服务调用活动中的“消息体脚本”，用于设置消息体内容。
     * @method set
     * @methodOf module:server.body
     * @static
     * @param {String|Object} [data] 消息体内容。
     * @o2syntax
     * this.body.set(data);
     * @deprecated 不建议使用，建议return一个json对象或数组的方式来设置body。
     */
    set(data){
        if ((typeof data)==="string"){
            globalThis.java_jaxrsBody.set(data);
        }
        if ((typeof data)==="object"){
            globalThis.java_jaxrsBody.set(JSON.stringify(data));
        }
    }
};

/***************************************************************/
/* headers - 服务调用活动，消息头脚本，设置调用消息头 ******************/
/***************************************************************/
/**
 * 用于流程配置的服务调用活动中的“消息头脚本”，仅在jaxrs方式的服务调用活动中可用。<br>
 * @o2range 流程配置-服务调用活动中的“消息头脚本”，服务协议为jaxrs
 * @module server.headers
 * @o2cn 服务调用消息头
 * @o2category server.process
 * @o2ordernumber 225
 * @deprecated headers对象已经不建议使用了，建议return一个json对象的方式来设置headers。如：
 * <pre><code class='language-js'>return {
 *     "Content-Type": "application/x-www-form-urlencoded"，
 *     "Accept-Language": "en"
 * };</code></pre>
 * @example
 * //设置jaxrs服务调用的消息头
 * this.headers.put("Content-Type", "application/x-www-form-urlencoded");
 *
 * //或者
 * this.headers.put({
 *      "Content-Type": "application/x-www-form-urlencoded"，
 *      "Accept-Language": "en"
 * });
 *
 * @example
 * <caption>
 *    headers.put方法是为了兼容以前的版本。<br>
 *    <b>建议通过return一个json对象的方式来设置消息头内容</b>
 * </caption>
 * return {
 *      "Content-Type": "application/x-www-form-urlencoded"，
 *      "Accept-Language": "en"
 * };
 */
const headers = {
    /**
     * @summary jaxrs方式的服务调用活动，“消息头脚本”中，headers.put设置消息头。
     * @method add
     * @methodOf module:server.headers
     * @static
     * @param {String|Object} [name] 要设置的消息头名称，或消息头json对象。
     * @param {String} [value] 要设置的消息头值。
     * @o2syntax
     * this.headers.put(name, value);
     * this.headers.put(obj);
     * @deprecated 不建议使用，建议return一个json对象的方式来设置headers。如：
     * <pre><code class='language-js'>return {
     *     "Content-Type": "application/x-www-form-urlencoded"，
     *     "Accept-Language": "en"
     * };</code></pre>
     */
    put(name, value){
        try{
            if ((typeof name)==="object"){
                var _keys = Object.keys(name);
                for (var i=0; i<_keys.length; i++){
                    if (globalThis.java_jaxrsHeaders) globalThis.java_jaxrsHeaders.put(_keys[i], name[_keys[i]]);
                }
            }else{
                if (globalThis.java_jaxrsHeaders) globalThis.java_jaxrsHeaders.put(name, value);
            }
        }catch(e){}
    },
    /**
     * @summary jaxrs方式的服务调用活动，“消息头脚本”中，删除已经设置消息头。
     * @method remove
     * @methodOf module:server.headers
     * @static
     * @param {String} [name] 要删除的消息头名称。
     * @o2syntax
     * this.headers.remove(name);
     * @deprecated 不建议使用
     */
    remove(name){
        try{
            if (globalThis.java_jaxrsHeaders) globalThis.java_jaxrsHeaders.remove(name);
        }catch(e){}
    }
};

/***************************************************************/
/* requestText - 服务活动，服务响应脚本，获取请求的文本内容  ******/
/*               服务管理 invoke 请求消息体的文本内容 *     ******/
/***************************************************************/
/**
 * 调用接口时传入的请求消息体的文本内容。
 * @o2range 服务管理-接口
 * @module server.requestText
 * @o2cn 传入的服务消息体文本
 * @o2category server.service
 * @o2ordernumber 250
 * @o2syntax
 * const text = this.requestText;
 * @example
 * const text = this.requestText; //传入的服务消息体文本
 * const object = JSON.parse(text); //转成对象
 */
/**
 * 用于流程配置的服务活动中的“服务响应脚本”，获取请求的文本内容。<br>
 * @o2range 流程配置-服务活动中的“响应脚本”
 * @module server.requestText
 * @o2cn 服务调用请求对象
 * @o2category server.process
 * @o2ordernumber 235
 * @o2syntax
 * const text = this.requestText;
 * const object = JSON.parse(text); //转成对象
 */
const requestTextDescriptor = {
    get: function(){
        return globalThis.java_requestText || '';
    }
}

/***************************************************************/
/* request - 服务活动，服务响应脚本，获取请求内容  ****************/
/*           服务管理 invoke 请求的java对象 java_request *********/
/***************************************************************/
const requestDescriptor = {
    get: function(){
        if (globalThis.java_request){       //注入java_request对象，表示脚本运行在 服务管理 invoke 中。直接返回 java_request 对象。
            /**
             * 调用接口时传入的请求对象。java的 request（java.net.http.HttpRequest）对象。
             * @o2range 服务管理-接口
             * @module server.request
             * @o2cn 传入的服务消息体文本
             * @o2category server.service
             * @o2ordernumber 250
             * @o2syntax
             * const req = this.request;
             */
            globalThis.java_request.getBody = function(){
                try{
                    return JSON.parse(globalThis.java_requestText);
                }catch(e){
                    return globalThis.java_requestText;
                }
            }
            return globalThis.java_request;
        }
        if (globalThis.java_requestText) {  //注入java_requestText对象，表示脚本运行在 服务活动，服务响应脚本 中。
            /**
             * 用于流程配置的服务活动中的“服务响应脚本”，描述发起服务的请求对象。<br>
             * @o2range 流程配置-服务活动中的“响应脚本”
             * @module server.request
             * @o2cn 服务调用请求对象
             * @o2category server.process
             * @o2ordernumber 235
             * @o2syntax
             * const res = this.request;
             */
            return {
                /**
                 * @summary 获取请求的body内容。
                 * @method getBody
                 * @methodOf server.module:request
                 * @static
                 * @return {Object|String} 请求的body内容，如果能转换为json，则返回json对象，否则返回请求的内容的文本.
                 * @o2syntax
                 * const req = this.request.getBody();
                 */
                getBody(){
                    try{
                        return JSON.parse(globalThis.java_requestText);
                    }catch(e){
                        return globalThis.java_requestText;
                    }
                },
                /**
                 * @summary 获取请求的body的原始内容。
                 * @member {String} text
                 * @memberOf server.module:request
                 * @o2syntax
                 * var req = this.request.text;
                 */
                get text(){
                    return globalThis.java_requestText;
                }
            }
        }
        return null;
    }
}


/***************************************************************/
/* expire - 活动时效计算对象，用于设置活动时效 ********************/
/***************************************************************/
//活动时效计算对象，流程时效得去掉
//java_expire,
//脚本返回JSON数据:
//{
//    hour: 3                     //几小时后超时
//    workHour: 5             //几个工作小时后超时
//    date: '2016-08-01'   //到达指定时间后超时
//}
//设置指定的超时时间:
//this.expire.setDate([Date or String] date)
//设置几小时后超时:
//this.expire.setHour([Int] hour)
//设置几个工作小时后超时：
//this.expire.setWorkHour([Int] hour)
/**
 * 用于流程配置的人工活动的“时效脚本”中，用于设置超时时间。可以通过设置小时数，工作小时数，和指定时间点来设置超时，如果全部设置，则优先级为：工作小时>小时>时间<br>
 * @o2range 流程配置-人工活动中的“时效脚本”
 * @module server.expire
 * @o2cn 超时时间设置
 * @o2category server.process
 * @o2ordernumber 240
 * @deprecated expire对象已经不建议使用了。建议return一个json对象的方式来设置超时时间。
 * @example
 * //设置超时时限为待办产生后5小时
 * this.expire.setHour(5);
 *
 * //设置超时时限为待办产生后5个工作小时（只计算工作时间）
 * this.expire.setWorkHour(5);
 *
 * //设置超时时限为指定时间，如业务数据中的设定的办理期限（processingTime）
 * this.expire.setDate(this.data.processingTime);
 *
 * @example
 * <caption>
 *    expire对象是为了兼容以前的版本。<br>
 *    <b>建议可以直接返回一个json对象来设置超时时间</b>
 * </caption>
 * //设置超时时限为待办产生后5小时
 * return {"hour": 5};
 *
 * //设置超时时限为待办产生后5个工作小时（只计算工作时间）
 * return {"workHour": 5};
 *
 * //设置超时时限为指定时间，如业务数据中的设定的办理期限（processingTime）
 * return {"date": this.data.processingTime};
 */
const expire = {
    /**
     * @summary 设置超时小时数。
     * @method setHour
     * @methodOf server.module:expire
     * @static
     * @param {Number} [hour] 超时的小时数。
     * @deprecated 不建议使用，建议return一个json对象的方式来设置超时时间。如：
     * <pre><code class='language-js'>return {"hour": 5}</code></pre>
     */
    "setHour": function(hour){
        try{globalThis.java_expire.setHour(hour);}catch(e){}
    },
    /**
     * @summary 设置超时工作小时数。
     * @method setWorkHour
     * @methodOf server.module:expire
     * @static
     * @param {Number} [hour] 超时的工作小时数。
     * @deprecated 不建议使用，建议return一个json对象的方式来设置超时时间。如：
     * <pre><code class='language-js'>return {"workHour": 5}</code></pre>
     */
    "setWorkHour": function(hour){
        try{globalThis.java_expire.setWorkHour(hour);}catch(e){}
    },
    /**
     * @summary 设置超时时间。
     * @method setDate
     * @methodOf server.module:expire
     * @static
     * YYYY-MM-DD HH:mm:SS
     * @param {String} [date] 一个表示日期时间的字符串，按以下格式：
     * <pre><code class="language-js">yyyy-MM-dd HH:mm:ss   //如2021-09-12 18:26:51</code></pre>
     * @deprecated 不建议使用，建议return一个json对象的方式来设置超时时间。如：
     * <pre><code class='language-js'>return {"date": "2021-09-12 18:26:51"}</code></pre>
     */
    "setDate": function(date){
        try{globalThis.java_expire.setDate(date);}catch(e){}
    }
};



/******************************************/
/******************************************/
/*  data 和 embedData 处理 **************/
/******************************************/
/******************************************/
/**
 * 在流程调用活动中。当启用流程等待的情况下，在"子流程成功后"、"子流程取消后"、"子流程完成后"，三个事件脚本中，可以访问到embedData对象<br/>
 * embedData对象就是被调用的子流程的业务数据，它是一个类似JSON的对象，您可以用访问JSON对象的方法访问embedData对象的所有数据。<br/>
 * 如果您需要获取embedData的json文本，请使用embedData.toString()方法。<b>注意：JSON.stringify()方法不能用于embedData对象</b><br>
 * 您可以通过work对象的embedCompleted值来判断被调用的子流程是否正常完成。 cancel end terminate
 * <pre><code class='language-js'>
 *  var embedStatus = this.workContext.getWork().embedCompleted;
 *  if (embedStatus=="end"){
 *      //被调用的子流程正常流转到了结束活动
 *  }
 *  if (embedStatus=="cancel"){
 *      //被调用的子流程流转到了取消活动
 *  }
 *  if (embedStatus=="terminate"){
 *      //被调用的子流程被终止了
 *  }
 * </code></pre>
 * @o2range 流程配置-流程调用活动中，当启用流程等待的情况下，在"子流程成功后"、"子流程取消后"、"子流程完成后"，三个事件中可用
 * @module server.embedData
 * @o2cn 调用活动的子流程业务数据
 * @o2category server.process
 * @o2ordernumber 205
 * @example
 * //您可以在表单或流程的各个嵌入脚本中，通过this来获取当前实例的业务数据，如下：
 * var embedData = this.embedData;
 */

/**
 * 在流程事件、流程路由事件、流程活动事件中通过this.data获取流程实例的业务数据。（内容管理无后台脚本）。<br/>
 * 这些数据一般情况下是通过您创建的表单收集而来的，也可以通过脚本进行创建和增删改查操作。<br/>
 * data对象基本上是一个JSON对象，您可以用访问JSON对象的方法访问data对象的所有数据。
 * @module server.data
 * @o2cn 流程数据
 * @o2category server.process
 * @o2ordernumber 205
 * @example
 * //您可以在表单或流程的各个嵌入脚本中，通过this来获取当前实例的业务数据，如下：
 * var data = this.data;
 * @borrows module:data#[property] as [property]
 */

const ArrayList = Java.type('java.util.ArrayList');
const createProxy = function(target, j_data){
    target.$Jdata ?? Object.defineProperty(target, '$Jdata', {
        value: j_data
    });
    return new Proxy(target, {
        get(target, property){
            if (property==='add'){
                return function(key, value){
                    if (key==="length" && (target.$Jdata instanceof ArrayList)){
                        while (target.$Jdata.size()>value){
                            target.$Jdata.remove(target.$Jdata.size()-1);
                        }
                    }else{
                        target.$Jdata[key] = value;
                    }
                    return Reflect.set(target, key, value);
                }
            }
            if (property==='del'){
                return function (key) {
                    delete target.$Jdata[key];
                    delete target[key];
                }
            }
            return (property!=='$Jdata' && target[property] && (typeof target[property]==='object' || Array.isArray(target[property]))) ? createProxy(target[property], target.$Jdata[property]) : target[property];
        },
        set(target, property, value){
            if (property==="length" && (target.$Jdata instanceof ArrayList)){
                while (target.$Jdata.size()>value){
                    target.$Jdata.remove(target.$Jdata.size()-1);
                }
            }else{
                target.$Jdata[property] = value;
            }
            return Reflect.set(...arguments);
        }
    });
}

const _javaDataToJson = (javaData)=>{
    try{
        // const Gson = Java.type('com.google.gson.Gson');
        // const gson = new Gson();
        // return JSON.parse(gson.toJson(javaData));
        return JSON.parse(javaData.toString());
    }catch(e) {
        return null;
    }
}

let _data = null;
const dataDescriptor = {
    get(){
        return _data ?? (()=>{
            if (!globalThis.java_data) return null;
            const data = _javaDataToJson(globalThis.java_data);
            return _data = createProxy(data, globalThis.java_data);
        })();
    }
}

let _embedData = null;
const embedDataDescriptor = {
    get(){
        return _embedData ?? (()=>{
            if (!globalThis.java_embedData) return null;
            // const Gson = Java.type('com.google.gson.Gson');
            // const gson = new Gson();
            const data = _javaDataToJson(globalThis.java_embedData);
            return _embedData = createProxy(data, globalThis.java_embedData);
        })();
    }
}


/************************************************************/
/*  person text直接注入，oauth配置和默认生成口令脚本中使用 *****/
/************************************************************/

/************************************************************/
/*  value直接注入，视图显示值中使用 ***************************/
/************************************************************/

const _get = function(key, java_data){
    if (!globalThis[`o${key}`]) globalThis[`o${key}`] = JSON.parse(java_data);
    return globalThis[`o${key}`];
}
const o= {
    entityManager: { get: function(){return null;} },
    context: { get: function(){return ((globalThis.java_resources) ? globalThis.java_resources.getContext() : null)} },
    applications: { get: function(){return ((globalThis.java_resources) ? globalThis.java_resources.getApplications() : null)} },
    organization: { get: function(){return ((globalThis.java_resources) ? globalThis.java_resources.getOrganization() : null)} },
    /**
     * 获取当前用户对象。
     * @module server.currentPerson
     * @o2cn 当前用户全称对象
     * @o2category server.common
     * @o2ordernumber 250
     * @o2syntax
     * var user = this.currentPerson;
     */
    currentPerson: { get: function(){return (globalThis.java_effectivePerson) ? _get('currentPerson', globalThis.java_effectivePerson) : null;} },
    effectivePerson: { get: function(){return (globalThis.java_effectivePerson) ? _get('effectivePerson', globalThis.java_effectivePerson) : null;} },

    resources: { get: function(){return (globalThis.java_resources || null)} },
    customResponse: { get: function(){return (globalThis.java_customResponse || null)} },
    message: { get: function(){return (globalThis.java_message) ? _get('message', globalThis.java_message) : null;} },

    //===========================================================================
    //  将要导出的对象和方法绑定到 globalThis。（以后将通过 export 和 import 来引入）
    //===========================================================================
    fetch: { value: fetch },
    exec: { value: exec },
    define: { value: define },
    Action: { value: Action },
    log: { value: log },
    Actions: { value: Actions },
    print: { value: print },
    org: { value: org },
    processActions: { value: processActions },
    cmsActions: { value: cmsActions },
    portalActions: { value: portalActions },
    serviceActions: { value: serviceActions },
    includedScripts: { value: includedScripts },
    include: { value: include },
    Dict: { value: Dict },
    Table: { value: Table },
    statement: { value: statement },
    view: { value: view },
    service: { value: service },

    response: responseDescriptor,
    parameters: parametersDescriptor,
    requestText: requestTextDescriptor,
    request: requestDescriptor,

    workContext: { value: workContext },
    workContent: { value: workContext },
    assignData: { value: assignData },
    body: { value: body },
    headers: { value: headers },
    expire: { value: expire },

    // @todo 需要包装为javascript对象
    data: dataDescriptor,
    embedData: embedDataDescriptor
    // embedData: { value: globalThis.embedData }
};

Object.defineProperties(globalThis, o);

/* 清除 engine */
globalThis.engine = null;


//=== java_resources: =============
//所有服务段脚本中都有 java对象
//  Context                 getContext()                    应用上下文对象，无用
//  Applications            getApplications()               与服务端交互的请求方法
//          describeApi()
//          getQuery()
//          postQuery()
//          putQuery()
//          deleteQuery()

//  Organization            getOrganization()               组织相关的服务
//  WebservicesClient       getWebservicesClient()          发起HTTP请求和Webservice请求的工具
//  EntityManagerContainer  getEntityManagerContainer()     jpa管理对象，不暴露

//=== java_customResponse: =========================================
// 只在服务管理 invoke 中使用，主要用于设置 invoke 的请求响应 java对象
//      seeOther()              设置 303 跳转
//      temporaryRedirect()     设置 301 跳转
//      setBody()               设置 响应数据 和 响应 content-type 200

//=== java_effectivePerson: =========================================
//当前用户的对象 java对象 -- 改为 json字符串 todo
//      toJson() 转json字符串

//=== java_parameters: ==============================================
//只查询语句中使用，用于参数传递 json字符串

//=== java_requestText: ==============================================
// 只在服务管理 invoke 中使用，获取请求消息体的文本内容 json字符串

//=== java_request: ==================================================
// 只在服务管理 invoke 中使用，请求的 request对象 java对象

//=== java_message: ==================================================
// 只在消息过滤器和加载器中使用，描述消息的 json字符串
// 主要信息有：title、person、body、type

//=== person: ========================================================
//  只在系统配置中，默认用户密码设置脚本中使用，获取当前执行到的，即将生成密码的用户。 java对象
//  getName()、getMobile()、getEmployee() 等获取个人信息的方法
