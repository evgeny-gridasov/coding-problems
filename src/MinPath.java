import java.security.SecureRandom;

/**
 * Find a best spot for a well on a map with houses and trees:
 * - May walk up/down/left/right
 * - May walk through cells with houses
 * - Must not walk through cells with trees
 * - A well must be placed in an empty cell (i.e. not where a house or a tree is)
 * - Sum of all distances from every house to well must be minimal
 *
 * This exercise is to practice algorithms, not Java style/correctness.
 * Usage: java MinPath WIDTHxHEIGHT HOUSES TREES
 *
 * Typical result for a map 40x25, 8 houses and 128 trees:
 * Run: java MinPath 40x25 8 128
 *
 *       ...........tt...................#H....t.
 *       ..tt...........t.t......t.t....t#t...tt.
 *       ...t.t.......tt...t....t........#..H....
 *       .....t.tt.t........t...tt.......#.##....
 *       ..............t........t....t...#.#t....
 *       .......HHt..................t...#.#.....
 *       .......##t.t....................#t#.....
 *       .....t.####t......t.............#.#.t..t
 *       .....t...t#...t..tt......t......#.#..t..
 *       ...t.....t#.tt...t...t........t.###.....
 *       ........t.#.t....t.t...t....tt..##t.....
 *       .........t#............t....t...##......
 *       .........t################O#######......
 *       .....t......t.#........t..#...#tH.....t.
 *       ..............#...........#...##t.t.....
 *       .tt...........#...........#....H.......t
 *       ...........t..#...........#.............
 *       ..t.....tt...t#...........#..........tt.
 *       .............##..ttt......#.t.t......t..
 *       ....t........#.......tt...H........t....
 *       ............t#t..t.............tt......t
 *       ......t....t##.............t..........t.
 *       .....t.....H#............t..t..........t
 *       ..t....t....t......t.......t......t.t...
 *       ...................t.....tt............t
 *
 */
public class MinPath {
    static final char HOUSE = 'H';
    static final char TREE = 't';
    static final char EMPTY = '.';
    //static final char EMPTY = '\u00b7'; // middle dot
    static final char WELL = 'O';
    static final char PATH = '#';
    //static final char PATH = '\u2588'; // large square

    static int mapsizeH;
    static int mapsizeW;
    static int houses;
    static int trees;
    static Cell [][] map;
    static int minDistance = Integer.MAX_VALUE;
    static int wellX = -1;
    static int wellY = -1;

    public static void main(String[] args) {
        if (!parseArgs(args)) {
            return;
        }

        genMap();

        // Dijkstrify for every empty cell and get distance
        for (int i=0; i<mapsizeH; i++) {
            for (int j=0; j<mapsizeW; j++) {
                if (map[i][j].type == EMPTY) {
                    dijkstrify(i, j, 0);
                    int t = totalDistance();
                    if (t < minDistance) {
                        minDistance = t;
                        wellY = i;
                        wellX = j;
                    }
                    reset();
                }
            }
        }

        if (wellX >=0) {
            // place a well
            map[wellY][wellX].type = WELL;

            // Dijkstrify from the good spot to help draw paths back
            dijkstrify(wellY, wellX, 0);
            drawPaths();
        } else {
            System.out.println("Could not place a well. Too many trees?");
        }

        printMap(false);
    }

