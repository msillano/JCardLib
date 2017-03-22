/**
*
* @author  m. sillano
* @version 1.00 22/10/06
*/

import myio.*;
import JCards.*;
import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Applicazione con uso del package JCards.
 * Gestione completa di un torneo di bridge,
 * gestione mani, stampa, calcolo punteggi, 
 * reports.
 */

public class GioBridgeTournement
        {
  static final int MAX_HANDS   = 100;
  static final int MAX_PLAYERS = 7;
  static final int NOPLAYER    = 20;
  
  static public String ReportHeader = "GioBridge Chicago Tournment";

  static public String ToDay;  
  static public String StoreDir = "c:\\GioBridge";  
  static public int numberOfPlayers;            
  static public String PlayersNames[] = new String[MAX_PLAYERS];
  static public int totaleMani;
 
  // PlayN//PlayE//PlayS//PlayO//Game//0|1|2|3:vulnerable//0|1|2|3:mazziere-dealer
    static public int tournementCouples[][] = new int[MAX_HANDS][7];
  
  //NSpoints//EOpoints//NSscore//EOscore
    static public int handsScores[][] = new int[MAX_HANDS][4];
  
  //NStotHPoints//EOtotHPoints//NStotPScores//EOtotPScores//STDNpoints//STDEpoints//GioNPoints//GioEPoints
    static public int tournementScores[][] = new int[MAX_HANDS/2][8];
  
  //  hand result as string (like N:4P! +1)
  static public String results[]= new String[MAX_HANDS];
  
  //playerIdx//ranxPoints//tournement0Score//tournement0Score+tournement1Score//....
  static public int playerScores[][]= new int[MAX_PLAYERS][MAX_HANDS/2+2];
  
  // --- dati partite del torneo
  static private ArrayList<BridgeHandTournement> ManiBridge= new ArrayList<BridgeHandTournement>(MAX_HANDS);
  
  static String status = "init";
  static int maniGiocate = 0;
  static int handsforGioco= 0;

  // -----------
  static public JCards.Card52 UseCard = new JCards.Card52();
  static private String playerShort[]={ "N", "E", "S", "W" };
  static private String cardLong[]={ "Nord", "Est", "Sud", "Ovest" };
  static private String vulnerableString[]={ "None", "NS", "EW", "All" };

//
// ================  simple UTILS proc
//

//
// Trim string to left
//
  static String trimTo(String s, int n){
    String u = s + "                                                                                                                                   ";
    return u.substring(0,n);
  }                                                                                          
//
// trim int to rigth
//
  static String trIn(int x, int n){
    String u;
    if (x != 0)
         u = "                                                                                          "+x;
    else u = "                                                                                          ";
    return u.substring(u.length()-n,u.length());
  }                                                                                          


//
// ================  Access data HELPER proc
//

   static int getPlayerRole(int tournement, int player){
     for (int k=0; k<4; k++)
        if (tournementCouples[tournement*handsforGioco][k]== player)  {
            MyIO.stampa("\r\nRuolo di "+PlayersNames[player]+" in partita #"+tournement+" = "+k);

            return k;
            }
     return NOPLAYER;   
   }


  static int getTotalPoints(int i,int couple){
    return tournementScores[i/handsforGioco][couple];
  }

  static int getTotalScore(int i,int couple){
    return tournementScores[i/handsforGioco][couple+2];
  }
  
  static int getSTDScore(int i,int couple){
    return tournementScores[i/handsforGioco][couple+4];
  }

  static int getGioScore(int i,int couple){
    return tournementScores[i/handsforGioco][couple+6];
  }
  


//
// ================  Data PROCESSING proc
//

  public static void setCouplesTournement(){
// per 4 giocatori     
if (numberOfPlayers == 4) {

     handsforGioco= totaleMani/3;
     for (int i= 0; i <totaleMani; i++ ) {
    int gameN = i/handsforGioco;
    
  //PlayN: A, A, A
        tournementCouples[i][0]= 0;
  //PlayE: C  D  B
        tournementCouples[i][1]= gameN==0? 2:(gameN==1?3:1);
  //PlayS: B  C  D
        tournementCouples[i][2]= gameN==0? 1:(gameN==1?2:3);
  //PlayO  D  B  C
        tournementCouples[i][3]= gameN==0? 3:(gameN==1?1:2);
  //Game
        tournementCouples[i][4]= gameN;
  //0|1|2|3:vulnerable
        tournementCouples[i][5]= (i%handsforGioco) >3?3:(i%handsforGioco);
  //0|1|2|3:mazziere
        tournementCouples[i][6]= 3-((i%handsforGioco)%4);
     }
    }                                                                             
// per 5 giocatori     
if (numberOfPlayers == 5) {

     handsforGioco= totaleMani/5;
     for (int i= 0; i <totaleMani; i++ ) {
    int gameN = i/handsforGioco;
  //PlayN: A, B, C, D, E
        tournementCouples[i][0]= gameN%5;
  //PlayE: C  D  E  A  B
        tournementCouples[i][1]= (gameN+2)%5;
  //PlayS: B  C  D  E  A
        tournementCouples[i][2]= (gameN+1)%5;
  //PlayO  E  A  B  C  D 
        tournementCouples[i][3]= (gameN+4)%5;
  //Game
        tournementCouples[i][4]= gameN+1;
  // 0|1|2|3:vulnerable
        if (handsforGioco < 4)
              tournementCouples[i][5]= (i%handsforGioco)+1;
        else      
              tournementCouples[i][5]= (i%handsforGioco)%4;
  //0|1|2|3:mazziere
        tournementCouples[i][6]= 3-((i%handsforGioco)%4);
     }
    }                                                                             
  }


static String getHeaderPartita(int hand){
    int part= (hand/handsforGioco)+1;
    return " ================================== Partita #"+part+" ====  \r\n";
}

static String getHeaderMano(int hand){
    int part= (hand/handsforGioco)+1;
    return " ------------------------- Mano n."+(hand-1)+" -----------  \r\n";
}
 
 static String getCouples(int hand, boolean sep){
    String x = "";
       x += "   " +trimTo(" N-S: "+ PlayersNames[tournementCouples[hand][0]] + " - "+PlayersNames[tournementCouples[hand][2]],28) + (sep?"|  ":" ");
       x += trimTo(" E-0: "+ PlayersNames[tournementCouples[hand][1]] + " - "+PlayersNames[tournementCouples[hand][3]],28) + "\r\n";
    return x;    
}


static String getHandVuln(int hand){
    String x = "";
         x += " NS : "+ (((tournementCouples[hand][5]==1)|(tournementCouples[hand][5]==3))?"in ZONA":"in prima")+" \t";
         x += " EO : "+ (((tournementCouples[hand][5]==2)|(tournementCouples[hand][5]==3))?"in ZONA":"in prima")+" \t";
         x += " licita "+ cardLong[tournementCouples[hand][6]] +" : " +PlayersNames[tournementCouples[hand][tournementCouples[hand][6]]] + " \r\n";
    return x;    
}


static String getPlayersList(){
    String x = "";
    for (int w= 0; w <numberOfPlayers; w++ ) x+= PlayersNames[w]+" ";
    return x;    
}


 /**
 * Torna le carte di un giocatore in una mano, oppure (caso palyer == NOPLAYER)  
 * le mami in cross...
 * Aggiunge header partita 
 *
 * @param player Indice del giocatore 0..3 oppure NOPLAYER
 *
 * @param i Numero progressivo che identifica la mano  [0..totaleMani)
 *
 * @param h L'oggetto mano attuale.
 */
 private static  String getHandStr(int player, int i, BridgeHandTournement h){
    String x = "";
// cambio partita => header   
    if ((i==0 )|| (tournementCouples[i][4] != tournementCouples[i-1][4])){
       x += getHeaderPartita(i);
       x += getCouples(i, false);
        } 
// per ogni mano        
   if (player < NOPLAYER){
   boolean gioca = false;
   for (int k=0; k<4; k++)
      if (tournementCouples[i][k]== player){
         x += "\r\n"+getHeaderMano(i);
         x += getHandVuln(i)+"\r\n";
         x += h.getPlayerCards(k);
         gioca = true; 
         }
   if (!gioca){
         x += "\r\n"+getHeaderMano(i);
         x += PlayersNames[player] + ": non gioca! \r\n";
         }
   }
   else {
         x += "\r\n"+getHeaderMano(i);
         x += getHandVuln(i)+"\r\n";
         x += h.get4HandCross();
         }
  return x;
  }   


/**
 * Crea una stringa che contiene, per un solo giocatore, le informazioni per
 * selezionare le sue carte e giocare la mano.
 * Usa anche gli array: tournementCouples, PlayersNames, cardLong.
 *
 *@param player Indice del giocatore [0..numberOfPlayers)
 *
 *@param i Numero progressivo che identifica la mano  [0..totaleMani)
 *
 *@param h Struttura dati di una mano: carte etc..
 */
 static  String getHandPlayer(int player, int i, BridgeHandTournement h){
    return getHandStr(player, i, h);
    }

/**
 * Crea una stringa che contiene le distribuzioni di una mano (cross).
 * Usa anche gli array: tournementCouples, PlayersNames, cardLong.
 *
 *@param i Numero progressivo che identifica la mano  [0..totaleMani)
 *
 *@param h Struttura dati di una mano: carte etc..
 *
 *
 */
 static  String getHandDump(int i, BridgeHandTournement h){
    return getHandStr(NOPLAYER, i, h);
    }

/**
 * File con distribuzioni carte per un giocatore
 */
 public static String FileForPlayer(int player){
     String s= "";
     s += "\r\n DISTRIBUZIONE per il giocatore "+PlayersNames[player]+"\r\n\r\n";

     for (int i= 0; i <totaleMani; i++ ) {
        BridgeHandTournement x= ManiBridge.get(i);
        s+= getHandPlayer(player, i, x);
        }                     
   return s;
   }
  
/**
 * Torna una stringa con i risultati delle partita cui appartiene
 * la mano indicata
 */
 public static String OnePartita(int from){
     int startH =  (from/handsforGioco)*handsforGioco;
     int endH =  ((from/handsforGioco)+1)*handsforGioco;

     String s= "\r\n";
     for (int i= startH; i <endH; i++ ) {
          BridgeHandTournement x= (BridgeHandTournement)ManiBridge.get(i);
          s+= getTorneoData( i, x);
          }                     
   return s;
   }
/**
 *The 15 tag names of the MTS are (in order):
* (1) Event      (the name of the tournament or match)
 *(2) Site       (the location of the event)
* (3) Date       (the starting date of the game)
* (4) Board      (the board number)
* (5) West       (the west player)
* (6) North      (the north player)
* (7) East       (the east player)
* (8) South      (the south player)
* (9) Dealer     (the dealer)
*(10) Vulnerable (the situation of vulnerability)
*(11) Deal       (the dealt cards)
*(12) Scoring    (the scoring method)
*(13) Declarer   (the declarer of the contract)
*(14) Contract   (the contract)
*(15) Result     (the result of the game)

 *
 *[Event "#"]
 *[Date "2006.11.05"]
 *[North "WBridge5"]
 *[East "WBridge5"]
 *[South "Joueur"]
 *[West "WBridge5"]

  * [Dealer "N"]
  * [Vulnerable "None"]
  * [Deal "N:.63.AKQ987.A9732 A8654.KQ5.T.QJT6 J973.J98742.3.K4 KQT2.AT.J6542.85"]
  * [Scoring "IMP"]
  * [Declarer "S"]
  * [Contract "5HX"]
  * [Result "9"]
  */
   
  public static String exportPBNHand(int hand, boolean single){
     BridgeHandTournement x= ManiBridge.get(hand);
     SimpleDateFormat DFormat = new SimpleDateFormat("yyyy.MM.dd");
     String s ="";
     if (single | (hand==0)){
        s += "[Event \""+ReportHeader+"\"]\r\n";
        s += "[Site \"Roma, Italy \"]\r\n";
        s += "[Date \""+DFormat.format(Calendar.getInstance().getTime())+"\"]\r\n";
        s += "[Competition \"Chicago\"]\r\n";
        s += "[GioPlayers \""+getPlayersList()+"\"]\r\n";
        s += "[GioHands \""+totaleMani+"\"]\r\n";
       }
     else {
        s += "[Event \"#\"]\r\n";
        s += "[Site \"#\"]\r\n";
        s += "[Date \"#\"]\r\n";
      }
      s += "[Board \""+(hand+1)+"\"]\r\n";
      s += "[North \""+ PlayersNames[tournementCouples[hand][0]]+"\"]\r\n";
      s += "[East \""+ PlayersNames[tournementCouples[hand][1]]+"\"]\r\n";
      s += "[South \""+ PlayersNames[tournementCouples[hand][2]]+"\"]\r\n";
      s += "[West \""+ PlayersNames[tournementCouples[hand][3]]+"\"]\r\n";
      s += "[Dealer \""+playerShort[tournementCouples[hand][6]]+"\"]\r\n";
      s += "[Vulnerable \""+vulnerableString[tournementCouples[hand][6]]+"\"]\r\n";
      s += x.getPBNDeal(tournementCouples[hand][6]);
      s += "{\r\n";
      s += getHeaderPartita(hand)+getCouples(hand, false)+getHandVuln(hand)+"\r\n";
//     s +=  x.get4HandCross()+"\r\n";
      if (x.isDeclared()){
          s += "Contratto: "+x.getItResult()+"\r\n";
          if (x.isPlayed()){
             s += trimTo(" hand n."+(hand+1),10)+" NS  IMP: "+trIn(handsScores[hand][2],8)+" HP: "+
              trIn(handsScores[hand][0],4)+"  ";
             s += " EO  HP: "+trIn(handsScores[hand][1],4)+" IMP: "+trIn(handsScores[hand][3],8)+"\r\n";
           }
      }
      s+= "}\r\n";
      s += "[Scoring \"IMP\"]\r\n";
      if (x.isDeclared()){
        s += x.getPBNDeclarer();
        s += x.getPBNContract();
        if (x.isPlayed()){
           s += x.getPBNResult();
           s += x.getPBNScore();
           s += "[GioResult \""+results[hand]+"\"]\r\n";
           }
      }
      s+= "\r\n";
   return s;   
  }
 
 static int countHand=0;
 
  private static void processPBNline(String l){
    try{
    
    if (l.startsWith("[GioPlayers ")){
         String tmp[] = l.split("\\W");
         numberOfPlayers = 0;
         for (int y = 0; y < MAX_PLAYERS;y++){
             if (y+3<tmp.length){
               PlayersNames[y] = tmp[y+3];
               numberOfPlayers++;
               }
           }
//        MyIO.stampa(l +"\r\n"); 
//        MyIO.stampa( "{"+tmp[0]+", "+tmp[1]+", "+tmp[2]+", "+tmp[3]+", "+tmp[4]+", "+tmp[5]+"}");
//        for (int w= 0; w <numberOfPlayers; w++ ) MyIO.stampa(w +" " +PlayersNames[w]);
    }else

    if (l.startsWith("[GioHands ")){
         String tmp[] = l.split("\\W");
         int n = Integer.valueOf(tmp[3]); 
         if (n > 0){
            totaleMani = n;           
            ManiBridge.clear();
            for (int i= 0; i <totaleMani; i++ ) {
               JCards.BridgeHandTournement y = new JCards.BridgeHandTournement(UseCard);
            ManiBridge.add(y);
            }                   
// coppie e tavoli
      setCouplesTournement();
// set players index


   for (int y = 0; y < MAX_PLAYERS;y++)
         playerScores[y][0]=y;  
        }
    countHand=0;
    }else

    if (l.startsWith("[Deal ")){
         String tmp[] = l.split("\\\"");
//          MyIO.stampa( "{"+tmp[0]+", "+tmp[1]+", "+tmp[2]+"}");
         BridgeHandTournement x= ManiBridge.get(countHand);
         x.set3HandPBN(tmp[1]);
         countHand++;
    }else

    if (l.startsWith("[GioResult ")){
         String tmp[] = l.split("\\\"");
//          MyIO.stampa( "{"+tmp[0]+", "+tmp[1]+", "+tmp[2]+"}");
         results[countHand-1] = tmp[1];
         DoAddResult(countHand-1);
    }else
    if (l.startsWith("")){
    }else
    if (l.startsWith("")){
    }else
    if (l.startsWith("")){
    }
}
       catch (Exception e){ };
}

 
/**
 * Crea una stringa che contiene le informazioni per segnare su carta gli scores,
 * ovvero con i risultati.
 * Usa anche gli array: tournementCouples, PlayersNames, cardLong.
 *
 *@param i Numero progressivo che identifica la mano  [0..totaleMani)
 *
 *@param h Struttura dati di una mano: carte etc..
 *
 */

 static  String getTorneoData( int i, BridgeHandTournement h){
    String x = "";
    int part= (i/handsforGioco)+1;
    if ((i==0 )|| (tournementCouples[i][4] != tournementCouples[i-1][4])){

       x += getHeaderPartita(i);
       x += getCouples(i, true);
       }

      if (i >= maniGiocate){
         x += "--------------------------------------------------------------------- \r\n";
         x += trimTo(" hand #"+(i+1),10)+"|          |       "+(((tournementCouples[i][5]==1)|(tournementCouples[i][5]==3))?"*":" ");
         x += " | "+ (((tournementCouples[i][5]==2)|(tournementCouples[i][5]==3))?"*":" ")+"       |          | \r\n";
      } else {
         x += "--------------------------------------------------------------------- \r\n";
         x += trimTo(" hand #"+(i+1),10)+"| "+trIn(handsScores[i][2],8)+" | "+
              trIn(handsScores[i][0],4)+"  "+(((tournementCouples[i][5]==1)|(tournementCouples[i][5]==3))?"*":" ");
         x += " | "+ (((tournementCouples[i][5]==2)|(tournementCouples[i][5]==3))?"*":" ")+
             "  "+trIn(handsScores[i][1],4)+" | "+trIn(handsScores[i][3],8)+" | "+h.getItResult()+"\r\n";
      }

    if ((i+1)%handsforGioco==0){
            int PN = getTotalPoints(i, 0);
            int PE = getTotalPoints(i, 1);
            int SN =  getTotalScore(i, 0);
            int SE =  getTotalScore(i, 1);

            int TN =    getSTDScore(i, 0);
            int TE =    getSTDScore(i, 1);
            int GN =    getGioScore(i, 0);
            int GE =    getGioScore(i, 1);
       x += "===================================================================== \r\n";
       x += "    TOTALI"+"| "+trIn(SN,8)+" |"+trIn(PN,5)+"    | "+trIn(PE,7) +" | "+trIn(SE,8)+" | \r\n";
       x += "--------------------------------------------------------------------- \r\n";
       x += "differenze"+"| "+(SN>SE? trIn(SN-SE,8):"        ")+" |"+(PN>PE? trIn(PN-PE,5):"     ")+"    | " +(PE>PN? trIn(PE-PN,7):"       ")+" | "+(SE>SN? trIn(SE-SN,8):"        ")+" | \r\n";
       x += "--------------------------------------------------------------------- \r\n";
       x += "STD points"+"| "+trIn(TN,8)+" |         |         | "+ trIn(TE,8)+" | \r\n";
       x += "--------------------------------------------------------------------- \r\n";
// inserisce "0" solo se ci sono punti (PE+PN) se cioè giocata almeno una mano
if((GN==0)&(GE==0)&((PN+PE)>0))
       x += " GioPoints"+"|        0 |         |         |        0 | \r\n";
else
       x += " GioPoints"+"| "+trIn(GN,8)+" |         |         | "+ trIn(GE,8)+" | \r\n";
        }
       return x;
   }   




  public static String getPlayersScores(){
       String x ="";
       x += "\r\n\r\n\r\n =============================== GioBridge Torneo ====  \r\n\r\n";

       for (int w= 0; w <numberOfPlayers; w++ ) {
       
          x+= trimTo("   "+PlayersNames[playerScores[w][0]],16)+"|";
          x+= trimTo(" GioPoints: "+trIn(playerScores[w][1],3),16)+"|";
          for (int t = 2; t <= maniGiocate/handsforGioco + 2;t++){
                 x+= " "+trIn(playerScores[w][t],4)+"|";
            }
       x += "\r\n --------------------------------------------------------------------- \r\n";
      }    
  return x;    
  }



//
// ================  Menu TASK PROCESSING proc
//

/**
 * Chiede e controlla i dati iniziali di un torneo
 * Stampa a video i risultati
 */

  public static String GetPass1Data(){
  try{
    
    MyIO.stampa("Dir di appoggio file ["+StoreDir+"]  >>");
    String tmp = MyIO.leggiStr();
    if (!tmp.equals("")){
    	StoreDir = tmp;
    }

    SimpleDateFormat DFormat = new SimpleDateFormat("yy-MM-dd");
    String strNow = DFormat.format(Calendar.getInstance().getTime());

    MyIO.stampa("Data del Torneo ["+strNow+"] >> ");
    tmp = MyIO.leggiStr();
    if (!tmp.equals("")){
    	strNow = tmp;
    }

    File fx = new File(StoreDir);
    fx.mkdir();
    
    ReportHeader += " " +ToDay;    
    do {
        numberOfPlayers =  MyIO.leggiInt("Numero giocatori: 4..7 >> ");
        } while ((numberOfPlayers<4) | (numberOfPlayers>7));
    for (int i = 0; i <numberOfPlayers; i++ ){
        PlayersNames[i] = MyIO.leggiStr(" - Nome del giocatore n."+(i+1)+" >> ");
// TODO: test per nomi diversi        
    }

   MyIO.stampa(StoreDir);
   boolean  done = false;
   while (!done){
      totaleMani =  MyIO.leggiInt("Numero massimo mani del Torneo >> ");
      if (totaleMani >= MAX_HANDS) totaleMani=MAX_HANDS-1;
      switch (numberOfPlayers){
                 case 4:
                        totaleMani = (totaleMani/3)*3; 
                        break;   
                 case 5:
                        totaleMani = (totaleMani/5)*5; 
                        break;   
                 case 6:
// TODO: sviluppare strategie per 6 e 7 (anche 8?? ) giocatori                 
                        break;   
                 case 7:
                        break;   
                        } 
      MyIO.stampa(" ## Numero mani del Torneo: "+totaleMani);
      String sTmp = MyIO.leggiStr("Ricalcolare il Numero mani? [s/n] >> ");
      done = sTmp.equals("s");
      }
   }
   catch (Exception e){ };
   
   MyIO.stampa("\r\n ========================================== ");
   MyIO.stampa("    " + ReportHeader +" - "+ToDay);
   MyIO.stampa("    " + "Giocatori:");
   for (int i = 0; i <numberOfPlayers; i++ ){
       MyIO.stampa("    " + "  "+ (i+1) +" - " + PlayersNames[i]);
       }
   MyIO.stampa("    " + "Numero mani del Torneo  "+  totaleMani);
   MyIO.stampa("\r\n  ------------------------------------------ ");
   
   return "pass1done";
   }



 public static String GetDataFile(){
    
    MyIO.stampa("  Sorry, not jet implemented...");
    return "init";   
  }

   

  public static String DoCreaHands(){
   for (int i= 0; i <totaleMani; i++ ) {
         JCards.BridgeHandTournement y = new JCards.BridgeHandTournement(UseCard);
         y.runGame(1,"");
         ManiBridge.add(y);
     }                   
// coppie e tavoli
      setCouplesTournement();
// set players index
  for (int y = 0; y < MAX_PLAYERS;y++)
         playerScores[y][0]=y;  
              
   return "pass1done";
   }


  public static String DoDump(){
     MyIO.stampa("  ------------------------------------------ ");
     for (int i= 0; i <totaleMani; i++ ) {
        BridgeHandTournement x= ManiBridge.get(i);
        x.get4HandCross();
        }        
             
   return status;
   }



  static String DoCreaFilePlayers(){
     for (int i= 0; i <numberOfPlayers; i++ ) {
       try{
      
          BufferedWriter xOut  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream( StoreDir+"_H_"+i+".txt")));
          xOut.write(ReportHeader);
          xOut.write(FileForPlayer(i));
          xOut.close();  
          }
       catch (Exception e){ };
       }    
  return "pass1done";
  }

  static String DoCreaFileScore(){
      try{
      
     String s= "\r\n RISULTATI Chicago \r\n\r\n";
     for (int i= 0; i <totaleMani; i++ ) {
        BridgeHandTournement x= ManiBridge.get(i);
           s += getTorneoData(i, x);
//          s+= getScoreCard( i, x);
          }                     
      s+=  getPlayersScores();
         
      BufferedWriter xOut  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(StoreDir+"_scores.txt")));
      xOut.write(ReportHeader);
      xOut.write(s);
      xOut.newLine();
      xOut.close();  
      }
         catch (Exception e){ };  
   return "pass1done";
  }
