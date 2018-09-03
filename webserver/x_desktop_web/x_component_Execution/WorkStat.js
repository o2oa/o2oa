MWF.xApplication.Execution = MWF.xApplication.Execution || {};
MWF.xDesktop.requireApp("Template", "Explorer", null, false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.Execution.WorkStat = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.app = app;
        this.lp = app.lp.workStat;
        this.path = "/x_component_Execution/$WorkStat/";

        this.actions = actions;
        this.options.style = this.getViewStyle();
        this.setOptions(options);

        this.loadCss();
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_Execution/$WorkStat/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        this.middleContent = this.app.middleContent;
        //this.middleContent.setStyles({"margin-top":"0px","border":"0px solid #f00"});
        this.createNaviContent();
        //this.createContentDiv();


        this.resizeWindow();
        this.app.addEvent("resize", function(){
            this.resizeWindow();
        }.bind(this));
    },
    resizeWindow: function(){
        var size = this.app.middleContent.getSize();
        this.naviDiv.setStyles({"height":(size.y-40)+"px"});
        this.naviContentDiv.setStyles({"height":(size.y-180)+"px"});
        this.contentDiv.setStyles({"height":(size.y-60)+"px"});
        if(this.deptStatContent){
            this.deptStatContent.setStyles({"height":(size.y-130)+"px"})
        }
        if(this.statViewListDiv){
            var y = this.contentDiv.getSize().y - this.dateToolbar.getSize().y - this.dateStatListDiv.getSize().y;
            this.statViewListDiv.setStyles({"height":(y-40)+"px"})
        }

    },
    createNaviContent: function(){
        this.naviDiv = new Element("div.naviDiv",{
            "styles":this.css.naviDiv
        }).inject(this.middleContent);

        this.naviTitleDiv = new Element("div.naviTitleDiv",{
            "styles":this.css.naviTitleDiv,
            "text": this.lp.minderExplorerTitle
        }).inject(this.naviDiv);
        this.naviContentDiv = new Element("div.naviContentDiv",{"styles":this.css.naviContentDiv}).inject(this.naviDiv);
        this.naviBottomDiv = new Element("div.naviBottomDiv",{"styles":this.css.naviBottomDiv}).inject(this.naviDiv);

        this.createContentDiv();

        var jsonUrl = this.path+"navi.json";
        MWF.getJSON(jsonUrl, function(json){
            json.each(function(data, i){
                var naviContentLi = new Element("li.naviContentLi",{"styles":this.css.naviContentLi}).inject(this.naviContentDiv);
                naviContentLi.addEvents({
                    "mouseover" : function(ev){
                        if(this.bindObj.currentNaviItem != this.node)this.node.setStyles( this.styles )
                    }.bind({"styles": this.css.naviContentLi_over, "node":naviContentLi, "bindObj": this }) ,
                    "mouseout" : function(ev){
                        if(this.bindObj.currentNaviItem != this.node)this.node.setStyles( this.styles )
                    }.bind({"styles": this.css.naviContentLi, "node":naviContentLi, "bindObj": this }) ,
                    "click" : function(ev){
                        if( this.bindObj.currentNaviItem )this.bindObj.currentNaviItem.setStyles( this.bindObj.css.naviContentLi );
                        this.node.setStyles( this.styles );
                        this.bindObj.currentNaviItem = this.node;
                        if( this.action && this.bindObj[this.action] ){
                            this.bindObj[this.action]();
                        }
                    }.bind({"styles": this.css.naviContentLi_current, "node":naviContentLi, "bindObj": this, "action" : data.action })
                });
                var naviContentImg = new Element("img.naviContentImg",{
                    "styles":this.css.naviContentImg,
                    "src":this.path+"default/icon/"+data.icon
                }).inject(naviContentLi);
                var naviContentSpan = new Element("span.naviContentSpan",{
                    "styles":this.css.naviContentSpan,
                    "text":data.title
                }).inject(naviContentLi);
                if( i == 0 ){
                    naviContentLi.click();
                }
            }.bind(this));
        }.bind(this));
    },
    createContentDiv: function(){
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.middleContent);

    },
    //*************************中心工作开始**************************************
    openCenterWork: function(){
        this.contentDiv.empty();
        this.loadCategoryBar();
        this.loadToolbar();
        //this.loadView();
    },
    loadCategoryBar : function(){
        var _self = this;
        this.categoryBar = new Element("div.categoryBar",{"styles":this.css.categoryBar}).inject(this.contentDiv);

        this.allCategoryNode = new Element("li.allCategoryNode", {
            "styles": this.css.categoryNode,
            "text" : this.lp.category.all
        }).inject(this.categoryBar);
        this.allCategoryNode.addEvents({
            "mouseover" : function(){ if( this.currentCategoryNode != this.allCategoryNode)this.allCategoryNode.setStyles(this.css.categoryNode_over) }.bind(this),
            "mouseout" : function(){ if( this.currentCategoryNode != this.allCategoryNode)this.allCategoryNode.setStyles(this.css.categoryNode) }.bind(this),
            "click":function(){
                if( this.currentCategoryNode )this.currentCategoryNode.setStyles(this.css.categoryNode);
                this.currentCategoryNode = this.allCategoryNode;
                this.allCategoryNode.setStyles(this.css.categoryNode_current);
                this.loadView()
            }.bind(this)
        });
        this.actions.getCategoryCountAll( function( json ){
                json.data.each( function( d ){
                    var categoryNode = new Element("li.categoryNode", {
                        "styles": this.css.categoryNode,
                        "text" : d.workTypeName + "(" + d.centerCount +")"
                    }).inject(this.categoryBar);
                    categoryNode.store( "workTypeName" , d.workTypeName );
                    categoryNode.addEvents({
                        "mouseover" : function(){ if( _self.currentCategoryNode != this.node)this.node.setStyles(_self.css.categoryNode_over) }.bind({node : categoryNode }),
                        "mouseout" : function(){ if( _self.currentCategoryNode != this.node)this.node.setStyles(_self.css.categoryNode) }.bind({node : categoryNode }),
                        "click":function(){
                            if( _self.currentCategoryNode )_self.currentCategoryNode.setStyles(_self.css.categoryNode);
                            _self.currentCategoryNode = this.node;
                            this.node.setStyles(_self.css.categoryNode_current);
                            _self.loadView(  )
                        }.bind({ name : d.workTypeName, node : categoryNode })
                    })
                }.bind(this))
            }.bind(this), null, false
        );
        this.allCategoryNode.click();
    },
    loadToolbar: function(){
        this.toolbar = new Element("div.toolbar",{
            styles : this.css.toolbar
        }).inject(this.categoryBar);

        //this.toolbarTextNode = new Element("div",{
        //    styles : this.css.toolbarTextNode,
        //    text: this.lp.workTask.centerWork,
        //}).inject(this.toolbar);

        this.fileterNode = new Element("div.fileterNode",{
            styles : this.css.fileterNode
        }).inject(this.toolbar);

        this.loadFilter();
    },
    loadFilter: function () {
        var _self = this;
        var html = "<table bordr='0' cellpadding='5' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
                //"    <td styles='filterTableTitle' lable='year'></td>" +
                //"    <td styles='filterTableValue' item='year'></td>" +
                //"    <td styles='filterTableTitle' lable='workLevel'></td>" +
                //"    <td styles='filterTableValue' item='workLevel'></td>" +
                //"    <td styles='filterTableTitle' lable='workType'></td>" +
                //"    <td styles='filterTableValue' item='workType'></td>" +
                //"    <td styles='filterTableTitle' lable='star'></td>" +
                //"    <td styles='filterTableValue' item='star'></td>" +
            "    <td styles='filterTableValue' item='workTitle'></td>" +
            "    <td styles='filterTableValue' item='searchAction'></td>" +
            "    <td styles='filterTableValue' item='returnAction' style='display:none;'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.filter = new MForm(this.fileterNode, {}, {
                style: "execution",
                isEdited: true,
                itemTemplate: {
                    //year: {
                    //    "text": this.lp.yearCount +":", "type": "select", "className": "inputSelectUnformatWidth",
                    //    "selectValue": function () {
                    //        var years = [], year = new Date().getFullYear();
                    //        for (var i = 0; i < 6; i++) years.push(year--);
                    //        return years;
                    //    }
                    //},
                    //workLevel: {
                    //    "text": this.lp.level +":", "type": "select","className": "inputSelectUnformatWidth",
                    //    "selectValue": this.lp.workForm.workLevelValue.split(",")
                    //},
                    //workType: {
                    //    "text": this.lp.type +":","type": "select","className": "inputSelectUnformatWidth",
                    //    "selectValue": this.lp.workForm.workTypeValue.split(",")
                    //},
                    //star: {"text": this.lp.starWork +":", "type": "select", "className": "inputSelectUnformatWidth", "selectValue": this.lp.starWorkText.split(",")},
                    workTitle: { "style":this.css.filterTitle , defaultValue : this.lp.searchText, "event" : {
                        focus : function( item ){ if(item.get("value")==_self.lp.searchText)item.setValue("") },
                        blur : function( item ){ if(item.get("value").trim()=="")item.setValue(_self.lp.searchText) },
                        keydown: function( item, ev){
                            if (ev.code == 13){  //回车，搜索
                                _self.fileterNode.getElements("[item='returnAction']").setStyle("display","");
                                _self.loadView(  );
                            }
                        }.bind(this)
                    }},
                    searchAction: {
                        "type": "button", "value": this.lp.search, "style": this.css.filterButton,
                        "event": {
                            "click": function () {
                                _self.fileterNode.getElements("[item='returnAction']").setStyle("display","");
                                _self.loadView();
                            }
                        }
                    },
                    returnAction : {
                        "type": "button", "value": this.lp.return, "style": this.css.filterButton,
                        "event": {
                            "click": function () {
                                _self.filter.getItem("workTitle").setValue( _self.lp.searchText );
                                _self.fileterNode.getElements("[item='returnAction']").setStyle("display","none");
                                _self.loadView();
                            }
                        }
                    }
                }
            }, this.app, this.css);
            this.filter.load();
        }.bind(this), true);
    },
    loadView : function(  ){
        var filterData = {};
        if( this.currentCategoryNode ){
            var value = this.currentCategoryNode.retrieve("workTypeName");
            if( value && value != "" ){
                //filterData.defaultWorkTypes = [value];
                filterData.workTypes = [value];
            }
        }
        if( this.filter ){
            var fd = this.filter.getResult(true, ",", true, true, true);
            fd.workTitle = fd.workTitle.replace(this.lp.searchText,"");
            for( var key in fd ){
                if( fd[key] != "" ){
                    filterData[key] = fd[key];
                }
            }
        }

        var flag = false;
        if( this.viewContainer ){
            flag = true;
            this.viewContainer.destroy();
        }
        this.viewContainer = Element("div",{
            "styles" : this.css.viewContainer
        }).inject(this.contentDiv);

        this.setViewSize();
        if( !flag ){
            this.setViewSizeFun = this.setViewSize.bind(this);
            this.app.addEvent("resize", this.setViewSizeFun );
        }

        if( this.view ){
            this.view.destroy();
        }
        this.getViewStyle();
        this.view = new MWF.xApplication.Execution.WorkStat.WorkView( this.viewContainer, this.app, this, {
            //templateUrl : this.path+ ( this.getViewStyle() == "default" ? "listItem.json" : "listItem_graph.json" ),
            templateUrl : this.path + "listItem_stat.json",
            "scrollEnable" : true
        }, {
            lp : this.lp.centerWorkView
        });
        if( filterData )this.view.filterData = filterData;
        this.view.load();
    },
    getViewStyle : function(){
        if( this.viewStyle ) return this.viewStyle;
        this.actions.getProfileByCode( { "configCode" : "MIND_LISTSTYLE"} ,function( json ){
            if( json.data ){
                this.viewStyle = ( json.data.configValue == "ICON" ? "graph" : "default");
            }else{
                this.viewStyle = "default";
            }
        }.bind(this), function(){
            this.viewStyle = "default";
        }.bind(this), false );
        //return this.viewStyle || "default";
        return "default"
    },
    setViewSize: function(){
        var size = this.app.middleContent.getSize();
        var categoryBarSzie = this.categoryBar ? this.categoryBar.getSize() : {x:0, y:0};
        this.viewContainer.setStyles({"height":(size.y - categoryBarSzie.y - 56 )+"px"});
    },
    //*************************中心工作结束**************************************
    //*************************按部门统计开始**************************************
    statDepartment:function(){
        this.contentDiv.empty();

        this.deptToolbar = new Element("div.deptToolbar",{
            styles : this.css.deptToolbar
        }).inject(this.contentDiv);

        this.deptFileterNode = new Element("div.deptFileterNode",{
            styles : this.css.fileterNode
        }).inject(this.deptToolbar);

        this.loadDeptFilter();

    },
    loadDeptFilter: function () {
        var _self = this;
        _self.nowDate = new Date();
        _self.day = new Date(_self.nowDate.getFullYear(),(_self.nowDate.getMonth()+1),0);
        var html = "<table bordr='0' cellpadding='5' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' item='beginDate'></td>" +
            "    <td styles='filterTableValue' item='endDate'></td>" +
            "    <td styles='filterTableValue' item='workType'></td>" +
            "    <td styles='filterTableValue' item='centerWork'></td>" +
            "    <td styles='filterTableValue' item='reportCycle'></td>" +
            "    <td styles='filterTableValue' item='archiveType'></td>" +
            "    <td styles='filterTableValue' item='searchAction'></td>" +
            "    <td styles='filterTableValue' item='returnAction' style='display:none;'></td>" +
            "    <td styles='filterTableValue' item='exportAction'></td>" +
            "</tr>" +
            "</table>";
        this.deptFileterNode.set("html", html);
        var defaultWorkType="";
        this.actions.listCategoryAll(function(json){
            if(json.type=="success"){
                json.data.each(function(d,i){
                    defaultWorkType = defaultWorkType + "," + d.workTypeName
                }.bind(this))
            }
        }.bind(this),null,false);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.deptFilter = new MForm(this.deptFileterNode, {}, {
                style: "execution",
                isEdited: true,
                onPostLoad: function(){
                    _self.loadDeptStat()
                }.bind(this),
                itemTemplate: {
                    beginDate:{
                        "style":this.css.filterTitle,
                        tType:"date",
                        name:"beginDate",
                        attr : {readonly:true},
                        notEmpty:true,
                        defaultValue : _self.nowDate.getFullYear()+"-"+(_self.nowDate.getMonth()+1)+"-01",
                        "event":{
                            focus : function( item ){ if(item.get("value")==_self.lp.deptStat.beginDate)item.setValue("") },
                            blur : function( item ){ if(item.get("value").trim()=="")item.setValue(_self.lp.deptStat.beginDate) }
                        }
                    },
                    endDate:{
                        "style":this.css.filterTitle,
                        tType:"date",
                        name:"endDate",
                        attr : {readonly:true},
                        notEmpty:true,
                        defaultValue : _self.nowDate.getFullYear()+"-"+(_self.nowDate.getMonth()+1)+"-"+_self.day.getDate(),
                        "event":{
                            focus : function( item ){ if(item.get("value")==_self.lp.deptStat.endDate)item.setValue("") },
                            blur : function( item ){ if(item.get("value").trim()=="")item.setValue(_self.lp.deptStat.endDate) }
                        }
                    },
                    workType:{
                        text: this.lp.workType + ":",
                        type: "select",
                        readonly:true,
                        selectValue: defaultWorkType.split(",")
                    },
                    centerWork:{
                        type:"text",
                        defaultValue:this.lp.centerWorkDefault,
                        event:{
                            focus:function(item){this.select()}
                        }
                    },
                    reportCycle: {
                        text: this.lp.reportCycle + ":",
                        type: "radio",
                        notEmpty:true,
                        //selectValue: lp.reportCycleValue.split(","),
                        selectText: this.lp.reportCycleText.split(","),
                        defaultValue: this.lp.reportCycleText.split(",")[0],
                        event: {

                        }
                    },
                    archiveType: {
                        text: this.lp.archiveType + ":",
                        type: "radio",
                        notEmpty:true,
                        //selectValue: lp.reportCycleValue.split(","),
                        selectText: this.lp.archiveType.split(","),
                        defaultValue: this.lp.archiveType.split(",")[0],
                        event: {

                        }
                    },
                    searchAction: {
                        "type": "button", "value": this.lp.search, "style": this.css.filterButton,
                        "event": {
                            "click": function () {
                                //_self.deptFileterNode.getElements("[item='returnAction']").setStyle("display","");
                                _self.loadDeptStat()
                            }
                        }
                    },
                    returnAction : {
                        "type": "button", "value": this.lp.return, "style": this.css.filterButton,
                        "event": {
                            "click": function () {
                                _self.deptFilter.getItem("beginDate").setValue( _self.lp.deptStat.beginDate );
                                _self.deptFilter.getItem("endDate").setValue( _self.lp.deptStat.endDate );
                                _self.deptFileterNode.getElements("[item='returnAction']").setStyle("display","none");
                            }
                        }
                    },
                    exportAction : {
                        "type": "button", "value": this.lp.export, "style": this.css.filterButton,
                        "event": {
                            "click": function () {
                                _self.exportDeptExcel()
                            }
                        }
                    }
                }
            }, this.app,this.css);
            this.deptFilter.load();
        }.bind(this), true);
    },

    loadDeptStat:function(){
        if(this.deptStatContent){
            this.deptStatContent.set("text","loading...");
            this.deptStatContent.setStyles({"padding-left":"20px"})
        }

        this.bDate = this.deptFilter.getItem("beginDate").get("value");
        this.eDate = this.deptFilter.getItem("endDate").get("value");
        this.workType = this.deptFilter.getItem("workType").get("value");
        this.centerWork = this.deptFilter.getItem("centerWork").get("value");
        this.cycleType = this.deptFilter.getItem("reportCycle").get("value");
        this.archiveType = this.deptFilter.getItem("archiveType").get("value");

        if(this.bDate == "" || this.bDate == this.lp.deptStat.beginDate || this.eDate == "" || this.eDate == this.lp.deptStat.endDate){
           //this.app.notice("选择日期","error")
        }
        var filterData = {
            "cycleType":this.cycleType,
            "status":this.archiveType,
            "startDate":this.bDate,
            "endDate":this.eDate,
            "workTypeName":this.workType,
            "centerTitle":this.centerWork == this.lp.centerWorkDefault?"":this.centerWork
        };
        this.app.createShade();

        this.actions.getStatType(filterData,
            function(json){
                if(json.type == "success"){
                    this.deptStatData = json.data;
                    this.displayDeptStat();
                    var y = this.contentDiv.getSize().y - this.deptToolbar.getSize().y;
                    this.deptStatContent.setStyles({"height":(y-20)+"px"});
                }
                this.app.destroyShade()
            }.bind(this),
            function(xhr,text,error){
                this.showErrorMessage(xhr,text,error);
                this.app.destroyShade()
            }.bind(this)
        )
    },
    displayDeptStat: function() {
        if(this.deptStatContent) this.deptStatContent.destroy();
        this.deptStatContent = new Element("div.deptStatContent", {styles: this.css.deptStatContent}).inject(this.contentDiv);


        if(this.deptStatData){
            this.deptStatInfo = new Element("div.deptStatInfo",{
                "styles": this.css.deptStatInfo
            }).inject(this.deptStatContent);
            var htmlstr = "<span >已汇报:</span> <img src='"+this.path+"default/icon/Checkmark-24.png' style='vertical-align:middle;margin-right:20px; width:20px;' />";
            htmlstr += "<span>不需要汇报: </span><img src='"+this.path+"default/icon/Circle24.png' style='vertical-align:middle;margin-right:20px;width:20px;' />";
            htmlstr += "<span>未汇报:</span> <img src='"+this.path+"default/icon/Delete-24.png' style='vertical-align:middle;margin-right:20px;width:20px;' />";
            this.deptStatInfo.set("html",htmlstr);

            this.deptStatText = new Element("div.deptStatText",{"styles":this.css.deptStatText}).inject(this.deptStatInfo);
            this.deptStatText.set("html","统计类别: "+this.cycleType+"  统计周期: "+this.bDate+" --- " + this.eDate);

            if(this.deptStatData.header) cols = this.deptStatData.header.length;
            this.deptStatTable = new Element("table.deptStatTable",{styles:this.css.deptStatTable}).inject(this.deptStatContent);
            this.deptStatHeadTr = new Element("tr.deptStatHeadTr",{styles:this.css.deptStatHeadTr}).inject(this.deptStatTable);
            this.deptStatData.header.each(function(d,i){
                this.tmpTd = new Element("td.deptStatTh",{
                    "styles":this.css.deptStatTh,
                    "text": d.title
                }).inject(this.deptStatHeadTr);
                if(i==0){
                    this.tmpTd.setStyles({"width":"100px"})
                }else if(i==1 || i == 2){
                    this.tmpTd.setStyles({
                        "width":"300px",
                        "height":"auto",
                        "max-height":"50px",
                        "overflow-y":"hidden"
                    })
                }else{
                    var bd = d.startDate;
                    var ed = d.endDate;
                    tmpstr = bd.split("-")[1]+"."+bd.split("-")[2]+"-" + ed.split("-")[1]+"."+ed.split("-")[2];
                    this.tmpTd.set("text",tmpstr)
                }
            }.bind(this));
            this.deptStatTable.setStyles({"width":(cols * 50 + 680)+"px"});

            if(this.deptStatData.content){
                var curRow = 0;
                var rows1 = this.deptStatData.content.length; //多少部门
                this.deptStatData.content.each(function(d,i){
                    if(d.array){
                        var rows2 = d.array.length; //多少中心工作
                        var rowsWork2 = d.rowCount;
                        d.array.each(function(dd,ii){
                            var rows3 = dd.array.length; //多少工作
                            var rowsWork3 = dd.rowCount;
                            if(dd.array){
                                var _self = this;
                                dd.array.each(function(ddd,iii){
                                    _self.tmpTr = new Element("tr.deptStatTr",{styles:_self.css.deptStatTr}).inject(_self.deptStatTable);
                                    curRow ++;
                                    if(iii==0){
                                        if(ii==0){
                                            _self.tmpTd = new Element("td.deptStatTd",{
                                                "styles":_self.css.deptStatTd,
                                                "rowspan": rowsWork2,
                                                "text": d.title.split("@")[0]
                                            }).inject(_self.tmpTr);
                                            _self.tmpTd.setStyles({"text-align":"center"});
                                            _self.tmpTd = new Element("td.deptStatTd",{
                                                "styles":_self.css.deptStatTd,
                                                "rowspan": rowsWork3,
                                                "text": dd.title
                                            }).inject(_self.tmpTr)

                                        }else{
                                            _self.tmpTd = new Element("td.deptStatTd",{
                                                "styles":_self.css.deptStatTd,
                                                "rowspan": rowsWork3,
                                                "text": dd.title
                                            }).inject(_self.tmpTr)
                                        }
                                    }
                                    _self.tmpTd = new Element("td.deptStatTd",{
                                        "styles":_self.css.deptStatTd,
                                        "text": ddd.title,
                                        "col":1,
                                        "row":curRow
                                    }).inject(_self.tmpTr);
                                    _self.tmpTd.setStyles({"cursor":"pointer","text-decoration":"underline"});
                                    _self.tmpTd.addEvents({
                                        "click":function(){
                                            MWF.xDesktop.requireApp("Execution", "WorkDetail", function(){
                                                var workform = new MWF.xApplication.Execution.WorkDetail(_self, _self.app.restActions,{id:ddd.id},{
                                                    "isNew": false,
                                                    "isEdited": false,
                                                    "tabLocation":_self.category
                                                });

                                                workform.load();
                                            }.bind(_self));
                                        }.bind(_self),
                                        "mouseenter":function(e){
                                            _self.overStyles(e)
                                        }.bind(_self),
                                        "mouseleave":function(e){
                                            _self.outStyles(e)
                                        }.bind(_self)
                                    });
                                    if(ddd.fields){
                                        ddd.fields.each(function(dddd,iiii){
                                            _self.tmpTd = new Element("td.deptStatTd",{
                                                "styles":_self.css.deptStatTdStatus,
                                                "row":curRow,
                                                "col":iiii+2
                                            }).inject(_self.tmpTr);
                                            _self.tmpTd.addEvents({
                                                "mouseenter":function(e){
                                                    _self.overStyles(e)
                                                }.bind(_self),
                                                "mouseleave":function(e){
                                                    _self.outStyles(e)
                                                }.bind(_self)
                                            });
                                            var imgName = "";
                                            if(dddd.reportStatus == -1){
                                                imgName = "Circle24.png"
                                            }else if(dddd.reportStatus == 0){
                                                imgName = "Delete-24.png"
                                            }else if(dddd.reportStatus == 1){
                                                imgName = "Checkmark-24.png"
                                            }
                                            _self.deptStatStatusSpan = new Element("span.deptStatStatusSpan",{
                                                "styles":this.css.deptStatStatusSpan
                                            }).inject(_self.tmpTd);
                                            _self.deptStatStatusSpan.setStyles({
                                                "background":"url('"+_self.path+"default/icon/"+imgName+"')"
                                            })

                                        }.bind(_self))
                                    }
                                }.bind(_self))
                            }
                        }.bind(this))
                    }
                }.bind(this))
            }

        }
    },
    overStyles:function(e){
        var curRow = $(e.target).get("row");
        var curCol = $(e.target).get("col");
        //this.deptStatTable.getElements("[row='"+curRow+"']").setStyles({"border":"1px solid #cccccc","background-color":"#cccccc"})
        //this.deptStatTable.getElements("[col='"+curCol+"']").setStyles({"border":"1px solid #cccccc","background-color":"#cccccc"})
        //this.deptStatTable.getElements("[row='"+curRow+"']").setStyles({"background-color":"#ff0"})
        //this.deptStatTable.getElements("[col='"+curCol+"']").setStyles({"background-color":"#ff0"})
    },
    outStyles:function(e){
        var curRow = $(e.target).get("row");
        var curCol = $(e.target).get("col");
        //this.deptStatTable.getElements("[row='"+curRow+"']").setStyles({"border":"1px solid #000","background-color":""})
        //this.deptStatTable.getElements("[col='"+curCol+"']").setStyles({"border":"1px solid #000","background-color":""})
        //this.deptStatTable.getElements("[row='"+curRow+"']").setStyles({"background-color":""})
        //this.deptStatTable.getElements("[col='"+curCol+"']").setStyles({"background-color":""})
    },
    exportDeptExcel : function(){
        this.bDate = this.deptFilter.getItem("beginDate").get("value");
        this.eDate = this.deptFilter.getItem("endDate").get("value");
        this.archiveType = this.deptFilter.getItem("archiveType").get("value");
        this.workType = this.deptFilter.getItem("workType").get("value");
        this.centerWork = this.deptFilter.getItem("centerWork").get("value");
        this.cycleType = this.deptFilter.getItem("reportCycle").get("value");

        if(this.bDate == "" || this.bDate == this.lp.deptStat.beginDate || this.eDate == "" || this.eDate == this.lp.deptStat.endDate){
            this.app.notice("选择日期","error");
            return false;
        }
        var filterData = {
            "cycleType":this.cycleType,
            "startDate":this.bDate,
            "endDate":this.eDate,
            "status":this.archiveType,
            "workTypeName":this.workType,
            "centerTitle":this.centerWork == this.lp.centerWorkDefault?"":this.centerWork
        };

        this.actions.exportByDeptWork(filterData,function(json){
                if(json.data && json.data.id){
                    var address = this.actions.action.address;
                    var url = address + "/jaxrs/export/statisticreportcontent/"+json.data.id+"/stream";
                    window.open(url)
                }
            }.bind(this),
            function(xhr,text,error){
                this.showErrorMsg(xhr,text,error)
            }.bind(this),false)

    },
    //*************************按部门统计结束**************************************

    //*************************按日期统计开始**************************************
    statDate:function(){
        this.contentDiv.empty();
        this.dateToolbar = new Element("div.dateToolbar",{
            styles : this.css.dateToolbar
        }).inject(this.contentDiv);

        this.dateFileterNode = new Element("div.dateFileterNode",{
            styles : this.css.fileterNode
        }).inject(this.dateToolbar);

        this.loadDateFilter();
    },
    loadDateFilter: function () {
        var _self = this;
        _self.nowDate = new Date();
        _self.day = new Date(_self.nowDate.getFullYear(),(_self.nowDate.getMonth()+1),0);
        var html = "<table bordr='0' cellpadding='5' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' item='beginDate'></td>" +
            "    <td styles='filterTableValue' item='endDate'></td>" +
            "    <td styles='filterTableValue' item='workType'></td>" +
            "    <td styles='filterTableValue' item='centerWork'></td>" +
            "    <td styles='filterTableValue' item='reportCycle'></td>" +
            "    <td styles='filterTableValue' item='archiveType'></td>" +
            "    <td styles='filterTableValue' item='searchAction'></td>" +
            "    <td styles='filterTableValue' item='returnAction' style='display:none;'></td>" +
            "    <td styles='filterTableValue' item='exportAction'></td>" +
            "</tr>" +
            "</table>";
        this.dateFileterNode.set("html", html);
        var defaultWorkType="";

        this.actions.listCategoryAll(function(json){
            if(json.type=="success"){
                json.data.each(function(d,i){
                    defaultWorkType = defaultWorkType + "," + d.workTypeName
                }.bind(this))
            }
        }.bind(this),null,false);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.dateFilter = new MForm(this.dateFileterNode, {}, {
                style: "execution",
                isEdited: true,
                onPostLoad: function(){
                    _self.loadDateStat()
                }.bind(this),
                itemTemplate: {
                    beginDate:{
                        "style":this.css.filterTitle,
                        tType:"date",
                        name:"beginDate",
                        attr : {readonly:true},
                        notEmpty:true,
                        defaultValue : _self.nowDate.getFullYear()+"-"+(_self.nowDate.getMonth()+1)+"-01",
                        "event":{
                            focus : function( item ){ if(item.get("value")==_self.lp.dateStat.beginDate)item.setValue("") },
                            blur : function( item ){ if(item.get("value").trim()=="")item.setValue(_self.lp.dateStat.beginDate) }
                        }
                    },
                    endDate:{
                        "style":this.css.filterTitle,
                        tType:"date",
                        name:"endDate",
                        attr : {readonly:true},
                        notEmpty:true,
                        defaultValue : _self.nowDate.getFullYear()+"-"+(_self.nowDate.getMonth()+1)+"-"+_self.day.getDate(),
                        "event":{
                            focus : function( item ){ if(item.get("value")==_self.lp.dateStat.endDate)item.setValue("") },
                            blur : function( item ){ if(item.get("value").trim()=="")item.setValue(_self.lp.dateStat.endDate) }
                        }
                    },
                    workType:{
                        text: this.lp.workType + ":",
                        type: "select",
                        attr : {readonly:true},
                        selectValue: defaultWorkType.split(",")
                    },
                    centerWork:{
                        type:"text",
                        defaultValue:this.lp.centerWorkDefault,
                        event:{
                            focus:function(item){this.select()}
                        }
                    },
                    reportCycle: {
                        text: this.lp.reportCycle + ":",
                        type: "radio",
                        notEmpty:true,
                        //selectValue: lp.reportCycleValue.split(","),
                        selectText: this.lp.reportCycleText.split(","),
                        defaultValue: this.lp.reportCycleText.split(",")[0],
                        event: {

                        }
                    },
                    archiveType: {
                        text: this.lp.archiveType + ":",
                        type: "radio",
                        notEmpty:true,
                        //selectValue: lp.reportCycleValue.split(","),
                        selectText: this.lp.archiveType.split(","),
                        defaultValue: this.lp.archiveType.split(",")[0],
                        event: {

                        }
                    },
                    searchAction: {
                        "type": "button", "value": this.lp.search, "style": this.css.filterButton,
                        "event": {
                            "click": function () {
                                //_self.deptFileterNode.getElements("[item='returnAction']").setStyle("display","");
                                _self.loadDateStat()
                            }
                        }
                    },
                    returnAction : {
                        "type": "button", "value": this.lp.return, "style": this.css.filterButton,
                        "event": {
                            "click": function () {
                                _self.dateFilter.getItem("beginDate").setValue( _self.lp.dateStat.beginDate );
                                _self.dateFilter.getItem("endDate").setValue( _self.lp.dateStat.endDate );
                                _self.dateFileterNode.getElements("[item='returnAction']").setStyle("display","none");
                            }
                        }
                    },
                    exportAction : {
                        "type": "button", "value": this.lp.export, "style": this.css.filterButton,
                        "event": {
                            "click": function () {
                                _self.exportDateExcel()
                            }
                        }
                    }
                }
            }, this.app, this.css);
            this.dateFilter.load();
        }.bind(this), true);
    },

    loadDateStat:function(){
        this.bDate = this.dateFilter.getItem("beginDate").get("value");
        this.eDate = this.dateFilter.getItem("endDate").get("value");
        this.workType = this.dateFilter.getItem("workType").get("value");
        this.centerWork = this.dateFilter.getItem("centerWork").get("value");
        this.cycleType = this.dateFilter.getItem("reportCycle").get("value");
        this.archiveType = this.dateFilter.getItem("archiveType").get("value");

        if(this.bDate == "" || this.bDate == this.lp.dateStat.beginDate || this.eDate == "" || this.eDate == this.lp.dateStat.endDate){
            //this.app.notice("选择日期","error")
        }
        if(this.dateStatListDiv) this.dateStatListDiv.destroy();
        if(this.statViewListDiv) this.statViewListDiv.destroy();
        var filterData = {
            "reportCycle":this.cycleType,
            "status":this.archiveType,
            "workTypeName":this.workType,
            "startDate":this.bDate,
            "centerTitle":this.centerWork == this.lp.centerWorkDefault?"":this.centerWork,
            "endDate":this.eDate
        };
        this.app.createShade();
        this.actions.getStatDateList(filterData,function(json){
                this.app.destroyShade();
                if(json.type == "success"){
                    this.dateStatListData = json.data;
                    this.displayDateStatList();
                }

            }.bind(this),
            function(xhr,text,error){
                this.showErrorMessage(xhr,text,error);
                this.app.destroyShade();
            }.bind(this)
        )


    },
    displayDateStatList: function() {
        if(this.dateStatListDiv) this.dateStatListDiv.destroy();
        if(this.statViewListDiv) this.statViewListDiv.destroy();

        this.dateStatListDiv = new Element("div.dateStatListDiv", {
            "styles": this.css.dateStatListDiv
        }).inject(this.contentDiv);

        var dateStatListTitleDiv = new Element("div.dateStatListTitleDiv", {
            "styles": this.css.dateStatListTitleDiv,
            "text": this.lp.dateStatListTitle
        }).inject(this.dateStatListDiv);

        this.dateStatContentDiv = new Element("div.dateStatContentDiv", {
            "styles": this.css.dateStatContentDiv
        }).inject(this.dateStatListDiv);

        this.loadDateStatList();
    },
    loadDateStatList: function(){
        if(this.dateStatListData){
            this.dateStatListData.each(function(d,i){
                var tmpLi = new Element("li.dateStatContentLi",{
                    "styles": this.css.dateStatContentLi,
                    "text": d.datetime,
                    "title": d.reportCycle
                }).inject(this.dateStatContentDiv);
                tmpLi.addEvents({
                    "click":function(){
                        this.dateStatContentDiv.getElements("li").setStyles({"background-color":"","color":""});
                        tmpLi.setStyles({"background-color":"#3c76c1","color":"#ffffff"});
                        this.currentDateData = d;
                        this.displayDateStat(d)
                    }.bind(this)
                })
            }.bind(this));
            if(this.dateStatListData.length==0){
                this.tmpSpan = new Element("span",{
                    styles:{"margin-left":"10px"},
                    text :this.lp.nullReportStat
                }).inject(this.dateStatContentDiv)
            }
            if(this.dateStatContentDiv.getElements("li").length>0)this.dateStatContentDiv.getElements("li")[0].click()
        }

    },
    displayDateStat: function(d,id){
        if(this.statViewListDiv){
            this.statViewListDiv.empty();
            this.statViewListDiv.set("text","loading...");
            this.statViewListDiv.setStyles({"padding-left":"15px"})
        }
        if(d){
            var filterData = {
                "statisticTimeFlag": d.datetime,
                "workTypeName":this.workType,
                "status":this.archiveType,
                "centerTitle":this.centerWork == this.lp.centerWorkDefault?"":this.centerWork,
                "reportCycle": d.reportCycle
            };
            if(id){
                filterData.centerId = id
            }

            this.app.createShade();
            this.actions.getStatDate(filterData,function(json){
                    if(json.type == "success"){
                        this.dateStatData = json.data;
                        this.displayDateStatTable()
                    }
                    this.app.destroyShade();
                }.bind(this),
                function(xhr,text,error){
                    this.showErrorMessage(xhr,text,error);
                    this.app.destroyShade();
                }.bind(this)
            )


        }

    },

    displayDateStatTable:function(){
        if(this.statViewListDiv) this.statViewListDiv.empty();
        if(this.dateStatData){
            if(this.statViewListDiv) this.statViewListDiv.destroy();
            this.statViewListDiv = new Element("div.statViewListDiv", {
                "styles": this.css.statViewListDiv
            }).inject(this.contentDiv);

            var y = this.contentDiv.getSize().y - this.dateToolbar.getSize().y - this.dateStatListDiv.getSize().y;
            this.statViewListDiv.setStyles({"height":(y-50)+"px"});

            this.statTable = new Element("table.statTable",{
                "styles":this.css.statTable
            }).inject(this.statViewListDiv);
            //this.statTable.set("border","1")

            this.statHeadTr = new Element("tr.statHeadTr",{
                "styles":this.css.statHeadTr
            }).inject(this.statTable);


            for(var o in this.lp.statTable){
                var statHeadTd = new Element("td.statHeadTd",{
                    "styles": this.css.statHeadTd,
                    "text":this.lp.statTable[o]
                }).inject(this.statHeadTr);
            }

            this.dateStatData.each(function(d,i){
                var centerTr = new Element("tr.centerTr").inject(this.statTable);
                var centerTd = new Element("td.dateStatCenterTd",{
                    "styles": this.css.dateStatCenterTd,
                    "colspan": 9,
                    "text": d.title
                }).inject(centerTr);
                if(d.contents && d.contents.length>0){
                    d.contents.each(function(dd,ii){
                        var baseTr = new Element("tr.baseTr").inject(this.statTable);
                        //var baseTd = new Element("td.dateStatBaseTd",{"styles":this.css.dateStatBaseTd,"text":(ii+1),"id":dd.workId}).inject(baseTr)
                        //baseTd.setStyles({"width":"35px","text-align":"center"})

                        for(var o in this.lp.statTable){
                            //if(o!="order"){
                            var val = "";
                            if(o=="opinions") {
                                if(dd[o]){
                                    dd[o].each(function(ddd){
                                        val = val + ddd.processorName.split("@")[0] + "：\n"+ ddd.opinion +"\n"
                                    })
                                }
                            }else if(o == "responsibilityUnitName"){
                                val = dd[o];
                                val = val.split("@")[0]
                            }else{
                                if(dd[o])val = dd[o]
                            }
                            var baseTd = new Element("td.dateStatBaseTd",{
                                "styles": this.css.dateStatBaseTd,
                                "html": val.length>50?val.substring(0,50)+"...":val,
                                "title":val
                            }).inject(baseTr);
                            if(o=="serialNumber"){
                                baseTd.setStyles({"width":"35px","padding-left":"15px","min-width":""})
                            }
                            if(o=="responsibilityUnitName"){
                                baseTd.setStyles({"width":"87px"})
                            }
                            if(o=="workDetail"){
                                //baseTd.setStyles({"cursor":"pointer","color":"#3d77c1","text-decoration":"underline"})
                                //baseTd.addEvents({
                                //    "click":function(){
                                //        this.loadSubStat(id, dd.workId,baseTr);
                                //        //this.actions.getStatByWorkId(id, d.workId,function(json){
                                //        //
                                //        //}.bind(this),function(xhr,text,error){
                                //        //    this.showErrorMsg(xhr,text,error)
                                //        //}.bind(this),false)
                                //    }.bind(this)
                                //})
                            }
                            //}
                        }


                    }.bind(this))
                }
            }.bind(this))
        }
    },
    exportDateExcel : function(){
        if(this.currentDateData){
            var sendData = {};
            sendData.statisticTimeFlag = this.currentDateData.datetime;
            sendData.reportCycle = this.currentDateData.reportCycle;
            sendData.status = this.currentDateData.status;
            sendData.centerTitle = this.centerWork == this.lp.centerWorkDefault?"":this.centerWork;
            sendData.workTypeName = this.workType;

            this.actions.exportByCenterWork(sendData,function(json){
                    if(json.data && json.data.id){
                        var address = this.actions.action.address;
                        var url = address + "/jaxrs/export/statisticreportcontent/"+json.data.id+"/stream";
                        window.open(url)
                    }
                }.bind(this),
                function(xhr,text,error){
                    this.showErrorMsg(xhr,text,error)
                }.bind(this),false)
        }
    },






    //*************************按日期统计结束**************************************
    showErrorMessage:function(xhr,text,error){
        var errorText = error;
        if (xhr) errorMessage = xhr.responseText;
        if(errorMessage!=""){
            var e = JSON.parse(errorMessage);
            if(e.message){
                this.app.notice( e.message,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }else{
            this.app.notice(errorText,"error")
        }

    },
    setScrollBar: function(node, style, offset, callback){
        if (!style) style = "attachment";
        if (!offset){
            offset = {
                "V": {"x": 0, "y": 0},
                "H": {"x": 0, "y": 0}
            };
        }
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(node, {
                "style": style,
                "offset": offset,
                "indent": false
            });
            if (callback) callback();
        });
        return false;
    }
});



