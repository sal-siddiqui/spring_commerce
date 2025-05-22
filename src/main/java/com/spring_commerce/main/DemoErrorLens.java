package com.spring_commerce.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DemoErrorLens {

    public static void main(final String[] args) {
        final String x = "This is a string"; 

        final List<String> list = new ArrayList<>();
        list.add("Hello");
        list.add(null); // ⚠️ Potential null insertion warning

        DemoErrorLens.printList(null); // ⚠️ Passing null

        final Date deprecatedDate = new Date(2022, 10, 15); // ⚠️ Deprecated constructor

        DemoErrorLens.unusedMethod(); // ⚠️ Method is declared but not used

        // ❌ Unreachable code (due to return above)
        return;
        System.out.println("This won't be printed");

        final int y; // ⚠️ y is never used

        // ❌ Missing semicolon
        System.out.println("Missing semicolon")

        // ❌ Missing return statement for non-void method
        System.out.println("Finished");
    }

    public static void printList(final List<String> items) {
        // ❌ Possible NullPointerException if items is null
        for (final String item : items) {
            System.out.println(item);
        }
    }

    @Deprecated
    public static void unusedMethod() {
        // This method is unused
    }

    public int buggyMethod() {
        // ❌ No return statement
    }
}
