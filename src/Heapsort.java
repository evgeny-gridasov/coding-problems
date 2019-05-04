/**
 * Heap sort algorithm implementation
 * Also prints the heap before sorting for better understanding
 *
 * This exercise is to practice algorithms, not Java style/correctness.
 *
 * Usage: java Heapsort [CSV list of integers]
 *
 * Example: java Heapsort 3,7,1,4,9,4,2
 * Built heap:
 *        1
 *    4       2
 *  7   9   4   3
 * Sorted array:
 * 1 2 3 4 4 7 9
 *
 */
public class Heapsort {

    static int[] unsorted;

    public static void main(String[] args) {
        if (!parseArgs(args)) {
            return;
        }

        int [] heap = buildHeap();
        System.out.println("Built heap:");
        printHeap(heap);

        sort(heap);

        System.out.println("Sorted array:");
        for (int i: unsorted) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
    }

    /**
     * Using the heap property of the top element,
     * swap it out with the last element and bubble down
     * @param heap
     */
    static void sort(int[] heap) {
        for (int i=0,j=unsorted.length-1; i<unsorted.length; i++,j--) {
            unsorted[i] = heap[0];
            heap[0] = heap[j];
            bubbleDown(heap,0, j);
        }
    }

    /**
     * Build the heap
     */
    static int[] buildHeap() {
        int [] heap = new int[unsorted.length];

        for (int i=0; i<unsorted.length; i++) {
            heap[i] = unsorted[i];
            bubbleUp(heap, i);
        }

        return heap;
    }

    /**
     * Check parent element and swap if it is bigger recursively
     * The aim is to build a heap with min element on top
     */
    static void bubbleUp(int[] heap, int index) {
        if (index == 0) return;
        int parent = (index + 1) / 2 - 1;
        if (heap[parent] > heap[index]) {
            swap(heap, parent, index);
            bubbleUp(heap, parent);
        }
    }

    /**
     * Pick the smallest child element and swap over recursively
     * The opposite to bubbleUp
     */
    static void bubbleDown(int[] heap, int index, int limit) {
        int child = (index + 1) *2 - 1;
        if (child >= limit) return;

        // swap with the smaller child
        if (child + 1 < limit && heap[child] > heap[child+1]) {
            child++;
        }
        if (heap[index] > heap[child]) {
            swap(heap, index, child);
            bubbleDown(heap, child, limit);
        }
    }

    /**
     * Swap 2 elements of an int array
     */
    static void swap(int[] array, int i, int j) {
        int tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    /**
     * Prints the heap contents in a tree-like fashion
     */
    static void printHeap(int[] heap) {
        // find max depth of the tree represented by the heap
        int depth = 0;
        for (int i=1; i <= heap.length; i=i<<1, depth++); // would log2 be cheaper?

        /**
         * i - current index in the heap
         * levelMax - max elements per current level
         * levelPos - current position on the level
         * levelStep - number of spaces per step when printing
         */
        for (int i = 0, levelMax = 1, levelPos = 1, levelStep = 4 << (depth - 1); i < heap.length; i++, levelPos++) {

            // check max elements per level and go to the next level if needed
            if (levelPos > levelMax) {
                levelMax = levelMax << 1;
                levelPos = 1;
                levelStep = levelStep / 2;
                System.out.println();
            }

            // pre element spaces
            for (int j = 0; j < levelStep / 2 - 1; j++) {
                System.out.print(" ");
            }
            System.out.print(heap[i]);

            // correction for longer numbers
            int delta = -1;
            for (int j = Math.abs(heap[i]); j>0; j=j/10, delta++); // would log10 be cheaper?

            // post element spaces
            for (int j = 0; j < levelStep / 2 - delta; j++) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    static boolean parseArgs(String[] args) {
        if (args.length != 1) {
            printHelp();
            return false;
        }
        try {
            String[] strs = args[0].split(",");
            unsorted = new int[strs.length];
            for (int i=0; i<strs.length; i++) {
                unsorted[i] = Integer.parseInt(strs[i]);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            printHelp();
            return false;
        }
        return true;
    }

    static void printHelp() {
        System.out.println("Usage: java Heapsort [CSV list of integers]");
        System.out.println("Example: java Heapsort 3,7,1,4");
    }
}
