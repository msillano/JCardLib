

package myio;

import java.io.*;

/**
 * Il package myio contiene l'unica classe <B>MyIO</B> che, con i suoi metodi statici, permette la traduzione dei blocchi 
 * DNS: <B>LEGGI</B> e <B>STAMPA</B>.
 * Package utile come libreria di funzioni per una rapida implementazione di semplici applicazioni non grafiche in Java partendo dai DNS.
 *
 * <ol>
 * <li> <I> Requisito di input </I> consentire l'inserimento dei dati in una variabile &lt;int&gt; oppure &lt;String&gt;
 * <li> <I> Requisito di output </I> visualizzare sulla consolle stringhe
 * </ol>
 * @see  "Testo: Informazione automatica e Java - § 5.7.5 e cpp. 6, 7"
 * @see  <a href="../../docs/myio.jpg">Class Diagram (realizzato con JUde)</a>
 * @author ing. Alessandro Simonetta, ing. Marco Sillano
 * @author alessandro.simonetta@uniroma1.it, sillano@mclink.it
 * @version 2.1
 */
public final class MyIO {
    /**
     * Implementa i blocchi DNS: <B>STAMPA &lt;msg&gt;</B>.
     *
     * Questo metodo  riproduce a video una stringa.<BR>
     *
     *@use.  <pre>
import myio.MyIO;
   ....
   MyIO.stampa("Risultato = "+n);
   ....   </pre>
     *
     * @param msg Stringa presentata a video 
     */
    public static void stampa(String msg) {
        System.out.println(msg);
    }

    /**
     * Equivale ai blocchi DNS: <B>STAMPA &lt;msg&gt;</B> seguito da <B>LEGGI &lt;String&gt;</B>.
     *
     * Questo metodo presenta un messaggio all'utente e successivamente legge
     * una stringa.
     *
     *@use. <pre>
import myio.MyIO;
   ....
   String sName = MyIO.leggiStr("Nome utente => ");
   ....   </pre>
     *
     * @param  msg Stringa presentata a video prima della lettura del dato
     * @return La stringa introdotta dall'utente sino 'a_capo' (escluso).
     * @exception IOException errore di I/O sul canale
     */
    public static String leggiStr(String msg) throws IOException {
        String sLetto = "";
        System.out.print(msg);
        BufferedReader kbd =
                new BufferedReader(new InputStreamReader(System.in));
        sLetto = kbd.readLine();
        return sLetto;
    }

    /**
     * Implementa i blocchi DNS: <B>LEGGI &lt;String&gt;</B>.
     * 
     * Questo metodo legge una stringa.
     *
     *@use. <pre>
import myio.MyIO;
   ....
   String sName = MyIO.leggiStr();
   ....   </pre>
     *
     * @throws IOException Errore di I/O sul canale
     * @return La stringa introdotta dall'utente sino 'a_capo' (escluso).
     * @see #leggiStr(String)
     */
    public static String leggiStr() throws IOException {
        return leggiStr("");
    }


    /**
     * Equivale ai blocchi DNS: <B>STAMPA &lt;msg&gt;</B> seguito da <B>LEGGI &lt;intero&gt;</B>.
     *
     * Questo metodo presenta un messaggio all'utente e successivamente legge
     * un numero (intero).
     * Gestisce localmente gli errori utente sul numero inserito.
     *@use. <pre>
import myio.MyIO;
   ....
   int iNumber = MyIO.leggiInt("Valore massimo => ");
   ....   </pre>
     *
     * @param msg Stringa presentata a video prima della lettura del dato.
     * @return  Un numero intero introdotto dall'utente, oppure zero.
     */
    public static int leggiInt(String msg) {
        int iLetto = 0;
        try {
            iLetto = Integer.parseInt(leggiStr(msg));
        } catch (NumberFormatException e) {
            stampa("Errore: Il valore inserito non e' un intero.");
        } catch (Exception e) {
            stampa("Errore: "+ e.getMessage());
        }
        return iLetto;
    }

    /**
     * Implementa i blocchi DNS: <B>LEGGI &lt;intero&gt;</B>.
     * 
     * Questo metodo legge una valore intero.
     *
     *@use. <pre>
import myio.MyIO;
   ....
   int iNumber = MyIO.leggiInt();
   ....   </pre>
     * 
     * @return  Un numero intero introdotto dall'utente, oppure zero.
     * @see #leggiInt(String)
     */

    public static int leggiInt() {
        return leggiInt("");
    }
}
