package com.mt;

@Winner
public class Runner {

    public static void main(String[] args) {
        new Runner().run();
    }

    private void run() {
        this.print("START");

        this.print("Bobby".foo());
        this.print("Bobby".bar());

        RunnerWinner mc = new RunnerWinner();
//        FooWinner mc = new FooWinner();
        this.print(mc.win());

        this.print("END");
    }
}
