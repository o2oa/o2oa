package com.x.calendar.assemble.control.factory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.core.entity.Calendar_Event;
import com.x.calendar.core.tools.LogUtil;

public class MessageFactory {

	public static void send_alarm( EntityManagerContainer emc, Calendar_Event event ) throws Exception {
		LogUtil.INFO( "send calendar alarm message", event.getTitle() + ", StartTime: " + event.getStartTimeStr() );
		if( ListTools.isNotEmpty( event.getParticipants() )) {
			for( String participant : event.getParticipants() ) {
				MessageConnector.send( MessageConnector.TYPE_CALENDAR_ALARM, event.getValarm_Summary(), participant, event );
			}
		}
		
		event = emc.find( event.getId(), Calendar_Event.class );
		emc.beginTransaction( Calendar_Event.class );
		event.setAlarmAlready( true );
		emc.check( event, CheckPersistType.all );
		emc.commit();
	}
	
}