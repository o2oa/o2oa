o2.require("o2.widget.Paging", null, false);
MWF.xApplication.process.Application.IndexView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date": null
    },
    initialize: function(node, app, options, tabData){
        this.setOptions(options);

        this.path = "../x_component_process_Application/$IndexView/";

        this.app = app;
        this.container = $(node);
        this.tabData = tabData;
        if( !this.tabData.type )this.tabData.type = "application";
        if( !this.tabData.category )this.tabData.category = "workCompleted";
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
        this.stylePath = this.path + this.options.style + "/style.css";
        this.container.loadCss(this.stylePath, this._load.bind(this));
    },
    _load: function(){

        this.pageSize = 10;
        this.currentKey = "";

        this.docPageNum = 1;
        this.docTotal = 0;

        // this.selectedConditionList = [];
        // this.selectedFieldList = [];

        this.execludedFaceList = ["creatorUnitLevelName"];

        var url = this.path+this.options.style+"/view/view.html";
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
        this.app.selectTab();
    },
    gotoFTSearch: function(){
        this.app.openFTSearchView( "", true );
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

        o2.Actions.load("x_custom_index_assemble_control").IndexAction.post({
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

        o2.Actions.load("x_custom_index_assemble_control").IndexAction.export({
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
            var indexAction = o2.Actions.load("x_custom_index_assemble_control").IndexAction;
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
    openWork: function(id, event, row){
        o2.api.page.openWork(id);
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

        this.docListNode.loadHtml(this.path+this.options.style+"/view/docList.html",
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
        this.fieldSelectedArea.loadHtml(this.path+this.options.style+"/view/fieldSelect.html",
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
        this.conditionArea.loadHtml(this.path+this.options.style+"/view/condition.html",
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
        this.defaultConditionArea.loadHtml(this.path+this.options.style+"/view/conditionItem_default.html",
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
                new MWF.xApplication.process.Application.Input(e.target, {
                    id: row.name,
                    onChange: function (data) {
                        // this.changeCustomConditionItemString(row, data);
                        setData(data);
                    }.bind(this)
                }, this.app);
                break;
            case "date":
                new MWF.xApplication.process.Application.DatePicker(e.target, {
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
                new MWF.xApplication.process.Application.NumberRange(e.target, {
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
        this.conditionSelectedArea.loadHtml(this.path+this.options.style+"/view/conditionSelected.html",
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

MWF.xApplication.process.Application.ElModule = new Class({
    Implements: [Events, Options],
    options:{
        id: ""
    },
    initialize: function(node, options, app, data){
        this.setOptions(options);
        this.container = $(node);
        this.data = data;
        this.app = app;
        this.load();
    },
    load: function(){
        this.container.appendHTML(this._createElementHtml());
        this.node = this.container.getFirst();
        this.node.addClass("o2_vue");

        if (!this.vm) this._loadVue(
            this._mountVueApp.bind(this)
        );
    },

    _loadVue: function(callback){
        if (!window.Vue){
            var vue = (o2.session.isDebugger) ? "vue_develop" : "vue";
            o2.loadAll({"css": "../o2_lib/vue/element/index.css", "js": [vue, "elementui"]}, { "sequence": true }, callback);
        }else{
            if (callback) callback();
        }
    },
    _mountVueApp: function(){
        if (!this.vueApp) this.vueApp = this._createVueExtend();
        this.vm = new Vue(this.vueApp);
        this.vm.$mount(this.node);
    },

    _createVueExtend: function(){
        var _self = this;
        var app = {
            data: this._createVueData(),
            mounted: function(){
                _self._afterMounted(this.$el);
            }
        };
        return app;
    },
    _createElementHtml: function(){

    },
    _createVueData: function(){

    },

    _afterMounted: function(el){
        this.node = el;
        // this.node.set({
        //     "id": this.json.id
        // });
        // this._loadVueCss();
        this.fireEvent("postLoad");
        this.fireEvent("load");
    },
    // _loadVueCss: function(){
    //     if (this.styleNode){
    //         this.node.removeClass(this.styleNode.get("id"));
    //     }
    //     if (this.json.vueCss && this.json.vueCss.code){
    //         this.styleNode = this.node.getParent().loadCssText(this.json.vueCss.code, {"notInject": true});
    //         this.styleNode.inject(this.node.getParent(), "before");
    //     }
    // },
});

MWF.xApplication.process.Application.Input = new Class({
    Extends: MWF.xApplication.process.Application.ElModule,
    options: {
        value: "",
        id: ""
    },
    _createVueData: function(){
        this.json = {
            data: this.data || this.options.value,
            readonly: false,
            disabled: false,
            clearable: true,
            editable: true,
            maxlength: "",
            minlength: "",
            showWordLimit: false,
            showPassword: false,
            size: "small",
            prefixIcon: "",
            suffixIcon: "",
            rows: 2,
            autosize: false,
            resize: "none",
            inputType: "text",
            description: "",
            click: function(){
                this.fireEvent("change", [this.json.data]);
            }.bind(this),
            change: function () {
                // debugger;
                this.fireEvent("change", [this.json.data]);
            }.bind(this),
            focus: function () {

            },
            blur: function () {

            },
            input: function () {

            },
            clear: function () {
                // debugger;
                this.fireEvent("change", [this.json.data]);
            }.bind(this)
        };
        // this.json[this.options.id] = this.data;
        return this.json;
    },
    _createElementHtml: function() {
        var html = "<div>";
        html += "<el-input";
        html += " v-model=\"data\"";
        html += " :maxlength=\"maxlength\"";
        html += " :minlength=\"minlength\"";
        html += " :show-word-limit=\"showWordLimit\"";
        html += " :show-password=\"showPassword\"";
        html += " :disabled=\"disabled\"";
        html += " :size=\"size\"";
        html += " :prefix-icon=\"prefixIcon\"";
        html += " :suffix-icon=\"suffixIcon\"";
        html += " :rows=\"rows\"";
        html += " :autosize=\"autosize\"";
        html += " :readonly=\"readonly\"";
        html += " :resize=\"resize\"";
        html += " :clearable=\"clearable\"";
        html += " :type=\"inputType\"";
        html += " :placeholder=\"description\"";
        html += " @change=\"change\"";
        html += " @focus=\"focus\"";
        html += " @blur=\"blur\"";
        html += " @input=\"input\"";
        html += " @clear=\"clear\"";

        // this.options.elEvents.forEach(function(k){
        //     html += " @"+k+"=\"$loadElEvent_"+k.camelCase()+"\"";
        // });

        // if (this.json.elProperties){
        //     Object.keys(this.json.elProperties).forEach(function(k){
        //         if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
        //     }, this);
        // }
        //
        // if (this.json.elStyles) html += " :style=\"elStyles\"";

        html += ">";

        // html += "<el-button slot=\"append\">"+this.app.lp.ok+"</el-button>"

        // if (this.json.vueSlot) html += this.json.vueSlot;

        html += "</el-input>";
        html += "</div>";
        return html;
    }
});

MWF.xApplication.process.Application.DatePicker = new Class({
    Extends: MWF.xApplication.process.Application.ElModule,
    options: {
        start: "",
        end: "",
        id: ""
    },
    _createVueData: function(){
        if(this.options.start)this.startDate = new Date(this.options.start);
        if(this.options.end)this.endDate = new Date(this.options.end);
        this.json = {
            data: this.data || [this.options.start, this.options.end],
            isReadonly: false,
            selectType: "datetimerange",
            disabled: false,
            clearable: true,
            editable: true,
            size: "small",
            prefixIcon: "",
            rangeSeparator: this.app.lp.rangeSeparator,
            startPlaceholder: this.app.lp.startPlaceholder,
            endPlaceholder: this.app.lp.endPlaceholder,
            description: "",
            arrowControl: true,
            format: "yyyy-MM-dd HH:mm:ss",
            pickerOptions: {
                firstDayOfWeek: 1,
                disabledDate: function(date){
                    if( this.startDate && date < this.startDate )return true;
                    if( this.endDate && date > this.endDate )return true;
                    return false;
                }.bind(this)
            },
            change: function () {
                this.fireEvent("change", [this.json.data]);
            }.bind(this),
            focus: function () {

            },
            blur: function () {

            }
        };
        // this.json[this.options.id] = this.data;
        return this.json;
    },
    _createElementHtml: function() {
        var html = "<el-date-picker";
        html += " v-model=\"data\"";
        html += " :type=\"selectType\"";
        html += " :readonly=\"isReadonly\"";
        html += " :disabled=\"disabled\"";
        html += " :editable=\"editable\"";
        html += " :clearable=\"clearable\"";
        html += " :size=\"size\"";
        html += " :prefix-icon=\"prefixIcon\"";
        html += " :range-separator=\"rangeSeparator\"";
        html += " :start-placeholder=\"startPlaceholder\"";
        html += " :end-placeholder=\"endPlaceholder\"";
        html += " :value-format=\"format\"";
        html += " :format=\"format\"";
        html += " :picker-options=\"pickerOptions\"";
        html += " :arrow-control=\"arrowControl\"";
        html += " @change=\"change\"";
        html += " @focus=\"focus\"";
        html += " @blur=\"blur\"";
        html += ">";
        html += "</el-date-picker>";
        return html;
    }
});

MWF.xApplication.process.Application.NumberRange = new Class({
    Extends: MWF.xApplication.process.Application.ElModule,
    options: {
        max: "100",
        min: "1",
        id: ""
    },
    checkNumber: function(num, type){
        debugger;
        var str = num;
        var len1 = str.substr(0, 1);
        var len2 = str.substr(1, 1);
        //如果第一位是0，第二位不是点，就用数字把点替换掉
        if (str.length > 1 && len1 === 0 && len2 !== ".") str = str.substr(1, 1);
        //第一位不能是.
        if (len1 === ".") str = "";
        //限制只能输入一个小数点
        if (str.indexOf(".") !== -1) {
            var str_ = str.substr(str.indexOf(".") + 1);
            if (str_.indexOf(".") !== -1) {
                str = str.substr(0, str.indexOf(".") + str_.indexOf(".") + 1);
            }
        }
        //正则替换
        str = str.replace(/[^\d^\.]+/g, '') // 保留数字和小数点
        return str
    },
    _createVueData: function(){
        this.data = {};
        this.json = {
            minData: this.options.min,
            maxData: this.options.max,
            min: {
                clearable: false,
                // click: function(){
                //     this.fireEvent("change", [this.json.data]);
                // }.bind(this),
                change: function () {
                    if( this.json.minData ){
                        var minData = (this.json.minData || 0).toFloat();
                        if( this.json.maxData ){
                            var maxData = (this.json.maxData || 0).toFloat();
                            if( minData > maxData ){
                                this.app.notice( this.app.lp.minGreatThanMaxError, "info", this.container);
                                this.json.maxData = this.json.minData;
                            }
                        }
                        if( minData < this.options.min.toFloat() ){
                            this.app.notice( this.app.lp.tooMinError, "info", this.container);
                            this.json.minData = this.options.min.toString();
                            return;
                        }
                        if( minData > this.options.max.toFloat() ){
                            this.app.notice( this.app.lp.tooMaxError, "info", this.container);
                            this.json.minData = this.options.max.toString();
                            return;
                        }
                    }
                    this.change();
                }.bind(this),
                focus: function () {},
                blur: function () {},
                oninput: function ( num ) {
                    return this.checkNumber(num, "min")
                }.bind(this),
                clear: function () {
                    this.change();
                }.bind(this)
            },
            max: {
                clearable: false,
                // click: function(){
                //     this.fireEvent("change", [this.json.data]);
                // }.bind(this),
                change: function () {
                    if( this.json.maxData ){
                        var maxData = (this.json.maxData || 0).toFloat();
                        if( this.json.minData ){
                            var minData = (this.json.minData || 0).toFloat();
                            if( minData > maxData ){
                                this.app.notice( this.app.lp.minGreatThanMaxError, "info", this.container);
                                this.json.minData = this.json.maxData;
                            }
                        }
                        if( maxData < this.options.min.toFloat() ){
                            this.app.notice(this.app.lp.tooMinError, "info", this.container);
                            this.json.maxData = this.options.min.toString();
                            return;
                        }
                        if( maxData > this.options.max.toFloat() ){
                            this.app.notice(this.app.lp.tooMaxError, "info", this.container);
                            this.json.maxData = this.options.max.toString();
                            return;
                        }
                    }
                    this.change();
                }.bind(this),
                focus: function () {},
                blur: function () {},
                oninput: function ( num ) {
                    return this.checkNumber(num, "max")
                }.bind(this),
                clear: function () {
                    this.change();
                }.bind(this)
            }
        };
        // this.json[this.options.id] = this.data;
        return this.json;
    },
    change: function(){
        debugger;
        var minData = this.json.minData;
        if( !minData )minData = this.options.min;
        var maxData = this.json.maxData;
        if( !maxData )maxData = this.options.max;
        this.fireEvent("change", [[minData.toFloat(), maxData.toFloat()]]);
    },
    _createElementHtml: function() {
        var html = "<div>";
        html += "<span style='color:#999;padding-right: 5px;'>"+this.app.lp.min+":"+this.options.min+"</span>";
        html += "<el-input style=\"width:200px;\"";
        html += " v-model=\"minData\"";
        html += " size=small";
        html += " :clearable=\"min.clearable\"";
        html += " type=text";
        html += " placeholder="+this.app.lp.fromValue;
        html += " @change=\"min.change\"";
        html += " @focus=\"min.focus\"";
        html += " @blur=\"min.blur\"";
        html += " @input=\"min.input\"";
        html += " @clear=\"min.clear\"";
        html += "@keyup.native=\"minData = min.oninput(minData)\"";
        html += ">";
        html += "</el-input>";
        html += "<span style='padding: 0px 5px;'>"+this.app.lp.to+"</span>";
        html += "<el-input style=\"width:200px;\"";
        html += " v-model=\"maxData\"";
        html += " size=small";
        html += " :clearable=\"max.clearable\"";
        html += " type=text";
        html += " placeholder="+this.app.lp.toValue;
        html += " @change=\"max.change\"";
        html += " @focus=\"max.focus\"";
        html += " @blur=\"max.blur\"";
        html += " @input=\"max.input\"";
        html += " @clear=\"max.clear\"";
        html += "@keyup.native=\"maxData = max.oninput(maxData)\"";
        html += ">";
        html += "</el-input>";
        html += "<span style='color:#999;padding-left: 5px;'>"+this.app.lp.max+":"+this.options.max+"</span>";
        html += "</div>";
        return html;
    }
})