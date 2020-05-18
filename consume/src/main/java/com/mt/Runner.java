package com.mt;

public class Runner {

    public static void main(String[] args) {
        new Runner().run();
    }

    private void run() {
        this.printExtension("START");

        this.printExtension(new BarWinner().win());

        this.printExtension("END");
    }
}
