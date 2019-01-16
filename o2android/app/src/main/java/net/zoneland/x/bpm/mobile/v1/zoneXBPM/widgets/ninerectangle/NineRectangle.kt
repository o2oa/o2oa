package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.ninerectangle

import android.graphics.Bitmap
import android.graphics.PointF
import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.BitmapUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import java.util.*

/**
 * 仿微信群聊 组合头像
 * Created by FancyLou on 2016/1/5.
 */
class NineRectangle {

    private var outWidth = 200f
    private var outHeight = 200f

    constructor() {}

    constructor(rectDimension: Float) {
        this.outWidth = rectDimension
        this.outHeight = rectDimension
    }


    /**
     * 生成组合头像
     * @param bitmaps
     * @return
     */
    fun getNineRectCombineBitmap(bitmaps: List<Bitmap>?): Bitmap? {
        var bitmaps = bitmaps
        if (bitmaps == null || bitmaps.size < 1 || bitmaps.size > 9) {
            XLog.error("bitmaps is null or size error")
            return null
        }
        var mEntityList = getEntityList(bitmaps.size)
        var newBitmap: Bitmap? = Bitmap.createBitmap(outWidth.toInt(), outHeight.toInt(), Bitmap.Config.ARGB_8888)
        for (i in mEntityList!!.indices) {
            var bitmapI: Bitmap? = BitmapUtil.zoomBitmap(bitmaps[i], mEntityList[i].width.toInt(), mEntityList[i].height.toInt())
            newBitmap = BitmapUtil.mixtureBitmap(newBitmap, bitmapI, PointF(
                    mEntityList[i].x, mEntityList[i].y))
            bitmapI!!.recycle()
            bitmaps[i].recycle()
            bitmapI = null
        }
        bitmaps = null
        mEntityList = null
        return newBitmap
    }

    /**
     *
     * @param size 头像个数1-9
     * @return BitmapEntity组成的坐标系 字符串   例：51.0,1.0,98.0,98.0;1.0,101.0,98.0,98.0;101.0,101.0,98.0,98.0;
     */
    fun getBitmapCoordinates(size: Int): String? {
        if (size < 1 || size > 9) {
            return null
        }
        val mCRC = generateColumnRowCountByCount(size)
        var bitmap: BitmapEntity? = null
        val perBitmapWidth = (outWidth - BitmapEntity.devide * 2 * mCRC.columns) / mCRC.columns
        val topDownDelta = outHeight - mCRC.rows * (perBitmapWidth + BitmapEntity.devide * 2)
        val mList = LinkedList<BitmapEntity>()
        for (row in 0 until mCRC.rows) {
            for (column in 0 until mCRC.columns) {
                bitmap = BitmapEntity()
                bitmap.y = 1f + topDownDelta / 2 + (row * 2).toFloat() + row * perBitmapWidth
                bitmap.x = 1f + (column * 2).toFloat() + column * perBitmapWidth
                bitmap.width = perBitmapWidth
                bitmap.height = perBitmapWidth
                mList.add(bitmap)
            }
        }
        when (size) {
            3 -> {
                val mBitmap1 = mList[0]
                val mBitmap2 = mList[1]
                val mDesBitmap = BitmapEntity()
                mDesBitmap.y = mBitmap1.y
                mDesBitmap.x = (mBitmap1.x + mBitmap2.x) / 2
                mDesBitmap.width = mBitmap1.width
                mDesBitmap.height = mBitmap1.height
                mList[0] = mDesBitmap
                mList.removeAt(1)
            }
            5, 8 -> {
                val mBitmap1 = mList[0]
                val mBitmap2 = mList[1]
                val mBitmap3 = mList[2]
                val mDesBitmap1 = BitmapEntity()
                mDesBitmap1.y = mBitmap1.y
                mDesBitmap1.x = (mBitmap1.x + mBitmap2.x) / 2
                mDesBitmap1.width = mBitmap1.width
                mDesBitmap1.height = mBitmap1.height

                val mDesBitmap2 = BitmapEntity()
                mDesBitmap2.x = (mBitmap2.x + mBitmap3.x) / 2
                mDesBitmap2.width = mBitmap2.width
                mDesBitmap2.height = mBitmap2.height
                mList[0] = mDesBitmap1
                mList[1] = mDesBitmap2
                mList.removeAt(2)
            }
            7 -> {
                mList.removeAt(0)
                mList.removeAt(2)
            }
        }

        val buffer = StringBuffer()
        for (i in mList.indices) {
            val entity = mList[i]
            buffer.append(entity.x)
            buffer.append(",")
            buffer.append(entity.y)
            buffer.append(",")
            buffer.append(entity.width)
            buffer.append(",")
            buffer.append(entity.height)
            if (i != mList.size - 1) {
                buffer.append(";")
            }
        }
        return buffer.toString()
    }


    private fun getEntityList(size: Int): List<BitmapEntity>? {
        if (size < 1 || size > 9) {
            return null
        }
        val list = ArrayList<BitmapEntity>(size)
        val string = getBitmapCoordinates(size)
        if (!TextUtils.isEmpty(string)) {
            val arr1 = string!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in arr1.indices) {
                val content = arr1[i]
                val arr2 = content.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val entity = BitmapEntity()
                entity.x = java.lang.Float.valueOf(arr2[0])
                entity.y = java.lang.Float.valueOf(arr2[1])
                entity.width = java.lang.Float.valueOf(arr2[2])
                entity.height = java.lang.Float.valueOf(arr2[3])
                list.add(entity)
            }
        }
        return list
    }


    private inner class ColumnRowCount(internal var rows: Int, internal var columns: Int, internal var count: Int) {

        override fun toString(): String {
            return ("ColumnRowCount [rows=" + rows + ", columns=" + columns
                    + ", count=" + count + "]")
        }
    }

    private fun generateColumnRowCountByCount(count: Int): ColumnRowCount {
        when (count) {
            2 -> return ColumnRowCount(1, 2, count)
            3, 4 -> return ColumnRowCount(2, 2, count)
            5, 6 -> return ColumnRowCount(2, 3, count)
            7, 8, 9 -> return ColumnRowCount(3, 3, count)
            else -> return ColumnRowCount(1, 1, count)
        }

    }


}
