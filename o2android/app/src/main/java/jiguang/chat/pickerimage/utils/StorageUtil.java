package jiguang.chat.pickerimage.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class StorageUtil {
	public final static long K = 1024;
	public final static long M = 1024 * 1024;
	// 外置存储卡默认预警临界值
    private static final long THRESHOLD_WARNING_SPACE = 100 * M;
	// 保存文件时所需的最小空间的默认值
	public static final long THRESHOLD_MIN_SPCAE = 20 * M;

    public static void init(Context context, String rootPath) {
        ExternalStorage.getInstance().init(context, rootPath);
    }

	/**
	 * 获取文件保存路径，没有toast提示
	 *
	 * @param fileName
	 * @param fileType
	 * @return 可用的保存路径或者null
	 */
	public static String getWritePath(String fileName, StorageType fileType) {
		return getWritePath(null, fileName, fileType, false);
	}

	/**
	 * 获取文件保存路径
	 *
	 * @param fileName
	 *            文件全名
	 * @param tip
	 *            空间不足时是否给出默认的toast提示
	 * @return 可用的保存路径或者null
	 */
	private static String getWritePath(Context context, String fileName, StorageType fileType, boolean tip) {
		String path = ExternalStorage.getInstance().getWritePath(fileName, fileType);
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		File dir = new File(path).getParentFile();
		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		return path;
	}

	/**
	 * 判断能否使用外置存储
	 */
	public static boolean isExternalStorageExist() {
		return ExternalStorage.getInstance().isSdkStorageReady();
	}


    /**
     * 判断外部存储是否存在，以及是否有足够空间保存指定类型的文件
     *
     * @param context
     * @param fileType
     * @param tip  是否需要toast提示
     * @return false: 无存储卡或无空间可写, true: 表示ok
     */
    public static boolean hasEnoughSpaceForWrite(Context context, StorageType fileType, boolean tip) {
        if (!ExternalStorage.getInstance().isSdkStorageReady()) {
            return false;
        }

        long residual = ExternalStorage.getInstance().getAvailableExternalSize();
        if (residual < fileType.getStorageMinSize()) {
            return false;
        } else if (residual < THRESHOLD_WARNING_SPACE) {
        }

        return true;
    }
	/**
	 * 根据输入的文件名和类型，找到该文件的全路径。
	 *
	 * @param fileName
	 * @param fileType
	 * @return 如果存在该文件，返回路径，否则返回空
	 */
	public static String getReadPath(String fileName, StorageType fileType) {
		return ExternalStorage.getInstance().getReadPath(fileName, fileType);
	}

	/**
	 * 获取文件保存路径，空间不足时有toast提示
	 *
	 * @param context
	 * @param fileName
	 * @param fileType
	 * @return 可用的保存路径或者null
	 */
	public static String getWritePath(Context context, String fileName, StorageType fileType) {
		return getWritePath(context, fileName, fileType, true);
	}

    public static String getSystemImagePath() {
        if (Build.VERSION.SDK_INT > 7) {
            String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            return picturePath + "/im/";
        } else {
            String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            return picturePath + "/im/";
        }
    }
}
