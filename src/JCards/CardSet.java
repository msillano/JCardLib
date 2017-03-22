package JCards;

// import myio.*;
// import JCards.IGame;
// import JCards.ICard;


/*  Codifiche:
 *     VIX    VETTORE INDEX
 *            Carte individuate con un Indice 
 *               (e.g. tra 0 e 51, senza ripertizioni).
 *
 *            Si possono usare sotto-insiemi dell'indice [0..x]
 *            eventualmente rimappati sulle carte (ICard) per
 *            casi particolari.
 *
 *            Rappresentazione agevole per stuffing e
 *            facilmente convertibili in stringhe
 *            per la stampa: l'interpretazione degli indici dipende
 *            dal contesto (tramite IGame)
 *
 *     VDF    VETTORE di DIFFERENZE incrementali
 *            (numero di carte da saltare - tra quelle disponibili in un VIX - 
 *              per selezionare una mano).
 *            esempio (4 giocatori, 13 carte ciascuno):
 *            Mano A: 13 numeri con somma <= 39 (= 52-13)
 *            Mano B: 13 numeri con somma <= 26 (= 52-26)
 *            Mano C: 13 numeri con somma <= 13 (= 52-39)
 *            Mano D: le restanti carte.
 *            Rappresentazione soprattutto interna,
 *            che agevola alcuni calcoli.
 *
 *     XID    Indice della mano, un long:
 *            esempio (4 giocatori, 13 carte ciascuno):
 *            Mano A: un long inferiore a 635'013'559'600
 *            Mano B: un long inferiore a   8'122'425'444
 *            Mano C: un long inferiore a      10'400'600
 *            Ogni mano è associata ad un numero e viceversa.
 *            Una rappresentazione molto compatta, e, giustapponendo, si ha la 
 *            definizione di una smazzata completa con solo 20 (12+10+8) cifre
 *            decimali. (La versione HEX è più compatta, ma si è preferita
 *            quella decimale).
 *
 *     PBN    (see http://home.iae.nl/users/veugent/pbn/ )
 *   The Deal tag value gives the cards of each hand.  The tag value is
 *   defined as "<first>:<1st_hand> <2nd_hand> <3rd_hand> <4th_hand>".  The 4
 *   hands are given in clockwise rotation.  A space character exists between
 *   two consecutive hands.  The direction of the <1st_hand> is indicated by
 *   <first>, being W (West), N (North), E (East), or S (South).  The cards of
 *   each hand are given in the order:  spades, hearts, diamonds, clubs.  A dot
 *   character "." exists between two consecutive suits of a hand.  The cards of
 *   a suit are given by their ranks.  The ranks are defined as (in descending
 *   order):
 *               A , K , Q , J , T , 9 , 8 , 7 , 6 , 5 , 4 , 3 , 2.
 *   Note that the 'ten' is defined as the single character "T". If a hand
 *   contains a void in a certain suit, then no ranks are entered at the place
 *   of that suit.
 *   Not all 4 hands need to be given. A hand whose cards are not given, is
 *   indicated by "-" . For example, only the east/west hands are given:
 *             [Deal "W:KQT2.AT.J6542.85 - A8654.KQ5.T.QJT6 -"]
 *   In import format, the ranks of a suit can be given in any order; the
 *   value of <first> is free.  In export format, the ranks must be given in
 *   descending order; <first> is equal to the dealer.
 */

