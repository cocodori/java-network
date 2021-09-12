package ch04InternetAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ReverseTest {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress ia = InetAddress.getByName("223.130.195.200");
        System.out.println(ia.getCanonicalHostName());
    }
}
