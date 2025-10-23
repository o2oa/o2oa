MWF.xDesktop.requireApp('process.Xform', 'DatatablePC', null, false);
MWF.xApplication.process.Xform.DatatableV10 = new Class({
    Implements: [Events],
    Extends: MWF.xApplication.process.Xform.DatatablePC,
    isEdit: false,
    options: {
        moduleEvents: [
            'queryLoad',
            'postLoad',
            'load',
            'afterLoad',
            'beforeLoadLine',
            'afterLoadLine',
            'change',
            'addLine',
            'deleteLine',
            'afterDeleteLine',
            'editLine',
            'completeLineEdit',
            'cancelLineEdit',
            'beforeExport',
            'export',
            'beforeImport',
            'import',
            'validImport',
            'afterImport',
        ],
    },

    //在load方法最后执行
    _afterLoaded: function () {
        //兼容移动端
        // if (!this.mobileEditable) this.node.addClass('datatable_readonly');
        // if (this.datatableMode === 'mobile_default' && this.mobileEditable) {
        // const buttonNode = new Element('oo-button.datatable_edit_button', {
        //     text: MWF.xApplication.process.Xform.LP.editDatatable,
        //     type: 'light',
        //     'left-icon': 'edit',
        // });
        // this.node.insertAdjacentElement('afterend', buttonNode);

        this.node.addEventListener('click', () => {
            if (this.datatableMode === 'mobile_default') {
                this.datatableMode = 'mobile_edit';

                // const size = document.body.getSize();
                const s = this.node.getSize();
                const p = this.node.getPosition();

                this.placeholderNode = new Element('div', {
                    styles: {
                        width: s.x + 'px',
                        height: s.y + 'px',
                    },
                }).inject(this.node, 'before');

                document.body.appendChild(this.node);

                this.node.setStyles({
                    width: s.x + 'px',
                    height: s.y + 'px',
                    top: p.y + 'px',
                    left: p.x + 'px',
                    position: 'absolute',
                    'overflow-x': 'auto',
                    'overflow-y': 'hidden',
                    'z-index': 201,
                    transition: 'width 0.2s, height 0.2s, top 0.2s, left 0.2s',
                });

                window.setTimeout(() => {
                    this.node.addClass('datatable_edit_mode');
                    this.reload();
                    this.node.setStyles({
                        width: '100%',
                        height: '100%',
                        top: 0,
                        left: 0,
                    });
                    window.setTimeout(() => {
                        this.node.setStyles({
                            'overflow-y': 'auto',
                        });
                    });
                });
            }
        });

        //编辑完成后的处理
        this.addEvent("completeLineEdit", (line)=>{
            const ths = this._getHeadThs();
            this._checkMobileTr(line.node, ths);
        });

        //插入行后处理
        this.addEvent("addLine",(o)=>{
            const ths = this._getHeadThs();
            this._checkMobileTr(o.line.node, ths);
            o.line.node.scrollIntoView({behavior: 'smooth', block: "nearest", inline: "nearest"});
        });
        this.addEvent("editLine",(line)=>{
            const ths = this._getHeadThs();
            this._checkMobileTr(line.node, ths);
        });
        this.addEvent("cancelLineEdit",(line)=>{
            const ths = this._getHeadThs();
            this._checkMobileTr(line.node, ths);
        });
        this.addEvent("change",(o)=>{
            if (o.type==='move'){
                o.line.node.scrollIntoView({behavior: 'smooth', block: "nearest", inline: "nearest"});
                o.line.node.addClass('mwf_move_line');
                window.setTimeout(() => {
                    o.line.node.removeClass('mwf_move_line');
                }, 100);
            }
        })
        
        //验证后处理
        this.addEvent("validationLine",(line, flag)=>{
            if (!flag) {
                if (!line.node.isIntoView()) line.node.scrollIntoView({behavior: 'smooth', block: "nearest", inline: "nearest"});
            }
        });
        
    },


    _loadMobileTitle: function () {
        this.mobileTitleNode = new Element('div.mwf_datatable_mobile_title');
        this.node.insertAdjacentElement('afterbegin', this.mobileTitleNode);
        this.mobileTitleNode.textContent = this.json.name || MWF.xApplication.process.Xform.LP.datatableTitle;
    },
    _getHeadThs: function(){
        const trs = this.tBody.querySelectorAll('tr');
        return trs[0].querySelectorAll('th');
    },
    _checkMobileTr: function(tr, ths){
        const tds = tr.querySelectorAll('td');
        const isTotal = tr.hasClass('mwf_totaltr');

        for (let n = 0; n < tds.length; n++) {
            if (tds[n].hasClass('mwf_sequence') || ths[n].hasClass('mwf_sequence')){
                ths[n].addClass('mwf_sequence');
                tds[n].addClass('mwf_sequence');
                if (isTotal){
                    tds[n].setStyle('display', 'none');
                }
                // tds[n].setStyle('display', 'absolute');
            }else if (ths[n].hasClass('mwf_addlineaction')) {
                tds[n].addClass('mwf_editaction');
                tds[n].setStyle('grid-column', '1 / -1');
            } else if (ths[n].hasClass('mwf_moveaction')) {
                tds[n].addClass('mwf_moveaction');
                const icon = tds[n].querySelector('div');
                if (icon){
                    icon.textContent = '';
                    icon.addClass('ooicon-up');
                }
            } else {
                const tNode = ths[n].cloneNode(true);
                tNode.addClass('mwf_mobile_edit_th');
                tds[n].insertAdjacentElement('beforebegin', tNode);
                tds[n].addClass('mwf_origional');
            }
        }
    }, 
    
    _loadMobileActionNode: function(){
        //添加操作按钮
        const actionNode = new Element('div.mwf_mobile_actions');
        this.node.appendChild(actionNode);
        actionNode.addEventListener('click', (e)=>{
            e.stopPropagation();
        });
        this.mobileActionNode = actionNode;
    },
    _loadMobileAddAction: function(){
        //添加新增条目按钮
        if (this.addable){
            const addAction = new Element('oo-button.mwf_mobile_addAction', {type: 'light'});
            addAction.setAttribute('text', MWF.xApplication.process.Xform.LP.addLine);
            this.mobileActionNode.appendChild(addAction);
            addAction.addEventListener('click', (e) => {
                const line = this.addLine();
                e.stopPropagation();
            });
        }
    },
    _loadMobileOkAction: function(){
        //添加编辑完成按钮
        const okAction = new Element('oo-button.mwf_mobile_okAction');
        okAction.setAttribute('text', MWF.xApplication.process.Xform.LP.editOk);
        this.mobileActionNode.appendChild(okAction);
        okAction.addEventListener('click', (e) => {
            e.stopPropagation();
            const s = this.placeholderNode.getSize();
            const p = this.placeholderNode.getPosition();

            this.node.setStyles({
                width: s.x + 'px',
                height: s.y + 'px',
                top: p.y + 'px',
                left: p.x + 'px',
                'overflow-x': 'auto',
                'overflow-y': 'hidden',
                transition: 'width 0.2s, height 0.2s, top 0.2s, left 0.2s',
            });

            this.mobileTitleNode.destroy();
            this.node.removeClass('datatable_edit_mode');
            this.reload();

            window.setTimeout(() => {
                this.placeholderNode.insertAdjacentElement('beforebegin', this.node);
                this.placeholderNode.destroy();
                this.node.setStyles({
                    height: 'unset',
                    position: 'static',
                    'overflow-y': 'auto',
                });
                this.datatableMode = 'mobile_default';
            }, 200);
        });
    },

    loadDatatable: function () {
        if (o2.isMediaMobile()) {
            //移动端模式，默认表格显示模式，不进行编辑，没有多行编辑等
            if (!this.node.hasClass('datatable_edit_mode')) {
                //移动端默认模式
                this.mobileMultiEditMode = this.multiEditMode;
                this.mobileEditable = this.editable;
                this.multiEditMode = false;
                this.editable = false;
                this.datatableMode = 'mobile_default';
                this._loadDefaultDatatable();
            } else {
                //移动端编辑模式
                this.datatableMode = 'mobile_edit';
                this.multiEditMode = this.mobileMultiEditMode;
                this.editable = this.mobileEditable;

                this._loadDefaultDatatable(() => {
                    //修改dom结构
                    //创建标题栏
                    this._loadMobileTitle();

                    const trs = this.tBody.querySelectorAll('tr');
                    const ths = trs[0].querySelectorAll('th');

                    for (let i = 1; i < trs.length; i++) {
                        this._checkMobileTr(trs[i], ths);
                    }

                    this._loadMobileActionNode();
                    this._loadMobileAddAction();
                    this._loadMobileOkAction();
                });
            }
        } else {
            //PC端模式
            this.datatableMode = 'pc';
            this._loadDefaultDatatable();
        }
    },

    _loadDefaultDatatable: function (cb) {
        this.loading = true;
        this._loadStyles();

        this._loadTitleTr();
        this._loadTemplate();
        this._loadTotalTr();

        this.fireEvent('load');
        this._loadDatatable(
            function () {
                this._loadImportExportAction();
                this.fieldModuleLoaded = true;
                this.loading = false;
                this.fireEvent('postLoad');

                if (cb) cb();
            }.bind(this),
        );
    },
    _removeEl: function () {
        var node;
        if (this.titleTr) {
            node = this.titleTr.getElement('th.mwf_addlineaction');
            if (node) node.destroy();
            node = this.titleTr.getElement('th.mwf_moveaction');
            if (node) node.destroy();
        }
        if (this.templateTr) {
            node = this.templateTr.getElement('td.mwf_editaction');
            if (node) node.destroy();
            node = this.templateTr.getElement('td.mwf_moveaction');
            if (node) node.destroy();
        }
        if (this.totalTr) {
            this.totalTr.destroy();
            this.totalTr = null;
        }
        if (this.exportActionNode) {
            this.exportActionNode.destroy();
            this.totalTr = null;
        }
        if (this.importActionNode) {
            this.importActionNode.destroy();
            this.totalTr = null;
        }
        if (this.mobileActionNode) {
            this.mobileActionNode.destroy();
            this.mobileActionNode = null;
        }
        const ths = this.node.getElements('.mwf_mobile_edit_th');
        ths.destroy();
    },
    // _moveUpLine: function(ev, line){
    //     if( this.currentEditedLine && !this._completeLineEdit(null, true) )return false;

    //     const prevNode = line.node.previousElementSibling;
    //     if (prevNode){
    //         const cloneNode = line.node.cloneNode(true);
    //         cloneNode.style.opacity = 0;
    //         prevNode.insertAdjacentElement('beforebegin', cloneNode);
    //     }

    //     var data, upData, curData;
    //     if( this.isShowAllSection ){
    //         if (line.options.indexInSectionLine === 0) return;

    //         data = this.getBusinessDataById();
    //         var sdata = data[ this.sectionBy ];
    //         if( !sdata )return;

    //         upData = sdata.data[line.options.indexInSectionLine - 1];
    //         curData = sdata.data[line.options.indexInSectionLine];
    //         sdata.data[line.options.indexInSectionLine] = upData;
    //         sdata.data[line.options.indexInSectionLine - 1] = curData;

    //         this.setAllSectionData( data, false, "moveUpList" );
    //     }else {
    //         if (line.options.index === 0) return;

    //         data = this.getInputData();
    //         upData = data.data[line.options.index - 1];
    //         curData = data.data[line.options.index];
    //         data.data[line.options.index] = upData;
    //         data.data[line.options.index - 1] = curData;
    //         this.setData(data, false, "moveUpList");
    //     }
    //     this.fireEvent("change", [{lines: this.lineList, "type":"move", line: line}]);
    // }
});
