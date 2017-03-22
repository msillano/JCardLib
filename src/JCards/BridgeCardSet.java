package JCards;


/**
 * Estende CardSet per il calcolo delle distribuzioni delle mani di Bridge.
 *
 * Sono aggiunti metodi specifici.
 *
 * PBN:
 *    <suit> = S , H , D , C
 *    <rank> = A , K , Q , J , T , 9 , 8 , 7 , 6 , 5 , 4 , 3 , 2.
 *
 */


public class BridgeCardSet extends CardSet
        {

        /** Numero di carte del primo seme presenti nella mano */
        public int firstSuiteCards;

        public int handCards[]= { 0,0,0,0 };

        /** punteggio (Bridge) della mano */
        public int bridgeCardPoints;

//====================================================================
//====================================================================
// constructors

        /**
         * Constructor BridgeCardSet()
         *
         * Utilizza il costruttore della superclasse.
         */
        public BridgeCardSet(IGame gioco, int cSize)
                {
                super(gioco, cSize);
                }

        /**
         * Constructor BridgeCardSet()
         *
         * Utilizza il costruttore della superclasse.
         */
        public BridgeCardSet(BridgeCardSet set)
                {
                super(set);
                }

        /**
         * Override di  CardSet.getClone()
         *
         * Metodo di copia per BridgeCardSet.
         */
        public BridgeCardSet getClone()
                {
                BridgeCardSet x = new BridgeCardSet(Gioco, setSize );
                // aggiorna fullSize
                x.setFullSize(fullSize);
                // e copia VIXavailable
                x.setVIXavailable(VIXavailable);
                return x;
                }

//====================================================================

        /**
         * Override di CarSet.getNextSet()
         */
        public BridgeCardSet getNextSet()
                {
                return (BridgeCardSet)pNext;
                }

        /**
         * Overload di CarSet.setNextSet()
         */
        public void setNextSet(BridgeCardSet cardset)
                {
                pNext = cardset;
                }

        /**
         * Override di CardSet.setHandXID().
         *
         * Estende il metodo di base aggiungendo il calcolo
         * del numero di carte e del punteggtio nel primo seme.
         *
         * @param aXID Numero long che identifica la mano.
         *  Deve essere compreso in [0..getTotHands() ).
         *
         * @post. Risultano aggiornati i field: XIDdata, FirstSuiteCards
         *    e gli array VDFdata e VIXdata.
         */
        public void setHandXID(long aXID)
                {
                super.setHandXID(aXID);
                firstSuiteCards = countFirstSuite();
                bridgeCardPoints = getPuntiSet();
                }



        /**
         * Override di CardSet.setVIX().
         *
         * Estende il metodo di base aggiungendo il calcolo
         * del numero di carte e del punteggtio nel primo seme.
         *
         * @param array dati di una mano.
         *   nota. fullSize e VIXavailable devono essere corretti.
         *
         *  @post. Risultano aggiornati i field: XIDdata, FirstSuiteCards
         *    e gli array VDFdata e VIXdata.
         */
        public void setVIX(int[] array)
                {
                super.setVIX(array);
                firstSuiteCards = countFirstSuite();
                bridgeCardPoints = getPuntiSet();
                }


//====================================================================
//====================================================================
// new public

        /**
         * Totale mani diverse ma con resti uguali.
         *
         * Torna il numero totale di mani E con le stesse carte del
         * primo seme (resti).
         */
        public long getRestiHandsLike()
                {
                assert(Gioco != null);
                return Gioco.getSuite().tartaglia( 26 - setSize, 13 - firstSuiteCards);
                }

        /**
         * Appende una nuova distribuzione di resti.
         *
         * Aggiunge alla lista linkata un nuovo BridgeCardSet
         * solo se ha le carte del primo seme (resti) differenti
         * dai BridgeCardSet gia' presenti nella lista.
         *
         * @param aRx Un altro BridgeCardSet inizializzato con una valida distribuzione
         *       in VIXdata. E' richiesto uguale anche setSize.
         *
         *  @post. Se diverso a quelli esistenti, accoda aRx alla lista usando
         *          pNext dell'ultimo elemento.
         */
        public void addResultResti(BridgeCardSet aRx)
                {
                assert(aRx != null) ;
                if (ugualsFirstSuite(aRx))
                        return ;
                if (pNext == null) {
                        pNext = aRx;
                        }
                else {
                        // safe downcasting se si usa solo addResultResti() per creare la lista.
                        ((BridgeCardSet)pNext).addResultResti(aRx);
                        }
                }

        /**
         */
        public int getBridgeCardPoints()
                {
                return bridgeCardPoints;
                }

         public String toBPNString(){
            return toString(0)+"."+toString(1)+"."+toString(2)+"."+toString(3);
         }


//====================================================================
// private

        /**
         * Confranta i resti in due distribuzioni.
         *
         * Torna "true" se due mani hanno uguali le carte
         * del primo seme (resti), non contano le restanti carte.
         *
         * @ param aRx Un altro CardSet con una valida distribuzione
         *       in VIXdata. E' richiesto uguale anche setSize.
         *
         * @result true se aRX è uguale a this, limitamante alle
         *      carte del primo seme (VIX < 13), ovvero ai resti.
         */
        private boolean ugualsFirstSuite(CardSet aRx)
                {
                assert(XIDdata != -1);
                assert(aRx.XIDdata != -1);
                assert(setSize == aRx.getSetSize());

                for (int i = 0;i < setSize;i++)
                        if ((VIXdata[i] != aRx.VIXdata[i]) & ((aRx.VIXdata[i] < 13) | (VIXdata[i] < 13)) )
                                return false;
                return true;
                }

        /**
         *  Conta le carte dei vari semi, aggiornando handCards[].
         *
         * @return Numero di carte del primo seme (resti) presenti nella mano.
         */
        private int countFirstSuite()
                {
                assert(XIDdata != -1);
                int x = 0;
                handCards[0]= 0;
                handCards[1]= 0;
                handCards[2]= 0;
                handCards[3]= 0;
                for (int i = 0;i < setSize;i++) {
                        if (VIXdata[i] < 13 ){
                                handCards[0]++;
                                x++;
                        }
                        if ((VIXdata[i] >= 13 ) & (VIXdata[i] < 26 ))
                                handCards[1]++;
                        if ((VIXdata[i] >= 26 ) & (VIXdata[i] < 39 ))
                                handCards[2]++;
                        if ((VIXdata[i] >= 39 ) & (VIXdata[i] < 52 ))
                                handCards[3]++;
                        }
                return x;
                }  
        }