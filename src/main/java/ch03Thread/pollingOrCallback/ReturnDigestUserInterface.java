package ch03Thread.pollingOrCallback;

import javax.xml.bind.DatatypeConverter;

public class ReturnDigestUserInterface {
    /**
     * 이 코드는 NullPointerException이 발생한다.
     * 스레드가 계산된 다이제스트를 private 필드에 설정하기 전에 메인 프로그램에서 getDigest() 메서드를 사용하여
     * 값을 얻고, 이 값을 사용하려고 시도하기 때문에 NPE가 발생함.
     *
     * dr.start가 같은 스레드 안에서 단지 run()를 호출했고, 이런 흐름은 모두 단일 스레드 프로그램 내에서 일어났지만 결과는 전혀 다르다.
     * dr.start()가 호출되면서 시작된 다이제스트 계산은 main() 메서드가 dr.getDigest()를 호출하기 전에 끝날 수도 있지만, 끝나지 않을 수도 있다.
     * 만약 계산이 끝나지 않았다면 dr.getDigest()는 null을 반환하고, 이 값에 접근하려고 할 때 NPE를 발생시킨다.
     */
    public static void main(String[] args) {
        String[] filenames = {"pppp.txt"};
        for (String filename : filenames) {
            // 다이제스트 계산
            ReturnDigest dr = new ReturnDigest(filename);
            dr.start();

            // 결과 출력
            StringBuilder result = new StringBuilder(filename + ": ");
            byte[] digest = dr.getDigest();
            result.append(DatatypeConverter.printHexBinary(digest));
            System.out.println(result);
        }
    }
}
