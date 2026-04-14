o2.require("o2.widget.Paging", null, false);
o2.requireApp("StandingBook", "Common", null, false);
MWF.xApplication.StandingBook.IndexView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date": null
    },
    initialize: function(node, app, options, appList){
        this.setOptions(options);


        this.path = "../x_component_StandingBook/$IndexView/";

        this.app = app;
        this.node = $(node);
        this.container = new Element("div", {
            style: "height:100%"
        }).inject(this.node);

        // this.container.loadCss(this.path + this.options.style + "/style.css");

        this.appList = appList.map(function (item) {
            return {
                category: item.category,
                key: item.key,
                name: item.name
            }
        });
        this.facetOrderList = ["category","applicationName", "processPlatform_string_applicationName", "processName", "processPlatform_string_processName","appName", "cms_string_appName","categoryName", "cms_string_categoryName"];
        this.load();
    },
    recordStatus: function(){
        return {
            view: "index",
            app: this.appList
        }
    },
    load: function(){
        if( this.appList.length === 0 ){
            this.showEmpty()
        }else{
            this.getUserData( function () {
                this._load();
            }.bind(this))
        }
    },
    _load: function( json ){

        this.pageSize = 50;
        this.currentKey = "";

        this.docPageNum = 1;
        this.docTotal = 0;

        // this.selectedConditionList = [];
        // this.selectedFieldList = [];

        this.execludedFaceList = ["creatorUnitLevelName"];

        var url = this.path+this.options.style+"/view.html";
        this.container.loadHtml(url, {"bind": {
                "lp": this.app.lp,
                "appData": {
                    name: this.appList.map(function (app) {
                        return app.name
                    }).join("， ")
                }
            }, "module": this}, function(){
            this.search();

        }.bind(this));

        this.setSizeFun = this.setSize.bind(this);
        this.app.addEvent("resize", this.setSizeFun)
    },
    reload: function(){
        this.container.empty();
        this.load();
    },
    destroy : function(){
        // this.app.removeEvent("resize", this.resetNodeSizeFun );
        if( this.tooltipList ){
            while (this.tooltipList.length){
                this.tooltipList.shift().destroy();
            }
        }
        this.app.removeEvent("resize", this.setSizeFun);
        this.container.empty();
    },
    // searchByCondition: function(){
    //     this.dynamicConditionList = [];
    //     Object.each(this.dynamicConditionValueMap, function (data, key) {
    //         if( o2.typeOf(data) === "array"){
    //             this.dynamicConditionList.push({
    //                 field: key,
    //                 min: data[0],
    //                 max: data[1]
    //             });
    //         }else if(o2.typeOf(data) === "string"){
    //             this.dynamicConditionList.push({
    //                 field: key,
    //                 valueList: [data]
    //             });
    //         }
    //     }.bind(this));
    //
    //     this.selectedConditionList = this.selectedConditionListTemporary;
    //
    //     this._search(null, true);
    // },
    searchKeydown: function(e){
        if( e.keyCode === 13 ){
            this.search();
        }
    },
    reset: function(){
        if( this.searching )return;
        this.selectedFilterList = [];
        this._search();
    },
    search: function(pageNum, query){
        if( this.searching )return;
        this.selectedFilterList = [];
        this.sortValueMap = {};
        this.selectedFieldList = Array.clone(this.userData.selectedFieldList || []);
        this._search(pageNum, query);
    },
    _search: function( pageNum, byCondition ){
        if( this.searching )return;
        this.searching = true;
        pageNum = o2.typeOf(pageNum) === "number" ? pageNum : null;
        this.docPageNum = pageNum || 1;
        // this.currentKey = this.searchInput.get("value") || "";
        // if( this.currentKey ){

        var startDate = new Date();

        var filterList = this.selectedFilterList.map(function(f){
            return {
                field: f.field,
                valueList: f.valueList
            };
        });
        // filterList = filterList.concat(this.dynamicConditionList);

        var fixedFieldList = [], dynamicFieldList = [], sortList = [];
        this.selectedFieldList.each(function(d){
            if( d.fixed ){
                fixedFieldList.push(d.field);
            }else{
                dynamicFieldList.push(d.field);
            }
            if( this.sortValueMap[d.field] )sortList.push({
                field: d.field,
                order: this.sortValueMap[d.field]
            })
        }.bind(this));

        o2.Actions.load("x_custom_index_assemble_control").IndexAction.post({
            directoryList: this.appList,
            // query: this.searchInput.get("value") || "",
            page:  this.docPageNum,
            size: this.pageSize,
            // sortList: sortList,
            sort: sortList[0] || null,
            fixedFieldList: fixedFieldList,
            dynamicFieldList: dynamicFieldList,
            filterList: filterList
        }).then(function(json){
            this.data = json;
            this.docTotal =  json.data.count;


            if(!this.commonFieldsLoaded && (json.data.fixedFieldList.length || json.data.dynamicFieldList.length)){

                this.commonFieldsLoaded = true;

                this.commonFields = [];
                this.uncommonFields = [];
                if( !this.selectedFieldList )this.selectedFieldList = [];

                json.data.fixedFieldList.map(function(field){
                    field.fixed = true;
                    field.name = field.name || "";

                    field.fieldName = this.getFieldName(field);

                    if( this.isCommonField(field) ){
                        if( !this.userData.selectedFieldList || this.userData.selectedFieldList.length === 0 ){
                            this.selectedFieldList.push( field );
                        }
                        this.commonFields.push( field );
                    }else{
                        this.uncommonFields.push( field );
                    }

                }.bind(this));

                json.data.dynamicFieldList.each(function (field) {
                    if( !field.fixed ){
                        // if( field.fieldType === "date" || field.fieldType === "number" ){
                        //     if( field.min && field.min !== "null" && field.max && field.max !== "null" ){ //&& (field.min != field.max)
                        //         field.filteable = true;
                        //     }
                        // }else if( field.fieldType === "string" ){
                        //     field.filteable = true;
                        // }

                        field.fieldName = this.getFieldName(field);

                        var name = field.name || "";
                        field.name = this.endsWith(name, ".name") ? name.substring(0, name.length-".name".length) : name;

                        if( this.isCommonField(field) ){
                            this.commonFields.push( field );
                        }else{
                            this.uncommonFields.push( field );
                        }
                    }
                }.bind(this));
            }



            var docLoaded = false;
            var filterLoaded = false;
            var afterLoadFun = function () {
                if(docLoaded && filterLoaded){
                    this.setSize();
                    if(!pageNum){
                        var endDate = new Date();
                        var t = endDate.getTime()-startDate.getTime();
                        t = ((t/1000)*100).toInt()/100;
                        var text = this.app.lp.docTotalInfor.replace("{count}", this.docTotal||0).replace("{time}", t);
                        // this.docTotalNode.set("html", text);
                        this.loadDocPagination( text );
                    }
                    this.searching = false;
                }
            }.bind(this);

            if( json.data.count > 0 ){

                this.selectedFieldList.each(function (field, index) {
                    switch (this.sortValueMap[field.field]) {
                        case "asc":
                            field.icon = "o2icon-order_up mainColor_color";
                            break;
                        case "desc":
                            field.icon = "o2icon-order_down mainColor_color";
                            break;
                        default:
                            field.icon = "o2icon-order_none";
                            break;
                    }
                    if( index === this.selectedFieldList.length - 1 ){
                        field.o2_last = true;
                    }else{
                        field.o2_last = false;
                    }
                }.bind(this));

                var sequence = (this.docPageNum - 1) * this.pageSize;
                json.data.documentList.map(function (d, i) {
                    sequence++;
                    d.o2_sequence = sequence;
                    return d;
                });

                this.loadFilter(function () {
                    filterLoaded = true;
                    afterLoadFun();
                });

                this.loadDocList(json, function () {
                    docLoaded = true;
                    afterLoadFun();
                });

            }else{
                var html =
                    '<div class="listNoData">'+
                    '   <div class="listNoDataIcon"></div>'+
                    '   <div class="listNoDataText">'+this.app.lp.noData+'</div>'+
                    '</div>';
                this.docListNode.set("html", html);
                // this.setSize();
                this.loadFilter(function () {
                    filterLoaded = true;
                    docLoaded = true;
                    afterLoadFun();
                });
            }
        }.bind(this), function () {
            this.searching = false;
        }.bind(this));
    },
    setSize: function(){
        var contentY = this.app.content.getSize().y;

        var topY = this.topNode ? ( this.topNode.getSize().y + this.getOffsetY(this.topNode) ): 0;
        var bottomY = this.docPaginationNode ? ( this.docPaginationNode.getSize().y + this.getOffsetY(this.topNode) ) : 0;

        // this.docContent.setStyle("height", "calc( 100% - "+ (topY + bottomY + this.getOffsetY(this.docContent) ) +"px )");
        this.docContent.setStyle("height",  contentY - topY - bottomY );

    },
    getOffsetY : function(node){
        return (node.getStyle("margin-top").toInt() || 0 ) +
            (node.getStyle("margin-bottom").toInt() || 0 ) +
            (node.getStyle("padding-top").toInt() || 0 ) +
            (node.getStyle("padding-bottom").toInt() || 0 )+
            (node.getStyle("border-top-width").toInt() || 0 ) +
            (node.getStyle("border-bottom-width").toInt() || 0 );
    },
    getFieldName: function(field){
        if( field.field.split("_").length >= 3 ){
            var arr = field.field.split("_");
            arr.shift();
            arr.shift();
            return arr.join("_");
        }else{
            return field.field;
        }
    },
    endsWith: function(s, e) {
        return s.substring(s.length-e.length) === e;
    },
    isCommonField: function(d){
        var endWith = MWF.xApplication.StandingBook.options.NotCommonlyField.endWith;
        var equals = MWF.xApplication.StandingBook.options.NotCommonlyField.equals;

        if( equals.contains( d.fieldName ) ){
            return false;
        }
        for( var i=0; i<endWith.length; i++ ){
            if( this.endsWith( d.fieldName, endWith[i] )){
                return false;
            }
        }
        return true;
    },
    exportExcel: function(){

        var filterList = this.selectedFilterList.map(function(cond){
            return {
                field: cond.field,
                valueList: cond.valueList
            };
        });
        // filterList = filterList.concat(this.dynamicConditionList);

        var fixedFieldList = [], dynamicFieldList = [], sortList = [];
        this.selectedFieldList.each(function(d){
            if( d.fixed ){
                fixedFieldList.push(d.field);
            }else{
                dynamicFieldList.push(d.field);
            }
            if( this.sortValueMap[d.field] )sortList.push({
                field: d.field,
                order: this.sortValueMap[d.field]
            })
        }.bind(this));

        o2.Actions.load("x_custom_index_assemble_control").IndexAction.export({
            // key: this.appData.key,
            // type: this.appData.type,
            // category: this.appData.category,
            directoryList: this.appList,
            // sortList: sortList,
            sort: sortList[0] || null,
            fixedFieldList: fixedFieldList,
            dynamicFieldList: dynamicFieldList,
            filterList: filterList
        }).then(function(json){
            var indexAction = o2.Actions.load("x_custom_index_assemble_control").IndexAction;
            var address = indexAction.action.address;
            var uri = indexAction.action.actions.exportResult.uri.replace("{flag}", json.data.id);
            window.open(address+uri, "_blank");
        }.bind(this));
    },
    openItem: function(id, category, event, row){
        if( category === "processPlatform" ){
            this.openWork(id, event, row)
        }else{
            this.openDoc(id, event, row)
        }
    },
    gotoMainPage: function(){
        this.app.gotoMainPage();
    },
    // openWork: function(id, event, row){
    //     o2.api.page.openWork(id);
    // },
    openWork: function(id, event, row){
        var appId = "process.Work"+id;
        var op = {
            "jobId": id,
            "appId": appId
        };
        return layout.desktop.openApplication(this.event, "process.Work", op);
    },

    openDoc: function(id, event, row){
        o2.api.page.openDocument(id);
    },

    orderColumn: function(field, fieldType, e, row){
        if( this.searching )return;
        if( ["boolean", "strings"].contains(fieldType) )return;
        var order = this.sortValueMap[field] || "none";
        this.sortValueMap = {}; //只保留一个sort
        var o;
        switch (order) {
            case "none":
                o = "asc";
                break;
            case "asc":
                o = "desc";
                break;
            case "desc":
                o = "none";
                break;
        }
        if( o === "none" ){
            delete this.sortValueMap[field];
        }else{
            this.sortValueMap[field] = o;
        }
        this._search();
    },
    loadDocList: function(json, callback){
        this.docListNode.empty();

        this.docListNode.loadHtml(this.path+this.options.style+"/docList.html",
            {
                "bind": {"lp": this.app.lp, "data": json.data.documentList, "fieldList": this.selectedFieldList},
                "module": this,
                "reload": true
            },
            function(){
                if(callback)callback();
            }.bind(this)
        );
    },
    addColumn: function(ev){
        MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false); //加载资源
        var opt = {
            "title": "调整列",
            "count": 0,
            "values": this.selectedFieldList.map(function (d) {
                if( d._ )delete d._;
                return d;
            }),
            "expand": true,
            "exclude": [],
            "expandSubEnable": true,
            "hasLetter": false, //是否点击字母搜索
            "hasTop": false, //可选、已选的标题
            // "level1Indent" : 0, //第一层的缩进
            // "indent" : 36, //第二层及以上的缩进
            "selectAllEnable": true, //是否允许多选，如果分类可以被选中，blue_flat样式下失效
            "width": "700px", //选中框宽度
            "height": "550px", //选中框高度
            "category": true, //按分类选择
            "noSelectedContainer": false, //是否隐藏右侧已选区域
            "categorySelectable": false, //分类是否可以被选择，如果可以被选择那么执行的是item的事件
            "uniqueFlag": "field", //项目匹配（是否选中）关键字
            "defaultExpandLevel": 1, //默认展开项目，0表示折叠所有分类
            "onLoad" : function(selector) {
                this.searchInput.setStyle("width","calc( 100% - 10px )");
            },
            "selectableItems": [ //可选项树
                {
                    "name": "常用字段",
                    "id": "category1",
                    "subItemList": this.commonFields
                },
                {
                    "name" : "其他字段",
                    "id" : "category2",
                    "subItemList": this.uncommonFields
                }
            ],
            "onComplete": function (array) {
                if( this.searching )return;
                var fields = [];
                this.selectedFieldList = array.map(function (d) {
                    fields.push( d.data.field );
                    return d.data;
                });

                Object.keys( this.sortValueMap ).each(function (key) {
                    if( !fields.contains( key ) )delete this.sortValueMap[key];
                }.bind(this))

                this._search( this.docPageNum );

                this.saveUserData();
            }.bind(this)
        };
        var selector = new MWF.xApplication.Template.Selector.Custom(this.app.content, opt);
        selector.load();
        ev.stopPropagation();
    },
    loadDocPagination: function( text ){
        this.docPaginationNode.empty();
        if( this.docTotal > 0 ){
            this.docPaging = new o2.widget.Paging(this.docPaginationNode, {
                style: "blue_round",
                countPerPage: this.pageSize,
                visiblePages: 9,
                currentPage: 1,
                itemSize: this.docTotal,
                useMainColor: true,
                text: {
                    firstPage: this.app.lp.firstPage,
                    lastPage: this.app.lp.lastPage
                },
                // pageSize: pageSize,
                onJumpingPage: function (pageNum) {
                    if( this.searching )return;
                    this._search(pageNum);
                }.bind(this),
                hasInfor: true,
                inforTextStyle: text,
                onPostLoad: function () {
                    this.wraper.setStyle("border-top", "1px solid #4A90E2");
                    this.wraper.addClass("mainColor_border");
                }
            });
            this.docPaging.load();
        }
    },
    orderFacet: function(facetList){
        facetList.sort(function (a, b) {
            var indexA =  this.facetOrderList.indexOf(a.field);
            var indexB =  this.facetOrderList.indexOf(b.field);
            if( indexA === -1 )indexA = 999999;
            if( indexB === -1 )indexB = 999999;
            return indexA - indexB;
        }.bind(this));
        return facetList
    },
    loadFilter: function( callback ){
        this.filterArea.empty();
        if( this.tooltipList && this.tooltipList.length ){
            this.tooltipList.each(function (tooltip) {
                tooltip.destroy();
            });
            this.tooltipList = [];
            this.currentTooltip = null;
        }
        var lp = this.app.lp.defaultText;
        var json = this.data;
        var index = 0;
        var facetList = [];
        this.orderFacet(json.data.facetList).each(function(d) {
        // ( json.data.facetList || [] ).each(function(d) {
            if (d.valueCountPairList && d.valueCountPairList.length){
                d.index = index;
                index++;
                d.label = lp[d.field] || d.name || d.field;
                // d.valueCountPairList.each(function (v) {
                //     v.field = d.field;
                //     v.parentLabel = d.label;
                //     if (["category","completed"].contains(d.field)) {
                //         v.label = lp[v.value.toString()] || v.value;
                //     } else {
                //         v.label = v.value;
                //     }
                // });
                facetList.push(d);
            }
        });
        this.filterArea.empty();
        if( this.selectedFilterList.length || facetList.length ){
            this.filterArea.loadHtml(this.path+this.options.style+"/filter.html",
                {
                    "bind": {
                        "lp": this.app.lp,
                        "selectedFilterList": this.selectedFilterList,
                        "filterList": facetList
                    },
                    "module": this,
                    "reload": true
                },
                function(){
                    if(callback)callback();
                }.bind(this)
            );
        }
    },
    checkedFilterItemEnter: function(e){
        e.target.addClass("mainColor_border").addClass("filterItem-content-selected-over");
        e.target.getElement("i").addClass("mainColor_color");
    },
    checkedFilterItemLeave: function(e){
        e.target.removeClass("mainColor_border").removeClass("filterItem-content-selected-over");
        e.target.getElement("i").removeClass("mainColor_color");
    },
    removeFilterItem: function(e, item){
        if( this.searching )return;
        var index = -1;
        this.selectedFilterList.each(function (f, idx) {
            if( f.field === item.field )index = idx;
        });
        if( index > -1 ) { //已经选择了
            this.selectedFilterList.splice(index, 1);
        }
        this._search(null, true);
    },
    loadFilterTooltip: function(e, row){
        var target = this.app.getEventTarget(e, "filterItem-content");
        var arrowNode = target.getElement(".arrow");
        arrowNode.addEvents({
            "click": function (ev) {
                tooltip.status === "display" ? tooltip.hide() : tooltip.load();
                ev.stopPropagation();
             },
            "mouseover": function (ev) { ev.stopPropagation(); },
            "mouseout": function (ev) { ev.stopPropagation(); }
        });
        if( !this.tooltipList )this.tooltipList = [];
        var tooltip = new MWF.xApplication.StandingBook.ConditionTooltip(this.contentNode, target, this.app, row, {
            axis : "y",
            hasArrow: false,
            hiddenDelay : 300,
            displayDelay : 100,
            offset : {
                x : 0,
                y : -1
            },
            position : { //node 固定的位置
                x : "center", //x 轴上left center right, auto 系统自动计算
                y : "bottom" //y轴上top middle bottom,  auto 系统自动计算
            },
            overflow : "scroll",
            isFitToContainer: true,
            nodeStyles: {
                "box-shadow": "#aaaaaa 0px 4px 18px 0px",
                "border": "1px solid #ccc"
            },
            onQueryLoad: function(){
                var width = Math.min(this.contentNode.getSize().x - 60, 1400);
                if( tooltip.node ){
                    tooltip.node.setStyle("width",width+"px");
                }else{
                    tooltip.options.nodeStyles["width"] = width + "px"; //target.getSize().x + "px";
                    tooltip.options.nodeStyles["max-width"] = "auto";
                }
                if( this.currentTooltip ){
                    this.currentTooltip.hide();
                }
            }.bind(this),
            onPostLoad: function(){
                tooltip.node.addClass("mainColor_border");
                arrowNode.addClass("o2icon-chevron-thin-up").removeClass("o2icon-chevron-thin-down");
                target.addClass("filterItem-content-over").addClass("mainColor_border");
                this.currentTooltip = tooltip;
                // if( tooltip.input )tooltip.input.focus();
                this.loadCurrentFilterItem(tooltip.contentNode, row);
            }.bind(this),
            onQueryCreate : function(){

            }.bind(this),
            onHide : function(){
                tooltip.node.removeClass("mainColor_border");
                arrowNode.addClass("o2icon-chevron-thin-down").removeClass("o2icon-chevron-thin-up");
                target.removeClass("filterItem-content-over").removeClass("mainColor_border");
                this.currentTooltip = null;
            }.bind(this),
            onSetCoondinates: function (obj) {
                obj.left = obj.left + 20
            }
        });
        this.tooltipList.push( tooltip );
    },
    loadCurrentFilterItem: function(node, row){
        node.empty();
        var itemWrap = new Element("div.filterItem-subitemWrap").inject(node);
        var array = this.data.data.facetList.filter(function (field) {
            return row.field === field.field
        });
        if( array.length > 0 ){
            var lp = this.app.lp;

            if( array[0].valueCountPairList.length > 1 ){
                var multiButton = new Element("div.multi-button", {
                    events: {
                        "click": function () {  this.loadMultiFilterItem(node, row) }.bind(this),
                        "mouseenter": function () { multiButton.addClass("mainColor_color").addClass("mainColor_border") },
                        "mouseleave": function () { multiButton.removeClass("mainColor_color").removeClass("mainColor_border") },
                    }
                }).inject( itemWrap );
                new Element("i.o2icon-add").inject( multiButton );
                new Element("span", {text: lp.multiSelect}).inject( multiButton );
            }

            array[0].valueCountPairList.each(function (v) {
                // v.field = row.field;
                // v.parentLabel = row.label;
                if (["category","completed"].contains(row.field)) {
                    v.label = lp[v.value.toString()] || v.value;
                } else {
                    v.label = v.value;
                }

                var subitemNode = new Element("div.filterItem-subitem", {
                    text: v.label + "("+v.count+")",
                    events: {
                        "click": function () {
                            if( this.searching )return;
                            this.selectedFilterList.push({
                                label: row.label,
                                field: row.field,
                                labelList: [v.label],
                                valueList: [v.value]
                            });
                            this._search(null, true);
                        }.bind(this),
                        "mouseenter": function () { subitemNode.addClass("mainColor_color") },
                        "mouseleave": function () { subitemNode.removeClass("mainColor_color") },
                    }
                }).inject(itemWrap)
            }.bind(this));
        }
    },
    loadMultiFilterItem: function(node, row){
        node.empty();
        var itemWrap = new Element("div.filterItem-subitemWrap").inject(node);
        var array = this.data.data.facetList.filter(function (field) {
            return row.field === field.field
        });
        var lp = this.app.lp;

        array[0].valueCountPairList.each(function (v) {
            var sNode = new Element("div.filterItem-subitem", {
                events: {
                    "click": function () {
                        if( sNode.retrieve("checked") ){
                            sNode.store("checked", false);
                            icon.removeClass("o2icon-check_box").addClass("o2icon-check_box_outline_blank")
                        }else{
                            sNode.store("checked", true).addClass("mainColor_color");
                            icon.addClass("o2icon-check_box").removeClass("o2icon-check_box_outline_blank")
                        }
                    },
                    "mouseenter": function () { if( !sNode.retrieve("checked") )sNode.addClass("mainColor_color") },
                    "mouseleave": function () {  if( !sNode.retrieve("checked") )sNode.removeClass("mainColor_color") },
                }
            }).inject(itemWrap);
            sNode.store("v", v);
            var icon = new Element("i.checkbox").inject(sNode);
            icon.addClass("o2icon-check_box_outline_blank");
            var span = new Element("span", {
                text: v.label + "("+v.count+")",
            }).inject(sNode);
        });

        var buttonArea = new Element("div.filter-buttonArea").inject(node);
        var okButton = new Element("div.filter-okButton", {
            text: lp.ok,
            events: {
                "click": function () {
                    if( this.searching )return;
                    var valueList = [], labelList = [];
                    node.getElements("div.filterItem-subitem").each(function (sNode) {
                        if( sNode.retrieve("checked") ){
                            var v = sNode.retrieve("v");
                            valueList.push( v.value );
                            labelList.push( v.label );
                        }
                    })
                    this.selectedFilterList.push({
                        label: row.label,
                        field: row.field,
                        labelList: labelList,
                        valueList: valueList
                    });
                    this._search(null, true);
                }.bind(this)
            }
        }).inject(buttonArea);
        okButton.addClass("mainColor_bg");

        var cancelButton = new Element("div.filter-cancelButton", {
            text: lp.cancel,
            events: {
                "click": function () { this.loadCurrentFilterItem(node, row) }.bind(this)
            }
        }).inject(buttonArea);
    },


    iconOver: function(e){
        e.target.addClass('mainColor_color');
    },
    iconOut: function(e){
        e.target.removeClass('mainColor_color');
    },

    inputOver: function(e){
        this.app.getEventTarget(e, "view_inputArea").addClass('mainColor_border');
    },
    inputOut: function(e){
        this.app.getEventTarget(e, "view_inputArea").removeClass('mainColor_border');
    },
    mainColorOver: function (className, e) {
        var target = this.app.getEventTarget(e, className);
        if(target)target.addClass('mainColor_color');
    },
    mainColorOut: function (className, e) {
        var target = this.app.getEventTarget(e, className);
        if(target)target.removeClass('mainColor_color');
    },

    thEneter: function(e){
        if( this.moving )return;
        var th = this.app.getEventTarget(e, "th");
        th.getElement("i.move").setStyle("display","inline");
    },
    thLeave: function(e){
        var th = this.app.getEventTarget(e, "th");
        th.getElement("i.move").hide();
    },
    moveClick: function(e){
        e.stopPropagation();
    },
    move: function(e){
        if( !e.page )e.page = {
            x: e.pageX,
            y: e.pageY
        };
        var curTh = this.app.getEventTarget(e, "th");
        var titleNodes = [];
        curTh.getParent("tr").getElements("th").each(function(th, i){
            if (th!==curTh && i!==0){
                titleNodes.push(th);
            }
        }.bind(this));

        this._createMoveNode( curTh );

        this._setNodeMove(titleNodes, e, curTh);

        e.stopPropagation();
    },
    _createMoveNode: function( curTh ){
        this.moveNode = new Element("div", {"text": curTh.get("text")});
        this.moveNode.inject(this.node);
        this.moveNode.setStyles({
            "border": "2px dashed #ffa200",
            "opacity": 0.7,
            "height": "30px",
            "line-height": "30px",
            "padding": "0px 10px",
            "position": "absolute"
        });
    },
    _setMoveNodePosition: function(e){
        var x = e.pageX+2;
        var y = e.pageY+2;
        this.moveNode.positionTo(x, y);
    },
    createMoveFlagNode: function(){
        this.moveFlagNode = new Element("div", {styles:{
                "height": "13px",
                "width": "5px",
                //"border": "0px dashed #333",
                "border-right": "1px solid #BBB",
                "border-left": "1px solid #FFF",
                "overflow": "hidden",
                "display": "inline-block",
                "background": "#ffa200"
            }
        })
    },
    _setNodeMove: function(droppables, e, curTh){
        this.moveFlagNode = null;
        this.moving = true;
        this._setMoveNodePosition(e);
        var movePosition = this.moveNode.getPosition();
        var moveSize = this.moveNode.getSize();
        var contentPosition = curTh.getParent("tr").getPosition();
        var contentSize = curTh.getParent("tr").getSize();

        var nodeDrag = new Drag.Move(this.moveNode, {
            "droppables": droppables,
            "limit": {
                "x": [contentPosition.x, contentPosition.x+contentSize.x],
                "y": [movePosition.y, movePosition.y+moveSize.y]
            },
            "onEnter": function(dragging, inObj){
                if (!this.moveFlagNode) this.createMoveFlagNode();
                this.moveFlagNode.inject(inObj, "top");
            }.bind(this),
            "onLeave": function(dragging, inObj){
                if (this.moveFlagNode){
                    this.moveFlagNode.dispose();
                }
            }.bind(this),
            "onDrop": function(dragging, inObj){
                if (inObj){
                    var oldIdx, newIdx, curFieldObj, curField = curTh.get("data-o2-field"), inObjField = inObj.get("data-o2-field");
                    this.selectedFieldList.each( function (f, i) {
                        if( f.field === curField ){
                            oldIdx = i;
                            curFieldObj = f;
                        }
                    });
                    this.selectedFieldList.splice(oldIdx, 1);

                    this.selectedFieldList.each( function (f, i) {
                        if( f.field === inObjField )newIdx = i;
                    });
                    this.selectedFieldList.splice(newIdx, 0, curFieldObj);

                    var curThIdx, inObjIdx;
                    curTh.getParent("tr").getElements("th").each(function (th, i) {
                        if( th === curTh )curThIdx = i;
                        if( th === inObj )inObjIdx = i;
                    });
                    curTh.inject(inObj, "before");
                    curTh.getParent("table").getElements("tr").each(function (tr, i) {
                        if( i === 0 )return;
                        var tds = tr.getElements("td");
                        tds[curThIdx].inject( tds[inObjIdx], "before" );
                    });

                    var addColumnIcon = curTh.getElement("i.addcolumnicon");
                    if( addColumnIcon ){
                        addColumnIcon.inject( curTh.getParent("tr").getElements("th").getLast() )
                    }

                    this.saveUserData();

                    if (this.moveNode) this.moveNode.destroy();
                    if (this.moveFlagNode)this.moveFlagNode.destroy();
                }else{
                    if (this.moveNode) this.moveNode.destroy();
                    if (this.moveFlagNode)this.moveFlagNode.destroy();
                }
                this.moving = false;
            }.bind(this),
            "onCancel": function(dragging){
                if (this.moveNode) this.moveNode.destroy();
                if (this.moveFlagNode)this.moveFlagNode.destroy();
                this.moving = false;
            }.bind(this)
        });
        nodeDrag.start(e);
    },
    saveUserData: function () {
        this.selectedFieldList.map(function (d) {
            if( d._ )delete d._;
            return d;
        });
        var ids = this.appList.map(function (app) {
            return app.key
        });
        ids.sort();
        ids = ids.join("-");
        this.userData = {"selectedFieldList": Array.clone(this.selectedFieldList) };
        this.app.userData.saveViewData(ids, this.userData )
    },
    getUserData: function ( callback ) {
        var ids = this.appList.map(function (app) {
            return app.key
        });
        ids.sort();
        ids = ids.join("-");
        this.app.userData.getViewData(ids).then(function ( userData ) {
            this.userData = userData || {};
            callback();
        }.bind(this));
    },
    showEmpty: function(){
        var html =
            '<div class="listNoData">'+
            '   <div class="listNoAppIcon"></div>'+
            '   <div class="listNoDataText">'+this.app.lp.noAppNote+'</div>'+
            '</div>';
        this.node.set("html", html);
    }
});


MWF.xApplication.StandingBook.ConditionTooltip = new Class({
    Extends: MTooltips,
    _customNode: function(node, contentNode){
    },
    _loadCustom : function( callback ){
        if(callback)callback();
    }
});