MWF.require("o2.widget.Paging", null, false);
// MWF.require("o2.widget.Mask", null, false);
o2.requireApp("Template", "MTooltips", null, false);
MWF.xApplication.ftsearch.FTSearchView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "query": ""
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_ftsearch/$FTSearchView/";

        this.app = app;
        this.container = $(node);
        this.facetOrderList = ["category","applicationName", "processName","appName","categoryName"];
        this.load();
    },
    load: function(){
        this.pageSize = 10;
        this.currentKey = "";

        this.docPageNum = 1;
        this.docTotal = 0;
        this.docList = [];
        this.selectedConditionList = [];
        this.selectedFilterList = [];

        var url = this.path+this.options.style+"/view.html";
        this.container.loadHtml(url, {
            "bind": {"lp": this.app.lp, "data": {"query":this.options.query}},
            "module": this
         }, function(){
            // this.loadSelectedCondition();
            this.search(null, this.options.query);
        }.bind(this));

        // this.resizeFun = this.checkAllSwitchButton.bind(this);
        // this.app.addEvent("resize", this.resizeFun);

        this.setSizeFun = this.setSize.bind(this);
        this.app.addEvent("resize", this.setSizeFun);
    },
    recordStatus: function(){
        return {
            // view: "ftsearch",
            query: this.getQuery()
        }
    },
    reload: function(){
        this.container.empty();
        if(this.resizeFun){
            this.app.removeEvent("resize", this.resizeFun);
        }
        this.load();
    },
    destroy : function(){
        // this.app.removeEvent("resize", this.resetNodeSizeFun );
        this.container.empty();
        if(this.resizeFun){
            this.app.removeEvent("resize", this.resizeFun);
        }
    },
    getQuery: function(){
        return this.searchInput.get("value") || "";
    },
    gotoMainPage: function(){
        this.app.gotoMainPage();
    },
    selectTab: function(){
        this.app.selectTab();
    },
    searchKeydown: function(e){
        if( e.keyCode === 13 ){
            this.search();
        }
    },
    setSize: function(){
        var contentY = this.app.content.getSize().y - 10;

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
     search: function(pageNum, query){
        this.selectedConditionList = [];
        this.selectedFilterList = [];
        if( typeOf(this.collapseCondition) === "boolean" ){
            this._search(pageNum, query, function () {
                // this.loadSelectedCondition();
            }.bind(this));
        }else{
            MWF.UD.getDataJson("ftsearchCollapseCondition", function (json) {
                this.collapseCondition = json === "true" || !json ;
                this._search(pageNum, query, function () {
                    // this.loadSelectedCondition();
                }.bind(this));
            }.bind(this), true);
        }
    },
    _search: function( pageNum, query, callback ){
        // this.mask = new o2.widget.Mask({ "style": "desktop", "zIndex": 50000 });
        // this.mask.loadNode(this.app.content);

        pageNum = o2.typeOf(pageNum) === "number" ? pageNum : null;
        this.docPageNum = pageNum || 1;
        this.currentKey = query || this.searchInput.get("value") || "";

        var docLoaded = false;
        var filterLoaded = false;
        var afterLoadFun = function () {
            debugger;
            if(docLoaded && filterLoaded){
                this.setSize();
                if(!pageNum){
                    if( this.docTotal ){
                        var endDate = new Date();
                        var t = endDate.getTime()-startDate.getTime();
                        t = ((t/1000)*100).toInt()/100;
                        var text = this.app.lp.docTotalInfor.replace("{count}", this.docTotal||0).replace("{time}", t);
                        // this.docTotalNode.set("html", text);
                        this.loadDocPagination( text );
                    }else{

                        this.docPaginationNode.empty();
                    }
                }
                if( typeOf(callback) === "function" )callback();
            }
            // if (this.mask) { this.mask.hide(); this.mask = null; }
        }.bind(this);

        if( this.currentKey ){

            var startDate = new Date();

            // var filterList = this.selectedConditionList.map(function(cond){
            //     return {
            //         field: cond.field,
            //         valueList: cond.valueList
            //     };
            // });

            var filterList = this.selectedFilterList.map(function(cond){
                return {
                    field: cond.field,
                    valueList: cond.valueList
                };
            });

            o2.Actions.load("x_query_assemble_surface").SearchAction.post({
                query: this.currentKey || "",
                page:  this.docPageNum,
                size: this.pageSize,
                filterList: filterList
            }).then(function(json){
                var sequence = (this.docPageNum - 1) * this.pageSize;
                this.data = json.data;
                this.docList = json.data.documentList.map(function (d) {
                    sequence++;
                    d.sequence = sequence;
                    return d;
                });

                this.docTotal =  json.data.count;
                if( o2.typeOf(this.docTotal) !== "number" )this.docTotal = 0;

                this.loadDocList(this.docList, function () {
                    docLoaded = true;
                    afterLoadFun();
                });

                var facetList = this.orderFacet(json.data.facetList);

                this.loadFilter(facetList, function () {
                    filterLoaded = true;
                    afterLoadFun();
                });

                // this.loadCondition( facetList);

                // if(!pageNum){
                //     var endDate = new Date();
                //     var t = endDate.getTime()-startDate.getTime();
                //     t = ((t/1000)*100).toInt()/100;
                //     var text = this.app.lp.docTotalInfor.replace("{count}", this.docTotal||0).replace("{time}", t);
                //     this.docTotalNode.set("html", text);
                //
                //     this.loadDocPagination();
                // }

                // if( typeOf(callback) === "function" )callback();

            }.bind(this), function () {
                this.data = {};
                this.docList = [];
                this.docTotal = 0;
                this.loadFilter([], function () {
                    filterLoaded = true;
                    afterLoadFun();
                });
                this.loadDocList(null, function () {
                    docLoaded = true;
                    afterLoadFun();
                });
            }.bind(this));
        }else{
            this.data = {};
            this.docList = [];
            this.docTotal = 0;
            this.loadFilter([], function () {
                filterLoaded = true;
                afterLoadFun();
            });
            this.loadDocList(null, function () {
                docLoaded = true;
                afterLoadFun();
            });
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

    setBody: function(ev, d){
        // var body;
        // var regex = /(<([^>]+)>)/ig;
        // if(d.highlighting ){
        //     body = o2.typeOf( d.highlighting ) === "array" ? d.highlighting[0] : d.highlighting;
        // }
        // if( !body && d._summary_){
        //     body = o2.typeOf( d._summary_ ) === "array" ? d._summary_.join("") : d._summary_;
        // }
        ev.target.set("html", d.highlighting || d.summary || "")
    },
    setSummary: function(ev, d){
        var body;
        if( !body && d._summary_){
            body = o2.typeOf( d._summary_ ) === "array" ? d._summary_.join("") : d._summary_;
        }
        if( body ){
            ev.target.set("text", body)
        }else{
            ev.target.getParent().hide();
        }
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
        f( !o2.api ){
            MWF.require("MWF.framework", function () {
                o2.api.page.openDocument(id);
            }.bind(this));
        }else{
            o2.api.page.openDocument(id);
        }
    },
    loadDocList: function(data, callback){
        this.docListNode.empty();
        this.docListNode.loadHtml(this.path+this.options.style+"/docList.html",
            {
                "bind": {"lp": this.app.lp, "data": data},
                "module": this,
                "reload": true
            },
            function(){
                if(callback)callback();
            }.bind(this)
        );
    },
    loadDocPagination: function(text){
        this.docPaginationNode.empty();
        if( o2.typeOf(this.docTotal) === "number" && this.docTotal > 0 ){
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
    loadCondition: function( json ){
        var lp = this.app.lp;
        json.each(function(d){
            d.label = lp[d.field.toString()] || d.field;
            d.valueCountPairList.each(function (v) {
                v.field = d.field;
                v.parentLabel = d.label;
                if( ["category","completed"].contains(d.field)){
                    v.label = lp[v.value.toString()] || v.value;
                }else{
                    v.label = v.value;
                }
            });
        });
        this.conditionArea.empty();
        json = json.filter(function(d){
            return d.valueCountPairList && d.valueCountPairList.length;
        });
        json.each(function(d, i){
            d.index = i;
        });
        this.conditionArea.loadHtml(this.path+this.options.style+"/condition.html",
            {
                "bind": {"lp": this.app.lp, "data": json, status: {
                        collapseCondition: this.collapseCondition,
                        showSwitch: json.length > 1
                    }},
                "module": this,
                "reload": true
            },
            function(){

            }.bind(this)
        );
        // }.bind(this));
    },
    switchCondition: function(){
        this.collapseCondition = !this.collapseCondition;
        if( this.collapseCondition ){
            this.switchNode.getElement("i").addClass( 'o2icon-chevron-thin-down' ).removeClass('o2icon-chevron-thin-up');
            this.switchNode.getElement("span").set("text", this.app.lp.expandCondition);
            this.conditionNode.getElements(".itemWrap").each(function (item, index) {
                item.setStyle("display", index ? "none": "")
            })
        }else{
            this.switchNode.getElement("i").removeClass( 'o2icon-chevron-thin-down' ).addClass('o2icon-chevron-thin-up');
            this.switchNode.getElement("span").set("text", this.app.lp.collapseCondition);
            this.conditionNode.getElements(".itemWrap").each(function (item, index) {
                item.setStyle("display", "");
            })
        }
        MWF.UD.putData("ftsearchCollapseCondition", this.collapseCondition.toString(), null,);
    },
    loadSelectedCondition: function(){
        this.conditionSelectedArea.empty();
        this.selectedConditionList.each(function (item) {
            var condNode = new Element("div.item", {
                    events: {
                        mouseover: function (ev) {
                            this.conditionSelectedOver(ev)
                        }.bind(this),
                        mouseout: function (ev) {
                            this.conditionSelectedOut(ev)
                        }.bind(this)
                    }
            }).inject( this.conditionSelectedArea );
            condNode.addClass("mainColor_bg");
            condNode.addEvents({
                click: function (ev) {
                    this.removeSelectedConditionItem(ev, item, condNode)
                }.bind(this),
            });

            new Element("span", {
                text: item.parentLabel+":"
            }).inject(condNode);

            var titleList = [];
            item.labelList.each(function (label, i) {
                if( i === 2 ){
                    new Element("div.item-text", {
                        text: "..."
                    }).inject(condNode);
                }else if( i <= 1 ){
                    new Element("div.item-text", {
                        text: label
                    }).inject(condNode);
                }
                titleList.push( label );
            });

            var iconNode = new Element("i.o2icon-close").inject(condNode);
            iconNode.addClass("icon");

            condNode.set("title", item.parentLabel+":" + titleList.join(","));
        }.bind(this))
    },
    // loadSelectedCondition: function(){
    //     this.conditionSelectedArea.empty();
    //     this.conditionSelectedArea.loadHtml(this.path+this.options.style+"/conditionSelected.html",
    //         {
    //             "bind": {"lp": this.app.lp, "data": this.selectedConditionList},
    //             "module": this,
    //             "reload": true
    //         },
    //         function(){
    //
    //         }.bind(this)
    //     );
    // },

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

    loadMultiSelectConditionItem: function(e, row){
        if( this.curMultiSelectNode ){
            this.curMultiSelectNode.empty();
            this.curMultiSelectNode.hide();
            this.curMultiSelectItemNode.show();
        }
        this.curMultiSelectConditionList = [];
        this.curMultiSelectItemNode = e.target.getParent(".item");
        this.curMultiSelectItemNode.hide();
        this.curMultiSelectNode = this.curMultiSelectItemNode.getParent().getElement(".item-multiselect").show();
        this.curMultiSelectNode.loadHtml(this.path+this.options.style+"/conditionItem_multiselect.html",
            {
                "bind": {"lp": this.app.lp, "data": row},
                "module": this,
                "reload": true
            },
            function(){}.bind(this)
        );
    },
    multiSelect: function(e, item){
        var target = this.app.getEventTarget(e, "subItem-content");
        var index = -1;
        this.curMultiSelectConditionList.each(function (cond, idx) {
            if( cond.field === item.field && cond.value === item.value )index = idx;
        });
        if( index > -1 ){ //已经选择了
            target.getElement("i.checkbox").removeClass("o2icon-check_box").addClass("o2icon-check_box_outline_blank").removeClass("mainColor_color");
            this.curMultiSelectConditionList.splice(index, 1);
        }else{ //还没有选择
            target.getElement("i.checkbox").removeClass("o2icon-check_box_outline_blank").addClass("o2icon-check_box").addClass("mainColor_color");
            this.curMultiSelectConditionList.push(item);
        }
    },
    okMultiSelect: function(e, item){
        if(!this.curMultiSelectConditionList.length){
            this.app.notice(this.app.lp.selectConditionNote, "info");
            return;
        }
        var object = {
            field: this.curMultiSelectConditionList[0].field,
            parentLabel: this.curMultiSelectConditionList[0].parentLabel,
            valueList: [],
            labelList: []
        };
        this.curMultiSelectConditionList.each(function(cond){
            object.valueList.push(cond.value);
            object.labelList.push(cond.label);
        })

        this.changeCondition(e, object)
    },
    cancelMultiSelect: function(e, item){
        if( this.curMultiSelectNode ){
            this.curMultiSelectItemNode.show();
            this.curMultiSelectNode.empty();
            this.curMultiSelectNode.hide();
        }
    },

    checkAllSwitchButton: function(e){
        if(this.conditionNode)this.conditionNode.getElements(".item").each(function(itemNode){
            var more = itemNode.getElement(".switch-button");
            var ul = itemNode.getElement(".subitem-wrap");
            if( 41 < ul.scrollHeight ){
                more.show();
            }else{
                more.hide();
            }
        });
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
    changeCondition: function(e, item, removedItemNode){
        var index = -1;
        this.selectedConditionList.each(function (cond, idx) {
            if( cond.field === item.field )index = idx;
        });
        if( index > -1 ) { //已经选择了
            this.selectedConditionList.splice(index, 1);
        }else{
            this.selectedConditionList.push(item);
        }
        this._search( null, null,function () {
            this.loadSelectedCondition()
        }.bind(this));
    },
    changeSingleCondition: function(e, item){
        var itemIndex = -1, valueIndex = -1;
        this.selectedConditionList.each(function (cond, idx) {
            if( cond.field === item.field ){
                itemIndex = idx;
                valueIndex = cond.valueList.indexOf(item.value);
            }
        });
        if( valueIndex > -1 ){ //已经选择了
            e.target.removeClass('mainColor_bg');
            e.target.store("selected", false);
            this.selectedConditionList[itemIndex].valueList.splice(valueIndex, 1);
            this.selectedConditionList[itemIndex].labelList.splice(valueIndex, 1);
            if( !this.selectedConditionList[itemIndex].valueList.length ){
                this.selectedConditionList.splice(itemIndex, 1);
            }
        }else{ //还没有选择
            e.target.removeClass('mainColor_bg_opacity');
            e.target.addClass('mainColor_bg');
            e.target.store("selected", true);
            if( itemIndex > -1 ){
                this.selectedConditionList[itemIndex].valueList.push(item.value);
                this.selectedConditionList[itemIndex].labelList.push(item.label);
            }else{
                this.selectedConditionList.push({
                    field: item.field,
                    parentLabel: item.parentLabel,
                    valueList: [item.value],
                    labelList: [item.label]
                });
            }
        }
        this._search(null, null, function () {
            // this.loadSelectedCondition();
        }.bind(this));
    },
    subConditionOver: function(e){
        if(!e.target.retrieve("selected"))e.target.addClass('mainColor_bg_opacity');
    },
    subConditionOut: function(e){
        if(!e.target.retrieve("selected"))e.target.removeClass('mainColor_bg_opacity');
    },
    subConditionOver_multi: function(e){
        var target = this.app.getEventTarget(e, "subItem-content");
        if(!target.retrieve("selected"))target.addClass('mainColor_bg_opacity');
    },
    subConditionOut_multi: function(e){
        var target = this.app.getEventTarget(e, "subItem-content");
        if(!target.retrieve("selected"))target.removeClass('mainColor_bg_opacity');
    },
    removeSelectedConditionItem: function(e, item, itemNode){
        this.changeCondition(e, item, itemNode);
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
    },


    loadFilter: function( json, callback ){

        var lp = this.app.lp;
        this.filterArea.empty();
        if( this.tooltipList && this.tooltipList.length ){
            this.tooltipList.each(function (tooltip) {
                tooltip.destroy();
            });
            this.tooltipList = [];
            this.currentTooltip = null;
        }
        var index = 0;
        var facetList = [];
        json.each(function(d) {
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
            this.filterArea.removeClass("index-filterNoDoc");
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
        }else{
            this.filterArea.addClass("index-filterNoDoc")
            if(callback)callback();
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
        var index = -1;
        this.selectedFilterList.each(function (f, idx) {
            if( f.field === item.field )index = idx;
        });
        if( index > -1 ) { //已经选择了
            this.selectedFilterList.splice(index, 1);
        }
        this._search();
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
        var tooltip = new MWF.xApplication.ftsearch.FTSearchView.ConditionTooltip(this.app.appNode, target, this.app, row, {
            axis : "y",
            hasArrow: false,
            hiddenDelay : 300,
            displayDelay : 100,
            offset : {
                x : 0,
                y : -2
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
                var width = Math.min(this.app.appNode.getSize().x - 80, 1200);
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
                target.addClass("filterItem-content-over").removeClass("filterItem-content").addClass("mainColor_border");
                target.setStyle("border-bottom", "0px");
                this.currentTooltip = tooltip;
                // if( tooltip.input )tooltip.input.focus();
                this.loadCurrentFilterItem(tooltip.contentNode, row);
            }.bind(this),
            onQueryCreate : function(){

            }.bind(this),
            onHide : function(){
                tooltip.node.removeClass("mainColor_border");
                arrowNode.addClass("o2icon-chevron-thin-down").removeClass("o2icon-chevron-thin-up");
                target.addClass("filterItem-content").removeClass("filterItem-content-over").removeClass("mainColor_border");
                target.setStyle("border-bottom", "1px solid #ccc");
                this.currentTooltip = null;
            }.bind(this),
            onSetCoondinates: function (obj) {
                obj.left = obj.left + 30
            }
        });
        this.tooltipList.push( tooltip );
    },
    loadCurrentFilterItem: function(node, row){
        node.empty();
        var itemWrap = new Element("div.filterItem-subitemWrap").inject(node);
        var array = this.data.facetList.filter(function (field) {
            return row.field === field.field
        });
        if( array.length > 0 ){
            var lp = this.app.lp;

            if( array[0].valueCountPairList.length > 1 ){
                var multiButton = new Element("div.filter-multi-button", {
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
                            this.selectedFilterList.push({
                                label: row.label,
                                field: row.field,
                                labelList: [v.label],
                                valueList: [v.value]
                            });
                            this._search();
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
        var array = this.data.facetList.filter(function (field) {
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
                    this._search();
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
    }

});

MWF.xApplication.ftsearch.FTSearchView.ConditionTooltip = new Class({
    Extends: MTooltips,
    _customNode: function(node, contentNode){
    },
    _loadCustom : function( callback ){
        if(callback)callback();
    }
});
