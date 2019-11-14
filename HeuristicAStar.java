/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heuristicastar;
import java.util.Scanner;
/**
 *
 * @author James
 */
public class HeuristicAStar{
    
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    
    private static final int FLOORCOST = 1;
    private static final int MOATCOST = 20;
    private static final int SWAMPCOST = 5;
    
    private static char[][] map = new char[][]{
            {'F','F','F','F','F','F','F','F','F','F'},
            {'F','W','W','W','W','W','W','W','W','F'},
            {'F','F','F','F','F','F','F','F','F','F'},
            {'T','F','F','F','F','F','F','F','F','T'},
            {'M','M','M','M','M','M','M','M','M','M'},
            {'S','S','S','S','F','F','S','S','S','S'},
            {'S','S','S','S','F','F','S','S','S','S'},
            {'S','S','S','S','F','F','S','S','S','S'},
            {'S','S','S','S','F','F','S','S','S','S'},
            {'S','S','S','S','F','F','S','S','S','S'}};
        
    private static int t1x = 3;
    private static int t1y = 0;
    private static int t2x = 3;
    private static int t2y = 9;
    private static String characterType;
    private static int startX;
    private static int startY;
    private static int goalX;
    private static int goalY;
    private static int fringeCount;
    private static int treeCount;
    private static int heuristictype;
    
    
/**
 * This is the main method you will modify. It takes an (x, y)
 * coordinate and returns the estimated distance to the goal.
 * The default version uses a simple Manhattan distance.
 * Your custom version should 1) be admissible, and 2) perform
 * better in terms of nodes added to the fringe and tree.
 * Note that you can access the goal location using the global
 * variables goalX, goalY.
 * @param x
 * @param y
 * @return 
 */        
    public static double heuristic(int x, int y, int gx, int gy) {    
    
        int manhattan = Math.abs(gx - x) + Math.abs(gy - y);
        if (heuristictype == 0) {
            return manhattan;
        }
        
        // ******* Your heurstic goes here *******
        else {
            //first want to determine if path to goal will go through 
//            a moat (points are on opposite sides of x = 4) = +20
//            a swamp (x > 4 && y < 4 || y > 5)
//            a wall (x < 1 && y != 0 || y != 9)
            
            int heuristic = 0;
            int tileCount = 0; //how many tiles have been accounted for (to have a better chance at gettting close to g(n))
            
            if((x < 4 && gx > 4) || (x > 4 && gx < 4)){  //if either points on opposite sides of moat
               //System.out.println("You must cross the moat.");
               //must cross moat so add 20 to heuristic
               heuristic += MOATCOST;
               tileCount++;
            }
            if ((x > 4 && (y < 4 || y >5))){ //fringe node is in the swamp
               //System.out.println("You are in a swamp");
               //find out how deep in the swamp
               int horzDistance = x -4; //calculate cost of horizaontal swamp exit
               int vertDistance = Math.min(Math.abs(y-4), Math.abs(y-5)); //the shortest distance vertically to either x = 4 or 5
               
               if(horzDistance <= vertDistance){ //if horziontal exit is <= cost of vertical exit
                   //add cost of horizontal exit to heuristic
                   heuristic += horzDistance * SWAMPCOST;
                   tileCount += horzDistance; //keep track of accounted tiles
               }
               else{//vertical exit better
                   heuristic += vertDistance * SWAMPCOST;
                   tileCount += vertDistance;
               }
            }
            if ((gx > 4 && (gy < 4 || gy >5))){//goal node is in a swamp
               //System.out.println("Goal node is in swamp");
               //find out how deep in the swamp
               int horzDistance = gx -4; //calculate cost of horizaontal swamp exit
               int vertDistance = Math.min(Math.abs(gy-4), Math.abs(gy-5)); //the shortest distance vertically to either x = 4 or 5
               
               if(horzDistance <= vertDistance){ //if horziontal exit is <= cost of vertical exit
                   //add cost of horizontal exit to heuristic
                   heuristic += horzDistance * SWAMPCOST;
                   tileCount += horzDistance; //keep track of accounted tiles
               }
               else{//vertical exit better
                   heuristic += vertDistance * SWAMPCOST;
                   tileCount += vertDistance;
               }
            }
            if (x < 1 && (y != 0 || y != 9)){//fringe node is behind the wall
               //System.out.println("Fringe node is behind the wall");
               //find out how deep behind the wall
               int vertDistance = Math.min(Math.abs(y - 0), Math.abs(y - 9)); //the shortest vertical exit distance
               //add cost for traversing to either sides of the wall x = 1 (vert distance + 1 horizontal)
               heuristic += vertDistance + FLOORCOST;
               tileCount += vertDistance;
            }
            if (gx < 1 && (gy != 0 || gy != 9)){//goal node is behind the wall
               //System.out.println("Goal node is behind the wall");
               //find out how deep behind the wall
               int vertDistance = Math.min(Math.abs(gy - 0), Math.abs(gy - 9)); //the shortest vertical exit distance
               //add cost for traversing to either sides of the wall x = 1 (vert distance + 1 horizontal)
               heuristic += vertDistance + FLOORCOST;
               tileCount += vertDistance;
            }
            
            //teleportation tiles
//            a teleportation tile is only considered useful if both y's are outside the range of 3-6
//            because if either y's are within this range it cost less to go straight to the tile than 
//            to the teleportation tile, thus going to the t tile will not be in the shortest path
//            thus if either y < 3 && other y > 6 then a T tile is in the shortest path
            if ((y < 3 && gy > 6) || (y > 6 && gy < 3)){ //current node and goal node are on opposite ends of the map, thus a t tile is useful
               //discover which t tile must be used
                if(y < 3){//if current node is at the top part of the map
                    //must go to tile 3,0
                    int tileDistance = Math.abs(3 - x) + Math.abs(0 - y); //total moves required for t tile
                    int goalDistance = Math.abs(3 - gx) + Math.abs(9 - gy); //total moves from other t tile to goal
                 
                    heuristic += tileDistance + goalDistance; //cost to and from tele tiles
                }
                else{//if current node is at the bottom part of the map
                    int tileDistance = Math.abs(3 - x) + Math.abs(9 - y); //total moves required for t tile
                    int goalDistance = Math.abs(3 - gx) + Math.abs(0 - gy); //total moves from other t tile to goal
                 
                    heuristic += tileDistance + goalDistance; //cost to to and from tele tiles
                }
            }
            
            heuristic = heuristic + (manhattan - tileCount); //account for remaining tiles

            return heuristic;
        }
    }
        
