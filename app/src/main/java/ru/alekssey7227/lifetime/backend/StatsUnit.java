package ru.alekssey7227.lifetime.backend;

public class StatsUnit {
    private int id;

    private int goalId;

    private long day;

    private int estimatedTime;

    public StatsUnit(int id, int goalId, long day, int estimatedTime) {
        this.id = id;
        this.goalId = goalId;
        this.day = day;
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

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}
