package JCards;

import java.util.Random;
// import myio.*;

/**
 * Questa classe implementa ICard per un mazzo di 52 carte (francese).
 * Le carte sono individuate da un indice [0..51] (VIX).
 * La mappatura di default dell'indice sulle carte è:
 *  Fiori-Quadri-Cuori-Picche, e, per ogni seme: 23456789TJDKA
 *
 * Fornisce i seguenti servizi:
 *  - Conversione in stringa di una carta, secondo vari stili, utilizzando
 *    un filtro di traslazione (dinamico, può essere cambiato al run-time).
 *  - Punteggio di una carta, secondo il metodo Bridge
 *  - Un triangolo di Tartaglia di dimensioni adeguate per effetture tutti
 *    i calcoli sulle distribuzioni delle 52 carte, con relative funzioni.
 *
 * PBN:
 *    <suit> = S , H , D , C
 *    <rank> = A , K , Q , J , T , 9 , 8 , 7 , 6 , 5 , 4 , 3 , 2.
 *
 */

public class Card52 implements ICard
        {

        /**
         * Numero di carte nel mazzo francese.
         *
         */
        static public int NCARD = 52;

        /**
         *Triangolo di Tartaglia.
         *Statico, viene creato in fase di inizializzalizzazione per velocizzare i calcoli.
         */
        static private long[][] TT = new long[NCARD + 1][NCARD + 1];

        /**
         * Array di stringhe, definisce le carte francesi (52)
         * Anche in più modi (e.g. comprese "scartina" 'x') e per più standard.
         *
         * Deve essere usata con una tabella filtro di traslazione per definire il set di
         *    52 simboli effettivamente usato.
         */
        static private String[] CARD52STR = {
                                                /* 0x0 */    "xf",      "2f",      "3f",      "4f",      "5f",      "6f",      "7f",      "8f",      "9f",      "Tf",      "Ff",      "Df",      "Rf",      "Af",      "F",      "C",
                                                /* 0x10*/    "xq",      "2q",      "3q",      "4q",      "5q",      "6q",      "7q",      "8q",      "9q",      "Tq",      "Fq",      "Dq",      "Rq",      "Aq",      "Q",      "D",
                                                /* 0x20*/    "xc",      "2c",      "3c",      "4c",      "5c",      "6c",      "7c",      "8c",      "9c",      "Tc",      "Fc",      "Dc",      "Rc",      "Ac",      "C",      "H",
                                                /* 0x30*/    "xp",      "2p",      "3p",      "4p",      "5p",      "6p",      "7p",      "8p",      "9p",      "Tp",      "Fp",      "Dp",      "Rp",      "Ap",      "P",      "S",
                                                /* 0x40*/     "x",       "2",       "3",       "4",       "5",       "6",       "7",       "8",       "9",       "T",       "J",       "Q",       "K",       "A",      "-",      ""
                                            };

        /**
         * Array di int per il punteggio.
         * Deve essere usato insieme alla tabella CARD52STR.
         *
         */
        static private int[] Card52_BridgePoints = {
                    /* 0x0 */    0,      0,      0,      0,      0,      0,      0,      0,      0,      0,      1,      2,      3,      4,      0,      0,
                    /* 0x10*/    0,      0,      0,      0,      0,      0,      0,      0,      0,      0,      1,      2,      3,      4,      0,      0,
                    /* 0x20*/    0,      0,      0,      0,      0,      0,      0,      0,      0,      0,      1,      2,      3,      4,      0,      0,
                    /* 0x30*/    0,      0,      0,      0,      0,      0,      0,      0,      0,      0,      1,      2,      3,      4,      0,      0,
                    /* 0x40*/    0,      0,      0,      0,      0,      0,      0,      0,      0,      0,      1,      2,      3,      4,      0,      0
                };
        /**
         *Standard default "translate" filter.
         *Trasla da 0..51 a CARD52STR
         */
        static private int[] filterFull52 = {
                                                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                                                17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                                                33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
                                                49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61
                                            };

        /**
         *Ptr al filter della traslazione in uso.
         * nota: può avere più di 52 elementi, per scopi particolari.
         *       e.g. per accedere ai simboli dei semi.
         */
        private int[] useFilter = filterFull52;

        /**
         *Ptr alla tabella punti
         */
        private int[] usePoints = Card52_BridgePoints;

        /**
         * Blocco statico
         * Popola il Triangolo di Tartaglia TT
         */
        static
                {
                for (int i = 0; i <= NCARD;i++)
                        for (int j = 0; j <= i; j++)
                                {
                                if ((j == 0) | (j == i))
                                        TT[i][j] = 1;
                                else
                                        TT[i][j] = TT[i - 1][j - 1] + TT[i - 1][j];
                                }
                }


        /**
         *Costruttore di default.
         */
        public Card52()
                {
                super();
                }

        /**
         * Accesso al Triangolo di tartaglia.
         *
         * @param  nTotal Numero totale di carte (mazzo)
         * @param  nSet   Numero di carte da distribuire in un set (mano)
         *
         * @return Un long, pari al numero di tutte le diverse distribuzioni possibili.
         */
        public long tartaglia(int nTotal, int nSet)
                {
                assert(nTotal <= NCARD);
                assert(nSet <= nTotal);
                return TT[nTotal][nSet];
                }


        /**
         * Conversione di una carta a stringa.
         * Permette una completa rimappatura delle carte, definite da VIX [0..51].
         *
         * @param  cardVIX Indice della carta
         *
         * @return una stringa, rappresentazione testuale della carta, come
         *         definita dal filtro in uso.
         */
        public String getStrCard(int cardVIX)
                {
                assert(cardVIX < NCARD);
                assert(useFilter != null);
                return CARD52STR[useFilter[cardVIX]];
                }

        /**
         * Calcolo punteggio di una carta.
         *
         * Utilizza una tabella dei punteggio per Bridge, insieme alla
         * mappa delle carte usate.
         *
         * @param  cardVIX Indice della carta
         *
         * @return un int, valore della carta, come
         *         definita dal filtro in uso.
         */
        public int getPuntiCard(int cardVIX)
                {
                assert(cardVIX < NCARD);
                assert(useFilter != null);
                return usePoints[useFilter[cardVIX]];
                }


        /**
        *  Trova l'indice di una stringa ( con il filtro in uso).
        *
        * @param   s Stringa rappresentante una carta (uno o due char).
        *          In UpCase.
        *
        * @return indice corrispondente della carta, come
        *         definita dal filtro in uso (-1 se non trovata).
        */
        public int getVIXCard(String s)
                {
                for (int i = 0; i < NCARD; i++)
                        if (s.equals(getStrCard(i).toUpperCase()))
                                return i;
                return -1;
                }



        /**
         * Permette di cambiare il filtro in uso.
         *
         * @param  filter Il nuovo filtro per la definizione delle carte.
         *                se "null" utilizza il filtro di default.
         */
        public void setFilter(int[] filter)
                {
                if (filter == null)
                        useFilter = filterFull52 ;
                else
                        useFilter = filter;
                }

        /**
         * Accesso al filtro in uso.
         *
         * @return Il filtro attualmente in uso.
         */
        public int[] getFilter()
                {
                return useFilter;
                }

        /**
         * Permette di cambiare array punti in uso.
         *
         * @param  points Il nuovo array per la definizione delle carte.
         *                se "null" utilizza il default (Bridge).
         */
        public void setPoints(int[] points)
                {
                if (points == null)
                        usePoints = Card52_BridgePoints ;
                else
                        usePoints = points;
                }

        /**
         * Accesso al numero di carte del mazzo.
         *
         *  @return int Numero totale di carte.
         */
        public int getNCard()
                {
                return NCARD;
                }

        /**
         * Genera un Long Random da usare come IDX.
         *
         *  @return long tra 0 e Lmax.
         */
        public long createRandomLong(long Lmax){
            Random ran = new Random(); 
            long x = ran.nextLong();
              if (x<0) x = -x;
              x %= Lmax;
            return x;
            }



        public int[] createRandomStuff(){
/**
 * Crea un array random di NCARD
 * Usa una tecnica di swap e di indirizzamento indiretto
 **/
                int hand1[] = new int[NCARD];
                int hand2[] = new int[NCARD];
                int NSWAP = NCARD*4;
// inizializza                
                for (int i =0; i < NCARD; i++)
                {
                      hand1[i] = i;
                      hand2[i] = i;
                }

                for (int i =0; i < NSWAP; i++){
                    HelpArray.swapp(hand1, (int)(Math.random()*NCARD),(int)(Math.random()*NCARD));
                    }                
                for (int i =0; i < NSWAP; i++){
                    HelpArray.swapp(hand2, (int)(Math.random()*NCARD),(int)(Math.random()*NCARD));
                    }
                for (int i =0; i < NCARD; i++){
                      hand1[i] = hand2[hand1[i]];
                    }
                
                return hand1;                      
                } 





        }