/**
 * Stampa tutte le mani (in croce) ed i dati (ma non i risultati)
 */
 
  static String DoCreaFileHands(){
     String s= "";
     for (int i= 0; i <totaleMani; i++ ) {
        BridgeHandTournement x= ManiBridge.get(i);
        s+= getHandPlayer(NOPLAYER, i, x);
        }                     
     try{
          BufferedWriter xOut  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(StoreDir+"_full.txt")));
          xOut.write(ReportHeader);
          xOut.write(s);
          xOut.newLine();
          xOut.close();  
          }
       catch (Exception e){ };
   return "pass1done";
  }

   static void   DoExportPBNHand(int hand){
     try{
          BufferedWriter xOut  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(StoreDir+"_H"+hand+".pbn")));
          xOut.write(exportPBNHand(hand, true));
          xOut.newLine();
          xOut.close();  
          }
       catch (Exception e){ };
   
   
   }
   static void   DoExportPBNAllHand(){
     String s= "";
     for (int i= 0; i <totaleMani; i++ ) {
        s+= exportPBNHand(i, false);
        }                     
     try{
          BufferedWriter xOut  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(StoreDir+"_all.pbn")));
          xOut.write(s);
          xOut.newLine();
          xOut.close();  
          }
       catch (Exception e){ };
      }

  public static boolean AskResultData(int hand){
   String sn="";
   int Declarer =0;    
      try{

    String gx = MyIO.leggiStr("               Dichiara (n|e!s!o|N|E|S|W) >> ");
    Declarer = gx=="n"? 0:gx=="e"? 1:gx=="s"? 2:gx=="o"? 3:gx=="N"? 0:gx=="E"? 1:gx=="S"? 2:gx=="W"? 3:0;
    sn = gx + ":";
     
      sn +=  MyIO.leggiStr("                      Numero prese (1..7) >> ") +
             MyIO.leggiStr("               Seme (f|q|c|p|s|C|D|H|S|N) >> ") +
             MyIO.leggiStr(" Contrate(!|X|x) o surcontrate (!!|XX|xx) >> ") +":";
          
    if ((Declarer == 0)|(Declarer == 2))
        sn += ((tournementCouples[hand][5]==1)|(tournementCouples[hand][5]==3))? "2":"1";
    else
        sn += ((tournementCouples[hand][5]==2)|(tournementCouples[hand][5]==3))? "2":"1";
      sn += "=" + MyIO.leggiInt(" ============= Prese totali fatte (0..13) >> ");
      
   }
   catch (Exception e){ return false;};
//   MyIO.stampa("\r\n"+sn+"\r\n");
   
   results[hand] = sn;

   return true;
   }


  public static void DoImportPBNGioHands(){
  try{
    StoreDir = MyIO.leggiStr("Dir di appoggio file [c:\\GioBridge]  >>");
    SimpleDateFormat DFormat = new SimpleDateFormat("yy-MM-dd");
    String strNow = DFormat.format(Calendar.getInstance().getTime());
    ToDay = MyIO.leggiStr("Data del Torneo ["+strNow+"] >> ");
    if (ToDay.equals("")){
         ToDay = strNow;  
         }
    if (StoreDir.equals(""))
         StoreDir =  "c:\\GioBridge";
    File fx = new File(StoreDir);
    fx.mkdir();
    StoreDir += "\\"+ ToDay;
   }
   catch (Exception e){ };
     try{
          BufferedReader xIn  = new BufferedReader(new InputStreamReader(new FileInputStream(StoreDir+"_all.pbn")));
          while (xIn.ready()) processPBNline(xIn.readLine());
          xIn.close();  
          }
       catch (Exception e){ };
   



  }



