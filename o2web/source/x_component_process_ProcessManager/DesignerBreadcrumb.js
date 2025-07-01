o2.require("o2.widget.PinYin", null, false);
var _portalAction = o2.Actions.load('x_portal_assemble_designer');
var _processAction = o2.Actions.load('x_processplatform_assemble_designer');
var _cmsAction = o2.Actions.load('x_cms_assemble_control');
var _queryAction = o2.Actions.load('x_query_assemble_designer');
var _serviceAction = o2.Actions.load('x_program_center');
var _openApp = o2.api.page.openApplication;
var _ooiconMap = {
    'portal.PageDesigner': 'pagepeizhi',
    'portal.WidgetDesigner': 'app-center',
    'portal.DictionaryDesigner': 'js',
    'portal.ScriptDesigner': 'jiaoben',
    'process.FormDesigner': 'biaodan',
    'process.ProcessDesigner': 'a-flowprocess',
    'process.DictionaryDesigner': 'js',
    'process.ScriptDesigner': 'jiaoben',
    'cms.CategoryManager': 'bujianpeizhi',
    'cms.FormDesigner': 'biaodan',
    'cms.DictionaryDesigner': 'js',
    'cms.ScriptDesigner': 'jiaoben',
    'query.ViewDesigner': 'shitupeizhi3',
    'query.StatDesigner': 'shujubiao2',
    'query.TableDesigner': 'shujubiao',
    'query.StatementDesigner': 'chaxunpeizhi',
    'query.ImporterDesigner': 'file_upload',
    'service.AgentDesigner': 'dailipeizhi1',
    'service.InvokeDesigner': 'jiekoupeizhi21',
    'service.ScriptDesigner': 'jiaoben',
    'service.DictionaryDesigner': 'js'
};
var _appNameMap = {
    'portal.PageDesigner': '门户页面',
    'portal.WidgetDesigner': '门户部件',
    'portal.DictionaryDesigner': '门户数据字典',
    'portal.ScriptDesigner': '门户脚本',
    'process.FormDesigner': '流程表单',
    'process.ProcessDesigner': '流程设计',
    'process.DictionaryDesigner': '流程数据字典',
    'process.ScriptDesigner': '流程脚本',
    'cms.CategoryManager': '内容管理分类',
    'cms.FormDesigner': '内容管理表单',
    'cms.DictionaryDesigner': '内容管理数据字典',
    'cms.ScriptDesigner': '内容管理脚本',
    'query.ViewDesigner': '视图配置',
    'query.StatDesigner': '统计配置',
    'query.TableDesigner': '数据表',
    'query.StatementDesigner': '查询配置',
    'query.ImporterDesigner': '导入模型',
    'service.AgentDesigner': '代理',
    'service.InvokeDesigner': '接口',
    'service.ScriptDesigner': '服务中心脚本',
    'service.DictionaryDesigner': '服务中心数据字典'
};

var UNCATEGORIZED = 'uncategorized';
var ALL = 'all';
var RECENTLY_DESIGNER_NAME = 'RecentlyOpenedDesigner';
var RECENTLY_DESIGNER_MAX_COUNT = 20; //最近打开的设计元素数量

var _checkClass = (dom, clazz, flag)=>{
    !!flag ?
        !dom.hasClass(clazz) && dom.addClass(clazz) :
        dom.hasClass(clazz) && dom.removeClass(clazz);
};

var _toPY = (item)=>{
    return `${item.name}`.toPY().toLowerCase() + '#' +
        `${item.alias||''}`.toPY().toLowerCase() + '#' +
        `${item.name}`.toPYFirst().toLowerCase() + '#' +
        `${item.alias||''}`.toPYFirst().toLowerCase() + '#';
};

var _sort =  (data, key='name', isDesc=false)=>{
    return data.sort(function (a, b){
        var av = !!key ? a[key] : a, bv = !!key ? b[key] : b;
        if( typeOf(av) === 'string' && typeOf(bv) === 'string' ){
            var isLetterA = /^[a-zA-Z0-9]/.test(av);
            var isLetterB = /^[a-zA-Z0-9]/.test(bv);

            if (isLetterA && !isLetterB) return isDesc ? 1 : -1; // a是字母，b不是，a排在前面
            if (!isLetterA && isLetterB) return isDesc ? -1 : 1;  // a不是字母，b是，b排在前面

            return isDesc ?  bv.localeCompare(av) : av.localeCompare(bv);
        }
        return isDesc ? (bv - av) : (av - bv);
    }.bind(this));
};

