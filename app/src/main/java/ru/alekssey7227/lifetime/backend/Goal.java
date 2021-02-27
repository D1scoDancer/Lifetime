package ru.alekssey7227.lifetime.backend;

import java.text.DecimalFormat;

public class Goal {
    private static long DEFAULT_ITERATION = 60;

    private int id;

    private String name;

    /**
     * Stores time in minutes
     */
    private long time;

    private long iteration;

    public Goal(int id, String name, long time, long iteration) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.iteration = iteration;
    }

    public Goal(int id, String name, long time) {
        this.id = id;
        this.name = name;
        this.time = time;
        iteration = DEFAULT_ITERATION;
    }

    public Goal(int id) {
        this.id = id;
        name = "Chess";
        time = 200;
        iteration = DEFAULT_ITERATION;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getIteration() {
        return iteration;
    }

    public void setIteration(long iteration) {
        this.iteration = iteration;
    }

    public double getTimeInHours() {
        DecimalFormat df = new DecimalFormat("#.##");
        String s = df.format(time / 60.0);
        return Double.parseDouble(s);
    }
}
