/**
 @Name：表格插件
 @Author：ray
 @License：MIT
 */
layui.define(["jquery"], function (exports, undef) {
    "use strict";
    
    var $ = layui.$,
        win = window,
        doc = document,
        tableCache = {
            tables: {},
            datas: {},
            cols: {},
            jqobjs: {}
        }, tableindex = 0, scrollGap = {}, $win = $(win), timer = null;
    $win.resize(function () {
        if (timer != null) clearTimeout(timer);
        timer = setTimeout(function () {
            timer = null;
            for (var t in tableCache.tables) {
                var that = tableCache.tables[t],
                    data = tableCache.datas[that.tableIndex];
                if (!data || data.length === 0) continue;
                TableClass.utils.modifyStyle.call(that);
            }
        }, 20);
    });

    var TableClass = function (table, option) {
        this.elem = table;
        this.tableIndex = tableindex;
        var globalOptions = $.extend({}, TableClass.option);

        //option.page:true
        typeof option.page === "boolean" && option.page && (option.page = {});
        option.page && !globalOptions.page && (globalOptions.page = {});
        //option.page:false或则object
        this.options = $.extend(true, {}, TableClass.option2, globalOptions, option);
        //允许分页但没有设置limits
        this.options.page && !this.options.page.limits && (this.options.page.limits = [10, 20, 30, 40, 50, 60, 70, 80, 90]);
        //初始化
        TableClass.utils.init.call(this);
        return this;
    };
    //可被外部全局配置
    TableClass.option = {
        expandRow: false,//是否支持展开行
        nowrap: true,//是否自动换行，默认不换行
        minHeight: 80,//表格最小高度
        singleSelect: false,//可以选择多行
        localSort: true,//本地排序，默认true，如果不想使用本地排序，请配置成false
        cellMinWidth: 60, //所有单元格默认最小宽度
        loading: true, //请求数据时，是否显示loading
        even: true, //隔行换色，可选true|false，默认false
        serialField: "id"
/*        page: {
            align: 'right',
            groups: 5,//显示连续页码数量
             curr:1,
            count: -1,//总条数
            limit:10,
            limits:[10, 20, 30, 40, 50, 60, 70, 80, 90]
        }*/
    };
    //不可被外部全局配置
    TableClass.option2 = {
        version: "1.4",
/*        page: {
            align: 'right',//对齐方式，可选left|right | center，默认left
            groups: 5,//显示连续页码数量
            limit: 10, //每页显示的条数
            curr: 1,//当前页
            count: -1//总条数
        }*/
    };

    $.rayTableOptions = TableClass.option;

    //日期+时间
    var regOnlyTime = /^(20|21|22|23|(|[01])\d):(|[0-5])\d(|:(|[0-5])\d)$/;
    var regDateTime = /^[1-9]\d{3}\/((|0)[1-9]|1[0-2])\/((|0)[1-9]|[1-2][0-9]|3[0-1])(|\s(20|21|22|23|(|[01])\d):(|[0-5])\d(|:(|[0-5])\d))$/;

    Array.prototype.contains = function (needle) {
        for (var i in this) {
            if (this[i] === needle) return true;
        }
        return false;
    }
    Array.prototype.raysort = function (key, desc) {
        if (!key) return this;
        this.sort(function (o1, o2) {
            var v1 = o1[key], v2 = o2[key];
            //把空值放在前面
            if (v1 && !v2) {
                return 1;
            } else if (!v1 && v2) {
                return -1;
            }

            //类型不一致
            var type_v1 = typeof v1, type_v2 = typeof v2;
            if (type_v1 !== type_v2) {
                return v1 > v2 ? 1 : -1;
            }

            //return>0，交换位置
            //数字
            if (type_v1 === "number")
                return v1 - v2;

            //字符串数字
            if (!isNaN(v1) && !isNaN(v2))
                return parseFloat(v1) - parseFloat(v2);
            //时间
            var tmpV1 = v1.replace(/-/g, '/'), tmpV2 = v2.replace(/-/g, '/');
            var isdate_v1 = regDateTime.test(tmpV1) || regOnlyTime.test(tmpV1);
            var isdate_v2 = regDateTime.test(tmpV2) || regOnlyTime.test(tmpV2);
            if (isdate_v1 && isdate_v2) {
                return new Date(tmpV1) - new Date(tmpV2);
            }

            //字符
            return v1 > v2 ? 1 : -1;
        });

        desc && this.reverse(); //倒序
        return this;
    }

    var serialnum = 0;
    function getNum() {
        return serialnum++;
    }

    TableClass.utils = {
        //只运行一次
        initDefault: function () {
            //计算浏览横向滚动条的高度和竖向滚动条的宽度
            var $div = $("<div/>").appendTo($("body"));
            $div.width(100).height(100).css("overflow", "scroll");
            var elem = $div[0];
            scrollGap.width = elem.offsetHeight - elem.clientHeight;
            scrollGap.height = elem.offsetWidth - elem.clientWidth;
            $div.remove();
        },
        init: function () {
            var that = this,
                $t = $(this.elem),
                options = that.options;

            if (options.data && options.data instanceof Array)
                tableCache.datas[that.tableIndex] = options.data;
            else
                tableCache.datas[that.tableIndex] = [];

            var addExpand = false, cols = tableCache.cols[that.tableIndex] = [];
            options.colsCount = 0;
            options.autoColNums = 0;
            options.minWidth = 0;
            options.autoCols = {};
debugger
            var cellcss = options.cellcss = {}, othercss = {},
                tableHead = (function () {
                    var str = [];
                    str.push('<table class="laytable-head"><thead>');
                    $(options.cols).each(function (a1, list) {
                        str.push('<tr>');
                        //是否支持展开
                        if (!addExpand && options.expandRow) {
                            addExpand = true;
                            str.push('<th rowspan="' + options.cols.length + '"><span class="laytable-expand"></span></th>');
                            options.minWidth += 37;//左右padding各10px+内容16px+右边框1px
                        }
                        $(list).each(function (a, b) {
                            //表头和数据共有样式
                            var cellclass = "laytable-cell-" + that.tableIndex + "-" + (b.field ? b.field : getNum());
                            var curCellCss = cellcss[cellclass] = {};
                            //表头特有样式
                            var headclass = "laytable-cell-head-" + that.tableIndex + "-" + (b.field ? b.field : getNum());
                            var curheadCellCss = othercss[headclass] = {};
                            //数据特有样式
                            var curDataCellCss;
                            if (b.field) {
                                var dataclass = "laytable-cell-data-" + that.tableIndex + "-" + (b.field ? b.field : getNum());
                                curDataCellCss = othercss[dataclass] = {};
                            }

                            b.field && cols.push(b);
                            //计算css样式宽度
                            if (!b.hidden && b.field) {
                                if (b.width) {
                                    if (/^\d+$/.test(b.width)) {
                                        curCellCss.width = b.width + "px";
                                        curCellCss["min-width"] = Math.min(options.cellMinWidth, b.width) + "px";
                                        options.minWidth += b.width;
                                    } else if (/^\d+%$/.test(b.width) || b.width === "*") {
                                        options.widthType = true;
                                        curCellCss.width = b.width;
                                        curCellCss["min-width"] = options.cellMinWidth + "px";
                                        options.minWidth += options.cellMinWidth;
                                    }
                                } else {
                                    var minW = b.minWidth || options.cellMinWidth;
                                    curCellCss["min-width"] = minW + "px";
                                    options.minWidth += minW;
                                    options.autoCols[b.field] = {};
                                    options.autoCols[b.field].minWidth = minW;
                                    options.autoColNums++;
                                }
                                options.colsCount++;
                            }

                            b.align && (curCellCss["text-align"] = b.align);
                            //表头特有样式
                            b.style && (function () {
                                for (var c in b.style)
                                    curheadCellCss[c] = b.style[c];
                            })();
                            //数据特有样式
                            (b.field && b.dataStyle) && (function () {
                                for (var c in b.dataStyle)
                                    curDataCellCss[c] = b.dataStyle[c];
                            })();

                            str.push('<th class="' + cellclass);
                            //是否可拖拽，因为动态隐藏滚动条问题，固定列不支持拖拽宽度，暂时
                            str.push(b.field ? (b.resize ? " laytable-cell-resize" : "") : "");
                            str.push(b.style ? ' ' + headclass : '');
                            str.push(b.hidden ? ' hidden' : '');
                            str.push(b.sort ? ' laytable-sort-th' : '');
                            str.push(options.nowrap ? ' nowrap' : '');
                            str.push('"');//class结束
                            str.push(b.colspan ? ' colspan=' + b.colspan : '');
                            str.push(b.fixed ? ' fixed=' + b.fixed : '');
                            str.push(b.rowspan ? ' rowspan=' + b.rowspan : '');
                            str.push(b.field ? ' data-field="' + b.field + '"' : '');
                            str.push(b.islink ? ' islink' : '');
                            //str.push(b.sort ? ' data-type="' + (typeof b.sort === "string" ? b.sort : 's') + '"' : '');
                            str.push('>');//th前半开

                            //内容
                            var thContent;
                            if (b.field === 'chk') {
                                //checkbox
                                thContent = '<input type="checkbox" class="rayui-chk" chk="all" ' + (options.singleSelect ? 'disabled' : '') + '/>';
                            } else {
                                thContent = b.title === undef ? "" : b.title;
                            }
                            thContent += (b.sort ? '<span class="laytable-sort-span"><i class="laytable-sort laytable-desc"></i><i class="laytable-sort laytable-asc" ></i></span>' : '');

                            str.push(options.nowrap ? '<div class="' + cellclass + ' ' + headclass + '">' + thContent + '</div>' : thContent);
                            str.push('</th>');
                        });
                        str.push("</tr>");
                    });

                    str.push("</thead></table>");

                    return str.join('');
                })(),

                tablebody = '<table class="laytable-body"' + (options.even ? ' ray-even' : '') + '><tbody></tbody></table>',

                tableMain = [
                    '<div class="laytable-box" style="',
                    'min-height:' + options.minHeight + 'px',
                    options.width ? ';width:' + options.width + 'px' : '',
                    '"',//end style
                    options.skin ? ' skin="' + options.skin + '"' : '',
                    '>',
                    '<div class="laytable-head-boxmain">',
                    '<div class="laytable-head-box">',
                    tableHead,
                    '</div>',
                    '</div>',//end laytable-head-boxmain
                    '<div class="laytable-body-boxmain">',
                    '<div class="laytable-body-box" scroll-left=1>',
                    tablebody,
                    '</div>',
                    '</div>',//end laytable-body-boxmain
                    '<div class="laytable-fixed laytable-fixed-l"></div>',
                    '<div class="laytable-fixed laytable-fixed-r"></div>',
                    options.page ? '<div class="laytable-page-boxmain" style="text-align:' + options.page.align + '"></div>' : '',
                    '<div class="laytable-style">',
                    '</div>',
                    '<style>',
                    (function () {
                        var ss = [], isempty;
                        //这里只写其他样式，cell共有样式需要在首次初始化数据添加
                        for (var css in othercss) {
                            isempty = true;
                            for (var key in othercss[css]) {
                                if (isempty) {
                                    ss.push("." + css + "{");
                                    isempty = false;
                                }
                                ss.push(key + ":" + othercss[css][key] + ";");
                            }
                            if (!isempty)
                                ss.push("}");
                        }
                        return ss.join('');
                    })(),
                    '</style>',
                    '</div>'
                ].join('');

            $t.after(tableMain);

            //提前设定选择器
            TableClass.utils.jqSelector.call(that);

            //没有写url，认为是本地分页
            if (options.url) {
                TableClass.utils.initHeight.call(that);
                //ajax请求数据
                TableClass.utils.ajaxData.call(that);
            } else {
                TableClass.utils.onRecvData.call(that);
            }
        },
        jqSelector: function () {
            var that = this;

            var jqobjs = tableCache.jqobjs[that.tableIndex] = {};
            jqobjs.tb_box = $(this.elem).next("div.laytable-box");
            //固定表头和列
            jqobjs.tb_fixed = jqobjs.tb_box.find(">div.laytable-fixed");
            jqobjs.tb_fixed_l = jqobjs.tb_box.find(">div.laytable-fixed-l");
            jqobjs.tb_fixed_r = jqobjs.tb_box.find(">div.laytable-fixed-r");
            //thead
            jqobjs.tb_head_boxmain = jqobjs.tb_box.find("div.laytable-head-boxmain");
            jqobjs.tb_head_box = jqobjs.tb_head_boxmain.find("div.laytable-head-box");
            jqobjs.tb_head_fixed_l = jqobjs.tb_head_boxmain.find("div.laytable-fixed-l");
            jqobjs.tb_head_fixed_r = jqobjs.tb_head_boxmain.find("div.laytable-fixed-r");
            jqobjs.tb_head = jqobjs.tb_head_box.find(">table");
            //tbody
            jqobjs.tb_body_boxmain = jqobjs.tb_box.find("div.laytable-body-boxmain");
            jqobjs.tb_body_box = jqobjs.tb_body_boxmain.find("div.laytable-body-box");
            jqobjs.tb_body_fixed_l = jqobjs.tb_body_boxmain.find("div.laytable-fixed-l");
            jqobjs.tb_body_fixed_r = jqobjs.tb_body_boxmain.find("div.laytable-fixed-r");
            jqobjs.tb_body = jqobjs.tb_body_box.find(">table");
            //page
            jqobjs.tb_page_box = jqobjs.tb_box.find("div.laytable-page-boxmain");
            jqobjs.tb_head_body = jqobjs.tb_box.find("div.laytable-head-boxmain>div.laytable-head-box>table,div.laytable-body-boxmain>div.laytable-body-box>table");
            //css
            jqobjs.div_style = jqobjs.tb_box.find("div.laytable-style");
        },
        onRecvData: function () {
            var that = this,
                options = that.options;

            if (options.initComplete == undef) {
                options.initComplete = true;

                //设置样式
                var jqobjs = tableCache.jqobjs[that.tableIndex];

                //设置表格法最小宽度
                options.minWidth += options.colsCount;
                jqobjs.tb_head_body.css("min-width", options.minWidth + "px");

                //顺序不能变
                TableClass.utils.renderData.call(that);
                TableClass.utils.initStyle.call(that);
                TableClass.utils.initPage.call(that);
                //表头固定列
                TableClass.utils.initFixedHead.call(that);
                TableClass.utils.changePaging.call(that);

                TableClass.utils.modifyStyle.call(that);
                TableClass.utils.addEvents.call(that);
            } else {
                TableClass.utils.renderData.call(that);
                TableClass.utils.changePaging.call(that);
                TableClass.utils.modifyStyle.call(that);
            }

        },
        ajaxData: function () {
            var that = this,
                options = that.options;

            TableClass.utils.showError.call(that, 0);
            TableClass.utils.loading.call(that, 1);
            var data = {};
            if (options.where)
                $.extend(data, options.where);
            if (options.page)
                $.extend(data, { page: options.page.curr, limit: options.page.limit });
            if (options.initSort)
                $.extend(data, options.initSort);

            $.ajax({
                type: options.method || "get",
                url: options.url,
                data: data,
                dataType: "json",
                beforeSend: function (xhr) {
                    if (typeof options.onAjaxBeforeSend === "function" &&
                        options.onAjaxBeforeSend.call(this, xhr) === false) {
                        xhr.abort();
                    }
                },
                success: function (result) {
                    //清除数据
                    TableClass.utils.clearData.call(that);
                    if (typeof options.onAjaxSuccess === "function")
                        result = options.onAjaxSuccess.call(this, result) || result;
                    //result数据格式：code,msg,count,data
                    var code = result["ret"];
                    if (code !== 0) {
                        TableClass.utils.showError.call(that, (result["msg"] || "返回的数据状态异常"));
                    }
                    tableCache.datas[that.tableIndex] = result["data"] || [];
                    options.page.count = result["count"] || 0;
                    code === 0 && TableClass.utils.onRecvData.call(that);
                    //所有处理完毕
                    typeof options.onComplete === "function" && options.onComplete(result);
                },
                error: function (xhr, textStatus, errorThrown) {
                    TableClass.utils.showError.call(that, "请求数据接口异常");
                    //所有处理完毕
                    typeof options.onAjaxError === "function" && options.onAjaxError.call(this, xhr, textStatus, errorThrown);
                },
                complete: function () {
                    TableClass.utils.loading.call(that, 0);
                }
            });
        },
        //type:0关闭1显示
        loading: function (type) {
            var that = this,
                options = that.options;
            if (!options.loading) return;

            var jqobjs = tableCache.jqobjs[that.tableIndex],
                $tbox = jqobjs.tb_box,
                $loading = jqobjs.tb_box.find("div.laytable-loading");

            if (type === 0) {
                $loading.remove();
                return;
            }

            if (type === 1 && options.loading && options.url) {
                $('<div class="laytable-loading"/>')
                    .css({ "width": $tbox.width() + "px", "height": $tbox.height() + "px" })
                    .appendTo($tbox);
            }
        },
        showError: function (msg) {
            var that = this,
                jqobjs = tableCache.jqobjs[that.tableIndex],
                $diverr = jqobjs.tb_body_box.find("div.laytable-msg");

            if (msg === 0) {
                $diverr.remove();
                return;
            }

            if ($diverr.length === 0)
                $diverr = $('<div class="laytable-msg"/>').appendTo(jqobjs.tb_body_box);

            $diverr.html(msg);
        },
        initStyle: function () {
            var that = this,
                options = that.options,
                jqobjs = tableCache.jqobjs[that.tableIndex];

            //设置共有样式，只初始化时运行一次
            if (options.cellcss) {
                var cellcss = options.cellcss;
                var ss = [], isempty, css;
                ss.push("<style>");
                //这里只写其他样式，cell共有样式需要在首次初始化数据添加
                for (css in cellcss) {
                    isempty = true;
                    for (var key in cellcss[css]) {
                        if (isempty) {
                            ss.push("." + css + "{");
                            isempty = false;
                        }
                        ss.push(key + ":" + cellcss[css][key] + ";");
                    }
                    if (!isempty)
                        ss.push("}");
                }
                ss.push("</style>");
                jqobjs.div_style.html(ss.join(''));
                //设置自动列样式
                isempty = true;
                for (var field in options.autoCols) {
                    isempty = false;
                    css = ".laytable-cell-" + that.tableIndex + "-" + field;
                    var style = TableClass.utils.getCssStyle.call(that, css);
                    options.autoCols[field].style = style;
                }
                isempty && (options.autoCols = undef);
                options.cellcss = undef;
            }
        },
        modifyStyle: function () {
            var that = this;
            TableClass.utils.fullHeight.call(that);
            TableClass.utils.fullWidth.call(that);
            TableClass.utils.adjustPercentWidth.call(that);
            TableClass.utils.modifyFixedHeight.call(that);
        },
        initFixedHead: function () {
            var that = this,
                jqobjs = tableCache.jqobjs[that.tableIndex],
                $tbhead = jqobjs.tb_head,
                tbTop = jqobjs.tb_box.offset().top;

            var htmlL = [], htmlR = [], leftTop = -1000, rightTop = -1000, isInsertL, isInsertR;
            $tbhead.find("tr").each(function () {
                isInsertL = isInsertR = false;
                var $trobj = $(this);
                $(this).find("th").each(function () {
                    var fixed = $(this).attr("fixed");
                    var objClone;
                    if (fixed === 'left') {
                        leftTop === -1000 && (leftTop = $(this).offset().top);
                        if (!isInsertL) { isInsertL = true; htmlL.push('<tr style="height:' + $trobj.height() + 'px">'); }
                        objClone = $(this).clone();
                        htmlL.push(objClone[0].outerHTML);
                    } else if (fixed === 'right') {
                        rightTop === -1000 && (rightTop = $(this).offset().top);
                        if (!isInsertR) { isInsertR = true; htmlR.push('<tr style="height:' + $trobj.height() + 'px">'); }
                        objClone = $(this).clone();
                        htmlR.push(objClone[0].outerHTML);
                    }
                });
                if (isInsertL) htmlL.push("</tr>");
                if (isInsertR) htmlR.push("</tr>");
            });
            if (htmlL.length !== 0) {
                var $headFixedL = $('<div class="laytable-head-box" />');
                $headFixedL.html('<table class="laytable-head"><thead>' + htmlL.join('') + "</thead></table>")
                    .prependTo(jqobjs.tb_fixed_l.css("top", (leftTop - tbTop - 1) + "px"));
            }
            if (htmlR.length !== 0) {
                var $headFixedR = $('<div class="laytable-head-box" />');
                $headFixedR.html('<table class="laytable-head"><thead>' + htmlR.join('') + "</thead></table>")
                    .prependTo(jqobjs.tb_fixed_r.css("top", (rightTop - tbTop - 1) + "px"));
                //右侧宽度滚动条问题
                jqobjs.tb_fixed_r.css("right", scrollGap.width + "px");
                jqobjs.tb_box.find(".laytable-fixed-amend")
                    .height($headFixedR.height() - 1)//1:自己下边框
                    .css("top", (rightTop - tbTop - 2) + "px") //2:自己和td的border
                    .css("right", scrollGap.width - 49 + "px"); //amend宽度50，减去自己的左边框1px
            }
        },
        renderData: function () {
            var that = this,
                jqobjs = tableCache.jqobjs[that.tableIndex],
                $tbody = jqobjs.tb_body.find(">tbody"),
                options = that.options,
                data = TableClass.utils.getData.call(that);

            //清除数据
            TableClass.utils.clearData.call(that);

            //设置表头排序图标
            if (options.initSort)
                jqobjs.tb_box.find(".laytable-head th[data-field=" + options.initSort.sortField + "]").attr("sort-type", options.initSort.sortType);

            if (data.length === 0) {
                TableClass.utils.showError.call(that, "无数据");
                return;
            }

            //合并单元格，instanceof为field数组
            if (options.colspanDefs && options.colspanDefs instanceof Array) {
                var preValue = {}, firstData = data[0], count = data.length;
                if (count > 1) {
                    $.each(options.colspanDefs, function (a, key) {
                        if (firstData.hasOwnProperty(key)) {
                            firstData[key + "rowspan"] = 1;
                            preValue[key] = firstData;
                        }
                    });
                    var i;
                    for (i = 1; i < count; i++) {
                        var model = data[i];
                        for (var key in preValue) {
                            if (model[key] === preValue[key][key]) {
                                preValue[key][key + "rowspan"]++;
                                model[key + "rowspan"] = -1;//默认隐藏
                            } else {
                                preValue[key] = model;
                                model[key + "rowspan"] = 1;
                            }
                        }
                    }
                }
                preValue = null;
            }

            //添加数据
            var $fixedtbodyl = jqobjs.tb_fixed_l.find("div.laytable-body-noscroll table>tbody"),
                $fixedtbodyr = jqobjs.tb_fixed_r.find("div.laytable-body-noscroll table>tbody");
            var cols = tableCache.cols[that.tableIndex], trs_fixed_l = [], trs_fixed_r = [];
            $(data).each(function (a, b) {
                var trs = [], trs_fixed_l_one = [], trs_fixed_r_one = [];
                //添加数据前
                typeof options.onBeforeAddRow === "function" && options.onBeforeAddRow(a, b);
                trs.push('<tr data-index=' + a + '>');

                if (options.expandRow) {
                    trs.push('<td><a class="laytable-expand"></a></td>');
                }

                $.each(cols, function (c, opt) {
                    var td = [],
                        field = opt.field,
                        cellCss = 'laytable-cell-' + that.tableIndex + '-' + field,
                        rowspan = b[field + "rowspan"];

                    td.push('<td data-field="' + field + '"');
                    if (rowspan !== undef && rowspan > 1)
                        td.push(' rowspan=' + rowspan);
                    td.push(' class="' + cellCss);
                    if (!options.nowrap)
                        td.push(' laytable-cell-data-' + that.tableIndex + '-' + field);
                    td.push(opt.hidden ? ' hidden' : '');
                    td.push(options.nowrap ? ' nowrap' : '');
                    if (rowspan !== undef && rowspan === -1)
                        td.push(' hiddenImp');
                    td.push('"');//end class
                    td.push(opt.islink ? ' islink' : '');
                    td.push('>');

                    var value;
                    if (typeof opt.formatter === "function") {
                        //formatter函数
                        value = opt.formatter(b[field], b, a);
                        //模板
                    } else if (field === 'chk') {
                        //checkbox
                        value = '<input type="checkbox" class="rayui-chk" chk="row"/>';
                    } else {
                        value = b[field];
                        if (opt.dataType === "html" && value !== undef && value !== '')
                            value = value.replace(/[<>&"]/g, function (c) { return { '<': '&lt;', '>': '&gt;', '&': '&amp;', '"': '&quot;' }[c]; });
                    }
                    if (value === undef || value === '')
                        value = "&nbsp;";

                    td.push(options.nowrap ? '<div class="' + cellCss + ' laytable-cell-data-' + that.tableIndex + '-' + field + '">' + value + '</div>' : value);
                    td.push('</td>');

                    var strTmp = td.join('');
                    trs.push(strTmp);

                    opt.fixed === 'left' && (trs_fixed_l_one.push(strTmp));
                    opt.fixed === 'right' && (trs_fixed_r_one.push(strTmp));
                });

                trs.push("</tr>");
                var strHtml = trs.join('');
                var $tr = $("" + strHtml);
                $tr.appendTo($tbody);
                //首次复制数据设置行高度
                if (trs_fixed_l_one.length > 0) {
                    trs_fixed_l.push('<tr data-index=' + a + ' style="height:' + $tr.height() + 'px">' + trs_fixed_l_one.join('') + "</tr>");
                    trs_fixed_l_one = [];
                }
                if (trs_fixed_r_one.length > 0) {
                    trs_fixed_r.push('<tr data-index=' + a + ' style="height:' + $tr.height() + 'px">' + trs_fixed_r_one.join('') + "</tr>");
                    trs_fixed_r_one = [];
                }
                //添加数据后
                typeof options.onAddRow === "function" && options.onAddRow(a, $tr, b);
            });
            data = null;
            //添加固定列数据
            if (trs_fixed_l.length > 0) {
                if ($fixedtbodyl.length === 0) {
                    $('<div class="laytable-body-noscroll" />').html($('<div class="laytable-body-box" />')
                        .html('<table class="laytable-body"' + (options.even ? ' ray-even' : '') + '>' + trs_fixed_l.join('') + '</table>'))
                        .appendTo(jqobjs.tb_fixed_l);
                } else {
                    $fixedtbodyl.html(trs_fixed_l.join(''));
                }
            }
            if (trs_fixed_r.length > 0) {
                if ($fixedtbodyr.length === 0) {
                    $('<div class="laytable-body-noscroll" />').html($('<div class="laytable-body-box" />')
                        .html('<table class="laytable-body"' + (options.even ? ' ray-even' : '') + '>' + trs_fixed_r.join('') + '</table>'))
                        .appendTo(jqobjs.tb_fixed_r);
                    //添加补丁
                    $('<div class="laytable-fixed-amend"/>').appendTo(jqobjs.tb_box);
                } else {
                    $fixedtbodyr.html(trs_fixed_r.join(''));
                }
            }
        },
        initHeight: function () {
            var that = this,
                $t = $(this.elem),
                options = that.options,
                jqobjs = tableCache.jqobjs[that.tableIndex],
                $theaddiv = jqobjs.tb_head_box,
                $tpagediv = jqobjs.tb_page_box,
                $tbodydiv = jqobjs.tb_body_box,
                fullHeightGap;

            !options.heightSetting && (options.heightSetting = options.height);

            if (/^full-\d+$/.test(options.heightSetting)) {//full-差距值
                fullHeightGap = options.heightSetting.split('-')[1];
                options.height = $win.height() - fullHeightGap;
            } else if (/^sel-[#|.][\w]+-\d+$/.test(options.heightSetting)) { //sel-id序列-差距值
                fullHeightGap = $t.offset().top;
                var list = options.heightSetting.split('-');
                $("" + list[1]).each(function () {
                    fullHeightGap += $(this).outerHeight();
                });
                if (list.length === 3) fullHeightGap += parseInt(list[2]);
                options.height = $win.height() - fullHeightGap;
            }

            //最终高度不能小于最小高度
            if (options.height < options.minHeight) options.height = options.minHeight;
            //数据高度-head-page-2px(laytable-box上下各1px border)
            var tmpH = options.height - $theaddiv.outerHeight() - $tpagediv.outerHeight() - 2;
            $tbodydiv.outerHeight(tmpH);//数据表格设置高度
        },
        fullHeight: function () {
            //计算高
            var that = this,
                options = that.options;

            var jqobjs = tableCache.jqobjs[that.tableIndex],
                $theaddiv = jqobjs.tb_head_box,
                $tbodydiv = jqobjs.tb_body_box;

            //如果设置的是固定高度，则无需再设置了，ajax时initHeight方法会调用两遍
            if (!/^\d+$/.test(options.heightSetting)) TableClass.utils.initHeight.call(that);
            var tbodydiv = $tbodydiv[0];
            if (tbodydiv.scrollHeight > tbodydiv.clientHeight) {
                //说明有竖向滚动条
                $theaddiv.css("margin-right", (scrollGap.width) + "px");
                //右侧固定和数据最右侧列还原右边框
                jqobjs.tb_fixed_r.find("tr>td:last,th:last").removeClass("last");
                jqobjs.tb_fixed_r.css("right", scrollGap.width + "px");
            } else {
                $theaddiv.css("margin-right", "0");
                //右侧固定和数据最右侧列移除右边框
                jqobjs.tb_fixed_r.find("tr").each(function () {
                    $(this).find("td:last,th:last").addClass("last");
                });
                jqobjs.tb_fixed_r.css("right", "0");
            }

            //数据高度大于数据box高度时，body最后一行去除下边框
            var lastTrs = jqobjs.tb_box.find(".laytable-body").find("tr:last");
            if ($tbodydiv.find(".laytable-body").outerHeight() >= $tbodydiv.outerHeight()) {
                lastTrs.addClass("last");
            } else {
                lastTrs.removeClass("last");
            }
        },
        //自适应宽度以数据宽度为准
        fullWidth: function () {
            var that = this,
                options = that.options,
                jqobjs = tableCache.jqobjs[that.tableIndex],
                $tb_data_first_tr = jqobjs.tb_body.find("tr:first"),
                $tbodydiv = jqobjs.tb_body_box,
                tbodydiv = $tbodydiv[0];


            //有自适应列时动态修改表头宽度
            if (options.autoCols && !options.resized) {
                var field, w, autoMinWidth = 0;
                //必须，为了自适应数据宽度
                for (field in options.autoCols) {
                    options.autoCols[field].style.style.width = "";
                    autoMinWidth += options.autoCols[field].minWidth;
                }

                //如果table小于div宽度就100%
                tbodydiv.clientWidth > options.minWidth && (jqobjs.tb_head_body.css("width", "100%"));
                if (!options.nowrap) {
                    //允许换行，使用table的自适应宽度
                    for (field in options.autoCols) {
                        //需要减去右边框1px，否则总是出现横向滚动条，不要使用width函数
                        w = $tb_data_first_tr.find("td[data-field=" + field + "]").outerWidth();
                        options.autoCols[field].style.style.width = w - 1 + "px";
                    }
                } else {
                    //不允许换行
                    var obj, aver = 0;
                    if (tbodydiv.clientWidth > options.minWidth) {
                        var widthGap = tbodydiv.clientWidth - options.minWidth;
                        aver = Math.floor(widthGap / options.autoColNums);
                    }
                    for (field in options.autoCols) {
                        obj = options.autoCols[field];
                        obj.style.style.width = obj.minWidth + aver + "px";
                    }
                }
                //修正头部滚动条
                jqobjs.tb_head_box.scrollLeft($tbodydiv.scrollLeft());
            }

            //修改div宽度隐藏固定列滚动条
            var clientH = Math.min(tbodydiv.clientHeight, jqobjs.tb_body.height()),//取bodydiv和table的最小高度
                $tbody_fixed_box_noscroll = jqobjs.tb_box.find("div.laytable-fixed .laytable-body-noscroll");
            $tbody_fixed_box_noscroll.each(function () {
                var innerTablediv = $(this).find(">.laytable-body-box"),
                    tbodyWidth = innerTablediv.find(".laytable-body").width();
                innerTablediv.outerHeight(clientH).outerWidth(tbodyWidth + scrollGap.width);
                $(this).outerWidth(tbodyWidth);
            });

            //判断是否去掉最后一列边框，数据宽度大于数据box可视宽度时去掉右边框
            if ($tbodydiv.find(".laytable-body").outerWidth() < tbodydiv.clientWidth) {
                jqobjs.tb_body.find("tr>td.last").removeClass("last");
                jqobjs.tb_head.find("tr>th.last").removeClass("last");
            } else {
                jqobjs.tb_body.find("tr").each(function () {
                    $(this).find("td:last").addClass("last");
                });
                jqobjs.tb_head.find("tr").each(function () {
                    $(this).find("th:last").addClass("last");
                });
            }

        },
        adjustPercentWidth: function () {
            var that = this,
                options = that.options,
                jqobjs = tableCache.jqobjs[that.tableIndex];

            //百分比宽度
            if (options.widthType && !options.resized) {
                var cols = tableCache.cols[that.tableIndex],
                    totalWidth = jqobjs.tb_head_box.width() - cols.length,
                    surplus = totalWidth, fieldStartCss, ww = 0;
                $.each(cols, function (c, opt) {
                    var field = opt.field,
                        cellClass = '.laytable-cell-' + that.tableIndex + '-' + field,
                        cellCss = TableClass.utils.getCssStyle.call(that, cellClass);
                    if (opt.width === "*") {
                        fieldStartCss = cellCss;
                        return true;
                    }
                    var percent = opt.width.replace("%", ""),
                        width = totalWidth / 100 * (parseFloat(percent));

                    width = Math.floor(width);//解决那零点几导致的横向滚动条
                    cellCss.style.width = width + "px";
                    if (width < options.cellMinWidth) cellCss.style["min-width"] = width + "px";
                    surplus -= width;
                    ww += width;
                    return true;
                });
                if (fieldStartCss) {
                    fieldStartCss.style.width = surplus + "px";
                    if (surplus < options.cellMinWidth) fieldStartCss.style["min-width"] = surplus + "px";
                }
            }
        },
        modifyFixedHeight: function (isneedAmendHead) {
            var that = this,
                jqobjs = tableCache.jqobjs[that.tableIndex],
                tbody_box = jqobjs.tb_body_box[0],
                $fixed_r = jqobjs.tb_fixed_r,
                options = that.options;

            //修改复制数据的高度，数据不自动换行时不需要调整
            if (!options.nowrap) {
                //不管本地数据还是ajax数据
                var $tbody = jqobjs.tb_body.find(">tbody"),
                    body_fixed = jqobjs.tb_fixed.find(".laytable-body-box>table");

                if (body_fixed.length > 0) {
                    $tbody.find("tr").each(function (a) {
                        body_fixed.find("tr:eq(" + a + ")").outerHeight($(this).outerHeight());
                    });
                }
            }

            //有自动列宽时，或则拖拽时（由拖拽自己判断），动态修改固定列表头行高
            if (isneedAmendHead || options.autoColNums > 0) {
                //修改表头高度，如果设置表头宽度合理一般不用修改，设置不合理也不好看
                //注意多行固定问题，倒着修改正好
                var head_fixed_l = jqobjs.tb_fixed_l.find(".laytable-head"),
                    head_fixed_r = jqobjs.tb_fixed_r.find(".laytable-head"),
                    head = jqobjs.tb_head,
                    trheadCount = head.find("tr").length;

                var trc = head_fixed_l.find("tr").length, tmp, index;
                if (trc > 0) {
                    tmp = trheadCount - 1; index = trc - 1;
                    while (index >= 0) {
                        head_fixed_l.find("tr:eq(" + (index--) + ")").height(head.find("tr:eq(" + (tmp--) + ")").height());
                    }
                }
                trc = head_fixed_r.find("tr").length;
                if (trc > 0) {
                    tmp = trheadCount - 1; index = trc - 1;
                    var trh = 0, datatrH;
                    while (index >= 0) {
                        datatrH = head.find("tr:eq(" + (tmp--) + ")").height();
                        trh += datatrH;
                        head_fixed_r.find("tr:eq(" + (index--) + ")").height(datatrH);
                    }
                    //修改amend的高度
                    jqobjs.tb_box.find('div.laytable-fixed-amend').height(trh - 1);//1自己下边框
                }
            }//end if

            //判断是否隐藏右侧固定列
            if ($fixed_r.children().length > 0) {
                if (tbody_box.scrollWidth > tbody_box.clientWidth) {
                    //有横向滚动条，有滚动条事件所以不能隐藏（隐藏后滚动无效），这里只能使用飘出
                    $fixed_r.removeClass("blowout-right");
                    //右侧固定列显示时，判断是否有竖向滚动条，有amend就显示
                    if (tbody_box.scrollHeight > tbody_box.clientHeight)
                        jqobjs.tb_box.find('div.laytable-fixed-amend').removeClass("hidden");

                } else {
                    $fixed_r.addClass("blowout-right");
                    //右侧固定列隐藏时，amend必须隐藏
                    jqobjs.tb_box.find('div.laytable-fixed-amend').addClass("hidden");
                }
            }
        },
        getPageData: function () {
            var that = this,
                page = that.options.page,
                data = tableCache.datas[that.tableIndex],
                onePageData = [];

            var count = data.length, start = 0, end = data.length;
            if (page) {
                start = page.curr === 1 ? 0 : (page.curr - 1) * page.limit;
                end = page.curr * page.limit;
            }

            while (start < end) {
                if (start >= count) break;
                onePageData.push(data[start++]);
            }
            return onePageData;
        },
        initPage: function () {
            var that = this,
                jqobjs = tableCache.jqobjs[that.tableIndex],
                options = that.options,
                page = options.page;
            if (!page) return;

            var data = tableCache.datas[that.tableIndex];
            //如果没有数据则不显示分页
            if (data.length === 0) return;

            //分页limit是否在limits里
            if (!page.limits.contains(page.limit)) {
                page.limits.push(page.limit);
                page.limits.sort(function (a, b) { return a - b; });
            }

            if (!options.url && page.count === -1) page.count = data.length;
            if (page.count === 0) return;

            var $pagediv = jqobjs.tb_page_box,
                curr = page.curr,
                raylimits = (function () {
                    return [
                        /*'<select class="laytable-page-limits">',
                        function () {
                            var strTmp = [];
                            $(page.limits).each(function (a, b) {
                                strTmp.push('<option value="' + b + '" ' + (page.limit === b ? "selected" : "") + '>' + b + ' 条/页</option>');
                            });
                            return strTmp.join('');
                        }(),
                        '</select>',*/
                        '&nbsp;&nbsp;',
                        '<span class="laytable-page-count">共 ' + page.count + ' 条</span>',
                        '<ul class="laytable-page-pagination">',
                        '</ul>',
                        '<span>到第&nbsp;</span>',
                        '<input type="text" class="rayui-input laytable-page-input" min="1" value="' + curr + '" />&nbsp;页',
                        '&nbsp;&nbsp;',
                        '<a class="rayui-btn laytable-page-btnok">确定</a>'
                    ].join('');
                })();

            $pagediv.html(raylimits);
        },
        changePaging: function () {
            var that = this,
                options = that.options;
            if (!options.page) return;

            var jqobjs = tableCache.jqobjs[that.tableIndex],
                $ul = jqobjs.tb_page_box.find(".laytable-page-pagination"),
                page = options.page,
                pages = Math.ceil(page.count / page.limit);
            page.pages = pages;

            if (page.curr <= 0) page.curr = 1;
            else if (page.curr > pages) page.curr = pages;

            var curr = page.curr,
                count = page.count,
                libtns = [
                    '<li><a class="page-item page-prev' + (curr === 1 || count === 0 ? " rayui-disabled" : "") + '" value="-1">&lt;</a></li>',
                    (function () {
                        var groups = pages < page.groups ? pages : page.groups,
                            halve = Math.floor((groups - 1) / 2),
                            end = Math.max(groups, (curr + halve) > pages ? pages : (curr + halve)),
                            start = (end - groups) < 1 ? 1 : end - groups + 1,
                            strTmp = [];

                        //分3部分加载
                        //part1
                        if (start > 1)
                            strTmp.push('<li><a class="page-item" value="1">1</a></li>');

                        //左分隔符
                        if (start > 2)
                            strTmp.push('<li>...</li>');

                        //part2
                        while (start <= end) {
                            strTmp.push('<li><a class="page-item" value="' + start + '">' + (start++) + '</a></li>');
                        }
                        //part3
                        //右分隔符
                        if (end < pages - 1)
                            strTmp.push('<li>...</li>');
                        if (end < pages)
                            strTmp.push('<li><a class="page-item" value="' + pages + '">' + pages + '</a></li>');

                        return strTmp.join('');
                    })(),
                    '<li><a class="page-item page-last' + (curr === pages || count === 0 ? " rayui-disabled" : "") + '" value="+1">&gt;</a></li>'
                ];

            $ul.html(libtns.join(''));
            //选中当前页面
            $ul.find("li>a[value=" + page.curr + "]").addClass("page-active");
            //修改总条数
            jqobjs.tb_page_box.find(".laytable-page-count").html('共 ' + page.count + ' 条');
        },
        clearData: function () {
            var that = this,
                jqobjs = tableCache.jqobjs[that.tableIndex],
                $tbody = jqobjs.tb_body.find(">tbody"),
                $fixedtbodyl = jqobjs.tb_fixed_l.find("div.laytable-body-noscroll table>tbody"),
                $fixedtbodyr = jqobjs.tb_fixed_r.find("div.laytable-body-noscroll table>tbody");

            $tbody.html("");
            $fixedtbodyl.html("");
            $fixedtbodyr.html("");
            jqobjs.tb_fixed_l.find(".laytable-body-box").css("height", "");
            jqobjs.tb_fixed_r.find(".laytable-body-box").css("height", "");
        },
        deleteData: function (data) {
            var that = this,
                options = that.options,
                key = options.serialField,
                iskey = key !== undef,
                datas = tableCache.datas[that.tableIndex],
                start, end, count, dataStr;

            if (options.url || !options.page) {
                //ajax
                start = 0;
                end = datas.length - 1;
                count = datas.length;
            } else {
                //本地
                var page = options.page;
                start = page.curr === 1 ? 0 : (page.curr - 1) * page.limit;
                end = page.curr * page.limit;
                count = datas.length;
            }

            if (iskey && datas[0].hasOwnProperty(key)) {
                while (start < end) {
                    if (start >= count) break;
                    if (datas[start][key] === data[key]) {
                        datas.splice(start, 1);
                        return;
                    }
                    start++;
                }
            } else {
                dataStr = JSON.stringify(data);
                while (start < end) {
                    if (start >= count) break;
                    if (JSON.stringify(datas[start]) === dataStr) {
                        datas.splice(start, 1);
                        return;
                    }
                    start++;
                }
            }
        },
        reloadx: function (option) {
            option !== undef && $.extend(true, this.options, option);
            debugger
            var that = this;
                //options = that.options,
            var options = option;
            that.options = option;
            that.elem = option.elem;
            this.tableIndex = 0;
            var page = options.page;
            debugger
            if (page) {
                if (page.curr <= 0) page.curr = 1;
                if (page.curr > page.pages) page.curr = page.pages;
            }
            if (options.url) {
                TableClass.utils.ajaxData.call(that);
            } else {
                TableClass.utils.onRecvData.call(that);
            }
        },
        reload: function (option) {
            option !== undef && $.extend(true, this.options, option);
            var that = this,
                options = that.options,
                page = options.page;

            if (page) {
                if (page.curr <= 0) page.curr = 1;
                if (page.curr > page.pages) page.curr = page.pages;
            }
            if (options.url) {
                TableClass.utils.ajaxData.call(that);
            } else {
                TableClass.utils.onRecvData.call(that);
            }
        },
        getCssStyle: function (css) {
            var that = this,
                jqobjs = tableCache.jqobjs[that.tableIndex],
                style = jqobjs.div_style.find("style")[0],
                sheet = style.sheet || style.styleSheet,
                rules = sheet.cssRules || sheet.rules;

            var curCss;
            $(rules).each(function (a, rr) {
                if (css === rr.selectorText) {
                    curCss = rr;
                    return false;
                }
                return true;
            });
            //如果没有则添加一条，因为初始化的时候保证一定含有，所以这里没必要再添加了
            return curCss;
        },
        getData: function () {
            var that = this,
                options = that.options,
                datas = options.url ? tableCache.datas[that.tableIndex] : TableClass.utils.getPageData.call(that);

            //如果开启本地排序则数据排序
            if (datas.length > 1 && options.initSort && options.localSort)
                datas.raysort(options.initSort.sortField, options.initSort.sortType === 'desc');

            return datas;
        },
        addEvents: function () {
            var that = this,
                dict = {},
                $doc = $(doc),
                $body = $('body'),
                jqobjs = tableCache.jqobjs[that.tableIndex],
                $thisAllTable = jqobjs.tb_head_body,
                $tbodydiv = jqobjs.tb_box.find("div.laytable-body-box"),
                $page = jqobjs.tb_page_box,
                options = that.options,
                isresizing = false,
                page = options.page;

            //拖拽调整宽度
            jqobjs.tb_box.find(".laytable-head").on('mousemove.laytable', "th[class*=laytable-cell-resize]", function (e) {
                if (dict.resizeStart) return;
                var othis = $(this),
                    oLeft = othis.offset().left,
                    pLeft = e.clientX - oLeft;
                //是否处于拖拽允许区域
                dict.allowResize = othis.outerWidth() - pLeft <= 10;
                $body.css('cursor', (dict.allowResize ? 'col-resize' : ''));
            }).on('mouseleave.laytable', "th[class*=laytable-cell-resize]", function () {
                if (dict.resizeStart) return;
                $body.css('cursor', '');
            }).on('mousedown.laytable', "th[class*=laytable-cell-resize]", function (e) {
                if (dict.allowResize) {
                    e.preventDefault();
                    var $othis = $(this);
                    dict.elem = this;
                    dict.width = $othis.width();
                    dict.resizeStart = true; //开始拖拽

                    var thcss = ".laytable-cell-" + that.tableIndex + "-" + $(dict.elem).data("field");
                    dict.cssRule = TableClass.utils.getCssStyle.call(that, thcss);

                    var cssminWidth = parseFloat($othis.css("min-width").replace("px", ""));
                    dict.minWidth = cssminWidth === 0 ? options.cellMinWidth : cssminWidth;
                    dict.offset = e.clientX; //记录初始坐标
                    var tbone = $thisAllTable.eq(0);
                    //记录当前table宽度
                    dict.tb_width = tbone.outerWidth();
                    dict.tb_minwidth = parseFloat(tbone.css("min-width").replace("px", ""));
                }
            });

            //拖拽中
            $doc.on('mousemove.laytable', function (e) {
                if (dict.resizeStart) {
                    e.preventDefault();
                    if (dict.elem) {
                        options.resized = true;
                        var gap = e.clientX - dict.offset;
                        var setWidth = dict.width + gap;
                        if (setWidth > dict.minWidth) {
                            dict.cssRule.style.width = setWidth + "px";
                            var newTbW = dict.tb_width + gap;
                            //修改最小宽度和宽度
                            $thisAllTable.outerWidth(newTbW);
                            if (newTbW < dict.tb_minwidth)
                                $thisAllTable.css("min-width", newTbW + "px");
                            //修改固定数据的高度
                            TableClass.utils.fullWidth.call(that);
                            //修改固定数据的高度
                            TableClass.utils.modifyFixedHeight.call(that, true);
                        }
                    }
                    isresizing = true;
                }
            }).on('mouseup.laytable', function (e) {
                if (dict.resizeStart) {
                    dict = {};
                    $body.css('cursor', '');
                }
            });

            //行事件
            var tables = jqobjs.tb_box.find("div.laytable-body-box>table");
            if (tables.length > 1) {
                tables.on("mouseenter.laytable", "tr", function () {
                    tables.find("tr:eq(" + $(this).index() + ")").addClass("laytable-tr-hover");
                }).on("mouseleave.laytable", "tr", function () {
                    tables.find("tr:eq(" + $(this).index() + ")").removeClass("laytable-tr-hover");
                });
            }

            //横竖向滚动条，$tbodydiv有3个，数据、左固定、右固定
            var funcScrollTop = function () {
                if ($(this).attr("scroll-left") === "1") jqobjs.tb_head_box.scrollLeft($(this).scrollLeft());
                $tbodydiv.not(this).scrollTop($(this).scrollTop());
                if($(this).scrollLeft()===0){
                    $(".laytable-fixed-l").css("box-shadow","");
                }else{
                    $(".laytable-fixed-l").css("box-shadow","0px -1px 8px rgba(0,0,0,0.08)");
                }
            }
            $tbodydiv.on("scroll.laytable", funcScrollTop);
            $tbodydiv.hover(function () {
                $tbodydiv.not(this).off("scroll.laytable", funcScrollTop);
            }, function () {
                //$tbodydiv.not(this).on("scroll.laytable", funcScrollTop);
                //不能使用上面代码，因为当删除数据导致div没有时，数据的div无法绑定时间
                $tbodydiv.off("scroll.laytable", funcScrollTop)
                    .on("scroll.laytable", funcScrollTop);
            });

            //checkbox
            var tmpLength = jqobjs.tb_fixed_l.find(".laytable-head-box").length,
                $obj = tmpLength > 0 ? jqobjs.tb_fixed_l : jqobjs.tb_box;
            $obj.on("click.laytableChk", "input.rayui-chk", function () {
                var chk = $(this).attr("chk"), value = this.checked, objChk;
                //全选
                if (chk === "all") {
                    jqobjs.tb_box.find("input.rayui-chk").not(this).prop("checked", value);

                    //回调事件
                    objChk = {
                        type: "all",
                        index: -1,
                        checked: value
                    };
                    typeof options.onCheck === "function" && (options.onCheck.call(this, objChk));
                    return;
                }

                var $tr = $(this).closest("tr"), index = $tr.index();
                //行选
                if (options.singleSelect) {
                    //当前表格内选中的checkbox排除当前checkbox
                    var $chked = $obj.find("input.rayui-chk:checked").not(this);
                    //所有表格内选中的checkbox排除当前checkbox
                    jqobjs.tb_box.find("input.rayui-chk:checked").not(this).prop("checked", false);
                    if ($chked.length > 0 && typeof options.onCheck === "function") {
                        var $trpre = $chked.closest("tr"), indexpre = $trpre.index();
                        $chked.prop("checked", false);
                        objChk = {
                            type: "one",
                            index: indexpre,
                            checked: false
                        };
                        options.onCheck.call($chked, objChk);
                    }
                }

                //同步数据表格当前行选中
                tmpLength > 0 &&
                    (jqobjs.tb_box.find(".laytable-body").find("tr:eq(" + index + ") input.rayui-chk").prop("checked", value));

                //如果是允许多选，需要判断all是否选中
                if (!options.singleSelect) {
                    var chkbox = jqobjs.tb_box.find(".laytable-head input.rayui-chk"),
                        valTmp = !value ? false :
                            $obj.find(".laytable-body input.rayui-chk").length === $obj.find(".laytable-body input.rayui-chk:checked").length;

                    chkbox.prop("checked", valTmp);
                }

                //回调事件
                objChk = {
                    type: "one",
                    index: index,
                    checked: value
                };
                typeof options.onCheck === "function" && (options.onCheck.call(this, objChk));
            });

            //page分页select
            $page.on("change.laytable", "select.laytable-page-limits", function () {
                var preLimit = page.limit;
                page.limit = parseInt($(this).val());
                //按照数据重新计算pages
                page.pages = Math.ceil(page.count / page.limit);

                options.onPageLimitChanged && options.onPageLimitChanged(preLimit, page.limit);
                //重新设定page
                TableClass.utils.reload.call(that);
            });

            //page页码
            //-------2019年9月2日----byLJ-----
           /* $page.on("click.laytableli", "ul.laytable-page-pagination a:not('.rayui-disabled')", function () {
                var ss = $(this).attr("value"),
                    prePage = page.curr;
                if (ss === "-1") page.curr -= 1;
                else if (ss === "+1") page.curr += 1;
                else page.curr = parseInt(ss);
                options.onPageJump && options.onPageJump(prePage, page.curr);
                //重新设定page
                TableClass.utils.reload.call(that);
            });*/

            //input页码输入框
            /*  $page.on("keyup.laytable", ".laytable-page-input", function (e) {
                var value = this.value
                    , keyCode = e.keyCode;
                if (/^(37|38|39|40)$/.test(keyCode)) return;
                if (/\D/.test(value)) {
                    this.value = value.replace(/\D/, '');
                }
                var w = (value.length - 4) * 4;
                //修改宽度
                $(this).width(30 + w);
                if (keyCode === 13) $page.find("a.laytable-page-btnok").click();
            });

            //page输入页码跳转
            $page.on("click.laytablebtn", "a.laytable-page-btnok", function () {
                var $input = $page.find(".laytable-page-input"),
                    input = $input.val(),
                    value = parseInt(input),
                    prePage = page.curr;
                if (input === '' || value < 1) return;

                page.curr = value;
                options.onPageJump && options.onPageJump(prePage, page.curr);
                //重新设定page
                TableClass.utils.reload.call(that);
                $input.val(page.curr);
            });*/

            //排序
            var sortThs = jqobjs.tb_box.find("th.laytable-sort-th");
            jqobjs.tb_box.on("click.laytableSort", "th.laytable-sort-th,span.laytable-sort-span>i", function (e) {
                e.stopPropagation();
                if (isresizing) {
                    isresizing = false;
                    return;
                }
                //如果没有data-field，说明点击的是i
                var tosortfield, tosorttype, $th;
                if (this.tagName === "I") {
                    $th = $(this).closest("th");
                    tosortfield = $th.data("field");
                    tosorttype = $(this).prop("class").indexOf("asc") > -1 ? "asc" : "desc";
                } else {
                    $th = $(this);
                    var nowSortType = $(this).attr("sort-type");
                    tosortfield = $(this).data("field");
                    tosorttype = nowSortType === undef ? "asc" : nowSortType === "asc" ? "desc" : "asc";
                }
                sortThs.attr("sort-type", "");
                $th.attr("sort-type", tosorttype);

                !options.initSort && (options.initSort = {});
                options.initSort.sortField = tosortfield;
                options.initSort.sortType = tosorttype;

                typeof options.onSort === "function" && options.onSort(options.initSort);

                TableClass.utils.reload.call(that);
            });

            //如果出现省略，则可查看更多
            jqobjs.tb_box.on("click.laytableShow", 'td.nowrap:not("[islink]")', function () {
                var obj = $(this).find("div")[0] || this;
                if (obj.scrollWidth > $(obj).outerWidth()) {
                    var $p = $(".laytable-tips");
                    if ($p.length === 0) {
                        $p = $('<div class="laytable-tips"><div class="laytable-tips-content"></div></div>').appendTo($body);
                        $('<i class="laytable-tips-close"></i>')
                            .appendTo($p)
                            .click(function () {
                                $p.remove();
                            });
                    }

                    var $content = $p.find(".laytable-tips-content");
                    $content.html($(obj).html());

                    //计算left、top
                    var cwidth = $win.width(),
                        offset = $(this).offset(),
                        left, top = offset.top - 1,
                        width = $p.outerWidth();

                    width > 500 && (width = 500);
                    if (offset.left + width < cwidth) {
                        left = offset.left;
                    } else {
                        var tmpW = offset.left + $(obj).outerWidth();
                        if (width > tmpW)
                            width = tmpW;
                        left = tmpW - width;
                    }

                    $p.css({
                        width: width + "px",
                        top: top + "px",
                        left: left + "px"
                    });

                }
            });

        },
        on: function (event, func) {
            if (typeof event !== "string" || typeof func !== "function") return;
            var that = this,
                options = that.options,
                jqobjs = tableCache.jqobjs[that.tableIndex],
                $tbody = jqobjs.tb_body;
            switch (event) {
                //工具条
                case "tool":
                    jqobjs.tb_box.on("click.laytableTool", ".laytable-body td[data-field^=opts] *[event]", function () {
                        var evt = $(this).attr("event"),
                            $tr = $(this).closest("tr"),
                            index = $tr.data("index"),
                            datas = TableClass.utils.getData.call(that);

                        var data = datas[index],
                            $datatr = {
                                index: index,
                                data: data,
                                tr: $tbody.find("tr[data-index=" + index + "]"),
                                del: function () {
                                    //删除本地数据
                                    TableClass.utils.deleteData.call(that, data);
                                    //修改页码总数
                                    options.page && (options.page.count -= 1);
                                    //本地重新渲染数据
                                    TableClass.utils.onRecvData.call(that);
                                },
                                update: function () {
                                    //本地重新渲染数据
                                    TableClass.utils.onRecvData.call(that);
                                }
                            };
                        func.call(this, evt, $datatr);
                    });
                    break;
                //展开行
                case "expand":
                    jqobjs.tb_box.on("click.laytableExpand", "td>a.laytable-expand", function () {
                        var $tr = $(this).closest("tr"),
                            expand = $tr.attr("expand");
                        if (expand !== undef) {
                            if (expand === "1") {
                                $tr.next("tr").hide();
                                $tr.attr("expand", "0");
                            } else {
                                $tr.next("tr").show();
                                $tr.attr("expand", "1");
                            }
                            return;
                        }

                        var index = $tr.data("index"),
                            datas = TableClass.utils.getData.call(that),
                            data = datas[index],
                            $trAppend = $([
                                '<tr>',
                                '<td><span class="laytable-expand"></span></td>',
                                '<td colspan="' + options.colsCount + '">',
                                '<div class="laytable-expand-box">',
                                '</div>',
                                '</td>',
                                '</tr>'
                            ].join(''));

                        $tr.attr("expand", "1").after($trAppend);

                        var $container = $trAppend.find(".laytable-expand-box"),
                            str = func.call(this, index, data, $container);
                        str != null && $container.append(str);
                    });
                    break;
                //以下事件写相应属性事件也是可以的
                //排序
                case "sort": options.onSort = func; break;
                //选中
                case "check": options.onCheck = func; break;
            }
        }
    }

    TableClass.prototype = {
        on: function (event, callback) {
            TableClass.utils.on.call(this, event, callback);
        },
        reload: function (option) {
            TableClass.utils.reload.call(this, option);
        },
        getSelectedRows: function () {
            var list = [],
                that = this,
                jqobjs = tableCache.jqobjs[that.tableIndex],
                $tbody = jqobjs.tb_body,
                datas = TableClass.utils.getData.call(that);

            $tbody.find("input.rayui-chk").each(function (a) {
                if (this.checked) {
                    list.push({
                        index: a,
                        data: datas[a]
                    });
                }
            });
            return list;
        },
        getData: function () {
            return TableClass.utils.getData.call(this);
        },
        getDataByIndex: function (index) {
            if (index === -1) return [];
            return TableClass.utils.getData.call(this)[index];
        }
    };

    var table2 = {
        options: TableClass.option,
        render: function (option) {
            scrollGap.width || function () {
                //可以在这里加载css
                TableClass.utils.initDefault();
            }();
            var obj = option.elem;
            if ($(obj).length === 0) return "DOM对象不存在";
            var classObj = new TableClass(obj, option);
            tableCache.tables[tableindex++] = classObj;
            return classObj;
        },
        reload: function (option) {
            //var obj = option.elem;
            //if ($(obj).length === 0) return "DOM对象不存在";
            //var classObj = new TableClass(obj, option);
           // tableCache.tables[tableindex++] = classObj;
            this.options = option;
            this.elem = option.elem;
            TableClass.utils.reload(option);
        }
    }

    exports("table2", table2);
});
