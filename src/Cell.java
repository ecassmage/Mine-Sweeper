
/**
 * This is for the Individual Cells of the Minesweeper Game.
 * @author Evan Morrison
 * @version 1.0
 * @since 1.0
 */
public class Cell {

    /**
     * A non changeable variable during run-time, for the starting character
     */
    static String stateGlobal = "?";  // This can be changed to anything you want if you want to. This is the Symbol for the Cells starting State.

    /**
     * Current State
     * <p>
     *     Is this cell a Mine or is it nothing.
     * </p>
     */
    private String state;

    /**
     * What is visually being shown at the moment
     * <p>
     *     This will show what the User will see on screen whether it be a ?, a number, an F or an M.
     * </p>
     */
    private String VisualState;

    /**
     * Is this a Mine or Not.
     */
    private boolean isMine;

    /**
     * Has this been flipped to reveal what it is Yet
     */
    private boolean Flipped;

    /**
     * Number of mines surrounding this tile.
     */
    private int numberOfSurroundingMines = 0;

    /**
     * An Array of all the Cells surrounding This Cell
     */
    private final Cell[] Surroundings = new Cell[8];  // Starts at North and rotates Clockwise

    /**
     * Main Program, for testing stuffs.
     * @param args args args args
     */
    public static void main(String[] args){
        // Nothing Shall Happen Muwhahahahahaahaahahaahhahahahaha
    }

    /**
     * Constructor for The Cells. Gives them their Starting state.
     */
    public Cell(){
        this.state = " ";
        this.VisualState = stateGlobal;
        this.isMine = false;
        this.Flipped = false;
    }

    /**
     * Changes the State of this Cell.
     * @param state What is the new State of This Cell going to be?
     */
    public void ChangeCellState(String state){
        this.state = state;
        if (state.equals("M")) this.isMine = true;
        else this.isMine = false;
    }

    /**
     * Checks how many mines Surround this Cell.
     * <p>
     *     If this number has already been calculated
     * </p>
     * @return returns the number of Mines
     */
    private int numberOfMines(){
        // Dunno Why I did it this way But Ta Da!!!
        // try { // This was just some Debugging to make sure it wasn't still Erroring
        if (numberOfSurroundingMines == 0) {
            for (int i = 0; i < 8; i++) {
                if (Surroundings[i] != null && Surroundings[i].isMine) {
                    numberOfSurroundingMines++;
                }
            }
        }
        // }
        //catch (Exception all){
            //System.out.println("We Failed");
        // }
        return numberOfSurroundingMines;
    }

    /**
     * For Flipping a Cell.
     * <p>
     *     just makes sure that a Mine isn't replace with a Number.
     * </p>
     */
    private void chooseState(){
        if (!isMine) {
            VisualState = Integer.toString(numberOfMines());
            Flipped = true;
        }
        // else will basically just keep its standard symbol default being ?
    }

    /**
     * Flips Cells to Show what is underneath them
     * @param event Check if Cell is Being Selected or being Flagged. Left Click or Right Click
     * @param Recursion Makes sure that Recursive Runs of this don't do certain actions.
     */
    public void changeVisibleState(int event, boolean Recursion){
        // 0 means the Cell was selected
        // 1 means the Cell was Flagged
        Flipped = true;
        if (event == 0) {
            if (!Recursion) {
                VisualState = state;
            }
            for (int i = 0; i < 8; i++){
                if (Surroundings[i] != null){
                    if (Surroundings[i].numberOfMines() == 0 && !Surroundings[i].getMine()){
                        Surroundings[i].VisualState = " ";
                        if (!Surroundings[i].Flipped) Surroundings[i].changeVisibleState(0, true);
                    }
                    else {
                        Surroundings[i].chooseState();
                    }
                }
            }
        }
        else if (event == 1) VisualState = "F";
        else System.out.println("Sorry but this is not a valid event. What are you Doing Programmer for making it possible to get this???");
    }

    /**
     * Adds Surrounding Cells to the Attribute {@link #Surroundings}
     * <p>
     *     Adds Surrounding Cells to the Attribute {@link #Surroundings} while also checking that a none existent Cell isn't accidentally added or something not possible like that.
     * </p>
     * @param board Game Board for Current Minesweeper Round
     * @param x x Coordinate on Board
     * @param y y Coordinate on Board
     */
    public void addSurroundingCell(Cell[][] board, int x, int y){
        int c = 0;
        for (int i = -1; i <= 1; i++){
            for (int j = -1; j <= 1; j++){
                if (i == 0 && j == 0){
                    continue;
                }
                try {
                    Surroundings[c] = board[x + i][y + j];
                }
                catch (Exception ArrayIndexOutOfBoundsException){
                    Surroundings[c] = null;
                }
                c++;
            }
        }
    }


    /**
     * This fetches the State of the Cell
     * @return returns state of the Cell
     */
    public String getState(){return state;}

    /**
     * This fetches the boolean for if this Cell is a Mine or Not
     * @return returns true if Cell is a Mine and false if not
     */
    public boolean getMine(){return isMine;}

    /**
     * This fetches the Visual State of the Cell
     * @return returns Visual State of the Cell
     */
    public String getVisibleState(){return VisualState;}

    /**
     * This flips the Cell should it not already be flipped
     */
    public void flipCell(){if (!Flipped) changeVisibleState(0, false);}

    /**
     * This fetches the State of the Cell
     * @return returns true if Cell is Flipped and false if Not Flipped.
     */
    public boolean getFlippedState(){return Flipped;}
}
