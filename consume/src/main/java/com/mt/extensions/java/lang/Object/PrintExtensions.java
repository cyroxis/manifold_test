package com.mt.extensions.java.lang.Object;

import manifold.ext.api.Extension;
import manifold.ext.api.This;

@Extension
public class PrintExtensions {

    public static void printExtension(@This Object o, String s) {
        System.out.println(String.format("[%s]:%s", o.getClass().getSimpleName(), s));
    }

    public static void printExtension(@This Object o, String s, Object...args) {
        o.printExtension(String.format(s, args));
    }

}
