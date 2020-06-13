package com.company;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.StrictMath.pow;

public class CalculationsBasedOnPrevious implements Runnable {
    BigDecimal first_const_num = BigDecimal.ONE;
    BigDecimal first_const_denom = BigDecimal.ONE;
    BigDecimal second_const_first_part = BigDecimal.ONE;
    BigDecimal second_const_second_part = BigDecimal.ONE;

    BigDecimal first_const = BigDecimal.ONE;
    BigDecimal second_const = BigDecimal.ONE;

    BigDecimal final_const = BigDecimal.ONE;
    BigDecimal result;
    BigDecimal base;
    int target_index;
    int base_index;
    int scale;
    boolean work_done;

    public CalculationsBasedOnPrevious(BigDecimal base, int base_index, int target_index, int scale)
    {
        this.base = base;
        this.result = BigDecimal.ONE;
        this.target_index = target_index;
        this.scale = scale;
        this.work_done = false;
        this.base_index = base_index;
    }
    public void run(){
        BigDecimal temp_sum = base;
        for(int current_index = base_index; current_index < target_index; current_index++)
        {
            for(int i = 1; i <= 3; i++)
            {
                first_const_num = first_const_num.multiply(BigDecimal.valueOf((4* current_index) + i));
            }
            first_const_denom = BigDecimal.valueOf(64);
            first_const_denom = first_const_denom.multiply(BigDecimal.valueOf(pow((current_index +1),3)));

            first_const = first_const_num.divide(first_const_denom, scale, RoundingMode.HALF_UP); //Correct

            second_const_first_part = BigDecimal.ONE.divide(BigDecimal.valueOf(pow(99, 4)), scale, RoundingMode.HALF_UP);

            second_const_second_part = BigDecimal.valueOf(26390);
            BigDecimal temp = BigDecimal.valueOf(pow(99, 4));
            BigDecimal temp2 = BigDecimal.valueOf(1103 + (26390* current_index));
            second_const_second_part = second_const_second_part.divide(temp.multiply(temp2), scale, RoundingMode.HALF_UP); //Correct

            second_const = second_const_first_part.add(second_const_second_part);

            final_const = first_const.multiply(second_const);

            temp_sum = temp_sum.multiply(final_const);
        }
        result = temp_sum;
        work_done = true;

    }
}