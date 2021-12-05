/**
 * 在流程事件、流程路由事件、流程活动事件中通过this.data获取流程实例的业务数据。（内容管理无后台脚本）。<br/>
 * 这些数据一般情况下是通过您创建的表单收集而来的，也可以通过脚本进行创建和增删改查操作。<br/>
 * data对象基本上是一个JSON对象，您可以用访问JSON对象的方法访问data对象的所有数据。
 * @module server.data
 * @o2category server
 * @o2ordernumber 15
 * @example
 * //您可以在表单或流程的各个嵌入脚本中，通过this来获取当前实例的业务数据，如下：
 * var data = this.data;
 * @borrows module:data#[property] as [property]
 */


/**
 * 您可以在流程事件、流程路由事件、流程活动事件中通过workContext获取和流程相关的流程实例对象数据。
 * @module server.workContext
 * @o2category server
 * @o2range {Process}
 * @o2ordernumber 20
 * @o2syntax
 * //您可以在表单或流程的各个嵌入脚本中，通过this来获取当前流程实例数据，如下：
 * var context = this.workContext;
 * @borrows module:workContext.getActivity as getActivity
 * @borrows module:workContext.getControl as getControl
 * @borrows module:workContext.getWorkLogList as getWorkLogList
 * @borrows module:workContext.getRecordList as getRecordList
 * @borrows module:workContext.getAttachmentList as getAttachmentList
 * @borrows module:workContext.getRouteList as getRouteList
 */
/**
 * 获取当前流程实例对象：work对象或workCompleted对象。
 * @method getWork
 * @static
 * @return {(Work|WorkCompleted)} 流程实例对象；如果流程已结束，返回已结束的流程实例对象。
 * @o2ActionOut x_processplatform_assemble_surface.WorkAction.manageGet|example=Work|ignoreNoDescr=true|ignoreProps=[properties]|Work对象:
 * @o2ActionOut x_processplatform_assemble_surface.WorkCompletedAction.get|example=WorkCompleted|ignoreProps=[properties]|WorkCompleted对象:
 * @o2syntax
 * var work = this.workContext.getWork();
 */
/**
 * 当前流程实例正在流转中，并且当前用户有待办，则返回当前用户的待办对象，否则返回null。
 * @summary 获取当前流程与当前用户相关的待办对象：task对象。
 * @o2ActionOut x_processplatform_assemble_surface.TaskAction.get|example=Task|Task对象:
 * @method getTask
 * @static
 * @return {(Task|Null)} 当前用户的待办任务对象：task。当前用户没有对此流程实例的待办时，或流程实例已经流转结束，返回null。
 * @o2syntax
 * var task = this.workContext.getTask();
 */
/**
 * 获取当前流程实例的所有待办对象。如果流程实例已流转完成，则返回一个空数组。
 * @method getTaskList
 * @methodOf module:server.workContext
 * @o2ActionOut x_processplatform_assemble_surface.TaskAction.listWithWork|example=Task
 * @static
 * @return {(Task[])} 待办任务列表.
 * @o2syntax
 * var taskList = this.workContext.getTaskList();
 */
/**
 * 根据当前工作的job获取当前流程实例的所有待办对象。如果流程实例已流转完成，则返回一个空数组。
 * @method getTaskListByJob
 * @methodOf module:server.workContext
 * @o2ActionOut x_processplatform_assemble_surface.TaskAction.listWithJob|example=Task
 * @static
 * @return {(Task[])} 待办任务列表.
 * @o2syntax
 * var taskList = this.workContext.getTaskListByJob();
 */
/**
 * 获取当前流程实例的所有已办对象。如果流程实例没有任何人处理过，则返回一个空数组。
 * @method getTaskCompletedList
 * @methodOf module:server.workContext
 * @static
 * @return {(TaskCompleted[])} 已办任务列表.
 * @o2ActionOut x_processplatform_assemble_surface.TaskCompletedAction.listWithWork|example=Task
 * @o2syntax
 * var taskCompletedList = this.workContext.getTaskCompletedList();
 */
/**
 * 根据当前工作的job获取当前流程实例的所有已办对象。如果流程实例没有任何人处理过，则返回一个空数组。
 * @method getTaskCompletedListByJob
 * @methodOf module:server.workContext
 * @static
 * @return {(TaskCompleted[])} 已办任务列表.
 * @o2ActionOut x_processplatform_assemble_surface.TaskCompletedAction.listWithJob|example=Task
 * @o2syntax
 * var taskCompletedList = this.workContext.getTaskCompletedListByJob();
 */
/**
 * @summary 获取当前流程实例的所有待阅对象数组。如果流程实例无待阅，则返回一个空数组。
 * @method getReadList
 * @methodOf module:server.workContext
 * @static
 * @return {(Read[])} 当前流程实例的所有待阅对象数组.
 * @o2ActionOut x_processplatform_assemble_surface.ReadAction.get|example=Read
 * @o2syntax
 * var readList = this.workContext.getReadList();
 */
/**
 * @summary 根据当前工作的job获取当前流程实例的所有待阅对象。如果流程实例无待阅，则返回一个空数组。
 * @method getReadListByJob
 * @methodOf module:server.workContext
 * @static
 * @return {(Read[])} 当前流程实例的所有待阅对象数组.
 * @o2ActionOut x_processplatform_assemble_surface.ReadAction.listWithJob|example=Read
 * @o2syntax
 * var readList = this.workContext.getReadListByJob();
 */
/**
 * @summary 获取当前流程实例的所有已阅对象。如果流程实例没有已阅，则返回一个空数组。
 * @method getReadCompletedList
 * @methodOf module:server.workContext
 * @static
 * @return {(ReadCompleted[])} 当前流程实例的所有已阅对象数组.
 * @o2ActionOut x_processplatform_assemble_surface.ReadCompletedAction.listWithWork|example=Read
 * @o2syntax
 * var readCompletedList = this.workContext.getReadCompletedList();
 */
/**
 * @summary 根据当前工作的job获取当前流程实例的所有已阅对象。如果流程实例没有已阅，则返回一个空数组。
 * @method getReadCompletedListByJob
 * @methodOf module:server.workContext
 * @static
 * @return {(ReadCompleted[])} 当前流程实例的所有已阅对象数组.
 * @o2ActionOut x_processplatform_assemble_surface.ReadCompletedAction.listWithJob|example=Read
 * @o2syntax
 * var readCompletedList = this.workContext.getReadCompletedListByJob();
 */


