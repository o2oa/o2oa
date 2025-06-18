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
                debugger;
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
                    transition: 'width 0.3s, height 0.3s, top 0.3s, left 0.3s',
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
        // }
    },

    _
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
                    const trs = this.tBody.querySelectorAll('tr');
                    const headerTr = trs[0];
                    const ths = headerTr.querySelectorAll('th');

                    for (let i = 1; i < trs.length; i++) {
                        const tds = trs[i].querySelectorAll('td');
                        for (let n = 0; n < tds.length; n++) {
                            if (ths[n].hasClass('mwf_addlineaction')) {
                                tds[n].addClass('mwf_editaction');``
                                tds[n].setStyle('grid-column', '1 / -1');
                            } else if (ths[n].hasClass('mwf_moveaction')) {
                                tds[n].addClass('mwf_moveaction');
                            } else {
                                const tNode = ths[n].cloneNode(true);
                                tNode.addClass('mwf_mobile_edit_th');
                                tds[n].insertAdjacentElement('beforebegin', tNode);
                                tds[n].addClass('mwf_origional');
                            }
                        }
                    }

                    //添加操作按钮
                    const actionNode = new Element('div.mwf_mobile_actions');
                    this.node.appendChild(actionNode);
                    actionNode.addEventListener('click', (e)=>{
                        e.stopPropagation();
                    });
                    this.mobileActionNode = actionNode;

                    if (this.addable){
                        const addAction = new Element('oo-button.mwf_mobile_addAction', {type: 'light', 'left-icon': 'create'});
                        addAction.setAttribute('text', MWF.xApplication.process.Xform.LP.addLine);
                        actionNode.appendChild(addAction);
                        addAction.addEventListener('click', () => {
                            const line = this.addLine();
                            line.node.scrollIntoView({behavior: 'smooth'});
                        });
                    }
                   
                    const okAction = new Element('oo-button.mwf_mobile_okAction');
                    okAction.setAttribute('text', MWF.xApplication.process.Xform.LP.editOk);
                    actionNode.appendChild(okAction);
                    okAction.addEventListener('click', () => {
                        const s = this.placeholderNode.getSize();
                        const p = this.placeholderNode.getPosition();

                        this.node.setStyles({
                            width: s.x + 'px',
                            height: s.y + 'px',
                            top: p.y + 'px',
                            left: p.x + 'px',
                            'overflow-x': 'auto',
                            'overflow-y': 'hidden',
                            transition: 'width 0.3s, height 0.3s, top 0.3s, left 0.3s',
                        });

                        this.node.removeClass('datatable_edit_mode');
                        this.reload();

                        window.setTimeout(() => {
                            this.placeholderNode.insertAdjacentElement('beforebegin', this.node);
                            this.placeholderNode.destroy();
                            this.node.setStyles({
                                position: 'static',
                                'overflow-y': 'auto',
                            });
                            this.datatableMode = 'mobile_default';
                        }, 300);
                    });
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
    }
});
