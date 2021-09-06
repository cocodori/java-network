스레드를 직접 만들어서 실행할 때 보통 `Tread` 클래스를 서브클래싱하거나 `Runnable`  인터페이스를 구현한다. 자바 네트워크 프로그래밍 3장 '스레드'를 보면 일반적으로 `Thread` 를 서브클래싱하는 것보다 `Runnable`  인터페이스를 구현하는 것을 더 선호해야 할 이유는 없고, 반대의 경우도 마찬가지라고 한다.

하지만 `Thread` 가 하는 일이 정말로 `Thread` 가 아니기 때문에 객체지향의 관점에서 보자면 `Runnable` 을 구현하는 게 맞다고 한다. `Thread` 가 하는 일이 실제로 `Thread` 가 아니라는 말은 무슨 말인지 잘 이해 못했다.

어쨌든 `Thread` 를 확장하는 것보다 `Runnable` 을 구현해야 하는 게 맞는 것 같다. Java는 다중 상속을 지원하지 않고 `Thread` 가 아닌 다른 클래스를 상속하고 있거나, 그렇게 될 가능성이 있으므로 `Runnable` 인터페이스를 구현해서 스레드를 실행해야 한다.

### 스레드 반환하기

`run()` 메소드와 `start()` 메소드는 어떤 값도 반환하지 않는다.

만약 SHA-256 다이제스트를 메인 스레드 안에서 직접 출력하는 대신, 다이제스트 스레드가 다이제스트 값을 메인 스레드로 반환하다고 가정한다.

**Runnable 구현**

```java
public class DigestRunnable implements Runnable {
    private String filename;

    public DigestRunnable(String filename) {
        this.filename = filename;
    }

    @Override
    public void run() {
        try {
            FileInputStream in = new FileInputStream(filename);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            DigestInputStream din = new DigestInputStream(in, sha);
            while (din.read() != -1);
            din.close();
            byte[] digest = sha.digest();

            StringBuilder result = new StringBuilder(filename + ": ");
            result.append(DatatypeConverter.printHexBinary(digest));
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

		public byte[] getDigest() {
	        return digest;
	  }
}
```

**스레드 결과를 반환하기 위해 접근자 메소드를 사용하는 main 프로그램**

```java
public class ReturnDigestUserInterface {
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
```

이 코드는 NPE가 발생한다.

스레드가 계산된 다이제스트를 Private 필드에 설정하기도 전에 메인 프로그램에서 `getDigest()` 를 사용하여 값을 얻어서 사용하려고 시도하기 때문이다.

### 경쟁 조건

이런 문제를 해결하기 위해 `getDigest()` 호출을 `main()` 뒤로 미뤄볼 수 있다.

```java
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
```

이 코드는 아마 실패하지만, 어쩌면 성공할 수도 있다. 첫 번째 for loop에서 스레드를 생성하지만, 두 번째 for loop까지 너무 빨리 끝나버린다면?

생성된 스레드가 할 일이 끝나기도 전에 값에 접근하니 똥컴이 아니라면 당연히 NPE다.

이 코드는 생성한 스레드의 수, CPU, 디스크 상태, JVM이 각각 스레드에 시간을 할당하는 알고리즘 등 다양한 요인에 의해 성공하거나 실패하는데, **이런 행동 조건을 경쟁 조건Race condition이라고 한다.**

### 폴링

폴링은 쉽게 말하자면 무한 루프를 돌면서 스레드가 끝났는지 검사하는 것이다.

**폴링 예제**

```java
public static void main(String[] args) {
        String[] filenames = {"pppp.txt", "www.txt"};
        ReturnDigest[] digests = new ReturnDigest[filenames.length];

        for (int i = 0; i < filenames.length; i++) {
            // 다이제스트 계산
            digests[i] = new ReturnDigest(filenames[i]);
            digests[i].start();
        }

        for (int i = 0; i < filenames.length; i++) {
            **while (true) { // 무한 루프를 돌면서 스레드가 실행이 끝났는지 검사함
                byte[] digest = digests[i].getDigest();
                if (digest != null) {
                    // 결과 출력
                    StringBuffer result = new StringBuffer(filenames[i] +": ");
                    result.append(DatatypeConverter.printHexBinary(digest));
                    System.out.println(result);
                    break;
                }
            }**
        }
    }
```

이건 쓸데없이 너무 많은 일을 하기 때문에 CPU 낭비다.

웃긴 건 이 코드는 성공은 할 것 같지만 아니다. 그냥 아무 일도 일어나지 않는다. 프로그램이 뻗지도 않고 계속 돌기만 한다.

더 어리둥절한 건 break point를 찍고 debug 모드로 돌리면 이 코드는 정상 작동하여 파일들의 Digest 값을 출력한다.

몇몇 JVM은 메인 스레드가 사용 가능한 CPU 시간을 점유하고, 다른 스레드가 실행할 시간을 남겨주지 않는다고 한다. 이 말에 근거해서, 디버그 모드로는 동작하는데, 일반 모드로 실행하면 동작하지 않는 이유를 생각해보자면 이렇다.

1. 일반 모드로 동작할 때는 메인 스레드가 무한 루프를 도느라 너무 바쁘다. 그래서 JV이 다른 스레드가 실행될 시간을 남기지 못한다.
2. 디버그 모드는 천천히 돌기 때문에.. 다음, 다음으로 넘어가는 사이 사이에 약간의 시간이 생겨서 JVM은 다른 스레드가 실행할 시간을 할당할 수 있게 되어 코드가 정상 작동한다.

이건 그냥 내 생각일 뿐이다.

어쨌거나 중요한 것은 폴링도 적절한 방식은 아니라는 것이다.

### 콜백

콜백을 사용하면 무한 루프를 돌면서 너 다 끝났니? 하고 기다릴 필요가 없다. 다 끝나면 나를 호출할 것이기 때문에!

**callback digest**

```java
public class InstanceCallbackDigest implements Runnable {
    private String filename;
    private InstanceCallbackDigestUserInterface callback;

    public InstanceCallbackDigest(String filename, InstanceCallbackDigestUserInterface callback) {
        this.filename = filename;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            FileInputStream in = new FileInputStream("/Users/hunlee/salda/learn/JavaNetworkProgramming/src/main/resources/"+filename);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            DigestInputStream din = new DigestInputStream(in, sha);
            while (din.read() != -1);
            din.close();
            byte[] digest = sha.digest();
            callback.receiveDigest(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

run 메소드 끝에서 계산된 다이제스트 값을 `InstanceCallbackDigestUserInterface` 의 `receiveDigest()` 메소드 인자로 넘긴다.

**main**

```java
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
```

main 프로그램은 명시적으로 스레드의 결과를 반환받지 않는다. 이미 독립적으로 실행되는 스레드에 의해 호출된다. 즉 `receiveDigest()` 는 메인 스레드가 아닌, 다이제스트를 계산하는 스레드 안에서 실행된다.

콜백이 폴링보다 좋은 첫 번째 이유는 CPU를 낭바하지 않는다는 것이다.

더 좋은 이유는 콜백 방식이 더 유연하며, 많은 스레드와 객체, 그리고 클래스가 엮여 있는 복잡한 상황에 충분히 대처할 수 있기 때문. 예를 들어 하나 이상의 객체에 대해 스레드의 계산 결과를 알려줘야 하는 상황이라면, 스레드는 객체를 목록으로 관리하여 콜백을 호출할 수 있다. 계산 결과가 필요한 객체는 `Thread` 혹은 `Runnable` 클래스 메소드를 호출하여 자신을 목록에 등록할 수 있다.