/**
 * 您可以通过this.org获取组织中的人员、人员属性、组织、组织属性、身份、群组和角色。
 * @module server.org
 * @o2category server
 * @o2ordernumber 100
 * @o2syntax
 * //您可以通过this来获取当前实例的org对象，如下：
 * var org = this.org;
 */

    //身份**********
    //获取身份
    /**
     根据身份标识获取对应的身份对象或数组
     * @method getIdentity
     * @o2membercategory identity
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 身份的distinguishedName、name、id、unique属性值，身份对象，或上述属性值和对象的数组。
     * @return {IdentityData|IdentityData[]} 返回身份，单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.IdentityAction.listObject|example=Identity|ignoreNoDescr=true|ignoreProps=[woUnitDutyList,woUnit,woGroupList]
     * @o2syntax
     * //返回身份，单个是对象，多个是数组。
     * var identityList = this.org.getIdentity( name );
     */

    //列出人员的身份
    /**
     * 根据人员标识获取对应的身份对象数组。
     * @method listIdentityWithPerson
     * @o2membercategory identity
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {IdentityData[]} 返回身份对象数组。
     * @o2ActionOut x_organization_assemble_express.IdentityAction.listWithPersonObject|example=Identity
     * @o2syntax
     * //返回身份对象数组。
     * var identityList = this.org.listIdentityWithPerson( person );
     */

    //查询组织成员身份--返回身份的对象数组
    //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
    /**
     * 根据组织标识获取对应的身份对象数组：identity对象数组。
     * @method listIdentityWithUnit
     * @o2membercategory identity
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested] true嵌套的所有身份成员；false直接身份成员；默认false。
     * @return {IdentityData[]} 返回身份对象数组。
     * @o2ActionOut x_organization_assemble_express.IdentityAction.listWithUnitSubNestedObject|example=Identity
     * @o2syntax
     * //返回直接组织身份对象数组。
     * var identityList = this.org.listIdentityWithUnit( unit );
     *
     *
     * //返回嵌套组织身份对象数组。
     * var identityList = this.org.listIdentityWithUnit( unit, true );
     */


    //组织**********
    //获取组织
    /**
     根据组织标识获取对应的组织：unit对象或数组
     * @method getUnit
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @return {UnitData|UnitData[]} 单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listObject|example=Unit
     * @o2syntax
     * //返回组织，单个是对象，多个是数组。
     * var unitList = this.org.getUnit( name );
     */

    //查询组织的下级--返回组织的对象数组
    //nested  布尔  true嵌套下级；false直接下级；默认false；
    /**
     根据组织标识获取下级组织的对象数组：unit对象数组。
     * @method listSubUnit
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有下级组织；false直接下级组织；默认false。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitSubNestedObject|example=Unit
     * @o2syntax
     * //返回嵌套下级组织数组。
     * var unitList = this.org.listSubUnit( name, true );
     */

    //查询组织的上级--返回组织的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    /**
     根据组织标识批量获取上级组织的对象数组：unit对象数组。
     * @method listSupUnit
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有上级组织；false直接上级组织；默认false。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitSupNestedObject|example=Unit
     * @o2syntax
     * //返回嵌套上级组织数组。
     * var unitList = this.org.listSupUnit( name, true );
     */

    //根据个人身份获取组织
    //flag 数字    表示获取第几层的组织
    //     字符串  表示获取指定类型的组织
    //     空     表示获取直接所在的组织
    /**
     根据个人身份获取组织：unit对象或数组。
     * @method getUnitByIdentity
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag} name - 身份的distinguishedName、name、id、unique属性值，身份对象。
     * @param {String|Number} [flag]  当值为数字的时候， 表示获取第几层的组织。<br/> 当值为字符串的时候，表示获取指定类型的组织。<br/> 当值为空的时候，表示获取直接所在组织。
     * @return {UnitData|UnitData[]} 返回对应组织，单个为对象，多个为数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.getWithIdentityWithLevelObject|example=Unit
     * @o2syntax
     * //返回直接所在组织，单个为对象，多个为数组。
     * var unitList = this.org.getUnitByIdentity( name );
     *
     * //返回第一层组织，单个为对象，多个为数组。
     * var unitList = this.org.getUnitByIdentity( name, 1 );
     *
     * * //返回类型为company的组织，单个为对象，多个为数组。
     * var unitList = this.org.getUnitByIdentity( name, "company" );
     */

    //列出身份所在组织的所有上级组织
    /**
     * 批量查询身份所在的组织,并递归查找其上级组织对象.
     * @method listAllSupUnitWithIdentity
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 身份的distinguishedName、name、id、unique属性值，身份对象，或上述属性值和对象的数组。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithIdentitySupNestedObject|example=Unit
     * @o2syntax
     * //返回组织数组。
     * var unitList = this.org.listAllSupUnitWithIdentity( name );
     */

    //获取人员所在的所有组织
    /**
     * 根据个人标识批量获取组织对象成员：Unit对象数组。
     * @method listUnitWithPerson
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithPersonObject|example=Unit
     * @o2syntax
     * //返回组织数组。
     * var unitList = this.org.listUnitWithPerson( name );
     */

    //列出人员所在组织的所有上级组织
    /**
     * 根据个人标识批量查询所在组织及所有上级组织：Unit对象数组。
     * @method listAllSupUnitWithPerson
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {UnitData[]} 返回个人所在组织及所有上级组织。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithPersonSupNestedObject|example=Unit
     * @o2syntax
     * //返回组织数组。
     * var unitList = this.org.listAllSupUnitWithPerson( name );
     */

    //根据组织属性，获取所有符合的组织
    /**
     * 根据组织属性，获取所有符合的组织。
     * @method listUnitWithAttribute
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {String} attributeName 组织属性名称。
     * @param {String} attributeValue 组织属性值。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitAttributeObject|example=Unit
     * @o2syntax
     * //返回组织数组。
     * var unitList = this.org.listUnitWithAttribute( attributeName, attributeName );
     */

    //根据组织职务，获取所有符合的组织
    /**
     * 根据组织职务，获取所有符合的组织。
     * @method listUnitWithDuty
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {String} dutyName 组织职务名称。
     * @param {IdentityFlag} identity 身份的distinguishedName、name、id、unique属性值，身份对象。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitDutyObject|example=Unit
     * @o2syntax
     * //返回组织数组。
     * var unitList = this.org.listUnitWithDuty( dutyName, identity );
     */


    /**
     * 列式所有顶层组织。
     * @method listTopUnit
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @return {UnitData[]} 返回顶层组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listObject|example=Unit
     * @o2syntax
     * //返回顶层组织数组。
     * var unitList = this.org.listTopUnit();
     */


    //人员
    //获取人员--返回人员的对象数组
    /**
     根据人员标识获取对应的人员对象或数组：person对象或数组
     * @method getPerson
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {PersonData|PersonData[]} 返回人员，单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listObject|example=Person
     * @o2syntax
     * //返回人员，单个是对象，多个是数组。
     * var personList = this.org.getPerson( name );
     */

    //查询下级人员--返回人员的对象数组
    //nested  布尔  true嵌套下级；false直接下级；默认false；
    /**
     根据人员标识获取下级人员的对象数组：person对象数组。该上下级关系被人员的汇报对象值（superior）决定。
     * @method listSubPerson
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有下级人员；false直接下级人员；默认false。
     * @return {PersonData[]} 返回人员数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonSubDirectObject|example=Person
     * @o2syntax
     * //返回嵌套下级人员数组。
     * var personList = this.org.listSubPerson( name, true );
     */

    //查询上级人员--返回人员的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    /**
     *根据人员标识获取上级人员的对象数组：person对象数组。该上下级关系被人员的汇报对象值（superior）决定。
     * @method listSupPerson
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有上级人员；false直接上级人员；默认false。
     * @return {PersonData[]} 返回人员数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonSupDirectObject|example=Person
     * @o2syntax
     * //返回嵌套上级人员数组。
     * var personList = this.org.listSupPerson( name, true );
     */

    //获取群组的所有人员--返回人员的对象数组
    /**
     * 根据群组标识获取人员对象成员：person对象数组。
     * @method listPersonWithGroup
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 群组的distinguishedName、name、id、unique属性值，群组对象，或上述属性值和对象的数组。
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithGroupObject|example=Person
     * @o2syntax
     * //返回人员数组。
     * var personList = this.org.listPersonWithGroup( group );
     */

    //获取角色的所有人员--返回人员的对象数组
    /**
     * 根据角色标识获取人员对象数组：person对象数组。
     * @method listPersonWithRole
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {RoleFlag|RoleFlag[]} name - 角色的distinguishedName、name、id、unique属性值，角色对象，或上述属性值和对象的数组。
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithRoleObject|example=Person
     * @o2syntax
     * //返回人员数组。
     * var personList = this.org.listPersonWithRole( role );
     */

    //获取身份的所有人员--返回人员的对象数组
    /**
     * 根据身份标识获取人员对象成员：person对象数组。
     * @method listPersonWithIdentity
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 身份的distinguishedName、name、id、unique属性值，身份对象，或上述属性值和对象的数组。
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithIdentityObject|example=Person
     * @o2syntax
     * //返回人员数组。
     * var personList = this.org.listPersonWithIdentity( identity );
     */

    //获取身份的所有人员--返回人员的对象数组或人员对象

    //查询组织成员的人员--返回人员的对象数组
    //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
    /**
     * 根据组织标识获取人员对象成员：person对象数组。
     * @method listPersonWithUnit
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested] 是否嵌套获取组织以及下级组织的人员，true表示嵌套，flase表示获取直接组织。默认为false
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithUnitSubDirectObject|example=Person
     * @o2syntax
     * //返回组织的直接人员数组。
     * var personList = this.org.listPersonWithUnit( unit );
     *
     * //返回组织的以及嵌套下级组织所有的人员数组。
     * var personList = this.org.listPersonWithUnit( unit, true );
     */

    //根据属性查询人员--返回人员的对象数组
    //name  string 属性名
    //value  string 属性值
    /**
     * 根据人员属性名称和属性值获取人员对象成员：person对象数组。
     * @method listPersonWithAttribute
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {String} name 人员属性名称。
     * @param {String} value 人员属性值。
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonAttributeObject|example=Person
     * @o2syntax
     * //返回拥有对应属性名和属性值人员数组。
     * var personList = this.org.listPersonWithAttribute( name, value );
     */

    //根据属性查询人员--返回人员的全称数组
    //name  string 属性名
    //value  string 属性值


    //组织职务***********
    //获取指定的组织职务的身份
    /**
     * 根据职务名称和组织名称获取身份。
     * @method getDuty
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {String} dutyName 组织职务名称。
     * @param {UnitFlag} unit 组织的distinguishedName、name、id、unique属性值，组织对象。
     * @return {IdentityData[]} 返回身份数组。
     * @o2ActionOut x_organization_assemble_express.UnitDutyAction.getWithUnitWithName|example=Identity
     * @o2syntax
     * //返回身份数组。
     * var identityList = this.org.getDuty( dutyName, unit );
     */

    //获取身份的所有职务名称
    /**
     * 批量获取身份的所有职务名称。
     * @method listDutyNameWithIdentity
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} identity - 身份的distinguishedName、name、id、unique属性值，身份对象，或上述属性值和对象的数组。
     * @return {String[]} 返回职务名称数组。
     * @o2syntax
     * //返回职务名称数组。
     * var dutyNameList = this.org.listDutyNameWithIdentity( identity );
     */

    //批量获取组织的所有职务名称
    /**
     * 批量获取组织的所有职务名称。
     * @method listDutyNameWithUnit
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} unit - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @return {String[]} 返回职务名称数组。
     * @o2syntax
     * //返回职务名称数组。
     * var dutyNameList = this.org.listDutyNameWithUnit( unit );
     */

    //获取组织的所有职务
    /**
     * 批量获取组织的所有职务。
     * @method listUnitAllDuty
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} unit - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @return {Object[]} 返回职务数组
     * @o2ActionOut x_organization_assemble_express.UnitDutyAction.listWithUnitObject|example=UnitDuty
     * @o2syntax
     * //返回职务数组。
     * var dutyList = this.org.listUnitAllDuty( unit );
     */


    //群组***************
    //获取群组--返回群组的对象数组
    /**
     根据群组标识获取对应的群组对象或数组：group对象或数组
     * @method getGroup
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 群组的distinguishedName、name、id、unique属性值，群组对象，或上述属性值和对象的数组。
     * @return {GroupData|GroupData[]} 返回群组，单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.GroupAction.listObject|example=Group
     * @o2syntax
     * //返回群组，单个是Object，多个是Array。
     * var groupList = this.org.getGroup( name );
     */

    //查询下级群组--返回群组的对象数组
    //nested  布尔  true嵌套下级；false直接下级；默认false；
    /**
     根据群组标识获取下级群组的对象数组：group对象数组。
     * @method listSubGroup
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 群组的distinguishedName、name、id、unique属性值，群组对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有下级群组；false直接下级群组；默认false。
     * @return {GroupData[]} 返回群组数组。
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithGroupSubDirectObject|example=Group
     * @o2syntax
     * //返回嵌套下级群组数组。
     * var groupList = this.org.listSubGroup( name, true );
     */

    //查询上级群组--返回群组的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    /**
     根据群组标识获取上级群组的对象数组：group对象数组。
     * @method listSupGroup
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 群组的distinguishedName、name、id、unique属性值，群组对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有上级群组；false直接上级群组；默认false。
     * @return {GroupData[]} 返回群组数组。
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithGroupSupDirectObject|example=Group
     * @o2syntax
     * //返回嵌套上级群组数组。
     * var groupList = this.org.listSupGroup( name, true );
     */

    //人员所在群组（嵌套）--返回群组的对象数组
    /**
     * 根据人员标识获取所有的群组对象数组。如果群组具有群组（group）成员，且群组成员中包含该人员，那么该群组也被返回。
     * @method listGroupWithPerson
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {GroupData[]} 返回群组对象数组。
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithPersonObject|example=Group
     * @o2syntax
     * //返回群组数组。
     * var groupList = this.org.listGroupWithPerson( name );
     */


    //角色***************
    //获取角色--返回角色的对象数组
    /**
     * 根据角色标识获取对应的角色对象或数组。
     * @method getRole
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {RoleFlag|RoleFlag[]} name - 角色的distinguishedName、name、id、unique属性值，角色对象；或上述属性值和对象的数组。
     * @return {RoleData|RoleData[]} 返回角色，单个为Object，多个为Array。
     * @o2ActionOut x_organization_assemble_express.RoleAction.listObject|example=Role
     * @o2syntax
     * //返回角色，单个为对象，多个为数组。
     * var roleList = this.org.getRole( name );
     */

    //人员所有角色（嵌套）--返回角色的对象数组
    /**
     * 根据人员标识获取所有的角色对象数组。如果角色具有群组（group）成员，且群组中包含该人员，那么该角色也被返回。
     * @method listRoleWithPerson
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {RoleData[]} 返回角色对象数组。
     * @o2ActionOut x_organization_assemble_express.RoleAction.listWithPersonObject|example=Role
     * @o2syntax
     * //返回角色数组。
     * var roleList = this.org.listRoleWithPerson( name );
     */


    //人员***************
    //人员是否拥有角色--返回true, false
    /**
     * 人员是否拥有角色。
     * @method personHasRole
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag} name - 人员的distinguishedName、name、id、unique属性值，人员对象。
     * @param {RoleFlag|RoleFlag[]} roleList - 角色的distinguishedName、name、id、unique属性值，角色对象；或上述属性值和对象的数组。
     * @return {Boolean} 如果人员拥有角色返回true, 否则返回false。
     * @o2syntax
     * //返回判断结果。
     * var groupList = this.org.personHasRole( name, roleList );
     */

    //群组是否拥有角色--返回true, false
    /**
     * 群组是否拥有角色。
     * @method groupHasRole
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag} name - 群组的distinguishedName、name、id、unique属性值，群组对象。
     * @param {RoleFlag|RoleFlag[]} roleList - 角色的distinguishedName、name、id、unique属性值，角色对象；或上述属性值和对象的数组。
     * @return {Boolean} 如果群组拥有角色返回true, 否则返回false。
     * @o2syntax
     * //返回判断结果。
     * var groupList = this.org.groupHasRole( name, roleList );
     */


    //人员属性************
    //添加人员属性值(在属性中添加values值，如果没有此属性，则创建一个)
    /**
     * 添加人员属性值(在属性中添加values值，如果没有此属性，则创建一个)
     * @method appendPersonAttribute
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag} person - 人员的distinguishedName、name、id、unique属性值，人员对象。
     * @param {String} attr 属性名称。
     * @param {String[]} values 属性值，必须为数组。
     * @param {Function} [success] 执行成功的回调。
     * @param {Function} [failure] 执行失败的回调。
     * @o2syntax
     * //同步执行
     * this.org.appendPersonAttribute( person, attribute, valueArray);
     */

    //设置人员属性值(将属性值修改为values，如果没有此属性，则创建一个)
    /**
     * 设置人员属性值(将属性值修改为values，如果没有此属性，则创建一个)
     * @method setPersonAttribute
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag} person - 人员的distinguishedName、name、id、unique属性值，人员对象。
     * @param {String} attr 属性名称。
     * @param {String[]} values 属性值，必须为数组。
     * @param {Function} [success] 执行成功的回调。
     * @param {Function} [failure] 执行失败的回调。
     * @o2syntax
     * //同步执行
     * this.org.setPersonAttribute( person, attribute, valueArray);
     */

    //获取人员属性值
    /**
     根据人员和属性名称获取属性值数组。
     * @method getPersonAttribute
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag} person - 人员的distinguishedName、name、id、unique属性值，人员对象。
     * @param {String} attr 属性名称。
     * @return {String[]} 返回属性值数组，
     * 如：<pre><code class='language-js'>[ value1, value2 ]</code></pre>
     * @o2syntax
     * //返回该人员的属性值数组。
     * var attributeList = this.org.getPersonAttribute( person, attr );
     */

    //列出人员所有属性的名称
    /**
     列出人员所有属性的名称数组。
     * @method listPersonAttributeName
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {String[]} 返回人员属性名称数组，
     * 如：<pre><code class='language-js'>[ attributeName1, attributeName2 ]</code></pre>
     * @o2syntax
     * //返回人员所有属性的名称数组。
     * var attributeNameList = this.org.listPersonAttributeName( person );
     */

    //列出人员的所有属性
    /**
     列出人员的所有属性对象数组。
     * @method listPersonAllAttribute
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @return {Object[]} 返回人员属性对象数组，如：
     * <pre><code class='language-js'>[{
     *    "name": "住址",
     *    "person": "张三@zhangsan@P",
     *    "attributeList": [
     *        "杭州市","绍兴市"
     *    ]
     * }]</code></pre>
     * @o2syntax
     * //返回人员所有属性的对象数组。
     * var attributeObjectList = this.org.listPersonAllAttribute( person );
     */


    //组织属性**************
    //添加组织属性值(在属性中添加values值，如果没有此属性，则创建一个)
    /**
     * 添加组织属性值(在属性中添加values值，如果没有此属性，则创建一个)
     * @method appendUnitAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag} unit - 组织的distinguishedName、name、id、unique属性值，组织对象。
     * @param {String} attribute 属性名称。
     * @param {String[]} valueArray 属性值，必须为数组。
     * @param {Function} [success] 执行成功的回调。
     * @param {Function} [failure] 执行失败的回调。
     * @o2syntax
     * this.org.appendUnitAttribute( unit, attribute, valueArray);
     */

    //设置组织属性值(将属性值修改为values，如果没有此属性，则创建一个)
    /**
     * 设置组织属性值(将属性值修改为values，如果没有此属性，则创建一个)
     * @method setUnitAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag} unit - 组织的distinguishedName、name、id、unique属性值，组织对象。
     * @param {String} attribute 属性名称。
     * @param {String[]} valueArray 属性值，必须为数组。
     * @param {Function} [success] 执行成功的回调。
     * @param {Function} [failure] 执行失败的回调。
     * @o2syntax
     * this.org.setUnitAttribute( unit, attribute, valueArray);
     */

    //获取组织属性值
    /**
     根据组织标识和属性名称获取对应属性值。
     * @method getUnitAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag} unit - 组织的distinguishedName、name、id、unique属性值，组织对象。
     * @param {String} attr 属性名称。
     * @return {String[]} 返回属性值数组，
     * 如：<pre><code class='language-js'>[ value1, value2 ]</code></pre>
     * @o2syntax
     * //返回该组织的属性值数组。
     * var attributeList = this.org.getUnitAttribute( unit, attr );
     */

    //列出组织所有属性的名称
    /**
     列出组织所有属性的名称数组。
     * @method listUnitAttributeName
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @return {String[]} 返回组织属性名称数组，
     * 如：<pre><code class='language-js'>[ attributeName1, attributeName2 ]</code></pre>
     * @o2syntax
     * //返回组织所有属性的名称数组。
     * var attributeNameList = this.org.listUnitAttributeName( unit );
     */

    //列出组织的所有属性
    /**
     列出组织的所有属性对象数组。
     * @method listUnitAllAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @return {Object[]} 返回组织属性对象数组，如：
     * <pre><code class='language-js'>[{
     *    "name": "部门类别",
     *    "unit": "开发部@kfb@U",
     *    "attributeList": [
     *        "生产部门",
     *        "二级部门"
     *    ]
     * }]</code></pre>
     * @o2syntax
     * //返回组织所有属性的对象数组。
     * var attributeObjectList = this.org.listUnitAllAttribute( unit );
     */