/**
 *  Agggiorna i punteggi, al termine (o dopo le modifica) di una mano.
 *  La mano  deve essere pre-definita, in particolare è usato getHandPoints().
 *  Utilizza results[hand] per aggiornare tutti i dati, sia della
 *  partita che della classifica per giocatori del torneo.
 *  
 *@param hand Numero progressivo che identifica la mano  [0..totaleMani)
 *
 */

  public static boolean DoAddResult(int hand){

   BridgeHandTournement x= ManiBridge.get(hand);
   if (x.setResult(results[hand])){
     results[hand] = x.getResult();
  //NSpoints
     handsScores[hand][0]= x.getHandPoints(1) + x.getHandPoints(3);
  //EOpoints
     handsScores[hand][1]= x.getHandPoints(2) + x.getHandPoints(4);
  //NSscore
  //EOscore
      handsScores[hand][2] =x.getScore(1);
      handsScores[hand][3] =x.getScore(2);
//
// create partita data (tournementScores[partita]) from hands (handsScores[])         
//
  int partita = hand/handsforGioco;
//
  tournementScores[partita][0]=0;
  tournementScores[partita][1]=0;
  tournementScores[partita][2]=0;
  tournementScores[partita][3]=0;
//
  for (int i = partita *handsforGioco; i <= hand;i++){
  //NStotHPoints
  //EOtotHPoints
  //NStotPScores
  //EOtotPScores
     tournementScores[partita][0]+=handsScores[i][0];
     tournementScores[partita][1]+=handsScores[i][1];
     tournementScores[partita][2]+=handsScores[i][2];
     tournementScores[partita][3]+=handsScores[i][3];
     }

  // 0          //  1         //   2        //   3        //   4      //  5       //  6       //  7
  //NStotHPoints//EOtotHPoints//NStotPScores//EOtotPScores//STDNpoints//STDEpoints//GioNPoints//GioEPoints
  //
  if (tournementScores[partita][2]>tournementScores[partita][3]){
     // NS vince
  //STDNpoints
  //STDEpoints
     tournementScores[partita][4] = (tournementScores[partita][2]-tournementScores[partita][3]+50)/100;
     tournementScores[partita][5] = 0;
  //GioNPoints
  //GioEPoints       
     int npoints = tournementScores[partita][0]-tournementScores[partita][1];
     if (npoints >0) npoints = (npoints+1)/3;
         else npoints = (npoints-1)/3;

     tournementScores[partita][6] =  tournementScores[partita][4]-npoints;
     tournementScores[partita][7] = 0;
// attribuisce a EO un punteggio negativo
     if (tournementScores[partita][6] < 0) {
       tournementScores[partita][7] = - tournementScores[partita][6] ;
       tournementScores[partita][6] = 0;
       } 
     }
   else  {
     // EO vince
     tournementScores[partita][5] = (tournementScores[partita][3]-tournementScores[partita][2]+50)/100;
     tournementScores[partita][4] = 0;
  //GioNPoints
  //GioEPoints       
     int epoints = tournementScores[partita][1]-tournementScores[partita][0];
     if (epoints >0) epoints = (epoints+1)/3;
         else epoints = (epoints-1)/3;
     tournementScores[partita][7] =  tournementScores[partita][5]-epoints;
     tournementScores[partita][6] = 0;
// attribuisce a NS un punteggio negativo
     if (tournementScores[partita][7] < 0) {
       tournementScores[partita][6] = - tournementScores[partita][7] ;
       tournementScores[partita][7] = 0;
       }

     } 

//
//  Create player data (playerScores[]) from tournement data (tournementScores[])
//
//  playerIdx//ranxPoints//tournement0Score//tournement0Score+tournement1Score//....
//  static public int playerScores[][]= new int[MAX_PLAYERS][MAX_HANDS/2+2]
//
//clean-up
  for (int p = 0; p < MAX_PLAYERS;p++)
     for (int i = 0; i < MAX_HANDS/2+2;i++)
         playerScores[p][i]=0;

// add tornements scores
  for (int t = 0; t <= maniGiocate/handsforGioco;t++){
      for (int z = 0; z < MAX_PLAYERS;z++){
        int points =0;
        switch(getPlayerRole(t,z)){
            case NOPLAYER:
               points = 0;
               break;
            case 0:
            case 2:
               points =  tournementScores[t][6];
               break;
            case 1:
            case 3:
               points =  tournementScores[t][7];
               break;
            }
                    
       playerScores[z][t+2]=playerScores[z][t+1] + points;
       }
  }
// set players index
  for (int y = 0; y < MAX_PLAYERS;y++){
         playerScores[y][0]=y;  
         }

// sorts players on last score from max to min
  int lastTourn = maniGiocate/handsforGioco +2;
  boolean done = false;
  while (!done){
    done = true;
// bubblesort ordering players
    for (int h = 0; h < numberOfPlayers; h++){
        if (playerScores[h][lastTourn] < playerScores[h+1][lastTourn]){
            done = false;
            int appoggio = 0;
            for (int k = 0; k < MAX_HANDS/2+2;k++){
// swap di tutti i campi  [h][*] e [h+1][*]               
                 appoggio = playerScores[h][k];  
                 playerScores[h][k]=playerScores[h+1][k];  
                 playerScores[h+1][k]= appoggio;  
                 }
            }
        } 
    } // ends while

// setting gioPoints;

    playerScores[0][1]=  10;
    
    if (playerScores[1][lastTourn]==playerScores[0][lastTourn] ) playerScores[1][1]=  playerScores[0][1];
    else  playerScores[1][1]=  8;
    
    if (playerScores[2][lastTourn]==playerScores[1][lastTourn] ) playerScores[2][1]=  playerScores[1][1];
    else  playerScores[2][1]=  6;
    
    if (playerScores[3][lastTourn]==playerScores[2][lastTourn] ) playerScores[3][1]=  playerScores[2][1];
    else  playerScores[3][1]=  5;
    
    if (playerScores[4][lastTourn]==playerScores[3][lastTourn] ) playerScores[4][1]=  playerScores[3][1];
    else  playerScores[4][1]=  4;
    
    if (playerScores[5][lastTourn]==playerScores[4][lastTourn] ) playerScores[5][1]=  playerScores[4][1];
    else  playerScores[5][1]=  3;
    
    if (playerScores[6][lastTourn]==playerScores[5][lastTourn] ) playerScores[6][1]=  playerScores[5][1];
    else  playerScores[6][1]=  2;

      
//  data processed
     return true;
     }
// bad data
   return false;
   }