/**
 *
 *  Classe generica, gestisce un insieme di carte, e.g. una mano.
 *  Si è preferita una Classe radice ad un'Interface per il molto codice comune.
 * 
 *   Richiede un IGame per il costruttore.
 *   Può essere usata come generatore di distribuzioni, ovvero come
 *   implementazione di una singola mano, agganciato alla lista in IGame.
 *   Le strategie più complesse, che utilizzano più di un CardSet,
 *   sono gestite dalla particolare implementazione di IGame.<br/>
 *
 *   Da usare come base per classi specializzate.
 *
 *
 * <br/>STRUTTURA:
 *
 *
 *  <pre>
 *   IndexIXD -> SetVIX
 *                     Array Indici liberi
 *                            VIXavailable
 *                                     \
 *                                      \
 *      Numero--> Array differenze--> Array Indici-->(ICard.Translate -> Carte )
 *     XIDdata           VDFdata         VIXdata                        stringa
 *
 *   SetVIX -> IndexIXD
 *
 *      Array Indici liberi
 *            VIXavailable
 *             \
 *               \
 *      Array Indici--> Array differenze-->  Numero
 *           VIXdata           VDFdata      XIDdata
 *
 *  </pre>
 *
 * @author M. Sillano
 * @version 1.00 06/05/11
 */

