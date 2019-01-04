package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.ninerectangle

/**
 * Created by FancyLou on 2016/1/5.
 */
class BitmapEntity {

    var x: Float = 0.toFloat()
    var y: Float = 0.toFloat()
    var width: Float = 0.toFloat()
    var height: Float = 0.toFloat()
    var index = -1

    override fun toString(): String {
        return ("MyBitmap [x=" + x + ", y=" + y + ", width=" + width
                + ", height=" + height + ", devide=" + devide + ", index="
                + index + "]")
    }

    companion object {

        var devide = 1
    }
}
