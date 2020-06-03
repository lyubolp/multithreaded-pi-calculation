package com.company;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.sqrt;

public class CalculationsBasedOnPrevious implements Runnable {
    BigDecimal first_const_num = BigDecimal.ONE;
    BigDecimal first_const_denom = BigDecimal.ONE;
    BigDecimal second_const_first_part = BigDecimal.ONE;
    BigDecimal second_const_second_part = BigDecimal.ONE;

    BigDecimal first_const = BigDecimal.ONE;
    BigDecimal second_const = BigDecimal.ONE;

    BigDecimal final_const = BigDecimal.ONE;
    BigDecimal result;
    BigDecimal previous;
    int n;
    int scale;
    boolean work_done;

    public CalculationsBasedOnPrevious(BigDecimal result, BigDecimal previous,  int n, int scale)
    {
        this.previous = previous;
        this.result = result;
        this.n = n;
        this.scale = scale;
        this.work_done = false;
    }
    public void run(){
        for(int i = 1; i <= 3; i++)
        {
            first_const_num = first_const_num.multiply(BigDecimal.valueOf((4*n) + i));
        }
        first_const_denom = BigDecimal.valueOf(64);
        first_const_denom = first_const_denom.multiply(BigDecimal.valueOf(pow((n+1),3)));

        first_const = first_const_num.divide(first_const_denom, scale, RoundingMode.HALF_UP); //Correct

        second_const_first_part = BigDecimal.ONE.divide(BigDecimal.valueOf(pow(99, 4)), scale, RoundingMode.HALF_UP);

        second_const_second_part = BigDecimal.valueOf(26390);
        BigDecimal temp = BigDecimal.valueOf(pow(99, 4));
        BigDecimal temp2 = BigDecimal.valueOf(1103 + (26390*n));
        second_const_second_part = second_const_second_part.divide(temp.multiply(temp2), scale, RoundingMode.HALF_UP); //Correct

        second_const = second_const_first_part.add(second_const_second_part);

        final_const = first_const.multiply(second_const);

        result = previous.add(previous.multiply(final_const));
        //System.out.println("n0+n1:" + result.toPlainString()); //n1 is okay
        //n0+n1 = 0,318309886
        work_done = true;

    }
}