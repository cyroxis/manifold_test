package com.mt;

public class Foo {

    public String hello() {
        return "Hi there!";
    }

    @Override
    public String toString() {
        return hello();
    }
}
