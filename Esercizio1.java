import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Antonio Lombardi
 * 		   Matricola: 0001070477
 * 		   E-mail: antonio.lombardi12@studio.unibo.it
 * 
 * 
 *         Esercizio 1 : memorizzazione parola, occorrenze
 *         
 *         In questo esercizio in aggiunta ho implementato un principio di salting, ho quindi introdotto una componente random
 *         nella memorizzazione delle chiavi, che rende più sicura la memorizzazione e aiuta a migliorare la distribuzione apparente degli hash
 *         all'interno del dizionario grazie alla variabilità extra negli input.
 *         Nel mio caso come variabile salt ho generato un intero random per comodità, 
 *         l'implementazione ideale sarebbe stata attraverso la generazione di una stringa composta da numeri e caratteri
 */ 

public class Esercizio1{
    
    S dizionario = new S(); //Struttura dati dizionario in cui memorizzo la coppia <parola, occorrenze>

    public Esercizio1(String inpString)
    {
        leggiOccorrenze(inpString);
    }

    /**
     * Il metodo leggiOccorrenze legge il file in input, aggiungendo le coppie <chiave, n_occorrenze> 
     * all'interno del dizionario S, invocando il metodo aggiungiOccorrenze(String parola, int n_occorrenze)
     * 
     * @param iString file in input
     */
    public void leggiOccorrenze(String iString)
    {
        try {
            Locale.setDefault(Locale.US);
            Scanner scan = new Scanner(new FileReader(iString));
            
            while (scan.hasNextLine()) {
                
                String linea = scan.nextLine();
                String elementi[] = linea.split(",");

                //Al metodo gli passo la chiave(stringa) trasformata in minuscolo e il n_occorrenze associato
                dizionario.aggiungiOccorrenze(elementi[1].toLowerCase(), Integer.parseInt(elementi[0].trim()));
            }
            scan.close();

        } catch (IOException ex) {
            System.err.println("Errore durante l'operazione di I/O: " + ex.getMessage());
            ex.printStackTrace(System.err);
        }   
    }

    /**
     * Metodo che legge il file in input con le parole da trovare
     * chiama il metodo occorrenzeParola che ritorna il numero di occorrenze di una determinata parola
     * @param iString
     */
    public void trovaParoleRichieste(String iString)
    {
        try {
            Scanner scan = new Scanner(new FileReader(iString));
            
            while (scan.hasNextLine()) {
                
                String parola = scan.nextLine().trim();
                //System.out.println(parola);
                if(!parola.isEmpty())
                System.out.print(parola+", "+dizionario.occorrenzeParola(parola)+"\n");
            }
            scan.close();

        } catch (IOException ex) {
            System.err.println("Errore durante l'operazione di I/O: " + ex.getMessage());
            ex.printStackTrace(System.err);
        }   
    }


    
    /**
     * Struttura dati che vuole rappresentare un dizionario, composta da una dimensione degli array, e due array con relative liste di trabocco.
     * 
     * In generale un array composto da liste di trabocco serve a gestire le collisioni, che si verificano nel momento in cui
     * una chiave data in input alla funzione hash viene trasformata nello stesso indice in cui precedentemente è stata trasformata un'altra chiave.
     * Grazie alle liste di trabocco, visto che la nostra funzione Hash non è iniettiva siamo in grado di gestire la casistica sopra descritta
     * memorizzando nella LinkedList chiavi che hanno lo stesso valore Hash.
     * 
     * La lunghezza della tabellaHash è molto importante, perchè determina il fattore di carico {alfa}
     * Con m = 139 abbiamo un fattore di carico di 0.75, che ci garantisce un buon compromesso tra l'utilizzo della memoria
     * e la bassa probabilità di collisioni
     * 
     * I due array sono entrambi implementati con questa tecnica.
     * 
     *  - Il primo array serve per memorizzare la componente Salt associata ad ogni chiave, quindi memorizza la coppia <parola, salt>
     *    una volta applicata la funzione hash alla stringa, avremo l'indice dell'array in cui quest'utlima verrà memorizzata, a questo punto 
     *    con essa memorizziamo anche il salt generato casualmente
     *    N.B = senza la memorizzazione del salt in una struttura dati, sarebbe impossibile risalire alla parola, direttamente memorizzata con il suo salt
     *    all'interno dell'array tabellaHash
     * 
     *  - Il secondo array invece serve per memorizzare le coppie <parola+salt, n_occorrenze>, quindi questa volta applichiamo la funzione hash
     *    ad una chiave che è concatenata ad una variabile casuale, il che rende di gran lunga più sicuro il sistema.
     * 
     * Nel momento in cui si vuole trovare il n_occorrenze associato ad una parola, al posto di applicare UN procedimento inverso, se ne dovranno fare
     * DUE.
     * Prima facciamo l'hashing della parola, con questo indice andiamo a cercare all'interno dell'array saltInt[] qual'è il salt usato per 
     * memorizzare questa parola.
     * 
     * Una volta prelevato il salt, concateniamo la parola con il salt e applichiamo nuovamente la funzione Hash, che restituirà
     * l'indice che ci permetterà di risalire alla posizione all'interno di tabellaHash[] in cui si trova la coppia <parola+salt, n_occorrenze>
     * 
     *    
     */
    private class S
    {
        final int k = 139; //lunghezza del dizionario : numero primo maggiore del numero di parole da gestire
        LinkedList[] tabellaHash; //Array di liste contente le associazioni <parola+salt, n_occorrenze>
        LinkedList[] saltInt; //Array di liste contenente le associazioni <parola, salt>

