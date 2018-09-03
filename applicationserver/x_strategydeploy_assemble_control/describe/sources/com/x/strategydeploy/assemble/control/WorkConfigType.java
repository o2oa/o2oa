package com.x.strategydeploy.assemble.control;

public enum WorkConfigType {
/*
 * 正常状态(abled)，挂起状态(hang)，作废状态(disabled)，软删除(softdelete),未知状态(unknown)
 * 
 * abled:可以编辑，可以被下级配置中选择；可以在已配置的文档中正常读取、展示；可以在已配置的文档中取消关联，不可以被删除掉。
 * hang:不可以编辑，不可以被下级配置中选择；可以在已配置的文档中正常读取、展示；不可以在已配置的文档中取消关联，不可以被删除掉。
 * disabled:不可以编辑，不可以被下级配置中选择；不可以在已配置的文档中正常读取、展示；不可以在已配置的文档中取消关联，不可以被删除掉。
 * softdelete:可以编辑，不可以被下级配置中选择；不可以在已配置的文档中正常读取、展示；不可以在已配置的文档中取消关联，可以被删除掉。
 * unknown:可以编辑，不可以被下级配置中选择；不可以在已配置的文档中正常读取、展示；不可以在已配置的文档中取消关联，不可以被删除掉。
 * */
	abled,hang,disabled,softdelete,unknown
}