//dict
/**
 * this.Dict是一个工具类，如果您在流程、门户中创建了数据字典，可以使用this.Dict类对数据字典进行增删改查操作。
 * @module server.Dict
 * @o2category server
 * @o2ordernumber 120
 * @o2syntax
 * //您可以通过this.Dict()对本应用或其他应用的数据字典中的数据进行增删改查，如下：
 * var dict = new this.Dict( options )
 * @example
 * var dict = new this.Dict({
 *     //type: 应用类型。可以为process  cms。
 *     //如果没有该选项或者值为空字符串，则表示应用脚本和被应用的脚本配置类型相同。
 *     //比如在流程的A应用脚本中引用流程B应用的脚本配置，则type可以省略。
 *    type : "cms",
 *    application : "bulletin", //流程、CMS的名称、别名、id, 默认为当前应用
 *    name : "bulletinDictionary", // 数据字典的名称、别名、id
 * });
 */
/**
 * 根据路径获取数据字典中的数据。
 * @method get
 * @methodOf module:server.Dict
 * @static
 * @param {String} [path] 数据字典中的数据路径，允许使用中文。当路径为多级时，用点号(.)分隔。当值为空的时候，表示获取数据字典中的所有数据。
 * @param {Function} [success] 获取数据成功时的回调函数。<b>流程设计后台脚本中无此参数。</b>
 * @param {Function} [failure] 获取数据失败时的回调。<b>流程设计后台脚本中无此参数。</b>
 * @return {(Object|Array|String|Number|Boolean)}
 * 返回数据字典的数据，类型和配置数据字典时候指定的一致。
 * @o2syntax
 * var data = dict.get( path, success, failure )
 * @example
 * <caption>
 *     已经配置好了如下图所示的数据字典
 * <img src='img/module/Dict/dict.png' />
 * </caption>
 * var dict = new this.Dict({
 *     //type: 应用类型。可以为process  cms。
 *     //如果没有该选项或者值为空字符串，则表示应用脚本和被应用的脚本配置类型相同。
 *     //比如在流程的A应用脚本中引用流程B应用的脚本配置，则type可以省略。
 *    type : "cms",
 *    application : "bulletin", //流程、CMS的名称、别名、id, 默认为当前应用
 *    name : "bulletinDictionary", // 数据字典的名称、别名、id
 * });
 *
 * var data = dict.get();
 * //data的值为
 * {
 *    "category": [
 *        {
 *            "enable": true,
 *            "sequence": 1.0,
 *            "text": "公司公告",
 *            "value": "company"
 *        },
 *        {
 *            "enable": "false",
 *            "sequence": 2.0,
 *            "text": "部门公告",
 *            "value": "department"
 *        }
 *    ]
 * }
 *
 *  var category = dict.get("category");
 *  //category的值为
 *  [
 *     {
 *        "enable": true,
 *        "sequence": 1.0,
 *        "text": "公司公告",
 *        "value": "company"
 *    },
 *     {
 *       "enable": "false",
 *       "sequence": 2.0,
 *        "text": "部门公告",
 *        "value": "department"
 *    }
 *  ]
 *
 *  var array0 = dict.get("category.0");
 *  //array0 的值为
 *  {
 *    "enable": true,
 *    "sequence": 1.0,
 *    "text": "公司公告",
 *    "value": "company"
 * }
 *
 * var enable = dict.get("category.0.eanble");
 * //enable 的值为 true
 */
