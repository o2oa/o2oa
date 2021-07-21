/**
 * data对象是流程平台中，流程实例的业务数据；以及内容管理平台中，文档实例的业务数据。<br/>
 * 这些数据一般情况下是通过您创建的表单收集而来的，也可以通过脚本进行创建和增删改查操作。<br/>
 * data对象基本上是一个JSON对象，您可以用访问JSON对象的方法访问data对象的所有数据，但增加和删除数据时略有不同。
 * @module server.data
 * @o2category server
 * @o2ordernumber 10
 * @example
 * //您可以在表单或流程的各个嵌入脚本中，通过this来获取当前实例的业务数据，如下：
 * var data = this.data;
 * @borrows module:data#[property] as [property]
 */


/**
 * 您可以通过workContext获取和流程相关的流程实例对象数据。
 * @module server.workContext
 * @o2category server
 * @o2range {Process}
 * @o2ordernumber 20
 * @o2syntax
 * //您可以在表单或流程的各个嵌入脚本中，通过this来获取当前流程实例数据，如下：
 * var context = this.workContext;
 * @borrows module:workContext.getWork as getWork
 * @borrows module:workContext.getActivity as getActivity
 * @borrows module:workContext.getTask as getTask
 * @borrows module:workContext.getTaskList as getTaskList
 * @borrows module:workContext.getTaskListByJob as getTaskListByJob
 * @borrows module:workContext.getTaskCompletedList as getTaskCompletedList
 * @borrows module:workContext.getTaskCompletedListByJob as getTaskCompletedListByJob
 * @borrows module:workContext.getReadList as getReadList
 * @borrows module:workContext.getReadListByJob as getReadListByJob
 * @borrows module:workContext.getReadCompletedList as getReadCompletedList
 * @borrows module:workContext.getReadCompletedListByJob as getReadCompletedListByJob
 * @borrows module:workContext.getControl as getControl
 * @borrows module:workContext.getWorkLogList as getWorkLogList
 * @borrows module:workContext.getRecordList as getRecordList
 * @borrows module:workContext.getAttachmentList as getAttachmentList
 * @borrows module:workContext.getRouteList as getRouteList
 */


/**
 * 本文档说明如何在后台脚本中使用Actions调用平台的RESTful服务。<br/>
 * 通过访问以下地址来查询服务列表：http://server:20030/x_program_center/jest/list.html
 * @module server.Actions
 * @o2category server
 * @o2ordernumber 130
 * @o2syntax
 * //获取Actions
 * this.Actions
 * @borrows module:Actions.getHost as getHost
 * @borrows module:Actions#load as load
 */