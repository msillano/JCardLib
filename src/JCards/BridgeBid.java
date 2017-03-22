package JCards;

// import myio.*;

 /**  Classe ausiliaria, specifica per punteggi 
  *      gestisce i dati globali relativi al contratto ed al calcolo del punteggio
  *      di una mano di bridge.
  *  Si è preferita una Classe reale ad una astratta o ad un'Interface perchè molto specializzata.
  *
  *  Per l'imput/output dei dati si segue lo standarda PBN:
  *
  * [Vulnerable "None"]
  * [Scoring "IMP"]
  * [Declarer "S"]
  * [Contract "5HX"]
  * [Result "9"]
  * 
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
  *   Richiede un IGame per il costruttore.
  */

class BridgeBid {
    
        /**
         *  Se "true" i dati sono validi e disponibili, 
         *  altrimenti non inizializzato o inizializzato con dati incoerenti.
         */

        protected boolean statusOK = false;
  /*
   * vulnerable: indica la/le coppia in zona
   * Codificato bit mapped:
   * 0  == "None" , "Love" or "-"  no vulnerability
   * 1  == "NS"                    North-South vulnerable
   * 2  == "EW"                    East-West vulnerable
   * 3  == "All" or "Both"         both sides vulnerable
   */
   
    /** 
     * Se "true" il dichiarante è in zona, altrimenti è in prima.
     * Lo stato della coppia in difesa non serve
     */
         protected boolean declarer_vulnerable;
    /**
     *Declarer
     * Indica il giocatore che vince la licita.
     * player Posizione del set (giocatore): 1..NPLAYERS (N, E, S, O)
     */
         protected int declarer = 5;
//         static private String playerShort[]={ "N", "E", "S", "W" };
     /**
      * Punti della linea (NS, EO) cui appartiene il dichiarante.
      */         
         protected int declarer_points;
  /**    
  * Contract defined as:  "<k><denomination><risk>"
  * with
  *    <k>             the number of odd tricks, <k> = 1 .. 7
  *    <denomination>  the denomination of the contract, being S (spades),
  *                    H (Hearts), D (Diamonds), C (Clubs), or NT (NoTrump)
  *    <risk>          the risk of the contract, being void (undoubled),
  *                    X (doubled), or XX (redoubled)
  */    
        int      contract_tricks = 0;
        int      contract_suit = 0;
        boolean  contract_doubled;
        boolean  contract_redoubled;


/**
 * number of result tricks = 0..13
 */    
        int      result_tricks = 0;
    
        /**
         * Constructor base.
         */
        public BridgeBid()
                {
                }
    
        /*
         * Constructor and setup.
         *
         * @param s definizione di una mano.
         *
         * @post. 
         */      
           
        public BridgeBid(String s)
               {
                 setValues(s);
                }
        /**
         *  Se "true" i dati sono validi e disponibili, 
         *  altrimenti non inizializzato o inizializzato con dati incoerenti.
         */

        public boolean getStatus(){
            return statusOK;
        }

        public boolean isDeclared(){
            return (contract_tricks != 0);
        }
        public boolean isPlayed(){
            return (result_tricks != 0);
        }
      

/**
 *  Gets the Declarer as int and string.
 */
        public int getIDeclarer(){return declarer;}
        
        public String getDeclarer(){
//  North   East  South   West
            switch (declarer){
                case 1: return "Nord ";
                case 2: return "East ";
                case 3: return "South";
                case 4: return "West ";
                default: 
                        return "?";        
                 }
         }



        public String getPBNContract(){
            String s ="";    
               s = s + contract_tricks;
               switch (contract_suit){
                case 1: s= s+"C";
                        break;
                case 2: s= s+"D";
                        break;
                case 3: s= s+"H";
                        break;
                case 4: s= s+"S";
                        break;
                case 5: s= s+"NT";
                        break;
                default: 
                        s=s+"?";                                
               }
              
               if (contract_redoubled)
                        s = s +"XX";
              
               if (contract_doubled)
                        s = s +"X";
        
            return s;
         }
      
        public String getPBNResult(){
            String s ="";            
            return s+result_tricks;
         }


/**
 *  Gets the Contract Suit as string.
 */
        public int getIContractSuit(){ return contract_suit;}
        
