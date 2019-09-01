package example;

import java.time.LocalTime;

public class Record {
    private final Type type;
    private final LocalTime time;

    public Record(Type type, LocalTime time) {
        this.type = type;
        this.time = time;
    }

    public Type getType() {
        return type;
    }

    public LocalTime getTime() {
        return time;
    }
}
