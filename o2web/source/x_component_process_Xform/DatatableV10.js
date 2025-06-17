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
        if (!this.mobileEditable) this.node.addClass('datatable_readonly');
        if (this.datatableMode==='mobile_default' && this.mobileEditable) {
                const buttonNode = new Element('oo-button.datatable_edit_button', {
                    text: MWF.xApplication.process.Xform.LP.editDatatable + 'v10',
                    type: 'light',
                    'left-icon': 'edit',
                });
                this.node.insertAdjacentElement('afterend', buttonNode);

                buttonNode.addEventListener('click', () => {
                    // const size = document.body.getSize();
                    const s = this.node.getSize();
                    const p = this.node.getPosition();

                    this.placeholderNode = new Element('div', {styles: {
                        width: s.x + 'px',
                        height: s.y + 'px',
                    }}).inject(this.node, 'before');

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
                        transition: "width 0.3s, height 0.3s, top 0.3s, left 0.3s"
                    });
                    

                    window.setTimeout(() => {
                        this.node.addClass('datatable_edit_mode');
                        this.reload();
                        this.node.setStyles({
                            width: '100%',
                            height: '100%',
                            top: 0,
                            left: 0
                        });
                        window.setTimeout(() => {
                            this.node.setStyles({
                                'overflow-y': 'auto',
                            });
                        });
                    });
                });
        }
    },

    loadDatatable: function () {
		debugger;
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

				this._loadDefaultDatatable();

                //修改dom结构
                const trs = this.tBody.querySelectorAll('tr');
                const headerTr = trs[0];
                const ths = headerTr.querySelectorAll('th')

                for (let i = 1; i<trs.length; i++){
                    const tds = trs[i].querySelectorAll('td');
                    for (let n = 0; n< tds.length; n++){
                        if (tds[n].hasClass('mwf_editaction')){
                            tds[n].setStyle('grid-column', '1 / -1');
                        }else if (tds[n].hasClass('mwf_sequence') || tds[n].hasClass('mwf_moveaction') ) {
                            //nothing
                        }else{
                            const tNode = ths[n].cloneNode(true);
                            tds[n].insertAdjacentElement('beforebegin', tNode);
                        }
                    }
                }

            }
        } else {
            //PC端模式
            this.datatableMode = 'pc';
            this._loadDefaultDatatable();
        }
    },

    _loadDefaultDatatable: function () {
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
            }.bind(this),
        );
    }
});