var o2DesignerConfig = {
    type: 'root',
    children: [
        {
            handleClick: () => {
                _openApp('portal.PortalExplorer');
            },
            name: '门户管理',
            id: 'portal.PortalExplorer',
            ooicon: 'portal',
            type: 'app-category',
            children: [{
                name: '门户应用',
                id: 'portal.PortalManager',
                label: '应用',
                type: 'app',
                categorized: true,
                handleClick: (item) => {
                    _openApp('portal.PortalManager', null, {application: item.id});
                },
                listAction: () => {
                    return _portalAction.PortalAction.list().then((json) => {
                        return _sort(json.data, 'name').map((item) => {
                            return {
                                ...item,
                                pinyin: _toPY(item),
                                category: item.portalCategory,
                                defaultIcon: '../x_component_portal_PortalExplorer/$Main/default/icon/application.png',
                                type: 'app'
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '页面配置',
                        id: 'portal.PageManager',
                        type: 'desiginer-category',
                        ooicon: 'pagepeizhi',
                        handleClick: (item, appid) => {
                            _openApp('portal.PortalManager', null, {navi: 0, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('portal.PageDesigner', opt, opt);
                        },
                        children: [{
                            label: '页面',
                            type: 'designer',
                            categorized: true,
                            handleClick: (page) => {
                                _openApp('portal.PageDesigner', null, {id: page.id});
                            },
                            listAction: (appid) => {
                                return _portalAction.PageAction.listWithPortal(appid).then((pages) => {
                                    return _sort(pages.data).map((page) => {
                                        return {
                                            ...page,
                                            appid: appid,
                                            pinyin: _toPY(page)
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '部件配置',
                        id: 'portal.WidgetDesigner',
                        type: 'designer-category',
                        label: '部件',
                        ooicon: 'app-center',
                        handleClick: (item, appid) => {
                            _openApp('portal.PortalManager', null, {navi: 1, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('portal.WidgetDesigner', opt, opt);
                        },
                        children: [{
                            label: '部件',
                            type: 'designer',
                            categorized: true,
                            handleClick: (portal) => {
                                _openApp('portal.WidgetDesigner', null, {id: portal.id});
                            },
                            listAction: (appid) => {
                                return _portalAction.WidgetAction.listWithPortal(appid).then((widgets) => {
                                    return _sort(widgets.data).map((widget) => {
                                        return {
                                            ...widget,
                                            appid: appid,
                                            pinyin: _toPY(widget)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据字典',
                        id: 'portal.DictionaryDesigner',
                        type: 'designer-category',
                        label: '数据字典',
                        ooicon: 'js',
                        handleClick: (item, appid) => {
                            _openApp('portal.PortalManager', null, {navi: 2, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('portal.DictionaryDesigner', opt, opt);
                        },
                        children: [{
                            label: '数据字典',
                            type: 'designer',
                            handleClick: (dict) => {
                                _openApp('portal.DictionaryDesigner', null, {
                                    id: dict.id, application: {
                                        id: dict.appid
                                    }
                                });
                            },
                            listAction: (appid) => {
                                return _portalAction.DictAction.listWithApplication(appid).then((dicts) => {
                                    return _sort(dicts.data).map((dict) => {
                                        return {
                                            ...dict,
                                            appid: appid,
                                            pinyin: _toPY(dict)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '脚本配置',
                        id: 'portal.ScriptDesigner',
                        type: 'designer-category',
                        label: '脚本配置',
                        ooicon: 'jiaoben',
                        handleClick: (item, appid) => {
                            _openApp('portal.PortalManager', null, {navi: 3, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('portal.ScriptDesigner', opt, opt);
                        },
                        children: [{
                            label: '脚本配置',
                            type: 'designer',
                            handleClick: (script) => {
                                _openApp('portal.ScriptDesigner', null, {
                                    id: script.id,
                                    application: {id: script.appid}
                                });
                            },
                            listAction: (appid) => {
                                return _portalAction.ScriptAction.listWithPortal(appid).then((scripts) => {
                                    return _sort(scripts.data).map((script) => {
                                        return {
                                            ...script,
                                            appid: appid,
                                            pinyin: _toPY(script)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '资源文件',
                        id: 'portal.FileDesigner',
                        type: 'designer-category',
                        ooicon: 'folder-open',
                        handleClick: (item, appid) => {
                            _openApp('portal.PortalManager', null, {navi: 4, application: appid});
                        }
                    }, {
                        name: '门户属性',
                        id: 'portal.Property',
                        type: 'designer-category',
                        ooicon: 'jiekoupeizhi2',
                        handleClick: (item, appid) => {
                            _openApp('portal.PortalManager', null, {navi: 5, application: appid});
                        }
                    }]
            }]
        },
        {
            handleClick: () => {
                _openApp('process.ApplicationExplorer');
            },
            name: '流程管理',
            id: 'process.ApplicationExplorer',
            ooicon: 'liucheng',
            type: 'app-category',
            children: [{
                name: '流程应用',
                id: 'process.ProcessManager',
                label: '应用',
                type: 'app',
                categorized: true,
                handleClick: (item) => {
                    _openApp('process.ProcessManager', null, {application: item.id});
                },
                listAction: () => {
                    return _processAction.ApplicationAction.listSummary().then((json) => {
                        return _sort(json.data, 'name').map((item) => {
                            return {
                                ...item,
                                category: item.applicationCategory,
                                defaultIcon: '../x_component_process_ApplicationExplorer/$Main/default/icon/application.png',
                                type: 'app',
                                pinyin: _toPY(item)
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '表单配置',
                        id: 'process.FormManager',
                        type: 'designer-category',
                        ooicon: 'biaodan',
                        handleClick: (item, appid) => {
                            _openApp('process.ProcessManager', null, {navi: 0, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('process.FormDesigner', opt, opt);
                        },
                        children: [{
                            label: '表单',
                            type: 'designer',
                            categorized: true,
                            handleClick: (form) => {
                                _openApp('process.FormDesigner', null, {id: form.id});
                            },
                            listAction: (appid) => {
                                return _processAction.FormAction.listWithApplication(appid).then((forms) => {
                                    return _sort(forms.data).map((form) => {
                                        return {
                                            ...form,
                                            appid: appid,
                                            pinyin: _toPY(form)
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '流程配置',
                        id: 'process.ProcessDesigner',
                        type: 'designer-category',
                        label: '流程',
                        ooicon: 'a-flowprocess',
                        handleClick: (item, appid) => {
                            _openApp('process.ProcessManager', null, {navi: 1, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('process.ProcessDesigner', opt, opt);
                        },
                        children: [{
                            label: '流程',
                            type: 'designer',
                            categorized: true,
                            handleClick: (process) => {
                                _openApp('process.ProcessDesigner', null, {id: process.id});
                            },
                            listAction: (appid) => {
                                return _processAction.ProcessAction.listWithApplication(appid).then((processes) => {
                                    return _sort(processes.data).map((process) => {
                                        return {
                                            ...process,
                                            appid: appid,
                                            pinyin: _toPY(process)
                                        };
                                    });
                                });
                            }
                        }]
                    }, {
                        name: '数据字典',
                        id: 'process.DictionaryDesigner',
                        type: 'designer-category',
                        label: '数据字典',
                        ooicon: 'js',
                        handleClick: (item, appid) => {
                            _openApp('process.ProcessManager', null, {navi: 2, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('process.DictionaryDesigner', opt, opt);
                        },
                        children: [{
                            label: '数据字典',
                            type: 'designer',
                            handleClick: (dict) => {
                                _openApp('process.DictionaryDesigner', null, { id: dict.id, application: { id: dict.appid } });
                            },
                            listAction: (appid) => {
                                return _processAction.ApplicationDictAction.listWithApplication(appid).then((dicts) => {
                                    return _sort(dicts.data).map((dict) => {
                                        return {
                                            ...dict,
                                            appid: appid,
                                            pinyin: _toPY(dict)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '脚本配置',
                        id: 'process.ScriptDesigner',
                        type: 'designer-category',
                        label: '脚本配置',
                        ooicon: 'jiaoben',
                        handleClick: (item, appid) => {
                            _openApp('process.ProcessManager', null, {navi: 3, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('process.ScriptDesigner', opt, opt);
                        },
                        children: [{
                            label: '脚本配置',
                            type: 'designer',
                            handleClick: (script) => {
                                _openApp('process.ScriptDesigner', null, {
                                    id: script.id,
                                    application: {id: script.appid}
                                });
                            },
                            listAction: (appid) => {
                                return _processAction.ScriptAction.listWithApplication(appid).then((scripts) => {
                                    return _sort(scripts.data).map((script) => {
                                        return {
                                            ...script,
                                            appid: appid,
                                            pinyin: _toPY(script)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '资源文件',
                        id: 'process.FileDesigner',
                        type: 'designer-category',
                        ooicon: 'folder-open',
                        handleClick: (item, appid) => {
                            _openApp('process.ProcessManager', null, {navi: 4, application: appid});
                        }
                    }, {
                        name: '应用属性',
                        id: 'process.Property',
                        type: 'designer-category',
                        ooicon: 'jiekoupeizhi2',
                        handleClick: (item, appid) => {
                            _openApp('process.ProcessManager', null, {navi: 5, application: appid});
                        }
                    }]
            }]
        },
        {
            handleClick: () => {
                _openApp('cms.Column');
            },
            name: '内容管理',
            id: 'cms.Column',
            ooicon: 'note',
            type: 'app-category',
            children: [{
                name: '内容管理',
                id: 'cms.ColumnManager',
                label: '应用',
                type: 'app',
                categorized: true,
                handleClick: (item) => {
                    _openApp('cms.ColumnManager', null, {column: item.id});
                },
                listAction: () => {
                    return _cmsAction.AppInfoAction.listAllAppInfo().then((json) => {
                        return _sort(json.data, 'appName').map((item) => {
                            return {
                                ...item,
                                category: item.appType,
                                alias: item.appAlias,
                                name: item.appName,
                                icon: item.appIcon,
                                defaultIcon: '../x_component_cms_Column/$Main/default/icon/column.png',
                                type: 'app',
                                pinyin: _toPY({alias: item.appAlias, name: item.appName})
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '分类配置',
                        id: 'cms.CategoryManager',
                        type: 'designer-category',
                        ooicon: 'bujianpeizhi',
                        handleClick: (item, appid) => {
                            _openApp('cms.ColumnManager', null, {navi: 'categoryConfig', column: appid});
                        },
                        children: [{
                            label: '分类',
                            type: 'designer',
                            handleClick: (category) => {
                                _openApp('cms.ColumnManager', null, {
                                    navi: 'categoryConfig',
                                    column: category.appid,
                                    categoryId: category.id
                                });
                            },
                            listAction: (appid) => {
                                return _cmsAction.CategoryInfoAction.listViewableCategoryInfo_AllType(appid).then((categorys) => {
                                    return _sort(categorys.data).map((category) => {
                                        return {
                                            ...category,
                                            alias: category.categoryAlias,
                                            name: category.categoryName,
                                            appid: category.appId,
                                            pinyin: _toPY({alias: category.categoryAlias, name: category.categoryName})
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '表单配置',
                        id: 'cms.FormDesigner',
                        type: 'designer-category',
                        label: '表单',
                        ooicon: 'biaodan',
                        handleClick: (item, appid) => {
                            _openApp('cms.ColumnManager', null, {navi: 'formConfig', column: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('cms.FormDesigner', opt, opt);
                        },
                        children: [{
                            label: '表单',
                            type: 'designer',
                            handleClick: (form) => {
                                _openApp('cms.FormDesigner', null, {id: form.id});
                            },
                            listAction: (appid) => {
                                return _cmsAction.FormAction.listFormByAppId(appid).then((forms) => {
                                    return _sort(forms.data).map((form) => {
                                        return {
                                            ...form,
                                            appid: appid,
                                            pinyin: _toPY(form)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据字典',
                        id: 'cms.DictionaryDesigner',
                        type: 'designer-category',
                        label: '数据字典',
                        ooicon: 'js',
                        handleClick: (item, appid) => {
                            _openApp('cms.ColumnManager', null, {navi: 'dataConfig', column: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('cms.DictionaryDesigner', opt, opt);
                        },
                        children: [{
                            label: '数据字典',
                            type: 'designer',
                            handleClick: (dict) => {
                                _openApp('cms.DictionaryDesigner', null, {
                                    id: dict.id, application: { id: dict.appid }
                                });
                            },
                            listAction: (appid) => {
                                return _cmsAction.AppDictDesignAction.listWithAppInfo(appid).then((dicts) => {
                                    return _sort(dicts.data).map((dict) => {
                                        return {
                                            ...dict,
                                            appid: appid,
                                            pinyin: _toPY(dict)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '脚本配置',
                        id: 'cms.ScriptDesigner',
                        type: 'designer-category',
                        label: '脚本配置',
                        ooicon: 'jiaoben',
                        handleClick: (item, appid) => {
                            _openApp('cms.ColumnManager', null, {navi: 'scriptConfig', column: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('cms.ScriptDesigner', opt, opt);
                        },
                        children: [{
                            label: '脚本配置',
                            type: 'designer',
                            handleClick: (script) => {
                                _openApp('cms.ScriptDesigner', null, {id: script.id, application: {id: script.appid}});
                            },
                            listAction: (appid) => {
                                return _cmsAction.ScriptAction.listWithApplication(appid).then((scripts) => {
                                    return _sort(scripts.data).map((script) => {
                                        return {
                                            ...script,
                                            appid: appid,
                                            pinyin: _toPY(script)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '资源文件',
                        id: 'cms.FileDesigner',
                        type: 'designer-category',
                        ooicon: 'folder-open',
                        handleClick: (item, appid) => {
                            _openApp('cms.ColumnManager', null, {navi: 'fileConfig', column: appid});
                        }
                    }, {
                        name: '栏目属性',
                        id: 'cms.Property',
                        type: 'designer-category',
                        ooicon: 'jiekoupeizhi2',
                        handleClick: (item, appid) => {
                            _openApp('cms.ColumnManager', null, {navi: 'applicationProperty', column: appid});
                        }
                    }]
            }]
        },
        {
            handleClick: () => {
                _openApp('query.QueryExplorer');
            },
            name: '数据中心',
            id: 'query.QueryExplorer',
            ooicon: 'integral',
            type: 'app-category',
            children: [{
                name: '数据应用',
                id: 'query.QueryManager',
                label: '应用',
                type: 'app',
                categorized: true,
                handleClick: (item) => {
                    _openApp('query.QueryManager', null, {application: item.id});
                },
                listAction: () => {
                    return _queryAction.QueryAction.listAll().then((json) => {
                        return _sort(json.data, 'name').map((item) => {
                            return {
                                ...item,
                                category: item.queryCategory,
                                defaultIcon: '../x_component_query_QueryExplorer/$Main/default/icon/application.png',
                                type: 'app',
                                pinyin: _toPY(item)
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '视图配置',
                        id: 'query.ViewDesigner',
                        type: 'designer-category',
                        ooicon: 'shitupeizhi3',
                        handleClick: (item, appid) => {
                            _openApp('query.QueryManager', null, {navi: 0, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('query.ViewDesigner', opt, opt);
                        },
                        children: [{
                            label: '视图',
                            type: 'designer',
                            handleClick: (view) => {
                                _openApp('query.ViewDesigner', null, {id: view.id, application: {id: view.appid}});
                            },
                            listAction: (appid) => {
                                return _queryAction.ViewAction.listWithQuery(appid).then((views) => {
                                    return _sort(views.data).map((view) => {
                                        return {
                                            ...view,
                                            appid: appid,
                                            pinyin: _toPY(view)
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '统计配置',
                        id: 'query.StatDesigner',
                        type: 'designer-category',
                        label: '统计',
                        ooicon: 'shujubiao2',
                        handleClick: (item, appid) => {
                            _openApp('query.QueryManager', null, {navi: 1, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('query.StatDesigner', opt, opt);
                        },
                        children: [{
                            label: '统计',
                            type: 'designer',
                            handleClick: (stat) => {
                                _openApp('query.StatDesigner', null, {id: stat.id, application: {id: stat.appid}});
                            },
                            listAction: (appid) => {
                                return _queryAction.StatAction.listWithQuery(appid).then((stats) => {
                                    return _sort(stats.data).map((stat) => {
                                        return {
                                            ...stat,
                                            appid: appid,
                                            pinyin: _toPY(stat)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据表',
                        id: 'query.TableDesigner',
                        type: 'designer-category',
                        label: '数据表',
                        ooicon: 'shujubiao',
                        handleClick: (item, appid) => {
                            _openApp('query.QueryManager', null, {navi: 2, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('query.TableDesigner', opt, opt);
                        },
                        children: [{
                            label: '数据表',
                            type: 'designer',
                            handleClick: (table) => {
                                _openApp('query.TableDesigner', null, {
                                    id: table.id, application: {
                                        id: table.appid
                                    }
                                });
                            },
                            listAction: (appid) => {
                                return _queryAction.TableAction.listWithQuery(appid).then((tables) => {
                                    return _sort(tables.data).map((table) => {
                                        return {
                                            ...table,
                                            appid: appid,
                                            pinyin: _toPY(table)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '查询配置',
                        id: 'query.StatementDesigner',
                        type: 'designer-category',
                        label: '脚本配置',
                        ooicon: 'chaxunpeizhi',
                        handleClick: (item, appid) => {
                            _openApp('query.QueryManager', null, {navi: 3, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('query.StatementDesigner', opt, opt);
                        },
                        children: [{
                            label: '查询配置',
                            type: 'designer',
                            handleClick: (statement) => {
                                _openApp('query.StatementDesigner', null, {
                                    id: statement.id,
                                    application: {id: statement.appid}
                                });
                            },
                            listAction: (appid) => {
                                return _queryAction.StatementAction.listWithQuery(appid).then((statements) => {
                                    return _sort(statements.data).map((statement) => {
                                        return {
                                            ...statement,
                                            appid: appid,
                                            pinyin: _toPY(statement)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '导入模型',
                        id: 'query.ImporterDesigner',
                        type: 'designer-category',
                        ooicon: 'file_upload',
                        handleClick: (item, appid) => {
                            _openApp('query.QueryManager', null, {navi: 4, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            _openApp('query.ImporterDesigner', opt, opt);
                        },
                        children: [{
                            label: '导入模型',
                            type: 'designer',
                            handleClick: (importer) => {
                                _openApp('query.ImporterDesigner', null, {
                                    id: importer.id,
                                    application: {id: importer.appid}
                                });
                            },
                            listAction: (appid) => {
                                return _queryAction.ImportModelAction.listWithQuery(appid).then((items) => {
                                    return _sort(items.data).map((item) => {
                                        return {
                                            ...item,
                                            appid: appid,
                                            pinyin: _toPY(item)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据中心属性',
                        id: 'query.Property',
                        type: 'designer-category',
                        ooicon: 'jiekoupeizhi2',
                        handleClick: (item, appid) => {
                            _openApp('query.QueryManager', null, {navi: 5, application: appid});
                        }
                    }]
            }]
        },
        {
            handleClick: () => {
                _openApp('service.ServiceManager');
            },
            name: '服务管理',
            id: 'service.ServiceManager',
            ooicon: 'process-service',
            type: 'app-category',
            children: [{
                name: '代理配置',
                id: 'service.AgentDesigner',
                type: 'designer-category',
                ooicon: 'dailipeizhi1',
                handleClick: (item, appid) => {
                    _openApp('service.ServiceManager', null, {navi: 0});
                },
                handleCreate: ()=>{
                    _openApp('service.AgentDesigner', null, {});
                },
                children: [{
                    label: '代理',
                    type: 'designer',
                    handleClick: (item) => {
                        _openApp('service.AgentDesigner', null, {id: item.id});
                    },
                    listAction: () => {
                        return _serviceAction.AgentAction.list().then((agents) => {
                            return _sort(agents.data).map(agent=>{
                                return {
                                    ...agent,
                                    pinyin: _toPY(agent)
                                };
                            });
                        })
                    }
                }]
            },
                {
                    name: '接口配置',
                    id: 'portal.InvokeDesigner',
                    type: 'designer-category',
                    label: '接口',
                    ooicon: 'jiekoupeizhi21',
                    handleClick: (item, appid) => {
                        _openApp('service.ServiceManager', null, {navi: 1});
                    },
                    handleCreate: ()=>{
                        _openApp('service.InvokeDesigner', null, {});
                    },
                    children: [{
                        label: '接口',
                        type: 'designer',
                        categorized: true,
                        handleClick: (item) => {
                            _openApp('service.InvokeDesigner', null, {id: item.id});
                        },
                        listAction: () => {
                            return _serviceAction.InvokeAction.list().then((items) => {
                                return _sort(items.data).map(item=>{
                                    return {
                                        ...item,
                                        pinyin: _toPY(item)
                                    };
                                });
                            })
                        }
                    }]
                }, {
                    name: '脚本配置',
                    id: 'service.ScriptDesigner',
                    type: 'designer-category',
                    label: '脚本配置',
                    ooicon: 'jiaoben',
                    handleClick: (item, appid) => {
                        _openApp('service.ServiceManager', null, {navi: 2});
                    },
                    handleCreate: ()=>{
                        _openApp('service.ScriptDesigner', null, {});
                    },
                    children: [{
                        label: '脚本配置',
                        type: 'designer',
                        handleClick: (item) => {
                            _openApp('service.ScriptDesigner', null, { id: item.id });
                        },
                        listAction: () => {
                            return _serviceAction.ScriptAction.list().then((items) => {
                                return _sort(items.data).map(item=>{
                                    return {
                                        ...item,
                                        pinyin: _toPY(item)
                                    };
                                });
                            })
                        }
                    }]
                }, {
                    name: '数据配置',
                    id: 'service.DictionaryDesigner',
                    type: 'designer-category',
                    label: '数据配置',
                    ooicon: 'js',
                    handleClick: () => {
                        _openApp('service.ServiceManager', null, {navi: 3});
                    },
                    handleCreate: ()=>{
                        _openApp('service.DictionaryDesigner', null, {});
                    },
                    children: [{
                        label: '数据配置',
                        type: 'designer',
                        handleClick: (item) => {
                            _openApp('service.DictionaryDesigner', null, {id: item.id});
                        },
                        listAction: () => {
                            return _serviceAction.DictAction.list().then((items) => {
                                return _sort(items.data).map(item=>{
                                    return {
                                        ...item,
                                        pinyin: _toPY(item)
                                    };
                                });
                            });
                        }
                    }]
                }]
        },
        {type: 'separator'},
        {
            name: '搜索设计',
            title: '根据关键字搜索具体设计',
            id: 'FindDesigner',
            ooicon: 'search',
            handleClick: (item) => {
                _openApp('FindDesigner');
            }
        },
        {
            name: '最近打开',
            title: '最近打开的设计元素',
            id: 'recentlyOpened',
            ooicon: 'clock',
            type: 'app-category',
            autoRefresh: true,
            children: [{
                label: '最近打开',
                type: 'designer',
                handleClick: (item) => {
                    _openApp(item.app, null, { id: item.id, application: {id: item.appid} });
                },
                listAction: () => {
                    return o2.UD.getDataJson(RECENTLY_DESIGNER_NAME).then((items) => {
                        return _sort(items || [], 'time', true).map((item) => {
                            return {
                                ...item,
                                pinyin: _toPY(item),
                                ooicon: _ooiconMap[item.app] || '',
                                title: (item.applicationName || '') + ' ' + _appNameMap[item.app] + ' ' + item.app
                            };
                        });
                    })
                }
            }]
        },
    ]
};

var o2DesignerBreadcrumb = new Class({
    Extends: o2.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default", //样式
        "keepActive": true, //是否保持上次的激活状态
        "pathlist": [] //路径列表 [{id, name, type}]
    },
    initialize: function(container, app, options){
        this.setOptions(options);
        this.app = app;
        this.path = `../x_component_process_ProcessManager/$DesignerBreadcrumb/${this.options.style}/`;
        this.container = $(container);
        this.items = [];
    },
    load: function (){
        this.container.loadCss(`${this.path}style.css`);
        this.node = new Element('div.breadcrumb').inject(this.container);
        this.options.pathlist.each((pathData, i)=>{
            this.addItem(pathData, i);
        });
        this.app.content.addEvent('mousedown', (e)=>{
            if( this.activeMenu ){
                // if(this.options.keepActive){
                //     this.lastActiveMenus = [];
                //     var activeMenu = this.activeMenu;
                //     while(activeMenu){
                //         this.lastActiveMenus.push(activeMenu);
                //         activeMenu = activeMenu.activeMenu;
                //     }
                // }
                this.activeMenu.hide();
                this.activeMenu = null;
            }
        });
        this.node.addEvent('mousedown', (e)=>{ e.stopPropagation(); });
        this.addToRecently();
    },
    addItem: function(pathData){
        var item = new o2DesignerBreadcrumb.Item(this, this.items.getLast() || null, pathData);
        this.items.push(item);
        item.load();
        return item;
    },
    addToRecently: function(){
        var pathlist = this.options.pathlist;
        var path = pathlist.getLast();
        var applicationName = pathlist.length === 4 ? pathlist[1].name : pathlist[0].name;
        if( path.id ){
            o2.UD.getDataJson(RECENTLY_DESIGNER_NAME).then((items) => {
                var list = _sort(items || [], 'time', true).filter((item) => {
                    return item.id !== path.id;
                });
                list.unshift({
                    applicationName: applicationName,
                    app: this.app.options.name,
                    id: path.id,
                    name: path.name,
                    alias: path.alias || '',
                    time: new Date().getTime(),
                    timeString: new Date().format('db')
                });
                (list.length > RECENTLY_DESIGNER_MAX_COUNT) && (list.length = RECENTLY_DESIGNER_MAX_COUNT);
                o2.UD.putData(RECENTLY_DESIGNER_NAME, list);
            });
        }
    }
});

o2DesignerBreadcrumb.Item = new Class({
    initialize: function(breadcrumb, parent, pathData, config){
        this.breadcrumb = breadcrumb;
        this.app = breadcrumb.app;
        this.parent = parent;
        this.pathData = pathData;
        this.level = parent ? (parent.level + 1) : 1;
        this.siblingConfigs = this.level === 1 ? o2DesignerConfig.children : this.parent.config.children;
        this.config = this.siblingConfigs.length === 1 ? this.siblingConfigs[0] : this.siblingConfigs.find((item)=>{
            return item.id === this.pathData.id || item.name === this.pathData.name;
        });
    },
    load: function (){
        if( this.level > 1 ){
            this.separator = new Element('div.breadcrumb-separator', {
                text: '>'
            }).inject(this.breadcrumb.node);
        }
        this.node = new Element('div.breadcrumb-item', {
            text: (this.config.label ? this.config.label+'：' : '') + this.pathData.name
        }).inject(this.breadcrumb.node);

        this.loadMenu();
    },
    loadMenu: function (){
        const { app, node, data } = this;
        var _self = this;
        this.menu = new o2DesignerBreadcrumb.Menu(app.content, node, app, data, {
            axis : "y",
            hiddenDelay : 300,
            displayDelay : 300,
            onPostCreate: function (){ _self.setActiveMenu(this); },
            onShow: function (){ _self.setActiveMenu(this); },
            onHide: function (){ _self.cancelActiveMenu(this); }
        });
        this.menu.level = this.level;
        this.menu.item = this;
    },
    setActiveMenu: function ( submenu ){
        var bc = this.breadcrumb;
        if( bc.activeMenu ){
            // if(bc.options.keepActive){
            //     this.lastActiveMenus = [];
            //     var activeMenu = this.activeMenu;
            //     while(activeMenu){
            //         this.lastActiveMenus.push(activeMenu);
            //         activeMenu = activeMenu.activeMenu;
            //     }
            // }
            bc.activeMenu.hide();
        }
        bc.activeMenu = submenu;
    },
    cancelActiveMenu: function ( submenu ){
        if( submenu.activeMenu ){
            submenu.activeMenu.hide();
        }
    },
    destroy: function () {
        this.separator && this.separator.destroy();
        this.node.destroy();
        this.switchCategory(false);
    }
});

o2.xDesktop.requireApp('Template', 'MTooltips', null, false);
o2DesignerBreadcrumb.Menu = new Class({
    Extends: MTooltips,
    Implements: [Options, Events],
    options: {
        offset : {
            x : 0,
            y : 8
        },
        priorityOfAuto: {
            x : ["right", "center", "left" ], //当position x 为 auto 时候的优先级
            y : ["bottom", "middle", "top" ] //当position y 为 auto 时候的优先级
        },
        hasArrow: false,
        isAutoHide: false,
        overflow: 'scroll',
        // hideByClickBody : true,
        nodeStyles: {
            "max-height":  "80%",
            "display": "flex",
            "position" : "absolute",
            "max-width" : "500px",
            "min-width" : "100px",
            "z-index" : "101",
            "border-color" : "var(--oo-color-gray-d)",
            "background-color" : "#fff",
            "padding" : "5px 0px",
            "border-radius" : "var(--oo-default-radius)",
            "box-shadow": "0 0 10px 1px var(--oo-color-gray-d)",
            "-webkit-user-select": "text",
            "-moz-user-select": "text"
        }
    },
    //执行后才显示位置也样式
    _loadCustom : function( callback, refresh ){
        this.menus = [];
        if( !refresh ){
            this.contentNode.addEvent('mousedown', (e)=>{ e.stopPropagation(); });
            this.contentNode.loadCss(`${this.item.breadcrumb.path}style.css`);
            this.contentNode.setStyles({'height': 'auto'});
        }

        var template = Array.clone(this.getChildrenTempalte());
        // var creatable = !!(template.length === 1 && template[0].createFunction);
        Promise.resolve(this.getList( template )).then((data)=>{
            this.parseCategory();
            this.contentNode.loadHtml(
                this.item.breadcrumb.path+"menu.html",
                {
                    bind: {
                        lp: this.lp,
                        data: data,
                        type: template[0].type,
                        editingAppid: this.getPathAppid(),
                        pathData: this.getPathData(),
                        categories: this.categories
                    },
                    module: this
                },
                function(){
                    if(callback)callback();
                }.bind(this)
            );
        });
    },
    reload: function (){
        debugger;
        this.refresh(true);
    },
    getPathAppid : function(){
        var pathlist = this.item.breadcrumb.options.pathlist;
        return pathlist.length === 4 ? pathlist[1].id : pathlist[0].id;
    },
    getPathData: function(){
        return this.item.breadcrumb.options.pathlist[this.level-1] || {};
    },
    getAppid: function(){
        var parent = this.item.parent;
        while(parent){
            if( parent.pathData && parent.pathData.id && parent.pathData.type === 'app' ){
                return parent.pathData.id;
            }
            parent = parent.parent;
        }
    },
    getAppname: function(){
        var parent = this.item.parent;
        while(parent){
            if( parent.pathData && parent.pathData.name && parent.pathData.type === 'app' ){
                return parent.pathData.name;
            }
            parent = parent.parent;
        }
    },
    getCurrentAppid: function (){
        return this.getAppid();
    },
    getCurrentAppname: function (){
        return this.getAppname();
    },
    getChildrenTempalte: function () {
        return this.item.siblingConfigs;
    },
    getList: function ( template ) {
        var list = template;
        this.categories = [];
        var appid = this.getAppid();
        if( list.length === 1 && list[0].listAction){
            return list[0].listAction( appid ).then((data)=>{
                return data.map(d=>{
                    if( list[0].type === 'app' ){
                        d.children = list[0].children.map((child)=>{
                            child.appid = d.id;
                            return child;
                        });
                    }
                    d.handleClick = ()=>{
                        list[0].handleClick(d, appid);
                    };
                    if( list[0].categorized ){
                        (!d.category || d.category==='未分类') && (d.category = UNCATEGORIZED);
                        if( !this.categories.includes( d.category ) ){
                            this.categories.push( d.category );
                        }
                    }
                    return d;
                });
            });
        }else{
            return list.map(child=>{
                child.appid = appid;
                return child;
            });
        }
    },
    switchCategoryArea: function (e) {
        if( this.categoryArea.hasClass('hide') ){
            this.categoryArea.setStyles({'width': this.menuNode.getSize().x+'px'})
        }
        _checkClass(this.categoryArea, 'hide', !this.categoryArea.hasClass('hide'));
        _checkClass(e.currentTarget, 'active', !this.categoryArea.hasClass('hide'));
        !!this.activeMenu && this.activeMenu.setCoondinates();
    },
    parseCategory: function (e){
        var hasUncategorized = this.categories.includes(UNCATEGORIZED);
        hasUncategorized && (this.categories = this.categories.erase( UNCATEGORIZED ));
        if( this.categories.length > 0 ){
            this.categories = _sort(this.categories,'').map(c=>{ return {value: c, label: c}; });
            this.categories.unshift({value: ALL, label: '全部分类'});
            hasUncategorized && this.categories.push({value: UNCATEGORIZED, label: '未分类'});
        }
    },
    filterByCategory: function (e, data){
        !!this.activeCategoryNode && this.activeCategoryNode.removeClass('active');
        this.activeCategoryNode = e.currentTarget;
        e.currentTarget.addClass('active');

        this.currentCategory = data.value;

        this.search();
    },
    loadSearchInput: function (e) {
        var searchInput = this.searchInput;

        var isComposing = false; // 标记是否处于输入法输入中

        //输入法开始输入事件
        searchInput.addEventListener('compositionstart', () => { isComposing = true; });

        //输入法结束输入事件
        searchInput.addEventListener('compositionend', (e) => {
            isComposing = false;
            this.currentSearchKey = e.currentTarget.value;
            this.search();
        });

        //常规的input事件
        searchInput.addEventListener('input', (e) => {
            this.currentSearchKey = e.currentTarget.value;
            !isComposing && this.search();
        });
    },
    clearSearch: function (){
        this.currentSearchKey = '';
        this.searchInput.setAttribute('value', '');
        this.search();
    },
    search: function() {
        var category = this.currentCategory;
        var key = this.currentSearchKey.toLowerCase();
        var items = this.menuNode && this.menuNode.querySelectorAll('.breadcrumb-menu-item');
        (items || []).forEach(item=>{
            var ds = item.dataset;
            var isMatchKey = !key || (ds.id.includes(key) ||
                    ds.name.includes(key) ||
                    (ds.alias||'').includes(key) ||
                    (ds.pinyin||'').includes(key)
                );
            var isMatchCategory = !category || category === ALL || ds.category === category;
            _checkClass( item, 'hide', !isMatchKey || !isMatchCategory );
        });

        _checkClass(this.clearSearchNode, 'hide', !key);

        if( !!this.activeMenu ){
            var fun = this.activeMenu.target.offsetParent === null ? 'hide' : 'setCoondinates';
            this.activeMenu[fun]();
        }
    },
    handleLoadItem: function (e, data){
        var app = this.item.breadcrumb.app;
        var _self = this;
        if( data.children && data.children.length > 0 ){
            var menu = new o2DesignerBreadcrumb.SubMenu(app.content, e.currentTarget, app, data, {
                axis : "x",
                hiddenDelay : 300,
                displayDelay : 300,
                autoRefresh: data.autoRefresh,
                onPostCreate: function (){ _self.setActiveMenu(this); },
                onShow: function (){ _self.setActiveMenu(this); },
                onHide: function (){ _self.cancelActiveMenu(this); }
            });
            menu.level = this.level + 1;
            menu.item = this.item;
            menu.parent = this;
            menu.currentAppid = this.getCurrentAppid();
            menu.currentAppname = this.getCurrentAppname();
            this.menus.push(menu);
        }
    },
    setActiveMenu: function ( submenu ){
        this.activeEl = submenu.target;
        this.activeEl.addClass('active');
        this.activeMenu = submenu;
    },
    cancelActiveMenu: function ( submenu ){
        submenu.target.removeClass('active');
        if( submenu.activeMenu ){
            submenu.activeMenu.hide();
        }
    },
    handleMouseEnter: function (e, data){
        this.timer_hide_acitvie = window.setTimeout(() => {
            if(this.activeMenu && this.activeMenu.data.id !== data.id){
                this.activeMenu.hide();
            }
            this.timer_hide_acitvie = null;
        }, this.options.hiddenDelay);
    },
    handleMouseLeave: function (e, data){
        if(this.timer_hide_acitvie){
            window.clearTimeout(this.timer_hide_acitvie);
            this.timer_hide_acitvie = null;
        }
    },
    handleCreate: function (e, data){
        data.handleCreate(this.getAppid(), this.getAppname());
        e.stopPropagation();
    },
    handleClick: function (e, data){
        data.handleClick(data, this.getAppid());
    },
    _customNode : function( node, contentNode ){
        this.fireEvent("customContent", [contentNode, node])
    }
})

o2DesignerBreadcrumb.SubMenu = new Class({
    Extends: o2DesignerBreadcrumb.Menu,
    Implements: [Options, Events],
    options: {
        offset : {
            x : 5,
            y : 0
        },
        priorityOfAuto :{
            x : [ "center", "right", "left" ], //当position x 为 auto 时候的优先级
            y : ["bottom", "middle", "top" ] //当position y 为 auto 时候的优先级
        }
    },
    getAppid: function(){
        var parent = this, topParent;
        while(parent){
            if( parent.data && parent.data.id && parent.data.type === 'app' ){
                return parent.data.id;
            }
            topParent = parent;
            parent = parent.parent;
        }
        return this.currentAppid;
    },
    getAppname: function(){
        var parent = this, topParent;
        while(parent){
            if( parent.data && (parent.data.name || parent.data.appName) && parent.data.type === 'app' ){
                return parent.data.name || parent.data.appName;
            }
            topParent = parent;
            parent = parent.parent;
        }
        return this.currentAppname;
    },
    getChildrenTempalte: function () {
        return this.data.children;
    },
    handleCreate: function (e, data){
        data.handleCreate(data.appid || this.getAppid(), this.getAppname());
        e.stopPropagation();
    },
    handleClick: function (e, data){
        data.handleClick(data, data.appid || this.getAppid());
    },
    getCurrentAppid: function (){
        return this.currentAppid;
    },
    getCurrentAppname: function (){
        return this.currentAppname;
    }
});
