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
 * 您可以通过documentContext获取内容管理实例相关的对象数据。
 * @module server.documentContext
 * @o2category server
 * @o2range {CMS}
 * @o2ordernumber 30
 * @o2syntax
 * //您可以在内容管理表单中，通过this来获取当前实例的documentContext对象，如下：
 * var context = this.documentContext;
 * @borrows module:documentContext.getDocument as getDocument
 * @borrows module:documentContext.getControl as getControl
 * @borrows module:documentContext.getAttachmentList as getAttachmentList
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {IdentityData|IdentityData[]} 返回身份，单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.IdentityAction.listObject|example=Identity|ignoreNoDescr=true|ignoreProps=[woUnitDutyList,woUnit,woGroupList]
     * @o2syntax
     * //同步执行，返回身份，单个是对象，多个是数组。
     * var identityList = this.org.getIdentity( name );
     *
     * //异步执行，在回调方法中获取身份
     * this.org.getIdentity( name, function(identityList){
     *     //identityList 为返回的身份，单个是对象，多个是数组。
     * })
     */

    //列出人员的身份
    /**
     * 根据人员标识获取对应的身份对象数组。
     * @method listIdentityWithPerson
     * @o2membercategory identity
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {IdentityData[]} 返回身份对象数组。
     * @o2ActionOut x_organization_assemble_express.IdentityAction.listWithPersonObject|example=Identity
     * @o2syntax
     * //同步执行，返回身份对象数组。
     * var identityList = this.org.listIdentityWithPerson( person );
     *
     * //异步执行，在回调方法中获取
     * this.org.listIdentityWithPerson( person, function(identityList){
     *     //identityList 返回的身份对象数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {IdentityData[]} 返回身份对象数组。
     * @o2ActionOut x_organization_assemble_express.IdentityAction.listWithUnitSubNestedObject|example=Identity
     * @o2syntax
     * //同步执行，返回直接组织身份对象数组。
     * var identityList = this.org.listIdentityWithUnit( unit );
     *
     *
     * //同步执行，返回嵌套组织身份对象数组。
     * var identityList = this.org.listIdentityWithUnit( unit, true );
     *
     * //异步执行，在回调方法中获取
     * this.org.listIdentityWithUnit( unit, false, function(identityList){
     *     //identityList 返回直接组织身份对象数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {UnitData|UnitData[]} 单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listObject|example=Unit
     * @o2syntax
     * //同步执行，返回组织，单个是对象，多个是数组。
     * var unitList = this.org.getUnit( name );
     *
     * //异步执行，在回调方法中获取组织
     * this.org.getUnit( name, function(unitList){
     *     //unitList 为返回的组织，单个是对象，多个是数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitSubNestedObject|example=Unit
     * @o2syntax
     * //同步执行，返回嵌套下级组织数组。
     * var unitList = this.org.listSubUnit( name, true );
     *
     * //异步执行，在回调方法中获取
     * this.org.listSubUnit( name, true, function(unitList){
     *     //unitList 为返回嵌套下级组织数组。
     * })
     */

    //查询组织的上级--返回组织的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    //async 布尔 true异步请求
    /**
     根据组织标识批量获取上级组织的对象数组：unit对象数组。
     * @method listSupUnit
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {Boolean} [nested]  true嵌套的所有上级组织；false直接上级组织；默认false。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitSupNestedObject|example=Unit
     * @o2syntax
     * //同步执行，返回嵌套上级组织数组。
     * var unitList = this.org.listSupUnit( name, true );
     *
     * //异步执行，在回调方法中获取
     * this.org.listSupUnit( name, true, function(unitList){
     *     //unitList 为返回嵌套上级组织数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {UnitData|UnitData[]} 返回对应组织，单个为对象，多个为数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.getWithIdentityWithLevelObject|example=Unit
     * @o2syntax
     * //同步执行，返回直接所在组织，单个为对象，多个为数组。
     * var unitList = this.org.getUnitByIdentity( name );
     *
     * //同步执行，返回第一层组织，单个为对象，多个为数组。
     * var unitList = this.org.getUnitByIdentity( name, 1 );
     *
     * * //同步执行，返回类型为company的组织，单个为对象，多个为数组。
     * var unitList = this.org.getUnitByIdentity( name, "company" );
     *
     * //异步执行，在回调方法中获取
     * this.org.getUnitByIdentity( name, 1, function(unitList){
     *     //unitList 返回第一层组织，单个为对象，多个为数组。
     * })
     */

    //列出身份所在组织的所有上级组织
    /**
     * 批量查询身份所在的组织,并递归查找其上级组织对象.
     * @method listAllSupUnitWithIdentity
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 身份的distinguishedName、name、id、unique属性值，身份对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithIdentitySupNestedObject|example=Unit
     * @o2syntax
     * //同步执行，返回组织数组。
     * var unitList = this.org.listAllSupUnitWithIdentity( name );
     *
     * //异步执行，在回调方法中获取
     * this.org.listAllSupUnitWithIdentity( name, function(unitList){
     *     //unitList 返回组织数组。
     * })
     */

    //获取人员所在的所有组织
    /**
     * 根据个人标识批量获取组织对象成员：Unit对象数组。
     * @method listUnitWithPerson
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithPersonObject|example=Unit
     * @o2syntax
     * //同步执行，返回组织数组。
     * var unitList = this.org.listUnitWithPerson( name );
     *
     * //异步执行，在回调方法中获取
     * this.org.listUnitWithPerson( name, function(unitList){
     *     //unitList 返回组织数组。
     * })
     */

    //列出人员所在组织的所有上级组织
    /**
     * 根据个人标识批量查询所在组织及所有上级组织：Unit对象数组。
     * @method listAllSupUnitWithPerson
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {UnitData[]} 返回个人所在组织及所有上级组织。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithPersonSupNestedObject|example=Unit
     * @o2syntax
     * //同步执行，返回组织数组。
     * var unitList = this.org.listAllSupUnitWithPerson( name );
     *
     * //异步执行，在回调方法中获取
     * this.org.listAllSupUnitWithPerson( name, function(unitList){
     *     //unitList 返回组织数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitAttributeObject|example=Unit
     * @o2syntax
     * //同步执行，返回组织数组。
     * var unitList = this.org.listUnitWithAttribute( attributeName, attributeName );
     *
     * //异步执行，在回调方法中获取
     * this.org.listUnitWithAttribute( attributeName, attributeName, function(unitList){
     *     //unitList 返回组织数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {UnitData[]} 返回组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitDutyObject|example=Unit
     * @o2syntax
     * //同步执行，返回组织数组。
     * var unitList = this.org.listUnitWithDuty( dutyName, identity );
     *
     * //异步执行，在回调方法中获取
     * this.org.listUnitWithDuty( dutyName, identity, function(unitList){
     *     //unitList 返回组织数组。
     * })
     */


    /**
     * 列式所有顶层组织。
     * @method listTopUnit
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {UnitData[]} 返回顶层组织数组。
     * @o2ActionOut x_organization_assemble_express.UnitAction.listObject|example=Unit
     * @o2syntax
     * //同步执行，返回顶层组织数组。
     * var unitList = this.org.listTopUnit();
     *
     * //异步执行，在回调方法中获取
     * this.org.listTopUnit(function(unitList){
     *     //unitList 返回顶层组织数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {PersonData|PersonData[]} 返回人员，单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listObject|example=Person
     * @o2syntax
     * //同步执行，返回人员，单个是对象，多个是数组。
     * var personList = this.org.getPerson( name );
     *
     * //异步执行，在回调方法中获取人员
     * this.org.getPerson( name, function(personList){
     *     //personList 为返回的人员，单个是对象，多个是数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {PersonData[]} 返回人员数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonSubDirectObject|example=Person
     * @o2syntax
     * //同步执行，返回嵌套下级人员数组。
     * var personList = this.org.listSubPerson( name, true );
     *
     *
     * //异步执行，在回调方法中获取
     * this.org.listSubPerson( name, true, function(personList){
     *     //personList 为返回嵌套下级人员数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {PersonData[]} 返回人员数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonSupDirectObject|example=Person
     * @o2syntax
     * //同步执行，返回嵌套上级人员数组。
     * var personList = this.org.listSupPerson( name, true );
     *
     * //异步执行，在回调方法中获取
     * this.org.listSupPerson( name, true, function(personList){
     *     //personList 为返回嵌套上级人员数组。
     * })
     */

    //获取群组的所有人员--返回人员的对象数组
    /**
     * 根据群组标识获取人员对象成员：person对象数组。
     * @method listPersonWithGroup
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 群组的distinguishedName、name、id、unique属性值，群组对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithGroupObject|example=Person
     * @o2syntax
     * //同步执行，返回人员数组。
     * var personList = this.org.listPersonWithGroup( group );
     *
     * //异步执行，在回调方法中获取
     * this.org.listPersonWithGroup( group, function(personList){
     *     //personList 为返回的人员数组。
     * })
     */

    //获取角色的所有人员--返回人员的对象数组
    /**
     * 根据角色标识获取人员对象数组：person对象数组。
     * @method listPersonWithRole
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {RoleFlag|RoleFlag[]} name - 角色的distinguishedName、name、id、unique属性值，角色对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithRoleObject|example=Person
     * @o2syntax
     * //同步执行，返回人员数组。
     * var personList = this.org.listPersonWithRole( role );
     *
     * //异步执行，在回调方法中获取
     * this.org.listPersonWithRole( role, function(personList){
     *     //personList 为返回的人员数组。
     * })
     */

    //获取身份的所有人员--返回人员的对象数组
    /**
     * 根据身份标识获取人员对象成员：person对象数组。
     * @method listPersonWithIdentity
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 身份的distinguishedName、name、id、unique属性值，身份对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithIdentityObject|example=Person
     * @o2syntax
     * //同步执行，返回人员数组。
     * var personList = this.org.listPersonWithIdentity( identity );
     *
     * //异步执行，在回调方法中获取
     * this.org.listPersonWithIdentity( identity, function(personList){
     *     //personList 为返回的人员数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithUnitSubDirectObject|example=Person
     * @o2syntax
     * //同步执行，返回组织的直接人员数组。
     * var personList = this.org.listPersonWithUnit( unit );
     *
     * //同步执行，返回组织的以及嵌套下级组织所有的人员数组。
     * var personList = this.org.listPersonWithUnit( unit, true );
     *
     * //异步执行，在回调方法中获取
     * this.org.listPersonWithUnit( unit, false, function(personList){
     *     //personList 为返回的群组的直接人员数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {PersonData[]} 返回人员对象数组。
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonAttributeObject|example=Person
     * @o2syntax
     * //同步执行，返回拥有对应属性名和属性值人员数组。
     * var personList = this.org.listPersonWithAttribute( name, value );
     *
     * //异步执行，在回调方法中获取
     * this.org.listPersonWithAttribute( name, value, function(personList){
     *     //personList 返回拥有对应属性名和属性值人员数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {IdentityData[]} 返回身份数组。
     * @o2ActionOut x_organization_assemble_express.UnitDutyAction.getWithUnitWithName|example=Identity
     * @o2syntax
     * //同步执行，返回身份数组。
     * var identityList = this.org.getDuty( dutyName, unit );
     *
     * //异步执行，在回调方法中获取
     * this.org.getDuty( dutyName, unit, function(unitList){
     *     //unitList 返回身份数组。
     * })
     */

    //获取身份的所有职务名称
    /**
     * 批量获取身份的所有职务名称。
     * @method listDutyNameWithIdentity
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} identity - 身份的distinguishedName、name、id、unique属性值，身份对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {String[]} 返回职务名称数组。
     * @o2syntax
     * //同步执行，返回职务名称数组。
     * var dutyNameList = this.org.listDutyNameWithIdentity( identity );
     *
     * //异步执行，在回调方法中获取
     * this.org.listDutyNameWithIdentity( identity, function(dutyNameList){
     *     //dutyNameList 返回职务名称数组。
     * })
     */

    //批量获取组织的所有职务名称
    /**
     * 批量获取组织的所有职务名称。
     * @method listDutyNameWithUnit
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} unit - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {String[]} 返回职务名称数组。
     * @o2syntax
     * //同步执行，返回职务名称数组。
     * var dutyNameList = this.org.listDutyNameWithUnit( unit );
     *
     * //异步执行，在回调方法中获取
     * this.org.listDutyNameWithUnit( unit, function(dutyNameList){
     *     //dutyNameList 返回职务名称数组。
     * })
     */

    //获取组织的所有职务
    /**
     * 批量获取组织的所有职务。
     * @method listUnitAllDuty
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} unit - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {Object[]} 返回职务数组
     * @o2ActionOut x_organization_assemble_express.UnitDutyAction.listWithUnitObject|example=UnitDuty
     * @o2syntax
     * //同步执行，返回职务数组。
     * var dutyList = this.org.listUnitAllDuty( unit );
     *
     * //异步执行，在回调方法中获取
     * this.org.listUnitAllDuty( unit, function(dutyList){
     *     //dutyList 返回职务数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {GroupData|GroupData[]} 返回群组，单个是Object，多个是Array。
     * @o2ActionOut x_organization_assemble_express.GroupAction.listObject|example=Group
     * @o2syntax
     * //同步执行，返回群组，单个是Object，多个是Array。
     * var groupList = this.org.getGroup( name );
     *
     * //异步执行，在回调方法中获取群组
     * this.org.getGroup( name, function(groupList){
     *     //groupList 为返回的群组，单个是Object，多个是Array。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {GroupData[]} 返回群组数组。
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithGroupSubDirectObject|example=Group
     * @o2syntax
     * //同步执行，返回嵌套下级群组数组。
     * var groupList = this.org.listSubGroup( name, true );
     *
     * //异步执行，在回调方法中获取群组
     * this.org.listSubGroup( name, true, function(groupList){
     *     //groupList 为返回嵌套下级群组数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {GroupData[]} 返回群组数组。
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithGroupSupDirectObject|example=Group
     * @o2syntax
     * //同步执行，返回嵌套上级群组数组。
     * var groupList = this.org.listSupGroup( name, true );
     *
     * //异步执行，在回调方法中获取群组
     * this.org.listSupGroup( name, true, function(groupList){
     *     //groupList 为返回嵌套上级群组数组。
     * })
     */

    //人员所在群组（嵌套）--返回群组的对象数组
    /**
     * 根据人员标识获取所有的群组对象数组。如果群组具有群组（group）成员，且群组成员中包含该人员，那么该群组也被返回。
     * @method listGroupWithPerson
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {GroupData[]} 返回群组对象数组。
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithPersonObject|example=Group
     * @o2syntax
     * //同步执行，返回群组数组。
     * var groupList = this.org.listGroupWithPerson( name );
     *
     * //异步执行，在回调方法中获取群组
     * this.org.listGroupWithPerson( name, function(groupList){
     *     //groupList 为返回的群组数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {RoleData|RoleData[]} 返回角色，单个为Object，多个为Array。
     * @o2ActionOut x_organization_assemble_express.RoleAction.listObject|example=Role
     * @o2syntax
     * //同步执行，返回角色，单个为对象，多个为数组。
     * var roleList = this.org.getRole( name );
     *
     * //异步执行，在回调方法中获取角色，单个为对象，多个为数组
     * this.org.getRole( name, function(roleList){
     *     //roleList 为返回判断结果。
     * })
     */

    //人员所有角色（嵌套）--返回角色的对象数组
    /**
     * 根据人员标识获取所有的角色对象数组。如果角色具有群组（group）成员，且群组中包含该人员，那么该角色也被返回。
     * @method listRoleWithPerson
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {RoleData[]} 返回角色对象数组。
     * @o2ActionOut x_organization_assemble_express.RoleAction.listWithPersonObject|example=Role
     * @o2syntax
     * //同步执行，返回角色数组。
     * var roleList = this.org.listRoleWithPerson( name );
     *
     * //异步执行，在回调方法中获取角色
     * this.org.listRoleWithPerson( name, function(roleList){
     *     //roleList 为返回的角色数组。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {Boolean} 如果人员拥有角色返回true, 否则返回false。
     * @o2syntax
     * //同步执行，返回判断结果。
     * var groupList = this.org.personHasRole( name, roleList );
     *
     * //异步执行，在回调方法中获取判断结果
     * this.org.personHasRole( name, roleList, function(flag){
     *     //flag 为返回判断结果。
     * })
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {Boolean} 如果群组拥有角色返回true, 否则返回false。
     * @o2syntax
     * //同步执行，返回判断结果。
     * var groupList = this.org.groupHasRole( name, roleList );
     *
     * //异步执行，在回调方法中获取判断结果
     * this.org.groupHasRole( name, roleList, function(flag){
     *     //flag 为返回判断结果。
     * })
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
     * @param {(Boolean)} [async] 当参数为boolean，表示是否异步执行，默认为false。
     * @o2syntax
     * //同步执行
     * this.org.appendPersonAttribute( person, attribute, valueArray);
     *
     * //异步执行
     * this.org.appendPersonAttribute( person, attribute, valueArray, function(){
     *     //执行成功的回调
     * }, null, true);
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
     * @param {(Boolean)} [async] 当参数为boolean，表示是否异步执行，默认为false。
     * @o2syntax
     * //同步执行
     * this.org.setPersonAttribute( person, attribute, valueArray);
     *
     * //异步执行
     * this.org.setPersonAttribute( person, attribute, valueArray, function(){
     *     //执行成功的回调
     * }, null, true);
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {String[]} 返回属性值数组，
     * 如：<pre><code class='language-js'>[ value1, value2 ]</code></pre>
     * @o2syntax
     * //同步执行，返回该人员的属性值数组。
     * var attributeList = this.org.getPersonAttribute( person, attr );
     *
     * //异步执行，在回调方法中获取
     * this.org.getPersonAttribute( person, attr, function(attributeList){
     *     //attributeList 为返回该人员的属性值数组。
     * })
     */

    //列出人员所有属性的名称
    /**
     列出人员所有属性的名称数组。
     * @method listPersonAttributeName
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {String[]} 返回人员属性名称数组，
     * 如：<pre><code class='language-js'>[ attributeName1, attributeName2 ]</code></pre>
     * @o2syntax
     * //同步执行，返回人员所有属性的名称数组。
     * var attributeNameList = this.org.listPersonAttributeName( person );
     *
     * //异步执行，在回调方法中获取
     * this.org.listPersonAttributeName( person, function(attributeNameList){
     *     //attributeNameList 为人员所有属性的名称数组。
     * })
     */

    //列出人员的所有属性
    /**
     列出人员的所有属性对象数组。
     * @method listPersonAllAttribute
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 人员的distinguishedName、name、id、unique属性值，人员对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {Object[]} 返回人员属性对象数组，如：
     * <pre><code class='language-js'>[{
     *    "name": "住址",
     *    "person": "张三@zhangsan@P",
     *    "attributeList": [
     *        "杭州市","绍兴市"
     *    ]
     * }]</code></pre>
     * @o2syntax
     * //同步执行，返回人员所有属性的对象数组。
     * var attributeObjectList = this.org.listPersonAllAttribute( person );
     *
     * //异步执行，在回调方法中获取
     * this.org.listPersonAllAttribute( person, function(attributeObjectList){
     *     //attributeObjectList 为人员所有属性的对象数组。
     * })
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
     * @param {(Boolean)} [async] 当参数为boolean，表示是否异步执行，默认为false。
     * @o2syntax
     * //同步执行
     * this.org.appendUnitAttribute( unit, attribute, valueArray);
     *
     * //异步执行
     * this.org.appendUnitAttribute( unit, attribute, valueArray, function(){
     *     //执行成功的回调
     * }, null, true);
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
     * @param {(Boolean)} [async] 当参数为boolean，表示是否异步执行，默认为false。
     * @o2syntax
     * //同步执行
     * this.org.setUnitAttribute( unit, attribute, valueArray);
     *
     * //异步执行
     * this.org.setUnitAttribute( unit, attribute, valueArray, function(){
     *     //执行成功的回调
     * }, null, true);
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
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {String[]} 返回属性值数组，
     * 如：<pre><code class='language-js'>[ value1, value2 ]</code></pre>
     * @o2syntax
     * //同步执行，返回该组织的属性值数组。
     * var attributeList = this.org.getUnitAttribute( unit, attr );
     *
     * //异步执行，在回调方法中获取
     * this.org.getUnitAttribute( unit, attr, function(attributeList){
     *     //attributeList 为返回该组织的属性值数组。
     * })
     */

    //列出组织所有属性的名称
    /**
     列出组织所有属性的名称数组。
     * @method listUnitAttributeName
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
     * @return {String[]} 返回组织属性名称数组，
     * 如：<pre><code class='language-js'>[ attributeName1, attributeName2 ]</code></pre>
     * @o2syntax
     * //同步执行，返回组织所有属性的名称数组。
     * var attributeNameList = this.org.listUnitAttributeName( unit );
     *
     * //异步执行，在回调方法中获取
     * this.org.listUnitAttributeName( unit, function(attributeNameList){
     *     //attributeNameList 为组织所有属性的名称数组。
     * })
     */

    //列出组织的所有属性
    /**
     列出组织的所有属性对象数组。
     * @method listUnitAllAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 组织的distinguishedName、name、id、unique属性值，组织对象，或上述属性值和对象的数组。
     * @param {(Boolean|Function)} [asyncOrCallback] 当参数为boolean，表示是否异步执行，默认为false。当参数为function，表示回调方法。
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
     * //同步执行，返回组织所有属性的对象数组。
     * var attributeObjectList = this.org.listUnitAllAttribute( unit );
     *
     * //异步执行，在回调方法中获取
     * this.org.listUnitAllAttribute( unit, function(attributeObjectList){
     *     //attributeObjectList 为组织所有属性的对象数组。
     * })
     */


