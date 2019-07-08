MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.ProjectSetting = new Class({
    Extends: MWF.xApplication.TeamWork.Common.Popup,
    options:{
        "closeByClickMask" : false
    },

    open: function (e) {
        //设置css 和 lp等
        var css = this.css;
        this.cssPath = "/x_component_TeamWork/$ProjectSetting/"+this.options.style+"/css.wcss";
        this._loadCss();
        if(css) this.css = Object.merge(  css, this.css );

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

            this.projectSettingBgText = new Element("div.projectSettingBgText",{styles:this.css.projectSettingBgText,"text":this.lp.projectSettingBgText}).inject(this.formTableArea);
            this.projectSettingBgContainer = new Element("div.projectSettingBgContainer",{styles:this.css.projectSettingBgContainer}).inject(this.formTableArea);
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

                    // var value ;
                    // MWF.xDesktop.requireApp("Template", "widget.ImageClipper",null,false);
                    // this.clipper = new MWF.xApplication.Template.widget.ImageClipper(this.app, {
                    //     "imageUrl": value ? MWF.xDesktop.getImageSrc( value ) : "",
                    //     "aspectRatio": this.options.aspectRatio || 0,
                    //     "ratioAdjustedEnable" : this.options.ratioAdjustedEnable || false,
                    //     "reference": this.options.reference,
                    //     "referenceType": this.options.referenceType,
                    //     "onChange": function () {
                    //         if( this.image )this.image.destroy();
                    //         if(this.imageWrap)this.imageWrap.destroy();
                    //         if( styles.imageWrapStyle ){
                    //             this.imageWrap = new Element("div", { styles : styles.imageWrapStyle}).inject( parent, "top" )
                    //         }
                    //         this.image = new Element("img", {
                    //             "src" : this.clipper.imageSrc
                    //         }).inject( this.imageWrap || parent, "top" );
                    //         if( styles.imageStyle )this.image.setStyles( styles.imageStyle );
                    //         this.image.addEvent("click",function(){
                    //             window.open( MWF.xDesktop.getImageSrc( this.imageId ), "_blank" );
                    //         }.bind(this));
                    //         this.imageId = this.module.imageId = this.clipper.imageId;
                    //         if( this.options.validImmediately ){
                    //             this.module.verify( true )
                    //         }
                    //     }.bind(this)
                    // });
                    // this.clipper.load();




                    MWF.xDesktop.requireApp("TeamWork", "UploadImage", function(){
                        var ui = new MWF.xApplication.TeamWork.UploadImage(this, data, {
                            documentId : this.data.id ||"",
                            onPostOk : function( id ){

                                this.data.icon = id;
                                this.projectSettingBgImg.setStyles({
                                    "background-image":"url('"+MWF.xDesktop.getImageSrc( id )+"')"
                                });
                                //window.open( MWF.xDesktop.getImageSrc( id ), "_blank" );
                            }.bind(this)
                        });
                        ui.open()
                    }.bind(this));
                }.bind(this)
            });
            this.projectSettingContainer = new Element("div.projectSettingContainer",{styles:this.css.projectSettingContainer}).inject(this.formTableArea);
            this.projectSettingTitleContainer = new Element("div.projectSettingTitleContainer",{styles:this.css.projectSettingTitleContainer}).inject(this.projectSettingContainer);
            this.projectSettingTitleText = new Element("div.projectSettingTitleText",{styles:this.css.projectSettingTitleText,text:this.lp.projectTitle}).inject(this.projectSettingTitleContainer);
            this.projectSettingTitleDiv = new Element("div.projectSettingTitleDiv",{styles:this.css.projectSettingTitleDiv}).inject(this.projectSettingTitleContainer);
            this.projectSettingTitleIn = new Element("input.projectSettingTitleIn",{styles:this.css.projectSettingTitleIn,value:rs.title || ""}).inject(this.projectSettingTitleDiv);

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
                                "z-index" : "102"
                            },
                            onClose:function(d){
                                if(d){
                                    this.actions.groupWithIds({ids:d},function(json){
                                        this.groups = json.data;
                                        var tmp = [];
                                        json.data.each(function(ddd){
                                           tmp.push(ddd.name);
                                        });
                                        this.projectSettingGroupValue.set("text",tmp.join())
                                    }.bind(this));
                                }else{
                                    this.groups = [];
                                }
                                //this.newProjectGroupValue.set("text",d)
                            }.bind(this)
                        });
                        gs.load()
                    }.bind(this));
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

            this.projectSettingContainer = new Element("div.projectSettingContainer",{styles:this.css.projectSettingContainer}).inject(this.formTableArea);
            this.projectSettingContainer.setStyles({"height":"120px"});
            this.projectSettingDesText = new Element("div.projectSettingDesText",{styles:this.css.projectSettingDesText,text:this.lp.projectDes}).inject(this.projectSettingContainer);

            this.projectSettingDesContainer = new Element("div.projectSettingDesContainer",{styles:this.css.projectSettingDesContainer}).inject(this.projectSettingContainer);
            this.projectSettingDesIn = new Element("textarea.projectSettingDesIn",{styles:this.css.projectSettingDesIn,value:rs.projectDetail.description||""}).inject(this.projectSettingDesContainer);

            this.projectSettingContainer = new Element("div.projectSettingContainer",{styles:this.css.projectSettingContainer}).inject(this.formTableArea);
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

                    this.actions.projectSave(data,function(json){
                        this.close();
                    }.bind(this));
                }.bind(this)
            });
            this.projectSettingClose = new Element("div.projectSettingClose",{styles:this.css.projectSettingClose,text:this.lp.close}).inject(this.projectSettingContainer);
            this.projectSettingClose.addEvents({
                click:function(){
                    this.close();
                }.bind(this)
            });
            //this.projectSettingAction = new Element("div.projectSettingAction",{styles:this.css.projectSettingAction,text:this.lp.confirm}).inject(this.formTableArea);

        }.bind(this));

    },
    projectInfor:function(callback){
        if(this.data.id){
            this.actions.projectGet(this.data.id,function(json){
                if(callback)callback(json.data)
            }.bind(this));

        }
    },
    groupInfor:function(ids){
        if(!ids) return;
        var resGroups = [];
        ids.each(function(data){
            this.actions.groupGet(data,function(json){

            }.bind(this))
        }.bind(this))
    }



});
