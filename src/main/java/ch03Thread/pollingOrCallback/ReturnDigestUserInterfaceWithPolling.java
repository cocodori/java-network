package ch03Thread.pollingOrCallback;

import javax.xml.bind.DatatypeConverter;

/**
 *  폴링은 스레드가 끝났는지를 무한 루프를 돌면서 확인함.
 *  따라서 동작할 수도 있지만(하지 않을 수도 있음) 쓸데없이 너무 많은 일을 하게 됨.
 *  이 방법은 동작하지 않을 수도 있는데 몇몇 JVM에서는 메인 스레드가 사용 가능한 CPU 시간을 점유하고, 다른 스레드가 실행할 시간을 남겨주지 않는다.
 *  메인 스레드가 작업 종료 상태를 확인하느라 너무 바쁜 나머지, 작업 스레드가 일할 시간이 없다.
 */
public class ReturnDigestUserInterfaceWithPolling {
    public static void main(String[] args) {
        String[] filenames = {"pppp.txt", "www.txt"};
        ReturnDigest[] digests = new ReturnDigest[filenames.length];

        for (int i = 0; i < filenames.length; i++) {
            // 다이제스트 계산
            digests[i] = new ReturnDigest(filenames[i]);
            digests[i].start();
        }

        for (int i = 0; i < filenames.length; i++) {
            while (true) {
                byte[] digest = digests[i].getDigest();
                if (digest != null) {
                    // 결과 출력
                    StringBuffer result = new StringBuffer(filenames[i] +": ");
                    result.append(DatatypeConverter.printHexBinary(digest));
                    System.out.println(result);
                    break;
                }
            }
        }
    }
}