/**
 * 根据路径新增数据字典的数据。
 * @method add
 * @methodOf module:Dict
 * @instance
 * @param {String} path 数据字典中的数据路径，允许使用中文。当路径为多级时，用点号(.)分隔。如果path在数据字典中已有数据，且原有数据是数组，则数组添加一项；如果原有数据不是数组，则报错。
 * @param {(Object|Array|String|Number|Boolean)} data 需要新增的数据
 * @param {Function} [success] 增加数据成功时的回调函数。<b>流程设计后台脚本中无此参数。</b>
 * @param {Function} [failure] 增加数据错误时的回调函数。<b>流程设计后台脚本中无此参数。</b>
 * @o2syntax
 * dict.add( path, data, success, failure )
 * @example
 * var dict = new this.Dict({
 *     //type: 应用类型。可以为process  cms。
 *     //如果没有该选项或者值为空字符串，则表示应用脚本和被应用的脚本配置类型相同。
 *     //比如在流程的A应用脚本中引用流程B应用的脚本配置，则type可以省略。
 *    type : "cms",
 *    application : "bulletin", //流程、CMS的名称、别名、id, 默认为当前应用
 *    name : "bulletinDictionary", // 数据字典的名称、别名、id
 * });
 *
 * dict.add( "category", { text : "系统公告", value : "system" }, function(data){
 *    //data 形如
 *    //{
 *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id为数据字典ID
 *    //}
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 * @example
 * <caption>
 *     对get方法样例的数据字典进行赋值，如下：
 * </caption>
 * var dict = new this.Dict({
 *     //type: 应用类型。可以为process  cms。
 *     //如果没有该选项或者值为空字符串，则表示应用脚本和被应用的脚本配置类型相同。
 *     //比如在流程的A应用脚本中引用流程B应用的脚本配置，则type可以省略。
 *    type : "cms",
 *    application : "bulletin", //流程、CMS的名称、别名、id, 默认为当前应用
 *    name : "bulletinDictionary", // 数据字典的名称、别名、id
 * });
 *
 * dict.add( "category", { text : "系统公告", value : "system" }, function(data){
 *    //data 形如
 *    //{
 *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id为数据字典ID
 *    //}
 * }, function(xhr, text, error){
 *    //xhr 为 xmlHttpRequest, text 为错误文本， error为Error对象
 * });
 *     //数据字典的值变为
 * {
 *    "category": [
 *        {
 *            "enable": true,
 *            "sequence": 1.0,
 *            "text": "公司公告",
 *            "value": "company"
 *        },
 *        {
 *            "enable": "false",
 *            "sequence": 2.0,
 *            "text": "部门公告",
 *            "value": "department"
 *        },
 *        {
 *            "text": "系统公告",
 *            "value": "system"
 *        }
 *    ]
 * }
 *
 *  dict.add( "category.2.sequence", 3 );
 *     //数据字典的值变为
 * {
 *    "category": [
 *        {
 *            "enable": true,
 *            "sequence": 1.0,
 *            "text": "公司公告",
 *            "value": "company"
 *        },
 *        {
 *            "enable": "false",
 *            "sequence": 2.0,
 *            "text": "部门公告",
 *            "value": "department"
 *        },
 *        {
 *            "sequence" : 3.0,
 *            "text": "系统公告",
 *            "value": "system"
 *        }
 *    ]
 * }

 * dict.add( "archiveOptions", {
 *    "yes" : "是",
 *    "no" : "否"
 * });
 *     //数据字典的值变为
 * {
 *    "category": [
 *        {
 *            "enable": true,
 *            "sequence": 1.0,
 *            "text": "公司公告",
 *            "value": "company"
 *        },
 *        {
 *            "enable": "false",
 *            "sequence": 2.0,
 *            "text": "部门公告",
 *            "value": "department"
 *        },
 *        {
 *            "sequence" : 3.0,
 *            "text": "系统公告",
 *            "value": "system"
 *        }
 *
 *    ],
 *    "archiveOptions" : {
 *        "yes" : "是",
 *        "no" : "否"
 *    }
 * }
 * @example
 * <caption>下面是错误的赋值，如下：</caption>
 * dict.add( "category.3", { text : "系统公告", value : "system" }); //出错，因为不能对数组下标直接赋值
 *
 * dict.add( "category.1.value", { text : "系统公告" } ); //出错，因为不能对已经存在的非数组路径赋值
 */

