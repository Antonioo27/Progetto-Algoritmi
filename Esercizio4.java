import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;
/**
 * @author Antonio Lombardi
 * 		   Matricola: 0001070477
 * 		   E-mail: antonio.lombardi12@studio.unibo.it
 * 
 * 
 *         Esercizio 4 : Applicazione dell'algoritmo di Dijkstra per ogni nodo con grafo non orientato
 * 
 *         
 */

public class Esercizio4{
    
    int n;      // numero di nodi nel grafo
    int m;      // numero di archi nel grafo    
    Vector< LinkedList<Edge> > adjList; // lista di adiacenza
    int source; // nodo sorgente
    Vector< ArrayList<Integer> > p;    // vettore di arraylist di genitori
    double[] d; // array delle distanze più brevi dal nodo sorgente
    LinkedList<Edge> sp;  // archi appartenenti al cammino minimo

    private class Edge {
        final int src;
        final int dst;
        final double w;


    public Edge(int src, int dst, double w)
        {
            assert(w >= 0.0);
            
            this.src = src;
            this.dst = dst;
            this.w = w;
        }
    }  
    public Esercizio4(String inputf)
    {
        this.source = source; 
        this.sp = new LinkedList<Edge>();
        leggiGrafoNonOrientato(inputf);
    }
    
    
    /**
    * Metodo che legge in input un file di testo, ricavando il numero di nodi, il numero di archi
    * e tutti i nodi adiacenti ad un determinato nodo, leggendo anche il peso dell'arco
    * @param inputf
    */
    private void leggiGrafoNonOrientato(String inputf)
    {
        Locale.setDefault(Locale.US);
        
        try {
            Scanner f = new Scanner(new FileReader(inputf));
            n = f.nextInt();
            m = f.nextInt();
            
            adjList = new Vector< LinkedList<Edge> >();
            
            for (int i=0; i<n; i++) {
                adjList.add(i, new LinkedList<Edge>() );
            }
            
            //Creo lista di adiacenza che serve ad avere una rappresentazione del grafo 
            //siccome il mio grafo non è orientato, ogni volta che aggiungo un arco dal nodo x ad y,
            //aggiungo un altro arco dal nodo y ad x.
            for (int i=0; i<m; i++) {
                final int src = f.nextInt();
                final int dst = f.nextInt();
                final double weight = f.nextDouble();
                assert( weight >= 0.0 ); //controllo che tutti i pesi siano positivi (condizione dell'algoritmo)
                adjList.get(src).add(new Edge(src, dst, weight));
                adjList.get(dst).add(new Edge(dst, src, weight));
            }
            f.close();
            
        } catch (IOException ex) {
            System.err.println(ex);
            System.exit(1);
        }   
    }

    /**
     * Metodo ricorsivo che per ogni coppia di nodi sorgente destinazione,
     * tramite il vettore di padri stampa tutti i nodi in cui deve passare il nodo
     * sorgente per raggiungere il nodo destinazione con il minimo costo possibile 
     * @param dst
     * @param source
     */
    protected void print_path(int dst,int source)
    {
        if (dst == source)
            System.out.print("   "+dst);
        
        else if (p.get(dst) == null)
            System.out.print("Irraggiungibile");
        
        else {
            print_path(p.get(dst).get(0),source);
            System.out.print("->" + dst);
        }
    }
    
    //Metodo che permette di chiamare il metodo print_path qual'ora ci fosse
    //più di un cammino minimo
    protected void print_other_paths(int dst,int source)
    {
       if(dst == source){
            System.out.print("   "+dst); 
            System.out.printf(", %12.4f \n",d[dst]); 
       }

       //for che mi permette di ciclare l'arraylist in una determinata posizione del vettore
       //in modo tale da poter stampare più di un cammino minimo qual'ora esisterebbe
       for (Integer padre : p.get(dst)) {
            print_path(padre, source);
            System.out.print("->"+dst);
            System.out.printf(", %12.4f \n",d[dst]);
       }
    }
    
    /**
     * Metodo che prende in input il nodo sorgente, e fa un for su tutti i nodi
     * richiamando in ogni iterazione il metodo print_other_paths che si occupa di stampare
     * uno o più cammini minimi di un determinato nodo
     */
    public void print_paths(int source )
    {
        System.out.println("\n\nSource = " + source);
        System.out.println();
        System.out.println("   s    d         dist ");
        System.out.println("---- ---- ------------ -------------------");
        for (int dst=0; dst<n; dst++) {
            System.out.printf("%4d %4d :\n", source, dst);         
            print_other_paths(dst,source);
            System.out.println();
        }
    }

