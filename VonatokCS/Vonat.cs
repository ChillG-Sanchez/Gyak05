using System;

public class Vonat
{
    public string VonatSzam { get; set; }
    public DateTime IndulasiIdo { get; set; }
    public DateTime ErkezesiIdo { get; set; }
    public string Utvonal { get; set; }

    public Vonat(string vonatSzam, DateTime indulasiIdo, DateTime erkezesiIdo, string utvonal)
    {
        VonatSzam = vonatSzam;
        IndulasiIdo = indulasiIdo;
        ErkezesiIdo = erkezesiIdo;
        Utvonal = utvonal;
    }

    public TimeSpan UtazasiIdo
    {
        get
        {
            return ErkezesiIdo - IndulasiIdo;
        }
    }

    public override string ToString()
    {
        return $"Vonat száma: {VonatSzam}, Indulási idő: {IndulasiIdo}, Érkezési idő: {ErkezesiIdo}, Útvonal: {Utvonal}, Utazási idő: {UtazasiIdo}";
    }
}