
public class SmallGrid {
    private static final int NUM_STATES = 11;
    private static final int NUM_ACTIONS = 4;
    
    // actions
    private static final int N = 1;
    private static final int E = 2;
    private static final int S = 3;
    private static final int W = 4;
    
    // command line arguments
    private static double DISCOUNT_FACTOR;
    private static double EPSILON = 1e-6;
    private static double POS_TERMINAL_REWARD;
    private static double NEG_TERMINAL_REWARD;
    private static double NON_TERMINAL_REWARD;
    private static double STEP_COST;
    
    // transition and reward functions
    private static double[][][] T = new double[NUM_STATES+1][NUM_ACTIONS+1][NUM_STATES+1];
    private static double[] R = new double[NUM_STATES+1];
    private static int[] policy = new int[NUM_STATES+1];
    
    public static void main (String[] args) {
        if (args.length != 6 ) {
            System.out.println("java SmallGrid discount epsilon posR negR otherR");
            System.out.println("    discount = discount factor (gamma)");
            System.out.println("    epsilon  = maximum utility error");
            System.out.println("    posR     = positive reward in state (4,3)");
            System.out.println("    negR     = negative reward in state (4,2)");
            System.out.println("    otherR   = reward in all other states");
            System.out.println("    stepCost = step cost");
        } else {
            DISCOUNT_FACTOR = Double.parseDouble(args[0]);
            EPSILON = Double.parseDouble(args[1]);
            POS_TERMINAL_REWARD = Double.parseDouble(args[2]);
            NEG_TERMINAL_REWARD = Double.parseDouble(args[3]);
            NON_TERMINAL_REWARD = Double.parseDouble(args[4]);
            STEP_COST = Double.parseDouble((args[5]));
            initMDP();

            valueIteration();
        }
    }
    
    
    // print out the current values and action choices for all states
    public static void printStatus(int iterationCount, double[] utility, int[] policy) {
        System.out.println();
        System.out.println("--------------------------------");
        System.out.println();
        System.out.println("Iteration " + iterationCount);
        System.out.println();
        
        System.out.format("%8.3f", utility[9]);
        System.out.format("%8.3f", utility[7]);
        System.out.format("%8.3f", utility[4]);
        System.out.format("%8.3f\n", utility[1]);

        System.out.print(actionInState(policy[9]));
        System.out.print(actionInState(policy[7]));
        System.out.print(actionInState(policy[4]));
        System.out.print(actionInState(policy[1]));
        System.out.println();
        System.out.println();

        System.out.format("%8.3f", utility[10]);
        System.out.print("   XXXXX");
        System.out.format("%8.3f", utility[5]);
        System.out.format("%8.3f\n", utility[2]);

        System.out.print(actionInState(policy[10]));
        System.out.print("   XXXXX");
        System.out.print(actionInState(policy[5]));
        System.out.print(actionInState(policy[2]));
        System.out.println();
        System.out.println();


        System.out.format("%8.3f", utility[11]);
        System.out.format("%8.3f", utility[8]);
        System.out.format("%8.3f", utility[6]);
        System.out.format("%8.3f\n", utility[3]);

        System.out.print(actionInState(policy[11]));
        System.out.print(actionInState(policy[8]));
        System.out.print(actionInState(policy[6]));
        System.out.print(actionInState(policy[3]));
        System.out.println();

    }


