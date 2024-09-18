import java.time.Duration;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

public class Program {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        List<Vonat> vonatok = beolvasVonatok("train_schedule.csv");

        listazVonatok(vonatok);

        LocalDateTime keresesiIdo = LocalDateTime.of(2024, 9, 17, 10, 0);
        Vonat legkozelebbiVonat = keresLegkozelebbiInduloVonat(vonatok, keresesiIdo);
        System.out.println("Legközelebb induló vonat: " + legkozelebbiVonat);

        Vonat leghosszabbUtazasVonat = keresLeghosszabbUtazasiIdoVonat(vonatok);
        System.out.println("Leghosszabb utazási időtartamú vonat: " + leghosszabbUtazasVonat);

        LocalDateTime idopont = LocalDateTime.of(2024, 9, 17, 12, 0);
        List<Vonat> indulasiIdoElott = keresVonatokIndulasiIdoElott(vonatok, idopont);
        System.out.println("Vonatok indulása " + idopont.format(formatter) + " előtt:");
        listazVonatok(indulasiIdoElott);

        String utvonal = "Budapest-Debrecen";
        List<Vonat> utvonalVonatok = keresVonatokUtvonalSzerint(vonatok, utvonal);
        System.out.println("Vonatok az útvonalon: " + utvonal);
        listazVonatok(utvonalVonatok);

        Map<String, List<Vonat>> csoportositottVonatok = csoportositVonatokUtvonalSzerint(vonatok);
        System.out.println("Vonatok csoportosítása útvonal szerint:");
        csoportositottVonatok.forEach((key, value) -> {
            System.out.println("Útvonal: " + key);
            listazVonatok(value);
        });

        Duration atlagosUtazasiIdo = szamolAtlagosUtazasiIdo(vonatok);
        System.out.println("Átlagos utazási idő: " + atlagosUtazasiIdo.toHours() + " óra " + atlagosUtazasiIdo.toMinutesPart() + " perc");

        irjKesesVonatokatFajlba(vonatok, "keses_vonatmenetrend.csv");
    }

    public static List<Vonat> beolvasVonatok(String fajlNev) {
        List<Vonat> vonatok = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fajlNev))) {
            String sor;
            br.readLine();
            while ((sor = br.readLine()) != null) {
                String[] adatok = sor.split(",");
                String vonatSzam = adatok[0];
                LocalDateTime indulasiIdo = LocalDateTime.parse(adatok[1], formatter);
                LocalDateTime erkezesiIdo = LocalDateTime.parse(adatok[2], formatter);
                String utvonal = adatok[3];
                vonatok.add(new Vonat(vonatSzam, indulasiIdo, erkezesiIdo, utvonal));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vonatok;
    }

    public static void listazVonatok(List<Vonat> vonatok) {
        vonatok.forEach(System.out::println);
    }

    public static Vonat keresLegkozelebbiInduloVonat(List<Vonat> vonatok, LocalDateTime keresesiIdo) {
        return vonatok.stream()
                .filter(v -> v.getIndulasiIdo().isAfter(keresesiIdo))
                .min((v1, v2) -> v1.getIndulasiIdo().compareTo(v2.getIndulasiIdo()))
                .orElse(null);
    }

    public static Vonat keresLeghosszabbUtazasiIdoVonat(List<Vonat> vonatok) {
        return vonatok.stream()
                .max((v1, v2) -> v1.getUtazasiIdo().compareTo(v2.getUtazasiIdo()))
                .orElse(null);
    }

    public static List<Vonat> keresVonatokIndulasiIdoElott(List<Vonat> vonatok, LocalDateTime idopont) {
        return vonatok.stream()
                .filter(v -> v.getIndulasiIdo().isBefore(idopont))
                .collect(Collectors.toList());
    }

    public static List<Vonat> keresVonatokUtvonalSzerint(List<Vonat> vonatok, String utvonal) {
        return vonatok.stream()
                .filter(v -> v.getUtvonal().equals(utvonal))
                .collect(Collectors.toList());
    }

    public static Map<String, List<Vonat>> csoportositVonatokUtvonalSzerint(List<Vonat> vonatok) {
        return vonatok.stream()
                .collect(Collectors.groupingBy(Vonat::getUtvonal));
    }

    public static Duration szamolAtlagosUtazasiIdo(List<Vonat> vonatok) {
        long osszesPerc = vonatok.stream()
                .mapToLong(v -> v.getUtazasiIdo().toMinutes())
                .sum();
        return Duration.ofMinutes(osszesPerc / vonatok.size());
    }

    public static void irjKesesVonatokatFajlba(List<Vonat> vonatok, String fajlNev) {
        try (FileWriter writer = new FileWriter(fajlNev)) {
            for (Vonat vonat : vonatok) {
                LocalDateTime kesesErkezesiIdo = vonat.getErkezesiIdo().plusMinutes(15);
                writer.write(vonat.getVonatSzam() + "," + vonat.getIndulasiIdo().format(formatter) + "," +
                             kesesErkezesiIdo.format(formatter) + "," + vonat.getUtvonal() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}