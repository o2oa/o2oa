/**
 * 论坛角色信息管理
 *
 * 论坛角色>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 * 论坛访客(FORUM_GUEST):论坛访客
 * 		0、论坛可见（FORUM_VIEW）:用户可以BBS系统中访问该论坛
 *
 * 论坛管理员(FORUM_SUPER_MANAGER):论坛管理员，拥有该论坛管理的最大权限
 *      0、论坛可见（FORUM_VIEW）:用户可以BBS系统中访问该论坛
 * 		1、论坛信息管理（FORUM_INFO_MANAGEMENT）：用户拥有对论坛的版块增加，删除，修改权限
 * 		1、论坛发布主题（FORUM_SUBJECT_PUBLISH）:用户可以在论坛中所有版块发布主题
 * 		2、论坛发表回复（FORUM_REPLY_PUBLISH）：用户可以回复论坛中所有主题
 * 		3、论坛主题推荐（FORUM_SUBJECT_RECOMMEND）:用户可以在指定论坛中所有版块对指定主题进行推荐到论坛首页
 * 		4、论坛主题置顶（FORUM_SUBJECT_STICK）：用户拥有对论坛中所有的主题的置顶权限
 * 		5、论坛主题申精（FORUM_SUBJECT_CREAM）：用户拥有对论坛中所有的主题的精华主题设置权限
 * 		6、论坛主题管理（FORUM_SUBJECT_MANAGEMENT）：用户拥有对论坛中所有的主题的锁定删除权限
 * 		7、论坛回贴管理（FORUM_REPLY_MANAGEMENT）：用户拥有对论坛中所有的回复的删除权限
 * 		8、论坛权限管理（FORUM_PERMISSION_MANAGEMENT）：用户拥有对论坛的用户进行该论坛权限设置的权限
 * 		9、论坛配置管理（FORUM_CONFIG_MANAGEMENT）：用户拥有对论坛的参数配置进行设置的权限
 *
 *
 * 版块角色>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 * 版块访客(SECTION_GUEST):版块访客
 * 		0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 *
 * 版块主(SECTION_MANAGER):拥有版块及版块内容管理的最大权限
 *      0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 * 		1、发布主题（SECTION_SUBJECT_PUBLISH）:用户可以在指定版块中发布主题
 *		2、审核主题（SECTION_SUBJECT_AUDIT）:用户可以审核在指定版块中发布的主题，如果主题需要审核
 * 		3、主题管理（SECTION_SUBJECT_MANAGEMENT）:用户可以在指定版块中对已发布主题进行查询删除
 * 		4、发表回复（SECTION_REPLY_PUBLISH）:用户可以在指定版块中对所有主题进行回复
 *		5、审核回复（SECTION_REPLY_AUDIT）:用户可以审核在指定版块中的所有回复内容，如果回复需要审核
 *		6、回贴管理（SECTION_REPLY_MANAGEMENT）:用户可以在指定版块中对回复进行查询或者删除
 * 		7、版块主题推荐（SECTION_SUBJECT_RECOMMEND）:用户可以在指定版块中对指定主题进行推荐操作
 * 		8、版块主题置顶（SECTION_SUBJECT_STICK）:用户可以在指定版块中对指定主题进行置顶操作
 * 		9、版块主题申精（SECTION_SUBJECT_CREAM）:用户可以在指定版块中对指定主题进行精华主题设置操作
 * 		10、版块权限管理（SECTION_PERMISSION_MANAGEMENT）:用户可以对论坛用户进行指定版块的权限管理
 * 		11、版块配置管理（SECTION_CONFIG_MANAGEMENT）:用户可以对指定版块进行系统参数配置修改
 *
 * 主题发布者(SECTION_SUBJECT_PUBLISHER):允许在指定版块内发布主题
 * 		0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 * 		1、发布主题（SECTION_SUBJECT_PUBLISH）:用户可以在指定版块中发布主题
 *
 * 主题回复者(SECTION_REPLY_PUBLISHER):允许在指定版块内发布主题
 * 		0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 * 		1、发表回复（SECTION_REPLY_PUBLISH）:用户可以在指定版块中对所有主题进行回复
 *
 * 主题推荐者(SECTION_RECOMMENDER):允许在指定版块内发布主题
 * 		0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 * 		1、版块主题推荐（SECTION_SUBJECT_RECOMMEND）:用户可以在指定版块中对指定主题进行推荐操作
 *
 * 主题审核者(SECTION_SUBJECT_AUDITOR):允许在指定版块内发布主题
 * 		0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 * 		1、审核主题（SECTION_SUBJECT_AUDIT）:用户可以审核在指定版块中发布的主题，如果主题需要审核
 *
 * 回复审核者(SECTION_REPLY_AUDITOR):允许在指定版块内发布主题
 * 		0、论坛可见（SECTION_VIEW）:用户可以BBS系统中访问该版块
 * 		1、审核回复（SECTION_REPLY_AUDIT）:用户可以审核在指定版块中的所有回复内容，如果回复需要审核 *
 *
 * @author LIYI
 *
 */