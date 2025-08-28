MWF.xApplication.process.Xform.widget = MWF.xApplication.process.Xform.widget || {};
//MWF.xDesktop.requireApp("process.Xform", "widget.RestActions", null, false);
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.widget.MWFRaphael", null, false);
MWF.xApplication.process.Xform.widget.Monitor = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default"
    },
    initialize: function(container, worklog, recordList, processid, options, module){
        this.setOptions(options);

        this.path = "../x_component_process_Xform/widget/$Monitor/";
        this.cssPath = "../x_component_process_Xform/widget/$Monitor/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.container = $(container);
        this.worklog = worklog;
        this.recordList = recordList;
        this.processid = processid;

        this.mobileScale = 2;

        this.module = module;

        this.load();
    },
    load: function(){
        this.logProcessChartNode = new Element("div", {"styles": this.css.logProcessChartNode}).inject(this.container);
        this.logPathChartNode = new Element("div", {"styles": this.css.logPathChartNode}).inject(this.container);
        this.checkMonitorOpen();
    },

    checkMonitorOpen: function(){
        var moduleNode = this.container;
        var module = moduleNode.retrieve("module");
        var isDisplayNode = false;
        var isTabContent = false;
        while (true){
            if (moduleNode.getStyle("display")==="none"){
                isDisplayNode = true;
            }
            if (module && module.json.type==="Tab$Content"){
                isTabContent = true;
            }
            if (isDisplayNode && isTabContent) break;

            moduleNode = moduleNode.getParent();

            if (!moduleNode) break;

            if (!isTabContent) module = moduleNode.retrieve("module");
        }

        if (isDisplayNode){
            if (isTabContent){
                for (var i=0; i<module.tab.tab.pages.length; i++){
                    if (module.tab.tab.pages[i].contentNode === module.node) break;
                }
                module.tab.tab.pages[i].setOptions({
                    "onPostShow": function(){
                        this.openProcess();
                    }.bind(this)
                });
            }else{
                this.openProcessChartAction = new Element("div", {"styles": this.css.openProcessChartAction, "text": "Show Process"}).inject(this.logProcessChartNode);
                this.openProcessChartAction.addEvent("click", function(e){
                    this.openProcess(e);
                }.bind(this));
            }

        }else{
            this.openProcess();
        }
    },

    openProcess: function(){
        if (!this.process){
            this.logProcessChartNode.empty();
            this.loadToolbar();
            this.paperNode =  new Element("div.paperNode", {"styles": (layout.mobile) ? this.css.paperNodeMobile : this.css.paperNode}).inject(this.logProcessChartNode);
            //this.paperNode.addEvent("scroll", function(){
            //    this.setCountNodePosition();
            //}.bind(this));

            this.getProcess(function(json){
                this.processData = json.data;
                this.loadPaper();
                this.bindTabEvent();
            }.bind(this));
        }
    },
    setTouceEvent3: function (){
        // const svgContainer = document.getElementById('svgContainer');
        // const svg = document.getElementById('mainSvg');
        // const zoomInBtn = document.getElementById('zoomIn');
        // const zoomOutBtn = document.getElementById('zoomOut');
        // const resetBtn = document.getElementById('reset');
        // const viewBoxInfo = document.getElementById('viewBoxInfo');
        // const scaleInfo = document.getElementById('scaleInfo');

        const svgContainer = this.paperInNode;
        const svg = this.paperInNode.getElement('svg');

        // 初始viewBox值
        // const initialViewBox = {
        //     x: 0,
        //     y: 0,
        //     width: 15939,
        //     height: 3440
        // };

        const viewBox = svg.get('viewBox').split(' ');
        const initialViewBox = {
            x: viewBox[0].toFloat(),
            y: viewBox[1].toFloat(),
            width: viewBox[2].toFloat(),
            height: viewBox[3].toFloat()
        };

        console.log('initialViewBox', initialViewBox);


        let currentViewBox = {...initialViewBox};
        let currentScale = 1;

        //更新viewBox显示
        function updateViewBoxInfo() {
            var msg1 = `ViewBox: ${currentViewBox.x.toFixed(2)} ${currentViewBox.y.toFixed(2)} ${currentViewBox.width.toFixed(2)} ${currentViewBox.height.toFixed(2)}`;
            var msg2 = `缩放比例: ${currentScale.toFixed(2)}x`;
            console.log(msg1, msg2);
        }

        // 缩放函数
        function zoom(factor, centerX, centerY) {
            // 计算新的viewBox尺寸
            const newWidth = currentViewBox.width / factor;
            const newHeight = currentViewBox.height / factor;

            // 计算保持中心点不变的新viewBox位置
            const dx = (currentViewBox.width - newWidth) * (centerX / svgContainer.clientWidth);
            const dy = (currentViewBox.height - newHeight) * (centerY / svgContainer.clientHeight);

            // 更新viewBox
            currentViewBox.x += dx;
            currentViewBox.y += dy;
            currentViewBox.width = newWidth;
            currentViewBox.height = newHeight;

            console.log(`${currentViewBox.x} ${currentViewBox.y} ${currentViewBox.width} ${currentViewBox.height}`)
            //updateViewBoxInfo();

            // 应用新的viewBox
            svg.setAttribute('viewBox', `${currentViewBox.x} ${currentViewBox.y} ${currentViewBox.width} ${currentViewBox.height}`);

            // 更新缩放比例
            currentScale *= factor;

            // 更新显示信息
            updateViewBoxInfo();
        }

        // // 重置函数
        // function resetZoom() {
        //     currentViewBox = {...initialViewBox};
        //     svg.setAttribute('viewBox', `${currentViewBox.x} ${currentViewBox.y} ${currentViewBox.width} ${currentViewBox.height}`);
        //     currentScale = 1;
        //     updateViewBoxInfo();
        // }
        //
        // // 按钮事件监听
        // zoomInBtn.addEventListener('click', () => {
        //     const centerX = svgContainer.clientWidth / 2;
        //     const centerY = svgContainer.clientHeight / 2;
        //     zoom(1.2, centerX, centerY);
        // });
        //
        // zoomOutBtn.addEventListener('click', () => {
        //     const centerX = svgContainer.clientWidth / 2;
        //     const centerY = svgContainer.clientHeight / 2;
        //     zoom(0.8, centerX, centerY);
        // });
        //
        // resetBtn.addEventListener('click', resetZoom);

        // 鼠标滚轮事件
        svgContainer.addEventListener('wheel', function(e) {
            e.preventDefault();

            // 确定缩放方向和中心点
            const delta = e.deltaY < 0 ? 1.2 : 0.8;
            const rect = svgContainer.getBoundingClientRect();
            const centerX = e.clientX - rect.left;
            const centerY = e.clientY - rect.top;

            zoom(delta, centerX, centerY);
        });

        // 触摸事件处理
        let initialDistance = null;
        let initialCenter = null;

        svgContainer.addEventListener('touchstart', function(e) {
            if (e.touches.length === 2) {
                e.preventDefault();

                // 计算初始两指距离和中心点
                const touch1 = e.touches[0];
                const touch2 = e.touches[1];

                initialDistance = Math.sqrt(
                    Math.pow(touch2.clientX - touch1.clientX, 2) +
                    Math.pow(touch2.clientY - touch1.clientY, 2)
                );

                const rect = svgContainer.getBoundingClientRect();
                initialCenter = {
                    x: ((touch1.clientX + touch2.clientX) / 2) - rect.left,
                    y: ((touch1.clientY + touch2.clientY) / 2) - rect.top
                };
            }
        });

        svgContainer.addEventListener('touchmove', function(e) {
            if (e.touches.length === 2) {
                e.preventDefault();

                const touch1 = e.touches[0];
                const touch2 = e.touches[1];

                // 计算当前两指距离
                const currentDistance = Math.sqrt(
                    Math.pow(touch2.clientX - touch1.clientX, 2) +
                    Math.pow(touch2.clientY - touch1.clientY, 2)
                );

                if (initialDistance && initialCenter) {
                    // 计算缩放因子
                    const factor = currentDistance / initialDistance;

                    // 应用缩放
                    zoom(factor, initialCenter.x, initialCenter.y);

                    // 重置初始值以便下次计算
                    initialDistance = currentDistance;
                }
            }
        });

        svgContainer.addEventListener('touchend', function() {
            initialDistance = null;
            initialCenter = null;
        });
    },
    setTouceEvent2: function (){
        const container = this.paperInNode;
        const zoomableSvg = this.paperInNode.getElement('svg');

        let initialDistance = 0;  // 初始触摸距离
        let initialScale = this.mobileScale;     // 初始缩放比例
        let currentScale = 1;     // 当前缩放比例
        let initialCenter = { x: 0, y: 0 }; // 初始缩放中心点
        const scaleFactor = 0.003;  // 控制缩放变化的平滑因子，数值越小变化越慢
        const minScale = 0.95;     // 最小缩放比例
        const maxScale = 1.1;       // 最大缩放比例

        container.addEventListener('touchstart', (e) => {
            if (e.touches.length == 2) {
                // 计算两个触摸点的初始距离
                const touch1 = e.touches[0];
                const touch2 = e.touches[1];
                initialDistance = getDistance(touch1, touch2);

                // 计算缩放中心点
                initialCenter.x = (touch1.clientX + touch2.clientX) / 2;
                initialCenter.y = (touch1.clientY + touch2.clientY) / 2;
            }
        });

        container.addEventListener('touchmove', (e) => {
            if (e.touches.length == 2) {
                const touch1 = e.touches[0];
                const touch2 = e.touches[1];
                const newDistance = getDistance(touch1, touch2);

                // 计算缩放比例的变化
                const scaleChange = (newDistance / initialDistance);
                let newScale = initialScale * scaleChange;

                // 限制缩放比例的最大和最小值
                newScale = Math.max(minScale, Math.min(maxScale, newScale));

                // 平滑缩放
                const smoothScale = currentScale + (newScale - currentScale) * scaleFactor;

                // 更新当前缩放比例
                currentScale = smoothScale;

                // 更新SVG的viewBox属性
                updateViewBox(currentScale);
            }
        });

        container.addEventListener('touchend', () => {
            // 触摸结束时更新初始缩放比例
            initialScale = currentScale;
        });

        // 计算两点之间的距离
        function getDistance(touch1, touch2) {
            const dx = touch2.clientX - touch1.clientX;
            const dy = touch2.clientY - touch1.clientY;
            return Math.sqrt(dx * dx + dy * dy);
        }

        // 更新viewBox，确保保持中心点不变
        function updateViewBox(scale) {
            const originalViewBox = zoomableSvg.getAttribute('viewBox').split(' ');
            const originalWidth = parseFloat(originalViewBox[2]);
            const originalHeight = parseFloat(originalViewBox[3]);

            // 计算新的视口大小
            const newWidth = originalWidth / scale;
            const newHeight = originalHeight / scale;

            // 计算新的viewBox坐标（保持中心不变）
            const newX = (originalWidth - newWidth) / 2;
            const newY = (originalHeight - newHeight) / 2;

            zoomableSvg.setAttribute('viewBox', `${newX} ${newY} ${newWidth} ${newHeight}`);
        }
    },
    setTouceEvent: function(){
        debugger;

        var svg = this.paperInNode.getElement('svg');
        svg.setAttribute('viewBox', ``);
        svg.setAttribute('preserveAspectRatio', ``);

        var zoomable = this.paperInNode;
        let initialDistance = 0;  // 初始距离
        let initialScale = 1 / this.mobileScale;     // 初始缩放比例
        let initialCenter = { x: 0, y: 0 }; // 缩放中心点

        zoomable.addEventListener('touchstart', (e) => {

            if (e.touches.length == 2) {
                // 获取两个触摸点的初始距离
                const touch1 = e.touches[0];
                const touch2 = e.touches[1];
                initialDistance = getDistance(touch1, touch2);

                // 计算中心点位置
                initialCenter.x = (touch1.clientX + touch2.clientX) / 2;
                initialCenter.y = (touch1.clientY + touch2.clientY) / 2;
            }
        });

        zoomable.addEventListener('touchmove', (e) => {
            if (e.touches.length == 2) {
                const touch1 = e.touches[0];
                const touch2 = e.touches[1];
                const newDistance = getDistance(touch1, touch2);

                // 计算缩放比例
                const scale = (newDistance / initialDistance) * initialScale;

                // 获取当前缩放比例
                const deltaX = (touch1.clientX + touch2.clientX) / 2 - initialCenter.x;
                const deltaY = (touch1.clientY + touch2.clientY) / 2 - initialCenter.y;

                // 应用缩放和移动
                zoomable.style.transform = `scale(${scale}) translate(${deltaX}px, ${deltaY}px)`;
                console.log('zoomable.style.transform', `scale(${scale}) translate(${deltaX}px, ${deltaY}px)`);
            }
        });

        zoomable.addEventListener('touchend', (e) => {
            if (e.touches.length < 2) {
                // 触摸结束时，更新初始值
                initialScale = getScale(zoomable);
            }
        });

        // 计算两点之间的距离
        function getDistance(touch1, touch2) {
            const dx = touch2.clientX - touch1.clientX;
            const dy = touch2.clientY - touch1.clientY;
            return Math.sqrt(dx * dx + dy * dy);
        }

        // 获取当前的缩放比例
        function getScale(element) {
            const style = window.getComputedStyle(element);
            const transform = style.transform;
            if (transform !== 'none') {
                const matrix = new DOMMatrix(transform);
                return matrix.a;  // 获取缩放比例
            }
            return 1 / this.mobileScale;  // 默认缩放比例为1
        }
    },

    loadToolbar: function(){
        MWF.require("MWF.widget.Toolbar", function(){
            this.toolbarNode = new Element("div").inject(this.logProcessChartNode);
            this.createToolbarNode("play.png", "logPlay", "");
            //this.createToolbarNode("pause.png", "logPause", "true");
            //this.createToolbarNode("stop.png", "playStop", "true");
            //this.createToolbarNode("prev.png", "logPrev", "true");
            //this.createToolbarNode("next.png", "logNext", "");
            this.toolbar = new MWF.widget.Toolbar(this.toolbarNode, {"style": this.options.style}, this);
            this.toolbar.load();
        }.bind(this));
    },
    createToolbarNode: function(img, action, disabled){
        new Element("div", {
            "MWFnodetype": "MWFToolBarButton",
            "MWFButtonImage": this.path+""+this.options.style+"/tools/"+img,
            "title": "",
            "MWFButtonAction": action,
            "MWFButtonText": "",
            "MWFButtonDisable": disabled
        }).inject(this.toolbarNode);
    },

    logPlay: function(){

        if (this.process){
            this.isPlaying = true;
            this.toolbar.childrenButton[0].setDisable(true);
            //this.toolbar.childrenButton[1].setDisable(false);
            //this.toolbar.childrenButton[2].setDisable(false);

            this.processReturnStyle();

            this.playBegin();
            this.playToNextActivity();
        }
    },
    playBegin: function(){
        var src = "../x_component_process_Xform/widget/$Monitor/"+this.options.style+"/fly.png";
        this.playIcon = this.paper.image(src, this.process.begin.center.x-16, this.process.begin.center.y-16, 32, 32);
        this.playLogNode = null;
        this.playsStatus = {
            "index": 0
        };
        this.isPlaying = true;
    },
    playGetNextActivity: function(){
        var log = this.worklog[this.playsStatus.index];
        var activityType = log.fromActivityType;
        var activity = (activityType.toLowerCase()=="begin") ? this.process.begin : this.process[activityType+"s"][log.fromActivity];
        return {"log": log, "activity": activity};
    },
    playToNextActivity: function(route){
        var activity = this.playGetNextActivity();
        if (this.playsStatus.index == 0){
            this.playToActivityStop(activity.activity, activity.log);
        }else{
            this.playMoveToActivity(activity.activity, activity.log, route);
        }
    },
    playMoveToActivity: function(activity, log, route){
        if (route){
            var points = [route.beginPoint];
            points = points.concat(route.positionPoints, [route.endPoint], [{"x": activity.center.x, "y": activity.center.y}]);
            this.playMoveByRoutePoint(points, function(){
                this.playToActivityStop(activity, log, route);
            }.bind(this));
        }else{
            this.playToActivityStop(activity, log, route);
        }
    },
    playMoveByRoutePoint: function(points, callback){

        var p = {"x": this.playIcon.attr("x").toFloat(), "y": this.playIcon.attr("y").toFloat()};
        var toP = points.shift();

        var d = MWFRaphael.getPointDistance(p, toP);
        var ms = d/0.2;

        this.playIcon.animate({"x": toP.x-16, "y": toP.y-16}, ms, "linear", function() {
            if (points.length) {
                this.playMoveByRoutePoint(points, callback);
            } else {
                if (callback) callback();
            }
        }.bind(this));
    },

    playToActivityStop: function(activity, log, prevRoute){
        this.playIcon.attr({
            "x": activity.center.x-16,
            "y": activity.center.y-16
        });
        if (log.connected){
            activity.shap.attr(this.css.passedActivityShap);
        }else{
            activity.shap.attr(this.css.currentActivityShap);
        }
        var route = this.process.routes[log.route];
        if (prevRoute){
            prevRoute.line.attr(this.css.passedRouteShap);
            prevRoute.point.attr(this.css.passedRouteFillShap);
            prevRoute.arrow.attr(this.css.passedRouteFillShap);
        }

        if( !layout.mobile ){
            this.ensureRectIsVisible(activity);
        }

        this.showPlayLog(activity,log);
        this.playsStatus.index++;

        window.setTimeout(function(){
            if (this.worklog.length<=this.playsStatus.index){
                this.playStop();
            }else{
                this.playLogNode.destroy();
                this.playLogNode = null;
                this.playToNextActivity(route);
            }
        }.bind(this), 2000);
    },
    showPlayLog: function(activity,log){
        // var offset = this.paperNode.getPosition(this.paperNode.getOffsetParent());
        // var size = this.paperNode.getSize();
        this.playLogNode = this.createWorkLogNode([log], activity);
        this.playLogNode.setStyle("display", this.playLogNode.get("html") ? "block" : "none");
        // var p = this.getlogNodePosition(activity, this.playLogNode, offset, size);
        // this.playLogNode.setPosition({"x": p.x, "y": p.y});

        this.setWorkLogPosition(activity, this.playLogNode);
    },

    ensureRectIsVisible: function(activity) {
        // 获取矩形的位置和尺寸
        var rectTop = activity.point.y;
        var rectLeft = activity.point.x;
        var rectHeight = activity.height;
        var rectWidth = activity.width;
        var rectBottom = rectTop + rectHeight;
        var rectRight = rectLeft + rectWidth;

        var scrollParent = this.paperNode;

        // 获取父滚动区域的尺寸和滚动位置
        var scrollSize = scrollParent.getSize();
        var scrollTop = scrollParent.scrollTop;
        var scrollLeft = scrollParent.scrollLeft;
        var scrollBottom = scrollSize.y + scrollTop;
        var scrollRight = scrollSize.x + scrollLeft;

        // 检查是否需要垂直滚动
        if (rectTop < scrollTop) {
            // 矩形顶部在视口上方，需要向上滚动
            scrollParent.scrollTop = rectTop;
        } else if (rectBottom > scrollBottom) {
            // 矩形底部在视口下方，需要向下滚动
            scrollParent.scrollTop = rectBottom - scrollSize.y;
        }

        // 检查是否需要水平滚动
        if (rectLeft < scrollLeft) {
            // 矩形左侧在视口左方，需要向左滚动
            scrollParent.scrollLeft = rectLeft;
        } else if (rectRight > scrollRight) {
            // 矩形右侧在视口右方，需要向右滚动
            scrollParent.scrollLeft = rectRight - scrollSize.x;
        }
    },


    playStop: function(){
        this.playIcon.remove();
        if (this.playLogNode) this.playLogNode.destroy();
        this.playLogNode = null;
        this.playsStatus = {
            "index": 0
        };
        this.isPlaying = false;

        this.toolbar.childrenButton[0].setDisable(false);
        //this.toolbar.childrenButton[1].setDisable(true);
        //this.toolbar.childrenButton[2].setDisable(true);

        this.clearCount();
        this.loadWorkLog();
    },



    processReturnStyle: function(){
        this.worklog.each(function(log){
            var activityType = log.fromActivityType;
            var activity = (activityType.toLowerCase()=="begin") ? this.process.begin : this.process[activityType+"s"][log.fromActivity];
            activity.shap.attr(activity.style.shap);
            activity.passedCount = 0;
            activity.worklogs = [];

            var route = this.process.routes[log.route];
            if (route){
                route.line.attr(this.process.css.route.line.normal);
                route.point.attr(this.process.css.route.decision.normal);
                route.arrow.attr(this.process.css.route.arrow.normal);
            }

            if (log.taskCompletedList && log.taskCompletedList.length){
                log.taskCompletedList.each(function(tc){
                    if (tc.processingType === "appendTask"){
                        if (activity.routes && activity.routes.length){
                            activity.routes.each(function(r){
                                if (tc.routeName === r.data.name){
                                    r.line.attr(this.process.css.route.line.normal);
                                    r.point.attr(this.process.css.route.decision.normal);
                                    r.arrow.attr(this.process.css.route.arrow.normal);
                                }
                            }.bind(this));
                        }
                    }
                }.bind(this));
            }

            if (activity.countSet) activity.countSet.remove();
        }.bind(this));

    },



    loadPaper: function(){
        MWFRaphael.load(function(){
            this.paperInNode =  new Element("div", {"styles": this.css.paperInNode}).inject(this.paperNode);
            this.paper = Raphael(this.paperInNode, "98%", "99%");

            this.paper.container = this.paperNode;

            MWF.xDesktop.requireApp("process.ProcessDesigner", "Process", function(){
                this.process = new MWF.APPPD.Process(this.paper, this.processData, this, {"style":"flat", "isView": true,
                    "onPostLoad": function(){
                        if (layout.mobile){
                            var s = this.paper.canvas.getSize();
                            var x = s.x*this.mobileScale;
                            var y = s.y*this.mobileScale;
                            this.paper.canvas.set({
                                "viewBox": "0 0 "+x+" "+y+"",
                                "preserveAspectRatio": "xMinYMin meet"
                            });
                        }

                        this.loadWorkLog();


                        this.setTouceEvent3();

                        this.fireEvent("postLoad");
                    }.bind(this)
                });
                this.process.load();
            }.bind(this));
        }.bind(this));
    },
    getProcess: function(callback){
        this.action = MWF.Actions.get("x_processplatform_assemble_surface");
        //this.action = new MWF.xApplication.process.Xform.widget.RestActions("x_processplatform_assemble_surface");
        //this.action = new MWF.xApplication.process.Xform.widget.RestActions("x_processplatform_assemble_designer");

        this.action.getProcess(function(json){
            if (callback) callback(json);
        }, null, this.processid)
    },

    clearCount: function (){
        var activitys = {};
        this.worklog.each(function(log){
            var activityType = log.fromActivityType;
            var activity = (activityType.toLowerCase()=="begin") ? this.process.begin : this.process[activityType+"s"][log.fromActivity];
            activity.passedCount = 0;
            activity.worklogs = [];
            if (!activitys[log.fromActivity]) activitys[log.fromActivity] = activity
        }.bind(this));
        if (this.recordList){
            this.recordList.each(function (r, i){
                var activity = activitys[r.fromActivity];
                if(activity)activity.recordCount = 0;
            }.bind(this));
        }
    },
    loadWorkLog: function(){
        this.countNodes = [];
        var activitys = {};
        this.worklogToken = {};

        this.worklog.each(function(log){
            this.worklogToken[log.fromActivityToken] = log;
            var activityType = log.fromActivityType;
            var activity = (activityType.toLowerCase()=="begin") ? this.process.begin : this.process[activityType+"s"][log.fromActivity];
            if (log.connected){
                activity.shap.attr(this.css.passedActivityShap);
            }else{
                activity.shap.attr(this.css.currentActivityShap);
            }
            var route = this.process.routes[log.route];
            if (route){
                route.line.attr(this.css.passedRouteShap);
                route.point.attr(this.css.passedRouteFillShap);
                route.arrow.attr(this.css.passedRouteFillShap);
            }


            if (log.taskCompletedList && log.taskCompletedList.length){
                log.taskCompletedList.each(function(tc){
                    if (tc.processingType === "appendTask"){
                        if (activity.routes && activity.routes.length){
                            activity.routes.each(function(r){
                                if (tc.routeName === r.data.name){
                                    r.line.attr(this.css.passedRouteShap);
                                    r.point.attr(this.css.passedRouteFillShap);
                                    r.arrow.attr(this.css.passedRouteFillShap);
                                }
                            }.bind(this));
                        }
                    }
                }.bind(this));
            }


            var passedCount = log.taskCompletedList.length;
            //var passedCount = log.taskCompletedList.length || 1;
            if (passedCount) activity.passedCount = (activity.passedCount) ? activity.passedCount+passedCount : passedCount;
            if (!activity.worklogs) activity.worklogs = [];
            activity.worklogs.push(log);
            if (!activitys[log.fromActivity]) activitys[log.fromActivity] = activity
        }.bind(this));
        if (this.recordList){
            this.recordList.each(function (r, i){
                var activity = activitys[r.fromActivity];
                if (!activity.recordCount) activity.recordCount = 0
                activity.recordCount++;
            }.bind(this));
        }

        var offset = this.paperNode.getPosition(this.paperNode.getOffsetParent());
        var size = this.paperNode.getSize();
        Object.each(activitys, function(activity){
            this.writePassCount(activity);
            this.writeWorkLog(activity, offset, size);
        }.bind(this));
    },
    writePassCount: function(activity){
        if (activity.passedCount || activity.recordCount){
            var x = activity.point.x+activity.width;
            var y = activity.point.y;
            var shap = this.paper.circle(x, y, 9);
            shap.attr(this.css.activityPassedCount);

            text = this.paper.text(x, y, activity.recordCount || activity.passedCount);
            text.attr(this.css.activityPassedCountText);

            activity.countSet = this.paper.set();
            activity.countSet.push(shap, text);
        }
    },
    writeWorkLog: function(activity, offset, size){
        var _self = this;
        activity.set.click(function(e){
            if (!_self.isPlaying){
                if (this.process.selectedActivitys.length){
                    if (!this.noselected){
                        this.selected();
                        _self.showWorklog(this, offset, size);
                    }
                    this.noselected = false;
                }
                if (this.countSet) this.countSet.toFront();
            }
            e.stopPropagation();
        }.bind(activity));
        activity.set.mousedown(function(e){
            if (!_self.isPlaying) {
                if (!this.process.selectedActivitys.length) {
                    this.selected();
                    _self.showWorklog(this, offset, size);
                }
                if (this.countSet) this.countSet.toFront();
            }
            e.stopPropagation();
        }.bind(activity));

        this.paper.canvas.addEvent("click", function(e){
            if (!_self.isPlaying) {
                if (this.unSelectedEvent) {
                    if (this.currentSelected || this.selectedActivitys.length) {
                        this.unSelected(e);

                        _self.hideCurrentWorklog();
                    }
                } else {
                    this.unSelectedEvent = true;
                }
            }
        }.bind(this.process));
    },
    getlogNodePosition : function(activity, node, offset, psize){
        var targetCoondinates = {
            left: activity.point.x,
            top: activity.point.y,
            width: activity.width,
            height: activity.height,
            right: activity.point.x + activity.width,
            bottom: activity.point.y + activity.height
        };

        var containerScroll = this.paperNode.getScroll();
        var containerSize = this.paperNode.getSize();
        var nodeSize = node.getSize();
        var left;

        var offsetX = 10;

        var priorityOfX = [ "center", "right", "left" ];
        for( var i=0; i<priorityOfX.length; i++ ){
            if( priorityOfX[i] === "left" ){
                if( targetCoondinates.left - containerScroll.x > containerSize.x - targetCoondinates.right){
                    left = targetCoondinates.left - nodeSize.x - offsetX;
                    positionX = "left";
                    break;
                }
            }
            if( priorityOfX[i] === "right" ){
                if( containerSize.x  + containerScroll.x - targetCoondinates.right > nodeSize.x ){
                    left = targetCoondinates.right + offsetX;
                    positionX = "right";
                    break;
                }
            }
        }
        if( !left ){
            if( targetCoondinates.left - containerScroll.x > containerSize.x - targetCoondinates.right){
                left = targetCoondinates.left - nodeSize.x - offsetX;
                positionX = "left";
            }else{
                left = targetCoondinates.right + offsetX;
                positionX = "right";
            }
        }

        var top;
        var priorityOfY = [ "middle", "bottom", "top" ];
        for( var i=0; i<priorityOfY.length; i++ ){
            if( priorityOfY[i] === "middle" ){
                if( targetCoondinates.top + (targetCoondinates.height/2) - ( nodeSize.y / 2 ) > containerScroll.y &&
                    targetCoondinates.bottom - (targetCoondinates.height/2) + ( nodeSize.y / 2 ) - containerScroll.y < containerSize.y ){
                    top = targetCoondinates.top + (targetCoondinates.height/2) - ( nodeSize.y / 2 ) ;
                    this.positionY = "middle";
                    break;
                }
            }
            if( priorityOfY[i] === "top" ){
                if( targetCoondinates.top - containerScroll.y > containerSize.y - targetCoondinates.bottom ){
                    top = targetCoondinates.bottom - nodeSize.y;
                    this.positionY = "top";
                    break;
                }
            }
            if( priorityOfY[i] === "bottom" ){
                if( containerSize.y  + containerScroll.y - targetCoondinates.bottom > nodeSize.y ){
                    top = targetCoondinates.top;
                    this.positionY = "bottom";
                    break;
                }
            }
        }
        if( !top ){
            if( targetCoondinates.top + (targetCoondinates.height/2) - ( nodeSize.y / 2 ) > containerScroll.y &&
                targetCoondinates.bottom - (targetCoondinates.height/2) + ( nodeSize.y / 2 ) - containerScroll.y < containerSize.y ){
                top = targetCoondinates.top + (targetCoondinates.height/2) - ( nodeSize.y / 2 ) ;
                this.positionY = "middle";
            } else if( targetCoondinates.top - containerScroll.y > containerSize.y - targetCoondinates.bottom ){
                top = targetCoondinates.bottom - nodeSize.y;
                this.positionY = "top";
            }else{
                top = targetCoondinates.top;
                this.positionY = "bottom";
            }
        }

        // var arrowOffsetY = 0;
        // if( this.options.isFitToContainer ){
        if( top < containerScroll.y ){
            // arrowOffsetY = containerScroll.y - top;
            top = containerScroll.y;
        }else if( top + nodeSize.y > containerSize.y  + containerScroll.y ){
            // arrowOffsetY = containerSize.y  + containerScroll.y - top - nodeSize.y;
            top = containerSize.y  + containerScroll.y - nodeSize.y;
        }

        return {
            "x" : left,
            "y" : top
        };
    },
    getlogNodePosition_bak: function(activity, node, offset, psize){
        offset.x = 0;
        offset.y = 0;
        var size = node.getSize();
        var y = 0;
        var x = activity.point.x+activity.width+15+offset.x;
        tmpX = x + size.x;
        if (tmpX>offset.x+psize.x){
            x = activity.point.x - size.x - 15 + offset.x;
            if (x<offset.x){
                y = activity.point.y-size.y-15+offset.y;
                x = activity.center.x - (size.x/2) + offset.x;
            }else{
                y = activity.center.y - (size.y/2) + offset.y;
                if (y<offset.y){
                    y = offset.y
                }
            }
        }else{
            y = activity.center.y - (size.y/2) + offset.y;
            if (y<offset.y){
                y = offset.y
            }
        }
        if( x + size.x > psize.x )x = psize.x - size.x;
        if( y + size.y > psize.y )y = psize.y - size.y;
        if( x < 0 )x = 0;
        if( y < 0 )y = 0;

        // var p = this.paperNode.getScroll();
        // var scrollY = 0;
        // var scrollX = 0;
        // var tmpNode = this.paperNode.getParent();
        // while (tmpNode){
        //     var s = tmpNode.getScroll();
        //     scrollY += s.y;
        //     scrollX += s.x;
        //     tmpNode = tmpNode.getParent();
        // }
        // y = y-p.y-scrollY;
        // x = x-p.x-scrollX;

        return {"x": x, "y": y};
    },
    showWorklog: function(activity, offset, psize){
        this.hideCurrentWorklog();

        if (!activity.worklogNode) activity.worklogNode = this.createWorkLogNode(activity.worklogs, activity);

        this.currentWorklogNode = activity.worklogNode;
        this.currentWorklogNode.setStyle("display", !!this.currentWorklogNode.get("html") ? "block" : "none");
        this.setWorkLogPosition(activity, activity.worklogNode, offset, psize);
    },
    setWorkLogPosition: function(activity, logNode, offset, psize){
        if( !logNode )logNode = activity.worklogNode;
        if( layout.mobile ){
            var pSize = this.paperNode.getSize();
            var bodySize =  $(document.body).getSize();
            if( this.paperNode.getPosition().y + pSize.y > bodySize.y ){
                var mobileActionNode = document.body.getElement(".o2_form_mobile_actions");
                logNode.inject( $(document.body) );
                logNode.setStyles({
                    "display": "block",
                    "position": "absolute",
                    "width": "calc( 100% - 4px )",
                    "max-width": "500px",
                    "bottom": mobileActionNode ? (mobileActionNode.getSize().y+1+"px") : "1px",
                    "left": "0px"
                });
                logNode.setStyle("left", (bodySize.x - logNode.getSize().x)/2 + "px");
            }else{
                logNode.inject( this.paperNode );
                logNode.setStyles({
                    "display": "block",
                    "position": "absolute",
                    "width": "calc( 100% - 4px )",
                    "max-width": "500px",
                    "bottom": "1px",
                    "left": "0px"
                });
                logNode.setStyle("left", (pSize.x - logNode.getSize().x)/2 + "px");
            }
        }else{
            var p = this.getlogNodePosition(activity, logNode, offset, psize);
            logNode.setPosition({"x": p.x, "y": p.y});
        }
        this.fireEvent('showWorklog', [logNode]);
    },
    hideCurrentWorklog: function(){
        if (this.currentWorklogNode){
            this.currentWorklogNode.setStyle("display", "none");
            this.currentWorklogNode = null;
        }
    },
    bindTabEvent: function(){
        if( this.module ){
            var tab = this.module.getParentModule();
            if( tab && tab.page ){
                tab.page.addEvent("postHide", function () {
                    if(this.currentWorklogNode)this.currentWorklogNode.hide()
                }.bind(this))
            }
        }
    },

    createWorkLogNode: function(worklogs, activity){
        var node = new Element("div", {"styles": this.css.workLogNode});

        if (this.recordList){
            var logs = this.recordList.filter(function(r){
                return r.fromActivity === activity.data.id;
            });
            logs.each(function(log, idx){
                var workNode = new Element("div", {"styles": this.css.workLogWorkNode}).inject(node);
                if ((idx % 2)==0){
                    workNode.setStyle("background-color", "#FFF");
                }else{
                    workNode.setStyle("background-color", "#EEE");
                }

                var router, opinion, arrivedActivitys, arrivedUsers;
                arrivedActivitys = log.properties.nextManualList.map(function(o){
                    return o.activityName;
                }).join(",");
                arrivedUsers = (log.properties.nextManualTaskIdentityList && log.properties.nextManualTaskIdentityList.length) ? o2.name.cns(log.properties.nextManualTaskIdentityList).join(",") : "";

                switch (log.type) {
                    case "empower":
                        router = MWF.xApplication.process.Xform.LP.empower;
                        var empowerTo = (log.properties.nextManualTaskIdentityList && log.properties.nextManualTaskIdentityList.length) ? o2.name.cns(log.properties.nextManualTaskIdentityList).join(",") : "";
                        opinion = MWF.xApplication.process.Xform.LP.empowerTo + empowerTo;
                        break;
                    case "retract":
                        router = MWF.xApplication.process.Xform.LP.retract;
                        opinion = MWF.xApplication.process.Xform.LP.retract;
                        break;
                    case "reroute":
                        router = log.properties.routeName || MWF.xApplication.process.Xform.LP.reroute;
                        opinion = log.properties.opinion || MWF.xApplication.process.Xform.LP.rerouteTo+": "+arrivedActivitys;
                        break;
                    case "rollback":
                        router = log.properties.routeName || MWF.xApplication.process.Xform.LP.rollback;
                        opinion = log.properties.opinion || MWF.xApplication.process.Xform.LP.rollbackTo+": "+log.arrivedActivityName;
                        break;
                    case "reset":
                        var resetUser = log.properties.nextManualTaskIdentityList.erase(log.identity);
                        resetUserText = o2.name.cns(resetUser).join(",");
                        router = MWF.xApplication.process.Xform.LP.resetTo+":"+resetUserText;
                        opinion = log.properties.opinion || ""
                        break;
                    case "appendTask":
                    case "back":
                    case "addSplit":
                    case "urge":
                    case "expire":
                    case "read":
                    default:
                        router = log.properties.routeName || "";
                        opinion = log.properties.opinion || "";
                }
                if( !log.person )log.person = '';
                if (log.type==="currentTask"){
                    var taskNode = new Element("div", {"styles": this.css.workLogTaskNode}).inject(workNode);
                    var html = "<div style='font-weight: bold; color: red'>"+log.person.substring(0, log.person.indexOf("@"))+" "+MWF.xApplication.process.Xform.LP.processing+" </div>";
                    taskNode.set("html", html);
                }else{
                    var taskNode = new Element("div", {"styles": this.css.workLogTaskNode}).inject(workNode);
                    var html = "<div style='font-weight: bold'>"+log.person.substring(0, log.person.indexOf("@"))+": </div>";
                    html += "<div style='margin-left: 10px'>["+router+"] "+o2.txt(opinion)+"</div>";
                    html += "<div style='text-align: right'>"+log.recordTime+"</div>";
                    taskNode.set("html", html);
                }
            }.bind(this));
        }else{
            worklogs.each(function(log, idx){
                var workNode = new Element("div", {"styles": this.css.workLogWorkNode}).inject(node);
                if ((idx % 2)==0){
                    workNode.setStyle("background-color", "#FFF");
                }else{
                    workNode.setStyle("background-color", "#EEE");
                }

                if (log.taskCompletedList.length+log.taskList.length<1){
                    if (log.connected){
                        var taskNode = new Element("div", {"styles": this.css.workLogTaskNode}).inject(workNode);
                        var html = "<div style='font-weight: bold'>"+MWF.xApplication.process.Xform.LP.systemProcess+" </div>";
                        html += "<div style='text-align: right'>"+log.arrivedTime+"</div>";
                        taskNode.set("html", html);
                    }else{
                        var taskNode = new Element("div", {"styles": this.css.workLogTaskNode}).inject(workNode);
                        var html = "<div style='font-weight: bold; color: red'>"+MWF.xApplication.process.Xform.LP.systemProcess+" </div>";
                        taskNode.set("html", html);
                    }
                }else{
                    log.taskCompletedList.each(function(task){
                        if( !task.person )task.person = '';
                        var taskNode = new Element("div", {"styles": this.css.workLogTaskNode}).inject(workNode);
                        var html = "<div style='font-weight: bold'>"+task.person.substring(0, task.person.indexOf("@"))+": </div>";
                        html += "<div style='margin-left: 10px'>["+(task.routeName || "")+"] "+o2.txt(task.opinion)+"</div>";
                        html += "<div style='text-align: right'>"+task.completedTime+"</div>";
                        taskNode.set("html", html);
                    }.bind(this));

                    log.taskList.each(function(task){
                        if( !task.person )task.person = '';
                        var taskNode = new Element("div", {"styles": this.css.workLogTaskNode}).inject(workNode);
                        var html = "<div style='font-weight: bold; color: red'>"+task.person.substring(0, task.person.indexOf("@"))+" "+MWF.xApplication.process.Xform.LP.processing+" </div>";
                        taskNode.set("html", html);
                    }.bind(this));
                }

            }.bind(this));
        }
        node.inject(this.paperNode);
        return node;
    }
});

MWF.xApplication.process.Xform.widget.Monitor.Animation = new Class({
    Implements: [Events],

    initialize: function(monitor, log){
    }
});
