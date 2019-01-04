package jiguang.chat.pickerimage.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/** package */
class ExternalStorage {
	/**
	 * 外部存储根目录
	 */
    private String sdkStorageRoot = null;

	private static ExternalStorage instance;

	private ExternalStorage() {

	}

	synchronized public static ExternalStorage getInstance() {
		if (instance == null) {
			instance = new ExternalStorage();
		}
		return instance;
	}

    public void init(Context context, String sdkStorageRoot) {
        if (!TextUtils.isEmpty(sdkStorageRoot)) {
            File dir = new File(sdkStorageRoot);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (dir.exists() && !dir.isFile()) {
                this.sdkStorageRoot = sdkStorageRoot;
                if (!sdkStorageRoot.endsWith("/")) {
                    this.sdkStorageRoot = sdkStorageRoot + "/";
                }
            }
        }

        if (TextUtils.isEmpty(this.sdkStorageRoot)) {
            loadStorageState(context);
        }

        createSubFolders();
    }

    private void loadStorageState(Context context) {
        String externalPath = Environment.getExternalStorageDirectory().getPath();
        this.sdkStorageRoot = externalPath + "/" + context.getPackageName() + "/";
    }

	private void createSubFolders() {
		boolean result = true;
		File root = new File(sdkStorageRoot);
		if (root.exists() && !root.isDirectory()) {
			root.delete();
		}
		for (StorageType storageType : StorageType.values()) {
			result &= makeDirectory(sdkStorageRoot + storageType.getStoragePath());
		}
		if (result) {
			createNoMediaFile(sdkStorageRoot);
		}
	}

	/**
	 * 创建目录
	 *
	 * @param path
	 * @return
	 */
	private boolean makeDirectory(String path) {
		File file = new File(path);
		boolean exist = file.exists();
		if (!exist) {
			exist = file.mkdirs();
		}
		return exist;
	}

	protected static String NO_MEDIA_FILE_NAME = ".nomedia";

	private void createNoMediaFile(String path) {
		File noMediaFile = new File(path + "/" + NO_MEDIA_FILE_NAME);
		try {
			if (!noMediaFile.exists()) {
				noMediaFile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件全名转绝对路径（写）
	 *
	 * @param fileName
	 *            文件全名（文件名.扩展名）
	 * @return 返回绝对路径信息
	 */
	public String getWritePath(String fileName, StorageType fileType) {
		return pathForName(fileName, fileType, false, false);
	}

	private String pathForName(String fileName, StorageType type, boolean dir,
                               boolean check) {
		String directory = getDirectoryByDirType(type);
		StringBuilder path = new StringBuilder(directory);

		if (!dir) {
			path.append(fileName);
		}

		String pathString = path.toString();
		File file = new File(pathString);

		if (check) {
			if (file.exists()) {
				if ((dir && file.isDirectory())
						|| (!dir && !file.isDirectory())) {
					return pathString;
				}
			}

			return "";
		} else {
			return pathString;
		}
	}

	/**
	 * 返回指定类型的文件夹路径
	 *
	 * @param fileType
	 * @return
	 */
	public String getDirectoryByDirType(StorageType fileType) {
		if (sdkStorageRoot==null) {
			sdkStorageRoot = Environment.getExternalStorageDirectory().getPath()+File.separator;
		}

		return sdkStorageRoot + fileType.getStoragePath();
	}

	/**
	 * 根据输入的文件名和类型，找到该文件的全路径。
	 * @param fileName
     * @param fileType
	 * @return 如果存在该文件，返回路径，否则返回空
	 */
	public String getReadPath(String fileName, StorageType fileType) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }

        return pathForName(fileName, fileType, false, true);
    }

    public boolean isSdkStorageReady() {
        String externalRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (this.sdkStorageRoot.startsWith(externalRoot)) {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } else {
            return true;
        }
    }

	/**
	 * 获取外置存储卡剩余空间
	 * @return
	 */
    public long getAvailableExternalSize() {
		return getResidualSpace(sdkStorageRoot);
	}
    
    /**
     * 获取目录剩余空间
     * @param directoryPath
     * @return
     */
    private long getResidualSpace(String directoryPath) {
        try {
            StatFs sf = new StatFs(directoryPath);
            long blockSize = sf.getBlockSize();
            long availCount = sf.getAvailableBlocks();
            long availCountByte = availCount * blockSize;
            return availCountByte;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
