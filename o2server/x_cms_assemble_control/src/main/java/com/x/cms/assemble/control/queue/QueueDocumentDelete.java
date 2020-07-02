package com.x.cms.assemble.control.queue;

import com.x.base.core.project.x_hotpic_assemble_control;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.cms.assemble.control.ThisApplication;

/**
 * Document删除时也需要检查一下热点图片里的数据是否已经删除掉了
 *
 */
public class QueueDocumentDelete extends AbstractQueue<String> {
	
	//{application}/{infoId}
	public void execute( String docId ) throws Exception {
		try {
			ThisApplication.context().applications().deleteQuery( x_hotpic_assemble_control.class, "cipher/hotpic/cms/" + docId );
		}catch( Exception e ) {
			e.printStackTrace();
		}		
	}
}