        public String getContractSuit(){
//  Club  Diamond Heart Spades Notrumps
               switch (contract_suit){
                case 1: return "Club   ";
                case 2: return "Diamond";
                case 3: return "Heart  ";
                case 4: return "Spades ";
                case 5: return "Notrump";
                default: 
                        return "?";        
                 }
         }

/**
 *  Gets the Risk as string.
 */
        public String getRisk(){

               if (contract_redoubled)
                       return "!!";              
               if (contract_doubled)
                       return "!";
               return("");
          }



/**
 *  Legge tutti i dati necessari al calcolo del punteggio da una stringa sintetica...
 *  Accetta iniziali in italiano (minuscole) o  inglese (maiuscole).
 *     <dichiarante>:<prese><seme><contre>[:<zona>[:<punti>]]=<presefatte> 
 *  esempio: N:2CX:2:24=8
 *
 *    maiuscole: inglese (PBN)
 *    minuscole: italiano
 *  West  North   East  South
 *  ovest nord    est   sud 
 *  Club  Diamond Heart Spades Notrumps
 *  fiori quadri  cuori picche senza
 *    X|x|!    = contre
 *    XX|xx|!! = surcontre
 *
 *    sn    = MyIO.leggiStr("               Dichiara (n|e!s!o|N|E|S|W) >> ") +
 *     ":" +  MyIO.leggiStr("                      Numero prese (1..7) >> ") +
 *            MyIO.leggiStr("               Seme (f|q|c|p|s|C|D|H|S|N) >> ") +
 *            MyIO.leggiStr(" Contrate(!|X|x) o surcontrate (!!|XX|xx) >> ") +
 *      ":" + MyIO.leggiStr("          In prima (1) oppure in Zona (2) >> ");
 *    fatte = MyIO.leggiInt(" ============= Prese totali fatte (0..13) >> ");
 *    sn = sn + "=" + fatte;
 */
        void setValues(String x)
                {
                statusOK = false;    
// set declarer                    
                x = x.trim();
                if (x.substring(0,5).equals("ERROR")) return;
                declarer = 0;
                 if ((x.substring(0,2).equals("n:"))|(x.substring(0,2).equals("N:"))){
                       declarer = 1;
                       }
                 if ((x.substring(0,2).equals("e:"))|(x.substring(0,2).equals("E:"))){
                       declarer = 2;
                       }
                 if ((x.substring(0,2).equals("s:"))|(x.substring(0,2).equals("S:"))){
                       declarer = 3;
                       }
                 if ((x.substring(0,2).equals("o:"))|(x.substring(0,2).equals("W:"))){
                       declarer = 4;
                       }
               if (declarer == 0){
                    return;
                  }      
// set contract_tricks
                x = x.substring(2);
                contract_tricks = 0;
                contract_tricks= Integer.valueOf(x.substring(0,1));
                if ((contract_tricks <1)|(contract_tricks >7)){
                    return;
                }
// set contract_suit
                x = x.substring(1);
                contract_suit= 0;
            
               if ((x.substring(0,1).equals("f"))|(x.substring(0,1).equals("C"))){
                       contract_suit = 1;
                       }
               if ((x.substring(0,1).equals("q"))|(x.substring(0,1).equals("D"))){
                       contract_suit = 2;
                       }
               if ((x.substring(0,1).equals("c"))|(x.substring(0,1).equals("H"))){
                       contract_suit = 3;
                       }
               if ((x.substring(0,1).equals("p"))|(x.substring(0,1).equals("S"))){
                       contract_suit = 4;
                       }
               if ((x.substring(0,1).equals("s"))|(x.substring(0,1).equals("N"))){
                       contract_suit = 5;
                       }
               if (contract_suit == 0){
                    return;
                  }      
// set doubled
                x = x.substring(1).toUpperCase();
                contract_doubled = false;
                contract_redoubled= false;
               if ((x.substring(0,3).equals("XX:"))|(x.substring(0,3).equals("!!:"))){
                        contract_redoubled= true;
                        x = x.substring(3);
                       }
                else
               if ((x.substring(0,2).equals("X:"))|(x.substring(0,2).equals("!:"))){
                        contract_doubled= true;
                        x = x.substring(2);
                       }
                else      
                    x = x.substring(1);
// set declarer_vulnerable;
                declarer_vulnerable = false;
                if (x.substring(0,2).equals("2:")){
                        declarer_vulnerable= true;
                       }
                if (x.substring(0,2).equals("2=")){
                        declarer_vulnerable= true;
                       }
                if (x.substring(1,2).equals(":")){
                 x = x.substring(2); 
                    
// set points  
                declarer_points = 0;
                if (x.charAt(2)=='='){
                        declarer_points =Integer.valueOf(x.substring(0,2));
                        x = x.substring(3); 
                    }
                    else {
                        declarer_points =Integer.valueOf(x.substring(0,1));
                        x = x.substring(2); 
                    }
                }
                else
                    x = x.substring(2); 
// set result


                result_tricks= -1;
                result_tricks= Integer.valueOf(x);
                if ((result_tricks <0)|(result_tricks >13)){
                    return;
                    }
                statusOK = true;
                }
        /**
         * Crea una stringa sintetica con i dati della mano (inglese).
         *
         * @return Una stringa pronta per la stampa.
         */
        public String toItString()
                {
               String s = "";                    
               if (!statusOK){
                    s = "ERROR:";
                    }                          
               switch (declarer){
                case 1: s= s+"N:";
                        break;
                case 2: s= s+"E:";
                        break;
                case 3: s= s+"S:";
                        break;
                case 4: s= s+"O:";
                        break;
                default: 
                        s=s+"?:";        
                 }
               s = s + contract_tricks;
               switch (contract_suit){
                case 1: s= s+"F";
                        break;
                case 2: s= s+"Q";
                        break;
                case 3: s= s+"C";
                        break;
                case 4: s= s+"P";
                        break;
                case 5: s= s+"SA";
                        break;
                default: 
                        s=s+"?";                                
               }
              
               if (contract_redoubled)
                        s = s +"!!";
              
               if (contract_doubled)
                        s = s +"!";
               s +=" ";
               if ((contract_tricks + 6 )==result_tricks)              
                     s += "m.i.";
               if ((contract_tricks + 6) > result_tricks)              
                     s += "- "+ (contract_tricks + 6-result_tricks);
               if ((contract_tricks + 6 )< result_tricks)              
                     s += "+ "+ (result_tricks-contract_tricks - 6);
                return s;
                }
            
