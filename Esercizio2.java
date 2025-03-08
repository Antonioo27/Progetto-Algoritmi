import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.Scanner;

/**
 * @author Antonio Lombardi
 * 		   Matricola: 0001070477
 * 		   E-mail: antonio.lombardi12@studio.unibo.it
 * 
 * 
 *         Esercizio 2 : Caselle raggiungibili dal cavallo 
 *         
 */ 

public class Esercizio2 {
    int n; //numero di righe scacchiera
    int m; //numero di colonne scacchiera
    char[][] scacchiera;
    int[] indiceCavallo = new int[2]; //posizione del cavallo nella scacchiera
    int[][] mosseCavallo = {
        {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
        {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };

    
    public Esercizio2(String inputf)
    {
        leggiScacchiera(inputf);
    }
/**
 * Metodo che legge in input un file che rappresenta una scacchiera e popola una matrice di caratteri
 * con la relativa lunghezza e larghezza.
 * @param inputf
 */
    private void leggiScacchiera(String inputf)
    {
        Locale.setDefault(Locale.US);

        try {
            Scanner f = new Scanner(new FileReader(inputf));
            n = f.nextInt();
            m = f.nextInt();
            
            scacchiera = new char[n][m];
            int count = 0;

            while (f.hasNextLine()) {
                String riga = f.next();
                char[] caratteri = riga.toCharArray();
                
                //Popolo la scacchiera e quando la trovo salvo la posizione del cavallo
                for(int i=0; i<m; i++){
                    if(caratteri[i]=='C'){
                        indiceCavallo[0] = count;
                        indiceCavallo[1] = i;
                    }
                    scacchiera[count][i] = caratteri[i]; 
                }
                count++;
            }
            f.close();
        } catch (Exception e) {
            System.err.println("File non trovato");
            e.printStackTrace();
        }
        
    }
    /**
     * Metodo che prende in input una matrice char e stampa la matrice
     * @param scacchiera
     */
    private void stampaScacchiera(char[][] scacchiera)
    {
        //Itera le righe della matrice
        for (int i = 0; i < scacchiera.length; i++) {
            // Itera le colonne della matrice
            for (int j = 0; j < scacchiera[i].length; j++) {
                // Stampa il valore corrente della matrice
                System.out.print(scacchiera[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Metodo che sfrutta la ricerca in ampiezza (Breadh first search), quindi utilizza una coda (FIFO) 
     * nella quale inizialmente viene inserita la posizione del cavallo.
     * 
     * Ad ogni iterazione viene estratto il primo elemento dalla coda,
     * si controllano quali caselle sono raggiungibili dal cavallo
     * in quella determinata posizione e vengono inserite in coda.
     * Le posizioni raggiungibili dal cavallo vengono marcate con il carattere 'C'.
     * 
     * il metodo restituisce true se una volta che la lista è vuota, all'interno della scacchiera ci sono ancora caselle con il valore "."
     */
    public boolean BFS()
    {
        Queue<int[]> coda = new LinkedList<>();
        //Inseriamo nella coda la posizione iniziale del cavallo
        coda.add(indiceCavallo);

        while(!coda.isEmpty()){
            int[] posizione = coda.poll();
            //Inseriamo in coda tutte le posizioni in cui un cavallo si può spostare
            coda = controllaMosse(posizione,coda);
        }
        stampaScacchiera(scacchiera);

        //Metodo che controlla se esistono posizioni le quali il cavallo non è riuscito a visitare 
        return checkScacchiera();
    }
    
    /**
     * Metodo ausiliare di BFS(), prende in input un array di interi che rappresentano 
     * una posizione all'interno della scacchiera,
     * e la coda, fa dei controlli sulle possibili 8 mosse dalla posizione in input.
     * Le mosse ammissibili vengono poi inserite in coda. 
     * Infine il metodo restituisce la coda modificata.
     * 
     * 
     * @param posizioneAttuale
     * @param coda
     */
    public Queue<int[]> controllaMosse(int[] posizioneAttuale, Queue<int[]> coda)
    {
    
        for (int[] mossa : this.mosseCavallo) {
            int nuovaRiga = posizioneAttuale[0] + mossa[0];
            int nuovaColonna = posizioneAttuale[1] + mossa[1];
        
            //Controllo che lo spostamento sia all'interno della scacchiera e che nella casella destinazione non sia presente una 'X' o una 'C'
            if (nuovaRiga >= 0 && nuovaRiga < scacchiera.length && nuovaColonna >= 0 
                && nuovaColonna < scacchiera[0].length &&  scacchiera[nuovaRiga][nuovaColonna] == '.') {
                scacchiera[nuovaRiga][nuovaColonna] = 'C';
                coda.add(new int[]{nuovaRiga, nuovaColonna});
            }
        }

        return coda;
    }

    /**
     * Metodo ausiliare di BFS(), cicla tutti gli elementi della scacchiera, non appena incontra un valore
     * = '.' restituisce false, true altrimenti
     * @return
     */

    private boolean checkScacchiera()
    {
        for (int i = 0; i < scacchiera.length; i++) {
            for (int j = 0; j < scacchiera[i].length; j++) {
                if (scacchiera[i][j]=='.') 
                    return false;     
            }
        }
        return true;
    }
    
    public static void main( String[] args) {
        
        if ( args.length != 1 ) {
            System.err.println("Specificare il nome del file di input");
            System.exit(1);
        }
        
        Esercizio2 ob = new Esercizio2(args[0]);       
        System.out.println(ob.BFS());
    }

    
}
