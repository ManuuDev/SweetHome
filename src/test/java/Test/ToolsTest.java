package Test;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.shdevelopment.Constant.SysInfo;
import org.shdevelopment.Core.Tools;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;

public class ToolsTest {

    @Test
    public void getAllPossibleIPs() {
        String rawIp = "192.168.1.";
        List<String> allIps = new ArrayList<>();

        for(int i = 2; i < 255; i++) {
            allIps.add(rawIp.concat(String.valueOf(i)));
        }

        try(MockedStatic systemInfo = mockStatic(SysInfo.class)){
            systemInfo.when(SysInfo::getIPV4).thenReturn("192.168.1.1");
            List<String> result = Tools.getIPsFromLANDevices();
            for(String ip : allIps){
                assertTrue(result.contains(ip));
            }
        }
    }
}