//
//
//
//
  public static void main(String[] args)
     {
        
/*
 * status == "init" actions
 */        
   while (status.equals("init") )     {
   
   MyIO.stampa("\r\n ========================================== ");
   MyIO.stampa("   GioBridgeTournement package ver. 1.0");
   MyIO.stampa("  ------------------------------------------ ");
   MyIO.stampa("  Pass1: definizione Torneo:");
   MyIO.stampa("   1: Start di un nuovo torneo");
   MyIO.stampa("   2: Rilegge i dati di un torneo");
   MyIO.stampa("   0: Uscita dal programma.");
   int risp = MyIO.leggiInt("\r\n...azione >> ") ;
          
   switch (risp){
                 case 0:
                        return;                            
                 case 1:
                        status = GetPass1Data(); 
                        break;   
                 case 2:
                        DoImportPBNGioHands();
                        status= "pass1done";
                        break;   
                        } 
    } 
   while (status.equals("pass1done") )     {
/*
 * status == "pass1done" actions
 */        
   
   MyIO.stampa(" ");
   MyIO.stampa("  Pass2: preparazione Mani:");
   MyIO.stampa("   1: Genera le mani per il torneo");
   MyIO.stampa("   2: Stampa tutte le mani");
   MyIO.stampa("   3: Stampa per giocare");
   MyIO.stampa("   4: Salva su file");
   MyIO.stampa("   5: leggi da file");
   MyIO.stampa("   0: Uscita dal programma.");
   int risp = MyIO.leggiInt("\r\n...azione >> ") ;
          
   switch (risp){
                 case 0:
                        return;                            
                 case 1:
                        DoCreaHands(); 
                        break;   
                                                 
                 case 2:
                        DoCreaFileHands();
                        break;   

                 case 3:
                        DoCreaFileScore();
                        DoCreaFilePlayers(); 
                        status = "pass2done";
                        break;   
                         
                 case 4:
                        DoExportPBNAllHand();
                        break;   
                 case 5:
    MyIO.stampa("  Sorry, not jet implemented...");
                        break;   
                 }
    } 
 
   while (status.equals("pass2done") )     {
/*
 * status == "pass1done" scores
 */        
   
   MyIO.stampa(" ");
   MyIO.stampa("  Pass3: gioco del Torneo:");
   MyIO.stampa("   1: Aggiorna con risutati");
   MyIO.stampa("   2: Visualizza dati partita");
   MyIO.stampa("   3: Modifica dati di una mano ");
   MyIO.stampa("   4: Visualizza le mani");
   MyIO.stampa("   5: Stampa le mani");
   MyIO.stampa("   6: Visualizza risultati");
   MyIO.stampa("   7: Stampa risultati");
   MyIO.stampa("   8: Salva su file");
   MyIO.stampa("   9: Leggi da file");
   MyIO.stampa("  10: Export one hand (BPN)");
   MyIO.stampa("  11: Export all hand (BPN)");
   MyIO.stampa("   0: Uscita dal programma.");
   int risp = MyIO.leggiInt("\r\n...azione >> ") ;
          
   switch (risp){
                 case 0:
                        return;                            
                         
                 case 1:
//   MyIO.stampa("   1: Aggiorna con risutati");
                        MyIO.stampa(OnePartita(maniGiocate));
                        if (AskResultData(maniGiocate))
                         if (DoAddResult(maniGiocate))
                            {
                            maniGiocate++;
                            MyIO.stampa(OnePartita(maniGiocate-1));
                            MyIO.stampa(getPlayersScores());
                            }
                        break;   
                         
                 case 2:
//   MyIO.stampa("   2: Visualizza dati partita");
                        int isp = MyIO.leggiInt("\r\n...N# partita >> ") ;
                        isp = (isp-1) * handsforGioco +1;
                        MyIO.stampa(OnePartita(isp));
                        break;   
                         
                 case 3:
//   MyIO.stampa("   3: Modifica dati di una mano ");
                        int xh = MyIO.leggiInt("\r\n...N# mano da modificare >> ") ;
                        MyIO.stampa(OnePartita(xh));
                        if (AskResultData(xh))
                        if (DoAddResult(xh))
                            {
                            MyIO.stampa(OnePartita(xh));
                            }
                        break;   
                 case 4:
//   MyIO.stampa("   4: Visualizza le mani");
                        DoDump();
                        break;   
                 case 5:
//   MyIO.stampa("   5: Stampa le mani");
                        DoCreaFileHands();
                        break;   
                 case 6:
//   MyIO.stampa("   6: Visualizza risultati");
                        DoDump();
                        break;   
                 case 7:
//   MyIO.stampa("   7: Stampa (file) risultati");
                        DoCreaFileScore();
                        break;   
                 case 8:
//   MyIO.stampa("   8: Salva su file");
    MyIO.stampa("  Sorry, not jet implemented...");
                        break;   
                 case 9:
//   MyIO.stampa("   9: leggi da file");
                        DoImportPBNGioHands();
                        break;   

                 case 10:
                        int im = MyIO.leggiInt("\r\n...N# mano da esportare >> ") ;
                        DoExportPBNHand(im-1);
                        break;   

                 case 11:
                        DoExportPBNAllHand();
                        break;   

                 }
    } 
 
  }
}