    // return the appropriate string for the prescribed action
    // in a particular state
    public static String actionInState(int action) {
        String prefix = "     ";
        String suffix = "  ";
        switch (action) {
            case N: return prefix + "N" + suffix;
            case E: return prefix + "E" + suffix;
            case S: return prefix + "S" + suffix;
            case W: return prefix + "W" + suffix;
            default: return "    NONE";
        }
    }
    
    
    // Initialize the transition and reward functions.
    //
    // States are numbered as follow:
    //
    //  |-----|-----|-----|-----|
    //  |  9  |  7  |  4  |  1  |
    //  |-----|-----|-----|-----|
    //  | 10  |XXXXX|  5  |  2  |
    //  |-----|-----|-----|-----|
    //  | 11  |  8  |  6  |  3  |
    //  |-----|-----|-----|-----|
    //
    public static void initMDP() {
        // set up reward function;
        // specifies reward you get given the state you're starting out in
        // BEFORE a transition
        for(int s = 1 ; s <= NUM_STATES ; ++s) {
            R[s] = NON_TERMINAL_REWARD;

        }
        
        R[1] = POS_TERMINAL_REWARD;
        R[2] = NEG_TERMINAL_REWARD;
        
        // set up transition function
        // format = T[state][action][state']
        for(int s1 = 1 ; s1 <= NUM_STATES ; ++s1) {
            for(int a = 1 ; a <= NUM_ACTIONS ; ++a) {
                for(int s2 = 1 ; s2 <= NUM_STATES ; ++s2) {
                    T[s1][a][s2] = 0.0;
                }
            }
        }
        
        // set transition probabilities for all actions
        T[3][N][6] = 0.1;
        T[3][N][2] = 0.8;
        T[3][N][3] = 0.1;
        
        T[3][E][2] = 0.1;
        T[3][E][3] = 0.9;
        
        T[3][S][6] = 0.1;
        T[3][S][3] = 0.9;
        
        T[3][W][2] = 0.1;
        T[3][W][6] = 0.8;
        T[3][W][3] = 0.1;
        
        
        
        T[4][N][1] = 0.1;
        T[4][N][4] = 0.8;
        T[4][N][7] = 0.1;
        
        T[4][E][4] = 0.1;
        T[4][E][1] = 0.8;
        T[4][E][5] = 0.1;
        
        T[4][S][1] = 0.1;
        T[4][S][5] = 0.8;
        T[4][S][7] = 0.1;
        
        T[4][W][4] = 0.1;
        T[4][W][7] = 0.8;
        T[4][W][5] = 0.1;
        
        
        
        T[5][N][5] = 0.1;
        T[5][N][4] = 0.8;
        T[5][N][2] = 0.1;
        
        T[5][E][4] = 0.1;
        T[5][E][2] = 0.8;
        T[5][E][6] = 0.1;
        
        T[5][S][2] = 0.1;
        T[5][S][6] = 0.8;
        T[5][S][5] = 0.1;
        
        T[5][W][4] = 0.1;
        T[5][W][5] = 0.8;
        T[5][W][6] = 0.1;
        
        
        
        T[6][N][3] = 0.1;
        T[6][N][5] = 0.8;
        T[6][N][8] = 0.1;
        
        T[6][E][5] = 0.1;
        T[6][E][3] = 0.8;
        T[6][E][6] = 0.1;
        
        T[6][S][3] = 0.1;
        T[6][S][6] = 0.8;
        T[6][S][8] = 0.1;
        
        T[6][W][5] = 0.1;
        T[6][W][8] = 0.8;
        T[6][W][6] = 0.1;
        
        
        
        T[7][N][4] = 0.1;
        T[7][N][7] = 0.8;
        T[7][N][9] = 0.1;
        
        T[7][E][7] = 0.2;
        T[7][E][4] = 0.8;
        
        T[7][S][4] = 0.1;
        T[7][S][7] = 0.8;
        T[7][S][9] = 0.1;
        
        T[7][W][7] = 0.2;
        T[7][W][9] = 0.8;
        
        
        
        T[8][N][11] = 0.1;
        T[8][N][8] = 0.8;
        T[8][N][6] = 0.1;
        
        T[8][E][8] = 0.2;
        T[8][E][6] = 0.8;
        
        T[8][S][11] = 0.1;
        T[8][S][8] = 0.8;
        T[8][S][6] = 0.1;
        
        T[8][W][8] = 0.2;
        T[8][W][11] = 0.8;
        
        
        
        T[9][N][7] = 0.1;
        T[9][N][9] = 0.9;
        
        T[9][E][9] = 0.1;
        T[9][E][7] = 0.8;
        T[9][E][10] = 0.1;
        
        T[9][S][7] = 0.1;
        T[9][S][10] = 0.8;
        T[9][S][9] = 0.1;
        
        T[9][W][10] = 0.1;
        T[9][W][9] = 0.9;
        
        
        
        T[10][N][10] = 0.2;
        T[10][N][9] = 0.8;
        
        T[10][E][9] = 0.1;
        T[10][E][10] = 0.8;
        T[10][E][11] = 0.1;
        
        T[10][S][10] = 0.2;
        T[10][S][11] = 0.8;
        
        T[10][W][9] = 0.1;
        T[10][W][10] = 0.8;
        T[10][W][11] = 0.1;
        
        
        
        T[11][N][11] = 0.1;
        T[11][N][10] = 0.8;
        T[11][N][8] = 0.1;
        
        T[11][E][10] = 0.1;
        T[11][E][8] = 0.8;
        T[11][E][11] = 0.1;
        
        T[11][S][8] = 0.1;
        T[11][S][11] = 0.9;
        
        T[11][W][10] = 0.1;
        T[11][W][11] = 0.9;
        
    }


