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
 * Obiettivo: testare JCard e fornire esempi di uso delle sue classi.
 * Sono provate le funzioni di gestione di insiemi di carte
 *  e di generazione di distribuzioni (resti).
 *
 * Sono testati gli algoritmi che utilizzano il triangolo di Tartaglia
 *  per la generazione di "tutte" le possibili distribuzioni di una o
 *  due mani.
 * Non sono testate distribuzioni (mani) complete (4 gioocatori).
 */

public class TestCard01
        {
        /**
         * Programma di test, ri-usa un CardSet standalone piu' volte.
         * Verifica dell'algoritmo di calcolo delle mani
         * calcolando le prime e ultime 10 mani.
         */
        static void doTestStartEnd()
                {
                // crea gli oggetti
                JCards.IGame theGame = new JCards.BridgeProbs(new JCards.Card52());
                JCards.CardSet TestSet = new JCards.CardSet(theGame, 13);

                // ripristina filtro standard (non quello di BridgeProbs)
                theGame.getSuite().setFilter(null);

                // print intro
                long totHANDS1 = theGame.getMAXHAND(1);
                MyIO.stampa("\r\n Test Sart-End: 10+10 mani su " + totHANDS1 + " (13 carte da 52)");

                // Test: calcola le prime 10 mani
                for (long i = 0L; i <= 9L; i++) {
                        TestSet.setHandXID(i);
                        String xres = " " + (i + 1) + "::  ";
                        xres += TestSet.toString();
                        MyIO.stampa(xres);
                        }
                MyIO.stampa("...... more ....");

                // Test: calcola le ultime 10 mani
                for (long i = totHANDS1 - 11; i < totHANDS1; i++) {
                        TestSet.setHandXID(i);
                        String xres = " " + (i + 1) + "::  ";
                        xres += TestSet.toString();
                        MyIO.stampa(xres);
                        }
                MyIO.stampa("done ok ........\r\n");
                }


        /**
         * Programma ausiliario, permette l'accesso diretto al
         * Triangolo di tartaglia, per il calcolo del numero di mani differenti.
         */
        static void doTartaglia()
                {
                // crea gli oggetti
                JCards.IGame theGame = new JCards.BridgeProbs(new JCards.Card52());

                // print intro
                MyIO.stampa("\r\n Calcolo distribuzioni:\r\n totale mani diverse possibili.");
                int risp = MyIO.leggiInt(" numero di carte  nel mazzo..>> ") ;
                int mano = MyIO.leggiInt(" numero di carte nella mano..>> ") ;
                long n = theGame.getSuite().tartaglia(risp, mano);
                MyIO.stampa("\r\n Mani differenti: " + n);
                MyIO.stampa("done ok ........\r\n");

                }

        /**
         * Bridge: statistiche sui resti, ovvero delle distribuzioni di un seme
         * nelle mani degli avversari (Est e Ovest), note le carte del seme
         * presenti in Nord e Sud.<br/>
         * Il programma chiede inizialmente le carte possedute dalla linea NS.
         * Poi calcola tutte le possibili distribuzioni delle rimanenti carte
         * (resti) nelle mani di E e W con le relative probabilità.
         */
        static void doTestResti()
                {
                // crea gli oggetti
                JCards.IGame theGame = new JCards.BridgeProbs(new JCards.Card52());

                // print intro
                MyIO.stampa("\r\n Test Resti di un seme:\r\n Introdurre le carte di N-S come AKQJT98765432");
                // input NS cards...
                String s = "";
                try {
                        s = MyIO.leggiStr("\r\n NS >> ");
                        }
                catch (Exception e) { }
                s = s.toUpperCase();
                // calcola e stampa le statistiche
                theGame.runGame(0,s);
                theGame.printGame(0,null);
                MyIO.stampa("done ok ........\r\n");
                }


        /**
         * Un menu iniziale offre la scelta tra i diversi test disponibili,
         * implementati come metodi in questa classe.
         */
        public static void main(String[] args)
                {
                while (true) {
                        MyIO.stampa("\r\n ========================================== ");
                        MyIO.stampa("Test01 per JCards package");
                        MyIO.stampa("1: TARTAGLIA: calcoli distribuzioni");
                        MyIO.stampa("2: Test prime e ultime mani (13 carte su 52).");
                        MyIO.stampa("3: Test resti in un seme (Bridge).");
                        MyIO.stampa("0: Uscita dal programma.");
                        int risp = MyIO.leggiInt("\r\n...azione >> ") ;
                        if (risp == 1)
                                doTartaglia();
                        if (risp == 2)
                                doTestStartEnd();
                        if (risp == 3)
                                doTestResti();
                        if (risp == 0)
                                break;
                        }
                }
        }