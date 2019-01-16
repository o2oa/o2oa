package jiguang.chat.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jiguang.chat.utils.keyboard.data.EmoticonEntity;
import jiguang.chat.utils.keyboard.data.EmoticonPageEntity;
import jiguang.chat.utils.keyboard.data.EmoticonPageSetEntity;


public class XmlUtil {

    Context mContext;

    public XmlUtil(Context context) {
        this.mContext = context;
    }

    public InputStream getXmlFromAssets(String xmlName) {
        try {
            InputStream inStream = this.mContext.getResources().getAssets().open(xmlName);
            return inStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public InputStream getXmlFromSD(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream inStream = new FileInputStream(file);
                return inStream;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EmoticonPageSetEntity<EmoticonEntity> ParserXml(String filePath, InputStream inStream) {

        String arrayParentKey = "EmoticonBean";
        boolean isChildCheck = false;

        EmoticonPageSetEntity.Builder<EmoticonEntity> emoticonPageSetEntity = new EmoticonPageSetEntity.Builder<>();
        ArrayList<EmoticonEntity> emoticonList = new ArrayList<>();
        emoticonPageSetEntity.setEmoticonList(emoticonList);
        EmoticonEntity emoticonBeanTemp = null;

        if (null != inStream) {
            XmlPullParser pullParser = Xml.newPullParser();
            try {
                pullParser.setInput(inStream, "UTF-8");
                int event = pullParser.getEventType();

                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {

                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            String skeyName = pullParser.getName();

                            /**
                             * EmoticonBeans data
                             */
                            if (isChildCheck && emoticonBeanTemp != null) {
                                if (skeyName.equals("eventType")) {
                                    try {
                                        String value = pullParser.nextText();
                                        emoticonBeanTemp.setEventType(Integer.parseInt(value));
                                    } catch (NumberFormatException e) {
                                    }
                                } else if (skeyName.equals("iconUri")) {
                                    String value = pullParser.nextText();
                                    emoticonBeanTemp.setIconUri("file://" + filePath + "/" + value);
                                } else if (skeyName.equals("content")) {
                                    String value = pullParser.nextText();
                                    emoticonBeanTemp.setContent(value);
                                }
                            }
                            /**
                             * EmoticonSet data
                             */
                            else {
                                try {
                                    if (skeyName.equals("name")) {
                                        String value = pullParser.nextText();
                                        emoticonPageSetEntity.setSetName(value);
                                    } else if (skeyName.equals("line")) {
                                        String value = pullParser.nextText();
                                        emoticonPageSetEntity.setLine(Integer.parseInt(value));
                                    } else if (skeyName.equals("row")) {
                                        String value = pullParser.nextText();
                                        emoticonPageSetEntity.setRow(Integer.parseInt(value));
                                    } else if (skeyName.equals("iconUri")) {
                                        String value = pullParser.nextText();
                                        emoticonPageSetEntity.setIconUri(value);
                                    } else if (skeyName.equals("isShowDelBtn")) {
                                        String value = pullParser.nextText();
                                        EmoticonPageEntity.DelBtnStatus delBtnStatus;
                                        if (!TextUtils.isEmpty(value) && Integer.parseInt(value) == 1) {
                                            delBtnStatus = EmoticonPageEntity.DelBtnStatus.FOLLOW;
                                        } else if (!TextUtils.isEmpty(value) && Integer.parseInt(value) == 2) {
                                            delBtnStatus = EmoticonPageEntity.DelBtnStatus.LAST;
                                        } else {
                                            delBtnStatus = EmoticonPageEntity.DelBtnStatus.GONE;
                                        }
                                        emoticonPageSetEntity.setShowDelBtn(delBtnStatus);
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (skeyName.equals(arrayParentKey)) {
                                isChildCheck = true;
                                emoticonBeanTemp = new EmoticonEntity();
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            String ekeyName = pullParser.getName();
                            if (isChildCheck && ekeyName.equals(arrayParentKey)) {
                                isChildCheck = false;
                                emoticonList.add(emoticonBeanTemp);
                            }
                            break;
                        default:
                            break;
                    }
                    event = pullParser.next();
                }
                return new EmoticonPageSetEntity(emoticonPageSetEntity);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new EmoticonPageSetEntity(emoticonPageSetEntity);
    }
}
