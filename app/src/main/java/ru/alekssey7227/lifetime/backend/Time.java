package ru.alekssey7227.lifetime.backend;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

//TODO: getProperTime() - класс сам определяет в каком формате вернуть время
public class Time {

    private static final DecimalFormat decimalFormat;

    private long minutes;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator('.');
        decimalFormat = new DecimalFormat("0.##", symbols);
    }

    public Time(long minutes) {
        this.minutes = minutes;
    }

    public long getTimeInMinutes() {
        return minutes;
    }

    public void setTimeInMinutes(long minutes) {
        this.minutes = minutes;
    }

    public double getTimeInHours() {
        return minutes / 60.0;
    }

    public void setTimeInHours(double hours) {
        this.minutes = (long) (hours * 60);
    }

    public double getTimeInDays() {
        return minutes / 60.0 / 24;
    }

    public void setTimeInDays(long days) {
        this.minutes = days * 24 * 60;
    }

    public String getTimeInMinutesString() {
        return decimalFormat.format(minutes);
    }

    public String getTimeInHoursString() {
        return decimalFormat.format(getTimeInHours());
    }

    public String getTimeInDaysString() {
        return decimalFormat.format(getTimeInDays());
    }

    public String getTimeInMinutesStringFormatted() {
        return decimalFormat.format(minutes) + " m";
    }

    public String getTimeInHoursStringFormatted() {
        return decimalFormat.format(getTimeInHours()) + " h";
    }

    public String getTimeInDaysStringFormatted() {
        return decimalFormat.format(getTimeInDays() + " d");
    }

    @NonNull
    @Override
    public String toString() {
        return getTimeInMinutesString();
    }
}
