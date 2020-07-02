//projectData 项目业务数据
MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.ProjectSetting = new Class({
    Extends: MWF.xApplication.TeamWork.Common.Popup,
    options:{
        "closeByClickMask" : false
    },

    open: function (e) {
        //设置css 和 lp等
        var css = this.css;
        this.cssPath = "../x_component_TeamWork/$ProjectSetting/"+this.options.style+"/css.wcss";
        this._loadCss();
        if(css) this.css = Object.merge(  css, this.css );

        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.ProjectAction;

        this.lp = this.app.lp.projectSetting;

        this.fireEvent("queryOpen");
        this.isNew = false;
        this.isEdited = false;
        this._open();
        this.fireEvent("postOpen");
    },
    _createTableContent: function () {
        this.projectInfor(function(rs){
            this.groups = rs.groups;
            this.groupsArr = [];
            this.projectSettingTop = new Element("div.projectSettingTop",{styles:this.css.projectSettingTop}).inject(this.formTableArea);
            this.projectSettingTopText = new Element("div.projectSettingTopText",{styles:this.css.projectSettingTopText,text:this.lp.title}).inject(this.projectSettingTop);
            this.projectSettingTopClose = new Element("div.projectSettingTopClose",{styles:this.css.projectSettingTopClose}).inject(this.projectSettingTop);
            this.projectSettingTopClose.addEvents({
                click:function(){this.close()}.bind(this)
            });

            this.projectSettingContent = new Element("div.projectSettingContent",{styles:this.css.projectSettingContent}).inject(this.formTableArea);
            this.projectSettingNaviLayout = new Element("div.projectSettingNaviLayout",{styles:this.css.projectSettingNaviLayout}).inject(this.projectSettingContent);
            this.projectSettingLayout = new Element("div.projectSettingLayout",{styles:this.css.projectSettingLayout}).inject(this.projectSettingContent);

            this.createNavi();

        }.bind(this));

    },
    createNavi:function(){
        var _self = this;
        this.projectSettingNaviLayout.empty();

        //概况
        this.naviGeneral = new Element("div.naviGeneral",{styles:this.css.naviItem}).inject(this.projectSettingNaviLayout);
        this.naviGeneralHover = new Element("div.naviItemHover",{styles:this.css.naviItemHover}).inject(this.naviGeneral);
        this.naviGeneralHover.setStyles({"height":"58px","margin-top":"2px"});
        this.naviGeneralIcon = new Element("div.naviItemIcon",{styles:this.css.naviItemIcon}).inject(this.naviGeneral);

        this.naviGeneralIcon.setStyles({"background-image":"url(../x_component_TeamWork/$ProjectSetting/default/icon/icon_general.png)"});
        this.naviGeneralText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:this.lp.projectGeneral}).inject(this.naviGeneral);
        this.naviGeneral.addEvents({
            mouseover:function(){
                if(_self.curNavi == "general") return;
                this.getElements(".naviItemIcon").setStyles({"background-image":"url(../x_component_TeamWork/$ProjectSetting/default/icon/icon_general_click.png)"});
                this.getElements(".naviItemText").setStyles({"color":"#4a90e2"});
            },
            mouseout:function(){
                if(_self.curNavi == "general") return;
                this.getElement(".naviItemIcon").setStyles({"background-image":"url(../x_component_TeamWork/$ProjectSetting/default/icon/icon_general.png)"});
                this.getElement(".naviItemText").setStyles({"color":""});
            },
            click:function(){
                _self.curNavi = "general";
                _self.changeNavi(this);
                _self.loadGeneral();
            }
        });

        //项目详情
        this.naviDetail = new Element("div.naviGeneral",{styles:this.css.naviItem}).inject(this.projectSettingNaviLayout);
        this.naviDetailHover = new Element("div.naviItemHover",{styles:this.css.naviItemHover}).inject(this.naviDetail);
        this.naviDetailHover.setStyles({"height":"58px","margin-top":"2px"});
        this.naviDetailIcon = new Element("div.naviItemIcon",{styles:this.css.naviItemIcon}).inject(this.naviDetail);
        this.naviDetailIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_detail.png)"});
        this.naviDetailText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:this.lp.projectDetail}).inject(this.naviDetail);
        this.naviDetail.addEvents({
            mouseover:function(){
                if(_self.curNavi == "detail") return;
                this.getElements(".naviItemIcon").setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_detail_click.png)"});
                this.getElements(".naviItemText").setStyles({"color":"#4a90e2"});
            },
            mouseout:function(){
                if(_self.curNavi == "detail") return;
                this.getElement(".naviItemIcon").setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_detail.png)"});
                this.getElement(".naviItemText").setStyles({"color":""});
            },
            click:function(){
                _self.curNavi = "detail";
                _self.changeNavi(this);
                _self.loadDetail();
            }
        });

        //权限设置
        this.naviAccess = new Element("div.naviAccess",{styles:this.css.naviItem}).inject(this.projectSettingNaviLayout);
        this.naviAccessHover = new Element("div.naviItemHover",{styles:this.css.naviItemHover}).inject(this.naviAccess);
        this.naviAccessHover.setStyles({"height":"58px","margin-top":"2px"});
        this.naviAccessIcon = new Element("div.naviItemIcon",{styles:this.css.naviItemIcon}).inject(this.naviAccess);
        this.naviAccessIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_access.png)"});
        this.naviAccessText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:this.lp.projectAccess}).inject(this.naviAccess);
        this.naviAccess.addEvents({
            mouseover:function(){
                if(_self.curNavi == "access") return;
                this.getElements(".naviItemIcon").setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_access_click.png)"});
                this.getElements(".naviItemText").setStyles({"color":"#4a90e2"});
            },
            mouseout:function(){
                if(_self.curNavi == "access") return;
                this.getElement(".naviItemIcon").setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_access.png)"});
                this.getElement(".naviItemText").setStyles({"color":""});
            },
            click:function(){
                _self.curNavi = "access";
                _self.changeNavi(this);
                _self.loadAccess();
            }
        });

        //更多设置
        this.naviMore = new Element("div.naviMore",{styles:this.css.naviItem}).inject(this.projectSettingNaviLayout);
        this.naviMoreHover = new Element("div.naviItemHover",{styles:this.css.naviItemHover}).inject(this.naviMore);
        this.naviMoreHover.setStyles({"height":"58px","margin-top":"2px"});
        this.naviMoreIcon = new Element("div.naviItemIcon",{styles:this.css.naviItemIcon}).inject(this.naviMore);
        this.naviMoreIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_more.png)"});
        this.naviMoreText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:this.lp.projectMore}).inject(this.naviMore);
        this.naviMore.addEvents({
            mouseover:function(){
                if(_self.curNavi == "more") return;
                this.getElements(".naviItemIcon").setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_more_click.png)"});
                this.getElements(".naviItemText").setStyles({"color":"#4a90e2"});
            },
            mouseout:function(){
                if(_self.curNavi == "more") return;
                this.getElement(".naviItemIcon").setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_more.png)"});
                this.getElement(".naviItemText").setStyles({"color":""});
            },
            click:function(){
                _self.curNavi = "more";
                _self.changeNavi(this);
                _self.loadMore();
            }
        });

        this.naviGeneral.click()
        /*

        //自定义字段
        this.naviCustom = new Element("div.naviCustom",{styles:this.css.naviItem}).inject(this.projectSettingNaviLayout);
        this.naviCustomHover = new Element("div.naviItemHover",{styles:this.css.naviItemHover}).inject(this.naviCustom);
        this.naviCustomIcon = new Element("div.naviItemIcon",{styles:this.css.naviItemIcon}).inject(this.naviCustom);
        this.naviCustomIcon.setStyles({"background-image":"url(../x_component_TeamWork/$ProjectSetting/default/icon/icon_custom.png)"});
        this.naviCustomText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:this.lp.customField}).inject(this.naviCustom);
        this.naviCustom.addEvents({
            mouseover:function(){
                if(_self.curNavi == "custom") return;
                this.getElements(".naviItemIcon").setStyles({"background-image":"url(../x_component_TeamWork/$ProjectSetting/default/icon/icon_custom_click.png)"});
                this.getElements(".naviItemText").setStyles({"color":"#4a90e2"});
            },
            mouseout:function(){
                if(_self.curNavi == "custom") return;
                this.getElement(".naviItemIcon").setStyles({"background-image":"url(../x_component_TeamWork/$ProjectSetting/default/icon/icon_custom.png)"});
                this.getElement(".naviItemText").setStyles({"color":""});
            },
            click:function(){
                _self.curNavi = "custom";
                _self.changeNavi(this);
                _self.loadCustom()
            }
        });

        this.naviCustom = new Element("div.naviCustom",{styles:this.css.naviItem}).inject(this.projectSettingNaviLayout);
        this.naviCustomHover = new Element("div.naviItemHover",{styles:this.css.naviItemHover}).inject(this.naviCustom);
        this.naviCustomIcon = new Element("div.naviItemIcon",{styles:this.css.naviItemIcon}).inject(this.naviCustom);
        this.naviCustomIcon.setStyles({"background-image":"url(../x_component_TeamWork/$ProjectSetting/default/icon/icon_custom.png)"});
        this.naviCustomText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:"任务设置"}).inject(this.naviCustom);
        this.naviCustom.addEvents({
            mouseover:function(){
                if(_self.curNavi == "custom") return;
                this.getElements(".naviItemIcon").setStyles({"background-image":"url(../x_component_TeamWork/$ProjectSetting/default/icon/icon_custom_click.png)"});
                this.getElements(".naviItemText").setStyles({"color":"#4a90e2"});
            },
            mouseout:function(){
                if(_self.curNavi == "custom") return;
                this.getElement(".naviItemIcon").setStyles({"background-image":"url(../x_component_TeamWork/$ProjectSetting/default/icon/icon_custom.png)"});
                this.getElement(".naviItemText").setStyles({"color":""});
            },
            click:function(){
                _self.curNavi = "custom";
                _self.changeNavi(this);
                _self.loadCustom()
            }
        });

        this.naviCustom = new Element("div.naviCustom",{styles:this.css.naviItem}).inject(this.projectSettingNaviLayout);
        this.naviCustomHover = new Element("div.naviItemHover",{styles:this.css.naviItemHover}).inject(this.naviCustom);
        this.naviCustomIcon = new Element("div.naviItemIcon",{styles:this.css.naviItemIcon}).inject(this.naviCustom);
        this.naviCustomIcon.setStyles({"background-image":"url(../x_component_TeamWork/$ProjectSetting/default/icon/icon_custom.png)"});
        this.naviCustomText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:"更多设置"}).inject(this.naviCustom);
        this.naviCustom.addEvents({
            mouseover:function(){
                if(_self.curNavi == "custom") return;
                this.getElements(".naviItemIcon").setStyles({"background-image":"url(../x_component_TeamWork/$ProjectSetting/default/icon/icon_custom_click.png)"});
                this.getElements(".naviItemText").setStyles({"color":"#4a90e2"});
            },
            mouseout:function(){
                if(_self.curNavi == "custom") return;
                this.getElement(".naviItemIcon").setStyles({"background-image":"url(../x_component_TeamWork/$ProjectSetting/default/icon/icon_custom.png)"});
                this.getElement(".naviItemText").setStyles({"color":""});
            },
            click:function(){
                _self.curNavi = "custom";
                _self.changeNavi(this);
                _self.loadCustom()
            }
        });
        */

    },
    changeNavi:function(obj){
        this.projectSettingNaviLayout.getElements(".naviItemHover").setStyles({"background-color":"#ffffff"});
        this.projectSettingNaviLayout.getElements(".naviItemText").setStyles({"color":""});
        this.projectSettingNaviLayout.getElements(".naviItemIcon").each(function(dom){
            var bgurl = dom.getStyle("background-image");
            if(bgurl.indexOf("_click")>-1){
                bgurl = bgurl.replace("_click","");
            }
            dom.setStyles({"background-image":bgurl});
        }.bind(this));

        obj.getElement(".naviItemHover").setStyles({"background-color":"#4a90e2"});
        obj.getElement(".naviItemIcon").setStyles({"background-image":obj.getElement(".naviItemIcon").getStyle("background-image").replace(".png","_click.png")});
        obj.getElement(".naviItemText").setStyles({"color":"#4a90e2"});
    },
    loadCustom:function(){
        this.projectSettingLayout.empty();

        this.customTopLayout = new Element("div.customTopLayout",{styles:this.css.customTopLayout}).inject(this.projectSettingLayout);
        this.customTopText = new Element("div.customTopText",{styles:this.css.customTopText,text:this.lp.customTip}).inject(this.customTopLayout);
        this.customTopNew = new Element("div.customTopNew",{styles:this.css.customTopNew,text:this.lp.customNew}).inject(this.customTopLayout);
        this.customTopNew.addEvents({
            mouseover:function(){this.setStyles({"opacity":"0.7"})},
            mouseout:function(){this.setStyles({"opacity":"1"})},
            click:function(){
                MWF.xDesktop.requireApp("TeamWork", "ExtField", function(){
                    var pc = new MWF.xApplication.TeamWork.ExtField(this,this.container, this.customTopNew, this.app, {projectId:this.data.id}, {
                        axis : "x",
                        position : { //node 固定的位置
                            x : "left",
                            y : "middle"
                        },
                        nodeStyles : {
                            "min-width":"200px",
                            "width":"300px",
                            "padding":"2px",
                            "border-radius":"5px",
                            "box-shadow":"0px 0px 4px 0px #999999",
                            "z-index" : "201"
                        },
                        onPostLoad:function(){
                            pc.node.setStyles({"opacity":"0","top":(pc.node.getStyle("top").toInt())+"px"});
                            var fx = new Fx.Tween(pc.node,{duration:400});
                            fx.start(["opacity"] ,"0", "1");
                        },
                        onClose:function(rd){
                            if(!rd) return;
                            if(rd.value && rd.value !=""){
                                this.chatTextarea.set("value",this.chatTextarea.get("value")+"["+rd.value+"]")
                            }
                        }.bind(this)
                    });
                    pc.load();
                }.bind(this));
            }.bind(this)
        });

        this.customContent = new Element("div.customContent",{styles:this.css.customContent}).inject(this.projectSettingLayout);
        this.createExtFieldList();
    },
    createExtFieldList:function(){
        this.customContent.empty();
        this.app.setLoading(this.customContent);
        this.rootActions.ProjectExtFieldReleAction.listFieldsWithProject(this.data.id,function(json){
            this.customContent.empty();
            json.data.each(function(data){
                this.createExtFieldItem(data);
            }.bind(this));
        }.bind(this))
    },
    createExtFieldItem:function(data){
        var _self = this;
        var customExtItem = new Element("div.customExtItem",{styles:this.css.customExtItem,id:data.id}).inject(this.customContent);
        customExtItem.addEvents({
            mouseover:function(){
                this.setStyles({"background-color":"#f5f5f5"});
                customExitItemRemove.show();
                customExitItemEdit.show();
            },
            mouseout:function(){
                this.setStyles({"background-color":""});
                customExitItemRemove.hide();
                customExitItemEdit.hide();
            }
        });
        var customExtItemTop = new Element("div.customExtItemTop",{styles:this.css.customExtItemTop}).inject(customExtItem);

        var customExtName = new Element("div.customExtName",{styles:this.css.customExtName,text:data.displayName}).inject(customExtItemTop);
        var customExitItemRemove = new Element("div.customExitItemRemove",{styles:this.css.customExitItemAction,text:this.lp.remove}).inject(customExtItemTop);
        customExitItemRemove.addEvents({
            click:function(e){
                _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){
                    _self.actions.projectExtFieldRemove(data.id,function(json){
                        customExtItem.destroy();
                        this.close();
                    }.bind(this))
                },function(){
                    this.close();
                });
            }
        });
        var customExitItemEdit = new Element("div.customExitItemEdit",{styles:this.css.customExitItemAction,text:this.lp.edit}).inject(customExtItemTop);
        customExitItemEdit.addEvents({
            click:function(){
                MWF.xDesktop.requireApp("TeamWork", "ExtField", function(){
                    var pc = new MWF.xApplication.TeamWork.ExtField(this,this.container, this.customTopNew, this.app, {projectId:this.data.id,id:data.id}, {
                        axis : "x",
                        position : { //node 固定的位置
                            x : "left",
                            y : "middle"
                        },
                        nodeStyles : {
                            "min-width":"200px",
                            "width":"300px",
                            "padding":"2px",
                            "border-radius":"5px",
                            "box-shadow":"0px 0px 4px 0px #999999",
                            "z-index" : "201"
                        },
                        onPostLoad:function(){
                            pc.node.setStyles({"opacity":"0","top":(pc.node.getStyle("top").toInt())+"px"});
                            var fx = new Fx.Tween(pc.node,{duration:400});
                            fx.start(["opacity"] ,"0", "1");
                        },
                        onClose:function(rd){
                            if(!rd) return;
                            if(rd.value && rd.value !=""){
                                this.chatTextarea.set("value",this.chatTextarea.get("value")+"["+rd.value+"]")
                            }
                        }.bind(this)
                    });
                    pc.load();
                }.bind(this));
            }.bind(this)
        });

        var customExtValueList = new Element("div.customExtValueList",{styles:this.css.customExtValueList}).inject(customExtItem);
        new Element("span.customExtCreator",{styles:this.customExtText,text:this.lp.description+"："+data.description}).inject(customExtValueList);
        customExtValueList = new Element("div.customExtValueList",{styles:this.css.customExtValueList}).inject(customExtItem);
        new Element("span.customExtCreator",{styles:this.customExtText,text:this.lp.option + "："+data.optionsData}).inject(customExtValueList);

    },
    loadGeneral:function(){
        this.projectSettingLayout.empty();
        this.groupsArr = [];
        this.projectSettingBgText = new Element("div.projectSettingBgText",{styles:this.css.projectSettingBgText,"text":this.lp.projectSettingBgText}).inject(this.projectSettingLayout);
        this.projectSettingBgContainer = new Element("div.projectSettingBgContainer",{styles:this.css.projectSettingBgContainer}).inject(this.projectSettingLayout);
        this.projectSettingBgImg = new Element("div.projectSettingBgImg",{styles:this.css.projectSettingBgImg}).inject(this.projectSettingBgContainer);
        if(this.data.icon && this.data.icon!=""){
            this.projectSettingBgImg.setStyles({
                "background-image":"url('"+MWF.xDesktop.getImageSrc( this.data.icon )+"')"
            });
        }

        this.projectSettingBgUpload = new Element("div.projectSettingBgUpload",{styles:this.css.projectSettingBgUpload,text:this.lp.upload}).inject(this.projectSettingBgContainer);
        this.projectSettingBgUpload.addEvents({
            click:function(){
                var data = {};
                MWF.xDesktop.requireApp("TeamWork", "UploadImage", function(){
                    var ui = new MWF.xApplication.TeamWork.UploadImage(this, data, {
                        documentId : this.data.id ||"",
                        onPostOk : function( id ){

                            this.data.icon = id;
                            this.projectSettingBgImg.setStyles({
                                "background-image":"url('"+MWF.xDesktop.getImageSrc( id )+"')"
                            });
                        }.bind(this)
                    });
                    ui.open()
                }.bind(this));
            }.bind(this)
        });
        this.projectSettingContainer = new Element("div.projectSettingContainer",{styles:this.css.projectSettingContainer}).inject(this.projectSettingLayout);
        this.projectSettingTitleContainer = new Element("div.projectSettingTitleContainer",{styles:this.css.projectSettingTitleContainer}).inject(this.projectSettingContainer);
        this.projectSettingTitleText = new Element("div.projectSettingTitleText",{styles:this.css.projectSettingTitleText,text:this.lp.projectTitle}).inject(this.projectSettingTitleContainer);
        this.projectSettingTitleDiv = new Element("div.projectSettingTitleDiv",{styles:this.css.projectSettingTitleDiv}).inject(this.projectSettingTitleContainer);
        this.projectSettingTitleIn = new Element("input.projectSettingTitleIn",{styles:this.css.projectSettingTitleIn,value:this.projectData.title || ""}).inject(this.projectSettingTitleDiv);
        this.projectSettingTitleIn.addEvents({
            focus:function(){
                this.projectSettingTitleIn.setStyles({"border":"1px solid #4A90E2"});
            }.bind(this),
            blur:function(){
                this.projectSettingTitleIn.setStyles({"border":"1px solid #A6A6A6"});
            }.bind(this)
        });

        // this.projectSettingContainer = new Element("div.projectSettingContainer",{styles:this.css.projectSettingContainer}).inject(this.formTableArea);
        this.projectSettingGroupContainer = new Element("div.projectSettingGroupContainer",{styles:this.css.projectSettingGroupContainer}).inject(this.projectSettingContainer);
        this.projectSettingGroupText = new Element("div.projectSettingGroupText",{styles:this.css.projectSettingGroupText,text:this.lp.projectGroup}).inject(this.projectSettingGroupContainer);
        this.projectSettingGroupDiv = new Element("div.projectSettingGroupDiv",{styles:this.css.projectSettingGroupDiv}).inject(this.projectSettingGroupContainer);
        this.projectSettingGroupDiv.addEvents({
            click:function(){
                var data = {groups:this.groups};
                MWF.xDesktop.requireApp("TeamWork", "GroupSelect", function(){
                    var gs = new MWF.xApplication.TeamWork.GroupSelect(this.container, this.projectSettingGroupDiv, this.app, data, {
                        axis : "y",
                        nodeStyles : {
                            "min-width":"190px",
                            "z-index" : "102"
                        },
                        onClose:function(d){
                            this.projectSettingGroupDiv.setStyles({"border":"1px solid #A6A6A6"});
                            if(d){
                                this.rootActions.ProjectGroupAction.listWithIds({ids:d},function(json){
                                    this.groups = json.data;
                                    var tmp = [];
                                    json.data.each(function(ddd){
                                        tmp.push(ddd.name);
                                    });
                                    this.projectSettingGroupValue.set("text",tmp.join());
                                    this.projectSettingGroupValue.set("title",tmp.join())
                                }.bind(this));
                            }else{
                                this.groups = [];
                            }
                            //this.newProjectGroupValue.set("text",d)
                        }.bind(this)
                    });
                    gs.load();
                }.bind(this));
                this.projectSettingGroupDiv.setStyles({"border":"1px solid #4A90E2"});
            }.bind(this)
        });
        this.projectSettingGroupValue = new Element("div.projectSettingGroupValue",{styles:this.css.projectSettingGroupValue}).inject(this.projectSettingGroupDiv);
        this.projectSettingGroupIcon = new Element("div.projectSettingGroupIcon",{styles:this.css.projectSettingGroupIcon}).inject(this.projectSettingGroupDiv);
        if(this.groups){
            this.groups.each(function(data){
                this.groupsArr.push(data.name);
            }.bind(this));
        }
        this.projectSettingGroupValue.set("text",this.groupsArr.join());
        this.projectSettingGroupValue.set("title",this.groupsArr.join());

        this.projectSettingContainer = new Element("div.projectSettingContainer",{styles:this.css.projectSettingContainer}).inject(this.projectSettingLayout);
        this.projectSettingContainer.setStyles({"height":"120px"});
        this.projectSettingDesText = new Element("div.projectSettingDesText",{styles:this.css.projectSettingDesText,text:this.lp.projectDes}).inject(this.projectSettingContainer);

        this.projectSettingDesContainer = new Element("div.projectSettingDesContainer",{styles:this.css.projectSettingDesContainer}).inject(this.projectSettingContainer);
        this.projectSettingDesIn = new Element("textarea.projectSettingDesIn",{styles:this.css.projectSettingDesIn,value:this.projectData.description||""}).inject(this.projectSettingDesContainer);
        this.projectSettingDesIn.addEvents({
            focus:function(){
                this.projectSettingDesIn.setStyles({"border":"1px solid #4A90E2"});
            }.bind(this),
            blur:function(){
                this.projectSettingDesIn.setStyles({"border":"1px solid #A6A6A6"});
            }.bind(this)
        });

        this.projectSettingContainer = new Element("div.projectSettingContainer",{styles:this.css.projectSettingContainer}).inject(this.projectSettingLayout);
        this.projectSettingConfirm = new Element("div.projectSettingConfirm",{styles:this.css.projectSettingConfirm,text:this.lp.confirm}).inject(this.projectSettingContainer);
        this.projectSettingConfirm.addEvents({
            click:function(){
                if(this.projectSettingTitleIn.get("value").trim()=="") return;
                var groups = [];
                if(this.groups){
                    this.groups.each(function(d){
                        groups.push(d.id);
                    });
                }
                var data = {
                    "id":this.data.id,
                    "icon":this.data.icon || "",
                    "title":this.projectSettingTitleIn.get("value").trim(),
                    "description":this.projectSettingDesIn.get("value"),
                    "groups":groups
                };

                this.actions.save(data,function(json){
                    this.projectSettingLayout.empty();
                    this.app.setLoading(this.projectSettingLayout);
                    this.projectInfor(function(json){
                        this.loadGeneral(json);
                        if(this.explorer.currentListType == "block"){
                            this.explorer.loadSingleBlockItem(this.explorer.container.getElementById(json.id),json)
                        }else if(this.explorer.currentListType == "list"){
                            this.explorer.loadSingleListItem(this.explorer.container.getElementById(json.id),json)
                        }
                    }.bind(this));
                }.bind(this));
            }.bind(this)
        });
        // this.projectSettingClose = new Element("div.projectSettingClose",{styles:this.css.projectSettingClose,text:this.lp.close}).inject(this.projectSettingContainer);
        // this.projectSettingClose.addEvents({
        //     click:function(){
        //         this.close();
        //     }.bind(this)
        // });
        //this.projectSettingAction = new Element("div.projectSettingAction",{styles:this.css.projectSettingAction,text:this.lp.confirm}).inject(this.formTableArea);

    },
    loadDetail:function(){
        var _self = this;
        this.projectSettingLayout.empty();
        //var authTaskTitle = new Element("div.authTaskTitle",{styles:this.css.authTitle,text:this.lp.auth.task}).inject(this.projectSettingLayout);
        var tips = this.projectData.creatorPerson.split("@")[0] + " " + this.lp.projectDetails.at+this.projectData.createTime +this.lp.projectDetails.create;
        var detailTopContainer = new Element("div.detailTopContainer",{ styles: this.css.detailTopContainer, text:tips }).inject(this.projectSettingLayout);
        var detailStatTitle = new Element("div.detailStatTitle",{styles:this.css.detailStatTitle, text:this.lp.projectDetails.taskStat}).inject(this.projectSettingLayout);
        var detailStatContainer = new Element("div.detailStatContainer",{styles:this.css.detailStatContainer}).inject(this.projectSettingLayout);

        var detailStatTotal = new Element("div.detailStatTotal",{styles:this.css.detailStatTotal}).inject(detailStatContainer);
        new Element("div.detailStatTotalTitle",{styles:this.css.detailStatTotalTitle,text:this.lp.projectDetails.total}).inject(detailStatTotal);
        new Element("div.detailStatTotalCount",{styles:this.css.detailStatTotalCount,text:this.projectData.taskTotal}).inject(detailStatTotal);
        var tbar = new Element("div.detailStatTotalBar",{styles:this.css.detailStatTotalBar}).inject(detailStatTotal);
        tbar.setStyles({"background-color":"#95b9e4"});

        var detailStatProcess = new Element("div.detailStatTotal",{styles:this.css.detailStatTotal}).inject(detailStatContainer);
        new Element("div.detailStatTotalTitle",{styles:this.css.detailStatTotalTitle,text:this.lp.projectDetails.process}).inject(detailStatProcess);
        new Element("div.detailStatTotalCount",{styles:this.css.detailStatTotalCount,text:this.projectData.progressTotal}).inject(detailStatProcess);
        var pbar = new Element("div.detailStatTotalBar",{styles:this.css.detailStatTotalBar}).inject(detailStatProcess);
        pbar.setStyles({"background-color":"#ecedf4"});

        var detailStatCompleted = new Element("div.detailStatTotal",{styles:this.css.detailStatTotal}).inject(detailStatContainer);
        new Element("div.detailStatTotalTitle",{styles:this.css.detailStatTotalTitle,text:this.lp.projectDetails.complete}).inject(detailStatCompleted);
        new Element("div.detailStatTotalCount",{styles:this.css.detailStatTotalCount,text:this.projectData.completedTotal}).inject(detailStatCompleted);
        var cbar = new Element("div.detailStatTotalBar",{styles:this.css.detailStatTotalBar}).inject(detailStatCompleted);
        cbar.setStyles({"background-color":"#f1f9eb"});

        var detailStatOver = new Element("div.detailStatTotal",{styles:this.css.detailStatTotal}).inject(detailStatContainer);
        var detailStatTotalTitle = new Element("div.detailStatTotalTitle",{styles:this.css.detailStatTotalTitle,text:this.lp.projectDetails.over}).inject(detailStatOver);
        var detailStatTotalCount = new Element("div.detailStatTotalCount",{styles:this.css.detailStatTotalCount,text:this.projectData.overtimeTotal}).inject(detailStatOver);
        var detailStatTotalBar = new Element("div.detailStatTotalBar",{styles:this.css.detailStatTotalBar}).inject(detailStatOver);

    },
    loadAccess:function(){
        var _self = this;
        this.projectSettingLayout.empty();

        var authTaskTitle = new Element("div.authTaskTitle",{styles:this.css.authTitle,text:this.lp.auth.task}).inject(this.projectSettingLayout);
        authTaskTitle.setStyle("margin-top","20px");
        var authTaskContainer = new Element("div.authTaskContainer",{styles:this.css.authContainer}).inject(this.projectSettingLayout);
        //var authCommentTitle = new Element("div.authCommentTitle",{styles:this.css.authTitle,text:this.lp.auth.comment}).inject(this.projectSettingLayout);
        //authCommentTitle.setStyle("margin-top","20px");
        //var authCommentContainer = new Element("div.authCommentContainer",{styles:this.css.authContainer}).inject(this.projectSettingLayout);


        this.getProjectAuth(this.projectData.id,function(){
            //alert(JSON.stringify(this.projectAuthData))

            //创建任务
            //var taskCreateFlag = true;
            taskCreateFlag=this.projectAuthData.hasOwnProperty("taskCreate") ? this.projectAuthData.taskCreate:true;

            var newTaskContainer = new Element("div.authItemContainer",{styles:this.css.authItemContainer}).inject(authTaskContainer);
            var newTaskIcon = new Element("div.authItemIcon",{styles:this.css.authItemIcon}).inject(newTaskContainer);
            var newTaskTitle = new Element("div.authItemTitle",{styles:this.css.authItemTitle,text:this.lp.auth.taskCreate}).inject(newTaskContainer);

            if(!taskCreateFlag) newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_unselected.png)"});
            else newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_selected.png)"});

            var isChanged = false;
            newTaskContainer.addEvents({
                mouseenter:function(){
                    if(isChanged) return;
                    if(taskCreateFlag){
                        newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_selected_click.png)"});
                    }else{
                        newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_unselected_click.png)"});
                    }
                },
                mouseleave:function(){
                    if(isChanged) { isChanged = false ; return;}
                    if(taskCreateFlag){
                        newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_selected.png)"});
                    }else{
                        newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_unselected.png)"});
                    }
                },
                click:function(){
                    if(taskCreateFlag){
                        this.projectAuthData.taskCreate = false;
                        taskCreateFlag = false;
                        newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_unselected.png)"});
                    }else{
                        this.projectAuthData.taskCreate = true;
                        taskCreateFlag = true;
                        newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_selected.png)"});
                    }

                    this.saveProjectAuth();
                    isChanged = true;
                }.bind(this)
            });

            /*
            //允许评论
            commentFlag=this.projectAuthData.hasOwnProperty("comment") ? this.projectAuthData.comment:true;

            var newTaskContainer = new Element("div.authItemContainer",{styles:this.css.authItemContainer}).inject(authTaskContainer);
            var newTaskIcon = new Element("div.authItemIcon",{styles:this.css.authItemIcon}).inject(newTaskContainer);
            var newTaskTitle = new Element("div.authItemTitle",{styles:this.css.authItemTitle,text:this.lp.auth.taskCreate}).inject(newTaskContainer);

            if(!taskCreateFlag) newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_unselected.png)"});
            else newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_selected.png)"});

            var isChanged = false;
            newTaskContainer.addEvents({
                mouseenter:function(){
                    if(isChanged) return;
                    if(taskCreateFlag){
                        newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_selected_click.png)"});
                    }else{
                        newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_unselected_click.png)"});
                    }
                },
                mouseleave:function(){
                    if(isChanged) { isChanged = false ; return;}
                    if(taskCreateFlag){
                        newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_selected.png)"});
                    }else{
                        newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_unselected.png)"});
                    }
                },
                click:function(){
                    if(taskCreateFlag){
                        this.projectAuthData.taskCreate = false;
                        taskCreateFlag = false;
                        newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_unselected.png)"});
                    }else{
                        this.projectAuthData.taskCreate = true;
                        taskCreateFlag = true;
                        newTaskIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ProjectSetting/default/icon/icon_selected.png)"});
                    }

                    this.saveProjectAuth();
                    isChanged = true;
                }.bind(this)
            });
            */
        }.bind(this));

    },
    getProjectAuth:function(id,callback){
        this.rootActions.GlobalAction.projectConfigGetByProject(id,function(json){
            if(json.type == "error"){
                this.projectAuthData = {};
            }else{
                this.projectAuthData = json.data||{};
                //alert(JSON.stringify(json.data))
            }
            if(callback)callback(json)
        }.bind(this),function(json){
            this.projectAuthData = {};
            if(callback)callback(json)
        }.bind(this));
    },
    loadAccessItem:function(container,data){

    },
    saveProjectAuth:function(callback){
        var data = {
            id:this.projectAuthData.id||"",
            project:this.projectData.id,
            taskCreate:this.projectAuthData.taskCreate || false,
            taskCopy:this.projectAuthData.taskCopy || false,
            taskRemove:this.projectAuthData.taskRemove || false,
            laneCreate:this.projectAuthData.laneCreate || false,
            laneEdit:this.projectAuthData.laneEdit || false,
            laneRemove:this.projectAuthData.laneRemove || false,
            attachmentUpload:this.projectAuthData.attachmentUpload || false,
            comment:this.projectAuthData.comment || false

        };
        this.rootActions.GlobalAction.projectConfigSave(data,function(json){
            this.rootActions.GlobalAction.projectConfigGet(json.data.id,function(d){
                this.projectAuthData = d.data;
                if(callback)callback(json);
            }.bind(this))

        }.bind(this))

    },
    loadMore:function(){
        var _self = this;
        this.projectSettingLayout.empty();
        this.moreActionTitle = new Element("div.moreActionTitle",{styles:this.css.moreActionTitle,text:this.lp.moreActionTitle}).inject(this.projectSettingLayout);
        this.moreActionDescription = new Element("div.moreActionDescription",{styles:this.css.moreActionDescription,text:this.lp.moreActionDescription}).inject(this.projectSettingLayout);
        this.moreActionContainer = new Element("div.moreActionContainer",{ styles:this.css.moreActionContainer }).inject(this.projectSettingLayout);
        if(this.projectData.completed){
            this.moreActionUnComplete = new Element("div.moreActionUnComplete",{styles:this.css.moreActionUnComplete, text:this.lp.moreActionUnComplete}).inject(this.moreActionContainer);
            this.moreActionUnComplete.addEvents({
                mouseover:function(){
                    this.setStyles({"background-color":"#ffeded"})
                },
                mouseout:function(){
                    this.setStyles({"background-color":"#ffffff"})
                },
                click:function(e){
                    _self.app.confirm("warn",e,_self.lp.moreActionUnComplete,_self.lp.moreActionUnCompleteTips,450,100,function(){
                        _self.rootActions.ProjectAction.completeProject(_self.projectData.id,{completed:false},function(){
                            this.close();
                            _self.close({"action":"reload"});
                        }.bind(this))
                    },function(){
                        this.close();
                    });
                }
            });
        }else{
            this.moreActionComplete = new Element("div.moreActionComplete",{styles:this.css.moreActionComplete, text:this.lp.moreActionComplete}).inject(this.moreActionContainer);
            this.moreActionComplete.addEvents({
                mouseover:function(){
                    this.setStyles({"background-color":"#ffeded"})
                },
                mouseout:function(){
                    this.setStyles({"background-color":"#ffffff"})
                },
                click:function(e){
                    _self.app.confirm("warn",e,_self.lp.moreActionComplete,_self.lp.moreActionCompleteTips,450,100,function(){
                        _self.rootActions.ProjectAction.completeProject(_self.projectData.id,{completed:true},function(){
                            this.close();
                            _self.close({"action":"reload"});
                        }.bind(this))
                    },function(){
                        this.close();
                    });
                }
            });
        }
        if(this.projectData.deleted){
            this.moreActionRecover = new Element("div.moreActionRemove",{styles:this.css.moreActionRecover, text:this.lp.moreActionRecover}).inject(this.moreActionContainer);
            this.moreActionRecover.addEvents({
                mouseover:function(){
                    this.setStyles({"background-color":"#c21c07"})
                },
                mouseout:function(){
                    this.setStyles({"background-color":"#e62412"})
                },
                click:function(e){
                    _self.app.confirm("warn",e,_self.lp.moreActionRecover,_self.lp.moreActionRecoverTips,450,100,function(){
                        _self.rootActions.ProjectAction.recoveryProject(_self.projectData.id,function(){
                            this.close();
                            _self.close({"action":"reload"});
                        }.bind(this))
                    },function(){
                        this.close();
                    });
                }
            });
        }else{
            this.moreActionRemove = new Element("div.moreActionRemove",{styles:this.css.moreActionRemove, text:this.lp.moreActionRemove}).inject(this.moreActionContainer);
            this.moreActionRemove.addEvents({
                mouseover:function(){
                    this.setStyles({"background-color":"#c21c07"})
                },
                mouseout:function(){
                    this.setStyles({"background-color":"#e62412"})
                },
                click:function(e){
                    _self.app.confirm("warn",e,_self.lp.moreActionRemove,_self.lp.moreActionRemoveTips,450,100,function(){
                        _self.rootActions.ProjectAction.delete(_self.projectData.id,function(){
                            this.close();
                            _self.close({"action":"reload"});
                        }.bind(this))
                    },function(){
                        this.close();
                    });
                }
            });
        }
    },
    projectInfor:function(callback){
        if(this.data.id){
            this.actions.get(this.data.id,function(json){
                this.projectData = json.data;
                if(callback)callback(json.data)
            }.bind(this));

        }
    },
    groupInfor:function(ids){
        if(!ids) return;
        var resGroups = [];
        ids.each(function(data){
            this.rootActions.ProjectGroupAction.get(data,function(json){

            }.bind(this))
        }.bind(this))
    }

});
