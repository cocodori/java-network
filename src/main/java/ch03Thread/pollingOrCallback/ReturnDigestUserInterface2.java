package ch03Thread.pollingOrCallback;

import javax.xml.bind.DatatypeConverter;

/**
 * ReturnDigestUserInterface에서 발생한 문제를 해결하기 위해
 * dr.getDigest() 호출을 main() 뒤로 이동해볼 수도 있다.
 *
 * 이 코드도 역시 문제가 있다. 이 코드는 성공할 수도, 실패할 수도 있다.
 * 첫 번째 for loop가 너무 빨라서 첫 번째 루프에서 생성된 스레드가 끝나기도 전에
 * 두 번쨰 for loop가 수행되면 이 코드 역시 NPE가 발생한다.
 *
 * 이 코드의 결과는 생성한 스레드의 개수, CPU, 디스크 상태, JVM이 각각 슬데ㅡ에 시간을 할당하는 알고리즘 등
 * 다양한 요인에 의해 성공할 수도 실패할 수도 있다. 이런 행동 조건을 경쟁 조건(Race condition)이라고 한다.
 */
public class ReturnDigestUserInterface2 {
    public static void main(String[] args) {
        String[] filenames = {"pppp.txt", "www.txt"};
        ReturnDigest[] digests = new ReturnDigest[filenames.length];

        for (int i = 0; i < filenames.length; i++) {
            // 다이제스트 계산
            digests[i] = new ReturnDigest(filenames[i]);
            digests[i].start();
        }

        for (int i = 0; i < filenames.length; i++) {
            // 결과 출력
            StringBuffer result = new StringBuffer(filenames[i] +": ");
            byte[] digest = digests[i].getDigest();
            result.append(DatatypeConverter.printHexBinary(digest));

            System.out.println(result);
        }
    }
}