/**
 * 根据路径修改数据字典的数据。
 * @method set
 * @methodOf module:Dict
 * @instance
 * @param {String} path 数据字典中的数据路径，允许使用中文。当路径为多级时，用点号(.)分隔。如果数据路径不存在，则报错。
 * @param {(Object|Array|String|Number|Boolean)} data 修改后的数据
 * @param {Function} [success] 设置数据成功时的回调函数。<b>流程设计后台脚本中无此参数。</b>
 * @param {Function} [failure] 设置数据错误时的回调函数。<b>流程设计后台脚本中无此参数。</b>
 * @o2syntax
 * dict.set( path, data, success, failure )
 * @example
 * var dict = new this.Dict({
 *     //type: 应用类型。可以为process  cms。
 *     //如果没有该选项或者值为空字符串，则表示应用脚本和被应用的脚本配置类型相同。
 *     //比如在流程的A应用脚本中引用流程B应用的脚本配置，则type可以省略。
 *    type : "cms",
 *    application : "bulletin", //流程、CMS的名称、别名、id, 默认为当前应用
 *    name : "bulletinDictionary", // 数据字典的名称、别名、id
 * });
 *
 * dict.set( "category", { text : "系统公告", value : "system" }, function(data){
 *    //data 形如
 *    //{
 *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id为数据字典ID
 *    //}
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 * @example
 * <caption>
 *     对Example add的数据字典进行赋值，如下：
 * </caption>
 * var dict = new this.Dict({
 *     //type: 应用类型。可以为process  cms。
 *     //如果没有该选项或者值为空字符串，则表示应用脚本和被应用的脚本配置类型相同。
 *     //比如在流程的A应用脚本中引用流程B应用的脚本配置，则type可以省略。
 *    type : "cms",
 *    application : "bulletin", //流程、CMS的名称、别名、id, 默认为当前应用
 *    name : "bulletinDictionary", // 数据字典的名称、别名、id
 * });
 *
 * dict.set( "archiveOptions", [ { text : "是" }, { text : "否" } ]);
 *      //数据字典的值变为
 *  {
 *     "category": [
 *         {
 *             "enable": true,
 *             "sequence": 1.0,
 *             "text": "公司公告",
 *             "value": "company"
 *         },
 *         {
 *             "enable": "false",
 *             "sequence": 2.0,
 *             "text": "部门公告",
 *             "value": "department"
 *         },
 *         {
 *             "sequence" : 3.0,
 *             "text": "系统公告",
 *             "value": "system"
 *         }
 *
 *     ],
 *     "archiveOptions" : [ { text : "是" }, { text : "否" } ]
 * }
 *
 * dict.set( "category.2", { text : "县级公告", value : "county" }, function(data){
 *     //data 形如
 *     //{
 *     //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id为数据字典ID
 *     //}
 *  }, function(xhr){
 *     //xhr 为 xmlHttpRequest
 *  });
 *
 *   /数据字典的值变为
 *  {
 *     "category": [
 *         {
 *             "enable": true,
 *             "sequence": 1.0,
 *             "text": "公司公告",
 *             "value": "company"
 *         },
 *         {
 *             "enable": "false",
 *             "sequence": 2.0,
 *             "text": "部门公告",
 *             "value": "department"
 *         },
 *         {
 *             "text": "县级公告",
 *             "value": "county"
 *         }
 *     ],
 *     "archiveOptions" : [ { text : "是" }, { text : "否" } ]
 * }
 *
 * dict.set( "category.1.sequence", 3 );
 * dict.set( "category.2.sequence", 2 );
 *      //数据字典的值变为
 *      {
 *     "category": [
 *         {
 *             "enable": true,
 *             "sequence": 1.0,
 *             "text": "公司公告",
 *             "value": "company"
 *         },
 *         {
 *             "enable": "false",
 *             "sequence": 3.0,
 *             "text": "部门公告",
 *             "value": "department"
 *         },
 *         {
 *             "sequence": 2.0,
 *             "text": "县级公告",
 *             "value": "county"
 *         }
 *     ],
 *     "archiveOptions" : [ { text : "是" }, { text : "否" } ]
 * }
 * @example
 * <caption>
 *     下面是错误的赋值：
 * </caption>
 * dict.set( "category_1", { text : "公司公告" } ); //出错，因为category_1在数据字典中不存在
 */