//dict
/**
 * this.Dict是一个工具类，如果您在流程、门户中创建了数据字典，可以使用this.Dict类对数据字典进行增删改查操作。
 * @module server.Dict
 * @o2category server
 * @o2ordernumber 120
 * @param {(String|Object)} optionsOrName 数据字典标识字符串或者是对象。
 * <div>如果对本应用的数据字典操作，将optionsOrName设置为string。</div>
 * <pre><code class='language-js'>var dict = new this.Dict("bulletinDictionary"); //数据字典的名称、别名或id
 * </code></pre>
 * <div>如果需要对其他应用的数据字典进行操作，将options设置为JsonObject</div>
 * <pre><code class='language-js'>var dict = new this.Dict({
 *     //type: 应用类型。可以为process  cms。
 *     //如果没有该选项或者值为空字符串，则表示应用脚本和被应用的脚本配置类型相同。
 *     //比如在流程的A应用脚本中引用流程B应用的脚本配置，则type可以省略。
 *    type : "cms",
 *    application : "bulletin", //流程、CMS的名称、别名、id, 默认为当前应用
 *    name : "bulletinDictionary", // 数据字典的名称、别名、id
 *    enableAnonymous : true //允许用户在未登录的情况下读取cms的数据字典, type为process的时候此参数无效，默认为false
 * });
 * </code></pre>
 * @return {Object} Dict对象
 * @o2syntax
 * //您可以在页面、表单、流程各个嵌入脚本中，通过this.Dict()对本应用或其他应用的数据字典中的数据进行增删改查，如下：
 * var dict = new this.Dict( options )
 * @borrows module:Dict#add as add
 * @borrows module:Dict#set as set
 * @borrows module:Dict#delete as delete
 */
