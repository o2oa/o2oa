package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R

object O2IM {

    const val IM_Message_Receiver_Action = "net.o2oa.android.im.message"
    const val IM_Message_Receiver_name = "IM_Message_Receiver_name"

    const val conversation_type_single = "single"
    const val conversation_type_group = "group"

    val im_emoji_hashMap = hashMapOf<String, Int>(
            "[01]" to R.mipmap.im_emotion_01,
            "[02]" to R.mipmap.im_emotion_02,
            "[03]" to R.mipmap.im_emotion_03,
            "[04]" to R.mipmap.im_emotion_04,
            "[05]" to R.mipmap.im_emotion_05,
            "[06]" to R.mipmap.im_emotion_06,
            "[07]" to R.mipmap.im_emotion_07,
            "[08]" to R.mipmap.im_emotion_08,
            "[09]" to R.mipmap.im_emotion_09,
            "[10]" to R.mipmap.im_emotion_10,
            "[11]" to R.mipmap.im_emotion_11,
            "[12]" to R.mipmap.im_emotion_12,
            "[13]" to R.mipmap.im_emotion_13,
            "[14]" to R.mipmap.im_emotion_14,
            "[15]" to R.mipmap.im_emotion_15,
            "[16]" to R.mipmap.im_emotion_16,
            "[17]" to R.mipmap.im_emotion_17,
            "[18]" to R.mipmap.im_emotion_18,
            "[19]" to R.mipmap.im_emotion_19,
            "[20]" to R.mipmap.im_emotion_20,
            "[21]" to R.mipmap.im_emotion_21,
            "[22]" to R.mipmap.im_emotion_22,
            "[23]" to R.mipmap.im_emotion_23,
            "[24]" to R.mipmap.im_emotion_24,
            "[25]" to R.mipmap.im_emotion_25,
            "[26]" to R.mipmap.im_emotion_26,
            "[27]" to R.mipmap.im_emotion_27,
            "[28]" to R.mipmap.im_emotion_28,
            "[29]" to R.mipmap.im_emotion_29,
            "[30]" to R.mipmap.im_emotion_30,
            "[31]" to R.mipmap.im_emotion_31,
            "[32]" to R.mipmap.im_emotion_32,
            "[33]" to R.mipmap.im_emotion_33,
            "[34]" to R.mipmap.im_emotion_34,
            "[35]" to R.mipmap.im_emotion_35,
            "[36]" to R.mipmap.im_emotion_36,
            "[37]" to R.mipmap.im_emotion_37,
            "[38]" to R.mipmap.im_emotion_38,
            "[39]" to R.mipmap.im_emotion_39,
            "[40]" to R.mipmap.im_emotion_40,
            "[41]" to R.mipmap.im_emotion_41,
            "[42]" to R.mipmap.im_emotion_42,
            "[43]" to R.mipmap.im_emotion_43,
            "[44]" to R.mipmap.im_emotion_44,
            "[45]" to R.mipmap.im_emotion_45,
            "[46]" to R.mipmap.im_emotion_46,
            "[47]" to R.mipmap.im_emotion_47,
            "[48]" to R.mipmap.im_emotion_48,
            "[49]" to R.mipmap.im_emotion_49,
            "[50]" to R.mipmap.im_emotion_50,
            "[51]" to R.mipmap.im_emotion_51,
            "[52]" to R.mipmap.im_emotion_52,
            "[53]" to R.mipmap.im_emotion_53,
            "[54]" to R.mipmap.im_emotion_54,
            "[55]" to R.mipmap.im_emotion_55,
            "[56]" to R.mipmap.im_emotion_56,
            "[57]" to R.mipmap.im_emotion_57,
            "[58]" to R.mipmap.im_emotion_58,
            "[59]" to R.mipmap.im_emotion_59,
            "[60]" to R.mipmap.im_emotion_60,
            "[61]" to R.mipmap.im_emotion_61,
            "[62]" to R.mipmap.im_emotion_62,
            "[63]" to R.mipmap.im_emotion_63,
            "[64]" to R.mipmap.im_emotion_64,
            "[65]" to R.mipmap.im_emotion_65,
            "[66]" to R.mipmap.im_emotion_66,
            "[67]" to R.mipmap.im_emotion_67,
            "[68]" to R.mipmap.im_emotion_68,
            "[69]" to R.mipmap.im_emotion_69,
            "[70]" to R.mipmap.im_emotion_70,
            "[71]" to R.mipmap.im_emotion_71,
            "[72]" to R.mipmap.im_emotion_72,
            "[73]" to R.mipmap.im_emotion_73,
            "[74]" to R.mipmap.im_emotion_74,
            "[75]" to R.mipmap.im_emotion_75,
            "[76]" to R.mipmap.im_emotion_76,
            "[77]" to R.mipmap.im_emotion_77,
            "[78]" to R.mipmap.im_emotion_78,
            "[79]" to R.mipmap.im_emotion_79,
            "[80]" to R.mipmap.im_emotion_80,
            "[81]" to R.mipmap.im_emotion_81,
            "[82]" to R.mipmap.im_emotion_82,
            "[83]" to R.mipmap.im_emotion_83,
            "[84]" to R.mipmap.im_emotion_84,
            "[85]" to R.mipmap.im_emotion_85,
            "[86]" to R.mipmap.im_emotion_86,
            "[87]" to R.mipmap.im_emotion_87
    )

    fun emojiResId(key: String) :Int {
        return im_emoji_hashMap[key] ?: R.mipmap.im_emotion_01
    }

}