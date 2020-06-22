package com.company;

import javax.annotation.processing.SupportedSourceVersion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.Semaphore;

import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.sqrt;

public class StarterClass {
    private static Semaphore mutex;
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

    public static BigDecimal recfact(long start, long n) {
        long i;
        if (n <= 16) {
            BigDecimal r = new BigDecimal(start);
            for (i = start + 1; i < start + n; i++) r = r.multiply(BigDecimal.valueOf(i));
            return r;
        }
        i = n / 2;
        return recfact(start, i).multiply(recfact(start + i, n - i));
    }

    public static BigDecimal calculate_certain_n(int n, int scale)
    {
        BigDecimal result = BigDecimal.ONE;

        BigDecimal first_part = BigDecimal.valueOf(sqrt(8));
        first_part = first_part.divide(BigDecimal.valueOf(pow(99, 2)), scale, RoundingMode.HALF_UP);

        BigDecimal second_part = recfact(1, 4*n);

        BigDecimal second_part_denom_first_part = BigDecimal.valueOf(pow(4, n));
        BigDecimal second_part_denom_second_part = recfact(1, n);
        BigDecimal second_part_denom = second_part_denom_first_part.multiply(second_part_denom_second_part);
        second_part_denom = second_part_denom_first_part.pow(4);

        second_part = second_part.divide(second_part_denom, scale, RoundingMode.HALF_UP);

        BigDecimal third_part = BigDecimal.valueOf(1103 + (26390*n));
        BigDecimal third_part_denom = BigDecimal.valueOf(99);
        third_part_denom = third_part_denom.pow(4*n);
        third_part = third_part.divide(third_part_denom, scale, RoundingMode.HALF_UP);

        result = result.multiply(first_part);
        result = result.multiply(second_part);
        result = result.multiply(third_part);

        return result;
    }
    public static void main(String[] args) {
        mutex = new Semaphore(1);
        int[] parsed_args = handle_args(args);
        int amount_of_elements = parsed_args[0];
        int scale = 1000;
        int threads_count = parsed_args[1];

        System.out.println("Amount of elements: " + amount_of_elements + ", Threads: " + threads_count);

        BigDecimal result = BigDecimal.ZERO;
        BigDecimal[] elements = new BigDecimal[amount_of_elements];
        List<Stack<Integer>> tasks = new ArrayList<Stack<Integer>>();

        for(int i = 0; i < threads_count; i++)
        {
            tasks.add(new Stack<Integer>());
        }
        int amount_of_work_per_thread = (amount_of_elements / threads_count);
        if(amount_of_elements % threads_count != 0)
        {
            amount_of_work_per_thread += 1;
        }

        int[] last_calculated_index = new int[threads_count];
        int calculated_elements = 0;
        for(int i = 0; i < threads_count; i++)
        {
            elements[amount_of_work_per_thread * i] = calculate_certain_n(amount_of_work_per_thread * i, scale);
            last_calculated_index[i] = amount_of_work_per_thread * i;
            calculated_elements++;
            result = result.add(elements[amount_of_work_per_thread * i]);
        }
        for(int current_thread = 0; current_thread < threads_count; current_thread++)
        {
            for(int i = amount_of_work_per_thread - 1; i > 0; i--)
            {
                tasks.get(current_thread).push(i + (current_thread*amount_of_work_per_thread));
            }
        }

        Thread[] threads = new Thread[threads_count];
        CalculationsBasedOnPrevious[] workers = new CalculationsBasedOnPrevious[threads_count];
        for(int current_thread = 0; current_thread < threads_count; current_thread++)
        {
            workers[current_thread] = new CalculationsBasedOnPrevious(elements[amount_of_work_per_thread * current_thread],
                    last_calculated_index[current_thread], tasks.get(current_thread).pop(),
                    scale);
            threads[current_thread] = new Thread(workers[current_thread]);
            threads[current_thread].start();
        }
        while(calculated_elements + threads_count < amount_of_elements){
            for(int current_thread = 0; current_thread < threads_count; current_thread++)
            {
                if(workers[current_thread].work_done)
                {
                    try {
                        threads[current_thread].join();
                    } catch (InterruptedException ex) {
                        System.out.print("Error");
                    }
                    try{
                        mutex.acquire();
                        result = result.add(workers[current_thread].result);
                    }catch (InterruptedException e) {
                        // exception handling code
                    } finally {
                        mutex.release();
                    }

                    last_calculated_index[current_thread] = workers[current_thread].target_index;
                    elements[last_calculated_index[current_thread]] = workers[current_thread].result;
                    if(!tasks.get(current_thread).empty())
                    {
                        workers[current_thread] = new CalculationsBasedOnPrevious(elements[last_calculated_index[current_thread]],
                                last_calculated_index[current_thread], tasks.get(current_thread).pop(), scale);

                        threads[current_thread] = new Thread(workers[current_thread]);
                        threads[current_thread].start();

                        try{
                            mutex.acquire();
                            calculated_elements++;
                        }catch (InterruptedException e) {
                            // exception handling code
                        } finally {
                            mutex.release();
                        }
                    }
                    else{
                        workers[current_thread].work_done = false;
                    }
                }
            }
        }
        System.out.println(BigDecimal.ONE.divide(result, scale, RoundingMode.HALF_UP));
    }
}
//3.14159265358979279401149285925126360843227873615132156357983493056932100312250770614203052737207380256736559113
//0,318309878


