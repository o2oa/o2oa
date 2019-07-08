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
    _getShow(name, typeItem, scriptItem){
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
        var pageContentNode = this._createNewPage().getFirst();

        var html = '<div class="doc_block doc_layout_copiesSecretPriority">';
        if (this._getShow("copies", "copiesShow", "copiesShowScript")) html += '   <div class="doc_layout_copies"></div>';
        if (this._getShow("secret", "secretShow", "secretShowScript")) html += '   <div class="doc_layout_secret"></div>';
        if (this._getShow("priority", "priorityShow", "priorityShowScript")) html += '   <div class="doc_layout_priority"></div>';
        html += '</div>'

        if (this._getShow("redHeader", "redHeaderShow", "redHeaderShowScript")) html += '<div class="doc_block doc_layout_redHeader"></div>';
        //@todo

            // '<table class="doc_block doc_layout_filenoup" width="100%" cellpadding="0" cellspacing="0" border="0">' +
            // '   <tr><td class="doc_layout_filenoup_fileno_td">' +
            // '       <span>　</span><span class="doc_layout_filenoup_fileno">浙移发〔2019〕20号</span>' +
            // '   </td><td class="doc_layout_filenoup_signer_td">' +
            // '       <table class="doc_layout_filenoup_signer_table" cellpadding="0" cellspacing="0" border="0">' +
            // '           <tr><td class="doc_layout_filenoup_signerTitle_td">' +
            // '               <span class="doc_layout_filenoup_signer">签发人：</span>' +
            // '           </td><td class="doc_layout_filenoup_signerContent_td">' +
            // '               <span class="doc_layout_filenoup_signerContent"></span><span>　</span>' +
            // '           </td></tr>' +
            // '       </table>' +
            // '   </td></tr>' +
            // '</table>'+
            "<div class=\"doc_block doc_layout_fileno\"></div>" +
            "<div color=\"#ff0000\" class=\"doc_block doc_layout_redline\"></div>" +
            "<div class=\"doc_block doc_layout_subject\"></div>" +
            "<div class=\"doc_block doc_layout_mainSend\">：</div>"+
            "<div class=\"doc_block doc_layout_filetext\"></div>" +

            '<table class="doc_block doc_layout_attachment" width="100%" cellpadding="0" cellspacing="0" border="0">' +
            '   <tr><td class="doc_layout_attachment_title_td">' +
            '       <span>　　</span><span class="doc_layout_attachment_title"></span>' +
            '   </td><td class="doc_layout_attachment_content_td">' +
            '       <span class="doc_layout_attachment_content"></span>' +
            '   </td></tr>' +
            '</table>' +
            '<table class="doc_block doc_layout_issuance" cellpadding="0" cellspacing="0" border="0">' +
            '   <tr><td class="doc_layout_issuanceUnit"></td></tr>' +
            '   <tr><td class="doc_layout_issuanceDate"></td></tr>' +
            '</table>' +
            '<div class="doc_block doc_layout_annotation"></div>'+
        // pageContentNode.set("html", html);
        //
        // pageContentNode = this._createNewPage().getFirst();
        // html = '' +
            '<table class="doc_block doc_layout_edition" width="100%" cellpadding="0" cellspacing="0" border="0">' +
            '   <tr><td class="doc_layout_edition_copyto">' +
            '       <table class="doc_layout_edition_copyto_table" align="center" cellpadding="0" cellspacing="0" border="0">' +
            '           <tr>' +
            '               <td class="doc_layout_edition_copyto_title"></td>' +
            '               <td class="doc_layout_edition_copyto_content"></td>' +
            '           </tr>' +
            '       </table>' +
            '   </td></tr>' +
            '   <tr><td class="doc_layout_edition_issuance">' +
            '       <table class="doc_layout_edition_issuance_table" align="center" width="100%" cellpadding="0" cellspacing="0" border="0">' +
            '           <tr>' +
            '               <td class="doc_layout_edition_issuance_unit"></td>' +
            '               <td class="doc_layout_edition_issuance_date"></td>' +
            '           </tr>' +
            '       </table>' +
            '   </td></tr>' +
            '</table>';
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
        if (this.layout_fileNoUpTable) this.layout_redHeader.setStyles(this.css.doc_layout_filenoup);

        var td = this.contentNode.getElement(".doc_layout_filenoup_fileno_td");
        if (td) this.layout_redHeader.setStyles(this.css.doc_layout_filenoup_fileno_td);

        this.layout_fileno = this.contentNode.getElement(".doc_layout_filenoup_fileno");
        if (this.layout_fileno) this.layout_fileno.setStyles(this.css.doc_layout_filenoup_fileno);

        td = this.contentNode.getElement(".doc_layout_filenoup_signer_td");
        if (td) this.layout_redHeader.setStyles(this.css.doc_layout_filenoup_signer_td);

        var node = this.contentNode.getElement(".doc_layout_filenoup_signer_table");
        if (node) node.setStyles(this.css.doc_layout_filenoup_signer_table);
        node = this.contentNode.getElement(".doc_layout_filenoup_signerTitle_td").setStyles(this.css.doc_layout_filenoup_signerTitle_td);
        if (node) node.setStyles(this.css.doc_layout_filenoup_signerTitle_td);
        this.layout_signerTitle = this.contentNode.getElement(".doc_layout_filenoup_signer").setStyles(this.css.doc_layout_filenoup_signer);
        if (node) node.setStyles(this.css.doc_layout_filenoup_signer);
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

        this.layout_issuanceTable.setStyles(this.css.doc_layout_issuance);
        this.layout_issuanceUnit.setStyles(this.css.doc_layout_issuanceUnit);
        this.layout_issuanceDate.setStyles(this.css.doc_layout_issuanceDate);
    },

    //附注
    _loadAnnotation: function(){
        this.layout_annotation = this.contentNode.getElement(".doc_layout_annotation");
        this.layout_annotation.setStyles(this.css.doc_layout_annotation);
    },

    //版记
    _loadEdition: function(){
        this.layout_edition = this.contentNode.getElement(".doc_layout_edition").setStyles(this.css.doc_layout_edition);
        this.contentNode.getElement(".doc_layout_edition_copyto").setStyles(this.css.doc_layout_edition_copyto);
        this.contentNode.getElement(".doc_layout_edition_copyto_table").setStyles(this.css.doc_layout_edition_copyto_table);
        this.layout_copytoTitle = this.contentNode.getElement(".doc_layout_edition_copyto_title").setStyles(this.css.doc_layout_edition_copyto_title);
        this.layout_copytoContent = this.contentNode.getElement(".doc_layout_edition_copyto_content").setStyles(this.css.doc_layout_edition_copyto_content);

        var issuance = this.contentNode.getElement(".doc_layout_edition_issuance").setStyles(this.css.doc_layout_edition_issuance);
        var issuance_table = this.contentNode.getElement(".doc_layout_edition_issuance_table").setStyles(this.css.doc_layout_edition_issuance_table);
        this.layout_edition_issuance_unit = this.contentNode.getElement(".doc_layout_edition_issuance_unit").setStyles(this.css.doc_layout_edition_issuance_unit);
        this.layout_edition_issuance_date = this.contentNode.getElement(".doc_layout_edition_issuance_date").setStyles(this.css.doc_layout_edition_issuance_date);
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

        this.toolNode = new Element("div", {"styles": this.css.doc_toolbar}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.doc_content}).inject(this.node);

        this._loadToolbars();
        this._loadFiletextPage();

        if (this.options.pageShow==="single"){
            this._singlePage();
        }else{
            this._doublePage();
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
        if (this.filetextEditor) this.filetextEditor.destroy();
        this.layout_filetext.setAttribute('contenteditable', false);
        this.data = this.getData();
        this._checkSplitPage(this.pages[0]);
        this._repage();
    },
    _editFiletext: function(){
        this.pages = [];
        this.contentNode.empty();
        this._createPage();
        this._loadPageLayout();

        // var docData = this._getBusinessData();
        // if (!docData) docData = this._getDefaultData();

        this.setData(this.data);

        this.loadCkeditorFiletext(function(e){
            e.editor.focus();
            e.editor.getSelection().scrollIntoView();
        }.bind(this));
    },


    _loadToolbars: function(){
	    var html ="";
        if (!this.readonly) html += "<span MWFnodetype=\"MWFToolBarButton\" MWFButtonImage=\"/x_component_process_Xform/$Form/default/icon/editdoc.png\" title=\""+MWF.xApplication.process.Xform.LP.editdoc+"\" MWFButtonAction=\"_switchReadOrEdit\" MWFButtonText=\""+MWF.xApplication.process.Xform.LP.editdoc+"\"></span>";
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
    _repage: function(){
        if (this.options.pageShow==="single"){
            this._singlePage();
        }else{
            this._doublePage();
        }
    },
    _singlePage: function(){
        var w = this.contentNode.getSize().x;
        var count = 1;
        var pageWidth = count * this.options.docPageFullWidth;
        var margin = (w-pageWidth)/(count+1);
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
        return Object.clone(MWF.xApplication.process.Xform.LP.documentEditor);
    },

    _loadFiletextPage: function(){
        this._computeData();

        this._createPage();
        this._loadPageLayout();

        // this.data = this._getBusinessData();
        // if (!this.data) this.data = this._getDefaultData();

        this.setData(this.data);
        this._checkSplitPage(this.pages[0]);
        this._repage();
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
                var contentNode = pageNode.getFirst();
                if (contentNode.getSize().y>this.options.docPageHeight){
                    this._splitPage(pageNode);
                }
            }
        }
    },

    // _checkStartBoundary: function(editor){
    //     var sel = editor.getSelection();
    //     var range = sel.getRanges(true)[0];
    //     if (range.collapsed && range.startOffset==0){
    //         var o = range.getBoundaryNodes();
    //         var n = o.startNode.getPrevious();
    //         if (n && n.textContent) return false;
    //
    //         var els = range.startPath().elements;
    //         for (var i=0; i<els.length; i++){
    //             if (els[i].getPrevious()) return false;
    //         }
    //         return true;
    //     }
    //     return false;
    // },
    // _moveToPrevEditor: function(editor){
    //     var filetextNode = editor.container.$;
    //     var pageNode = filetextNode.getParent(".doc_layout_page");
    //     var prevPageNode = pageNode.getPrevious();
    //     if (prevPageNode && prevPageNode.hasClass("doc_layout_page")){
    //         var node = prevPageNode.getElement(".doc_layout_filetext");
    //         if (node){
    //             var prevEditor = node.retrieve("editor");
    //             if (prevEditor){
    //                 prevEditor.focus();
    //                 var range = prevEditor.createRange();
    //                 range.moveToPosition(prevEditor.editable(), CKEDITOR.POSITION_BEFORE_END);
    //                 range.select();
    //                 range.scrollIntoView();
    //             }
    //         }
    //     }
    // },
    // _backFromNextEditorWord:function(lastNode, nextNode, contentNode){
    //     var text = nextNode.textContent;
    //     var len = text.length;
    //     var left = text.substring(0, 1);
    //     var right = text.substring(1, len);
    //     var oldContent = lastNode.textContent;
    //     lastNode.textContent = oldContent+left;
    //
    //     if (contentNode.getSize().y<=this.options.docPageHeight){
    //         nextNode.textContent = right;
    //         if (right) this._backFromNextEditorWord(lastNode, nextNode, contentNode);
    //     }else{
    //         lastNode.textContent = oldContent;
    //     }
    // },
    // _checkBackFromNextEditor: function(editor){
    //     var filetextNode = editor.container.$;
    //     var pageNode = filetextNode.getParent(".doc_layout_page");
    //     var contentNode = pageNode.getFirst();
    //     var nextPageNode = pageNode.getNext();
    //
    //     if (nextPageNode && nextPageNode.hasClass("doc_layout_page")){
    //         var node = nextPageNode.getElement(".doc_layout_filetext");
    //         if (node){
    //             var nextEditor = node.retrieve("editor");
    //             if (nextEditor){
    //                 var range = editor.createRange();
    //                 range.moveToPosition(editor.editable(), CKEDITOR.POSITION_BEFORE_END);
    //                 var boundaryNodes = range.getBoundaryNodes();
    //                 var id = boundaryNodes.endNode.getParent().$.get("data-pagepart");
    //
    //                 var nextRange = nextEditor.createRange();
    //                 nextEditor.moveToPosition(nextEditor.editable(), CKEDITOR.POSITION_AFTER_START);
    //                 var nextBoundaryNodes = nextEditor.getBoundaryNodes();
    //                 var nextId = nextBoundaryNodes.startNode.getParent().$.get("data-pagepart");
    //
    //                 if (id==nextId){
    //                     var lastNode = boundaryNodes.endNode.$.lastChild;
    //                     var nextNode = nextBoundaryNodes.startNode.$.firstChild;
    //                     if (!lastNode) lastNode = boundaryNodes.endNode.$.appendChild(document.createTextNode(""));
    //                     if (lastNode && nextNode) this._backFromNextEditorWord(lastNode, nextNode, contentNode);
    //                     if (!nextNode.textContent) if (nextNode.parentNode) {
    //                         nextNode.parentNode.removeChild(nextNode);
    //                     }
    //                     if ()
    //
    //
    //                 }else{
    //
    //                 }
    //             }
    //         }
    //     }
    // },

    // _checkMergePage: function(pageNode){
    //     var contentNode = pageNode.getFirst();
    //     if (contentNode.getSize().y<=this.options.docPageHeight){
    //         var i = pageNode.get("data-pagecount").toInt();
    //         if (i && this.pages.length-1>=i){
    //             var nextPageNode = this.pages[i];
    //             this._mergepage(pageNode, nextPageNode);
    //         }
    //     }
    //     var n = pageNode.get("data-pagecount").toInt();
    //     if (n && this.pages.length-1>=n){
    //         this._checkMergePage(this.pages[n]);
    //     }
    // },
    // _checkPageHeight: function(pageNode){
    //     var contentNode = pageNode.getFirst();
    //     return (contentNode.getSize().y<=this.options.docPageHeight);
    // },

    // _mergeFiletextNodeOneWord:function(nextNode, prevNode, contentNode){
    //     var text = nextNode.textContent;
    //     var len = text.length;
    //     var left = text.substring(0, 1);
    //     var right = text.substring(1, len);
    //     var oldContent = prevNode.textContent;
    //     prevNode.textContent = oldContent+left;
    //
    //     if (contentNode.getSize().y<=this.options.docPageHeight){
    //         nextNode.textContent = right;
    //         if (right) return this._mergeFiletextNodeOneWord(nextNode, prevNode, contentNode);
    //         return true;
    //     }else{
    //         prevNode.textContent = oldContent;
    //         return false;
    //     }
    // },

    // _getLastTextNode:function(node){
	//     var n = node.lastChild;
	//     while (n && n.nodeType!==Node.TEXT_NODE){
    //         var tmp = this._getLastTextNode(n);
    //         if (tmp) return tmp;
	//         n = n.previousSibling;
    //     }
    //     return n;
    // },
    // _mergeFiletext: function(node, prevPageNode, prevFiletextNode, pageNode, continueMerge){
    //     var contentNode = pageNode.getFirst();
    //     var fnode = node.firstChild;
    //     if (!fnode){
    //         if (node.parentNode) node.parentNode.removeChild(node);
    //     }else{
    //         while (this._checkPageHeight(pageNode) && fnode) {
    //             var tmpnode = fnode.nextSibling;
    //             var nodeType = fnode.nodeType;
    //             if (!prevPageNode) prevPageNode = prevFiletextNode;
    //
    //             if (nodeType == Node.ELEMENT_NODE) {
    //                 if (fnode.tagName == "table") {
    //                     var tNode = fnode.clone().inject(prevPageNode);
    //                     if (this._checkPageHeight(pageNode)){
    //                         fnode.destroy();
    //                         this._mergepage(pageNode, nextPageNode);
    //                     }else{
    //                         tNode.destroy();
    //                     }
    //                 } else if (fnode.tagName == "BR") {
    //                     if (fnode.parentNode) fnode.parentNode.removeChild(fnode);
    //                 } else {
    //                     var id = fnode.get("data-pagePart");
    //                     if (!id){
    //                         id = (new o2.widget.UUID()).toString();
    //                         fnode.set("data-pagePart", id);
    //                     }
    //                     var tmpNode = prevPageNode.getLast();
    //                     if (tmpNode && tmpNode.get("data-pagePart")==id){
    //                         prevPageNode = tmpNode;
    //                     }else{
    //                         prevPageNode = fnode.clone(false).inject(prevPageNode);
    //                     }
    //                     //var subnode = lnode.getLast();
    //                     continueMerge = this._mergeFiletext(fnode, prevPageNode, prevFiletextNode, pageNode, continueMerge);
    //                     prevPageNode = prevPageNode.getParent();
    //                 }
    //             } else if (nodeType == Node.TEXT_NODE) {
    //                 var prevPageTextNode = this._getLastTextNode(prevPageNode);
    //                 if (!prevPageTextNode) prevPageTextNode = prevPageNode.appendChild(document.createTextNode(""));
    //
    //                 var flag = this._mergeFiletextNodeOneWord(fnode, prevPageTextNode, contentNode);
    //
    //                 if (!fnode.textContent) if (fnode.parentNode) fnode.parentNode.removeChild(fnode);
    //                 if (!prevPageTextNode.textContent) if (prevPageTextNode.parentNode) prevPageTextNode.parentNode.removeChild(prevPageTextNode);
    //
    //                 if (flag){
    //                     while ((contentNode.getSize().y <= this.options.docPageHeight) && continueMerge) {
    //                         continueMerge = this._mergeFiletext(node, prevPageNode, prevFiletextNode, pageNode, continueMerge);
    //                     }
    //                 } else {
    //                     return false;
    //                 }
    //             } else {
    //                 //lnode.remove();
    //                 if (fnode.parentNode) fnode.parentNode.removeChild(fnode);
    //             }
    //
    //             if (continueMerge) {
    //                 fnode = tmpnode;
    //             }else{
    //                 fnode = null;
    //             }
    //         }
    //         if (!node.lastChild) if (node.parentNode) node.parentNode.removeChild(node); //node.remove();
    //     }
    //     return true;
    //     //this._checkSplitPage(pageNode);
    // },
    // _mergepage: function(pageNode, nextPageNode){
    //     var contentNode = pageNode.getFirst();
    //     var nextContentNode = nextPageNode.getFirst();
    //     var nextBlockNodes = nextPageNode.getElements(".doc_block");
    //
    //     if (nextBlockNodes.length){
    //         var nextBlockNode = nextBlockNodes[0];
    //         if (nextBlockNode.hasClass("doc_layout_filetext")){
    //             var filetextNode = pageNode.getElement(".doc_layout_filetext");
    //             if (!filetextNode){
    //                 filetextNode = new Element("div.doc_layout_filetext").inject(contentNode);
    //                 filetextNode.setAttribute('contenteditable', true);
    //             }
    //             if (!filetextNode.hasClass("doc_block"))filetextNode.addClass("doc_block");
    //             var nextEditor = filetextNode.retrieve("editor");
    //
    //             var nextNode = nextBlockNode;
    //             var nextPageNode = filetextNode;
    //             this._mergeFiletext(nextNode, filetextNode, filetextNode, pageNode, true);
    //
    //         }else{
    //             var tmpNode = nextBlockNode.clone().inject(contentNode);
    //             if (this._checkPageHeight(pageNode)){
    //                 nextBlockNode.destroy();
    //                 this._mergepage(pageNode, nextPageNode);
    //             }else{
    //                 tmpNode.destroy();
    //             }
    //         }
    //     }
    // },


    loadCkeditorFiletext: function(callback){
	    debugger;
        if (this.layout_filetext){
            o2.load("ckeditor", function(){
                CKEDITOR.disableAutoInline = true;
                //if (!this.layout_filetext.retrieve("editor", null)) {
                    this.layout_filetext.setAttribute('contenteditable', true);
                    this.filetextEditor = CKEDITOR.inline(this.layout_filetext, this._getEditorConfig());
                this.filetextEditor.on("instanceReady", function(e){
                    if (callback) callback(e);
                }.bind(this));

                    //this.layout_filetext.store("editor", this.filetextEditor);

                //}
            }.bind(this));
        }


        //
        // this.layout_filetexts = this.contentNode.getElements(".doc_layout_filetext");
	    // if (this.layout_filetexts && this.layout_filetexts.length){
        //     this.filetextEditors = [];
        //     o2.load("ckeditor", function(){
        //         CKEDITOR.disableAutoInline = true;
        //         this.layout_filetexts.each(function(layout_filetext){
        //
        //             if (!layout_filetext.retrieve("editor")){
        //                 layout_filetext.setAttribute('contenteditable', true);
        //                 var filetextEditor = CKEDITOR.inline(layout_filetext, this._getEditorConfig());
        //                 layout_filetext.store("editor", filetextEditor);
        //                 this.filetextEditors.push(filetextEditor);
        //                 // filetextEditor.on( 'blur', function(e) {
        //                 //     // var filetextNode = e.editor.container.$;
        //                 //     // var pageNode = filetextNode.getParent(".doc_layout_page");
        //                 //     // this._checkSplitPage(pageNode);
        //                 //     // this._repage();
        //                 //     // //debugger;
        //                 //     // this.loadCkeditorFiletext();
        //                 // }.bind(this));
        //                 //
        //                 // filetextEditor.on( 'key', function(e) {
        //                 //     if (e.data.keyCode == 8){
        //                 //         if (this._checkStartBoundary(e.editor)) this._moveToPrevEditor(e.editor);
        //                 //         var pageNode =  e.editor.container.$.getParent(".doc_layout_page");
        //                 //         debugger;
        //                 //         this._checkMergePage(pageNode);
        //                 //
        //                 //         if (!e.editor.getData()){
        //                 //             e.cancel();
        //                 //             var filetextNode = e.editor.container.$;
        //                 //             e.editor.destroy();
        //                 //             filetextNode.destroy();
        //                 //         }else{
        //                 //
        //                 //         }
        //                 //     }
        //                 //     debugger;
        //                 // }.bind(this));
        //             }
        //
        //         }.bind(this));
        //    }.bind(this));
	    // }
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

    _computeItemData: function(name, typeItem, dataItem, scriptItem){
        switch (this.json[typeItem]) {
            case "data":
                if (this.form.businessData.data[this.json[dataItem]]!=undefined) this.data[name] = this.form.businessData.data[this.json[dataItem]];
                break;
            case "script":
                if (this.json[scriptItem] && this.json[scriptItem].code){
                    var v = this.form.Macro.exec(this.json[scriptItem].code, this);

                    if (name=="attachment") this.data[name] = (typeOf(v)=="array") ? v.join("<br/>") : v;
                    if (name=="issuanceDate" || name=="editionDate"){
                        var d = Date.parse(v);
                        this.data[name] = (d.isValid()) ? d.format("“％Y年％m月％d％日") : v;
                    }

                }
                break;
        }
    },
    _computeData: function(){
        this.data = this._getBusinessData();
        if (!this.data) this.data = this._getDefaultData();

        this._computeItemData("copies", "copiesValueType", "copiesValueData", "copiesValueScript");
        this._computeItemData("secret", "secretValueType", "secretValueData", "secretValueScript");
        this._computeItemData("priority", "priorityValueType", "priorityValueData", "priorityValueScript");
        this._computeItemData("redHeader", "redHeaderValueType", "redHeaderValueData", "redHeaderValueScript");
        this._computeItemData("fileno", "filenoValueType", "filenoValueData", "filenoValueScript");
        this._computeItemData("signer", "signerValueType", "signerValueData", "signerValueScript");
        this._computeItemData("subject", "subjectValueType", "subjectValueData", "subjectValueScript");
        this._computeItemData("mainSend", "mainSendValueType", "mainSendValueData", "mainSendValueScript");
        this._computeItemData("attachment", "attachmentValueType", "attachmentValueData", "attachmentValueScript");
        this._computeItemData("issuanceUnit", "issuanceUnitValueType", "issuanceUnitValueData", "issuanceUnitValueScript");
        this._computeItemData("issuanceDate", "issuanceDateValueType", "issuanceDateValueData", "issuanceDateValueScript");
        this._computeItemData("annotation", "annotationValueType", "annotationValueData", "annotationValueScript");
        this._computeItemData("copyto", "copytoValueType", "copytoValueData", "copytoValueScript");
        this._computeItemData("editionUnit", "editionUnitValueType", "editionUnitValueData", "editionUnitValueScript");
        this._computeItemData("editionDate", "editionDateValueType", "editionDateValueData", "editionDateValueScript");
    },

    _loadValue: function(){
        var data = this._getBusinessData();
    },
    resetData: function(){
        this.setData(this._getBusinessData());
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


    // loadCkeditor: function(config, pageNode){
    //     COMMON.AjaxModule.loadDom("ckeditor", function(){
    //         CKEDITOR.disableAutoInline = true;
    //         var editorDiv = new Element("div").inject(this.node);
    //         //var editorDiv = pageNode;
    //         var htmlData = this._getBusinessData();
    //         if (htmlData){
    //             editorDiv.set("html", htmlData);
    //         }else if (this.json.templateCode){
    //             editorDiv.set("html", this.json.templateCode);
    //         }
    //         var height = this.node.getSize().y;
    //         var editorConfig = config || {};
    //
    //         if (this.form.json.mode==="Mobile"){
    //             if (!editorConfig.toolbar && !editorConfig.toolbarGroups){
    //                 editorConfig.toolbar = [
    //                     { name: 'paragraph',   items: [ 'Bold', 'Italic', "-" , 'TextColor', "BGColor", 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', "-", 'Undo', 'Redo' ] },
    //                     { name: 'basicstyles', items: [ 'Styles', 'FontSize']}
    //                 ];
    //             }
    //         }
    //         var editorConfig = {};
    //         editorConfig.localImageMaxWidth = 800;
    //         editorConfig.reference = this.form.businessData.work.job;
    //         editorConfig.referenceType = "processPlatformJob";
    //
    //         editorConfig.toolbarGroups = [
    //             { name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
    //             { name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
    //             { name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
    //             { name: 'forms', groups: [ 'forms' ] },
    //             { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
    //             { name: 'insert', groups: [ 'insert' ] },
    //             { name: 'tools', groups: [ 'tools' ] },
    //             '/',
    //             { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
    //             { name: 'links', groups: [ 'links' ] },
    //             '/',
    //             { name: 'styles', groups: [ 'styles' ] },
    //             { name: 'colors', groups: [ 'colors' ] },
    //             { name: 'others', groups: [ 'others' ] },
    //             { name: 'about', groups: [ 'about' ] }
    //         ];
    //         editorConfig.enterMode = 3;
    //         editorConfig.removeButtons = 'ShowBlocks,Templates,Scayt,Form,Bold,Italic,Underline,Strike,Subscript,Superscript,CopyFormatting,RemoveFormat,Indent,Outdent,Blockquote,CreateDiv,BidiLtr,BidiRtl,Language,Link,Unlink,Anchor,Flash,HorizontalRule,Smiley,SpecialChar,Iframe,PageBreak,Styles,Format,Font,FontSize,TextColor,BGColor,About,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField';
    //         // editorConfig.bodyClass = "document-editor";
    //         editorConfig.contentsCss = [ '/x_component_process_Xform/$Form/doc.css' ];
    //         //editorConfig.extraPlugins = ['ecnet','doc_page', 'doc_copiesSecretPriority','doc_fileno','doc_redHeader','doc_subject','doc_mainSend', 'doc_file', 'doc_filenoup', 'doc_attachment'];
    //         editorConfig.extraPlugins = ['ecnet','doc_page'];
    //         editorConfig.removePlugins = ['magicline'];
    //         // editorConfig.allowedContent = "div(o2_editorPlugin_doc_Page,o2_editorPlugin_doc_Page_content," +
    //         //     "o2_editorPlugin_redHeader,o2_editorPlugin_redHeader_content," +
    //         //     "o2_editorPlugin_doc_copiesSecretPriority,o2_editorPlugin_doc_copies,o2_editorPlugin_doc_secret,o2_editorPlugin_doc_priority," +
    //         //     "o2_editorPlugin_doc_fileno,o2_editorPlugin_doc_subject,o2_editorPlugin_doc_mainSend,o2_editorPlugin_doc_file," +
    //         //     "o2_editorPlugin_doc_redline);" +
    //         //     "table(o2_editorPlugin_doc_filenoup,o2_editorPlugin_doc_filenoup_signer_table,o2_editorPlugin_doc_attachment)[width,cellpadding,cellspacing,border];" +
    //         //     "tr td(o2_editorPlugin_doc_filenoup_td,o2_editorPlugin_doc_filenoup_td_signer,o2_editorPlugin_doc_filenoup_signer_td,o2_editorPlugin_doc_filenoup_signerContent_td," +
    //         //     "o2_editorPlugin_doc_attachment_title_td,o2_editorPlugin_doc_attachment_title_content_td);" +
    //         //     "span(o2_editorPlugin_doc_filenoup_fileno,o2_editorPlugin_doc_filenoup_signer,o2_editorPlugin_doc_filenoup_signerContent,o2_editorPlugin_doc_attachment_title,o2_editorPlugin_doc_attachment_content)";
    //
    //         editorConfig.allowedContent = true;
    //
    //         editorConfig.disableAutoInline = false;
    //         //this.editor = CKEDITOR.replace(editorDiv, editorConfig);
    //         //this.editor = CKEDITOR.inline(editorDiv);
    //
    //         //this.editor.on("key",function(e){
    //             // var sel = e.editor.getSelection();
    //             // var h = e.editor.getSelectedHtml().getHtml();
    //             // if (h.indexOf("o2_editorPlugin_redHeader")!=-1 ||
    //             //     h.indexOf("o2_editorPlugin_doc_Page")!=-1){
    //             //     e.cancel();
    //             // }
    //             // debugger;
    //             // var el = sel.getSelectedElement();
    //         //});
    //         var html = "<div class=\"o2_editorPlugin_doc_Page\">" +
    //             "<div class=\"o2_editorPlugin_doc_Page_content\">" +
    //
    //             '<div class="o2_editorPlugin_doc_copiesSecretPriority">' +
    //             '   <div class="o2_editorPlugin_doc_copies">000001</div>' +
    //             '   <div class="o2_editorPlugin_doc_secret">机密★1年</div>' +
    //             '   <div class="o2_editorPlugin_doc_priority">特急</div>' +
    //             '</div>' +
    //
    //             "<div class=\"o2_editorPlugin_redHeader\"><div class=\"o2_editorPlugin_redHeader_content\">文件红头</div></div>" +
    //
    //             // '<table class="o2_editorPlugin_doc_filenoup" width="100%" cellpadding="0" cellspacing="0" border="0">' +
    //             // '   <tr><td class="o2_editorPlugin_doc_filenoup_td">' +
    //             // '       <span>　</span><span class="o2_editorPlugin_doc_filenoup_fileno">浙移发〔2019〕20号</span>' +
    //             // '   </td><td class="o2_editorPlugin_doc_filenoup_td_signer">' +
    //             // '       <table class="o2_editorPlugin_doc_filenoup_signer_table" cellpadding="0" cellspacing="0" border="0">' +
    //             // '           <tr><td class="o2_editorPlugin_doc_filenoup_signer_td">' +
    //             // '               <span class="o2_editorPlugin_doc_filenoup_signer">签发人：</span>' +
    //             // '           </td><td class="o2_editorPlugin_doc_filenoup_signerContent_td">' +
    //             // '               <span class="o2_editorPlugin_doc_filenoup_signerContent">蔡志煌　谢玲巧　张傻托　王雄哥</span><span>　</span>' +
    //             // '           </td></tr>' +
    //             // '       </table>' +
    //             // '   </td></tr>' +
    //             // '</table>'+
    //             "<div class=\"o2_editorPlugin_doc_fileno\">浙移发〔2019〕20号</div>" +
    //             "<div color=\"#ff0000\" class=\"o2_editorPlugin_doc_redline\"></div>" +
    //             "<div class=\"o2_editorPlugin_doc_subject\">文件标题</div>" +
    //             "<div class=\"o2_editorPlugin_doc_mainSend\">主送单位：</div>"+
    //             "<div class=\"o2_editorPlugin_doc_file\">　　正文内容...</div>" +
    //
    //             '<table class="o2_editorPlugin_doc_attachment" width="100%" cellpadding="0" cellspacing="0" border="0">' +
    //             '   <tr><td class="o2_editorPlugin_doc_attachment_title_td">' +
    //             '       <span>　　</span><span class="o2_editorPlugin_doc_attachment_title">附件：</span>' +
    //             '   </td><td class="o2_editorPlugin_doc_attachment_title_content_td">' +
    //             '       <span class="o2_editorPlugin_doc_attachment_content">附件名称</span>' +
    //             '   </td></tr>' +
    //             '</table>'+
    //
    //             "</div></div>"
    //         //this.editor.setData(html);
    //         editorDiv.loadCss('/x_component_process_Xform/$Form/doc.css', function(){
    //             editorDiv.set("html", html);
    //
    //             var node = editorDiv.getElement('.o2_editorPlugin_doc_file');
    //             node.setAttribute('contenteditable', true);
    //             this.editor = CKEDITOR.inline(node, {"allowedContent":true});
    //
    //         });
    //
    //     }.bind(this));
    // },
    // getEcnetString: function(node, nodes){
    //     for (var i=0; i<node.childNodes.length; i++){
    //         if (node.childNodes[i].nodeType===Node.TEXT_NODE){
    //             var s = this.ecnetString.length;
    //             this.ecnetString += node.childNodes[i].nodeValue;
    //             var e = this.ecnetString.length;
    //
    //             nodes.push({
    //                 "pnode": node,
    //                 "node": node.childNodes[i],
    //                 "start": s, "end": e
    //             });
    //         }else{
    //             this.getEcnetString(node.childNodes[i], nodes);
    //         }
    //     }
    // },
    // createEcnetNode: function(node){
    //     var newNode = node.node.ownerDocument.createElement("span");
    //
    //     var increment = 0;
    //     var html = node.node.nodeValue;;
    //     node.ecnets.each(function(ecnet){
    //         var s = ecnet.begin+increment-node.start;
    //         var e = ecnet.end+increment-node.start;
    //         if (s<0) s=0;
    //         if (e>node.end+increment) e = node.end+increment;
    //         var length = html.length;
    //
    //         var left = html.substring(0, s);
    //         var ecnetStr = html.substring(s, e);
    //         var right = html.substring(e, html.length);
    //
    //         html = left+"<span class='o2_ecnet_item' style='color: red'><u>"+ecnetStr+"</u></span>"+right;
    //         increment += (html.length-length);
    //
    //     }.bind(this));
    //     newNode.innerHTML = html;
    //     node.pnode.replaceChild(newNode, node.node);
    //     node.pnode.textNode = node.node;
    //     node.pnode.ecnetNode = newNode;
    //
    //     var _self = this;
    //     var editorFrame = this.editor.document.$.defaultView.frameElement;
    //     var spans = newNode.getElementsByTagName("span");
    //     if (spans.length){
    //         for (var i = 0; i<spans.length; i++){
    //             var span = spans[i];
    //             if (span.className==="o2_ecnet_item"){
    //                 var ecnetNode = new Element("div", {"styles": {
    //                     "border": "1px solid #999999",
    //                     "box-shadow": "0px 0px 5px #999999",
    //                     "background-color": "#ffffff",
    //                     "position": "fixed",
    //                     "display": "none"
    //                 }}).inject(editorFrame, "after");
    //                 var correctNode = new Element("div", {
    //                     "styles": {
    //                         "padding": "3px 10px",
    //                         "font-weight": "bold",
    //                         "font-size": "12px",
    //                         "cursor": "pointer"
    //                     },
    //                     "text": node.ecnets[i].origin+"->"+node.ecnets[i].correct,
    //                     "events": {
    //                         "mouseover": function(){this.setStyle("background-color", "#dddddd")},
    //                         "mouseout": function(){this.setStyle("background-color", "#ffffff")},
    //                         "mousedown": function(){
    //                             var ecnetNode = this.getParent();
    //                             var node = ecnetNode.node;
    //                             var item = ecnetNode.node.ecnets[ecnetNode.idx];
    //                             var textNode = node.node.ownerDocument.createTextNode(item.correct);
    //                             ecnetNode.span.parentNode.replaceChild(textNode, ecnetNode.span);
    //                             ecnetNode.destroy();
    //                             node.node.nodeValue = node.pnode.ecnetNode.innerText;
    //
    //                             node.ecnets.erase(item);
    //                             if (!node.ecnets.length){
    //                                 _self.ecnetNodes.erase(node);
    //                             }
    //                         }
    //                     }
    //                 }).inject(ecnetNode);
    //                 var ignoreNode = new Element("div", {
    //                     "styles": {
    //                         "padding": "3px 10px",
    //                         "font-size": "12px",
    //                         "cursor": "pointer"
    //                     },
    //                     "text": MWF.xApplication.process.Xform.LP.ignore,
    //                     "events": {
    //                         "mouseover": function(){this.setStyle("background-color", "#dddddd")},
    //                         "mouseout": function(){this.setStyle("background-color", "#ffffff")},
    //                         "mousedown": function(){
    //                             var ecnetNode = this.getParent();
    //                             var node = ecnetNode.node;
    //                             var item = ecnetNode.node.ecnets[ecnetNode.idx];
    //                             var textNode = node.node.ownerDocument.createTextNode(ecnetNode.span.innerText);
    //                             ecnetNode.span.parentNode.replaceChild(textNode, ecnetNode.span);
    //                             ecnetNode.destroy();
    //                             node.node.nodeValue = node.pnode.ecnetNode.innerText;
    //
    //                             node.ecnets.erase(item);
    //                             if (!node.ecnets.length){
    //                                 _self.ecnetNodes.erase(node);
    //                             }
    //                         }
    //                     }
    //                 }).inject(ecnetNode);
    //                 ecnetNode.node = node;
    //                 ecnetNode.idx = i;
    //
    //                 span.ecnetNode = ecnetNode;
    //                 ecnetNode.span = span;
    //                 span.addEventListener("click", function(){
    //                     var ecnetNode = this.ecnetNode;
    //                     ecnetNode.show();
    //                     var y = this.offsetTop;
    //                     var x = this.offsetLeft;
    //                     var w = this.offsetWidth;
    //                     var h = this.offsetHeight;
    //                     var p = editorFrame.getPosition();
    //                     var s = ecnetNode.getSize();
    //                     var top = y+p.y+h+5;
    //                     var left = x+p.x-((s.x-w)/2);
    //
    //                     ecnetNode.style.left = ""+left+"px";
    //                     ecnetNode.style.top = ""+top+"px";
    //
    //                     var _span = this;
    //                     var hideEcnetNode = function(){
    //                         ecnetNode.hide();
    //                         _span.ownerDocument.removeEventListener("mousedown", hideEcnetNode);
    //                     };
    //                     this.ownerDocument.addEventListener("mousedown", hideEcnetNode);
    //
    //                 });
    //
    //             }
    //         }
    //     }
    // },
    // clearEcnetNodes: function(){
    //     if (this.ecnetNodes && this.ecnetNodes.length){
    //         this.ecnetNodes.each(function(node){
    //             if (node.pnode.ecnetNode){
    //                 if (node.pnode.ecnetInforNode) node.pnode.ecnetInforNode.destroy();
    //                 node.pnode.ecnetInforNode = null;
    //                 node.pnode.replaceChild(node.pnode.textNode, node.pnode.ecnetNode);
    //             }
    //         }.bind(this));
    //         this.ecnetNodes = [];
    //     }
    // },
    // ecnet: function(data){
    //     //this.editor.document.$.body.innerText
    //     var editorFrame = this.editor.document.$.defaultView.frameElement;
    //     //var data = this.editor.getData();
    //     var body = this.editor.document.$.body;
    //
    //     if (!this.ecnetNodes) this.ecnetNodes = [];
    //     if (this.ecnetNodes.length) this.clearEcnetNodes();
    //
    //     var nodes = [];
    //     this.ecnetString = "";
    //     this.getEcnetString(body, nodes);
    //
    //     MWF.Actions.get("x_general_assemble_control").ecnetCheck({"value": this.ecnetString}, function(json){
    //         if (json.data.itemList && json.data.itemList.length){
    //
    //             nodes.each(function(node){
    //                 var items = [];
    //                 json.data.itemList.each(function(item){
    //                     if ((node.end<=item.end && node.end>item.begin) || (node.start>=item.begin && node.start<item.end) || (node.start<=item.begin && node.end>item.end)){
    //                         items.push(item);
    //                     }
    //                 }.bind(this));
    //                 if (items.length){
    //                     node.ecnets = items;
    //                     this.ecnetNodes.push(node);
    //                 }
    //             }.bind(this));
    //
    //
    //             this.ecnetNodes.each(function(node){
    //                 this.createEcnetNode(node);
    //             }.bind(this));
    //         }else{
    //             body = null;
    //             nodes = null;
    //         }
    //     }.bind(this));
    // },

}); 