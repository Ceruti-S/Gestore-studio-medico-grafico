public static void testRipristinoLog() throws IOException {
    System.out.println("--- INIZIO TEST MANOMISSIONE ---");

    // 1. Creiamo un oggetto "fake" per sporcare il file
    String datoCorrotto = "QUESTO FILE E' STATO MANOMESSO";

    // 2. Sovrascriviamo forzatamente il file LOG principale (ma NON il backup)
    // Usiamo direttamente Files.write per scrivere immondizia nel file
    Files.write(ConfigFile.LOG_FILE, datoCorrotto.getBytes());
    System.out.println("File log.dat manomesso con successo.");

    // 3. Ora chiamiamo il tuo controllo
    System.out.println("Avvio ControlloDatiIniziale.validaDati()...");
    ControlloDatiIniziale.validaDati();

    // 4. Verifica finale: proviamo a leggere il log
    try {
        Object obj = LetturaFile.leggiFileCifrato(ConfigFile.LOG_FILE);
        if (obj instanceof ArrayList) {
            System.out.println("TEST RIUSCITO: Il file è stato ripristinato ed è di nuovo un ArrayList.");
        }
    } catch (Exception e) {
        System.out.println("TEST FALLITO: Il file è ancora corrotto.");
    }
}

try
        {

testRipristinoLog();
        }
                catch(IOException e)
        {

        }