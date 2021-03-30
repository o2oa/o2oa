MWF.xApplication.Profile.Common = {};
MWF.xApplication.Profile.Common.Pagination = new Class({
    Implements: [Events, Options],
    options: {
        "page": 1,
        "pageSize": 10,
        "showNumber": true,
        "showText": false
    },
    initialize: function (target, options) {
        this.setOptions(options);
        if (typeof (target) == "string") {
            this.target = $(target);
        } else {
            this.target = target;
        }
        this.total = this.options.total;

        this.length = this.options.length;
        this.currentPage = 0;
        this.pageSize = this.options.pageSize;
        this.pageNode = new Element('div');
        this.target.empty();
        this.pageNode.inject(this.target);
    },
    create: function (panel) {
        panel.empty();
        if(this.page==1){
            return;
        }
        if (this.currentPage > 0) {
            var prev = new Element('div', {
                'text': '',
                'class': "pagination-previous page",
                //'href': 'javascript:void(null)',
                'events': {
                    'click': this.click.bind(this, this.currentPage - 1)
                }
            });
            panel.grab(prev);
        }
        if (this.options.showNumber) {
            var beginInx = this.currentPage - 2 < 0 ? 0 : this.currentPage - 2;
            var endIdx = this.currentPage + 2 > this.page ? this.page : this.currentPage + 2;
            if (beginInx > 0) panel.grab(this.createNumber(0));
            if (beginInx > 1) panel.grab(this.createNumber(1));
            if (beginInx > 2) panel.grab(this.createSplit());
            for (var i = beginInx; i < endIdx; i++) {
                panel.grab(this.createNumber(i));
            }
            if (endIdx < this.page - 2) panel.grab(this.createSplit());
            if (endIdx < this.page - 1) panel.grab(this.createNumber(this.page - 2));
            if (endIdx < this.page) panel.grab(this.createNumber(this.page - 1));
        }
        if (this.currentPage < this.page - 1) {
            var next = new Element('div', {
                'text': '',
                'class': 'pagination-next page',
                //'href': 'javascript:void(null)',
                'events': {
                    'click': this.click.bind(this, this.currentPage + 1)
                }
            });
            panel.grab(next);
        }
        if (this.options.showText) panel.grab(this.createText());
    },
    createNumber: function (i) {
        var a = new Element('div', {
            'text': i + 1,
            "class": "pagination-link page",
            //s'href': 'javascript:void(null)',
            'events': {'click': this.click.bind(this, i)}
        });
        if (i === this.currentPage) {
            a.set('class', "pagination-link page is-current mainColor_bg");
        }
        return a;
    },
    createSplit: function () {
        return new Element('div.page', {'text': '...'});
    },
    createText: function () {
        var text = MWF.xApplication.Profile.LP.pageText.replace("{n}",  (this.currentPage + 1) + '/' + (this.page) );
        return new Element('span', {
            'text': text,
            'style': "margin:0 3px;"
        });
    },
    click: function (index) {
        this.currentPage = index;
        this.load();
        this.fireEvent('afterLoad', [this.currentPage + 1]);
    },
    load: function () {
        this.fireEvent('beforeLoad');
        this.page = !this.total?(this.currentPage + Math.ceil((this.length)+1 / this.pageSize)):Math.ceil(this.total / this.pageSize);

        this.create(this.pageNode);

    },
    reload: function (param) {
        this.currentPage = 0;
        this.load();
    },
    setpageSize: function (pageSize) {
        this.pageSize = pageSize;
        this.reload();
    }
});


