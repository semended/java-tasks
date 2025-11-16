package com.mipt.semengolodniuk.hw6;

import java.util.List;

public class DemoMain {
    public static void main(String[] args) {
        User ok  = new User("Vlad", 21, "v@example.com");
        User bad = new User(null, -7, null);

        print("OK",  Validator.validate(ok));
        print("BAD", Validator.validate(bad));
    }

    private static void print(String label, List<ValidationError> errs) {
        System.out.println("[" + label + "] errors = " + errs.size());
        for (ValidationError e : errs) {
            System.out.println(" - " + e.field() + ": " + e.message());
        }
    }
}
