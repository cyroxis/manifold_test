package com.mt.extensions.java.lang.String;

import manifold.ext.api.Extension;
import manifold.ext.api.This;

@Extension
public class MyStringExtension {

  public static String foo(@This String s) {
    return "foo: " + s;
  }

}