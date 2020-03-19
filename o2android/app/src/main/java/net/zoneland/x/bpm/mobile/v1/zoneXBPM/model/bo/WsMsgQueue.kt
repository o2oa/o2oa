package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo

import java.util.*


class WsMsgQueue<T> {
    private val list: LinkedList<T> = LinkedList()

    fun add(o: T) {
        list.addFirst(o)
    }

    fun get(): T? {
        return if (list.isNotEmpty()) {
            list.removeLast()
        }else {
            null
        }
    }

    fun size(): Int {
        return list.size
    }
}