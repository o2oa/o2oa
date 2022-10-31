MWF.require("o2.widget.Paging", null, false);
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

        var url = this.path+this.options.style+"/view.html";
        this.container.loadHtml(url, {
            "bind": {"lp": this.app.lp, "data": {"query":this.options.query}},
            "module": this
         }, function(){
            this.loadSelectedCondition();
            this.search(null, this.options.query);
        }.bind(this));

        this.resizeFun = this.checkAllSwitchButton.bind(this);
        this.app.addEvent("resize", this.resizeFun);
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
    search: function(pageNum, query){
        this.selectedConditionList = [];
        this.loadSelectedCondition();
        this._search(pageNum, query);
    },
    _search: function( pageNum, query ){
        pageNum = o2.typeOf(pageNum) === "number" ? pageNum : null;
        this.docPageNum = pageNum || 1;
        this.currentKey = query || this.searchInput.get("value") || "";
        if( this.currentKey ){

            var startDate = new Date();

            var filterList = this.selectedConditionList.map(function(cond){
                return {
                    field: cond.field,
                    valueList: cond.valueList
                };
                // {
                //     field: cond.field,
                //     value: cond.value,
                //     start: xxx
                //     end: xxx
                // }
            });
            o2.Actions.load("x_custom_index_assemble_control").SearchAction.post({
                query: this.currentKey,
                page:  this.docPageNum,
                size: this.pageSize,
                filterList: filterList
            }).then(function(json){
                this.docList = json.data.documentList;
                this.docTotal =  json.data.count;

                this.loadDocList(this.docList);
                var facetList = this.orderFacet(json.data.facetList);
                this.loadCondition( facetList);

                if(!pageNum){
                    var endDate = new Date();
                    var t = endDate.getTime()-startDate.getTime();
                    t = ((t/1000)*100).toInt()/100;
                    var text = this.app.lp.docTotalInfor.replace("{count}", this.docTotal||0).replace("{time}", t);
                    this.docTotalNode.set("html", text);

                    this.loadDocPagination();
                }
            }.bind(this));
        }else{
            this.docList = [];
            this.docTotal = 0;
            this.docTotalNode.set("text", "");
            this.loadSelectedCondition();
            this.loadCondition( []);
            this.loadDocList();
            this.loadDocPagination();

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
        ev.target.set("html", d.highlighting || "")
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
    openWork: function(id, event, row){
        o2.api.page.openWork(id);
    },
    openDoc: function(id, event, row){
        debugger;
        o2.api.page.openDocument(id);
    },
    loadDocList: function(data){
        this.docListNode.empty();
        this.docListNode.loadHtml(this.path+this.options.style+"/docList.html",
            {
                "bind": {"lp": this.app.lp, "data": data},
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
    loadCondition: function( json ){
        var lp = this.app.lp;
        json.each(function(d){
            d.label = lp[d.field] || d.field;
            d.valueCountPairList.each(function (v) {
                v.field = d.field;
                v.parentLabel = d.label;
                if( d.field === "category" ){
                    v.label = lp[v.value] || v.value;
                }else{
                    v.label = v.value;
                }
            });
        });
        this.conditionArea.empty();
        json = json.filter(function(d){
            return d.valueCountPairList && d.valueCountPairList.length;
        });
        this.conditionArea.loadHtml(this.path+this.options.style+"/condition.html",
            {
                "bind": {"lp": this.app.lp, "data": json},
                "module": this,
                "reload": true
            },
            function(){

            }.bind(this)
        );
        // }.bind(this));
    },
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
        this._search();
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
            this.loadSelectedCondition();
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
            this.loadSelectedCondition();
        }
        this._search();
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