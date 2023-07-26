package Test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.shdevelopment.ContactManagement.StatelessContactBook;
import org.shdevelopment.Core.Main;
import org.shdevelopment.Structures.Contact;
import org.shdevelopment.SysInfo.Console;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class StatelessContactBookTest {
    @Spy
    StatelessContactBook statelessContactBook = spy(StatelessContactBook.class);
    @Mock
    static Console console = mock(Console.class);
    @BeforeAll
    public static void beforeClass() {
        doNothing().when(console)
                   .insertMessage(ArgumentMatchers.anyString());

        Main.console = console;
    }

    @BeforeEach
    public void before() {
        doNothing().when(statelessContactBook)
                   .updateContactList();
    }

    @Test
    void addContactTest() {
        doReturn(false)
                .when(statelessContactBook)
                .existContact(ArgumentMatchers.any(Contact.class));

        Contact contact = new Contact("1.0.0.0","name", null);

        statelessContactBook.addContact(contact);
        boolean result = statelessContactBook.getCopyOfContactBook().contains(contact);
        assertTrue(result);
    }
}