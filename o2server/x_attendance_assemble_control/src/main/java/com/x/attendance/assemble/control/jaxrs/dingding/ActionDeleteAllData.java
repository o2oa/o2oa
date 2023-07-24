package com.x.attendance.assemble.control.jaxrs.dingding;


import java.util.List;

import com.x.attendance.entity.AttendanceDingtalkDetail;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionDeleteAllData extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionDeleteAllData.class);


    public ActionResult<WrapBoolean> execute(EffectivePerson effectivePerson) {
        ActionResult<WrapBoolean> result = new ActionResult<>();

        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<AttendanceDingtalkDetail> details  = emc.listAll(AttendanceDingtalkDetail.class);
            if ( null == details ) {
                result.error( new FindEmptyException() );
            }else{
                //进行数据库持久化操作
                emc.beginTransaction( AttendanceDingtalkDetail.class );
                for (AttendanceDingtalkDetail d : details) {
                    emc.remove(d);
                }
                emc.commit();
                result.setData(new WrapBoolean(true));
                logger.info( "成功删除所有的钉钉打卡数据信息"  );
            }
        } catch ( Exception e ) {
            result.error(e);
            logger.error(e);
        }

        return result;
    }


}
