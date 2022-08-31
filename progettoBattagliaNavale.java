import java.util.Random;
import java.util.Scanner;

import javax.management.openmbean.OpenType;

public class progettoBattagliaNavale {

    public static char QUADRATO_NAVE_POSIZIONATA = 0x2593; // Nave posizionata
    public static char QUADRATO_COLPITO = 0x2592; // Nave colpita
    public static char QUADRATO_ACQUA = ' '; //
    public static char QUADRATO_NON_COLPITO = 'O'; // Nave non colpita, colpo mancato

    public static int ORIENTAMENTO_ALTO = 0;
    public static int ORIENTAMENTO_BASSO = 1;
    public static int ORIENTAMENTO_DESTRA = 2;
    public static int ORIENTAMENTO_SINISTRA = 3;
    public static int DIMENSIONE_TABELLA = 10;
    public static int MAX_NAVI = 4;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        char[][] tabellaAI = tabellaAI();
        
        // Non stampo la tabella AI una volta creata altrimenti vedo le navi di AI
        //stampa(tabellaAI);

        char[][] tabellaGiocatore = tabellaGiocatore();

        char[][] tabellaMosseAI = pulisciTabella();

        char[][] tabellaMosseGiocatore = pulisciTabella();

        partita(tabellaAI, tabellaGiocatore, tabellaMosseAI, tabellaMosseGiocatore);

    }

    public static int coordinataRiga() {
        Scanner in = new Scanner(System.in);
        int riga;

        System.out.println("Inserisci la riga (1, 2...)");
        riga = in.nextInt();

        if (riga < 1 || riga > DIMENSIONE_TABELLA) {
            System.out.println("Valore di riga errato");
            return -1;
        }

        return riga - 1; // Tolgo -1 perchè l'inserimento della riga parte da 0
    }

    public static int coordinataColonna() {
        Scanner in = new Scanner(System.in);
        char colonna;

        System.out.println("Inserisci la colonna (a, b...) oppure ! per interrompere la partita");

        colonna = in.next().charAt(0);

        if (colonna == '!')
            return -2;

        if (colonna < 97 || colonna > 97 + DIMENSIONE_TABELLA) {
            System.out.println("Valore di colonna errato");
            return -1;
        }

        return colonna - 97; // Tolgo 97 perchè il carattere 'a' ha valore ascii 97 e per riportarlo ad un
                             // valore intero 0 tolgo 97

    }

    public static int coordinataAI() {
        return randomWithRange(0, DIMENSIONE_TABELLA - 1);

    }

    public static boolean verificaCannonata(char[][] tabella, int riga, int colonna, char[][] tabellaMosse) {
        Scanner in = new Scanner(System.in);

        if (tabellaMosse[riga][colonna] != QUADRATO_ACQUA) {
            System.out.println("Mossa già fatta");
            return false;
        }

        if (tabella[riga][colonna] == QUADRATO_ACQUA) {
            System.out.println("ACQUA");
            tabellaMosse[riga][colonna] = QUADRATO_NON_COLPITO;
            tabella[riga][colonna] = QUADRATO_NON_COLPITO; // Anche nella tabella delle navi dell'avversario segno che
                                                           // il colppo è andato a vuoto, così che nella stampa questo
                                                           // venga evidenziato
        } else {
            // System.out.println(String.format("è stata colpita la nave da %c" ,
            // tabella[riga][colonna]));
            System.out.println("COLPITA");
            tabellaMosse[riga][colonna] = QUADRATO_COLPITO;

            // Prima di segnalare in tabella il colpo, memorizzo il valore della nave per
            // controllare se è stata affondata
            char valoreNave = tabella[riga][colonna];
            tabella[riga][colonna] = QUADRATO_COLPITO; // Anche nella tabella delle navi dell'avversario segno che la
                                                       // nave è colpita, così che nella stampa questo venga evidenziato

            // Verifico se la nave è stata affondata
            boolean naveAffondata = verificaNaveAffondata(tabella, valoreNave);

            if (naveAffondata == true)
                System.out.println(String.format("NAVE DA %c AFFONDATA", valoreNave + 48));

        }

        System.out.println("Premi Enter per continuare");
        try {
            in.nextLine();
        } catch (Exception e) {
        }

        return true;

    }

    public static boolean verificaNaveAffondata(char[][] tabella, char valoreNave) {

        for (int x = 0; x < DIMENSIONE_TABELLA; x++) {
            for (int y = 0; y < DIMENSIONE_TABELLA; y++) {
                if (tabella[x][y] == valoreNave) {
                    return false;
                }
            }
        }

        return true;

    }

    public static void partita(char[][] tabellaAI, char[][] tabellaGiocatore, char[][] tabellaMosseAI,
            char[][] tabellaMosseGiocatore) {
        boolean finePartita = false;
        boolean turnoAI = false;
        int coordinataColonna;
        int coordinataRiga;
        boolean cannonataOk = true;
        int contatoreTurniAI = 0;
        int contatoreTurniGiocatore = 0;

        // Ciclo che permette di passare dal giocatore ad AI ad ogni mossa
        do {

            // Turno del giocatore
            if (turnoAI == false) {

                System.out.println("");
                System.out.println("Turno giocatore----------------------------");

                // Stampo la tabella con le navi del giocatore e le mosse di AI
                System.out.println("Navi giocatore e cannonate AI");
                stampa(tabellaGiocatore);

                // Stampo la tabella con le mosse del giocatore
                System.out.println("Cannonate giocatore");
                stampa(tabellaMosseGiocatore);

                // Richiedo al giocatore le coordinate
                coordinataColonna = coordinataColonna();

                if (coordinataColonna == -1) {
                    // Coordinata errata
                    continue;
                } else if (coordinataColonna == -2) {
                    // Esco dal gioco
                    break;
                }

                coordinataRiga = coordinataRiga();
                if (coordinataRiga == -1) {
                    // Coordinata errata
                    continue;
                }

                // Verifico la cannonata
                cannonataOk = verificaCannonata(tabellaAI, coordinataRiga, coordinataColonna, tabellaMosseGiocatore);
                if (cannonataOk){
                    contatoreTurniGiocatore++;
                }

            } else {
                System.out.println("");
                System.out.println("Turno AI------------------------------------");

                // Per l'AI le coordinate sono generate random
                coordinataColonna = coordinataAI();
                coordinataRiga = coordinataAI();
                System.out.println(String.format("Coordinate AI %c %d", coordinataColonna + 97, coordinataRiga + 1));

                // Verifico la cannonata
                cannonataOk = verificaCannonata(tabellaGiocatore, coordinataRiga, coordinataColonna, tabellaMosseAI);
                if (cannonataOk){
                    contatoreTurniAI++;
                }
            }

            // Se la cannonata è stata corretta...
            if (cannonataOk) {

                boolean partitaFinita = false;
                // Verifico se la partita è finita
                if (turnoAI == true)
                    partitaFinita = verificaPartitaFinita(tabellaGiocatore);

                else
                    partitaFinita = verificaPartitaFinita(tabellaAI);

                if (partitaFinita) {
                    System.out.println("****** PARTITA FINITA *****");

                    if(turnoAI){
                        System.out.println("Ha vinto AI in " + contatoreTurniAI + " mosse" );
                    } else {
                        System.out.println("Hai vinto tu in " + contatoreTurniGiocatore + " mosse" );
                    }

                    stampa(tabellaAI);
                    break;
                }

                // Cambio giocatore
                if (turnoAI == false) {
                    turnoAI = true;
                } else {
                    turnoAI = false;
                }
            }

        } while (finePartita == false);
    }

    public static boolean verificaPartitaFinita(char[][] tabella) {
        // Per verificare se la partita è finita, devo verificare nave per nave se ce ne
        // sono ancora presenti.
        // Nella tabella ho memorizzato il valore di ciascuna nave (2, 3....) qundi
        // prendo per ogni nave il suo valore
        // e lo cerco nella tabella. Se ne trovo uno, la partita non è finita

        for (int nave = 0; nave < MAX_NAVI; nave++) {
            int valoreNave = maxPosizioni(nave);

            boolean naveAffondata = verificaNaveAffondata(tabella, (char) valoreNave);

            if (naveAffondata == false)
                return false;
        }

        return true;
    }

    public static char[][] tabellaGiocatore() {
        Scanner in = new Scanner(System.in);
        char[][] tabella = new char[DIMENSIONE_TABELLA][DIMENSIONE_TABELLA];

        int riga;
        int colonna;
        char orientamento;

        // Genero una tabella riempita con dei valori di default che indicano ACQUA
        tabella = pulisciTabella();

        // Ciclo su tutte le navi inseribili
        for (int navi = 0; navi < MAX_NAVI; navi++) {

            // Per ogni nave inseribile recupero il numero di posizioni che occupa
            int maxPosizioni = maxPosizioni(navi);

            Boolean exit = false;

            do {

                // Faccio inserire le coordinate
                System.out.println("Inserisci la nave da " + maxPosizioni);
                colonna = coordinataColonna();
                if (colonna == -1) {
                    continue; // richiedo inserimento coordinate
                } else if (colonna == -2)
                    System.exit(0); // Esco dal programma

                riga = coordinataRiga();
                if (riga == -1)
                    continue; // Richiedo inserimento coordinate

                // Se riga e colonna sono ok procedo con l'inserimento della direzione
                System.out.println("Inserire orientamento alto, basso, destra, sinistra (a, b, d, s):");
                orientamento = in.next().charAt(0); // Inserisco un carattere alfanumerico

                // Verifico l'orientamento
                if (orientamento != 'a' && orientamento != 'b' && orientamento != 'd' && orientamento != 's') {
                    System.out.println("Valore di orientamento errato");
                    exit = false;
                    continue;
                }

                // Creo nuove variabili da usare nella gestione
                // della matrice in quanto
                // riga contiene un valore numerico che parte da 1 (e non da 0)
                // colonna contiene un char che parte da 'a' e cha ha valore ascii 97
                // orientamento contiene un char mentre i metodi lo gestiscono come intero da 0
                // a 3
                int orientamentoCorretto = 0;

                if (orientamento == 'a') {
                    orientamentoCorretto = ORIENTAMENTO_ALTO;
                } else if (orientamento == 'b') {
                    orientamentoCorretto = ORIENTAMENTO_BASSO;
                } else if (orientamento == 's') {
                    orientamentoCorretto = ORIENTAMENTO_SINISTRA;
                } else if (orientamento == 'd') {
                    orientamentoCorretto = ORIENTAMENTO_DESTRA;
                }

                // Verifico come per AI che la posizione della nave sia corretta
                boolean verifica = verificaPosizioneNave(riga, colonna, tabella,
                        maxPosizioni, orientamentoCorretto);

                if (verifica == true) {

                    // Inseriamo la nave
                    // La nave e' inseribile, procedo con l'inserimento
                    tabella = inserisciNave(riga, colonna, orientamentoCorretto, tabella,
                            maxPosizioni);

                    // Ad ogni inserimento stampo la tabella delle navi
                    stampa(tabella);

                    exit = true;

                } else {
                    System.out.println(
                            "La nave non e' inseribile alle coordinate inserite, reinserire le coordinate");
                }

            } while (exit == false);
        }

        return tabella;
    }

    // Genera la tabella AI
    public static char[][] tabellaAI() {

        char[][] tabella = new char[DIMENSIONE_TABELLA][DIMENSIONE_TABELLA];

        // Genero una tabella riempita con dei valori di default che indicano ACQUA
        tabella = pulisciTabella();

        // Ciclo sulle navi da inserire
        for (int navi = 0; navi < MAX_NAVI; navi++) {

            boolean naveInseribile = true;
            int maxPosizioni = maxPosizioni(navi);

            int riga;
            int colonna;
            int orientamento;

            do {

                // Genero random la posizione della prima riga e colonna, della direzione
                // verticale orizzontale e su giu
                riga = coordinataAI();
                colonna = coordinataAI();
                orientamento = randomWithRange(0, 3);

                int saveRiga = riga;
                int saveColonna = colonna;

                naveInseribile = verificaPosizioneNave(saveRiga, saveColonna, tabella, maxPosizioni,
                        orientamento);

            } while (naveInseribile == false);

            // La nave e' inseribile, procedo con l'inserimento
            tabella = inserisciNave(riga, colonna, orientamento, tabella, maxPosizioni);

        }

        return tabella;

    }

    static int maxPosizioni(int navi) {
        // Per ogni nave definisco quante posizioni occupa
        return navi + 2;
    }

    static int randomWithRange(int min, int max) { // defining method for a random number generator

        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    public static char[][] inserisciNave(int riga, int colonna, int orientamento, char[][] tabella,
            int valoreNave) {

        for (int x = 0; x < valoreNave; x++) {

            tabella[riga][colonna] = (char) (valoreNave);

            if (orientamento == ORIENTAMENTO_ALTO)
                riga--;
            else if (orientamento == ORIENTAMENTO_BASSO)
                riga++;
            else if (orientamento == ORIENTAMENTO_DESTRA)
                colonna++;
            else if (orientamento == ORIENTAMENTO_SINISTRA)
                colonna--;

        }

        return tabella;

    }

    public static char[][] pulisciTabella() {
        char[][] tabella = new char[DIMENSIONE_TABELLA][DIMENSIONE_TABELLA];

        for (int riga = 0; riga < DIMENSIONE_TABELLA; riga++) {
            for (int colonna = 0; colonna < DIMENSIONE_TABELLA; colonna++) {
                tabella[riga][colonna] = QUADRATO_ACQUA;
            }
        }

        return tabella;
    }

    public static boolean verificaPosizioneNave(int riga, int colonna, char[][] tabella, int valoreNave,
            int orientamento) {

        boolean ok = true;

        // Per ogni posizione, devo verificare se e' occupabile
        for (int posizione = 0; posizione < valoreNave; posizione++) {

            // Verifico che riga e colonna siano all'interno della tabella
            if (riga >= DIMENSIONE_TABELLA || riga < 0 || colonna >= DIMENSIONE_TABELLA || colonna < 0)
                return false;

            // Verifico che la posizione non sia occupata
            if (tabella[riga][colonna] != QUADRATO_ACQUA) {
                return false;
            }

            // Verifico che le posizioni confinanti o siano vuote o appartenenti alla stessa
            // nave
            for (int x = riga - 1; x <= riga + 1; x++) {
                for (int y = colonna - 1; y <= colonna + 1; y++) {

                    // Escludo il controllo sulla posizione attuale
                    if (x == riga && y == colonna)
                        continue;

                    // Verifico di avere una posizione che sia interna alla tabella
                    if (x >= 0 && x < DIMENSIONE_TABELLA && y >= 0 && y < DIMENSIONE_TABELLA) {

                        // Verifico che la posizione sia acqua o sia appartenente alla stessa nave
                        if (tabella[x][y] != QUADRATO_ACQUA && tabella[x][y] != (char) valoreNave) {

                            // Se non lo e', esco
                            ok = false;
                            break;
                        }
                    }
                }
                if (ok == false)
                    break;
            }

            if (!ok)
                break;

            // Verifico se la cella e' gia' occupata
            if (orientamento == ORIENTAMENTO_ALTO)
                riga--;
            else if (orientamento == ORIENTAMENTO_BASSO)
                riga++;
            else if (orientamento == ORIENTAMENTO_DESTRA)
                colonna++;
            else if (orientamento == ORIENTAMENTO_SINISTRA)
                colonna--;

        }

        return ok;

    }

    public static void stampa(char[][] tabella) {

        // Vado oltre le dimensioni della tabella per poter aggiungere una riga e una
        // colonna con le coordinate
        for (int riga = 0; riga < DIMENSIONE_TABELLA + 1; riga++) {
            for (int colonna = 0; colonna < DIMENSIONE_TABELLA + 1; colonna++) {

                // Nella prima riga metto le coordinate della colonna
                if (riga == 0) {
                    if (colonna == 0) {
                        System.out.print("  "); // Per la prima colonna lascio uno spazio bianco
                    }

                    if (colonna == DIMENSIONE_TABELLA)
                        break;

                    System.out.print(String.format("%c", 'a' + colonna)); // Parto dal carattere 'a' e ogni volta
                                                                          // incremento di 1 per visualizzare b, c....
                } else if (colonna == 0) {

                    // Se è la prima colonna (e non la prima riga) metto la coordinata della riga
                    if (riga > 0) {
                        if (riga >= 10)
                            System.out.print(riga);
                        else
                            System.out.print(" " + riga);

                    }
                } else {

                    char daStampare = QUADRATO_ACQUA;

                    if (tabella[riga - 1][colonna - 1] != QUADRATO_ACQUA) {
                        if (tabella[riga - 1][colonna - 1] == QUADRATO_NON_COLPITO)
                            daStampare = QUADRATO_NON_COLPITO;
                        else if (tabella[riga - 1][colonna - 1] == QUADRATO_COLPITO)
                            daStampare = QUADRATO_COLPITO;
                        else {
                            daStampare = QUADRATO_NAVE_POSIZIONATA;
                        }

                    }

                    System.out.print(String.format("%s", daStampare));

                }
            }
            System.out.println();
        }
    }

}