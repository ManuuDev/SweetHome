package org.shdevelopment.Constant;

import org.shdevelopment.Crypto.Crypto;
import org.shdevelopment.Structures.ContactData;
import org.shdevelopment.Structures.CustomException;

import static org.shdevelopment.Core.Tools.getDeviceName;
import static org.shdevelopment.Core.Tools.getSystemIPV4;

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
