package jdbc.metier;

public class JoueurBDD {
    private final int id;
    private final String pseudo;

    public JoueurBDD(int id, String pseudo) {
        this.id = id;
        this.pseudo = pseudo;
    }

    public int getId() {
        return id;
    }

    public String getPseudo() {
        return pseudo;
    }

    @Override
    public String toString() {
        return "JoueurBDD{id=" + id + ", pseudo='" + pseudo + "'}";
    }

}