        public String toString()
                {
               String s = "";                    
               if (!statusOK){
                    s = "ERROR:";
                    }                          
               switch (declarer){
                case 1: s= s+"N:";
                        break;
                case 2: s= s+"E:";
                        break;
                case 3: s= s+"S:";
                        break;
                case 4: s= s+"W:";
                        break;
                default: 
                        s=s+"?:";        
                 }
               s = s + contract_tricks;
               switch (contract_suit){
                case 1: s= s+"C";
                        break;
                case 2: s= s+"D";
                        break;
                case 3: s= s+"H";
                        break;
                case 4: s= s+"S";
                        break;
                case 5: s= s+"N";
                        break;
                default: 
                        s=s+"?";                                
               }
              
               if (contract_redoubled)
                        s = s +"XX";
              
               if (contract_doubled)
                        s = s +"X";
                        
               if (declarer_vulnerable)
                     s += ":2:";
               else      
                     s += ":1:";
               s += declarer_points+"="+ result_tricks;      
                return s;
                }
    /**
     * Calcola il punteggio della mano, modo torneo
     *
     */        
            
            
   int getScore(){
      int score = 0;

      if (result_tricks < (6 +contract_tricks )){
// ======  start down
        int down= (6 +contract_tricks ) - result_tricks;
        assert (down >0);
        if (contract_redoubled) {
            if (declarer_vulnerable){
// zona surcontrato  
// 400 (prima) 600 (successive)
            score = 400 + 600*(down-1);
            }
            else {
// prima surcontrato                
// 200 (prima) 400 (seconda e terza) 600 (successive)
               switch (down){
                case 1: score = 200;
                        break;
                case 2: score = 600;
                        break;
                case 3: score = 1000;
                        break;
                default: 
                        score = 1000 + 600*(down-3);                                
               }           
            }
        }
        else
        if (contract_doubled) {
            if (declarer_vulnerable){
// zona contrato                
            score = 200 + 300*(down-1);
            }
            else {
// prima contrato                
// 100 (prima) 200 (seconda e terza) 300 (successive)
               switch (down){
                case 1: score = 100;
                        break;
                case 2: score = 300;
                        break;
                case 3: score = 500;
                        break;
                default: 
                        score = 500 + 300*(down-3);                                
               }           
            }
        }
        else{
            if (declarer_vulnerable){
// zona                 
             score = 100 * down;
            }
            else {
// prima                 
             score = 50 * down;
            }
        }
// ============  ends down    
        return ( - score);
      }
// ============  start positive
// prese del contratto
               switch (contract_suit){
                case 1: 
                case 2: 
                        score = 20*contract_tricks;
                        break;
                case 3: 
                case 4: 
                        score = 30*contract_tricks;
                        break;
                case 5:  
                        score = 40 + 30*(contract_tricks -1);
               }
        if (contract_doubled) score = score*2;
        if (contract_redoubled) score = score*4;
// premi manche e bien-joue'     
        if ((score>= 100) & (declarer_vulnerable)) score = score + 500;
        if ((score>= 100) & (!declarer_vulnerable)) score = score + 300;
        if (score < 100) score = score + 50;
        if (contract_doubled) score = score + 50;
        if (contract_redoubled) score = score + 100;
// premi slam
        if ((contract_tricks ==  6) & (!declarer_vulnerable)) score = score + 500;
        if ((contract_tricks ==  6) & (declarer_vulnerable)) score = score + 750;
        if ((contract_tricks ==  7) & (!declarer_vulnerable)) score = score + 1000;
        if ((contract_tricks ==  7) & (declarer_vulnerable)) score = score + 1500;
// prese in piu
        int plus = result_tricks - 6 - contract_tricks;
        assert(plus >= 0);
        if ((contract_doubled)& (!declarer_vulnerable)) score = score + 100*plus;
        if ((contract_doubled)& (declarer_vulnerable)) score = score + 200*plus;
        if ((contract_redoubled)& (!declarer_vulnerable)) score = score + 200*plus;
        if ((contract_redoubled)& (declarer_vulnerable)) score = score + 400*plus;

        if ((!contract_redoubled)& (!contract_doubled))
               switch (contract_suit){
                case 1: 
                case 2: 
                        score = score + 20*plus;
                        break;
                case 3: 
                case 4: 
                case 5: 
                        score = score + 30*plus;
               }
   return score; 
   }            