    /**
     * Generates a random map
     */
    static void genMap() {
        map = new Cell[mapsizeH][mapsizeW];
        for (int i=0; i<mapsizeH; i++) {
            for (int j=0; j<mapsizeW; j++) {
                map[i][j] = new Cell();
            }
        }

        // I'm serious about my random numbers
        SecureRandom instanceStrong;
        try {
             instanceStrong = new SecureRandom();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        // Trees
        for (int i=0; i<trees; i++) {
            map[instanceStrong.nextInt(mapsizeH)][instanceStrong.nextInt(mapsizeW)].type = TREE;
        }

        // Houses
        for (int i=0; i<houses; i++) {
            map[instanceStrong.nextInt(mapsizeH)][instanceStrong.nextInt(mapsizeW)].type = HOUSE;
        }
    }

    /**
     * Find distances, bail out when we hit a tree or map boundary
     */
    static void dijkstrify(int y, int x, int distance) {
        if (y<0 || y>=mapsizeH || x<0 || x>=mapsizeW) return;
        if (map[y][x].type == TREE) return;
        if (map[y][x].distance > distance) {
            map[y][x].distance = distance;
            dijkstrify(y-1, x, distance + 1);
            dijkstrify(y+1, x, distance + 1);
            dijkstrify(y, x-1, distance + 1);
            dijkstrify(y, x+1, distance + 1);
        }
        return;
    }

    /**
     * Calculate total distance from every house to the well
     */
    static int totalDistance() {
        int d = 0;
        for (int i=0; i<mapsizeH; i++) {
            for (int j=0; j<mapsizeW; j++) {
                if (map[i][j].type == HOUSE) {
                    int distance = map[i][j].distance;
                    if (distance == Integer.MAX_VALUE) {
                        // means we tried to put a well or a house in a forest, i.e. fully surrounded by trees
                        return Integer.MAX_VALUE;
                    }
                    d = d + distance;
                }
            }
        }
        if (d <= 0) {
            throw new IllegalStateException("Total distance is wrong");
        }
        return d;
    }

    /**
     * Resets the map distance back to Integer.MAX_VALUE
     */
    static void reset() {
        for (int i=0; i<mapsizeH; i++) {
            for (int j=0; j<mapsizeW; j++) {
                map[i][j].distance = Integer.MAX_VALUE;
            }
        }
    }

    /**
     * Prints a map optionally showing distance
     */
    static void printMap(boolean printDistance) {
        for (int i=0; i<mapsizeH; i++) {
            StringBuilder sb = new StringBuilder(mapsizeW);
            for (int j=0; j<mapsizeW; j++) {
                sb.append(map[i][j].type);
                if (printDistance) {
                    if (map[i][j].distance < Integer.MAX_VALUE) {
                        sb.append(String.format("(%02d)", map[i][j].distance));
                    } else {
                        sb.append("(XX)");
                    }
                }
            }
            System.out.println(sb.toString());
        }
    }

    /**
     * Entry function to draw paths from every house back to the well
     */
    static void drawPaths() {
        for (int i=0; i<mapsizeH; i++) {
            for (int j=0; j<mapsizeW; j++) {
                if (map[i][j].type == HOUSE) {
                    if (!findWell(i, j, map[i][j].distance)) {
                        throw new IllegalStateException("Can't find the well");
                    }
                }
            }
        }
    }

    /**
     * Recursive call to walk back to the well over decreasing distances
     */
    static boolean findWell(int y, int x, int distance) {
        if (y<0 || y>=mapsizeH || x<0 || x>=mapsizeW) return false;
        if (map[y][x].distance == 0) return true;
        if (map[y][x].distance > distance) return false;
        if (map[y][x].type == EMPTY) {
            map[y][x].type = PATH;
        }
        if (!findWell(y-1, x, distance - 1)) {
            if (!findWell(y+1, x, distance - 1)) {
                if (!findWell(y, x+1, distance - 1)) {
                    return findWell(y, x-1, distance - 1);
                }
            }
        }
        return true;
    }

    /**
     * Parse args: mapsize, number of houses, number of trees.
     */
    static boolean parseArgs(String[] args) {
        if (args.length != 3) {
            printHelp();
            return false;
        }
        try {
            mapsizeW = Integer.parseInt(args[0].split("x")[0]);
            mapsizeH = Integer.parseInt(args[0].split("x")[1]);
            houses = Integer.parseInt(args[1]);
            trees = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.out.println(e.toString());
            printHelp();
            return false;
        }
        return true;
    }

    static void printHelp() {
        System.out.println("Usage: java MinPath WIDTHxHEIGHT HOUSES TREES");
        System.out.println("Example: java MinPath 10x5 4 3");
    }

    /**
     * A class representing a cell of our map
     */
    static class Cell {
        public int distance = Integer.MAX_VALUE;
        public char type = EMPTY;
    }
}
