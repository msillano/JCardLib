package JCards;

// import JCards.ICard;

/**
 * Definisce un gioco, cioè una struttura che può gestire uno o più istanze di CardSet,
 * e che applica certe regole, anche evolutive.
 * In fase di creazione deve essere associato ad un mazzo di carte.
 *
 * Due metodi sono previsti per l'attività principale: runGame() e printGame().
 */

public interface IGame
        {

        /** Numero dei giocatori
         *
         *  @return numero di giocatori.
         */
        public int getNPlayers();

        /** Numero di carte per mano
         *
         *  @return numero di carte per ogni giocatore.
         */
        public int getNCHand();

        /** Accesso a ICard associato.
         *
         *  @return ICard associato al gioco in fase creazione (v. costruttore)
         *        per accedere ai servizi forniti.
         */
        public ICard getSuite();

        /** Numero totale di mani differenti.
         *
         * @param player Numero del giocatore [1..getNPlayers()].
         *
         * @return Numero di mani differenti, immaginando di estrarre dal mazzo
         *        prima le getNCHand() carte del primo giocatore, poi quelle del secondo, e
         *        cosi' via. Un long [0...getMAXHAND(x) ) individua
         *        biunivocamente una mano per il giocatore x.
         */
        public long getMAXHAND(int player);

        /** Avvia la prima fase dell'applicazione Game.
         *
         *  @param sParam stringa generica, usabile come parametro, e.g.
         *         stato del gioco, condizioni iniziali etc...
         *         Dipende dall'implementazione.
         *
         **/
        public void runGame(int action, String sParam);

        /** Avvia la seconda fase di Game con la stampa risultati.
         *
         *  @param sParam stringa generica, usabile come parametro, e.g.
         *         stato del gioco, condizioni iniziali etc...
         *         Dipende dall'implementazione.
         */
        public void printGame(int action, String sParam);


        }