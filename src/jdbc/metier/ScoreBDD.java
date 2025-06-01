package jdbc.metier;

public class ScoreBDD {
    private final int id;
    private final int idJoueur;
    private final int idPartie;
    private final int score;

    public ScoreBDD(int id, int idJoueur, int idPartie, int score) {
        this.id = id;
        this.idJoueur = idJoueur;
        this.idPartie = idPartie;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public int getIdJoueur() {
        return idJoueur;
    }

    public int getIdPartie() {
        return idPartie;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "ScoreBDD{id=" + id + ", idJoueur=" + idJoueur + ", idPartie=" + idPartie + ", score=" + score + "}";
    }
}
