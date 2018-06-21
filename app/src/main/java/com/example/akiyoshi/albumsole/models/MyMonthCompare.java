package com.example.akiyoshi.albumsole.models;

import java.util.Comparator;

public class MyMonthCompare implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        int argu1[] = SeparateMonthYear(o1);
        int argu2[] = SeparateMonthYear(o2);

        if(argu1[1] < argu2[1]){
            return 1;
        }else if(argu1[1] > argu2[1]){
            return -1;
        }else{
            if(argu1[0] < argu2[0]){
                return 1;
            }else if(argu1[0] > argu2[0]){
                return -1;
            }else{
                return 0;
            }
        }
    }

    public int[] SeparateMonthYear(String str) {
        String strs[] = str.split(",");
        String month = strs[0];
        int year = Integer.parseInt(strs[1].trim());
        int numMonth = 0;

        switch (month) {
            case "January":
                numMonth = 1;
                break;
            case "February":
                numMonth = 2;
                break;
            case "March":
                numMonth = 3;
                break;
            case "April":
                numMonth = 4;
                break;
            case "May":
                numMonth = 5;
                break;
            case "June":
                numMonth = 6;
                break;
            case "July":
                numMonth = 7;
                break;
            case "August":
                numMonth = 8;
                break;
            case "September":
                numMonth = 9;
                break;
            case "October":
                numMonth = 10;
                break;
            case "November":
                numMonth = 11;
                break;
            case "December":
                numMonth = 12;
                break;
        }

        int result[] = new int[2];
        result[0] = numMonth;
        result[1] = year;
        return result;
    }
}
