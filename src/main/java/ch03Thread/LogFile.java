package ch03Thread;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Date;

public class LogFile {
    private Writer out;

    public LogFile(File f) throws IOException {
        FileWriter fw = new FileWriter(f);
        this.out = new BufferedWriter(fw);
    }

    public void writeEntry(String message) throws IOException {
        LocalDateTime dateTime = LocalDateTime.now();
        out.write(dateTime.toString());
        out.write('\t');
        out.write(message);
        out.write("\r\n");
    }

    public void close() throws IOException {
        out.flush();
        out.close();
    }
}
