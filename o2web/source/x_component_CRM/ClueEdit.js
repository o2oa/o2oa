MWF.xApplication.CRM.ClueEdit = new Class({
    Extends: MWF.xApplication.CRM.Template.PopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "800",
        "height": "100%",
        "top" : 0,
        "left" : 0,
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "title": "",
        "draggable": false,
        "closeAction": true
    },

    initialize: function (explorer, actions, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = this.app.lp.clue.clueEdit;
        this.path = "/x_component_CRM/$ClueEdit/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};
        this.actions = actions;
        debugger
    },
    load: function () {
        this.loadResource(function(){
            this.appArea = jQuery("body").children(":first");
            this.createForm();
        }.bind(this))
    },
    loadResource: function ( callback ) {
        if(callback)callback();
    },
    createForm:function(){
        _self = this;
        jQuery(_self.appArea).next().attr("style","");
        jQuery(_self.appArea).next().attr("class","mask");
        var section_header = '<div class="section-header"><div class="section-mark" style="border-left-color: rgb(70, 205, 207);"></div> '+
            '<div data-v-ec8f8850="" class="section-title">基本信息</div></div>';
        //var itemTemplateObject = _self.getItemTemplate(_self.lp );
        var itemTemplateObject = _self.lp;
        debugger
        var section_conent = '<div class="section-conent">';
        for ( i in itemTemplateObject){
            var stype = itemTemplateObject[i].type;
            //var innerHtml = '<input type="text" class="inline-input" name="'+i+'" id="'+i+'" stype="'+stype+'">';
            var notEmpty = itemTemplateObject[i].notEmpty?itemTemplateObject[i].notEmpty:"false";
            var innerHtml = '<input type="text" class="inline-input" name="'+i+'" id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'">';
            if(stype=="textarea"){
                innerHtml =  '<textarea rows="6" class="el-textarea__inner"  id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'"  style="resize: none; min-height: 30.6px;"></textarea>';
            }
            if(stype=="select"){
                innerHtml = '<div class="inline-input" style="display: inline-block;cursor:pointer;"  id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'" ></div><div class="el-icon-arrow-down el-icon--right" style="margin-left: -20px;height:34px; display: inline-block;"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div>';
            }
            section_conent = section_conent+'<div class="conent-inline"><div class="conent-title" lable="'+i+'">'+itemTemplateObject[i].text+'</div>' +
                '<div class="conent-value">'+innerHtml+'</div></div>';
        }
        section_conent = section_conent + '</div>';
        var section_button = '<div class="section_button"><div><button class="el-button handle-button el-button-cancle"><span>取消</span></button>'+
            '<button class="el-button handle-button el-button-primary"><span>保存</span></button></div></div>';
        var htmlstr = section_header+section_conent+section_button;

        jQuery(".headMoreImg").notifyMe(
            'left',
            'default',
            '新建线索',
            '',
            '',
            htmlstr,
            'notifyEdit',
            50
        );
        jQuery(".conent-value").each(function(index,element){
                var cobj = jQuery(element).children().eq(0)
                var stype = jQuery(cobj).attr("stype");
                if(stype=="datetime"){
                    _self.loadTimeContainer(jQuery(cobj).attr("id"));
                }
                if(stype=="select"){
                    var selectObjects = _self.app.lp.clue;
                    for ( j in selectObjects){
                        if(j==jQuery(cobj).attr("id")){
                            var clp = itemTemplateObject[j];
                            var valueList = clp.value;
                            var valueArr = valueList.split(",");
                            if(valueArr.length>0){
                                var selectHtml = '<ul class="el-dropdown-type" style="display: none;" tid="'+jQuery(cobj).attr("id")+'">'
                                for(var n=0;n<valueArr.length;n++){
                                    selectHtml = selectHtml+'<li class="el-dropdown-menu__item">'+valueArr[n]+'</li>'
                                }
                                jQuery(".notify-content").append(selectHtml+'<div class="popper__arrow"></div></ul>');
                                jQuery(cobj).click(function(){
                                    jQuery("[tid='"+jQuery(cobj).attr("id")+"']").css({"left":jQuery(cobj).offset().left-50,"top":jQuery(cobj).offset().top+30,"width":282})
                                    jQuery("[tid='"+jQuery(cobj).attr("id")+"']").toggle(100);
                                });
                                jQuery("[tid='"+jQuery(cobj).attr("id")+"']").children().click(function(){
                                    jQuery(cobj).text(jQuery(this).text());
                                    jQuery("[tid='"+jQuery(cobj).attr("id")+"']").toggle(100);
                                });
                            }
                        }
                    }

                }
            }
        );

        jQuery('.el-button-cancle').click(function(){
            setTimeout(function(){
                jQuery("#notifyEdit").remove();
                if(jQuery(".mask").length>0){
                    jQuery(".mask").attr("style",'left: 0px; top: 0px; width: 100%; overflow: hidden; position: absolute; z-index: 500000; background-color: rgb(255, 255, 255)');
                    jQuery(".mask").attr("class","");
                }
            },200);
        });
        jQuery('.el-button-primary').click(function(){
            var sflag = true;
            jQuery(".inline-input[notempty='true']").each(function(index,element){
                    if(jQuery(element).val()=="" && jQuery(element).text()==""){
                        sflag = false;
                        var nameStr = jQuery(element).parent().prev().text()+'不能为空';
                        if(jQuery(element).nextAll(".empError").length>0)jQuery(element).nextAll(".empError").remove();
                        jQuery(element).parent().append('<div class="empError" style="color:#f56c6c;padding: 0;line-height: 1;">'+nameStr+'</div>');
                    }else{
                        if(jQuery(element).nextAll(".empError").length>0)jQuery(element).nextAll(".empError").remove();
                    }
            });
            if(sflag){
                var filter = {};
                filter = {
                    name:jQuery('div[lable="name"]').next().children().eq(0).val(),
                    source:jQuery('div[lable="source"]').next().children().eq(0).val(),
                    telephone:jQuery('div[lable="telephone"]').next().children().eq(0).val(),
                    cellphone:jQuery('div[lable="cellphone"]').next().children().eq(0).val(),
                    industry:jQuery('div[lable="industry"]').next().children().eq(0).text(),
                    level:jQuery('div[lable="level"]').next().children().eq(0).text(),
                    address:jQuery('div[lable="address"]').next().children().eq(0).val(),
                    nexttime:jQuery('div[lable="nexttime"]').next().children().eq(0).val(),
                    remark:jQuery('div[lable="remark"]').next().children().eq(0).val()
                };

                _self.actions.saveClue( filter, function (json) {
                    if(json.type=="success"){
                        Showbo.Msg.alert('保存成功!');
                    }
                    setTimeout(function(){
                        jQuery("#notifyEdit").remove();
                        if(jQuery(".mask").length>0){
                            jQuery(".mask").attr("style",'left: 0px; top: 0px; width: 100%; overflow: hidden; position: absolute; z-index: 500000; background-color: rgb(255, 255, 255)');
                            jQuery(".mask").attr("class","");
                        }
                    },200);
                }.bind(_self));
            }
        });
        jQuery(".inline-input[notempty='true']").blur( function () {
            if(jQuery(this).attr("stype")!="datetime"){
                if(jQuery(this).val()=="" && jQuery(this).text()==""){
                    var nameStr = jQuery(this).parent().prev().text()+'不能为空';
                    if(jQuery(this).nextAll(".empError").length>0)jQuery(this).nextAll(".empError").remove();
                    jQuery(this).parent().append('<div class="empError" style="color:#f56c6c;padding: 0;line-height: 1;">'+nameStr+'</div>');
                }else{
                    if(jQuery(this).nextAll(".empError").length>0)jQuery(this).nextAll(".empError").remove();
                }
            }
        });

    },
    loadTimeContainer: function(stime){
        jQuery("#"+stime).ymdateplugin({
            showTimePanel: true
        });
    },
    getItemTemplate: function( lp ){
        _self = this;
        return {
            name: {
                text: lp.name,
                type: "text",
                //attr : {placeholder:lp.name},
                notEmpty:true,
                value:this.customerData && this.customerData.customername?this.customerData.customername:""
            },
            source:{
                type: "text",
                notEmpty:true,
                text: lp.source
            },
            telephone: {
                type: "text",
                notEmpty:true,
                text: lp.telephone
            },
            cellphone: {
                notEmpty:true,
                text: lp.cellphone
            },
            industry:{
                type: "select",
                text: lp.industry,
                notEmpty:true,
                value:this.app.lp.clue.industry.value
            },
            level:{
                type: "select",
                text: lp.level,
                notEmpty:true,
                value:this.app.lp.clue.level.value
            },
            address: {
                text: lp.address,
                name:"address",
                notEmpty:true,
                type: "text"
            },
            nexttime: {
                text: lp.nexttime,
                name:"nexttime",
                notEmpty:true,
                attr : {id:"nexttime"},
                type: "datetime"
            },
            remark: {
                text: lp.remark,
                name:"remark",
                type: "textarea"
            }
        }
    },

    _ok: function (data, callback) {

        var saveDataStr = "";
        for ( i in this.data){
            saveDataStr = saveDataStr+"'"+i+"':'"+this.data[i]+"',";
        }
        debugger;
        saveDataStr =  "'{"+saveDataStr.replace(/'/g, '"')+"}'";
        debugger;
        //var saveData = JSON.parse(saveDataStr);
        var saveData = eval('(' + saveDataStr.substring(1,saveDataStr.length - 1) + ')');
        debugger;
        //alert(JSON.stringify(saveData))
        this.app.createShade();
        this.actions.saveClue(saveData,function(json){
            this.app.destroyShade();
            this.app.notice(this.lp.saveSuccess,"success");
            this.close();
            this.fireEvent("reloadView",json);
        }.bind(this),function(xhr,text,error){
            this.app.showErrorMessage(xhr,text,error);
            this.app.destroyShade();
        }.bind(this));
    }
});