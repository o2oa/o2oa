MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.ProjectList = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "hasNavi" : true,
        "align" : "center"
    },
    initialize: function (container, app, data, options) {
        this.setOptions(options);
        this.container = container;

        this.app = app;
        this.lp = this.app.lp.ProjectList;
        this.actions = this.app.restActions;

        this.path = "/x_component_TeamWork/$ProjectList/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        this.data = data;
    },
    load: function () {
        this.container.empty();
        this.container.setStyles({display:"flex"});
        this.currentListType = "block";
        this.currentNavi = "allItem";

        this.naviTable = new Element("div",{styles:this.css.naviTable}).inject(this.container);
        this.createNavi();

        this.contentTable = new Element("div.contentTable",{styles:this.css.contentTable}).inject(this.container);
        this.createContent();

        this.clickNavi();

        //this.allItemAdd.click(); //临时
    },
    reload:function(){
        this.createNavi();
        this.clickNavi();
    },
    createNavi:function(){
        this.app.setLoading(this.naviTable);
        this.actions.projectGroupGet(function(json){
            this.naviTable.empty();
            if(json.type == "success") {
                var resData = json.data;
                this.projectGroup = json.data;
                var _self = this;

                if(this.naviTable)this.naviTable.empty();
                this.naviContent = new Element("div.naviContent",{
                    styles:this.css.naviContent
                }).inject(this.naviTable);

                var naviTitle = new Element("div.naviTitle",{styles:this.css.naviTitle,text:this.lp.navi.title}).inject(this.naviContent);

                //全部项目
                if(resData.hasOwnProperty("allCount")){
                    this.allItem = new Element("div.allItem",{styles:this.css.naviMenu}).inject(this.naviContent);
                    this.allItemText = new Element("div",{styles:this.css.allItemText,text:this.lp.navi.allItem}).inject(this.allItem);
                    this.allItemCount = new Element("div.allItemCount",{
                        styles:this.css.allItemCount,
                        text:"（"+(resData.allCount ||"") +"）"
                    }).inject(this.allItem);
                    this.allItemAdd = new Element("div.allItemAdd",{styles:this.css.allItemAdd}).inject(this.allItem);
                    this.allItemAdd.addEvents({
                        click:function(e){
                            this.addNewProject();

                            e.stopPropagation();
                        }.bind(this),
                        mouseover:function(){
                            this.app.showTips(this.allItemAdd,{_html:"<div style='margin:2px 5px;'>"+this.lp.content.addProjectBlockText+"</div>"});
                        }.bind(this)
                    });
                    this.allItem.addEvents({
                        mouseover:function(){
                            this.allItem.setStyles({
                                "background-color":"#F2F5F7"
                            });
                            this.allItemText.setStyles({"color":"#4A90E2"});
                            this.allItemCount.setStyles({"color":"#4A90E2"});
                            this.allItemAdd.setStyles({
                                "background-image":"url(/x_component_TeamWork/$ProjectList/default/icon/icon_zengjia_blue2_click.png)"
                            });

                        }.bind(this),
                        mouseout:function(){
                            this.allItem.setStyles(this.css.naviMenu);
                            this.allItemText.setStyles(this.css.allItemText);
                            this.allItemCount.setStyles(this.css.allItemCount);
                            this.allItemAdd.setStyles(this.css.allItemAdd);
                        }.bind(this),
                        click:function(){
                            this.openItem({type:"all"});
                            this.currentNavi = "all"
                        }.bind(this)
                    });
                }


                //我的星标
                if(resData.hasOwnProperty("starCount")){
                    this.starItem = new Element("div.starItem",{styles:this.css.naviItem}).inject(this.naviContent);
                    this.starItemImg = new Element("div.starItemImg",{styles:this.css.starItemImg}).inject(this.starItem);
                    this.starItemText = new Element("div.starItemText",{styles:this.css.starItemText,text:this.lp.navi.starItem}).inject(this.starItem);
                    this.starItemCount = new Element("div.starItemCount",{styles:this.css.starItemCount,text:resData.starCount}).inject(this.starItem);
                    this.starItemIcon = new Element("div.starItemIcon",{styles:this.css.starItemIcon}).inject(this.starItem);
                    this.starItem.addEvents({
                        mouseover:function(){
                            this.starItem.setStyles({
                                "background-color":"#F2F5F7"
                            });
                            this.starItemImg.setStyles({
                                "background-image":"url(/x_component_TeamWork/$ProjectList/default/icon/icon_wdxx_1_blue.png)"
                            });
                            this.starItemText.setStyles({
                                "color":"#4A90E2"
                            });
                        }.bind(this),
                        mouseout:function(){
                            this.starItem.setStyles(this.css.naviItem);
                            this.starItemImg.setStyles(this.css.starItemImg);
                            this.starItemText.setStyles(this.css.starItemText);
                        }.bind(this),
                        click:function(){
                            this.openItem({type:"star"});
                            this.currentNavi = "star"
                        }.bind(this)
                    });
                }

                //我的项目
                if(resData.hasOwnProperty("myCount")){
                    this.myItem = new Element("div.myItem",{styles:this.css.naviItem}).inject(this.naviContent);
                    this.myItemImg = new Element("div.myItemImg",{styles:this.css.myItemImg}).inject(this.myItem);
                    this.myItemText = new Element("div.myItemText",{styles:this.css.myItemText,text:this.lp.navi.myItem}).inject(this.myItem);
                    this.myItemCount = new Element("div.myItemCount",{styles:this.css.myItemCount,text:resData.myCount}).inject(this.myItem);
                    // this.myItemIcon = new Element("div.myItemIcon",{styles:this.css.myItemIcon}).inject(this.myItem);
                    this.myItem.addEvents({
                        mouseover:function(){
                            this.myItem.setStyles({
                                "background-color":"#F2F5F7"
                            });
                            this.myItemImg.setStyles({
                                "background-image":"url(/x_component_TeamWork/$ProjectList/default/icon/icon_wdxm_xm_click.png)"
                            });
                            this.myItemText.setStyles({
                                "color":"#4A90E2"
                            });
                        }.bind(this),
                        mouseout:function(){
                            this.myItem.setStyles(this.css.naviItem);
                            this.myItemImg.setStyles(this.css.myItemImg);
                            this.myItemText.setStyles(this.css.myItemText);
                        }.bind(this),
                        click:function(){
                            this.openItem({type:"my"});
                            this.currentNavi = "my"
                        }.bind(this)
                    });
                }


                //未分组项目
                if(resData.hasOwnProperty("unGroupCount")){
                    this.unGroupItem = new Element("div.unGroupItem",{styles:this.css.naviItem}).inject(this.naviContent);
                    this.unGroupItemImg = new Element("div.unGroupItemImg",{styles:this.css.unGroupItemImg}).inject(this.unGroupItem);
                    this.unGroupItemText = new Element("div.unGroupItemText",{styles:this.css.unGroupItemText,text:this.lp.navi.unGroupItem}).inject(this.unGroupItem);
                    this.unGroupItemCount = new Element("div.unGroupItemCount",{styles:this.css.unGroupItemCount,text:resData.unGroupCount}).inject(this.unGroupItem);
                    this.unGroupItem.addEvents({
                        mouseover:function(){
                            this.unGroupItem.setStyles({
                                "background-color":"#F2F5F7"
                            });
                            this.unGroupItemImg.setStyles({
                                "background-image":"url(/x_component_TeamWork/$ProjectList/default/icon/icon_wfzxm_xm_click.png)"
                            });
                            this.unGroupItemText.setStyles({
                                "color":"#4A90E2"
                            });
                        }.bind(this),
                        mouseout:function(){
                            this.unGroupItem.setStyles(this.css.naviItem);
                            this.unGroupItemImg.setStyles(this.css.unGroupItemImg);
                            this.unGroupItemText.setStyles(this.css.unGroupItemText);
                        }.bind(this),
                        click:function(){
                            this.openItem({type:"unGroup"});
                            this.currentNavi = "unGroup"
                        }.bind(this)
                    });
                }


                //已完成项目
                if(resData.hasOwnProperty("completedCount")){
                    this.completeItem = new Element("div.completeItem",{styles:this.css.naviItem}).inject(this.naviContent);
                    this.completeItemImg = new Element("div.completeItemImg",{styles:this.css.completeItemImg}).inject(this.completeItem);
                    this.completeItemText = new Element("div.completeItemText",{styles:this.css.completeItemText,text:this.lp.navi.completeItem}).inject(this.completeItem);
                    this.completeItemCount = new Element("div.completeItemCount",{styles:this.css.completeItemCount,text:resData.completedCount}).inject(this.completeItem);
                    this.completeItem.addEvents({
                        mouseover:function(){
                            this.completeItem.setStyles({
                                "background-color":"#F2F5F7"
                            });
                            this.completeItemImg.setStyles({
                                "background-image":"url(/x_component_TeamWork/$ProjectList/default/icon/icon_renwu_ywc_click.png)"
                            });
                            this.completeItemText.setStyles({
                                "color":"#4A90E2"
                            });
                        }.bind(this),
                        mouseout:function(){
                            this.completeItem.setStyles(this.css.naviItem);
                            this.completeItemImg.setStyles(this.css.completeItemImg);
                            this.completeItemText.setStyles(this.css.completeItemText);
                        }.bind(this),
                        click:function(){
                            this.openItem({type:"complete"});
                            this.currentNavi = "complete"
                        }.bind(this)
                    });
                }

                //已归档
                if(resData.hasOwnProperty("archiveCount")){
                    this.historyItem = new Element("div.historyItem",{styles:this.css.naviItem}).inject(this.naviContent);
                    this.historyItemImg = new Element("div.historyItemImg",{styles:this.css.historyItemImg}).inject(this.historyItem);
                    this.historyItemText = new Element("div.historyItemText",{styles:this.css.historyItemText,text:this.lp.navi.historyItem}).inject(this.historyItem);
                    this.historyItemCount = new Element("div.historyItemCount",{styles:this.css.historyItemCount,text:resData.archiveCount}).inject(this.historyItem);
                    // this.historyItemIcon = new Element("div.historyItemIcon",{styles:this.css.historyItemIcon}).inject(this.historyItem);
                    this.historyItem.addEvents({
                        mouseover:function(){
                            this.historyItem.setStyles({
                                "background-color":"#F2F5F7"
                            });
                            this.historyItemImg.setStyles({
                                "background-image":"url(/x_component_TeamWork/$ProjectList/default/icon/icon_lsck_xm_click.png)"
                            });
                            this.historyItemText.setStyles({
                                "color":"#4A90E2"
                            });
                        }.bind(this),
                        mouseout:function(){
                            this.historyItem.setStyles(this.css.naviItem);
                            this.historyItemImg.setStyles(this.css.historyItemImg);
                            this.historyItemText.setStyles(this.css.historyItemText);
                        }.bind(this),
                        click:function(){
                            this.openItem({type:"archive"});
                            this.currentNavi = "archive"
                        }.bind(this)
                    });
                }

                //回收站
                if(resData.hasOwnProperty("deleteCount")){
                    this.removeItem = new Element("div.removeItem",{styles:this.css.naviItem}).inject(this.naviContent);
                    this.removeItemImg = new Element("div.removeItemImg",{styles:this.css.removeItemImg}).inject(this.removeItem);
                    this.removeItemText = new Element("div.removeItemText",{styles:this.css.removeItemText,text:this.lp.navi.removeItem}).inject(this.removeItem);
                    this.removeItemCount = new Element("div.removeItemCount",{styles:this.css.removeItemCount,text:resData.deleteCount}).inject(this.removeItem);
                    this.removeItem.addEvents({
                        mouseover:function(){
                            this.removeItem.setStyles({
                                "background-color":"#F2F5F7"
                            });
                            this.removeItemImg.setStyles({
                                "background-image":"url(/x_component_TeamWork/$ProjectList/default/icon/icon_delete_blue_click.png)"
                            });
                            this.removeItemText.setStyles({
                                "color":"#4A90E2"
                            });
                        }.bind(this),
                        mouseout:function(){
                            this.removeItem.setStyles(this.css.naviItem);
                            this.removeItemImg.setStyles(this.css.removeItemImg);
                            this.removeItemText.setStyles(this.css.removeItemText);
                        }.bind(this),
                        click:function(){
                            this.openItem({type:"remove"});
                            this.currentNavi = "remove"
                        }.bind(this)
                    });
                }

                //项目分组
                this.groupMenu = new Element("div.groupMenu",{styles:this.css.naviMenu}).inject(this.naviContent);
                this.groupMenuText = new Element("div.groupMenu",{styles:this.css.groupMenuText,text:this.lp.navi.groupMenu}).inject(this.groupMenu);

                this.groupMenuAdd = new Element("div.groupMenuAdd",{styles:this.css.groupMenuAdd}).inject(this.groupMenu);
                this.groupMenuAdd.addEvents({
                    click:function(e){
                        this.createProjectGroup(this.groupMenuAdd);
                        e.stopPropagation();
                    }.bind(this),
                    mouseover:function(e){
                        this.app.showTips(this.groupMenuAdd,{_html:"<div style='margin:2px 5px;'>"+this.lp.navi.addGroup+"</div>"});
                        e.stopPropagation();
                    }.bind(this)
                });
                this.groupMenu.addEvents({
                    mouseover:function(){
                        this.groupMenu.setStyles({
                            "background-color":"#F2F5F7"
                        });
                        this.groupMenuText.setStyles({"color":"#4A90E2"});

                        this.groupMenuAdd.setStyles({
                            "background-image":"url(/x_component_TeamWork/$ProjectList/default/icon/icon_zengjia_blue2_click.png)"
                        })
                    }.bind(this),
                    mouseout:function(){
                        this.groupMenu.setStyles(this.css.naviMenu);
                        this.groupMenuText.setStyles(this.css.groupMenuText);
                        this.groupMenuAdd.setStyles(this.css.groupMenuAdd);
                    }.bind(this),
                    click:function(){

                    }.bind(this)
                });


                this.loadProjectGroup();

                //新建项目分组
                this.groupAdd = new Element("div.groupAdd",{styles:this.css.groupAdd}).inject(this.naviContent);
                this.groupAdd.addEvents({
                    mouseover:function(){
                        this.groupAdd.setStyles({"background-color":"#F2F5F7"});
                    }.bind(this),
                    mouseout:function(){
                        this.groupAdd.setStyles(this.css.groupAdd);
                    }.bind(this),
                    click:function(){
                        this.createProjectGroup(this.groupAdd);
                    }.bind(this)
                });
                new Element("div.groupAddIcon",{styles:this.css.groupAddIcon}).inject(this.groupAdd);
                new Element("div.groupAddText",{styles:this.css.groupAddText,text:this.lp.navi.addGroup}).inject(this.groupAdd);



            }

        }.bind(this));

    },
    clickNavi:function(){
        //点击
        if(this.currentNavi = "allItem"){
            this.openItem({"type":"all"})
        }else if(this.currentNavi = "star"){
            this.openItem({"type":"star"})
        }else if(this.currentNavi = "my"){
            this.openItem({"type":"my"})
        }else if(this.currentNavi = "complete"){
            this.openItem({"type":"complete"})
        }else if(this.currentNavi = "unGroup"){
            this.openItem({"unGroup":"all"})
        }else if(this.currentNavi = "archive"){
            this.openItem({"type":"archive"})
        }else if(this.currentNavi = "remove"){
            this.openItem({"type":"remove"})
        }else{

        }
    },
    addNewProject:function(){
        MWF.xDesktop.requireApp("TeamWork", "NewProject", function(){
            this.np = new MWF.xApplication.TeamWork.NewProject(this,{},
                {"width": 350,"height": 350,
                    onPostOpen:function(){
                        this.np.formAreaNode.setStyles({"top":"10px"});
                        var fx = new Fx.Tween(this.np.formAreaNode,{duration:200});
                        fx.start(["top"] ,"10px", "100px");

                    }.bind(this),
                    onPostClose:function(json){
                        //this.reloadLayoutList();
                        if(json.data && json.data.id){
                            this.actions.projectGet(json.data.id,function(jsonr){
                                if(jsonr.data){
                                    this.openProject(jsonr.data)
                                }
                            }.bind(this));

                        }
                    }.bind(this)
                },{
                    container : this.container

                }
            );
            this.np.open();
        }.bind(this));
    },

    loadProjectGroup:function(){
        var _self = this;
        //自定义项目分组
        //if(this.customGroup) this.customGroup.empty();
        //else this.customGroup = new Element("div.customGroup",{styles:this.css.customGroup}).inject(this.naviContent);
        this.customGroup = new Element("div.customGroup",{styles:this.css.customGroup}).inject(this.naviContent);

        if(this.projectGroup.groups){
            this.projectGroup.groups.each(function(d){
                var customGroupItemContainer = new Element("div.customGroupItemContainer",{styles:this.css.customGroupItemContainer}).inject(this.customGroup);
                customGroupItemContainer.addEvents({
                    mouseover:function(e){
                        this.setStyles({"background-color":"#F2F5F7","color":"#4A90E2"});
                        this.getElements(".customGroupItemRemove").setStyles({"display":"block"});
                        this.getElements(".customGroupItemEdit").setStyles({"display":"block"});
                        //_self.app.showTips(this,{_html:"<div>"+d.title+"</div>"});

                    },
                    mouseout:function(){
                        if(this.get("atIn")!="yes"){
                            this.setStyles(_self.css.customGroupItemContainer);
                            this.getElements(".customGroupItemRemove").setStyles({"display":"none"});
                            this.getElements(".customGroupItemEdit").setStyles({"display":"none"});
                        }
                    },
                    click:function(){
                        _self.openItem({group:d.id});
                        _self.currentNavi = d.id;
                    }
                });
                var customGroupItem = new Element("div.customGroupItem",{styles:this.css.customGroupItem,text:d.name}).inject(customGroupItemContainer);

                var customGroupItemRemove = new Element("div.customGroupItemRemove",{styles:this.css.customGroupItemRemove}).inject(customGroupItemContainer);
                customGroupItemRemove.addEvents({
                    click:function(e){
                        customGroupItemContainer.set("atIn","yes");
                        _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){
                            // var newData1 = [];
                            // data1.each(function(dd){
                            //     if(dd.title != d.title) newData1.push(dd)
                            // });
                            // data1 = newData1;
                            // var id ="";
                            _self.actions.groupRemove(d.id,function(){
                                _self.reloadProjectGroup();
                            }.bind(this));


                            this.close();
                            customGroupItemContainer.set("atIn","no");

                        },function(){
                            this.close();
                            customGroupItemContainer.set("atIn","no");
                        });
                        e.stopPropagation();
                    }
                });
                var customGroupItemEdit = new Element("div.customGroupItemEdit",{styles:this.css.customGroupItemEdit}).inject(customGroupItemContainer);
                customGroupItemEdit.addEvents({
                    click:function(e){
                        customGroupItemContainer.set("atIn","yes");
                        MWF.xDesktop.requireApp("TeamWork", "Group", function(){
                            var json = d;
                            json.do = "edit";
                            var tooltip = new MWF.xApplication.TeamWork.Group(_self.container, customGroupItemEdit, _self.app, json, {
                                axis : "y",
                                nodeStyles : {
                                    "z-index" : "101"
                                },
                                onPostLoad:function(){
                                    tooltip.node.setStyles({"opacity":"0"});
                                    var fx = new Fx.Tween(tooltip.node,{duration:200});
                                    fx.start(["opacity"] ,"0", "1");
                                    tooltip.groupIn.select();
                                },
                                onClose:function(rd){
                                    customGroupItemContainer.set("atIn","no");
                                    if(rd)_self.reloadProjectGroup();
                                }
                            });
                            tooltip.load()
                        });

                        e.stopPropagation()
                    }
                });
            }.bind(this))
        }

    },
    reloadProjectGroup:function(){
        var _self = this;
        this.actions.groupList(function(json){
            var data = json.data;
            if(data.length == 0) return;
            this.customGroup.empty();
            data.each(function(d){

                var customGroupItemContainer = new Element("div.customGroupItemContainer",{styles:this.css.customGroupItemContainer}).inject(this.customGroup);
                customGroupItemContainer.addEvents({
                    mouseover:function(e){
                        this.setStyles({"background-color":"#F2F5F7","color":"#4A90E2"});
                        this.getElements(".customGroupItemRemove").setStyles({"display":"block"});
                        this.getElements(".customGroupItemEdit").setStyles({"display":"block"});
                        //_self.app.showTips(this,{_html:"<div>"+d.title+"</div>"});

                    },
                    mouseout:function(){
                        if(this.get("atIn")!="yes"){
                            this.setStyles(_self.css.customGroupItemContainer);
                            this.getElements(".customGroupItemRemove").setStyles({"display":"none"});
                            this.getElements(".customGroupItemEdit").setStyles({"display":"none"});
                        }
                    },
                    click:function(){
                        //alert(this.get("text"))
                    }
                });
                var customGroupItem = new Element("div.customGroupItem",{styles:this.css.customGroupItem,text:d.name}).inject(customGroupItemContainer);

                var customGroupItemRemove = new Element("div.customGroupItemRemove",{styles:this.css.customGroupItemRemove}).inject(customGroupItemContainer);
                customGroupItemRemove.addEvents({
                    click:function(e){
                        customGroupItemContainer.set("atIn","yes");
                        _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){
                            // var newData1 = [];
                            // data1.each(function(dd){
                            //     if(dd.title != d.title) newData1.push(dd)
                            // });
                            // data1 = newData1;
                            // var id ="";
                            _self.actions.groupRemove(d.id,function(){
                                _self.reloadProjectGroup();
                            }.bind(this));


                            this.close();
                            customGroupItemContainer.set("atIn","no");

                        },function(){
                            this.close();
                            customGroupItemContainer.set("atIn","no");
                        });
                        e.stopPropagation();
                    }
                });
                var customGroupItemEdit = new Element("div.customGroupItemEdit",{styles:this.css.customGroupItemEdit}).inject(customGroupItemContainer);
                customGroupItemEdit.addEvents({
                    click:function(e){
                        customGroupItemContainer.set("atIn","yes");
                        MWF.xDesktop.requireApp("TeamWork", "Group", function(){
                            var json = d;
                            json.do = "edit";
                            var tooltip = new MWF.xApplication.TeamWork.Group(_self.container, customGroupItemEdit, _self.app, json, {
                                axis : "y",
                                nodeStyles : {
                                    "z-index" : "101"
                                },
                                onPostLoad:function(){
                                    tooltip.node.setStyles({"opacity":"0"});
                                    var fx = new Fx.Tween(tooltip.node,{duration:200});
                                    fx.start(["opacity"] ,"0", "1");
                                    tooltip.groupIn.select();
                                },
                                onClose:function(rd){
                                    customGroupItemContainer.set("atIn","no");
                                    if(rd)_self.reloadProjectGroup();
                                }
                            });
                            tooltip.load()
                        });

                        e.stopPropagation()
                    }
                });


            }.bind(this));
        }.bind(this));
    },
    createProjectGroup:function(node){
        MWF.xDesktop.requireApp("TeamWork", "Group", function(){
            var json = {};
            json.do = "add";
            var tooltip = new MWF.xApplication.TeamWork.Group(this.container, node, this.app, json, {
                axis : "y",
                nodeStyles : {
                    "z-index" : "101"
                },
                onPostLoad:function(){
                    tooltip.node.setStyles({"opacity":"0"});
                    var fx = new Fx.Tween(tooltip.node,{duration:200});
                    fx.start(["opacity"] ,"0", "1");

                }.bind(this),
                onClose:function(d){
                    //if(d && d.title){
                    //    data1.push({"title":d.title});
                        if(d)this.reloadProjectGroup();

                    //}
                }.bind(this)
            });
            tooltip.load()
        }.bind(this));

    },



    //右边项目展示
    createContent:function(){

        if(this.contentTable) this.contentTable.empty();
        this.columnGray = new Element("div.columnGray",{styles:this.css.columnGray}).inject(this.contentTable); //灰色竖线
        this.projectContent = new Element("div.projectContent",{
            styles:this.css.projectContent
        }).inject(this.contentTable);

        this.rowGray = new Element("div.rowGray",{styles:this.css.rowGray}).inject(this.projectContent); //灰色竖线

        this.layoutMenu();
        this.layoutList();

    },
    layoutMenu:function(){
        //alert(this.currentListType)
        this.menuContainer = new Element("div.menuContainer",{styles:this.css.menuContainer}).inject(this.projectContent);
        this.menuTitle = new Element("div.menuTitle",{styles:this.css.menuTitle}).inject(this.menuContainer);
        this.menuCount = new Element("div.menuCount",{styles:this.css.menuCount}).inject(this.menuContainer);
        this.menuSetting = new Element("div.menuSetting",{styles:this.css.menuSetting}).inject(this.menuContainer);
        this.menuSettingList = new Element("dis.menuSettingList",{styles:this.css.menuSettingList}).inject(this.menuSetting);
        this.menuSettingList.addEvents({
            click:function(){
                this.currentListType = "list";
                this.menuSettingList.setStyle("background-image","url(/x_component_TeamWork/$ProjectList/default/icon/icon_liebiao_click.png)");
                this.menuSettingBlock.setStyle("background-image","url(/x_component_TeamWork/$ProjectList/default/icon/icon_tubiao.png)");
                this.openItem()
            }.bind(this),
            mouseover:function(){
                //this.app.showTips(this.menuSettingList,{_html:"<div style=''> "+this.lp.content.listTip+" </div>"},{axis:"y",position : { x : "auto", y : "top" }});
            }.bind(this)
        });
        this.menuSettingBlock = new Element("dis.menuSettingBlock",{styles:this.css.menuSettingBlock}).inject(this.menuSetting);
        this.menuSettingBlock.addEvents({
            click:function(){
                this.currentListType = "block";
                this.menuSettingList.setStyle("background-image","url(/x_component_TeamWork/$ProjectList/default/icon/icon_liebiao.png)");
                this.menuSettingBlock.setStyle("background-image","url(/x_component_TeamWork/$ProjectList/default/icon/icon_tubiao_click.png)");
                this.openItem()
            }.bind(this),
            mouseover:function(){
                //this.app.showTips(this.menuSettingList,{_html:"<div style=''> "+this.lp.content.blockTip+" </div>"},{axis:"y",position : { x : "auto", y : "top" }});
            }.bind(this)
        });

        if(this.currentListType == "block"){
            this.menuSettingBlock.setStyle("background-image","url(/x_component_TeamWork/$ProjectList/default/icon/icon_tubiao_click.png)");
        }else{
            this.menuSettingList.setStyle("background-image","url(/x_component_TeamWork/$ProjectList/default/icon/icon_liebiao_click.png)");
        }

    },
    reloadLayoutList:function(){
        if(this.layoutList)this.layoutList.empty();
        this.openItem()
    },
    layoutList:function(){
        this.layoutListContainer = new Element("div.layoutListContainer",{styles:this.css.layoutListContainer}).inject(this.projectContent);
        this.layoutList = new Element("div.layoutList",{styles:this.css.layoutList}).inject(this.layoutListContainer);
        //this.menuSettingBlock.click();
    },
    openItem:function(options){
        var _self = this;
        if(this.layoutList) this.layoutList.empty();
        var _self = this;
        var id = "(0)";
        var count = 100;
        var filter = {};
        var typeAction = "projectListNext";
        if(options && options.type && options.type=="all"){
            typeAction = "projectListNext"
        }else if(options && options.type && options.type=="star"){
            typeAction = "projectStarListNext"
        }else if(options && options.type && options.type=="my"){
            typeAction = "projectMyListNext"
        }else if(options && options.type && options.type=="complete"){
            typeAction = "projectCompleteListNext"
        }else if(options && options.type && options.type=="archive"){
            typeAction = "projectArchiveListNext"
        }else if(options && options.type && options.type=="remove"){
            typeAction = "projectRemoveListNext"
        }else if(options && options.type && options.type=="unGroup"){
            typeAction = "projectUnGroupListNext"
        }

        if(options && options.group){
            groupId = options.group;
            typeAction = "projectListNext";
            filter = {group:groupId};
        }

        //alert(typeAction)
        this.app.setLoading(this.layoutList);
        this.actions[typeAction](id, count, filter,function(json){
            this.layoutList.empty();
            var projectItemData = json.data;

            if(options && options.type && options.type=="all"){
                this.menuTitle.set("text","全部项目");
            }else if(options && options.type && options.type=="star"){
                this.menuTitle.set("text","我的星标")
            }else if(options && options.type && options.type=="my"){
                typeAction = "projectMyListNext"
            }else if(options && options.type && options.type=="complete"){
                typeAction = "projectCompleteListNext"
            }else if(options && options.type && options.type=="archive"){
                typeAction = "projectArchiveListNext"
            }else if(options && options.type && options.type=="remove"){
                typeAction = "projectRemoveListNext"
            }else if(options && options.type && options.type=="unGroup"){
                typeAction = "projectUnGroupListNext"
            }

            if(options && options.group){
                groupId = options.group;
                typeAction = "projectListNext";
                filter = {group:groupId};
            }

            this.menuCount.set("text","("+json.count+")");
            //debugger;
            if(this.currentListType=="list"){
                if(!projectItemData) return;
                projectItemData.each(function(d){
                    var projectListItem = new Element("div.projectListItem",{styles:this.css.projectListItem}).inject(this.layoutList);
                    projectListItem.set("id",d.id);
                    projectListItem.addEvents({
                        click:function(){
                            _self.openProject(d);
                        },
                        mouseenter:function(){
                            this.setStyles({
                                "background-color":"#F2F5F7"
                            });
                            //projectListItem.getElements(".projectListItemRemoveIcon").setStyles({"display":"block"});
                            //projectListItem.getElements(".projectListItemSettingIcon").setStyles({"display":"block"});
                            projectListItem.getElements(".projectListItemContainer").setStyles({"border-bottom-width":"0px"});
                        },
                        mouseleave:function(){
                            this.setStyles({
                                "background-color":""
                            });
                            //projectListItem.getElements(".projectListItemRemoveIcon").setStyles({"display":"none"});
                            //projectListItem.getElements(".projectListItemSettingIcon").setStyles({"display":"none"});
                            projectListItem.getElements(".projectListItemContainer").setStyles({"border-bottom-width":"1px"});
                        }
                    });
                    this.loadSingleListItem(projectListItem,d);


                }.bind(this));
                this.addProjectList = new Element("div.addProjectList",{styles:this.css.addProjectList}).inject(this.layoutList);
                this.addProjectList.addEvents({
                    click:function(){
                        _self.addNewProject();
                    },
                    mouseover:function(){
                        this.setStyles({
                            "background-color":"#F2F5F7"
                        });
                    },
                    mouseout:function(){
                        this.setStyles({
                            "background-color":""
                        });
                    }
                });
                this.addProjectListImgContainer = new Element("div.addProjectListImgContainer",{styles:this.css.addProjectListImgContainer}).inject(this.addProjectList);
                this.addProjectListImg = new Element("div.addProjectListImg",{styles:this.css.addProjectListImg}).inject(this.addProjectListImgContainer);
                this.addProjectListText = new Element("div.addProjectListText",{styles:this.css.addProjectListText,text:this.lp.content.addProjectListText}).inject(this.addProjectList);

            }else{
                if(!projectItemData) return;
                projectItemData.each(function(d){
                    var projectBlockItem = new Element("div.projectBlockItem",{styles:this.css.projectBlockItem}).inject(this.layoutList);
                    projectBlockItem.set("id",d.id);
                    if(d.icon && d.icon!=""){
                        projectBlockItem.setStyles({
                            "background-image":"url('"+MWF.xDesktop.getImageSrc( d.icon )+"')"
                        });
                    }
                    projectBlockItem.addEvents({
                        click:function(){
                            _self.openProject(d);
                        },
                        mouseenter:function(){
                            this.setStyles({
                                "box-shadow":"0px 0px 10px #4a90e2"
                            });
                            //projectBlockItem.getElements(".projectBlockItemIconSetting").setStyles({"display":"block"});
                            //projectBlockItem.getElements(".projectBlockItemIconRemove").setStyles({"display":"block"});
                        },
                        mouseleave:function(){
                            this.setStyles({
                                "box-shadow":"0px 0px 0px #DFDFDF"
                            });
                            //projectBlockItem.getElements(".projectBlockItemIconSetting").setStyles({"display":"none"});
                            //projectBlockItem.getElements(".projectBlockItemIconRemove").setStyles({"display":"none"});
                        }
                    });
                    this.loadSingleBlockItem(projectBlockItem,d);

                }.bind(this));
                this.addProjectBlock = new Element("div.addProjectBlock",{styles:this.css.addProjectBlock}).inject(this.layoutList);
                this.addProjectBlockIcon = new Element("div.addProjectBlockIcon",{styles:this.css.addProjectBlockIcon}).inject(this.addProjectBlock);
                this.addProjectBlockText = new Element("div.addProjectBlockText",{styles:this.css.addProjectBlockText,text:this.lp.content.addProjectBlockText}).inject(this.addProjectBlock);
                this.addProjectBlock.addEvents({
                    click:function(){
                        _self.addNewProject();
                    },
                    mouseenter:function(){
                        this.setStyles({
                            "box-shadow":"0px 0px 10px #4a90e2"
                        });

                        //var fx = new Fx.Tween(this,{duration:50});
                        //fx.start(["margin-top"] ,"10px", "5px");
                    },
                    mouseleave:function(){
                        this.setStyles({
                            "box-shadow":"0px 0px 0px #DFDFDF"
                        });
                        //var fx = new Fx.Tween(this,{duration:50});
                        //fx.start(["margin-top"] ,"5px", "10px");
                    }
                })
            }
        }.bind(this));

    },
    loadSingleBlockItem:function(container,d){
        if(container) container.empty();
        var projectBlockItemCover = new Element("div.projectBlockItemCover",{styles:this.css.projectBlockItemCover}).inject(container);
        var _self = this;
        var projectBlockItemIconContainer = new Element("div.projectBlockItemIconContainer",{styles:this.css.projectBlockItemIconContainer}).inject(projectBlockItemCover);

        var projectBlockItemIconFav = new Element("div.projectBlockItemIconFav",{styles:this.css.projectBlockItemIconFav}).inject(projectBlockItemIconContainer);
        projectBlockItemIconFav.addEvents({
            click:function(e){
                _self.setFav(d,function(data){
                    _self.actions.projectGet(data.data.id,function(json){
                        _self.loadSingleBlockItem(container,json.data)
                    });
                    _self.createNavi();
                });

                e.stopPropagation();
            }
        });

        if(d.star){
            projectBlockItemIconFav.setStyles({
                "background-image":"url(/x_component_TeamWork/$ProjectList/default/icon/icon_wdxx_click.png)"
            });
        }

        var projectBlockItemIconSetting = new Element("div.projectBlockItemIconSetting",{styles:this.css.projectBlockItemIconSetting}).inject(projectBlockItemIconContainer);
        projectBlockItemIconSetting.addEvents({
            click:function(e){
                MWF.xDesktop.requireApp("TeamWork", "ProjectSetting", function(){
                    var ps = new MWF.xApplication.TeamWork.ProjectSetting(_self,d,
                        {"width": "800","height": "80%",
                            onPostOpen:function(){
                                ps.formAreaNode.setStyles({"top":"10px"});
                                var fx = new Fx.Tween(ps.formAreaNode,{duration:200});
                                fx.start(["top"] ,"10px", "100px");
                            },
                            onPostClose:function(json){
                                // _self.actions.projectGet(d.id,function(json){
                                //     _self.loadSingleBlockItem(container,json.data)
                                // });
                                //_self.reload();

                            }
                        },{
                            container : _self.container,
                            lp : _self.lp.projectSetting,
                            css:_self.css

                        }
                    );
                    ps.open();
                });

                e.stopPropagation();
            }
        });
        var projectBlockItemIconRemove = new Element("div.projectBlockItemIconRemove",{styles:this.css.projectBlockItemIconRemove}).inject(projectBlockItemIconContainer);
        projectBlockItemIconRemove.addEvents({
            click:function(e){
                _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){
                    var id = d.id;
                    _self.actions.projectRemove(id,function(){
                        //刷新代码
                        this.close();
                        _self.reload();
                    }.bind(this));
                },function(){
                    this.close();
                });

                e.stopPropagation();

            }.bind(this)
        });

         var projectBlockItemName = new Element("div.projectBlockItemName",{styles:this.css.projectBlockItemName,text:d.title}).inject(projectBlockItemCover);
         var projectBlockItemDes = new Element("div.projectBlockItemDes",{styles:this.css.projectBlockItemDes,text:d.description||""}).inject(projectBlockItemCover);
    },
    loadSingleListItem:function(container,d){
        if(container) container.empty();
        var _self = this;
        var projectListItemImgContainer = new Element("div.projectListItemImgContainer",{styles:this.css.projectListItemImgContainer}).inject(container);
        var projectListItemImg = new Element("div.projectListItemImg",{styles:this.css.projectListItemImg}).inject(projectListItemImgContainer);
        if(d.icon && d.icon!=""){
            projectListItemImg.setStyles({
                "background-image":"url('"+MWF.xDesktop.getImageSrc( d.icon )+"')"
            });
        }
        var projectListItemContainer = new Element("div.projectListItemContainer",{styles:this.css.projectListItemContainer}).inject(container);
        var projectListItemInforContainer = new Element("div.projectListItemInforContainer",{styles:this.css.projectListItemInforContainer}).inject(projectListItemContainer);

        var projectListItemTitle = new Element("div.projectListItemTitle",{styles:this.css.projectListItemTitle,text:d.title}).inject(projectListItemInforContainer);
        var projectListItemDes = new Element("div.projectListItemDes",{styles:this.css.projectListItemDes,text:d.description||""}).inject(projectListItemInforContainer);
        var projectListItemIconContainer = new Element("div.projectListItemIconContainer",{styles:this.css.projectListItemIconContainer}).inject(projectListItemContainer);

        var projectListItemFavIcon = new Element("div.projectListItemFavIcon",{styles:this.css.projectListItemFavIcon}).inject(projectListItemIconContainer);
        projectListItemFavIcon.addEvents({
            click:function(e){
                _self.setFav(d,function(data){
                    _self.actions.projectGet(data.data.id,function(json){
                        _self.loadSingleListItem(container,json.data)
                    });
                    _self.createNavi();
                });
                if(projectListItemFavIcon.getStyle("background-image").indexOf("icon_wdxx_click.png")>-1){
                    projectListItemFavIcon.setStyles({
                        "background-image":"url(/x_component_TeamWork/$ProjectList/default/icon/icon_wdxx_1.png)"
                    })
                }else{
                    projectListItemFavIcon.setStyles({
                        "background-image":"url(/x_component_TeamWork/$ProjectList/default/icon/icon_wdxx_click.png)"
                    })
                }

                e.stopPropagation();
            }
        });

        if(d.star){
            projectListItemFavIcon.setStyles({
                "background-image":"url(/x_component_TeamWork/$ProjectList/default/icon/icon_wdxx_click.png)"
            });
        }
        var projectListItemSettingIcon = new Element("div.projectListItemSettingIcon",{styles:this.css.projectListItemSettingIcon}).inject(projectListItemIconContainer);
        projectListItemSettingIcon.addEvents({
            click:function(e){

                MWF.xDesktop.requireApp("TeamWork", "ProjectSetting", function(){
                    var ps = new MWF.xApplication.TeamWork.ProjectSetting(_self,d,
                        {"width": "800","height": "80%",
                            onPostOpen:function(){
                                ps.formAreaNode.setStyles({"top":"10px"});
                                var fx = new Fx.Tween(ps.formAreaNode,{duration:200});
                                fx.start(["top"] ,"10px", "100px");
                            },
                            onPostClose:function(json){
                                _self.actions.projectGet(d.id,function(json){
                                    _self.loadSingleListItem(container,json.data)
                                });
                            }
                        },{
                            container : _self.container,
                            lp : _self.lp.projectSetting,
                            css:_self.css

                        }
                    );
                    ps.open();
                });

                e.stopPropagation();
            }
        });
        var projectListItemRemoveIcon = new Element("div.projectListItemRemoveIcon",{styles:this.css.projectListItemRemoveIcon}).inject(projectListItemIconContainer);
        projectListItemRemoveIcon.addEvents({
            click:function(e){
                _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){
                    var id = d.id;
                    _self.actions.projectRemove(id,function(){
                        //刷新代码
                        this.close();
                        _self.reload();
                    }.bind(this));
                },function(){
                    this.close();
                });

                e.stopPropagation();
            }
        });
    },
    setFav:function(d,callback){
        if(d.star){
            this.actions.projectUnStar(d.id,function(d){
                if(callback)callback(d)
            }.bind(this))
        }else if(!d.star){
            this.actions.projectStar(d.id,function(d){
                if(callback)callback(d)
            }.bind(this))
        }

    },
    openProject:function(d){
        MWF.xDesktop.requireApp("TeamWork", "Project", function(){
            var p = new MWF.xApplication.TeamWork.Project(this.container,this.app,d,{

                }
            );
            p.load();
        }.bind(this));
    },
    test:function(){

    }

});
