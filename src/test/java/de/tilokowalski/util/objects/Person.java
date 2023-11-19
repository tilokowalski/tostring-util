package de.tilokowalski.util.objects;

import de.tilokowalski.util.ToString;
import de.tilokowalski.util.ToStringDontResolve;
import de.tilokowalski.util.ToStringIgnore;
import java.util.List;

/**
 * A simple person class for testing. Contains a reference to an {@link Address} object, a list with old addresses and other fields that are used in the tests.
 */
public class Person {

    /**
     * The name of the person.
     */
    private String name;

    /**
     * The age of the person. This field is ignored by the {@link ToString} class.
     */
    @ToStringIgnore
    private int age;

    /**
     * The address of the person.
     */
    private Address address;

    /**
     * A list with old addresses of the person. This field is not resolved by the {@link ToString} class.
     */
    @ToStringDontResolve
    private List<Address> addressesOld;

    /**
     * Creates a new person object.
     *
     * @param name The name of the person.
     * @param age The age of the person.
     * @param address The address of the person.
     * @param addressesOld A list with old addresses of the person.
     */
    public Person(String name, int age, Address address, List<Address> addressesOld) {
        this.name = name;
        this.age = age;
        this.address = address;
        this.addressesOld = addressesOld;
    }

}