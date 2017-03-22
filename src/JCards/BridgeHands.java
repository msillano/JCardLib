
package JCards;

import myio.*;
import java.io.*;

//import JCards.ICard;

/**
 * Questa implementazione di IGame è mirata alla gestione
 * di smazzate per il gioco del Bridge.
 *
 * Si hanno quattro giocatori (N, E, S, W), ognuno con 13 carte.
 * Le funzionalità di questa classe riguardano:
 *  Generazione di una distribuzione random. <br/>
 *  Creazione di statistiche per il test dei generatori random. <br/>
 *  Import di una distribuzione: interattiva, come XID (long), come PBN. <br/>
 *  Export di una distribuzione: come XID, PBN, HTML. <br/>
 *  Calcolo punti nella mano e punteggio finale (nota licita e risultato). <br/>
 *
  * codifiche
  * Si utilizza lo standard PBN nelle seguenti funzionalita'
  *  a) Lettura/scrrittura di file PBN compatibili
  *  b) inserimento interattivo di dati
  *
  * PBN
  *    <suit> = S , H , D , C
  *    <rank> = A , K , Q , J , T , 9 , 8 , 7 , 6 , 5 , 4 , 3 , 2.
  *
  * [Dealer "N"]
  * [Vulnerable "None"]
  * [Deal "N:.63.AKQ987.A9732 A8654.KQ5.T.QJT6 J973.J98742.3.K4 KQT2.AT.J6542.85"]
  * [Scoring "IMP"]
  * [Declarer "S"]
  * [Contract "5HX"]
  * [Result "9"]
  * 
  *
  * [Dealer "N"]
  *  The Dealer tag value is the direction of the game's dealer. The tag value
  * is "W" (West), "N" (North), "E" (East), or "S" (South).
  *
  * [Vulnerable "None"]
  *  The Vulnerable tag value defines the situation of vulnerability. The
  * following tag values are possible:
  *  "None" , "Love" or "-"  no vulnerability
  *  "NS"                    North-South vulnerable
  *  "EW"                    East-West vulnerable
  *  "All" or "Both"         both sides vulnerable
  * In export format the tag values "None" and "All" are applied.
  *
  * [Deal "N:.63.AKQ987.A9732 A8654.KQ5.T.QJT6 J973.J98742.3.K4 KQT2.AT.J6542.85"]
  *    The Deal tag value gives the cards of each hand.  The tag value is
  * defined as "<first>:<1st_hand> <2nd_hand> <3rd_hand> <4th_hand>".  The 4
  * hands are given in clockwise rotation.  A space character exists between
  * two consecutive hands.  The direction of the <1st_hand> is indicated by
  * <first>, being W (West), N (North), E (East), or S (South).  The cards of
  * each hand are given in the order:  spades, hearts, diamonds, clubs.  A dot
  * character "." exists between two consecutive suits of a hand.  The cards of
  * a suit are given by their ranks.  The ranks are defined as (in descending
  * order):
  *    A , K , Q , J , T , 9 , 8 , 7 , 6 , 5 , 4 , 3 , 2.
  *  Note that the 'ten' is defined as the single character "T". If a hand
  * contains a void in a certain suit, then no ranks are entered at the place
  * of that suit.
  *  Not all 4 hands need to be given. A hand whose cards are not given, is
  * indicated by "-" . For example, only the east/west hands are given:
  * [Deal "W:KQT2.AT.J6542.85 - A8654.KQ5.T.QJT6 -"]
  *  In import format, the ranks of a suit can be given in any order; the
  * value of <first> is free.  In export format, the ranks must be given in
  * descending order; <first> is equal to the dealer.
  *
  * [Scoring "IMP"]
  *  This tag gives the used scoring method. It is an essential part of
  * the game since the tactics of the players depend on the scoring method.
  *  There are a lot of scoring systems with all kind of variations, refer
  * to Bridge Encyclopedia.  New scoring systems evolve for coping with all
  * kind of irregularities, see e.g.:
  *  http://www.gallery.uunet.be/hermandw/bridge/hermtd.html.
  *  The wealth of scoring systems makes standardisation difficult.
  * Therefore, the specification of the tag value is open ended:  only
  * example values are given.  The tag value consists of fields separated by
  * semicolons.  A field indicates either a basic scoring system or a modifier.
  * Examples of basic scoring systems are:
  *    MP           MatchPoint scoring
  *    MatchPoints  identical to 'MP'
  *    IMP          IMP scoring (since 1962)
  *    Cavendish    Cavendish scoring
  *    Chicago      Chicago scoring
  *    Rubber       Rubber scoring
  *    BAM          Board-A-Match
  *    Instant      apply InstantScoreTable
  * Examples of modifiers are:
  *    Butler    the trick point score is IMPed against the average value
  *              of all scores
  *    Butler-2  as 'Butler', but the 2 extreme scores are not used in
  *              computing the average value
  *    Experts   the trick point score is IMPed against a datum score
  *              determined by experts
  *    Cross     the trick point score is IMPed against every other
  *              trick point score, and summed
  *    Cross1    value of 'Cross' , divided by number of scores
  *    Cross2    value of 'Cross' , divided by number of comparisons
  *    Mean      the datum score is based on a (normal) average value
  *    Median    the datum score is based on the median value
  *    MP1       MatchPoints are computed as:  the sum of points, constructed
  *              by earning 2 points for each lower score, 1 point for each
  *              equal score, and 0 points for each higher score.
  *    MP2       MatchPoints are computed as:  the sum of points, constructed
  *              by earning 1 point for each lower score, 0.5 points for each
  *              equal score, and 0 points for each higher score.
  *    OldMP     NO bonus of 100 (Doubled) or 200 (Redoubled) for the fourth
  *              and each subsequent undertrick, when not vulnerable
  *    Mitchell2 see http://www.gallery.uunet.be/hermandw/bridge/hermtd.html
  *    Mitchell3 idem
  *    Mitchell4 idem
  *    Ascherman idem
  *    Bastille  idem
  *    EMP       European MatchPoints
  *    IMP_1948  IMP scoring used between 1948 and 1960
  *    IMP_1961  IMP scoring revised in 1961
  *
  * [Declarer "S"]
  *    The Declarer tag value is the direction of the declarer of the contract.
  * The tag value is "W" (West), "N" (North), "E" (East), or "S" (South).
  *  The Declarer tag can also cope with the irregularity that the declarer
  * and the dummy are swapped.  This may happen when e.g. South is declarer,
  * but by accident East plays the first card and South puts his cards on the
  * table.  The tag value becomes a caret (^) followed by the direction of the
  * irregular declarer:  "^W", "^N", "^E", resp. "^S".
  *  When all 4 players pass, then the tag value is an empty string.
  *
  * [Contract "5HX"]
  *  The Contract tag value can be "Pass" when all players pass, or a 'real'
  * contract defined as:  "<k><denomination><risk>"
  * with
  *    <k>             the number of odd tricks, <k> = 1 .. 7
  *    <denomination>  the denomination of the contract, being S (spades),
  *                    H (Hearts), D (Diamonds), C (Clubs), or NT (NoTrump)
  *    <risk>          the risk of the contract, being void (undoubled),
  *                    X (doubled), or XX (redoubled)
  *
  * [Result "9"]
  *  The Result tag value gives the result of the game in number of tricks.
  * The possible tag values are:
  *    "<result>"                 number of tricks won by declarer
  *    "EW <result>"              number of tricks won by EW
  *    "NS <result>"              number of tricks won by NS
  *    "EW <result> NS <result>"  number of tricks won by EW resp. by NS
  *    "NS <result> EW <result>"  number of tricks won by NS resp. by EW
  * with <result> = 0 .. 13 .
  *  The <result> must match the actual number of won tricks.  However, the
  * players could accidentally agree on a wrong number of tricks.  A caret
  * character ("^") preceding one of the above tag values indicates that the
  * <result> differs from the actual number of won tricks.
  *  When all 4 players pass, then the tag value is an empty string.
  *  In export format the tag value contains the number of tricks won by
  * declarer.
  *  The Result tag normally gives the final result after the play has ended.
  * This is the case when all 52 cards have been played, or when the Play
  * section ends with '*'.  The Result tag can also be used to give a partial
  * result.  When the play has not ended, then the Result tag indicates the
  * number of won tricks for the completed, played tricks in the play section.
  *  Usage of '+' in the play section would make it explicitly clear that the
  * Result tag is based on a partial result.
  *
  *
  * 4.1.5  Tag: Generator
  *   This tag indicates how the cards have been generated. It is intended for
  * bridge computer programs and especially hand generators. The tag value may
  * include the name of the program and possibly a seed value.
  * 
  * 4.1.2  Tag: DealId
  *   This tag identifies a deal by a unique value; no two PBN deals should
  * have the same tag value for DealId.  The syntax of the tag value is left
  * unspecified; typically, it will contain values from tags such as Round,
  * Board, Event, Date, Generator, etc.
  * Adding this tag to games allows programs to easily find all games
  * belonging to the same deal, for instance to do comparisons for duplicate
  * scoring.  Usage is strongly recommended, when there is a chance that the
  * PBN games are copied in other PBN files.
  *
  */
