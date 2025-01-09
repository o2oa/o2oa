package com.x.message.assemble.communicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.IMMsg;
import com.x.message.core.entity.IMMsgCollection;
import com.x.message.core.entity.IMMsgFile;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class IMMessageDeleteQueue extends AbstractQueue<IMMsg> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IMMessageDeleteQueue.class);

    @Override
    protected void execute(IMMsg imMsg) throws Exception {
        if (StringUtils.isEmpty(imMsg.getId())) {
            LOGGER.warn("没有传入 id， 无法删除消息");
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("开始删除消息， id {}", imMsg.getId());
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            IMMsg message = emc.find(imMsg.getId(), IMMsg.class);
            if (null == message) {
                LOGGER.info("没有找到消息对象 {}", imMsg.getId());
                return;
            }
            // 删除收藏的消息
            List<IMMsgCollection> collections = business.imConversationFactory()
                    .listCollectionByPersonAndMsgId(null, message.getId());
            if (null != collections && !collections.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("有相关联的收藏消息数据一起删除， collections {}",
                            collections.size());
                }
                for (IMMsgCollection collection : collections) {
                    emc.beginTransaction(IMMsgCollection.class);
                    emc.remove(collection);
                    emc.commit();
                }
            }
            // 删除消息对应的文件 如果有
            String fileId = message.getBodyFileId();
            // 先删除消息本身
            emc.beginTransaction(IMMsg.class);
            emc.remove(message);
            emc.commit();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("删除消息完成， id {}", imMsg.getId());
            }
            if (StringUtils.isNotEmpty(fileId)) { // 消息是带文件的 删除对应的文件和数据
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("消息是带文件的 查询是否是空闲文件对象， fileId {}", fileId);
                }
                // 先查询是否还有更多的关联这个文件的消息
                List<IMMsg> list = business.imConversationFactory().listMessageByFileId(fileId);
                if (null == list || list.isEmpty()) { // 没有关联数据 删除文件对象
                    IMMsgFile file = emc.find(fileId, IMMsgFile.class);
                    if (null != file) {
                        StorageMapping mapping = ThisApplication.context().storageMappings()
                                .get(IMMsgFile.class, file.getStorage());
                        if (null != mapping) {
                            file.deleteContent(mapping);
                        }
                        emc.beginTransaction(IMMsgFile.class);
                        emc.delete(IMMsgFile.class, file.getId());
                        emc.commit();
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(" 删除对应的文件和数据， fileId {}", fileId);
                        }
                    }
                }
            }
        }
    }


}