/**
 * 根据路径删除数据字典的数据。<b>流程设计后台脚本中无此方法。</b>
 * @method delete
 * @methodOf module:Dict
 * @instance
 * @param {String} path 数据字典中的数据路径，允许使用中文。当路径为多级时，用点号(.)分隔。如果数据路径不存在，则报错。
 * @param {Function} [success] 删除数据成功时的回调函数。
 * @param {Function} [failure] 删除数据错误时的回调函数。
 * @o2syntax
 * dict.delete( path, success, failure )
 * @example
 * var dict = new this.Dict({
 *     //type: 应用类型。可以为process  cms。
 *     //如果没有该选项或者值为空字符串，则表示应用脚本和被应用的脚本配置类型相同。
 *     //比如在流程的A应用脚本中引用流程B应用的脚本配置，则type可以省略。
 *    type : "cms",
 *    application : "bulletin", //流程、CMS的名称、别名、id, 默认为当前应用
 *    name : "bulletinDictionary", // 数据字典的名称、别名、id
 * });
 *
 * dict.delete( "category", function(){
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 * @example
 * <caption>
 *     对Example set的数据字典进行赋值，如下：
 * </caption>
 * var dict = new this.Dict("bulletinDictionary");
 *
 * dict.delete( "archiveOptions");
 * //数据字典的值变为
 * {
 *    "category": [
 *        {
 *            "enable": true,
 *            "sequence": 1.0,
 *            "text": "公司公告",
 *     *            "value": "company"
 *        },
 *        {
 *            "enable": "false",
 *            "sequence": 3.0,
 *            "text": "部门公告",
 *            "value": "department"
 *        },
 *        {
 *            "sequence": 2.0,
 *            "text": "县级公告",
 *            "value": "county"
 *        }
 *    ]
 * }
 *
 * dict.delete( "category.2.sequence", function(data){
 *    //data 形如
 *    //{
 *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id为数据字典ID
 *    //}
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 * //数据字典的值变为
 * {
 *    "category": [
 *        {
 *            "enable": true,
 *            "sequence": 1.0,
 *            "text": "公司公告",
 *            "value": "company"
 *        },
 *        {
 *            "enable": "false",
 *            "sequence": 3.0,
 *            "text": "部门公告",
 *            "value": "department"
 *        },
 *        {
 *            "text": "县级公告",
 *            "value": "county"
 *        }
 *    ]
 * }
 *
 * dict.delete( "category.2");
 * //数据字典的值变为
 * {
 *    "category": [
 *        {
 *            "enable": true,
 *            "sequence": 1.0,
 *            "text": "公司公告",
 *            "value": "company"
 *        },
 *        {
 *            "enable": "false",
 *            "sequence": 3.0,
 *            "text": "部门公告",
 *            "value": "department"
 *        }
 *    ]
 * }
 * @example
 * <caption>
 *     下面是错误的删除：
 * </caption>
 * dict.delete( "category_1" ); //出错，因为category_1在数据字典中不存在
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
 */
/**
 * 平台预置了Actions对象用于调用平台提供的服务，您可以使用this.Actions.load来获取这些方法。由于是运行在服务器端，服务都是同步调用。
 * @method load
 * @methodOf module:server.Actions
 * @instance
 * @param {String} root 平台RESTful服务根，具体服务列表参见:http://server:20030/x_program_center/jest/list.html。
 * 如:
 *<pre><code class='language-js'>
 * "x_processplatform_assemble_surface" //流程平台相关服务根
 * </code></pre>
 * @return {Object} 返回action对象，用于后续服务调用
 * @o2syntax
 * var actions = this.Actions.load( root );
 * @o2syntax
 * //获取流程平台服务对象。
 * var processAction = this.Actions.load("x_processplatform_assemble_surface");
 * @o2syntax
 * <caption>
 *     通过this.Actions.load(root)方法得到action对象，就可以访问此服务下的方法了。<br/>
 *     访问方法的规则如下：
 *  </caption>
 *  var requestString = this.Actions.load( root )[actionName][methodName]( arguements );
 *
 *  requestString : 服务返回的响应数据，字符串格式，可以通过 requestObjest = JSON.parse(requestString);解析成对象
 *
 *  root : 平台服务根名称，如果 x_processplatform_assemble_surface
 *
 *  actionName : 服务下的Action分类名称，如 TaskAction
 *
 *  methodName : Action分类下的方法名称，如 get
 *
 *  arguements : 需调用的RESTful服务的相关参数。这些参数需要按照先后顺序传入。根据实际情况可以省略某些参数。参数序列分别是:
 *
 *      uri的参数, data/formData(Post, Put方法), file(附件), success, failure, async。
 *
 *      uri参数：如果有uri有多个参数，需要按先后顺序传入。
 *
 *      data（formData）参数：提交到后台的数据，如果是上传附件，传入formData。POST 和 PUT 方法需要传入，GET方法和DELETE方法省略。
 *
 *      file参数：POST 或者 PUT方法中有效，当需要上传附件时传入,否则可以省略。
 *
 *      success参数：服务执行成功时的回调方法，形如 function(json){
 *          json为后台服务传回的数据
 *      }。
 *
 *      failure 参数：服务执行失败时的回调方法，形如 function(xhr){
 *          xhr XmlHttpRequest对象，服务器请求失败时有值
 *       }
 *      此参数可以省略，如果省略，系统会自动弹出错误信息。
 *  @o2syntax
 *  <caption>
 *  处理返回的数据有两种方式，二选一即可：<br/>
 *  1、该方法返回的结果是响应数据字符串，通过JSON.parse(responseString)获取对象。<br/>
 *  2、通过success方法作为第一个参数来处理结果
 *  </caption>
 *  //success：arguements中的第一个function对象
 *  function(json){
 *    //json为后台服务传回的数据
 *  }
 *  @example
 * <caption>
 *     <b>样例1:</b>
 *     根据x_processplatform_assemble_surface服务获取当前用户的待办列表：<br/>
 *     可以通过对应服务的查询页面，http://server:20020/x_processplatform_assemble_surface/jest/index.html<br/>
 *     可以看到以下界面：<img src="img/module/Actions/Actions.png"/>
 *     我们可以找到TaskAction的V2ListPaging服务是列式当前用户待办的服务。<br/>
 *     该服务有以下信息：<br/>
 *     1、actionName是：TaskAction<br/>
 *     2、methodName是：V2ListPaging<br/>
 *     3、有两个url参数，分别是 page(分页), size(每页数量)<br/>
 *     4、有一系列的body参数<br/>
 *     5、该服务方法类型是POST<br/>
 *     根据这些信息我们可以组织出下面的方法：
 * </caption>
 * var processAction = this.Actions.load("x_processplatform_assemble_surface"); //获取action
 * var method = processAction.TaskAction.V2ListPaging; //获取列式方法
 * //执行方法1
 * method(
 *  1,  //uri 第1个参数，如果无uri参数，可以省略
 *  20, //uri 第2个参数，如果无uri参数，可以省略，如果还有其他uri参数，可以用逗号, 分隔
 *  {   //body 参数，对POST和PUT请求，该参数必须传，可以为空对象
 *      processList : [xxx] //具体参数
 *  },
 *  function(json){ //正确调用的回调
 *       //json.data得到服务返回数据
 *  },
 *  function(xhr){ //可选，错误调用的回调
 *      //xhr为XmlHttpRequest对象，服务器请求失败时有值
 *      var responseJSON = JSON.parse( xhr.responseText ) //xhr.responseText {String}是后台返回的出错信息
 *      //responseJSON见下面的说明
 *
 *      var message = responseJSON.message; //message为错误提示文本
 *  }
 * );
 *
 * //执行方法2
 * var responseString = method( 1, 20, {processList : [xxx]} )
 * var responseObject = JSON.parse(responseObject);
 * @example
 * <caption>出错信息responseJSON的格式</caption>
 * {
 *       "type": "error", //类型为错误
 *       "message": "标识为:343434 的 Task 对象不存在.", //提示文本
 *       "date": "2020-12-29 17:02:13", //出错时间
 *       "prompt": "com.x.base.core.project.exception.ExceptionEntityNotExist" //后台错误类
 *}
 * @example
 * <caption>
 *     <b>样例2:</b>
 *      已知流程实例的workid，在脚本中获取数据，修改后进行保存。
 * </caption>
 * //查询服务列表找到获取data数据服务为DataAction的getWithWork方法
 * //查询服务列表找到更新data数据服务为DataAction的updateWithWork方法
 *
 * var workid = "cce8bc22-225a-4f85-8132-7374d546886e";
 * var data;
 * this.Actions.load("x_processplatform_assemble_surface").DataAction.getWithWork( //平台封装好的方法
 *      workid, //uri的参数
 *      function( json ){ //服务调用成功的回调函数, json为服务传回的数据
 *          data = json.data; //为变量data赋值
 *      }.bind(this)
 * )
 *
 * data.subject = "新标题"; //修改数据
 * this.Actions.load("x_processplatform_assemble_surface").DataAction.updateWithWork(
 *      workid, //uri的参数
 *      data, //保存的数据
 *      function(){ //服务调用成功的回调函数
 *
 *      }.bind(this)
 * );
 */


