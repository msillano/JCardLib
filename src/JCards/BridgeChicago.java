
package JCards;

import myio.*;
import java.io.*;

//import JCards.ICard;

/**
 * Questa implementazione di IGame è mirata alla gestione
 * di un torneo tra 4 o 5 giocatori (Chicago)
 *
 */

public class BridgeChicago implements IGame
        {
        /**
         *  Se "true" i dati sono validi e disponibili,
         *  altrimenti non inizializzato o inizializzato con dati incoerenti.
         */
        protected boolean statusOK = false;

       /** Mazzo di carte associato */
        private ICard suite;

       /** BridgeBid gestisce i risultati */
        private BridgeBid aBid;

       /** Numero di giocatori, RO */
        private static int NPLAYERS = 4;

        /** Numero di carte per ogni giocatore RO*/
        private static int NCHAND = 13;

        /** Testa di una lista di BridgeCardSet per le mani */
        private BridgeCardSet firstSet = null;

        /**
         * Array filtro specifico per Card52.
         *
         * Il filtro che definisce le carte per questa classe usa per il
         *    13 simboli che specificano solo la carta, ma non il seme,
         *    in ordine discendente di punteggio.
         * <br/>
         * VIX[0..51]: "AKQFT98765432AKQFT98765432AKQFT98765432AKQFT98765432"
         */
       private static int[] handFilter = {
                             0x4D, 0x4C, 0x4B, 0x4A, 0x49, 0x48, 0x47, 0x46, 0x45, 0x44, 0x43, 0x42, 0x41,
                             0x4D, 0x4C, 0x4B, 0x4A, 0x49, 0x48, 0x47, 0x46, 0x45, 0x44, 0x43, 0x42, 0x41,
                             0x4D, 0x4C, 0x4B, 0x4A, 0x49, 0x48, 0x47, 0x46, 0x45, 0x44, 0x43, 0x42, 0x41,
                             0x4D, 0x4C, 0x4B, 0x4A, 0x49, 0x48, 0x47, 0x46, 0x45, 0x44, 0x43, 0x42, 0x41,
                             0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E, 0x4E,
                           };

        // =======================================================================
        // =======================================================================


        /**
         * Constructor BridgeChicago
         *
         * @post. Inizializza i Fields: suite.
         *        Installa in refMazzo il filtro di conversione "probFilter".
         */
        public BridgeChicago(ICard refMazzo)
                {
                suite = refMazzo;
                suite.setFilter(handFilter);
                aBid = new BridgeBid();
                statusOK= false;
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
         *  Se "true" i dati sono validi e disponibili,
         *  altrimenti non inizializzato o inizializzato con dati incoerenti.
         */

        public boolean getStatus(){
            return statusOK;
        }

         void create3LongHand(long nIdx, long eIdx, long sIdx){
                MyIO.stampa("\r\n iDX1: "+ nIdx);
                MyIO.stampa(" iDX2: "+ eIdx);
                MyIO.stampa(" iDX3: "+ sIdx);
                BridgeCardSet x = new BridgeCardSet(this, 13);
                firstSet = x;
                x.setFullSize(52);
                x.setVIXavailable(null);
                x.setHandXID(nIdx);

                x.setNextSet(new BridgeCardSet(x));
                x = x.getNextSet();
                x.setHandXID(eIdx);

                x.setNextSet(new BridgeCardSet(x));
                x = x.getNextSet();
                x.setHandXID(sIdx);

                x.setNextSet(new BridgeCardSet(x));
                x = x.getNextSet();
                x.setHandXID(0L);
                statusOK= true;
                }


         void create3PBNHand(String nPbn, String ePbn, String sPbn){
                if (nPbn.length() != 16) return;
                if (ePbn.length() != 16) return;
                if (sPbn.length() != 16) return;
                BridgeCardSet x = new BridgeCardSet(this, 13);
                firstSet = x;
                int nCards[] = PBNtoHand(nPbn);

                x.setFullSize(52);
                x.setVIXavailable(null);
                x.setVIX(nCards);

                nCards = PBNtoHand(ePbn);
                x.setNextSet(new BridgeCardSet(x));
                x = x.getNextSet();
                x.setVIX(nCards);

                nCards = PBNtoHand(sPbn);
                x.setNextSet(new BridgeCardSet(x));
                x = x.getNextSet();
                x.setVIX(nCards);

                x.setNextSet(new BridgeCardSet(x));
                x = x.getNextSet();
                x.setHandXID(0L);
                statusOK= true;
                }




     public int[] PBNtoHand(String cX){
// N:KQT2.AT.J6542.85 - .KQ5.T.QJT6 - only one hand
            assert (cX.length() == 16);

            int aHand[]= new int[13];
            int hand[] = new int[13];
            cX = cX.toUpperCase();
            String s1 = cX.substring(0, cX.indexOf('.'));
            cX = cX.substring(cX.indexOf('.')+1, cX.length());
            String s2 = cX.substring(0, cX.indexOf('.'));
            cX = cX.substring(cX.indexOf('.')+1, cX.length());
            String s3 = cX.substring(0, cX.indexOf('.'));
            cX = cX.substring(cX.indexOf('.')+1, cX.length());
            String s4 = cX;
// convert
            int n1 = remapFree1(s1, hand);

            for (int i= 0; i < n1; i++){
                  aHand[i] = hand[i];
                  }

            int n2 = remapFree1(s2, hand);

            for (int i= n1; i < n1+n2; i++){
                  aHand[i] = hand[i-n1] + 13;
                  }

            int n3 = remapFree1(s3, hand);

            for (int i= n1+n2; i < n1+n2+n3; i++){
                  aHand[i] = hand[i-n1-n2] + 26;
                  }

            int n4 = remapFree1(s4, hand);

            for (int i= n1+n2+n3; (i < n1+n2+n3+n4) & (i < 13); i++){
                  aHand[i] = hand[i-n1-n2-n3] + 39;
                  }
// done
            return aHand;
            }

         void createRandomHand1(){
                int[] full =   suite.createRandomStuff();
                int[] hand =   new int[13];
//
                BridgeCardSet x = new BridgeCardSet(this, 13);
                firstSet = x;
                x.setFullSize(52);
                x.setVIXavailable(null);
//
                HelpArray.cpyArray( hand, full, 0, 13);
                x.setVIX(hand);
// loop

                for (int n=13; n < 51; n+=13)
                   {

                   x.setNextSet(new BridgeCardSet(x));
                   x = x.getNextSet();
//
                   HelpArray.cpyArray( hand, full, n, n+13);
                   x.setVIX(hand);
//  ------------------------------------------------
//                MyIO.stampa("\r\n handID: "+ x.getXIDdata());
//                debugPrintArray("aval",x.getVIXavailable() );
//                debugPrintArray("diff",x.getVDFdata() );
//                debugPrintArray("hand",x.getVIXdata() );
                 }
                statusOK= true;
                }

/**
 *            Mano A: un long inferiore a 635'013'559'600
 *            Mano B: un long inferiore a   8'122'425'444
 *            Mano C: un long inferiore a      10'400'600
 */
         void createRandomHand2(){

                create3LongHand(
                        suite.createRandomLong(635013559600L),
                        suite.createRandomLong(8122425444L),
                        suite.createRandomLong(10400600L));
                statusOK= true;
                }



         /**
          * Funzione di formattazione locale.
          * Torna una stringa di 14 char, con i dati a dx (true) o sx (false).
          */
         private String formatCards(String p, boolean dx){
            String s = "               ";
                 if (!statusOK) return s;
            if (dx) {
               s = s+p;
               s = s.substring(s.length()-14,s.length());
            }
            else {
               s = p+s;
               s = s.substring(0,14);
            };

            return s;
            }


        public String to3HexLongs()
                {
                 String s = "";
                 if (!statusOK) return s;
                 BridgeCardSet x =  firstSet;
                 s += Long.toHexString(x.getXIDdata())+ ":";
                 x =  x.getNextSet();
                 s += Long.toHexString(x.getXIDdata())+ ":";
                 x =  x.getNextSet();
                 s += Long.toHexString(x.getXIDdata());
                 return s;
                 }

        public String to3Longs()
                {
                 String s = "";
                 if (!statusOK) return s;
                 BridgeCardSet x =  firstSet;
                 s += x.getXIDdata()+ ":";
                 x =  x.getNextSet();
                 s += x.getXIDdata()+ ":";
                 x =  x.getNextSet();
                 s += x.getXIDdata();
                 return s;
                 }

         public String get4HandPBN(){
// N:KQT2.AT.J6542.85 - .KQ5.T.QJT6
                 String s = "";
                 if (!statusOK) return s;
                 s = "N:";
                 BridgeCardSet x =  firstSet;
                 s += x.toBPNString()+ " ";
                 x =  x.getNextSet();
                 s += x.toBPNString()+ " ";
                 x =  x.getNextSet();
                 s += x.toBPNString()+ " ";
                 x =  x.getNextSet();
                 s += x.toBPNString()+ " ";
                 return s;
                 }


         public void set3HandPBN(String sPBN){
// N:KQT2.AT.J6542.85 - .KQ5.T.QJT6 - only 3 hand
                 String s = sPBN.substring(sPBN.indexOf(":")+1, sPBN.length());
                 String ns = s.substring(0,s.indexOf(" "));
                 s = s.substring(s.indexOf(" ")+1, s.length());
                 String es = s.substring(0,s.indexOf(" "));
                 s = s.substring(s.indexOf(" ")+1, s.length());
                 if (s.indexOf(" ") > 0)
                     s = s.substring(0,s.indexOf(" "));
//                 MyIO.stampa("|"+ns+"|"+es+"|"+s+"|");
                  create3PBNHand(ns,es,s);
                 }


         public  void writeHand(String fName){
         }

         public  void readHand(String fName){
         }


        /**
         * Stampa on-screen la distribuzione
         */

         void Print4HandCross(){
                 if (!statusOK) return;

                String fill = "               ";

                BridgeCardSet n =  firstSet;
                assert (n != null);
                BridgeCardSet e =  n.getNextSet();
                assert (e != null);
                BridgeCardSet s =  e.getNextSet();
                assert (s != null);
                BridgeCardSet w =  s.getNextSet();
                assert (w != null);

                MyIO.stampa(fill + formatCards(n.toString(0), false));
                MyIO.stampa(fill + formatCards(n.toString(1), false));
                MyIO.stampa(fill + formatCards(n.toString(2), false));
                MyIO.stampa(fill + formatCards(n.toString(3), false));


                MyIO.stampa( formatCards(w.toString(0), true)+"      " + formatCards(e.toString(0), false));
                MyIO.stampa( formatCards(w.toString(1), true)+"      " + formatCards(e.toString(1), false));
                MyIO.stampa( formatCards(w.toString(2), true)+"      " + formatCards(e.toString(2), false));
                MyIO.stampa( formatCards(w.toString(3), true)+"      " + formatCards(e.toString(3), false));

                MyIO.stampa(fill + formatCards(s.toString(0), false));
                MyIO.stampa(fill + formatCards(s.toString(1), false));
                MyIO.stampa(fill + formatCards(s.toString(2), false));
                MyIO.stampa(fill + formatCards(s.toString(3), false));
                }


         String getXMLHand(String more){
                BridgeCardSet n =  firstSet;
                assert (n != null);
                BridgeCardSet e =  n.getNextSet();
                assert (e != null);
                BridgeCardSet s =  e.getNextSet();
                assert (s != null);
                BridgeCardSet w =  s.getNextSet();
                assert (w != null);

                String xs = "<deal>\r\n";
                if (!statusOK) return xs +"</deal>";
                xs += "<north  s=\""+n.toString(0)+"\" h=\""+n.toString(1)+"\" d=\""+n.toString(2)+"\" c=\""+n.toString(3)+"\" p=\""+getHandPoints(1)+"\" />\r\n";
                xs += "<east   s=\""+e.toString(0)+"\" h=\""+e.toString(1)+"\" d=\""+e.toString(2)+"\" c=\""+e.toString(3)+"\" p=\""+getHandPoints(2)+"\" />\r\n";
                xs += "<south  s=\""+s.toString(0)+"\" h=\""+s.toString(1)+"\" d=\""+s.toString(2)+"\" c=\""+s.toString(3)+"\" p=\""+getHandPoints(3)+"\" />\r\n";
                xs += "<west   s=\""+w.toString(0)+"\" h=\""+w.toString(1)+"\" d=\""+w.toString(2)+"\" c=\""+w.toString(3)+"\" p=\""+getHandPoints(4)+"\" />\r\n";
                xs += more;
                xs += "</deal>";
              return xs;
         }

        /**
         *
         * @param player Posizione del set (giocatore): 1..NPLAYERS (N, E, S, O)
         *
         * @return punteggio del set (giocatore).
         */

         int getHandPoints(int player){
                assert ((player > 0) & (player <= NPLAYERS));
                BridgeCardSet useCardSet;
                useCardSet = firstSet;
                for (int i = 0; i < player-1; i++)
                        useCardSet = (BridgeCardSet)useCardSet.pNext;
                return  useCardSet.getBridgeCardPoints();
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
         */
        public void runGame(int action, String sParam)
                {
                switch (action){
                 case 1:
                        createRandomHand1();
                        break;
                 case 2:
                        createRandomHand2();
                        break;
                 case 3:
                        set3HandPBN(sParam);
                        break;
                 case 4:
                        break;
                }

             }

        /**
         */
        public void printGame(int action, String sParam)
                {
               MyIO.stampa(" -----------------------------");
               switch (action){
                 case 1:
                    Print4HandCross();
                        break;
                 case 2:
                    MyIO.stampa(get4HandPBN());
                    MyIO.stampa(to3Longs());
                    MyIO.stampa(to3HexLongs());
                        break;
                 case 3:
                    MyIO.stampa(" punti N :"+getHandPoints(1));
                    MyIO.stampa(" punti E :"+getHandPoints(2));
                    MyIO.stampa(" punti S :"+getHandPoints(3));
                    MyIO.stampa(" punti W :"+getHandPoints(4));
                    MyIO.stampa("    punti NS :"+(getHandPoints(1)+getHandPoints(3)));
                    MyIO.stampa("    punti EW :"+(getHandPoints(2)+getHandPoints(4)));
                        break;
                 case 4:
                    MyIO.stampa(getXMLHand(""));
                        break;
                 case 5:
                    aBid.setValues(sParam);
                      MyIO.stampa(aBid.getTableScore());
//                    MyIO.stampa(aBid.toString());
//                    MyIO.stampa("Punti= " + aBid.getScore());
                    break;
                 case 6:
                    try{
                    BufferedWriter xOut  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sParam)));
                    xOut.write(get4HandPBN());
                    xOut.newLine();
                    xOut.write(aBid.toString());
                    xOut.newLine();
                    xOut.close();
                    }
                  catch (Exception e){ };
                    break;
                 case 7:
                    try{
                    BufferedReader xIn  = new  BufferedReader(new InputStreamReader(new FileInputStream(sParam)));
                    set3HandPBN(xIn.readLine());
                    aBid.setValues(xIn.readLine());
                    xIn.close();
                    }
                  catch (Exception e){ };
                    break;
                 case 8:
                    MyIO.stampa(aBid.getXMLScore());
                        break;
                 case 9:
                    try{
                    BufferedWriter xOut  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sParam)));
                    xOut.write(getXMLHand(aBid.getXMLScore()));
                    xOut.newLine();
                    xOut.close();
                    }
                  catch (Exception e){ };
                    break;
                }

               MyIO.stampa("" );
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

        }