MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xApplication.Attendance.WeekendSetting = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.PopupForm,
    options:{
        "height": 300,
        "hasTop" : true,
        "hasBottom" : true,
        "title": MWF.xApplication.Attendance.LP.weekendSetting
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
    _createTableContent: function(){
        var lp = MWF.xApplication.Attendance.LP;
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


        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td style='width: 100px;' styles='formTableTitle' lable='ATTENDANCE_WEEKEND'></td>"+
            "    <td styles='formTableValue' item='ATTENDANCE_WEEKEND'></td>"+
            "</table>";
        html = html+"<span style='font-size:12px'>"+this.dataJson.ATTENDANCE_WEEKEND.description+"</span>"
        this.formTableArea.set("html",html);
        this.itemTemplate = {
            ATTENDANCE_WEEKEND : { text : lp.selectWeekend,
                type : "select",
                value : d.ATTENDANCE_WEEKEND ,
                selectValue :this.dataJson.ATTENDANCE_WEEKEND.selectContent.split("|") ,//["周六","周日"]
                /*event : {
                    change : function( item, ev ){
                        this.createTableArea.getElement("[item='valueArea']").setStyle( "display" , (item.getValue() == "汇报对象") ? "none" : "" );
                    }.bind(this)
                }*/
            }
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
        //
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
            this.app.notice( MWF.xApplication.Attendance.LP.saveSuccess , "success");
        }
    }
});
