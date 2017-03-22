package JCards;

import JCards.BridgeHands;
import JCards.IGame;
import JCards.ICard;

public class BridgeHandTournement extends BridgeHands implements IGame {
      static private String cardShort[]={ "N", "E", "S", "W","-" };


    /**
     * Method BridgeHandTournement
     *
     *
     */
    public BridgeHandTournement(ICard refMazzo) {
        super(refMazzo);
        // TODO: Add your code here
    }

     /**
     * Method getNPlayers
     *
     *
     * @return
     *
     */
    public int getNPlayers() {
        // TODO: Add your code here
        return super.getNPlayers();
    }


 public     String getPlayerCards(int player){
                
                
                BridgeCardSet bSet[]= new BridgeCardSet[4];
         
                bSet[0]  =  firstSet;
                assert (bSet[0]!= null);
                bSet[1]  =  bSet[0].getNextSet();
                assert (bSet[1]!= null);
                bSet[2]  =  bSet[1].getNextSet();
                assert (bSet[2]!= null);
                bSet[3]  =  bSet[2].getNextSet();
                assert (bSet[3]!= null);
                
                String xs = "";
                if (!statusOK) return xs +" Sorry: error \r\n";
                xs += " Picche: "+ bSet[player].toString(0)+"\r\n";
                xs += "  Cuori: "+ bSet[player].toString(1)+"\r\n";
                xs += " Quadri: "+ bSet[player].toString(2)+"\r\n";
                xs += "  Fiori: "+ bSet[player].toString(3)+"\r\n";
              return xs;
         }

public boolean setResult(String rx){
      thisBid = new BridgeBid(rx);
      return thisBid.getStatus();
      }

public String getResult(){
      return thisBid.toString();
      }
    
public String getItResult(){
      return thisBid.toItString();
      }
    
public boolean isDeclared(){
            return thisBid.isDeclared();
        }
    
public boolean isPlayed(){
            return thisBid.isPlayed();
        }
public String get4HandCross(){
        return super.get4HandCross();
}

public int getScore(int player){
      int score = thisBid.getScore();
      if ((thisBid.getIDeclarer() == 1) | (thisBid.getIDeclarer() == 3)){
        if (score >0){
           if ((player == 1) | (player == 3)) return score;
           return 0;
        }
        else{
           if ((player == 2) | (player == 4)) return -score;
           return 0;
        }
    }
    else{
        if (score >0){
           if ((player == 2) | (player == 4)) return score;
           return 0;
        }
        else{
           if ((player == 1) | (player == 3)) return -score;
           return 0;
        }

      }
    }

//
//  PBN get Strings
//

/**
  * - [Dealer "N"]
  * - [Vulnerable "None"]
  * [Deal "N:.63.AKQ987.A9732 A8654.KQ5.T.QJT6 J973.J98742.3.K4 KQT2.AT.J6542.85"]
  * - [Scoring "IMP"]
  * [Declarer "S"]
  * [Contract "5HX"]
  * [Result "9"]
  * [Score "-200"]
  */
public String getPBNDeal(int d){
    return "[Deal \""+get4HandPBN(d)+"\"]\r\n";
}

public String getPBNDeclarer(){
    return "[Declarer \""+cardShort[thisBid.getIDeclarer()-1]+"\"]\r\n";
}

public String getPBNContract(){
    return "[Contract \""+thisBid.getPBNContract()+"\"]\r\n";
}
public String getPBNResult(){
    return "[Result \""+thisBid.getPBNResult()+"\"]\r\n";
}
public String getPBNScore(){
    return "[Score \""+thisBid.getScore()+"\"]\r\n";
}
/*
public String get4HandCross(){
    return super.get4HandCross();
}
*/
public int getHandPoints(int x){
      return super.getHandPoints(x);
      }
    /**
     * Method getNCHand
     *
     *
     * @return
     *
     
    public int getNCHand() {
        // TODO: Add your code here
    }

    
     * Method getSuite
     *
     *
     * @return
     *
    
    public ICard getSuite() {
        // TODO: Add your code here
    }

    
     * Method getMAXHAND
     *
     *
     * @param player
     *
     * @return
     *
     
    public long getMAXHAND(int player) {
        // TODO: Add your code here
    }
     */

    /**
     * Method runGame
     *
     *
     * @param action Indice numerico che individua l'azione richiesta.
     * @param sParam Generico, funzione dell'azione richiesta.
     *
     */
  //  public void runGame(int action, String sParam) {
        // TODO: Add your code here
 // }

    /**
     * Method printGame
     *
     *
     * @param action Indice numerico che individua l'azione richiesta.
     * @param sParam Generico, funzione dell'azione richiesta.
     *
     */
  //  public void printGame(int action, String sParam) {
        // TODO: Add your code here
   // }    
}