/**
 * this.Table是一个工具类，您可以使用这个类对数据中心的数据表进行增删改查操作。
 * @module server.Table
 * @o2category server
 * @o2ordernumber 135
 * @param {String} tableName 数据表的id、名称或别名。
 * @return {Object} table对象
 * @o2syntax
 * //您可以在脚本中，通过this.Table()来返回Table的对象，如下：
 * var table = new this.Table( tableName )
 */

/**
 * 列示表中的行对象,下一页。
 * @method listRowNext
 * @methodOf module:server.Table
 * @instance
 * @param {String} id  当前页最后一条数据的Id，如果是第一页使用"(0)"。
 * @param {String|Number} count 下一页的行数
 * @param {Function} [success] 调用成功时的回调函数。
 * @param {Function} [failure] 调用错误时的回调函数。
 * @o2syntax
 * table.listRowNext( id, count, success, failure )
 * @example
 * var table = new this.Table("table1");
 *
 * table.listRowNext( "0", 20, function(data){
 *    //data 形如
 *    //{
 *    //    "type": "success",
 *    //    "data":[
 *    //       {
 *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //数据的id
 *    //        "createTime": "2021-11-01 16:23:41", //数据创建时间
 *    //        "updateTime": "2021-11-01 16:23:41", //数据更新时间
 *    //         ... //定义的字段（列）和值
 *    //        }
 *   //     ],
 *   //       "message": "",
 *   //     "date": "2021-11-01 18:34:19",
 *   //     "spent": 13,
 *   //}
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 */

/**
 * 列示表中的行对象,上一页。
 * @method listRowPrev
 * @methodOf module:server.Table
 * @instance
 * @param {String} id  当前页第一条数据的Id，如果是最后一页使用"(0)"。
 * @param {String|Number} count 上一页的行数
 * @param {Function} [success] 调用成功时的回调函数。
 * @param {Function} [failure] 调用错误时的回调函数。
 * @o2syntax
 * table.listRowNext( id, count, success, failure )
 * @example
 * var table = new this.Table("table1");
 *
 * table.listRowNext( "0", 20, function(data){
 *    //data 形如
 *    //{
 *    //    "type": "success",
 *    //    "data":[
 *    //       {
 *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //数据的id
 *    //        "createTime": "2021-11-01 16:23:41", //数据创建时间
 *    //        "updateTime": "2021-11-01 16:23:41", //数据更新时间
 *    //         ... //定义的字段（列）和值
 *    //        }
 *   //     ],
 *   //       "message": "",
 *   //     "date": "2021-11-01 18:34:19",
 *   //     "spent": 13,
 *   //}
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 */

/**
 * 根据条件获取表中的数据。
 * @method listRowSelect
 * @methodOf module:server.Table
 * @instance
 * @param {String} [where] 查询条件，格式为jpql语法,o.name='zhangsan'，允许为空。
 * @param {String} [orderBy] 排序条件，格式为：o.updateTime desc，允许为空
 * @param {String|Number} [size] 返回结果集数量,允许为空。
 * @param {Function} [success] 调用成功时的回调函数。
 * @param {Function} [failure] 调用错误时的回调函数。
 * @o2syntax
 * table.listRowSelect( where, orderBy, size, success, failure )
 * @example
 * var table = new this.Table("table1");
 *
 * //查询字段name等于zhangsan的数据，结果按updateTime倒序
 * table.listRowSelect( "o.name='zhangsan", "o.updateTime desc", 20, function(data){
 *    //data 形如
 *    //{
 *    //    "type": "success",
 *    //    "data":[
 *    //       {
 *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //数据的id
 *    //        "createTime": "2021-11-01 16:23:41", //数据创建时间
 *    //        "updateTime": "2021-11-01 16:23:41", //数据更新时间
 *    //         ... //定义的字段（列）和值
 *    //        }
 *   //     ],
 *   //       "message": "",
 *   //     "date": "2021-11-01 18:34:19",
 *   //     "spent": 13,
 *   //}
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 */

/**
 * 通过where 统计数量。
 * @method rowCountWhere
 * @methodOf module:server.Table
 * @instance
 * @param {String} where 查询条件，格式为jpql语法,o.name='zhangsan'，允许为空。
 * @param {Function} [success] 调用成功时的回调函数。
 * @param {Function} [failure] 调用错误时的回调函数。
 * @o2syntax
 * table.rowCountWhere( where, success, failure )
 * @example
 * var table = new this.Table("table1");
 *
 * //查询字段name等于zhangsan的数据，结果按updateTime倒序
 * table.rowCountWhere( "o.name='zhangsan", function(data){
 *    //data 形如
 *    //{
 *    //   "type": "success",
 *    //  "data": {
 *    //      "value": 5 //符合条件数据的总条数
 *    //  },
 *    //  "message": "",
 *    //  "date": "2021-11-01 18:32:27"
 *    //}
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 */

/**
 * 删除数据表中指定id的记录。
 * @method deleteRow
 * @methodOf module:server.Table
 * @instance
 * @param {id} 需要删除记录的id。
 * @param {Function} [success] 调用成功时的回调函数。
 * @param {Function} [failure] 调用错误时的回调函数。
 * @o2syntax
 * table.deleteRow( id, success, failure )
 * @example
 * var table = new this.Table("table1");
 *
 * table.deleteRow( "e1f89185-d8b0-4b66-9e34-aed3323d0d79", function(data){
 *    //data 形如
 *    //{
 *    //   "type": "success",
 *    //  "data": {
 *    //      "value": true //true表示删除成功，false表示无此数据
 *    //  },
 *    //  "message": "",
 *    //  "date": "2021-11-01 18:32:27"
 *    //}
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 */

/**
 * 更新指定表中所有行的数据。
 * @method deleteAllRow
 * @methodOf module:server.Table
 * @instance
 * @param {Function} [success] 调用成功时的回调函数。
 * @param {Function} [failure] 调用错误时的回调函数。
 * @o2syntax
 * table.deleteAllRow( success, failure, async )
 * @example
 * var table = new this.Table("table1");
 *
 * table.deleteAllRow( function(data){
 *    //data 形如
 *    //{
 *    //   "type": "success",
 *    //  "data": {
 *    //      "value": 1 //表示删除的条数，0表示无数据
 *    //  },
 *    //  "message": "",
 *    //  "date": "2021-11-01 18:32:27"
 *    //}
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 */

