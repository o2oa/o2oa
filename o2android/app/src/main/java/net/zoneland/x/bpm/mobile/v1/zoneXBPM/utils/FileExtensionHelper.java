package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import android.text.TextUtils;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.File;
import java.util.UUID;

/**
 * Created by FancyLou on 2015/11/25.
 */
public class FileExtensionHelper {

    /*****************************************base folder*********************************************/
    public static String getXBPMBaseFolder() {
        return SDCardHelper.INSTANCE.getSdCardPath() + File.separator + O2.INSTANCE.getBASE_FILE_PATH();
    }

    /**
     * 临时目录
     * @return
     */
    public static String getXBPMTempFolder() {
        return getXBPMBaseFolder() + File.separator + O2.INSTANCE.getBASE_TMP_FOLDER();
    }

    /**
     * liaot
     * @return
     */
    public static String getXBPMIMReciFolder() {
        return getXBPMBaseFolder() + File.separator + O2.INSTANCE.getBASE_IM_RECI_FOLDER();
    }

    /**
     * 日志目录
     * @return
     */
    public static String getXBPMLogFolder() {
        return getXBPMBaseFolder() + File.separator + O2.INSTANCE.getBASE_LOG_FOLDER();
    }

    /**
     * 流程平台附件临时目录
     * @return
     */
    public static String getXBPMWORKAttachmentFolder() {
        return getXBPMBaseFolder() + File.separator + O2.INSTANCE.getBASE_WORK_ATTACH_FOLDER();
    }

    /**
     * 论坛附件下载目录
     * @return
     */
    public static String getXBPMBBSAttachFolder() {
        return getXBPMBaseFolder() + File.separator + O2.INSTANCE.getBASE_BBS_ATTACH_FOLDER();
    }

    /**
     * 会议附件下载目录
     * @return
     */
    public static String getXBPMMEETINGAttachFolder() {
        return getXBPMBaseFolder() + File.separator + O2.INSTANCE.getBASE_MEETING_ATTACH_FOLDER();
    }

    /**
     * 内容附件下载目录
     * @return
     */
    public static String getXBPMCMSAttachFolder() {
        return getXBPMBaseFolder() + File.separator + O2.INSTANCE.getBASE_CMS_ATTACH_FOLDER();
    }

    /**
     * 头像缓存目录
     * @return
     */
    public static String getXBPMAvatarTempFolder() {
        return getXBPMBaseFolder() + File.separator + O2.INSTANCE.getAVATAR_TMP_FOLDER();
    }

    /**
     * 存放皮肤文件的目录
     * @return
     */
    public static String getSkinFileTempFolder() {
        return getXBPMBaseFolder() + File.separator + O2.INSTANCE.getSKIN_FILE_FOLDER();
    }





    /*****************************************************************************************/

    /**
     *
     * @param skin
     * @return
     */
    public static String generateSkinFilePath(String skin) {
        return getSkinFileTempFolder() + File.separator + skin;
    }

    /**
     * 签名图片临时地址
     * @return
     */
    public static String generateSignTempFilePath() {
        return getXBPMTempFolder() + File.separator + UUID.randomUUID().toString() + O2.INSTANCE.getIMAGE_SUFFIX_PNG();
    }

    /**
     * 根据姓名生成图片存储路径
     *
     * @return
     */
    public static String generateAvatarFilePath() {
        String avatar_icon_file_name = UUID.randomUUID().toString();
        String imageFilePath = getXBPMAvatarTempFolder() + File.separator + avatar_icon_file_name + O2.INSTANCE.getIMAGE_SUFFIX_PNG();
        return imageFilePath;
    }

    /**
     * BBS上传文件压缩后使用的临时文件路径
     * @return
     */
    public static String generateBBSTempFilePath() {
        String randomId = UUID.randomUUID().toString();
        return getXBPMTempFolder() + File.separator + randomId + O2.INSTANCE.getIMAGE_SUFFIX_PNG();
    }

