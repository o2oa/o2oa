MWF.xApplication.MinderEditor.MainMenu = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "defaultAction" : "openRecentFiles"
    },
    initialize : function( container, app,  options){
        this.setOptions( options );
        this.container = container;
        this.app = app;
        //this.css = this.app.css;
        this.lp = this.app.lp;
        this.isHidden = false;

        this.path = "../x_component_MinderEditor/$MainMenu/";
        this.cssPath = "../x_component_MinderEditor/$MainMenu/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.itemJson = [
            {
                text : "新建",
                action : "openCreate",
                hasContent : false
            },
            {
                text : "打开",
                action : "openRecentFiles",
                hasContent : true
            },
            {
                text : "另存为",
                action : "openSaveAs",
                hasContent : true
            },
            {
                text : "共享",
                action : "openShare",
                hasContent : true
            },
            {
                text : "历史版本",
                action : "openFileVersion",
                hasContent : true
            }
        ];

        this.load();
    },
    load : function(){
        this.maskNode = new Element("div.maskNode",{
            "styles": this.css.maskNode,
            events : {
                mousedown : function( ev ){
                    this.hide();
                }.bind(this)
            }
        }).inject(this.container);

        this.node = new Element("div.MainMenu", {
            "styles": this.css.node,
            events : {
                mousedown : function( ev ){
                    ev.stopPropagation();
                }
            }
        }).inject(this.container);

        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);

        this.rightContentNode = new Element("div", {"styles": this.css.rightContentNode}).inject(this.node);

        this.loadItems();

        this.show();

        this.resetNodeSizeFun = this.resetNodeSize.bind(this);
        this.app.addEvent("resize", this.resetNodeSizeFun );
    },
    //show : function(){
    //    this.node.setStyle("display","");
    //    this.rightNode.setStyle("display","none");
    //    this.fireEvent("show");
    //    this.resetNodeSize();
    //},
    //hide : function(){
    //    this.node.setStyle("display","");
    //    this.rightNode.setStyle("display","none");
    //    this.fireEvent("hide");
    //},
    loadItems : function(){
        var _self = this;
        this.itemContainer = new Element("div", {"styles": this.css.itemContainer}).inject(this.contentNode);
        this.menuItem = {};
        this.itemJson.each( function( item ){
            var itemNode = new Element("div", {
                "styles": this.css.itemNode,
                "text" : item.text
            }).inject(this.contentNode);
            itemNode.addEvents( {
                mouseover : function(){
                    if( _self.currentItemNode != this )this.setStyles( _self.css.itemNode_over )
                },
                mouseout : function(){
                    if( _self.currentItemNode != this )this.setStyles( _self.css.itemNode )
                },
                click : function(){
                    _self.setCurrentItemNode( this, item.action, item );
                }
            });
            this.menuItem[ item.action  ] = itemNode;
            if( item.action == (this.app.options.menuAction || this.options.defaultAction) )itemNode.click();
        }.bind(this))
    },
    setCurrentItemNode : function( node , action, item ){
        if( this.currentItemNode && item.hasContent ){
            this.currentItemNode.setStyles( this.css.itemNode );
        }
        if( this[action] ){
            this[action]();
        }
        if( item.hasContent ){
            this.currentItemNode = node;
            node.setStyles( this.css.itemNode_current );
        }
    },
    trigger : function(){
        this.isHidden ? this.show( true ) : this.hide( true )
    },
    hide: function( isFireEvent ){
        this.maskNode.setStyle("display","none");
        this.isHidden = true;
        this.node.setStyle("display", "none");
        this.fireEvent("hide");

        //var x = this.node.getSize().x + 9;
        //var fx = new Fx.Morph(this.node, {
        //    "duration": "300",
        //    "transition": Fx.Transitions.Expo.easeOut
        //});
        //fx.start({
        //    "opacity": 0
        //}).chain(function(){
        //    this.isHidden = true;
        //    //this.node.setStyle("display", "none");
        //    this.node.setStyles({
        //        "left": "-"+x+"px"
        //    });
        //    this.fireEvent("hide");
        //}.bind(this));
    },
    show: function( action ){
        if( action ){
            this.menuItem[ action  ].click()
        }else if( this.currentItemNode ){
            this.currentItemNode.click();
        }
        this.resetNodeSize();
        this.maskNode.setStyle("display","");
        this.isHidden = false;
        this.node.setStyles({"display":""});
        this.fireEvent("show");

        //this.resetNodeSize();
        //var fx = new Fx.Morph(this.node, {
        //    "duration": "500",
        //    "transition": Fx.Transitions.Expo.easeOut
        //});
        //fx.start({
        //    "opacity": 1
        //}).chain(function(){
        //    this.node.setStyles({
        //        "left": "0px"
        //    });
        //    this.fireEvent("show");
        //    this.isHidden = false;
        //}.bind(this))
    },
    resetNodeSize: function(){
        var size = this.container.getSize();

        this.contentNode.setStyle("height", size.y - 84 );
        this.rightContentNode.setStyle("height", size.y - 86 );

        this.setShareRecordListHeight();

        this.setFileVersionListHeight();

        //this.trapezoid.setStyle("top", ( (size.y - 50)/2 - this.trapezoid.getSize().y/2 ));

        //var y = size.y - 395;
        //var meetContainerY = this.meetingItemContainer.getSize().y + 12;
        //this.scrollNode.setStyle("height", Math.min( y, meetContainerY ) );
    },
    setShareRecordListHeight : function(){
        if( this.shareRecordList ){
            var size = this.container.getSize();
            this.shareRecordList.setStyle("height", size.y - 256 );
        }
    },
    setFileVersionListHeight : function(){
        if( this.fileVersionList ){
            var size = this.container.getSize();
            this.fileVersionList.setStyle("height", size.y - 156 );
        }
    },
    getSize : function(){
        //var size = this.node.getSize();
        //return {
        //    x : this.isHidden ? 9 : size.x,
        //    y : size.y
        //}
        return { x : 9, y : 0 }
    },
    empty : function(){
        this.shareRecordList = null;
        this.fileVersionList = null;
        this.rightContentNode.empty();
    },
    reload : function(){
        this.destory();
    },
    destory : function(){
        this.maskNode.destroy();
        this.app.removeEvent("resize", this.resetNodeSizeFun );
        //this.app.node.removeEvent("mousedown", this.hideFun);
        this.node.destory();
    },
    openRecentFiles : function(){
        var _self = this;
        this.empty();
        new Element("div.rightTitleNode", {
            "text" : "打开",
            "styles" : this.css.rightTitleNode
        }).inject( this.rightContentNode );


        var listTitle = new Element("div.listTitle", {
            "text" : "最近修改的文件",
            "styles" : this.css.listTitle
        }).inject( this.rightContentNode );

        var documentArea = new Element("div.listArea", {
            "styles" : this.css.documentArea
        }).inject( this.rightContentNode );

        this.app.restActions.listNextMindWithFilter( "(0)", 5, { orderField : "updateTime", orderType : "DESC" }, function( json ){
            (json.data || []).each( function( d ){
                var documentNode = new Element("div.documentNode", {
                    "styles" : this.css.documentNode
                }).inject( documentArea );

                documentNode.addEvents( {
                    "mouseover" : function(){
                        documentNode.setStyles( this.css.documentNode_over )
                    }.bind(this),
                    "mouseout" : function(){
                        documentNode.setStyles( this.css.documentNode )
                    }.bind(this),
                    "click" : function(){
                        this.openMinder( d );
                        this.hide();
                    }.bind(this)
                });

                var iconNode = Element("div.documentIconNode", {
                    "styles" : this.css.documentIconNode
                }).inject( documentNode );

                var rightNode = Element("div.documentRightNode", {
                    "styles" : this.css.documentRightNode
                }).inject( documentNode );

                Element("div.documentTextNode", {
                    "text" : d.name,
                    "styles" : this.css.documentTextNode
                }).inject( rightNode );

                Element("div.documentMemoNode", {
                    "text" : "修改于"+d.updateTime,
                    "styles" : this.css.documentMemoNode
                }).inject( rightNode );

            }.bind(this));
            if( !json.data || json.data.length == 0 ){
                new Element("div.listItemTextNode", {
                    "text" : "没有找到最近修改的文件",
                    "styles" : this.css.listItemTextNode
                }).inject( documentArea );
            }else{
                var action = new Element("div.listItemActionNode", {
                    "text" : "打开列表，查看更多文件",
                    "styles" : this.css.listItemActionNode,
                    "events" : {
                        "mouseover" : function(){
                            this.setStyles( _self.css.listItemActionNode_over )
                        },
                        "mouseout" : function(){
                            this.setStyles( _self.css.listItemActionNode )
                        },
                        "click" : function(ev){
                            _self.openMinderList(ev);
                            _self.hide();
                        }
                    }
                }).inject( documentArea );
                action.setStyle("margin-top", "10px");
            }
        }.bind(this));


        var actionArea = new Element("div.rightActionArea", {
            "styles" : this.css.rightActionArea
        }).inject( this.rightContentNode );

        this.createActionNode( actionArea, "import", "打开本地文件", "上传本地脑图文件，并用脑图编辑", function(){
            this.app.openSaveAsDialog();
            this.hide();
        }.bind(this) );
    },
    openMinderList : function( documentData ){
        var appId = "Minder";
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "Minder", {
                "appId" : appId
            });
        }
    },
    openMinder : function( documentData ){
        var appId = "MinderEditor"+documentData.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "MinderEditor", {
                "appId" : appId,
                "folderId" : documentData.folderId,
                "id" : documentData.id,
                "isEdited" : true,
                "isNew" : false
            });
        }
    },
    openCreate : function(){
        this.app.openNewMinderDialog();
        this.hide();
    },
    openSaveAs : function(){
        this.empty();
        new Element("div.rightTitleNode", {
            "text" : "另存为",
            "styles" : this.css.rightTitleNode
        }).inject( this.rightContentNode );

        var actionArea = new Element("div.rightActionArea", {
            "styles" : this.css.rightActionArea
        }).inject( this.rightContentNode );

        this.createActionNode( actionArea, "saveas", "另存为", "保存副本到文件夹", function(){
            this.app.openSaveAsDialog();
            this.hide();
        }.bind(this) );
        this.createActionNode( actionArea, "edit", "重命名", "重命名此文件", function(){
            this.app.openRenameDialog();
            this.hide();
        }.bind(this) );
        this.createActionNode( actionArea, "download", "导出", "导出图片到本地", function(){
            // this.app.openExportDialog();
            this.exportAsImage();
            this.hide();
        }.bind(this));

    },
    exportAsImage : function(){
        var title = this.app.minder.getRoot().getText();

        var converter = new MWF.xApplication.MinderEditor.Converter(this.app, this.app.minder, {
            "background": "#ffffff",
            "zoom": 2
        });
        converter.toPng(null, null, function( img ){
            var id = this.app.data.id;
            var formData = new FormData();
            formData.append('file', img, title+".png");
            formData.append('site', id);

            debugger;

            MWF.xDesktop.uploadImageByScale( id, "mindInfo", -1, formData, img,
                function(json){
                    var url = o2.Actions.load("x_file_assemble_control").FileAction.action.actions.downloadStream.uri.replace( "{id}", json.data.id );
                    url = o2.filterUrl( o2.Actions.load("x_file_assemble_control").FileAction.action.getAddress() + url);
                    window.open(url);
                }.bind(this)
            );

        }.bind(this), function(){
            this.app.notice("抱歉，脑图中有外网图片，无法导出","error")
        }.bind(this))
    },
    openFileVersion : function(){
        var _self = this;
        this.empty();
        new Element("div.rightTitleNode", {
            "text" : "历史版本",
            "styles" : this.css.rightTitleNode
        }).inject( this.rightContentNode );


        //var listTitle = new Element("div.listTitle", {
        //    "text" : "分享记录",
        //    "styles" : this.css.listTitle
        //}).inject( this.rightContentNode );

        var listArea = this.fileVersionList = new Element("div.listArea", {
            "styles" : this.css.listArea
        }).inject( this.rightContentNode );

        if( this.app.data.id ){
            this.app.restActions.listVersionsWithMindId( this.app.data.id, function( json ){
                (json.data || []).each( function( d ){
                    var listItemNode = new Element("div.listItemNode", {
                        "styles" : this.css.listItemNode
                    }).inject( listArea );

                    new Element("div.listItemTextNode", {
                        "text" : d.creator.split("@")[0] + "创建于" + d.createTime ,
                        "styles" : this.css.listItemTextNode
                    }).inject( listItemNode );

                    if( this.app.userName == d.creator ){
                        new Element("div.listItemActionNode", {
                            "text" : "还原",
                            "styles" : this.css.listItemActionNode,
                            "events" : {
                                "mouseover" : function(){
                                    this.setStyles( _self.css.listItemActionNode_over )
                                },
                                "mouseout" : function(){
                                    this.setStyles( _self.css.listItemActionNode )
                                },
                                "click" : function(ev){
                                    _self.restoreVersion(ev, d );
                                }
                            }
                        }).inject( listItemNode );
                    }
                    this.setFileVersionListHeight();
                }.bind(this));
                if( !json.data || json.data.length == 0 ){
                    new Element("div.listItemTextNode", {
                        "text" : "没有找到历史版本",
                        "styles" : this.css.listItemTextNode
                    }).inject( listArea );
                }
            }.bind(this))
        }else{
            new Element("div.listItemTextNode", {
                "text" : "没有找到历史版本",
                "styles" : this.css.listItemTextNode
            }).inject( listArea );
        }

    },
    restoreVersion : function( ev, data ){
        this.app.restActions.viewMindVersionWithId( data.id, function( json ){
            this.app.isMovingCenter = true;
            this.app.data.content = json.data.content ?  JSON.parse(json.data.content) : { content : { data : {} } };
            this.app.minder.importJson(this.app.data.content);
        }.bind(this))
    },
    openShare : function(){
        var _self = this;
        this.empty();
        new Element("div.rightTitleNode", {
            "text" : "共享",
            "styles" : this.css.rightTitleNode
        }).inject( this.rightContentNode );

        var actionArea = new Element("div.rightActionArea", {
            "styles" : this.css.rightActionArea
        }).inject( this.rightContentNode );

        this.createActionNode( actionArea, "share", "分享", "邀请其他人查看文件", function(){
           this.app.openShareDialog();
            this.hide();
        }.bind(this));

        var listTitle = new Element("div.listTitle", {
            "text" : "分享记录",
            "styles" : this.css.listTitle
        }).inject( this.rightContentNode );

        this.documentArea = this.shareRecordList = new Element("div.documentArea", {
            "styles" : this.css.documentArea
        }).inject( this.rightContentNode );

        if( this.app.data.id ){
            this.loadShareRecordList()
        }else{
            new Element("div.listItemTextNode", {
                "text" : "没有找到分享记录",
                "styles" : this.css.listItemTextNode
            }).inject( this.documentArea );
        }

    },
    loadShareRecordList : function(){
        var _self = this;
        this.documentArea.empty();
        this.app.restActions.listShareRecordsWithMindId( this.app.data.id, function( json ){
                //(json.data || []).each( function( d ){
                //var listItemNode = new Element("div.listItemNode", {
                //    "styles" : this.css.listItemNode
                //}).inject( this.listArea );
                //
                //new Element("div.listItemTextNode", {
                //    "text" : d.source.split("@")[0] + "于" + d.createTime + "分享给" + d.target.split("@")[0],
                //    "styles" : this.css.listItemTextNode
                //}).inject( listItemNode );
                //
                //if( this.app.userName == d.source ){
                //    new Element("div.listItemActionNode", {
                //        "text" : "取消分享",
                //        "styles" : this.css.listItemActionNode,
                //        "events" : {
                //            "mouseover" : function(){
                //                this.setStyles( _self.css.listItemActionNode_over )
                //            },
                //            "mouseout" : function(){
                //                this.setStyles( _self.css.listItemActionNode )
                //            },
                //            "click" : function(ev){
                //                _self.cancelShare(ev, d );
                //            }
                //        }
                //    }).inject( listItemNode );
                //}

                (json.data || []).each( function( d ){
                    var documentNode = new Element("div.documentNode", {
                        "styles" : this.css.documentNode
                    }).inject( this.documentArea );

                    documentNode.addEvents( {
                        "mouseover" : function(){
                            documentNode.setStyles( this.css.documentNode_over )
                        }.bind(this),
                        "mouseout" : function(){
                            documentNode.setStyles( this.css.documentNode )
                        }.bind(this)
                    });

                    //var iconNode = Element("div.documentIconNode", {
                    //    "styles" : this.css.documentIconNode
                    //}).inject( documentNode );

                    var rightNode = Element("div.documentRightNode", {
                        "styles" : this.css.documentRightNode
                    }).inject( documentNode );

                    Element("div.documentTextNode", {
                        "text" : d.source.split("@")[0] + "分享给" + d.target.split("@")[0],
                        "styles" : this.css.documentTextNode
                    }).inject( rightNode );

                    var documentBottomNode = Element("div.documentBottomNode", {
                        "styles" : this.css.documentBottomNode
                    }).inject( rightNode );

                    Element("div.documentMemoNode", {
                        "text" : "于"+ d.createTime,
                        "styles" : this.css.documentMemoNode
                    }).inject( documentBottomNode );

                    if( this.app.userName == d.source ){
                        new Element("div.documentActionNode", {
                            "text" : "取消分享",
                            "styles" : this.css.documentActionNode,
                            "events" : {
                                "mouseover" : function(){
                                    this.setStyles( _self.css.documentActionNode_over )
                                },
                                "mouseout" : function(){
                                    this.setStyles( _self.css.documentActionNode )
                                },
                                "click" : function(ev){
                                    _self.cancelShare(ev, d );
                                }
                            }
                        }).inject( documentBottomNode );
                    }
                    this.setShareRecordListHeight();
            }.bind(this));
            if( !json.data || json.data.length == 0 ){
                new Element("div.listItemTextNode", {
                    "text" : "没有找到分享记录",
                    "styles" : this.css.listItemTextNode
                }).inject( this.documentArea );
            }
        }.bind(this))
    },
    cancelShare : function( ev, data ){
        var _self = this;
        this.app.confirm("warn", ev, "取消分享确认", "是否取消对"+data.target.split("@")[0]+"的分享?", 350, 120, function () {
            _self.app.restActions.cancelShareMind( data.id , {}, function(){
                _self.loadShareRecordList();
                _self.app.notice( "取消分享成功！" )
            });
            this.close();
        }, function () {
            this.close();
        });
    },
    createActionNode : function( actionArea, image, text, memo, action ){
        var actionNode = Element("div.rightActionNode", {
            "styles" : this.css.rightActionNode
        }).inject( actionArea );
        actionNode.addEvents( {
            "mouseover" : function(){
                actionNode.setStyles( this.css.rightActionNode_over )
            }.bind(this),
            "mouseout" : function(){
                actionNode.setStyles( this.css.rightActionNode )
            }.bind(this),
            "click" : function(){
                action()
            }.bind(this)
        });

        var iconNode = Element("div.rightActionIconNode", {
            "styles" : this.css.rightActionIconNode
        }).inject( actionNode );
        if(image)iconNode.setStyle("background-image", "url("+this.path+this.options.style+"/icon/"+image+".png)");

        var rightNode = Element("div.rightActionRightNode", {
            "styles" : this.css.rightActionRightNode
        }).inject( actionNode );

        Element("div.rightActionTextNode", {
            "text" : text,
            "styles" : this.css.rightActionTextNode
        }).inject( rightNode );

        Element("div.rightActionMemoNode", {
            "text" : memo,
            "styles" : this.css.rightActionMemoNode
        }).inject( rightNode );
    }
});