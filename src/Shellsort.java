import java.util.Random;

public class Shellsort{

    public static void main(String[] args) {
        int[]sizes ={500,1000,2000,4000,8000};
        int gap1 = 1;
        int gap2 = 2;
        int i =0;
        System.out.println("Algorithm Name\t|gap chosen\t|run time(in ns)\t");
        System.out.println("----------------|-----------|-------------------");
        System.out.print("Shell sort_gap1\t| 3X+1\t\t| ");
        for(int size:sizes){
            int []arr1=RandomArray(size);
            long startTime=System.nanoTime();
            sequence(arr1, gap1);
            long endTime=System.nanoTime();
            System.out.print(0.5*Math.pow(2, i++)+"k: " + (endTime-startTime) + "\t| ");
        }
        System.out.print("\n----------------|-----------|-------------------");
        System.out.print("\nShell sort_gap2\t| n/2\t\t| ");
        i = 0;
        for(int size:sizes){
            int []arr1=RandomArray(size);
            long startTime=System.nanoTime();
            sequence(arr1, gap2);
            long endTime=System.nanoTime();
            System.out.print(0.5*Math.pow(2, i++)+"k: " + (endTime-startTime) + "\t| ");
        }

        System.out.print("\n----------------|-----------|-------------------");
        System.out.print("\nMerge sort     \t| ---\t\t| ");
        i = 0;
        for(int size:sizes){
            int []arr1=RandomArray(size);
            int[] tmpArray = new int[size];
            long startTime=System.nanoTime();
            mergeSort(arr1 , tmpArray, 0, arr1.length - 1 );
            long endTime=System.nanoTime();
            System.out.print(0.5*Math.pow(2, i++)+"k: " + (endTime-startTime) + "\t| ");
        }
    }
    //execution times for each gap

    /**
     * @param sizes
     * @return
     */
    public static int[] RandomArray(int sizes){
        int[]arr=new int[sizes];
        Random r=new Random();
        for(int i=0; i<sizes;i++){
            arr[i]=r.nextInt();

        }
        return arr;

    }
    /**
     * @param arr
     * @param gapS
     */
    public static void sequence(int[]arr,int gapS){
        int n=arr.length;
        int gap;
        if(gapS == 1){
            gap = 1;
            while (gap < n / 3) {
            gap = 3 * gap + 1;
            }
            while (gap > 0) {
                for (int i = gap; i < n; i++)
                    insertionSort(arr, gap);
                gap/=3;
            }
        }
        if(gapS == 2){
            gap = n / 2;
            while (gap > 0) {
                for (int i = gap; i < n; i++)
                    insertionSort(arr, gap);
                gap/=2;
            }
        }

    }

    public static void merge( int [ ] tab, int [ ] tmpArray, int leftPos, int rightPos, int rightEnd ){
        /* Dans cette fonction, on part de 3 tab un de gauche, un de droite
        un global ensuite on compare elt de tab de gauche avec elt de tab de droite et on met le plus petit dans
        celui de ()
        * */

        /*int leftPos Début tab gauche,
        int rightPos début tab droite
        * */

        int leftEnd = rightPos - 1; // Extrémité droite de la partie gauche
        int tmpPos = leftPos;  // Position dans le tmp array
        int numElements = rightEnd - leftPos + 1;
        while( leftPos <= leftEnd && rightPos <= rightEnd )
            if( tab[ leftPos ] < tab[ rightPos ] )
                tmpArray[ tmpPos++ ] = tab[ leftPos++ ];  // Attention ici juste un elt est incrémenté pas les 2
            else
                tmpArray[ tmpPos++ ] = tab[ rightPos++ ];
        while( leftPos <= leftEnd ) // Copier le reste de la partie gauche
            tmpArray[ tmpPos++ ] = tab[ leftPos++ ];
        while( rightPos <= rightEnd ) // Copier le reste de la partie de droite
            tmpArray[ tmpPos++ ] = tab[ rightPos++ ];
        // Copier le résultat de la fusion dans la partie correspondante du tableau a
        for( int i = 0; i < numElements; i++, rightEnd-- )
            tab[ rightEnd ] = tmpArray[ rightEnd ];


    }

    public static void mergeSort(int [ ] tab, int [ ] tmpArray, int left, int right){
        /* On divise en petit tableaux
         * On trie chacun et on fusione les 2
         * On div jusqu'à avoir des tab de 1 elt ensuite on merge
         * on remonte d'un niveau on appelle merge sort ainsi de suite
         * */
        if( left < right ) {
            int center = ( left + right ) / 2;
            mergeSort( tab, tmpArray, left, center );// Trier la partie gauche
            mergeSort( tab, tmpArray, center + 1, right ); // Trier la partie droite
            merge( tab, tmpArray, left, center + 1, right ); // Fusionner les deux parties
        }
    }
    public static void insertionSort(int[]arr, int gap){
        int j;
        for( int i = gap; i < arr.length; i++ ) {
            int tmp = arr[ i ];
            for( j = i; j >= gap && tmp < arr[ j - gap ]; j-=gap )
                arr[ j ] = arr[ j - 1 ];
            arr[ j ] = tmp;
        }
    }

}