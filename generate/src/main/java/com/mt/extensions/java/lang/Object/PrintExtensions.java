package com.mt.extensions.java.lang.Object;

import manifold.ext.api.Extension;
import manifold.ext.api.This;

@Extension
public class PrintExtensions {

    public static void print(@This Object o, String s) {
        System.out.println(String.format("[%s]:%s", o.getClass().getSimpleName(), s));
    }

    public static void print(@This Object o, String s, Object...args) {
        o.print(String.format(s, args));
    }

}
