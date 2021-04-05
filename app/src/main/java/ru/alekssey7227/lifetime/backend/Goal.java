package ru.alekssey7227.lifetime.backend;

public class Goal {
    private static final Time DEFAULT_ITERATION = new Time(60);

    private int id;

    private String name;

    private Time time;

    private Time iteration;

    private int image;

    public Goal(int id, String name, Time time, Time iteration, int image) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.iteration = iteration;
        this.image = image;
    }

    public Goal(int id, String name, long time, long iteration, int image){
        this.id = id;
        this.name = name;
        this.time = new Time(time);
        this.iteration = new Time(iteration);
        this.image = image;
    }

    public Goal(int id, String name, Time time) {
        this.id = id;
        this.name = name;
        this.time = time;
        iteration = DEFAULT_ITERATION;
        image = 0;
    }

    public Goal(int id) {
        this.id = id;
        name = "Chess";
        time = new Time(0);
        iteration = DEFAULT_ITERATION;
        image = 0;
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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void increment() {
        time.setTimeInMinutes(time.getTimeInMinutes() + iteration.getTimeInMinutes());
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
