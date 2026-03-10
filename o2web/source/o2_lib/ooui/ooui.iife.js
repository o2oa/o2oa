var V8=Object.defineProperty;var H8=(_,T,z)=>T in _?V8(_,T,{enumerable:!0,configurable:!0,writable:!0,value:z}):_[T]=z;var m=(_,T,z)=>(H8(_,typeof T!="symbol"?T+"":T,z),z),a3=(_,T,z)=>{if(!T.has(_))throw TypeError("Cannot "+z)};var l=(_,T,z)=>(a3(_,T,"read from private field"),z?z.call(_):T.get(_)),d=(_,T,z)=>{if(T.has(_))throw TypeError("Cannot add the same private member more than once");T instanceof WeakSet?T.add(_):T.set(_,z)},u=(_,T,z,cs)=>(a3(_,T,"write to private field"),cs?cs.call(_,z):T.set(_,z),z);var c=(_,T,z)=>(a3(_,T,"access private method"),z);var $OOUI=function(_){var Rt,Y2,d3,Ps,N4,Is,O4,xt,Ns,F4,W2,h3,Dt,_e,mt,Pt,It,q2,U2,X2,G2,K2,J2,Q2,le,Le,xe,Vt,Ht,Bt,Yt,h2,t1,e1,c2,p2,u2,s1,m2,ye,ae,we,ke,Ee,Xi,Fs,Gi,B8,Me,es,Wt,t2,st,bt,$s,$4,js,j4,zs,z4,Zs,Z4,Rs,R4,Vs,V4,de,he,ce,Ys,H4,Ws,B4,qs,Y4,Us,W4,Se,Ki,Ji,Xs,q4,Gs,U4,Ks,X4,Qi,G,C2,B,ht,i1,r1,o1,l1,a1,d1,g2,f2,v2,Ae,ss,Te,ns,Js,G4,h1,c3,b2,tn,K4,pe,sn,J4,_2,Ie,is,nn,Q4,rn,tr,L2,q5,c1,p3,on,er,ln,sr,dn,nr,hn,ir,cn,rr,pn,or,un,lr,mn,ar,yt,Y,x2,L,Ct,Ne,K,Nt,Oe,Fe,y2,Cn,dr,u1,u3,gn,hr,$e,rs,fn,cr,vn,pr,W,bn,ur,e3,_n,mr,je,os,m1,m3,C1,C3,g1,g3,Ln,Cr,v1,f3,b1,v3,_1,b3,xn,gr,qt,yn,fr,wn,vr,En,br,L1,_3,Mn,_r,Sn,Lr,x1,L3,An,xr,w2,U5,Tn,yr,Dn,wr,Pn,In,Nn,kr,y1,ze,ls,On,Er,Ot,Fn,Mr,$n,Sr,jn,Ar,zn,Tr,Rn,Dr,Q,Vn,Pr,Hn,Ir,s3,E2,Ut,M2,S2,Ze,k1,x3,Yn,Nr,Re,E1,y3,qn,Or,Un,Fr,Xn,$r,Gn,jr,Kn,zr,M1,w3,S1,k3,Jn,Zr,Qn,Rr,A1,E3,t5,Vr,T1,M3,e5,Hr,ue,z2,s5,Br,Gt,e2,n5,Yr,D1,S3,P1,A3,Kt,s2,A2,X5,Jt,n2,r5,Wr,I1,T3,T2,G5,l5,qr,a5,Ur,d5,Xr,nt,D2,wt,q,P2,I2,D,tt,it,me,rt,F,N1,U,kt,Ve,O1,F1,He,Ft,Qt,$1,D3,A,j1,P3,Be,as,N2,K5,z1,I3,Z1,N3,R1,O3,O2,J5,Ye,ds,F2,Q5,h5,Gr,V1,F3,qe,hs,c5,Kr,p5,Jr,H1,$3,C5,Qr,f5,t0,v5,e0,_5,s0,x5,n0,y5,i0,$2,Ue,Ce,Xe,te,ft,ee,w5,r0,Y1,j3,k5,o0,W1,z3,M5,l0,S5,a0,A5,d0,Et,T5,h0,D5,c0,P5,p0,I5,u0,N5,m0,q1,O5,C0,F5,g0,Z,U1,$5,f0,j5,v0,X1,Z3,j2,G1,R3,K1,V3,r3,Y8,z5,b0,Z5,_0,R5,L0,Ge,Ke,se,Je,fe,V5,vt,H5,x0,B5,y0,Y5,w0;"use strict";var T=globalThis&&globalThis.__spreadArray||function(o,i,t){if(t||arguments.length===2)for(var e=0,s=i.length,r;e<s;e++)(r||!(e in i))&&(r||(r=Array.prototype.slice.call(i,0,e)),r[e]=i[e]);return o.concat(r||Array.prototype.slice.call(i))},z=function(){function o(i,t,e){this.name=i,this.version=t,this.os=e,this.type="browser"}return o}(),cs=function(){function o(i){this.version=i,this.type="node",this.name="node",this.os=process.platform}return o}(),k0=function(){function o(i,t,e,s){this.name=i,this.version=t,this.os=e,this.bot=s,this.type="bot-device"}return o}(),E0=function(){function o(){this.type="bot",this.bot=!0,this.name="bot",this.version=null,this.os=null}return o}(),M0=function(){function o(){this.type="react-native",this.name="react-native",this.version=null,this.os=null}return o}(),S0=/alexa|bot|crawl(er|ing)|facebookexternalhit|feedburner|google web preview|nagios|postrank|pingdom|slurp|spider|yahoo!|yandex/,A0=/(nuhk|curl|Googlebot|Yammybot|Openbot|Slurp|MSNBot|Ask\ Jeeves\/Teoma|ia_archiver)/,H3=3,T0=[["aol",/AOLShield\/([0-9\._]+)/],["edge",/Edge\/([0-9\._]+)/],["edge-ios",/EdgiOS\/([0-9\._]+)/],["yandexbrowser",/YaBrowser\/([0-9\._]+)/],["kakaotalk",/KAKAOTALK\s([0-9\.]+)/],["samsung",/SamsungBrowser\/([0-9\.]+)/],["silk",/\bSilk\/([0-9._-]+)\b/],["miui",/MiuiBrowser\/([0-9\.]+)$/],["beaker",/BeakerBrowser\/([0-9\.]+)/],["edge-chromium",/EdgA?\/([0-9\.]+)/],["chromium-webview",/(?!Chrom.*OPR)wv\).*Chrom(?:e|ium)\/([0-9\.]+)(:?\s|$)/],["chrome",/(?!Chrom.*OPR)Chrom(?:e|ium)\/([0-9\.]+)(:?\s|$)/],["phantomjs",/PhantomJS\/([0-9\.]+)(:?\s|$)/],["crios",/CriOS\/([0-9\.]+)(:?\s|$)/],["firefox",/Firefox\/([0-9\.]+)(?:\s|$)/],["fxios",/FxiOS\/([0-9\.]+)/],["opera-mini",/Opera Mini.*Version\/([0-9\.]+)/],["opera",/Opera\/([0-9\.]+)(?:\s|$)/],["opera",/OPR\/([0-9\.]+)(:?\s|$)/],["pie",/^Microsoft Pocket Internet Explorer\/(\d+\.\d+)$/],["pie",/^Mozilla\/\d\.\d+\s\(compatible;\s(?:MSP?IE|MSInternet Explorer) (\d+\.\d+);.*Windows CE.*\)$/],["netfront",/^Mozilla\/\d\.\d+.*NetFront\/(\d.\d)/],["ie",/Trident\/7\.0.*rv\:([0-9\.]+).*\).*Gecko$/],["ie",/MSIE\s([0-9\.]+);.*Trident\/[4-7].0/],["ie",/MSIE\s(7\.0)/],["bb10",/BB10;\sTouch.*Version\/([0-9\.]+)/],["android",/Android\s([0-9\.]+)/],["ios",/Version\/([0-9\._]+).*Mobile.*Safari.*/],["safari",/Version\/([0-9\._]+).*Safari/],["facebook",/FB[AS]V\/([0-9\.]+)/],["instagram",/Instagram\s([0-9\.]+)/],["ios-webview",/AppleWebKit\/([0-9\.]+).*Mobile/],["ios-webview",/AppleWebKit\/([0-9\.]+).*Gecko\)$/],["curl",/^curl\/([0-9\.]+)$/],["searchbot",S0]],B3=[["iOS",/iP(hone|od|ad)/],["Android OS",/Android/],["BlackBerry OS",/BlackBerry|BB10/],["Windows Mobile",/IEMobile/],["Amazon OS",/Kindle/],["Windows 3.11",/Win16/],["Windows 95",/(Windows 95)|(Win95)|(Windows_95)/],["Windows 98",/(Windows 98)|(Win98)/],["Windows 2000",/(Windows NT 5.0)|(Windows 2000)/],["Windows XP",/(Windows NT 5.1)|(Windows XP)/],["Windows Server 2003",/(Windows NT 5.2)/],["Windows Vista",/(Windows NT 6.0)/],["Windows 7",/(Windows NT 6.1)/],["Windows 8",/(Windows NT 6.2)/],["Windows 8.1",/(Windows NT 6.3)/],["Windows 10",/(Windows NT 10.0)/],["Windows ME",/Windows ME/],["Windows CE",/Windows CE|WinCE|Microsoft Pocket Internet Explorer/],["Open BSD",/OpenBSD/],["Sun OS",/SunOS/],["Chrome OS",/CrOS/],["Linux",/(Linux)|(X11)/],["Mac OS",/(Mac_PowerPC)|(Macintosh)/],["QNX",/QNX/],["BeOS",/BeOS/],["OS/2",/OS\/2/]];function D0(o){return o?Y3(o):typeof document>"u"&&typeof navigator<"u"&&navigator.product==="ReactNative"?new M0:typeof navigator<"u"?Y3(navigator.userAgent):N0()}function P0(o){return o!==""&&T0.reduce(function(i,t){var e=t[0],s=t[1];if(i)return i;var r=s.exec(o);return!!r&&[e,r]},!1)}function Y3(o){var i=P0(o);if(!i)return null;var t=i[0],e=i[1];if(t==="searchbot")return new E0;var s=e[1]&&e[1].split(".").join("_").split("_").slice(0,3);s?s.length<H3&&(s=T(T([],s,!0),O0(H3-s.length),!0)):s=[];var r=s.join("."),a=I0(o),h=A0.exec(o);return h&&h[1]?new k0(t,r,a,h[1]):new z(t,r,a)}function I0(o){for(var i=0,t=B3.length;i<t;i++){var e=B3[i],s=e[0],r=e[1],a=r.exec(o);if(a)return s}return null}function N0(){var o=typeof process<"u"&&process.version;return o?new cs(process.version.slice(1)):null}function O0(o){for(var i=[],t=0;t<o;t++)i.push("0");return i}var ps,F0=new Uint8Array(16);function $0(){if(!ps&&(ps=typeof crypto<"u"&&crypto.getRandomValues&&crypto.getRandomValues.bind(crypto)||typeof msCrypto<"u"&&typeof msCrypto.getRandomValues=="function"&&msCrypto.getRandomValues.bind(msCrypto),!ps))throw new Error("crypto.getRandomValues() not supported. See https://github.com/uuidjs/uuid#getrandomvalues-not-supported");return ps(F0)}const j0=/^(?:[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}|00000000-0000-0000-0000-000000000000)$/i;function z0(o){return typeof o=="string"&&j0.test(o)}for(var R=[],ti=0;ti<256;++ti)R.push((ti+256).toString(16).substr(1));function Z0(o){var i=arguments.length>1&&arguments[1]!==void 0?arguments[1]:0,t=(R[o[i+0]]+R[o[i+1]]+R[o[i+2]]+R[o[i+3]]+"-"+R[o[i+4]]+R[o[i+5]]+"-"+R[o[i+6]]+R[o[i+7]]+"-"+R[o[i+8]]+R[o[i+9]]+"-"+R[o[i+10]]+R[o[i+11]]+R[o[i+12]]+R[o[i+13]]+R[o[i+14]]+R[o[i+15]]).toLowerCase();if(!z0(t))throw TypeError("Stringified UUID is invalid");return t}function W3(o,i,t){o=o||{};var e=o.random||(o.rng||$0)();if(e[6]=e[6]&15|64,e[8]=e[8]&63|128,i){t=t||0;for(var s=0;s<16;++s)i[t+s]=e[s];return i}return Z0(e)}const q3=function(o,i,t){const e=i.toLowerCase();e==="text"?o.textContent=t:e==="html"?o.innerHTML=t:e==="styles"?n.setStyles(o,t):o.setAttribute(i,t)},R0=function(o,i){const t=i.toLowerCase();if(t==="text")return o.innerText||o.textContent||"";if(t==="html")return o.innerHTML||"";if(t==="value")switch(o.tagName.toString().toLowerCase()){case"select":return o.options[o.selectedIndex].value;default:return o.value}return o.getAttribute(i)},us=function(o){const i=W0(o||"").split(" "),t={};return i.filter(function(e){if(e!==""&&!t[e])return t[e]=e})},U3=function(o){return!o.tagName||/^(?:body|html)$/i.test(o.tagName)},X3=function(o){return/html/i.test(o.tagName)},ms=function(o,i){return parseInt(n.getStyle(o,i))||0},V0=function(o){return ms(o,"border-top-width")},H0=function(o){return ms(o,"border-left-width")},B0=function(o,i){return{left:o.x-ms(i,"margin-left"),top:o.y-ms(i,"margin-top")}},n=(o,i)=>{let t;const e=o.replace(/^\S*?(?=\.|#|$)/,h=>(t=h,""));let s="",r;e&&(r=e.split(".").filter(p=>!!p).map(p=>{const C=p.split("#"),g=C.shift();return s=C.pop()||s,g}));const a=document.createElement(t);return s&&a.setAttribute("id",s),r&&r.length&&a.setAttribute("class",r.join(" ")),i&&n.set(a,i),a};Object.assign(n,{el:(o,i)=>v(o)==="string"&&o?(i||document).querySelector(o):o,els:(o,i)=>(i||document).querySelectorAll(o),addClass:(o,i)=>{const t=n.el(o);return us(i).forEach(e=>{t.classList.add(e)}),t},removeClass:(o,i)=>{const t=n.el(o);return us(i).forEach(e=>{t.classList.remove(e)}),t},hasClass:(o,i)=>n.el(o).classList.contains(i),toggleClass:(o,i,t=!1)=>{const e=n.el(o);return us(i).forEach(s=>{let r=t;(r==null||r===!1)&&(r=!n.hasClass(e,s)),r?n.addClass(e,s):n.removeClass(e,s)}),e},checkClass:(o,i,t)=>{const e=n.el(o);return us(i).forEach(s=>{t?n.addClass(e,s):n.removeClass(e,s)}),e},mapProps:(o,i)=>{const t=n.el(o);for(const e in i){const s=b(e);if(typeof i[e]=="boolean")i[e]=t.hasAttribute(e)||t.hasAttribute(s)||!1;else{const r=t.getAttribute(e)||t.getAttribute(s)||"";r!==i[e]&&(i[e]=r)}}return t},toggleAttr:(o,i,t)=>{const e=n.el(o);return t?e.setAttribute(i,t):e.removeAttribute(i),e},set:(o,i,t)=>{const e=n.el(o);return typeof i=="object"?Object.keys(i).forEach(s=>{q3(e,s,i[s])}):q3(e,i,t),e},get:(o,i)=>{const t=n.el(o);return R0(t,i)},setProperty:(o,i,t)=>{const e=n.el(o);return e.setAttribute(i,t),e},getScroll:o=>{const i=n.el(o);return{x:i.scrollLeft,y:i.scrollTop}},getScrolls:o=>{let t=n.el(o).parentNode,e={x:0,y:0};for(;t&&!U3(t);)e.x+=t.scrollLeft,e.y+=t.scrollTop,t=t.parentNode;return e},getOffsetParent:o=>{let i=o.offsetParent;for(;i&&["table","td","th"].includes(i.tagName.toString().toLowerCase());)i=i.offsetParent;return i||document.body},getPosition:(o,i)=>{const t=n.el(o);for(i===window&&(i=document.documentElement);i&&n.getStyle(i,"display")==="contents";)i=i.parentElement;for(;o&&n.getStyle(o,"display")==="contents";)o=o.parentElement;const e=o.getBoundingClientRect(),s=document.documentElement,r=n.getScroll(s),a=n.getScrolls(t),h=n.getStyle(t,"position")==="fixed",p={x:e.left+a.x+(h?0:r.x)-s.clientLeft,y:e.top+a.y+(h?0:r.y)-s.clientTop},C=n.getScrolls(t),g={x:p.x-C.x,y:p.y-C.y};if(i){const f=n.getPosition(i);return{x:g.x-f.x-H0(i),y:g.y-f.y-V0(i)}}return g},setPosition:(o,i)=>{const t=n.el(o);return n.setStyles(t,B0(i.this))},getSize:o=>{const i=n.el(o);if(X3(i))return{x:i.clientWidth,y:i.clientHeight};{const t=i.getBoundingClientRect();return{x:t.width,y:t.height}}},getScrollSize:o=>{const i=n.el(o);if(/^(?:body|html)$/i.test(i.tagName)){const t=document.documentElement,e=document.body;return{x:Math.max(t.scrollWidth,e.scrollWidth),y:Math.max(t.scrollHeight,e.scrollHeight)}}return{x:i.scrollWidth,y:i.scrollHeight}},getStyle:function(o,i){const t=n.el(o),e=r2(i);let s=t.style[e];return s||(s=window.getComputedStyle(t)[e]),s},getStyles:o=>{const i=[...arguments],t=n.el(i.shift()),e={};return i.forEach(s=>{e[s]=n.getStyle(t,s)}),e},setStyle:(o,i,t)=>{const e=n.el(o);return typeof i=="object"?Object.keys(i).forEach(s=>{e.style[r2(s)]=i[s]}):e.style[r2(i)]=t,e},setStyles:(o,i)=>n.setStyle(o,i),isPositioned:o=>{const i=n.el(o),t=n.getStyle(i,"position");return t&&["absolute","fixed","relative","sticky"].includes(t)},show:o=>{const i=n.el(o);return n.getStyle(i,"display")==="none"&&n.setStyle(i,"display",i.dataset.storeDisplay||"block"),i},hide:o=>{const i=n.el(o),t=n.getStyle(i,"display");return t!=="none"&&(i.dataset.storeDisplay=t,n.setStyle(i,"display","none")),i},getParentSrcollNode:o=>{let t=n.el(o).parentElement,e=n.getStyle(t,"overflow"),s=n.getStyle(t,"overflow-y");for(;t&&(n.getScrollSize(t).y<=n.getSize(t).y||!["auto","scroll"].includes(e)&&!["auto","scroll"].includes(s));)t=t.parentElement,t&&(e=n.getStyle(t,"overflow"),s=n.getStyle(t,"overflow-y"));return t||document.documentElement},getParent:function(o,i){if(i){let t=o.parentElement;for(;t&&!t.matches(i);)t=t.parentElement;return t}else return o.parentElement},isBody:o=>U3(n.el(o)),isHtml:o=>X3(n.el(o)),empty:o=>{const i=n.el(o);for(;i.childNodes.length;)i.removeChild(i.childNodes[0]);return i},peel:(o,i)=>{const{node:t,position:e}=i&&v(i)==="element"?{node:i,position:"beforeend"}:{node:o,position:"beforebegin"};let s=o.firstElementChild;for(;s;)t.insertAdjacentElement(e,s),s=o.firstElementChild;o.remove()},getPositionParent(o){let i=n.el(o).parentElement;for(;i;){const t=i.tagName.toString().toLowerCase();if(t==="application")return i;if(t==="oo-dialog")return i._content;i=i.parentElement}return document.body},overlap(o,i,t=document.body,e){const s=this.getSize(n.el(i)),r=this.getPosition(n.el(i),t),a=n.el(o);this.setStyles(a,{position:"absolute",width:`${s.x}px`,height:`${s.y}px`,top:`${r.y}px`,left:`${r.x}px`}),a.isConnected||t.append(a)},appendTop(o,i){const t=o.firstChild;return t?o.insertBefore(i,t):o.appendChild(i)},isDisplayNone(o){if(window.getComputedStyle(o).display==="none")return!0;const t=o.parentElement;return t?n.isDisplayNone(t):!1}}),D0();function v(o){if(o==null)return"null";if(Array.isArray(o))return"array";if(o instanceof Map)return"map";if(o instanceof Set)return"set";if(o===window)return"window";if(o instanceof Date)return"date";if(o instanceof RegExp)return"regexp";if(o instanceof Error)return"error";if(o instanceof Event)return"event";if((typeof o=="function"||typeof o=="object")&&typeof o.then=="function")return"promise";if(o.nodeName){if(o.nodeType===1)return"element";if(o.nodeType===3)return/\S/.test(o.nodeValue)?"textnode":"whitespace";if(o.nodeType===9)return"document";if(o.nodeType||o.nodeType===0)return o.nodeName}else if(typeof o.length=="number"&&o.callee)return"arguments";return typeof o}function i2(o,i,t=null,e=!0){const s=i||this,r=t?Object.values(t):[],a=t?Object.keys(t).join(","):"";return Function("return function("+a+"){"+o+"}")().apply(s,r)}function $(o,i=20,t,e){const s=t;return i&&v(i)!=="number"&&(t=i,i=s&&v(s)==="number"?s:20),o.clear&&o.clear(),o.timerFunction=(r,a)=>{o.clear=()=>{o.timerId&&clearTimeout(o.timerId),o.promise&&(o.promise=null)},o.timerId=setTimeout(function(){o.timerId=null;try{const h=t?o.apply(t,e):o(e);Promise.resolve(h).then(p=>{r(p)})}catch(h){a(h)}},i)},o.promise=new Promise(o.timerFunction),o.promise}function G3(o){if(!o)return o;const i=v(o);return i==="function"?null:i==="array"||i==="object"?K3(o):o}function Y0(o){let i=o.length,t=new Array(i);for(;i--;)t[i]=G3(o[i]);return t}function K3(o){if(v(o)==="array")return Y0(o);const i={};for(let t in o)i[t]=G3(o[t]);return i}function J3(o){const i=document.createElement("a");i.target="_blank",i.href=o,i.click(),i.remove()}function b(o){return o.replace(/([A-Z])/g,"-$1").toLowerCase()}function r2(o){return o.replace(/-(\w)/g,function(i,t){return t.toUpperCase()})}function W0(o){return o.replace(/\s+/g," ").trim()}function ei(o){return String(o).replace(/\b[a-z]/g,function(i){return i.toUpperCase()})}function q0(o){return o.replace(/<script(?:\s+[\w-]+(?:=(?:"[^"]*"|'[^']*'))?)*\s*>([\s\S]*?)<\/script\s*>|\s+on\w+\s*=\s*(?:"[^"]*"|'[^']*')|javascript:.*/g,"")}function jt(o,i){const t=o.indexOf(i);t!==-1&&(o.splice(t,1),jt(o,i))}const si={ms:1,second:1e3,minute:6e4,hour:36e5,day:864e5,week:6084e5};function ot(o){return new Date(o.getTime())}function Cs(o,i){return o?o instanceof Date?i?ot(o):o:new Date(o):null}function U0(o,i){const t=new Date(o,i);return t.setDate(0),t}function X0(o,i){if(!o)return null;const t=Cs(o,i);return t.setHours(23),t.setMinutes(59),t.setSeconds(59),t.setMilliseconds(999),t}function I(o,i){if(!o)return null;const t=Cs(o,i);return t.setHours(0),t.setMinutes(0),t.setSeconds(0),t.setMilliseconds(0),t}function Q3(o,i){const t=ot(o),e=(7+o.getDay()-parseInt(i||0))%7;t.setDate(t.getDate()-e+3);const s=t.valueOf();return t.setMonth(0,1),t.getDay()!==4&&t.setMonth(0,1+(4-t.getDay()+7)%7),1+Math.ceil((s-t)/si.week)}function X(o,i="day",t=1){switch(i){case"year":return X(o,"month",t*12);case"month":const e=Cs(o),s=e.getDate();return e.setMonth(e.getMonth()+t+1,0),s<e.getDate()&&o.setDate(s),e;case"week":return X(o,"day",t*7);case"date":case"day":return o=Cs(o),o.setDate(o.getDate()+t),o}if(!si[i])throw new Error(i+" is not a supported interval");return o.setTime(this.getTime()+t*si[i])}function O(o,i){const t={"M+":o.getMonth()+1,"D+":o.getDate(),"h+":o.getHours()%12===0?12:o.getHours()%12,"H+":o.getHours(),"m+":o.getMinutes(),"s+":o.getSeconds(),"q+":Math.floor((o.getMonth()+3)/3),d:o.getDay(),S:o.getMilliseconds(),a:o.getHours()<12?"上午":"下午",A:o.getHours()<12?"AM":"PM"};/(Y+)/.test(i)&&(i=i.replace(RegExp.$1,(o.getFullYear()+"").substr(4-RegExp.$1.length))),/(W+)/.test(i)&&(t["W+"]=Q3(o));for(let e in t)new RegExp("("+e+")").test(i)&&(i=i.replace(RegExp.$1,RegExp.$1.length===1?t[e]:("00"+t[e]).substr((""+t[e]).length)));return i}function t4(o){if(!o)return!1;let i;if(o instanceof Date&&(i=o),typeof o=="number"){if(!isFinite(o))return!1;i=new Date(o)}if(typeof o=="string"){if(o.trim()==="")return!1;i=new Date(o)}return i?!isNaN(i.getTime()):!1}const ne={CNY:{iso:"CNY",symbol:"¥",continent:"Asia",thousands:",",decimal:"."},JPY:{iso:"JPY",symbol:"¥",continent:"Asia",thousands:",",decimal:"."},INR:{iso:"INR",symbol:"₹",continent:"Asia",thousands:",",decimal:"."},KRW:{iso:"KRW",symbol:"₩",continent:"Asia",thousands:",",decimal:"."},SGD:{iso:"SGD",symbol:"S$",continent:"Asia",thousands:",",decimal:"."},THB:{iso:"THB",symbol:"฿",continent:"Asia",thousands:",",decimal:"."},MYR:{iso:"MYR",symbol:"RM",continent:"Asia",thousands:",",decimal:"."},PHP:{iso:"PHP",symbol:"₱",continent:"Asia",thousands:",",decimal:"."},VND:{iso:"VND",symbol:"₫",continent:"Asia",thousands:".",decimal:","},HKD:{iso:"HKD",symbol:"HK$",continent:"Asia",thousands:",",decimal:"."},MOP:{iso:"MOP",symbol:"MOP$",continent:"Asia",thousands:",",decimal:"."},TWD:{iso:"TWD",symbol:"NT$",continent:"Asia",thousands:",",decimal:"."},AED:{iso:"AED",symbol:"د.إ",continent:"Asia",thousands:",",decimal:"."},SAR:{iso:"SAR",symbol:"ر.س",continent:"Asia",thousands:",",decimal:"."},TRY:{iso:"TRY",symbol:"₺",continent:"Asia",thousands:".",decimal:","},EUR:{iso:"EUR",symbol:"€",continent:"Europe",thousands:".",decimal:","},GBP:{iso:"GBP",symbol:"£",continent:"Europe",thousands:",",decimal:"."},CHF:{iso:"CHF",symbol:"CHF",continent:"Europe",thousands:"'",decimal:"."},RUB:{iso:"RUB",symbol:"₽",continent:"Europe",thousands:" ",decimal:","},PLN:{iso:"PLN",symbol:"zł",continent:"Europe",thousands:" ",decimal:","},NOK:{iso:"NOK",symbol:"kr",continent:"Europe",thousands:" ",decimal:","},SEK:{iso:"SEK",symbol:"kr",continent:"Europe",thousands:" ",decimal:","},DKK:{iso:"DKK",symbol:"kr",continent:"Europe",thousands:".",decimal:","},HUF:{iso:"HUF",symbol:"Ft",continent:"Europe",thousands:" ",decimal:","},USD:{iso:"USD",symbol:"US$",continent:"North America",thousands:",",decimal:"."},CAD:{iso:"CAD",symbol:"C$",continent:"North America",thousands:",",decimal:"."},MXN:{iso:"MXN",symbol:"Mex$",continent:"North America",thousands:",",decimal:"."},BRL:{iso:"BRL",symbol:"R$",continent:"South America",thousands:".",decimal:","},ARS:{iso:"ARS",symbol:"AR$",continent:"South America",thousands:".",decimal:","},CLP:{iso:"CLP",symbol:"CLP$",continent:"South America",thousands:".",decimal:","},AUD:{iso:"AUD",symbol:"A$",continent:"Oceania",thousands:",",decimal:"."},NZD:{iso:"NZD",symbol:"NZ$",continent:"Oceania",thousands:",",decimal:"."},PGK:{iso:"PGK",symbol:"K",continent:"Oceania",thousands:",",decimal:"."},TOP:{iso:"TOP",symbol:"T$",continent:"Oceania",thousands:",",decimal:"."},WST:{iso:"WST",symbol:"WS$",continent:"Oceania",thousands:",",decimal:"."},ZAR:{iso:"ZAR",symbol:"R",continent:"Africa",thousands:" ",decimal:"."},EGP:{iso:"EGP",symbol:"ج.م",continent:"Africa",thousands:",",decimal:"."},NGN:{iso:"NGN",symbol:"₦",continent:"Africa",thousands:",",decimal:"."},KES:{iso:"KES",symbol:"KSh",continent:"Africa",thousands:",",decimal:"."},GHS:{iso:"GHS",symbol:"GH₵",continent:"Africa",thousands:",",decimal:"."},MAD:{iso:"MAD",symbol:"د.م.",continent:"Africa",thousands:",",decimal:"."},TZS:{iso:"TZS",symbol:"TSh",continent:"Africa",thousands:",",decimal:"."}},ni={prefix:"¥",suffix:"",thousands:",",decimal:".",precision:2,allowblank:!0,disablenegative:!0,maximum:9007199254740991,minimum:-9007199254740991,round:!0},G0=["+","-"],K0=["decimal","thousands","prefix","suffix"];function J0(o,i){return G0.includes(o)?(console.warn(`oo-currency "${i}" property don't accept "${o}" as a value.`),!1):/\d/g.test(o)?(console.warn(`oo-currency "${i}" property don't accept "${o}" (any number) as a value.`),!1):!0}function e4(o){for(const i of K0)if(!J0(o[i],i))return!1;return!0}function Q0(o){return typeof o=="string"?/^-?[\d]+$/g.test(o):!1}function s4(o){return typeof o=="string"?/^-?[\d]+(\.[\d]+)$/g.test(o):!1}function ii(o){return o.replace(/^(-?)0+(?!\.)(.+)/,"$1$2")}function n4(o,i,t){return i>o.length-1?o:o.substring(0,i)+t+o.substring(i+1)}function gs(o,i,t){return o=o?o.toString():"",t==="."&&(o=o.replaceAll(t,"@@")),i&&i!=="."&&(o=o.replaceAll(i,".")),o=o.replace(/[^\d.]+/g,"")||"0",o.replace(/^(\d*\.?\d*).*$/,"$1")}function to(o,i){return o.replace(/(\d)(?=(?:\d{3})+\b)/gm,`$1${i}`)}function fs(o,i){const t=i-vs(o);if(t>=0)return o;let e=o.slice(0,t);const s=o.slice(t);if(e.charAt(e.length-1)==="."&&(e=e.slice(0,-1)),parseInt(s.charAt(0),10)>=5){for(let r=e.length-1;r>=0;r-=1){const a=e.charAt(r);if(a!=="."&&a!=="-"){const h=parseInt(a,10)+1;if(h<10)return n4(e,r,h);e=n4(e,r,"0")}}return`1${e}`}return e}function i4(o,i,t="."){if((!i||i<0)&&(i=0),i===0)o.includes(t)&&(o=o.split(".")[0]);else if(o.includes(t)&&!o.endsWith(t)){const e=o.split(".");o=`${e[0]}${t}${e[1].substring(0,i)}`}else o=`${o}${t}${"0".repeat(i)}`;return ii(o)}function vs(o){typeof o=="number"&&(o=o.toString());const i=o.indexOf(".");return i===-1?0:o.length-i-1}class bs{constructor(i){m(this,"number",0n);m(this,"decimal",0);this.setNumber(i)}getNumber(){return this.number}getDecimalPrecision(){return this.decimal}setNumber(i){this.decimal=0,typeof i=="bigint"?this.number=i:typeof i=="number"?this.setupString(i.toString()):this.setupString(i)}toFixed(i=0,t=!0){let e=this.toString();const s=i-this.getDecimalPrecision();return s>0?(e.includes(".")||(e+="."),e.padEnd(e.length+s,"0")):s<0?t?fs(e,i):e.slice(0,s):e}toString(){let i=this.number.toString();if(this.decimal){let t=!1;return i.charAt(0)==="-"&&(i=i.substring(1),t=!0),i=i.padStart(i.length+this.decimal,"0"),i=`${i.slice(0,-this.decimal)}.${i.slice(-this.decimal)}`,i=ii(i),(t?"-":"")+i}return i}lessThan(i){const[t,e]=this.adjustComparisonNumbers(i);return t<e}biggerThan(i){const[t,e]=this.adjustComparisonNumbers(i);return t>e}isEqual(i){const[t,e]=this.adjustComparisonNumbers(i);return t===e}setupString(i){if(i=ii(i),Q0(i))this.number=BigInt(i);else if(s4(i))this.decimal=vs(i),this.number=BigInt(i.replace(".",""));else throw new Error(`BigNumber has received and invalid format for the constructor: ${i}`)}adjustComparisonNumbers(i){let t;i.constructor.name!=="BigNumber"?t=new bs(i):t=i;const e=this.getDecimalPrecision()-t.getDecimalPrecision();let s=this.getNumber(),r=t.getNumber();return e>0?r=t.getNumber()*10n**BigInt(e):e<0&&(s=this.getNumber()*10n**BigInt(e*-1)),[s,r]}}function eo(o,i,t){return i?o+t+i:o}function Z2(o){return Math.max(0,Math.min(o,1e3))}function _s(o,i={},t="CNY",e=!1){const s=o,r=t&&ne.hasOwnProperty(t)?ne[t]:{},a=Object.assign({},ni,r,i);if(t&&(a.prefix=a.prefixuse==="symbol"?a.symbol:a.prefixuse==="iso"?a.iso:a.prefix),!e4(a))return"error";const{round:h,prefix:p,suffix:C,thousands:g,decimal:f,allowblank:x,disablenegative:y,minimum:Mt,maximum:Q1,number:St}=a,$t=a.precision==="auto"?vs(gs(o)):a.precision;if(o==null)o="";else if(typeof o=="number")h?o=o.toFixed(Z2($t)):o=o.toFixed(Z2($t)+1).slice(0,-1);else if(h&&s4(o)&&f===".")o=fs(o,Z2($t));else if(!y&&o==="-")return o;const Qe=y?"":o.indexOf("-")>=0?"-":"";let et=o.replace(p,"").replace(C,"").replace(f.repeat(2),f);et.startsWith(f)?et="0"+et:et.startsWith(`-${f}`)&&(et=`-0${f}${et}`);let P4=!1;et.endsWith(f)&&(et=et.slice(0,-1),P4=!0);const o3=et.split(f);o3.length>1&&(et=`${o3[0]}${f}${o3.slice(1).join("")}`);let l3=gs(et);h&&g!=="."&&(l3=fs(l3,$t||0));const ts=new bs(Qe+i4(l3,$t,"."));Q1&&ts.biggerThan(Q1)&&ts.setNumber(Q1),Mt&&ts.lessThan(Mt)&&ts.setNumber(Mt);let I4=ts.toFixed(Z2($t),h);if(/^0(\.0+)?$/g.test(I4)&&x)return e?/^0(\.0+)?$/g.test(s)||s==="0."?s:"":s?"0":"";let[W5,R8]=I4.split(".");return W5=to(W5,g),a.precision==="auto"&&P4?p+W5+f+C:p+eo(W5,R8,f)+C}function l2(o,i={},t="CNY",e){const s=t&&ne.hasOwnProperty(t)?ne[t]:{},r=Object.assign({},ni,s,i);if(t&&(r.prefix=r.prefixuse==="symbol"?r.symbol:r.prefixuse==="iso"?r.iso:r.prefix),!e4(r))return"error";const{round:a,disablenegative:h,prefix:p,suffix:C,decimal:g,minimum:f,maximum:x,thousands:y}=r,Mt=r.precision==="auto"?vs(gs(o,g,y)):r.precision;if(!h&&o==="-")return o;const Q1=h?"":o.indexOf("-")>=0?"-":"";let St=o.replace(p,"").replace(C,"");St.startsWith(g)?St="0"+St:St.startsWith(`-${g}`)&&(St=`-0${g}${St}`),St.endsWith(g)&&(St=St.slice(0,-1));let $t=gs(St,g,y);a&&y!=="."&&($t=fs($t,Mt||0));const Qe=new bs(Q1+i4($t,Mt,"."));x&&Qe.biggerThan(x)&&Qe.setNumber(x),f&&Qe.lessThan(f)&&Qe.setNumber(f);let et=Qe.toFixed(Z2(Mt),a);return e?et:parseFloat(et)}function r4(o,i){const t=`css${W3()}`;let e=o.toString();if(e){if(i&&n.addClass(i,t),e=e.replace(/\/\*(\s|\S)*?\*\//g,""),i){const r=new RegExp("[^{}\\/]+\\s*(?=\\{)","g"),a="."+t+" ";e=e.replace(r,h=>{const p=h.trim();return p.startsWith("@")||p==="from"||p==="to"?p:p.split(/\s*,\s*/g).map(function(g){return a+g}).join(", ")})}const s=n("style");return s.setAttribute("type","text/css"),s.setAttribute("id",t),document.head.appendChild(s),s.appendChild(document.createTextNode(e)),s}return null}const ri={};function o4(o,i,t,e){if(t&&ri[t])return ri[t];const s=document.createElement("template");s.innerHTML=o;const r=s.content;if(i){const a=document.createElement("style");a.textContent=i,r.prepend(a)}if(e){const a=document.querySelector("#oo-css-skin");if(a){const h=a.cloneNode(!0);h.removeAttribute("id"),r.prepend(h)}}return document.body.appendChild(s),t&&(ri[t]=s.content),s.content}class N extends HTMLElement{constructor(){super();d(this,Y2);d(this,Ps);d(this,Is);d(this,Rt,null)}_setEvent(){}_afterRender(){}_render(t){}_connected(){}_disconnected(){}_initialize(t,e,s,r){Object.hasOwn(this,"value")&&(this.value&&this.setAttribute("value",this.value),Reflect.deleteProperty(this,"value")),this._props=e?this._getProps(e.prop):{};const a=o4(s,r,t,!0);this._useTemplate(a),this._render(t),this._setEvent(),this._afterRender(),this._createProperties(e),this.dispatchEvent(new CustomEvent("load"))}_createProperties(t){Object.keys(this._props).forEach(e=>{let s=e;for(;this.hasOwnProperty(s)||t.prototype.hasOwnProperty(s);)s=`_${s}`;Object.hasOwn(this,s)||Object.defineProperty(this,s,{get:()=>this._props[s],set:r=>{r!==this._props[s]&&this.setAttribute(b(s),r)}})})}_getProps(t){const e=K3(t);return this.getAttributeNames().forEach(s=>{const r=r2(s);if(e.hasOwnProperty(r)){const a=this.getAttribute(s);/{{.*}}/g.test(a)||c(this,Y2,d3).call(this,e,r,a)}}),e}_useCss(t){const e=document.createElement("style");return e.textContent=t,this._content.insertAdjacentElement("beforebegin",e),e}_useCssLink(t){const e=document.createElement("link");return e.rel="stylesheet",e.type="text/css",e.charSet="UTF-8",e.href=t,this._content.insertAdjacentElement("beforebegin",e),e}useCss(t){this.styleNode&&(this.styleNode.remove(),this.styleNode=null),t&&(this.styleNode=this._useCss(t))}useCssLink(t){this.styleLinkNode&&(this.styleLinkNode.remove(),this.styleLinkNode=null),t&&(this.styleLinkNode=this._useCssLink(t))}_useTemplate(t,e="open"){(this.shadowRoot||this.attachShadow({mode:e})).appendChild(t.cloneNode(!0)),c(this,Ps,N4).call(this)}connectedCallback(){this._connected()}disconnectedCallback(){this._disconnected()}attributeChangedCallback(t,e,s){var r;if(!/{{.*}}/g.test(s)&&this._props){const a=r2(t),h=(r=this._props)==null?void 0:r[a];c(this,Y2,d3).call(this,this._props,a,s),c(this,Is,O4).call(this,a,h,e)}}_setProps(t,e,s){const r=this._props[t];s!==r&&(this._setPropMap[t]?this._setPropMap[t](e,r):this._setPropMap.$default(t,e,r))}_useSkin(t){if(l(this,Rt)&&l(this,Rt).remove(),u(this,Rt,null),t){const e=t.split(/\s*;\s*/g);u(this,Rt,n("style"));let s="";e.forEach(r=>{const a=r.split(/\s*:\s*/g);s+=`--${a[0]}: ${a[1]};
	`}),l(this,Rt).textContent=`
.content{
	${s}
}
`,this._content.insertAdjacentElement("beforebegin",l(this,Rt))}}_fillContent(t){new MutationObserver(function(s){s.forEach(r=>{r.addedNodes.forEach(a=>{a.nodeType===Node.ELEMENT_NODE&&a.setAttribute("slot",t)})})}).observe(this,{subtree:!1,childList:!0,attributes:!1,characterData:!1}),this._setContent(t)}_setContent(t){let e=this.firstElementChild;for(;e;)e.setAttribute("slot",t),e=e.nextElementSibling}_canRender(t,e){if(t&&t.length){for(const s of t)if(!this.getAttribute(s))return!1}if(e&&e.length){for(const s of e)if(this.getAttribute(s))return!0}return!0}}Rt=new WeakMap,Y2=new WeakSet,d3=function(t,e,s){switch(v(t[e])){case"boolean":t[e]=!!s&&s!=="false";break;case"number":t[e]=isNaN(s)?s:parseInt(s);break;case"object":try{t[e]=JSON.parse(s)}catch{t[e]=s}break;case"array":try{t[e]=JSON.parse(s)}catch{t[e]=s?s.split(/\s*,\s*/g):[]}break;default:t[e]=s}},Ps=new WeakSet,N4=function(){for(const t in this._elements)this._elements[t]=this.shadowRoot.querySelector(`.${t}`);this._content=this.shadowRoot.querySelector(".content")},Is=new WeakSet,O4=function(t,e,s){(t?[t]:Object.keys(this._props)).forEach(a=>{this._setProps(a,e,s)})};const so=`<div class="button content">\r
	<div class="prefix"></div>\r
	<div class="text"></div>\r
	<div class="suffix"></div>\r
</div>\r
`,no=`body{\r
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
\r
    --padding: 0.375em 0.6em\r
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
.button.icon-light{\r
    background-color: var(--light-bg);\r
    color: unset;\r
    padding: 0.375em;\r
    border: 1px solid var(--oo-color-gray-b);\r
}\r
.button.icon-simple{\r
    background-color: transparent;\r
    color: unset;\r
    padding: 0.375em;\r
    border: 1px solid transparent;\r
    gap: 0;\r
}\r
\r
.button.icon .text, .button.icon-light .text{\r
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
.button.icon-light:hover{\r
    background-color: var(--light-hover-bg);\r
    color: var(--light-hover-color);\r
}\r
.button.icon-light:active{\r
    background-color: var(--light-active-bg);\r
    color: var(--light-active-color);\r
}\r
.button.icon-light:focus{\r
    background-color: var(--light-focus-bg);\r
    color: var(--light-focus-color);\r
}\r
\r
.button.icon-simple:hover{\r
    background-color: transparent;\r
    color: unset;\r
}\r
.button.icon-simple:active{\r
    background-color: transparent;\r
    color: unset;\r
}\r
.button.icon-simple:focus{\r
    background-color: transparent;\r
    color: unset;\r
}\r
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
    display: none !important;\r
}\r
`,Os=class Os extends N{constructor(){super();d(this,Ns);d(this,W2);m(this,"_elements",{text:null,prefix:null,suffix:null,button:null});d(this,xt,{});m(this,"_setPropMap",{leftIcon:t=>{n.removeClass(this._elements.prefix,`ooicon-${t} icon`),this._props.leftIcon&&n.addClass(this._elements.prefix,`ooicon-${this._props.leftIcon} icon`)},rightIcon:t=>{n.removeClass(this._elements.suffix,`ooicon-${t} icon`),this._props.rightIcon&&n.addClass(this._elements.suffix,`ooicon-${this._props.rightIcon} icon`)},styles:()=>{},disabled:t=>{n.checkClass(this._elements.button,"disabled",this._props.disabled),n.toggleAttr(this._elements.button,"disabled",this._props.disabled),c(this,Ns,F4).call(this,this._props.disabled)},type:t=>{this._props.type||(this._props.type="default"),t&&n.removeClass(this._elements.button,t),n.addClass(this._elements.button,this._props.type)},text:()=>{this.innerHTML.trim()||(this._elements.text.textContent=this._props.text)},skin:()=>{this._useSkin(this._props.skin)},$default:t=>{t==="value"&&(this.value=this._props[t]),n.toggleAttr(this._elements.button,t,this._props[t])}})}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_init(t,e,s,r){this._initialize(t,e||Os,s||so,r||no)}_render(){const t=this;new MutationObserver(function(){var s;c(s=t,W2,h3).call(s)}).observe(this,{subtree:!0,childList:!0,attributes:!0,characterData:!0}),c(this,W2,h3).call(this)}_connected(){}};xt=new WeakMap,Ns=new WeakSet,F4=function(t){if(t){u(this,xt,l(this,xt)||{});for(let e in this)e.startsWith("on")&&this[e]&&(l(this,xt)[e]=this[e],this[e]=null);this.style.pointerEvents="none"}else{if(l(this,xt))for(let e in l(this,xt))l(this,xt)[e]&&(this[e]=l(this,xt)[e],l(this,xt)[e]=null);this.style.pointerEvents="auto"}},W2=new WeakSet,h3=function(){this.innerHTML.trim()&&(n.empty(this._elements.text),this._elements.text.insertAdjacentHTML("beforeend",this.innerHTML))},m(Os,"prop",{leftIcon:"",rightIcon:"",styles:"",disabled:!1,text:"",type:"",skin:""});let Ls=Os;class l4 extends Ls{constructor(){super(),this._init("oo-button")}}const io=`<div class="content">\r
    <div class="left">\r
        <div class="top">\r
            <div class="top-left">\r
                <slot name="title"></slot>\r
            </div>\r
            <div class="title"></div>\r
            <div class="today">今天</div>\r
            <div class="top-right">\r
                <div class="prev ooicon-arrow_back"></div>\r
                <div class="next ooicon-arrow_forward"></div>\r
            </div>\r
        </div>\r
        <div class="middle">\r
            <table border="0" cellpadding="0"  cellspacing="1" class="yearContent">\r
                <tbody>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                </tbody>\r
            </table>\r
            <table border="0" cellpadding="0" cellspacing="1" class="monthContent">\r
                <tbody>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
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
                    <td class="week hide"><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                </tbody>\r
            </table>\r
        </div>\r
        <div class="bottom">\r
            <div><div class="clean">清除</div></div>\r
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
<!--        <div class="cleanTime">清除</div>-->\r
    </div>\r
    <div class="footer">\r
        <div class="cleanTime">清除</div>\r
    </div>\r
</div>\r
`,a4=`*{box-sizing:border-box}.content{display:flex;height:100%;overflow:hidden;-webkit-user-select:none;user-select:none}.content[mode=time]{flex-direction:column}.left{flex:8;gap:.5em;display:flex;flex-direction:column}.right{flex:5;height:100%;overflow:hidden}.top{display:flex;align-items:center;gap:.5em;padding:.5em 0;border-bottom:1px solid var(--oo-color-gray1)}.top-right{display:flex;align-items:center;gap:.5em}.middle{flex:1}.bottom{line-height:1em;display:flex;justify-content:center;gap:2em}.title{cursor:pointer;color:var(--oo-color-text);font-weight:700;flex:1}.title:hover{color:var(--oo-color-main)}.prev{height:2em;line-height:2em;cursor:pointer}.prev:hover{color:var(--oo-color-main)}.next{height:2em;line-height:2em;cursor:pointer}.next:hover{color:var(--oo-color-main)}.time{display:flex;height:100%;justify-content:center}.cleanTime{height:1.5em;text-align:center;color:var(--oo-color-main);cursor:pointer;display:flex;align-items:center;justify-content:center}table{width:100%;height:100%}th{color:var(--oo-color-text);font-weight:700;line-height:2em;height:2em}td{cursor:pointer;text-align:center;color:var(--oo-color-text)}td.other{color:var(--oo-color-text3)}td div{display:flex;justify-content:center;align-items:center;width:100%;height:100%;position:relative}td span{display:block;border:1px solid transparent}.yearContent td span,.monthContent td span{min-height:3em;min-width:3em;border-radius:.25em;display:flex;align-items:center;justify-content:center}.dateContent td span{min-height:2em;min-width:2em;border-radius:.25em;display:flex;align-items:center;justify-content:center}td:hover span{border-color:var(--oo-color-over);background-color:var(--oo-color-over)}td.today span{color:var(--oo-color-main);border-color:var(--oo-color-main);background-color:var(--oo-color-bg-white)}td.selected span{color:var(--oo-color-text-white);border-color:var(--oo-color-main);background-color:var(--oo-color-main)}.week-row-over span,.week-row-selected span{border-color:var(--oo-color-over)!important;background-color:var(--oo-color-over)!important}.week-row-selected>td:first-child>div>span{color:var(--oo-color-text-white)!important;border-color:var(--oo-color-main)!important;background-color:var(--oo-color-main)!important}.line{height:100%;width:1em;border-right:1px solid var(--oo-color-gray1)}ul{padding:0;margin:0;list-style:none;overflow:auto;flex:1;min-width:2em}ul::-webkit-scrollbar{display:none;width:0;height:0}li{text-align:center;height:2em;line-height:2em;cursor:pointer;display:flex;justify-content:center}li span{height:1.6em;display:block;line-height:1.6em;width:1.6em;border-radius:.25em;border:1px solid transparent}li:hover span{border-color:var(--oo-color-over);background-color:var(--oo-color-over)}li.selected span{color:var(--oo-color-text-white);border-color:var(--oo-color-main);background-color:var(--oo-color-main)}.week{color:var(--oo-color-main)}.disabled{color:var(--oo-color-text4)!important;cursor:not-allowed!important}.disabled span{border-color:transparent!important;background-color:transparent!important}.today{color:var(--oo-color-main);font-weight:700;cursor:pointer}.clean{color:var(--oo-color-main);cursor:pointer}.hide{display:none}@media only screen and (max-width: 767px){.content{display:flex;flex-direction:column;gap:.2em}[mode=datetime] .clean{display:none!important}.clean-enable[mode=datetime] .cleanTime{display:flex!important}.line{height:1px!important;width:100%!important;border-bottom:1px solid var(--oo-color-gray1);border-right:0!important}}ul::-webkit-scrollbar{display:unset;background-color:transparent;cursor:pointer;width:4px;height:4px}ul::-webkit-scrollbar-thumb{width:4px;border-radius:4px;background-color:var(--oo-color-text3);cursor:pointer}ul::-webkit-scrollbar-thumb:hover{width:4px;border-radius:4px;background-color:var(--oo-color-text2);cursor:pointer}
`,At="datetime",E="date",w="year",k="month",M="week",lt="time",oi="hour",li="minute",ai="second",ro="YYYY-MM-DD HH:mm:ss",ve="YYYY-MM-DD",oo="YYYY",lo="YYYY-MM",ao="YYYY WW",ho="HH:mm:ss",co="HH:mm",P=class P extends N{constructor(t){super();d(this,Gi);d(this,Me);d(this,Wt);d(this,st);d(this,$s);d(this,js);d(this,zs);d(this,Zs);d(this,Rs);d(this,Vs);m(this,"currentDate");m(this,"selectedYear");m(this,"selectedMonth");m(this,"selectedDate");m(this,"selectedHour");m(this,"selectedMinute");m(this,"selectedSecond");m(this,"selectedWeekNumber");m(this,"mode");d(this,Dt,void 0);d(this,_e,void 0);d(this,mt,void 0);d(this,Pt,void 0);d(this,It,void 0);d(this,q2,void 0);d(this,U2,void 0);d(this,X2,void 0);d(this,G2,void 0);d(this,K2,void 0);d(this,J2,void 0);d(this,Q2,void 0);d(this,le,void 0);d(this,Le,void 0);d(this,xe,void 0);d(this,Vt,void 0);d(this,Ht,void 0);d(this,Bt,void 0);d(this,Yt,void 0);m(this,"oldValue");d(this,h2,void 0);d(this,t1,void 0);d(this,e1,void 0);d(this,c2,void 0);d(this,p2,void 0);d(this,u2,void 0);d(this,s1,void 0);d(this,m2,void 0);d(this,ye,void 0);d(this,ae,void 0);d(this,we,void 0);d(this,ke,void 0);d(this,Ee,void 0);m(this,"_elements",{content:null,top:null,title:null,prev:null,next:null,middle:null,bottom:null,left:null,yearContent:null,monthContent:null,dateContent:null,right:null,time:null,hourContent:null,minuteContent:null,secondContent:null,line:null,clean:null,cleanTime:null,today:null});d(this,Xi,{});m(this,"_setPropMap",{skin:()=>{this._useSkin(this._props.skin)},view:()=>{this._changeType()},value:()=>{this._props.value!==this.oldValue&&(this.useValue(),this.showView(),this._checkTimeSelected())},baseDate:()=>{this._useBaseDate(),this.showView()},mode:()=>{this._checkMode(),this._changeType()},secondEnable:()=>{this._checkClass(),this.setContentEvent()},cleanEnable:()=>{this._checkClass()},todayEnable:()=>{this._checkClass()},datetimeRange:()=>{this.setRange()},dateRange:()=>{this.setRange()},timeRange:()=>{this.setRange()},weekBegin:()=>{[E,M].includes(this.currentView)&&this.showView()},step:()=>{this._checkStep()},$default:t=>{}});d(this,Fs,[t=>this.mode===w&&this._isEnableYear(t)&&(this.selectedYear=parseInt(t)||!0),t=>this.mode===k&&this._isEnableMonth(t)&&((()=>{const[e,s]=t.split("-");this.selectedYear=parseInt(e),this.selectedMonth=parseInt(s)-1})()||!0),t=>this.mode===E&&this._isEnableDate(t)&&((()=>{const[e,s,r]=t.split("-");this.selectedYear=parseInt(e),this.selectedMonth=parseInt(s)-1,this.selectedDate=parseInt(r)})()||!0),t=>this.mode===M&&((()=>{const[e,s]=t.split(" ");this.selectedYear=parseInt(e),this.selectedWeekNumber=parseInt(s)})()||!0),t=>this.mode===lt&&((()=>{const[e,s,r]=t.split(":");this._isEnableHour(e)&&(this.selectedHour=parseInt(e)),this._isEnableMinute(s)&&(this.selectedMinute=parseInt(s)),r&&this._isEnableSecond(r)&&(this.selectedSecond=parseInt(r))})()||!0),t=>{const e=P.parseDate(t,!0);this._isEnableDate(e)&&(this.selectedYear=e.getFullYear(),this.selectedMonth=e.getMonth(),this.selectedDate=e.getDate()),this._isEnableHour(e.getHours())&&(this.selectedHour=e.getHours()),this._isEnableMinute(e.getMinutes())&&(this.selectedMinute=e.getMinutes()),this._isEnableSecond(e.getSeconds())&&(this.selectedSecond=e.getSeconds())}]);!t&&this._initialize("oo-calendar",P,io,a4)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}static parseDate(t,e){return t?t instanceof Date?e?new Date(t.getTime()):t:new Date(t):null}setDatetimeRange(t){this._props.datetimeRange=t,this._setPropMap.datetimeRange(),this.showView(),this._checkTimeSelected()}setDateRange(t){this._props.dateRange=t,this._setPropMap.dateRange(),this.showView()}setTimeRange(t){this._props.timeRange=t,this._setPropMap.timeRange(),this._checkTimeContent()}setCustomRangeMethod(t,e){switch(t){case w:u(this,m2,e),$(this.showView,20,this);break;case k:u(this,ye,e),$(this.showView,20,this);break;case E:u(this,ae,e),u(this,s1,(s,r)=>{do{if(l(this,ae).call(this,s))return!0;s=s.setDate(s.getDate()+1)}while(s<=r);return!1}),$(this.showView,20,this);break;case oi:u(this,we,e),$(this._checkTimeContent,20,this);break;case li:u(this,ke,e),$(this._checkTimeContent,20,this);break;case ai:u(this,Ee,e),$(this._checkTimeContent,20,this);break}}_checkMode(){[w,k,E,M,At,lt].includes(this._props.mode)||(console.warn(`Invalid mode: ${this._props.mode}. Use "${At}" instead`),this._props.mode=At),this.mode=this._props.mode,this._elements.content.setAttribute("mode",this._props.mode)}_setProps(t,e){const s=this._props[t];e!==s&&(this._setPropMap[t]?this._setPropMap[t](e,s):this._setPropMap.$default(t,e,s))}_render(){this._checkMode(),this.currentView=this._getDefaultView(),this.setRange(),this._checkClass(),this._useBaseDate(),this.useValue(),this.showView(),this._checkTimeSelected(),[At,lt].includes(this.mode)&&(this._checkTimeContent(),this._checkStep())}_getDefaultView(){const t=this._props.view;switch(this.mode){case w:return w;case k:return[w,k].includes(t)?t:k;case M:return[w,k,M].includes(t)?t:M;case lt:return lt;default:return[w,k,M,E].includes(t)?t:E}}useValue(){if(!this._props.value){this.selectedYear=null,this.selectedMonth=null,this.selectedDate=null,this.selectedHour=null,this.selectedMinute=null,this.selectedSecond=null;return}const t=v(this._props.value)==="date"?O(this._props.value,this.getDefaultFormat()):this._props.value;l(this,Fs).some(e=>e(t)),isNaN(this.selectedYear)||this.currentDate.setFullYear(this.selectedYear),isNaN(this.selectedMonth)||this.currentDate.setMonth(this.selectedMonth),isNaN(this.selectedDate)||this.currentDate.setDate(this.selectedDate),isNaN(this.selectedHour)||this.currentDate.setHours(this.selectedHour),isNaN(this.selectedMinute)||this.currentDate.setMinutes(this.selectedMinute),isNaN(this.selectedSecond)||this.currentDate.setSeconds(this.selectedSecond)}_useBaseDate(){this.currentDate=this._getDefaultCurrentDate()}_getDefaultCurrentDate(){var t,e;if(this._props.baseDate&&this._isEnableDate(this._props.baseDate))return P.parseDate(this._props.baseDate,!0);if(this._isEnableDate(new Date))return new Date;if((t=this._props)!=null&&t.datetimeRange.length)return ot(l(this,Pt)[0][0]||l(this,Pt)[0][1]);if((e=this._props)!=null&&e.dateRange.length)return ot(l(this,mt)[0][0]||l(this,mt)[0][1])}_changeType(){this.currentView=this._getDefaultView(),this._checkClass(),this.setContentEvent(),this.showView()}_setEvent(){this._elements.title.addEventListener("click",()=>{this.changeView()}),this._elements.prev.addEventListener("click",()=>{this.gotoPrev()}),this._elements.next.addEventListener("click",()=>{this.gotoNext()}),this._elements.today.addEventListener("click",()=>{this.gotoToday()}),this._elements.clean.addEventListener("click",()=>{this.clear()}),this._elements.cleanTime.addEventListener("click",()=>{this.clear()}),this.setContentEvent(),this.setTouchEvent()}setContentEvent(){this.mode!==lt&&this.setYearEvent(),[lt,w].includes(this.mode)||this.setMonthEvent(),this.mode===M&&this.setWeekEvent(),[At,E].includes(this.mode)&&this.setDateEvent(),[lt,At].includes(this.mode)&&(this.setHourEvent(),this.setMinuteEvent(),this._props.secondEnable&&this.setSecondEvent())}setTouchEvent(){let t=0,e=0;this._elements.middle.addEventListener("touchstart",function(r){t=r.changedTouches[0].screenX}),this._elements.middle.addEventListener("touchend",function(r){e=r.changedTouches[0].screenX,s()});const s=()=>{t-e>50?this.gotoNext():e-t>50&&this.gotoPrev()}}getDate(){const t=ot(this.currentDate);return v(this.selectedYear)!=="null"&&t.setFullYear(this.selectedYear),v(this.selectedMonth)!=="null"&&t.setMonth(this.selectedMonth),v(this.selectedDate)!=="null"&&t.setDate(this.selectedDate),t.setHours(this.selectedHour||0),t.setMinutes(this.selectedMinute||0),t.setSeconds(this.selectedSecond||0),t}getSelectedDate(){return v(this.selectedYear)!=="null"&&v(this.selectedMonth)!=="null"&&v(this.selectedDate)!=="null"?new Date(this.selectedYear,this.selectedMonth,this.selectedDate):null}selectDate(t){const e=t.parentNode.parentNode.querySelector("td.selected");e&&n.removeClass(e,"selected"),n.addClass(t,"selected"),this.dispatchChangeEvent()}selectTime(t,e){const s=t.parentNode.querySelector("li.selected");s&&n.removeClass(s,"selected"),n.addClass(t,"selected"),this.dispatchChangeEvent()}getDefaultFormat(){return this.mode===w&&oo||this.mode===k&&lo||this.mode===E&&ve||this.mode===M&&ao||this.mode===lt&&(this._props.secondEnable?ho:co)||ro}dispatchChangeEvent(){const t=this.getDate(),e=this._props.format||this.getDefaultFormat();this.oldValue=O(t,e),this._props.value=this.oldValue,this.dispatchEvent(new CustomEvent("change",{detail:{date:t,value:this.oldValue}}))}dispatchWeekEvent(t,e,s){const r=this._props.format||this.getDefaultFormat(),a=P.parseDate(e),h=P.parseDate(s);this.oldValue=O(a,r),this._props.value=this.oldValue,this.dispatchEvent(new CustomEvent("change",{detail:{date:a,value:this.oldValue,startDate:a,endDate:h,weekNumber:t}}))}clear(){this.selectedYear=null,this.selectedMonth=null,this.selectedDate=null,this.selectedHour=null,this.selectedMinute=null,this.selectedSecond=null;var t=this._elements.content.querySelectorAll(".selected");t.forEach(e=>{n.removeClass(e,"selected")}),this.dispatchEvent(new CustomEvent("change",{detail:{date:null,value:""}}))}gotoToday(){const t=new Date;this.currentDate.setFullYear(t.getFullYear()),this.currentDate.setMonth(t.getMonth()),this.currentDate.setDate(t.getDate()),this.showView()}gotoPrev(){switch(this.currentView){case w:X(this.currentDate,w,-16),this.changeViewToYear();break;case k:X(this.currentDate,w,-1),this.changeViewToMonth();break;case E:X(this.currentDate,k,-1),this.changeViewToDate();break;case M:X(this.currentDate,k,-1),this.changeViewToWeek();break}}gotoNext(){switch(this.currentView){case w:X(this.currentDate,w,16),this.changeViewToYear();break;case k:X(this.currentDate,w,1),this.changeViewToMonth();break;case E:X(this.currentDate,k,1),this.changeViewToDate();break;case M:X(this.currentDate,k,1),this.changeViewToWeek();break}}setTitle(t){let e;switch(this.currentView){case w:t&&(this._elements.title.textContent=t);return;case k:e={year:"numeric"};break;case E:case M:e={year:"numeric",month:"long"};break;default:e={year:"numeric",month:"long",day:"numeric"}}this._elements.title.textContent=new Intl.DateTimeFormat("zh-CN",e).format(this.currentDate)}showView(){switch(this.currentView){case w:this.changeViewToYear();break;case k:this.changeViewToMonth();break;case E:this.setWeekTitle(),this.changeViewToDate();break;case M:this.setWeekTitle(),this.changeViewToWeek();break}}changeView(){switch(this.currentView){case E:case M:this.changeViewToMonth();break;case k:this.changeViewToYear();break;case w:if(this.mode===w)break;this.mode===k?this.changeViewToMonth():this.mode===M?this.changeViewToWeek():this.changeViewToDate();break}}_checkClass(){n.checkClass(this._elements.content,"clean-enable",this._props.cleanEnable),n.checkClass(this._elements.left,"hide",this.currentView===lt),n.checkClass(this._elements.yearContent,"hide",this.currentView!==w),n.checkClass(this._elements.monthContent,"hide",this.currentView!==k),n.checkClass(this._elements.dateContent,"hide",this.currentView!==E),n.checkClass(this._elements.right,"hide",![At,lt].includes(this.mode)),n.checkClass(this._elements.secondContent,"hide",!this._props.secondEnable),n.checkClass(this._elements.line,"hide",this.mode!==At),n.checkClass(this._elements.bottom,"hide",!this._props.cleanEnable),n.checkClass(this._elements.cleanTime,"hide",!this._props.cleanEnable||this.mode!==lt),n.checkClass(this._elements.today,"hide",!this._props.todayEnable||!this._isEnableDate(new Date))}_checkTimeContent(){this._checkHourContent(),this._checkMinuteContent(),this._checkSecondContent()}_checkStep(){this._props.secondEnable?this._checkSecondStep():this._checkMinuteStep()}_checkTimeSelected(){this._checkHourSelected(),this._checkMinuteSelected(),this._checkSecondSelected()}_checkViewClass(t){n.checkClass(this._elements.yearContent,"hide",t!==w),n.checkClass(this._elements.monthContent,"hide",t!==k),n.checkClass(this._elements.dateContent,"hide",![E,M].includes(t))}checkEnable(){switch(this.currentView){case w:this.checkYearTdsEnable();break;case k:this.checkMonthTdsEnable();break;case E:this.checkDateTdsEnable();break;case M:this.checkWeekTdsEnable();break}[At,lt].includes(this.mode)&&this._checkTimeContent()}changeViewToYear(t){this.currentView=w,this._checkViewClass(w),this.setYearContent(t)}setYearContent(){this.setYearEvent();const t=this._elements.yearContent.querySelectorAll("td"),e=new Date().getFullYear(),s=this.currentDate.getFullYear();let r=new Date(s,1,1);r.setFullYear(Math.floor(r.getFullYear()/t.length)*t.length);const a=r.getFullYear();r.setFullYear(r.getFullYear()+t.length-1);const h=r.getFullYear();this.setTitle(a+"-"+h),t.forEach((p,C)=>{const g=a+C;p.querySelector("span").textContent=g,p.dataset.year=g.toString(),p.querySelector("slot").setAttribute("name",g.toString()),this._checkYearTdClass(p,g,e)}),this.viewStatus={view:w,range:[`${a}`,`${h}`]},this.dispatchEvent(new CustomEvent("viewchange",{detail:{...this.viewStatus}}))}_checkYearTdClass(t,e,s){n.checkClass(t,"selected",this._isYearSelected(e)),n.checkClass(t,"today",s===e);const r=!this._needCheckEnable(w)||this._isEnableYear(e);return n.checkClass(t,"disabled",!r),r}_isYearSelected(t){return this.selectedYear===t}checkYearTdsEnable(t){this._needCheckEnable(w)&&this._elements.yearContent.querySelectorAll("td").forEach((s,r)=>{n.checkClass(s,"disabled",!this._isEnableYear(s.dataset.year))})}setYearEvent(){l(this,q2)||(this._elements.yearContent.querySelectorAll("td").forEach(t=>{t.addEventListener("click",()=>{let e=t.dataset.year;this._isEnableYear(e)&&(this.currentDate.setFullYear(e),this.mode===w?this._handleYearClick(t,parseInt(e)):this.changeViewToMonth())})}),u(this,q2,!0))}_handleYearClick(t,e){this.selectedYear=e,this.selectDate(t)}changeViewToMonth(){this.currentView=k,this.setTitle(),this._checkViewClass(k),this.setMonthContent()}setMonthContent(){const t=new Date().getFullYear(),e=new Date().getMonth(),s=this.currentDate.getFullYear();this._elements.monthContent.querySelectorAll("td").forEach((a,h)=>{a.querySelector("span").textContent=this._props.months[h].substr(0,2),a.dataset.year=s.toString(),a.dataset.month=(h+1).toString();const p=(h+1).toString().padStart(2,"0");a.querySelector("slot").setAttribute("name",`${s.toString()}-${p}`),this._checkMonthTdClass(a,s,h,t,e)}),this.viewStatus={view:k,year:s,range:[`${s}-01`,`${s}-12`]},this.dispatchEvent(new CustomEvent("viewchange",{detail:{...this.viewStatus}}))}_checkMonthTdClass(t,e,s,r,a){n.checkClass(t,"selected",this._isMonthSelected(e,s)),n.checkClass(t,"today",r===e&&a===s);const h=!this._needCheckEnable(k)||this._isEnableMonth(e+"-"+t.dataset.month);return n.checkClass(t,"disabled",!h),h}_isMonthSelected(t,e){return this.selectedYear===t&&this.selectedMonth===e}checkMonthTdsEnable(){this._needCheckEnable(k)&&this._elements.monthContent.querySelectorAll("td").forEach((e,s)=>{const r=e.dataset.year,a=e.dataset.month;n.checkClass(e,"disabled",!this._isEnableMonth(r+"-"+a))})}setMonthEvent(){l(this,U2)||(this._elements.monthContent.querySelectorAll("td").forEach(t=>{t.addEventListener("click",()=>{let e=t.dataset.year,s=t.dataset.month;this._isEnableMonth(e+"-"+s)&&(this.currentDate.setFullYear(e),this.currentDate.setMonth(parseInt(s)-1),this.mode===k?this._handleMonthClick(t,parseInt(e),parseInt(s)-1):this.mode===M?this.changeViewToWeek(e,s):this.changeViewToDate(e,s))})}),u(this,U2,!0))}_handleMonthClick(t,e,s){this.selectedYear=e,this.selectedMonth=s,this.selectDate(t)}changeViewToDate(){this.currentView=E,this.setTitle(),this._checkViewClass(E),this.setDateContent()}setWeekTitle(){this._elements.dateContent.querySelectorAll("th").forEach((e,s)=>{s===0?n.checkClass(e,"hide",this.mode!==M&&this.currentView!==M):e.textContent=this._props.daysTitles[(s-1+this._props.weekBegin)%7]})}setDateContent(){const t=I(new Date).getTime(),e=I(this.currentDate,!0);e.setDate(1);const s=ot(e),r=(7+s.getDay()+1-this._props.weekBegin)%7;s.setDate(s.getDate()-r),s>=e&&X(s,"day",-7);const a=O(X(ot(s),"date",1),ve);this._elements.dateContent.querySelectorAll("td").forEach((p,C)=>{if(C%8===0)return;s.setDate(s.getDate()+1),p.querySelector("span").textContent=s.getDate().toString();const g=O(s,ve);p.dataset.dateValue=g,p.querySelector("slot").setAttribute("name",g),this._checkDateTdClass(p,s,t)}),this.viewStatus={view:E,year:this.currentDate.getFullYear(),month:this.currentDate.getMonth(),range:[a,O(s,ve)]},this.dispatchEvent(new CustomEvent("viewchange",{detail:{...this.viewStatus}}))}_checkDateTdClass(t,e,s){n.checkClass(t,"selected",this._isDateSelected(e)),n.checkClass(t,"today",e.getTime()===s),n.checkClass(t,"other",e.getMonth()!==this.currentDate.getMonth());const r=!this._needCheckEnable(E)||this._isEnableDate(e);return n.checkClass(t,"disabled",!r),r}_isDateSelected(t){const e=this.getSelectedDate(),s=e?e.getTime():null;return t.getTime()===s}checkDateTdsEnable(){this._needCheckEnable(E)&&this._elements.dateContent.querySelectorAll("td").forEach((e,s)=>{if(s%8===0)return;const r=new Date(`${e.dataset.dateValue} 00:00:00`);n.checkClass(e,"disabled",!this._isEnableDate(r))})}setDateEvent(){l(this,X2)||(this._elements.dateContent.querySelectorAll("td").forEach((t,e)=>{e%8!==0&&t.addEventListener("click",()=>{let s=t.dataset.dateValue;if(this._isEnableDate(s)){const r=s.split("-");this._handleDateClick(t,parseInt(r[0]),parseInt(r[1])-1,parseInt(r[2]))}})}),u(this,X2,!0))}_handleDateClick(t,e,s,r){this.selectedYear=e,this.selectedMonth=s,this.selectedDate=r,this.mode!==E&&this._checkTimeContent(),this.selectDate(t)}changeViewToWeek(){this.currentView=M,this.setTitle(),this._checkViewClass(M),this.setWeekTitle(),this.setWeekContent()}setWeekContent(){const t=I(new Date).getTime(),e=I(this.currentDate,!0);e.setDate(1);const s=ot(e),r=(7+s.getDay()+1-this._props.weekBegin)%7;s.setDate(s.getDate()-r);const a=O(X(ot(s),"date",1),ve),h=[];this._elements.dateContent.querySelectorAll("td").forEach((C,g)=>{if(g%8===0){const f=ot(s);f.setDate(f.getDate()+1);const x=f.getFullYear(),y=Q3(f,this._props.weekBegin);C.querySelector("span").textContent=y,C.parentNode.dataset.weekNumber=y,C.parentNode.dataset.year=x,h.push(`${x} ${y}`),C.querySelector("slot").setAttribute("name",`week-${x}-${y}`),n.removeClass(C,"hide"),n.checkClass(C.parentNode,"week-row-selected",this._isWeekSelected(x,y))}else{s.setDate(s.getDate()+1),C.querySelector("span").textContent=s.getDate().toString();const f=O(s,ve);C.dataset.dateValue=f,C.querySelector("slot").setAttribute("name",f),this._checkWeekDateTdClass(C,s,t)}}),this.viewStatus={view:this.mode===M?M:E,year:this.currentDate.getFullYear(),month:this.currentDate.getMonth(),weeks:h,range:[a,O(s,ve)]},this.dispatchEvent(new CustomEvent("viewchange",{detail:{...this.viewStatus}}))}_checkWeekDateTdClass(t,e,s){n.checkClass(t,"today",e.getTime()===s),n.checkClass(t,"other",e.getMonth()!==this.currentDate.getMonth());const r=!this._needCheckEnable(M)||this._isEnableDate(e);n.checkClass(t,"disabled",!r)}_isWeekSelected(t,e){return t===this.selectedYear&&e===this.selectedWeekNumber}checkWeekTdsEnable(){this._needCheckEnable(M)&&this._elements.dateContent.querySelectorAll("td").forEach((e,s)=>{s%8!==0&&n.checkClass(e,"disabled",!this._isEnableDate(date))})}setWeekEvent(){l(this,Q2)||(this._elements.dateContent.querySelectorAll("tr").forEach((t,e)=>{t.addEventListener("mouseenter",()=>{if(l(this,le)){const s=t.querySelector("td:nth-child(2)").dataset.dateValue,r=t.querySelector("td:last-child").dataset.dateValue;this._isEnableWeek(s,r)&&n.addClass(t,"week-row-over")}else n.addClass(t,"week-row-over")}),t.addEventListener("mouseleave",()=>{n.removeClass(t,"week-row-over")}),t.addEventListener("click",()=>{const s=t.querySelector("td:nth-child(2)").dataset.dateValue,r=t.querySelector("td:last-child").dataset.dateValue;l(this,le)?this._isEnableWeek(s,r)&&this._handleWeekClick(t,s,r):this._handleWeekClick(t,s,r)})}),u(this,Q2,!0))}_handleWeekClick(t,e,s){const r=t.parentNode.querySelector("tr.week-row-selected");r&&n.removeClass(r,"week-row-selected"),n.addClass(t,"week-row-selected");const[a,h,p]=e.split("-");this.selectedYear=parseInt(a),this.selectedMonth=parseInt(h)-1,this.selectedDate=parseInt(p);const C=t.dataset.weekNumber;this.selectedWeekNumber=parseInt(C),this.dispatchWeekEvent(C,e,s)}_checkHourContent(){this._elements.hourContent.querySelectorAll("li").forEach((t,e)=>{const s=this._isEnableHour(e);n.checkClass(t,"disabled",!s),!s&&this.selectedHour===e&&(this.selectedHour=null),n.checkClass(t,"selected",s&&this.selectedHour===e)})}_checkHourSelected(){this._elements.hourContent.querySelectorAll("li").forEach((t,e)=>{n.checkClass(t,"selected",this.selectedHour===e),this.selectedHour===e&&setTimeout(()=>{e!==0&&u(this,c2,!0),t.scrollIntoView({block:"nearest"})},100)})}setHourEvent(){if(l(this,G2))return;const t=()=>{l(this,c2)||this._fixTimeNode(this._elements.hourContent),u(this,c2,!1)};this._elements.hourContent.addEventListener("scroll",()=>{$(t,100,this)}),this._elements.hourContent.querySelectorAll("li").forEach((e,s)=>{const r=s.toString().padStart(2,"0");e.dataset.hour=r,e.addEventListener("click",()=>{this._isEnableHour(r)&&(this.currentDate.setHours(r),this.selectedHour=parseInt(r),this._checkMinuteContent(),this._checkSecondContent(),this.selectTime(e,oi))})}),u(this,G2,!0)}_checkMinuteContent(){this._elements.minuteContent.querySelectorAll("li").forEach((t,e)=>{const s=this._isEnableMinute(e);n.checkClass(t,"disabled",!s),!s&&this.selectedMinute===e&&(this.selectedMinute=null),n.checkClass(t,"selected",s&&this.selectedMinute===e)})}_checkMinuteStep(){const t=this._props.step||1;this._elements.minuteContent.querySelectorAll("li").forEach((e,s)=>{n.checkClass(e,"hide",s%t!==0)})}_checkMinuteSelected(){this._elements.minuteContent.querySelectorAll("li").forEach((t,e)=>{n.checkClass(t,"selected",this.selectedMinute===e),this.selectedMinute===e&&setTimeout(()=>{e!==0&&u(this,p2,!0),t.scrollIntoView({block:"nearest"})},100)})}setMinuteEvent(){if(l(this,K2))return;const t=()=>{l(this,p2)||this._fixTimeNode(this._elements.minuteContent),u(this,p2,!1)};this._elements.minuteContent.addEventListener("scroll",()=>{$(t,100,this)}),this._elements.minuteContent.querySelectorAll("li").forEach((e,s)=>{const r=s.toString().padStart(2,"0");e.dataset.minute=r,e.addEventListener("click",()=>{this._isEnableMinute(r)&&(this.currentDate.setMinutes(r),this.selectedMinute=parseInt(r),this._checkSecondContent(),this.selectTime(e,li))})}),u(this,K2,!0)}_checkSecondContent(){this._props.secondEnable&&this._elements.secondContent.querySelectorAll("li").forEach((t,e)=>{const s=this._isEnableSecond(e);n.checkClass(t,"disabled",!s),!s&&this.selectedSecond===e&&(this.selectedSecond=null),n.checkClass(t,"selected",s&&this.selectedSecond===e)})}_checkSecondStep(){const t=this._props.step||1;this._elements.secondContent.querySelectorAll("li").forEach((e,s)=>{n.checkClass(e,"hide",s%t!==0)})}_checkSecondSelected(){this._props.secondEnable&&this._elements.secondContent.querySelectorAll("li").forEach((t,e)=>{n.checkClass(t,"selected",this.selectedSecond===e),this.selectedSecond===e&&setTimeout(()=>{e!==0&&u(this,u2,!0),t.scrollIntoView({block:"nearest"})},100)})}setSecondEvent(){if(l(this,J2))return;const t=()=>{l(this,u2)||this._fixTimeNode(this._elements.secondContent),u(this,u2,!1)};this._elements.secondContent.addEventListener("scroll",()=>{$(t,100,this)}),this._elements.secondContent.querySelectorAll("li").forEach((e,s)=>{const r=s.toString().padStart(2,"0");e.dataset.second=r,e.addEventListener("click",()=>{this._isEnableSecond(r)&&(this.currentDate.setSeconds(r),this.selectedSecond=parseInt(r),this.selectTime(e,ai))})}),u(this,J2,!0)}_fixTimeNode(t){const e=t.querySelector("li"),s=n.getSize(e).y;t.scrollTop%s<s/2?t.scrollTop=Math.floor(t.scrollTop/s)*s:t.scrollTop=Math.ceil(t.scrollTop/s)*s}_needCheckEnable(t){switch(t){case w:return!!l(this,Le)||!!l(this,m2);case k:return!!l(this,xe)||!!l(this,ye);case E:return!!l(this,Vt)||!!l(this,ae);case M:return!!l(this,Vt)||!!l(this,ae);case oi:return!!l(this,Ht)||!!l(this,we);case li:return!!l(this,Bt)||!!l(this,ke);case ai:return!!l(this,Yt)||!!l(this,Ee)}}setRange(){u(this,Le,null),u(this,xe,null),u(this,Vt,null),u(this,le,null),u(this,Ht,null),u(this,Bt,null),u(this,Yt,null);const{datetimeRange:t,dateRange:e,timeRange:s}=this._props;t&&t.length?(u(this,h2,typeof t=="string"?i2(`return ${t}`):t),this._setDatetimeRange()):(e&&e.length&&(u(this,t1,typeof e=="string"?i2(`return ${e}`):e),this._setDateRange()),s&&s.length&&(u(this,e1,typeof s=="string"?i2(`return ${s}`):s),this._setTimeRange()))}_setDatetimeRange(){const t=l(this,h2),e=v(t[0])!=="array"?[t]:t;!e[0][0]&&!e[0][1]||(this._setDateRange(l(this,h2)),u(this,Pt,e.map(s=>[P.parseDate(s[0]),P.parseDate(s[1])])),u(this,It,e.map(s=>[I(s[0],!0),I(s[1],!0)])),u(this,Ht,s=>{if(!s)return[0,23];const r=I(s,!0),a=[];for(let h=0;h<l(this,It).length;h++){const p=l(this,It)[h],C=c(this,Wt,t2).call(this,p[0],r),g=c(this,Wt,t2).call(this,r,p[1]);(C||g)&&a.push([C?l(this,Pt)[h][0].getHours():0,g?l(this,Pt)[h][1].getHours():23])}return a.length?P.RangeArrayUtils.union(a):[0,23]}),u(this,Bt,(s,r)=>{if(!s)return[0,59];const a=I(s,!0),h=[];for(let p=0;p<l(this,It).length;p++){const C=l(this,It)[p],g=l(this,Pt)[p],f=c(this,Wt,t2).call(this,C[0],a)&&r===g[0].getHours(),x=c(this,Wt,t2).call(this,a,C[1])&&r===g[1].getHours();(f||x)&&h.push([f?g[0].getMinutes():0,x?g[1].getMinutes():59])}return h.length?P.RangeArrayUtils.union(h):[0,59]}),u(this,Yt,(s,r,a)=>{if(!s)return[0,59];const h=I(s,!0),p=[];for(let C=0;C<l(this,It).length;C++){const g=l(this,It)[C],f=l(this,Pt)[C],x=c(this,Wt,t2).call(this,g[0],h)&&r===f[0].getHours()&&a===f[0].getMinutes(),y=c(this,Wt,t2).call(this,h,g[1])&&r===f[1].getHours()&&a===f[1].getMinutes();(x||y)&&p.push([x?f[0].getSeconds():0,y?f[1].getSeconds():59])}return p.length?P.RangeArrayUtils.union(p):[0,59]}))}_setDateRange(t=l(this,t1)){const e=v(t[0])!=="array"?[t]:t;!e[0][0]&&!e[0][1]||(u(this,mt,e.map(s=>[I(s[0],!0),X0(s[1],!0)])),u(this,Le,s=>{const r=new Date(s+"-01-01"),a=new Date(s+"-12-31"),h=this._getCurrentDateRange(l(this,mt))||l(this,mt);for(let p=0;p<h.length;p++){const C=h[p];if(P.RangeArrayUtils.isIntersection(C,[r,a]))return!0}return!1}),u(this,xe,s=>{const r=new Date(s+"-01"),a=U0(...s.split("-")),h=this._getCurrentDateRange(l(this,mt))||l(this,mt);for(let p=0;p<h.length;p++){const C=h[p];if(P.RangeArrayUtils.isIntersection(C,[r,a]))return!0}return!1}),u(this,Vt,s=>{const r=I(s,!0),a=this._getCurrentDateRange(l(this,mt))||l(this,mt);for(let h=0;h<a.length;h++){const p=a[h];if(!p[0]&&c(this,Me,es).call(this,r,p[1])||!p[1]&&c(this,Me,es).call(this,p[0],r)||c(this,Me,es).call(this,p[0],r)&&c(this,Me,es).call(this,r,p[1]))return!0}return!1}),u(this,le,(s,r)=>{do{if(l(this,Vt).call(this,s))return!0;s=s.setDate(s.getDate()+1)}while(s<=r);return!1}))}_setTimeRange(){const t=l(this,e1),e=v(t[0])!=="array"?[t]:t;!e[0][0]&&!e[0][1]||(u(this,Dt,e.map(s=>[s[0]?new Date("2020-01-01 "+s[0]):null,s[1]?new Date("2020-01-01 "+s[1]):null])),u(this,Ht,s=>{if(l(this,_e))return l(this,_e);const r=[];for(let a=0;a<l(this,Dt).length;a++){const h=l(this,Dt)[a];r.push([h[0]?h[0].getHours():0,h[1]?h[1].getHours():23])}return u(this,_e,P.RangeArrayUtils.union(r)),l(this,_e)}),u(this,Bt,(s,r)=>{const a=[];for(let h=0;h<l(this,Dt).length;h++){const p=l(this,Dt)[h],C=p[0]&&r===p[0].getHours(),g=p[1]&&r===p[1].getHours();(C||g)&&a.push([C?p[0].getMinutes():0,g?p[1].getMinutes():59])}return a.length?P.RangeArrayUtils.union(a):[0,59]}),u(this,Yt,(s,r,a)=>{const h=[];for(let p=0;p<l(this,Dt).length;p++){const C=l(this,Dt)[p],g=C[0]&&r===C[0].getHours()&&a===C[0].getMinutes(),f=C[1]&&r===C[1].getHours()&&a===C[1].getMinutes();(g||f)&&h.push([g?C[0].getSeconds():0,f?C[1].getSeconds():59])}return h.length?P.RangeArrayUtils.union(h):[0,59]}))}_getCurrentDateRange(){return null}_isEnableYear(t){return c(this,st,bt).call(this,l(this,Le),t)&&c(this,st,bt).call(this,l(this,m2),t)}_isEnableMonth(t){if(!c(this,st,bt).call(this,l(this,xe),t))return!1;if(l(this,ye)){const[e,s]=t.split("-");return!e||!s?!1:(t=e+"-"+s.padStart(2,"0"),c(this,st,bt).call(this,l(this,ye),t))}return!0}_isEnableDate(t){return c(this,st,bt).call(this,l(this,Vt),P.parseDate(t))&&c(this,st,bt).call(this,l(this,ae),I(t,!0))}_isEnableWeek(t,e){return c(this,st,bt).call(this,l(this,le),P.parseDate(t),P.parseDate(e))&&c(this,st,bt).call(this,l(this,s1),I(t,!0),I(e))}_isEnableHour(t,e,s){if(t=parseInt(t),!(!l(this,Ht)||c(this,js,j4).call(this,t,e,s)))return!1;if(l(this,we)){const a=I(e||this.getSelectedDate(s)||this.currentDate,!0);return a.setHours(t||0),c(this,st,bt).call(this,l(this,we),a,t||0,s)}return!0}_isEnableMinute(t,e=this.selectedHour,s,r){if(t=parseInt(t),!(!l(this,Bt)||c(this,Zs,Z4).call(this,t,e,s,r)))return!1;if(l(this,ke)){const h=I(s||this.getSelectedDate(r)||this.currentDate,!0);return h.setHours(e||0,t||0),c(this,st,bt).call(this,l(this,ke),h,e||0,t||0,r)}return!0}_isEnableSecond(t,e=this.selectedMinute,s=this.selectedHour,r,a){if(t=parseInt(t),!(!l(this,Yt)||c(this,Vs,V4).call(this,t,e,s,r,a)))return!1;if(l(this,Ee)){const p=I(r||this.getSelectedDate(a)||this.currentDate,!0);return p.setHours(s||0,e||0,t||0),c(this,st,bt).call(this,l(this,Ee),p,s||0,e||0,t||0,a)}return!0}};Dt=new WeakMap,_e=new WeakMap,mt=new WeakMap,Pt=new WeakMap,It=new WeakMap,q2=new WeakMap,U2=new WeakMap,X2=new WeakMap,G2=new WeakMap,K2=new WeakMap,J2=new WeakMap,Q2=new WeakMap,le=new WeakMap,Le=new WeakMap,xe=new WeakMap,Vt=new WeakMap,Ht=new WeakMap,Bt=new WeakMap,Yt=new WeakMap,h2=new WeakMap,t1=new WeakMap,e1=new WeakMap,c2=new WeakMap,p2=new WeakMap,u2=new WeakMap,s1=new WeakMap,m2=new WeakMap,ye=new WeakMap,ae=new WeakMap,we=new WeakMap,ke=new WeakMap,Ee=new WeakMap,Xi=new WeakMap,Fs=new WeakMap,Gi=new WeakSet,B8=function(t,e){return t>e||t-e===0},Me=new WeakSet,es=function(t,e){return t<e||t-e===0},Wt=new WeakSet,t2=function(t,e){return t-e===0},st=new WeakSet,bt=function(t,...e){return!t||v(t)!=="function"||t(...e)},$s=new WeakSet,$4=function(t){const e=l(this,Ht);return e&&v(e)==="function"?e(P.parseDate(t)):[0,23]},js=new WeakSet,j4=function(t,e,s){const r=c(this,$s,$4).call(this,e||this.getSelectedDate(s));if(!r||!r.length||r[0]===0&&r[1]===23)return!0;if(v(r[0])==="array")for(let a=0;a<r.length;a++){const h=r[a];if(h[0]<=t&&t<=h[1])return!0}else if(r[0]<=t&&t<=r[1])return!0;return!1},zs=new WeakSet,z4=function(t,e){const s=l(this,Bt);return s&&v(s)==="function"?s(P.parseDate(t),e):[0,59]},Zs=new WeakSet,Z4=function(t,e,s,r){const a=c(this,zs,z4).call(this,s||this.getSelectedDate(r),e);if(!a||!a.length||a[0]===0&&a[1]===59)return!0;if(v(a[0])==="array")for(let h=0;h<a.length;h++){const p=a[h];if(p[0]<=t&&t<=p[1])return!0}else if(a[0]<=t&&t<=a[1])return!0;return!1},Rs=new WeakSet,R4=function(t,e,s){const r=l(this,Yt);return r&&v(r)==="function"?r(P.parseDate(t),e,s):[0,59]},Vs=new WeakSet,V4=function(t,e,s,r,a){const h=c(this,Rs,R4).call(this,r||this.getSelectedDate(a),s,e);if(!h||!h.length||h[0]===0&&h[1]===59)return!0;if(v(h[0])==="array")for(let p=0;p<h.length;p++){const C=h[p];if(C[0]<=t&&t<=C[1])return!0}else if(h[0]<=t&&t<=h[1])return!0;return!1},m(P,"prop",{view:E,mode:At,baseDate:null,secondEnable:!0,cleanEnable:!1,todayEnable:!0,datetimeRange:"",dateRange:"",timeRange:"",value:"",format:"",weekBegin:1,months:["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],daysTitles:["日","一","二","三","四","五","六"],clean:"清除",year:"年",month:"月",date:"日",hour:"时",minute:"分",second:"秒",week:"周",step:1});let ie=P;ie.RangeArrayUtils={complementary(o,i,t){if(!o)return o;const e=this.getRangeObject(o);if(!i||i.length===0)return this.parse([e],t);const s=this.union(i),r={};if(s[0][0]>e.start)r.start=e.start;else if(e.end>s[0][1])r.start=s[0][1],s.shift();else return[];const a=[];for(;s.length>0;){if(s[0][0]>=e.end)return r.end=e.end,a.push({...r}),this.parse(a,t);if(e.end<=s[0][1])return r.end=s[0][0],a.push({...r}),this.parse(a,t);r.end=s[0][0],a.push({...r}),r.start=s[0][1],s.shift()}return r.end=e.end,a.push({...r}),this.parse(a,t)},union(o,i){if(!o||o.length===0)return o;const t=o.map(a=>this.getRangeObject(a)).sort((a,h)=>a.start-h.start),e=[];let s=t.shift(),r;for(;t.length>0;)r=t.shift(),this.isIntersection(s,r)?s.end=Math.max(s.end,r.end):(e.push({...s}),s=r);return r?this.isIntersection(s,r)?(s.end=Math.max(s.end,r.end),e.push({...s})):e.push({...s}):e.push({...s}),this.parse(e,i)},intersection(o,i){if(!o||o.length===0)return o;if(o.length===1)return o[1];const t=o.map(s=>this.getRangeObject(s)).sort((s,r)=>s.start-r.start),e=t.shift();for(;t.length>0;){const s=t.shift();if(this.isIntersection(e,s))e.start=s.start,e.end=Math.min(e.end,s.end);else return[]}return i&&i===E?[new Date(e.start),new Date(e.end)]:[e.start,e.end]},isIntersection(o,i){const t=v(o)==="object"?o:this.getRangeObject(o),e=v(i)==="object"?i:this.getRangeObject(i);return!(t.start>e.end||e.start>t.end)},parse(o,i){return o.map(t=>i&&i===E?[new Date(t.start),new Date(t.end)]:[t.start,t.end])},getRangeObject(o){if(o[0]&&o[1])return{start:Math.min(o[0],o[1]),end:Math.max(o[0],o[1])};if(!o[0]&&o[1])return{start:-1/0,end:o[1]};if(o[0]&&!o[1])return{start:o[0],end:1/0}}};const po=`<div class="content">\r
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
</div>`,uo=`.content{display:flex;flex-direction:column;height:100%}.top{background-color:var(--oo-color-gray2);height:2.5em;display:flex;align-items:center;line-height:2.5em}.middle{flex:1}.title{cursor:pointer;color:var(--oo-color-text);font-weight:700;flex:1;padding-left:.8em}.title:hover{color:var(--oo-color-main)}.date{font-size:1.25em;cursor:pointer}.date:hover{color:var(--oo-color-main)}.prev{font-size:1.25em;cursor:pointer;padding-right:.5em;height:2.5em;line-height:2.5em;width:2.5em;text-align:right}.prev:hover{color:var(--oo-color-main)}.next{font-size:1.25em;cursor:pointer;padding-left:.5em;height:2.5em;line-height:2.5em;width:2.5em}.next:hover{color:var(--oo-color-main)}table{width:100%;border-top:1px solid var(--oo-color-gray-d1);border-left:1px solid var(--oo-color-gray-d1);table-layout:fixed}th{color:var(--oo-color-text);font-weight:400;background-color:#f7f7f7;line-height:2em;height:2em;border-right:1px solid var(--oo-color-gray-d1);border-bottom:1px solid var(--oo-color-gray-d1)}td{cursor:pointer;color:var(--oo-color-text);border-right:1px solid var(--oo-color-gray-d1);border-bottom:1px solid var(--oo-color-gray-d1);vertical-align:top}td.other{color:var(--oo-color-text3)}td>div:first-child{min-height:8em;padding:.25em}td div.cellTitle{display:flex;justify-content:space-between}td span:first-child{display:inline-block;text-align:center;border:1px solid transparent;height:1.5em;line-height:1.5em;width:1.5em;border-radius:1.5em}td.today span{color:var(--oo-color-text-white);background-color:var(--oo-color-main)}.disabled{color:var(--oo-color-text4)!important}.disabled span{border-color:transparent!important;background-color:transparent!important}.hide{display:none}
`;customElements.get("oo-calendar")||customElements.define("oo-calendar",ie);const mo=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    /*display: contents;*/\r
    padding: var(--oo-menu-padding);\r
}\r
`,Co=`
<div class="content">
    <slot name="items"></slot>
</div>
`,Hs=class Hs extends N{constructor(){super();m(this,"_setPropMap",{css:()=>{this.useCss(this._props.css)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},isShow:()=>{this._props.isShow&&window.setTimeout(()=>{this.show()},10)}});d(this,de,null);d(this,he,null);d(this,ce,null)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_init(t,e,s,r){this._initialize(t,e||Hs,s||Co,r||mo)}_setEvent(){super._setEvent(),this.shadowRoot.addEventListener("mousedown",t=>{t.stopPropagation()})}show(t,e){!l(this,de)&&!l(this,he)&&(u(this,de,Object.assign({},{scale:0},t)),u(this,he,Object.assign({},{scale:1},e)),n.setStyles(this,{scale:0}),n.addClass(this,"show"),window.setTimeout(()=>{n.addClass(this,"transition"),n.setStyles(this,{scale:1})},10),window.setTimeout(()=>{this.dispatchEvent(new Event("show"))},210),this._addHideEvent(),this.isHide=!1)}_addHideEvent(){l(this,ce)||u(this,ce,()=>{this.hide()}),document.addEventListener("mousedown",l(this,ce))}hide(){l(this,de)&&l(this,he)&&!this.isHide&&(n.setStyles(this,l(this,de)),window.setTimeout(()=>{n.removeClass(this,"transition"),n.removeClass(this,"show"),n.setStyles(this,l(this,he)??{}),u(this,he,null),u(this,de,null),this._afterHide(),this.dispatchEvent(new Event("hide"))},200),this._removeHideEvent(),this.isHide=!0)}_afterHide(){}_removeHideEvent(){l(this,ce)&&document.removeEventListener("mousedown",l(this,ce))}};de=new WeakMap,he=new WeakMap,ce=new WeakMap,m(Hs,"prop",{css:"",cssLink:"",skin:"",isShow:!1});let xs=Hs;class di extends xs{constructor(){super(),this._init("oo-menu",di)}}customElements.get("oo-menu")||customElements.define("oo-menu",di);const go=`* {\r
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
`,fo=`
<div class="menuItem content enabled">
    <slot name="left"></slot>
    <slot name="item"></slot>
    <slot name="right"></slot>
</div>
`,Bs=class Bs extends N{constructor(){super();m(this,"_elements",{});m(this,"_setPropMap",{css:()=>{this.useCss(this._props.css)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},label:()=>{switch(this._props.type){case"command":this._elements.label&&(this._elements.label.textContent=this._props.label);break;case"checkbox":case"radio":this._elements.input&&(this._elements.input.text=this._props.label);break}},disabled:()=>{switch(n.removeClass(this._content,"disabled"),n.addClass(this._content,"enabled"),this._props.type){case"command":this._props.disabled&&(n.removeClass(this._content,"enabled"),n.addClass(this._content,"disabled"));break;case"checkbox":case"radio":this._elements.input.disabled=this._props.disabled,this._props.disabled&&n.removeClass(this._content,"enabled");break}},icon:t=>{this._elements.icon&&(n.removeClass(this._elements.icon,`ooicon-${t}`),this._props.icon&&n.addClass(this._elements.icon,`ooicon-${this._props.icon}`))},radiogroup:t=>{this._elements.radio&&(this._elements.radio.name=this._props.radiogroup)},type:t=>{t!==null&&t!==this._props.type&&this._renderItem()}});this._initialize("oo-menu-item",Bs,fo,go)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_setEvent(){super._setEvent(),this.addEventListener("click",()=>{if(this.type==="command"){const t=n.getParent(this,"oo-menu");t&&t.hide()}})}_connected(){this.getAttribute("type")||this.setAttribute("type","command"),n.getParent(this,"oo-menu")&&this.setAttribute("slot","items"),this._renderItem()}_renderItem(){switch(n.empty(this),this._elements.icon=null,this._elements.label=null,this._elements.input=null,this._elements.hr=null,this.checkbox=null,this.radio=null,n.addClass(this._content,"enabled"),this._props.type){case"command":this._elements.icon=n("div.icon",{slot:"item"}),this._elements.label=n("div.label",{text:this._props.label,slot:"item"}),this._props.icon&&n.addClass(this._elements.icon,`ooicon-${this._props.icon}`),this.append(this._elements.icon),this.append(this._elements.label);break;case"checkbox":this._elements.input=n("oo-checkbox",{slot:"item"}),this.append(this._elements.input),this._elements.input.text=this._props.label,this.checkbox=this._elements.input;break;case"radio":this._elements.input=n("oo-radio",{slot:"item"}),this.append(this._elements.input),this._elements.input.text=this._props.label,this._elements.input.name=this._props.radiogroup,this.radio=this._elements.input;break;case"hr":this._elements.hr=n("hr",{slot:"item"}),this.append(this._elements.hr),n.removeClass(this._content,"enabled");break}}};m(Bs,"prop",{css:"",cssLink:"",skin:"",icon:"",label:"",type:"command",disabled:!1,checked:!1,radiogroup:""});let hi=Bs;customElements.get("oo-menu-item")||customElements.define("oo-menu-item",hi);class d4{constructor(i,t){d(this,Ys);d(this,Ws);d(this,qs);d(this,Us);m(this,"options",{css:"",cssLink:"",skin:""});this.options=Object.assign(this.options,t),this.target=i,this.container=this.options.container||n.getOffsetParent(i),this.area=this.options.area;const e=this.options.event||"click";this.target.addEventListener(e,()=>{this.show()}),this.target.addEventListener("mousedown",s=>{})}show(){this.menu||c(this,Ys,H4).call(this),c(this,Us,W4).call(this),this.menu.show()}hide(){this.menu.hide()}destroy(){this.menu.remove()}}Ys=new WeakSet,H4=function(){var i;this.menu=n("oo-menu",(i=this.options)==null?void 0:i.attr),this.container.append(this.menu),this.options.items&&this.options.items.length&&c(this,Ws,B4).call(this),this.options.class&&n.addClass(this.menu,this.options.class)},Ws=new WeakSet,B4=function(){this.options.items.forEach(i=>{c(this,qs,Y4).call(this,i)})},qs=new WeakSet,Y4=function(i){const t=document.createElement("oo-menu-item");this.menu.append(t),i==="-"?t.setAttribute("type","hr"):(Object.keys(i).forEach(e=>{e!=="command"?t.setAttribute(b(e),i[e]):t.addEventListener("click",i.command)}),t.addEventListener("click",e=>{this.menu.dispatchEvent(new CustomEvent("command",{detail:i}))}))},Us=new WeakSet,W4=function(){const i=n.getSize(this.target),t=n.getPosition(this.target,this.container),e=n.getSize(this.menu);let s=t.y+i.y,r=t.x,a="left",h="top";if(this.area){const p=n.getSize(this.area),C=n.getPosition(this.container,this.area);C.x+r+e.x>p.x&&(r=r-e.x+i.x,a="right"),C.y+s+e.y>p.y&&(s=s-e.y-i.y,h="bottom")}n.setStyles(this.menu,{top:s+"px",left:r+"px","transform-origin":`${a} ${h}`})};class h4 extends d4{constructor(t,e){super(t,e);d(this,Xs);d(this,Gs);d(this,Ks);d(this,Se,null);d(this,Ki,null);d(this,Ji,null);m(this,"options",{css:"",cssLink:"",skin:"",view:"date",mode:"datetime",baseDate:null,secondEnable:!0,cleanEnable:!1,todayEnable:!0,datetimeRange:"",dateRange:"",timeRange:"",format:"",weekBegin:1,value:""});this.options=Object.assign(this.options,e)}reset(t){if(this.options=Object.assign(this.options,t),this.calendar)for(let e in this.options)v(this.options[e])!=="null"&&this.calendar.setAttribute(b(e),this.options[e])}show(){this.menu||c(this,Xs,q4).call(this),c(this,Ks,X4).call(this),this.menu.show()}}Se=new WeakMap,Ki=new WeakMap,Ji=new WeakMap,Xs=new WeakSet,q4=function(){var t;this.menu=n("oo-menu",(t=this.options)==null?void 0:t.attr),this.container.append(this.menu),u(this,Se,n("div",{slot:"items",styles:{height:"100%"}})),this.menu.append(l(this,Se)),c(this,Gs,U4).call(this),this.options.class&&n.addClass(this.menu,this.options.class)},Gs=new WeakSet,U4=function(){if(!this.calendar){let t="<oo-calendar";for(let e in this.options)v(this.options[e])!=="null"&&(t+=` ${b(e)}='${this.options[e]}'`);t+="></oo-calendar>",n.set(l(this,Se),"html",t),this.calendar=l(this,Se).firstElementChild,this.calendar.addEventListener("change",e=>{this.target.dispatchEvent(new CustomEvent("change",e))})}},Ks=new WeakSet,X4=function(){const t=n.getSize(this.target),e=n.getPosition(this.target,this.container),s=n.getSize(this.menu);let r=e.y+t.y,a=e.x,h="left",p="top";if(this.area){const C=n.getSize(this.area),g=n.getPosition(this.container,this.area);g.x+a+s.x>C.x&&(a=a-s.x+t.x,h="right"),g.y+r+s.y>C.y&&(r=r-s.y-t.y,p="bottom")}n.setStyles(this.menu,{top:r+"px",left:a+"px","transform-origin":`${h} ${p}`})};function c4(o,i){return new h4(o,i)}const n1=class n1 extends N{constructor(){super();m(this,"_elements",{content:null,top:null,prev:null,next:null,date:null,dateContent:null});d(this,Qi,{});d(this,G,null);d(this,C2,null);m(this,"_setPropMap",{skin:()=>{this._useSkin(this._props.skin)},baseDate:()=>{this.setCurrentDate(),this.setDateContent()},weekBegin:()=>{this.setWeekTitle(),this.setDateContent()},$default:t=>{}});this._initialize("oo-calendar-view",n1,po,uo)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}static parseDate(t,e){return t?v(t)==="date"?e?new Date(t.getTime()):t:new Date(t):null}_render(){this.setCurrentDate()}_connected(){this.setWeekTitle(),this.setDateContent()}_setEvent(){this._elements.prev.addEventListener("click",t=>{X(l(this,G),"month",-1),this.setDateContent(),this.changeDate()}),this._elements.next.addEventListener("click",t=>{X(l(this,G),"month",1),this.setDateContent(),this.changeDate()}),this._elements.date.addEventListener("change",t=>{u(this,G,t.detail.date),this._elements.date.textContent=O(l(this,G),this._props.format),this.setDateContent()})}setCurrentDate(){u(this,G,this._props.baseDate?n1.parseDate(this._props.baseDate,!0):new Date),this.changeDate()}changeDate(){this._elements.date.textContent=O(l(this,G),this._props.format),l(this,C2)?l(this,C2).reset({value:O(l(this,G),"YYYY-MM")}):u(this,C2,c4(this._elements.date,{mode:"month",format:this._props.format,container:document.body,value:O(l(this,G),"YYYY-MM")})),this.dispatchEvent(new CustomEvent("change",{detail:{date:l(this,G),value:O(l(this,G),"YYYY-MM")}}))}setWeekTitle(){const t=this._elements.dateContent.querySelectorAll("th");this._props.daysTitles.forEach((e,s)=>{t[(7+s-this._props.weekBegin)%7].textContent=e})}setDateContent(){const t=I(new Date).getTime(),e=I(l(this,G),!0);e.setDate(1);const s=this._elements.dateContent.querySelectorAll("td"),r=new Date(e.getTime());r.setDate(r.getDate()-1);const a=(7+r.getDay()+1-this._props.weekBegin)%7,h=(p,C)=>{p.querySelectorAll("slot").forEach(y=>y.remove());const g=O(C,"YYYY-MM-DD"),f=n("slot",{name:`title-${g}`});p.querySelector("div.cellTitle").appendChild(f);const x=n("slot",{name:`content-${g}`});p.querySelector("div.cellContent").appendChild(x),p.querySelector("span:first-child").textContent=O(C,"D"),p.dataset.dateValue=g,n.checkClass(p,"today",C.getTime()===t),n.checkClass(p,"other",C.getMonth()!==l(this,G).getMonth())};for(let p=a-1;p>=0;p--)h(s[p],r),r.setDate(r.getDate()-1);for(let p=a;p<s.length;p++)h(s[p],e),e.setDate(e.getDate()+1)}};Qi=new WeakMap,G=new WeakMap,C2=new WeakMap,m(n1,"prop",{baseDate:"",weekBegin:1,format:"YYYY年MM月",months:["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],daysTitles:["周日","周一","周二","周三","周四","周五","周六"]});let ci=n1;const vo=`<div class="content">\r
    <div class="left">\r
        <div class="top">\r
            <div class="top-left">\r
                <slot name="title"></slot>\r
            </div>\r
            <div class="title"></div>\r
            <div class="today">今天</div>\r
            <div class="top-right">\r
                <div class="prev ooicon-arrow_back"></div>\r
                <div class="next ooicon-arrow_forward"></div>\r
            </div>\r
        </div>\r
        <div class="middle">\r
            <table border="0" cellpadding="0"  cellspacing="1" class="yearContent">\r
                <tbody>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                </tbody>\r
            </table>\r
            <table border="0" cellpadding="0" cellspacing="1" class="monthContent">\r
                <tbody>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
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
                    <td class="week hide"><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                <tr>\r
                    <td class="week hide"><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                    <td><div><span></span><slot></slot></div></td>\r
                </tr>\r
                </tbody>\r
            </table>\r
        </div>\r
        <div class="bottom">\r
            <div><div class="clean">清除</div></div>\r
        </div>\r
    </div>\r
    <div class="line"></div>\r
    <div class="right">\r
        <div class="time">\r
            <div class="timeTitle">开始时间</div>\r
            <div class="timeContent">\r
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
        </div>\r
        <div class="timeLine"></div>\r
        <div class="endTime">\r
            <div class="endTimeTitle">结束时间</div>\r
            <div class="endTimeContent">\r
                <ul class="endHourContent">\r
                    <li><span>00</span></li><li><span>01</span></li><li><span>02</span></li><li><span>03</span></li><li><span>04</span></li><li><span>05</span></li>\r
                    <li><span>06</span></li><li><span>07</span></li><li><span>08</span></li><li><span>09</span></li><li><span>10</span></li><li><span>11</span></li>\r
                    <li><span>12</span></li><li><span>13</span></li><li><span>14</span></li><li><span>15</span></li><li><span>16</span></li><li><span>17</span></li>\r
                    <li><span>18</span></li><li><span>19</span></li><li><span>20</span></li><li><span>21</span></li><li><span>22</span></li><li><span>23</span></li>\r
                </ul>\r
                <ul class="endMinuteContent">\r
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
                <ul class="endSecondContent">\r
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
        </div>\r
<!--        <div class="cleanTime">清除</div>-->\r
    </div>\r
    <div class="footer">\r
        <div class="cleanTime">清除</div>\r
    </div>\r
</div>\r
`,bo=`.right{flex:10!important;display:flex;overflow:auto}.cleanTime{flex:100%}.time,.endTime{flex:1;display:flex;flex-direction:column;justify-content:center}.timeTitle,.endTimeTitle{display:flex;align-items:center;justify-content:center;height:3em}.timeContent,.endTimeContent{display:flex;height:calc(100% - 3em);flex:1}.timeLine{height:100%;width:0;border-right:1px solid var(--oo-color-gray1);margin:0 .5em}td.in-range span,td.in-range.today span{border-color:var(--oo-color-over);background-color:var(--oo-color-over)}td:hover span{color:var(--oo-color-main)}.disabled span{color:var(--oo-color-text4)!important}.is-select-one td:hover span{color:var(--oo-color-text-white);border-color:var(--oo-color-main);background-color:var(--oo-color-main)}@media only screen and (max-width: 767px){.right{flex:6!important}.timeTitle,.endTimeTitle{height:2.5em}}
`,re="datetime",_t="date",S="year",V="month",p4="week",ut="time",be="hour",R2="minute",pi="second",H="millisecond",_o="years",Lo="months",xo="dates",u4="end_hour",m4="end_minute",C4="end_second",De=class De extends ie{constructor(){super(!0);d(this,Ae);d(this,Te);d(this,B,void 0);m(this,"currentDate");m(this,"selectedArray",[]);m(this,"selectedHour");m(this,"selectedMinute");m(this,"selectedSecond");m(this,"selectedEndHour");m(this,"selectedEndMinute");m(this,"selectedEndSecond");m(this,"mode");d(this,ht,void 0);d(this,i1,void 0);d(this,r1,void 0);d(this,o1,void 0);d(this,l1,void 0);d(this,a1,void 0);d(this,d1,void 0);d(this,g2,void 0);d(this,f2,void 0);d(this,v2,void 0);this._elements=Object.assign(this._elements,{timeLine:null,endTime:null,endHourContent:null,endMinuteContent:null,endSecondContent:null}),this._initialize("oo-calendar-range",De,vo,bo),this.useCss(a4)}static get observedAttributes(){return De.prop=Object.assign({},ie.prop),Object.keys(De.prop).map(t=>b(t))}static isAllNumber(t){return t.every(e=>typeof e=="number"&&!isNaN(e))}_render(){this._checkMode(),this.currentView=this._getDefaultView(),this.setRange(),this._checkClass(),this._useBaseDate(),this.useValue(),this.showView(),this._checkTimeSelected(),[re,ut].includes(this.mode)&&(this._checkTimeContent(),this._checkStep())}_checkMode(){[S,V,_t,re,ut,_o,Lo,xo].includes(this._props.mode)||(console.warn(`Invalid mode: ${this._props.mode}. Use "${re}" instead`),this._props.mode=re),this._props.mode.endsWith("s")?(u(this,B,!0),this.mode=this._props.mode.slice(0,-1)):this.mode=this._props.mode,this._elements.content.setAttribute("mode",this._props.mode)}_useSingleValue(t,e){if(!t)return;let s={};switch(this.mode){case S:this._isEnableYear(t)&&(s[S]=parseInt(t));break;case V:if(this._isEnableMonth(t)){const[C,g]=t.split("-");s[S]=parseInt(C),s[V]=parseInt(g)-1}break;case _t:this._isEnableDate(t)&&(s=c(this,Ae,ss).call(this,new Date(t)));break;case ut:const[r,a,h]=t.split(":");this._useTimeValue(e!==0,parseInt(r),parseInt(a),v(h)!=="null"?parseInt(h):null);break;default:const p=ie.parseDate(t,!0);this._isEnableDate(p)&&(s=c(this,Ae,ss).call(this,p)),!l(this,B)&&this._useTimeValue(e!==0,p.getHours(),p.getMinutes(),p.getSeconds(),p);break}this.mode!==ut&&this.selectedArray.push(s)}_useTimeValue(t,e,s,r,a){const h=a?I(a,!0):null;this._isEnableHour(e,h,t)&&(t?this.selectedEndHour=e:this.selectedHour=e),this._isEnableMinute(s,e,h,t)&&(t?this.selectedEndMinute=s:this.selectedMinute=s),v(r)!=="null"&&this._isEnableSecond(r,s,e,h,t)&&(t?this.selectedEndSecond=r:this.selectedSecond=r)}useValue(){this.selectedArray=[],this.selectedHour=null,this.selectedMinute=null,this.selectedSecond=null,this.selectedEndHour=null,this.selectedEndMinute=null,this.selectedEndSecond=null;const t=this._parseValue().filter(e=>!!e);if(t.forEach((e,s)=>{this._useSingleValue(e,s)}),this.mode!==ut&&t.length>0){const e=this.selectedArray[0];!isNaN(e[S])&&this.currentDate.setFullYear(e[S]),!isNaN(e[V])&&this.currentDate.setMonth(e[V]),!isNaN(e[_t])&&this.currentDate.setDate(e[_t])}this.mode!==ut&&!l(this,B)&&(this.selectedArray.length>2&&(this.selectedArray.length=2),this._sortSelectedArray(),u(this,ht,this.selectedArray.length===1))}_checkTimeSelected(){this._checkHourSelected(),this._checkMinuteSelected(),this._checkSecondSelected(),this._checkEndHourSelected(),this._checkEndMinuteSelected(),this._checkEndSecondSelected()}getDate(){const t=[];return this.mode===ut?t.push(ot(this.currentDate),ot(this.currentDate)):this.selectedArray.forEach((e,s)=>{if(!l(this,B)&&s>1)return;const r=ot(this.currentDate);v(e[S])!=="null"&&r.setFullYear(e[S]),v(e[V])!=="null"&&r.setMonth(e[V]),v(e[_t])!=="null"&&r.setDate(e[_t]),t.push(r)}),t.forEach((e,s)=>{const r=s===1;e.setHours((r?this.selectedEndHour:this.selectedHour)||0),e.setMinutes((r?this.selectedEndMinute:this.selectedMinute)||0),e.setSeconds((r?this.selectedEndSecond:this.selectedSecond)||0)}),t}getSelectedDate(t=!1){let e;return this.selectedArray.length===1?e=this.selectedArray[0]:this.selectedArray.length===2&&(e=t?this.selectedArray[1]:this.selectedArray[0]),e&&e[H]?new Date(e[H]):null}selectTime(t,e){const s=t.parentNode.querySelector("li.selected");s&&n.removeClass(s,"selected"),n.addClass(t,"selected");const r=()=>{this._checkTimeValidation(e);const a=[this.selectedHour,this.selectedMinute,this.selectedEndHour,this.selectedEndMinute];this._props.secondEnable&&a.push(this.selectedSecond,this.selectedEndSecond),De.isAllNumber(a)&&this.dispatchChangeEvent()};if(this.mode===ut)r();else{const a=this.selectedArray.filter(h=>!!h[H]);a.length===0?(this.selectedArray.push(c(this,Ae,ss).call(this,this._getDefaultCurrentDate())),this.selectedArray.push({...this.selectedArray[0]})):a.length===1&&this.selectedArray.push(c(this,Ae,ss).call(this,new Date(a[0][H]))),u(this,ht,this.selectedArray.length===1),a.length<2&&this._checkLeftClass(),this.selectedArray[0][H]<this.selectedArray[1][H]?this.dispatchChangeEvent():r()}}selectDate(t,e){l(this,B)?this.selectMultipleDate(t,e):this.selectRangeDate(t,e)}selectMultipleDate(t,e){n.toggleClass(t,"selected");var s=this.selectedArray.findIndex((r,a)=>{switch(this.mode){case S:return e[S]===r[S];case V:return e[S]===r[S]&&e[V]===r[V];case _t:return e[H]===r[H]}});s>-1?this.selectedArray.splice(s,1):this.selectedArray.push(e),this.dispatchChangeEvent()}selectRangeDate(t,e){switch(this.selectedArray.length){case 0:this.selectedArray.push(e);break;case 1:this.selectedArray.push(e),this._sortSelectedArray();break;case 2:const s=[_t,re].includes(this.mode)?H:this.mode;this.selectedArray[0][s]===this.selectedArray[1][s]&&e[s]===this.selectedArray[1][s]?this.selectedArray=[]:this.selectedArray=[e];break}if(u(this,ht,this.selectedArray.length===1),this.hoverYear=null,this.hoverMonth=null,this.hoverDate=null,this._checkLeftClass(),[re,ut].includes(this.mode)&&this._checkTimeContent(),this.selectedArray.length===2){const s=this.getDate();s[0]<=s[1]?this.dispatchChangeEvent():this._clearTime()}}_sortSelectedArray(){this.selectedArray.sort((t,e)=>t[S]-e[S]||t[V]-e[V]||t[_t]-e[_t])}_checkTimeValidation(t){const e=C=>typeof C=="number"&&!isNaN(C)&&C>=0,s=(C,g)=>{const f=ei(C),x=this[`selected${f}`],y=this[`selectedEnd${f}`];return e(x)&&e(y)?g?x>y||x===y:x>y:!1},r=(C,g)=>{De.isAllNumber([this[C],this[g]])&&(this[C]=this[g])},h={[be]:{check:()=>s(be),assign:()=>{this.selectedEndHour=this.selectedHour,r("selectedEndMinute","selectedMinute"),this._props.secondEnable&&r("selectedEndSecond","selectedSecond")}},[u4]:{check:()=>s(be),assign:()=>{this.selectedHour=this.selectedEndHour,r("selectedMinute","selectedEndMinute"),this._props.secondEnable&&r("selectedSecond","selectedEndSecond")}},[R2]:{check:()=>s(be,!0)&&s(R2),assign:()=>{this.selectedEndMinute=this.selectedMinute,this._props.secondEnable&&r("selectedEndSecond","selectedSecond")}},[m4]:{check:()=>s(be,!0)&&s(R2),assign:()=>{this.selectedMinute=this.selectedEndMinute,this._props.secondEnable&&r("selectedSecond","selectedEndSecond")}},[pi]:{check:()=>s(be,!0)&&s(R2,!0)&&s(pi),assign:()=>{this.selectedEndSecond=this.selectedSecond}},[C4]:{check:()=>s(be,!0)&&s(R2,!0)&&s(pi),assign:()=>{this.selectedSecond=this.selectedEndSecond}}}[t];h&&h.check()&&(h.assign(),this._checkTimeSelected())}dispatchChangeEvent(){const t=this.getDate(),e=this._props.format||this.getDefaultFormat(),s=t.map(r=>O(r,e));this.oldValue=JSON.stringify(s),this._props.value=this.oldValue,this.dispatchEvent(new CustomEvent("change",{detail:{dates:t,value:s}}))}clear(){this.selectedArray=[],this._clearLeft(),this._clearTime(),this.dispatchEvent(new CustomEvent("change",{detail:{dates:[],value:[]}}))}_clearLeft(){this.selectedArray=[],this._checkLeftClass()}_clearTime(){this.selectedHour=null,this.selectedMinute=null,this.selectedSecond=null,this.selectedEndHour=null,this.selectedEndMinute=null,this.selectedEndSecond=null,[re,ut].includes(this.mode)&&this._checkTimeContent()}_setEvent(){super._setEvent(),this._elements.middle.addEventListener("mouseleave",()=>{l(this,ht)&&(this.hoverYear=null,this.hoverMonth=null,this.hoverDate=null,this._checkLeftClass(),[re,ut].includes(this.mode)&&this._checkTimeContent())})}_checkClass(){super._checkClass(),n.checkClass(this._elements.endSecondContent,"hide",!this._props.secondEnable)}_checkLeftClass(){switch(n.checkClass(this._elements.left,"is-select-one",this.selectedArray.length===1),this.currentView){case S:this.checkYearTdsClass();break;case V:this.checkMonthTdsClass();break;case _t:this.checkDateTdsClass();break;case p4:this.checkWeekTdsClass();break}}_checkTimeContent(){this._checkHourContent(),this._checkMinuteContent(),this._checkSecondContent(),this._checkEndHourContent(),this._checkEndMinuteContent(),this._checkEndSecondContent()}checkYearTdsClass(){const t=new Date().getFullYear();this._elements.yearContent.querySelectorAll("td").forEach((s,r)=>{const a=s.dataset.year;this._checkYearTdClass(s,parseInt(a),t)})}_checkYearTdClass(t,e,s){const r=super._checkYearTdClass(t,e,s);if(!l(this,B)&&r){const a=l(this,ht)&&this.hoverYear;if(a){const h=[a,this.selectedArray[0][S]];n.checkClass(t,"in-range",Math.min(...h)<e&&e<Math.max(...h))}else if(this.selectedArray.length>1){const[h,p]=this.selectedArray;n.checkClass(t,"in-range",h[S]<e&&e<p[S])}else n.removeClass(t,"in-range")}}_isYearSelected(t){return this.selectedArray.find(e=>e[S]===t)}setYearEvent(){super.setYearEvent(),!l(this,i1)&&(this._elements.yearContent.querySelectorAll("td").forEach(t=>{t.addEventListener("mouseenter",()=>{if(!l(this,B)){let e=t.dataset.year;l(this,ht)&&this._isEnableYear(e)&&(this.hoverYear=parseInt(e),this.checkYearTdsClass())}})}),u(this,i1,!0))}_handleYearClick(t,e){this.selectDate(t,{year:e})}checkMonthTdsClass(){const t=new Date().getFullYear(),e=new Date().getMonth();this._elements.monthContent.querySelectorAll("td").forEach((s,r)=>{this._checkMonthTdClass(s,parseInt(s.dataset.year),parseInt(s.dataset.month)-1,t,e)})}_checkMonthTdClass(t,e,s,r,a){const h=super._checkMonthTdClass(t,e,s,r,a);if(!l(this,B)&&h){const p={year:e,month:s},C=l(this,ht)&&this.hoverMonth;if(v(C)==="number"&&this.hoverYear){const g={year:this.hoverYear,month:C},f=this.selectedArray[0],x=this._compare(g,f)?g:f,y=x===f?g:f;n.checkClass(t,"in-range",this._compare(x,p)&&this._compare(p,y))}else if(this.selectedArray.length>1){const[g,f]=this.selectedArray;n.checkClass(t,"in-range",this._compare(g,p)&&this._compare(p,f))}else n.removeClass(t,"in-range")}}_compare(t,e){return e[S]===t[S]?e[V]>t[V]:e[S]>t[S]}_isMonthSelected(t,e){return this.selectedArray.find(s=>s[S]===t&&s[V]===e)}setMonthEvent(){super.setMonthEvent(),!l(this,r1)&&(this._elements.monthContent.querySelectorAll("td").forEach(t=>{t.addEventListener("mouseover",()=>{if(!l(this,B)){let e=t.dataset.year,s=t.dataset.month;l(this,ht)&&this._isEnableMonth(e+"-"+s)&&(this.hoverYear=parseInt(e),this.hoverMonth=parseInt(s)-1,this.checkMonthTdsClass())}})}),u(this,r1,!0))}_handleMonthClick(t,e,s){this.selectDate(t,{year:e,month:s})}_checkDateTdClass(t,e,s){const r=super._checkDateTdClass(t,e,s);if(!l(this,B)&&r){const a=l(this,ht)&&this.hoverDate;if(a){const h=this.selectedArray[0],p=Math.min(a.getTime(),h[H]),C=Math.max(a.getTime(),h[H]);n.checkClass(t,"in-range",e.getTime()>p&&e.getTime()<C)}else if(this.selectedArray.length>1){const[h,p]=this.selectedArray;n.checkClass(t,"in-range",e.getTime()>h[H]&&e.getTime()<p[H])}else n.removeClass(t,"in-range")}}checkDateTdsClass(){const t=I(new Date).getTime();this._elements.dateContent.querySelectorAll("td").forEach((e,s)=>{if(s%8===0)return;const r=new Date(`${e.dataset.dateValue} 00:00:00`);this._checkDateTdClass(e,r,t)})}setDateEvent(){super.setDateEvent(),!l(this,o1)&&(this._elements.dateContent.querySelectorAll("td").forEach((t,e)=>{e%8!==0&&t.addEventListener("mouseover",()=>{if(!l(this,B)){let s=t.dataset.dateValue;l(this,ht)&&this._isEnableDate(s)&&(this.hoverDate=new Date(s),this.checkDateTdsClass())}})}),u(this,o1,!0))}_isDateSelected(t){return this.selectedArray.find(e=>e[H]===t.getTime())}_handleDateClick(t,e,s,r){const a={year:e,month:s,date:r};a[H]=new Date(e,s,r).getTime(),this.selectDate(t,a)}_checkWeekDateTdClass(t,e,s){this._checkDateTdClass(t,e,s)}checkWeekTdsClass(){const t=I(new Date).getTime();this._elements.dateContent.querySelectorAll("td").forEach((e,s)=>{if(s%8===0)return;const r=new Date(`${e.dataset.dateValue} 00:00:00`);this._checkWeekDateTdClass(e,r,t)})}setWeekEvent(){super.setWeekEvent()}_checkEndHourContent(){this._elements.endHourContent.querySelectorAll("li").forEach((t,e)=>{const s=this._isEnableHour(e,null,!0);n.checkClass(t,"disabled",!s),!s&&this.selectedEndHour===e&&(this.selectedEndHour=null),n.checkClass(t,"selected",s&&this.selectedEndHour===e)})}_checkEndHourSelected(){this._elements.endHourContent.querySelectorAll("li").forEach((t,e)=>{n.checkClass(t,"selected",this.selectedEndHour===e),this.selectedEndHour===e&&setTimeout(()=>{e!==0&&u(this,g2,!0),t.scrollIntoView({block:"nearest"})},100)})}setHourEvent(){if(super.setHourEvent(),l(this,l1))return;const t=()=>{l(this,g2)||this._fixTimeNode(this._elements.endHourContent),u(this,g2,!1)};this._elements.endHourContent.addEventListener("scroll",()=>{$(t,100,this)}),this._elements.endHourContent.querySelectorAll("li").forEach((e,s)=>{const r=s.toString().padStart(2,"0");e.dataset.hour=r,e.addEventListener("click",()=>{this._isEnableHour(r,null,!0)&&(this.currentDate.setHours(r),this.selectedEndHour=parseInt(r),this._checkEndMinuteContent(),this._checkEndSecondContent(),this.selectTime(e,u4))})}),u(this,l1,!0)}_checkEndMinuteContent(){this._elements.endMinuteContent.querySelectorAll("li").forEach((t,e)=>{const s=this._isEnableMinute(e,this.selectedEndHour,null,!0);n.checkClass(t,"disabled",!s),!s&&this.selectedEndMinute===e&&n.removeClass(t,"selected"),n.checkClass(t,"selected",s&&this.selectedEndMinute===e)})}_checkMinuteStep(){super._checkMinuteStep();const t=this._props.step||1;this._elements.endMinuteContent.querySelectorAll("li").forEach((e,s)=>{n.checkClass(e,"hide",s%t!==0)})}_checkEndMinuteSelected(){this._elements.endMinuteContent.querySelectorAll("li").forEach((t,e)=>{n.checkClass(t,"selected",this.selectedEndMinute===e),this.selectedEndMinute===e&&setTimeout(()=>{e!==0&&u(this,f2,!0),t.scrollIntoView({block:"nearest"})},100)})}setMinuteEvent(){if(super.setMinuteEvent(),l(this,a1))return;const t=()=>{l(this,f2)||this._fixTimeNode(this._elements.endMinuteContent),u(this,f2,!1)};this._elements.endMinuteContent.addEventListener("scroll",()=>{$(t,100,this)}),this._elements.endMinuteContent.querySelectorAll("li").forEach((e,s)=>{const r=s.toString().padStart(2,"0");e.dataset.minute=r,e.addEventListener("click",()=>{this._isEnableMinute(r,this.selectedEndHour,null,!0)&&(this.currentDate.setMinutes(r),this.selectedEndMinute=parseInt(r),this._checkEndSecondContent(),this.selectTime(e,m4))})}),u(this,a1,!0)}_checkEndSecondContent(){this._props.secondEnable&&this._elements.endSecondContent.querySelectorAll("li").forEach((t,e)=>{const s=this._isEnableSecond(e,this.selectedEndMinute,this.selectedEndHour,null,!0);n.checkClass(t,"disabled",!s),!s&&this.selectedEndSecond===e&&(this.selectedEndSecond=null),n.checkClass(t,"selected",s&&this.selectedEndSecond===e)})}_checkSecondStep(){super._checkSecondStep();const t=this._props.step||1;this._elements.endSecondContent.querySelectorAll("li").forEach((e,s)=>{n.checkClass(e,"hide",s%t!==0)})}_checkEndSecondSelected(){this._props.secondEnable&&this._elements.endSecondContent.querySelectorAll("li").forEach((t,e)=>{n.checkClass(t,"selected",this.selectedEndSecond===e),this.selectedEndSecond===e&&setTimeout(()=>{e!==0&&u(this,v2,!0),t.scrollIntoView({block:"nearest"})},100)})}setSecondEvent(){if(super.setSecondEvent(),l(this,d1))return;const t=()=>{l(this,v2)||this._fixTimeNode(this._elements.endSecondContent),u(this,v2,!1)};this._elements.endSecondContent.addEventListener("scroll",()=>{$(t,100,this)}),this._elements.endSecondContent.querySelectorAll("li").forEach((e,s)=>{const r=s.toString().padStart(2,"0");e.dataset.second=r,e.addEventListener("click",()=>{this._isEnableSecond(r,this.selectedEndMinute,this.selectedEndHour,null,!0)&&(this.currentDate.setSeconds(r),this.selectedEndSecond=parseInt(r),this.selectTime(e,C4))})}),u(this,d1,!0)}_getCurrentDateRange(t){if(l(this,B)||this.selectedArray.length===0)return null;const e=new Date(this.selectedArray[0][H]);for(let s=0;s<t.length;s++){const r=t[s];if(!r[0]&&c(this,Te,ns).call(this,e,r[1]))return[r];if(!r[1]&&c(this,Te,ns).call(this,r[0],e))return[r];if(c(this,Te,ns).call(this,r[0],e)&&c(this,Te,ns).call(this,e,r[1]))return[r]}}_formatValue(t){try{const e=this._props.format||this.getDefaultFormat();return[S,V,p4,ut].includes(this.mode)?t instanceof Date?O(t,e):t:t4(t)?O(new Date(t),e):""}catch{return""}}_parseValue(){let t;try{t=JSON.parse(this._props.value)}catch{t=this._props.value}if(l(this,B))return Array.isArray(t)?t.map(e=>this._formatValue(e)):[this._formatValue(t)];{const e=Array.isArray(t)?t.map(s=>this._formatValue(s)):[this._formatValue(t)];return[e[0]||"",e[1]||""]}}};B=new WeakMap,ht=new WeakMap,i1=new WeakMap,r1=new WeakMap,o1=new WeakMap,l1=new WeakMap,a1=new WeakMap,d1=new WeakMap,g2=new WeakMap,f2=new WeakMap,v2=new WeakMap,Ae=new WeakSet,ss=function(t){const e=I(t,!0);return{year:e.getFullYear(),month:e.getMonth(),date:e.getDate(),millisecond:e.getTime()}},Te=new WeakSet,ns=function(t,e){return t<e||t-e===0};let ui=De;const yo=`.content {\r
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
`,wo='<div class="capsulae content"></div>',Qs=class Qs extends N{constructor(){super();d(this,Js);m(this,"_setPropMap",{skin:()=>{this._useSkin(this._props.skin)}});this._init("oo-capsulae")}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_init(t,e,s,r){this._initialize(t,e||Qs,s||wo,r||yo)}_render(){new MutationObserver(function(e){e.forEach(s=>{s.addedNodes.forEach(r=>{r.nodeType===Node.ELEMENT_NODE&&this._content.appendChild(r)})})}).observe(this,{subtree:!1,childList:!0,attributes:!1,characterData:!1}),c(this,Js,G4).call(this)}};Js=new WeakSet,G4=function(){let t=this.firstElementChild;for(;t;)this._content.appendChild(t),t=this.firstElementChild},m(Qs,"prop",{skin:""});let mi=Qs;const ko=`* {\r
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
`;function W8(){}function Eo(o){var i;return typeof o=="object"&&(o.dn||o.distinguishedName)||((i=o==null?void 0:o.includes)==null?void 0:i.call(o,"@"))}function Ci(o){if(Eo(o)){const t=(typeof o=="object"?o.dn||o.distinguishedName||"":o).split("@"),e=(t==null?void 0:t.at(-1))||"unknown";return["I","P","U","G","R","UD","UA","PA","PP","UP","PROCESS","A"].includes(e)?e:"unknown"}}function ys(o){if(Array.isArray(o))return o.flat(1/0).map(i=>ys(i));{const i=o.split("@");return(i==null?void 0:i[0])||o}}function Mo(o){if(Array.isArray(o))return o.flat(1/0).map(i=>ys(i));{const i=o.split("@");return(i==null?void 0:i[1])||o}}function g4(o){return typeof o=="object"?o.dn||o.id||o.unique:o}function zt(o){return o.map(i=>typeof i=="object"?i.dn||i.id||i.unique:i)}async function So(o){return Array.isArray(o)?await o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({groupList:zt(o)}).then(i=>i.data):await o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({groupList:zt([o])}).then(i=>i.data[0])}async function Ao(o){return Array.isArray(o)?await o2.Actions.load("x_organization_assemble_express").IdentityAction.listObject({identityList:zt(o)}).then(i=>i.data):await o2.Actions.load("x_organization_assemble_express").IdentityAction.listObject({identityList:zt([o]),referenceFlag:"true"}).then(i=>i.data[0])}async function To(o){return Array.isArray(o)?await o2.Actions.load("x_organization_assemble_express").PersonAction.listObject({personList:zt(o)}).then(i=>i.data):await o2.Actions.load("x_organization_assemble_express").PersonAction.get(o).then(i=>i.data)}function ws(o){return o?`/x_organization_assemble_control/jaxrs/person/${encodeURI(g4(o))}/icon`:"/x_organization_assemble_personal/jaxrs/person/icon"}async function Do(o){return Array.isArray(o)?await o2.Actions.load("x_organization_assemble_express").RoleAction.listObject({roleList:zt(o)}).then(i=>i.data):await o2.Actions.load("x_organization_assemble_express").RoleAction.listObject({roleList:zt([o])}).then(i=>i.data[0])}async function Po(o){return Array.isArray(o)?await o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({unitList:zt(o)}).then(i=>i.data):await o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({unitList:zt([o])}).then(i=>i.data[0])}async function Io(o){return await o2.Actions.load("x_organization_assemble_control").UnitDutyAction.get(g4(o)).then(i=>i.data)}const No="x_processplatform_assemble_surface",Oo="x_processplatform_assemble_designer";function f4(o,i){return Array.isArray(o)?o.map(t=>f4(t,i)):(o.dn||(o.dn=`${o.id}@${i.toUpperCase()}@${o.name}`),o)}async function Fo(o,i,t,e,s,...r){const a=await o2.Actions.load(o);return f4(await a[i][t].apply(s,r),e)}const V2={async invoke(o,i,t,...e){return await Fo(Oo,o,i,t,this,...e)},async listApplication(){return await this.invoke("ApplicationAction","list","app")},async listProcess(o){return await this.invoke("ProcessAction","listWithApplication","process",o)},async getApplication(o){return await this.invoke("ApplicationAction","get","app",o)},async getProcess(o){return await this.invoke("ProcessAction","getProcess","process",o)},async getActivity(o,i){return await this.invoke("ProcessAction","getActivity","a",o,i)},async getProcessDesign(o){return await this.invoke("ProcessAction","get","process",o)},async getProcessWithApp(o){return new Promise(i=>{window.setTimeout(async()=>{const t=Mo(o),e=await o2.Actions.load(No),s=await this.invoke("ProcessAction","get","process",t),r=await e.ApplicationAction.get(s.data.application);s.data.application=r.data,i(s.data)},500)})}},v4={I_html:`
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
`,A_html:`
<div class="content">
    <div class="name"></div>
    <div class="description"></div>
</div>
`,other_html:`
<div class="content">
    <div class="name"></div>
</div>
`},Pe=class Pe extends N{constructor(){super();d(this,h1);d(this,tn);m(this,"_elements",{icon:null,name:null,unitName:null,unit:null,duty:null,levelName:null,description:null,phone:null,application:null});m(this,"isAsync",!0);m(this,"_setPropMap",{value:()=>{this._canRender()&&c(this,h1,c3).call(this)},data:()=>{this._canRender()&&c(this,h1,c3).call(this)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._canRender()&&this._useSkin(this._props.skin)}});d(this,b2,null);this._props=this._getProps(Pe.prop),this._createProperties(Pe)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_canRender(){return super._canRender(null,["value","data"])}async _render(){l(this,b2)&&clearTimeout(l(this,b2)),u(this,b2,setTimeout(()=>{c(this,tn,K4).call(this)},10))}async createICard(t){var a;const e=this._props.value||this._props.data.dn,s=await Ao(e),r=ws(s.person);n.setStyle(this._elements.icon,"background-image",`url('${r}')`),this._elements.name.textContent=s.name,this._elements.unitName.textContent=s.woUnit.name,this._elements.unit.textContent=s.woUnit.levelName,this._elements.duty.textContent=(a=s.woUnitDutyList)==null?void 0:a.map(h=>`${h.name}(${h.woUnit.name})`).join(", ")}async createPCard(t){const e=this._props.value||this._props.data.dn,s=await To(e),r=ws(s.dn||s.id);n.setStyle(this._elements.icon,"background-image",`url('${r}')`),this._elements.name.textContent=s.name,this._elements.unitName.textContent=s.woIdentityList.map(a=>a.woUnit.name).join(", "),this._elements.unit.innerHTML=s.woIdentityList.map(a=>`<div>${a.woUnit.levelName}</div>`).join(""),this._elements.duty.textContent=s.woIdentityList.map(a=>a.woUnitDutyList&&a.woUnitDutyList.length?a.woUnitDutyList.map(h=>`${h.name}(${a.woUnit.name})`).join(", "):null).filter(a=>a!=null&&a!=="").join(", ")}async createUCard(t){const e=v(t)==="object"?await Promise.resolve(t):await Po(t);this._elements.name.textContent=e.name,this._elements.levelName.textContent=e.levelName,this._elements.description.textContent=e.description}async createUDCard(t){const e=v(t)==="object"?await Promise.resolve(t):await Io(t);this._elements.name.textContent=e.name,this._elements.levelName.textContent=e.woUnit.levelName,this._elements.description.textContent=""}async createGCard(t){const e=v(t)==="object"?await Promise.resolve(t):await So(t);this._elements.name.textContent=e.name,this._elements.description.textContent=e.description}async createRCard(t){const e=v(t)==="object"?await Promise.resolve(t):await Do(t);this._elements.name.textContent=e.name,this._elements.description.textContent=e.description}async createPROCESSCard(t){const e=v(t)==="object"?await(async()=>(v(t.application)==="object"||(t.application=await V2.getApplication(t.application)),t))():await V2.getProcessWithApp(t),s=e.application.icon||"";s?n.setStyle(this._elements.icon,"background-image",`url('data:image/png;base64,${s}')`):(n.addClass(this._elements.icon,"process-icon"),n.addClass(this._elements.icon,"ooicon-process")),this._elements.name.textContent=e.name,this._elements.description.innerHTML=e.description||`应用"${e.application.name}"中的流程"${e.name}"`,this._elements.application.textContent=e.application.name}async createAPPCard(t){const e=await V2.getApplication(t.id||t);e.data.icon?n.setStyle(this._elements.icon,"background-image",`url('data:image/png;base64,${e.data.icon}')`):(n.addClass(this._elements.icon,"process-icon"),n.addClass(this._elements.icon,"ooicon-computer")),this._elements.name.textContent=e.data.name,this._elements.application.textContent=e.data.applicationCategory||"未分类应用",this._elements.description.textContent=e.data.description}async createACard(t){const[,,e,s,r]=t.split("@"),{data:a,process:h}=await Promise.all([V2.getActivity(e,r),V2.getProcess(s)]).then(([p,C])=>({data:p.data,process:C.data}));this._elements.name.textContent=h.name+"-"+a.name,this._elements.description.textContent=a.description}async createOtherCard(t){this._elements.name.textContent=v(t)==="object"?t.name:t}};h1=new WeakSet,c3=function(){const t=this.shadowRoot||this.attachShadow({mode:"open"});n.empty(t);const e=Ci(this._props.value||this._props.data),s=Pe.cardTypys.includes(e)?v4[e+"_html"]:v4.other_html,r=o4(s,ko,"oo-card"+e,!0);this._useTemplate(r),this._render()},b2=new WeakMap,tn=new WeakSet,K4=async function(){if(this._props.value||Object.keys(this._props.data).length){const t=this._props.value||this._props.data,e=Ci(t);await(this[`create${e}Card`]||this.createOtherCard).call(this,t),n.addClass(this._content,"show"),this.dispatchEvent(new CustomEvent("loaded"))}},m(Pe,"prop",{value:"",data:{},border:"",cssLink:"",skin:""}),m(Pe,"cardTypys",["I","P","U","UD","G","R","PROCESS","APP","A"]);let gi=Pe;const $o=`<label class="content">\r
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
`,jo=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    --border: var(--oo-color-text2);\r
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
    word-break: break-all;\r
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
.hide{\r
    display: none !important;\r
}\r
\r
/* 移动端，标签在上，输入框下划线 skin-mode='mobile' */\r
.content.mobile .label>.text{\r
    color: var(--oo-color-text);\r
}\r
/* ---------------------------------------------- */`,en=class en extends N{constructor(){super();m(this,"_elements",{label:null,button:null,text:null,slot:null,check:null,input:null,checkIcon:null});m(this,"_setPropMap",{text:()=>{this._elements.text.textContent=this._props.text},value:()=>{this._elements.input.value=this._props.value,this._elements.input.setAttribute("value",this._props.value)},name:t=>{t!==this._props.name&&(this._elements.input.name=this._props.name,this._elements.input.setAttribute("name",this._props.name),this.setAttribute("name",this._props.name))},checked:()=>{this._elements.input.checked=!!this._props.checked,this._render()},disabled:()=>{this._elements.input.disabled=!!this._props.disabled,this._render()},size:()=>{this._props.size?n.setStyle(this._elements.button,"font-size",this._props.size):n.setStyle(this._elements.button,"font-size","auto")},skin:()=>{this._useSkin(this._props.skin)},skinMode:t=>{t&&n.removeClass(this._content,t),this._props.skinMode&&n.addClass(this._content,this._props.skinMode)},readmode:()=>{this._props.readmode?(this.checked?n.removeClass(this,"hide"):n.addClass(this,"hide"),n.addClass(this._elements.button,"hide"),n.addClass(this._elements.label,"readmode")):(n.removeClass(this,"hide"),n.removeClass(this._elements.button,"hide"),n.removeClass(this._elements.label,"readmode"),this._props.disabled||(this._elements.input.disabled=!1))},$default:()=>{}});m(this,"group",null)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_init(t,e,s,r){this._initialize(t,e||en,s||$o,r||jo)}_setEvent(){super._setEvent(),this._elements.input.addEventListener("change",t=>{t.stopPropagation(),this.checked=this._elements.input.checked,this.dispatchEvent(new Event("change",t))})}_unchecked(){this._elements.input.checked=!1,this._props.checked=!1,this._render()}_render(){let t=this.firstChild;for(;t;)this._elements.slot.appendChild(t),t=this.firstChild;const e=this._elements.input.checked;n.checkClass(this._elements.button,"checked",e),n.checkClass(this._elements.check,"checked",e),n.checkClass(this._elements.label,"checked",e),n.checkClass(this._content,"checked",e),n.checkClass(this,"checked",e);const s=this._elements.input.disabled;n.checkClass(this._elements.button,"disabled",s),n.checkClass(this._elements.check,"disabled",s),n.checkClass(this._elements.label,"disabled",s),this._setPropMap.text(),this._setPropMap.readmode()}_connected(){var s;this._render();const t=this.tagName.toLowerCase()+"-group",e=n.getParent(this,t);e&&(this.group=e,(s=e._appendItem)==null||s.call(e,this))}_disconnected(){if(this.group){const t=this.group;this.group=null,t._removeItem(this)}}get checked(){return this._elements.input.checked}set checked(t){const e=this.checked!==t;this._elements.input.checked=!!t,this._render(),e&&this.dispatchEvent(new Event("change"))}};m(en,"prop",{checked:!1,disabled:!1,text:"",value:"",name:"",size:"",skin:"",readmode:!1,skinMode:""});let ks=en;class zo extends ks{constructor(){super(),this._init("oo-checkbox")}_render(){this._elements.input.type="checkbox",n.addClass(this._elements.checkIcon,"ooicon-checkmark"),super._render()}}const Zo=`<div class="group content">\r
	<div style="display: table-row;">\r
		<div class="label-row" class="hide">\r
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
`,Ro=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    --shadow: var(--oo-shadow-border);\r
}\r
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
    min-height: 2.25em;\r
}\r
\r
.labelText {\r
    margin-right: 0.5em;\r
    padding: 0.5em 0.35em;\r
    display: flex;\r
    align-items: center;\r
    height: 100%;\r
    min-height: 2.25em;\r
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
    padding-left: 0.3rem;\r
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
    display: none !important;\r
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
    width: 100%;\r
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
.emptyPlaceholder{\r
    padding-left: 0.3em;\r
}\r
\r
/* 移动端，标签在上，输入框下划线 skin-mode='mobile' */\r
.content.mobile{\r
    --oo-shadow-border: inset 0 -1px 0 0px var(--oo-color-gray-d);\r
    --oo-shadow-border-focus: inset 0 -1px 0 0px var(--oo-color-main);\r
    --oo-shadow-border-error: inset 0 -1px 0 0px var(--oo-color-error);\r
    --oo-default-radius: 0;\r
    --oo-area-radius: 0;\r
}\r
\r
.content.mobile.group {\r
    display: block !important;\r
}\r
.content.mobile.group > div {\r
    display: block !important;\r
}\r
\r
.content.mobile .label-row {\r
    display: block !important;\r
    width: unset !important;\r
}\r
\r
.content.mobile .label-row > .label {\r
    display: block !important;\r
}\r
\r
.content.mobile .label-row > .label > .labelText {\r
    width: unset !important;\r
    min-width: unset !important;\r
    max-width: unset !important;\r
    margin-right: 0 !important;\r
    padding: 0.5em 0.35em !important;\r
    display: block !important;\r
    height: unset !important;\r
    min-height: unset !important;\r
}\r
\r
.content.mobile .itemsContent{\r
    width: 100% !important;\r
    padding: 0.5em 0.35em !important;\r
    box-shadow: var(--shadow);\r
    display: block !important;\r
}\r
.content.mobile .items{\r
    display: block !important;\r
    font-size: 1.125em;\r
    color: var(--oo-color-text);\r
}\r
.content.mobile.readmode .itemsContent {\r
    padding: 0.5em 0.05em !important;\r
    /* border-bottom: 1px solid #dddddd !important; */\r
}\r
\r
.content.mobile .requiredFlag {\r
    box-shadow: var(--shadow);\r
}\r
.content.mobile.required .itemsContent{\r
    display: table-cell !important;\r
}\r
\r
/* ---------------------------------------------- */`,Vo={title:"O2OA",error:{http:{title:"发送HTTP请求错误",text:"发送{method}请求 '{url}' 状态: {status} ({statusText})"},loadIndexError:"加载首页出错",loadIndexNotSet:"未指定要加载的首页应用！您应该从O2OA的应用项目中运行。",action:{title:"O2ActionError"},failCode:{BROADCAST_LISTENED_LOOP_BROADCAST:"广播事件监听到另一个循环的广播事件:{$}",ENTITY_EXISTS:"实体对象已存在，对象类型：{$0}，标识：{$1}",ENTITY_FLAG_CONFLICT:"实体对象标识冲突,对象类型:{$0},标识:{$1}",ENTITY_NOT_FOUND:"实体对象不存在,对象类型:{$0},标识:{S1}",FIELD_EMPTY:"字段不能为空,字段:{$0}",FIELD_EXISTS:"字段已存在,字段:{$0},值:{$1}",FIELD_NOT_EXISTS:"字段值不存在,字段:{$0}",FIELD_VERIFICATION_FAIL:"字段校验失败,字段:{$0},值:{$1}",MISSING_PARAMETER:"缺少参数",OBJECT_EXIST:"对象已存在",OBJECT_NOT_FOUND:"对象不存在",PARAMETER_VERIFICATION_FAIL:"参数校验不通过,参数:{$}",APPLICATION_HAS_PROCESS:"应用中已存在名为“{$}”的流程。",MANUAL_PARSE_TASK_TARGET_EMPTY:"无法为活动“{$0}”解析到待办对象，工作：{$1}",PROCESSVERSION_ACTIVE:"流程版不是激活流程版本",PROCESSVERSION_HAS_PROCESSING_JOB:"流程版本存在流转中的任务",PROCESS_HAS_PROCESSVERSION:"流程中存在流程版本",ROUTE_NOT_FOUND_ACTIVITY:"路由没有找到对应的活动:{}",UPLOAD_FILE_EMPTY:"上传文件为空.",UPLOAD_FILE_TOO_LARGE:"上传文件过大,文件长度:{}.",WORK_NOT_FOUND_ROUTE:"工作没有找到任何路由, 工作:{}",WORK_TASK_EMPTY:"工作的待办对象为空, 工作:{}",FORBIDDEN:"未授权访问",LOGIN_CODE_ERROR:"验证码错误",LOGIN_ERROR:"用户不存在或密码错误",PASSWORD_EXPIRED:"密码已过期",PERMISSION_DENIED:"权限不足",PERSON_BAN:"用户被禁用",PERSON_LOCKED:"用户被锁定",UNAUTHORIZED:"未认证",GROUP_NOT_FOUND:"群组“{$0}”不存在。",IDENTITY_NOT_FOUND:"身份“{$0}”不存在。",PERSON_MAJOR_IDENTITY_NOT_FOUND:"用户主身份不存在, 用户:{$0}",PERSON_NOT_FOUND:"用户不存在, 标识:{$0}",PERSON_NOT_FOUND_WITH_IDENTITY:"无法通过身份查找到用户, 身份:{$0}",PERSON_NOT_MATCH_IDENTITY:"用户和身份不匹配, 用户:{$0}, 身份:{$1}",ROLE_NOT_FOUND:"角色不存在,标识:{$0}",SYSTEM_ROLE_CAN_NOT_DELETE:"系统角色不能删除,标识:{}",UNITDUTY_NOT_FOUND:"组织职务不存在,标识:{}",UNITPOST_NOT_FOUND:"职务不存在,标识:{}",UNIT_NOT_FOUND:"组织不存在,标识:{}",OTHER:"其他错误"}},login:{passwordLogin:"密码登录",codeLogin:"短信验证码登录",login:"登录",username:"用户名",password:"密码",captcha:"验证码",code:"短信验证码",sendCode:"发送验证码",reSendCode:"重发",forget:"忘记密码？",findPassword:"找回密码",register:"注册",wechat:"微信",official:"官网",forum:"论坛",support:"支持",changeCaptcha:"换一张",usernameEmpty:"用户名不能为空",sendCodeError:"您填写的信息有误",bindLoginInfo1:"手机扫码，安全登陆",bindLoginInfo2:"打开APP扫一扫",bindLoginInfo3:"登陆网页版",codeVerify:"短信验证",setNewPassword:"设置新密码",complete:"完成",next:"下一步",confirm:"确认",codeAnswerEmpty:"短信验证码不能为空",newPassword:"请输入新密码",confirmPassword:"请确认新密码",checkConfirmPassword:"新密码与确认密码不一致",resetPasswordSuccess:"您的密码修改成功！请重新登陆",loginResetSuccessInfo:"{seconds}S后自动跳转至登陆页面",resetPasswordError:"重置密码失败！",loginResetErrorInfo:"请核对您的用户名和短信验证码。",resetModify:"返回修改"},index:{homepage:"首页",searchKey:"请输入搜索关键字",menu:{app:"应用",process:"流程",cms:"信息",query:"数据"}},common:{clear:"清空",ok:"确定",cancel:"取消",yes:"是",no:"否",selectAll:"全选",delete:"删除",disable:"禁用",selectCountLimit:"选择数量限制",selectCountInfo:"您最多可以选择 {n} 个值"},component:{selectFile:"选择文件",notSelectedFile:"未选择文件",valueMissing:"请填写{label}",this:"此",delComfirm:"您确定要删除文件“{name}”吗？",delTitle:"删除文件确认"}};window.oo=window.oo||{I18n:{instances:{}}};const ct=class ct{constructor(i,t={}){m(this,"res",null);m(this,"defaultRes",{});this.name=i,this.defaultRes=t,i&&(window.oo.I18n.instances[i]=this)}static detect(){const i=new URL(window.location).searchParams,t=(i.get("lng")||i.get("lp")||localStorage.getItem("o2.language")||navigator.language||ct.defaultLng).toLocaleLowerCase();return ct.supportedLanguages.includes(t)?t:ct.supportedLanguages.find(e=>t.substring(0,t.indexOf("-"))===e)||ct.defaultLng}async load(){return this}t(i,t){return b4(i,t,this.res||this.defaultRes)}o(i){return _4(i,this.res||this.defaultRes)}};m(ct,"supportedLanguages",["zh-cn","en"]),m(ct,"defaultLng","zh-cn"),m(ct,"lng",ct.detect()),m(ct,"instances",{}),m(ct,"get",(i="index",t={})=>window.oo.I18n.instances[i]||(window.oo.I18n.instances[i]=new ct(i,t)));let Tt=ct;function b4(o,i,t){let e=(i2(`return this.${o}`,t)||o).toString();return i&&Object.keys(i).forEach(s=>{const r=new RegExp(`{${s}}`,"g");e=e.replace(r,i[s])}),e}function _4(o,i){const t=o?i2(`return this.${o}`,i)||{}:i;return Object.defineProperties(t,{t:{get(){return function(r,a){return b4(r,a,this)}},enumerable:!0,configurable:!0},o:{get(){return function(r){return _4(r,this)}},enumerable:!0,configurable:!0}}),t}Tt.get("index",Vo);const an=class an extends N{constructor(){super();d(this,sn);d(this,Ie);d(this,nn);d(this,rn);d(this,L2);d(this,c1);d(this,on);d(this,ln);m(this,"_items",[]);d(this,pe,0);m(this,"_elements",{label:null,group:null,labelText:null,items:null,invalidHint:null,input:null,requiredFlag:null,itemsContent:null});m(this,"_setPropMap",{name:()=>{this._props.name&&this._items.forEach(t=>{t.setAttribute("name",this._props.name)})},skin:()=>{this._items.forEach(t=>{t.setAttribute("skin",this._props.skin)})},skinMode:t=>{t&&n.removeClass(this._content,t),this._props.skinMode&&n.addClass(this._content,this._props.skinMode)},disabled:()=>{this._items.forEach(t=>{this._props.disabled?t.setAttribute("disabled",!0):t.disabled||t.removeAttribute("disabled")}),n.toggleAttr(this._elements.input,"disabled",this._props.disabled)},size:()=>{this._props.size&&this._items.forEach(t=>{t.setAttribute("size",this._props.size)})},col:()=>{this.rerender()},label:()=>{this._props.label?(this._elements.labelText.textContent=this._props.label,n.removeClass(this._elements.labelText,"hide"),n.removeClass(this._elements.label.parentElement,"hide")):(this._elements.labelText.textContent="",n.addClass(this._elements.labelText,"hide"),n.addClass(this._elements.label.parentElement,"hide"))},labelStyle:()=>{n.toggleAttr(this._elements.labelText,"style",this._props.labelStyle),this._setPropMap.labelAlign()},labelAlign:()=>{switch(this._props.labelAlign){case"right":n.setStyle(this._elements.labelText,"justify-content","flex-end");break;case"center":n.setStyle(this._elements.labelText,"justify-content","center");break;default:n.setStyle(this._elements.labelText,"justify-content","flex-start")}},viewStyle:()=>{this._props.readmode&&n.toggleAttr(this._elements.itemsContent,"style",this._props.viewStyle)},count:()=>{this.rerender()},readmode:t=>{this.rerender(),n.toggleAttr(this._elements.input,"readonly",this._props.readmode),this._props.readmode?n.toggleAttr(this._elements.itemsContent,"style",this._props.viewStyle):n.toggleAttr(this._elements.itemsContent,"style",""),this._fillEmptyReadmode(),this._setPropMap.required()},max:()=>{this._props.max===0?this._elements.input.removeAttribute("max"):this._elements.input.setAttribute("max",this._props.max)},maxlength:()=>{this._props.maxlength===0?this._elements.input.removeAttribute("maxlength"):this._elements.input.setAttribute("maxlength",this._props.maxlength)},min:()=>{this._props.min===0?this._elements.input.removeAttribute("min"):this._elements.input.setAttribute("min",this._props.min)},minlength:()=>{this._props.minlength===0?this._elements.input.removeAttribute("minlength"):this._elements.input.setAttribute("minlength",this._props.minlength)},pattern:()=>{this._props.pattern?this._elements.input.setAttribute("pattern",this._props.pattern):this._elements.input.removeAttribute("pattern")},required:()=>{this._props.required?this._elements.input.setAttribute("required",this._props.required):this._elements.input.removeAttribute("required"),n.checkClass(this._elements.group,"required",this._props.required&&!this._props.readmode&&!this._props.disabled),n.checkClass(this._elements.requiredFlag,"hide",!this._props.required||this._props.readmode||this._props.disabled)},step:()=>{this._props.step===1?this._elements.input.removeAttribute("step"):this._elements.input.setAttribute("step",this._props.step)},$default:()=>{}});d(this,_2,null)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_connected(){c(this,L2,q5).call(this),c(this,c1,p3).call(this),c(this,Ie,is).call(this)}rerender(){c(this,L2,q5).call(this),c(this,c1,p3).call(this),c(this,Ie,is).call(this)}_init(t,e,s,r){this._initialize(t,e||an,s||Zo,r||Ro)}_render(){this._props.name||(this._props.name=W3()),parseInt(this._props.col)||n.removeClass(this._elements.items,"hide-items"),parseInt(this._props.label)||n.addClass(this._elements.labelText,"hide"),this._itemChangeFun=this._itemChange.bind(this)}_removeItem(t){this._items.splice(this._items.indexOf(t),1),t.removeAttribute("slot"),t.removeEventListener("change",this._itemChangeFun),n.setStyle(t,"width","auto"),c(this,Ie,is).call(this)}_appendItem(t){this._items.push(t),t.setAttribute("slot","items"),t.setAttribute("name",this._props.name),t.setAttribute("skin",this._props.skin),t.setAttribute("skin-mode",this._props.skinMode),this._props.disabled?t.setAttribute("disabled",!0):t.disabled||t.removeAttribute("disabled"),this._props.size&&t.setAttribute("size",this._props.size),t.addEventListener("change",this._itemChangeFun),this._appendItemCheckValue(t),c(this,Ie,is).call(this,!0),this._props.count&&(this._props.values?this._props.values.length:0)>=this._props.count&&!t.checked&&(t.disabled=!0),this._props.readmode&&(t.readmode=!0)}_appendItemCheckValue(t){this._props.values&&this._props.values.length&&this._props.values.includes(t.value)&&(t.checked=!0)}_itemChange(t){t.currentTarget.checked?(this._props.values||(this._props.values=[]),this._props.values.includes(t.currentTarget.value)||this._props.values.push(t.currentTarget.value),this._props.disabled||(t.currentTarget.disabled=!1)):this._props.values&&jt(this._props.values,t.currentTarget.value),this._elements.input.value=this._props.values.join(","),this.unInvalidStyle(),this._props.validityBlur&&this.checkValidity(),c(this,L2,q5).call(this)}_setEvent(){super._setEvent(),this._elements.input.addEventListener("invalid",t=>{c(this,ln,sr).call(this,t.target.validity)})}setCustomValidity(){return this._elements.input.setCustomValidity(...arguments)}checkValidity(){return n.isDisplayNone(this)?!0:(this.dispatchEvent(new CustomEvent("validity")),this._elements.input.checkValidity(...arguments))}reportValidity(){return this.dispatchEvent(new CustomEvent("validity")),this._elements.input.reportValidity(...arguments)}unInvalidStyle(){this._elements.input.setCustomValidity(""),n.removeClass(this._elements.input,"invalid"),this._elements.invalidHint.textContent="",n.removeClass(this._elements.invalidHint,"show")}_fillEmptyReadmode(){!this._elements.input.value&&this._props.readmode?(this.emptyPlaceholderNode||(this.emptyPlaceholderNode=n("span.emptyPlaceholder"),this.emptyPlaceholderNode.setAttribute("slot","items"),this.emptyPlaceholderNode.textContent="-"),this._elements.items.insertAdjacentElement("beforebegin",this.emptyPlaceholderNode)):this.emptyPlaceholderNode&&this.emptyPlaceholderNode.remove()}};pe=new WeakMap,sn=new WeakSet,J4=function(){u(this,pe,0),this._items.forEach(t=>{n.setStyle(t,"width","auto");const e=n.getSize(t);e.x>l(this,pe)&&u(this,pe,e.x)})},_2=new WeakMap,Ie=new WeakSet,is=function(){l(this,_2)&&clearTimeout(l(this,_2)),u(this,_2,setTimeout(()=>{c(this,nn,Q4).call(this)},10))},nn=new WeakSet,Q4=function(){if(!this._props.readmode){c(this,rn,tr).call(this);const t=parseInt(this._props.col);t&&(c(this,sn,J4).call(this),this._items.forEach((e,s)=>{l(this,pe)&&n.setStyle(e,"width",l(this,pe)+"px"),(s+1)%t===0&&e.insertAdjacentElement("afterend",n("br.group-col-separator",{slot:"items"}))}))}n.removeClass(this._elements.items,"hide-items")},rn=new WeakSet,tr=function(){this.querySelectorAll("br.group-col-separator").forEach(e=>{e.remove()})},L2=new WeakSet,q5=function(){if(this._props.count)if((this._props.values?this._props.values.length:0)>=this._props.count)for(const e of this._items)e.checked||(e.disabled=!0);else for(const e of this._items)e.disabled=!1},c1=new WeakSet,p3=function(){if(this._props.readmode){n.addClass(this._elements.group,"readmode");const t=this.querySelectorAll("br");t.length&&t.forEach(e=>{n.addClass(e,"hide")});for(const e of this._items)e.style.width="unset",e.readmode=!!this._props.readmode}else{n.removeClass(this._elements.group,"readmode");for(const t of this._items)t.readmode=!!this._props.readmode}},on=new WeakSet,er=function(){return this._elements.input.validity.customError||this._props.validity&&this._elements.input.setCustomValidity(this._props.validity),this._elements.invalidHint&&(this._elements.invalidHint.textContent=this._elements.input.validationMessage),this._elements.input.validationMessage},ln=new WeakSet,sr=function(t){t.valueMissing&&(lp=Tt.get("index").o("component"),this._elements.input.setCustomValidity(lp.t("valueMissing",{label:this._props.label||lp.this}))),this.dispatchEvent(new CustomEvent("invalid",{detail:t})),n.addClass(this._elements.input,"invalid"),c(this,on,er).call(this)&&n.addClass(this._elements.invalidHint,"show")},m(an,"prop",{disabled:!1,value:"",name:"",size:"",col:"",label:"",labelStyle:"",labelAlign:"right",viewStyle:"",skin:"",count:0,readmode:!1,skinMode:"",max:0,maxlength:0,min:0,minlength:0,pattern:"",required:!1,step:1,validity:"",validityBlur:!1});let Es=an;const t3=class t3 extends Es{constructor(){super();d(this,dn);d(this,hn);d(this,cn);c(this,dn,nr).call(this),this._init("oo-checkbox-group",t3)}get value(){return c(this,hn,ir).call(this)}set value(t){Array.isArray(t)?this._props.values!==t&&(this._props.values=t,this._setPropMap.values()):this._props.value!==t&&(this._props.value=t,this._setPropMap.value())}get text(){return c(this,cn,rr).call(this)}};dn=new WeakSet,nr=function(){this._setPropMap=Object.assign(this._setPropMap,{value:()=>{this._props.values=this._props.value?this._props.value.split(/\s*,\s*/g):[],this._setPropMap.values(),this.rerender()},values:()=>{var t;if(this._props.values&&this._props.values.length)for(const e of this._items)this._props.values.includes(e.value)?e.checked=!0:e._unchecked();else for(const e of this._items)e._unchecked();this._elements.input.value=((t=this._props.values)==null?void 0:t.join(","))||"",this._fillEmptyReadmode()}})},hn=new WeakSet,ir=function(){const t=[];return this._items.forEach(e=>{e.checked&&t.push(e.value)}),t},cn=new WeakSet,rr=function(){const t=[];return this._items.forEach(e=>{e.checked&&t.push(e.text)}),t};let fi=t3;const Ho=`<div class="content">\r
	<div class="labelArea">\r
		<slot name="before-outer"></slot>\r
		<label for="input" class="label hide">\r
			<div class="labelText hide"></div>\r
			<slot name="label"></slot>\r
		</label>\r
		<div class="body">\r
			<div style="display: flex; align-items: center;">\r
				<div class="box">\r
					<slot name="before-inner-before"></slot>\r
					<div class="prefix"></div>\r
					<slot name="before-inner-after"></slot>\r
					<div class="view">\r
						<input class="input" id="input"/>\r
						<div class="viewText">-</div>\r
					</div>\r
\r
					<div class="file hide">\r
						<div class="fileEmptyFlag">-</div>\r
						<div class="fileList">\r
							<div class="emptyInfo">未选择文件</div>\r
						</div>\r
						<label class="fileLabel" for="fileinput"><oo-button type="simple" left-icon="create" class="fileButton" text="选择文件"></oo-button></label>\r
					</div>\r
					<slot name="after-inner-before"></slot>\r
					<div class="suffix"></div>\r
					<slot name="after-inner-after"></slot>\r
\r
					\r
				</div>\r
				<div class="requiredFlag hide">&#10039;</div>\r
			</div>\r
			<div class="invalidHint ooicon-error"></div>\r
		</div>\r
		<slot name="after-outer"></slot>\r
	</div>\r
</div>\r
`,L4=`* {\r
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
.options-position-node{\r
    position: absolute;\r
    transition: background-color 0.2s;\r
}\r
.options-position-node.hide{\r
    display: block !important;\r
    opacity: 0 !important;\r
    width: 0 !important;\r
    height: 0 !important;\r
}\r
.labelArea {\r
    display: flex;\r
    /* align-items: center; */\r
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
    /* align-items: center; */\r
}\r
.flex-end {\r
    justify-content: flex-end;\r
}\r
.flex-center {\r
    justify-content: center;\r
}\r
.body{\r
    flex: 1;\r
}\r
.body>div:first-child{\r
    display: flex; \r
    align-items: center;\r
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
    background: transparent;\r
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
.input[type='color'] {\r
    padding: 0.2em;\r
    height: revert;\r
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
    padding: 0.5em;\r
    display: grid;\r
    grid-template-columns: 1fr auto;\r
    gap: 2em;\r
    align-items: start;\r
    flex: 1;\r
}\r
.fileList {\r
    display: grid;\r
    grid-template-columns: 1fr 1fr;\r
    gap: 0.5em 1em;\r
    align-items: stretch;\r
    height: 100%;\r
}\r
.emptyInfo{\r
    height: 100%;\r
    display: flex;\r
    align-items: center;\r
    color: var(--oo-color-text4);\r
}\r
.fileInput{\r
    position: absolute;\r
    opacity: 0;\r
    width: 0;\r
    height: 0;\r
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
    display: none !important;\r
}\r
div.invalidHint {\r
    color: var(--oo-color-error);\r
    height: 0;\r
    line-height: 1.75em;\r
    overflow: hidden;\r
    word-break: break-all;\r
    transition: height 0.2s;\r
    display: flex;\r
    align-items: center;\r
    padding-left: 1px;\r
}\r
div.invalidHint.show {\r
    min-height: 1.75em;\r
    height: unset;\r
}\r
div.invalidHint.show::before {\r
    margin-right: 0.2em;\r
}\r
.view {\r
    position: relative;\r
    overflow: hidden;\r
    flex: 1;\r
    height: 100%;\r
}\r
.view[type='file']{\r
    position: absolute;\r
    opacity: 0;\r
}\r
.view[type='file'] input{\r
    width: 0;\r
    height: 0;\r
    padding: 0;\r
    opacity: 0;\r
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
\r
/* 移动端，标签在上，输入框下划线 skin-mode='mobile' */\r
.content.mobile{\r
    --oo-shadow-border: inset 0 -1px 0 0px var(--oo-color-gray-d);\r
    --oo-shadow-border-focus: inset 0 -1px 0 0px var(--oo-color-main);\r
    --oo-shadow-border-error: inset 0 -1px 0 0px var(--oo-color-error);\r
    --oo-default-radius: 0;\r
    --oo-area-radius: 0;\r
}\r
\r
.content.mobile .labelArea {\r
    display: flex !important;\r
    align-items: unset !important;\r
    position: static !important;\r
    height: unset !important;\r
    flex-direction: column;\r
}\r
\r
.content.mobile .label {\r
    color: var(--oo-color-text3);\r
}\r
\r
.content.mobile .requiredFlag {\r
    display: flex;\r
    align-items: center;\r
    box-shadow: var(--shadow);\r
}\r
.content.mobile .body>div{\r
    align-items: stretch !important;\r
}\r
\r
.content.mobile .labelText {\r
    width: unset !important;\r
    min-width: unset !important;\r
    max-width: unset !important;\r
    display: block !important;\r
}\r
.content.mobile .input {\r
    padding: 0.5em 0.35em;\r
    font-size: 1.125em;\r
\r
}\r
.content.mobile .viewText {\r
    padding: 0.5em 0.35em;\r
    font-size: 1.125em;\r
    background-color: transparent !important;\r
    border-bottom: 1px solid #dddddd !important;\r
}\r
/* 移动端显示一列 */\r
.content.mobile .file {\r
    grid-template-columns: 1fr;\r
}\r
.content.mobile .fileList {\r
    grid-template-columns: 1fr;\r
}\r
\r
.content.mobile oo-file {\r
    border-bottom: 1px solid #dedede;\r
}\r
/* .content.mobile .hide {\r
    display: none !important;\r
} */\r
\r
/* --------------------------- */\r
\r
@media only screen and (max-width: 767px) {\r
    .options-position-node{\r
        position: fixed;\r
        width: 100%;\r
        height: 100%;\r
        background-color: #cccccc33;\r
        z-index: 300000;\r
        top: 0 !important;\r
        left: 0 !important;\r
    }\r
    \r
    .options-area{\r
        transform-origin: bottom !important;\r
    }\r
    .options-area{\r
        top: unset !important;\r
        min-width: unset !important;\r
        width: 100% !important;\r
        bottom: 0 !important;\r
        height: 60%;\r
        padding-bottom: 20px !important;\r
    }\r
    .arrow{\r
        display: none;\r
    }\r
    .options-content{\r
        height: 100%;\r
        width: 100%;\r
        max-height: unset !important;\r
        min-height: unset !important;\r
        font-size: 1.125em;\r
        overscroll-behavior: contain;\r
    }\r
    \r
    .calendar-area{\r
        transform-origin: bottom !important;\r
        top: unset !important;\r
        min-width: unset !important;\r
        width: 100% !important;\r
        bottom: 0 !important;\r
        display: flex;\r
        justify-content: center;\r
        /*flex-direction: column;*/\r
    }\r
    .calendar-area.visible{\r
        transform: scale(1);\r
        opacity: 1;\r
    }\r
    .calendar-area.invisible{\r
        transform: scale(1, 0);\r
        opacity: 0;\r
    }\r
    \r
    .calendar-area oo-calendar, .calendar-area oo-calendar-range{\r
        width: 100%;\r
        padding-bottom: 20px !important;\r
        justify-content: center;\r
    }\r
    .calendar-area oo-calendar[mode='datetime'],\r
    .calendar-area oo-calendar:not([mode]),\r
    .calendar-area oo-calendar-range[mode='datetime'],\r
    .calendar-area oo-calendar-range:not([mode]){\r
        height: 38em;\r
    }\r
\r
    .calendar-area oo-calendar[mode='time'][second-enable='false'],\r
    .calendar-area oo-calendar-range[mode='time'][second-enable='false']{\r
        width: 100%;\r
    }\r
    .hide {\r
        display: none !important;\r
    }\r
}`,p1=class p1 extends N{constructor(){super();d(this,pn);d(this,un);m(this,"_elements",{label:null,input:null,file:null,box:null,prefix:null,suffix:null,labelText:null,fileList:null,emptyInfo:null,invalidHint:null,viewText:null,view:null,requiredFlag:null,body:null,fileLabel:null,fileButton:null,fileEmptyFlag:null});m(this,"_setPropMap",{leftIcon:t=>{n.removeClass(this._elements.prefix,`ooicon-${t} left-icon`),this._props.leftIcon&&n.addClass(this._elements.prefix,`ooicon-${this._props.leftIcon} left-icon`)},rightIcon:t=>{n.removeClass(this._elements.suffix,`ooicon-${t} right-icon`),this._props.rightIcon&&n.addClass(this._elements.suffix,`ooicon-${this._props.rightIcon} right-icon`)},width:()=>{this._props.width&&(this._elements.box.style.width=this._props.width,this._elements.box.parentElement.style.minWidth=this._props.width)},height:()=>{this._props.height&&(this._elements.box.style.height=this._props.height,this._elements.box.parentElement.style.minHeight=this._props.height)},skinMode:t=>{t&&n.removeClass(this._content,t),this._props.skinMode&&n.addClass(this._content,this._props.skinMode),this._elements.file&&this._elements.file.querySelectorAll("oo-file").forEach(s=>{s.setAttribute("skin-mode",this._props.skinMode)})},style:()=>{this._setPropMap.width(),this._setPropMap.height()},inputStyle:()=>{n.toggleAttr(this._elements.input,"style",this._props.inputStyle)},labelStyle:()=>{n.toggleAttr(this._elements.labelText,"style",this._props.labelStyle),this._setPropMap.labelAlign()},labelAlign:()=>{switch(this._props.labelAlign){case"right":n.setStyle(this._elements.labelText,"justify-content","flex-end");break;case"center":n.setStyle(this._elements.labelText,"justify-content","center");break;default:n.setStyle(this._elements.labelText,"justify-content","flex-start")}},viewStyle:()=>{this._props.readmode?n.set(this._elements.body,"style",this._props.viewStyle):n.set(this._elements.body,"style","")},bgcolor:()=>{this._props.bgcolor?n.setStyle(this._elements.box,"background-color",this._props.bgcolor):n.setStyle(this._elements.box,"background-color","transparent")},del:()=>{this._checkFileRead()},disabled:()=>{n.toggleAttr(this._elements.input,"disabled",this._props.disabled),n.checkClass(this._elements.box,"disabled",this._props.disabled),n.checkClass(this._elements.label,"disabled",this._props.disabled),this._elements.fileLabel&&n.checkClass(this._elements.fileLabel,"hide",this._props.disabled),this._checkFileRead()},readmode:()=>{n.toggleAttr(this._elements.input,"readonly",this._props.readmode),n.checkClass(this._elements.box,"readmode",this._props.readmode),n.checkClass(this._elements.label,"readmode",this._props.readmode),this._elements.fileLabel&&n.checkClass(this._elements.fileLabel,"hide",this._props.readmode),this._setPropMap.viewStyle(),this._setPropMap.required(),this._checkFileRead()},readonly:()=>{n.toggleAttr(this._elements.input,"readonly",this._props.readonly),this._elements.fileInput&&(this._props.readonly?this._elements.fileInput.setAttribute("disabled","true"):this._elements.fileInput.removeAttribute("disabled")),this._checkFileRead()},label:()=>{this._props.label?(this._elements.labelText.textContent=this._props.label,n.removeClass(this._elements.labelText,"hide"),n.removeClass(this._elements.label,"hide")):(this._elements.labelText.textContent="",n.addClass(this._elements.labelText,"hide"),n.addClass(this._elements.label,"hide"))},skin:()=>{this._useSkin(this._props.skin)},value:()=>{try{this._elements.input.value=this._props.value}catch{}this._props.value?(this._elements.viewText.textContent=this._props.value,this._props.type==="color"&&(this._elements.view.style.color=this._props.value)):this._elements.viewText.innerHTML="-"},appearance:()=>{n.checkClass(this._elements.input,"appearance",this._props.appearance==="none")},validity:()=>{},type:()=>{this._checkTypeFile()},autoSize:()=>{},resize:()=>{n.toggleClass(this._elements.input,"resize",this._props.resize)},required:()=>{n.toggleAttr(this._elements.input,"required",this._props.required),n.checkClass(this._elements.requiredFlag,"hide",!this._props.required||this._props.readmode||this._props.disabled)},selectFile:()=>{const t=this._elements.file.querySelector("oo-button");this._props.type==="file"&&t.setAttribute("text",this._props.selectFile||Tt.get("index").o("component").selectFile)},fileButtonStyle:()=>{this._elements.fileButton.setAttribute("type",this._props.fileButtonStyle)},fileButtonIcon:()=>{this._elements.fileButton.setAttribute("left-icon",this._props.fileButtonIcon)},placeholder:()=>{n.toggleAttr(this._elements.input,"placeholder",this._props.placeholder),this._props.type==="file"&&this._checkFileRead()},col:()=>{const t=this._props.col?parseInt(this._props.col):2;n.setStyles(this._elements.fileList,{"grid-template-columns":`repeat(${t}, 1fr)`})},$default:t=>{t==="value"?this.value=this._props[t]:(n.toggleAttr(this._elements.input,t,this._props[t]),this._elements.fileInput&&n.toggleAttr(this._elements.fileInput,t,this._props[t]))}});m(this,"_canValidityBlur",!1);const t=Tt.get("index").o("component");["selectFile","notSelectedFile"].forEach(e=>p1.prop[e]=t[e])}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_checkTypeFile(){this._props.type==="file"?(this._elements.fileInput||(this._elements.fileInput=this._elements.input.cloneNode(!0),this._elements.fileInput.setAttribute("class","fileInput"),this._elements.fileInput.setAttribute("type","file"),this._elements.fileInput.setAttribute("id","fileinput"),this._elements.view.append(this._elements.fileInput),this._props.readonly&&this._elements.fileInput.setAttribute("disabled","true")),this._elements.file&&n.removeClass(this._elements.file,"hide")):(this._elements.file&&n.addClass(this._elements.file,"hide"),n.toggleAttr(this._elements.input,"type",this._props.type)),n.toggleAttr(this._elements.view,"type",this._props.type)}_checkFileRead(){const t=this._props.readonly||this._props.readmode||this._props.disabled,e=this._props.readmode||this._props.disabled;if(this._elements.fileList){const s=!t&&this._props.del,r=this._elements.fileList.querySelectorAll("oo-file");for(const a of r)a.setAttribute("del",s)}e?(this._elements.emptyInfo&&(this._elements.emptyInfo.textContent=""),e&&this._elements.fileEmptyFlag&&n.show(this._elements.fileEmptyFlag)):(this._elements.fileEmptyFlag&&n.hide(this._elements.fileEmptyFlag),this._elements.emptyInfo&&(this._elements.emptyInfo.textContent=this._props.placeholder||"未选择文件"))}_init(t,e,s,r){this._initialize(t,e||p1,s||Ho,r||L4)}_inputFocus(){this._elements.input.addEventListener("focus",()=>{this._props.readmode||(n.addClass(this._elements.box,"focus"),n.addClass(this._elements.label,"focus"))})}_fileInputFocus(){this._elements.fileInput&&this._elements.fileInput.addEventListener("focus",t=>{this._props.readmode||(n.addClass(this._elements.box,"focus"),n.addClass(this._elements.label,"focus"))})}_inputBlur(){this._elements.input.addEventListener("blur",()=>{this.unInvalidStyle(),this._props.validityBlur&&this._canValidityBlur&&this.checkValidity(),n.removeClass(this._elements.box,"focus"),n.removeClass(this._elements.label,"focus")})}_fileInputBlur(){this._elements.fileInput&&this._elements.fileInput.addEventListener("blur",t=>{this.unInvalidStyle(),this._props.validityBlur&&this._canValidityBlur&&this.checkValidity(),n.removeClass(this._elements.box,"focus"),n.removeClass(this._elements.label,"focus")})}_inputInput(){this._elements.input.addEventListener("input",()=>{(!this.constructor._name||this.constructor._name!=="OOCurrency")&&(this.value=this._elements.input.value,this._elements.viewText.textContent=this._elements.input.value,this.unInvalidStyle())})}_inputChange(){this._elements.input.addEventListener("change",t=>{this._canValidityBlur=!0,this.unInvalidStyle(),this.dispatchEvent(new Event("change",t))})}_fileInputChange(){this._elements.fileInput&&this._elements.fileInput.addEventListener("change",t=>{this._canValidityBlur=!0,this.unInvalidStyle(),this._changeFileValue(t),this.dispatchEvent(new Event("change",t))})}_inputInvalid(){this._elements.input.addEventListener("invalid",t=>{c(this,un,lr).call(this,t.target.validity)})}_setEvent(){super._setEvent(),this._inputFocus(),this._inputBlur(),this._inputInput(),this._inputChange(),this._inputInvalid(),this._fileInputFocus(),this._fileInputBlur(),this._fileInputChange()}_changeFileValue(t){if(this._props.type==="file")if(t.target.files.length){n.empty(this._elements.fileList);for(const e of t.target.files){const s=n("oo-file",{filename:e.name,size:e.size});this._elements.fileList.append(s)}}else n.empty(this._elements.fileList),this._elements.fileList.innerHTML=`<div class="emptyInfo">${this._props.notSelectedFile}</div>`}unInvalidStyle(t){this._elements.input.setCustomValidity(""),n.removeClass(this._elements.input,"invalid"),n.removeClass(this._elements.body,"invalid"),this._elements.invalidHint.textContent="",n.removeClass(this._elements.invalidHint,"show")}setCustomValidity(){return this._elements.input.setCustomValidity(...arguments)}checkValidity(){if(n.isDisplayNone(this))return!0;this.unInvalidStyle();const t=this._elements.input.hasAttribute("readonly");t&&this._elements.input.removeAttribute("readonly"),this.dispatchEvent(new CustomEvent("validity"));const e=this._elements.input.checkValidity(...arguments);return t&&this._elements.input.setAttribute("readonly",!0),n.toggleAttr(this,"valid",e),e}reportValidity(){const t=this._elements.input.hasAttribute("readonly");t&&this._elements.input.removeAttribute("readonly"),this.dispatchEvent(new CustomEvent("validity"));const e=this._elements.input.reportValidity(...arguments);return t&&this._elements.input.setAttribute("readonly",!0),e}_render(){this._setPropMap.viewStyle(),this._setPropMap.value(),this._setPropMap.type()}_connected(){this._setPropMap.labelAlign()}focus(){this._elements.input.focus()}blur(){this._elements.input.blur()}get files(){return this._elements.fileInput.files}get value(){switch(this._props.type){case"number":const t=parseFloat(this._elements.input.value);return isNaN(t)?null:t;default:return this._elements.input.value}}set value(t){this.setAttribute("value",t)}};pn=new WeakSet,or=function(){return this._elements.input.validity.customError||this._props.validity&&this._elements.input.setCustomValidity(this._props.validity),this._elements.invalidHint&&(this._elements.invalidHint.textContent=this._elements.input.validationMessage),this._elements.input.validationMessage},un=new WeakSet,lr=function(t){if(t.valueMissing){const e=Tt.get("index").o("component");this._elements.input.setCustomValidity(e.t("valueMissing",{label:this._props.label||e.this}))}this.dispatchEvent(new CustomEvent("invalid",{detail:t})),n.addClass(this._elements.input,"invalid"),n.addClass(this._elements.body,"invalid"),c(this,pn,or).call(this)&&n.addClass(this._elements.invalidHint,"show")},m(p1,"prop",{leftIcon:"",rightIcon:"",width:"",height:"",inputStyle:"",labelStyle:"",labelAlign:"right",viewStyle:"",disabled:!1,readmode:!1,label:"",bgcolor:"#ffffff",skin:"",appearance:"",selectFile:"Select File",notSelectedFile:"Not Selected Files",validity:"",validityBlur:!1,autoSize:!1,resize:!1,skinMode:"",col:1,del:!0,fileButtonStyle:"simple",fileButtonIcon:"create",placeholder:"",readonly:!1,type:"text",autofocus:!1,form:"",max:"",maxlength:"",min:"",minlength:"",name:"",pattern:"",size:"",spellcheck:"",src:"",step:"",tabindex:"",value:"",title:"",list:"",autocomplete:"",accept:"",alt:"",capture:"",checked:"",dirname:"",formaction:"",formenctype:"",formmethod:"",formnovalidate:"",formtarget:"",multiple:"",required:!1,cols:"",rows:""});let at=p1;const Bo=`.content{--label: var(--oo-color-text2);--option-bg: var(--oo-color-text-white);--option-over: var(--oo-color-text2);--option-over-bg: var(--oo-color-gray-e);--option-select: var(--oo-color-main);--option-select-bg: var(--oo-color-text-white);--option-shadow-color: var(--oo-color-gray-d);--option-shadow: 0 0 10px 1px var(--option-shadow-color);--option-border: var(--oo-color-gray-d);--option-selected: var(--oo-color-main);--option-selected-bg: var(--oo-color-light);--option-group-bg: var(--oo-color-text-white);--option-group-color: var(--oo-color-gray-9)}.content{height:100%;width:100%;position:static}.labelArea{display:flex;position:static;height:100%}.drop{text-align:left;transition:color .2s;font-size:.8em;margin-right:.6em;color:var(--label);cursor:pointer;transform-origin:center}.drop.disabled,.suffix.disabled{opacity:.8;cursor:not-allowed}.box.focus .drop{color:var(--focus)}.calendar-area{position:absolute;border:1px solid var(--option-border);background-color:var(--option-bg);border-radius:var(--oo-area-radius);box-shadow:var(--option-shadow);z-index:100;transform-origin:top;transition:height .2s,opacity .2s,transform .2s}.calendar-area.visible{transform:scale(1);opacity:1}.calendar-area.invisible{transform:scaleY(0);opacity:0}.view.range{display:flex;align-items:center}.input{cursor:pointer}.range .input{flex:1}div.right-icon{cursor:pointer}.viewText{word-break:break-all;white-space:nowrap}.range .viewText{flex:1}.range-separator{color:var(--label)}
`,vi="datetime",bi="date",Ms="year",Ss="month",As="week",_i="time",Li="daterange",xi="yearrange",yi="monthrange",Yo="datetimerange",wi="timerange",x4="dates",ki="years",Ei="months",Wo="YYYY-MM-DD HH:mm:ss",qo="YYYY-MM-DD",Uo="YYYY",Xo="YYYY-MM",Go="YYYY WW",Ko="HH:mm:ss",Jo="HH:mm",gt=class gt extends at{constructor(){super();d(this,mn);d(this,Cn);d(this,u1);d(this,gn);d(this,$e);d(this,fn);d(this,yt,!1);d(this,Y,null);d(this,x2,null);d(this,L,null);d(this,Ct,null);d(this,Ne,"datetime");d(this,K,!1);d(this,Nt,!1);d(this,Oe,"");d(this,Fe,{});d(this,y2,{});c(this,mn,ar).call(this),this._init("oo-datetime",gt),this._useCss(Bo)}static get observedAttributes(){return gt.prop=Object.assign({},gt.prop,gt.calendarProp,gt.rangeProp),Object.keys(gt.prop).map(t=>b(t))}static formatDate(t,e){return O(t,e)}useValue(t){const e=t||this._getValue();l(this,K)?(this._elements.input.value=e[0]||"",this._elements.viewText.textContent=e[0]||"-",this._elements.endInput.value=e[1]||"",this._elements.endViewText.textContent=e[1]||"-"):(this._elements.input.value=e||"",this._elements.viewText.textContent=e||"-")}_checkMode(){const{mode:t}=this._props;u(this,K,[Yo,Li,xi,yi,wi].includes(t)),u(this,Nt,[x4,ki,Ei].includes(t)),!l(this,K)&&!l(this,Nt)&&![vi,bi,Ms,Ss,_i,Li,As].includes(t)&&(console.warn(`Invalid mode: ${t}. Use "${vi}" instead`),this._props.mode=vi),u(this,Ne,l(this,K)?t.slice(0,-5):t),u(this,Oe,l(this,K)||l(this,Nt)?"oo-calendar-range":"oo-calendar"),l(this,L)&&l(this,L).tagName!==l(this,Oe)&&(l(this,L).remove(),u(this,L,null))}_getValue(){let t;try{t=JSON.parse(this._props.value),typeof t=="number"&&(t=t.toString())}catch{t=this._props.value}return l(this,K)||l(this,Nt)?Array.isArray(t)?t.map(e=>this._formatValue(e)):l(this,Nt)?[this._formatValue(t)]:[this._formatValue(t),""]:Array.isArray(t)?this._formatValue(t[0]):this._formatValue(t)}_formatValue(t){try{const e=this._props.format||this._getDefaultFormat();return[Ms,Ss,As,_i,ki,Ei,xi,yi,wi].includes(this._props.mode)?t instanceof Date?O(t,e):t:t4(t)?O(new Date(t),e):""}catch{return""}}_getDefaultFormat(){return[Ms,ki,xi].includes(this._props.mode)&&Uo||[Ss,Ei,yi].includes(this._props.mode)&&Xo||[bi,x4,Li].includes(this._props.mode)&&qo||this._props.mode===As&&Go||[_i,wi].includes(this._props.mode)&&(this._props.secondEnable?Ko:Jo)||Wo}_render(){this._checkMode(),u(this,yt,!1),this._elements.input.setAttribute("readonly","readonly"),u(this,Ct,this.querySelector("div.options-position-node")),l(this,Ct)||(u(this,Ct,n("div.options-position-node.hide")),this._elements.body.insertAdjacentElement("afterbegin",l(this,Ct)),l(this,Ct).addEventListener("click",()=>{c(this,$e,rs).call(this)})),u(this,Y,this.querySelector("div.calendar-area")),l(this,Y)||(u(this,Y,n("div.calendar-area.invisible")),l(this,Ct).append(l(this,Y))),this._createEndElement(),this._checkEndElement(),this._props.readonly=!0,this._setPropMap.viewStyle(),this._setPropMap.value()}_createEndElement(){this._elements.separator=n("span.range-separator"),this._elements.separator.textContent=this._props.separator,this._elements.view.append(this._elements.separator),this._elements.endInput=this._elements.input.cloneNode(!0),this._elements.endInput.addEventListener("click",()=>{this._handleInputClick()}),this._elements.endInput.setAttribute("id","endInput"),this._elements.endInput.setAttribute("placeholder",this._props.endPlaceholder),this._elements.view.append(this._elements.endInput),this._elements.endViewText=this._elements.viewText.cloneNode(!0),this._elements.view.append(this._elements.endViewText)}_checkEndElement(){n.checkClass(this._elements.view,"range",l(this,K)),n.checkClass(this._elements.separator,"hide",!l(this,K)),n.checkClass(this._elements.endInput,"hide",!l(this,K)),n.checkClass(this._elements.endViewText,"hide",!l(this,K))}_setEvent(){super._setEvent(),this._elements.input.addEventListener("click",()=>{this._handleInputClick()}),l(this,Y).addEventListener("click",t=>{t.stopPropagation()}),l(this,Y).addEventListener("mousedown",t=>{t.stopPropagation()}),this._content.firstElementChild.addEventListener("mousedown",t=>{t.stopPropagation()})}_handleInputClick(){!this._props.read&&!this._props.readmode&&!this._props.disabled&&(u(this,yt,!l(this,yt)),l(this,yt)?c(this,gn,hr).call(this):c(this,$e,rs).call(this))}_connected(){this.hasAttribute("right-icon")||this.setAttribute("right-icon","calendar"),this._elements.input.setAttribute("readonly","readonly")}setDatetimeRange(t){l(this,L)?l(this,L).setDatetimeRange(t):l(this,Fe).datetimeRange=t}setDateRange(t){l(this,L)?l(this,L).setDateRange(t):l(this,Fe).dateRange=t}setTimeRange(t){l(this,L)?l(this,L).setTimeRange(t):l(this,Fe).timeRange=t}setCustomRangeMethod(t,e){l(this,L)?l(this,L).setCustomRangeMethod(t,e):l(this,y2)[t]=e}get value(){return this._getValue()}set value(t){this.setAttribute("value",t)}};mn=new WeakSet,ar=function(){this._setPropMap=Object.assign(this._setPropMap,{disabled:()=>{n.toggleAttr(this._elements.input,"disabled",this._props.disabled),n.checkClass(this._elements.box,"disabled",this._props.disabled),n.checkClass(this._elements.label,"disabled",this._props.disabled),n.checkClass(this._elements.suffix,"disabled",this._props.disabled),n.toggleAttr(this._elements.endInput,"disabled",this._props.disabled)},value:()=>{const t=this._getValue();this.useValue(t),l(this,L)&&l(this,L).setAttribute("value",l(this,K)||l(this,Nt)?JSON.stringify(t):t)},format:()=>{l(this,L)&&l(this,L).setAttribute("format",this._props.format||this._getDefaultFormat()),this._setPropMap.value()},mode:()=>{this._checkMode(),l(this,L)&&l(this,L).setAttribute("mode",l(this,Ne)),this._checkEndElement(),this._setPropMap.value()},timeRange:()=>{l(this,L)&&l(this,L).setAttribute("time-range",this._props.timeRange)},read:()=>{},readonly:()=>{},readmode:()=>{n.checkClass(this._elements.box,"readmode",this._props.readmode),n.checkClass(this._elements.label,"readmode",this._props.readmode),this._setPropMap.viewStyle(),this._setPropMap.required()},inputStyle:()=>{n.toggleAttr(this._elements.input,"style",this._props.inputStyle),n.toggleAttr(this._elements.endInput,"style",this._props.inputStyle)},endPlaceholder:()=>{n.toggleAttr(this._elements.endInput,"placeholder",this._props.endPlaceholder)},separator:()=>{this._elements.separator.textContent=this._props.separator||""},$default:t=>{gt.calendarProp.hasOwnProperty(t)?l(this,L)&&l(this,L).setAttribute(b(t),this._props[t]):(n.toggleAttr(this._elements.input,t,this._props[t]),n.toggleAttr(this._elements.endInput,t,this._props[t]))}})},yt=new WeakMap,Y=new WeakMap,x2=new WeakMap,L=new WeakMap,Ct=new WeakMap,Ne=new WeakMap,K=new WeakMap,Nt=new WeakMap,Oe=new WeakMap,Fe=new WeakMap,y2=new WeakMap,Cn=new WeakSet,dr=function(){const t=n.getSize(this._elements.box),e=n.getParentSrcollNode(this._elements.box),s=n.getOffsetParent(this._elements.box),r=n.getSize(e),a=l(this,L)?n.getSize(l(this,L)):{x:0,y:0},h=n.getPosition(s,e),p=n.getPosition(this._elements.box,s);n.setStyles(l(this,Ct),{left:(h.x+p.x+a.x>r.x?r.x-h.x-a.x-20:p.x)+"px"});const C=t.y+6+t.y*.2;n.setStyles(l(this,Y),{top:C+"px"})},u1=new WeakSet,u3=function(){n.checkClass(this._elements.box,"focus",l(this,yt)),n.checkClass(this._elements.label,"focus",l(this,yt))},gn=new WeakSet,hr=function(){u(this,yt,!0),c(this,u1,u3).call(this),n.removeClass(l(this,Ct),"hide"),window.setTimeout(()=>{n.removeClass(l(this,Y),"invisible"),n.addClass(document.body,"ooui-select-visible"),n.addClass(l(this,Y),"visible"),this.ownerDocument.dispatchEvent(new MouseEvent("mousedown")),c(this,fn,cr).call(this),c(this,Cn,dr).call(this),u(this,x2,c(this,$e,rs).bind(this)),this.ownerDocument.addEventListener("mousedown",l(this,x2))},10),this.unInvalidStyle()},$e=new WeakSet,rs=function(){u(this,yt,!1),c(this,u1,u3).call(this),n.removeClass(l(this,Y),"visible"),n.addClass(l(this,Y),"invisible"),n.removeClass(document.body,"ooui-select-visible"),this.ownerDocument.removeEventListener("mousedown",l(this,x2)),window.setTimeout(()=>{n.addClass(l(this,Ct),"hide")},200)},fn=new WeakSet,cr=function(){if(l(this,L))l(this,L).checkEnable();else{let e=`<${l(this,Oe)}`;l(this,Ne)&&(e+=` mode=${l(this,Ne)}`);for(let[p]of Object.entries(gt.calendarProp))v(this._props[p])!=="null"&&!["mode","value"].includes(p)&&(e+=` ${b(p)}='${this._props[p]}'`);e+=`></${l(this,Oe)}>`,n.set(l(this,Y),"html",e),u(this,L,l(this,Y).firstElementChild);const s=this._getValue();l(this,L).value=Array.isArray(s)?JSON.stringify(s):s,l(this,L).addEventListener("change",p=>{this.setAttribute("value",l(this,K)||l(this,Nt)?JSON.stringify(p.detail.value):p.detail.value),this.dispatchEvent(new CustomEvent("change",p)),[bi,Ss,Ms,As].includes(this._props.mode)&&c(this,$e,rs).call(this)});const{datetimeRange:r,dateRange:a,timeRange:h}=l(this,Fe);r?l(this,L).setDatetimeRange(r):(a&&l(this,L).setDateRange(a),h&&l(this,L).setTimeRange(h));for(var t in l(this,y2))l(this,L).setCustomRangeMethod(t,l(this,y2)[t])}},m(gt,"calendarProp",{view:"date",mode:"datetime",baseDate:null,secondEnable:!1,cleanEnable:!0,todayEnable:!0,datetimeRange:"",dateRange:"",timeRange:"",weekBegin:1,step:1,value:"",format:"",rightIcon:"calendar",read:!1}),m(gt,"rangeProp",{endPlaceholder:"",separator:"至"});let Mi=gt;const Qo=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    /* display: flex;\r
    align-items: flex-start;\r
    flex-direction: row; */\r
    \r
    border-radius: var(--oo-default-radius);\r
    height: 100%;\r
    transition: background-color 0.2s;\r
    cursor: pointer;\r
    position: relative;\r
    transition: background-color 0.2s, box-shadow 0.2s;\r
}\r
.content:hover{\r
    background-color: var(--oo-color-hover);\r
    box-shadow: inset 0 0 0 1px var(--oo-color-main);\r
}\r
.del{\r
    opacity: 0;\r
    color: #333333;\r
    transition: opacity 0.2s;\r
    cursor: pointer;\r
    padding: 0.5em;\r
}\r
.content:hover .del{\r
    opacity: 0.5;\r
}\r
.content:hover .del:hover{\r
    opacity: 1;\r
}\r
.area{\r
    display: flex;\r
    align-items: center;\r
    flex-direction: row;\r
    flex: 1;\r
    padding: 0.5em;\r
    border-radius: var(--oo-default-radius);\r
    transition: opacity 0.2s;\r
    opacity: 1;\r
}\r
.content.uploading .area{\r
    opacity: 0.6;\r
    background-color: #f9f9f9;\r
}\r
.content.error .area{\r
    box-shadow: inset 0 0 0 1px var(--oo-color-error);\r
    background-color: var(--oo-color-error-bg);\r
    color: var(--oo-color-error);\r
}\r
.content.error .area .del{\r
    color: var(--oo-color-error);\r
    opacity: 1 !important;\r
}\r
\r
.content.warn .area{\r
    box-shadow: inset 0 0 0 1px var(--oo-color-warn);\r
    background-color: var(--oo-color-warn-bg);\r
    color: var(--oo-color-warn);\r
}\r
.content.warn .area .del{\r
    color: var(--oo-color-warn);\r
}\r
\r
    \r
\r
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
    flex: 1;\r
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
\r
.progress {\r
    height: 3px;\r
    border: 1px solid #ccc;\r
    border-radius: var(--oo-default-radius);\r
    width: 100%;\r
    width: 100%;\r
    position: absolute;\r
    bottom: 0;\r
    opacity: 0;\r
}\r
.content.uploading .progress{\r
    opacity: 1;\r
}\r
\r
/* 针对整个进度条的背景（Chrome, Safari, Edge） */\r
.progress::-webkit-progress-bar {\r
    background-color: #f0f0f0;\r
    border-radius: var(--oo-default-radius);\r
}\r
/* 针对进度条的填充颜色（Chrome, Safari, Edge） */\r
.progress::-webkit-progress-value {\r
    background-color: var(--oo-color-main);\r
    border-radius: var(--oo-default-radius);\r
    transition: width 0.5s;\r
}\r
\r
\r
.content.mobile .del{\r
    opacity: 1 !important;\r
}`,y4={html:"html",htm:"html",jsp:"html",asp:"html",js:"js",mjs:"js",cjs:"js",css:"css",xml:"xml",xsl:"xsl",avi:"avi",mkv:"avi",mov:"avi",ogg:"avi",mp4:"mp4",mpa:"avi",mpe:"avi",mpeg:"avi",mpg:"avi",rmvb:"rm",rm:"rm",doc:"word",docx:"word",dotx:"word",dot:"word",xls:"excel",xlsx:"excel",xlsm:"excel",xlt:"excel",xltx:"excel",pptx:"ppt",ppt:"ppt",pot:"ppt",potx:"ppt",potm:"ppt",mp3:"mp4",wav:"wav",wma:"wma",wmv:"wmv",bmp:"img",gif:"gif",png:"png",psd:"psd",jpeg:"jpeg",jpg:"jpeg",jpe:"jpeg",tiff:"tiff",ico:"jpeg",ai:"ai",apng:"img",avif:"img",cur:"img",jfif:"jpeg",pjpeg:"jpeg",pjp:"jpeg",tif:"tiff",webp:"img",pdf:"pdf",rar:"rar",txt:"txt",zip:"zip",exe:"exe",ofd:"ofd",tmp:"tmp",arch:"arch",att:"att",au:"au",cad:"cad",cdr:"cdr",eps:"eps",iso:"iso",fla:"flash",link:"link",folder:"folder",unknown:"zip"},t9=`* {\r
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
    /* height: calc(100% - 4em - 5em); */\r
    /* overflow: auto; */\r
    display: flex;\r
    align-items: center;\r
}\r
\r
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
`,e9=`
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
`,f1=class f1 extends xs{constructor(){super();d(this,vn);d(this,bn);d(this,_n);d(this,je);d(this,m1);d(this,C1);d(this,g1);m(this,"_elements",{header:null,title:null,actions:null,close:null,body:null,buttons:null,button_ok:null,button_yes:null,button_no:null,button_cancel:null,footer:null,resize:null,bodyIcon:null});d(this,W,{x:0,y:0,positionX:0,positionY:0,styleX:"left",styleY:"top"});d(this,e3,{x:0,y:0,width:0,height:0});const t=Tt.get("index").o("common");["ok","yes","no","cancel"].forEach(e=>f1.prop[e]=t[e]),c(this,vn,pr).call(this),this._init("oo-dialog",f1,e9,t9)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_addHideEvent(){}_removeHideEvent(){}_setEvent(){super._setEvent(),this._elements.close.addEventListener("click",t=>{this.close(t)}),this._elements.button_cancel.addEventListener("click",t=>{c(this,je,os).call(this,"cancel",t),this.stopPropagation||this.dispatchEvent(new CustomEvent("resolvecancel")),this.stopPropagation=!1}),this._elements.button_ok.addEventListener("click",t=>{c(this,je,os).call(this,"ok",t,this._props.closeOnEvent),this.stopPropagation||this.dispatchEvent(new CustomEvent("resolveok")),this.stopPropagation=!1}),this._elements.button_yes.addEventListener("click",t=>{c(this,je,os).call(this,"yes",t,this._props.closeOnEvent),this.stopPropagation||this.dispatchEvent(new CustomEvent("resolveyes")),this.stopPropagation=!1}),this._elements.button_no.addEventListener("click",t=>{c(this,je,os).call(this,"no",t),this.stopPropagation||this.dispatchEvent(new CustomEvent("resolveno")),this.stopPropagation=!1}),this._elements.header.addEventListener("drag",t=>c(this,m1,m3).call(this,t)),this._elements.header.addEventListener("dragend",t=>c(this,g1,g3).call(this,t)),this._elements.header.addEventListener("dragstart",t=>c(this,C1,C3).call(this,t)),this._elements.body.addEventListener("drag",t=>c(this,m1,m3).call(this,t)),this._elements.body.addEventListener("dragend",t=>c(this,g1,g3).call(this,t)),this._elements.body.addEventListener("dragstart",t=>c(this,C1,C3).call(this,t))}close(t){this.dispatchEvent(new MouseEvent("close",t)),this.hide()}_afterHide(){this.remove()}_render(){["ok","yes","no","cancel"].forEach(t=>this._setPropMap[t]()),this._fillContent("content")}_setBodySize(){let t="100%";(this._props.dlgTitle||this._props.canClose)&&(t=t+" - 4em"),this._props.buttons&&(t=t+" - 5em"),this._elements.body.style.height=`calc(${t})`}};vn=new WeakSet,pr=function(){this.setPropMap=Object.assign(this._setPropMap,{dlgTitle:()=>{this._props.dlgTitle?(this._elements.title.textContent=this._props.dlgTitle,n.removeClass(this._elements.header,"hide")):this._props.canClose?n.removeClass(this._elements.header,"hide"):n.addClass(this._elements.header,"hide"),this._setBodySize()},buttons:()=>{if(this._props.buttons){n.removeClass(this._elements.buttons,"hide");const t=this._props.buttons.split(/,\s*/g);["ok","yes","no","cancel"].forEach(e=>{t.includes(e)?n.addClass(this._elements[`button_${e}`],"show"):n.removeClass(this._elements[`button_${e}`],"show")})}else n.addClass(this._elements.buttons,"hide");this._setBodySize()},buttonAlign:()=>{this._elements.buttons&&n.setStyle(this._elements.buttons,"justify-content",this._props.buttonAlign||"flex-end")},canResize:()=>{const t=this._props.canResize?"addClass":"removeClass";n[t](this,"resize")},canMove:()=>{this._elements.header.setAttribute("draggable",!!this._props.canMove),this._elements.body.setAttribute("draggable",!!this._props.canMove)},canClose:()=>{const t=this._props.canClose?"addClass":"removeClass";n[t](this._elements.close,"show"),!this._props.canClose&&!this._props.dlgTitle?n.addClass(this._elements.header,"hide"):n.removeClass(this._elements.header,"hide")},icon:(t,e)=>{this._props.icon?this._elements.bodyIcon&&(n.addClass(this._elements.bodyIcon,"ooicon-"+this._props.icon),n.removeClass(this._elements.bodyIcon,"hide")):this._elements.bodyIcon&&(n.removeClass(this._elements.bodyIcon,"ooicon-"+e),n.addClass(this._elements.bodyIcon,"hide"))},ok:()=>{this._elements.button_ok&&(this._elements.button_ok.textContent=this._props.ok)},yes:()=>{this._elements.button_ok&&(this._elements.button_yes.textContent=this._props.yes)},no:()=>{this._elements.button_ok&&(this._elements.button_no.textContent=this._props.no)},cancel:()=>{this._elements.button_ok&&(this._elements.button_cancel.textContent=this._props.cancel)},okSize:()=>{this._elements.button_ok&&(this._props.okSize?n.setStyle(this._elements.button_ok,"width",this._props.okSize):n.setStyle(this._elements.button_ok,"width","unset"))},size:()=>{this._props.size==="small"?(n.addClass(this._elements.header,"small"),n.addClass(this._elements.buttons,"small")):(n.removeClass(this._elements.header,"small"),n.removeClass(this._elements.buttons,"small"))},closeOnEvent:()=>{}})},W=new WeakMap,bn=new WeakSet,ur=function(){const t={x:0,y:0,positionX:0,positionY:0};t.styleX=this.style.right?"right":"left",t.styleY=this.style.bottom?"bottom":"top";const e=n.getOffsetParent(this),s=n.getPosition(this,e),r=n.getSize(e),a=n.getSize(this);return t.positionX=t.styleX==="right"?r.x-(s.x+a.x):s.x,t.positionY=t.styleY==="bottom"?r.y-(s.y+a.y):s.y,t},e3=new WeakMap,_n=new WeakSet,mr=function(t,e){const s=new MouseEvent(t,Object.assign({},e,{bubbles:!1})),r=s.preventDefault;s.preventDefault=()=>{this.preventDefault=!0,r.apply(s)};const a=s.stopPropagation;s.stopPropagation=()=>{this.stopPropagation=!0,a.apply(s)};const h=s.stopImmediatePropagation;return s.stopImmediatePropagation=()=>{this.stopPropagation=!0,h.apply(s)},s},je=new WeakSet,os=function(t,e,s=!0){const r=c(this,_n,mr).call(this,t,e);this.dispatchEvent(r),!this.preventDefault&&s&&this.close(e),this.preventDefault=!1},m1=new WeakSet,m3=function(t){if(t.screenX||t.screenY){const e=t.screenX-l(this,W).x,s=t.screenY-l(this,W).y,r=l(this,W).styleX==="right"?l(this,W).positionX-e:l(this,W).positionX+e,a=l(this,W).styleY==="bottom"?l(this,W).positionY-s:l(this,W).positionY+s,h={};h[l(this,W).styleX]=r+"px",h[l(this,W).styleY]=a+"px",n.setStyles(this,h),this.dispatchEvent(new DragEvent("move",t))}},C1=new WeakSet,C3=function(t){u(this,W,c(this,bn,ur).call(this)),l(this,W).x=t.screenX,l(this,W).y=t.screenY,this.dispatchEvent(new DragEvent("startMove",t))},g1=new WeakSet,g3=function(t){this.dispatchEvent(new DragEvent("endMove",t))},m(f1,"prop",{css:"",cssLink:"",skin:"",isShow:!1,dlgTitle:"",canResize:!1,canMove:!0,canClose:!0,icon:"",closeOnEvent:!0,buttons:"ok, cancel",ok:"OK",yes:"Yes",no:"No",cancel:"Cancel",buttonAlign:"flex-end",size:"default",okSize:""});let Si=f1;customElements.get("oo-dialog")||customElements.define("oo-dialog",Si);class w4{constructor(i,t){d(this,Ln);d(this,v1);d(this,b1);d(this,_1);d(this,xn);m(this,"options",{css:"",cssLink:"",skin:"",isShow:!1,title:"",canResize:!1,canMove:!0,canClose:!0,icon:"",buttons:"ok, cancel",content:null,position:"center center",offset:{x:0,y:0},zIndex:100,modal:!0,modalArea:null,width:"",height:"",maxWidth:"",events:{},buttonAlign:"flex-end",positionNode:null,okSize:"",closeOnModalClick:!1});m(this,"dialogEvents",[]);const e=Event.prototype.isPrototypeOf(i)?n.getPositionParent(i.currentTarget):i;this.options=Object.assign(this.options,t),this.container=e||document.body,this.positionNode=this.options.positionNode||this.container,this.area=this.options.area||this.container,this.options.modal&&!this.options.modalArea&&(this.options.modalArea=this.container),this.options.dlgTitle=this.options.title}async show(i){var t,e;i&&(this.options.position=i),this.dialog||this._createDialog(),await this._positionDialog(),(t=this.options)!=null&&t.attr&&n.set(this.dialog,(e=this.options)==null?void 0:e.attr),this.dialog.show(),this.options.modal&&this.options.modalArea&&this._showModal()}hide(){this.dialog.hide()}close(){this.dialog.close(),this.dialog=null}_showModal(){const i=n.getStyle(this.dialog,"z-index");this.modalNode=n("div.oo-dialog-modal"),this.dialog.insertAdjacentElement("beforebegin",this.modalNode);const t=n.getOffsetParent(this.modalNode);if(t!==this.options.modalArea){const e=n.getPosition(this.options.modalArea,t),s=e.x+"px",r=e.y+"px";n.setStyles(this.modalNode,{left:s,top:r})}n.setStyles(this.modalNode,{opacity:.3,"z-index":i-1}),this.modalNode.addEventListener("click",e=>{this.options.closeOnModalClick&&this.close()})}_createDialog(){this.dialog=n("oo-dialog",{styles:{position:"absolute"}}),["css","cssLink","skin","isShow","okSize","dlgTitle","canResize","canMove","canClose","icon","buttons","ok","yes","no","cancel","buttonAlign","closeOnEvent"].forEach(t=>{this.options.hasOwnProperty(t)&&this.dialog.setAttribute(b(t),this.options[t])}),this.options.events&&Object.keys(this.options.events).forEach(t=>{this.dialog.addEventListener(t,this.options.events[t])}),this.dialogEvents&&this.dialogEvents.length&&this.dialogEvents.forEach(({type:t,listener:e})=>{this.dialog.addEventListener(t,e)});const i=this.options.zIndex||100;n.setStyle(this.dialog,"z-index",i),c(this,v1,f3).call(this,this.options.content),this.container.insertAdjacentElement("beforeend",this.dialog),this.options.width&&n.setStyle(this.dialog,"width",this.options.width),this.options.height&&n.setStyle(this.dialog,"height",this.options.height),this.options.maxWidth&&n.setStyle(this.dialog,"max-width",this.options.maxWidth),this.dialog.addEventListener("hide",()=>{c(this,Ln,Cr).call(this)})}async _getDialogPosition(){const i=await new Promise(r=>{requestAnimationFrame(()=>{r(n.getSize(this.dialog))})});i.y<300&&this.dialog.setAttribute("size","small");const t=this.options.position||"center center",e=t.split(/\s+/),s=c(this,xn,gr).call(this,{x:e[0],y:e.length>1?e[1]:e[0]},i);return s["transform-origin"]=t,s}async _positionDialog(){if(this.options.position&&this.options.position!=="none"){const i=await this._getDialogPosition();n.setStyles(this.dialog,i)}}addEventListener(i,t){this.dialog?this.dialog.addEventListener(i,t):this.dialogEvents.push({type:i,listener:t})}}Ln=new WeakSet,Cr=function(){this.modalNode&&(this.modalNode.addEventListener("transitionend",i=>{this.modalNode.remove(),this.modalNode=null}),n.setStyles(this.modalNode,{opacity:0}))},v1=new WeakSet,f3=function(i){if(i){switch(v(i)){case"element":this.dialog.append(i);break;case"string":this.dialog.append(n("div",{html:q0(i)}));break;case"promise":i.then(e=>{c(this,v1,f3).call(this,e)}).catch(()=>{});break}this.dialog._setContent("content")}},b1=new WeakSet,v3=function(i,t,e,s){const r=i==="y"?"top":"left",a=`calc(100% - ${e[i]}px)`,h=t[i]==="center"?`50% - ${e[i]/2}px`:"0px",p=t[i]==="center"?r:t[i],C={};return C[p]=`clamp(0%, calc(${h} + ${(this.options.offset[i]||0)+"em"} + ${s[i]}px), ${a})`,C},_1=new WeakSet,b3=function(i,t,e,s,r){switch(i[t]){case"center":return r-e[t]/2+s[t]/2;case"left":case"top":return r;case"right":case"bottom":return e[t]-r-s[t];default:return 0}},xn=new WeakSet,gr=function(i,t){const e=this.options.positionNode&&Event.prototype.isPrototypeOf(this.options.positionNode)?this.options.positionNode.target:this.options.positionNode,s={x:0,y:0};if(e){const{x:r,y:a}=n.getPosition(e,this.container),h=n.getSize(e),p=n.getSize(this.container);s.x=c(this,_1,b3).call(this,i,"x",p,h,r),s.y=c(this,_1,b3).call(this,i,"y",p,h,a)}return{...c(this,b1,v3).call(this,"x",i,t,s),...c(this,b1,v3).call(this,"y",i,t,s)}};function k4(o,i,t,e={}){const s=t||document.body,r={title:o,content:i},a=e.buttons||"ok, cancel",h=new w4(s,Object.assign(r,e,{closeOnEvent:!1}));h.show();const p=new Promise(C=>{a.split(/,\s*/g).forEach(g=>{h.addEventListener("resolve"+g,f=>{C({dlg:h,status:g})})})});return p.dlg=h,p}const E4={async msg(o,i,t,e,s="right top",r={},a="info"){const h=t||document.body;return e||(s="center center"),await k4(o,i,h,Object.assign({canClose:!1,maxWidth:"40em"},r,{positionNode:e,position:s},{info:{icon:"info",skin:""},error:{icon:"error",skin:"icon-color: var(--oo-color-error)"},warn:{icon:"help",skin:"icon-color: var(--oo-color-warn)"},success:{icon:"check",skin:"icon-color: var(--oo-color-success)"}}[a]))},async info(o,i,t,e,s="right top",r={}){return await this.msg(...arguments)},async warn(o,i,t,e,s="right top",r={}){return await this.msg(o,i,t,e,s,r,"warn")},async error(o,i,t,e,s="right top",r={}){return await this.msg(o,i,t,e,s,r,"error")},async success(o,i,t,e,s="right top",r={}){return await this.msg(o,i,t,e,s,r,"success")}},s9=(o,i)=>{const t=o[i];return t?typeof t=="function"?t():Promise.resolve(t):new Promise((e,s)=>{(typeof queueMicrotask=="function"?queueMicrotask:setTimeout)(s.bind(null,new Error("Unknown variable dynamic import: "+i)))})},n9=`<div class="container info">\r
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
`,kn=class kn extends N{constructor(){super();d(this,yn);d(this,wn);m(this,"_elements",{container:null,content:null,icon:null,title:null,text:null,close:null});d(this,qt,null);m(this,"_setPropMap",{title:()=>{this._elements.title.textContent=this._props.title},text:()=>{this._elements.text.textContent=this._props.text},icon:()=>{n.toggleClass(this._elements.icon,`ooicon-${this._props.icon}`,this._props.icon)},titleStyle:()=>{this._elements.title.setAttribute("style",this._props.titleStyle)},textStyle:()=>{this._elements.text.setAttribute("style",this._props.textStyle)},type:()=>{c(this,wn,vr).call(this)},skin:()=>{c(this,yn,fr).call(this).then()},showClose:()=>{n[this._props.showClose?"removeClass":"addClass"](this._elements.close,"hide")},width:()=>{this._props.width&&n.setStyle(this._elements.container,"width",this._props.width)},height:()=>{this._props.width&&n.setStyle(this._elements.container,"height",this._props.height)},contentAlign:()=>{this._props.contentAlign?n.addClass(this._elements.content,"align_"+this._props.contentAlign):n.removeClass(this._elements.content,"align_left align_right align_center")},contentValign:()=>{this._props.contentValign?n.addClass(this._elements.content,"align_"+this._props.contentValign):n.removeClass(this._elements.content,"valign_left valign_right valign_center")},style:()=>{n.toggleAttr(this._elements.container,"style",this._props.style)},$default:t=>{n.toggleAttr(this._elements.container,t,this._props[t])}});this._initialize("oo-notice",kn,n9)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_setEvent(){this._elements.close.addEventListener("click",()=>{this.style.opacity=0,window.setTimeout(()=>{this.remove()},200)})}};qt=new WeakMap,yn=new WeakSet,fr=async function(){l(this,qt)&&(l(this,qt).remove(),u(this,qt,null)),u(this,qt,document.createElement("style")),l(this,qt).textContent=!this._props.skin||this._props.skin==="default"?(await Promise.resolve().then(()=>D4)).default:(await s9(Object.assign({"./template/notice.banner.scope.css":()=>Promise.resolve().then(()=>q9),"./template/notice.default.scope.css":()=>Promise.resolve().then(()=>D4)}),`./template/notice.${this._props.skin}.scope.css`)).default,this.shadowRoot.prepend(l(this,qt)),this.dispatchEvent(new CustomEvent("skinLoaded"))},wn=new WeakSet,vr=function(){const t="container "+this._props.type||"info";if(this._elements.container.setAttribute("class",t),!this._props.icon){const s="icon "+{info:"ooicon-info",error:"ooicon-error",warn:"ooicon-help",success:"ooicon-check"}[this._props.type]||"ooicon-info";this._elements.icon.setAttribute("class",s)}},m(kn,"prop",{title:"",text:"",icon:"",titleStyle:"",textStyle:"",showClose:!0,type:"info",skin:"default",contentAlign:"",contentValign:"",width:"",height:""});let Ai=kn;customElements.get("oo-notice")||customElements.define("oo-notice",Ai);const J=class J{constructor(i){d(this,En);d(this,L1);d(this,Mn);d(this,Sn);d(this,x1);d(this,An);d(this,w2);d(this,Tn);d(this,Dn);d(this,Nn);m(this,"options",{title:"",text:"",icon:"",type:"info",skin:"default",style:"",showClose:!0,contentAlign:"",contentValign:"",width:"",height:"",container:"",duration:5e3,location:"topCenter",position:"absolute",marginTop:10,marginBottom:10,marginLeft:10,marginRight:10});m(this,"where",{x:"center",y:"top"});m(this,"location",{fromTop:null,toTop:null,fromBottom:null,toBottom:null,fromLeft:null,toLeft:null,fromRight:null,toRight:null});d(this,Pn,{bottom:(i,t)=>{const e=n.getSize(this.node);this.location.toBottom=J.env.height+this.options.marginTop+this.options.marginBottom+t.y,this.location.fromBottom=this.location.toBottom-e.y,J.env.height+=e.y+this.options.marginTop+this.options.marginBottom},top:(i,t)=>{const e=n.getSize(this.node);this.location.toTop=J.env.height+this.options.marginTop+t.y,this.location.fromTop=this.location.toTop-e.y,J.env.height+=e.y+this.options.marginTop+this.options.marginBottom},middle:()=>{}});d(this,In,{left:(i,t)=>{this.location.fromLeft=this.location.toLeft=t.x+this.options.marginLeft+this.options.marginRight},right:(i,t)=>{const e=n.getSize(i),r=n.getSize(n.getOffsetParent(i)).x-e.x-t.x;this.location.fromRight=this.location.toRight=r+this.options.marginRight},center:(i,t)=>{if(n.isHtml(i))n.addClass(this.node,"body-center");else{n.removeClass(this.node,"body-center");const e=n.getSize(i),s=n.getSize(this.node);this.location.fromLeft=this.location.toLeft=t.x+e.x/2-s.x/2}}});this.options=Object.assign(this.options,i),this.container=this.options.container&&n.el(this.options.container)||document.body;const t=n.isBody(this.container)?"fixed":this.options.position||"absolute";this.node=n("oo-notice."+this.options.skin+(t==="fixed"?".fixed":".absolute")),["title","text","icon","type","skin","style","showClose","contentAlign","contentValign","width","height"].forEach(e=>{this.node.setAttribute(e,this.options[e])}),this.container.append(this.node),this.node.addEventListener("skinLoaded",()=>{c(this,Sn,Lr).call(this)})}resetPosition(i){const t=i+this.options.marginTop+this.options.marginBottom,e=ei(this.where.y);this.location[`to${e}`]=this.location[`to${e}`]-t,this.location[`from${e}`]=this.location[`from${e}`]-t;const s={};s[this.where.y]=this.location[`to${e}`]+"px",c(this,w2,U5).call(this,s,!0)}};En=new WeakSet,br=function(){return J.env.notices.includes(this)||(J.env.notices.push(this),c(this,Tn,yr).call(this),this.node._elements.close&&this.node._elements.close.addEventListener("click",()=>{c(this,x1,L3).call(this)})),c(this,L1,_3).call(this)},L1=new WeakSet,_3=function(i){return c(this,w2,U5).call(this,{top:this.location.fromTop+"px",left:this.location.fromLeft+"px",bottom:this.location.fromBottom+"px",right:this.location.fromRight+"px",opacity:0},i)},Mn=new WeakSet,_r=function(i){return c(this,w2,U5).call(this,{top:this.location.toTop+"px",left:this.location.toLeft+"px",bottom:this.location.toBottom+"px",right:this.location.toRight+"px",opacity:"1"},i)},Sn=new WeakSet,Lr=async function(){await c(this,En,br).call(this),await c(this,Mn,_r).call(this,!0)&&this.options.duration&&setTimeout(()=>{c(this,x1,L3).call(this)},this.options.duration||3e3)},x1=new WeakSet,L3=async function(){if(!this.isHidden){this.isHidden=!0,c(this,An,xr).call(this);const i=await c(this,L1,_3).call(this,!0);i&&i.propertyName==="opacity"&&(this.node.remove(),this.node=null)}},An=new WeakSet,xr=function(){const i=J.env.notices.indexOf(this),t=ei(this.where.y),e=Math.abs(this.location[`to${t}`]-this.location[`from${t}`]);J.env.height=J.env.height-e-this.options.marginTop-this.options.marginBottom;for(let s=i+1;s<J.env.notices.length;s++)J.env.notices[s].resetPosition(e);J.env.notices.splice(i,1)},w2=new WeakSet,U5=function(i,t){return new Promise(e=>{if(t){n.addClass(this.node,"transition");const s=r=>{n.removeClass(this.node,"transition"),this.node.removeEventListener("transitionend",s),e(r)};this.node.addEventListener("transitionend",s),setTimeout(()=>{n.setStyles(this.node,i)},10)}else n.removeClass(this.node,"transition"),setTimeout(()=>{n.setStyles(this.node,i),e()},10)})},Tn=new WeakSet,yr=function(){const i=c(this,Nn,kr).call(this),t=n.isBody(this.container)?document.documentElement:this.container,e=c(this,Dn,wr).call(this,t);l(this,Pn)[i.y](t,e),l(this,In)[i.x](t,e)},Dn=new WeakSet,wr=function(i,t,e){const s=n("div");i.append(s);const r=n.getOffsetParent(s);return s.remove(),r===i?{x:0,y:0}:(()=>n.getPosition(i,n.getOffsetParent(i)))()},Pn=new WeakMap,In=new WeakMap,Nn=new WeakSet,kr=function(){const i={left:"x",right:"x",center:"x",top:"y",bottom:"y",middle:"y"};return b(this.options.location).split("-").forEach(t=>{i[t]&&(this.where[i[t]]=t)}),this.where},m(J,"env",{notices:[],height:0});let H2=J;const Zt={error:(o,i,t={},e)=>{e&&(t=Object.assign(t,{skin:"default",location:"topRight",marginTop:10,duration:5e3}));const r=Object.assign({title:o,text:i,duration:0,skin:"banner",marginTop:0,type:"error"},t);new H2(r);let a;throw a=new Error(i,{cause:r.err}),a.name=o,a},msg:(o,i,t,e={})=>{const s=Object.assign({title:o,text:i,duration:5e3,type:t,location:"topRight",marginTop:10},e);new H2(s)},failed:(o,i,t={})=>{const e=Object.assign({duration:8e3},t);Zt.msg(o,i,"error",e)},success:(o,i,t={})=>{Zt.msg(o,i,"success",t)},info:(o,i,t={})=>{Zt.msg(o,i,"info",t)},warn:(o,i,t={})=>{Zt.msg(o,i,"warn",t)}},i9=`
<div class="content normal">
    <div class="area">
        <div class="icon">
            <oo-icon></oo-icon>
        </div>
        <div class="filename">
            <div class="name"></div>
            <div class="size"></div>
        </div>
        <div class="actions">
            <oo-button class="del" type="icon" left-icon="delete"></oo-button>

        </div>
<!--   <div class="preview hide">预览</div>-->
    </div>
    <progress class="progress" max="100" value="0"></progress>
</div>
`,w1=class w1 extends N{constructor(){super();d(this,ze);d(this,On);d(this,Fn);d(this,$n);d(this,jn);d(this,zn);m(this,"_elements",{icon:null,name:null,size:null,preview:null,del:null,progress:null});m(this,"_setPropMap",{progress:()=>{this._elements.progress.value=this._props.progress||0},status:()=>{this._content.className=`content ${this._props.status}`},filename:()=>{c(this,ze,ls).call(this)},size:()=>{c(this,ze,ls).call(this)},url:()=>{c(this,ze,ls).call(this)},preview:()=>{c(this,ze,ls).call(this)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},del:()=>{n.checkClass(this._elements.del,"hide",!this._props.del)},skinMode:t=>{t&&n.removeClass(this._content,t),this._props.skinMode&&n.addClass(this._content,this._props.skinMode)},previewUrl:()=>{},download:()=>{}});d(this,y1,null);d(this,Ot,null);const t=Tt.get("index").o("component");["delTitle","delComfirm"].forEach(e=>w1.prop[e]=t[e]),this._initialize("oo-file",w1,i9,Qo)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}async _render(){if(this._props.filename){const t=this._props.size?c(this,zn,Tr).call(this,parseFloat(this._props.size)):"";this.extension=this._props.filename.substring(this._props.filename.lastIndexOf(".")+1),n.empty(this._elements.icon);const e=n("oo-icon");e.setAttribute("type",y4[this.extension]||y4.unknown),this._elements.icon.append(e),this._elements.name.textContent=this._props.filename,this._elements.size.textContent=t,n.checkClass(this._elements.del,"hide",!this._props.del)}}_setEvent(){super._setEvent(),this._elements.icon.addEventListener("click",t=>{t.stopPropagation(),(this._props.preview&&this._props.url||this._props.previewUrl)&&c(this,On,Er).call(this)}),this._elements.del.addEventListener("click",t=>{if(t.stopPropagation(),this._props.status==="error")this._remove();else{const e=this._props.delComfirm.replace("{name}",this._props.filename);E4.info(this._props.delTitle,e,this._content,this._content).then(s=>{s.status==="ok"&&this._remove(),s.dlg.close()})}}),this._elements.name.addEventListener("click",t=>{this._props.download&&this._props.url&&J3(this._props.url)})}_removeFile(){return this.files?this.files._remove(this):Promise.resolve()}_remove(){return this._removeFile().then(()=>{this.dispatchEvent(new CustomEvent("remove")),this.remove()})}_getPreviewType(){return c(this,Fn,Mr).call(this,this.extension)?"Image":c(this,$n,Sr).call(this,this.extension)?"Video":c(this,jn,Ar).call(this,this.extension)?"Audio":""}_canPreview(){return(this._props.preview&&this._props.url||this._props.previewUrl)&&!!this._getPreviewType()}_previewPrev(t,e){let s=t.indexOf(l(this,Ot)),r=t[--s];for(;r&&!r._canPreview();)r=t[--s];r&&(u(this,Ot,r),e.src=r._props.previewUrl||r._props.url)}_previewNext(t,e){let s=t.indexOf(l(this,Ot)),r=t[++s];for(;r&&!r._canPreview();)r=t[++s];r&&(u(this,Ot,r),e.src=r._props.previewUrl||r._props.url)}_previewImage(t){t.addEventListener("click",e=>{e.stopPropagation()}),u(this,Ot,this),t.src=this._props.previewUrl||this._props.url}_previewVideo(t){t.setAttribute("controls","play"),t.addEventListener("click",e=>{e.stopPropagation()}),u(this,Ot,this),t.src=this._props.previewUrl||this._props.url}_previewAudio(t){t.setAttribute("controls","play"),t.addEventListener("click",e=>{e.stopPropagation()}),u(this,Ot,this),t.src=this._props.previewUrl||this._props.url}};y1=new WeakMap,ze=new WeakSet,ls=function(){l(this,y1)||setTimeout(()=>{this._render(),u(this,y1,null)},5)},On=new WeakSet,Er=function(){const t=this._getPreviewType();if(t){const e=n("div.oofile-preview-mask"),s=n("div.oofile-preview-area"),r=n.getPosition(this._elements.icon);n.setStyles(e,{top:r.y+"px",left:r.x+"px"}),n.setStyles(s,{top:r.y+"px",left:r.x+"px"}),document.body.append(e),document.body.append(s),setTimeout(()=>{n.addClass(e,"show"),n.addClass(s,"show")},5),s.addEventListener("click",()=>{this.previewFile=null,this.keyPreviewFun&&document.removeEventListener("keydown",this.keyPreviewFun),e.remove(),s.remove()});const a=n("oo-button.oofile-preview-prev",{type:"icon","left-icon":"arrow_back"}),h=n("img.oofile-preview"),p=n("oo-button.oofile-preview-next",{type:"icon","left-icon":"arrow_forward"});if(s.append(a),s.append(h),s.append(p),!this.files)a.addClass("hide"),p.addClass("hide");else{const g=[...this.files._elements.fileList.querySelectorAll("oo-file").values()];a.addEventListener("click",f=>{f.stopPropagation(),this._previewPrev(g,h)}),p.addEventListener("click",f=>{f.stopPropagation(),this._previewNext(g,h)}),s.focus(),this.keyPreviewFun=f=>{f.keyCode===37&&this._previewPrev(g,h),f.keyCode===39&&this._previewNext(g,h)},document.addEventListener("keydown",this.keyPreviewFun)}this[`_preview${t}`](h)}else this._props.previewUrl&&J3(this._props.previewUrl)},Ot=new WeakMap,Fn=new WeakSet,Mr=function(t){return["apng","avif","bmp","gif","ico","cur","jpg","jpeg","jfif","pjpeg","pjp","png","svg","tif","tiff","webp"].includes(t.toString().toLowerCase())},$n=new WeakSet,Sr=function(t){return["avi","mkv","mov","ogg","mp4","mpa","mpe","mpeg","mpg","rmvb","rm"].includes(t.toString().toLowerCase())},jn=new WeakSet,Ar=function(t){return["mp3","wav","wma","wmv"].includes(t.toString().toLowerCase())},zn=new WeakSet,Tr=function(t){if(!t)return"";const e=["B","KB","MB","GB"];let s=0;for(;t>=1024&&s<e.length-1;)t/=1024,s++;return t.toFixed(2)+" "+e[s]},m(w1,"prop",{filename:"",size:"",preview:!0,previewUrl:"",download:!0,url:"",cssLink:"",skin:"",del:!1,status:"normal",progress:0,delComfirm:"",delTitle:"",skinMode:""});let Ti=w1;const r9=`
<div class="content">
</div>
`,o9=Object.assign({"../../assets/icons/ai.svg":()=>Promise.resolve().then(()=>U9).then(o=>o.default),"../../assets/icons/arch.svg":()=>Promise.resolve().then(()=>X9).then(o=>o.default),"../../assets/icons/att.svg":()=>Promise.resolve().then(()=>G9).then(o=>o.default),"../../assets/icons/au.svg":()=>Promise.resolve().then(()=>K9).then(o=>o.default),"../../assets/icons/avi.svg":()=>Promise.resolve().then(()=>J9).then(o=>o.default),"../../assets/icons/cad.svg":()=>Promise.resolve().then(()=>Q9).then(o=>o.default),"../../assets/icons/cdr.svg":()=>Promise.resolve().then(()=>t8).then(o=>o.default),"../../assets/icons/css.svg":()=>Promise.resolve().then(()=>e8).then(o=>o.default),"../../assets/icons/eps.svg":()=>Promise.resolve().then(()=>s8).then(o=>o.default),"../../assets/icons/excel.svg":()=>Promise.resolve().then(()=>n8).then(o=>o.default),"../../assets/icons/exe.svg":()=>Promise.resolve().then(()=>i8).then(o=>o.default),"../../assets/icons/flash.svg":()=>Promise.resolve().then(()=>r8).then(o=>o.default),"../../assets/icons/folder.svg":()=>Promise.resolve().then(()=>o8).then(o=>o.default),"../../assets/icons/gif.svg":()=>Promise.resolve().then(()=>l8).then(o=>o.default),"../../assets/icons/html.svg":()=>Promise.resolve().then(()=>a8).then(o=>o.default),"../../assets/icons/ico.svg":()=>Promise.resolve().then(()=>d8).then(o=>o.default),"../../assets/icons/img.svg":()=>Promise.resolve().then(()=>h8).then(o=>o.default),"../../assets/icons/iso.svg":()=>Promise.resolve().then(()=>c8).then(o=>o.default),"../../assets/icons/java.svg":()=>Promise.resolve().then(()=>p8).then(o=>o.default),"../../assets/icons/jpeg.svg":()=>Promise.resolve().then(()=>u8).then(o=>o.default),"../../assets/icons/js.svg":()=>Promise.resolve().then(()=>m8).then(o=>o.default),"../../assets/icons/link.svg":()=>Promise.resolve().then(()=>C8).then(o=>o.default),"../../assets/icons/mp3.svg":()=>Promise.resolve().then(()=>g8).then(o=>o.default),"../../assets/icons/mp4.svg":()=>Promise.resolve().then(()=>f8).then(o=>o.default),"../../assets/icons/ofd.svg":()=>Promise.resolve().then(()=>v8).then(o=>o.default),"../../assets/icons/pdf.svg":()=>Promise.resolve().then(()=>b8).then(o=>o.default),"../../assets/icons/png.svg":()=>Promise.resolve().then(()=>_8).then(o=>o.default),"../../assets/icons/ppt.svg":()=>Promise.resolve().then(()=>L8).then(o=>o.default),"../../assets/icons/psd.svg":()=>Promise.resolve().then(()=>x8).then(o=>o.default),"../../assets/icons/rar.svg":()=>Promise.resolve().then(()=>y8).then(o=>o.default),"../../assets/icons/rm.svg":()=>Promise.resolve().then(()=>w8).then(o=>o.default),"../../assets/icons/svg.svg":()=>Promise.resolve().then(()=>k8).then(o=>o.default),"../../assets/icons/tiff.svg":()=>Promise.resolve().then(()=>E8).then(o=>o.default),"../../assets/icons/tmp.svg":()=>Promise.resolve().then(()=>M8).then(o=>o.default),"../../assets/icons/txt.svg":()=>Promise.resolve().then(()=>S8).then(o=>o.default),"../../assets/icons/unknown.svg":()=>Promise.resolve().then(()=>A8).then(o=>o.default),"../../assets/icons/wav.svg":()=>Promise.resolve().then(()=>T8).then(o=>o.default),"../../assets/icons/wma.svg":()=>Promise.resolve().then(()=>D8).then(o=>o.default),"../../assets/icons/wmv.svg":()=>Promise.resolve().then(()=>P8).then(o=>o.default),"../../assets/icons/word.svg":()=>Promise.resolve().then(()=>I8).then(o=>o.default),"../../assets/icons/xml.svg":()=>Promise.resolve().then(()=>N8).then(o=>o.default),"../../assets/icons/xsl.svg":()=>Promise.resolve().then(()=>O8).then(o=>o.default),"../../assets/icons/zip.svg":()=>Promise.resolve().then(()=>F8).then(o=>o.default)}),Zn=class Zn extends N{constructor(){super();m(this,"_setPropMap",{type:()=>{this._render()}});this._initialize("oo-icon",Zn,r9,"svg{width:100%; height: 100%}")}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}async _render(){if(n.empty(this._content),this._props.type){const t=await o9[`../../assets/icons/${this._props.type}.svg`]();n.empty(this._content),this._content.insertAdjacentHTML("afterbegin",t)}}};m(Zn,"prop",{type:""});let Di=Zn;class l9 extends at{constructor(){super(),this._init("oo-input")}}const k2=class k2 extends at{constructor(){super();d(this,Rn);d(this,Vn);d(this,Hn);d(this,Q,[]);this.setAttribute("type","file"),c(this,Rn,Dr).call(this),this._init("oo-files")}static get observedAttributes(){return k2.prop=Object.assign({},at.prop,k2.prop),Object.keys(k2.prop).map(t=>b(t))}_inputInput(){this._elements.input.addEventListener("input",()=>{this.unInvalidStyle()})}_fileInputChange(){this._elements.fileInput&&this._elements.fileInput.addEventListener("change",t=>{this._canValidityBlur=!0,this.unInvalidStyle(),this._changeFileValue(t)})}_setEvent(){super._setEvent(),this.addEventListener("validity",t=>{this._props.min&&this.files.length<this._props.min&&t.target.setCustomValidity(`文件数量不能少于${this._props.min} `)})}_changeFileValue(t){if(this._props.type==="file"){if(t.target.files.length){const e=[],s=this._props.max?parseInt(this._props.max):0;for(const r of t.target.files)if(l(this,Q).findIndex(h=>h.name===r.name)===-1){const h=[];s===1&&this._elements.fileList.querySelectorAll("oo-file").forEach(p=>{h.push(p._remove())}),h.length?e.push(Promise.all(h).then(()=>this.addFile(r))):e.push(this.addFile(r))}Promise.allSettled(e).then(r=>{this.dispatchEvent(new CustomEvent("upload",{detail:{results:r}}))})}this._elements.fileInput.value=""}}_removeFile(t,e){return this.removeFile?this.removeFile(t,e):Promise.resolve()}_remove(t){const e=t.file;return this._removeFile(e,t).then(()=>{const s=l(this,Q).findIndex(a=>a.name===e.name);s!=-1&&l(this,Q).splice(s,1),this._elements.input.value=l(this,Q).map(a=>a.name).join(", "),this.dispatchEvent(new CustomEvent("removefile",{detail:{file:e,fileNode:t}})),this.dispatchEvent(new CustomEvent("change",{detail:{file:e,fileNode:t}})),l(this,Q).length||n.show(this._elements.emptyInfo);const r=this._props.max?parseInt(this._props.max):0;n.checkClass(this._elements.fileButton,"hide",r&&r>1&&l(this,Q).length>=r),this.checkValidity()}).catch(s=>{throw Zt.failed("文件删除错误",`${s.message}`,{container:this._elements.box}),t.setAttribute("status","warn"),s})}_checkFile(t){if(!t.name||!t.size||!t.url&&!(t instanceof File))return!1;const e=this._props.size?parseFloat(this._props.size):0;if(e&&t.size>e*1024*1024)return $(c(this,Hn,Ir),20,this,[this._props.size]),!1;const s=this._props.max?parseInt(this._props.max):0;return n.checkClass(this._elements.fileButton,"hide",s&&s>1&&l(this,Q).length+1>=s),s&&l(this,Q).length>=s?($(c(this,Vn,Pr),20,this,[s]),!1):!0}_uploadFile(t,e){return this.uploadFile&&t instanceof File?this.uploadFile(t,e):Promise.resolve()}addFile(t,e){if(this._checkFile(t)){const s=e||n("oo-file",{filename:t.name,size:t.size,url:t.url??"","preview-url":t.previewUrl??"",preview:!0}),r=!(this._props.readonly||this._props.readmode||this._props.disabled)&&this._props.del;return s.setAttribute("del",r),s.setAttribute("skin-mode",this._props.skinMode),s.files=this,s.file=t,this._elements.fileList.append(s),this.unInvalidStyle(),this._elements.emptyInfo&&n.hide(this._elements.emptyInfo),this._elements.fileEmptyFlag&&n.hide(this._elements.fileEmptyFlag),l(this,Q).push(t),this._uploadFile(t,s).then(a=>{this._elements.input.value=l(this,Q).map(h=>h.name).join(", "),this.dispatchEvent(new CustomEvent("addfile",{detail:{file:t,fileNode:s}})),this.dispatchEvent(new CustomEvent("change",{detail:{file:t,fileNode:s}})),this._props.validityBlur&&this._canValidityBlur&&$(this.checkValidity,100,this)}).catch(a=>{throw Zt.failed("文件上传错误",`${a.message}`,{container:this._elements.box}),s.setAttribute("status","error"),a})}}get files(){return l(this,Q).map(t=>({id:t.id,url:t.url,previewUrl:t.previewUrl,lastModified:t.lastModified,lastModifiedDate:t.lastModifiedDate,name:t.name,size:t.size,type:t.type}))}set files(t){t&&Array.isArray(t)&&(u(this,Q,[]),this._elements.fileList.querySelectorAll("oo-file").forEach(e=>{e._remove()}),t.forEach(e=>{this.addFile(e)}))}get value(){return this.files}set value(t){this.files=t}};Rn=new WeakSet,Dr=function(){this._setPropMap=Object.assign(this._setPropMap,{col:()=>{const t=this._props.col?parseInt(this._props.col):2;n.setStyles(this._elements.fileList,{"grid-template-columns":`repeat(${t}, 1fr)`})},type:()=>{this._checkTypeFile()},del:t=>{t!==this._props.del&&this._elements.fileList.querySelectorAll("oo-file").forEach(e=>{this._props.del?e.setAttribute("del","true"):e.setAttribute("del","false")})},size:()=>{},min:()=>{},max:()=>{(this._props.max?parseInt(this._props.max):0)===1&&(this.setAttribute("col","1"),this.removeAttribute("multiple"))},value:()=>{},multiple:()=>{const t=this._props.max?parseInt(this._props.max):0;n.toggleAttr(this._elements.input,"multiple",this._props.multiple&&t!==1),this._elements.fileInput&&n.toggleAttr(this._elements.fileInput,"multiple",this._props.multiple&&t!==1)}})},Q=new WeakMap,Vn=new WeakSet,Pr=function(t){Zt.warn("文件数量限制",`文件数量不能超过${t}个`,{container:this._elements.box})},Hn=new WeakSet,Ir=function(t){Zt.warn("文件大小限制",`单个文件大小不能超过${t}M`,{container:this._elements.box})},m(k2,"prop",{type:"file"});let Pi=k2;const a9=`.content{\r
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
`,d9=`
<div class="content">
    <div class="text"></div>
    <div class="label"></div>
    <div class="options-content">
        <slot name="items"></slot>
    </div>
</div>`,Bn=class Bn extends N{constructor(){super();m(this,"_setPropMap",{$default:()=>{},text:()=>{this._elements.text.textContent=this._props.text},label:()=>{this._elements.label.textContent=this._props.label},skin:()=>{this._useSkin(this._props.skin)}});m(this,"_elements",{text:null,label:null});d(this,s3,null);m(this,"parent",null);d(this,E2,[]);this._initialize("oo-option-group",Bn,d9,a9)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_render(){this._elements.text.textContent=this._props.text||this._props.label}_connected(){var s,r;const t=this.parentElement,e=t.tagName.toLowerCase();["oo-select","oo-option"].includes(e)&&(this.parent=t),this.parent?((r=(s=this.parent)._appendGroup)==null||r.call(s,this),n.show(this)):n.hide(this)}get select(){return this.parent?this.parent.tagName.toLowerCase()==="oo-select"?this.parent:this.parent.select:null}get level(){let t=this.parent,e=1;for(;t;)t.tagName.toLowerCase()==="oo-option"&&e++,t=t.parent;return e}_disconnected(){var t;(t=this.parent)==null||t._removeGroup(this),this.parent=null}_removeItem(t){this.select&&this.select._removeItem(t),l(this,E2).splice(l(this,E2).indexOf(t),1)}_appendItem(t){this.select&&this.select._appendItem(t),l(this,E2).push(t)}};s3=new WeakMap,E2=new WeakMap,m(Bn,"prop",{text:"",skin:""});let Ii=Bn;const h9=`.content{\r
    background-color: var(--option-bg);\r
    transition: background-color 0.2s, color 0.2s;\r
    cursor: pointer;\r
    overflow: hidden;\r
    box-sizing: border-box;\r
}\r
.content *{\r
    box-sizing: border-box;\r
}\r
.body:hover{\r
    background-color: var(--option-over-bg);\r
    color: var(--option-over);\r
}\r
.body.selected{\r
    font-weight: bold;\r
    color: var(--option-selected);\r
    background-color: var(--option-selected-bg);\r
}\r
.body.stem-selected{\r
  font-weight: bold;\r
  color: var(--option-selected);\r
  /*background-color: var(--option-selected-bg);*/\r
}\r
.body.disabled{\r
    opacity: 0.4;\r
    cursor: not-allowed;\r
}\r
.body.disabled:hover{\r
    background-color: var(--option-bg);\r
}\r
.body.focus{\r
    background-color: var(--option-cascade-focus-bg);\r
}\r
\r
.area{\r
    float: left;\r
    line-height: 2em;\r
    overflow: hidden;\r
    padding: 0 0 0 1em;\r
    white-space: nowrap;\r
    display: flex;\r
    gap: 0.5em;\r
    align-items: center;\r
}\r
\r
.label, .text{\r
    line-height: 2em;\r
    overflow: hidden;\r
    word-break: keep-all;\r
    text-overflow: ellipsis;\r
}\r
\r
.body{\r
    display: flex;\r
    justify-content: space-between;\r
    align-items: center;\r
    padding: 0;\r
    gap: 1em;\r
}\r
.arrow{\r
    padding: 0 0.5em 0 0;\r
}\r
.checkbox{\r
    position: relative;\r
    display: inline-block;\r
    font-size: 1em;\r
    cursor: pointer;\r
    width: 1em;\r
    height: 1em;\r
}\r
.checkbox i{\r
    position: absolute;\r
    top: 0;\r
    left: 0;\r
    transition: opacity 0.2s ease, height 0.2s ease, width 0.2s ease;\r
}\r
.checkbox i.invisible{\r
    opacity: 0;\r
    height: 0;\r
    width: 0;\r
}\r
.checkbox i.visible{\r
    opacity: 1;\r
    width: 100%;\r
    height: 100%;\r
}\r
.checkbox i.ooicon-checkbox-checked, .checkbox i.ooicon-radio-checked{\r
    color: var(--oo-color-main);\r
}\r
.checkbox i.ooicon-checkbox-unchecked, .checkbox i.ooicon-radio-unchecked{\r
    color: var(--oo-color-text2);\r
}\r
.checkbox i.ooicon-checkbox-unchecked:hover, .checkbox i.ooicon-radio-unchecked:hover{\r
    color: var(--oo-color-main) !important;\r
}\r
\r
\r
\r
.options-position-node{\r
    position: absolute;\r
    height: 2em;\r
    top: 0;\r
    transition: background-color 0.2s;\r
}\r
.options-position-node.hide{\r
    display: block !important;\r
    opacity: 0 !important;\r
    width: 0 !important;\r
    height: 0 !important;\r
}\r
.options-area{\r
    top: 0;\r
    position: absolute;\r
    border: 1px solid var(--option-border);\r
    background-color: var(--option-bg);\r
    border-radius: var(--oo-area-radius);\r
    box-shadow: var(--option-shadow);\r
    z-index: 100;\r
    transform-origin: left top;\r
    /* transition: height 0.1s, opacity 0.1s, transform 0.1s; */\r
}\r
.options-area.visible{\r
    transform: scale(1);\r
    opacity: 1;\r
}\r
.options-area.invisible{\r
    transform: scale(0.001, 1);\r
    opacity: 0;\r
}\r
.options-content{\r
    max-height: 17em;\r
    padding: 0.5em 0;\r
    border-radius: var(--oo-area-radius);\r
    overflow: auto;\r
}\r
\r
.options-content::-webkit-scrollbar {\r
    width: 4px;\r
    height: 4px;\r
    border-radius: 4px;\r
    background-color: transparent;\r
}\r
.options-content::-webkit-scrollbar-thumb{\r
    width: 4px;\r
    border-radius: 4px;\r
    background-color: #dddddd;\r
    cursor: pointer;\r
    /*opacity: 0;*/\r
}\r
.options-content:hover::-webkit-scrollbar-thumb{\r
    width: 6px;\r
    border-radius: 6px;\r
    background-color: #dddddd;\r
}\r
.options-content:hover::-webkit-scrollbar-thumb:hover{\r
    width: 6px;\r
    border-radius: 6px;\r
    background-color: #cccccc;\r
}\r
.hide {\r
    display: none !important;\r
}\r
@media only screen and (max-width: 767px) {\r
    /*级联状态下的样式*/\r
    .options-area{\r
        top: unset !important;\r
        min-width: unset !important;\r
        width: 100% !important;\r
        bottom: 0 !important;\r
        height: 100%;\r
        padding-bottom: 20px !important;\r
        z-index: 100;\r
        transform-origin: bottom !important;\r
        border-radius: unset !important;\r
        box-shadow: unset !important;\r
    }\r
    .options-area.visible {\r
        transform: unset !important;\r
        opacity: 1;\r
    }\r
    .options-area.current{\r
        position: fixed;\r
        left: 0 !important;\r
    }\r
    .options-content{\r
        height: 100%;\r
        width: 100%;\r
        max-height: unset !important;\r
        min-height: unset !important;\r
        overscroll-behavior: contain;\r
    }\r
}\r
`,c9=`
<div class="content">
    <div class="body">
        <div class="area">
            <div class="checkbox hide"></div>
            <div class="icon hide"></div>
            <div class="text"></div>
            <div class="label"></div>
        </div>
        <div class="arrow"></div>
    </div>
    <div class="optionsPosition options-position-node">
        <div class="optionsArea options-area invisible">
            <div class="optionsContent options-content"><slot name="items" class="items"></slot></div>
        </div> 
    </div>
</div>`,p9="__loading",Wn=class Wn extends N{constructor(){super();d(this,k1);d(this,Yn);d(this,E1);m(this,"_setPropMap",{value:()=>{},$default:()=>{},text:()=>{c(this,k1,x3).call(this)},disabled:()=>{n.checkClass(this._elements.body,"disabled",!!this._props.disabled)},selected:()=>{var t,e;(this.isLeaf||(t=this.select)!=null&&t._props.checkStrictly)&&!this.loading&&((e=this.select)==null||e._selected(this)),this._checkSelected()},icon:t=>{n.removeClass(this._elements.icon,`ooicon-${t}`),this._props.icon&&n.addClass(this._elements.icon,`ooicon-${this._props.icon}`),n.checkClass(this._elements.icon,"hide",!this._props.icon)},skin:()=>{this._useSkin(this._props.skin)}});m(this,"_elements",{body:null,text:null,label:null,area:null,optionsPosition:null,optionsArea:null,optionsContent:null,arrow:null,icon:null,checkbox:null,items:null});d(this,Ut,[]);d(this,M2,[]);d(this,S2,!1);d(this,Ze,!1);m(this,"parent",null);d(this,Re,!1);this._initialize("oo-option",Wn,c9,h9)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_render(){c(this,k1,x3).call(this),this._setPropMap.disabled()}_connected(){var e;this._connectToSelect();let t=this.firstElementChild;for(;t;)(e=t._connected)==null||e.call(t),t=t.nextElementSibling}_connectToSelect(){const t=this.parentElement,e=t.tagName.toLowerCase();["oo-select","oo-option-group","oo-option"].includes(e)&&(this.parent=t),this.parent&&this.parent._appendItem?(this.parent._appendItem(this),n.show(this)):n.hide(this),this._checkCheckbox()}get select(){return this.parent?this.parent.tagName.toLowerCase()==="oo-select"?this.parent:this.parent.select:null}get level(){let t=this.parent,e=1;for(;t;)t.tagName.toLowerCase()==="oo-option"&&e++,t=t.parent;return e}_disconnected(){var t;(t=this.parent)==null||t._removeItem(this),this.parent=null}_setEvent(){const t=s=>{const r=this.select,{multiple:a,checkStrictly:h}=r==null?void 0:r._props;!this._props.disabled&&(this.isLeaf||h)&&(this.selected=a?!this.selected:!0),!a&&(r==null||r._hideOptions()),s.stopPropagation()},e=s=>{this.parent._currentCascade&&this.parent._currentCascade!==this&&this.parent._currentCascade._hideOptions(),!this.isLeaf&&this._showOptions(),s.stopPropagation()};if(this._content.addEventListener("click",s=>{this.isLeaf&&t(s)}),this._content.addEventListener("click",s=>{!this.isLeaf&&e(s)}),this._elements.checkbox.addEventListener("click",s=>{t(s)}),this.level===1){const s=()=>{var h;const a=n.getSize(this._elements.text);(h=this.select)==null||h.checkMaxWidth(a.x+(c(this,Yn,Nr).call(this)?20:0))};new MutationObserver(s).observe(this._content,{subtree:!0,childList:!0,attributes:!0,characterData:!0})}}_isMobile(){return window.matchMedia("only screen and (max-width: 767px)").matches}_checkSelected(){this.loading||(n.checkClass(this._elements.body,"selected",this._props.selected),this._checkCheckbox()),this._selectCascade(this._props.selected)}_selectCascade(t=!0){var s;const e=this.optionCascade;if(e&&e.length){const r=(s=this.select)==null?void 0:s._props.multiple;for(let a=e.length-1;a>-1;a--)e[a].stemSelected=r?e[a]._hasSubOptionSelected():t}}_showCascadeOptions(){var e;const t=this.optionCascade;t&&t.length&&(e=this.select)!=null&&e.dropped&&t.forEach(s=>{s._showOptions(!0)})}_hasSubOptionSelected(){return l(this,Ut).some(t=>t.selected||t.stemSelected)}_checkCheckbox(t=!1){var h,p,C;if(this.loading)return;const{checkStrictly:e,multiple:s}=((h=this.select)==null?void 0:h._props)||{},r=e?s?"checkbox":"radio":s&&this.isLeaf?"checkbox":"",a=this._elements.checkbox;if(n.checkClass(a,"hide",!r),t&&((p=this.checkedIcon)==null||p.remove(),(C=this.uncheckedIcon)==null||C.remove(),this.checkedIcon=null,this.uncheckedIcon=null),r){const g=`ooicon-${r}`;this.checkedIcon||(this.uncheckedIcon=n(`i.${g}-unchecked.invisible`),this.checkedIcon=n(`i.${g}-checked.invisible`),a.append(this.checkedIcon,this.uncheckedIcon)),n.checkClass(this.uncheckedIcon,"invisible",this._props.selected),n.checkClass(this.uncheckedIcon,"visible",!this._props.selected),n.checkClass(this.checkedIcon,"invisible",!this._props.selected),n.checkClass(this.checkedIcon,"visible",this._props.selected)}}get optionCascade(){let t=this.parent;const e=[];for(;t;)t.tagName.toLowerCase()==="oo-option"&&e.unshift(t),t=t.parent;return e}get value(){let t=this.parent;const e=[this._props.value||this._props.text];for(;t;)t.tagName.toLowerCase()==="oo-option"&&e.unshift(t._props.value||t._props.text),t=t.parent;return e}get text(){let t=this.parent;const e=[this._props.text||this._elements.label.textContent];for(;t;)t.tagName.toLowerCase()==="oo-option"&&e.unshift(t._props.text||t._elements.label.textContent),t=t.parent;return e}get isLeaf(){return!l(this,Ut).length}set stemSelected(t){t!==l(this,Ze)&&(u(this,Ze,t),n.checkClass(this._elements.body,"stem-selected",l(this,Ze)))}get stemSelected(){return l(this,Ze)}_positionOptions(){var t,e,s,r;if((t=this.select)!=null&&t.dropped&&!this._isMobile()){const a=(r=(s=(e=this._content.getRootNode())==null?void 0:e.host)==null?void 0:s.assignedSlot)==null?void 0:r.parentElement,p=n.getSize(a||this._content).x+2;n.setStyles(this._elements.optionsArea,{left:p+"px"})}}_positionSelectOptions(t=!1){var e,s,r;if((e=this.select)!=null&&e.dropped&&!this._isMobile()){const a=this._elements.items.assignedElements({flatten:!0});if(a.length>1||!((s=a[0])!=null&&s.loading)){const h=this.optionCascade;h.length>0&&((r=this.select)==null||r.positionOptions(h.concat(this),t))}}}_showOptions(t=!1){var e;!this._props.disabled&&!l(this,Re)&&(u(this,Re,!0),c(this,E1,y3).call(this),this._positionOptions(),n.removeClass(this._elements.optionsPosition,"hide"),n.removeClass(this._elements.optionsArea,"invisible"),n.addClass(this._elements.optionsArea,"visible"),this._positionSelectOptions(t),this.dispatchEvent(new CustomEvent("show")),this.parent&&(this.parent._currentCascade=this)),!this._props.disabled&&((e=this.select)==null||e._addPathItem(this))}_hideOptions(){u(this,Re,!1),c(this,E1,y3).call(this),n.removeClass(this._elements.optionsArea,"visible"),n.addClass(this._elements.optionsArea,"invisible"),l(this,Ut).forEach(t=>{t._hideOptions()}),this.dispatchEvent(new CustomEvent("hide"))}_appendItem(t){var s;const e=this.select;e&&(e.cascading=!0),l(this,Ut).push(t),e==null||e._addToMap(t),t.setAttribute("slot","items"),t.setAttribute("skin",(s=this.select)==null?void 0:s._props.skin),e!=null&&e._props.size&&t.setAttribute("size",this.select._props.size),e==null||e._appendItemCheckValue(t),n.addClass(this._elements.arrow,"ooicon-arrow_forward"),this._checkCheckbox()}_removeItem(t){l(this,Ut).splice(l(this,Ut).indexOf(t),1),t.removeAttribute("slot"),this.isLeaf&&n.removeClass(this._elements.arrow,"ooicon-arrow_forward")}_removeGroup(t){l(this,M2).splice(l(this,M2).indexOf(t),1),t.removeAttribute("slot")}_appendGroup(t){var e,s,r;l(this,M2).push(t),t.setAttribute("slot","items"),t.setAttribute("skin",(e=this.select)==null?void 0:e._props.skin),(s=this.select)!=null&&s._props.size&&option.setAttribute("size",(r=this.select)==null?void 0:r._props.size)}get loading(){return l(this,S2)}set loading(t){t!==l(this,S2)&&(u(this,S2,t),this.setAttribute("icon","loading"),this.setAttribute("value",p9))}};Ut=new WeakMap,M2=new WeakMap,S2=new WeakMap,Ze=new WeakMap,k1=new WeakSet,x3=function(){if(this.innerHTML.trim()){let t=this.firstChild;for(;t;)if(t.nodeType===Node.TEXT_NODE||t.nodeType===Node.ELEMENT_NODE&&!["oo-option","oo-option-group"].includes(t.tagName.toLowerCase())){let e=t;t=t.nextSibling,this._elements.label.appendChild(e)}else t=t.nextSibling}this._elements.label.innerHTML.trim()||(this._props.text?this._elements.text.textContent=this._props.text:this._elements.text.innerHTML="&nbsp;"),this._elements.area.title=this._elements.area.textContent},Yn=new WeakSet,Nr=function(){var t;return((t=this.parent)==null?void 0:t.tagName.toLowerCase())==="oo-option-group"},Re=new WeakMap,E1=new WeakSet,y3=function(){n.checkClass(this._elements.body,"focus",l(this,Re))},m(Wn,"prop",{value:"",text:"",icon:"",selected:!1,disabled:!1,skin:""});let dt=Wn;const u9=`* {\r
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
    /* margin-right: 0.5em; */\r
}\r
.content:hover{\r
    background-color: var(--hover-bg);\r
    color: var(--hover-color);\r
}\r
.border.content{\r
    background-color: var(--oo-color-hover);\r
    padding: 0.2em 0.5em;\r
    box-shadow: var(--oo-shadow-border);\r
}\r
.border.content:hover{\r
    box-shadow: var(--oo-shadow-border-focus);\r
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
`,m9=Object.assign({"./styles/tooltip/deep.css":()=>Promise.resolve().then(()=>$8),"./styles/tooltip/default.css":()=>Promise.resolve().then(()=>j8),"./styles/tooltip/toolbar.css":()=>Promise.resolve().then(()=>z8)}),Xt=class Xt{constructor(i,t,e){d(this,qn);d(this,Un);d(this,Xn);d(this,Gn);d(this,Kn);d(this,M1);d(this,S1);d(this,Jn);d(this,Qn);d(this,A1);d(this,t5);d(this,T1);d(this,e5);this.content=i,this.target=t,this.options=Object.assign({},Xt.option,e),c(this,qn,Or).call(this),c(this,e5,Hr).call(this),this.showFun||(this.showFun=this.show.bind(this)),this.hideFun||(this.hideFun=this.hide.bind(this)),this.options.events?c(this,A1,E3).call(this):$(this.showFun,this.options.defer,this)}close(){n.setStyles(this.node,{opacity:0}),this.hideTimeout=window.setTimeout(()=>{this.container.remove()},600)}hide(){n.setStyles(this.node,{opacity:0}),this.hideTimeout=window.setTimeout(()=>{n.addClass(this.node,"hide"),n.removeClass(this.node,"show"),this.p&&n.setStyles(this.node,{left:this.p.fromX+"px",top:this.p.fromY+"px"}),c(this,Xn,$r).call(this)},600)}async show(){if(!this.target||!this.target.isConnected)return null;this.hideTimeout&&(clearTimeout(this.hideTimeout),this.hideTimeout=null),n.removeClass(this.node,"hide"),n.addClass(this.node,"show"),c(this,Un,Fr).call(this),await c(this,A1,E3).call(this),await c(this,t5,Vr).call(this),this.contentNode.childNodes.length&&(n.removeClass(this.node,"hide"),n.addClass(this.node,"show"),this.p=c(this,Kn,zr).call(this),n.setStyles(this.node,{left:this.p.fromX+"px",top:this.p.fromY+"px"}),n.setStyles(this.triangle,{left:this.p.flagX+"px",top:this.p.flagY+"px"}),window.setTimeout(()=>{n.setStyles(this.node,{opacity:1,left:this.p.x+"px",top:this.p.y+"px"}),c(this,Jn,Zr).call(this)},5)),(!this.target||!this.target.isConnected)&&this.hide()}};qn=new WeakSet,Or=async function(){this.container=n("div.ootooltip_container"),this.node=n("div.ootooltip"),this.triangle=n(`div.ootooltip_triangle.${this.options.position}`),this.contentNode=n("div.ootooltip_content"),this.node.append(this.triangle),this.node.append(this.contentNode),this.container.append(this.node),c(this,Gn,jr).call(this)},Un=new WeakSet,Fr=function(){(this.options.container||document.body).append(this.container)},Xn=new WeakSet,$r=function(){this.container.remove()},Gn=new WeakSet,jr=function(){this.options.events&&(this.options.events.show&&(this.options.events.show.split(/\s*,\s*/g).forEach(t=>{this.target.addEventListener(t,e=>{this.hideFun.clear&&this.hideFun.clear(),$(this.showFun,this.options.defer,this)})}),this.options.showOnEnterCotent&&this.container.addEventListener("mouseenter",()=>{this.hideFun.clear&&this.hideFun.clear()})),this.options.events.hide&&(this.options.events.hide.split(/\s*,\s*/g).forEach(t=>{this.target.addEventListener(t,()=>{this.showFun.clear&&this.showFun.clear(),$(this.hideFun,100,this)})}),this.options.showOnEnterCotent&&this.container.addEventListener("mouseleave",()=>{this.showFun.clear&&this.showFun.clear(),$(this.hideFun,100,this)})))},Kn=new WeakSet,zr=function(){const i=this.options.container?{x:this.target.offsetLeft,y:this.target.offsetTop}:n.getPosition(this.target),t=n.getSize(this.target),e=n.getSize(this.node),s=n.getSize(this.triangle).x/2;let r=c(this,S1,k3).call(this,this.options.position,i,t,e,s);switch(this.options.position){case"top":r.y<0&&(r=c(this,M1,w3).call(this,"bottom",i,t,e,s));break;case"left":r.x<0&&(r=c(this,M1,w3).call(this,"right",i,t,e,s));break}if((this.options.position==="top"||this.options.position==="bottom")&&r.x<0){const a=r.x;r.flagX+=a,r.x=0,r.fromX=0}if((this.options.position==="left"||this.options.position==="right")&&r.y<0){const a=r.y;r.flagY+=a,r.y=0,r.fromY=0}return r},M1=new WeakSet,w3=function(i,t,e,s,r){return n.removeClass(this.triangle,this.options.position),n.addClass(this.triangle,i),this.options.position=i,c(this,S1,k3).call(this,this.options.position,t,e,s,r)},S1=new WeakSet,k3=function(i,t,e,s,r){const a={},h=this.options.emerge||0,p=this.options.offset||this.options.offset===0?this.options.offset:3;switch(i){case"bottom":a.x=a.fromX=t.x+e.x/2-s.x/2,a.y=t.y+e.y+r+p,a.fromY=a.y-h,a.flagX=s.x/2-r,a.flagY=0-r;break;case"left":a.x=t.x-s.x-r-p,a.fromX=a.x+h,a.y=a.fromY=t.y+e.y/2-s.y/2,a.flagX=s.x-r,a.flagY=s.y/2-r;break;case"right":a.x=t.x+e.x+r+p,a.fromX=a.x-h,a.y=a.fromY=t.y+e.y/2-s.y/2,a.flagX=0-r,a.flagY=s.y/2-r;break;default:a.x=a.fromX=t.x+e.x/2-s.x/2,a.y=t.y-s.y-r-p,a.fromY=a.y+h,a.flagX=s.x/2-r,a.flagY=s.y-r}return a},Jn=new WeakSet,Zr=function(){this.options.autoClose>0&&window.setTimeout(()=>{this[this.options.events?"hide":"close"]()},this.options.autoClose)},Qn=new WeakSet,Rr=async function(i){const t=await m9[`./styles/tooltip/${i}.css`]();return r4(t.default,this.container)},A1=new WeakSet,E3=async function(){const i=Xt.loadedStyles[this.options.style||"default"];if(i){const t=await Promise.resolve(i);n.addClass(this.container,t.id)}else{const t=this.options.style||"default";Xt.loadedStyles[t]=c(this,Qn,Rr).call(this,t),Xt.loadedStyles[t]=await Xt.loadedStyles[t]}},t5=new WeakSet,Vr=async function(){if(this.options.resetContentOnShow||!this.contentLoaded)return this.target.dispatchEvent(new MouseEvent("tooltip")),n.empty(this.contentNode),this.contentLoaded=!0,await c(this,T1,M3).call(this,this.content),""},T1=new WeakSet,M3=async function(i){switch(v(i)){case"string":n.set(this.contentNode,"html",i);break;case"element":this.contentNode.append(i);break;case"function":const t=await Promise.resolve(i());await c(this,T1,M3).call(this,t)}},e5=new WeakSet,Hr=function(){this.observerNode=this.observerNode||this.target.parentElement;var i=new MutationObserver((t,e)=>{if(t[0].removedNodes&&t[0].removedNodes.length){for(let s of t[0].removedNodes)if(s===this.target){this.hide(),e.disconnect();break}}});this.observerNode&&i.observe(this.observerNode,{childList:!0})},m(Xt,"option",{position:"top",style:"default",autoClose:0,emerge:-8,offset:6,defer:600,events:{show:"mouseover",hide:"mouseout, mousedown"},showOnEnterCotent:!1,resetContentOnShow:!1,container:null}),m(Xt,"loadedStyles",{});let B2=Xt;function Ni(o,i,t){return new B2(o,i,t)}const C9=`
<div class="content">
    <div class="icon"></div>
    <div class="text"></div>
</div>
`,i5=class i5 extends N{constructor(){super();d(this,ue);d(this,s5);d(this,Gt);d(this,n5);m(this,"_elements",{icon:null,text:null});m(this,"_setPropMap",{type:()=>{this._canRender()&&c(this,Gt,e2).call(this)},value:()=>{this._canRender()&&(c(this,Gt,e2).call(this),c(this,ue,z2).call(this))},data:()=>{this._canRender()&&(c(this,Gt,e2).call(this),c(this,ue,z2).call(this))},text:()=>{this._props.text?this._elements.text.textContent=this._props.text:c(this,ue,z2).call(this)},entity:()=>{this._canRender()&&(c(this,Gt,e2).call(this),c(this,ue,z2).call(this))},isTooltip:()=>{this.tooltip=this._props.isTooltip!==!1?this.tooltip||this.defaultTooltip:null},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)}});this._initialize("oo-org",i5,C9,u9)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_canRender(){return super._canRender(null,["value","data"])}_render(){this._canRender()&&c(this,Gt,e2).call(this)}_connected(){this._canRender()&&(c(this,Gt,e2).call(this),c(this,ue,z2).call(this)),this.tooltip=this._props.isTooltip!==!1?this.defaultTooltip:null,this.tip||(this.tip=Ni(async()=>v(this.tooltip)==="function"?await this.tooltip():this.tooltip,this))}defaultTooltip(){const t=n("oo-card",{value:this.value,data:JSON.stringify(this.data),skin:"border: 0; shadow: 0"});return new Promise(e=>{t.addEventListener("loaded",()=>{e(t)})})}};ue=new WeakSet,z2=function(){this._props.text?this._elements.text.textContent=this._props.text:this._props.value?this._elements.text.textContent=ys(this._props.value):this._elements.text.textContent=c(this,s5,Br).call(this,this._props.data)},s5=new WeakSet,Br=function(t){return t.dn?ys(t.dn):t.name},Gt=new WeakSet,e2=function(){switch(this._props.type){case"text":n.addClass(this._elements.icon,"hide");break;case"text-border":n.checkClass(this._content,"border",this._props.type==="text-border"),n.addClass(this._elements.icon,"hide");break;default:n.checkClass(this._content,"border",this._props.type==="border");const t=c(this,n5,Yr).call(this,this._props.entity||Ci(this._props.value||this._props.data));v(t)==="function"?n.setStyle(this._elements.icon,"background-image",`url('${t(this._props.value||this._props.data)}')`):n.addClass(this._elements.icon,t),n.removeClass(this._elements.icon,"hide")}},n5=new WeakSet,Yr=function(t){return{I:e=>{var s;return ws(((s=e.person)==null?void 0:s.dn)||e.dn||e)},P:e=>ws(e.dn||e),U:"ooicon-unit",G:"ooicon-group",R:"ooicon-role",UD:"ooicon-duty",UA:"ooicon-attribute",PA:"ooicon-attribute",PROCESS:"ooicon-process",APP:"ooicon-computer",A:"ooicon-activities",unknown:"ooicon-notes"}[t]},m(i5,"prop",{type:"simple",value:"",text:"",data:{},entity:"",cssLink:"",skin:"",isTooltip:!0});let Oi=i5;const g9=`*{box-sizing:border-box}.content{--gap: .5em;--color: var(--oo-color-text2);--bg: var(--oo-default-radius);--radius: var(--oo-default-radius);--border: 1px solid var(--oo-color-gray-d);--hover-bg: var(--oo-color-light);--current-color: var(--oo-color-text-white);--current-bg: var(--oo-color-main)}.content{display:flex;gap:var(--gap);align-items:center}.prev,.next,.pages>div{padding:.2em;color:var(--color);background:var(--bg);border-radius:var(--radius);border:var(--border);cursor:pointer;display:flex;align-items:center;justify-content:center;min-width:1.788em;height:1.788em;transition:background .3s}.prev:hover,.next:hover,.pages>div:hover{background:var(--hover-bg)}.pages{display:flex;gap:var(--gap);align-items:center}.pages>.current{color:var(--current-color);background:var(--current-bg)}.pages>.current:hover{background:var(--current-bg)}.point3{transform:rotate(90deg);color:var(--color)}.jumper{margin-left:1em;display:flex;align-items:center;gap:.3em}.jumper oo-input{width:3.5em;text-align:center;-webkit-appearance:none;-moz-appearance:none;appearance:none}oo-input::-webkit-inner-spin-button,oo-input::-webkit-outer-spin-button{-webkit-appearance:none;-moz-appearance:none;appearance:none;margin:0}.hide{display:none}
`;customElements.get("oo-button")||customElements.define("oo-button",l4);const f9=`
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
`;function a2(o,i){o.apply(i||this)}const o5=class o5 extends N{constructor(){super();d(this,D1);d(this,P1);d(this,Kt);d(this,A2);d(this,Jt);d(this,r5);d(this,I1);d(this,T2);m(this,"_elements",{first:null,last:null,prev:null,next:null,pages:null,jumper:null,jumperTextLeft:null,jumperTextRight:null,jumperPageCount:null,jumperInput:null});m(this,"_setPropMap",{cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},current:(t,e)=>{t!==e&&a2(c(this,Kt,s2),this)},total:(t,e)=>{t!==e&&a2(c(this,Kt,s2),this)},pageSize:(t,e)=>{t!==e&&a2(c(this,Kt,s2),this)},pageCount:()=>{},pages:(t,e)=>{t!==e&&a2(c(this,Kt,s2),this)},first:()=>{c(this,D1,S3).call(this)},last:()=>{c(this,P1,A3).call(this)},jumper:()=>{n.checkClass(this._elements.jumper,"hide",!this._props.jumper)},jumperText:()=>{this._props.jumperText||(this._props.jumperText="到第{n}页"),c(this,T2,G5).call(this)}});this._initialize("oo-pagination",o5,f9,g9)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}async _render(){c(this,A2,X5).call(this),(this._props.pageCount>1||this._props.showSingle)&&(c(this,D1,S3).call(this),c(this,P1,A3).call(this),a2(c(this,Kt,s2),this),c(this,T2,G5).call(this))}_setEvent(){this._elements.prev.addEventListener("click",()=>{this.gotoPrev()}),this._elements.next.addEventListener("click",()=>{this.gotoNext()}),this._elements.first.addEventListener("click",()=>{this.gotoFirst()}),this._elements.last.addEventListener("click",()=>{this.gotoLast()}),this._elements.jumperInput&&this._elements.jumperInput.addEventListener("keydown",t=>{if(t.key==="Enter"||t.keyCode===13){const e=this._elements.jumperInput;!isNaN(e.value)&&e.value&&(this.gotoPage(parseInt(e.value)),e.value="")}})}setCurrent(t){this._props.current=t,c(this,A2,X5).call(this)}gotoPage(t){this.setCurrent(t),this.dispatchEvent(new CustomEvent("page",{detail:this._props.current})),this.setAttribute("current",this._props.current),a2(c(this,Kt,s2),this)}pagesForward(){this.gotoPage(this._props.current+(this._props.pages-2))}pagesBack(){this.gotoPage(this._props.current-(this._props.pages-2))}gotoFirst(){this.gotoPage(1)}gotoLast(){this.gotoPage(this._props.pageCount)}gotoPrev(){this.gotoPage(--this._props.current)}gotoNext(){this.gotoPage(++this._props.current)}};D1=new WeakSet,S3=function(){const t=this._props.first;this._elements.first.textContent=t,n.checkClass(this._elements.first,"hide",!t)},P1=new WeakSet,A3=function(){const t=this._props.last;this._elements.last.textContent=t,n.checkClass(this._elements.last,"hide",!t)},Kt=new WeakSet,s2=function(t,e,s){if(c(this,A2,X5).call(this),n.empty(this._elements.pages),this._props.pageCount>1||this._props.showSingle)if(this._props.pageCount<=this._props.pages)for(let r=1;r<=this._props.pageCount;r++)c(this,Jt,n2).call(this,r);else{c(this,Jt,n2).call(this,1);const r=this._props.pages-2,a=parseInt(r/2);let h;if(this._props.current-a<=2)for(let p=1;p<=r;p++)h=p+1,c(this,Jt,n2).call(this,h);else if(this._props.current+a>=this._props.pageCount-1){c(this,I1,T3).call(this);for(let p=r;p>=1;p--)h=this._props.pageCount-p,c(this,Jt,n2).call(this,h)}else{c(this,I1,T3).call(this);for(let p=0;p<r;p++)h=this._props.current-(a-p),c(this,Jt,n2).call(this,h,s)}h<this._props.pageCount-1&&c(this,r5,Wr).call(this),c(this,Jt,n2).call(this,this._props.pageCount)}c(this,T2,G5).call(this)},A2=new WeakSet,X5=function(){this._props.pageCount=Math.ceil(this._props.total/this._props.pageSize),this._props.pageCount<1&&(this._props.pageCount=1),this._props.current<1&&(this._props.current=1),this._props.current>this._props.pageCount&&(this._props.current=this._props.pageCount)},Jt=new WeakSet,n2=function(t){const e=document.createElement("div");e.textContent=t,t===this._props.current&&n.addClass(e,"current"),e.addEventListener("click",()=>{this.gotoPage(t)}),this._elements.pages.append(e)},r5=new WeakSet,Wr=function(){const t=document.createElement("div");n.addClass(t,"ooicon-point3 point3"),t.addEventListener("click",()=>{this.pagesForward()}),this._elements.pages.append(t)},I1=new WeakSet,T3=function(){const t=document.createElement("div");n.addClass(t,"ooicon-point3 point3"),t.addEventListener("click",()=>{this.pagesBack()}),this._elements.pages.append(t)},T2=new WeakSet,G5=function(){const t=this._props.jumperText.split("{n}");this._elements.jumperTextLeft.textContent=t[0],this._elements.jumperTextRight.textContent=(t==null?void 0:t[1])??"",this._elements.jumperPageCount.textContent=this._props.pageCount},m(o5,"prop",{total:200,pageSize:20,pageCount:0,pages:10,current:1,showSingle:!0,jumper:!1,first:"第一页",last:"最后一页",jumperText:"到第{n}页",border:!0,cssLink:"",skin:""});let Fi=o5;const v9=`.content{\r
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
}`;class b9 extends ks{constructor(){super(),this._init("oo-radio"),this._useCss(v9)}_unCheckedOther(){this.ownerDocument.querySelectorAll(`oo-radio[name='${this._props.name}']`).forEach(t=>{t!==this&&t.checked&&t._unchecked()})}_render(){this._elements.input.type="radio",this._elements.input.checked&&this._unCheckedOther(),super._render()}}class _9 extends Es{constructor(){super();d(this,l5);d(this,a5);c(this,l5,qr).call(this),this._init("oo-radio-group")}_appendItemCheckValue(t){this._props.value?t.value===this._props.value&&t.setAttribute("checked",!0):t._unchecked()}_itemChange(t){t.currentTarget.checked?this.value=t.currentTarget.value:this.value="",this._elements.input.value=this.value,this.unInvalidStyle()}_removeItem(t){super._removeItem(t)}get text(){return c(this,a5,Ur).call(this)}}l5=new WeakSet,qr=function(){this.setPropMap=Object.assign(this._setPropMap,{value:()=>{if(this._props.value){for(const t of this._items)if(t.value===this._props.value){t.setAttribute("checked",!0);break}}this._elements.input.value=this._props.value,this._fillEmptyReadmode()}})},a5=new WeakSet,Ur=function(){for(const t of this._items)if(t.checked)return t.text;return""};const L9=`.content{\r
    --option-bg: var(--oo-color-text-white);\r
    --option-over: var(--oo-color-text2);\r
    --option-over-bg: var(--oo-color-gray-e);\r
    --option-cascade-focus-bg: var(--oo-color-hover);\r
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
.content {\r
    height: 100%;\r
    width: 100%;\r
    /*padding: 2px;*/\r
    position: static;\r
}\r
.labelArea {\r
    display: flex;\r
    /* align-items: center; */\r
    position: static;\r
    height: 100%;\r
}\r
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
.options-path-area{\r
    display: none;\r
}\r
.options-path-area .current{\r
    color: var(--option-selected)\r
}\r
.options-result-area{\r
    display: none;\r
}\r
.options-result-area div{\r
    flex: 1;\r
}\r
.options-result-area i{\r
    color: var(--option-selected);\r
}\r
.options-result-area oo-button{\r
    color: var(--option-selected);\r
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
.options-content{\r
    max-height: 17em;\r
    min-height: 3em;\r
    padding: 0.5em 0;\r
    border-radius: var(--oo-area-radius);\r
    overflow: auto;\r
}\r
\r
.options-content::-webkit-scrollbar {\r
    width: 6px;\r
    height: 6px;\r
    border-radius: 4px;\r
    background-color: transparent;\r
}\r
.options-content::-webkit-scrollbar-thumb{\r
    width: 6px;\r
    border-radius: 6px;\r
    /* background-color: transparent; */\r
    background-color: #dddddd;\r
    cursor: pointer;\r
    /*opacity: 0;*/\r
}\r
.options-content:hover::-webkit-scrollbar-thumb{\r
    background-color: #dddddd;\r
}\r
.options-content:hover::-webkit-scrollbar-thumb:hover{\r
    background-color: #cccccc;\r
}\r
\r
.view{\r
    position: relative;\r
    overflow: hidden;\r
}\r
.input{\r
    width: 100%;\r
    cursor: pointer;\r
    text-overflow: ellipsis;\r
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
.multiple .input{\r
    opacity: 0;\r
    height: 0;\r
    display: block;\r
    padding: 0;\r
    margin: 0;\r
}\r
.multiple .viewText{\r
    position: relative;\r
    display: flex !important;\r
    flex-wrap: wrap;\r
    border: 0;\r
    outline: none;\r
    color: var(--input);\r
    border-radius: var(--radius);\r
    padding: 0.5em 0.6em;\r
    height: 100%;\r
    min-height: 1.89em;\r
    box-shadow: none;\r
    width: inherit;\r
    font-size: 1em;\r
    background: transparent;\r
    gap: 0.5em;\r
}\r
\r
@media only screen and (max-width: 767px) {\r
    .content{\r
        position: relative;\r
    }\r
    .cascading .options-path-area {\r
        position: absolute;\r
        width: 100% !important;\r
        top: unset !important;\r
        bottom: 60%;\r
        display: flex;\r
        align-items: center;\r
        height: 2.75em;\r
        font-size: 1em;\r
        gap: 0.5em;\r
        padding: 0 1em;\r
        word-break: keep-all;\r
        overflow: auto;\r
        border-radius: var(--oo-area-radius);\r
        box-shadow: var(--option-shadow);\r
        border: 1px solid var(--option-border);\r
        background-color: var(--option-bg);\r
        z-index: 100;\r
        transform-origin: top;\r
        transition: height 0.2s, opacity 0.2s, transform 0.2s;\r
        border-bottom: 0;\r
    }\r
    .cascading .options-area {\r
        border-radius: unset !important;\r
        box-shadow: unset !important;\r
    }\r
\r
    .cascading.multiple .options-result-area {\r
        display: flex;\r
        justify-content: space-between;\r
        height: 3.75em;\r
        position: absolute;\r
        width: 100% !important;\r
        top: unset !important;\r
        bottom: 0;\r
        left: 0;\r
        align-items: center;\r
        border: 1px solid var(--option-border);\r
        background-color: var(--option-bg);\r
        border-top: 0;\r
        gap: 0.5em;\r
        padding: 0 1em 20px 1em !important;\r
    }\r
\r
    .cascading.multiple .options-area {\r
        bottom: 3.75em !important;\r
        padding-bottom: 0 !important;\r
    }\r
\r
    .cascading.multiple .options-path-area {\r
        bottom: calc(60% + 3.75em) !important;\r
    }\r
\r
    .options-result-content{\r
        position: absolute;\r
        border: 1px solid var(--option-border);\r
        background-color: var(--option-bg);\r
        border-radius: var(--oo-area-radius);\r
        z-index: 100;\r
        transform-origin: top;\r
        transition: height 0.2s, opacity 0.2s, transform 0.2s;\r
        top: unset !important;\r
        width: 100% !important;\r
        bottom: 3.75em !important;\r
        display: flex;\r
        flex-direction: column;\r
        padding: 1em 0;\r
        overflow: auto;\r
    }\r
    .options-result-content.visible{\r
        transform: scale(1);\r
        height: calc(60% + 2.75em);\r
        opacity: 1;\r
    }\r
    .options-result-content.invisible{\r
        transform: scale(1, 0.001);\r
        height: 0;\r
        opacity: 0;\r
    }\r
    .result-node{\r
        padding: 0 1em;\r
        display: flex;\r
        align-items: center;\r
        justify-content: space-between;\r
        gap: 0.5em;\r
    }\r
    .result-node:hover{\r
        background-color: var(--option-over-bg);\r
        color: var(--option-over);\r
    }\r
    .result-node div{\r
        min-height: 2em;\r
        line-height: 1.2em;\r
        padding: 0.525em 0;\r
        overflow: hidden;\r
        word-break: break-all;\r
        cursor: pointer;\r
        flex: 1;\r
    }\r
    .result-node oo-button{\r
        color: var(--option-selected);\r
    }\r
\r
}\r
`,Lt="___",M4=" /",S4="__loading",j=o=>Array.isArray(o)?o:o==null?[]:[o],oe=o=>Array.isArray(o)?o.every(i=>Array.isArray(i)):!1,d2=o=>Array.isArray(o)?o[o.length-1]:o,Ts=(o,i)=>{if(oe(o)!==oe(i))return!1;const t=j(o),e=j(i);if(t.length!==e.length)return!1;for(let s=0;s<t.length;s++){if(Array.isArray(t[s])!==Array.isArray(e[s]))return!1;if(Array.isArray(t[s])){if(!Ts(t[s],e[s]))return!1}else if(t[s]!==e[s])return!1}return!0},x9=(o,i,t)=>{if(t===-1)o.appendChild(i);else{const e=Array.from(o.children),s=t<e.length?e[t]:null;o.insertBefore(i,s)}},We=class We extends at{constructor(){super();d(this,d5);d(this,$1);d(this,j1);d(this,Be);d(this,N2);d(this,z1);d(this,Z1);d(this,R1);d(this,O2);d(this,Ye);d(this,F2);d(this,h5);d(this,nt,!1);d(this,D2,[]);d(this,wt,new Map);d(this,q,new Map);d(this,P2,null);d(this,I2,[]);d(this,D,null);d(this,tt,null);d(this,it,null);d(this,me,[]);d(this,rt,null);d(this,F,null);d(this,N1,null);d(this,U,!1);d(this,kt,!1);d(this,Ve,null);d(this,O1,null);d(this,F1,!1);d(this,He,null);d(this,Ft,null);d(this,Qt,0);d(this,A,[]);c(this,d5,Xr).call(this),this._init("oo-select",We),this._useCss(L9)}_setProps(t,e,s){const r=this._props[t];(["path","text","value"].includes(t)?e===r:s===r)||(this._setPropMap[t]?this._setPropMap[t](e,r):this._setPropMap.$default(t,e,r))}static get observedAttributes(){return We.prop=Object.assign({},at.prop,We.prop),Object.keys(We.prop).map(t=>b(t))}_multipleChanged(){this._elements.viewText.innerHTML=this._props.multiple&&this._isEditable()?"&nbsp;":"-";let t=c(this,Ye,ds).call(this,this._props.path||this._props.value);if(t&&t.length>0){const e=this._props.multiple?JSON.stringify([t]):Array.isArray(t[0])?JSON.stringify(t[0]):t[0];this._props.path?this.path=e:this.value=e}}_checkStrictlyChanged(){if(!this._props.checkStrictly&&this.cascading){this.silent=!0;for(let t=l(this,A).length-1;t>=0;t--){const e=l(this,A)[t],s=e instanceof dt?e:l(this,wt).get(e);j(s).forEach(r=>{r.isLeaf||(r.selected=!1,this._selected(r),jt(l(this,A),e))})}this.silent=!1,this._setSelectValue()}}_showAllLevelsChanged(){if(this._elements.viewText.innerHTML=this._props.multiple&&this._isEditable()?"&nbsp;":"-",!this.cascading)return;if(this._props.path){const e=this._props.path;this.path="",this.path=e;return}let t=c(this,Ye,ds).call(this,this._props.value);if(t&&t.length>0){let e;l(this,kt)?(e=(this._props.multiple?t:j(t)).map(r=>{const a=l(this,q).get(r);return a?a.value:r}),e=this._props.multiple?e:e[0],u(this,q,new Map),l(this,wt).forEach((r,a)=>{l(this,q).set(a,r)})):(e=this._props.multiple?t.map(s=>d2(s)):d2(t),u(this,q,new Map),l(this,wt).forEach((s,r)=>{l(this,q).set(r.split(Lt).at(-1),s)})),this.value=Array.isArray(e)?JSON.stringify(e):e}}_render(){this._elements.viewText.innerHTML=this._props.multiple&&this._isEditable()?"&nbsp;":"-",u(this,nt,!1),this._elements.drop=n("div.drop.ooicon-drop_down"),this._elements.view.insertAdjacentElement("afterend",this._elements.drop),u(this,tt,this.querySelector("div.options-position-node")),l(this,tt)||(u(this,tt,n("div.options-position-node.hide")),this._elements.body.insertAdjacentElement("afterbegin",l(this,tt)),l(this,tt).addEventListener("click",()=>{c(this,R1,O3).call(this)})),u(this,it,this.querySelector("div.options-path-area")),l(this,it)||(u(this,it,n("div.options-path-area")),l(this,tt).append(l(this,it)),this._addPathItem(this)),u(this,D,this.querySelector("div.options-area")),l(this,D)||(u(this,D,n("div.options-area.invisible")),n.set(l(this,D),"html",'<div class="arrow"></div><div class="options-content"><slot name="items"></slot></div>'),l(this,tt).append(l(this,D)),u(this,N1,l(this,D).querySelector("div.arrow"))),u(this,rt,this.querySelector("div.options-result-area")),l(this,rt)||(u(this,rt,n("div.options-result-area")),n.set(l(this,rt),"html",'<div class="count">未选</div><i class="ooicon-icon_arrow_up"></i><oo-button type="simple" text="清空" ></oo-button>'),u(this,O1,l(this,rt).querySelector("div.count")),l(this,tt).append(l(this,rt))),u(this,F,this.querySelector("div.options-result-content")),l(this,F)||(u(this,F,n("div.options-result-content.hide.invisible")),l(this,tt).append(l(this,F))),this._props.allowInput?this._elements.input.removeAttribute("readonly"):this._elements.input.setAttribute("readonly",!0)}_removeItem(t){l(this,D2).splice(l(this,D2).indexOf(t),1),t.removeAttribute("slot")}_reloadOptionsCheckbox(){l(this,wt).forEach((t,e)=>{t._checkCheckbox(!0)})}_addToMap(t){const e=t.value,s=(r,a,h)=>{r.set(a,r.has(a)?j(r.get(a)).concat(h):h)};s(l(this,wt),e.join(Lt),t),s(l(this,q),this._valueToString(e),t)}_appendItem(t){l(this,D2).push(t),this._addToMap(t),t.setAttribute("slot","items"),t.setAttribute("skin",this._props.skin);const e=n.getSize(this._elements.view),s=n.getSize(t._elements.text),r=Math.max(s.x,e.x);r>l(this,Qt)&&(u(this,Qt,r),c(this,$1,D3).call(this)),this._props.size&&t.setAttribute("size",this._props.size),this._appendItemCheckValue(t),this._setPropMap.$default("value")}checkMaxWidth(t){t>l(this,Qt)&&(u(this,Qt,t),c(this,$1,D3).call(this))}_appendItemCheckValue(t){const e=this._props.path?this._parsePath():this._parseValue(),s=t.value,r=a=>{if(t.loading)this._checkLoadingOptionSelected(t);else if(c(this,O2,J5).call(this)?Ts(a,s):d2(a)===d2(s)){const p=l(this,A).indexOf(this._valueToString(s));p>-1&&(l(this,A)[p]=t,this._unselectValue(s,!0)),t.selected=!0}};this._props.multiple?e.forEach(a=>r(a)):r(e)}_valueToString(t){return c(this,O2,J5).call(this)?j(t).join(Lt):d2(j(t))}_textToString(t){return l(this,kt)?j(t).join(M4):d2(j(t))}_removeGroup(t){l(this,I2).splice(l(this,I2).indexOf(t),1),t.removeAttribute("slot")}_appendGroup(t){l(this,I2).push(t),t.setAttribute("slot","items"),t.setAttribute("skin",this._props.skin),this._props.size&&option.setAttribute("size",this._props.size)}_selected(t){if(this._props.multiple){const e=l(this,A);t.selected?!e.includes(t)&&e.push(t):e.includes(t)&&jt(e,t),this._setSelectValue()}else t.selected&&(this._emptyValue(t),u(this,A,[t]),this._setSelectValue());t.selected?this._addResultNode(t):this._removeResultNode(t),t.selected?this._addTagNode(t):this._removeTagNode(t)}_setSelectValue(){if(this.silent)return;const t=this._getSelectedValue(),e=!Ts(this.value,t);e&&(this.oldValue=t,this._props.value=Array.isArray(t)?JSON.stringify(t):t);const s=this._getSelectedText();this._props.text=Array.isArray(s)?JSON.stringify(s):s,this._setTextShow(this._getTextShow()),e&&(this._refreshCount(),this.unInvalidStyle(),l(this,nt)&&this._props.multiple&&this.positionOptionsTop(!0),this.dispatchEvent(new CustomEvent("change")))}_isEditable(){return!this._props.disabled&&!this._props.readmode&&!this._props.read&&!this._props.readonly}_inputValue(){this._emptyValue();const t=this._elements.input.value;this._props.value=t,this._elements.viewText.textContent=t??" ",this._props.text=t,t||(this._elements.viewText.innerHTML=this._props.multiple&&this._isEditable()?"&nbsp;":"-")}_emptyValue(t){const e=j(t);for(let s=l(this,A).length-1;s>=0;s--){const r=l(this,A)[s];e.includes(r)||(r instanceof dt?r.selected&&(r.selected=!1):this._unselectValue(r.split(Lt)),jt(l(this,A),r))}}_unselectValue(t,e){const s=this._valueToString(t);if(!e&&l(this,q).has(s))j(l(this,q).get(s)).forEach(r=>r.selected=!1);else{jt(l(this,A),s),this._removeResultNode(s),this._removeTagNode(s);const r=this._getLoadingOption(t);r&&j(r).forEach(a=>a.selected=!1),this._setSelectValue()}}_setTextShow(t){this._elements.input.value=t,this._props.multiple||(this._elements.viewText.textContent=t,this._elements.view.title=this._elements.view.textContent||""),t||(l(this,F).innerHTML="",this._elements.viewText.innerHTML=this._props.multiple&&this._isEditable()?"&nbsp;":"-")}_addResultNode(t){const e=n("div.result-node");e.insertAdjacentHTML("beforeend",`<div>${this._textToString(t.text)}</div><oo-button type="simple" text="清除"></oo-button>`),t instanceof dt&&(t.resultNode=e),c(this,Be,as).call(this,t,e,"click",l(this,F)),this._refreshCount()}_removeResultNode(t){t.resultNode?(t.resultNode.remove(),t.resultNode=null):c(this,N2,K5).call(this,t,".result-node",l(this,F)),this._refreshCount()}_checkTagNodes(){var t;this._props.multiple&&(this._elements.viewText.querySelectorAll("oo-tag.text").forEach(e=>{e.setAttribute("close",this._isEditable()?"on":"off"),this._props.readmode?e.setAttribute("readmode","true"):e.removeAttribute("readmode")}),(t=this.tooltipContent)==null||t.querySelectorAll("oo-tag").forEach(e=>{e.setAttribute("close",this._isEditable()?"on":"off"),this._props.readmode?e.setAttribute("readmode","true"):e.removeAttribute("readmode")}))}_addTagNode(t){if(this._props.multiple){const e=this._elements.viewText;["-","&nbsp;"].includes(e.innerHTML)&&(e.innerHTML="");const s=n("oo-tag.text");if(!this._isEditable()&&s.setAttribute("close","off"),s.setAttribute("text",this._textToString(t.text)),t instanceof dt&&(t.tagNode=s),this._props.maxTags){this._checkTooltip();const r=l(this,A).indexOf(t instanceof dt?t:this._valueToString(t.value));r>this._props.maxTags-1?c(this,Be,as).call(this,t,s,"close",this.tooltipContent,r-this._props.maxTags):c(this,Be,as).call(this,t,s,"close",e),this._checkMaxTags()}else c(this,Be,as).call(this,t,s,"close",e)}}_removeTagNode(t){t.tagNode?(t.tagNode.remove(),t.tagNode=null):(c(this,N2,K5).call(this,t,"oo-tag",this._elements.viewText),this.tooltipContent&&c(this,N2,K5).call(this,t,"oo-tag",this.tooltipContent)),this._checkMaxTags()}_checkMaxTags(){if(this._props.maxTags&&this.multiple){const t=this._elements.viewText.querySelectorAll("oo-tag.text");if(t.length>this._props.maxTags){this._checkTooltip();for(let e=this._props.maxTags;e<t.length;e++)this.tooltipContent.appendChild(t[e])}else if(t.length<this._props.maxTags&&this.tooltipContent){const e=this.tooltipContent.querySelectorAll("oo-tag"),s=Math.min(this._props.maxTags-t.length,e.length);for(let r=0;r<s;r++)l(this,Ft)?this._elements.viewText.insertBefore(e[r],l(this,Ft)):this._elements.viewText.appendChild(e[r]);this._checkTooltip()}else this._checkTooltip()}else if(this.tooltipContent){const t=this.tooltipContent.querySelectorAll("oo-tag");for(let e=0;e<t.length;e++)this._elements.viewText.appendChild(t[e]);this._checkTooltip()}}_checkTooltip(){let t=l(this,Ft);this._props.maxTags<l(this,A).length?(t||(u(this,Ft,t=n("oo-tag")),t.setAttribute("close","off"),this._elements.viewText.appendChild(t)),this.tooltipContent||(this.tooltipContent=n("div.tooltipContent"),this._isMobile()||(n.setStyles(this.tooltipContent,{display:"flex","flex-direction":"column","flex-wrap":"wrap","align-items":"flex-start",gap:"0.5em",padding:"0.25em","max-height":"17em","min-height":"3em",overflow:"auto"}),u(this,He,Ni(this.tooltipContent,t,{showOnEnterCotent:!0,position:"bottom"})))),t.textContent="+"+(l(this,A).length-this._props.maxTags)):c(this,j1,P3).call(this)}_refreshCount(){l(this,O1).textContent=l(this,A).length>0?`已选${l(this,A).length}项`:"未选",l(this,A).length===0&&l(this,F).offsetParent&&this._hideResultContent()}_setEvent(){super._setEvent(),this._elements.box.addEventListener("click",()=>{this._isEditable()&&c(this,z1,I3).call(this)}),this._elements.input.addEventListener("click",t=>{this._isEditable()&&(this._props.allowInput||c(this,z1,I3).call(this),t.stopPropagation())}),this._elements.input.addEventListener("input",t=>{this._inputValue(t)}),l(this,D).addEventListener("click",t=>{t.stopPropagation()}),l(this,D).addEventListener("mousedown",t=>{t.stopPropagation()}),this._content.firstElementChild.addEventListener("mousedown",t=>{t.stopPropagation()}),l(this,it).addEventListener("click",t=>{t.stopPropagation()}),l(this,it).addEventListener("mousedown",t=>{t.stopPropagation()}),l(this,rt).addEventListener("click",t=>{l(this,F).offsetParent?this._hideResultContent():this._showResultContent(),t.stopPropagation()}),l(this,rt).addEventListener("mousedown",t=>{t.stopPropagation()}),l(this,rt).querySelector("oo-button").addEventListener("click",t=>{this._emptyValue(),l(this,F).offsetParent&&this._hideResultContent(),t.stopPropagation()}),l(this,F).addEventListener("click",t=>{t.stopPropagation()})}_isMobile(){return window.matchMedia("only screen and (max-width: 767px)").matches}positionOptions(t,e=!1){e?$(this._positionOptions,200,this,[t]):this._positionOptions(t)}positionOptionsTop(t=!1){t?$(this._positionOptionsTop,5,this):this._positionOptionsTop()}_positionOptionsTop(){if(this._isMobile())return;let e=n.getSize(this._elements.box).y+10;l(this,U)||(e+=6),n.setStyles(l(this,D),{top:e+"px"})}_positionOptions(t){if(this._isMobile())return;this._positionOptionsTop();const e=n.getParentSrcollNode(this._elements.box),s=n.getOffsetParent(this._elements.box),r=n.getSize(e);let a=n.getSize(l(this,D));(t||this._openedOptions()).forEach(C=>{const g=n.getSize(C._elements.optionsArea);a.x+=g.x});const h=n.getPosition(s,e),p=n.getPosition(this._elements.box,s);if(n.setStyles(l(this,tt),{left:(h.x+p.x+a.x>r.x?r.x-h.x-a.x:p.x)+"px"}),!l(this,U)){const C=n.getSize(this._elements.box),g=this._props.optionWidth==="auto"?n.getSize(l(this,D)).x:C.x;n.setStyles(l(this,D),{"min-width":this._props.optionWidth==="auto"?"unset":g+"px"});const f=n.getSize(l(this,D)).x/2-6;n.setStyles(l(this,D).querySelector(".arrow"),{left:f+"px"})}}_showOptions(){this._props.readmode||(u(this,nt,!0),c(this,Z1,N3).call(this),this._positionOptions(),this._openedOptions().forEach(t=>{t._positionOptions()}),n.removeClass(l(this,tt),"hide"),n.removeClass(l(this,D),"invisible"),n.addClass(document.body,"ooui-select-visible"),n.addClass(l(this,D),"visible"),n.addClass(l(this,it),"hide"),window.setTimeout(()=>{n.removeClass(l(this,it),"hide")},200),this.ownerDocument.dispatchEvent(new MouseEvent("mousedown")),this.dispatchEvent(new CustomEvent("show")),u(this,P2,c(this,R1,O3).bind(this)),this.ownerDocument.addEventListener("mousedown",l(this,P2)),this.unInvalidStyle())}_hideOptions(){u(this,nt,!1),c(this,Z1,N3).call(this),n.removeClass(l(this,D),"visible"),n.addClass(l(this,D),"invisible"),n.removeClass(document.body,"ooui-select-visible"),this.ownerDocument.removeEventListener("mousedown",l(this,P2)),n.addClass(l(this,it),"hide"),window.setTimeout(()=>{n.addClass(l(this,tt),"hide")},200),this.dispatchEvent(new CustomEvent("hide"))}_openedOptions(){return l(this,me).filter(t=>t.option!==this).map(t=>t.option)}_hideResultContent(){const t=l(this,rt).querySelector("i");n.addClass(t,"ooicon-icon_arrow_up"),n.removeClass(t,"ooicon-drop_down"),n.addClass(l(this,F),"invisible"),n.removeClass(l(this,F),"visible"),setTimeout(()=>{n.addClass(l(this,F),"hide")},200)}_showResultContent(){const t=l(this,rt).querySelector("i");n.removeClass(t,"ooicon-icon_arrow_up"),n.addClass(t,"ooicon-drop_down"),n.removeClass(l(this,F),"hide"),setTimeout(()=>{n.removeClass(l(this,F),"invisible"),n.addClass(l(this,F),"visible")},1)}_cancelCurrentPath(){if(l(this,Ve)){const{option:t,pathNode:e}=l(this,Ve);n.removeClass(t===this?l(this,D):t._elements.optionsArea,"current"),n.removeClass(e,"current"),u(this,Ve,null)}}_setCurrentPath(t){const{option:e,pathNode:s}=t,r=e===this?l(this,D):e._elements.optionsArea;r&&n.addClass(r,"current"),n.addClass(s,"current"),u(this,Ve,t)}_removePathItem(t){var e;for(;l(this,me).length>t;){const{pathNode:s}=l(this,me).pop();(e=s.previousElementSibling)==null||e.remove(),s.remove()}}_addPathItem(t){t!==this&&(this._removePathItem(t.level),l(this,it).append(n("div.ooicon-arrow_forward")));const e=n("div.current",{text:t===this?"顶层":t._props.text||t._props.value});l(this,it).append(e);const s={option:t,pathNode:e};l(this,me).push(s),this._cancelCurrentPath(),this._setCurrentPath(s),e.addEventListener("click",r=>{this._cancelCurrentPath(),this._setCurrentPath(s);const a=l(this,me).findIndex(h=>h.pathNode===e);a>-1&&this._removePathItem(a+1),r.preventDefault()})}_useText(){const t=this.text;let e=this._props.multiple?j(t).map((s,r)=>{this._textToString(s)}).join(","):this._textToString(t);this._setTextShow(e),this._props.multiple&&t.length>0&&(c(this,j1,P3).call(this),this._elements.viewText.innerHTML="",l(this,F).innerHTML="",t.forEach((s,r)=>{const a=l(this,A)[r];if(a){a instanceof dt&&(a.tagNode&&(a.tagNode=null),a.resultNode&&(a.resultNode=null),s=this._textToString(a.text));const h=a instanceof dt?a.value:a.split(Lt);this._addResultNode({text:s,value:h}),this._addTagNode({text:s,value:h})}}))}_useValue(t){const e=this._props.path?this._parsePath():this._parseValue();if(!Ts(e,j(this.oldValue))){if(this.silent=!0,this._emptyValue(),t==="path"){u(this,F1,!!this._props.path);const a=this._pathToValue();this._props.value=Array.isArray(a)?JSON.stringify(a):a,l(this,U)&&!this._props.showAllLevels&&(!this.oldPath&&this._props.path?(u(this,q,new Map),l(this,wt).forEach((h,p)=>{l(this,q).set(p,h)})):this.oldPath&&!this._props.path&&(u(this,q,new Map),l(this,wt).forEach((h,p)=>{l(this,q).set(p.split(Lt).at(-1),h)}))),this.oldPath=this._props.path}const s=this.text,r=(a,h)=>{if(!a)return;const p=l(this,q).get(this._valueToString(a));p?j(p).forEach(C=>C.selected=!0):this._useLoadingValue(a,h)};this._props.multiple?j(e).forEach((a,h)=>{r(a,s[h])}):r(e,s),l(this,U)||l(this,A).length===0&&(this._elements.input.value=this.value),this._setTextShow(this._getTextShow()),this._refreshCount(),this.oldValue=e,this.silent=!1}}_useLoadingValue(t,e){const s={value:t,text:e||t},r=this._getLoadingOption(t);j(r).forEach(a=>a.selected=!0),l(this,A).push(this._valueToString(t)),this._addResultNode(s),this._addTagNode(s)}_getLoadingOption(t){if(c(this,O2,J5).call(this)){const e=[...t];for(;e.length;){e[e.length-1]=S4;const s=l(this,wt).get(e.join(Lt));if(!s)e.pop();else return s}}}_checkLoadingOptionSelected(t){const e=t.value.join(Lt);l(this,A).forEach(s=>{if(!(s instanceof dt)){const r=s.split(Lt);for(;r.length;)if(r[r.length-1]=S4,r.join(Lt)===e){t.selected=!0;return}else r.pop()}})}_getTextShow(){let t=this._getAllLevelsData("text");return t=t.map(e=>{const s=j(e);return l(this,kt)?s.join(M4):s.at(-1)}).filter(e=>e!=null),this._props.multiple?t.join(", "):t[0]||""}_getSelectedText(){return this._getData("text")}_getSelectedValue(){return this._getData("value")}_getData(t){let e=this._getAllLevelsData(t);return l(this,kt)||(e=e.map(s=>j(s).at(-1)).filter(s=>s!=null)),this._props.multiple?e:e[0]||""}_getAllLevelsData(t){const e=l(this,A).map(s=>typeof s=="string"?s.split(Lt):t==="text"?s.text||s.value:s.value||s.text);return this._props.allowInput&&e.length===0&&e.push(this._elements.input.value),e}_pathToValue(){const e=this._parsePath().map(s=>l(this,kt)?s:j(s).at(-1));return this._props.multiple?e:e.at(-1)}_parsePath(){const t=c(this,Ye,ds).call(this,this._props.path);let e=this._props.multiple?1:0;if(l(this,U)&&(e+=1),t)switch(e){case 2:return oe(t)?t:[];case 1:return oe(t)?[]:Array.isArray(t)?t:[];default:return oe(t)||Array.isArray(t)?null:t}else return e===0?null:[]}_parseValue(){return c(this,F2,Q5).call(this,this._props.value)}_parseText(){return c(this,F2,Q5).call(this,this._props.text)}get value(){return this._parseValue()||""}set value(t){this._props.path||this.setAttribute("value",t)}get text(){return c(this,F2,Q5).call(this,this._props.text||this._props.value)||""}set text(t){this._props.text!==t&&this.setAttribute("text",t)}get path(){return this._isEditable()?this._getAllLevelsData("value"):this._props.path?this._parsePath():!this.cascading||this._props.showAllLevels?j(this._parseValue()):[]}set path(t){this._props.path!==t&&this.setAttribute("path",t)}get dropped(){return l(this,nt)}set cascading(t){t!==l(this,U)&&(u(this,U,!!t),u(this,kt,l(this,U)&&this._props.showAllLevels),n.checkClass(this._elements.body,"cascading",l(this,U)),n.checkClass(l(this,N1),"hide",l(this,U)))}get cascading(){return l(this,U)}};d5=new WeakSet,Xr=function(){this._setPropMap=Object.assign(this._setPropMap,{disabled:()=>{n.toggleAttr(this._elements.input,"disabled",this._props.disabled),n.checkClass(this._elements.box,"disabled",this._props.disabled),n.checkClass(this._elements.label,"disabled",this._props.disabled),n.checkClass(this._elements.drop,"disabled",this._props.disabled),this._checkTagNodes()},optionWidth:()=>{this._props.optionWidth==="auto"&&l(this,D)&&n.setStyle(l(this,D),"min-width","unset")},value:()=>{!this._props.path&&this._useValue()},path:()=>{this._useValue("path")},text:()=>{this._useText()},readonly:()=>{this._checkTagNodes()},readmode:()=>{n.checkClass(this._elements.box,"readmode",this._props.readmode),n.checkClass(this._elements.label,"readmode",this._props.readmode),this._props.readmode?n.toggleAttr(this._elements.body,"style",this._props.viewStyle):n.toggleAttr(this._elements.body,"style",""),this._checkTagNodes(),this._setPropMap.viewStyle(),this._setPropMap.required()},autoSize:()=>{this._props.autoSize?n.setStyle(this._elements.view,"width",`calc(${l(this,Qt)}px + 2rem)`):n.setStyle(this._elements.view,"width","auto")},read:()=>{this._checkTagNodes()},allowInput:()=>{this._props.allowInput?(this._elements.input.removeAttribute("readonly"),this._elements.input.value=this._props.value,this._inputValue()):this._elements.input.setAttribute("readonly",!0)},viewStyle:()=>{this._props.readmode&&n.toggleAttr(this._elements.body,"style",this._props.viewStyle)},multiple:()=>{n.checkClass(this._elements.body,"multiple",this._props.multiple),this._reloadOptionsCheckbox(),this._multipleChanged(),this._checkMaxTags()},checkStrictly:()=>{this._reloadOptionsCheckbox(),this._checkStrictlyChanged()},showAllLevels:()=>{u(this,kt,l(this,U)&&this._props.showAllLevels),this._showAllLevelsChanged()},maxTags:()=>{this._checkMaxTags()}})},nt=new WeakMap,D2=new WeakMap,wt=new WeakMap,q=new WeakMap,P2=new WeakMap,I2=new WeakMap,D=new WeakMap,tt=new WeakMap,it=new WeakMap,me=new WeakMap,rt=new WeakMap,F=new WeakMap,N1=new WeakMap,U=new WeakMap,kt=new WeakMap,Ve=new WeakMap,O1=new WeakMap,F1=new WeakMap,He=new WeakMap,Ft=new WeakMap,Qt=new WeakMap,$1=new WeakSet,D3=function(){this._props.autoSize&&(this.setMinWidthTimer&&window.clearTimeout(this.setMinWidthTimer),this.setMinWidthTimer=setTimeout(()=>{n.setStyle(this._elements.view,"width",`calc(${l(this,Qt)}px + 1rem)`),this.setMinWidthTimer=null},10))},A=new WeakMap,j1=new WeakSet,P3=function(){l(this,Ft)&&(l(this,Ft).remove(),u(this,Ft,null)),this.tooltipContent&&(this.tooltipContent.remove(),this.tooltipContent=null),l(this,He)&&(l(this,He).close(),u(this,He,null))},Be=new WeakSet,as=function(t,e,s,r,a){const h=!(t instanceof dt),p=this._valueToString(t.value);e.dataset.value=p,e.addEventListener(s,C=>{h?this._unselectValue(t.value):t.selected=!1,C.stopPropagation()}),x9(r,e,a??l(this,A).indexOf(h?p:t))},N2=new WeakSet,K5=function(t,e,s){const r=t instanceof dt?this._valueToString(t.value):t;s.querySelectorAll(e).forEach(a=>{a.dataset.value===r&&a.remove()})},z1=new WeakSet,I3=function(){u(this,nt,!l(this,nt)),l(this,nt)?this._showOptions():this._hideOptions()},Z1=new WeakSet,N3=function(){n.checkClass(this._elements.drop,"down",l(this,nt)),n.checkClass(this._elements.box,"focus",l(this,nt)),n.checkClass(this._elements.label,"focus",l(this,nt))},R1=new WeakSet,O3=function(){l(this,F).offsetParent?this._hideResultContent():this._hideOptions()},O2=new WeakSet,J5=function(){return l(this,kt)||l(this,F1)},Ye=new WeakSet,ds=function(t){let e;try{e=JSON.parse(t),typeof e=="number"&&(e=e.toString())}catch{e=t}return e},F2=new WeakSet,Q5=function(t){const e=c(this,Ye,ds).call(this,t),s=c(this,h5,Gr).call(this);if(e)switch(s){case 2:return oe(e)?e:[];case 1:return oe(e)?[]:Array.isArray(e)?e:[];default:return oe(e)||Array.isArray(e)?null:e}else return s===0?null:[]},h5=new WeakSet,Gr=function(){let t=this._props.multiple?1:0;return l(this,U)&&this._props.showAllLevels&&(t+=1),t},m(We,"prop",{read:!1,allowInput:!1,optionWidth:"fixed",multiple:!1,checkStrictly:!1,showAllLevels:!0,text:"",path:"",maxTags:0});let Ds=We;const y9=`<div class="content">\r
	<slot name="before-outer"></slot>\r
	<div class="label hide">\r
		<div class="labelText"></div>\r
		<slot name="label"></slot>\r
	</div>\r
	<div class="body" style="width: 100%;">\r
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
`,w9=`* {\r
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
    --bg:  transparent;\r
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
    /* align-items: center; */\r
    height: 100%;\r
}\r
.flex-end{\r
    justify-content: flex-end;\r
}\r
.hide{\r
    display: none !important;\r
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
.body{\r
    flex: 1;\r
}\r
.input {\r
    border: 0;\r
    outline: none;\r
    color: var(--input);\r
    border-radius: var(--radius);\r
    padding: 0.5em 0.6em;\r
    height: 100%;\r
    min-height: 1.89em;\r
    /*min-width: 12.28em;*/\r
    box-shadow: none;\r
    width: inherit;\r
    /*min-width: 13.365875em;*/\r
    font-size: 1em;\r
    background: transparent;\r
    position: relative;\r
    display: flex;\r
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
    background-color: transparent;\r
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
    display: flex;\r
    flex-wrap: wrap;\r
    align-items: center;\r
    padding: 0.051em;\r
    gap: 0.3em;\r
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
    /* position: absolute; */\r
    width: 1px;\r
    /* height: 1px; */\r
    /* bottom: 0; */\r
    /* left: 50%; */\r
    border: 0;\r
    outline: none;\r
    overflow: hidden;\r
    display: block !important;\r
    /* font-size: 1px; */\r
    opacity: 0;\r
    padding-left: 0;\r
    padding-right: 0;\r
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
\r
\r
/* 移动端，标签在上，输入框下划线 skin-mode='mobile' */\r
.content.mobile{\r
    --oo-shadow-border: inset 0 -1px 0 0px var(--oo-color-gray-d);\r
    --oo-shadow-border-focus: inset 0 -1px 0 0px var(--oo-color-main);\r
    --oo-shadow-border-error: inset 0 -1px 0 0px var(--oo-color-error);\r
    --oo-default-radius: 0;\r
    --oo-area-radius: 0;\r
}\r
\r
.content.mobile {\r
    display: flex !important;\r
    align-items: unset !important;\r
    position: relative !important;\r
    height: unset !important;\r
    flex-direction: column;\r
}\r
.content.mobile .label {\r
    color: var(--oo-color-text3);\r
}\r
\r
.content.mobile .labelText {\r
    width: unset !important;\r
    min-width: unset !important;\r
    max-width: unset !important;\r
    display: block !important;\r
}\r
.content.mobile .input {\r
    padding: 0.35em;\r
    font-size: 1.125em;\r
}\r
.content.mobile .placeholder{\r
    padding: 0.35em;\r
}\r
\r
.content.mobile .box.readmode .input{\r
    padding: 0.5em 0.35em;\r
    font-size: 1.125em;\r
    background-color: transparent !important;\r
    border-bottom: 1px solid #dddddd !important;\r
}\r
.content.mobile .hide {\r
    display: none !important;\r
}\r
\r
/* ---------------------------------------------- */`,u5=class u5 extends N{constructor(){super();d(this,V1);d(this,qe);d(this,c5);d(this,p5);m(this,"_elements",{label:null,input:null,box:null,body:null,prefix:null,suffix:null,labelText:null,placeholder:null,inputContent:null,inputValidity:null,invalidHint:null,requiredFlag:null});m(this,"_setPropMap",{leftIcon:t=>{n.removeClass(this._elements.prefix,`ooicon-${t} left-icon`),this._props.leftIcon&&n.addClass(this._elements.prefix,`ooicon-${this._props.leftIcon} left-icon`)},rightIcon:t=>{n.removeClass(this._elements.suffix,`ooicon-${t} right-icon`),this._props.rightIcon&&n.addClass(this._elements.suffix,`ooicon-${this._props.rightIcon} right-icon`)},skinMode:t=>{t&&n.removeClass(this._content,t),this._props.skinMode&&n.addClass(this._content,this._props.skinMode)},itemType:t=>{this._props.itemType||(this._props.itemType="simple"),t!==this._props.itemType&&this._elements.inputContent.querySelectorAll("oo-org").forEach(s=>{s.setAttribute("type",this._props.itemType)})},isTooltip:()=>{const t=this._props.isTooltip!==!1;this._elements.inputContent.querySelectorAll("oo-org").forEach(s=>{s.setAttribute("is-tooltip",t)})},disabled:()=>{n.toggleAttr(this._elements.input,"disabled",this._props.disabled),n.checkClass(this._elements.box,"disabled",this._props.disabled),n.checkClass(this._elements.label,"disabled",this._props.disabled)},readmode:()=>{(!this.value||!this.value.length)&&n.checkClass(this._elements.placeholder,"hide",this._props.readmode),n.toggleAttr(this._elements.input,"readmode",this._props.readmode),n.checkClass(this._elements.box,"readmode",this._props.readmode),n.checkClass(this._elements.label,"readmode",this._props.readmode),this._props.readmode?n.toggleAttr(this._elements.body,"style",this._props.viewStyle):n.toggleAttr(this._elements.body,"style",""),this._setPropMap.required()},label:()=>{this._props.label?(this._elements.labelText.textContent=this._props.label,n.removeClass(this._elements.label,"hide")):(this._elements.labelText.textContent="",n.addClass(this._elements.label,"hide"))},skin:()=>{this._useSkin(this._props.skin)},value:()=>{c(this,V1,F3).call(this,this._props.value)},placeholder:()=>{this.value&&this.value.length?n.addClass(this._elements.placeholder,"hide"):(this._elements.inputContent.innerHTML=this._props.readmode?'<div style="padding: 0.1em;">-</div>':'<div style="padding: 0.1em;">&nbsp;</div>',!this._props.readmode&&!this._props.readonly&&!this._props.disabled&&n.removeClass(this._elements.placeholder,"hide"),this._props.placeholder?this._elements.placeholder.textContent=this._props.placeholder:this._elements.placeholder.innerHTML="&nbsp")},readonly:()=>{this._setPropMap.placeholder()},inputStyle:()=>{n.toggleAttr(this._elements.input,"style",this._props.inputStyle)},labelStyle:()=>{n.toggleAttr(this._elements.labelText,"style",this._props.labelStyle),this._setPropMap.labelAlign()},labelAlign:()=>{switch(this._props.labelAlign){case"right":n.setStyle(this._elements.labelText,"justify-content","flex-end");break;case"center":n.setStyle(this._elements.labelText,"justify-content","center");break;default:n.setStyle(this._elements.labelText,"justify-content","flex-start")}},viewStyle:()=>{this._props.readmode&&n.toggleAttr(this._elements.body,"style",this._props.viewStyle)},bgcolor:()=>{this._props.bgcolor?n.setStyle(this._elements.box,"background-color",this._props.bgcolor):n.setStyle(this._elements.box,"background-color","transparent")},required:()=>{this._props.required?this._elements.inputValidity.setAttribute("required",this._props.required):this._elements.inputValidity.removeAttribute("required"),n.checkClass(this._elements.requiredFlag,"hide",!this._props.required||this._props.readmode||this._props.disabled)},$default:()=>{}})}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}addValue(t){const e=t.dn||t;switch(this._props.value){case"string":(!this._props.count||this._props.value.split(/\s*,\s*/g).length<this._props.count)&&(this._props.value+=", "+e);break;case"array":(!this._props.count||this._props.value.length<this._props.count)&&this._props.value.push(e);break}c(this,qe,hs).call(this,e)}setTooltip(t,e){const s=t.detail.target;s.tooltip=e}_init(t,e,s,r){this._initialize(t,e||u5,s||y9,r||w9)}_setEvent(){super._setEvent(),this._elements.box.addEventListener("mouseover",()=>{!this._props.disabled&&!this._props.readonly&&(n.addClass(this._elements.box,"focus"),n.addClass(this._elements.label,"focus"))}),this._elements.box.addEventListener("mouseout",()=>{!this._props.disabled&&!this._props.readonly&&(n.removeClass(this._elements.box,"focus"),n.removeClass(this._elements.label,"focus"))}),this._elements.box.addEventListener("click",()=>{!this._props.disabled&&!this._props.readonly&&(this.dispatchEvent(new MouseEvent("select")),(this._props.selConfig||this._props.selOptions)&&this.select())}),this._elements.inputValidity.addEventListener("invalid",t=>{c(this,p5,Jr).call(this,t.target.validity)})}select(){const t=this._props.selOptions?i2("return "+this._props.selOptions):{};this._props.selConfig&&(t.config=this._props.selConfig),t.selected=this.value,t.count=this._props.count,n.getPositionParent(this),(this._props.selTitle,void 0).then(e=>{if(e.type==="ok"){const s=e.data.map(r=>r.dn||r.id||r);c(this,V1,F3).call(this,s),this.unInvalidStyle(),this._props.validityBlur&&this.checkValidity(),this.dispatchEvent(new MouseEvent("change"))}else this.dispatchEvent(new MouseEvent("cancel"))})}focus(){this._elements.input.focus()}blur(){this._elements.input.blur()}setCustomValidity(){return this._elements.inputValidity.setCustomValidity(...arguments)}checkValidity(){return n.isDisplayNone(this)?!0:(this.dispatchEvent(new CustomEvent("validity")),this._elements.inputValidity.checkValidity(...arguments))}reportValidity(){return this.dispatchEvent(new CustomEvent("validity")),this._elements.inputValidity.reportValidity(...arguments)}unInvalidStyle(){this._elements.inputValidity.setCustomValidity(""),n.removeClass(this._elements.inputValidity,"invalid"),this._elements.invalidHint.textContent="",n.removeClass(this._elements.invalidHint,"show")}_connected(){this._setPropMap.labelAlign()}};V1=new WeakSet,F3=function(t){let e=t;e=Array.isArray(e)?e:[e],e=this._props.count?e.slice(0,this._props.count):e,this._props.value=e,e&&e.length?(n.addClass(this._elements.placeholder,"hide"),n.empty(this._elements.inputContent),c(this,qe,hs).call(this,e),this._elements.inputValidity.value=e):(this._elements.inputContent.innerHTML=this._props.readmode?'<div style="padding: 0.1em;">-</div>':'<div style="padding: 0.1em;">&nbsp;</div>',!this._props.readmode&&!this._props.readonly&&!this._props.disabled&&n.removeClass(this._elements.placeholder,"hide"),this._props.placeholder?this._elements.placeholder.textContent=this._props.placeholder:this._elements.placeholder.innerHTML="&nbsp")},qe=new WeakSet,hs=function(t){switch(v(t)){case"string":const e=t.split(/\s*,\s*/g);if(e.length===1){if(e[0]){const s=n("oo-org",{value:e[0],type:this._props.itemType});this._props.itemType==="text"&&s.setAttribute("type","text"),this._props.isTooltip===!1&&s.setAttribute("is-tooltip",!1),s.addEventListener("tooltip",r=>{this.dispatchEvent(new CustomEvent("tooltip",{detail:{target:r.target}}))}),this._elements.inputContent.append(s)}}else c(this,qe,hs).call(this,e);break;case"array":t.forEach(s=>{c(this,qe,hs).call(this,s)});break}},c5=new WeakSet,Kr=function(){return this._elements.inputValidity.validity.customError||this._props.validity&&this._elements.inputValidity.setCustomValidity(this._props.validity),this._elements.invalidHint&&(this._elements.invalidHint.textContent=this._elements.inputValidity.validationMessage),this._elements.inputValidity.validationMessage},p5=new WeakSet,Jr=function(t){t.valueMissing&&(lp=Tt.get("index").o("component"),this._elements.inputValidity.setCustomValidity(lp.t("valueMissing",{label:this._props.label||lp.this}))),this.dispatchEvent(new CustomEvent("invalid",{detail:t})),n.addClass(this._elements.inputValidity,"invalid"),c(this,c5,Kr).call(this)&&n.addClass(this._elements.invalidHint,"show")},m(u5,"prop",{leftIcon:"",rightIcon:"create",itemType:"text",isTooltip:!0,label:"",inputStyle:"",labelStyle:"",labelAlign:"right",viewStyle:"",bgcolor:"#ffffff",skin:"",placeholder:"",disabled:!1,readonly:!1,readmode:!1,value:[],selConfig:"",selOptions:"",selTitle:"",count:0,validity:"",validityBlur:!1,skinMode:"",required:!1});let $i=u5;class k9 extends $i{constructor(){super(),this._init("oo-selector")}get value(){const i=typeOf(this._props.value)==="string"&&this._props.value?this._props.value.split(/\s*,\s*/g):this._props.value;return this._props.count===1?i[0]:this._props.count?i.slice(0,this._props.count):i}set value(i){this.setAttribute("value",i)}}const E9=`* {\r
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
`,M9=`
<div class="content">
</div>
`,m5=class m5 extends N{constructor(){super();d(this,H1);m(this,"_setPropMap",{rows:()=>{c(this,H1,$3).call(this)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)}});this._initialize("oo-skeleton",m5,M9,E9)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_render(){c(this,H1,$3).call(this)}};H1=new WeakSet,$3=function(){n.empty(this._content);try{const t=parseInt(this._props.rows);for(let e=0;e<t;e++)this._content.append(n("div.item"))}catch{}},m(m5,"prop",{rows:"5",cssLink:"",skin:""});let ji=m5;const S9=`<div class="content">\r
		<slot name="before-outer"></slot>\r
		<div class="label hide">\r
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
`,A9=`* {\r
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
    height: 100%;\r
    display: flex;\r
    align-items: center;\r
}\r
\r
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
    display: none !important;\r
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
`,g5=class g5 extends N{constructor(){super();d(this,C5);m(this,"_elements",{label:null,labelText:null,box:null,prefix:null,suffix:null,bar:null,barArea:null,button:null,buttonArea:null,invalidHint:null,prefixText:null,suffixText:null,barIconArea:null,barTextArea:null});m(this,"_setPropMap",{leftIcon:t=>{this._setIcon(this._elements.prefix,t,this._props.leftIcon)},rightIcon:t=>{this._setIcon(this._elements.suffix,t,this._props.rightIcon)},trueText:()=>{const t=this._getTrueLabelNode();t.textContent=this._props.trueText,n.checkClass(t,"hide",!this._props.trueText)},falseText:()=>{const t=this._getFalseLabelNode();t.textContent=this._props.falseText,n.checkClass(t,"hide",!this._props.trueText)},labelStyle:()=>{n.toggleAttr(this._elements.labelText,"style",this._props.labelStyle),this._setPropMap.labelAlign()},labelAlign:()=>{switch(this._props.labelAlign){case"right":n.setStyle(this._elements.labelText,"justify-content","flex-end");break;case"center":n.setStyle(this._elements.labelText,"justify-content","center");break;default:n.setStyle(this._elements.labelText,"justify-content","flex-start")}},innerTrueText:()=>{this.isTrue&&(this._elements.barTextArea.textContent=this._props.innerTrueText)},innerFalseText:()=>{this.isTrue||(this._elements.barTextArea.textContent=this._props.innerFalseText)},buttonTrueText:()=>{this.isTrue&&!this._props.buttonTrueIcon&&(this._elements.buttonArea.textContent=this._props.buttonTrueText.substring(0,1))},buttonFalseText:()=>{!this.isTrue&&!this._props.buttonFalseIcon&&(this._elements.buttonArea.textContent=this._props.buttonFalseText.substring(0,1))},trueIcon:t=>{this._setIcon(this.this._getTrueLabelNode(),t,this._props.trueIcon)},falseIcon:t=>{this._setIcon(this._getFalseLabelNode(),t,this._props.falseIcon)},innerTrueIcon:t=>{this.isTrue&&this._setIcon(this._elements.barIconArea,t,this._props.innerTrueIcon)},innerFalseIcon:t=>{this.isTrue||this._setIcon(this._elements.barIconArea,t,this._props.innerFalseIcon)},buttonTrueIcon:t=>{this.isTrue&&this._setIcon(this._elements.buttonArea,t,this._props.buttonTrueIcon,!0)},buttonFalseIcon:t=>{this.isTrue||this._setIcon(this._elements.buttonArea,t,this._props.buttonFalseIcon,!0)},value:()=>{this.value=this._props.value},width:()=>{this._props.width?this._elements.bar.style.width=this._props.width:this._elements.bar.style.width="unset"},trueLocation:()=>{const t=this._getFalseLabelNode(),e=this._getTrueLabelNode();n.addClass(t,"falseLabel"),n.removeClass(t,"trueLabel"),n.addClass(e,"trueLabel"),n.removeClass(e,"falseLabel"),n.checkClass(this._elements.box,"trueIsLeft",this._props.trueLocation.toLowerCase()==="left")},disabled:()=>{n.checkClass(this._elements.box,"disabled",this._props.disabled),n.checkClass(this._elements.label,"disabled",this._props.disabled)},readonly:()=>{n.checkClass(this._elements.box,"readonly",this._props.readonly),n.checkClass(this._elements.label,"readonly",this._props.readonly)},label:()=>{this._props.label?(this._elements.labelText.textContent=this._props.label,n.removeClass(this._elements.labelText,"hide"),n.removeClass(this._elements.label,"hide")):(this._elements.labelText.textContent="",n.addClass(this._elements.labelText,"hide"),n.removeClass(this._elements.label,"hide"))},skin:()=>{this._useSkin(this._props.skin)},validity:()=>{},$default:t=>{}});this._init("oo-switch")}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_setIcon(t,e,s,r){n.removeClass(t,`ooicon-${e}`),s?(n.addClass(t,`ooicon-${s}`),n.removeClass(t,"hide")):r||n.addClass(t,"hide")}_getTrueLabelNode(){return this._props.trueLocation.toLowerCase()!=="left"?this._elements.suffixText:this._elements.prefixText}_getFalseLabelNode(){return this._props.trueLocation.toLowerCase()!=="left"?this._elements.prefixText:this._elements.suffixText}_init(t,e,s,r){this._initialize(t,e||g5,s||S9,r||A9)}_render(){super._render(),this._setPropMap.value(),this._props.label&&this._setPropMap.label(),this._setPropMap.trueLocation()}_setEvent(){super._setEvent(),this._elements.bar.addEventListener("click",t=>{!this._props.disabled&&!this._props.readonly&&(this.value=c(this,C5,Qr).call(this),this.dispatchEvent(new Event("change",t)))})}set value(t){this._props.trueValue&&this._props.falseValue?this._props.booleanValue=t===this._props.trueValue:this._props.booleanValue=!(!t||t==="false"),n.checkClass(this._elements.box,"false",!this._props.booleanValue),["innerTrueText","innerFalseText","buttonTrueText","buttonFalseText","innerTrueIcon","innerFalseIcon","buttonTrueIcon","buttonFalseIcon"].forEach(e=>{var s,r;(r=(s=this._setPropMap)[e])==null||r.call(s)})}get value(){return this._props.trueValue&&this._props.falseValue?this._props.booleanValue?this._props.trueValue:this._props.falseValue:this._props.booleanValue}get isTrue(){return this._props.booleanValue}};C5=new WeakSet,Qr=function(){return this._props.trueValue&&this._props.falseValue?this._props.booleanValue?this._props.falseValue:this._props.trueValue:!this._props.booleanValue},m(g5,"prop",{leftIcon:"",rightIcon:"",trueLocation:"right",labelStyle:"",labelAlign:"right",trueText:"",falseText:"",innerTrueText:"",innerFalseText:"",buttonTrueText:"",buttonFalseText:"",trueIcon:"",falseIcon:"",innerTrueIcon:"",innerFalseIcon:"",buttonTrueIcon:"",buttonFalseIcon:"",trueValue:"",falseValue:"",disabled:!1,readonly:!1,width:"",label:"",validity:"",validityBlur:!1,value:"true",booleanValue:!0,skin:""});let zi=g5;const T9=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    display: block;\r
}\r
`,D9=`
<div class="content">
    <slot name="content"></slot>
</div>
`,b5=class b5 extends N{constructor(){super();d(this,f5);d(this,v5);m(this,"_setPropMap",{css:()=>{this.useCss(this._props.css)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},label:()=>{this.label&&(this.label.querySelector(".text").textContent=this._props.label,this.tabs.showSlider())},icon:t=>{if(this.label){const e=this.label.querySelector(".icon");this._props.icon?e?(n.removeClass(e,`ooicon-${t}`),n.addClass(e,`ooicon-${this._props.icon}`)):this.label.insertAdjacentElement("afterbegin",n(`div.icon.ooicon-${this._props.icon}`)):e&&e.remove(),this.tabs&&this.tabs.showSlider()}},rightIcon:t=>{if(this.label){const e=this.label.querySelector(".rightIcon");this._props.rightIcon?e?(n.removeClass(e,`ooicon-${t}`),n.addClass(e,`ooicon-${this._props.rightIcon}`)):this.label.insertAdjacentElement("beforeend",n(`div.rightIcon.ooicon-${this._props.rightIcon}`)):e&&e.remove(),this.tabs&&this.tabs.showSlider()}}});m(this,"label",null);m(this,"tabs",null);this._initialize("oo-tab",b5,D9,T9)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_render(){this._fillContent("content")}_connected(){this.tabs=n.getParent(this,"oo-tabs"),this.tabs&&c(this,f5,t0).call(this)}_disconnected(){this.label&&this.label.remove()}selected(){this.label&&n.addClass(this.label,"current"),n.removeClass(this,"hide"),this.dispatchEvent(new CustomEvent("current"))}unselected(){this.label&&n.removeClass(this.label,"current"),n.addClass(this,"hide"),this.dispatchEvent(new CustomEvent("uncurrent"))}};f5=new WeakSet,t0=function(){this.label||c(this,v5,e0).call(this);const t=this.previousElementSibling;t&&t.label?t.label.insertAdjacentElement("afterend",this.label):this.tabs._elements.labels.insertAdjacentElement("afterbegin",this.label),this.setAttribute("slot","pane"),this!==this.tabs.currentTab&&n.addClass(this,"hide"),this.tabs._checkCurrentTab()},v5=new WeakSet,e0=function(){this.label=n("div.label"),this._props.icon&&this.label.append(n(`div.icon.ooicon-${this._props.icon}`)),this.label.append(n("div.text",{text:this._props.label||"New Tab"})),this._props.rightIcon&&this.label.append(n(`div.rightIcon.ooicon-${this._props.rightIcon}`)),this.label.addEventListener("click",()=>{this.tabs.setCurrent(this)})},m(b5,"prop",{css:"",cssLink:"",skin:"",label:"",icon:"",rightIcon:""});let Zi=b5;const P9=`* {\r
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
`,I9=`
<div class="content tabs">
    <div class="labels">
        <div class="slider"></div>
        <slot name="label"></slot>
    </div>
    <div class="panes">
        <slot name="pane"></slot>
    </div>
</div>
`,L5=class L5 extends N{constructor(){super();d(this,_5);m(this,"_elements",{labels:null,panes:null,slider:null});m(this,"_setPropMap",{css:()=>{this.useCss(this._props.css)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},position:t=>{n.removeClass(this._content,t),n.addClass(this._content,this._props.position),n.removeClass(this._elements.labels,t),n.addClass(this._elements.labels,this._props.position),n.removeClass(this._elements.slider,t),n.addClass(this._elements.slider,this._props.position)},current:()=>{this._checkCurrentTab()}});m(this,"currentTab",null);this._initialize("oo-tabs",L5,I9,P9)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_checkCurrentTab(){const e=this.querySelectorAll("oo-tab").item(this._props.current);e&&e!==this.currentTab&&this.setCurrent(e)}setCurrent(t){if(this.currentTab&&(this.currentTab.unselected(),this.currentTab=null),t){const e=this.querySelectorAll("oo-tab");let s=0;for(const r of e){if(r===t)break;s++}this._props.current=s,this.currentTab=t,t.selected(),this.showSlider()}else c(this,_5,s0).call(this)}showSlider(){const t=n.getPosition(this.currentTab.label,this._elements.labels),e=n.getSize(this.currentTab.label);if(this._props.position==="top"||this._props.position==="bottom"){const s=this._elements.labels.scrollLeft,r=t.x+s;n.setStyles(this._elements.slider,{left:r+"px",width:e.x+"px"})}else{const s=this._elements.labels.scrollTop,r=t.y+s;n.setStyles(this._elements.slider,{top:r+"px",height:e.y+"px"})}}};_5=new WeakSet,s0=function(){n.setStyles(this._elements.slider,{left:0,width:0})},m(L5,"prop",{css:"",cssLink:"",skin:"",position:"top",current:0});let Ri=L5;const N9=`<div class="button content">\r
	<div class="box">\r
		<div class="menu ooicon-menu hide"></div>\r
		<div class="prefix"></div>\r
		<div class="text"></div>\r
		<div class="suffix"></div>\r
		<div class="close ooicon-close"></div>\r
	</div>\r
</div>\r
`,O9=`body{\r
    display: inline-block;\r
}\r
* {\r
    box-sizing: border-box;\r
    /*user-select: none;*/\r
}\r
.content{\r
    --color: var(--oo-color-text);\r
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
.readmode .button{\r
    cursor: default;\r
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
    /*justify-content: baseline;*/\r
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
    display: none !important;\r
}\r
`,B1=class B1 extends Ls{constructor(){super();d(this,x5);m(this,"_elements",{box:null,text:null,prefix:null,suffix:null,button:null,close:null,menu:null});c(this,x5,n0).call(this),this._init("oo-tag",B1,N9,O9)}_setEvent(){this._elements.close&&this._elements.close.addEventListener("click",t=>{this.shadowRoot.dispatchEvent(new MouseEvent("close",t)),t.stopPropagation()}),this._elements.menu&&this._elements.menu.addEventListener("click",t=>{this.shadowRoot.dispatchEvent(new MouseEvent("menu",t)),t.stopPropagation()})}};x5=new WeakSet,n0=function(){this.setPropMap=Object.assign(this._setPropMap,{close:()=>{this._props.close==="on"?n.removeClass(this._elements.close,"hide"):n.addClass(this._elements.close,"hide")},menu:()=>{this._props.menu==="on"?n.removeClass(this._elements.menu,"hide"):n.addClass(this._elements.menu,"hide")},type:t=>{this._props.type||(this._props.type="default"),t&&n.removeClass(this._elements.button,t),n.addClass(this._elements.button,this._props.type)},readmode:()=>{this._props.readmode?n.addClass(this._elements.box,"readmode"):n.removeClass(this._elements.box,"readmode")}})},m(B1,"prop",{leftIcon:"",rightIcon:"",style:"",disabled:!1,text:"",type:"default",close:"on",menu:"off",skin:""}),m(B1,"events",{close:new Event("close",{composed:!0}),menu:new Event("menu",{composed:!0})});let Vi=B1;const F9=`<div class="content">\r
	<label>\r
		<slot name="before-outer"></slot>\r
		<div class="label hide">\r
			<div class="labelText hide"></div>\r
			<slot name="label"></slot>\r
		</div>\r
		<div class="body" style="width: 100%; height: 100%;">\r
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
		</div>\r
		<slot name="after-outer"></slot>\r
	</label>\r
</div>\r
`,$9=`* {\r
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
.body{\r
    height: 100%;\r
}\r
.body>div:first-child{\r
     height: 100%;\r
}\r
/* .labelText {\r
    align-items: center;\r
} */\r
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
.body.invalid>div:first-child{\r
    height: calc(100% - 1.75em) !important;\r
}\r
.readmode .viewText {\r
    display: block;\r
    white-space: pre-wrap;\r
}\r
\r
/* 移动端，标签在上，输入框下划线 skin-mode='mobile' */\r
.content.mobile>label {\r
    display: flex !important;\r
    align-items: unset !important;\r
    position: relative !important;\r
    height: unset !important;\r
    flex-direction: column;\r
}\r
\r
.content.mobile .labelText {\r
    width: unset !important;\r
    min-width: unset !important;\r
    max-width: unset !important;\r
    display: block !important;\r
}\r
.content.mobile .input {\r
    padding: 0.5em 0.35em;\r
    font-size: 1.125em;\r
\r
}\r
.content.mobile .viewText {\r
    padding: 0.5em 0.35em;\r
    font-size: 1.125em;\r
    background-color: transparent !important;\r
    white-space: pre-wrap;\r
}\r
/* ---------------------------------------------- */`,n3=class n3 extends at{constructor(){super();d(this,y5);c(this,y5,i0).call(this),this._init("oo-textarea"),this._useCss($9)}_init(t){this._initialize(t,n3,F9,L4)}_setEvent(){super._setEvent(),this._elements.input.addEventListener("input",()=>{this.setHeight()}),n.toggleClass(this._elements.input,"autosize",this._props.autoSize),this.setHeight()}setHeight(){if(this._props.autoSize){this._elements.input.style.height="auto";const t=this._elements.input.scrollHeight;this._elements.input.style.height=t+"px"}}};y5=new WeakSet,i0=function(){this._setPropMap=Object.assign(this._setPropMap,{autoSize:()=>{n.toggleClass(this._elements.input,"autosize",this._props.autoSize),this.setHeight()},value:()=>{try{this._elements.input.value=this._props.value}catch{}this._props.value?this._elements.viewText.textContent=this._props.value:this._elements.viewText.innerHTML="-",window.setTimeout(()=>{this.setHeight()},10)}})};let Hi=n3;const j9=`.content{\r
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
\r
.item .icon.img{\r
    width: 1.5em;\r
    height: 1.5em;\r
    background-size: cover;\r
    background-position: center;\r
    background-repeat: no-repeat;\r
    margin-right: 0.5em;\r
}\r
\r
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
.dragArea{\r
    display: none;\r
    padding: 0.5em 0 0.5em 0.5em;\r
}\r
.items-draggable .item:hover .dragArea{\r
    display: block;\r
}\r
.draggable{\r
    font-size: 1em;\r
    text-align: left;\r
    cursor: move;\r
}\r
.dragArea:hover .draggable{\r
    color: var(--item-select);\r
}\r
.dragging{\r
    opacity: 0.5;\r
}\r
.dragover-top{\r
    border-top: 2px dashed var(--item-select);\r
    border-bottom: 1px dashed var(--item-select);\r
}\r
.dragover-bottom{\r
    border-top: 1px dashed var(--item-select);\r
    border-bottom: 2px dashed var(--item-select);\r
}\r
.dragover-middle{\r
    border-top: 2px dashed var(--item-select);\r
    border-bottom: 2px dashed var(--item-select);\r
    border-left: 2px dashed var(--item-select);\r
    border-right: 2px dashed var(--item-select);\r
}\r
\r
.hide{\r
    display: none;\r
}\r
\r
.input{\r
    cursor: pointer;\r
}\r
`,z9=`
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
</nav>`,E5=class E5 extends N{constructor(){super();d(this,w5);d(this,Y1);d(this,k5);d(this,W1);d(this,$2,[]);d(this,Ue,[]);d(this,Ce,new Map);d(this,Xe,new WeakMap);d(this,te,new WeakMap);d(this,ft,null);d(this,ee,null);m(this,"_setPropMap",{$default:()=>{},skin:()=>{this._useSkin(this._props.skin)},cssLink:()=>{this.useCssLink(this._props.cssLink)},searchable:()=>{n.checkClass(this._elements.search,"hide",!this._props.searchable)},dragEnable:()=>{n.checkClass(this._elements.items,"items-draggable",!!this._props.dragEnable)},menu:()=>{this.loaded&&c(this,w5,r0).call(this);const t={children:this._props.menu};this._props.key&&(t[this._props.key]="oo-nav-root"),this.data=c(this,W1,z3).call(this,t);const e=new Yi(this,null,this._elements.items,this.data,0).append();l(this,$2).push(e),this.loaded=!0}});m(this,"_elements",{box:null,navContent:null,items:null,search:null,searchInput:null,clearSearch:null,searchItems:null});this._initialize("oo-nav",E5,z9,j9)}static get observedAttributes(){return Object.keys(this.prop).map(t=>b(t))}_render(){this._setPropMap.dragEnable(),this._setEvent()}_connected(){}_disconnected(){}_setEvent(){super._setEvent();const{searchInput:t,clearSearch:e}=this._elements;let s=!1;t.addEventListener("compositionstart",()=>{s=!0}),t.addEventListener("compositionend",r=>{s=!1,this.search(r.currentTarget.value)}),t.addEventListener("input",r=>{!s&&this.search(r.currentTarget.value)}),e.addEventListener("click",r=>{t.setAttribute("value",""),this.search("")})}setMenu(t){this._props.menu=t,this._setPropMap.menu()}active(t){var e;(e=this.getItem(t))==null||e.active()}select(t){var e;(e=this.getItem(t))==null||e.select()}collapse(t){var e;(e=this.getItem(t))==null||e.collapse()}expand(t){var e;(e=this.getItem(t))==null||e.expand()}getItem(t){return l(this,Ce).get(t)}getSelectedItem(){return l(this,ft)}_checkItemUnique(t){const e=t[this._props.key];return!(e&&l(this,Ce).get(e))}_checkChildrenMap(t){const e=t.data.___;e.children&&!l(this,te).has(e.children)&&l(this,te).set(e.children,t)}_addItem(t){const e=t.data.___;l(this,Xe).set(e,t),e.children&&l(this,te).set(e.children,t);const s=e[this._props.key];s&&l(this,Ce).set(s,t)}_removeItem(t){const e=t.data.___;l(this,ft)===t&&u(this,ft,null),l(this,Xe).delete(e),e.children&&l(this,te).delete(e.children,t);const s=e[this._props.key];s&&l(this,Ce).delete(s)}_selectItem(t){l(this,ft)!==t&&(l(this,ft)&&(l(this,ft).data.selected=!1),u(this,ft,t))}_selectSearchItem(t){var e;l(this,ee)!==t&&((e=l(this,ee))==null||e.unselect(),u(this,ee,t))}_fireEvent(t,e,s){this.dispatchEvent(new CustomEvent(t,{detail:{data:e,manual:!!s}}))}search(t){var e;n.checkClass(this._elements.clearSearch,"hide",!t),n.checkClass(this._elements.items,"hide",!!t),n.checkClass(this._elements.searchItems,"hide",!t),c(this,k5,o0).call(this),t?c(this,Y1,j3).call(this,l(this,$2),t):((e=l(this,ft))==null||e.checkExpand(),u(this,ee,null)),this.dispatchEvent(new CustomEvent("search",{detail:{value:t}}))}};$2=new WeakMap,Ue=new WeakMap,Ce=new WeakMap,Xe=new WeakMap,te=new WeakMap,ft=new WeakMap,ee=new WeakMap,w5=new WeakSet,r0=function(){u(this,$2,[]),u(this,Ue,[]),u(this,Ce,new Map),u(this,Xe,new WeakMap),u(this,te,new WeakMap),u(this,ft,null),u(this,ee,null),n.empty(this._elements.items)},Y1=new WeakSet,j3=function(t,e){t.forEach(s=>{if(s._searchable()&&s.data.text.includes(e)){const r=new Z9(this,s,s.data.___);l(this,Ue).push(r)}c(this,Y1,j3).call(this,s.items,e)})},k5=new WeakSet,o0=function(){for(;l(this,Ue).length;)l(this,Ue).pop().remove();u(this,ee,null)},W1=new WeakSet,z3=function(t){return new Proxy(t,{get:(e,s)=>{if(s==="___")return e;{const r=e[s];return(typeof r=="object"||Array.isArray(r))&&r!==null?c(this,W1,z3).call(this,r):r}},set:(e,s,r)=>{let a,h;if(Array.isArray(e))r.___&&(r=r.___),a=Reflect.set(e,s,r),h=l(this,te).get(e),h==null||h._updateChildren(s,r);else{const p=e[s];a=Reflect.set(e,s,r),h=l(this,Xe).get(e),h==null||h._updateProperty(s,r,p)}return a}})},m(E5,"prop",{skin:"",cssLink:"",key:"name",searchable:!0,allExpanded:!1,dragEnable:!1,indent:1.3,iconPrefix:"ooicon-",menu:[]});let Bi=E5;const i3=class i3{constructor(i,t,e,s,r){d(this,M5);d(this,S5);d(this,A5);d(this,T5);d(this,D5);m(this,"items",[]);m(this,"childrenMap",new WeakMap);d(this,Et,"top");this.nav=i,this.data=s,this.parentItem=t,this.parentEl=e,this.level=r,this._render()}_render(){this.nodeEl=n("div.content"),this.level>0?(c(this,M5,l0).call(this),c(this,S5,a0).call(this),c(this,A5,d0).call(this)):(this.nav._addItem(this),this.childrenEl=n("div.children.show"),this.nodeEl.append(this.childrenEl)),this._createItems()}append(i="beforeend",t=this.parentEl){return t.insertAdjacentElement(i,this.nodeEl),this}_updateChildren(){for(let e=0;e<this.items.length;e++){const s=this.items[e];this.data.___.children.includes(s.data.___)||(s.remove(),e--)}const i=[];let t;this.data.children.forEach((e,s)=>{let r=this.childrenMap.get(e.___);r?this.items.indexOf(r)!==s&&(this.items.splice(this.items.indexOf(r),1),this.items.splice(s,0,r),t?r.append("afterend",t.nodeEl):r.append("afterbegin",this.childrenEl)):t?r=this._createItem(e,"afterend",t.nodeEl):r=this._createItem(e,"afterbegin",this.childrenEl),r?t=r:i.push(e)}),i.forEach(e=>{jt(this.data.___.children,e.___)}),this.level>0&&(n.checkClass(this.arrowEl,"hide",!this._expandable()),n.checkClass(this.expandEl,"hide",!this._expandable())),i.length>0&&this.throwNotUniqueError(i)}_createItems(){var t;const i=[];(t=this.data.children)==null||t.forEach(e=>{!this._createItem(e)&&i.push(e)}),i.forEach(e=>{jt(this.data.___.children,e.___)}),i.length>0&&this.throwNotUniqueError(i)}throwNotUniqueError(i){const t=i.map(e=>e.___[this.nav._props.key]);throw new Error(`Item key ${this.nav._props.key}:${t.join(", ")} is not unique!`)}_createItem(i,t,e){if(this.nav._checkItemUnique(i.___)){const s=new i3(this.nav,this,this.childrenEl,i,this.level+1).append(t,e);return this.items.push(s),this.childrenMap.set(i.___,s),this.nav._addItem(s),s}else return null}_removeItem(i){this.childrenEl.removeChild(i.nodeEl),jt(this.items,i),this.childrenMap.delete(i.data.___),this.nav._removeItem(i)}remove(){var i;this.emptyItems(),(i=this.parentItem)==null||i._removeItem(this)}emptyItems(){for(;this.items.length>0;)this.items.pop().remove()}_updateProperty(i,t,e){if(t===e)return;const s=this.data.___;switch(i){case"icon":e&&n.removeClass(this.iconEl,`${this.nav._props.iconPrefix}${e}`),t&&n.addClass(this.iconEl,`${this.nav._props.iconPrefix}${t}`),n.checkClass(this.iconEl,"hide",!t);break;case"text":this.textEl.textContent=t||"";break;case"title":this.itemEl.setAttribute("title",t||"");break;case"count":this.countEl.textContent=s.count||"",n.checkClass(this.countEl,"hide",typeof s.count>"u");break;case"expanded":t?this.expand():this.collapse();break;case"selected":t?this.select():this.unselect();break;case"expandable":n.checkClass(this.arrowEl,"hide",!this._expandable()),n.checkClass(this.expandEl,"hide",!this._expandable());break;case"disabled":n.checkClass(this.itemEl,"disabled",!!t);break;case"draggable":n.checkClass(this.dragEl,"hide",!this._draggable());break;case"group":n.checkClass(this.arrowEl,"hide",!this._expandable()),n.checkClass(this.expandEl,"hide",!this._expandable()),n.checkClass(this.itemEl,"group",!!t),n.checkClass(this.childrenEl,"group",!!t),t&&!s.expanded&&this.expand(!1);break;case this.nav._props.key:this.nav._removeItem(this),this.nav._addItem(this);break;case"children":this.nav._checkChildrenMap(this),this._updateChildren(i,t);break}}_selectable(i=!0){var e;const t=this.data.___;switch(t.selectable){case"no":return!1;case"yes":return i?!t.disabled&&!t.group:!t.group;default:const s=!t.group&&!((e=t.children)!=null&&e.length);return i?!t.disabled&&s:s}}_searchable(){var t;const i=this.data.___;return!i.disabled&&!i.group&&(!((t=i.children)!=null&&t.length)||i.selectable==="yes")}_expandable(i=!1){var s;const t=this.data.___,e=!t.group&&!!((s=t.children)!=null&&s.length)||!!t.expandable;return i?!t.disabled&&e:e}_draggable(){const i=this.data.___;return!!i.draggable&&!i.disabled&&!!this.nav._props.dragEnable}_droppable(){const i=this.data.___,t=this.nav.draggingItem;return t!=null&&t._isDescendants(this)?!1:!!i.droppable&&!i.disabled&&!!this.nav._props.dragEnable}_isDescendants(i){for(;i;){if(i===this)return!0;i=i.parentItem}return!1}_handleDragover(i){const t=n.getSize(this.itemEl);switch(this.data.droppable){case"in":u(this,Et,"middle");break;case!0:u(this,Et,i.offsetY<t.y/3?"top":i.offsetY<=t.y/3*2?"middle":"bottom");break;default:u(this,Et,i.offsetY<t.y/2?"top":"bottom");break}n.checkClass(this.itemEl,"dragover-top",l(this,Et)==="top"),n.checkClass(this.itemEl,"dragover-middle",l(this,Et)==="middle"),n.checkClass(this.itemEl,"dragover-bottom",l(this,Et)==="bottom")}_handleDragleave(i){n.removeClass(this.itemEl,"dragover-top"),n.removeClass(this.itemEl,"dragover-middle"),n.removeClass(this.itemEl,"dragover-bottom")}_handleDrop(i){this._handleDragleave(i);const t=this.nav.draggingItem,e=t.data.___;if(t&&t!==this){let s;const r=t.parentItem.data;r&&(s=r.children.___.indexOf(e),r.children.splice(s,1));let a,h;l(this,Et)==="middle"?(h=this.data,h.children?(h.children.push(e),a=h.children.___.length-1):(h.children=[e],a=0),!this.data.expanded&&(this.data.expanded=!0)):(h=this.parentItem.data,h&&(h.children?(a=h.children.___.indexOf(this.data.___),l(this,Et)==="bottom"&&a++,h.children.splice(a,0,e)):(h.children=[e],a=0))),this.nav.draggingItem=null,this.nav._fireEvent("drop",{from:r,to:h,data:e,fromIndex:s,toIndex:a},!0)}}select(i=!0,t=!0){var e;this._selectable(i)&&(n.addClass(this.itemEl,"selected"),this.nav._selectItem(this),t&&((e=this.parentItem)==null||e.checkExpand(i)),this.nav._fireEvent("select",this.data,i))}active(i=!0){!this.data.___.disabled&&this.nav._fireEvent("active",this.data,i)}unselect(){n.removeClass(this.itemEl,"selected")}checkExpand(i=!0,t=!1){var e;(!this.data.___.expanded||t)&&this._expandable(i)&&this.expand(i),(e=this.parentItem)==null||e.checkExpand(i,t)}expand(i=!0){this._expandable(i)&&(this.arrowEl&&n.addClass(this.arrowEl,"down"),this.data.___.expanded=!0,i?c(this,T5,h0).call(this):(n.addClass(this.childrenEl,"show"),this.nav._fireEvent("expand",this.data,!1)))}collapse(i=!0){this._expandable(i)&&(this.arrowEl&&n.removeClass(this.arrowEl,"down"),this.data.___.expanded=!1,i?c(this,D5,c0).call(this):(n.removeClass(this.childrenEl,"show"),this.nav._fireEvent("collapse",this.data,!1)))}};M5=new WeakSet,l0=function(){const i=this.data.___,t=i[this.nav._props.key];this.itemEl=n("div.item"),t&&this.itemEl.append(n("slot",{name:`${t}-inner-before`})),this.iconEl=n(`div.icon ${this.nav._props.iconPrefix}${i.icon||""}`),this.itemEl.append(this.iconEl),i.img&&(this.iconEl.addClass("img"),this.iconEl.style.backgroundImage=`url(${i.img})`),this.textEl=n("div.text",{text:i.text||""}),this.itemEl.append(this.textEl),i.title&&this.itemEl.setAttribute("title",i.title),t&&this.itemEl.append(n("slot",{name:`${t}-inner`})),this.countEl=n("div.count"),typeof i.count<"u"&&(this.countEl.textContent=i.count),this.itemEl.append(this.countEl),this.expandEl=n("div.arrowArea"),this.arrowEl=n("div.arrow ooicon-drop_down"),this.expandEl.append(this.arrowEl),this.itemEl.append(this.expandEl),this.dragEl=n("div.dragArea"),this.dragIconEl=n("div.draggable ooicon-sorting"),this.dragEl.append(this.dragIconEl),this.itemEl.append(this.dragEl),t&&this.itemEl.append(n("slot",{name:`${t}-inner-after`})),this.nodeEl.append(this.itemEl),this.childrenEl=n("div.children"),this.nodeEl.append(this.childrenEl)},S5=new WeakSet,a0=function(){const{itemEl:i,expandEl:t,dragEl:e,data:s}=this;i.addEventListener("click",r=>{if(this._selectable())this.select(),s.___.selected=!0,this.active();else if(this._expandable()){//!data.expanded ? this.expand() : this.collapse();
s.expanded=!s.___.expanded,this.active()}else this.active()}),t.addEventListener("click",r=>{if(this._expandable()&&this._selectable()){//!data.expanded ? this.expand() : this.collapse();
s.expanded=!s.___.expanded,this.active(),r.stopPropagation()}}),e.addEventListener("mousedown",r=>{this._draggable()&&i.setAttribute("draggable","true"),r.stopPropagation()}),e.addEventListener("click",r=>{r.stopPropagation()}),i.addEventListener("dragstart",r=>{this._draggable()&&(this.nav.draggingItem=this,i.addClass("dragging"),this.nav._fireEvent("dragstart",this.data,!0)),r.stopPropagation()}),i.addEventListener("dragend",r=>{this._draggable()&&(i.setAttribute("draggable","false"),i.removeClass("dragging"),this.nav.draggingItem=null,this.nav._fireEvent("dragend",this.data,!0)),r.stopPropagation()}),i.addEventListener("dragover",r=>{this._droppable()&&(r.preventDefault(),this._handleDragover(r)),r.stopPropagation()}),i.addEventListener("dragleave",r=>{this._droppable()&&this._handleDragleave(r)}),i.addEventListener("drop",r=>{this._droppable()&&(r.preventDefault(),this._handleDrop(r)),r.stopPropagation()})},A5=new WeakSet,d0=function(){const{itemEl:i,iconEl:t,childrenEl:e,arrowEl:s,expandEl:r,countEl:a,dragEl:h,nav:p,level:C}=this,g=this.data.___;n.checkClass(t,"hide",!g.icon&&!g.img),n.checkClass(a,"hide",typeof g.count>"u"),n.checkClass(i,"disabled",!!g.disabled),C===1&&n.addClass(i,"level1"),n.setStyle(i,"padding-left",p._props.indent*C+"em"),n.checkClass(s,"hide",!this._expandable()),n.checkClass(r,"hide",!this._expandable()),n.checkClass(h,"hide",!this._draggable()),n.checkClass(e,"show",!!g.group),s&&n.checkClass(s,"down",!!g.expanded),p._props.allExpanded&&!g.expanded&&!g.group&&this.expand(!1),g.group&&n.addClass(i,"group"),g.group&&n.addClass(e,"group"),g.selected&&this.select(!1),g.expanded&&this.checkExpand(!1,!0)},Et=new WeakMap,T5=new WeakSet,h0=function(){const i=this.childrenEl;n.addClass(i,"show");const t=n.getSize(i).y;n.setStyle(i,"height","0"),setTimeout(()=>{n.setStyle(i,"height",t+"px"),setTimeout(()=>{n.setStyle(i,"height","unset"),this.nav._fireEvent("expand",this.data,!0)},200)},1)},D5=new WeakSet,c0=function(){const i=this.childrenEl,t=n.getSize(i).y;n.setStyle(i,"height",t+"px"),setTimeout(()=>{n.setStyle(i,"height","0"),setTimeout(()=>{n.setStyle(i,"height","unset"),n.removeClass(i,"show"),this.nav._fireEvent("collapse",this.data,!0)},200)},1)};let Yi=i3;class Z9{constructor(i,t,e){d(this,P5);d(this,I5);d(this,N5);this.nav=i,this.item=t,this.data=e,this.parentEl=i._elements.searchItems,this._render()}_render(){c(this,P5,p0).call(this),c(this,I5,u0).call(this),c(this,N5,m0).call(this),this.parentEl.append(this.nodeEl)}select(i=!0,t=!0){this.item._selectable()?(n.addClass(this.itemEl,"selected"),this.nav._selectSearchItem(this),t&&(this.item.data.___.selected=!0,this.item.select(!0,!1),this.item.active(i))):this.item.active(i)}unselect(){n.removeClass(this.itemEl,"selected")}remove(){this.parentEl.removeChild(this.nodeEl)}}P5=new WeakSet,p0=function(){const i=this.data;this.nodeEl=n("div.content"),this.itemEl=n("div.item level1"),i.icon&&this.itemEl.append(n(`div.icon ${this.nav._props.iconPrefix}${i.icon}`)),this.itemEl.append(n("div.text",{text:i.text||""})),i.title&&this.itemEl.setAttribute("title",i.title),this.nodeEl.append(this.itemEl)},I5=new WeakSet,u0=function(){this.itemEl.addEventListener("click",i=>{this.select()})},N5=new WeakSet,m0=function(){const{itemEl:i,data:t}=this;n.checkClass(i,"disabled",!!t.disabled),n.setStyle(i,"padding-left",this.nav._props.indent+"em"),t.selected&&this.select(!1,!1)};function A4(o,i){const t=()=>{o.setSelectionRange(i,i)};t(),setTimeout(t,1)}const pt=class pt extends at{constructor(){super();d(this,O5);d(this,F5);d(this,$5);d(this,j5);d(this,X1);d(this,q1,null);d(this,Z,{});d(this,U1,null);c(this,j5,v0).call(this),this._init("oo-currency",pt)}static get observedAttributes(){return pt.prop=Object.assign({},at.prop,pt.currencyProp),Object.keys(pt.prop).map(t=>b(t))}attributeChangedCallback(t,e,s){if(!/{{.*}}/g.test(s)){const r=r2(t),a=this._props[r];c(this,F5,g0).call(this,this._props,r,s),c(this,O5,C0).call(this,r,a,e)}}_render(){c(this,$5,f0).call(this)}_dispatchChangeEvent(){this.dispatchEvent(new CustomEvent("change",{bubbles:!0,composed:!0,detail:{value:this._props.value,text:this._props.text}}))}_setText(t){const e=_s(t||this._props.value,l(this,Z),"",!1);this._elements.input.value=e,this._elements.viewText.textContent=e,this._props.text=e}_inputValue(t){if(l(this,U1)===t)return;const e={...l(this,Z),precision:"auto"},{suffix:s,decimal:r}=e,a=!!r&&t.endsWith(r+(s||"")),h=l2(t,e,"",!0)+(a?r:""),p=_s(h,e,"",!0),C=this._checkNegativeZero(t,p,e);this._elements.input.value=C,this._elements.viewText.textContent=C,this._props.text=_s(h,l(this,Z),""),this._props.value=l2(this._props.text,l(this,Z),""),u(this,U1,p)}_checkNegativeZero(t,e,s){function r(g,f){return f===""?g:g.startsWith(f)?g.slice(f.length):g}const{prefix:a,disablenegative:h}=s,p=r(t,a),C=r(e,a);return/^0(\.0+)?$/g.test(C)||C==="0."?!h&&p.startsWith("-")?`${a}-${C}`:`${a}${C}`:`${a}${C}`}_input(t,e,s){if(!this._props.read&&!this._props.readmode&&!this._props.disabled){const{suffix:r,decimal:a,prefix:h}=l(this,Z),p=t.currentTarget,C=p.value,g=e||p.selectionEnd;let f;C.length===1?f=r.length:f=C.length-(g||0),this._inputValue(p.value),s?A4(p,p.value.indexOf(a)+1):(f=Math.max(f,r.length),f=p.value.length-f,f=Math.max(f,h.length),A4(p,f)),this.unInvalidStyle()}}_setEvent(){super._setEvent();let t,e=!1;this._elements.input.addEventListener("blur",r=>{this._setText(this._elements.input.value)});let s=!1;this._elements.input.addEventListener("compositionstart",()=>{s=!0}),this._elements.input.addEventListener("compositionend",r=>{s=!1,this._input(r,t,e)}),this._elements.input.addEventListener("input",r=>{!s&&this._input(r,t,e)}),this._elements.input.addEventListener("keydown",r=>{const{thousands:a,decimal:h}=l(this,Z),p=r.currentTarget;let C=p.value,g=p.selectionEnd;t=null,e=r.key===h;const f=r.code==="Backspace",x=C.length-(g||0)===0;if(f){const Mt=C[g-1];Mt&&Mt===a&&(t=g-2)}let y=l2(C,l(this,Z),"");this._props.allowblank&&f&&x&&y===0&&(r.value=""),this._props.allowblank&&f&&x&&C==="0"&&(p.value=""),r.key==="+"&&typeof y=="string"&&(y=parseFloat(y))(y<0)&&(p.value=String(y*-1))})}_connected(){this.hasAttribute("right-icon")||this.setAttribute("right-icon","currency")}get value(){return this._props.value}set value(t){this.setAttribute("value",t)}get text(){return this._props.text}set text(t){this._props.text=t}};q1=new WeakMap,O5=new WeakSet,C0=function(t,e,s){(t?[t]:Object.keys(this._props)).forEach(a=>{this._setProps(a,e,s)})},F5=new WeakSet,g0=function(t,e,s){switch(v(t[e])){case"boolean":t[e]=!!s&&s!=="false";break;case"number":t[e]=isNaN(s)?s:parseFloat(s);break;case"object":try{t[e]=JSON.parse(s)}catch{t[e]=s}break;case"array":try{t[e]=JSON.parse(s)}catch{t[e]=s?s.split(/\s*,\s*/g):[]}break;default:t[e]=s}},Z=new WeakMap,U1=new WeakMap,$5=new WeakSet,f0=function(){u(this,Z,{});for(var t in pt.currencyProp)this._props.hasOwnProperty(t)&&(l(this,Z)[t]=this._props[t])},j5=new WeakSet,v0=function(){this._setPropMap=Object.assign(this._setPropMap,{disabled:()=>{n.toggleAttr(this._elements.input,"disabled",this._props.disabled),n.checkClass(this._elements.box,"disabled",this._props.disabled),n.checkClass(this._elements.label,"disabled",this._props.disabled),n.checkClass(this._elements.suffix,"disabled",this._props.disabled)},value:()=>{this._setText(),this._props.value=l2(this._props.text,l(this,Z),"")},text:()=>{this._props.value=l2(this._props.text,l(this,Z),""),this._setText()},readmode:()=>{n.toggleAttr(this._elements.input,"readonly",this._props.readmode),n.checkClass(this._elements.box,"readmode",this._props.readmode),n.checkClass(this._elements.label,"readmode",this._props.readmode)},prefixuse:()=>{c(this,X1,Z3).call(this)},currency:()=>{c(this,X1,Z3).call(this)},$default:t=>{if(pt.currencyProp.hasOwnProperty(t))if(l(this,Z)[t]=this._props[t],this._props.value)switch(t){case"prefix":case"suffix":case"thousands":case"decimal":case"precision":this._setText();break;case"maximum":this._props.maximum<this._props.value&&this._setPropMap.value();break;case"minimum":this._props.minimum>this._props.value&&this._setPropMap.value();break}else t==="allowblank"&&this._setText();else n.toggleAttr(this._elements.input,t,this._props[t])}})},X1=new WeakSet,Z3=function(){let t=!1;const{currency:e,prefixuse:s}=this._props;if(e&&l(this,q1)!==e)if(ne[e])t=!0,u(this,q1,e),Object.assign(l(this,Z),ne[e]);else throw new Error("This currency has no preset: "+key);if(e&&s)if(["symbol","iso"].includes(s)){const r=ne[e][s];r!==l(this,Z).prefix&&(t=!0,l(this,Z).prefix=r)}else throw new Error('prefixUse must be "symbol" or "iso"');t&&this._setText()},m(pt,"currencyProp",{currency:"CNY",prefixuse:"symbol",...ni}),m(pt,"preset",ne),m(pt,"formatCurrency",_s),m(pt,"unformatCurrency",l2),m(pt,"_name","OOCurrency");let Wi=pt;const R9='<div><oo-select option-width="auto" class="selectNode" style="width:100%"></oo-select>',J1=class J1 extends at{constructor(){super();d(this,G1);d(this,K1);d(this,r3);m(this,"_elements",{selectNode:null});m(this,"_setPropMap",{$default:t=>{var e;t!=="style"&&((e=this._elements.selectNode)==null||e.setAttribute(b(t),this._props[t]))}});d(this,j2,new WeakMap);this._init("oo-cascade",J1,R9,"")}static get observedAttributes(){return J1.prop=Object.assign({},at.prop,Ds.prop),Object.keys(J1.prop).map(t=>b(t))}_render(){const t=this.getAttributeNames();if(this._elements.selectNode){t.forEach(s=>{s!=="style"&&this._elements.selectNode.setAttribute(s,this.getAttribute(s))});for(var e in this._elements.selectNode._elements)this._elements[e]=this._elements.selectNode._elements[e];this._elements.selectNode.cascading=!0}}setOption(t){c(this,G1,R3).call(this,t,this._elements.selectNode)}_setEvent(){var t;(t=this._elements.selectNode)==null||t.addEventListener("change",e=>{this.dispatchEvent(new CustomEvent("change"))})}_connected(){}get value(){var t;return(t=this._elements.selectNode)==null?void 0:t.value}set value(t){this._elements.selectNode&&(this._elements.selectNode.value=t)}get text(){var t;return(t=this._elements.selectNode)==null?void 0:t.text}set text(t){this._elements.selectNode&&(this._elements.selectNode.text=t)}get path(){var t;return(t=this._elements.selectNode)==null?void 0:t.path}set path(t){this._elements.selectNode&&(this._elements.selectNode.path=t)}};j2=new WeakMap,G1=new WeakSet,R3=function(t,e,s){if(t)if(v(t)==="function"){const r=n("oo-option");r.loading=!0,e.appendChild(r);const a=async h=>{const p=h.target.tagName.toLowerCase()==="oo-select"?[]:h.target.value,C=await t(p,h);s&&(s.children=C),n.empty(e),c(this,K1,V3).call(this,C,e);const g=l(this,j2).get(e);g&&(e.removeEventListener("show",g),l(this,j2).delete(e))};e.addEventListener("show",a),l(this,j2).set(e,a)}else c(this,K1,V3).call(this,t,e)},K1=new WeakSet,V3=function(t,e){t&&t.length&&(t.forEach(s=>{const{text:r,label:a,icon:h,value:p,type:C,children:g}=s,x=n(C==="group"?"oo-option-group":"oo-option");x.setAttribute("text",r||a),x.setAttribute("value",p||r),h&&x.setAttribute("icon",h),e.appendChild(x),g&&c(this,G1,R3).call(this,g,x,s)}),this._elements.selectNode.dropped&&e.offsetParent&&e.tagName.toLowerCase()==="oo-option"&&e._positionSelectOptions())},r3=new WeakSet,Y8=async function(t){return v(t)==="function"?await t():t};let qi=J1;const V9=Object.assign({"./styles/mask/default.css":()=>Promise.resolve().then(()=>Z8)}),ge=class ge{constructor(i,t){d(this,z5);d(this,Z5);d(this,R5);this.node=i,this.options=Object.assign({},B2.option,t)}show(){c(this,R5,L0).call(this),this.maskNode||c(this,z5,b0).call(this),this.positionMaskNode(),n.addClass(this.maskNode,"show")}hide(){n.removeClass(this.maskNode,"show"),setTimeout(()=>{this.maskNode.remove()},200)}positionMaskNode(){n.overlap(this.maskNode,this.node,n.getOffsetParent(this.node))}};z5=new WeakSet,b0=function(){this.maskNode=n("div.maskNode"),this.node.insertAdjacentElement("afterend",this.maskNode)},Z5=new WeakSet,_0=async function(i){const t=await V9[`./styles/mask/${i}.css`]();return r4(t.default,this.container)},R5=new WeakSet,L0=async function(){const i=ge.loadedStyles[this.options.style||"default"];if(i){const t=await Promise.resolve(i);n.addClass(this.maskNode,t.id)}else{const t=this.options.style||"default";ge.loadedStyles[t]=c(this,Z5,_0).call(this,t),ge.loadedStyles[t]=await ge.loadedStyles[t]}},m(ge,"option",{position:"top",style:"default",autoClose:0,emerge:-8,offset:6,defer:600,events:{show:"mouseover",hide:"mouseout, mousedown"},showOnEnterCotent:!1,resetContentOnShow:!1}),m(ge,"loadedStyles",{});let Ui=ge;function H9(o,i){return o.mask||(o.mask=new Ui(o,i)),o.mask.show(),o.mask}function B9(o){const i=o.mask;i&&i.hide()}class T4{constructor(i,t={}){d(this,H5);d(this,B5);d(this,Y5);d(this,Ge,null);d(this,Ke,null);d(this,se,null);d(this,Je,null);d(this,fe,null);d(this,V5,null);d(this,vt,{});this.node=n.el(i),this.options=t,this.start={x:0,y:0},this.container=n.getPositionParent(this.node),u(this,Ge,l(this,Ge)||(e=>{this.dragReady(e)})),this.node.addEventListener("mousedown",l(this,Ge)),u(this,se,l(this,se)||(e=>{this.dragChecK(e)})),u(this,V5,l(this,se)||(e=>{this.dragstart(e)})),u(this,Ke,l(this,Ke)||(e=>{this.drag(e)})),u(this,Je,l(this,Je)||(e=>{this.dragend(e)})),u(this,fe,l(this,fe)||(e=>{this.dragcancel(e)}))}setOptions(i={}){Object.assign(this.options,i)}addEventListener(i,t){l(this,vt)[i]||(l(this,vt)[i]=[]),l(this,vt)[i].push(t)}removeEventListener(i,t){if(l(this,vt)[i])if(t){const e=l(this,vt)[i].indexOf(t);l(this,vt)[i].splice(e,1)}else l(this,vt)[i]=[]}dispatchEvent(i,t){l(this,vt)[i]&&l(this,vt)[i].forEach(e=>{e.apply(this,[t,this])})}dragReady(i){if(i.buttons===1){const{clientX:t,clientY:e}=i;this.start={x:t,y:e},this.container.addEventListener("mousemove",l(this,se)),this.container.addEventListener("mouseup",l(this,fe)),this.dispatchEvent("dragready",i)}}dragChecK(i){if(i.buttons===1){if(!i.clientX||!i.clientY)return;const{clientX:t,clientY:e}=i,s=this.options.snap||6;Math.abs(this.start.x-t)>s||Math.abs(this.start.y-e)>s?this.dragCheckDetermined(i):this.dispatchEvent("dragchecK",i)}else this.dragcancel()}dragCheckDetermined(i){this.container.removeEventListener("mousemove",l(this,se)),this.container.addEventListener("mousemove",l(this,Ke)),this.container.removeEventListener("mouseup",l(this,fe)),this.container.addEventListener("mouseup",l(this,Je)),this.dragstart(i)}dragstart(i){this.dragNode||(this.dragNode=c(this,H5,x0).call(this)),c(this,Y5,w0).call(this,i),this.dispatchEvent("dragstart",i)}drag(i){if(i.buttons===1){const{clientX:t,clientY:e}=i,s=t-this.start.x,r=e-this.start.y;n.setStyles(this.dragNode,{left:`${this.start.pos.x+s}px`,top:`${this.start.pos.y+r}px`}),this.dispatchEvent("dragmove",i)}else this.dragend()}dragcancel(i){this.removeAllEvents(),this.dispatchEvent("dragcancel",i)}dragend(i){this.dispatchEvent("dragend",i),this.removeAllEvents(),n.removeClass(this.node,"dragged"),this.dragNode!==this.node&&this.dragNode.remove()}removeAllEvents(){this.container.removeEventListener("mousemove",l(this,se)),this.container.removeEventListener("mousemove",l(this,Ke)),this.container.removeEventListener("mouseup",l(this,fe)),this.container.removeEventListener("mouseup",l(this,Je))}destroy(){this.removeAllEvents(),this.node.removeEventListener("mousedown",l(this,Ge))}}Ge=new WeakMap,Ke=new WeakMap,se=new WeakMap,Je=new WeakMap,fe=new WeakMap,V5=new WeakMap,vt=new WeakMap,H5=new WeakSet,x0=function(){switch(this.options.dragNode||(this.options.dragNode="clone"),v(this.options.dragNode)){case"string":return this.options.dragNode==="none"?this.node:c(this,B5,y0).call(this);case"function":return this.options.dragNode(this);case"element":return this.options.dragNode}},B5=new WeakSet,y0=function(){const i=this.options.dragNode==="deepClone",t=this.node.cloneNode(i||!0);return n.setStyles(t,{opacity:.5}),t},Y5=new WeakSet,w0=function(i){const t=n.getSize(this.node),e=n.getPosition(this.container,document.body),s=i.clientX-e.x,r=i.clientY-e.y;this.start.pos={x:s,y:r},n.setStyles(this.dragNode,{position:"absolute",width:`${t.x}px`,height:`${t.y}px`,left:`${s}px`,top:`${r}px`,background:"#dddddd","z-index":"10000"}),n.addClass(this.dragNode,"drag"),n.addClass(this.node,"dragged"),this.container.append(this.dragNode)};class Y9 extends T4{constructor(t,e,s={}){super(t,s);m(this,"dropNode",null);this.droppables=e||[],this.droppableNodes=null}setDroppables(t){t&&(this.droppables=t)}getDroppableNodes(){switch(v(this.droppables)){case"string":return[...document.querySelectorAll(this.droppables)];case"function":return this.droppables(this);case"array":return this.droppables}}dragCheckDetermined(t){super.dragCheckDetermined(t),this.droppableNodes=this.getDroppableNodes(),this.droppablesRect=this.droppableNodes.map(e=>{const s=e.getBoundingClientRect(),r=s.width*s.height;return{node:e,rect:s,area:r}})}drag(t){if(super.drag(t),t.buttons===1){const e=this.findDropNode(t);e?this.dropNode!==e?(this.dropNode&&this.dropleave(t),this.dropNode=e,this.dropenter(t)):this.dropover(t):this.dropNode&&(this.dropleave(t),this.dropNode=e)}}findDropNode(t){const e=this.droppablesRect.filter(s=>t.pageY>s.rect.top&&t.pageY<s.rect.top+s.rect.height&&t.pageX>s.rect.left&&t.pageX<s.rect.left+s.rect.width);if(e.length){let s=e[0];return e.forEach(r=>{r.area<s.area&&(s=r)}),s.node.node||s.node}return null}createMarkNode(){switch(this.options.dropMark||(this.options.dropMark={border:"2px solid var(--oo-color-main)","z-index":"19999"}),v(this.options.dropMark)){case"object":if(this.markNode)return this.markNode;const t=n("div",{styles:this.options.dropMark}),e=n("div",{styles:{"background-color":"var(--oo-color-main-light)",width:"100%",height:"100%",opacity:"0.1"}});return t.append(e),t;case"element":return this.options.dropMark;case"function":return this.options.dropMark(this)}}positionMarkNode(){if(this.options.dropMark!=="none"&&(this.markNode=this.createMarkNode(),this.markNode)){const t=this.options.markContainer||this.container,e=n.getSize(this.dropNode),s=n.getPosition(this.dropNode,t);n.setStyles(this.markNode,{position:"absolute","z-index":"9999",transition:"width 0.2s, height 0.2s, left 0.2s, top 0.2s"}),t.append(this.markNode),window.setTimeout(()=>{this.markNode&&n.setStyles(this.markNode,{width:`${e.x}px`,height:`${e.y}px`,left:`${s.x}px`,top:`${s.y}px`})},10)}}removeMarkNode(){this.markNode&&this.markNode.remove()}dropenter(t){this.positionMarkNode(),this.dispatchEvent("dragenter",t),t.stopPropagation()}dropleave(t){this.dispatchEvent("dragleave",t),this.removeMarkNode(),this.dropNode=null,t.stopPropagation()}dropover(t){this.dispatchEvent("dragover",t),t.stopPropagation()}dragend(t){this.dropNode?(this.dispatchEvent("drop",t),this.dispatchEvent("dragend",t)):this.dispatchEvent("dragcancel",t),this.removeAllEvents(),n.removeClass(this.node,"dragged"),this.dragNode!==this.node&&this.dragNode.remove()}removeAllEvents(){super.removeAllEvents(),this.removeMarkNode()}}function W9(){if(customElements.get("oo-button")||customElements.define("oo-button",l4),customElements.get("oo-calendar")||customElements.define("oo-calendar",ie),customElements.get("oo-calendar-view")||customElements.define("oo-calendar-view",ci),customElements.get("oo-calendar-range")||customElements.define("oo-calendar-range",ui),customElements.get("oo-capsulae")||customElements.define("oo-capsulae",mi),customElements.get("oo-card")||customElements.define("oo-card",gi),customElements.get("oo-checkbox-group")||customElements.define("oo-checkbox-group",fi),customElements.get("oo-checkbox")||customElements.define("oo-checkbox",zo),customElements.get("oo-datetime")||customElements.define("oo-datetime",Mi),customElements.get("oo-file")||customElements.define("oo-file",Ti),customElements.get("oo-icon")||customElements.define("oo-icon",Di),customElements.get("oo-input")||customElements.define("oo-input",l9),customElements.get("oo-files")||customElements.define("oo-files",Pi),customElements.get("oo-select")||customElements.define("oo-select",Ds),customElements.get("oo-option-group")||customElements.define("oo-option-group",Ii),customElements.get("oo-option")||customElements.define("oo-option",dt),customElements.get("oo-cascade")||customElements.define("oo-cascade",qi),customElements.get("oo-org")||customElements.define("oo-org",Oi),customElements.get("oo-pagination")||customElements.define("oo-pagination",Fi),customElements.get("oo-radio-group")||customElements.define("oo-radio-group",_9),customElements.get("oo-radio")||customElements.define("oo-radio",b9),customElements.get("oo-selector")||customElements.define("oo-selector",k9),customElements.get("oo-skeleton")||customElements.define("oo-skeleton",ji),customElements.get("oo-switch")||customElements.define("oo-switch",zi),customElements.get("oo-tab")||customElements.define("oo-tab",Zi),customElements.get("oo-tabs")||customElements.define("oo-tabs",Ri),customElements.get("oo-tag")||customElements.define("oo-tag",Vi),customElements.get("oo-textarea")||customElements.define("oo-textarea",Hi),customElements.get("oo-nav")||customElements.define("oo-nav",Bi),customElements.get("oo-currency")||customElements.define("oo-currency",Wi),HTMLFormElement){const o="oo-input, oo-files, oo-radio-group, oo-checkbox-group, oo-selector, oo-select";if(HTMLFormElement.prototype.reportValidity&&!HTMLFormElement.prototype.reportValidity.OOInputAdded){const i=HTMLFormElement.prototype.reportValidity;HTMLFormElement.prototype.reportValidity=function(){const t=i.apply(this);if(!t)return t;const e=this.querySelectorAll(o);for(const s of e)if(s.reportValidity&&!s.checkValidity())return!1;return!0},HTMLFormElement.prototype.reportValidity.OOInputAdded=!0}if(HTMLFormElement.prototype.checkValidity&&!HTMLFormElement.prototype.checkValidity.OOInputAdded){const i=HTMLFormElement.prototype.checkValidity;HTMLFormElement.prototype.checkValidity=function(){let t=i.apply(this);const e=this.querySelectorAll(o);for(const s of e)s.checkValidity&&(t=t&&s.checkValidity());return t},HTMLFormElement.prototype.checkValidity.OOInputAdded=!0}if(HTMLFormElement.prototype.submit&&!HTMLFormElement.prototype.submit.OOInputAdded){const i=HTMLFormElement.prototype.submit;HTMLFormElement.prototype.submit=function(){if(this.reportValidity())return i.apply(this)},HTMLFormElement.prototype.submit.OOInputAdded=!0}}}const D4=Object.freeze(Object.defineProperty({__proto__:null,default:`* {\r
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
    /* transition: all 0.5s, width 0s; */\r
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
`},Symbol.toStringTag,{value:"Module"})),q9=Object.freeze(Object.defineProperty({__proto__:null,default:`* {\r
    box-sizing: border-box;\r
}\r
.container{\r
    /*position: fixed;*/\r
    width: 100%;\r
    top: 0;\r
    left: 0;\r
    padding: 0.714em;\r
    min-height: 5em;\r
    /* transition: all 0.5s; */\r
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
`},Symbol.toStringTag,{value:"Module"})),U9=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),X9=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),G9=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),K9=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),J9=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),Q9=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),t8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),e8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),s8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),n8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),i8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),r8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),o8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),l8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),a8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),d8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),h8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),c8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),p8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),u8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),m8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),C8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),g8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),f8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),v8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),b8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),_8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),L8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),x8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),y8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),w8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),k8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),E8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),M8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),S8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),A8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),T8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),D8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),P8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),I8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
`},Symbol.toStringTag,{value:"Module"})),N8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),O8=Object.freeze(Object.defineProperty({__proto__:null,default:`<svg width="36px" height="36px" viewBox="0 0 36 36" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\r
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
</svg>`},Symbol.toStringTag,{value:"Module"})),F8=Object.freeze(Object.defineProperty({__proto__:null,default:`<?xml version="1.0" encoding="UTF-8"?>\r
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
`},Symbol.toStringTag,{value:"Module"})),$8=Object.freeze(Object.defineProperty({__proto__:null,default:`.ootooltip{font-size:.875rem;padding:.3rem;border-radius:.3rem;position:absolute;background:var(--oo-color-main-deep);color:var(--oo-color-text-white);opacity:0;z-index:20000}.ootooltip.show{opacity:0;transition:opacity .6s,top .6s,left .6s}.ootooltip_triangle{width:.5rem;height:.5rem;position:absolute;border-bottom-right-radius:.15rem;background:var(--oo-color-main-deep)}.ootooltip_triangle.top{transform:rotate(45deg)}.ootooltip_triangle.left{transform:rotate(-45deg)}.ootooltip_triangle.right{transform:rotate(135deg)}.ootooltip_triangle.bottom{transform:rotate(-135deg)}.ootooltip_container{display:contents}.hide{display:none}
`},Symbol.toStringTag,{value:"Module"})),j8=Object.freeze(Object.defineProperty({__proto__:null,default:`.ootooltip{font-size:.875rem;border-radius:var(--oo-area-radius);position:absolute;background:var(--oo-color-bg-white);color:var(--oo-color-text2);border:1px solid var(--oo-color-gray-e1);box-shadow:0 0 .625rem var(--oo-color-gray-b);opacity:0;z-index:20000}.ootooltip *{box-sizing:border-box}.ootooltip.show{opacity:0;transition:opacity .6s,top .6s,left .6s}.ootooltip_triangle{width:.7rem;height:.7rem;position:absolute;border-bottom-right-radius:.15rem;background:var(--oo-color-bg-white);box-shadow:0 0 .625rem 0 var(--oo-color-gray-9);border-right:1px solid var(--oo-color-gray-e1);border-bottom:1px solid var(--oo-color-gray-e1)}.ootooltip_triangle.top{transform:rotate(45deg)}.ootooltip_triangle.left{transform:rotate(-45deg)}.ootooltip_triangle.right{transform:rotate(135deg)}.ootooltip_triangle.bottom{transform:rotate(-135deg)}.ootooltip_container{display:contents}.hide{display:none}.ootooltip_content{background:var(--oo-color-bg-white);position:relative;border-radius:var(--oo-area-radius);overflow:hidden;padding:.3rem}
`},Symbol.toStringTag,{value:"Module"})),z8=Object.freeze(Object.defineProperty({__proto__:null,default:`.ootooltip{font-size:.725rem;padding:0 .3rem;border-radius:.2rem;position:absolute;background:var(--oo-color-bg-white);color:var(--oo-color-text2);border:1px solid var(--oo-color-gray-9);box-shadow:0 0 .625rem var(--oo-color-gray-9);opacity:0;z-index:20000}.ootooltip *{box-sizing:border-box}.ootooltip.show{opacity:0;transition:opacity .6s,top .6s,left .6s}.ootooltip_triangle{width:.4rem;height:.4rem;position:absolute;background:var(--oo-color-bg-white);border-right:1px solid var(--oo-color-gray-9);border-bottom:1px solid var(--oo-color-gray-9)}.ootooltip_triangle.top{transform:rotate(45deg)}.ootooltip_triangle.left{transform:rotate(-45deg)}.ootooltip_triangle.right{transform:rotate(135deg)}.ootooltip_triangle.bottom{transform:rotate(-135deg)}.ootooltip_container{display:contents}.hide{display:none}.ootooltip_content{background:var(--oo-color-bg-white);position:relative;border-radius:.2rem;overflow:hidden;padding:.3rem}
`},Symbol.toStringTag,{value:"Module"})),Z8=Object.freeze(Object.defineProperty({__proto__:null,default:`.maskNode{opacity:0;background-color:var(--oo-color-gray-d);transition:.2s;position:absolute}.maskNode.show{opacity:.3}
`},Symbol.toStringTag,{value:"Module"}));return _.DatetimePicker=h4,_.Dialog=w4,_.Dragdrop=Y9,_.Draggable=T4,_.Menu=d4,_.Notice=H2,_.Tooltip=B2,_.confirm=E4,_.datetimePicker=c4,_.defineComponent=W9,_.dialog=k4,_.mask=H9,_.notice=Zt,_.tooltip=Ni,_.unmask=B9,Object.defineProperty(_,Symbol.toStringTag,{value:"Module"}),_}({});
//# sourceMappingURL=ooui.iife.js.map
