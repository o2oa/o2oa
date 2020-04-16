MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Documenteditor = MWF.APPDocumenteditor =  new Class({
    Extends: MWF.APP$Module,
    options: {
        "moduleEvents": ["load", "queryLoad", "beforeLoad", "postLoad", "afterLoad", "loadPage"],
        "docPageHeight": 850.4,
        "docPageFullWidth": 794,
        "pageShow": "single"
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
    },
    _loadCss: function(reload){
        var key = encodeURIComponent(this.cssPath);
        if (!reload && o2.widget.css[key]){
            this.css = o2.widget.css[key];
        }else{
            this.cssPath = (this.cssPath.indexOf("?")!=-1) ? this.cssPath+"&v="+o2.version.v : this.cssPath+"?v="+o2.version.v;
            var r = new Request.JSON({
                url: this.cssPath,
                secure: false,
                async: false,
                method: "get",
                noCache: false,
                onSuccess: function(responseJSON, responseText){
                    this.css = responseJSON;
                    o2.widget.css[key] = responseJSON;
                }.bind(this),
                onError: function(text, error){
                    console.log(error + text);
                }
            });
            r.send();
        }
    },
    load: function(){
        this._loadModuleEvents();
        if (this.fireEvent("queryLoad")){
            this.fireEvent("beforeLoad");
            this.cssPath = this.form.path+this.form.options.style+"/doc.wcss";
            this._loadCss();

            this._queryLoaded();
            this._loadUserInterface(function(){
                this.fireEvent("postLoad");
                this.fireEvent("afterLoad");
                this.fireEvent("load");

                this.form.app.addEvent("resize", function(){
                    // if (this.options.pageShow!=="double"){
                    //     this._doublePage();
                    // }else{
                    this._singlePage();
                    // }
                    this._checkScale();
                }.bind(this));
            }.bind(this));
            this._loadStyles();

            this._afterLoaded();

        }
    },

    _createNewPage: function(){
        var pageNode = new Element("div.doc_layout_page", {"styles": this.css.doc_page}).inject(this.contentNode);
        var pageContentNode = new Element("div.doc_layout_page_content", {"styles": this.css.doc_layout_page_content}).inject(pageNode);
        pageNode.set("data-pagecount", this.pages.length+1);
        this.pages.push(pageNode);
        return pageNode;
    },
    _getShow: function(name, typeItem, scriptItem){
        switch (this.json[typeItem]) {
            case "y":
                return true;
            case "n":
                return false;
            case "a":
                if (["copies", "secret", "priority", "attachment", "annotation", "copyto", "copyto2"].indexOf(name!=-1)){
                    return !!this.data[name] && (!!this.data[name].length);
                }
                return true;
            case "s":
                if (this.json[scriptItem] && this.json[scriptItem].code){
                    return !!this.form.Macro.exec(this.json[scriptItem].code, this);
                }
                return true;
            default:
                return true;
        }
    },
    _createPage: function(callback){
        var pageContentNode = this._createNewPage().getFirst();

        var control = this.getShowControl();
        this.json.fileup =  !!(control.signer);

        //this.json.documentTempleteType!="cus"
        this.getTempleteJson(function(){
            var templete = this.json.documentTempleteName || "standard";
            pageContentNode.loadHtml("/x_component_process_FormDesigner/Module/Documenteditor/templete/"+this.templeteJson[templete].file, function(){
                if (this.attachmentTemplete){
                    var attNode = pageContentNode.getElement(".doc_layout_attachment_content");
                    if (attNode) attNode.empty();
                }
                this.fireEvent("loadPage");
                if (callback) callback(control);
            }.bind(this));
        }.bind(this));
    },
    getTempleteJson: function(callback){
        if (this.templeteJson){
            if (callback) callback();
        }else{
            o2.getJSON("/x_component_process_FormDesigner/Module/Documenteditor/templete/templete.json", function(json){
                this.templeteJson = json;
                if (callback) callback();
            }.bind(this));
        }
    },
    getShowControl: function(){
        var control = {};
        control.copiesSecretPriority = this._getShow("copiesSecretPriority", "copiesSecretPriorityShow", "copiesSecretPriorityShowScript");

        control.copies = this._getShow("copies", "copiesShow", "copiesShowScript");
        control.secret = this._getShow("secret", "secretShow", "secretShowScript");
        control.priority = this._getShow("priority", "priorityShow", "priorityShowScript");

        control.redHeader = this._getShow("redHeader", "redHeaderShow", "redHeaderShowScript");
        control.redLine = this._getShow("redLine", "redLineShow", "redLineShowScript");

        control.signer = this._getShow("signer", "signerShow", "signerShowScript");
        control.fileno = this._getShow("fileno", "filenoShow", "filenoShowScript");
        control.subject = this._getShow("subject", "subjectShow", "subjectShowScript");
        control.mainSend = this._getShow("mainSend", "mainSendShow", "mainSendShowScript");
        control.attachment = this._getShow("attachment", "attachmentShow", "attachmentShowScript");
        control.issuanceUnit = this._getShow("issuanceUnit", "issuanceUnitShow", "issuanceUnitShowScript");
        control.issuanceDate = this._getShow("issuanceDate", "issuanceDateShow", "issuanceDateShowScript");
        control.annotation = this._getShow("annotation", "annotationShow", "annotationShowScript");
        control.copyto = this._getShow("copyto", "copytoShow", "copytoShowScript");
        control.copyto2 = this._getShow("copyto2", "copyto2Show", "copyto2ShowScript");
        control.editionUnit = this._getShow("editionUnit", "editionUnitShow", "editionUnitShowScript");
        control.editionDate = this._getShow("editionDate", "editionDateShow", "editionDateShowScript");

        control.meetingAttend = this._getShow("meetingAttend", "meetingAttendShow", "meetingAttendShowScript");
        control.meetingLeave = this._getShow("meetingLeave", "meetingLeaveShow", "meetingLeaveShowScript");
        control.meetingSit = this._getShow("meetingSit", "meetingSitShow", "meetingSitShowScript");
        return control;
    },
    // _getEdit: function(name, typeItem, scriptItem){
    //     switch (this.json[typeItem]) {
    //         case "y":
    //             return true;
    //         case "n":
    //             return false;
    //         // case "a":
    //         //     if (["copies", "secret", "priority", "attachment", "annotation", "copyto"].indexOf(name!=-1)){
    //         //         return !!this.data[name] && (!!this.data[name].length);
    //         //     }
    //         //     return true;
    //         case "s":
    //             if (this.json[scriptItem] && this.json[scriptItem].code){
    //                 return !!this.form.Macro.exec(this.json[scriptItem].code, this);
    //             }
    //             return true;
    //     }
    // },
    getEditControl: function(){
        var control = {};
        control.copies = this._getEdit("copies", "copiesEdit", "copiesEditScript");
        control.secret = this._getEdit("secret", "secretEdit", "secretEditScript");
        control.priority = this._getEdit("priority", "priorityEdit", "priorityEditScript");
        control.redHeader = this._getEdit("redHeader", "redHeaderEdit", "redHeaderEditScript");
        control.signer = this._getEdit("signer", "signerEdit", "signerEditScript");
        control.fileno = this._getEdit("fileno", "filenoEdit", "filenoEditScript");
        control.subject = this._getEdit("subject", "subjectEdit", "subjectEditScript");
        control.mainSend = this._getEdit("mainSend", "mainSendEdit", "mainSendEditScript");
        control.attachment = this._getEdit("attachment", "attachmentEdit", "attachmentEditScript");
        control.issuanceUnit = this._getEdit("issuanceUnit", "issuanceUnitEdit", "issuanceUnitEditScript");
        control.issuanceDate = this._getEdit("issuanceDate", "issuanceDateEdit", "issuanceDateEditScript");
        control.annotation = this._getEdit("annotation", "annotationEdit", "annotationEditScript");
        control.copyto = this._getEdit("copyto", "copytoEdit", "copytoEditScript");
        control.copyto2 = this._getEdit("copyto2", "copyto2Edit", "copyto2EditScript");
        control.editionUnit = this._getEdit("editionUnit", "editionUnitEdit", "editionUnitEditScript");
        control.editionDate = this._getEdit("editionDate", "editionDateEdit", "editionDateEditScript");
        control.meetingAttend = this._getShow("meetingAttend", "meetingAttendEdit", "meetingAttendEditScript");
        control.meetingLeave = this._getShow("meetingLeave", "meetingLeaveEdit", "meetingLeaveEditScript");
        control.meetingSit = this._getShow("meetingSit", "meetingSitEdit", "meetingSitEditScript");
        return control;
    },
    //份数 密级 紧急程度
    _loadCopiesSecretPriority: function(){
        this.layout_copiesSecretPriority = this.contentNode.getElement(".doc_layout_copiesSecretPriority");
        if (this.layout_copiesSecretPriority) this.layout_copiesSecretPriority.setStyles(this.css.doc_layout_copiesSecretPriority);

        this.layout_copies = this.contentNode.getElement(".doc_layout_copies");
        if (this.layout_copies) this.layout_copies.setStyles(this.css.doc_layout_copies);

        this.layout_secret = this.contentNode.getElement(".doc_layout_secret");
        if (this.layout_secret) this.layout_secret.setStyles(this.css.doc_layout_secret);

        this.layout_priority = this.contentNode.getElement(".doc_layout_priority");
        if (this.layout_priority) this.layout_priority.setStyles(this.css.doc_layout_priority);

        this.layout_copiesSecretPriority_blank = this.contentNode.getElement(".doc_layout_copiesSecretPriority_blank");

    },

    //红头
    _loadRedHeader: function(){
        this.layout_redHeader = this.contentNode.getElement(".doc_layout_redHeader");
        if (this.layout_redHeader) this.layout_redHeader.setStyles(this.css.doc_layout_redHeader);
    },
    //文号签发人（上行文）
    _loadFileNoUp: function(){
        this.layout_filenoArea = this.contentNode.getElement(".doc_layout_fileno_area");
        this.layout_fileNoUpTable = this.contentNode.getElement(".doc_layout_filenoup");
        if (this.layout_fileNoUpTable) this.layout_fileNoUpTable.setStyles(this.css.doc_layout_filenoup);

        var td = this.contentNode.getElement(".doc_layout_filenoup_fileno_td");
        if (td) td.setStyles(this.css.doc_layout_filenoup_fileno_td);

        this.layout_fileno = this.contentNode.getElement(".doc_layout_filenoup_fileno");
        if (this.layout_fileno) this.layout_fileno.setStyles(this.css.doc_layout_filenoup_fileno);

        td = this.contentNode.getElement(".doc_layout_filenoup_signer_td");
        if (td) td.setStyles(this.css.doc_layout_filenoup_signer_td);

        var node = this.contentNode.getElement(".doc_layout_filenoup_signer_table");
        if (node) node.setStyles(this.css.doc_layout_filenoup_signer_table);
        node = this.contentNode.getElement(".doc_layout_filenoup_signerTitle_td");
        if (node) node.setStyles(this.css.doc_layout_filenoup_signerTitle_td);
        this.layout_signerTitle = this.contentNode.getElement(".doc_layout_filenoup_signer");
        if (this.layout_signerTitle) this.layout_signerTitle.setStyles(this.css.doc_layout_filenoup_signer);
        node = this.contentNode.getElement(".doc_layout_filenoup_signerContent_td");
        if (node) node.setStyles(this.css.doc_layout_filenoup_signerContent_td);

        this.layout_signer = this.contentNode.getElement(".doc_layout_filenoup_signerContent");
        if (this.layout_signer) this.layout_signer.setStyles(this.css.doc_layout_filenoup_signerContent);
    },

    //文号
    _loadFileNo: function(){
        this.layout_fileNoUpTable = this.contentNode.getElement(".doc_layout_filenoup");
        this.layout_filenoArea = this.contentNode.getElement(".doc_layout_fileno_area");
        this.layout_fileno = this.contentNode.getElement(".doc_layout_fileno");
        if (this.layout_fileno) this.layout_fileno.setStyles(this.css.doc_layout_fileno);
    },

    //红线
    _loadRedLine: function(){
        this.layout_redLine = this.contentNode.getElement(".doc_layout_redline");
        if (this.layout_redLine) this.layout_redLine.setStyles(this.css.doc_layout_redline);
    },

    //标题
    _loadSubject:function(){
        this.layout_subject = this.contentNode.getElement(".doc_layout_subject");
        if (this.layout_subject) this.layout_subject.setStyles(this.css.doc_layout_subject);
    },

    //主送
    _loadMainSend: function(){
        this.layout_mainSend = this.contentNode.getElement(".doc_layout_mainSend");
        if (this.layout_mainSend) this.layout_mainSend.setStyles(this.css.doc_layout_mainSend);
    },

    //正文
    // _createFiletext: function(filetextNode, node, where){
    //     if (!filetextNode){
    //         var filetextNode = new Element("div.doc_layout_filetext").inject(node, where);
    //         filetextNode.addClass("doc_block");
    //         filetextNode.setAttribute('contenteditable', true);
    //     }
    //     CKEDITOR.disableAutoInline = true;
    //     var filetextEditor = CKEDITOR.inline(filetextNode, this._getEditorConfig());
    //     filetextNode.store("editor", filetextEditor);
    //     if (!this.filetextEditors) this.filetextEditors = [];
    //     this.filetextEditors.push(filetextEditor);
    //
    //     filetextEditor.on( 'blur', function(e) {
    //         // var filetextNode = e.editor.container.$;
    //         // var pageNode = filetextNode.getParent(".doc_layout_page");
    //         // this._checkSplitPage(pageNode);
    //         // this._repage();
    //     }.bind(this));
    //
    //     return filetextNode;
    // },
    _loadFiletext: function(){
        this.layout_filetext = this.contentNode.getElement(".doc_layout_filetext");
        this.layout_filetext.setStyles(this.css.doc_layout_filetext);

        //this.layout_filetext = this.contentNode.getElement(".doc_layout_filetext");
        // if (this.layout_filetexts.length){
        //     this.layout_filetexts.each(function(layout_filetext){
        //         layout_filetext.setStyles(this.css.doc_layout_filetext);
        //     }.bind(this));
        // }
    },

    //附件
    _loadAttachment: function(){
        this.layout_attachmentTable = this.contentNode.getElement(".doc_layout_attachment");
        if (this.layout_attachmentTable) this.layout_attachmentTable.setStyles(this.css.doc_layout_attachment);

        var node = this.contentNode.getElement(".doc_layout_attachment_title_td");
        if (node) node.setStyles(this.css.doc_layout_attachment_title_td);

        this.layout_attachmentTitle = this.contentNode.getElement(".doc_layout_attachment_title");
        if (node) node.setStyles(this.css.doc_layout_attachment_title);

        node = this.contentNode.getElement(".doc_layout_attachment_content_td");
        if (node) node.setStyles(this.css.doc_layout_attachment_content_td);

        this.layout_attachment = this.contentNode.getElement(".doc_layout_attachment_content");
        if (this.layout_attachment) this.layout_attachment.setStyles(this.css.doc_layout_attachment_content);
    },

    //发布单位
    _loadIssuance: function(){
        this.layout_issuanceTable = this.contentNode.getElement(".doc_layout_issuance");
        this.layout_issuanceUnit = this.contentNode.getElement(".doc_layout_issuanceUnit");
        this.layout_issuanceDate = this.contentNode.getElement(".doc_layout_issuanceDate");

        if (this.layout_issuanceTable) this.layout_issuanceTable.setStyles(this.css.doc_layout_issuance);
        if (this.layout_issuanceUnit) this.layout_issuanceUnit.setStyles(this.css.doc_layout_issuanceUnit);
        if (this.layout_issuanceDate) this.layout_issuanceDate.setStyles(this.css.doc_layout_issuanceDate);
    },

    //附注
    _loadAnnotation: function(){
        this.layout_annotation = this.contentNode.getElement(".doc_layout_annotation");
        if (this.layout_annotation) this.layout_annotation.setStyles(this.css.doc_layout_annotation);
    },

    //版记
    _loadEdition: function(){
        this.layout_editionArea = this.contentNode.getElement(".doc_layout_editionArea");
        this.layout_edition = this.contentNode.getElement(".doc_layout_edition");
        if (this.layout_edition) this.layout_edition.setStyles(this.css.doc_layout_edition);

        var node = this.contentNode.getElement(".doc_layout_edition_copyto");
        if (node) node.setStyles(this.css.doc_layout_edition_copyto);
        node = this.contentNode.getElement(".doc_layout_edition_copyto_table");
        if (node) node.setStyles(this.css.doc_layout_edition_copyto_table);

        var node = this.contentNode.getElement(".doc_layout_edition_copyto2");
        if (node) node.setStyles(this.css.doc_layout_edition_copyto);
        node = this.contentNode.getElement(".doc_layout_edition_copyto2_table");
        if (node) node.setStyles(this.css.doc_layout_edition_copyto_table);

        this.layout_copytoTitle = this.contentNode.getElement(".doc_layout_edition_copyto_title");
        if (this.layout_copytoTitle) this.layout_copytoTitle.setStyles(this.css.doc_layout_edition_copyto_title);
        this.layout_copytoContent = this.contentNode.getElement(".doc_layout_edition_copyto_content");
        if (this.layout_copytoContent) this.layout_copytoContent.setStyles(this.css.doc_layout_edition_copyto_content);

        this.layout_copyto2Title = this.contentNode.getElement(".doc_layout_edition_copyto2_title");
        if (this.layout_copyto2Title) this.layout_copyto2Title.setStyles(this.css.doc_layout_edition_copyto_title);
        this.layout_copyto2Content = this.contentNode.getElement(".doc_layout_edition_copyto2_content");
        if (this.layout_copyto2Content) this.layout_copyto2Content.setStyles(this.css.doc_layout_edition_copyto_content);

        var issuance = this.contentNode.getElement(".doc_layout_edition_issuance");
        if (issuance) issuance.setStyles(this.css.doc_layout_edition_issuance);
        var issuance_table = this.contentNode.getElement(".doc_layout_edition_issuance_table");
        if (issuance_table) issuance_table.setStyles(this.css.doc_layout_edition_issuance_table);
        this.layout_edition_issuance_unit = this.contentNode.getElement(".doc_layout_edition_issuance_unit");
        if (this.layout_edition_issuance_unit) this.layout_edition_issuance_unit.setStyles(this.css.doc_layout_edition_issuance_unit);
        this.layout_edition_issuance_date = this.contentNode.getElement(".doc_layout_edition_issuance_date");
        if (this.layout_edition_issuance_date) this.layout_edition_issuance_date.setStyles(this.css.doc_layout_edition_issuance_date);
    },

    _loadMeeting: function(){
        this.layout_meetingAttendArea = this.contentNode.getElement(".doc_layout_meeting_attend");
        this.layout_meetingAttendTitle = this.contentNode.getElement(".doc_layout_meeting_attend_title");
        this.layout_meetingAttendContent = this.contentNode.getElement(".doc_layout_meeting_attend_content");

        this.layout_meetingLeaveArea = this.contentNode.getElement(".doc_layout_meeting_leave");
        this.layout_meetingLeaveTitle = this.contentNode.getElement(".doc_layout_meeting_leave_title");
        this.layout_meetingLeaveContent = this.contentNode.getElement(".doc_layout_meeting_leave_content");

        this.layout_meetingSitArea = this.contentNode.getElement(".doc_layout_meeting_sit");
        this.layout_meetingSitTitle = this.contentNode.getElement(".doc_layout_meeting_sit_title");
        this.layout_meetingSitContent = this.contentNode.getElement(".doc_layout_meeting_sit_content");
    },
    _loadPageLayout: function(control){
        this._loadCopiesSecretPriority();
        this._loadRedHeader();

        if (this.json.fileup){
            this._loadFileNoUp();
        }else{
            this._loadFileNo();
        }
        if (!this.layout_fileno) this._loadFileNo();

        this._loadRedLine();
        this._loadSubject();

        this._loadMainSend();
        this._loadFiletext();
        this._loadAttachment();

        this._loadIssuance();

        this._loadAnnotation();

        this._loadEdition();

        //会议纪要
        this._loadMeeting();

        this.reSetShow(control);
        this.reSetEdit();

        // 份数:          this.layout_copies
        // 密级:          this.layout_secret
        // 紧急程度:       this.layout_priority
        // 红头:          this.layout_redHeader
        // 上行文编号签发：  this.layout_fileNoUpTable
        // 文号:           this.layout_fileno
        // 签发:           this.layout_signerTitle
        // 签发人:         this.layout_signer
        // 文号：          this.layout_fileno
        // 红线：          this.layout_redLine
        // 标题：          this.layout_subject
        // 主送单位：       this.layout_mainSend
        // 正文：          this.layout_filetexts
        // 附件：          this.layout_attachmentTitle
        // 附件：          this.layout_attachment
        // 单位：          this.layout_issuanceUnit
        // 签发时间：       this.layout_issuanceDate
        // 附注：          this.layout_annotation
        // 抄送：          this.layout_copytoTitle
        // 抄送：          this.layout_copytoContent
        // 版记单位         this.layout_edition_issuance_unit
        // 版记日期         this.layout_edition_issuance_date
    },

    reSetShow: function(control){
        if (!control) control = this.getShowControl();
        var m = function(s){ return (control[s]) ? "show" : "hide"; }

        if (this.layout_copiesSecretPriority) this.layout_copiesSecretPriority[m("copiesSecretPriority")]();
        // control.copies = this._getShow("copies", "copiesShow", "copiesShowScript");
        // control.secret = this._getShow("secret", "secretShow", "secretShowScript");
        // control.priority = this._getShow("priority", "priorityShow", "priorityShowScript");
        var n = 0;
        if (!control.copies) n++;
        if (!control.secret) n++;
        if (!control.priority) n++;
        if (this.layout_copiesSecretPriority_blank){
            while (n>0){
                this.layout_copiesSecretPriority_blank.empty();
                this.layout_copiesSecretPriority_blank.appendHTML("<span style='font-size:16.0pt'>&nbsp;</span>");
                n--;
            }
        }

        if (this.layout_copies) this.layout_copies[m("copies")]();
        if (this.layout_secret) this.layout_secret[m("secret")]();
        if (this.layout_priority) this.layout_priority[m("priority")]();

        if (this.layout_redHeader) this.layout_redHeader[m("redHeader")]();
        if (this.layout_redLine) this.layout_redLine[m("redLine")]();

        if (this.layout_fileNoUpTable) this.layout_fileNoUpTable[m("signer")]();
        if (this.layout_filenoArea) this.layout_filenoArea[(!control.signer) ? "show" : "hide"]();

        if (this.layout_fileno) this.layout_fileno[m("fileno")]();
        if (this.layout_subject) this.layout_subject[m("subject")]();
        if (this.layout_mainSend) this.layout_mainSend[m("mainSend")]();
        if (this.layout_attachmentTable) this.layout_attachmentTable[m("attachment")]();
        if (this.layout_issuanceUnit) this.layout_issuanceUnit[m("issuanceUnit")]();
        if (this.layout_issuanceDate) this.layout_issuanceDate[m("issuanceDate")]();
        if (this.layout_annotation) this.layout_annotation[m("annotation")]();

        if ((!control.copyto || !this.layout_copytoContent) && (!control.copyto2 || !this.layout_copyto2Content)  && (!control.editionUnit || !this.layout_edition_issuance_unit) && (!control.editionDate || !this.layout_edition_issuance_date)){
            if (this.layout_editionArea) this.layout_editionArea.hide();
        }else{
            if ((!control.copyto || !this.layout_copytoContent) && (!control.copyto2 || !this.layout_copyto2Content) ){
                if (this.layout_edition){
                    if (this.layout_copytoContent) this.layout_copytoContent.getParent("tr").destroy();
                    if (this.layout_copyto2Content) this.layout_copyto2Content.getParent("tr").destroy();
                    // if (this.layout_edition) this.layout_edition.getElement("tr").getElements("td")[0].setStyles({
                    //     "border-top": "solid windowtext 1.5pt",
                    //     "mso-border-top-alt": "solid windowtext 1pt"
                    // });
                }
            }else if (!control.copyto || !this.layout_copytoContent){
                if (this.layout_copytoContent) this.layout_copytoContent.getParent("tr").destroy();
                //if (this.layout_copyto2Content) this.layout_edition.getElement("tr").destroy();
                // if (this.layout_edition) this.layout_edition.getElement("tr").getElements("td").setStyles({
                //     "border-top": "solid windowtext 1.5pt",
                //     "mso-border-top-alt": "solid windowtext 1pt"
                // });
            }else if (!control.copyto2 || !this.layout_copyto2Content) {
                if (this.layout_copyto2Content) this.layout_copyto2Content.getParent("tr").destroy();
                // if (this.layout_edition) this.layout_edition.getElement("tr").getElements("td").setStyles({
                //     "border-bottom": "solid windowtext 0.75pt",
                //     "mso-border-bottom-alt": "solid windowtext 0.75pt"
                // });
            }
            if ((!control.editionUnit || !this.layout_edition_issuance_unit) && (!control.editionDate || !this.layout_edition_issuance_date)){
                if (this.layout_editionArea && (this.layout_edition_issuance_date || this.layout_edition_issuance_unit)){
                    var trs = this.layout_editionArea.getElement("table").rows;
                    trs.item(trs.length-1).destroy();
                    // trs = this.layout_editionArea.getElement("table").rows;
                    // var tr = trs.item(trs.length-1);
                    // if (tr){
                    //     tr.getElements("td").setStyles({
                    //         "border-bottom": "solid windowtext 1.5pt",
                    //         "mso-border-bottom-alt": "solid windowtext 1pt"
                    //     });
                    // }
                }
            }
            if (this.layout_editionArea && (this.layout_edition_issuance_date || this.layout_edition_issuance_unit)){
                trs = this.layout_editionArea.getElement("table").rows;
                for (var i=0; i<trs.length; i++){
                    var tds = trs.item(i).cells;
                    for (var n=0; n<tds.length; n++){
                        var td = tds.item(n);
                        var tdClass = td.get("class");
                        var tdClassList = (tdClass) ? tdClass.split(/\s+/g) : [];

                        if (tdClassList.indexOf("line_top_thin_bottom_thick") != -1) tdClassList = tdClassList.erase("line_top_thin_bottom_thick");
                        if (tdClassList.indexOf("line_top_thick_bottom_thin") != -1) tdClassList = tdClassList.erase("line_top_thick_bottom_thin");
                        if (tdClassList.indexOf("line_top_thick_bottom_thick") != -1) tdClassList = tdClassList.erase("line_top_thick_bottom_thick");

                        if (tdClassList.indexOf("line_top_thin_bottom_thin") == -1) tdClassList.unshift("line_top_thin_bottom_thin");

                        // td.setStyles({
                        //     "border-top": "solid windowtext 0.75pt",
                        //     "mso-border-top-alt": "solid windowtext 0.75pt",
                        //     "border-bottom": "solid windowtext 0.75pt",
                        //     "mso-border-bottom-alt": "solid windowtext 0.75pt",
                        // });
                        if (i==0 && i!=(trs.length-1)){
                            if (tdClassList.indexOf("line_top_thin_bottom_thin") != -1) tdClassList = tdClassList.erase("line_top_thin_bottom_thin");
                            tdClassList.unshift("line_top_thick_bottom_thin");
                            // td.setStyles({
                            //     "border-top": "solid windowtext 1.5pt",
                            //     "mso-border-top-alt": "solid windowtext 1pt"
                            // });
                        }else if (i==0 && i==(trs.length-1)){
                            if (tdClassList.indexOf("line_top_thin_bottom_thin") != -1) tdClassList = tdClassList.erase("line_top_thin_bottom_thin");
                            tdClassList.unshift("line_top_thick_bottom_thick");
                        }else if (i!=0 && i==(trs.length-1)){
                            if (tdClassList.indexOf("line_top_thin_bottom_thin") != -1) tdClassList = tdClassList.erase("line_top_thin_bottom_thin");
                            tdClassList.unshift("line_top_thin_bottom_thick");
                        }
                        // if (i==(trs.length-1)){
                        //     if (tdClassList.indexOf("line_bottom_thin") != -1) tdClassList = tdClassList.erase("line_bottom_thin");
                        //     if (tdClassList.indexOf("line_bottom_thick") == -1) tdClassList.push("line_bottom_thick");
                        //     // td.setStyles({
                        //     //     "border-bottom": "solid windowtext 1.5pt",
                        //     //     "mso-border-bottom-alt": "solid windowtext 1pt"
                        //     // });
                        // }

                        tdClass = tdClassList.join(" ");
                        td.set("class", tdClass);
                    }
                }
            }



            if (this.layout_editionArea) this.layout_editionArea.show();
            if (this.layout_copytoTitle) this.layout_copytoTitle[m("copyto")]();
            if (this.layout_copytoContent) this.layout_copytoContent[m("copyto")]();
            if (this.layout_copyto2Title) this.layout_copyto2Title[m("copyto2")]();
            if (this.layout_copyto2Content) this.layout_copyto2Content[m("copyto2")]();

            if (this.layout_edition_issuance_unit) this.layout_edition_issuance_unit[m("editionUnit")]();
            if (this.layout_edition_issuance_date) this.layout_edition_issuance_date[m("editionDate")]();
        }

        if (this.layout_meetingAttendArea) this.layout_meetingAttendArea[m("meetingAttend")]();
        if (this.layout_meetingLeaveArea) this.layout_meetingLeaveArea[m("meetingLeave")]();
        if (this.layout_meetingSitArea) this.layout_meetingSitArea[m("meetingSit")]();

        // this.layout_annotation[m("annotation")]();
        // this.layout_annotation[m("annotation")]();
        // this.layout_annotation[m("annotation")]();
    },
    reSetEdit: function(control){
        //未进行数据绑定时，可允许编辑
        if (!control) var control = this.getEditControl();
        if (!!this.json.subjectValueData && this.json.subjectValueType=="data") this.layout_subject.set("contenteditable", control.subject);

        // this.layout_subject.addEvent("keydown", function(e){
        //     debugger;
        //     if (this.json.subjectValueType=="data" && this.json.subjectValueData){
        //         // var v = e.target.get("HTML");
        //         // this.form.businessData.data[this.json.subjectValueData] = v
        //         var module = this.form.all[this.json.subjectValueData];
        //         if (module){
        //             var bindFun = module.node.retrieve(this.json.id+"bindFun");
        //             module.node
        //         }
        //     }
        // }.bind(this));
    },

    _loadUserInterface: function(callback){
        this.node.empty();
        this.node.setStyles(this.form.css.documentEditorNode);
        this.pages = [];

        this.allowEdit = this._isAllowEdit();
        this.allowPrint = this._isAllowPrint();
        this.allowHistory = this._isAllowHistory();
        this.toolNode = new Element("div", {"styles": this.css.doc_toolbar}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.doc_content}).inject(this.node);

        if (!this.form.isLoaded){
            this.form.addEvent("afterModulesLoad", function(){this.loadDocumentEditor(callback);}.bind(this));
        }else{
            this.loadDocumentEditor(callback);
        }

    },
    loadDocumentEditor: function(callback){
        this._loadToolbars();
        this._loadFiletextPage(function(){
            if (this.options.pageShow!=="double"){
                this._singlePage();
            }else{
                this._doublePage();
            }

            // this.form.addEvent("beforeProcess", function(){
            //     this.resetData();
            //     if (this.checkSaveNewEdition()) this.saveNewDataEdition();
            //     this.notSaveResetData = true;
            // }.bind(this));
            this.form.addEvent("beforeSave", function(){
                this.resetData();
                this.checkSaveNewEdition();
                //if (!this.notSaveResetData) this.resetData();
            }.bind(this));

            if (this.json.toWord=="y"){
                if (this.json.toWordTrigger=="open") this.docToWord();
                if (this.json.toWordTrigger=="save")  this.form.addEvent("beforeSave", this.docToWord.bind(this));
                if (this.json.toWordTrigger=="submit")  this.form.addEvent("beforeProcess", this.docToWord.bind(this));
            }
            if (!layout.mobile) this.loadSideToolbar();

            o2.load("/o2_lib/diff-match-patch/diff_match_patch.js");

            var id = this.form.businessData.data["$work"].job;
            o2.Actions.load("x_processplatform_assemble_surface").DocumentVersionAction.listWithJobCategory(id, this.json.id, function(json){
                this.historyDocumentList = json.data;
                if (this.historyDocumentList.length){
                    o2.Actions.load("x_processplatform_assemble_surface").DocumentVersionAction.get(this.historyDocumentList[this.historyDocumentList.length-1].id, function(json){
                        var data = JSON.parse(json.data.data);
                        this.originaHistoryData = data.data;
                    }.bind(this));
                }
            }.bind(this));

            if (callback) callback();
        }.bind(this));

        if (!this.form.documenteditorList) this.form.documenteditorList=[];
        this.form.documenteditorList.push(this);
    },

    checkSaveNewEdition: function(callback){
        debugger;
        if (!this.allowEdit || !this.data.filetext || this.data.filetext == this.json.defaultValue.filetext) return false;
        if (this.form.businessData.work){
            var originaData = this.form.businessData.originalData[this.json.id];
            var editionData = {"category": this.json.id};
            if (!originaData || !originaData.filetext || !this.originaHistoryData){
                //保存原始版本
                this.originaHistoryData = {"data": this.data.filetext};
                editionData.data = JSON.stringify({"data": this.data.filetext});
            }else if (originaData.filetext!=this.data.filetext){
                //保存历史版本
                var data = this.data.filetext;
                var earlyData = originaData.filetext;
                var dmp = new diff_match_patch();
                var diff_d = dmp.diff_main(earlyData, data);
                dmp.diff_cleanupSemantic(diff_d);
                var patch_list = dmp.patch_make(earlyData, data, diff_d);
                editionData.data = JSON.stringify({"patchs": dmp.patch_toText(patch_list)});
            }else{
                return false;
            }
            o2.Actions.load("x_processplatform_assemble_surface").DocumentVersionAction.create(this.form.businessData.work.id, editionData, function(json){
                //originaData.filetext = this.data.filetext;
                if (callback) callback();
            }.bind(this));
        }
    },
    // saveNewDataEdition: function(callback){
    //     if (this.form.businessData.work){
    //         var editionData = {"category": this.json.id};
    //         if (this.form.businessData.originalData[this.json.id] && this.form.businessData.originalData[this.json.id].filetext){
    //             var data = this.data.filetext;
    //             var earlyData = this.form.businessData.originalData[this.json.id].filetext;
    //             var dmp = new diff_match_patch();
    //             var diff_d = dmp.diff_main(earlyData, data);
    //             dmp.diff_cleanupSemantic(diff_d);
    //             var patch_list = dmp.patch_make(earlyData, data, diff_d);
    //             editionData.data = {"patchs": dmp.patch_toText(patch_list)};
    //
    //         }else{
    //             editionData.data = {"data": this.data.filetext};
    //         }
    //         o2.Actions.load("x_processplatform_assemble_surface").DocumentVersionAction.create(this.form.businessData.work.id, editionData, function(json){
    //             this.form.businessData.originalData[this.json.id] = this.data.filetext;
    //             if (callback) callback();
    //         }.bind(this));
    //     }
    // },
    resizeToolbar: function(){
        if (this.toolbarNode){
            var p = this.toolNode.getPosition(this.form.app.content);
            var size = this.toolNode.getSize();
            var pl = this.toolbarNode.getStyle("padding-left").toInt();
            var pr = this.toolbarNode.getStyle("padding-right").toInt();
            var x = size.x-pl-pr;

            if (p.y<0){
                this.toolbarNode.inject(this.form.node);
                this.toolbarNode.setStyles({
                    "position": "absolute",
                    "width": ""+x+"px",
                    "z-index": 20000,
                    "top": "0px",
                    "left": ""+p.x+"px"
                });
            }else{
                this.toolbarNode.inject(this.toolNode);
                this.toolbarNode.setStyles({"position": "static"});
            }
        }
    },
    resizeSidebar: function(){
        if (this.sidebarNode){
            var fileTextNode = this.contentNode.getElement("div.doc_layout_filetext");
            if (fileTextNode){
                this.sidebarNode.position({
                    relativeTo: fileTextNode,
                    position: 'topLeft',
                    edge: 'topRight',
                    offset: {"x": -20, "y": 60}
                });

                var p = fileTextNode.getPosition(this.form.app.content);
                var ptop = fileTextNode.getPosition(this.node);
                //if ((p.y+s.y)<0) this.sidebarNode.setStyle("top", p.y+s.y);

                if (p.y<0){
                    var top = ptop.y-p.y+200;
                    this.sidebarNode.setStyle("top", top);
                }
                // var p = fileTextNode.getPosition();
                // this.sidebarNode.setStyle("top", p.y);
            }
        }
    },
    loadSideToolbar: function(){
        if (this.allowEdit){
            if (this.pages.length){
                var fileTextNode = this.pages[0].getElement("div.doc_layout_filetext");
                if (fileTextNode){
                    this.sidebarNode = new Element("div", {"styles": this.css.doc_sidebar}).inject(this.node);
                    this.resizeSidebar();

                    this.scrollNode = this.sidebarNode.getParentSrcollNode();
                    if (this.scrollNode){
                        this.scrollNode.addEvent("scroll", function(){
                            this.resizeSidebar();
                        }.bind(this));
                    }

                    html = "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/icon/editdoc.png\" title=\""+MWF.xApplication.process.Xform.LP.editdoc+"\" MWFButtonAction=\"_switchReadOrEditInline\" MWFButtonText=\""+MWF.xApplication.process.Xform.LP.editdoc+"\"></span>";

                    this.sidebarNode.set("html", html);

                    MWF.require("MWF.widget.Toolbar", function() {
                        this.sideToolbar = new MWF.widget.Toolbar(this.sidebarNode, {"style": "documentEdit_side"}, this);
                        this.sideToolbar.load();
                    }.bind(this));

                }
            }
        }
    },
    _returnScale: function(){
        this.isScale = false;
        this.scale = 0;
        this.contentNode.setStyles({
            "transform":"scale(1)",
        });

        if (this.pages.length){
            this.pages.each(function(page){
                page.setStyles({
                    "transform":"scale(1)",
                });
            });
        }
        this.node.setStyles({
            "height": "auto"
        });
    },
    _checkScale: function(offset){
        offset = 0;
        if (this.pages.length){
            //var pageSize = this.pages[0].getSize();
            var pageSize_x = this.options.docPageFullWidth
            var contentSize = this.contentNode.getSize();
            var contentWidth = (offset) ? contentSize.x-20-offset : contentSize.x-20;
            if (contentWidth<pageSize_x){
                this.isScale = true;
                var scale = (contentWidth)/pageSize_x;
                this.scale = scale;
                this.zoom();
                this.resetNodeSize();
            }
        }
    },
    zoom: function(scale){
        if (scale) this.scale = scale;

        var w = this.node.getSize().x;
        if (this.history && this.history.historyListAreaNode) w = w-this.history.historyListAreaNode.getComputedSize().totalWidth-2;
        w = w/this.scale;
        this.contentNode.setStyles({
            "transform":"scale("+this.scale+")",
            "transform-origin": "0px 0px",
            "overflow": "hidden",
            "width": ""+w+"px"
        });
    },

    _switchReadOrEdit: function(){
        if (this.editMode){
            this._readFiletext();
            if (this.allowEdit) this.toolbar.childrenButton[0].setText(MWF.xApplication.process.Xform.LP.editdoc);
            this.editMode = false;
        }else{
            this._editFiletext();
            if (this.allowEdit) this.toolbar.childrenButton[0].setText(MWF.xApplication.process.Xform.LP.editdocCompleted)
            this.editMode = true;
        }
    },
    _switchReadOrEditInline: function(){
        if (this.editMode){
            this._readFiletext();
            if (this.allowEdit){
                if (!layout.mobile) this.sideToolbar.childrenButton[0].setText(MWF.xApplication.process.Xform.LP.editdoc);
                this.toolbar.childrenButton[0].setText(MWF.xApplication.process.Xform.LP.editdoc);
            }
            this.editMode = false;
        }else{
            this._editFiletext("inline");
            if (this.allowEdit){
                if (!layout.mobile) this.sideToolbar.childrenButton[0].setText(MWF.xApplication.process.Xform.LP.editdocCompleted);
                this.toolbar.childrenButton[0].setText(MWF.xApplication.process.Xform.LP.editdocCompleted);
            }
            this.editMode = true;
        }
    },
    _printDoc: function(){
        this.toWord(function(data){
            if (this.form.businessData.work && !this.form.businessData.work.completedTime){
                this.form.workAction.getAttachmentStream(data.id, this.form.businessData.work.id);
            }else{
                this.form.workAction.getWorkcompletedAttachmentStream(data.id, ((this.form.businessData.workCompleted) ? this.form.businessData.workCompleted.id : this.form.businessData.work.id));
            }
        }.bind(this), "$doc.doc");
    },
    _historyDoc: function(){
        this.getHistory(function(){
            //this.history.play();
        }.bind(this));
    },
    getHistory: function(callback){
        if (this.history){
            this.history.active(function(){
                if (callback) callback();
            });
        }else{
            MWF.xDesktop.requireApp("process.Xform", "widget.DocumentHistory", function(){
                this.history = new MWF.xApplication.process.Xform.widget.DocumentHistory(this);
                this.history.load(function(){
                    if (callback) callback();
                });
            }.bind(this));
        }
    },


    htmlToText: function(html){
        var tmpdiv = new Element("div", {"html": html});
        var text = tmpdiv.get("text");
        tmpdiv.destroy();
        return text;
    },


    _readFiletext: function(){
        //this._returnScale();
        this.zoom(1);
        if (this.filetextEditor) this.filetextEditor.destroy();
        if (this.filetextScrollNode){
            if (this.reLocationFiletextToolbarFun){
                this.filetextScrollNode.removeEvent("scroll", this.reLocationFiletextToolbarFun);
                //this.form.app.removeEvent("resize", this.reLocationFiletextToolbarFun);
                this.reLocationFiletextToolbarFun = null;
            }
            this.filetextScrollNode = null;
        }
        if (this.filetextToolbarNode) this.filetextToolbarNode = null;

        this.layout_filetext.setAttribute('contenteditable', false);
        this.data = this.getData();
        // debugger;
        if (!this.data.filetext){
            //this.data.filetext = this.json.defaultValue.filetext;
            this.layout_filetext.set("html", this.json.defaultValue.filetext);
        }
        //this._checkSplitPage(this.pages[0]);
        this._repage();
    },
    _editFiletext: function(inline){
        this._returnScale();
        this.zoom(1);
        this._singlePage();
        this.pages = [];
        this.contentNode.empty();
        this._createPage(function(control){
            this._loadPageLayout(control);

            // var docData = this._getBusinessData();
            // if (!docData) docData = this._getDefaultData();
            if (this.data.filetext == this.json.defaultValue.filetext) this.data.filetext = "";
            this.setData(this.data);

            this._checkScale();
            this.node.setStyles({
                "height":"auto"
            });

            this._createEditor(inline);
        }.bind(this));
    },
    _createEditor: function(inline){
        if (this.allowEdit){
            this.loadCkeditorFiletext(function(e){
                e.editor.focus();
                e.editor.getSelection().scrollIntoView();

                //this.getFiletextToolber();
                //this.filetextToolbarNode.inject(this.layout_filetext.getOffsetParent());

                this.locationFiletextToolbar();
            }.bind(this), inline);
        }
    },
    getFiletextToolber: function(){
        if (this.filetextEditor) {
            if (!this.filetextToolbarNode) {
                var className = "cke_editor_" + this.filetextEditor.name;
                var filetextToolbarNode = $$("." + className)[0];
                this.filetextToolbarNode = filetextToolbarNode;
                //filetextToolbarNode.destroy();
            }
        }
    },
    reLocationFiletextToolbar: function(){
        this.getFiletextToolber();
        // if (this.filetextToolbarNode){
        //     this.filetextToolbarNode.setStyle("display", "block");
        //     var offsetNode = this.layout_filetext.getOffsetParent();
        //
        //     this.filetextToolbarNode.setStyle("left", 0);
        //     var h = this.filetextToolbarNode.getSize().y;
        //     var p = this.layout_filetext.getPosition(offsetNode).y-h;
        //
        //     var postion = this.layout_filetext.getPosition(this.form.app.content);
        //     if (postion.y-h<0){
        //         this.filetextToolbarNode.inject(this.form.app.content);
        //         this.filetextToolbarNode.setStyle("top", 0);
        //         this.filetextToolbarNode.setStyle("left", ""+postion.x+"px");
        //     }else{
        //         this.filetextToolbarNode.inject(offsetNode);
        //         this.filetextToolbarNode.setStyle("top", "" + p + "px");
        //         this.filetextToolbarNode.setStyle("left", 0);
        //         this.filetextToolbarNode.setStyle("left", "auto");
        //     }
        // }
        if (this.filetextToolbarNode){
            if (!this.filetextScrollNode){
                var scrollNode = this.contentNode;
                while (scrollNode && (scrollNode.getScrollSize().y<=scrollNode.getSize().y || (scrollNode.getStyle("overflow")!=="auto" &&  scrollNode.getStyle("overflow-y")!=="auto"))){
                    scrollNode = scrollNode.getParent();
                }
                this.filetextScrollNode = scrollNode;
            }
            var h = this.filetextToolbarNode.getSize().y;
            var position = this.layout_filetext.getPosition();
            var size = this.layout_filetext.getSize();
            var contentSize = this.filetextScrollNode.getSize();

            if (position.y<0 && size.y+position.y+h<contentSize.y){
                var top = size.y+position.y;
                this.filetextToolbarNode.setStyle("top", ""+top+"px");
            }else if (position.y-h<0){
                this.filetextToolbarNode.setStyle("top", 0);
            }else{
                var p = this.layout_filetext.getPosition().y-h;
                this.filetextToolbarNode.setStyle("top", "" + p + "px");
            }


        }

        //
        // this.filetextToolbarNode.inject(offsetNode);
        //
        //
        // this.filetextToolbarNode.setStyle("top", ""+p+"px");

    },
    locationFiletextToolbar: function(){
        this.reLocationFiletextToolbar();
        if (this.filetextToolbarNode) {
            var scrollNode = this.contentNode;
            while (scrollNode && (scrollNode.getScrollSize().y<=scrollNode.getSize().y || (scrollNode.getStyle("overflow")!=="auto" &&  scrollNode.getStyle("overflow-y")!=="auto"))){
                scrollNode = scrollNode.getParent();
            }
            if (scrollNode){
                this.filetextScrollNode = scrollNode;
                this.reLocationFiletextToolbarFun = this.reLocationFiletextToolbar.bind(this);
                this.filetextScrollNode.addEvent("scroll", this.reLocationFiletextToolbarFun);
            }
        }
    },

    _isAllowEdit:function(){
        if (this.readonly) return false;
        if (this.json.allowEdit=="n") return false;
        if (this.json.allowEdit=="s"){
            if (this.json.allowEditScript && this.json.allowEditScript.code){
                return !!this.form.Macro.exec(this.json.allowEditScript.code, this);
            }
        }
        return true;
    },
    _isAllowPrint: function(){
        if (this.json.allowPrint=="n") return false;
        if (this.json.allowPrint=="s"){
            if (this.json.allowPrintScript && this.json.allowPrintScript.code){
                return !!this.form.Macro.exec(this.json.allowPrintScript.code, this);
            }
        }
        return true;
    },
    _isAllowHistory: function(){
        if (this.json.allowHistory=="n") return false;
        if (this.json.allowHistory=="s"){
            if (this.json.allowHistoryScript && this.json.allowHistoryScript.code){
                return !!this.form.Macro.exec(this.json.allowHistoryScript.code, this);
            }
        }
        return true;
    },

    _getEdit: function(name, typeItem, scriptItem){
        switch (this.json[typeItem]) {
            case "y":
                return true;
            case "n":
                return false;
            case "s":
                if (this.json[scriptItem] && this.json[scriptItem].code){
                    return !!this.form.Macro.exec(this.json[scriptItem].code, this);
                }
                return true;
        }
    },
    loadCkeditorStyle: function(node){
        if (node){
            o2.load("ckeditor", function(){
                //CKEDITOR.disableAutoInline = true;
                node.setAttribute('contenteditable', true);
                var editor = CKEDITOR.inline(this.layout_filetext, this._getEditorConfig());
                this.filetextEditor.on("instanceReady", function(e){
                    if (callback) callback(e);
                }.bind(this));
            }.bind(this));
        }
    },


    _loadToolbars: function(){
        var html ="";
        if (this.allowEdit){
            //html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/icon/editdoc.png\" title=\""+MWF.xApplication.process.Xform.LP.editdoc+"\" MWFButtonAction=\"_switchReadOrEdit\" MWFButtonText=\""+MWF.xApplication.process.Xform.LP.editdoc+"\"></span>";
            html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/icon/editdoc.png\" title=\""+MWF.xApplication.process.Xform.LP.editdoc+"\" MWFButtonAction=\"_switchReadOrEditInline\" MWFButtonText=\""+MWF.xApplication.process.Xform.LP.editdoc+"\"></span>";
            //html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/icon/headerdoc.png\" title=\""+MWF.xApplication.process.Xform.LP.headerdoc+"\" MWFButtonAction=\"_redheaderDoc\" MWFButtonText=\""+MWF.xApplication.process.Xform.LP.headerdoc+"\"></span>";
        }
        if (this.allowPrint){
            html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/icon/print.png\" title=\""+MWF.xApplication.process.Xform.LP.printdoc+"\" MWFButtonAction=\"_printDoc\" MWFButtonText=\""+MWF.xApplication.process.Xform.LP.printdoc+"\"></span>";
        }
        if (this.allowHistory){
           html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/icon/versions.png\" title=\""+MWF.xApplication.process.Xform.LP.history+"\" MWFButtonAction=\"_historyDoc\" MWFButtonText=\""+MWF.xApplication.process.Xform.LP.history+"\"></span>";
        }
        this.toolbarNode = new Element("div", {"styles": this.css.doc_toolbar_node}).inject(this.toolNode);
        this.toolbarNode.set("html", html);

        MWF.require("MWF.widget.Toolbar", function() {
            this.toolbar = new MWF.widget.Toolbar(this.toolbarNode, {"style": "documentEdit"}, this);
            this.toolbar.load();
        }.bind(this));

        this.scrollNode = this.toolbarNode.getParentSrcollNode();
        if (this.scrollNode){
            this.scrollNode.addEvent("scroll", function(){
                this.resizeToolbar();
            }.bind(this));
        }

        this.doublePageAction = new Element("div", {"styles": this.css.doc_toolbar_doublePage, "text": MWF.xApplication.process.Xform.LP.doublePage}).inject(this.toolbarNode);
        this.doublePageAction.addEvent("click", function(){
            if (this.options.pageShow!=="double"){
                this._doublePage();
            }else{
                this._singlePage();
            }
        }.bind(this));


        // this.zoomSelectAction =  new Element("select", {"styles": {"float": "right"}}).inject(this.toolbarNode);
        // var options = "<option value='2'>200%</option> " +
        //     "<option value='1.75'>175%</option>" +
        //     "<option value='1.5'>150%</option>" +
        //     "<option value='1' selected>100%</option>" +
        //     "<option value='0.75'>75%</option>" +
        //     "<option value='0.5'>50%</option>" +
        //     "<option value='0.25'>25%</option>"
        // this.zoomSelectAction.set("html", options);
        // this.zoomSelectAction.addEvent("change", function(e){
        //     this._returnScale();
        //     this.scale = e.target.options[e.target.selectedIndex].value;
        //     this.zoom();
        // }.bind(this));

    },
    _repage: function(delay){
        if (this.options.pageShow!=="double"){
            this._singlePage();
        }else{
            this._doublePage();
        }
        if (delay){
            if (!this.form.isLoaded){
                this.form.addEvent("afterLoad", this._checkScale.bind(this));
            }else{
                this._checkScale();
            }
        }else{
            this._checkScale();
        }
    },
    _singlePage: function(){
        //if (this.editMode) this._readFiletext();
        this.zoom(1);
        this._checkScale();

        var w = this.contentNode.getSize().x;
        var count = 1;
        var docPageFullWidth = (this.scale) ? this.scale*this.options.docPageFullWidth : this.options.docPageFullWidth;
        //var docPageFullWidth = this.options.docPageFullWidt;

        var pageWidth = count * docPageFullWidth;
        var margin = (w-pageWidth)/(count+1);

        if (this.isScale){
            margin = "10";
        }
        if (this.scale) margin = margin/this.scale;
        this.pages.each(function(page, i){
            page.setStyles({
                "float": "left",
                "margin-left": ""+margin+"px"
            });
        });

        this.resetNodeSize();
        // this.pages.each(function(page){
        //     page.setStyle("float", "none");
        // });
        this.resizeSidebar();
        this.options.pageShow="single";
        this.doublePageAction.set("text", MWF.xApplication.process.Xform.LP.doublePage);
    },
    resetNodeSize: function(){
        var contentSize = this.contentNode.getSize();
        var toolbarSize = this.toolNode.getSize();
        contentHeight = contentSize.y;
        var h = contentHeight+toolbarSize.y+20;
        //h = h - contentSize.y*(1-this.scale);

        this.node.setStyles({
            "height":""+h+"px"
        });
    },
    createWaitSplitPage: function(){
        this.node.mask({
            "style": {
                "background-color": "#cccccc",
                "opacity": 0.3
            }
        });

        this.waitSplitPageNode = new Element("div", {"styles": this.form.css.waitSplitPageNode, "text": MWF.xApplication.process.Xform.LP.computePage}).inject(this.node);
        this.waitSplitPageNode.position({
            "relativeTo": this.node,
            "position": "topRight",
            "edge": "topRight",
            "offset": {
                "x": -10,
                "y": 45
            }
        });

        //this.form.notice(MWF.xApplication.process.Xform.LP.computePage, "info", this.node);
    },
    clearWaitSplitPage: function(){
        this.node.unmask();
        if (this.waitSplitPageNode) this.waitSplitPageNode.destroy();
        this.waitSplitPageNode = null;
    },

    _doublePage: function(){
        if (this.editMode) this._switchReadOrEditInline();
        this.zoom(1);
        this.createWaitSplitPage();
        window.setTimeout(function(){
            this._checkSplitPage(this.pages[0]);

            this.zoom(1);
            var w = this.contentNode.getSize().x;
            var toPageWidth = (w-100)/2;
            scale = toPageWidth/this.options.docPageFullWidth;
            if (scale<1) this.zoom(scale);

            var docPageFullWidth = (this.scale) ? this.scale*this.options.docPageFullWidth : this.options.docPageFullWidth;
            //var docPageFullWidth = this.options.docPageFullWidth;

            var w = this.contentNode.getSize().x;
            var count = (w/docPageFullWidth).toInt();
            var pages = this.contentNode.getElements(".doc_layout_page");
            count = Math.min(pages.length, count);

            var pageWidth = count * docPageFullWidth;
            var margin = (w-pageWidth)/(count+1);
            if (this.scale) margin = margin/(this.scale);
            this.pages.each(function(page, i){
                page.setStyles({
                    "float": "left",
                    "margin-left": ""+margin+"px"
                });
            });
            // this.pages.each(function(page, i){
            //     if ((i % 2)===0){
            //         page.setStyle("float", "left");
            //     }else{
            //         page.setStyle("float", "right");
            //     }
            // });
            this.resetNodeSize();

            this.options.pageShow="double";
            this.doublePageAction.set("text", MWF.xApplication.process.Xform.LP.singlePage);

            this.resizeSidebar();
            this.clearWaitSplitPage();
        }.bind(this), 1000);
    },
    _getDefaultData: function(){
        return Object.clone(this.json.defaultValue);
        //return Object.clone(MWF.xApplication.process.Xform.LP.documentEditor);
    },

    _loadFiletextPage: function(callback){
        this.data = this._getBusinessData();
        if (!this.data) this.data = this._getDefaultData();
        this._computeData(true);

        this._createPage(function(control){
            this._loadPageLayout(control);
            
            // this.data = this._getBusinessData();
            // if (!this.data) this.data = this._getDefaultData();

            this.setData(this.data);
            // this._checkSplitPage(this.pages[0]);
            //this._repage(true);
            //this.loadCkeditorFiletext();

            if (!this.readonly){
                //if (this.json.allowEditFiletext!==false) this.loadCkeditorFiletext();
                // if (this.json.allowEditRedheader) this.loadCkeditorRedheader();
                // if (this.json.allowEditSubject) this.loadCkeditorSubject();
                // if (this.json.allowEditMainSend) this.loadCkeditorMainSend();
                // if (this.json.allowEditFileNo) this.loadCkeditorFileNo();
                // if (this.json.allowEditSigner) this.loadCkeditorSigner();
                // if (this.json.allowEditAttachment) this.loadCkeditorAttachment();
            }

            if (callback) callback();
        }.bind(this));
    },


    _getEditorConfig: function(){
        // var mathElements = [
        //     'math',
        //     'maction',
        //     'maligngroup',
        //     'malignmark',
        //     'menclose',
        //     'merror',
        //     'mfenced',
        //     'mfrac',
        //     'mglyph',
        //     'mi',
        //     'mlabeledtr',
        //     'mlongdiv',
        //     'mmultiscripts',
        //     'mn',
        //     'mo',
        //     'mover',
        //     'mpadded',
        //     'mphantom',
        //     'mroot',
        //     'mrow',
        //     'ms',
        //     'mscarries',
        //     'mscarry',
        //     'msgroup',
        //     'msline',
        //     'mspace',
        //     'msqrt',
        //     'msrow',
        //     'mstack',
        //     'mstyle',
        //     'msub',
        //     'msup',
        //     'msubsup',
        //     'mtable',
        //     'mtd',
        //     'mtext',
        //     'mtr',
        //     'munder',
        //     'munderover',
        //     'semantics',
        //     'annotation',
        //     'annotation-xml'
        // ];

        //CKEDITOR.plugins.addExternal('ckeditor_wiris', 'https://ckeditor.com/docs/ckeditor4/4.13.0/examples/assets/plugins/ckeditor_wiris/', 'plugin.js');

        var editorConfig = {
            qtRows: 20, // Count of rows
            qtColumns: 20, // Count of columns
            qtBorder: '1', // Border of inserted table
            qtWidth: '95%', // Width of inserted table
            qtStyle: { 'border-collapse' : 'collapse' },
            qtClass: 'test', // Class of table
            qtCellPadding: '0', // Cell padding table
            qtCellSpacing: '0', // Cell spacing table
            qtPreviewBorder: '4px double black', // preview table border
            qtPreviewSize: '4px', // Preview table cell size
            qtPreviewBackground: '#c8def4', // preview table background (hover)

            // format_tags: '标题一;标题二;标题三;标题四;正文', // entries is displayed in "Paragraph format"
            format_tags: '标题一;标题二;正文(标题三,四)', // entries is displayed in "Paragraph format"
            'format_标题一': {
                name: '标题一',
                element: 'div',
                styles: {
                    'font-family': '黑体',
                    'font-size': '16pt'
                }
            },
            'format_标题二': {
                name: '标题二',
                element: 'div',
                styles: {
                    'font-family': '楷体',
                    'font-size': '16pt'
                }
            },
            // 'format_标题三': {
            //     name: '标题三',
            //     element: 'div',
            //     styles: {
            //         'font-family': '仿宋',
            //         'font-size': '16pt'
            //     }
            // },
            // 'format_标题四': {
            //     name: '标题四',
            //     element: 'div',
            //     styles: {
            //         'font-family': '仿宋',
            //         'font-size': '16pt'
            //     }
            // },
            'format_正文(标题三,四)': {
                name: '正文(标题三,四)',
                element: 'div',
                styles: {
                    'font-family': '仿宋',
                    'font-size': '16pt'
                }
            }


        };
        editorConfig.toolbarGroups = [
            { name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
            { name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
            { name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
            { name: 'forms', groups: [ 'forms' ] },
            { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
            { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
            { name: 'links', groups: [ 'links' ] },
            { name: 'insert', groups: [ 'insert' ] },
            { name: 'styles', groups: [ 'styles' ] },
            { name: 'colors', groups: [ 'colors' ] },
            { name: 'tools', groups: [ 'tools' ] },
            { name: 'others', groups: [ 'others' ] },
            { name: 'about', groups: [ 'about' ] }
        ];
        //editorConfig.extraPlugins = "ecnet,colordialog,tableresize,quicktable,mathjax,ckeditor_wiris";
        //editorConfig.extraPlugins = "ecnet,colordialog,quicktable,tableresize,eqneditor";
        //editorConfig.extraPlugins = "tableresize,quicktable";
        editorConfig.extraPlugins = "quicktable,tableresize";

        //editorConfig.mathJaxLib = 'https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.4/MathJax.js?config=TeX-AMS_HTML',
        editorConfig.removeButtons = 'NumberedList,Source,Save,NewPage,Preview,Print,Templates,Paste,PasteFromWord,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,Bold,Italic,Underline,Strike,Subscript,Superscript,CopyFormatting,RemoveFormat,BulletedList,Outdent,Indent,Blockquote,CreateDiv,BidiLtr,BidiRtl,Language,Link,Unlink,Anchor,Image,Flash,HorizontalRule,Smiley,SpecialChar,PageBreak,Iframe,TextColor,BGColor,Maximize,ShowBlocks,About,Styles,Font,FontSize';

        //editorConfig.extraAllowedContent = mathElements.join(' ') + '(*)[*]{*};img[data-mathml,data-custom-editor,role](Wirisformula)';

        //editorConfig.removeButtons = 'NewPage,Templates,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,Bold,Italic,Underline,Strike,Subscript,Superscript,Blockquote,CreateDiv,BidiLtr,BidiRtl,Language,Link,Unlink,Anchor,Image,Flash,HorizontalRule,Smiley,SpecialChar,Iframe,Styles,Font,FontSize,TextColor,BGColor,ShowBlocks,About';
        editorConfig.removePlugins = ['magicline'];
        editorConfig.enterMode = 3;
        // editorConfig.extraPlugins = ['ecnet','mathjax'];
        // editorConfig.removePlugins = ['magicline'];
        // editorConfig.mathJaxLib = 'https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.4/MathJax.js?config=TeX-AMS_HTML';
        return editorConfig;
    },

    _checkSplitPage: function(pageNode){
        if (this.layout_edition)  this.layout_edition.setStyles({ "position": "static"});
        var contentNode = pageNode.getFirst();
        if (contentNode.getSize().y>this.options.docPageHeight){
            this._splitPage(pageNode);
        }
        var i = pageNode.get("data-pagecount").toInt();

        if (i && this.pages.length-1>=i){
            this._checkSplitPage(this.pages[i]);
        }
        if (this.layout_edition)  this.layout_edition.setStyles({ "position": "absolute", "bottom": "0px" });
    },

    _splitFiletextNodeOneWord:function(lnode, nextPageNode){
        var text = lnode.textContent;
        var len = text.length;
        var left = text.substring(0, len-1);
        var right = text.substring(len-1, len);
        lnode.textContent = left;
        nextPageNode.textContent = right+nextPageNode.textContent;
        //nextPageNode.appendText(right, "top");
    },
    _splitFiletext: function(node, nextPageNode, nextFiletextNode, pageNode){
        var contentNode = pageNode.getFirst();
        var lnode = node.lastChild;
        if (!lnode){
            if (node.parentNode) node.parentNode.removeChild(node);
            //node.remove();
        }else{
            while (contentNode.getSize().y>this.options.docPageHeight && lnode) {
                var tmpnode = lnode.previousSibling;
                var nodeType = lnode.nodeType;
                if (!nextPageNode) nextPageNode = nextFiletextNode;

                if (nodeType == Node.ELEMENT_NODE) {
                    if (lnode.tagName == "table") {
                        lnode.inject(nextPageNode);
                    } else if (lnode.tagName == "BR") {
                        if (lnode.parentNode) lnode.parentNode.removeChild(lnode);
                    } else {
                        var id = lnode.get("data-pagePart");
                        if (!id){
                            id = (new o2.widget.UUID()).toString();
                            lnode.set("data-pagePart", id);
                        }
                        var tmpNode = nextPageNode.getFirst();
                        if (tmpNode && tmpNode.get("data-pagePart")==id){
                            nextPageNode = tmpNode;
                        }else{
                            nextPageNode = lnode.clone(false).inject(nextPageNode, "top");
                        }
                        //var subnode = lnode.getLast();
                        this._splitFiletext(lnode, nextPageNode, nextFiletextNode, pageNode);
                        if (!lnode.firstChild) if (lnode.parentNode) lnode.parentNode.removeChild(lnode);
                        nextPageNode = nextPageNode.getParent();
                    }
                } else if (nodeType == Node.TEXT_NODE) {
                    var nextPageTextNode = nextPageNode.insertBefore(document.createTextNode(""), nextPageNode.firstChild);
                    while ((contentNode.getSize().y > this.options.docPageHeight) && lnode.textContent) {
                        //console.log(contentNode.getSize().y);
                        this._splitFiletextNodeOneWord(lnode, nextPageTextNode)
                    }
                    if (!lnode.textContent) if (lnode.parentNode) lnode.parentNode.removeChild(lnode); //lnode.remove();
                } else {
                    //lnode.remove();
                    if (lnode.parentNode) lnode.parentNode.removeChild(lnode);
                }

                lnode = tmpnode;
            }
            if (!node.lastChild) if (node.parentNode) node.parentNode.removeChild(node); //node.remove();
        }
        //this._checkSplitPage(pageNode);
    },

    _splitPage: function(pageNode){
        var contentNode = pageNode.getFirst();
        var blockNodes = pageNode.getElements(".doc_block");
        if (blockNodes.length){
            var blockNode = blockNodes[blockNodes.length-1];
            var idx = this.pages.indexOf(pageNode);
            if (this.pages.length<=idx+1) this._createNewPage();
            var nextPage = this.pages[idx+1];
            if (blockNode.hasClass("doc_layout_filetext")){

                var filetextNode = nextPage.getElement(".doc_layout_filetext");
                if (!filetextNode){
                    filetextNode = new Element("div.doc_layout_filetext").inject(nextPage.getFirst(), "top");
                    //filetextNode.setAttribute('contenteditable', true);
                }
                if (!filetextNode.hasClass("doc_block"))filetextNode.addClass("doc_block");
                //var nextEditor = filetextNode.retrieve("editor");

                var node = blockNode;

                var nextPageNode = filetextNode;
                this._splitFiletext(node, nextPageNode, filetextNode, pageNode);

            }else{
                blockNode.inject(nextPage.getFirst(), "top");
                //var contentNode = pageNode.getFirst();
                if (contentNode.getSize().y>this.options.docPageHeight){
                    this._splitPage(pageNode);
                }
            }
        }
    },

    loadCkeditorFiletext: function(callback, inline){
        if (this.layout_filetext){
            o2.load("/o2_lib/htmleditor/ckeditor4130/ckeditor.js", function(){
                CKEDITOR.disableAutoInline = true;
                this.layout_filetext.setAttribute('contenteditable', true);

                if (inline){
                    this.filetextEditor = CKEDITOR.inline(this.layout_filetext, this._getEditorConfig());
                }else{
                    this.filetextEditor = CKEDITOR.replace(this.layout_filetext, this._getEditorConfig());
                }
                this.filetextEditor.on("instanceReady", function(e){
                    if (callback) callback(e);
                }.bind(this));
                this.filetextEditor.on( 'focus', function( e ) {
                    window.setTimeout(this.reLocationFiletextToolbar.bind(this), 10);
                }.bind(this) );

                // this.filetextEditor.on("key", function(e){
                //     debugger;
                // }.bind(this));

            }.bind(this));
        }
    },

    _loadEvents: function(editorConfig){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                this.editor.on(key, function(event){
                    return this.form.Macro.fire(e.code, this, event);
                }.bind(this), this);
            }
        }.bind(this));

    },

    _bindFieldChange: function(name,dataItem, dom){
        var field = this.form.all[this.json[dataItem]];
        if (field){
            var bindFun = function(){
                this._computeItemFieldData(name, dataItem);
                //if (this.data[name]){
                if (this[dom]){
                    if (dom=="layout_redHeader" || dom=="layout_subject"){
                        this[dom].set("html", this.data[name]|| "");
                    }else if (dom=="layout_attachment"){
                        this.setAttachmentData();
                    }else{
                        this[dom].set("text", this.data[name]|| "");
                    }
                }

                this.reSetShow();
                //}
            }.bind(this);
            field.node.store(this.json.id+"bindFun", bindFun);
            field.addModuleEvent("change", bindFun);
        }
    },
    _computeItemFieldData: function(name, dataItem){
        var v = "";
        var module = this.form.all[this.json[dataItem]];
        if (module && module.getData) v = module.getData();
        if (!v) v = this.form.businessData.data[this.json[dataItem]];

        if (v){
            var t = o2.typeOf(v);
            switch (t) {
                case "string":
                    switch (name) {
                        case "issuanceDate":
                        case "editionDate":
                            var d = new Date(v);
                            if (d.isValid()){
                                var y = d.getFullYear();
                                var m = d.getMonth();
                                var day = d.getDate();
                                m = m +1;
                                this.data[name] = ""+y+"年"+m+"月"+day+"日";
                            }else{
                                this.data[name] = v;
                            }
                            //this.data[name] = (new Date(v).isValid()) ? Date.parse(v).format("％Y年％m月％d％日") : v;
                            break;
                        case "mainSend":
                            this.data[name] = v + "：";
                            break;
                        default:
                            this.data[name] = v;
                    }
                    break;
                case "array":
                    var strs = [];
                    v.each(function(value){
                        if (o2.typeOf(value)=="object" && value.distinguishedName){
                            strs.push(value.name);
                        }else{
                            strs.push(value.toString());
                        }
                    });
                    //if (strs.length){
                    switch (name) {
                        case "attachment":
                            // this.data[name] = strs.map(function(n, i){ var j = i+1; return j+"、"+n}).join("<br>");
                            var atts = strs.map(function(a){
                                return (a.indexOf(".")!=-1) ? a.substring(0, a.lastIndexOf(".")) : a;
                            });
                            this.data[name] = atts;
                            break;
                        case "issuanceDate":
                        case "editionDate":
                            var tmpStrs = strs.map(function(n, i){
                                var d = Date.parse(n);
                                return (d.isValid()) ? d.format("%Y年%m月%d日") : n;
                            });
                            this.data[name] = tmpStrs.join("，");
                            break;
                        case "mainSend":
                            this.data[name] = strs.join("，") + "：";
                            break;
                        default:
                            this.data[name] = strs.join("，");
                    }
                    //}
                    break;
                default:
                    this.data[name] = v.toString();
            }
        }else{
            this.data[name] = this.json.defaultValue[name];
        }
    },
    computeData: function(){
        this._computeData(false);
        this.setData(this.data);
    },
    _computeItemData: function(name, typeItem, dataItem, scriptItem, ev, dom){
        switch (this.json[typeItem]) {
            case "data":
                if (this.json[dataItem]){
                    if (ev) this._bindFieldChange(name, dataItem, dom);
                    this._computeItemFieldData(name, dataItem);
                }
                break;
            case "script":
                if (this.json[scriptItem] && this.json[scriptItem].code){
                    var v = this.form.Macro.exec(this.json[scriptItem].code, this);
                    this.data[name] = v;
                    if (name=="attachment"){
                        //this.data[name] = (typeOf(v)=="array") ? v.map(function(n, i){ var j = i+1; return j+"、"+n}).join("<br>") : v;
                        this.data[name] = (typeOf(v)=="array") ? v : [v];
                    }
                    if (name=="issuanceDate" || name=="editionDate"){
                        var d = Date.parse(v);
                        this.data[name] = (d.isValid()) ? d.format("%Y年%m月%d日") : v;
                    }
                }
                break;
        }
    },

    _computeData: function(ev){
        this._computeItemData("copies", "copiesValueType", "copiesValueData", "copiesValueScript", ev, "layout_copies");
        this._computeItemData("secret", "secretValueType", "secretValueData", "secretValueScript", ev, "layout_secret");
        this._computeItemData("priority", "priorityValueType", "priorityValueData", "priorityValueScript", ev, "layout_priority");
        this._computeItemData("redHeader", "redHeaderValueType", "redHeaderValueData", "redHeaderValueScript", ev, "layout_redHeader");
        this._computeItemData("fileno", "filenoValueType", "filenoValueData", "filenoValueScript", ev, "layout_fileno");
        this._computeItemData("signer", "signerValueType", "signerValueData", "signerValueScript", ev, "layout_signer");
        this._computeItemData("subject", "subjectValueType", "subjectValueData", "subjectValueScript", ev, "layout_subject");
        this._computeItemData("mainSend", "mainSendValueType", "mainSendValueData", "mainSendValueScript", ev, "layout_mainSend");
        this._computeItemData("attachment", "attachmentValueType", "attachmentValueData", "attachmentValueScript", ev, "layout_attachment");
        this._computeItemData("issuanceUnit", "issuanceUnitValueType", "issuanceUnitValueData", "issuanceUnitValueScript", ev, "layout_issuanceUnit");
        this._computeItemData("issuanceDate", "issuanceDateValueType", "issuanceDateValueData", "issuanceDateValueScript", ev, "layout_issuanceDate");
        this._computeItemData("annotation", "annotationValueType", "annotationValueData", "annotationValueScript", ev, "layout_annotation");
        this._computeItemData("copyto", "copytoValueType", "copytoValueData", "copytoValueScript", ev, "layout_copytoContent");
        this._computeItemData("copyto2", "copyto2ValueType", "copyto2ValueData", "copyto2ValueScript", ev, "layout_copyto2Content");
        this._computeItemData("editionUnit", "editionUnitValueType", "editionUnitValueData", "editionUnitValueScript", ev, "layout_edition_issuance_unit");
        this._computeItemData("editionDate", "editionDateValueType", "editionDateValueData", "editionDateValueScript", ev, "layout_edition_issuance_date");

        this._computeItemData("meetingAttend", "meetingAttendValueType", "meetingAttendValueData", "meetingAttendValueScript", ev, "layout_meetingAttendContent");
        this._computeItemData("meetingLeave", "meetingLeaveValueType", "meetingLeaveValueData", "meetingLeaveValueScript", ev, "layout_meetingLeaveContent");
        this._computeItemData("meetingSit", "meetingSitValueType", "meetingSitValueData", "meetingSitValueScript", ev, "layout_meetingSitContent");
    },

    _loadValue: function(){
        var data = this._getBusinessData();
    },
    reload: function(){
        this.resetData();
    },
    resetData: function(diffFiletext){
        if (this.editMode){ this._switchReadOrEditInline(); }

        this._computeData(false);

        this.pages = [];
        this.contentNode.empty();
        if (this.allowEdit) this.toolbar.childrenButton[0].setText(MWF.xApplication.process.Xform.LP.editdoc);
        this.editMode = false;

        this._createPage(function(control){
            this._loadPageLayout(control);

            this.setData(this.data, diffFiletext);
            //this._checkSplitPage(this.pages[0]);
debugger;
            this._repage();
        }.bind(this));
    },
    getData: function(){
        //if (this.editMode){
        if (this.layout_copies) this.data.copies = this.layout_copies.get("text");
        if (this.layout_secret) this.data.secret = this.layout_secret.get("text");
        if (this.layout_priority) this.data.priority = this.layout_priority.get("text");
        if (this.layout_redHeader) this.data.redHeader = this.layout_redHeader.get("html");
        if (this.layout_fileno) this.data.fileno = this.layout_fileno.get("text");
        if (this.layout_signerTitle) this.data.signerTitle = this.layout_signerTitle.get("text");
        if (this.layout_signer) this.data.signer = this.layout_signer.get("text");
        if (this.layout_subject) this.data.subject = this.layout_subject.get("html");
        if (this.layout_mainSend) this.data.mainSend = this.layout_mainSend.get("text");
        if (this.editMode) if (this.layout_filetext) this.data.filetext = this.layout_filetext.get("html");
        if (this.layout_signer) this.data.signer = this.layout_signer.get("text");
        if (this.layout_attachmentTitle) this.data.attachmentTitle = this.layout_attachmentTitle.get("text");
        if (this.layout_attachment){
            this._computeItemData("attachment", "attachmentValueType", "attachmentValueData", "attachmentValueScript", false, "layout_attachment");
            // var atts = [];
            // var nodes = this.layout_attachment.getElements(".doc_layout_attachment_content_name");
            // if (nodes.length){
            //     nodes.each(function(node){
            //         atts.push(node.get("text"));
            //     });
            // }
            // this.data.attachment = atts;
        }
        if (this.layout_issuanceUnit) this.data.issuanceUnit = this.layout_issuanceUnit.get("text");
        if (this.layout_issuanceDate) this.data.issuanceDate = this.layout_issuanceDate.get("text");
        if (this.layout_annotation) this.data.annotation = this.layout_annotation.get("text");
        if (this.layout_copytoTitle) this.data.copytoTitle = this.layout_copytoTitle.get("text");
        if (this.layout_copytoContent) this.data.copyto = this.layout_copytoContent.get("text");
        if (this.layout_copyto2Title) this.data.copyto2Title = this.layout_copyto2Title.get("text");
        if (this.layout_copyto2Content) this.data.copyto2 = this.layout_copyto2Content.get("text");
        if (this.layout_edition_issuance_unit) this.data.editionUnit = this.layout_edition_issuance_unit.get("text");
        if (this.layout_edition_issuance_date) this.data.editionDate = this.layout_edition_issuance_date.get("text");

        if (this.layout_meetingAttendTitle) this.data.meetingAttendTitle = this.layout_meetingAttendTitle.get("text");
        if (this.layout_meetingLeaveTitle) this.data.meetingLeaveTitle = this.layout_meetingLeaveTitle.get("text");
        if (this.layout_meetingSitTitle) this.data.meetingSitTitle = this.layout_meetingSitTitle.get("text");

        if (this.layout_meetingAttendContent) this.data.meetingAttend = this.layout_meetingAttendContent.get("html");
        if (this.layout_meetingLeaveContent) this.data.meetingLeave = this.layout_meetingLeaveContent.get("html");
        if (this.layout_meetingSitContent) this.data.meetingSit = this.layout_meetingSitContent.get("html");

        //}
        return this.data;
    },
    setAttachmentData: function(){
        if (!this.attachmentTemplete){
            this.attachmentTemplete = this.layout_attachment.get("html");
        }
        this.layout_attachment.empty();
        if (this.data.attachment && this.data.attachment.length && this.data.attachment.each){
            //var tmpdiv = new Element("div", {"styles": {"display":"none"}}).inject(document.body);
            var tmpdiv = new Element("div");
            this.data.attachment.each(function(name, idx){
                tmpdiv.set("html", this.attachmentTemplete);
                var serialNode = tmpdiv.getElement(".doc_layout_attachment_content_serial");
                var nameNode = tmpdiv.getElement(".doc_layout_attachment_content_name");
                if (serialNode) serialNode.set("text", idx+1);
                if (nameNode) nameNode.set("text", name);
                var html = tmpdiv.get("html");
                tmpdiv.empty();
                this.layout_attachment.appendHTML(html);
            }.bind(this));
            tmpdiv.destroy();
        }
    },
    setData: function(data, diffFiletext){
        if (data){
            this.data = data;
            // this.data["$json"] = this.json;
            this._setBusinessData(data);
            if (this.layout_copies){
                if (data.copies){
                    this.layout_copies.set("text", data.copies || " ");
                }else{
                    this.layout_copies.set("html", "<span>&nbsp</span>");
                }
            }
            if (this.layout_secret){
                if (data.secret){
                    this.layout_secret.set("text", data.secret || " ");
                }else{
                    this.layout_secret.set("html", "<span>&nbsp</span>");
                }
            }
            if (this.layout_priority){
                if (data.priority){
                    this.layout_priority.set("text", data.priority || " ");
                }else{
                    this.layout_priority.set("html", "<span>&nbsp</span>");
                }
            }


            if (this.layout_redHeader) this.layout_redHeader.set("html", data.redHeader || "");
            if (this.layout_fileno) this.layout_fileno.set("text", data.fileno || " ");
            if (this.layout_signerTitle) this.layout_signerTitle.set("text", data.signerTitle || " ");
            if (this.layout_signer) this.layout_signer.set("text", data.signer || " ");
            if (this.layout_subject) this.layout_subject.set("html", data.subject || " ");
            if (this.layout_mainSend) this.layout_mainSend.set("text", data.mainSend || " ");
            if (diffFiletext) {
                this.layout_filetext.set("html", diffFiletext);
            }else if (this.layout_filetext){
                //this.layout_filetext.set("placeholder", this.json.defaultValue.filetext);
                this.layout_filetext.set("html", data.filetext || "");
            }
            if (this.layout_signer) this.layout_signer.set("text", data.signer || "");
            if (this.layout_attachmentTitle) this.layout_attachmentTitle.set("text", data.attachmentTitle || " ");

            if (this.layout_attachment){
                this.setAttachmentData();
            }

            if (this.layout_issuanceUnit) this.layout_issuanceUnit.set("text", data.issuanceUnit || " ");
            if (this.layout_issuanceDate) this.layout_issuanceDate.set("text", data.issuanceDate || " ");
            if (this.layout_annotation) this.layout_annotation.set("text", data.annotation || " ");
            if (this.layout_copytoTitle) this.layout_copytoTitle.set("text", data.copytoTitle || " ");
            if (this.layout_copytoContent) this.layout_copytoContent.set("text", data.copyto || " ");
            if (this.layout_copyto2Title) this.layout_copyto2Title.set("text", data.copyto2Title || " ");
            if (this.layout_copyto2Content) this.layout_copyto2Content.set("text", data.copyto2 || " ");
            if (this.layout_edition_issuance_unit) this.layout_edition_issuance_unit.set("text", data.editionUnit || " ");
            if (this.layout_edition_issuance_date) this.layout_edition_issuance_date.set("text", data.editionDate || " ");

            if (this.layout_meetingAttendTitle) this.layout_meetingAttendTitle.set("text", data.meetingAttendTitle || this.json.defaultValue.meetingAttendTitle || " ");
            if (this.layout_meetingLeaveTitle) this.layout_meetingLeaveTitle.set("text", data.meetingLeaveTitle || this.json.defaultValue.meetingLeaveTitle || " ");
            if (this.layout_meetingSitTitle) this.layout_meetingSitTitle.set("text", data.meetingSitTitle || this.json.defaultValue.meetingSitTitle || " ");

            if (this.layout_meetingAttendContent) this.layout_meetingAttendContent.set("html", data.meetingAttend || " ");
            if (this.layout_meetingLeaveContent) this.layout_meetingLeaveContent.set("html", data.meetingLeave || " ");
            if (this.layout_meetingSitContent) this.layout_meetingSitContent.set("html", data.meetingSit || " ");
        }
    },
    createErrorNode: function(text){
        var node = new Element("div");
        var iconNode = new Element("div", {
            "styles": {
                "width": "20px",
                "height": "20px",
                "float": "left",
                "background": "url("+"/x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
            }
        }).inject(node);
        var textNode = new Element("div", {
            "styles": {
                "line-height": "20px",
                "margin-left": "20px",
                "color": "red",
                "word-break": "keep-all"
            },
            "text": text
        }).inject(node);
        return node;
    },

    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            this.isNotValidationMode = true;
            this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            this.node.setStyle("border", "1px solid red");

            this.errNode = this.createErrorNode(text).inject(this.node, "after");
            this.showNotValidationMode(this.node);
        }
    },
    showNotValidationMode: function(node){
        var p = node.getParent("div");
        if (p){
            if (p.get("MWFtype") == "tab$Content"){
                if (p.getParent("div").getStyle("display")=="none"){
                    var contentAreaNode = p.getParent("div").getParent("div");
                    var tabAreaNode = contentAreaNode.getPrevious("div");
                    var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
                    var tabNode = tabAreaNode.getLast().getFirst().getChildren()[idx];
                    tabNode.click();
                    p = tabAreaNode.getParent("div");
                }
            }
            this.showNotValidationMode(p);
        }
    },
    validationMode: function(){
        if (this.isNotValidationMode){
            this.isNotValidationMode = false;
            this.node.setStyles(this.node.retrieve("borderStyle"));
            if (this.errNode){
                this.errNode.destroy();
                this.errNode = null;
            }
        }
    },

    validationConfigItem: function(routeName, data){
        var flag = (data.status=="all") ? true: (routeName == data.decision);
        if (flag){
            var n = this.getData();
            var v = (data.valueType=="value") ? n : n.length;
            switch (data.operateor){
                case "isnull":
                    if (!v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    },
    validationConfig: function(routeName, opinion){
        if (this.json.validationConfig){
            if (this.json.validationConfig.length){
                for (var i=0; i<this.json.validationConfig.length; i++) {
                    var data = this.json.validationConfig[i];
                    if (!this.validationConfigItem(routeName, data)) return false;
                }
            }
            return true;
        }
        return true;
    },

    validation: function(routeName, opinion){
        if (!this.validationConfig(routeName, opinion))  return false;

        if (!this.json.validation) return true;
        if (!this.json.validation.code) return true;
        var flag = this.form.Macro.exec(this.json.validation.code, this);
        if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
        if (flag.toString()!="true"){
            this.notValidationMode(flag);
            return false;
        }
        return true;
    },
    removeDisplayNone: function(node){
        var n = node.getFirst();
        while (n){
            if (n.getStyle("display")=="none"){
                var tmp = n.getNext();
                n.destroy();
                n = tmp;
            }else{
                n = this.removeDisplayNone(n);
                n = n.getNext();
            }
        }
        return node;
    },

    toWord: function(callback, name){
        var toEdit = false;
        if (this.editMode){
            toEdit = true;
            this._readFiletext();
        }
        //this._returnScale();
        this.zoom(1);

        this.pages = [];
        this.contentNode.empty();
        this._createPage(function(control){
            this._loadPageLayout(control);
            this.setData(this.data);
            this._checkScale();
            this.node.setStyles({
                "height":"auto"
            });

            //var content = this.contentNode.getFirst().getFirst().get("html");
            var tmpNode = this.contentNode.getFirst().getFirst().clone(true);
            var htmlNode = tmpNode.getLast();
            htmlNode = this.removeDisplayNone(htmlNode);
            var content = "<html><head><meta charset=\"UTF-8\" /></head><body>"+tmpNode.get("html")+"</body></html>";

            var body = {
                "fileName": name || this.json.toWordFilename || "$doc.doc",
                "site": this.json.toWordSite || "$doc",
                "content": content
            };
            o2.Actions.get("x_processplatform_assemble_surface").docToWord(this.form.businessData.work.id, body, function(json){
                if (this.form.businessData.workCompleted){
                    o2.Actions.get("x_processplatform_assemble_surface").getAttachmentWorkcompleted(json.data.id, this.form.businessData.workCompleted.id,function(attjson){
                        if (callback) callback(attjson.data);
                        this.showToWord(attjson.data);
                    }.bind(this));
                }else{
                    o2.Actions.get("x_processplatform_assemble_surface").getAttachment(json.data.id, this.form.businessData.work.id,function(attjson){
                        if (callback) callback(attjson.data);
                        this.showToWord(attjson.data);
                    }.bind(this));
                }
            }.bind(this));
            tmpNode.destroy();

            if (!toEdit){
                this._readFiletext();
            }else{
                this._createEditor();
            }
        }.bind(this));
    },
    docToWord: function(){
        var flag = true;
        if (this.json.toWordConditionScript && this.json.toWordConditionScript.code){
            flag = !!this.form.Macro.exec(this.json.toWordConditionScript.code, this);
        }
        if (flag){
            this.toWord();
        }
    },
    showToWord: function(att){
        var site = this.json.toWordSite || "$doc";
        var attModule = this.form.all[site];
        if (attModule){
            attModule.attachmentController.clear();
            attModule.attachmentController.addAttachment(att);
        }
    }
});
