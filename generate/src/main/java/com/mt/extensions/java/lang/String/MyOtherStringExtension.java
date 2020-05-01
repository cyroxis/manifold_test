package com.mt.extensions.java.lang.String;

import manifold.ext.api.Extension;
import manifold.ext.api.This;

@Extension
public class MyOtherStringExtension {

  public static String bar(@This String s) {
    return "bar: " + s;
  }

}