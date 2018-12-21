
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xApplication.Attendance.AppSetting = new Class({
    Extends: MWF.widget.Common,
    options:{
        style : "default"
    },
    initialize: function(app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Attendance/$AppSetting/";
        this.cssPath = "/x_component_Attendance/$AppSetting/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.load();
    },
    load: function(){
        this.app.restActions.listSetting(function(json){
            if( json.data && json.data.length>0){
                this.data = json.data;
            }
        }.bind(this),null,false);
        if( !this.data ){
            this.data = [];
        }
    },
    decodeData : function( data ){
        //{
        //    'configCode':'APPEALABLE',
        //    'configName':'申诉功能启用状态',
        //    'configValue':'false',
        //    'ordernumber':1
        //}
        var json = {};
        this.dataJson = {};
        data.each( function(d){
            json[d.configCode] = d.configValue;
            this.dataJson[d.configCode] = d;
        }.bind(this));
        //alert(JSON.stringify(json))
        return json;
    },
    encodeData : function( orgData, data ){
        var arr = [];
        for( var d in data ){
            if( this.itemTemplate[d] ){
                var flag = false;
                for( var i=0; i<orgData.length;i++ ){
                    if( orgData[i].configCode == d ){
                        flag = true;
                        orgData[i].configValue = data[d];
                        arr.push( Object.clone(orgData[i]) );
                    }
                }
                if( !flag ){
                    arr.push( {
                        configCode : d,
                        configValue : data[d],
                        configName : this.itemTemplate[d].text
                    } )
                }
            }
        };
       // alert(JSON.stringify(arr))
        return arr;
    },
    open: function(e){
        this.isNew = false;
        this.isEdited = false;
        this._open();
    },
    create: function(){
        this.isNew = true;
        this._open();
    },
    edit: function(){
        this.isEdited = true;
        this._open();
    },
    _open : function(){
        this.createMarkNode = new Element("div", {
            "styles": this.css.createMarkNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.app.content, "after");

        this.createAreaNode = new Element("div", {
            "styles": this.css.createAreaNode
        });

        this.createNode();

        this.createAreaNode.inject(this.createMarkNode, "after");
        this.createAreaNode.fade("in");

        this.setCreateNodeSize();
        this.setCreateNodeSizeFun = this.setCreateNodeSize.bind(this);
        this.addEvent("resize", this.setCreateNodeSizeFun);
    },
    createNode: function(){
        var _self = this;

        this.createNode = new Element("div", {
            "styles": this.css.createNode
        }).inject(this.createAreaNode);

        //
        //this.createIconNode = new Element("div", {
        //    "styles": this.isNew ? this.css.createNewNode : this.css.createIconNode
        //}).inject(this.createNode);

        this.createContainerNode = new Element("div", {
            "styles": this.css.createContainerNode
        }).inject(this.createNode);


        this.setScrollBar( this.createContainerNode );


        this.createFormNode = new Element("div", {
            "styles": this.css.createFormNode
        }).inject(this.createContainerNode);

        this.createTableContainer = new Element("div", {
            "styles": this.css.createTableContainer
        }).inject(this.createFormNode);

        this.createTableArea = new Element("div", {
            "styles": this.css.createTableArea
        }).inject(this.createTableContainer);


        var table = new Element("table", {
            "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.editTable, "class" : "editTable"
        }).inject( this.createTableArea );


        var d = this.decodeData( this.data );


        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td colspan='4' styles='formTableHead'>系统设置</td></tr>" +
            "<tr><td styles='formTableTitle' lable='APPEALABLE'></td>"+
            "    <td styles='formTableValue' item='APPEALABLE'></td>"+
            "<tr><td styles='formTableTitle' lable='APPEAL_AUDITOR_TYPE'></td>"+
            "    <td styles='formTableValue' item='APPEAL_AUDITOR_TYPE'></td>"+
            "<tr item='valueArea' style='display:"+ (d.APPEAL_AUDITOR_TYPE == "汇报对象" ? "none" : "") +"' ><td styles='formTableTitle' lable='APPEAL_AUDITOR_VALUE'></td>"+
            "    <td styles='formTableValue' item='APPEAL_AUDITOR_VALUE'></td>"+
            //"<tr><td styles='formTableTitle' lable='APPEAL_CHECKER_TYPE'></td>"+
            //"    <td styles='formTableValue' item='APPEAL_CHECKER_TYPE'></td>"+
            //"<tr><td styles='formTableTitle' lable='APPEAL_CHECKER_VALUE'></td>"+
            //"    <td styles='formTableValue' item='APPEAL_CHECKER_VALUE'></td>"+
            "</table>";
        this.createTableArea.set("html",html);

        this.itemTemplate = {
            APPEALABLE : { text:"申诉及审批功能启用状态",
                type : "select",
                value : d.APPEALABLE || "true",
                selectText : ["开启","关闭"],
                selectValue : ["true","false"]
            },
            APPEAL_AUDITOR_TYPE : { text : "申诉审核人确定方式",
                type : "select",
                value : d.APPEAL_AUDITOR_TYPE ,
                selectValue : this.dataJson.APPEAL_AUDITOR_TYPE.selectContent.split("|"), //["人员属性","所属部门职位","指定人","汇报对象"],
                event : {
                    change : function( item, ev ){
                        this.createTableArea.getElement("[item='valueArea']").setStyle( "display" , (item.getValue() == "汇报对象") ? "none" : "" );
                    }.bind(this)
                }
            },
            APPEAL_AUDITOR_VALUE : { text : "申诉审核人确定内容",
                type : "text",
                value : d.APPEAL_AUDITOR_VALUE ,
                defaultValue : "直属领导"
            }//,
            //APPEAL_CHECKER_TYPE : { text : "考勤结果申诉复核人确定方式",
            //    type : "select",
            //    value : d.APPEAL_CHECKER_TYPE ,
            //    selectValue : ["无","人员属性","所属部门职位","指定人"] //,"指定角色"]
            //},
            //APPEAL_CHECKER_VALUE : { text : "考勤结果申诉复核人确定内容",
            //    type : "text",
            //    value : d.APPEAL_CHECKER_VALUE
            //}
        };
        this.document = new MForm( this.createTableArea, this.data, {
            style : "popup",
            isEdited : this.isEdited || this.isNew,
            itemTemplate : this.itemTemplate
        }, this.app,this.css);
        this.document.load();

        this.cancelActionNode = new Element("div", {
            "styles": this.css.createCancelActionNode,
            "text": "取消"
        }).inject(this.createFormNode);


        this.cancelActionNode.addEvent("click", function(e){
            this.cancelCreate(e);
        }.bind(this));

        if( this.isNew || this.isEdited ){
            this.createOkActionNode = new Element("div", {
                "styles": this.css.createOkActionNode,
                "text": "确定"
            }).inject(this.createFormNode);

            this.createOkActionNode.addEvent("click", function(e){
                this.okCreate(e);
            }.bind(this));
        }

    },
    setCreateNodeSize: function(){
        var size = this.app.node.getSize();
        var allSize = this.app.content.getSize();

        var height = "470";
        var width = "600";

        this.createAreaNode.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px"
        });
        var hY = height;
        var mY = (size.y-height)/2;
        this.createNode.setStyles({
            "height": ""+hY+"px",
            "margin-top": ""+mY+"px",
            "width" : ""+width+"px"
        });

        this.createContainerNode.setStyles({
            "height": ""+hY+"px"
        });

        var iconSize = this.createIconNode ? this.createIconNode.getSize() : {x:0,y:0};
        var formMargin = hY-iconSize.y-60;
        this.createFormNode.setStyles({
            "height": ""+formMargin+"px",
            "margin-top": ""+60+"px"
        });
    },
    cancelCreate: function(e){
        this.createMarkNode.destroy();
        this.createAreaNode.destroy();
        delete this;
    },
    okCreate: function(e){
        var data = this.document.getResult(true,",",true,false,false);
        if(data){
            var arr = this.encodeData( this.data, data );
            this.save( arr );
        }
    },
    save: function( arr ){
        var flag = true;
        arr.each( function( d ){
            this.app.restActions.saveSetting( d, function(json){
                if( json.type == "ERROR" ){
                    this.app.notice( json.message  , "error");
                    flag = false;
                }
            }.bind(this), null, false);
        }.bind(this));
        if( flag ){
            this.createMarkNode.destroy();
            this.createAreaNode.destroy();
            this.app.notice( "保存成功" , "success");
        }
    }
});
