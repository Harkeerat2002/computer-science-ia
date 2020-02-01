package com.school.comsciia.healthapp.Models;

import java.util.Calendar;
import java.util.Date;

public class DateValue {
    public String value;
    public int date;

    public DateValue(){

    }

    public DateValue(String value) {
        this.value = value;
        this.date = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    }
}
