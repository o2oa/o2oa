o2.xApplication.process.Xform.widget = o2.xApplication.process.Xform.widget || {};
o2.xApplication.process.Xform.widget.OOXML = o2.xApplication.process.Xform.widget.OOXML || {};
o2.OOXML = o2.OOXML || {};
o2.xApplication.process.Xform.widget.OOXML.WordprocessingML = o2.OOXML.WML = new Class({
    Implements: [Options, Events],
    options: {
        "AppVersion": "16.0000",
        "Application": "o2oa",
        "Company": "",
        "ScaleCrop": "false",
        "LinksUpToDate": "false",
        "SharedDoc": "false",
        "HyperlinksChanged": "false",
        "w_document": "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"\n" +
            "            xmlns:wpc=\"http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas\"\n" +
            "            xmlns:cx=\"http://schemas.microsoft.com/office/drawing/2014/chartex\"\n" +
            "            xmlns:cx1=\"http://schemas.microsoft.com/office/drawing/2015/9/8/chartex\"\n" +
            "            xmlns:cx2=\"http://schemas.microsoft.com/office/drawing/2015/10/21/chartex\"\n" +
            "            xmlns:cx3=\"http://schemas.microsoft.com/office/drawing/2016/5/9/chartex\"\n" +
            "            xmlns:cx4=\"http://schemas.microsoft.com/office/drawing/2016/5/10/chartex\"\n" +
            "            xmlns:cx5=\"http://schemas.microsoft.com/office/drawing/2016/5/11/chartex\"\n" +
            "            xmlns:cx6=\"http://schemas.microsoft.com/office/drawing/2016/5/12/chartex\"\n" +
            "            xmlns:cx7=\"http://schemas.microsoft.com/office/drawing/2016/5/13/chartex\"\n" +
            "            xmlns:cx8=\"http://schemas.microsoft.com/office/drawing/2016/5/14/chartex\"\n" +
            "            xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\"\n" +
            "            xmlns:aink=\"http://schemas.microsoft.com/office/drawing/2016/ink\"\n" +
            "            xmlns:am3d=\"http://schemas.microsoft.com/office/drawing/2017/model3d\"\n" +
            "            xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n" +
            "            xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"\n" +
            "            xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\" xmlns:v=\"urn:schemas-microsoft-com:vml\"\n" +
            "            xmlns:wp14=\"http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing\"\n" +
            "            xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\"\n" +
            "            xmlns:w10=\"urn:schemas-microsoft-com:office:word\"\n" +
            "            xmlns:w14=\"http://schemas.microsoft.com/office/word/2010/wordml\"\n" +
            "            xmlns:w15=\"http://schemas.microsoft.com/office/word/2012/wordml\"\n" +
            "            xmlns:w16cex=\"http://schemas.microsoft.com/office/word/2018/wordml/cex\"\n" +
            "            xmlns:w16cid=\"http://schemas.microsoft.com/office/word/2016/wordml/cid\"\n" +
            "            xmlns:w16=\"http://schemas.microsoft.com/office/word/2018/wordml\"\n" +
            "            xmlns:w16sdtdh=\"http://schemas.microsoft.com/office/word/2020/wordml/sdtdatahash\"\n" +
            "            xmlns:w16se=\"http://schemas.microsoft.com/office/word/2015/wordml/symex\"\n" +
            "            xmlns:wpg=\"http://schemas.microsoft.com/office/word/2010/wordprocessingGroup\"\n" +
            "            xmlns:wpi=\"http://schemas.microsoft.com/office/word/2010/wordprocessingInk\"\n" +
            "            xmlns:wne=\"http://schemas.microsoft.com/office/word/2006/wordml\"\n" +
            "            xmlns:wps=\"http://schemas.microsoft.com/office/word/2010/wordprocessingShape\"\n" +
            "            xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"\n" +
            "            mc:Ignorable=\"w14 w15 w16se w16cid w16 w16cex w16sdtdh wp14\">",
        "xmlHead": "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
    },
    initialize: function(container, worklog, processid, options){
        this.setOptions(options);
        this.path = "../x_component_process_Xform/widget/$OOXML/WordprocessingML/";
        this.dpi = this.getDPI();
    },
    getDPI: function(){
        debugger;
        var div = new Element("div", {"styles": {"width": "1in", "height": "1in"}}).inject(document.body);
        var dpi = div.offsetWidth.toInt();
        div.destroy();
        return dpi;
    },
    getZipTemplate: function(){
        return fetch(this.path+"template.zip").then(function(res){
            return res.blob().then(JSZip.loadAsync);
        });
    },
    load: function(data){
        return new Promise(function(resolve){
            o2.load(["/o2_lib/jszip/jszip.min.js", "/o2_lib/jszip/FileSaver.js"], function(){
                //this.getZipTemplate();
                this.getZipTemplate().then(function(zip){
                    //console.log(zip.files);
                    this.zip = zip;
                    return this.processDocument(data);
                }.bind(this)).then(function(oo_content){
                    var word = new Blob( [oo_content], {type : "application/vnd.openxmlformats-officedocument.wordprocessingml.document"} );
                    //oo_content.type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                    resolve(word);
                });
            }.bind(this));
        }.bind(this));
    },
    processDocument: function(data){
       return this.zip.file("word/document.xml").async("text").then(function(oo_string){
            return this.processWordDocument(oo_string, data);
        }.bind(this)).then(function(oo_str){
            if (oo_str.substring(0, 5)!=="<?xml"){
                oo_str = oo_str.replace(/<w:document.*\>/, this.options.w_document);
                oo_str = this.options.xmlHead + oo_str;
            }
            return this.zip.file("word/document.xml", oo_str).generateAsync({type:"blob"});
            // this.zip.file("word/document.xml", oo_str).generateAsync({type:"blob"}).then(function(oo_content) {
            //     this.saveAs(oo_content, "example.docx");
            // }.bind(this));
        }.bind(this));
    },
    saveAs: function(content, name){
        o2.saveAs(content, name);
    },

    getPageRule: function(cssRules){
        var pageRule = null;
        if (cssRules){
            for (var i=0; i<cssRules.length; i++){
                if (cssRules[i].type===CSSRule.PAGE_RULE){
                    pageRule = cssRules[i];
                    break;
                }
            }
        }
        return pageRule;
    },
    processWordDocument: function(oo_string, data){
        //wgxpath.install();

        var domparser = new DOMParser();
        var oo_doc = domparser.parseFromString(oo_string, "text/xml");

        //var oo_body = oo_doc.evaluate("//w:document/w:body", oo_doc, this.nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
        // var oo_document = oo_doc.documentElement;
        // var keys = Object.keys(this.options.xmlns);
        // keys.forEach(function(k){
        //     oo_document.setAttribute(k, this.options.xmlns[k]);
        // }.bind(this));


        var oo_body = oo_doc.documentElement.querySelector("body");
        // var dom_div;
        // if (o2.typeOf(data) === "string"){
            var dom_div = new Element("div", {"styles": {
                    "display": "block",
                    "width": "442.2pt",
                    "padding": "104.9pt 73.7pt 99.25pt 79.4pt",
                }}).set("html", data).inject(document.body);
        // }else{
        //     dom_div = data;
        // }

        var style= dom_div.getElement("style");
        if (style){
            var dom_pageRule = this.getPageRule(dom_div.getElement("style").sheet.cssRules);
            if (dom_pageRule) this.processPageSection(dom_pageRule, oo_body);
        }
        var dom_wordSection = dom_div.getElement(".WordSection1");
        if (dom_wordSection){
            this.processPageSection(dom_wordSection, oo_body);
            this.processDom(dom_wordSection, oo_body);
        }


        dom_div.destroy();


        // var p = this.createParagraph(xmlDoc);
        // var r = this.createRun(xmlDoc, {
        //     "text": "份数文本",
        //     "font": {
        //         "hint": "eastAsia",
        //         "eastAsia": "黑体",
        //         "other": "Times New Roman"
        //     },
        //     "rPrs": {
        //         "b": { val: "true" },
        //         "color": { val: "FF0000" }
        //     }
        // });
        // this.insertChildren(body, [p, r], "afterbegin");
        //
        var s = new XMLSerializer();
        return s.serializeToString(oo_doc);
    },
    processDom: function(dom, oo_body, append, divAsP){
        dom = dom.getFirst();
        while (dom){
            if (dom.getStyle("display")!=="none"){
                if (dom.hasClass("doc_layout_redHeader")){
                    this.processDom(dom, oo_body, append, true);
                }else if (dom.hasClass("doc_layout_filetext")){
                    this.processFiletext(dom, oo_body, append);
                }else if (dom.tagName.toLowerCase() === "p" || (!!divAsP && dom.tagName.toLowerCase() === "div")){
                    this.processParagraph(dom, oo_body, append);
                    // }else if (dom.tagName.toLowerCase() === "span") {
                    //     this.processRun(dom, oo_body, append);
                }else if (dom.tagName.toLowerCase() === "hr") {
                    this.processHr(dom, oo_body, append);
                }else if (dom.tagName.toLowerCase() === "table") {
                    this.processTable(dom, oo_body, append);
                }else{
                    this.processDom(dom, oo_body, append, divAsP);
                }
            }
            dom = dom.getNext();
        }
    },
    // processFiletextParagraphRun: function(node, oo_p, p){
    //     node = node.firstChild;
    //     while (node){
    //         if (node.nodeType===Node.TEXT_NODE){
    //             if (node.nodeValue.trim()) this.processRun(node.parentElement, oo_p, p);
    //         }else if (node.nodeType===Node.ELEMENT_NODE){
    //             if (node.tagName.toLowerCase() === "span") {
    //                 this.processRun(node, oo_p, p);
    //             }else if (node.tagName.toLowerCase() === "table") {
    //                 this.processTable(node, oo_p, p);
    //             }else{
    //                 this.processFiletextParagraphRun(node, oo_p, p);
    //             }
    //         }else{
    //             this.processFiletextParagraphRun(node, oo_p, p);
    //         }
    //         node = node.nextSibling;
    //     }
    // },
    processFiletext: function(dom, oo_body, append){
        node = dom.getFirst();
        while (node){
            if (node.tagName.toLowerCase() === "div" || node.tagName.toLowerCase() === "p") {
                this.processParagraph(node, oo_body, append);
            }else if (node.tagName.toLowerCase() === "table") {
                this.processTable(node, oo_body, append);
            }
            node = node.getNext();
        }
    },
    // processFiletextParagraph: function(){
    //
    // },
    isEmptyP: function(p){
        var oo_t = p.querySelector("t");
        return !oo_t;
    },
    processParagraphRun: function(node, oo_p, p, oo_body, append, ilvl){
        node = node.firstChild;
        while (node){
            if (node.nodeType===Node.TEXT_NODE){
                if (node.nodeValue.trim()) this.processRun(node.parentElement || node.parentNode, oo_p, p, node.nodeValue);
            }else if (node.nodeType===Node.ELEMENT_NODE){
                if (node.tagName.toLowerCase() === "span") {
                    this.processRun(node, oo_p, p, node.get("text"));
                }else if (node.tagName.toLowerCase() === "br") {
                    this.processRun(node, oo_p, p, "", "br");
                }else if (node.tagName.toLowerCase() === "div" || node.tagName.toLowerCase() === "p") {
                    if (!this.isEmptyP(oo_p)){
                        oo_p = this.createParagraphFromDom(node, oo_body, append);
                    }else{
                        this.setParagraphAttrFromDom(node, oo_p);
                    }
                    this.processParagraphRun(node, oo_p, p, oo_body, append, ilvl);
                }else if (node.tagName.toLowerCase() === "ul" || node.tagName.toLowerCase() === "ol") {
                    this.processNumbering(node, oo_p, p, oo_body, append, ilvl);
                }else if (node.tagName.toLowerCase() === "table") {
                    this.processTable(node, oo_body);
                    if (this.isEmptyP(oo_p)) oo_p.destroy();
                    oo_p = this.createParagraphFromDom(p, oo_body, append);
                }else{
                    this.processParagraphRun(node, oo_p, p, oo_body, append, ilvl);
                }
            }else{
                this.processParagraphRun(node, oo_p, p, oo_body, append, ilvl);
            }
            node = node.nextSibling;
        }
    },
    processNumbering: function(node, oo_p, p, oo_body, append, ilvl){
        ilvl = ilvl || 0;
        var nextIlvl = ilvl + 1;
        var numId = (node.tagName.toLowerCase() === "ul") ? "1" : "2";
        var lis = node.getChildren("li");
        for (var i=0; i<lis.length; i++){
            var li = lis[i];
            if (!this.isEmptyP(oo_p)) oo_p = this.createParagraphFromDom(li, oo_body, append);
            var oo_pPr = oo_p.querySelector("pPr");
            if (!oo_pPr){
                oo_pPr = this.createEl(oo_body.ownerDocument, "pPr");
                oo_p.appendChild(oo_pPr);
            }
            oo_numPr = this.createEl(oo_body.ownerDocument, "numPr");
            oo_ilvl = this.createEl(oo_body.ownerDocument, "ilvl");
            this.setAttrs(oo_ilvl, {"val": ilvl});
            oo_numId = this.createEl(oo_body.ownerDocument, "numId");
            this.setAttrs(oo_numId, {"val": numId});
            oo_numPr.appendChild(oo_ilvl);
            oo_numPr.appendChild(oo_numId);
            oo_pPr.appendChild(oo_numPr);

            this.processParagraphRun(li, oo_p, li, oo_body, append, nextIlvl);
        }
    },
    getPPrs: function(dom){
        var pPrs = {};

        var align = dom.getStyle("text-align");
        if (align){
            var jc = "start"
            switch (align){
                case "center": jc = "center"; break;
                case "right":
                case "end": jc = "end"; break;
                case "justify": jc = "both"; break;
            }
            pPrs.jc = {"val": jc};
        }
        var left = dom.getStyle("margin-left");
        if (left && left.toFloat()){
            var left = this.pxToPt(left)*20;
            if (left) {
                if (!pPrs.ind) pPrs.ind = {};
                pPrs.ind.left = left;
            }
        }

        var right = dom.getStyle("margin-left");
        if (right && right.toFloat()){
            var right = this.pxToPt(right)*20;
            if (right) {
                if (!pPrs.ind) pPrs.ind = {};
                pPrs.ind.right = right;
            }
        }

        var indent = dom.getStyle("text-indent");
        if (indent && indent.toFloat()){
            var indent = this.pxToPt(indent)*20;
            if (indent) {
                if (!pPrs.ind) pPrs.ind = {};
                if (indent>0){
                    pPrs.ind.firstLine = indent;
                }else{
                    pPrs.ind.hanging = Math.abs(indent);
                }
            }
        }
        return pPrs;
    },
    setParagraphAttrFromDom: function(dom, oo_p){
        var pPrs = this.getPPrs(dom);
        var oo_pPr = oo_p.querySelector("pPr");
        if (!oo_pPr){
            oo_pPr = this.createEl(oo_p.ownerDocument, "pPr");
            oo_p.appendChild(oo_pPr);
        }

        Object.keys(pPrs).each(function(k){
            var node = oo_pPr.querySelector(k);
            if (!node) node = this.createEl(oo_p.ownerDocument, k);
            this.setAttrs(node, pPrs[k]);
            oo_pPr.appendChild(node);
        }.bind(this));
    },
    createParagraphFromDom: function(dom, oo_body, append){
        var oo_p = this.createParagraph(oo_body.ownerDocument, {"pPrs": this.getPPrs(dom)});

        if (append){
            oo_body.appendChild(oo_p);
        }else{
            var oo_sectPr = this.getEl(oo_body, "sectPr");
            if (oo_sectPr){
                this.insertSiblings(oo_sectPr, [oo_p], "beforebegin");
            }else{
                this.insertChildren(oo_body, [oo_p]);
            }
        }
        return oo_p;
    },
    processParagraph: function(dom, oo_body, append){
        var oo_p = this.createParagraphFromDom(dom, oo_body, append);
        this.processParagraphRun(dom, oo_p, dom, oo_body, append);
        if (this.isEmptyP(oo_p)) oo_p.destroy();
        return oo_p;
    },
    getTableTblW: function(table){
        var type = "dxa";
        var w = table.style.width;
        if (!w) w = table.get("width");
        //if (w) w = this.pxToPt(w);
        if (w && o2.typeOf(w)==="string"){
            var u = w.substring(w.length-1, w.length);
            if (u==="%"){
                w = w.toFloat()*50;
                type = "pct";
            }else{
                u = w.substring(w.length-2, w.length);
                if (u.toLowerCase()!=="pt"){
                    w = this.pxToPt(w);
                }
            }
        }
        if (!w || !w.toFloat()){
            w = 0;
            type = "auto";
        }else{
            //w = w.toFloat();
            if (type === "dxa") w = w.toFloat()*20;
        }
        return {"w": w, "type": type};
    },
    getTableBorder: function(table, where){
        var attr = {
            "space": "0",
            "val": "single",
            "color": "auto",
            "sz": "0"
        }
        // var border = (table.currentStyle) ? table.currentStyle[("border-"+where+"-style").camelCase()] : table.getStyle("border-"+where);
        // if (border==="none") border = table.get("border");
        // if (!border || border==="none"){
        //     attr.val = "none";
        // }else{
        var sz;
        var border = (table.currentStyle) ? table.currentStyle[("border-"+where+"-style").camelCase()] : table.getStyle("border-"+where);
        if (!border || border==="none"){
            sz = table.get("border");
            if (!sz || sz==="none") sz = table.getStyle("border-"+where+"-width");
        }else{
            sz = (table.currentStyle) ? table.currentStyle[("border-"+where+"-width").camelCase()] : table.getStyle("border-"+where+"-width");
            if (!sz || !sz.toFloat()) sz = table.get("border");
        }

        if (sz && o2.typeOf(sz)==="string"){
            u = sz.substring(sz.length-2, sz.length);
            if (u.toLowerCase()!=="pt"){
                sz = this.pxToPt(sz);
            }
        }
        if (!sz || !sz.toFloat()) sz = 0;
        attr.sz = sz.toFloat()*8;
        if (Browser.name=="firefox") attr.sz = attr.sz*1.25;    //firefox边框计算问题

        var color = this.getColorHex(((table.currentStyle) ? table.currentStyle[("border-"+where+"-color").camelCase()] : table.getStyle("border-"+where+"-color")));
        if (!color) color = "auto";
        attr.color = color;

        var style = (table.currentStyle) ? table.currentStyle[("border-"+where+"-style").camelCase()] : table.getStyle("border-"+where+"-style");
        switch (style){
            case "dashed": case "dotted": case "double": attr.val = "double"; break;
            default: attr.val = "single";
        }
        if (attr.sz===0) attr.val="none";
        // }

        // var sz = table.get("border");
        // if (sz) sz = this.pxToPt(sz);
        // if (!sz) sz = table.getStyle("border-"+where+"-width");
        // if (!sz || !sz.toFloat()) sz = 0;
        // attr.sz = sz.toFloat()*20;


        var space = table.get("cellspacing");
        if (space) attr.space = this.pxToPt(space);


        return attr;
    },
    getTableTblGrid: function(table){
        var grids = [];
        var trs = table.rows;
        for (var i = 0; i < trs.length; i++){
            var idx = 0;
            tds = trs[i].cells;
            for (var j=0; j<tds.length; j++){
                var td = tds[j];
                var colspan = td.get("colspan");
                if (!colspan || colspan.toInt()===1) {
                    while (grids.length<=idx) grids.push(0);
                    var pt = this.pxToPt(td.clientWidth);
                    if (pt>grids[idx]) grids[idx] = pt;
                }else{
                    var addTd = colspan.toInt()-1;
                    var tempTds = [];
                    for (var n=0; n<addTd; n++) tempTds.push(new Element("td").inject(td, "after"));

                    while (grids.length<=idx) grids.push(0);
                    var pt = this.pxToPt(td.clientWidth);
                    if (pt>grids[idx]) grids[idx] = pt;

                    tempTds.each(function(tmpTd){
                        idx++;
                        while (grids.length<=idx) grids.push(0);
                        var pt = this.pxToPt(tmpTd.clientWidth);
                        if (pt>grids[idx]) grids[idx] = pt;
                    }.bind(this));
                    tempTds.each(function(tmpTd){
                        tmpTd.destroy();
                    });
                }
                idx++;
            }
        }
        return grids;
    },
    getTdValign: function(td){
        var v = "";
        var valign = td.getStyle("vertical-align") || td.get("valign");
        if (valign) {
            switch (valign){
                case "bottom": v = "bottom"; break;
                case "top": v = "top"; break;
                default: v = "center";
            }
        }
        return v;
    },
    getMsoStyle: function(dom){
        var o = {};

        var s = dom.getAttribute("style");
        if (s){
            var sList = s.split(/\s*;\s*/g);
            sList.map(function(style){
                var styles = style.split(/\s*:\s*/g);
                if (styles.length===2){
                    if (styles[0].substr(0,3).toLowerCase()==="mso"){
                        o[styles[0]] = styles[1];
                    }
                }
                return false;
            });
        }
        return o;
    },
    processTableDom: function(dom, oo_body, append, divAsP, oo_tc){
        dom = dom.firstChild;
        while (dom){
            if (dom.nodeType===Node.ELEMENT_NODE){
                if (dom.hasClass("doc_layout_filetext")){
                    this.processFiletext(dom, oo_body, append);
                }else if (dom.tagName.toLowerCase() === "p" || (!!divAsP && dom.tagName.toLowerCase() === "div")){
                    this.processParagraph(dom, oo_body, append);
                    // }else if (dom.tagName.toLowerCase() === "span") {
                    //     this.processRun(dom, oo_body, append);
                }else if (dom.tagName.toLowerCase() === "br") {
                    this.processRun(dom, oo_body, append, "", "br");
                }else if (dom.tagName.toLowerCase() === "hr") {
                    this.processHr(dom, oo_body, append);
                }else if (dom.tagName.toLowerCase() === "table") {
                    this.processTable(dom, oo_body, append);
                }else{
                    this.processTableDom(dom, oo_body, append, divAsP);
                }
            }else if (dom.nodeType===Node.TEXT_NODE){
                if (dom.nodeValue.trim()){
                    if (!oo_body || oo_body.tagName.toString().toLowerCase()!=="w:p") var oo_body = this.createParagraphFromDom(dom.parentElement || dom.parentNode, oo_body, dom.parentElement);
                    this.processRun(dom.parentElement || dom.parentNode, oo_body, append, dom.nodeValue);
                }
            }else{
                this.processTableDom(dom, oo_body, append);
            }
            dom = dom.nextSibling;
        }
    },
    arrangeTable: function(table){
        debugger;
        //检查table，不合理的colspan
        var tableMatrix = [];
        var trs = table.rows;
        var rowspan = {};
        for (var i=0; i<trs.length; i++){
            tableMatrix[i] = [];

            //垂直合并单元格处理
            var tdIdx = 0;
            var rowspanObj = rowspan[tdIdx];
            while (rowspanObj && rowspanObj.count){
                rowspanObj.count--;
                tableMatrix[i].push({"td": rowspanObj.td, type: 0});
                if (rowspanObj.count<1) delete rowspan[tdIdx];
                tdIdx++;
                rowspanObj = rowspan[tdIdx];
            }
            var tds = trs[i].cells;
            for (var j=0; j<tds.length; j++){
                var td = tds[j];
                //记录实体单元格
                tableMatrix[i].push({"td": td, type: 1});

                //记录此单元格是否有垂直合并
                var rspan = td.get("rowspan");
                if (rspan && rspan.toInt()>1){
                    rowspan[tdIdx] = {
                        "td": td,
                        "count": rspan.toInt()-1
                    };
                }

                //补齐水平合并的虚拟单元格
                var cspan = td.get("colspan");
                if (cspan && cspan.toInt()>1){
                    for (var n=1; n<cspan.toInt(); n++){
                        tableMatrix[i].push({"td": td, type: 0});
                    }
                }

                //检查有没有之前的垂直合并内容
                var nextIdx = tdIdx+1;
                var rowspanObj = rowspan[nextIdx];
                while (rowspanObj && rowspanObj.count){
                    rowspanObj.count--;
                    tableMatrix[i].push({"td": rowspanObj.td, type: 0});
                    if (rowspanObj.count<1) delete rowspan[nextIdx];
                    nextIdx++;
                    rowspanObj = rowspan[nextIdx];
                }

                if (cspan && cspan.toInt()>1){
                    if (rowspan[tdIdx]){
                        rowspan[tdIdx].count = (rowspan[tdIdx].count)*cspan.toInt();
                        for (var n=1; n<cspan.toInt(); n++){
                            rowspan[tdIdx+n] = rowspan[tdIdx];
                        }
                    }
                }

                tdIdx = nextIdx-1;
                tdIdx++;
            }
        }

        var y = tableMatrix.length;
        if (y>0){
            var x = tableMatrix[0].length;
            for (var x1=0; x1<x; x1++){
                var flag = 0;
                for (var y1=0; y1<y; y1++){
                    if (tableMatrix[y1][x1].type!==0){
                        flag=1;
                        break;
                    }
                }
                if (flag===0){
                    for (var y1=0; y1<y; y1++){
                        if (y1==tableMatrix[y1][x1].td.getParent("tr").rowIndex){
                            if (tableMatrix[y1][x1].td){
                                var colspan = tableMatrix[y1][x1].td.get("colspan");
                                colspan = colspan.toInt()-1;
                                tableMatrix[y1][x1].td.set("colspan", colspan);
                            }
                        }
                    }
                }
            }
        }

    },
    processTable: function(table, oo_body, append){
        this.arrangeTable(table);

        var oo_doc = oo_body.ownerDocument;

        var oo_tbl = this.createEl(oo_doc, "tbl");
        var oo_tblPr = this.createEl(oo_doc, "tblPr");

        //表格宽度属性
        var oo_tblW = this.createEl(oo_doc, "tblW");
        var tblW = this.getTableTblW(table);
        this.setAttrs(oo_tblW, tblW);
        oo_tblPr.appendChild(oo_tblW);

        //表格边框属性
        var oo_tblBorders = this.createEl(oo_doc, "tblBorders");
        var oo_top = this.createEl(oo_doc, "top");
        this.setAttrs(oo_top, this.getTableBorder(table, "top"));
        var oo_start = this.createEl(oo_doc, "start");
        this.setAttrs(oo_start, this.getTableBorder(table, "left"));
        var oo_bottom = this.createEl(oo_doc, "bottom");
        this.setAttrs(oo_bottom, this.getTableBorder(table, "bottom"));
        var oo_end = this.createEl(oo_doc, "end");
        this.setAttrs(oo_end, this.getTableBorder(table, "right"));
        this.insertSiblings(oo_tblBorders, [oo_top, oo_start, oo_bottom, oo_end], "beforeend");
        oo_tblPr.appendChild(oo_tblBorders);

        //表格边距
        var mar = table.get("cellpadding").toFloat();
        if (!mar) mar = 0;
        //if (mar){
        mar = this.pxToPt(mar)*20;
        var left = table.getStyle("padding-left");
        var right = table.getStyle("padding-right");
        var top = table.getStyle("padding-top");
        var bottom = table.getStyle("padding-bottom");
        left = (left) ? this.pxToPt(left)*20 : 0;
        right = (right) ? this.pxToPt(right)*20 : 0;
        top = (top) ? this.pxToPt(top)*20 : 0;
        bottom = (bottom) ? this.pxToPt(bottom)*20 : 0;
        var oo_tblCellMar = this.createEl(oo_doc, "tblCellMar");
        var oo_mar = this.createEl(oo_doc, "start");
        this.setAttrs(oo_mar, {"type": "dxa", "w": left || mar});
        oo_tblCellMar.appendChild(oo_mar);
        oo_mar = this.createEl(oo_doc, "end");
        this.setAttrs(oo_mar, {"type": "dxa", "w": right || mar});
        oo_tblCellMar.appendChild(oo_mar);
        oo_mar = this.createEl(oo_doc, "top");
        this.setAttrs(oo_mar, {"type": "dxa", "w": top || mar});
        oo_tblCellMar.appendChild(oo_mar);
        oo_mar = this.createEl(oo_doc, "bottom");
        this.setAttrs(oo_mar, {"type": "dxa", "w": bottom || mar});
        oo_tblCellMar.appendChild(oo_mar);
        oo_tblPr.appendChild(oo_tblCellMar);
        //}

        //左右对齐
        var align = table.get("align");
        if (align){
            var jc = "start"
            switch (align){
                case "center": jc = "center"; break;
                case "right":
                case "end": jc = "end"; break;
                case "justify": jc = "both"; break;
            }
            var oo_jc = this.createEl(oo_doc, "jc");
            this.setAttrs(oo_jc, {"val": jc});
            oo_tblPr.appendChild(oo_jc);
        }

        //表格浮动
        var floatTable = false;
        var msoStyle = this.getMsoStyle(table);
        var horzAnchor = msoStyle["mso-table-anchor-horizontal"];
        var vertAnchor = msoStyle["mso-table-anchor-vertical"];
        var tblpXSpec = msoStyle["mso-table-left"];
        var tblpYSpec = msoStyle["mso-table-top"];
        if (horzAnchor || vertAnchor || tblpXSpec || tblpYSpec){
            if (horzAnchor && horzAnchor!=="page" && horzAnchor!=="margin" && horzAnchor!=="text") horzAnchor="margin";
            if (vertAnchor && vertAnchor!=="page" && vertAnchor!=="margin" && vertAnchor!=="text") vertAnchor="margin";
            var o = {
                "horzAnchor": horzAnchor || null,
                "vertAnchor": vertAnchor || null,
                "tblpXSpec": tblpXSpec || null,
                "tblpYSpec": tblpYSpec || null
            }
            var oo_tblpPr = this.createEl(oo_doc, "tblpPr");
            this.setAttrs(oo_tblpPr, o);
            oo_tblPr.appendChild(oo_tblpPr);
            floatTable = true;
        }

        //表格背景
        var bg = table.getStyle("background-color");
        if (bg && bg!=="transparent"){
            bg = this.getColorHex(bg);
            var oo_shd = this.createEl(oo_doc, "shd");
            this.setAttrs(oo_shd, {"val": "clear", "color": "auto", "fill": bg});
            oo_tblPr.appendChild(oo_shd);
        }


        // table.style
        //
        // mso-table-anchor-vertical:margin; mso-table-anchor-horizontal:column;mso-table-left:left;mso-table-top:bottom
        //
        // <w:tblpPr w:tblpYSpec="bottom" w:tblpXSpec="center" w:horzAnchor="margin"/>
        // <w:tblOverlap w:val="never"/>



        oo_tbl.appendChild(oo_tblPr);

        //表格网格
        var grids = this.getTableTblGrid(table);
        var oo_tblGrid = this.createEl(oo_doc, "tblGrid");
        grids.each(function(grid){
            var oo_gridCol = this.createEl(oo_doc, "gridCol");
            this.setAttrs(oo_gridCol, {"w": grid*20});
            oo_tblGrid.appendChild(oo_gridCol);
        }.bind(this));
        oo_tbl.appendChild(oo_tblGrid);

        var vmge = {};
        var trs = table.rows;
        for (var i=0; i<trs.length; i++){
            var tr = trs[i];

        // }
        // trs.each(function(tr){
            var oo_tr = this.createEl(oo_doc, "tr");
            if (floatTable){
                var oo_trPr = this.createEl(oo_doc, "trPr");
                var oo_cantSplit = this.createEl(oo_doc, "cantSplit");
                var oo_tblHeader = this.createEl(oo_doc, "tblHeader");
                oo_trPr.appendChild(oo_cantSplit);
                oo_trPr.appendChild(oo_tblHeader);
                oo_tr.appendChild(oo_trPr);
            }


            var tdIdx = 0;
            //垂直合并单元格
            var nextIdx = tdIdx;
            var mge = vmge["td"+nextIdx];
            while (mge){
                vmge["td"+nextIdx].idx--;
                var tcPr = vmge["td"+nextIdx].tcPr;

                var oo_mtc = this.createEl(oo_doc, "tc");
                if (tcPr) {
                    var oo_mtcPr = tcPr.cloneNode(true);
                    var oo_mvMerge = oo_mtcPr.querySelector("vMerge");
                    if (oo_mvMerge) oo_mvMerge.destroy();
                    oo_mvMerge = this.createEl(oo_doc, "vMerge");
                    oo_mtcPr.appendChild(oo_mvMerge);
                    oo_mtc.appendChild(oo_mtcPr);
                }else{
                    var oo_mtcPr = this.createEl(oo_doc, "tcPr");
                    var oo_mvMerge = this.createEl(oo_doc, "vMerge");
                    oo_mtcPr.appendChild(oo_mvMerge);
                    oo_mtc.appendChild(oo_mtcPr);
                }

                // var oo_mtc = this.createEl(oo_doc, "tc");
                // var oo_mtcPr = this.createEl(oo_doc, "tcPr");
                // var oo_mvMerge = this.createEl(oo_doc, "vMerge");
                // oo_mtcPr.appendChild(oo_mvMerge);
                // oo_mtc.appendChild(oo_mtcPr);
                var oo_mp = this.createEl(oo_doc, "p");
                oo_mtc.appendChild(oo_mp);
                oo_tr.appendChild(oo_mtc);
                if (vmge["td"+nextIdx].idx<1) delete vmge["td"+nextIdx];

                tdIdx++;

                nextIdx++;
                mge = vmge["td"+nextIdx];
            }

            // var tds = tr.getElements("td");
            var tds = tr.cells;

            for (var j=0; j<tds.length; j++){
                var td = tds[j];
            //}
            // tds.each(function(td, idx){
                 var oo_tc = this.createEl(oo_doc, "tc");

                 var oo_tcPr = this.createEl(oo_doc, "tcPr");
                 //单元格宽度
                 var oo_tcW = this.createEl(oo_doc, "tcW");
                var tcW = this.getTableTblW(td);
                this.setAttrs(oo_tcW, tcW);
                 //this.setAttrs(oo_tcW, {"w": this.pxToPt(td.clientWidth)*20, "type": "dxa"});
                 oo_tcPr.appendChild(oo_tcW);

                 //单元格边框
                 var oo_tcBorders = this.createEl(oo_doc, "tcBorders");
                 var oo_top = this.createEl(oo_doc, "top");
                 this.setAttrs(oo_top, this.getTableBorder(td, "top"));
                 var oo_start = this.createEl(oo_doc, "start");
                 this.setAttrs(oo_start, this.getTableBorder(td, "left"));
                 var oo_bottom = this.createEl(oo_doc, "bottom");
                 this.setAttrs(oo_bottom, this.getTableBorder(td, "bottom"));
                 var oo_end = this.createEl(oo_doc, "end");
                 this.setAttrs(oo_end, this.getTableBorder(td, "right"));
                 this.insertSiblings(oo_tcBorders, [oo_top, oo_start, oo_bottom, oo_end], "beforeend");
                 oo_tcPr.appendChild(oo_tcBorders);

                //单元格背景
                var bg = td.getStyle("background-color");
                if (bg && bg!=="transparent"){
                    bg = this.getColorHex(bg);
                    var oo_shd = this.createEl(oo_doc, "shd");
                    this.setAttrs(oo_shd, {"val": "clear", "color": "auto", "fill": bg});
                    oo_tcPr.appendChild(oo_shd);
                }

                 //单元格边距
                var left = td.getStyle("padding-left");
                var right = td.getStyle("padding-right");
                var top = td.getStyle("padding-top");
                var bottom = td.getStyle("padding-bottom");
                left = (left) ? this.pxToPt(left)*20 : 0;
                right = (right) ? this.pxToPt(right)*20 : 0;
                top = (top) ? this.pxToPt(top)*20 : 0;
                bottom = (bottom) ? this.pxToPt(bottom)*20 : 0;
                var oo_tcMar = this.createEl(oo_doc, "tcMar");
                var oo_mar = this.createEl(oo_doc, "start");
                this.setAttrs(oo_mar, {"type": "dxa", "w": left});
                oo_tcMar.appendChild(oo_mar);
                oo_mar = this.createEl(oo_doc, "end");
                this.setAttrs(oo_mar, {"type": "dxa", "w": right});
                oo_tcMar.appendChild(oo_mar);
                oo_mar = this.createEl(oo_doc, "top");
                this.setAttrs(oo_mar, {"type": "dxa", "w": top});
                oo_tcMar.appendChild(oo_mar);
                oo_mar = this.createEl(oo_doc, "bottom");
                this.setAttrs(oo_mar, {"type": "dxa", "w": bottom});
                oo_tcMar.appendChild(oo_mar);
                oo_tcPr.appendChild(oo_tcMar);

                 var v = this.getTdValign(td);
                 if (v){
                     var oo_vAlign = this.createEl(oo_doc, "vAlign");
                     this.setAttrs(oo_vAlign, {"val": v});
                     oo_tcPr.appendChild(oo_vAlign);
                 }

                 var oo_hideMark = this.createEl(oo_doc, "hideMark");
                 oo_tcPr.appendChild(oo_hideMark);

                //垂直合并单元格
                var rowspan = td.get("rowspan");
                if (rowspan && rowspan.toInt()>1){
                    vmge["td"+tdIdx] = {
                        "tcPr": oo_tcPr,
                        "idx": rowspan.toInt()-1
                    };
                    var oo_vMerge = this.createEl(oo_doc, "vMerge");
                    this.setAttrs(oo_vMerge, {"val": "restart"});
                    oo_tcPr.appendChild(oo_vMerge);
                }
                //水平合并单元格
                var colspan = td.get("colspan");
                if (colspan && colspan.toInt()>1){
                    tdIdx = tdIdx+(colspan.toInt()-1);
                    var oo_gridSpan = this.createEl(oo_doc, "gridSpan");
                    this.setAttrs(oo_gridSpan, {"val": colspan});
                    oo_tcPr.appendChild(oo_gridSpan);
                    // this.insertChildren(oo_tcPr, [oo_gridSpan], "afterbegin");
                }

                 oo_tc.appendChild(oo_tcPr);
                 //表格内容；
                 this.processTableDom(td, oo_tc, td, true, oo_tc);

                 var pflag = false;
                 var node = oo_tc.firstChild;
                 while (node){
                     if (node.tagName==="w:p"){
                         pflag = true;
                         break;
                     }
                     node = node.nextSibling;
                 }

                 if (!pflag){
                     var oo_p = this.createEl(oo_doc, "p");
                     oo_tc.appendChild(oo_p);
                 }

                 oo_tr.appendChild(oo_tc);

                 //垂直合并单元格
                 var nextIdx = tdIdx+1;
                 var mge = vmge["td"+nextIdx];
                 while (mge){
                     vmge["td"+nextIdx].idx--;
                     var tcPr = vmge["td"+nextIdx].tcPr;

                     var oo_mtc = this.createEl(oo_doc, "tc");

                     if (tcPr) {
                         var oo_mtcPr = tcPr.cloneNode(true);
                         var oo_mvMerge = oo_mtcPr.querySelector("vMerge");
                         if (oo_mvMerge) oo_mvMerge.destroy();
                         oo_mvMerge = this.createEl(oo_doc, "vMerge");
                         oo_mtcPr.appendChild(oo_mvMerge);
                         oo_mtc.appendChild(oo_mtcPr);
                     }else{
                         var oo_mtcPr = this.createEl(oo_doc, "tcPr");
                         var oo_mvMerge = this.createEl(oo_doc, "vMerge");
                         oo_mtcPr.appendChild(oo_mvMerge);
                         oo_mtc.appendChild(oo_mtcPr);
                     }

                     var oo_mp = this.createEl(oo_doc, "p");
                     oo_mtc.appendChild(oo_mp);
                     oo_tr.appendChild(oo_mtc);

                     if (vmge["td"+nextIdx].idx<1) delete vmge["td"+nextIdx];
                     //tdIdx++;

                     nextIdx++;
                     mge = vmge["td"+nextIdx];
                 }

                if (colspan && colspan.toInt()>1){
                    if (vmge["td"+tdIdx]){
                        vmge["td"+tdIdx].idx = (vmge["td"+tdIdx].idx)*colspan.toInt();
                        for (var n=1; n<colspan.toInt(); n++){
                            var m = tdIdx+n
                            rowspan[m] = vmge["td"+tdIdx];
                        }
                    }
                }

                tdIdx = nextIdx-1;
                tdIdx++;
             }

            oo_tbl.appendChild(oo_tr);
        }


        if (append){
            oo_body.appendChild(oo_tbl);
        }else{
            var oo_sectPr = this.getEl(oo_body, "sectPr");
            if (oo_sectPr){
                this.insertSiblings(oo_sectPr, [oo_tbl], "beforebegin");
            }else{
                this.insertChildren(oo_body, [oo_tbl]);
            }
        }
    },

    pxToPt: function(px){
        var v = px;
        if (px && o2.typeOf(px)==="string"){
            u = px.substring(px.length-2, px.length);
            if (u.toLowerCase()!=="pt"){
                v = (px.toFloat()/this.dpi)*72;
            }else{
                v = px.toFloat();
            }
        }else{
            v = (px.toFloat()/this.dpi)*72;
        }
        return v;
    },
    processHr: function(hr, oo_body, append){
        var oo_doc = oo_body.ownerDocument;

        var oo_p = this.createParagraph(oo_doc, {});
        if (append){
            oo_body.appendChild(oo_p);
        }else{
            var oo_sectPr = this.getEl(oo_body, "sectPr");
            if (oo_sectPr){
                this.insertSiblings(oo_sectPr, [oo_p], "beforebegin");
            }else{
                this.insertChildren(oo_body, [oo_p]);
            }
        }

        var oo_run = this.createRun(oo_doc, {"rPrs": {"noProof":{}}});
        var oo_drawing = this.createEl(oo_doc, "drawing");
        var oo_anchor = this.createEl(oo_doc, "anchor", "wp");
        this.setAttrs(oo_anchor, {
            "distT": "0", "distB": "0", "distL": "0", "distR": "0", "simplePos": "false","behindDoc": "false","relativeHeight": "500", "locked": "false", "layoutInCell":"true", "allowOverlap": "false"
        }, false);
        oo_run.appendChild(oo_drawing);
        oo_drawing.appendChild(oo_anchor);

        var oo_simplePos = this.createEl(oo_doc, "simplePos", "wp");
        this.setAttrs(oo_simplePos, {"x": "0", "y": "0"}, false);

        var oo_positionH = this.createEl(oo_doc, "positionH", "wp");
        this.setAttrs(oo_positionH, {"relativeFrom": "column"}, false);
        var oo_posOffset = this.createEl(oo_doc, "posOffset", "wp");
        oo_posOffset.appendChild(oo_doc.createTextNode("0"));
        oo_positionH.appendChild(oo_posOffset);

        var oo_positionV = this.createEl(oo_doc, "positionV", "wp");
        this.setAttrs(oo_positionV, {"relativeFrom": "paragraph"}, false);
        var oo_posOffset = this.createEl(oo_doc, "posOffset", "wp");
        oo_posOffset.appendChild(oo_doc.createTextNode("161290"));  //此处需要根据行高来设置数值,暂时固定数值
        oo_positionV.appendChild(oo_posOffset);

        var oo_extent = this.createEl(oo_doc, "extent", "wp");
        var cx = this.pxToPt(hr.clientWidth)*12700;
        this.setAttrs(oo_extent, {"cx": cx, "cy": "0"}, false);   //cx为线长度（pt*12700）

        var oo_effectExtent = this.createEl(oo_doc, "effectExtent", "wp");
        this.setAttrs(oo_effectExtent, {"l": "0", "t": "0", "r": "0", "b": "0"}, false);

        var oo_wrapNone = this.createEl(oo_doc, "wrapNone", "wp");

        var oo_docPr = this.createEl(oo_doc, "docPr", "wp");
        id = (Math.random()*100).toInt();
        this.setAttrs(oo_docPr, {"id": id, "name": "Red Line"}, false);   //id设置随机整数

        var oo_cNvGraphicFramePr = this.createEl(oo_doc, "cNvGraphicFramePr", "wp");

        this.insertSiblings(oo_anchor, [oo_simplePos, oo_positionH, oo_positionV, oo_extent, oo_effectExtent, oo_wrapNone, oo_docPr, oo_cNvGraphicFramePr], "beforeend");

        var oo_graphic = this.createEl(oo_doc, "graphic", "a");
        var oo_graphicData = this.createEl(oo_doc, "graphicData", "a");
        this.setAttrs(oo_graphicData, {"uri": "http://schemas.microsoft.com/office/word/2010/wordprocessingShape"}, false);
        var oo_wsp = this.createEl(oo_doc, "wsp", "wps");

        this.insertChildren(oo_anchor, [oo_graphic, oo_graphicData, oo_wsp], "beforeend");

        var oo_cNvCnPr = this.createEl(oo_doc, "cNvCnPr", "wps");
        var oo_spPr = this.createEl(oo_doc, "spPr", "wps")

        var oo_xfrm = this.createEl(oo_doc, "xfrm", "a");
        var oo_off = this.createEl(oo_doc, "off", "a");
        this.setAttrs(oo_off, {"x": "0", "y": "0"}, false);
        var oo_ext = this.createEl(oo_doc, "ext", "a");
        this.setAttrs(oo_ext, {"cx": cx, "cy": "0"}, false);

        this.insertSiblings(oo_xfrm, [oo_off, oo_ext], "beforeend");

        var oo_prstGeom = this.createEl(oo_doc, "prstGeom", "a");
        this.setAttrs(oo_prstGeom, {"prst": "line"}, false);
        var oo_avLst = this.createEl(oo_doc, "avLst", "a");
        oo_prstGeom.appendChild(oo_avLst);

        var oo_ln = this.createEl(oo_doc, "ln", "a");

        var w = this.pxToPt(hr.clientHeight)*12700;
        this.setAttrs(oo_ln, {"w": w}, false);    //线的粗细 pt*12700
        var oo_solidFill = this.createEl(oo_doc, "solidFill", "a");
        var oo_srgbClr = this.createEl(oo_doc, "srgbClr", "a");

        var color = this.getColorHex(hr.get("color"));
        if (!color) color = this.getColorHex(hr.getStyle("background-color"));
        if (!color) color = "FF0000";
        this.setAttrs(oo_srgbClr, {"val": color}, false);    //line color
        oo_solidFill.appendChild(oo_srgbClr);
        oo_ln.appendChild(oo_solidFill);

        this.insertSiblings(oo_spPr, [oo_xfrm, oo_prstGeom, oo_ln], "beforeend");

        var oo_style = this.createEl(oo_doc, "style", "wps");
        //oo_style.innerHTML = '<a:lnRef idx="1"><a:schemeClr val="accent1"/></a:lnRef><a:fillRef idx="0"><a:schemeClr val="accent1"/></a:fillRef><a:effectRef idx="0"><a:schemeClr val="accent1"/></a:effectRef><a:fontRef idx="minor"><a:schemeClr val="tx1"/></a:fontRef>';

        var oo_lnRef = this.createEl(oo_doc, "lnRef", "a");
        this.setAttrs(oo_lnRef, {"idx": "1"}, false);
        var oo_schemeClr = this.createEl(oo_doc, "schemeClr", "a");
        this.setAttrs(oo_schemeClr, {"val": "accent1"}, false);
        oo_lnRef.appendChild(oo_schemeClr);

        var oo_fillRef = this.createEl(oo_doc, "fillRef", "a");
        this.setAttrs(oo_fillRef, {"idx": "0"}, false);
        var oo_schemeClr = this.createEl(oo_doc, "schemeClr", "a");
        this.setAttrs(oo_schemeClr, {"val": "accent1"}, false);
        oo_fillRef.appendChild(oo_schemeClr);

        var oo_effectRef = this.createEl(oo_doc, "effectRef", "a");
        this.setAttrs(oo_effectRef, {"idx": "0"}, false);
        var oo_schemeClr = this.createEl(oo_doc, "schemeClr", "a");
        this.setAttrs(oo_schemeClr, {"val": "accent1"}, false);
        oo_effectRef.appendChild(oo_schemeClr);

        var oo_fontRef = this.createEl(oo_doc, "fontRef", "a");
        this.setAttrs(oo_fontRef, {"idx": "minor"}, false);
        var oo_schemeClr = this.createEl(oo_doc, "schemeClr", "a");
        this.setAttrs(oo_schemeClr, {"val": "tx1"}, false);
        oo_fontRef.appendChild(oo_schemeClr);

        this.insertSiblings(oo_style, [oo_lnRef, oo_fillRef, oo_effectRef, oo_fontRef], "beforeend");

        var oo_bodyPr = this.createEl(oo_doc, "bodyPr", "wps");



        this.insertSiblings(oo_wsp, [oo_cNvCnPr, oo_spPr, oo_style, oo_bodyPr], "beforeend");


        // <a:graphic xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main">
        //     <a:graphicData uri="http://schemas.microsoft.com/office/word/2010/wordprocessingShape">
        //         <wps:wsp>
        //             <wps:cNvCnPr/>
        //             <wps:spPr>
        //                 <a:xfrm>
        //                     <a:off x="0" y="0"/>
        //                     <a:ext cx="5631180" cy="0"/>
        //                 </a:xfrm>
        //                 <a:prstGeom prst="line">
        //                     <a:avLst/>
        //                 </a:prstGeom>
        //                 <a:ln w="19050">
        //                     <a:solidFill>
        //                         <a:srgbClr val="FF0000"/>
        //                     </a:solidFill>
        //                 </a:ln>
        //             </wps:spPr>
        //             <wps:style>
        //                 <a:lnRef idx="1">
        //                     <a:schemeClr val="accent1"/>
        //                 </a:lnRef>
        //                 <a:fillRef idx="0">
        //                     <a:schemeClr val="accent1"/>
        //                 </a:fillRef>
        //                 <a:effectRef idx="0">
        //                     <a:schemeClr val="accent1"/>
        //                 </a:effectRef>
        //                 <a:fontRef idx="minor">
        //                     <a:schemeClr val="tx1"/>
        //                 </a:fontRef>
        //             </wps:style>
        //             <wps:bodyPr/>
        //         </wps:wsp>
        //     </a:graphicData>
        // </a:graphic>

        oo_p.appendChild(oo_run);
    },

    processRunFont: function(node, rPrs, font){
        //字体处理缩放
        var msoStyle = this.getMsoStyle(node);
        if (msoStyle["mso-font-width"]) rPrs.w = {"val": msoStyle["mso-font-width"].toFloat()};

        //处理字号
        if (msoStyle["mso-ansi-font-size"]) rPrs.sz = {"val": this.parseFontSize(msoStyle["mso-ansi-font-size"])*2};
        if (msoStyle["mso-hansi-font-size"]) rPrs.sz = {"val": this.parseFontSize(msoStyle["mso-hansi-font-size"])*2};
        if (msoStyle["mso-font-size"]) rPrs.sz = {"val": this.parseFontSize(msoStyle["mso-font-size"])*2};
        if (msoStyle["mso-fareast-font-size"]) rPrs.sz = {"val": this.parseFontSize(msoStyle["mso-fareast-font-size"])*2};

        //处理字体
        if (msoStyle["mso-ansi-font-family"]){
            if (!font) font = { "hint": "eastAsia" };
            font.ascii = this.parseFont(msoStyle["mso-ansi-font-family"]);
        }
        if (msoStyle["mso-hansi-font-family"]){
            if (!font) font = { "hint": "eastAsia" };
            font.hAnsi = this.parseFont(msoStyle["mso-hansi-font-family"]);
        }
        if (msoStyle["mso-font-family"]){
            if (!font) font = { "hint": "eastAsia" };
            font.eastAsia = this.parseFont(msoStyle["mso-font-family"]);
        }
        if (msoStyle["mso-fareast-font-family"]){
            if (!font) font = { "hint": "eastAsia" };
            font.eastAsia = this.parseFont(msoStyle["mso-fareast-font-family"]);
        }
    },
    parseFont: function(name){
        if (name.substr(0, 1)==="\""){
            return name.substr(1, name.length-2);
        }else{
            return name;
        }
    },
    parseFontSize: function(sz){
        var size = sz;
        if (size && o2.typeOf(size)==="string"){
            u = size.substring(size.length-2, size.length);
            if (u.toLowerCase()!=="pt"){
                size = Math.round(this.pxToPt(size));
            }
        }
        return size.toFloat();
    },
    processRun: function(span, oo_p, p, text, br){
        var rPrs = {"noProof": {}};
        var font = null;
        var styles = span.getStyles("font-size", "color", "letter-spacing", "font-weight", "font-family")
        var keys = Object.keys(styles);

        for (var i = 0; i<keys.length; i++){
            switch (keys[i]){
                case "font-size":
                    rPrs.sz = {"val": this.parseFontSize(styles["font-size"])*2};
                    break;
                case "color":
                    rPrs.color = {"val": this.getColorHex(styles["color"])};
                    break;
                case "letter-spacing":
                    //实际测试发现letter-spacing * 0.55 转换word比较合适
                    rPrs.spacing = {"val": (styles["letter-spacing"].toFloat()*20*0.55 || 0)};
                    break;
                case "font-weight":
                    var b = styles["font-weight"];
                    if (b.toLowerCase()=="normal"){
                        //nothing
                    }else if (b.toLowerCase()=="bold") {
                        rPrs.b = {"val": "true"};
                    }else{
                        var n = b.toFloat();
                        if (n>=600) rPrs.b = {"val": "true"};
                    }
                    break;
                case "font-family":
                    var fonts = styles["font-family"].split(/,\s*/);
                    font = {
                        "hint": "eastAsia",
                        "eastAsia": this.parseFont(fonts[fonts.length-1])
                    }
                    if (fonts.length>1) font.other = this.parseFont(fonts[0]);
                    break;
                default:
                //nothing
            }
        }
        this.processRunFont(p, rPrs, font);
        this.processRunFont(span, rPrs, font);


        var oo_run = this.createRun(oo_p.ownerDocument, {"rPrs": rPrs, "font": font, "text": text, "br": br});
        oo_p.appendChild(oo_run);
    },


    processPageSection: function(dom_pageRule, oo_body){
        var oo_sectPr = this.getOrCreateEl(oo_body, "sectPr");
        if (oo_sectPr){
            for (var i = 0; i<dom_pageRule.style.length; i++){
                switch (dom_pageRule.style[i]){
                    case "size":
                        var v = dom_pageRule.style["size"].split(/\s/);
                        var w = v[0].toFloat()*20, h=v[1].toFloat()*20;
                        var oo_pgSz = this.getOrCreateEl(oo_sectPr, "pgSz");
                        this.setAttrs(oo_pgSz, {"w": w, "h": h});
                        break;
                    case "margin-top":
                    case "margin-right":
                    case "margin-bottom":
                    case "margin-left":
                        var p = dom_pageRule.style[i].split("-")[1];
                        var v = dom_pageRule.style["margin"+p.capitalize()].toFloat()*20;
                        var oo_pgMar = this.getOrCreateEl(oo_sectPr, "pgMar");
                        var attrs = {};
                        attrs[p] = v
                        this.setAttrs(oo_pgMar, attrs);
                        break;
                    case "line-height":
                    case "letter-spacing":
                        var oo_docGrid = this.getOrCreateEl(oo_sectPr, "docGrid");
                        var lh = dom_pageRule.style["lineHeight"].toFloat()*20;
                        var cs = dom_pageRule.style["letterSpacing"].toFloat()*4096;
                        var attrs = {"type": "linesAndChars"};
                        if (lh) attrs["linePitch"] = lh;
                        //if (cs) attrs["charSpace"] = cs;
                        this.setAttrs(oo_docGrid, attrs);
                        break;
                    default:
                    //nothing
                }

            }
        }

    },

    getOrCreateEl: function(el, tag){
        var node = this.getEl(el, tag);
        if (!node){
            node = this.createEl(el.ownerDocument, tag);
            el.appendChild(node);
        }
        return node;
    },

    createParagraph: function(xmlDoc, options){
        var p = this.createEl(xmlDoc,"p");
        var pPr = this.createEl(xmlDoc,"pPr");

        /*
        * //如：对齐方式描述如下
        * {
        *   "jc": { val: "both" },
        * }
        * */
        if (options && options.pPrs){
            Object.keys(options.pPrs).each(function(k){
                var node = this.createEl(xmlDoc, k);
                this.setAttrs(node, options.pPrs[k]);
                pPr.appendChild(node);
            }.bind(this));
        }

        p.appendChild(pPr);
        return p;
    },
    createRun: function(xmlDoc, options){
        var r = this.createEl(xmlDoc, "r");
        var rPr = this.createEl(xmlDoc,"rPr");
        r.appendChild(rPr);

        if (options && options.text){
            var t = this.createEl(xmlDoc,"t");
            t.appendChild(xmlDoc.createTextNode(options.text));
            r.appendChild(t);
        }
        if (options && options.br){
            var oo_br = this.createEl(xmlDoc,"br");
            r.appendChild(oo_br);
        }
        if (options && options.font){
            var rFonts = this.createEl(xmlDoc,"rFonts");
            var font = {
                "eastAsia": options.font.eastAsia || options.font.font,
                "ascii": options.font.ascii || options.font.other || options.font.eastAsia || options.font.font,
                "hAnsi": options.font.hAnsi || options.font.other || options.font.eastAsia || options.font.font,
            }
            if (options.font.hint) font.hint = options.font.hint;
            this.setAttrs(rFonts, font);
            rPr.appendChild(rFonts);
        }
        /*
        * //如：粗体和字体颜色描述如下
        * {
        *   "b": { val: "true" },
        *   "color": { val: "FF0000" }
        * }
        * */
        if (options && options.rPrs){
            Object.keys(options.rPrs).each(function(k){
                var node = this.createEl(xmlDoc, k);
                this.setAttrs(node, options.rPrs[k]);
                rPr.appendChild(node);
            }.bind(this));
        }

        return r;
    },

    insertChildren: function(p, els, position){
        this.insertAdjacent(p, els, position, "beforeend");
    },
    insertSiblings: function(p, els, position){
        this.insertAdjacent(p, els, position, "afterend");
    },
    insertAdjacent: function(p, els, posFirst, posNext){
        var pos = posFirst || "beforeend";
        var posMapping = {
            'beforebegin': "before",
            'afterbegin': "top",
            'beforeend': "bottom",
            'afterend': "after"
        };
        els.each(function(e){
            (p.insertAdjacentElement) ? p.insertAdjacentElement(pos, e) : e.inject(p, posMapping[pos]);
            p = e;
            pos = posNext || "afterend";
        });
    },
    getEl: function(el, tag, ns){
        return this.getEls(el, tag, ns)[0];
    },
    getEls: function(el, tag, ns){
        var n = ns || "w";
        return el.getElementsByTagNameNS(this.nsResolver(n), tag);
    },
    createEl: function(xmlDoc, tag, ns){
        var n = ns || "w";
        if (false && Browser.name==="ie"){
            return xmlDoc.createElement(n+":"+tag);
        }
        return xmlDoc.createElementNS(this.nsResolver(n), n+":"+tag);
    },
    setAttr: function(node, name, value, ns){
        if (ns===false){
            node.setAttribute(name, value);
        }else{
            var n = ns || "w";
            if (false && Browser.name==="ie") {
                node.setAttribute(n+":"+name, value);
            }else{
                node.setAttributeNS(this.nsResolver(n), n+":"+name, value);
            }
        }
    },
    setAttrs: function(node, attrs, ns){
        if (ns===false){
            Object.keys(attrs).forEach(function(key){
                node.setAttribute(key, attrs[key]);
            });
        }else{
            var n = this.nsResolver(ns || "w");
            Object.keys(attrs).forEach(function(key){
                if (false && Browser.name==="ie") {
                    node.setAttribute((ns || "w")+":"+key, attrs[key]);
                }else{
                    node.setAttributeNS(n, (ns || "w")+":"+key, attrs[key]);
                }

            });
        }
    },
    nsResolver: function(prefix){
        var ns = {
            "w": "http://schemas.openxmlformats.org/wordprocessingml/2006/main",
            "wpc": "http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas",
            "cx": "http://schemas.microsoft.com/office/drawing/2014/chartex",
            "cx1": "http://schemas.microsoft.com/office/drawing/2015/9/8/chartex",
            "cx2": "http://schemas.microsoft.com/office/drawing/2015/10/21/chartex",
            "cx3": "http://schemas.microsoft.com/office/drawing/2016/5/9/chartex",
            "cx4": "http://schemas.microsoft.com/office/drawing/2016/5/10/chartex",
            "cx5": "http://schemas.microsoft.com/office/drawing/2016/5/11/chartex",
            "cx6": "http://schemas.microsoft.com/office/drawing/2016/5/12/chartex",
            "cx7": "http://schemas.microsoft.com/office/drawing/2016/5/13/chartex",
            "cx8": "http://schemas.microsoft.com/office/drawing/2016/5/14/chartex",
            "mc": "http://schemas.openxmlformats.org/markup-compatibility/2006",
            "aink": "http://schemas.microsoft.com/office/drawing/2016/ink",
            "am3d": "http://schemas.microsoft.com/office/drawing/2017/model3d",
            "o": "urn:schemas-microsoft-com:office:office",
            "r": "http://schemas.openxmlformats.org/officeDocument/2006/relationships",
            "m": "http://schemas.openxmlformats.org/officeDocument/2006/math",
            "v": "urn:schemas-microsoft-com:vml",
            "wp14": "http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing",
            "wp": "http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing",
            "w10": "urn:schemas-microsoft-com:office:word",
            "w14": "http://schemas.microsoft.com/office/word/2010/wordml",
            "w15": "http://schemas.microsoft.com/office/word/2012/wordml",
            "w16cex": "http://schemas.microsoft.com/office/word/2018/wordml/cex",
            "w16cid": "http://schemas.microsoft.com/office/word/2016/wordml/cid",
            "w16": "http://schemas.microsoft.com/office/word/2018/wordml",
            "w16sdtdh": "http://schemas.microsoft.com/office/word/2020/wordml/sdtdatahash",
            "w16se": "http://schemas.microsoft.com/office/word/2015/wordml/symex",
            "wpg": "http://schemas.microsoft.com/office/word/2010/wordprocessingGroup",
            "wpi": "http://schemas.microsoft.com/office/word/2010/wordprocessingInk",
            "wne": "http://schemas.microsoft.com/office/word/2006/wordml",
            "wps": "http://schemas.microsoft.com/office/word/2010/wordprocessingShape",
            "a": "http://schemas.openxmlformats.org/drawingml/2006/main"
        };
        return ns[prefix] || null;
    },
    getColorHex: function(clr){
        if (!clr) return "";
        var colorKeys = {
            "black": "000000",
            "silver": "c0c0c0",
            "gray": "808080",
            "white": "ffffff",
            "maroon": "800000",
            "red": "ff0000",
            "purple": "800080",
            "fuchsia": "ff00ff",
            "green": "008000",
            "lime": "00ff00",
            "olive": "808000",
            "yellow": "ffff00",
            "navy": "000080",
            "blue": "0000ff",
            "teal": "008080",
            "aqua": "00ffff",
            "initial": "000000"
        }
        if (colorKeys[clr]) return colorKeys[clr];

        var f = clr.substr(0,1);
        if (f==="#") return clr.replace("#", "");
        return clr.rgbToHex() || clr;
    }
});
