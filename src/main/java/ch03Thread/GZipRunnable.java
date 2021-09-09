package ch03Thread;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class GZipRunnable implements Runnable{
    private final File input;

    public GZipRunnable(File input) {
        this.input = input;
    }

    @Override
    public void run() {
        // 압축 파일을 다시 압축하지 않도록 한다.
        if (!input.getName().endsWith(".gz")) {
            File output = new File(input.getParent(), input.getName() + ".gz");
            if (!output.exists()) {
                try (
                        InputStream in = new BufferedInputStream(new FileInputStream(input));
                        OutputStream out = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(output)))
                        ) {
                    int b;
                    while ((b = in.read()) != -1) out.write(b);
                    out.flush();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
    }
}
