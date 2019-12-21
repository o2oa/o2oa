/**
 * @fileoverview 百度地图的添加标注工具类，对外开放。
 * 允许用户在地图上点击后添加一个点标注，允许用户设定标注的图标样式。
 * 主入口类是<a href="symbols/BMapLib.MarkerTool.html">MarkerTool</a>，
 * 基于Baidu Map API 1.2。
 *
 * @author Baidu Map Api Group
 * @version 1.2
 */

/**
 * @namespace BMap的所有library类均放在BMapLib命名空间下
 */
var BMapLib = window.BMapLib = BMapLib || {};
if(typeof BMapLib._toolInUse == "undefined"){

    BMapLib._toolInUse = false; //该工具是否在使用，避免多个鼠标工具一起使用的情况
                                //如：用户打开了添加标注工具，就不能再打开测距工具。
}
(function() {
    /**
     * baidu tangram 代码部分，tangram代码提供了一些基础的操作，如：类的继承、
     * 事件的派发、事件的绑定等，而且兼容各种浏览器，在tangram基础上构建MarkerTool
     * 比较快捷。
     */
    var baidu = baidu || {guid : "$BAIDU$"};

    (function() {
        // 一些页面级别唯一的属性，需要挂载在window[baidu.guid]上
        window[baidu.guid] = {};

        /**
         * 将源对象的所有属性拷贝到目标对象中
         * @name baidu.extend
         * @function
         * @grammar baidu.extend(target, source)
         * @param {Object} target 目标对象
         * @param {Object} source 源对象
         * @returns {Object} 目标对象
         */
        baidu.extend = function (target, source) {
            for (var p in source) {
                if (source.hasOwnProperty(p)) {
                    target[p] = source[p];
                }
            }
            return target;
        };

        /**
         * @ignore
         * @namespace
         * @baidu.lang 对语言层面的封装，包括类型判断、模块扩展、继承基类以及对象自定义事件的支持。
         * @property guid 对象的唯一标识
         */
        baidu.lang = baidu.lang || {};

        /**
         * 返回一个当前页面的唯一标识字符串。
         * @function
         * @grammar baidu.lang.guid()
         * @returns {String} 当前页面的唯一标识字符串
         */
        baidu.lang.guid = function() {
            return "TANGRAM__" + (window[baidu.guid]._counter ++).toString(36);
        };

        window[baidu.guid]._counter = window[baidu.guid]._counter || 1;

        /**
         * 所有类的实例的容器
         * key为每个实例的guid
         */
        window[baidu.guid]._instances = window[baidu.guid]._instances || {};

        /**
         * Tangram继承机制提供的一个基类，用户可以通过继承baidu.lang.Class来获取它的属性及方法。
         * @function
         * @name baidu.lang.Class
         * @grammar baidu.lang.Class(guid)
         * @param {string} guid	对象的唯一标识
         * @meta standard
         * @remark baidu.lang.Class和它的子类的实例均包含一个全局唯一的标识guid。
         * guid是在构造函数中生成的，因此，继承自baidu.lang.Class的类应该直接或者间接调用它的构造函数。<br>
         * baidu.lang.Class的构造函数中产生guid的方式可以保证guid的唯一性，及每个实例都有一个全局唯一的guid。
         */
        baidu.lang.Class = function(guid) {
            this.guid = guid || baidu.lang.guid();
            window[baidu.guid]._instances[this.guid] = this;
        };

        /**
         * 判断目标参数是否string类型或String对象
         * @name baidu.lang.isString
         * @function
         * @grammar baidu.lang.isString(source)
         * @param {Any} source 目标参数
         * @shortcut isString
         * @meta standard
         *
         * @returns {boolean} 类型判断结果
         */
        baidu.lang.isString = function (source) {
            return '[object String]' == Object.prototype.toString.call(source);
        };

        /**
         * 判断目标参数是否为function或Function实例
         * @name baidu.lang.isFunction
         * @function
         * @grammar baidu.lang.isFunction(source)
         * @param {Any} source 目标参数
         * @returns {boolean} 类型判断结果
         */
        baidu.lang.isFunction = function (source) {
            return '[object Function]' == Object.prototype.toString.call(source);
        };

        /**
         * 重载了默认的toString方法，使得返回信息更加准确一些。
         * @return {string} 对象的String表示形式
         */
        baidu.lang.Class.prototype.toString = function(){
            return "[object " + (this._className || "Object" ) + "]";
        };

        /**
         * 释放对象所持有的资源，主要是自定义事件。
         * @name dispose
         * @grammar obj.dispose()
         */
        baidu.lang.Class.prototype.dispose = function(){
            delete window[baidu.guid]._instances[this.guid];
            for(var property in this){
                if (!baidu.lang.isFunction(this[property])) {
                    delete this[property];
                }
            }
            this.disposed = true;
        };

        /**
         * 自定义的事件对象。
         * @function
         * @name baidu.lang.Event
         * @grammar baidu.lang.Event(type[, target])
         * @param {string} type	 事件类型名称。为了方便区分事件和一个普通的方法，事件类型名称必须以"on"(小写)开头。
         * @param {Object} [target]触发事件的对象
         * @meta standard
         * @remark 引入该模块，会自动为Class引入3个事件扩展方法：addEventListener、removeEventListener和dispatchEvent。
         * @see baidu.lang.Class
         */
        baidu.lang.Event = function (type, target) {
            this.type = type;
            this.returnValue = true;
            this.target = target || null;
            this.currentTarget = null;
        };

        /**
         * 注册对象的事件监听器。引入baidu.lang.Event后，Class的子类实例才会获得该方法。
         * @grammar obj.addEventListener(type, handler[, key])
         * @param 	{string}   type         自定义事件的名称
         * @param 	{Function} handler      自定义事件被触发时应该调用的回调函数
         * @param 	{string}   [key]		为事件监听函数指定的名称，可在移除时使用。如果不提供，方法会默认为它生成一个全局唯一的key。
         * @remark 	事件类型区分大小写。如果自定义事件名称不是以小写"on"开头，该方法会给它加上"on"再进行判断，即"click"和"onclick"会被认为是同一种事件。
         */
        baidu.lang.Class.prototype.addEventListener = function (type, handler, key) {
            if (!baidu.lang.isFunction(handler)) {
                return;
            }
            !this.__listeners && (this.__listeners = {});
            var t = this.__listeners, id;
            if (typeof key == "string" && key) {
                if (/[^\w\-]/.test(key)) {
                    throw("nonstandard key:" + key);
                } else {
                    handler.hashCode = key;
                    id = key;
                }
            }
            type.indexOf("on") != 0 && (type = "on" + type);
            typeof t[type] != "object" && (t[type] = {});
            id = id || baidu.lang.guid();
            handler.hashCode = id;
            t[type][id] = handler;
        };

        /**
         * 移除对象的事件监听器。引入baidu.lang.Event后，Class的子类实例才会获得该方法。
         * @grammar obj.removeEventListener(type, handler)
         * @param {string}   type     事件类型
         * @param {Function|string} handler  要移除的事件监听函数或者监听函数的key
         * @remark 	如果第二个参数handler没有被绑定到对应的自定义事件中，什么也不做。
         */
        baidu.lang.Class.prototype.removeEventListener = function (type, handler) {
            if (baidu.lang.isFunction(handler)) {
                handler = handler.hashCode;
            } else if (!baidu.lang.isString(handler)) {
                return;
            }
            !this.__listeners && (this.__listeners = {});
            type.indexOf("on") != 0 && (type = "on" + type);
            var t = this.__listeners;
            if (!t[type]) {
                return;
            }
            t[type][handler] && delete t[type][handler];
        };

        /**
         * 派发自定义事件，使得绑定到自定义事件上面的函数都会被执行。引入baidu.lang.Event后，Class的子类实例才会获得该方法。
         * @grammar obj.dispatchEvent(event, options)
         * @param {baidu.lang.Event|String} event 	Event对象，或事件名称(1.1.1起支持)
         * @param {Object} options 扩展参数,所含属性键值会扩展到Event对象上(1.2起支持)
         * @remark 处理会调用通过addEventListenr绑定的自定义事件回调函数之外，还会调用直接绑定到对象上面的自定义事件。
         * 例如：<br>
         * myobj.onMyEvent = function(){}<br>
         * myobj.addEventListener("onMyEvent", function(){});
         */
        baidu.lang.Class.prototype.dispatchEvent = function (event, options) {
            if (baidu.lang.isString(event)) {
                event = new baidu.lang.Event(event);
            }
            !this.__listeners && (this.__listeners = {});
            options = options || {};
            for (var i in options) {
                event[i] = options[i];
            }
            var i, t = this.__listeners, p = event.type;
            event.target = event.target || this;
            event.currentTarget = this;
            p.indexOf("on") != 0 && (p = "on" + p);
            baidu.lang.isFunction(this[p]) && this[p].apply(this, arguments);
            if (typeof t[p] == "object") {
                for (i in t[p]) {
                    t[p][i].apply(this, arguments);
                }
            }
            return event.returnValue;
        };

        /**
         * 为类型构造器建立继承关系
         * @name baidu.lang.inherits
         * @function
         * @grammar baidu.lang.inherits(subClass, superClass[, className])
         * @param {Function} subClass 子类构造器
         * @param {Function} superClass 父类构造器
         * @param {string} className 类名标识
         * @remark 使subClass继承superClass的prototype，
         * 因此subClass的实例能够使用superClass的prototype中定义的所有属性和方法。<br>
         * 这个函数实际上是建立了subClass和superClass的原型链集成，并对subClass进行了constructor修正。<br>
         * <strong>注意：如果要继承构造函数，需要在subClass里面call一下，具体见下面的demo例子</strong>
         * @shortcut inherits
         * @meta standard
         * @see baidu.lang.Class
         */
        baidu.lang.inherits = function (subClass, superClass, className) {
            var key, proto,
                selfProps = subClass.prototype,
                clazz = new Function();
            clazz.prototype = superClass.prototype;
            proto = subClass.prototype = new clazz();
            for (key in selfProps) {
                proto[key] = selfProps[key];
            }
            subClass.prototype.constructor = subClass;
            subClass.superClass = superClass.prototype;

            if ("string" == typeof className) {
                proto._className = className;
            }
        };

    })();


    /**
     * MarkerTool代码部分, 此类继承基类baidu.lang.Class，便于派发自定义事件，如：markend事件派发
     * @exports MarkerTool as BMapLib.MarkerTool
     */
    var MarkerTool =
    /**
     * MarkerTool类的构造函数
     * @class 地图上添加标注类，实现点击地图添加点标注<b>入口</b>。
     * 实例化该类后，即可调用该类提供的<a href="symbols/BMapLib.MarkerTool.html#open">open</a>
     * 方法开启添加点标注状态。
     *
     * @constructor
     * @param {Map} map Baidu map的实例对象
     * @param {Json Object} opts 可选的输入参数，非必填项。可输入选项包括：
     * <br />"<b>icon</b>" : {Icon} 标注使用到的图标，标注时候鼠标跟随样式也通过此属性设置
     * <br />"<b>followText</b>" : {String} 跟随鼠标移动的说明文字，默认为空
     * <br />"<b>autoClose</b>" : {Boolean} 是否在每次添加完Marker后自动关闭工具
     *
     * @example <b>参考示例：</b><br />
     * var map = new BMap.Map("container");<br />map.centerAndZoom(new BMap.Point(116.404, 39.915), 15);<br />var mkrTool = new BMapLib.MarkerTool(map, {followText: "添加一个点"});
     */
        BMapLib.MarkerTool = function(map, opts){
            baidu.lang.Class.call(this);//继承基类baidu.lang.Class的构造函数
            this._map = map;
            this._opts = {
                icon: MarkerTool.SYS_ICONS[8], //默认选择红色雨滴样式
                followText: "点击地图添加定位标记", //鼠标跟随文字提示
                autoClose: true //是否添加完毕标注就关闭此工具
            };

            baidu.extend(this._opts, opts);//用户设定参数覆盖默认设定参数

            this._isOpen = false; // 表示控件项当前的状态
            this._opts.followText = this._checkStr(this._opts.followText); //检查字串合法性

            this._followMarker = null; //鼠标跟随Marker
            this._followLabel = null; //鼠标跟随文本提示
        };

    baidu.lang.inherits(MarkerTool, baidu.lang.Class , "MarkerTool");//继承基类baidu.lang.Class所有prototype属性挂接的方法

    /**
     * 开启工具
     * @return {Boolean} true表示开启成功，false表示开启失败
     */
    MarkerTool.prototype.open = function(){
        if(!this._map){
            return false;
        }
        if (this._isOpen == true){
            return true;
        }
        if (BMapLib._toolInUse){
            return false;
        }
        BMapLib._toolInUse = true; //当前鼠标状态正在使用中，如果存在多个鼠标工具，
        //可以使用此变量限制地图上只能存在一种鼠标操作状态
        this._isOpen = true;

        // 绑定mousemove 和 click 事件
        if (!this._binded){
            this._bind();
            this._binded = true;
        }
        //初始化跟随Marker
        if (!this._followMarker) {
            this._followMarker = new BMap.Marker(this._map.getCenter(), {offset: new BMap.Size(-10, -10)}); //偏移-10像素,解决cursor问题
            this._map.addOverlay(this._followMarker);
            this._followMarker.setZIndex(1000); // 设置跟随Marker的z轴高度
            this._followMarker.hide();
        }

        //初始化跟随Label
        if (!this._followLabel){
            this._followLabel = new BMap.Label(this._opts.followText, {offset: new BMap.Size(20, 0)});
        }

        this._preCursor = this._map.getDefaultCursor(); //记录当前的鼠标cursor
        this._map.setDefaultCursor("url(" + MarkerTool.CUR_IMG + "), default");//设置鼠标样式

        return true;
    };
    /**
     * 关闭工具
     * @return 无返回值
     */
    MarkerTool.prototype.close = function(){
        if (!this._isOpen){
            return;
        }

        //取消绑定事件
        this._map.removeEventListener("mousemove", this._mouseMoveHandler);
        this._map.removeEventListener("click", this._clickHandler);

        this._followMarker.hide();//隐藏跟随marker
        this._map.setDefaultCursor(this._preCursor);//设置鼠标样式
        BMapLib._toolInUse = false;
        this._isOpen = false;
        this._binded = false;
    };
    /**
     * 设置标注的图标及鼠标跟随样式
     * @param {Icon} icon 标注样式及鼠标跟随样式，为了方便用户设置Icon,系统提供了
     * 24种默认的图标，分别是：BMapLib.MarkerTool.SYS_ICON[0] -- BMapLib.MarkerTool.SYS_ICON[23]
     */
    MarkerTool.prototype.setIcon = function(icon){
        if (!icon || !(icon instanceof BMap.Icon)){
            return;
        }
        this._opts.icon = icon;
    };
    /**
     * 获取标注图标及鼠标跟随样式
     * @return {Icon} 当前标注及鼠标跟随样式
     */
    MarkerTool.prototype.getIcon = function(){
        return this._opts.icon;
    };
    /**
     * 检查字串的合法性，剔除xss漏洞输入字符
     * @return {String} 合法字符
     */
    MarkerTool.prototype._checkStr = function(str){
        if (!str){
            return "";
        }
        return str.replace(/</g, "&lt;").replace(/>/g, "&gt;");
    };
    /**
     * 绑定地图的mousemove 和 click 事件
     * @return 无返回值
     */
    MarkerTool.prototype._bind = function(){
        var me = this;
        if (!me._isOpen){//判断工具是否打开
            return;
        }

        //绑定mousemove事件
        me._mouseMoveHandler = function(evt){
            var pt = evt.point;
            me._followMarker.setIcon(me._opts.icon); //每次都检查最新的Icon
            me._followMarker.setPosition(pt);
            me._followMarker.setLabel(me._followLabel);
            me._followMarker.show();
        };
        me._map.addEventListener("mousemove", me._mouseMoveHandler);

        //绑定click事件
        me._clickHandler = function(evt){
            var evtPix = evt.pixel;
            var iconPix = new BMap.Pixel(evtPix.x - 10, evtPix.y - 10); //补偿_followMarker的-10像素问题,解决cursor问题
            var pt = me._map.pixelToPoint(iconPix);

            var mkr = new BMap.Marker(pt, {
                icon: me._opts.icon,
                enableDragging : true
            });
            me._map.addOverlay(mkr);

            /**
             * 添加标注过程中，每次点击地图添加完标注时，派发事件的接口
             * @name MarkerTool#onmarkend
             * @event
             * @param {Event Object} e 回调函数会返回event参数，包括以下返回值：
             * <br />"<b>type</b> : {String} 事件类型
             * <br />"<b>target</b>：{MarkerTool} 当前MarkerTool对象
             * <br />"<b>marker</b>：{Marker} 当前添加的Marker标注
             *
             * @example <b>参考示例：</b><br />
             * mkrTool.addEventListener("markend", function(e) {  alert(e.type);  });
             */
            var event = new baidu.lang.Event("onmarkend");
            event.marker = mkr;
            me.dispatchEvent(event);

            if(me._opts.autoClose){ //自动关闭工具
                me.close();
            }
        };
        me._map.addEventListener("click", me._clickHandler);
    };

    MarkerTool.CUR_IMG = "/x_component_Attendance/$AddressExplorer/default/icon/transparent.cur"; //鼠标透明样式，发布时候修改为绝对路径
    MarkerTool.ICON_IMG = "/x_component_Attendance/$AddressExplorer/default/icon/us_mk_icon.png"; //图标样式，发布时候修改为绝对路径
    MarkerTool.SYS_ICONS = [//MarkerTool 提供的系统样式，便于用户选择使用
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(6, 21), imageOffset: new BMap.Size(0, 0)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(6, 21), imageOffset: new BMap.Size(-23, 0)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(6, 21), imageOffset: new BMap.Size(-46, 0)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(6, 21), imageOffset: new BMap.Size(-69, 0)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(6, 21), imageOffset: new BMap.Size(-92, 0)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(6, 21), imageOffset: new BMap.Size(-115, 0)}),

        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(0, -21)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-23, -21)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-46, -21)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-69, -21)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-92, -21)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-115, -21)}),

        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(1, 21), imageOffset: new BMap.Size(0, -46)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(1, 21), imageOffset: new BMap.Size(-23, -46)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(1, 21), imageOffset: new BMap.Size(-46, -46)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(1, 21), imageOffset: new BMap.Size(-69, -46)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(1, 21), imageOffset: new BMap.Size(-92, -46)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(1, 21), imageOffset: new BMap.Size(-115, -46)}),

        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(25, 25), {anchor: new BMap.Size(12, 25), imageOffset: new BMap.Size(0, -67)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(25, 25), {anchor: new BMap.Size(12, 25), imageOffset: new BMap.Size(-25, -67)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(24, 25), {anchor: new BMap.Size(12, 25), imageOffset: new BMap.Size(-50, -67)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(25, 25), {anchor: new BMap.Size(12, 25), imageOffset: new BMap.Size(-75, -67)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(25, 25), {anchor: new BMap.Size(12, 25), imageOffset: new BMap.Size(-100, -67)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(19, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-125, -67)})
    ];

})();//闭包结束