    /**
     * Metodo che calcola il cammino minimo da un nodo s verso tutti gli altri,
     * se presente più di un cammino minimo, cioè cammini minimi che hanno lo stesso peso totale
     * verranno stampati entrambi, questo attraverso l'uso di un vettore composto da liste dinamiche in ogni posizione,
     * in modo tale che ogni qual volta si aggiorna la distanza di un nodo destinazione, viene inserito il padre, e se abbiamo un cammino minimo con stesso peso
     * ma con un padre differente, nella lista all'interno quella posizione nel vettore viene aggiunto il secondo padre
     * 
     * 
     * {ANALISI COSTO COMPUTAZIONALE}
     * 
     * Il metodo è implementato con una linkedList, la quale memorizza inizialmente tutti i nodi 
     * 
     * -Il cilco while viene eseguito al massimo n volte, poichè ad ogni iterazione viene estratto un nodo dalla lista
     * 
     * -il metodo di ricerca del nodo con distanza minima ha costo O(n), poichè nel caso peggiore itera tutti gli elementi della lista
     * -il metodo remove invece nel caso peggiore itera tutta la lista sino a trovare l'oggetto specificato u, di conseguenza O(n)
     * 
     * -il foreach viene eseguito in tutto O(m), poichè ogni arco del grafo viene visitato esattamente una volta
     * 
     *  Mettendo insieme le varie componenti di costo abbiamo : O(n) x 2xO(n) x O(m) = O(n²)
     *   
     *  @param s
     */
    public void shortestPaths(int s)
    {
        
        LinkedList<Integer> lista = new LinkedList<Integer>(); 
	    
        d = new double[n];
        p = new Vector< ArrayList<Integer> >();

        //inizializzazione degli arraylist all'interno del vettore dei padri
        for (int i = 0; i < n; i++) {
            p.add(i, new ArrayList<Integer>() );
        }

        Arrays.fill(d, Double.POSITIVE_INFINITY);

        //distanza dal nodo sorgente a se stesso è zero
        d[s] = 0.0;

        for (int v=0; v<n; v++) { 
            lista.add(v);
        }
        
        while (!lista.isEmpty()) {// O(n)
            
            int u = trovaNodoMinoreDistanza(lista); //costo O(n)
            lista.remove((Integer) u); //costo O(n)

            //Itero tutti i nodi adiacenti al nodo u 
            for (Edge e : adjList.get(u)) { //O(m)
                final int v = e.dst;
                //Se il nodo v non è stato ancora visitato, aggiorno la distanza
                if (d[v] == Double.POSITIVE_INFINITY) {
                    d[v] = d[u] + e.w;
                    p.get(v).add(u);
                }
                else {
                    //Se trovo una distanza minore della distanza attuale di v, la aggiorno
                    //quindi elimino tutti i padri o padre precedentemente inseriti, e aggiungo il nuovo
                    if(d[u] + e.w < d[v]){
                        d[v] = d[u] + e.w;
                        p.set(v, new ArrayList<Integer>());
                        p.get(v).add(u);
                    }
                    //Se trovo un'altro cammino minimo lo aggiungo alla lista dei padri di v
                    else if(d[u] + e.w == d[v]){
                        //controllo che il cammino sia completamente distinto
                        p.get(v).add(u);
                    }                        
                }
            }
        }
    }
    
    /**
     * Metodo che prende in input una lista di nodi, restituendo 
     * l'indice del nodo con la distanza minima
     * 
     * Per fare ciò utilizza il vettore delle distanze d, nel quale ogni indice corrisponde
     * all'id del nodo
     * 
     * @param listaNodi
     * @return
     */
    protected int trovaNodoMinoreDistanza(List<Integer> listaNodi) {
        double distanzaMinima = Double.POSITIVE_INFINITY;
        int nodoMinimo = -1;
        for (int nodo : listaNodi) {
            if (d[nodo] <= distanzaMinima) {
                distanzaMinima = d[nodo];
                nodoMinimo = nodo;
            }
        }
        return nodoMinimo;
    }

    public static void main( String args[])
    {
        //Caso in cui non viene specificato il file di input
        if (args.length != 1) {
            final int n = 100;
            System.out.printf("%d %d\n", n, n*(n-1));
            for (int i=0; i<n; i++) {
                for (int j=0; j<n; j++) {
                    if (i != j) {
                        final double weight = 0.1 + Math.random() * 100;
                        System.out.printf("%d %d %f\n", i, j, weight);
                    }
                }
            }
            return;
        }
	
        Esercizio4 sp = new Esercizio4(args[0]);
    
        //Per ogni nodo invoco il metodo che mi calcola i cammini minimi per arrivare
        //agli altri nodi e chiamo il metodo print_paths per stampare i cammini minimi
        long start_t = System.currentTimeMillis();
        for (int i=0; i<sp.n; i++){
            sp.shortestPaths(i);
            sp.print_paths(i);
        }

        long end_t = System.currentTimeMillis();
        long elapsed = (end_t - start_t);
        long min = elapsed / (60*1000);
        double sec = (elapsed -min*60)/1000.0;
        System.out.println("Elapsed time: "+sec+" sec");
    }
}