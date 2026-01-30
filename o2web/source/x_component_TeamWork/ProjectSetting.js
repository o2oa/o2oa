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

        this.path = "../x_component_TeamWork/$ProjectSetting/";


        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.ProjectAction;

        this.lp = this.app.lp.projectSetting;

        this.fireEvent("queryOpen");
        this.isNew = false;
        this.isEdited = false;
        this._open();
        this.fireEvent("postOpen");
    },
    naviClick:function(e){
        this.leftContainer.getElements('.setting_naviItem').removeClass('setting_naviItemCur');
        var imgList = this.leftContainer.getElements('img');
        imgList.forEach(function(img){
            var _src = img.get("src");
            if(_src.indexOf("_click") >-1){
                img.set('src',_src.replace('_click.png','.png'));
            }
        })
        var _target = e.currentTarget;
        _target.addClass('setting_naviItemCur');
        var _img = _target.getElement('img');
        var _src = _img.get("src");
        if(_src.indexOf("_click") == -1){
            _img.set('src',_src.replace('.png','_click.png'));
        }
        
        
    },
    _createTableContent: function () {
        var _self = this;
        this.formTableArea.setStyles({"width":"100%","height":"100%"});
        this.projectSettingTop = new Element("div.projectSettingTop",{styles:this.css.projectSettingTop}).inject(this.formTableArea);
        this.projectSettingTopText = new Element("div.projectSettingTopText",{styles:this.css.projectSettingTopText,text:this.lp.title}).inject(this.projectSettingTop);
        this.projectSettingTopClose = new Element("div.projectSettingTopClose",{styles:this.css.projectSettingTopClose}).inject(this.projectSettingTop);
        this.projectSettingTopClose.addEvents({
            click:function(){this.close()}.bind(this)
        });

        
        this.projectInfor(function(){ 
            this.projectSettingLayout = new Element("div.projectSettingLayout",{styles:this.css.projectSettingLayout}).inject(this.formTableArea);

            var _html  = this.path + this.options.style + "/view.html";
            this.cssFile = this.path + this.options.style  + "/view.css";
            this.projectSettingLayout.loadAll({"html":_html,css:this.cssFile},{"bind": {"lp": this.lp,"data":this.projectData}, "module": this},function(){ 
                
                this.generalNavi.click();

            }.bind(this))
        }.bind(this))

        
    },
    
    loadGeneral:function(){
        //概况
        this.rightContainer.empty();
        this.groupsArr = [];
        this.projectSettingBgText = new Element("div.projectSettingBgText",{styles:this.css.projectSettingBgText,"text":this.lp.projectSettingBgText}).inject(this.rightContainer);
        this.projectSettingBgContainer = new Element("div.projectSettingBgContainer",{styles:this.css.projectSettingBgContainer}).inject(this.rightContainer);
        this.projectSettingBgImg = new Element("div.projectSettingBgImg",{styles:this.css.projectSettingBgImg}).inject(this.projectSettingBgContainer);
        if(this.projectData.icon && this.projectData.icon!=""){
            this.projectSettingBgImg.setStyles({
                "background-image":"url('"+MWF.xDesktop.getImageSrc( this.projectData.icon )+"')"
            });
        }

        if(this.projectData.control.edit){
            this.projectSettingBgUpload = new Element("div.projectSettingBgUpload",{styles:this.css.projectSettingBgUpload,text:this.lp.upload}).inject(this.projectSettingBgContainer);
            this.projectSettingBgUpload.addEvents({
                click:function(){
                    var data = {};
                    MWF.xDesktop.requireApp("TeamWork", "UploadImage", function(){
                        var ui = new MWF.xApplication.TeamWork.UploadImage(this, data, {
                            documentId : this.data.id ||"",
                            onPostOk : function( id ){ 
                                this.projectData.icon = id;
                                this.actions.save(this.projectData);
                                
                                this.projectSettingBgImg.setStyles({
                                    "background-image":"url('"+MWF.xDesktop.getImageSrc( id )+"')"
                                });
                            }.bind(this)
                        });
                        ui.open()
                    }.bind(this));
                }.bind(this)
            });
        }
        

        this.projectGeneralContainer = new Element("div.projectGeneralContainer",{styles:this.css.projectGeneralContainer}).inject(this.rightContainer);

        var _html  = this.path + this.options.style + "/generalView.html";
        this.projectGeneralContainer.loadAll({"html":_html,css:this.cssFile},{"bind": {"lp": this.lp,"data":this.projectData}, "module": this},function(){ 

            if(this.projectData.control.edit){
                //负责人
                this.manageablePersonList.addEvents({
                    click:function(){ 
                        var values = this.projectData.manageablePersonList;
                        this.app.selectPerson("person",0,values,function(personList){
                            
                            this.projectData.manageablePersonList = personList;
                            var _person = [];
                            this.projectData.manageablePersonList.forEach(function(p){
                                _person.push(p.split("@")[0])
                            })
                            this.manageablePersonList.set("text",_person.join(','));

                        }.bind(this))
                    }.bind(this)
                })

                //参与者
                this.participantList.addEvents({
                    click:function(){
                        this.app.selectPerson("person",0,this.projectData.participantList,function(personList){
                            this.projectData.participantList = personList;
                            var _person = [];
                            this.projectData.participantList.forEach(function(p){
                                _person.push(p.split("@")[0])
                            })
                            this.participantList.set("text",_person.join(','));
                        }.bind(this))
                    }.bind(this)
                });

                //结束时间
                this.endTime.addEvents({
                    click:function(){
                        var opt = {
                            type:"date"
                        };
                        this.app.selectCalendar(this.endTime,this.container,opt,function(json){
                            
                            if(json.action == "ok"){
                                this.endTime.set("text",json.dateString);
                                this.projectData.endTime = json.dateString+" 23:59:59"
                            }else if(json.action == "clear"){
                                this.endTime.set("text","");
                                this.projectData.endTime = ''
                            }
                            
                        }.bind(this))
                    }.bind(this)
                });
            }
            
        }.bind(this))
    },
    
    loadDynamic:function(){
        //动态
        this.dcount = 0;
        var _self = this;
        this.dynamicPage = 1;
        this.dynamicCount = 30;
        this.dynamicStatus = true;
        this.rightContainer.empty();
        this.dynamicContainer = new Element('div.dynamicContainer',{styles:this.css.dynamicContainer}).inject(this.rightContainer);
        this.getDynamicData();
        this.dynamicContainer.addEvents({
            scroll:function(){
                var clientHeight = this.clientHeight;
                var scrollTop = this.scrollTop;
                var scrollHeight = this.scrollHeight;

                if (clientHeight + scrollTop >= (scrollHeight - 10) && _self.dynamicStatus) { 
                    _self.dynamicStatus = false;
                    _self.getDynamicData()
                }

            }
        })
        
    },
    getDynamicData:function(){
        //var filter = {"orderField":"","orderType":""};
        var filter = {};
        this.rootActions.DynamicAction.listNextWithProject(this.dynamicPage,this.dynamicCount,this.projectData.id,filter,function(json){
            json.data.forEach(function(item){
                this.loadDynamicItem(item)
            }.bind(this));
            if(this.dynamicPage * this.dynamicCount >= json.count){
                this.dynamicStatus = false;
            }else{
                this.dynamicPage ++ ;
                this.dynamicStatus = true
            }
            
        }.bind(this))
    },
    loadDynamicItem:function(data){
        var dynamicItemContainer = new Element("div.dynamicItemContainer",{styles:this.css.dynamicItemContainer}).inject(this.dynamicContainer);
        dynamicItemContainer.addEvents({
            mouseover:function(){
                this.setStyles({"background-color":"#f2f5f7"});
            },
            mouseout:function(){
                this.setStyles({"background-color":"#ffffff"})
            }
        });
        var dynamicItemTop = new Element("div.dynamicItemTop",{styles:this.css.dynamicItemTop}).inject(dynamicItemContainer);
        new Element("div",{styles:this.css.dynamicItemUser,text:data.operator.split("@")[0]}).inject(dynamicItemTop);
        new Element("div",{styles:this.css.dynamicItemDate,text:data.dateTimeStr}).inject(dynamicItemTop);
        new Element("div.dynamicItemDetail",{styles:this.css.dynamicItemDetail,text:data.description}).inject(dynamicItemContainer);
    },
    
    loadDetail:function(){ 

        this.rightContainer.empty();
        var _html  = this.path + this.options.style + "/detailView.html";
        this.rightContainer.loadAll({"html":_html,css:this.cssFile},{"bind": {"lp": this.lp,"data":this.projectData}, "module": this},function(){ 
            var tips = this.projectData.creatorPerson.split("@")[0] + " " + this.projectData.createTime +" " + this.app.lp.common.create
            this.createTip.set("text",tips)
        }.bind(this));

    },
    loadStatus:function(){
        //项目状态
        this.rightContainer.empty();
        //进行中 #409eff
        //已完成 #69b439
        //已取消 #ea5974
        //已搁置 #8a8a8a
        //已归档 #129de6
        var content = new Element('div',{
            styles:{"width":"100%","height":"100%"}
        }).inject(this.rightContainer);
        var _html  = this.path + this.options.style + "/status.html";
        content.loadAll(
            {"html":_html,"css":this.css.cssFile}, {"bind":{"lp":this.app.lp.project.status,"data":this.projectData},"module":this},
            function(){ 
                  
                //1、如果是管理员全放开
                //2、如果是负责人
                //2.1 如果已归档，隐藏所有，显示已归档
                //2.2、如果未归档，显示所有，
                //3、普通权限，显示当前的状态

                if(this.projectData.control.sysAdmin){
                    //可修改
                    if(this.projectData.workStatus !="archived"){
                        content.getElement('.setting_status_item_archived').addEvents({
                            mouseover:function(){
                                this.setStyles({"background-color":"#129de6","color":"#ffffff"})
                            },
                            mouseout:function(){
                                this.setStyles({"background-color":"#f8f8f8","color":"#999999"})
                            }
                        })
                    }
                }else if(this.projectData.control.edit){
                    if(this.projectData.workStatus == "archived"){
                        content.getElements('.setting_status_item').destroy();
                    }else{
                        //content.getElements('.setting_status_item_admin').destroy();
                        content.getElement('.setting_status_item_archived').addEvents({
                            mouseover:function(){
                                this.setStyles({"background-color":"#129de6","color":"#ffffff"})
                            },
                            mouseout:function(){
                                this.setStyles({"background-color":"#f8f8f8","color":"#999999"})
                            }
                        })
                    }
                }else{
                    content.getElements('.setting_status_item').hide();
                    content.getElements('.setting_status_item_admin').hide();
                }

                if(this.projectData.workStatus == 'processing'){
                    content.getElement('.setting_status_item_processing').setStyles({"display":"flex","background-color":"#409eff","color":"#ffffff"});
                }else if(this.projectData.workStatus == 'delay'){
                    content.getElement('.setting_status_item_delay').setStyles({"display":"flex","background-color":"#8a8a8a","color":"#ffffff"});
                }else if(this.projectData.workStatus == 'completed'){
                    content.getElement('.setting_status_item_completed').setStyles({"display":"flex","background-color":"#69b439","color":"#ffffff"});
                }else if(this.projectData.workStatus == 'archived'){
                    content.getElement('.setting_status_item_archived').setStyles({"display":"flex","background-color":"#129de6","color":"#ffffff"});
                }

            }.bind(this)
        )
    },
    setStatus:function(status,e){
        var target = e.currentTarget;
        
        //1、如果已归档，只有管理员才能修改
        //2、如果负责人，也可以改

        if((this.projectData.control.sysAdmin || this.projectData.control.edit) && this.projectData.workStatus != status){
            if(this.projectData.workStatus != status){
                this.rootActions.ProjectAction.updateProjectStatus(this.projectData.id,{workStatus:status},function(){
                    this.projectData.workStatus = status;
                    this.loadStatus()
                }.bind(this))
            }
        }
        // else if(this.projectData.control.edit){
        //     if(this.projectData.workStatus != status){
        //         this.rootActions.ProjectAction.updateProjectStatus(this.projectData.id,{workStatus:status},function(){
        //             this.projectData.workStatus = status;
        //             this.loadStatus()
        //         }.bind(this))
        //     }
        // }
     
    },
    projectSave:function(){
        //保存按钮
        var _valueList = ['title','source','objective','description'];
        _valueList.forEach(function(v){
            if(this[v]) this.projectData[v] = this[v].get("value")
        }.bind(this));

        if(this.projectData.title == ""){
            this.app.notice(this.lp.projectGenerals.title+this.app.lp.common.notEmpty,'error');
            return;
        }
        if(this.projectData.manageablePersonList.length == 0){
            this.app.notice(this.lp.projectGenerals.executor+this.app.lp.common.notEmpty,'error');
            return;
        }
        
        
        this.actions.save(this.projectData,function(json){
            if(json.type=='success'){
                this.app.notice(this.app.lp.common.save + this.app.lp.common.success)
            }
        }.bind(this))
    },
    
    projectInfor:function(callback){
        if(this.data.id){
            this.actions.get(this.data.id,function(json){ 
                this.projectData = json.data;
                if(callback)callback(json.data)
            }.bind(this));

        }
    }

});
