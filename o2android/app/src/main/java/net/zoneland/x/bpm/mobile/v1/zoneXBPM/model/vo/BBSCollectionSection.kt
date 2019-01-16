package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.BBSCollectionRealmObject

/**
 * Created by fancy on 2017/4/11.
 */


data class BBSCollectionSectionVO(
        var id: String = "",
        var sectionName: String ="",
        var sectionIcon: String ="",
        var createTime: Long = 0L
) {
    fun copyFromPersistence(o: BBSCollectionRealmObject) : BBSCollectionSectionVO {
        return BBSCollectionSectionVO(o.id, o.sectionName, o.sectionIcon, createTime)
    }
}