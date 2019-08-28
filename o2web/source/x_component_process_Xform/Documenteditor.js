MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Documenteditor = MWF.APPDocumenteditor =  new Class({
	Extends: MWF.APP$Module,
    options: {
        "moduleEvents": ["load", "postLoad", "afterLoad"],
        "docPageHeight": 850.4,
        "docPageFullWidth": 794,
        "pageShow": "double"
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
        if (this.fireEvent("queryLoad")){
            this.cssPath = this.form.path+this.form.options.style+"/doc.wcss";
            this._loadCss();

            this._queryLoaded();
            this._loadUserInterface();
            this._loadStyles();

            this._afterLoaded();
            this.fireEvent("postLoad");
            this.fireEvent("load");
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
                if (["copies", "secret", "priority", "attachment", "annotation", "copyto"].indexOf(name!=-1)){
                    return !!this.data[name];
                }
                return true;
            case "s":
                if (this.json[scriptItem] && this.json[scriptItem].code){
                    return !!this.form.Macro.exec(this.json[scriptItem].code, this);
                }
                return true;
        }
    },
    _createPage: function(){
	    debugger;
        var pageContentNode = this._createNewPage().getFirst();

        var html = '<div class="doc_block doc_layout_copiesSecretPriority">';
        if (this._getShow("copies", "copiesShow", "copiesShowScript")) html += '   <div class="doc_layout_copies"></div>';
        if (this._getShow("secret", "secretShow", "secretShowScript")) html += '   <div class="doc_layout_secret"></div>';
        if (this._getShow("priority", "priorityShow", "priorityShowScript")) html += '   <div class="doc_layout_priority"></div>';
        html += '</div>'
        if (this._getShow("redHeader", "redHeaderShow", "redHeaderShowScript")) html += '<div class="doc_block doc_layout_redHeader"></div>';

        if (this._getShow("signer", "signerShow", "signerShowScript")){
            this.json.fileup = true;
            html += '<table class="doc_block doc_layout_filenoup" width="100%" cellpadding="0" cellspacing="0" border="0">' +
                '<tr><td class="doc_layout_filenoup_fileno_td">';
            if (this._getShow("fileno", "filenoShow", "filenoShowScript")) html += '   <span>　</span><span class="doc_layout_filenoup_fileno"></span>';

            html += '   </td><td class="doc_layout_filenoup_signer_td">' +
                '       <table class="doc_layout_filenoup_signer_table" cellpadding="0" cellspacing="0" border="0">' +
                '           <tr><td class="doc_layout_filenoup_signerTitle_td">'+
                '               <span class="doc_layout_filenoup_signer"></span>' +
                '           </td><td class="doc_layout_filenoup_signerContent_td">' +
                '               <span class="doc_layout_filenoup_signerContent"></span><span>　</span>' +
                '           </td></tr>' +
                '       </table>' +
                '   </td></tr>' +
                '</table>';
        }else{
            if (this._getShow("fileno", "filenoShow", "filenoShowScript")) html += '<div class=\"doc_block doc_layout_fileno\"></div>';
        }
        html += "<div color=\"#ff0000\" class=\"doc_block doc_layout_redline\"></div>";
        if (this._getShow("subject", "subjectShow", "subjectShowScript")) html += "<div class=\"doc_block doc_layout_subject\"></div>";
        if (this._getShow("mainSend", "mainSendShow", "mainSendShowScript")) html += "<div class=\"doc_block doc_layout_mainSend\"></div>";
        html += "<div class=\"doc_block doc_layout_filetext\"></div>";

        if (this._getShow("attachment", "attachmentShow", "attachmentShowScript")){
            html += '<table class="doc_block doc_layout_attachment" width="100%" cellpadding="0" cellspacing="0" border="0">' +
                '   <tr><td class="doc_layout_attachment_title_td">' +
                '       <span>　　</span><span class="doc_layout_attachment_title"></span>' +
                '   </td><td class="doc_layout_attachment_content_td">' +
                '       <span class="doc_layout_attachment_content"></span>' +
                '   </td></tr>' +
                '</table>';
        };

        // html += '<table class="doc_block doc_layout_issuance" cellpadding="0" cellspacing="0" border="0">' +
        //     '   <tr><td class="doc_layout_issuanceUnit"></td></tr>' +
        //     '   <tr><td class="doc_layout_issuanceDate"></td></tr>' +
        //     '</table>'

        var showIssuanceUnit = this._getShow("issuanceUnit", "issuanceUnitShow", "issuanceUnitShowScript");
        var showIssuanceDate = this._getShow("issuanceDate", "issuanceDateShow", "issuanceDateShowScript");
        if (showIssuanceUnit || showIssuanceDate){
            html += '<div class="doc_block" style="overflow: hidden;"><table class="doc_layout_issuance" cellpadding="0" cellspacing="0" border="0">';
            if (showIssuanceUnit) html += '   <tr><td class="doc_layout_issuanceUnit"></td></tr>';
            if (showIssuanceDate) html += '   <tr><td class="doc_layout_issuanceDate"></td></tr>';
            html += '</table></div>';
        }
        if (this._getShow("annotation", "annotationShow", "annotationShowScript")) html += '<div class="doc_block doc_layout_annotation"></div>';

        html += '<table class="doc_block doc_layout_edition" width="100%" cellpadding="0" cellspacing="0" border="0">' +
            '   <tr><td class="doc_layout_edition_copyto">';

        if (this._getShow("copyto", "copytoShow", "copytoShowScript")){
            html +=  '  <table class="doc_layout_edition_copyto_table" align="center" cellpadding="0" cellspacing="0" border="0">' +
                '           <tr>' +
                '               <td class="doc_layout_edition_copyto_title"></td>' +
                '               <td class="doc_layout_edition_copyto_content"></td>' +
                '           </tr>' +
                '       </table>';
        }


        html += '   </td></tr><tr><td class="doc_layout_edition_issuance">'+
        '       <table class="doc_layout_edition_issuance_table" align="center" width="100%" cellpadding="0" cellspacing="0" border="0">' +
        '           <tr>';

        if (this._getShow("editionUnit", "editionUnitShow", "editionUnitShowScript")) html += '<td class="doc_layout_edition_issuance_unit"></td>';
        if (this._getShow("editionDate", "editionDateShow", "editionDateShowScript")) html += '<td class="doc_layout_edition_issuance_date"></td>';

        html += '   </tr>' +
            '   </table>' +
            '</td></tr>' +
            '</table>';

        //@todo

        //     '<table class="doc_block doc_layout_filenoup" width="100%" cellpadding="0" cellspacing="0" border="0">' +
        //     '   <tr><td class="doc_layout_filenoup_fileno_td">' +
        //     '       <span>　</span><span class="doc_layout_filenoup_fileno">浙移发〔2019〕20号</span>' +
        //     '   </td><td class="doc_layout_filenoup_signer_td">' +
        //     '       <table class="doc_layout_filenoup_signer_table" cellpadding="0" cellspacing="0" border="0">' +
        //     '           <tr><td class="doc_layout_filenoup_signerTitle_td">' +
        //     '               <span class="doc_layout_filenoup_signer">签发人：</span>' +
        //     '           </td><td class="doc_layout_filenoup_signerContent_td">' +
        //     '               <span class="doc_layout_filenoup_signerContent"></span><span>　</span>' +
        //     '           </td></tr>' +
        //     '       </table>' +
        //     '   </td></tr>' +
        //     '</table>'+
        //
        //
        //     "<div class=\"doc_block doc_layout_fileno\"></div>" +
        //     "<div color=\"#ff0000\" class=\"doc_block doc_layout_redline\"></div>" +
        //     "<div class=\"doc_block doc_layout_subject\"></div>" +
        //     "<div class=\"doc_block doc_layout_mainSend\">：</div>"+
        //     "<div class=\"doc_block doc_layout_filetext\"></div>" +
        //
        //     '<table class="doc_block doc_layout_attachment" width="100%" cellpadding="0" cellspacing="0" border="0">' +
        //     '   <tr><td class="doc_layout_attachment_title_td">' +
        //     '       <span>　　</span><span class="doc_layout_attachment_title"></span>' +
        //     '   </td><td class="doc_layout_attachment_content_td">' +
        //     '       <span class="doc_layout_attachment_content"></span>' +
        //     '   </td></tr>' +
        //     '</table>' +
        //     '<table class="doc_block doc_layout_issuance" cellpadding="0" cellspacing="0" border="0">' +
        //     '   <tr><td class="doc_layout_issuanceUnit"></td></tr>' +
        //     '   <tr><td class="doc_layout_issuanceDate"></td></tr>' +
        //     '</table>' +
        //     '<div class="doc_block doc_layout_annotation"></div>'+
        // // pageContentNode.set("html", html);
        // //
        // // pageContentNode = this._createNewPage().getFirst();
        // // html = '' +
        //     '<table class="doc_block doc_layout_edition" width="100%" cellpadding="0" cellspacing="0" border="0">' +
        //     '   <tr><td class="doc_layout_edition_copyto">' +
        //     '       <table class="doc_layout_edition_copyto_table" align="center" cellpadding="0" cellspacing="0" border="0">' +
        //     '           <tr>' +
        //     '               <td class="doc_layout_edition_copyto_title"></td>' +
        //     '               <td class="doc_layout_edition_copyto_content"></td>' +
        //     '           </tr>' +
        //     '       </table>' +
        //     '   </td></tr>' +
        //     '   <tr><td class="doc_layout_edition_issuance">' +
        //     '       <table class="doc_layout_edition_issuance_table" align="center" width="100%" cellpadding="0" cellspacing="0" border="0">' +
        //     '           <tr>' +
        //     '               <td class="doc_layout_edition_issuance_unit"></td>' +
        //     '               <td class="doc_layout_edition_issuance_date"></td>' +
        //     '           </tr>' +
        //     '       </table>' +
        //     '   </td></tr>' +
        //     '</table>';
        pageContentNode.set("html", html);
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
    },

    //红头
    _loadRedHeader: function(){
        this.layout_redHeader = this.contentNode.getElement(".doc_layout_redHeader");
        if (this.layout_redHeader) this.layout_redHeader.setStyles(this.css.doc_layout_redHeader);
    },
    //文号签发人（上行文）
    _loadFileNoUp: function(){
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
        node = this.contentNode.getElement(".doc_layout_filenoup_signerTitle_td").setStyles(this.css.doc_layout_filenoup_signerTitle_td);
        if (node) node.setStyles(this.css.doc_layout_filenoup_signerTitle_td);
        this.layout_signerTitle = this.contentNode.getElement(".doc_layout_filenoup_signer").setStyles(this.css.doc_layout_filenoup_signer);
        if (this.layout_signerTitle) this.layout_signerTitle.setStyles(this.css.doc_layout_filenoup_signer);
        node = this.contentNode.getElement(".doc_layout_filenoup_signerContent_td").setStyles(this.css.doc_layout_filenoup_signerContent_td);
        if (node) node.setStyles(this.css.doc_layout_filenoup_signerContent_td);

        this.layout_signer = this.contentNode.getElement(".doc_layout_filenoup_signerContent");
        if (this.layout_signer) this.layout_signer.setStyles(this.css.doc_layout_filenoup_signerContent);
    },

    //文号
    _loadFileNo: function(){
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
        this.layout_edition = this.contentNode.getElement(".doc_layout_edition");
        if (this.layout_edition) this.layout_edition.setStyles(this.css.doc_layout_edition);

        var node = this.contentNode.getElement(".doc_layout_edition_copyto");
        if (node) node.setStyles(this.css.doc_layout_edition_copyto);
        node = this.contentNode.getElement(".doc_layout_edition_copyto_table");
        if (node) node.setStyles(this.css.doc_layout_edition_copyto_table);

        this.layout_copytoTitle = this.contentNode.getElement(".doc_layout_edition_copyto_title");
        if (this.layout_copytoTitle) this.layout_copytoTitle.setStyles(this.css.doc_layout_edition_copyto_title);
        this.layout_copytoContent = this.contentNode.getElement(".doc_layout_edition_copyto_content");
        if (this.layout_copytoContent) this.layout_copytoContent.setStyles(this.css.doc_layout_edition_copyto_content);

        var issuance = this.contentNode.getElement(".doc_layout_edition_issuance");
        if (issuance) issuance.setStyles(this.css.doc_layout_edition_issuance);
        var issuance_table = this.contentNode.getElement(".doc_layout_edition_issuance_table");
        if (issuance_table) issuance_table.setStyles(this.css.doc_layout_edition_issuance_table);
        this.layout_edition_issuance_unit = this.contentNode.getElement(".doc_layout_edition_issuance_unit");
        if (this.layout_edition_issuance_unit) this.layout_edition_issuance_unit.setStyles(this.css.doc_layout_edition_issuance_unit);
        this.layout_edition_issuance_date = this.contentNode.getElement(".doc_layout_edition_issuance_date");
        if (this.layout_edition_issuance_date) this.layout_edition_issuance_date.setStyles(this.css.doc_layout_edition_issuance_date);
    },

    _loadPageLayout: function(){
	    this._loadCopiesSecretPriority();
	    this._loadRedHeader();

	    if (this.json.fileup){
            this._loadFileNoUp();
        }else{
            this._loadFileNo();
        }
        this._loadRedLine();
	    this._loadSubject();

        this._loadMainSend();
        this._loadFiletext();
        this._loadAttachment();

        this._loadIssuance();

        this._loadAnnotation();

        this._loadEdition();

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
	_loadUserInterface: function(){
		this.node.empty();
        this.node.setStyles(this.form.css.documentEditorNode);
        this.pages = [];

        this.allowEdit = this._isAllowEdit();

        this.toolNode = new Element("div", {"styles": this.css.doc_toolbar}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.doc_content}).inject(this.node);
        //this.contentNode.addEvent("resize", this._checkScale.bind(this));

        this._loadToolbars();
        this._loadFiletextPage();

        if (this.options.pageShow==="single"){
            this._singlePage();
        }else{
            this._doublePage();
        }
        //this._checkScale();
        //var pages = this.contentNode.getElements(".doc_layout_page");


        this.form.addEvent("afterSave", function(){
            this.resetData();
        }.bind(this));
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
	    debugger;
        offset = 0;
        if (this.pages.length){
            var pageSize = this.pages[0].getSize();
            var contentSize = this.node.getSize();
            var contentWidth = (offset) ? contentSize.x-20-offset : contentSize.x-20;
            if (contentWidth<pageSize.x){
                this.isScale = true;
                var scale = (contentWidth)/pageSize.x;
                this.scale = scale;

                var h = this.node.getSize().y;
                h = h - contentSize.y*(1-scale);

                this.node.setStyles({
                    "height":""+h+"px"
                });
                this.contentNode.setStyles({
                    "transform":"scale(1, "+scale+")",
                    "transform-origin": "0px 0px",
                    "overflow": "visible"
                });

                this._singlePage();
                this.pages.each(function(page){
                    page.setStyles({
                        "transform":"scale("+scale+", 1)",
                        "transform-origin": "0px 0px",
                        "overflow": "visible",
                        "margin-left": "10px"
                    });
                });

                if (this.doublePageAction) this.doublePageAction.hide();

            }
        }
    },

    _switchReadOrEdit: function(){
	    if (this.eiitMode){
	        this._readFiletext();
            this.toolbar.childrenButton[0].setText(MWF.xApplication.process.Xform.LP.editdoc);
            this.eiitMode = false;
        }else{
            this._editFiletext();
            this.toolbar.childrenButton[0].setText(MWF.xApplication.process.Xform.LP.editdocCompleted)
            this.eiitMode = true;
        }
    },
    _readFiletext: function(){
        this._returnScale();
        if (this.filetextEditor) this.filetextEditor.destroy();
        this.layout_filetext.setAttribute('contenteditable', false);
        this.data = this.getData();
        this._checkSplitPage(this.pages[0]);
        this._repage();
    },
    _editFiletext: function(){
	    this._returnScale();

        this.pages = [];
        this.contentNode.empty();
        this._createPage();
        this._loadPageLayout();

        // var docData = this._getBusinessData();
        // if (!docData) docData = this._getDefaultData();

        this.setData(this.data);

        this._checkScale();
        this.node.setStyles({
            "height":"auto"
        });

        this._createEditor();
    },
    _createEditor: function(){
        if (this.allowEdit){
            this.loadCkeditorFiletext(function(e){
                e.editor.focus();
                e.editor.getSelection().scrollIntoView();
            }.bind(this));
        }
    },

    _isAllowEdit:function(){
	    if (this.readonly) return false;
	    if (this.json.allowEdit=="n") return false;
        if (this.json.allowEdit=="s"){
            if (this.josn.allowEditScript && this.josn.allowEditScript.code){
                return !!this.form.Macro.exec(this.josn.allowEditScript.code, this);
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
            html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/icon/editdoc.png\" title=\""+MWF.xApplication.process.Xform.LP.editdoc+"\" MWFButtonAction=\"_switchReadOrEdit\" MWFButtonText=\""+MWF.xApplication.process.Xform.LP.editdoc+"\"></span>";
           //html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/icon/headerdoc.png\" title=\""+MWF.xApplication.process.Xform.LP.headerdoc+"\" MWFButtonAction=\"_redheaderDoc\" MWFButtonText=\""+MWF.xApplication.process.Xform.LP.headerdoc+"\"></span>";
        }

        this.toolNode.set("html", html);

        MWF.require("MWF.widget.Toolbar", function() {
            this.toolbar = new MWF.widget.Toolbar(this.toolNode, {"style": "documentEdit"}, this);
            this.toolbar.load();
        }.bind(this));

        this.doublePageAction = new Element("div", {"styles": this.css.doc_toolbar_doublePage, "text": MWF.xApplication.process.Xform.LP.doublePage}).inject(this.toolNode);
        this.doublePageAction.addEvent("click", function(){
            if (this.options.pageShow==="single"){
                this._doublePage();
            }else{
                this._singlePage();
            }
        }.bind(this));
    },
    _repage: function(delay){
        if (this.options.pageShow==="single"){
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
        var w = this.contentNode.getSize().x;
        var count = 1;
        var pageWidth = count * this.options.docPageFullWidth;
        var margin = (w-pageWidth)/(count+1);
        if (this.isScale){
            margin = "10"
        }
        this.pages.each(function(page, i){
            page.setStyles({
                "float": "left",
                "margin-left": ""+margin+"px"
            });
        });

        // this.pages.each(function(page){
        //     page.setStyle("float", "none");
        // });
        this.options.pageShow="single";
        this.doublePageAction.set("text", MWF.xApplication.process.Xform.LP.doublePage);
    },
    _doublePage: function(){
	    var w = this.contentNode.getSize().x;
	    var count = (w/this.options.docPageFullWidth).toInt();
        var pages = this.contentNode.getElements(".doc_layout_page");
        count = Math.min(pages.length, count);

	    var pageWidth = count * this.options.docPageFullWidth;
        var margin = (w-pageWidth)/(count+1);
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
        this.options.pageShow="double";
        this.doublePageAction.set("text", MWF.xApplication.process.Xform.LP.singlePage);
    },
    _getDefaultData: function(){
	    return this.json.defaultValue;
        //return Object.clone(MWF.xApplication.process.Xform.LP.documentEditor);
    },

    _loadFiletextPage: function(){
        this.data = this._getBusinessData();
        if (!this.data) this.data = this._getDefaultData();
        this._computeData(true);

        this._createPage();
        this._loadPageLayout();

        // this.data = this._getBusinessData();
        // if (!this.data) this.data = this._getDefaultData();

        this.setData(this.data);
        this._checkSplitPage(this.pages[0]);
        this._repage(true);
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
    },


    _getEditorConfig: function(){
        var editorConfig = {};
        editorConfig.toolbarGroups = [
            { name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
            { name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
            { name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
            { name: 'forms', groups: [ 'forms' ] },
            //{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
            { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
            { name: 'links', groups: [ 'links' ] },
            "/",
            { name: 'insert', groups: [ 'insert' ] },
            { name: 'styles', groups: [ 'styles' ] },
            { name: 'colors', groups: [ 'colors' ] },
            { name: 'tools', groups: [ 'tools' ] },
            { name: 'others', groups: [ 'others' ] },
            { name: 'about', groups: [ 'about' ] }
        ];
        editorConfig.removeButtons = 'NewPage,Templates,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,Bold,Italic,Underline,Strike,Subscript,Superscript,Blockquote,CreateDiv,BidiLtr,BidiRtl,Language,Link,Unlink,Anchor,Image,Flash,HorizontalRule,Smiley,SpecialChar,Iframe,Styles,Font,FontSize,TextColor,BGColor,ShowBlocks,About';
        editorConfig.enterMode = 3;
        editorConfig.extraPlugins = ['ecnet','mathjax'];
        editorConfig.removePlugins = ['magicline'];
        editorConfig.mathJaxLib = 'https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.4/MathJax.js?config=TeX-AMS_HTML';
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

    loadCkeditorFiletext: function(callback){
        if (this.layout_filetext){
            o2.load("ckeditor", function(){
                CKEDITOR.disableAutoInline = true;
                this.layout_filetext.setAttribute('contenteditable', true);
                this.filetextEditor = CKEDITOR.inline(this.layout_filetext, this._getEditorConfig());
                this.filetextEditor.on("instanceReady", function(e){
                    if (callback) callback(e);
                }.bind(this));
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
	    debugger;
        var field = this.form.all[this.json[dataItem]];
        if (field){
            field.addModuleEvent("change", function(){
                debugger;
                this._computeItemFieldData(name, dataItem);
                if (this.data[name]){
                    if (this[dom]){
                        if (dom=="layout_redHeader"){
                            this[dom].set("html", this.data[name]|| "");
                        }else{
                            this[dom].set("text", this.data[name]|| "");
                        }
                    }
                }
            }.bind(this));
        }
    },
    _computeItemFieldData: function(name, dataItem){
        var v = this.form.businessData.data[this.json[dataItem]];
        if (v){
            var t = o2.typeOf(v);
            switch (t) {
                case "string":
                    switch (name) {
                        case "issuanceDate":
                        case "editionDate":
                            var d = Date.parse(v);
                            this.data[name] = (d.isValid()) ? d.format("“％Y年％m月％d％日") : v;
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
                    if (strs.length){
                        switch (name) {
                            case "attachment":
                                this.data[name] = strs.map(function(n, i){ var j = i+1; return j+"、"+n}).join("<br>");
                                break;
                            case "issuanceDate":
                            case "editionDate":
                                var tmpStrs = strs.map(function(n, i){
                                    var d = Date.parse(n);
                                    return (d.isValid()) ? d.format("“％Y年％m月％d％日") : n;
                                });
                                this.data[name] = tmpStrs.join("，")
                                break;
                            case "mainSend":
                                debugger;
                                this.data[name] = strs.join("，") + "：";
                                break;
                            default:
                                this.data[name] = strs.join("，");
                        }
                    }
                    break;
                default:
                    this.data[name] = v.toString();
            }
        }
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
                    if (name=="attachment") this.data[name] = (typeOf(v)=="array") ? v.map(function(n, i){ var j = i+1; return j+"、"+n}).join("<br>") : v;
                    if (name=="issuanceDate" || name=="editionDate"){
                        var d = Date.parse(v);
                        this.data[name] = (d.isValid()) ? d.format("“％Y年％m月％d％日") : v;
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
        this._computeItemData("editionUnit", "editionUnitValueType", "editionUnitValueData", "editionUnitValueScript", ev, "layout_edition_issuance_unit");
        this._computeItemData("editionDate", "editionDateValueType", "editionDateValueData", "editionDateValueScript", ev, "layout_edition_issuance_date");
        debugger;
    },

    _loadValue: function(){
        var data = this._getBusinessData();
    },
    resetData: function(){
        this._computeData();

        this.pages = [];
        this.contentNode.empty();
        this._createPage();
        this._loadPageLayout();

        this.setData(this.data);
        this._checkSplitPage(this.pages[0]);

        this._repage();
    },
    getData: function(){
        if (this.eiitMode){
            if (this.layout_copies) this.data.copies = this.layout_copies.get("text");
            if (this.layout_secret) this.data.secret = this.layout_secret.get("text");
            if (this.layout_priority) this.data.priority = this.layout_priority.get("text");
            if (this.layout_redHeader) this.data.redHeader = this.layout_redHeader.get("html");
            if (this.layout_fileno) this.data.fileno = this.layout_fileno.get("text");
            if (this.layout_signerTitle) this.data.signerTitle = this.layout_signerTitle.get("text");
            if (this.layout_signer) this.data.signer = this.layout_signer.get("text");
            if (this.layout_subject) this.data.subject = this.layout_subject.get("text");
            if (this.layout_mainSend) this.data.mainSend = this.layout_mainSend.get("text");
            if (this.layout_filetext) this.data.filetext = this.layout_filetext.get("html");
            if (this.layout_signer) this.data.signer = this.layout_signer.get("text");
            if (this.layout_attachmentTitle) this.data.attachmentTitle = this.layout_attachmentTitle.get("text");
            if (this.layout_attachment) this.data.attachment = this.layout_attachment.get("html");
            if (this.layout_issuanceUnit) this.data.issuanceUnit = this.layout_issuanceUnit.get("text");
            if (this.layout_issuanceDate) this.data.issuanceDate = this.layout_issuanceDate.get("text");
            if (this.layout_annotation) this.data.annotation = this.layout_annotation.get("text");
            if (this.layout_copytoTitle) this.data.copytoTitle = this.layout_copytoTitle.get("text");
            if (this.layout_copytoContent) this.data.copyto = this.layout_copytoContent.get("text");
            if (this.layout_edition_issuance_unit) this.data.editionUnit = this.layout_edition_issuance_unit.get("text");
            if (this.layout_edition_issuance_date) this.data.editionDate = this.layout_edition_issuance_date.get("text");
        }
        return this.data;
    },
    setData: function(data){
        if (data){
            this.data = data;
            this._setBusinessData(data);
            if (this.layout_copies) this.layout_copies.set("text", data.copies || " ");
            if (this.layout_secret) this.layout_secret.set("text", data.secret || " ");
            if (this.layout_priority) this.layout_priority.set("text", data.priority || " ");
            if (this.layout_redHeader) this.layout_redHeader.set("html", data.redHeader || "");
            if (this.layout_fileno) this.layout_fileno.set("text", data.fileno || " ");
            if (this.layout_signerTitle) this.layout_signerTitle.set("text", data.signerTitle || " ");
            if (this.layout_signer) this.layout_signer.set("text", data.signer || " ");
            if (this.layout_subject) this.layout_subject.set("text", data.subject || " ");
            if (this.layout_mainSend) this.layout_mainSend.set("text", data.mainSend || " ");
            if (this.layout_filetext) this.layout_filetext.set("html", data.filetext || "");
            if (this.layout_signer) this.layout_signer.set("text", data.signer || "");
            if (this.layout_attachmentTitle) this.layout_attachmentTitle.set("text", data.attachmentTitle || " ");
            if (this.layout_attachment) this.layout_attachment.set("html", data.attachment || " ");
            if (this.layout_issuanceUnit) this.layout_issuanceUnit.set("text", data.issuanceUnit || " ");
            if (this.layout_issuanceDate) this.layout_issuanceDate.set("text", data.issuanceDate || " ");
            if (this.layout_annotation) this.layout_annotation.set("text", data.annotation || " ");
            if (this.layout_copytoTitle) this.layout_copytoTitle.set("text", data.copytoTitle || " ");
            if (this.layout_copytoContent) this.layout_copytoContent.set("text", data.copyto || " ");
            if (this.layout_edition_issuance_unit) this.layout_edition_issuance_unit.set("text", data.editionUnit || " ");
            if (this.layout_edition_issuance_date) this.layout_edition_issuance_date.set("text", data.editionDate || " ");
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
    }
}); 