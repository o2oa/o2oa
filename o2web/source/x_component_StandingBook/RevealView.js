o2.requireApp("StandingBook", "IndexView", null, false);
MWF.xApplication.StandingBook.RevealView = new Class({
    Extends: MWF.xApplication.StandingBook.IndexView,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date": null
    },
    initialize: function(node, app, options, reveal){
        this.setOptions(options);


        this.path = "../x_component_StandingBook/$IndexView/";

        this.app = app;
        this.node = $(node);
        this.container = new Element("div", {
            style: "height:100%"
        }).inject(this.node);

        this.reveal = reveal[0];

        this.revealId = reveal[0].id;

        this.facetOrderList = ["category","applicationName", "processPlatform_string_applicationName", "processName", "processPlatform_string_processName","appName", "cms_string_appName","categoryName", "cms_string_categoryName"];

        // this.container.loadCss(this.path + this.options.style + "/style.css");

        o2.Actions.load("x_custom_index_assemble_control").RevealAction.get( this.revealId, function (json) {

            this.revealUpdateTime = json.date;
            this.revealSourceData = json.data;
            this.appList = (json.data.processPlatformList || []).concat( json.data.cmsList || [] );

            var selectedFieldList = [], fieldList = [], filterlist = [], fieldMap = {};
            json.data.data.each(function (field) {
                if( field.text )field.name = field.text;
                if( field.displayDefault )selectedFieldList.push( field );
                if( field.display ){
                    fieldList.push( field );
                    fieldMap[ field.field ] = field;
                }
                if( field.filter )filterlist.push( field );
            });
            this.revealData = {
                selectedFieldList: selectedFieldList,
                fieldList: fieldList,
                fieldMap: fieldMap,
                filterlist: filterlist
            };
            this.load();
        }.bind(this))
    },
    recordStatus: function(){
        return {
            view: "reveal",
            revealId: this.revealId
        }
    },
    mergeSelectedFieldList: function(){
        var arr = Array.clone(this.userData.selectedFieldList || []);
        var map = {};
        arr.each(function (d) { map[d.field] = true });
        ( this.revealData.selectedFieldList || [] ).each(function (s) {
            if( map[s.field] ){
                arr.push( s );
                map[s.field] = true;
            }
        });
        return arr;
    },
    search: function(pageNum, query){
        if( this.searching )return;
        this.selectedFilterList = [];
        this.sortValueMap = {};
        // if( this.userData.updateTime && this.revealUpdateTime && (new Date( this.userData.updateTime ) < new Date( this.revealUpdateTime )) ){
        //     this.selectedFieldList = this.mergeSelectedFieldList();
        // }else{
            this.selectedFieldList = Array.clone(this.userData.selectedFieldList || this.revealData.selectedFieldList);
        // }
        this._search(pageNum, query);
    },
    _search: function( pageNum, byCondition ){
        if( this.searching )return;
        this.searching = true;

        pageNum = o2.typeOf(pageNum) === "number" ? pageNum : null;
        this.docPageNum = pageNum || 1;


        var startDate = new Date();

        var filterList = this.selectedFilterList.map(function(cond){
            if( cond.dynamic ){
                if( o2.typeOf(cond.value) === "array"){
                    return {
                        field: cond.field,
                        min: cond.value[0],
                        max: cond.value[1]
                    };
                }else if(o2.typeOf(cond.value) === "string"){
                    return {
                        field: cond.field,
                        valueList: [cond.value]
                    }
                }
            }else{
                return {
                    field: cond.field,
                    valueList: cond.valueList
                };
            }
        });
        filterList = filterList.clean();

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
            revealId: this.revealId,
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

            // if(!this.commonFieldsLoaded && (json.data.fixedFieldList.length || json.data.dynamicFieldList.length)){
            //
            //     this.commonFieldsLoaded = true;
            //
            //     this.commonFields = [];
            //     this.uncommonFields = [];
            //     if( !this.selectedFieldList )this.selectedFieldList = [];
            //
            //     json.data.fixedFieldList.map(function(field){
            //         field.fixed = true;
            //         field.name = field.name || "";
            //
            //         field.fieldName = this.getFieldName(field);
            //
            //         if( this.isCommonField(field) ){
            //             if( !this.userData.selectedFieldList || this.userData.selectedFieldList.length === 0 ){
            //                 this.selectedFieldList.push( field );
            //             }
            //             this.commonFields.push( field );
            //         }else{
            //             this.uncommonFields.push( field );
            //         }
            //
            //     }.bind(this));
            //
            //     json.data.dynamicFieldList.each(function (field) {
            //         if( !field.fixed ){
            //
            //             field.fieldName = this.getFieldName(field);
            //
            //             var name = field.name || "";
            //             field.name = this.endsWith(name, ".name") ? name.substring(0, name.length-".name".length) : name;
            //
            //             if( this.isCommonField(field) ){
            //                 this.commonFields.push( field );
            //             }else{
            //                 this.uncommonFields.push( field );
            //             }
            //         }
            //     }.bind(this));
            // }

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
    exportExcel: function(){

        var filterList = this.selectedFilterList.map(function(cond){
            if( cond.dynamic ){
                if( o2.typeOf(cond.value) === "array"){
                    return {
                        field: cond.field,
                        min: cond.value[0],
                        max: cond.value[1]
                    };
                }else if(o2.typeOf(cond.value) === "string"){
                    return {
                        field: cond.field,
                        valueList: [cond.value]
                    }
                }
            }else{
                return {
                    field: cond.field,
                    valueList: cond.valueList
                };
            }
        });
        filterList = filterList.clean();

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
            revealId: this.revealId,
            directoryList: this.appList,
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
                    "name": "字段",
                    "id": "category1",
                    "subItemList": this.revealData.fieldList
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

        //无数据且未经过过滤时不显示过滤
        if( (!json.data.documentList || json.data.documentList.length === 0 ) && this.selectedFilterList.length === 0 ){
            if (callback) callback();
            return;
        }

        this.orderFacet(this.revealData.filterlist).each(function(filter) {
        // this.revealData.filterlist.each(function (filter) {

            for( var i=0; i<this.selectedFilterList.length; i++ ){
                if( this.selectedFilterList[i].field === filter.field ){
                    return;
                }
            }

            //默认的过滤条件
            var arr = (json.data.facetList || []).filter(function (facet) {
                return facet.field === filter.field;
            });
            if( arr.length ){
                var d = arr[0];
                if (d.valueCountPairList && d.valueCountPairList.length){
                    d.facet = true;
                    d.index = index;
                    index++;
                    d.label = filter.text || lp[d.field] || d.name || d.field;
                    facetList.push(d);
                }
            }else{

                //动态字段过滤条件
                arr = (json.data.dynamicFieldList || []).filter(function (field) {
                    return field.field === filter.field;
                });
                if( arr.length ){
                    var field = arr[0];
                    field.label = filter.text || lp[field.field] || field.name || field.field;
                    if( field.fieldType === "date" || field.fieldType === "number" ){
                        if( typeOf(field.min) !== "null" && field.min !== "null" && typeOf(field.max) !== "null" && field.max !== "null" ){ //&& (field.min != field.max)
                            field.dynamic = true;
                            field.index = index;
                            index++;
                            facetList.push(field);
                        }
                    }else{
                        field.dynamic = true;
                        field.index = index;
                        index++;
                        facetList.push(field);
                    }
                }
            }
        }.bind(this));

        this.filterArea.empty();
        if( this.selectedFilterList.length || facetList.length ) {
            this.filterArea.loadHtml("../x_component_StandingBook/$RevealView/" + this.options.style + "/filter.html",
                {
                    "bind": {
                        "lp": this.app.lp,
                        "selectedFilterList": this.selectedFilterList,
                        "filterList": facetList
                    },
                    "module": this,
                    "reload": true
                },
                function () {
                    if (callback) callback();
                }.bind(this)
            );
        }
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
        var tooltip = new MWF.xApplication.StandingBook.RevealConditionTooltip(this.contentNode, target, this.app, row, {
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
                var width;
                if( row.dynamic ){
                    switch (row.fieldType) {
                        case "number":
                            width = Math.min(this.contentNode.getSize().x - 60, 400);
                            break;
                        case "date":
                            width = Math.min(this.contentNode.getSize().x - 60, 640);
                            break;
                        default:
                            width = Math.min(this.contentNode.getSize().x - 60, 600);
                            break;
                    }

                }else{
                    width = Math.min(this.contentNode.getSize().x - 60, 1400);
                }
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

                if( row.facet ){
                    this.loadCurrentFilterItem(tooltip.contentNode, row);
                }else{
                    this.loadDymanicFilterItem(tooltip.contentNode, row, tooltip);
                }
            }.bind(this),
            onQueryCreate : function(){

            }.bind(this),
            onHide : function(){
                if(tooltip.maskNode){
                    tooltip.maskNode.destroy();
                    tooltip.maskNode = null;
                }
                tooltip.options.isAutoHide = true;

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
    loadDymanicFilterItem: function(node, row, tooltip){
        if( this.searching )return;
        if( node.retrieve("loaded") )return;
        var setData = function(data) {
            if( !data )return;
            tooltip.hide();
            this.selectedFilterList.push({
                dynamic: true,
                label: row.label,
                field: row.field,
                value: data
            });
            this._search(null, true);
        }.bind(this);
        var focus = function () {
            tooltip.options.isAutoHide = false;

            if(!tooltip.maskNode)tooltip.maskNode = new Element("div.maskNode", {
                "styles": {
                    "width": "100%",
                    "height": "100%",
                    "opacity": 0,
                    "position": "absolute",
                    "background-color": "#fff",
                    "top": "0px",
                    "left": "0px"
                },
                "events": {
                    "mouseover": function (e) {
                        e.stopPropagation();
                    },
                    "mouseout": function (e) {
                        e.stopPropagation();
                    },
                    "click": function (e) {
                        tooltip.hide();
                        e.stopPropagation();
                    }.bind(this)
                }
            }).inject( tooltip.node, "before" );
        }
        switch (row.fieldType) {
            case "string":
                this.input = new MWF.xApplication.StandingBook.Input(node, {
                    id: row.name,
                    placeholder: "输入全匹配文字回车搜索："+ (row.label || row.name),
                    onEnter: function (data) {
                        // this.changeCustomConditionItemString(row, data);
                        setData(data);
                    }.bind(this),
                    onClear: function(data){
                        setData(data)
                    },
                    // onLoad: function () { this.focus(); },
                    onFocus: function () {
                        focus();
                    }
                }, this.app);
                break;
            case "date":
                new MWF.xApplication.StandingBook.DatePicker(node, {
                    start: row.min,
                    end: row.max,
                    id: row.name,
                    onChange: function (data) {
                        // this.changeCustomConditionItemRange(row, data);
                        setData(data);
                    }.bind(this),
                    onFocus: function () {
                        focus();
                    }
                    //onLoad: function () { this.focus(); }
                }, this.app);
                break;
            case "number":
                this.input = new MWF.xApplication.StandingBook.NumberRange(node, {
                    min: row.min,
                    max: row.max,
                    id: row.name,
                    onEnter: function (data) {
                        // this.changeCustomConditionItemRange(row, data);
                        setData(data);
                    }.bind(this),
                    onLoad: function () { this.focus(); }
                }, this.app);
                break;
        }
        node.store("loaded", true)
    },


    saveUserData: function () {
        this.selectedFieldList.map(function (d) {
            if( d._ )delete d._;
            return d;
        });
        this.userData = {
            "selectedFieldList": Array.clone(this.selectedFieldList),
            "updateTime": new Date().format("db")
        };
        this.app.userData.saveRevealData(this.revealId, this.userData );
    },
    getUserData: function ( callback ) {
        this.app.userData.getRevealData(this.revealId).then(function(json){
            if( json && json.selectedFieldList  ){
                var selectedFieldList = [];
                json.selectedFieldList.each(function (field) {
                    if( this.revealData.fieldMap[field.field] ){
                        var revelField = this.revealData.fieldMap[field.field];
                        if( revelField.text ){
                            field.name = revelField.text;
                            field.text = revelField.text;
                        }
                        selectedFieldList.push( field );
                    }
                }.bind(this));
                json.selectedFieldList = selectedFieldList;
            }
            this.userData = json || {};
            callback();
        }.bind(this));
    }

});


MWF.xApplication.StandingBook.RevealConditionTooltip = new Class({
    Extends: MTooltips,
    _customNode: function(node, contentNode){
    },
    _loadCustom : function( callback ){
        if(callback)callback();
    },
});
