import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Vonat {
    private String vonatSzam;
    private LocalDateTime indulasiIdo;
    private LocalDateTime erkezesiIdo;
    private String utvonal;

    public Vonat(String vonatSzam, LocalDateTime indulasiIdo, LocalDateTime erkezesiIdo, String utvonal) {
        this.vonatSzam = vonatSzam;
        this.indulasiIdo = indulasiIdo;
        this.erkezesiIdo = erkezesiIdo;
        this.utvonal = utvonal;
    }

    public String getVonatSzam() {
        return vonatSzam;
    }

    public void setVonatSzam(String vonatSzam) {
        this.vonatSzam = vonatSzam;
    }

    public LocalDateTime getIndulasiIdo() {
        return indulasiIdo;
    }

    public void setIndulasiIdo(LocalDateTime indulasiIdo) {
        this.indulasiIdo = indulasiIdo;
    }

    public LocalDateTime getErkezesiIdo() {
        return erkezesiIdo;
    }

    public void setErkezesiIdo(LocalDateTime erkezesiIdo) {
        this.erkezesiIdo = erkezesiIdo;
    }

    public String getUtvonal() {
        return utvonal;
    }

    public void setUtvonal(String utvonal) {
        this.utvonal = utvonal;
    }

    public Duration getUtazasiIdo() {
        return Duration.between(indulasiIdo, erkezesiIdo);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Vonat száma: " + vonatSzam + ", Indulási idő: " + indulasiIdo.format(formatter) +
               ", Érkezési idő: " + erkezesiIdo.format(formatter) + ", Útvonal: " + utvonal +
               ", Utazási idő: " + getUtazasiIdo().toHours() + " óra " + getUtazasiIdo().toMinutesPart() + " perc";
    }
}