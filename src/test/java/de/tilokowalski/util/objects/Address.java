package de.tilokowalski.util.objects;

public class Address {
        private String street;
        private String city;
        private String zip;
        private Person resident;

        public Address(String street, String city, String zip) {
            this.street = street;
            this.city = city;
            this.zip = zip;
        }

        public void setResident(Person resident) {
            this.resident = resident;
        }

        // Getters and setters
    }