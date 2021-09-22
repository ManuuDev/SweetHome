package Test;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.shdevelopment.ContactManagement.StatelessContactBook;
import org.shdevelopment.Structures.Contact;
import org.shdevelopment.SysInfo.Log;

import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class StatelessContactBookTests extends TestCase {

    StatelessContactBook contactBook = StatelessContactBook.getInstance();

    //https://github.com/powermock/powermock/issues/969

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.doNothing().when(Log.class, "addMessage", Mockito.any(String.class), Mockito.any());

        initMocks(this);
    }

    @After
    public void teardown() {
        contactBook.clearContactBook();
    }

    @Test
    public void addContactTest() {

        StatelessContactBook spy = spy(contactBook);

        Contact contact = new Contact("0.0.0.0", "ContactA", null);

        doNothing().when(spy).updateContactList();

        spy.addContact(contact);

        assertTrue(spy.existContact("0.0.0.0"));
    }

    @Test
    public void removeContactTest() {

        StatelessContactBook spy = spy(contactBook);

        Contact contact = new Contact("0.0.0.0", "ContactA", null);

        doNothing().when(spy).updateContactList();

        spy.addContact(contact);
        spy.removeContact(contact);

        assertFalse(spy.existContact("0.0.0.0"));
    }

    @Test
    public void removeContactWithIPTest() {

        StatelessContactBook spy = spy(contactBook);

        Contact contact = new Contact("0.0.0.0", "ContactA", null);

        doNothing().when(spy).updateContactList();

        spy.addContact(contact);
        spy.removeContact(contact.getIp());

        assertFalse(spy.existContact("0.0.0.0"));
    }


    @Test
    public void searchContactTest() {

        StatelessContactBook spy = spy(contactBook);

        Contact contactA = new Contact("0.0.0.0", "ContactA", null);
        Contact contactB = new Contact("0.0.0.1", "ContactB", null);

        doNothing().when(spy).updateContactList();

        spy.addContact(contactA);
        spy.addContact(contactB);

        assertEquals(contactA, spy.searchContact("0.0.0.0"));
        assertEquals(contactB, spy.searchContact("0.0.0.1"));
    }

    @Test
    public void searchContactWithSocketTest() {

        StatelessContactBook spy = spy(contactBook);

        Contact contact = new Contact("0.0.0.1", "ContactB", null);

        doNothing().when(spy).updateContactList();

        Socket socket = mock(Socket.class);
        InetAddress inetAddress = mock(InetAddress.class);
        when(socket.getInetAddress()).thenReturn(inetAddress);
        when(inetAddress.toString()).thenReturn("0.0.0.1");

        spy.addContact(contact);

        assertEquals(contact, spy.searchContact("0.0.0.1"));
    }

    @Test
    public void getNameTest() {

        StatelessContactBook spy = spy(contactBook);

        Contact contact = new Contact("0.0.0.0", "ContactA", null);

        doNothing().when(spy).updateContactList();

        spy.addContact(contact);

        assertEquals(contact.getName(), spy.searchContact("0.0.0.0").getName());
    }

    @Test
    public void existContactTest() {

        StatelessContactBook spy = spy(contactBook);

        Contact contact = new Contact("0.0.0.0", "ContactA", null);
        doNothing().when(spy).updateContactList();
        spy.addContact(contact);

        List<Contact> copyOfList = spy.getCopyOfContactBook();
        boolean contactFound = false;

        for (Contact _contact : copyOfList) {
            if (_contact.getIp().equals(contact.getIp())) {
                contactFound = true;
                break;
            }
        }

        assertEquals(contactFound, spy.existContact("0.0.0.0"));
    }
}
