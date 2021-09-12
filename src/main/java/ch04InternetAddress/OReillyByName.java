package ch04InternetAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class OReillyByName {
    public static void main(String[] args) {
        try {
            InetAddress address = InetAddress.getByName("www.oreilly.com");
            System.out.println(address);
            InetAddress localhost = InetAddress.getLocalHost();
            System.out.println(localhost);
        } catch (UnknownHostException e) {
            System.err.println(e);
        }
    }
}