        /**
         * Crea una stringa per tabella con i dati della mano (inglese).
         *
         * @return Una stringa pronta per la stampa.
         */
   public String getTableScore(){
    if (!statusOK) return "<score />";
    String xs = "";
    if ((declarer==1)|(declarer==3)){
        if (declarer_vulnerable)
           xs += "II \t|";
        else   
           xs += "I \t|";
        }
    else
           xs += " - \t|";
    if ((declarer==2)|(declarer==4)){
        if (declarer_vulnerable)
           xs += "II \t|";
        else   
           xs += "I \t|";
        }
    else
           xs += " - \t|";
// --           
    xs +=contract_tricks +getContractSuit();
    xs +=getRisk();
// --
    int plus = result_tricks - 6 - contract_tricks;
    if (plus <0)
        xs += plus +"\t|";
    if (plus == 0)
        xs += " m.i. \t|";
    if (plus >0)
        xs += "+"+plus +"\t|";
     xs +=  getDeclarer()+"\t|";
// --
    if ((declarer==1)|(declarer==3)){
        xs += declarer_points+" \t|"+(40-declarer_points) +" \t|";
        }
    else
        xs += (40-declarer_points)+" \t|"+ declarer_points +" \t|";
// --
    if (plus <0){
    if ((declarer==1)|(declarer==3)){
        xs += " - \t|"+(-getScore())+"\t|";
        }
    else
        xs += (-getScore())+"\t| - \t|";
    }
    else
       {
    if ((declarer==2)|(declarer==4)){
        xs += " - \t|"+getScore()+"\t|";
        }
    else
        xs += getScore()+"\t| - \t|";
    }
    
    return xs;
   }
  
  /**
   * Torna una stringa XML con il tag "score" che contiene i dati
   *  della partita (in inglese)
   */
  
   public String getXMLScore(){
    
   if (!statusOK) return "<score />";
   String xs = "<score \r\n";
   xs += " declarer=\""+getDeclarer()+"\" \r\n";
   if (declarer_vulnerable)
      xs+=" vulnerable=\"true\" \r\n";
   else   
      xs+=" vulnerable=\"false\" \r\n";

   xs += " points=\""+declarer_points+"\" \r\n";
      
   xs += " contractSuit=\""+getContractSuit()+"\" \r\n";
//--
   if (contract_doubled)
      xs+=" risk=\"doubled\" \r\n";
   else   
   if (contract_redoubled)
      xs+=" risk=\"redoubled\" \r\n";
   else   
      xs+=" risk=\"normal\" \r\n";
   xs += " contract=\""+contract_tricks+"\" \r\n";
   xs += " result=\""+result_tricks+"\" \r\n";
   xs += ">" +getScore() +"</score> \r\n";
   return xs;
   }


}
