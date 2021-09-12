package ch04InternetAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyAddress2 {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress me = InetAddress.getLocalHost();
        String dottedQuad = me.getHostAddress();
        System.out.println("My Address is " + dottedQuad);
    }
}