/**
 * 获取数据表中指定id的记录。
 * @method getRow
 * @methodOf module:server.Table
 * @instance
 * @param {id} 需要获取记录的id。
 * @param {Function} [success] 调用成功时的回调函数。
 * @param {Function} [failure] 调用错误时的回调函数。
 * @o2syntax
 * table.getRow( id, success, failure )
 * @example
 * var table = new this.Table("table1");
 *
 * table.getRow( "e1f89185-d8b0-4b66-9e34-aed3323d0d79", function(data){
 *    //data 形如
 *    //{
 *    //    "type": "success",
 *    //    "data":{
 *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //数据的id
 *    //        "createTime": "2021-11-01 16:23:41", //数据创建时间
 *    //        "updateTime": "2021-11-01 16:23:41", //数据更新时间
 *    //         ... //定义的字段（列）和值
 *    //     },
 *   //     "message": "",
 *   //     "date": "2021-11-01 18:34:19",
 *   //     "spent": 13,
 *   //}
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 */

/**
 * 往数据表中批量插入数据。
 * @method insertRow
 * @methodOf module:server.Table
 * @instance
 * @param {Object[]} data 需要插入的数据。
 * @param {Function} [success] 调用成功时的回调函数。
 * @param {Function} [failure] 调用错误时的回调函数。
 * @o2syntax
 * table.insertRow( data, success, failure )
 * @example
 * var table = new this.Table("table1");
 * var data = [
 *  {
 *    "subject": "标题一",
 *    ... //其他字段
 *  },
 *  ...
 * ];
 * table.insertRow( data, function(data){
 *    //data 形如
 *    //{
 *    //   "type": "success",
 *    //  "data": {
 *    //      "value": true //true表示插入成功
 *    //  },
 *    //  "message": "",
 *    //  "date": "2021-11-01 18:32:27"
 *    //}
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 */

/**
 * 往数据表中修改单条数据。
 * @method updateRow
 * @methodOf module:server.Table
 * @instance
 * @param {String} id 需要修改的数据id。
 * @param {Function} [success] 调用成功时的回调函数。
 * @param {Function} [failure] 调用错误时的回调函数。
 * @o2syntax
 * table.updateRow( id, data, success, failure )
 * @example
 * var table = new this.Table("table1");
 * var data = {
 *    "id" : "2cf3a20d-b166-490b-8d29-05544db3d79b",
 *    "subject": "标题一",
 *    ... //其他字段
 *  };
 * table.updateRow( "2cf3a20d-b166-490b-8d29-05544db3d79b", data, function(data){
 *    //data 形如
 *    //{
 *    //   "type": "success",
 *    //  "data": {
 *    //      "value": true //true表示修改成功
 *    //  },
 *    //  "message": "",
 *    //  "date": "2021-11-01 18:32:27"
 *    //}
 * }, function(xhr){
 *    //xhr 为 xmlHttpRequest
 * });
 */

/**
 * this.include是一个方法，当您在流程、门户或者内容管理中创建了脚本配置，可以使用this.include()用来引用脚本配置。<br/>
 * @module include()
 * @o2category server
 * @o2ordernumber 140
 *
 * @param {(String|Object)} optionsOrName 可以是脚本标识字符串或者是对象。<b>流程设计中的脚本只支持字符串。</b>
 * <pre><code class='language-js'>
 * //如果需要引用本应用的脚本配置，将options设置为String。
 * this.include("initScript") //脚本配置的名称、别名或id
 *
 * //如果需要引用其他应用的脚本配置，将options设置为Object;
 * this.include({
 *       //type: 应用类型。可以为 portal  process  cms。
 *       //如果没有该选项或者值为空字符串，则表示应用脚本和被应用的脚本配置类型相同。
 *       //比如在门户的A应用脚本中引用门户B应用的脚本配置，则type可以省略。
 *       type : "portal",
 *       application : "首页", // 门户、流程、CMS的名称、别名、id。 默认为当前应用
 *       name : "initScript" // 脚本配置的名称、别名或id
 * })
 * </code></pre>
 * @param {Function} [callback] 加载后执行的回调方法。<b>流程设计中的脚本不支持该参数。</b>
 *
 * @o2syntax
 * //您可以在表单、流程、视图和查询视图的各个嵌入脚本中，通过this.include()来引用本应用或其他应用的脚本配置，如下：
 * this.include( optionsOrName, callback )
 * @example
 * <caption>
 *    <b>样例一：</b>在通用脚本中定义一个通用的方法去获取公文管理所有的文种，在查询语句中根据该方法来拼接JPQL。<br/>
 *     1、在内容管理应用中有一个fileRes的应用，在该应用中创建一个脚本，命名为FileSql，并定义方法。
 *     <img src='img/module/include/server_define1.png' />
 * </caption>
 * //定义一个方法
 * this.define("getFileSQL",function(){
 *   var application = ["公司发文","部门发文","党委发文"];
 *   var appSql = " ( ";
 *   for(var i=0;i<application.length;i++){
 *       if(i==application.length-1){
 *           appSql = appSql + " o.applicationName = '"+application[i]+"' "
 *       }else{
 *           appSql = appSql + " o.applicationName = '"+application[i]+"' OR "
 *       }
 *   }
 *   appSql = appSql + " ) ";
 *   return appSql;
 *}.bind(this));
 * @example
 * <caption>
 *      2、在查询语句中使用该方法。
 *     <img src='img/module/include/server_define2.png'/>
 * </caption>
 * this.include({
 *   type : "cms",
 *   application : "fileRes",
 *   name : "FileSql"
 * })
 *
 * var sql = this.getFileSQL();
 *
 * return "SELECT o FROM com.x.processplatform.core.entity.content.Task o WHERE "+sql
 */


/**
 * this.define是一个方法，您在脚本中您可以通过this.define()来定义自己的方法。<br/>
 * 通过这种方式定义方法，在不同的应用使用相同的方法名称也不会造成冲突。
 * @module define()
 * @o2category server
 * @o2ordernumber 150
 *
 * @param {(String)} name 定义的方法名称。
 * @param {Function} fun  定义的方法
 * @param {Boolean} [overwrite] 定义的方法是否能被覆盖重写。默认值为true。
 * @o2syntax
 * this.define(name, fun, overwrite)
 * @example
 * <caption>
 *    <b>样例一：</b>在通用脚本中定义一个通用的方法去获取公文管理所有的文种，在查询语句中根据该方法来拼接JPQL。<br/>
 *     1、在内容管理应用中有一个fileRes的应用，在该应用中创建一个脚本，命名为FileSql，并定义方法。
 *     <img src='img/module/include/server_define1.png' />
 * </caption>
 * //定义一个方法
 * this.define("getFileSQL",function(){
 *   var application = ["公司发文","部门发文","党委发文"];
 *   var appSql = " ( ";
 *   for(var i=0;i<application.length;i++){
 *       if(i==application.length-1){
 *           appSql = appSql + " o.applicationName = '"+application[i]+"' "
 *       }else{
 *           appSql = appSql + " o.applicationName = '"+application[i]+"' OR "
 *       }
 *   }
 *   appSql = appSql + " ) ";
 *   return appSql;
 *}.bind(this));
 * @example
 * <caption>
 *      2、在查询语句中使用该方法。
 *     <img src='img/module/include/server_define2.png'/>
 * </caption>
 * this.include({
 *   type : "cms",
 *   application : "fileRes",
 *   name : "FileSql"
 * })
 *
 * var sql = this.getFileSQL();
 *
 * return "SELECT o FROM com.x.processplatform.core.entity.content.Task o WHERE "+sql
 */

/**
 * this.print是一个方法，在服务器控制台输出信息。<br/>
 * @module print()
 * @o2category server
 * @o2ordernumber 145
 *
 * @param {(String)} text 要输出的文本信息。</b>
 * @param {(String)} type 要输出的文本信息的类型，会添加到输出信息的前面，默认为“PRINT”。</b>
 * @example
 * this.print("这是我要输出的信息");
 * this.print("这是一个错误信息信息", "ERROR");
 */
