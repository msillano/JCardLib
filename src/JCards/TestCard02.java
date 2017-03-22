/**
*
* @author  m. sillano
* @version 1.00 06/05/17
*/

import myio.*;
import JCards.*;

/**
 * Classe per il test del package JCards, tramite
 * alcune semplici applicazioni..
 * Obiettivo: testare BridgeHands, che a sua volta usa BridgeBid
 * e Bridge e fornire esempi di uso delle sue classi.
 *
 * Sono testate le funzioni che interessano distribuzioni complete
 *  (quattro giocatori), conversioni di formato, generazione random,
 *  input/output di mani complete in vari formati.
 * Sono anche testate le funzioni di calcolo punteggi.
 */

public class TestCard02
        {
//  Club  Diamond Heart Spades Notrumps
//  fiori quadri  cuori picche senza
//
//  X|x|!    = contre
//  XX|xx|!! = surcontre
//
//  West  North   East  South
//  ovest nord    est   sud 
//
//  maiuscole: inglese (PBN)
//  minuscole: italiano
// 
     static JCards.IGame theGame;
     
       static String getPlayData()
                {
                  String sn="";
                  int fatte = 0;
                  int punti = 0;
                  try{
                     sn    = MyIO.leggiStr("               Dichiara (n|e!s!o|N|E|S|W) >> ") +
                      ":" +  MyIO.leggiStr("                      Numero prese (1..7) >> ") +
                            MyIO.leggiStr( "               Seme (f|q|c|p|s|C|D|H|S|N) >> ") +
                            MyIO.leggiStr( " Contrate(!|X|x) o surcontrate (!!|XX|xx) >> ") +
                      ":" + MyIO.leggiStr( "          In prima (1) oppure in Zona (2) >> ");
                     fatte = MyIO.leggiInt(" ============= Prese totali fatte (0..13) >> ") ;
                     punti = MyIO.leggiInt(" ======== Punti linea dichiarante (0..40) >> ") ;
                     }
                  catch (Exception e){ };
                  sn = sn + ":"+ punti + "=" + fatte;
                  MyIO.stampa(">> " + sn);
                  return sn;
                  }

       static String getFileName()
                {
                  String sn="";
                  try{
                     sn    = MyIO.leggiStr(" Nome del file >> ");
                     }
                  catch (Exception e){ };
                  return sn;
                  }

        /**
         * Interfaccia utente per chiedere i valori presenti in
         * 3 mani, con semplice sintassi PBN-like.
         * Un metodo per inserire una distribuzione.
         */
       static void doInputHands()
                {
                  String sn="";
// la lunghezza è fissa: 13 carte + 3 separatori                  
// stringa nulla: per uscire
                  while ((sn.length() != 16) & (sn.length() != 0)) {
                  try{
                     sn = MyIO.leggiStr("N cards (23A.AT3..456jkQ) >> ") ;
                     }
                  catch (Exception e){ };
                   }

                  String se="";
// la lunghezza è fissa: 13 carte + 3 separatori                  
// stringa nulla: per uscire
                  while ((sn.length() != 16) & (sn.length() != 0)) {
                  try{
                     se = MyIO.leggiStr("E cards (23A.AT3..456jkQ) >> ") ;
                     }
                  catch (Exception e){ };
                  } 
                   
                  String ss="";
// la lunghezza è fissa: 13 carte + 3 separatori                  
// stringa nulla: per uscire
                  while ((sn.length() != 16) & (sn.length() != 0)) {
                  try{
                     ss = MyIO.leggiStr("S cards (23A.AT3..456jkQ) >> ") ;
                     }
                  catch (Exception e){ };
                   }
                  if((sn.length() == 16) &(se.length() == 16) &(ss.length() == 16) ) {
                     theGame.runGame(2, "N:"+sn+" "+se+" "+ss);
                     }
                }


        /**
         * Un menu iniziale offre la scelta tra i diversi test disponibili,
         * implementati come metodi in questa classe.
         */
        public static void main(String[] args)
                {
                 theGame = new JCards.BridgeHands(new JCards.Card52());
                 while (true) {
                        MyIO.stampa("\r\n ========================================== ");
                        MyIO.stampa("Test02 per JCards package");
                        MyIO.stampa(" 1: Generazione di mani Random - A");
                        MyIO.stampa(" 2: Generazione di mani Random - B");
                        MyIO.stampa(" 3: Inserisci dati distribuzione");
                        MyIO.stampa(" 4: Stampa distribuzione on screen");
                        MyIO.stampa(" 5: Stampa punteggi");
                        MyIO.stampa(" 6: Stampa indici distribuzione on screen");
                        MyIO.stampa(" 7: Stampa mano XML on screen");
                        MyIO.stampa(" 8: Calcolo punteggio di una partita");
                        MyIO.stampa(" 9: Stampa punteggio XML on screen");
                        MyIO.stampa(" 10: Salva su file");
                        MyIO.stampa(" 11: Leggi da file");
                        MyIO.stampa(" 12: Export XML file");
                        MyIO.stampa(" 0: Uscita dal programma.");
                        int risp = MyIO.leggiInt("\r\n...azione >> ") ;
          
                 switch (risp){
                 case 0:
                        return;                            
                 case 1:
                        theGame.runGame(1,"");                            
                        break;   
                 case 2:
                        theGame.runGame(2,"");                            
                        break;   
                 case 3:
                        doInputHands();                            
                        break;   
                 case 4:
                        theGame.printGame(1,"");
                        break;   

                 case 5:
                        theGame.printGame(3,"");
                        break;   
                 case 6:
                        theGame.printGame(2,"");
                        break;   
                 case 7:
                        theGame.printGame(4,"");
                        break;   
                 case 8:
                        theGame.printGame(5,getPlayData());
                        break;   
                 case 9:
                        theGame.printGame(8,"");
                        break;   
                 case 10:
                        theGame.printGame(6,getFileName());
                        break;   
                 case 11:
                        theGame.printGame(7,getFileName());
                        break;   
                 case 12:
                        theGame.printGame(9,getFileName());
                        break;   
                        } 
                 }
             }
        }