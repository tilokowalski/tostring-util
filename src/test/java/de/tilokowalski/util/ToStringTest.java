package de.tilokowalski.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.tilokowalski.util.objects.Address;
import de.tilokowalski.util.objects.Person;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link ToString} class by utilizing the {@link Person} and {@link Address} classes.
 * The annotations {@link ToStringIgnore} and {@link ToStringResolve} are indirectly tested because they are included in the {@link Person} class.
 */
public class ToStringTest {

    /**
     * A list with some integers to be tested.
     */
    private List<Integer> list;

    /**
     * A map with some strings to be tested.
     */
    private Map<String, String> map;

    private Person person;

    /**
     * An example address object to be tested.
     */
    private Address address;

    /**
     * A list with old addresses of the person object to be tested.
     */
    private List<Address> addressesOld = new ArrayList<Address>();

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        list = Arrays.asList(1, 2, 3);

        map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        address = new Address("123 Main St", "Anytown", "12345");
        addressesOld.add(new Address("123 Main St", "Anytown", "12345"));

        person = new Person("John Doe", 30, address, addressesOld);

        address.setResident(person);
    }

    /**
     * Tests the {@link ToString#create(Object)} method with a list.
     * The method must not resolve objects by default.
     */
    @Test
    void testCreateWithList() {
        String expected = "ArrayList[3]";
        String result = ToString.create(list);

        assertEquals(expected, result);
    }

    /**
     * Tests the {@link ToString#create(Object)} method with a map.
     * The method must not resolve objects by default.
     */
    @Test
    void testCreateWithMap() {
        String expected = "HashMap[2]";
        String result = ToString.create(map);

        assertEquals(expected, result);
    }

    /**
     * Tests the {@link ToString#createDump(Object)} method with a person object.
     * The method must return a multi-line string with all fields of the object and their values, including references to other objects and resolved objects.
     */
    @Test
    void testCreateDumpWithPersonObject() {
        String expected = "Person[\n\tname=\"John Doe\"\n\taddress=Address[\n\t\tstreet=\"123 Main St\"\n\t\tcity=\"Anytown\"\n\t\tzip=\"12345\"\n\t\tresident=Person[PARENT]\n\t]\n\taddressesOld=ArrayList[1]\n]";
        String result = ToString.createDump(person);

        assertEquals(expected, result);
    }

    /**
     * Tests the {@link ToString#createCustom(Object)} method with a person object.
     * With this configuration, the method must return a single-line string with all fields of the object and their values, including references to other objects but in a unresolved form.
     */
    @Test
    void testCreateCustomWithPersonObject() {
        String expected = "Person[name=\"John Doe\",address=Address[],addressesOld=ArrayList[1]]";
        String result = ToString.createCustom(person, ',', 0, ToString.TS_LEVEL_DEEP, false);

        assertEquals(expected, result);
    }

}