public class BridgeHands implements IGame
        {
        /**
         *  Se "true" i dati sono validi e disponibili, 
         *  altrimenti non inizializzato o inizializzato con dati incoerenti.
         */
        protected boolean statusOK = false;

       /** Mazzo di carte associato */
        private ICard suite;
        
       /** BridgeBid gestisce i risultati */
        protected BridgeBid thisBid;

       /** Numero di giocatori, RO */
        private static int NPLAYERS = 4;

        /** Numero di carte per ogni giocatore RO*/
        private static int NCHAND = 13;
         
        /** Testa di una lista di BridgeCardSet per le mani */
         BridgeCardSet firstSet = null;
        
        /** Matrice carte <giocatore><seme> */
        private int carteNelSeme[][] = new int[4][4]; 
        
        /**
         * Array filtro specifico per Card52.
         *
         * Il filtro che definisce le carte per questa classe usa  
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

       static private String playerShort[]={ "N", "E", "S", "W" };
        // =======================================================================
        // Dati per statistiche su distribuzioni
       private static  String strDistribuzioni[] = {
                             "4-4-3-2",
                             "5-3-3-2",
                             "5-4-3-1",
                             "5-4-2-2",
                             "4-3-3-3",
                             "6-3-2-2",
                             "6-4-2-1",
                             "6-3-3-1",
                             "5-5-2-1",
                             "4-4-4-1",
                             "7-3-2-1",
                             "6-4-3-0",
                             "5-4-4-0",
                             "5-5-3-0",
                             "6-5-1-1",
                             "6-5-2-0",
                             "7-2-2-2",
                             "7-4-1-1",
                             "7-4-2-0",
                             "7-3-3-0",
                             "8-2-2-1",
                             "8-3-1-1",
                             "7-5-1-0",
                             "8-3-2-0",
                             "6-6-1-0",
                             "8-4-1-0",
                             "9-2-1-1",
                             "9-3-1-0",
                             "9-2-2-0",
                             "7-6-0-0",
                             "8-5-0-0",
                             "10-2-1-0",
                             "9-4-0-0",
                             "10-1-1-1",
                             "10-3-0-0",
                             "11-1-1-0",
                             "11-2-0-0",
                             "12-1-0-0",
                             "13-0-0-0",
                    };
                    
             static  int countDist[] = new int[39];
             
             static int MAXDIS = 39;

             static  int countResti[][] = new int[8][8];

        // =======================================================================


        /**
         * Constructor BridgeHands
         *
         * @post. Inizializza i Fields: suite.
         *        Installa in refMazzo il filtro di conversione "probFilter".
         */
        public BridgeHands(ICard refMazzo)
                {
                suite = refMazzo;
                suite.setFilter(handFilter);
                thisBid = new BridgeBid();
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
//                MyIO.stampa("\r\n iDX1: "+ nIdx);                
//                MyIO.stampa(" iDX2: "+ eIdx);                
//                MyIO.stampa(" iDX3: "+ sIdx);                
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
                getHandStat();
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
                getHandStat();
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
                getHandStat();
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
                getHandStat();
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
/**
 * Genera una stringa HEX utilizzando i 3 XID data della distribuzione, 
 * private del primo carattere (non random in quanto si hanno dei 
 * limiti superiori definiti). Per test di Randomness.
 **/
        public String toRandomEx()
                {
                 if (!statusOK) return "";
                 BridgeCardSet x =  firstSet;
                 String s = Long.toHexString(x.getXIDdata());
                 x =  x.getNextSet();
                 String s1 =Long.toHexString(x.getXIDdata());
                 s += s1.substring(1);
                 x =  x.getNextSet();
                 s1 =Long.toHexString(x.getXIDdata());
                 s += s1.substring(1);
                 return s.substring(1);
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

         public String get4HandPBN(int dealer){
// N:KQT2.AT.J6542.85 - .KQ5.T.QJT6         
                 String s = "";    
                 if (!statusOK) return s;
                 s+="N:";
                 String hands[] = new String[4];
                 BridgeCardSet x =  firstSet;
                 s+= x.toBPNString()+" ";
                 x =  x.getNextSet();
                 s+= x.toBPNString()+" ";
                 x =  x.getNextSet();
                 s+= x.toBPNString()+" ";
                 x =  x.getNextSet();
                 s+= x.toBPNString();
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
                  MyIO.stampa(get4HandCross());
                }
         
         void swapArray(int v[], int a, int b){
            int tmp = v[a];
            v[a] = v[b];
            v[b] = tmp;
         }
         
         void getHandStat(){
                for (int j = 0; j < NPLAYERS; j++)
                   {
                   BridgeCardSet useCardSet;
                   useCardSet = firstSet;
                   for (int i = 0; i < j; i++)
                        useCardSet = (BridgeCardSet)useCardSet.pNext;
                   carteNelSeme[j][0] = useCardSet.handCards[0];
                   carteNelSeme[j][1] = useCardSet.handCards[1];
                   carteNelSeme[j][2] = useCardSet.handCards[2];
                   carteNelSeme[j][3] = useCardSet.handCards[3];
                   }
                 }
         
         void updateRestArray(int r[][]){
                for (int j = 0; j < 4; j++){
                    if ((carteNelSeme[0][j]+carteNelSeme[2][j]) > (carteNelSeme[1][j]+carteNelSeme[3][j])){
                       if (carteNelSeme[1][j]>carteNelSeme[3][j]){
                          r[carteNelSeme[1][j]][carteNelSeme[3][j]]++;
                        }
                        else
                          r[carteNelSeme[3][j]][carteNelSeme[1][j]]++;
                         
                    }
                else    {
                       if (carteNelSeme[0][j]>carteNelSeme[2][j]){
                          r[carteNelSeme[0][j]][carteNelSeme[2][j]]++;
                        }
                        else
                          r[carteNelSeme[2][j]][carteNelSeme[0][j]]++;
                    
                    }
                }
            }
              
         String getFullResti(){
                 int total = 0;
                 double perc = 0.0;
                 String s ="";            
                 for (int i= 7; i>=0; i--)
                     {
                     total = 0;                
                     for (int j= 0; j<=i; j++)
                         total += countResti[i-j][j];
//                      s +=  i + "= " + total +"\r\n";   
                     if (total >0) {
                        s += "  ------------------- \r\n";
                        for (int j= 0; j<=i; j++)
                           if ( countResti[i-j][j] >0){
                            perc = (countResti[i-j][j]*100.0)/total;
                            s += " "+(i-j)+"-"+j+": \t"+ countResti[i-j][j] +"\t"+perc+"\r\n";
                           }
                        
                        }   
                     }
                return s;
                }
         /**
          * Crea una stringa con la distribuzione delle carte di un giocatore (e.g. 4-4-3-2 )
          * 
          */
         String getPlayerDistribution(int player){
                int d[] = new int[4];
                for (int j = 0; j < 4; j++){
                    d[j]= carteNelSeme[player][j];
                boolean more = true;    
                do    {
                      more = false;
                      for (int i = 0; i < 3; i++){
                         if (d[i] < d[i+1]){
                           swapArray(d,i, i+1);
                           more = true;
                           }
                         }
                  } while (more);
                }
         return ""+d[0]+"-"+d[1]+"-"+d[2]+"-"+d[3];
         }     
               
         void initStats(){
                    for (int i = 0; i < 39; i++) countDist[i]= 0;
                    for (int i = 0; i < 8; i++) {
                       for (int j = 0; j < 8; j++) countResti[i][j]= 0;
                    }
         }
      
         void addStats(String aDist){
                    for (int i = 0; i < 39; i++) 
                       if (aDist.equals(strDistribuzioni[i])) {
                          countDist[i]++;
                          return;
                       }
                    }               

         String getFullStats(){
                 int total = 0;
                 double perc = 0.0;
                 String s ="";            
                    for (int i = 0; i < 39; i++) total += countDist[i];
                    for (int i = 0; i < 39; i++){
                        perc = (countDist[i]*100.0) / total;
                    s += strDistribuzioni[i] + "  \t" + countDist[i] + "\t" + perc+"\r\n";
                    } 
                 s += "\r\n  ============ processed "+total+" hands.\r\n"   ;
                 return s;   
                 }

         String getHandFullScores(){
                    String xs = " punti N :"+getHandPoints(1) +"\r\n";
                     xs += " punti E :"+getHandPoints(2) +"\r\n";
                     xs += " punti S :"+getHandPoints(3) +"\r\n";
                     xs += " punti W :"+getHandPoints(4) +"\r\n";
                     xs += "    punti NS :"+(getHandPoints(1)+getHandPoints(3)) +"\r\n";
                     xs += "    punti EW :"+(getHandPoints(2)+getHandPoints(4)) +"\r\n";
                return xs;
                }
         
         String  get4HandCross(){
                 if (!statusOK) return "error \r\n";
              
                String fill = "               ";
              
                BridgeCardSet n =  firstSet;
                assert (n != null);
                BridgeCardSet e =  n.getNextSet();
                assert (e != null);
                BridgeCardSet s =  e.getNextSet();
                assert (s != null);
                BridgeCardSet w =  s.getNextSet();
                assert (w != null);
                
                String xs = fill +"P:"+ formatCards(n.toString(0), false) +"\r\n";
                xs += fill +"C:"+ formatCards(n.toString(1), false) +"\r\n";
                xs += fill +"Q:"+ formatCards(n.toString(2), false) +"\r\n";
                xs += fill +"F:"+ formatCards(n.toString(3), false) +"\r\n";


                xs += "P:"+formatCards(w.toString(0), true)+"      P:" + formatCards(e.toString(0), false) +"\r\n";
                xs += "C:"+formatCards(w.toString(1), true)+"      C:" + formatCards(e.toString(1), false) +"\r\n";
                xs += "Q:"+formatCards(w.toString(2), true)+"      Q:" + formatCards(e.toString(2), false) +"\r\n";
                xs += "F:"+formatCards(w.toString(3), true)+"      F:" + formatCards(e.toString(3), false) +"\r\n";

                xs += fill +"P:"+ formatCards(s.toString(0), false) +"\r\n";
                xs += fill +"C:"+ formatCards(s.toString(1), false) +"\r\n";
                xs += fill +"Q:"+ formatCards(s.toString(2), false) +"\r\n";
                xs += fill +"F:"+ formatCards(s.toString(3), false) +"\r\n";
                return xs;
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
                BridgeCardSet useCardSet = firstSet;
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
         * Stampa su file la distribuzione, in modo adatto
         * a test statistici di randomness (v. http://www.fourmilab.ch/random/ )
         * Output: test ASCII, 80 char EX per riga, almeno 20M (circa 2800 righe)
         **/
      
       void doEXtestFile1(String fileOutName)
                {
                          try{
   BufferedWriter xOut  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutName)));
                    for (int i = 0; i < 300000; i++){
                         String exLine = "";
                         runGame(1,"");   
                         exLine = exLine + toRandomEx();
                         runGame(1,"");   
                         exLine = exLine + toRandomEx();
                         runGame(1,"");   
                         exLine = exLine + toRandomEx();
                         runGame(1,"");   
                         exLine = exLine + toRandomEx();
                         runGame(1,"");   
                         exLine = exLine + toRandomEx();
                         exLine = exLine.toUpperCase();
                         exLine = exLine.substring(0,80);
                                        
                         xOut.write(exLine);
                         xOut.newLine();
                         }
                    
                    xOut.close();  
                    
                                    }
                  catch (Exception e){ };
    
                }

       void doEXtestFile2(String fileOutName)
                {
                          try{
   BufferedWriter xOut  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutName)));
                    for (int i = 0; i < 300000; i++){
                         String exLine = "";
                         runGame(2,"");   
                         exLine = exLine + toRandomEx();
                         runGame(2,"");   
                         exLine = exLine + toRandomEx();
                         runGame(2,"");   
                         exLine = exLine + toRandomEx();
                         runGame(2,"");   
                         exLine = exLine + toRandomEx();
                         runGame(2,"");   
                         exLine = exLine + toRandomEx();
                         exLine = exLine.toUpperCase();
                         exLine = exLine.substring(1,81);
                                        
                         xOut.write(exLine);
                         xOut.newLine();
                         }
                    
                    xOut.close();  
                    
                                    }
                  catch (Exception e){ };
    
                }


      void doHandTestFile(int use, boolean full, int n, String fileOutName)
                {
                 try{
                    initStats();
                    BufferedWriter xOut  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutName)));
                    if (full)
                           xOut.write(" RANDOM#"+use+" "+n+" HANDS ==========================\r\n" );
                    for (int i = 0; i < n; i++){
                         runGame(use,"");   
                         if (full){
                            xOut.newLine();
                            xOut.write(" HAND n."+i+"  ==========================\r\n" );
                            xOut.write(get4HandCross());
                            xOut.write(getHandFullScores());
                            xOut.write("N: "+getPlayerDistribution(0)+"\r\n");
                            xOut.write("E: "+getPlayerDistribution(1)+"\r\n");
                            xOut.write("S: "+getPlayerDistribution(2)+"\r\n");
                            xOut.write("O: "+getPlayerDistribution(3)+"\r\n");
                            }
                         addStats(getPlayerDistribution(0));
                         addStats(getPlayerDistribution(1));
                         addStats(getPlayerDistribution(2));
                         addStats(getPlayerDistribution(3));
                         updateRestArray(countResti);
                         }                    
                    xOut.write(" RANDOM#"+use+" STATISTICS ==========================\r\n" );
                    xOut.write(getFullStats());
                    xOut.write(" RESTI ========================================\r\n" );
                    xOut.write(getFullResti());
                    xOut.close();  
                    }  // try
                  catch (Exception e){ };
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
                        doEXtestFile1(sParam);
                        break;   
                 case 5:
                        doEXtestFile2(sParam);
                        break;   
                 case 6:
                        doHandTestFile(1,true,MyIO.leggiInt(" Numero di smazzate >> "), sParam);
                        break;   
                 case 7:
                        doHandTestFile(2,true,MyIO.leggiInt(" Numero di smazzate >> "), sParam);
                        break;   
                 case 8:
                        doHandTestFile(1,false,MyIO.leggiInt(" Numero di smazzate >> "), sParam);
                        break;   
                 case 9:
                        doHandTestFile(2,false,MyIO.leggiInt(" Numero di smazzate >> "), sParam);
                        break;   
                 case 10:
                        thisBid.setValues(sParam);
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
                    MyIO.stampa(get4HandPBN(0));
                    MyIO.stampa(to3Longs());
                    MyIO.stampa(to3HexLongs());
                        break;   
                 case 3:
                    MyIO.stampa(getHandFullScores());
                        break;   
                 case 4:
                    MyIO.stampa(getXMLHand(""));
                        break;   
                 case 5:
                    thisBid.setValues(sParam);
                      MyIO.stampa(thisBid.getTableScore());
//                    MyIO.stampa(thisBid.toString());
//                    MyIO.stampa("Punti= " + thisBid.getScore());
                    break;   
                 case 6:
                    try{
                    BufferedWriter xOut  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sParam)));
                    xOut.write(get4HandPBN(thisBid.getIDeclarer() - 1));
                    xOut.newLine();
                    xOut.write(thisBid.toString());
                    xOut.newLine();
                    xOut.close();  
                    }
                  catch (Exception e){ };
                    break;   
                 case 7:
                    try{
                    BufferedReader xIn  = new  BufferedReader(new InputStreamReader(new FileInputStream(sParam)));
                    set3HandPBN(xIn.readLine());
                    thisBid.setValues(xIn.readLine());
                    xIn.close();  
                    }
                  catch (Exception e){ };
                    break;   
                 case 8:
                    MyIO.stampa(thisBid.getXMLScore());
                        break;   
                 case 9:
                    try{
                    BufferedWriter xOut  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sParam)));
                    xOut.write(getXMLHand(thisBid.getXMLScore()));
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