MWF.xApplication.Profile.Common.Content = new Class({
    Implements: [Events, Options],
    options: {
        "pageSize": 10,
        "page":1,
        "pagination": true,
        "search": false,
        "columns": [],
        "title": "",
        "searchItemList": []
    },
    initialize: function (target, options) {
        this.setOptions(options);
        this.searchItemList = this.options.searchItemList;
        this.opButtonList = this.options.opButtonList;
        this.columns = this.options.columns;
        this.contentNode = target;
        this.contentNode.empty();
    },
    load: function () {

        //this.createNavi();
        //this.createSearch();
        //this.createOp();
        this.createTable();
        this.createPagination();
    },
    reload: function (param) {
        this.currentPage = 0;
        this.load();
    },
    createPagination: function () {
        if (this.paginationNode) this.paginationNode.remove();
        this.paginationNode = new Element("div", {
            "class": "pagination ",
            "style": "padding: 10px;"
        }).inject(this.contentNode);

        new MWF.xApplication.Profile.Common.Pagination(this.paginationNode, {
            "total": this.total,
            "length": this.dataList.length,
            "pageSize": this.options.pageSize,
            "showNumber": true,
            "showText": false,
            "onAfterLoad": function (data) {

                this.options.page = data;
                this.createTbody();

            }.bind(this)
        }).load();

    },
    createOp: function () {
        this.opNode = new Element("div", {
            "class": "container is-fluid buttons",
            "style": "margin-left:10px;margin-bottom:0px"
        }).inject(this.contentNode);

        this.opButtonList.each(function (opButton) {
            var opButtonNode = new Element("a", {
                "class": "button is-link",
                "text": opButton.text
            }).inject(this.opNode);
            if (opButton.action) {
                opButtonNode.addEvent("click", function (ev) {
                    opButton.action(ev, this);
                }.bind(this));
            }
        }.bind(this));

    },
    createNavi: function () {
        this.subNaviNode = new Element("div", {
            "style": "background-color:#3273dc;color:#fff;padding:0.5em 0.5em;margin-bottom:0.5em;display:block",
            "html": this.options.title
        }).inject(this.contentNode);
    },
    createSearch: function () {
        this.searchItemNode = new Element("div", {
            "style": "padding: 0.5rem"
        }).inject(this.contentNode);
        this.searchItemList.each(function (searchItem) {
            var itemNode;

            if (searchItem.type === "date" || searchItem.type === "input") {
                itemNode = new Element("input", {
                    "class": "input control",
                    "name": searchItem.name,
                    "placeholder": searchItem.title,
                    "style": "width:200px;margin:5px;"
                }).inject(this.searchItemNode);
                if (searchItem.type === "date") new MWF.widget.Calendar(itemNode, {
                    "style": "xform",
                    "onComplate": function () {
                    }
                });
            }
            if (searchItem.type === "select") {
                var itemParentNode = new Element("div", {"class": "select"}).inject(this.searchItemNode);
                itemNode = new Element("select", {
                    "style": "width:200px;margin:5px;",
                    "name": searchItem.name
                }).inject(itemParentNode);

                searchItem.options.each(function (option) {
                    new Element("option", {
                        "text": option.split("|")[0],
                        "value": option.split("|").length > 1 ? option.split("|")[1] : option.split("|")[0]
                    }).inject(itemNode);
                });
            }
            if (searchItem.click) {
                itemNode.addEvent("click", function (ev) {
                    searchItem.click(ev, itemNode);
                });
            }
        }.bind(this));
        this.searchOkButton = new Element("a", {
            "class": "button is-link",
            "style": "margin:5px;",
            "text": MWF.xApplication.Profile.LP.search
        }).inject(this.searchItemNode).addEvent("click", function () {

            this.options.page = 1;
            this.createTbody();
            this.createPagination();
        }.bind(this));
        this.searchCancleButton = new Element("a", {
            "class": "button",
            "style": "margin:5px;",
            "text": MWF.xApplication.Profile.LP.cancel1
        }).inject(this.searchItemNode).addEvent("click", function () {
            this.searchItemList.each(function (item) {
                $$('[name=' + item.name + ']').set("value", "");
            }.bind(this));

            this.options.page = 1;
            this.createTbody();
            this.createPagination();
        }.bind(this));
    },
    getMtSelects: function () {
        var arr = [];
        $$('input[name="mtSelectItem"]').each(function (item) {
            if (item.checked) {
                arr.push(item.getParent().getParent().retrieve("data"));
            }
        });
        return arr;
    },
    createTable: function () {
        this.tableDivNode = new Element("div.profile_common_tableDiv").inject(this.contentNode);
        this.tableNode = new Element("table", {
            "class": "table is-fullwidth is-hoverable emPowerTable",
            "style": "margin-bottom:0px",
            "width":"100%",
            "border":"0",
            "cellpadding":"0",
            "cellspacing":"0"
        }).inject(this.tableDivNode);
        this.createThead();
        this.createTbody();
    },
    createThead: function () {

        this.theadNode = new Element("thead").inject(this.tableNode);

        var trNode = new Element("tr.first").inject(this.theadNode);

        this.columns.each(function (column) {

            var thNode = new Element("th").inject(trNode);
            if (column.title) thNode.set("text", column.title);
            if (column.width) thNode.setStyle("width", column.width);
            if (column.type) {
                if (column.type === "checkbox") {
                    var checkAllNode = new Element("input", {"type": "checkbox", "name": "mtSelectAll"}).inject(thNode);
                    checkAllNode.addEvent("click", function () {

                        $$('input[name="mtSelectItem"]').each(function (item) {
                            if (checkAllNode.checked) {
                                item.checked = 'checked';
                            } else {
                                item.checked = '';
                            }
                        });
                    });
                }
            }
        });
    },
    createTbody: function () {

        if (this.tbodyNode) this.tbodyNode.remove();
        this.tbodyNode = new Element("tbody").inject(this.tableNode);
        this.fireEvent('beforeLoadData');
        this.dataList.each(function (data) {

            var trNode = new Element("tr").inject(this.tbodyNode);
            trNode.store("data", data);
            this.columns.each(function (column) {
                var thNode = new Element("td").inject(trNode);

                if(column.formatter){
                    column.formatter(data,thNode);
                }else{
                    thNode.set("text",data[column.field])
                }

                if (column.type) {
                    if (column.type === "operation") {
                        column.opButtonList.each(function (opButton) {
                            var opButtonNode = new Element("a", {
                                "class": "button is-link",
                                "text": opButton.text
                            }).inject(thNode);
                            if (opButton.action) {
                                opButtonNode.addEvent("click", function (ev) {
                                    opButton.action(data);
                                }.bind(this));
                            }
                        }.bind(this));
                    }
                    if (column.type === "checkbox") {
                        var checkItemNode = new Element("input", {
                            "type": "checkbox",
                            "name": "mtSelectItem"
                        }).inject(thNode);

                        checkItemNode.addEvent("click", function (event) {
                            if (event.target.checked) {
                                var i = 0;
                                $$('input[name="mtSelectItem"]').each(function (chk) {
                                    if (!chk.checked) {
                                        i = i + 1;
                                    }
                                });
                                if (i === 0) {
                                    $$('input[name="mtSelectAll"]')[0].checked = 'checked';
                                }
                            } else {
                                $$('input[name="mtSelectAll"]')[0].checked = '';
                            }
                        });
                    }
                }

            }.bind(this));
        }.bind(this));
    }
});