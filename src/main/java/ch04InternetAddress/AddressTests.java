package ch04InternetAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AddressTests {
    public static int getVersion(InetAddress inetAddress) {
        byte[] address = inetAddress.getAddress();
        if (address.length == 4) return 4;
        else if (address.length == 16) return 6;

        return -1;
    }

    public static void main(String[] args) throws UnknownHostException {
        int version = getVersion(InetAddress.getLocalHost());
        System.out.println(version);
    }
}
