package com.company;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.sqrt;

public class Calculations implements Runnable {
    BigDecimal prev;
    BigDecimal result;
    int n;
    int scale;
    boolean should_continue;
    boolean work_done;

    public Calculations(BigDecimal result, int n, int scale)
    {
        this.result = result;
        this.n = n;
        this.scale = scale;
        this.work_done = false;
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

    public void run()
    {
        BigDecimal first_num = recfact(1, (4*this.n));
        BigDecimal first_denom = BigDecimal.valueOf(4);
        first_denom = first_denom.pow(n);
        first_denom = first_denom.multiply(recfact(1, n));
        first_denom = first_denom.pow(4);

        BigDecimal second_num = BigDecimal.valueOf(1103 + (26390*n));
        BigDecimal second_denom = BigDecimal.valueOf(99);
        second_denom = second_denom.pow(4*n);

        BigDecimal first = first_num.divide(first_denom,scale, RoundingMode.HALF_UP);
        BigDecimal second = second_num.divide(second_denom, scale, RoundingMode.HALF_UP);

        BigDecimal first_const = BigDecimal.valueOf(sqrt(8) / (pow(99, 2)));
        BigDecimal sum = first.multiply(second);

        result = result.add(first_const.multiply(sum));
        work_done = true;

    }
}