public class CardSet
        {

        /** Gioco associato al CardSet */
        protected IGame Gioco = null;

        /** Numero di carte nel set (mano) */
        protected int setSize;

        /** Numero di carte da cui estrarre il set (mazzo) */
        protected int fullSize;

        /** Array di carte (indici) disponibili */
        protected int[] VIXavailable;

        /** Il set (mano) di carte come indici [0..fullSize)  */
        protected int[] VIXdata;

        /** Il set (mano) di carte come differenze tra un indice ed
         *        il successivo */
        protected int[] VDFdata;

        /** Il set (mano) di carte come numero identificativo: 
         *        [0.. getTotHands() ). */
        protected long XIDdata = -1;

        /** Puntatore a Next per una lista di CardSet */
        protected CardSet pNext = null;

        // ================================================================================
        // ================================================================================

        /**
         * Constructor base.
         * Inizializza alcune strutture dinamiche.
         * Utilizza l'intero mazzo presente in "gioco.suite".
         *
         * @param gioco individua l'implementazione di IGame ed il mazzo di 
         *       carte di riferimento.
         * @param cSize individua il numero di carte presenti nel set.
         *
         * @post. Risultano aggiornati i Field: Gioco, setSize, fullSize<br/>
         *       Sono creati gli array: VDFdata, VIXdata. <br/>
         *       E' inizializzato VIXavailable con il default (tutte le carte disponibili).
         */
        public CardSet(IGame gioco, int cSize)
                {
                //  deveesistere un gioco ed un mazzo di riferimento
                assert(gioco != null) ;
                assert(gioco.getSuite() != null) ;
                //
                // inizializzazioni
                Gioco = gioco;
                setSize = cSize;
                VDFdata = new int[cSize + 1];
                VIXdata = new int[cSize];
                // default: tutto il mazzo
                initFullSize (Gioco.getSuite().getNCard());
                }


        /**
         * Constructor Clone_next_player.
         * Crea un CardSet per il giocatore successivo al CardSet passato come parametro.
         * Inizializza alcune strutture dinamiche, escludendo le carte gia' utilizzate
         *   in VIX del CardSet precedente (x).
         *
         * @param x  CardSet relativo al giocatore precedente, correttamente istanziato.
         *
         * @post. Risultano aggiornati i Field: Gioco, setSize, fullSize<br/>
         *       Sono creati gli array: VDFdata, VIXdata. <br/>
         *       E' inizializzato correttamente VIXavailable.
         */
       public CardSet(CardSet x)
                {
                // inizializzazioni
                Gioco = x.Gioco;
                setSize = x.setSize;
                VDFdata = new int[setSize + 1];
                VIXdata = new int[setSize];
                // default: tutto il mazzo
                setFullSize (x.fullSize-setSize);
                setVIXavailable(x.getVIXavailable());
                removeFromAvailable(x.VIXdata);
                }




        /**
         * Method di copia.
         * Utilizzabile per creare altri CardSet con gli stessi
         * dati generali, e.g. per creare tutte le distribuzioni.<br/>
         * Il CardSet generato è pronto per essere calcolato con setHandXID().
         *
         *  @post. Risultano istanziati i Field: Gioco, fullSize, setSize.<br/>
         *       Sono creati gli array: VDFdata, VIXdata. <br/>
         *       E' copiato: VIXavailable.
         *
         */
        public CardSet getClone()
                {
                CardSet x = new CardSet(Gioco, setSize );
                // aggiorna fullSize
                x.setFullSize(fullSize);
                // e copia VIXavailable
                x.setVIXavailable(VIXavailable);
                return x;
                }

        // ================================================================================
        // ================================================================================
        // set e get attributes

        /**
         * Set un nuovo valore di fullSize, non aggiorna VIXavailable table.
         *
         * @param fSize  Numero di carte totali (mazzo).
         */
        public void setFullSize(int fSize)
                {
                fullSize = fSize;
                }

        /**
         * Set un nuovo valore di fullSize
         * e rigenera VIXavailable table con un default.
         *
         * @param fSize  Numero di carte totali (mazzo).
         */
        public void initFullSize(int fSize)
                {
                fullSize = fSize;
                setVIXavailable(null);
                }

        /**
         * Set un nuovo valore di setSize (numero di carte distribuite)
         * e rigenera gli array usati per la distribuzione (mano).
         *
         * @param sSize  Numero di carte da distribuire.
         */
        public void setSetSize(int sSize)
                {
                setSize = sSize;
                VDFdata = new int[sSize + 1];
                VIXdata = new int[sSize];
                }

        /**
         * Get del valore di Size
         */
        public int getSetSize()
                {
                return setSize;
                }

        /**
         * Set di un nuovo valore di XID, calcola l'effettiva distribuzione.
         *
         * @param aXID Numero long che identifica la mano.
         *  Deve essere compreso in [0..getTotHands() ).
         *   nota. fullSize e VIXavailable devono essere corretti.
         *
         *  @post. Risultano aggiornati i field: XIDdata, FirstSuiteCards
         *    e gli array VDFdata e VIXdata.
         */
        public void setHandXID(long aXID)
                {
                XIDdata = aXID;
                XIDtoVDFdata();
                VDFtoVIXdata();
                }

        /**
         * Get del valore di XID
         */
        public long getXIDdata()
                {
                return XIDdata;
                }

        /**
         * Get del valore di VDF
         */
        public int[] getVDFdata()
                {
                return VDFdata;
                }
            
        /**
         * Set del vettore VIX.
         * Copia i dati e riordina l'array.
         * Aggiorna le altre rappresentazioni della mano.
         *
         * @param array Carte di una mano, 13 valori [0..51] (anche non ordinati).
         *   nota. fullSize e VIXavailable devono essere corretti.
         *
         *  @post. Risultano aggiornati i field: XIDdata, FirstSuiteCards
         *    e gli array VDFdata e VIXdata.
         */
        public void setVIX(int[] array)
                {
                assert(array.length >= setSize);
                //
                for (int i = 0; i < setSize;i++)
                        VIXdata[i] = array[i];
                HelpArray.bSort(VIXdata);
                VIXtoVDFdata();
                VDFdataToXID();
                }

        /**
         * Get del vettore VIXavailable.
         */
        public int[] getVIXavailable()
                {
                return VIXavailable;
                }
        /**
         * Get del vettore VIXdata.
         */
        public int[] getVIXdata()
                {
                return VIXdata;
                }

        // ================================================================================
        // CardSet List

        /**
         * Get del next CardSet
         */
        public CardSet getNextSet()
                {
                return pNext;
                }

        /**
         * Set del next CardSet
         */
        public void setNextSet(CardSet cardset)
                {
                pNext = cardset;
                }

        /**
         * Swap 2 CardSet nella lista, per riordinarla.
         */
        public void swapNext2Sets()
                {
                if ((getNextSet() != null) && (getNextSet().getNextSet() != null)) {
                        CardSet tmpSet = getNextSet();
                        setNextSet(tmpSet.getNextSet());
                        tmpSet.setNextSet(getNextSet().getNextSet());
                        getNextSet().setNextSet(tmpSet);
                        }
                }


        // ================================================================================
        // public methods

        /**
         * Aggiorna l'array VIXavailable.
         *
         * @param table Array da usare come VIXavailable.
         *       Viene effettuata una copia con table.clone()<br/>
         *       Se == "null": inizializza un nuovo array
         *          VIXavailable di default (tutte le carte disponibili).
         *
         *  @post. Risulta aggiornato l'array VIXavailable, di dimensione fullSize.
         */
        protected void setVIXavailable(int[] table)
                {
                if (table == null) {
                        VIXavailable = new int[fullSize+1];
                        for (int i = 0; i < fullSize;i++)
                                VIXavailable[i] = i;
                        VIXavailable[fullSize] = -1;
                        }
                else
                        VIXavailable = table.clone();
                }

        /**
         * Elimina dall'array VIXavailable
         * tutti i valori presenti nell'array table.
         *
         * @param table Array di int. Non necessariamente ordinato.
         *
         * @return  Numero degli elementi eliminati.
         *
         *  @post. Risulta aggiornato l'array VIXavailable. L'ultimo elemento può essere duplicato.
         */
        public int removeFromAvailable(int[] table)
                {
                return HelpArray.diffArray(VIXavailable, table);
                }

        /**
         * Torna il numero massimo di mani che può sviluppare CardSet.
         *  Dipende da fullSize e setSize. <br/>
         *  Valore massimo ammesso per XID.
         */
        public long getTotHands()
                {
                assert(Gioco != null);
                return Gioco.getSuite().tartaglia(fullSize, setSize);
                }


        /**
         * Crea una stringa con tutte le carte in VIXdata
         * Per stampe orizzontali.
         *
         * @return Una stringa pronta per la stampa.
         */
        public String toString()
                {
                assert(Gioco != null);
//                assert(XIDdata != -1);
                String s = "";
                for (int i = 0; i < setSize;i++)
                        s += Gioco.getSuite().getStrCard(VIXdata[i]) + " ";
                return s;
                }

        /**
         * Crea una stringa con le carte di un seme in VIXdata
         * Per stampe verticali.
         *
         * @return Una stringa pronta per la stampa.
         */
        public String toString(int seme)
                {
                assert(seme < 4);
                assert(seme >= 0);
                assert(Gioco != null);
//                assert(XIDdata != -1);
                String s = "";
                for (int i = 0; i < setSize;i++)
                       if ((VIXdata[i] >= 13*seme) & (VIXdata[i] < 13*(seme+1)))
                           s += Gioco.getSuite().getStrCard(VIXdata[i]);
                return s;
                }


       /**
         * Crea una stringa con tutte le carte complementari
         * a VIX. Per stampe orizzontali.<br/>
         * Da usare nel caso di 2 mani, al posto di un altro
         * CardSet.
         *
         * @return Una stringa pronta per la stampa.
         */
        public String toStringComplement()
                {
                assert(Gioco != null);
                assert(XIDdata != -1);
                int[] VIXtmp = VIXavailable.clone();
                // elimina le carte nella mano attuale
                HelpArray.diffArray(VIXtmp, VIXdata);
                // crea la stringa
                String s = "";
                for (int i = 0; i < setSize;i++)
                        s += Gioco.getSuite().getStrCard(VIXtmp[i]) + " ";
                return s;
                }

        /**
         * Calcolo punteggio.
         *
         *  @return un int, valore della mano, come
         *         definita dal filtro in uso.
         */
        public int getPuntiSet()
                {

                int x = 0;
                for (int i = 0; i < setSize; i++) {
                        x += Gioco.getSuite().getPuntiCard(VIXdata[i]);
                        }
                return x;
                }


        // ================================================================================
        // ================================================================================
        // private methods

        /**
         * Aggiorna VDFdata utilizzando XIDdata
         *
         *  @post. Risulta aggiornato l'array VDFdata.
         */
        private void XIDtoVDFdata()
                {
                // init VDFdata
                for (int i = 0; i < setSize;i++)
                        VDFdata[i] = 0;
                // set VDFdata
                createMano(fullSize, setSize, XIDdata, VDFdata );
                }

        /**
         * Conversione da VDFdata a XIDdata (long).
         */
        private void VDFdataToXID()
                {
                XIDdata = evalMano(fullSize, setSize, VDFdata );
                }


        /**
         * Aggiorna VIXdata utilizzando VDFdata
         *
         *  @post. Risulta aggiornato l'array VIXdata.
         */
        private void VDFtoVIXdata()
                {
                Convert(VDFdata, VIXdata, setSize);
                }

        /**
         * Utility per la conversione dal formato VIX al formato VDF.
         *
         */
        private void VIXtoVDFdata()
                {
                VDFdata[0] = 0;
                VDFdata[1] = getPosition(VIXdata[0]);
                for (int i = 1; i < setSize;i++)
                      VDFdata[i+1] = (getPosition(VIXdata[i])- getPosition(VIXdata[i-1]))-1;
                }

        /**
         * Utility per la conversione dal formato VDF al formato XID.
         *
         * Aggiorna un array VIX utilizzando fromT e this.VIXavailable, che contiene una
         * successione (ordinata) di valori VIX disponibili.
         *
         * @param fromT  array delle differenze (VDF)
         * @param Card array di destinazione (VIX)
         * @param n Numero di carte nella distribuzione (mano).
         */
        private void Convert(int[] fromT, int[] Card, int n)
                {
                assert(fromT.length > n);
                assert(Card.length >= n);
                //
                Card[0] = fromT[1];
                for (int i = 1; i < n;i++)
                        Card[i] = Card[i - 1] + fromT[i + 1] + 1;
                for (int i = 0; i < n;i++)
                        Card[i] = VIXavailable[Card[i]];
                }

        /**
         * Utility di conversione Card - index.
         */
        private int getPosition(int card){
                for (int i = 0; i < VIXavailable.length;i++)
                        if (card == VIXavailable[i]) return(i);
                return -1;
            
                }

        /**
         * Utility generale.
         * Calcola una distribuzione in formato VDF partendo da un
         * numero (XID)
         *
         * @post. Aggiorna un array VDF (mano[]) che definisce una distribuzione
         *        (come differenze) formata da nMano carte prese da un mazzo di nMazzo carte,
         *        l'effettiva distribuzione e' individuata univocamente dal long X.
         */
        private void createMano(int nMazzo, int nMano, long X, int[] mano )
                {
                assert(Gioco != null);
                assert(nMano <= nMazzo);
                assert(mano.length >= nMano + 1);
                //
                if ((nMano == 0) | (nMano == nMazzo))
                        return ;
                long n = Gioco.getSuite().tartaglia(nMazzo - 1, nMano);
                if (X < n)
                        createMano(nMazzo - 1, nMano, X, mano );
                else {
                        createMano(nMazzo - 1, nMano - 1, X - n, mano );
                        int w = 0;
                        for (int i = 0; i < nMano; i++)
                                w += mano[i];
                        mano[nMano] = nMazzo - nMano - w;
                        }
                }

        /**
         * Utility di conversione da distribuzione VDF a numero XID (long).
         *
         * Inversa di createMano().
         */

        private long evalMano(int nMazzo, int nMano, int[] mano )
                {
                 int w = 0;
                 if ((nMano == 0) | (nMano == nMazzo))
                        return 0;
                 for (int i = 0; i < nMano; i++)
                            w += mano[i];
                  if ( mano[nMano] == nMazzo - nMano - w)
                      return (Gioco.getSuite().tartaglia(nMazzo - 1, nMano)+
                                evalMano(nMazzo - 1, nMano - 1, mano ));
                  return( evalMano(nMazzo - 1, nMano, mano ));
                }

 
        }