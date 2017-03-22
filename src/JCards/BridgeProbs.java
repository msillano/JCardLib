
package JCards;

import myio.*;
// import JCards.IGame;
// import JCards.ICard;

/**
 * Questa implementazione di IGame è mirata alla soluzione
 * di problemi di distrubuzione dei resti nel Bridge.
 *
 * Si considera un solo seme e si conosce la carte di quel seme
 * presenti in NS.
 * Si cercano le possibili distribuzioni delle restanti carte 
 * del seme (resti) tra i due giocatori E O, con calcolo delle
 * relative probabilità.
 */
public class BridgeProbs implements IGame
        {
        /** Numero di giocatori, RO */
        private static int NPLAYERS = 4;

        /** Numero di carte per ogni giocatore, RO */
        private static int NCHAND = 13; 
 
        /** Mazzo di carte associato */
        private ICard suite;
        
        /** Testa di una lista di BridgeCardSet per le mani */
        private BridgeCardSet firstSet = null;

        /**
         * Array filtro specifico per Card52.
         *
         * Il filtro che definisce le carte per questa classe usa per il 
         *    primo seme un set di 13 simboli che specificano solo la
         *    carta, ma non il seme, in ordine discendente di punteggio.
         * Le altre carte sono tutte rappresentate con "-".<br/>
         * VIX[0..51]: "AKQFT98765432----------------------------..."
         */
       private static int[] probFilter = { 
                             0x4D, 0x4C, 0x4B, 0x4A, 0x49, 0x48, 0x47, 0x46, 0x45, 0x44, 0x43, 0x42, 0x41,
                             0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E,
                             0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E,
                             0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E,
                           };

        // =======================================================================
        // =======================================================================


        /**
         * Constructor BridgeProbs
         *
         * @post. Inizializza i Fields: suite.
         *        Installa in refMazzo il filtro di conversione "probFilter".
         */
        public BridgeProbs(ICard refMazzo)
                {
                suite = refMazzo;
                suite.setFilter(probFilter);
                }

        /**
         * Accesso a ICard in uso.
         */
        public ICard getSuite()
                {
                return suite;
                }

        /**
         * Accesso al numero di giocatori.
         */
        public int getNPlayers()
                {
                return NPLAYERS;
                }

        /**
         * Accesso al numero di carte per giocatore.
         */
        public int getNCHand()
                {
                return NCHAND;
                }

        /**
         * Accesso al numero totale di mani differenti per giocatore,
         * Usato per calcoli statistici. <br/>
         * Nel caso dei resti, due mani (NS) sono definite.
         * Pertanto Est può avere in tutto getMAXHANDS(3) mani differenti.
         *
         * @param player Posizione del set (giocatore): 1..NPLAYERS
         *
         * @return Numero delle possibili distribuzione nel set (giocatore).
         */
        public long getMAXHAND(int player)
                {
                assert ((player > 0) & (player <= NPLAYERS));
                //
                return suite.tartaglia(suite.getNCard() - ((player - 1)*NCHAND), NCHAND);
                }

        /**
         * Test resti di un seme, fase 1.
         *
         * Crea tutte le possibili differenti distribuzioni dei
         * resti per il giocatore in Est.
         *
         * @post. Una lista linkata di CardSet, il primo
         *     puntato da this.firstSet.
         */
        public void runGame(int action, String sParam)
                {
                BridgeCardSet useCardSet;
                firstSet = initFirstResti(sParam);
                long xCount = firstSet.getTotHands();
                for (long i = 1L; i < xCount; i++) {
                        useCardSet = firstSet.getClone();
                        useCardSet.setHandXID(i);
                        firstSet.addResultResti(useCardSet);
                        }
                }

        /**
         * Test resti di un seme, fase 2.
         * Stampa statistiche sulle distribuzioni dei resti.
         *
         * Prima tabella: resti ordinati per numero di carte nella mano.
         * Seconda tabella: resti ordinati per punteggio.
         *
         * @param sParam non usato in questa implementazione.
         *
         * @pre. this.firstSet punta al primo CardSet di una
         *  lista linkata, contenenti tutte le distribuzioni possibili.
         *  La lista è inizialmente ordinata rispetto FirstSuiteCards.
         */
        public void printGame(int action, String sParam)
                {
                long n = getMAXHAND(3);
                MyIO.stampa("\r\n  Est:  Ovest:   totale mani: " + n + "  ( 100 %)");
                BridgeCardSet useCards = (BridgeCardSet)firstSet;
                long tot = 0L;
                float c = 0;
                String s;
                while (useCards != null) {
                        s = useCards.toString() + " ";
                        s += useCards.toStringComplement();
                        long y = useCards.getRestiHandsLike();
                        tot += y;
                        float percent = (float)((y * 100.0) / n);
                        s += y;
                        s += "  (" + percent + " %)" ;
                        c += percent;
                        // tot % per numero di resti in E.
                        if ((useCards.pNext == null) || (useCards.firstSuiteCards != ((BridgeCardSet)useCards.pNext).firstSuiteCards)) {
                                s += " ** tot " + useCards.firstSuiteCards + " cards = " + c + " %";
                                c = 0;
                                }

                        MyIO.stampa(s);
                        useCards = (BridgeCardSet)useCards.pNext;
                        }
                MyIO.stampa("Verifica:  totale = " + tot + "\r\n");

                // -------------------- fine prima parte
                sortCardsPunti();

                // -------------------- seconda parte
                useCards = (BridgeCardSet)firstSet;
                tot = 0L;
                c = 0;
                while (useCards != null) {
                        s = useCards.toString() + "[" + useCards.bridgeCardPoints + "] ";
                        s += useCards.toStringComplement();
                        long y = useCards.getRestiHandsLike();
                        tot += y;
                        float percent = (float)((y * 100.0) / n);
                        s += y;
                        s += "  (" + percent + " %)" ;
                        c += percent;
                        // tot % per numero di resti in E.
                        if ((useCards.pNext == null) || (useCards.bridgeCardPoints != ((BridgeCardSet)useCards.pNext).bridgeCardPoints)) {
                                s += " ** tot " + useCards.bridgeCardPoints + " punti = " + c + " %";
                                c = 0;
                                }

                        MyIO.stampa(s);
                        useCards = (BridgeCardSet)useCards.pNext;
                        }
                MyIO.stampa("Verifica:  totale = " + tot);
                }


        // =======================================================================
        // =======================================================================

        /**
         * Array degli indici VIX da una stringa.
         * Utilizza il filtro in uso.
         * I caratteri spuri sono ignorati.
         *
         * @param s Stringa con le carte in NS (un char per carta)
         * @param map Array con il risultato (-1 se non usato).
         *
         * @return Numero dei valori posseduti da NS
         */
        private int remapFree1(String s, int[] map)
                {
                int count = 0;
                // inizializza a -1
                for (int i = 0; i < map.length;i++)
                        map[i] = -1;
                // Per ogni carta in s
                for (int i = 0; i < s.length(); i ++) {
                        int x = suite.getVIXCard( s.substring(i, i + 1));
                        if ((x >= 0) && (!HelpArray.presentInArray(map, x))) {
                                // aggiunge in map ed incrementa count
                                map[count++] = x;
                                }
                        }
                return count;
                }

        /**
         *  Crea il primo set (BridgeCardSet), per l'applicazione "resti",
         *   eliminando le carte in NS (stringa NScards).
         *  Se x sono le carte di NS nel seme, 13-x saranno
         *  quelle di EW.
         *  L'insieme di carte da usare (mazzo) e' quindi (13-x) * 2
         *  e le singole mani sono di 13-x carte.
         *
         *  @return CardSet totalmente inizializzato e che contiene la
         *      prima distribuzione valida, corrispondente a XID = 0.
         */
        private BridgeCardSet initFirstResti(String NScards)
                {
                // un seme, max 13 carte
                int[] map = new int[13];
                // count cards on NS
                int x = remapFree1( NScards, map);
                // the hand is  13-x cards
                BridgeCardSet aSet = new BridgeCardSet(this, 13 - x);
                // picked up from (13-x) * 2 cards
                aSet.setFullSize((13 - x)*2);
                // kill NS cards
                aSet.removeFromAvailable(map);
                // set first hand
                aSet.setHandXID(0L);
                return aSet;
                }

        /**
         * Ordina la lista di CardSet in base ai punti.
         *
         * Utilizza bubble sort.
         */
        private void sortCardsPunti()
                {
                // usa dunny per poter spostare il primo CardSet
                BridgeCardSet Dummy = new BridgeCardSet(this, 0);
                Dummy.setNextSet(firstSet);
                // bubble sorte
                int Swap;
                do {
                        Swap = 0;
                        BridgeCardSet testSet = Dummy;
                        // loop di test
                        while (testSet.getNextSet().getNextSet() != null) {
                                if (testSet.getNextSet().bridgeCardPoints < testSet.getNextSet().getNextSet().bridgeCardPoints ) {
                                        // il maggiore prima
                                        testSet.swapNext2Sets();
                                        Swap++;
                                        }
                                testSet = testSet.getNextSet();
                                }
                        }
                while (Swap > 0);
                // ripristina la testa della lista.
                firstSet = Dummy.getNextSet();
                }
        }