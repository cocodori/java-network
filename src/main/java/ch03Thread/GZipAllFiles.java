package ch03Thread;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GZipAllFiles {
    public final static int THREAD_COUNT = 4;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);

        String[] filenames = { "pppp.txt", "www.txt" };
        for (String filename : filenames) {
            File f = new File(filename);
            if (f.exists()) {
                if (f.isDirectory()) {
                    File[] files = f.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        if (!files[i].isDirectory()) { // 디렉터리 아래 있는 디렉터리는 처리하지 않는다.
                            Runnable task = new GZipRunnable(files[i]);
                            pool.submit(task);
                        }
                    }
                } else {
                    Runnable task = new GZipRunnable(f);
                    pool.submit(task);
                }
            }
        }

        pool.shutdown();
    }
}
