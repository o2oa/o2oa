MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xApplication.Attendance.AbnormalExport = new Class({
    Extends: MWF.xApplication.Attendance.Explorer,
    Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Attendance/$AbnormalExport/";
        this.cssPath = "/x_component_Attendance/$AbnormalExport/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.initData();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    load: function(){
        this.loadToolbar();
        this.loadFilter();
        this.loadContentNode();

        //this.loadView( filterData );
        this.setNodeScroll();

    },
    loadFilter : function(){
        this.fileterNode = new Element("div.fileterNode", {
            "styles" : this.css.fileterNode
        }).inject(this.node);

        var table = new Element("table", {
            "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.filterTable, "class" : "filterTable"
        }).inject( this.fileterNode );
        var tr = new Element("tr").inject(table);


        this.createYearSelectTd( tr );
        this.createMonthSelectTd( tr );
        this.createActionTd( tr );
    },
    createYearSelectTd : function( tr ){
        var _self = this;
        var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "年度"  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
        this.yearString = new MDomItem( td, {
            "name" : "yearString",
            "type" : "select",
            "selectValue" : function(){
                var years = [];
                var year = new Date().getFullYear();
                for(var i=0; i<6; i++ ){
                    years.push( year-- );
                }
                return years;
            }
        }, true, this.app );
        this.yearString.load();
    },
    createMonthSelectTd : function( tr ){
        var _self = this;
        var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "月份"  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
        this.monthString = new MDomItem( td, {
            "name" : "monthString",
            "type" : "select",
            "selectValue" :["01","02","03","04","05","06","07","08","09","10","11","12"]
        }, true, this.app );
        this.monthString.load();
    },
    createActionTd : function( tr ){
        var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
        var input = new Element("button",{
            "text" : "导出",
            "styles" : this.css.filterButton
        }).inject(td);
        input.addEvent("click", function(){
            this.export(this.yearString.getValue(), this.monthString.getValue() );
            //this.loadView( filterData );
        }.bind(this))
    },
    setContentSize: function(){
        var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : {"x":0,"y":0};
        var titlebarSize = this.app.titleBar ? this.app.titleBar.getSize() : {"x":0,"y":0};
        var filterSize = this.fileterNode ? this.fileterNode.getSize() : {"x":0,"y":0};
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();
        //var filterSize = this.filterNode.getSize();
        var filterConditionSize = this.filterConditionNode ? this.filterConditionNode.getSize() : {"x":0,"y":0};

        var height = nodeSize.y-toolbarSize.y-pt-pb-filterConditionSize.y-titlebarSize.y-filterSize.y;
        this.elementContentNode.setStyle("height", ""+height+"px");

        this.pageCount = (height/30).toInt()+5;

        if (this.view && this.view.items.length<this.pageCount){
            this.view.loadElementList(this.pageCount-this.view.items.length);
        }
    },
    export : function(year, month){
        this.actions.exportAbnormalAttachment(year,month);
    },
    loadView : function( filterData ){
        //this.elementContentNode.empty();
        //this.view = new MWF.xApplication.Attendance.AbnormalExport.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        //this.view.filterData = filterData;
        //this.view.load();
        //this.setContentSize();
    },
    createDocument: function(){
        //if(this.view)this.view._createDocument();
    }
});

MWF.xApplication.Attendance.AbnormalExport.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        //return new MWF.xApplication.Attendance.AbnormalExport.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        //if(!count )count=20;
        //var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        //var filter = this.filterData || {};
        //filter.empName = layout.desktop.session.user.name;
        //this.actions.listAppealFilterNext(id, count, filter, function(json){
        //    var data = json.data;
        //    data.sort( function( a, b ){
        //        return parseInt( b.appealDateString.replace(/-/g,"") ) -  parseInt( a.appealDateString.replace(/-/g,"") );
        //    })
        //    json.data = data;
        //    if (callback) callback(json);
        //});
    },
    _removeDocument: function(documentData, all){

    },
    _createDocument: function(){

    },
    _openDocument: function( documentData ){

    }

});

MWF.xApplication.Attendance.AbnormalExport.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document,
    agree : function(){

    },
    deny : function(){

    }

});

