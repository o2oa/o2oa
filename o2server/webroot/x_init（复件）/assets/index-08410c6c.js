var Ks=Object.defineProperty;var Xs=(t,e,o)=>e in t?Ks(t,e,{enumerable:!0,configurable:!0,writable:!0,value:o}):t[e]=o;var u=(t,e,o)=>(Xs(t,typeof e!="symbol"?e+"":e,o),o),$s=(t,e,o)=>{if(!e.has(t))throw TypeError("Cannot "+o)};var I=(t,e,o)=>{if(e.has(t))throw TypeError("Cannot add the same private member more than once");e instanceof WeakSet?e.add(t):e.set(t,o)};var w=(t,e,o)=>($s(t,e,"access private method"),o);(function(){const e=document.createElement("link").relList;if(e&&e.supports&&e.supports("modulepreload"))return;for(const i of document.querySelectorAll('link[rel="modulepreload"]'))s(i);new MutationObserver(i=>{for(const r of i)if(r.type==="childList")for(const A of r.addedNodes)A.tagName==="LINK"&&A.rel==="modulepreload"&&s(A)}).observe(document,{childList:!0,subtree:!0});function o(i){const r={};return i.integrity&&(r.integrity=i.integrity),i.referrerPolicy&&(r.referrerPolicy=i.referrerPolicy),i.crossOrigin==="use-credentials"?r.credentials="include":i.crossOrigin==="anonymous"?r.credentials="omit":r.credentials="same-origin",r}function s(i){if(i.ep)return;i.ep=!0;const r=o(i);fetch(i.href,r)}})();const Zs="modulepreload",qs=function(t,e){return new URL(t,e).href},ro={},Le=function(e,o,s){if(!o||o.length===0)return e();const i=document.getElementsByTagName("link");return Promise.all(o.map(r=>{if(r=qs(r,s),r in ro)return;ro[r]=!0;const A=r.endsWith(".css"),c=A?'[rel="stylesheet"]':"";if(!!s)for(let p=i.length-1;p>=0;p--){const Q=i[p];if(Q.href===r&&(!A||Q.rel==="stylesheet"))return}else if(document.querySelector(`link[href="${r}"]${c}`))return;const B=document.createElement("link");if(B.rel=A?"stylesheet":Zs,A||(B.as="script",B.crossOrigin=""),B.href=r,document.head.appendChild(B),A)return new Promise((p,Q)=>{B.addEventListener("load",p),B.addEventListener("error",()=>Q(new Error(`Unable to preload CSS for ${r}`)))})})).then(()=>e()).catch(r=>{const A=new Event("vite:preloadError",{cancelable:!0});if(A.payload=r,window.dispatchEvent(A),!A.defaultPrevented)throw r})},ei=`:root {\r
    --oo-color-main: #4A90E2;\r
    --oo-color-main-light: #5b9be4;\r
    --oo-color-main-deep: #337acc;\r
    --oo-color-over: #afcbeb;\r
    --oo-color-deep: #092E59;\r
    --oo-color-light: #F7FBFF;\r
    --oo-color-hover: #ebf1f7;\r
\r
    --oo-color-gray1: #CCCCCC;\r
    --oo-color-gray2: #F0F0F0;\r
    --oo-color-gray-9: #999999;\r
    --oo-color-gray-b: #BBBBBB;\r
    --oo-color-gray-e: #EEEEEE;\r
    --oo-color-gray-e1: #E1E1E1;\r
    --oo-color-gray-d: #DDDDDD;\r
    --oo-color-gray-d1: #D1D1D1;\r
    --oo-color-gray-f3: #F3F3F3;\r
    --oo-color-gray-f9: #F9F9F9;\r
\r
    --oo-color-bg-white: #FFFFFF;\r
\r
    --oo-color-text: #333333;\r
    --oo-color-text2: #666666;\r
    --oo-color-text3: #999999;\r
    --oo-color-text4: #CCCCCC;\r
    --oo-color-text-white: #FFFFFF;\r
    --oo-color-highlight: #F23030;\r
    --oo-color-badge: #F23030;\r
    --oo-color-layout-bg: #F0F0F0;\r
\r
    --oo-color-error: #FB4747;\r
    --oo-color-error-bg: #FFF5F0;\r
    --oo-color-error-border: rgba(242,48,48,0.25);\r
    --oo-color-info: #4A90E2;\r
    --oo-color-info-bg: #F0F7FF;\r
    --oo-color-info-border: rgba(41,122,204,0.25);\r
    --oo-color-warn: #F1A801;\r
    --oo-color-warn-bg: #FFFAF0;\r
    --oo-color-warn-border: rgba(204,155,41,0.25);\r
    --oo-color-success: #66CC80;\r
    --oo-color-success-bg: #F0FFF4;\r
    --oo-color-success-border: rgba(41,204,82,0.25);\r
\r
\r
    --oo-font-family: "Microsoft YaHei", "SimSun", "宋体", serif;\r
    --oo-font-size-default: 1rem;   /*~16*/\r
    --oo-font-size-small: 0.875rem;     /*~14*/\r
    --oo-font-size-smaller: 0.75rem;   /*~12*/\r
    --oo-font-size-large: 1.25rem;     /*~20*/\r
    --oo-font-size-larger: 1.5rem;    /*~24*/\r
    --oo-font-size-largest: 2rem;   /*~32*/\r
    --oo-font-size-huge: 3.5rem;      /*~56*/\r
\r
    --oo-default-radius: 20em;\r
    --oo-area-radius: 0.7553rem;\r
\r
    --oo-height-default: 2.098rem;\r
\r
    /****** menu *****/\r
    --oo-menu-padding: 0.5em 0;\r
    --oo-menu-radius: 0.4em;\r
    --oo-menu-border-color: var(--oo-color-gray-d);\r
    --oo-menu-shadow: 0em 0em 0.5em 0.125em var(--oo-color-gray-e);\r
    --oo-menu-bg-color: var(--oo-color-bg-white);\r
    --oo-dialog-shadow: 0.2em 0.2em 1em 0.125em var(--oo-color-gray-b);\r
    /*****************/\r
\r
\r
\r
\r
\r
    box-sizing: border-box;\r
\r
    font-family: "Microsoft YaHei", "YaHei", "SimSun", "宋体", serif;\r
\r
    padding: 0;\r
}\r
\r
\r
body {\r
    padding: 0;\r
    margin: 0;\r
}\r
\r
\r
\r
/*button[is='oo-bt'] {*/\r
/*    border-radius: var(--oo-default-radius);*/\r
/*    border: 0;*/\r
/*    padding: 0.375em 0.75em;*/\r
/*    cursor: pointer;*/\r
/*    color: var(--oo-color-text-white);*/\r
/*    background-color: var(--oo-color-main);*/\r
/*    display: inline-flex;*/\r
/*    align-items: center;*/\r
/*    font-size: var(--oo-font-size-default);*/\r
/*}*/\r
\r
\r
`,ti=`@font-face {\r
    font-family: 'ooicon';\r
    src:\r
            url('/src/assets/fonts/ooicon.ttf') format('truetype'),\r
            url('/src/assets/fonts/ooicon.woff') format('woff'),\r
            url('/src/assets/fonts/ooicon.svg') format('svg');\r
    font-weight: normal;\r
    font-style: normal;\r
    font-display: block;\r
}\r
*::-webkit-scrollbar {\r
    width:8px;\r
    height: 8px;\r
    border-radius: 8px;\r
    background-color: var(--oo-color-gray-d);\r
}\r
*::-webkit-scrollbar-thumb{\r
    width: 8px;\r
    border-radius: 8px;\r
    background-color: var(--oo-color-gray-b);\r
    cursor: pointer;\r
}\r
*::-webkit-scrollbar-thumb:hover{\r
    width: 8px;\r
    border-radius: 8px;\r
    background-color: var(--oo-color-text2);\r
    cursor: pointer;\r
}\r
* {\r
    scrollbar-color: var(--oo-color-gray-b) var(--oo-color-gray-d);\r
    scrollbar-width: thin;\r
}\r
\r
oo-button, oo-tag{\r
    display: inline-block;\r
    border-radius: var(--oo-default-radius);\r
}\r
oo-input, oo-select, oo-textarea{\r
    display: inline-block;\r
    border-radius: var(--oo-default-radius);\r
    color: var(--oo-color-text2);\r
}\r
oo-input:focus, oo-textarea:focus{\r
    color: var(--oo-color-text2);\r
}\r
oo-capsulae{\r
    display: contents;\r
}\r
oo-code-editor{\r
    display: block;\r
}\r
oo-radio, oo-checkbox{\r
    display: inline-block;\r
    line-height: 1.5em;\r
}\r
oo-radio-group{\r
    display: block;\r
}\r
oo-checkbox-group{\r
    display: block;\r
}\r
oo-menu, oo-dialog{\r
    font-size: 0.875rem;\r
    position: absolute;\r
    z-index: 10000;\r
    border-radius: var(--oo-menu-radius);\r
    border: 1px solid var(--oo-menu-border-color);\r
    box-shadow: var(--oo-menu-shadow);\r
    background-color: var(--oo-menu-bg-color);\r
    display: flex;\r
    width: inherit;\r
    justify-content: flex-start;\r
    align-items: flex-start;\r
    flex-direction: column;\r
    flex-wrap: nowrap;\r
    user-select: none;\r
    overflow: hidden;\r
    visibility: hidden;\r
    transform-origin: 0 0;\r
    opacity: 0;\r
}\r
oo-dialog{\r
    box-shadow: var(--oo-dialog-shadow);\r
}\r
\r
\r
oo-menu.transition, oo-dialog.transition{\r
    transition: scale 0.2s;\r
}\r
oo-menu.show, oo-dialog.show{\r
    visibility: visible;\r
    opacity: 1;\r
}\r
\r
\r
oo-menu-item{\r
    display: contents;\r
}\r
oo-menu-item .icon{\r
    min-width: 1.2em;\r
}\r
oo-menu-item .item{\r
    display: contents;\r
}\r
oo-menu-item hr{\r
    width: 100%;\r
    padding: 0;\r
    margin: 0;\r
}\r
oo-menu-item oo-radio, oo-menu-item oo-checkbox{\r
    width: 100%;\r
}\r
oo-menu-item hr{\r
    width: 100%;\r
    padding: 0;\r
    margin: 0;\r
}\r
.oo-dialog-modal{\r
    position: absolute;\r
    width: 100%;\r
    height: 100%;\r
    top: 0;\r
    left: 0;\r
    background-color: var(--oo-color-gray-d);\r
    opacity: 0;\r
    transition: opacity 0.1s;\r
}\r
\r
\r
oo-notice.default{\r
    opacity: 0;\r
}\r
oo-notice.banner{\r
    width: 100%;\r
    opacity: 0;\r
}\r
oo-notice.body-center{\r
    left: 50%;\r
    transform: translateX(-50%);\r
}\r
oo-notice.absolute{\r
    position: absolute;\r
}\r
oo-notice.fixed{\r
    position: fixed;\r
}\r
\r
oo-notice.transition {\r
    transition: top 0.3s, left 0.3s, bottom 0.3s, right 0.3s, opacity 0.3s;\r
}\r
\r
oo-tabs {\r
    display: block;\r
    position: relative;\r
}\r
oo-tab {\r
    display: block;\r
    transition: opacity 0.2s;\r
}\r
oo-tab.hide{\r
    opacity: 0;\r
    display: none;\r
}\r
\r
[class^="ooicon-"], [class*=" ooicon-"] {\r
    /* use !important to prevent issues with browser extensions that change fonts */\r
    font-family: 'ooicon' !important;\r
    speak: never;\r
    font-style: normal;\r
    font-weight: normal;\r
    font-variant: normal;\r
    text-transform: none;\r
    line-height: 1;\r
\r
    /* Better Font Rendering =========== */\r
    -webkit-font-smoothing: antialiased;\r
    -moz-osx-font-smoothing: grayscale;\r
}\r
\r
.ooicon-puzzle:before {\r
    content: "\\f12e";\r
}\r
.ooicon-srcipt:before {\r
    content: "\\f13b";\r
}\r
.ooicon-file-text:before {\r
    content: "\\f15c";\r
}\r
.ooicon-database:before {\r
    content: "\\f1c0";\r
}\r
.ooicon-category:before {\r
    content: "\\e922";\r
}\r
.ooicon-chat:before {\r
    content: "\\e901";\r
}\r
.ooicon-reset:before {\r
    content: "\\e902";\r
}\r
.ooicon-logo:before {\r
    content: "\\e903";\r
}\r
.ooicon-qr:before {\r
    content: "\\e904";\r
}\r
.ooicon-drop_down:before {\r
    content: "\\e905";\r
}\r
.ooicon-checkmark:before {\r
    content: "\\e906";\r
}\r
.ooicon-info:before {\r
    content: "\\e907";\r
}\r
.ooicon-info_outline:before {\r
    content: "\\e908";\r
}\r
.ooicon-error:before {\r
    content: "\\e909";\r
}\r
.ooicon-error_outline:before {\r
    content: "\\e90a";\r
}\r
.ooicon-remove:before {\r
    content: "\\e90b";\r
}\r
.ooicon-remove_outline:before {\r
    content: "\\e90c";\r
}\r
.ooicon-cancel:before {\r
    content: "\\e90d";\r
}\r
.ooicon-check:before {\r
    content: "\\e90e";\r
}\r
.ooicon-help:before {\r
    content: "\\e90f";\r
}\r
.ooicon-cancel_outline:before {\r
    content: "\\e910";\r
}\r
.ooicon-check_outline:before {\r
    content: "\\e911";\r
}\r
.ooicon-help_outline:before {\r
    content: "\\e912";\r
}\r
.ooicon-close:before {\r
    content: "\\e913";\r
}\r
.ooicon-password:before {\r
    content: "\\e914";\r
}\r
.ooicon-user:before {\r
    content: "\\e915";\r
}\r
.ooicon-phone:before {\r
    content: "\\e916";\r
}\r
.ooicon-verified:before {\r
    content: "\\e917";\r
}\r
.ooicon-computer:before {\r
    content: "\\e918";\r
}\r
.ooicon-home:before {\r
    content: "\\e919";\r
}\r
.ooicon-home1:before {\r
    content: "\\e91a";\r
}\r
.ooicon-description:before {\r
    content: "\\e91b";\r
}\r
.ooicon-o2:before {\r
    content: "\\e91c";\r
}\r
.ooicon-reload:before {\r
    content: "\\e984";\r
}\r
.ooicon-search:before {\r
    content: "\\e986";\r
}\r
.ooicon-checkmark1:before {\r
    content: "\\ea10";\r
}\r
.ooicon-org:before {\r
    content: "\\ea18";\r
}\r
.ooicon-table-tool:before {\r
    content: "\\ea19";\r
}\r
.ooicon-asset:before {\r
    content: "\\ea1a";\r
}\r
.ooicon-execution:before {\r
    content: "\\ea1b";\r
}\r
.ooicon-knowledge:before {\r
    content: "\\ea1c";\r
}\r
.ooicon-guard:before {\r
    content: "\\ea1d";\r
}\r
.ooicon-strategy:before {\r
    content: "\\ea1e";\r
}\r
.ooicon-drive:before {\r
    content: "\\ea29";\r
}\r
.ooicon-cloud-note:before {\r
    content: "\\ea2a";\r
}\r
.ooicon-onboarding:before {\r
    content: "\\ea2b";\r
}\r
.ooicon-dimission:before {\r
    content: "\\ea2c";\r
}\r
.ooicon-employee-file:before {\r
    content: "\\ea2d";\r
}\r
.ooicon-seal:before {\r
    content: "\\ea2e";\r
}\r
.ooicon-appstore:before {\r
    content: "\\ea2f";\r
}\r
.ooicon-app-center:before {\r
    content: "\\ea30";\r
}\r
.ooicon-mobile:before {\r
    content: "\\ea31";\r
}\r
.ooicon-systemconfig:before {\r
    content: "\\ea32";\r
}\r
.ooicon-log-viewer:before {\r
    content: "\\ea33";\r
}\r
.ooicon-publish:before {\r
    content: "\\ea34";\r
}\r
.ooicon-message:before {\r
    content: "\\ea35";\r
}\r
.ooicon-wps:before {\r
    content: "\\ea36";\r
}\r
.ooicon-review:before {\r
    content: "\\ea37";\r
}\r
.ooicon-doc-cooperation:before {\r
    content: "\\ea38";\r
}\r
.ooicon-net-meeting:before {\r
    content: "\\ea39";\r
}\r
.ooicon-notice:before {\r
    content: "\\ea3a";\r
}\r
.ooicon-address-book:before {\r
    content: "\\ea3b";\r
}\r
.ooicon-data-center:before {\r
    content: "\\ea3c";\r
}\r
.ooicon-audit:before {\r
    content: "\\ea3d";\r
}\r
.ooicon-calendar:before {\r
    content: "\\ea3e";\r
}\r
.ooicon-veriface:before {\r
    content: "\\ea3f";\r
}\r
.ooicon-hot-article:before {\r
    content: "\\ea40";\r
}\r
.ooicon-full-text-search:before {\r
    content: "\\ea41";\r
}\r
.ooicon-file-gateway:before {\r
    content: "\\ea42";\r
}\r
.ooicon-mail:before {\r
    content: "\\ea43";\r
}\r
.ooicon-file:before {\r
    content: "\\ea44";\r
}\r
.ooicon-leave:before {\r
    content: "\\ea45";\r
}\r
.ooicon-platform:before {\r
    content: "\\ea46";\r
}\r
.ooicon-training:before {\r
    content: "\\ea47";\r
}\r
.ooicon-onlyoffice:before {\r
    content: "\\ea48";\r
}\r
.ooicon-cms-maintain:before {\r
    content: "\\ea49";\r
}\r
.ooicon-cms:before {\r
    content: "\\ea4a";\r
}\r
.ooicon-minder:before {\r
    content: "\\ea4b";\r
}\r
.ooicon-default:before {\r
    content: "\\ea4c";\r
}\r
.ooicon-portal:before {\r
    content: "\\ea4d";\r
}\r
.ooicon-forum:before {\r
    content: "\\ea4e";\r
}\r
.ooicon-netraffic:before {\r
    content: "\\ea4f";\r
}\r
.ooicon-process-maintain:before {\r
    content: "\\ea50";\r
}\r
.ooicon-process:before {\r
    content: "\\ea51";\r
}\r
.ooicon-work-plan:before {\r
    content: "\\ea52";\r
}\r
.ooicon-control-panel:before {\r
    content: "\\ea53";\r
}\r
.ooicon-crm:before {\r
    content: "\\ea54";\r
}\r
.ooicon-attendance:before {\r
    content: "\\ea55";\r
}\r
.ooicon-score:before {\r
    content: "\\ea56";\r
}\r
.ooicon-work-overtime:before {\r
    content: "\\ea57";\r
}\r
.ooicon-work-overtime-record:before {\r
    content: "\\ea58";\r
}\r
.ooicon-meeting-room:before {\r
    content: "\\ea59";\r
}\r
.ooicon-meeting:before {\r
    content: "\\ea5a";\r
}\r
.ooicon-logistics:before {\r
    content: "\\ea5b";\r
}\r
.ooicon-dynamic:before {\r
    content: "\\ea5c";\r
}\r
.ooicon-weekly:before {\r
    content: "\\ea5d";\r
}\r
.ooicon-work-management:before {\r
    content: "\\ea5e";\r
}\r
.ooicon-work-report:before {\r
    content: "\\ea5f";\r
}\r
.ooicon-supplier:before {\r
    content: "\\ea60";\r
}\r
.ooicon-file-document:before {\r
    content: "\\ea61";\r
}\r
.ooicon-news:before {\r
    content: "\\ea62";\r
}\r
.ooicon-profile:before {\r
    content: "\\ea63";\r
}\r
.ooicon-service:before {\r
    content: "\\ea64";\r
}\r
.ooicon-payment:before {\r
    content: "\\ea65";\r
}\r
.ooicon-party:before {\r
    content: "\\ea66";\r
}\r
.ooicon-travel:before {\r
    content: "\\ea67";\r
}\r
.ooicon-vehicle:before {\r
    content: "\\ea68";\r
}\r
.ooicon-finance:before {\r
    content: "\\ea69";\r
}\r
.ooicon-procurement:before {\r
    content: "\\ea6a";\r
}\r
.ooicon-note:before {\r
    content: "\\ea6b";\r
}\r
.ooicon-reimbursement:before {\r
    content: "\\ea6c";\r
}\r
.ooicon-reimbursement2:before {\r
    content: "\\ea6d";\r
}\r
.ooicon-workcenter:before {\r
    content: "\\ea6e";\r
}\r
.ooicon-office-supplies:before {\r
    content: "\\ea6f";\r
}\r
.ooicon-app-build:before {\r
    content: "\\ea70";\r
}\r
.ooicon-ai:before {\r
    content: "\\ea71";\r
}\r
.ooicon-ternary-management:before {\r
    content: "\\ea72";\r
}\r
.ooicon-radio-checked:before {\r
    content: "\\ea73";\r
}\r
.ooicon-radio-unchecked:before {\r
    content: "\\ea74";\r
}\r
.ooicon-checkbox-checked:before {\r
    content: "\\ea75";\r
}\r
.ooicon-checkbox-unchecked:before {\r
    content: "\\ea76";\r
}\r
.ooicon-menu:before {\r
    content: "\\f0c9";\r
}\r
.ooicon-find:before {\r
    content: "\\f1e5";\r
}\r
.ooicon-add:before {\r
    content: "\\e900";\r
}\r
.ooicon-create:before {\r
    content: "\\e921";\r
}\r
.ooicon-import:before {\r
    content: "\\e920";\r
}\r
.ooicon-download:before {\r
    content: "\\e923";\r
}\r
.ooicon-file_upload:before {\r
    content: "\\e924";\r
}\r
.ooicon-folder_open:before {\r
    content: "\\e91f";\r
}\r
.ooicon-point3:before {\r
    content: "\\e925";\r
}\r
.ooicon-arrow_back:before {\r
    content: "\\e91d";\r
}\r
.ooicon-arrow_forward:before {\r
    content: "\\e91e";\r
}\r
`;var no=globalThis&&globalThis.__spreadArray||function(t,e,o){if(o||arguments.length===2)for(var s=0,i=e.length,r;s<i;s++)(r||!(s in e))&&(r||(r=Array.prototype.slice.call(e,0,s)),r[s]=e[s]);return t.concat(r||Array.prototype.slice.call(e))},oi=function(){function t(e,o,s){this.name=e,this.version=o,this.os=s,this.type="browser"}return t}(),si=function(){function t(e){this.version=e,this.type="node",this.name="node",this.os=process.platform}return t}(),ii=function(){function t(e,o,s,i){this.name=e,this.version=o,this.os=s,this.bot=i,this.type="bot-device"}return t}(),ri=function(){function t(){this.type="bot",this.bot=!0,this.name="bot",this.version=null,this.os=null}return t}(),ni=function(){function t(){this.type="react-native",this.name="react-native",this.version=null,this.os=null}return t}(),Ai=/alexa|bot|crawl(er|ing)|facebookexternalhit|feedburner|google web preview|nagios|postrank|pingdom|slurp|spider|yahoo!|yandex/,ci=/(nuhk|curl|Googlebot|Yammybot|Openbot|Slurp|MSNBot|Ask\ Jeeves\/Teoma|ia_archiver)/,Ao=3,ai=[["aol",/AOLShield\/([0-9\._]+)/],["edge",/Edge\/([0-9\._]+)/],["edge-ios",/EdgiOS\/([0-9\._]+)/],["yandexbrowser",/YaBrowser\/([0-9\._]+)/],["kakaotalk",/KAKAOTALK\s([0-9\.]+)/],["samsung",/SamsungBrowser\/([0-9\.]+)/],["silk",/\bSilk\/([0-9._-]+)\b/],["miui",/MiuiBrowser\/([0-9\.]+)$/],["beaker",/BeakerBrowser\/([0-9\.]+)/],["edge-chromium",/EdgA?\/([0-9\.]+)/],["chromium-webview",/(?!Chrom.*OPR)wv\).*Chrom(?:e|ium)\/([0-9\.]+)(:?\s|$)/],["chrome",/(?!Chrom.*OPR)Chrom(?:e|ium)\/([0-9\.]+)(:?\s|$)/],["phantomjs",/PhantomJS\/([0-9\.]+)(:?\s|$)/],["crios",/CriOS\/([0-9\.]+)(:?\s|$)/],["firefox",/Firefox\/([0-9\.]+)(?:\s|$)/],["fxios",/FxiOS\/([0-9\.]+)/],["opera-mini",/Opera Mini.*Version\/([0-9\.]+)/],["opera",/Opera\/([0-9\.]+)(?:\s|$)/],["opera",/OPR\/([0-9\.]+)(:?\s|$)/],["pie",/^Microsoft Pocket Internet Explorer\/(\d+\.\d+)$/],["pie",/^Mozilla\/\d\.\d+\s\(compatible;\s(?:MSP?IE|MSInternet Explorer) (\d+\.\d+);.*Windows CE.*\)$/],["netfront",/^Mozilla\/\d\.\d+.*NetFront\/(\d.\d)/],["ie",/Trident\/7\.0.*rv\:([0-9\.]+).*\).*Gecko$/],["ie",/MSIE\s([0-9\.]+);.*Trident\/[4-7].0/],["ie",/MSIE\s(7\.0)/],["bb10",/BB10;\sTouch.*Version\/([0-9\.]+)/],["android",/Android\s([0-9\.]+)/],["ios",/Version\/([0-9\._]+).*Mobile.*Safari.*/],["safari",/Version\/([0-9\._]+).*Safari/],["facebook",/FB[AS]V\/([0-9\.]+)/],["instagram",/Instagram\s([0-9\.]+)/],["ios-webview",/AppleWebKit\/([0-9\.]+).*Mobile/],["ios-webview",/AppleWebKit\/([0-9\.]+).*Gecko\)$/],["curl",/^curl\/([0-9\.]+)$/],["searchbot",Ai]],co=[["iOS",/iP(hone|od|ad)/],["Android OS",/Android/],["BlackBerry OS",/BlackBerry|BB10/],["Windows Mobile",/IEMobile/],["Amazon OS",/Kindle/],["Windows 3.11",/Win16/],["Windows 95",/(Windows 95)|(Win95)|(Windows_95)/],["Windows 98",/(Windows 98)|(Win98)/],["Windows 2000",/(Windows NT 5.0)|(Windows 2000)/],["Windows XP",/(Windows NT 5.1)|(Windows XP)/],["Windows Server 2003",/(Windows NT 5.2)/],["Windows Vista",/(Windows NT 6.0)/],["Windows 7",/(Windows NT 6.1)/],["Windows 8",/(Windows NT 6.2)/],["Windows 8.1",/(Windows NT 6.3)/],["Windows 10",/(Windows NT 10.0)/],["Windows ME",/Windows ME/],["Windows CE",/Windows CE|WinCE|Microsoft Pocket Internet Explorer/],["Open BSD",/OpenBSD/],["Sun OS",/SunOS/],["Chrome OS",/CrOS/],["Linux",/(Linux)|(X11)/],["Mac OS",/(Mac_PowerPC)|(Macintosh)/],["QNX",/QNX/],["BeOS",/BeOS/],["OS/2",/OS\/2/]];function li(t){return t?ao(t):typeof document>"u"&&typeof navigator<"u"&&navigator.product==="ReactNative"?new ni:typeof navigator<"u"?ao(navigator.userAgent):gi()}function hi(t){return t!==""&&ai.reduce(function(e,o){var s=o[0],i=o[1];if(e)return e;var r=i.exec(t);return!!r&&[s,r]},!1)}function ao(t){var e=hi(t);if(!e)return null;var o=e[0],s=e[1];if(o==="searchbot")return new ri;var i=s[1]&&s[1].split(".").join("_").split("_").slice(0,3);i?i.length<Ao&&(i=no(no([],i,!0),Bi(Ao-i.length),!0)):i=[];var r=i.join("."),A=di(t),c=ci.exec(t);return c&&c[1]?new ii(o,r,A,c[1]):new oi(o,r,A)}function di(t){for(var e=0,o=co.length;e<o;e++){var s=co[e],i=s[0],r=s[1],A=r.exec(t);if(A)return i}return null}function gi(){var t=typeof process<"u"&&process.version;return t?new si(process.version.slice(1)):null}function Bi(t){for(var e=[],o=0;o<t;o++)e.push("0");return e}const lo=function(t,e,o){const s=e.toLowerCase();s==="text"?t.textContent=o:s==="html"?t.innerHTML=o:s==="styles"?n.setStyles(t,o):t.setAttribute(e,o)},Ei=function(t,e){const o=e.toLowerCase();if(o==="text")return t.innerText||t.textContent||"";if(o==="html")return t.innerHTML||"";if(o==="value")switch(t.tagName.toString().toLowerCase()){case"select":return t.options[t.selectedIndex].value;default:return t.value}return t.getAttribute(e)},Qe=function(t){const e=mi(t||"").split(" "),o={};return e.filter(function(s){if(s!==""&&!o[s])return o[s]=s})},ho=function(t){return!t.tagName||/^(?:body|html)$/i.test(t.tagName)},go=function(t){return/html/i.test(t.tagName)},Se=function(t,e){return parseInt(n.getStyle(t,e))||0},ui=function(t){return Se(t,"border-top-width")},pi=function(t){return Se(t,"border-left-width")},Qi=function(t,e){return{left:t.x-Se(e,"margin-left"),top:t.y-Se(e,"margin-top")}},n=(t,e)=>{let o;const s=t.replace(/^\S*?(?=\.|#|$)/,c=>(o=c,""));let i="",r;s&&(r=s.split(".").filter(c=>!!c).map(c=>{const d=c.split("#"),B=d.shift();return i=d.pop()||i,B}));const A=document.createElement(o);return i&&A.setAttribute("id",i),r&&r.length&&A.setAttribute("class",r.join(" ")),e&&n.set(A,e),A};Object.assign(n,{el:(t,e)=>oe(t)==="string"&&t?(e||document).querySelector(t):t,els:(t,e)=>(e||document).querySelectorAll(t),addClass:(t,e)=>{const o=n.el(t);return Qe(e).forEach(s=>{o.classList.add(s)}),o},removeClass:(t,e)=>{const o=n.el(t);return Qe(e).forEach(s=>{o.classList.remove(s)}),o},hasClass:(t,e)=>n.el(t).classList.contains(e),toggleClass:(t,e,o)=>{const s=n.el(t);return Qe(e).forEach(i=>{let r=o;(r==null||r===!1)&&(r=!n.hasClass(s,i)),r?n.addClass(s,i):n.removeClass(s,i)}),s},checkClass:(t,e,o)=>{const s=n.el(t);return Qe(e).forEach(i=>{o?n.addClass(s,i):n.removeClass(s,i)}),s},mapProps:(t,e)=>{const o=n.el(t);for(const s in e){const i=x(s);if(typeof e[s]=="boolean")e[s]=o.hasAttribute(s)||o.hasAttribute(i)||!1;else{const r=o.getAttribute(s)||o.getAttribute(i)||"";r!==e[s]&&(e[s]=r)}}return o},toggleAttr:(t,e,o)=>{const s=n.el(t);return o?s.setAttribute(e,o):s.removeAttribute(e),s},set:(t,e,o)=>{const s=n.el(t);return typeof e=="object"?Object.keys(e).forEach(i=>{lo(s,i,e[i])}):lo(s,e,o),s},get:(t,e)=>{const o=n.el(t);return Ei(o,e)},setProperty:(t,e,o)=>{const s=n.el(t);return s.setAttribute(e,o),s},getScroll:t=>{const e=n.el(t);return{x:e.scrollLeft,y:e.scrollTop}},getScrolls:t=>{let e=n.el(t).parentNode,o={x:0,y:0};for(;e&&!ho(e);)o.x+=e.scrollLeft,o.y+=e.scrollTop,e=e.parentNode;return o},getOffsetParent:t=>{let e=t.offsetParent;for(;e&&["table","td","th"].includes(e.tagName.toString().toLowerCase());)e=e.offsetParent;return e||document.body},getPosition:(t,e)=>{const o=n.el(t);e===window&&(e=document.documentElement);const s=t.getBoundingClientRect(),i=document.documentElement,r=n.getScroll(i),A=n.getScrolls(o),c=n.getStyle(o,"position")==="fixed",d={x:s.left+A.x+(c?0:r.x)-i.clientLeft,y:s.top+A.y+(c?0:r.y)-i.clientTop},B=n.getScrolls(o),p={x:d.x-B.x,y:d.y-B.y};if(e){const Q=n.getPosition(e);return{x:p.x-Q.x-pi(e),y:p.y-Q.y-ui(e)}}return p},setPosition:(t,e)=>{const o=n.el(t);return n.setStyles(o,Qi(e.this))},getSize:t=>{const e=n.el(t);if(go(e))return{x:e.clientWidth,y:e.clientHeight};{const o=e.getBoundingClientRect();return{x:o.width,y:o.height}}},getScrollSize:t=>{const e=n.el(t);if(/^(?:body|html)$/i.test(e.tagName)){const o=document.documentElement,s=document.body;return{x:Math.max(o.scrollWidth,s.scrollWidth),y:Math.max(o.scrollHeight,s.scrollHeight)}}return{x:e.scrollWidth,y:e.scrollHeight}},getStyle:function(t,e){const o=n.el(t),s=le(e);let i=o.style[s];return i||(i=window.getComputedStyle(o)[s]),i},getStyles:t=>{const e=[...arguments],o=n.el(e.shift()),s={};return e.forEach(i=>{s[i]=n.getStyle(o,i)}),s},setStyle:(t,e,o)=>{const s=n.el(t);return typeof e=="object"?Object.keys(e).forEach(i=>{s.style[le(i)]=e[i]}):s.style[le(e)]=o,s},setStyles:(t,e)=>n.setStyle(t,e),show:t=>{const e=n.el(t);return n.getStyle(e,"display")==="none"&&n.setStyle(e,"display",e.dataset.storeDisplay||"block"),e},hide:t=>{const e=n.el(t),o=n.getStyle(e,"display");return o!=="none"&&(e.dataset.storeDisplay=o,n.setStyle(e,"display","none")),e},getParentSrcollNode:t=>{let e=n.el(t).parentElement;for(;e&&(n.getScrollSize(e).y-2<=n.getSize(e).y||n.getStyle(e,"overflow")!=="auto"&&n.getStyle(e,"overflow-y")!=="auto");)e=e.parentElement;return e||document.documentElement},getParent:function(t,e){if(e){let o=t.parentElement;for(;o&&!o.matches(e);)o=o.parentElement;return o}else return t.parentElement},isBody:t=>ho(n.el(t)),isHtml:t=>go(n.el(t)),empty:t=>{const e=n.el(t);for(;e.childNodes.length;)e.removeChild(e.childNodes[0]);return e},peel:(t,e)=>{const{node:o,position:s}=e&&oe(e)==="element"?{node:e,position:"beforeend"}:(()=>{if(e){const r=document.createComment("");t.parentNode.insertBefore(r,t)}return{node:t,position:"beforebegin"}})();let i=t.firstElementChild;for(;i;)o.insertAdjacentElement(s,i),i=t.firstElementChild;t.remove()}});li();function oe(t){if(t==null)return"null";if(Array.isArray(t))return"array";if(t instanceof Map)return"map";if(t instanceof Set)return"set";if(t===window)return"window";if(t instanceof Date)return"date";if(t instanceof RegExp)return"regexp";if(t instanceof Error)return"error";if(t instanceof Promise)return"promise";if(t.nodeName){if(t.nodeType===1)return"element";if(t.nodeType===3)return/\S/.test(t.nodeValue)?"textnode":"whitespace";if(t.nodeType===9)return"document";if(t.nodeType||t.nodeType===0)return t.nodeName}else if(typeof t.length=="number"&&t.callee)return"arguments";return typeof t}function jo(t){if(!t)return t;const e=oe(t);return e==="array"||e==="object"?Ho(t):t}function wi(t){let e=t.length,o=new Array(e);for(;e--;)o[e]=jo(t[e]);return o}function Ho(t){if(oe(t)==="array")return wi(t);const e={};for(let o in t)e[o]=jo(t[o]);return e}function x(t){return t.replace(/([A-Z])/g,"-$1").toLowerCase()}function le(t){return t.replace(/-(\w)/g,function(e,o){return o.toUpperCase()})}function mi(t){return t.replace(/\s+/g," ").trim()}function zo(t){return String(t).replace(/\b[a-z]/g,function(e){return e.toUpperCase()})}function fi(t){return t.replace(/(?:<script(?:\s+[\w-]+(?:=(?:"[^"]*"|'[^']*'))?)*\s*>([\s\S]*?)<\/script\s*>)|(?:on\w+\s*=\s*(?:"[^"]*"|'[^']*'))|(?:javascript:.*)/g,"")}const Mi="data:font/ttf;base64,AAEAAAALAIAAAwAwT1MvMg8SDwYAAAC8AAAAYGNtYXCUt5O5AAABHAAAAKxnYXNwAAAAEAAAAcgAAAAIZ2x5Ztd1CKIAAAHQAACWhGhlYWQkeKgBAACYVAAAADZoaGVhB+MEKQAAmIwAAAAkaG10eBMmO90AAJiwAAACIGxvY2GYVHC2AACa0AAAARJtYXhwAKICxAAAm+QAAAAgbmFtZZfiJnkAAJwEAAABenBvc3QAAwAAAACdgAAAACAAAwP3AZAABQAAApkCzAAAAI8CmQLMAAAB6wAzAQkAAAAAAAAAAAAAAAAAAAABEAAAAAAAAAAAAAAAAAAAAABAAADx5QPA/8AAQAPAAEAAAAABAAAAAAAAAAAAAAAgAAAAAAADAAAAAwAAABwAAQADAAAAHAADAAEAAAAcAAQAkAAAACAAIAAEAAAAAQAg6SXphOmG6hDqHup28MnxLvE78VzxwPHl//3//wAAAAAAIOkA6YTphuoQ6hjqKfDJ8S7xO/Fc8cDx5f/9//8AAf/jFwQWphalFhwWFRYLD7kPVQ9JDykOxg6iAAMAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAf//AA8AAQAAAAAAAAAAAAIAADc5AQAAAAABAAAAAAAAAAAAAgAANzkBAAAAAAEAAAAAAAAAAAACAAA3OQEAAAAAAQCrAFYDVQL/AAsAAAEhESMRITUhETMRIQNV/ttg/tsBJWABJQF7/tsBJWABJP7cAAAAAAUAKf/WA/YDegAtAF0AaQB1AIEAAAEVHgEHMAYHHgEVFgYHDgEnIiYnLgEnMRc3PgE3HgEXMDI7ATI2NTQmJz4BNzEBMhceARcWFRQHDgEHBgcOASMiJy4BJyYnLgE3PgE3PgE3JicuAScmNTQ3PgE3NjMDIgYVFBYzMjY1NCYzIgYVFBYzMjY1NCYzIgYVFBYzMjY1NCYDkB0fBRQhBV8BAwMuiy4JEAgvSRcGDw4bDAsZDgUDBR52CQgKFQv+OVVKS28gIBsbZEdGVxEgES8uL1MiIhYHBQECCwYgHQsZGRoqDQ0hIXJMS1XLFyAgFxYgILEWICAWFyAgwxYgIBYXICABOQEdWj4+IwUCBAMHAiMUAgIBBykgBwYFDAYFBwIlShkoEBAkFAJBISBvS0tVVUdHayIiCQEDBgYVDQ4OBQ4GCAsCBwoKERsbRSgpLVNLS3EhIv6rHxcXICAXFx8fFxcgIBcXHx8XFyAgFxcfAAAAAAIAXQALA6MDiwAZADMAACUmJyY2NzY3PgE3Jw4BBwYHDgEXFhcHBQMHARM3FhcWBgcGBw4BBxc+ATc2Nz4BJyYnNyUBCy4XFwIYGTAnYDQESIc3QSAhAh8fP18BLQF+AWsBfi4XFwIYGTAnYDMDSIc3QSEgAh8fP1/+0+QyPj2APD0xKC8IcQlAN0FSUatSU0JgEAE+fwKO/sF/Mj49fz09MScwB3IJQDhBUVKqU1JDXxEAAAIAAP/ABAADwAAbANwAAAEyFx4BFxYVFAcOAQcGIyInLgEnJjU0Nz4BNzYFISIGBzAiMRQiMQc4ASMUMDEOAQcxMCIVMAYxFDAxIhQxDgEVFDAxFAYdARQWMyEyFh0BMBQxFAYjMCIrATUiJjU0NjsCMjY9ATQmKwEiBhUxFBY7AjIWFRQGKwIiBh0BFBYzITI2OwE4ATE0MjEzMDIxNT4BNRE0JjUxLgEjITUiMCMiJjU0NjMyMDMhHgEVMBQVERUUBiMhIiYnNTQmIyIGFREUFhceATMhPgE3NTQ2NRE0JicjMCIHJiIjAgBqXV6LKCgoKIteXWpqXV6LKCgoKIteXQFg/g4EBwMBAQEBAgIBAQEBAQIBEgwBYQgMDAkBAccJCwsIATIfKysfZAwSEgwaQwkLCwlEGQwSEgwBLgIEAgEBAQEICgEDEAr+oQEBCAwMCAEBAZIIDAwJ/m4IDAERDQwSAgEEDwgB+AsQAgESDAEBAQEBAQPAKCiLXl1qal1eiygoKCiLXl1qal1eiygo5gICAQEBAQICAQEBAQIEAgEBAwFlDBIMCccBCQwBDAgIDCweDB8rEQ0MEgwICAwRDWUMEgEBAQMPCQEuAgQCCgwBDAgIDAEMCAEB/nICCQwMCPwNEREN/tIDBwMHCgENCgEBAwEB+AwRAQEBAAAACwAg/8AEIAPAAAIACAAVABgAHAAhACUAKQA1AD0ARQAAJRc1ARczFRcRARUXNTMVIxc1MzUjNQE1IyUzNSMTESERITczNSMXMzUjAzUzNSM1IxUjFzMVAzM1IxUjFTMFFSMVMzUjNQPGWvwAUMdPARpQTyigT0/9XHMBJlpa2gFm/ppQx8cjc3NKSrlKSkpKWjo6OjoBV0TNRBpaWgOmUMdPAWb9zSdMJk2ZTJpNAUBzQED+mgFm/ppPx6Nz/mZGRWlpRUYBUYlEiZk6Ojo6AAEAZADvA5wCgAAMAAAlFjI3ATYmIyEiBhcBAdMSNhMBbhMLG/zuGwsTAW/vEhIBZRIaGhL+mwAAAQAgAEwD4AMcAAUAABM3FwEXASBg8AIQYP2QAZxg8AIQYP2QAAAAAAMAVv/0A6oDSAADAAcAFwAAATUjFRMRIxETMhcWFRQHBiMiJyY1NDc2AipUVFQqsH19fX2wsH19fX0CHlZW/qoBAP8AAoB9fbCwfX19fbCwfX0AAAQAVv/0A6oDSAADABMAIwAnAAABNTMVAzI3NjU0JyYjIgcGFRQXFhMyFxYVFAcGIyInJjU0NzYTETMRAdZUKoxlZWVljIxlZWVljLB9fX19sLB9fX19hlQCHlZW/iplZYyMZWVlZYyMZWUDAH19sLB9fX19sLB9ff2AAQD/AAAAAwBW//QDqgNIAAMABwAXAAABESMRFzUjFRMyFxYVFAcGIyInJjU0NzYCKlRUVCqwfX19fbCwfX19fQF0AQD/AKxWVgKAfX2wsH19fX2wsH19AAAABABW//QDqgNIAA8AHwAjACcAACUyNzY1NCcmIyIHBhUUFxYTMhcWFRQHBiMiJyY1NDc2FzMRIxUzFSMCAIxlZWVljIxlZWVljLB9fX19sLB9fX19hlRUVFRIZWWMjGVlZWWMjGVlAwB9fbCwfX19fbCwfX3U/wBWVgAAAAIAVv/0A6oDSAADABMAAAE1IRUTMhcWFRQHBiMiJyY1NDc2Atb+VNawfX19fbCwfX19fQF0VFQB1H19sLB9fX19sLB9fQAAAAMAVv/0A6oDSAAPAB8AJAAAJTI3NjU0JyYjIgcGFRQXFhMyFxYVFAcGIyInJjU0NzYDIRUhNQIAjGVlZWWMjGVlZWWMsH19fX2wsH19fX0mAaz+VEhlZYyMZWVlZYyMZWUDAH19sLB9fX19sLB9ff6AVFQAAgBW//QDqgNIAAsAGwAAASc3JwcnBxcHFzcXAzIXFhUUBwYjIicmNTQ3NgLWmpo8mpo8mpo8mpqasH19fX2wsH19fX0BBJqaPJqaPJqaPJqaAoB9fbCwfX19fbCwfX0AAgBW//QDqgNIAAUAFQAAJQEnAScHATIXFhUUBwYjIicmNTQ3NgGqAYA8/ryYPAEqsH19fX2wsH19fX3IAYA+/ryYPAGqfX2wsH19fX2wsH19AAAAAwBW//QDqgNIABwAIAAwAAABNjU0JyYjIgcGFTM0NzYzMhcWFRQPAQYdATM0NwM1IxUTMhcWFRQHBiMiJyY1NDc2AoIoMjJGRjIyVBoaIiIaGho0MlQyMlQqsH19fX2wsH19fX0Bvig4RjIyMjJGIhoaGhoiIho2NkIWQjb+3lRUAtR9fbCwfX19fbCwfX0AAwBW//QDqgNIAA8AHwArAAAlMjc2NTQnJiMiBwYVFBcWEzIXFhUUBwYjIicmNTQ3NgEXBxcHJwcnNyc3FwIAjGVlZWWMjGVlZWWMsH19fX2wsH19fX0BHjxubjxubjxubjxuSGVljIxlZWVljIxlZQMAfX2wsH19fX2wsH19/wA8bm48bm48bm48bgADAFb/9AOqA0gADwAfACUAACUyNzY1NCcmIyIHBhUUFxYTMhcWFRQHBiMiJyY1NDc2BRcBJzcXAgCMZWVlZYyMZWVlZYywfX19fbCwfX19fQF0PP6q1DyYSGVljIxlZWVljIxlZQMAfX2wsH19fX2wsH197jz+qtY8mAAABABW//QDqgNIAB0ALQA9AEEAAAEyFxYVFAcGFSM0NzY3Njc2NTQnJiMiBwYVIzQ3NhMyNzY1NCcmIyIHBhUUFxYTMhcWFRQHBiMiJyY1NDc2EzUzFQIARjIyQEBUFBQYGBQUGhoiIhoaVDIyRoxlZWVljIxlZWVljLB9fX19sLB9fX19hlQCnjIyRjY5OS4uISEPDxYWHCIZGRkZIkYyMv2qZWWMjGVlZWWMjGVlAwB9fbCwfX19fbCwfX39VlZWAAAAAAEAvgBZA0IC3QALAAAJAgcJAScJATcJAQNC/v8BAUH+//7/QQEB/v9BAQEBAQKc/v/+/0EBAf7/QQEBAQFB/v8BAQAAAwBC/9ADvgOwACUANQBAAAATNTQ3PgE3NjMyFx4BFxYVMRUzMhYVMREUBiMxISImNTERNDY7ARMVMzU+ATU0JiMiBhUUFhcDFSE1NCYjIgYVMdYYF1E2Nj4+NjZRFxgxKTo6Kf1KKTo6KTH4ZBYbOikpOhsWYwEqVz4+VwIjYz42NlEYFxcYUTY2PmM6Kf5zKTo6KQGNKTr+snBwDS4bKTs7KRstDQGwY2M+V1c+AAACADj/+APIA4gAHwA1AAABNDc+ATc2MzIXHgEXFhUxFRQHDgEHBiMiJy4BJyY1MQM2Nz4BNzYzMDI5ATIXHgEXFhcVITUBHBISPikqLy8qKT4SEhISPikqLy8qKT4SEuQxNjVzPD0/AT48PXI3NjL8cAKkLyopPhISEhI+KSovWy8qKT4SEhISPikqL/5GHRcXHwgJCQgfFxYel5cAAAAAAwDW/8gDKgN0AAMABwAbAAAlESERBTUjFQEyFxYVERQHBiMhIicmNRE0NzYzAuD+QAE2rAEANCYmJiY0/qw0JiYmJjSeAlb9qoAqKgNWJiY0/VQ0JiYmJjQCrDQmJgAAAgCA/8gDgAN0AAUAEQAAJQEnAScHAQURFAcGByYnJjURAaoBVjz+5m48AQABgG5upKRubsgBVjz+6G48AgCs/wCyk5MoKJOTsgEAAAMAaQAjA6kDHgAiACYAKwAAASEiBhURFBY7ARUjIgYVFBYzITI2NTQmKwE1MzI2NRE0JiMTIREhBSERIREDYP1TNxMTN7FwDhUVDgIrDhQUDnGyNxISNxn9IQLf/XACQP3AAx43E/43EzdYFA8OFRUODxRYNxMByRM3/dQB+0/+owFdAAIAagBPA5YDMQAFAA4AAAkCNQkBBxEjNSMVIxElA5b+av5qAZYBlmXLzMsBMQF1ATv+xYABPP7Edf7PzMwBMeUAAAAAAQBWAFUDqgMrAAoAACUjESMJASMRIxEjAarUgAGqAaqA1KxVAVYBgP6A/qoBAAAABACf//QDYQNiAAIABgAKABwAAAEzJxM1IRUFNSEVCQERFAcGIyEiJyY1EzQ3NjMhAiv09IT+ogFe/qIBCAEIGxsj/fAjGxsCGhojAWECL/H+MllZr1lZAr/++P3wIxoZGRojAsIjGhkAAAAAAQBIAAgDuAN4AR8AAAEqASMxKgE5ASEiBgcxOAEjMRQiBzEwIjkBFCI5AQYwOQEwIjkBDgEHOQEwBjkBFAYHMTAUOQEiFDkBDgEHMTgBFTEGFBUxFRQWMzEhMhYVMTgBFTERMRwBFTEUBiMxKgE5AiE1LgE1MTQ2NzEzMTMyNjUxNTQmIzEjIgYVMRUUFjMxMzEzMhYVMRQGIzEjMSMiBhUxFRQWMzEhMjY3MTM4ATkBNDIzOQEwMjkBNDI5ATM+ATUxETQmJzE1OAE5AS4BIzEhNSoBOQEiJjUxNDYzMTAyMzkBITIWFTEwFDkCETEwFDkBFAYjMTAiOQEhKgE5ASImNTE1MRE0JiMxIgYVMREUFhcxHgEzMSEyNjcxMDQ5ATY0NTERNCYjMSMDiAEBAQEC/PQGCwUBAQEBAQEBAgMCAQEBAQICAQEbFAInDRITDQEC/sgNEhIMAU4wREQwnBMcHBMoag0SEg1qKBMcHBMB2QMGAwEBAQEBAQwQAQEEGRD92wEBDRISDQEBAnQNEhMNAf2QAQINExwTFBsCAwUXDgMTEBoDARsTAgN4AwMBAQEBAgQCAQEBAQEBAwcEAQIEAp0UGxMNAf7KAQEBDRMBARIMDRIBRDASMEQbEwETHBINDRIbFJ4THAEBAQEGFw4B2AMGAwEPEwESDQ0SEw0C/ZACDRMSDQEBiRMbGxP+KAYKBQsPFRABAwQCAxITHAAAAAABANUABQLHA1EABQAACQIHCQECx/6mAVpM/loBpgMF/qb+pkwBpgGmAAAAAAEA+gABAwADVQAFAAATNwkBJwH6XAGq/lZcAVIC+1r+Vv5WWgFQAAACAFAAUAOwAwYAAwAZAAAlESERATIXFhURFAcGIyEiJyY1ETQ3NjMhFwNb/UoCtiMZGRkZI/1KIxkZGRkjAQRXpwGw/lACBxoaI/5QIxoaGhojAgcjGhtYAAAAAAIAev/rA4YDawAHAAwAABMJASMRIREjESEVITV6AYYBht7+sN4DDPz0AToBif53/rEBTwIxcXEAAgBWAAEDqgNVAAsAGwAAATUjNSMVIxUzFTM1AzIXFhUUBwYjIicmNTQ3NgLWrFSsrFQqsH19fX2wsH19fX0BgVSsrFSsrAHUfX2wsH19fX2wsH19AAAAAAQARwBbA70DJQAlAD4ASwBYAAATDgEPAREXHgEfAQ0BNz4BMzI2NzY3NjQnJicuASMiJi8BISIGBwUeAR8BEQcOAQ8BIScuAS8BETc+AT8BIRcFBhQXHgE3JTUlJgYHFwYUFx4BPwE1JyYGB7UKIhsnHBwoCAkBRgFGBwcwFwoSAwMCAgICAwMSChcwBwf+u+BlBQJsBh8QJiQPHwcR/dwTCB8OIiIOHwgTAiQP/asEBARtxQEx/s/FbQRDBAQEVpnu7plWBAMSJikJDf4jAwUmHyMDAh4gMQsHCUVFokVFCQcLMSAeBQ5SDx0JEv6PEgcfDyMhDh8JEwFwEwkfDiEioQcUBwoGAgM4AwIFC5wHFAcLBQIDOAMCBgoAAgCQAAUDcAOFAAQADAAANyEVITUJAjMRIREzkALg/SAC4P6Q/pDSATzSb2pqAdr+kAFwATz+xAACAJAABQNwA4UABAAMAAA3IRUhNTcRIwkBIxEhkALg/SDS0gFwAXDS/sRvampqATwBcP6Q/sQAAAMBqgBVAlYDAQAPAB8ALwAAATIXFhUUBwYjIicmNTQ3NhMyFxYVFAcGIyInJjU0NzY3IicmNTQ3NjMyFxYVFAcGAgAiGhoaGiIiGhoaGiIiGhoaGiIiGhoaGiIiGhoaGiIiGhoaGgEBGhoiIhoaGhoiIhoaAQAaGiIiGhoaGiIiGhpUGhoiIhoaGhoiIhoaAAEAaAAoA5gDWAA2AAABITcuASMiBgcOARUUFhceATMyNjc+ATcXBgcOAQcGIyInLgEnJjU0Nz4BNzYzMhceARcWFzcRA5j+znIrcD09cCssLi4sK3A9PXArBAcDTRwiI04rKy5VSkpvICAgIG9KSlUqKChJISEbeAImciwuLiwrcD09cCssLi4sAwgERCAZGiQKCiAgb0pKVVVKSm8gIAgJHxYWHHj+zgAAAAACAFAAJQObA3AAKQBFAAAlJy4BIz4BNTQnLgEnJiMiBw4BBwYVFBceARcWMzI2NxQWHwEeATc2JiclIicuAScmNTQ3PgE3NjMyFx4BFxYVFAcOAQcGA5XMECENJCkZGlg7O0NDOztYGhkZGlg7O0M8bCoNDq4XPxYXBBn9/y0nJzsREREROycnLS0nJzsREREROycnka4ODSpsPEM7O1gaGRkaWDs7Q0M7O1gaGSklDiEQzBkEFxY/F8MRETsnJy0tJyc7ERERETsnJy0tJyc7EREAAAEAPQBSA8MDEgAFAAAJAScHCQEDNv5ZxY0BUgI0AxL+WsWN/q4CMwABAHr/+wORA2wAgAAAATQ2NTQmKwE1OAExPgE3PgE3PgE3PgE1NCYnLgEnLgEnLgEjIgYHDgEHDgEHDgEVFBYXHgEXHgEXHgEXOAExFSMiBhUcARUOARUUFjMyNjU0Jic8ATU0NjsBFTAUMQ4BFRQWMzI2NTQmJzA0MTUzMhYVHAEXDgEVFBYzMjY1NCYnA1ABY0WZEB8PER8NDRUHBwgIBwcVDQ0fERElEhMlEREfDQ0VBwcHBwcHFQ0NHxEPHxCYRmIbJiwfHywmG1c9mCQyOSgoODIkmT1WARwmLB8fLCUcAQYBAQFGYkEBBwcHFA0OHxERJBMTJBERHw4NFAcIBwcIBxQNDh8RESQTEyQRER8ODRQHBwcBQWJGAQEBBCocHywsHxwqBAEBAT1X4QEDNyYoODgoJjYEAeFXPQEBAQQqHB8sLB8cKgQAAAYAif/3A5oDTQAcACsAOgBWAI0AmwAAARE0JiMhIgYVERQWMzAzOgEzMjMyJjc+ATc2MhclITIWFRQGIyEiJjU0NjMTIyImNTQ2OwEyFhUUBiMlMhceARcWFRQHDgEHBiMiJy4BJyY1NDc+ATc2FyIGBwYWMRcWFA8BDgEjIiYvASMmIgcOARUUFjMyNjc2FjEXHgE3MDY3NiYvATAmNz4BNTQmIycyFhUUBiMhIiY1NDYzAxhALf5LLj9ALR4fTiMkCRgtOglTPxREL/4wAREMEBAM/u8MEBAMNjcLEBALNwsQEAsBTyomJTcREBARNyUmKislJTgQEBAQOCUlHwYNBgUDKgoKBAUNBgcNBSoBAQMCAgI1JgoTCQQFOQQLBhQDAQQENwYDBAQ2JWgMEBAM/u8MEBAMAbABLy1BQS3+ES1BjV4ILwwEBPcQCwwQEAwLEP6AEAsMEBAMCxBqEBA4JSUrKiUmNxARERA3JiUqKyUlOBAQZgICAgUqChwLBAQGBgQrAQMGDQcmNQQFAgQ5BAQBBxAGCwQ4BwYIEgkmNdgQDAwQEAwMEAAAAgCQABoDcAMyAEwAjQAAAR4BHwI/AT4BMzYWFx4BFw4BDwIXHgEXHgEXFgcOAQcGBw4BJyInLgEnJjU+ATc+AT8CJy4BNT4BNz4BFzIWHwM3PgE3NjIXAwcnJiIHDgEfASMOARUUFhczFSMiBgcOARcGFhceATsBFR4BNxY2NzUzMjY1NCYrATUzPgE1NCYnIzc2NCcmIgcCTQQHAwYFBQsJEwoVKBAQEgEBGhYGCwoQHQ4sMQQCCAglGxwjNXU9U0NDXxoaAjIsDBgNDQoKEhYBExAPKBUIDwgHCgcEBAoFHU8dDkNCBhIHBwEHTFAICwsIUFAECAMDBAEBBAMDCARQAQ0ICQwBUQkNDQlRUQgLCwhQSwYGBxIGAzIEBgQHBgMEBAMCDQ0OJhUZKgwCBgkMGg0pbDspKCdHHR4VHR0CFxdWPDxLPW8qDBcKCwcHDCcWFSUODQwCAgIDBAQHBgoFGhr+1ENDBgYGEwdMAgwJCA0BJAMDBAgEBQgDAwNHCAoBAQoIRw0JCQ0kAQ0ICQwCTAcTBgYGAAAAAAEAjQAMA5kDbwBDAAABBgcOAQcGBwYHDgEHBiMiJi8BPgE3PgE3NhYXHgEXLgEnDgEHDgEnLgEnLgEnJjY3PgE3PgEzMhceARcWFx4BFxYGBwOZChcXQSgpLCIoKV0zNDYZMRhHBQkEIj1RLVotDBcKFDgdDCETQkEQHCsOBgcBAQsMC0IlTzYJFCEiaEdIXhUfBRYFBAEKExYWLhYXEw8ODxcHBwMD6QkQBz5HHA8MGwcRCSFbLggYDCoPAwQ1LRUrFR0lCwoyGTUSDQxOSUl5Gk8PRDAHAAQAZgA8A5oDKwAWADIASgBjAAABMhYzHgEVERQGBw4BBw4BBxE+ATc+ARMRMhYXHgEVERQGJy4BIw4BBz4BNz4BNz4BNTEBMhYXHgEXES4BJy4BJy4BNRE0Njc+ATMDFBYXHgEXHgEXLgEjIgYjIiY1ETQ2MxExAvQFCwYRGBkTITgXHjscFTslFi+OChEHBggqNSYaIB9RJxY5FRU0Hycz/aIYLhclOxUcOx4XOCETGRgRBgsFdiwvHi4bGzMWJ04jGx0nLzAcFAMrAQIcFP3XFBwBAQoICygcAmonOBEKC/2kAfUHBwcSCv4PHkcFAwMBAgkTGQUEBQECNCICXAsKETgn/ZYcKAsICgEBHBQCKRQcAQEB/aQfNwIBBgMDHBIJAwZDHQHxFRz+CwAAAAAEAIkAPAOaA00ACgAUACEAQAAAAREUBiMhIiY1ESEBFSEnLgEjIgYHNxQWMzI2NTQmIyIGFQMyFh0BITU0NjsBMhYdATMyFh0BITU0NjsBNTQ2OwEDmjYN/XQNNQMR/dQBRwECX0JCXgNBOSkpOTkpKTlBDRMBBhMOQQ4TQA02/O81DUAUDUICR/44DjU1DgHI/l4HB0JaWkL/KTo6KSg6OigBqRMOISEOExMOITUNQEANNSEOEwAAGQBmADUDmgMJABAAJgAyAD4ASwB9ANEA+QEQAT0BSgGqAdYB8QH2AgUCGAIpAjsCUAJmAnwCkgKoAsEAAAEiJjU0NjMxMhYVFAYxDgEjATIWFRQVFAYVFDEOASMhIiY1ETQ2MwEiBhUUFjMyNjU0JiUiBhUUFjMyNjU0JiciBhUUFjMyNjU0JiMhIyIGBxQWFzEXBw4BFRcUFjMyNjcxNxceATsBMjY3PAEnMSc3NjQvASYiBzEHJy4BIzcmBgcGFhcnJgcOAQcGBw4BFRQWFx4BMzI2MTY3PgE3NhcHDgEXHgEzMjYzMTc4ATE+ATUVMDQ5ATU0JjUXOAExIjAxNTgBMTAmOQIwIjU4ATkBNyYGBw4BFRQWMzI2NTQmJy4BBw4BFQYWFR4BFRQGIyImNTQ2NzYmJzciBhUxFRQWFxQWMzI2NT4BNTE1NCYjBSYiBwYUFzEXBw4BHQEeATMyNjcxNxceATsBMjY1NiYnMSc3PgEvASYiBzEHNyIGFRQWMzI2NTQmIwUjIgYHDgEVFBYzMTMHIgYxFRQWFzEXIyoBDwEOARUUFjMxOwE4ATEyMDUwMjE4ATc4ATkBNzA0OQE4ATM9ATgBMTU4ATE0MDEwJjE1MSc4ATEnNzA2NTY0NTEvAiMnJyMiBgcOARUUFjMeATMxMxUjIgYHDgEVFBYXHgEzMTMyNjUxNTQmJy4BIzE3ISImJy4BPQE0NjcyNjMhMhYdARQGBw4BIzElITUhFSUhIiY1NDYzITIWFRQGIwMqASMnLgE3PgEfAR4BBw4BIzEzMCIxJy4BNz4BHwEeAQcOAQcjIiY1NDY7ATIWFRQGBw4BIyUiJicmNj8BNhYXFhQVDgEPASoBIyMiJicmNj8BNhYXFhQVDgEPATAiOQEXIyImJy4BNTQ2OwEyFhUUBgcOASMxNyImPQE0NjUyNjMyFjMUFh0BFAYjMRcjIiY1NDY7ATIWFR4BFRQGBxQGIzEnIiY9ATQ2NT4BMzIWFxQWHQEUBhUOASMxAREDAwMDAwMBAQMBAmADAwEBOkv+FgN6AwMCUg8WFg8QFhb+xBAWFhAPFha+EBYWEA8WFg8BMgECAgEBAQ4OAQEBAwICAgEODgEBAQICAwECDg4CAQECBQIODgECAkIDBQEBAgIBGCcnXDAwKgEBAQEBAgEBAyswMVsmJxUEAgIBAQMCAQEBGQECAQEBAQEcAgUBAgISDQ0SAgIBBQIBAgEBAQILCAgLAQIBAQMTAwMBAQMBAQMBAQMD/k4BBgECAg4OAQEBAwIBAgEPDgEBAQECBAEBAQ4OAQEBAQIFAg67DxYWDxAWFhABWRkCAgEBAQQDCgIBAQEBAS8BAQEBAQEDAz8BAQEBAQEBAQgIAQEBAQECATIyAgIBAQEBAQECAisrAgIBAQEBAQECAjICBAEBAQIBkvzaAQMBAQEBAQEDAQMmAwQBAQEDAfzgAxr85gL+/R4DAwMDAuIDAwMDvAEBAa4DAgIBBQKvAgIBAQMCMwLiAwICAQUC4gMBAQEDAjcCBAQCNwMEAQEBAwH+YwIDAQECAq8CBQEBAQECrgEBATMCAwEBAQPiAgUBAQEBAuICNzcBAwEBAQQDNwIEAQEBAgGxAwMBAQMBAQMBAQMDHBYDAwMDFgICAQEBAQICHAMDAQEDAQEDAQEBAQMBAYsDAwMDAwMBAwEBAVsDA11FRV0XGAGGLFsBcwMD/pEXERAXFxARFw0XEBEXFxEQFycXEBAXFxAQFwMBAgQBDw8BAwECAgIBAQ8PAQECAgIEAQ8PAgQCAQICDw8BAYMBAgICBgEBAQMDFxcYKAECAQICAQEBASkXFxUDAgECAgUCAgIBDQECAgEBAgEBAQEBAQEaAQEDAwkEDhMTDgQJAwMBAQECAQEDAQIFAwgMDAgDBQICBgF3BAODAQMBAQEBAQEDAYMDBFECAgIFAg8PAQMBAgICAQEPDwEBAgIBBAIPDwEFAQICAg9TFxEQFxcQERcaAQEBAwEDBAICAgEDAQIBAQECAQMEAQEBAQEBAQEBAQEJCAEBAQICAQIBARoBAQEDAQEDAQENAQEBAwEBAwEBAQQDGgEDAQEBHwEBAQMBNwEDAQIEAzcBAwEBAQ4pKQ4EAgMEBAMCBP1cXwEGAgMCAV8BBgICAnsBBgIDAgF7AQYCAgIHBAMCBAQCAgIBAQEHAgICBgFfAQIDAQMBAQIBXwICAgYBewECAwEDAQECAXsHAQEBAgICBAQCAgIBAQFtBAI4AgIBAQEBAgI4AgRtBAMCBAEBAQIBAgIBAQEHBANYAQMBAQEBAQEDAVgCAgEBAQAAAAADAG4AYQOPAwMAEAAsAEAAAAEyNj0BNCYjISIGHwEUFjMhJTIWFzEXHgEzMSEyFhUxERQGIyEiJjUxETQ2MwEiBgcOARUUFjsBMjY1NCYnLgEjA1AICQkI/rINBwg0BwUBGv4nDRMJYQQVCQEZFj1iFv3HGlZaGQEjJTsQJzQ8KtskMS4gCUUvAr0OCQ4IDxoJEwQCRgkITA0KGUL+iEUWGkEB7EAb/wAjHQU2JSg4LiIfLgMpNwAAAgBEAKIDvALEACAAJgAAATIWFw8BPwEeARUUBgcjISInLgEnJjU0Nz4BNzY3PgEzBRcPAT8BAgAzWiOfEa6JOE1mSwj+Hy4oKD0REg8PNiQlKyGDUAEFRrFoFL8CxB4bkqwOlhBbOUZiAxAQOCUmKigjIzYSEgU9SyBSxwxovQAEAFYASwPNAx0AHQA1AGwAiQAAEz4BNz4BMzoBMx4BFxYGBw4BByoBKwEuAScmNjcxExQGBw4BIyImNTQ2NzMyFhceARUUBjkBEzIXHgEXFhUUBw4BBwYjIiYnPgE3PAE1NCYnLgEnLgEnPgEnLgEnLgEnKgEjIgYHNjc+ATc2MxcPAS8BJiIPAQYUFzEnFxYyNz4BJz8BNjQnJiIH3gQJBQgRCQEDAS5DAwMlIAwYDQIDAgQwRQEBKCDvAgEObD5HdWtNBA8eDTlJAaBJQEBgGxwcG2BAQEkaMhcCAgEKCQkZEAwbDx4eAwIYFBUzHAIDAgYNBhAgIVg3NjyHOF02AgoaCgMLCx97Cx8KBgYBK2wLCwsfCgH3AgQBAgMCQS0nQBEFBwECRTAlPg/+hwMEAxUUGhs1SwEEAwxDKwEBAp8cHF9AQUlJQEBgGxwHBwQIAwIDAhIhEA4ZCwgOBRhGJxwyFBMWAgEBNy4uRBMTzEBqNgIICAMLHwseegsLBg8IMXwNIwwNDQAAAAAEAFYASwPNAx0AHQA1AGwAjQAAEz4BNz4BMzoBMx4BFxYGBw4BByoBKwEuAScmNjcxExQGBw4BIyImNTQ2NzMyFhceARUUBjkBEzIXHgEXFhUUBw4BBwYjIiYnPgE3PAE1NCYnLgEnLgEnPgEnLgEnLgEnKgEjIgYHNjc+ATc2MxcHJyYiBwYUHwEHBhQXFjI/ARcWMjc2NC8BNzY0JyYiB94ECQUIEQkBAwEuQwMDJSAMGA0CAwIEMEUBASgg7wIBDmw+R3VrTQQPHg05SQGgSUBAYBscHBtgQEBJGjIXAgIBCgkJGRAMGw8eHgMCGBQVMxwCAwIGDQYQICFYNzY8Qjg4Ch8LCws4OAsLCx8LNzgLHwsLCzg4CwsLHwsB9wIEAQIDAkEtJ0ARBQcBAkUwJT4P/ocDBAMVFBobNUsBBAMMQysBAQKfHBxfQEFJSUBAYBscBwcECAMCAwISIRAOGQsIDgUYRiccMhQTFgIBATcuLkQTE8o4OAsLCx8LODgKHwsLCzg4CwsLHwo4OQofCwsLAAIAqwAaA1UDTQAZADMAAAEeARUUBiMiJicmNjc1JREUFjMhMjY1EQUVJTkBBTUuATc+ATMyFhcWBgcVJTQmIyEiBhUCDQoNFQ8MFAMCCwv+ulA4AZo4UP64/p4BRgsMAwMTDQ0TAwIMDAFISTX+UjVJAgUEEgsOFRAMDBYFQ1v+ADhQUDgCAFpDyk8lBRUMDA8PDA0VBSRPNEpKNAAAAAACALMAMwNOAzcADgBNAAAlISIGFRQWMyEyNjU0JiM3LgErASImNTQ2Nz4BNzAyMT4BJzYmJy4BBzgBFTE1OAExDgEHBhYXHgEVFAYrASIGFRQWFx4BMyEyNjU0JicDBf35EBUVEAIHDxYWDy4NIhJvFyENDAUJBAEgJAEBJSEhUio3TQkKLi8MDSEXcSU2Dg4NIxMB4CY2Dg2BFxAQFxcQEBfMDg8iGA8aCAQHAx1OLCtQHBwUCgEBDVY6OmsgCBoPGCI4KBQkDg4POSgTJA4AAgBvAB0DlQNNAA8AKAAAATIWFREUBiMhIiY1ETQ2MwEiBhUUBiMiJjU0JiMiBhUUFjMyNjU0JiMDDThQUDj96jhQUDgBlg0SOikpOhMNDRJfQ0NfEw0DTVA5/eI5UFA5Ah45UP7zEw0pOzspDRMTDURgYEQNEwAABACCADUDfgMxAAoAFQAgACwAACUUBiMiJjU0NjsBFxQWMzI2NTQmKwEnNCYjIgYVFBY7ATcxNDYzMhYVFAYrAQHnaUpKaGhKszJpSkpoaEqzMmlKSmhoSrMyaUpKaGhKs+dKaGhKSmmzSmhoSkpp5UpoaEpKaLJKaGhKSmgAAAAEAKv/9wOrA28AVQBzAQABDgAAAR4BFTEVFAYjIiY1MRUjJiIjIgYHDgEHLgEnLgEnLgEjIgYHDgEXHgEfAR4BFx4BFw4BBw4BFx4BFx4BFx4BFx4BFxU0NjMyNhUUBgchLgE1MRE0NjcFISIGFREUFjsBMjY3NCYrAREhFRQWMzI2PQE0JiMDMhYXHgEXHgEXHgEXHgEzMjY3NjQnLgEnJjY3PgEzMhYXHgEXHgEzMjY3PgEnLgEnLgE3PgEzMhYXHgEXHgEzOgE3PgEnMDQxLgE3PgEzMhYXHgEVFgYHDgEHDgEHDgEjIiYnLgEvAi4BNz4BMzIWFx4BFzIWMzI2Nz4BJy4BJy4BJy4BJyY2Nz4BMwMyNjU0JisBIgYVFBYzAo8XVA8KCg8BAgUBCRIJAgYCAgQCBAkEDx4JCRMIFwwLAwYCAQwZDQEDAQQJBBIGAQIPFgMGAwQIAxcyHA4LCg9XFv6LFldUFwFn/qsTGxsTuAsOAQ8LtAFMDwoLDxsTOwQHAwkQCQsVCwIEAgMEAwIFAgUDBAgEBQUKAwUCBAgDBAkGAgUDAgUCBQECAgMCBAIGAwgEAgYDBQgDAgcEAgMCBwMDAgQIAgUCBQkDKTEBBAUNGg0MGQ0DBgMCBQMsVSkKFAsCCAUKBQMGAwMGAwIEAQMFAgQBAwMGAxQqFAIEAgMCBwIFAjwKDw8KaAoPDwoDbwEiT8YKDw8KVgEFBQIDAgMFAgYLBhIKBgYPMRkFCQMBEiMRAgQCAwYEESIJCR8LAgICAgMCDxsOVAoPGwtIHQEBIFICk08iAZQbE/3aExsPCgsOAh58Cw8PC4ATG/8ABQQKFwsNHA4DBAIBAgICBQkFBgoGCQ0GAgEFBgYNBAICAQIECgUDBQIFCwUDBAICBAsGBQUBBAsHAQYMBAEBBAUua0AGBwMKEwoJEgoCAgEBFCsaBQoFEAgFAwEBAQMBAQICBAoFBAkEHTsdAgYDBw0FAgEBNA4LCg8PCgsOAAAFAHEAMgOXAxwAJwA8AEAAmgCnAAABMhYVMREUBiMxIxUUFx4BFxYjMSEiNj8BPgE1MTUjIiY1MRE0NjMxBSEiBhUxERQWMzEhMjY1MRE0JiMxFxEhEQUjIgYVMRUOAQcxJyYGBzEHBhYXMRcGFBcxBw4BFzEXHgE3MTceARcxFRQWMzEzMjY1MTU+ATcxFxY2NzE3NiYnMSc2NCcxNz4BJzEnLgEHMQcuAScxNTQmIwcyFhUUBiMiJjU0NjMDQhVAQBWnKytkJiYK/NoKTDIQLkqnFUBAFQJt/accKCgcAlkcKCgcH/1pAVwoCAwKEggWBxAEFAQEBxUCAhUHBAQUBBAHFggSCgwIKAgLChMIFgcPBBQEBAcVAgIVBwQEFAQPBxYIEwoLCBQYIyMYGSMjGQMcHzX+IzgcMwoJCQ4EBAgHAgcRCTMeNgHdNCAlKBz+ThwoKBwBshwoJf4QAfBVDAgaAwoHDQQEByMHEAQMChYLDAQQByIHBQUMBgsDGggLCwgaAwsGDAUFByIHEAQMCxYKDAQQByMHBAQNBwoDGggMYSIYGSIiGRgiAAYAsgAmA5MDKQAaACgANgBEAFEAZQAAATIWFxE0JiMhIgYVERQWMyEuATU0Nz4BNzYzJTQ2MyEyFhUUBiMhIiYVNDY7ATIWFRQGKwEiJhcjIiY1NDY7ATIWFRQGJSIGFRQWMzI2NTQmIxcUBisBIiY9ATQ2MzIWHQEzMhYVAtwVJhIvKP43LSovKAEcGRsSE0ArKzD+LRQOAX4OFBQO/oIOFBQO3w8TFA7fDhSriQ4UFA6JDhQUARpLa2tLTGtrTHAMCGIIDAwICQxNCAwByAYHARcWQUEW/c4WQR5MKjArK0ATErMOFBQODhQUmQ4UFA4OFBS7FA4OFBQODxOIa0tMa2tMS2vGCQwMCYUJDAwJcQwIAAcAZgAaA5oDTQAcACwAPABMAFsAdgCQAAABERQGJyYnLgEnJicuAT0BNDY3Njc+ATc2NzYWFQMjIgYVFBYzMTMyNjU0JiM1IyIGFRQWMzEzMjY1NCYjNSMiBhUUFjMxMzI2NTQmIwUjIiY1NDY7ATIWFRQGIwMuATU0Nj8BPgEzMhYXHgEVFAYPAQ4BIyImJxMiJi8BLgE1NDY3PgEzMhYfAR4BFRQGBw4BAj0OIAsdHV5AQVIFLi4FTT4+XSAgEBcXo4kOFBQOiQ4UFA6JDhQUDokOFBQOiQ4UFA6JDhQUDgG4ex4qKh57HioqHooKCwsKVwobDg4aCwoLCwpXChsODhsKig4bClcKCwsKChsODhsKVwoLCwoLGgMF/VoJOAoDDQwqHh4nAkczzTNDAiMcHCsODgcKPgL+SBQODhQUDg4UiBQODhQUDg4UiRQODhQUDg4U8iodHioqHh0qARAKGg4PGgpXCgsLCgobDg4aC1YLCgoL/Z4KC1YLGg4PGgoKCwsKVwoaDw4aCgsKAAAEAEsAMwOEA20ALwA7AEcAUwAAATIXHgEXFhUUBw4BBwYHDgEjIicuAScmJy4BNz4BNz4BNyYnLgEnJjU0Nz4BNzYzAyIGFRQWMzI2NTQmMyIGFRQWMzI2NTQmMyIGFRQWMzI2NTQmAexUSktvICAbG2RGR1cRIBEvLi9TIiIWBwUBAgsGIB0LGRkaKg0NISFyTEtWzBcgIBcWICCxFiAgFhcgIMMWICAWFyAgA20gIW9LS1VUSEdrIiEKAQMGBhUNDg8EDgYICwIICQoSGxtEKSgtVEtKcSIh/qwgFxYgIBYXICAXFiAgFhcgIBcWICAWFyAAAwChABMDawNTABgATwBcAAABMhYXMRceARUxERQGIzEhIiY1MRE0NjMxEyMiBgcGFBcxFx4BMzI2NzE3MwcnBxceATMxMzI2NzE3NjQnLgEjMSMiBgcxByczFzcnLgEjMRMVFBY7AS4BLwEuAScCfxY4EG0QEVs2/lg2W1s2jY0GCwMCA44DCgYGCgOIWVsrGjUDCgUBBgoDfgMDAwoGjAYKA3tmWTMaLgMKBsApHX4EDwttDB0QA1MREG0PKRf+KzZYWDYCJDdX/tIGBQUNBecFBgYE3qdBKk4FBQYF6AUMBQUGBQXJp0wqRAUFAQd6HikPGwttDBADAAAAAQCJABoDmgMrADMAAAEjNTQmIyEiBhURFBY7ATUjESEVIyIGHQEzNTQ2HwEWFA8BBiY9ASMVFBYzITI2NRE0JiMDQq4zJP6jJDMzJFdXAV2vJDPaNxNyDQ1yEzfaMyQBtCQ0NCQCfFckNDQk/qQkNFgBXFczJK5FHhQTcg0jDXEUFR5FriQzMyQBtCQzAAAABADEACkDPAM+AA4AEgAWABkAAAEhIgYVAxQWMyEyNjURJxMhNSE1ITUhJzUXAk/+5lQcATBBAZY7Nu1P/sQBPP7EATx32QM+USD9zSFQUCEBt+39iU5PT3fZ2QAHAG8AGwNsA0sAFgAuAFYAhACYAKwAwAAAATIWFx4BHQEUBiMhIiY9ATQ2Nz4BOwEDMhYXHgEVFAYHDgEjIiYnLgE1NDY3PgEFMhYXHgEVFAYHDgEHHgEdARQGKwEiJj0BNDY3LgEnLgE1NDY3PgEzAzIWFx4BFx4BFRQGBw4BIyImJw4BBw4BIyoBJy4BJyY2Nz4BNy4BNTQ2Nz4BMxcmBgcxBwYWFx4BMzI2NzE3NiYnIyYGBzEHBhYXHgEzMjY3MTc2JicjJgYHMQcGFhceATMyNjcxNzYmJwF7Kk0eHjMZEf5iEhgzHh5MKicTHDMTExUVExMzHBwzFBMUFBMUMwFXEBwLCgsLCgkXDTVKEw7QDRNKNQ0XCQoLCwoLHA8mEiMRNzoBATYlJCFWLxIiEQMSBxASBgECAQUGAgQEAgEBASwvJiMiVi9TBAwDDQMCBQIEAgMGAg0DAgVeBQwDDAQCBQIEAgMHAgwEAgVfBQsDDQMCBAIEAwMGAg0DAgUBLiAeHU0qFhIZGRIWKk0dHiABLhYVEzQcHDMUFBYWFBQzHBw0ExUWZQ4NDB8RER8MCw0CA0szCQwSEQ0JM0sDAg0LDB8RER8MDQ4BVAMDDR8BAUAWHTQUEhQDAwEHAwcHAQEFBAcQCAEEAhM5IR0zFBIUdAMCBRIEDAMBAgMDEgUMAwMCBRIEDAMBAgMDEgUMAwMCBRIEDAMBAgMDEgUMAwAAAAMAkwAoA3gDQQADABoAKAAAEzMTIwMhMDY3NhYVETAGJy4BMSEwJj0BMjYzBRUwFjcwNjU2JicwJgfgnWeOkAFJeCYlLSpDPkb+xkEBCSkCZxsVGwILEh4SAWP+xQK5WAgJGw/+JTIhJTsDPNI8bp8GFSMdECEQFAUAAAACAK4AIQNpAzQAHAA1AAABMhYVERQGIyEiJj0BMzUjNTM1IzUzNSM1NDYzIQUmBgcGFhceATc+AScmBgcGJicuATc+AScC2DxVVTz+aD1VOjo6Ojo6VT0BmP7TIi8SFS5NTosUEyInKiQTDTsdHSoNExAhAzRWPP4RPFZWPHQ7Ojo6O1c8VtgqIxMVik1PLRUTLiAhEBMNKh0dOw0TJCoABAAiAIAD3gLmAB4AIwAnACsAACUjNjQ1ETQmIyEiBhURFBYVIyIGFRQWMyEyNjU0JiMBMxEjEQMjNTMFIzUzA7k5AVk+/iw9WgE4EBYWEANwEBYWD/4lZmZEZ2cBM2dnywEDAQGAVz8/V/6AAQMBFhAPFhYPEBYBk/7NATP+zczM7wAACQCB//oDkgNNAEMASABmAIAAkQCWAKkAuwDAAAABLgEjKgEjDgEHBhQVFgYHDgEHIgYjOAExIiYnJjY3PgE3MjY3PgEnLgEjKgEjDgEHDgEXFhceARcWMzoBMz4BNz4BJyU3FwcnNw4BBw4BBw4BBw4BFx4BFxY2Nz4BPwE+AScuAScxNw4BBw4BBxQGMQYWFxY2Nz4BNzYmJy4BJzE3PgE3NhYXHgEXHgEHDgEHJzcHFzcnAw4BIw4BBwYmJy4BNzA0PwIXJwcOAQcGFhceARcyFjc+ATcnJTcnBxcDKAEfJgQFBA4ZBw8GFRgZRSsFCwZJcAwIEhgYRScCIg8HCwMEKR8EBAJJfigpGBIPICFZNjY6AgYCS4YvMCkL/rnta+5qwQEFARgvGAkRCAMEAwIHBAIGAhIkEjkCBAIDCAUwAgQBAwYEAQEIBAMGAgMFAwMBAwMFAy0KFQsIFwkPHg8JAwcKFAtqPBRBFEH9AgEBHjweBQkFBAECASIFamEIAgMCAQECBgsFAQMBCxgNPgFnG2ocawGGEjIBDgsTJgIpTiEhJwQCX0cpTiEhKgYIEwkeFhsfC04+P5FKODEwRRQTAUI5OY5JbP1l/GScAgICGTIZCRMJAgYDBAYCAQQBEycTPAMGAwUGAjEBAQIDBwMBAgcGAwICAgMFAwQHAwMEAmMLFgoIAggOHA4JFwkMFQxlFhU9FT3+SAEBCxcMAgEEAwoFAgFtD2RBGAULBgMDAgULBQIBBAkFOtUdZB1kAAAAABAAhAANA4QDWgANADEAPwBDAEgATABQAFUAWQBdAGIAZgBqAG8AcwB3AAABNDYzMhYdARQGIyImNSUVFBYzMjY9ATMVFBYzMjY9ATMyFhURNRQGIyEiJjURNDY7ATcyFh0BFAYjIiY9ATQ2AyERIQUzFSM1FTMVIxUzFSMDMxUjNRUzFSMVMxUjAzMVIzUVMxUjFTMVIwMzFSM1FTMVIxUzFSMCeiIZGCIiGBki/oA0JSU0sTQlJDQZGUREGf26GEVCGRtYGCIiGBgiInsCiv12AfdYWFhYWFiUWFhYWFhYlFlZWVlZWZRZWVlZWVkDIBgiIhg8GCEhGB4dJDMzJB0dJDMzJB0XRf2ULxhEF0UCPUMZWCIYPBghIRg8GCL87QHvOldXklc6WAF7V1eSVzpYAXtXV5JXOlgBe1dXklc6WAAAAAgAbAAGA60DRwA9AFwAeQCQAK4AzAFIAWcAAAEyFx4BFxYVFAcOAQcGBzEXMAYXFjYXFgcOASMGKwEiIy4BJyY3NhY3NiY5ATcmJy4BJyYnLgE1NDY3PgEzAw4BBwYWFx4BFxY2Nz4BJy4BJyYGBw4BIy4BJy4BBwEjIgYVFBY7ATIWFx4BHQEUFjMyNj0BNCYnLgEjASMiJj0BNCYjIgYdARQWOwEyNjU0JiMTIyIGBw4BHQEUFjMyNj0BNDY3PgE7ATI2NTQmIzEBIgYdARQGBw4BKwEiBhUUFjsBMjY3PgE9ATQmIzEHLgEjIiYnLgEnLgEnJjY3PgE1NjQ1Njc+ATc2NTQnLgEnJiMiBgcOARUUFhcWFx4BFxYXMBQXFBYXHgEHDgEHDgEHDgEjIgYHDgEXHgE3PgEzMjY3PgE3PgE3NjQnHgEzMjY3BhQXHgEXHgEXHgEzMhYXFjY3PgEnLgEnAS4BNTQ2Nz4BMzIXHgEXFhUUBw4BBwYjIicuAScmJwI2LyoqPhISCwwoHBwhAxExMWcmCRQTSzQ0OS88ODhTFhYKJWgwMREDHRoZJw0NBQECDw4da0BiBAYCAQIDECkXFysRAwIBAQUFBAgDDB8REB8LAwgEAYdeBwkJB14KEwcHCAkHBgkMDAsfEP3deRUeCQcGCjEieQcJCQcGfxAfCwwNCgYHCQgHBxMKfwYJCQYCYAcJCAcHEwpbBgoKBlsQHgwMDAkGVA0cHBkcDg8aCw0PAwIBAwECAR4bGicKCxMTRC0tM0R0IQ8QAgEGDQ4nGRkcAQIBAwECAw8NDBoODhwZHBwOBgcCAQsHCxkaGh4PEh8OExcEAwEOHA8RIhACAgUXEw4fERAeGhkaCwQIAwMCAQEHBP3tAQENDhxlPC0nJzsRERMTPScmKCQjJDoVFQcDBxISPykqMCUrK1IjIxUFUxkZCAkCAgICAQECAgICCQgZGVMEEh4dRyYmJAoSCR44GTVC/ngBBgQECAMREwEBERADCAQEBgEBAgMLDAEODAMDAQHICQcGCgcIBxILcQcJCQdxER4MCw383h4VYwYKCgZjIjAJBwYJAyINCwweEXkGCQkGeQsSCAcHCgYHCf2HCgZmChMHBwgJBgcJDAwMHhBmBgpUAwICAgIIBgcTCwoWCwQFAgIGAhgjIlErLCk0LS5DFBRGOxw9IAkUCygoKEYdHhQCAQIFBAsWCgsTBwYIAgICAgMCCwYGBwEDAgICAwkHCh4SCxULBgYJCAwYDBIeCgcJAwICAgMBAwMDCAQEBgEBiAkSCR01GDM9ERE7KCcuNDc3Wx0dGBhNMTIzAAAHANv/3gMmA4YAHwAqADQAPgBIAJEAvwAAATAXHgEXFhcwNjcwFx4BBwYHBicuAScmNTQ3PgE3NicDDgEHHgEXPgE/ATcHHgEXNy4BLwE3Bx4BFzcuAS8BDwEeARc3LgEvATUjHAEdASMVMw4BBy8BBx4BFw4BBx4BFz4BNx4BHwE3LgEnPgE1NzMGFhceATMyNjc+ATcuAScOAQcOASMiJicuATc1IzQ2PQEHIxUjFTMVDwEXPgE/ARUUBiMiJiceARUyFjMyNj8BNT4BPwEmNDUPATUzNSM1AeclJVkmJgEtASEhCDk5tCo6OWgkJTU1cyYmHTQGDgkFCQQHDAUFKhIGDAUUBQoEBoIQCRAHEgUMBQxAEQcLBRQECQQJFSEhAQICDAwLBw8HBxkSBAgDEhoJBQkFCw0JEgoDAwEpAQICBBALCQ0CAgMBBQoFAQIBAQMDAwUCAgIBPQFgFCYmFRQECA4GCQcHBgsHAQIHDAYODgEBCw0DAgEODh8fA4YsLI1ZWVlIUU9Py2JhJQgTE085OERsXFyeREU7/V8NGw8CBQIMFgsLBQgLGQ0IChMJCwkKDhsNCwkSCREIBwwYDQgJEAgPxAcOCAsTChMJBwgPBQoFExwJBQgECx4UBAcECBMGDAYJFAsKICwMExIMDAcTCwIDAgsSBgUEBwgNLyMKCREHBwEnEigGBRUCBQICHgcHAQEGCwQBDg0FJwMEAQEECgYFBCISJwAABgBmAEYD1AMrACMAMwA9AE0AYwBwAAABMhYdAS4BLwEiJisCDgEPAQ4BBzEHHgEXMSEiJicRPgEzMRMjIgYVFBYzMTMyNjU0JiM3HgEfATc+ATMnJyEiBhUUFjMxITI2NTQmIwEnNiYnJgYHDgEXHgE3FxYyNzY0JwclJjQ3NjIXHgEHBiInAtxbHgYvHBMCBQIDA0lcFAQVIgsCAggH/u5XIgEBIlfgsw0REQ2zDBISDNUHKRkHCQoTCoBR/qsOFBQOAVUOFBQOAY9FLRU7PJs4NgcyMptARQodCgoKAf67KCgocSgoASgpcSgDK2cTkgQHBAIBBRkUAxlNNToRIBBgFgFZFmD+qhQODhQUDg4UZwIFAwEBAQEIRBQODhQUDg4U/fZGP5syMwc3N5w8OxUtRQoKCh0KAVQocSgoKChxKCgoAAAAAAcAZgBiA5oDKwAgAZ8BrQG9Ac4B3AHqAAABMhYdATMyFh0BMREUBiMhIiY1ETgBMTU0NjsBNTQ2PwEXMCIxIzAGKwIiBiM4ATEqATEjFCIjMQYwIzgBMSIGMSMHMCIxFCIjMBQxMCIVIw8BMCIxFCIxMAYxMCIVMCIVOAEjFAYxIhQxDwEVMAYxMAYxFQ8BDgEVFBYXDgEHMAYHMAYHOAExFAYVBhQjMBQxBzAUMSIUMQcUMDEHOAExHAEjOAExFTAGMR0BOAExFAYxMBQxMBQVBzAUMTAUFRwBFRQWMzAyMTIwMTMwMjEyMDE6ATUwMjEzMjYxOAEzMDIxNDIxMDIzOAExHgEzMjY3OAExMjAzMBQzMDIxMDIxFzM6ATEwMjEXMzAyMTsBOgEzMjY1JzA0MScwNCMnNDAxLgE1MCIxNCYxOAExLgEnPgE1NCYnNTgBJzA0MTgBMS4BNTgBMTAmOQE1LgEnMDQjOAE1IzQiNSM0IjEnOAEnIzQwIzUjOAEnOAEjNSoBNTAiMTAiNTAiMTAmMSMwJisCMDQjMCIjOAExIiYjMCIxIjAxJzAiMSMiMDEqASMTHgEXDgEjIiYnPgE3MyceATMyNjcXDgEHIy4BJzcnFBYXBy4BIyoBBy4BNTQ2NxceARUUBgcmIgcnPgE3NyEiBhUUFjMhMjY1NCYC4RokBxBkYBP9sxBkYBMIIBgG7QIBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBRl4EBAEBAQEBAQECAQEBAQEBAQEBAR4VAgECAQEBAQECAQEBAQEBARxKKipKHAEBAQEBAQEBAQEBAgEBAQEBARUeBwEBAQEBAQECAgEDBFVCAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQECAQEBAQMBcAIEAhg/JCRAGAMEAuaEBQwGBQsEawgLAeIBCAdwHAYFcgQHBAIEAgMDSzllNUQDAwQJBG4FBgFu/qIMEhIMAV4MEhIDKyMZTxxVVP75VxsZWQEHVFMeTxciAgHNAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEOb0oOHA4BAQEBAQEBAQEBAQEBAQEBAQECAQECAQEBAQEBAQECAQEBAQEBFR0BAQEbISEbAQEBHRUZAQEBAQEBAQEBAQICAQ4cDkZtEQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB/q4EBwMXGxsXAwcE8gMCAgK3BhILCRAGvCoHDwa+AQEBCxcMPl4PAxFcOwwXCwEBvAUOB7cSDAwSEgwMEgAEAFUAcwOrAvMAEAAaACwANgAACQE+ATMhMhYXAQ4BIyImJzEnAS4BNRE0NjcBAQ4BIyEiJicBFx4BMzI2PwEBExEUBgcJAR4BFQHk/pwJFgsCoQ4cC/6jBhEJCRAGZP7xDQ8IBwEcAgAIEQr9TgYKBgEQJw0iEhIiDSYBDCsKCv7+AQ0EBQGVAVQFBQgJ/rMGBgYGF/7yDCETAbsNGAv+4/7OAwQCAQETJw4ODg4n/vECJ/4jEB8MARkBJwkVCgAFAJQAGgNsA00AFQAZADYAOgA+AAABMhYXMRMVFAYjMSEiJjUxNRM+ATMxASEVIQEiBgcuASMiBhUOARUUFjsBMjY1NCYnPgE1NCYjExUjNSMVIzUC0Sg6BDU8K/32KzwsAzsoAgj9lQJr/u0aMhAFCwUaHiYoOCvpIC0lGgUCQCrlMyQzA001KP3dTSo8PCpIAicnN/1mZgJDGhQFARoUCjYeKDUrHhknCgoSBSg1/g8zMzMzAAAAAwB8ABoDcwNNAKoA0ADoAAAlNDY3PgE3PgE3PgE3PgE/AT4BNz4BNz4BNz4BNzwBNS4BJy4BJy4BJy4BJy4BJy4BJy4BJy4BJy4BIyIGBw4BBw4BBw4BBw4BBw4BBw4BFQ4BBw4BFRQWFx4BFx4BFx4BFx4BFx4BFx4BFx4BFx4BFxUUBgcOAQcOAQcOAQcOAQ8BDgEHDgEHDgEHDgEXFBYXHgEXHgEXHgEXHgEXHgEXFjI7AS4BJy4BNTElLgEnLgEHDgEHDgEHBhQXHgEXHgEXHgEzMjY3PgE3PgE3NjQnMQciJi8BLgE9ATQ2MzIWHQEXFhQHDgEjMQGQBgYGEgsMGhAPIhMBAgIDAQEBBAcCBAUCAgMBAQMBAgQCAQECAgYFBA4JBQsGCBAJCRQKChQLCBEJCRIJCRIICRAHBwsEAwYBAgEDBQIBAwECAQQDAwQDAgYCAgQBAgQDAQYDBgsFBQYBAgICBgQFDAYHDggIDwkhBw4HBgwEBQYBBAICBgYFCgYIFQwMGw4PHg4PHQ4OGQoUCQ4EBQUB4wkYEDKJQBQlEA8ZCRISCRkPECUUFS0XFy0VFSQQEBgJEhKUBwwFRAUFFA4OFDoKCgUMB/8VKRQTJBAQHQwNFQcDBgUKAwQDAQQDAwcEBg0GBQoFBAYEAgUCDh4ODhsNDRgKBgoFBQkDBAYDAgMCAQEFBAQKBgcQCQsYDQwZDQ4bDgMIBAQJBQYLBQUKBAMFAwEDAQUMBQUJBAQIAgYKBQUOCBQFCQQFCQQGCQQEBwIDBQMJAwcFBAoGBw4IEycTChMHAwMBAgQBAgQBAgMBAQIBAQ8hERElElcUJRAxGxwIGQ8QJRQqXCsUJQ8QGQgJCQkJCBkPECUUKlwrvQUFRQUNB4kOFBQOezsKHAsEBgAADABYADUDpgM8AEsAcgCOAJkAtQDAAOcBCwEXAT4BXAFpAAABLgE3PgEnLgEnLgEHDgEHDgEHLgEnLgEnLgEHDgEXFSImIyIGBw4BBw4BFx4BFx4BMzIzOgEzMjMyNjM+ATc+ATc+ATc1NCYnLgEnBTceATMyNjU0JicuATU0NjMyFhcHLgEjIgYVFBYXHgEVFAYjIiYnFyMnIw4BIyImNTQ2NzQmIyIGByc+ATMyFh0BMScUFjMyNjc1DgEVFyMnIw4BIyImNTQ2Ny4BIyIGByc+ATMyFh0BMScUFjMyNjc1DgEVFzceATMyNjU0JicuATU0NjMyFhcHLgEjIgYVFBYXHgEVDgEjIiYnATIzOgEzMjMyNjUmFDc2JgcqASMiIyoBIyIjIgYVBjAXFBYzNzIWBxYGIyImNzQ2Ew4BBwYWFx4BFx4BFyMqASMiJjU8ATU0NjMhOgEzDgEHDgEHDgEHNzoBMzI2NSYWNTQmIyIjKgEjIiMiBh0BFBYzOgEzJxYGIyImNzQ2Mx4BBwNbAwEBAgECAxQRGjkhDx0OCxMJAQMBAQMCFjAYFRYBBQkFEiIQCxEGCwQDBBsYCBAKNjc2bTc2NwEEAgwVCg0QAwEBAQIBBSUe/jkQCA4HBwYOCQoTGBQIGQgQBgwGBgYNCQsUGRcLGQjWHgMBBxAKEBMfIgkJBw4JDQwZDhEePQkIBwsGFxK3HgIBBxEKEBMfIwEICQcPCQ0MGQ4RHkcJCAgKBhcSUhAHDwcHBg4JChQZFA0UCBAGDAYGBggOCxQBGBcLGQj9vkI4OW44OUI6FQEBARQ2GhIbNjIyYzIyNzwUAQENSAYRGQEBGBEPGwEYuxcgBgMCAgMKCAMJBAcvLy8qJxE1ASUBAQIOEAUBAwMUJBE2RWdFMRoBARU6Qzk5bzk5Q0cJEjhFaEW4ARkQDxsBGRAPGgEBBAECAwkSCRMfCxISBAEKCAcSCgECAQECAQ4DCgkhFg0CCAkGEAsTKhYaKw4ECAEDCgcLHBADBgMcAwYDHyYKgxQFBgQEBQYDBA4NDxIIBRQEBQQEBAUDBA4ODxMIBw0LBgcTDRESBAYHBQQWBggYGTwdBgUFBRQCCwYdCwYHEw0REgQGBwUEFgYIGBk8HQYFBQUUAgsGEBQFBgQEBQYDBA4NDxIIBRQEBQQEBgIEBA4ODxMIBwISNQ8XERcPOgFFEBUVBiR5FxIOGhkRDhj+IRMvHg8eDhEfDwgNCD0PREJEDjMKGw8DBAEGEQ2JNA0YDRgCRTwRGxIxUBEXGBIPGAEYEAAABQCCADUDfgMxAAsAFgAhACwATwAAASIGFRQWMzI2NTQmATU0NjsBMhYdASMhIzU0NjsBMhYdATM1NDY7ATIWHQEjExQGIyE1Nz4BJyYGDwEuASsBIgYdASMiJjURNDYzITIWFREBgR4oKhwbKif+4zMgLCQw0wHo1DEjLCMxQTMhLCMw09NUHP72ewkBBgULBnwNLhgzKT0PHlMaVwIYVBwCriscHygrHBwr/YdeJDIyJF5eJDIyJF5eJDIyJF4BJCcNh6UIDAYFBAl/Fhs/KoccVgEpFltbFv6ZAAMAXAAjA6QDQgASACUANwAAARcWFAcFBiInJSY0PwEXFjI/ATcXFhQHBQYiJyUmND8BFxYyPwEDBRYUBwUGIicxJSY0NyU2MhcDJX8YGP6SFUEW/pIYGH/tGEEX7QN8GBj+kxdBFv6TGBh88RdBFvHxAW0YGP6TF0EW/pMYGAFtF0EWASs4CxwJoAkJoAocCjhoCwtozDYLHAmgCwugChwKNmkKCmkBS6kLHgqpCgqpCx4KqQsLAAAJAG0AGwOZA0wAAwAHAAsAEAAVABoATgBqAH4AAAEzFSMVMxUjFTMVIxMzFSM1FTMVIzUVMxUjNRMhFS4BJyYnJjY3Njc2Nz4BFxYfAR4BFRYGDwEOARUUFh8BFjI/AT4BFzIWFxQWFRYGBzEFHgEXMhYVFgYPAQ4BIyImLwEmND8BPgEzHgEXAxY2Nz4BNTQmJyYGBw4BFRQWFzECWy0tLS0tLVvj4+Pj4+OK/vAnRh0oExQCFRUpHyQlTicoJQUCAQEBAW8CAgICdAQNBG4BAwEBAgECGxgz/gcZQSUBAQEBAdIDCAQEBwNrBgbSAQIBAQIBkQkbCQUFBQUJGwkEBQQFAV0uXC5cLgFCLi6KLi6KLi4BQWMHJhwoMzNqNDMpHxMUEAIDDgIBAgEBAwFqAwUDAwYCdAQEbgEBAQIBAgMBRZc8CCVAGgICAQIB0gMDAwNrBhEG0gEBAQEB/v4KAQkEDAcGDAQKAQkEDAcGDAQAAAAFAMcAJwNzA0wACAAvADgAQABIAAABMBYVERQGMREFMzAWBzAGByMVMzAWBzAGJwcVMzAWBzAGByMVMBYzIREhMAYXFTEXMBYHIzAmNzMVMBYHIzAmNxcwFgcjMCY3Ax1WVv3hZEwJHDdUZEgEHjZPYEUCFTNgCxYB1f4sIwFURESLMDCLRESLMDCLRESLMDADTBlJ/blVJwMlTSkwSwVHKS1RAwFJJC5QBTEiAyUMETAuMiQ1IeoyJDUh7TIlNiEAAAAAAQBx//oDlwNtAGQAADcyNjU0Jic3HgEzMjY3Fw4BFRQWMzI2NTQmIyoBByc+AT8BHgEzMjY1NCYjIgYPAS4BJzc+ATU0JiMiBhUUFhcHDgEHJz4BNTQmIyIGFRQWMzI2NxcOARUUFhcHLgEjIgYVFBYzvyAuAgFxEiwYDBgLQgYHLiAgLi4gAgUCQg4UBaUKJRcgLS0gGCcJpQkrHiAjMDoqKTscFyATJRBXAgIuICAuLiAHDwZYCQoIB3EHEQggLi4gLy8gBQoFYA4PBARrCRYLIC4uICEuAWsOJhUEExcuICEuGhUFIDMNtwY4Jio7OyodLg23AQwLUwYMBiEuLiEhLgMDUxAlFBIhDmADBC4hIC8ABgBmABoDvANNABAAFAAZACUAMQA9AAABMhYVERQGIyEiJjURNDYzIRMhESERIRUhNSUiBhUUFjMyNjU0JiMiBhUUFjMyNjU0JjMiBhUUFjMyNjU0JgMzOVBQOf28OVBQOQJEPP1EArz9RAK8/b8LERELDBERcgwREQwMEBDIDBAQDAwREQNNUDn93jhQUDgCIjlQ/iT+8gIl+/t8EAwLEBALDBAQDAsQEAsMEBAMCxAQCwwQAAAAAAYAfAAhA4gDKwAiADMARABQAFwAbgAAASEiBhURFBY7ARUjIgYVFBYzITI2NTQmKwE1MzI2NRE0JiMTISImNRE0NjMhMhYVERQGIwMyFhURFAYjISImNRE0NjMhBSIGFRQWMzI2NTQmMyIGFRQWMzI2NTQmBQ4BFRQWFxYyNz4BNTQmJyYiAv/95CRDQyT0ghEbGxEBWhEbGxGC0iNmZiMP/eccKCgcAhkdKCgdIRwoKBz+KhwoKBwB1v6+EhkZEhIaGnASGRkSEhoa/tUKDAwKChcKCgwMCgoXAys4L/5xOi1WGxARGxsREBtWKD8Bjzcw/dwoHAFmHSgoHf6aHCgBzSgc/tkcKCgcASccKCsZEhIZGRISGRkSEhkZEhIZBgUUDAwUBQYGBRQMDBQFBgABAG0ARAOgAxEANAAANyImPQE0Nj8BJy4BPQE0NjMyFh0BFAYPAR8BNycuAT0BNDYzMhYdARQGDwEXHgEdARQGIyGZFBgaB3YmAgNINTRIAwImQ0Y9KAMCSDQ0SAICJXAHGhYW/SVESAvtDxgEQTYDCARiM0dHM2IECAM5LAEsOgMIBGIzR0czYgQIAzZCAxkO7QtIAAAAAAwARv/vA88DdwAcADUAOgBqAIMAmwC0ANkA8gELAT4BUAAAATIXHgEXFhUUBw4BBwYjIicuAScmNTQ3PgE3NjMTIgYHDgEVFBYXHgEzMjY3PgE1NCYnLgEjNSMHMzcFIgYHDgEVFBYXHgEzMjY3PgE1NCYnLgEjIgYHPgE3PgEzMhYXHgEXFTcuAScuASMzIgYHDgEVFBYXHgEzMjY3PgE1NCYnLgEjFzIWFx4BFRQGBw4BIyImJy4BNTQ2Nz4BJTIWFx4BFRQGBw4BIyImJy4BNTQ2Nz4BMzcyFhceARceARUUBgcOAQcOASMiJicuAScuATU0Njc+ATc+ATM3IgYHDgEVFBYXHgEzMjY3PgE1NCYnLgEjFTIWFx4BFRQGBw4BIyImJy4BNTQ2Nz4BMwMiBw4BBwYVFjY3JjY3PgE3NhYXHgEXHgE3PgE3PgE3PgE3PgEXPgE3MTcnJicuAScmIwEOAQcxBxceARUUNjcxNz4BJwIKXlJTeyMkJCN7U1JeXlJSeyQjIyR7UlJetwgMBQUEBAUFDAkIDAUEBQUEBQ0IFVQWU/66DRUHCAgHCAgTDAsSBwcHBwYHEAkJDgUBBAMDCAUEBQMBAwEdAQgGBQ8JcwsSBggIBwcHEwsLEgYICAgHBxIL0wMDAgECAgIBAwMCBAECAgICAQT+uQQIAwMDAwMDBgUECAMDBAMDAwgEdgMFAgIDAgEBAQIBAwICBQMDBQICAwIBAQECAQMCAgUDaQcNBQQFBQQFDQgIDAUFBAQFBQwJAwQBAgEBAgEEAwIEAQIBAQIBBAJKVk1MciEhAQUFBAMFAjw3MZlhEicVKUMbFSsXDBMHBgkEBQsGBAoFAQEPJSZsRERMAasCBAIIAQQEAwIBAQEBA3cjJHpTUl5eUlN6JCMjI3tTUl5eUlJ7JCP9fAUFBRAKCxAFBQUFBQUQCwoQBQUFU6amAgoJCh8VFR8JCQkHBwgTDAsTBwcHBwYNEgQFBAICAgUDBAQJDwUFBQgICiAWFx8JCAkICAkgFxYgCQkIYgICAgsHCAoDAgICAgMKCAcLAgICFAQDBAsHCAsDAwMEBAQLBwcKAwQDNQICAQcFBhMNDhQFBQYCAgEBAgIHBQUTDg0UBgQHAQICGwUFBg8LChAFBQUFBQUQCgsQBQUFEQICAwoHCAoDAgICAgMKCAcKAwICAh4hIXJMTVcEAwUGDwUCKw0MED4JFQ0RBwUHFxAKFQgHCQQEAQIFCQQBAkc9PFkZGf7CAgQCBwgTJxQCHBMMEBoDAAAFAJ//9wOaA1YAGwCuAOUA+AEFAAABMhceARcWFRQHDgEHBiMiJy4BJyY1NDc+ATc2AzIWFTERLgEnIzc+AS8BLgEjBiYHIiY3MDQxNCY3NhY3MjY1PAE9ATwBNTQmJzEjIgYVHAEVFBYzFjYXFgYHFCYHKgEjIgYVHAEVFgYnJgYHMQceATMxMzI2NSY2JzQmJyI2NTwBNTQ2MxYyNzIWFRwBFRQGBw4BHQEUFgcUFhcxMxwBFRQWFzEhIiY1MRE0NjMxASIGBwYWMRcWFA8BDgEjIiYvASMmIgcOARUUFjMyNjc2FjEXHgE3MDY3NiYvATAmNz4BNTQmIyceARcyFjsBBw4BDwEOATcmNic3MwcOAQcOAScjPgEXAs0qJiU3ERAQETclJiorJSU4EBAQEDglJQMtfhY+KwMBBAMCAQkRDiRJJQsIAgMGBgoNCyEpA2gfCykBChUCBQQBCgQkSCQSDAEFCAgMBgkFEQ0+HRABAQEcEBYKBQcgQCAHBAQHIA4BAQYRJCgl/vwuWmImAZoGDQYFAyoKCgQFDQYHDQUqAQEDAgICNSYKEwkEBTkECwYUAwEEBDcGAwQENiW8FR8JChMJAQkDBQIKKx4CPFMXvwQCAQICByghCBotFAGREBA4JSUrKiUmNxARERA3JiUqKyUlOBAQAcVQW/7IHCkDAQYPBAIJBgEBAQgLAQ4fCQkGARMYEw8KCgMHBRoLBSEJGBUYIAsBBwYRJxEGAgELEhw4HAcIAgEHBUsPEBYJDh4PDwsBCBcSJBIHBgEBBgYXLhcHBQEBCxwKCQYIChcKAgUDM1kdWi8B/2dE/dUCAgIFKgocCwQEBgYEKwEDBg0HJjUEBQIEOQQEAQcQBgsEOAcGCBIJJjWHAQEBAQMBAQEEEEoBC1sBCQMBAwICAwEIBgEAAAAIAIkAGgOaA00ACwAbACsA6QD2AQIBEgEmAAABMjY1NCYjIgYVFBYnIgYVFBYzMjY1NCYnLgEjEzYmJy4BBw4BFRQWMzI2NwEeARUxERUWFA8BPQEVBy4BJzUjNz4BLwIuAQcOAQcGFhcxFyMuASMiBgcxIy4BJyY0Nz4BFzEzHgEzMjY3MTM+ATc2NCcuAQcjLgEHDgEVFBYXFjY3MTMeARcWFAcOASsBLgEjIgYHMSMiBhUUFjMxMx4BMzI2NzEzBw4BFx4BFxY2NzE3FQ4BBycHFzcXPgE3MTUzFR4BFzE3FwcVFwcnDgEHMRUjNS4BJzEHJzc1JxUHFyEiJic1ETQ2MwEiBhUUFjMyNjU0JiMVMhYVFAYjIiY1NDYhIyIGFRQWMzEzMjY1NCYjJyMiBhUUFhceATMxMzI2NTQmJzECMwcKCgcHCgroBwoKBwcKAgMCBgR2AgIDBAoFBQUKBwUIAgESTDsCAQESBQkFfRIGAQUCLAQMBQYIAgEDBAaVByIVFSIHCAsTBgYGBhYMiAciFRUiBwsWJwsNDQwrGOAIKRgYHx8YGCkI5QsTBgYGBxUNBAciFRUiB4gkNDQkBAciFRUiB5UGBAMBAggGBQwEGQUKBDw/JjAyBAgEagQHBDM0MzE0MgQIBGgECAQyNDMZPCD+zko8ATZEAewcKCgcHCgoHA0VEw8OFBP+5r8KDg4KvwoODgpkXAoNAwQDCAVcCQwMCQIaCgcHCgoHBwqqCgcHCgoHBAYCAwL+2AQKBAQCAgIJBQcKBgUBsQEhZP6/AgYfFAE8AT0KAwYBRxMGEAcDLQQDAQIIBgYMBAcUGRkUAQsKCxkLCwwBFBoaFAEXFBYyFhUYARcaBAUmGRkmBAQZGAEMCQsaCwoMFRkZFTUmJTYUGRkUBgULBgYJAQIDBRlFAwQDJG8XTRsCAwM1NQEFAhtUGxAdVBwDAwI1NgEFAhtUGxANDiU4IF4IAeNgJv28KB0cKCgcHSgjEw8NFRMPDxMKBwcKCgcHCkUKBwQGAgMCCgcGCgEAAAAACABE//cDvANvAA8ASwBpAHkAhwCVAJkAqQAAATIWFREUBiMhIiY1ETQ2MwEmIisCMhYXFRccAQ8CIwYiIy8BLgE9ATc0NjczIyIGBx0BFBYXMyEyNjU3NTQmNz4BPwE+AScmBicTISIGHQEUFjMyNj0BIREjIgYVFBY7ATI2NRE0JiMFIgYVFBYzMjY3NjQnLgEjBSMiBhUUFjsBMjY1NCYnIyIGFRQWOwEyNjU0JgMzFSMjITIWFTEUBiMhIiY1MTQ2AzdNODxJ/ZJQNUBFASAHAQYDRQECAQ8BARgBAQIBARgBAQ8BAQFEHy4CAgICATACAwEJAwEOCAYOEg0YSgbu/lIKDgcFBQcBrlQFBwcFVAoODgr+siU0NCUYKQwMDAwpGAEVTQUICAVNBQgIBbMGBwcGswUICNRiYvcCUBgZGRj9sBgZGQNvVDH+iTJTUzIBdzFU/mUDAQEBTAICAQIdAQEdAQMBAkwBAQErHwVJAgMBAgICSQQjDwoYDQoVJgoQewMBVw4KeAUHBwV4/lIHBQUHDgoBrgoOjzQlJDQXFRUvFRQYJQcFBgcHBgUHTQcGBQcHBQYH/iqUGRgZGRkZGBkAAAAACwB9ADEDgwNXAAsADwATABcAHAAgACQAMAA9AEkAVQAAAREUBiMxISImNTERASEVISUjFTM1IxUzJSEVITUlIxUzJTUhFQEyFhUxFSE1NDYzMRciBhUUFjMyNjU0JiMjIgYVFBYzMjY1NCYzIgYVFBYzMjY1NCYDgzVF/e5ENgJn/soBNv51Ojo6OgGM/skBN/50OjoBi/7KAVscXvz6XhzADRMTDQ0SEg3QDRISDQ0SElsNExMNDRISApX+FRteXRwB6/5QOjo50Do6OjqWOQQ5OQF6IDxFRToiMBMODRQUDQ4TEw4NFBQNDhMTDg0UFA0OEwAAAAAGAMIAcgN/Au8AEACbAM4BGgE/AVAAAAEyNjc2JicmBgcGFhceATMxBS4BJzYmJy4BBw4BFRQWFx4BNz4BMzIWFxYGBw4BFR4BFx4BFx4BHQEUBjEjNTQmJy4BKwE1NCYnDgEjIiYnDgEdASMiBh0BIyImPQE0Njc+ATc+ATc0JicuATc+ARcWNjc2JicuASMiBgcGFhcOAQcOAR0BFBY7ARUUFjMhMjY9ATMyNj0BNCYnMQEOASMiJicuATU0Njc+ATMyFhceARcjLgEnLgEjIgYHDgEVFBYXHgEzMjY3PgE3Mw4BBxcOASMiJicuATUzHgEXHgEzMjY3PgE1NCYnLgEnLgEnLgE1NDY3PgEzMhYXHgEXIy4BJy4BIyIGBw4BFRQWFx4BFx4BFx4BFRQGBzEXLgE1JzQmJy4BKwEVIzUzMhYXHgEVFAYHDgEHMR4BHwEUFhcjJy4BKwEVMzI2Nz4BNTQmJzECIi9ICQoqKitZHBsEHxMzHAFFCRgNFwIZGUwjAgMCAgIFAgoVCx0vCwwLFAEBAQMCDxoKCgoCIwcGBg8JLjYqFTkgHzoVKjYqERkrAQEKCgoaDgMDAQEBGAUVFEMfAwcCAQIDDBkOHzYPDgYTDRgJDAwLCCsZEQH0EhkjCAsMDP5qBxEKDxcHBwYGBwgWDwsSBwYIAhQBBgQECwYKDgUFBAQEBQ8KBwsEBAYCFAIKCHgGEgwLEgYICRQBBQQDCwgHCgQEBAYFAgwKDA4CCAcHBwcQCgsRBgYIARQCBQMDCgcGCQMEAwQEAgsIDRADCAgGB28CAwIDAwMHBh8VNwkPBgUGAgMDCAQICQECBAMWCwIJByAgBgkDAwMDAwH0Oi4tUxMTGCUmXCMVFw8LEQYfTh4eDxEBBAMCBAEBAQEFBiAaGjgVAQUCAgMBBBAMDB4PyAEBhggQBgYGBi9KDxcaGhcPSi8GGRGGAQHIDx4MDBAEAQMCAgUBGUUcHBAQAQIDAwcCBgYiHB0/GQYRCw8jEsgIDCkSGBgSKQwIyBIjD/7iBgUKCQgXDQ4WCQoJBQYFDwoHCAMDAwcHBRELChEGBgYDAwMLBwsRBgIEBQQFBREMBwoDAwICAgIGBAUIAwEEAwMGAQQNCAgNBAUEBAUEDwoFCQIDAgIBAgcEBAYDAQMDBAYCBQ0JCA0FBwMIBQ8FCAICAjJ7BAQFDggFCQMEBgECCwkUBQkEZQMCJwMCAwcFBQcCAAAABACr/+cDXQOAAAkAHwBRAJQAAAEqASM1PgE3FSMDIgYdAS4BNTQ2MzIWFRQGBzU0JiMxBzQ3PgE3NjMyFx4BFxYVFAYHLgEjIgYdAQ4BBzU+ATU0JiMiBhUUFhcVJicuAScmNTEFIgYdASoBKwE1NCYjIgYdASM1PgE1NCcuAScmIyIHDgEHBhUUFx4BFxYXFScmIgcGFB8BFBYVHgE7ATI2NRE0JiMxAfQCBAIGDAUPOhQdJzJNNjZOKiEdFeQREjwoKC4uKCg8EhEhHQYPCBUdBQwGNUVpSkpoTTsmISEwDQ4CVRQdAgQCEB0UFR0ZKC0UFUgwMDc3MDBIFRQRETspKS9KDykODw+rAg8oF/4sPx0VARBaAQQCYQGmHhWQDUYsN09PNyhBEIoVHkQvKSk9EhISEj0pKS8uUR4EBR0VDQIEAS0SXzxLa2tLQGMOLQgUFDslJSnRHhVeghUdHRWCnCVmOzgxMUkWFRUWSTExODItLkYYGAixSw8PDioPrgEBAQ8TQC0BGhUeAAAAAAMAbAAfA5QDTQAXAG8AlAAAATIWFx4BFRQGBw4BIyImJy4BNTQ2Nz4BNzIWFx4BFx4BFx4BFRQGBw4BBzEXFgYHMQ4BIzEPAw4BBwYmJzEvAQ4BIyImJzEHDgEnLgEnMS8CIiYnMS4BNzE/AS4BJy4BNTQ2Nz4BNz4BNz4BMxUiBgcOARUUFhceARceARceATMyNjc+ATc+ATc+ATU0JicuASMCAClKHR0fHx0dSikpSx0dHx8dHUspHzwcGzEWFSELDA0NDAQLBo8GAwcDBQMEZycCAQMCCBQGCZIMFwsMFwucBhMIAgQBAidrAwUCCAMHCIYGCwQMDAwMCyEWFTEbHTwfNF8kJScSEQUKBgQKBiRfNDNfJQUKBAYLBBETKCQlXzMC5h8dHUspKUsdHR4eHR1LKSlLHR0fZwwMDCEVFTEcHDwfHzwdChUKtwgUBgEDAQ5gBQIFAgYDCAu7AQICAcYIAwYCBQIFYA8DAQYUCAusChUKHTwfHzwcHDEVFSEMDAwwJyUlXjQjQh4IDwcGCwUlJyclBQsGBw8IHkIjNF4lJScAAAAEAIIAUAN+AvsAJQAyAFsAdwAAATIXHgEXFhUUBgcuASMiBhUUFhcxDgEjIicuAScmNTQ3PgE3NjMBMhYVFAYjIiY1NDYzFSIGFTEVIyIGFTEUFjMxMxUUFjMxMjY1MTUzMjY9ATQmIzEjNTQmJyMBIgYVMRUjIgYVFBY7AjI2NzE+AT0CNCYjMQHWRz4+XBsbAQEPIRJFYxsXI04qRj4+XRsaGhtdPj5GARM+V1c+PVdXPQcKLAcKCgcsCgcICiwHCgoHLAoHAf7wCg6lCg4OCQG9BQgEAwMNCgL7GxtcPz5GCA4HBgdjRiM+FxEUGxtdPj5HRj4/XBsb/oZYPj1YWD0+WEsLBywKBwgKLAcKCgcsCgcBBwosBwoBASwOCqYOCQoOAwQDCQQBvQoOAAAABgBHABoDtgNLADAASwBWAGIAfQCZAAABMhYVMRUuAScOAQcVNjQ9ASMVFAYHHgEXPgE3NTQ2Nw4BFx4BFzEhIiY1MRE0NjMxFyMVMxUjFTMVDgEHFzc+ATcOAQc1MzUjNTM1FyMWBgceARc+ATU3IxUzFSMVPgE3NTMXMhceARcWFRQHDgEHBiMiJy4BJyY1NDY3PgEXJgYHMQcOARUxBxQWMzE3MjY3MTc+ATU0NicxArAeTA8gEFR5JQE5KCgNGQwFCgUKCg0BCwYQCv7ZHkxMHraZMS8vDhwOB5kBAgINGQwqKi05MwEDAw0aDQIC9qQyLRY3HjQ1MSsrPxMSEhM/KysxMCsrQBITJCEhVpAOGg2pAQQKDAg1BAcCqAYHCQYDS00e0wYIAgYuKAIFCAXq4EBjIgoVCwULBQQdNhonXjYRIA9NHgH+Hk2oO2E6dgIDAj8aDR8RAgUBbTphOz4vXi8BAgIxYS9AO28tGCULVHoSEz8rKzEwKytAEhMTEkArKzAvViIhI14NCg2lAQcEOQkLEAMEpAYPCQgBBgACAGYAPAO8A00ANQBJAAABIy4BJz4BNTQmIyIGFRQWFw4BByMiBw4BBwYVFBceARcWMyE4ATEzMjc+ATc2NTQnLgEnJiMBPgE3LgE1NDYzMhYVFAYHHgEXIQLbKQUzJRIWPSsrPhYSJTMFLy8pKT0SEhISPSkpLwFZOi4pKT0SEhISPSkpLv6cBTMlEhY+Kys9FhIlMwX+xQILLEURDywaLD8/LBosDxFFLBISPysqMC8rKj8SEhISPyorLzAqKz8SEv4yK0YRDywZLT4/LBksDxFGKwAAAAAEAEAAMwPAAzMAAwAHABQATgAAATcXIxcnMwcBIiY1NDYzMhYVFAYjFzYmJz4BNTQmIyIGFRQWMzI2Nx4BByMwJi8BNz4BPQE0JiMiBh0BFBYfAQcOARUjFBYXITI2NTQmMQFVKytWKytWKwGAEhkZEhIZGRJzCBYcCAo6KCg6OigJEgkTFAp6C017OgMCYUdHYQIDO4QoLwFFLwKfHk9NAUlAQMDAwAEqGRISGRkSEhm/MVogDBwPJzg4Jyc4AwMVRy1VJD5SAwgEh0RcXESHBAgEUT4fPGQ1QgQtOzwdAAMAqwBCA1UDKwAFABAAHgAANxEhESUFEyEyFhU5ASE0NjMnITIWFRQGIyEiJjU0NqsCqv6p/q0+Ai4aJP1WJBofAmwNEhIN/ZQNEhJCAg/98YmJAm0lGholfBMMDRISDQwTAAADAJYATgNuAyYADwAfAE8AAAEyFhURFAYjISImNRE0NjMBISIGFRQWMzEhMjY1NCYjASMOAQcxBwYWFx4BNzE3Fx4BMzoBFT4BNzE3FxY2Nz4BJzEnLgEHDgEHMQcnLgEjAvBeIClV/iNRLChVAbD+fgkMDAkBgggNDAn+tAMGCQM3AwcICRADKZUDCQUBAQYJA0g2BhIGBgEGSwQKBQYJAkaWAwsGAyZTIf4QIVNTIQHwIVP97RYJCRoaCQkWAXUBCAWSCBAEAw8Ja90EBQEBBgaiOwYVBgYSB1IEBAEBBgWd3wUFAAAABwB+ADIDggM2ABAAIgA0AFYAYABlAHgAAAEyFhURFAYjISImNRE0NjMhASIGBwYUFx4BMyEyNjU0JiMhJyIGBwYUFx4BOwEyNjU0JisBASEVFAYHMRc+AT8BIzUzNTMVFAYjKgErARUjFzMyNjUxEQcjFTMnMhY7ATUHFSM1MycjFSMVMxUjHQIzNSM1MzUjNQMBNUxMNf3+NUxMNQIC/goMFAYGBgYUDAEpExkZE/7XDwgNBAQEBA0Ixg0REQ3GAST+3hAPGxEUAgEDA9sHBwMGAgQfCS8UE0ChggEGDAYIIGFhLyRDQ0u+T0NDAzZMNf3+NUxMNQICNUz9fQkHCBEHCAgSDg0TgAgIBxEIBwkTDQ0TAYKSLkweFx5LLgkjX/QHBgEeEBABH8FgAgFfICAgwR0gIREOBCMhIB0AAAADAHkAYgNoAwIADgAeAEwAACUiBhUUFjMhMjY1NCYjIQEyFhURFAYjISImNRE0NjMFIgYHMQcOAScxJyYGBzEHBhQXHgEzMjY3MTc+ARcxFxY2NzE3PgE3NCYnLgEnASsIDAwIAa0JDAwJ/lMBtThQUDj+IjlQUDkB8AYKBacCBAJ0Dh8LeAgIBAoGBQsEcAEFAnMNHQuwBAUBBAMECgWSDgoKDg4KCg4CcFA5/u84UFA4ARE5UIQDBJIBAQFBBwQMfwgXCAQEBAR2AgECPwcDCZoECQYGCgQEBQEABQCiAAEDhANVAA0AGwApAFsAawAAJTIWFRQGKwEiJjU0NjMjMhYVFAYrASImNTQ2MyMyFhUUBisBIiY1NDYzEz4BFzEXHgEHMQcwFDEOAScxJwMOAQ8BBRM2Ji8BJgYHMQMTPgE/ASUnMDQxJy4BNzEDDgEXHgE3PgEnLgEnJgYHA20JDg4JWAoNDQpvCg0NCm4KDQ0KbwoODgpuCg0NCt8KGwv0CgQJMQkcCw+JAggFBP5w4wYBBQMHEwblTQEGBQMBFhEBCwIKNhARAgMuHh8mAgIXERInEDAOCgkODgkKDg4KCQ4OCQoODgoJDg4JCg4DIQsCCc4JHAs/AQsDCAz+yQUJAwGcARYHEQcCBgIH/ucBrAULBAK6DQEBCR0L/v0LJBQeKAMDLx4UIAkIBAsAAAAGAIkAGgO8A00ALgAyAEYAVgByAJEAACUjNTM1IzUjFSM1IxUjFTMVIxUzBw4BBxc+ATc+ATcnMwceARc3LgEnLgEnMzUxKwE1MycOAQcUFhcUFhc+ATcVMzU+ATcnJRY2Nz4BJy4BIyYGBwYWFwUyFhcRLgEHISYGFREUFjchNy4BNTQ3PgE3NjMFJgYxJzYmMTA2NxY2MTMWNjEwFhcGFjEwBgcmBjEjA7wsIyM1LDUkJCs+Bg0dECoIFQ0JDQQqXiUNIxYqBgsEDxgIRmEsLJ8IGhEDAgEBBgoFMQcNBTP+5hMpEA8IBwgjFRsoAQEWEwFtEiMRAVsq/kAqXFwqATADEhMTE0IsLDL+jhlvJEZGChpYMEQQeQgZQUEJGVYyRKk8MDg4ODgwPC8GDxwNIgcVDggOBRsdDCEUIwQKBQ0UBy88aCJBHQcXDwoOBQcPCIflECARDZEIBw4PKRMUFwEnHBQkCTYFBQErK1oBAVor/fYqWgEEHEAhMCsrQBITWFcNOEBTECkYZFEFDyg6XA4oGGMAAAAHAKsAMANVA00ACwAkAGQAdACEAJQAxQAAASIGFQYWMxY2NTYmBzQ2MxcyFhceARUHKwEvAS4BIyIGDwEjNxcHFxQGBwYiIyoBLwEHKgExOAEjMCIxMCIxOAEjMCI1LgE/AScuATc0NjczPwE+ATMyFh8CMx8BMhYVFgYHMRMyFhURFAYjISImNRE0NjMBISIGFRQWMzEhMjY1NCYjNSEiBhUUFjMxITI2NTQmIwMmBgcOAQcOAQcOARUGFhceARceARceATMWNjc+ATc+ATc+ATc0JicuAScuAScuASMCDkhoAWdISWgBZ4YGBGYDAwIBAgEKCQIPAw4ICQ0DEhMBoDYTAQEBAgEBAgE3OAEBAQEBAQECAQEUNgIBAQMCRAMUAQMCAgMBEwMMFSMCAwEBAlw4UFA4/mY4UFA4AXn+wwsMDgkBPgsMDAz+wwsMDgkBPgsMDAydFSkTEyEPDhcICQgBCQgHFw4OIhIUKRUVKRMTIg4PFggJCAEICAgWDw4hExMpFgLmZklIaAFnSElnJwQGAQIBAQQChwUtCAoJCDKHqShAAgQBAQEnJwEBBAFBKAIDAgICAQY6AQMDAToGAQECAgIEAQE4UDn99TlQUDkCCzlQ/YURCgoREQoNDmwRCgoREQoNDgHLAQkIBxcODiISFCgWFSkTEyIODxYICQgBCQgHFw4OIhIUKRUVKRMTIg4PFggJCAAAAAAGAIkAPAN3AysAGAAdACEAJgAqAC4AADciJjURNDY3ITIWHwERFxEzMhYVERQGByElIRUhNTUhFSEDIxUzNRcjFTMnIxUz2xU9PBIBxRA9BQQlMBUbGhL9kAGH/ooBdv6KAXa9ub2yiZAHiZA8QRcCNxdGA0QcB/3UIgI2Hxf+AxckAto9PXg+ATq7u3s9uD0AAAAABQBcAA0DoQNYAD8AZABwAH4AigAAATIXHgEXFhUUBgcmBgcXLgEnNSMVLgEjIgcOAQcGFTEVFBceARcWMzI2NxcOARcOASMiJy4BJyY1NDc+ATc2MwEVHgEXMTcXBxUXBycOAQcxFSM1LgEnMQcnNzUnNxc+ATcxNTMHIgYVFBYzMjY1NCYBIgYdARQWMzI2PQE0JhMUBiMiJjU0NjMyFgH+VkxMcSEhAwEDLiIEBQoEgCNeNjcxMEcUFRUURzAxNxMlEg0PDwEPIA9XS0xyISEhIXJMS1cBLwQIAzIzMS8zMQQIBGYEBwQyMzIyMzIEBwRoNRkjIxkZJCT+8TxUVDw7VVXjFxEQGBcREBgDWCIhck1NVxAdDgMoIAIDBgFGOxIWCwsnGhoeBR0aGSgLCwIDFxEVAgICISFzTUxXV01NciEi/gM5AQUDHlodER9aHQIEAjk7AQUCHVkeER1aHgMDAzlmJBoZJCQZGiQB11Y8BDxVVTwEPFb96w8ZFxESFxcAAAQARAA8A5cDCQALABEAOQA/AAABFBYzMjY1NCYjIgYXNycjBxcXFgYnJhceARcWNz4BFxYHDgEHBiMiJicuASMiJj0BPgE3NhceARcWExcHIyc3AnghGBchIRcYIaNqatVqak0NkWwLBAQ1NDRWrUEWCyAhYzQ1HVVnXylYLyw9VoIsITU2aSgoYz4+fj8/AlUXICAXFyAgyrO0tLNzIhY4BRkZMwoLI0YZHA4lJk4fHiMVCAk9LG1HUAkHGRhIJSQBf2pqamoABgBZ//cDvAMWABsAXABzAHgAfQCBAAABIgcOAQcGFRQXHgEXFjMyNz4BNzY1NCcuAScmBzYyHwE/ATYWHwEWBg8BMzIWFRQGKwEVMzIWFRQGKwEVFAYjIiY9ASMiJjU0NjsBNSMiJjU0NjsBJy4BNTQ2NzEHNDY3PgE7ATU0JiMhIgYVERQWMyE1MwMhFSE1FSEVITUVMxUjAt4uKCk8EhEREjwpKC4uKCk8ERISETwpKI4HEwc/PwEHEgYCBgEHNyUKDQ0KNjYKDQ0KNg4JCg02Cg4OCjY2Cg4OCiQ3AwQEA6ATFBRbRzhDF/4aF0NDFwEqAd4BTP60AUz+tN3dAbMREjwoKS4uKCg9ERISET0oKC4uKSg8EhFcBwdBQQEGAQYCBxIHOQ4KCg4QDgkKDigKDg4KKA4KCQ4QDgoKDjkDCQUFCASjU24cHBzzF0NDF/4aF0M4Abs3N283N244AAEAeAAaA5oDTQA8AAABJicuAScmBxYXHgEHBjEDNycHJwcXNwUGJy4BJyYxBxcHJgYHBhYxFjYxNxYXFjY3NjEXNycwNz4BNzYnA4kTKCdsQkJHpDo6CBgY/WFDNGPHeksBA1NMTHQjI048CwoVCDRCPBEOcllZfCAhV3dOEA8fCAcQAhdGOTpSFhYBUVtbmzMzAQNrPjACzG5J/TYICD4jI0VGDwIFBzI1Dk8TPQoLGRQUUXdUGxtWNzc4AAAAAAIAbABfA5YDBgAUAEMAADchMhYXFhQHDgEjISImJyY0Nz4BMwEWBgcFBiYvASY2Mzc2Fh8BHgE/AScmNjc2FhcWFx4BFxYXHgE7ATc+ARceARcxiQLuCg8FBQUFDwn9EQkQBAUFBBAJAw0GBwv9nBMlDnQICAsSBw8GSwwbDHuXCQsOCBIMDiAgSiIiEQYPCAOpDR0PFzwXowkICBIICAkJCAgSCAgJAZ0KGAPKBwsPewkWAQEEBCsGAgUw2Q4gBQMBBgcZGDocHA4GBkMFBAMEHiUABQBmAF4DvAMJAFoAbgB6AJEAnQAAASMiBgcnLgEjISIGDwEuASsBIgYHDgEdARQWMxcOARUHHAEVHAEdARQWFx4BOwEyNjc+AT0BIRUUFhceATsBMjY3PgE9ATwBJzY0NSc0Jic3MjY9ATQmJy4BIyU3NT4BNyEeAR8CDgEjISImJzcTIiY1NDYzMhYVFAYlFAYHDgErASImNzU0Njc+ATsBMhYdARciJjU0NjMyFhUUBgOmUAMFAyQJOyj+rS43ByQDBQNQBQgDAwQNCh4HCAsGBQYPBz0IDwUGBgIJBgYFDwg8CA8GBQYBAQsIBx4JDQQDAwgE/XEaAgkHAZ4GCQEbCgErFf55FSsBChIZIyMZGSMjATcDAgMGBKkHCwECAwIHA6kICoEZIyMZGSMjAkABAm8vLjYnbwECAwQDCAUTCg0FDiETggMGAwIDAp0IDwUGBgYGBQ8IMzMIDwUGBgYGBQ8InQIDAgMGA4ITIQ4FDQoTBQgDBAMXSAIKCQkJCQpKKBYeHhYo/qwkGRkkJBkZJC0EBgMDAgoIMwQGAwIDCggzLSQZGSQkGRkkAAAAAgBmABoDmgNNABsAMwAAASIHDgEHBhUUFx4BFxYzMjc+ATc2NTQnLgEnJhMjFTMVIxUjNSM1MzUjNTMnNxc3FwczFQIAVUpLbyAhISBvS0pVVUpLbyAhISBvS0phiIiIXIiIiHZpQWhoQGh2A00gIW9KS1VVSktvICAgIG9LSlVVS0pvISD+Zi1biYlbLVtoQWlpQWhbAAAGAEQAHQN3A00AJQBUAF4AmQD+ATUAACUuAScuASMiBgcOAQcGFBceARceATc+ATc+ATc+ATU0JicuAScxJS4BIyIGBw4BBw4BBw4BFRQWFx4BFx4BFx4BMzI2Nz4BNz4BNz4BNTQmJy4BJzETLgEjDgEPASEnEzU8ATU0JjUuASMiBgcOAR0BHAEdARwBHQEcAR0BHgEXHgEzMjY3PgE3PAExPAE9ATwBNTA0NTwBNSclLgEnLgEjITQmLwE0JjUuAScuAScuASsBIgYHDgEVFBYXHgEXHgEXHgE3Mx4BHwMUFhceARceARceARceATchFjY3PgE1NCYnISchMjY3PgE3PgE/AT4BNz4BNz4BNTYmJzEHFSMOAQcUBgcXBycPARUjNScuAScHJzc0JicuATUjNTM+ATcnNxc+ATc1MxUeARc3FwceARczAugGDAcHEAgPHAsFCQMGBgMJBRAvFgcMBgUIAwQDAwQDCAX+pgsdDwgPCAcMBQUJAwMDAwMDCQUFDQYIDwgIDwcHDAYFCAMDAwMDAwgFxAgWDAsVCG8BL24bAQgjFRYjBwEBBRoSBQkEBQgFEhsEAwEFAwkGCBMJ/cgEAQMDAQICAg0JBg0HVAgQBgYGAQEBBAMCBwUECwU+ChIIDgwJAwEBBAIDBgMECQYGDggBtAkQBgQEFBT+XAwBtAsVCAoOBAIIBRMFCAUEBQECAQEDA8EiAQIBBAEYKhcJDTwLAgUCGCkXAwECAiMjAQUDFycXBgsGOwYLBhcqGAMFAiCkBQgDAwMMCgYNBw8hDwcNBhEKCgMJBQYNBwgPCAgQCAcNBgMLDAMDAwkFBg0HBxAICBAHBw4FBggEAwMDAwQIBgYNBwcQCAgQBwcNBgKSCgoBCwlzdP7dBgEDAQEDARQYGBQBAwEHAQMBAgEDAQYBAQEHEhsFAQEBAQUbEgECAQIBBAEDAQEBAQIBAn0FCAIEAwkQCRoGCgMDCAQKDwQEAwYGBg8IBQoFBQoEBQcEAwQBNmIsS0QyBg0GBg8HBw4GBgsEBAQBAgcHBw8IERIBTgcHChgOCBcOOg4bDA0PBAULBQULBGY6AwYDAgUCGCkXBAUiIgMBAgEYKhcDBAIDBgM9BgwFGCkXAwQCIiICBAMXKRgFDAYAAAAABgDAAA8DYANjAB0AXABgAHIAfACMAAATPgEzMSUyFhceARUxAxQGBzEFMCIxLgE1MRM0NjcTMBQVHAEVHgEzMjYXFjYVFAYjMSMqASsBKgErASoBOQEVMDI7ATAyMTM6ATsBOgE7ATI2NTQmJyYGJyY2NTE3HwE3JwceATcxNxcHHgE3HgE3MTcnNwcXNzYmLwEmBicOARceATM+ATU2JicmBgfnETkgAZEfNA4ODwNNNv5rATdNAxISZgIXJRw3Ew4OBBgbAgQBOgQHBBICAwMDBQILBhILDQYLBiMtFCMeH2QEAQGjBDlMJ04CDA42CDUKIBMCDxJNhCgQhRAGBwtOCxmyDAcHBh0RFyEBExAQIgwDJRwgAiEcHUEh/e43TAECAU03AhAiQh39sxYJBAsEBwUDAwIBDAsLHyMVFRcBAQgHARYEcUkWFvyIDw4FXAVZFg0DEA4JiU43HE4cCxoHLgYHzw0hEBATASEXER0GBwcMAAAAAAYAvwA8A64DKwArADkASABXAGMApwAAAS4BIyIHDgEHBhUUFhcHBiIvASYGDwEGIi8BJiIPASMiJjURNDYzITIWFREFIgYVFBY7ATI2NTQmIyciBhUUFjMhMjY1NCYjITUiBhUUFjMhMjY1NCYjIQEiJjU0NjMyFhUUBic1MzI2NzY0Jy4BByM3PgE1LgEnJiIPAScmIgcOARUUFh8BIyIGFRQWFzMVIyIGFRQWOwEVFBYzMjY9ATMyNjU0JisBAyYJEQkqJSY3EBAdGhkFDAU/BQwFNAQNBT8FDAVGAhwoSh0BmQ5Z/jsKDw8KmgoPDwqaCg8PCgEiCw8PC/7eCg8PCgEiCw8PC/7eAaJGZGRGR2RkNkQECQIDAwIJBCwsAgMBAgIGDQU/PgUOBQIDAwIsKQcJCQdERAcKCgdECgcHCkQHCQkHRAGwAgEQEDglJSsoSBsWBAQyBAEEMAUEMgQEOygcAkRNGhpN/uwfDwsKDw8KCw+aDwsLDw8LCw+ZDwoLDw8LCg/9eGRGR2RkR0ZklB4EAwQJAwQEAScCBgMDBQIFBTc3BQUCBQMDBgInCQYGCAEeCQYGCSMGCQkGIwkGBgkABQCJ//cDdwNvACAALgA8AMgA2gAAATIWHQERFAYjIiY1DgEHDgEXMhYVFAYrASImNRE0NjMhAyMiBhUUFjsBMjY1NCY3ISIGFRQWMyEyNjU0JhMxIy4BLwEmNj8BNjQvATgBMSYiDwEOAS8BLgE9ATQmKwEiBh0BFAYPAQYmLwEiMDEmIg8BBhQfAR4BDwEOAQcjIgYdARQWOwEeAR8BFgYPAQYUHwEwMjEWMj8BPgEfAR4BHQEUFhczPgE9ATQ2PwE2Fh8BFjI/ATY0LwEuAT8BPgE3MzI2JzU2JiMxBw4BJzEuATcwNDU+ARceAQcxAs5HHhcQEBdSciEhHwEQFxcQ0kkdHUkB363CERgYEcIRGBhK/uARGBgRASARGBjcEQcKAgoDAgQNBAQVBQsEDQQNBRkGBwkGHQYICAYYBg0EDAEEDAQUBQUMBAICCgILBhIGCAgGEgYKAgoDAgQMBQUUAQQMBAwEDQYYBggIBh0GCQcGGQUNBA0EDAQVBAQNBAIDCgIKBxEGCQEBCQZeAS8fHyoBAS8fHysCA29IFwn+uREYGBEQNSYmdU4YEREYZhYCdRVP/mQYEBEYGBEQGK0YEBEXFxEQGP5QAQcGGAYMBQwFDAQVBAQNBAIDCgIKBxEFCQkFEQcKAgoDAgQNBAQVBAwFDAUMBhgGBwEIBh0GCAEHBhgGDAUMBQwEFQQEDAUCAwoCCwYRBggBAQgGEQYLAgoDAgUMBAQVBAwFDAUMBhgGBwEIBh0GCCAeKAEBKh4CAR4oAQIsHgAACACJADwDvAMrADUATwBsAI0AqgDLAOgBCQAAAR4BFTEVFBYXHgEzMTMyFhUxERQWFx4BMzEzMhYVFAYjMSEiJjU0NjMxMzI2Nz4BNTERNDY3ASMiBgcOARUxFRQWMzEzMjY1MTU0JicuASMlIyIGBw4BFTEVFBYXHgEzMTMyNjUxNTQmJy4BIzMjIgYHDgEVMRUUFhceATMxMzI2Nz4BNTE1NCYnLgEjMScjIgYHDgEVMRUUFhceATMxMzI2NTE1NCYnLgEjMyMiBgcOARUxFRQWFx4BMzEzMjY3PgE1MTU0JicuASMxJyMiBgcOARUxFRQWFx4BMzEzMjY1MTU0JicuASMzIyIGBw4BFTEVFBYXHgEzMTMyNjc+ATUxNTQmJy4BIzECZRg7BAQECgZXFRwEBAQKBSkMEBAM/QQLEBALKQUKBAQEPBcBgzcFCgQEBBALNwsQBAQECgX+nRsGCgMEBAQEAwoGGwsQBAQDCgakGwYKBAMFBQMECgYbBQoEBAQEBAQKBaQbBgoDBAQEBAMKBhsLEAQEAwoGpBsGCgQDBQUDBAoGGwUKBAQEBAQECgWkGwYKAwQEBAQDCgYbCxAEBAMKBqQbBgoEAwUFAwQKBhsFCgQEBAQEBAoFAysBGkAzBQsEAwUbP/5pBQsEAwUQDAwQEAwMEAUDBAsFAkBAGgH+AwQEBAoGcQsREQtxBgoEBARVBAQECgYcBgoEBAURDBwGCgQEBAQEBAoGHAYKBAQFBQQECgYcBgoEBASqBQQECgYcBgoEBAQQDBwGCgQEBQUEBAoGHAYKBAQEBAQECgYcBgoEBAWpBAQECwUcBgoEBAURDBwFCwQEBAQEBAsFHAYKBAQFBQQECgYcBQsEBAQABgBrAAoDlQNcACAALAA8AE0AVwBlAAABISIGHQEUFhceATsBNTQ2MyEyFh0BMzI2Nz4BPQE0JiMHIiY1NDYzMhYVFAYHMhYdARQGIyEiJj0BNDYzJSEiBh0BFBYzITI2PQE0JiMTITU0NjMhMhYVAyEiJjU0NjMhMhYVFAYDG/3KMkgNCwwdEBVHMgFsMkcVEB0MCw1IMigZJCQZGSMjVggMDAj+lAgMDAgBbP6UIi8vIgFsIi8vIlH98kcyARwyR4T++g0SEg0BBg0SEgJVRzLzEB4LDAwoM0dHMygMDAseEPMyR8ojGRojIxoZI7YMCWUIDAwIZQkMPC8iZSEwMCFlIi8BbGYyR0cy/Y0RDQwSEgwNEQAAAAkAiQAaA3oDTQAMABUAJQA1AEIATABWAGoAeAAAAREUBiMhIiY1ETQ2MxMjBzM3MxczJzMjFTM1MzI2Nz4BNTQmJyMzIxUzNTMyNjc+ATU0JicjBRQWHwIjNz4BPwEzNzIWFRQGKwE1MzMyFhUUBisBNTMDISIGDwEVFBYXMyEyNj8BNTQmIxMyFhUUBiMhIiY1NDYzA3c8Kv3eKjw8Kn8zTDAPSw8wSqxHLBYSHQsMDCQjBrNHLBcRHQwMCyMjB/6HAQEBGDgYAQEBAQG+ExMTExMTsxQTFBMTEyD+tg8XAQEUDwUBSg8XAQEXEZEcKCgc/ZodKCgdAoD+ACo8PCoBmio8/qvPLy/Pz0gJCQoZECAgAs9ICQkKGRAgIAIkBAcDBEhJAgYDBgIREBERQxEQERFDATMJBgMhBgoBCQYCIQgKAREoHB0oKB0cKAAAAAQAVv/3A7kDTQAYACEAJQApAAABMhYXEx4BBwMOASMhIiYnAyY2NxM+ATMhByMDMzczFzMDMyMRMycXIzcCuQo+D6kFGB29BTYK/qAgLAavGxYFoBFFCwFi2TluTg9cDk5u10tL8ho1GwNNFR7+zApXHP6jCgsxCgE3JzgKAUggE+b+djs7AYr+dvVpaQAAAAYASwBDA7QDagALAD4AcQB+AIsAvgAAJTAWFwYiJz4BNxYyAR4BFxYGBw4BIyoBIzEyFhceARUwFDEUBgcOASMiJjU0NjcxLgEnJjY3PgE3PgEzOgEzIR4BFxYGBw4BIyoBIzEyFhceARUwFDEUBgcOASMiJjU0NjcxLgEnJjY3PgE3PgEzOgEzAx4BFwYmJy4BJzA2NyEeATEOAQcOASc+ATc3HgEXFgYHDgEHKgEjMTIWFx4BFRwBFRQGBw4BIyImNTQ2NzEuAScmNjc+ATc+ATM6ARcCZhMTQpVVBxUPOG7+sCEvAwIbFwgRCQICAQwXCys4AQELVDA3W1Q7IjEBARwXAwcDBgwGAQIBAkYhLwMCGxcIEQkCAgEMFwsrOAEBC1QwN1tUOyIxAQEcFwMHAwYMBgECAV05TxcNIRQNPjIQEP5rDw8yPQsUIQ0XTzjPIS8DAhsXCBEJAgIBDBcLKzgBAQtUMDdbVDsiMQEBHBcDBwMGDAYBAgF2GhkaGgsaDhQBcAEuIRwuCwQFBAMMQCkCAwQCFBQaGTNIAQExIhssCwEDAQIBAS4hHC4LBAUEAwxAKQIDBAIUFBoZM0gBATEiGywLAQMBAgEBKyp/VQEFBT9fIRsbHBwhXj4FBQFVfypsAS4gHC4MBAQBAwMMQCkBAQECBQIUFBoaMkgBATEiHCsLAgIBAgIBAAAAAAMAUAAQA7ADcAAbADcAQwAAASIHDgEHBhUUFx4BFxYzMjc+ATc2NTQnLgEnJgMiJy4BJyY1NDc+ATc2MzIXHgEXFhUUBw4BBwYDNDYzMhYVFAYjIiYCAFlPT3UiIiIidU9PWVlPT3UiIiIidU9PWUM7O1gaGRkaWDs7Q0M7O1gaGRkaWDs75V9DQ19fQ0NfA3AiInVPT1lZT091IiIiInVPT1lZT091IiL9DBkaWDs7Q0M7O1gaGRkaWDs7Q0M7O1gaGQFEQ19fQ0NfXwAAAAACAFAAEAOwA3AAGwA3AAABIgcOAQcGFRQXHgEXFjMyNz4BNzY1NCcuAScmAyInLgEnJjU0Nz4BNzYzMhceARcWFRQHDgEHBgIAWU9PdSIiIiJ1T09ZWU9PdSIiIiJ1T09ZQzs7WBoZGRpYOztDQzs7WBoZGRpYOzsDcCIidU9PWVlPT3UiIiIidU9PWVlPT3UiIv0MGRpYOztDQzs7WBoZGRpYOztDQzs7WBoZAAAAAAIAaAAoA5gDWAAPABUAAAEhIgYVERQWMyEyNjURNCYBJzcXNxcDMv2cKjw8KgJkKjw8/nG9SHX0SQNYPCr9nCo8PCoCZCo8/Ye9SHX1SQAAAgBoACgDmANYAA8AEwAAASEiBhURFBYzITI2NRE0JgMhESEDMv2cKjw8KgJkKjw8Kv2cAmQDWDwq/ZwqPDwqAmQqPP02AmQAAwBYAJIDFgLbAA8AIAAwAAAlFRQGIyEiJj0BNDYzITIWNRUUBiMhIiY9ATQ2MyEyFhU1FRQGIyEiJj0BNDYzITIWAxYRDP18DBERDAKEDBERDP18DBERDAKEDBERDP18DBERDAKEDBHqOgwSEgw6DBER3joMEhIMOgwREQzqOgwSEgw6DBERAAEATQBzA2kDYwBkAAABFAYjIiYjIgYVFBYdASoBIw4BIyImNTQ2NTQmIyIGFRQWFRQGBw4BIyImJy4BLwEqATURHgEXHgEzMjY3PgE1NCY1NDYzMhYVFAYVFBYzMjY3FQ4BFQ4BFRQWFx4BMzI2MzIWFQNpKScsKycdGA8ECAQlSiYZLDU1JSY7Lw4IDB0PHjsdBg4GBgECAh4DHTseDx0MCA4vOyYlNTUsGSpSKQIEBQcHCggWDRgyLikpAVYlNTUhGhw3GwMECxgdJyssJyoqKS4xGQ0WCAoHBwUBAgEBAQHqAgMBBQcHCggWDRgyLikpKScsKyceFw0DAQIeAx07Hg8dDAgOLzsmAAACAEAAJwLkAycAEgAXAAABNyETIQ8BLwEjHwEzNTcTISchJSEDBSUCXwf+WBcBJQpfXgZUC60CrRj+ygcBRP3hAqQ9/ur+7QJGVP8AbRoaQ4YwAS8BBVfh/U5OTgAAAAAFAD8AAAMvA24ACAAaACoAOgBKAAABHgEXIzUeARcDIREUBiMhIiY1ETQ2MyERFBYTNTQmIyEiBh0BFBYzITI2PQE0JiMhIgYdARQWMyEyNj0BNCYjISIGHQEUFjMhMjYDDgMHA+cFCQQhAQocE/1uFBsbFAGIG2IJB/6nBgkJBgFZBwkJB/6nBgkJBgFZBwkJB/6nBgkJBgFZBwkChQQJBecDBwT+6P37ExwcEwMQExz+9RMc/pgfBwkJBx8HCQmEHwcJCQcfBgoKhB8GCgoGHwcJCQAAAAAEAD8AAAMvA24AHgA9AFwAewAAATI3PgE3NjcVFAcOAQcGIyInLgEnJj0BFhceARcWMxEyNz4BNzY3FRQHDgEHBiMiJy4BJyY9ARYXHgEXFjM1Mjc+ATc2NxUUBw4BBwYjIicuAScmPQEWFx4BFxYzETIXHgEXFh0BFAcOAQcGIyInLgEnJj0BNDc+ATc2MwG3NzY3YioqHh4dZkVETk5FRGYeHR4qKmI2Nzc3NjdiKioeHh1mRUROTkVEZh4dHioqYjY3Nzc2N2IqKh4eHWZFRE5ORURmHh0eKipiNjc3TkRFZh0eHh1mRUROTkVEZh4dHR5mREVOAfYFBRQQEBVTGhcXIgoKCgoiFxcaUxUQEBQFBf6HBQUVEA8WVBoWFyIKCgoKIhcWGlQWDxAVBQW8BQYUEA8WVBkXFyIKCgoKIhcXGVQWDxAUBgUCNQoKIhcXGj4aFxciCgoKCiIXFxo+GhcXIgoKAAAFAHEAKQOPA0cAEwAYACoANQBAAAABERQGIzERFAYrASImPQETPgE7ATMRIxEzARUUBisBIiY1ESImNREzMhYXJRUjNTQ2OwEyFhUhFSM1NDY7ATIWFQGrEQwRC+QMEW8BCAW9jnJyAVYRDOQLEQwRvQUIAf6ZnQgGgQYIASudCAaBBggCuP6qCxH+/wsREQvkAYUFBf7HATn+ceQLERELAQERCwFWBQWKY2MGCQkGY2MGCQkGAAAAAQAAAAEAAGb5Ar9fDzz1AAsEAAAAAADgnLHCAAAAAOCcscIAAP/ABCADwAAAAAgAAgAAAAAAAAABAAADwP/AAAAEAAAA/+AEIAABAAAAAAAAAAAAAAAAAAAAiAQAAAAAAAAAAAAAAAIAAAAEAACrBAAAKQQAAF0EAAAABAAAIAQAAGQEAAAgBAAAVgQAAFYEAABWBAAAVgQAAFYEAABWBAAAVgQAAFYEAABWBAAAVgQAAFYEAABWBAAAvgQAAEIEAAA4BAAA1gQAAIAEAABpBAAAagQAAFYEAACfBAAASAQAANUEAAD6BAAAUAQAAHoEAABWBAAARwQAAJAEAACQBAABqgQAAGgEAABQBAAAPQQAAHoEAACJBAAAkAQAAI0EAABmBAAAiQQAAGYEAABuBAAARAQAAFYEAABWBAAAqwQAALMEAABvBAAAggQAAKsEAABxBAAAsgQAAGYEAABLBAAAoQQAAIkEAADEBAAAbwQAAJMEAACuBAAAIgQAAIEEAACEBAAAbAQAANsEAABmBAAAZgQAAFUEAACUBAAAfAQAAFgEAACCBAAAXAQAAG0EAADHBAAAcQQAAGYEAAB8BAAAbQQAAEYEAACfBAAAiQQAAEQEAAB9BAAAwgQAAKsEAABsBAAAggQAAEcEAABmBAAAQAQAAKsEAACWBAAAfgQAAHkEAACiBAAAiQQAAKsEAACJBAAAXAQAAEQEAABZBAAAeAQAAGwEAABmBAAAZgQAAEQEAADABAAAvwQAAIkEAACJBAAAawQAAIkEAABWBAAASwQAAFAEAABQBAAAaAQAAGgDbgBYA7cATQMlAEADbgA/A24APwQAAHEAAAAAAAoAFAAeADgA8gFMAkwCtALQAuQDDgNOA3gDtgPaBBQERARuBLYE/AU6BZwFvgYWBmYGlga8Bv4HIAc4B24IgAiWCKoI2Aj0CSAJrgnKCeYKLgqECu4LAguoDH4NTA24DlAOrBIWEnISshN4FEIUkhT6FTYVdhbwF8YYUBkYGZIaEBpYGoYbmBvWHCYcaB2KHiwgICE2IdojyCQoJIQl2ifAKC4oiilEKaYqMCqMKyQrcC1SLq4wNjEaMZYzZjQuNQI1mjZqNtY3Rjd4N+w4jDj6OZQ6YDtuO7g8ejzePY497j5YPzA/fEEuQepCzEPyRTxFyEZwRrhHwEgoSIBIqEjMSRBJlknGSjJK5ktCAAAAAQAAAIgCwgAZAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAA4ArgABAAAAAAABAAYAAAABAAAAAAACAAcAVwABAAAAAAADAAYAMwABAAAAAAAEAAYAbAABAAAAAAAFAAsAEgABAAAAAAAGAAYARQABAAAAAAAKABoAfgADAAEECQABAAwABgADAAEECQACAA4AXgADAAEECQADAAwAOQADAAEECQAEAAwAcgADAAEECQAFABYAHQADAAEECQAGAAwASwADAAEECQAKADQAmG9vaWNvbgBvAG8AaQBjAG8AblZlcnNpb24gMS4wAFYAZQByAHMAaQBvAG4AIAAxAC4AMG9vaWNvbgBvAG8AaQBjAG8Abm9vaWNvbgBvAG8AaQBjAG8AblJlZ3VsYXIAUgBlAGcAdQBsAGEAcm9vaWNvbgBvAG8AaQBjAG8AbkZvbnQgZ2VuZXJhdGVkIGJ5IEljb01vb24uAEYAbwBuAHQAIABnAGUAbgBlAHIAYQB0AGUAZAAgAGIAeQAgAEkAYwBvAE0AbwBvAG4ALgAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",Ii=ti.replace(/\/src\/assets\/fonts\/ooicon\.ttf/,Mi);document.head.insertAdjacentElement("beforeend",n("style",{text:ei}));n.el("#oo-css-skin",document.documentElement)||document.head.insertAdjacentElement("beforeend",n("style#oo-css-skin",{text:Ii}));var Ci=Object.defineProperty,bi=(t,e,o)=>e in t?Ci(t,e,{enumerable:!0,configurable:!0,writable:!0,value:o}):t[e]=o,E=(t,e,o)=>(bi(t,typeof e!="symbol"?e+"":e,o),o),$t=(t,e,o)=>{if(!e.has(t))throw TypeError("Cannot "+o)},a=(t,e,o)=>($t(t,e,"read from private field"),o?o.call(t):e.get(t)),h=(t,e,o)=>{if(e.has(t))throw TypeError("Cannot add the same private member more than once");e instanceof WeakSet?e.add(t):e.set(t,o)},f=(t,e,o,s)=>($t(t,e,"write to private field"),s?s.call(t,o):e.set(t,o),o),g=(t,e,o)=>($t(t,e,"access private method"),o),we,yi=new Uint8Array(16);function Fi(){if(!we&&(we=typeof crypto<"u"&&crypto.getRandomValues&&crypto.getRandomValues.bind(crypto)||typeof msCrypto<"u"&&typeof msCrypto.getRandomValues=="function"&&msCrypto.getRandomValues.bind(msCrypto),!we))throw new Error("crypto.getRandomValues() not supported. See https://github.com/uuidjs/uuid#getrandomvalues-not-supported");return we(yi)}const vi=/^(?:[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}|00000000-0000-0000-0000-000000000000)$/i;function xi(t){return typeof t=="string"&&vi.test(t)}var C=[];for(var Ve=0;Ve<256;++Ve)C.push((Ve+256).toString(16).substr(1));function Di(t){var e=arguments.length>1&&arguments[1]!==void 0?arguments[1]:0,o=(C[t[e+0]]+C[t[e+1]]+C[t[e+2]]+C[t[e+3]]+"-"+C[t[e+4]]+C[t[e+5]]+"-"+C[t[e+6]]+C[t[e+7]]+"-"+C[t[e+8]]+C[t[e+9]]+"-"+C[t[e+10]]+C[t[e+11]]+C[t[e+12]]+C[t[e+13]]+C[t[e+14]]+C[t[e+15]]).toLowerCase();if(!xi(o))throw TypeError("Stringified UUID is invalid");return o}function Ni(t,e,o){t=t||{};var s=t.random||(t.rng||Fi)();if(s[6]=s[6]&15|64,s[8]=s[8]&63|128,e){o=o||0;for(var i=0;i<16;++i)e[o+i]=s[i];return e}return Di(s)}const We={};function Yi(t,e,o,s){if(o&&We[o])return We[o];const i=document.createElement("template");i.innerHTML=t;const r=i.content;if(e){const A=document.createElement("style");A.textContent=e,r.prepend(A)}if(s){const A=document.querySelector("#oo-css-skin").cloneNode(!0);A.removeAttribute("id"),r.prepend(A)}return document.body.appendChild(i),o&&(We[o]=i.content),i.content}var z,tt,Go,ot,Jo,st,_o,it,Po;class S extends HTMLElement{constructor(){super(),h(this,tt),h(this,ot),h(this,st),h(this,it),h(this,z,null)}_setEvent(){}_afterRender(){}_render(e){}_connected(){}_disconnected(){}_initialize(e,o,s,i){this._props=o?this._getProps(o.prop):{};const r=Yi(s,i,e,!0);this._useTemplate(r),this._render(e),this._setEvent(),this._afterRender(),g(this,tt,Go).call(this)}_getProps(e){const o=Ho(e);return this.getAttributeNames().forEach(s=>{const i=le(s);if(o.hasOwnProperty(i)){const r=this.getAttribute(s);o[i]=oe(o[i])==="boolean"?!!r&&r!=="false":r}}),o}_useCss(e){const o=document.createElement("style");return o.textContent=e,this._content.insertAdjacentElement("beforebegin",o),o}_useCssLink(e){const o=document.createElement("link");return o.rel="stylesheet",o.type="text/css",o.charSet="UTF-8",o.href=e,this._content.insertAdjacentElement("beforebegin",o),o}useCss(e){this.styleNode&&(this.styleNode.remove(),this.styleNode=null),e&&(this.styleNode=this._useCss(e))}useCssLink(e){this.styleNode&&(this.styleNode.remove(),this.styleNode=null),e&&(this.styleNode=this._useCssLink(e))}_useTemplate(e,o="open"){this.attachShadow({mode:o}).appendChild(e.cloneNode(!0)),g(this,ot,Jo).call(this)}connectedCallback(){this._connected()}disconnectedCallback(){this._disconnected()}attributeChangedCallback(e,o,s){const i=le(e);typeof this._props[i]=="boolean"?this._props[i]=s!=="null"&&s!=="false":this._props[i]=s,g(this,st,_o).call(this,i,o)}_setProps(e,o){this._setPropMap[e]?this._setPropMap[e](o):this._setPropMap.$default(e,o)}_useSkin(e){if(a(this,z)&&a(this,z).remove(),f(this,z,null),e){const o=e.split(/\s*;\s*/g);f(this,z,n("style"));let s="";o.forEach(i=>{const r=i.split(/\s*:\s*/g);s+=`--${r[0]}: ${r[1]};
	`}),a(this,z).textContent=`
.content{
	${s}
}
`,this._content.insertAdjacentElement("beforebegin",a(this,z))}}_fillContent(e){new MutationObserver(function(o){o.forEach(s=>{s.addedNodes.forEach(i=>{i.nodeType===Node.ELEMENT_NODE&&i.setAttribute("slot",e)})})}).observe(this,{subtree:!1,childList:!0,attributes:!1,characterData:!1}),g(this,it,Po).call(this,e)}}z=new WeakMap,tt=new WeakSet,Go=function(){Object.keys(this._props).forEach(t=>{let e=t;for(;this[e]||this.hasOwnProperty(e);)e=`_${e}`;Object.defineProperty(this,e,{get:()=>this._props[e],set:o=>{debugger;o!==this._props[e]&&this.setAttribute(x(e),o)}})})},ot=new WeakSet,Jo=function(){for(const t in this._elements)this._elements[t]=this.shadowRoot.querySelector(`.${t}`);this._content=this.shadowRoot.querySelector(".content")},st=new WeakSet,_o=function(t,e){(t?[t]:Object.keys(this._props)).forEach(o=>{this._setProps(o,e)})},it=new WeakSet,Po=function(t){let e=this.firstElementChild;for(;e;)e.setAttribute("slot",t),e=e.nextElementSibling};const Ui=`<div class="content">\r
	<label>\r
		<slot name="before-outer"></slot>\r
		<div class="label">\r
			<div class="labelText hide"></div>\r
			<slot name="label"></slot>\r
		</div>\r
		<div class="box">\r
			<slot name="before-inner-before"></slot>\r
			<div class="prefix"></div>\r
			<slot name="before-inner-after"></slot>\r
			<input class="input" />\r
			<slot name="after-inner-before"></slot>\r
			<div class="suffix"></div>\r
			<slot name="after-inner-after"></slot>\r
		</div>\r
		<slot name="after-outer"></slot>\r
	</label>\r
</div>\r
`,Oo=`* {\r
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
}\r
.content {\r
    height: 100%;\r
    width: 100%;\r
    padding: 2px;\r
    position: relative;\r
}\r
label{\r
    display: flex;\r
    align-items: baseline;\r
    position: relative;\r
    height: 100%;\r
}\r
.label {\r
    white-space: nowrap;\r
    transition: color 0.3s;\r
    display: inline-flex;\r
    align-items: center;\r
    color: var(--label);\r
}\r
.labelText{\r
    padding: 0 0.35em;\r
}\r
.hide{\r
    display: none;\r
}\r
.box {\r
    /*border-width: 1px;*/\r
    /*border-style: solid;*/\r
    /*border-color: var(--border);*/\r
    border-radius: var(--radius);\r
    box-shadow: 0 0 1px 1px var(--border);\r
    display: inline-flex;\r
    align-items: baseline;\r
    height: 100%;\r
    width: 100%;\r
    background: #ffffff;\r
    transition: border-color 0.3s, box-shadow 0.3s;\r
}\r
input {\r
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
div.left-icon{\r
    width: 1em;\r
    text-align: right;\r
    transition: color 0.3s;\r
    font-size: 0.9em;\r
    margin-left: 0.6em;\r
    color: var(--icon-left);\r
}\r
div.right-icon{\r
    width: 1em;\r
    text-align: left;\r
    transition: color 0.3s;\r
    font-size: 0.9em;\r
    margin-right: 0.6em;\r
    color: var(--icon-right);\r
}\r
\r
\r
.box.focus {\r
    /*border-color: var(--focus);*/\r
    box-shadow: 0 0 2px 1px var(--focus);\r
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
`,Lo=class Vo extends S{constructor(){super(),E(this,"_elements",{label:null,input:null,box:null,prefix:null,suffix:null,labelText:null}),E(this,"_setPropMap",{leftIcon:e=>{this._props.leftIcon!==e&&(n.removeClass(this._elements.prefix,`ooicon-${e} left-icon`),this._props.leftIcon&&n.addClass(this._elements.prefix,`ooicon-${this._props.leftIcon} left-icon`))},rightIcon:e=>{this._props.rightIcon!==e&&(n.removeClass(this._elements.suffix,`ooicon-${e} right-icon`),this._props.rightIcon&&n.addClass(this._elements.suffix,`ooicon-${this._props.rightIcon} right-icon`))},width:()=>{this._props.width&&(this._elements.box.style.width=this._props.width)},height:()=>{this._props.height&&(this._elements.box.style.height=this._props.height)},style:()=>{this._setPropMap.width(),this._setPropMap.height()},inputStyle:()=>{n.toggleAttr(this._elements.input,"style",this._props.inputStyle)},bgcolor:()=>{this._props.bgcolor?n.setStyle(this._elements.box,"background-color",this._props.bgcolor):n.setStyle(this._elements.box,"background-color","transparent")},disabled:()=>{n.toggleAttr(this._elements.input,"disabled",this._props.disabled),n.checkClass(this._elements.box,"disabled",this._props.disabled),n.checkClass(this._elements.label,"disabled",this._props.disabled)},label:()=>{this._props.label?(this._elements.labelText.textContent=this._props.label,n.removeClass(this._elements.labelText,"hide")):(this._elements.labelText.textContent="",n.addClass(this._elements.labelText,"hide"))},skin:()=>{this._useSkin(this._props.skin)},value:()=>{this._elements.input.value=this._props.value},$default:e=>{e==="value"?this.value=this._props[e]:n.toggleAttr(this._elements.input,e,this._props[e])}})}static get observedAttributes(){return Object.keys(this.prop).map(e=>x(e))}_init(e,o,s,i){this._initialize(e,o||Vo,s||Ui,i||Oo)}_setEvent(){super._setEvent(),this.shadowRoot.addEventListener("click",e=>{e.target.tagName!=="INPUT"&&e.stopPropagation()}),this._elements.input.addEventListener("focus",()=>{n.addClass(this._elements.box,"focus"),n.addClass(this._elements.label,"focus")}),this._elements.input.addEventListener("blur",()=>{n.removeClass(this._elements.box,"focus"),n.removeClass(this._elements.label,"focus")}),this._elements.input.addEventListener("input",()=>{this.value=this._elements.input.value}),this._elements.input.addEventListener("change",e=>{this.dispatchEvent(new Event("change",e))})}focus(){this._elements.input.focus()}blur(){this._elements.input.blur()}get value(){return this._elements.input.value}};E(Lo,"prop",{leftIcon:"",rightIcon:"",width:"",height:"",style:"",inputStyle:"",disabled:!1,label:"",bgcolor:"#ffffff",skin:"",id:"",placeholder:"",readonly:!1,type:"text",autofocus:!1,form:"",max:"",maxlength:"",min:"",minlength:"",name:"",pattern:"",size:"",spellcheck:"",src:"",step:"",tabindex:"",value:"",title:"",list:"",autocomplete:"",accept:"",alt:"",capture:"",checked:"",dirname:"",formaction:"",formenctype:"",formmethod:"",formnovalidate:"",formtarget:"",multiple:"",required:""});let Zt=Lo;class Si extends Zt{constructor(){super(),this._init("oo-input")}}customElements.define("oo-input",Si);const Ti=`<div class="button content">\r
	<div class="prefix"></div>\r
	<div class="text"></div>\r
	<div class="suffix"></div>\r
</div>\r
`,Ri=`body{\r
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
    --hover-bg: var(--oo-color-main-light);\r
    --active-bg: var(--oo-color-main-deep);\r
    --focus-bg: var(--oo-color-main-light);\r
\r
    --cancel-hover-bg: var(--oo-color-gray-d1);\r
    --cancel-active-bg: var(--oo-color-gray-b);\r
    --cancel-focus-bg: var(--oo-color-gray1);\r
}\r
\r
.button {\r
    border-radius: var(--radius);\r
    border: 0;\r
    padding: 0.375em 0.75em;\r
    cursor: pointer;\r
    color: var(--color);\r
    background-color: var(--bg);\r
    transition: background 0.5s;\r
    text-align: center;\r
    line-height: 1em;\r
    height: 100%;\r
    width: 100%;\r
}\r
.button.cancel{\r
    background-color: var(--cancel-bg);\r
    color: var(--cancel-color);\r
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
    justify-content: center;\r
}\r
\r
.content>.icon{\r
    display: inline-block;\r
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
`;var T,rt,Wo,Me,nt;const Ko=class Xo extends S{constructor(){super(),h(this,rt),h(this,Me),E(this,"_elements",{text:null,prefix:null,suffix:null,button:null}),h(this,T,{}),E(this,"_setPropMap",{leftIcon:e=>{n.removeClass(this._elements.prefix,`ooicon-${e} icon`),this._props.leftIcon&&n.addClass(this._elements.prefix,`ooicon-${this._props.leftIcon} icon`)},rightIcon:e=>{n.removeClass(this._elements.suffix,`ooicon-${e} icon`),this._props.rightIcon&&n.addClass(this._elements.suffix,`ooicon-${this._props.rightIcon} icon`)},style:()=>{n.toggleAttr(this._elements.button,"style",this._props.style)},disabled:e=>{debugger;n.checkClass(this._elements.button,"disabled",this._props.disabled),n.toggleAttr(this._elements.button,"disabled",this._props.disabled),g(this,rt,Wo).call(this,this._props.disabled)},type:e=>{this._props.type||(this._props.type="default"),e&&n.removeClass(this._elements.button,this._props.type),n.addClass(this._elements.button,this._props.type)},text:()=>{this.innerHTML.trim()||(this._elements.text.textContent=this._props.text)},skin:()=>{this._useSkin(this._props.skin)},$default:e=>{e==="value"&&(this.value=this._props[e]),n.toggleAttr(this._elements.button,e,this._props[e])}})}static get observedAttributes(){return Object.keys(this.prop).map(e=>x(e))}_init(e,o,s,i){this._initialize(e,o||Xo,s||Ti,i||Ri)}_render(){const e=this;new MutationObserver(function(){var o;g(o=e,Me,nt).call(o)}).observe(this,{subtree:!0,childList:!0,attributes:!0,characterData:!0}),g(this,Me,nt).call(this)}_connected(){}};T=new WeakMap,rt=new WeakSet,Wo=function(t){if(t){f(this,T,a(this,T)||{});for(let e in this)e.startsWith("on")&&this[e]&&(a(this,T)[e]=this[e],this[e]=null);this.style.pointerEvents="none"}else{if(a(this,T))for(let e in a(this,T))a(this,T)[e]&&(this[e]=a(this,T)[e],a(this,T)[e]=null);this.style.pointerEvents="auto"}},Me=new WeakSet,nt=function(){this.innerHTML.trim()&&(n.empty(this._elements.text),this._elements.text.insertAdjacentHTML("beforeend",this.innerHTML))},E(Ko,"prop",{leftIcon:"",rightIcon:"",style:"",disabled:!1,text:"",type:"",skin:""});let $o=Ko;class ki extends $o{constructor(){super(),this._init("oo-button")}}customElements.define("oo-button",ki);const ji=`<label class="content">\r
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
	<input class="input"/>\r
</label>\r
`,Hi=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    --border: var(--oo-color-gray-9);\r
    --check: var(--oo-color-gray1);\r
    --bg: var(--oo-color-text-white);\r
    --hover: var(--oo-color-main);\r
    --checked: var(--oo-color-main);\r
    --disabled: var(--oo-color-gray2);\r
    --radius: var(--oo-default-radius);\r
}\r
label {\r
    display: flex;\r
    align-items: center;\r
    cursor: pointer;\r
    padding-right: 1em;\r
    word-break: keep-all;\r
}\r
.button{\r
    border-radius: var(--radius);\r
    width: 1em;\r
    height: 1em;\r
    border: 2px solid var(--border);\r
    background-color: var(--bg);\r
    display: flex;\r
    justify-content: center;\r
    align-items: center;\r
    transition: border 0.3s, color 0.3s, background-color 0.3s;\r
    color: var(--oo-color-gray-9);\r
    position: relative;\r
}\r
.button:hover{\r
    border: 2px solid var(--hover);\r
}\r
.button.checked{\r
    color: var(--checked);\r
    border: 2px solid var(--checked);\r
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
    transition: width 0.3s, height 0.3s, background 0.3s;\r
}\r
.check>div{\r
    scale: 0.5;\r
}\r
.check.checked{\r
    width: 100%;\r
    height: 100%;\r
    color: var(--bg);\r
    background-color: var(--checked);\r
}\r
.check.disabled{}\r
.check.checked.disabled{}\r
\r
.label{\r
    display: flex;\r
    align-items: center;\r
    padding-left: 0.3em;\r
    transition: color 0.3s;\r
}\r
\r
.label.checked{\r
    color: var(--checked);\r
}\r
.label.disabled{\r
    opacity: 0.4;\r
    cursor: not-allowed;\r
}\r
.input{\r
    display: none;\r
}\r
`,Zo=class qo extends S{constructor(){super(),E(this,"_elements",{label:null,button:null,text:null,slot:null,check:null,input:null,checkIcon:null}),E(this,"_setPropMap",{text:()=>{this._elements.text.textContent=this._props.text},value:()=>{this._elements.input.value=this._props.value,this._elements.input.setAttribute("value",this._props.value)},name:e=>{e!==this._props.name&&(this._elements.input.name=this._props.name,this._elements.input.setAttribute("name",this._props.name),this.setAttribute("name",this._props.name))},checked:()=>{debugger;this._elements.input.checked=!!this._props.checked,this._render()},disabled:()=>{this._elements.input.disabled=!!this._props.disabled,this._render()},size:()=>{this._props.size?n.setStyle(this._elements.button,"font-size",this._props.size):n.setStyle(this._elements.button,"font-size","auto")},skin:()=>{this._useSkin(this._props.skin)},$default:()=>{}}),E(this,"group",null)}static get observedAttributes(){return Object.keys(this.prop).map(e=>x(e))}_init(e,o,s,i){this._initialize(e,o||qo,s||ji,i||Hi)}_setEvent(){super._setEvent(),this._elements.input.addEventListener("change",e=>{this.checked=this._elements.input.checked,this.dispatchEvent(new Event("change",e))})}_unchecked(){this._elements.input.checked=!1,this._props.checked=!1,this._render()}_render(){let e=this.firstChild;for(;e;)this._elements.slot.appendChild(e),e=this.firstChild;const o=this._elements.input.checked;n.checkClass(this._elements.button,"checked",o),n.checkClass(this._elements.check,"checked",o),n.checkClass(this._elements.label,"checked",o);const s=this._elements.input.disabled;n.checkClass(this._elements.button,"disabled",s),n.checkClass(this._elements.check,"disabled",s),n.checkClass(this._elements.label,"disabled",s)}_connected(){debugger;const e=this.tagName.toLowerCase()+"-group",o=n.getParent(this,e);o&&(this.group=o,setTimeout(()=>{var s;(s=o._appendItem)==null||s.call(o,this)},10))}_disconnected(){if(this.group){const e=this.group;this.group=null,e._removeItem(this)}}get checked(){return this._elements.input.checked}set checked(e){const o=this.checked!==e;this._elements.input.checked=!!e,this._render(),o&&this.dispatchEvent(new Event("change"))}};E(Zo,"prop",{checked:!1,disabled:!1,text:"",value:"",name:"",size:"",skin:""});let es=Zo;const zi=`.check{\r
    border-radius: var(--radius);\r
    width: 0;\r
    height: 0;\r
    transition: width 0.3s, height 0.3s, background-color 0.3s;\r
    transform-origin: center;\r
    background-color: var(--bg);\r
}\r
.check.checked{\r
    width: 0.5em;\r
    height: 0.5em;\r
    background-color: var(--checked);\r
}\r
`;class Gi extends es{constructor(){super(),this._init("oo-radio"),this._useCss(zi)}_unCheckedOther(){debugger;this.ownerDocument.querySelectorAll(`oo-radio[name='${this._props.name}']`).forEach(e=>{e!==this&&e.checked&&e._unchecked()})}_render(){this._elements.input.type="radio",this._elements.input.checked&&this._unCheckedOther(),super._render()}}customElements.define("oo-radio",Gi);const Ji=`<div class="group content">\r
	<slot name="before"></slot>\r
	<div class="label">\r
		<div class="labelText"></div>\r
		<slot name="label"></slot>\r
	</div>\r
	<slot name="items" class="items hide"></slot>\r
	<slot name="after"></slot>\r
</div>\r
`,_i=`* {\r
    box-sizing: border-box;\r
}\r
.group {\r
    display: flex;\r
    align-items: center;\r
}\r
.label {\r
    padding: 0 0.35em;\r
    white-space: nowrap;\r
    transition: color 0.5s;\r
    display: flex;\r
    align-items: center;\r
}\r
.items{\r
    display: block;\r
}\r
.hide{\r
    visibility: hidden;\r
}\r
`;var O,At,Bo,re,ne,Ie,ct,Eo,at,uo;const ts=class os extends S{constructor(){super(),h(this,At),h(this,ne),h(this,ct),h(this,at),E(this,"_items",[]),h(this,O,0),E(this,"_elements",{label:null,group:null,labelText:null,items:null}),E(this,"_setPropMap",{name:()=>{this._props.name&&this._items.forEach(e=>{e.setAttribute("name",this._props.name)})},skin:()=>{this._items.forEach(e=>{e.setAttribute("skin",this._props.skin)})},disabled:()=>{this._items.forEach(e=>{this._props.disabled?e.setAttribute("disabled",!0):e.disabled||e.removeAttribute("disabled")})},size:()=>{this._props.size&&this._items.forEach(e=>{e.setAttribute("size",this._props.size)})},col:()=>{g(this,ne,Ie).call(this)},label:()=>{this._props.label?(this._elements.labelText.textContent=this._props.label,n.removeClass(this._elements.labelText,"hide")):(this._elements.labelText.textContent="",n.addClass(this._elements.labelText,"hide"))},$default:()=>{}}),h(this,re,null)}static get observedAttributes(){return Object.keys(this.prop).map(e=>x(e))}_init(e,o,s,i){this._initialize(e,o||os,s||Ji,i||_i)}_render(){this._props.name||(this._props.name=Ni()),parseInt(this._props.col)||n.removeClass(this._elements.items,"hide"),this._itemChangeFun=this._itemChange.bind(this)}_removeItem(e){this._items.splice(this._items.indexOf(e),1),e.removeAttribute("slot"),e.removeEventListener("change",this._itemChangeFun),n.setStyle(e,"width","auto"),g(this,ne,Ie).call(this)}_appendItem(e){this._items.push(e),e.setAttribute("slot","items"),e.setAttribute("name",this._props.name),e.setAttribute("skin",this._props.skin),this._props.disabled?e.setAttribute("disabled",!0):e.disabled||e.removeAttribute("disabled"),this._props.size&&e.setAttribute("size",this._props.size),e.addEventListener("change",this._itemChangeFun),this._appendItemCheckValue(e),g(this,ne,Ie).call(this,!0),this._setPropMap.value()}_appendItemCheckValue(e){this._props.values&&this._props.values.length&&this._props.values.includes(e.value)&&(e.checked=!0)}_itemChange(e){this.dispatchEvent(new Event("change",e))}};O=new WeakMap,At=new WeakSet,Bo=function(){f(this,O,0),this._items.forEach(t=>{n.setStyle(t,"width","auto");const e=n.getSize(t);e.x>a(this,O)&&f(this,O,e.x)})},re=new WeakMap,ne=new WeakSet,Ie=function(){a(this,re)&&clearTimeout(a(this,re)),f(this,re,setTimeout(()=>{g(this,ct,Eo).call(this)},10))},ct=new WeakSet,Eo=function(){g(this,at,uo).call(this);const t=parseInt(this._props.col);t&&(g(this,At,Bo).call(this),this._items.forEach((e,o)=>{a(this,O)&&n.setStyle(e,"width",a(this,O)+"px"),(o+1)%t===0&&e.insertAdjacentElement("afterend",n("br.group-col-separator",{slot:"items"}))}),n.removeClass(this._elements.items,"hide"))},at=new WeakSet,uo=function(){this.querySelectorAll("br.group-col-separator").forEach(t=>{t.remove()})},E(ts,"prop",{disabled:!1,value:"",name:"",size:"",col:"",label:"",skin:""});let ss=ts;var lt,is;class Pi extends ss{constructor(){super(),h(this,lt),g(this,lt,is).call(this),this._init("oo-radio-group")}_appendItemCheckValue(e){this._props.value?e.value===this._props.value&&e.setAttribute("checked",!0):e._unchecked()}_itemChange(e){e.currentTarget.checked?this.value=e.currentTarget.value:this.value="",this.dispatchEvent(new Event("change",e))}_removeItem(e){super._removeItem(e)}}lt=new WeakSet,is=function(){this.setPropMap=Object.assign(this._setPropMap,{value:()=>{if(this._props.value){for(const t of this._items)if(t.value===this._props.value){t.setAttribute("checked",!0);break}}}})};customElements.define("oo-radio-group",Pi);class Oi extends es{constructor(){super(),this._init("oo-checkbox")}_render(){this._elements.input.type="checkbox",n.addClass(this._elements.checkIcon,"ooicon-checkmark"),super._render()}}customElements.define("oo-checkbox",Oi);var ht,rs,dt,ns;class Li extends ss{constructor(){super(),h(this,ht),h(this,dt),g(this,ht,rs).call(this),this._init("oo-checkbox-group")}get value(){return g(this,dt,ns).call(this)}set value(e){this._props.values!==e&&(this._props.values=e,this._setPropMap.values())}}ht=new WeakSet,rs=function(){this._setPropMap=Object.assign(this._setPropMap,{value:()=>{this._props.value&&(!this._props.values||!this._props.values.length)&&(this._props.values=this._props.value.split(/\s*,\s*/g),this._setPropMap.values())},values:()=>{if(this._props.values&&this._props.values.length)for(const t of this._items)this._props.values.includes(t.value)?t.checked=!0:t._unchecked();else for(const t of this._items)t._unchecked()}})},dt=new WeakSet,ns=function(){const t=[];return this._items.forEach(e=>{e.checked&&t.push(e.value)}),t};customElements.define("oo-checkbox-group",Li);const Vi=`.content{\r
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
}\r
\r
.drop{\r
    text-align: left;\r
    transition: color 0.3s;\r
    font-size: 0.8em;\r
    margin-right: 0.6em;\r
    color: var(--label);\r
    cursor: pointer;\r
    transform-origin: center;\r
}\r
.drop.down{\r
    transform: rotate(180deg);\r
}\r
\r
.drop.disabled{\r
    opacity: 0.8;\r
    cursor: not-allowed;\r
}\r
\r
.box.focus .drop{\r
    color: var(--focus);\r
}\r
\r
.options-area{\r
    position: absolute;\r
    border: 1px solid var(--option-border);\r
    background-color: var(--option-bg);\r
    border-radius: var(--oo-area-radius);\r
    box-shadow: var(--option-shadow);\r
    z-index: 100;\r
    transform-origin: top;\r
    transition: height 0.3s, opacity 0.3s, transform 0.3s;\r
}\r
.options-area.visible{\r
    transform: scale(1);\r
    opacity: 1;\r
}\r
.options-area.invisible{\r
    transform: scale(1, 0);\r
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
    height: 17em;\r
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
.input{\r
    cursor: pointer;\r
}\r
`;var gt,As,U,$,v,Ae,X,Bt,po,Ce,Ke,Et,cs,he,Te;class Wi extends Zt{constructor(){super(),h(this,gt),h(this,Bt),h(this,Ce),h(this,Et),h(this,he),h(this,U,!1),h(this,$,[]),h(this,v,null),h(this,Ae,null),h(this,X,null),g(this,gt,As).call(this),this._init("oo-select"),this._useCss(Vi)}_render(){f(this,U,!1),this._elements.drop=n("div.drop.ooicon-drop_down"),this._elements.input.insertAdjacentElement("afterend",this._elements.drop),f(this,v,this.querySelector("div.options-area")),a(this,v)||(f(this,v,n("div.options-area.invisible")),n.set(a(this,v),"html",'<div class="arrow"></div><div class="options-content"><slot name="items"></slot></div>'),this._content.insertAdjacentElement("beforeend",a(this,v))),this._props.readonly=!0}_removeItem(e){a(this,$).splice(a(this,$).indexOf(e),1),e.removeAttribute("slot")}_appendItem(e){a(this,$).push(e),e.setAttribute("slot","items"),e.setAttribute("skin",this._props.skin),this._props.disabled?e.setAttribute("disabled",!0):e.disabled||e.removeAttribute("disabled"),this._props.size&&e.setAttribute("size",this._props.size),this._appendItemCheckValue(e),this._setPropMap.$default("value")}_appendItemCheckValue(e){this._props.value&&e.value===this._props.value&&(e.selected=!0)}_selected(e){if(e.selected){a(this,X)&&a(this,X)!==e&&(a(this,X).selected=!1),f(this,X,e);const o=e._props.text||e._elements.label.textContent;this._elements.input.value=o,this._props.value=o}g(this,he,Te).call(this)}_setEvent(){super._setEvent(),this._elements.input.addEventListener("click",()=>{f(this,U,!a(this,U)),a(this,U)?g(this,Et,cs).call(this):g(this,he,Te).call(this)}),a(this,v).addEventListener("click",e=>{e.stopPropagation()}),a(this,v).addEventListener("mousedown",e=>{e.stopPropagation()}),this._content.firstElementChild.addEventListener("mousedown",e=>{e.stopPropagation()})}}gt=new WeakSet,As=function(){this._setPropMap=Object.assign(this._setPropMap,{disabled:()=>{n.toggleAttr(this.elements.input,"disabled",this.props.disabled),n.checkClass(this.elements.box,"disabled",this.props.disabled),n.checkClass(this.elements.label,"disabled",this.props.disabled),n.checkClass(this.elements.drop,"disabled",this.props.disabled)},value:()=>{if(this._props.value){for(const t of a(this,$))if(t.value===this._props.value){t.selected=!0;break}}}})},U=new WeakMap,$=new WeakMap,v=new WeakMap,Ae=new WeakMap,X=new WeakMap,Bt=new WeakSet,po=function(){const t=n.getPosition(this._elements.box,this._content),e=n.getSize(this._elements.box),o=t.x,s=t.y+e.y+6+e.y*.2,i=e.x;n.setStyles(a(this,v),{top:s+"px",left:o+"px",width:i+"px"});const r=i/2-6;n.setStyles(a(this,v).querySelector(".arrow"),{left:r+"px"})},Ce=new WeakSet,Ke=function(){n.checkClass(this._elements.drop,"down",a(this,U)),n.checkClass(this._elements.box,"focus",a(this,U)),n.checkClass(this._elements.label,"focus",a(this,U))},Et=new WeakSet,cs=function(){f(this,U,!0),g(this,Ce,Ke).call(this),g(this,Bt,po).call(this),n.removeClass(a(this,v),"invisible"),n.addClass(a(this,v),"visible"),this.ownerDocument.dispatchEvent(new MouseEvent("mousedown")),f(this,Ae,g(this,he,Te).bind(this)),this.ownerDocument.addEventListener("mousedown",a(this,Ae))},he=new WeakSet,Te=function(){f(this,U,!1),g(this,Ce,Ke).call(this),n.removeClass(a(this,v),"visible"),n.addClass(a(this,v),"invisible"),this.ownerDocument.removeEventListener("mousedown",a(this,Ae))};customElements.define("oo-select",Wi);const Ki=`.content{\r
    padding: 0 1em;\r
    background-color: var(--option-bg);\r
    line-height: 2em;\r
    overflow: hidden;\r
    transition: background-color 0.3s, color 0.3s;\r
    cursor: pointer;\r
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
.label, .text{\r
    line-height: 2em;\r
    overflow: hidden;\r
    word-break: keep-all;\r
    text-overflow: ellipsis;\r
}\r
`,Xi='<div class="content"><div class="text"></div><div class="label"></div></div>';var be,ut,j;const as=class ls extends S{constructor(){super(),h(this,be),E(this,"_setPropMap",{value:()=>{},$default:()=>{},text:()=>{g(this,be,ut).call(this)},disabled:()=>{n.checkClass(this._content,"disabled",!!this._props.disabled)},selected:()=>{n.checkClass(this._content,"selected",this._props.selected),a(this,j)._selected(this)},skin:()=>{this._useSkin(this._props.skin)}}),E(this,"_elements",{text:null,label:null}),h(this,j,null),this._initialize("oo-option",ls,Xi,Ki)}static get observedAttributes(){return Object.keys(this.prop).map(e=>x(e))}_render(){g(this,be,ut).call(this)}_connected(){const e=this.parentElement;e.tagName.toLowerCase()==="oo-select"&&f(this,j,e),a(this,j)?(a(this,j)._appendItem(this),n.show(this)):n.hide(this)}_disconnected(){if(a(this,j)){const e=a(this,j);f(this,j,null),e._removeItem(this)}}_setEvent(){this._content.addEventListener("click",()=>{this._props.disabled||(this.selected=!0)})}};be=new WeakSet,ut=function(){if(this.innerHTML.trim()){let t=this.firstChild;for(;t;)this._elements.label.appendChild(t),t=this.firstChild}this._elements.label.innerHTML.trim()||(this._elements.text.textContent=this._props.text)},j=new WeakMap,E(as,"prop",{value:"",text:"",selected:!1,disabled:!1,skin:""});let $i=as;customElements.define("oo-option",$i);const Zi=`<div class="button content">\r
	<div class="box">\r
		<div class="menu ooicon-menu hide"></div>\r
		<div class="prefix"></div>\r
		<div class="text"></div>\r
		<div class="suffix"></div>\r
		<div class="close ooicon-close"></div>\r
	</div>\r
</div>\r
`,qi=`body{\r
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
    padding: 0.5em 0.875em;\r
    cursor: pointer;\r
    color: var(--color);\r
    background-color: var(--bg);\r
    transition: background 0.3s, color 0.3s;\r
    text-align: center;\r
    line-height: 1em;\r
    height: 100%;\r
    width: 100%;\r
    display: flex;\r
    align-items: center;\r
}\r
.button.current{\r
    background-color: var(--current-bg);\r
    color: var(--current-color);\r
}\r
.box>div{\r
    /*margin: 0 0.09375em;*/\r
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
.box>.icon{\r
    display: inline-block;\r
}\r
\r
\r
.close{\r
    display: inline-block;\r
    border-radius: 100%;\r
    font-size: 0.8em;\r
}\r
.menu{\r
    display: none;\r
    border-radius: 100%;\r
    font-size: 0.6em;\r
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
`;var pt,hs;const Qt=class ds extends $o{constructor(){super(),h(this,pt),E(this,"_elements",{text:null,prefix:null,suffix:null,button:null,close:null,menu:null}),g(this,pt,hs).call(this),this._init("oo-tag",ds,Zi,qi)}_setEvent(){this._elements.close&&this._elements.close.addEventListener("click",e=>{this.shadowRoot.dispatchEvent(new MouseEvent("close",e)),e.stopPropagation()}),this._elements.menu&&this._elements.menu.addEventListener("click",e=>{this.shadowRoot.dispatchEvent(new MouseEvent("menu",e)),e.stopPropagation()})}};pt=new WeakSet,hs=function(){this.setPropMap=Object.assign(this._setPropMap,{close:()=>{this._props.close==="on"?n.removeClass(this._elements.close,"hide"):n.addClass(this._elements.close,"hide")},menu:()=>{this._props.menu==="on"?n.removeClass(this._elements.menu,"hide"):n.addClass(this._elements.menu,"hide")},type:t=>{this._props.type||(this._props.type="default"),t&&n.removeClass(this._elements.button,t),n.addClass(this._elements.button,this._props.type)}})},E(Qt,"prop",{leftIcon:"",rightIcon:"",style:"",disabled:!1,text:"",type:"default",close:"on",menu:"off",skin:""}),E(Qt,"events",{close:new Event("close",{composed:!0}),menu:new Event("menu",{composed:!0})});let er=Qt;customElements.define("oo-tag",er);const tr=`.content {\r
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
    transition: background-color 0.3s, color 0.3s;\r
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
`,or='<div class="capsulae content"></div>';var wt,gs;const Bs=class Es extends S{constructor(){super(),h(this,wt),E(this,"_setPropMap",{skin:()=>{this._useSkin(this._props.skin)}}),this._init("oo-capsulae")}static get observedAttributes(){return Object.keys(this.prop).map(e=>x(e))}_init(e,o,s,i){this._initialize(e,o||Es,s||or,i||tr)}_render(){new MutationObserver(function(e){e.forEach(o=>{o.addedNodes.forEach(s=>{s.nodeType===Node.ELEMENT_NODE&&this._content.appendChild(s)})})}).observe(this,{subtree:!1,childList:!0,attributes:!1,characterData:!1}),g(this,wt,gs).call(this)}};wt=new WeakSet,gs=function(){let t=this.firstElementChild;for(;t;)this._content.appendChild(t),t=this.firstElementChild},E(Bs,"prop",{skin:""});let sr=Bs;customElements.define("oo-capsulae",sr);const ir=(t,e)=>{const o=t[e];return o?typeof o=="function"?o():Promise.resolve(o):new Promise((s,i)=>{(typeof queueMicrotask=="function"?queueMicrotask:setTimeout)(i.bind(null,new Error("Unknown variable dynamic import: "+e)))})},rr=`<div class="container info">\r
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
`;var G,mt,us,ft,ps;const Qs=class ws extends S{constructor(){super(),h(this,mt),h(this,ft),E(this,"_elements",{container:null,content:null,icon:null,title:null,text:null,close:null}),h(this,G,null),E(this,"_setPropMap",{title:()=>{this._elements.title.textContent=this._props.title},text:()=>{this._elements.text.textContent=this._props.text},icon:()=>{n.toggleClass(this._elements.icon,`ooicon-${this._props.icon}`,this._props.icon)},titleStyle:()=>{this._elements.title.setAttribute("style",this._props.titleStyle)},textStyle:()=>{this._elements.text.setAttribute("style",this._props.textStyle)},type:()=>{g(this,ft,ps).call(this)},skin:()=>{g(this,mt,us).call(this).then()},showClose:()=>{n[this._props.showClose?"removeClass":"addClass"](this._elements.close,"hide")},width:()=>{this._props.width&&n.setStyle(this._elements.container,"width",this._props.width)},height:()=>{this._props.width&&n.setStyle(this._elements.container,"height",this._props.height)},contentAlign:()=>{this._props.contentAlign?n.addClass(this._elements.content,"align_"+this._props.contentAlign):n.removeClass(this._elements.content,"align_left align_right align_center")},contentValign:()=>{this._props.contentValign?n.addClass(this._elements.content,"align_"+this._props.contentValign):n.removeClass(this._elements.content,"valign_left valign_right valign_center")},style:()=>{n.toggleAttr(this._elements.container,"style",this._props.style)},$default:e=>{n.toggleAttr(this._elements.container,e,this._props[e])}}),this._initialize("oo-notice",ws,rr)}static get observedAttributes(){return Object.keys(this.prop).map(e=>x(e))}_setEvent(){this._elements.close.addEventListener("click",()=>{this._elements.container.style.opacity=0,this._elements.container.addEventListener("transitionend",()=>{this.remove()})})}};G=new WeakMap,mt=new WeakSet,us=async function(){a(this,G)&&(a(this,G).remove(),f(this,G,null)),f(this,G,document.createElement("style")),a(this,G).textContent=!this._props.skin||this._props.skin==="default"?(await Le(()=>import("./notice.default.scope-f2207f95-34562446.js"),[],import.meta.url)).default:(await ir(Object.assign({"./template/notice.banner.scope.css":()=>Le(()=>import("./notice.banner.scope-1254bf39-e09fc96d.js"),[],import.meta.url),"./template/notice.default.scope.css":()=>Le(()=>import("./notice.default.scope-f2207f95-34562446.js"),[],import.meta.url)}),`./template/notice.${this._props.skin}.scope.css`)).default,this.shadowRoot.prepend(a(this,G))},ft=new WeakSet,ps=function(){const t="container "+this._props.type||"info";if(this._elements.container.setAttribute("class",t),!this._props.icon){const e="icon "+{info:"ooicon-info",error:"ooicon-cancel",warn:"ooicon-error",success:"ooicon-check"}[this._props.type]||"ooicon-info";this._elements.icon.setAttribute("class",e)}},E(Qs,"prop",{title:"",text:"",icon:"",titleStyle:"",textStyle:"",showClose:!0,type:"info",skin:"default",contentAlign:"",contentValign:"",width:"",height:""});let nr=Qs;customElements.define("oo-notice",nr);const Ar=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    /*display: contents;*/\r
    padding: var(--oo-menu-padding);\r
}\r
`,cr=`
<div class="content">
    <slot name="items"></slot>
</div>
`;var L,V,W;const ms=class fs extends S{constructor(){super(),E(this,"_setPropMap",{css:()=>{this.useCss(this._props.css)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},show:()=>{this._props.show&&window.setTimeout(()=>{this.show()},10)}}),h(this,L,null),h(this,V,null),h(this,W,null)}static get observedAttributes(){return Object.keys(this.prop).map(e=>x(e))}_init(e,o,s,i){debugger;this._initialize(e,o||fs,s||cr,i||Ar)}_setEvent(){super._setEvent(),this.shadowRoot.addEventListener("mousedown",e=>{e.stopPropagation()})}show(e,o){!a(this,L)&&!a(this,V)&&(f(this,L,Object.assign({},{scale:0},e)),f(this,V,Object.assign({},{scale:1},o)),n.setStyles(this,{scale:0}),n.addClass(this,"show"),window.setTimeout(()=>{n.addClass(this,"transition"),n.setStyles(this,{scale:1})},10),window.setTimeout(()=>{this.dispatchEvent(new Event("show"))},210),this._addHideEvent())}_addHideEvent(){a(this,W)||f(this,W,()=>{this.hide()}),document.addEventListener("mousedown",a(this,W))}hide(){a(this,L)&&a(this,V)&&(n.setStyles(this,a(this,L)),window.setTimeout(()=>{n.removeClass(this,"transition"),n.removeClass(this,"show"),n.setStyles(this,a(this,V)),f(this,V,null),f(this,L,null),this._afterHide(),this.dispatchEvent(new Event("hide"))},200),this._removeHideEvent())}_afterHide(){}_removeHideEvent(){a(this,W)&&document.removeEventListener("mousedown",a(this,W))}};L=new WeakMap,V=new WeakMap,W=new WeakMap,E(ms,"prop",{css:"",cssLink:"",skin:"",show:!1});let Ms=ms;class qt extends Ms{constructor(){super(),this._init("oo-menu",qt)}}customElements.define("oo-menu",qt);const ar=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    --padding: 0.5em 0.714em;\r
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
`,lr=`
<div class="menuItem content enabled">
    <slot name="left"></slot>
    <slot name="item"></slot>
    <slot name="right"></slot>
</div>
`,Is=class Cs extends S{constructor(){super(),E(this,"_elements",{}),E(this,"_setPropMap",{css:()=>{this.useCss(this._props.css)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},label:()=>{switch(this._props.type){case"command":this._elements.label&&(this._elements.label.textContent=this._props.label);break;case"checkbox":case"radio":this._elements.input&&(this._elements.input.text=this._props.label);break}},disabled:()=>{switch(n.removeClass(this._content,"disabled"),n.addClass(this._content,"enabled"),this._props.type){case"command":this._props.disabled&&(n.removeClass(this._content,"enabled"),n.addClass(this._content,"disabled"));break;case"checkbox":case"radio":this._elements.input.disabled=this._props.disabled,this._props.disabled&&n.removeClass(this._content,"enabled");break}},icon:e=>{this._elements.icon&&(n.removeClass(this._elements.icon,`ooicon-${e}`),this._props.icon&&n.addClass(this._elements.icon,`ooicon-${this._props.icon}`))},radiogroup:e=>{this._elements.radio&&(this._elements.radio.name=this._props.radiogroup)},type:e=>{e!==null&&e!==this._props.type&&this._renderItem()}}),this._initialize("oo-menu-item",Cs,lr,ar)}static get observedAttributes(){return Object.keys(this.prop).map(e=>x(e))}_setEvent(){super._setEvent(),this.addEventListener("click",()=>{if(this.type==="command"){const e=n.getParent(this,"oo-menu");e&&e.hide()}})}_connected(){this.getAttribute("type")||this.setAttribute("type","command"),n.getParent(this,"oo-menu")&&this.setAttribute("slot","items"),this._renderItem()}_renderItem(){switch(n.empty(this),this._elements.icon=null,this._elements.label=null,this._elements.input=null,this._elements.hr=null,this.checkbox=null,this.radio=null,n.addClass(this._content,"enabled"),this._props.type){case"command":this._elements.icon=n("div.icon",{slot:"item"}),this._elements.label=n("div.label",{text:this._props.label,slot:"item"}),this._props.icon&&n.addClass(this._elements.icon,`ooicon-${this._props.icon}`),this.append(this._elements.icon),this.append(this._elements.label);break;case"checkbox":this._elements.input=n("oo-checkbox",{slot:"item"}),this.append(this._elements.input),this._elements.input.text=this._props.label,this.checkbox=this._elements.input;break;case"radio":this._elements.input=n("oo-radio",{slot:"item"}),this.append(this._elements.input),this._elements.input.text=this._props.label,this._elements.input.name=this._props.radiogroup,this.radio=this._elements.input;break;case"hr":this._elements.hr=n("hr",{slot:"item"}),this.append(this._elements.hr),n.removeClass(this._content,"enabled");break}}};E(Is,"prop",{css:"",cssLink:"",skin:"",icon:"",label:"",type:"command",disabled:!1,checked:!1,radiogroup:""});let hr=Is;customElements.define("oo-menu-item",hr);const dr=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    /*padding: var(--oo-menu-padding);*/\r
    width: 100%;\r
    height: 100%;\r
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
.buttons>.show{\r
    display: inline-block;\r
}\r
.header{\r
    height: 3.2em;\r
    padding: 1em;\r
    display: flex;\r
    justify-content: space-between;\r
    align-items: center;\r
    border-top-right-radius: var(--oo-menu-radius);\r
    border-top-left-radius: var(--oo-menu-radius);\r
}\r
.title{\r
    font-size: 1.125em;\r
    width: 100%;\r
}\r
.actions>div{\r
    cursor: pointer;\r
    border-radius: var(--oo-default-radius);\r
    padding: 0.2em;\r
    color: var(--oo-color-text2);\r
    display: none;\r
}\r
.actions>div:hover{\r
    background-color: var(--oo-color-gray-e)\r
}\r
\r
.body{\r
    padding: 0 1em;\r
    height: calc(100% - 3.2em - 4.6em);\r
    overflow: auto;\r
}\r
.buttons{\r
    padding: 1em;\r
    height: 4em;\r
}\r
.footer{\r
    height: 0.6em;\r
    border-bottom-right-radius: var(--oo-menu-radius);\r
    border-bottom-left-radius: var(--oo-menu-radius);\r
}\r
.resize{\r
    height: 0.6em;\r
    width: 0.6em;\r
    float: right;\r
    border-bottom-right-radius: var(--oo-menu-radius);\r
    border-top-left-radius: 100%;\r
    background-color: var(--oo-color-gray-e);\r
    cursor: nw-resize;\r
    display: none;\r
}\r
.resize.show, .actions .show{\r
    display: block;\r
}\r
`,gr=`
<div class="dialog content">
    <div class="header" draggable="true">
        <div class="title"></div>
        <div class="actions">
            <div class="close ooicon-close show"></div>
        </div>
    </div>
    <div class="body">
        <slot name="content"></slot>
    </div>
    <div class="buttons">
        <oo-button class="button_ok show">OK</oo-button>
        <oo-button class="button_yes">Yes</oo-button>
        <oo-button class="button_no" type="cancel">No</oo-button>
        <oo-button class="button_cancel show" type="cancel">Cancel</oo-button>
    </div>
    <div class="footer">
        <div class="resize" draggable="true"></div>
    </div>
</div>
`;var Mt,bs,F,It,ys,R;const Fs=class Ct extends Ms{constructor(){super(),h(this,Mt),h(this,It),E(this,"_elements",{header:null,title:null,actions:null,close:null,body:null,buttons:null,button_ok:null,button_yes:null,button_no:null,button_cancel:null,footer:null,resize:null}),h(this,F,{x:0,y:0,positionX:0,positionY:0,styleX:"left",styleY:"top"}),h(this,R,{x:0,y:0,width:0,height:0}),g(this,Mt,bs).call(this),this._init("oo-dialog",Ct,gr,dr)}static get observedAttributes(){return Object.keys(this.prop).map(e=>x(e))}_addHideEvent(){}_removeHideEvent(){}_setEvent(){super._setEvent(),this._elements.close.addEventListener("click",e=>{this.close(e)}),this._elements.button_cancel.addEventListener("click",e=>{this.dispatchEvent(new DragEvent("cancel",e)),this.close(e)}),this._elements.button_ok.addEventListener("click",e=>{this.dispatchEvent(new DragEvent("ok",e)),this.close(e)}),this._elements.button_yes.addEventListener("click",e=>{this.dispatchEvent(new DragEvent("yes",e)),this.close(e)}),this._elements.button_no.addEventListener("click",e=>{this.dispatchEvent(new DragEvent("no",e)),this.close(e)}),this._elements.resize.addEventListener("drag",e=>{const o=e.pageX-a(this,R).x,s=e.pageY-a(this,R).y,i=a(this,R).width+o,r=a(this,R).height+s;n.setStyles(this,{width:i+"px",height:r+"px"}),this.dispatchEvent(new DragEvent("resize",e))}),this._elements.resize.addEventListener("dragend",e=>{this.dispatchEvent(Ct.events.endResize),this.dispatchEvent(new DragEvent("endResize",e))}),this._elements.resize.addEventListener("dragstart",e=>{a(this,R).x=e.pageX,a(this,R).y=e.pageY;const o=n.getSize(this);a(this,R).width=o.x,a(this,R).height=o.y,this.dispatchEvent(new DragEvent("startResize",e))}),this._elements.header.addEventListener("drag",e=>{if(e.screenX||e.screenY){const o=e.screenX-a(this,F).x,s=e.screenY-a(this,F).y,i=a(this,F).styleX==="right"?a(this,F).positionX-o:a(this,F).positionX+o,r=a(this,F).styleY==="bottom"?a(this,F).positionY-s:a(this,F).positionY+s,A={};A[a(this,F).styleX]=i+"px",A[a(this,F).styleY]=r+"px",n.setStyles(this,A),this.dispatchEvent(new DragEvent("move",e))}}),this._elements.header.addEventListener("dragend",e=>{this.dispatchEvent(new DragEvent("endMove",e))}),this._elements.header.addEventListener("dragstart",e=>{f(this,F,g(this,It,ys).call(this)),a(this,F).x=e.screenX,a(this,F).y=e.screenY,this.dispatchEvent(new DragEvent("startMove",e))})}close(e){this.dispatchEvent(new MouseEvent("close",e)),this.hide()}_afterHide(){this.remove()}_render(){this._fillContent("content")}};Mt=new WeakSet,bs=function(){this.setPropMap=Object.assign(this._setPropMap,{title:()=>{this._elements.title.textContent=this._props.title},buttons:()=>{const t=this._props.buttons.split(/,\s*/g);["ok","yes","no","cancel"].forEach(e=>{t.includes(e)?n.addClass(this._elements[`button_${e}`],"show"):n.removeClass(this._elements[`button_${e}`],"show")})},canResize:()=>{const t=this._props.canResize?"addClass":"removeClass";n[t](this._elements.resize,"show")},canMove:()=>{this._elements.header.setAttribute("draggable",!!this._props.canMove)},canClose:()=>{const t=this._props.canClose?"addClass":"removeClass";n[t](this._elements.close,"show")},ok:()=>{this._elements.button_ok.textContent=this._props.ok},yes:()=>{this._elements.button_yes.textContent=this._props.yes},no:()=>{this._elements.button_no.textContent=this._props.no},cancel:()=>{this._elements.button_cancel.textContent=this._props.cancel}})},F=new WeakMap,It=new WeakSet,ys=function(){const t={x:0,y:0,positionX:0,positionY:0};t.styleX=this.style.right?"right":"left",t.styleY=this.style.bottom?"bottom":"top";const e=n.getOffsetParent(this),o=n.getPosition(this,e),s=n.getSize(e),i=n.getSize(this);debugger;return t.positionX=t.styleX==="right"?s.x-(o.x+i.x):o.x,t.positionY=t.styleY==="bottom"?s.y-(o.y+i.y):o.y,t},R=new WeakMap,E(Fs,"prop",{css:"",cssLink:"",skin:"",show:!1,title:"",canResize:!1,canMove:!0,canClose:!0,buttons:"ok, cancel",ok:"OK",yes:"Yes",no:"No",cancel:"Cancel"});let Br=Fs;customElements.define("oo-dialog",Br);const Er=`* {\r
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
    padding: 0.8em;\r
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
`,ur=`
<div class="content tabs">
    <div class="labels">
        <div class="slider"></div>
        <slot name="label"></slot>
    </div>
    <div class="panes">
        <slot name="pane"></slot>
    </div>
</div>
`;var bt,vs,yt,xs;const Ds=class Ns extends S{constructor(){super(),h(this,bt),h(this,yt),E(this,"_elements",{labels:null,panes:null,slider:null}),E(this,"_setPropMap",{css:()=>{this.useCss(this._props.css)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},position:e=>{n.removeClass(this._content,e),n.addClass(this._content,this._props.position),n.removeClass(this._elements.labels,e),n.addClass(this._elements.labels,this._props.position),n.removeClass(this._elements.slider,e),n.addClass(this._elements.slider,this._props.position)},current:()=>{this._checkCurrentTab()}}),E(this,"currentTab",null),this._initialize("oo-tabs",Ns,ur,Er)}static get observedAttributes(){return Object.keys(this.prop).map(e=>x(e))}_checkCurrentTab(){const e=this.querySelectorAll("oo-tab").item(this._props.current);e&&e!==this.currentTab&&this.setCurrent(e)}setCurrent(e){this.currentTab&&(this.currentTab.unselected(),this.currentTab=null),e?(this.currentTab=e,e.selected(),g(this,bt,vs).call(this)):g(this,yt,xs).call(this)}};bt=new WeakSet,vs=function(){const t=n.getPosition(this.currentTab.label,this._elements.labels),e=n.getSize(this.currentTab.label);if(this._props.position==="top"||this._props.position==="bottom"){const o=this._elements.labels.scrollLeft,s=t.x+o;n.setStyles(this._elements.slider,{left:s+"px",width:e.x+"px"})}else{const o=this._elements.labels.scrollTop,s=t.y+o;n.setStyles(this._elements.slider,{top:s+"px",height:e.y+"px"})}},yt=new WeakSet,xs=function(){n.setStyles(this._elements.slider,{left:0,width:0})},E(Ds,"prop",{css:"",cssLink:"",skin:"",position:"top",current:0});let pr=Ds;customElements.define("oo-tabs",pr);const Qr=`* {\r
    box-sizing: border-box;\r
}\r
.content{\r
    display: block;\r
}\r
`,wr=`
<div class="content">
    <slot name="content"></slot>
</div>
`;var Ft,Ys,vt,Qo;const Us=class Ss extends S{constructor(){super(),h(this,Ft),h(this,vt),E(this,"_setPropMap",{css:()=>{this.useCss(this._props.css)},cssLink:()=>{this.useCssLink(this._props.cssLink)},skin:()=>{this._useSkin(this._props.skin)},label:()=>{},icon:()=>{}}),E(this,"label",null),E(this,"tabs",null),this._initialize("oo-tab",Ss,wr,Qr)}static get observedAttributes(){return Object.keys(this.prop).map(e=>x(e))}_render(){this._fillContent("content")}_connected(){this.tabs=n.getParent(this,"oo-tabs"),this.tabs&&g(this,Ft,Ys).call(this)}selected(){this.label&&n.addClass(this.label,"current"),n.removeClass(this,"hide")}unselected(){this.label&&n.removeClass(this.label,"current"),n.addClass(this,"hide")}};Ft=new WeakSet,Ys=function(){this.label||g(this,vt,Qo).call(this);const t=this.previousElementSibling;t&&t.label?t.label.insertAdjacentElement("afterend",this.label):this.tabs._elements.labels.insertAdjacentElement("afterbegin",this.label),this.setAttribute("slot","pane"),this!==this.tabs.currentTab&&n.addClass(this,"hide"),this.tabs._checkCurrentTab()},vt=new WeakSet,Qo=function(){this.label=n("div.label"),this._props.icon&&this.label.append(n(`div.ooicon-${this._props.icon}`)),this.label.append(n("div",{text:this._props.label||"New Tab"})),this.label.addEventListener("click",()=>{this.tabs.setCurrent(this)})},E(Us,"prop",{css:"",cssLink:"",skin:"",label:"",icon:""});let mr=Us;customElements.define("oo-tab",mr);const fr=`<div class="content">\r
	<label>\r
		<slot name="before-outer"></slot>\r
		<div class="label">\r
			<div class="labelText hide"></div>\r
			<slot name="label"></slot>\r
		</div>\r
		<div class="box">\r
			<slot name="before-inner-before"></slot>\r
			<div class="prefix"></div>\r
			<slot name="before-inner-after"></slot>\r
			<textarea class="input"></textarea>\r
			<slot name="after-inner-before"></slot>\r
			<div class="suffix"></div>\r
			<slot name="after-inner-after"></slot>\r
		</div>\r
		<slot name="after-outer"></slot>\r
	</label>\r
</div>\r
`,Mr=`* {\r
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
}\r
`;class eo extends Zt{constructor(){super(),this._init("oo-textarea"),this._useCss(Mr)}_init(e){this._initialize(e,eo,fr,Oo)}set value(e){debugger;this._props.value=e,this._elements.input.value=e}}customElements.define("oo-textarea",eo);var xt,wo,ye,Xe,Dt,mo,Nt,Ts,Fe,$e,Yt,fo,de,ve,Ut,Mo,St,Io,Tt,Rt,kt,Co;const k=class ce{constructor(e){h(this,xt),h(this,ye),h(this,Dt),h(this,Nt),h(this,Fe),h(this,Yt),h(this,de),h(this,Ut),h(this,St),h(this,kt),E(this,"options",{title:"",text:"",icon:"",type:"info",skin:"default",style:"",showClose:!0,contentAlign:"",contentValign:"",width:"",height:"",container:"",duration:5e3,location:"topCenter",position:"absolute",marginTop:10,marginBottom:0,marginLeft:10,marginRight:0}),E(this,"where",{x:"center",y:"top"}),E(this,"location",{fromTop:null,toTop:null,fromBottom:null,toBottom:null,fromLeft:null,toLeft:null,fromRight:null,toRight:null}),h(this,Tt,{bottom:(s,i)=>{const r=n.getSize(s),A=n.getSize(document.documentElement),c=n.getSize(this.node);this.location.toBottom=ce.env.height+this.options.marginTop+(A.y-r.y-i.y),this.location.fromBottom=this.location.toBottom-c.y,ce.env.height+=c.y+this.options.marginTop+this.options.marginBottom},top:(s,i)=>{const r=n.getSize(this.node);this.location.toTop=ce.env.height+this.options.marginTop+i.y,this.location.fromTop=this.location.toTop-r.y,ce.env.height+=r.y+this.options.marginTop+this.options.marginBottom},middle:()=>{}}),h(this,Rt,{left:(s,i)=>{this.location.fromLeft=this.location.toLeft=i.x+this.options.marginLeft+this.options.marginRight},right:(s,i)=>{n.getSize(s),n.getSize(document.documentElement),this.location.fromRight=this.location.toRight=i.x+this.options.marginLeft+this.options.marginRight},center:(s,i)=>{if(n.isHtml(s))n.addClass(this.node,"body-center");else{n.removeClass(this.node,"body-center");const r=n.getSize(s),A=n.getSize(this.node);this.location.fromLeft=this.location.toLeft=i.x+r.x/2-A.x/2}}}),this.options=Object.assign(this.options,e),this.container=this.options.container&&n.el(this.options.container)||document.body;const o=n.isBody(this.container)?"fixed":this.options.position||"absolute";this.node=n("oo-notice."+this.options.skin+(o==="fixed"?".fixed":".absolute")),["title","text","icon","type","skin","style","showClose","contentAlign","contentValign","width","height"].forEach(s=>{this.node.setAttribute(s,this.options[s])}),this.container.append(this.node),g(this,Nt,Ts).call(this)}resetPosition(e){const o=e+this.options.marginTop+this.options.marginBottom,s=zo(this.where.y);this.location[`to${s}`]=this.location[`to${s}`]-o,this.location[`from${s}`]=this.location[`from${s}`]-o;const i={};i[this.where.y]=this.location[`to${s}`]+"px",g(this,de,ve).call(this,i,!0)}};xt=new WeakSet,wo=function(){return k.env.notices.includes(this)||(k.env.notices.push(this),g(this,Ut,Mo).call(this),this.node._elements.close&&this.node._elements.close.addEventListener("click",()=>{g(this,Fe,$e).call(this)})),g(this,ye,Xe).call(this)},ye=new WeakSet,Xe=function(t){return g(this,de,ve).call(this,{top:this.location.fromTop+"px",left:this.location.fromLeft+"px",bottom:this.location.fromBottom+"px",right:this.location.fromRight+"px",opacity:0},t)},Dt=new WeakSet,mo=function(t){return g(this,de,ve).call(this,{top:this.location.toTop+"px",left:this.location.toLeft+"px",bottom:this.location.toBottom+"px",right:this.location.toRight+"px",opacity:"1"},t)},Nt=new WeakSet,Ts=async function(){await g(this,xt,wo).call(this),await g(this,Dt,mo).call(this,!0)&&this.options.duration&&setTimeout(()=>{g(this,Fe,$e).call(this)},this.options.duration||3e3)},Fe=new WeakSet,$e=async function(){if(!this.isHidden){this.isHidden=!0,g(this,Yt,fo).call(this);const t=await g(this,ye,Xe).call(this,!0);t&&t.propertyName==="opacity"&&(this.node.remove(),this.node=null)}},Yt=new WeakSet,fo=function(){const t=k.env.notices.indexOf(this),e=zo(this.where.y),o=Math.abs(this.location[`to${e}`]-this.location[`from${e}`]);k.env.height=k.env.height-o-this.options.marginTop-this.options.marginBottom;for(let s=t+1;s<k.env.notices.length;s++)k.env.notices[s].resetPosition(o);k.env.notices.splice(t,1)},de=new WeakSet,ve=function(t,e){return new Promise(o=>{if(n.setStyles(this.node,t),e){n.addClass(this.node,"transition");const s=i=>{n.removeClass(this.node,"transition"),this.node.removeEventListener("transitionend",s),o(i)};this.node.addEventListener("transitionend",s)}else setTimeout(()=>{o()})})},Ut=new WeakSet,Mo=function(){const t=g(this,kt,Co).call(this),e=n.isBody(this.container)?document.documentElement:this.container,o=g(this,St,Io).call(this,e);console.log(o),a(this,Tt)[t.y](e,o),a(this,Rt)[t.x](e,o)},St=new WeakSet,Io=function(t){const e=n("div");t.append(e);const o=n.getOffsetParent(e);return e.remove(),o===t?{x:0,y:0}:n.getPosition(t,n.getOffsetParent(t))},Tt=new WeakMap,Rt=new WeakMap,kt=new WeakSet,Co=function(){const t={left:"x",right:"x",center:"x",top:"y",bottom:"y",middle:"y"};return x(this.options.location).split("-").forEach(e=>{t[e]&&(this.where[t[e]]=e)}),this.where},E(k,"env",{notices:[],height:0});let bo=k;var jt,yo,Ht,Rs,zt,ks,xe,Ze,Gt,Fo,Jt,vo,_t,js;class xo{constructor(e,o){h(this,jt),h(this,Ht),h(this,zt),h(this,xe),h(this,Gt),h(this,Jt),h(this,_t),E(this,"options",{css:"",cssLink:"",skin:"",show:!1,title:"",canResize:!1,canMove:!0,canClose:!0,buttons:"ok, cancel",ok:"OK",yes:"Yes",no:"No",cancel:"Cancel",content:null,position:"center center",offset:{x:0,y:0},zIndex:100,modal:!0,modalArea:null,width:"",height:"",events:{}}),this.options=Object.assign(this.options,o),this.container=e,this.positionNode=this.options.positionNode||this.container,this.area=this.options.area||this.container}async show(e){var o,s;e&&(this.options.position=e),this.dialog||g(this,zt,ks).call(this),await g(this,_t,js).call(this),(o=this.options)!=null&&o.attr&&n.set(this.dialog,(s=this.options)==null?void 0:s.attr),this.dialog.show(),this.options.modal&&this.options.modalArea&&g(this,Ht,Rs).call(this)}hide(){this.dialog.hide()}close(){this.dialog.close(),this.dialog=null}}jt=new WeakSet,yo=function(){this.modalNode&&(this.modalNode.addEventListener("transitionend",t=>{this.modalNode.remove(),this.modalNode=null}),n.setStyles(this.modalNode,{opacity:0}))},Ht=new WeakSet,Rs=function(){const t=n.getStyle(this.dialog,"z-index");this.modalNode=n("div.oo-dialog-modal"),this.options.modalArea.insertAdjacentElement("beforeend",this.modalNode);const e=n.getOffsetParent(this.modalNode);if(e!==this.options.modalArea){const o=n.getPosition(this.options.modalArea,e),s=o.x+"px",i=o.y+"px";n.setStyles(this.modalNode,{left:s,top:i})}n.setStyles(this.modalNode,{opacity:.3,"z-index":t-1})},zt=new WeakSet,ks=function(){this.dialog=n("oo-dialog"),["css","cssLink","skin","show","title","canResize","canMove","canClose","buttons","ok","yes","no","cancel"].forEach(e=>{this.dialog.setAttribute(e,this.options[e])}),this.options.events&&Object.keys(this.options.events).forEach(e=>{this.dialog.addEventListener(e,this.options.events[e])});const t=this.options.zIndex||100;n.setStyle(this.dialog,"z-index",t),g(this,xe,Ze).call(this,this.options.content),this.container.append(this.dialog),this.options.width&&n.setStyle(this.dialog,"width",this.options.width),this.options.height&&n.setStyle(this.dialog,"height",this.options.height),this.dialog.addEventListener("hide",()=>{g(this,jt,yo).call(this)})},xe=new WeakSet,Ze=function(t){if(t)switch(oe(this.options.content)){case"element":this.dialog.append(t);break;case"string":this.dialog.append(n("div",{html:fi(t)}));break;case"promise":t.then(e=>{g(this,xe,Ze).call(this,e)}).catch(()=>{});break}},Gt=new WeakSet,Fo=function(t,e){const o={};return t.x==="center"?o.left=`calc(50% - ${e.x/2}px + ${(this.options.offset.x||0)+"em"})`:o[t.x]=(this.options.offset.x||0)+"rem",t.y==="center"?o.top=`calc(50% - ${e.y/2}px + ${(this.options.offset.y||0)+"em"})`:o[t.y]=(this.options.offset.y||0)+"rem",o},Jt=new WeakSet,vo=async function(){const t=await new Promise(i=>{window.setTimeout(()=>{i(n.getSize(this.dialog))})}),e=this.options.position||"center center",o=e.split(/\s+/),s=g(this,Gt,Fo).call(this,{x:o[0],y:o.length>1?o[1]:o[0]},t);return s["transform-origin"]=e,s},_t=new WeakSet,js=async function(){if(this.options.position&&this.options.position!=="none"){const t=await g(this,Jt,vo).call(this);n.setStyles(this.dialog,t)}};var Do=globalThis&&globalThis.__spreadArray||function(t,e,o){if(o||arguments.length===2)for(var s=0,i=e.length,r;s<i;s++)(r||!(s in e))&&(r||(r=Array.prototype.slice.call(e,0,s)),r[s]=e[s]);return t.concat(r||Array.prototype.slice.call(e))},Ir=function(){function t(e,o,s){this.name=e,this.version=o,this.os=s,this.type="browser"}return t}(),Cr=function(){function t(e){this.version=e,this.type="node",this.name="node",this.os=process.platform}return t}(),br=function(){function t(e,o,s,i){this.name=e,this.version=o,this.os=s,this.bot=i,this.type="bot-device"}return t}(),yr=function(){function t(){this.type="bot",this.bot=!0,this.name="bot",this.version=null,this.os=null}return t}(),Fr=function(){function t(){this.type="react-native",this.name="react-native",this.version=null,this.os=null}return t}(),vr=/alexa|bot|crawl(er|ing)|facebookexternalhit|feedburner|google web preview|nagios|postrank|pingdom|slurp|spider|yahoo!|yandex/,xr=/(nuhk|curl|Googlebot|Yammybot|Openbot|Slurp|MSNBot|Ask\ Jeeves\/Teoma|ia_archiver)/,No=3,Dr=[["aol",/AOLShield\/([0-9\._]+)/],["edge",/Edge\/([0-9\._]+)/],["edge-ios",/EdgiOS\/([0-9\._]+)/],["yandexbrowser",/YaBrowser\/([0-9\._]+)/],["kakaotalk",/KAKAOTALK\s([0-9\.]+)/],["samsung",/SamsungBrowser\/([0-9\.]+)/],["silk",/\bSilk\/([0-9._-]+)\b/],["miui",/MiuiBrowser\/([0-9\.]+)$/],["beaker",/BeakerBrowser\/([0-9\.]+)/],["edge-chromium",/EdgA?\/([0-9\.]+)/],["chromium-webview",/(?!Chrom.*OPR)wv\).*Chrom(?:e|ium)\/([0-9\.]+)(:?\s|$)/],["chrome",/(?!Chrom.*OPR)Chrom(?:e|ium)\/([0-9\.]+)(:?\s|$)/],["phantomjs",/PhantomJS\/([0-9\.]+)(:?\s|$)/],["crios",/CriOS\/([0-9\.]+)(:?\s|$)/],["firefox",/Firefox\/([0-9\.]+)(?:\s|$)/],["fxios",/FxiOS\/([0-9\.]+)/],["opera-mini",/Opera Mini.*Version\/([0-9\.]+)/],["opera",/Opera\/([0-9\.]+)(?:\s|$)/],["opera",/OPR\/([0-9\.]+)(:?\s|$)/],["pie",/^Microsoft Pocket Internet Explorer\/(\d+\.\d+)$/],["pie",/^Mozilla\/\d\.\d+\s\(compatible;\s(?:MSP?IE|MSInternet Explorer) (\d+\.\d+);.*Windows CE.*\)$/],["netfront",/^Mozilla\/\d\.\d+.*NetFront\/(\d.\d)/],["ie",/Trident\/7\.0.*rv\:([0-9\.]+).*\).*Gecko$/],["ie",/MSIE\s([0-9\.]+);.*Trident\/[4-7].0/],["ie",/MSIE\s(7\.0)/],["bb10",/BB10;\sTouch.*Version\/([0-9\.]+)/],["android",/Android\s([0-9\.]+)/],["ios",/Version\/([0-9\._]+).*Mobile.*Safari.*/],["safari",/Version\/([0-9\._]+).*Safari/],["facebook",/FB[AS]V\/([0-9\.]+)/],["instagram",/Instagram\s([0-9\.]+)/],["ios-webview",/AppleWebKit\/([0-9\.]+).*Mobile/],["ios-webview",/AppleWebKit\/([0-9\.]+).*Gecko\)$/],["curl",/^curl\/([0-9\.]+)$/],["searchbot",vr]],Yo=[["iOS",/iP(hone|od|ad)/],["Android OS",/Android/],["BlackBerry OS",/BlackBerry|BB10/],["Windows Mobile",/IEMobile/],["Amazon OS",/Kindle/],["Windows 3.11",/Win16/],["Windows 95",/(Windows 95)|(Win95)|(Windows_95)/],["Windows 98",/(Windows 98)|(Win98)/],["Windows 2000",/(Windows NT 5.0)|(Windows 2000)/],["Windows XP",/(Windows NT 5.1)|(Windows XP)/],["Windows Server 2003",/(Windows NT 5.2)/],["Windows Vista",/(Windows NT 6.0)/],["Windows 7",/(Windows NT 6.1)/],["Windows 8",/(Windows NT 6.2)/],["Windows 8.1",/(Windows NT 6.3)/],["Windows 10",/(Windows NT 10.0)/],["Windows ME",/Windows ME/],["Windows CE",/Windows CE|WinCE|Microsoft Pocket Internet Explorer/],["Open BSD",/OpenBSD/],["Sun OS",/SunOS/],["Chrome OS",/CrOS/],["Linux",/(Linux)|(X11)/],["Mac OS",/(Mac_PowerPC)|(Macintosh)/],["QNX",/QNX/],["BeOS",/BeOS/],["OS/2",/OS\/2/]];function Nr(t){return t?Uo(t):typeof document>"u"&&typeof navigator<"u"&&navigator.product==="ReactNative"?new Fr:typeof navigator<"u"?Uo(navigator.userAgent):Sr()}function Yr(t){return t!==""&&Dr.reduce(function(e,o){var s=o[0],i=o[1];if(e)return e;var r=i.exec(t);return!!r&&[s,r]},!1)}function Uo(t){var e=Yr(t);if(!e)return null;var o=e[0],s=e[1];if(o==="searchbot")return new yr;var i=s[1]&&s[1].split(".").join("_").split("_").slice(0,3);i?i.length<No&&(i=Do(Do([],i,!0),Tr(No-i.length),!0)):i=[];var r=i.join("."),A=Ur(t),c=xr.exec(t);return c&&c[1]?new br(o,r,A,c[1]):new Ir(o,r,A)}function Ur(t){for(var e=0,o=Yo.length;e<o;e++){var s=Yo[e],i=s[0],r=s[1],A=r.exec(t);if(A)return i}return null}function Sr(){var t=typeof process<"u"&&process.version;return t?new Cr(process.version.slice(1)):null}function Tr(t){for(var e=[],o=0;o<t;o++)e.push("0");return e}var me,Rr=new Uint8Array(16);function kr(){if(!me&&(me=typeof crypto<"u"&&crypto.getRandomValues&&crypto.getRandomValues.bind(crypto)||typeof msCrypto<"u"&&typeof msCrypto.getRandomValues=="function"&&msCrypto.getRandomValues.bind(msCrypto),!me))throw new Error("crypto.getRandomValues() not supported. See https://github.com/uuidjs/uuid#getrandomvalues-not-supported");return me(Rr)}const jr=/^(?:[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}|00000000-0000-0000-0000-000000000000)$/i;function Hr(t){return typeof t=="string"&&jr.test(t)}var b=[];for(var qe=0;qe<256;++qe)b.push((qe+256).toString(16).substr(1));function zr(t){var e=arguments.length>1&&arguments[1]!==void 0?arguments[1]:0,o=(b[t[e+0]]+b[t[e+1]]+b[t[e+2]]+b[t[e+3]]+"-"+b[t[e+4]]+b[t[e+5]]+"-"+b[t[e+6]]+b[t[e+7]]+"-"+b[t[e+8]]+b[t[e+9]]+"-"+b[t[e+10]]+b[t[e+11]]+b[t[e+12]]+b[t[e+13]]+b[t[e+14]]+b[t[e+15]]).toLowerCase();if(!Hr(o))throw TypeError("Stringified UUID is invalid");return o}function Gr(t,e,o){t=t||{};var s=t.random||(t.rng||kr)();if(s[6]=s[6]&15|64,s[8]=s[8]&63|128,e){o=o||0;for(var i=0;i<16;++i)e[o+i]=s[i];return e}return zr(s)}const So=function(t,e,o){const s=e.toLowerCase();s==="text"?t.textContent=o:s==="html"?t.innerHTML=o:s==="styles"?l.setStyles(t,o):t.setAttribute(e,o)},Jr=function(t,e){const o=e.toLowerCase();if(o==="text")return t.innerText||t.textContent||"";if(o==="html")return t.innerHTML||"";if(o==="value")switch(t.tagName.toString().toLowerCase()){case"select":return t.options[t.selectedIndex].value;default:return t.value}return t.getAttribute(e)},fe=function(t){const e=Kr(t||"").split(" "),o={};return e.filter(function(s){if(s!==""&&!o[s])return o[s]=s})},To=function(t){return!t.tagName||/^(?:body|html)$/i.test(t.tagName)},Ro=function(t){return/html/i.test(t.tagName)},Re=function(t,e){return parseInt(l.getStyle(t,e))||0},_r=function(t){return Re(t,"border-top-width")},Pr=function(t){return Re(t,"border-left-width")},Or=function(t,e){return{left:t.x-Re(e,"margin-left"),top:t.y-Re(e,"margin-top")}},l=(t,e)=>{let o;const s=t.replace(/^\S*?(?=\.|#|$)/,c=>(o=c,""));let i="",r;s&&(r=s.split(".").filter(d=>!!d).map(d=>{const B=d.split("#"),p=B.shift();return i=B.pop()||i,p}));const A=document.createElement(o);return i&&A.setAttribute("id",i),r&&r.length&&A.setAttribute("class",r.join(" ")),e&&l.set(A,e),A};Object.assign(l,{el:(t,e)=>m(t)==="string"&&t?(e||document).querySelector(t):t,els:(t,e)=>(e||document).querySelectorAll(t),addClass:(t,e)=>{const o=l.el(t);return fe(e).forEach(s=>{o.classList.add(s)}),o},removeClass:(t,e)=>{const o=l.el(t);return fe(e).forEach(s=>{o.classList.remove(s)}),o},hasClass:(t,e)=>l.el(t).classList.contains(e),toggleClass:(t,e,o)=>{const s=l.el(t);return fe(e).forEach(i=>{let r=o;(r==null||r===!1)&&(r=!l.hasClass(s,i)),r?l.addClass(s,i):l.removeClass(s,i)}),s},checkClass:(t,e,o)=>{const s=l.el(t);return fe(e).forEach(i=>{o?l.addClass(s,i):l.removeClass(s,i)}),s},mapProps:(t,e)=>{const o=l.el(t);for(const s in e){const i=Wr(s);if(typeof e[s]=="boolean")e[s]=o.hasAttribute(s)||o.hasAttribute(i)||!1;else{const r=o.getAttribute(s)||o.getAttribute(i)||"";r!==e[s]&&(e[s]=r)}}return o},toggleAttr:(t,e,o)=>{const s=l.el(t);return o?s.setAttribute(e,o):s.removeAttribute(e),s},set:(t,e,o)=>{const s=l.el(t);return typeof e=="object"?Object.keys(e).forEach(i=>{So(s,i,e[i])}):So(s,e,o),s},get:(t,e)=>{const o=l.el(t);return Jr(o,e)},setProperty:(t,e,o)=>{const s=l.el(t);return s.setAttribute(e,o),s},getScroll:t=>{const e=l.el(t);return{x:e.scrollLeft,y:e.scrollTop}},getScrolls:t=>{let o=l.el(t).parentNode,s={x:0,y:0};for(;o&&!To(o);)s.x+=o.scrollLeft,s.y+=o.scrollTop,o=o.parentNode;return s},getOffsetParent:t=>{let e=t.offsetParent;for(;e&&["table","td","th"].includes(e.tagName.toString().toLowerCase());)e=e.offsetParent;return e||document.body},getPosition:(t,e)=>{const o=l.el(t);e===window&&(e=document.documentElement);const s=t.getBoundingClientRect(),i=document.documentElement,r=l.getScroll(i),A=l.getScrolls(o),c=l.getStyle(o,"position")==="fixed",d={x:s.left+A.x+(c?0:r.x)-i.clientLeft,y:s.top+A.y+(c?0:r.y)-i.clientTop},B=l.getScrolls(o),p={x:d.x-B.x,y:d.y-B.y};if(e){const Q=l.getPosition(e);return{x:p.x-Q.x-Pr(e),y:p.y-Q.y-_r(e)}}return p},setPosition:(t,e)=>{const o=l.el(t);return l.setStyles(o,Or(e.this))},getSize:t=>{const e=l.el(t);if(Ro(e))return{x:e.clientWidth,y:e.clientHeight};{const o=e.getBoundingClientRect();return{x:o.width,y:o.height}}},getScrollSize:t=>{const e=l.el(t);if(/^(?:body|html)$/i.test(e.tagName)){const o=document.documentElement,s=document.body;return{x:Math.max(o.scrollWidth,s.scrollWidth),y:Math.max(o.scrollHeight,s.scrollHeight)}}return{x:e.scrollWidth,y:e.scrollHeight}},getStyle:function(t,e){const o=l.el(t),s=J(e);let i=o.style[s];return i||(i=window.getComputedStyle(o)[s]),i},getStyles:t=>{const e=[...arguments],o=l.el(e.shift()),s={};return e.forEach(i=>{s[i]=l.getStyle(o,i)}),s},setStyle:(t,e,o)=>{const s=l.el(t);return typeof e=="object"?Object.keys(e).forEach(i=>{s.style[J(i)]=e[i]}):s.style[J(e)]=o,s},setStyles:(t,e)=>l.setStyle(t,e),show:t=>{const e=l.el(t);return l.getStyle(e,"display")==="none"&&l.setStyle(e,"display",e.dataset.storeDisplay||"block"),e},hide:t=>{const e=l.el(t),o=l.getStyle(e,"display");return o!=="none"&&(e.dataset.storeDisplay=o,l.setStyle(e,"display","none")),e},getParentSrcollNode:t=>{let o=l.el(t).parentElement;for(;o&&(l.getScrollSize(o).y-2<=l.getSize(o).y||l.getStyle(o,"overflow")!=="auto"&&l.getStyle(o,"overflow-y")!=="auto");)o=o.parentElement;return o||document.documentElement},getParent:function(t,e){if(e){let o=t.parentElement;for(;o&&!o.matches(e);)o=o.parentElement;return o}else return t.parentElement},isBody:t=>To(l.el(t)),isHtml:t=>Ro(l.el(t)),empty:t=>{const e=l.el(t);for(;e.childNodes.length;)e.removeChild(e.childNodes[0]);return e},peel:(t,e)=>{const{node:o,position:s}=e&&m(e)==="element"?{node:e,position:"beforeend"}:(()=>{if(e){const r=document.createComment("");t.parentNode.insertBefore(r,t)}return{node:t,position:"beforebegin"}})();let i=t.firstElementChild;for(;i;)o.insertAdjacentElement(s,i),i=t.firstElementChild;t.remove()}});Nr();function m(t){if(t==null)return"null";if(Array.isArray(t))return"array";if(t instanceof Map)return"map";if(t instanceof Set)return"set";if(t===window)return"window";if(t instanceof Date)return"date";if(t instanceof RegExp)return"regexp";if(t instanceof Error)return"error";if(t instanceof Promise)return"promise";if(t.nodeName){if(t.nodeType===1)return"element";if(t.nodeType===3)return/\S/.test(t.nodeValue)?"textnode":"whitespace";if(t.nodeType===9)return"document";if(t.nodeType||t.nodeType===0)return t.nodeName}else if(typeof t.length=="number"&&t.callee)return"arguments";return typeof t}function Hs(t,e,o=null,s=!1){const i=e||this,r=o?Object.values(o):[],A=o?Object.keys(o).join(","):"";try{return Function("return function("+A+"){"+t+"}")().apply(i,r)}catch(c){if(s)throw c;return""}}function zs(t){if(!t)return t;const e=m(t);return e==="array"||e==="object"?se(t):t}function Lr(t){let e=t.length,o=new Array(e);for(;e--;)o[e]=zs(t[e]);return o}function se(t){if(m(t)==="array")return Lr(t);const e={};for(let o in t)e[o]=zs(t[o]);return e}const Vr=(t,e,o)=>{switch(m(o)){case"object":m(t[e])==="object"?Pt(t[e],o):t[e]=se(o);break;case"array":t[e]=se(o);break;default:t[e]=o}return t},Pt=function(){return[...arguments].reduce((t,e)=>{for(const o in e)Vr(t,o,e[o]);return t})};function Wr(t){return t.replace(/([A-Z])/g,"-$1").toLowerCase()}function J(t){return t.replace(/-(\w)/g,function(e,o){return o.toUpperCase()})}function Kr(t){return t.replace(/\s+/g," ").trim()}async function Xr(){return await Promise.all([...arguments])}function $r(t){return t&&typeof t[Symbol.iterator]=="function"&&(Array.isArray(t)?t.length:t.size)>0}function Ot(t){return Promise.resolve(t)===t}const Zr=!0,to=class{constructor(){u(this,"nodeName","#text");u(this,"nodeType",Node.ELEMENT_NODE);u(this,"parentNode",null);u(this,"nextSibling",null);u(this,"previousSibling",null);u(this,"domNode",null);u(this,"childNodes",[]);u(this,"dom");u(this,"itemData")}append(t){const e=this.childNodes.length?this.childNodes[this.childNodes.length-1]:null;return this.childNodes.push(t),t.parentNode=this,e&&(e.nextSibling=t,t.previousSibling=e),t}insertBefore(t,e){const o=this.childNodes.indexOf(e);return o>-1&&(this.childNodes.splice(o,0,t),t.parentNode=this,t.previousSibling=e.previousSibling,t.nextSibling=e,e.previousSibling=t,e.previousSibling&&(e.previousSibling.nextSibling=t)),t}insertAfter(t,e){return e.nextSibling?this.insertBefore(t,e.nextSibling):this.append(t)}getParent(t){let e=this.parentNode;for(;e&&e.vType!==t;)e=e.parentNode;return e}getPrevious(t){let e=this.previousSibling;for(;e&&e.vType!==t;)e=e.previousSibling;return e}getParentEachItemData(){const t={};this.itemData&&Object.assign(t,this.itemData);let e=this.getParent("eachItem");for(;e;)Object.keys(e.itemData).forEach(o=>{t[o]=e.itemData[o]}),e=e.getParent("eachItem");return t}remove(){const t=this.previousSibling,e=this.nextSibling;t&&(t.nextSibling=e),e&&(e.previousSibling=t),this.domNode.parentNode.removeChild(this.domNode);const o=this.parentNode.childNodes.indexOf(this);o!==-1&&this.parentNode.childNodes.splice(o,1)}exec(t,e){const o=Object.assign({$:this.dom.bind,$$:this.dom.bind,$m:this.dom.module},this.getParentEachItemData(),e),s=Hs(t,this.dom.module,o,!Zr);return m(s)==="null"||m(s)==="undefined"?"":s}execInterpolation(t,e){let o=[];return{value:t.replace(/{{([\S\s\n\t]*?)}}/gm,(s,i)=>{const{value:r,bindPaths:A}=this.execCode(i,"",e);return o=o.concat(A),r}),paths:o}}findBindPath(t){const e=[];if(t){const o=this.dom.module.autoUpdate?/\${1}\.(.*?)(?=[=()}\s;,!&|\[\]+\-*\/?:]|$)|(?=\s|^)\$(?=\s|$)/gm:/\$\$\.(.*?)(?=[=()}\s;,!&|\[\]+\-*\/?:]|$)|(?=\s)\$(?=\s)/gm;let s;for(;(s=o.exec(t))!==null;){const i=s[1].split(".");let r=this.dom.bind;const A=i.reduce((c,d)=>{const B=Object.getOwnPropertyDescriptors(Object.getPrototypeOf(r));return Object.keys(B).includes(d)?c:(r=r[d]||{},c?`${c}.${d}`:d)},"");e&&e.push(A)}}return e}execCode(t,e,o){if(t){const s=[];if(t==="$")s.push("$");else{const A=this.dom.module.autoUpdate?/\${1}\.(.*?)(?=[=()}\s;,!&|\[\]+\-*\/?:]|$)|(?=\s|^)\$(?=\s|$)/gm:/\$\$\.(.*?)(?=[=()}\s;,!&|\[\]+\-*\/?:]|$)|(?=\s)\$(?=\s)/gm;let c;for(;(c=A.exec(t))!==null;){const d=c[1].split(".");let B=this.dom.bind;const p=d.reduce((Q,D)=>{const M=Object.getOwnPropertyDescriptors(Object.getPrototypeOf(B));return Object.keys(M).includes(D)?Q:(B=B[D]||{},Q?`${Q}.${D}`:D)},"");s&&s.push(p)}}const i=this.exec(`return (${t})`,o);return{value:m(i)==="null"||m(i)==="undefined"?e:i,bindPaths:s}}else return{value:m(e)==="null"||m(e)==="undefined"?"":e}}};class Gs extends to{constructor(o,s,i){super();u(this,"nodeName","#text");u(this,"nodeType",Node.ELEMENT_NODE);u(this,"parentNode",null);u(this,"domNode",null);u(this,"codeValue","");u(this,"nodeValue","");this.dom=s,o&&(this.domNode=o,this.domNode.vNode=this,this.nodeName=o.nodeName,this.nodeType=o.nodeType,this.codeValue=o.nodeValue,this.nodeValue=o.nodeValue),i&&i.append(this),this.parse()}parse(){if(this.vType="text",this.codeValue){const{value:o,paths:s}=this.execInterpolation(this.codeValue.trim());this.render(o),this.addBindList(s)}}render(o){if(o!==this.nodeValue){this.nodeValue=o;const s=document.createTextNode(this.nodeValue);this.domNode.parentNode.replaceChild(s,this.domNode),this.domNode=s}}update(){if(this.itemData=this.getParentEachItemData(),this.codeValue){const{value:o}=this.execInterpolation(this.codeValue.trim());this.render(o)}}updateAll(){this.update()}addBindList(o){this.dom.addBindList(o,{node:this})}}class qr extends Gs{constructor(e,o){super(e,o)}}var Be,Lt,Xt,en;class Pe extends to{constructor(o,s,i,r){super();I(this,Be);I(this,Xt);u(this,"attrs",{});u(this,"slots",{});u(this,"events",{});u(this,"elements",{});u(this,"models",{});u(this,"childNodes",[]);return this.dom=s,this.domNode=o,this.domNode.vNode=this,this.nodeName=o.nodeName,this.nodeType=o.nodeType,this.slotComponent=r,this.fieldType=this.getNodeFieldType(),i&&i.append(this),this.parse()}createNode(o,s){return this.dom.createNode(o,this,s)}getNodeFieldType(){const o=this.domNode.tagName.toLowerCase(),s={select:i=>i==="select",radioOrCheck:i=>i==="input"&&["radio","checkbox"].includes(this.domNode.getAttribute("type"))||["oo-radio","oo-checkbox"].includes(i),input:i=>["input","textarea","oo-input","oo-textarea"].includes(i),radioOrCheckGroup:i=>["oo-radio-group","oo-checkbox-group"].includes(i)};for(const i of Object.keys(s))if(s[i](o))return i;return"other"}parse(){return this.vType="element",this.parseAttr()}parseChildren(o,s){const i=o||this.domNode.childNodes;for(let r=0;r<i.length;r++){const A=i[r];A.nodeType===Node.ELEMENT_NODE?this.createNode(A,s||this.slotComponent):A.nodeValue.trim()?this.createNode(A):(A.parentNode.removeChild(A),o||r--)}}async getSubComponentBuilder(){var i;const o=J(this.nodeName.toLowerCase()),s=this.dom.module.components[o];if(s){if(so.prototype.isPrototypeOf(s))return s;if(m(s)==="function")return await s.call(this.dom.module);const r=await((i=s.load)==null?void 0:i.call(this.dom.module));return r&&(r.watch=s.watch),r}}async createSubComponent(o){const s=o||await this.getSubComponentBuilder();if(s){const i=w(this,Be,Lt).call(this);s.watch&&this.dom.addWatchComponentList(s.watch,this),this.domNode.style.display="contents",this.subComponentGenerator=s,this.subComponent=await s.generate(this.domNode,{},this.dom.module,i)}return this.subComponent}async updateWatchComponent(){const o=await this.getSubComponentBuilder();if(o!==this.subComponentGenerator)this.subComponent&&(await Promise.resolve(this.subComponent),this.subComponent.destroy(),this.subComponent=null),this.subComponent=this.createSubComponent(o);else if(this.subComponent){const s=w(this,Be,Lt).call(this),i=Object.keys(s);await Promise.resolve(this.subComponent);for(const r of i)if(s[r]!==this.subComponent.module.bind[r]){this.subComponent.destroy(),this.subComponent=null,this.subComponent=this.createSubComponent(o);break}}else this.subComponent=this.createSubComponent(o)}async updateSubComponent(o){if(this.subComponent&&this.ooProp){await Promise.resolve(this.subComponent);const s=new Set;Object.keys(this.ooProp).forEach(i=>{const r=i.includes(":")?J(i.split(/\s*:{1,2}\s*/)[1]):"";if(r){const A=this.ooProp[i],c=A.bindPaths?A.bindPaths.find(d=>o.startsWith(d+".")||o===d||d==="$"):"";if(c){const{value:d}=this.execCode(A.code);this.subComponent.module.bind[r]=d,s.add(c),s.add(r)}}else this.ooProp[i].bindPaths.forEach(A=>{if(o.startsWith(A+".")||o===A||A==="$"){const c=o.replace(new RegExp(`^${A}.`),"");c&&s.add(c)}})}),s.forEach(i=>{this.subComponent.vdom.update(i,o)})}}async parseAttr(){(this.inited?Object.keys(this.attrs):this.domNode.getAttributeNames()).forEach(i=>{const r=this.domNode.getAttributeNode(i),A=this.attrs[i]?this.attrs[i].code:r.value,{value:c,paths:d}=this.execInterpolation(A);this.getParseMethod(i).call(this,i,c,A,d,r)});const s=this.nodeName.toLowerCase();if(s==="oo-slot"&&(this.domNode.style.display="contents",this.addSlot()),this.dom.module.components&&Object.keys(this.dom.module.components).includes(J(s))){const i=this.inited?[]:Array.from(this.domNode.childNodes.values());await this.updateWatchComponent(),i&&i.length&&this.parseChildren(i,this.subComponent)}else this.inited||this.parseChildren();this.inited=!0}getParseMethod(o){return o.startsWith("oo-on:")||o.startsWith("@")?this.parseEventAttr:o==="oo-element"?this.parseElementAttr:o==="oo-model"?this.parseModelAttr:o.startsWith("oo-boolean:")||o.startsWith(".")?this.parseBooleanAttr:o==="oo-slot"?this.parseSlotAttr:o==="oo-html"?this.parseHtmlAttr:this.parseCommonAttr}parseHtmlAttr(o,s,i,r,A){s!==i&&(A.value=s),this.attrs[o]={code:i,value:s},r.length&&this.addBindList(r,{type:"attrs",key:o}),this.domNode.removeAttribute(o),this.domNode.insertAdjacentHTML("afterbegin",s)}parseSlotAttr(o,s,i,r){this.slots[o]={code:i,value:s},r.length&&this.addBindList(r,{type:"slots",key:o}),this.bindSlot(s)}parseBooleanAttr(o,s,i,r){const A=this.execCode(i,!1),c=o.substring(o.lastIndexOf(".")+1);this.domNode.removeAttribute(o),A.value?this.domNode.setAttribute(c,!0):this.domNode.removeAttribute(c),this.attrs[o]={code:i,value:s},r.length&&this.addBindList(r,{type:"attrs",key:o}),A.bindPaths.length&&this.addBindList(A.bindPaths,{type:"attrs",key:o})}parseCommonAttr(o,s,i,r,A){s!==i&&(A.value=s),this.attrs[o]={code:i,value:s},r.length&&this.addBindList(r,{type:"attrs",key:o})}parseEventAttr(o,s,i,r){const A=o.replace(/^oo-on:|^@/,"");this.bindElementEvent(A,s),this.domNode.removeAttribute(o),r.length&&(this.events[A]={code:i,value:s},this.addBindList(r,{type:"events",key:A}))}parseElementAttr(o,s,i,r){if(this.elements&&this.elements.element){const c=this.elements.element.value;c&&delete this.dom.module[c]}const A=this.domNode;Object.defineProperty(this.dom.module,s,{get(){return A.vNode&&A.vNode.subComponent?Ot(A.vNode.subComponent)?A.vNode.subComponent.then(()=>A.vNode.subComponent.module):A.vNode.subComponent.module:A}}),this.domNode.removeAttribute(o),r.length&&(this.elements.element={code:i,value:s},this.addBindList(r,{type:"elements",key:"element"}))}parseModelAttr(o,s,i,r){const A=this.exec(`return ($.${s})`);this.bindModelValueSet(A),this.bindModelListener(),this.dom.addModelList(s,this),this.domNode.removeAttribute(o),this.models.model={code:i,value:s},r.length&&this.addBindList(r,{type:"models",key:"model"})}bindModelValueSet(o){return{other:s=>{this.domNode.setAttribute("value",s)},input:s=>{this.domNode.value=s},radioOrCheck:s=>{this.domNode.checked=this.domNode.getAttribute("value")===s},select:s=>{const i=Array.isArray(s)?s:[s],r=this.domNode.querySelectorAll("option");r.length&&r.forEach(A=>{A.selected=i.includes(A.value)})},radioOrCheckGroup:s=>{this.domNode.setAttribute("value",s)}}[this.fieldType](o),!0}bindModelValueGet(){return{other:()=>this.domNode.value,input:()=>this.domNode.value,radioOrCheck:()=>this.domNode.checked?this.domNode.getAttribute("value"):"",select:()=>{const o=this.domNode.getAttribute("multiple"),s=[],i=this.domNode.querySelectorAll("option");for(const r of i)if(r.selected){if(!o)return r.getAttribute("value");s.push(r.getAttribute("value"))}return s},radioOrCheckGroup:()=>this.domNode.value}[this.fieldType]()}bindModelListener(){const o=i=>{const A=this.models.model.value.split("."),c=A.length-1;return A.reduce((d,B,p)=>p===c?d[B]=i:d[B]||{},this.dom.bind)};({other:["input"],input:["input"],radioOrCheck:["change"],select:["change"],radioOrCheckGroup:["change"]})[this.fieldType].forEach(i=>{this.domNode.addEventListener(i,r=>{o(this.bindModelValueGet()),r.stopPropagation()})})}bindElementEvent(o,s){this.eventListeners||(this.eventListeners={}),this.eventListeners[o]&&this.domNode.removeEventListener(o,this.eventListeners[o]),this.eventListeners[o]=i=>{const r=this.dom.module[s];if(r&&m(r)==="function"){const A=this.getParentEachItemData(),c=Object.keys(A)?A.$item:this.dom.bind,d=this.itemData?this.itemData.$item:c;r.apply(this.dom.module,[i,d,c,this.dom.bind])}else return this.exec(`return (${s})`,{$event:i,$node:this.domNode})},this.domNode.addEventListener(o,this.eventListeners[o])}update(o){const{code:s,value:i}=this[o.type][o.key],r=this.execInterpolation(s).value;if(r!==i)switch(this[o.type][o.key].value=r,o.type){case"attrs":this.domNode.setAttribute(o.key,r);break;case"events":this.bindElementEvent(o.key,r);break;case"elements":i&&delete this.dom.module[i],this.dom.module[r]=this.domNode;break;case"models":this.bindModelValueSet(r);break;case"slots":this.bindSlot(r);break}}async bindSlot(o){if(this.slotComponent){this.slotComponent=await Promise.resolve(this.slotComponent);const s=this.slotComponent.vdom.slotList.get(o);s&&(s.append(this),s.domNode.append(this.domNode))}}updateModel(){const o=this.models.model.value,s=this.exec(`return ($.${o})`);this.bindModelValueSet(s)}addBindList(o,s){!this.inited&&o.length&&o.forEach(i=>{this.dom.addBind(i,{node:this,par:s})})}addBind(o,s){this.inited||this.dom.addBind(o,{node:this,par:s})}takePlace(){this.domNode.parentNode&&(this.placeNode||(this.placeNode=document.createComment("")),this.domNode.parentNode.insertBefore(this.placeNode,this.domNode),this.domNode.remove())}returnPlace(){!this.dom.domNode.contains(this.domNode)&&this.placeNode&&(this.placeNode.parentNode.insertBefore(this.domNode,this.placeNode),this.placeNode=this.placeNode.parentNode.removeChild(this.placeNode))}updateAll(){this.parse(),this.subComponent?this.updateSubComponent("$"):this.childNodes.forEach(o=>o.updateAll())}addSlot(){this.inited||this.dom.addSlot(this.domNode.getAttribute("name"),this)}}Be=new WeakSet,Lt=function(){let o={};return this.ooProp=this.ooProp||{},Object.keys(this.attrs).filter(s=>s.startsWith("oo-prop")||s.startsWith(":")).map(s=>{const i=(this.ooProp[s]?this.ooProp[s].code:"")||this.domNode.getAttribute(s);if(i){this.ooProp[s]={code:i};const{value:r,bindPaths:A}=this.execCode(i);A&&A.length&&(this.ooProp[s].bindPaths=A,this.dom.addSubComponentList(A,this));const c=s.includes("::")?J(s.split(/\s*::\s*/)[1]):"";if(c){const d={};m(r)==="object"?d[c]=r:d[c]=this.dom.module.component.proxyBind({$OOValue:r},A[0]),Object.assign(o,d)}else{const d=s.includes(":")?J(s.split(/\s*:\s*/)[1]):"";if(!d)o=Object.assign(r,o);else{const B={};B[d]=r,Object.assign(o,B)}this.domNode.removeAttribute(s)}}}),o},Xt=new WeakSet,en=async function(){this.subComponentPromise,await Promise.resolve(this.subComponentPromise)};class ge extends Pe{parse(){this.vType="if",this.parseIf("oo-if")}parseIf(e,o){this.if||(this.if={});const s=this.if.code||this.domNode.getAttribute(e),{value:i,bindPaths:r}=this.execCode(s,!1);this.doElse=!i,i!==this.if.value&&(this.if={code:s,value:i},this.domNode.removeAttribute("oo-if"),i?(this.returnPlace(),this.parseAttr()):this.takePlace()),r&&this.dom.addIfList(r,this)}updateIf(){this.parseIf("oo-if");const e=this.nextSibling;e&&(e instanceof ie||e instanceof oo)&&e.updateIf()}updateAll(){this.parseIf("oo-if"),this.if.value&&this.childNodes.forEach(e=>{e.updateAll()})}}class ie extends ge{parse(){this.vType="elseIf";const e=this.previousSibling;if(e&&(e instanceof ge||e instanceof ie)?e.doElse:!0)this.parseIf("oo-else-if");else{this.doElse=!1,this.if||(this.if={});const s=this.if.code||this.domNode.getAttribute("oo-else-if"),i=this.findBindPath(s,!1);this.if={code:s,value:!1},i&&this.dom.addIfList(i,this),this.takePlace()}}updateIf(){this.parse();const e=this.nextSibling;e&&(e instanceof ie||e instanceof oo)&&e.updateIf()}updateAll(){this.updateIf(),this.if.value&&this.childNodes.forEach(e=>{e.updateAll()})}}class oo extends ge{parse(){this.vType="else";const e=this.previousSibling;(e&&(e instanceof ge||e instanceof ie)?e.doElse:!0)?(this.returnPlace(),this.parseAttr()):this.takePlace()}updateIf(){this.parse()}updateAll(){this.parse(),this.doElse&&this.childNodes.forEach(e=>{e.updateAll()})}}class tn extends Pe{parse(){this.vType="each",this.itemData=this.getParentEachItemData(),this.takePlace(),this.each||(this.each={});const e=this.each.code||this.domNode.getAttribute("oo-each"),o=this.domNode.getAttribute("oo-item")||"$item",{value:s,bindPaths:i}=this.execCode(e,[]),r=A=>{this.domNode.removeAttribute("oo-each"),this.each={code:e,value:A,item:o},this.each.cacheValue=se(A),A&&$r(A)&&(this.setIteratorValue(A),A.forEach((c,d)=>{const B=this.domNode.cloneNode(!0);B.setAttribute("oo-each-item",`${d},${o}`),B.eachNode=this,this.endNode.parentNode.insertBefore(B,this.endNode)})),i&&this.dom.addEachList(i,this)};Ot(s)?(r([]),s.then(A=>{this.updateEach(A)})):r(s),this.inited=!0}proxyValue(e,o){const s=e||{};if(!s.hasOwnProperty("$OOProxy")){const i=new Proxy(s,{get:(r,A)=>{const c=r[A];return(Array.isArray(c)||m(c)==="object")&&A!=="parent"?r[A]=this.proxyValue(c,o):c},set:(r,A,c,d)=>{const B=r[A],p=(Array.isArray(c)||m(c)==="object")&&A!=="parent"?this.proxyValue(c,o):c;if(c!==B){const Q=Reflect.set(r,A,p,d);return!A.startsWith("$OO")&&A!=="parent"&&this.updateEachItem(r.$OOProxy),Q}return!0}});return i.$OOProxy=o,i}return e}setIteratorValue(e){const o=[];e.forEach((s,i)=>{o.push(this.proxyValue({value:s,index:i,position:i,$OOIndex:i},i))}),this.each.value=o}takePlace(){this.beginNode||(this.beginNode=document.createComment("each begin"),this.domNode.parentNode.insertBefore(this.beginNode,this.domNode)),this.endNode||(this.endNode=document.createComment("each end"),this.domNode.parentNode.insertBefore(this.endNode,this.domNode)),this.domNode.remove()}updateEachItem(e){let o=this.nextSibling;for(;o&&e>0;)o=o.nextSibling,e--;o&&o.updateAll()}updateEach(e,o){this.itemData=this.getParentEachItemData();const{code:s,item:i}=this.each,r=e||this.execCode(s,[]).value,A=c=>{c&&this.setIteratorValue(c);const d=c;let B=this.beginNode.nextSibling;const p=Array(d.length);for(p.fill(null);B&&B!==this.endNode;)if(B.vNode){const D=B.vNode.itemData.$item.value,M=d.indexOf(D);if(M!==-1)p[M]=B,B=B.nextSibling;else{const y=B;B=B.nextSibling,y.remove()}}else B=B.nextSibling;let Q=this.beginNode;d.forEach((D,M)=>{const y=p[M];if(y){const H=Q.nextSibling,pe=!this.each.cacheValue||!this.each.cacheValue[M]||JSON.stringify(this.each.value[M].value)!==JSON.stringify(this.each.cacheValue[M]);if(y!==H||pe){debugger;y.vNode.eachItem&&(y.vNode.eachItem.idx=M,y.vNode.eachItem.key=i),y.setAttribute("oo-each-item",`${M},${i}`),y.vNode.computeItemData(),y!==H&&Q.parentNode.insertBefore(y,Q.nextSibling),pe&&(y.vNode.updateAll(),this.each.cacheValue[M]=se(this.each.value[M].value))}Q=y}else{const H=this.domNode.cloneNode(!0);H.setAttribute("oo-each-item",`${M},${i}`),H.eachNode=this,Q.parentNode.insertBefore(H,Q.nextSibling),Q=H;const pe=this.parentNode.createNode(H);this.parentNode.insertAfter(pe,Q||this)}})};Ot(r)?r.then(c=>{this.updateEach(c)}):A(r)}bindDom(e,o){const s=e.childNodes;for(let i=0;i<s.length;i++){const r=s[i];if(r.nodeType===Node.ELEMENT_NODE){const A=o.createNode(r);this.bindDom(r,A)}else r.nodeValue.trim()?o.createNode(r):(r.parentNode.removeChild(r),i--)}}updateAll(){this.inited?this.updateEach():this.parse()}}class on extends Pe{parse(){this.vType="eachItem",this.computeItemData(),this.parseAttr()}getIndexKey(){return this.eachItem||(()=>{const e=this.domNode.getAttribute("oo-each-item").split(/\s*,\s*/),o=e[0],s=e[1];return{idx:o,key:s}})()}computeItemData(){const{idx:e,key:o}=this.eachItem=this.getIndexKey();this.domNode.removeAttribute("oo-each-item");const s=this.domNode.eachNode;if(s){const i=s.each.value[e],r=s.getParentEachItemData();i.parent=r?r.$item:null,this.itemData={},this.itemData.$item=this.itemData[o]=i}}updateAll(){const e=this.inited;this.parse(),e&&this.childNodes.forEach(o=>{o.updateAll()})}}class sn extends to{constructor(o,s){super();u(this,"domNode",null);u(this,"bind",null);u(this,"module",null);u(this,"childNodes",[]);u(this,"bindList",new Map);u(this,"modelList",new Map);u(this,"ifList",new Map);u(this,"eachList",new Map);u(this,"subComponentList",new Map);u(this,"watchComponentList",new Map);u(this,"slotList",new Map);this.bind=s.bind,this.domNode=o,this.module=s,this.domNode.vNode=this,this.vType="dom",this.dom=this}createNode(o,s,i){switch(o.nodeType){case Node.ELEMENT_NODE:return new(this.getElementNodeType(o))(o,this,s||this,i);case Node.TEXT_NODE:return new Gs(o,this,s||this);case Node.CDATA_SECTION_NODE:return new qr(o,this,s||this);default:return null}}getElementNodeType(o){return o.hasAttribute("oo-if")?ge:o.hasAttribute("oo-else-if")?ie:o.hasAttribute("oo-else")?oo:o.hasAttribute("oo-each")?tn:o.hasAttribute("oo-each-item")?on:Pe}addModelList(o,s){const i=this.modelList.get(o)||new Set;i.add(s),this.modelList.set(o,i)}addBindList(o,s){o.length&&o.forEach(i=>{const r=this.bindList.get(i)||new Set;r.add(s),this.bindList.set(i,r)})}addBind(o,s){const i=this.bindList.get(o)||new Set;i.add(s),this.bindList.set(o,i)}addSlot(o,s){this.slotList.set(o,s)}addMapList(o,s,i){o.length&&o.forEach(r=>{const A=this[i].get(r)||new Set;A.add(s),this[i].set(r,A)})}addIfList(o,s){this.addMapList(o,s,"ifList")}addEachList(o,s){this.addMapList(o,s,"eachList")}addSubComponentList(o,s){this.addMapList(o,s,"subComponentList")}addWatchComponentList(o,s){this.addMapList(o,s,"watchComponentList")}update(o){this.currentUpdatePath||(this.currentUpdatePath=new Set),this.currentUpdatePath.add(o),this.deferUpdate(this.doUpdate)}deferUpdate(o,s=5){this.updateTimer&&(clearTimeout(this.updateTimer),this.updateTimer=null),this.updateTimer=setTimeout(()=>{o.apply(this)},s)}findUpdateNodeList(o,s){const i=new Set;return this.currentUpdatePath.forEach(r=>{Array.from(o.keys()).filter(c=>!r||r===c||r.startsWith(`${c}.`)||c.startsWith(`${r}.`)||c==="$"||r==="$").forEach(c=>{o.get(c).forEach(d=>{i.add(d)})}),s&&(i.forEach(c=>{c[s](r)}),i.clear())}),i}doUpdate(){this.currentUpdatePath&&this.currentUpdatePath.size&&(this.findUpdateNodeList(this.bindList).forEach(o=>{o.node.update(o.par)}),this.findUpdateNodeList(this.modelList).forEach(o=>{o.updateModel()}),this.findUpdateNodeList(this.ifList).forEach(o=>{o.updateIf()}),this.findUpdateNodeList(this.eachList).forEach(o=>{o.updateEach()}),this.findUpdateNodeList(this.subComponentList,"updateSubComponent"),this.findUpdateNodeList(this.watchComponentList).forEach(o=>{o.updateWatchComponent()}),this.currentUpdatePath.clear())}}var ke,Js,je,_s,He,Ps,ze,Os,Ge,Ls,Je,Vs,Ee,Wt,Z,De,_e,Ws,q,Ne,ue,Kt,ee,Ye,te,Ue,K,ae;const Y=class Y{constructor(e,o,s){I(this,ke);I(this,je);I(this,He);I(this,ze);I(this,Je);I(this,_e);I(this,q);I(this,ue);I(this,ee);I(this,te);I(this,K);u(this,"container",document.body);u(this,"vdom",null);this._fromBind=o.bind||{},this._opBind=e.bind||{},this.components=Object.assign({},o.components||{},e.components||{}),this.module=Object.assign({},o,e),this.module.bind={},this.module.components=this.components,this.module.$parent=this.module.$p=s,w(this,ke,Js).call(this),this.container=l.el(this.module.dom)||document.body,this.module.component=this,this.uuid=Gr()}async init(e,o){w(this,ee,Ye).call(this),w(this,te,Ue).call(this),this.nodes=null;const[s,i]=await Xr(et(this._fromBind,this.module),et(this._opBind,this.module));this.module.bind=this.proxyBind(Pt({},s,i)),await w(this,je,_s).call(this,e,o)}async render(){return await w(this,K,ae).call(this,"beforeRender"),this.nodes=this.module.template?this.loadHtmlText(this.module.template):null,this.styleNode=this.module.style?this.loadCssText(this.module.style):null,await w(this,K,ae).call(this,"afterRender"),this}rerender(){return w(this,ee,Ye).call(this),this.nodes=null,this.nodes=this.module.template?this.loadHtmlText(this.module.template):null,this}update(){this.vdom&&this.vdom.update()}updateModel(e){this.container.value=e;const o=new InputEvent("input",{view:window});this.container.dispatchEvent(o)}dispatchEvent(e){this.container.dispatchEvent(new CustomEvent(e,{detail:{module:this.module}}))}loadHtmlText(e){var r;const o=l("div");o.insertAdjacentHTML("afterbegin",`<template>${e}</template>`);const s=o.firstChild.content;s.normalize(),w(this,ze,Os).call(this,s),w(r=Y,Ge,Ls).call(r,s,this.vdom);const i=[];return s.childNodes.forEach(A=>i.push(A)),this.container.appendChild(s),this.vdom.domNode=this.container,o.remove(),i}proxyBind(e,o){const s=e||{};if(!s.hasOwnProperty("$OOProxy")){const i=new Proxy(s,{get:(r,A)=>{var p,Q;const{wv:c,owner:d}=w(p=Y,Z,De).call(p,r,A),B=w(Q=Y,Ee,Wt).call(Q,c);if((Array.isArray(B)||m(B)==="object")&&A!=="$OOWatch"&&d){const D=this.proxyBind(B,o?`${o}.${A}`:A);return c&&c.hasOwnProperty("$OOWatch")?c.value=D:r[A]=D,D}return B},set:(r,A,c,d)=>{var D,M;const{wv:B,owner:p}=w(D=Y,Z,De).call(D,r,A),Q=w(M=Y,Ee,Wt).call(M,B);if(m(c),c!==Q){const y=w(this,ue,Kt).call(this,r,A,c,d);return A.startsWith("$OO")||w(this,Je,Vs).call(this,r,A,p),y}return!0}});return i.$OOProxy=o&&o!=="$OOProp"?o:"",i}return e}loadCssText(e){const o=`css${this.uuid}`;let s=e.toString();if(s){l.addClass(this.container,o),s=s.replace(/\/\*(\s|\S)*?\*\//g,""),s=this.execInterpolation(s);const i=new RegExp("(.+)(?=[\\r\\n]*{)","g"),r="."+o+" ";s=s.replace(i,c=>{const d=c.trim();return d.startsWith("@")||d==="from"||d==="to"?d:d.split(/\s*,\s*/g).map(function(p){return r+p}).join(", ")});const A=l("style");return A.setAttribute("type","text/css"),A.setAttribute("id",o),document.head.appendChild(A),A.appendChild(document.createTextNode(s)),A}return null}execInterpolation(e){return e.replace(/{{(.*?)}}/gm,(o,s)=>s?Hs(`return (${s})`,this.module,{$:this.module.bind,$$:this.module.bind,$m:this.module}):"")}async destroy(){await w(this,K,ae).call(this,"beforeDestroy"),w(this,ee,Ye).call(this),w(this,te,Ue).call(this),this.nodes=null,this.styleNode=null,this.vdom=null,await w(this,K,ae).call(this,"afterDestroy")}changeStyle(e){if(e){const o=e.__esModule?e.default.toString():e.toString();w(this,te,Ue).call(this),this.styleNode=this.loadCssText(o)}}hide(){this.nodes&&this.nodes.forEach(e=>l.hide(e))}show(){this.nodes&&this.nodes.forEach(e=>l.show(e))}};ke=new WeakSet,Js=function(){const e={get(){let o=this;for(;o.$parent;)o=o.$parent;return o},enumerable:!0,configurable:!0};Object.defineProperties(this.module,{$topParent:e,$top:e})},je=new WeakSet,_s=async function(e,o){var c;const s=m(e),i={object(){return{opts:e}},element(){return{el:e,opts:o}},string(){return{el:document.querySelector(e),opts:o}}},{opts:r,el:A}=((c=i[s])==null?void 0:c.call(i))||{opts:o};A&&(this.container=this.module.dom=A),await w(this,He,Ps).call(this,r)},He=new WeakSet,Ps=async function(e){e&&(e.bind&&(e.bind=await et(e.bind,e),Pt(this.module.bind,e.bind),delete e.bind),e.components&&(Object.assign(this.module.components||{},e.components),delete e.components),Object.assign(this.module,e))},ze=new WeakSet,Os=function(e){this.vdom=new sn(e,this.module)},Ge=new WeakSet,Ls=function(e,o){const s=e.childNodes;for(let i=0;i<s.length;i++){const r=s[i];r.nodeType===Node.ELEMENT_NODE||r.nodeValue.trim()?o.createNode(r):(r.parentNode.removeChild(r),i--)}},Je=new WeakSet,Vs=function(e,o,s){const i=e.$OOProxy,r=o==="$OOValue"?i:i?`${i}.${o}`:o;this.vdom&&this.vdom.update(r)},Ee=new WeakSet,Wt=function(e){return m(e)==="object"&&e.hasOwnProperty("$OOWatch")?e.value:e},Z=new WeakSet,De=function(e,o){var s;if(e.hasOwnProperty(o))return e[o]&&e[o].hasOwnProperty("$OOValue")?{wv:e[o].$OOValue,owner:!0}:{wv:e[o],owner:!0};if(e.$OOProp){const{wv:i}=w(s=Y,Z,De).call(s,e.$OOProp,o);return{wv:i,owner:!1}}else return{wv:e[o],owner:!0}},_e=new WeakSet,Ws=function(e){switch(m(e)){case"function":e.apply(this.module);break}},q=new WeakSet,Ne=function(e,o,s){const i=e[o];return m(i)==="object"&&i.hasOwnProperty("$OOWatch")?(i.value=s,w(this,_e,Ws).call(this,i.$OOWatch),!0):(e[o]=s,!0)},ue=new WeakSet,Kt=function(e,o,s,i){if(e.hasOwnProperty(o))if(e[o]&&e[o].hasOwnProperty("$OOValue")){if(e[o].$OOProxy&&this.module.$parent){const r=e[o].$OOProxy.split("."),A=r.pop();let c=this.module.$parent.bind;r.forEach(d=>c=c[d]),c[A]!==s&&(c[A]=s)}return w(this,q,Ne).call(this,e[o],"$OOValue",s,i)}else return w(this,q,Ne).call(this,e,o,s,i);else return e.hasOwnProperty("$OOProp")&&e.$OOProp.hasOwnProperty(o)?w(this,ue,Kt).call(this,e.$OOProp,o,s,i):w(this,q,Ne).call(this,e,o,s,i)},ee=new WeakSet,Ye=function(){this.nodes&&this.nodes.forEach(e=>e.remove())},te=new WeakSet,Ue=function(){this.styleNode&&this.styleNode.parentNode.removeChild(this.styleNode)},K=new WeakSet,ae=function(e){return this.module[e]?this.module[e].apply(this.module):null},I(Y,Ge),I(Y,Ee),I(Y,Z);let Vt=Y;function et(t,e){return m(t)==="function"?t.call(e,e):t}function rn(t){return t?so.prototype.isPrototypeOf(t)?t.options:t.prototype||t||{}:{}}class so{constructor(e,o){e.style=e.style?e.style.toString():"",e.template=e.template?e.template.toString():"",this.options=e,this.from=rn(o)}async generate(e,o,s,i){const r=new Vt(this.options,this.from,s);return await r.init(e,o),i&&Object.keys(i).forEach(A=>{delete r.module.bind[A]}),r.module.bind.$OOProp=i,await r.render()}async render(){return await this.generate(...arguments)}}const N=function(t,e){return new so(t,e)};N.from=function(t,e){return N(e,t)};N.watch=function(t,e){return{$OOWatch:t,value:e}};const nn=`<header oo-element="headerNode">
    <img class="logo" src="./logo.png" alt="logo"/>
</header>
`,An=N({template:nn,autoUpdate:!0,afterRender(){window.setTimeout(()=>{l.setStyles(this.headerNode,{opacity:1})},100)}}),cn=`<main>
    <div class="main_area">
        <div class="title" oo-element="titleNode">
            <div class="title1">欢迎使用O2OA(翱途)企业应用开发平台</div>
            <div class="title2">首次启动服务器需要进行服务器初始化配置</div>
        </div>
        <div style="height: 7rem"></div>
        <div class="pane" oo-element="paneNode">
            <explain :explain="$.explain[$.step]"></explain>
            <div class="pane_layout">
<!--                <div>-->
                <password oo-if="$.step===0" :secret="$.secret"></password>
                <database oo-if="$.step===1" :database="$.database"></database>
                <restore oo-if="$.step===2" :restore="$.restore"></restore>
                <init-info oo-if="$.step===3" oo-prop="$"></init-info>
                <execute oo-if="$.step===4" oo-prop="$"></execute>
<!--                </div>-->
            </div>
        </div>
        <div class="bottom" style="height: 7rem" oo-element="bottomNode">
            <div class="current" style="left: {{$.step*6}}rem"></div>

            <div class="step"></div>
            <div class="step"></div>
            <div class="step"></div>
            <div class="step"></div>
            <div class="step"></div>
        </div>
    </div>
</main>
`,_={error:(t,e,o={},s)=>{s&&(o=Object.assign(o,{skin:"default",location:"topRight",marginTop:10,duration:5e3}));const r=Object.assign({title:t,text:e,duration:0,skin:"banner",marginTop:0,type:"error"},o);new bo(r);const A=Error(e,{cause:r.err});throw A.name=t,A},msg:(t,e,o,s={})=>{const i=Object.assign({title:t,text:e,duration:5e3,type:o,location:"topRight",marginTop:10},s);new bo(i)},success:(t,e,o={})=>{_.msg(t,e,"success",o)},info:(t,e,o={})=>{_.msg(t,e,"info",o)},warn:(t,e,o={})=>{_.msg(t,e,"warn",o)}},an="/jaxrs/secret/set",ln="/jaxrs/server/execute",hn="/jaxrs/server/stop",dn="/jaxrs/server/execute/status",gn="/x_desktop/res/config/config.json",Bn="/jaxrs/externaldatasources/check",En="/jaxrs/h2/check",un="/jaxrs/externaldatasources/list",pn="/jaxrs/externaldatasources/set",Qn="/jaxrs/externaldatasources/validate",wn="/jaxrs/restore/upload",mn="/jaxrs/restore/upload/cancel";async function P(t,e){try{const o=await fetch(t,{cache:"no-cache"});if(o.ok)return(await o.json()).data;{const s=await o.json(),i=new Error(s.message);throw e||_.error(`Get ${t} Status Code: ${o.status} (${o.statusText})`,i.message,{err:i}),i}}catch(o){throw e||_.error(`Get ${t} Failed`,o.message,{err:o}),o}}async function Oe(t,e,o){try{const s={method:"POST",body:e};o&&(s.headers={"Content-Type":o||"application/json"});const i=await fetch(t,s);if(i.ok)return(await i.json()).data;{const r=await i.json(),A=new Error(r.message);throw _.error(`Post ${t} Status Code: ${i.status} (${i.statusText})`,A.message,{err:A}),A}}catch(s){throw _.error(`Post ${t} Failed`,s.message,{err:s}),s}}async function fn(t){return await Oe(an,JSON.stringify({secret:t}),"application/json")}async function Mn(){return await P(un)}async function In(t){return await Oe(Qn,JSON.stringify({externalDataSources:[t]}),"application/json")}async function Cn(t){return await Oe(pn,JSON.stringify({externalDataSources:[t]}),"application/json")}async function bn(){const[t,e]=await Promise.all([P(Bn),P(En)]);return t.configured||e.configured}async function ko(t){return await Oe(wn,t)}async function yn(){return await P(mn)}async function Fn(){return await P(hn)}async function vn(){return await P(ln)}async function io(){try{return await P(dn,!0)}catch{return{status:"starting"}}}async function xn(){try{return await P(gn,!0),{status:"started"}}catch{return{status:"starting"}}}const Dn=`
<div class="pane_content">
    <div class="input_title">设置密码</div>
    <oo-input oo-model="secret.passStr" type="password" left-icon="password" placeholder="请设置管理员密码" skin="icon-right:var(--oo-color-main)" right-icon="{{($.secret.passStr && $m.checkPassword($.secret.passStr)) ? 'check' : ''}}"></oo-input>
    <div style="color: red; padding-left: 1em; font-size:0.875rem; height: 1rem;"><span oo-if="!$m.checkPassword($.secret.passStr)">密码必须6位以上，包含字母和数字</span></div>
    
    <div class="input_title">确认密码</div>
    <oo-input oo-model="secret.confirmPass" type="password" left-icon="password" placeholder="请再次输入密码" skin="icon-right:var(--oo-color-main)" right-icon="{{($.secret.passStr && $.secret.confirmPass && $m.checkConfirm($.secret.passStr, $.secret.confirmPass)) ? 'check' : ''}}"></oo-input>
    <div style="color: red; padding-left: 1em; font-size:0.875rem; height: 1rem;"><span oo-if="!$m.checkConfirm($.secret.passStr, $.secret.confirmPass)">确认密码和设置密码不一致</span></div>
</div>
<div class="actions">
    <oo-button @click="setPassword">下一步</oo-button>
</div>
`,Nn=N({template:Dn,autoUpdate:!0,checkConfirm(t,e){return!t||!e||t===e},checkPassword(t){return!t||/^(?=.*[a-z])(?=.*\d).{6,30}$/.test(t)},async setPassword(){if(!this.bind.secret.passStr||!this.bind.secret.confirmPass||!this.checkConfirm(this.bind.secret.passStr,this.bind.secret.confirmPass)||!this.checkPassword(this.bind.secret.passStr))return _.msg("设置管理员密码","您必须为管理员（xadmin）设置密码！","error",{container:this.$p.paneNode,location:"topRight"}),!1;await fn(this.bind.secret.passStr),this.bind.secret.passStr=this.bind.secret.passStr;const t=this.$p.bind.step+1;this.$p.bind.step=t<5?t:0}}),Yn=`
<div class="pane_content" style="padding: 2rem 2rem">
    <div class="input_title" style="padding: 0.5rem 0; margin-top: 0">选择数据库</div>
    <div oo-if="$.databaseConfigured!==true && $.databaseConfigured!==false" style="padding: 3rem 2rem; text-align: center">
        <div class="icon loading" style="display: block; height: 40px; width: 40px; margin: auto"></div>
    </div>
    
    <div oo-else-if="$.databaseConfigured===true" style="padding: 3rem 2rem; text-align: center">
        <div class="icon ooicon-check"></div>
        <div style="padding: 1rem 0; color: var(--oo-color-text2)">您已初始化了数据库设置！请直接点击“下一步”</div>
        <div style="padding: 1rem 0; font-size: 0.875rem; color: var(--oo-color-text3)">如需修改，请启动服务器后，进入“系统配置”-“服务器配置”-“数据库配置”中修改</div>
    </div>
    
    <div  oo-else style="color:#777777; font-size: 0.875rem">
        <div style="padding: 0.5em 0.3em; margin-top: 0.5rem; border: 1px solid #cccccc; border-radius: 0.5rem">
            <oo-radio-group name="database_type" col="4" oo-model="database.type" @change="changeDb" oo-element="databaseTypeNode">
                <oo-radio value="h2" name="database_type" style="margin: 0.3em 0em 0.3em 0">H2(内置数据库)</oo-radio>
                <oo-radio oo-each="$.databaseList" oo-item="db"  value="{{db.value.type}}" text="{{$.databaseName[db.value.type] || db.value.type}}" style="margin: 0.3em 0em 0.3em 0"></oo-radio>
            </oo-radio-group>
        </div>
    </div>
    <div oo-if="$.database.type!=='h2' && $.database.type!=='$configured'">
        <div class="input_title" style="padding: 0.5rem 0; margin-top: 1rem">数据库配置</div>
        <div style="color:#777777; font-size: 0.875rem">
            <div style="padding: 0.5em 0.3em; margin-top: 0.5rem; border: 1px solid #cccccc; border-radius: 0.5rem">
                <oo-textarea label="数据库连接：" style="margin-top:0.5rem; width: 98%" oo-model="externalDataSources.url" spellcheck="false"></oo-textarea>
                <div style="margin-top:1rem; margin-bottom:1rem; width: 98%; display: flex; justify-content: space-between;">
                    <oo-input label="用户名：　　" style="width: 48%" oo-model="externalDataSources.username"></oo-input>
                    <oo-input label="密码：" style="width: 48%" oo-model="externalDataSources.password"></oo-input>
                </div>
                
                <div style="width: 98%; display: flex; justify-content: space-between;">
                    <div style="font-size: 0.725rem; width: calc(100% - 8.5rem); padding-left: 0.5rem; display: flex; align-items: center;">
                        <div class="loading" oo-element="testLoading"></div>
                        <div oo-if="$.testDbMessage==='success'" style="color: green">连接成功！</div>
                        <div oo-if="$.testDbMessage && $.testDbMessage!=='success'"  style="color: red">连接失败:{{$.testDbMessage}}</div>
                    </div>
                    <oo-button @click="test" style="width: 8.5rem;">测试数据库连接</oo-button>
                </div>
               
            </div>
        </div>
    </div>
</div>
<div class="actions">
    <oo-button type="cancel" @click="stepPrev">上一步</oo-button>
    <oo-button @click="nextStep">下一步</oo-button>
</div>
`,Un=`
.loading {
  position: relative;
  width: 20px;
  height: 20px;
  border: 2px solid #000;
  border-top-color: rgba(0, 0, 0, 0.2);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.2);
  border-radius: 100%;
  display: none;
  animation: circle infinite 0.75s linear;
}
.icon{
    font-size: 3rem;
}
.ooicon-check{
    color: green;
}
.ooicon-cancel{
    color: red;
}
`,Sn=N({template:Yn,style:Un,autoUpdate:!0,bind(){return Mn().then(t=>{this.bind.databaseList=t}),this.databaseCheck(),{databaseName:{sqlserver:"SQL Server",oracle:"Oracle",postgresql:"PostgreSQL",mysql:"MySQL",dm:"达梦",kingbase:"人大金仓V7",kingbase8:"人大金仓V8",informix:"Informix",gbase:"南大通用",db2:"DB2"},externalDataSources:{},testDbMessage:"",databaseConfigured:null,h2:{},h2_upgrade:"no"}},async databaseCheck(){const t=await bn();this.bind.databaseConfigured=t},changeDb(t){if(this.bind.testDbMessage="",this.bind.database.type!=="h2"){const e=this.bind.databaseList.find(o=>o.type===this.bind.database.type);e&&(this.bind.externalDataSources=se(e.externalDataSources[0]))}this.bind.testDbMessage=""},async test(t){this.bind.testDbMessage="",l.setStyle(this.testLoading,"display","block"),t.target.setAttribute("disabled",!0);const e=await In(this.bind.externalDataSources);e[0].success?this.bind.testDbMessage="success":this.bind.testDbMessage=e[0].failureMessage,l.setStyle(this.testLoading,"display","none"),t.target.setAttribute("disabled",!1)},stepPrev(){const t=this.$p.bind.step-1;this.$p.bind.step=t<0?0:t},async nextStep(){this.bind.databaseConfigured?this.bind.database.type="$configured":this.bind.database.type!=="h2"?(await Cn(this.bind.externalDataSources),this.bind.database.type=this.bind.database.type,this.bind.database.url=this.bind.externalDataSources.url):this.bind.database.type=this.bind.database.type;const t=this.$p.bind.step+1;this.$p.bind.step=t<5?t:0}}),Tn=`
<div class="pane_content" style="padding: 2rem 2rem">
    <div class="input_title">初始化数据</div>
    <div class="upload_area" @dragover="dragover" @drop="drop" @dragout="dragout">
        <div>将zip文件拖动到此处</div>
        <div>或</div>
        <oo-button @click="selectFile">选择zip文件</oo-button>
    </div>
    <div class="upload_name" oo-if="!!$.restore.name">
        <div><span>已上传存储文件：</span><span style="color:var(--oo-color-main)">{{$.restore.name}}</span></div>
        <oo-button class="delete_button" @click="deleteUpload">删除</oo-button>
    </div>
    <div class="upload_name">
        <div style="font-size: 0.875rem; color:#666666">如果需要导入数据，请上传数据zip文件，如果不需要，可直接点击“下一步”</div>
    </div>
</div>
<div class="actions">
    <oo-button type="cancel" @click="stepPrev">上一步</oo-button>
    <oo-button @click="nextStep">下一步</oo-button>
</div>
<input type="file" @change="uploadFile" oo-element="uploadFileNode" style="display: none"/>
<div class="maskNode" oo-if="$.status==='uploading'">
    <div class="loading"></div>
    <div>正在上传数据文件 ... </div>
</div>
`,Rn=`
.upload_area {
    border: 0.12rem dashed #cccccc;
    border-radius: 1rem;
    width: 90%;
    height: 12rem;
    margin-top: 3rem;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    font-size: 14px;
}
.upload_area.over{
    background-color: #f1f1f1;
}
.upload_area>div{
    line-height: 1.8em
}
.upload_name{
    display: flex;
    width: 90%;
    justify-content: space-between;
    align-items: center;
    margin: 2rem 0;
}
.delete_button{
    font-size: 0.725rem;
    width: unset;
    height:1.5rem;
}
.loading {
  position: relative;
  width: 1.4rem;
  height: 1.4rem;
  border: 0.2rem solid #000;
  border-top-color: rgba(0, 0, 0, 0.2);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.2);
  border-radius: 100%;
  animation: circle infinite 0.75s linear;
  margin-right: 0.5rem;
}
.maskNode {
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: #eeeeee;
    opacity: 0.8;
    display: flex;
    justify-content: center;
    align-items: center;
    border-top-right-radius: 1rem;
    border-bottom-right-radius: 1rem;
}
`,kn=N({template:Tn,style:Rn,autoUpdate:!0,bind(){return{status:""}},dragover(t){t.preventDefault(),l.addClass(t.target,"over")},dragout(t){t.preventDefault(),l.removeClass(t.target,"over")},async drop(t){if(t.preventDefault(),this.bind.restore.name="",l.removeClass(t.target,"over"),t.dataTransfer.files&&t.dataTransfer.files.length){const e=t.dataTransfer.files[0],o=new FormData;o.append("file",e),this.bind.status="uploading",await ko(o),this.bind.restore.name=e.name,this.bind.status=""}},async uploadFile(t){if(t.preventDefault(),this.bind.restore.name="",t.target.files&&t.target.files.length){const e=t.target.files[0],o=new FormData;o.append("file",e),this.bind.status="uploading",await ko(o),this.bind.restore.name=e.name,this.bind.status=""}},selectFile(){this.uploadFileNode&&this.uploadFileNode.click()},async deleteUpload(){await yn(),this.bind.restore.name=""},stepPrev(){const t=this.$p.bind.step-1;this.$p.bind.step=t<0?0:t},nextStep(){const t=this.$p.bind.step+1;this.$p.bind.step=t<5?t:0}}),jn=`
<div class="pane_content" style="padding: 2rem 2rem">
    <div class="input_title">初始化信息</div>
    
    <div class="infoArea">
        <div class="line">
            <div oo-if="$.secret.passStr" class="info"><span class="icon ooicon-check"></span>管理员密码已设置</div>
            <div oo-if="!$.secret.passStr" class="info"><span class="icon ooicon-cancel"></span>管理员密码未设置</div>
        </div>
        
        <div class="line">
            <div class="info"><span class="icon ooicon-check"></span>数据库：{{$.database.type==='$configured' ? '已初始化数据库' : $.database.type}}</div>
        </div>
        
        <div class="line">
            <div oo-if="$.restore.name" class="info"><span class="icon ooicon-check"></span>导入数据文件：{{$.restore.name}}</div>
            <div oo-if="!$.restore.name" class="info"><span class="icon ooicon-cancel"></span>不导入数据文件</div>
        </div>
    </div>
    
    <div>
        <div oo-if="$.status==='unknown'" class="icon ooicon-error" style="color:#ffc42bde"></div>
        <div oo-if="$.status==='waiting'" class="info">等待服务器执行初始化 ... </div>
    </div>
  
</div>
<div class="actions">
    <oo-button type="cancel" @click="stepPrev">上一步</oo-button>
    <oo-button type="cancel" @click="stepCancel">取消</oo-button>
    <oo-button @click="nextStep">执行</oo-button>
</div>
<input type="file" @change="uploadFile" oo-element="uploadFileNode" style="display: none"/>
`,Hn=`
.info {
    display: flex;
    align-items: center;
}
.line{
    padding: 1rem;
}
.icon{
    font-size: 2rem;
    margin-right: 0.3rem;
}
.ooicon-check{
    color: green;
}
.ooicon-cancel{
    color: #999999;
}
.infoArea{
    display: flex;
    flex-direction: column;
    height: 16rem;
    justify-content: center;
    padding-left: 2rem;
}
`,zn=N({template:jn,style:Hn,autoUpdate:!0,stepPrev(){const t=this.$p.bind.step-1;this.$p.bind.step=t<0?0:t},async stepCancel(t){this.dialog=new xo(t.target.parentElement,{modalArea:this.$p.dom.firstElementChild,content:"是否确定要关闭初始化服务器？",width:"30em",position:"center",events:{ok:async()=>{await Fn(),this.$p.bind.step=4,this.$p.bind.serverStop=!0}}}),this.dialog.show()},nextStep(t){this.dialog=new xo(t.target.parentElement,{modalArea:this.$p.dom.firstElementChild,content:"是否确定要执行初始化任务？",width:"30em",position:"center",events:{ok:async()=>{await vn();const e=this.$p.bind.step+1;this.$p.bind.step=e<5?e:0}}}),this.dialog.show()}}),Gn=`
<div class="pane_content" style="padding: 2rem 2rem">
    <div class="input_title">执行服务器初始化</div>
    
    <div class="infoArea">
        <div oo-if="$.status==='waiting' || $.status==='running' || $.status==='starting'" class="loading" oo-element="testLoading"></div>
        <div oo-if="$.status==='success' || $.status==='started'" class="icon ooicon-check"></div>
        <div oo-if="$.status==='unknown'" class="icon ooicon-error" style="color:#ffc42bde"></div>
        
        <div oo-if="$.status==='waiting'" class="info">等待服务器执行初始化 ... </div>
        <div oo-if="$.status==='running'" class="info">服务器初始化正在执行中 ... </div>
        <div oo-if="$.status==='starting'" class="info">正在启动O2OA(翱途)服务器 ... </div>
        
        <div oo-if="$.status==='success'" class="info">服务器初始化执行成功，即将启动O2OA(翱途)服务器！</div>
        <div oo-if="$.status==='started'" class="info">服务器初始化执行成功，O2OA(翱途)服务器已启动！</div>
        <div oo-if="$.status==='started'" class="info"><oo-button style="margin-left:0" @click="gotoIndex">进入系统登录页面</oo-button></div>
        
        <div oo-if="$.status==='failure' || $.status==='stop'" class="icon ooicon-cancel"></div>
        <div oo-if="$.status==='failure'" class="info">服务器初始化执行失败 </div>
        <div oo-if="$.status==='failure'" class="info" style="font-size: 0.875rem; color: red; text-align: center;">{{$.failureMessage || $.messages.join(', ')}}</div>
        <div oo-if="$.status==='failure'" class="info" style="font-size: 0.875rem; color: #666666">您可以重启服务器后重新进行初始化配置！</div>
        
        <div oo-if="$.status==='unknown'" class="info">无法获取服务器状态，请查看服务器控制台信息</div>
        
        <div oo-if="$.status==='stop'" class="info">初始化服务器已关闭，您可以再次手工启动服务器，以完成初始化配置。</div>
        
<!--        <div class="line">-->
<!--            <div oo-if="$.secret.passStr" class="info"><span class="icon ooicon-check"></span>管理员密码已设置</div>-->
<!--            <div oo-if="!$.secret.passStr" class="info"><span class="icon ooicon-cancel"></span>管理员密码未设置</div>-->
<!--        </div>-->
<!--        -->
<!--        <div class="line">-->
<!--            <div class="info"><span class="icon ooicon-check"></span>数据库：{{$.database.type}}</div>-->
<!--        </div>-->
<!--        -->
<!--        <div class="line">-->
<!--            <div oo-if="$.restore.name" class="info"><span class="icon ooicon-check"></span>导入数据文件：{{$.restore.name}}</div>-->
<!--            <div oo-if="!$.restore.name" class="info"><span class="icon ooicon-cancel"></span>不导入数据文件</div>-->
<!--        </div>-->
    </div>
  
</div>
`,Jn=`
.info {
    display: flex;
    align-items: center;
    margin: 0.5rem;
}
.line{
    padding: 1rem;
}
.icon{
    font-size: 4rem;
    margin: 1rem;
}
.ooicon-check{
    color: #66cc80;
}
.ooicon-cancel{
    color: red;
}
.infoArea{
    display: flex;
    flex-direction: column;
    height: 25rem;
    justify-content: center;
    padding-left: 2rem;
    align-items: center;
}
.loading {
  position: relative;
  width: 3rem;
  height: 3rem;
  border: 0.25rem solid #000;
  border-top-color: rgba(0, 0, 0, 0.2);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.2);
  border-radius: 100%;
  animation: circle infinite 0.75s linear;
  margin: 2rem;
}
`,_n=N({template:Gn,style:Jn,autoUpdate:!0,bind(){return{status:"success",messages:[],failureMessage:"",checkCount:0}},async afterRender(){this.$p.bind.serverStop?this.bind.status="stop":(this.checkCount=0,this.timeoutCheck())},async timeoutCheck(){this.bind.status==="starting"?await this.checkServer():await this.check(),this.checkCount>150?this.bind.status="unknown":this.bind.status==="started"||this.bind.status==="failure"||window.setTimeout(()=>{this.timeoutCheck()},2e3)},async check(){const t=await io();this.bind.status=t.status,this.bind.failureMessage=t.failureMessage||"",this.bind.messages=t.messages||[],this.checkCount++},async checkServer(){const t=await xn();this.bind.status=t.status,this.bind.failureMessage=t.failureMessage||"",this.bind.messages=t.messages||[],this.checkCount++},gotoIndex(){window.location=`/?${new Date().getTime()}`}}),Pn=`
<div class="explain">
    <div class="explain_content">
        <div class="explain_title ooicon-{{$.explain.icon}}">{{$.explain.title}}</div>
        <div oo-each="$.explain.textList" oo-item="text" class="explain_text">
            <p oo-html="{{text.value}}"></p>
        </div>
    </div>
</div>
`,On=N({template:Pn,autoUpdate:!0}),Ln=N({template:cn,autoUpdate:!0,components:{password:Nn,explain:On,database:Sn,restore:kn,initInfo:zn,execute:_n},async bind(){const e=(await io()).status==="waiting"?0:4;return{explain:[{icon:"password",title:"设置管理员密码",textList:["首次启动服务器，您必须为超级管理员（xadmin）设置一个密码。","密码长度必须6位以上，同时包含数字和字母。","请牢记此密码!"]},{icon:"database",title:"设置数据库",textList:["O2OA（翱途）平台内置H2数据库，它是一个内嵌式的内存数据库，适合用于开发环境、功能演示环境，并不适合用作正式环境。","如果作为正式环境使用，建议您使用拥有更高性能并且更加稳定的商用级别数据库。如Mysql8, Oracle12C, SQLServer 2012等","您可以在此初始化服务器页面选择使用内置H2数据库，或外部数据库。","更多数据库配置选项可在服务器初始化后，进入“系统配置”应用进行设置"]},{icon:"import",title:"初始化数据",textList:["您可以将从其它服务器导出的数据包，在此页面中导入，以便于快速恢复或搭建应用。","在您已有服务器进行导出操作（ctl -dd 命令，或在“系统配置”的“数据库配置”中操作），会在服务器目录“o2server/local/dump”下得到“dumpData_时间”的文件夹，将其打包为zip文件后，可在服务器初始化时导入所有数据","关于数据的导出可查看：",'<a href="https://www.o2oa.net/cms/serverdeployment/256.html" style="color: #ffffff; text-indent: 0em; display: block;" target="_blank">《数据导出导入与系统数据备份》</a>','<a href="https://www.o2oa.net/cms/videoproduct/455.html" style="color: #ffffff; text-indent: 0em; display: block;" target="_blank">《系统配置-服务配置-数据库配置》</a>']},{icon:"import",title:"初始化信息",textList:["您已经准备好了服务器初始化配置，请确认您的初始化信息。","点击“执行”按钮，执行服务器初始化，完成后服务器会自动启动。","点击“取消”按钮，取消服务器初始化，并关闭初始化服务器，您可以再次手工启动服务器，以完成初始化配置。"]},{icon:"reload",title:"执行初始化",textList:["服务器正在执行初始化任务，执行完成后会自动启动服务器。"]},{icon:"cancel",title:"取消初始化",textList:["取消服务器初始化，服务器已关闭，您可以再次手工启动服务器，以完成初始化配置"]}],step:e,serverStop:!1}},afterRender(){window.setTimeout(()=>{l.setStyles(this.titleNode,{top:0,opacity:1})},600),window.setTimeout(()=>{l.setStyles(this.paneNode,{opacity:1}),l.setStyles(this.bottomNode,{opacity:1})},1100)}}),Vn=`<footer oo-element="footerNode">
    <div>
        <div class="link">
            <ul>
                <li><a href="https://www.o2oa.net/handbook.html" target="_blank">产品文档</a></li>
                <li><a href="https://www.o2oa.net/develop.html" target="_blank">开发社区</a></li>
                <li><a href="https://www.o2oa.net/forum/" target="_blank">藕粉社区</a></li>
                <li><a href="https://www.o2oa.net/market/" target="_blank">应用市场</a></li>
            </ul>
            <div>咨询电话：<span style="color: #fff904; font-size:1.2em">400-888-0545 0571-88480535</span></div>
            <div>O2OA开发平台官网：<a href="https://www.o2oa.net/" target="_blank">https://www.o2oa.net</a></div>
        </div>
        <div class="code">
            <div class="code_item">
                <div><img alt="官方微博" src="./pic_code_weibo.png"></div>
                <div>官方微博</div>
            </div>
            <div class="code_item">
                <div><img alt="官方微信" src="./pic_code_weixin.png"></div>
                <div>官方微信</div>
            </div>
            <div class="code_item">
                <div><img alt="售前顾问" src="./pic_code_guwen.png"></div>
                <div>售前顾问</div>
            </div>
        </div>
        <div class="source">
            <div class="source_code">
                <div class="source_item">
                    <a href="https://github.com/o2oa/o2oa" target="_blank">
                        <div><img class="icon_github" alt="GitHub" src="./icon_github.png"></div>
                        <div>GitHub</div>
                    </a>
                </div>
                <div class="source_item">
                    <a href="https://gitee.com/o2oa/O2OA" target="_blank">
                        <div><img alt="gitee" src="./icon_gitee.png"></div>
                        <div>gitee</div>
                    </a>
                </div>
                <div class="source_item">
                    <a href="https://gitlab.com/o2oa/o2oa" target="_blank">
                        <div><img alt="GitLab" src="./icon_gitlab.png"></div>
                        <div>GitLab</div>
                    </a>
                </div>
                <div class="source_item">
                    <a href="https://gitcode.net/O2OA/o2oa" target="_blank">
                        <div><img alt="GitCode" src="./icon_gitcode.png"></div>
                        <div>GitCode</div>
                    </a>
                </div>
            </div>

        </div>
    </div>
</footer>
`,Wn=N({template:Vn,autoUpdate:!0,afterRender(){window.setTimeout(()=>{l.setStyles(this.footerNode,{opacity:1})},1600)}}),Kn=`<index-header></index-header>
<index-main oo-prop="$"></index-main>
<index-footer></index-footer>
`,Xn=N({template:Kn,autoUpdate:!0,components:{indexHeader:An,indexMain:Ln,indexFooter:Wn},bind(){return{secret:{passStr:"",confirmPass:""},database:{type:"h2",url:"",username:"",password:""},restore:{name:""}}}}),$n=async()=>{(await io()).status==="starting"?window.location=`/?${new Date().getTime()}`:Xn.generate(document.body)};$n();
