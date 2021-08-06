MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xApplication.Attendance.AppSetting = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.PopupForm,
    options : {
        "width": "520",
        "hasTop" : true,
        "hasBottom" : true,
        "title": MWF.xApplication.Attendance.LP.systemSetting
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
        return arr;
    },
    _createTableContent: function(){
        var _self = this;

        this.app.restActions.listSetting(function(json){
            if( json.data && json.data.length>0){
                this.data = json.data;
            }
        }.bind(this),null,false);
        if( !this.data ){
            this.data = [];
        }

        var d = this.decodeData( this.data );

        var lp = this.app.lp;
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td styles='formTableTitle' lable='APPEALABLE'></td>"+
            "    <td styles='formTableValue' item='APPEALABLE'></td>"+
            "<tr><td styles='formTableTitle' lable='APPEAL_AUDIFLOWTYPE'></td>"+
            "    <td styles='formTableValue' item='APPEAL_AUDIFLOWTYPE'></td>"+
            "<tr item='AUDITOR_TYPE' style='display:"+ (d.APPEAL_AUDIFLOWTYPE == "WORKFLOW" ? "none" : "") +"'><td styles='formTableTitle' lable='APPEAL_AUDITOR_TYPE'></td>"+
            "    <td styles='formTableValue' item='APPEAL_AUDITOR_TYPE'></td>"+
            "<tr item='valueArea' style='display:"+ (d.APPEAL_AUDITOR_TYPE == lp.reportTo || d.APPEAL_AUDITOR_TYPE == "汇报对象" || d.APPEAL_AUDIFLOWTYPE == "WORKFLOW" ? "none" : "") +"' ><td styles='formTableTitle' lable='APPEAL_AUDITOR_VALUE'></td>"+
            "    <td styles='formTableValue' style='width: 60%' item='APPEAL_AUDITOR_VALUE'></td>" +
            "<tr item='AUDIFLOW' style='display:"+ (d.APPEAL_AUDIFLOWTYPE == "BUILTIN" ? "none" : "") +"'><td styles='formTableTitle' lable='APPEAL_AUDIFLOW_ID'></td>" +
            "    <td styles='formTableValue' style='width: 60%' item='APPEAL_AUDIFLOW_ID'></td>" +
            //"<tr><td styles='formTableTitle' lable='APPEAL_CHECKER_TYPE'></td>"+
            //"    <td styles='formTableValue' item='APPEAL_CHECKER_TYPE'></td>"+
            //"<tr><td styles='formTableTitle' lable='APPEAL_CHECKER_VALUE'></td>"+
            //"    <td styles='formTableValue' item='APPEAL_CHECKER_VALUE'></td>"+
            "</table>";
        this.formTableArea.set("html",html);


        this.itemTemplate = {
            APPEALABLE : { text: lp.appealEnable,
                type : "select",
                value : d.APPEALABLE || "true",
                selectText : lp.appealSelectText,
                selectValue : ["true","false"]
            },
            APPEAL_AUDIFLOWTYPE : { text : lp.appealAuditFlowType,
                type : "select",
                value : d.APPEAL_AUDIFLOWTYPE ,
                selectValue : this.dataJson.APPEAL_AUDIFLOWTYPE.selectContent.split("|"), //["人员属性","所属部门职位","指定人","汇报对象"],
                selectText : lp.appealAuditFlowTypeSelectText,
                event : {
                    change : function( item, ev ){
                        this.formTableArea.getElement("[item='AUDITOR_TYPE']").setStyle( "display" , (item.getValue() == "WORKFLOW") ? "none" : "" );
                        this.formTableArea.getElement("[item='valueArea']").setStyle( "display" , (item.getValue() == "WORKFLOW") ? "none" : "" );
                        this.formTableArea.getElement("[item='AUDIFLOW']").setStyle( "display" , (item.getValue() == "BUILTIN") ? "none" : "" );
                    }.bind(this)
                }
            },
            APPEAL_AUDITOR_TYPE : { text : lp.appealAuditorType,
                type : "select",
                value : d.APPEAL_AUDITOR_TYPE ,
                selectValue : this.dataJson.APPEAL_AUDITOR_TYPE.selectContent.split("|"), //["人员属性","所属部门职位","指定人","汇报对象"],
                event : {
                    change : function( item, ev ){
                        this.formTableArea.getElement("[item='valueArea']").setStyle( "display" , (item.getValue() == "汇报对象" || item.getValue() == lp.reportTo) ? "none" : "" );
                    }.bind(this)
                }
            },
            APPEAL_AUDITOR_VALUE : { text : lp.appealAuditorValue,
                type : "text",
                value : d.APPEAL_AUDITOR_VALUE ,
                defaultValue : lp.directSupervisor
            },
            APPEAL_AUDIFLOW_ID : { text : lp.appealAuditFlow,
                type : "org",
                orgType: ["process"],
                count : 1,
                isEdited : this.isEdited || this.isNew,
                value : (!d.APPEAL_AUDIFLOW_ID||d.APPEAL_AUDIFLOW_ID== lp.none || d.APPEAL_AUDIFLOW_ID=="无") ?"":d.APPEAL_AUDIFLOW_ID,
                defaultValue : "",
                orgWidgetOptions : {
                    "onLoadedInfor": function(item){
                        // this.loadAcceptAndReject( item );
                        console.log(item);
                    }.bind(this),
                    "onComplete": function(item){
                        console.log(item);
                    }.bind(this)
                }
            }
            //,
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
        this.document = new MForm( this.formTableArea, this.data, {
            style : "attendance",
            isEdited : this.isEdited || this.isNew,
            itemTemplate : this.itemTemplate
        }, this.app,this.css);
        this.document.load();

        // this.cancelActionNode = new Element("div", {
        //     "styles": this.css.createCancelActionNode,
        //     "text": lp.cancel
        // }).inject(this.createFormNode);
        //
        //
        // this.cancelActionNode.addEvent("click", function(e){
        //     this.cancelCreate(e);
        // }.bind(this));

        // if( this.isNew || this.isEdited ){
        //     this.createOkActionNode = new Element("div", {
        //         "styles": this.css.createOkActionNode,
        //         "text": lp.ok
        //     }).inject(this.createFormNode);
        //
        //     this.createOkActionNode.addEvent("click", function(e){
        //         this.okCreate(e);
        //     }.bind(this));
        // }

    },
    ok: function(e){
        var data = this.document.getResult(true,",",true,false,false);
        if(data){
            var APPEAL_AUDIFLOW_ID = data.APPEAL_AUDIFLOW_ID;
            if(!!APPEAL_AUDIFLOW_ID &&APPEAL_AUDIFLOW_ID!=this.app.lp.none &&APPEAL_AUDIFLOW_ID!="无"&&APPEAL_AUDIFLOW_ID!=""&&!!this.document.items.APPEAL_AUDIFLOW_ID.orgObject){
                data.APPEAL_AUDIFLOW_ID = this.document.items.APPEAL_AUDIFLOW_ID.orgObject[0].data.id;
            }
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
            if( this.formMaskNode )this.formMaskNode.destroy();
            if( this.formAreaNode )this.formAreaNode.destroy();
            // if (this.explorer && this.explorer.view)this.explorer.view.reload();
            this.app.notice( this.app.lp.saveSuccess , "success");
        }
    }
});
