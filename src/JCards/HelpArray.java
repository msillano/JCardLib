package JCards;

import myio.*;

public  class HelpArray {
       
              /**
         * Test di un elemento in un int[].
         *
         * @return true se value è un elemento di arrayInt[].
         */
        public static boolean presentInArray(int[] arrayInt, int value)
                {
                for (int i = 0; i < arrayInt.length;i++)
                        if (arrayInt[i] == value)
                                return true;
                return false;
                }

       
       /**
         * Elimina da un array int[] un elemento.
         *
         * Sposta tutti gli elementi successivi, l'ultimo è duplicato.
         *
         * @param table Array di int.
         * @param idx  Indice dell'elemento da eliminare (non può essere l'ultimo valore di table).
         */
        public static  void killElement(int[] table, int idx)
                {
                assert (idx < table.length);

                for (int y = idx; y < table.length - 1;y++)
                        table[y] = table[y + 1];
                }

        /**
         * Elimina dall'array big[] tutti gli elementi in table[] presenti anche in big[].
         * Accetta dublicati ed elementi non in ordine in table[].
         * Non è richiesto l'ordinamento di big[]. <br/>
         * Non elimina l'ultimo elemento di big[], che anzi è duplicato ad ogni eliminazione.
         */
        public static  int diffArray(int[] big, int[] table )
                {
//                                 debugPrintArray("before-kill", big);
                int k = 0;
                for (int i = 0; i < table.length;i++)
                        for (int j = 0; j < big.length;j++)
                                if (big[j] == table[i]) {
                                        killElement(big, j);
                                        k++;
                                        }
//                                 debugPrintArray("after-kill", big);
                return k;
                }
        
       
       
        public static void swapp(int[] arrayInt, int p1, int p2){
             assert (p1 < arrayInt.length);
             assert (p2 < arrayInt.length);
             int tmp = arrayInt[p1];
             arrayInt[p1] = arrayInt[p2];
             arrayInt[p2] = tmp;
             }
         

        public static void bSort(int[] arrayInt){
             boolean swap;
             do {
                swap = false;
                for (int i = 0; i < arrayInt.length-1; i++)
                   if (arrayInt[i]>arrayInt[i+1]){
                        swapp(arrayInt, i, i+1);
                        swap = true;
                   }
                } while (swap);
             }
       
        public static void cpyArray(int[] to, int[] from, int start, int end){
                int k = 0;                
                for (int i = start; i < end; i++)
                   {
                    assert(k < to.length);
                    to[k++] = from[i];
                   }
             }

        /**
         * Utility per stampa in debug di int[].
         */
        public static void debugPrintArray(String nome, int[] x)
                {
                String xres = " *** " + nome + "[" + x.length + "] = {";
                for (int i = 0; i < x.length; i++)
                        xres += x[i] + ", ";
                xres += "}";
                MyIO.stampa(xres);
                }

}