        private class ChiaveValore{
            String chiave;
            int valore;
            ChiaveValore next;

            //Classe che rappresenta la coppia <chiave, valore>
            public ChiaveValore()
            {
                this.chiave = "";
                this.valore = 0;
                this.next = null;
            }
            public ChiaveValore(String chiave, int valore)
            {
                this.chiave = chiave;
                this.valore = valore;
                this.next = null;
            }

        }

        //Classe LinkedList utile per creare le liste di trabocco
        private class LinkedList
        {
            ChiaveValore head;
            int size = 0;
    
    
        public LinkedList()
        {
            this.head = null;
            this.size = 0;
        }
    
        public boolean isEmpty()
        {
            return head == null;
        }
    
        public int size()
        {
            return this.size;
        }

        public void insert(ChiaveValore c)
        {
            if (head == null) {
                head = c;
            } else {
                ChiaveValore last = getLast();
                last.next = c;
            }
            size++;
        }
    
        public ChiaveValore getLast()
        {
            if (isEmpty()) {
                return null;
            }

            ChiaveValore n = new ChiaveValore();
    
            n = head;
            while(n.next != null){
                n = n.next;
            }
            return n;
        }

    
        public ChiaveValore get(int index)
        {
            if(index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index out of bounds: " + index);
            }
            ChiaveValore n = head;
    
            for(int i = 0; i < index; i++) {
                n = n.next;
            }
            return n;
        }

        public boolean contains(String chiaveDaCercare) {
            ChiaveValore current = head;
            
            while(current != null) {
                if(current.chiave.equalsIgnoreCase(chiaveDaCercare)) {
                    return true; // Trovato
                }
                current = current.next;
            }
            return false; // Non trovato
        }

        }
        
        public S()
        {
            tabellaHash = new LinkedList[k]; 
            for (int i = 0; i < k; i++) {
                tabellaHash[i] = new LinkedList(); // Inizializzazione delle linked list nell'array 
            }
            saltInt = new LinkedList[k]; 
            for (int i = 0; i < k; i++) {
                saltInt[i] = new LinkedList(); // Inizializzazione delle linked list nell'array
            }


        }  
        
        /**
         * Metodo che prende in input una stringa e il numero di occorrenze, applica un passaggio detto "salting" alla stringa --> vedere metodo aggiungiSaltAParola,
         * nel quale si concatena un intero generato randomicamente con la stringa iniziale.
         * 
         * Dopodichè il metodo richiama la funzione hashFunction che fa l'hashing della stringa.
         * Il metodo ora gestisce tre casi :
         * 
         * 1) Caso in cui la chiave non è presente nella struttura dati S
         * 2) Caso in cui la chiave è presente nella struttura dati S, aggiorniamo quindi il n_occorrenze di quella chiave 
         * 3) Caso in cui abbiamo una stringa con lo stesso valore Hash di un altra stringa allora si incrementa lista di trabocco
         *     
         * Costo medio O(1), poichè richiama aggiungiSaltAParola il quale ha un costo medio di O(1) e prosegue facendo operazioni 
         * che permettono l'accesso all'interno del dizionario con un costo costante grazie alla funzione Hash
         * 
         * @param parola
         * @param n_occorrenze
         */
        public void aggiungiOccorrenze(String parola, int n_occorrenze)
        {
            String parolaSalt = aggiungiSaltAParola(parola);
            
            int indice = hashFunction(parolaSalt);
            
            boolean aggiunto = false;
            
            if (tabellaHash[indice].size()==0) {
                tabellaHash[indice].insert(new ChiaveValore(parola, n_occorrenze));
                aggiunto = true;
            }
            else
            {
                for (int i=0; i<tabellaHash[indice].size(); i++) {
                    if(tabellaHash[indice].get(i).chiave.equalsIgnoreCase(parola)){
                        tabellaHash[indice].get(i).valore = tabellaHash[indice].get(i).valore+n_occorrenze;
                        aggiunto = true;
                    }
                }
            }  
            if(aggiunto == false)
               tabellaHash[indice].insert(new ChiaveValore(parola, n_occorrenze));

        }
        
