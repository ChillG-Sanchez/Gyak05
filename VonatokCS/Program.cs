using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;

public class Program
{
    public static void Main(string[] args)
    {
        List<Vonat> vonatok = BeolvasVonatok("train_schedule.csv");

        ListazVonatok(vonatok);

        DateTime keresesiIdo = new DateTime(2024, 9, 17, 10, 0, 0);
        Vonat legkozelebbiVonat = KeresLegkozelebbiInduloVonat(vonatok, keresesiIdo);
        Console.WriteLine("Legközelebb induló vonat: " + legkozelebbiVonat);

        Vonat leghosszabbUtazasVonat = KeresLeghosszabbUtazasiIdoVonat(vonatok);
        Console.WriteLine("Leghosszabb utazási időtartamú vonat: " + leghosszabbUtazasVonat);

        DateTime idopont = new DateTime(2024, 9, 17, 12, 0, 0);
        List<Vonat> indulasiIdoElott = KeresVonatokIndulasiIdoElott(vonatok, idopont);
        Console.WriteLine("Vonatok indulása " + idopont + " előtt:");
        ListazVonatok(indulasiIdoElott);

        string utvonal = "Budapest-Debrecen";
        List<Vonat> utvonalVonatok = KeresVonatokUtvonalSzerint(vonatok, utvonal);
        Console.WriteLine("Vonatok az útvonalon: " + utvonal);
        ListazVonatok(utvonalVonatok);

        var csoportositottVonatok = CsoportositVonatokUtvonalSzerint(vonatok);
        Console.WriteLine("Vonatok csoportosítása útvonal szerint:");
        foreach (var csoport in csoportositottVonatok)
        {
            Console.WriteLine("Útvonal: " + csoport.Key);
            ListazVonatok(csoport.Value);
        }

        TimeSpan atlagosUtazasiIdo = SzamolAtlagosUtazasiIdo(vonatok);
        Console.WriteLine("Átlagos utazási idő: " + atlagosUtazasiIdo);

        IrjKesesVonatokatFajlba(vonatok, "keses_vonatmenetrend.csv");
    }

    public static List<Vonat> BeolvasVonatok(string fajlNev)
{
    List<Vonat> vonatok = new List<Vonat>();
    using (StreamReader sr = new StreamReader(fajlNev))
    {
        string sor;
        sr.ReadLine();
        while ((sor = sr.ReadLine()) != null)
        {
            string[] adatok = sor.Split(',');
            string vonatSzam = adatok[0];
            DateTime indulasiIdo = DateTime.ParseExact(adatok[1], "yyyy-MM-dd HH:mm", CultureInfo.InvariantCulture);
            DateTime erkezesiIdo = DateTime.ParseExact(adatok[2], "yyyy-MM-dd HH:mm", CultureInfo.InvariantCulture);
            string utvonal = adatok[3];
            vonatok.Add(new Vonat(vonatSzam, indulasiIdo, erkezesiIdo, utvonal));
        }
    }
    return vonatok;
}

    public static void ListazVonatok(List<Vonat> vonatok)
    {
        foreach (var vonat in vonatok)
        {
            Console.WriteLine(vonat);
        }
    }

    public static Vonat KeresLegkozelebbiInduloVonat(List<Vonat> vonatok, DateTime keresesiIdo)
    {
        return vonatok
            .Where(v => v.IndulasiIdo > keresesiIdo)
            .OrderBy(v => v.IndulasiIdo)
            .FirstOrDefault();
    }

    public static Vonat KeresLeghosszabbUtazasiIdoVonat(List<Vonat> vonatok)
    {
        return vonatok
            .OrderByDescending(v => v.UtazasiIdo)
            .FirstOrDefault();
    }

    public static List<Vonat> KeresVonatokIndulasiIdoElott(List<Vonat> vonatok, DateTime idopont)
    {
        return vonatok
            .Where(v => v.IndulasiIdo < idopont)
            .ToList();
    }

    public static List<Vonat> KeresVonatokUtvonalSzerint(List<Vonat> vonatok, string utvonal)
    {
        return vonatok
            .Where(v => v.Utvonal == utvonal)
            .ToList();
    }

    public static Dictionary<string, List<Vonat>> CsoportositVonatokUtvonalSzerint(List<Vonat> vonatok)
    {
        return vonatok
            .GroupBy(v => v.Utvonal)
            .ToDictionary(g => g.Key, g => g.ToList());
    }

    public static TimeSpan SzamolAtlagosUtazasiIdo(List<Vonat> vonatok)
    {
        double atlagosPerc = vonatok
            .Average(v => v.UtazasiIdo.TotalMinutes);
        return TimeSpan.FromMinutes(atlagosPerc);
    }

    public static void IrjKesesVonatokatFajlba(List<Vonat> vonatok, string fajlNev)
    {
        using (StreamWriter sw = new StreamWriter(fajlNev))
        {
            foreach (var vonat in vonatok)
            {
                DateTime kesesErkezesiIdo = vonat.ErkezesiIdo.AddMinutes(15);
                sw.WriteLine($"{vonat.VonatSzam},{vonat.IndulasiIdo:yyyy-MM-dd HH:mm},{kesesErkezesiIdo:yyyy-MM-dd HH:mm},{vonat.Utvonal}");
            }
        }
    }
}