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
    is_iPad: function(){
        var ua = navigator.userAgent.toLowerCase();
        return (ua.match(/iPad/i)=="ipad");
    },
    load: function(callback){
        this.getHistroyDocumentList(function(){
            if (this.historyDocumentList && this.historyDocumentList.length){
                this.getHistoryDataList(function(){
                    this.createHistoryToolbar();
                    if (!layout.mobile || this.is_iPad()) this.createHistoryListNode();

                    this.documentEditor.options.pageShow = "single";
                    debugger;
                    this.documentEditor.resetData();

                    this.beginDiffHistory();

                    this.loadHistoryToolbar();
                    if (!layout.mobile) this.loadHistoryList();
                    if (callback) callback();
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

        this.historyListTitleNode = new Element("div", {"styles": this.css.historyListTitleNode}).inject(this.historyListTitleAreaNode);
        this.historyListTitleInsertNode = new Element("div", {"styles": this.css.historyListTitleInsertNode}).inject(this.historyListTitleAreaNode);
        this.historyListTitleDeleteNode = new Element("div", {"styles": this.css.historyListTitleDeleteNode}).inject(this.historyListTitleAreaNode);
    },
    loadHistoryList: function(){
        var text = MWF.xApplication.process.Xform.LP.documentHistory.diff_patch_count;
        text = text.replace(/{history}/, this.historyDataList.length).replace(/{diff}/, this.diffCount);

        var insertStr = MWF.xApplication.process.Xform.LP.documentHistory.insertTimes;
        var deleteStr = MWF.xApplication.process.Xform.LP.documentHistory.deleteTimes;
        insertStr = insertStr.replace(/{times}/, this.diffInsertCount);
        deleteStr = deleteStr.replace(/{times}/, this.diffDeleteCount);

        this.historyListTitleNode.set("text", text);
        this.historyListTitleInsertNode.set("text", insertStr);
        this.historyListTitleDeleteNode.set("text", deleteStr);

        //var original = this.historyDataList[0];
        //this.createHistoryListItem(original);
        this.historyDataList.each(function(historyData){
            this.createHistoryListItem(historyData);
        }.bind(this));

        // this.diffPatch.each(function(patchObj){
        //     this.createHistoryListItem(patchObj);
        // }.bind(this));
    },
    createHistoryListItem: function(historyData){
        new MWF.xApplication.process.Xform.widget.DocumentHistory.Item(this, historyData);
    },


    getHistoryDataList: function(callback){
        // if (this.historyDataList && this.historyDataList.length){
        //     this.getHistoryDataListFinish(this.historyDataList);
        //     if (callback) callback();
        // }else{
            var historyDataList = [];
            var getDataCount = 0;
            var idx = 0;

            var checkBeginDiffHistory = function(){
                if (getDataCount>=this.historyDocumentList.length){
                    this.getHistoryDataListFinish(historyDataList, callback);
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
        //}
    },
    getHistoryDataListFinish: function(historyDataList, callback){
        this.historyDataList = historyDataList;
        this.originaHistoryData = historyDataList[0].json.data || null;

        if (this.documentEditor.allowEdit){
            o2.load("/o2_lib/diff-match-patch/diff_match_patch_uncompressed.js", function(){
                var originaData = this.documentEditor.form.businessData.originalData[this.documentEditor.json.id];
                var data = this.documentEditor.data.filetext;
                var earlyData = originaData.filetext;
                if (data!=earlyData){
                    var dmp = new diff_match_patch();
                    var diff_d = dmp.diff_main(earlyData, data);
                    dmp.diff_cleanupSemantic(diff_d);
                    var patch_list = dmp.patch_make(earlyData, data, diff_d);

                    if (patch_list.length){
                        var patch = dmp.patch_toText(patch_list);
                        var patchData = JSON.stringify({"patchs": patch});
                        var currentData = {
                            "data": patchData,
                            "json": {"patchs": patch},
                            "person": layout.session.user.distinguishedName,
                            "activityName": this.documentEditor.form.businessData.activity.name,
                            "createTime" : (new Date()).format("db")
                        };
                        this.historyDataList.push(currentData);
                    }
                }
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    getHistroyDocumentData: function(id, callback, i, historyDataList){
        o2.Actions.load("x_processplatform_assemble_surface").DocumentVersionAction.get(id, function(json){
            var obj = JSON.parse(json.data.data);
            json.data.json = obj;
            if (historyDataList) historyDataList[i] = json.data;
            if (callback) callback(json.data);
        }.bind(this));
    },
    getHistroyDocumentList: function(callback){
        //if (!this.historyDocumentList){
            var id = this.documentEditor.form.businessData.work.job;
            o2.Actions.load("x_processplatform_assemble_surface").DocumentVersionAction.listWithJobCategory(id, this.documentEditor.json.id, function(json){
                this.historyDocumentList = json.data;
                if (callback) callback();
            }.bind(this));
        //}else{
        //   if (callback) callback();
        //}
    },

    beginDiffHistory: function(){
        //o2.load("/o2_lib/diff-match-patch/diff_match_patch_uncompressed.js", function(){
            this.initAnimation();
            //if (callback) callback();
        //}.bind(this));
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
        this.currentHistoryData = this.originaHistoryData;
        this.documentEditor.layout_filetext.set("html", this.currentHistoryData);
        this.patchIndex = 0;
        this.diffIndex = 0;
        if (this.originaDiff) this.originaDiff.showCurrent();
    },
    initAnimationStatus: function(){
        this.patchIndex = 0;
        this.diffIndex = 0;
        this.currentDiffs = null;
        this.stop = true;
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
            this.stop = true;
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
        if (!this.playing){
            this.initData();
            this.initAnimationStatus();
        }
        this.reverse = false;
        this.stop = false;
        this.stopWhile = "";
        this.options.fxTime = 500;
        this.options.inforTime = 2000;

        this.toolbar.childrenButton[0].disable();
        this.toolbar.childrenButton[3].disable();
        this.toolbar.childrenButton[4].disable();
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
    to: function(diff){
        if (this.nextPlayPrefixFunction){
            this.playing = false;
            this.nextPlayPrefixFunction(function(){
                this.initData();
                this.initAnimationStatus();
                this.reverse = false;
                this.options.fxTime = 0;
                this.options.inforTime = 0;
                this.stop = false;
                this.stopWhile = diff.id;
                this.toolbar.childrenButton[3].disable();
                this.toolbar.childrenButton[4].disable();

                this.doAnimationAuto(diff.id);

            }.bind(this));
            //this.nextPlayPrefixFunction = null;
        }else{
            this.initData();
            this.initAnimationStatus();
            this.reverse = false;
            this.options.fxTime = 0;
            this.options.inforTime = 0;
            this.stop = false;
            this.stopWhile = diff.id;
            this.toolbar.childrenButton[3].disable();
            this.toolbar.childrenButton[4].disable();

            this.doAnimationAuto(diff.id);
        }
    },
    origina: function(){
        if (this.nextPlayPrefixFunction){
            this.playing = false;
            this.nextPlayPrefixFunction(function(){
                this.initData();
                this.initAnimationStatus();
            }.bind(this));
            this.nextPlayPrefixFunction = null;
        }else{
            this.initData();
            this.initAnimationStatus();
        }
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

        if (this.historyListAreaNode) this.historyListAreaNode.destroy();
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

                    this.beginDiffHistory();

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

                    if (!layout.mobile || this.is_iPad()) {
                        this.createHistoryListNode();
                        this.loadHistoryList();
                    }

                    this.documentEditor.options.pageShow = "single";
                    this.documentEditor.resetData();

                    if (callback) callback();

                }.bind(this));
            }
        }.bind(this));
    },

    diffHistroy: function(){
        var diffPatch =  [];
        //var historyPatchs = [];
        for (var i=1; i<this.historyDataList.length; i++){
            // var earlyDataText = this.historyDataList[i-1].data;
            // var laterData = this.historyDataList[i];
            //
            // var dmp = new diff_match_patch();
            // // dmp.Diff_Timeout = parseFloat(10);
            // // dmp.Diff_EditCost = parseFloat(4);
            // var diff_d = dmp.diff_main(earlyDataText, laterData.data);
            // dmp.diff_cleanupSemantic(diff_d);
            // var patch_list = dmp.patch_make(earlyDataText, laterData.data, diff_d);

            //historyPatchs.push({"patch_list": patch_list, "obj": laterData});
            var history = this.historyDataList[i];
            var data = history.json;
            history.json = data;
            if (data.patchs){
                var dmp = new diff_match_patch();
                var patch_list = dmp.patch_fromText(data.patchs);
                history.json.patchObj = patch_list;

                patch_list.each(function(patch){
                    diffPatch.push({"patch":patch, "obj": history});
                }.bind(this));
            }
        }
        return diffPatch;
    },

    doPatchAnimation: function(callback){
        var patchObj = this.diffPatch[this.patchIndex];
        var patch = patchObj.patch;
        var obj = patchObj.obj;
        this.currentDiffs = patch.diffs;
        this.diffIndex = (this.reverse) ? patch.diffs.length-1 : 0;

        var start = (this.reverse) ? patch.start1+patch.length2 : patch.start1;
        this.doDiffsAnimation(obj, start, function(){
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
        if (this.historyInforDiv){
            this.historyInforDiv.dispose();
            this.historyInforDiv = null;
        }
        var styles =  (!layout.mobile) ? this.css.historyInforNode : this.css.historyInforMobileNode
        var insertInforDiv = new Element("div", { "styles": styles }).inject(this.documentEditor.node);
        insertInforDiv.setStyle("background", color);
        insertInfor = insertInfor.replace(/{name}/, o2.name.cn(obj.person))
            .replace(/{activity}/, obj.activityName)
            .replace(/{time}/, obj.createTime);
        insertInforDiv.set("html", insertInfor);
        if (!layout.mobile){
            insertInforDiv.position({
                "relativeTo": node,
                "position": 'upperCenter',
                "edge": 'bottomCenter',
                "offset": {
                    "x": 0, "y": -10
                }
            });
        }else{

        }

        // debugger;
        // var p = node.getPosition(node.getOffsetParent());
        // if (p.x<0) p.x=0;
        // var y = (p.y-10-insertInforDiv.getSize().y);
        // var x = p.x;
        // alert(x)
        // var scale = (this.documentEditor.scale<0.7) ? 0.7 : this.documentEditor.scale;
        // insertInforDiv.setStyles({
        //     "left": ""+x+"px",
        //     "top": ""+y+"px",
        //     "transform":"scale("+scale+")",
        //     "transform-origin": "0px 0px",
        // });
        this.historyInforDiv = insertInforDiv;
        return insertInforDiv;
    },
    doDiffsAnimation: function(obj, start, callback){
        var diff = this.currentDiffs[this.diffIndex];
        var filetextNode = this.documentEditor.layout_filetext;
        switch (diff[0]) {
            case DIFF_INSERT:
                if (diff["item"]) diff["item"].showCurrent((!this.stopWhile || this.stopWhile == diff["id"]));
                if (this.originaDiff) this.originaDiff.hideCurrent();
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
                if (!this.stopWhile || this.stopWhile == diff["id"]) ins.scrollIn();

                this.doInsetAnimation(ins, diff[1], function(invisible){
                    var insertInforDiv = null;
                    if (!invisible && (!this.stopWhile || this.stopWhile == diff["id"]) ){
                        insertInforDiv = this.createDiifInforNode(obj, ins, "#e2edfb", MWF.xApplication.process.Xform.LP.documentHistory.insertContent);
                    }
                    window.setTimeout(function(){
                        var endFunction = function(cb){
                            if (insertInforDiv) insertInforDiv.fade("out");
                            var fx = new Fx.Tween(ins, {property: 'opacity', duration:this.options.speed*this.options.fxTime});
                            fx.start(1.1).chain(function(){
                                if (insertInforDiv) insertInforDiv.destroy();
                                if (diff["item"]) diff["item"].hideCurrent();
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
                                            window.setTimeout(function(){this.doDiffsAnimation(obj, start, function(){
                                                if (callback) callback();
                                                if (cb) cb();
                                            });}.bind(this), this.options.speed*this.options.fxTime);
                                            //this.doDiffsAnimation(obj, start, callback);
                                        }else{
                                            if (callback) callback();
                                        }
                                    }else{
                                        start += text.length;
                                        this.diffIndex++;
                                        if (this.diffIndex<this.currentDiffs.length){
                                            window.setTimeout(function(){this.doDiffsAnimation(obj, start, function(){
                                                if (callback) callback();
                                                if (cb) cb();
                                            });}.bind(this), this.options.speed*this.options.fxTime);
                                            //this.doDiffsAnimation(obj, start, callback);
                                        }else{
                                            if (callback) callback();
                                        }
                                    }
                                }else{
                                    this.initAnimationStatus();
                                    this.documentEditor.resetData();
                                    if (cb) cb();
                                }


                            }.bind(this));
                            if (this.nextPlayPrefixFunction) this.nextPlayPrefixFunction = null;
                        }.bind(this)

                        if (this.stopWhile) if (this.stopWhile == diff["id"]) this.stop = true;

                        if (!this.stop || !this.playing) {
                            endFunction();
                        } else{
                            if (this.stopWhile){
                                this.stopWhile = "";
                                //this.playing = false;
                            }
                            this.nextPlayPrefixFunction = endFunction;

                        }
                        this.checkToolbar();
                    }.bind(this), (invisible ? 100: this.options.speed*this.options.inforTime));
                }.bind(this));
                break;
            case DIFF_DELETE:
                if (diff["item"]) diff["item"].showCurrent((!this.stopWhile || this.stopWhile == diff["id"]));
                if (this.originaDiff) this.originaDiff.hideCurrent();
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
                if (!this.stopWhile || this.stopWhile == diff["id"]) del.scrollIn();

                this.doDeleteAnimation(del, diff, obj, function(deleteInforDiv){
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
                                if (diff["item"]) diff["item"].hideCurrent();
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
                                            window.setTimeout(function(){this.doDiffsAnimation(obj, start, function(){
                                                if (callback) callback();
                                                if (cb) cb();
                                            });}.bind(this), this.options.speed*this.options.fxTime);
                                        }else{
                                            if (callback) callback();
                                        }
                                    }else{
                                        this.diffIndex++;
                                        if (this.diffIndex<this.currentDiffs.length){
                                            window.setTimeout(function(){this.doDiffsAnimation(obj, start, function(){
                                                if (callback) callback();
                                                if (cb) cb();
                                            });}.bind(this), this.options.speed*this.options.fxTime);
                                        }else{
                                            if (callback) callback();
                                        }
                                    }

                                }else{
                                    this.initAnimationStatus();
                                    this.documentEditor.resetData();
                                    if (cb) cb();
                                }

                            }.bind(this));
                            if (this.nextPlayPrefixFunction) this.nextPlayPrefixFunction = null;
                        }.bind(this)

                        if (this.stopWhile){
                            if (this.stopWhile == diff["id"]) this.stop = true;
                        }

                        if (!this.stop || !this.playing) {
                            endFunction();
                        } else{
                            if (this.stopWhile){
                                this.stopWhile = "";
                                //this.playing = false;
                            }
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
            var duration = this.options.speed*this.options.fxTime/nodes.length;
            if (!duration){
                ins.appendChild(node);
                idx++;
                if (idx<nodes.length){
                    this.doInsetNodeAnimation(ins, nodes, idx, callback);
                }else{
                    if (callback) callback();
                }
            }else{
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
        }
    },

    doCharAnimation: function(node, str, idx, callback){
        var duration = this.options.speed*this.options.fxTime/str.length;
        if (!duration){
            node.set("html", str);
            idx = str.length;
            if (callback) callback();
        }else{
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
        }
    },

    doDeleteAnimation: function(node, diff, obj, callback){
        var str = diff[1];
        var tmp = new Element("div", {"html": str});
        if (!tmp.get("text").trim()){
            if (callback) callback(null);
        }else{
            var deleteInforDiv = (!this.stopWhile || this.stopWhile == diff["id"]) ? this.createDiifInforNode(obj, node, "#fbe0e7", MWF.xApplication.process.Xform.LP.documentHistory.deleteContent) : null;
            var fx = new Fx.Tween(node, {property: 'opacity', duration:this.options.speed*this.options.fxTime});
            fx.start(1,0.5).chain(function(){
                if (callback) callback(deleteInforDiv);
            }.bind(this));
        }
    }
});

MWF.xApplication.process.Xform.widget.DocumentHistory.Item = new Class({
    initialize: function(history, historyData){
        this.history = history;
        this.documentEditor = this.history.documentEditor;
        this.css = this.history.css;
        this.historyData = historyData;
        this.load();
    },
    load: function(){
        var patchs = this.historyData.json.patchObj || null;
        var obj = this.historyData;

        this.node = new Element("div", {"styles": this.css.historyListItemNode}).inject(this.history.historyListContentAreaNode);
        var patchHtml = "<div style='font-weight: bold; height: 30px; line-height: 30px'>"+o2.name.cn(obj.person)+" ["+obj.activityName+"]</div><div style='height: 20px; line-height: 20px; color:#666666'>"+obj.createTime+"</div>"
        this.patchNode = new Element("div", {"styles": this.css.historyListItemPatchNode, "html": patchHtml}).inject(this.node);
        this.diffsNode = new Element("div", {"styles": this.css.historyListItemDiffsNode}).inject(this.node);

        var _self = this;
        if (patchs){
            patchs.each(function(patch){
                patch.diffs.each(function(diff){
                    if (diff[0]!=0){
                        diff["id"] = (new o2.widget.UUID()).toString();
                        var tmp = new Element("div", {"html": diff[1]});
                        infor = tmp.get("text");
                        var infor = ((infor.length>50) ? infor.substring(0, 50)+"..." : infor);

                        tmp.destroy();
                        if (diff[0]==-1){
                            infor = MWF.xApplication.process.Xform.LP.documentHistory.delete +": "+"<span style='color:red'><del>"+infor+"</del></span>"
                        }else{
                            infor = MWF.xApplication.process.Xform.LP.documentHistory.insert +": "+"<span style='color:blue'><ins>"+infor+"</ins></span>"
                        }
                        diffNode = new Element("div", {"styles": this.css.historyListItemDiffNode, "html": infor}).inject(this.diffsNode);
                        diffNode.store("diff", diff);
                        diff["item"] = {
                            "node": diffNode,
                            "showCurrent": function(show){
                                var thisDiff = this.node.retrieve("diff");
                                var color = (thisDiff[0]==-1) ? "#fbe0e7": "#e2edfb";
                                this.node.setStyles({"background-color": color});

                                var ss = _self.history.historyListContentAreaNode.getScrollSize();
                                var s = _self.history.historyListContentAreaNode.getSize();
                                if (ss.y>s.y) if (show) this.node.scrollIn();
                            },
                            "hideCurrent": function(){
                                this.node.setStyles(_self.css.historyListItemDiffNode);
                            }
                        };

                        diffNode.addEvents({
                            // "mouseover": function(){
                            //     if (_self.history.stop){
                            //         var diff = this.retrieve("diff");
                            //         var color = (diff[0]==-1) ? "red": "blue";
                            //         this.setStyles({"border-color": color});
                            //     }
                            // },
                            // "mouseout": function(){ if (_self.history.stop) this.setStyles(_self.css.historyListItemDiffNode_out) },
                            "click": function(){
                                if (_self.history.stop){
                                    var diff = this.retrieve("diff");
                                    _self.history.to(diff);
                                }
                            }
                        });

                    }
                }.bind(this));
            }.bind(this));
        }else{
            infor = MWF.xApplication.process.Xform.LP.documentHistory.original;
            diffNode = new Element("div", {"styles": this.css.historyListItemDiffNode, "html": infor}).inject(this.diffsNode);
            this.history.originaDiff = {
                "node": diffNode,
                "showCurrent": function(){
                    this.node.setStyles({"background-color": "#e2edfb"});
                    //if (show) this.node.scrollIn();
                },
                "hideCurrent": function(){
                    this.node.setStyles(_self.css.historyListItemDiffNode);
                }
            };
            diffNode.addEvents({
                "click": function(){
                    if (_self.history.stop){
                        _self.history.origina();
                    }
                }
            });
        }
    }
})