    public static class Tile {            
        private char tileType;
        private int x;
        private int y;
        private int treestate; 
        private int parentX;
        private int parentY;
        private double gDist;
        private boolean inPath;
            
        public Tile() {}

        public double getCost() {
            switch(tileType) {
                case 'F': 
                    return FLOORCOST;
                case 'T':
                    return FLOORCOST;
                case 'W':
                    return  10000.0;
                case 'M':
                    return MOATCOST;
                case 'S':
                    return SWAMPCOST;
                default:
                    return 0.0;
            }
        }
    }
        
    // Returns the tile in the fringe with the lowest f = g + h measure
    public static Tile getBest(Tile[][] graph) {
        double bestH = 100000;
        Tile bestTile = new Tile();
        bestTile.x = -1; // Hack to let caller know that nothing was in fringe (failure)
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (graph[i][j].treestate == 1) {   // Fringe node
                    double h = graph[i][j].gDist + heuristic(i, j, goalX, goalY);
                    if (h < bestH) {
                        bestH = h;
                        bestTile = graph[i][j];
                    }
                }
            }
        }
        return bestTile;
    }
    
        
    // When a node is expanded, we check each adjacent node. This method takes the graph,
    // the coordinates of the parent tile (the one expanded) and its child (the adjacent tile).
    public static void checkAdjacent(Tile[][] graph, int parentX, int parentY, int childX, int childY) {
    
        // If already in tree, exit
        if (graph[childX][childY].treestate == 2) {
            return;
        }
        // If unexplored, add to fringe with path from parent and distance based on
        // distance from start to parent and cost of entering the node.
        if (graph[childX][childY].treestate == 0) {
            graph[childX][childY].treestate = 1;
            graph[childX][childY].gDist = graph[parentX][parentY].gDist + graph[childX][childY].getCost();
            graph[childX][childY].parentX = parentX;
            graph[childX][childY].parentY = parentY;
        
            // Add to stats of nodes added to fringe
            fringeCount++;
            return;
        }       
        // If fringe, reevaluate based on new path
        if (graph[childX][childY].treestate == 1) {
            if (graph[parentX][parentY].gDist + graph[childX][childY].getCost() < graph[childX][childY].gDist) { 
                // Shorter path through parent, so change path and cost.
                graph[childX][childY].gDist = graph[parentX][parentY].gDist + graph[childX][childY].getCost();
                graph[childX][childY].parentX = parentX;
                graph[childX][childY].parentY = parentY;        
            }
            return;
        }
    }
        
    // Once the goal has been found, we need the path found to the goal. This method
    // works backward from the goal through the parents of each tile. It also totals
    // up the cost of the path and returns it.
    public static double finalPath(Tile[][] graph) {
        double cost = 0;
    
        // Start at goal
        int x = goalX;
        int y = goalY;
    
        // Loop until start reached
        while (x != startX || y != startY) {
        
            // Add node to path and add to cost
            graph[x][y].inPath = true;
            cost += graph[x][y].getCost();
        
            // Work backward to parent and continue
            int tempx = graph[x][y].parentX;
            int tempy = graph[x][y].parentY;
            x = tempx;
            y = tempy;
        }
        graph[startX][startY].inPath = true;
        return cost;
    }

    // This method prints the map at the end. Each tile contains the tile type, 
    // its tree status (0=unexplored, 1=fringe, 2=tree), and a * if that tile 
    // was in the final path.
    public static void printGraph(Tile[][] graph) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                System.out.print(graph[j][i].tileType);
                System.out.print(graph[j][i].treestate);
                if (graph[j][i].inPath) {
                    System.out.print("*");
                }
                else {
                    System.out.print(" ");
                }
                System.out.print("  ");
            }
            System.out.print("\n");
        }    
    }

    /** 
     * Get inputs from user
     */
    public static void setProperties() {
        Scanner scanner = new Scanner(System.in);
        
        // Where does character start and end
        System.out.print("X coordinate of start: ");
        startX = scanner.nextInt();
        System.out.print("Y coordinate of start: ");
        startY = scanner.nextInt();
        System.out.print("X coordinate of end: ");
        goalX = scanner.nextInt();
        System.out.print("Y coordinate of end: ");
        goalY = scanner.nextInt();
        
        // What heuristic to use        
        System.out.print("Manhattan (0) or Custom (1) heuristc: ");
        heuristictype = scanner.nextInt();
    }   
        
    public static void makeGraph(Tile[][] graph) { 
        // Construct and initialize the tiles. 
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Tile t = new Tile();
                graph[i][j] = t;
                graph[i][j].tileType = map[i][j];   // Tile type based on above map
                graph[i][j].treestate = 0; // Initially unexplored
                graph[i][j].x = i;         // Each tile knows its location in array
                graph[i][j].y = j;
                graph[i][j].parentX = -1;  // Initially no parents on path fro start
                graph[i][j].parentY = -1;
                graph[i][j].inPath = false;    // Initially not in path from start
            }
        }
    }   
    

    // Main A* search method. Takes graph as parameter, and returns whether search successful
    public static int search(Tile[][] graph) {
        fringeCount = 1;
        boolean goalFound = false;
    
        // Add start state to tree, path cost = 0
        graph[startX][startY].treestate = 1;
        graph[startX][startY].gDist = 0;    
    
        // Loop until goal added to tree
        while (!goalFound) {
        
            // Get the best tile in the fringe
            Tile bestTile = getBest(graph);
            if (bestTile.x == -1) {
                // The default tile was returned, which means the fringe was empty.
                // This means the search has failed.
                System.out.print("Search failed!!!!!!\n");
                printGraph(graph);
                return 0;
            }
        
            // Otherwise, add that best tile to the tree (removing it from fringe)
            int x = bestTile.x;
            int y = bestTile.y;
            graph[x][y].treestate = 2;
            treeCount++;
        
            // If it is a goal, done!
            if (x == goalX && y == goalY) {
                goalFound = true;
                System.out.print("Found the goal!!!!!\n");
            
                // Compute the path taken and its cost, printing the explored graph,
                // the path  cost, and the number of tiles explored (which should be
                // as small as possible!)
                double cost = finalPath(graph);
                printGraph(graph);
                System.out.print("Path cost: " + cost+ "\n");
                System.out.print(treeCount + " tiles added to tree\n");
                System.out.print(fringeCount + " tiles added to fringe\n");
            
                return 1;
            }
        
            // Otherwise, we look at the 4 adjacent tiles to the one just added
            // to the tree (making sure each  is in the graph!) and either add it
            // to the tree or recheck its path.
            
            // Special cases for teleport pads
            if (x == t1x && y == t1y) {
                System.out.println("Teleport checked");
                checkAdjacent(graph, x, y, t2x, t2y);
            }
            if (x == t2x && y == t2y) {
                checkAdjacent(graph, x, y, t1x, t1y);
            }
                
            
            if (x > 0) { // Tile to left
                checkAdjacent(graph, x, y, x-1, y);
            }
            if (x < WIDTH-1) { // Tile to right
                checkAdjacent(graph, x, y, x+1, y);
            }
            if (y > 0) { // Tile above
                checkAdjacent(graph, x, y, x, y-1);
            }
            if (y < HEIGHT-1) { // Tile below
                checkAdjacent(graph, x, y, x, y+1);
            }
        
        }
        return 1;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    
        Tile[][] graph = new Tile[WIDTH][HEIGHT];
        makeGraph(graph);
        setProperties();
        search(graph);
  
    }
    
}