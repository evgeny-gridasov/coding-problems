import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dispenses an amount in notes certain denomination. Not the best implementation, but it works.
 * This exercise is to practice algorithms, not Java style/correctness.
 *
 * Usage: java Bankmat [Sorted CSV denominations list] [Amount to dispense]
 *
 * Example: java Bankomat 20,50 310
 * Dispensing: $50x5 $20x3
 *
 * Example: java Bankomat 10,20,50,100 1370
 * Dispensing: $50x3 $20x1 $100x12
 *
 */
public class Bankomat {
    static int[] denominations;
    static int dispenseAmount;

    public static void main(String[] args) {
        if (!parseArgs(args)) {
            return;
        }

        Map<Integer, Integer> order = new HashMap<>();
        for (int d : denominations) {
            order.put(d, 0);
        }

        AtomicInteger amount = new AtomicInteger(dispenseAmount);
        dispenseRecursive(order, 0, amount);

        System.out.print("Dispensing:");
        for (Map.Entry<Integer, Integer> kv:  order.entrySet()) {
            if (kv.getValue() > 0) {
                System.out.print(" $" + kv.getKey() + "x" + kv.getValue());
            }
        }
        System.out.println();
        // We may not necessarily get full amount in notes. Like trying to get $30 with $20,$50 notes.
        if (amount.get() > 0) {
            System.out.println("Remainder: " + amount.get());
        }
    }

    /**
     * Recursive dispense. Fall back to previous denomination if can't get full amount.
     */
    static void dispenseRecursive(Map<Integer, Integer> order, int level, AtomicInteger amount) {
        if (level < denominations.length) {
            int denomination = denominations[level];
            if (amount.get() % denomination == 0) {
                dispenseRecursive(order, level + 1, amount);
                int notes = amount.get() / denomination;
                addNotes(order, denomination, notes);
                amount.set(amount.get() % denomination);
            } else {
                dispenseRecursive(order, level + 1, amount);
                if (amount.get() % denomination == 0) {
                    addNotes(order, denomination, amount.get() / denomination);
                    amount.set(0);
                } else {
                    int notes = amount.get() / denomination;
                    // Drop one higher denomination note (for example when need $110 do dispense in $20,$50 notes)
                    if (notes > 0 && level > 0 && ((amount.get() % denomination) % denominations[level - 1]) != 0) {
                        notes = notes - 1;
                    }
                    addNotes(order, denomination, notes);
                    amount.set(amount.get()- notes * denomination);
                }
            }
        }
    }

    /**
     * Add notes to an existing order entry
     */
    static void addNotes(Map<Integer,Integer> map, int denomination, int notes) {
        map.put(denomination, map.get(denomination) + notes);
    }

    /**
     * Parse args and help
     */
    static boolean parseArgs(String [] args) {
        if (args.length != 2) {
            printHelp();
            return false;
        }
        try {
            String[] dens = args[0].split(",");
            denominations = new int[dens.length];
            for (int i=0; i<dens.length; i++) {
                denominations[i] = Integer.parseInt(dens[i]);
                if (i>0 && denominations[i] <= denominations[i-1]) {
                    throw new IllegalArgumentException("Denominations must be in ascending order");
                }
            }
            dispenseAmount = Integer.parseInt(args[1]);
            if (dispenseAmount <=0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            printHelp();
        }
        return false;
    }

    static void printHelp() {
        System.out.println("Usage: java Bankmat [Sorted CSV denominations list] [Amount to dispense]");
        System.out.println("Example: java Bankmat 20,50 210");
    }
}