MWF.xApplication.Execution.WorkStat.WorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkStat.WorkDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        if(id=="(0)")this.app.createShade();
        var filter = this.filterData || {};
        this.actions.getCenterWorkListNext(id, count, filter, function (json) {
            if (callback)callback(json);
            this.app.destroyShade();
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        //this.actions.deleteSchedule(documentData.id, function(json){
        //    this.reload();
        //    this.app.notice(this.app.lp.deleteDocumentOK, "success");
        //}.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        MWF.xDesktop.requireApp("Execution", "StatForm", function(){
            var statForm = new MWF.xApplication.Execution.StatForm( this,this.app.restActions, documentData, {
                "centerWorkId":documentData.id
            });
            statForm.load();

        }.bind(this));

    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }

});

MWF.xApplication.Execution.WorkStat.WorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverDocument : function(){
        this.node.getElements("[styles='documentItemTitleNode']").setStyles(this.css["documentItemTitleNode_over"]);
        this.node.getElements("[styles='documentItemIconNode']").setStyles(this.css["documentItemIconNode_over"]);
        this.node.getElements("[styles='documentItemStatNode']").setStyles(this.css["documentItemStatNode_over"]);
    },
    mouseoutDocument : function(){
        this.node.getElements("[styles='documentItemTitleNode']").setStyles(this.css["documentItemTitleNode"]);
        this.node.getElements("[styles='documentItemIconNode']").setStyles(this.css["documentItemIconNode"]);
        this.node.getElements("[styles='documentItemStatNode']").setStyles(this.css["documentItemStatNode"]);
    },
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){

    },
    removeCenterWork : function(itemData){
        //如果是管理员有删除部署的中心工作的权限
        //if(isAdmin){
        //    return true;
        //}
        return false;
    }
});