        /**
         * Metodo che prende in input una parola, la cerca all'interno dell'array di liste saltInt[] il quale ha memorizzato i rispettivi salt di ogni
         * parola, una volta trovata concatena la chiave(parola) ed il valore(salt) ed ottiene la stringa "salted".
         * 
         * Il metodo ora fa l'hashing della stringa = parola+salt, ed ottiene l'indice in cui questa è memorizzata all'interno dell'array hashTable[]
         * a questo punto dopo aver trovato la chiave restituisce il numero delle occorrenze. 
         * 
         * Infine questo metodo resituirà il n_occorenze di quella parola
         * Costo medio O(1), dato dalla ricerca della parola all'interno di saltInt O(1) e dalla ricerca all'interno di tabellaHash O(1)
         * Il tutto reso possibile dalla doppia applicazione della funzione Hash
         * 
         * @param parola
         * 
         */
        public int occorrenzeParola(String parola)
        {
            parola = parola.toLowerCase();
            int indiceSalt = hashFunction(parola);
            
            if(saltInt[indiceSalt] == null){
                return 0;
            }

            String parolaSalted = "";
            
            for (int i=0; i<saltInt[indiceSalt].size(); i++) {
                if(saltInt[indiceSalt].get(i).chiave.equalsIgnoreCase(parola)){
                    parolaSalted =  saltInt[indiceSalt].get(i).chiave + saltInt[indiceSalt].get(i).valore;
                }
            }   

            //Seconda parte del metodo, da qui cerchiamo la parola+salt all'interno di hashTable[]
            int indice = hashFunction(parolaSalted);

            for (int i=0; i<tabellaHash[indice].size(); i++) {
                if(tabellaHash[indice].get(i).chiave.equalsIgnoreCase(parola)){
                    return tabellaHash[indice].get(i).valore;
                }
            }   

            return 0;
        }
        
        /**
         * Metodo utilizzato in aggiungiOccorrenze per fare il "salting" della parola,
         * in questo caso il salt viene creato attraverso la generazione di un intero casuale tra 100.000 e 1.000.000.
         * 
         * In questo metodo mi vado a salvare nell'array saltInt[] tutti i salt associati alle diverse stringhe.
         * Ogni stringa ha quindi associato con essa un salt generato casualmente
         * 
         * Il costo computazionale medio per il salting di una parola è O(1), poichè usiamo la tecnica dell'hashing per memorizzare le coppie
         * chiave, salt
         *  
         * @param parola
         * 
         */
        protected String aggiungiSaltAParola(String parola)
        {
            
            Random random = new Random();

            //Genero un intero casuale tra 100.000 e 1.000.000
            int salt = 100000 + random.nextInt(900000);
            
            //Hash della stringa originale
            int indiceParolaSalt = hashFunction(parola);
            
            //Caso in cui non c'è nessuna parola con quel determinato hash value
            if (saltInt[indiceParolaSalt].size()==0) {
                saltInt[indiceParolaSalt].insert(new ChiaveValore(parola, salt));
                return parola+salt;
            }
            else
            {
                //Caso in cui gli hash di più parole sono uguali e la lista di trabocco non contiene la parola
                if(!saltInt[indiceParolaSalt].contains(parola)){
                    saltInt[indiceParolaSalt].insert(new ChiaveValore(parola, salt));  
                    return parola+salt;
                }
                //Se invece la parola è gia presente nella lista di trabocco si ritorna semplicemente la parola concatenata al salt
                else{
                    for (int i=0; i<saltInt[indiceParolaSalt].size(); i++) {
                        if(saltInt[indiceParolaSalt].get(i).chiave.equalsIgnoreCase(parola)){
                            return saltInt[indiceParolaSalt].get(i).chiave + saltInt[indiceParolaSalt].get(i).valore;
                        }
                    }
                }   
            }  
                       
            return parola+salt;
        }
        
        /**
         * Metodo che prende in input una stringa e restituisce un indice di tipo intero.
         * 
         * Trasforma ogni carattere della stringa in codice ASCII, per poi sommarli l'uno con l'altro
         * infine applica il modulo alla somma per fare in modo che l'intero prodotto sia <= della lunghezza del dizionario S
         * 
         * @param string
         * 
         */
        protected int hashFunction(String string)
        {
            int somma = 0;
            char[] arrayCaratteri = new char[string.length()];
            arrayCaratteri = string.toCharArray();
            
            for (char c : arrayCaratteri) {
                int charASCII = (int) c;
                somma += charASCII;
            }      

            return somma%k;
        }
    }

    public static void main(String[] args) {
            
        if ( args.length != 2 ) {
            System.err.println("Specificare il nome dei file di input");
            System.exit(1);
        }
        Esercizio1 es = new Esercizio1(args[0]);
        es.trovaParoleRichieste(args[1]);

    }
}