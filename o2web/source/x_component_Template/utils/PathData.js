MWF.PathData = new Class({
    Implements: [Options, Events],
    options: {
        'type': 'cms', //process
        'bundle': ''
    },
    initialize: function (options) {
        this.setOptions(options);
        this.sheet2JsonOptions = {};
    },
    get: (id, pathList, ...args) => {
        return this._execute('get', id, pathList, ...args);
    },

    create: (id, pathList, data, ...args) => {
        if (data === undefined) {
            throw new Error('创建操作必须提供data参数');
        }
        return this._execute('create', id, pathList, data, ...args);
    },

    update: (id, pathList, data, ...args) => {
        if (data === undefined) {
            throw new Error('更新操作必须提供data参数');
        }
        return this._execute('update', id, pathList, data, ...args);
    },

    delete: (id, pathList, ...args) => {
        return this._execute('delete', id, pathList, ...args);
    },

    deleteDatatableLineField: (id, datatableName, index, fieldId) => {
        if (typeof fieldId !== 'string' || !fieldId.trim()) {
            throw new Error('字段ID必须是有效字符串');
        }
        return this.delete( id, [datatableName, "data", index.toString(), fieldId], null, null, false);
    },

    updateDatatableLineField: (id, datatableName, index, fieldId, data) => {
        if (typeof fieldId !== 'string' || !fieldId.trim()) {
            throw new Error('字段ID必须是有效字符串');
        }
        return this.update(id, [datatableName, "data", index.toString(), fieldId], data, null, null, false);
    },

    updateDatatableLine: (id, datatableName, index, data) => {
        return this.update(id, [datatableName, "data", index.toString()], data, null, null, false);
    },

    createDatatableLine: (id, datatableName, data) => {
        return this.create(id, [datatableName, "data"], data, null, null, false);
    },
    _safeDynamicCall: function(action, methodName, args){ // 私有方法：动态调用安全封装
        if (typeof action[methodName] !== 'function') {
            var availableMethods = Object.keys(action).filter(k => typeof action[k] === 'function');
            throw new Error(`调用的方法不存在: ${methodName} (可用方法: ${availableMethods.join(', ')})`);
        }
        try {
            return action[methodName](...args);
        } catch (e) {
            throw new Error(`调用${methodName}失败: ${e.message}`);
        }
    },
    // 私有方法：参数验证
    _validateParameters: function (type, id, paths){
        if (typeof id !== 'string' || !id.trim()) {
            throw new Error('文档ID必须是有效字符串');
        }

        if (paths.some(p => typeof p !== 'string' && typeof p !== 'number')) {
            throw new Error('路径元素必须是字符串或数字');
        }
    },
    _execute: (type, id, paths = [], ...args) => {
        try {
            if( typeof paths === "string" ){
                paths = [paths];
            }
            // 1. 基础验证
            this._validateParameters(type, id, paths);

            // 2. 参数标准化
            paths = paths.map(p => p.toString()); // 确保所有路径元素都是字符串

            // 3. 路径深度验证
            if (paths.length > 8) {
                throw new Error(`路径层级超过限制(8级)，当前: ${paths.length}`);
            }

            // 4. 获取Action实例
            var action = o2.Actions.load('x_cms_assemble_control').DataAction;

            // 5. 方法选择逻辑
            var methodMap = {
                create: 'createWithDocument',
                update: 'updateWithDocument',
                get: 'getWithDocument',
                delete: 'deleteWithDocument'
            };

            var baseMethod = methodMap[type];
            if (!baseMethod) {
                throw new Error(`不支持的操作类型: ${type} (支持: ${Object.keys(methodMap).join(', ')})`);
            }

            // 6. 动态方法处理
            var hasPath = paths.length > 0;
            var methodName = hasPath
                ? `${baseMethod}WithPath${paths.length-1}`
                : baseMethod;

            // 7. 准备调用参数
            var callArgs = [id];
            if (hasPath) callArgs.push(...paths);
            callArgs.push(...args);

            // 8. 安全调用
            return safeDynamicCall(action, methodName, callArgs);

        } catch (error) {
            console.error(`PathData操作失败[${type}]`, {
                documentId: id,
                paths: paths,
                error: {
                    name: error.name,
                    message: error.message,
                    stack: error.stack
                }
            });
            throw error; // 重新抛出以保持调用栈
        }
    }
});