package ru.alekssey7227.lifetime.backend;

public class Goal {
    private static final Time DEFAULT_ITERATION = new Time(60);

    private int id;

    private String name;

    private Time time;

    private Time iteration;

    public Goal(int id, String name, Time time, Time iteration) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.iteration = iteration;
    }

    public Goal(int id, String name, long time, long iteration){
        this.id = id;
        this.name = name;
        this.time = new Time(time);
        this.iteration = new Time(iteration);
    }

    public Goal(int id, String name, Time time) {
        this.id = id;
        this.name = name;
        this.time = time;
        iteration = DEFAULT_ITERATION;
    }

    public Goal(int id) {
        this.id = id;
        name = "Chess";
        time = new Time(0);
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

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Time getIteration() {
        return iteration;
    }

    public void setIteration(Time iteration) {
        this.iteration = iteration;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", iteration=" + iteration +
                '}';
    }
}
