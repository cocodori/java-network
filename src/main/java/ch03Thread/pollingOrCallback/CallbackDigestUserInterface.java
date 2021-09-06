package ch03Thread.pollingOrCallback;

import javax.xml.bind.DatatypeConverter;

public class CallbackDigestUserInterface {
    public static void receiveDigest(byte[] digest, String name) {
        StringBuffer result = new StringBuffer(name+": ");
        result.append(DatatypeConverter.printHexBinary(digest));
        System.out.println(result);
    }

    public static void main(String[] args) {
        String[] filenames = {"pppp.txt", "www.txt"};
        for (String filename: filenames) {
            // 다이제스트 계산
            CallbackDigest cb = new CallbackDigest(filename);
            Thread t = new Thread(cb);
            t.start();
        }
    }
}
