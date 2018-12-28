MWF.xApplication.Report = MWF.xApplication.Report || {};
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MSelector", null, false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.Report.StatisticsView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp;
        this.path = "/x_component_Report/$StatisticsView/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        this.createMiddleContent();
        //this.app.addEvent("resize", function(){
        //    this.resizeContent();
        //}.bind(this));
    },
    reload:function(year){
        this.currentYear = year;
        this.createMiddleContent();
    },
    createMiddleContent : function(){
        this.node.empty();
        this.middleContent = new Element("div.middleContent",{"styles":this.css.middleContent}).inject(this.node);
        this.viewNode = new Element("div.viewNode" ).inject(this.node);
        this.createNavi();
    },
    createNavi: function(){
        this.naviContent = new Element("div.naviContent",{"styles":this.css.naviContent}).inject(this.middleContent);

        this.naviRightContent = new Element("div.naviRightContent",{"styles":this.css.naviRightContent}).inject(this.middleContent);

        this.keyWorkNavi = new Element("div.naviNode",{
            "styles":this.css.naviNode,
            "text" : "按公司工作重点"
        }).inject(this.naviContent).addEvent("click", function() {
                this.departmentNavi.setStyles( this.css.naviNode );
                this.keyWorkNavi.setStyles( this.css.naviNode_current );
                this.currentType = "keywork";
            }.bind(this));

        this.departmentNavi = new Element("div.naviNode",{
            "styles":this.css.naviNode,
            "text" : "按责任部门"
        }).inject(this.naviContent).addEvent("click", function() {
                this.departmentNavi.setStyles( this.css.naviNode_current );
                this.keyWorkNavi.setStyles( this.css.naviNode );
                this.currentType = "department";
            }.bind(this));

        this.keyWorkNavi.click();
        this.loadFilter();

        new Element("button.action",{
            "styles":this.css.action,
            "text" : "统计"
        }).inject(this.naviRightContent).addEvent("click", function() {
                var filterData = {
                    year : this.yearSelector.getValue(),
                    month : this.monthSelector.getValue()
                };
                if( this.currentType == "department" ){
                    this.actions.statByUnit( year, function(){} );
                }else{
                    this.actions.statByKeyWork( year, function(){} );
                }
            }.bind(this));
    },
    loadFilter: function(options, callback){
        this.yearSelectorArea = new Element("div",{ styles : this.css.yearSelectorArea }).inject(this.naviRightContent);
        this.yearSelector = new MWF.xApplication.Report.StatisticsView.YearSelect(this.yearSelectorArea ,{}, this.app );
        this.yearSelector.load();

        this.monthSelectorArea = new Element("div",{ styles : this.css.yearSelectorArea }).inject(this.naviRightContent);
        this.monthSelector = new MWF.xApplication.Report.StatisticsView.MonthSelect(this.monthSelectorArea ,{}, this.app );
        this.monthSelector.load();
    },
    destroy : function(){
        this.node.empty();
    }

});

MWF.xApplication.Report.StatisticsView.YearSelect = new Class({
    Extends: MSelector,
    options : {
        "style": "default",
        "width": "100px",
        "height": "30px",
        "textField" : "text",
        "valueField" : "value",
        "emptyOptionEnable" : false,
        "value" : (new Date()).getFullYear().toString()
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        var arr = [];
        var data = new Date();
        data.decrement("year",5);
        for( var i=0; i<11; i++ ){
            data.increment("year",1);
            arr.push({
                text : data.getFullYear()+ "年",
                value : data.getFullYear().toString()
            })
        }

        if(callback)callback( arr );
    }
});

MWF.xApplication.Report.StatisticsView.MonthSelect = new Class({
    Extends: MSelector,
    options : {
        "style": "default",
        "width": "100px",
        "height": "30px",
        "textField" : "text",
        "valueField" : "value",
        "emptyOptionEnable" : false,
        "value" : (new Date().get("month")+1).toString()
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        var arr = [];
        [1,2,3,4,5,6,7,8,9,10,11,12].each( function( i ){
            arr.push({
                text : i + "月",
                value : i.toString()
            })
        });
        if(callback)callback( arr );
    }
});
