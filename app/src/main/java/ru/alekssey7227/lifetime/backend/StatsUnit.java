package ru.alekssey7227.lifetime.backend;

public class StatsUnit {
    private int id;

    private int goalId;

    private int day;

    private int month;

    private int year;

    private Time estimatedTime;

    public StatsUnit(int id, int goalId, int day, int month, int year, Time estimatedTime) {
        this.id = id;
        this.goalId = goalId;
        this.day = day;
        this.month = month;
        this.year = year;
        this.estimatedTime = estimatedTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Time getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Time estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public void addTime(long et){
        estimatedTime.setTimeInMinutes(estimatedTime.getTimeInMinutes() + et);
    }
}
