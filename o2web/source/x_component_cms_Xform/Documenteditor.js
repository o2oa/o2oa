MWF.xDesktop.requireApp("process.Xform", "Documenteditor", null, false);
MWF.xApplication.cms.Xform.Documenteditor = MWF.CMSDocumenteditor =  new Class({
    Extends: MWF.APPDocumenteditor,
    loadDocumentEditor: function(callback){
        this._loadToolbars();
        this._loadFiletextPage(function(){

            this._singlePage();

            this.form.addEvent("beforeSave", function(){
                this.getData();
                this.checkSaveNewEdition();
            }.bind(this));

            if (this.json.toWord=="y"){
                if (this.json.toWordTrigger=="open") this.docToWord();
                if (this.json.toWordTrigger=="save") {
                    if (!this.form.toWordSaveList) this.form.toWordSaveList = [];
                    this.form.toWordSaveList.push(this);
                }

                if (this.json.toWordTrigger=="submit") {
                    if (!this.form.toWordSubmitList) this.form.toWordSubmitList = [];
                    this.form.toWordSubmitList.push(this);
                }
            }
            //if (!layout.mobile) this.loadSideToolbar();

            o2.load("../o2_lib/diff-match-patch/diff_match_patch.js");

            // if (this.form.businessData.data["$work"]){
            // 	var id = this.form.businessData.data["$work"].job;
            // 	o2.Actions.load("x_processplatform_assemble_surface").DocumentVersionAction.listWithJobCategory(id, this.json.id, function(json){
            // 		this.historyDocumentList = json.data;
            // 		if (this.historyDocumentList.length){
            // 			o2.Actions.load("x_processplatform_assemble_surface").DocumentVersionAction.get(this.historyDocumentList[this.historyDocumentList.length-1].id, function(json){
            // 				var data = JSON.parse(json.data.data);
            // 				this.originaHistoryData = data.data;
            // 			}.bind(this));
            // 		}
            // 	}.bind(this));
            // }


            if (callback) callback();
        }.bind(this));

        if (!this.form.documenteditorList) this.form.documenteditorList=[];
        this.form.documenteditorList.push(this);
    },
    _isAllowHistory: function(){
        return false;
        // if (this.json.allowHistory=="n") return false;
        // if (this.json.allowHistory=="s"){
        //     if (this.json.allowHistoryScript && this.json.allowHistoryScript.code){
        //         return !!this.form.Macro.exec(this.json.allowHistoryScript.code, this);
        //     }
        // }
        // return true;
    },
});