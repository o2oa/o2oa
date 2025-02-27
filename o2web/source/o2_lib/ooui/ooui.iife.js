var vn=Object.defineProperty;var bn=(L,y,M)=>y in L?vn(L,y,{enumerable:!0,configurable:!0,writable:!0,value:M}):L[y]=M;var C=(L,y,M)=>(bn(L,typeof y!="symbol"?y+"":y,M),M),c4=(L,y,M)=>{if(!y.has(L))throw TypeError("Cannot "+M)};var l=(L,y,M)=>(c4(L,y,"read from private field"),M?M.call(L):y.get(L)),c=(L,y,M)=>{if(y.has(L))throw TypeError("Cannot add the same private member more than once");y instanceof WeakSet?y.add(L):y.set(L,M)},p=(L,y,M,Yt)=>(c4(L,y,"write to private field"),Yt?Yt.call(L,M):y.set(L,M),M);var a=(L,y,M)=>(c4(L,y,"access private method"),M);var $OOUI=function(L){var i2,W1,h4,r5,M9,o5,S9,l5,A9,G,a5,T9,H1,p4,Q,Z2,W,t2,e2,b,E,A,$,T,j,I,Y1,q1,U1,X1,G1,K1,J1,h1,L2,p1,j2,r2,D2,F2,$2,_2,u1,Q1,tt,C1,m1,g1,t4,et,u4,c5,nt,C4,st,m4,h5,O9,w2,j1,it,g4,I2,Ft,rt,f4,p5,P9,f1,C3,u5,Z9,y2,D1,e4,Ln,v1,m3,n4,_n,z2,$t,l2,i1,N2,It,b1,g3,L1,f3,K,v2,ot,v4,C5,j9,R2,zt,m5,D9,B2,Nt,g5,F9,V2,Rt,x2,k2,E2,b5,$9,L5,I9,_5,z9,w5,N9,W2,s4,i4,y5,R9,x5,B9,k5,V9,r4,D,_1,E5,W9,at,b4,w1,S5,H9,M2,T5,Y9,y1,Y2,Bt,O5,q9,P5,U9,x1,v3,dt,L4,Z5,X9,j5,G9,F5,K9,$5,J9,I5,Q9,z5,t8,N5,e8,R5,n8,J,O,k1,H,B5,s8,ht,_4,V5,i8,pt,w4,W5,r8,ut,q2,Vt,H5,o8,Y5,l8,q5,a8,U5,d8,X5,c8,Ct,y4,Y,a2,R,E1,te,h8,ee,p8,ne,u8,se,C8,ie,m8,mt,x4,gt,k4,re,g8,oe,f8,ft,E4,le,v8,vt,M4,ae,b8,S2,F1,de,L8,c2,r1,ce,_8,bt,S4,Lt,A4,h2,o1,M1,b3,p2,l1,pe,w8,_t,T4,wt,O4,Ce,y8,me,x8,ge,k8,q,U2,P,S1,A1,u2,yt,P4,X2,xt,Z4,fe,E8,kt,j4,ve,M8,T1,L3,Et,D4,G2,Wt,be,S8,Le,A8,Mt,F4,ye,T8,ke,O8,Ee,P8,Se,Z8,Te,j8,Oe,D8,P1,K2,A2,J2,C2,U,m2,Pe,F8,At,$4,Ze,$8,Tt,I4,De,I8,Fe,z8,$e,N8,Ie,R8,ze,B8,Ne,V8,Re,W8,Be,H8,g2,Ve,Y8,We,q8,Ye,U8,Ot,z4,qe,X8,Ue,G8,Pt,N4,Xe,K8,Z1,_3,Ge,J8,Ke,Q8,Je,Qe,t3,t7,e3,e7,Z,n3,n7,d4,s3,s7,Q2,Ht,i3,i7,jt,R4,Dt,B4,r3,r7,o3,o7,l3,l7,a3,a7,t1,e1,f2,n1,O2,d3,X,c3,d7,h3,c7,p3,h7;"use strict";var y=globalThis&&globalThis.__spreadArray||function(s,n,t){if(t||arguments.length===2)for(var e=0,i=n.length,o;e<i;e++)(o||!(e in n))&&(o||(o=Array.prototype.slice.call(n,0,e)),o[e]=n[e]);return s.concat(o||Array.prototype.slice.call(n))},M=function(){function s(n,t,e){this.name=n,this.version=t,this.os=e,this.type="browser"}return s}(),Yt=function(){function s(n){this.version=n,this.type="node",this.name="node",this.os=process.platform}return s}(),p7=function(){function s(n,t,e,i){this.name=n,this.version=t,this.os=e,this.bot=i,this.type="bot-device"}return s}(),u7=function(){function s(){this.type="bot",this.bot=!0,this.name="bot",this.version=null,this.os=null}return s}(),C7=function(){function s(){this.type="react-native",this.name="react-native",this.version=null,this.os=null}return s}(),m7=/alexa|bot|crawl(er|ing)|facebookexternalhit|feedburner|google web preview|nagios|postrank|pingdom|slurp|spider|yahoo!|yandex/,g7=/(nuhk|curl|Googlebot|Yammybot|Openbot|Slurp|MSNBot|Ask\ Jeeves\/Teoma|ia_archiver)/,V4=3,f7=[["aol",/AOLShield\/([0-9\._]+)/],["edge",/Edge\/([0-9\._]+)/],["edge-ios",/EdgiOS\/([0-9\._]+)/],["yandexbrowser",/YaBrowser\/([0-9\._]+)/],["kakaotalk",/KAKAOTALK\s([0-9\.]+)/],["samsung",/SamsungBrowser\/([0-9\.]+)/],["silk",/\bSilk\/([0-9._-]+)\b/],["miui",/MiuiBrowser\/([0-9\.]+)$/],["beaker",/BeakerBrowser\/([0-9\.]+)/],["edge-chromium",/EdgA?\/([0-9\.]+)/],["chromium-webview",/(?!Chrom.*OPR)wv\).*Chrom(?:e|ium)\/([0-9\.]+)(:?\s|$)/],["chrome",/(?!Chrom.*OPR)Chrom(?:e|ium)\/([0-9\.]+)(:?\s|$)/],["phantomjs",/PhantomJS\/([0-9\.]+)(:?\s|$)/],["crios",/CriOS\/([0-9\.]+)(:?\s|$)/],["firefox",/Firefox\/([0-9\.]+)(?:\s|$)/],["fxios",/FxiOS\/([0-9\.]+)/],["opera-mini",/Opera Mini.*Version\/([0-9\.]+)/],["opera",/Opera\/([0-9\.]+)(?:\s|$)/],["opera",/OPR\/([0-9\.]+)(:?\s|$)/],["pie",/^Microsoft Pocket Internet Explorer\/(\d+\.\d+)$/],["pie",/^Mozilla\/\d\.\d+\s\(compatible;\s(?:MSP?IE|MSInternet Explorer) (\d+\.\d+);.*Windows CE.*\)$/],["netfront",/^Mozilla\/\d\.\d+.*NetFront\/(\d.\d)/],["ie",/Trident\/7\.0.*rv\:([0-9\.]+).*\).*Gecko$/],["ie",/MSIE\s([0-9\.]+);.*Trident\/[4-7].0/],["ie",/MSIE\s(7\.0)/],["bb10",/BB10;\sTouch.*Version\/([0-9\.]+)/],["android",/Android\s([0-9\.]+)/],["ios",/Version\/([0-9\._]+).*Mobile.*Safari.*/],["safari",/Version\/([0-9\._]+).*Safari/],["facebook",/FB[AS]V\/([0-9\.]+)/],["instagram",/Instagram\s([0-9\.]+)/],["ios-webview",/AppleWebKit\/([0-9\.]+).*Mobile/],["ios-webview",/AppleWebKit\/([0-9\.]+).*Gecko\)$/],["curl",/^curl\/([0-9\.]+)$/],["searchbot",m7]],W4=[["iOS",/iP(hone|od|ad)/],["Android OS",/Android/],["BlackBerry OS",/BlackBerry|BB10/],["Windows Mobile",/IEMobile/],["Amazon OS",/Kindle/],["Windows 3.11",/Win16/],["Windows 95",/(Windows 95)|(Win95)|(Windows_95)/],["Windows 98",/(Windows 98)|(Win98)/],["Windows 2000",/(Windows NT 5.0)|(Windows 2000)/],["Windows XP",/(Windows NT 5.1)|(Windows XP)/],["Windows Server 2003",/(Windows NT 5.2)/],["Windows Vista",/(Windows NT 6.0)/],["Windows 7",/(Windows NT 6.1)/],["Windows 8",/(Windows NT 6.2)/],["Windows 8.1",/(Windows NT 6.3)/],["Windows 10",/(Windows NT 10.0)/],["Windows ME",/Windows ME/],["Windows CE",/Windows CE|WinCE|Microsoft Pocket Internet Explorer/],["Open BSD",/OpenBSD/],["Sun OS",/SunOS/],["Chrome OS",/CrOS/],["Linux",/(Linux)|(X11)/],["Mac OS",/(Mac_PowerPC)|(Macintosh)/],["QNX",/QNX/],["BeOS",/BeOS/],["OS/2",/OS\/2/]];function v7(s){return s?H4(s):typeof document>"u"&&typeof navigator<"u"&&navigator.product==="ReactNative"?new C7:typeof navigator<"u"?H4(navigator.userAgent):_7()}function b7(s){return s!==""&&f7.reduce(function(n,t){var e=t[0],i=t[1];if(n)return n;var o=i.exec(s);return!!o&&[e,o]},!1)}function H4(s){var n=b7(s);if(!n)return null;var t=n[0],e=n[1];if(t==="searchbot")return new u7;var i=e[1]&&e[1].split(".").join("_").split("_").slice(0,3);i?i.length<V4&&(i=y(y([],i,!0),w7(V4-i.length),!0)):i=[];var o=i.join("."),d=L7(s),h=g7.exec(s);return h&&h[1]?new p7(t,o,d,h[1]):new M(t,o,d)}function L7(s){for(var n=0,t=W4.length;n<t;n++){var e=W4[n],i=e[0],o=e[1],d=o.exec(s);if(d)return i}return null}function _7(){var s=typeof process<"u"&&process.version;return s?new Yt(process.version.slice(1)):null}function w7(s){for(var n=[],t=0;t<s;t++)n.push("0");return n}var qt,y7=new Uint8Array(16);function x7(){if(!qt&&(qt=typeof crypto<"u"&&crypto.getRandomValues&&crypto.getRandomValues.bind(crypto)||typeof msCrypto<"u"&&typeof msCrypto.getRandomValues=="function"&&msCrypto.getRandomValues.bind(msCrypto),!qt))throw new Error("crypto.getRandomValues() not supported. See https://github.com/uuidjs/uuid#getrandomvalues-not-supported");return qt(y7)}const k7=/^(?:[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}|00000000-0000-0000-0000-000000000000)$/i;function E7(s){return typeof s=="string"&&k7.test(s)}for(var S=[],w3=0;w3<256;++w3)S.push((w3+256).toString(16).substr(1));function M7(s){var n=arguments.length>1&&arguments[1]!==void 0?arguments[1]:0,t=(S[s[n+0]]+S[s[n+1]]+S[s[n+2]]+S[s[n+3]]+"-"+S[s[n+4]]+S[s[n+5]]+"-"+S[s[n+6]]+S[s[n+7]]+"-"+S[s[n+8]]+S[s[n+9]]+"-"+S[s[n+10]]+S[s[n+11]]+S[s[n+12]]+S[s[n+13]]+S[s[n+14]]+S[s[n+15]]).toLowerCase();if(!E7(t))throw TypeError("Stringified UUID is invalid");return t}function Y4(s,n,t){s=s||{};var e=s.random||(s.rng||x7)();if(e[6]=e[6]&15|64,e[8]=e[8]&63|128,n){t=t||0;for(var i=0;i<16;++i)n[t+i]=e[i];return n}return M7(e)}const q4=function(s,n,t){const e=n.toLowerCase();e==="text"?s.textContent=t:e==="html"?s.innerHTML=t:e==="styles"?r.setStyles(s,t):s.setAttribute(n,t)},S7=function(s,n){const t=n.toLowerCase();if(t==="text")return s.innerText||s.textContent||"";if(t==="html")return s.innerHTML||"";if(t==="value")switch(s.tagName.toString().toLowerCase()){case"select":return s.options[s.selectedIndex].value;default:return s.value}return s.getAttribute(n)},Ut=function(s){const n=Z7(s||"").split(" "),t={};return n.filter(function(e){if(e!==""&&!t[e])return t[e]=e})},U4=function(s){return!s.tagName||/^(?:body|html)$/i.test(s.tagName)},X4=function(s){return/html/i.test(s.tagName)},Xt=function(s,n){return parseInt(r.getStyle(s,n))||0},A7=function(s){return Xt(s,"border-top-width")},T7=function(s){return Xt(s,"border-left-width")},O7=function(s,n){return{left:s.x-Xt(n,"margin-left"),top:s.y-Xt(n,"margin-top")}},r=(s,n)=>{let t;const e=s.replace(/^\S*?(?=\.|#|$)/,h=>(t=h,""));let i="",o;e&&(o=e.split(".").filter(u=>!!u).map(u=>{const m=u.split("#"),f=m.shift();return i=m.pop()||i,f}));const d=document.createElement(t);return i&&d.setAttribute("id",i),o&&o.length&&d.setAttribute("class",o.join(" ")),n&&r.set(d,n),d};Object.assign(r,{el:(s,n)=>v(s)==="string"&&s?(n||document).querySelector(s):s,els:(s,n)=>(n||document).querySelectorAll(s),addClass:(s,n)=>{const t=r.el(s);return Ut(n).forEach(e=>{t.classList.add(e)}),t},removeClass:(s,n)=>{const t=r.el(s);return Ut(n).forEach(e=>{t.classList.remove(e)}),t},hasClass:(s,n)=>r.el(s).classList.contains(n),toggleClass:(s,n,t=!1)=>{const e=r.el(s);return Ut(n).forEach(i=>{let o=t;(o==null||o===!1)&&(o=!r.hasClass(e,i)),o?r.addClass(e,i):r.removeClass(e,i)}),e},checkClass:(s,n,t)=>{const e=r.el(s);return Ut(n).forEach(i=>{t?r.addClass(e,i):r.removeClass(e,i)}),e},mapProps:(s,n)=>{const t=r.el(s);for(const e in n){const i=_(e);if(typeof n[e]=="boolean")n[e]=t.hasAttribute(e)||t.hasAttribute(i)||!1;else{const o=t.getAttribute(e)||t.getAttribute(i)||"";o!==n[e]&&(n[e]=o)}}return t},toggleAttr:(s,n,t)=>{const e=r.el(s);return t?e.setAttribute(n,t):e.removeAttribute(n),e},set:(s,n,t)=>{const e=r.el(s);return typeof n=="object"?Object.keys(n).forEach(i=>{q4(e,i,n[i])}):q4(e,n,t),e},get:(s,n)=>{const t=r.el(s);return S7(t,n)},setProperty:(s,n,t)=>{const e=r.el(s);return e.setAttribute(n,t),e},getScroll:s=>{const n=r.el(s);return{x:n.scrollLeft,y:n.scrollTop}},getScrolls:s=>{let t=r.el(s).parentNode,e={x:0,y:0};for(;t&&!U4(t);)e.x+=t.scrollLeft,e.y+=t.scrollTop,t=t.parentNode;return e},getOffsetParent:s=>{let n=s.offsetParent;for(;n&&["table","td","th"].includes(n.tagName.toString().toLowerCase());)n=n.offsetParent;return n||document.body},getPosition:(s,n)=>{const t=r.el(s);for(n===window&&(n=document.documentElement);n&&r.getStyle(n,"display")==="contents";)n=n.parentElement;for(;s&&r.getStyle(s,"display")==="contents";)s=s.parentElement;const e=s.getBoundingClientRect(),i=document.documentElement,o=r.getScroll(i),d=r.getScrolls(t),h=r.getStyle(t,"position")==="fixed",u={x:e.left+d.x+(h?0:o.x)-i.clientLeft,y:e.top+d.y+(h?0:o.y)-i.clientTop},m=r.getScrolls(t),f={x:u.x-m.x,y:u.y-m.y};if(n){const w=r.getPosition(n);return{x:f.x-w.x-T7(n),y:f.y-w.y-A7(n)}}return f},setPosition:(s,n)=>{const t=r.el(s);return r.setStyles(t,O7(n.this))},getSize:s=>{const n=r.el(s);if(X4(n))return{x:n.clientWidth,y:n.clientHeight};{const t=n.getBoundingClientRect();return{x:t.width,y:t.height}}},getScrollSize:s=>{const n=r.el(s);if(/^(?:body|html)$/i.test(n.tagName)){const t=document.documentElement,e=document.body;return{x:Math.max(t.scrollWidth,e.scrollWidth),y:Math.max(t.scrollHeight,e.scrollHeight)}}return{x:n.scrollWidth,y:n.scrollHeight}},getStyle:function(s,n){const t=r.el(s),e=$1(n);let i=t.style[e];return i||(i=window.getComputedStyle(t)[e]),i},getStyles:s=>{const n=[...arguments],t=r.el(n.shift()),e={};return n.forEach(i=>{e[i]=r.getStyle(t,i)}),e},setStyle:(s,n,t)=>{const e=r.el(s);return typeof n=="object"?Object.keys(n).forEach(i=>{e.style[$1(i)]=n[i]}):e.style[$1(n)]=t,e},setStyles:(s,n)=>r.setStyle(s,n),isPositioned:s=>{const n=r.el(s),t=r.getStyle(n,"position");return t&&["absolute","fixed","relative","sticky"].includes(t)},show:s=>{const n=r.el(s);return r.getStyle(n,"display")==="none"&&r.setStyle(n,"display",n.dataset.storeDisplay||"block"),n},hide:s=>{const n=r.el(s),t=r.getStyle(n,"display");return t!=="none"&&(n.dataset.storeDisplay=t,r.setStyle(n,"display","none")),n},getParentSrcollNode:s=>{let t=r.el(s).parentElement,e=r.getStyle(t,"overflow"),i=r.getStyle(t,"overflow-y");for(;t&&(r.getScrollSize(t).y<=r.getSize(t).y||!["auto","scroll"].includes(e)&&!["auto","scroll"].includes(i));)t=t.parentElement,t&&(e=r.getStyle(t,"overflow"),i=r.getStyle(t,"overflow-y"));return t||document.documentElement},getParent:function(s,n){if(n){let t=s.parentElement;for(;t&&!t.matches(n);)t=t.parentElement;return t}else return s.parentElement},isBody:s=>U4(r.el(s)),isHtml:s=>X4(r.el(s)),empty:s=>{const n=r.el(s);for(;n.childNodes.length;)n.removeChild(n.childNodes[0]);return n},peel:(s,n)=>{const{node:t,position:e}=n&&v(n)==="element"?{node:n,position:"beforeend"}:{node:s,position:"beforebegin"};let i=s.firstElementChild;for(;i;)t.insertAdjacentElement(e,i),i=s.firstElementChild;s.remove()},getPositionParent(s){let n=r.el(s).parentElement;for(;n;){const t=n.tagName.toString().toLowerCase();if(t==="application")return n;if(t==="oo-dialog")return n._content;n=n.parentElement}return document.body},overlap(s,n,t=document.body,e){const i=this.getSize(r.el(n)),o=this.getPosition(r.el(n),t),d=r.el(s);this.setStyles(d,{position:"absolute",width:`${i.x}px`,height:`${i.y}px`,top:`${o.y}px`,left:`${o.x}px`}),d.isConnected||t.append(d)},appendTop(s,n){const t=s.firstChild;return t?s.insertBefore(n,t):s.appendChild(n)}}),v7();function v(s){if(s==null)return"null";if(Array.isArray(s))return"array";if(s instanceof Map)return"map";if(s instanceof Set)return"set";if(s===window)return"window";if(s instanceof Date)return"date";if(s instanceof RegExp)return"regexp";if(s instanceof Error)return"error";if(s instanceof Event)return"event";if((typeof s=="function"||typeof s=="object")&&typeof s.then=="function")return"promise";if(s.nodeName){if(s.nodeType===1)return"element";if(s.nodeType===3)return/\S/.test(s.nodeValue)?"textnode":"whitespace";if(s.nodeType===9)return"document";if(s.nodeType||s.nodeType===0)return s.nodeName}else if(typeof s.length=="number"&&s.callee)return"arguments";return typeof s}function a1(s,n,t=null,e=!0){const i=n||this,o=t?Object.values(t):[],d=t?Object.keys(t).join(","):"";return Function("return function("+d+"){"+s+"}")().apply(i,o)}function d1(s,n=20,t,e){const i=t;return n&&v(n)!=="number"&&(t=n,n=i&&v(i)==="number"?i:20),s.clear&&s.clear(),s.timerFunction=(o,d)=>{s.clear=()=>{s.timerId&&clearTimeout(s.timerId),s.promise&&(s.promise=null)},s.timerId=setTimeout(function(){s.timerId=null;try{const h=t?s.apply(t,e):s(e);Promise.resolve(h).then(u=>{o(u)})}catch(h){d(h)}},n)},s.promise=new Promise(s.timerFunction),s.promise}function G4(s){if(!s)return s;const n=v(s);return n==="function"?null:n==="array"||n==="object"?K4(s):s}function P7(s){let n=s.length,t=new Array(n);for(;n--;)t[n]=G4(s[n]);return t}function K4(s){if(v(s)==="array")return P7(s);const n={};for(let t in s)n[t]=G4(s[t]);return n}function _(s){return s.replace(/([A-Z])/g,"-$1").toLowerCase()}function $1(s){return s.replace(/-(\w)/g,function(n,t){return t.toUpperCase()})}function Z7(s){return s.replace(/\s+/g," ").trim()}function J4(s){return String(s).replace(/\b[a-z]/g,function(n){return n.toUpperCase()})}function j7(s){return s.replace(/<script(?:\s+[\w-]+(?:=(?:"[^"]*"|'[^']*'))?)*\s*>([\s\S]*?)<\/script\s*>|\s+on\w+\s*=\s*(?:"[^"]*"|'[^']*')|javascript:.*/g,"")}function I1(s,n){const t=s.indexOf(n);t!==-1&&(s.splice(t,1),I1(s,n))}const y3={ms:1,second:1e3,minute:6e4,hour:36e5,day:864e5,week:6084e5};function b2(s){return new Date(s.getTime())}function Gt(s,n){return s?s instanceof Date?n?b2(s):s:new Date(s):null}function D7(s,n){const t=new Date(s,n);return t.setDate(0),t}function F7(s,n){if(!s)return null;const t=Gt(s,n);return t.setHours(23),t.setMinutes(59),t.setSeconds(59),t.setMilliseconds(999),t}function z(s,n){if(!s)return null;const t=Gt(s,n);return t.setHours(0),t.setMinutes(0),t.setSeconds(0),t.setMilliseconds(0),t}function Q4(s,n){const t=b2(s),e=(7+s.getDay()-parseInt(n||0))%7;t.setDate(t.getDate()-e+3);const i=t.valueOf();return t.setMonth(0,1),t.getDay()!==4&&t.setMonth(0,1+(4-t.getDay()+7)%7),1+Math.ceil((i-t)/y3.week)}function B(s,n="day",t=1){switch(n){case"year":return B(s,"month",t*12);case"month":const e=Gt(s),i=e.getDate();return e.setMonth(e.getMonth()+t+1,0),i<e.getDate()&&s.setDate(i),e;case"week":return B(s,"day",t*7);case"date":case"day":return s=Gt(s),s.setDate(s.getDate()+t),s}if(!y3[n])throw new Error(n+" is not a supported interval");return s.setTime(this.getTime()+t*y3[n])}function V(s,n){const t={"M+":s.getMonth()+1,"D+":s.getDate(),"h+":s.getHours()%12===0?12:s.getHours()%12,"H+":s.getHours(),"m+":s.getMinutes(),"s+":s.getSeconds(),"q+":Math.floor((s.getMonth()+3)/3),d:s.getDay(),S:s.getMilliseconds(),a:s.getHours()<12?"上午":"下午",A:s.getHours()<12?"AM":"PM"};/(Y+)/.test(n)&&(n=n.replace(RegExp.$1,(s.getFullYear()+"").substr(4-RegExp.$1.length))),/(W+)/.test(n)&&(t["W+"]=Q4(s));for(let e in t)new RegExp("("+e+")").test(n)&&(n=n.replace(RegExp.$1,RegExp.$1.length===1?t[e]:("00"+t[e]).substr((""+t[e]).length)));return n}function t9(s,n){const t=`css${Y4()}`;let e=s.toString();if(e){if(n&&r.addClass(n,t),e=e.replace(/\/\*(\s|\S)*?\*\//g,""),n){const o=new RegExp("[^{}\\/]+\\s*(?=\\{)","g"),d="."+t+" ";e=e.replace(o,h=>{const u=h.trim();return u.startsWith("@")||u==="from"||u==="to"?u:u.split(/\s*,\s*/g).map(function(f){return d+f}).join(", ")})}const i=r("style");return i.setAttribute("type","text/css"),i.setAttribute("id",t),document.head.appendChild(i),i.appendChild(document.createTextNode(e)),i}return null}const x3={};function e9(s,n,t,e){if(t&&x3[t])return x3[t];const i=document.createElement("template");i.innerHTML=s;const o=i.content;if(n){const d=document.createElement("style");d.textContent=n,o.prepend(d)}if(e){const d=document.querySelector("#oo-css-skin");if(d){const h=d.cloneNode(!0);h.removeAttribute("id"),o.prepend(h)}}return document.body.appendChild(i),t&&(x3[t]=i.content),i.content}class k extends HTMLElement{constructor(){super();c(this,W1);c(this,r5);c(this,o5);c(this,l5);c(this,i2,null)}_setEvent(){}_afterRender(){}_render(t){}_connected(){}_disconnected(){}_initialize(t,e,i,o){Object.hasOwn(this,"value")&&(this.value&&this.setAttribute("value",this.value),Reflect.deleteProperty(this,"value")),this._props=e?this._getProps(e.prop):{};const d=e9(i,o,t,!0);this._useTemplate(d),this._render(t),this._setEvent(),this._afterRender(),this._createProperties(e),this.dispatchEvent(new CustomEvent("load"))}_createProperties(t){Object.keys(this._props).forEach(e=>{let i=e;for(;this.hasOwnProperty(i)||t.prototype.hasOwnProperty(i);)i=`_${i}`;Object.hasOwn(this,i)||Object.defineProperty(this,i,{get:()=>this._props[i],set:o=>{o!==this._props[i]&&this.setAttribute(_(i),o)}})})}_getProps(t){const e=K4(t);return this.getAttributeNames().forEach(i=>{const o=$1(i);if(e.hasOwnProperty(o)){const d=this.getAttribute(i);/{{.*}}/g.test(d)||a(this,W1,h4).call(this,e,o,d)}}),e}_useCss(t){const e=document.createElement("style");return e.textContent=t,this._content.insertAdjacentElement("beforebegin",e),e}_useCssLink(t){const e=document.createElement("link");return e.rel="stylesheet",e.type="text/css",e.charSet="UTF-8",e.href=t,this._content.insertAdjacentElement("beforebegin",e),e}useCss(t){this.styleNode&&(this.styleNode.remove(),this.styleNode=null),t&&(this.styleNode=this._useCss(t))}useCssLink(t){this.styleNode&&(this.styleNode.remove(),this.styleNode=null),t&&(this.styleNode=this._useCssLink(t))}_useTemplate(t,e="open"){(this.shadowRoot||this.attachShadow({mode:e})).appendChild(t.cloneNode(!0)),a(this,r5,M9).call(this)}connectedCallback(){this._connected()}disconnectedCallback(){this._disconnected()}attributeChangedCallback(t,e,i){if(!/{{.*}}/g.test(i)){const o=$1(t),d=this._props[o];a(this,W1,h4).call(this,this._props,o,i),a(this,o5,S9).call(this,o,d,e)}}_setProps(t,e,i){const o=this._props[t];i!==o&&(this._setPropMap[t]?this._setPropMap[t](e,o):this._setPropMap.$default(t,e,o))}_useSkin(t){if(l(this,i2)&&l(this,i2).remove(),p(this,i2,null),t){const e=t.split(/\s*;\s*/g);p(this,i2,r("style"));let i="";e.forEach(o=>{const d=o.split(/\s*:\s*/g);i+=`--${d[0]}: ${d[1]};
	`}),l(this,i2).textContent=`
.content{
	${i}
}
`,this._content.insertAdjacentElement("beforebegin",l(this,i2))}}_fillContent(t){new MutationObserver(function(i){i.forEach(o=>{o.addedNodes.forEach(d=>{d.nodeType===Node.ELEMENT_NODE&&d.setAttribute("slot",t)})})}).observe(this,{subtree:!1,childList:!0,attributes:!1,characterData:!1}),a(this,l5,A9).call(this,t)}_canRender(t,e){if(t&&t.length){for(const i of t)if(!this.getAttribute(i))return!1}if(e&&e.length){for(const i of e)if(this.getAttribute(i))return!0}return!0}}i2=new WeakMap,W1=new WeakSet,h4=function(t,e,i){switch(v(t[e])){case"boolean":t[e]=!!i&&i!=="false";break;case"number":t[e]=isNaN(i)?i:parseInt(i);break;case"object":try{t[e]=JSON.parse(i)}catch{t[e]=i}break;case"array":try{t[e]=JSON.parse(i)}catch{t[e]=i?i.split(/\s*,\s*/g):[]}break;default:t[e]=i}},r5=new WeakSet,M9=function(){for(const t in this._elements)this._elements[t]=this.shadowRoot.querySelector(`.${t}`);this._content=this.shadowRoot.querySelector(".content")},o5=new WeakSet,S9=function(t,e,i){(t?[t]:Object.keys(this._props)).forEach(d=>{this._setProps(d,e,i)})},l5=new WeakSet,A9=function(t){let e=this.firstElementChild;for(;e;)e.setAttribute("slot",t),e=e.nextElementSibling};const $7=`<div class="button content">\r
	<div class="prefix"></div>\r
	<div class="text"></div>\r
	<div class="suffix"></div>\r
</div>\r
`,I7=`body{\r
    display: inline-block;\r
}\r
* {\r
    box-sizing: border-box;\r
    user-select: none;\r
}\r
.content{\r
    --color: var(--oo-color-text-white);\r
    --bg: var(--oo-color-main);\r
    --radius: var(--oo-default-radius);\r
\r
    --cancel-color: var(--oo-color-text2);\r
    --cancel-bg: var(--oo-color-gray-d);\r
\r
    --simple-color: var(--oo-color-text3);\r
    --simple-bg: transparent;\r
\r
    --light-color: var(--oo-color-text3);\r
    --light-bg: var(--oo-color-bg-white);\r
\r
    --icon-color: var(--oo-color-text3);\r
    --icon-bg: transparent;\r
\r
    --hover-bg: var(--oo-color-main-light);\r
    --active-bg: var(--oo-color-main-deep);\r
    --focus-bg: var(--oo-color-main-light);\r
\r
    --cancel-hover-bg: var(--oo-color-gray-d1);\r
    --cancel-active-bg: var(--oo-color-gray-b);\r
    --cancel-focus-bg: var(--oo-color-gray1);\r
\r
    --simple-hover-bg: var(--oo-color-gray-e1);\r
    --simple-active-bg: var(--oo-color-gray-d);\r
    --simple-focus-bg: var(--oo-color-gray1);\r
\r
    --light-hover-bg: var(--oo-color-main);\r
    --light-active-bg: var(--oo-color-main-deep);\r
    --light-focus-bg: var(--oo-color-bg-white);\r
    --light-hover-color: var(--oo-color-text-white);\r
    --light-active-color: var(--oo-color-text-white);\r
    --light-focus-color: var(--oo-color-text3);\r
\r
    --icon-hover-bg: var(--oo-color-gray-e1);\r
    --icon-active-bg: var(--oo-color-gray-d);\r
    --icon-focus-bg: var(--oo-color-gray1);\r
}\r
\r
.button {\r
    border-radius: var(--radius);\r
    border: 0;\r
    padding: 0.375em 0.6em;\r
    cursor: pointer;\r
    color: var(--color);\r
    background-color: var(--bg);\r
    transition: background 0.1s, color 0.1s;\r
    text-align: center;\r
    display: flex;\r
    align-items: baseline;\r
    justify-content: center;\r
    height: 100%;\r
    width: 100%;\r
    /*display: grid;*/\r
    /*grid-template-columns: auto auto;*/\r
    gap: 0.3em;\r
}\r
.button.cancel{\r
    background-color: var(--cancel-bg);\r
    color: var(--cancel-color);\r
}\r
.button.simple{\r
    background-color: var(--simple-bg);\r
    color: unset;\r
    padding: 0.375em 0.375em;\r
}\r
.button.light{\r
    background-color: var(--light-bg);\r
    color: unset;\r
    padding: 0.375em 0.8em;\r
    border: 1px solid var(--oo-color-gray-b);\r
}\r
.button.icon{\r
    background-color: var(--icon-bg);\r
    align-items: center;\r
    color: unset;\r
    padding: 0.3125em;\r
    gap: 0;\r
}\r
.button.icon .text{\r
    display: none;\r
}\r
\r
div.prefix{\r
    display: none;\r
    font-size: 0.9em;\r
}\r
div.suffix{\r
    display: none;\r
    font-size: 0.9em;\r
}\r
div.text{\r
    height: 100%;\r
    display: inline-flex;\r
    align-items: center;\r
    justify-content: center;\r
}\r
\r
.content>.icon{\r
    display: block;\r
}\r
\r
.button:hover{\r
    background-color: var(--hover-bg);\r
}\r
.button:active{\r
    background-color: var(--active-bg);\r
}\r
.button:focus{\r
    background-color: var(--focus-bg);\r
}\r
.button.cancel:hover{\r
    background-color: var(--cancel-hover-bg);\r
}\r
.button.cancel:active{\r
    background-color: var(--cancel-active-bg);\r
}\r
.button.cancel:focus{\r
    background-color: var(--cancel-focus-bg);\r
}\r
.button.simple:hover{\r
    background-color: var(--simple-hover-bg);\r
}\r
.button.simple:active{\r
    background-color: var(--simple-active-bg);\r
}\r
.button.simple:focus{\r
    background-color: var(--simple-focus-bg);\r
}\r
\r
.button.light:hover{\r
    background-color: var(--light-hover-bg);\r
    color: var(--light-hover-color);\r
}\r
.button.light:active{\r
    background-color: var(--light-active-bg);\r
    color: var(--light-active-color);\r
}\r
.button.light:focus{\r
    background-color: var(--light-focus-bg);\r
    color: var(--light-focus-color);\r
}\r
\r
.button.icon:hover{\r
    background-color: var(--icon-hover-bg);\r
}\r
.button.icon:active{\r
    background-color: var(--icon-active-bg);\r
}\r
.button.icon:focus{\r
    background-color: var(--icon-focus-bg);\r
}\r
\r
\r
.disabled, .disabled:hover, .disabled:active, .disabled:focus {\r
    outline: none;\r
    cursor: not-allowed;\r
    opacity: 0.5;\r
}\r
.cancel.disabled, .cancel.disabled:hover, .cancel.disabled:active, .cancel.disabled:focus {\r
    outline: none;\r
    cursor: not-allowed;\r
    opacity: 0.5;\r
}\r
.hide{\r
    display: none;\r
}\r
`,d5=class d5 extends k{constructor(){super();c(this,a5);c(this,H1);C(this,"_elements",{text:null,prefix:null,suffix:null,button:null});c(this,G,{});C(this,"_setPropMap",{leftIcon:t=>{r.removeClass(this._elements.prefix,`ooicon-${t} icon`),this._props.leftIcon&&r.addClass(this._elements.prefix,`ooicon-${this._props.leftIcon} icon`)},rightIcon:t=>{r.removeClass(this._elements.suffix,`ooicon-${t} icon`),this._props.rightIcon&&r.addClass(this._elements.suffix,`ooicon-${this._props.rightIcon} icon`)},styles:()=>{},disabled:t=>{r.checkClass(this._elements.button,"disabled",this._props.disabled),r.toggleAttr(this._elements.button,"disabled",this._props.disabled),a(this,a5,T9).call(this,this._props.disabled)},type:t=>{this._props.type||(this._props.type="default"),t&&r.removeClass(this._elements.button,t),r.addClass(this._elements.button,this._props.type)},text:()=>{this.innerHTML.trim()||(this._elements.text.textContent=this._props.text)},skin:()=>{this._useSkin(this._props.skin)},$default:t=>{t==="value"&&(this.value=this._props[t]),r.toggleAttr(this._elements.button,t,this._props[t])}})}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_init(t,e,i,o){this._initialize(t,e||d5,i||$7,o||I7)}_render(){const t=this;new MutationObserver(function(){var i;a(i=t,H1,p4).call(i)}).observe(this,{subtree:!0,childList:!0,attributes:!0,characterData:!0}),a(this,H1,p4).call(this)}_connected(){}};G=new WeakMap,a5=new WeakSet,T9=function(t){if(t){p(this,G,l(this,G)||{});for(let e in this)e.startsWith("on")&&this[e]&&(l(this,G)[e]=this[e],this[e]=null);this.style.pointerEvents="none"}else{if(l(this,G))for(let e in l(this,G))l(this,G)[e]&&(this[e]=l(this,G)[e],l(this,G)[e]=null);this.style.pointerEvents="auto"}},H1=new WeakSet,p4=function(){this.innerHTML.trim()&&(r.empty(this._elements.text),this._elements.text.insertAdjacentHTML("beforeend",this.innerHTML))},C(d5,"prop",{leftIcon:"",rightIcon:"",styles:"",disabled:!1,text:"",type:"",skin:""});let Kt=d5;class n9 extends Kt{constructor(){super(),this._init("oo-button")}}const z7=`<div class="content">\r
    <div class="left">\r
        <div class="top">\r
            <div class="title"></div>\r
            <div class="prev ooicon-arrow_back"></div>\r
            <div class="next ooicon-arrow_forward"></div>\r
        </div>\r
        <div class="middle">\r
            <table border="0" cellpadding="0"  cellspacing="1" class="yearContent">\r
                <tbody>\r
                <tr>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                </tbody>\r
            </table>\r
            <table border="0" cellpadding="0" cellspacing="1" class="monthContent">\r
                <tbody>\r
                <tr>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                </tbody>\r
            </table>\r
            <table border="0" cellpadding="0" cellspacing="1" class="dateContent">\r
                <thead>\r
                <tr>\r
                    <th class="hide">周</th>\r
                    <th>一</th>\r
                    <th>二</th>\r
                    <th>三</th>\r
                    <th>四</th>\r
                    <th>五</th>\r
                    <th>六</th>\r
                    <th>日</th>\r
                </tr>\r
                </thead>\r
                <tbody>\r
                <tr>\r
                    <td class="week hide"><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                    <td><div><span></span></div></td>\r
                </tr>\r
                </tbody>\r
            </table>\r
        </div>\r
        <div class="bottom">\r
            <div><div class="clean">清除</div></div>\r
            <div><div class="today">今天</div></div>\r
        </div>\r
    </div>\r
    <div class="line"></div>\r
    <div class="right">\r
        <div class="time">\r
            <ul class="hourContent">\r
                <li><span>00</span></li><li><span>01</span></li><li><span>02</span></li><li><span>03</span></li><li><span>04</span></li><li><span>05</span></li>\r
                <li><span>06</span></li><li><span>07</span></li><li><span>08</span></li><li><span>09</span></li><li><span>10</span></li><li><span>11</span></li>\r
                <li><span>12</span></li><li><span>13</span></li><li><span>14</span></li><li><span>15</span></li><li><span>16</span></li><li><span>17</span></li>\r
                <li><span>18</span></li><li><span>19</span></li><li><span>20</span></li><li><span>21</span></li><li><span>22</span></li><li><span>23</span></li>\r
            </ul>\r
            <ul class="minuteContent">\r
                <li><span>00</span></li><li><span>01</span></li><li><span>02</span></li><li><span>03</span></li><li><span>04</span></li><li><span>05</span></li>\r
                <li><span>06</span></li><li><span>07</span></li><li><span>08</span></li><li><span>09</span></li><li><span>10</span></li><li><span>11</span></li>\r
                <li><span>12</span></li><li><span>13</span></li><li><span>14</span></li><li><span>15</span></li><li><span>16</span></li><li><span>17</span></li>\r
                <li><span>18</span></li><li><span>19</span></li><li><span>20</span></li><li><span>21</span></li><li><span>22</span></li><li><span>23</span></li>\r
                <li><span>24</span></li><li><span>25</span></li><li><span>26</span></li><li><span>27</span></li><li><span>28</span></li><li><span>29</span></li>\r
                <li><span>30</span></li><li><span>31</span></li><li><span>32</span></li><li><span>33</span></li><li><span>34</span></li><li><span>35</span></li>\r
                <li><span>36</span></li><li><span>37</span></li><li><span>38</span></li><li><span>39</span></li><li><span>40</span></li><li><span>41</span></li>\r
                <li><span>42</span></li><li><span>43</span></li><li><span>44</span></li><li><span>45</span></li><li><span>46</span></li><li><span>47</span></li>\r
                <li><span>48</span></li><li><span>49</span></li><li><span>50</span></li><li><span>51</span></li><li><span>52</span></li><li><span>53</span></li>\r
                <li><span>54</span></li><li><span>55</span></li><li><span>56</span></li><li><span>57</span></li><li><span>58</span></li><li><span>59</span></li>\r
            </ul>\r
            <ul class="secondContent">\r
                <li><span>00</span></li><li><span>01</span></li><li><span>02</span></li><li><span>03</span></li><li><span>04</span></li><li><span>05</span></li>\r
                <li><span>06</span></li><li><span>07</span></li><li><span>08</span></li><li><span>09</span></li><li><span>10</span></li><li><span>11</span></li>\r
                <li><span>12</span></li><li><span>13</span></li><li><span>14</span></li><li><span>15</span></li><li><span>16</span></li><li><span>17</span></li>\r
                <li><span>18</span></li><li><span>19</span></li><li><span>20</span></li><li><span>21</span></li><li><span>22</span></li><li><span>23</span></li>\r
                <li><span>24</span></li><li><span>25</span></li><li><span>26</span></li><li><span>27</span></li><li><span>28</span></li><li><span>29</span></li>\r
                <li><span>30</span></li><li><span>31</span></li><li><span>32</span></li><li><span>33</span></li><li><span>34</span></li><li><span>35</span></li>\r
                <li><span>36</span></li><li><span>37</span></li><li><span>38</span></li><li><span>39</span></li><li><span>40</span></li><li><span>41</span></li>\r
                <li><span>42</span></li><li><span>43</span></li><li><span>44</span></li><li><span>45</span></li><li><span>46</span></li><li><span>47</span></li>\r
                <li><span>48</span></li><li><span>49</span></li><li><span>50</span></li><li><span>51</span></li><li><span>52</span></li><li><span>53</span></li>\r
                <li><span>54</span></li><li><span>55</span></li><li><span>56</span></li><li><span>57</span></li><li><span>58</span></li><li><span>59</span></li>\r
            </ul>\r
        </div>\r
        <div class="cleanTime">清除</div>\r
    </div>\r
</div>`,N7=`*{box-sizing:border-box}.content{display:flex;height:100%;padding:1em;overflow:auto;-webkit-user-select:none;user-select:none}.left{flex:8;display:grid;grid-template-rows:2em 1fr 1.5em}.right{flex:5;height:100%}.top{background-color:var(--oo-color-gray2);height:2em;display:flex;line-height:2em}.middle{flex:1}.bottom{line-height:1em;display:flex;justify-content:space-between}.title{cursor:pointer;color:var(--oo-color-text);font-weight:700;flex:1;padding-left:.8em}.title:hover{color:var(--oo-color-main)}.prev{height:2em;line-height:2em;cursor:pointer;padding-left:1em}.prev:hover{color:var(--oo-color-main)}.next{height:2em;line-height:2em;cursor:pointer;padding-left:1em}.next:hover{color:var(--oo-color-main)}.time{display:flex;height:100%}.timeWithClean{height:calc(100% - 1.5em)}.cleanTime{height:1.5em;text-align:center;color:var(--oo-color-main);cursor:pointer}table{width:100%;height:100%}th{color:var(--oo-color-text);font-weight:700;line-height:2em;height:2em}td{cursor:pointer;text-align:center;color:var(--oo-color-text)}td.other{color:var(--oo-color-text3)}td div{display:flex;justify-content:center}td span{display:block;border:1px solid transparent}.yearContent td span,.monthContent td span{height:2.5em;line-height:2.5em;padding:0 .2em;border-radius:.25em}.dateContent td span{height:1.6em;line-height:1.6em;width:1.6em;border-radius:.25em}td:hover span{border-color:var(--oo-color-over);background-color:var(--oo-color-over)}td.today span{color:var(--oo-color-main);border-color:var(--oo-color-main);background-color:var(--oo-color-bg-white)}td.selected span{color:var(--oo-color-text-white);border-color:var(--oo-color-main);background-color:var(--oo-color-main)}.week-row-over span,.week-row-selected span{border-color:var(--oo-color-over)!important;background-color:var(--oo-color-over)!important}.week-row-selected>td:first-child>div>span{color:var(--oo-color-text-white)!important;border-color:var(--oo-color-main)!important;background-color:var(--oo-color-main)!important}.line{height:100%;width:1em;border-right:1px solid var(--oo-color-gray1)}ul{padding:0;margin:0;list-style:none;overflow:auto;flex:1;min-width:2em}ul::-webkit-scrollbar{display:none;width:0;height:0}li{text-align:center;height:2em;line-height:2em;cursor:pointer;display:flex;justify-content:center}li span{height:1.6em;display:block;line-height:1.6em;width:1.6em;border-radius:.25em;border:1px solid transparent}li:hover span{border-color:var(--oo-color-over);background-color:var(--oo-color-over)}li.selected span{color:var(--oo-color-text-white);border-color:var(--oo-color-main);background-color:var(--oo-color-main)}.week{color:var(--oo-color-main)}.disabled{color:var(--oo-color-text4)!important}.disabled span{border-color:transparent!important;background-color:transparent!important}.today,.clean{color:var(--oo-color-main);cursor:pointer}.hide{display:none}ul::-webkit-scrollbar{display:unset;background-color:transparent;cursor:pointer;width:4px;height:4px}ul::-webkit-scrollbar-thumb{width:4px;border-radius:4px;background-color:var(--oo-color-text3);cursor:pointer}ul::-webkit-scrollbar-thumb:hover{width:4px;border-radius:4px;background-color:var(--oo-color-text2);cursor:pointer}
`,x=class x extends k{constructor(){super();c(this,et);c(this,nt);c(this,st);c(this,h5);c(this,w2);c(this,it);c(this,I2);c(this,rt);c(this,p5);c(this,f1);c(this,u5);c(this,y2);c(this,e4);c(this,v1);c(this,n4);c(this,z2);c(this,l2);c(this,N2);c(this,b1);c(this,L1);c(this,K);c(this,ot);c(this,C5);c(this,R2);c(this,m5);c(this,B2);c(this,g5);c(this,V2);c(this,Q,void 0);c(this,Z2,void 0);c(this,W,void 0);c(this,t2,void 0);c(this,e2,void 0);c(this,b,void 0);c(this,E,void 0);c(this,A,void 0);c(this,$,void 0);c(this,T,void 0);c(this,j,void 0);c(this,I,void 0);c(this,Y1,void 0);c(this,q1,void 0);c(this,U1,void 0);c(this,X1,void 0);c(this,G1,void 0);c(this,K1,void 0);c(this,J1,void 0);c(this,h1,void 0);c(this,L2,void 0);c(this,p1,void 0);c(this,j2,void 0);c(this,r2,void 0);c(this,D2,void 0);c(this,F2,void 0);c(this,$2,void 0);c(this,_2,void 0);c(this,u1,void 0);c(this,Q1,void 0);c(this,tt,void 0);c(this,C1,void 0);c(this,m1,void 0);c(this,g1,void 0);C(this,"_elements",{content:null,top:null,title:null,prev:null,next:null,middle:null,bottom:null,left:null,yearContent:null,monthContent:null,dateContent:null,right:null,time:null,hourContent:null,minuteContent:null,secondContent:null,line:null,clean:null,cleanTime:null,today:null});c(this,t4,{});C(this,"_setPropMap",{skin:()=>{this._useSkin(this._props.skin)},view:()=>{this.currentView=this._props.view,this.showView()},value:()=>{this._props.value!==l(this,_2)&&(a(this,nt,C4).call(this),this.showView(),a(this,it,g4).call(this))},baseDate:()=>{a(this,st,m4).call(this),this.showView()},yearOnly:()=>{this._changeType()},monthOnly:()=>{this._changeType()},dateOnly:()=>{this._changeType()},weekOnly:()=>{this._changeType()},timeOnly:()=>{this._changeType()},secondEnable:()=>{a(this,w2,j1).call(this),this.setContentEvent()},cleanEnable:()=>{a(this,w2,j1).call(this)},todayEnable:()=>{a(this,w2,j1).call(this)},datetimeRange:()=>{this.setRange()},dateRange:()=>{this.setRange()},timeRange:()=>{this.setRange()},weekBegin:()=>{this.currentView==="date"&&this.showView()},$default:t=>{}});c(this,c5,[t=>this._props.yearOnly&&a(this,b1,g3).call(this,t)&&p(this,E,parseInt(t)||!0),t=>this._props.monthOnly&&a(this,L1,f3).call(this,t)&&((()=>{const[e,i]=t.split("-");p(this,E,parseInt(e)),p(this,A,parseInt(i)-1)})()||!0),t=>this._props.dateOnly&&a(this,K,v2).call(this,t)&&((()=>{const[e,i,o]=t.split("-");p(this,E,parseInt(e)),p(this,A,parseInt(i)-1),p(this,$,parseInt(o))})()||!0),t=>this._props.weekOnly&&((()=>{const[e,i]=t.split(" ");p(this,E,parseInt(e)),p(this,h1,parseInt(i))})()||!0),t=>this._props.timeOnly&&((()=>{const[e,i,o]=t.split(":");a(this,R2,zt).call(this,e)&&p(this,T,parseInt(e)),a(this,B2,Nt).call(this,i)&&p(this,j,parseInt(i)),o&&a(this,V2,Rt).call(this,o)&&p(this,I,parseInt(o))})()||!0),t=>{const e=x.parseDate(t,!0);a(this,K,v2).call(this,e)&&(p(this,E,e.getFullYear()),p(this,A,e.getMonth()),p(this,$,e.getDate())),a(this,R2,zt).call(this,e.getHours())&&p(this,T,e.getHours()),a(this,B2,Nt).call(this,e.getMinutes())&&p(this,j,e.getMinutes()),a(this,V2,Rt).call(this,e.getSeconds())&&p(this,I,e.getSeconds())}]);this._initialize("oo-calendar",x,z7,N7)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}static parseDate(t,e){return t?v(t)==="date"?e?new Date(t.getTime()):t:new Date(t):null}_setProps(t,e){const i=this._props[t];e!==i&&(this._setPropMap[t]?this._setPropMap[t](e,i):this._setPropMap.$default(t,e,i))}_render(){this.currentView=a(this,et,u4).call(this),this.setRange(),a(this,w2,j1).call(this),a(this,st,m4).call(this),a(this,nt,C4).call(this),this.showView(),a(this,it,g4).call(this),(!this._props.yearOnly||!this._props.monthOnly||!this._props.dateOnly||!this._props.weekOnly)&&(a(this,rt,f4).call(this),a(this,f1,C3).call(this),a(this,y2,D1).call(this))}_changeType(){this.currentView=a(this,et,u4).call(this),a(this,w2,j1).call(this),this.setContentEvent(),this.showView()}_setEvent(){this._elements.title.addEventListener("click",()=>{this.changeView()}),this._elements.prev.addEventListener("click",()=>{this.gotoPrev()}),this._elements.next.addEventListener("click",()=>{this.gotoNext()}),this._elements.today.addEventListener("click",()=>{this.gotoToday()}),this._elements.clean.addEventListener("click",()=>{this.clear()}),this._elements.cleanTime.addEventListener("click",()=>{this.clear()}),this.setContentEvent()}setContentEvent(){this._props.timeOnly||this.setYearEvent(),!this._props.timeOnly&&!this._props.yearOnly&&this.setMonthEvent(),!this._props.timeOnly&&!this._props.yearOnly&&!this._props.monthOnly&&(this._props.weekOnly?this.setWeekEvent():this.setDateEvent()),!this._props.yearOnly&&!this._props.monthOnly&&!this._props.dateOnly&&!this._props.weekOnly&&(this.setHourEvent(),this.setMinuteEvent(),this._props.secondEnable&&this.setSecondEvent())}getDate(){const t=b2(l(this,b));return v(l(this,E))!=="null"&&t.setFullYear(l(this,E)),v(l(this,A))!=="null"&&t.setMonth(l(this,A)),v(l(this,$))!=="null"&&t.setDate(l(this,$)),t.setHours(l(this,T)||0),t.setMinutes(l(this,j)||0),t.setSeconds(l(this,I)||0),t}getSelectedDate(){return v(l(this,E))!=="null"&&v(l(this,A))!=="null"&&v(l(this,$))!=="null"?new Date(l(this,E),l(this,A),l(this,$)):null}selectDate(t){const e=t.parentNode.parentNode.querySelector("td.selected");e&&r.removeClass(e,"selected"),r.addClass(t,"selected"),this.dispatchChangeEvent()}selectTime(t){const e=t.parentNode.querySelector("li.selected");e&&r.removeClass(e,"selected"),r.addClass(t,"selected"),this.dispatchChangeEvent()}getDefaultFormat(){return this._props.yearOnly&&"YYYY"||this._props.monthOnly&&"YYYY-MM"||this._props.dateOnly&&"YYYY-MM-DD"||this._props.weekOnly&&"YYYY ww"||this._props.timeOnly&&(this._props.secondEnable?"HH:mm:ss":"HH:mm")||"YYYY-MM-DD HH:mm:ss"}dispatchChangeEvent(){const t=this.getDate(),e=this._props.format||this.getDefaultFormat();p(this,_2,V(t,e)),this.dispatchEvent(new CustomEvent("change",{detail:{date:t,value:l(this,_2)}}))}dispatchWeekEvent(t,e,i){const o=this._props.format||this.getDefaultFormat(),d=x.parseDate(e),h=x.parseDate(i);p(this,_2,V(d,o)),this.dispatchEvent(new CustomEvent("change",{detail:{date:d,value:l(this,_2),startDate:d,endDate:h,weekNumber:t}}))}clear(){p(this,E,null),p(this,A,null),p(this,$,null),p(this,T,null),p(this,j,null),p(this,I,null);var t=this._elements.content.querySelectorAll(".selected");t.forEach(e=>{r.removeClass(e,"selected")}),this.dispatchEvent(new CustomEvent("change",{detail:{date:null,value:""}}))}gotoToday(){const t=new Date;l(this,b).setFullYear(t.getFullYear()),l(this,b).setMonth(t.getMonth()),l(this,b).setDate(t.getDate()),this.showView()}gotoPrev(){switch(this.currentView){case"year":B(l(this,b),"year",-16),this.changeViewToYear();break;case"month":B(l(this,b),"year",-1),this.changeViewToMonth();break;case"date":B(l(this,b),"month",-1),this.changeViewToDate();break;case"week":B(l(this,b),"month",-1),this.changeViewToWeek();break}}gotoNext(){switch(this.currentView){case"year":B(l(this,b),"year",16),this.changeViewToYear();break;case"month":B(l(this,b),"year",1),this.changeViewToMonth();break;case"date":B(l(this,b),"month",1),this.changeViewToDate();break;case"week":B(l(this,b),"month",1),this.changeViewToWeek();break}}setTitle(t){let e;switch(this.currentView){case"year":t&&(this._elements.title.textContent=t);return;case"month":e={year:"numeric"};break;case"date":case"week":e={year:"numeric",month:"long"};break;default:e={year:"numeric",month:"long",day:"numeric"}}this._elements.title.textContent=new Intl.DateTimeFormat("zh-CN",e).format(l(this,b))}showView(){switch(this.currentView){case"year":this.changeViewToYear();break;case"month":this.changeViewToMonth();break;case"date":this.setWeekTitle(),this.changeViewToDate();break;case"week":this.setWeekTitle(),this.changeViewToWeek();break}}changeView(){switch(this.currentView){case"date":case"week":this.changeViewToMonth();break;case"month":this.changeViewToYear();break;case"year":if(this._props.yearOnly)break;this._props.monthOnly?this.changeViewToMonth():this._props.weekOnly?this.changeViewToWeek():this.changeViewToDate();break}}changeViewToYear(t){this.currentView="year",a(this,I2,Ft).call(this,"year"),this.setYearContent(t)}setYearContent(){this.setYearEvent();const t=this._elements.yearContent.querySelectorAll("td"),e=new Date().getFullYear(),i=l(this,E),o=l(this,b).getFullYear();let d=new Date(o,1,1);d.setFullYear(d.getFullYear()-2);const h=d.getFullYear();d.setFullYear(d.getFullYear()+t.length-1);const u=d.getFullYear();this.setTitle(h+"-"+u),t.forEach((m,f)=>{const w=h+f;m.querySelector("span").textContent=w,m.dataset.year=w.toString(),r.checkClass(m,"selected",i===w),r.checkClass(m,"today",e===w),this._props.enableYear&&r.checkClass(m,"disabled",!a(this,b1,g3).call(this,w))})}setYearEvent(){l(this,Y1)||(this._elements.yearContent.querySelectorAll("td").forEach(t=>{t.addEventListener("click",()=>{let e=t.dataset.year;a(this,b1,g3).call(this,e)&&(l(this,b).setFullYear(e),this._props.yearOnly?(p(this,E,parseInt(e)),this.selectDate(t)):this.changeViewToMonth())})}),p(this,Y1,!0))}changeViewToMonth(){this.currentView="month",this.setTitle(),a(this,I2,Ft).call(this,"month"),this.setMonthContent()}setMonthContent(){const t=new Date().getFullYear(),e=new Date().getMonth(),i=l(this,E),o=l(this,A),d=l(this,b).getFullYear();this._elements.monthContent.querySelectorAll("td").forEach((u,m)=>{if(u.querySelector("span").textContent=this._props.months[m].substr(0,2),u.dataset.year=d.toString(),u.dataset.month=(m+1).toString(),r.checkClass(u,"selected",i===d&&o===m),r.checkClass(u,"today",t===d&&e===m),l(this,j2)){const f=u.dataset.month;r.checkClass(u,"disabled",!a(this,L1,f3).call(this,d+"-"+f))}})}setMonthEvent(){l(this,q1)||(this._elements.monthContent.querySelectorAll("td").forEach(t=>{t.addEventListener("click",()=>{let e=t.dataset.year,i=t.dataset.month;a(this,L1,f3).call(this,e+"-"+i)&&(l(this,b).setFullYear(e),l(this,b).setMonth(parseInt(i)-1),this._props.monthOnly?(p(this,E,parseInt(e)),p(this,A,parseInt(i)-1),this.selectDate(t)):this._props.weekOnly?this.changeViewToWeek(e,i):this.changeViewToDate(e,i))})}),p(this,q1,!0))}changeViewToDate(){this.currentView="date",this.setTitle(),a(this,I2,Ft).call(this,"date"),this.setDateContent()}setWeekTitle(){this._elements.dateContent.querySelectorAll("th").forEach((e,i)=>{i===0?r.checkClass(e,"hide",!this._props.weekOnly):e.textContent=this._props.daysTitles[(i-1+this._props.weekBegin)%7]})}setDateContent(){const t=z(new Date).getTime(),e=this.getSelectedDate(),i=e?e.getTime():null,o=z(l(this,b),!0);o.setDate(1);const d=b2(o),h=(7+d.getDay()+1-this._props.weekBegin)%7;d.setDate(d.getDate()-h),this._elements.dateContent.querySelectorAll("td").forEach((m,f)=>{f%8!==0&&(d.setDate(d.getDate()+1),m.querySelector("span").textContent=d.getDate().toString(),m.dataset.dateValue=V(d,"YYYY-MM-DD"),r.checkClass(m,"selected",d.getTime()===i),r.checkClass(m,"today",d.getTime()===t),r.checkClass(m,"other",d.getMonth()!==l(this,b).getMonth()),l(this,r2)&&r.checkClass(m,"disabled",!a(this,K,v2).call(this,d)))})}setDateEvent(){l(this,U1)||(this._elements.dateContent.querySelectorAll("td").forEach((t,e)=>{e%8!==0&&t.addEventListener("click",()=>{let i=t.dataset.dateValue;if(a(this,K,v2).call(this,i)){const o=i.split("-");l(this,b).setFullYear(o[0]),l(this,b).setMonth(parseInt(o[1])-1),l(this,b).setDate(o[2]),p(this,E,parseInt(o[0])),p(this,A,parseInt(o[1])-1),p(this,$,parseInt(o[2])),this._props.dateOnly||(a(this,rt,f4).call(this),a(this,f1,C3).call(this),a(this,y2,D1).call(this)),this.selectDate(t)}})}),p(this,U1,!0))}changeViewToWeek(){this.currentView="week",this.setTitle(),a(this,I2,Ft).call(this,"week"),this.setWeekContent()}setWeekContent(){const t=z(new Date).getTime(),e=z(l(this,b),!0);e.setDate(1);const i=b2(e),o=(7+i.getDay()+1-this._props.weekBegin)%7;i.setDate(i.getDate()-o),this._elements.dateContent.querySelectorAll("td").forEach((h,u)=>{if(u%8===0){const m=b2(i);m.setDate(m.getDate()+1);const f=m.getFullYear(),w=Q4(m,this._props.weekBegin);h.querySelector("span").textContent=w,h.parentNode.dataset.weekNumber=w,h.parentNode.dataset.year=f,r.removeClass(h,"hide"),r.checkClass(h.parentNode,"week-row-selected",f===l(this,E)&&w===l(this,h1))}else i.setDate(i.getDate()+1),h.querySelector("span").textContent=i.getDate().toString(),h.dataset.dateValue=V(i,"YYYY-MM-DD"),r.checkClass(h,"today",i.getTime()===t),r.checkClass(h,"other",i.getMonth()!==l(this,b).getMonth()),l(this,r2)&&r.checkClass(h,"disabled",!a(this,K,v2).call(this,i))})}setWeekEvent(){l(this,J1)||(this._elements.dateContent.querySelectorAll("tr").forEach((t,e)=>{t.addEventListener("mouseenter",()=>{if(l(this,L2)){const i=t.querySelector("td:nth-child(2)").dataset.dateValue,o=t.querySelector("td:last-child").dataset.dateValue;a(this,ot,v4).call(this,i,o)&&r.addClass(t,"week-row-over")}else r.addClass(t,"week-row-over")}),t.addEventListener("mouseleave",()=>{r.removeClass(t,"week-row-over")}),t.addEventListener("click",()=>{const i=t.querySelector("td:nth-child(2)").dataset.dateValue,o=t.querySelector("td:last-child").dataset.dateValue;l(this,L2)?a(this,ot,v4).call(this,i,o)&&this.selectWeek(t,i,o):this.selectWeek(t,i,o)})}),p(this,J1,!0))}selectWeek(t,e,i){const o=t.parentNode.querySelector("tr.week-row-selected");o&&r.removeClass(o,"week-row-selected"),r.addClass(t,"week-row-selected");const[d,h,u]=e.split("-");l(this,b).setFullYear(d),l(this,b).setMonth(parseInt(h)-1),l(this,b).setDate(u),p(this,E,parseInt(d)),p(this,A,parseInt(h)-1),p(this,$,parseInt(u));const m=t.dataset.weekNumber;p(this,h1,parseInt(m)),this.dispatchWeekEvent(m,e,i)}setHourEvent(){if(l(this,X1))return;const t=()=>{l(this,C1)||a(this,v1,m3).call(this,this._elements.hourContent),p(this,C1,!1)};this._elements.hourContent.addEventListener("scroll",()=>{d1(t,100,this)}),this._elements.hourContent.querySelectorAll("li").forEach((e,i)=>{const o=i.toString().padStart(2,"0");e.dataset.hour=o,e.addEventListener("click",()=>{a(this,R2,zt).call(this,o)&&(l(this,b).setHours(o),p(this,T,parseInt(o)),a(this,f1,C3).call(this),a(this,y2,D1).call(this),this.selectTime(e))})}),p(this,X1,!0)}setMinuteEvent(){if(l(this,G1))return;const t=()=>{l(this,m1)||a(this,v1,m3).call(this,this._elements.minuteContent),p(this,m1,!1)};this._elements.minuteContent.addEventListener("scroll",()=>{d1(t,100,this)}),this._elements.minuteContent.querySelectorAll("li").forEach((e,i)=>{const o=i.toString().padStart(2,"0");e.dataset.minute=o,e.addEventListener("click",()=>{a(this,B2,Nt).call(this,o)&&(l(this,b).setMinutes(o),p(this,j,parseInt(o)),a(this,y2,D1).call(this),this.selectTime(e))})}),p(this,G1,!0)}setSecondEvent(){if(l(this,K1))return;const t=()=>{l(this,g1)||a(this,v1,m3).call(this,this._elements.secondContent),p(this,g1,!1)};this._elements.secondContent.addEventListener("scroll",()=>{d1(t,100,this)}),this._elements.secondContent.querySelectorAll("li").forEach((e,i)=>{const o=i.toString().padStart(2,"0");e.dataset.second=o,e.addEventListener("click",()=>{a(this,V2,Rt).call(this,o)&&(l(this,b).setSeconds(o),p(this,I,parseInt(o)),this.selectTime(e))})}),p(this,K1,!0)}setRange(){p(this,p1,null),p(this,j2,null),p(this,r2,null),p(this,L2,null),p(this,D2,null),p(this,F2,null),p(this,$2,null),this._props.datetimeRange&&this._props.datetimeRange.length?(p(this,u1,a1(`return ${this._props.datetimeRange}`)),this.setDatetimeRange()):(this._props.dateRange&&this._props.dateRange.length&&(p(this,Q1,a1(`return ${this._props.dateRange}`)),this.setDateRange()),this._props.timeRange&&this._props.timeRange.length&&(p(this,tt,a1(`return ${this._props.timeRange}`)),this.setTimeRange()))}setDatetimeRange(){const t=l(this,u1),e=v(t[0])!=="array"?[t]:t;!e[0][0]&&!e[0][1]||(this.setDateRange(l(this,u1)),p(this,t2,e.map(i=>[x.parseDate(i[0]),x.parseDate(i[1])])),p(this,e2,e.map(i=>[z(i[0],!0),z(i[1],!0)])),p(this,D2,i=>{if(!i)return[0,23];const o=z(i,!0),d=[];for(let h=0;h<l(this,e2).length;h++){const u=l(this,e2)[h],m=a(this,l2,i1).call(this,u[0],o),f=a(this,l2,i1).call(this,o,u[1]);(m||f)&&d.push([m?l(this,t2)[h][0].getHours():0,f?l(this,t2)[h][1].getHours():23])}return d.length?x.RangeArrayUtils.union(d):[0,23]}),p(this,F2,(i,o)=>{if(!i)return[0,59];const d=z(i,!0),h=[];for(let u=0;u<l(this,e2).length;u++){const m=l(this,e2)[u],f=l(this,t2)[u],w=a(this,l2,i1).call(this,m[0],d)&&o===f[0].getHours(),s1=a(this,l2,i1).call(this,d,m[1])&&o===f[1].getHours();(w||s1)&&h.push([w?f[0].getMinutes():0,s1?f[1].getMinutes():59])}return h.length?x.RangeArrayUtils.union(h):[0,59]}),p(this,$2,(i,o,d)=>{if(!i)return[0,59];const h=z(i,!0),u=[];for(let m=0;m<l(this,e2).length;m++){const f=l(this,e2)[m],w=l(this,t2)[m],s1=a(this,l2,i1).call(this,f[0],h)&&o===w[0].getHours()&&d===w[0].getMinutes(),u3=a(this,l2,i1).call(this,h,f[1])&&o===w[1].getHours()&&d===w[1].getMinutes();(s1||u3)&&u.push([s1?w[0].getSeconds():0,u3?w[1].getSeconds():59])}return u.length?x.RangeArrayUtils.union(u):[0,59]}))}setDateRange(t=l(this,Q1)){const e=v(t[0])!=="array"?[t]:t;!e[0][0]&&!e[0][1]||(p(this,W,e.map(i=>[z(i[0],!0),F7(i[1],!0)])),p(this,p1,i=>{const o=new Date(i+"-01-01"),d=new Date(i+"-12-31");for(let h=0;h<l(this,W).length;h++){const u=l(this,W)[h];if(x.RangeArrayUtils.isIntersection(u,[o,d]))return!0}return!1}),p(this,j2,i=>{const o=new Date(i+"-01"),d=D7(...i.split("-"));for(let h=0;h<l(this,W).length;h++){const u=l(this,W)[h];if(x.RangeArrayUtils.isIntersection(u,[o,d]))return!0}return!1}),p(this,r2,i=>{const o=z(i,!0);for(let d=0;d<l(this,W).length;d++){const h=l(this,W)[d];if(!h[0]&&a(this,z2,$t).call(this,o,h[1])||!h[1]&&a(this,z2,$t).call(this,h[0],o)||a(this,z2,$t).call(this,h[0],o)&&a(this,z2,$t).call(this,o,h[1]))return!0}return!1}),p(this,L2,(i,o)=>{do{if(l(this,r2).call(this,i))return!0;i=i.setDate(i.getDate()+1)}while(i<=o);return!1}))}setTimeRange(){const t=l(this,tt),e=v(t[0])!=="array"?[t]:t;!e[0][0]&&!e[0][1]||(p(this,Q,e.map(i=>[i[0]?new Date("2020-01-01 "+i[0]):null,i[1]?new Date("2020-01-01 "+i[1]):null])),p(this,D2,i=>{if(l(this,Z2))return l(this,Z2);const o=[];for(let d=0;d<l(this,Q).length;d++){const h=l(this,Q)[d];o.push([h[0]?h[0].getHours():0,h[1]?h[1].getHours():23])}return p(this,Z2,x.RangeArrayUtils.union(o)),l(this,Z2)}),p(this,F2,(i,o)=>{const d=[];for(let h=0;h<l(this,Q).length;h++){const u=l(this,Q)[h],m=u[0]&&o===u[0].getHours(),f=u[1]&&o===u[1].getHours();(m||f)&&d.push([m?u[0].getMinutes():0,f?u[1].getMinutes():59])}return d.length?x.RangeArrayUtils.union(d):[0,59]}),p(this,$2,(i,o,d)=>{const h=[];for(let u=0;u<l(this,Q).length;u++){const m=l(this,Q)[u],f=m[0]&&o===m[0].getHours()&&d===m[0].getMinutes(),w=m[1]&&o===m[1].getHours()&&d===m[1].getMinutes();(f||w)&&h.push([f?m[0].getSeconds():0,w?m[1].getSeconds():59])}return h.length?x.RangeArrayUtils.union(h):[0,59]}))}};Q=new WeakMap,Z2=new WeakMap,W=new WeakMap,t2=new WeakMap,e2=new WeakMap,b=new WeakMap,E=new WeakMap,A=new WeakMap,$=new WeakMap,T=new WeakMap,j=new WeakMap,I=new WeakMap,Y1=new WeakMap,q1=new WeakMap,U1=new WeakMap,X1=new WeakMap,G1=new WeakMap,K1=new WeakMap,J1=new WeakMap,h1=new WeakMap,L2=new WeakMap,p1=new WeakMap,j2=new WeakMap,r2=new WeakMap,D2=new WeakMap,F2=new WeakMap,$2=new WeakMap,_2=new WeakMap,u1=new WeakMap,Q1=new WeakMap,tt=new WeakMap,C1=new WeakMap,m1=new WeakMap,g1=new WeakMap,t4=new WeakMap,et=new WeakSet,u4=function(){return this._props.yearOnly&&"year"||this._props.monthOnly&&"month"||this._props.dateOnly&&"date"||this._props.weekOnly&&"week"||this._props.timeOnly&&"time"||this._props.view},c5=new WeakMap,nt=new WeakSet,C4=function(){if(!this._props.value)return;const t=v(this._props.value)==="date"?V(this._props.value,this.getDefaultFormat()):this._props.value;l(this,c5).some(e=>e(t)),isNaN(l(this,E))||l(this,b).setFullYear(l(this,E)),isNaN(l(this,A))||l(this,b).setMonth(l(this,A)),isNaN(l(this,$))||l(this,b).setDate(l(this,$)),isNaN(l(this,T))||l(this,b).setHours(l(this,T)),isNaN(l(this,j))||l(this,b).setMinutes(l(this,j)),isNaN(l(this,I))||l(this,b).setSeconds(l(this,I))},st=new WeakSet,m4=function(){p(this,b,a(this,h5,O9).call(this))},h5=new WeakSet,O9=function(){var t,e;if(this._props.baseDate&&a(this,K,v2).call(this,this._props.baseDate))return x.parseDate(this._props.baseDate,!0);if(a(this,K,v2).call(this,new Date))return new Date;if((t=this._props)!=null&&t.datetimeRange.length)return b2(l(this,t2)[0][0]||l(this,t2)[0][1]);if((e=this._props)!=null&&e.dateRange.length)return b2(l(this,W)[0][0]||l(this,W)[0][1])},w2=new WeakSet,j1=function(){r.checkClass(this._elements.left,"hide",this._props.timeOnly),r.checkClass(this._elements.yearContent,"hide",this.currentView!=="year"),r.checkClass(this._elements.monthContent,"hide",this.currentView!=="month"),r.checkClass(this._elements.dateContent,"hide",this.currentView!=="date"),r.checkClass(this._elements.right,"hide",this._props.yearOnly||this._props.monthOnly||this._props.dateOnly||this._props.weekOnly),r.checkClass(this._elements.time,"timeWithClean",this._props.cleanEnable&&this._props.timeOnly),r.checkClass(this._elements.secondContent,"hide",!this._props.secondEnable),r.checkClass(this._elements.line,"hide",this._props.yearOnly||this._props.monthOnly||this._props.dateOnly||this._props.weekOnly||this._props.timeOnly),r.checkClass(this._elements.clean,"hide",!this._props.cleanEnable),r.checkClass(this._elements.cleanTime,"hide",!this._props.cleanEnable||!this._props.timeOnly),r.checkClass(this._elements.today,"hide",!this._props.todayEnable||!a(this,K,v2).call(this,new Date))},it=new WeakSet,g4=function(){a(this,p5,P9).call(this),a(this,u5,Z9).call(this),a(this,y2,D1).call(this)},I2=new WeakSet,Ft=function(t){r.checkClass(this._elements.yearContent,"hide",t!=="year"),r.checkClass(this._elements.monthContent,"hide",t!=="month"),r.checkClass(this._elements.dateContent,"hide",!["date","week"].includes(t))},rt=new WeakSet,f4=function(){this._elements.hourContent.querySelectorAll("li").forEach((t,e)=>{const i=a(this,R2,zt).call(this,e);r.checkClass(t,"disabled",!i),!i&&l(this,T)===e&&(p(this,T,null),r.removeClass(t,"selected"))})},p5=new WeakSet,P9=function(){this._elements.hourContent.querySelectorAll("li").forEach((t,e)=>{r.checkClass(t,"selected",l(this,T)===e),l(this,T)===e&&setTimeout(()=>{e!==0&&p(this,C1,!0),t.scrollIntoView({block:"nearest"})},100)})},f1=new WeakSet,C3=function(){this._elements.minuteContent.querySelectorAll("li").forEach((t,e)=>{const i=a(this,B2,Nt).call(this,e);r.checkClass(t,"disabled",!i),!i&&l(this,j)===e&&(p(this,j,null),r.removeClass(t,"selected"))})},u5=new WeakSet,Z9=function(){this._elements.minuteContent.querySelectorAll("li").forEach((t,e)=>{r.checkClass(t,"selected",l(this,j)===e),l(this,j)===e&&setTimeout(()=>{e!==0&&p(this,m1,!0),t.scrollIntoView({block:"nearest"})},100)})},y2=new WeakSet,D1=function(){this._props.secondEnable&&this._elements.secondContent.querySelectorAll("li").forEach((t,e)=>{const i=a(this,V2,Rt).call(this,e);r.checkClass(t,"disabled",!i),!i&&l(this,I)===e&&(p(this,I,null),r.removeClass(t,"selected"))})},e4=new WeakSet,Ln=function(){this._props.secondEnable&&this._elements.secondContent.querySelectorAll("li").forEach((t,e)=>{r.checkClass(t,"selected",l(this,I)===e),l(this,I)===e&&setTimeout(()=>{e!==0&&p(this,g1,!0),t.scrollIntoView({block:"nearest"})},100)})},v1=new WeakSet,m3=function(t){const e=t.querySelector("li"),i=r.getSize(e).y;t.scrollTop%i<i/2?t.scrollTop=Math.floor(t.scrollTop/i)*i:t.scrollTop=Math.ceil(t.scrollTop/i)*i},n4=new WeakSet,_n=function(t,e){return t>e||t-e===0},z2=new WeakSet,$t=function(t,e){return t<e||t-e===0},l2=new WeakSet,i1=function(t,e){return t-e===0},N2=new WeakSet,It=function(t,...e){return!t||v(t)!=="function"||t(...e)},b1=new WeakSet,g3=function(t){return a(this,N2,It).call(this,l(this,p1),t)},L1=new WeakSet,f3=function(t){return a(this,N2,It).call(this,l(this,j2),t)},K=new WeakSet,v2=function(t){return a(this,N2,It).call(this,l(this,r2),x.parseDate(t))},ot=new WeakSet,v4=function(t,e){return a(this,N2,It).call(this,l(this,L2),x.parseDate(t),x.parseDate(e))},C5=new WeakSet,j9=function(t){const e=l(this,D2);return e&&v(e)==="function"?e(x.parseDate(t)):[0,23]},R2=new WeakSet,zt=function(t,e){t=parseInt(t);const i=a(this,C5,j9).call(this,e||this.getSelectedDate());if(!i||!i.length||i[0]===0&&i[1]===23)return!0;if(v(i[0])==="array")for(let o=0;o<i.length;o++){const d=i[o];if(d[0]<=t&&t<=d[1])return!0}else if(i[0]<=t&&t<=i[1])return!0;return!1},m5=new WeakSet,D9=function(t,e){const i=l(this,F2);return i&&v(i)==="function"?i(x.parseDate(t),e):[0,59]},B2=new WeakSet,Nt=function(t,e=l(this,T),i){t=parseInt(t);const o=a(this,m5,D9).call(this,i||this.getSelectedDate(),e);if(!o||!o.length||o[0]===0&&o[1]===59)return!0;if(v(o[0])==="array")for(let d=0;d<o.length;d++){const h=o[d];if(h[0]<=t&&t<=h[1])return!0}else if(o[0]<=t&&t<=o[1])return!0;return!1},g5=new WeakSet,F9=function(t,e,i){const o=l(this,$2);return o&&v(o)==="function"?o(x.parseDate(t),e,i):[0,59]},V2=new WeakSet,Rt=function(t,e=l(this,j),i=l(this,T),o){t=parseInt(t);const d=a(this,g5,F9).call(this,o||this.getSelectedDate(),i,e);if(!d||!d.length||d[0]===0&&d[1]===59)return!0;if(v(d[0])==="array")for(let h=0;h<d.length;h++){const u=d[h];if(u[0]<=t&&t<=u[1])return!0}else if(d[0]<=t&&t<=d[1])return!0;return!1},C(x,"prop",{view:"datetime",baseDate:null,yearOnly:!1,monthOnly:!1,dateOnly:!1,weekOnly:!1,timeOnly:!1,secondEnable:!0,cleanEnable:!0,todayEnable:!0,datetimeRange:"",dateRange:"",timeRange:"",value:"",format:"",weekBegin:1,months:["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],daysTitles:["日","一","二","三","四","五","六"],clean:"清除",year:"年",month:"月",date:"日",hour:"时",minute:"分",second:"秒",week:"周"});let z1=x;z1.RangeArrayUtils={complementary(s,n,t){if(!s)return s;const e=this.getRangeObject(s);if(!n||n.length===0)return this.parse([e],t);const i=this.union(n),o={};if(i[0][0]>e.start)o.start=e.start;else if(e.end>i[0][1])o.start=i[0][1],i.shift();else return[];const d=[];for(;i.length>0;){if(i[0][0]>=e.end)return o.end=e.end,d.push({...o}),this.parse(d,t);if(e.end<=i[0][1])return o.end=i[0][0],d.push({...o}),this.parse(d,t);o.end=i[0][0],d.push({...o}),o.start=i[0][1],i.shift()}return o.end=e.end,d.push({...o}),this.parse(d,t)},union(s,n){if(!s||s.length===0)return s;const t=s.map(d=>this.getRangeObject(d)).sort((d,h)=>d.start-h.start),e=[];let i=t.shift(),o;for(;t.length>0;)o=t.shift(),this.isIntersection(i,o)?i.end=Math.max(i.end,o.end):(e.push({...i}),i=o);return o?this.isIntersection(i,o)?(i.end=Math.max(i.end,o.end),e.push({...i})):e.push({...i}):e.push({...i}),this.parse(e,n)},intersection(s,n){if(!s||s.length===0)return s;if(s.length===1)return s[1];const t=s.map(i=>this.getRangeObject(i)).sort((i,o)=>i.start-o.start),e=t.shift();for(;t.length>0;){const i=t.shift();if(this.isIntersection(e,i))e.start=i.start,e.end=Math.min(e.end,i.end);else return[]}return n&&n==="date"?[new Date(e.start),new Date(e.end)]:[e.start,e.end]},isIntersection(s,n){const t=v(s)==="object"?s:this.getRangeObject(s),e=v(n)==="object"?n:this.getRangeObject(n);return!(t.start>e.end||e.start>t.end)},parse(s,n){return s.map(t=>n&&n==="date"?[new Date(t.start),new Date(t.end)]:[t.start,t.end])},getRangeObject(s){if(s[0]&&s[1])return{start:Math.min(s[0],s[1]),end:Math.max(s[0],s[1])};if(!s[0]&&s[1])return{start:-1/0,end:s[1]};if(s[0]&&!s[1])return{start:s[0],end:1/0}}};const R7=`<div class="content">\r
    <div class="top">\r
        <slot name="top-left"></slot>\r
        <div class="prev ooicon-arrow_back"></div>\r
        <div class="date"></div>\r
        <div class="next ooicon-arrow_forward"></div>\r
        <slot name="top-right"></slot>\r
    </div>\r
    <div class="middle">\r
        <table border="0" cellpadding="0" cellspacing="1" class="dateContent">\r
            <thead>\r
            <tr>\r
                <th>周一</th>\r
                <th>周二</th>\r
                <th>周三</th>\r
                <th>周四</th>\r
                <th>周五</th>\r
                <th>周六</th>\r
                <th>周日</th>\r
            </tr>\r
            </thead>\r
            <tbody>\r
            <tr>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
            </tr>\r
            <tr>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
            </tr>\r
            <tr>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
            </tr>\r
            <tr>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
            </tr>\r
            <tr>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
            </tr>\r
            <tr>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
                <td><div><div class="cellTitle"><span></span></div><div class="cellContent"></div></div></td>\r
            </tr>\r
            </tbody>\r
        </table>\r
    </div>\r
</div>`,B7=`.content{display:flex;flex-direction:column;height:100%}.top{background-color:var(--oo-color-gray2);height:2.5em;display:flex;align-items:center;line-height:2.5em}.middle{flex:1}.title{cursor:pointer;color:var(--oo-color-text);font-weight:700;flex:1;padding-left:.8em}.title:hover{color:var(--oo-color-main)}.date{font-size:1.25em;cursor:pointer}.date:hover{color:var(--oo-color-main)}.prev{font-size:1.25em;cursor:pointer;padding-right:.5em;height:2.5em;line-height:2.5em;width:2.5em;text-align:right}.prev:hover{color:var(--oo-color-main)}.next{font-size:1.25em;cursor:pointer;padding-left:.5em;height:2.5em;line-height:2.5em;width:2.5em}.next:hover{color:var(--oo-color-main)}table{width:100%;border-top:1px solid var(--oo-color-gray-d1);border-left:1px solid var(--oo-color-gray-d1);table-layout:fixed}th{color:var(--oo-color-text);font-weight:400;background-color:#f7f7f7;line-height:2em;height:2em;border-right:1px solid var(--oo-color-gray-d1);border-bottom:1px solid var(--oo-color-gray-d1)}td{cursor:pointer;color:var(--oo-color-text);border-right:1px solid var(--oo-color-gray-d1);border-bottom:1px solid var(--oo-color-gray-d1);vertical-align:top}td.other{color:var(--oo-color-text3)}td>div:first-child{min-height:8em;padding:.25em}td div.cellTitle{display:flex;justify-content:space-between}td span:first-child{display:inline-block;text-align:center;border:1px solid transparent;height:1.5em;line-height:1.5em;width:1.5em;border-radius:1.5em}td.today span{color:var(--oo-color-text-white);background-color:var(--oo-color-main)}.disabled{color:var(--oo-color-text4)!important}.disabled span{border-color:transparent!important;background-color:transparent!important}.hide{display:none}
`;customElements.get("oo-calendar")||customElements.define("oo-calendar",z1);const V7=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    /*display: contents;*/\r
    padding: var(--oo-menu-padding);\r
}\r
`,W7=`
<div class="content">
    <slot name="items"></slot>
</div>
`,f5=class f5 extends k{constructor(){super();C(this,"_setPropMap",{css:()=>{this.useCss(this._props.css)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},isShow:()=>{this._props.isShow&&window.setTimeout(()=>{this.show()},10)}});c(this,x2,null);c(this,k2,null);c(this,E2,null)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_init(t,e,i,o){this._initialize(t,e||f5,i||W7,o||V7)}_setEvent(){super._setEvent(),this.shadowRoot.addEventListener("mousedown",t=>{t.stopPropagation()})}show(t,e){!l(this,x2)&&!l(this,k2)&&(p(this,x2,Object.assign({},{scale:0},t)),p(this,k2,Object.assign({},{scale:1},e)),r.setStyles(this,{scale:0}),r.addClass(this,"show"),window.setTimeout(()=>{r.addClass(this,"transition"),r.setStyles(this,{scale:1})},10),window.setTimeout(()=>{this.dispatchEvent(new Event("show"))},210),this._addHideEvent(),this.isHide=!1)}_addHideEvent(){l(this,E2)||p(this,E2,()=>{this.hide()}),document.addEventListener("mousedown",l(this,E2))}hide(){l(this,x2)&&l(this,k2)&&!this.isHide&&(r.setStyles(this,l(this,x2)),window.setTimeout(()=>{r.removeClass(this,"transition"),r.removeClass(this,"show"),r.setStyles(this,l(this,k2)??{}),p(this,k2,null),p(this,x2,null),this._afterHide(),this.dispatchEvent(new Event("hide"))},200),this._removeHideEvent(),this.isHide=!0)}_afterHide(){}_removeHideEvent(){l(this,E2)&&document.removeEventListener("mousedown",l(this,E2))}};x2=new WeakMap,k2=new WeakMap,E2=new WeakMap,C(f5,"prop",{css:"",cssLink:"",skin:"",isShow:!1});let Jt=f5;class k3 extends Jt{constructor(){super(),this._init("oo-menu",k3)}}customElements.get("oo-menu")||customElements.define("oo-menu",k3);const H7=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    --padding: 0.5em 0.714em 0.5em 0.714em;\r
    --radius: 0.2em;\r
    --border-color: var(--oo-color-gray-d);\r
    --shadow: 0 0 0.5em 0.125em var(--oo-color-gray-e);\r
    display: flex;\r
    width: 100%;\r
    justify-content: flex-start;\r
    align-items: center;\r
    flex-wrap: nowrap;\r
    padding: var(--padding);\r
    cursor: pointer;\r
    color: var(--oo-color-text2);\r
    white-space: nowrap;\r
    min-width: 10rem;\r
}\r
\r
.enabled:hover{\r
    background-color: var(--oo-color-light);\r
    color: var(--oo-color-main);\r
}\r
.disabled{\r
    opacity: 0.4;\r
    cursor: not-allowed;\r
}\r
`,Y7=`
<div class="menuItem content enabled">
    <slot name="left"></slot>
    <slot name="item"></slot>
    <slot name="right"></slot>
</div>
`,v5=class v5 extends k{constructor(){super();C(this,"_elements",{});C(this,"_setPropMap",{css:()=>{this.useCss(this._props.css)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},label:()=>{switch(this._props.type){case"command":this._elements.label&&(this._elements.label.textContent=this._props.label);break;case"checkbox":case"radio":this._elements.input&&(this._elements.input.text=this._props.label);break}},disabled:()=>{switch(r.removeClass(this._content,"disabled"),r.addClass(this._content,"enabled"),this._props.type){case"command":this._props.disabled&&(r.removeClass(this._content,"enabled"),r.addClass(this._content,"disabled"));break;case"checkbox":case"radio":this._elements.input.disabled=this._props.disabled,this._props.disabled&&r.removeClass(this._content,"enabled");break}},icon:t=>{this._elements.icon&&(r.removeClass(this._elements.icon,`ooicon-${t}`),this._props.icon&&r.addClass(this._elements.icon,`ooicon-${this._props.icon}`))},radiogroup:t=>{this._elements.radio&&(this._elements.radio.name=this._props.radiogroup)},type:t=>{t!==null&&t!==this._props.type&&this._renderItem()}});this._initialize("oo-menu-item",v5,Y7,H7)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_setEvent(){super._setEvent(),this.addEventListener("click",()=>{if(this.type==="command"){const t=r.getParent(this,"oo-menu");t&&t.hide()}})}_connected(){this.getAttribute("type")||this.setAttribute("type","command"),r.getParent(this,"oo-menu")&&this.setAttribute("slot","items"),this._renderItem()}_renderItem(){switch(r.empty(this),this._elements.icon=null,this._elements.label=null,this._elements.input=null,this._elements.hr=null,this.checkbox=null,this.radio=null,r.addClass(this._content,"enabled"),this._props.type){case"command":this._elements.icon=r("div.icon",{slot:"item"}),this._elements.label=r("div.label",{text:this._props.label,slot:"item"}),this._props.icon&&r.addClass(this._elements.icon,`ooicon-${this._props.icon}`),this.append(this._elements.icon),this.append(this._elements.label);break;case"checkbox":this._elements.input=r("oo-checkbox",{slot:"item"}),this.append(this._elements.input),this._elements.input.text=this._props.label,this.checkbox=this._elements.input;break;case"radio":this._elements.input=r("oo-radio",{slot:"item"}),this.append(this._elements.input),this._elements.input.text=this._props.label,this._elements.input.name=this._props.radiogroup,this.radio=this._elements.input;break;case"hr":this._elements.hr=r("hr",{slot:"item"}),this.append(this._elements.hr),r.removeClass(this._content,"enabled");break}}};C(v5,"prop",{css:"",cssLink:"",skin:"",icon:"",label:"",type:"command",disabled:!1,checked:!1,radiogroup:""});let E3=v5;customElements.get("oo-menu-item")||customElements.define("oo-menu-item",E3);class s9{constructor(n,t){c(this,b5);c(this,L5);c(this,_5);c(this,w5);C(this,"options",{css:"",cssLink:"",skin:""});this.options=Object.assign(this.options,t),this.target=n,this.container=this.options.container||r.getOffsetParent(n),this.area=this.options.area;const e=this.options.event||"click";this.target.addEventListener(e,()=>{this.show()}),this.target.addEventListener("mousedown",i=>{})}show(){this.menu||a(this,b5,$9).call(this),a(this,w5,N9).call(this),this.menu.show()}hide(){this.menu.hide()}destroy(){this.menu.remove()}}b5=new WeakSet,$9=function(){var n;this.menu=r("oo-menu",(n=this.options)==null?void 0:n.attr),this.container.append(this.menu),this.options.items&&this.options.items.length&&a(this,L5,I9).call(this),this.options.class&&r.addClass(this.menu,this.options.class)},L5=new WeakSet,I9=function(){this.options.items.forEach(n=>{a(this,_5,z9).call(this,n)})},_5=new WeakSet,z9=function(n){const t=document.createElement("oo-menu-item");this.menu.append(t),n==="-"?t.setAttribute("type","hr"):(Object.keys(n).forEach(e=>{e!=="command"?t.setAttribute(_(e),n[e]):t.addEventListener("click",n.command)}),t.addEventListener("click",e=>{this.menu.dispatchEvent(new CustomEvent("command",{detail:n}))}))},w5=new WeakSet,N9=function(){const n=r.getSize(this.target),t=r.getPosition(this.target,this.container),e=r.getSize(this.menu);let i=t.y+n.y,o=t.x,d="left",h="top";if(this.area){const u=r.getSize(this.area),m=r.getPosition(this.container,this.area);m.x+o+e.x>u.x&&(o=o-e.x+n.x,d="right"),m.y+i+e.y>u.y&&(i=i-e.y-n.y,h="bottom")}r.setStyles(this.menu,{top:i+"px",left:o+"px","transform-origin":`${d} ${h}`})};class i9 extends s9{constructor(t,e){super(t,e);c(this,y5);c(this,x5);c(this,k5);c(this,W2,null);c(this,s4,null);c(this,i4,null);C(this,"options",{css:"",cssLink:"",skin:"",view:"date",baseDate:null,yearOnly:!1,monthOnly:!1,dateOnly:!1,timeOnly:!1,weekOnly:!1,secondEnable:!0,cleanEnable:!1,todayEnable:!0,datetimeRange:"",dateRange:"",timeRange:"",format:"",weekBegin:1,value:""});this.options=Object.assign(this.options,e)}reset(t){if(this.options=Object.assign(this.options,t),this.calendar)for(let e in this.options)v(this.options[e])!=="null"&&this.calendar.setAttribute(_(e),this.options[e])}show(){this.menu||a(this,y5,R9).call(this),a(this,k5,V9).call(this),this.menu.show()}}W2=new WeakMap,s4=new WeakMap,i4=new WeakMap,y5=new WeakSet,R9=function(){var t;this.menu=r("oo-menu",(t=this.options)==null?void 0:t.attr),this.container.append(this.menu),p(this,W2,r("div",{slot:"items",styles:{height:"100%"}})),this.menu.append(l(this,W2)),a(this,x5,B9).call(this),this.options.class&&r.addClass(this.menu,this.options.class)},x5=new WeakSet,B9=function(){if(!this.calendar){let t="<oo-calendar";for(let e in this.options)v(this.options[e])!=="null"&&(t+=` ${_(e)}='${this.options[e]}'`);t+="></oo-calendar>",r.set(l(this,W2),"html",t),this.calendar=l(this,W2).firstElementChild,this.calendar.addEventListener("change",e=>{this.target.dispatchEvent(new CustomEvent("change",e))})}},k5=new WeakSet,V9=function(){const t=r.getSize(this.target),e=r.getPosition(this.target,this.container),i=r.getSize(this.menu);let o=e.y+t.y,d=e.x,h="left",u="top";if(this.area){const m=r.getSize(this.area),f=r.getPosition(this.container,this.area);f.x+d+i.x>m.x&&(d=d-i.x+t.x,h="right"),f.y+o+i.y>m.y&&(o=o-i.y-t.y,u="bottom")}r.setStyles(this.menu,{top:o+"px",left:d+"px","transform-origin":`${h} ${u}`})};function r9(s,n){return new i9(s,n)}const lt=class lt extends k{constructor(){super();C(this,"_elements",{content:null,top:null,prev:null,next:null,date:null,dateContent:null});c(this,r4,{});c(this,D,null);c(this,_1,null);C(this,"_setPropMap",{skin:()=>{this._useSkin(this._props.skin)},baseDate:()=>{this.setCurrentDate(),this.setDateContent()},weekBegin:()=>{this.setWeekTitle(),this.setDateContent()},$default:t=>{}});this._initialize("oo-calendar-view",lt,R7,B7)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}static parseDate(t,e){return t?v(t)==="date"?e?new Date(t.getTime()):t:new Date(t):null}_render(){this.setCurrentDate()}_connected(){this.setWeekTitle(),this.setDateContent()}_setEvent(){this._elements.prev.addEventListener("click",t=>{B(l(this,D),"month",-1),this.setDateContent(),this.changeDate()}),this._elements.next.addEventListener("click",t=>{B(l(this,D),"month",1),this.setDateContent(),this.changeDate()}),this._elements.date.addEventListener("change",t=>{p(this,D,t.detail.date),this._elements.date.textContent=V(l(this,D),this._props.format),this.setDateContent()})}setCurrentDate(){p(this,D,this._props.baseDate?lt.parseDate(this._props.baseDate,!0):new Date),this.changeDate()}changeDate(){this._elements.date.textContent=V(l(this,D),this._props.format),l(this,_1)?l(this,_1).reset({value:V(l(this,D),"YYYY-MM")}):p(this,_1,r9(this._elements.date,{monthOnly:!0,format:this._props.format,container:document.body,value:V(l(this,D),"YYYY-MM")})),this.dispatchEvent(new CustomEvent("change",{detail:{date:l(this,D),value:V(l(this,D),"YYYY-MM")}}))}setWeekTitle(){const t=this._elements.dateContent.querySelectorAll("th");this._props.daysTitles.forEach((e,i)=>{t[(7+i-this._props.weekBegin)%7].textContent=e})}setDateContent(){const t=z(new Date).getTime(),e=z(l(this,D),!0);e.setDate(1);const i=this._elements.dateContent.querySelectorAll("td"),o=new Date(e.getTime());o.setDate(o.getDate()-1);const d=(7+o.getDay()+1-this._props.weekBegin)%7,h=(u,m)=>{u.querySelectorAll("slot").forEach(u3=>u3.remove());const f=V(m,"YYYY-MM-DD"),w=r("slot",{name:`title-${f}`});u.querySelector("div.cellTitle").appendChild(w);const s1=r("slot",{name:`content-${f}`});u.querySelector("div.cellContent").appendChild(s1),u.querySelector("span:first-child").textContent=V(m,"D"),u.dataset.dateValue=f,r.checkClass(u,"today",m.getTime()===t),r.checkClass(u,"other",m.getMonth()!==l(this,D).getMonth())};for(let u=d-1;u>=0;u--)h(i[u],o),o.setDate(o.getDate()-1);for(let u=d;u<i.length;u++)h(i[u],e),e.setDate(e.getDate()+1)}};r4=new WeakMap,D=new WeakMap,_1=new WeakMap,C(lt,"prop",{baseDate:"",weekBegin:1,format:"YYYY年MM月",months:["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],daysTitles:["周日","周一","周二","周三","周四","周五","周六"]});let M3=lt;const q7=`.content {\r
    --border-color: var(--oo-color-text3);\r
    --border-radius: var(--oo-default-radius);\r
    --current-bg: var(--oo-color-main);\r
    --current-color: var(--oo-color-text-white);\r
}\r
\r
.capsulae{\r
    display: flex;\r
    align-items: center;\r
    justify-content: flex-start;\r
}\r
.capsulae>div{\r
    padding: 0.357em 0.714em;\r
    cursor: pointer;\r
    border-left: 1px solid var(--border-color);\r
    border-top: 1px solid var(--border-color);\r
    border-bottom: 1px solid var(--border-color);\r
    white-space: nowrap;\r
    transition: background-color 0.2s, color 0.2s;\r
}\r
\r
.capsulae>div:first-child{\r
    border-top-left-radius: var(--border-radius);\r
    border-bottom-left-radius: var(--border-radius);\r
}\r
\r
.capsulae>div:last-child{\r
    border-top-right-radius: var(--border-radius);\r
    border-bottom-right-radius: var(--border-radius);\r
    border-right: 1px solid var(--border-color);\r
}\r
.capsulae>div.current{\r
    background-color: var(--current-bg);\r
    color: var(--current-color);\r
}\r
`,U7='<div class="capsulae content"></div>',M5=class M5 extends k{constructor(){super();c(this,E5);C(this,"_setPropMap",{skin:()=>{this._useSkin(this._props.skin)}});this._init("oo-capsulae")}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_init(t,e,i,o){this._initialize(t,e||M5,i||U7,o||q7)}_render(){new MutationObserver(function(e){e.forEach(i=>{i.addedNodes.forEach(o=>{o.nodeType===Node.ELEMENT_NODE&&this._content.appendChild(o)})})}).observe(this,{subtree:!1,childList:!0,attributes:!1,characterData:!1}),a(this,E5,W9).call(this)}};E5=new WeakSet,W9=function(){let t=this.firstElementChild;for(;t;)this._content.appendChild(t),t=this.firstElementChild},C(M5,"prop",{skin:""});let S3=M5;const X7=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    --padding:  0.8rem;\r
    --color: var(--oo-color-text2);\r
    --font-size: 0.875rem;\r
    --border: 1px solid var(--oo-color-gray1);\r
    --radius: var(--oo-area-radius);\r
    --shadow: 0 0 0.625rem 0 var(--oo-color-gray-b);\r
}\r
\r
.content{\r
    padding: var(--padding);\r
    color: var(--color);\r
    font-size: var(--font-size);\r
    border: var(--border);\r
    border-radius: var(--radius);\r
    box-shadow: var(--shadow);\r
    display: none;\r
    max-width: 30em;\r
}\r
.line{\r
    display: flex;\r
    margin-bottom: 0.5rem;\r
}\r
.icon{\r
    width: 3rem;\r
    height: 3rem;\r
    background-position: center;\r
    background-repeat: no-repeat;\r
    background-size: cover;\r
    border-radius: 100%;\r
}\r
\r
.nameArea{\r
    width: calc(100% - 3rem);\r
    padding: 0 0.5rem;\r
}\r
.name{\r
    color: var(--oo-color-main);\r
    font-size: 0.875rem;\r
    margin-bottom: 0.4rem;\r
}\r
.application{\r
    color: var(--oo-color-text3);\r
    font-size: 0.875rem;\r
    margin-bottom: 0.5rem;\r
}\r
\r
.show{\r
    display: inline-block;\r
}\r
.description{\r
    word-break: break-all;\r
}\r
.process-icon{\r
    background-color: var(--oo-color-gray-b);\r
    color: var(--oo-color-text-white);\r
    display: flex;\r
    justify-content: center;\r
    align-items: center;\r
    font-size: 2.5em;\r
}\r
`;function wn(){}function G7(s){var n;return typeof s=="object"&&(s.dn||s.distinguishedName)||((n=s==null?void 0:s.includes)==null?void 0:n.call(s,"@"))}function A3(s){if(G7(s)){const t=(typeof s=="object"?s.dn||s.distinguishedName||"":s).split("@"),e=(t==null?void 0:t[2])||"unknown";return["I","P","U","G","R","UD","UA","PA","PP","UP","PROCESS"].includes(e)?e:"unknown"}}function Qt(s){if(Array.isArray(s))return s.flat(1/0).map(n=>Qt(n));{const n=s.split("@");return(n==null?void 0:n[0])||s}}function K7(s){if(Array.isArray(s))return s.flat(1/0).map(n=>Qt(n));{const n=s.split("@");return(n==null?void 0:n[1])||s}}function J7(s){return typeof s=="object"?s.dn||s.id||s.unique:s}function s2(s){return s.map(n=>typeof n=="object"?n.dn||n.id||n.unique:n)}async function Q7(s){return Array.isArray(s)?await o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({groupList:s2(s)}).then(n=>n.data):await o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({groupList:s2([s])}).then(n=>n.data[0])}async function t0(s){return Array.isArray(s)?await o2.Actions.load("x_organization_assemble_express").IdentityAction.listObject({identityList:s2(s)}).then(n=>n.data):await o2.Actions.load("x_organization_assemble_express").IdentityAction.listObject({identityList:s2([s]),referenceFlag:"true"}).then(n=>n.data[0])}async function e0(s){return Array.isArray(s)?await o2.Actions.load("x_organization_assemble_express").PersonAction.listObject({personList:s2(s)}).then(n=>n.data):await o2.Actions.load("x_organization_assemble_express").PersonAction.get(s).then(n=>n.data)}function t5(s){return s?`/x_organization_assemble_control/jaxrs/person/${encodeURI(J7(s))}/icon`:"/x_organization_assemble_personal/jaxrs/person/icon"}async function n0(s){return Array.isArray(s)?await o2.Actions.load("x_organization_assemble_express").RoleAction.listObject({roleList:s2(s)}).then(n=>n.data):await o2.Actions.load("x_organization_assemble_express").RoleAction.listObject({roleList:s2([s])}).then(n=>n.data[0])}async function o9(s){return Array.isArray(s)?await o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({unitList:s2(s)}).then(n=>n.data):await o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({unitList:s2([s])}).then(n=>n.data[0])}const l9="x_processplatform_assemble_surface";function a9(s,n){return Array.isArray(s)?s.map(t=>a9(t,n)):(s.dn||(s.dn=`${s.id}@${n.toUpperCase()}@${s.name}`),s)}async function s0(s,n,t,e,i,...o){const d=await o2.Actions.load(s);return a9(await d[n][t].apply(i,o),e)}const T3={async invoke(s,n,t,...e){return await s0(l9,s,n,t,this,...e)},async listApplication(){return await this.invoke("ApplicationAction","list","app")},async listProcess(s){return await this.invoke("ProcessAction","listWithApplication","process",s)},async getApplication(s){return await this.invoke("ApplicationAction","get","app",s)},async getProcess(s){return await this.invoke("ProcessAction","getProcess","process",s)},async getProcessDesign(s){return await this.invoke("ProcessAction","get","process",s)},async getProcessWithApp(s){return new Promise(n=>{window.setTimeout(async()=>{const t=K7(s),e=await o2.Actions.load(l9),i=await this.invoke("ProcessAction","get","process",t),o=await e.ApplicationAction.get(i.data.application);i.data.application=o.data,n(i.data)},500)})}},d9={I_html:`
<div class="content">
    <div class="line">
        <div class="icon"></div>
        <div class="nameArea">
            <div class="name"></div>
            <div class="unitName"></div>
        </div>
    </div>
    <div class="line">
        <div>组织：</div>
        <div class="unit"></div>
    </div>
    <div class="line">
        <div>职务：</div>
        <div class="duty"></div>
    </div>
</div>
`,P_html:`
<div class="content">
    <div class="line">
        <div class="icon"></div>
        <div class="nameArea">
            <div class="name"></div>
            <div class="unitName"></div>
        </div>
    </div>
    <div class="line">
        <div>组织：</div>
        <div class="unit"></div>
    </div>
    <div class="line">
        <div>职务：</div>
        <div class="duty"></div>
    </div>
</div>
`,U_html:`
<div class="content">
    <div class="name"></div>
    <div class="levelName"></div>
    <div class="description"></div>
</div>
`,UD_html:`
<div class="content">
    <div class="name"></div>
    <div class="levelName"></div>
    <div class="description"></div>
</div>
`,G_html:`
<div class="content">
    <div class="name"</div>
    <div class="description"></div>
</div>
`,R_html:`
<div class="content">
    <div class="name"></div>
    <div class="description"></div>
</div>
`,PROCESS_html:`
<div class="content">
    <div class="line">
        <div class="icon"></div>
        <div class="nameArea">
            <div class="name"></div>
            <div class="application"></div>
        </div>
    </div>
     <div class="line">
        <div class="description"></div>
    </div>
</div>
`,APP_html:`
<div class="content">
    <div class="line">
        <div class="icon"></div>
        <div class="nameArea">
            <div class="name"></div>
            <div class="application"></div>
        </div>
    </div>
     <div class="line">
        <div class="description"></div>
    </div>
</div>
`,other_html:`
<div class="content">
    <div class="name"></div>
</div>
`},H2=class H2 extends k{constructor(){super();c(this,at);c(this,S5);C(this,"_elements",{icon:null,name:null,unitName:null,unit:null,duty:null,levelName:null,description:null,phone:null,application:null});C(this,"isAsync",!0);C(this,"_setPropMap",{value:()=>{this._canRender()&&a(this,at,b4).call(this)},data:()=>{this._canRender()&&a(this,at,b4).call(this)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._canRender()&&this._useSkin(this._props.skin)}});c(this,w1,null);this._props=this._getProps(H2.prop),this._createProperties(H2)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_canRender(){return super._canRender(null,["value","data"])}async _render(){l(this,w1)&&clearTimeout(l(this,w1)),p(this,w1,setTimeout(()=>{a(this,S5,H9).call(this)},10))}async createICard(t){var d;const e=this._props.value||this._props.data.dn,i=await t0(e),o=t5(i.person);r.setStyle(this._elements.icon,"background-image",`url('${o}')`),this._elements.name.textContent=i.name,this._elements.unitName.textContent=i.woUnit.name,this._elements.unit.textContent=i.woUnit.levelName,this._elements.duty.textContent=(d=i.woUnitDutyList)==null?void 0:d.map(h=>`${h.name}(${h.woUnit.name})`).join(", ")}async createPCard(t){const e=this._props.value||this._props.data.dn,i=await e0(e),o=t5(i.dn||i.id);r.setStyle(this._elements.icon,"background-image",`url('${o}')`),this._elements.name.textContent=i.name,this._elements.unitName.textContent=i.woIdentityList.map(d=>d.woUnit.name).join(", "),this._elements.unit.innerHTML=i.woIdentityList.map(d=>`<div>${d.woUnit.levelName}</div>`).join(""),this._elements.duty.textContent=i.woIdentityList.map(d=>d.woUnitDutyList&&d.woUnitDutyList.length?d.woUnitDutyList.map(h=>`${h.name}(${d.woUnit.name})`).join(", "):null).filter(d=>d!=null&&d!=="").join(", ")}async createUCard(t){const e=v(t)==="object"?await Promise.resolve(t):await o9(t);this._elements.name.textContent=e.name,this._elements.levelName.textContent=e.levelName,this._elements.description.textContent=e.description}async createUDCard(t){const e=v(t)==="object"?await Promise.resolve(t):await o9(t);this._elements.name.textContent=e.name,this._elements.levelName.textContent=e.levelName,this._elements.description.textContent=e.description}async createGCard(t){const e=v(t)==="object"?await Promise.resolve(t):await Q7(t);this._elements.name.textContent=e.name,this._elements.description.textContent=e.description}async createRCard(t){const e=v(t)==="object"?await Promise.resolve(t):await n0(t);this._elements.name.textContent=e.name,this._elements.description.textContent=e.description}async createPROCESSCard(t){const e=v(t)==="object"?await(async()=>(v(t.application)==="object"||(t.application=await T3.getApplication(t.application)),t))():await T3.getProcessWithApp(t),i=e.application.icon||"";i?r.setStyle(this._elements.icon,"background-image",`url('data:image/png;base64,${i}')`):(r.addClass(this._elements.icon,"process-icon"),r.addClass(this._elements.icon,"ooicon-process")),this._elements.name.textContent=e.name,this._elements.description.innerHTML=e.description||`应用"${e.application.name}"中的流程"${e.name}"`,this._elements.application.textContent=e.application.name}async createAPPCard(t){const e=await T3.getApplication(t.id||t);e.icon?r.setStyle(this._elements.icon,"background-image",`url('data:image/png;base64,${e.icon}')`):(r.addClass(this._elements.icon,"process-icon"),r.addClass(this._elements.icon,"ooicon-computer")),this._elements.name.textContent=e.name,this._elements.application.textContent=e.applicationCategory||"未分类应用",this._elements.description.textContent=e.description}async createOtherCard(t){this._elements.name.textContent=v(t)==="object"?t.name:t}};at=new WeakSet,b4=function(){const t=this.shadowRoot||this.attachShadow({mode:"open"});r.empty(t);const e=A3(this._props.value||this._props.data),i=H2.cardTypys.includes(e)?d9[e+"_html"]:d9.other_html,o=e9(i,X7,"oo-card"+e,!0);this._useTemplate(o),this._render()},w1=new WeakMap,S5=new WeakSet,H9=async function(){if(this._props.value||Object.keys(this._props.data).length){const t=this._props.value||this._props.data,e=A3(t);await(this[`create${e}Card`]||this.createOtherCard).call(this,t),r.addClass(this._content,"show"),this.dispatchEvent(new CustomEvent("loaded"))}},C(H2,"prop",{value:"",data:{},border:"",cssLink:"",skin:""}),C(H2,"cardTypys",["I","P","U","UD","G","R","PROCESS","APP"]);let O3=H2;const i0=`<label class="content">\r
	<div class="button">\r
		<div class="check-icon">\r
			<div class="check">\r
				<div class="checkIcon"></div>\r
			</div>\r
		</div>\r
	</div>\r
	<div class="label">\r
		<div class="text"></div>\r
		<div class="slot">\r
			<slot name="label"></slot>\r
		</div>\r
	</div>\r
	<input class="input" required/>\r
</label>\r
`,r0=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    --border: var(--oo-color-gray-9);\r
    --check: var(--oo-color-text-white);\r
    --bg: var(--oo-color-text-white);\r
    --hover: var(--oo-color-main);\r
    --checked: var(--oo-color-main);\r
    --disabled: var(--oo-color-gray2);\r
    --radius: var(--oo-default-radius);\r
    --checked-bg: var(--oo-color-text-white);\r
    --checked-color: var(--oo-color-main);\r
\r
    --hover-bg: var(--oo-color-hover);\r
    --padding: 0;\r
    --margin: 0;\r
}\r
label {\r
    display: flex;\r
    align-items: center;\r
    cursor: pointer;\r
    word-break: keep-all;\r
    transition: background-color 0.2s;\r
    padding: var(--padding);\r
    margin: var(--margin);\r
    position: relative;\r
}\r
label:hover{\r
    /*background-color: var(--hover-bg);*/\r
    color: var(--hover);\r
}\r
\r
label.checked{\r
    /*background-color: var(--checked-bg);*/\r
}\r
.abc .label{\r
    padding: 10px;\r
}\r
\r
.button{\r
    border-radius: var(--radius);\r
    width: 1em;\r
    min-width: 1em;\r
    max-width: 1em;\r
    height: 1em;\r
    border: 0.1em solid var(--border);\r
    background-color: var(--bg);\r
    display: flex;\r
    justify-content: center;\r
    align-items: center;\r
    transition: border 0.2s, color 0.2s, background-color 0.2s;\r
    color: var(--oo-color-gray-9);\r
    position: relative;\r
}\r
.button:hover{\r
    border: 0.1em solid var(--hover);\r
}\r
.button.checked{\r
    color: var(--checked-color);\r
    border: 0.1em solid var(--checked);\r
    background-color: var(--checked-bg);\r
}\r
.button.disabled{\r
    opacity: 0.4;\r
    background-color: var(--disabled);\r
    cursor: not-allowed;\r
}\r
\r
.check-icon{\r
    width: 1em;\r
    height: 1em;\r
    margin: auto;\r
    position: absolute;\r
    display: flex;\r
    justify-content: center;\r
    align-items: center;\r
}\r
\r
.check{\r
    border-radius: var(--radius);\r
    width: 30%;\r
    height: 30%;\r
    display: flex;\r
    align-items: center;\r
    justify-content: center;\r
    color: var(--check);\r
    transition: width 0.2s, height 0.2s, background 0.2s;\r
}\r
.check>div{\r
    scale: 0.5;\r
}\r
.check.checked{\r
    width: 100%;\r
    height: 100%;\r
    color: var(--checked-bg);\r
    background-color: var(--checked);\r
}\r
.check.disabled{}\r
.check.checked.disabled{}\r
\r
.label{\r
    display: flex;\r
    align-items: center;\r
    padding-left: 0.3em;\r
    transition: color 0.2s;\r
    flex: auto;\r
}\r
\r
.label.checked{\r
    color: var(--checked-color);\r
}\r
.label.disabled{\r
    opacity: 0.4;\r
    cursor: not-allowed;\r
}\r
.label.readmode.checked{\r
    color: inherit;\r
}\r
.input{\r
    width: 1px;\r
    height: 1px;\r
    overflow: hidden;\r
    opacity: 0;\r
    position: absolute;\r
    bottom: 1px;\r
    left: 50%;\r
}\r
.slot{\r
    flex: auto;\r
}\r
br{\r
    height: 0;\r
}\r
`,A5=class A5 extends k{constructor(){super();C(this,"_elements",{label:null,button:null,text:null,slot:null,check:null,input:null,checkIcon:null});C(this,"_setPropMap",{text:()=>{this._elements.text.textContent=this._props.text},value:()=>{this._elements.input.value=this._props.value,this._elements.input.setAttribute("value",this._props.value)},name:t=>{t!==this._props.name&&(this._elements.input.name=this._props.name,this._elements.input.setAttribute("name",this._props.name),this.setAttribute("name",this._props.name))},checked:()=>{this._elements.input.checked=!!this._props.checked,this._render()},disabled:()=>{this._elements.input.disabled=!!this._props.disabled,this._render()},size:()=>{this._props.size?r.setStyle(this._elements.button,"font-size",this._props.size):r.setStyle(this._elements.button,"font-size","auto")},skin:()=>{this._useSkin(this._props.skin)},readmode:()=>{this._props.readmode?(this.checked||r.addClass(this,"hide"),r.addClass(this._elements.button,"hide"),r.addClass(this._elements.label,"readmode"),this._elements.input.disabled=!0):(r.removeClass(this,"hide"),r.removeClass(this._elements.button,"hide"),r.removeClass(this._elements.label,"readmode"),this._props.disabled||(this._elements.input.disabled=!1))},$default:()=>{}});C(this,"group",null)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_init(t,e,i,o){this._initialize(t,e||A5,i||i0,o||r0)}_setEvent(){super._setEvent(),this._elements.input.addEventListener("change",t=>{this.checked=this._elements.input.checked,this.dispatchEvent(new Event("change",t))})}_unchecked(){this._elements.input.checked=!1,this._props.checked=!1,this._render()}_render(){let t=this.firstChild;for(;t;)this._elements.slot.appendChild(t),t=this.firstChild;const e=this._elements.input.checked;r.checkClass(this._elements.button,"checked",e),r.checkClass(this._elements.check,"checked",e),r.checkClass(this._elements.label,"checked",e),r.checkClass(this._content,"checked",e),r.checkClass(this,"checked",e);const i=this._elements.input.disabled;r.checkClass(this._elements.button,"disabled",i),r.checkClass(this._elements.check,"disabled",i),r.checkClass(this._elements.label,"disabled",i),this._setPropMap.text()}_connected(){this._render();const t=this.tagName.toLowerCase()+"-group",e=r.getParent(this,t);e&&(this.group=e,setTimeout(()=>{var i;(i=e._appendItem)==null||i.call(e,this)},10))}_disconnected(){if(this.group){const t=this.group;this.group=null,t._removeItem(this)}}get checked(){return this._elements.input.checked}set checked(t){const e=this.checked!==t;this._elements.input.checked=!!t,this._render(),e&&this.dispatchEvent(new Event("change"))}};C(A5,"prop",{checked:!1,disabled:!1,text:"",value:"",name:"",size:"",skin:"",readmode:!1});let e5=A5;class o0 extends e5{constructor(){super(),this._init("oo-checkbox")}_render(){this._elements.input.type="checkbox",r.addClass(this._elements.checkIcon,"ooicon-checkmark"),super._render()}}const l0=`<div class="group content">\r
	<div style="display: table-row;">\r
		<div class="label-row">\r
			<slot name="before"></slot>\r
			<div class="label">\r
				<div class="labelText"></div>\r
				<slot name="label"></slot>\r
			</div>\r
		</div>\r
		<div class="itemsContent" style="display: table-cell;">\r
			<slot name="items" class="items hide-items"></slot>\r
			<slot name="after"></slot>\r
			<input type="text" class="input"/>\r
		</div>\r
		<div class="requiredFlag hide">&#10039;</div>\r
	</div>\r
	<div style="display: table-row;">\r
		<div style="display: table-cell; vertical-align: top;">\r
		</div>\r
		<div style="display: table-cell;">\r
			<div class="invalidHint ooicon-error"></div>\r
		</div>\r
	</div>\r
</div>\r
`,a0=`* {\r
    box-sizing: border-box;\r
}\r
\r
.group {\r
    display: table;\r
    align-items: center;\r
    min-height: 1.5em;\r
    width: 100%;\r
}\r
\r
.label {\r
    white-space: nowrap;\r
    transition: color 0.5s;\r
    display: flex;\r
    align-items: center;\r
    color: var(--oo-color-text2);\r
    height: 100%;\r
}\r
\r
.labelText {\r
    margin-right: 0.5em;\r
    padding: 0.5em 0.35em;\r
    display: flex;\r
    align-items: center;\r
    height: 100%;\r
}\r
.flex-end {\r
    justify-content: flex-end;\r
}\r
\r
.label-row {\r
    display: table-cell;\r
    vertical-align: middle;\r
    width: 1px;\r
}\r
\r
.group.readmode .label-row {\r
    display: table-cell;\r
    vertical-align: middle;\r
}\r
\r
.items {\r
    display: block;\r
}\r
.group.readmode .items {\r
    display: flex;\r
}\r
\r
.hide-items {\r
    position: absolute;\r
    opacity: 0;\r
    width: 1px;\r
    height: 1px;\r
    overflow: hidden;\r
}\r
.hide {\r
    display: none;\r
}\r
div.invalidHint {\r
    /*position: absolute;*/\r
    color: var(--oo-color-error);\r
    height: 0;\r
    width: 0;\r
    overflow: hidden;\r
    word-break: break-all;\r
    transition: margin-top 0.2s, height 0.2s;\r
}\r
div.invalidHint.show {\r
    margin: 0.3em 0 0 0;\r
    height: unset;\r
    width: unset;\r
    line-height: 1.2em;\r
}\r
div.invalidHint.show::before {\r
    line-height: 1.4em;\r
    margin-right: 0.2em;\r
}\r
.itemsContent {\r
    display: table-cell;\r
    border-radius: var(--oo-default-radius);\r
    position: relative;\r
    vertical-align: middle;\r
    transition: padding 0.2s;\r
}\r
.group:has(div.invalidHint.show) .itemsContent {\r
    padding: 0 0.5em;\r
    background: var(--oo-color-error-bg);\r
    box-shadow: var(--oo-shadow-border-error);\r
}\r
\r
input {\r
    position: absolute;\r
    width: 1px;\r
    height: 1px;\r
    overflow: hidden;\r
    left: 50%;\r
    bottom: 1px;\r
    appearance: none;\r
    outline: none;\r
    border: 0;\r
    opacity: 0;\r
    font-size: 1px;\r
}\r
\r
.requiredFlag {\r
    color: var(--oo-color-highlight);\r
    padding-left: 0.3em;\r
    font-size: 0.75em;\r
    display: table-cell;\r
    vertical-align: middle;\r
}\r
`,D5=class D5 extends k{constructor(){super();c(this,T5);c(this,Y2);c(this,O5);c(this,P5);c(this,x1);c(this,dt);c(this,Z5);c(this,j5);C(this,"_items",[]);c(this,M2,0);C(this,"_elements",{label:null,group:null,labelText:null,items:null,invalidHint:null,input:null,requiredFlag:null,itemsContent:null});C(this,"_setPropMap",{name:()=>{this._props.name&&this._items.forEach(t=>{t.setAttribute("name",this._props.name)})},skin:()=>{this._items.forEach(t=>{t.setAttribute("skin",this._props.skin)})},disabled:()=>{this._items.forEach(t=>{this._props.disabled?t.setAttribute("disabled",!0):t.disabled||t.removeAttribute("disabled")}),r.toggleAttr(this._elements.input,"disabled",this._props.disabled)},size:()=>{this._props.size&&this._items.forEach(t=>{t.setAttribute("size",this._props.size)})},col:()=>{this.rerender()},label:()=>{this._props.label?(this._elements.labelText.textContent=this._props.label,r.removeClass(this._elements.labelText,"hide")):(this._elements.labelText.textContent="",r.addClass(this._elements.labelText,"hide"))},labelStyle:()=>{r.toggleAttr(this._elements.labelText,"style",this._props.labelStyle)},labelAlign:()=>{switch(this._props.labelAlign){case"right":r.setStyle(this._elements.labelText,"justify-content","flex-end");break;case"center":r.setStyle(this._elements.labelText,"justify-content","center");break;default:r.setStyle(this._elements.labelText,"justify-content","flex-start")}},viewStyle:()=>{this._props.readmode&&r.toggleAttr(this._elements.itemsContent,"style",this._props.viewStyle)},count:()=>{this.rerender()},readmode:t=>{this.rerender(),r.toggleAttr(this._elements.input,"readonly",this._props.readmode),this._props.readmode?r.toggleAttr(this._elements.itemsContent,"style",this._props.viewStyle):r.toggleAttr(this._elements.itemsContent,"style",""),this._setPropMap.required()},max:()=>{this._props.max===0?this._elements.input.removeAttribute("max"):this._elements.input.setAttribute("max",this._props.max)},maxlength:()=>{this._props.maxlength===0?this._elements.input.removeAttribute("maxlength"):this._elements.input.setAttribute("maxlength",this._props.maxlength)},min:()=>{this._props.min===0?this._elements.input.removeAttribute("min"):this._elements.input.setAttribute("min",this._props.min)},minlength:()=>{this._props.minlength===0?this._elements.input.removeAttribute("minlength"):this._elements.input.setAttribute("minlength",this._props.minlength)},pattern:()=>{this._props.pattern?this._elements.input.setAttribute("pattern",this._props.pattern):this._elements.input.removeAttribute("pattern")},required:()=>{this._props.required?this._elements.input.setAttribute("required",this._props.required):this._elements.input.removeAttribute("required"),r.checkClass(this._elements.requiredFlag,"hide",!this._props.required||this._props.readmode||this._props.disabled)},step:()=>{this._props.step===1?this._elements.input.removeAttribute("step"):this._elements.input.setAttribute("step",this._props.step)},$default:()=>{}});c(this,y1,null)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_connected(){a(this,x1,v3).call(this),a(this,dt,L4).call(this),a(this,Y2,Bt).call(this)}rerender(){a(this,x1,v3).call(this),a(this,dt,L4).call(this),a(this,Y2,Bt).call(this)}_init(t,e,i,o){this._initialize(t,e||D5,i||l0,o||a0)}_render(){this._props.name||(this._props.name=Y4()),parseInt(this._props.col)||r.removeClass(this._elements.items,"hide-items"),parseInt(this._props.label)||r.addClass(this._elements.labelText,"hide"),this._itemChangeFun=this._itemChange.bind(this)}_removeItem(t){this._items.splice(this._items.indexOf(t),1),t.removeAttribute("slot"),t.removeEventListener("change",this._itemChangeFun),r.setStyle(t,"width","auto"),a(this,Y2,Bt).call(this)}_appendItem(t){this._items.push(t),t.setAttribute("slot","items"),t.setAttribute("name",this._props.name),t.setAttribute("skin",this._props.skin),this._props.disabled?t.setAttribute("disabled",!0):t.disabled||t.removeAttribute("disabled"),this._props.size&&t.setAttribute("size",this._props.size),t.addEventListener("change",this._itemChangeFun),this._appendItemCheckValue(t),a(this,Y2,Bt).call(this,!0),this._props.count&&(this._props.values?this._props.values.length:0)>=this._props.count&&!t.checked&&(t.disabled=!0),this._props.readmode&&(t.readmode=!0)}_appendItemCheckValue(t){this._props.values&&this._props.values.length&&this._props.values.includes(t.value)&&(t.checked=!0)}_itemChange(t){t.currentTarget.checked?(this._props.values||(this._props.values=[]),this._props.values.includes(t.currentTarget.value)||this._props.values.push(t.currentTarget.value),this._props.disabled||(t.currentTarget.disabled=!1)):this._props.values&&I1(this._props.values,t.currentTarget.value),this._elements.input.value=this._props.values.join(","),this.unInvalidStyle(),this._props.validityBlur&&this.checkValidity(),a(this,x1,v3).call(this),this.dispatchEvent(new Event("change",t))}_setEvent(){super._setEvent(),this._elements.input.addEventListener("invalid",t=>{a(this,j5,G9).call(this,t.target.validity)})}setCustomValidity(){return this._elements.input.setCustomValidity(...arguments)}checkValidity(){return this.dispatchEvent(new CustomEvent("validity")),this._elements.input.checkValidity(...arguments)}reportValidity(){return this.dispatchEvent(new CustomEvent("validity")),this._elements.input.reportValidity(...arguments)}unInvalidStyle(){this._elements.input.setCustomValidity(""),r.removeClass(this._elements.input,"invalid"),this._elements.invalidHint.textContent="",r.removeClass(this._elements.invalidHint,"show")}};M2=new WeakMap,T5=new WeakSet,Y9=function(){p(this,M2,0),this._items.forEach(t=>{r.setStyle(t,"width","auto");const e=r.getSize(t);e.x>l(this,M2)&&p(this,M2,e.x)})},y1=new WeakMap,Y2=new WeakSet,Bt=function(){l(this,y1)&&clearTimeout(l(this,y1)),p(this,y1,setTimeout(()=>{a(this,O5,q9).call(this)},10))},O5=new WeakSet,q9=function(){if(!this._props.readmode){a(this,P5,U9).call(this);const t=parseInt(this._props.col);t&&(a(this,T5,Y9).call(this),this._items.forEach((e,i)=>{l(this,M2)&&r.setStyle(e,"width",l(this,M2)+"px"),(i+1)%t===0&&e.insertAdjacentElement("afterend",r("br.group-col-separator",{slot:"items"}))}))}r.removeClass(this._elements.items,"hide-items")},P5=new WeakSet,U9=function(){this.querySelectorAll("br.group-col-separator").forEach(e=>{e.remove()})},x1=new WeakSet,v3=function(){if(this._props.count)if((this._props.values?this._props.values.length:0)>=this._props.count)for(const e of this._items)e.checked||(e.disabled=!0);else for(const e of this._items)e.disabled=!1},dt=new WeakSet,L4=function(){if(this._props.readmode){r.addClass(this._elements.group,"readmode");const t=this.querySelectorAll("br");t.length&&t.forEach(e=>{r.addClass(e,"hide")});for(const e of this._items)e.style.width="unset",e.readmode=!!this._props.readmode}else{r.removeClass(this._elements.group,"readmode");for(const t of this._items)t.readmode=!!this._props.readmode}},Z5=new WeakSet,X9=function(){return this._elements.input.validity.customError||this._props.validity&&this._elements.input.setCustomValidity(this._props.validity),this._elements.invalidHint&&(this._elements.invalidHint.textContent=this._elements.input.validationMessage),this._elements.input.validationMessage},j5=new WeakSet,G9=function(t){this.dispatchEvent(new CustomEvent("invalid",{detail:t})),r.addClass(this._elements.input,"invalid"),a(this,Z5,X9).call(this)&&r.addClass(this._elements.invalidHint,"show")},C(D5,"prop",{disabled:!1,value:"",name:"",size:"",col:"",label:"",labelStyle:"",labelAlign:"right",viewStyle:"",skin:"",count:0,readmode:!1,max:0,maxlength:0,min:0,minlength:0,pattern:"",required:!1,step:1,validity:"",validityBlur:!1});let n5=D5;const o4=class o4 extends n5{constructor(){super();c(this,F5);c(this,$5);c(this,I5);a(this,F5,K9).call(this),this._init("oo-checkbox-group",o4)}get value(){return a(this,$5,J9).call(this)}set value(t){Array.isArray(t)?this._props.values!==t&&(this._props.values=t,this._setPropMap.values()):this._props.value!==t&&(this._props.value=t,this._setPropMap.value())}get text(){return a(this,I5,Q9).call(this)}};F5=new WeakSet,K9=function(){this._setPropMap=Object.assign(this._setPropMap,{value:()=>{this._props.values=this._props.value?this._props.value.split(/\s*,\s*/g):[],this._setPropMap.values(),this.rerender()},values:()=>{var t;if(this._props.values&&this._props.values.length)for(const e of this._items)this._props.values.includes(e.value)?e.checked=!0:e._unchecked();else for(const e of this._items)e._unchecked();this._elements.input.value=((t=this._props.values)==null?void 0:t.join(","))||""}})},$5=new WeakSet,J9=function(){return this._props.values||[]},I5=new WeakSet,Q9=function(){const t=[];return this._items.forEach(e=>{e.checked&&t.push(e.text)}),t};let P3=o4;const d0={title:"O2OA",error:{http:{title:"发送HTTP请求错误",text:"发送{method}请求 '{url}' 状态: {status} ({statusText})"},loadIndexError:"加载首页出错",loadIndexNotSet:"未指定要加载的首页应用！您应该从O2OA的应用项目中运行。",action:{title:"O2ActionError"},failCode:{BROADCAST_LISTENED_LOOP_BROADCAST:"广播事件监听到另一个循环的广播事件:{$}",ENTITY_EXISTS:"实体对象已存在，对象类型：{$0}，标识：{$1}",ENTITY_FLAG_CONFLICT:"实体对象标识冲突,对象类型:{$0},标识:{$1}",ENTITY_NOT_FOUND:"实体对象不存在,对象类型:{$0},标识:{S1}",FIELD_EMPTY:"字段不能为空,字段:{$0}",FIELD_EXISTS:"字段已存在,字段:{$0},值:{$1}",FIELD_NOT_EXISTS:"字段值不存在,字段:{$0}",FIELD_VERIFICATION_FAIL:"字段校验失败,字段:{$0},值:{$1}",MISSING_PARAMETER:"缺少参数",OBJECT_EXIST:"对象已存在",OBJECT_NOT_FOUND:"对象不存在",PARAMETER_VERIFICATION_FAIL:"参数校验不通过,参数:{$}",APPLICATION_HAS_PROCESS:"应用中已存在名为“{$}”的流程。",MANUAL_PARSE_TASK_TARGET_EMPTY:"无法为活动“{$0}”解析到待办对象，工作：{$1}",PROCESSVERSION_ACTIVE:"流程版不是激活流程版本",PROCESSVERSION_HAS_PROCESSING_JOB:"流程版本存在流转中的任务",PROCESS_HAS_PROCESSVERSION:"流程中存在流程版本",ROUTE_NOT_FOUND_ACTIVITY:"路由没有找到对应的活动:{}",UPLOAD_FILE_EMPTY:"上传文件为空.",UPLOAD_FILE_TOO_LARGE:"上传文件过大,文件长度:{}.",WORK_NOT_FOUND_ROUTE:"工作没有找到任何路由, 工作:{}",WORK_TASK_EMPTY:"工作的待办对象为空, 工作:{}",FORBIDDEN:"未授权访问",LOGIN_CODE_ERROR:"验证码错误",LOGIN_ERROR:"用户不存在或密码错误",PASSWORD_EXPIRED:"密码已过期",PERMISSION_DENIED:"权限不足",PERSON_BAN:"用户被禁用",PERSON_LOCKED:"用户被锁定",UNAUTHORIZED:"未认证",GROUP_NOT_FOUND:"群组“{$0}”不存在。",IDENTITY_NOT_FOUND:"身份“{$0}”不存在。",PERSON_MAJOR_IDENTITY_NOT_FOUND:"用户主身份不存在, 用户:{$0}",PERSON_NOT_FOUND:"用户不存在, 标识:{$0}",PERSON_NOT_FOUND_WITH_IDENTITY:"无法通过身份查找到用户, 身份:{$0}",PERSON_NOT_MATCH_IDENTITY:"用户和身份不匹配, 用户:{$0}, 身份:{$1}",ROLE_NOT_FOUND:"角色不存在,标识:{$0}",SYSTEM_ROLE_CAN_NOT_DELETE:"系统角色不能删除,标识:{}",UNITDUTY_NOT_FOUND:"组织职务不存在,标识:{}",UNITPOST_NOT_FOUND:"职务不存在,标识:{}",UNIT_NOT_FOUND:"组织不存在,标识:{}",OTHER:"其他错误"}},login:{passwordLogin:"密码登录",codeLogin:"短信验证码登录",login:"登录",username:"用户名",password:"密码",captcha:"验证码",code:"短信验证码",sendCode:"发送验证码",reSendCode:"重发",forget:"忘记密码？",findPassword:"找回密码",register:"注册",wechat:"微信",official:"官网",forum:"论坛",support:"支持",changeCaptcha:"换一张",usernameEmpty:"用户名不能为空",sendCodeError:"您填写的信息有误",bindLoginInfo1:"手机扫码，安全登陆",bindLoginInfo2:"打开APP扫一扫",bindLoginInfo3:"登陆网页版",codeVerify:"短信验证",setNewPassword:"设置新密码",complete:"完成",next:"下一步",confirm:"确认",codeAnswerEmpty:"短信验证码不能为空",newPassword:"请输入新密码",confirmPassword:"请确认新密码",checkConfirmPassword:"新密码与确认密码不一致",resetPasswordSuccess:"您的密码修改成功！请重新登陆",loginResetSuccessInfo:"{seconds}S后自动跳转至登陆页面",resetPasswordError:"重置密码失败！",loginResetErrorInfo:"请核对您的用户名和短信验证码。",resetModify:"返回修改"},index:{homepage:"首页",searchKey:"请输入搜索关键字",menu:{app:"应用",process:"流程",cms:"信息",query:"数据"}},common:{clear:"清空",ok:"确定",cancel:"取消",yes:"是",no:"否",selectAll:"全选",delete:"删除",disable:"禁用",selectCountLimit:"选择数量限制",selectCountInfo:"您最多可以选择 {n} 个值"},component:{selectFile:"选择文件",notSelectedFile:"未选择文件"}};window.oo=window.oo||{I18n:{instances:{}}};const N=class N{constructor(n,t={}){C(this,"res",null);C(this,"defaultRes",{});this.name=n,this.defaultRes=t,n&&(window.oo.I18n.instances[n]=this)}static detect(){const n=new URL(window.location).searchParams,t=(n.get("lng")||n.get("lp")||localStorage.getItem("o2.language")||navigator.language||N.defaultLng).toLocaleLowerCase();return N.supportedLanguages.includes(t)?t:N.supportedLanguages.find(e=>t.substring(0,t.indexOf("-"))===e)||N.defaultLng}async load(){return this}t(n,t){return c9(n,t,this.res||this.defaultRes)}o(n){return h9(n,this.res||this.defaultRes)}};C(N,"supportedLanguages",["zh-cn","en"]),C(N,"defaultLng","zh-cn"),C(N,"lng",N.detect()),C(N,"instances",{}),C(N,"get",(n="index",t={})=>window.oo.I18n.instances[n]||(window.oo.I18n.instances[n]=new N(n,t)));let N1=N;function c9(s,n,t){let e=(a1(`return this.${s}`,t)||s).toString();return n&&Object.keys(n).forEach(i=>{const o=new RegExp(`{${i}}`,"g");e=e.replace(o,n[i])}),e}function h9(s,n){const t=s?a1(`return this.${s}`,n)||{}:n;return Object.defineProperties(t,{t:{get(){return function(o,d){return c9(o,d,this)}},enumerable:!0,configurable:!0},o:{get(){return function(o){return h9(o,this)}},enumerable:!0,configurable:!0}}),t}N1.get("index",d0);const c0=`<div class="content">\r
	<div class="labelArea">\r
		<slot name="before-outer"></slot>\r
		<div class="label">\r
			<div class="labelText hide"></div>\r
			<slot name="label"></slot>\r
		</div>\r
		<div style="width: 100%">\r
			<div style="display: flex; align-items: center;">\r
				<div class="box">\r
					<slot name="before-inner-before"></slot>\r
					<div class="prefix"></div>\r
					<slot name="before-inner-after"></slot>\r
					<div class="view">\r
						<input class="input" />\r
						<div class="viewText">&nbsp;</div>\r
					</div>\r
\r
					<div class="file hide">\r
						<oo-button>选择文件</oo-button>\r
						<div class="fileList">未选择文件</div>\r
					</div>\r
					<slot name="after-inner-before"></slot>\r
					<div class="suffix"></div>\r
					<slot name="after-inner-after"></slot>\r
				</div>\r
				<div class="requiredFlag hide">&#10039;</div>\r
			</div>\r
			<div class="invalidHint ooicon-error"></div>\r
		</div>\r
		<slot name="after-outer"></slot>\r
	</div>\r
</div>\r
`,p9=`* {\r
    box-sizing: border-box;\r
}\r
.content {\r
    --label: var(--oo-color-text2);\r
    --label-width: 10em;\r
    --icon-left: var(--oo-color-text2);\r
    --icon-right: var(--oo-color-text2);\r
    --focus: var(--oo-color-main);\r
    --border: var(--oo-color-gray1);\r
    --radius: var(--oo-default-radius);\r
    --input: var(--oo-color-text);\r
    --font-family: var(--oo-font-family);\r
    --placeholder: var(--oo-color-text4);\r
    --disabled: var(--oo-color-gray2);\r
    --shadow: var(--oo-shadow-border);\r
    --shadow-focus: var(--oo-shadow-border-focus);\r
}\r
.content {\r
    height: 100%;\r
    width: 100%;\r
    /*padding: 2px;*/\r
    position: relative;\r
}\r
.labelArea {\r
    display: flex;\r
    align-items: center;\r
    position: relative;\r
    height: 100%;\r
}\r
.label {\r
    white-space: nowrap;\r
    transition: color 0.2s;\r
    display: inline-flex;\r
    align-items: center;\r
    height: 100%;\r
    color: var(--label);\r
}\r
.labelText {\r
    padding: 0.5em 0.35em;\r
    margin-right: 0.5em;\r
    height: 100%;\r
    display: flex;\r
    align-items: center;\r
}\r
.flex-end {\r
    justify-content: flex-end;\r
}\r
.flex-center {\r
    justify-content: center;\r
}\r
.box {\r
    /*border-width: 1px;*/\r
    /*border-style: solid;*/\r
    /*border-color: var(--border);*/\r
    border-radius: var(--radius);\r
    box-shadow: var(--shadow);\r
    display: flex;\r
    align-items: center;\r
    height: 100%;\r
    width: 100%;\r
    background: #ffffff;\r
    transition: border-color 0.2s, box-shadow 0.2s, background-color 0.2s;\r
}\r
\r
.box:has(.input.invalid) {\r
    background: var(--oo-color-error-bg);\r
    box-shadow: var(--oo-shadow-border-error);\r
}\r
\r
.input {\r
    border: 0;\r
    outline: none;\r
    color: var(--input);\r
    border-radius: var(--radius);\r
    padding: 0.5em 0.6em;\r
    box-shadow: none;\r
    height: 100%;\r
    width: 100%;\r
    font-size: 1em;\r
    background: transparent;\r
    font-family: var(--font-family);\r
}\r
\r
input[type='date']::-webkit-calendar-picker-indicator {\r
    opacity: 0.8;\r
    cursor: pointer;\r
}\r
\r
.input[type='file'] {\r
    position: absolute;\r
    opacity: 0;\r
}\r
.file {\r
    padding: 0.2em;\r
    display: grid;\r
    grid-template-columns: auto auto;\r
    gap: 2em;\r
    align-items: center;\r
}\r
textarea {\r
    border: 0;\r
    outline: none;\r
    color: var(--input);\r
    border-radius: var(--radius);\r
    padding: 0.3em 0.6em;\r
    box-shadow: none;\r
    height: 100%;\r
    width: inherit;\r
    font-size: 1em;\r
    background: transparent;\r
}\r
\r
input::placeholder,\r
textarea::placeholder {\r
    color: var(--placeholder);\r
}\r
\r
div.left-icon {\r
    width: 1em;\r
    text-align: right;\r
    transition: color 0.2s;\r
    font-size: 0.9em;\r
    margin-left: 0.5em;\r
    margin-top: 0.1875em;\r
    color: var(--icon-left);\r
}\r
div.right-icon {\r
    width: 1em;\r
    text-align: left;\r
    transition: color 0.2s;\r
    font-size: 0.9em;\r
    margin-right: 0.5em;\r
    margin-top: 0.1875em;\r
    color: var(--icon-right);\r
}\r
\r
.box.focus {\r
    /*border-color: var(--focus);*/\r
    box-shadow: var(--shadow-focus);\r
    outline: none;\r
    color: var(--focus);\r
}\r
.box.focus div.left-icon {\r
    color: var(--focus);\r
}\r
.box.focus div.right-icon {\r
    color: var(--focus);\r
}\r
\r
.label.focus {\r
    color: var(--focus);\r
    outline: none;\r
}\r
\r
.box.disabled {\r
    background-color: var(--disabled) !important;\r
    outline: none;\r
    cursor: not-allowed;\r
    box-shadow: inset 0 0 0 1px var(--border);\r
    color: inherit;\r
}\r
.box.disabled div.left-icon {\r
    /*color: var(--oo-color-text4);*/\r
    opacity: 0.6;\r
    color: inherit;\r
}\r
.box.disabled div.right-icon {\r
    /*color: var(--oo-color-text4);*/\r
    opacity: 0.6;\r
    color: inherit;\r
}\r
.label.disabled {\r
    /*color: var(--oo-color-text4);*/\r
    outline: none;\r
    cursor: not-allowed;\r
}\r
input:disabled,\r
textarea:disabled {\r
    opacity: 0.45;\r
    cursor: not-allowed;\r
}\r
\r
.box.readmode {\r
    outline: none;\r
    cursor: default;\r
    box-shadow: none;\r
    background: transparent;\r
}\r
.box.readmode div.left-icon {\r
    color: inherit;\r
    display: none;\r
}\r
.box.readmode div.right-icon {\r
    color: inherit;\r
    display: none;\r
}\r
.label.readmode {\r
    outline: none;\r
    cursor: default;\r
}\r
.label.readmode.focus {\r
    color: inherit;\r
    outline: none;\r
}\r
\r
input:focus,\r
textarea:focus {\r
    border: 0;\r
    outline: none;\r
    box-shadow: none;\r
}\r
.appearance::-webkit-inner-spin-button,\r
.appearance::-webkit-outer-spin-button {\r
    -webkit-appearance: none;\r
    appearance: none;\r
    margin: 0;\r
}\r
.hide {\r
    display: none;\r
}\r
div.invalidHint {\r
    /*position: absolute;*/\r
    color: var(--oo-color-error);\r
    height: 0;\r
    overflow: hidden;\r
    word-break: break-all;\r
    transition: margin-top 0.2s, height 0.2s;\r
}\r
div.invalidHint.show {\r
    margin: 0.3em 0 0 0em;\r
    height: unset;\r
    line-height: 1.2em;\r
}\r
div.invalidHint.show::before {\r
    line-height: 1.4em;\r
    margin-right: 0.2em;\r
}\r
.view {\r
    position: relative;\r
    overflow: hidden;\r
    flex: 1;\r
}\r
.readmode .input {\r
    display: none;\r
}\r
.viewText {\r
    width: 100%;\r
    height: 100%;\r
    top: 0;\r
    left: 0;\r
    overflow: hidden;\r
    padding: 0.5em 0.6em;\r
    display: none;\r
}\r
\r
.readmode .viewText {\r
    display: block;\r
}\r
.requiredFlag {\r
    color: var(--oo-color-highlight);\r
    padding-left: 0.3em;\r
    font-size: 0.75em;\r
}\r
`,ct=class ct extends k{constructor(){super();c(this,z5);c(this,N5);C(this,"_elements",{label:null,input:null,file:null,box:null,prefix:null,suffix:null,labelText:null,fileList:null,invalidHint:null,viewText:null,view:null,requiredFlag:null});C(this,"_setPropMap",{leftIcon:t=>{r.removeClass(this._elements.prefix,`ooicon-${t} left-icon`),this._props.leftIcon&&r.addClass(this._elements.prefix,`ooicon-${this._props.leftIcon} left-icon`)},rightIcon:t=>{r.removeClass(this._elements.suffix,`ooicon-${t} right-icon`),this._props.rightIcon&&r.addClass(this._elements.suffix,`ooicon-${this._props.rightIcon} right-icon`)},width:()=>{this._props.width&&(this._elements.box.style.width=this._props.width,this._elements.box.parentElement.style.minWidth=this._props.width)},height:()=>{this._props.height&&(this._elements.box.style.height=this._props.height,this._elements.box.parentElement.style.minHeight=this._props.height)},style:()=>{this._setPropMap.width(),this._setPropMap.height()},inputStyle:()=>{r.toggleAttr(this._elements.input,"style",this._props.inputStyle)},labelStyle:()=>{r.toggleAttr(this._elements.labelText,"style",this._props.labelStyle)},labelAlign:()=>{switch(this._props.labelAlign){case"right":r.setStyle(this._elements.labelText,"justify-content","flex-end");break;case"center":r.setStyle(this._elements.labelText,"justify-content","center");break;default:r.setStyle(this._elements.labelText,"justify-content","flex-start")}},viewStyle:()=>{r.toggleAttr(this._elements.viewText,"style",this._props.viewStyle)},bgcolor:()=>{this._props.bgcolor?r.setStyle(this._elements.box,"background-color",this._props.bgcolor):r.setStyle(this._elements.box,"background-color","transparent")},disabled:()=>{r.toggleAttr(this._elements.input,"disabled",this._props.disabled),r.checkClass(this._elements.box,"disabled",this._props.disabled),r.checkClass(this._elements.label,"disabled",this._props.disabled)},readmode:()=>{r.toggleAttr(this._elements.input,"readonly",this._props.readmode),r.checkClass(this._elements.box,"readmode",this._props.readmode),r.checkClass(this._elements.label,"readmode",this._props.readmode),this._setPropMap.required()},label:()=>{this._props.label?(this._elements.labelText.textContent=this._props.label,r.removeClass(this._elements.labelText,"hide")):(this._elements.labelText.textContent="",r.addClass(this._elements.labelText,"hide"))},skin:()=>{this._useSkin(this._props.skin)},value:()=>{try{this._elements.input.value=this._props.value}catch{}this._props.value?this._elements.viewText.textContent=this._props.value:this._elements.viewText.innerHTML="&nbsp;"},appearance:()=>{r.checkClass(this._elements.input,"appearance",this._props.appearance==="none")},validity:()=>{},type:()=>{this._elements.file&&r.checkClass(this._elements.file,"hide",this._props.type!=="file"),r.toggleAttr(this._elements.input,"type",this._props.type)},autoSize:()=>{},resize:()=>{r.toggleClass(this._elements.input,"resize",this._props.resize)},required:()=>{r.toggleAttr(this._elements.input,"required",this._props.required),r.checkClass(this._elements.requiredFlag,"hide",!this._props.required||this._props.readmode||this._props.disabled)},$default:t=>{t==="value"?this.value=this._props[t]:r.toggleAttr(this._elements.input,t,this._props[t])}});const t=N1.get("index").o("component");["selectFile","notSelectedFile"].forEach(e=>ct.prop[e]=t[e])}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_init(t,e,i,o){this._initialize(t,e||ct,i||c0,o||p9)}_setEvent(){super._setEvent(),this._elements.input.addEventListener("focus",()=>{this._props.readmode||(r.addClass(this._elements.box,"focus"),r.addClass(this._elements.label,"focus"))}),this._elements.input.addEventListener("blur",()=>{this.unInvalidStyle(),this._props.validityBlur&&this.checkValidity(),r.removeClass(this._elements.box,"focus"),r.removeClass(this._elements.label,"focus")}),this._elements.input.addEventListener("input",()=>{this.value=this._elements.input.value,this._elements.viewText.textContent=this._elements.input.value,this.unInvalidStyle()}),this._elements.input.addEventListener("change",t=>{this.unInvalidStyle(),this.dispatchEvent(new Event("change",t))}),this._elements.input.addEventListener("change",t=>{if(this._props.type==="file")if(console.log(t.target.value),t.target.files.length){r.empty(this._elements.fileList);for(const e of t.target.files){const i=r("oo-file",{filename:e.name,size:e.size});this._elements.fileList.append(i)}}else r.empty(this._elements.fileList),this._elements.fileList.textContent=this._props.notSelectedFile}),this._elements.input.addEventListener("invalid",t=>{a(this,N5,e8).call(this,t.target.validity)})}unInvalidStyle(t){this._elements.input.setCustomValidity(""),r.removeClass(this._elements.input,"invalid"),this._elements.invalidHint.textContent="",r.removeClass(this._elements.invalidHint,"show")}setCustomValidity(){return this._elements.input.setCustomValidity(...arguments)}checkValidity(){const t=this._elements.input.hasAttribute("readonly");t&&this._elements.input.removeAttribute("readonly"),this.dispatchEvent(new CustomEvent("validity"));const e=this._elements.input.checkValidity(...arguments);return t&&this._elements.input.setAttribute("readonly",!0),r.toggleAttr(this,"valid",e),e}reportValidity(){const t=this._elements.input.hasAttribute("readonly");t&&this._elements.input.removeAttribute("readonly"),this.dispatchEvent(new CustomEvent("validity"));const e=this._elements.input.reportValidity(...arguments);return t&&this._elements.input.setAttribute("readonly",!0),e}_render(){this._setPropMap.value(),this._setPropMap.type()}focus(){this._elements.input.focus()}blur(){this._elements.input.blur()}get files(){return this._elements.input.files}get value(){return this._elements.input.value}set value(t){this.setAttribute("value",t)}};z5=new WeakSet,t8=function(){return this._elements.input.validity.customError||this._props.validity&&this._elements.input.setCustomValidity(this._props.validity),this._elements.invalidHint&&(this._elements.invalidHint.textContent=this._elements.input.validationMessage),this._elements.input.validationMessage},N5=new WeakSet,e8=function(t){this.dispatchEvent(new CustomEvent("invalid",{detail:t})),r.addClass(this._elements.input,"invalid"),a(this,z5,t8).call(this)&&r.addClass(this._elements.invalidHint,"show")},C(ct,"prop",{leftIcon:"",rightIcon:"",width:"",height:"",inputStyle:"",labelStyle:"",labelAlign:"right",viewStyle:"",disabled:!1,readmode:!1,label:"",bgcolor:"#ffffff",skin:"",appearance:"",selectFile:"Select File",notSelectedFile:"Not Selected Files",validity:"",validityBlur:!1,autoSize:!1,resize:!1,id:"",placeholder:"",readonly:!1,type:"text",autofocus:!1,form:"",max:"",maxlength:"",min:"",minlength:"",name:"",pattern:"",size:"",spellcheck:"",src:"",step:"",tabindex:"",value:"",title:"",list:"",autocomplete:"",accept:"",alt:"",capture:"",checked:"",dirname:"",formaction:"",formenctype:"",formmethod:"",formnovalidate:"",formtarget:"",multiple:"",required:!1,cols:"",rows:""});let P2=ct;const h0=`.content{--option-bg: var(--oo-color-text-white);--option-over: var(--oo-color-text2);--option-over-bg: var(--oo-color-gray-e);--option-select: var(--oo-color-main);--option-select-bg: var(--oo-color-text-white);--option-shadow-color: var(--oo-color-gray-d);--option-shadow: 0 0 10px 1px var(--option-shadow-color);--option-border: var(--oo-color-gray-d);--option-selected: var(--oo-color-main);--option-selected-bg: var(--oo-color-light);--option-group-bg: var(--oo-color-text-white);--option-group-color: var(--oo-color-gray-9)}.drop{text-align:left;transition:color .2s;font-size:.8em;margin-right:.6em;color:var(--label);cursor:pointer;transform-origin:center}.drop.disabled,.suffix.disabled{opacity:.8;cursor:not-allowed}.box.focus .drop{color:var(--focus)}.calendar-area{position:absolute;border:1px solid var(--option-border);background-color:var(--option-bg);border-radius:var(--oo-area-radius);box-shadow:var(--option-shadow);z-index:100;transform-origin:top;transition:height .2s,opacity .2s,transform .2s}.calendar-area.visible{transform:scale(1);opacity:1}.calendar-area.invisible{transform:scaleY(0);opacity:0}.input,div.right-icon{cursor:pointer}.viewText{word-break:break-all;white-space:nowrap}
`,n2=class n2 extends P2{constructor(){super();c(this,R5);c(this,B5);c(this,ht);c(this,V5);c(this,pt);c(this,W5);c(this,J,!1);c(this,O,null);c(this,k1,null);c(this,H,null);a(this,R5,n8).call(this),this._init("oo-datetime",n2),this._useCss(h0)}static get observedAttributes(){return n2.prop=Object.assign({},n2.prop,n2.calendarProp),Object.keys(n2.prop).map(t=>_(t))}_render(){p(this,J,!1),this._elements.input.setAttribute("readonly","readonly"),p(this,O,this.querySelector("div.calendar-area")),l(this,O)||(p(this,O,r("div.calendar-area.invisible")),this._content.insertAdjacentElement("beforeend",l(this,O))),this._props.readonly=!0,this._setPropMap.value()}_setEvent(){super._setEvent(),this._elements.input.addEventListener("click",()=>{!this._props.read&&!this._props.readmode&&!this._props.disabled&&(p(this,J,!l(this,J)),l(this,J)?a(this,V5,i8).call(this):a(this,pt,w4).call(this))}),l(this,O).addEventListener("click",t=>{t.stopPropagation()}),l(this,O).addEventListener("mousedown",t=>{t.stopPropagation()}),this._content.firstElementChild.addEventListener("mousedown",t=>{t.stopPropagation()})}_connected(){this.hasAttribute("right-icon")||this.setAttribute("right-icon","calendar"),this._elements.input.setAttribute("readonly","readonly")}};R5=new WeakSet,n8=function(){this._setPropMap=Object.assign(this._setPropMap,{disabled:()=>{r.toggleAttr(this._elements.input,"disabled",this._props.disabled),r.checkClass(this._elements.box,"disabled",this._props.disabled),r.checkClass(this._elements.label,"disabled",this._props.disabled),r.checkClass(this._elements.suffix,"disabled",this._props.disabled)},value:()=>{l(this,H)&&l(this,H).setAttribute("value",this._props.value),this._elements.input.value=this._props.value,this._elements.viewText.textContent=this._props.value},timeRange:()=>{l(this,H)&&l(this,H).setAttribute("time-range",this._props.timeRange)},read:()=>{},readonly:()=>{},readmode:()=>{r.checkClass(this._elements.box,"readmode",this._props.readmode),r.checkClass(this._elements.label,"readmode",this._props.readmode)},$default:t=>{n2.calendarProp.hasOwnProperty(t)?l(this,H)&&l(this,H).setAttribute(_(t),this._props[t]):r.toggleAttr(this._elements.input,t,this._props[t])}})},J=new WeakMap,O=new WeakMap,k1=new WeakMap,H=new WeakMap,B5=new WeakSet,s8=function(){const t=r.getPosition(this._elements.box,this._content),e=r.getSize(this._elements.box),i=t.x,o=t.y+e.y+6+e.y*.2;e.x,r.setStyles(l(this,O),{top:o+"px",left:i+"px"})},ht=new WeakSet,_4=function(){r.checkClass(this._elements.box,"focus",l(this,J)),r.checkClass(this._elements.label,"focus",l(this,J))},V5=new WeakSet,i8=function(){p(this,J,!0),a(this,ht,_4).call(this),a(this,B5,s8).call(this),r.removeClass(l(this,O),"invisible"),r.addClass(l(this,O),"visible"),this.ownerDocument.dispatchEvent(new MouseEvent("mousedown")),a(this,W5,r8).call(this),p(this,k1,a(this,pt,w4).bind(this)),this.ownerDocument.addEventListener("mousedown",l(this,k1)),this.unInvalidStyle()},pt=new WeakSet,w4=function(){p(this,J,!1),a(this,ht,_4).call(this),r.removeClass(l(this,O),"visible"),r.addClass(l(this,O),"invisible"),this.ownerDocument.removeEventListener("mousedown",l(this,k1))},W5=new WeakSet,r8=function(){if(!l(this,H)){let t="<oo-calendar";for(let[e]of Object.entries(n2.calendarProp))v(this._props[e])!=="null"&&(t+=` ${_(e)}='${this._props[e]}'`);t+="></oo-calendar>",r.set(l(this,O),"html",t),p(this,H,l(this,O).firstElementChild),l(this,H).addEventListener("change",e=>{this.setAttribute("value",e.detail.value),this.dispatchEvent(new CustomEvent("change",e))})}},C(n2,"calendarProp",{view:"date",baseDate:null,yearOnly:!1,monthOnly:!1,dateOnly:!1,weekOnly:!1,timeOnly:!1,secondEnable:!1,cleanEnable:!0,todayEnable:!0,datetimeRange:"",dateRange:"",timeRange:"",weekBegin:1,value:"",format:"",rightIcon:"calendar",read:!1});let Z3=n2;const p0=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    display: flex;\r
    align-items: center;\r
    flex-direction: row;\r
}\r
.icon{\r
    height: 2.75em;\r
    width: 2.75em;\r
    display: flex;\r
    align-items: center;\r
    justify-content: center;\r
}\r
.icon>oo-icon{\r
    height: 2.4em;\r
    width: 2.4em;\r
}\r
.filename{\r
    padding: 0 0.5em;\r
}\r
.name{\r
\r
}\r
.size{\r
    color: var(--oo-color-text3);\r
    font-size: 0.875em;\r
}\r
\r
.hide{\r
    display: none;\r
}\r
\r
.mask{\r
    position: absolute;\r
    width: 100%;\r
    height: 100%;\r
    background: var(--oo-color-text2);\r
    opacity: 0.5;\r
}\r
\r
.preview{\r
    height: 70%;\r
    width: 70%;\r
    background-repeat: no-repeat;\r
    background-size: contain;\r
    background-position: center;\r
}\r
`,u9={html:"html",htm:"html",jsp:"html",asp:"html",js:"js",mjs:"js",cjs:"js",css:"css",xml:"xml",xsl:"xsl",avi:"avi",mkv:"avi",mov:"avi",ogg:"avi",mp4:"mp4",mpa:"avi",mpe:"avi",mpeg:"avi",mpg:"avi",rmvb:"rm",rm:"rm",doc:"word",docx:"word",dotx:"word",dot:"word",xls:"excel",xlsx:"excel",xlsm:"excel",xlt:"excel",xltx:"excel",pptx:"ppt",ppt:"ppt",pot:"ppt",potx:"ppt",potm:"ppt",mp3:"mp4",wav:"wav",wma:"wma",wmv:"wmv",bmp:"img",gif:"gif",png:"png",psd:"psd",jpeg:"jpeg",jpg:"jpeg",jpe:"jpeg",tiff:"tiff",ico:"jpeg",ai:"ai",apng:"img",avif:"img",cur:"img",jfif:"jpeg",pjpeg:"jpeg",pjp:"jpeg",tif:"tiff",webp:"img",pdf:"pdf",rar:"rar",txt:"txt",zip:"zip",exe:"exe",ofd:"ofd",tmp:"tmp",arch:"arch",att:"att",au:"au",cad:"cad",cdr:"cdr",eps:"eps",iso:"iso",fla:"flash",link:"link",folder:"folder",unknown:"zip"},u0=`
<div class="content">
    <div class="icon">
        <oo-icon></oo-icon>
    </div>
    <div class="filename">
        <div class="name"></div>
        <div class="size"></div>
    </div>
<!--    <div class="preview hide">预览</div>-->
</div>
`,G5=class G5 extends k{constructor(){super();c(this,q2);c(this,H5);c(this,Y5);c(this,q5);c(this,U5);c(this,X5);C(this,"_elements",{icon:null,name:null,size:null,preview:null});C(this,"_setPropMap",{filename:()=>{a(this,q2,Vt).call(this)},size:()=>{a(this,q2,Vt).call(this)},url:()=>{a(this,q2,Vt).call(this)},preview:()=>{a(this,q2,Vt).call(this)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)}});c(this,ut,null);this._initialize("oo-file",G5,u0,p0)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}async _render(){if(this._props.filename){const t=this._props.size?a(this,X5,c8).call(this,parseFloat(this._props.size)):"";this.extension=this._props.filename.substring(this._props.filename.lastIndexOf(".")+1),r.empty(this._elements.icon);const e=r("oo-icon");e.setAttribute("type",u9[this.extension]||u9.unknown),this._elements.icon.append(e),this._elements.name.textContent=this._props.filename,this._elements.size.textContent=t}}_setEvent(){super._setEvent(),this._elements.icon.addEventListener("click",t=>{t.stopPropagation(),this._props.preview&&this._props.url&&a(this,H5,o8).call(this)})}_previewImage(t){const e=r("img.preview");e.addEventListener("click",i=>{i.stopPropagation()}),t.append(e),e.src=this._props.url}_previewVideo(t){const e=r("video.preview",{controls:"play"});e.addEventListener("click",i=>{i.stopPropagation()}),t.append(e),e.src=this._props.url}_previewAudio(t){const e=r("audio.preview",{controls:"play"});e.addEventListener("click",i=>{i.stopPropagation()}),t.append(e),e.src=this._props.url}};ut=new WeakMap,q2=new WeakSet,Vt=function(){l(this,ut)||setTimeout(()=>{this._render(),p(this,ut,null)},5)},H5=new WeakSet,o8=function(){const t=a(this,Y5,l8).call(this,this.extension)?"Image":a(this,q5,a8).call(this,this.extension)?"Video":a(this,U5,d8).call(this,this.extension)?"Audio":"";if(t){const e=r("div.preview_mask"),i=r("div.preview_area"),o=r.getPosition(this._elements.icon);r.setStyles(e,{top:o.y+"px",left:o.x+"px"}),r.setStyles(i,{top:o.y+"px",left:o.x+"px"}),document.body.append(e),document.body.append(i),setTimeout(()=>{r.addClass(e,"show"),r.addClass(i,"show")},5),i.addEventListener("click",()=>{e.remove(),i.remove()}),this[`_preview${t}`](i)}},Y5=new WeakSet,l8=function(t){return["apng","avif","bmp","gif","ico","cur","jpg","jpeg","jfif","pjpeg","pjp","png","svg","tif","tiff","webp"].includes(t)},q5=new WeakSet,a8=function(t){return["avi","mkv","mov","ogg","mp4","mpa","mpe","mpeg","mpg","rmvb","rm"].includes(t)},U5=new WeakSet,d8=function(t){return["mp3","wav","wma","wmv"].includes(t)},X5=new WeakSet,c8=function(t){if(!t)return"";const e=["B","KB","MB","GB"];let i=0;for(;t>=1024&&i<e.length-1;)t/=1024,i++;return t.toFixed(2)+" "+e[i]},C(G5,"prop",{filename:"",size:"",preview:!1,url:"",cssLink:"",skin:""});let j3=G5;const C0=`
<div class="content">
</div>
`,m0=Object.assign({"../../assets/icons/ai.svg":()=>Promise.resolve().then(()=>y6).then(s=>s.default),"../../assets/icons/arch.svg":()=>Promise.resolve().then(()=>x6).then(s=>s.default),"../../assets/icons/att.svg":()=>Promise.resolve().then(()=>k6).then(s=>s.default),"../../assets/icons/au.svg":()=>Promise.resolve().then(()=>E6).then(s=>s.default),"../../assets/icons/avi.svg":()=>Promise.resolve().then(()=>M6).then(s=>s.default),"../../assets/icons/cad.svg":()=>Promise.resolve().then(()=>S6).then(s=>s.default),"../../assets/icons/cdr.svg":()=>Promise.resolve().then(()=>A6).then(s=>s.default),"../../assets/icons/css.svg":()=>Promise.resolve().then(()=>T6).then(s=>s.default),"../../assets/icons/eps.svg":()=>Promise.resolve().then(()=>O6).then(s=>s.default),"../../assets/icons/excel.svg":()=>Promise.resolve().then(()=>P6).then(s=>s.default),"../../assets/icons/exe.svg":()=>Promise.resolve().then(()=>Z6).then(s=>s.default),"../../assets/icons/flash.svg":()=>Promise.resolve().then(()=>j6).then(s=>s.default),"../../assets/icons/folder.svg":()=>Promise.resolve().then(()=>D6).then(s=>s.default),"../../assets/icons/gif.svg":()=>Promise.resolve().then(()=>F6).then(s=>s.default),"../../assets/icons/html.svg":()=>Promise.resolve().then(()=>$6).then(s=>s.default),"../../assets/icons/ico.svg":()=>Promise.resolve().then(()=>I6).then(s=>s.default),"../../assets/icons/img.svg":()=>Promise.resolve().then(()=>z6).then(s=>s.default),"../../assets/icons/iso.svg":()=>Promise.resolve().then(()=>N6).then(s=>s.default),"../../assets/icons/java.svg":()=>Promise.resolve().then(()=>R6).then(s=>s.default),"../../assets/icons/jpeg.svg":()=>Promise.resolve().then(()=>B6).then(s=>s.default),"../../assets/icons/js.svg":()=>Promise.resolve().then(()=>V6).then(s=>s.default),"../../assets/icons/link.svg":()=>Promise.resolve().then(()=>W6).then(s=>s.default),"../../assets/icons/mp3.svg":()=>Promise.resolve().then(()=>H6).then(s=>s.default),"../../assets/icons/mp4.svg":()=>Promise.resolve().then(()=>Y6).then(s=>s.default),"../../assets/icons/ofd.svg":()=>Promise.resolve().then(()=>q6).then(s=>s.default),"../../assets/icons/pdf.svg":()=>Promise.resolve().then(()=>U6).then(s=>s.default),"../../assets/icons/png.svg":()=>Promise.resolve().then(()=>X6).then(s=>s.default),"../../assets/icons/ppt.svg":()=>Promise.resolve().then(()=>G6).then(s=>s.default),"../../assets/icons/psd.svg":()=>Promise.resolve().then(()=>K6).then(s=>s.default),"../../assets/icons/rar.svg":()=>Promise.resolve().then(()=>J6).then(s=>s.default),"../../assets/icons/rm.svg":()=>Promise.resolve().then(()=>Q6).then(s=>s.default),"../../assets/icons/svg.svg":()=>Promise.resolve().then(()=>tn).then(s=>s.default),"../../assets/icons/tiff.svg":()=>Promise.resolve().then(()=>en).then(s=>s.default),"../../assets/icons/tmp.svg":()=>Promise.resolve().then(()=>nn).then(s=>s.default),"../../assets/icons/txt.svg":()=>Promise.resolve().then(()=>sn).then(s=>s.default),"../../assets/icons/unknown.svg":()=>Promise.resolve().then(()=>rn).then(s=>s.default),"../../assets/icons/wav.svg":()=>Promise.resolve().then(()=>on).then(s=>s.default),"../../assets/icons/wma.svg":()=>Promise.resolve().then(()=>ln).then(s=>s.default),"../../assets/icons/wmv.svg":()=>Promise.resolve().then(()=>an).then(s=>s.default),"../../assets/icons/word.svg":()=>Promise.resolve().then(()=>dn).then(s=>s.default),"../../assets/icons/xml.svg":()=>Promise.resolve().then(()=>cn).then(s=>s.default),"../../assets/icons/xsl.svg":()=>Promise.resolve().then(()=>hn).then(s=>s.default),"../../assets/icons/zip.svg":()=>Promise.resolve().then(()=>pn).then(s=>s.default)}),K5=class K5 extends k{constructor(){super();C(this,"_setPropMap",{type:()=>{this._render()}});this._initialize("oo-icon",K5,C0,"svg{width:100%; height: 100%}")}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}async _render(){if(r.empty(this._content),this._props.type){const t=await m0[`../../assets/icons/${this._props.type}.svg`]();r.empty(this._content),this._content.insertAdjacentHTML("afterbegin",t)}}};C(K5,"prop",{type:""});let D3=K5;class g0 extends P2{constructor(){super(),this._init("oo-input")}}const f0=`.content{\r
    background-color: var(--option-bg);\r
    transition: background-color 0.2s, color 0.2s;\r
    cursor: pointer;\r
    overflow: hidden;\r
}\r
.content:hover{\r
    background-color: var(--option-over-bg);\r
    color: var(--option-over);\r
}\r
.content.selected{\r
    font-weight: bold;\r
    color: var(--option-selected);\r
    background-color: var(--option-selected-bg);\r
}\r
.content.disabled{\r
    opacity: 0.4;\r
    cursor: not-allowed;\r
}\r
.content.disabled:hover{\r
    background-color: var(--option-bg);\r
}\r
\r
.area{\r
    padding: 0 1em;\r
    float: left;\r
    line-height: 2em;\r
    overflow: hidden;\r
}\r
\r
.label, .text{\r
    line-height: 2em;\r
    overflow: hidden;\r
    word-break: keep-all;\r
    text-overflow: ellipsis;\r
}\r
`,v0='<div class="content"><div class="area"><div class="text"></div><div class="label"></div></div></div>',J5=class J5 extends k{constructor(){super();c(this,Ct);C(this,"_setPropMap",{value:()=>{},$default:()=>{},text:()=>{a(this,Ct,y4).call(this)},disabled:()=>{r.checkClass(this._content,"disabled",!!this._props.disabled)},selected:()=>{r.checkClass(this._content,"selected",this._props.selected),l(this,Y)._selected(this)},skin:()=>{this._useSkin(this._props.skin)}});C(this,"_elements",{text:null,label:null,area:null});c(this,Y,null);c(this,a2,null);this._initialize("oo-option",J5,v0,f0)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_render(){a(this,Ct,y4).call(this),this._setPropMap.disabled()}_connected(){const t=this.parentElement;t.tagName.toLowerCase()==="oo-option-group"?(p(this,a2,t),t.parentElement.tagName.toLowerCase()==="oo-select"&&p(this,Y,t.parentElement)):t.tagName.toLowerCase()==="oo-select"&&p(this,Y,t),l(this,a2)||l(this,Y)?((l(this,a2)||l(this,Y))._appendItem(this),r.show(this)):r.hide(this)}_disconnected(){if(l(this,a2)||l(this,Y)){const t=l(this,a2)||l(this,Y);p(this,Y,null),p(this,a2,null),t._removeItem(this)}}_setEvent(){this._content.addEventListener("click",()=>{this._props.disabled||(this.selected=!0)});const t=()=>{var o;const i=r.getSize(this._elements.area);(o=l(this,Y))==null||o.checkMaxWidth(i.x)};new MutationObserver(t).observe(this._content,{subtree:!0,childList:!0,attributes:!0,characterData:!0})}};Ct=new WeakSet,y4=function(){if(this.innerHTML.trim()){let t=this.firstChild;for(;t;)this._elements.label.appendChild(t),t=this.firstChild}this._elements.label.innerHTML.trim()||(this._props.text?this._elements.text.textContent=this._props.text:this._elements.text.innerHTML="&nbsp;")},Y=new WeakMap,a2=new WeakMap,C(J5,"prop",{value:"",text:"",selected:!1,disabled:!1,skin:""});let F3=J5;const b0=`.content{\r
    padding: 0 1em;\r
    background-color: var(--option-group-bg);\r
    line-height: 2em;\r
    overflow: hidden;\r
    transition: background-color 0.3s, color 0.3s;\r
    cursor: pointer;\r
}\r
\r
.label, .text{\r
    color: var(--option-group-color);\r
    line-height: 2em;\r
    overflow: hidden;\r
    word-break: keep-all;\r
    text-overflow: ellipsis;\r
}\r
`,L0=`
<div class="content">
    <div class="text"></div>
    <div class="label"></div>
    <div class="options-content">
        <slot name="items"></slot>
    </div>
</div>`,Q5=class Q5 extends k{constructor(){super();C(this,"_setPropMap",{$default:()=>{},text:()=>{this._elements.text.textContent=this._props.text},label:()=>{this._elements.label.textContent=this._props.label},skin:()=>{this._useSkin(this._props.skin)}});C(this,"_elements",{text:null,label:null});c(this,R,null);c(this,E1,[]);this._initialize("oo-option-group",Q5,L0,b0)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_render(){this._elements.text.textContent=this._props.text||this._props.label}_connected(){const t=this.parentElement;t.tagName.toLowerCase()==="oo-select"&&p(this,R,t),l(this,R)?(l(this,R)._appendGroup(this),r.show(this)):r.hide(this)}_disconnected(){if(l(this,R)){const t=l(this,R);p(this,R,null),t._removeGroup(this)}}_removeItem(t){l(this,R)&&l(this,R)._removeItem(t),l(this,E1).splice(l(this,E1).indexOf(t),1)}_appendItem(t){l(this,R)&&l(this,R)._appendItem(t),l(this,E1).push(t)}};R=new WeakMap,E1=new WeakMap,C(Q5,"prop",{text:"",skin:""});let $3=Q5;const _0=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    --padding: 0.1em 0;\r
    --radius: var(--oo-default-radius);\r
    --hover-color: var(--oo-color-main);\r
    --hover-bg: var(--oo-color-hover);\r
}\r
.content{\r
    padding: var(--padding);\r
    border-radius: var(--radius);\r
    display: inline-flex;\r
    align-items: center;\r
    font-size: 1em;\r
    cursor: pointer;\r
    transition: background-color 0.3s, color 0.3s;\r
    margin-right: 0.5em;\r
}\r
.content:hover{\r
    background-color: var(--hover-bg);\r
    color: var(--hover-color);\r
}\r
\r
.icon{\r
    background-position: center;\r
    background-size: cover;\r
    background-repeat: no-repeat;\r
    border-radius: var(--oo-default-radius);\r
    /*opacity: 0.6;*/\r
    margin-right: 0.2em;\r
    height: 1.125rem;\r
    width: 1.125rem;\r
\r
    display: flex;\r
    font-size: 0.875rem;\r
    justify-content: center;\r
    align-items: center;\r
}\r
.avatar{\r
    background-color: transparent;\r
}\r
\r
.hide{\r
    display: none;\r
}\r
.text{\r
    white-space: nowrap;\r
}\r
`,w0=Object.assign({"./styles/tooltip/deep.css":()=>Promise.resolve().then(()=>un),"./styles/tooltip/default.css":()=>Promise.resolve().then(()=>Cn),"./styles/tooltip/toolbar.css":()=>Promise.resolve().then(()=>mn)}),d2=class d2{constructor(n,t,e){c(this,te);c(this,ee);c(this,ne);c(this,se);c(this,ie);c(this,mt);c(this,gt);c(this,re);c(this,oe);c(this,ft);c(this,le);c(this,vt);c(this,ae);this.content=n,this.target=t,this.options=Object.assign({},d2.option,e),a(this,te,h8).call(this),a(this,ae,b8).call(this),this.options.events?a(this,ft,E4).call(this):this.show()}close(){r.setStyles(this.node,{opacity:0}),this.hideTimeout=window.setTimeout(()=>{this.container.remove()},600)}hide(){r.setStyles(this.node,{opacity:0}),this.hideTimeout=window.setTimeout(()=>{r.addClass(this.node,"hide"),r.removeClass(this.node,"show"),this.p&&r.setStyles(this.node,{left:this.p.fromX+"px",top:this.p.fromY+"px"}),a(this,ne,u8).call(this)},600)}async show(){if(!this.target||!this.target.isConnected)return null;this.hideTimeout&&(clearTimeout(this.hideTimeout),this.hideTimeout=null),r.removeClass(this.node,"hide"),r.addClass(this.node,"show"),a(this,ee,p8).call(this),await a(this,ft,E4).call(this),await a(this,le,v8).call(this),this.contentNode.childNodes.length&&(r.removeClass(this.node,"hide"),r.addClass(this.node,"show"),this.p=a(this,ie,m8).call(this),r.setStyles(this.node,{left:this.p.fromX+"px",top:this.p.fromY+"px"}),r.setStyles(this.triangle,{left:this.p.flagX+"px",top:this.p.flagY+"px"}),window.setTimeout(()=>{r.setStyles(this.node,{opacity:1,left:this.p.x+"px",top:this.p.y+"px"}),a(this,re,g8).call(this)},5)),(!this.target||!this.target.isConnected)&&this.hide()}};te=new WeakSet,h8=async function(){this.container=r("div.ootooltip_container"),this.node=r("div.ootooltip"),this.triangle=r(`div.ootooltip_triangle.${this.options.position}`),this.contentNode=r("div.ootooltip_content"),this.node.append(this.triangle),this.node.append(this.contentNode),this.container.append(this.node),a(this,se,C8).call(this)},ee=new WeakSet,p8=function(){document.body.append(this.container)},ne=new WeakSet,u8=function(){this.container.remove()},se=new WeakSet,C8=function(){this.options.events&&(this.showFun||(this.showFun=this.show.bind(this)),this.hideFun||(this.hideFun=this.hide.bind(this)),this.options.events.show&&(this.options.events.show.split(/\s*,\s*/g).forEach(t=>{this.target.addEventListener(t,e=>{this.hideFun.clear&&this.hideFun.clear(),d1(this.showFun,this.options.defer,this)})}),this.options.showOnEnterCotent&&this.container.addEventListener("mouseenter",()=>{this.hideFun.clear&&this.hideFun.clear()})),this.options.events.hide&&(this.options.events.hide.split(/\s*,\s*/g).forEach(t=>{this.target.addEventListener(t,()=>{this.showFun.clear&&this.showFun.clear(),d1(this.hideFun,100,this)})}),this.options.showOnEnterCotent&&this.container.addEventListener("mouseleave",()=>{this.showFun.clear&&this.showFun.clear(),d1(this.hideFun,100,this)})))},ie=new WeakSet,m8=function(){const n=r.getPosition(this.target),t=r.getSize(this.target),e=r.getSize(this.node),i=r.getSize(this.triangle).x/2;let o=a(this,gt,k4).call(this,this.options.position,n,t,e,i);switch(this.options.position){case"top":o.y<0&&(o=a(this,mt,x4).call(this,"bottom",n,t,e,i));break;case"left":o.x<0&&(o=a(this,mt,x4).call(this,"right",n,t,e,i));break}if((this.options.position==="top"||this.options.position==="bottom")&&o.x<0){const d=o.x;o.flagX+=d,o.x=0,o.fromX=0}if((this.options.position==="left"||this.options.position==="right")&&o.y<0){const d=o.y;o.flagY+=d,o.y=0,o.fromY=0}return o},mt=new WeakSet,x4=function(n,t,e,i,o){return r.removeClass(this.triangle,this.options.position),r.addClass(this.triangle,n),this.options.position=n,a(this,gt,k4).call(this,this.options.position,t,e,i,o)},gt=new WeakSet,k4=function(n,t,e,i,o){const d={},h=this.options.emerge||0,u=this.options.offset||this.options.offset===0?this.options.offset:3;switch(n){case"bottom":d.x=d.fromX=t.x+e.x/2-i.x/2,d.y=t.y+e.y+o+u,d.fromY=d.y-h,d.flagX=i.x/2-o,d.flagY=0-o;break;case"left":d.x=t.x-i.x-o-u,d.fromX=d.x+h,d.y=d.fromY=t.y+e.y/2-i.y/2,d.flagX=i.x-o,d.flagY=i.y/2-o;break;case"right":d.x=t.x+e.x+o+u,d.fromX=d.x-h,d.y=d.fromY=t.y+e.y/2-i.y/2,d.flagX=0-o,d.flagY=i.y/2-o;break;default:d.x=d.fromX=t.x+e.x/2-i.x/2,d.y=t.y-i.y-o-u,d.fromY=d.y+h,d.flagX=i.x/2-o,d.flagY=i.y-o}return d},re=new WeakSet,g8=function(){this.options.autoClose>0&&window.setTimeout(()=>{this[this.options.events?"hide":"close"]()},this.options.autoClose)},oe=new WeakSet,f8=async function(n){const t=await w0[`./styles/tooltip/${n}.css`]();return t9(t.default,this.container)},ft=new WeakSet,E4=async function(){const n=d2.loadedStyles[this.options.style||"default"];if(n){const t=await Promise.resolve(n);r.addClass(this.container,t.id)}else{const t=this.options.style||"default";d2.loadedStyles[t]=a(this,oe,f8).call(this,t),d2.loadedStyles[t]=await d2.loadedStyles[t]}},le=new WeakSet,v8=async function(){if(this.options.resetContentOnShow||!this.contentLoaded)return this.target.dispatchEvent(new MouseEvent("tooltip")),r.empty(this.contentNode),this.contentLoaded=!0,await a(this,vt,M4).call(this,this.content),""},vt=new WeakSet,M4=async function(n){switch(v(n)){case"string":r.set(this.contentNode,"html",n);break;case"element":this.contentNode.append(n);break;case"function":const t=await Promise.resolve(n());await a(this,vt,M4).call(this,t)}},ae=new WeakSet,b8=function(){this.observerNode=this.observerNode||this.target.parentElement;var n=new MutationObserver((t,e)=>{if(t[0].removedNodes&&t[0].removedNodes.length){for(let i of t[0].removedNodes)if(i===this.target){this.hide(),e.disconnect();break}}});this.observerNode&&n.observe(this.observerNode,{childList:!0})},C(d2,"option",{position:"top",style:"default",autoClose:0,emerge:-8,offset:6,defer:600,events:{show:"mouseover",hide:"mouseout, mousedown"},showOnEnterCotent:!1,resetContentOnShow:!1}),C(d2,"loadedStyles",{});let R1=d2;function C9(s,n,t){return new R1(s,n,t)}const y0=`
<div class="content">
    <div class="icon"></div>
    <div class="text"></div>
</div>
`,he=class he extends k{constructor(){super();c(this,S2);c(this,de);c(this,c2);c(this,ce);C(this,"_elements",{icon:null,text:null});C(this,"_setPropMap",{type:()=>{this._canRender()&&a(this,c2,r1).call(this)},value:()=>{this._canRender()&&(a(this,c2,r1).call(this),a(this,S2,F1).call(this))},data:()=>{this._canRender()&&(a(this,c2,r1).call(this),a(this,S2,F1).call(this))},text:()=>{this._props.text?this._elements.text.textContent=this._props.text:a(this,S2,F1).call(this)},entity:()=>{this._canRender()&&(a(this,c2,r1).call(this),a(this,S2,F1).call(this))},isTooltip:()=>{this.tooltip=this._props.isTooltip!==!1?this.tooltip||this.defaultTooltip:null},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)}});this._initialize("oo-org",he,y0,_0)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_canRender(){return super._canRender(null,["value","data"])}_render(){this._canRender()&&a(this,c2,r1).call(this)}_connected(){this._canRender()&&(a(this,c2,r1).call(this),a(this,S2,F1).call(this)),this.tooltip=this._props.isTooltip!==!1?this.defaultTooltip:null,this.tip||(this.tip=C9(async()=>v(this.tooltip)==="function"?await this.tooltip():this.tooltip,this))}defaultTooltip(){const t=r("oo-card",{value:this.value,data:JSON.stringify(this.data),skin:"border: 0; shadow: 0"});return new Promise(e=>{t.addEventListener("loaded",()=>{e(t)})})}};S2=new WeakSet,F1=function(){this._props.text?this._elements.text.textContent=this._props.text:this._props.value?this._elements.text.textContent=Qt(this._props.value):this._elements.text.textContent=a(this,de,L8).call(this,this._props.data)},de=new WeakSet,L8=function(t){return t.dn?Qt(t.dn):t.name},c2=new WeakSet,r1=function(){switch(this._props.type){case"text":r.addClass(this._elements.icon,"hide");break;default:const t=a(this,ce,_8).call(this,this._props.entity||A3(this._props.value||this._props.data));v(t)==="function"?r.setStyle(this._elements.icon,"background-image",`url('${t(this._props.value||this._props.data)}')`):r.addClass(this._elements.icon,t),r.removeClass(this._elements.icon,"hide")}},ce=new WeakSet,_8=function(t){return{I:e=>{var i;return t5(((i=e.person)==null?void 0:i.dn)||e.dn||e)},P:e=>t5(e.dn||e),U:"ooicon-unit",G:"ooicon-group",R:"ooicon-role",UD:"ooicon-duty",UA:"ooicon-attribute",PA:"ooicon-attribute",PROCESS:"ooicon-process",APP:"ooicon-computer"}[t]},C(he,"prop",{type:"simple",value:"",text:"",data:{},entity:"",cssLink:"",skin:"",isTooltip:!0});let I3=he;const x0=`*{box-sizing:border-box}.content{--gap: .5em;--color: var(--oo-color-text2);--bg: var(--oo-default-radius);--radius: var(--oo-default-radius);--border: 1px solid var(--oo-color-gray-d);--hover-bg: var(--oo-color-light);--current-color: var(--oo-color-text-white);--current-bg: var(--oo-color-main)}.content{display:flex;gap:var(--gap);align-items:center}.prev,.next,.pages>div{padding:.2em;color:var(--color);background:var(--bg);border-radius:var(--radius);border:var(--border);cursor:pointer;display:flex;align-items:center;justify-content:center;min-width:1.788em;height:1.788em;transition:background .3s}.prev:hover,.next:hover,.pages>div:hover{background:var(--hover-bg)}.pages{display:flex;gap:var(--gap);align-items:center}.pages>.current{color:var(--current-color);background:var(--current-bg)}.pages>.current:hover{background:var(--current-bg)}.point3{transform:rotate(90deg);color:var(--color)}.jumper{margin-left:1em;display:flex;align-items:center;gap:.3em}.jumper oo-input{width:3.5em;text-align:center;-webkit-appearance:none;-moz-appearance:none;appearance:none}oo-input::-webkit-inner-spin-button,oo-input::-webkit-outer-spin-button{-webkit-appearance:none;-moz-appearance:none;appearance:none;margin:0}.hide{display:none}
`;customElements.get("oo-button")||customElements.define("oo-button",n9);const k0=`
<div class="content">
    <oo-button class="first" type="light"></oo-button>
    <div class="prev ooicon-arrow_back"></div>
    <div class="pages"></div>
    <div class="next ooicon-arrow_forward"></div>
    <oo-button  class="last" type="light"></oo-button>
    <div class="jumper hide">
        <span class="jumperTextLeft"></span>
        <oo-input class="jumperInput" appearance="none" type="number" input-style="text-align: center"></oo-input>
        <span>/</span>
        <span class="jumperPageCount"></span>
        <span class="jumperTextRight"></span>
    </div>
</div>
`;function c1(s,n){s.apply(n||this)}const ue=class ue extends k{constructor(){super();c(this,bt);c(this,Lt);c(this,h2);c(this,M1);c(this,p2);c(this,pe);c(this,_t);c(this,wt);C(this,"_elements",{first:null,last:null,prev:null,next:null,pages:null,jumper:null,jumperTextLeft:null,jumperTextRight:null,jumperPageCount:null,jumperInput:null});C(this,"_setPropMap",{cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},current:(t,e)=>{t!==e&&c1(a(this,h2,o1),this)},total:(t,e)=>{t!==e&&c1(a(this,h2,o1),this)},pageSize:(t,e)=>{t!==e&&c1(a(this,h2,o1),this)},pageCount:()=>{},pages:(t,e)=>{t!==e&&c1(a(this,h2,o1),this)},first:()=>{a(this,bt,S4).call(this)},last:()=>{a(this,Lt,A4).call(this)},jumper:()=>{r.checkClass(this._elements.jumper,"hide",!this._props.jumper)},jumperText:()=>{this._props.jumperText||(this._props.jumperText="到第{n}页"),a(this,wt,O4).call(this)}});this._initialize("oo-pagination",ue,k0,x0)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}async _render(){a(this,M1,b3).call(this),(this._props.pageCount>1||this._props.showSingle)&&(a(this,bt,S4).call(this),a(this,Lt,A4).call(this),c1(a(this,h2,o1),this),a(this,wt,O4).call(this))}_setEvent(){this._elements.prev.addEventListener("click",()=>{this.gotoPrev()}),this._elements.next.addEventListener("click",()=>{this.gotoNext()}),this._elements.first.addEventListener("click",()=>{this.gotoFirst()}),this._elements.last.addEventListener("click",()=>{this.gotoLast()}),this._elements.jumperInput&&this._elements.jumperInput.addEventListener("keydown",t=>{if(t.key==="Enter"||t.keyCode===13){const e=this._elements.jumperInput;!isNaN(e.value)&&e.value&&(this.gotoPage(parseInt(e.value)),e.value="")}})}setCurrent(t){this._props.current=t,a(this,M1,b3).call(this)}gotoPage(t){this.setCurrent(t),this.dispatchEvent(new CustomEvent("page",{detail:this._props.current})),this.setAttribute("current",this._props.current),c1(a(this,h2,o1),this)}pagesForward(){this.gotoPage(this._props.current+(this._props.pages-2))}pagesBack(){this.gotoPage(this._props.current-(this._props.pages-2))}gotoFirst(){this.gotoPage(1)}gotoLast(){this.gotoPage(this._props.pageCount)}gotoPrev(){this.gotoPage(--this._props.current)}gotoNext(){this.gotoPage(++this._props.current)}};bt=new WeakSet,S4=function(){const t=this._props.first;this._elements.first.textContent=t,r.checkClass(this._elements.first,"hide",!t)},Lt=new WeakSet,A4=function(){const t=this._props.last;this._elements.last.textContent=t,r.checkClass(this._elements.last,"hide",!t)},h2=new WeakSet,o1=function(t,e,i){if(a(this,M1,b3).call(this),r.empty(this._elements.pages),this._props.pageCount>1||this._props.showSingle)if(this._props.pageCount<=this._props.pages)for(let o=1;o<=this._props.pageCount;o++)a(this,p2,l1).call(this,o);else{a(this,p2,l1).call(this,1);const o=this._props.pages-2,d=parseInt(o/2);let h;if(this._props.current-d<=2)for(let u=1;u<=o;u++)h=u+1,a(this,p2,l1).call(this,h);else if(this._props.current+d>=this._props.pageCount-1){a(this,_t,T4).call(this);for(let u=o;u>=1;u--)h=this._props.pageCount-u,a(this,p2,l1).call(this,h)}else{a(this,_t,T4).call(this);for(let u=0;u<o;u++)h=this._props.current-(d-u),a(this,p2,l1).call(this,h,i)}h<this._props.pageCount-1&&a(this,pe,w8).call(this),a(this,p2,l1).call(this,this._props.pageCount)}},M1=new WeakSet,b3=function(){this._props.pageCount=Math.ceil(this._props.total/this._props.pageSize),this._props.pageCount<1&&(this._props.pageCount=1),this._props.current<1&&(this._props.current=1),this._props.current>this._props.pageCount&&(this._props.current=this._props.pageCount)},p2=new WeakSet,l1=function(t){const e=document.createElement("div");e.textContent=t,t===this._props.current&&r.addClass(e,"current"),e.addEventListener("click",()=>{this.gotoPage(t)}),this._elements.pages.append(e)},pe=new WeakSet,w8=function(){const t=document.createElement("div");r.addClass(t,"ooicon-point3 point3"),t.addEventListener("click",()=>{this.pagesForward()}),this._elements.pages.append(t)},_t=new WeakSet,T4=function(){const t=document.createElement("div");r.addClass(t,"ooicon-point3 point3"),t.addEventListener("click",()=>{this.pagesBack()}),this._elements.pages.append(t)},wt=new WeakSet,O4=function(){const t=this._props.jumperText.split("{n}");this._elements.jumperTextLeft.textContent=t[0],this._elements.jumperTextRight.textContent=(t==null?void 0:t[1])??"",this._elements.jumperPageCount.textContent=this._props.pageCount},C(ue,"prop",{total:200,pageSize:20,pageCount:0,pages:10,current:1,showSingle:!0,jumper:!1,first:"第一页",last:"最后一页",jumperText:"到第{n}页",border:!0,cssLink:"",skin:""});let z3=ue;const E0=`.content{\r
    --radius: 100%;\r
}\r
.check{\r
    border-radius: var(--radius);\r
    width: 0;\r
    height: 0;\r
    transition: width 0.2s, height 0.2s, background-color 0.2s;\r
    transform-origin: center;\r
    background-color: var(--bg);\r
}\r
.check.checked{\r
    width: 0.4em;\r
    height: 0.4em;\r
    background-color: var(--checked);\r
}\r
`;class M0 extends e5{constructor(){super(),this._init("oo-radio"),this._useCss(E0)}_unCheckedOther(){this.ownerDocument.querySelectorAll(`oo-radio[name='${this._props.name}']`).forEach(t=>{t!==this&&t.checked&&t._unchecked()})}_render(){this._elements.input.type="radio",this._elements.input.checked&&this._unCheckedOther(),super._render()}}var m9=globalThis&&globalThis.__spreadArray||function(s,n,t){if(t||arguments.length===2)for(var e=0,i=n.length,o;e<i;e++)(o||!(e in n))&&(o||(o=Array.prototype.slice.call(n,0,e)),o[e]=n[e]);return s.concat(o||Array.prototype.slice.call(n))},S0=function(){function s(n,t,e){this.name=n,this.version=t,this.os=e,this.type="browser"}return s}(),A0=function(){function s(n){this.version=n,this.type="node",this.name="node",this.os=process.platform}return s}(),T0=function(){function s(n,t,e,i){this.name=n,this.version=t,this.os=e,this.bot=i,this.type="bot-device"}return s}(),O0=function(){function s(){this.type="bot",this.bot=!0,this.name="bot",this.version=null,this.os=null}return s}(),P0=function(){function s(){this.type="react-native",this.name="react-native",this.version=null,this.os=null}return s}(),Z0=/alexa|bot|crawl(er|ing)|facebookexternalhit|feedburner|google web preview|nagios|postrank|pingdom|slurp|spider|yahoo!|yandex/,j0=/(nuhk|curl|Googlebot|Yammybot|Openbot|Slurp|MSNBot|Ask\ Jeeves\/Teoma|ia_archiver)/,g9=3,D0=[["aol",/AOLShield\/([0-9\._]+)/],["edge",/Edge\/([0-9\._]+)/],["edge-ios",/EdgiOS\/([0-9\._]+)/],["yandexbrowser",/YaBrowser\/([0-9\._]+)/],["kakaotalk",/KAKAOTALK\s([0-9\.]+)/],["samsung",/SamsungBrowser\/([0-9\.]+)/],["silk",/\bSilk\/([0-9._-]+)\b/],["miui",/MiuiBrowser\/([0-9\.]+)$/],["beaker",/BeakerBrowser\/([0-9\.]+)/],["edge-chromium",/EdgA?\/([0-9\.]+)/],["chromium-webview",/(?!Chrom.*OPR)wv\).*Chrom(?:e|ium)\/([0-9\.]+)(:?\s|$)/],["chrome",/(?!Chrom.*OPR)Chrom(?:e|ium)\/([0-9\.]+)(:?\s|$)/],["phantomjs",/PhantomJS\/([0-9\.]+)(:?\s|$)/],["crios",/CriOS\/([0-9\.]+)(:?\s|$)/],["firefox",/Firefox\/([0-9\.]+)(?:\s|$)/],["fxios",/FxiOS\/([0-9\.]+)/],["opera-mini",/Opera Mini.*Version\/([0-9\.]+)/],["opera",/Opera\/([0-9\.]+)(?:\s|$)/],["opera",/OPR\/([0-9\.]+)(:?\s|$)/],["pie",/^Microsoft Pocket Internet Explorer\/(\d+\.\d+)$/],["pie",/^Mozilla\/\d\.\d+\s\(compatible;\s(?:MSP?IE|MSInternet Explorer) (\d+\.\d+);.*Windows CE.*\)$/],["netfront",/^Mozilla\/\d\.\d+.*NetFront\/(\d.\d)/],["ie",/Trident\/7\.0.*rv\:([0-9\.]+).*\).*Gecko$/],["ie",/MSIE\s([0-9\.]+);.*Trident\/[4-7].0/],["ie",/MSIE\s(7\.0)/],["bb10",/BB10;\sTouch.*Version\/([0-9\.]+)/],["android",/Android\s([0-9\.]+)/],["ios",/Version\/([0-9\._]+).*Mobile.*Safari.*/],["safari",/Version\/([0-9\._]+).*Safari/],["facebook",/FB[AS]V\/([0-9\.]+)/],["instagram",/Instagram\s([0-9\.]+)/],["ios-webview",/AppleWebKit\/([0-9\.]+).*Mobile/],["ios-webview",/AppleWebKit\/([0-9\.]+).*Gecko\)$/],["curl",/^curl\/([0-9\.]+)$/],["searchbot",Z0]],f9=[["iOS",/iP(hone|od|ad)/],["Android OS",/Android/],["BlackBerry OS",/BlackBerry|BB10/],["Windows Mobile",/IEMobile/],["Amazon OS",/Kindle/],["Windows 3.11",/Win16/],["Windows 95",/(Windows 95)|(Win95)|(Windows_95)/],["Windows 98",/(Windows 98)|(Win98)/],["Windows 2000",/(Windows NT 5.0)|(Windows 2000)/],["Windows XP",/(Windows NT 5.1)|(Windows XP)/],["Windows Server 2003",/(Windows NT 5.2)/],["Windows Vista",/(Windows NT 6.0)/],["Windows 7",/(Windows NT 6.1)/],["Windows 8",/(Windows NT 6.2)/],["Windows 8.1",/(Windows NT 6.3)/],["Windows 10",/(Windows NT 10.0)/],["Windows ME",/Windows ME/],["Windows CE",/Windows CE|WinCE|Microsoft Pocket Internet Explorer/],["Open BSD",/OpenBSD/],["Sun OS",/SunOS/],["Chrome OS",/CrOS/],["Linux",/(Linux)|(X11)/],["Mac OS",/(Mac_PowerPC)|(Macintosh)/],["QNX",/QNX/],["BeOS",/BeOS/],["OS/2",/OS\/2/]];function F0(s){return s?v9(s):typeof document>"u"&&typeof navigator<"u"&&navigator.product==="ReactNative"?new P0:typeof navigator<"u"?v9(navigator.userAgent):z0()}function $0(s){return s!==""&&D0.reduce(function(n,t){var e=t[0],i=t[1];if(n)return n;var o=i.exec(s);return!!o&&[e,o]},!1)}function v9(s){var n=$0(s);if(!n)return null;var t=n[0],e=n[1];if(t==="searchbot")return new O0;var i=e[1]&&e[1].split(".").join("_").split("_").slice(0,3);i?i.length<g9&&(i=m9(m9([],i,!0),N0(g9-i.length),!0)):i=[];var o=i.join("."),d=I0(s),h=j0.exec(s);return h&&h[1]?new T0(t,o,d,h[1]):new S0(t,o,d)}function I0(s){for(var n=0,t=f9.length;n<t;n++){var e=f9[n],i=e[0],o=e[1],d=o.exec(s);if(d)return i}return null}function z0(){var s=typeof process<"u"&&process.version;return s?new A0(process.version.slice(1)):null}function N0(s){for(var n=[],t=0;t<s;t++)n.push("0");return n}const b9=function(s,n,t){const e=n.toLowerCase();e==="text"?s.textContent=t:e==="html"?s.innerHTML=t:e==="styles"?g.setStyles(s,t):s.setAttribute(n,t)},R0=function(s,n){const t=n.toLowerCase();if(t==="text")return s.innerText||s.textContent||"";if(t==="html")return s.innerHTML||"";if(t==="value")switch(s.tagName.toString().toLowerCase()){case"select":return s.options[s.selectedIndex].value;default:return s.value}return s.getAttribute(n)},s5=function(s){const n=Y0(s||"").split(" "),t={};return n.filter(function(e){if(e!==""&&!t[e])return t[e]=e})},L9=function(s){return!s.tagName||/^(?:body|html)$/i.test(s.tagName)},_9=function(s){return/html/i.test(s.tagName)},i5=function(s,n){return parseInt(g.getStyle(s,n))||0},B0=function(s){return i5(s,"border-top-width")},V0=function(s){return i5(s,"border-left-width")},W0=function(s,n){return{left:s.x-i5(n,"margin-left"),top:s.y-i5(n,"margin-top")}},g=(s,n)=>{let t;const e=s.replace(/^\S*?(?=\.|#|$)/,h=>(t=h,""));let i="",o;e&&(o=e.split(".").filter(u=>!!u).map(u=>{const m=u.split("#"),f=m.shift();return i=m.pop()||i,f}));const d=document.createElement(t);return i&&d.setAttribute("id",i),o&&o.length&&d.setAttribute("class",o.join(" ")),n&&g.set(d,n),d};Object.assign(g,{el:(s,n)=>w9(s)==="string"&&s?(n||document).querySelector(s):s,els:(s,n)=>(n||document).querySelectorAll(s),addClass:(s,n)=>{const t=g.el(s);return s5(n).forEach(e=>{t.classList.add(e)}),t},removeClass:(s,n)=>{const t=g.el(s);return s5(n).forEach(e=>{t.classList.remove(e)}),t},hasClass:(s,n)=>g.el(s).classList.contains(n),toggleClass:(s,n,t=!1)=>{const e=g.el(s);return s5(n).forEach(i=>{let o=t;(o==null||o===!1)&&(o=!g.hasClass(e,i)),o?g.addClass(e,i):g.removeClass(e,i)}),e},checkClass:(s,n,t)=>{const e=g.el(s);return s5(n).forEach(i=>{t?g.addClass(e,i):g.removeClass(e,i)}),e},mapProps:(s,n)=>{const t=g.el(s);for(const e in n){const i=H0(e);if(typeof n[e]=="boolean")n[e]=t.hasAttribute(e)||t.hasAttribute(i)||!1;else{const o=t.getAttribute(e)||t.getAttribute(i)||"";o!==n[e]&&(n[e]=o)}}return t},toggleAttr:(s,n,t)=>{const e=g.el(s);return t?e.setAttribute(n,t):e.removeAttribute(n),e},set:(s,n,t)=>{const e=g.el(s);return typeof n=="object"?Object.keys(n).forEach(i=>{b9(e,i,n[i])}):b9(e,n,t),e},get:(s,n)=>{const t=g.el(s);return R0(t,n)},setProperty:(s,n,t)=>{const e=g.el(s);return e.setAttribute(n,t),e},getScroll:s=>{const n=g.el(s);return{x:n.scrollLeft,y:n.scrollTop}},getScrolls:s=>{let t=g.el(s).parentNode,e={x:0,y:0};for(;t&&!L9(t);)e.x+=t.scrollLeft,e.y+=t.scrollTop,t=t.parentNode;return e},getOffsetParent:s=>{let n=s.offsetParent;for(;n&&["table","td","th"].includes(n.tagName.toString().toLowerCase());)n=n.offsetParent;return n||document.body},getPosition:(s,n)=>{const t=g.el(s);for(n===window&&(n=document.documentElement);n&&g.getStyle(n,"display")==="contents";)n=n.parentElement;for(;s&&g.getStyle(s,"display")==="contents";)s=s.parentElement;const e=s.getBoundingClientRect(),i=document.documentElement,o=g.getScroll(i),d=g.getScrolls(t),h=g.getStyle(t,"position")==="fixed",u={x:e.left+d.x+(h?0:o.x)-i.clientLeft,y:e.top+d.y+(h?0:o.y)-i.clientTop},m=g.getScrolls(t),f={x:u.x-m.x,y:u.y-m.y};if(n){const w=g.getPosition(n);return{x:f.x-w.x-V0(n),y:f.y-w.y-B0(n)}}return f},setPosition:(s,n)=>{const t=g.el(s);return g.setStyles(t,W0(n.this))},getSize:s=>{const n=g.el(s);if(_9(n))return{x:n.clientWidth,y:n.clientHeight};{const t=n.getBoundingClientRect();return{x:t.width,y:t.height}}},getScrollSize:s=>{const n=g.el(s);if(/^(?:body|html)$/i.test(n.tagName)){const t=document.documentElement,e=document.body;return{x:Math.max(t.scrollWidth,e.scrollWidth),y:Math.max(t.scrollHeight,e.scrollHeight)}}return{x:n.scrollWidth,y:n.scrollHeight}},getStyle:function(s,n){const t=g.el(s),e=N3(n);let i=t.style[e];return i||(i=window.getComputedStyle(t)[e]),i},getStyles:s=>{const n=[...arguments],t=g.el(n.shift()),e={};return n.forEach(i=>{e[i]=g.getStyle(t,i)}),e},setStyle:(s,n,t)=>{const e=g.el(s);return typeof n=="object"?Object.keys(n).forEach(i=>{e.style[N3(i)]=n[i]}):e.style[N3(n)]=t,e},setStyles:(s,n)=>g.setStyle(s,n),isPositioned:s=>{const n=g.el(s),t=g.getStyle(n,"position");return t&&["absolute","fixed","relative","sticky"].includes(t)},show:s=>{const n=g.el(s);return g.getStyle(n,"display")==="none"&&g.setStyle(n,"display",n.dataset.storeDisplay||"block"),n},hide:s=>{const n=g.el(s),t=g.getStyle(n,"display");return t!=="none"&&(n.dataset.storeDisplay=t,g.setStyle(n,"display","none")),n},getParentSrcollNode:s=>{let t=g.el(s).parentElement,e=g.getStyle(t,"overflow"),i=g.getStyle(t,"overflow-y");for(;t&&(g.getScrollSize(t).y<=g.getSize(t).y||!["auto","scroll"].includes(e)&&!["auto","scroll"].includes(i));)t=t.parentElement,t&&(e=g.getStyle(t,"overflow"),i=g.getStyle(t,"overflow-y"));return t||document.documentElement},getParent:function(s,n){if(n){let t=s.parentElement;for(;t&&!t.matches(n);)t=t.parentElement;return t}else return s.parentElement},isBody:s=>L9(g.el(s)),isHtml:s=>_9(g.el(s)),empty:s=>{const n=g.el(s);for(;n.childNodes.length;)n.removeChild(n.childNodes[0]);return n},peel:(s,n)=>{const{node:t,position:e}=n&&w9(n)==="element"?{node:n,position:"beforeend"}:{node:s,position:"beforebegin"};let i=s.firstElementChild;for(;i;)t.insertAdjacentElement(e,i),i=s.firstElementChild;s.remove()},getPositionParent(s){let n=g.el(s).parentElement;for(;n;){const t=n.tagName.toString().toLowerCase();if(t==="application")return n;if(t==="oo-dialog")return n._content;n=n.parentElement}return document.body},overlap(s,n,t=document.body,e){const i=this.getSize(g.el(n)),o=this.getPosition(g.el(n),t),d=g.el(s);this.setStyles(d,{position:"absolute",width:`${i.x}px`,height:`${i.y}px`,top:`${o.y}px`,left:`${o.x}px`}),d.isConnected||t.append(d)},appendTop(s,n){const t=s.firstChild;return t?s.insertBefore(n,t):s.appendChild(n)}}),F0();function w9(s){if(s==null)return"null";if(Array.isArray(s))return"array";if(s instanceof Map)return"map";if(s instanceof Set)return"set";if(s===window)return"window";if(s instanceof Date)return"date";if(s instanceof RegExp)return"regexp";if(s instanceof Error)return"error";if(s instanceof Event)return"event";if((typeof s=="function"||typeof s=="object")&&typeof s.then=="function")return"promise";if(s.nodeName){if(s.nodeType===1)return"element";if(s.nodeType===3)return/\S/.test(s.nodeValue)?"textnode":"whitespace";if(s.nodeType===9)return"document";if(s.nodeType||s.nodeType===0)return s.nodeName}else if(typeof s.length=="number"&&s.callee)return"arguments";return typeof s}function H0(s){return s.replace(/([A-Z])/g,"-$1").toLowerCase()}function N3(s){return s.replace(/-(\w)/g,function(n,t){return t.toUpperCase()})}function Y0(s){return s.replace(/\s+/g," ").trim()}class q0 extends n5{constructor(){super();c(this,Ce);c(this,me);a(this,Ce,y8).call(this),this._init("oo-radio-group")}_appendItemCheckValue(t){this._props.value?t.value===this._props.value&&t.setAttribute("checked",!0):t._unchecked()}_itemChange(t){t.currentTarget.checked?this.value=t.currentTarget.value:this.value="",this._elements.input.value=this.value,this.unInvalidStyle(),this.dispatchEvent(new Event("change",t))}_removeItem(t){super._removeItem(t)}get text(){return a(this,me,x8).call(this)}}Ce=new WeakSet,y8=function(){this.setPropMap=Object.assign(this._setPropMap,{value:()=>{if(this._props.value){for(const t of this._items)if(t.value===this._props.value){t.setAttribute("checked",!0);break}}this._elements.input.value=this._props.value}})},me=new WeakSet,x8=function(){for(const t of this._items)if(t.checked)return t.text;return""};const U0=`.content{\r
    --option-bg: var(--oo-color-text-white);\r
    --option-over: var(--oo-color-text2);\r
    --option-over-bg: var(--oo-color-gray-e);\r
    --option-select: var(--oo-color-main);\r
    --option-select-bg: var(--oo-color-text-white);\r
    --option-shadow-color: var(--oo-color-gray-d);\r
    --option-shadow:  0 0 10px 1px var(--option-shadow-color);\r
    --option-border: var(--oo-color-gray-d);\r
    --option-selected: var(--oo-color-main);\r
    --option-selected-bg: var(--oo-color-light);\r
    --option-group-bg: var(--oo-color-text-white);\r
    --option-group-color: var(--oo-color-gray-9);\r
}\r
\r
.drop{\r
    text-align: left;\r
    transition: color 0.2s;\r
    font-size: 0.8em;\r
    margin-right: 0.5em;\r
    color: var(--label);\r
    cursor: pointer;\r
    transform-origin: center;\r
}\r
.drop.down{\r
    transform: rotate(180deg);\r
}\r
.readmode .drop{\r
    display: none;\r
}\r
\r
.box{\r
    position: relative;\r
}\r
.box.focus .drop{\r
    color: var(--focus);\r
}\r
.box.disabled .drop.disabled{\r
    opacity: 0.6;\r
    cursor: not-allowed;\r
    color: var(--label);\r
}\r
.box.readmode.focus{\r
    color: var(--label);\r
}\r
\r
.options-area{\r
    position: absolute;\r
    border: 1px solid var(--option-border);\r
    background-color: var(--option-bg);\r
    border-radius: var(--oo-area-radius);\r
    box-shadow: var(--option-shadow);\r
    z-index: 100;\r
    /*min-width: 10em;*/\r
    transform-origin: top;\r
    transition: height 0.2s, opacity 0.2s, transform 0.2s;\r
}\r
.options-area.visible{\r
    transform: scale(1);\r
    opacity: 1;\r
}\r
.options-area.invisible{\r
    transform: scale(1, 0.001);\r
    opacity: 0;\r
}\r
.arrow{\r
    position: absolute;\r
    top: -6px;\r
    width: 10px;\r
    height: 10px;\r
    z-index: -1;\r
}\r
.arrow:before {\r
    border-bottom-color: transparent!important;\r
    border-right-color: transparent!important;\r
    border-top: 1px solid var(--option-border);\r
    border-left: 1px solid var(--option-border);\r
    border-top-left-radius: 3px;\r
    background-color: var(--option-bg);\r
    right: 0;\r
    position: absolute;\r
    width: 10px;\r
    height: 10px;\r
    z-index: -1;\r
    content: " ";\r
    transform: rotate(45deg);\r
}\r
.options-content{\r
    max-height: 17em;\r
    min-height: 3em;\r
    padding: 0.5em 0;\r
    border-radius: var(--oo-area-radius);\r
    overflow: auto;\r
}\r
.options-content::-webkit-scrollbar {\r
    width:8px;\r
    height: 8px;\r
    border-radius: 8px;\r
    background-color: #dddddd;\r
}\r
.options-content::-webkit-scrollbar-thumb{\r
    width: 8px;\r
    border-radius: 8px;\r
    background-color: #bbbbbb;\r
    cursor: pointer;\r
}\r
.options-content::-webkit-scrollbar-thumb:hover{\r
    width: 8px;\r
    border-radius: 8px;\r
    background-color: #666666;\r
    cursor: pointer;\r
}\r
\r
.view{\r
    position: relative;\r
    overflow: hidden;\r
}\r
.input{\r
    width: 100%;\r
    cursor: pointer;\r
}\r
.readmode .input {\r
     display: none;\r
    cursor: default;\r
}\r
.viewText{\r
    position: absolute;\r
    width: 100%;\r
    height: 100%;\r
    display: none;\r
    /*align-items: center;*/\r
    top: 0;\r
    left: 0;\r
    overflow: hidden;\r
    word-break: keep-all;\r
    text-overflow: ellipsis;\r
    cursor: pointer;\r
    padding: 0.5em 0.6em;\r
}\r
.readmode .viewText{\r
    position: static;\r
}\r
.disabled .viewText{\r
    cursor: not-allowed;\r
    opacity: 0.6;\r
}\r
`,O1=class O1 extends P2{constructor(){super();c(this,ge);c(this,yt);c(this,xt);c(this,fe);c(this,kt);c(this,ve);c(this,T1);c(this,q,!1);c(this,U2,[]);c(this,P,null);c(this,S1,null);c(this,A1,[]);c(this,u2,0);c(this,X2,null);a(this,ge,k8).call(this),this._init("oo-select"),this._useCss(U0)}static get observedAttributes(){return O1.prop=Object.assign({},P2.prop,O1.prop),Object.keys(O1.prop).map(t=>_(t))}_render(){p(this,q,!1),this._elements.drop=r("div.drop.ooicon-drop_down"),this._elements.view.insertAdjacentElement("afterend",this._elements.drop),p(this,P,this.querySelector("div.options-area")),l(this,P)||(p(this,P,r("div.options-area.invisible")),r.set(l(this,P),"html",'<div class="arrow"></div><div class="options-content"><slot name="items"></slot></div>'),this._elements.box.insertAdjacentElement("beforeend",l(this,P))),this._elements.input.setAttribute("readonly",!0)}_removeItem(t){l(this,U2).splice(l(this,U2).indexOf(t),1),t.removeAttribute("slot")}_appendItem(t){l(this,U2).push(t),t.setAttribute("slot","items"),t.setAttribute("skin",this._props.skin);const e=r.getSize(this._elements.view),i=r.getSize(t._elements.area),o=Math.max(i.x,e.x);o>l(this,u2)&&(p(this,u2,o),a(this,yt,P4).call(this)),this._props.size&&t.setAttribute("size",this._props.size),this._appendItemCheckValue(t),this._setPropMap.$default("value")}checkMaxWidth(t){t>l(this,u2)&&(p(this,u2,t),a(this,yt,P4).call(this))}_appendItemCheckValue(t){this._props.value&&t.value===this._props.value&&(t.selected=!0)}_removeGroup(t){l(this,A1).splice(l(this,A1).indexOf(t),1),t.removeAttribute("slot")}_appendGroup(t){l(this,A1).push(t),t.setAttribute("slot","items"),t.setAttribute("skin",this._props.skin),this._props.size&&option.setAttribute("size",this._props.size)}_selected(t){if(t.selected){l(this,X2)&&l(this,X2)!==t&&(l(this,X2).selected=!1),p(this,X2,t);const e=t._props.text||t._elements.label.textContent;this._elements.input.value=e,this._elements.viewText.textContent=e,this.value=t._props.value||e,this.text=e,this.unInvalidStyle(),this.dispatchEvent(new CustomEvent("change"))}a(this,T1,L3).call(this)}_setEvent(){super._setEvent(),this._elements.box.addEventListener("click",()=>{!this._props.disabled&&!this._props.readmode&&!this._props.read&&!this._props.readonly&&a(this,xt,Z4).call(this)}),this._elements.input.addEventListener("click",t=>{!this._props.disabled&&!this._props.readmode&&!this._props.read&&!this._props.readonly&&(a(this,xt,Z4).call(this),t.stopPropagation())}),l(this,P).addEventListener("click",t=>{t.stopPropagation()}),l(this,P).addEventListener("mousedown",t=>{t.stopPropagation()}),this._content.firstElementChild.addEventListener("mousedown",t=>{t.stopPropagation()})}get value(){return this._props.value}set value(t){this.setAttribute("value",t)}get text(){return this._props.text}set text(t){this._props.text=t}};ge=new WeakSet,k8=function(){this._setPropMap=Object.assign(this._setPropMap,{disabled:()=>{r.toggleAttr(this._elements.input,"disabled",this._props.disabled),r.checkClass(this._elements.box,"disabled",this._props.disabled),r.checkClass(this._elements.label,"disabled",this._props.disabled),r.checkClass(this._elements.drop,"disabled",this._props.disabled)},value:()=>{if(this._props.value){for(const t of l(this,U2))if(t.value===this._props.value){t.selected=!0;break}}},readonly:()=>{},readmode:()=>{r.checkClass(this._elements.box,"readmode",this._props.readmode),r.checkClass(this._elements.label,"readmode",this._props.readmode),this._props.readmode?r.toggleAttr(this._elements.input,"style",this._props.viewStyle):r.toggleAttr(this._elements.input,"style",""),this._setPropMap.required()},autoSize:()=>{r.setStyle(this._elements.view,"width",`${l(this,u2)}px`)},read:()=>{},viewStyle:()=>{this._props.readmode&&r.toggleAttr(this._elements.viewText,"style",this._props.viewStyle)}})},q=new WeakMap,U2=new WeakMap,P=new WeakMap,S1=new WeakMap,A1=new WeakMap,u2=new WeakMap,yt=new WeakSet,P4=function(){this._props.autoSize&&(this.setMinWidthTimer&&window.clearTimeout(this.setMinWidthTimer),this.setMinWidthTimer=setTimeout(()=>{r.setStyle(this._elements.view,"width",`${l(this,u2)}px`),this.setMinWidthTimer=null},10))},X2=new WeakMap,xt=new WeakSet,Z4=function(){p(this,q,!l(this,q)),l(this,q)?a(this,ve,M8).call(this):a(this,T1,L3).call(this)},fe=new WeakSet,E8=function(){const t=r.getPosition(this._elements.box,this._content),e=r.getSize(this._elements.box);t.x;const i=t.y+e.y+6+e.y*.2,o=e.x;r.setStyles(l(this,P),{top:i+"px",width:"100%"});const d=o/2-6;r.setStyles(l(this,P).querySelector(".arrow"),{left:d+"px"})},kt=new WeakSet,j4=function(){r.checkClass(this._elements.drop,"down",l(this,q)),r.checkClass(this._elements.box,"focus",l(this,q)),r.checkClass(this._elements.label,"focus",l(this,q))},ve=new WeakSet,M8=function(){this._props.readmode||(p(this,q,!0),a(this,kt,j4).call(this),a(this,fe,E8).call(this),r.removeClass(l(this,P),"invisible"),r.addClass(l(this,P),"visible"),this.ownerDocument.dispatchEvent(new MouseEvent("mousedown")),p(this,S1,a(this,T1,L3).bind(this)),this.ownerDocument.addEventListener("mousedown",l(this,S1)),this.unInvalidStyle())},T1=new WeakSet,L3=function(){p(this,q,!1),a(this,kt,j4).call(this),r.removeClass(l(this,P),"visible"),r.addClass(l(this,P),"invisible"),this.ownerDocument.removeEventListener("mousedown",l(this,S1))},C(O1,"prop",{read:!1});let R3=O1;const X0=`<div class="content">\r
	<slot name="before-outer"></slot>\r
	<div class="label hide">\r
		<div class="labelText"></div>\r
		<slot name="label"></slot>\r
	</div>\r
	<div style="width: 100%;">\r
		<div style="display: flex; align-items: center;">\r
			<div class="box">\r
				<slot name="before-inner-before"></slot>\r
				<div class="prefix"></div>\r
				<slot name="before-inner-after"></slot>\r
				<div class="input">\r
					<div class="placeholder hide">&nbsp;</div>\r
					<div class="inputContent"></div>\r
					<input type="text" class="inputValidity" style="display: none"/>\r
				</div>\r
				<slot name="after-inner-before"></slot>\r
				<div class="suffix"></div>\r
				<slot name="after-inner-after"></slot>\r
			</div>\r
			<div class="requiredFlag hide">&#10039;</div>\r
		</div>\r
		<div class="invalidHint ooicon-error">zsasd</div>\r
	</div>\r
	<slot name="after-outer"></slot>\r
\r
</div>\r
`,G0=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    --label: var(--oo-color-text2);\r
    --icon-left: var(--oo-color-text2);\r
    --icon-right: var(--oo-color-text2);\r
    --focus: var(--oo-color-main);\r
    --border: var(--oo-color-gray1);\r
    --radius: var(--oo-default-radius);\r
    --input: var(--oo-color-text);\r
    --placeholder: var(--oo-color-text4);\r
    --disabled: var(--oo-color-gray2);\r
    --bg:  var(--oo-color-bg-white);\r
    --shadow: var(--oo-shadow-border);\r
    --shadow-focus: var(--oo-shadow-border-focus)\r
}\r
.content {\r
    position: relative;\r
    width: 100%;\r
    height: 100%;\r
    display: flex;\r
    align-items: center;\r
}\r
.label {\r
    white-space: nowrap;\r
    transition: color 0.2s;\r
    display: inline-flex;\r
    align-items: center;\r
    color: var(--label);\r
    height: 100%;\r
}\r
.labelText{\r
    padding: 0.5em 0.35em;\r
    margin-right: 0.5em;\r
    display: flex;\r
    align-items: center;\r
    height: 100%;\r
}\r
.flex-end{\r
    justify-content: flex-end;\r
}\r
.hide{\r
    display: none;\r
}\r
.box {\r
    border-radius: var(--radius);\r
    box-shadow: var(--shadow);\r
    display: inline-flex;\r
    align-items: center;\r
    height: 100%;\r
    width: 100%;\r
    background: var(--bg);\r
    transition: border-color 0.2s, box-shadow 0.2s;\r
    cursor: pointer;\r
}\r
.box:has(input.invalid){\r
    background: var(--oo-color-error-bg);\r
    box-shadow: var(--oo-shadow-border-error);\r
}\r
.input {\r
    border: 0;\r
    outline: none;\r
    color: var(--input);\r
    border-radius: var(--radius);\r
    padding: 0.35em 0.6em;\r
    height: 100%;\r
    min-height: 1.89em;\r
    /*min-width: 12.28em;*/\r
    box-shadow: none;\r
    width: inherit;\r
    /*min-width: 13.365875em;*/\r
    font-size: 1em;\r
    background: transparent;\r
    position: relative;\r
}\r
\r
div.left-icon{\r
    width: 1em;\r
    text-align: right;\r
    transition: color 0.2s;\r
    font-size: 0.9em;\r
    margin-left: 0.6em;\r
    color: var(--icon-left);\r
}\r
div.right-icon{\r
    width: 1em;\r
    text-align: left;\r
    transition: color 0.2s;\r
    font-size: 0.9em;\r
    margin-right: 0.6em;\r
    color: var(--icon-right);\r
}\r
\r
\r
.box.focus {\r
    box-shadow: var(--shadow-focus);\r
    outline: none;\r
    color: var(--focus);\r
}\r
.box.focus  div.left-icon{\r
    color: var(--focus);\r
}\r
.box.focus  div.right-icon{\r
    color: var(--focus);\r
}\r
\r
.label.focus {\r
    color: var(--focus);\r
    outline: none;\r
}\r
\r
.box.disabled {\r
    background-color: var(--disabled)!important;\r
    outline: none;\r
    cursor: not-allowed;\r
}\r
.box.disabled div.left-icon{\r
    /*color: var(--oo-color-text4);*/\r
    opacity: 0.6;\r
}\r
.box.disabled div.right-icon{\r
    /*color: var(--oo-color-text4);*/\r
    opacity: 0.6;\r
}\r
.label.disabled {\r
    /*color: var(--oo-color-text4);*/\r
    outline: none;\r
    cursor: not-allowed;\r
}\r
input:disabled, textarea:disabled{\r
    opacity: 0.45;\r
    cursor: not-allowed;\r
}\r
.box.readmode {\r
    outline: none;\r
    cursor: default;\r
    box-shadow: none;\r
}\r
.box.readmode div.left-icon{\r
    color: inherit;\r
    display: none;\r
}\r
.box.readmode div.right-icon{\r
    color: inherit;\r
    display: none;\r
}\r
.label.readmode {\r
    outline: none;\r
    cursor: default;\r
}\r
.label.readmode.focus {\r
    color: inherit;\r
    outline: none;\r
}\r
\r
input:focus, textarea:focus {\r
    border: 0;\r
    outline: none;\r
    box-shadow: none;\r
}\r
\r
.list{\r
    min-width: 13.365875em;\r
    width: 100%;\r
    padding: 0.3em 0.5em;\r
    height: 100%;\r
    min-height: 1.3em;\r
}\r
\r
.placeholder{\r
    position: absolute;\r
    top: 0;\r
    left: 0;\r
    color: var(--placeholder);\r
    padding: 0.5em 0.6em;\r
    white-space: nowrap;\r
    text-overflow: ellipsis;\r
    width: 100%;\r
    overflow: hidden;\r
}\r
\r
.inputContent{\r
    color: var(--oo-color-text2);\r
    display: flex;\r
    flex-wrap: wrap;\r
    align-items: center;\r
    padding: 0.051em;\r
}\r
.item{\r
    padding: 0.15em 0.3em;\r
    border-radius: var(--oo-default-radius);\r
    display: inline-flex;\r
    align-items: baseline;\r
    font-size: 1em;\r
}\r
.item:hover{\r
    background-color: var(--oo-color-gray-e);\r
}\r
\r
.itemIcon{\r
    width: 1.25em;\r
    height: 1.25em;\r
    background-position: center;\r
    background-size: cover;\r
    background-repeat: no-repeat;\r
    border-radius: var(--oo-default-radius);\r
    opacity: 0.6;\r
}\r
\r
.inputValidity{\r
    position: absolute;\r
    width: 1px;\r
    height: 1px;\r
    bottom: 0;\r
    left: 50%;\r
    border: 0;\r
    outline: none;\r
    overflow: hidden;\r
    display: block;\r
    font-size: 1px;\r
    opacity: 0;\r
}\r
div.invalidHint{\r
    /*position: absolute;*/\r
    color: var(--oo-color-error);\r
    height: 0;\r
    overflow: hidden;\r
    word-break: break-all;\r
    transition: margin-top 0.2s, height 0.2s;\r
}\r
div.invalidHint.show{\r
    margin: 0.3em 0 0 0em;\r
    height: unset;\r
    line-height: 1.2em;\r
}\r
div.invalidHint.show::before{\r
    line-height: 1.4em;\r
    margin-right: 0.2em;\r
}\r
.requiredFlag{\r
    color: var(--oo-color-highlight);\r
    padding-left: 0.3em;\r
    font-size: 0.75em;\r
}\r
\r
/*.one-line{*/\r
/*    height: calc(26.27px + 0.35em + 0.35em);*/\r
/*    overflow: hidden;*/\r
/*}*/\r
`,_e=class _e extends k{constructor(){super();c(this,Et);c(this,G2);c(this,be);c(this,Le);C(this,"_elements",{label:null,input:null,box:null,prefix:null,suffix:null,labelText:null,placeholder:null,inputContent:null,inputValidity:null,invalidHint:null,requiredFlag:null});C(this,"_setPropMap",{leftIcon:t=>{r.removeClass(this._elements.prefix,`ooicon-${t} left-icon`),this._props.leftIcon&&r.addClass(this._elements.prefix,`ooicon-${this._props.leftIcon} left-icon`)},rightIcon:t=>{r.removeClass(this._elements.suffix,`ooicon-${t} right-icon`),this._props.rightIcon&&r.addClass(this._elements.suffix,`ooicon-${this._props.rightIcon} right-icon`)},itemType:t=>{this._props.itemType||(this._props.itemType="simple"),t!==this._props.itemType&&this._elements.inputContent.querySelectorAll("oo-org").forEach(i=>{i.setAttribute("type",this._props.itemType)})},isTooltip:()=>{const t=this._props.isTooltip!==!1;this._elements.inputContent.querySelectorAll("oo-org").forEach(i=>{i.setAttribute("is-tooltip",t)})},disabled:()=>{r.toggleAttr(this._elements.input,"disabled",this._props.disabled),r.checkClass(this._elements.box,"disabled",this._props.disabled),r.checkClass(this._elements.label,"disabled",this._props.disabled)},readmode:()=>{(!this.value||!this.value.length)&&r.checkClass(this._elements.placeholder,"hide",this._props.readmode),r.toggleAttr(this._elements.input,"readmode",this._props.readmode),r.checkClass(this._elements.box,"readmode",this._props.readmode),r.checkClass(this._elements.label,"readmode",this._props.readmode),this._props.readmode?r.toggleAttr(this._elements.input,"style",this._props.viewStyle):r.toggleAttr(this._elements.input,"style",""),this._setPropMap.required()},label:()=>{this._props.label?(this._elements.labelText.textContent=this._props.label,r.removeClass(this._elements.label,"hide")):(this._elements.labelText.textContent="",r.addClass(this._elements.label,"hide"))},skin:()=>{this._useSkin(this._props.skin)},value:()=>{a(this,Et,D4).call(this,this._props.value)},placeholder:()=>{this.value&&this.value.length?r.addClass(this._elements.placeholder,"hide"):(this._elements.inputContent.innerHTML='<div style="padding: 0.1em;">&nbsp</div>',!this._props.readmode&&!this._props.readonly&&!this._props.disabled&&r.removeClass(this._elements.placeholder,"hide"),this._props.placeholder?this._elements.placeholder.textContent=this._props.placeholder:this._elements.placeholder.innerHTML="&nbsp")},readonly:()=>{this._setPropMap.placeholder()},inputStyle:()=>{r.toggleAttr(this._elements.input,"style",this._props.inputStyle)},labelStyle:()=>{r.toggleAttr(this._elements.labelText,"style",this._props.labelStyle)},labelAlign:()=>{switch(this._props.labelAlign){case"right":r.setStyle(this._elements.labelText,"justify-content","flex-end");break;case"center":r.setStyle(this._elements.labelText,"justify-content","center");break;default:r.setStyle(this._elements.labelText,"justify-content","flex-start")}},viewStyle:()=>{this._props.readmode&&r.toggleAttr(this._elements.input,"style",this._props.viewStyle)},bgcolor:()=>{this._props.bgcolor?r.setStyle(this._elements.box,"background-color",this._props.bgcolor):r.setStyle(this._elements.box,"background-color","transparent")},required:()=>{this._props.required?this._elements.inputValidity.setAttribute("required",this._props.required):this._elements.inputValidity.removeAttribute("required"),r.checkClass(this._elements.requiredFlag,"hide",!this._props.required||this._props.readmode||this._props.disabled)},$default:()=>{}})}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}addValue(t){const e=t.dn||t;switch(this._props.value){case"string":(!this._props.count||this._props.value.split(/\s*,\s*/g).length<this._props.count)&&(this._props.value+=", "+e);break;case"array":(!this._props.count||this._props.value.length<this._props.count)&&this._props.value.push(e);break}a(this,G2,Wt).call(this,e)}setTooltip(t,e){const i=t.detail.target;i.tooltip=e}_init(t,e,i,o){this._initialize(t,e||_e,i||X0,o||G0)}_setEvent(){super._setEvent(),this._elements.box.addEventListener("mouseover",()=>{!this._props.disabled&&!this._props.readonly&&(r.addClass(this._elements.box,"focus"),r.addClass(this._elements.label,"focus"))}),this._elements.box.addEventListener("mouseout",()=>{!this._props.disabled&&!this._props.readonly&&(r.removeClass(this._elements.box,"focus"),r.removeClass(this._elements.label,"focus"))}),this._elements.box.addEventListener("click",()=>{!this._props.disabled&&!this._props.readonly&&(this.dispatchEvent(new MouseEvent("select")),(this._props.selConfig||this._props.selOptions)&&this.select())}),this._elements.inputValidity.addEventListener("invalid",t=>{a(this,Le,A8).call(this,t.target.validity)})}select(){const t=this._props.selOptions?a1("return "+this._props.selOptions):{};this._props.selConfig&&(t.config=this._props.selConfig),t.selected=this.value,t.count=this._props.count,r.getPositionParent(this),(this._props.selTitle,void 0).then(e=>{if(e.type==="ok"){const i=e.data.map(o=>o.dn||o.id||o);a(this,Et,D4).call(this,i),this.unInvalidStyle(),this._props.validityBlur&&this.checkValidity(),this.dispatchEvent(new MouseEvent("change"))}else this.dispatchEvent(new MouseEvent("cancel"))})}focus(){this._elements.input.focus()}blur(){this._elements.input.blur()}setCustomValidity(){return this._elements.inputValidity.setCustomValidity(...arguments)}checkValidity(){return this.dispatchEvent(new CustomEvent("validity")),this._elements.inputValidity.checkValidity(...arguments)}reportValidity(){return this.dispatchEvent(new CustomEvent("validity")),this._elements.inputValidity.reportValidity(...arguments)}unInvalidStyle(){this._elements.inputValidity.setCustomValidity(""),r.removeClass(this._elements.inputValidity,"invalid"),this._elements.invalidHint.textContent="",r.removeClass(this._elements.invalidHint,"show")}};Et=new WeakSet,D4=function(t){let e=t;e=Array.isArray(e)?e:[e],e=this._props.count?e.slice(0,this._props.count):e,this._props.value=e,e&&e.length?(r.addClass(this._elements.placeholder,"hide"),r.empty(this._elements.inputContent),a(this,G2,Wt).call(this,e)):(this._elements.inputContent.innerHTML='<div style="padding: 0.1em;">&nbsp</div>',!this._props.readmode&&!this._props.readonly&&!this._props.disabled&&r.removeClass(this._elements.placeholder,"hide"),this._props.placeholder?this._elements.placeholder.textContent=this._props.placeholder:this._elements.placeholder.innerHTML="&nbsp")},G2=new WeakSet,Wt=function(t){switch(v(t)){case"string":const e=t.split(/\s*,\s*/g);if(e.length===1){if(e[0]){const i=r("oo-org",{value:e[0],type:this._props.itemType});this._props.itemType==="text"&&i.setAttribute("type","text"),this._props.isTooltip===!1&&i.setAttribute("is-tooltip",!1),i.addEventListener("tooltip",o=>{this.dispatchEvent(new CustomEvent("tooltip",{detail:{target:o.target}}))}),this._elements.inputContent.append(i)}}else a(this,G2,Wt).call(this,e);break;case"array":t.forEach(i=>{a(this,G2,Wt).call(this,i)});break}},be=new WeakSet,S8=function(){return this._elements.inputValidity.validity.customError||this._props.validity&&this._elements.inputValidity.setCustomValidity(this._props.validity),this._elements.invalidHint&&(this._elements.invalidHint.textContent=this._elements.inputValidity.validationMessage),this._elements.inputValidity.validationMessage},Le=new WeakSet,A8=function(t){this.dispatchEvent(new CustomEvent("invalid",{detail:t})),r.addClass(this._elements.inputValidity,"invalid"),a(this,be,S8).call(this)&&r.addClass(this._elements.invalidHint,"show")},C(_e,"prop",{leftIcon:"",rightIcon:"create",itemType:"text",isTooltip:!0,label:"",inputStyle:"",labelStyle:"",labelAlign:"right",viewStyle:"",bgcolor:"#ffffff",skin:"",placeholder:"",disabled:!1,readonly:!1,readmode:!1,value:[],selConfig:"",selOptions:"",selTitle:"",count:0,validity:"",validityBlur:!1,required:!1});let B3=_e;class K0 extends B3{constructor(){super(),this._init("oo-selector")}get value(){const n=typeOf(this._props.value)==="string"&&this._props.value?this._props.value.split(/\s*,\s*/g):this._props.value;return this._props.count===1?n[0]:this._props.count?n.slice(0,this._props.count):n}set value(n){this.setAttribute("value",n)}}const J0=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    display: block;\r
}\r
.item{\r
    height: 1em;\r
    /*background-color: var(--oo-color-gray-f3);*/\r
    border-radius: 1em;\r
    margin-bottom: 1.2em;\r
    background: linear-gradient(90deg,#f2f2f2 25%,#e6e6e6 37%,#f2f2f2 63%);\r
    background-size: 400% 100%;\r
    animation: skeleton-loading 1.4s ease infinite;\r
}\r
.item:last-child{\r
    width: 60%;\r
}\r
.item:first-child{\r
    width: 40%;\r
}\r
\r
@keyframes skeleton-loading {\r
    0% {\r
        background-position: 100% 50%;\r
    }\r
    100% {\r
        background-position: 0 50%;\r
    }\r
}\r
`,Q0=`
<div class="content">
</div>
`,we=class we extends k{constructor(){super();c(this,Mt);C(this,"_setPropMap",{rows:()=>{a(this,Mt,F4).call(this)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)}});this._initialize("oo-skeleton",we,Q0,J0)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_render(){a(this,Mt,F4).call(this)}};Mt=new WeakSet,F4=function(){r.empty(this._content);try{const t=parseInt(this._props.rows);for(let e=0;e<t;e++)this._content.append(r("div.item"))}catch{}},C(we,"prop",{rows:"5",cssLink:"",skin:""});let V3=we;const t6=`<div class="content">\r
		<slot name="before-outer"></slot>\r
		<div class="label">\r
			<div class="labelText hide"></div>\r
			<slot name="label"></slot>\r
		</div>\r
		<div style="width: 100%">\r
			<div class="box">\r
				<slot name="before-inner-before"></slot>\r
				<div class="prefix hide"></div>\r
				<div class="prefixText hide"></div>\r
				<slot name="before-inner-after"></slot>\r
\r
				<div class="bar">\r
					<slot name="bar"></slot>\r
					<div class="barArea">\r
						<div class="barIconArea"></div>\r
						<div class="barTextArea"></div>\r
					</div>\r
					<div class="button">\r
						<slot name="button"></slot>\r
						<div class="buttonArea"></div>\r
					</div>\r
				</div>\r
\r
				<slot name="after-inner-before"></slot>\r
				<div class="suffixText hide"></div>\r
				<div class="suffix hide"></div>\r
				<slot name="after-inner-after"></slot>\r
			</div>\r
			<div class="invalidHint ooicon-error"></div>\r
		</div>\r
		<slot name="after-outer"></slot>\r
</div>\r
`,e6=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    --label: var(--oo-color-text2);\r
    --icon-left: var(--oo-color-text2);\r
    --icon-right: var(--oo-color-text2);\r
    --width: unset;\r
\r
\r
    --focus: var(--oo-color-main);\r
    --border: var(--oo-color-gray1);\r
    --radius: var(--oo-default-radius);\r
    --input: var(--oo-color-text);\r
    --placeholder: var(--oo-color-text4);\r
    --disabled: var(--oo-color-gray2);\r
}\r
.content {\r
    height: 100%;\r
    width: 100%;\r
    position: relative;\r
    display: flex;\r
    align-items: center;\r
}\r
.label {\r
    white-space: nowrap;\r
    transition: color 0.2s;\r
    display: inline-flex;\r
    align-items: center;\r
    color: var(--label);\r
}\r
.labelText{\r
    padding: 0 0.35em;\r
    margin-right: 0.5em;\r
}\r
.box {\r
    display: flex;\r
    gap: 0.3em;\r
    align-items: center;\r
    color: var(--oo-color-text3);\r
    transition: all 0.2s;\r
}\r
.box.false .bar{\r
    color: var(--oo-color-text3);\r
}\r
.bar{\r
    min-width: 3em;\r
    width: var(--width);\r
    font-size: 0.875em;\r
    height: 1.8em;\r
    padding: 0.2em;\r
    border-radius: 100em;\r
    box-shadow: 0 0 0 1px var(--border);\r
    background-color: var(--oo-color-main);\r
    position: relative;\r
    cursor: pointer;\r
    transition: all 0.2s;\r
    user-select: none;\r
}\r
.box.false .bar{\r
    background-color: var(--oo-color-gray-b);\r
}\r
.box .falseLabel{\r
    color: var(--oo-color-text3);\r
}\r
.box .trueLabel{\r
    color: var(--oo-color-main);\r
}\r
.box.false .falseLabel{\r
    color: var(--oo-color-main);\r
}\r
.box.false .trueLabel{\r
    color: var(--oo-color-text3);\r
}\r
\r
.barArea{\r
    color: #ffffff;\r
    padding: 0 0.5em;\r
    display: flex;\r
    align-items: center;\r
    gap: 0.1em;\r
    padding-right: 1.6em;\r
}\r
.barTextArea{\r
    overflow: hidden;\r
    text-overflow: ellipsis;\r
    white-space: nowrap;\r
}\r
.box.false .barArea{\r
     padding-left: 1.6em;\r
     padding-right: 0.5em;\r
}\r
.box.trueIsLeft .barArea{\r
    padding-left: 1.6em;\r
    padding-right: 0.5em;\r
}\r
.box.trueIsLeft.false .barArea{\r
    padding-left: 0.5em;\r
    padding-right: 1.6em;\r
}\r
\r
\r
.button{\r
    width: 1.4em;\r
    height: 1.4em;\r
    background-color: #ffffff;\r
    border-radius: 100em;\r
    transition: all 0.2s;\r
\r
    position: absolute;\r
    left: calc(100% - 1.4em - 0.2em);\r
    top: 0.2em;\r
}\r
.box.false .button{\r
    left: 0.2em;\r
    right: unset;\r
}\r
.box.trueIsLeft .button{\r
    left: 0.2em;\r
}\r
.box.trueIsLeft.false .button{\r
    left: calc(100% - 1.4em - 0.2em);\r
}\r
\r
.buttonArea{\r
    display: flex;\r
    justify-content: center;\r
    align-items: center;\r
    font-size: 0.875em;\r
    width: 100%;\r
    height: 100%;\r
}\r
\r
.suffixText, .prefixText{\r
    transition: color 0.2s;\r
}\r
\r
div.prefix{\r
    text-align: right;\r
    color: var(--icon-left);\r
}\r
div.suffix{\r
    text-align: left;\r
    color: var(--icon-right);\r
}\r
\r
.hide{\r
    display: none;\r
}\r
div.invalidHint{\r
    /*position: absolute;*/\r
    color: var(--oo-color-error);\r
    height: 0;\r
    overflow: hidden;\r
    word-break: break-all;\r
    transition: margin-top 0.2s, height 0.2s;\r
}\r
div.invalidHint.show{\r
    margin: 0.3em 0 0 0em;\r
    height: unset;\r
    line-height: 1.2em;\r
}\r
div.invalidHint.show::before{\r
    line-height: 1.4em;\r
    margin-right: 0.2em;\r
}\r
\r
.box.disabled {\r
    opacity: 0.5;\r
    outline: none;\r
    cursor: not-allowed;\r
}\r
\r
.box.disabled .bar {\r
    outline: none;\r
    cursor: not-allowed;\r
}\r
\r
.box.readonly .bar {\r
    cursor: default;\r
}\r
\r
\r
\r
\r
\r
\r
\r
\r
\r
.box:has(.input.invalid){\r
    background: var(--oo-color-error-bg);\r
    box-shadow: var(--oo-shadow-border-error);\r
}\r
\r
.input {\r
    border: 0;\r
    outline: none;\r
    color: var(--input);\r
    border-radius: var(--radius);\r
    padding: 0.3em 0.6em;\r
    box-shadow: none;\r
    height: 100%;\r
    width: inherit;\r
    font-size: 1em;\r
    background: transparent;\r
}\r
.input[type="file"]{\r
    position: absolute;\r
    opacity: 0;\r
}\r
.file{\r
    padding: 0.2em;\r
    display: grid;\r
    grid-template-columns: auto auto;\r
    gap: 2em;\r
    align-items: center;\r
}\r
textarea {\r
    border: 0;\r
    outline: none;\r
    color: var(--input);\r
    border-radius: var(--radius);\r
    padding: 0.3em 0.6em;\r
    box-shadow: none;\r
    height: 100%;\r
    width: inherit;\r
    font-size: 1em;\r
    background: transparent;\r
}\r
\r
input::placeholder, textarea::placeholder{\r
    color: var(--placeholder);\r
}\r
\r
\r
\r
\r
.box.focus {\r
    /*border-color: var(--focus);*/\r
    box-shadow: inset 0 0 0 1px var(--focus);\r
    outline: none;\r
    color: var(--focus);\r
}\r
.box.focus  div.left-icon{\r
    color: var(--focus);\r
}\r
.box.focus  div.right-icon{\r
    color: var(--focus);\r
}\r
\r
.label.focus {\r
    color: var(--focus);\r
    outline: none;\r
}\r
\r
\r
.box.disabled div.left-icon{\r
    /*color: var(--oo-color-text4);*/\r
    opacity: 0.4;\r
}\r
.box.disabled div.right-icon{\r
    /*color: var(--oo-color-text4);*/\r
    opacity: 0.4;\r
}\r
.label.disabled {\r
    /*color: var(--oo-color-text4);*/\r
    opacity: 0.3;\r
    outline: none;\r
    cursor: not-allowed;\r
}\r
input:disabled, textarea:disabled{\r
    opacity: 0.4;\r
    cursor: not-allowed;\r
}\r
\r
input:focus, textarea:focus {\r
    border: 0;\r
    outline: none;\r
    box-shadow: none;\r
}\r
.appearance::-webkit-inner-spin-button,\r
.appearance::-webkit-outer-spin-button {\r
    -webkit-appearance: none;\r
    appearance: none;\r
    margin: 0;\r
}\r
`,xe=class xe extends k{constructor(){super();c(this,ye);C(this,"_elements",{label:null,labelText:null,box:null,prefix:null,suffix:null,bar:null,barArea:null,button:null,buttonArea:null,invalidHint:null,prefixText:null,suffixText:null,barIconArea:null,barTextArea:null});C(this,"_setPropMap",{leftIcon:t=>{this._setIcon(this._elements.prefix,t,this._props.leftIcon)},rightIcon:t=>{this._setIcon(this._elements.suffix,t,this._props.rightIcon)},trueText:()=>{const t=this._getTrueLabelNode();t.textContent=this._props.trueText,r.checkClass(t,"hide",!this._props.trueText)},falseText:()=>{const t=this._getFalseLabelNode();t.textContent=this._props.falseText,r.checkClass(t,"hide",!this._props.trueText)},innerTrueText:()=>{this.isTrue&&(this._elements.barTextArea.textContent=this._props.innerTrueText)},innerFalseText:()=>{this.isTrue||(this._elements.barTextArea.textContent=this._props.innerFalseText)},buttonTrueText:()=>{this.isTrue&&!this._props.buttonTrueIcon&&(this._elements.buttonArea.textContent=this._props.buttonTrueText.substring(0,1))},buttonFalseText:()=>{!this.isTrue&&!this._props.buttonFalseIcon&&(this._elements.buttonArea.textContent=this._props.buttonFalseText.substring(0,1))},trueIcon:t=>{this._setIcon(this.this._getTrueLabelNode(),t,this._props.trueIcon)},falseIcon:t=>{this._setIcon(this._getFalseLabelNode(),t,this._props.falseIcon)},innerTrueIcon:t=>{this.isTrue&&this._setIcon(this._elements.barIconArea,t,this._props.innerTrueIcon)},innerFalseIcon:t=>{this.isTrue||this._setIcon(this._elements.barIconArea,t,this._props.innerFalseIcon)},buttonTrueIcon:t=>{this.isTrue&&this._setIcon(this._elements.buttonArea,t,this._props.buttonTrueIcon,!0)},buttonFalseIcon:t=>{this.isTrue||this._setIcon(this._elements.buttonArea,t,this._props.buttonFalseIcon,!0)},value:()=>{this.value=this._props.value},width:()=>{this._props.width?this._elements.bar.style.width=this._props.width:this._elements.bar.style.width="unset"},trueLocation:()=>{const t=this._getFalseLabelNode(),e=this._getTrueLabelNode();r.addClass(t,"falseLabel"),r.removeClass(t,"trueLabel"),r.addClass(e,"trueLabel"),r.removeClass(e,"falseLabel"),r.checkClass(this._elements.box,"trueIsLeft",this._props.trueLocation.toLowerCase()==="left")},disabled:()=>{r.checkClass(this._elements.box,"disabled",this._props.disabled),r.checkClass(this._elements.label,"disabled",this._props.disabled)},readonly:()=>{r.checkClass(this._elements.box,"readonly",this._props.readonly),r.checkClass(this._elements.label,"readonly",this._props.readonly)},label:()=>{this._props.label?(this._elements.labelText.textContent=this._props.label,r.removeClass(this._elements.labelText,"hide")):(this._elements.labelText.textContent="",r.addClass(this._elements.labelText,"hide"))},skin:()=>{this._useSkin(this._props.skin)},validity:()=>{},$default:t=>{}});this._init("oo-switch")}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_setIcon(t,e,i,o){r.removeClass(t,`ooicon-${e}`),i?(r.addClass(t,`ooicon-${i}`),r.removeClass(t,"hide")):o||r.addClass(t,"hide")}_getTrueLabelNode(){return this._props.trueLocation.toLowerCase()!=="left"?this._elements.suffixText:this._elements.prefixText}_getFalseLabelNode(){return this._props.trueLocation.toLowerCase()!=="left"?this._elements.prefixText:this._elements.suffixText}_init(t,e,i,o){this._initialize(t,e||xe,i||t6,o||e6)}_render(){super._render(),this._setPropMap.value(),this._props.label&&this._setPropMap.label(),this._setPropMap.trueLocation()}_setEvent(){super._setEvent(),this._elements.bar.addEventListener("click",t=>{!this._props.disabled&&!this._props.readonly&&(this.value=a(this,ye,T8).call(this),this.dispatchEvent(new Event("change",t)))})}set value(t){this._props.trueValue&&this._props.falseValue?this._props.booleanValue=t===this._props.trueValue:this._props.booleanValue=!(!t||t==="false"),r.checkClass(this._elements.box,"false",!this._props.booleanValue),["innerTrueText","innerFalseText","buttonTrueText","buttonFalseText","innerTrueIcon","innerFalseIcon","buttonTrueIcon","buttonFalseIcon"].forEach(e=>{var i,o;(o=(i=this._setPropMap)[e])==null||o.call(i)})}get value(){return this._props.trueValue&&this._props.falseValue?this._props.booleanValue?this._props.trueValue:this._props.falseValue:this._props.booleanValue}get isTrue(){return this._props.booleanValue}};ye=new WeakSet,T8=function(){return this._props.trueValue&&this._props.falseValue?this._props.booleanValue?this._props.falseValue:this._props.trueValue:!this._props.booleanValue},C(xe,"prop",{leftIcon:"",rightIcon:"",trueLocation:"right",trueText:"",falseText:"",innerTrueText:"",innerFalseText:"",buttonTrueText:"",buttonFalseText:"",trueIcon:"",falseIcon:"",innerTrueIcon:"",innerFalseIcon:"",buttonTrueIcon:"",buttonFalseIcon:"",trueValue:"",falseValue:"",disabled:!1,readonly:!1,width:"",label:"",validity:"",validityBlur:!1,value:"true",booleanValue:!0,skin:""});let W3=xe;const n6=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    display: block;\r
}\r
`,s6=`
<div class="content">
    <slot name="content"></slot>
</div>
`,Me=class Me extends k{constructor(){super();c(this,ke);c(this,Ee);C(this,"_setPropMap",{css:()=>{this.useCss(this._props.css)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},label:()=>{this.label&&(this.label.querySelector(".text").textContent=this._props.label,this.tabs.showSlider())},icon:t=>{if(this.label){const e=this.label.querySelector(".icon");this._props.icon?e?(r.removeClass(e,`ooicon-${t}`),r.addClass(e,`ooicon-${this._props.icon}`)):this.label.insertAdjacentElement("afterbegin",r(`div.icon.ooicon-${this._props.icon}`)):e&&e.remove(),this.tabs&&this.tabs.showSlider()}},rightIcon:t=>{if(this.label){const e=this.label.querySelector(".rightIcon");this._props.rightIcon?e?(r.removeClass(e,`ooicon-${t}`),r.addClass(e,`ooicon-${this._props.rightIcon}`)):this.label.insertAdjacentElement("beforeend",r(`div.rightIcon.ooicon-${this._props.rightIcon}`)):e&&e.remove(),this.tabs&&this.tabs.showSlider()}}});C(this,"label",null);C(this,"tabs",null);this._initialize("oo-tab",Me,s6,n6)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_render(){this._fillContent("content")}_connected(){this.tabs=r.getParent(this,"oo-tabs"),this.tabs&&a(this,ke,O8).call(this)}_disconnected(){this.label&&this.label.remove()}selected(){this.label&&r.addClass(this.label,"current"),r.removeClass(this,"hide"),this.dispatchEvent(new CustomEvent("current"))}unselected(){this.label&&r.removeClass(this.label,"current"),r.addClass(this,"hide"),this.dispatchEvent(new CustomEvent("uncurrent"))}};ke=new WeakSet,O8=function(){this.label||a(this,Ee,P8).call(this);const t=this.previousElementSibling;t&&t.label?t.label.insertAdjacentElement("afterend",this.label):this.tabs._elements.labels.insertAdjacentElement("afterbegin",this.label),this.setAttribute("slot","pane"),this!==this.tabs.currentTab&&r.addClass(this,"hide"),this.tabs._checkCurrentTab()},Ee=new WeakSet,P8=function(){this.label=r("div.label"),this._props.icon&&this.label.append(r(`div.icon.ooicon-${this._props.icon}`)),this.label.append(r("div.text",{text:this._props.label||"New Tab"})),this._props.rightIcon&&this.label.append(r(`div.rightIcon.ooicon-${this._props.rightIcon}`)),this.label.addEventListener("click",()=>{this.tabs.setCurrent(this)})},C(Me,"prop",{css:"",cssLink:"",skin:"",label:"",icon:"",rightIcon:""});let H3=Me;const i6=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    display: block;\r
\r
    --padding: 0.8em;\r
    --border: var(--oo-color-gray1);\r
    --color: var(--oo-color-text2);\r
\r
    --hover: var(--oo-color-main-light);\r
    --current: var(--oo-color-main);\r
\r
    --label-padding: 0.4em;\r
    --label-margin: 0em;\r
\r
    --justify: flex-start;\r
\r
    --pane-height: unset;\r
    --pane-padding: 0.8em;\r
}\r
.tabs{\r
    padding: var(--padding);\r
}\r
.tabs.left{\r
    padding: var(--padding);\r
    display: flex;\r
    justify-content: flex-start;\r
}\r
.tabs.right{\r
    padding: var(--padding);\r
    display: flex;\r
    justify-content: flex-start;\r
    flex-direction: row-reverse;\r
}\r
.tabs.bottom{\r
    padding: var(--padding);\r
    display: flex;\r
    flex-direction: column-reverse;\r
}\r
\r
.labels{\r
    display: flex;\r
    align-items: center;\r
    justify-content: var(--justify);\r
    border-bottom: 1px solid var(--border);\r
    position: relative;\r
    overflow: auto;\r
}\r
.labels.left{\r
    display: flex;\r
    justify-content: var(--justify);\r
    border-right: 1px solid var(--border);\r
    position: relative;\r
    overflow: auto;\r
    align-items: flex-end;\r
    border-bottom: 0;\r
    flex-direction: column;\r
}\r
.labels.right{\r
    display: flex;\r
    justify-content: var(--justify);\r
    border-left: 1px solid var(--border);\r
    position: relative;\r
    overflow: auto;\r
    align-items: flex-start;;\r
    border-bottom: 0;\r
    flex-direction: column;\r
}\r
.labels.bottom{\r
    display: flex;\r
    align-items: center;\r
    justify-content: var(--justify);\r
    border-top: 1px solid var(--border);\r
    border-bottom: 0;\r
    position: relative;\r
    overflow: auto;\r
}\r
.slider {\r
    height: 2px;\r
    width: 0;\r
    background-color: var(--current);\r
    position: absolute;\r
    bottom: 0;\r
    left: 0;\r
    right: unset;\r
    top: unset;\r
    transition: left 0.2s, width 0.2s, top 0.2s, height 0.2s;\r
}\r
.slider.left {\r
    width: 2px;\r
    height: 0;\r
    background-color: var(--current);\r
    position: absolute;\r
    right: 0;\r
    top: 0;\r
    left: unset;\r
    bottom: unset;\r
    transition: left 0.2s, width 0.2s, top 0.2s, height 0.2s;\r
}\r
.slider.right {\r
    width: 2px;\r
    height: 0;\r
    background-color: var(--current);\r
    position: absolute;\r
    right: unset;\r
    top: 0;\r
    left: 0;\r
    bottom: unset;\r
    transition: left 0.2s, width 0.2s, top 0.2s, height 0.2s;\r
}\r
.slider.bottom {\r
    height: 2px;\r
    width: 0;\r
    background-color: var(--current);\r
    position: absolute;\r
    bottom: unset;\r
    left: 0;\r
    right: unset;\r
    top: 0;\r
    transition: left 0.2s, width 0.2s, top 0.2s, height 0.2s;\r
}\r
.panes{\r
    padding: var(--pane-padding);\r
    height: var(--pane-height);\r
    overflow: auto;\r
}\r
.label{\r
    padding: var(--label-padding);\r
    margin: var(--label-margin);\r
    cursor: pointer;\r
    color: var(--color);\r
    transition: color 0.2s;\r
    white-space: nowrap;\r
    display: flex;\r
    align-items: center;\r
}\r
.label>div{\r
    padding: 0.2em;\r
}\r
.label.current{\r
    color: var(--current);\r
}\r
\r
.labels::-webkit-scrollbar {\r
    width: 5px;\r
    height: 5px;\r
    border-radius: 5px;\r
    background-color: var(--oo-color-gray-d);\r
}\r
.labels::-webkit-scrollbar-thumb:hover {\r
     width: 5px;\r
     border-radius: 5px;\r
     background-color: var(--oo-color-text2);\r
     cursor: pointer;\r
 }\r
.labels*::-webkit-scrollbar-thumb {\r
    width: 5px;\r
    border-radius: 5px;\r
    background-color: var(--oo-color-gray-b);\r
    cursor: pointer;\r
}\r
`,r6=`
<div class="content tabs">
    <div class="labels">
        <div class="slider"></div>
        <slot name="label"></slot>
    </div>
    <div class="panes">
        <slot name="pane"></slot>
    </div>
</div>
`,Ae=class Ae extends k{constructor(){super();c(this,Se);C(this,"_elements",{labels:null,panes:null,slider:null});C(this,"_setPropMap",{css:()=>{this.useCss(this._props.css)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},position:t=>{r.removeClass(this._content,t),r.addClass(this._content,this._props.position),r.removeClass(this._elements.labels,t),r.addClass(this._elements.labels,this._props.position),r.removeClass(this._elements.slider,t),r.addClass(this._elements.slider,this._props.position)},current:()=>{this._checkCurrentTab()}});C(this,"currentTab",null);this._initialize("oo-tabs",Ae,r6,i6)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_checkCurrentTab(){const e=this.querySelectorAll("oo-tab").item(this._props.current);e&&e!==this.currentTab&&this.setCurrent(e)}setCurrent(t){if(this.currentTab&&(this.currentTab.unselected(),this.currentTab=null),t){const e=this.querySelectorAll("oo-tab");let i=0;for(const o of e){if(o===t)break;i++}this._props.current=i,this.currentTab=t,t.selected(),this.showSlider()}else a(this,Se,Z8).call(this)}showSlider(){const t=r.getPosition(this.currentTab.label,this._elements.labels),e=r.getSize(this.currentTab.label);if(this._props.position==="top"||this._props.position==="bottom"){const i=this._elements.labels.scrollLeft,o=t.x+i;r.setStyles(this._elements.slider,{left:o+"px",width:e.x+"px"})}else{const i=this._elements.labels.scrollTop,o=t.y+i;r.setStyles(this._elements.slider,{top:o+"px",height:e.y+"px"})}}};Se=new WeakSet,Z8=function(){r.setStyles(this._elements.slider,{left:0,width:0})},C(Ae,"prop",{css:"",cssLink:"",skin:"",position:"top",current:0});let Y3=Ae;const o6=`<div class="button content">\r
	<div class="box">\r
		<div class="menu ooicon-menu hide"></div>\r
		<div class="prefix"></div>\r
		<div class="text"></div>\r
		<div class="suffix"></div>\r
		<div class="close ooicon-close"></div>\r
	</div>\r
</div>\r
`,l6=`body{\r
    display: inline-block;\r
}\r
* {\r
    box-sizing: border-box;\r
    user-select: none;\r
}\r
.content{\r
    --color: var(--oo-color-text3);\r
    --bg: var(--oo-color-gray2);\r
    --radius: var(--oo-default-radius);\r
    --current-color: var(--oo-color-text-white);\r
    --current-bg: var(--oo-color-main);\r
}\r
\r
.button {\r
    border-radius: var(--radius);\r
    border: 0;\r
    padding: 0.5em 0.5em;\r
    cursor: pointer;\r
    color: var(--color);\r
    background-color: var(--bg);\r
    transition: background 0.2s, color 0.2s;\r
    text-align: center;\r
    line-height: 1em;\r
    height: 100%;\r
    width: 100%;\r
    display: flex;\r
    align-items: center;\r
}\r
.button.current{\r
    background-color: var(--current-bg)!important;\r
    color: var(--current-color)!important;\r
}\r
.box>div{\r
    /*margin: 0 0.2em;*/\r
}\r
div.prefix{\r
    display: none;\r
    font-size: 0.9em;\r
}\r
div.suffix{\r
    display: none;\r
    font-size: 0.9em;\r
}\r
div.text{\r
    height: 100%;\r
    display: inline-flex;\r
    align-items: center;\r
    justify-content: baseline;\r
}\r
.box{\r
    display: grid;\r
    gap: 0.2em;\r
    grid-auto-flow: column;\r
    align-items: baseline;\r
}\r
.box>.icon{\r
    display: inline-block;\r
}\r
\r
\r
.box>.close{\r
    display: inline-block;\r
    border-radius: var(--oo-default-radius);\r
    font-size: 0.8em;\r
    padding: 0.1em;\r
    margin: 0;\r
}\r
.box>.menu{\r
    display: none;\r
    border-radius: var(--oo-default-radius);\r
    font-size: 0.8em;\r
    padding: 0.1em;\r
    margin: 0;\r
}\r
.button .close:hover{\r
    background-color: var(--color);\r
    color: var(--bg);\r
}\r
.button.current .close:hover, .menu:hover{\r
    background-color: var(--current-color);\r
    color: var(--current-bg);\r
}\r
\r
.button.current .menu{\r
    display: inline-block;\r
}\r
\r
.hide{\r
    display: none!important;\r
}\r
`,St=class St extends Kt{constructor(){super();c(this,Te);C(this,"_elements",{text:null,prefix:null,suffix:null,button:null,close:null,menu:null});a(this,Te,j8).call(this),this._init("oo-tag",St,o6,l6)}_setEvent(){this._elements.close&&this._elements.close.addEventListener("click",t=>{this.shadowRoot.dispatchEvent(new MouseEvent("close",t)),t.stopPropagation()}),this._elements.menu&&this._elements.menu.addEventListener("click",t=>{this.shadowRoot.dispatchEvent(new MouseEvent("menu",t)),t.stopPropagation()})}};Te=new WeakSet,j8=function(){this.setPropMap=Object.assign(this._setPropMap,{close:()=>{this._props.close==="on"?r.removeClass(this._elements.close,"hide"):r.addClass(this._elements.close,"hide")},menu:()=>{this._props.menu==="on"?r.removeClass(this._elements.menu,"hide"):r.addClass(this._elements.menu,"hide")},type:t=>{this._props.type||(this._props.type="default"),t&&r.removeClass(this._elements.button,t),r.addClass(this._elements.button,this._props.type)}})},C(St,"prop",{leftIcon:"",rightIcon:"",style:"",disabled:!1,text:"",type:"default",close:"on",menu:"off",skin:""}),C(St,"events",{close:new Event("close",{composed:!0}),menu:new Event("menu",{composed:!0})});let q3=St;const a6=`<div class="content">\r
	<label>\r
		<slot name="before-outer"></slot>\r
		<div class="label">\r
			<div class="labelText hide"></div>\r
			<slot name="label"></slot>\r
		</div>\r
		<div style="width: 100%">\r
			<div style="display: flex; align-items: center;">\r
				<div class="box">\r
					<slot name="before-inner-before"></slot>\r
					<div class="prefix"></div>\r
					<slot name="before-inner-after"></slot>\r
					<div class="view">\r
						<textarea class="input"></textarea>\r
						<div class="viewText"></div>\r
					</div>\r
					<slot name="after-inner-before"></slot>\r
					<div class="suffix"></div>\r
					<slot name="after-inner-after"></slot>\r
				</div>\r
				<div class="requiredFlag hide">&#10039;</div>\r
			</div>\r
			<div class="invalidHint ooicon-error"></div>\r
			<slot name="after-outer"></slot>\r
		</div>\r
	</label>\r
</div>\r
`,d6=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    --label: var(--oo-color-text2);\r
    --icon-left: var(--oo-color-text2);\r
    --icon-right: var(--oo-color-text2);\r
    --focus: var(--oo-color-main);\r
    --border: var(--oo-color-gray1);\r
    --radius: var(--oo-area-radius);\r
    --input: var(--oo-color-text);\r
    --placeholder: var(--oo-color-text4);\r
    --disabled: var(--oo-color-gray2);\r
}\r
textarea{\r
    resize: none;\r
    font-size: 1em;\r
    height: 5em;\r
    min-height: 2em;\r
}\r
textarea.resize{\r
    resize: both;\r
}\r
textarea.autosize{\r
    resize: none;\r
    overflow: hidden;\r
}\r
textarea::-webkit-scrollbar {\r
    width: 8px;\r
    height: 8px;\r
    border-radius: 8px;\r
    background-color: var(--oo-color-gray-d);\r
}\r
\r
textarea::-webkit-scrollbar-thumb {\r
    width: 8px;\r
    border-radius: 8px;\r
    background-color: var(--oo-color-gray-b);\r
    cursor: pointer;\r
}\r
\r
textarea::-webkit-scrollbar-thumb:hover {\r
    width: 8px;\r
    border-radius: 8px;\r
    background-color: var(--oo-color-text2);\r
    cursor: pointer;\r
}\r
label {\r
    display: flex;\r
    position: relative;\r
    height: 100%;\r
    align-items: center;\r
}\r
.requiredFlag{\r
    color: var(--oo-color-highlight);\r
    padding-left: 0.3em;\r
    font-size: 0.75em;\r
}\r
`,l4=class l4 extends P2{constructor(){super();c(this,Oe);a(this,Oe,D8).call(this),this._init("oo-textarea"),this._useCss(d6)}_init(t){this._initialize(t,l4,a6,p9)}_setEvent(){super._setEvent(),this._elements.input.addEventListener("input",()=>{this.setHeight()}),r.toggleClass(this._elements.input,"autosize",this._props.autoSize),this.setHeight()}setHeight(){if(this._props.autoSize){this._elements.input.style.height="auto";const t=this._elements.input.scrollHeight;this._elements.input.style.height=t+"px"}}};Oe=new WeakSet,D8=function(){this._setPropMap=Object.assign(this._setPropMap,{autoSize:()=>{r.toggleClass(this._elements.input,"autosize",this._props.autoSize),this.setHeight()},value:()=>{try{this._elements.input.value=this._props.value}catch{}this._props.value?this._elements.viewText.textContent=this._props.value:this._elements.viewText.innerHTML="&nbsp;",window.setTimeout(()=>{this.setHeight()},10)}})};let U3=l4;const c6=`.content{\r
    --item-color: var(--oo-color-text2);\r
    --item-bg: var(--oo-color-text-white);\r
    --item-over: var(--oo-color-text2);\r
    --item-over-bg: var(--oo-color-hover);\r
    --item-select: var(--oo-color-main);\r
    --item-select-bg: var(--oo-color-hover);\r
    --item-shadow-color: var(--oo-color-gray-d);\r
    --item-shadow:  0 0 10px 1px var(--item-shadow-color);\r
    --item-border: var(--oo-color-gray-d);\r
    --item-selected: var(--oo-color-main);\r
    --item-selected-bg: var(--oo-color-light);\r
    --item-group-bg: var(--oo-color-text-white);\r
    --item-group-color: var(--oo-color-gray-9);\r
    --item-count-color: var(--oo-color-gray-9);\r
    display: flex;\r
    flex-direction: column;\r
}\r
.search{\r
}\r
.search.hide{\r
    display: none;\r
}\r
.searchInput{\r
    width: calc( 100% - 2em );\r
    padding:0 1em;\r
}\r
.clearSearch{\r
    padding-right: 0.625rem;\r
    cursor: pointer\r
}\r
.navContent{\r
    display: flex;\r
    flex-direction: column;\r
    padding: 0.5em 0;\r
    border-radius: var(--oo-area-radius);\r
    overflow: auto;\r
}\r
.navContent::-webkit-scrollbar {\r
    width:8px;\r
    height: 8px;\r
    border-radius: 8px;\r
    background-color: #dddddd;\r
}\r
.navContent::-webkit-scrollbar-thumb{\r
    width: 8px;\r
    border-radius: 8px;\r
    background-color: #bbbbbb;\r
    cursor: pointer;\r
}\r
.navContent::-webkit-scrollbar-thumb:hover{\r
    width: 8px;\r
    border-radius: 8px;\r
    background-color: #666666;\r
    cursor: pointer;\r
}\r
\r
.items .content{\r
    display: contents;\r
}\r
.item{\r
    min-height: 2.5em;\r
    color: var(--item-color);\r
    background-color: var(--item-bg);\r
    line-height: 1.5em;\r
    overflow: hidden;\r
    transition: background-color 0.2s, color 0.2s;\r
    cursor: pointer;\r
    display: flex;\r
    align-items: center;\r
    white-space: nowrap;\r
    font-size: 1em;\r
    padding-right: 1em;\r
}\r
.item.group{\r
    cursor: default;\r
}\r
.item.level1{\r
    font-size: 1.0714rem;\r
}\r
.item:hover{\r
    background-color: var(--item-over-bg);\r
    color: var(--item-over);\r
}\r
.item.selected{\r
    color: var(--item-selected);\r
    background-color: var(--item-selected-bg);\r
}\r
.item.disabled{\r
    opacity: 0.4;\r
    cursor: not-allowed;\r
}\r
.item.disabled:hover{\r
    background-color: var(--item-bg);\r
}\r
.item.group:hover{\r
    background-color: transparent;\r
}\r
.item .icon{\r
    font-size: 1em;\r
    width: 1.3em;\r
}\r
.item .count{\r
    padding-right: 0.5em;\r
    font-size: 0.825em;\r
    color: var(--item-count-color);\r
}\r
.children{\r
    position: absolute;\r
    overflow: hidden;\r
    display: none;\r
}\r
.children.show{\r
    display: block;\r
    visibility: visible;\r
    position: static;\r
    transition: height 0.2s;\r
}\r
.group.children{\r
    padding-bottom: 0.5em;\r
}\r
\r
.text{\r
    padding:0.5em 0;\r
    line-height: 1.5em;\r
    overflow: hidden;\r
    word-break: keep-all;\r
    text-overflow: ellipsis;\r
    flex: 1;\r
}\r
.group > .text, .group > .icon, .group > .count{\r
    color: var(--item-group-color);\r
    padding: 1em 0 0.5em 0;\r
}\r
\r
.arrowArea{\r
    padding: 0.5em 0 0.5em 0.5em;\r
}\r
.arrow{\r
    font-size: 1em;\r
    text-align: left;\r
    transition: color 0.2s;\r
    color: var(--label);\r
    cursor: pointer;\r
    transform-origin: center;\r
    transform: rotate(270deg);\r
}\r
.arrow.down {\r
    transform: rotate(0deg);\r
}\r
\r
.hide{\r
    display: none;\r
}\r
\r
.input{\r
    cursor: pointer;\r
}\r
`,h6=`
<nav class="content">
    <slot name="before-outer"></slot>
    <div class="box">
        <slot name="search"></slot>
        <div class="search">
            <oo-input class="searchInput" right-icon="search" placeholder="输入关键字搜索">
                    <div slot="after-inner-before" class="ooicon-close clearSearch hide"></div>
            </oo-input>
        </div>
        <slot name="before-inner-before"></slot>
        <div class="navContent">
            <slot name="before-inner-after"></slot>
            <div class="searchItems hide"></div>
            <div class="items"></div>
            <slot name="after-inner-before"></slot>
        </div>
        <slot name="after-inner-after"></slot>
    </div>
    <slot name="after-outer"></slot>
</nav>`,je=class je extends k{constructor(){super();c(this,Pe);c(this,At);c(this,Ze);c(this,Tt);c(this,P1,[]);c(this,K2,[]);c(this,A2,new Map);c(this,J2,new WeakMap);c(this,C2,new WeakMap);c(this,U,null);c(this,m2,null);C(this,"_setPropMap",{$default:()=>{},skin:()=>{this._useSkin(this._props.skin)},searchable:()=>{r.checkClass(this._elements.search,"hide",!this._props.searchable)},menu:()=>{this.loaded&&a(this,Pe,F8).call(this),this.data=a(this,Tt,I4).call(this,{children:this._props.menu});const t=new G3(this,null,this._elements.items,this.data,0).append();l(this,P1).push(t),this.loaded=!0}});C(this,"_elements",{box:null,navContent:null,items:null,search:null,searchInput:null,clearSearch:null,searchItems:null});this._initialize("oo-nav",je,h6,c6)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_render(){this._setEvent()}_connected(){}_disconnected(){}_setEvent(){super._setEvent();const{searchInput:t,clearSearch:e}=this._elements;let i=!1;t.addEventListener("compositionstart",()=>{i=!0}),t.addEventListener("compositionend",o=>{i=!1,this.search(o.currentTarget.value)}),t.addEventListener("input",o=>{!i&&this.search(o.currentTarget.value)}),e.addEventListener("click",o=>{t.setAttribute("value",""),this.search("")})}setMenu(t){this._props.menu=t,this._setPropMap.menu()}active(t){var e;(e=this.getItem(t))==null||e.active()}select(t){var e;(e=this.getItem(t))==null||e.select()}collapse(t){var e;(e=this.getItem(t))==null||e.collapse()}expand(t){var e;(e=this.getItem(t))==null||e.expand()}getItem(t){return l(this,A2).get(t)}getSelectedItem(){return l(this,U)}_checkItemUnique(t){const e=t[this._props.key];return!(e&&l(this,A2).get(e))}_checkChildrenMap(t){const e=t.data.___;e.children&&!l(this,C2).has(e.children)&&l(this,C2).set(e.children,t)}_addItem(t){const e=t.data.___;l(this,J2).set(e,t),e.children&&l(this,C2).set(e.children,t);const i=e[this._props.key];i&&l(this,A2).set(i,t)}_removeItem(t){const e=t.data.___;l(this,U)===t&&p(this,U,null),l(this,J2).delete(e),e.children&&l(this,C2).delete(e.children,t);const i=e[this._props.key];i&&l(this,A2).delete(i)}_selectItem(t){l(this,U)!==t&&(l(this,U)&&(l(this,U).data.selected=!1),p(this,U,t))}_selectSearchItem(t){var e;l(this,m2)!==t&&((e=l(this,m2))==null||e.unselect(),p(this,m2,t))}_fireEvent(t,e,i){this.dispatchEvent(new CustomEvent(t,{detail:{data:e,manual:!!i}}))}search(t){var e;r.checkClass(this._elements.clearSearch,"hide",!t),r.checkClass(this._elements.items,"hide",!!t),r.checkClass(this._elements.searchItems,"hide",!t),a(this,Ze,$8).call(this),t?a(this,At,$4).call(this,l(this,P1),t):((e=l(this,U))==null||e.checkExpand(),p(this,m2,null)),this.dispatchEvent(new CustomEvent("search",{detail:{value:t}}))}};P1=new WeakMap,K2=new WeakMap,A2=new WeakMap,J2=new WeakMap,C2=new WeakMap,U=new WeakMap,m2=new WeakMap,Pe=new WeakSet,F8=function(){p(this,P1,[]),p(this,K2,[]),p(this,A2,new Map),p(this,J2,new WeakMap),p(this,C2,new WeakMap),p(this,U,null),p(this,m2,null),r.empty(this._elements.items)},At=new WeakSet,$4=function(t,e){t.forEach(i=>{if(i._searchable()&&i.data.text.includes(e)){const o=new p6(this,i,i.data.___);l(this,K2).push(o)}a(this,At,$4).call(this,i.items,e)})},Ze=new WeakSet,$8=function(){for(;l(this,K2).length;)l(this,K2).pop().remove();p(this,m2,null)},Tt=new WeakSet,I4=function(t){return new Proxy(t,{get:(e,i)=>{if(i==="___")return e;{const o=e[i];return(typeof o=="object"||Array.isArray(o))&&o!==null?a(this,Tt,I4).call(this,o):o}},set:(e,i,o)=>{let d,h;if(Array.isArray(e))o.___&&(o=o.___),d=Reflect.set(e,i,o),h=l(this,C2).get(e),h==null||h._updateChildren(i,o);else{const u=e[i];d=Reflect.set(e,i,o),h=l(this,J2).get(e),h==null||h._updateProperty(i,o,u)}return d}})},C(je,"prop",{skin:"",key:"name",searchable:!0,allExpanded:!1,indent:1.3,iconPrefix:"ooicon-",menu:[]});let X3=je;const a4=class a4{constructor(n,t,e,i,o){c(this,De);c(this,Fe);c(this,$e);c(this,Ie);c(this,ze);C(this,"items",[]);C(this,"childrenMap",new WeakMap);this.nav=n,this.data=i,this.parentItem=t,this.parentEl=e,this.level=o,this._render()}_render(){this.nodeEl=r("div.content"),this.level>0?(a(this,De,I8).call(this),a(this,Fe,z8).call(this),a(this,$e,N8).call(this)):(this.childrenEl=r("div.children.show"),this.nodeEl.append(this.childrenEl)),this._createItems()}append(n="beforeend",t=this.parentEl){return t.insertAdjacentElement(n,this.nodeEl),this}_updateChildren(){for(let e=0;e<this.items.length;e++){const i=this.items[e];this.data.___.children.includes(i.data.___)||(i.remove(),e--)}const n=[];let t;this.data.children.forEach((e,i)=>{let o=this.childrenMap.get(e.___);o?this.items.indexOf(o)!==i&&(this.items.splice(this.items.indexOf(o),1),this.items.splice(i,0,o),t?o.append("afterend",t.nodeEl):o.append("afterbegin",this.childrenEl)):t?o=this._createItem(e,"afterend",t.nodeEl):o=this._createItem(e,"afterbegin",this.childrenEl),o?t=o:n.push(e)}),n.forEach(e=>{I1(this.data.___.children,e.___)}),r.checkClass(this.arrowEl,"hide",!this._expandable()),r.checkClass(this.expandEl,"hide",!this._expandable()),n.length>0&&this.throwNotUniqueError(n)}_createItems(){var t;const n=[];(t=this.data.children)==null||t.forEach(e=>{!this._createItem(e)&&n.push(e)}),n.forEach(e=>{I1(this.data.___.children,e.___)}),n.length>0&&this.throwNotUniqueError(n)}throwNotUniqueError(n){const t=n.map(e=>e.___[this.nav._props.key]);throw new Error(`Item key ${this.nav._props.key}:${t.join(", ")} is not unique!`)}_createItem(n,t,e){if(this.nav._checkItemUnique(n.___)){const i=new a4(this.nav,this,this.childrenEl,n,this.level+1).append(t,e);return this.items.push(i),this.childrenMap.set(n.___,i),this.nav._addItem(i),i}else return null}_removeItem(n){this.childrenEl.removeChild(n.nodeEl),I1(this.items,n),this.childrenMap.delete(n.data.___),this.nav._removeItem(n)}remove(){var n;this.emptyItems(),(n=this.parentItem)==null||n._removeItem(this)}emptyItems(){for(;this.items.length>0;)this.items.pop().remove()}_updateProperty(n,t,e){if(t===e)return;const i=this.data.___;switch(n){case"icon":e&&r.removeClass(this.iconEl,`${this.nav._props.iconPrefix}${e}`),t&&r.addClass(this.iconEl,`${this.nav._props.iconPrefix}${t}`),r.checkClass(this.iconEl,"hide",!t);break;case"text":this.textEl.textContent=t||"";break;case"title":this.itemEl.setAttribute("title",t||"");break;case"count":this.countEl.textContent=i.count||"",r.checkClass(this.countEl,"hide",typeof i.count>"u");break;case"expanded":t?this.expand():this.collapse();break;case"selected":t?this.select():this.unselect();break;case"expandable":r.checkClass(this.arrowEl,"hide",!this._expandable()),r.checkClass(this.expandEl,"hide",!this._expandable());break;case"disabled":r.checkClass(this.itemEl,"disabled",!!t);break;case"group":r.checkClass(this.arrowEl,"hide",!this._expandable()),r.checkClass(this.expandEl,"hide",!this._expandable()),r.checkClass(this.itemEl,"group",!!t),r.checkClass(this.childrenEl,"group",!!t),t&&!i.expanded&&this.expand(!1);break;case this.nav._props.key:this.nav._removeItem(this),this.nav._addItem(this);break;case"children":this.nav._checkChildrenMap(this),this._updateChildren(n,t);break}}_selectable(n=!0){var e;const t=this.data.___;switch(t.selectable){case"no":return!1;case"yes":return n?!t.disabled&&!t.group:!t.group;default:const i=!t.group&&!((e=t.children)!=null&&e.length);return n?!t.disabled&&i:i}}_searchable(){var t;const n=this.data.___;return!n.disabled&&!n.group&&(!((t=n.children)!=null&&t.length)||n.selectable==="yes")}_expandable(n=!1){var i;const t=this.data.___,e=!t.group&&!!((i=t.children)!=null&&i.length)||!!t.expandable;return n?!t.disabled&&e:e}select(n=!0,t=!0){var e;this._selectable(n)&&(r.addClass(this.itemEl,"selected"),this.nav._selectItem(this),t&&((e=this.parentItem)==null||e.checkExpand(n)),this.nav._fireEvent("select",this.data,n))}active(n=!0){!this.data.___.disabled&&this.nav._fireEvent("active",this.data,n)}unselect(){r.removeClass(this.itemEl,"selected")}checkExpand(n=!0,t=!1){var e;(!this.data.___.expanded||t)&&this._expandable(n)&&this.expand(n),(e=this.parentItem)==null||e.checkExpand(n,t)}expand(n=!0){this._expandable(n)&&(this.arrowEl&&r.addClass(this.arrowEl,"down"),this.data.___.expanded=!0,n?a(this,Ie,R8).call(this):(r.addClass(this.childrenEl,"show"),this.nav._fireEvent("expand",this.data,!1)))}collapse(n=!0){this._expandable(n)&&(this.arrowEl&&r.removeClass(this.arrowEl,"down"),this.data.___.expanded=!1,n?a(this,ze,B8).call(this):(r.removeClass(this.childrenEl,"show"),this.nav._fireEvent("collapse",this.data,!1)))}};De=new WeakSet,I8=function(){const n=this.data.___;this.itemEl=r("div.item"),this.iconEl=r(`div.icon ${this.nav._props.iconPrefix}${n.icon||""}`),this.itemEl.append(this.iconEl),this.textEl=r("div.text",{text:n.text||""}),this.itemEl.append(this.textEl),n.title&&this.itemEl.setAttribute("title",n.title),this.countEl=r("div.count"),typeof n.count<"u"&&(this.countEl.textContent=n.count),this.itemEl.append(this.countEl),this.expandEl=r("div.arrowArea"),this.arrowEl=r("div.arrow ooicon-drop_down"),this.expandEl.append(this.arrowEl),this.itemEl.append(this.expandEl),this.nodeEl.append(this.itemEl),this.childrenEl=r("div.children"),this.nodeEl.append(this.childrenEl)},Fe=new WeakSet,z8=function(){const{itemEl:n,expandEl:t,data:e}=this;n.addEventListener("click",i=>{if(this._selectable())this.select(),e.___.selected=!0,this.active();else if(this._expandable()){//!data.expanded ? this.expand() : this.collapse();
e.expanded=!e.___.expanded,this.active()}else this.active()}),t.addEventListener("click",i=>{if(this._expandable()&&this._selectable()){//!data.expanded ? this.expand() : this.collapse();
e.expanded=!e.___.expanded,this.active(),i.stopPropagation()}})},$e=new WeakSet,N8=function(){const{itemEl:n,iconEl:t,childrenEl:e,arrowEl:i,expandEl:o,countEl:d,nav:h,level:u}=this,m=this.data.___;r.checkClass(t,"hide",!m.icon),r.checkClass(d,"hide",typeof m.count>"u"),r.checkClass(n,"disabled",!!m.disabled),u===1&&r.addClass(n,"level1"),r.setStyle(n,"padding-left",h._props.indent*u+"em"),r.checkClass(i,"hide",!this._expandable()),r.checkClass(o,"hide",!this._expandable()),r.checkClass(e,"show",!!m.group),i&&r.checkClass(i,"down",!!m.expanded),h._props.allExpanded&&!m.expanded&&!m.group&&this.expand(!1),m.group&&r.addClass(n,"group"),m.group&&r.addClass(e,"group"),m.selected&&this.select(!1),m.expanded&&this.checkExpand(!1,!0)},Ie=new WeakSet,R8=function(){const n=this.childrenEl;r.addClass(n,"show");const t=r.getSize(n).y;r.setStyle(n,"height","0"),setTimeout(()=>{r.setStyle(n,"height",t+"px"),setTimeout(()=>{r.setStyle(n,"height","unset"),this.nav._fireEvent("expand",this.data,!0)},200)},1)},ze=new WeakSet,B8=function(){const n=this.childrenEl,t=r.getSize(n).y;r.setStyle(n,"height",t+"px"),setTimeout(()=>{r.setStyle(n,"height","0"),setTimeout(()=>{r.setStyle(n,"height","unset"),r.removeClass(n,"show"),this.nav._fireEvent("collapse",this.data,!0)},200)},1)};let G3=a4;class p6{constructor(n,t,e){c(this,Ne);c(this,Re);c(this,Be);this.nav=n,this.item=t,this.data=e,this.parentEl=n._elements.searchItems,this._render()}_render(){a(this,Ne,V8).call(this),a(this,Re,W8).call(this),a(this,Be,H8).call(this),this.parentEl.append(this.nodeEl)}select(n=!0,t=!0){this.item._selectable()?(r.addClass(this.itemEl,"selected"),this.nav._selectSearchItem(this),t&&(this.item.data.___.selected=!0,this.item.select(!0,!1),this.item.active(n))):this.item.active(n)}unselect(){r.removeClass(this.itemEl,"selected")}remove(){this.parentEl.removeChild(this.nodeEl)}}Ne=new WeakSet,V8=function(){const n=this.data;this.nodeEl=r("div.content"),this.itemEl=r("div.item level1"),n.icon&&this.itemEl.append(r(`div.icon ${n.icon}`)),this.itemEl.append(r("div.text",{text:n.text||""})),n.title&&this.itemEl.setAttribute("title",n.title),this.nodeEl.append(this.itemEl)},Re=new WeakSet,W8=function(){this.itemEl.addEventListener("click",n=>{this.select()})},Be=new WeakSet,H8=function(){const{itemEl:n,data:t}=this;r.checkClass(n,"disabled",!!t.disabled),r.setStyle(n,"padding-left",this.nav._props.indent+"em"),t.selected&&this.select(!1,!1)};const u6=(s,n)=>{const t=s[n];return t?typeof t=="function"?t():Promise.resolve(t):new Promise((e,i)=>{(typeof queueMicrotask=="function"?queueMicrotask:setTimeout)(i.bind(null,new Error("Unknown variable dynamic import: "+n)))})},C6=`<div class="container info">\r
	<slot name="before-content"></slot>\r
	<div class="content">\r
		<div class="icon"></div>\r
		<div class="message">\r
			<slot name="content"></slot>\r
			<div class="title"></div>\r
			<div class="text"></div>\r
		</div>\r
	</div>\r
	<div class="close ooicon-close"></div>\r
	<slot name="after-content"></slot>\r
</div>\r
`,He=class He extends k{constructor(){super();c(this,Ve);c(this,We);C(this,"_elements",{container:null,content:null,icon:null,title:null,text:null,close:null});c(this,g2,null);C(this,"_setPropMap",{title:()=>{this._elements.title.textContent=this._props.title},text:()=>{this._elements.text.textContent=this._props.text},icon:()=>{r.toggleClass(this._elements.icon,`ooicon-${this._props.icon}`,this._props.icon)},titleStyle:()=>{this._elements.title.setAttribute("style",this._props.titleStyle)},textStyle:()=>{this._elements.text.setAttribute("style",this._props.textStyle)},type:()=>{a(this,We,q8).call(this)},skin:()=>{a(this,Ve,Y8).call(this).then()},showClose:()=>{r[this._props.showClose?"removeClass":"addClass"](this._elements.close,"hide")},width:()=>{this._props.width&&r.setStyle(this._elements.container,"width",this._props.width)},height:()=>{this._props.width&&r.setStyle(this._elements.container,"height",this._props.height)},contentAlign:()=>{this._props.contentAlign?r.addClass(this._elements.content,"align_"+this._props.contentAlign):r.removeClass(this._elements.content,"align_left align_right align_center")},contentValign:()=>{this._props.contentValign?r.addClass(this._elements.content,"align_"+this._props.contentValign):r.removeClass(this._elements.content,"valign_left valign_right valign_center")},style:()=>{r.toggleAttr(this._elements.container,"style",this._props.style)},$default:t=>{r.toggleAttr(this._elements.container,t,this._props[t])}});this._initialize("oo-notice",He,C6)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_setEvent(){this._elements.close.addEventListener("click",()=>{this._elements.container.style.opacity=0,this._elements.container.addEventListener("transitionend",()=>{this.remove()})})}};g2=new WeakMap,Ve=new WeakSet,Y8=async function(){l(this,g2)&&(l(this,g2).remove(),p(this,g2,null)),p(this,g2,document.createElement("style")),l(this,g2).textContent=!this._props.skin||this._props.skin==="default"?(await Promise.resolve().then(()=>E9)).default:(await u6(Object.assign({"./template/notice.banner.scope.css":()=>Promise.resolve().then(()=>gn),"./template/notice.default.scope.css":()=>Promise.resolve().then(()=>E9)}),`./template/notice.${this._props.skin}.scope.css`)).default,this.shadowRoot.prepend(l(this,g2))},We=new WeakSet,q8=function(){const t="container "+this._props.type||"info";if(this._elements.container.setAttribute("class",t),!this._props.icon){const i="icon "+{info:"ooicon-info",error:"ooicon-error",warn:"ooicon-help",success:"ooicon-check"}[this._props.type]||"ooicon-info";this._elements.icon.setAttribute("class",i)}},C(He,"prop",{title:"",text:"",icon:"",titleStyle:"",textStyle:"",showClose:!0,type:"info",skin:"default",contentAlign:"",contentValign:"",width:"",height:""});let K3=He;customElements.get("oo-notice")||customElements.define("oo-notice",K3);const F=class F{constructor(n){c(this,Ye);c(this,Ot);c(this,qe);c(this,Ue);c(this,Pt);c(this,Xe);c(this,Z1);c(this,Ge);c(this,Ke);c(this,t3);C(this,"options",{title:"",text:"",icon:"",type:"info",skin:"default",style:"",showClose:!0,contentAlign:"",contentValign:"",width:"",height:"",container:"",duration:5e3,location:"topCenter",position:"absolute",marginTop:10,marginBottom:0,marginLeft:10,marginRight:0});C(this,"where",{x:"center",y:"top"});C(this,"location",{fromTop:null,toTop:null,fromBottom:null,toBottom:null,fromLeft:null,toLeft:null,fromRight:null,toRight:null});c(this,Je,{bottom:(n,t)=>{const e=r.getSize(this.node);this.location.toBottom=F.env.height+this.options.marginTop+this.options.marginBottom+t.y,this.location.fromBottom=this.location.toBottom-e.y,F.env.height+=e.y+this.options.marginTop+this.options.marginBottom},top:(n,t)=>{const e=r.getSize(this.node);this.location.toTop=F.env.height+this.options.marginTop+t.y,this.location.fromTop=this.location.toTop-e.y,F.env.height+=e.y+this.options.marginTop+this.options.marginBottom},middle:()=>{}});c(this,Qe,{left:(n,t)=>{this.location.fromLeft=this.location.toLeft=t.x+this.options.marginLeft+this.options.marginRight},right:(n,t)=>{r.getSize(n),this.location.fromRight=this.location.toRight=t.x+this.options.marginLeft+this.options.marginRight},center:(n,t)=>{if(r.isHtml(n))r.addClass(this.node,"body-center");else{r.removeClass(this.node,"body-center");const e=r.getSize(n),i=r.getSize(this.node);this.location.fromLeft=this.location.toLeft=t.x+e.x/2-i.x/2}}});this.options=Object.assign(this.options,n),this.container=this.options.container&&r.el(this.options.container)||document.body;const t=r.isBody(this.container)?"fixed":this.options.position||"absolute";this.node=r("oo-notice."+this.options.skin+(t==="fixed"?".fixed":".absolute")),["title","text","icon","type","skin","style","showClose","contentAlign","contentValign","width","height"].forEach(e=>{this.node.setAttribute(e,this.options[e])}),this.container.append(this.node),a(this,Ue,G8).call(this)}resetPosition(n){const t=n+this.options.marginTop+this.options.marginBottom,e=J4(this.where.y);this.location[`to${e}`]=this.location[`to${e}`]-t,this.location[`from${e}`]=this.location[`from${e}`]-t;const i={};i[this.where.y]=this.location[`to${e}`]+"px",a(this,Z1,_3).call(this,i,!0)}};Ye=new WeakSet,U8=function(){return F.env.notices.includes(this)||(F.env.notices.push(this),a(this,Ge,J8).call(this),this.node._elements.close&&this.node._elements.close.addEventListener("click",()=>{a(this,Pt,N4).call(this)})),a(this,Ot,z4).call(this)},Ot=new WeakSet,z4=function(n){return a(this,Z1,_3).call(this,{top:this.location.fromTop+"px",left:this.location.fromLeft+"px",bottom:this.location.fromBottom+"px",right:this.location.fromRight+"px",opacity:0},n)},qe=new WeakSet,X8=function(n){return a(this,Z1,_3).call(this,{top:this.location.toTop+"px",left:this.location.toLeft+"px",bottom:this.location.toBottom+"px",right:this.location.toRight+"px",opacity:"1"},n)},Ue=new WeakSet,G8=async function(){await a(this,Ye,U8).call(this),await a(this,qe,X8).call(this,!0)&&this.options.duration&&setTimeout(()=>{a(this,Pt,N4).call(this)},this.options.duration||3e3)},Pt=new WeakSet,N4=async function(){if(!this.isHidden){this.isHidden=!0,a(this,Xe,K8).call(this);const n=await a(this,Ot,z4).call(this,!0);n&&n.propertyName==="opacity"&&(this.node.remove(),this.node=null)}},Xe=new WeakSet,K8=function(){const n=F.env.notices.indexOf(this),t=J4(this.where.y),e=Math.abs(this.location[`to${t}`]-this.location[`from${t}`]);F.env.height=F.env.height-e-this.options.marginTop-this.options.marginBottom;for(let i=n+1;i<F.env.notices.length;i++)F.env.notices[i].resetPosition(e);F.env.notices.splice(n,1)},Z1=new WeakSet,_3=function(n,t){return new Promise(e=>{if(r.setStyles(this.node,n),t){r.addClass(this.node,"transition");const i=o=>{r.removeClass(this.node,"transition"),this.node.removeEventListener("transitionend",i),e(o)};this.node.addEventListener("transitionend",i)}else setTimeout(()=>{e()})})},Ge=new WeakSet,J8=function(){const n=a(this,t3,t7).call(this),t=r.isBody(this.container)?document.documentElement:this.container,e=a(this,Ke,Q8).call(this,t);l(this,Je)[n.y](t,e),l(this,Qe)[n.x](t,e)},Ke=new WeakSet,Q8=function(n,t,e){const i=r("div");n.append(i);const o=r.getOffsetParent(i);return i.remove(),o===n?{x:0,y:0}:(()=>r.getPosition(n,r.getOffsetParent(n)))()},Je=new WeakMap,Qe=new WeakMap,t3=new WeakSet,t7=function(){const n={left:"x",right:"x",center:"x",top:"y",bottom:"y",middle:"y"};return _(this.options.location).split("-").forEach(t=>{n[t]&&(this.where[n[t]]=t)}),this.where},C(F,"env",{notices:[],height:0});let B1=F;const V1={error:(s,n,t={},e)=>{e&&(t=Object.assign(t,{skin:"default",location:"topRight",marginTop:10,duration:5e3}));const o=Object.assign({title:s,text:n,duration:0,skin:"banner",marginTop:0,type:"error"},t);new B1(o);let d;throw d=new Error(n,{cause:o.err}),d.name=s,d},msg:(s,n,t,e={})=>{const i=Object.assign({title:s,text:n,duration:5e3,type:t,location:"topRight",marginTop:10},e);new B1(i)},failed:(s,n,t={})=>{const e=Object.assign({duration:8e3},t);V1.msg(s,n,"error",e)},success:(s,n,t={})=>{V1.msg(s,n,"success",t)},info:(s,n,t={})=>{V1.msg(s,n,"info",t)},warn:(s,n,t={})=>{V1.msg(s,n,"warn",t)}},m6=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    --radius: var(--oo-default-radius);\r
    --header-padding: 1em 0.5em 1.2em 1em;\r
    --body-padding: 0.5em 1.5em;\r
\r
    --icon-color: var(--oo-color-main);\r
    --icon-size: var(--oo-font-size-larger);\r
}\r
.content{\r
    /*padding: var(--oo-menu-padding);*/\r
    width: 100%;\r
    height: 100%;\r
    resize: both;\r
}\r
.buttons{\r
    display: flex;\r
    justify-content: flex-end;\r
}\r
.buttons>oo-button{\r
    display: none;\r
    margin-left: 0.5em;\r
    min-width: 5em\r
}\r
.actions>div.show{\r
    display: block;\r
}\r
.buttons>.show{\r
    display: inline-block;\r
}\r
.header{\r
    height: 4em;\r
    padding: 1.5em 1em 1em 1.5em;\r
    display: flex;\r
    justify-content: space-between;\r
    align-items: center;\r
    border-top-right-radius: var(--radius);\r
    border-top-left-radius: var(--radius);\r
}\r
.header.small{\r
    height: 3em;\r
    padding: 1em 0.5em 1.2em 1em;\r
}\r
.title{\r
    font-size: 1.125em;\r
    width: 100%;\r
}\r
.actions>div{\r
    cursor: pointer;\r
    border-radius: var(--radius);\r
    padding: 0.2em;\r
    color: var(--oo-color-text2);\r
    display: none;\r
}\r
.actions>div:hover{\r
    background-color: var(--oo-color-gray-e)\r
}\r
\r
.body{\r
    padding: var(--body-padding);\r
    height: calc(100% - 4em - 5em);\r
    overflow: auto;\r
    display: flex;\r
    align-items: center;\r
}\r
.bodyIcon{\r
    color: var(--icon-color);\r
    font-size: var(--icon-size);\r
    margin-right: 0.4em;\r
}\r
.bodyContent{\r
    width: 100%;\r
    height: 100%;\r
}\r
.buttons{\r
    padding: 1.5em 1.5em;\r
    height: 5em;\r
}\r
.buttons.small{\r
    padding: 1em 1.5em;\r
    height: 4em;\r
}\r
.footer{\r
    height: 0.6em;\r
    border-bottom-right-radius: var(--radius);\r
    border-bottom-left-radius: var(--radius);\r
}\r
/*.resize{*/\r
/*    height: 0.6em;*/\r
/*    width: 0.6em;*/\r
/*    float: right;*/\r
/*    border-bottom-right-radius: var(--oo-menu-radius);*/\r
/*    border-top-left-radius: 100%;*/\r
/*    background-color: var(--oo-color-gray-e);*/\r
/*    cursor: nw-resize;*/\r
/*    display: none;*/\r
/*}*/\r
/*.resize.show, .actions .show{*/\r
/*    display: block;*/\r
/*}*/\r
\r
.hide{\r
    display: none;\r
}\r
`,g6=`
<div class="dialog content">
    <div class="header" draggable="true">
        <div class="title"></div>
        <div class="actions">
            <div class="close ooicon-close show"></div>
        </div>
    </div>
    <div class="body">
        <div class="bodyIcon hide"></div>
        <div class="bodyContent">
            <slot name="content"></slot>
        </div>
    </div>
    <div class="buttons">
        <oo-button class="button_ok show">OK</oo-button>
        <oo-button class="button_yes">Yes</oo-button>
        <oo-button class="button_no" type="cancel">No</oo-button>
        <oo-button class="button_cancel show" type="cancel">Cancel</oo-button>
    </div>
<!--    <div class="footer">-->
<!--        <div class="resize" draggable="true"></div>-->
<!--    </div>-->
</div>
`,Zt=class Zt extends Jt{constructor(){super();c(this,e3);c(this,n3);c(this,s3);c(this,Q2);C(this,"_elements",{header:null,title:null,actions:null,close:null,body:null,buttons:null,button_ok:null,button_yes:null,button_no:null,button_cancel:null,footer:null,resize:null,bodyIcon:null});c(this,Z,{x:0,y:0,positionX:0,positionY:0,styleX:"left",styleY:"top"});c(this,d4,{x:0,y:0,width:0,height:0});const t=N1.get("index").o("common");["ok","yes","no","cancel"].forEach(e=>Zt.prop[e]=t[e]),a(this,e3,e7).call(this),this._init("oo-dialog",Zt,g6,m6)}static get observedAttributes(){return Object.keys(this.prop).map(t=>_(t))}_addHideEvent(){}_removeHideEvent(){}_setEvent(){super._setEvent(),this._elements.close.addEventListener("click",t=>{this.close(t)}),this._elements.button_cancel.addEventListener("click",t=>{a(this,Q2,Ht).call(this,"cancel",t),this.stopPropagation||this.dispatchEvent(new CustomEvent("resolvecancel")),this.stopPropagation=!1}),this._elements.button_ok.addEventListener("click",t=>{a(this,Q2,Ht).call(this,"ok",t,this._props.closeOnEvent),this.stopPropagation||this.dispatchEvent(new CustomEvent("resolveok")),this.stopPropagation=!1}),this._elements.button_yes.addEventListener("click",t=>{a(this,Q2,Ht).call(this,"yes",t,this._props.closeOnEvent),this.stopPropagation||this.dispatchEvent(new CustomEvent("resolveyes")),this.stopPropagation=!1}),this._elements.button_no.addEventListener("click",t=>{a(this,Q2,Ht).call(this,"no",t),this.stopPropagation||this.dispatchEvent(new CustomEvent("resolveno")),this.stopPropagation=!1}),this._elements.header.addEventListener("drag",t=>{if(t.screenX||t.screenY){const e=t.screenX-l(this,Z).x,i=t.screenY-l(this,Z).y,o=l(this,Z).styleX==="right"?l(this,Z).positionX-e:l(this,Z).positionX+e,d=l(this,Z).styleY==="bottom"?l(this,Z).positionY-i:l(this,Z).positionY+i,h={};h[l(this,Z).styleX]=o+"px",h[l(this,Z).styleY]=d+"px",r.setStyles(this,h),this.dispatchEvent(new DragEvent("move",t))}}),this._elements.header.addEventListener("dragend",t=>{this.dispatchEvent(new DragEvent("endMove",t))}),this._elements.header.addEventListener("dragstart",t=>{p(this,Z,a(this,n3,n7).call(this)),l(this,Z).x=t.screenX,l(this,Z).y=t.screenY,this.dispatchEvent(new DragEvent("startMove",t))})}close(t){this.dispatchEvent(new MouseEvent("close",t)),this.hide()}_afterHide(){this.remove()}_render(){["ok","yes","no","cancel"].forEach(t=>this._setPropMap[t]()),this._fillContent("content")}};e3=new WeakSet,e7=function(){this.setPropMap=Object.assign(this._setPropMap,{title:()=>{this._props.title&&(this._elements.title.textContent=this._props.title),this.removeAttribute("title")},buttons:()=>{if(this._props.buttons){r.removeClass(this._elements.buttons,"hide");const t=this._props.buttons.split(/,\s*/g);["ok","yes","no","cancel"].forEach(e=>{t.includes(e)?r.addClass(this._elements[`button_${e}`],"show"):r.removeClass(this._elements[`button_${e}`],"show")})}else r.addClass(this._elements.buttons,"hide")},buttonAlign:()=>{this._elements.buttons&&r.setStyle(this._elements.buttons,"justify-content",this._props.buttonAlign||"flex-end")},canResize:()=>{const t=this._props.canResize?"addClass":"removeClass";r[t](this,"resize")},canMove:()=>{this._elements.header.setAttribute("draggable",!!this._props.canMove)},canClose:()=>{const t=this._props.canClose?"addClass":"removeClass";r[t](this._elements.close,"show")},icon:(t,e)=>{this._props.icon?this._elements.bodyIcon&&(r.addClass(this._elements.bodyIcon,"ooicon-"+this._props.icon),r.removeClass(this._elements.bodyIcon,"hide")):this._elements.bodyIcon&&(r.removeClass(this._elements.bodyIcon,"ooicon-"+e),r.addClass(this._elements.bodyIcon,"hide"))},ok:()=>{this._elements.button_ok&&(this._elements.button_ok.textContent=this._props.ok)},yes:()=>{this._elements.button_ok&&(this._elements.button_yes.textContent=this._props.yes)},no:()=>{this._elements.button_ok&&(this._elements.button_no.textContent=this._props.no)},cancel:()=>{this._elements.button_ok&&(this._elements.button_cancel.textContent=this._props.cancel)},okSize:()=>{this._elements.button_ok&&(this._props.okSize?r.setStyle(this._elements.button_ok,"width",this._props.okSize):r.setStyle(this._elements.button_ok,"width","unset"))},size:()=>{this._props.size==="small"?(r.addClass(this._elements.header,"small"),r.addClass(this._elements.buttons,"small")):(r.removeClass(this._elements.header,"small"),r.removeClass(this._elements.buttons,"small"))},closeOnEvent:()=>{}})},Z=new WeakMap,n3=new WeakSet,n7=function(){const t={x:0,y:0,positionX:0,positionY:0};t.styleX=this.style.right?"right":"left",t.styleY=this.style.bottom?"bottom":"top";const e=r.getOffsetParent(this),i=r.getPosition(this,e),o=r.getSize(e),d=r.getSize(this);return t.positionX=t.styleX==="right"?o.x-(i.x+d.x):i.x,t.positionY=t.styleY==="bottom"?o.y-(i.y+d.y):i.y,t},d4=new WeakMap,s3=new WeakSet,s7=function(t,e){const i=new MouseEvent(t,Object.assign({},e,{bubbles:!1})),o=i.preventDefault;i.preventDefault=()=>{this.preventDefault=!0,o.apply(i)};const d=i.stopPropagation;i.stopPropagation=()=>{this.stopPropagation=!0,d.apply(i)};const h=i.stopImmediatePropagation;return i.stopImmediatePropagation=()=>{this.stopPropagation=!0,h.apply(i)},i},Q2=new WeakSet,Ht=function(t,e,i=!0){const o=a(this,s3,s7).call(this,t,e);this.dispatchEvent(o),!this.preventDefault&&i&&this.close(e),this.preventDefault=!1},C(Zt,"prop",{css:"",cssLink:"",skin:"",isShow:!1,title:"",canResize:!1,canMove:!0,canClose:!0,icon:"",closeOnEvent:!0,buttons:"ok, cancel",ok:"OK",yes:"Yes",no:"No",cancel:"Cancel",buttonAlign:"flex-end",size:"default",okSize:""});let J3=Zt;customElements.get("oo-dialog")||customElements.define("oo-dialog",J3);class y9{constructor(n,t){c(this,i3);c(this,jt);c(this,Dt);c(this,r3);C(this,"options",{css:"",cssLink:"",skin:"",isShow:!1,title:"",canResize:!1,canMove:!0,canClose:!0,icon:"",buttons:"ok, cancel",content:null,position:"center center",offset:{x:0,y:0},zIndex:100,modal:!0,modalArea:null,width:"",height:"",maxWidth:"",events:{},buttonAlign:"flex-end",positionNode:null,okSize:""});C(this,"dialogEvents",[]);const e=Event.prototype.isPrototypeOf(n)?r.getPositionParent(n.currentTarget):n;this.options=Object.assign(this.options,t),this.container=e||document.body,this.positionNode=this.options.positionNode||this.container,this.area=this.options.area||this.container,this.options.modal&&!this.options.modalArea&&(this.options.modalArea=this.container)}async show(n){var t,e;n&&(this.options.position=n),this.dialog||this._createDialog(),await this._positionDialog(),(t=this.options)!=null&&t.attr&&r.set(this.dialog,(e=this.options)==null?void 0:e.attr),this.dialog.show(),this.options.modal&&this.options.modalArea&&this._showModal()}hide(){this.dialog.hide()}close(){this.dialog.close(),this.dialog=null}_showModal(){const n=r.getStyle(this.dialog,"z-index");this.modalNode=r("div.oo-dialog-modal"),this.dialog.insertAdjacentElement("beforebegin",this.modalNode);const t=r.getOffsetParent(this.modalNode);if(t!==this.options.modalArea){const e=r.getPosition(this.options.modalArea,t),i=e.x+"px",o=e.y+"px";r.setStyles(this.modalNode,{left:i,top:o})}r.setStyles(this.modalNode,{opacity:.3,"z-index":n-1})}_createDialog(){this.dialog=r("oo-dialog",{styles:{position:"absolute"}}),["css","cssLink","skin","isShow","okSize","title","canResize","canMove","canClose","icon","buttons","ok","yes","no","cancel","buttonAlign","closeOnEvent"].forEach(t=>{this.options.hasOwnProperty(t)&&this.dialog.setAttribute(_(t),this.options[t])}),this.options.events&&Object.keys(this.options.events).forEach(t=>{this.dialog.addEventListener(t,this.options.events[t])}),this.dialogEvents&&this.dialogEvents.length&&this.dialogEvents.forEach(({type:t,listener:e})=>{this.dialog.addEventListener(t,e)});const n=this.options.zIndex||100;r.setStyle(this.dialog,"z-index",n),a(this,jt,R4).call(this,this.options.content),this.container.insertAdjacentElement("beforeend",this.dialog),this.options.width&&r.setStyle(this.dialog,"width",this.options.width),this.options.height&&r.setStyle(this.dialog,"height",this.options.height),this.options.maxWidth&&r.setStyle(this.dialog,"max-width",this.options.maxWidth),this.dialog.addEventListener("hide",()=>{a(this,i3,i7).call(this)})}async _getDialogPosition(){const n=await new Promise(o=>{window.setTimeout(()=>{o(r.getSize(this.dialog))})});n.y<300&&this.dialog.setAttribute("size","small");const t=this.options.position||"center center",e=t.split(/\s+/),i=a(this,r3,r7).call(this,{x:e[0],y:e.length>1?e[1]:e[0]},n);return i["transform-origin"]=t,i}async _positionDialog(){if(this.options.position&&this.options.position!=="none"){const n=await this._getDialogPosition();r.setStyles(this.dialog,n)}}addEventListener(n,t){this.dialog?this.dialog.addEventListener(n,t):this.dialogEvents.push({type:n,listener:t})}}i3=new WeakSet,i7=function(){this.modalNode&&(this.modalNode.addEventListener("transitionend",n=>{this.modalNode.remove(),this.modalNode=null}),r.setStyles(this.modalNode,{opacity:0}))},jt=new WeakSet,R4=function(n){if(n)switch(v(n)){case"element":this.dialog.append(n);break;case"string":this.dialog.append(r("div",{html:j7(n)}));break;case"promise":n.then(e=>{a(this,jt,R4).call(this,e)}).catch(()=>{});break}},Dt=new WeakSet,B4=function(n,t,e,i){const o=n==="y"?"top":"left",d=`calc(100% - ${e[n]}px)`,h=t[n]==="center"?`50% - ${e[n]/2}px`:"0px",u=t[n]==="center"?o:t[n],m={};return m[u]=`clamp(0%, calc(${h} + ${(this.options.offset[n]||0)+"em"} + ${i[n]}px), ${d})`,m},r3=new WeakSet,r7=function(n,t){const e=this.options.positionNode&&Event.prototype.isPrototypeOf(this.options.positionNode)?this.options.positionNode.target:this.options.positionNode,i={x:0,y:0};if(e){const{x:o,y:d}=r.getPosition(e,this.container),h=r.getSize(e),u=r.getSize(this.container);switch(n.x){case"center":i.x=o-u.x/2+h.x/2;break;case"left":i.x=o;break;case"right":i.x=u.x-o-h.x;break}switch(n.y){case"center":i.y=d-u.y/2+h.y/2;break;case"top":i.y=d;break;case"bottom":i.y=u.y-d-h.y;break}}return{...a(this,Dt,B4).call(this,"x",n,t,i),...a(this,Dt,B4).call(this,"y",n,t,i)}};function x9(s,n,t,e={}){const i=t||document.body,o={title:s,content:n};return new Promise(d=>{const h=e.buttons||"ok, cancel",u=new y9(i,Object.assign(o,e,{closeOnEvent:!1}));h.split(/,\s*/g).forEach(m=>{u.addEventListener("resolve"+m,f=>{d({dlg:u,status:m})})}),u.show()})}const f6={async msg(s,n,t,e,i="right top",o={},d="info"){const h=t||document.body;return e||(i="center center"),await x9(s,n,h,Object.assign({canClose:!1,maxWidth:"40vw"},o,{positionNode:e,position:i},{info:{icon:"info",skin:""},error:{icon:"error",skin:"icon-color: var(--oo-color-error)"},warn:{icon:"help",skin:"icon-color: var(--oo-color-warn)"},success:{icon:"check",skin:"icon-color: var(--oo-color-success)"}}[d]))},async info(s,n,t,e,i="right top",o={}){return await this.msg(...arguments)},async warn(s,n,t,e,i="right top",o={}){return await this.msg(s,n,t,e,i,o,"warn")},async error(s,n,t,e,i="right top",o={}){return await this.msg(s,n,t,e,i,o,"error")},async success(s,n,t,e,i="right top",o={}){return await this.msg(s,n,t,e,i,o,"success")}},v6=Object.assign({"./styles/mask/default.css":()=>Promise.resolve().then(()=>fn)}),T2=class T2{constructor(n,t){c(this,o3);c(this,l3);c(this,a3);this.node=n,this.options=Object.assign({},R1.option,t)}show(){a(this,a3,a7).call(this),this.maskNode||a(this,o3,o7).call(this),this.positionMaskNode(),r.addClass(this.maskNode,"show")}hide(){r.removeClass(this.maskNode,"show"),setTimeout(()=>{this.maskNode.remove()},200)}positionMaskNode(){r.overlap(this.maskNode,this.node,r.getOffsetParent(this.node))}};o3=new WeakSet,o7=function(){this.maskNode=r("div.maskNode"),this.node.insertAdjacentElement("afterend",this.maskNode)},l3=new WeakSet,l7=async function(n){const t=await v6[`./styles/mask/${n}.css`]();return t9(t.default,this.container)},a3=new WeakSet,a7=async function(){const n=T2.loadedStyles[this.options.style||"default"];if(n){const t=await Promise.resolve(n);r.addClass(this.maskNode,t.id)}else{const t=this.options.style||"default";T2.loadedStyles[t]=a(this,l3,l7).call(this,t),T2.loadedStyles[t]=await T2.loadedStyles[t]}},C(T2,"option",{position:"top",style:"default",autoClose:0,emerge:-8,offset:6,defer:600,events:{show:"mouseover",hide:"mouseout, mousedown"},showOnEnterCotent:!1,resetContentOnShow:!1}),C(T2,"loadedStyles",{});let Q3=T2;function b6(s,n){return s.mask||(s.mask=new Q3(s,n)),s.mask.show(),s.mask}function L6(s){const n=s.mask;n&&n.hide()}class k9{constructor(n,t={}){c(this,c3);c(this,h3);c(this,p3);c(this,t1,null);c(this,e1,null);c(this,f2,null);c(this,n1,null);c(this,O2,null);c(this,d3,null);c(this,X,{});this.node=r.el(n),this.options=t,this.start={x:0,y:0},this.container=r.getPositionParent(this.node),p(this,t1,l(this,t1)||(e=>{this.dragReady(e)})),this.node.addEventListener("mousedown",l(this,t1)),p(this,f2,l(this,f2)||(e=>{this.dragChecK(e)})),p(this,d3,l(this,f2)||(e=>{this.dragstart(e)})),p(this,e1,l(this,e1)||(e=>{this.drag(e)})),p(this,n1,l(this,n1)||(e=>{this.dragend(e)})),p(this,O2,l(this,O2)||(e=>{this.dragcancel(e)}))}setOptions(n={}){Object.assign(this.options,n)}addEventListener(n,t){l(this,X)[n]||(l(this,X)[n]=[]),l(this,X)[n].push(t)}removeEventListener(n,t){if(l(this,X)[n])if(t){const e=l(this,X)[n].indexOf(t);l(this,X)[n].splice(e,1)}else l(this,X)[n]=[]}dispatchEvent(n,t){l(this,X)[n]&&l(this,X)[n].forEach(e=>{e.apply(this,[t,this])})}dragReady(n){if(n.buttons===1){const{clientX:t,clientY:e}=n;this.start={x:t,y:e},this.container.addEventListener("mousemove",l(this,f2)),this.container.addEventListener("mouseup",l(this,O2)),this.dispatchEvent("dragready",n)}}dragChecK(n){if(n.buttons===1){if(!n.clientX||!n.clientY)return;const{clientX:t,clientY:e}=n,i=this.options.snap||6;Math.abs(this.start.x-t)>i||Math.abs(this.start.y-e)>i?this.dragCheckDetermined(n):this.dispatchEvent("dragchecK",n)}else this.dragcancel()}dragCheckDetermined(n){this.container.removeEventListener("mousemove",l(this,f2)),this.container.addEventListener("mousemove",l(this,e1)),this.container.removeEventListener("mouseup",l(this,O2)),this.container.addEventListener("mouseup",l(this,n1)),this.dragstart(n)}dragstart(n){this.dragNode||(this.dragNode=a(this,c3,d7).call(this)),a(this,p3,h7).call(this,n),this.dispatchEvent("dragstart",n)}drag(n){if(n.buttons===1){const{clientX:t,clientY:e}=n,i=t-this.start.x,o=e-this.start.y;r.setStyles(this.dragNode,{left:`${this.start.pos.x+i}px`,top:`${this.start.pos.y+o}px`}),this.dispatchEvent("dragmove",n)}else this.dragend()}dragcancel(n){this.removeAllEvents(),this.dispatchEvent("dragcancel",n)}dragend(n){this.dispatchEvent("dragend",n),this.removeAllEvents(),r.removeClass(this.node,"dragged"),this.dragNode!==this.node&&this.dragNode.remove()}removeAllEvents(){this.container.removeEventListener("mousemove",l(this,f2)),this.container.removeEventListener("mousemove",l(this,e1)),this.container.removeEventListener("mouseup",l(this,O2)),this.container.removeEventListener("mouseup",l(this,n1))}destroy(){this.removeAllEvents(),this.node.removeEventListener("mousedown",l(this,t1))}}t1=new WeakMap,e1=new WeakMap,f2=new WeakMap,n1=new WeakMap,O2=new WeakMap,d3=new WeakMap,X=new WeakMap,c3=new WeakSet,d7=function(){switch(this.options.dragNode||(this.options.dragNode="clone"),v(this.options.dragNode)){case"string":return this.options.dragNode==="none"?this.node:a(this,h3,c7).call(this);case"function":return this.options.dragNode(this);case"element":return this.options.dragNode}},h3=new WeakSet,c7=function(){const n=this.options.dragNode==="deepClone",t=this.node.cloneNode(n||!0);return r.setStyles(t,{opacity:.5}),t},p3=new WeakSet,h7=function(n){const t=r.getSize(this.node),e=r.getPosition(this.container,document.body),i=n.clientX-e.x,o=n.clientY-e.y;console.log(i),this.start.pos={x:i,y:o},r.setStyles(this.dragNode,{position:"absolute",width:`${t.x}px`,height:`${t.y}px`,left:`${i}px`,top:`${o}px`,background:"#dddddd","z-index":"10000"}),r.addClass(this.dragNode,"drag"),r.addClass(this.node,"dragged"),this.container.append(this.dragNode)};class _6 extends k9{constructor(t,e,i={}){super(t,i);C(this,"dropNode",null);this.droppables=e||[],this.droppableNodes=null}setDroppables(t){t&&(this.droppables=t)}getDroppableNodes(){switch(v(this.droppables)){case"string":return[...document.querySelectorAll(this.droppables)];case"function":return this.droppables(this);case"array":return this.droppables}}dragCheckDetermined(t){super.dragCheckDetermined(t),this.droppableNodes=this.getDroppableNodes(),this.droppablesRect=this.droppableNodes.map(e=>{const i=e.getBoundingClientRect(),o=i.width*i.height;return{node:e,rect:i,area:o}})}drag(t){if(super.drag(t),t.buttons===1){const e=this.findDropNode(t);e?this.dropNode!==e?(this.dropNode&&this.dropleave(t),this.dropNode=e,this.dropenter(t)):this.dropover(t):this.dropNode&&(this.dropleave(t),this.dropNode=e)}}findDropNode(t){const e=this.droppablesRect.filter(i=>t.pageY>i.rect.top&&t.pageY<i.rect.top+i.rect.height&&t.pageX>i.rect.left&&t.pageX<i.rect.left+i.rect.width);if(e.length){let i=e[0];return e.forEach(o=>{o.area<i.area&&(i=o)}),i.node.node||i.node}return null}createMarkNode(){switch(this.options.dropMark||(this.options.dropMark={border:"2px solid var(--oo-color-main)","z-index":"19999"}),v(this.options.dropMark)){case"object":if(this.markNode)return this.markNode;const t=r("div",{styles:this.options.dropMark}),e=r("div",{styles:{"background-color":"var(--oo-color-main-light)",width:"100%",height:"100%",opacity:"0.1"}});return t.append(e),t;case"element":return this.options.dropMark;case"function":return this.options.dropMark(this)}}positionMarkNode(){if(this.options.dropMark!=="none"&&(this.markNode=this.createMarkNode(),this.markNode)){const t=this.options.markContainer||this.container,e=r.getSize(this.dropNode),i=r.getPosition(this.dropNode,t);r.setStyles(this.markNode,{position:"absolute","z-index":"9999",transition:"width 0.2s, height 0.2s, left 0.2s, top 0.2s"}),t.append(this.markNode),window.setTimeout(()=>{this.markNode&&r.setStyles(this.markNode,{width:`${e.x}px`,height:`${e.y}px`,left:`${i.x}px`,top:`${i.y}px`})},10)}}removeMarkNode(){this.markNode&&this.markNode.remove()}dropenter(t){this.positionMarkNode(),this.dispatchEvent("dragenter",t),t.stopPropagation()}dropleave(t){this.dispatchEvent("dragleave",t),this.removeMarkNode(),this.dropNode=null,t.stopPropagation()}dropover(t){this.dispatchEvent("dragover",t),t.stopPropagation()}dragend(t){this.dropNode?(this.dispatchEvent("drop",t),this.dispatchEvent("dragend",t)):this.dispatchEvent("dragcancel",t),this.removeAllEvents(),r.removeClass(this.node,"dragged"),this.dragNode!==this.node&&this.dragNode.remove()}removeAllEvents(){super.removeAllEvents(),this.removeMarkNode()}}function w6(){if(customElements.get("oo-button")||customElements.define("oo-button",n9),customElements.get("oo-calendar")||customElements.define("oo-calendar",z1),customElements.get("oo-calendar-view")||customElements.define("oo-calendar-view",M3),customElements.get("oo-capsulae")||customElements.define("oo-capsulae",S3),customElements.get("oo-card")||customElements.define("oo-card",O3),customElements.get("oo-checkbox-group")||customElements.define("oo-checkbox-group",P3),customElements.get("oo-checkbox")||customElements.define("oo-checkbox",o0),customElements.get("oo-datetime")||customElements.define("oo-datetime",Z3),customElements.get("oo-file")||customElements.define("oo-file",j3),customElements.get("oo-icon")||customElements.define("oo-icon",D3),customElements.get("oo-input")||customElements.define("oo-input",g0),customElements.get("oo-select")||customElements.define("oo-select",R3),customElements.get("oo-option-group")||customElements.define("oo-option-group",$3),customElements.get("oo-option")||customElements.define("oo-option",F3),customElements.get("oo-org")||customElements.define("oo-org",I3),customElements.get("oo-pagination")||customElements.define("oo-pagination",z3),customElements.get("oo-radio-group")||customElements.define("oo-radio-group",q0),customElements.get("oo-radio")||customElements.define("oo-radio",M0),customElements.get("oo-selector")||customElements.define("oo-selector",K0),customElements.get("oo-skeleton")||customElements.define("oo-skeleton",V3),customElements.get("oo-switch")||customElements.define("oo-switch",W3),customElements.get("oo-tab")||customElements.define("oo-tab",H3),customElements.get("oo-tabs")||customElements.define("oo-tabs",Y3),customElements.get("oo-tab")||customElements.define("oo-tag",q3),customElements.get("oo-textarea")||customElements.define("oo-textarea",U3),customElements.get("oo-nav")||customElements.define("oo-nav",X3),HTMLFormElement){const s="oo-input, oo-radio-group, oo-checkbox-group, oo-selector, oo-select";if(HTMLFormElement.prototype.reportValidity&&!HTMLFormElement.prototype.reportValidity.OOInputAdded){const n=HTMLFormElement.prototype.reportValidity;HTMLFormElement.prototype.reportValidity=function(){const t=n.apply(this);if(!t)return t;const e=this.querySelectorAll(s);for(const i of e)if(i.reportValidity&&!i.reportValidity())return!1;return!0},HTMLFormElement.prototype.reportValidity.OOInputAdded=!0}if(HTMLFormElement.prototype.checkValidity&&!HTMLFormElement.prototype.checkValidity.OOInputAdded){const n=HTMLFormElement.prototype.checkValidity;HTMLFormElement.prototype.checkValidity=function(){let t=n.apply(this);const e=this.querySelectorAll(s);for(const i of e)i.checkValidity&&(t=t&&i.checkValidity());return t},HTMLFormElement.prototype.checkValidity.OOInputAdded=!0}}}const y6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_ai</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-701.000000, -5214.000000)">\r
            <g id="icon_ai" transform="translate(701.000000, 5214.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#FCECD3"></path>\r
                <g id="eps-2" transform="translate(14.400106, 7.200000)" fill="#F29F22">\r
                    <path d="M5.66147171,9.90586129 L5.66147171,10.7366305 L1.66147171,10.7366305 L1.66147171,9.90586129 L5.66147171,9.90586129 Z M3.4355097,0.740009276 L3.4355097,5.59629302 C3.00831174,5.71314019 2.69301872,6.1252267 2.69301872,6.61593769 C2.69301872,7.19758216 3.13576826,7.66879899 3.68234257,7.66879899 C4.22897223,7.66879899 4.67210918,7.19752326 4.67210918,6.61593769 C4.67210918,6.1252267 4.35637341,5.71319908 3.92950751,5.59629302 L3.92950751,5.59629302 L3.92950751,0.740009276 L4.13090321,0.740009276 L6.88237025,7.36790575 L5.62623445,9.87847081 L1.73889345,9.87847081 L0.482370247,7.36790575 L3.23416935,0.740009276 L3.4355097,0.740009276 Z" id="Combined-Shape"></path>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#F8CF90"></path>\r
                <path d="M34.3596082,21.1704545 C35.2655716,21.1704545 36,21.9044128 36,22.8116116 L36,30.9838429 C36,31.8902289 35.2645731,32.625 34.3596082,32.625 L1.64039183,32.625 C0.734428437,32.625 0,31.8910417 0,30.9838429 L0,22.8116116 C0,21.9052256 0.73542691,21.1704545 1.64039183,21.1704545 L34.3596082,21.1704545 Z M16.7027344,24.1529785 C16.5418945,24.1529785 16.4067627,24.18396 16.2973389,24.2459229 C16.187915,24.3078857 16.099585,24.3889648 16.0323486,24.4891602 C15.9651123,24.5893555 15.9018311,24.7152588 15.8425049,24.8668701 C15.8029541,24.9679443 15.7672119,25.0593506 15.7352783,25.1410889 L15.6902344,25.2564453 L14.2070801,29.0216797 C14.1464355,29.1719727 14.1029297,29.2893066 14.0765625,29.3736816 C14.0501953,29.4580566 14.0370117,29.5397949 14.0370117,29.6188965 C14.0370117,29.7560059 14.0937012,29.8786133 14.2070801,29.9867188 C14.320459,30.0948242 14.4509766,30.148877 14.5986328,30.148877 C14.7726562,30.148877 14.8979004,30.0981201 14.9743652,29.9966064 C15.0380859,29.9120117 15.112793,29.7619568 15.1984863,29.5464417 L15.2512207,29.4092773 L15.5280762,28.6657227 L17.8853027,28.6657227 L18.1621582,29.393457 C18.1990723,29.4831055 18.2432373,29.5865967 18.2946533,29.7039307 C18.3460693,29.8212646 18.3941895,29.9082764 18.4390137,29.9649658 C18.4838379,30.0216553 18.539209,30.0664795 18.605127,30.0994385 C18.6710449,30.1323975 18.7501465,30.148877 18.8424316,30.148877 C19.0006348,30.148877 19.1357666,30.0928467 19.2478271,29.9807861 C19.3598877,29.8687256 19.415918,29.745459 19.415918,29.6109863 C19.415918,29.5033203 19.3747192,29.3416382 19.2923218,29.1259399 L19.2379395,28.9900391 L17.7231445,25.2485352 C17.6519531,25.0613281 17.5932861,24.9116943 17.5471436,24.7996338 C17.501001,24.6875732 17.4443115,24.5827637 17.3770752,24.4852051 C17.3098389,24.3876465 17.2215088,24.3078857 17.112085,24.2459229 C17.0026611,24.18396 16.8662109,24.1529785 16.7027344,24.1529785 Z M20.9069824,24.1529785 C20.7355957,24.1529785 20.5958496,24.2123047 20.4877441,24.330957 C20.4012598,24.4258789 20.3493691,24.5587695 20.3320723,24.7296289 L20.3255859,24.8648926 L20.3255859,29.4330078 C20.3255859,29.6703125 20.3802979,29.8489502 20.4897217,29.9689209 C20.5991455,30.0888916 20.7382324,30.148877 20.9069824,30.148877 C21.0836426,30.148877 21.2266846,30.0895508 21.3361084,29.9708984 C21.4236475,29.8759766 21.4761709,29.7422422 21.4936787,29.5696953 L21.5002441,29.4330078 L21.5002441,24.8648926 C21.5002441,24.6249512 21.4455322,24.4463135 21.3361084,24.3289795 C21.2266846,24.2116455 21.0836426,24.1529785 20.9069824,24.1529785 Z M16.6948242,25.390918 L17.5688965,27.7837402 L15.8365723,27.7837402 L16.6948242,25.390918 Z" id="形状结合" fill="#F29F22"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),x6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_arch</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-767.000000, -5214.000000)">\r
            <g id="icon_arch" transform="translate(767.000000, 5214.000000)">\r
                <path d="M23.604,0 L23.6045455,6.21998174 C23.6045455,6.99128734 24.2096546,9.01596452 26.960151,9.01596452 L32.604,8.985 L32.5571138,33.0899335 C32.5571138,34.4653386 31.4404421,35.5573497 30.0474034,35.639316 L29.878966,35.6442572 L5.82814787,35.6442572 C4.35779218,35.6442572 3.15,34.5203548 3.15,33.0899335 L3.15,33.0899335 L3.15,2.55432373 C3.15,1.12390244 4.35779218,0 5.82814787,0 L5.82814787,0 L23.604,0 Z" id="Combined-Shape" fill="#E6EBEF"></path>\r
                <path d="M21.819278,9.36683891 L21.819278,9.92708207 L14.1793768,9.92708207 L14.1793768,9.36683891 C14.1793768,9.2025 14.226945,9.07178116 14.3220815,8.97466869 C14.417218,8.87755623 14.508683,8.82153191 14.5965033,8.80659574 L21.2704343,8.80659574 C21.3143445,8.80659574 21.3655708,8.81032979 21.4241133,8.81779787 C21.4826557,8.82527964 21.5631599,8.87755623 21.6655991,8.97466869 C21.7680517,9.07178116 21.819278,9.2025 21.819278,9.36683891 Z M20.1947088,7.68610942 C20.238619,7.68610942 20.2898453,7.68984347 20.3483877,7.69731155 C20.4069167,7.70479331 20.4874209,7.75706991 20.5898736,7.85418237 C20.6923262,7.95129483 20.7435525,8.08201368 20.7435525,8.24635258 L15.2770641,8.24635258 C15.2770641,8.08201368 15.3246324,7.95129483 15.4197688,7.85418237 C15.5149053,7.75706991 15.6063703,7.70104559 15.6941907,7.68610942 L20.1947088,7.68610942 Z M22.9169654,9.92708207 C23.1657807,10.1661155 23.304814,10.3379362 23.3340919,10.442503 C23.3780021,10.5769696 23.3780021,10.7786489 23.3340919,11.0475684 L22.4998522,16.0897568 C22.4705743,16.2540957 22.3973996,16.3848146 22.2803147,16.4819271 C22.1632165,16.5790395 22.0607773,16.6350638 21.9729569,16.65 L14.1793768,16.65 C13.7988443,16.65 13.5793068,16.4632432 13.5207644,16.0897568 C13.4768542,15.8955456 13.3341629,15.0626353 13.0926636,13.5910669 C12.8511778,12.1194985 12.7084731,11.2716657 12.6645763,11.0475684 C12.5913882,10.8832295 12.580414,10.7188906 12.6316403,10.5545517 C12.6828666,10.3902128 12.7194606,10.293114 12.741409,10.263228 C12.7633574,10.2333419 12.8402036,10.1586474 12.9719207,10.0391307 L13.4109956,9.59093617 L13.4109956,10.4873252 L22.5876592,10.4873252 L22.5876592,9.59093617 L22.9169654,9.92708207 Z M20.1947088,12.9523951 L20.1947088,11.8319088 L19.4263276,11.8319088 L19.4263276,12.7282979 L16.5723405,12.7282979 L16.5723405,11.8319088 L15.8259078,11.8319088 L15.8259078,12.9523951 C15.8259078,13.3258815 16.0015351,13.5126383 16.3528031,13.5126383 L19.6458651,13.5126383 C19.8068602,13.5126383 19.9349192,13.4678161 20.0300557,13.3781854 C20.1251922,13.288541 20.172747,13.1988967 20.172747,13.109266 L20.1947088,12.9523951 L20.1947088,12.9523951 Z" id="archive" fill="#8199AE"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C2CDD7"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M9.03581543,24.1529785 C8.87497559,24.1529785 8.73984375,24.18396 8.63041992,24.2459229 C8.52099609,24.3078857 8.43266602,24.3889648 8.36542969,24.4891602 C8.29819336,24.5893555 8.23491211,24.7152588 8.17558594,24.8668701 C8.13603516,24.9679443 8.10029297,25.0593506 8.06835937,25.1410889 L8.02331543,25.2564453 L6.54016113,29.0216797 C6.4795166,29.1719727 6.43601074,29.2893066 6.40964355,29.3736816 C6.38327637,29.4580566 6.37009277,29.5397949 6.37009277,29.6188965 C6.37009277,29.7560059 6.42678223,29.8786133 6.54016113,29.9867188 C6.65354004,30.0948242 6.78405762,30.148877 6.93171387,30.148877 C7.1057373,30.148877 7.23098145,30.0981201 7.30744629,29.9966064 C7.37116699,29.9120117 7.44587402,29.7619568 7.53156738,29.5464417 L7.58430176,29.4092773 L7.86115723,28.6657227 L10.2183838,28.6657227 L10.4952393,29.393457 C10.5321533,29.4831055 10.5763184,29.5865967 10.6277344,29.7039307 C10.6791504,29.8212646 10.7272705,29.9082764 10.7720947,29.9649658 C10.8169189,30.0216553 10.87229,30.0664795 10.938208,30.0994385 C11.004126,30.1323975 11.0832275,30.148877 11.1755127,30.148877 C11.3337158,30.148877 11.4688477,30.0928467 11.5809082,29.9807861 C11.6929687,29.8687256 11.748999,29.745459 11.748999,29.6109863 C11.748999,29.5033203 11.7078003,29.3416382 11.6254028,29.1259399 L11.5710205,28.9900391 L10.0562256,25.2485352 C9.98503418,25.0613281 9.92636719,24.9116943 9.88022461,24.7996338 C9.83408203,24.6875732 9.77739258,24.5827637 9.71015625,24.4852051 C9.64291992,24.3876465 9.55458984,24.3078857 9.44516602,24.2459229 C9.33574219,24.18396 9.19929199,24.1529785 9.03581543,24.1529785 Z M15.2334229,24.2518555 L13.3231201,24.2518555 C13.0699951,24.2518555 12.8867432,24.3085449 12.7733643,24.4219238 C12.6788818,24.5164063 12.6237671,24.6594116 12.60802,24.8509399 L12.6032959,24.9716797 L12.6032959,29.4330078 C12.6032959,29.6676758 12.6547119,29.8456543 12.7575439,29.9669434 C12.860376,30.0882324 13.0053955,30.148877 13.1926025,30.148877 C13.366626,30.148877 13.5076904,30.0908691 13.6157959,29.9748535 C13.7022803,29.882041 13.7541709,29.7478848 13.7714678,29.5723848 L13.7779541,29.4330078 L13.7779541,27.570166 L14.1892822,27.570166 C14.397583,27.570166 14.5702881,27.5978516 14.7073975,27.6532227 C14.8445068,27.7085938 14.9796387,27.8114258 15.112793,27.9617188 C15.2193164,28.0819531 15.3372305,28.2460625 15.4665352,28.4540469 L15.5656494,28.6182617 L16.0363037,29.4013672 C16.1470459,29.5859375 16.2347168,29.7256836 16.2993164,29.8206055 C16.363916,29.9155273 16.44104,29.9939697 16.5306885,30.0559326 C16.6203369,30.1178955 16.7310791,30.148877 16.862915,30.148877 C16.9736572,30.148877 17.0712158,30.127124 17.1555908,30.0836182 C17.2399658,30.0401123 17.306543,29.9807861 17.3553223,29.9056396 C17.4041016,29.8304932 17.4284912,29.7546875 17.4284912,29.6782227 C17.4284912,29.6043945 17.3869629,29.4692627 17.3039062,29.2728271 C17.2208496,29.0763916 17.104834,28.8562256 16.9558594,28.6123291 C16.8068848,28.3684326 16.6374756,28.1383789 16.4476318,27.922168 C16.2577881,27.705957 16.0587158,27.5424805 15.850415,27.4317383 C16.3461182,27.3236328 16.715918,27.1403809 16.9598145,26.8819824 C17.2037109,26.623584 17.3256592,26.2781738 17.3256592,25.845752 C17.3256592,25.6348145 17.2894043,25.435083 17.2168945,25.2465576 C17.1443848,25.0580322 17.0382568,24.8925781 16.8985107,24.7501953 C16.7587646,24.6078125 16.5979248,24.4983887 16.4159912,24.4219238 C16.2656982,24.3533691 16.0969482,24.3078857 15.9097412,24.2854736 C15.7693359,24.2686646 15.6074249,24.2581589 15.4240082,24.2539566 L15.2334229,24.2518555 Z M20.8654541,24.1529785 C20.4804932,24.1529785 20.1179443,24.2215332 19.7778076,24.3586426 C19.4376709,24.495752 19.14104,24.6954834 18.887915,24.9578369 C18.63479,25.2201904 18.440332,25.5359375 18.304541,25.9050781 C18.16875,26.2742188 18.1008545,26.6921387 18.1008545,27.1588379 C18.1008545,27.4462402 18.1278809,27.7178223 18.1819336,27.973584 C18.2359863,28.2293457 18.3170654,28.4699463 18.4251709,28.6953857 C18.5332764,28.9208252 18.6637939,29.1245117 18.8167236,29.3064453 C18.9881104,29.5068359 19.1759766,29.668335 19.3803223,29.7909424 C19.584668,29.9135498 19.812085,30.0038574 20.0625732,30.0618652 C20.3130615,30.119873 20.5912354,30.148877 20.8970947,30.148877 C21.3005127,30.148877 21.653833,30.0842773 21.9570557,29.9550781 C22.2602783,29.8258789 22.5094482,29.6577881 22.7045654,29.4508057 C22.8996826,29.2438232 23.0427246,29.0328857 23.1336914,28.8179932 C23.2246582,28.6031006 23.2701416,28.4033691 23.2701416,28.2187988 C23.2701416,28.0737793 23.2200439,27.9524902 23.1198486,27.8549316 C23.0196533,27.757373 22.8996826,27.7085938 22.7599365,27.7085938 C22.59646,27.7085938 22.4778076,27.7567139 22.4039795,27.8529541 C22.3301514,27.9491943 22.2681885,28.0737793 22.2180908,28.226709 C22.0994385,28.5510254 21.9234375,28.7968994 21.6900879,28.9643311 C21.4567383,29.1317627 21.1713135,29.2154785 20.8338135,29.2154785 C20.5226807,29.2154785 20.2510986,29.1416504 20.0190674,28.9939941 C19.7870361,28.8463379 19.6077393,28.622876 19.4811768,28.3236084 C19.3546143,28.0243408 19.291333,27.6492676 19.291333,27.1983887 C19.291333,26.5233887 19.434375,26.0026367 19.720459,25.6361328 C20.006543,25.2696289 20.3908447,25.086377 20.8733643,25.086377 C21.1765869,25.086377 21.4316895,25.1575684 21.6386719,25.2999512 C21.8456543,25.442334 22.0256104,25.6572266 22.17854,25.9446289 C22.2708252,26.1186523 22.354541,26.2412598 22.4296875,26.3124512 C22.504834,26.3836426 22.6175537,26.4192383 22.7678467,26.4192383 C22.9023193,26.4192383 23.0170166,26.3678223 23.1119385,26.2649902 C23.2068604,26.1621582 23.2543213,26.0435059 23.2543213,25.9090332 C23.2543213,25.6638184 23.1567627,25.4034424 22.9616455,25.1279053 C22.7665283,24.8523682 22.4857178,24.6209961 22.1192139,24.4337891 C21.75271,24.246582 21.33479,24.1529785 20.8654541,24.1529785 Z M28.6253174,24.1529785 C28.4512939,24.1529785 28.3108887,24.2123047 28.2041016,24.330957 C28.1186719,24.4258789 28.0674141,24.5587695 28.0503281,24.7296289 L28.0439209,24.8648926 L28.0439209,26.5260254 L25.5087158,26.5260254 L25.5087158,24.8648926 C25.5087158,24.6249512 25.4540039,24.4463135 25.3445801,24.3289795 C25.2351562,24.2116455 25.0921143,24.1529785 24.9154541,24.1529785 C24.7361572,24.1529785 24.5944336,24.2123047 24.4902832,24.330957 C24.4069629,24.4258789 24.3569707,24.5587695 24.3403066,24.7296289 L24.3340576,24.8648926 L24.3340576,29.4330078 C24.3340576,29.6703125 24.386792,29.8489502 24.4922607,29.9689209 C24.5977295,30.0888916 24.7387939,30.148877 24.9154541,30.148877 C25.0894775,30.148877 25.2318604,30.0895508 25.3426025,29.9708984 C25.4311963,29.8759766 25.4843525,29.7422422 25.5020713,29.5696953 L25.5087158,29.4330078 L25.5087158,27.4831543 L28.0439209,27.4831543 L28.0439209,29.4330078 C28.0439209,29.6703125 28.0966553,29.8489502 28.202124,29.9689209 C28.3075928,30.0888916 28.4486572,30.148877 28.6253174,30.148877 C28.7993408,30.148877 28.9417236,30.0895508 29.0524658,29.9708984 C29.1410596,29.8759766 29.1942158,29.7422422 29.2119346,29.5696953 L29.2185791,29.4330078 L29.2185791,24.8648926 C29.2185791,24.6249512 29.1638672,24.4463135 29.0544434,24.3289795 C28.9450195,24.2116455 28.8019775,24.1529785 28.6253174,24.1529785 Z M9.02790527,25.390918 L9.90197754,27.7837402 L8.16965332,27.7837402 L9.02790527,25.390918 Z M14.8616455,25.1338379 C15.2650635,25.1338379 15.5353271,25.1628418 15.6724365,25.2208496 C15.8174561,25.2814941 15.930835,25.3731201 16.0125732,25.4957275 C16.0943115,25.618335 16.1351807,25.7600586 16.1351807,25.9208984 C16.1351807,26.1265625 16.0844238,26.2880615 15.9829102,26.4053955 C15.8813965,26.5227295 15.7343994,26.6057861 15.5419189,26.6545654 C15.3494385,26.7033447 15.1121338,26.7277344 14.8300049,26.7277344 L13.7779541,26.7277344 L13.7779541,25.1338379 L14.8616455,25.1338379 Z" id="形状结合" fill="#8199AE"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),k6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_att</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-636.000000, -5275.000000)">\r
            <g id="icon_att" transform="translate(636.000000, 5275.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#E6EBEF"></path>\r
                <path d="M21.5821505,12.1466165 C22.1746692,11.4969925 22.1746692,10.5225564 21.5821505,9.87293233 C20.9896318,9.22330827 19.9661904,9.27744361 19.3736717,9.87293233 L14.4180607,14.8533835 C13.825542,15.4488722 13.825542,16.4774436 14.4180607,17.0729323 C15.0105794,17.6684211 16.0340208,17.7225564 16.6265395,17.0729323 L19.6968637,14.0413534 C19.8584597,13.8789474 19.8584597,13.6082707 19.6968637,13.4458647 C19.5352677,13.2834586 19.2120757,13.2293233 19.0504796,13.4458647 L17.1113275,15.3406015 C16.8420008,15.6112782 16.4649435,15.6112782 16.1956168,15.3406015 C15.9262901,15.0699248 15.9262901,14.6368421 16.1956168,14.3661654 L18.0809036,12.4714286 C18.781153,11.7676692 19.912325,11.7676692 20.6125744,12.4714286 C21.3128238,13.175188 21.3128238,14.3120301 20.6125744,15.0157895 L17.5961156,18.0473684 C16.4649435,19.1842105 14.633522,19.1842105 13.4484846,18.0473684 C12.3173126,16.9105263 12.3173126,15.0699248 13.4484846,13.9330827 L18.4040956,8.95263158 C19.5352677,7.81578947 21.4205545,7.81578947 22.5517265,8.95263158 C23.6828986,10.0894737 23.6828986,11.9300752 22.5517265,13.0669173 L22.0669385,13.5541353 C22.0130732,13.0669173 21.8514772,12.5796992 21.5282851,12.1466165 L21.5821505,12.1466165" id="Fill-4" fill="#8199AE"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C2CDD7"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M13.2637939,24.1529785 C13.1029541,24.1529785 12.9678223,24.18396 12.8583984,24.2459229 C12.7489746,24.3078857 12.6606445,24.3889648 12.5934082,24.4891602 C12.5261719,24.5893555 12.4628906,24.7152588 12.4035645,24.8668701 C12.3640137,24.9679443 12.3282715,25.0593506 12.2963379,25.1410889 L12.2512939,25.2564453 L10.7681396,29.0216797 C10.7074951,29.1719727 10.6639893,29.2893066 10.6376221,29.3736816 C10.6112549,29.4580566 10.5980713,29.5397949 10.5980713,29.6188965 C10.5980713,29.7560059 10.6547607,29.8786133 10.7681396,29.9867188 C10.8815186,30.0948242 11.0120361,30.148877 11.1596924,30.148877 C11.3337158,30.148877 11.45896,30.0981201 11.5354248,29.9966064 C11.5991455,29.9120117 11.6738525,29.7619568 11.7595459,29.5464417 L11.8122803,29.4092773 L12.0891357,28.6657227 L14.4463623,28.6657227 L14.7232178,29.393457 C14.7601318,29.4831055 14.8042969,29.5865967 14.8557129,29.7039307 C14.9071289,29.8212646 14.955249,29.9082764 15.0000732,29.9649658 C15.0448975,30.0216553 15.1002686,30.0664795 15.1661865,30.0994385 C15.2321045,30.1323975 15.3112061,30.148877 15.4034912,30.148877 C15.5616943,30.148877 15.6968262,30.0928467 15.8088867,29.9807861 C15.9209473,29.8687256 15.9769775,29.745459 15.9769775,29.6109863 C15.9769775,29.5033203 15.9357788,29.3416382 15.8533813,29.1259399 L15.798999,28.9900391 L14.2842041,25.2485352 C14.2130127,25.0613281 14.1543457,24.9116943 14.1082031,24.7996338 C14.0620605,24.6875732 14.0053711,24.5827637 13.9381348,24.4852051 C13.8708984,24.3876465 13.7825684,24.3078857 13.6731445,24.2459229 C13.5637207,24.18396 13.4272705,24.1529785 13.2637939,24.1529785 Z M19.8925049,24.2518555 L16.1589111,24.2518555 C15.9664307,24.2518555 15.8194336,24.2953613 15.7179199,24.382373 C15.6164063,24.4693848 15.5656494,24.5880371 15.5656494,24.7383301 C15.5656494,24.8833496 15.6144287,25.0000244 15.7119873,25.0883545 C15.7900342,25.1590186 15.9009873,25.201417 16.0448467,25.2155498 L16.1589111,25.2208496 L17.4403564,25.2208496 L17.4403564,29.4330078 C17.4403564,29.6729492 17.4950684,29.8522461 17.6044922,29.9708984 C17.713916,30.0895508 17.8556396,30.148877 18.0296631,30.148877 C18.2010498,30.148877 18.3407959,30.09021 18.4489014,29.972876 C18.5353857,29.8790088 18.5872764,29.7450635 18.6045732,29.57104 L18.6110596,29.4330078 L18.6110596,25.2208496 L19.8925049,25.2208496 C20.0928955,25.2208496 20.2431885,25.1766846 20.3433838,25.0883545 C20.4435791,25.0000244 20.4936768,24.8833496 20.4936768,24.7383301 C20.4936768,24.5933105 20.4442383,24.4759766 20.3453613,24.3863281 C20.2662598,24.3146094 20.1538301,24.2715781 20.0080723,24.2572344 L19.8925049,24.2518555 Z M24.9550049,24.2518555 L21.2214111,24.2518555 C21.0289307,24.2518555 20.8819336,24.2953613 20.7804199,24.382373 C20.6789063,24.4693848 20.6281494,24.5880371 20.6281494,24.7383301 C20.6281494,24.8833496 20.6769287,25.0000244 20.7744873,25.0883545 C20.8525342,25.1590186 20.9634873,25.201417 21.1073467,25.2155498 L21.2214111,25.2208496 L22.5028564,25.2208496 L22.5028564,29.4330078 C22.5028564,29.6729492 22.5575684,29.8522461 22.6669922,29.9708984 C22.776416,30.0895508 22.9181396,30.148877 23.0921631,30.148877 C23.2635498,30.148877 23.4032959,30.09021 23.5114014,29.972876 C23.5978857,29.8790088 23.6497764,29.7450635 23.6670732,29.57104 L23.6735596,29.4330078 L23.6735596,25.2208496 L24.9550049,25.2208496 C25.1553955,25.2208496 25.3056885,25.1766846 25.4058838,25.0883545 C25.5060791,25.0000244 25.5561768,24.8833496 25.5561768,24.7383301 C25.5561768,24.5933105 25.5067383,24.4759766 25.4078613,24.3863281 C25.3287598,24.3146094 25.2163301,24.2715781 25.0705723,24.2572344 L24.9550049,24.2518555 Z M13.2558838,25.390918 L14.1299561,27.7837402 L12.3976318,27.7837402 L13.2558838,25.390918 Z" id="形状结合" fill="#8199AE"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),E6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_au</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-895.000000, -5275.000000)">\r
            <g id="icon_au" transform="translate(895.000000, 5275.000000)">\r
                <path d="M23.604,0 L23.6045455,6.21998174 C23.6045455,6.99128734 24.2096546,9.01596452 26.960151,9.01596452 L32.604,8.985 L32.5571138,33.0899335 C32.5571138,34.4653386 31.4404421,35.5573497 30.0474034,35.639316 L29.878966,35.6442572 L5.82814787,35.6442572 C4.35779218,35.6442572 3.15,34.5203548 3.15,33.0899335 L3.15,33.0899335 L3.15,2.55432373 C3.15,1.12390244 4.35779218,0 5.82814787,0 L5.82814787,0 L23.604,0 Z" id="Combined-Shape" fill="#FEDADA"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#FDA3A3"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M15.1429887,24.1529785 C14.9821489,24.1529785 14.847017,24.18396 14.7375932,24.2459229 C14.6281694,24.3078857 14.5398393,24.3889648 14.472603,24.4891602 C14.4053667,24.5893555 14.3420854,24.7152588 14.2827592,24.8668701 C14.2432085,24.9679443 14.2074663,25.0593506 14.1755327,25.1410889 L14.1304887,25.2564453 L12.6473344,29.0216797 C12.5866899,29.1719727 12.543184,29.2893066 12.5168169,29.3736816 C12.4904497,29.4580566 12.4772661,29.5397949 12.4772661,29.6188965 C12.4772661,29.7560059 12.5339555,29.8786133 12.6473344,29.9867188 C12.7607133,30.0948242 12.8912309,30.148877 13.0388872,30.148877 C13.2129106,30.148877 13.3381547,30.0981201 13.4146196,29.9966064 C13.4783403,29.9120117 13.5530473,29.7619568 13.6387407,29.5464417 L13.6914751,29.4092773 L13.9683305,28.6657227 L16.3255571,28.6657227 L16.6024126,29.393457 C16.6393266,29.4831055 16.6834917,29.5865967 16.7349077,29.7039307 C16.7863237,29.8212646 16.8344438,29.9082764 16.879268,29.9649658 C16.9240922,30.0216553 16.9794633,30.0664795 17.0453813,30.0994385 C17.1112993,30.1323975 17.1904008,30.148877 17.282686,30.148877 C17.4408891,30.148877 17.576021,30.0928467 17.6880815,29.9807861 C17.800142,29.8687256 17.8561723,29.745459 17.8561723,29.6109863 C17.8561723,29.5033203 17.8149736,29.3416382 17.7325761,29.1259399 L17.6781938,28.9900391 L16.1633989,25.2485352 C16.0922075,25.0613281 16.0335405,24.9116943 15.9873979,24.7996338 C15.9412553,24.6875732 15.8845659,24.5827637 15.8173295,24.4852051 C15.7500932,24.3876465 15.6617631,24.3078857 15.5523393,24.2459229 C15.4429155,24.18396 15.3064653,24.1529785 15.1429887,24.1529785 Z M22.701143,24.1529785 C22.5244829,24.1529785 22.3834184,24.2116455 22.2779497,24.3289795 C22.1935747,24.4228467 22.1429497,24.5559482 22.1260747,24.7282842 L22.1197465,24.8648926 L22.1197465,27.7402344 C22.1197465,28.2359375 22.0248247,28.6057373 21.8349809,28.8496338 C21.6451372,29.0935303 21.3208208,29.2154785 20.8620317,29.2154785 C20.5298051,29.2154785 20.2714067,29.1554932 20.0868364,29.0355225 C19.9022661,28.9155518 19.773726,28.7468018 19.7012163,28.5292725 C19.6432085,28.355249 19.6084038,28.1470537 19.5968022,27.9046865 L19.5924516,27.7165039 L19.5924516,24.8648926 C19.5924516,24.6275879 19.5390581,24.4496094 19.432271,24.330957 C19.3254838,24.2123047 19.1811235,24.1529785 18.9991899,24.1529785 C18.8251665,24.1529785 18.6847612,24.2123047 18.5779741,24.330957 C18.4925444,24.4258789 18.4412866,24.5587695 18.4242006,24.7296289 L18.4177934,24.8648926 L18.4177934,27.6532227 C18.4177934,28.090918 18.4652544,28.4673096 18.5601762,28.7823975 C18.6550981,29.0974854 18.8040727,29.3558838 19.0071001,29.5575928 C19.2101274,29.7593018 19.4724809,29.9082764 19.7941606,30.0045166 C20.1158403,30.1007568 20.4994829,30.148877 20.9450883,30.148877 C21.3195024,30.148877 21.6464555,30.1040527 21.9259477,30.0144043 C22.2054399,29.9247559 22.4506547,29.782373 22.6615922,29.5872559 C22.9068071,29.3578613 23.0735795,29.0915527 23.1619096,28.7883301 C23.2325737,28.545752 23.2749721,28.2550801 23.2891049,27.9163145 L23.2944047,27.6532227 L23.2944047,24.8648926 C23.2944047,24.6249512 23.2396928,24.4463135 23.130269,24.3289795 C23.0208452,24.2116455 22.8778032,24.1529785 22.701143,24.1529785 Z M15.1350786,25.390918 L16.0091508,27.7837402 L14.2768266,27.7837402 L15.1350786,25.390918 Z" id="形状结合" fill="#FB4747"></path>\r
                <path d="M21.1654114,8.8660911 C21.4260867,8.8660911 21.6,9.04005575 21.6,9.30104771 L21.6,11.8671988 C21.6000531,13.756212 20.2341492,15.3036 18.4707674,15.6308858 L18.4705785,16.9995009 L18.466,17.042 L19.774554,17.0429995 C20.0353192,17.0430595 20.2526435,17.2605228 20.2526435,17.5215148 C20.2526435,17.7824767 20.0353192,18 19.774554,18 L16.3406568,18 C16.0798318,18 15.8625075,17.7825367 15.8625075,17.5215148 C15.8625075,17.2605228 16.0797718,17.0429995 16.3406568,17.0429995 L17.5162494,17.0430729 C17.5149067,17.028707 17.5142199,17.0141727 17.5142199,16.9995009 L17.514095,15.6858706 C15.5456726,15.5515166 13.95,13.9088997 13.95,11.8671988 L13.95,9.30104771 C13.95,9.04011571 14.1237935,8.8660911 14.3846785,8.8660911 C14.6454437,8.8660911 14.8192971,9.04005575 14.8192971,9.30104771 L14.8192971,11.8671988 C14.8192971,13.4765292 16.1233026,14.8247478 17.774985,14.8247478 C19.4267873,14.8247478 20.7307928,13.5199979 20.7307928,11.8671988 L20.7307928,9.30104771 C20.7307928,9.04011571 20.9046163,8.8660911 21.1654114,8.8660911 Z M17.7315741,6.3 C18.7313436,6.3 19.55708,7.12644454 19.55708,8.12679377 L19.55708,11.7802314 C19.6441715,12.7806406 18.8181954,13.6070552 17.774985,13.6070552 C16.7753054,13.6070552 15.9059783,12.7806406 15.9059783,11.7802314 L15.9059783,8.12679377 C15.9059783,7.12644454 16.7318345,6.3 17.7315741,6.3 Z" id="Combined-Shape" fill="#FB4747"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),M6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_avi</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-766.000000, -5275.000000)">\r
            <g id="icon_avi" transform="translate(766.000000, 5275.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#FCECD3"></path>\r
                <path d="M22.0546307,13.608 C22.0546307,13.8327805 21.9983179,14.057561 21.8293793,14.1699512 C21.7167536,14.2823415 18.8447983,16.6425366 15.1281503,18.1598049 C15.0718374,18.216 14.9592117,18.216 14.846586,18.216 C14.7339603,18.216 14.6213346,18.1598049 14.5087089,18.1036098 C14.2834575,17.9912195 14.1708318,17.8226341 14.114519,17.5416585 C14.114519,17.4854634 13.9455804,15.6310244 13.9455804,13.608 C13.9455804,11.5849756 14.114519,9.78673171 14.114519,9.67434146 C14.1708318,9.44956098 14.2834575,9.28097561 14.5087089,9.11239024 C14.6213346,9.05619512 14.7339603,9 14.846586,9 C14.9592117,9 15.0718374,9.05619512 15.1281503,9.11239024 C18.8447983,10.5734634 21.7167536,12.9898537 21.8293793,13.1022439 C21.9983179,13.2146341 22.0546307,13.4394146 22.0546307,13.608" id="Fill-4" fill="#F29F22"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#F8CF90"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M14.1220459,24.1529785 C13.9612061,24.1529785 13.8260742,24.18396 13.7166504,24.2459229 C13.6072266,24.3078857 13.5188965,24.3889648 13.4516602,24.4891602 C13.3844238,24.5893555 13.3211426,24.7152588 13.2618164,24.8668701 C13.2222656,24.9679443 13.1865234,25.0593506 13.1545898,25.1410889 L13.1095459,25.2564453 L11.6263916,29.0216797 C11.5657471,29.1719727 11.5222412,29.2893066 11.495874,29.3736816 C11.4695068,29.4580566 11.4563232,29.5397949 11.4563232,29.6188965 C11.4563232,29.7560059 11.5130127,29.8786133 11.6263916,29.9867188 C11.7397705,30.0948242 11.8702881,30.148877 12.0179443,30.148877 C12.1919678,30.148877 12.3172119,30.0981201 12.3936768,29.9966064 C12.4573975,29.9120117 12.5321045,29.7619568 12.6177979,29.5464417 L12.6705322,29.4092773 L12.9473877,28.6657227 L15.3046143,28.6657227 L15.5814697,29.393457 C15.6183838,29.4831055 15.6625488,29.5865967 15.7139648,29.7039307 C15.7653809,29.8212646 15.813501,29.9082764 15.8583252,29.9649658 C15.9031494,30.0216553 15.9585205,30.0664795 16.0244385,30.0994385 C16.0903564,30.1323975 16.169458,30.148877 16.2617432,30.148877 C16.4199463,30.148877 16.5550781,30.0928467 16.6671387,29.9807861 C16.7791992,29.8687256 16.8352295,29.745459 16.8352295,29.6109863 C16.8352295,29.5033203 16.7940308,29.3416382 16.7116333,29.1259399 L16.657251,28.9900391 L15.1424561,25.2485352 C15.0712646,25.0613281 15.0125977,24.9116943 14.9664551,24.7996338 C14.9203125,24.6875732 14.863623,24.5827637 14.7963867,24.4852051 C14.7291504,24.3876465 14.6408203,24.3078857 14.5313965,24.2459229 C14.4219727,24.18396 14.2855225,24.1529785 14.1220459,24.1529785 Z M21.4231201,24.1529785 C21.2860107,24.1529785 21.1779053,24.1852783 21.0988037,24.2498779 C21.0197021,24.3144775 20.9630127,24.3869873 20.9287354,24.4674072 C20.9013135,24.5317432 20.8629229,24.6361572 20.8135635,24.7806494 L20.7744873,24.8965332 L19.4574463,28.8120605 L18.1443604,24.9242188 C18.0573486,24.6552734 17.9723145,24.4594971 17.8892578,24.3368896 C17.8062012,24.2142822 17.6644775,24.1529785 17.4640869,24.1529785 C17.3006104,24.1529785 17.1648193,24.2083496 17.0567139,24.3190918 C16.9486084,24.429834 16.8945557,24.5524414 16.8945557,24.6869141 C16.8945557,24.7396484 16.903125,24.804248 16.9202637,24.8807129 C16.9374023,24.9571777 16.9584961,25.0283691 16.9835449,25.0942871 L17.0206238,25.1941528 L17.0206238,25.1941528 L17.0567139,25.2959961 L18.4370361,29.0533203 C18.4871338,29.1983398 18.5372314,29.3354492 18.5873291,29.4646484 C18.6374268,29.5938477 18.6960937,29.7092041 18.7633301,29.8107178 C18.8305664,29.9122314 18.9188965,29.9939697 19.0283203,30.0559326 C19.1377441,30.1178955 19.2715576,30.148877 19.4297607,30.148877 C19.5879639,30.148877 19.7217773,30.1172363 19.8312012,30.0539551 C19.940625,29.9906738 20.0282959,29.9095947 20.0942139,29.8107178 C20.1601318,29.7118408 20.2181396,29.5971436 20.2682373,29.466626 L20.3433838,29.2654114 L20.3433838,29.2654114 L20.4185303,29.0533203 L21.822583,25.2643555 C21.8463135,25.1958008 21.8700439,25.1292236 21.8937744,25.064624 C21.9175049,25.0000244 21.9372803,24.9341064 21.9531006,24.8668701 C21.9689209,24.7996338 21.9768311,24.7370117 21.9768311,24.6790039 C21.9768311,24.5946289 21.9524414,24.5109131 21.9036621,24.4278564 C21.8548828,24.3447998 21.7876465,24.2782227 21.7019531,24.228125 C21.6162598,24.1780273 21.5233154,24.1529785 21.4231201,24.1529785 Z M23.4876709,24.1529785 C23.3162842,24.1529785 23.1765381,24.2123047 23.0684326,24.330957 C22.9819482,24.4258789 22.9300576,24.5587695 22.9127607,24.7296289 L22.9062744,24.8648926 L22.9062744,29.4330078 C22.9062744,29.6703125 22.9609863,29.8489502 23.0704102,29.9689209 C23.179834,30.0888916 23.3189209,30.148877 23.4876709,30.148877 C23.6643311,30.148877 23.807373,30.0895508 23.9167969,29.9708984 C24.0043359,29.8759766 24.0568594,29.7422422 24.0743672,29.5696953 L24.0809326,29.4330078 L24.0809326,24.8648926 C24.0809326,24.6249512 24.0262207,24.4463135 23.9167969,24.3289795 C23.807373,24.2116455 23.6643311,24.1529785 23.4876709,24.1529785 Z M14.1141357,25.390918 L14.988208,27.7837402 L13.2558838,27.7837402 L14.1141357,25.390918 Z" id="形状结合" fill="#F29F22"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),S6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_cad</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-311.000000, -5275.000000)">\r
            <g id="icon_cad" transform="translate(311.000000, 5275.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#DAEAF9"></path>\r
                <path d="M22.7295324,13.7023301 L22.7295324,12.7055171 L21.7327358,12.7055171 L21.7327358,0.0295980512 L21.5515135,0.0295980512 L21.5515135,12.7055171 L20.5547005,12.7055171 L20.5547005,13.7023301 L19.4966359,13.7023301 L18.3545447,10.3974143 C18.2415906,10.0705545 17.8706838,9.80558222 17.5913443,9.80558222 L17.5913443,9.80558222 L17.401455,9.82748087 C17.05416,9.86753208 16.6901453,10.1734542 16.5934312,10.4941141 L15.6258028,13.7023301 L3.27272727,13.7023301 L3.27272727,13.8835359 L15.5634989,13.8835359 L14.9589042,15.6450705 C14.8462067,15.9734237 15.0264567,16.2234971 15.3788071,16.2025987 L15.4226217,16.2 L15.4226217,16.2 C15.6711498,16.2 15.9863217,15.9394844 16.1231243,15.6260349 L16.5253698,14.7043886 L18.602117,14.7043886 L18.933637,15.6504418 C19.0476603,15.9758285 19.4206643,16.2061885 19.4726217,16.2 L19.4726217,16.2 L19.7380359,16.2116691 C20.088981,16.2270985 20.2800648,15.9693353 20.1679897,15.6450705 L19.5591536,13.8835359 L20.5547005,13.8835359 L20.5547005,14.880349 L21.5515135,14.880349 L21.5515135,21.15 L21.7327358,21.15 L21.7327358,14.880349 L22.7295324,14.880349 L22.7295324,13.8835359 L32.7150055,13.8835359 L32.7150055,13.7023301 L22.7295324,13.7023301 Z M16.7697346,13.6776979 L17.4956597,11.3300627 L18.2967309,13.6776979 L16.7697346,13.6776979 Z M20.7359228,13.7023301 L21.5515135,13.7023301 L21.5515135,12.8867393 L20.7359228,12.8867393 L20.7359228,13.7023301 Z M20.7359228,14.6991267 L21.5515135,14.6991267 L21.5515135,13.8835359 L20.7359228,13.8835359 L20.7359228,14.6991267 Z M21.7327358,14.6991267 L22.5483265,14.6991267 L22.5483265,13.8835359 L21.7327358,13.8835359 L21.7327358,14.6991267 Z M21.7327358,13.7023301 L22.5483265,13.7023301 L22.5483265,12.8867393 L21.7327358,12.8867393 L21.7327358,13.7023301 Z" id="cad" fill="#1A85CD"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#8CC2E6"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M12.2592041,24.1529785 C11.8742432,24.1529785 11.5116943,24.2215332 11.1715576,24.3586426 C10.8314209,24.495752 10.53479,24.6954834 10.281665,24.9578369 C10.02854,25.2201904 9.83408203,25.5359375 9.69829102,25.9050781 C9.5625,26.2742188 9.49460449,26.6921387 9.49460449,27.1588379 C9.49460449,27.4462402 9.52163086,27.7178223 9.57568359,27.973584 C9.62973633,28.2293457 9.71081543,28.4699463 9.8189209,28.6953857 C9.92702637,28.9208252 10.0575439,29.1245117 10.2104736,29.3064453 C10.3818604,29.5068359 10.5697266,29.668335 10.7740723,29.7909424 C10.978418,29.9135498 11.205835,30.0038574 11.4563232,30.0618652 C11.7068115,30.119873 11.9849854,30.148877 12.2908447,30.148877 C12.6942627,30.148877 13.047583,30.0842773 13.3508057,29.9550781 C13.6540283,29.8258789 13.9031982,29.6577881 14.0983154,29.4508057 C14.2934326,29.2438232 14.4364746,29.0328857 14.5274414,28.8179932 C14.6184082,28.6031006 14.6638916,28.4033691 14.6638916,28.2187988 C14.6638916,28.0737793 14.6137939,27.9524902 14.5135986,27.8549316 C14.4134033,27.757373 14.2934326,27.7085938 14.1536865,27.7085938 C13.99021,27.7085938 13.8715576,27.7567139 13.7977295,27.8529541 C13.7239014,27.9491943 13.6619385,28.0737793 13.6118408,28.226709 C13.4931885,28.5510254 13.3171875,28.7968994 13.0838379,28.9643311 C12.8504883,29.1317627 12.5650635,29.2154785 12.2275635,29.2154785 C11.9164307,29.2154785 11.6448486,29.1416504 11.4128174,28.9939941 C11.1807861,28.8463379 11.0014893,28.622876 10.8749268,28.3236084 C10.7483643,28.0243408 10.685083,27.6492676 10.685083,27.1983887 C10.685083,26.5233887 10.828125,26.0026367 11.114209,25.6361328 C11.400293,25.2696289 11.7845947,25.086377 12.2671143,25.086377 C12.5703369,25.086377 12.8254395,25.1575684 13.0324219,25.2999512 C13.2394043,25.442334 13.4193604,25.6572266 13.57229,25.9446289 C13.6645752,26.1186523 13.748291,26.2412598 13.8234375,26.3124512 C13.898584,26.3836426 14.0113037,26.4192383 14.1615967,26.4192383 C14.2960693,26.4192383 14.4107666,26.3678223 14.5056885,26.2649902 C14.6006104,26.1621582 14.6480713,26.0435059 14.6480713,25.9090332 C14.6480713,25.6638184 14.5505127,25.4034424 14.3553955,25.1279053 C14.1602783,24.8523682 13.8794678,24.6209961 13.5129639,24.4337891 C13.14646,24.246582 12.72854,24.1529785 12.2592041,24.1529785 Z M17.9703369,24.1529785 C17.8094971,24.1529785 17.6743652,24.18396 17.5649414,24.2459229 C17.4555176,24.3078857 17.3671875,24.3889648 17.2999512,24.4891602 C17.2327148,24.5893555 17.1694336,24.7152588 17.1101074,24.8668701 C17.0705566,24.9679443 17.0348145,25.0593506 17.0028809,25.1410889 L16.9578369,25.2564453 L15.4746826,29.0216797 C15.4140381,29.1719727 15.3705322,29.2893066 15.344165,29.3736816 C15.3177979,29.4580566 15.3046143,29.5397949 15.3046143,29.6188965 C15.3046143,29.7560059 15.3613037,29.8786133 15.4746826,29.9867188 C15.5880615,30.0948242 15.7185791,30.148877 15.8662354,30.148877 C16.0402588,30.148877 16.1655029,30.0981201 16.2419678,29.9966064 C16.3056885,29.9120117 16.3803955,29.7619568 16.4660889,29.5464417 L16.5188232,29.4092773 L16.7956787,28.6657227 L19.1529053,28.6657227 L19.4297607,29.393457 C19.4666748,29.4831055 19.5108398,29.5865967 19.5622559,29.7039307 C19.6136719,29.8212646 19.661792,29.9082764 19.7066162,29.9649658 C19.7514404,30.0216553 19.8068115,30.0664795 19.8727295,30.0994385 C19.9386475,30.1323975 20.017749,30.148877 20.1100342,30.148877 C20.2682373,30.148877 20.4033691,30.0928467 20.5154297,29.9807861 C20.6274902,29.8687256 20.6835205,29.745459 20.6835205,29.6109863 C20.6835205,29.5033203 20.6423218,29.3416382 20.5599243,29.1259399 L20.505542,28.9900391 L18.9907471,25.2485352 C18.9195557,25.0613281 18.8608887,24.9116943 18.8147461,24.7996338 C18.7686035,24.6875732 18.7119141,24.5827637 18.6446777,24.4852051 C18.5774414,24.3876465 18.4891113,24.3078857 18.3796875,24.2459229 C18.2702637,24.18396 18.1338135,24.1529785 17.9703369,24.1529785 Z M23.8040771,24.2518555 L22.2734619,24.2518555 C22.0203369,24.2518555 21.837085,24.3085449 21.7237061,24.4219238 C21.6292236,24.5164063 21.5741089,24.6594116 21.5583618,24.8509399 L21.5536377,24.9716797 L21.5536377,29.2233887 C21.5536377,29.4105957 21.5701172,29.5628662 21.6030762,29.6802002 C21.6360352,29.7975342 21.7065674,29.888501 21.8146729,29.9531006 C21.9011572,30.0047803 22.0213916,30.0357881 22.175376,30.046124 L22.2971924,30.05 L23.8278076,30.05 C24.0941162,30.05 24.3333984,30.0328613 24.5456543,29.998584 C24.7579102,29.9643066 24.9563232,29.9049805 25.1408936,29.8206055 C25.3254639,29.7362305 25.4955322,29.6241699 25.6510986,29.4844238 C25.8488525,29.3024902 26.0110107,29.096167 26.1375732,28.8654541 C26.2641357,28.6347412 26.3577393,28.3756836 26.4183838,28.0882813 C26.4790283,27.8008789 26.5093506,27.4831543 26.5093506,27.1351074 C26.5093506,26.0804199 26.2074463,25.2946777 25.6036377,24.7778809 C25.3716064,24.5748535 25.113208,24.4364258 24.8284424,24.3625977 C24.6006299,24.3035352 24.3365361,24.2680977 24.0361611,24.2562852 L23.8040771,24.2518555 Z M23.5034912,25.1812988 C23.862085,25.1812988 24.1679443,25.2201904 24.4210693,25.2979736 C24.6741943,25.3757568 24.8871094,25.5570313 25.0598145,25.8417969 C25.2325195,26.1265625 25.3188721,26.5550293 25.3188721,27.1271973 C25.3188721,27.9393066 25.1224365,28.5114746 24.7295654,28.8437012 C24.6425537,28.920166 24.5397217,28.9794922 24.4210693,29.0216797 C24.302417,29.0638672 24.1877197,29.0902344 24.0769775,29.1007813 C23.9662354,29.1113281 23.8133057,29.1166016 23.6181885,29.1166016 L22.7282959,29.1166016 L22.7282959,25.1812988 L23.5034912,25.1812988 Z M17.9624268,25.390918 L18.836499,27.7837402 L17.1041748,27.7837402 L17.9624268,25.390918 Z" id="形状结合" fill="#1A85CD"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),A6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_cdr</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-441.000000, -5275.000000)">\r
            <g id="icon_cdr" transform="translate(441.000000, 5275.000000)">\r
                <path d="M23.604,0 L23.6045455,6.21998174 C23.6045455,6.99128734 24.2096546,9.01596452 26.960151,9.01596452 L32.604,8.985 L32.5571138,33.0899335 C32.5571138,34.4653386 31.4404421,35.5573497 30.0474034,35.639316 L29.878966,35.6442572 L5.82814787,35.6442572 C4.35779218,35.6442572 3.15,34.5203548 3.15,33.0899335 L3.15,33.0899335 L3.15,2.55432373 C3.15,1.12390244 4.35779218,0 5.82814787,0 L5.82814787,0 L23.604,0 Z" id="Combined-Shape" fill="#E4F2D6"></path>\r
                <path d="M19.7986061,11.5560025 L22.6013534,7.50800332 C22.7369619,7.68398297 22.8273783,7.90398089 22.8273783,8.12397882 L22.8273783,9.39997923 L20.3410723,12.9199772 L19.7986061,11.5560025 Z M15.0520509,15.9559921 L19.1205313,14.0199979 L19.8438303,13.5359838 C19.8438303,13.5359838 20.2506879,10.5880054 16.7698658,11.5119842 C16.7698658,11.5119842 16.2725918,11.7319821 15.9109584,10.3679763 C15.9109584,10.3679763 15.4136844,8.69599834 14.2383356,8.95998339 L13.334236,14.7679971 C13.2890438,14.8119842 14.5547769,14.8999896 15.0520509,15.9559921 Z M16.6794495,10.9399958 L19.3917804,10.8519904 L21.9232787,7.2 L17.8999904,7.2 L16.0917591,9.57599003 L16.6794495,10.9399958 Z M15.5492929,8.95998339 L16.815058,7.2 L15.323268,7.2 L14.5547769,8.21198422 L15.5492929,8.95998339 Z M14.7356096,16.1320029 L12.9273783,17.1 C13.0177947,16.4400062 13.108211,15.8239996 13.1986274,15.1640058 C13.8767021,15.2079929 14.3739762,15.471978 14.7356096,16.1320029 L14.7356096,16.1320029 Z" id="Fill-3" fill="#77BC31"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#BBDD98"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M12.2592041,24.1529785 C11.8742432,24.1529785 11.5116943,24.2215332 11.1715576,24.3586426 C10.8314209,24.495752 10.53479,24.6954834 10.281665,24.9578369 C10.02854,25.2201904 9.83408203,25.5359375 9.69829102,25.9050781 C9.5625,26.2742188 9.49460449,26.6921387 9.49460449,27.1588379 C9.49460449,27.4462402 9.52163086,27.7178223 9.57568359,27.973584 C9.62973633,28.2293457 9.71081543,28.4699463 9.8189209,28.6953857 C9.92702637,28.9208252 10.0575439,29.1245117 10.2104736,29.3064453 C10.3818604,29.5068359 10.5697266,29.668335 10.7740723,29.7909424 C10.978418,29.9135498 11.205835,30.0038574 11.4563232,30.0618652 C11.7068115,30.119873 11.9849854,30.148877 12.2908447,30.148877 C12.6942627,30.148877 13.047583,30.0842773 13.3508057,29.9550781 C13.6540283,29.8258789 13.9031982,29.6577881 14.0983154,29.4508057 C14.2934326,29.2438232 14.4364746,29.0328857 14.5274414,28.8179932 C14.6184082,28.6031006 14.6638916,28.4033691 14.6638916,28.2187988 C14.6638916,28.0737793 14.6137939,27.9524902 14.5135986,27.8549316 C14.4134033,27.757373 14.2934326,27.7085938 14.1536865,27.7085938 C13.99021,27.7085938 13.8715576,27.7567139 13.7977295,27.8529541 C13.7239014,27.9491943 13.6619385,28.0737793 13.6118408,28.226709 C13.4931885,28.5510254 13.3171875,28.7968994 13.0838379,28.9643311 C12.8504883,29.1317627 12.5650635,29.2154785 12.2275635,29.2154785 C11.9164307,29.2154785 11.6448486,29.1416504 11.4128174,28.9939941 C11.1807861,28.8463379 11.0014893,28.622876 10.8749268,28.3236084 C10.7483643,28.0243408 10.685083,27.6492676 10.685083,27.1983887 C10.685083,26.5233887 10.828125,26.0026367 11.114209,25.6361328 C11.400293,25.2696289 11.7845947,25.086377 12.2671143,25.086377 C12.5703369,25.086377 12.8254395,25.1575684 13.0324219,25.2999512 C13.2394043,25.442334 13.4193604,25.6572266 13.57229,25.9446289 C13.6645752,26.1186523 13.748291,26.2412598 13.8234375,26.3124512 C13.898584,26.3836426 14.0113037,26.4192383 14.1615967,26.4192383 C14.2960693,26.4192383 14.4107666,26.3678223 14.5056885,26.2649902 C14.6006104,26.1621582 14.6480713,26.0435059 14.6480713,25.9090332 C14.6480713,25.6638184 14.5505127,25.4034424 14.3553955,25.1279053 C14.1602783,24.8523682 13.8794678,24.6209961 13.5129639,24.4337891 C13.14646,24.246582 12.72854,24.1529785 12.2592041,24.1529785 Z M24.3380127,24.2518555 L22.42771,24.2518555 C22.174585,24.2518555 21.991333,24.3085449 21.8779541,24.4219238 C21.7834717,24.5164063 21.7283569,24.6594116 21.7126099,24.8509399 L21.7078857,24.9716797 L21.7078857,29.4330078 C21.7078857,29.6676758 21.7593018,29.8456543 21.8621338,29.9669434 C21.9649658,30.0882324 22.1099854,30.148877 22.2971924,30.148877 C22.4712158,30.148877 22.6122803,30.0908691 22.7203857,29.9748535 C22.8068701,29.882041 22.8587607,29.7478848 22.8760576,29.5723848 L22.8825439,29.4330078 L22.8825439,27.570166 L23.2938721,27.570166 C23.5021729,27.570166 23.6748779,27.5978516 23.8119873,27.6532227 C23.9490967,27.7085938 24.0842285,27.8114258 24.2173828,27.9617188 C24.3239063,28.0819531 24.4418203,28.2460625 24.571125,28.4540469 L24.6702393,28.6182617 L25.1408936,29.4013672 C25.2516357,29.5859375 25.3393066,29.7256836 25.4039063,29.8206055 C25.4685059,29.9155273 25.5456299,29.9939697 25.6352783,30.0559326 C25.7249268,30.1178955 25.8356689,30.148877 25.9675049,30.148877 C26.0782471,30.148877 26.1758057,30.127124 26.2601807,30.0836182 C26.3445557,30.0401123 26.4111328,29.9807861 26.4599121,29.9056396 C26.5086914,29.8304932 26.5330811,29.7546875 26.5330811,29.6782227 C26.5330811,29.6043945 26.4915527,29.4692627 26.4084961,29.2728271 C26.3254395,29.0763916 26.2094238,28.8562256 26.0604492,28.6123291 C25.9114746,28.3684326 25.7420654,28.1383789 25.5522217,27.922168 C25.3623779,27.705957 25.1633057,27.5424805 24.9550049,27.4317383 C25.450708,27.3236328 25.8205078,27.1403809 26.0644043,26.8819824 C26.3083008,26.623584 26.430249,26.2781738 26.430249,25.845752 C26.430249,25.6348145 26.3939941,25.435083 26.3214844,25.2465576 C26.2489746,25.0580322 26.1428467,24.8925781 26.0031006,24.7501953 C25.8633545,24.6078125 25.7025146,24.4983887 25.5205811,24.4219238 C25.3702881,24.3533691 25.2015381,24.3078857 25.0143311,24.2854736 C24.8739258,24.2686646 24.7120148,24.2581589 24.528598,24.2539566 L24.3380127,24.2518555 Z M17.9782471,24.2518555 L16.4476318,24.2518555 C16.1945068,24.2518555 16.0112549,24.3085449 15.897876,24.4219238 C15.8033936,24.5164063 15.7482788,24.6594116 15.7325317,24.8509399 L15.7278076,24.9716797 L15.7278076,29.2233887 C15.7278076,29.4105957 15.7442871,29.5628662 15.7772461,29.6802002 C15.8102051,29.7975342 15.8807373,29.888501 15.9888428,29.9531006 C16.0753271,30.0047803 16.1955615,30.0357881 16.3495459,30.046124 L16.4713623,30.05 L18.0019775,30.05 C18.2682861,30.05 18.5075684,30.0328613 18.7198242,29.998584 C18.9320801,29.9643066 19.1304932,29.9049805 19.3150635,29.8206055 C19.4996338,29.7362305 19.6697021,29.6241699 19.8252686,29.4844238 C20.0230225,29.3024902 20.1851807,29.096167 20.3117432,28.8654541 C20.4383057,28.6347412 20.5319092,28.3756836 20.5925537,28.0882813 C20.6531982,27.8008789 20.6835205,27.4831543 20.6835205,27.1351074 C20.6835205,26.0804199 20.3816162,25.2946777 19.7778076,24.7778809 C19.5457764,24.5748535 19.2873779,24.4364258 19.0026123,24.3625977 C18.7747998,24.3035352 18.5107061,24.2680977 18.2103311,24.2562852 L17.9782471,24.2518555 Z M17.6776611,25.1812988 C18.0362549,25.1812988 18.3421143,25.2201904 18.5952393,25.2979736 C18.8483643,25.3757568 19.0612793,25.5570313 19.2339844,25.8417969 C19.4066895,26.1265625 19.493042,26.5550293 19.493042,27.1271973 C19.493042,27.9393066 19.2966064,28.5114746 18.9037354,28.8437012 C18.8167236,28.920166 18.7138916,28.9794922 18.5952393,29.0216797 C18.4765869,29.0638672 18.3618896,29.0902344 18.2511475,29.1007813 C18.1404053,29.1113281 17.9874756,29.1166016 17.7923584,29.1166016 L16.9024658,29.1166016 L16.9024658,25.1812988 L17.6776611,25.1812988 Z M23.9662354,25.1338379 C24.3696533,25.1338379 24.639917,25.1628418 24.7770264,25.2208496 C24.9220459,25.2814941 25.0354248,25.3731201 25.1171631,25.4957275 C25.1989014,25.618335 25.2397705,25.7600586 25.2397705,25.9208984 C25.2397705,26.1265625 25.1890137,26.2880615 25.0875,26.4053955 C24.9859863,26.5227295 24.8389893,26.6057861 24.6465088,26.6545654 C24.4540283,26.7033447 24.2167236,26.7277344 23.9345947,26.7277344 L22.8825439,26.7277344 L22.8825439,25.1338379 L23.9662354,25.1338379 Z" id="形状结合" fill="#77BC31"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),T6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_css</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-442.000000, -5331.000000)">\r
            <g id="icon_css" transform="translate(442.000000, 5331.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#E6EBEF"></path>\r
                <g id="CSS" transform="translate(11.000000, 4.000000)">\r
                    <rect id="矩形" fill="#000000" fill-rule="nonzero" opacity="0" x="0" y="0" width="14" height="14"></rect>\r
                    <path d="M4.84533,0 C3.65533,0 2.69283,0.96467 2.69283,2.15467 L2.69283,5.92375 L1.07625,5.92375 C0.78015,5.92375 0.53809,6.16574 0.53809,6.46191 L0.53809,9.69283 C0.53809,9.98893 0.78008,10.23092 1.07625,10.23092 L2.69283,10.23092 L2.69283,11.84533 C2.69283,13.03533 3.65533,14 4.84533,14 L11.30717,14 C12.49717,14 13.46184,13.03533 13.46184,11.84533 L13.46184,4.3071 C13.46184,3.73632 12.92879,3.18696 11.99184,2.26618 L11.59375,1.86802 L11.19559,1.47 C10.27495,0.53305 9.72608,0 9.15467,0 L4.8454,0 L4.84533,0 Z M4.84533,1.07625 L8.77191,1.07625 C9.15957,1.1732 9.15467,1.65235 9.15467,2.12625 L9.15467,3.76908 C9.15467,4.06518 9.39666,4.30717 9.69283,4.30717 L11.30717,4.30717 C11.84568,4.30717 12.38559,4.30682 12.38559,4.84533 L12.38559,11.84533 C12.38559,12.4376 11.89951,12.92375 11.30717,12.92375 L4.8454,12.92375 C4.25306,12.92375 3.76908,12.4376 3.76908,11.84533 L3.76908,10.23092 L9.15474,10.23092 C9.45091,10.23092 9.6929,9.989 9.6929,9.69283 L9.6929,6.46191 C9.6929,6.16574 9.45091,5.92375 9.15474,5.92375 L3.76908,5.92375 L3.76908,2.15467 C3.76908,1.5624 4.25299,1.07625 4.84533,1.07625 Z M3.37092,6.91033 C3.61592,6.91033 3.82592,6.95408 4.00092,7.04158 L3.86967,7.30408 C3.70342,7.21658 3.53717,7.17283 3.37092,7.17283 C3.14342,7.17283 2.95533,7.26033 2.80658,7.43533 C2.66658,7.60158 2.59658,7.83783 2.59658,8.14408 C2.59658,8.45908 2.66217,8.69967 2.79342,8.86592 C2.92467,9.02342 3.11717,9.10217 3.37092,9.10217 C3.51092,9.10217 3.69033,9.07158 3.90908,9.01033 L3.90908,9.27283 C3.75158,9.33408 3.55467,9.36467 3.31842,9.36467 C2.99467,9.36467 2.74092,9.26408 2.55717,9.06283 C2.37342,8.85283 2.28158,8.54217 2.28158,8.13092 C2.28158,7.74592 2.37783,7.44842 2.57033,7.23842 C2.77158,7.01967 3.03842,6.91033 3.37092,6.91033 L3.37092,6.91033 Z M5.01158,6.91033 C5.24783,6.91033 5.45783,6.95408 5.64158,7.04158 L5.53658,7.29092 C5.35283,7.21217 5.17783,7.17283 5.01158,7.17283 C4.87158,7.17283 4.76658,7.20342 4.69658,7.26467 C4.62658,7.32592 4.59158,7.40908 4.59158,7.51408 C4.59158,7.63658 4.61783,7.72842 4.67033,7.78967 C4.73158,7.84217 4.87592,7.92092 5.10342,8.02592 C5.33967,8.12217 5.49283,8.22283 5.56283,8.32783 C5.64158,8.42408 5.68092,8.54658 5.68092,8.69533 C5.68092,8.89658 5.61092,9.05842 5.47092,9.18092 C5.33092,9.30342 5.12967,9.36467 4.86717,9.36467 C4.61342,9.36467 4.41217,9.32967 4.26342,9.25967 L4.26342,8.97092 C4.47342,9.05842 4.67467,9.10217 4.86717,9.10217 C5.04217,9.10217 5.16908,9.07158 5.24783,9.01033 C5.33533,8.94908 5.37908,8.85283 5.37908,8.72158 C5.37908,8.61658 5.34842,8.52908 5.28717,8.45908 C5.24342,8.41533 5.08158,8.33217 4.80158,8.20967 C4.61783,8.12217 4.48658,8.02592 4.40783,7.92092 C4.32908,7.81592 4.28967,7.68033 4.28967,7.51408 C4.28967,7.33033 4.35092,7.18592 4.47342,7.08092 C4.60467,6.96717 4.78408,6.91033 5.01158,6.91033 L5.01158,6.91033 Z M6.75717,6.91033 C6.99342,6.91033 7.20342,6.95408 7.38717,7.04158 L7.28217,7.29092 C7.09842,7.21217 6.92342,7.17283 6.75717,7.17283 C6.61717,7.17283 6.51217,7.20342 6.44217,7.26467 C6.37217,7.32592 6.33717,7.40908 6.33717,7.51408 C6.33717,7.63658 6.36342,7.72842 6.41592,7.78967 C6.47717,7.84217 6.62158,7.92092 6.84908,8.02592 C7.08533,8.12217 7.23842,8.22283 7.30842,8.32783 C7.38717,8.42408 7.42658,8.54658 7.42658,8.69533 C7.42658,8.89658 7.35658,9.05842 7.21658,9.18092 C7.07658,9.30342 6.87533,9.36467 6.61283,9.36467 C6.35908,9.36467 6.15783,9.32967 6.00908,9.25967 L6.00908,8.97092 C6.21908,9.05842 6.42033,9.10217 6.61283,9.10217 C6.78783,9.10217 6.91467,9.07158 6.99342,9.01033 C7.08092,8.94908 7.12467,8.85283 7.12467,8.72158 C7.12467,8.61658 7.09408,8.52908 7.03283,8.45908 C6.98908,8.41533 6.82717,8.33217 6.54717,8.20967 C6.36342,8.12217 6.23217,8.02592 6.15342,7.92092 C6.07467,7.81592 6.03533,7.68033 6.03533,7.51408 C6.03533,7.33033 6.09658,7.18592 6.21908,7.08092 C6.35033,6.96717 6.52967,6.91033 6.75717,6.91033 L6.75717,6.91033 Z" id="形状" fill="#8199AE"></path>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C2CDD7"></path>\r
                <rect id="Rectangle-9" fill="#8199AE" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="CSS" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="8.1" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="9.59941406" y="30.05">CSS</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),O6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_eps</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-117.000000, -5331.000000)">\r
            <g id="icon_eps" transform="translate(117.000000, 5331.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#FCECD3"></path>\r
                <g id="eps-2" transform="translate(12.600000, 11.250000)">\r
                    <path d="M1.15305004,4.98925567 L1.15305004,6.1164143 L0,6.1164143 L0,4.98925567 L1.15305004,4.98925567 Z M6.18278995,0.0715655116 L6.182,0.459565512 L10.0879321,0.460027028 C10.1540145,0.262765566 10.3286669,0.113142652 10.5425808,0.0789532741 L10.6359899,0.0715655116 C10.9538111,0.0715655116 11.2125,0.324316279 11.2125,0.63505723 C11.2125,0.945973377 10.9538111,1.19892854 10.6359899,1.19892854 C10.380517,1.19892854 10.1634086,1.03563861 10.0879321,0.810192348 L6.182,0.809565512 L6.18278995,1.19892854 L5.02973991,1.19892854 L5.029,0.809565512 L1.12458087,0.810192348 C1.05846268,1.00757878 0.883735228,1.15731839 0.669883756,1.19153488 L0.576510087,1.19892854 C0.258509703,1.19892854 0,0.945973377 0,0.63505723 C0,0.324316279 0.258509703,0.0715655116 0.576510087,0.0715655116 C0.831862978,0.0715655116 1.04906352,0.234723495 1.12458087,0.460027028 L5.029,0.459565512 L5.02973991,0.0715655116 L6.18278995,0.0715655116 Z" id="Combined-Shape" fill="#F29F22"></path>\r
                    <rect id="Rectangle-3" fill="#F29F22" x="10.3553738" y="4.82751098" width="1.12124998" height="1.11461536"></rect>\r
                    <path d="M0.487385621,5.33076923 C0.487385621,5.33076923 0.974885621,0.484615385 5.84988562,0.484615385 C10.7248856,0.484615385 10.9744359,5.57884289 10.9744359,5.57884289" id="Path-2" stroke="#F29F22" stroke-width="0.75"></path>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#F8CF90"></path>\r
                <path d="M34.3596082,21.1704545 C35.2655716,21.1704545 36,21.9044128 36,22.8116116 L36,30.9838429 C36,31.8902289 35.2645731,32.625 34.3596082,32.625 L1.64039183,32.625 C0.734428437,32.625 0,31.8910417 0,30.9838429 L0,22.8116116 C0,21.9052256 0.73542691,21.1704545 1.64039183,21.1704545 L34.3596082,21.1704545 Z M18.7819336,24.2518555 L17.2750488,24.2518555 C17.0166504,24.2518555 16.828125,24.3072266 16.7094727,24.4179688 C16.6105957,24.5102539 16.5529175,24.6538086 16.536438,24.8486328 L16.5314941,24.9716797 L16.5314941,29.440918 C16.5314941,29.6729492 16.5855469,29.8489502 16.6936523,29.9689209 C16.8017578,30.0888916 16.9441406,30.148877 17.1208008,30.148877 C17.2895508,30.148877 17.4292969,30.0882324 17.5400391,29.9669434 C17.6286328,29.8699121 17.6817891,29.7365996 17.6995078,29.5670059 L17.7061523,29.4330078 L17.7061523,27.7916504 L18.7819336,27.7916504 C19.5017578,27.7916504 20.0436035,27.6393799 20.4074707,27.3348389 C20.7713379,27.0302979 20.9532715,26.5827148 20.9532715,25.9920898 C20.9532715,25.7152344 20.9084473,25.4660645 20.8187988,25.2445801 C20.7291504,25.0230957 20.5979736,24.8358887 20.4252686,24.682959 C20.2525635,24.5300293 20.0475586,24.4206055 19.8102539,24.3546875 C19.6103027,24.2975586 19.3389404,24.2642334 18.996167,24.2547119 L18.7819336,24.2518555 Z M24.0224121,24.1529785 C23.5768066,24.1529785 23.189209,24.2195557 22.8596191,24.35271 C22.5300293,24.4858643 22.2782227,24.6763672 22.1041992,24.9242188 C21.9301758,25.1720703 21.8431641,25.4555176 21.8431641,25.7745605 C21.8431641,26.1094238 21.9255615,26.3895752 22.0903564,26.6150146 C22.2551514,26.8404541 22.4779541,27.0184326 22.7587646,27.1489502 C23.0395752,27.2794678 23.3882813,27.3935059 23.8048828,27.4910645 C24.1160156,27.5622559 24.3645264,27.6294922 24.550415,27.6927734 C24.7363037,27.7560547 24.887915,27.8476807 25.005249,27.9676514 C25.122583,28.0876221 25.18125,28.2438477 25.18125,28.4363281 C25.18125,28.6789063 25.0724854,28.8812744 24.8549561,29.0434326 C24.6374268,29.2055908 24.3546387,29.2866699 24.0065918,29.2866699 C23.7534668,29.2866699 23.5491211,29.24646 23.3935547,29.16604 C23.2379883,29.0856201 23.1173584,28.9834473 23.031665,28.8595215 C22.9459717,28.7355957 22.8662109,28.5813477 22.7923828,28.3967773 C22.7317383,28.2412109 22.6585693,28.123877 22.572876,28.0447754 C22.4871826,27.9656738 22.382373,27.926123 22.2584473,27.926123 C22.1055176,27.926123 21.9796143,27.9768799 21.8807373,28.0783936 C21.7818604,28.1799072 21.7324219,28.3018555 21.7324219,28.4442383 C21.7324219,28.6894531 21.8148193,28.9419189 21.9796143,29.2016357 C22.1444092,29.4613525 22.3586426,29.6689941 22.6223145,29.8245605 C22.9940918,30.0407715 23.4660645,30.148877 24.0382324,30.148877 C24.5154785,30.148877 24.928125,30.0697754 25.2761719,29.9115723 C25.6242188,29.7533691 25.8885498,29.5345215 26.069165,29.2550293 C26.2497803,28.9755371 26.3400879,28.6604492 26.3400879,28.3097656 C26.3400879,28.0170898 26.2880127,27.7698975 26.1838623,27.5681885 C26.0797119,27.3664795 25.9346924,27.199707 25.7488037,27.0678711 C25.562915,26.9360352 25.3374756,26.8239746 25.0724854,26.7316895 C24.8074951,26.6394043 24.5115234,26.5550293 24.1845703,26.4785645 C23.9235352,26.4126465 23.7363281,26.3625488 23.6229492,26.3282715 C23.5095703,26.2939941 23.3975098,26.2465332 23.2867676,26.1858887 C23.1760254,26.1252441 23.0890137,26.0527344 23.0257324,25.9683594 C22.9624512,25.8839844 22.9308105,25.7837891 22.9308105,25.6677734 C22.9308105,25.4805664 23.0237549,25.3203857 23.2096436,25.1872314 C23.3955322,25.0540771 23.6400879,24.9875 23.9433105,24.9875 C24.2702637,24.9875 24.5075684,25.0488037 24.6552246,25.1714111 C24.8028809,25.2940186 24.9294434,25.4647461 25.0349121,25.6835938 C25.1166504,25.8365234 25.1924561,25.9466064 25.2623291,26.0138428 C25.3322021,26.0810791 25.434375,26.1146973 25.5688477,26.1146973 C25.7165039,26.1146973 25.8397705,26.058667 25.9386475,25.9466064 C26.0375244,25.8345459 26.0869629,25.7086426 26.0869629,25.5688965 C26.0869629,25.4159668 26.0474121,25.259082 25.9683105,25.0982422 C25.889209,24.9374023 25.7639648,24.7838135 25.5925781,24.6374756 C25.4211914,24.4911377 25.2056396,24.3738037 24.9459229,24.2854736 C24.6862061,24.1971436 24.3783691,24.1529785 24.0224121,24.1529785 Z M14.9494629,24.2518555 L11.8486816,24.2518555 C11.6825684,24.2518555 11.5461182,24.2762451 11.4393311,24.3250244 C11.3325439,24.3738037 11.2541016,24.4515869 11.2040039,24.558374 C11.1664307,24.6384644 11.1429474,24.7359818 11.1335541,24.8509262 L11.1288574,24.9716797 L11.1288574,29.3301758 C11.1288574,29.5833008 11.1848877,29.7665527 11.2969482,29.8799316 C11.390332,29.9744141 11.5336121,30.0295288 11.7267883,30.0452759 L11.8486816,30.05 L15.0404297,30.05 C15.225,30.05 15.3640869,30.0071533 15.4576904,29.92146 C15.5512939,29.8357666 15.5980957,29.7243652 15.5980957,29.5872559 C15.5980957,29.444873 15.5512939,29.330835 15.4576904,29.2451416 C15.3828076,29.1765869 15.2788154,29.1354541 15.1457139,29.1217432 L15.0404297,29.1166016 L12.3035156,29.1166016 L12.3035156,27.467334 L14.7398438,27.467334 C14.9217773,27.467334 15.05625,27.4258057 15.1432617,27.342749 C15.2302734,27.2596924 15.2737793,27.1522461 15.2737793,27.0204102 C15.2737793,26.8885742 15.2296143,26.7824463 15.1412842,26.7020264 C15.0706201,26.6376904 14.9708467,26.5990889 14.8419639,26.5862217 L14.7398438,26.5813965 L12.3035156,26.5813965 L12.3035156,25.1575684 L14.9494629,25.1575684 C15.1366699,25.1575684 15.2757568,25.11604 15.3667236,25.0329834 C15.4576904,24.9499268 15.5031738,24.8398438 15.5031738,24.7027344 C15.5031738,24.5682617 15.4576904,24.4594971 15.3667236,24.3764404 C15.2939502,24.3099951 15.1903799,24.2701279 15.0560127,24.2568389 L14.9494629,24.2518555 Z M18.497168,25.1338379 C19.021875,25.1338379 19.3686035,25.2195313 19.5373535,25.390918 C19.6876465,25.5517578 19.762793,25.7600586 19.762793,26.0158203 C19.762793,26.2293945 19.7140137,26.4020996 19.6164551,26.5339355 C19.5188965,26.6657715 19.3771729,26.7606934 19.1912842,26.8187012 C19.0053955,26.876709 18.7740234,26.9057129 18.497168,26.9057129 L17.7061523,26.9057129 L17.7061523,25.1338379 L18.497168,25.1338379 Z" id="形状结合" fill="#F29F22"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),P6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_excel</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-182.000000, -5214.000000)">\r
            <g id="icon_excel" transform="translate(182.000000, 5214.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#D9ECDA"></path>\r
                <text id="E" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="12.6" font-weight="normal" fill="#42A146">\r
                    <tspan x="8.17294922" y="19.65">E</tspan>\r
                </text>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#A0D0A2"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M14.6816895,24.1529785 C14.5999512,24.1529785 14.5281006,24.1668213 14.4661377,24.1945068 C14.4041748,24.2221924 14.3481445,24.2637207 14.2980469,24.3190918 C14.2479492,24.3744629 14.195874,24.4443359 14.1418213,24.5287109 C14.1057861,24.5849609 14.0647705,24.6488281 14.0187744,24.7203125 L13.9460449,24.833252 L13.0166016,26.2412598 L12.1464844,24.833252 C11.9856445,24.5722168 11.8597412,24.3935791 11.7687744,24.2973389 C11.6778076,24.2010986 11.5505859,24.1529785 11.3871094,24.1529785 C11.2447266,24.1529785 11.1240967,24.1971436 11.0252197,24.2854736 C10.9263428,24.3738037 10.8769043,24.4733398 10.8769043,24.584082 C10.8769043,24.6921875 10.9026123,24.8049072 10.9540283,24.9222412 C10.9925903,25.0102417 11.0456131,25.1112198 11.1130966,25.2251755 L11.1854004,25.343457 L12.277002,27.0283203 L10.9797363,28.9228027 C10.7872559,29.2049316 10.6910156,29.4435547 10.6910156,29.6386719 C10.6910156,29.8047852 10.7378174,29.9313477 10.8314209,30.0183594 C10.9250244,30.1053711 11.0430176,30.148877 11.1854004,30.148877 C11.2776855,30.148877 11.3581055,30.134375 11.4266602,30.1053711 C11.4952148,30.0763672 11.554541,30.0368164 11.6046387,29.9867188 C11.6547363,29.9366211 11.697583,29.8852051 11.7331787,29.8324707 C11.7598755,29.7929199 11.8002914,29.7311218 11.8544266,29.6470764 L11.9131348,29.5556152 L12.9968262,27.9023926 L14.0172363,29.5081543 C14.1464355,29.7059082 14.2347656,29.8390625 14.2822266,29.9076172 C14.3296875,29.9761719 14.3916504,30.0335205 14.4681152,30.0796631 C14.5445801,30.1258057 14.6381836,30.148877 14.7489258,30.148877 C14.8517578,30.148877 14.944043,30.1264648 15.0257813,30.0816406 C15.1075195,30.0368164 15.1708008,29.9761719 15.215625,29.899707 C15.2604492,29.8232422 15.2828613,29.7388672 15.2828613,29.646582 C15.2828613,29.5490234 15.2578125,29.4455322 15.2077148,29.3361084 C15.1701416,29.2540405 15.1155121,29.1560287 15.0438263,29.0420731 L14.9664551,28.9228027 L13.7364258,27.0283203 L14.8794434,25.3197266 C15.0824707,25.0165039 15.1839844,24.7765625 15.1839844,24.5999023 C15.1839844,24.4786133 15.1371826,24.3738037 15.0435791,24.2854736 C14.9499756,24.1971436 14.8293457,24.1529785 14.6816895,24.1529785 Z M22.757959,24.1529785 C22.3123535,24.1529785 21.9247559,24.2195557 21.595166,24.35271 C21.2655762,24.4858643 21.0137695,24.6763672 20.8397461,24.9242188 C20.6657227,25.1720703 20.5787109,25.4555176 20.5787109,25.7745605 C20.5787109,26.1094238 20.6611084,26.3895752 20.8259033,26.6150146 C20.9906982,26.8404541 21.213501,27.0184326 21.4943115,27.1489502 C21.7751221,27.2794678 22.1238281,27.3935059 22.5404297,27.4910645 C22.8515625,27.5622559 23.1000732,27.6294922 23.2859619,27.6927734 C23.4718506,27.7560547 23.6234619,27.8476807 23.7407959,27.9676514 C23.8581299,28.0876221 23.9167969,28.2438477 23.9167969,28.4363281 C23.9167969,28.6789063 23.8080322,28.8812744 23.5905029,29.0434326 C23.3729736,29.2055908 23.0901855,29.2866699 22.7421387,29.2866699 C22.4890137,29.2866699 22.284668,29.24646 22.1291016,29.16604 C21.9735352,29.0856201 21.8529053,28.9834473 21.7672119,28.8595215 C21.6815186,28.7355957 21.6017578,28.5813477 21.5279297,28.3967773 C21.4672852,28.2412109 21.3941162,28.123877 21.3084229,28.0447754 C21.2227295,27.9656738 21.1179199,27.926123 20.9939941,27.926123 C20.8410645,27.926123 20.7151611,27.9768799 20.6162842,28.0783936 C20.5174072,28.1799072 20.4679688,28.3018555 20.4679688,28.4442383 C20.4679688,28.6894531 20.5503662,28.9419189 20.7151611,29.2016357 C20.8799561,29.4613525 21.0941895,29.6689941 21.3578613,29.8245605 C21.7296387,30.0407715 22.2016113,30.148877 22.7737793,30.148877 C23.2510254,30.148877 23.6636719,30.0697754 24.0117188,29.9115723 C24.3597656,29.7533691 24.6240967,29.5345215 24.8047119,29.2550293 C24.9853271,28.9755371 25.0756348,28.6604492 25.0756348,28.3097656 C25.0756348,28.0170898 25.0235596,27.7698975 24.9194092,27.5681885 C24.8152588,27.3664795 24.6702393,27.199707 24.4843506,27.0678711 C24.2984619,26.9360352 24.0730225,26.8239746 23.8080322,26.7316895 C23.543042,26.6394043 23.2470703,26.5550293 22.9201172,26.4785645 C22.659082,26.4126465 22.471875,26.3625488 22.3584961,26.3282715 C22.2451172,26.2939941 22.1330566,26.2465332 22.0223145,26.1858887 C21.9115723,26.1252441 21.8245605,26.0527344 21.7612793,25.9683594 C21.697998,25.8839844 21.6663574,25.7837891 21.6663574,25.6677734 C21.6663574,25.4805664 21.7593018,25.3203857 21.9451904,25.1872314 C22.1310791,25.0540771 22.3756348,24.9875 22.6788574,24.9875 C23.0058105,24.9875 23.2431152,25.0488037 23.3907715,25.1714111 C23.5384277,25.2940186 23.6649902,25.4647461 23.770459,25.6835938 C23.8521973,25.8365234 23.9280029,25.9466064 23.997876,26.0138428 C24.067749,26.0810791 24.1699219,26.1146973 24.3043945,26.1146973 C24.4520508,26.1146973 24.5753174,26.058667 24.6741943,25.9466064 C24.7730713,25.8345459 24.8225098,25.7086426 24.8225098,25.5688965 C24.8225098,25.4159668 24.782959,25.259082 24.7038574,25.0982422 C24.6247559,24.9374023 24.4995117,24.7838135 24.328125,24.6374756 C24.1567383,24.4911377 23.9411865,24.3738037 23.6814697,24.2854736 C23.4217529,24.1971436 23.113916,24.1529785 22.757959,24.1529785 Z M16.6434082,24.1529785 C16.4693848,24.1529785 16.3289795,24.2123047 16.2221924,24.330957 C16.1367627,24.4258789 16.0855049,24.5587695 16.0684189,24.7296289 L16.0620117,24.8648926 L16.0620117,29.3301758 C16.0620117,29.5833008 16.118042,29.7665527 16.2301025,29.8799316 C16.3234863,29.9744141 16.4667664,30.0295288 16.6599426,30.0452759 L16.7818359,30.05 L19.617627,30.05 C19.8101074,30.05 19.9564453,30.005835 20.0566406,29.9175049 C20.1568359,29.8291748 20.2069336,29.7138184 20.2069336,29.5714355 C20.2069336,29.4316895 20.1561768,29.3156738 20.0546631,29.2233887 C19.9734521,29.1495605 19.8639756,29.1052637 19.7262334,29.090498 L19.617627,29.0849609 L17.2366699,29.0849609 L17.2366699,24.8648926 C17.2366699,24.6249512 17.181958,24.4463135 17.0725342,24.3289795 C16.9631104,24.2116455 16.8200684,24.1529785 16.6434082,24.1529785 Z" id="形状结合" fill="#42A146"></path>\r
                <path d="M25.1913007,15.705 C25.4446333,15.705 25.65,15.900971 25.65,16.1519485 L25.65,18.9030515 C25.65,19.1498943 25.4497742,19.35 25.1913007,19.35 L18.0086993,19.35 C17.7553667,19.35 17.55,19.154029 17.55,18.9030515 L17.55,16.1519485 C17.55,15.9051057 17.7502258,15.705 18.0086993,15.705 L25.1913007,15.705 Z M25.1913007,10.35 C25.4446333,10.35 25.65,10.545971 25.65,10.7969485 L25.65,13.5480515 C25.65,13.7948943 25.4497742,13.995 25.1913007,13.995 L18.0086993,13.995 C17.7553667,13.995 17.55,13.799029 17.55,13.5480515 L17.55,10.7969485 C17.55,10.5501057 17.7502258,10.35 18.0086993,10.35 L25.1913007,10.35 Z" id="Combined-Shape" fill="#42A146"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),Z6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_exe</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-376.000000, -5275.000000)">\r
            <g id="icon_exe" transform="translate(376.000000, 5275.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#E6EBEF"></path>\r
                <path d="M25.199,15.2207448 L25.2,19.1071958 C25.2,19.5231661 24.8619356,19.8318612 24.4431705,19.7965387 L17.403,19.2017448 L17.403,15.2207448 L25.199,15.2207448 Z M16.661,15.2207448 L16.661,19.1397448 L11.1068295,18.6716277 C10.6888441,18.6363709 10.35,18.265334 10.35,17.8458021 L10.35,15.2207448 L16.661,15.2207448 Z M16.661,10.5587448 L16.661,14.4787448 L10.35,14.4787448 L10.35,11.8542549 C10.35,11.4334208 10.6880644,11.0637518 11.1068295,11.0284293 L16.661,10.5587448 Z M25.2,10.5928612 L25.199,14.4787448 L17.403,14.4787448 L17.403,10.4967448 L24.4431705,9.90351832 C24.8611559,9.86826154 25.2,10.1730868 25.2,10.5928612 Z" id="Combined-Shape" fill="#8199AE"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C2CDD7"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M19.6749756,24.1529785 C19.5932373,24.1529785 19.5213867,24.1668213 19.4594238,24.1945068 C19.3974609,24.2221924 19.3414307,24.2637207 19.291333,24.3190918 C19.2412354,24.3744629 19.1891602,24.4443359 19.1351074,24.5287109 C19.0990723,24.5849609 19.0580566,24.6488281 19.0120605,24.7203125 L18.9393311,24.833252 L18.0098877,26.2412598 L17.1397705,24.833252 C16.9789307,24.5722168 16.8530273,24.3935791 16.7620605,24.2973389 C16.6710937,24.2010986 16.5438721,24.1529785 16.3803955,24.1529785 C16.2380127,24.1529785 16.1173828,24.1971436 16.0185059,24.2854736 C15.9196289,24.3738037 15.8701904,24.4733398 15.8701904,24.584082 C15.8701904,24.6921875 15.8958984,24.8049072 15.9473145,24.9222412 C15.9858765,25.0102417 16.0388992,25.1112198 16.1063828,25.2251755 L16.1786865,25.343457 L17.2702881,27.0283203 L15.9730225,28.9228027 C15.780542,29.2049316 15.6843018,29.4435547 15.6843018,29.6386719 C15.6843018,29.8047852 15.7311035,29.9313477 15.824707,30.0183594 C15.9183105,30.1053711 16.0363037,30.148877 16.1786865,30.148877 C16.2709717,30.148877 16.3513916,30.134375 16.4199463,30.1053711 C16.488501,30.0763672 16.5478271,30.0368164 16.5979248,29.9867188 C16.6480225,29.9366211 16.6908691,29.8852051 16.7264648,29.8324707 C16.7531616,29.7929199 16.7935776,29.7311218 16.8477127,29.6470764 L16.9064209,29.5556152 L17.9901123,27.9023926 L19.0105225,29.5081543 C19.1397217,29.7059082 19.2280518,29.8390625 19.2755127,29.9076172 C19.3229736,29.9761719 19.3849365,30.0335205 19.4614014,30.0796631 C19.5378662,30.1258057 19.6314697,30.148877 19.7422119,30.148877 C19.8450439,30.148877 19.9373291,30.1264648 20.0190674,30.0816406 C20.1008057,30.0368164 20.1640869,29.9761719 20.2089111,29.899707 C20.2537354,29.8232422 20.2761475,29.7388672 20.2761475,29.646582 C20.2761475,29.5490234 20.2510986,29.4455322 20.201001,29.3361084 C20.1634277,29.2540405 20.1087982,29.1560287 20.0371124,29.0420731 L19.9597412,28.9228027 L18.7297119,27.0283203 L19.8727295,25.3197266 C20.0757568,25.0165039 20.1772705,24.7765625 20.1772705,24.5999023 C20.1772705,24.4786133 20.1304687,24.3738037 20.0368652,24.2854736 C19.9432617,24.1971436 19.8226318,24.1529785 19.6749756,24.1529785 Z M14.6045654,24.2518555 L11.5037842,24.2518555 C11.3376709,24.2518555 11.2012207,24.2762451 11.0944336,24.3250244 C10.9876465,24.3738037 10.9092041,24.4515869 10.8591064,24.558374 C10.8215332,24.6384644 10.7980499,24.7359818 10.7886566,24.8509262 L10.78396,24.9716797 L10.78396,29.3301758 C10.78396,29.5833008 10.8399902,29.7665527 10.9520508,29.8799316 C11.0454346,29.9744141 11.1887146,30.0295288 11.3818909,30.0452759 L11.5037842,30.05 L14.6955322,30.05 C14.8801025,30.05 15.0191895,30.0071533 15.112793,29.92146 C15.2063965,29.8357666 15.2531982,29.7243652 15.2531982,29.5872559 C15.2531982,29.444873 15.2063965,29.330835 15.112793,29.2451416 C15.0379102,29.1765869 14.933918,29.1354541 14.8008164,29.1217432 L14.6955322,29.1166016 L11.9586182,29.1166016 L11.9586182,27.467334 L14.3949463,27.467334 C14.5768799,27.467334 14.7113525,27.4258057 14.7983643,27.342749 C14.885376,27.2596924 14.9288818,27.1522461 14.9288818,27.0204102 C14.9288818,26.8885742 14.8847168,26.7824463 14.7963867,26.7020264 C14.7257227,26.6376904 14.6259492,26.5990889 14.4970664,26.5862217 L14.3949463,26.5813965 L11.9586182,26.5813965 L11.9586182,25.1575684 L14.6045654,25.1575684 C14.7917725,25.1575684 14.9308594,25.11604 15.0218262,25.0329834 C15.112793,24.9499268 15.1582764,24.8398438 15.1582764,24.7027344 C15.1582764,24.5682617 15.112793,24.4594971 15.0218262,24.3764404 C14.9490527,24.3099951 14.8454824,24.2701279 14.7111152,24.2568389 L14.6045654,24.2518555 Z M24.8996338,24.2518555 L21.7988525,24.2518555 C21.6327393,24.2518555 21.4962891,24.2762451 21.389502,24.3250244 C21.2827148,24.3738037 21.2042725,24.4515869 21.1541748,24.558374 C21.1166016,24.6384644 21.0931183,24.7359818 21.083725,24.8509262 L21.0790283,24.9716797 L21.0790283,29.3301758 C21.0790283,29.5833008 21.1350586,29.7665527 21.2471191,29.8799316 C21.3405029,29.9744141 21.483783,30.0295288 21.6769592,30.0452759 L21.7988525,30.05 L24.9906006,30.05 C25.1751709,30.05 25.3142578,30.0071533 25.4078613,29.92146 C25.5014648,29.8357666 25.5482666,29.7243652 25.5482666,29.5872559 C25.5482666,29.444873 25.5014648,29.330835 25.4078613,29.2451416 C25.3329785,29.1765869 25.2289863,29.1354541 25.0958848,29.1217432 L24.9906006,29.1166016 L22.2536865,29.1166016 L22.2536865,27.467334 L24.6900146,27.467334 C24.8719482,27.467334 25.0064209,27.4258057 25.0934326,27.342749 C25.1804443,27.2596924 25.2239502,27.1522461 25.2239502,27.0204102 C25.2239502,26.8885742 25.1797852,26.7824463 25.0914551,26.7020264 C25.020791,26.6376904 24.9210176,26.5990889 24.7921348,26.5862217 L24.6900146,26.5813965 L22.2536865,26.5813965 L22.2536865,25.1575684 L24.8996338,25.1575684 C25.0868408,25.1575684 25.2259277,25.11604 25.3168945,25.0329834 C25.4078613,24.9499268 25.4533447,24.8398438 25.4533447,24.7027344 C25.4533447,24.5682617 25.4078613,24.4594971 25.3168945,24.3764404 C25.2441211,24.3099951 25.1405508,24.2701279 25.0061836,24.2568389 L24.8996338,24.2518555 Z" id="形状结合" fill="#8199AE"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),j6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_flash</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-117.000000, -5275.000000)">\r
            <g id="icon_flash" transform="translate(117.000000, 5275.000000)">\r
                <path d="M23.604,0 L23.6045455,6.21998174 C23.6045455,6.99128734 24.2096546,9.01596452 26.960151,9.01596452 L32.604,8.985 L32.5571138,33.0899335 C32.5571138,34.4653386 31.4404421,35.5573497 30.0474034,35.639316 L29.878966,35.6442572 L5.82814787,35.6442572 C4.35779218,35.6442572 3.15,34.5203548 3.15,33.0899335 L3.15,33.0899335 L3.15,2.55432373 C3.15,1.12390244 4.35779218,0 5.82814787,0 L5.82814787,0 L23.604,0 Z" id="Combined-Shape" fill="#F6D5D5"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#E99696"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M14.4409624,24.2518555 L11.6724077,24.2518555 C11.5062944,24.2518555 11.3698442,24.2762451 11.2630571,24.3250244 C11.15627,24.3738037 11.0778276,24.4515869 11.0277299,24.558374 C10.9901567,24.6384644 10.9666734,24.7359818 10.9572801,24.8509262 L10.9525835,24.9716797 L10.9525835,29.4330078 C10.9525835,29.6729492 11.0072954,29.8522461 11.1167192,29.9708984 C11.226143,30.0895508 11.3678667,30.148877 11.5418901,30.148877 C11.7132768,30.148877 11.8536821,30.09021 11.9631059,29.972876 C12.050645,29.8790088 12.1031684,29.7450635 12.1206762,29.57104 L12.1272417,29.4330078 L12.1272417,27.5227051 L14.0612749,27.5227051 C14.237935,27.5227051 14.3710893,27.4831543 14.4607377,27.4040527 C14.5503862,27.3249512 14.5952104,27.2181641 14.5952104,27.0836914 C14.5952104,26.9492188 14.5510454,26.8417725 14.4627153,26.7613525 C14.3920512,26.6970166 14.2922778,26.658415 14.163395,26.6455479 L14.0612749,26.6407227 L12.1272417,26.6407227 L12.1272417,25.1575684 L14.4409624,25.1575684 C14.6281694,25.1575684 14.7672563,25.11604 14.8582231,25.0329834 C14.9491899,24.9499268 14.9946733,24.8398438 14.9946733,24.7027344 C14.9946733,24.5682617 14.9491899,24.4594971 14.8582231,24.3764404 C14.7854497,24.3099951 14.6818794,24.2701279 14.5475122,24.2568389 L14.4409624,24.2518555 Z M22.9048295,24.1529785 C22.7439897,24.1529785 22.6088579,24.18396 22.499434,24.2459229 C22.3900102,24.3078857 22.3016801,24.3889648 22.2344438,24.4891602 C22.1672075,24.5893555 22.1039262,24.7152588 22.0446001,24.8668701 C22.0050493,24.9679443 21.9693071,25.0593506 21.9373735,25.1410889 L21.8923295,25.2564453 L20.4091752,29.0216797 C20.3485307,29.1719727 20.3050249,29.2893066 20.2786577,29.3736816 C20.2522905,29.4580566 20.2391069,29.5397949 20.2391069,29.6188965 C20.2391069,29.7560059 20.2957963,29.8786133 20.4091752,29.9867188 C20.5225542,30.0948242 20.6530717,30.148877 20.800728,30.148877 C20.9747514,30.148877 21.0999956,30.0981201 21.1764604,29.9966064 C21.2401811,29.9120117 21.3148881,29.7619568 21.4005815,29.5464417 L21.4533159,29.4092773 L21.7301713,28.6657227 L24.0873979,28.6657227 L24.3642534,29.393457 C24.4011674,29.4831055 24.4453325,29.5865967 24.4967485,29.7039307 C24.5481645,29.8212646 24.5962846,29.9082764 24.6411088,29.9649658 C24.6859331,30.0216553 24.7413042,30.0664795 24.8072221,30.0994385 C24.8731401,30.1323975 24.9522417,30.148877 25.0445268,30.148877 C25.2027299,30.148877 25.3378618,30.0928467 25.4499223,29.9807861 C25.5619829,29.8687256 25.6180131,29.745459 25.6180131,29.6109863 C25.6180131,29.5033203 25.5768144,29.3416382 25.4944169,29.1259399 L25.4400346,28.9900391 L23.9252397,25.2485352 C23.8540483,25.0613281 23.7953813,24.9116943 23.7492387,24.7996338 C23.7030961,24.6875732 23.6464067,24.5827637 23.5791704,24.4852051 C23.511934,24.3876465 23.423604,24.3078857 23.3141801,24.2459229 C23.2047563,24.18396 23.0683061,24.1529785 22.9048295,24.1529785 Z M16.4264116,24.1529785 C16.2523881,24.1529785 16.1119829,24.2123047 16.0051958,24.330957 C15.9197661,24.4258789 15.8685083,24.5587695 15.8514223,24.7296289 L15.8450151,24.8648926 L15.8450151,29.3301758 C15.8450151,29.5833008 15.9010454,29.7665527 16.0131059,29.8799316 C16.1064897,29.9744141 16.2497697,30.0295288 16.442946,30.0452759 L16.5648393,30.05 L19.4006303,30.05 C19.5931108,30.05 19.7394487,30.005835 19.839644,29.9175049 C19.9398393,29.8291748 19.989937,29.7138184 19.989937,29.5714355 C19.989937,29.4316895 19.9391801,29.3156738 19.8376665,29.2233887 C19.7564555,29.1495605 19.646979,29.1052637 19.5092368,29.090498 L19.4006303,29.0849609 L17.0196733,29.0849609 L17.0196733,24.8648926 C17.0196733,24.6249512 16.9649614,24.4463135 16.8555376,24.3289795 C16.7461137,24.2116455 16.6030717,24.1529785 16.4264116,24.1529785 Z M22.8969194,25.390918 L23.7709917,27.7837402 L22.0386674,27.7837402 L22.8969194,25.390918 Z" id="形状结合" fill="#D32E2E"></path>\r
                <path d="M21.6,9.55272746 C21.4776445,9.55975349 21.3599665,9.55993431 21.2439466,9.57419303 C20.6636985,9.64566746 20.1921696,9.94422235 19.798375,10.3740246 C19.5891779,10.602345 19.4178703,10.8685336 19.2306248,11.118552 C19.2116182,11.1439181 19.2005309,11.1757161 19.1776635,11.2203263 C19.7831053,11.2579879 20.3727578,11.2946938 20.9661224,11.3316063 L20.9661224,13.3060001 C20.9222685,13.3091256 20.8838591,13.3135169 20.8453508,13.3143693 C20.0851828,13.3306429 19.3250395,13.3490604 18.5647972,13.3593928 C18.4647152,13.3607618 18.4243755,13.3891759 18.3982908,13.4897878 C18.2414857,14.0948793 18.0330805,14.6802102 17.7472627,15.2327356 C17.3579971,15.9853481 16.8566712,16.6341458 16.1385753,17.0730406 C15.5051184,17.4601442 14.8212988,17.6114107 14.0909029,17.527589 C13.9499615,17.511393 13.9500357,17.5098173 13.950011,17.3605657 C13.9499863,16.8679682 13.950011,16.3753707 13.950011,15.882799 L13.950011,15.7572603 C14.0739751,15.7302927 14.1935835,15.7127534 14.3083165,15.6778815 C14.7984561,15.5289141 15.1912113,15.2225324 15.5279363,14.832639 C16.0831144,14.1899116 16.4173398,13.4220847 16.6722718,12.6102158 C16.8616951,12.0070615 17.0293152,11.3951247 17.247694,10.8035945 C17.5359619,10.0229553 17.9414872,9.30924429 18.517429,8.71464011 C19.1421002,8.06974293 19.8813807,7.68431833 20.7709823,7.65241702 C21.0497221,7.64239459 21.3272987,7.66150954 21.6,7.74827593 L21.6,9.55272746 L21.6,9.55272746 Z" id="Flash-2" fill="#D32E2E"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),D6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_wenjianjia</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-117.000000, -5214.000000)">\r
            <g id="icon_wenjianjia" transform="translate(117.000000, 5214.000000)">\r
                <path d="M2.91471774,2.671875 L17.5964776,2.671875 C18.8677924,2.671875 19.9976229,3.48235377 20.405072,4.6866073 L21.2541795,7.19622354 C21.4291183,7.71327149 21.1517841,8.2742376 20.6347361,8.44917643 C20.5326978,8.48370022 20.4257019,8.50131048 20.3179814,8.50131048 L0.988332189,8.50131048 C0.442491393,8.50131048 -1.48746602e-15,8.05881909 0,7.51297829 L0,5.58659274 C-1.97138028e-16,3.97683858 1.30496358,2.671875 2.91471774,2.671875 Z" id="Rectangle" fill="#CE9F06"></path>\r
                <path d="M0,7.13155242 L33.0350034,7.13155242 C34.6725258,7.13155242 36,8.4590266 36,10.096549 L36,30.2665559 C36,31.9040782 34.6725258,33.2315524 33.0350034,33.2315524 L2.96499657,33.2315524 C1.32747418,33.2315524 2.00538655e-16,31.9040782 0,30.2665559 L0,7.13155242 L0,7.13155242 Z" id="Rectangle-11" fill="#FFCD2D"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),F6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_gif</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-182.000000, -5331.000000)">\r
            <g id="icon_gif" transform="translate(182.000000, 5331.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#F6D5D5"></path>\r
                <path d="M12.6,18.45 L23.4,18.45 L23.4,7.65 L12.6,7.65 L12.6,18.45 Z M13.6851675,15.2461722 L22.3148325,15.2461722 L22.3148325,8.73516746 L13.6851675,8.73516746 L13.6851675,15.2461722 Z M17.354067,13.0758373 L19.369378,10.3370813 L20.0411483,11.4222488 L20.7129187,11.2155502 L21.2296651,14.1093301 L14.4602871,14.1093301 L16.2688995,12.404067 L17.354067,13.0758373 Z M15.3904306,11.1638756 C14.9253589,11.1638756 14.5119617,10.8538278 14.5119617,10.388756 C14.5119617,9.97535885 14.9253589,9.61363636 15.3904306,9.61363636 C15.8555024,9.61363636 16.2172249,9.97535885 16.2172249,10.388756 C16.2172249,10.8538278 15.8555024,11.1638756 15.3904306,11.1638756 L15.3904306,11.1638756 Z" id="Fill-4" fill="#D32E2E"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#E99696"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M14.4958008,24.1529785 C14.0211914,24.1529785 13.5966797,24.2221924 13.2222656,24.3606201 C12.8478516,24.4990479 12.530127,24.6987793 12.2690918,24.9598145 C12.0080566,25.2208496 11.8103027,25.5385742 11.6758301,25.9129883 C11.5413574,26.2874023 11.4741211,26.7000488 11.4741211,27.1509277 C11.4741211,27.6097168 11.543335,28.025 11.6817627,28.3967773 C11.8201904,28.7685547 12.0186035,29.0849609 12.277002,29.3459961 C12.5354004,29.6070313 12.8478516,29.8061035 13.2143555,29.9432129 C13.5808594,30.0803223 13.9869141,30.148877 14.4325195,30.148877 C14.8148437,30.148877 15.1681641,30.1053711 15.4924805,30.0183594 C15.8167969,29.9313477 16.1542969,29.7929199 16.5049805,29.6030762 C16.6262695,29.5371582 16.7258057,29.4686035 16.8035889,29.3974121 C16.8813721,29.3262207 16.9347656,29.24646 16.9637695,29.1581299 C16.9855225,29.0918823 16.999118,29.0089493 17.0045563,28.9093307 L17.0072754,28.8041504 L17.0072754,27.6927734 C17.0072754,27.4923828 16.9723389,27.3420898 16.9024658,27.2418945 C16.8325928,27.1416992 16.7409668,27.0770996 16.6275879,27.0480957 C16.552002,27.0287598 16.4652832,27.0158691 16.3674316,27.0094238 L16.2123047,27.0045898 L14.8794434,27.0045898 C14.7186035,27.0045898 14.5913818,27.0467773 14.4977783,27.1311523 C14.4041748,27.2155273 14.357373,27.3223145 14.357373,27.4515137 C14.357373,27.6097168 14.4134033,27.7217773 14.5254639,27.7876953 C14.6151123,27.8404297 14.7389326,27.8720703 14.8969248,27.8826172 L15.0218262,27.8865723 L15.9314941,27.8865723 L15.9314941,28.8278809 C15.6862793,28.9597168 15.4443604,29.0612305 15.2057373,29.1324219 C14.9671143,29.2036133 14.7120117,29.239209 14.4404297,29.239209 C13.8814453,29.239209 13.4457275,29.055957 13.1332764,28.6894531 C12.8208252,28.3229492 12.6645996,27.7995605 12.6645996,27.1192871 C12.6645996,26.8055176 12.7048096,26.5194336 12.7852295,26.2610352 C12.8656494,26.0026367 12.981665,25.7844482 13.1332764,25.6064697 C13.2848877,25.4284912 13.4674805,25.2927002 13.6810547,25.1990967 C13.8946289,25.1054932 14.137207,25.0586914 14.4087891,25.0586914 C14.6724609,25.0586914 14.8893311,25.0982422 15.0593994,25.1773438 C15.2294678,25.2564453 15.3645996,25.3540039 15.4647949,25.4700195 C15.5649902,25.5860352 15.6875977,25.7574219 15.8326172,25.9841797 C15.8853516,26.0553711 15.9506104,26.1107422 16.0283936,26.150293 C16.1061768,26.1898438 16.1872559,26.2096191 16.2716309,26.2096191 C16.4245605,26.2096191 16.5557373,26.1575439 16.6651611,26.0533936 C16.774585,25.9492432 16.8292969,25.8233398 16.8292969,25.6756836 C16.8292969,25.5385742 16.7818359,25.3816895 16.6869141,25.2050293 C16.5919922,25.0283691 16.4509277,24.8609375 16.2637207,24.7027344 C16.0765137,24.5445313 15.8326172,24.4133545 15.5320312,24.3092041 C15.2314453,24.2050537 14.8860352,24.1529785 14.4958008,24.1529785 Z M18.7554199,24.1529785 C18.5840332,24.1529785 18.4442871,24.2123047 18.3361816,24.330957 C18.2496973,24.4258789 18.1978066,24.5587695 18.1805098,24.7296289 L18.1740234,24.8648926 L18.1740234,29.4330078 C18.1740234,29.6703125 18.2287354,29.8489502 18.3381592,29.9689209 C18.447583,30.0888916 18.5866699,30.148877 18.7554199,30.148877 C18.9320801,30.148877 19.0751221,30.0895508 19.1845459,29.9708984 C19.272085,29.8759766 19.3246084,29.7422422 19.3421162,29.5696953 L19.3486816,29.4330078 L19.3486816,24.8648926 C19.3486816,24.6249512 19.2939697,24.4463135 19.1845459,24.3289795 C19.0751221,24.2116455 18.9320801,24.1529785 18.7554199,24.1529785 Z M24.126416,24.2518555 L21.3578613,24.2518555 C21.191748,24.2518555 21.0552979,24.2762451 20.9485107,24.3250244 C20.8417236,24.3738037 20.7632812,24.4515869 20.7131836,24.558374 C20.6756104,24.6384644 20.6521271,24.7359818 20.6427338,24.8509262 L20.6380371,24.9716797 L20.6380371,29.4330078 C20.6380371,29.6729492 20.692749,29.8522461 20.8021729,29.9708984 C20.9115967,30.0895508 21.0533203,30.148877 21.2273437,30.148877 C21.3987305,30.148877 21.5391357,30.09021 21.6485596,29.972876 C21.7360986,29.8790088 21.7886221,29.7450635 21.8061299,29.57104 L21.8126953,29.4330078 L21.8126953,27.5227051 L23.7467285,27.5227051 C23.9233887,27.5227051 24.056543,27.4831543 24.1461914,27.4040527 C24.2358398,27.3249512 24.2806641,27.2181641 24.2806641,27.0836914 C24.2806641,26.9492188 24.236499,26.8417725 24.1481689,26.7613525 C24.0775049,26.6970166 23.9777314,26.658415 23.8488486,26.6455479 L23.7467285,26.6407227 L21.8126953,26.6407227 L21.8126953,25.1575684 L24.126416,25.1575684 C24.313623,25.1575684 24.45271,25.11604 24.5436768,25.0329834 C24.6346436,24.9499268 24.680127,24.8398438 24.680127,24.7027344 C24.680127,24.5682617 24.6346436,24.4594971 24.5436768,24.3764404 C24.4709033,24.3099951 24.367333,24.2701279 24.2329658,24.2568389 L24.126416,24.2518555 Z" id="形状结合" fill="#D32E2E"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),$6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_html</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-831.000000, -5214.000000)">\r
            <g id="icon_html" transform="translate(831.000000, 5214.000000)">\r
                <path d="M23.604,0 L23.6045455,6.21998174 C23.6045455,6.99128734 24.2096546,9.01596452 26.960151,9.01596452 L32.604,8.985 L32.5571138,33.0899335 C32.5571138,34.4653386 31.4404421,35.5573497 30.0474034,35.639316 L29.878966,35.6442572 L5.82814787,35.6442572 C4.35779218,35.6442572 3.15,34.5203548 3.15,33.0899335 L3.15,33.0899335 L3.15,2.55432373 C3.15,1.12390244 4.35779218,0 5.82814787,0 L5.82814787,0 L23.604,0 Z" id="Combined-Shape" fill="#FADDCF"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#F2AA86"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M11.5037842,24.1529785 C11.3297607,24.1529785 11.1893555,24.2123047 11.0825684,24.330957 C10.9971387,24.4258789 10.9458809,24.5587695 10.9287949,24.7296289 L10.9223877,24.8648926 L10.9223877,26.5260254 L8.38718262,26.5260254 L8.38718262,24.8648926 C8.38718262,24.6249512 8.3324707,24.4463135 8.22304688,24.3289795 C8.11362305,24.2116455 7.97058105,24.1529785 7.7939209,24.1529785 C7.61462402,24.1529785 7.47290039,24.2123047 7.36875,24.330957 C7.28542969,24.4258789 7.2354375,24.5587695 7.21877344,24.7296289 L7.21252441,24.8648926 L7.21252441,29.4330078 C7.21252441,29.6703125 7.26525879,29.8489502 7.37072754,29.9689209 C7.47619629,30.0888916 7.61726074,30.148877 7.7939209,30.148877 C7.96794434,30.148877 8.11032715,30.0895508 8.22106934,29.9708984 C8.30966309,29.8759766 8.36281934,29.7422422 8.38053809,29.5696953 L8.38718262,29.4330078 L8.38718262,27.4831543 L10.9223877,27.4831543 L10.9223877,29.4330078 C10.9223877,29.6703125 10.9751221,29.8489502 11.0805908,29.9689209 C11.1860596,30.0888916 11.327124,30.148877 11.5037842,30.148877 C11.6778076,30.148877 11.8201904,30.0895508 11.9309326,29.9708984 C12.0195264,29.8759766 12.0726826,29.7422422 12.0904014,29.5696953 L12.0970459,29.4330078 L12.0970459,24.8648926 C12.0970459,24.6249512 12.042334,24.4463135 11.9329102,24.3289795 C11.8234863,24.2116455 11.6804443,24.1529785 11.5037842,24.1529785 Z M17.1239502,24.2518555 L13.3903564,24.2518555 C13.197876,24.2518555 13.0508789,24.2953613 12.9493652,24.382373 C12.8478516,24.4693848 12.7970947,24.5880371 12.7970947,24.7383301 C12.7970947,24.8833496 12.845874,25.0000244 12.9434326,25.0883545 C13.0214795,25.1590186 13.1324326,25.201417 13.276292,25.2155498 L13.3903564,25.2208496 L14.6718018,25.2208496 L14.6718018,29.4330078 C14.6718018,29.6729492 14.7265137,29.8522461 14.8359375,29.9708984 C14.9453613,30.0895508 15.087085,30.148877 15.2611084,30.148877 C15.4324951,30.148877 15.5722412,30.09021 15.6803467,29.972876 C15.7668311,29.8790088 15.8187217,29.7450635 15.8360186,29.57104 L15.8425049,29.4330078 L15.8425049,25.2208496 L17.1239502,25.2208496 C17.3243408,25.2208496 17.4746338,25.1766846 17.5748291,25.0883545 C17.6750244,25.0000244 17.7251221,24.8833496 17.7251221,24.7383301 C17.7251221,24.5933105 17.6756836,24.4759766 17.5768066,24.3863281 C17.4977051,24.3146094 17.3852754,24.2715781 17.2395176,24.2572344 L17.1239502,24.2518555 Z M19.4653564,24.2518555 L19.1014893,24.2518555 C18.8773682,24.2518555 18.6993896,24.2960205 18.5675537,24.3843506 C18.4576904,24.457959 18.3936035,24.5887878 18.375293,24.7768372 L18.3697998,24.8965332 L18.3697998,29.4844238 C18.3697998,29.7085449 18.4198975,29.8753174 18.5200928,29.9847412 C18.6202881,30.094165 18.7494873,30.148877 18.9076904,30.148877 C19.071167,30.148877 19.2023438,30.0935059 19.3012207,29.9827637 C19.3803223,29.8941699 19.4277832,29.7701387 19.4436035,29.6106699 L19.4495361,29.4844238 L19.4495361,25.4818848 L20.3789795,29.1759277 C20.4106201,29.2972168 20.4416016,29.4138916 20.4719238,29.5259521 C20.5022461,29.6380127 20.5437744,29.7401855 20.5965088,29.8324707 C20.6492432,29.9247559 20.7230713,30.0005615 20.8179932,30.0598877 C20.912915,30.1192139 21.0289307,30.148877 21.16604,30.148877 C21.3479736,30.148877 21.4903564,30.1027344 21.5931885,30.0104492 C21.6960205,29.9181641 21.7665527,29.8159912 21.8047852,29.7039307 C21.8353711,29.6142822 21.8731289,29.4837119 21.9180586,29.3122197 L21.9531006,29.1759277 L22.8825439,25.4818848 L22.8825439,29.4844238 C22.8825439,29.7059082 22.9326416,29.8720215 23.0328369,29.9827637 C23.1330322,30.0935059 23.2609131,30.148877 23.4164795,30.148877 C23.5825928,30.148877 23.7150879,30.0935059 23.8139648,29.9827637 C23.8930664,29.8941699 23.9405273,29.7701387 23.9563477,29.6106699 L23.9622803,29.4844238 L23.9622803,24.8965332 C23.9622803,24.6434082 23.8963623,24.4726807 23.7645264,24.3843506 C23.6590576,24.3136865 23.5240576,24.2712881 23.3595264,24.2571553 L23.2305908,24.2518555 L22.8667236,24.2518555 C22.647876,24.2518555 22.4890137,24.2716309 22.3901367,24.3111816 C22.2912598,24.3507324 22.2180908,24.4219238 22.1706299,24.5247559 C22.1326611,24.6070215 22.0904736,24.7306309 22.0440674,24.895584 L22.0084717,25.0270508 L21.16604,28.2029785 L20.3236084,25.0270508 C20.2629639,24.7950195 20.2089111,24.6275879 20.1614502,24.5247559 C20.1139893,24.4219238 20.0408203,24.3507324 19.9419434,24.3111816 C19.8628418,24.279541 19.7453496,24.2605566 19.5894668,24.2542285 L19.4653564,24.2518555 Z M25.7262451,24.1529785 C25.5522217,24.1529785 25.4118164,24.2123047 25.3050293,24.330957 C25.2195996,24.4258789 25.1683418,24.5587695 25.1512559,24.7296289 L25.1448486,24.8648926 L25.1448486,29.3301758 C25.1448486,29.5833008 25.2008789,29.7665527 25.3129395,29.8799316 C25.4063232,29.9744141 25.5496033,30.0295288 25.7427795,30.0452759 L25.8646729,30.05 L28.7004639,30.05 C28.8929443,30.05 29.0392822,30.005835 29.1394775,29.9175049 C29.2396729,29.8291748 29.2897705,29.7138184 29.2897705,29.5714355 C29.2897705,29.4316895 29.2390137,29.3156738 29.1375,29.2233887 C29.0562891,29.1495605 28.9468125,29.1052637 28.8090703,29.090498 L28.7004639,29.0849609 L26.3195068,29.0849609 L26.3195068,24.8648926 C26.3195068,24.6249512 26.2647949,24.4463135 26.1553711,24.3289795 C26.0459473,24.2116455 25.9029053,24.1529785 25.7262451,24.1529785 Z" id="形状结合" fill="#E6560E"></path>\r
                <path d="M14.7992179,15.8558049 L14.6302793,15.8558049 L10.3505028,13.9451707 C10.0689385,13.8327805 9.9,13.5518049 9.9,13.2146341 C9.9,12.9336585 10.0689385,12.6526829 10.3505028,12.5402927 L14.6302793,10.6296585 C14.6865922,10.6296585 14.742905,10.5734634 14.7992179,10.5734634 C15.0807821,10.5734634 15.3060335,10.7982439 15.3060335,11.0792195 C15.3060335,11.304 15.1934078,11.4725854 15.0244693,11.5849756 L11.0825698,13.2708293 L15.0244693,14.9004878 C15.1934078,15.012878 15.3060335,15.1814634 15.3060335,15.4062439 C15.3060335,15.6872195 15.0807821,15.8558049 14.7992179,15.8558049 Z M19.5858101,9.67434146 L17.1080447,17.1482927 C16.995419,17.3730732 16.8264804,17.4854634 16.6012291,17.4854634 C16.263352,17.4854634 16.0381006,17.2606829 16.0381006,16.9797073 C16.0381006,16.9235122 16.0944134,16.8673171 16.0944134,16.811122 L18.5721788,9.39336585 C18.6848045,9.16858537 18.853743,9 19.0789944,9 C19.4168715,9 19.5858101,9.22478049 19.5858101,9.5057561 L19.5858101,9.67434146 Z M25.3297207,13.9451707 L21.0499441,15.8558049 L20.8810056,15.8558049 C20.5994413,15.8558049 20.3741899,15.6872195 20.3741899,15.4062439 C20.3741899,15.1814634 20.4868156,15.012878 20.6557542,14.9004878 L24.6539665,13.2708293 L20.6557542,11.5849756 C20.4868156,11.4725854 20.3741899,11.304 20.3741899,11.0792195 C20.3741899,10.7982439 20.5994413,10.5734634 20.8810056,10.5734634 C20.9373184,10.5734634 20.9936313,10.6296585 21.0499441,10.6296585 L25.3297207,12.5402927 C25.6112849,12.6526829 25.7802235,12.9336585 25.7802235,13.2146341 C25.7802235,13.5518049 25.6112849,13.8327805 25.3297207,13.9451707 L25.3297207,13.9451707 Z" id="Fill-4" fill="#E6560E"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),I6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_ico</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-831.000000, -5384.000000)">\r
            <g id="icon_ico" transform="translate(831.000000, 5384.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#E4F2D6"></path>\r
                <path d="M21.1818182,5 C23.2905418,5 25,6.70945823 25,8.81818182 L25,15.1818182 C25,17.2905418 23.2905418,19 21.1818182,19 L14.8181818,19 C12.7094582,19 11,17.2905418 11,15.1818182 L11,8.81818182 C11,6.70945823 12.7094582,5 14.8181818,5 L21.1818182,5 Z M20.8636364,5.31818182 L15.1363636,5.31818182 C13.02764,5.31818182 11.3181818,7.02764005 11.3181818,9.13636364 L11.3181818,9.13636364 L11.3181818,14.8636364 C11.3181818,16.97236 13.02764,18.6818182 15.1363636,18.6818182 L15.1363636,18.6818182 L20.8636364,18.6818182 C22.97236,18.6818182 24.6818182,16.97236 24.6818182,14.8636364 L24.6818182,14.8636364 L24.6818182,9.13636364 C24.6818182,7.02764005 22.97236,5.31818182 20.8636364,5.31818182 L20.8636364,5.31818182 Z M18,5.95454545 C21.3388124,5.95454545 24.0454545,8.66118765 24.0454545,12 C24.0454545,15.3388124 21.3388124,18.0454545 18,18.0454545 C14.6611876,18.0454545 11.9545455,15.3388124 11.9545455,12 C11.9545455,8.66118765 14.6611876,5.95454545 18,5.95454545 Z M18,6.27272727 C14.8369146,6.27272727 12.2727273,8.83691461 12.2727273,12 C12.2727273,15.1630854 14.8369146,17.7272727 18,17.7272727 C21.1630854,17.7272727 23.7272727,15.1630854 23.7272727,12 C23.7272727,8.83691461 21.1630854,6.27272727 18,6.27272727 Z M18,7.86363636 C20.2844506,7.86363636 22.1363636,9.71554944 22.1363636,12 C22.1363636,14.2844506 20.2844506,16.1363636 18,16.1363636 C15.7155494,16.1363636 13.8636364,14.2844506 13.8636364,12 C13.8636364,9.71554944 15.7155494,7.86363636 18,7.86363636 Z M18,8.18181818 C15.8912764,8.18181818 14.1818182,9.89127641 14.1818182,12 C14.1818182,14.1087236 15.8912764,15.8181818 18,15.8181818 C20.1087236,15.8181818 21.8181818,14.1087236 21.8181818,12 C21.8181818,9.89127641 20.1087236,8.18181818 18,8.18181818 Z M18,9.13636364 C19.5815427,9.13636364 20.8636364,10.4184573 20.8636364,12 C20.8636364,13.5815427 19.5815427,14.8636364 18,14.8636364 C16.4184573,14.8636364 15.1363636,13.5815427 15.1363636,12 C15.1363636,10.4184573 16.4184573,9.13636364 18,9.13636364 Z M18,9.45454545 C16.5941843,9.45454545 15.4545455,10.5941843 15.4545455,12 C15.4545455,13.4058157 16.5941843,14.5454545 18,14.5454545 C19.4058157,14.5454545 20.5454545,13.4058157 20.5454545,12 C20.5454545,10.5941843 19.4058157,9.45454545 18,9.45454545 Z" id="形状结合" fill="#77BC31"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#BBDD98"></path>\r
                <rect id="Rectangle-9" fill="#77BC31" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="ICO" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="8.1" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="10.5268799" y="30.05">ICO</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),z6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_img</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-897.000000, -5214.000000)">\r
            <g id="icon_img" transform="translate(897.000000, 5214.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#E0F5E6"></path>\r
                <path d="M12.6,17.547774 C12.6,18.0460597 13.0101849,18.45 13.502226,18.45 L22.497774,18.45 C22.9960597,18.45 23.4,18.0398151 23.4,17.547774 L23.4,8.55222597 C23.4,8.05394032 22.9898151,7.65 22.497774,7.65 L13.502226,7.65 C13.0039403,7.65 12.6,8.06018486 12.6,8.55222597 L12.6,17.547774 Z M13.6851675,14.5739038 C13.6851675,14.9451874 13.9923018,15.2461722 14.3623228,15.2461722 L21.6376772,15.2461722 C22.0116598,15.2461722 22.3148325,14.9494889 22.3148325,14.5739038 L22.3148325,9.40743587 C22.3148325,9.03615228 22.0076982,8.73516746 21.6376772,8.73516746 L14.3623228,8.73516746 C13.9883402,8.73516746 13.6851675,9.03185077 13.6851675,9.40743587 L13.6851675,14.5739038 Z M17.354067,13.0758373 L19.369378,10.3370813 L20.0411483,11.4222488 L20.7129187,11.2155502 L21.2296651,14.1093301 L14.4602871,14.1093301 L16.2688995,12.404067 L17.354067,13.0758373 Z M15.3904306,11.1638756 C14.9253589,11.1638756 14.5119617,10.8538278 14.5119617,10.388756 C14.5119617,9.97535885 14.9253589,9.61363636 15.3904306,9.61363636 C15.8555024,9.61363636 16.2172249,9.97535885 16.2172249,10.388756 C16.2172249,10.8538278 15.8555024,11.1638756 15.3904306,11.1638756 L15.3904306,11.1638756 Z" id="Fill-4" fill="#66CC80"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#B2E5BF"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M11.4128174,24.1529785 C11.2414307,24.1529785 11.1016846,24.2123047 10.9935791,24.330957 C10.9070947,24.4258789 10.8552041,24.5587695 10.8379072,24.7296289 L10.8314209,24.8648926 L10.8314209,29.4330078 C10.8314209,29.6703125 10.8861328,29.8489502 10.9955566,29.9689209 C11.1049805,30.0888916 11.2440674,30.148877 11.4128174,30.148877 C11.5894775,30.148877 11.7325195,30.0895508 11.8419434,29.9708984 C11.9294824,29.8759766 11.9820059,29.7422422 11.9995137,29.5696953 L12.0060791,29.4330078 L12.0060791,24.8648926 C12.0060791,24.6249512 11.9513672,24.4463135 11.8419434,24.3289795 C11.7325195,24.2116455 11.5894775,24.1529785 11.4128174,24.1529785 Z M14.3633057,24.2518555 L13.9994385,24.2518555 C13.7753174,24.2518555 13.5973389,24.2960205 13.4655029,24.3843506 C13.3556396,24.457959 13.2915527,24.5887878 13.2732422,24.7768372 L13.267749,24.8965332 L13.267749,29.4844238 C13.267749,29.7085449 13.3178467,29.8753174 13.418042,29.9847412 C13.5182373,30.094165 13.6474365,30.148877 13.8056396,30.148877 C13.9691162,30.148877 14.100293,30.0935059 14.1991699,29.9827637 C14.2782715,29.8941699 14.3257324,29.7701387 14.3415527,29.6106699 L14.3474854,29.4844238 L14.3474854,25.4818848 L15.2769287,29.1759277 C15.3085693,29.2972168 15.3395508,29.4138916 15.369873,29.5259521 C15.4001953,29.6380127 15.4417236,29.7401855 15.494458,29.8324707 C15.5471924,29.9247559 15.6210205,30.0005615 15.7159424,30.0598877 C15.8108643,30.1192139 15.9268799,30.148877 16.0639893,30.148877 C16.2459229,30.148877 16.3883057,30.1027344 16.4911377,30.0104492 C16.5939697,29.9181641 16.664502,29.8159912 16.7027344,29.7039307 C16.7103809,29.6815186 16.7184756,29.6565488 16.7270186,29.6290215 L16.7836553,29.433166 L16.8160078,29.3122197 L16.8160078,29.3122197 L16.8510498,29.1759277 L17.7804932,25.4818848 L17.7804932,29.4844238 C17.7804932,29.7059082 17.8305908,29.8720215 17.9307861,29.9827637 C18.0309814,30.0935059 18.1588623,30.148877 18.3144287,30.148877 C18.480542,30.148877 18.6130371,30.0935059 18.7119141,29.9827637 C18.7910156,29.8941699 18.8384766,29.7701387 18.8542969,29.6106699 L18.8602295,29.4844238 L18.8602295,24.8965332 C18.8602295,24.6434082 18.7943115,24.4726807 18.6624756,24.3843506 C18.5570068,24.3136865 18.4220068,24.2712881 18.2574756,24.2571553 L18.12854,24.2518555 L17.7646729,24.2518555 C17.5458252,24.2518555 17.3869629,24.2716309 17.2880859,24.3111816 C17.189209,24.3507324 17.11604,24.4219238 17.0685791,24.5247559 C17.0306104,24.6070215 16.9884229,24.7306309 16.9420166,24.895584 L16.9064209,25.0270508 L16.0639893,28.2029785 L15.2215576,25.0270508 C15.1609131,24.7950195 15.1068604,24.6275879 15.0593994,24.5247559 C15.0119385,24.4219238 14.9387695,24.3507324 14.8398926,24.3111816 C14.760791,24.279541 14.6432988,24.2605566 14.487416,24.2542285 L14.3633057,24.2518555 Z M22.8509033,24.1529785 C22.3762939,24.1529785 21.9517822,24.2221924 21.5773682,24.3606201 C21.2029541,24.4990479 20.8852295,24.6987793 20.6241943,24.9598145 C20.3631592,25.2208496 20.1654053,25.5385742 20.0309326,25.9129883 C19.89646,26.2874023 19.8292236,26.7000488 19.8292236,27.1509277 C19.8292236,27.6097168 19.8984375,28.025 20.0368652,28.3967773 C20.175293,28.7685547 20.3737061,29.0849609 20.6321045,29.3459961 C20.8905029,29.6070313 21.2029541,29.8061035 21.569458,29.9432129 C21.9359619,30.0803223 22.3420166,30.148877 22.7876221,30.148877 C23.1699463,30.148877 23.5232666,30.1053711 23.847583,30.0183594 C24.1718994,29.9313477 24.5093994,29.7929199 24.860083,29.6030762 C24.9813721,29.5371582 25.0809082,29.4686035 25.1586914,29.3974121 C25.2364746,29.3262207 25.2898682,29.24646 25.3188721,29.1581299 C25.340625,29.0918823 25.3542206,29.0089493 25.3596588,28.9093307 L25.3623779,28.8041504 L25.3623779,27.6927734 C25.3623779,27.4923828 25.3274414,27.3420898 25.2575684,27.2418945 C25.1876953,27.1416992 25.0960693,27.0770996 24.9826904,27.0480957 C24.9071045,27.0287598 24.8203857,27.0158691 24.7225342,27.0094238 L24.5674072,27.0045898 L23.2345459,27.0045898 C23.0737061,27.0045898 22.9464844,27.0467773 22.8528809,27.1311523 C22.7592773,27.2155273 22.7124756,27.3223145 22.7124756,27.4515137 C22.7124756,27.6097168 22.7685059,27.7217773 22.8805664,27.7876953 C22.9702148,27.8404297 23.0940352,27.8720703 23.2520273,27.8826172 L23.3769287,27.8865723 L24.2865967,27.8865723 L24.2865967,28.8278809 C24.0413818,28.9597168 23.7994629,29.0612305 23.5608398,29.1324219 C23.3222168,29.2036133 23.0671143,29.239209 22.7955322,29.239209 C22.2365479,29.239209 21.8008301,29.055957 21.4883789,28.6894531 C21.1759277,28.3229492 21.0197021,27.7995605 21.0197021,27.1192871 C21.0197021,26.8055176 21.0599121,26.5194336 21.140332,26.2610352 C21.220752,26.0026367 21.3367676,25.7844482 21.4883789,25.6064697 C21.6399902,25.4284912 21.822583,25.2927002 22.0361572,25.1990967 C22.2497314,25.1054932 22.4923096,25.0586914 22.7638916,25.0586914 C23.0275635,25.0586914 23.2444336,25.0982422 23.414502,25.1773438 C23.5845703,25.2564453 23.7197021,25.3540039 23.8198975,25.4700195 C23.9200928,25.5860352 24.0427002,25.7574219 24.1877197,25.9841797 C24.2404541,26.0553711 24.3057129,26.1107422 24.3834961,26.150293 C24.4612793,26.1898438 24.5423584,26.2096191 24.6267334,26.2096191 C24.7796631,26.2096191 24.9108398,26.1575439 25.0202637,26.0533936 C25.1296875,25.9492432 25.1843994,25.8233398 25.1843994,25.6756836 C25.1843994,25.5385742 25.1369385,25.3816895 25.0420166,25.2050293 C24.9470947,25.0283691 24.8060303,24.8609375 24.6188232,24.7027344 C24.4316162,24.5445313 24.1877197,24.4133545 23.8871338,24.3092041 C23.5865479,24.2050537 23.2411377,24.1529785 22.8509033,24.1529785 Z" id="形状结合" fill="#66CC80"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),N6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_iso</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-246.000000, -5331.000000)">\r
            <g id="icon_iso" transform="translate(246.000000, 5331.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#DDE2E4"></path>\r
                <path d="M18,7.2 C21.2308658,7.2 23.85,9.81913421 23.85,13.05 C23.85,16.2808658 21.2308658,18.9 18,18.9 C14.7691342,18.9 12.15,16.2808658 12.15,13.05 C12.15,9.81913421 14.7691342,7.2 18,7.2 Z M18,11.7 C17.2544156,11.7 16.65,12.3044156 16.65,13.05 C16.65,13.7955844 17.2544156,14.4 18,14.4 C18.7455844,14.4 19.35,13.7955844 19.35,13.05 C19.35,12.3044156 18.7455844,11.7 18,11.7 Z" id="Combined-Shape" stroke="#546E7A" stroke-width="0.9" fill-opacity="0.1" fill="#546E7A"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#A9B6BC"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M12.0851807,24.1529785 C11.9137939,24.1529785 11.7740479,24.2123047 11.6659424,24.330957 C11.579458,24.4258789 11.5275674,24.5587695 11.5102705,24.7296289 L11.5037842,24.8648926 L11.5037842,29.4330078 C11.5037842,29.6703125 11.5584961,29.8489502 11.6679199,29.9689209 C11.7773437,30.0888916 11.9164307,30.148877 12.0851807,30.148877 C12.2618408,30.148877 12.4048828,30.0895508 12.5143066,29.9708984 C12.6018457,29.8759766 12.6543691,29.7422422 12.671877,29.5696953 L12.6784424,29.4330078 L12.6784424,24.8648926 C12.6784424,24.6249512 12.6237305,24.4463135 12.5143066,24.3289795 C12.4048828,24.2116455 12.2618408,24.1529785 12.0851807,24.1529785 Z M16.0798096,24.1529785 C15.6342041,24.1529785 15.2466064,24.2195557 14.9170166,24.35271 C14.5874268,24.4858643 14.3356201,24.6763672 14.1615967,24.9242188 C13.9875732,25.1720703 13.9005615,25.4555176 13.9005615,25.7745605 C13.9005615,26.1094238 13.982959,26.3895752 14.1477539,26.6150146 C14.3125488,26.8404541 14.5353516,27.0184326 14.8161621,27.1489502 C15.0969727,27.2794678 15.4456787,27.3935059 15.8622803,27.4910645 C16.1734131,27.5622559 16.4219238,27.6294922 16.6078125,27.6927734 C16.7937012,27.7560547 16.9453125,27.8476807 17.0626465,27.9676514 C17.1799805,28.0876221 17.2386475,28.2438477 17.2386475,28.4363281 C17.2386475,28.6789063 17.1298828,28.8812744 16.9123535,29.0434326 C16.6948242,29.2055908 16.4120361,29.2866699 16.0639893,29.2866699 C15.8108643,29.2866699 15.6065186,29.24646 15.4509521,29.16604 C15.2953857,29.0856201 15.1747559,28.9834473 15.0890625,28.8595215 C15.0033691,28.7355957 14.9236084,28.5813477 14.8497803,28.3967773 C14.7891357,28.2412109 14.7159668,28.123877 14.6302734,28.0447754 C14.5445801,27.9656738 14.4397705,27.926123 14.3158447,27.926123 C14.162915,27.926123 14.0370117,27.9768799 13.9381348,28.0783936 C13.8392578,28.1799072 13.7898193,28.3018555 13.7898193,28.4442383 C13.7898193,28.6894531 13.8722168,28.9419189 14.0370117,29.2016357 C14.2018066,29.4613525 14.41604,29.6689941 14.6797119,29.8245605 C15.0514893,30.0407715 15.5234619,30.148877 16.0956299,30.148877 C16.572876,30.148877 16.9855225,30.0697754 17.3335693,29.9115723 C17.6816162,29.7533691 17.9459473,29.5345215 18.1265625,29.2550293 C18.3071777,28.9755371 18.3974854,28.6604492 18.3974854,28.3097656 C18.3974854,28.0170898 18.3454102,27.7698975 18.2412598,27.5681885 C18.1371094,27.3664795 17.9920898,27.199707 17.8062012,27.0678711 C17.6203125,26.9360352 17.394873,26.8239746 17.1298828,26.7316895 C16.8648926,26.6394043 16.5689209,26.5550293 16.2419678,26.4785645 C15.9809326,26.4126465 15.7937256,26.3625488 15.6803467,26.3282715 C15.5669678,26.2939941 15.4549072,26.2465332 15.344165,26.1858887 C15.2334229,26.1252441 15.1464111,26.0527344 15.0831299,25.9683594 C15.0198486,25.8839844 14.988208,25.7837891 14.988208,25.6677734 C14.988208,25.4805664 15.0811523,25.3203857 15.267041,25.1872314 C15.4529297,25.0540771 15.6974854,24.9875 16.000708,24.9875 C16.3276611,24.9875 16.5649658,25.0488037 16.7126221,25.1714111 C16.8602783,25.2940186 16.9868408,25.4647461 17.0923096,25.6835938 C17.1740479,25.8365234 17.2498535,25.9466064 17.3197266,26.0138428 C17.3895996,26.0810791 17.4917725,26.1146973 17.6262451,26.1146973 C17.7739014,26.1146973 17.897168,26.058667 17.9960449,25.9466064 C18.0949219,25.8345459 18.1443604,25.7086426 18.1443604,25.5688965 C18.1443604,25.4159668 18.1048096,25.259082 18.025708,25.0982422 C17.9466064,24.9374023 17.8213623,24.7838135 17.6499756,24.6374756 C17.4785889,24.4911377 17.2630371,24.3738037 17.0033203,24.2854736 C16.7436035,24.1971436 16.4357666,24.1529785 16.0798096,24.1529785 Z M21.9451904,24.1529785 C21.5022217,24.1529785 21.1067139,24.2221924 20.758667,24.3606201 C20.4106201,24.4990479 20.1153076,24.6987793 19.8727295,24.9598145 C19.6301514,25.2208496 19.4449219,25.5372559 19.317041,25.9090332 C19.1891602,26.2808105 19.1252197,26.689502 19.1252197,27.1351074 C19.1252197,27.570166 19.1865234,27.9749023 19.3091309,28.3493164 C19.4317383,28.7237305 19.6130127,29.0440918 19.8529541,29.3104004 C20.0928955,29.576709 20.3921631,29.7830322 20.7507568,29.9293701 C21.1093506,30.075708 21.5154053,30.148877 21.9689209,30.148877 C22.4250732,30.148877 22.8298096,30.0776855 23.1831299,29.9353027 C23.5364502,29.7929199 23.8343994,29.5872559 24.0769775,29.3183105 C24.3195557,29.0493652 24.5014893,28.7316406 24.6227783,28.3651367 C24.7440674,27.9986328 24.8047119,27.5912598 24.8047119,27.1430176 C24.8047119,26.5365723 24.6939697,26.0085693 24.4724854,25.5590088 C24.251001,25.1094482 23.924707,24.7627197 23.4936035,24.5188232 C23.0625,24.2749268 22.5463623,24.1529785 21.9451904,24.1529785 Z M21.9451904,25.086377 C22.2747803,25.086377 22.5667969,25.1661377 22.8212402,25.3256592 C23.0756836,25.4851807 23.27146,25.7205078 23.4085693,26.0316406 C23.5456787,26.3427734 23.6142334,26.7105957 23.6142334,27.1351074 C23.6142334,27.5938965 23.5397461,27.9801758 23.3907715,28.2939453 C23.2417969,28.6077148 23.0414062,28.8397461 22.7895996,28.9900391 C22.537793,29.140332 22.2615967,29.2154785 21.9610107,29.2154785 C21.7263428,29.2154785 21.510791,29.1719727 21.3143555,29.0849609 C21.1179199,28.9979492 20.9438965,28.8667725 20.7922852,28.6914307 C20.6406738,28.5160889 20.5233398,28.2952637 20.4402832,28.0289551 C20.3572266,27.7626465 20.3156982,27.4646973 20.3156982,27.1351074 C20.3156982,26.8081543 20.3572266,26.513501 20.4402832,26.2511475 C20.5233398,25.9887939 20.6367187,25.7732422 20.7804199,25.6044922 C20.9241211,25.4357422 21.0955078,25.3072021 21.2945801,25.2188721 C21.4936523,25.130542 21.7105225,25.086377 21.9451904,25.086377 Z" id="形状结合" fill="#546E7A"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),R6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_java</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-564.000000, -5331.000000)">\r
            <g id="icon_java" transform="translate(564.000000, 5331.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#E4F2D6"></path>\r
                <path d="M21.9418717,9 L26,12.9984727 L21.9418717,17 L21.3931408,16.4593356 L24.9025383,13.0015273 L21.3931408,9.54066438 L21.9418717,9 Z M14.0581283,9.00305461 L14.6068592,9.54371899 L11.0974617,13.0015273 L14.6068592,16.4593356 L14.0581283,17 L10,13.0015273 L14.0581283,9.00305461 Z M20.115869,9.18327609 L20.8692114,9.36044293 L19.0556094,16.8197786 L18.302267,16.6426117 L20.115869,9.18327609 Z M17.0355556,12.371308 C17.5675556,12.371308 18,12.8111814 18,13.3523207 C18,13.8934599 17.5675556,14.3333333 17.0355556,14.3333333 C16.5035555,14.3333333 16.0711111,13.8934599 16.0711111,13.3523207 C16.0711111,12.8111814 16.5035555,12.371308 17.0355556,12.371308 Z M14.2977778,12.3333333 C14.8297778,12.3333333 15.2622222,12.7732067 15.2622222,13.314346 C15.2622222,13.8554852 14.8297777,14.2953587 14.2977778,14.2953587 C13.7657778,14.2953587 13.3333333,13.8554852 13.3333333,13.314346 C13.3333333,12.7732067 13.7657778,12.3333333 14.2977778,12.3333333 Z" id="形状结合" fill="#77BC31"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#BBDD98"></path>\r
                <rect id="Rectangle-9" fill="#77BC31" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="JAVA" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="8.1" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="7.57836914" y="30.05">JAVA</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),B6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_jpeg</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-311.000000, -5214.000000)">\r
            <g id="icon_jpeg" transform="translate(311.000000, 5214.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#E6EBEF"></path>\r
                <path d="M13.5,18.45 L22.5,18.45 C22.9970563,18.45 23.4,18.0470563 23.4,17.55 L23.4,8.55 C23.4,8.05294373 22.9970563,7.65 22.5,7.65 L13.5,7.65 C13.0029437,7.65 12.6,8.05294373 12.6,8.55 L12.6,17.55 C12.6,18.0470563 13.0029437,18.45 13.5,18.45 Z M14.3601675,15.2461722 L21.6398325,15.2461722 C22.0126247,15.2461722 22.3148325,14.9439645 22.3148325,14.5711722 L22.3148325,9.41016746 C22.3148325,9.03737526 22.0126247,8.73516746 21.6398325,8.73516746 L14.3601675,8.73516746 C13.9873753,8.73516746 13.6851675,9.03737526 13.6851675,9.41016746 L13.6851675,14.5711722 C13.6851675,14.9439645 13.9873753,15.2461722 14.3601675,15.2461722 Z M17.354067,13.0758373 L19.369378,10.3370813 L20.0411483,11.4222488 L20.7129187,11.2155502 L21.2296651,14.1093301 L14.4602871,14.1093301 L16.2688995,12.404067 L17.354067,13.0758373 Z M15.3904306,11.1638756 C14.9253589,11.1638756 14.5119617,10.8538278 14.5119617,10.388756 C14.5119617,9.97535885 14.9253589,9.61363636 15.3904306,9.61363636 C15.8555024,9.61363636 16.2172249,9.97535885 16.2172249,10.388756 C16.2172249,10.8538278 15.8555024,11.1638756 15.3904306,11.1638756 L15.3904306,11.1638756 Z" id="Fill-4" fill="#8199AE"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C2CDD7"></path>\r
                <path d="M34.3636364,21.15 C35.267375,21.15 36,21.882625 36,22.7863636 L36,30.9681818 C36,31.8719205 35.267375,32.6045455 34.3636364,32.6045455 L1.63636364,32.6045455 C0.732624955,32.6045455 9.13357262e-16,31.8719205 0,30.9681818 L0,22.7863636 C-1.10676068e-16,21.882625 0.732624955,21.15 1.63636364,21.15 L34.3636364,21.15 Z M10.4774414,24.1529785 C10.2981445,24.1529785 10.1564209,24.2123047 10.0522705,24.330957 C9.9689502,24.4258789 9.91895801,24.5587695 9.90229395,24.7296289 L9.89604492,24.8648926 L9.89604492,28.0724609 C9.89604492,28.8344727 9.63632813,29.2154785 9.11689453,29.2154785 C8.92177734,29.2154785 8.77148438,29.1719727 8.66601563,29.0849609 C8.56054688,28.9979492 8.48540039,28.8937988 8.44057617,28.7725098 C8.39575195,28.6512207 8.35092773,28.4890625 8.30610352,28.2860352 C8.26391602,28.0961914 8.20327148,27.9564453 8.12416992,27.8667969 C8.04506836,27.7771484 7.92773438,27.7323242 7.77216797,27.7323242 C7.61132813,27.7323242 7.47949219,27.7811035 7.37666016,27.8786621 C7.27382813,27.9762207 7.22241211,28.1172852 7.22241211,28.3018555 C7.22241211,28.4758789 7.2487793,28.6591309 7.30151367,28.8516113 C7.35424805,29.0440918 7.42280273,29.2141602 7.50717773,29.3618164 C7.66801758,29.6386719 7.87961426,29.8390625 8.14196777,29.9629883 C8.40432129,30.0869141 8.73193359,30.148877 9.12480469,30.148877 C9.55986328,30.148877 9.92900391,30.0598877 10.2322266,29.8819092 C10.5354492,29.7039307 10.7529785,29.4554199 10.8848145,29.136377 C10.963916,28.9465332 11.0146729,28.7593262 11.037085,28.5747559 C11.053894,28.4363281 11.0643997,28.2756531 11.068602,28.0927307 L11.0707031,27.9023926 L11.0707031,24.8648926 C11.0707031,24.6275879 11.0173096,24.4496094 10.9105225,24.330957 C10.8037354,24.2123047 10.659375,24.1529785 10.4774414,24.1529785 Z M14.5946777,24.2518555 L13.087793,24.2518555 C12.8293945,24.2518555 12.6408691,24.3072266 12.5222168,24.4179688 C12.4233398,24.5102539 12.3656616,24.6538086 12.3491821,24.8486328 L12.3442383,24.9716797 L12.3442383,29.440918 C12.3442383,29.6729492 12.398291,29.8489502 12.5063965,29.9689209 C12.614502,30.0888916 12.7568848,30.148877 12.9335449,30.148877 C13.1022949,30.148877 13.242041,30.0882324 13.3527832,29.9669434 C13.441377,29.8699121 13.4945332,29.7365996 13.512252,29.5670059 L13.5188965,29.4330078 L13.5188965,27.7916504 L14.5946777,27.7916504 C15.314502,27.7916504 15.8563477,27.6393799 16.2202148,27.3348389 C16.584082,27.0302979 16.7660156,26.5827148 16.7660156,25.9920898 C16.7660156,25.7152344 16.7211914,25.4660645 16.631543,25.2445801 C16.5418945,25.0230957 16.4107178,24.8358887 16.2380127,24.682959 C16.0653076,24.5300293 15.8603027,24.4206055 15.622998,24.3546875 C15.4230469,24.2975586 15.1516846,24.2642334 14.8089111,24.2547119 L14.5946777,24.2518555 Z M25.9338867,24.1529785 C25.4592773,24.1529785 25.0347656,24.2221924 24.6603516,24.3606201 C24.2859375,24.4990479 23.9682129,24.6987793 23.7071777,24.9598145 C23.4461426,25.2208496 23.2483887,25.5385742 23.113916,25.9129883 C22.9794434,26.2874023 22.912207,26.7000488 22.912207,27.1509277 C22.912207,27.6097168 22.9814209,28.025 23.1198486,28.3967773 C23.2582764,28.7685547 23.4566895,29.0849609 23.7150879,29.3459961 C23.9734863,29.6070313 24.2859375,29.8061035 24.6524414,29.9432129 C25.0189453,30.0803223 25.425,30.148877 25.8706055,30.148877 C26.2529297,30.148877 26.60625,30.1053711 26.9305664,30.0183594 C27.2548828,29.9313477 27.5923828,29.7929199 27.9430664,29.6030762 C28.0643555,29.5371582 28.1638916,29.4686035 28.2416748,29.3974121 C28.319458,29.3262207 28.3728516,29.24646 28.4018555,29.1581299 C28.4236084,29.0918823 28.437204,29.0089493 28.4426422,28.9093307 L28.4453613,28.8041504 L28.4453613,27.6927734 C28.4453613,27.4923828 28.4104248,27.3420898 28.3405518,27.2418945 C28.2706787,27.1416992 28.1790527,27.0770996 28.0656738,27.0480957 C27.9900879,27.0287598 27.9033691,27.0158691 27.8055176,27.0094238 L27.6503906,27.0045898 L26.3175293,27.0045898 C26.1566895,27.0045898 26.0294678,27.0467773 25.9358643,27.1311523 C25.8422607,27.2155273 25.795459,27.3223145 25.795459,27.4515137 C25.795459,27.6097168 25.8514893,27.7217773 25.9635498,27.7876953 C26.0531982,27.8404297 26.1770186,27.8720703 26.3350107,27.8826172 L26.4599121,27.8865723 L27.3695801,27.8865723 L27.3695801,28.8278809 C27.1243652,28.9597168 26.8824463,29.0612305 26.6438232,29.1324219 C26.4052002,29.2036133 26.1500977,29.239209 25.8785156,29.239209 C25.3195313,29.239209 24.8838135,29.055957 24.5713623,28.6894531 C24.2589111,28.3229492 24.1026855,27.7995605 24.1026855,27.1192871 C24.1026855,26.8055176 24.1428955,26.5194336 24.2233154,26.2610352 C24.3037354,26.0026367 24.419751,25.7844482 24.5713623,25.6064697 C24.7229736,25.4284912 24.9055664,25.2927002 25.1191406,25.1990967 C25.3327148,25.1054932 25.575293,25.0586914 25.846875,25.0586914 C26.1105469,25.0586914 26.327417,25.0982422 26.4974854,25.1773438 C26.6675537,25.2564453 26.8026855,25.3540039 26.9028809,25.4700195 C27.0030762,25.5860352 27.1256836,25.7574219 27.2707031,25.9841797 C27.3234375,26.0553711 27.3886963,26.1107422 27.4664795,26.150293 C27.5442627,26.1898438 27.6253418,26.2096191 27.7097168,26.2096191 C27.8626465,26.2096191 27.9938232,26.1575439 28.1032471,26.0533936 C28.2126709,25.9492432 28.2673828,25.8233398 28.2673828,25.6756836 C28.2673828,25.5385742 28.2199219,25.3816895 28.125,25.2050293 C28.0300781,25.0283691 27.8890137,24.8609375 27.7018066,24.7027344 C27.5145996,24.5445313 27.2707031,24.4133545 26.9701172,24.3092041 C26.6695313,24.2050537 26.3241211,24.1529785 25.9338867,24.1529785 Z M21.5674805,24.2518555 L18.4666992,24.2518555 C18.3005859,24.2518555 18.1641357,24.2762451 18.0573486,24.3250244 C17.9505615,24.3738037 17.8721191,24.4515869 17.8220215,24.558374 C17.7844482,24.6384644 17.760965,24.7359818 17.7515717,24.8509262 L17.746875,24.9716797 L17.746875,29.3301758 C17.746875,29.5833008 17.8029053,29.7665527 17.9149658,29.8799316 C18.0083496,29.9744141 18.1516296,30.0295288 18.3448059,30.0452759 L18.4666992,30.05 L21.6584473,30.05 C21.8430176,30.05 21.9821045,30.0071533 22.075708,29.92146 C22.1693115,29.8357666 22.2161133,29.7243652 22.2161133,29.5872559 C22.2161133,29.444873 22.1693115,29.330835 22.075708,29.2451416 C22.0008252,29.1765869 21.896833,29.1354541 21.7637314,29.1217432 L21.6584473,29.1166016 L18.9215332,29.1166016 L18.9215332,27.467334 L21.3578613,27.467334 C21.5397949,27.467334 21.6742676,27.4258057 21.7612793,27.342749 C21.848291,27.2596924 21.8917969,27.1522461 21.8917969,27.0204102 C21.8917969,26.8885742 21.8476318,26.7824463 21.7593018,26.7020264 C21.6886377,26.6376904 21.5888643,26.5990889 21.4599814,26.5862217 L21.3578613,26.5813965 L18.9215332,26.5813965 L18.9215332,25.1575684 L21.5674805,25.1575684 C21.7546875,25.1575684 21.8937744,25.11604 21.9847412,25.0329834 C22.075708,24.9499268 22.1211914,24.8398438 22.1211914,24.7027344 C22.1211914,24.5682617 22.075708,24.4594971 21.9847412,24.3764404 C21.9119678,24.3099951 21.8083975,24.2701279 21.6740303,24.2568389 L21.5674805,24.2518555 Z M14.3099121,25.1338379 C14.8346191,25.1338379 15.1813477,25.2195313 15.3500977,25.390918 C15.5003906,25.5517578 15.5755371,25.7600586 15.5755371,26.0158203 C15.5755371,26.2293945 15.5267578,26.4020996 15.4291992,26.5339355 C15.3316406,26.6657715 15.189917,26.7606934 15.0040283,26.8187012 C14.8181396,26.876709 14.5867676,26.9057129 14.3099121,26.9057129 L13.5188965,26.9057129 L13.5188965,25.1338379 L14.3099121,25.1338379 Z" id="形状结合" fill="#8199AE"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),V6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_cad备份 2</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-375.000000, -5331.000000)">\r
            <g id="icon_cad备份-2" transform="translate(375.000000, 5331.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#E6EBEF"></path>\r
                <g id="js" transform="translate(12.000000, 4.000000)" fill="#8199AE">\r
                    <path d="M8.79994012,8.94478439 C8.5843874,9.06887217 8.28261358,9.1345657 7.90898885,9.1345657 C7.48506848,9.1345657 7.17610957,9.05427361 6.97492703,8.90828798 C6.75218921,8.74040451 6.61567248,8.45573254 6.55819175,8.06157135 L5.7247212,8.06157135 C5.76064666,8.72580595 5.99775466,9.20755852 6.42886012,9.51412833 C6.78092957,9.7623039 7.27670085,9.88639168 7.90898885,9.88639168 C8.56283212,9.88639168 9.07297358,9.74770534 9.43222813,9.48493121 C9.79148267,9.2148578 9.97110995,8.84259445 9.97110995,8.37544045 C9.97110995,7.89368788 9.74837213,7.52142453 9.30289649,7.25135112 C9.10171394,7.13456262 8.65623831,6.95937987 7.95928448,6.74040143 C7.48506848,6.58711652 7.19047975,6.4776273 7.08270339,6.41923305 C6.8384103,6.28784598 6.72344884,6.10536395 6.72344884,5.88638551 C6.72344884,5.63820995 6.82404012,5.45572791 7.03959284,5.34623869 C7.21203503,5.25134803 7.45632812,5.20755235 7.77965721,5.20755235 C8.15328194,5.20755235 8.44068558,5.27324588 8.62749794,5.41923151 C8.81431031,5.55791785 8.95082703,5.79149485 9.02267794,6.11266323 L9.85614849,6.11266323 C9.80585286,5.54331929 9.59748522,5.11996097 9.23823067,4.84988756 C8.9005314,4.59441271 8.4263154,4.47032493 7.82276776,4.47032493 C7.26951575,4.47032493 6.81685503,4.59441271 6.45760048,4.84988756 C6.06960557,5.11266169 5.8827932,5.47762576 5.8827932,5.93748048 C5.8827932,6.3973352 6.07679066,6.74770071 6.47197066,6.98857699 C6.62285757,7.07616837 7.01803757,7.22215399 7.65032557,7.42653387 C8.21794776,7.60171662 8.54846194,7.71850513 8.64905321,7.76960009 C8.96519722,7.93018428 9.13045431,8.14916272 9.13045431,8.42653541 C9.13045431,8.64551385 9.01549285,8.81339733 8.79994012,8.94478439 L8.79994012,8.94478439 Z M11.294963,2.89139914 L6.70773146,0.192946071 C6.26892725,-0.064315357 5.72837834,-0.064315357 5.28957413,0.192946071 L0.705037048,2.89185534 C0.269320832,3.1495765 0.000932671945,3.62281279 0,4.1350142 L0,9.85628215 C0,10.3749873 0.276626001,10.8544589 0.721652571,11.1090213 L2.37467255,12.0524534 C2.39263528,12.0624899 2.41239428,12.0634023 2.43125515,12.0711578 C2.65084949,12.1797346 2.91220717,12.2353917 3.2377816,12.2353917 C3.83414415,12.2353917 4.29533718,12.0141322 4.56837063,11.7075624 C4.81266372,11.4155911 4.93481027,10.9776342 4.93481027,10.3790932 L4.93481027,4.57251487 L4.09415463,4.57251487 L4.09415463,10.349896 C4.09415463,10.7221594 4.02948881,10.9922328 3.90015717,11.1601163 C3.77082554,11.3279997 3.5624579,11.4155911 3.28223935,11.4155911 C3.24451763,11.4155911 3.0204326,11.3571969 2.84439787,11.2814668 L2.84215253,11.2810106 C2.83182397,11.2741675 2.82553701,11.2632186 2.81475937,11.257288 L1.16218846,10.3138558 C0.999372881,10.2206551 0.89850038,10.0456797 0.898136367,9.85582594 L0.898136367,4.1350142 C0.898136367,3.94842632 0.996931367,3.77461218 1.15545244,3.68109014 L5.73954045,0.98263707 C5.89966597,0.888392206 6.09719055,0.888392206 6.25731607,0.98263707 L10.8445476,3.68109014 C11.0035177,3.77506839 11.1023127,3.94888252 11.1023127,4.1354704 L11.1023127,9.87361794 C11.1023127,10.0570124 11.005763,10.2294579 10.8508345,10.3234362 L6.41987875,13.0132213 C6.26104351,13.109734 6.06351339,13.1125186 5.90210314,13.0205206 L4.95950902,12.4821986 C4.74324124,12.3586146 4.46930425,12.436536 4.34765362,12.6562408 C4.22600299,12.8759457 4.30270535,13.1542363 4.51897313,13.2778203 L5.46201632,13.8161423 C5.90407258,14.068119 6.4450346,14.0606361 6.88017364,13.7965255 L11.3111294,11.1067403 C11.7374109,10.8465504 11.9987004,10.3788255 12,9.87361794 L12,4.1350142 C11.9995936,3.62254993 11.7310866,3.14893021 11.294963,2.89139914 Z" id="形状"></path>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C2CDD7"></path>\r
                <rect id="Rectangle-9" fill="#8199AE" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="JS" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="8.1" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="12.9770508" y="30.05">JS</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),W6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_link</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-246.000000, -5275.000000)">\r
            <g id="icon_link" transform="translate(246.000000, 5275.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#E0F5E6"></path>\r
                <path d="M22.2009425,7.98949367 C21.1436122,6.93683544 19.431744,6.93683544 18.3744136,7.98949367 L16.8135926,9.54341772 C15.8066113,10.5459494 15.8066113,12.2502532 16.8135926,13.3029114 C17.0653379,13.5535443 17.4681304,13.5535443 17.7198758,13.3029114 C17.9716211,13.0522785 17.9716211,12.7013924 17.7198758,12.4507595 C17.166036,11.8492405 17.166036,10.946962 17.7198758,10.3955696 L19.2806968,8.84164557 C19.8345365,8.29025316 20.7408196,8.29025316 21.2946594,8.84164557 C21.8484991,9.44316456 21.8484991,10.345443 21.2946594,10.8968354 L20.5897724,11.6487342 C20.7408196,12.15 20.8415178,12.6512658 20.7911687,13.1525316 L22.1505935,11.7991139 C23.2079238,10.7464557 23.2079238,9.0421519 22.2009425,7.98949367 Z M18.2737155,10.9970886 C18.0723192,11.1975949 18.0723192,11.5986076 18.2737155,11.8492405 C18.8779042,12.4006329 18.8779042,13.353038 18.2737155,13.9044304 L16.7632435,15.4082278 C16.2094038,15.9596203 15.3031206,15.9596203 14.6989319,15.4082278 C14.1450922,14.8568354 14.1450922,13.954557 14.6989319,13.4031646 L15.4541678,12.6512658 C15.2527716,12.15 15.2024225,11.6487342 15.2527716,11.0973418 L13.8429978,12.5008861 C12.7856674,13.5535443 12.7856674,15.2578481 13.8429978,16.3105063 C14.9003281,17.3631646 16.6121963,17.3631646 17.6191776,16.3105063 L19.1799986,14.7565823 C20.237329,13.7039241 20.237329,12.0497468 19.1799986,10.9970886 C18.9282533,10.7464557 18.5254608,10.7464557 18.2737155,10.9970886 L18.2737155,10.9970886 Z" id="Fill-4" fill="#66CC80"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#B2E5BF"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M14.3652832,24.1529785 C14.1938965,24.1529785 14.0541504,24.2123047 13.9460449,24.330957 C13.8595605,24.4258789 13.8076699,24.5587695 13.790373,24.7296289 L13.7838867,24.8648926 L13.7838867,29.4330078 C13.7838867,29.6703125 13.8385986,29.8489502 13.9480225,29.9689209 C14.0574463,30.0888916 14.1965332,30.148877 14.3652832,30.148877 C14.5419434,30.148877 14.6849854,30.0895508 14.7944092,29.9708984 C14.8819482,29.8759766 14.9344717,29.7422422 14.9519795,29.5696953 L14.9585449,29.4330078 L14.9585449,24.8648926 C14.9585449,24.6249512 14.903833,24.4463135 14.7944092,24.3289795 C14.6849854,24.2116455 14.5419434,24.1529785 14.3652832,24.1529785 Z M20.5945312,24.1529785 C20.4310547,24.1529785 20.3011963,24.2090088 20.2049561,24.3210693 C20.1279639,24.4107178 20.0817686,24.5362256 20.0663701,24.6975928 L20.0605957,24.8253418 L20.0605957,28.2741699 L17.8022461,24.8569824 C17.7389648,24.7567871 17.6776611,24.6605469 17.618335,24.5682617 C17.5590088,24.4759766 17.501001,24.4008301 17.4443115,24.3428223 C17.3876221,24.2848145 17.3217041,24.2386719 17.2465576,24.2043945 C17.1714111,24.1701172 17.0811035,24.1529785 16.9756348,24.1529785 C16.8411621,24.1529785 16.715918,24.1905518 16.5999023,24.2656982 C16.4838867,24.3408447 16.4008301,24.4377441 16.3507324,24.5563965 C16.3190918,24.6374756 16.2993164,24.7415436 16.2914062,24.8686005 L16.2874512,25.0033203 L16.2874512,29.4765137 C16.2874512,29.6953613 16.3375488,29.8621338 16.4377441,29.9768311 C16.5379395,30.0915283 16.6710937,30.148877 16.837207,30.148877 C16.9980469,30.148877 17.1298828,30.0921875 17.2327148,29.9788086 C17.3149805,29.8881055 17.3643398,29.7628086 17.380793,29.602918 L17.3869629,29.4765137 L17.3869629,26.0791016 L19.5899414,29.4567383 C19.6532227,29.5490234 19.7165039,29.6393311 19.7797852,29.7276611 C19.8430664,29.8159912 19.9116211,29.8931152 19.9854492,29.9590332 C20.0592773,30.0249512 20.1383789,30.0730713 20.2227539,30.1033936 C20.3071289,30.1337158 20.4020508,30.148877 20.5075195,30.148877 C20.8942383,30.148877 21.1037109,29.9350739 21.1359375,29.5074677 L21.140332,29.3855469 L21.140332,24.8253418 C21.140332,24.6012207 21.0915527,24.4331299 20.9939941,24.3210693 C20.8964355,24.2090088 20.7632812,24.1529785 20.5945312,24.1529785 Z M26.507373,24.1529785 C26.3781738,24.1529785 26.2746826,24.1806641 26.1968994,24.2360352 C26.138562,24.2775635 26.0709549,24.3361481 25.9940781,24.4117889 L25.9141113,24.4931152 L23.6083008,26.8978027 L23.6083008,24.8648926 C23.6083008,24.6249512 23.5535889,24.4463135 23.444165,24.3289795 C23.3347412,24.2116455 23.1916992,24.1529785 23.0150391,24.1529785 C22.8357422,24.1529785 22.6940186,24.2123047 22.5898682,24.330957 C22.5065479,24.4258789 22.4565557,24.5587695 22.4398916,24.7296289 L22.4336426,24.8648926 L22.4336426,29.1759277 C22.4336426,29.360498 22.4375977,29.5015625 22.4455078,29.5991211 C22.453418,29.6966797 22.4758301,29.782373 22.5127441,29.8562012 C22.5628418,29.9458496 22.6340332,30.017041 22.7263184,30.0697754 C22.8186035,30.1225098 22.9148437,30.148877 23.0150391,30.148877 C23.1890625,30.148877 23.3314453,30.09021 23.4421875,29.972876 C23.5307812,29.8790088 23.5839375,29.7450635 23.6016562,29.57104 L23.6083008,29.4330078 L23.6083008,28.2346191 L24.5693848,27.3130859 L25.9576172,29.4488281 C26.0129883,29.54375 26.0762695,29.6492188 26.1474609,29.7652344 C26.2186523,29.88125 26.3056641,29.9741943 26.4084961,30.0440674 C26.5113281,30.1139404 26.6444824,30.148877 26.807959,30.148877 C26.9819824,30.148877 27.119751,30.101416 27.2212646,30.0064941 C27.3227783,29.9115723 27.3735352,29.7995117 27.3735352,29.6703125 C27.3735352,29.5542969 27.3445312,29.4349854 27.2865234,29.3123779 C27.2430176,29.2204224 27.183197,29.1169724 27.1070618,29.0020279 L27.0254883,28.883252 L25.3801758,26.5260254 L26.807959,25.1733887 C26.9846191,25.0046387 27.0729492,24.8345703 27.0729492,24.6631836 C27.0729492,24.5208008 27.020874,24.4001709 26.9167236,24.3012939 C26.8125732,24.202417 26.676123,24.1529785 26.507373,24.1529785 Z M9.40166016,24.1529785 C9.22763672,24.1529785 9.08723145,24.2123047 8.98044434,24.330957 C8.89501465,24.4258789 8.84375684,24.5587695 8.8266709,24.7296289 L8.82026367,24.8648926 L8.82026367,29.3301758 C8.82026367,29.5833008 8.87629395,29.7665527 8.98835449,29.8799316 C9.08173828,29.9744141 9.22501831,30.0295288 9.41819458,30.0452759 L9.54008789,30.05 L12.3758789,30.05 C12.5683594,30.05 12.7146973,30.005835 12.8148926,29.9175049 C12.9150879,29.8291748 12.9651855,29.7138184 12.9651855,29.5714355 C12.9651855,29.4316895 12.9144287,29.3156738 12.812915,29.2233887 C12.7317041,29.1495605 12.6222275,29.1052637 12.4844854,29.090498 L12.3758789,29.0849609 L9.99492187,29.0849609 L9.99492187,24.8648926 C9.99492187,24.6249512 9.94020996,24.4463135 9.83078613,24.3289795 C9.7213623,24.2116455 9.57832031,24.1529785 9.40166016,24.1529785 Z" id="形状结合" fill="#66CC80"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),H6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_mp3</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-766.000000, -5331.000000)">\r
            <g id="icon_mp3" transform="translate(766.000000, 5331.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#E8DBEC"></path>\r
                <path d="M15.4736068,14.0038384 L15.4736068,6.57130475 C15.4736068,6.25556415 15.7328586,6 16.0527864,6 L22.4208204,6 C22.7407482,6 23,6.25593561 23,6.57130475 L23,13.4303049 C23,13.4459062 22.9992645,13.461136 22.9981613,13.4759944 C22.9992645,13.512026 23,13.5502863 23,13.589661 L23,14.2857762 C23,15.3904968 22.0928025,16.2857143 20.9737907,16.2857143 L20.394611,16.2857143 C19.2755992,16.2857143 18.3684017,15.3904968 18.3684017,14.2857762 C18.3684017,13.181427 19.2755992,12.2858381 20.394611,12.2858381 L21.678735,12.2858381 C21.7372046,12.2858381 21.7912613,12.2869525 21.8420085,12.2895527 L21.8420085,9.42857143 L16.6315983,9.42857143 L16.6315983,16.0000619 C16.6315983,17.1047825 15.7244008,18 14.605389,18 L14.0262093,18 C12.9071975,18 12,17.1047825 12,16.0000619 C12,14.8957127 12.9071975,14.0001238 14.0262093,14.0001238 L15.3103333,14.0001238 C15.3691706,14.0001238 15.4228596,14.0012382 15.4736068,14.0038384 Z" id="路径" fill="#8E4B9E"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C6A5CE"></path>\r
                <rect id="Rectangle-9" fill="#8E4B9E" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="MP3" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="8.1" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="9.51833496" y="30.05">MP3</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),Y6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_mp4</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-571.000000, -5214.000000)">\r
            <g id="icon_mp4" transform="translate(571.000000, 5214.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#E8DBEC"></path>\r
                <path d="M22.0546307,13.608 C22.0546307,13.8327805 21.9983179,14.057561 21.8293793,14.1699512 C21.7167536,14.2823415 18.8447983,16.6425366 15.1281503,18.1598049 C15.0718374,18.216 14.9592117,18.216 14.846586,18.216 C14.7339603,18.216 14.6213346,18.1598049 14.5087089,18.1036098 C14.2834575,17.9912195 14.1708318,17.8226341 14.114519,17.5416585 C14.114519,17.4854634 13.9455804,15.6310244 13.9455804,13.608 C13.9455804,11.5849756 14.114519,9.78673171 14.114519,9.67434146 C14.1708318,9.44956098 14.2834575,9.28097561 14.5087089,9.11239024 C14.6213346,9.05619512 14.7339603,9 14.846586,9 C14.9592117,9 15.0718374,9.05619512 15.1281503,9.11239024 C18.8447983,10.5734634 21.7167536,12.9898537 21.8293793,13.1022439 C21.9983179,13.2146341 22.0546307,13.4394146 22.0546307,13.608" id="Fill-4" fill="#8E4B9E"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C6A5CE"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M11.1952881,24.2518555 L10.8314209,24.2518555 C10.6072998,24.2518555 10.4293213,24.2960205 10.2974854,24.3843506 C10.1876221,24.457959 10.1235352,24.5887878 10.1052246,24.7768372 L10.0997314,24.8965332 L10.0997314,29.4844238 C10.0997314,29.7085449 10.1498291,29.8753174 10.2500244,29.9847412 C10.3502197,30.094165 10.4794189,30.148877 10.6376221,30.148877 C10.8010986,30.148877 10.9322754,30.0935059 11.0311523,29.9827637 C11.1102539,29.8941699 11.1577148,29.7701387 11.1735352,29.6106699 L11.1794678,29.4844238 L11.1794678,25.4818848 L12.1089111,29.1759277 C12.1405518,29.2972168 12.1715332,29.4138916 12.2018555,29.5259521 C12.2321777,29.6380127 12.2737061,29.7401855 12.3264404,29.8324707 C12.3791748,29.9247559 12.4530029,30.0005615 12.5479248,30.0598877 C12.6428467,30.1192139 12.7588623,30.148877 12.8959717,30.148877 C13.0779053,30.148877 13.2202881,30.1027344 13.3231201,30.0104492 C13.4259521,29.9181641 13.4964844,29.8159912 13.5347168,29.7039307 C13.5423633,29.6815186 13.550458,29.6565488 13.559001,29.6290215 L13.6156377,29.433166 L13.6479902,29.3122197 L13.6479902,29.3122197 L13.6830322,29.1759277 L14.6124756,25.4818848 L14.6124756,29.4844238 C14.6124756,29.7059082 14.6625732,29.8720215 14.7627686,29.9827637 C14.8629639,30.0935059 14.9908447,30.148877 15.1464111,30.148877 C15.3125244,30.148877 15.4450195,30.0935059 15.5438965,29.9827637 C15.622998,29.8941699 15.670459,29.7701387 15.6862793,29.6106699 L15.6922119,29.4844238 L15.6922119,24.8965332 C15.6922119,24.6434082 15.6262939,24.4726807 15.494458,24.3843506 C15.3889893,24.3136865 15.2539893,24.2712881 15.089458,24.2571553 L14.9605225,24.2518555 L14.5966553,24.2518555 C14.3778076,24.2518555 14.2189453,24.2716309 14.1200684,24.3111816 C14.0211914,24.3507324 13.9480225,24.4219238 13.9005615,24.5247559 C13.8625928,24.6070215 13.8204053,24.7306309 13.773999,24.895584 L13.7384033,25.0270508 L12.8959717,28.2029785 L12.05354,25.0270508 C11.9928955,24.7950195 11.9388428,24.6275879 11.8913818,24.5247559 C11.8439209,24.4219238 11.770752,24.3507324 11.671875,24.3111816 C11.5927734,24.279541 11.4752813,24.2605566 11.3193984,24.2542285 L11.1952881,24.2518555 Z M19.1489502,24.2518555 L17.6420654,24.2518555 C17.383667,24.2518555 17.1951416,24.3072266 17.0764893,24.4179688 C16.9776123,24.5102539 16.9199341,24.6538086 16.9034546,24.8486328 L16.8985107,24.9716797 L16.8985107,29.440918 C16.8985107,29.6729492 16.9525635,29.8489502 17.0606689,29.9689209 C17.1687744,30.0888916 17.3111572,30.148877 17.4878174,30.148877 C17.6565674,30.148877 17.7963135,30.0882324 17.9070557,29.9669434 C17.9956494,29.8699121 18.0488057,29.7365996 18.0665244,29.5670059 L18.0731689,29.4330078 L18.0731689,27.7916504 L19.1489502,27.7916504 C19.8687744,27.7916504 20.4106201,27.6393799 20.7744873,27.3348389 C21.1383545,27.0302979 21.3202881,26.5827148 21.3202881,25.9920898 C21.3202881,25.7152344 21.2754639,25.4660645 21.1858154,25.2445801 C21.096167,25.0230957 20.9649902,24.8358887 20.7922852,24.682959 C20.6195801,24.5300293 20.4145752,24.4206055 20.1772705,24.3546875 C19.9773193,24.2975586 19.705957,24.2642334 19.3631836,24.2547119 L19.1489502,24.2518555 Z M24.8917236,24.176709 C24.7677979,24.176709 24.6616699,24.2182373 24.5733398,24.3012939 C24.5026758,24.3677393 24.4164023,24.4666689 24.3145195,24.598083 L24.2351807,24.7027344 L22.1389893,27.5068848 C22.0809814,27.5833496 22.0322021,27.6479492 21.9926514,27.7006836 C21.9531006,27.753418 21.9148682,27.8081299 21.8779541,27.8648193 C21.84104,27.9215088 21.8133545,27.9755615 21.7948975,28.0269775 C21.7764404,28.0783936 21.7672119,28.1291504 21.7672119,28.179248 C21.7672119,28.3743652 21.829834,28.5286133 21.9550781,28.6419922 C22.0552734,28.7326953 22.1955469,28.7871172 22.3758984,28.8052578 L22.5186768,28.8120605 L24.504126,28.8120605 L24.504126,29.5477051 C24.504126,29.745459 24.5496094,29.8950928 24.6405762,29.9966064 C24.731543,30.0981201 24.8508545,30.148877 24.9985107,30.148877 C25.1488037,30.148877 25.2687744,30.0994385 25.3584229,30.0005615 C25.4301416,29.92146 25.4731729,29.8090303 25.4875166,29.6632725 L25.4928955,29.5477051 L25.4928955,28.8120605 L25.7341553,28.8120605 C25.9345459,28.8120605 26.0848389,28.777124 26.1850342,28.707251 C26.2852295,28.6373779 26.3353271,28.5259766 26.3353271,28.3730469 C26.3353271,28.1858398 26.2740234,28.0645508 26.151416,28.0091797 C26.0594604,27.9676514 25.9448868,27.9416962 25.807695,27.9313141 L25.6629639,27.926123 L25.4928955,27.926123 L25.4928955,24.8648926 C25.4928955,24.4061035 25.2925049,24.176709 24.8917236,24.176709 Z M24.504126,25.6282227 L24.504126,27.926123 L22.8034424,27.926123 L24.504126,25.6282227 Z M18.8641846,25.1338379 C19.3888916,25.1338379 19.7356201,25.2195313 19.9043701,25.390918 C20.0546631,25.5517578 20.1298096,25.7600586 20.1298096,26.0158203 C20.1298096,26.2293945 20.0810303,26.4020996 19.9834717,26.5339355 C19.8859131,26.6657715 19.7441895,26.7606934 19.5583008,26.8187012 C19.3724121,26.876709 19.14104,26.9057129 18.8641846,26.9057129 L18.0731689,26.9057129 L18.0731689,25.1338379 L18.8641846,25.1338379 Z" id="形状结合" fill="#8E4B9E"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),q6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_ofd</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-311.000000, -5331.000000)">\r
            <g id="icon_ofd" transform="translate(311.000000, 5331.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#FADDCF"></path>\r
                <g id="文档" transform="translate(13.000000, 6.000000)" fill="#E6560E">\r
                    <path d="M8.52173913,3.50364964 C7.56521739,3.50364964 6.69565217,2.71532847 6.69565217,1.66423358 L6.69565217,0.262773723 C6.69565217,0.175182482 6.60869565,0 6.43478261,0 L1.2173913,0 C0.52173913,0 0,0.525547445 0,1.22627737 L0,10.7737226 C0,11.4744526 0.52173913,12 1.2173913,12 L8.7826087,12 C9.47826087,12 10,11.4744526 10,10.7737226 L10,3.67883212 C10,3.59124088 9.91304348,3.41605839 9.73913043,3.41605839 L8.52173913,3.41605839 L8.52173913,3.50364964 Z M8.26086957,9.89781022 L1.82608696,9.89781022 C1.73913043,9.89781022 1.56521739,9.81021898 1.56521739,9.6350365 L1.56521739,9.45985401 C1.56521739,9.37226277 1.65217391,9.19708029 1.82608696,9.19708029 L8.17391304,9.19708029 C8.26086957,9.19708029 8.43478261,9.28467153 8.43478261,9.45985401 L8.43478261,9.6350365 C8.43478261,9.81021898 8.34782609,9.89781022 8.26086957,9.89781022 L8.26086957,9.89781022 Z M8.26086957,7.79562044 L1.82608696,7.79562044 C1.73913043,7.79562044 1.56521739,7.7080292 1.56521739,7.53284672 L1.56521739,7.35766423 C1.56521739,7.27007299 1.65217391,7.09489051 1.82608696,7.09489051 L8.17391304,7.09489051 C8.26086957,7.09489051 8.43478261,7.18248175 8.43478261,7.35766423 L8.43478261,7.53284672 C8.43478261,7.7080292 8.34782609,7.79562044 8.26086957,7.79562044 L8.26086957,7.79562044 Z M8.26086957,5.69343066 L1.82608696,5.69343066 C1.73913043,5.69343066 1.56521739,5.60583942 1.56521739,5.43065693 L1.56521739,5.34306569 C1.56521739,5.25547445 1.65217391,5.08029197 1.82608696,5.08029197 L8.17391304,5.08029197 C8.26086957,5.08029197 8.43478261,5.16788321 8.43478261,5.34306569 L8.43478261,5.51824818 C8.43478261,5.60583942 8.34782609,5.69343066 8.26086957,5.69343066 L8.26086957,5.69343066 Z M7.30434783,0.788321168 L7.30434783,1.57664234 C7.30434783,2.27737226 7.82608696,2.80291971 8.52173913,2.80291971 L9.30434783,2.80291971 C9.56521739,2.80291971 9.73913043,2.45255474 9.47826087,2.27737226 L7.73913043,0.525547445 C7.65217391,0.350364964 7.30434783,0.525547445 7.30434783,0.788321168 Z" id="形状"></path>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#F2AA86"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M12.5321045,24.1529785 C12.0891357,24.1529785 11.6936279,24.2221924 11.3455811,24.3606201 C10.9975342,24.4990479 10.7022217,24.6987793 10.4596436,24.9598145 C10.2170654,25.2208496 10.0318359,25.5372559 9.90395508,25.9090332 C9.77607422,26.2808105 9.71213379,26.689502 9.71213379,27.1351074 C9.71213379,27.570166 9.7734375,27.9749023 9.89604492,28.3493164 C10.0186523,28.7237305 10.1999268,29.0440918 10.4398682,29.3104004 C10.6798096,29.576709 10.9790771,29.7830322 11.3376709,29.9293701 C11.6962646,30.075708 12.1023193,30.148877 12.555835,30.148877 C13.0119873,30.148877 13.4167236,30.0776855 13.7700439,29.9353027 C14.1233643,29.7929199 14.4213135,29.5872559 14.6638916,29.3183105 C14.9064697,29.0493652 15.0884033,28.7316406 15.2096924,28.3651367 C15.3309814,27.9986328 15.391626,27.5912598 15.391626,27.1430176 C15.391626,26.5365723 15.2808838,26.0085693 15.0593994,25.5590088 C14.837915,25.1094482 14.5116211,24.7627197 14.0805176,24.5188232 C13.6494141,24.2749268 13.1332764,24.1529785 12.5321045,24.1529785 Z M19.8608643,24.2518555 L17.0923096,24.2518555 C16.9261963,24.2518555 16.7897461,24.2762451 16.682959,24.3250244 C16.5761719,24.3738037 16.4977295,24.4515869 16.4476318,24.558374 C16.4100586,24.6384644 16.3865753,24.7359818 16.377182,24.8509262 L16.3724854,24.9716797 L16.3724854,29.4330078 C16.3724854,29.6729492 16.4271973,29.8522461 16.5366211,29.9708984 C16.6460449,30.0895508 16.7877686,30.148877 16.961792,30.148877 C17.1331787,30.148877 17.273584,30.09021 17.3830078,29.972876 C17.4705469,29.8790088 17.5230703,29.7450635 17.5405781,29.57104 L17.5471436,29.4330078 L17.5471436,27.5227051 L19.4811768,27.5227051 C19.6578369,27.5227051 19.7909912,27.4831543 19.8806396,27.4040527 C19.9702881,27.3249512 20.0151123,27.2181641 20.0151123,27.0836914 C20.0151123,26.9492188 19.9709473,26.8417725 19.8826172,26.7613525 C19.8119531,26.6970166 19.7121797,26.658415 19.5832969,26.6455479 L19.4811768,26.6407227 L17.5471436,26.6407227 L17.5471436,25.1575684 L19.8608643,25.1575684 C20.0480713,25.1575684 20.1871582,25.11604 20.278125,25.0329834 C20.3690918,24.9499268 20.4145752,24.8398438 20.4145752,24.7027344 C20.4145752,24.5682617 20.3690918,24.4594971 20.278125,24.3764404 C20.2053516,24.3099951 20.1017813,24.2701279 19.9674141,24.2568389 L19.8608643,24.2518555 Z M23.5469971,24.2518555 L22.0163818,24.2518555 C21.7632568,24.2518555 21.5800049,24.3085449 21.466626,24.4219238 C21.3721436,24.5164063 21.3170288,24.6594116 21.3012817,24.8509399 L21.2965576,24.9716797 L21.2965576,29.2233887 C21.2965576,29.4105957 21.3130371,29.5628662 21.3459961,29.6802002 C21.3789551,29.7975342 21.4494873,29.888501 21.5575928,29.9531006 C21.6440771,30.0047803 21.7643115,30.0357881 21.9182959,30.046124 L22.0401123,30.05 L23.5707275,30.05 C23.8370361,30.05 24.0763184,30.0328613 24.2885742,29.998584 C24.5008301,29.9643066 24.6992432,29.9049805 24.8838135,29.8206055 C25.0683838,29.7362305 25.2384521,29.6241699 25.3940186,29.4844238 C25.5917725,29.3024902 25.7539307,29.096167 25.8804932,28.8654541 C26.0070557,28.6347412 26.1006592,28.3756836 26.1613037,28.0882813 C26.2219482,27.8008789 26.2522705,27.4831543 26.2522705,27.1351074 C26.2522705,26.0804199 25.9503662,25.2946777 25.3465576,24.7778809 C25.1145264,24.5748535 24.8561279,24.4364258 24.5713623,24.3625977 C24.3435498,24.3035352 24.0794561,24.2680977 23.7790811,24.2562852 L23.5469971,24.2518555 Z M12.5321045,25.086377 C12.8616943,25.086377 13.1537109,25.1661377 13.4081543,25.3256592 C13.6625977,25.4851807 13.858374,25.7205078 13.9954834,26.0316406 C14.1325928,26.3427734 14.2011475,26.7105957 14.2011475,27.1351074 C14.2011475,27.5938965 14.1266602,27.9801758 13.9776855,28.2939453 C13.8287109,28.6077148 13.6283203,28.8397461 13.3765137,28.9900391 C13.124707,29.140332 12.8485107,29.2154785 12.5479248,29.2154785 C12.3132568,29.2154785 12.0977051,29.1719727 11.9012695,29.0849609 C11.704834,28.9979492 11.5308105,28.8667725 11.3791992,28.6914307 C11.2275879,28.5160889 11.1102539,28.2952637 11.0271973,28.0289551 C10.9441406,27.7626465 10.9026123,27.4646973 10.9026123,27.1351074 C10.9026123,26.8081543 10.9441406,26.513501 11.0271973,26.2511475 C11.1102539,25.9887939 11.2236328,25.7732422 11.367334,25.6044922 C11.5110352,25.4357422 11.6824219,25.3072021 11.8814941,25.2188721 C12.0805664,25.130542 12.2974365,25.086377 12.5321045,25.086377 Z M23.2464111,25.1812988 C23.6050049,25.1812988 23.9108643,25.2201904 24.1639893,25.2979736 C24.4171143,25.3757568 24.6300293,25.5570313 24.8027344,25.8417969 C24.9754395,26.1265625 25.061792,26.5550293 25.061792,27.1271973 C25.061792,27.9393066 24.8653564,28.5114746 24.4724854,28.8437012 C24.3854736,28.920166 24.2826416,28.9794922 24.1639893,29.0216797 C24.0453369,29.0638672 23.9306396,29.0902344 23.8198975,29.1007813 C23.7091553,29.1113281 23.5562256,29.1166016 23.3611084,29.1166016 L22.4712158,29.1166016 L22.4712158,25.1812988 L23.2464111,25.1812988 Z" id="形状结合" fill="#E6560E"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),U6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_pdf</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-506.000000, -5275.000000)">\r
            <g id="icon_pdf" transform="translate(506.000000, 5275.000000)">\r
                <path d="M23.604,0 L23.6045455,6.21998174 C23.6045455,6.99128734 24.2096546,9.01596452 26.960151,9.01596452 L32.604,8.985 L32.5571138,33.0899335 C32.5571138,34.4653386 31.4404421,35.5573497 30.0474034,35.639316 L29.878966,35.6442572 L5.82814787,35.6442572 C4.35779218,35.6442572 3.15,34.5203548 3.15,33.0899335 L3.15,33.0899335 L3.15,2.55432373 C3.15,1.12390244 4.35779218,0 5.82814787,0 L5.82814787,0 L23.604,0 Z" id="Combined-Shape" fill="#F6D5D5"></path>\r
                <path d="M18.1734167,6.75 C18.4489667,6.75 18.7124557,6.89372085 18.9151343,7.15479856 C19.2665878,7.60747036 19.5231703,8.55987807 19.1885282,9.87345901 C19.1160948,10.1587093 18.9056675,10.6148199 18.5619628,11.2319127 C19.0393491,12.1644631 19.7236928,13.2118082 20.4378183,14.1029247 C21.6720154,14.126659 22.5590377,14.3268837 23.0756729,14.6984407 C23.6724901,15.1269399 23.9717578,15.7915014 23.8035776,16.3140951 C23.6837088,16.6878097 23.353817,16.9108923 22.9221479,16.9108923 C22.4637289,16.9108923 21.8975359,16.6619177 21.2399083,16.1708125 C20.8237702,15.8596702 20.3714155,15.3685986 20.0290247,14.9638338 C18.8004201,14.9776226 17.2475836,15.1588667 15.7171173,15.467413 C14.9159032,16.4707282 14.2703367,17.0999241 13.7968583,17.3385486 C13.5221506,17.4766393 13.2237588,17.55 12.933958,17.55 C12.2879872,17.55 11.784727,17.2000197 11.7096995,16.6990026 C11.6545154,16.3304799 11.7899152,15.6430606 13.3155976,15.1752852 C13.8792302,15.0026718 14.5398562,14.8339355 15.2345763,14.6863376 C16.0267614,13.6653228 16.9250025,12.3336377 17.5865044,11.2004242 C17.1621796,10.2868545 16.9582208,9.52995168 16.9806246,8.94825813 C17.056528,6.96404731 17.8396505,6.75 18.1734167,6.75 Z M14.306587,15.7915014 C14.0460965,15.8596702 13.7985765,15.9295921 13.5683058,15.9999185 C12.7627795,16.2472074 12.5704436,16.5177586 12.5630992,16.5768922 C12.5781923,16.6001882 12.7097178,16.6864949 12.933958,16.6864949 C13.0896392,16.6864949 13.253945,16.645061 13.4087503,16.567385 C13.5527747,16.4949008 13.8309189,16.3019919 14.306587,15.7915014 Z M21.2386281,15.014303 C21.430088,15.2089313 21.6060168,15.3668792 21.7561055,15.4795162 C22.4434813,15.9926027 22.8005272,16.0482638 22.9221479,16.0482638 C22.9665512,16.0482638 22.9894267,16.0409142 22.9950192,16.0379137 C23.0118305,15.986568 22.9441474,15.6659184 22.5719747,15.3983677 C22.4068266,15.279696 22.0355298,15.1014862 21.2386281,15.014303 Z M18.0582982,12.1157133 L18.0440474,12.1407288 L18.0367367,12.1269062 C17.5753193,12.882528 17.0371226,13.6899337 16.5088644,14.4209445 L16.5476752,14.4127521 L16.5256757,14.4403635 C17.5084785,14.2768191 18.4744363,14.1676547 19.3360227,14.1223437 L19.3140232,14.0955751 L19.3446473,14.0955751 C18.8720112,13.4577822 18.4265629,12.7716441 18.0582982,12.1157133 Z M18.1561675,7.60962803 C18.1316075,7.63939709 17.8810555,7.96520481 17.8422447,8.98018486 C17.8331821,9.22052874 17.8818977,9.61538181 18.1134823,10.23207 C18.287255,9.87777434 18.3390028,9.71379165 18.3528156,9.65897342 C18.6728026,8.40277294 18.3135669,7.67048098 18.1561675,7.60962803 Z" id="Combined-Shape" fill="#D32E2E"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#E99696"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M12.8332231,24.2518555 L11.3263383,24.2518555 C11.0679399,24.2518555 10.8794145,24.3072266 10.7607622,24.4179688 C10.6618852,24.5102539 10.604207,24.6538086 10.5877275,24.8486328 L10.5827836,24.9716797 L10.5827836,29.440918 C10.5827836,29.6729492 10.6368364,29.8489502 10.7449419,29.9689209 C10.8530473,30.0888916 10.9954301,30.148877 11.1720903,30.148877 C11.3408403,30.148877 11.4805864,30.0882324 11.5913286,29.9669434 C11.6799223,29.8699121 11.7330786,29.7365996 11.7507973,29.5670059 L11.7574419,29.4330078 L11.7574419,27.7916504 L12.8332231,27.7916504 C13.5530473,27.7916504 14.094893,27.6393799 14.4587602,27.3348389 C14.8226274,27.0302979 15.004561,26.5827148 15.004561,25.9920898 C15.004561,25.7152344 14.9597368,25.4660645 14.8700883,25.2445801 C14.7804399,25.0230957 14.6492631,24.8358887 14.4765581,24.682959 C14.303853,24.5300293 14.0988481,24.4206055 13.8615434,24.3546875 C13.6615922,24.2975586 13.3902299,24.2642334 13.0474565,24.2547119 L12.8332231,24.2518555 Z M25.4459672,24.2518555 L22.6774126,24.2518555 C22.5112993,24.2518555 22.3748491,24.2762451 22.268062,24.3250244 C22.1612749,24.3738037 22.0828325,24.4515869 22.0327348,24.558374 C21.9951616,24.6384644 21.9716783,24.7359818 21.962285,24.8509262 L21.9575883,24.9716797 L21.9575883,29.4330078 C21.9575883,29.6729492 22.0123002,29.8522461 22.1217241,29.9708984 C22.2311479,30.0895508 22.3728715,30.148877 22.546895,30.148877 C22.7182817,30.148877 22.858687,30.09021 22.9681108,29.972876 C23.0556499,29.8790088 23.1081733,29.7450635 23.1256811,29.57104 L23.1322465,29.4330078 L23.1322465,27.5227051 L25.0662797,27.5227051 C25.2429399,27.5227051 25.3760942,27.4831543 25.4657426,27.4040527 C25.5553911,27.3249512 25.6002153,27.2181641 25.6002153,27.0836914 C25.6002153,26.9492188 25.5560502,26.8417725 25.4677202,26.7613525 C25.3970561,26.6970166 25.2972827,26.658415 25.1683999,26.6455479 L25.0662797,26.6407227 L23.1322465,26.6407227 L23.1322465,25.1575684 L25.4459672,25.1575684 C25.6331743,25.1575684 25.7722612,25.11604 25.863228,25.0329834 C25.9541948,24.9499268 25.9996782,24.8398438 25.9996782,24.7027344 C25.9996782,24.5682617 25.9541948,24.4594971 25.863228,24.3764404 C25.7904545,24.3099951 25.6868842,24.2701279 25.552517,24.2568389 L25.4459672,24.2518555 Z M18.24377,24.2518555 L16.7131547,24.2518555 C16.4600297,24.2518555 16.2767778,24.3085449 16.1633989,24.4219238 C16.0689165,24.5164063 16.0138017,24.6594116 15.9980546,24.8509399 L15.9933305,24.9716797 L15.9933305,29.2233887 C15.9933305,29.4105957 16.00981,29.5628662 16.042769,29.6802002 C16.075728,29.7975342 16.1462602,29.888501 16.2543657,29.9531006 C16.3408501,30.0047803 16.4610844,30.0357881 16.6150688,30.046124 L16.7368852,30.05 L18.2675004,30.05 C18.533809,30.05 18.7730913,30.0328613 18.9853471,29.998584 C19.197603,29.9643066 19.3960161,29.9049805 19.5805864,29.8206055 C19.7651567,29.7362305 19.9352251,29.6241699 20.0907915,29.4844238 C20.2885454,29.3024902 20.4507036,29.096167 20.5772661,28.8654541 C20.7038286,28.6347412 20.7974321,28.3756836 20.8580766,28.0882813 C20.9187211,27.8008789 20.9490434,27.4831543 20.9490434,27.1351074 C20.9490434,26.0804199 20.6471391,25.2946777 20.0433305,24.7778809 C19.8112993,24.5748535 19.5529008,24.4364258 19.2681352,24.3625977 C19.0403227,24.3035352 18.776229,24.2680977 18.475854,24.2562852 L18.24377,24.2518555 Z M17.943184,25.1812988 C18.3017778,25.1812988 18.6076372,25.2201904 18.8607622,25.2979736 C19.1138872,25.3757568 19.3268022,25.5570313 19.4995073,25.8417969 C19.6722124,26.1265625 19.7585649,26.5550293 19.7585649,27.1271973 C19.7585649,27.9393066 19.5621294,28.5114746 19.1692583,28.8437012 C19.0822465,28.920166 18.9794145,28.9794922 18.8607622,29.0216797 C18.7421098,29.0638672 18.6274126,29.0902344 18.5166704,29.1007813 C18.4059282,29.1113281 18.2529985,29.1166016 18.0578813,29.1166016 L17.1679887,29.1166016 L17.1679887,25.1812988 L17.943184,25.1812988 Z M12.5484575,25.1338379 C13.0731645,25.1338379 13.419893,25.2195313 13.588643,25.390918 C13.738936,25.5517578 13.8140825,25.7600586 13.8140825,26.0158203 C13.8140825,26.2293945 13.7653032,26.4020996 13.6677446,26.5339355 C13.570186,26.6657715 13.4284624,26.7606934 13.2425737,26.8187012 C13.056685,26.876709 12.8253129,26.9057129 12.5484575,26.9057129 L11.7574419,26.9057129 L11.7574419,25.1338379 L12.5484575,25.1338379 Z" id="形状结合" fill="#D32E2E"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),X6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_png</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-246.000000, -5214.000000)">\r
            <g id="icon_png" transform="translate(246.000000, 5214.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#E6EBEF"></path>\r
                <g id="png" transform="translate(12.600000, 6.750000)" fill="#8199AE">\r
                    <path d="M4.32009911,2.15996041 L6.48006608,2.15996041 L6.48006608,0 L4.32009911,0 L4.32009911,2.15996041 Z M6.48006608,4.31995381 L8.64013215,4.31995381 L8.64013215,2.15996041 L6.48006608,2.15996041 L6.48006608,4.31995381 Z M4.32009911,6.48007919 L6.48006608,6.48007919 L6.48006608,4.31995381 L4.32009911,4.31995381 L4.32009911,6.48007919 Z M8.64013215,2.15996041 L10.8,2.15996041 L10.8,0 L8.64013215,0 L8.64013215,2.15996041 Z M8.64013215,6.48007919 L10.8,6.48007919 L10.8,4.31995381 L8.64013215,4.31995381 L8.64013215,6.48007919 Z M0,2.15996041 L2.16006608,2.15996041 L2.16006608,0 L0,0 L0,2.15996041 Z M4.32009911,10.8 L6.48006608,10.8 L6.48006608,8.64003959 L4.32009911,8.64003959 L4.32009911,10.8 Z M8.64013215,10.8 L10.8,10.8 L10.8,8.64003959 L8.64013215,8.64003959 L8.64013215,10.8 Z M0,10.8 L2.16006608,10.8 L2.16006608,8.64003959 L0,8.64003959 L0,10.8 Z M8.64013215,6.48007919 L8.64013215,8.64003959 L6.48006608,8.64003959 L6.48006608,6.48007919 L8.64013215,6.48007919 Z M4.32009911,6.48007919 L4.32009911,8.64003959 L2.16003304,8.64003959 L2.16003304,6.48007919 L4.32009911,6.48007919 Z M2.16006608,4.31995381 L2.16006608,6.48007919 L0,6.48007919 L0,4.31995381 L2.16006608,4.31995381 Z M4.32009911,2.15996041 L4.32009911,4.31995381 L2.16003304,4.31995381 L2.16003304,2.15996041 L4.32009911,2.15996041 Z" id="Combined-Shape"></path>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C2CDD7"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M11.8973145,24.2518555 L10.3904297,24.2518555 C10.1320313,24.2518555 9.94350586,24.3072266 9.82485352,24.4179688 C9.72597656,24.5102539 9.66829834,24.6538086 9.65181885,24.8486328 L9.646875,24.9716797 L9.646875,29.440918 C9.646875,29.6729492 9.70092773,29.8489502 9.8090332,29.9689209 C9.91713867,30.0888916 10.0595215,30.148877 10.2361816,30.148877 C10.4049316,30.148877 10.5446777,30.0882324 10.6554199,29.9669434 C10.7440137,29.8699121 10.7971699,29.7365996 10.8148887,29.5670059 L10.8215332,29.4330078 L10.8215332,27.7916504 L11.8973145,27.7916504 C12.6171387,27.7916504 13.1589844,27.6393799 13.5228516,27.3348389 C13.8867188,27.0302979 14.0686523,26.5827148 14.0686523,25.9920898 C14.0686523,25.7152344 14.0238281,25.4660645 13.9341797,25.2445801 C13.8445313,25.0230957 13.7133545,24.8358887 13.5406494,24.682959 C13.3679443,24.5300293 13.1629395,24.4206055 12.9256348,24.3546875 C12.7256836,24.2975586 12.4543213,24.2642334 12.1115479,24.2547119 L11.8973145,24.2518555 Z M19.3724121,24.1529785 C19.2089355,24.1529785 19.0790771,24.2090088 18.9828369,24.3210693 C18.9058447,24.4107178 18.8596494,24.5362256 18.844251,24.6975928 L18.8384766,24.8253418 L18.8384766,28.2741699 L16.580127,24.8569824 C16.5168457,24.7567871 16.455542,24.6605469 16.3962158,24.5682617 C16.3368896,24.4759766 16.2788818,24.4008301 16.2221924,24.3428223 C16.1655029,24.2848145 16.099585,24.2386719 16.0244385,24.2043945 C15.949292,24.1701172 15.8589844,24.1529785 15.7535156,24.1529785 C15.619043,24.1529785 15.4937988,24.1905518 15.3777832,24.2656982 C15.2617676,24.3408447 15.1787109,24.4377441 15.1286133,24.5563965 C15.0969727,24.6374756 15.0771973,24.7415436 15.0692871,24.8686005 L15.065332,25.0033203 L15.065332,29.4765137 C15.065332,29.6953613 15.1154297,29.8621338 15.215625,29.9768311 C15.3158203,30.0915283 15.4489746,30.148877 15.6150879,30.148877 C15.7759277,30.148877 15.9077637,30.0921875 16.0105957,29.9788086 C16.0928613,29.8881055 16.1422207,29.7628086 16.1586738,29.602918 L16.1648438,29.4765137 L16.1648438,26.0791016 L18.3678223,29.4567383 C18.4311035,29.5490234 18.4943848,29.6393311 18.557666,29.7276611 C18.6209473,29.8159912 18.689502,29.8931152 18.7633301,29.9590332 C18.8371582,30.0249512 18.9162598,30.0730713 19.0006348,30.1033936 C19.0850098,30.1337158 19.1799316,30.148877 19.2854004,30.148877 C19.6721191,30.148877 19.8815918,29.9350739 19.9138184,29.5074677 L19.9182129,29.3855469 L19.9182129,24.8253418 C19.9182129,24.6012207 19.8694336,24.4331299 19.771875,24.3210693 C19.6743164,24.2090088 19.5411621,24.1529785 19.3724121,24.1529785 Z M23.9879883,24.1529785 C23.5133789,24.1529785 23.0888672,24.2221924 22.7144531,24.3606201 C22.3400391,24.4990479 22.0223145,24.6987793 21.7612793,24.9598145 C21.5002441,25.2208496 21.3024902,25.5385742 21.1680176,25.9129883 C21.0335449,26.2874023 20.9663086,26.7000488 20.9663086,27.1509277 C20.9663086,27.6097168 21.0355225,28.025 21.1739502,28.3967773 C21.3123779,28.7685547 21.510791,29.0849609 21.7691895,29.3459961 C22.0275879,29.6070313 22.3400391,29.8061035 22.706543,29.9432129 C23.0730469,30.0803223 23.4791016,30.148877 23.924707,30.148877 C24.3070313,30.148877 24.6603516,30.1053711 24.984668,30.0183594 C25.3089844,29.9313477 25.6464844,29.7929199 25.997168,29.6030762 C26.118457,29.5371582 26.2179932,29.4686035 26.2957764,29.3974121 C26.3735596,29.3262207 26.4269531,29.24646 26.455957,29.1581299 C26.47771,29.0918823 26.4913055,29.0089493 26.4967438,28.9093307 L26.4994629,28.8041504 L26.4994629,27.6927734 C26.4994629,27.4923828 26.4645264,27.3420898 26.3946533,27.2418945 C26.3247803,27.1416992 26.2331543,27.0770996 26.1197754,27.0480957 C26.0441895,27.0287598 25.9574707,27.0158691 25.8596191,27.0094238 L25.7044922,27.0045898 L24.3716309,27.0045898 C24.210791,27.0045898 24.0835693,27.0467773 23.9899658,27.1311523 C23.8963623,27.2155273 23.8495605,27.3223145 23.8495605,27.4515137 C23.8495605,27.6097168 23.9055908,27.7217773 24.0176514,27.7876953 C24.1072998,27.8404297 24.2311201,27.8720703 24.3891123,27.8826172 L24.5140137,27.8865723 L25.4236816,27.8865723 L25.4236816,28.8278809 C25.1784668,28.9597168 24.9365479,29.0612305 24.6979248,29.1324219 C24.4593018,29.2036133 24.2041992,29.239209 23.9326172,29.239209 C23.3736328,29.239209 22.937915,29.055957 22.6254639,28.6894531 C22.3130127,28.3229492 22.1567871,27.7995605 22.1567871,27.1192871 C22.1567871,26.8055176 22.1969971,26.5194336 22.277417,26.2610352 C22.3578369,26.0026367 22.4738525,25.7844482 22.6254639,25.6064697 C22.7770752,25.4284912 22.959668,25.2927002 23.1732422,25.1990967 C23.3868164,25.1054932 23.6293945,25.0586914 23.9009766,25.0586914 C24.1646484,25.0586914 24.3815186,25.0982422 24.5515869,25.1773438 C24.7216553,25.2564453 24.8567871,25.3540039 24.9569824,25.4700195 C25.0571777,25.5860352 25.1797852,25.7574219 25.3248047,25.9841797 C25.3775391,26.0553711 25.4427979,26.1107422 25.5205811,26.150293 C25.5983643,26.1898438 25.6794434,26.2096191 25.7638184,26.2096191 C25.916748,26.2096191 26.0479248,26.1575439 26.1573486,26.0533936 C26.2667725,25.9492432 26.3214844,25.8233398 26.3214844,25.6756836 C26.3214844,25.5385742 26.2740234,25.3816895 26.1791016,25.2050293 C26.0841797,25.0283691 25.9431152,24.8609375 25.7559082,24.7027344 C25.5687012,24.5445313 25.3248047,24.4133545 25.0242188,24.3092041 C24.7236328,24.2050537 24.3782227,24.1529785 23.9879883,24.1529785 Z M11.6125488,25.1338379 C12.1372559,25.1338379 12.4839844,25.2195313 12.6527344,25.390918 C12.8030273,25.5517578 12.8781738,25.7600586 12.8781738,26.0158203 C12.8781738,26.2293945 12.8293945,26.4020996 12.7318359,26.5339355 C12.6342773,26.6657715 12.4925537,26.7606934 12.306665,26.8187012 C12.1207764,26.876709 11.8894043,26.9057129 11.6125488,26.9057129 L10.8215332,26.9057129 L10.8215332,25.1338379 L11.6125488,25.1338379 Z" id="形状结合" fill="#8199AE"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),G6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_ppt</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-376.000000, -5214.000000)">\r
            <g id="icon_ppt" transform="translate(376.000000, 5214.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#FADDCF"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#F2AA86"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M12.9493652,24.2518555 L11.4424805,24.2518555 C11.184082,24.2518555 10.9955566,24.3072266 10.8769043,24.4179688 C10.7780273,24.5102539 10.7203491,24.6538086 10.7038696,24.8486328 L10.6989258,24.9716797 L10.6989258,29.440918 C10.6989258,29.6729492 10.7529785,29.8489502 10.861084,29.9689209 C10.9691895,30.0888916 11.1115723,30.148877 11.2882324,30.148877 C11.4569824,30.148877 11.5967285,30.0882324 11.7074707,29.9669434 C11.7960645,29.8699121 11.8492207,29.7365996 11.8669395,29.5670059 L11.873584,29.4330078 L11.873584,27.7916504 L12.9493652,27.7916504 C13.6691895,27.7916504 14.2110352,27.6393799 14.5749023,27.3348389 C14.9387695,27.0302979 15.1207031,26.5827148 15.1207031,25.9920898 C15.1207031,25.7152344 15.0758789,25.4660645 14.9862305,25.2445801 C14.896582,25.0230957 14.7654053,24.8358887 14.5927002,24.682959 C14.4199951,24.5300293 14.2149902,24.4206055 13.9776855,24.3546875 C13.7777344,24.2975586 13.5063721,24.2642334 13.1635986,24.2547119 L12.9493652,24.2518555 Z M18.352002,24.2518555 L16.8451172,24.2518555 C16.5867187,24.2518555 16.3981934,24.3072266 16.279541,24.4179688 C16.1806641,24.5102539 16.1229858,24.6538086 16.1065063,24.8486328 L16.1015625,24.9716797 L16.1015625,29.440918 C16.1015625,29.6729492 16.1556152,29.8489502 16.2637207,29.9689209 C16.3718262,30.0888916 16.514209,30.148877 16.6908691,30.148877 C16.8596191,30.148877 16.9993652,30.0882324 17.1101074,29.9669434 C17.1987012,29.8699121 17.2518574,29.7365996 17.2695762,29.5670059 L17.2762207,29.4330078 L17.2762207,27.7916504 L18.352002,27.7916504 C19.0718262,27.7916504 19.6136719,27.6393799 19.9775391,27.3348389 C20.3414062,27.0302979 20.5233398,26.5827148 20.5233398,25.9920898 C20.5233398,25.7152344 20.4785156,25.4660645 20.3888672,25.2445801 C20.2992187,25.0230957 20.168042,24.8358887 19.9953369,24.682959 C19.8226318,24.5300293 19.617627,24.4206055 19.3803223,24.3546875 C19.1803711,24.2975586 18.9090088,24.2642334 18.5662354,24.2547119 L18.352002,24.2518555 Z M25.2694336,24.2518555 L21.5358398,24.2518555 C21.3433594,24.2518555 21.1963623,24.2953613 21.0948486,24.382373 C20.993335,24.4693848 20.9425781,24.5880371 20.9425781,24.7383301 C20.9425781,24.8833496 20.9913574,25.0000244 21.088916,25.0883545 C21.1669629,25.1590186 21.277916,25.201417 21.4217754,25.2155498 L21.5358398,25.2208496 L22.8172852,25.2208496 L22.8172852,29.4330078 C22.8172852,29.6729492 22.8719971,29.8522461 22.9814209,29.9708984 C23.0908447,30.0895508 23.2325684,30.148877 23.4065918,30.148877 C23.5779785,30.148877 23.7177246,30.09021 23.8258301,29.972876 C23.9123145,29.8790088 23.9642051,29.7450635 23.981502,29.57104 L23.9879883,29.4330078 L23.9879883,25.2208496 L25.2694336,25.2208496 C25.4698242,25.2208496 25.6201172,25.1766846 25.7203125,25.0883545 C25.8205078,25.0000244 25.8706055,24.8833496 25.8706055,24.7383301 C25.8706055,24.5933105 25.821167,24.4759766 25.72229,24.3863281 C25.6431885,24.3146094 25.5307588,24.2715781 25.385001,24.2572344 L25.2694336,24.2518555 Z M12.6645996,25.1338379 C13.1893066,25.1338379 13.5360352,25.2195313 13.7047852,25.390918 C13.8550781,25.5517578 13.9302246,25.7600586 13.9302246,26.0158203 C13.9302246,26.2293945 13.8814453,26.4020996 13.7838867,26.5339355 C13.6863281,26.6657715 13.5446045,26.7606934 13.3587158,26.8187012 C13.1728271,26.876709 12.9414551,26.9057129 12.6645996,26.9057129 L11.873584,26.9057129 L11.873584,25.1338379 L12.6645996,25.1338379 Z M18.0672363,25.1338379 C18.5919434,25.1338379 18.9386719,25.2195313 19.1074219,25.390918 C19.2577148,25.5517578 19.3328613,25.7600586 19.3328613,26.0158203 C19.3328613,26.2293945 19.284082,26.4020996 19.1865234,26.5339355 C19.0889648,26.6657715 18.9472412,26.7606934 18.7613525,26.8187012 C18.5754639,26.876709 18.3440918,26.9057129 18.0672363,26.9057129 L17.2762207,26.9057129 L17.2762207,25.1338379 L18.0672363,25.1338379 Z" id="形状结合" fill="#E6560E"></path>\r
                <path d="M18.0321884,7.58571429 L18.0320903,13.0178571 L18.0320903,13.0178571 L23.4643768,13.0178571 C23.4643768,15.9447739 21.1436836,18.3310253 18.250243,18.4456867 L18.0321884,18.45 C15.0320736,18.45 12.6,16.0118422 12.6,13.0178571 C12.6,10.0909404 14.9206931,7.70468894 17.8141337,7.59002761 L18.0321884,7.58571429 Z M18.8678116,6.75 C23.8821394,6.75 24.3,10.9285714 24.3,12.1821429 L18.8678116,12.1821429 L18.8678116,12.1821429 L18.8678116,6.75 L18.8678116,6.75 Z" id="Combined-Shape" fill="#E6560E"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),K6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_psd</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-830.000000, -5275.000000)">\r
            <g id="icon_psd" transform="translate(830.000000, 5275.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#D8E6F5"></path>\r
                <path d="M22.9021254,6.75 C22.9138082,6.84845024 22.9242329,6.94707942 22.9333994,7.04578017 C22.9336869,7.05075458 22.9600361,7.63762682 22.9457651,7.84744644 C22.9236218,8.14787893 22.8952596,8.41474315 22.8620445,8.65172516 C22.6565352,9.754146 22.077716,10.4131292 22.0050311,10.4923976 L22.0050311,10.4923976 L21.9740807,10.500557 L21.9974822,10.500557 L21.9965835,10.5037779 L22.2352359,10.5037779 C22.3066267,10.5060682 22.3751059,10.5074639 22.4366472,10.508287 C21.2356565,13.2886922 18.2115397,15.6622342 18.2115397,15.6622342 L18.2115397,15.6622342 L18.2148109,15.6645961 L19.1903777,15.7903877 C19.0683015,15.8881938 18.9285394,15.9809181 18.7786762,16.0678449 L18.7786762,16.0678449 L18.7804376,16.0692764 L18.7407521,16.0896034 C18.4980017,16.2273837 18.2308433,16.3507775 17.9685737,16.4575661 C17.3603856,16.7199928 16.887682,16.8555542 16.5507145,16.9244085 C16.7545702,16.9805942 17.0476104,17.0496275 17.3368763,17.0771836 C17.3368763,17.0771836 15.122215,18.2396551 14.0766593,18.3174204 L14.1923321,18.3058073 C13.3376698,19.353019 12.6,20.25 12.6,20.25 C12.8764548,19.7861349 13.388607,19.0582498 13.9481273,18.2945122 C13.9454859,18.2808079 13.9418669,18.2618917 13.9382272,18.2413013 C13.9279104,18.1911278 13.9195707,18.1373041 13.9129205,18.0801163 C13.9128486,18.07983 13.9127767,18.0791501 13.9127767,18.0791501 L13.9009127,17.943327 C13.8293545,16.8157943 14.3175404,14.7473018 14.3175404,14.7473018 C14.4168981,15.019713 14.5574869,15.2849311 14.6631712,15.4675883 C14.6454853,15.1251418 14.65857,14.6350378 14.7607315,13.98221 C14.7987994,13.702534 14.851354,13.4136965 14.9243265,13.1448998 C14.9277415,13.1302986 14.930761,13.1161985 14.934176,13.1014185 L14.934176,13.1014185 C14.9827045,12.9363327 15.0377035,12.7783328 15.1020487,12.6360076 L15.1020487,12.6360076 L15.4689961,13.5457508 C15.4720875,13.5482917 15.5296023,13.4176077 15.6396538,13.1933297 L15.7467336,12.9790356 C16.2854455,11.9193535 17.5846143,9.62488308 19.3585739,8.22403383 C19.3670574,8.45550463 19.3861453,8.71073804 19.4278079,8.85639144 L19.4278079,8.85639144 L19.4278079,8.91604849 L19.4343143,8.84579846 C19.452863,8.81477107 19.5144043,8.7141736 19.6062849,8.57789642 C19.9272561,8.1377154 20.7142447,7.29013432 22.2153572,6.89103686 C22.4154384,6.84419158 22.6437381,6.79641584 22.9018019,6.75 L22.9018019,6.75 Z" id="Combined-Shape" fill="#3D80CC"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#9EBFE5"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M13.082666,24.2518555 L11.5757813,24.2518555 C11.3173828,24.2518555 11.1288574,24.3072266 11.0102051,24.4179688 C10.9113281,24.5102539 10.8536499,24.6538086 10.8371704,24.8486328 L10.8322266,24.9716797 L10.8322266,29.440918 C10.8322266,29.6729492 10.8862793,29.8489502 10.9943848,29.9689209 C11.1024902,30.0888916 11.244873,30.148877 11.4215332,30.148877 C11.5902832,30.148877 11.7300293,30.0882324 11.8407715,29.9669434 C11.9293652,29.8699121 11.9825215,29.7365996 12.0002402,29.5670059 L12.0068848,29.4330078 L12.0068848,27.7916504 L13.082666,27.7916504 C13.8024902,27.7916504 14.3443359,27.6393799 14.7082031,27.3348389 C15.0720703,27.0302979 15.2540039,26.5827148 15.2540039,25.9920898 C15.2540039,25.7152344 15.2091797,25.4660645 15.1195313,25.2445801 C15.0298828,25.0230957 14.8987061,24.8358887 14.726001,24.682959 C14.5532959,24.5300293 14.348291,24.4206055 14.1109863,24.3546875 C13.9110352,24.2975586 13.6396729,24.2642334 13.2968994,24.2547119 L13.082666,24.2518555 Z M18.3231445,24.1529785 C17.8775391,24.1529785 17.4899414,24.2195557 17.1603516,24.35271 C16.8307617,24.4858643 16.5789551,24.6763672 16.4049316,24.9242188 C16.2309082,25.1720703 16.1438965,25.4555176 16.1438965,25.7745605 C16.1438965,26.1094238 16.2262939,26.3895752 16.3910889,26.6150146 C16.5558838,26.8404541 16.7786865,27.0184326 17.0594971,27.1489502 C17.3403076,27.2794678 17.6890137,27.3935059 18.1056152,27.4910645 C18.416748,27.5622559 18.6652588,27.6294922 18.8511475,27.6927734 C19.0370361,27.7560547 19.1886475,27.8476807 19.3059814,27.9676514 C19.4233154,28.0876221 19.4819824,28.2438477 19.4819824,28.4363281 C19.4819824,28.6789063 19.3732178,28.8812744 19.1556885,29.0434326 C18.9381592,29.2055908 18.6553711,29.2866699 18.3073242,29.2866699 C18.0541992,29.2866699 17.8498535,29.24646 17.6942871,29.16604 C17.5387207,29.0856201 17.4180908,28.9834473 17.3323975,28.8595215 C17.2467041,28.7355957 17.1669434,28.5813477 17.0931152,28.3967773 C17.0324707,28.2412109 16.9593018,28.123877 16.8736084,28.0447754 C16.787915,27.9656738 16.6831055,27.926123 16.5591797,27.926123 C16.40625,27.926123 16.2803467,27.9768799 16.1814697,28.0783936 C16.0825928,28.1799072 16.0331543,28.3018555 16.0331543,28.4442383 C16.0331543,28.6894531 16.1155518,28.9419189 16.2803467,29.2016357 C16.4451416,29.4613525 16.659375,29.6689941 16.9230469,29.8245605 C17.2948242,30.0407715 17.7667969,30.148877 18.3389648,30.148877 C18.8162109,30.148877 19.2288574,30.0697754 19.5769043,29.9115723 C19.9249512,29.7533691 20.1892822,29.5345215 20.3698975,29.2550293 C20.5505127,28.9755371 20.6408203,28.6604492 20.6408203,28.3097656 C20.6408203,28.0170898 20.5887451,27.7698975 20.4845947,27.5681885 C20.3804443,27.3664795 20.2354248,27.199707 20.0495361,27.0678711 C19.8636475,26.9360352 19.638208,26.8239746 19.3732178,26.7316895 C19.1082275,26.6394043 18.8122559,26.5550293 18.4853027,26.4785645 C18.2242676,26.4126465 18.0370605,26.3625488 17.9236816,26.3282715 C17.8103027,26.2939941 17.6982422,26.2465332 17.5875,26.1858887 C17.4767578,26.1252441 17.3897461,26.0527344 17.3264648,25.9683594 C17.2631836,25.8839844 17.231543,25.7837891 17.231543,25.6677734 C17.231543,25.4805664 17.3244873,25.3203857 17.510376,25.1872314 C17.6962646,25.0540771 17.9408203,24.9875 18.244043,24.9875 C18.5709961,24.9875 18.8083008,25.0488037 18.955957,25.1714111 C19.1036133,25.2940186 19.2301758,25.4647461 19.3356445,25.6835938 C19.4173828,25.8365234 19.4931885,25.9466064 19.5630615,26.0138428 C19.6329346,26.0810791 19.7351074,26.1146973 19.8695801,26.1146973 C20.0172363,26.1146973 20.1405029,26.058667 20.2393799,25.9466064 C20.3382568,25.8345459 20.3876953,25.7086426 20.3876953,25.5688965 C20.3876953,25.4159668 20.3481445,25.259082 20.269043,25.0982422 C20.1899414,24.9374023 20.0646973,24.7838135 19.8933105,24.6374756 C19.7219238,24.4911377 19.5063721,24.3738037 19.2466553,24.2854736 C18.9869385,24.1971436 18.6791016,24.1529785 18.3231445,24.1529785 Z M23.8958496,24.2518555 L22.3652344,24.2518555 C22.1121094,24.2518555 21.9288574,24.3085449 21.8154785,24.4219238 C21.7209961,24.5164063 21.6658813,24.6594116 21.6501343,24.8509399 L21.6454102,24.9716797 L21.6454102,29.2233887 C21.6454102,29.4105957 21.6618896,29.5628662 21.6948486,29.6802002 C21.7278076,29.7975342 21.7983398,29.888501 21.9064453,29.9531006 C21.9929297,30.0047803 22.1131641,30.0357881 22.2671484,30.046124 L22.3889648,30.05 L23.9195801,30.05 C24.1858887,30.05 24.4251709,30.0328613 24.6374268,29.998584 C24.8496826,29.9643066 25.0480957,29.9049805 25.232666,29.8206055 C25.4172363,29.7362305 25.5873047,29.6241699 25.7428711,29.4844238 C25.940625,29.3024902 26.1027832,29.096167 26.2293457,28.8654541 C26.3559082,28.6347412 26.4495117,28.3756836 26.5101563,28.0882813 C26.5708008,27.8008789 26.601123,27.4831543 26.601123,27.1351074 C26.601123,26.0804199 26.2992187,25.2946777 25.6954102,24.7778809 C25.4633789,24.5748535 25.2049805,24.4364258 24.9202148,24.3625977 C24.6924023,24.3035352 24.4283086,24.2680977 24.1279336,24.2562852 L23.8958496,24.2518555 Z M23.5952637,25.1812988 C23.9538574,25.1812988 24.2597168,25.2201904 24.5128418,25.2979736 C24.7659668,25.3757568 24.9788818,25.5570313 25.1515869,25.8417969 C25.324292,26.1265625 25.4106445,26.5550293 25.4106445,27.1271973 C25.4106445,27.9393066 25.214209,28.5114746 24.8213379,28.8437012 C24.7343262,28.920166 24.6314941,28.9794922 24.5128418,29.0216797 C24.3941895,29.0638672 24.2794922,29.0902344 24.16875,29.1007813 C24.0580078,29.1113281 23.9050781,29.1166016 23.7099609,29.1166016 L22.8200684,29.1166016 L22.8200684,25.1812988 L23.5952637,25.1812988 Z M12.7979004,25.1338379 C13.3226074,25.1338379 13.6693359,25.2195313 13.8380859,25.390918 C13.9883789,25.5517578 14.0635254,25.7600586 14.0635254,26.0158203 C14.0635254,26.2293945 14.0147461,26.4020996 13.9171875,26.5339355 C13.8196289,26.6657715 13.6779053,26.7606934 13.4920166,26.8187012 C13.3061279,26.876709 13.0747559,26.9057129 12.7979004,26.9057129 L12.0068848,26.9057129 L12.0068848,25.1338379 L12.7979004,25.1338379 Z" id="形状结合" fill="#3D80CC"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),J6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_rar</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-181.000000, -5275.000000)">\r
            <g id="icon_rar" transform="translate(181.000000, 5275.000000)">\r
                <path d="M23.604,0 L23.6045455,6.21998174 C23.6045455,6.99128734 24.2096546,9.01596452 26.960151,9.01596452 L32.604,8.985 L32.5571138,33.0899335 C32.5571138,34.4653386 31.4404421,35.5573497 30.0474034,35.639316 L29.878966,35.6442572 L5.82814787,35.6442572 C4.35779218,35.6442572 3.15,34.5203548 3.15,33.0899335 L3.15,33.0899335 L3.15,2.55432373 C3.15,1.12390244 4.35779218,0 5.82814787,0 L5.82814787,0 L23.604,0 Z" id="Combined-Shape" fill="#E8DBEC"></path>\r
                <path d="M13.7422861,9.99139259 L22.2750805,9.99551066 C22.4532575,9.99360229 22.6233738,9.9207023 22.7480257,9.79280089 C22.8726776,9.66491956 22.9416242,9.49254293 22.939724,9.31359746 L22.9427843,7.88808027 C22.9447045,7.70859242 22.8755379,7.5356935 22.7505059,7.40741041 C22.625474,7.27914741 22.4548177,7.20602645 22.2761006,7.20411808 L13.7443463,7.2 C13.5659893,7.20162714 13.395593,7.27442669 13.2707211,7.40234819 C13.1458692,7.5302496 13.0767626,7.70278694 13.0786827,7.8819132 L13.0786827,9.30743039 C13.0762225,9.48655665 13.144789,9.65931496 13.2692809,9.78761814 C13.3937528,9.91590123 13.5639291,9.98922307 13.7422861,9.99139259 Z M20.739954,8.02281166 C20.7404741,7.89209791 20.844364,7.78546987 20.9744764,7.78213523 C21.0371224,7.78267762 21.0969681,7.80826995 21.1407923,7.85324738 C21.1846165,7.8982248 21.2087788,7.958871 21.2079587,8.02178717 L21.2069386,9.17886607 C21.2074786,9.24178224 21.1830563,9.302328 21.1390521,9.34710454 C21.0950279,9.39188108 21.0350822,9.41721227 20.9724162,9.41747342 C20.9099502,9.41693104 20.8502645,9.39149941 20.8064604,9.34674295 C20.7626762,9.3019865 20.7383939,9.24160144 20.7389339,9.17886607 L20.7389339,8.02281166 L20.739954,8.02281166 Z M19.7496595,8.02178717 C19.7488394,7.9590518 19.7728617,7.89854621 19.8164659,7.85360896 C19.86005,7.80867171 19.9196557,7.78295885 19.9821217,7.78213523 C20.0447677,7.78267762 20.1046334,7.80826995 20.1484376,7.85324738 C20.1922617,7.8982248 20.216444,7.958871 20.215624,8.02178717 L20.2145839,9.17886607 C20.2151439,9.24160144 20.1908416,9.3019865 20.1470574,9.34674295 C20.1032732,9.39149941 20.0435675,9.41693104 19.9811016,9.41747342 C19.8512492,9.41578601 19.7470392,9.30927851 19.7475993,9.17886607 L19.7496595,8.02178717 Z M13.7422861,13.4523442 L22.2750805,13.4574867 C22.4536176,13.4555984 22.6240539,13.3823971 22.7487658,13.2540538 C22.8734577,13.1257305 22.9421842,12.9528316 22.939724,12.7735245 L22.9427843,11.3480073 C22.9449645,11.1685195 22.8758579,10.9955804 22.7507459,10.8673977 C22.625634,10.7392352 22.4548177,10.6664155 22.2761006,10.6650696 L13.7443463,10.6609516 C13.3729708,10.6654714 13.0752624,10.9709121 13.0786827,11.3438893 L13.0786827,12.7704309 C13.0765025,12.9492961 13.1452091,13.1216727 13.269701,13.2495942 C13.3941929,13.3774956 13.5642091,13.4504559 13.7422861,13.4523442 Z M20.739954,11.4817142 C20.7410341,11.351583 20.844924,11.2458791 20.9744764,11.2430868 C21.0369424,11.2436292 21.0966481,11.2690809 21.1404322,11.3138374 C21.1842164,11.3585737 21.2085187,11.4189789 21.2079587,11.4817142 L21.2069386,12.6398176 C21.2075186,12.7708126 21.1028487,12.8777621 20.9724162,12.8794696 C20.8424038,12.8772197 20.7383539,12.7704109 20.7389339,12.6398176 L20.7389339,11.4817142 L20.739954,11.4817142 Z M19.7496595,11.4796452 C19.7496595,11.3490318 19.8551296,11.2410378 19.9821217,11.2410378 C20.1125341,11.2432877 20.2167641,11.3507192 20.215624,11.4817142 L20.2145839,12.6387931 C20.2151639,12.7693864 20.111114,12.8761751 19.9811016,12.878425 C19.8513092,12.8756327 19.7475793,12.7691654 19.7475993,12.6387931 L19.7496595,11.4796452 Z M22.5751292,16.3157328 L13.4258559,16.3157328 C13.3346272,16.3168577 13.2614202,16.3917464 13.2620002,16.4833686 C13.2620002,16.5749104 13.3357273,16.65 13.4258559,16.65 L22.5751292,16.65 C22.6187733,16.6497188 22.6604973,16.6319809 22.6910602,16.6007036 C22.7216432,16.5694263 22.7385248,16.527201 22.7379647,16.4833686 C22.7387848,16.4393755 22.7220232,16.396889 22.6914403,16.3653706 C22.6608374,16.3338724 22.6189534,16.3159939 22.5751292,16.3157328 Z M22.5792296,14.1692108 L13.4217555,14.1692108 C13.2169359,14.1692108 13.05,14.3399603 13.05,14.5518303 C13.05,14.7626757 13.2169359,14.9334051 13.4217555,14.9334051 L22.5782095,14.9334051 C22.7855493,14.9305928 22.9516651,14.7600643 22.949965,14.5518303 C22.9513251,14.4517108 22.9130414,14.3551469 22.8435148,14.283392 C22.7739882,14.211637 22.6789191,14.1705768 22.5792296,14.1692108 Z M22.5751292,15.4229741 L13.4258559,15.4229741 C13.3346272,15.424099 13.2614202,15.4990078 13.2620002,15.59063 C13.2620002,15.6821718 13.3357273,15.7572413 13.4258559,15.7572413 L22.5751292,15.7572413 C22.6187733,15.7569802 22.6604973,15.7392423 22.6910602,15.707965 C22.7216432,15.6766877 22.7385248,15.6344423 22.7379647,15.59063 C22.7385448,15.4993895 22.6659579,15.4246615 22.5751292,15.4229741 L22.5751292,15.4229741 Z" id="Fill-2" fill="#8E4B9E"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C6A5CE"></path>\r
                <path d="M34.3596082,21.1704545 C35.2655716,21.1704545 36,21.9044128 36,22.8116116 L36,30.9838429 C36,31.8902289 35.2645731,32.625 34.3596082,32.625 L1.64039183,32.625 C0.734428437,32.625 0,31.8910417 0,30.9838429 L0,22.8116116 C0,21.9052256 0.73542691,21.1704545 1.64039183,21.1704545 L34.3596082,21.1704545 Z M13.1162842,24.2518555 L11.2059814,24.2518555 C10.9528564,24.2518555 10.7696045,24.3085449 10.6562256,24.4219238 C10.5617432,24.5164063 10.5066284,24.6594116 10.4908813,24.8509399 L10.4861572,24.9716797 L10.4861572,29.4330078 C10.4861572,29.6676758 10.5375732,29.8456543 10.6404053,29.9669434 C10.7432373,30.0882324 10.8882568,30.148877 11.0754639,30.148877 C11.2494873,30.148877 11.3905518,30.0908691 11.4986572,29.9748535 C11.5851416,29.882041 11.6370322,29.7478848 11.6543291,29.5723848 L11.6608154,29.4330078 L11.6608154,27.570166 L12.0721436,27.570166 C12.2804443,27.570166 12.4531494,27.5978516 12.5902588,27.6532227 C12.7273682,27.7085938 12.8625,27.8114258 12.9956543,27.9617188 C13.1021777,28.0819531 13.2200918,28.2460625 13.3493965,28.4540469 L13.4485107,28.6182617 L13.919165,29.4013672 C14.0299072,29.5859375 14.1175781,29.7256836 14.1821777,29.8206055 C14.2467773,29.9155273 14.3239014,29.9939697 14.4135498,30.0559326 C14.5031982,30.1178955 14.6139404,30.148877 14.7457764,30.148877 C14.8565186,30.148877 14.9540771,30.127124 15.0384521,30.0836182 C15.1228271,30.0401123 15.1894043,29.9807861 15.2381836,29.9056396 C15.2869629,29.8304932 15.3113525,29.7546875 15.3113525,29.6782227 C15.3113525,29.6043945 15.2698242,29.4692627 15.1867676,29.2728271 C15.1037109,29.0763916 14.9876953,28.8562256 14.8387207,28.6123291 C14.6897461,28.3684326 14.5203369,28.1383789 14.3304932,27.922168 C14.1406494,27.705957 13.9415771,27.5424805 13.7332764,27.4317383 C14.2289795,27.3236328 14.5987793,27.1403809 14.8426758,26.8819824 C15.0865723,26.623584 15.2085205,26.2781738 15.2085205,25.845752 C15.2085205,25.6348145 15.1722656,25.435083 15.0997559,25.2465576 C15.0272461,25.0580322 14.9211182,24.8925781 14.7813721,24.7501953 C14.641626,24.6078125 14.4807861,24.4983887 14.2988525,24.4219238 C14.1485596,24.3533691 13.9798096,24.3078857 13.7926025,24.2854736 C13.6521973,24.2686646 13.4902863,24.2581589 13.3068695,24.2539566 L13.1162842,24.2518555 Z M18.5703369,24.1529785 C18.4094971,24.1529785 18.2743652,24.18396 18.1649414,24.2459229 C18.0555176,24.3078857 17.9671875,24.3889648 17.8999512,24.4891602 C17.8327148,24.5893555 17.7694336,24.7152588 17.7101074,24.8668701 C17.6705566,24.9679443 17.6348145,25.0593506 17.6028809,25.1410889 L17.5578369,25.2564453 L16.0746826,29.0216797 C16.0140381,29.1719727 15.9705322,29.2893066 15.944165,29.3736816 C15.9177979,29.4580566 15.9046143,29.5397949 15.9046143,29.6188965 C15.9046143,29.7560059 15.9613037,29.8786133 16.0746826,29.9867188 C16.1880615,30.0948242 16.3185791,30.148877 16.4662354,30.148877 C16.6402588,30.148877 16.7655029,30.0981201 16.8419678,29.9966064 C16.9056885,29.9120117 16.9803955,29.7619568 17.0660889,29.5464417 L17.1188232,29.4092773 L17.3956787,28.6657227 L19.7529053,28.6657227 L20.0297607,29.393457 C20.0666748,29.4831055 20.1108398,29.5865967 20.1622559,29.7039307 C20.2136719,29.8212646 20.261792,29.9082764 20.3066162,29.9649658 C20.3514404,30.0216553 20.4068115,30.0664795 20.4727295,30.0994385 C20.5386475,30.1323975 20.617749,30.148877 20.7100342,30.148877 C20.8682373,30.148877 21.0033691,30.0928467 21.1154297,29.9807861 C21.2274902,29.8687256 21.2835205,29.745459 21.2835205,29.6109863 C21.2835205,29.5033203 21.2423218,29.3416382 21.1599243,29.1259399 L21.105542,28.9900391 L19.5907471,25.2485352 C19.5195557,25.0613281 19.4608887,24.9116943 19.4147461,24.7996338 C19.3686035,24.6875732 19.3119141,24.5827637 19.2446777,24.4852051 C19.1774414,24.3876465 19.0891113,24.3078857 18.9796875,24.2459229 C18.8702637,24.18396 18.7338135,24.1529785 18.5703369,24.1529785 Z M24.7679443,24.2518555 L22.8576416,24.2518555 C22.6045166,24.2518555 22.4212646,24.3085449 22.3078857,24.4219238 C22.2134033,24.5164063 22.1582886,24.6594116 22.1425415,24.8509399 L22.1378174,24.9716797 L22.1378174,29.4330078 C22.1378174,29.6676758 22.1892334,29.8456543 22.2920654,29.9669434 C22.3948975,30.0882324 22.539917,30.148877 22.727124,30.148877 C22.9011475,30.148877 23.0422119,30.0908691 23.1503174,29.9748535 C23.2368018,29.882041 23.2886924,29.7478848 23.3059893,29.5723848 L23.3124756,29.4330078 L23.3124756,27.570166 L23.7238037,27.570166 C23.9321045,27.570166 24.1048096,27.5978516 24.2419189,27.6532227 C24.3790283,27.7085938 24.5141602,27.8114258 24.6473145,27.9617188 C24.7538379,28.0819531 24.871752,28.2460625 25.0010566,28.4540469 L25.1001709,28.6182617 L25.5708252,29.4013672 C25.6815674,29.5859375 25.7692383,29.7256836 25.8338379,29.8206055 C25.8984375,29.9155273 25.9755615,29.9939697 26.06521,30.0559326 C26.1548584,30.1178955 26.2656006,30.148877 26.3974365,30.148877 C26.5081787,30.148877 26.6057373,30.127124 26.6901123,30.0836182 C26.7744873,30.0401123 26.8410645,29.9807861 26.8898437,29.9056396 C26.938623,29.8304932 26.9630127,29.7546875 26.9630127,29.6782227 C26.9630127,29.6043945 26.9214844,29.4692627 26.8384277,29.2728271 C26.7553711,29.0763916 26.6393555,28.8562256 26.4903809,28.6123291 C26.3414062,28.3684326 26.1719971,28.1383789 25.9821533,27.922168 C25.7923096,27.705957 25.5932373,27.5424805 25.3849365,27.4317383 C25.8806396,27.3236328 26.2504395,27.1403809 26.4943359,26.8819824 C26.7382324,26.623584 26.8601807,26.2781738 26.8601807,25.845752 C26.8601807,25.6348145 26.8239258,25.435083 26.751416,25.2465576 C26.6789062,25.0580322 26.5727783,24.8925781 26.4330322,24.7501953 C26.2932861,24.6078125 26.1324463,24.4983887 25.9505127,24.4219238 C25.8002197,24.3533691 25.6314697,24.3078857 25.4442627,24.2854736 C25.3038574,24.2686646 25.1419464,24.2581589 24.9585297,24.2539566 L24.7679443,24.2518555 Z M18.5624268,25.390918 L19.436499,27.7837402 L17.7041748,27.7837402 L18.5624268,25.390918 Z M12.7445068,25.1338379 C13.1479248,25.1338379 13.4181885,25.1628418 13.5552979,25.2208496 C13.7003174,25.2814941 13.8136963,25.3731201 13.8954346,25.4957275 C13.9771729,25.618335 14.018042,25.7600586 14.018042,25.9208984 C14.018042,26.1265625 13.9672852,26.2880615 13.8657715,26.4053955 C13.7642578,26.5227295 13.6172607,26.6057861 13.4247803,26.6545654 C13.2322998,26.7033447 12.9949951,26.7277344 12.7128662,26.7277344 L11.6608154,26.7277344 L11.6608154,25.1338379 L12.7445068,25.1338379 Z M24.396167,25.1338379 C24.799585,25.1338379 25.0698486,25.1628418 25.206958,25.2208496 C25.3519775,25.2814941 25.4653564,25.3731201 25.5470947,25.4957275 C25.628833,25.618335 25.6697021,25.7600586 25.6697021,25.9208984 C25.6697021,26.1265625 25.6189453,26.2880615 25.5174316,26.4053955 C25.415918,26.5227295 25.2689209,26.6057861 25.0764404,26.6545654 C24.88396,26.7033447 24.6466553,26.7277344 24.3645264,26.7277344 L23.3124756,26.7277344 L23.3124756,25.1338379 L24.396167,25.1338379 Z" id="形状结合" fill="#8E4B9E"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),Q6=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_rm</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-701.000000, -5275.000000)">\r
            <g id="icon_rm" transform="translate(701.000000, 5275.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#E8DBEC"></path>\r
                <path d="M22.0546307,13.608 C22.0546307,13.8327805 21.9983179,14.057561 21.8293793,14.1699512 C21.7167536,14.2823415 18.8447983,16.6425366 15.1281503,18.1598049 C15.0718374,18.216 14.9592117,18.216 14.846586,18.216 C14.7339603,18.216 14.6213346,18.1598049 14.5087089,18.1036098 C14.2834575,17.9912195 14.1708318,17.8226341 14.114519,17.5416585 C14.114519,17.4854634 13.9455804,15.6310244 13.9455804,13.608 C13.9455804,11.5849756 14.114519,9.78673171 14.114519,9.67434146 C14.1708318,9.44956098 14.2834575,9.28097561 14.5087089,9.11239024 C14.6213346,9.05619512 14.7339603,9 14.846586,9 C14.9592117,9 15.0718374,9.05619512 15.1281503,9.11239024 C18.8447983,10.5734634 21.7167536,12.9898537 21.8293793,13.1022439 C21.9983179,13.2146341 22.0546307,13.4394146 22.0546307,13.608" id="Fill-4" fill="#8E4B9E"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C6A5CE"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M14.9684326,24.2518555 L13.0581299,24.2518555 C12.8050049,24.2518555 12.6217529,24.3085449 12.508374,24.4219238 C12.4138916,24.5164063 12.3587769,24.6594116 12.3430298,24.8509399 L12.3383057,24.9716797 L12.3383057,29.4330078 C12.3383057,29.6676758 12.3897217,29.8456543 12.4925537,29.9669434 C12.5953857,30.0882324 12.7404053,30.148877 12.9276123,30.148877 C13.1016357,30.148877 13.2427002,30.0908691 13.3508057,29.9748535 C13.43729,29.882041 13.4891807,29.7478848 13.5064775,29.5723848 L13.5129639,29.4330078 L13.5129639,27.570166 L13.924292,27.570166 C14.1325928,27.570166 14.3052979,27.5978516 14.4424072,27.6532227 C14.5795166,27.7085938 14.7146484,27.8114258 14.8478027,27.9617188 C14.9543262,28.0819531 15.0722402,28.2460625 15.2015449,28.4540469 L15.3006592,28.6182617 L15.7713135,29.4013672 C15.8820557,29.5859375 15.9697266,29.7256836 16.0343262,29.8206055 C16.0989258,29.9155273 16.1760498,29.9939697 16.2656982,30.0559326 C16.3553467,30.1178955 16.4660889,30.148877 16.5979248,30.148877 C16.708667,30.148877 16.8062256,30.127124 16.8906006,30.0836182 C16.9749756,30.0401123 17.0415527,29.9807861 17.090332,29.9056396 C17.1391113,29.8304932 17.163501,29.7546875 17.163501,29.6782227 C17.163501,29.6043945 17.1219727,29.4692627 17.038916,29.2728271 C16.9558594,29.0763916 16.8398437,28.8562256 16.6908691,28.6123291 C16.5418945,28.3684326 16.3724854,28.1383789 16.1826416,27.922168 C15.9927979,27.705957 15.7937256,27.5424805 15.5854248,27.4317383 C16.0811279,27.3236328 16.4509277,27.1403809 16.6948242,26.8819824 C16.9387207,26.623584 17.0606689,26.2781738 17.0606689,25.845752 C17.0606689,25.6348145 17.0244141,25.435083 16.9519043,25.2465576 C16.8793945,25.0580322 16.7732666,24.8925781 16.6335205,24.7501953 C16.4937744,24.6078125 16.3329346,24.4983887 16.151001,24.4219238 C16.000708,24.3533691 15.831958,24.3078857 15.644751,24.2854736 C15.5043457,24.2686646 15.3424347,24.2581589 15.1590179,24.2539566 L14.9684326,24.2518555 Z M19.2161865,24.2518555 L18.8523193,24.2518555 C18.6281982,24.2518555 18.4502197,24.2960205 18.3183838,24.3843506 C18.2085205,24.457959 18.1444336,24.5887878 18.126123,24.7768372 L18.1206299,24.8965332 L18.1206299,29.4844238 C18.1206299,29.7085449 18.1707275,29.8753174 18.2709229,29.9847412 C18.3711182,30.094165 18.5003174,30.148877 18.6585205,30.148877 C18.8219971,30.148877 18.9531738,30.0935059 19.0520508,29.9827637 C19.1311523,29.8941699 19.1786133,29.7701387 19.1944336,29.6106699 L19.2003662,29.4844238 L19.2003662,25.4818848 L20.1298096,29.1759277 C20.1614502,29.2972168 20.1924316,29.4138916 20.2227539,29.5259521 C20.2530762,29.6380127 20.2946045,29.7401855 20.3473389,29.8324707 C20.4000732,29.9247559 20.4739014,30.0005615 20.5688232,30.0598877 C20.6637451,30.1192139 20.7797607,30.148877 20.9168701,30.148877 C21.0988037,30.148877 21.2411865,30.1027344 21.3440186,30.0104492 C21.4468506,29.9181641 21.5173828,29.8159912 21.5556152,29.7039307 C21.5862012,29.6142822 21.623959,29.4837119 21.6688887,29.3122197 L21.7039307,29.1759277 L22.633374,25.4818848 L22.633374,29.4844238 C22.633374,29.7059082 22.6834717,29.8720215 22.783667,29.9827637 C22.8838623,30.0935059 23.0117432,30.148877 23.1673096,30.148877 C23.3334229,30.148877 23.465918,30.0935059 23.5647949,29.9827637 C23.6438965,29.8941699 23.6913574,29.7701387 23.7071777,29.6106699 L23.7131104,29.4844238 L23.7131104,24.8965332 C23.7131104,24.6434082 23.6471924,24.4726807 23.5153564,24.3843506 C23.4098877,24.3136865 23.2748877,24.2712881 23.1103564,24.2571553 L22.9814209,24.2518555 L22.6175537,24.2518555 C22.3987061,24.2518555 22.2398437,24.2716309 22.1409668,24.3111816 C22.0420898,24.3507324 21.9689209,24.4219238 21.92146,24.5247559 C21.8834912,24.6070215 21.8413037,24.7306309 21.7948975,24.895584 L21.7593018,25.0270508 L20.9168701,28.2029785 L20.0744385,25.0270508 C20.0137939,24.7950195 19.9597412,24.6275879 19.9122803,24.5247559 C19.8648193,24.4219238 19.7916504,24.3507324 19.6927734,24.3111816 C19.6136719,24.279541 19.4961797,24.2605566 19.3402969,24.2542285 L19.2161865,24.2518555 Z M14.5966553,25.1338379 C15.0000732,25.1338379 15.2703369,25.1628418 15.4074463,25.2208496 C15.5524658,25.2814941 15.6658447,25.3731201 15.747583,25.4957275 C15.8293213,25.618335 15.8701904,25.7600586 15.8701904,25.9208984 C15.8701904,26.1265625 15.8194336,26.2880615 15.7179199,26.4053955 C15.6164062,26.5227295 15.4694092,26.6057861 15.2769287,26.6545654 C15.0844482,26.7033447 14.8471436,26.7277344 14.5650146,26.7277344 L13.5129639,26.7277344 L13.5129639,25.1338379 L14.5966553,25.1338379 Z" id="形状结合" fill="#8E4B9E"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),tn=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_svg</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-506.000000, -5331.000000)">\r
            <g id="icon_svg" transform="translate(506.000000, 5331.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#DAEAF9"></path>\r
                <g id="svg" transform="translate(11.000000, 5.000000)" fill="#1A85CD">\r
                    <path d="M3.09748808,8.14801755 L3.38448722,7.79801861 C3.60317193,8.01651651 3.89790975,8.14193686 4.20698476,8.14801755 C4.58148363,8.14801755 4.79148301,7.97301807 4.79148301,7.71051887 C4.79148301,7.44801966 4.58148363,7.33951997 4.2944845,7.21352036 L3.86748577,7.02802092 C3.50762587,6.91888963 3.25535516,6.59514221 3.23748767,6.21952334 C3.25412515,5.97008266 3.37188218,5.73824794 3.56349961,5.57768575 C3.75511704,5.41712356 4.00398163,5.34175465 4.25248462,5.36902589 C4.60366472,5.37048432 4.93985597,5.51150837 5.18698182,5.76102472 L4.93148258,6.07252379 C4.74924511,5.89629061 4.50599431,5.79723487 4.25248462,5.79602461 C3.93748556,5.79602461 3.73098618,5.94652416 3.73098618,6.19502342 C3.73098618,6.44352268 3.97948543,6.54502237 4.23148467,6.66052201 L4.65498342,6.84252148 C5.03621667,6.95019149 5.29478389,7.30409633 5.28148153,7.70001889 C5.28148153,8.1970174 4.87198276,8.5995283 4.1929848,8.5995283 C3.78221815,8.60114307 3.38782447,8.43859743 3.09748808,8.14801755 L3.09748808,8.14801755 Z M5.45298101,5.45652563 L5.97097946,5.45652563 L6.42947808,7.05602084 C6.53447777,7.40601978 6.60097756,7.71051887 6.70947725,8.0675178 L6.73047718,8.0675178 C6.83547687,7.71751885 6.91247663,7.40951977 7.01047635,7.05602084 L7.46897497,5.45652563 L7.96597348,5.45652563 L6.99997638,8.54701637 L6.42247811,8.54701637 L5.45298101,5.45652563 Z M8.14797293,7.000021 C8.14797293,5.99202402 8.77097106,5.38652584 9.58646861,5.38652584 C9.93493428,5.38111522 10.269389,5.5235446 10.5069658,5.77852466 L10.2409667,6.09002373 C10.0787271,5.90991369 9.84633438,5.80903992 9.60396857,5.81352455 C9.02997029,5.81352455 8.64497144,6.25802321 8.64497144,6.98602104 C8.64497144,7.71401887 8.99497039,8.16901749 9.62146851,8.16901749 C9.79604744,8.17576124 9.96769174,8.12266264 10.1079671,8.01851795 L10.1079671,7.3010201 L9.49896888,7.3010201 L9.49896888,6.90202129 L10.5489657,6.90202129 L10.5489657,8.24251727 C10.2802912,8.47991748 9.93077605,8.60519174 9.57246866,8.59251623 C8.74997113,8.6030162 8.14797293,8.02201794 8.14797293,7.000021 L8.14797293,7.000021 Z" id="形状"></path>\r
                    <path d="M11.1999638,14.0000009 L2.44999002,14.0000009 C1.38764399,13.9980737 0.52692209,13.1373518 0.524995803,12.0750058 L0.524995803,1.92503623 C0.52692209,0.862690196 1.38764399,0.00196829527 2.44999002,8.971804e-07 L9.50946884,8.971804e-07 C9.83265802,-0.00261137763 10.1436507,0.123296575 10.3739663,0.350040947 L12.9674585,2.94353317 C13.0698393,3.04386469 13.1267424,3.18169652 13.1249987,3.32503202 L13.1249987,12.0750058 C13.1230317,13.1373518 12.2623098,13.9980737 11.1999638,14.0000009 L11.1999638,14.0000009 Z M2.44999002,1.05003006 C1.96674231,1.05003006 1.57499264,1.44178852 1.57499264,1.92503623 L1.57499264,12.0750058 C1.57499264,12.5582535 1.96674231,12.9500031 2.44999002,12.9500031 L11.1999638,12.9500031 C11.6832115,12.9500031 12.0749611,12.5582535 12.0749611,12.0750058 L12.0749611,3.54203138 L9.6249685,1.09203872 C9.59281233,1.0645357 9.55178025,1.04961494 9.50946884,1.05003006 L2.44999002,1.05003006 Z" id="形状"></path>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#8CC2E6"></path>\r
                <rect id="Rectangle-9" fill="#1A85CD" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="SVG" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="8.1" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="9.3581543" y="30.05">SVG</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),en=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_tiff</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-571.000000, -5275.000000)">\r
            <g id="icon_tiff" transform="translate(571.000000, 5275.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#E6EBEF"></path>\r
                <path d="M12.6,17.547774 C12.6,18.0460597 13.0101849,18.45 13.502226,18.45 L22.497774,18.45 C22.9960597,18.45 23.4,18.0398151 23.4,17.547774 L23.4,8.55222597 C23.4,8.05394032 22.9898151,7.65 22.497774,7.65 L13.502226,7.65 C13.0039403,7.65 12.6,8.06018486 12.6,8.55222597 L12.6,17.547774 Z M13.6851675,14.5739038 C13.6851675,14.9451874 13.9923018,15.2461722 14.3623228,15.2461722 L21.6376772,15.2461722 C22.0116598,15.2461722 22.3148325,14.9494889 22.3148325,14.5739038 L22.3148325,9.40743587 C22.3148325,9.03615228 22.0076982,8.73516746 21.6376772,8.73516746 L14.3623228,8.73516746 C13.9883402,8.73516746 13.6851675,9.03185077 13.6851675,9.40743587 L13.6851675,14.5739038 Z M17.354067,13.0758373 L19.369378,10.3370813 L20.0411483,11.4222488 L20.7129187,11.2155502 L21.2296651,14.1093301 L14.4602871,14.1093301 L16.2688995,12.404067 L17.354067,13.0758373 Z M15.3904306,11.1638756 C14.9253589,11.1638756 14.5119617,10.8538278 14.5119617,10.388756 C14.5119617,9.97535885 14.9253589,9.61363636 15.3904306,9.61363636 C15.8555024,9.61363636 16.2172249,9.97535885 16.2172249,10.388756 C16.2172249,10.8538278 15.8555024,11.1638756 15.3904306,11.1638756 L15.3904306,11.1638756 Z" id="Fill-4" fill="#8199AE"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C2CDD7"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M13.7067627,24.2518555 L9.97316895,24.2518555 C9.78068848,24.2518555 9.63369141,24.2953613 9.53217773,24.382373 C9.43066406,24.4693848 9.37990723,24.5880371 9.37990723,24.7383301 C9.37990723,24.8833496 9.42868652,25.0000244 9.52624512,25.0883545 C9.60429199,25.1590186 9.71524512,25.201417 9.85910449,25.2155498 L9.97316895,25.2208496 L11.2546143,25.2208496 L11.2546143,29.4330078 C11.2546143,29.6729492 11.3093262,29.8522461 11.41875,29.9708984 C11.5281738,30.0895508 11.6698975,30.148877 11.8439209,30.148877 C12.0153076,30.148877 12.1550537,30.09021 12.2631592,29.972876 C12.3496436,29.8790088 12.4015342,29.7450635 12.4188311,29.57104 L12.4253174,29.4330078 L12.4253174,25.2208496 L13.7067627,25.2208496 C13.9071533,25.2208496 14.0574463,25.1766846 14.1576416,25.0883545 C14.2578369,25.0000244 14.3079346,24.8833496 14.3079346,24.7383301 C14.3079346,24.5933105 14.2584961,24.4759766 14.1596191,24.3863281 C14.0805176,24.3146094 13.9680879,24.2715781 13.8223301,24.2572344 L13.7067627,24.2518555 Z M15.6328857,24.1529785 C15.461499,24.1529785 15.3217529,24.2123047 15.2136475,24.330957 C15.1271631,24.4258789 15.0752725,24.5587695 15.0579756,24.7296289 L15.0514893,24.8648926 L15.0514893,29.4330078 C15.0514893,29.6703125 15.1062012,29.8489502 15.215625,29.9689209 C15.3250488,30.0888916 15.4641357,30.148877 15.6328857,30.148877 C15.8095459,30.148877 15.9525879,30.0895508 16.0620117,29.9708984 C16.1495508,29.8759766 16.2020742,29.7422422 16.219582,29.5696953 L16.2261475,29.4330078 L16.2261475,24.8648926 C16.2261475,24.6249512 16.1714355,24.4463135 16.0620117,24.3289795 C15.9525879,24.2116455 15.8095459,24.1529785 15.6328857,24.1529785 Z M21.0038818,24.2518555 L18.2353271,24.2518555 C18.0692139,24.2518555 17.9327637,24.2762451 17.8259766,24.3250244 C17.7191895,24.3738037 17.6407471,24.4515869 17.5906494,24.558374 C17.5530762,24.6384644 17.5295929,24.7359818 17.5201996,24.8509262 L17.5155029,24.9716797 L17.5155029,29.4330078 C17.5155029,29.6729492 17.5702148,29.8522461 17.6796387,29.9708984 C17.7890625,30.0895508 17.9307861,30.148877 18.1048096,30.148877 C18.2761963,30.148877 18.4166016,30.09021 18.5260254,29.972876 C18.6135645,29.8790088 18.6660879,29.7450635 18.6835957,29.57104 L18.6901611,29.4330078 L18.6901611,27.5227051 L20.6241943,27.5227051 C20.8008545,27.5227051 20.9340088,27.4831543 21.0236572,27.4040527 C21.1133057,27.3249512 21.1581299,27.2181641 21.1581299,27.0836914 C21.1581299,26.9492188 21.1139648,26.8417725 21.0256348,26.7613525 C20.9549707,26.6970166 20.8551973,26.658415 20.7263145,26.6455479 L20.6241943,26.6407227 L18.6901611,26.6407227 L18.6901611,25.1575684 L21.0038818,25.1575684 C21.1910889,25.1575684 21.3301758,25.11604 21.4211426,25.0329834 C21.5121094,24.9499268 21.5575928,24.8398438 21.5575928,24.7027344 C21.5575928,24.5682617 21.5121094,24.4594971 21.4211426,24.3764404 C21.3483691,24.3099951 21.2447988,24.2701279 21.1104316,24.2568389 L21.0038818,24.2518555 Z M25.8963135,24.2518555 L23.1277588,24.2518555 C22.9616455,24.2518555 22.8251953,24.2762451 22.7184082,24.3250244 C22.6116211,24.3738037 22.5331787,24.4515869 22.4830811,24.558374 C22.4455078,24.6384644 22.4220245,24.7359818 22.4126312,24.8509262 L22.4079346,24.9716797 L22.4079346,29.4330078 C22.4079346,29.6729492 22.4626465,29.8522461 22.5720703,29.9708984 C22.6814941,30.0895508 22.8232178,30.148877 22.9972412,30.148877 C23.1686279,30.148877 23.3090332,30.09021 23.418457,29.972876 C23.5059961,29.8790088 23.5585195,29.7450635 23.5760273,29.57104 L23.5825928,29.4330078 L23.5825928,27.5227051 L25.516626,27.5227051 C25.6932861,27.5227051 25.8264404,27.4831543 25.9160889,27.4040527 C26.0057373,27.3249512 26.0505615,27.2181641 26.0505615,27.0836914 C26.0505615,26.9492188 26.0063965,26.8417725 25.9180664,26.7613525 C25.8474023,26.6970166 25.7476289,26.658415 25.6187461,26.6455479 L25.516626,26.6407227 L23.5825928,26.6407227 L23.5825928,25.1575684 L25.8963135,25.1575684 C26.0835205,25.1575684 26.2226074,25.11604 26.3135742,25.0329834 C26.404541,24.9499268 26.4500244,24.8398438 26.4500244,24.7027344 C26.4500244,24.5682617 26.404541,24.4594971 26.3135742,24.3764404 C26.2408008,24.3099951 26.1372305,24.2701279 26.0028633,24.2568389 L25.8963135,24.2518555 Z" id="形状结合" fill="#8199AE"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),nn=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_tmp</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-636.000000, -5214.000000)">\r
            <g id="icon_tmp" transform="translate(636.000000, 5214.000000)">\r
                <path d="M23.604,0 L23.6045455,6.21998174 C23.6045455,6.99128734 24.2096546,9.01596452 26.960151,9.01596452 L32.604,8.985 L32.5571138,33.0899335 C32.5571138,34.4653386 31.4404421,35.5573497 30.0474034,35.639316 L29.878966,35.6442572 L5.82814787,35.6442572 C4.35779218,35.6442572 3.15,34.5203548 3.15,33.0899335 L3.15,33.0899335 L3.15,2.55432373 C3.15,1.12390244 4.35779218,0 5.82814787,0 L5.82814787,0 L23.604,0 Z" id="Combined-Shape" fill="#E6EBEF"></path>\r
                <path d="M22.497774,7.65 C22.9898151,7.65 23.4,8.05394032 23.4,8.55222597 L23.4,8.55222597 L23.4,17.547774 C23.4,18.0398151 22.9960597,18.45 22.497774,18.45 L22.497774,18.45 L13.502226,18.45 C13.0101849,18.45 12.6,18.0460597 12.6,17.547774 L12.6,17.547774 L12.6,8.55222597 C12.6,8.06018486 13.0039403,7.65 13.502226,7.65 L13.502226,7.65 Z M14.622892,8.1 L14.177108,8.1 C14.0516797,8.1 13.95,8.21035767 13.95,8.327108 L13.95,8.772892 C13.95,8.89832028 14.0603577,9 14.177108,9 L14.622892,9 C14.7483203,9 14.85,8.88964233 14.85,8.772892 L14.85,8.327108 C14.85,8.20167972 14.7396423,8.1 14.622892,8.1 Z M22.0877245,8.1 L21.6419405,8.1 C21.5165123,8.1 21.4148325,8.21035767 21.4148325,8.327108 L21.4148325,8.772892 C21.4148325,8.89832028 21.5251902,9 21.6419405,9 L22.0877245,9 C22.2131528,9 22.3148325,8.88964233 22.3148325,8.772892 L22.3148325,8.327108 C22.3148325,8.20167972 22.2044749,8.1 22.0877245,8.1 Z M20.2877245,8.1 L19.8419405,8.1 C19.7165123,8.1 19.6148325,8.21035767 19.6148325,8.327108 L19.6148325,8.772892 C19.6148325,8.89832028 19.7251902,9 19.8419405,9 L20.2877245,9 C20.4131528,9 20.5148325,8.88964233 20.5148325,8.772892 L20.5148325,8.327108 C20.5148325,8.20167972 20.4044749,8.1 20.2877245,8.1 Z M18.4877245,8.1 L18.0419405,8.1 C17.9165123,8.1 17.8148325,8.21035767 17.8148325,8.327108 L17.8148325,8.772892 C17.8148325,8.89832028 17.9251902,9 18.0419405,9 L18.4877245,9 C18.6131528,9 18.7148325,8.88964233 18.7148325,8.772892 L18.7148325,8.327108 C18.7148325,8.20167972 18.6044749,8.1 18.4877245,8.1 Z M21.6376772,9.63516746 C22.0076982,9.63516746 22.3148325,9.93729206 22.3148325,10.3099816 L22.3148325,10.3099816 L22.3148325,16.8751858 C22.3148325,17.249394 22.0116598,17.55 21.6376772,17.55 L21.6376772,17.55 L14.3623228,17.55 C13.9923018,17.55 13.6851675,17.2478754 13.6851675,16.8751858 L13.6851675,16.8751858 L13.6851675,10.3099816 C13.6851675,9.93577343 13.9883402,9.63516746 14.3623228,9.63516746 L14.3623228,9.63516746 Z M15.745784,13.95 L14.854216,13.95 C14.6033594,13.95 14.4,14.1576416 14.4,14.4053211 L14.4,15.7446789 C14.4,15.9961458 14.5926315,16.2 14.854216,16.2 L15.745784,16.2 C15.9966406,16.2 16.2,15.9923584 16.2,15.7446789 L16.2,14.4053211 C16.2,14.1538542 16.0073685,13.95 15.745784,13.95 Z M18.445784,13.95 L17.554216,13.95 C17.3033594,13.95 17.1,14.1576416 17.1,14.4053211 L17.1,15.7446789 C17.1,15.9961458 17.2926315,16.2 17.554216,16.2 L18.445784,16.2 C18.6966406,16.2 18.9,15.9923584 18.9,15.7446789 L18.9,14.4053211 C18.9,14.1538542 18.7073685,13.95 18.445784,13.95 Z M21.145784,13.95 L20.254216,13.95 C20.0033594,13.95 19.8,14.1576416 19.8,14.4053211 L19.8,15.7446789 C19.8,15.9961458 19.9926315,16.2 20.254216,16.2 L21.145784,16.2 C21.3966406,16.2 21.6,15.9923584 21.6,15.7446789 L21.6,14.4053211 C21.6,14.1538542 21.4073685,13.95 21.145784,13.95 Z M15.745784,10.8 L14.854216,10.8 C14.6033594,10.8 14.4,11.0076416 14.4,11.2553211 L14.4,12.5946789 C14.4,12.8461458 14.5926315,13.05 14.854216,13.05 L15.745784,13.05 C15.9966406,13.05 16.2,12.8423584 16.2,12.5946789 L16.2,11.2553211 C16.2,11.0038542 16.0073685,10.8 15.745784,10.8 Z M18.445784,10.8 L17.554216,10.8 C17.3033594,10.8 17.1,11.0076416 17.1,11.2553211 L17.1,12.5946789 C17.1,12.8461458 17.2926315,13.05 17.554216,13.05 L18.445784,13.05 C18.6966406,13.05 18.9,12.8423584 18.9,12.5946789 L18.9,11.2553211 C18.9,11.0038542 18.7073685,10.8 18.445784,10.8 Z M21.145784,10.8 L20.254216,10.8 C20.0033594,10.8 19.8,11.0076416 19.8,11.2553211 L19.8,12.5946789 C19.8,12.8461458 19.9926315,13.05 20.254216,13.05 L21.145784,13.05 C21.3966406,13.05 21.6,12.8423584 21.6,12.5946789 L21.6,11.2553211 C21.6,11.0038542 21.4073685,10.8 21.145784,10.8 Z" id="Fill-4" fill="#8199AE"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C2CDD7"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M13.7917969,24.2518555 L10.0582031,24.2518555 C9.86572266,24.2518555 9.71872559,24.2953613 9.61721191,24.382373 C9.51569824,24.4693848 9.46494141,24.5880371 9.46494141,24.7383301 C9.46494141,24.8833496 9.5137207,25.0000244 9.6112793,25.0883545 C9.68932617,25.1590186 9.8002793,25.201417 9.94413867,25.2155498 L10.0582031,25.2208496 L11.3396484,25.2208496 L11.3396484,29.4330078 C11.3396484,29.6729492 11.3943604,29.8522461 11.5037842,29.9708984 C11.613208,30.0895508 11.7549316,30.148877 11.9289551,30.148877 C12.1003418,30.148877 12.2400879,30.09021 12.3481934,29.972876 C12.4346777,29.8790088 12.4865684,29.7450635 12.5038652,29.57104 L12.5103516,29.4330078 L12.5103516,25.2208496 L13.7917969,25.2208496 C13.9921875,25.2208496 14.1424805,25.1766846 14.2426758,25.0883545 C14.3428711,25.0000244 14.3929688,24.8833496 14.3929688,24.7383301 C14.3929688,24.5933105 14.3435303,24.4759766 14.2446533,24.3863281 C14.1655518,24.3146094 14.0531221,24.2715781 13.9073643,24.2572344 L13.7917969,24.2518555 Z M16.1332031,24.2518555 L15.7693359,24.2518555 C15.5452148,24.2518555 15.3672363,24.2960205 15.2354004,24.3843506 C15.1255371,24.457959 15.0614502,24.5887878 15.0431396,24.7768372 L15.0376465,24.8965332 L15.0376465,29.4844238 C15.0376465,29.7085449 15.0877441,29.8753174 15.1879395,29.9847412 C15.2881348,30.094165 15.417334,30.148877 15.5755371,30.148877 C15.7390137,30.148877 15.8701904,30.0935059 15.9690674,29.9827637 C16.0481689,29.8941699 16.0956299,29.7701387 16.1114502,29.6106699 L16.1173828,29.4844238 L16.1173828,25.4818848 L17.0468262,29.1759277 C17.0784668,29.2972168 17.1094482,29.4138916 17.1397705,29.5259521 C17.1700928,29.6380127 17.2116211,29.7401855 17.2643555,29.8324707 C17.3170898,29.9247559 17.390918,30.0005615 17.4858398,30.0598877 C17.5807617,30.1192139 17.6967773,30.148877 17.8338867,30.148877 C18.0158203,30.148877 18.1582031,30.1027344 18.2610352,30.0104492 C18.3638672,29.9181641 18.4343994,29.8159912 18.4726318,29.7039307 C18.5032178,29.6142822 18.5409756,29.4837119 18.5859053,29.3122197 L18.6209473,29.1759277 L19.5503906,25.4818848 L19.5503906,29.4844238 C19.5503906,29.7059082 19.6004883,29.8720215 19.7006836,29.9827637 C19.8008789,30.0935059 19.9287598,30.148877 20.0843262,30.148877 C20.2504395,30.148877 20.3829346,30.0935059 20.4818115,29.9827637 C20.5609131,29.8941699 20.608374,29.7701387 20.6241943,29.6106699 L20.630127,29.4844238 L20.630127,24.8965332 C20.630127,24.6434082 20.564209,24.4726807 20.432373,24.3843506 C20.3269043,24.3136865 20.1919043,24.2712881 20.027373,24.2571553 L19.8984375,24.2518555 L19.5345703,24.2518555 C19.3157227,24.2518555 19.1568604,24.2716309 19.0579834,24.3111816 C18.9591064,24.3507324 18.8859375,24.4219238 18.8384766,24.5247559 C18.8005078,24.6070215 18.7583203,24.7306309 18.7119141,24.895584 L18.6763184,25.0270508 L17.8338867,28.2029785 L16.9914551,25.0270508 C16.9308105,24.7950195 16.8767578,24.6275879 16.8292969,24.5247559 C16.7818359,24.4219238 16.708667,24.3507324 16.60979,24.3111816 C16.5306885,24.279541 16.4131963,24.2605566 16.2573135,24.2542285 L16.1332031,24.2518555 Z M24.0868652,24.2518555 L22.5799805,24.2518555 C22.321582,24.2518555 22.1330566,24.3072266 22.0144043,24.4179688 C21.9155273,24.5102539 21.8578491,24.6538086 21.8413696,24.8486328 L21.8364258,24.9716797 L21.8364258,29.440918 C21.8364258,29.6729492 21.8904785,29.8489502 21.998584,29.9689209 C22.1066895,30.0888916 22.2490723,30.148877 22.4257324,30.148877 C22.5944824,30.148877 22.7342285,30.0882324 22.8449707,29.9669434 C22.9335645,29.8699121 22.9867207,29.7365996 23.0044395,29.5670059 L23.011084,29.4330078 L23.011084,27.7916504 L24.0868652,27.7916504 C24.8066895,27.7916504 25.3485352,27.6393799 25.7124023,27.3348389 C26.0762695,27.0302979 26.2582031,26.5827148 26.2582031,25.9920898 C26.2582031,25.7152344 26.2133789,25.4660645 26.1237305,25.2445801 C26.034082,25.0230957 25.9029053,24.8358887 25.7302002,24.682959 C25.5574951,24.5300293 25.3524902,24.4206055 25.1151855,24.3546875 C24.9152344,24.2975586 24.6438721,24.2642334 24.3010986,24.2547119 L24.0868652,24.2518555 Z M23.8020996,25.1338379 C24.3268066,25.1338379 24.6735352,25.2195313 24.8422852,25.390918 C24.9925781,25.5517578 25.0677246,25.7600586 25.0677246,26.0158203 C25.0677246,26.2293945 25.0189453,26.4020996 24.9213867,26.5339355 C24.8238281,26.6657715 24.6821045,26.7606934 24.4962158,26.8187012 C24.3103271,26.876709 24.0789551,26.9057129 23.8020996,26.9057129 L23.011084,26.9057129 L23.011084,25.1338379 L23.8020996,25.1338379 Z" id="形状结合" fill="#8199AE"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),sn=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_txt</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-767.000000, -5384.000000)">\r
            <g id="icon_txt" transform="translate(767.000000, 5384.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#DAEAF9"></path>\r
                <g id="KHCFDC_TXT" transform="translate(12.000000, 6.000000)">\r
                    <rect id="矩形" fill="#000000" fill-rule="nonzero" opacity="0" x="0" y="0" width="12" height="12"></rect>\r
                    <path d="M2.4,0 L8,0 L11.2,3.2 L11.2,10.4 C11.2,11.28 10.48,12 9.6,12 L2.4,12 C1.52,12 0.800000004,11.28 0.800000004,10.4 L0.800000004,1.6 C0.800000004,0.72 1.52,0 2.4,0 Z M8,5.2 C8.24,5.2 8.4,5.36 8.4,5.6 C8.4,5.84 8.24,6 8,6 L8,6 L6.4,6 L6.4,9.6 C6.4,9.84 6.24,10 6,10 C5.76,10 5.6,9.84 5.6,9.6 L5.6,9.6 L5.6,6 L4,6 C3.76,6 3.6,5.84000002 3.6,5.6 C3.6,5.35999999 3.76,5.2 4,5.2 L4,5.2 Z M7,1 L10.2,4.2 L7.8,4.2 C7.32,4.2 7,3.88 7,3.4 L7,1 Z" id="形状" fill="#3D80CC"></path>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#8CC2E6"></path>\r
                <rect id="Rectangle-9" fill="#1A85CD" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="TXT" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="8.1" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="10.4912842" y="30.05">TXT</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),rn=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_unknown</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-703.000000, -5384.000000)">\r
            <g id="icon_unknown" transform="translate(703.000000, 5384.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#E6EBEF"></path>\r
                <g id="file-unknown-fill" transform="translate(12.000000, 5.000000)" fill="#8199AE">\r
                    <path d="M11.353125,3.5109375 C11.446875,3.6046875 11.5,3.73125 11.5,3.8640625 L11.5,12.5 C11.5,12.7765625 11.4545455,14 10,14 L2,14 C0.311852883,14 0.5,12.7765625 0.5,12.5 L0.5,1.5 C0.5,1.2234375 0.245212666,0 2,0 L7.6359375,0 C7.76875,0 7.896875,0.053125 7.990625,0.146875 L11.353125,3.5109375 Z M10.346875,4.09375 L7.40625,1.153125 L7.40625,4.09375 L10.346875,4.09375 Z M4.28125,7.578125 C4.28125,7.6625 4.35,7.7265625 4.434375,7.7265625 L4.940625,7.7265625 C5.025,7.7265625 5.09375,7.6609375 5.09375,7.5796875 C5.09375,7.1390625 5.496875,6.7734375 6,6.7734375 C6.503125,6.7734375 6.90625,7.1390625 6.90625,7.578125 C6.90625,7.9734375 6.578125,8.315625 6.1359375,8.3734375 C5.834375,8.4171875 5.596875,8.690625 5.59375,9 L5.59375,9.5 C5.59375,9.5859375 5.6640625,9.65625 5.75,9.65625 L6.25,9.65625 C6.3359375,9.65625 6.40625,9.5859375 6.40625,9.5 L6.40625,9.309375 C6.40625,9.215625 6.46875,9.1296875 6.5578125,9.1015625 C7.2546875,8.8765625 7.7296875,8.2578125 7.71875,7.55625 C7.70625,6.6890625 6.95,5.98125 6.0234375,5.96875 C5.0640625,5.9578125 4.28125,6.68125 4.28125,7.578125 Z M6,11.125 C6.27614063,11.125 6.5,10.9011406 6.5,10.625 C6.5,10.3488594 6.27614063,10.125 6,10.125 C5.72385938,10.125 5.5,10.3488594 5.5,10.625 C5.5,10.9011406 5.72385938,11.125 6,11.125 Z" id="形状"></path>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C2CDD7"></path>\r
                <rect id="Rectangle-9" fill="#8199AE" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="UNKNOWN" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="6" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="1.43554688" y="29.05">UNKNOWN</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),on=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_wav</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-830.000000, -5331.000000)">\r
            <g id="icon_wav" transform="translate(830.000000, 5331.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#E8DBEC"></path>\r
                <g id="wav" transform="translate(10.000000, 4.000000)">\r
                    <rect id="矩形" fill="#000000" fill-rule="nonzero" opacity="0" x="0" y="0" width="16" height="16"></rect>\r
                    <path d="M4.00600601,5.228 L2,5.228 L2,10.784 L4.00600601,10.784 L8.69069069,14 L8.69069069,2 L4.00600601,5.228 M12.7627628,8 C12.7627628,9.512 11.9339339,10.82 10.7327327,11.516 L11.3573574,12.608 C12.9309309,11.696 14,9.98 14,8.012 C14,6.056 12.9309309,4.328 11.3573574,3.416 L10.7327327,4.508 C11.9459459,5.18 12.7627628,6.488 12.7627628,8 Z M10.5885886,8 C10.5885886,8.708 10.2162162,9.308 9.62762763,9.644 L10.2282282,10.688 C11.1651652,10.16 11.7897898,9.152 11.7897898,8 C11.7897898,6.848 11.1651652,5.84 10.2282282,5.312 L9.62762763,6.356 C10.2042042,6.692 10.5885886,7.292 10.5885886,8 Z" id="形状" fill="#8E4B9E"></path>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C6A5CE"></path>\r
                <rect id="Rectangle-9" fill="#8E4B9E" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="WAV" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="8.1" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="8.70754395" y="30.05">WAV</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),ln=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_wma</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-896.000000, -5331.000000)">\r
            <g id="icon_wma" transform="translate(896.000000, 5331.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#FCECD3"></path>\r
                <path d="M12.4098361,17.9433962 C12.6995592,17.9433962 12.9344262,18.1782632 12.9344262,18.4679864 L12.9344262,18.4754098 C12.9344262,18.765133 12.6995592,19 12.4098361,19 L9.52459016,19 C9.23486702,19 9,18.765133 9,18.4754098 L9,18.4679864 C9,18.1782632 9.23486702,17.9433962 9.52459016,17.9433962 L12.4098361,17.9433962 Z M18.442623,17.9433962 C18.7323461,17.9433962 18.9672131,18.1782632 18.9672131,18.4679864 L18.9672131,18.4754098 C18.9672131,18.765133 18.7323461,19 18.442623,19 L15.557377,19 C15.2676539,19 15.0327869,18.765133 15.0327869,18.4754098 L15.0327869,18.4679864 C15.0327869,18.1782632 15.2676539,17.9433962 15.557377,17.9433962 L18.442623,17.9433962 Z M24.4754098,17.9433962 C24.765133,17.9433962 25,18.1782632 25,18.4679864 L25,18.4754098 C25,18.765133 24.765133,19 24.4754098,19 L21.5901639,19 C21.3004408,19 21.0655738,18.765133 21.0655738,18.4754098 L21.0655738,18.4679864 C21.0655738,18.1782632 21.3004408,17.9433962 21.5901639,17.9433962 L24.4754098,17.9433962 Z M12.4098361,16.0943396 C12.6995592,16.0943396 12.9344262,16.3292066 12.9344262,16.6189298 L12.9344262,16.6263532 C12.9344262,16.9160764 12.6995592,17.1509434 12.4098361,17.1509434 L9.52459016,17.1509434 C9.23486702,17.1509434 9,16.9160764 9,16.6263532 L9,16.6189298 C9,16.3292066 9.23486702,16.0943396 9.52459016,16.0943396 L12.4098361,16.0943396 Z M18.442623,16.0943396 C18.7323461,16.0943396 18.9672131,16.3292066 18.9672131,16.6189298 L18.9672131,16.6263532 C18.9672131,16.9160764 18.7323461,17.1509434 18.442623,17.1509434 L15.557377,17.1509434 C15.2676539,17.1509434 15.0327869,16.9160764 15.0327869,16.6263532 L15.0327869,16.6189298 C15.0327869,16.3292066 15.2676539,16.0943396 15.557377,16.0943396 L18.442623,16.0943396 Z M24.4754098,16.0943396 C24.765133,16.0943396 25,16.3292066 25,16.6189298 L25,16.6263532 C25,16.9160764 24.765133,17.1509434 24.4754098,17.1509434 L21.5901639,17.1509434 C21.3004408,17.1509434 21.0655738,16.9160764 21.0655738,16.6263532 L21.0655738,16.6189298 C21.0655738,16.3292066 21.3004408,16.0943396 21.5901639,16.0943396 L24.4754098,16.0943396 Z M12.4098361,14.245283 C12.6995592,14.245283 12.9344262,14.48015 12.9344262,14.7698732 L12.9344262,14.7772966 C12.9344262,15.0670198 12.6995592,15.3018868 12.4098361,15.3018868 L9.52459016,15.3018868 C9.23486702,15.3018868 9,15.0670198 9,14.7772966 L9,14.7698732 C9,14.48015 9.23486702,14.245283 9.52459016,14.245283 L12.4098361,14.245283 Z M18.442623,14.245283 C18.7323461,14.245283 18.9672131,14.48015 18.9672131,14.7698732 L18.9672131,14.7772966 C18.9672131,15.0670198 18.7323461,15.3018868 18.442623,15.3018868 L15.557377,15.3018868 C15.2676539,15.3018868 15.0327869,15.0670198 15.0327869,14.7772966 L15.0327869,14.7698732 C15.0327869,14.48015 15.2676539,14.245283 15.557377,14.245283 L18.442623,14.245283 Z M24.4754098,14.245283 C24.765133,14.245283 25,14.48015 25,14.7698732 L25,14.7772966 C25,15.0670198 24.765133,15.3018868 24.4754098,15.3018868 L21.5901639,15.3018868 C21.3004408,15.3018868 21.0655738,15.0670198 21.0655738,14.7772966 L21.0655738,14.7698732 C21.0655738,14.48015 21.3004408,14.245283 21.5901639,14.245283 L24.4754098,14.245283 Z M12.4098361,12.3962264 C12.6995592,12.3962264 12.9344262,12.6310934 12.9344262,12.9208166 L12.9344262,12.92824 C12.9344262,13.2179632 12.6995592,13.4528302 12.4098361,13.4528302 L9.52459016,13.4528302 C9.23486702,13.4528302 9,13.2179632 9,12.92824 L9,12.9208166 C9,12.6310934 9.23486702,12.3962264 9.52459016,12.3962264 L12.4098361,12.3962264 Z M18.442623,12.3962264 C18.7323461,12.3962264 18.9672131,12.6310934 18.9672131,12.9208166 L18.9672131,12.92824 C18.9672131,13.2179632 18.7323461,13.4528302 18.442623,13.4528302 L15.557377,13.4528302 C15.2676539,13.4528302 15.0327869,13.2179632 15.0327869,12.92824 L15.0327869,12.9208166 C15.0327869,12.6310934 15.2676539,12.3962264 15.557377,12.3962264 L18.442623,12.3962264 Z M24.4754098,12.3962264 C24.765133,12.3962264 25,12.6310934 25,12.9208166 L25,12.92824 C25,13.2179632 24.765133,13.4528302 24.4754098,13.4528302 L21.5901639,13.4528302 C21.3004408,13.4528302 21.0655738,13.2179632 21.0655738,12.92824 L21.0655738,12.9208166 C21.0655738,12.6310934 21.3004408,12.3962264 21.5901639,12.3962264 L24.4754098,12.3962264 Z M12.4098361,10.5471698 C12.6995592,10.5471698 12.9344262,10.7820368 12.9344262,11.07176 L12.9344262,11.0791834 C12.9344262,11.3689066 12.6995592,11.6037736 12.4098361,11.6037736 L9.52459016,11.6037736 C9.23486702,11.6037736 9,11.3689066 9,11.0791834 L9,11.07176 C9,10.7820368 9.23486702,10.5471698 9.52459016,10.5471698 L12.4098361,10.5471698 Z M18.442623,10.5471698 C18.7323461,10.5471698 18.9672131,10.7820368 18.9672131,11.07176 L18.9672131,11.0791834 C18.9672131,11.3689066 18.7323461,11.6037736 18.442623,11.6037736 L15.557377,11.6037736 C15.2676539,11.6037736 15.0327869,11.3689066 15.0327869,11.0791834 L15.0327869,11.07176 C15.0327869,10.7820368 15.2676539,10.5471698 15.557377,10.5471698 L18.442623,10.5471698 Z M12.4098361,8.69811321 C12.6995592,8.69811321 12.9344262,8.93298022 12.9344262,9.22270337 L12.9344262,9.23012682 C12.9344262,9.51984996 12.6995592,9.75471698 12.4098361,9.75471698 L9.52459016,9.75471698 C9.23486702,9.75471698 9,9.51984996 9,9.23012682 L9,9.22270337 C9,8.93298022 9.23486702,8.69811321 9.52459016,8.69811321 L12.4098361,8.69811321 Z M18.442623,8.69811321 C18.7323461,8.69811321 18.9672131,8.93298022 18.9672131,9.22270337 L18.9672131,9.23012682 C18.9672131,9.51984996 18.7323461,9.75471698 18.442623,9.75471698 L15.557377,9.75471698 C15.2676539,9.75471698 15.0327869,9.51984996 15.0327869,9.23012682 L15.0327869,9.22270337 C15.0327869,8.93298022 15.2676539,8.69811321 15.557377,8.69811321 L18.442623,8.69811321 Z M18.442623,6.8490566 C18.7323461,6.8490566 18.9672131,7.08392362 18.9672131,7.37364677 L18.9672131,7.38107021 C18.9672131,7.67079336 18.7323461,7.90566038 18.442623,7.90566038 L15.557377,7.90566038 C15.2676539,7.90566038 15.0327869,7.67079336 15.0327869,7.38107021 L15.0327869,7.37364677 C15.0327869,7.08392362 15.2676539,6.8490566 15.557377,6.8490566 L18.442623,6.8490566 Z M18.442623,5 C18.7323461,5 18.9672131,5.23486702 18.9672131,5.52459016 L18.9672131,5.53201361 C18.9672131,5.82173676 18.7323461,6.05660377 18.442623,6.05660377 L15.557377,6.05660377 C15.2676539,6.05660377 15.0327869,5.82173676 15.0327869,5.53201361 L15.0327869,5.52459016 C15.0327869,5.23486702 15.2676539,5 15.557377,5 L18.442623,5 Z" id="形状结合" fill="#F29F22"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#F8CF90"></path>\r
                <rect id="Rectangle-9" fill="#F29F22" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="WMA" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="8.1" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="7.91455078" y="30.05">WMA</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),an=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_wmv</title>\r
    <defs>\r
        <filter id="filter-1">\r
            <feColorMatrix in="SourceGraphic" type="matrix" values="0 0 0 0 0.949020 0 0 0 0 0.623529 0 0 0 0 0.133333 0 0 0 1.000000 0"></feColorMatrix>\r
        </filter>\r
    </defs>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-896.000000, -5384.000000)">\r
            <g id="icon_wmv" transform="translate(896.000000, 5384.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#FCECD3"></path>\r
                <g filter="url(#filter-1)" id="wmv">\r
                    <g transform="translate(11.000000, 5.000000)">\r
                        <path d="M11.003125,0.9984375 L11.003125,12.9671875 L1.0109375,12.9671875 L1.0109375,0.9984375 L11.003125,0.9984375 M11.003125,-0.0015625 L1.0109375,-0.0015625 C0.459375,-0.0015625 0.0109375,0.446875 0.0109375,0.9984375 L0.0109375,12.9671875 C0.0109375,13.51875 0.459375,13.9671875 1.0109375,13.9671875 L11.003125,13.9671875 C11.5546875,13.9671875 12.003125,13.51875 12.003125,12.9671875 L12.003125,0.9984375 C12.003125,0.4453125 11.5546875,-0.0015625 11.003125,-0.0015625 Z" id="形状" fill="#A39F8D" fill-rule="nonzero"></path>\r
                        <polygon id="路径" fill="#A39F8D" fill-rule="nonzero" points="2.484375 0.7921875 2.984375 0.7921875 2.984375 13.50625 2.484375 13.50625"></polygon>\r
                        <path d="M0.968671875,2.55879687 L0.968671875,2.05879687 L2.49835937,2.05879687 L2.49835937,2.55879687 L0.968671875,2.55879687 Z M0.968734375,4.111875 L0.968734375,3.611875 L2.49842187,3.611875 L2.49842187,4.111875 L0.968734375,4.111875 Z M0.96878125,5.66495312 L0.96878125,5.16495312 L2.49846875,5.16495312 L2.49846875,5.66495312 L0.96878125,5.66495312 Z M0.968828125,7.21801562 L0.968828125,6.71801562 L2.49851562,6.71801562 L2.49851562,7.21801562 L0.968828125,7.21801562 Z M0.968875,8.77109375 L0.968875,8.27109375 L2.4985625,8.27109375 L2.4985625,8.77109375 L0.968875,8.77109375 Z M0.968984375,11.87725 L0.968984375,11.37725 L2.49867187,11.37725 L2.49867187,11.87725 L0.968984375,11.87725 Z M0.968921875,10.3241719 L0.968921875,9.82417187 L2.49860937,9.82417187 L2.49860937,10.3241719 L0.968921875,10.3241719 Z M9.4690625,2.55917187 L9.4690625,2.05917187 L10.99875,2.05917187 L10.99875,2.55917187 L9.4690625,2.55917187 Z M9.46910937,4.11225 L9.46910937,3.61225 L10.9987969,3.61225 L10.9987969,4.11225 L9.46910937,4.11225 Z M9.46915625,5.6653125 L9.46915625,5.1653125 L10.9988437,5.1653125 L10.9988437,5.6653125 L9.46915625,5.6653125 Z M9.46921875,7.21840625 L9.46921875,6.71840625 L10.9989062,6.71840625 L10.9989062,7.21840625 L9.46921875,7.21840625 Z M9.46925,8.77146875 L9.46925,8.27146875 L10.9989375,8.27146875 L10.9989375,8.77146875 L9.46925,8.77146875 Z" id="形状" fill="#A39F8D" fill-rule="nonzero"></path>\r
                        <polygon id="路径" fill="#8C98A6" fill-rule="nonzero" points="9.45373438 11.877625 9.45373438 11.377625 10.9834219 11.377625 10.9834219 11.877625"></polygon>\r
                        <polygon id="路径" fill="#A39F8D" fill-rule="nonzero" points="9.4693125 10.3245469 9.4693125 9.82454688 10.999 9.82454688 10.999 10.3245469"></polygon>\r
                        <path d="M8.996875,0.6703125 L9.496875,0.6703125 L9.496875,13.384375 L8.996875,13.384375 L8.996875,0.6703125 Z M7.4023452,7.63424672 L5.25957674,9.46419978 C4.68105408,9.95594928 3.834375,9.51256859 3.834375,8.71717049 L3.834375,4.62463229 C3.834375,3.8292342 4.68353701,3.38316634 5.25957674,3.877603 L7.4023452,5.70755606 C7.97838493,6.1966184 7.97838493,7.14249722 7.4023452,7.63424672 L7.4023452,7.63424672 Z" id="形状" fill="#A39F8D" fill-rule="nonzero"></path>\r
                    </g>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#F8CF90"></path>\r
                <rect id="Rectangle-9" fill="#F29F22" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="WMV" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="8.1" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="8.04111328" y="30.05">WMV</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),dn=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_word</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-506.000000, -5214.000000)">\r
            <g id="icon_word" transform="translate(506.000000, 5214.000000)">\r
                <path d="M23.7267273,0 L23.7272727,6.21998174 C23.7272727,6.99128734 24.3323819,9.01596452 27.0828782,9.01596452 L32.7267273,8.985 L32.6798411,33.0899335 C32.6798411,34.4653386 31.5631693,35.5573497 30.1701307,35.639316 L30.0016932,35.6442572 L5.95087514,35.6442572 C4.48051945,35.6442572 3.27272727,34.5203548 3.27272727,33.0899335 L3.27272727,33.0899335 L3.27272727,2.55432373 C3.27272727,1.12390244 4.48051945,0 5.95087514,0 L5.95087514,0 L23.7267273,0 Z" id="Combined-Shape" fill="#DAEAF9"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#8CC2E6"></path>\r
                <path d="M34.3596082,21.15 C35.2655716,21.15 36,21.8839583 36,22.7911571 L36,30.9633884 C36,31.8697744 35.2645731,32.6045455 34.3596082,32.6045455 L1.64039183,32.6045455 C0.734428437,32.6045455 0,31.8705872 0,30.9633884 L0,22.7911571 C0,21.8847711 0.73542691,21.15 1.64039183,21.15 L34.3596082,21.15 Z M17.9762695,24.1529785 C17.5333008,24.1529785 17.137793,24.2221924 16.7897461,24.3606201 C16.4416992,24.4990479 16.1463867,24.6987793 15.9038086,24.9598145 C15.6612305,25.2208496 15.476001,25.5372559 15.3481201,25.9090332 C15.2202393,26.2808105 15.1562988,26.689502 15.1562988,27.1351074 C15.1562988,27.570166 15.2176025,27.9749023 15.34021,28.3493164 C15.4628174,28.7237305 15.6440918,29.0440918 15.8840332,29.3104004 C16.1239746,29.576709 16.4232422,29.7830322 16.7818359,29.9293701 C17.1404297,30.075708 17.5464844,30.148877 18,30.148877 C18.4561523,30.148877 18.8608887,30.0776855 19.214209,29.9353027 C19.5675293,29.7929199 19.8654785,29.5872559 20.1080566,29.3183105 C20.3506348,29.0493652 20.5325684,28.7316406 20.6538574,28.3651367 C20.7751465,27.9986328 20.835791,27.5912598 20.835791,27.1430176 C20.835791,26.5365723 20.7250488,26.0085693 20.5035645,25.5590088 C20.2820801,25.1094482 19.9557861,24.7627197 19.5246826,24.5188232 C19.0935791,24.2749268 18.5774414,24.1529785 17.9762695,24.1529785 Z M24.3755859,24.1529785 C23.990625,24.1529785 23.6280762,24.2215332 23.2879395,24.3586426 C22.9478027,24.495752 22.6511719,24.6954834 22.3980469,24.9578369 C22.1449219,25.2201904 21.9504639,25.5359375 21.8146729,25.9050781 C21.6788818,26.2742188 21.6109863,26.6921387 21.6109863,27.1588379 C21.6109863,27.4462402 21.6380127,27.7178223 21.6920654,27.973584 C21.7461182,28.2293457 21.8271973,28.4699463 21.9353027,28.6953857 C22.0434082,28.9208252 22.1739258,29.1245117 22.3268555,29.3064453 C22.4982422,29.5068359 22.6861084,29.668335 22.8904541,29.7909424 C23.0947998,29.9135498 23.3222168,30.0038574 23.5727051,30.0618652 C23.8231934,30.119873 24.1013672,30.148877 24.4072266,30.148877 C24.8106445,30.148877 25.1639648,30.0842773 25.4671875,29.9550781 C25.7704102,29.8258789 26.0195801,29.6577881 26.2146973,29.4508057 C26.4098145,29.2438232 26.5528564,29.0328857 26.6438232,28.8179932 C26.73479,28.6031006 26.7802734,28.4033691 26.7802734,28.2187988 C26.7802734,28.0737793 26.7301758,27.9524902 26.6299805,27.8549316 C26.5297852,27.757373 26.4098145,27.7085938 26.2700684,27.7085938 C26.1065918,27.7085938 25.9879395,27.7567139 25.9141113,27.8529541 C25.8402832,27.9491943 25.7783203,28.0737793 25.7282227,28.226709 C25.6095703,28.5510254 25.4335693,28.7968994 25.2002197,28.9643311 C24.9668701,29.1317627 24.6814453,29.2154785 24.3439453,29.2154785 C24.0328125,29.2154785 23.7612305,29.1416504 23.5291992,28.9939941 C23.297168,28.8463379 23.1178711,28.622876 22.9913086,28.3236084 C22.8647461,28.0243408 22.8014648,27.6492676 22.8014648,27.1983887 C22.8014648,26.5233887 22.9445068,26.0026367 23.2305908,25.6361328 C23.5166748,25.2696289 23.9009766,25.086377 24.3834961,25.086377 C24.6867188,25.086377 24.9418213,25.1575684 25.1488037,25.2999512 C25.3557861,25.442334 25.5357422,25.6572266 25.6886719,25.9446289 C25.780957,26.1186523 25.8646729,26.2412598 25.9398193,26.3124512 C26.0149658,26.3836426 26.1276855,26.4192383 26.2779785,26.4192383 C26.4124512,26.4192383 26.5271484,26.3678223 26.6220703,26.2649902 C26.7169922,26.1621582 26.7644531,26.0435059 26.7644531,25.9090332 C26.7644531,25.6638184 26.6668945,25.4034424 26.4717773,25.1279053 C26.2766602,24.8523682 25.9958496,24.6209961 25.6293457,24.4337891 C25.2628418,24.246582 24.8449219,24.1529785 24.3755859,24.1529785 Z M11.6876953,24.2518555 L10.1570801,24.2518555 C9.90395508,24.2518555 9.72070313,24.3085449 9.60732422,24.4219238 C9.5128418,24.5164063 9.45772705,24.6594116 9.44197998,24.8509399 L9.43725586,24.9716797 L9.43725586,29.2233887 C9.43725586,29.4105957 9.45373535,29.5628662 9.48669434,29.6802002 C9.51965332,29.7975342 9.59018555,29.888501 9.69829102,29.9531006 C9.78477539,30.0047803 9.90500977,30.0357881 10.0589941,30.046124 L10.1808105,30.05 L11.7114258,30.05 C11.9777344,30.05 12.2170166,30.0328613 12.4292725,29.998584 C12.6415283,29.9643066 12.8399414,29.9049805 13.0245117,29.8206055 C13.209082,29.7362305 13.3791504,29.6241699 13.5347168,29.4844238 C13.7324707,29.3024902 13.8946289,29.096167 14.0211914,28.8654541 C14.1477539,28.6347412 14.2413574,28.3756836 14.302002,28.0882813 C14.3626465,27.8008789 14.3929688,27.4831543 14.3929688,27.1351074 C14.3929688,26.0804199 14.0910645,25.2946777 13.4872559,24.7778809 C13.2552246,24.5748535 12.9968262,24.4364258 12.7120605,24.3625977 C12.484248,24.3035352 12.2201543,24.2680977 11.9197793,24.2562852 L11.6876953,24.2518555 Z M17.9762695,25.086377 C18.3058594,25.086377 18.597876,25.1661377 18.8523193,25.3256592 C19.1067627,25.4851807 19.3025391,25.7205078 19.4396484,26.0316406 C19.5767578,26.3427734 19.6453125,26.7105957 19.6453125,27.1351074 C19.6453125,27.5938965 19.5708252,27.9801758 19.4218506,28.2939453 C19.272876,28.6077148 19.0724854,28.8397461 18.8206787,28.9900391 C18.5688721,29.140332 18.2926758,29.2154785 17.9920898,29.2154785 C17.7574219,29.2154785 17.5418701,29.1719727 17.3454346,29.0849609 C17.148999,28.9979492 16.9749756,28.8667725 16.8233643,28.6914307 C16.6717529,28.5160889 16.5544189,28.2952637 16.4713623,28.0289551 C16.3883057,27.7626465 16.3467773,27.4646973 16.3467773,27.1351074 C16.3467773,26.8081543 16.3883057,26.513501 16.4713623,26.2511475 C16.5544189,25.9887939 16.6677979,25.7732422 16.811499,25.6044922 C16.9552002,25.4357422 17.1265869,25.3072021 17.3256592,25.2188721 C17.5247314,25.130542 17.7416016,25.086377 17.9762695,25.086377 Z M11.3871094,25.1812988 C11.7457031,25.1812988 12.0515625,25.2201904 12.3046875,25.2979736 C12.5578125,25.3757568 12.7707275,25.5570313 12.9434326,25.8417969 C13.1161377,26.1265625 13.2024902,26.5550293 13.2024902,27.1271973 C13.2024902,27.9393066 13.0060547,28.5114746 12.6131836,28.8437012 C12.5261719,28.920166 12.4233398,28.9794922 12.3046875,29.0216797 C12.1860352,29.0638672 12.0713379,29.0902344 11.9605957,29.1007813 C11.8498535,29.1113281 11.6969238,29.1166016 11.5018066,29.1166016 L10.6119141,29.1166016 L10.6119141,25.1812988 L11.3871094,25.1812988 Z" id="形状结合" fill="#1A85CD"></path>\r
                <rect id="Rectangle-10-Copy" fill="#1A85CD" x="9.16399617" y="17.55" width="16.8321969" height="1.63636364" rx="0.818181818"></rect>\r
                <rect id="Rectangle-10" fill="#1A85CD" x="18" y="13.5" width="7.83219685" height="1.63636364" rx="0.818181818"></rect>\r
                <rect id="Rectangle-10-Copy-4" fill="#1A85CD" x="19.8" y="9.45" width="6.03219685" height="1.63636364" rx="0.818181818"></rect>\r
                <text id="W" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="10.8" font-weight="normal" fill="#1A85CD">\r
                    <tspan x="8.20986328" y="15.4">W</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),cn=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_xml</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-636.000000, -5331.000000)">\r
            <g id="icon_xml" transform="translate(636.000000, 5331.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#E6EBEF"></path>\r
                <g id="file-xml" transform="translate(12.000000, 4.000000)" fill="#8199AE">\r
                    <path d="M6.66666667,5 L10.3333333,5 L6.66666667,1.33333333 L6.66666667,5 M2,0.333333328 L7.33333333,0.333333328 L11.3333333,4.33333333 L11.3333333,12.3333333 C11.3333333,13.0666667 10.7333333,13.6666667 10,13.6666667 L2,13.6666667 C1.26,13.6666667 0.666666672,13.0666667 0.666666672,12.3333333 L0.666666672,1.66666667 C0.666666672,0.926666672 1.26,0.333333328 2,0.333333328 M2.08,9.33333333 L4.57333333,11.8266667 L5.52,10.8866667 L3.96666667,9.33333333 L5.52,7.78 L4.57333333,6.84 L2.08,9.33333333 M9.52,9.33333333 L7.02666667,6.84 L6.08,7.78 L7.63333333,9.33333333 L6.08,10.8866667 L7.02666667,11.8266667 L9.52,9.33333333 Z" id="形状"></path>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C2CDD7"></path>\r
                <rect id="Rectangle-9" fill="#8199AE" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="XML" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="8.1" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="9.73388672" y="30.05">XML</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),hn=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_xsl</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-701.000000, -5331.000000)">\r
            <g id="icon_xsl" transform="translate(701.000000, 5331.000000)">\r
                <path d="M23.454,0 L23.4545455,6.21998174 C23.4545455,6.99128734 24.0596546,9.01596452 26.810151,9.01596452 L32.454,8.985 L32.4071138,33.0899335 C32.4071138,34.4653386 31.2904421,35.5573497 29.8974034,35.639316 L29.728966,35.6442572 L5.67814787,35.6442572 C4.20779218,35.6442572 3,34.5203548 3,33.0899335 L3,33.0899335 L3,2.55432373 C3,1.12390244 4.20779218,0 5.67814787,0 L5.67814787,0 L23.454,0 Z" id="Combined-Shape" fill="#E4F2D6"></path>\r
                <g id="XSL图标" transform="translate(11.000000, 4.000000)" fill-rule="nonzero">\r
                    <rect id="矩形" fill="#000000" opacity="0" x="0" y="0" width="14" height="14"></rect>\r
                    <path d="M2.625,5.25 L11.375,5.25 C11.5297096,5.25 11.6780827,5.31145816 11.787479,5.42085438 C11.8968752,5.5302506 11.9583333,5.67862372 11.9583333,5.83333334 L11.9583333,10.7916667 C11.9583333,11.1138328 11.6971661,11.375 11.375,11.375 L2.625,11.375 L2.625,12.8333333 C2.625,12.9106881 2.65572908,12.9848747 2.71042719,13.0395728 C2.7651253,13.0942709 2.83931185,13.125 2.91666666,13.125 L12.8333333,13.125 C12.9944164,13.125 13.125,12.9944164 13.125,12.8333333 L13.125,4.22216666 L12.9870417,4.08333334 L11.2571667,4.08333334 C10.516829,4.08333334 9.91666666,3.48317105 9.91666666,2.74283334 L9.91666666,0.993416662 L9.79883334,0.875 L2.91666666,0.875 C2.83931185,0.875 2.7651253,0.905729078 2.71042719,0.960427187 C2.65572908,1.0151253 2.625,1.08931185 2.625,1.16666666 L2.625,5.25 Z M1.75,11.375 L0.583333338,11.375 C0.261167231,11.375 0,11.1138328 0,10.7916667 L0,5.83333334 C0,5.67862372 0.0614581574,5.5302506 0.170854379,5.42085438 C0.280250601,5.31145816 0.428623718,5.25 0.583333338,5.25 L1.75,5.25 L1.75,1.16666666 C1.75,0.522334456 2.27233446,0 2.91666666,0 L10.163125,0 L14,3.86166666 L14,12.8333333 C14,13.4776655 13.4776655,14 12.8333333,14 L2.91666666,14 C2.27233446,14 1.75,13.4776655 1.75,12.8333333 L1.75,11.375 L1.75,11.375 Z M1.08354166,6.45983334 L2.33829166,8.27108334 L0.999541662,10.2083333 L1.75029166,10.2083333 L2.71629166,8.75933334 L3.68229166,10.2083333 L4.43304166,10.2083333 L3.08379166,8.27108334 L4.34904166,6.45983334 L3.59829166,6.45983334 L2.71629166,7.78283334 L1.83429166,6.45983334 L1.08354166,6.45983334 Z M6.14979166,6.38633334 C5.74554166,6.38633334 5.41479166,6.47558334 5.15229166,6.65933334 C4.86879166,6.84833334 4.73229166,7.11083334 4.73229166,7.44158334 C4.73229166,7.77233334 4.87404166,8.02433334 5.16279166,8.19758334 C5.27304166,8.26058334 5.56179166,8.36558334 6.02379166,8.51258334 C6.43854166,8.63858334 6.68004166,8.72258334 6.75354166,8.75933334 C6.98454166,8.87483334 7.10529166,9.03233334 7.10529166,9.23183334 C7.10529166,9.38933334 7.02129166,9.51008334 6.86379166,9.60458334 C6.70629166,9.69383334 6.48579166,9.74108334 6.21279166,9.74108334 C5.90304166,9.74108334 5.67729166,9.68333334 5.53029166,9.57833334 C5.36754166,9.45758334 5.26779166,9.25283334 5.22579166,8.96933334 L4.61679166,8.96933334 C4.64304166,9.44708334 4.81629166,9.79358334 5.13129166,10.0140833 C5.38854166,10.1925833 5.75079166,10.2818333 6.21279166,10.2818333 C6.69054166,10.2818333 7.06329166,10.1820833 7.32579166,9.99308334 C7.58829166,9.79883334 7.71954166,9.53108334 7.71954166,9.19508334 C7.71954166,8.84858334 7.55679166,8.58083334 7.23129166,8.38658334 C7.08429166,8.30258334 6.75879166,8.17658334 6.24954166,8.01908334 C5.90304166,7.90883334 5.68779166,7.83008334 5.60904166,7.78808334 C5.43054166,7.69358334 5.34654166,7.56233334 5.34654166,7.40483334 C5.34654166,7.22633334 5.42004166,7.09508334 5.57754166,7.01633334 C5.70354166,6.94808334 5.88204166,6.91658334 6.11829166,6.91658334 C6.39129166,6.91658334 6.60129166,6.96383334 6.73779166,7.06883334 C6.87429166,7.16858334 6.97404166,7.33658334 7.02654166,7.56758334 L7.63554166,7.56758334 C7.59879166,7.15808334 7.44654166,6.85358334 7.18404166,6.65933334 C6.93729166,6.47558334 6.59079166,6.38633334 6.14979166,6.38633334 L6.14979166,6.38633334 Z M8.24454166,6.45983334 L8.24454166,10.2083333 L10.8800417,10.2083333 L10.8800417,9.68333334 L8.85354166,9.68333334 L8.85354166,6.45983334 L8.24454166,6.45983334 L8.24454166,6.45983334 Z" id="形状" fill="#8BC755"></path>\r
                </g>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#BBDD98"></path>\r
                <rect id="Rectangle-9" fill="#77BC31" x="0" y="21.15" width="36" height="11.4545455" rx="1.63636364"></rect>\r
                <text id="XSL" font-family="ArialRoundedMTBold, Arial Rounded MT Bold" font-size="8.1" font-weight="normal" fill="#FFFFFF">\r
                    <tspan x="10.40625" y="30.05">XSL</tspan>\r
                </text>\r
            </g>\r
        </g>\r
    </g>\r
</svg>`},Symbol.toStringTag,{value:"Module"})),pn=Object.freeze(Object.defineProperty({__proto__:null,default:`<?xml version="1.0" encoding="UTF-8"?>\r
<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
    <title>icon_zip</title>\r
    <g id="页面-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">\r
        <g id="1-O2OAWeb系统视觉设计规范" transform="translate(-442.000000, -5214.000000)">\r
            <g id="icon_zip" transform="translate(442.000000, 5214.000000)">\r
                <path d="M23.604,0 L23.6045455,6.21998174 C23.6045455,6.99128734 24.2096546,9.01596452 26.960151,9.01596452 L32.604,8.985 L32.5571138,33.0899335 C32.5571138,34.4653386 31.4404421,35.5573497 30.0474034,35.639316 L29.878966,35.6442572 L5.82814787,35.6442572 C4.35779218,35.6442572 3.15,34.5203548 3.15,33.0899335 L3.15,33.0899335 L3.15,2.55432373 C3.15,1.12390244 4.35779218,0 5.82814787,0 L5.82814787,0 L23.604,0 Z" id="Combined-Shape" fill="#E6EBEF"></path>\r
                <path d="M16.611785,15.6850881 L19.9102528,15.6850881 L19.9102528,14.1325733 L16.611785,14.1325733 L16.611785,15.6850881 Z M20.7,1.55696639 L18.2273401,1.55696639 L18.2273401,3.10948127 L20.7,3.10948127 L20.7,4.66203554 L18.2273401,4.66203554 L18.2273401,6.21455041 L20.7,6.21455041 L20.7,7.76710468 L18.2273401,7.76710468 L18.2273401,9.31961956 L20.7,9.31961956 L20.7,16.3215365 C20.7,16.7505758 20.3293434,17.1 19.8742339,17.1 L16.5757661,17.1 C16.1206566,17.1 15.75,16.7505758 15.75,16.3215365 L15.75,10.8810375 L18.2226599,10.8810375 L18.2226599,9.3284832 L15.7546802,9.3284832 L15.7546802,7.77592893 L18.2273401,7.77592893 L18.2273401,6.21900193 L15.7546802,6.21900193 L15.7546802,4.66644766 L18.2273401,4.66644766 L18.2273401,3.10948127 L15.7546802,3.10948127 L15.7546802,1.55696639 L18.2273401,1.55696639 L18.2273401,0 L20.7,0 L20.7,1.55696639 Z" id="Fill-2" fill="#8199AE"></path>\r
                <path d="M24.5454545,5.88754402 C24.5454545,6.52925525 25.021645,8.21374723 27.1861472,8.21374723 L32.7272727,8.18181818 L24.5454545,0.0319290466 L24.5454545,5.88754402 Z" id="Fill-3" fill="#C2CDD7"></path>\r
                <path d="M34.3596082,21.1704545 C35.2655716,21.1704545 36,21.9044128 36,22.8116116 L36,30.9838429 C36,31.8902289 35.2645731,32.625 34.3596082,32.625 L1.64039183,32.625 C0.734428437,32.625 0,31.8910417 0,30.9838429 L0,22.8116116 C0,21.9052256 0.73542691,21.1704545 1.64039183,21.1704545 L34.3596082,21.1704545 Z M18.5090332,24.1529785 C18.3376465,24.1529785 18.1979004,24.2123047 18.0897949,24.330957 C18.0033105,24.4258789 17.9514199,24.5587695 17.934123,24.7296289 L17.9276367,24.8648926 L17.9276367,29.4330078 C17.9276367,29.6703125 17.9823486,29.8489502 18.0917725,29.9689209 C18.2011963,30.0888916 18.3402832,30.148877 18.5090332,30.148877 C18.6856934,30.148877 18.8287354,30.0895508 18.9381592,29.9708984 C19.0256982,29.8759766 19.0782217,29.7422422 19.0957295,29.5696953 L19.1022949,29.4330078 L19.1022949,24.8648926 C19.1022949,24.6249512 19.047583,24.4463135 18.9381592,24.3289795 C18.8287354,24.2116455 18.6856934,24.1529785 18.5090332,24.1529785 Z M22.6658203,24.2518555 L21.1589355,24.2518555 C20.9005371,24.2518555 20.7120117,24.3072266 20.5933594,24.4179688 C20.4944824,24.5102539 20.4368042,24.6538086 20.4203247,24.8486328 L20.4153809,24.9716797 L20.4153809,29.440918 C20.4153809,29.6729492 20.4694336,29.8489502 20.5775391,29.9689209 C20.6856445,30.0888916 20.8280273,30.148877 21.0046875,30.148877 C21.1734375,30.148877 21.3131836,30.0882324 21.4239258,29.9669434 C21.5125195,29.8699121 21.5656758,29.7365996 21.5833945,29.5670059 L21.5900391,29.4330078 L21.5900391,27.7916504 L22.6658203,27.7916504 C23.3856445,27.7916504 23.9274902,27.6393799 24.2913574,27.3348389 C24.6552246,27.0302979 24.8371582,26.5827148 24.8371582,25.9920898 C24.8371582,25.7152344 24.792334,25.4660645 24.7026855,25.2445801 C24.6130371,25.0230957 24.4818604,24.8358887 24.3091553,24.682959 C24.1364502,24.5300293 23.9314453,24.4206055 23.6941406,24.3546875 C23.4941895,24.2975586 23.2228271,24.2642334 22.8800537,24.2547119 L22.6658203,24.2518555 Z M16.2111328,24.2518555 L12.9125977,24.2518555 C12.7333008,24.2518555 12.5981689,24.2933838 12.5072021,24.3764404 C12.4162354,24.4594971 12.370752,24.5682617 12.370752,24.7027344 C12.370752,24.8319336 12.4162354,24.936084 12.5072021,25.0151855 C12.5799756,25.0784668 12.6810146,25.1164355 12.8103193,25.1290918 L12.9125977,25.1338379 L15.4715332,25.1338379 L12.4933594,28.6973633 C12.4458984,28.7527344 12.3905273,28.8179932 12.3272461,28.8931396 C12.2639648,28.9682861 12.2145264,29.030249 12.1789307,29.0790283 C12.143335,29.1278076 12.1149902,29.1798828 12.0938965,29.2352539 C12.0728027,29.290625 12.0622559,29.3512695 12.0622559,29.4171875 C12.0622559,29.609668 12.1268555,29.7632568 12.2560547,29.8779541 C12.3594141,29.9697119 12.5032734,30.0247666 12.6876328,30.0431182 L12.8334961,30.05 L16.5987305,30.05 C16.7806641,30.05 16.9164551,30.0091309 17.0061035,29.9273926 C17.095752,29.8456543 17.1405762,29.7349121 17.1405762,29.595166 C17.1405762,29.4606934 17.095752,29.3558838 17.0061035,29.2807373 C16.9343848,29.2206201 16.8331348,29.1845498 16.7023535,29.1725264 L16.5987305,29.1680176 L13.5295898,29.1680176 L16.3139648,25.8536621 C16.5301758,25.6005371 16.673877,25.4172852 16.7450684,25.3039063 C16.8162598,25.1905273 16.8518555,25.038916 16.8518555,24.8490723 C16.8518555,24.4871227 16.6753478,24.2896957 16.3223326,24.2567911 L16.2111328,24.2518555 Z M22.3810547,25.1338379 C22.9057617,25.1338379 23.2524902,25.2195313 23.4212402,25.390918 C23.5715332,25.5517578 23.6466797,25.7600586 23.6466797,26.0158203 C23.6466797,26.2293945 23.5979004,26.4020996 23.5003418,26.5339355 C23.4027832,26.6657715 23.2610596,26.7606934 23.0751709,26.8187012 C22.8892822,26.876709 22.6579102,26.9057129 22.3810547,26.9057129 L21.5900391,26.9057129 L21.5900391,25.1338379 L22.3810547,25.1338379 Z" id="形状结合" fill="#8199AE"></path>\r
            </g>\r
        </g>\r
    </g>\r
</svg>\r
`},Symbol.toStringTag,{value:"Module"})),un=Object.freeze(Object.defineProperty({__proto__:null,default:`.ootooltip{font-size:.875rem;padding:.3rem;border-radius:.3rem;position:absolute;background:var(--oo-color-main-deep);color:var(--oo-color-text-white);opacity:0;z-index:20000}.ootooltip.show{opacity:0;transition:opacity .6s,top .6s,left .6s}.ootooltip_triangle{width:.5rem;height:.5rem;position:absolute;border-bottom-right-radius:.15rem;background:var(--oo-color-main-deep)}.ootooltip_triangle.top{transform:rotate(45deg)}.ootooltip_triangle.left{transform:rotate(-45deg)}.ootooltip_triangle.right{transform:rotate(135deg)}.ootooltip_triangle.bottom{transform:rotate(-135deg)}.ootooltip_container{display:contents}.hide{display:none}
`},Symbol.toStringTag,{value:"Module"})),Cn=Object.freeze(Object.defineProperty({__proto__:null,default:`.ootooltip{font-size:.875rem;border-radius:var(--oo-area-radius);position:absolute;background:var(--oo-color-bg-white);color:var(--oo-color-text2);border:1px solid var(--oo-color-gray-e1);box-shadow:0 0 .625rem var(--oo-color-gray-b);opacity:0;z-index:20000}.ootooltip *{box-sizing:border-box}.ootooltip.show{opacity:0;transition:opacity .6s,top .6s,left .6s}.ootooltip_triangle{width:.7rem;height:.7rem;position:absolute;border-bottom-right-radius:.15rem;background:var(--oo-color-bg-white);box-shadow:0 0 .625rem 0 var(--oo-color-gray-9);border-right:1px solid var(--oo-color-gray-e1);border-bottom:1px solid var(--oo-color-gray-e1)}.ootooltip_triangle.top{transform:rotate(45deg)}.ootooltip_triangle.left{transform:rotate(-45deg)}.ootooltip_triangle.right{transform:rotate(135deg)}.ootooltip_triangle.bottom{transform:rotate(-135deg)}.ootooltip_container{display:contents}.hide{display:none}.ootooltip_content{background:var(--oo-color-bg-white);position:relative;border-radius:var(--oo-area-radius);overflow:hidden;padding:.3rem}
`},Symbol.toStringTag,{value:"Module"})),mn=Object.freeze(Object.defineProperty({__proto__:null,default:`.ootooltip{font-size:.725rem;padding:0 .3rem;border-radius:.2rem;position:absolute;background:var(--oo-color-bg-white);color:var(--oo-color-text2);border:1px solid var(--oo-color-gray-9);box-shadow:0 0 .625rem var(--oo-color-gray-9);opacity:0;z-index:20000}.ootooltip *{box-sizing:border-box}.ootooltip.show{opacity:0;transition:opacity .6s,top .6s,left .6s}.ootooltip_triangle{width:.4rem;height:.4rem;position:absolute;background:var(--oo-color-bg-white);border-right:1px solid var(--oo-color-gray-9);border-bottom:1px solid var(--oo-color-gray-9)}.ootooltip_triangle.top{transform:rotate(45deg)}.ootooltip_triangle.left{transform:rotate(-45deg)}.ootooltip_triangle.right{transform:rotate(135deg)}.ootooltip_triangle.bottom{transform:rotate(-135deg)}.ootooltip_container{display:contents}.hide{display:none}.ootooltip_content{background:var(--oo-color-bg-white);position:relative;border-radius:.2rem;overflow:hidden;padding:.3rem}
`},Symbol.toStringTag,{value:"Module"})),E9=Object.freeze(Object.defineProperty({__proto__:null,default:`* {\r
    box-sizing: border-box;\r
}\r
.container{\r
    /*position: absolute;*/\r
    /*min-width: 300px;*/\r
    /*min-height: 70px;*/\r
    padding: 0.357em;\r
    background: var(--oo-color-info-bg);\r
    border-radius: var(--oo-area-radius);\r
    border-width: 1px;\r
    border-style: solid;\r
    border-color: var(--oo-color-info-border);\r
    transition: all 0.5s, width 0s;\r
    display: flex;\r
    justify-content: space-between;\r
    align-items: flex-start;\r
    font-size: 0.857em;\r
}\r
.content{\r
    display: flex;\r
    width: inherit;\r
    justify-content: flex-start;\r
    align-items: flex-start;\r
}\r
.align_left{\r
    justify-content: flex-start;\r
}\r
.align_right{\r
    justify-content: flex-end;\r
}\r
.align_center{\r
     justify-content: center;\r
}\r
\r
.valign_top{\r
    align-items: flex-start;\r
}\r
.valign_bottom{\r
    align-items: flex-end;\r
}\r
.valign_center{\r
    align-items: center;\r
}\r
\r
.icon {\r
    color: var(--oo-color-info);\r
    font-size: var(--oo-font-size-largest);\r
    width: 1.2em;\r
    text-align: center;\r
    padding: 0.2em;\r
}\r
.message {\r
    padding: 0.357em;\r
}\r
.title {\r
    font-weight: bold;\r
    color: var(--oo-color-text);\r
    padding: 0 0.357em 0.357em 0.357em;\r
}\r
.text{\r
    color: var(--oo-color-text2);\r
    padding: 0 0.357em;\r
}\r
.close{\r
    cursor: pointer;\r
}\r
.hide{\r
    display: none;\r
}\r
\r
.error.container{\r
    background: var(--oo-color-error-bg);\r
    border-color: var(--oo-color-error-border);\r
}\r
.error .icon{\r
    color: var(--oo-color-error);\r
}\r
\r
.warn.container{\r
    background: var(--oo-color-warn-bg);\r
    border-color: var(--oo-color-warn-border);\r
}\r
.warn .icon{\r
    color: var(--oo-color-warn);\r
}\r
\r
.success.container{\r
    background: var(--oo-color-success-bg);\r
    border-color: var(--oo-color-success-border);\r
}\r
.success .icon{\r
    color: var(--oo-color-success);\r
}\r
`},Symbol.toStringTag,{value:"Module"})),gn=Object.freeze(Object.defineProperty({__proto__:null,default:`* {\r
    box-sizing: border-box;\r
}\r
.container{\r
    /*position: fixed;*/\r
    width: 100%;\r
    top: 0;\r
    left: 0;\r
    padding: 0.714em;\r
    min-height: 5em;\r
    transition: all 0.5s;\r
    display: flex;\r
    justify-content: space-between;\r
    align-items: flex-start;\r
}\r
.content{\r
    display: flex;\r
    width: inherit;\r
    justify-content: center;\r
    align-items: center;\r
}\r
.align_left{\r
    justify-content: flex-start;\r
}\r
.align_right{\r
    justify-content: flex-end;\r
}\r
.align_center{\r
    justify-content: center;\r
}\r
\r
.valign_top{\r
    align-items: flex-start;\r
}\r
.valign_bottom{\r
    align-items: flex-end;\r
}\r
.valign_center{\r
    align-items: center;\r
}\r
.icon {\r
    color: var(--oo-color-text-white);\r
    font-size: calc(var(--oo-font-size-largest)*2);\r
    text-align: center;\r
    padding: 0.1em;\r
}\r
.message {\r
    padding: 0.357em;\r
}\r
.title {\r
    font-weight: bold;\r
    color: var(--oo-color-text-white);\r
    padding: 0 0.357em 0.357em 0.357em;\r
}\r
.text{\r
    color: var(--oo-color-text-white);\r
    padding: 0 0.357em;\r
}\r
.close{\r
    cursor: pointer;\r
    color: var(--oo-color-text-white);\r
}\r
\r
.info.container{\r
    background: var(--oo-color-info);\r
}\r
.error.container{\r
    background: var(--oo-color-error);\r
}\r
.warn.container{\r
    background: var(--oo-color-warn);\r
}\r
.success.container{\r
    background: var(--oo-color-success);\r
}\r
`},Symbol.toStringTag,{value:"Module"})),fn=Object.freeze(Object.defineProperty({__proto__:null,default:`.maskNode{opacity:0;background-color:var(--oo-color-gray-d);transition:.2s;position:absolute}.maskNode.show{opacity:.3}
`},Symbol.toStringTag,{value:"Module"}));return L.DatetimePicker=i9,L.Dialog=y9,L.Dragdrop=_6,L.Draggable=k9,L.Menu=s9,L.Notice=B1,L.Tooltip=R1,L.confirm=f6,L.datetimePicker=r9,L.defineComponent=w6,L.dialog=x9,L.mask=b6,L.notice=V1,L.tooltip=C9,L.unmask=L6,Object.defineProperty(L,Symbol.toStringTag,{value:"Module"}),L}({});
//# sourceMappingURL=ooui.iife.js.map
