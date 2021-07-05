o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.widget.Paging = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        style : "default",
        countPerPage: 20,
        visiblePages: 10,
        currentPage: 1,
        itemSize: 0,
        pageSize: 0,
        hasFirstPage : true,
        hasLastPage : true,
        hasNextPage: true,
        hasPrevPage: true,
        hasBatchTuring : true,
        hasTruningBar: true,
        hasJumper: true,
        hiddenWithDisable: false,
        hiddenWithNoItem: true,
        text: {
            preBatchTuring : "...",
            nextBatchTuring : "...",
            prePage: "",
            nextPage: "",
            firstPage: "",
            lastPage: ""
        }
    },
    initialize: function (node, options, css) {

        this.setOptions(options || {});
        this.container = $(node);

        this.path = o2.session.path + "/widget/$Paging/";
        this.cssPath = o2.session.path + "/widget/$Paging/" + this.options.style + "/css.wcss";
        this._loadCss();

        if (css) {
            this.css = Object.clone(this.css);
            this.css = Object.merge(this.css, css);
        }
    },
    load: function () {
        this.fireEvent("queryLoad", this);
        this.options.pageSize = Math.ceil(this.options.itemSize / this.options.countPerPage);

        if ( this.options.pageSize == 0 && this.options.hiddenWithNoItem) return;

        this.container.empty();
        this.createNode();

        this.fireEvent("postLoad", this);
    },
    destroy: function(){
        this.container.empty();
    },
    createNode: function() {
        var _self = this;

        this.wraper = new Element("div.pagingBarWraper", {styles: this.css.pagingBarWraper}).inject(this.container);

        this.node = new Element("div.pagingBar", {styles: this.css.pagingBar}).inject(this.wraper);

        var i, max, min;

        var showWithDisable = !this.options.hiddenWithDisable;
        var pageSize = this.options.pageSize;
        var currentPage = this.options.currentPage;
        var visiblePages = this.options.visiblePages;

        var halfCount = Math.floor(visiblePages / 2);
        if (pageSize <= visiblePages) {
            min = 1;
            max = pageSize;
        } else if (currentPage + halfCount > pageSize) {
            min = pageSize - visiblePages;
            max = pageSize;
        } else if (currentPage - halfCount < 1) {
            min = 1;
            max = visiblePages;
        } else {
            min = currentPage - halfCount;
            max = currentPage + halfCount;
        }

        if( this.options.hasFirstPage && (min > 1 || showWithDisable) ){
            this.createFirst();
        }

        if (this.options.hasPrevPage && ( currentPage != 1 || showWithDisable ) ){
            this.createPrev();
        }

        if( this.options.hasTruningBar && this.options.hasBatchTuring && ( min > 1 ) ){ //showWithDisable
            this.createPrevBatch( min );
        }

        if( this.options.hasTruningBar ){
            this.pageTurnContainer = new Element("div", {
                styles : this.css.pageTurnContainer
            }).inject( this.node );
            this.pageTurnNodes = [];
            for (i = min; i <= max; i++) {
                if (currentPage == i) {
                    this.currentPage = new Element("div.currentPage", {
                        "styles": this.css.currentPage,
                        "text" : i
                    }).inject(this.pageTurnContainer);
                } else {
                    this.pageTurnNodes.push( this.createPageTurnNode(i) );
                }
            }
        }

        if( this.options.hasTruningBar && this.options.hasBatchTuring && ( max < pageSize )){ //showWithDisable
            this.createNextBatch( max );
        }

        if (this.options.hasNextPage && ( currentPage != pageSize || showWithDisable )){
            this.createNext();
        }

        if(this.options.hasLastPage && ( max < pageSize || showWithDisable ) ){
            this.createLast();
        }

        if (this.options.hasJumper) {
            this.createPageJumper();
        }
    },
    createFirst : function(){
        var firstPage = this.firstPage = new Element("div.firstPage", {
            styles: this.css.firstPage
        }).inject(this.node);
        if (this.options.text.firstPage) firstPage.set("text", this.options.text.firstPage);
        firstPage.addEvents({
            "mouseover": function (ev) {
                ev.target.setStyles(this.css.firstPage_over)
            }.bind(this),
            "mouseout": function (ev) {
                ev.target.setStyles(this.css.firstPage)
            }.bind(this),
            "click": function () {
                this.gotoPage(1)
            }.bind(this)
        })
    },
    createLast : function(){
        var lastPage = this.lastPage = new Element("div.lastPage", {
            styles: this.css.lastPage
        }).inject(this.node);
        if (this.options.text.lastPage) lastPage.set("text", this.options.text.lastPage);
        lastPage.addEvents({
            "mouseover": function (ev) {
                ev.target.setStyles(this.css.lastPage_over)
            }.bind(this),
            "mouseout": function (ev) {
                ev.target.setStyles(this.css.lastPage)
            }.bind(this),
            "click": function () {
                this.gotoPage( this.options.pageSize )
            }.bind(this)
        })
    },
    createPrev : function(){
        var prePage = this.prePage = new Element("div.prePage", {
            styles: this.css.prePage
        }).inject(this.node);
        if (this.options.text.prePage) prePage.set("text", this.options.text.prePage);
        prePage.addEvents({
            "mouseover": function (ev) {
                ev.target.setStyles(this.css.prePage_over)
            }.bind(this),
            "mouseout": function (ev) {
                ev.target.setStyles(this.css.prePage)
            }.bind(this),
            "click": function () {
                this.gotoPage(this.options.currentPage - 1)
            }.bind(this)
        });
    },
    createNext : function(){
        var nextPage = this.nextPage = new Element("div.nextPage", {
            styles: this.css.nextPage
        }).inject(this.node);
        if (this.options.text.nextPage) nextPage.set("text", this.options.text.nextPage);
        nextPage.addEvents({
            "mouseover": function (ev) {
                ev.target.setStyles(this.css.nextPage_over)
            }.bind(this),
            "mouseout": function (ev) {
                ev.target.setStyles(this.css.nextPage)
            }.bind(this),
            "click": function () {
                this.gotoPage(this.options.currentPage + 1)
            }.bind(this)
        });
    },
    createPageTurnNode: function(i){
        var pageTurnNode = new Element("div.pageItem", {
            "styles": this.css.pageItem,
            "text": i
        }).inject(this.pageTurnContainer);
        pageTurnNode.addEvents({
            "mouseover": function (ev) {
                ev.target.setStyles(this.css.pageItem_over)
            }.bind(this),
            "mouseout": function (ev) {
                ev.target.setStyles(this.css.pageItem)
            }.bind(this),
            "click": function () {
                this.obj.gotoPage(this.num)
            }.bind({obj: this, num: i})
        });
        return pageTurnNode;
    },
    createPrevBatch : function( min ){
        this.preBatchPage = new Element("div.prePage", {
            styles: this.css.preBatchPage
        }).inject(this.node);
        if (this.options.text.preBatchTuring ) this.preBatchPage.set("text", this.options.text.preBatchTuring);
        this.preBatchPage.addEvents({
            "mouseover": function (ev) {
                ev.target.setStyles(this.css.preBatchPage_over)
            }.bind(this),
            "mouseout": function (ev) {
                ev.target.setStyles(this.css.preBatchPage )
            }.bind(this),
            "click": function () {
                var page;
                if( this.options.visiblePages % 2 == 1 ){
                    page = min - Math.ceil( this.options.visiblePages / 2 );
                }else{
                    page = min - Math.ceil( this.options.visiblePages / 2 ) - 1;
                }
                if( page < 1 )page = 1;
                this.gotoPage( page );
            }.bind(this)
        });
    },
    createNextBatch : function( max ){
        this.nextBatchPage = new Element("div.prePage", {
            styles: this.css.nextBatchPage
        }).inject(this.node);
        if (this.options.text.nextBatchTuring ) this.nextBatchPage.set("text", this.options.text.nextBatchTuring);
        this.nextBatchPage.addEvents({
            "mouseover": function (ev) {
                ev.target.setStyles(this.css.nextBatchPage_over);
            }.bind(this),
            "mouseout": function (ev) {
                ev.target.setStyles(this.css.nextBatchPage );
            }.bind(this),
            "click": function () {
                var page;
                if( this.options.visiblePages % 2 == 1 ){
                    page = max + Math.ceil( (this.options.visiblePages) / 2 );
                }else{
                    page = max + Math.ceil( (this.options.visiblePages) / 2 ) + 1;
                }
                if( page > this.options.pageSize )page = this.options.pageSize;
                this.gotoPage( page );
            }.bind(this)
        });
    },
    createPageJumper : function(){
        var _self = this;
        var pageJumper = this.pageJumper = new Element("input.pageJumper", {
            "styles": this.css.pageJumper,
            "title": o2.LP.widget.pageJumperTitle,
        }).inject(this.node);
        this.pageJumperText = new Element("div.pageText", {
            "styles": this.css.pageJumperText,
            "text": "/" + this.options.pageSize
        }).inject(this.node);
        pageJumper.addEvents({
            "focus": function (ev) {
                ev.target.setStyles(this.css.pageJumper_over)
            }.bind(this),
            "blur": function (ev) {
                var value = this.value;
                _self.pageJumper.set("value","");
                if( value )_self.gotoPage(value);
                ev.target.setStyles(_self.css.pageJumper);
            },
            "keyup": function (e) {
                this.value = this.value.replace(/[^0-9_]/g, '');
            },
            "keydown": function (e) {
                if (e.code == 13 && this.value != "") {
                    var value = this.value;
                    _self.pageJumper.set("value","");
                    _self.gotoPage(value);
                    e.stopPropagation();
                    //e.preventDefault();
                }
            }
        });
    },
    gotoPage: function (num) {
        if( typeOf(num) === "string" )num = num.toInt();
        if (num < 1 || num > this.options.pageSize) return;
        this.fireEvent("jumpingPage", [num]);
        this.options.currentPage = num;
        this.load();
    },
    gotoItem: function (itemNum) {
        var pageNum = Math.ceil(itemNum / this.options.countPerPage);
        var index = itemNum % this.options.countPerPage;
        this.fireEvent("jumpingPage", [pageNum, itemNum, index]);
        this.options.currentPage = pageNum;
        this.load();
    }
});