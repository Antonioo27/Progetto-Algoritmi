import java.io.*;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Locale;
/**
 * @author Antonio Lombardi
 * 		   Matricola: 0001070477
 * 		   E-mail: antonio.lombardi12@studio.unibo.it
 * 
 * 
 *         Esercizio 3 : riempire dischi con file
 * 
 *         In questo esercizio ho appicato il problema dello zaino per riempire un singolo disco
 *         Dopodichè ho ripetuto il procedimento fino a che l'array di file è diventato vuoto
 *         
 */

    /**
     *  {RELAZIONE}
     * 
     * Il metodo riempiDisco utilizza la programmazione dinamica per risolvere una variante del problema dello zaino
     * in particolare riempire un disco attraverso dei file che hanno un peso p ed un valore v
     * In questo caso il valore è uguale al peso.
     * Utilizza una matrice di dimensione n x 650, dove n è il numero di file disponibili e 650 è la capienza di un singolo disco
     * 
     * Definizione dei sottoproblemi P(i,j)
     *  - Riempire un disco di capienza j, utilizzando un opportuno sottoinsieme dei primi i file,
     *    massimizzando il valore dei file usati.
     * 
     * Definizione delle soluzioni V[i,j]
     *  - V[i,j] è il valore massimo ottenibile dal sottoinsieme di file {x1,...,xi} con una capienza del disco di j 
     *  - i = 1,....,n
     *  - j = 0,...,650
     * 
     * Due casi base : 
     * 1) V[i,0] = 0  per ogni i = 1,...,n 
     * 
     * 2) V[1,j] = v[1]  se j >= v[1]
     *    V[1,j] = 0  se j < v[1]
     * 
     * Caso generale : 
     * 
     * - Se j < p[i], vuol dire che il file pesa troppo per essere inserito nel disco, quindi
     *   V[i,j] = V[i-1,j]
     * 
     * - Se j >= p[i] e j >= V[i-1][j-p[i]]+v[i], allora :
     *   inserisco il file all'interno del disco quindi V[i,j] = V[i,j-p[i]] + v[i]
     * 
     *  
     * Per quanto riguarda il costo computazionale dell'intero algoritmo :
     * 
     * Abbiamo il metodo riempiDisco che ha complessità O(n x P), dove n è il numero di file e P è la capienza
     * Il metodo stampa aggiornaFileRimanenti che viene ha complessità O(n)
     * 
     * Il metodo che richiama questi due metodi si chiama riempiDischiProblema
     * il quale attraverso un while controlla quando i file rimanenti sono terminati
     * Nel caso pessimo abbiamo n file tutti di dimensione P, di conseguenza questo metodo fa n iterazioni
     * In ogni iterazione chiama due metodi, il primo di complessità O(n x P), ed il secondo di complessità O(n).
     * 
     * Di conseguenza il costo computazionale complessivo è : O(n) x O(n x P) = O(n²).
     * 
     * 
     */

public class Esercizio3 {

    int[] p; //array dei pesi dei file, ovvero la loro dimensione	
    String[] nomiFile; //array dei nomi dei file in input
    int[] v; //array dei valori dei file, ovvero la loro dimensione
    int n;	//numero di file
    final int P = 650;	//capienza massima di un disco
    int[][] V; //L'elemento V[i][j] è il valore massimo che si può ottenere
               //inserendo il sottoinsieme di oggetti {x1,.,.,xi} ed avendo una capienza massima = j
    boolean[][] use; //use[i][j] mi dice se l'oggetto xi fa parte della soluzione ottima	

    /**
     * Istanzia un nuovo oggetto di questa classe, leggendo i parametri
     * di input dal file "inputF".
     *
     * @param inputF 
     */
    public Esercizio3( String inputF )
    {
        Locale.setDefault(Locale.US);
        try {
            Scanner s = new Scanner( new FileReader( inputF ) );
            n = s.nextInt();

            nomiFile = new String[n];
            p = new int[n];
            v = new int[n];

            for ( int i=0; i<n; i++ ) {
                nomiFile[i] = s.next();
                //Assegno il peso sia come peso effettivo che come valore
                p[i] = s.nextInt();
                v[i] = p[i];
            }
            s.close();
        } catch ( IOException ex ) {
            System.err.println(ex);
            System.exit(1);
        }
    }

    /**
     * Stampa la soluzione ottima. 
     * metodo invocato da riempiDisco, per ogni disco stampa i file che fanno parte della soluzione ottima
     * e lo spazio libero
     */
    protected void stampaSoluzione( int j, int i)
    {
        if(i<0){
            return;
        }
        if (use[i][j]) {
            stampaSoluzione(j-p[i], i-1);
            System.out.println(nomiFile[i]+" "+p[i]);
        }
        else
        stampaSoluzione(j, i-1);  
    }


