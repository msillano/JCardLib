package JCards;

/**
 *  Definisce un mazzo di carte.
 *
 *  Le carte sono identificate da un indice = [0..getNCard() ),
 *    detto indice VIX (Vector IndeX).<br/>
 *  Definisce un array di stringhe per rappresentare le
 *    carte secondo vari standard e lingue, ed una tabella dei
 *    punti associati ad ogni carta. <br/>
 *  Responsabile della trasformazione
 *    indice VIX -> stringa (dinamicamente variabile tramite un filtro) e viceversa.<br/>
 *  Definisce inoltre:<br/>
 *    un Triangolo di Tartaglia (di adeguate dimensioni)
 *    utilizzato nei calcoli delle distribuzioni.<br/>
 *    un generatore di mani random.
 */

public interface ICard
        {

        /**
         * Accesso al numero di carte totale.
         *
         * @return numero di carte totali del mazzo.
         */
        public int getNCard();


        /**
         * Genera un mazzo mescolato random.
         *
         * @return array[getNCard()] con valori [0...getNCard() ) distribuiti random.
         */
        public int[] createRandomStuff();
        
        /**
         * Genera un long random inferire a Lmax.
         *
         */
        public long createRandomLong(long Lmax);
        

        /**
         * Permette di cambiare il filtro in uso.
         * Si possono avere cosi' stringhe differenti per gli stessi indici.
         *
         * @param filter Il filtro da usare. Se == null è usato un filtro
         * di default. <br/>
         * Un filtro è formato da almeno getNCard() elementi, che contengono
         * indici a CARDxxSTR.
         */
        public void setFilter(int[] filter);

        /**
         * Accesso al filtro in uso.
         *
         * @return L'array usato come filtro.
         */
        public int[] getFilter();

        /**
         *  Accesso alle stringhe che rappresentano
         *   le carte con il filtro in uso.
         *
         * @param  cardVIX Indice della carta [0..getNCard() ).
         *
         * @return una stringa, rappresentazione testuale della carta, come
         *         definita dal filtro in uso e nell'array di stringhe
         *         in uso (v. setFilter).
         */
        public String getStrCard(int cardVIX);

        /**
         * Permette di cambiare array punti in uso.
         *
         * @param  points Il nuovo array per la definizione delle carte.
         *                Se "null" utilizza il default (Bridge).
         */
        public void setPoints(int[] points);

        /**
         *  Punteggio di una carta con il filtro in uso.
         *
         * @param  cardVIX Indice della carta [0..getNCard() ).
         *
         * @return un int, valore della carta,
         *         definita dal filtro in uso e nella tabella punti
         *         in uso (v. setPoints).
         */
        public int getPuntiCard(int cardVIX);

        /**
         *  Trova l'indice di una stringa (con il filtro in uso). 
         *  Funzione inversa a getStrCard().
         *
         * @param   s Stringa rappresentante una carta (uno o più char).
         *          Una delle stringhe definite negli array usati per
         *            la traduzione (v. CARDxxSTR).
         *          s deve essere in UpCase.
         *
         * @return indice corrispondente della carta, come
         *         definita dal filtro in uso, oppure -1;
         */
        public int getVIXCard(String s);

        /**
         * Accesso al Triangolo di Tartaglia, di dimensioni adeguate al mazzo.
         *
         * @param nTotal totale carte da cui estrarre una distribuzione.
         *
         * @param nSet numero di carte nella distribuzione (mano).
         *
         * @return Numero delle mani differenti formate da nSet carte che si possono
         *    ottenere da un mazzo di nTotal carte.
         */
        public long tartaglia(int nTotal, int nSet);

        }