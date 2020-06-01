package com.company;

import javax.annotation.processing.SupportedSourceVersion;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.sqrt;

public class StarterClass {
    private static int[] handle_args(String[] args)
    {
        int[] result = new int[2];
        if(args.length == 4)
        {
            if(args[0].equals("-p"))
            {
                result[0] = Integer.parseInt(args[1]);
            }
            else
            {
                System.out.println("Invalid argument for precision, please set it with -p <number>. Setting it to 10240");
                result[0] = 10240;
            }

            if(args[2].equals("-t") || args[2].equals("-tasks"))
            {
                result[1] = Integer.parseInt(args[3]);
            }
            else
            {
                System.out.println("Invalid argument for thread count, please set it with -t <number>. Setting it to 1");
                result[1] = 1;
            }
        }
        else if(args.length == 2)
        {
            if(args[0].equals("-p"))
            {
                result[0] = Integer.parseInt(args[1]);
            }
            else
            {
                System.out.println("Invalid argument for precision, please set it with -p <number>. Setting it to 10240");
                result[0] = 10240;
            }

            int max_threads = Runtime.getRuntime().availableProcessors();
            System.out.print("-t argument not passed, using all available threads: ");
            System.out.println(max_threads);
            result[1] = max_threads;
        }
        else
        {
            System.out.println("Invalid command line arguments, setting the default values (-p 10240 -t 1)");
            result[0] = 10240;
            result[1] = 1;
        }

        return result;
    }
    public static void main(String[] args) {
        int[] parsed_args = handle_args(args);
        int scale = parsed_args[0];
        int threads_count = parsed_args[1];

        Thread[] threads = new Thread[threads_count];
        Calculations[] workers = new Calculations[threads_count];
        BigDecimal prev = BigDecimal.ZERO;
        BigDecimal result = BigDecimal.ZERO;
        
        int n = 0;
        boolean should_continue = true;


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
        System.out.println(result);
        //System.out.println(BigDecimal.ONE.divide(r.result, scale, RoundingMode.HALF_UP));
    }
    //0.3184713375796178
}
