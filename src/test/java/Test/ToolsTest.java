package Test;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.shdevelopment.Constant.SysInfo;
import org.shdevelopment.Core.Tools;
import org.shdevelopment.Structures.Unit;

import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.logging.Logger.global;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class ToolsTest {
    @Mock
    File fileMock;

    @Rule //initMocks
    public MockitoRule rule = MockitoJUnit.rule();

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

    @Test
    public void getTotalSizeInBytesForListOfFilesTest() {
        ArrayList<File> fileList = new ArrayList<>();

        File file1 = Mockito.mock(File.class);
        final long fileSize1 = 2402l;
        File file2 = Mockito.mock(File.class);
        final long fileSize2 = 1248l;
        File file3 = Mockito.mock(File.class);
        final long fileSize3 = 134l;

        fileList.add(file1);
        fileList.add(file2);
        fileList.add(file3);

        when(file1.length()).thenReturn(fileSize1);
        when(file2.length()).thenReturn(fileSize2);
        when(file3.length()).thenReturn(fileSize3);

        long expected = fileSize1 + fileSize2 + fileSize3;
        long result = Tools.totalSizeInBytes(fileList);

        assertEquals(expected, result);
    }

    @Test
    public void getSocketIpTest() {
        Socket socket = Mockito.mock(Socket.class);
        InetAddress inetAddress = Mockito.mock(InetAddress.class);

        final String rawIp = "/192.168.0.0";
        final String expectedIp = "192.168.0.0";

        when(socket.getInetAddress()).thenReturn(inetAddress);
        when(inetAddress.toString()).thenReturn(rawIp);

        String result = Tools.getSocketIp(socket);

        assertEquals(expectedIp, result);
    }
    @Test
    public void fileSizeInKBTest() {
        Mockito.when(fileMock.length()).thenReturn(2048l);
        double result = Tools.fileSize(fileMock, Unit.KB);
        assertEquals(2,result, 0.2);
    }

    @Test
    public void fileSizeInMBTest() {
        Mockito.when(fileMock.length()).thenReturn(3145728l);
        double result = Tools.fileSize(fileMock, Unit.MB);
        assertEquals(3,result, 0.2);
    }

    @Test
    public void fileSizeInGBTest() {
        Mockito.when(fileMock.length()).thenReturn(1073741824l);
        double result = Tools.fileSize(fileMock, Unit.GB);
        assertEquals(1,result, 0.2);
    }
}