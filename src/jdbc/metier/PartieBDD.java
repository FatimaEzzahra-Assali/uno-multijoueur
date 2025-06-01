package jdbc.metier;

import java.time.LocalDateTime;

public class PartieBDD {
    private final int id;
    private final LocalDateTime date;

    public PartieBDD(int id, LocalDateTime date) {
        this.id = id;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "PartieBDD{id=" + id + ", date=" + date + "}";
    }
}
