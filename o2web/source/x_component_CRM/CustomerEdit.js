MWF.xApplication.CRM.AddressExplorer={};
MWF.xApplication.CRM.CustomerEdit = new Class({
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
        this.lp = this.app.lp.customer.customerEdit;
        this.path = "/x_component_CRM/$CustomerEdit/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};
        this.actions = actions;
    },
    load: function () {
        this.loadResource(function(){
            this.appArea = jQuery("body").children(":first");
            this.createForm();
            debugger
        }.bind(this))

    },
    loadResource: function ( callback ) {
        if(callback)callback();
    },
    createForm:function(){
        debugger
        _self = this;
        jQuery(_self.appArea).next().attr("style","");
        jQuery(_self.appArea).next().attr("class","mask");
        var section_header = '<div class="section-header"><div class="section-mark" style="border-left-color: rgb(70, 205, 207);"></div> '+
            '<div data-v-ec8f8850="" class="section-title">基本信息</div></div>';
        var itemTemplateObject = _self.lp;
        var section_conent = '<div class="section-conent">';
        debugger
        for ( i in itemTemplateObject){
            var stype = itemTemplateObject[i].type;
            var notEmpty = itemTemplateObject[i].notEmpty?itemTemplateObject[i].notEmpty:"false";
            var innerHtml = '<input type="text" class="inline-input" name="'+i+'" id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'">';
            if(stype=="textarea"){
                innerHtml =  '<textarea rows="6" class="el-textarea__inner"  id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'"  style="resize: none; min-height: 30.6px;"></textarea>';
            }
            if(stype=="select" || stype=="hide"){
                innerHtml = '<div class="inline-input" style="display: inline-block;cursor:pointer;"  id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'" ></div><div class="el-icon-arrow-down el-icon--right" style="margin-left: -20px; display: inline-block;"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div>';
            }
            if(stype=="map"){
                innerHtml = '<div class="setMap" id="setMap"' +' stype="'+stype+'"></div>';
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
            "新建客户",
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
                if(stype=="select" || stype=="hide"){
                    var selectObjects = _self.app.lp.customer;
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
                                    debugger
                                    jQuery(cobj).text(jQuery(this).text());
                                    jQuery("[tid='"+jQuery(cobj).attr("id")+"']").toggle(100);
                                });
                            }
                        }
                    }

                }
                if(stype=="map"){
                    _self.loadMap();
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
                    customername:jQuery('div[lable="customername"]').next().children().eq(0).val(),
                    level:jQuery('div[lable="level"]').next().children().eq(0).text(),
                    industry:jQuery('div[lable="industry"]').next().children().eq(0).text(),
                    source:jQuery('div[lable="source"]').next().children().eq(0).text(),
                    dealstatus:jQuery('div[lable="dealstatus"]').next().children().eq(0).text(),
                    telephone:jQuery('div[lable="telephone"]').next().children().eq(0).val(),
                    cellphone:jQuery('div[lable="cellphone"]').next().children().eq(0).val(),
                    website:jQuery('div[lable="website"]').next().children().eq(0).val(),
                    location:jQuery('div[lable="detailaddress"]').next().children().eq(0).attr("location"),
                    detailaddress:jQuery('div[lable="detailaddress"]').next().children().eq(0).val(),
                    lng:jQuery('div[lable="detailaddress"]').next().children().eq(0).attr("lng"),
                    lat:jQuery('div[lable="detailaddress"]').next().children().eq(0).attr("lat"),
                    province:jQuery('div[lable="detailaddress"]').next().children().eq(0).attr("province"),
                    city:jQuery('div[lable="detailaddress"]').next().children().eq(0).attr("city"),
                    nexttime:jQuery('div[lable="nexttime"]').next().children().eq(0).val(),
                    remark:jQuery('div[lable="remark"]').next().children().eq(0).val(),
                };
                debugger
                _self.actions.saveCustomer( filter, function (json) {
                    debugger
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
    loadMap: function(){
        _self = this;
        this.mapDiv = jQuery("#setMap")[0];
        jQuery(".section-conent").css("height","700px");
        if(this.mapDiv)this.mapDiv.empty();
        if(this.addressModule) delete this.addressModule;
        MWF.xDesktop.requireApp("CRM", "AddressExplorer", function(){
            this.addressModule = new MWF.xApplication.CRM.AddressExplorer(this.mapDiv,this,this.actions,{});
            this.addressModule.load();
        }.bind(this))
    },
    getItemTemplate: function( lp ){
        _self = this;
        return {
            customername: {
                text: lp.customername,
                type: "text",
                //attr : {placeholder:lp.name},
                notEmpty:true,
                value:this.customerData && this.customerData.customername?this.customerData.customername:""
            },
            level:{
                type: "select",
                notEmpty:true,
                text: lp.level,
                value:this.app.lp.customer.level.value
            },
            industry: {
                type: "select",
                notEmpty:true,
                text: lp.industry,
                value:this.app.lp.customer.industry.value
            },
            source: {
                type: "select",
                notEmpty:true,
                text: lp.source,
                value:this.app.lp.customer.source.value
            },
            /*dealstatus:{
                type: "hide",
                text: lp.dealstatus,
                value:this.app.lp.customer.dealstatus.value
            },*/
            telephone:{
                type: "text",
                notEmpty:true,
                text: lp.telephone,
                value:this.app.lp.clue.level.value
            },
            website: {
                text:lp.website,
                type: "text"
            },
            nexttime: {
                text:lp.nexttime,
                notEmpty:true,
                attr : {id:"nexttime"},
                type: "datetime"
            },
            cellphone: {
                text:lp.cellphone,
                type: "text"
            },
            detailaddress: {
                text:lp.detailaddress,
                type: "text"
            },
            remark: {
                text:lp.remark,
                type: "textarea"
            },
            location: {
                text:lp.location,
                type: "map"
            }
        }
    }


});