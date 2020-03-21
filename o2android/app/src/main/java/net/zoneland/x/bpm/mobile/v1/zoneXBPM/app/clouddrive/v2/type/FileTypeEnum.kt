package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.type


/**
 * 云盘 文档类型
 */
enum class FileTypeEnum(val key: String, val cn: String) {
    image("image", "图片"),
    office("office", "文档"),
    movie("movie", "视频"),
    music("music", "音乐"),
    other("other", "其它")

}