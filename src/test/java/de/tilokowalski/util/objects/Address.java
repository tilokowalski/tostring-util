package de.tilokowalski.util.objects;

import lombok.Setter;

/**
 * A simple address class for testing. Contains a reference to a {@link Person} object and other fields that are used in the tests.
 */
public class Address {

    /**
     * The street name.
     */
    private String street;

    /**
     * The city name.
     */
    private String city;

    /**
     * The zip code.
     */
    private String zip;

    /**
     * The resident of this address.
     */
    @Setter
    private Person resident;

    /**
     * Creates a new address object.
     *
     * @param street The street name.
     * @param city The city name.
     * @param zip The zip code.
     */
    public Address(String street, String city, String zip) {
        this.street = street;
        this.city = city;
        this.zip = zip;
    }

}