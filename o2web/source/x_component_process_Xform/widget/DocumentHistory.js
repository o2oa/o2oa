MWF.xApplication.process.Xform.widget = MWF.xApplication.process.Xform.widget || {};
MWF.xApplication.process.Xform.widget.DocumentHistory = new Class({
    Implements: [Options, Events],
    options: {
        "speed": 1,
        "fxTime": 500,
        "inforTime": 2000
    },
    initialize: function(documentEditor, options){
        this.setOptions(options);
        this.documentEditor = documentEditor;
        this.css = this.documentEditor.css;
    },
    load: function(callback){
        this.getHistroyDocumentList(function(){
            if (this.historyDocumentList && this.historyDocumentList.length){
                this.getHistoryDataList(function(){
                    this.createHistoryToolbar();
                    this.createHistoryListNode();

                    this.documentEditor.options.pageShow = "single";
                    this.documentEditor.resetData();

                    this.beginDiffHistory(function(){
                        this.loadHistoryToolbar();
                        this.loadHistoryList();

                        if (callback) callback();
                    }.bind(this));
                }.bind(this));
            }
        }.bind(this));
    },
    createHistoryToolbar: function(){
        this.documentEditor.documentToolbarNode = this.documentEditor.toolbarNode;
        this.toolbarNode = this.documentEditor.toolbarNode.clone(true);
        this.toolbarNode.inject(this.documentEditor.toolbarNode, "after");
        this.documentEditor.toolbarNode = this.toolbarNode;
        this.documentEditor.documentToolbarNode.hide();
        this.toolbarNode.empty();
        if (this.documentEditor.sidebarNode) this.documentEditor.sidebarNode.hide();
    },

    loadHistoryToolbar: function(){
        var html = "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/documenteditoricon/play.png\" title=\""+MWF.xApplication.process.Xform.LP.documentHistory.play+"\" MWFButtonAction=\"play\"></span>";
        html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/documenteditoricon/pause.png\" title=\""+MWF.xApplication.process.Xform.LP.documentHistory.pause+"\" MWFButtonAction=\"pause\"></span>";
        html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/documenteditoricon/stop.png\" title=\""+MWF.xApplication.process.Xform.LP.documentHistory.stop+"\" MWFButtonAction=\"stopPlay\"></span>";
        html += "<span MWFnodetype=\"MWFToolBarSeparator\"></span>";
        html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/documenteditoricon/prev.png\" title=\""+MWF.xApplication.process.Xform.LP.documentHistory.prev+"\" MWFButtonAction=\"prev\"></span>";
        html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/documenteditoricon/next.png\" title=\""+MWF.xApplication.process.Xform.LP.documentHistory.next+"\" MWFButtonAction=\"next\"></span>";
        html += "<span MWFnodetype=\"MWFToolBarSeparator\"></span>";
        html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/documenteditoricon/exit.png\" title=\""+MWF.xApplication.process.Xform.LP.documentHistory.exit+"\" MWFButtonAction=\"exit\" MWFButtonText=\""+MWF.xApplication.process.Xform.LP.documentHistory.exit+"\"></span>";
        html += "<span MWFnodetype=\"MWFToolBarSeparator\"></span>";

        var text = MWF.xApplication.process.Xform.LP.documentHistory.diff_patch_count;
        text = text.replace(/{history}/, this.historyDataList.length).replace(/{diff}/, this.diffCount);

        html += "<span style='float: left; line-height: 24px; color: #666666; margin-left: 10px'>"+text+"</span>";

        this.toolbarNode.set("html", html);

        MWF.require("MWF.widget.Toolbar", function() {
            this.toolbar = new MWF.widget.Toolbar(this.toolbarNode, {"style": "documentEdit"}, this);
            this.toolbar.load();
            this.checkToolbar();
        }.bind(this));
    },
    checkToolbar: function(){
        if (this.toolbar){
            if (this.playing){
                if (this.stop){
                    this.toolbar.childrenButton[0].enable();
                    this.toolbar.childrenButton[1].disable();

                    if (this.patchIndex==0 && this.diffIndex==0){
                        this.toolbar.childrenButton[3].disable();
                        if (this.diffPatch.length) this.toolbar.childrenButton[4].enable();
                    }else{
                        if (this.patchIndex<this.diffPatch.length-1){
                            this.toolbar.childrenButton[3].enable();
                            this.toolbar.childrenButton[4].enable();
                        }else if (this.patchIndex<this.diffPatch.length && this.diffIndex < this.diffPatch[this.patchIndex].patch.diffs.length){
                            this.toolbar.childrenButton[3].enable();
                            this.toolbar.childrenButton[4].enable();
                        }else{
                            if (this.diffPatch.length) this.toolbar.childrenButton[3].enable();
                            this.toolbar.childrenButton[4].disable();
                        }
                    }
                }else{
                    this.toolbar.childrenButton[0].disable();
                    this.toolbar.childrenButton[1].enable();

                    this.toolbar.childrenButton[3].disable();
                    this.toolbar.childrenButton[4].disable();
                }
                this.toolbar.childrenButton[2].enable();

            }else{
                this.toolbar.childrenButton[0].enable();
                this.toolbar.childrenButton[1].disable();
                this.toolbar.childrenButton[2].disable();

                if (this.patchIndex==0 && this.diffIndex==0){
                    this.toolbar.childrenButton[3].disable();
                    if (this.diffPatch.length) this.toolbar.childrenButton[4].enable();
                }else{
                    if (this.patchIndex<this.diffPatch.length-1){
                        this.toolbar.childrenButton[3].enable();
                        this.toolbar.childrenButton[4].enable();
                    }else if (this.patchIndex<this.diffPatch.length && this.diffIndex < this.diffPatch[this.patchIndex].patch.diffs.length){
                        this.toolbar.childrenButton[3].enable();
                        this.toolbar.childrenButton[4].enable();
                    }else{
                        if (this.diffPatch.length) this.toolbar.childrenButton[3].enable();
                        this.toolbar.childrenButton[4].disable();
                    }
                }
            }
        }
    },
    createHistoryListNode: function(){
        this.historyListAreaNode = new Element("div", {"styles": this.css.historyListAreaNode}).inject(this.documentEditor.contentNode, "before");
        this.documentEditor.contentNode.setStyle("width", "auto");
        this.documentEditor.zoom(1);
        this.documentEditor._checkScale();

        var size = this.documentEditor.node.getSize();
        var toolbarSize = this.documentEditor.toolbarNode.getSize();
        var h = size.y-toolbarSize.y;
        this.historyListAreaNode.setStyle("height", ""+h+"px");

        this.historyListTitleAreaNode = new Element("div", {"styles": this.css.historyListTitleAreaNode}).inject(this.historyListAreaNode);
        this.historyListContentAreaNode = new Element("div", {"styles": this.css.historyListContentAreaNode}).inject(this.historyListAreaNode);

        var y = this.historyListContentAreaNode.getEdgeHeight();
        var title_y = this.historyListTitleAreaNode.getComputedSize().totalHeight;
        h = h-y-title_y;
        this.historyListContentAreaNode.setStyle("height", ""+h+"px");

        var text = MWF.xApplication.process.Xform.LP.documentHistory.diff_patch_count;
        text = text.replace(/{history}/, this.historyDataList.length).replace(/{diff}/, this.diffCount);



        var insertStr = MWF.xApplication.process.Xform.LP.documentHistory.insertTimes;
        var deleteStr = MWF.xApplication.process.Xform.LP.documentHistory.deleteTimes;
        insertStr = insertStr.replace(/{times}/, this.diffInsertCount);
        deleteStr = deleteStr.replace(/{times}/, this.diffDeleteCount);

        this.historyListTitleNode = new Element("div", {"styles": this.css.historyListTitleNode, "text": text}).inject(this.historyListAreaNode);
        this.historyListTitleInsertNode = new Element("div", {"styles": this.css.historyListTitleInsertNode, "text": text}).inject(this.historyListAreaNode);
        this.historyListTitleDeleteNode = new Element("div", {"styles": this.css.historyListTitleDeleteNode, "text": text}).inject(this.historyListAreaNode);


        documentHistory


        diffDeleteCount
        this.historyListTitleAreaNode.set("", text);



    },
    loadHistoryList: function(){
        var original = this.historyDataList[0];
        this.diffPatch.each(function(patchObj){
            this.createHistoryListItem(patchObj);
        }.bind(this));
    },
    createHistoryListItem: function(patchObj){
        new MWF.xApplication.process.Xform.widget.DocumentHistory.Item(this, patchObj);
    },


    getHistoryDataList: function(callback){
        if (this.documentEditor.historyDataList){
            this.getHistoryDataListFinish(this.documentEditor.historyDataList);
            if (callback) callback();
        }else{
            var historyDataList = [];
            var getDataCount = 0;
            var idx = 0;

            var checkBeginDiffHistory = function(){
                if (getDataCount>=this.historyDocumentList.length){
                    this.getHistoryDataListFinish(historyDataList);
                    if (callback) callback();
                }
            }.bind(this);

            for (var i=this.historyDocumentList.length-1; i>=0; i--){
                historyDataList.push(null);
                this.getHistroyDocumentData(this.historyDocumentList[i].id, function(){
                    getDataCount++;
                    checkBeginDiffHistory();
                }.bind(this), idx, historyDataList);
                idx++;
            }
        }
    },
    getHistoryDataListFinish: function(historyDataList){
        this.documentEditor.historyDataList = historyDataList;
        this.historyDataList = historyDataList;
        var currentData = {
            "data": this.documentEditor.data.filetext,
            "person": layout.session.user.distinguishedName,
            "activityName": this.documentEditor.form.businessData.activity.name,
            "createTime" : (new Date()).format("db")
        };
        this.historyDataList.push(currentData);
    },
    getHistroyDocumentData: function(id, callback, i, historyDataList){
        o2.Actions.load("x_processplatform_assemble_surface").DocumentVersionAction.get(id, function(json){
            if (historyDataList) historyDataList[i] = json.data;
            if (callback) callback(json.data);
        }.bind(this));
    },
    getHistroyDocumentList: function(callback){
        if (!this.documentEditor.historyDocumentList){
            var id = this.documentEditor.form.businessData.work.job;
            o2.Actions.load("x_processplatform_assemble_surface").DocumentVersionAction.listWithJobCategory(id, this.json.id, function(json){
                this.historyDocumentList = json.data;
                this.documentEditor.historyDocumentList = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            this.historyDocumentList = this.documentEditor.historyDocumentList;
            if (callback) callback();
        }
    },

    beginDiffHistory: function(callback){
        o2.load("/o2_lib/diff-match-patch/diff_match_patch_uncompressed.js", function(){
            this.initAnimation();
            if (callback) callback();
        }.bind(this));
    },
    initAnimation: function(){
        this.diffPatch =  this.diffHistroy();
        this.diffCount = 0;
        this.diffInsertCount = 0;
        this.diffDeleteCount = 0;
        this.diffPatch.each(function(patch){
            patch.patch.diffs.each(function(diff){
                if (diff[0]!=0) this.diffCount++;
                if (diff[0]==-1) this.diffDeleteCount++;
                if (diff[0]==1) this.diffInsertCount++;
            }.bind(this));
        }.bind(this));

        // this.initData();
        this.initAnimationStatus();
    },
    initData: function(){
        this.currentHistoryData = this.historyDataList[0].data;
        this.documentEditor.layout_filetext.set("html", this.currentHistoryData);
    },
    initAnimationStatus: function(){
        this.patchIndex = 0;
        this.diffIndex = 0;
        this.currentDiffs = null;
        this.stop = false;
        this.step = false;
        this.playing = false;
        this.reverse = false;
        this.options.fxTime = 500;
        this.options.inforTime = 2000;

        this.checkToolbar();
    },
    doAnimationAuto: function(){
        this.playing = true;
        this.checkToolbar();
        this.doPatchAnimation(function(){
            this.patchIndex = 0;
            this.diffIndex = 0;
            this.playing = false;
            this.documentEditor.resetData();
            this.checkToolbar();
        }.bind(this));
    },
    do: function(){
        if (this.nextPlayPrefixFunction){
            this.nextPlayPrefixFunction();
            this.nextPlayPrefixFunction = null;
        }else{
            this.doAnimationAuto();
        }
    },
    play: function(){
        this.reverse = false;
        this.stop = false;
    //    this.checkToolbar();
        this.options.fxTime = 500;
        this.options.inforTime = 2000;

        this.toolbar.childrenButton[0].disable();
        this.toolbar.childrenButton[3].disable();
        this.toolbar.childrenButton[4].disable();

        if (!this.playing){
            this.initData();
            this.initAnimationStatus();
        }
        this.do();
    },
    stopPlay: function(){
        if (this.playing){
            this.stop = true;
            this.playing = false;

            if (this.nextPlayPrefixFunction){
                this.nextPlayPrefixFunction();
                this.nextPlayPrefixFunction = null;
            }

            this.patchIndex = 0;
            this.diffIndex = 0;
            this.toolbar.childrenButton[1].disable();
            this.toolbar.childrenButton[2].disable();
        }
    },
    pause: function(){
        if (this.playing){
            this.stop = true;
            this.toolbar.childrenButton[1].disable();
            this.toolbar.childrenButton[2].disable();
        }
    },
    next: function(){
        this.reverse = false;
        this.options.fxTime = 0;
        this.options.inforTime = 0;
        this.stop = true;

        this.toolbar.childrenButton[3].disable();
        this.toolbar.childrenButton[4].disable();

        if (!this.playing) this.initData();
        this.do();
    },
    prev: function(){
        this.reverse = true;
        this.options.fxTime = 0;
        this.options.inforTime = 0;
        this.stop = true;

        this.toolbar.childrenButton[3].disable();
        this.toolbar.childrenButton[4].disable();

        if (!this.playing) this.initData();
        this.do();
    },
    exit: function(){
        this.initAnimationStatus();
        this.options.fxTime = 0;
        this.options.inforTime = 0;
        if (this.nextPlayPrefixFunction){
            this.nextPlayPrefixFunction(function(){
                this.documentEditor.toolbarNode = this.documentEditor.documentToolbarNode;
                this.documentEditor.toolbarNode.show();
                if (this.documentEditor.sidebarNode) this.documentEditor.sidebarNode.show();
                this.documentEditor.resizeToolbar();
            }.bind(this));
            this.nextPlayPrefixFunction = null;
        }else{
            this.documentEditor.toolbarNode = this.documentEditor.documentToolbarNode;
            this.documentEditor.toolbarNode.show();
            if (this.documentEditor.sidebarNode) this.documentEditor.sidebarNode.show();
            this.documentEditor.resizeToolbar();
        }

        this.historyListAreaNode.destroy();
        this.historyListAreaNode = null;
        this.documentEditor.zoom(1);
        this.documentEditor._checkScale();

        this.documentEditor.resetData();
        this.toolbarNode.hide();
    },
    active: function(callback){
        this.getHistroyDocumentList(function(){
            if (this.historyDocumentList && this.historyDocumentList.length){
                this.getHistoryDataList(function(){
                    this.documentEditor.options.pageShow = "single";
                    this.documentEditor.resetData();

                    this.beginDiffHistory(function(){
                        this.documentEditor.resetData();
                        this.toolbarNode.show();
                        this.documentEditor.documentToolbarNode = this.documentEditor.toolbarNode;
                        this.documentEditor.documentToolbarNode.hide();
                        if (this.documentEditor.sidebarNode) this.documentEditor.sidebarNode.hide();
                        this.documentEditor.toolbarNode = this.toolbarNode;
                        this.documentEditor.resizeToolbar();

                        var text = MWF.xApplication.process.Xform.LP.documentHistory.diff_patch_count;
                        text = text.replace(/{history}/, this.historyDataList.length).replace(/{diff}/, this.diffCount);
                        this.toolbarNode.getLast().set("html", text);

                        if (callback) callback();
                    }.bind(this));
                }.bind(this));
            }
        }.bind(this));
    },

    doAnimationStep: function(i){
        this.doPatchAnimationStep(i);

        this.documentEditor.resetData();
        this.checkToolbar();
    },

    diffHistroy: function(){
        var diffPatch =  [];
        for (var i=1; i<this.historyDataList.length; i++){
            var earlyDataText = this.historyDataList[i-1].data;
            var laterData = this.historyDataList[i];

            var dmp = new diff_match_patch();
            // dmp.Diff_Timeout = parseFloat(10);
            // dmp.Diff_EditCost = parseFloat(4);
            var diff_d = dmp.diff_main(earlyDataText, laterData.data);
            dmp.diff_cleanupSemantic(diff_d);
            var patch_list = dmp.patch_make(earlyDataText, laterData.data, diff_d);

            patch_list.each(function(patch){
                diffPatch.push({"patch":patch, "obj": laterData});
            }.bind(this));
        }
        return diffPatch;
    },

    doPatchAnimation: function(callback){
        var patchObj = this.diffPatch[this.patchIndex];
        var patch = patchObj.patch;
        var obj = patchObj.obj;
        this.currentDiffs = patch.diffs;
        this.diffIndex = (this.reverse) ? patch.diffs.length-1 : 0;

        // var inforDiv = this.createPatchInforNode(obj);
        //
        // var fx = new Fx.Tween(inforDiv, {property: 'opacity'});
        // fx.start(0,1).chain(function(){

        var start = (this.reverse) ? patch.start1+patch.length2 : patch.start1;
        this.doDiffsAnimation(obj, start, function(){
            //inforDiv.destroy();
            if (this.reverse){
                this.patchIndex--;
                if (this.patchIndex>=0){
                    this.currentHistoryData = this.documentEditor.layout_filetext.get("html");
                    this.doPatchAnimation(callback);
                }else{
                    if (callback) callback();
                }
            }else{
                this.patchIndex++;
                if (this.patchIndex<this.diffPatch.length){
                    this.currentHistoryData = this.documentEditor.layout_filetext.get("html");
                    this.doPatchAnimation(callback);
                }else{
                    if (callback) callback();
                }
            }

        }.bind(this));
    },
    doPatchAnimationStep: function(i){
        if (this.patchIndex>this.diffPatch.length || this.patchIndex<0){
            this.initAnimationStatus();
            this.documentEditor.resetData();
            this.checkToolbar();
        }else{
            var patchObj = this.diffPatch[this.patchIndex];
            var patch = patchObj.patch;
            var obj = patchObj.obj;
            this.currentDiffs = patch.diffs;
            this.diffIndex = 0;

            this.doDiffsAnimation(obj, patch.start1);

            this.patchIndex = this.patchIndex+i;
        }
    },

    createDiifInforNode: function(obj, node, color, insertInfor){
        var insertInforDiv = new Element("div", { "styles": this.css.historyInforNode }).inject(this.documentEditor.node);
        insertInforDiv.setStyle("background", color);
        insertInfor = insertInfor.replace(/{name}/, o2.name.cn(obj.person))
            .replace(/{activity}/, obj.activityName)
            .replace(/{time}/, obj.createTime);
        insertInforDiv.set("html", insertInfor);
        insertInforDiv.position({
            "relativeTo": node,
            "position": 'upperCenter',
            "edge": 'bottomCenter',
            "offset": {
                "x": 0, "y": -10
            }
        });
        return insertInforDiv;
    },
    doDiffsAnimation: function(obj, start, callback){
        var diff = this.currentDiffs[this.diffIndex];
        var filetextNode = this.documentEditor.layout_filetext;
        switch (diff[0]) {
            case DIFF_INSERT:
                var text = diff[1];
                if (this.reverse){
                    start -= text.length;
                    var left = this.currentHistoryData.substring(0, start);
                    var middle = this.currentHistoryData.substring(start, start+diff[1].length);
                    var right = this.currentHistoryData.substring(start+diff[1].length);
                    filetextNode.set("html", left+"<ins style='color:blue;'></ins>"+right);
                }else{
                    var left = this.currentHistoryData.substring(0, start);
                    var right = this.currentHistoryData.substring(start);
                    filetextNode.set("html", left+"<ins style='color:blue;'></ins>"+right);
                }

                var ins = filetextNode.getElement("ins");
                ins.scrollIn();

                this.doInsetAnimation(ins, diff[1], function(invisible){
                    var insertInforDiv = null;
                    if (!invisible){
                        insertInforDiv = this.createDiifInforNode(obj, ins, "#e2edfb", MWF.xApplication.process.Xform.LP.documentHistory.insertContent);
                    }
                    window.setTimeout(function(){
                        var endFunction = function(cb){
                            if (insertInforDiv) insertInforDiv.fade("out");
                            var fx = new Fx.Tween(ins, {property: 'opacity', duration:this.options.speed*this.options.fxTime});
                            fx.start(1.1).chain(function(){
                                if (insertInforDiv) insertInforDiv.destroy();
                                if (this.reverse){
                                    ins.destroy();
                                    this.currentHistoryData = filetextNode.get("html");
                                }else{
                                    data = filetextNode.get("html");
                                    this.currentHistoryData = data.replace(/<ins[\s\S]*\/ins>/m, text);
                                    filetextNode.set("html", this.currentHistoryData);
                                }
                                if (this.playing){
                                    if (this.reverse){
                                        this.diffIndex--;
                                        if (this.diffIndex>=0){
                                            window.setTimeout(function(){this.doDiffsAnimation(obj, start, callback);}.bind(this), this.options.speed*this.options.fxTime);
                                            //this.doDiffsAnimation(obj, start, callback);
                                        }else{
                                            if (callback) callback();
                                        }
                                    }else{
                                        start += text.length;
                                        this.diffIndex++;
                                        if (this.diffIndex<this.currentDiffs.length){
                                            window.setTimeout(function(){this.doDiffsAnimation(obj, start, callback);}.bind(this), this.options.speed*this.options.fxTime);
                                            //this.doDiffsAnimation(obj, start, callback);
                                        }else{
                                            if (callback) callback();
                                        }
                                    }
                                }else{
                                    this.initAnimationStatus();
                                    this.documentEditor.resetData();
                                }
                                if (cb) cb();

                            }.bind(this));
                            if (this.nextPlayPrefixFunction) this.nextPlayPrefixFunction = null;
                        }.bind(this)
                        if (!this.stop || !this.playing) {
                            endFunction();
                        } else{
                            this.nextPlayPrefixFunction = endFunction;
                        }
                        this.checkToolbar();
                    }.bind(this), (invisible ? 100: this.options.speed*this.options.inforTime));
                }.bind(this));
                break;
            case DIFF_DELETE:
                var text = diff[1];
                if (this.reverse){
                    var left = this.currentHistoryData.substring(0, start);
                    //var middle = this.currentHistoryData.substring(start, start+diff[1].length);
                    var right = this.currentHistoryData.substring(start);
                    filetextNode.set("html", left+"<del style='color: red'>"+text+"</del>"+right);
                    start -= text.length;
                }else{
                    var left = this.currentHistoryData.substring(0, start);
                    var middle = this.currentHistoryData.substring(start, start+diff[1].length);
                    var right = this.currentHistoryData.substring(start+diff[1].length);
                    //start -= .length;
                    filetextNode.set("html", left+"<del style='color: red'>"+middle+"</del>"+right);
                }


                var del = filetextNode.getElement("del");
                del.scrollIn();

                this.doDeleteAnimation(del, diff[1], obj, function(deleteInforDiv){
                    // var deleteInforDiv = null;
                    // if (!invisible){
                    //     deleteInforDiv = this.createDiifInforNode(obj, del, "#fbe0e7", MWF.xApplication.process.Xform.LP.documentHistory.deleteContent);
                    // }
                    var invisible = !deleteInforDiv;
                    window.setTimeout(function(){
                        var endFunction = function(cb){
                            if (deleteInforDiv) deleteInforDiv.fade("out");
                            var fx = new Fx.Tween(del, {property: 'opacity', duration:this.options.speed*this.options.fxTime});
                            fx.start(0.5,0).chain(function(){
                                if (deleteInforDiv) deleteInforDiv.destroy();
                                if (this.reverse){
                                    data = filetextNode.get("html");
                                    this.currentHistoryData = data.replace(/<del[\s\S]*\/del>/m, text);
                                    filetextNode.set("html", this.currentHistoryData);
                                }else{
                                    del.destroy();
                                    this.currentHistoryData = filetextNode.get("html");
                                }

                                if (this.playing){
                                    if (this.reverse){
                                        this.diffIndex--;
                                        if (this.diffIndex>=0){
                                            window.setTimeout(function(){this.doDiffsAnimation(obj, start, callback);}.bind(this), this.options.speed*this.options.fxTime);
                                        }else{
                                            if (callback) callback();
                                        }
                                    }else{
                                        this.diffIndex++;
                                        if (this.diffIndex<this.currentDiffs.length){
                                            window.setTimeout(function(){this.doDiffsAnimation(obj, start, callback);}.bind(this), this.options.speed*this.options.fxTime);
                                        }else{
                                            if (callback) callback();
                                        }
                                    }

                                }else{
                                    this.initAnimationStatus();
                                    this.documentEditor.resetData();
                                }
                                if (cb) cb();

                            }.bind(this));
                            if (this.nextPlayPrefixFunction) this.nextPlayPrefixFunction = null;
                        }.bind(this)
                        if (!this.stop || !this.playing) {
                            endFunction();
                        } else{
                            this.nextPlayPrefixFunction = endFunction;
                        }
                        this.checkToolbar();
                    }.bind(this), (invisible ? 100: this.options.speed*this.options.inforTime));
                }.bind(this));
                break;
            case DIFF_EQUAL:
                if (this.reverse){
                    start -= diff[1].length;
                    this.diffIndex--;
                    if (this.diffIndex>=0){
                        this.doDiffsAnimation(obj, start, callback);
                    }else{
                        if (callback) callback();
                    }
                }else{
                    start += diff[1].length;
                    this.diffIndex++;
                    if (this.diffIndex<this.currentDiffs.length){
                        this.doDiffsAnimation(obj, start, callback);
                    }else{
                        if (callback) callback();
                    }
                }

                break;
        }
    },

    doInsetAnimation: function(node, str, callback){
        var tmp = new Element("div", {"html": str});
        if (!tmp.get("text").trim()){
            if (callback) callback(true);
        }else{
            var nodes = tmp.childNodes;
            this.doInsetNodeAnimation(node, nodes, 0, callback);
        }
    },
    doInsetNodeAnimation: function(ins, nodes, idx, callback){
        var node = nodes[idx];
        if (node.nodeType == Node.TEXT_NODE){
            this.doCharAnimation(ins, node.nodeValue, 0, function(){
                idx++;
                if (idx<nodes.length){
                    this.doInsetNodeAnimation(ins, nodes, idx, callback);
                }else{
                    if (callback) callback();
                }
            }.bind(this));
        }else{
            var duration = this.options.speed*this.options.fxTime/nodes.length
            var span = new Element("span", {"styles": {"opacity": 0}}).inject(ins);
            span.appendChild(node);
            var fx = new Fx.Tween(span, {property: 'opacity', duration:duration});
            fx.start(0,1).chain(function(){
                idx++;
                if (idx<nodes.length){
                    this.doInsetNodeAnimation(ins, nodes, idx, callback);
                }else{
                    if (callback) callback();
                }
            }.bind(this));
        }
    },

    doCharAnimation: function(node, str, idx, callback){
        var duration = this.options.speed*this.options.fxTime/str.length;
        var char = str.charAt(idx);
        var span = new Element("span", {"styles": {"opacity": 0}, "html": char}).inject(node);
        var fx = new Fx.Tween(span, {property: 'opacity', duration:duration});
        fx.start(0,1).chain(function(){
            idx++;
            if (idx<str.length){
                this.doCharAnimation(node, str, idx, callback);
            }else{
                if (callback) callback();
            }
        }.bind(this));
    },

    doDeleteAnimation: function(node, str, obj, callback){
        var tmp = new Element("div", {"html": str});
        if (!tmp.get("text").trim()){
            if (callback) callback(null);
        }else{
            deleteInforDiv = this.createDiifInforNode(obj, node, "#fbe0e7", MWF.xApplication.process.Xform.LP.documentHistory.deleteContent);
            var fx = new Fx.Tween(node, {property: 'opacity', duration:this.options.speed*this.options.fxTime});
            fx.start(1,0.5).chain(function(){
                if (callback) callback(deleteInforDiv);
            }.bind(this));
        }
    }
});

MWF.xApplication.process.Xform.widget.DocumentHistory.Item = new Class({
    initialize: function(history, patchObj){
        this.history = history;
        this.documentEditor = this.history.documentEditor;
        this.css = this.history.css;
        this.patchObj = patchObj;
        this.load();
    },
    load: function(){
        var patch = this.patchObj.patch;
        var obj = this.patchObj.obj;
        this.node = new Element("div", {"styles": this.css.historyListItemNode}).inject(this.history.historyListContentAreaNode);
        var patchHtml = "<div style='font-weight: bold; height: 30px; line-height: 30px'>"+o2.name.cn(obj.person)+" ["+obj.activityName+"]</div><div style='height: 20px; line-height: 20px; color:#666666'>"+obj.createTime+"</div>"
        this.patchNode = new Element("div", {"styles": this.css.historyListItemPatchNode, "html": html}).inject(this.node);

        this.diffsNode = new Element("div", {"styles": this.css.historyListItemDiffsNode}).inject(this.node);

        patch.diffs.each(function(diff){
            diffNode = new Element("div", {"styles": this.css.historyListItemDiffNode}).inject(this.diffsNode);
        }.bind(this));
    }
})