    public boolean[][] riempiDisco()
    {
        int i,j;
        V = new int[n][1+P];
        use = new boolean[n][1+P];

   
        //Inizializzo la prima riga, inserendo il valore del peso
        //In tutte le posizioni in cui il primo file ha un peso minore della capienza j
	    for ( j=0; j<=P; j++ ) {
	        if ( j < p[0] ) {
                V[0][j] = 0;
                use[0][j] = false;
	        } 
            else {
                V[0][j] = v[0];
                use[0][j] = true;
            }
        }

	    // Calcola gli altri elementi delle matrici V[i][j] e use[i][j]
	    // per i=1..n-1, j=1..P
        for ( i=1; i<n; i++ ) {
            for ( j=0; j<=P; j++ ) {

                //Controlliamo se conviene inserire l'oggetto x_i e allo stesso tempo
                //che non venga superata la capienza j visto che andiamo a sommare il valore che corrisponde però al peso
                if ( j>= p[i] && V[i-1][j-p[i]]+v[i] > V[i-1][j] && j >= V[i-1][j-p[i]]+v[i] ) {
                    V[i][j] = V[i-1][j-p[i]]+v[i];
                    use[i][j] = true;
                } else {
                    //In questo caso non c'è spazio per l'oggetto x_i    
                    V[i][j] = V[i-1][j];
			        use[i][j] = false;
                }
            }
        }
        

        stampaSoluzione(P,(n-1));
        System.out.println("Spazio libero: "+ (P-V[n-1][P])+"\n");

        return use;
       
    }

    /**
    * Metodo che modifica gli array nomiFile[], p[] e v[] ed il numero di file n
    * in modo tale da eliminare i file usati per riempire un determinato disco 
    */
    protected void aggiornaFileRimanenti(boolean use[][])
    {
         //Due LinkedList che conterranno rispettivamente i file e i pesi che fanno parte della soluzione ottima
         LinkedList<String> fileUsati = new LinkedList<String>();
         LinkedList<Integer> pesiUsati = new LinkedList<Integer>();
         
         //Popoliamo i due arraylist 
         int j = P;
         int i = n-1;
         while ( i>=0 ) {
             if (use[i][j]) {
                 pesiUsati.add(p[i]);
                 fileUsati.add(nomiFile[i]);
                 j = j-p[i];
             }
             i = i-1;
         }
 
         //Due LinkedList che mi servono come "magazzino" per memorizzare gli array iniziali dei 
         //nomiFile e dei pesi
         LinkedList<Integer> tempPesi = new LinkedList<Integer>();
         LinkedList<String> tempString = new LinkedList<String>();
 
         //riempio i due arraylist
         for (int pesi : p) {
             tempPesi.add(pesi);
         }
 
         for (String nome : nomiFile) {
             tempString.add(nome);
         }
 
 
         // Rimuovo dall'array dei pesi iniziale, i pesi che ho usato nella soluzione
         for (int num : pesiUsati) {
             tempPesi.remove(tempPesi.indexOf(num));
         }
         
         // Rimuovo dall'array dei nomiFile iniziale, i file che ho usato nella soluzione
         for (String nome : fileUsati) {
             tempString.remove(tempString.indexOf(nome));
         }        
 
         //Popolo l'array p[] con i pesi rimasti dopo il calcolo della soluzione
         p = new int[tempPesi.size()];
         int index = 0;
         for (int num : tempPesi) {
             p[index++] = num;
         }
 
         //Faccio la stessa cosa fatta sopra stavolta per i nomiFile[]
         nomiFile = new String[tempString.size()];
         int indexString = 0;
         for (String nome : tempString) {
             nomiFile[indexString++] = nome;
         }
 
         //aggiorno anche il vettore dei valori, sempre uguale a quello dei pesi
         v = p;
         //aggiorno anche il numero di file che probabilmente sarà diminuito
         n = nomiFile.length;
    }

    /**
     * Metodo che fa un controllo sull'array di file rimanenti,
     * se esistono ancora dei file allora chiama il metodo riempiDisco che restituisce la matrice della soluzione
     * da qui chiama il metodo aggiornaFileRimanenti che si occuperà di rimuovere i file precedentemente usati
     */
    public void riempiDischiProblema()
    {
        int count = 1;
        while(nomiFile.length>0){
            System.out.println("\nDisco : "+count);
            boolean[][] use = riempiDisco();
            aggiornaFileRimanenti(use);
            count++;
        }
    }


    public static void main( String[] args )
    {
        if ( args.length != 1 ) {
            System.err.println("Specificare il nome del file di input");
            System.exit(1);
        }
        Esercizio3 z = new Esercizio3(args[0]);
	    z.riempiDischiProblema();

    }
}
