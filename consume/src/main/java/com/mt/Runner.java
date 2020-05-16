package com.mt;

import com.at.TemplateWith;
import com.mt.extensions.java.FooCreator;

public class Runner {

    public static void main(String[] args) {
        new Runner().run();
    }

    private void run() {
        this.printExtension("START");

//        this.printExtension("Bobby".foo());
        new BarWinner().foo();

        this.printExtension("END");
    }
}