/**
 * 根据路径获取数据字典中的数据。
 * @method get
 * @methodOf module:server.Dict
 * @static
 * @param {String} [path] 数据字典中的数据路径，允许使用中文。当路径为多级时，用点号(.)分隔。当值为空的时候，表示获取数据字典中的所有数据。
 * @param {Function} [success] 获取数据成功时的回调函数。
 * @param {Function} [failure] 获取数据失败时的回调。
 * @param {Boolean} [async] 是否异步执行，默认为false。
 * @param {Boolean} [refresh] 是否忽略本地缓存直接从服务器获取，默认为false。
 * @return {(Object|Array|String|Number|Boolean)}
 * 返回数据字典的数据，类型和配置数据字典时候指定的一致。
 * @o2syntax
 * var data = dict.get( path, success, failure, async, refresh )
 * @example
 * var dict = new this.Dict("bulletinDictionary");
 *
 * //没有参数的时候，表示同步获取获取所有数据
 *  var data = dict.get()
 *
 *  //同步执行，获取category下key为subCategory的数据
 *  var data = dict.get("category.subCategory");
 *
 * //异步执行，使用回调处理数据，如果category为数组，获取第0项数据
 * dict.get("category.0", function(data){
 *      //data 是数据字典的数据
 *  }, function(xhr){
 *      //xhr 为 xmlHttpRequest
 *  }, true //异步执行
 * )
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
 *    enableAnonymous : true //允许用户在未登录的情况下读取cms的数据字典, type为process的时候此参数无效，默认为false
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
 * 平台预置了Actions对象用于调用平台提供的服务，您可以使用this.Actions.load来获取这些方法。
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
 *  this.Actions.load( root )[actionName][methodName]( arguements );
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
 *
 *      async : 方法同步或者异步执行，默认为true。
 *  @o2syntax
 *  <caption>
 *  处理返回的数据：<br/>
 *  通过success方法作为第一个参数来处理结果
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
 * //执行方法
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
 *  },
 *  true //可选，true为异步调用，false为同步调用，默认为true
 * );
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
 *      }.bind(this),
 *      false //同步执行
 * )
 *
 * data.subject = "新标题"; //修改数据
 * this.Actions.load("x_processplatform_assemble_surface").DataAction.updateWithWork(
 *      workid, //uri的参数
 *      data, //保存的数据
 *      function(){ //服务调用成功的回调函数
 *          o2.xDesktop.notice("success", {"y":"top", "x": "right"}, "保存成功");  //提示，{"y":"top", "x": "right"}指提示框在顶部右边
 *      }.bind(this)
 * );
 */