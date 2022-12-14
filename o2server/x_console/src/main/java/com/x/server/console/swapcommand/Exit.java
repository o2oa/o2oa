package com.x.server.console.swapcommand;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;

public class Exit {

    public static void main(String... args) throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(getBasePath() + "/command.swap", "rw")) {
            FileChannel fc = raf.getChannel();
            MappedByteBuffer mbb = fc.map(MapMode.READ_WRITE, 0, 256);
            FileLock flock = null;
            flock = fc.lock();
            mbb.put("exit".getBytes());
            flock.release();
        }
    }

    private static String getBasePath() {
        String path = Exit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File file = new File(path);
        if (!file.isDirectory()) {
            file = file.getParentFile();
        }
        while (null != file) {
            File versionFile = new File(file, "version.o2");
            if (versionFile.exists()) {
                return file.getAbsolutePath();
            }
            file = file.getParentFile();
        }
        throw new IllegalStateException("can not define o2server base directory.");
    }

}
