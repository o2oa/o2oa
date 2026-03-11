MWF.require('o2.widget.PinYin', null, false);
var o2DM  = o2DM || MWF.xApplication.designermanager || {};
o2DM._portalAction = o2.Actions.load('x_portal_assemble_designer');
o2DM._processAction = o2.Actions.load('x_processplatform_assemble_designer');
o2DM._cmsAction = o2.Actions.load('x_cms_assemble_control');
o2DM._queryAction = o2.Actions.load('x_query_assemble_designer');
o2DM._serviceAction = o2.Actions.load('x_program_center');
//o2DM._openApp = o2.api.page.openApplication;

o2DM._UNCATEGORIZED = 'uncategorized';
o2DM._ALL = 'all';
o2DM._HISTORY_DESIGNER_NAME = 'HistoryOpenedDesigner';
o2DM._HISTORY_DESIGNER_MAX_COUNT = 20; //最近打开的设计元素数量

o2DM._ooiconMap = {
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
o2DM._appNameMap = {
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

o2DM._checkClass = (dom, clazz, flag)=>{
    !!flag ?
        !dom.hasClass(clazz) && dom.addClass(clazz) :
        dom.hasClass(clazz) && dom.removeClass(clazz);
};

o2DM._toPY = (item)=>{
    return `${item.name}`.toPY().toLowerCase() + '#' +
        `${item.alias||''}`.toPY().toLowerCase() + '#' +
        `${item.name}`.toPYFirst().toLowerCase() + '#' +
        `${item.alias||''}`.toPYFirst().toLowerCase() + '#';
};

o2DM._sort =  (data, key='name', isDesc=false)=>{
    return data.sort(function (a, b){
        var av = !!key ? a[key] : a, bv = !!key ? b[key] : b;
        if( typeof av === 'string' && typeof bv === 'string' ){
            var isLetterA = /^[a-zA-Z0-9]/.test(av);
            var isLetterB = /^[a-zA-Z0-9]/.test(bv);

            if (isLetterA && !isLetterB) return isDesc ? 1 : -1; // a是字母，b不是，a排在前面
            if (!isLetterA && isLetterB) return isDesc ? -1 : 1;  // a不是字母，b是，b排在前面

            return isDesc ?  bv.localeCompare(av) : av.localeCompare(bv);
        }
        return isDesc ? (bv - av) : (av - bv);
    }.bind(this));
};

o2DM._findConfig = (componentName, rootObj = o2DM._config)=>{
    if (rootObj.componentName === componentName) {
        return rootObj;
    }

    if (rootObj.children && Array.isArray(rootObj.children)) {
        for (const child of rootObj.children) {
            const result = o2DM._findConfig(componentName, child);
            if (result) {
                return result;
            }
        }
    }

    return null;
};

o2DM._findAllParentConfigs = (componentName, rootObj = o2DM._config, parents = [])=>{
    if (!rootObj) return null;

    if (rootObj.componentName === componentName) {
        const result = [rootObj, ...parents];
        return result.length > 0 ? result.reverse() : null;
    }

    if (rootObj.children && Array.isArray(rootObj.children) && rootObj.children.length > 0) {
        const newParents = rootObj.componentName
      ? [rootObj, ...parents]
      : [...parents];

        for (const child of rootObj.children) {
            const result = o2DM._findAllParentConfigs(componentName, child, newParents);
            if (result) {
                return result;
            }
        }
    }

    return null;
};

o2DM._copyTextToClipboard=(text)=>{
    try{
        navigator.clipboard.writeText(text);
    }catch(err){
        o2.api.page.notice(err.message, 'error');
        throw err;
    }
};

o2DM._appTools = [
    {
        ooicon: 'window-max',
        _type: 'app-tool',
        name: '复制名称',
        handleClick: (data, appid, appdata)=>{ o2DM._copyTextToClipboard(appdata.name); },
    },{
        ooicon: 'window-max',
        _type: 'app-tool',
        name: '复制别名',
        condition: (data, appdata)=>{ return !!appdata.alias; },
        handleClick: (data, appid, appdata)=>{ o2DM._copyTextToClipboard(appdata.alias); },
    },{
        ooicon: 'window-max',
        _type: 'app-tool',
        name: '复制id',
        handleClick: (data, appid, appdata)=>{ o2DM._copyTextToClipboard(appdata.id); },
    },{
        ooicon: 'window-max',
        _type: 'app-tool',
        name: '复制对象',
        handleClick: (data, appid, appdata)=>{
            var {name, alias, id} = appdata;
            o2DM._copyTextToClipboard(
                JSON.stringify({name, alias, id}, null, 2)
            )
        }
    }
];

o2DM._designerTools = [
    {
        ooicon: 'window-max',
        _type: 'designer-tool',
        name: '复制名称',
        handleClick: (data, appdata)=>{ o2DM._copyTextToClipboard(data.name); },
    },{
        ooicon: 'window-max',
        _type: 'designer-tool',
        name: '复制别名',
        condition: (data, appdata)=>{ return !!data.alias; },
        handleClick: (data, appdata)=>{ o2DM._copyTextToClipboard(data.alias); },
    },{
        ooicon: 'window-max',
        _type: 'designer-tool',
        name: '复制id',
        handleClick: (data, appdata)=>{ o2DM._copyTextToClipboard(data.id); },
    },{
        ooicon: 'window-max',
        _type: 'designer-tool',
        name: '复制对象',
        handleClick: (data, appdata)=>{
            var {name, alias, id} = data;
            o2DM._copyTextToClipboard(
                JSON.stringify({name, alias, id}, null, 2)
            )
        }
    },{
        ooicon: 'window-max',
        _type: 'designer-tool',
        name: '复制应用和设计对象',
        title: '优先级：别名->名称',
        handleClick: (data, appdata)=>{
            var obj = {
                type: appdata ? appdata._appType : data._appType,
                name: data.alias || data.name
            };
            if( appdata ){
                obj.application = appdata.alias || appdata.name;
            }
            o2DM._copyTextToClipboard( JSON.stringify(obj, null, 2) )
        }
    }
    // ,{
    //     ooicon: 'window-max',
    //     _type: 'designer-tool',
    //     name: '拷贝到其他应用',
    //     handleClick: (data, appid, appdata)=>{
    //         var {name, alias, id} = appdata;
    //         o2DM._copyTextToClipboard(
    //             JSON.stringify({name, alias, id}, null, 2)
    //         )
    //     }
    // }
];

o2DM._config = {
    _type: 'root',
    children: [
        {
            handleClick: () => {
                o2DM._openApp('portal.PortalExplorer');
            },
            name: '门户管理',
            componentName: 'portal.PortalExplorer',
            ooicon: 'portal',
            _type: 'app-category',
            children: [{
                name: '门户应用',
                componentName: 'portal.PortalManager',
                label: '应用',
                _type: 'app',
                _appType: 'portal',
                categorized: true,
                handleClick: (item) => {
                    o2DM._openApp('portal.PortalManager', null, {application: item.id});
                },
                getAction: (appid)=>{
                    return o2DM._portalAction.PortalAction.get(appid);
                },
                listAction: () => {
                    return o2DM._portalAction.PortalAction.list().then((json) => {
                        return o2DM._sort(json.data, 'name').map((item) => {
                            return {
                                ...item,
                                _pinyin: o2DM._toPY(item),
                                category: item.portalCategory,
                                defaultIcon: '../x_component_portal_PortalExplorer/$Main/default/icon/application.png',
                                _appType: 'portal'
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '页面配置',
                        componentName: 'portal.PageDesigner',
                        _type: 'designer-category',
                        ooicon: 'pagepeizhi',
                        handleClick: (item, appid) => {
                            o2DM._openApp('portal.PortalManager', null, {navi: 0, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('portal.PageDesigner', opt, opt);
                        },
                        children: [{
                            label: '页面',
                            _type: 'designer',
                            categorized: true,
                            handleClick: (page) => {
                                o2DM._openApp('portal.PageDesigner', null, {id: page.id});
                            },
                            listAction: (appid) => {
                                return o2DM._portalAction.PageAction.listWithPortal(appid).then((pages) => {
                                    return o2DM._sort(pages.data).map((page) => {
                                        return {
                                            ...page,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(page)
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '部件配置',
                        componentName: 'portal.WidgetDesigner',
                        _type: 'designer-category',
                        label: '部件',
                        ooicon: 'app-center',
                        handleClick: (item, appid) => {
                            o2DM._openApp('portal.PortalManager', null, {navi: 1, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('portal.WidgetDesigner', opt, opt);
                        },
                        children: [{
                            label: '部件',
                            _type: 'designer',
                            categorized: true,
                            handleClick: (portal) => {
                                o2DM._openApp('portal.WidgetDesigner', null, {id: portal.id});
                            },
                            listAction: (appid) => {
                                return o2DM._portalAction.WidgetAction.listWithPortal(appid).then((widgets) => {
                                    return o2DM._sort(widgets.data).map((widget) => {
                                        return {
                                            ...widget,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(widget)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据字典',
                        componentName: 'portal.DictionaryDesigner',
                        _type: 'designer-category',
                        label: '数据字典',
                        ooicon: 'js',
                        handleClick: (item, appid) => {
                            o2DM._openApp('portal.PortalManager', null, {navi: 2, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('portal.DictionaryDesigner', opt, opt);
                        },
                        children: [{
                            label: '数据字典',
                            _type: 'designer',
                            handleClick: (dict) => {
                                o2DM._openApp('portal.DictionaryDesigner', null, {
                                    id: dict.id, application: { id: dict.appid }
                                });
                            },
                            listAction: (appid) => {
                                return o2DM._portalAction.DictAction.listWithApplication(appid).then((dicts) => {
                                    return o2DM._sort(dicts.data).map((dict) => {
                                        return {
                                            ...dict,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(dict)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '脚本配置',
                        componentName: 'portal.ScriptDesigner',
                        _type: 'designer-category',
                        label: '脚本配置',
                        ooicon: 'jiaoben',
                        handleClick: (item, appid) => {
                            o2DM._openApp('portal.PortalManager', null, {navi: 3, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('portal.ScriptDesigner', opt, opt);
                        },
                        children: [{
                            label: '脚本配置',
                            _type: 'designer',
                            handleClick: (script) => {
                                o2DM._openApp('portal.ScriptDesigner', null, {
                                    id: script.id,
                                    application: {id: script.appid}
                                });
                            },
                            listAction: (appid) => {
                                return o2DM._portalAction.ScriptAction.listWithPortal(appid).then((scripts) => {
                                    return o2DM._sort(scripts.data).map((script) => {
                                        return {
                                            ...script,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(script)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '资源文件',
                        componentName: 'portal.FileDesigner',
                        _type: 'designer-category',
                        ooicon: 'folder-open',
                        handleClick: (item, appid) => {
                            o2DM._openApp('portal.PortalManager', null, {navi: 4, application: appid});
                        }
                    }, {
                        name: '门户属性',
                        componentName: 'portal.Property',
                        _type: 'designer-category',
                        ooicon: 'jiekoupeizhi2',
                        handleClick: (item, appid) => {
                            o2DM._openApp('portal.PortalManager', null, {navi: 5, application: appid});
                        }
                    }]
            }]
        },
        {
            handleClick: () => {
                o2DM._openApp('process.ApplicationExplorer');
            },
            name: '流程管理',
            componentName: 'process.ApplicationExplorer',
            ooicon: 'liucheng',
            _type: 'app-category',
            children: [{
                name: '流程应用',
                componentName: 'process.ProcessManager',
                label: '应用',
                _type: 'app',
                _appType: 'process',
                categorized: true,
                handleClick: (item) => {
                    o2DM._openApp('process.ProcessManager', null, {application: item.id});
                },
                getAction: (appid)=>{
                    return o2DM._processAction.ApplicationAction.get(appid);
                },
                listAction: () => {
                    return o2DM._processAction.ApplicationAction.listSummary().then((json) => {
                        return o2DM._sort(json.data, 'name').map((item) => {
                            return {
                                ...item,
                                category: item.applicationCategory,
                                defaultIcon: '../x_component_process_ApplicationExplorer/$Main/default/icon/application.png',
                                _appType: 'process',
                                _pinyin: o2DM._toPY(item)
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '表单配置',
                        componentName: 'process.FormManager',
                        _type: 'designer-category',
                        ooicon: 'biaodan',
                        handleClick: (item, appid) => {
                            o2DM._openApp('process.ProcessManager', null, {navi: 0, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('process.FormDesigner', opt, opt);
                        },
                        children: [{
                            label: '表单',
                            _type: 'designer',
                            categorized: true,
                            handleClick: (form) => {
                                o2DM._openApp('process.FormDesigner', null, {id: form.id});
                            },
                            listAction: (appid) => {
                                return o2DM._processAction.FormAction.listWithApplication(appid).then((forms) => {
                                    return o2DM._sort(forms.data).map((form) => {
                                        return {
                                            ...form,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(form)
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '流程配置',
                        componentName: 'process.ProcessDesigner',
                        _type: 'designer-category',
                        label: '流程',
                        ooicon: 'a-flowprocess',
                        handleClick: (item, appid) => {
                            o2DM._openApp('process.ProcessManager', null, {navi: 1, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('process.ProcessDesigner', opt, opt);
                        },
                        children: [{
                            label: '流程',
                            _type: 'designer',
                            categorized: true,
                            handleClick: (process) => {
                                o2DM._openApp('process.ProcessDesigner', null, {id: process.id});
                            },
                            listAction: (appid) => {
                                return o2DM._processAction.ProcessAction.listWithApplication(appid).then((processes) => {
                                    return o2DM._sort(processes.data).map((process) => {
                                        return {
                                            ...process,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(process)
                                        };
                                    });
                                });
                            }
                        }]
                    }, {
                        name: '数据字典',
                        componentName: 'process.DictionaryDesigner',
                        _type: 'designer-category',
                        label: '数据字典',
                        ooicon: 'js',
                        handleClick: (item, appid) => {
                            o2DM._openApp('process.ProcessManager', null, {navi: 2, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('process.DictionaryDesigner', opt, opt);
                        },
                        children: [{
                            label: '数据字典',
                            _type: 'designer',
                            handleClick: (dict) => {
                                o2DM._openApp('process.DictionaryDesigner', null, { id: dict.id, application: { id: dict.appid } });
                            },
                            listAction: (appid) => {
                                return o2DM._processAction.ApplicationDictAction.listWithApplication(appid).then((dicts) => {
                                    return o2DM._sort(dicts.data).map((dict) => {
                                        return {
                                            ...dict,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(dict)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '脚本配置',
                        componentName: 'process.ScriptDesigner',
                        _type: 'designer-category',
                        label: '脚本配置',
                        ooicon: 'jiaoben',
                        handleClick: (item, appid) => {
                            o2DM._openApp('process.ProcessManager', null, {navi: 3, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('process.ScriptDesigner', opt, opt);
                        },
                        children: [{
                            label: '脚本配置',
                            _type: 'designer',
                            handleClick: (script) => {
                                o2DM._openApp('process.ScriptDesigner', null, {
                                    id: script.id,
                                    application: {id: script.appid}
                                });
                            },
                            listAction: (appid) => {
                                return o2DM._processAction.ScriptAction.listWithApplication(appid).then((scripts) => {
                                    return o2DM._sort(scripts.data).map((script) => {
                                        return {
                                            ...script,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(script)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '资源文件',
                        componentName: 'process.FileDesigner',
                        _type: 'designer-category',
                        ooicon: 'folder-open',
                        handleClick: (item, appid) => {
                            o2DM._openApp('process.ProcessManager', null, {navi: 4, application: appid});
                        }
                    }, {
                        name: '应用属性',
                        componentName: 'process.Property',
                        _type: 'designer-category',
                        ooicon: 'jiekoupeizhi2',
                        handleClick: (item, appid) => {
                            o2DM._openApp('process.ProcessManager', null, {navi: 5, application: appid});
                        }
                    }]
            }]
        },
        {
            handleClick: () => {
                o2DM._openApp('cms.Column');
            },
            name: '内容管理',
            componentName: 'cms.Column',
            ooicon: 'note',
            _type: 'app-category',
            children: [{
                name: '内容管理',
                componentName: 'cms.ColumnManager',
                label: '应用',
                _type: 'app',
                _appType: 'cms',
                categorized: true,
                handleClick: (item) => {
                    o2DM._openApp('cms.ColumnManager', null, {column: item.id});
                },
                getAction: (appid)=>{
                    return o2DM._cmsAction.AppInfoAction.getAppInfo(appid).then((item) => {
                        item.data = {
                            ...item.data,
                            alias: item.appAlias,
                            name: item.appName,
                        }
                        return item;
                    });
                },
                listAction: () => {
                    return o2DM._cmsAction.AppInfoAction.listAllAppInfo().then((json) => {
                        return o2DM._sort(json.data, 'appName').map((item) => {
                            return {
                                ...item,
                                category: item.appType,
                                alias: item.appAlias,
                                name: item.appName,
                                icon: item.appIcon,
                                defaultIcon: '../x_component_cms_Column/$Main/default/icon/column.png',
                                _appType: 'cms',
                                _pinyin: o2DM._toPY({alias: item.appAlias, name: item.appName})
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '分类配置',
                        componentName: 'cms.CategoryManager',
                        _type: 'designer-category',
                        ooicon: 'bujianpeizhi',
                        handleClick: (item, appid) => {
                            o2DM._openApp('cms.ColumnManager', null, {navi: 'categoryConfig', column: appid});
                        },
                        children: [{
                            label: '分类',
                            _type: 'designer',
                            handleClick: (category) => {
                                o2DM._openApp('cms.ColumnManager', null, {
                                    navi: 'categoryConfig',
                                    column: category.appid,
                                    categoryId: category.id
                                });
                            },
                            listAction: (appid) => {
                                return o2DM._cmsAction.CategoryInfoAction.listViewableCategoryInfo_AllType(appid).then((categorys) => {
                                    return o2DM._sort(categorys.data).map((category) => {
                                        return {
                                            ...category,
                                            alias: category.categoryAlias,
                                            name: category.categoryName,
                                            appid: category.appId,
                                            _pinyin: o2DM._toPY({alias: category.categoryAlias, name: category.categoryName})
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '表单配置',
                        componentName: 'cms.FormDesigner',
                        _type: 'designer-category',
                        label: '表单',
                        ooicon: 'biaodan',
                        handleClick: (item, appid) => {
                            o2DM._openApp('cms.ColumnManager', null, {navi: 'formConfig', column: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('cms.FormDesigner', opt, opt);
                        },
                        children: [{
                            label: '表单',
                            _type: 'designer',
                            handleClick: (form) => {
                                o2DM._openApp('cms.FormDesigner', null, {id: form.id});
                            },
                            listAction: (appid) => {
                                return o2DM._cmsAction.FormAction.listFormByAppId(appid).then((forms) => {
                                    return o2DM._sort(forms.data).map((form) => {
                                        return {
                                            ...form,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(form)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据字典',
                        componentName: 'cms.DictionaryDesigner',
                        _type: 'designer-category',
                        label: '数据字典',
                        ooicon: 'js',
                        handleClick: (item, appid) => {
                            o2DM._openApp('cms.ColumnManager', null, {navi: 'dataConfig', column: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('cms.DictionaryDesigner', opt, opt);
                        },
                        children: [{
                            label: '数据字典',
                            _type: 'designer',
                            handleClick: (dict) => {
                                o2DM._openApp('cms.DictionaryDesigner', null, {
                                    id: dict.id, application: { id: dict.appid }
                                });
                            },
                            listAction: (appid) => {
                                return o2DM._cmsAction.AppDictDesignAction.listWithAppInfo(appid).then((dicts) => {
                                    return o2DM._sort(dicts.data).map((dict) => {
                                        return {
                                            ...dict,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(dict)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '脚本配置',
                        componentName: 'cms.ScriptDesigner',
                        _type: 'designer-category',
                        label: '脚本配置',
                        ooicon: 'jiaoben',
                        handleClick: (item, appid) => {
                            o2DM._openApp('cms.ColumnManager', null, {navi: 'scriptConfig', column: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('cms.ScriptDesigner', opt, opt);
                        },
                        children: [{
                            label: '脚本配置',
                            _type: 'designer',
                            handleClick: (script) => {
                                o2DM._openApp('cms.ScriptDesigner', null, {id: script.id, application: {id: script.appid}});
                            },
                            listAction: (appid) => {
                                return o2DM._cmsAction.ScriptAction.listWithApplication(appid).then((scripts) => {
                                    return o2DM._sort(scripts.data).map((script) => {
                                        return {
                                            ...script,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(script)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '资源文件',
                        componentName: 'cms.FileDesigner',
                        _type: 'designer-category',
                        ooicon: 'folder-open',
                        handleClick: (item, appid) => {
                            o2DM._openApp('cms.ColumnManager', null, {navi: 'fileConfig', column: appid});
                        }
                    }, {
                        name: '栏目属性',
                        componentName: 'cms.Property',
                        _type: 'designer-category',
                        ooicon: 'jiekoupeizhi2',
                        handleClick: (item, appid) => {
                            o2DM._openApp('cms.ColumnManager', null, {navi: 'applicationProperty', column: appid});
                        }
                    }]
            }]
        },
        {
            handleClick: () => {
                o2DM._openApp('query.QueryExplorer');
            },
            name: '数据中心',
            componentName: 'query.QueryExplorer',
            ooicon: 'integral',
            _type: 'app-category',
            children: [{
                name: '数据应用',
                componentName: 'query.QueryManager',
                label: '应用',
                _type: 'app',
                _appType: 'query',
                categorized: true,
                handleClick: (item) => {
                    o2DM._openApp('query.QueryManager', null, {application: item.id});
                },
                getAction: (appid)=>{
                    return o2DM._queryAction.QueryAction.get(appid);
                },
                listAction: () => {
                    return o2DM._queryAction.QueryAction.listAll().then((json) => {
                        return o2DM._sort(json.data, 'name').map((item) => {
                            return {
                                ...item,
                                category: item.queryCategory,
                                defaultIcon: '../x_component_query_QueryExplorer/$Main/default/icon/application.png',
                                _appType: 'query',
                                _pinyin: o2DM._toPY(item)
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '视图配置',
                        componentName: 'query.ViewDesigner',
                        _type: 'designer-category',
                        ooicon: 'shitupeizhi3',
                        handleClick: (item, appid) => {
                            o2DM._openApp('query.QueryManager', null, {navi: 0, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('query.ViewDesigner', opt, opt);
                        },
                        children: [{
                            label: '视图',
                            _type: 'designer',
                            handleClick: (view) => {
                                o2DM._openApp('query.ViewDesigner', null, {id: view.id, application: {id: view.appid}});
                            },
                            listAction: (appid) => {
                                return o2DM._queryAction.ViewAction.listWithQuery(appid).then((views) => {
                                    return o2DM._sort(views.data).map((view) => {
                                        return {
                                            ...view,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(view)
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '统计配置',
                        componentName: 'query.StatDesigner',
                        _type: 'designer-category',
                        label: '统计',
                        ooicon: 'shujubiao2',
                        handleClick: (item, appid) => {
                            o2DM._openApp('query.QueryManager', null, {navi: 1, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('query.StatDesigner', opt, opt);
                        },
                        children: [{
                            label: '统计',
                            _type: 'designer',
                            handleClick: (stat) => {
                                o2DM._openApp('query.StatDesigner', null, {id: stat.id, application: {id: stat.appid}});
                            },
                            listAction: (appid) => {
                                return o2DM._queryAction.StatAction.listWithQuery(appid).then((stats) => {
                                    return o2DM._sort(stats.data).map((stat) => {
                                        return {
                                            ...stat,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(stat)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据表',
                        componentName: 'query.TableDesigner',
                        _type: 'designer-category',
                        label: '数据表',
                        ooicon: 'shujubiao',
                        handleClick: (item, appid) => {
                            o2DM._openApp('query.QueryManager', null, {navi: 2, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('query.TableDesigner', opt, opt);
                        },
                        children: [{
                            label: '数据表',
                            _type: 'designer',
                            handleClick: (table) => {
                                o2DM._openApp('query.TableDesigner', null, {
                                    id: table.id, application: { id: table.appid }
                                });
                            },
                            listAction: (appid) => {
                                return o2DM._queryAction.TableAction.listWithQuery(appid).then((tables) => {
                                    return o2DM._sort(tables.data).map((table) => {
                                        return {
                                            ...table,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(table)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '查询配置',
                        componentName: 'query.StatementDesigner',
                        _type: 'designer-category',
                        label: '脚本配置',
                        ooicon: 'chaxunpeizhi',
                        handleClick: (item, appid) => {
                            o2DM._openApp('query.QueryManager', null, {navi: 3, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('query.StatementDesigner', opt, opt);
                        },
                        children: [{
                            label: '查询配置',
                            _type: 'designer',
                            handleClick: (statement) => {
                                o2DM._openApp('query.StatementDesigner', null, {
                                    id: statement.id,
                                    application: {id: statement.appid}
                                });
                            },
                            listAction: (appid) => {
                                return o2DM._queryAction.StatementAction.listWithQuery(appid).then((statements) => {
                                    return o2DM._sort(statements.data).map((statement) => {
                                        return {
                                            ...statement,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(statement)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '导入模型',
                        componentName: 'query.ImporterDesigner',
                        _type: 'designer-category',
                        ooicon: 'file_upload',
                        handleClick: (item, appid) => {
                            o2DM._openApp('query.QueryManager', null, {navi: 4, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DM._openApp('query.ImporterDesigner', opt, opt);
                        },
                        children: [{
                            label: '导入模型',
                            _type: 'designer',
                            handleClick: (importer) => {
                                o2DM._openApp('query.ImporterDesigner', null, {
                                    id: importer.id,
                                    application: {id: importer.appid}
                                });
                            },
                            listAction: (appid) => {
                                return o2DM._queryAction.ImportModelAction.listWithQuery(appid).then((items) => {
                                    return o2DM._sort(items.data).map((item) => {
                                        return {
                                            ...item,
                                            appid: appid,
                                            _pinyin: o2DM._toPY(item)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据中心属性',
                        componentName: 'query.Property',
                        _type: 'designer-category',
                        ooicon: 'jiekoupeizhi2',
                        handleClick: (item, appid) => {
                            o2DM._openApp('query.QueryManager', null, {navi: 5, application: appid});
                        }
                    }]
            }]
        },
        {
            handleClick: () => {
                o2DM._openApp('service.ServiceManager');
            },
            name: '服务管理',
            componentName: 'service.ServiceManager',
            ooicon: 'process-service',
            _type: 'app-category',
            children: [{
                name: '代理配置',
                componentName: 'service.AgentDesigner',
                _type: 'designer-category',
                ooicon: 'dailipeizhi1',
                handleClick: (item, appid) => {
                    o2DM._openApp('service.ServiceManager', null, {navi: 0});
                },
                handleCreate: ()=>{
                    o2DM._openApp('service.AgentDesigner', null, {});
                },
                children: [{
                    label: '代理',
                    _type: 'designer',
                    handleClick: (item) => {
                        o2DM._openApp('service.AgentDesigner', null, {id: item.id});
                    },
                    listAction: () => {
                        return o2DM._serviceAction.AgentAction.list().then((agents) => {
                            return o2DM._sort(agents.data).map(agent=>{
                                return {
                                    ...agent,
                                    _appType: 'service',
                                    _pinyin: o2DM._toPY(agent)
                                };
                            });
                        })
                    }
                }]
            },
                {
                    name: '接口配置',
                    componentName: 'service.InvokeDesigner',
                    _type: 'designer-category',
                    label: '接口',
                    ooicon: 'jiekoupeizhi21',
                    handleClick: (item, appid) => {
                        o2DM._openApp('service.ServiceManager', null, {navi: 1});
                    },
                    handleCreate: ()=>{
                        o2DM._openApp('service.InvokeDesigner', null, {});
                    },
                    children: [{
                        label: '接口',
                        _type: 'designer',
                        categorized: true,
                        handleClick: (item) => {
                            o2DM._openApp('service.InvokeDesigner', null, {id: item.id});
                        },
                        listAction: () => {
                            return o2DM._serviceAction.InvokeAction.list().then((items) => {
                                return o2DM._sort(items.data).map(item=>{
                                    return {
                                        ...item,
                                        _appType: 'service',
                                        _pinyin: o2DM._toPY(item)
                                    };
                                });
                            })
                        }
                    }]
                }, {
                    name: '脚本配置',
                    componentName: 'service.ScriptDesigner',
                    _type: 'designer-category',
                    label: '脚本配置',
                    ooicon: 'jiaoben',
                    handleClick: (item, appid) => {
                        o2DM._openApp('service.ServiceManager', null, {navi: 2});
                    },
                    handleCreate: ()=>{
                        o2DM._openApp('service.ScriptDesigner', null, {});
                    },
                    children: [{
                        label: '脚本配置',
                        _type: 'designer',
                        handleClick: (item) => {
                            o2DM._openApp('service.ScriptDesigner', null, { id: item.id });
                        },
                        listAction: () => {
                            return o2DM._serviceAction.ScriptAction.list().then((items) => {
                                return o2DM._sort(items.data).map(item=>{
                                    return {
                                        ...item,
                                        _appType: 'service',
                                        _pinyin: o2DM._toPY(item)
                                    };
                                });
                            })
                        }
                    }]
                }, {
                    name: '数据配置',
                    componentName: 'service.DictionaryDesigner',
                    _type: 'designer-category',
                    label: '数据配置',
                    ooicon: 'js',
                    handleClick: () => {
                        o2DM._openApp('service.ServiceManager', null, {navi: 3});
                    },
                    handleCreate: ()=>{
                        o2DM._openApp('service.DictionaryDesigner', null, {});
                    },
                    children: [{
                        label: '数据配置',
                        _type: 'designer',
                        handleClick: (item) => {
                            o2DM._openApp('service.DictionaryDesigner', null, {id: item.id});
                        },
                        listAction: () => {
                            return o2DM._serviceAction.DictAction.list().then((items) => {
                                return o2DM._sort(items.data).map(item=>{
                                    return {
                                        ...item,
                                        _appType: 'service',
                                        _pinyin: o2DM._toPY(item)
                                    };
                                });
                            });
                        }
                    }]
                }]
        },
        //{_type: 'separator'},
        // {
        //     name: '搜索设计',
        //     title: '根据关键字搜索具体设计',
        //     componentName: 'FindDesigner',
        //     ooicon: 'search',
        //     handleClick: (item) => {
        //         o2DM._openApp('FindDesigner');
        //     }
        // },
        // {
        //     name: '最近打开',
        //     title: '最近打开的设计元素',
        //     componentName: 'historyOpened',
        //     ooicon: 'clock',
        //     _type: 'app-category',
        //     autoRefresh: true,
        //     children: [{
        //         label: '最近打开',
        //         _type: 'designer',
        //         handleClick: (item) => {
        //             o2DM._openApp(item.componentName, null, { id: item.id, application: {id: item.appid} });
        //         },
        //         listAction: () => {
        //             return new Promise((resolve, reject)=>{
        //                 o2.UD.getDataJson(o2DM._HISTORY_DESIGNER_NAME, (items) => {
        //                     const list = o2DM._sort(items || [], 'time', true).map((item) => {
        //                         return {
        //                             ...item,
        //                             _pinyin: o2DM._toPY(item),
        //                             icon: o2DM._ooiconMap[item.componentName] || '',
        //                             name: o2DM._appNameMap[item.componentName] + ' ' + item.componentName
        //                         };
        //                     });
        //                     resolve(list);
        //                 }, reject);
        //             });
        //         }
        //     }]
        // },
    ]
};
