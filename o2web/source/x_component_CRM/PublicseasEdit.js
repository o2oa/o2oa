MWF.xApplication.CRM.AddressExplorer={};
MWF.xApplication.CRM.PublicseasEdit = new Class({
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
        this.lp = this.app.lp.publicseas.publicseasEdit;
        this.path = "/x_component_CRM/$PublicseasEdit/";
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
        /*var baseUrls = [
            "/x_component_CRM/$Template/assets/js/jquery.min.js",
        ];
        var fullcalendarUrl = "/x_component_CRM/$Template/assets/js/notifyme.js";
        var confirmUrl = "/x_component_CRM/$Template/assets/js/showBo.js";
        //var confirmUrl = "/x_component_CRM/$Main/laydate/laydate.js";
        COMMON.AjaxModule.loadCss("/x_component_CRM/$Template/assets/css/notifyme.css",function(){
            COMMON.AjaxModule.load(baseUrls, function(){
                jQuery.noConflict();
                COMMON.AjaxModule.load(fullcalendarUrl, function(){
                    COMMON.AjaxModule.load(confirmUrl, function(){
                        if(callback)callback();
                    }.bind(this))
                }.bind(this));
            }.bind(this))
        }.bind(this));

        COMMON.AjaxModule.loadCss("/x_component_CRM/$Template/date/css/jquery-ui.css",function(){
            COMMON.AjaxModule.load(baseUrls, function(){
                jQuery.noConflict();
                COMMON.AjaxModule.load("/x_component_CRM/$Template/date/jquery-ym-datePlugin-0.1.js", function(){
                    //if(callback)callback();
                    /!* COMMON.AjaxModule.load("/x_component_CRM/$Template/date/js/datepicker.all.js", function(){
                     if(callback)callback();
                     }.bind(this))*!/
                }.bind(this));
            }.bind(this))
        }.bind(this));*/
    },
    createForm:function(){
        debugger
        _self = this;
        jQuery(_self.appArea).next().attr("style","");
        jQuery(_self.appArea).next().attr("class","mask");
        var section_header = '<div class="section-header"><div class="section-mark" style="border-left-color: rgb(70, 205, 207);"></div> '+
            '<div data-v-ec8f8850="" class="section-title">基本信息</div></div>';
        var itemTemplateObject = _self.getItemTemplate(_self.lp );
        var section_conent = '<div class="section-conent">';
        debugger
        for ( i in itemTemplateObject){
            var stype = itemTemplateObject[i].type;
            var innerHtml = '<input type="text" class="inline-input" name="'+i+'" id="'+i+'" stype="'+stype+'">';
            if(stype=="textarea"){
                innerHtml =  '<textarea rows="6" class="el-textarea__inner"  id="'+i+'" stype="'+stype+'"  style="resize: none; min-height: 30.6px;"></textarea>';
            }
            if(stype=="select" || stype=="hide"){
                innerHtml = '<div class="inline-input" style="display: inline-block;cursor:pointer;"  id="'+i+'" stype="'+stype+'" ></div><div class="el-icon-arrow-down el-icon--right" style="margin-left: -20px; display: inline-block;"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div>';
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
            _self.options.title,
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
                    var selectObjects = _self.app.lp.publicseas;
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
            Publicseasname: {
                text: lp.publicseasname,
                type: "text",
                //attr : {placeholder:lp.name},
                notEmpty:true,
                value:this.publicseasData && this.publicseasData.customername?this.publicseasData.customername:""
            },
            level:{
                type: "select",
                text: lp.level,
                value:this.app.lp.customer.level.value
            },
            industry: {
                type: "select",
                text: lp.industry,
                value:this.app.lp.customer.industry.value
            },
            source: {
                type: "select",
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
                text: lp.telephone,
                value:this.app.lp.clue.level.value
            },
            website: {
                text:lp.website,
                type: "text"
            },
            nexttime: {
                text:lp.nexttime,
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