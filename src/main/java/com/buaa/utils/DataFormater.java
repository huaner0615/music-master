package com.buaa.utils;

import java.text.DecimalFormat;

/**
 * Created by Window10 on 2016/3/17.
 */
public class DataFormater {
    private static DecimalFormat df;

    static {
        df = new DecimalFormat("0.00");
    }

    public static String miliToMinite(int mili) {
        return df.format(mili/60000.0);
    }
}
