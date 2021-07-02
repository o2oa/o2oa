MWF.xScript = MWF.xScript || {};
MWF.xScript.Environment = function(ev){
    var _data = ev.data;
    var _form = ev.form;
    var _forms = ev.forms;

    this.library = COMMON;
    //this.library.version = "4.0";

    this.power = {
        "isManager": MWF.AC.isProcessManager() || _form.businessData.control.allowReroute,
        "isReseter": _form.businessData.control.allowReset,
        "isDelete": _form.businessData.control.allowDeleteWork,
        "isPront": true,
        "isPrint": true
    };
    //data
    var getJSONData = function(jData){
        return new MWF.xScript.JSONData(jData, function(data, key, _self){
            var p = {"getKey": function(){return key;}, "getParent": function(){return _self;}};
            while (p && !_forms[p.getKey()]) p = p.getParent();
            if (p) if (p.getKey()) if (_forms[p.getKey()]) _forms[p.getKey()].resetData();
        }, "", null, _form);
    };


    this.setData = function(data){

        /**
         * data对象是流程平台中，流程实例的业务数据；以及内容管理平台中，文档实例的业务数据。<br/>
         * 这些数据一般情况下是通过您创建的表单收集而来的，也可以通过脚本进行创建和增删改查操作。<br/>
         * data对象基本上是一个JSON对象，您可以用访问JSON对象的方法访问data对象的所有数据，但增加和删除数据时略有不同。
         * @module data
         * @o2ordernumber 10
         * @example
         * //您可以在表单或流程的各个嵌入脚本中，通过this来获取当前实例的业务数据，如下：
         * var data = this.data;
         */
        this.data = getJSONData(data);

        /**
         * 访问或修改data对象的数据。<br/><br/>
         * data数据用于存储表单获取的数据，所有属性都是动态的，其格式和访问方式都和JSON类似。<br/>
         * 在表单脚本中使用data对象，实现了data和表单可编辑元素的双向绑定。<br/>
         * 改变data对象，会自动更新表单元素，修改表单可编辑元素，也会自动修改data对象。
         * @member {String|Number} [[property]]
         * @memberOf module:data
         * @instance
         * @example
         * var value = this.data.subject;  //获取名为subject的数据值
         *
         * //将subject的值修改为'123'。
         * //需要注意的是，用这种方式创建新字段，必须要在当前表单上有一个名为‘subject’的字段组件。
         * //如果表单上没有该组件，可以使用this.data.add('subject','123',true)。给已有字段赋值则没有这个限制。
         * this.data.subject = '123';
         *
         * @example
         * <caption>
         * <b>获取流程文档中的数据网格的值</b>：<br/>
         * 如有以下数据网格：
         *     <img src="img/module/data/datagrid.jpg">
         * 其数据网格设计如下(数据网格id为：datagrid)：
         *     <img src="img/module/data/datagridDesign.jpg">
         * </caption>
         * //获取流程文档中的数据网格的值
         * var data = this.data.datagrid;
         *
         * //获取到的data值格式如下：
         * {
         *   "data": [
         *       {
         *           "amountCol": { "amount": "12000" },
         *          "countCol": { "number": "10" },
         *          "nameCol": { "name": "手机" },
         *          "priceCol": { "price": "1200" }
         *      },
         *      {
         *          "amountCol": { "amount": "15000" },
         *          "countCol": { "number": "5" },
         *          "nameCol": { "name": "电脑" },
         *          "priceCol": { "price": "3000" }
         *      }
         *  ],
         *  "total": {
         *      "amountCol": "27000",
         *      "countCol": "15"
         *  }
         * }
         *
         *
         * //获取到数据网格中的其他数据：
         *
         * //获取数据网格中的第一条数据
         * var data = this.data.datagrid.data[0];
         *
         * //获取数据网格中的第一条数据的 nameCol 列的值
         * var data = this.data.datagrid.data[0].nameCol.name;
         *
         * //获取数据网格中的 amountCol 列的总计值
         * var data = this.data.datagrid.total.amountCol;
         *
         *@example
         * <caption>
         * <b>修改数据网格中的数据</b></br>
         * 经过本样例修改后，数据网格将变为：</br>
         *     <img src="img/module/data/datagrid2.jpg">
         * </caption>
         * //修改数据网格中的第一条数据的 nameCol 列的值
         * this.data.datagrid.data[0].nameCol.name='平板电脑';
         */

        /**
         * 为data对象添加一个数据节点。
         * @instance
         * @method add
         * @memberOf module:data
         * @param {(String|Number)} key - 要添加的新的数据节点名称或数组索引号。
         * @param {(String|Number|Array|JsonObject)} value - 新的数据节点的值。
         * @param {Boolean} [overwrite] - 如果要添加的节点已经存在，是否覆盖。默认为 false。
         * @return {(String|Number|Array|JsonObject)} 新添加的数据节点或原有的同名节点。
         * @o2syntax
         * var newData = this.data.add(key, value, overwrite);
         * @example
         * //为data添加一个名为"remark"值为"I am remark"的数据
         * this.data.add("remark", "I am remark");
         * @example
         * //为data添加一个名为"person"的Object对象数据
         * var person = this.data.add("person", {});
         * person.add("name", "Tom");
         * person.add("age", 23);
         *
         * //或者可以这样
         * var person = this.data.add("person", {name: "Tom", "age": "23"});
         * @example
         * //为data添加一个名为"orders"的数组对象数据
         * var orders = this.data.add("orders", []);
         * orders.add({name: "phone", count: 5});
         * orders.add({name: "computer", count: 10});
         * orders[0].add("count", 10, true);  //将第一条数据的count修改为10
         *
         * //或者可以这样
         * var orders = this.data.add("orders", [
         *  {name: "phone", count: 5},
         *  {name: "computer", count: 10}
         * ]);
         * //将第一条数据修改为name为mobile; count为10
         * orders.add(0, {name: "mobile", count: 10}, true);
         */

        /**保存data对象。不触发事件
         * @method save
         * @static
         * @memberOf module:data
         * @param {Function} [callback] - 保存成功后的回调函数。
         * @o2syntax
         * this.data.save(callback);
         * @example
         * this.data.save(function(json){
         *   this.form.notice("save success!", "success")
         *});
         */
        this.data.save = function(callback){
            _form.saveFormData(callback)
            // var formData = {
            //     "data": data,
            //     "sectionList": _form.getSectionList()
            // };
            // _form.workAction.saveSectionData(function(){if (callback) callback();}.bind(this), null, (ev.work.id || ev.workCompleted.id), formData);
        }
    };
    this.setData(_data);
    //task
    //this.task = ev.task;
    //this.task.process = function(routeName, opinion, callback){
    //    _form.submitWork(routeName, opinion, callback);
    //};
    //inquiredRouteList
    //this.inquiredRouteList = null;

    //workContext

    /**
     * 您可以通过workContext获取和流程相关的流程实例对象数据。
     * @module workContext
     * @o2range {Process}
     * @o2ordernumber 20
     * @o2syntax
     * //您可以在表单或流程的各个嵌入脚本中，通过this来获取当前流程实例数据，如下：
     * var context = this.workContext;
     */
    this.workContext = {
        /**
         * 获取当前流程实例对象：work对象或workCompleted对象。
         * @method getWork
         * @static
         * @return {(Work|WorkCompleted)} 流程实例对象；如果流程已结束，返回已结束的流程实例对象。
         * <div><br/>
         * 下面的Work对象和WorkCompleted对象为后台返回的数据，脚本中我们对这两个对象进行了修改和补充，如下：
         * </div>
         * <pre><code class='language-js'>{
         *      "creatorPersonDn": "张三@zhangsan@P",		//创建人，可能为空，如果由系统创建.
         *      "creatorPerson": "张三",  //创建人姓名
         *      "creatorIdentityDn": "张三@481c9edc-5fb5-41f1-b5c2-6ea609082cdb@I",		//创建人Identity,可能为空,如果由系统创建.
         *      "creatorIdentity": "张三" //创建人姓名
         *      "creatorUnitDn": "开发部@c448d8bb-98b8-4305-9d3f-12537723cfcc@U", //创建人组织全称,如果由系统创建。
         *      "creatorUnit": "开发部",  //创建人组织名称
         *      "creatorDepartment": "开发部",  //创建人组织名称，同creatorUnit
         *      "creatorCompany": "xx公司"  //创建人组织公司名称，creatorUnitLevel的第一段
         * }</code></pre>
         * @o2ActionOut x_processplatform_assemble_surface.WorkAction.manageGet|example=Work|ignoreNoDescr=true|ignoreProps=[properties]|Work对象:
         * @o2ActionOut x_processplatform_assemble_surface.WorkCompletedAction.get|example=WorkCompleted|ignoreProps=[properties]|WorkCompleted对象:
         * @o2syntax
         * var work = this.workContext.getWork();
         */
        "getWork": function(){return ev.work || ev.workCompleted;},
        /**
         * 获取当前流程实例所在的活动节点对象：activity对象。
         * @method getActivity
         * @static
         * @return {(Activity|Null)} 当前流程实例所在的活动节点对象，如果当前流程实例已流转完成，则返回null.
         * <pre><code class='language-js'>{
         *      "id": "801087c5-a4e6-4b91-bf4d-a81cdaa04471", //节点ID
         *      "name": "办理",  //节点名称
         *      "description": "", //节点描述
         *      "alias": "",  //节点别名
         *      "resetRange": "department", //重置处理人范围
         *      "resetCount": 0,  //重置处理人数字
         *      "allowReset": true, //是否允许重置
         *      "manualMode": "single" //处理方式 单人single, 并行parallel, 串行queue, grab抢办
         * }</code></pre>
         * @o2syntax
         * var activity = this.workContext.getActivity();
         */
        "getActivity": function(){return ev.activity || null;},

        /**
         * 当前流程实例正在流转中，并且当前用户有待办，则返回当前用户的待办对象，否则返回null。
         * @summary 获取当前流程与当前用户相关的待办对象：task对象。
         * @o2ActionOut x_processplatform_assemble_surface.TaskAction.get|example=Task|Task对象:
         * @method getTask
         * @static
         * @return {(Task|Null)} 当前用户的待办任务对象：task。当前用户没有对此流程实例的待办时，或流程实例已经流转结束，返回null。
         * <div><br/>
         * 下面的Task对象为后台返回的数据，脚本中我们对这它进行了修改和补充，如下：
         * </div>
         * <pre><code class='language-js'>{
         *      "personDn": "张三@zhangsan@P",		//创建人，可能为空，如果由系统创建.
         *      "person": "张三",  //创建人姓名
         *      "identityDn": "张三@481c9edc-5fb5-41f1-b5c2-6ea609082cdb@I",		//创建人Identity,可能为空,如果由系统创建.
         *      "identity": "张三" //创建人姓名
         *      "unitDn": "开发部@c448d8bb-98b8-4305-9d3f-12537723cfcc@U", //创建人组织全称,如果由系统创建。
         *      "unit": "开发部",  //创建人组织名称
         *      "department": "开发部",  //创建人组织名称，unit
         * }</code></pre>
         * @o2syntax
         * var task = this.workContext.getTask();
         */
        "getTask": function(){return ev.task || null;},

        /**
         * 获取当前流程实例的所有待办对象。如果流程实例已流转完成，则返回一个空数组。
         * @method getTaskList
         * @o2ActionOut x_processplatform_assemble_surface.TaskAction.listWithWork|example=Task
         * @static
         * @param {Function} [callback] 正确获取待办数组的回调，如果有此参数，本方法以异步执行，否则同步执行
         * @param {Function} [error] 获取待办数组出错时的回调。
         * @return {(Task[])} 待办任务列表.
         * @o2syntax
         * //本样例以同步执行
         * var taskList = this.workContext.getTaskList();
         * @o2syntax
         * //本样例以异步执行
         * this.workContext.getTaskList( function(taskList){
         *     //taskList 为待办数组
         * });
         */
        "getTaskList": function(callback, error){
            var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            var list;
            o2.Actions.get("x_processplatform_assemble_surface").listTaskByWork(ev.work.id, function(json){
                list = json.data;
                if (cb) cb(list);
            }, ecb, !!cb);
            return list;
        },
        /**
         * 根据当前工作的job获取当前流程实例的所有待办对象。如果流程实例已流转完成，则返回一个空数组。
         * @method getTaskListByJob
         * @o2ActionOut x_processplatform_assemble_surface.TaskAction.listWithJob|example=Task
         * @static
         * @param {Function} [callback] 正确获取待办数组的回调，如果有此参数，本方法以异步执行，否则同步执行
         * @param {Function} [error] 获取待办数组出错时的回调。
         * @return {(Task[])} 待办任务列表.
         * @o2syntax
         * //本样例以同步执行
         * var taskList = this.workContext.getTaskListByJob();
         * @o2syntax
         * //本样例以异步执行
         * this.workContext.getTaskListByJob( function(taskList){
         *     //taskList 为待办数组
         * });
         */
        "getTaskListByJob": function(callback, error){
            var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            var list;
            o2.Actions.get("x_processplatform_assemble_surface").listTaskByJob(ev.work.job, function(json){
                list = json.data;
                if (cb) cb(list);
            }, ecb, !!cb);
            return list;
        },

        /**
         * 获取当前流程实例的所有已办对象。如果流程实例没有任何人处理过，则返回一个空数组。
         * @method getTaskCompletedList
         * @static
         * @param {Function} [callback] 正确获取已办数组的回调，如果有此参数，本方法以异步执行，否则同步执行
         * @param {Function} [error] 获取已办数组出错时的回调。
         * @return {(TaskCompleted[])} 已办任务列表.
         * @o2ActionOut x_processplatform_assemble_surface.TaskCompletedAction.listWithWork|example=Task
         * @o2syntax
         * //本样例以同步执行
         * var taskCompletedList = this.workContext.getTaskCompletedList();
         * @o2syntax
         * //本样例以异步执行
         * this.workContext.getTaskCompletedList( function(taskCompletedList){
         *     //taskCompletedList 为已办数组
         * });
         */
        "getTaskCompletedList": function(callback, error){
            var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            var list;
            o2.Actions.get("x_processplatform_assemble_surface").listTaskCompletedByWork(ev.work.id, function(json){
                list = json.data;
                if (cb) cb(list);
            }, ecb, !!cb);
            return list;
        },

        /**
         * 根据当前工作的job获取当前流程实例的所有已办对象。如果流程实例没有任何人处理过，则返回一个空数组。
         * @method getTaskCompletedListByJob
         * @static
         * @param {Function} [callback] 正确获取已办数组的回调，如果有此参数，本方法以异步执行，否则同步执行
         * @param {Function} [error] 获取已办数组出错时的回调。
         * @return {(TaskCompleted[])} 已办任务列表.
         * @o2ActionOut x_processplatform_assemble_surface.TaskCompletedAction.listWithJob|example=Task
         * @o2syntax
         * //本样例以同步执行
         * var taskCompletedList = this.workContext.getTaskCompletedListByJob();
         * @o2syntax
         * //本样例以异步执行
         * this.workContext.getTaskCompletedListByJob( function(taskCompletedList){
         *     //taskCompletedList 为已办数组
         * });
         */
        "getTaskCompletedListByJob": function(callback, error){
            var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            var list;
            o2.Actions.get("x_processplatform_assemble_surface").listTaskCompletedByJob(ev.work.job, function(json){
                list = json.data;
                if (cb) cb(list);
            }, ecb, !!cb);
            return list;
        },

        /**
         * @summary 获取当前流程实例的所有待阅对象数组。如果流程实例无待阅，则返回一个空数组。
         * @method getReadList
         * @static
         * @param {Function} [callback] 正确获取待阅数组的回调，如果有此参数，本方法以异步执行，否则同步执行
         * @param {Function} [error] 获取待阅数组出错时的回调。
         * @return {(Read[])} 当前流程实例的所有待阅对象数组.
         * @o2ActionOut x_processplatform_assemble_surface.ReadAction.get|example=Read
         * @o2syntax
         * //本样例以同步执行
         * var readList = this.workContext.getReadList();
         * @o2syntax
         * //本样例以异步执行
         * this.workContext.getReadList( function(readList){
         *     //readList 为待阅数组
         * });
         */
        "getReadList": function(callback, error){
            var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            var list;
            o2.Actions.get("x_processplatform_assemble_surface").listReadByWork(ev.work.id, function(json){
                list = json.data;
                if (cb) cb(list);
            }, ecb, !!cb);
            return list;
        },
        /**
         * @summary 根据当前工作的job获取当前流程实例的所有待阅对象。如果流程实例无待阅，则返回一个空数组。
         * @method getReadListByJob
         * @static
         * @param {Function} [callback] 正确获取待阅数组的回调，如果有此参数，本方法以异步执行，否则同步执行
         * @param {Function} [error] 获取待阅数组出错时的回调。
         * @return {(Read[])} 当前流程实例的所有待阅对象数组.
         * @o2ActionOut x_processplatform_assemble_surface.ReadAction.listWithJob|example=Read
         * @o2syntax
         * //本样例以同步执行
         * var readList = this.workContext.getReadListByJob();
         * @o2syntax
         * //本样例以异步执行
         * this.workContext.getReadListByJob( function(readList){
         *     //readList 为待阅数组
         * });
         */
        "getReadListByJob": function(callback, error){
            var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            var list;
            o2.Actions.get("x_processplatform_assemble_surface").listReadByJob(ev.work.job, function(json){
                list = json.data;
                if (cb) cb(list);
            }, ecb, !!cb);
            return list;
        },

        /**
         * @summary 获取当前流程实例的所有已阅对象。如果流程实例没有已阅，则返回一个空数组。
         * @method getReadCompletedList
         * @static
         * @param {Function} [callback] 正确获取已阅数组的回调，如果有此参数，本方法以异步执行，否则同步执行
         * @param {Function} [error] 获取已阅数组出错时的回调。
         * @return {(ReadCompleted[])} 当前流程实例的所有已阅对象数组.
         * @o2ActionOut x_processplatform_assemble_surface.ReadCompletedAction.listWithWork|example=Read
         * @o2syntax
         * //本样例以同步执行
         * var readCompletedList = this.workContext.getReadCompletedList();
         * @o2syntax
         * //本样例以异步执行
         * this.workContext.getReadCompletedList( function(readCompletedList){
         *     //readCompletedList 为已阅数组
         * });
         */
        "getReadCompletedList": function(callback, error){
            var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            var list;
            o2.Actions.get("x_processplatform_assemble_surface").listReadCompletedByWork(ev.work.id, function(json){
                list = json.data;
                if (cb) cb(list);
            }, ecb, !!cb);
            return list;
        },
        /**
         * @summary 根据当前工作的job获取当前流程实例的所有已阅对象。如果流程实例没有已阅，则返回一个空数组。
         * @method getReadCompletedListByJob
         * @static
         * @param {Function} [callback] 正确获取已阅数组的回调，如果有此参数，本方法以异步执行，否则同步执行
         * @param {Function} [error] 获取已阅数组出错时的回调。
         * @return {(ReadCompleted[])} 当前流程实例的所有已阅对象数组.
         * @o2ActionOut x_processplatform_assemble_surface.ReadCompletedAction.listWithJob|example=Read
         * @o2syntax
         * //本样例以同步执行
         * var readCompletedList = this.workContext.getReadCompletedListByJob();
         * @o2syntax
         * //本样例以异步执行
         * this.workContext.getReadCompletedListByJob( function(readCompletedList){
         *     //readCompletedList 为已阅数组
         * });
         */
        "getReadCompletedListByJob": function(callback, error){
            var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            var list;
            o2.Actions.get("x_processplatform_assemble_surface").listReadCompletedByJob(ev.work.job, function(json){
                list = json.data;
                if (cb) cb(list);
            }, ecb, !!cb);
            return list;
        },
        "getJobTaskList": this.getTaskListByJob,
        "getJobReadList": this.getReadListByJob,
        "getJobTaskCompletedList": this.getTaskCompletedListByJob,
        "getJobReadCompletedList": this.getReadCompletedListByJob,

        /**
         * @summary 获取当前人对流程实例的权限。
         * @method getControl
         * @static
         * @return {WorkControl} 流程实例权限对象.
         * <pre><code class='language-js'>{
         *        "allowVisit": true,             //是否允许访问
         *        "allowProcessing": true,        //是否允许流转
         *        "allowReadProcessing": false,   //是否有待阅
         *        "allowSave": true,              //是否允许保存业务数据
         *        "allowReset": false,            //是否允许重置处理人
         *        "allowRetract": false,          //是否允许撤回
         *        "allowReroute": false,          //是否允许调度
         *        "allowDelete": true,             //是否允许删除流程实例
         *        "allowRollback": false,         //是否允许流程回溯
         *        "allowAddSplit": false,         //是否允许增加分支
         *        "allowPress": false,             //是否允许催办
         * }</code></pre>
         * @o2syntax
         * var control = this.workContext.getControl();
         */
        "getControl": function(){return ev.control;},
        /**
         * @summary 获取当前流程实例的所有流程记录(WorkLog)。
         * @method getWorkLogList
         * @static
         * @return {WorkLog[]} 流程记录对象.
         * @o2ActionOut x_processplatform_assemble_surface.WorkLogAction.listWithJob
         * @o2syntax
         * var workLogList = this.workContext.getWorkLogList();
         */
        "getWorkLogList": function(){return ev.workLogList;},
        /**
         * @summary 获取当前流程实例的所有流程记录(Record)。
         * @method getRecordList
         * @o2ActionOut x_processplatform_assemble_surface.RecordAction.listWithJob
         * @static
         * @return {Record[]} 流程记录(Record)对象.
         * @o2syntax
         * var recordList = this.workContext.getRecordList();
         */
        "getRecordList": function(){return ev.recordList;},
        /**
         * @summary 获取当前流程实例的附件对象列表。
         * @method getAttachmentList
         * @static
         * @return {WorkAttachmentData[]} 附件数据.
         * @o2ActionOut x_processplatform_assemble_surface.AttachmentAction.getWithWorkOrWorkCompleted|example=Attachment
         * @o2syntax
         * var attachmentList = this.workContext.getAttachmentList();
         */
        "getAttachmentList": function(){return ev.attachmentList;},
        /**
         * @summary 获取当前待办的可选路由。与task对象中的routeNameList取值相同。
         * @method getRouteList
         * @static
         * @return {String[]} 路由字符串数组.
         * @o2syntax
         * var routeList = this.workContext.getRouteList();
         */
        "getRouteList": function(){return (ev.task) ? ev.task.routeNameList: null;},
        "getInquiredRouteList": function(){return null;}
        // /**
        //  * @summary 重新设置流程实例标题。。
        //  * @method setTitle
        //  * @static
        //  * @param {String} title - 标题字符串.
        //  * @o2syntax
        //  * this.workContext.setTitle(title);
        //  * @example
        //  * this.workContext.setTitle("标题");
        //  */
        // "setTitle": function(title){
        //     if (!this.workAction){
        //         MWF.require("MWF.xScript.Actions.WorkActions", null, false);
        //         this.workAction = new MWF.xScript.Actions.WorkActions();
        //     }
        //     this.workAction.setTitle(ev.work.id, {"title": title});
        // }
    };
    this.workContent = this.workContext;
    var _redefineWorkProperties = function(work){
        if (work){
            work.creatorPersonDn = work.creatorPerson ||"";
            work.creatorUnitDn = work.creatorUnit ||"";
            work.creatorUnitDnList = work.creatorUnitList ||"";
            work.creatorIdentityDn = work.creatorIdentity ||"";
            var o = {
                "creatorPerson": {"get": function(){return this.creatorPersonDn.substring(0, this.creatorPersonDn.indexOf("@"));}},
                "creatorUnit": {"get": function(){return this.creatorUnitDn.substring(0, this.creatorUnitDn.indexOf("@"));}},
                "creatorDepartment": {"get": function(){return this.creatorUnitDn.substring(0, this.creatorUnitDn.indexOf("@"));}},
                "creatorIdentity": {"get": function(){return this.creatorIdentityDn.substring(0, this.creatorIdentityDn.indexOf("@"));}},
                // "creatorUnitList": {
                //     "get": function(){
                //         var v = [];
                //         this.creatorUnitDnList.each(function(dn){
                //             v.push(dn.substring(0, dn.indexOf("@")))
                //         });
                //         return v;
                //     }
                // },
                "creatorCompany": {"get": function(){
                    if (this.creatorUnitLevel){
                        var level = this.creatorUnitLevel.split("/");
                        return level[0];
                    }else{
                        return this.creatorUnitDn.substring(0, this.creatorUnitDn.indexOf("@"));
                    }
                }}
            };
            MWF.defineProperties(work, o);
        }
        return work;
    };
    var _redefineTaskProperties = function(task){
        if (task){
            task.personDn = task.person || "";
            task.unitDn = task.unit || "";
            task.unitDnList = task.unitList || "";
            task.identityDn = task.identity || "";
            var o = {
                "person": {"get": function(){return this.personDn.substring(0, this.personDn.indexOf("@"));}},
                "unit": {"get": function(){return this.unitDn.substring(0, this.unitDn.indexOf("@"));}},
                "department": {"get": function(){return this.unitDn.substring(0, this.unitDn.indexOf("@"));}},
                "identity": {"get": function(){return this.identityDn.substring(0, this.identityDn.indexOf("@"));}},
                // "unitList": {
                //     "get": function(){
                //         var v = [];
                //         this.unitDnList.each(function(dn){
                //             v.push(dn.substring(0, dn.indexOf("@")))
                //         });
                //         return v;
                //     }
                // },
                "company": {"get": function(){return this.unitList[0];}}
            };
            MWF.defineProperties(task, o);
        }
        return task;
    };
    _redefineWorkProperties(this.workContext.getWork());
    _redefineTaskProperties(_redefineWorkProperties(this.workContext.getTask()));


    //dict
    this.Dict = MWF.xScript.createDict((_form.businessData.work || _form.businessData.workCompleted).application);

    //unit
    var orgActions = null;
    var getOrgActions = function(){
        // if (!orgActions){
        //     MWF.xDesktop.requireApp("Org", "Actions.RestActions", null, false);
        //     orgActions = new MWF.xApplication.Org.Actions.RestActions ();
        // }
        if (!orgActions){
            MWF.require("MWF.xScript.Actions.UnitActions", null, false);
            orgActions = new MWF.xScript.Actions.UnitActions();
        }
    };
    var getNameFlag = function(name){
        var t = typeOf(name);
        if (t==="array"){
            var v = [];
            name.each(function(id){
                v.push((typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id);
            });
            return v;
        }else{
            return [(t==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name];
        }
    };


    this.org = {
        //群组***************
        //获取群组--返回群组的对象数组
        /**
         根据群组标识获取对应的群组对象数组：group对象数组
         */
        getGroup: function(name, async){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};

            var v = null;

            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listGroup(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var v = null;
            // orgActions.listGroup(data, function(json){v = json.data;}, null, false);
            // return (v && v.length===1) ? v[0] : v;
        },
        //查询下级群组--返回群组的对象数组
        //nested  布尔  true嵌套下级；false直接下级；默认false；
        listSubGroup: function(name, nested, async){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};

            var v = null;
            // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
            //     v = json.data;
            //     return v;
            // }.ag().catch(function(json){ return json; });
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listSubGroupNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listSubGroupDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;

            // var v = null;
            // if (nested){
            //     orgActions.listSubGroupNested(data, function(json){v = json.data;}, null, false);
            // }else{
            //     orgActions.listSubGroupDirect(data, function(json){v = json.data;}, null, false);
            // }
            // return v;
        },
        //查询上级群组--返回群组的对象数组
        //nested  布尔  true嵌套上级；false直接上级；默认false；
        listSupGroup:function(name, nested, async){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise
            if (nested){
                var promise = orgActions.listSupGroupNested(data, cb, null, !!async);
            }else{
                var promise = orgActions.listSupGroupDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
            // var v = null;
            // if (nested){
            //     orgActions.listSupGroupNested(data, function(json){v = json.data;}, null, false);
            // }else{
            //     orgActions.listSupGroupDirect(data, function(json){v = json.data;}, null, false);
            // }
            // return v;
        },
        //人员所在群组（嵌套）--返回群组的对象数组
        listGroupWithPerson:function(name, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listGroupWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
            // var v = null;
            // orgActions.listGroupWithPerson(data, function(json){v = json.data;}, null, false);
            // return v;
        },
        //群组是否拥有角色--返回true, false
        groupHasRole: function(name, role, async){
            getOrgActions();
            nameFlag = (typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
            var data = {"group":nameFlag,"roleList":getNameFlag(role)};

            var v = false;
            var cb = function(json){
                v = json.data.value;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.groupHasRole(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var v = false;
            // orgActions.groupHasRole(data, function(json){v = json.data.value;}, null, false);
            // return v;
        },

        //角色***************
        //获取角色--返回角色的对象数组
        getRole: function(name, async){
            getOrgActions();
            var data = {"roleList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listRole(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var v = null;
            // orgActions.listRole(data, function(json){v = json.data;}, null, false);
            // return (v && v.length===1) ? v[0] : v;
        },
        //人员所有角色（嵌套）--返回角色的对象数组
        listRoleWithPerson:function(name, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listRoleWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
            // var v = null;
            // orgActions.listRoleWithPerson(data, function(json){v = json.data;}, null, false);
            // return v;
        },

        //人员***************
        //人员是否拥有角色--返回true, false
        personHasRole: function(name, role, async){
            getOrgActions();
            nameFlag = (typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
            var data = {"person":nameFlag,"roleList":getNameFlag(role)};

            var v = false;
            var cb = function(json){
                v = json.data.value;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listRoleWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var v = false;
            // orgActions.personHasRole(data, function(json){v = json.data.value;}, null, false);
            // return v;
        },
        //获取人员--返回人员的对象数组
        getPerson: function(name, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
            // var v = null;
            // orgActions.listPerson(data, function(json){v = json.data;}, null, false);
            // return (v && v.length===1) ? v[0] : v;
        },
        //查询下级人员--返回人员的对象数组
        //nested  布尔  true嵌套下级；false直接下级；默认false；
        listSubPerson: function(name, nested, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listPersonSubNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listPersonSubDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
        },
        //查询上级人员--返回人员的对象数组
        //nested  布尔  true嵌套上级；false直接上级；默认false；
        listSupPerson: function(name, nested, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listPersonSupNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listPersonSupDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
        },
        //获取群组的所有人员--返回人员的对象数组
        listPersonWithGroup: function(name, async){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithGroup(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取角色的所有人员--返回人员的对象数组
        listPersonWithRole: function(name, async){
            getOrgActions();
            var data = {"roleList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise
            promise = orgActions.listPersonWithRole(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取身份的所有人员--返回人员的对象数组
        listPersonWithIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取身份的所有人员--返回人员的对象数组或人员对象
        getPersonWithIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                v =  (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //查询组织成员的人员--返回人员的对象数组
        //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
        listPersonWithUnit: function(name, nested, async){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listPersonWithUnitNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listPersonWithUnitDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
        },
        //根据属性查询人员--返回人员的对象数组
        //name  string 属性名
        //value  string 属性值
        listPersonWithAttribute: function(name, value, async){
            getOrgActions();
            var data = {"name": name, "attribute": value};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //根据属性查询人员--返回人员的全称数组
        //name  string 属性名
        //value  string 属性值
        listPersonNameWithAttribute: function(name, value, async){
            getOrgActions();
            var data = {"name": name, "attribute": value};
            var v = null;
            var cb = function(json){
                v = json.data.personList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithAttributeValue(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },

        //人员属性************
        //添加人员属性值(在属性中添加values值，如果没有此属性，则创建一个)
        appendPersonAttribute: function(person, attr, values, success, failure, async){
            getOrgActions();
            var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
            var data = {"attributeList":values,"name":attr,"person":personFlag};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.appendPersonAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
            //orgActions.appendPersonAttribute(data, cb, null, !!async);
        },
        //设置人员属性值(将属性值修改为values，如果没有此属性，则创建一个)
        setPersonAttribute: function(person, attr, values, success, failure, async){
            getOrgActions();
            var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
            var data = {"attributeList":values,"name":attr,"person":personFlag};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.setPersonAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var cb = function(json){
            //     if (success) return success(json);
            // }.ag().catch(function(xhr, text, error){
            //     if (failure) return failure(xhr, text, error);
            // });
            //
            // orgActions.setPersonAttribute(data, cb, null, !!async);
        },
        //获取人员属性值
        getPersonAttribute: function(person, attr, async){
            getOrgActions();
            var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
            var data = {"name":attr,"person":personFlag};
            var v = null;
            var cb = function(json){
                v = json.data.attributeList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.getPersonAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出人员所有属性的名称
        listPersonAttributeName: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data.nameList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonAttributeName(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出人员的所有属性
        listPersonAllAttribute: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonAllAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },

        //身份**********
        //获取身份
        getIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出人员的身份
        listIdentityWithPerson: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listIdentityWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //查询组织成员身份--返回身份的对象数组
        //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
        listIdentityWithUnit: function(name, nested, async){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;

            // var cb = function(json){
            //     v = json.data;
            //     if (async && o2.typeOf(async)=="function") return async(v);
            //     return v;
            // }.ag().catch(function(json){ return json; });

            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var method = (nested) ? "listIdentityWithUnitNested" : "listIdentityWithUnitDirect";
            var promise = orgActions[method](data, cb, null, !!async);
            promise.name = "org";

            //
            // if (nested){
            //     orgActions.listIdentityWithUnitNested(data, cb, null, !!async);
            // }else{
            //     orgActions.listIdentityWithUnitDirect(data, cb, null, !!async);
            // }
            return (!!async) ? promise : v;
        },

        //组织**********
        //获取组织
        getUnit: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnit(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //查询组织的下级--返回组织的对象数组
        //nested  布尔  true嵌套下级；false直接下级；默认false；
        listSubUnit: function(name, nested, async){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listUnitSubNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listUnitSubDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
        },
        //查询组织的上级--返回组织的对象数组
        //nested  布尔  true嵌套上级；false直接上级；默认false；
        //async 布尔 true异步请求
        listSupUnit: function(name, nested, async){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listUnitSupNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listUnitSupDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;

            // if (callback){
            //     if (nested){
            //         orgActions.listUnitSupNested(data, function(json){v = json.data; o2.runCallback(callback, "success", [v], this);});
            //     }else{
            //         orgActions.listUnitSupDirect(data, function(json){v = json.data; o2.runCallback(callback, "success", [v], this);});
            //     }
            // }else{
            //     var v = null;
            //     if (nested){
            //         orgActions.listUnitSupNested(data, function(json){v = json.data;}, null, false);
            //     }else{
            //         orgActions.listUnitSupDirect(data, function(json){v = json.data;}, null, false);
            //     }
            //     return v;
            // }
        },
        //根据个人身份获取组织
        //flag 数字    表示获取第几层的组织
        //     字符串  表示获取指定类型的组织
        //     空     表示获取直接所在的组织
        getUnitByIdentity: function(name, flag, async){
            getOrgActions();
            var getUnitMethod = "current";
            var v;
            if (flag){
                if (typeOf(flag)==="string") getUnitMethod = "type";
                if (typeOf(flag)==="number") getUnitMethod = "level";
            }

            var cb;
            var promise;
            switch (getUnitMethod){
                case "current":
                    var data = {"identityList":getNameFlag(name)};

                    // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                    //     v = json.data;  v=(v&&v.length===1) ? v[0] : v; return v;
                    // }.ag().catch(function(json){ return json; });


                    cb = function(json){
                        v = json.data;  v=(v&&v.length===1) ? v[0] : v;
                        if (async && o2.typeOf(async)=="function") return async(v);
                        return v;
                    };


                    promise = orgActions.listUnitWithIdentity(data, cb, null, !!async);
                    break;
                case "type":
                    var data = {"identity":(typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name,"type":flag};

                    cb = function(json){
                        v = json.data;
                        if (async && o2.typeOf(async)=="function") return async(v);
                        return v;
                    };

                    // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                    //     v = json.data;  return v;
                    // }.ag().catch(function(json){ return json; });

                    promise = orgActions.getUnitWithIdentityAndType(data, cb, null, !!async);
                    break;
                case "level":
                    var data = {"identity":(typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name,"level":flag};

                    cb = function(json){
                        v = json.data;  v=(v&&v.length===1) ? v[0] : v;
                        if (async && o2.typeOf(async)=="function") return async(v);
                        return v;
                    };

                    // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                    //     v = json.data;  return v;
                    // }.ag().catch(function(json){ return json; });

                    promise = orgActions.getUnitWithIdentityAndLevel(data, cb, null, !!async);
                    break;
            }
            return (!!async) ? promise : v;
        },
        //列出身份所在组织的所有上级组织
        listAllSupUnitWithIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitSupNestedWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取人员所在的所有组织
        listUnitWithPerson: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出人员所在组织的所有上级组织
        listAllSupUnitWithPerson: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitSupNestedWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //根据组织属性，获取所有符合的组织
        listUnitWithAttribute: function(name, attribute, async){
            getOrgActions();
            var data = {"name":name,"attribute":attribute};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            promise = orgActions.listUnitWithAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //根据组织职务，获取所有符合的组织
        listUnitWithDuty: function(name, id, async){
            getOrgActions();
            var data = {"name":name,"identity":(typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitWithDuty(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },

        //组织职务***********
        //获取指定的组织职务的身份
        getDuty: function(duty, id, async){
            getOrgActions();
            var data = {"name":duty,"unit":(typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id};
            var v = null;

            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.getDuty(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取身份的所有职务名称
        listDutyNameWithIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data.nameList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listDutyNameWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取组织的所有职务名称
        listDutyNameWithUnit: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data.nameList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listDutyNameWithUnit(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取组织的所有职务
        listUnitAllDuty: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitAllDuty(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出顶层组织
        listTopUnit: function(async){
            var action = MWF.Actions.get("x_organization_assemble_control");
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = action.listTopUnit(cb, null, !!async);
            return (!!async) ? promise : v;
        },

        //组织属性**************
        //添加组织属性值(在属性中添加values值，如果没有此属性，则创建一个)
        appendUnitAttribute: function(unit, attr, values, success, failure, async){
            getOrgActions();
            var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
            var data = {"attributeList":values,"name":attr,"unit":unitFlag};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.appendUnitAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var cb = function(json){
            //     if (success) return success(json);
            // }.ag().catch(function(xhr, text, error){
            //     if (failure) return failure(xhr, text, error);
            // });
            //
            // orgActions.appendPersonAttribute(data, cb, null, !!async);

            // orgActions.appendUnitAttribute(data, function(json){
            //     if (json.data.value){
            //         if (success) success();
            //     }else{
            //         if (failure) failure(null, "", "append values failed");
            //     }
            // }, function(xhr, text, error){
            //     if (failure) failure(xhr, text, error);
            // }, false);
        },
        //设置组织属性值(将属性值修改为values，如果没有此属性，则创建一个)
        setUnitAttribute: function(unit, attr, values, success, failure, async){
            getOrgActions();
            var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
            var data = {"attributeList":values,"name":attr,"unit":unitFlag};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.setUnitAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var cb = function(json){
            //     if (success) return success(json);
            // }.ag().catch(function(xhr, text, error){
            //     if (failure) return failure(xhr, text, error);
            // });
            // orgActions.setUnitAttribute(data, cb, null, !!async);

            // orgActions.setUnitAttribute(data, function(json){
            //     if (json.data.value){
            //         if (success) success();
            //     }else{
            //         if (failure) failure(null, "", "append values failed");
            //     }
            // }, function(xhr, text, error){
            //     if (failure) failure(xhr, text, error);
            // }, false);
        },
        //获取组织属性值
        getUnitAttribute: function(unit, attr, async){
            getOrgActions();
            var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
            var data = {"name":attr,"unit":unitFlag};
            var v = null;
            var cb = function(json){
                v = json.data.attributeList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.getUnitAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出组织所有属性的名称
        listUnitAttributeName: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data.nameList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitAttributeName(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出组织的所有属性
        listUnitAllAttribute: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitAllAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        }
    };

    this.Action = (function(){
        var actions = [];
        return function(root, json){
            var action = actions[root] || (actions[root] = new MWF.xDesktop.Actions.RestActions("", root, ""));
            action.getActions = function(callback){
                if (!this.actions) this.actions = {};
                Object.merge(this.actions, json);
                if (callback) callback();
            };
            this.invoke = function(option){
                action.invoke(option)
            }
        }
    })();

    this.service = {
        "jaxwsClient":{},
        "jaxrsClient":{}
    };

    var lookupAction = null;
    var getLookupAction = function(callback){
        if (!lookupAction){
            MWF.require("MWF.xDesktop.Actions.RestActions", function(){
                lookupAction = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "");
                lookupAction.getActions = function(actionCallback){
                    this.actions = {
                        //"lookup": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}"},
                        //"getView": {"uri": "/jaxrs/view/{id}/design"}
                        "lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
                        "getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"}
                    };
                    if (actionCallback) actionCallback();
                };
                if (callback) callback();
            });
        }else{
            if (callback) callback();
        }
    };

    this.view = {
        "lookup": function(view, callback, async){
            var filterList = {"filterList": (view.filter || null)};
            MWF.Actions.load("x_query_assemble_surface").ViewAction.executeWithQuery(view.view, view.application, filterList, function(json){
                var data = {
                    "grid": json.data.grid || json.data.groupGrid,
                    "groupGrid": json.data.groupGrid
                };
                if (callback) callback(data);
            }, null, async);
        },

        "lookupV1": function(view, callback){
            getLookupAction(function(){
                lookupAction.invoke({"name": "lookup","async": true, "parameter": {"view": view.view, "application": view.application},"success": function(json){
                    var data = {
                        "grid": json.data.grid,
                        "groupGrid": json.data.groupGrid
                    };
                    if (callback) callback(data);
                }.bind(this)});
            }.bind(this));
        },
        "select": function(view, callback, options){
            if (view.view){
                var viewJson = {
                    "application": view.application || _form.json.application,
                    "viewName": view.view || "",
                    "isTitle": (view.isTitle===false) ? "no" : "yes",
                    "select": (view.isMulti===false) ? "single" : "multi",
                    "filter": view.filter
                };
                if (!options) options = {};
                options.width = view.width;
                options.height = view.height;
                options.title = view.caption;
                var width = options.width || "700";
                var height = options.height || "400";

                if (layout.mobile){
                    var size = document.body.getSize();
                    width = size.x;
                    height = size.y;
                    options.style = "viewmobile";
                }
                width = width.toInt();
                height = height.toInt();

                var size = _form.app.content.getSize();
                var x = (size.x-width)/2;
                var y = (size.y-height)/2;
                if (x<0) x = 0;
                if (y<0) y = 0;
                if (layout.mobile){
                    x = 20;
                    y = 0;
                }

                var _self = this;
                MWF.require("MWF.xDesktop.Dialog", function(){
                    var dlg = new MWF.xDesktop.Dialog({
                        "title": options.title || "select view",
                        "style": options.style || "view",
                        "top": y,
                        "left": x-20,
                        "fromTop":y,
                        "fromLeft": x-20,
                        "width": width,
                        "height": height,
                        "html": "<div style='height: 100%;'></div>",
                        "maskNode": _form.app.content,
                        "container": _form.app.content,
                        "buttonList": [
                            {
                                "text": MWF.LP.process.button.ok,
                                "action": function(){
                                    //if (callback) callback(_self.view.selectedItems);
                                    if (callback) callback(_self.view.getData());
                                    this.close();
                                }
                            },
                            {
                                "text": MWF.LP.process.button.cancel,
                                "action": function(){this.close();}
                            }
                        ]
                    });
                    dlg.show();

                    if (layout.mobile){
                        var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                        var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                        if (backAction) backAction.addEvent("click", function(e){
                            dlg.close();
                        }.bind(this));
                        if (okAction) okAction.addEvent("click", function(e){
                            //if (callback) callback(this.view.selectedItems);
                            if (callback) callback(this.view.getData());
                            dlg.close();
                        }.bind(this));
                    }

                    MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
                        this.view = new MWF.xApplication.query.Query.Viewer(dlg.content.getFirst(), viewJson, {"style": "select"}, _form.app, _form.Macro);
                    }.bind(this));
                }.bind(this));
            }
        }
    };

    this.statement = {
        "execute": function (statement, callback, async) {
            var parameter = this.parseParameter(statement.parameter);
            var filterList = this.parseFilter(statement.filter, parameter);
            var obj = {
                "filterList": filterList,
                "parameter" : parameter
            };
            MWF.Actions.load("x_query_assemble_surface").StatementAction.executeV2(
                statement.name, statement.mode || "data", statement.page || 1, statement.pageSize || 20, obj,
                function (json) {
                    if (callback) callback(json);
                }, null, async);
        },
        parseFilter : function( filter, parameter ){
            if( typeOf(filter) !== "array" )return [];
            var filterList = [];
            ( filter || [] ).each( function (d) {
                var parameterName = d.path.replace(/\./g, "_");
                var value = d.value;
                if( d.comparison === "like" || d.comparison === "notLike" ){
                    if( value.substr(0, 1) !== "%" )value = "%"+value;
                    if( value.substr(value.length-1,1) !== "%" )value = value+"%";
                    parameter[ parameterName ] = value; //"%"+value+"%";
                }else{
                    if( d.formatType === "dateTimeValue" || d.formatType === "datetimeValue"){
                        value = "{ts '"+value+"'}"
                    }else if( d.formatType === "dateValue" ){
                        value = "{d '"+value+"'}"
                    }else if( d.formatType === "timeValue" ){
                        value = "{t '"+value+"'}"
                    }
                    parameter[ parameterName ] = value;
                }
                d.value = parameterName;

                filterList.push( d );
            }.bind(this));
            return filterList;
        },
        parseParameter : function( obj ){
            if( typeOf(obj) !== "object" )return {};
            var parameter = {};
            //传入的参数
            for( var p in obj ){
                var value = obj[p];
                if( typeOf( value ) === "date" ){
                    value = "{ts '"+value.format("db")+"'}"
                }
                parameter[ p ] = value;
            }
            return parameter;
        },
        "select": function (statement, callback, options) {
            if (statement.name) {
                // var parameter = this.parseParameter(statement.parameter);
                // var filterList = this.parseFilter(statement.filter, parameter);
                var statementJson = {
                    "statementId": statement.name || "",
                    "isTitle": (statement.isTitle === false) ? "no" : "yes",
                    "select": (statement.isMulti === false) ? "single" : "multi",
                    "filter": statement.filter,
                    "parameter": statement.parameter
                };
                if (!options) options = {};
                options.width = statement.width;
                options.height = statement.height;
                options.title = statement.caption;

                var width = options.width || "700";
                var height = options.height || "400";

                if (layout.mobile) {
                    var size = document.body.getSize();
                    width = size.x;
                    height = size.y;
                    options.style = "viewmobile";
                }
                width = width.toInt();
                height = height.toInt();

                var size = _form.app.content.getSize();
                var x = (size.x - width) / 2;
                var y = (size.y - height) / 2;
                if (x < 0) x = 0;
                if (y < 0) y = 0;
                if (layout.mobile) {
                    x = 20;
                    y = 0;
                }

                var _self = this;
                MWF.require("MWF.xDesktop.Dialog", function () {
                    var dlg = new MWF.xDesktop.Dialog({
                        "title": options.title || "select statement view",
                        "style": options.style || "view",
                        "top": y,
                        "left": x - 20,
                        "fromTop": y,
                        "fromLeft": x - 20,
                        "width": width,
                        "height": height,
                        "html": "<div style='height: 100%;'></div>",
                        "maskNode": _form.app.content,
                        "container": _form.app.content,
                        "buttonList": [
                            {
                                "text": MWF.LP.process.button.ok,
                                "action": function () {
                                    //if (callback) callback(_self.view.selectedItems);
                                    if (callback) callback(_self.statement.getData());
                                    this.close();
                                }
                            },
                            {
                                "text": MWF.LP.process.button.cancel,
                                "action": function () { this.close(); }
                            }
                        ]
                    });
                    dlg.show();

                    if (layout.mobile) {
                        var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                        var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                        if (backAction) backAction.addEvent("click", function (e) {
                            dlg.close();
                        }.bind(this));
                        if (okAction) okAction.addEvent("click", function (e) {
                            //if (callback) callback(this.view.selectedItems);
                            if (callback) callback(this.statement.getData());
                            dlg.close();
                        }.bind(this));
                    }

                    MWF.xDesktop.requireApp("query.Query", "Statement", function () {
                        this.statement = new MWF.xApplication.query.Query.Statement(dlg.content.getFirst(), statementJson, { "style": "select" }, _form.app, _form.Macro);
                    }.bind(this));
                }.bind(this));
            }
        }
    };

    this.importer = {
        "upload": function (options, callback, async) {
            MWF.xDesktop.requireApp("query.Query", "Importer", function () {
                var importer = new MWF.xApplication.query.Query.Importer(_form.app.content, options, {}, _form.app, _form.Macro);
                importer.load();
            }.bind(this));
        },
        "downloadTemplate": function(options, fileName){
            MWF.xDesktop.requireApp("query.Query", "Importer", function () {
                var importer = new MWF.xApplication.query.Query.Importer(_form.app.content, options, {}, _form.app, _form.Macro);
                importer.downloadTemplate(fileName);
            }.bind(this));
        }
    };


    //include 引用脚本
    //optionsOrName : {
    //  type : "", 默认为process, 可以为 portal  process  cms
    //  application : "", 门户/流程/CMS的名称/别名/id, 默认为当前应用
    //  name : "" // 脚本名称/别名/id
    //}
    //或者name: "" // 脚本名称/别名/id
    // if( !window.includedScripts ){
    //     var includedScripts = window.includedScripts = [];
    // }else{
    //     var includedScripts = window.includedScripts;
    // }

    /**
     * this.include是一个方法，当您在流程、门户或者内容管理中创建了脚本配置，可以使用this.include()用来引用脚本配置。<br/>
     * <b>（建议使用表单中的预加载脚本，需要判断加载的时候才使用本方法加载脚本，此时建议异步加载有助于表单加载速度。）</b><br/>
     * @module include
     * @o2ordernumber 140
     * @param {(String|Object|String[]|Object[])} optionsOrName 可以是脚本标识字符串（数组）或者是对象（数组）。
     * <pre><code class='language-js'>
     * //如果需要引用本应用的脚本配置，将options设置为String或者String Array。
     * this.include("initScript") //脚本配置的名称、别名或id
     * this.include(["initScript","initScript2"]) //可以是字符串数组
     *
     * //如果需要引用其他应用的脚本配置，将options设置为Object或者Object Array;
     * this.include({
     *       //type: 应用类型。可以为 portal  process  cms。
     *       //如果没有该选项或者值为空字符串，则表示应用脚本和被应用的脚本配置类型相同。
     *       //比如在门户的A应用脚本中引用门户B应用的脚本配置，则type可以省略。
     *       type : "portal",
     *       application : "首页", // 门户、流程、CMS的名称、别名、id。 默认为当前应用
     *       name : "initScript" // 脚本配置的名称、别名或id
     * })
     * this.include([  //也可以对象和字符串混合数组
     *  {
     *       type : "portal",
     *       application : "首页",
     *       name : "initScript"
     *  },
     *  "initScript2"
     * ])
     * </code></pre>
     * @param {Function} [callback] 加载后执行的回调方法
     * @param {Boolean} [async] 是否异步加载
     * @o2syntax
     * //您可以在表单、流程、视图和查询视图的各个嵌入脚本中，通过this.include()来引用本应用或其他应用的脚本配置，如下：
     * this.include( optionsOrName, callback, async )
     * @example
     * <caption>
     *    <b>样例一：</b>在通用脚本中定义返回当前人员名称的方法，在各个门户应用都使用这个方法显示人员名称。<br/>
     *     1、在门户应用中有一个commonApp的应用，在该应用中创建一个脚本，命名为initScript，并定义方法。
     *     <img src='img/module/include/define1.png' />
     * </caption>
     * //定义一个方法
     * this.define("getUserName", function(){
     *   return ( layout.desktop.session.user || layout.user ).name
     * }.bind(this))
     * @example
     * <caption>
     *      2、在门户页面中添加事件'queryLoad',在事件中引入 initScript 脚本配置。
     *     <img src='img/module/include/define2.png' style='max-width:700px;'/>
     * </caption>
     * this.include({
     *      type : "portal",
     *      application : "commonApp",
     *      name : "initScript"
     * })
     *
     * @example
     * <caption>
     *  3、在门户页面的'load'事件中使用方法。<br/>
     *     <img src='img/module/include/define3.png' style='max-width:700px;'/>
     * </caption>
     * var userNameNode = this.page.get("userName").node; //获取Dom对象
     * var urerName = this.getUserName(); //使用initScript脚本中的方法
     * userNameNode.set("text", urerName ); //为DOM对象设置值
     */

    var includedScripts = [];
    var _includeSingle = function( optionsOrName , callback, async){
        var options = optionsOrName;
        if( typeOf( options ) == "string" ){
            options = { name : options };
        }
        var name = options.name;
        var type = ( options.type && options.application ) ?  options.type : "process";
        var application = options.application || _form.json.application;
        var key = type +"-" + application + "-"  + name;
        if (includedScripts.indexOf( key )> -1){
            if (callback) callback.apply(this);
            return;
        }
        //if (includedScripts.indexOf( name )> -1){
        //    if (callback) callback.apply(this);
        //    return;
        //}
        var scriptAction;
        switch ( type ){
            case "portal" :
                if( this.scriptActionPortal ){
                    scriptAction = this.scriptActionPortal;
                }else{
                    MWF.require("MWF.xScript.Actions.PortalScriptActions", null, false);
                    scriptAction = this.scriptActionPortal = new MWF.xScript.Actions.PortalScriptActions();
                }
                break;
            case "process" :
                if( this.scriptActionProcess ){
                    scriptAction = this.scriptActionProcess;
                }else{
                    MWF.require("MWF.xScript.Actions.ScriptActions", null, false);
                    scriptAction = this.scriptActionProcess = new MWF.xScript.Actions.ScriptActions();
                }
                break;
            case "cms" :
                if( this.scriptActionCMS ){
                    scriptAction = this.scriptActionCMS;
                }else{
                    MWF.require("MWF.xScript.Actions.CMSScriptActions", null, false);
                    scriptAction = this.scriptActionCMS = new MWF.xScript.Actions.CMSScriptActions();
                }
                break;
        }

        scriptAction.getScriptByName( application, name, includedScripts, function(json){
            if (json.data){
                includedScripts.push( key );

                //名称、别名、id
                json.data.importedList.each( function ( flag ) {
                    if( type === "portal" ){
                        includedScripts.push( type + "-" + json.data.portal + "-" + flag );
                        if( json.data.portalName )includedScripts.push( type + "-" + json.data.portalName + "-" + flag );
                        if( json.data.portalAlias )includedScripts.push( type + "-" + json.data.portalAlias + "-" + flag );
                    }else if( type === "cms" ){
                        includedScripts.push( type + "-" + json.data.appId + "-" + flag );
                        if( json.data.appName )includedScripts.push( type + "-" + json.data.appName + "-" + flag );
                        if( json.data.appAlias )includedScripts.push( type + "-" + json.data.appAlias + "-" + flag );
                    }else if( type === "process" ){
                        includedScripts.push( type + "-" + json.data.application + "-" + flag );
                        if( json.data.appName )includedScripts.push( type + "-" + json.data.appName + "-" + flag );
                        if( json.data.appAlias )includedScripts.push( type + "-" + json.data.appAlias + "-" + flag );
                    }
                });
                includedScripts = includedScripts.concat(json.data.importedList);

                MWF.Macro.exec(json.data.text, this);
                if (callback) callback.apply(this);
            }else{
                if (callback) callback.apply(this);
            }
        }.bind(this), null, !!async);
    }
    this.include = function( optionsOrName , callback, async){
        if (o2.typeOf(optionsOrName)=="array"){
            if (!!async){
                var count = optionsOrName.length;
                var loaded = 0;
                optionsOrName.each(function(option){
                    _includeSingle.apply(this, [option, function(){
                        loaded++;
                        if (loaded>=count) if (callback) callback.apply(this);;
                    }.bind(this), true]);
                }.bind(this));

            }else{
                optionsOrName.each(function(option){
                    _includeSingle.apply(this, [option]);
                }.bind(this));
                if (callback) callback.apply(this);
            }
        }else{
            _includeSingle.apply(this, [optionsOrName , callback, async])
        }
    };

    /**
     * this.define是一个方法，您可以在流程、门户或者内容管理中创建脚本配置，在脚本配置中您可以通过this.define()来定义自己的方法。<br/>
     * 通过这种方式定义方法，在不同的应用使用相同的方法名称也不会造成冲突。
     * @module define
     * @o2ordernumber 150
     * @param {(String)} name 定义的方法名称。
     * @param {Function} fun  定义的方法
     * @param {Boolean} [overwrite] 定义的方法是否能被覆盖重写。默认值为true。
     * @o2syntax
     * this.define(name, fun, overwrite)
     * @example
     * <caption>
     *    <b>样例：</b>在通用脚本中定义返回当前人员名称的方法，在各个门户应用都使用这个方法显示人员名称。<br/>
     *     1、在门户应用中有一个commonApp的应用，在该应用中创建一个脚本，命名为initScript，并定义方法。
     *     <img src='img/module/include/define1.png' />
     * </caption>
     * //定义一个方法
     * this.define("getUserName", function(){
     *   return ( layout.desktop.session.user || layout.user ).name
     * }.bind(this))
     * @example
     * <caption>
     *      2、在门户页面中添加事件'queryLoad',在事件中引入 initScript 脚本配置。
     *     <img src='img/module/include/define2.png' style='max-width:700px;'/>
     * </caption>
     * this.include({
     *      type : "portal",
     *      application : "commonApp",
     *      name : "initScript"
     * })
     *
     * @example
     * <caption>
     *  3、在门户页面的'load'事件中使用方法。<br/>
     *     <img src='img/module/include/define3.png' style='max-width:700px;'/>
     * </caption>
     * var userNameNode = this.page.get("userName").node; //获取Dom对象
     * var urerName = this.getUserName(); //使用initScript脚本中的方法
     * userNameNode.set("text", urerName ); //为DOM对象设置值
     */
    this.define = function(name, fun, overwrite){
        var over = true;
        if (overwrite===false) over = false;
        var o = {};
        o[name] = {"value": fun, "configurable": over};
        MWF.defineProperties(this, o);
    }.bind(this);

    //如果前端事件有异步调用，想要在异步调用结束后继续运行页面加载，
    //可在调用前执行 var resolve = this.wait();
    //在异步调用结束后 执行 resolve.cb()；
    //目前只有表单的queryload事件支持此方法。

    /**
     * this.wait是一个方法，可以用来处理异步调用时的页面加载。<br/>
     * 该方法使用的具体场景：为了加快速度，需要一次性加载全部外部资源（如：数据字典、外部JS、内容管理文档等）后，再进行表单的加载。<br/>
     * this.wait需和this.goon配合使用。<br/>
     * <b>目前只有流程表单的queryload事件支持此方法。</b>
     * @module wait
     * @o2range {Process}
     * @o2syntax
     * var resolve = this.wait(); //让表单停止加载页面
     *
     * if (resolve && resolve.cb){
     *      resolve.cb(); //通过 resolve.cb() 方法继续执行表单加载
     * }else{
     *      //如果没有发生异步，则resolve.cb方法不存在，
     *      //所以在回调中中使用this.goon();使表单继续加载
     *      this.goon();
     * }
     * @example
     * <caption>需要在加载数据字典，内容管理文档数据，按照条件获取的脚本后，再进行加载表单。</caption>
     *
     * var resolve = this.wait(); //this.wait()让表单加载等待回调
     * var scriptLoaded = false; //脚本是否加载完成标识，按条件判断的脚本才建议用this.include(),否则使用预加载脚本更快。
     * var documentLoaded = false; //内容管理文档是否加载完成标识
     * var dictLoaded = true; //数据字典是否加载完成标识
     *
     * //检查是否全部资源已加载，如果是继续加载表单
     * var checkLoad = function(){
     *     if (scriptLoaded && documentLoaded && dictLoaded){ //各种资源以及加载完成
     *       if (resolve && resolve.cb){
     *            resolve.cb(); //通过 resolve.cb() 方法继续执行表单加载
     *        }else{
     *            //如果没有发生异步，则resolve.cb方法不存在，
     *            //所以在回调中中使用this.goon();使表单继续加载
     *            this.goon();
     *        }
     *      }
     * }.bind(this);
     *
     * //判断内容管理文档加载
     * if( this.data.documentId ){
     *      //异步载入内容管理文档
     *      o2.Actions.get("x_cms_assemble_control").getDocument(this.data.documentId, function (json) {
     *          this.form.documentJson = json; //将数据存在this.form上，以便其他地方使用
     *          documentLoaded = true; //标记内容管理加载完成
     *          checkLoad(); //检查全部资源是否完成加载
     *      }.bind(this), null, true); //true 为异步加载标志
     *  }else{
     *     documentLoaded = true; ////标记内容管理加载完成
     *     checkLoad(); //检查全部资源是否完成加载
     * }
     *
     * //判断脚本加载
     * if( this.data.scriptName ){ //假设scriptName为判断条件
     *      //加载脚本
     *     this.include( this.data.scriptName, function(){  //第二个参数为异步加载后的回调
     *         scriptLoaded = true; //标记脚本加载完成
     *         checkLoad(); //检查全部资源是否完成加载
     *     }, true ); //第三个参数表示异步
     * }else{
     *      scriptLoaded = true; ////标记脚本加载完成
     *     checkLoad(); //检查全部资源是否完成加载
     * }
     *
     * //加载数据字典bulletinDictionary的category数据
     * var dict = new Dict("bulletinDictionary");
     * dict.get("category", function(data){ //成功的回调
     *          this.form.bulletinCategory = data; //将数据存在this.form上，以便其他地方使用
     *          dictLoaded = true; //标记数据字典加载完成
     *          checkLoad(); //检查全部资源是否完成加载
     *    }.bind(this), function(xhr){ //错误的回调
     *          dictLoaded = true; ////标记数据字典加载完成
     *          checkLoad(); //检查全部资源是否完成加载
     *    }, true //异步执行
     * )
     */
    this.wait = function(){
        var _self = this;
        resolve = {"cb":  _self.goon.bind(_self)};
        var setResolve = function(callback){
            resolve.cb = callback;
        }.bind(this);
        this.target.event_resolve = setResolve;
        return resolve;
    }
    //和this.wait配合使用，
    //如果没有异步，则resolve.cb方法不存在，
    //所以在回调中中使用this.goon();使表单继续加载
    this.goon = function(){
        this.target.event_resolve = null;
    }


    //仅前台对象-----------------------------------------
    //form

    /**
     * form对象可在流程表单或内容管理表单中可用。（仅前端脚本可用）。
     * @module form
     * @o2range {Process|CMS}
     * @o2ordernumber 40
     * @o2syntax
     * //您可以在流程表单和内容管理的前端脚本中，通过this来获取form对象，如下：
     * var form = this.form;
     */
    this.page = this.form = {
        /**
         * 获取当前表单的基本信息。
         * @method getInfor
         * @static
         * @return {Object} 表单的基本信息.
         * <pre><code class='language-js'>{
         *    "id": "db3b2766-93a1-4058-b522-0edb922bd84f",   //表单ID
         *    "name": "报销申请表单",                         //表单名称
         *    "alias": "报销申请表单",                        //表单别名
         *    "description": "",                              //表单描述
         *    "application": "1dc23336-6be6-402b-bed6-36e707a1dd17",  //应用ID
         *    "lastUpdatePerson": "XX@huqi@P",                //最后修改人
         *    "lastUpdateTime": "2018-09-30 22:46:30",        //最后修改时间
         *    "icon": "...",                                  //表单图标
         * }</code></pre>
         * @o2syntax
         * var form = this.form.getInfor();
         */
        "getInfor": function(){return ev.formInfor;},

        "infor": ev.formInfor,

        /**
         * 获取打开当前文档的component对象。平台中每一个窗口应用，就是一个component对象。此处获取到的对象为x_component_process_Work。
         * @method getApp
         * @static
         * @return {x_component_process_Work}打开当前文档的component对象.
         * @o2syntax
         * var app = this.form.getApp();
         * @example
         * var app = this.form.getApp();
        //所有component对象都有以下方法。
        app.openInNewWindow();  //在新窗口中打开当前应用
        app.setCurrent();   //将当前应用设置为激活状态
        app.minSize();      //应用窗口最小化
        app.maxSize();      //应用窗口最大化
        app.restoreSize();  //应用窗口还原
        app.refresh();      //刷新应用
        app.close();        //关闭应用
        app.setTitle(str);  //设置应用标题
        app.dialog(option); //弹出一个对话框（详见MWF.widget.Dialog）

        //显示一个通知消息
        app.notice(content, type, target, where, offset);

        //显示一个确认框
        app.confirm(type, e, title, text, width, height, ok, cancel);

        //弹出一个信息框
        app.alert(type, e, title, text, width, height);

        //为应用绑定一个事件
        app.addEvent(type, fun);
         */
        "getApp": function(){return _form.app;},

        "app": _form.app,

        /**
         * 获取Form对应的DOM对象。
         * @method node
         * @static
         * @return {HTMLDivElement} 当前form对应的div对象.
         * @o2syntax
         * var node = this.form.node();
         */
        "node": function(){return _form.node;},

        /**
         * 获取表单是否可编辑。只读。
         * @member readonly
         * @static
         * @return {Boolean} 是否只读.
         * @o2syntax
         * var readonly = this.form.readonly;
         */
        "readonly": _form.options.readonly,

        /**
         * 获取表单元素对象。<br/>
         * <table>
         *    <tr><td>Actionbar(操作条)</td><td>Address(地址输入框)</td><td>Attachment(附件框)</td><td>Button(按钮)</td></tr>
         *    <tr><td>Calendar(日期输入框)</td><td>Checkbox(多选按钮)</td><td>Combox(组合框)</td><td>Datagrid(数据网格)</td></tr>
         *    <tr><td>Div(容器)</td><td>Htmleditor(富文本编辑框)</td><td>Html(内置html)</td><td>Iframe(嵌入Iframe)</td></tr>
         *    <tr><td>Image(图片)</td><td>Label(文本)</td><td>Log(流程意见)</td><td>Monitor(流程监控)</td></tr>
         *    <tr><td>Number(数字输入框)</td><td>Office(office控件)</td><td>Opinion(意见框)</td><td>Org(人员组织选择)</td></tr>
         *    <tr><td>Radio(单选按钮)</td><td>Select(选择框)</td><td>Sidebar(侧边操作条)</td><td>Stat(统计组件)</td></tr>
         *    <tr><td>Subform(子表单)</td><td>Tab(分页)</td><td>Table(表格)</td><td>Textarea(多行输入)</td></tr>
         *    <tr><td>Textfield(文本输入框)</td><td>Tree(树状控件)</td><td>View(视图组件)</td><td>ViewSelector(视图选择组件)</td></tr>
         *    <tr><td>Documenteditor(公文编辑器)</td><td>ImageClipper(图片编辑器)</td><td></td><td></td></tr>
         * </table>
         * @method get
         * @static
         * @return {FormComponent} 请查看本文档的Classes导航下的FormComponents。
         * @param {String} name 字段标识
         * @o2syntax
         * var field = this.form.get(name);
         * @example
         * var field = this.form.get("subject");
         */
        "get": function(name){return (_form.all) ? _form.all[name] : null;},

        /**
         * 获取表单中可输入的字段元素对象。<br/>
         * <table>
         *    <tr><td>Address(地址输入框)</td><td>Attachment(附件框)</td><td>Calendar(日期输入框)</td><td>Checkbox(多选按钮)</td></tr>
         *    <tr><td>Combox(组合框)</td><td>Datagrid(数据网格)</td><td>Htmleditor(富文本编辑框)</td><td>Number(数字输入框)</td></tr>
         *    <tr><td>Org(人员组织选择)</td><td>Radio(单选按钮)</td><td>Select(选择框)</td><td>Textarea(多行输入)</td></tr>
         *    <tr><td>Textfield(文本输入框)</td><td></td><td></td><td></td></tr>
         * </table>
         * @method getField
         * @static
         * @return {FormComponent} 请查看本文档的Classes导航下的FormComponents。
         * @param {String} name 字段标识
         * @o2syntax
         * var field = this.form.getField(name);
         * @example
         * var field = this.form.getField("subject");
         */
        "getField": function(name){return _forms[name];},

        "getAction": function(){return _form.workAction},
        "getDesktop": function(){return _form.app.desktop},

        /**获取业务数据
         * @method getData
         * @static
         * @see module:data
         * @o2syntax
         *  var data = this.form.getData();
         * @return {Object} 返回表单绑定的业务数据。
         */
        "getData": function(){return new MWF.xScript.JSONData(_form.getData());},

        /**保存当前表单所绑定的业务数据。<br/>
         * this.form.save()会触发 beforeSave和afterSave事件，因此在beforeSave和afterSave中不允许使用本方法。
         * @method save
         * @static
         * @param {Function} [callback] - 保存后的回调
         * @param {Boolean} [silent] - 是否静默，否提示保存成功，默认为false
         * @o2syntax
         * this.form.save(callback, silent);
         * @example
         *  this.form.save(function(){
         *      //do someting
         *  }, true);
         */
        "save": function(callback, silent){_form.saveWork(callback, silent); },

        /**
         *关闭当前表单
         * @method close
         * @static
         * @example
         * this.form.close();
         */
        "close": function(){_form.closeWork();},

        /**
         *挂起当前待办
         * @method pauseTask
         * @static
         * @example
         * this.form.pauseTask();
         */
        "pauseTask": function(){_form.pauseTask();},

        /**
         *将待办从挂起状态恢复为正常状态
         * @method resumeTask
         * @static
         * @example
         * this.form.resumeTask();
         */
        "resumeTask": function(){_form.resumeTask();},

        /**本校验不包括校验意见，校验路由；通常用在弹出提交界面时候的校验
         * @summary 根据表单中所有组件的校验设置和“流转校验”脚本进行校验。
         * @method verify
         * @static
         * @o2syntax
         * this.form.verify()
         *  @example
         *  if( !this.form.verify() ){
         *      return false;
         *  }
         *  @return {Boolean} 是否通过校验
         */
        "verify": function(){
            return !(!_form.formCustomValidation("", "") || !_form.formValidation("", ""));
        },



    /**对当前表单打开的流程实例进行流转。<b>（仅流程表单中可用）</b><br/>
     * 可以通过this.workContext.getControl().allowProcessing来判断当前用户是否有权限进行流转。<br/>
     * this.form.process()会触发 beforeSave、afterSave、beforeProcess、afterProcess事件，因此在上述事件中不允许使用本方法。
     * @method process
     * @static
     * @param {Object} [option] - 流程的相关数据，如果不带此参数，则弹出路由选择和意见填写框<br/>
     * 格式如下：
     <pre><code class="language-js">
     {
            "routeName": "", //流转到下一步要选择的路由名称
            "opinion": "", //流转意见
            "callback": function(){} //流转完成后的回调方法
      }
     </code></pre>
     * @example
     //不带参数，弹出路由选择和意见填写框
     this.form.process();
     * @example
     //带参数，流转
     this.form.process({
            "routeName": "送审批",
            "opinion": "同意",
            "callback": function(json){
                this.form.notice("process success", "success");
            }.bind(this)
      });
     */
        "process": function(option){
            var op = _form.getOpinion();
            var mds = op.medias;
            if (option){
                _form.submitWork(option.routeName, option.opinion, mds, option.callback,
                    option.processor, null, option.appendTaskIdentityList, option.processorOrgList, option.callbackBeforeSave );
            }else{
                _form.processWork();
            }
        },

        /**对当前文档的待办重新设定处理人。<b>（仅流程表单中可用）</b><br/>
         * 可以通过this.workContext.getControl().allowReset来判断当前用户是否有权限重置处理人。<br/>
         * this.form.reset()会触发 beforeReset、afterReset事件，因此在上述事件中不允许使用本方法。
         * @method reset
         * @static
         * @param {Object} [option] - 进行重置处理人的相关参数，如果不带此参数，弹出重置处理人对话框<br/>
         * 格式如下：
         <pre><code class="language-js">
         {
            "names": "", //{Array|String} 要重置给哪些身份
            "opinion": "", //流转意见
            "success ": function(){}, //重置成功后的回调方法
            "failure ": function(){} //重置失败后的回调方法
        }
         </code></pre>
         * @example
         //不带参数，弹出重置处理人对话框
         this.form.reset();
         * @example
         //带参数，直接调用后台服务重置
         this.form.reset({
            "names": ["张三(综合部)"],
            "opinion": "授权处理",
            "success": function(json){
                this.form.notice("reset success", "success");
            }.bind(this),
            "failure": function(xhr, text, error){
                //xhr--HttpRequest请求对象
                //text--HttpResponse内容文本
                //error--错误信息
                this.form.notice("reset failure:"+error, "error");
            }.bind(this)
        });
         */
        "reset": function(option){
            if (!option){
                if (_form.businessData.control["allowReset"]) _form.resetWork();
            }else{
                _form.resetWorkToPeson(option.names, option.opinion, option.keep, option.success, option.failure);
            }
        },

        /**撤回文档操作，上一个处理人收回已经流转下去的文件。<b>（仅流程表单中可用）</b><br/>
         * 这个操作只允许上一个处理人在流转文件之后，下一个处理人未处理的时候执行。<br/>
         * 可以通过this.workContext.getControl().allowRetract来判断当前用户是否有权限撤回。<br/>
         * this.form.retract()会触发 beforeRetract、afterRetract事件，因此在上述事件中不允许使用本方法。
         * @method retract
         * @static
         * @param {Object} [option] - 进行撤回的相关参数，如果不提供option参数，则弹出撤回对话框。<br/>
         * 格式如下：
         <pre><code class="language-js">
         {
            "success ": function(){}, //撤回成功后的回调方法
            "failure ": function(){} //撤回失败后的回调方法
        }
         </code></pre>
         * @example
         //不带参数，则弹出撤回对话框
         this.form.retract();
         * @example
         //带参数，直接调用后台服务撤回
         this.form.retract({
            "success": function(json){
                this.form.notice("retract success", "success");
            }.bind(this),
            "failure": function(xhr, text, error){
                //xhr--HttpRequest请求对象
                //text--HttpResponse内容文本
                //error--错误信息
                this.form.notice("retract failure: "+error, "error");
            }.bind(this)
        });
         */
        "retract": function(option){
            if (!option){
                if (_form.businessData.control["allowRetract"]) _form.retractWork();
            }else{
                _form.doRetractWork(option.success, option.failure);
            }
        },

        /**在已拆分的工作上添加分支。<b>（仅流程表单中可用）</b><br/>
         * 可以通过this.workContext.getControl().allowAddSplit来判断当前用户是否有权限。<br/>
         * @method addSplit
         * @static
         * @param {Object} [option] - 添加分支的相关参数，如果不提供option参数，则弹出添加分支对话框。<br/>
         * 格式如下：
         <pre><code class="language-js">
         {
            "value" : [], //splitValueList 添加的拆分值，拆分值取决于流程拆分节点的设置
            "trimExist" : true, //排除已经存在的拆分值.
            "success ": function(){}, //执行成功后的回调方法
            "failure ": function(){} //执行失败后的回调方法
        }
         </code></pre>
         * @example
         //不带参数，则弹出添加分支对话框
         this.form.addSplit();
         * @example
         //带参数，直接添加分支
         this.form.addSplit({
            "value" : ["开发部@kfb@U"],
            "trimExist" : true,
            "success": function(json){
                this.form.notice("addSplit success", "success");
            }.bind(this),
            "failure": function(xhr, text, error){
                //xhr--HttpRequest请求对象
                //text--HttpResponse内容文本
                //error--错误信息
                this.form.notice("addSplit failure: "+error, "error");
            }.bind(this)
        });
         */
        "addSplit": function(option){
            if (!option){
                if (_form.businessData.control["allowAddSplit"]) _form.addSplit();
            }else{
                _form.addSplitWork(option.value, option.trimExist, option.success, option.failure);
            }
        },


        "rollback": function(option){
            if (!option){
                if (_form.businessData.control["allowRollback"]) _form.rollback();
            }else{
                _form.doRollbackActionInvoke(option.log, option.flow, option.success, option.failure);
            }
        },

        /**删除当前工作文档。<b>（仅流程表单中可用）</b><br/>
         * 可以通过this.workContext.getControl().allowDeleteWork来判断当前用户是否有权限删除文档。<br/>
         * @method deleteWork
         * @static
         * @param {Object} [option] - 删除相关参数，如果不提供option参数，则弹出删除对话框。<br/>
         * 格式如下：
         <pre><code class="language-js">
         {
            "success ": function(){}, //执行成功后的回调方法
            "failure ": function(){} //执行失败后的回调方法
        }
         </code></pre>
         * @example
         //不带参数，则弹出删除提示对话框
         this.form.deleteWork();
         * @example
         //带参数，直接调用服务删除
         this.form.deleteWork({
            "success": function(json){
                this.form.notice("deleteWork success", "success");
            }.bind(this),
            "failure": function(xhr, text, error){
                //xhr--HttpRequest请求对象
                //text--HttpResponse内容文本
                //error--错误信息
                this.form.notice("deleteWork failure: "+error, "error");
            }.bind(this)
        });
         */
        "deleteWork": function(option){
            if (!option){
                if (_form.businessData.control["allowDelete"]) _form.deleteWork();
            }else{
                _form.doDeleteWork(option.success, option.failure);
            }
        },

        /**弹出一个确认框，带确认和关闭按钮
         * @method confirm
         * @static
         * @param {String} type - 要显示的信息类型。可选值：success 成功，info :信息，error :错误， wran : 警告
         * @param {String} title - 确认框标题栏显示文本。
         * @param {String} text - 确认框的内容显示文本。
         * @param {Number} width - 确认框的宽度。
         * @param {String} height - 确认框的高度。
         * @param {Function} ok - 点击“确定”按钮后的回调函数。
         * @param {Function} cancel - 点击“取消”按钮后的回调函数。
         * @example
         this.form.confirm("wran", "删除确认", "您确定要删除吗？", 300, 100,function(){
            //执行删除代码
            this.close();
        }, function(){
            this.close();
        });
         */
        "confirm": function(type, title, text, width, height, ok, cancel, callback, mask, style){
            if ((arguments.length<=1) || o2.typeOf(arguments[1])==="string"){
                var p = MWF.getCenter({"x": width, "y": height});
                e = {"event": {"clientX": p.x,"x": p.x,"clientY": p.y,"y": p.y}};
                _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
            }else{
                e = (arguments.length>1) ? arguments[1] : null;
                title = (arguments.length>2) ? arguments[2] : null;
                text = (arguments.length>3) ? arguments[3] : null;
                width = (arguments.length>4) ? arguments[4] : null;
                height = (arguments.length>5) ? arguments[5] : null;
                ok = (arguments.length>6) ? arguments[6] : null;
                cancel = (arguments.length>7) ? arguments[7] : null;
                callback = (arguments.length>8) ? arguments[8] : null;
                mask = (arguments.length>9) ? arguments[9] : null;
                style = (arguments.length>10) ? arguments[10] : null;

                // var p = MWF.getCenter({"x": width, "y": height});
                // e = {"event": {"clientX": p.x,"x": p.x,"clientY": p.y,"y": p.y}};
                _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
            }
        },

        /**弹出一个带关闭按钮的信息框
         * @method alert
         * @static
         * @param {String} type - 要显示的信息类型。可选值：success 成功，info :信息，error :错误， wran : 警告
         * @param {String} title - 信息框标题栏显示文本。
         * @param {String} text - 信息框的内容显示文本。
         * @param {Number} width - 信息框宽度。
         * @param {String} height - 信息框的高度。
         * @example
         this.form.alert("wran", "必填提醒", "请填写标题！", 300, 100);
         */
        "alert": function(type, title, text, width, height){
            _form.alert(type, title, text, width, height);
        },

        /**弹出一个提示框
         * @method notice
         * @static
         * @param {String} content - 要显示的信息文本
         * @param {String} [type] - 要显示的信息类型。可选值：success 成功，info :信息，error :错误， wran : 警告
         * @param {Element} [target] - 信息框显示位置的参考DOM对象。
         * @param {Object} [where] - 信息框显示相对于target的x轴和y轴位置。<br/>
         * 如： {"x": "center", "y": "center"}<br/>
         x : <br/>
         　水平位置，可用“left”、“right”和“center”；可以用数组定义外部（outside）位置和内部（inside）位置，如：['right', 'inside']<br/>
         y :<br/>
         　垂直位置，可用“top”、“bottom”和“center”；可以用数组定义外部（outside）位置和内部（inside）位置，如：['top', 'outside']。
         * @param {Object} [offset] - 相对位置的偏移量，允许负值。如：{"x": 10, "y": -10}
         * @param {Object} [option] - 其他选项。如： { delayClose: 5000 } 在5秒后关闭
         * @example
         this.form.notice("this is my information", "info");
         */
        "notice": function(content, type, target, where, offset, option){
            _form.notice(content, type, target, where, offset, option);
        },

        /**给表单添加事件。
         * @method addEvent
         * @static
         * @param {String} type - 事件名称，参考本API Classer->FormComponents->Form的事件
         * @param {Function} event - 事件方法。
         * @example
         this.form.addEvent("load", function(){
            this.form.notice("表单载入完成", "success");
        }.bind(this));
         */
        "addEvent": function(type, event ){_form.addEvent(type, event );},

        /**用一个新的浏览器窗口来打开当前文档，用于打印。<b>（仅流程表单中可用）</b><br/>
         * 如不指定表单，则使用表单设计中指定的打印表单。<br/>
         * @method print
         * @static
         * @param {String} [application] - 指定表单所在的流程应用ID或名称。省略此参数表示当前应用。
         * @param {String} [form] - 指定表单ID或名称。
         * @example
         //在新窗口中使用当前表单中配置的打印表单打开当前文档
         this.form.print();
         * @example
         //在新窗口中使用“订单打印表单”表单打开当前文档
         this.form.print("订单打印表单");
         * @example
         //在新窗口中使用“订单管理”应用中的“订单打印表单”表单打开当前文档
         this.form.print("订单管理", "订单打印表单");
         */
        "print": function(application, form){
            if (arguments.length){
                var app = (arguments.length>1) ? arguments[0] : null;
                var formName = (arguments.length>1) ? arguments[1] : arguments[0];
                _form.printWork(app, formName);
            }else{
                _form.printWork();
            }
        },

        /**同print方法。<b>（仅流程表单中可用）</b><br/>
         * @method openWindow
         * @static
         * @see this.form.print()
         * @param {String} [application] - 指定表单所在的流程应用ID或名称。省略此参数表示当前应用。
         * @param {String} [form] - 指定表单ID或名称。
         * @example
         this.form.openWindow();
         */
        "openWindow": function(application, form){
            if (arguments.length){
                var app = (arguments.length>1) ? arguments[0] : null;
                var formName = (arguments.length>1) ? arguments[1] : arguments[0];
                _form.openWindow(formName, app);
            }else{
                _form.openWindow();
            }
        },

        /**　打开一个在流转或已完成的流程实例。<br/>
         * @method openWork
         * @static
         * @param {String} [workId] - 在流转的流程实例ID。workId和workCompletedId两个参数必须提供其中一个
         * @param {String} [workCompletedId] - 已完成的流程实例ID。
         * @param {String} [title] - 手机端打开时的窗口标题。
         * @example
         this.form.openWork(id, "", "work title");
         */
        "openWork": function(workId, workCompletedId, title, options){
            var op = options || {};
            op.workId = workId;
            op.workCompletedId = workCompletedId;
            op.docTitle = title;
            op.appId = "process.Work"+(op.workId || op.workCompletedId);
            return layout.desktop.openApplication(this.event, "process.Work", op);
        },

        /**　使用流程的jobId打开工作。<br/>
         * @method openJob
         * @static
         * @param {String} id - 流程的jobId，如果流程拆分后，有多个流程实例（workId会有多个），但jobId是唯一的。
         * @param {Boolean} [choice] - 如果有多个流程实例，是否弹出界面选择。如果传入false,则直接打开第一个工作。
         * @example
         this.form.openJob(jobId, true);
         */
        "openJob": function(id, choice, options){
            var workData = null;
            o2.Actions.get("x_processplatform_assemble_surface").listWorkByJob(id, function(json){
                if (json.data) workData = json.data;
            }.bind(this), null, false);

            if (workData){
                var len = workData.workList.length + workData.workCompletedList.length;
                if (len){
                    if (len>1 && choice){
                        var node = new Element("div", {"styles": {"padding": "20px", "width": "500px"}}).inject(_form.node);
                        workData.workList.each(function(work){
                            var workNode = new Element("div", {
                                "styles": {
                                    "background": "#ffffff",
                                    "border-radius": "10px",
                                    "clear": "both",
                                    "margin-bottom": "10px",
                                    "height": "40px",
                                    "padding": "10px 10px"
                                }
                            }).inject(node);
                            var html = "<div style='height: 40px; width: 40px; float: left; background: url(../x_component_process_Xform/$Form/default/icon/work.png) no-repeat center center'></div>" +
                                "<div style='height: 40px; width: 40px; float: right'><div class='MWFAction' style='height: 20px; width: 40px; margin-top: 10px; border: 1px solid #999999; border-radius: 5px;text-align: center; cursor: pointer'>"+o2.LP.widget.open+"</div></div>"+
                                "<div style='height: 20px; line-height: 20px; margin: 0px 40px'>"+work.title+"</div>" +
                                "<div style='margin: 0px 40px'><div style='color:#999999; float: left; margin-right: 10px'>"+work.activityName+"</div>" +
                                "<div style='color:#999999; float: left; margin-right: 10px'>"+work.activityArrivedTime+"</div>" +
                                "<div style='color:#999999; float: left; margin-right: 10px'>"+(work.manualTaskIdentityText || "")+"</div></div>";
                            workNode.set("html", html);
                            var action = workNode.getElement(".MWFAction");
                            action.store("work", work);
                            action.addEvent("click", function(e){
                                var work = e.target.retrieve("work");
                                if (work) this.openWork(work.id, null, work.title, options);
                                dlg.close();
                            }.bind(this));

                        }.bind(this));
                        workData.workCompletedList.each(function(work){
                            var workNode = new Element("div", {
                                "styles": {
                                    "background": "#ffffff",
                                    "border-radius": "10px",
                                    "clear": "both",
                                    "margin-bottom": "10px",
                                    "height": "40px",
                                    "padding": "10px 10px"
                                }
                            }).inject(node);
                            var html = "<div style='height: 40px; width: 40px; float: left; background: url(../x_component_process_Xform/$Form/default/icon/work.png) no-repeat center center'></div>" +
                                "<div style='height: 40px; width: 40px; float: right'><div class='MWFAction' style='height: 20px; width: 40px; margin-top: 10px; border: 1px solid #999999; border-radius: 5px;text-align: center; cursor: pointer'>"+o2.LP.widget.open+"</div></div>"+
                                "<div style='height: 20px; line-height: 20px; margin: 0px 40px'>"+work.title+"</div>" +
                                "<div style='margin: 0px 40px'><div style='color:#999999; float: left; margin-right: 10px'>"+o2.LP.widget.workcompleted+"</div>" +
                                "<div style='color:#999999; float: left; margin-right: 10px'>"+work.completedTime+"</div>";
                            workNode.set("html", html);
                            var action = workNode.getElement(".MWFAction");
                            action.store("work", work);
                            action.addEvent("click", function(e){
                                var work = e.target.retrieve("work");
                                if (work) this.openWork(null, work.id, work.title, options);
                                dlg.close();
                            }.bind(this));

                        }.bind(this));
                        var height = node.getSize().y+20;
                        if (height>600) height = 600;

                        var dlg = o2.DL.open({
                            "title": o2.LP.widget.choiceWork,
                            "style" : "user",
                            "isResize": false,
                            "content": node,
                            "buttonList": [
                                {
                                    "type" : "cancel",
                                    "text": o2.LP.widget.close,
                                    "action": function(){dlg.close();}
                                }
                            ]
                        });
                    }else{
                        if (workData.workList.length){
                            var work =  workData.workList[0];
                            return this.openWork(work.id, null, work.title, options);
                        }else{
                            var work =  workData.workCompletedList[0];
                            return this.openWork(null, work.id, work.title, options);
                        }
                    }
                }
            }
            // var op = options || {};
            // op.workId = id;
            // op.workCompletedId = completedId;
            // op.docTitle = title;
            // op.appId = "process.Work"+(op.workId || op.workCompletedId);
            // layout.desktop.openApplication(this.event, "process.Work", op);
        },

        /**　打开一个内容管理文档。<br/>
         * @method openDocument
         * @static
         * @param {String} id - 内容管理文档实例的ID。
         * @param {Boolean} [title] - 手机APP端打开时的窗口标题。
         * @param {Object} [options] - 其他参数，内容如下<br>
         * <pre><code class="language-js">{
         *   "readonly": true, //是否以只读方式打开，默认为true
         *   "forceFormId": "xxxxxx", //不管编辑还是阅读都用此表单id打开，优先使用。6.0版本之前使用 printFormId。
         *   "readFormId": "xxxxxx", //强制的阅读表单id，优先于表单的readFormId。6.0版本之前使用 formId。
         *   "editFormId": "xxxxxx", //强制的编辑表单id，优先于表单的formId。6.0版本之前使用 formEditId。
         *    "saveOnClose" : true, //关闭的时候是否自动保存
         *    "onPostPublish" : function( documentData ){ //发布前执行方法，但数据已经准备好，该事件在桌面模式打开有效
         *       //documentData 为文档数据
         *    },
         *    "onAfterPublish" : function( form, documentData ){ //发布后执行的方法，该事件在桌面模式打开有效
         *       //form为内容管理Form对象，documentData 为文档数据
         *    },
         *    "onPostDelete" : function(){ //删除文档后执行的方法，该事件在桌面模式打开有效
         *    }
         * }</code></pre>
         * @example
         this.form.openDocument(id, "document title");
         */
        "openDocument": function(id, title, options){
            var op = options || {};
            op.documentId = id;
            op.docTitle = title;
            layout.desktop.openApplication(this.event, "cms.Document", op);
        },

        /**打开一个门户页面。<br/>
         * @method openPortal
         * @static
         * @param {String} portal - 要打开的门户应用名称、别名或ID。
         * @param {String} [page] - 要打开的页面名称、别名或ID。如果忽略，则打开门户的默认首页
         * @param {Object} [par] - 打开页面可以传入参数。<br>在被打开的页面中，可以通过脚本this.page.parameters访问到此参数。
         * @example
         this.form.openPortal(id, "", {"type": "my type"});
         */
        "openPortal": function (portal, page, par) {
            var action = MWF.Actions.get("x_portal_assemble_surface");
            action.getApplication(portal, function (json) {
                if (json.data) {
                    if (page) {
                        action.getPageByName(page, json.data.id, function (pageJson) {
                            var pageId = (pageJson.data) ? pageJson.data.id : "";
                            layout.desktop.openApplication(null, "portal.Portal", {
                                "portalId": json.data.id,
                                "pageId": pageId,
                                "parameters": par,
                                "appId": (par && par.appId) || ("portal.Portal" + json.data.id + pageId)
                            })
                        });
                    } else {
                        layout.desktop.openApplication(null, "portal.Portal", {
                            "portalId": json.data.id,
                            "parameters": par,
                            "appId": (par && par.appId) || ("portal.Portal" + json.data.id)
                        })
                    }
                }

            });
        },


        /**打开一个内容管理栏目（应用）。<br/>
         * @method openCMS
         * @static
         * @param {String} name - 内容管理栏目的名称、别名或ID。
         * @example
         this.form.openCMS("通知公告");
         */
        "openCMS": function(name){
            var action = MWF.Actions.get("x_cms_assemble_control");
            action.getColumn(name, function(json){
                if (json.data){
                    layout.desktop.openApplication(null, "cms.Module", {
                        "columnId": json.data.id,
                        "appId": "cms.Module"+json.data.id
                    });
                }
            });
        },

        /**打开一个流程应用。<br/>
         * @method openProcess
         * @static
         * @param {String} name - 流程应用的名称、别名或ID。
         * @example
         this.form.openProcess("财务审批");
         */
        "openProcess": function(name){
            var action = MWF.Actions.get("x_processplatform_assemble_surface");
            action.getApplication(name, function(json){
                if (json.data){
                    layout.desktop.openApplication(null, "process.Application", {
                        "id": json.data.id,
                        "appId": "process.Application"+json.data.id
                    });
                }
            });
        },

        /**打开任意一个component应用。<br/>
         * @method openApplication
         * @static
         * @param {String} name - 要打开的component的名称。component对应的名称可以在“控制面板-系统设置-界面设置-模块部署”中找到（即“组件路径”）。
         * @param {Object} [options] - 打开的component的相关参数
         * @example
         //打开会议管理
         this.form.openApplication("Meeting");
         * @example
         //打开一个流转中的流程实例。与 this.form.openWork(id, "", "work title");效果相同
         this.form.openApplication("process.Work", {
            "workId": id,   //流程实例ID
            "width": "1200",    //宽度
            "height": "800",    //高度
            "docTitle": "work title",   //APP端窗口标题
            "appId": "process.Work"+id  //给新打开的component实例一个唯一名称
        });
         */
        "openApplication":function(name, options){
            layout.desktop.openApplication(null, name, options);
        },

        /**创建一条内容管理文档。
         * @method createDocument
         * @static
         * @param {(String|Object)} [columnOrOptions]
         * 如果不传参数，则弹出范围为平台所有栏目的选择界面。<br/>
         * 当使用String时为内容管理应用（栏目）的名称、别名或ID。<br/>
         * 当使用Object时，本参数后面的参数省略，传入如下格式的内容:
         * <pre><code class="language-js">{
         *   "column" : column, //（string）可选，内容管理应用（栏目）的名称、别名或ID
         *   "category" : category, //（string）可选，要创建的文档所属的分类的名称、别名或ID
         *   "data" : data, //（json object）可选，创建文档时默认的业务数据
         *   "identity" : identity, //（string）可选，创建文档所使用的身份。如果此参数为空，且当前人有多个身份的情况下，会弹出身份选择对话框；否则使用默认身份。
         *   "callback" : callback, //（funcation）可选，文档创建后的回调函数。
         *   "target" : target, //（boolean）可选，为true时，在当前页面打开创建的文档；否则打开新窗口。默认false。
         *   "latest" : latest, //（boolean）可选，为true时，如果当前用户已经创建了此分类的文档，并且没有发布过，直接调用此文档为新文档；否则创建一个新文档。默认true。
         *   "selectColumnEnable" : selectColumnEnable, //（boolean）可选，是否可以选择应用和分类进行创建文档。有category参数时为默认false,否则默认为true。
         *   "ignoreTitle" : ignoreTitle //（boolean）可选，值为false时，创建的时候需要强制填写标题，默认为false。
         * }</code></pre>
         * @param {String} [category] - 要创建的文档所属的分类的名称、别名或ID
         * @param {Object} [data] - 创建文档时默认的业务数据
         * @param {String} [identity] - 可选，创建文档所使用的身份。如果此参数为空，且当前人有多个身份的情况下，会弹出身份选择对话框；否则使用默认身份。
         * @param {Function} [callback] - 文档创建后的回调函数
         * @param {Boolean} [target] - 为true时，在当前页面打开创建的文档；否则打开新窗口。默认false
         * @param {Boolean} [latest] - 为true时，如果当前用户已经创建了此分类的文档，并且没有发布过，直接调用此文档为新文档；否则创建一个新文档。默认true。
         * @param {Boolean} [selectColumnEnable] - 为true时，如果当前用户已经创建了此分类的文档，并且没有发布过，直接调用此文档为新文档；否则创建一个新文档。默认true。
         * @param {Boolean} [ignoreTitle] - 值为false时，创建的时候需要强制填写标题，默认为false。
         * @example
         //启动一个通知公告
         this.form.createDocument("", "通知公告");
         * @example
         //启动一个通知公告，标题为：关于XX的通知，启动后提示
         this.form.createDocument("", "通知公告", {"subject": "关于XX的通知"}, function(json){
            this.form.notice("创建成功!", "success");
        }.bind(this));
         * @example
         //启动一个通知公告，标题为：关于XX的通知，启动后提示
         this.form.createDocument({
            category : "通知公告",
            data : {"subject": "关于XX的通知"},
            callback : function(json){
                this.form.notice("创建成功!", "success");
            }.bind(this)
         });
         */
        "createDocument": function (columnOrOptions, category, data, identity, callback, target, latest, selectColumnEnable, ignoreTitle) {
            var column = columnOrOptions;
            var onAfterPublish, onPostPublish;
            if (typeOf(columnOrOptions) == "object") {
                column = columnOrOptions.column;
                category = columnOrOptions.category;
                data = columnOrOptions.data;
                identity = columnOrOptions.identity;
                callback = columnOrOptions.callback;
                target = columnOrOptions.target;
                latest = columnOrOptions.latest;
                selectColumnEnable = columnOrOptions.selectColumnEnable;
                ignoreTitle = columnOrOptions.ignoreTitle;
                onAfterPublish = columnOrOptions.onAfterPublish;
                onPostPublish = columnOrOptions.onPostPublish;
            }
            if (target) {
                if (layout.app && layout.app.inBrowser) {
                    layout.app.content.empty();
                    layout.app = null;
                }
            }

            MWF.xDesktop.requireApp("cms.Index", "Newer", function () {
                var starter = new MWF.xApplication.cms.Index.Newer(null, null, _form.app, null, {
                    "documentData": data,
                    "identity": identity,

                    "ignoreTitle": ignoreTitle === true,
                    "ignoreDrafted": latest === false,
                    "selectColumnEnable": !category || selectColumnEnable === true,
                    "restrictToColumn": !!category && selectColumnEnable !== true,

                    "categoryFlag": category, //category id or name
                    "columnFlag": column, //column id or name,
                    "onStarted": function (documentId, data) {
                        if (callback) callback();
                    },
                    "onPostPublish": function () {
                        if(onPostPublish)onPostPublish();
                    },
                    "onAfterPublish": function () {
                        if(onAfterPublish)onAfterPublish();
                    }
                });
                starter.load();
            })
        },

        /**启动一个流程实例。<br/>
         * @method startProcess
         * @static
         * @param {String} app  - 流程应用的名称、别名或ID。
         * @param {String} process  - 要启动的流程的名称、别名或ID。
         * @param {Object} [data]   - 流程启动时默认的业务数据。
         * @param {String} [identity]  - 流程启动所使用的身份。如果此参数为空，且当前人有多个身份的情况下，会弹出身份选择对话框；否则使用默认身份。
         * @param {Function} [callback]  - 流程启动后的回调函数。
         * @param {Boolean} [target]  - 为true时，在当前页面打开启动的流程实例；否则打开新窗口。默认false。
         * @param {Boolean} [latest]  - 为true时，如果当前用户已经创建了此流程的实例，并且没有流转过，直接调用此实例为新流程实例；否则创建一个新实例。默认false。
         * @example
         //启动一个发文管理实例
         this.form.startProcess("公文管理", "发文管理");
         * @example
         //启动一个发文管理实例，标题为：my file title，启动后提示
         this.form.startProcess("公文管理", "发文管理", {"title": "my file title"}, function(json){
            this.form.notice("create file success!", "success");
        });
         */
        "startProcess": function(app, process, data, identity, callback, target, latest){
            if (arguments.length>2){
                for (var i=2; i<arguments.length; i++){
                    if (typeOf(arguments[i])=="boolean"){
                        target = arguments[i];
                        break;
                    }
                }
            }

            if (target){
                if (layout.app && layout.app.inBrowser){
                    //layout.app.content.empty();
                    layout.app.$openWithSelf = true;
                }
            }
            var action = MWF.Actions.get("x_processplatform_assemble_surface").getProcessByName(process, app, function(json){
                if (json.data){
                    MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function(){
                        var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(json.data, _form.app, {
                            "workData": data,
                            "identity": identity,
                            "latest": latest,
                            "onStarted": function(data, title, processName){
                                if (data.work){
                                    var work = data.work;
                                    var options = {"draft": work, "appId": "process.Work"+(new o2.widget.UUID).toString(), "desktopReload": false};
                                    layout.desktop.openApplication(null, "process.Work", options);
                                }else{
                                    var currentTask = [];
                                    data.each(function(work){
                                        if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
                                    }.bind(this));

                                    if (currentTask.length==1){
                                        var options = {"workId": currentTask[0], "appId": currentTask[0]};
                                        layout.desktop.openApplication(null, "process.Work", options);
                                    }else{}
                                }

                                if (callback) callback(data);
                            }.bind(this)
                        });
                        starter.load();
                    }.bind(this));
                }
            });
        }
    };

    Object.defineProperty(this.form, "readonly", {
        get: function(){ return  _form.options.readonly; }
    });

    /**
     * 在用户提交的时候，选择的路由。仅在表单的“校验意见”和“校验路由”脚本中可用。只读。<b>（仅流程表单中可用）</b>。
     * @member currentRouteName
     * @memberOf module:form
     * @static
     * @return {String} 用户选择的路由。
     * @o2syntax
     * var currentRouteName = this.form.currentRouteName;
     */
    //this.form.currentRouteName = _form.json.currentRouteName;

    /**
     * 在用户提交的时候，输入的意见。仅在表单的“校验意见”和“校验路由”脚本中可用。只读。<b>（仅流程表单中可用）</b>。
     * @member opinion
     * @memberOf module:form
     * @static
     * @return {String} 用户填写的意见.
     * @o2syntax
     * var opinion = this.form.opinion;
     */
    //this.form.opinion = _form.json.opinion;

    /**
     * 在提交的时候，用户的手写意见以及录音意见，仅在表单的“校验意见”和“校验路由”脚本中可用。只读。<b>（仅流程表单中可用）</b>。
     * @member medias
     * @memberOf module:form
     * @static
     * @return {Blob[]} 手写意见以及录音意见数组。手写意见和录音意见都是 HTML5的blob类型文件。
     * @o2syntax
     * var medias = this.form.medias;
     */
    this.form.medias = [];

    this.target = ev.target;
    this.event = ev.event;
    this.status = ev.status;


    this.session = layout.desktop.session;
    this.Actions = o2.Actions;

    this.query = function(option){
        // options = {
        //      "name": "statementName",
        //      "data": "json data",
        //      "firstResult": 1,
        //      "maxResults": 100,
        //      "success": function(){},
        //      "error": function(){},
        //      "async": true or false, default is true
        // }
        if (option){
            var json = (option.data) || {};
            if (option.firstResult) json.firstResult = option.firstResult.toInt();
            if (option.maxResults) json.maxResults = option.maxResults.toInt();
            o2.Actions.get("x_query_assemble_surface").executeStatement(option.name, json, success, error, options.async);
        }
    }
    this.Table = MWF.xScript.createTable();
};

MWF.xScript.createTable = function(){
    return function(name){
        this.name = name;
        this.action = o2.Actions.get("x_query_assemble_surface");

        this.listRowNext = function(id, count, success, error, async){
            this.action.listRowNext(this.name, id, count, success, error, async);
        };
        this.listRowPrev = function(id, count, success, error, async){
            this.action.listRowPrev(this.name, id, count, success, error, async);
        };
        this.listRowPrev = function(id, count, success, error, async){
            this.action.listRowPrev(this.name, id, count, success, error, async);
        };
        this.listRowSelectWhere = function(where, success, error, async){
            this.action.listRowSelectWhere(this.name, where, success, error, async);
        };
        this.listRowCountWhere = function(where, success, error, async){
            this.action.listRowCountWhere(this.name, where, success, error, async);
        };
        this.deleteRow = function(id, success, error, async){
            this.action.deleteRow(this.name, id, success, error, async);
        };
        this.deleteAllRow = function(success, error, async){
            this.action.deleteAllRow(this.name, success, error, async);
        };
        this.getRow = function(id, success, error, async){
            this.action.getRow(this.name, id, success, error, async);
        };
        this.insertRow = function(data, success, error, async){
            this.action.insertRow(this.name, data, success, error, async);
        };
        this.updateRow = function(id, data, success, error, async){
            this.action.updateRow(this.name, id, data, success, error, async);
        };
    }
};


MWF.xScript.JSONData = function(data, callback, key, parent, _form){
    var getter = function(data, callback, k, _self){
        return function(){return (["array","object"].indexOf(typeOf(data[k]))===-1) ? data[k] : new MWF.xScript.JSONData(data[k], callback, k, _self, _form);};
    };
    var setter = function(data, callback, k, _self){
        return function(v){

            data[k] = v;
            //debugger;
            //this.add(k, v, true);
            if (callback) callback(data, k, _self);
        }
    };
    var define = function(){
        var o = {};
        for (var k in data) o[k] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, k, this]),"set": setter.apply(this, [data, callback, k, this])};
        o["length"] = {"get": function(){return Object.keys(data).length;}};
        o["some"] = {"get": function(){return data.some;}};
        MWF.defineProperties(this, o);

        var methods = {
            "getKey": {"value": function(){ return key; }},
            "getParent": {"value": function(){ return parent; }},
            "toString": {"value": function() { return data.toString();}},
            "setSection": {"value": function(newKey, newValue){
                    this.add(newKey, newValue, true);
                    try {
                        var path = [this.getKey()];
                        p = this.getParent();
                        while (p && p.getKey()){
                            path.unshift(p.getKey());
                            p = p.getParent();
                        }
                        if (path.length) _form.sectionListObj[path.join(".")] = newKey;
                    }catch(e){

                    }
                }},

            "add": {"value": function(newKey, newValue, overwrite){
                var flag = true;
                var type = typeOf(data);
                if (type==="array"){
                    if (arguments.length<2){
                        data.push(newKey);
                        newValue = newKey;
                        newKey = data.length-1;
                    }else{
                        if (!newKey && newKey!==0){
                            data.push(newValue);
                            newKey = data.length-1;
                        }else{
                            flag = false;
                        }
                    }
                    if (flag){
                        var o = {};
                        o[newKey] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
                        MWF.defineProperties(this, o);
                    }
                    this[newKey] = newValue;
                }else if (type==="object"){
                    if (!this.hasOwnProperty(newKey)){
                        data[newKey] = newValue;

                        if (flag){
                            var o = {};
                            o[newKey] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
                            MWF.defineProperties(this, o);
                        }
                        this[newKey] = newValue;
                    }else{
                        if (overwrite) this[newKey] = newValue;
                    }
                }

                return this[newKey];
            }},
            "del": {"value": function(delKey){
                if (!this.hasOwnProperty(delKey)) return null;
                // delete data[delKey];
                // delete this[delKey];
                    data[delKey] = "";
                    this[delKey] = "";
                return this;
            }}
        };
        MWF.defineProperties(this, methods);




        //this.getKey = function(){ return key; };
        //this.getParent = function(){ return parent; };
        //this.toString = function() { return data.toString();};
        //this.add = function(newKey, newValue, overwrite){
        //    var flag = true;
        //    var type = typeOf(data);
        //    if (!this.hasOwnProperty(newKey)){
        //        if (type=="array"){
        //            if (arguments.length<2){
        //                data.push(newKey);
        //                newValue = newKey;
        //                newKey = data.length-1;
        //            }else{
        //                debugger;
        //                if (!newKey && newKey!=0){
        //                    data.push(newValue);
        //                    newKey = data.length-1;
        //                }else{
        //                    flag == false;
        //                }
        //            }
        //        }else{
        //            data[newKey] = newValue;
        //        }
        //        //var valueType = typeOf(newValue);
        //        //var newValueData = newValue;
        //        //if (valueType=="object" || valueType=="array") newValueData = new MWF.xScript.JSONData(newValue, callback, newKey, this);
        //        //if (valueType=="null") newValueData = new MWF.xScript.JSONData({}, callback, newKey, this);
        //        if (flag){
        //            var o = {};
        //            o[newKey] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
        //            MWF.defineProperties(this, o);
        //        }
        //        this[newKey] = newValue;
        //    }else{
        //        if (overwrite) this[newKey] = newValue;
        //    }
        //
        //    //var valueType = typeOf(newValue);
        //    //var newValueData = newValue;
        //    //if (valueType=="object" || valueType=="array") newValueData = new MWF.xScript.JSONData(newValue, callback, newKey, this);
        //    //if (valueType=="null") newValueData = new MWF.xScript.JSONData({}, callback, newKey, this);
        //    //
        //    //this[newKey] = newValueData;
        //
        //    return this[newKey];
        //};
        //this.del = function(delKey){
        //    if (!this.hasOwnProperty(delKey)) return null;
        //    delete data[newKey];
        //    delete this[newKey];
        //    return this;
        //};
    };
    var type = typeOf(data);
    if (type==="object" || type==="array") define.apply(this);
};

//MWF.xScript.createDict = function(application){
//    return function(name){
//        var applicationId = application;
//        this.name = name;
//        //MWF.require("MWF.xScript.Actions.DictActions", null, false);
//        var action = MWF.Actions.get("x_processplatform_assemble_surface");
//
//        this.get = function(path, success, failure){
//            debugger;
//            var value = null;
//            if (path){
//                var arr = path.split(/\./g);
//                var ar = arr.map(function(v){
//                    return encodeURIComponent(v);
//                });
//                //var p = path.replace(/\./g, "/");
//                var p = ar.join("/");
//                action.getDictData(encodeURIComponent(this.name), applicationId, p, function(json){
//                    value = json.data;
//                    if (success) success(json.data);
//                }, function(xhr, text, error){
//                    if (failure) failure(xhr, text, error);
//                }, false);
//            }else{
//                action.getDictRoot(encodeURIComponent(this.name), applicationId, function(json){
//                    value = json.data;
//                    if (success) success(json.data);
//                }, function(xhr, text, error){
//                    if (failure) failure(xhr, text, error);
//                }, false);
//            }
//
//            return value;
//        };
//
//        this.set = function(path, value, success, failure){
//            var p = path.replace(/\./g, "/");
//            action.setDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
//                if (success) success(json.data);
//            }, function(xhr, text, error){
//                if (failure) failure(xhr, text, error);
//            });
//        };
//        this.add = function(path, value, success, failure){
//            var p = path.replace(/\./g, "/");
//            action.addDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
//                if (success) success(json.data);
//            }, function(xhr, text, error){
//                if (failure) failure(xhr, text, error);
//            });
//        };
//        this["delete"] = function(path, success, failure){
//            var p = path.replace(/\./g, "/");
//            action.deleteDictData(encodeURIComponent(this.name), applicationId, p, function(json){
//                if (success) success(json.data);
//            }, function(xhr, text, error){
//                if (failure) failure(xhr, text, error);
//            });
//        };
//        this.destory = this["delete"];
//    }
//};
// var dictLoaded = {};
if( !MWF.xScript.dictLoaded )MWF.xScript.dictLoaded = {};

MWF.xScript.addDictToCache = function ( options, path, json ) {
    if( !path )path = "root";
    if( path.indexOf("root") !== 0 )path = "root." + path ;

    var type = options.appType || "process";
    var enableAnonymous = options.enableAnonymous || false;

    var appFlagList = [];
    if( options.application )appFlagList.push( options.application );
    if( options.appId )appFlagList.push( options.appId );
    if( options.appName )appFlagList.push( options.appName );
    if( options.appAlias )appFlagList.push( options.appAlias );

    var dictFlagList = [];
    if( options.id )dictFlagList.push( options.id );
    if( options.name )dictFlagList.push( options.name );
    if( options.alias )dictFlagList.push( options.alias );

    var cache = {};
    cache[path] = json;

    for( var i=0; i<appFlagList.length; i++ ){
        for( var j=0; j<dictFlagList.length; j++ ){
            var k = dictFlagList[j] + type + appFlagList[i] + enableAnonymous;
            if( !MWF.xScript.dictLoaded[k] ){
                MWF.xScript.dictLoaded[k] = cache; //指向同一个对象
                // MWF.xScript.dictLoaded[k][path] = json; //指向不同的对象
            }else if( i===0 && j===0 ){
                MWF.xScript.setDictToCache( k, path ,json );
                var arr = path.split(/\./g);
                var p;
                var cache = MWF.xScript.dictLoaded[k];
                for( var l=0 ; l<arr.length; l++ ){
                    p = l === 0 ? arr[0] : ( p + "." + arr[l] );
                    if( cache[ p ] )break;
                }
                if( p ){
                    var mathP = p+".";
                    Object.keys( cache ).each( function( path, idx){
                        if( path.indexOf( mathP ) === 0 )delete cache[path];
                    })
                }
            }
        }
    }
};

MWF.xScript.getMatchedDict = function(key, path){
    if( !path )path = "root";
    if( path.indexOf("root") !== 0 )path = "root." + path ;

    var arr = path.split(/\./g);
    if( MWF.xScript.dictLoaded[key] ){
        var dicts = MWF.xScript.dictLoaded[key];
        var list = Array.clone(arr);
        var p;
        var dict;
        for( var i=0 ; i<arr.length; i++ ){
            p = i === 0 ? arr[0] : ( p + "." + arr[i] );
            list.shift();
            if( dicts[ p ] ){
                dict = dicts[ p ];
                break;
            }
        }
        return {
            dict : dict,
            unmatchedPathList : list
        }
    }
    return {
        dict : null,
        unmatchedPathList : list
    }
};

MWF.xScript.insertDictToCache = function(key, path, json){
    if( MWF.xScript.dictLoaded[key] ){
        var matchedDict = MWF.xScript.getMatchedDict( key, path );
        var dict = matchedDict.dict;
        var list = matchedDict.unmatchedPathList;
        if( !dict ){
            MWF.xScript.dictLoaded[key][path] = json;
        }else{
            for( var j=0; j<list.length-1; j++ ){
                if( !dict[ list[j] ] ){
                    dict[ list[j] ] = {};
                }
                dict = dict[ list[j] ];
            }
            var lastPath = list[list.length-1];
            if( !dict[lastPath] ){
                dict[lastPath] = json;
            }else if( typeOf( dict[lastPath] ) === "array" ){
                dict[lastPath].push( json );
            }
        }
    }else{
        MWF.xScript.dictLoaded[key] = {};
        MWF.xScript.dictLoaded[key][path] = json;
    }
};


MWF.xScript.setDictToCache = function(key, path, json){
    if( MWF.xScript.dictLoaded[key] ){
        var matchedDict = MWF.xScript.getMatchedDict( key, path );
        var dict = matchedDict.dict;
        var list = matchedDict.unmatchedPathList;
        if( !dict ){
            MWF.xScript.dictLoaded[key][path] = json;
        }else{
            for( var j=0; j<list.length-1; j++ ){
                if( !dict[ list[j] ] ){
                    dict[ list[j] ] = {};
                }
                dict = dict[ list[j] ];
            }
            dict[list[list.length-1]] = json;
        }
    }else{
        MWF.xScript.dictLoaded[key] = {};
        MWF.xScript.dictLoaded[key][path] = json;
    }
};

MWF.xScript.getDictFromCache = function( key, path ){
    var matchedDict = MWF.xScript.getMatchedDict( key, path );
    var dict = matchedDict.dict;
    var list = matchedDict.unmatchedPathList;
    if( dict ){
        for( var j=0; j<list.length; j++ ){
            dict = dict[ list[j] ];
            if( !dict )return null;
        }
        return dict;
    }
    return null;
};

MWF.xScript.deleteDictToCache = function(key, path){
    var matchedDict = MWF.xScript.getMatchedDict( key, path );
    var dict = matchedDict.dict;
    var list = matchedDict.unmatchedPathList;

    if( dict){
        for( var j=0; j<list.length-1; j++ ){
            dict = dict[ list[j] ];
            if( !dict )return;
        }
        delete dict[list[list.length-1]];
    }
};


MWF.xScript.createDict = function(application){
    //optionsOrName : {
    //  type : "", //默认为process, 可以为  process  cms
    //  application : "", //流程/CMS的名称/别名/id, 默认为当前应用
    //  name : "", // 数据字典名称/别名/id
    //  enableAnonymous : false //允许在未登录的情况下读取CMS的数据字典
    //}
    //或者name: "" // 数据字典名称/别名/id
    return function(optionsOrName){
        var options = optionsOrName;
        if( typeOf( options ) == "string" ){
            options = { name : options };
        }
        var name = this.name = options.name;
        var type = ( options.type && options.application ) ?  options.type : "process";
        var applicationId = options.application || application;
        var enableAnonymous = options.enableAnonymous || false;

        var opt = {
            "appType" : type,
            "name" : name,
            "appId" : applicationId,
            "enableAnonymous" : enableAnonymous
        };

        var key = name+type+applicationId+enableAnonymous;
        // if (!dictLoaded[key]) dictLoaded[key] = {};
        // this.dictData = dictLoaded[key];

        //MWF.require("MWF.xScript.Actions.DictActions", null, false);
        if( type == "cms" ){
            var action = MWF.Actions.get("x_cms_assemble_control");
        }else{
            var action = MWF.Actions.get("x_processplatform_assemble_surface");
        }

        var encodePath = function( path ){
            var arr = path.split(/\./g);
            var ar = arr.map(function(v){
                return encodeURIComponent(v);
            });
            return ar.join("/");
        };

        this.get = function(path, success, failure, async, refresh){
            var value = null;

            if (success===true) async=true;
            if (failure===true) async=true;

            if (!refresh ){
                var data = MWF.xScript.getDictFromCache( key, path );
                if( data ){
                    if (success && o2.typeOf(success)=="function") success( data );
                    if( !!async ){
                        return Promise.resolve( data );
                    }else{
                        return data;
                    }
                }
            }

            // var cb = function(json){
            //     value = json.data;
            //     MWF.xScript.addDictToCache(opt, path, value);
            //     if (success && o2.typeOf(success)=="function") value = success(json.data);
            //     return value;
            // }.ag().catch(function(xhr, text, error){ if (failure && o2.typeOf(failure)=="function") return failure(xhr, text, error); });

            var cb = function(json){
                value = json.data;
                MWF.xScript.addDictToCache(opt, path, value);
                if (success && o2.typeOf(success)=="function") value = success(json.data);
                return value;
            };

            var promise;
            if (path){
                var p = encodePath( path );
                //var p = path.replace(/\./g, "/");
                promise = action[ ( (enableAnonymous && type == "cms") ? "getDictDataAnonymous" : "getDictData" ) ](encodeURIComponent(this.name), applicationId, p, cb, null, !!async, false);
            }else{
                promise = action[ ( (enableAnonymous && type == "cms") ? "getDictRootAnonymous" : "getDictRoot" ) ](this.name, applicationId, cb, null, !!async, false);
            }
            return (!!async) ? promise : value;

            // if (path){
            //     var p = encodePath( path );
            //     //var p = path.replace(/\./g, "/");
            //     action[ ( (enableAnonymous && type == "cms") ? "getDictDataAnonymous" : "getDictData" ) ](encodeURIComponent(this.name), applicationId, p, function(json){
            //         value = json.data;
            //         // this.dictData[path] = value;
            //         MWF.xScript.addDictToCache(opt, path, value);
            //         if (success) success(json.data);
            //     }.bind(this), function(xhr, text, error){
            //         if (failure) failure(xhr, text, error);
            //     }, !!async);
            // }else{
            //     action[ ( (enableAnonymous && type == "cms") ? "getDictRootAnonymous" : "getDictRoot" ) ](this.name, applicationId, function(json){
            //         value = json.data;
            //         // this.dictData["root"] = value;
            //         MWF.xScript.addDictToCache(opt, path, value);
            //         if (success) success(json.data);
            //     }.bind(this), function(xhr, text, error){
            //         if (failure) failure(xhr, text, error);
            //     }, !!async);
            // }

            //return value;
        };

        this.set = function(path, value, success, failure){
            var p = encodePath( path );
            //var p = path.replace(/\./g, "/");
            return action.setDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
                MWF.xScript.setDictToCache(key, path, value);
                if (success) return success(json.data);
            }, function(xhr, text, error){
                if (failure) return failure(xhr, text, error);
            }, false, false);
        };
        this.add = function(path, value, success, failure){
            var p = encodePath( path );
            //var p = path.replace(/\./g, "/");
            return action.addDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
                MWF.xScript.insertDictToCache(key, path, value);
                if (success) return success(json.data);
            }, function(xhr, text, error){
                if (failure) return failure(xhr, text, error);
            }, false, false);
        };
        this["delete"] = function(path, success, failure){
            var p = encodePath( path );
            //var p = path.replace(/\./g, "/");
            return action.deleteDictData(encodeURIComponent(this.name), applicationId, p, function(json){
                MWF.xScript.deleteDictToCache(key, path);
                if (success) return success(json.data);
            }, function(xhr, text, error){
                if (failure) return failure(xhr, text, error);
            }, false, false);
        };
        this.destory = this["delete"];
    }
};

