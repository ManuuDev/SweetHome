package Constant;

import Crypto.Crypto;
import Structures.ContactData;
import Structures.CustomException;

import static Core.Tools.getDeviceName;
import static Core.Tools.getSystemIPV4;

public class SysInfo {

    public static final long SEARCH_TIME;

    public static String DEVICE_NAME;
    public static ContactData LOCAL_CONTACT;

    private static String LOCAL_IP;

    static {
        SEARCH_TIME = 20000;
    }

    public static void generateInformation() throws CustomException.NoIPV4 {
        LOCAL_IP = getSystemIPV4();

        DEVICE_NAME = getDeviceName();

        LOCAL_CONTACT = new ContactData(LOCAL_IP, DEVICE_NAME, Crypto.getPublicKey());
    }

    public static String getIPV4() {
        return LOCAL_IP;
    }
}