    public static void valueIteration() {
        for(int i=0; i < 10; ++i) {
            for(int s=3; s<=NUM_STATES; ++s) {
                double maxActionUtility = -Double.MAX_VALUE;
                int bestPolicy = -1; 
                for(int a=1; a <= NUM_ACTIONS; ++a) {
                    double u = STEP_COST;
                    for (int p=1; p <= NUM_STATES; ++p) {
                        double probability = T[s][a][p];
                        if (probability > 0) {
                            u += probability * R[p] * DISCOUNT_FACTOR;
                        }
                    }
                    if (u > maxActionUtility) {
                        maxActionUtility = u;
                        bestPolicy = a;
                    }
                }
                /*
                if (Math.abs(R[s] - maxActionUtility) > EPSILON) {
                    
                }
                */

                R[s] += maxActionUtility;
                policy[s] = bestPolicy;
            }

            printStatus(i, R, policy);

        }
        
        
        /*
        for (each state S)
            find best action
            for each action a
            for each state s'
                sum s'
        */

        /*
        double maxUChange = 0;//to store maximum value change
        //iterate through every state
        for (State state : States) {
            double bestDirVal = -Double.MAX_VALUE;//so that it definitely gets updated
            dir bestDir = null;
            //iterate through each possible action
            for (dir direction: dir.values()) {
                //get each neighbor reachable through current action
                State direct = state.getNeighbor(direction);
                State counter = state.getNeighbor(direction.counter());
                State clockwise = state.getNeighbor(direction.clockwise());
                //calculate total utility of each of those neighbors
                double utility = (transition(state, direction, direct) * direct.getValue());
                //make sure counter neighbor hasn't already been checked
                if (!direct.equals(counter)) {
                utility += (transition(state, direction, counter) * counter.getValue());
                }
                //make sure clockwise neighbor hasn't already been checked
                if (!direct.equals(clockwise) && !counter.equals(clockwise)) { 
                utility += (transition(state, direction, clockwise) * clockwise.getValue());
                }
                //if one of the possible states is 40, then the key loss prob means 41 is also possible
                if (direct.equals(States[40]) || counter.equals(States[40]) || clockwise.equals(States[40])) {
                    utility += (transition(state, direction, States[41]) * States[41].getValue());
                }
                //if this is the best direction update the current direction and value
                if (utility > bestDirVal) {
                    bestDirVal = utility;
                    bestDir = direction;
                }
            }
            //calculate the actual new utility and change in utility
            double newVal = state.getReward() + (Discount_Factor * bestDirVal);
            double uChange = Math.abs(newVal - state.getValue());
            //if change in utility is the largest yet set new max
            if (uChange > maxUChange) maxUChange = uChange;
            //update state value and optimal move
            state.setValue(newVal);
            state.setOptMove(bestDir);
        }
        //return the maximum change in utility
        return maxUChange;
        */
    }

} 