package com.company;

import javax.annotation.processing.SupportedSourceVersion;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.sqrt;

public class Main {

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

    public static BigDecimal calc(int scale)
    {
        BigDecimal prev = BigDecimal.ZERO;
        BigDecimal res = BigDecimal.ZERO;
        int n = 0;
        boolean should_continue = true;
        while(should_continue){
            BigDecimal first_num = recfact(1, (4*n));
            BigDecimal first_denom = BigDecimal.valueOf(pow(4 ,n));
            first_denom = first_denom.multiply(recfact(1, n));
            first_denom = first_denom.pow(4);

            BigDecimal second_num = BigDecimal.valueOf(1103 + (26390*n));
            BigDecimal second_denom = BigDecimal.valueOf(pow(99, 4*n));

            BigDecimal first = first_num.divide(first_denom,scale, RoundingMode.HALF_UP);
            BigDecimal second = second_num.divide(second_denom, scale, RoundingMode.HALF_UP);

            res = res.add(first.multiply(second));
            if(prev.subtract(res).compareTo(BigDecimal.ZERO) == 0)
            {
                should_continue = false;
            }
            prev = res;
            n++;

        }
        res = res.multiply(BigDecimal.valueOf(sqrt(8)/(pow(99,2))));
        return res;
    }
    public static void main(String[] args) {
        int scale = 200;
        BigDecimal result = calc(scale);
        System.out.println(BigDecimal.ONE.divide(result, scale, RoundingMode.HALF_UP));
    }
    //0.3184713375796178
}
