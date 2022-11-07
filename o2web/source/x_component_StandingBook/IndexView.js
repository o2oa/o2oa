o2.require("o2.widget.Paging", null, false);
o2.requireApp("StandingBook", "Common", null, false);
MWF.xApplication.StandingBook.IndexView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date": null
    },
    initialize: function(node, app, options, tabData){
        this.setOptions(options);

        this.path = "../x_component_StandingBook/$IndexView/";

        this.app = app;
        this.container = $(node);
        this.tabData = tabData;
        this.facetOrderList = ["category","applicationName", "processName","appName","categoryName"];
        this.load();
    },
    recordStatus: function(){
        return {
            view: "index",
            tab: {
                key: this.tabData.key,
                type: this.tabData.type,
                name: this.tabData.name,
                category: this.tabData.category
            }
        }
    },
    load: function(){

        this.pageSize = 10;
        this.currentKey = "";

        this.docPageNum = 1;
        this.docTotal = 0;

        // this.selectedConditionList = [];
        // this.selectedFieldList = [];

        this.execludedFaceList = ["creatorUnitLevelName"];

        var url = this.path+this.options.style+"/view.html";
        this.container.loadHtml(url, {"bind": {
                "lp": this.app.lp,
                "tabData": this.tabData
            }, "module": this}, function(){
            // this.loadSelectedCondition();
            this.search();
        }.bind(this));
    },
    reload: function(){
        this.container.empty();
        this.load();
    },
    destroy : function(){
        // this.app.removeEvent("resize", this.resetNodeSizeFun );
        this.container.empty();
    },
    selectTab: function(){
        this.app.selectTab(null, null, true);
    },
    searchByCondition: function(){
        this.dynamicConditionList = [];
        Object.each(this.dynamicConditionValueMap, function (data, key) {
            if( o2.typeOf(data) === "array"){
                this.dynamicConditionList.push({
                    field: key,
                    min: data[0],
                    max: data[1]
                });
            }else if(o2.typeOf(data) === "string"){
                this.dynamicConditionList.push({
                    field: key,
                    valueList: ["*"+data+"*"]
                });
            }
        }.bind(this));

        this.selectedConditionList = this.selectedConditionListTemporary;

        this._search(null, true);
    },
    searchKeydown: function(e){
        if( e.keyCode === 13 ){
            this.search();
        }
    },
    reset: function(){
        this.defaultConditionArea = null;
        this.selectedConditionList = [];
        this.selectedConditionListTemporary = [];

        this.dynamicConditionList = [];
        this.dynamicConditionValueMap = {};
        this.loadSelectedCondition();
        this._search();
    },
    search: function(pageNum, query){
        this.selectedConditionList = [];
        this.selectedConditionListTemporary = [];

        this.dynamicConditionList = [];
        this.dynamicConditionValueMap = {};

        this.sortValueMap = {};

        this.selectedFieldList = [];
        this.loadSelectedCondition();
        this._search(pageNum, query);
    },
    _search: function( pageNum, byCondition ){
        pageNum = o2.typeOf(pageNum) === "number" ? pageNum : null;
        this.docPageNum = pageNum || 1;
        // this.currentKey = this.searchInput.get("value") || "";
        // if( this.currentKey ){

        var startDate = new Date();

        var filterList = this.selectedConditionList.map(function(cond){
            return {
                field: cond.field,
                valueList: cond.valueList
            };
        });
        filterList = filterList.concat(this.dynamicConditionList);

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

        o2.Actions.load("x_query_assemble_surface").IndexAction.post({
            key: this.tabData.key,
            type: this.tabData.type,
            category: this.tabData.category,
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
            if(!this.fieldSelectArea){
                this.loadFieldSelect(json);
            }

            if( !this.defaultConditionArea ){
                this.loadCondition();
            }else if( byCondition ){
                this.loadSelectedCondition();
                this.loadDefaultCondition(true);
            }

            this.loadDocList(json);
            if(!pageNum){
                var endDate = new Date();
                var t = endDate.getTime()-startDate.getTime();
                t = ((t/1000)*100).toInt()/100;
                var text = this.app.lp.docTotalInfor.replace("{count}", this.docTotal||0).replace("{time}", t);
                this.docTotalNode.set("html", text);
                this.loadDocPagination();
            }
        }.bind(this));
    },
    exportExcel: function(){

        var filterList = this.selectedConditionList.map(function(cond){
            return {
                field: cond.field,
                valueList: cond.valueList
            };
        });
        filterList = filterList.concat(this.dynamicConditionList);

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

        o2.Actions.load("x_query_assemble_surface").IndexAction.export({
            key: this.tabData.key,
            type: this.tabData.type,
            category: this.tabData.category,
            // sortList: sortList,
            sort: sortList[0] || null,
            fixedFieldList: fixedFieldList,
            dynamicFieldList: dynamicFieldList,
            filterList: filterList
        }).then(function(json){
            debugger;
            var indexAction = o2.Actions.load("x_query_assemble_surface").IndexAction;
            var address = indexAction.action.address;
            var uri = indexAction.action.actions.exportResult.uri.replace("{flag}", json.data.id);
            window.open(address+uri, "_blank");
        }.bind(this));
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
    openItem: function(id, event, row){
        if( this.tabData.type === "process" || this.tabData.type === "application" ){
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

    orderColumn: function(field, e, row){
        debugger;
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
    loadDocList: function(json){
        this.docListNode.empty();

        debugger;

        var fieldList = this.selectedFieldList.map(function (field) {
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
            return field;
        }.bind(this));

        this.docListNode.loadHtml(this.path+this.options.style+"/docList.html",
            {
                "bind": {"lp": this.app.lp, "data": json.data.documentList, "fieldList":fieldList},
                "module": this,
                "reload": true
            },
            function(){

            }.bind(this)
        );
    },
    loadDocPagination: function(){
        this.docPaginationNode.empty();
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
                this._search(pageNum);
            }.bind(this)
        });
        this.docPaging.load();
    },


    loadFieldSelect: function(json){
        json.data.fixedFieldList.map(function(d){
            d.fixed = true;
            d.selected = true;
        });

        this.selectedFieldList = json.data.fixedFieldList;

        this.fieldSelectedArea.empty();
        this.fieldSelectedArea.loadHtml(this.path+this.options.style+"/fieldSelect.html",
            {
                "bind": {
                    "lp": this.app.lp,
                    "data": json.data.fixedFieldList.concat(json.data.dynamicFieldList)
                    // "status":{
                    //     isShowAllFieldSelect: !!this.isShowAllFieldSelect
                    // }
                },
                "module": this,
                "reload": true
            },
            function(){

            }.bind(this)
        );
        // }.bind(this));
    },
    fieldOver: function(e){
        var target = this.app.getEventTarget(e, "fieldSelect-item-content");
        if(!target.retrieve("selected"))target.addClass('mainColor_bg_opacity');
    },
    fieldOut: function(e){
        var target = this.app.getEventTarget(e, "fieldSelect-item-content");
        if(!target.retrieve("selected"))target.removeClass('mainColor_bg_opacity');
    },
    switchFieldSelected: function(e, item){
        var index = -1;
        var target = this.app.getEventTarget(e, "fieldSelect-item-content");
        this.selectedFieldList.each(function (obj, idx) {
            if( obj.field === item.field )index = idx;
        });
        if( index > -1 ){ //已经选择了
            // e.target.store("selected", false);
            target.getElement("i.checkbox").removeClass("o2icon-check_box").addClass("o2icon-check_box_outline_blank").removeClass("mainColor_color");
            this.selectedFieldList.splice(index, 1);
            if( this.sortValueMap[item.field] )delete this.sortValueMap[item.field];
            // this.loadSelectedCondition();
        }else{ //还没有选择
            // e.target.store("selected", true);
            target.getElement("i.checkbox").removeClass("o2icon-check_box_outline_blank").addClass("o2icon-check_box").addClass("mainColor_color");
            this.selectedFieldList.push( item );
            // this.loadSelectedCondition();
        }
        this._search( this.docPageNum );
    },
    checkSwitchFieldSelect: function(e){
        debugger;
        var p = this.app.getEventTarget(e, "fieldSelect");
        var more = p.getElement(".fieldSelect-switch-action");
        var ul = p.getElement(".fieldSelect-content");
        if( 80 < ul.scrollHeight ){
            more.show();
        }else{
            more.hide();
        }
    },
    switchFieldSelectContent: function(e){
        var p = this.app.getEventTarget(e, "fieldSelect");
        var more = p.getElement(".fieldSelect-switch-action");
        var ul = p.getElement(".fieldSelect-content");
        if( ul.retrieve("expand") ){
            ul.removeClass("fieldSelect-content_expand");
            more.getElement("i").addClass("o2icon-triangle_down").removeClass("o2icon-triangle_up");
            more.getElement("span").innerText = this.app.lp.more;
            ul.store("expand", false);
        }else{
            ul.addClass("fieldSelect-content_expand");
            more.getElement("i").addClass("o2icon-triangle_up").removeClass("o2icon-triangle_down");
            more.getElement("span").innerText = this.app.lp.collapse;
            ul.store("expand", true);
        }
    },

    loadCondition: function( ){
        var json = this.data;
        var lp = this.app.lp;
        var facetList = [];
        var index = 0;
        json.data.dynamicFieldList.each(function (field) {
            field.label = lp[field.field] || field.name || field.field;
            if( !field.fixed ){
                if( field.fieldType === "date" || field.fieldType === "number" ){
                    if( field.min && field.min !== "null" && field.max && field.max !== "null" && (field.min != field.max) ){
                        // if( this.dynamicConditionValueMap[field.field] ){
                        //     field.data = this.dynamicConditionValueMap[field.field];
                        // }
                        field.index = index;
                        index++;
                        facetList.push(field);
                    }
                }else{
                    // if( this.dynamicConditionValueMap[field.field] ){
                    //     field.data = this.dynamicConditionValueMap[field.field];
                    // }
                    field.index = index;
                    index++;
                    facetList.push(field);
                }
            }
        }.bind(this));
        this.conditionArea.empty();
        this.conditionArea.loadHtml(this.path+this.options.style+"/condition.html",
            {
                "bind": {
                    "lp": this.app.lp,
                    "data": facetList,
                    "status":{
                        isShowAllFilter: !!this.isShowAllFilter
                    }
                },
                "module": this,
                "reload": true
            },
            function(){
                this.loadDefaultCondition();
            }.bind(this)
        );
    },
    loadDefaultCondition: function(){
        var lp = this.app.lp;
        var json = this.data;
        var index = 0;
        var facetList = [];
        this.orderFacet(json.data.facetList).each(function(d) {
            if (d.valueCountPairList && d.valueCountPairList.length){
                d.index = index;
                index++;
                d.label = lp[d.field] || d.name || d.field;
                d.valueCountPairList.each(function (v) {
                    v.field = d.field;
                    v.parentLabel = d.label;
                    if (d.field === "category") {
                        v.label = lp[v.value] || v.value;
                    } else {
                        v.label = v.value;
                    }
                });
                facetList.push(d);
            }
        });
        this.defaultConditionArea.empty();
        this.defaultConditionArea.loadHtml(this.path+this.options.style+"/conditionItem_default.html",
            {
                "bind": {
                    "lp": this.app.lp,
                    "data": facetList,
                    "status":{
                        isShowAllFilter: !!this.isShowAllFilter
                    }
                },
                "module": this,
                "reload": true
            },
            function(){

            }.bind(this)
        );
    },
    loadCustomConditionItem: function(e, row){
        var setData = function(data) {
            if( data && data.length ){
                this.dynamicConditionValueMap[row.field] = data
            }else if(this.dynamicConditionValueMap[row.field]){
                delete this.dynamicConditionValueMap[row.field]
            }
        }.bind(this);
        switch (row.fieldType) {
            case "string":
                new MWF.xApplication.StandingBook.Input(e.target, {
                    id: row.name,
                    onChange: function (data) {
                        // this.changeCustomConditionItemString(row, data);
                        setData(data);
                    }.bind(this)
                }, this.app);
                break;
            case "date":
                new MWF.xApplication.StandingBook.DatePicker(e.target, {
                    start: row.min,
                    end: row.max,
                    id: row.name,
                    onChange: function (data) {
                        // this.changeCustomConditionItemRange(row, data);
                        setData(data);
                    }.bind(this)
                }, this.app);
                break;
            case "number":
                new MWF.xApplication.StandingBook.NumberRange(e.target, {
                    min: row.min,
                    max: row.max,
                    id: row.name,
                    onChange: function (data) {
                        // this.changeCustomConditionItemRange(row, data);
                        setData(data);
                    }.bind(this)
                }, this.app);
                break;
        }
    },
    // changeCustomConditionItemRange: function(row, data){
    //     var modified = false;
    //     var index = -1;
    //     this.dynamicConditionList.each(function (cond, idx) {
    //         if( cond.field === row.field ){
    //             index = idx;
    //             if( data.length ){
    //                 if( data[0] !== cond.min || data[1] !== cond.max ){
    //                     cond.min = data[0];
    //                     cond.max = data[1];
    //                     this.dynamicConditionValueMap[row.field] = data;
    //                     modified = true;
    //                 }
    //             }
    //         }
    //     }.bind(this))
    //     if( data.length ){
    //         if( index === -1 ){
    //             this.dynamicConditionList.push({
    //                 field : row.field,
    //                 min: data[0],
    //                 max: data[1]
    //             });
    //             this.dynamicConditionValueMap[row.field] = data;
    //             modified = true;
    //         }
    //     }else{
    //         if( index > -1 ){
    //             this.dynamicConditionList.splice(index, 1);
    //             delete this.dynamicConditionValueMap[row.field];
    //             modified = true;
    //         }
    //     }
    //     // if(modified)this._search();
    // },
    // changeCustomConditionItemString: function(row, data){
    //     var modified = false;
    //     var index = -1;
    //     this.dynamicConditionList.each(function (cond, idx) {
    //         if( cond.field === row.field ){
    //             index = idx;
    //             if( data !== cond.value ){
    //                 cond.value = data;
    //                 cond.valueList = ["*"+data+"*"];
    //                 this.dynamicConditionValueMap[row.field] = data;
    //                 modified = true;
    //             }
    //         }
    //     }.bind(this))
    //     if( data ){
    //         if( index === -1 ){
    //             this.dynamicConditionList.push({
    //                 field : row.field,
    //                 value: data,
    //                 valueList: ["*"+data+"*"]
    //             });
    //             this.dynamicConditionValueMap[row.field] = data;
    //             modified = true;
    //         }
    //     }else{
    //         if( index > -1 ){
    //             this.dynamicConditionList.splice(index, 1);
    //             delete this.dynamicConditionValueMap[row.field];
    //             modified = true;
    //         }
    //     }
    //     // if(modified)this._search();
    // },

    loadSelectedCondition: function(){
        this.conditionSelectedArea.empty();
        this.conditionSelectedArea.loadHtml(this.path+this.options.style+"/conditionSelected.html",
            {
                "bind": {"lp": this.app.lp, "data": this.selectedConditionList},
                "module": this,
                "reload": true
            },
            function(){

            }.bind(this)
        );
        // }.bind(this));
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
    switchMoreFilterItem: function(e){
        var target = this.app.getEventTarget(e, "switch-filter-action");
        var textNode = target.getElement(".switch-filter-text");
        var iconNode = target.getElement(".switch-filter-icon");
        if( this.isShowAllFilter ){
            textNode.set("text", this.app.lp.moreFilter);
            iconNode.removeClass("o2icon-chevron-thin-up").addClass("o2icon-chevron-thin-down");
            this.isShowAllFilter = false;
            this.dynamicConditionArea.getElements(".item").each(function(item, index){
                if(index > 0)item.hide();
            })
        }else{
            textNode.set("text", this.app.lp.collapseFilter);
            iconNode.removeClass("o2icon-chevron-thin-down").addClass("o2icon-chevron-thin-up");
            this.isShowAllFilter = true;
            this.dynamicConditionArea.getElements(".item").each(function(item){
                item.show();
            })
        }

    },
    checkSwitchButton: function(e){
        var p = e.target.getParent();
        var more = p.getElement(".switch-button");
        var ul = p.getElement(".subitem-wrap");
        if( 41 < ul.scrollHeight ){
            more.show();
        }else{
            more.hide();
        }
    },
    switchConditionItem: function(e){
        var more = this.app.getEventTarget(e, "switch-button");
        var ul = more.getParent().getElement(".subitem-wrap");
        if( ul.retrieve("expand") ){
            ul.removeClass("subitem-wrap_expand");
            more.getElement("i").addClass("o2icon-triangle_down").removeClass("o2icon-triangle_up");
            more.getElement("span").innerText = this.app.lp.more;
            ul.store("expand", false);
        }else{
            ul.addClass("subitem-wrap_expand");
            more.getElement("i").addClass("o2icon-triangle_up").removeClass("o2icon-triangle_down");
            more.getElement("span").innerText = this.app.lp.collapse;
            ul.store("expand", true);
        }
    },
    changeCondition: function(e, item){
        var index = -1;
        this.selectedConditionList.each(function (cond, idx) {
            if( cond.field === item.field )index = idx;
        });
        if( index > -1 ) { //已经选择了
            this.selectedConditionList.splice(index, 1);
        }else{
            this.selectedConditionList.push(item);
        }
        this.loadSelectedCondition();
        this._search(null, true);
    },
    changeSingleCondition: function(e, item){
        var itemIndex = -1, valueIndex = -1;
        this.selectedConditionListTemporary.each(function (cond, idx) {
            if( cond.field === item.field ){
                itemIndex = idx;
                valueIndex = cond.valueList.indexOf(item.value);
            }
        });
        if( valueIndex > -1 ){ //已经选择了
            e.target.removeClass('mainColor_bg');
            e.target.store("selected", false);
            this.selectedConditionListTemporary[itemIndex].valueList.splice(valueIndex, 1);
            this.selectedConditionListTemporary[itemIndex].labelList.splice(valueIndex, 1);
            if( !this.selectedConditionListTemporary[itemIndex].valueList.length ){
                this.selectedConditionListTemporary.splice(itemIndex, 1);
            }
            // this.loadSelectedCondition();
        }else{ //还没有选择
            e.target.removeClass('mainColor_bg_opacity');
            e.target.addClass('mainColor_bg');
            e.target.store("selected", true);
            if( itemIndex > -1 ){
                this.selectedConditionListTemporary[itemIndex].valueList.push(item.value);
                this.selectedConditionListTemporary[itemIndex].labelList.push(item.label);
            }else{
                this.selectedConditionListTemporary.push({
                    field: item.field,
                    parentLabel: item.parentLabel,
                    valueList: [item.value],
                    labelList: [item.label]
                });
            }
            // this.loadSelectedCondition();
        }
        // this._search();
    },
    // changeSingleCondition: function(e, item){
    //     var itemIndex = -1, valueIndex = -1;
    //     this.selectedConditionList.each(function (cond, idx) {
    //         if( cond.field === item.field ){
    //             itemIndex = idx;
    //             valueIndex = cond.valueList.indexOf(item.value);
    //         }
    //     });
    //     if( valueIndex > -1 ){ //已经选择了
    //         e.target.removeClass('mainColor_bg');
    //         e.target.store("selected", false);
    //         this.selectedConditionList[itemIndex].valueList.splice(valueIndex, 1);
    //         this.selectedConditionList[itemIndex].labelList.splice(valueIndex, 1);
    //         if( !this.selectedConditionList[itemIndex].valueList.length ){
    //             this.selectedConditionList.splice(itemIndex, 1);
    //         }
    //         this.loadSelectedCondition();
    //     }else{ //还没有选择
    //         e.target.removeClass('mainColor_bg_opacity');
    //         e.target.addClass('mainColor_bg');
    //         e.target.store("selected", true);
    //         if( itemIndex > -1 ){
    //             this.selectedConditionList[itemIndex].valueList.push(item.value);
    //             this.selectedConditionList[itemIndex].labelList.push(item.label);
    //         }else{
    //             this.selectedConditionList.push({
    //                 field: item.field,
    //                 parentLabel: item.parentLabel,
    //                 valueList: [item.value],
    //                 labelList: [item.label]
    //             });
    //         }
    //         this.loadSelectedCondition();
    //     }
    //     this._search();
    // },
    subConditionOver: function(e){
        if(!e.target.retrieve("selected"))e.target.addClass('mainColor_bg_opacity');
    },
    subConditionOut: function(e){
        if(!e.target.retrieve("selected"))e.target.removeClass('mainColor_bg_opacity');
    },
    removeSelectedConditionItem: function(e, item){
        this.changeCondition(e, item);
    },
    conditionSelectedOver: function(e){
        this.app.getEventTarget(e, "item").getElement("i").addClass("icon_over");
    },
    conditionSelectedOut: function(e){
        this.app.getEventTarget(e, "item").getElement("i").removeClass("icon_over");
    },
    mainColorOver: function (className, e) {
        var target = this.app.getEventTarget(e, className);
        if(target)target.addClass('mainColor_color');
    },
    mainColorOut: function (className, e) {
        var target = this.app.getEventTarget(e, className);
        if(target)target.removeClass('mainColor_color');
    }

});