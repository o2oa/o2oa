MWF.xApplication.process.Xform.widget = MWF.xApplication.process.Xform.widget || {};
MWF.xApplication.process.Xform.widget.HandwrittenApproval = new Class({
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

                    this.documentEditor.resetData();

                    this.beginDiffHistory();

                    this.loadHistoryToolbar();
                    if (!layout.mobile) this.loadHistoryList();
                    if (callback) callback();
                }.bind(this));
            }else{
                this.documentEditor.form.app.notice(MWF.xApplication.process.Xform.LP.handwrittenApproval.nodiff, "info", this.documentEditor.node);
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
        debugger;
        var html = "";
        if (this.historyDataList && this.historyDataList.length && this.historyDataList[0].json.v!="6"){
            html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"../x_component_process_Xform/$Form/default/documenteditoricon/play.png\" title=\""+MWF.xApplication.process.Xform.LP.handwrittenApproval.play+"\" MWFButtonAction=\"play\"></span>";
            html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"../x_component_process_Xform/$Form/default/documenteditoricon/pause.png\" title=\""+MWF.xApplication.process.Xform.LP.handwrittenApproval.pause+"\" MWFButtonAction=\"pause\"></span>";
            html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"../x_component_process_Xform/$Form/default/documenteditoricon/stop.png\" title=\""+MWF.xApplication.process.Xform.LP.handwrittenApproval.stop+"\" MWFButtonAction=\"stopPlay\"></span>";
            html += "<span MWFnodetype=\"MWFToolBarSeparator\"></span>";
            html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"../x_component_process_Xform/$Form/default/documenteditoricon/prev.png\" title=\""+MWF.xApplication.process.Xform.LP.handwrittenApproval.prev+"\" MWFButtonAction=\"prev\"></span>";
            html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"../x_component_process_Xform/$Form/default/documenteditoricon/next.png\" title=\""+MWF.xApplication.process.Xform.LP.handwrittenApproval.next+"\" MWFButtonAction=\"next\"></span>";
            html += "<span MWFnodetype=\"MWFToolBarSeparator\"></span>";
        }

        html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"../x_component_process_Xform/$Form/default/documenteditoricon/exit.png\" title=\""+MWF.xApplication.process.Xform.LP.handwrittenApproval.exit+"\" MWFButtonAction=\"exit\" MWFButtonText=\""+MWF.xApplication.process.Xform.LP.handwrittenApproval.exit+"\"></span>";
        html += "<span MWFnodetype=\"MWFToolBarSeparator\"></span>";

        var text = MWF.xApplication.process.Xform.LP.handwrittenApproval.diff_patch_count;
        text = text.replace(/{history}/, this.historyDataList.length).replace(/{diff}/, this.diffCount);

        html += "<span style='float: left; line-height: 24px; color: #666666; margin-left: 10px'>"+text+"</span>";

        this.toolbarNode.set("html", html);

        MWF.require("MWF.widget.Toolbar", function() {
            this.toolbar = new MWF.widget.Toolbar(this.toolbarNode, {"style": "documentEdit"}, this);
            this.toolbar.load();
            this.checkToolbar();
        }.bind(this));

        this.toolbarNode.setStyle("overflow", "visible");
        if (this.historyListAreaNode) this.historyListAreaNode.inject(this.toolbarNode);
    },
    checkToolbar: function(){
        if (this.historyDataList && this.historyDataList.length && this.historyDataList[0].json.v!="6"){
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
        }else{
            // 不做动画效果，不需要处理工具条
        }
    }
});
