var _portalAction = o2.Actions.load('x_portal_assemble_designer');
var _processAction = o2.Actions.load('x_processplatform_assemble_designer');
var _cmsAction = o2.Actions.load('x_cms_assemble_control');
var _queryAction = o2.Actions.load('x_query_assemble_designer');
var _serviceAction = o2.Actions.load('x_program_center');
var _openApp = o2.api.page.openApplication;

var _checkClass = (dom, clazz, flag)=>{
    !!flag ?
        !dom.hasClass(clazz) && dom.addClass(clazz) :
        dom.hasClass(clazz) && dom.removeClass(clazz);
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
                                category: item.portalCategory,
                                defaultIcon: '../x_component_portal_PortalExplorer/$Main/default/icon/application.png',
                                name: item.name,
                                id: item.id,
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
                        handleClick: (item, appid) => {
                            _openApp('portal.PortalManager', null, {navi: 0, application: appid});
                        },
                        createFunction: (appid)=>{
                            _openApp('portal.PageDesigner', null, {application: {id: appid}});
                        },
                        children: [{
                            label: '页面',
                            type: 'desiginer',
                            categorized: true,
                            handleClick: (page) => {
                                _openApp('portal.PageDesigner', null, {id: page.id});
                            },
                            listAction: (appid) => {
                                return _portalAction.PageAction.listWithPortal(appid).then((pages) => {
                                    return _sort(pages.data).map((page) => {
                                        return {
                                            ...page,
                                            name: page.name,
                                            id: page.id
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '部件配置',
                        id: 'portal.WidgetDesigner',
                        type: 'desiginer-category',
                        label: '部件',
                        handleClick: (item, appid) => {
                            _openApp('portal.PortalManager', null, {navi: 1, application: appid});
                        },
                        children: [{
                            label: '部件',
                            type: 'desiginer',
                            categorized: true,
                            handleClick: (portal) => {
                                _openApp('portal.WidgetDesigner', null, {id: portal.id});
                            },
                            listAction: (appid) => {
                                return _portalAction.WidgetAction.listWithPortal(appid).then((widgets) => {
                                    return _sort(widgets.data).map((widget) => {
                                        return {
                                            name: widget.name,
                                            icon: '',
                                            id: widget.id
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据字典',
                        id: 'portal.DictionaryDesigner',
                        type: 'desiginer-category',
                        label: '数据字典',
                        handleClick: (item, appid) => {
                            _openApp('portal.PortalManager', null, {navi: 2, application: appid});
                        },
                        children: [{
                            label: '数据字典',
                            type: 'desiginer',
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
                                            name: dict.name,
                                            icon: '',
                                            id: dict.id,
                                            appid: appid
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '脚本配置',
                        id: 'portal.ScriptDesigner',
                        type: 'desiginer-category',
                        label: '脚本配置',
                        handleClick: (item, appid) => {
                            _openApp('portal.PortalManager', null, {navi: 3, application: appid});
                        },
                        children: [{
                            label: '脚本配置',
                            type: 'desiginer',
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
                                            name: script.name,
                                            icon: '',
                                            id: script.id,
                                            appid: appid
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '资源文件',
                        id: 'portal.FileDesigner',
                        type: 'desiginer-category',
                        handleClick: (item, appid) => {
                            _openApp('portal.PortalManager', null, {navi: 4, application: appid});
                        }
                    }, {
                        name: '门户属性',
                        id: 'portal.Property',
                        type: 'desiginer-category',
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
                                name: item.name,
                                defaultIcon: '../x_component_process_ApplicationExplorer/$Main/default/icon/application.png',
                                id: item.id,
                                type: 'app'
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '表单配置',
                        id: 'process.FormManager',
                        type: 'desiginer-category',
                        handleClick: (item, appid) => {
                            _openApp('process.ProcessManager', null, {navi: 0, application: appid});
                        },
                        children: [{
                            label: '表单',
                            type: 'desiginer',
                            categorized: true,
                            handleClick: (form) => {
                                _openApp('process.FormDesigner', null, {id: form.id});
                            },
                            listAction: (appid) => {
                                return _processAction.FormAction.listWithApplication(appid).then((forms) => {
                                    return _sort(forms.data).map((form) => {
                                        return {
                                            ...form,
                                            name: form.name,
                                            id: form.id
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '流程配置',
                        id: 'process.ProcessDesigner',
                        type: 'desiginer-category',
                        label: '流程',
                        handleClick: (item, appid) => {
                            _openApp('process.ProcessManager', null, {navi: 1, application: appid});
                        },
                        children: [{
                            label: '流程',
                            type: 'desiginer',
                            categorized: true,
                            handleClick: (process) => {
                                _openApp('process.ProcessDesigner', null, {id: process.id});
                            },
                            listAction: (appid) => {
                                return _processAction.ProcessAction.listWithApplication(appid).then((processes) => {
                                    return _sort(processes.data).map((process) => {
                                        return {
                                            name: process.name,
                                            icon: '',
                                            id: process.id
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据字典',
                        id: 'process.DictionaryDesigner',
                        type: 'desiginer-category',
                        label: '数据字典',
                        handleClick: (item, appid) => {
                            _openApp('process.ProcessManager', null, {navi: 2, application: appid});
                        },
                        children: [{
                            label: '数据字典',
                            type: 'desiginer',
                            handleClick: (dict) => {
                                _openApp('process.DictionaryDesigner', null, {
                                    id: dict.id, application: {
                                        id: dict.appid
                                    }
                                });
                            },
                            listAction: (appid) => {
                                return _processAction.ApplicationDictAction.listWithApplication(appid).then((dicts) => {
                                    return _sort(dicts.data).map((dict) => {
                                        return {
                                            name: dict.name,
                                            icon: '',
                                            id: dict.id,
                                            appid: appid
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '脚本配置',
                        id: 'process.ScriptDesigner',
                        type: 'desiginer-category',
                        label: '脚本配置',
                        handleClick: (item, appid) => {
                            _openApp('process.ProcessManager', null, {navi: 3, application: appid});
                        },
                        children: [{
                            label: '脚本配置',
                            type: 'desiginer',
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
                                            name: script.name,
                                            icon: '',
                                            id: script.id,
                                            appid: appid
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '资源文件',
                        id: 'process.FileDesigner',
                        type: 'desiginer-category',
                        handleClick: (item, appid) => {
                            _openApp('process.ProcessManager', null, {navi: 4, application: appid});
                        }
                    }, {
                        name: '应用属性',
                        id: 'process.Property',
                        type: 'desiginer-category',
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
                                name: item.appName,
                                icon: item.appIcon,
                                defaultIcon: '../x_component_cms_Column/$Main/default/icon/column.png',
                                id: item.id,
                                type: 'app'
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '分类配置',
                        id: 'cms.CategoryManager',
                        type: 'desiginer-category',
                        handleClick: (item, appid) => {
                            _openApp('cms.ColumnManager', null, {navi: 'categoryConfig', column: appid});
                        },
                        children: [{
                            label: '分类',
                            type: 'desiginer',
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
                                            name: category.categoryName,
                                            icon: '',
                                            id: category.id,
                                            appid: category.appId
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '表单配置',
                        id: 'cms.FormDesigner',
                        type: 'desiginer-category',
                        label: '表单',
                        handleClick: (item, appid) => {
                            _openApp('cms.ColumnManager', null, {navi: 'formConfig', column: appid});
                        },
                        children: [{
                            label: '表单',
                            type: 'desiginer',
                            handleClick: (form) => {
                                _openApp('cms.FormDesigner', null, {id: form.id});
                            },
                            listAction: (appid) => {
                                return _cmsAction.FormAction.listFormByAppId(appid).then((forms) => {
                                    return _sort(forms.data).map((form) => {
                                        return {
                                            name: form.name,
                                            icon: '',
                                            id: form.id,
                                            appid: appid
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据字典',
                        id: 'cms.DictionaryDesigner',
                        type: 'desiginer-category',
                        label: '数据字典',
                        handleClick: (item, appid) => {
                            _openApp('cms.ColumnManager', null, {navi: 'dataConfig', column: appid});
                        },
                        children: [{
                            label: '数据字典',
                            type: 'desiginer',
                            handleClick: (dict) => {
                                _openApp('cms.DictionaryDesigner', null, {
                                    id: dict.id, application: {
                                        id: dict.appid
                                    }
                                });
                            },
                            listAction: (appid) => {
                                return _cmsAction.AppDictDesignAction.listWithAppInfo(appid).then((dicts) => {
                                    return _sort(dicts.data).map((dict) => {
                                        return {
                                            name: dict.name,
                                            icon: '',
                                            id: dict.id,
                                            appid: appid
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '脚本配置',
                        id: 'cms.ScriptDesigner',
                        type: 'desiginer-category',
                        label: '脚本配置',
                        handleClick: (item, appid) => {
                            _openApp('cms.ColumnManager', null, {navi: 'scriptConfig', column: appid});
                        },
                        children: [{
                            label: '脚本配置',
                            type: 'desiginer',
                            handleClick: (script) => {
                                _openApp('cms.ScriptDesigner', null, {id: script.id, application: {id: script.appid}});
                            },
                            listAction: (appid) => {
                                return _cmsAction.ScriptAction.listWithApplication(appid).then((scripts) => {
                                    return _sort(scripts.data).map((script) => {
                                        return {
                                            name: script.name,
                                            icon: '',
                                            id: script.id,
                                            appid: appid
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '资源文件',
                        id: 'cms.FileDesigner',
                        type: 'desiginer-category',
                        handleClick: (item, appid) => {
                            _openApp('cms.ColumnManager', null, {navi: 'fileConfig', column: appid});
                        }
                    }, {
                        name: '栏目属性',
                        id: 'cms.Property',
                        type: 'desiginer-category',
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
                                name: item.name,
                                id: item.id,
                                type: 'app'
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '视图配置',
                        id: 'query.PageManager',
                        type: 'desiginer-category',
                        handleClick: (item, appid) => {
                            _openApp('query.QueryManager', null, {navi: 0, application: appid});
                        },
                        children: [{
                            label: '视图',
                            type: 'desiginer',
                            handleClick: (view) => {
                                _openApp('query.ViewDesigner', null, {id: view.id, application: {id: view.appid}});
                            },
                            listAction: (appid) => {
                                return _queryAction.ViewAction.listWithQuery(appid).then((views) => {
                                    return _sort(views.data).map((view) => {
                                        return {
                                            name: view.name,
                                            icon: '',
                                            id: view.id,
                                            appid: appid
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '统计配置',
                        id: 'query.StatDesigner',
                        type: 'desiginer-category',
                        label: '统计',
                        handleClick: (item, appid) => {
                            _openApp('query.QueryManager', null, {navi: 1, application: appid});
                        },
                        children: [{
                            label: '统计',
                            type: 'desiginer',
                            handleClick: (stat) => {
                                _openApp('query.StatDesigner', null, {id: stat.id, application: {id: stat.appid}});
                            },
                            listAction: (appid) => {
                                return _queryAction.StatAction.listWithQuery(appid).then((stats) => {
                                    return _sort(stats.data).map((stat) => {
                                        return {
                                            name: stat.name,
                                            icon: '',
                                            id: stat.id,
                                            appid: appid
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据表',
                        id: 'query.TableDesigner',
                        type: 'desiginer-category',
                        label: '数据表',
                        handleClick: (item, appid) => {
                            _openApp('query.QueryManager', null, {navi: 2, application: appid});
                        },
                        children: [{
                            label: '数据表',
                            type: 'desiginer',
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
                                            name: table.name,
                                            icon: '',
                                            id: table.id,
                                            appid: appid
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '查询配置',
                        id: 'query.StatementDesigner',
                        type: 'desiginer-category',
                        label: '脚本配置',
                        handleClick: (item, appid) => {
                            _openApp('query.QueryManager', null, {navi: 3, application: appid});
                        },
                        children: [{
                            label: '查询配置',
                            type: 'desiginer',
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
                                            name: statement.name,
                                            icon: '',
                                            id: statement.id,
                                            appid: appid
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '导入模型',
                        id: 'query.ImporterDesigner',
                        type: 'desiginer-category',
                        handleClick: (item, appid) => {
                            _openApp('query.QueryManager', null, {navi: 4, application: appid});
                        },
                        children: [{
                            label: '导入模型',
                            type: 'desiginer',
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
                                            name: item.name,
                                            icon: '',
                                            id: item.id,
                                            appid: appid
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据中心属性',
                        id: 'query.Property',
                        type: 'desiginer-category',
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
                type: 'desiginer-category',
                handleClick: (item, appid) => {
                    _openApp('service.ServiceManager', null, {navi: 0});
                },
                children: [{
                    label: '代理',
                    type: 'desiginer',
                    handleClick: (item) => {
                        _openApp('service.AgentDesigner', null, {id: item.id});
                    },
                    listAction: () => {
                        return _serviceAction.AgentAction.list().then((pages) => {
                            return _sort(pages.data).map((page) => {
                                return {
                                    name: page.name,
                                    icon: '',
                                    id: page.id
                                };
                            });
                        })
                    }
                }]
            },
                {
                    name: '接口配置',
                    id: 'portal.InvokeDesigner',
                    type: 'desiginer-category',
                    label: '接口',
                    handleClick: (item, appid) => {
                        _openApp('service.ServiceManager', null, {navi: 1});
                    },
                    children: [{
                        label: '接口',
                        type: 'desiginer',
                        categorized: true,
                        handleClick: (item) => {
                            _openApp('service.InvokeDesigner', null, {id: item.id});
                        },
                        listAction: () => {
                            return _serviceAction.InvokeAction.list().then((items) => {
                                return _sort(items.data).map((item) => {
                                    return {
                                        name: item.name,
                                        id: item.id
                                    };
                                });
                            })
                        }
                    }]
                }, {
                    name: '脚本配置',
                    id: 'service.ScriptDesigner',
                    type: 'desiginer-category',
                    label: '脚本配置',
                    handleClick: (item, appid) => {
                        _openApp('service.ServiceManager', null, {navi: 2});
                    },
                    children: [{
                        label: '脚本配置',
                        type: 'desiginer',
                        handleClick: (item) => {
                            _openApp('service.ScriptDesigner', null, { id: item.id });
                        },
                        listAction: () => {
                            return _serviceAction.ScriptAction.list().then((items) => {
                                return _sort(items.data).map((item) => {
                                    return {
                                        name: item.name,
                                        icon: '',
                                        id: item.id
                                    };
                                });
                            })
                        }
                    }]
                }, {
                    name: '数据配置',
                    id: 'service.DictionaryDesigner',
                    type: 'desiginer-category',
                    label: '数据配置',
                    handleClick: () => {
                        _openApp('service.ServiceManager', null, {navi: 3});
                    },
                    children: [{
                        label: '脚本配置',
                        type: 'desiginer',
                        handleClick: (item) => {
                            _openApp('service.DictionaryDesigner', null, {id: item.id});
                        },
                        listAction: () => {
                            return _serviceAction.DictAction.list().then((items) => {
                                return _sort(items.data).map((item) => {
                                    return {
                                        name: item.name,
                                        icon: '',
                                        id: item.id
                                    };
                                });
                            })
                        }
                    }]
                }]
        }
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
        this.node.addEvent('mousedown', (e)=>{ e.stopPropagation(); })
    },
    addItem: function(pathData){
        var item = new o2DesignerBreadcrumb.Item(this, this.items.getLast() || null, pathData);
        this.items.push(item);
        item.load();
        return item;
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
            "position" : "absolute",
            "max-width" : "500px",
            "min-width" : "50px",
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
    _loadCustom : function( callback ){
        this.menus = []
        this.contentNode.addEvent('mousedown', (e)=>{ e.stopPropagation(); });
        this.contentNode.loadCss(`${this.item.breadcrumb.path}style.css`);

        var template = Array.clone(this.getChildrenTempalte());
        var creatable = !!(template.length === 1 && template[0].createFunction);
        Promise.resolve(this.getList( template )).then((data)=>{
            this.contentNode.loadHtml(
                this.item.breadcrumb.path+"menu.html",
                {
                    bind: {
                        lp: this.lp,
                        data: data,
                        creatable: creatable,
                        categories: this.categories.filter(c=>{
                            return c !== '未分类';
                        })
                    },
                    module: this
                },
                function(){
                    if(callback)callback();
                }.bind(this)
            );
        });
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
    getCurrentAppid: function (){
        return this.getAppid();
    },
    getChildrenTempalte: function () {
        return this.item.siblingConfigs;
    },
    getList: function ( template ) {
        var list = template;
        this.categories = [];
        if( list.length === 1 && list[0].listAction){
            var appid = this.getAppid();
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
                        !d.category && (d.category = '未分类');
                        if( !this.categories.includes( d.category ) ){
                            this.categories.push( d.category );
                        }
                    }
                    return d;
                });
            });
        }else{
            return list;
        }
    },
    handLoadCategory: function (e){
        var option = new Element('oo-option', { value: 'all' }).inject(e.currentTarget);
        option.setAttribute('text', '选择分类');

        var hasUncategory = false;
        if( this.categories.includes('未分类') ){
            this.categories = this.categories.erase( '未分类' );
            hasUncategory = true;
        }
        this.categories.forEach(category=>{
            var option = new Element('oo-option', { value: category }).inject(e.currentTarget);
            option.setAttribute('text', category);
        });
        if(hasUncategory){
            option = new Element('oo-option', { value: '未分类' }).inject(e.currentTarget);
            option.setAttribute('text', '未分类');
        }
    },
    handleChangeCategory: function (e){
        var items = this.menuNode && this.menuNode.querySelectorAll('.breadcrumb-menu-item');
        (items || []).forEach(item=>{
            _checkClass(
                item,
                'hide',
                e.currentTarget.value !== 'all' && item.dataset.category !== e.currentTarget.value
            );
        });
    },
    handleCreate: function (){
        this.getChildrenTempalte()[0].createFunction(this.getAppid());
    },
    handleMouseEnter: function (e, data){
        this.timer_hide_acitvie = window.setTimeout(() => {
            if(this.activeMenu){
                this.activeMenu.hide();
            }
            this.timer_hide_acitvie = null;
        }, this.options.hiddenDelay);
    },
    handleMouseLeave: function (e, data){
        if(this.timer_hide_acitvie){
            window.clearTimeout(this.timer_hide_acitvie);
        }
    },
    handleClick: function (e, data){
        data.handleClick(data, this.getAppid());
    },
    handleLoadItem: function (e, data){
        var app = this.item.breadcrumb.app;
        var _self = this;
        if( data.children && data.children.length > 0 ){
            var menu = new o2DesignerBreadcrumb.SubMenu(app.content, e.currentTarget, app, data, {
                axis : "x",
                hiddenDelay : 300,
                displayDelay : 300,
                onPostCreate: function (){ _self.setActiveMenu(this); },
                onShow: function (){ _self.setActiveMenu(this); },
                onHide: function (){ _self.cancelActiveMenu(this); }
            });
            menu.item = this.item;
            menu.parent = this;
            menu.currentAppid = this.getCurrentAppid();
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
        var parent = this.parent, topParent;
        while(parent){
            if( parent.data && parent.data.id && parent.data.type === 'app' ){
                return parent.data.id;
            }
            topParent = parent;
            parent = parent.parent;
        }
        return this.currentAppid;
    },
    getChildrenTempalte: function () {
        return this.data.children;
    },
    handleClick: function (e, data){
        data.handleClick(data, data.appid || this.getAppid());
    },
    getCurrentAppid: function (){
        return this.currentAppid;
    }
});