    /**
     * 生成广告热图图片地址 id作为图片地址
     * @param id
     * @return
     */
    public static String generateHotPicturePath(String id) {
        String imageFilePath = getXBPMAvatarTempFolder() + File.separator + id + O2.INSTANCE.getIMAGE_SUFFIX_PNG();
        return imageFilePath;
    }

    /**
     * 获取流程平台附件路径
     * @param fileName
     * @return
     */
    public static String getXBPMWORKAttachmentFileByName(String fileName) {
        return getXBPMWORKAttachmentFolder() + File.separator + fileName;
    }

    /**
     * 获取流程平台附件路径
     * @param fileName
     * @return
     */
    public static String getXBPMMEETINGAttachmentFileByName(String fileName) {
        return getXBPMMEETINGAttachFolder() + File.separator + fileName;
    }


    /**
     * 拍照后的暂存地址
     * png
     * @return
     */
    public static String getCameraCacheFilePath() {
        return getXBPMTempFolder() + File.separator + "camera_cache.png";
    }


    /**
     * 是否图片
     * @param extension
     * @return
     */
    public static boolean isImageFromFileExtension(String extension) {
        if(TextUtils.isEmpty(extension)){
            return false;
        }
        extension = extension.toLowerCase();
        switch (extension){
            case "jpg":
            case "jpeg":
            case "gif":
            case "png":
            case "bmp":
                return true;
        }
        return false;
    }

    /**
     * 文件扩展 返回图标
     * @param extension
     * @return
     */
    public static int getImageResourceByFileExtension(String extension) {
        if(TextUtils.isEmpty(extension)){
            return R.mipmap.icon_file_unkown;
        }
        extension = extension.toLowerCase();
        switch (extension){
            case "jpg":
            case "jpeg":
                return R.mipmap.icon_file_jpeg;
            case "gif":
                return R.mipmap.icon_file_gif;
            case "png":
                return R.mipmap.icon_file_png;
            case "tiff":
                return R.mipmap.icon_file_tiff;
            case "bmp":
            case "webp":
                return R.mipmap.icon_file_img;
            case "ogg":
            case "mp3":
            case "wav":
            case "wma":
                return R.mipmap.icon_file_mp3;
            case "mp4":
                return R.mipmap.icon_file_mp4;
            case "avi":
                return R.mipmap.icon_file_avi;
            case "mov":
            case "rm":
            case "mkv":
                return R.mipmap.icon_file_rm;
            case "doc":
            case "docx":
                return R.mipmap.icon_file_word;
            case "xls":
            case "xlsx":
                return R.mipmap.icon_file_excel;
            case "ppt":
            case "pptx":
                return R.mipmap.icon_file_ppt;
            case "html":
                return R.mipmap.icon_file_html;
            case "pdf":
                return R.mipmap.icon_file_pdf;
            case "txt":
            case "json":
                return R.mipmap.icon_file_txt;
            case "zip":
                return R.mipmap.icon_file_zip;
            case "rar":
                return R.mipmap.icon_file_rar;
            case "7z":
                return R.mipmap.icon_file_arch;
            case "ai":
                return R.mipmap.icon_file_ai;
            case "att":
                return R.mipmap.icon_file_att;
            case "au":
                return R.mipmap.icon_file_au;
            case "cad":
                return R.mipmap.icon_file_cad;
            case "cdr":
                return R.mipmap.icon_file_cdr;
            case "eps":
                return R.mipmap.icon_file_eps;
            case "exe":
                return R.mipmap.icon_file_exe;
            case "iso":
                return R.mipmap.icon_file_iso;
            case "link":
                return R.mipmap.icon_file_link;
            case "swf":
                return R.mipmap.icon_file_flash;
            case "psd":
                return R.mipmap.icon_file_psd;
            case "tmp":
                return R.mipmap.icon_file_tmp;
            default:
                return R.mipmap.icon_file_unkown;
        }

    }




}
