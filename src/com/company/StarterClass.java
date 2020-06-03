package com.company;

import javax.annotation.processing.SupportedSourceVersion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Queue;

import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.sqrt;

public class StarterClass {
    private static int[] handle_args(String[] args) {
        int[] result = new int[2];
        if (args.length == 4) {
            if (args[0].equals("-p")) {
                result[0] = Integer.parseInt(args[1]);
            } else {
                System.out.println("Invalid argument for precision, please set it with -p <number>. Setting it to 10240");
                result[0] = 10240;
            }

            if (args[2].equals("-t") || args[2].equals("-tasks")) {
                result[1] = Integer.parseInt(args[3]);
            } else {
                System.out.println("Invalid argument for thread count, please set it with -t <number>. Setting it to 1");
                result[1] = 1;
            }
        } else if (args.length == 2) {
            if (args[0].equals("-p")) {
                result[0] = Integer.parseInt(args[1]);
            } else {
                System.out.println("Invalid argument for precision, please set it with -p <number>. Setting it to 10240");
                result[0] = 10240;
            }

            int max_threads = Runtime.getRuntime().availableProcessors();
            System.out.print("-t argument not passed, using all available threads: ");
            System.out.println(max_threads);
            result[1] = max_threads;
        } else {
            System.out.println("Invalid command line arguments, setting the default values (-p 10240 -t 1)");
            result[0] = 10240;
            result[1] = 1;
        }

        return result;
    }

    public static void main(String[] args) {
        /*int[] parsed_args = handle_args(args);
        int scale = parsed_args[0];
        int threads_count = parsed_args[1];

        System.out.println("Precision: " + scale + ", Threads: " + threads_count);

        Thread[] threads = new Thread[threads_count];
        Calculations[] workers = new Calculations[threads_count];
        BigDecimal previous = BigDecimal.valueOf(-1);
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal[] results = new BigDecimal[threads_count];

        for (int i = 0; i < threads_count; i++) {
            results[i] = BigDecimal.ZERO;
        }
        int n = 0;
        boolean should_continue = true;

        for (int i = 0; i < threads_count; i++) {
            workers[i] = new Calculations(results[i], n, scale);
            threads[i] = new Thread(workers[i]);
            threads[i].start();
            n++;
        }

        while (should_continue) {
            for (int current_thread = 0; current_thread < threads_count; current_thread++) {
                if (workers[current_thread].work_done) {
                    try {
                        threads[current_thread].join();
                    } catch (InterruptedException ex) {
                        System.out.print("Error");
                    }
                    result = workers[current_thread].result;

                    if (previous.subtract(result).compareTo(BigDecimal.ZERO) == 0) {
                        should_continue = false;
                    } else {
                        workers[current_thread] = new Calculations(result, n, scale);
                        threads[current_thread] = new Thread(workers[current_thread]);
                        threads[current_thread].start();
                        previous = result;
                    }
                    n++;
                }
            }
        }*/
        boolean should_continue = true;
        int scale = 400;

        BigDecimal previous = BigDecimal.valueOf(1103*sqrt(8));
        previous = previous.divide(BigDecimal.valueOf(pow(99,2)), scale, RoundingMode.HALF_UP);
        BigDecimal result = BigDecimal.ZERO;
        //int n = 0;
        Thread[] threads = new Thread[1];

        BigDecimal EPSILON = BigDecimal.valueOf(1);
        //while(should_continue) {
        for(int n = 0; n < scale; n++){
            CalculationsBasedOnPrevious r = new CalculationsBasedOnPrevious(result, previous, n, scale);
            Thread t = new Thread(r);
            threads[0] = t;

            t.start();
            try {
                threads[0].join();
            } catch (InterruptedException ex) {
                System.out.print("Error");
            }

            //result = BigDecimal.ONE.divide(result, scale, RoundingMode.HALF_UP);
            result = r.result;

            //System.out.println("diff:" + previous.subtract(result).toPlainString());

            BigDecimal result_as_pi = BigDecimal.ONE.divide(result, scale, RoundingMode.HALF_UP);
            BigDecimal prev_as_pi = BigDecimal.ONE.divide(previous, scale, RoundingMode.HALF_UP);

            //System.out.println("Result as pi:" + result_as_pi);
            //System.out.println("Diff:" + result_as_pi.subtract(prev_as_pi).toPlainString());
            if(result_as_pi.subtract(prev_as_pi).compareTo(BigDecimal.ZERO) == 0)
            {
                should_continue = false;
            }

            previous = result;
            //n++;
        }
        System.out.println(BigDecimal.ONE.divide(result, scale, RoundingMode.HALF_UP));
        /*for (int current_thread = 0; current_thread < threads_count; current_thread++) {
            try {
                threads[current_thread].join();
            } catch (InterruptedException ex) {
                System.out.print("Error");
            }
        }*/
        //System.out.println(n);
        //System.out.println(BigDecimal.ONE.divide(result, scale, RoundingMode.HALF_UP));
        //System.out.println(result);

    }
    //3.14159265358979279401149285925126360843227873615132156357983493056932100312250770614203052737207380256736559113
}
//0,318309878
/*
while(should_continue) {
            Calculations r = new Calculations(prev, result, n, scale, should_continue);
            Thread t = new Thread(r);
            threads[0] = t;

            t.start();
            try {
                threads[0].join();
            } catch (InterruptedException ex) {
                System.out.print("Error");
            }

            r.result = r.result.multiply(BigDecimal.valueOf(sqrt(8) / (pow(99, 2))));
            result = BigDecimal.ONE.divide(r.result, scale, RoundingMode.HALF_UP);
            prev = result;
            n++;
            should_continue = r.should_continue;
        }
*/

