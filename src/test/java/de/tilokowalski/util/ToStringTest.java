package de.tilokowalski.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.tilokowalski.util.objects.Address;
import de.tilokowalski.util.objects.Person;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ToStringTest {

    private List<Integer> testList;

    private Map<String, String> testMap;

    private Person person;

    private Address address;

    @BeforeEach
    void setUp() {
        testList = Arrays.asList(1, 2, 3);
        testMap = new HashMap<>();
        testMap.put("key1", "value1");
        testMap.put("key2", "value2");

        address = new Address("123 Main St", "Anytown", "12345");
        person = new Person("John Doe", 30, address);

        // Creating a circular reference for testing
        address.setResident(person);
    }

    @Test
    void testCreateWithList() {
        String expected = "ArrayList[1,2,3]";
        String result = ToString.create(testList);
        assertEquals(expected, result);
    }

    @Test
    void testCreateWithMap() {
        String expected = "HashMap[2]"; // Since we don't resolve nested objects, we expect size here
        String result = ToString.create(testMap);
        assertEquals(expected, result);
    }

    @Test
    void testCreateDumpWithPersonObject() {
        String expected = "Person[\n\tname=\"John Doe\",\n\tage=30,\n\taddress=Address[\n\t\tstreet=\"123 Main St\",\n\t\tcity=\"Anytown\",\n\t\tzip=\"12345\",\n\t\tresident=PARENT\n\t]\n]";
        String result = ToString.createDump(person);
        assertEquals(expected, result);
    }

    @Test
    void testCreateCustomWithPersonObject() {
        String expected = "Person[name=\"John Doe\",age=30,address=Address[street=\"123 Main St\",city=\"Anytown\",zip=\"12345\",resident=PARENT]]";
        String result = ToString.createCustom(person, ',', 0, ToString.TS_LEVEL_DEEP, true);
        assertEquals(expected, result);
    }

}
