package ch03Thread;

import ch03Thread.pollingOrCallback.ReturnDigest;

import javax.xml.bind.DatatypeConverter;

public class JoinDigestUserInterface {
    public static void main(String[] args) {
        String[] filenames = { "pppp.txt", "www.txt" };
        ReturnDigest[] digestThreads = new ReturnDigest[filenames.length];

        for (int i = 0; i < filenames.length; i++) {
            // 다이제스트 계산
            digestThreads[i] = new ReturnDigest(filenames[i]);
            digestThreads[i].start();
        }

        for (int i = 0; i < filenames.length; i++) {
            try {
                digestThreads[i].join();
                // 결과 출력
                StringBuffer result = new StringBuffer(filenames[i]);
                result.append(": ");
                byte[] digest = digestThreads[i].getDigest();
                result.append(DatatypeConverter.printHexBinary(digest));
                System.out.println(result);
            } catch (InterruptedException e) {
                System.err.println("Thread Interrupted before completion");
            }
        }
    }


}
