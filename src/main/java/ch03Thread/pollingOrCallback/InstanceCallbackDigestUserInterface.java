package ch03Thread.pollingOrCallback;

import javax.xml.bind.DatatypeConverter;

public class InstanceCallbackDigestUserInterface {
    private String filename;
    private byte[] digest;

    public InstanceCallbackDigestUserInterface(String filename) {
        this.filename = filename;
    }

    public void calculateDigest() {
        InstanceCallbackDigest cb = new InstanceCallbackDigest(filename, this);
        Thread t = new Thread(cb);
        t.start();
    }

    void receiveDigest(byte[] digest) {
        this.digest = digest;
        System.out.println(this);
    }

    @Override
    public String toString() {
        String result = filename + ": ";
        if (digest != null)
            return result + DatatypeConverter.printHexBinary(digest);
        return result + "digest not available";
    }

    // 다이제스트 계산
    public static void main(String[] args) {
        String[] filenames = {"pppp.txt", "www.txt"};
        for (String filename : filenames) {
            InstanceCallbackDigestUserInterface d =
                    new InstanceCallbackDigestUserInterface(filename);
            d.calculateDigest();
        }
    }
}
