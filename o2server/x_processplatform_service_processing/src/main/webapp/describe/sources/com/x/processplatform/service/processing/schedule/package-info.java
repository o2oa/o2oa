
/**
 * ClearOrphanTask:查找所有task中所属于的activity已经不存在的task.<br>
 * ClearOrphanWork:查找所有work中所属于的process和application已经不存在的work.<br>
 * DelayTrigger:定时推动延时节点的work.<br>
 * ScanExpire: 扫描已经过期的task<br>
 * ScanOrphanActivity:扫描work所在的activity已经不存在的work<br>
 * ScanTrigger:尝试触发work流转.<br>
 * ScanUrge:触发催办提醒<br>
 * 
 * @author zhour
 *
 */
package com.x.processplatform.service.processing.schedule;