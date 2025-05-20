package com.spring_commerce.main;

public class test {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("This is a very long string that will surely push the line over the 100")
                .append(" And here's more text to make it wrap.")
                .append(" Still appending for science.");

        System.out.println(sb.toString());
    }
}
