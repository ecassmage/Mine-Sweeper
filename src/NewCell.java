
/**
 * This operates the Cells of the Board
 * <p>
 *     This class Operates each of the Cells that is on the minesweeper board.
 *     It holds the states of the Cells.
 * </p>
 * @author Evan Morrison
 * @version 1.00.1
 * @since 1.00.0
 */
public class NewCell {
    /**
     *
     */
    private static final String[] standardSymbols = {"?", " ", "F", "M"};

    private String state;
    private String visualState;
    private int numberOfSurroundingMines;

    private boolean isThisAMine;
    private boolean flipped;
    private boolean banOnMines;

    private final NewCell[] Surroundings = new NewCell[8];  // <Mainly Useful in flipping all surrounding cells which are empty

    /**
     * returns a String with a copy of a cells attribute information
     * @return returns a String with a copy of a cells attribute information
     */
    public String printCellObject(){
        String CellString =
                "Mine: " + getMine() +
                        "\nState: (" + getState() + ")" +
                        "\nVisual State: (" + getVisibleState() + ")" +
                        "\nNumber of Surrounding Mines: " + getNumberOfSurroundingMines() +
                        "\nFlipped State: " + getFlippedState() +
                        "\nIs Mine Banned: " + getMineBan() +
                        "\nSurroundings: ";
        for (int i = 0; i < 8; i++){
            if (Surroundings[i] == null)
                CellString += "null, ";
            else
                CellString += Surroundings[i].getClass() + ", ";
        }
        CellString += "\n";
        return CellString;
    }

    /**
     * Mainly Things like arging everything
     * @param args Args
     */
    public static void main(String[] args){
        NewCell testCell = new NewCell();
        System.out.println(testCell.getMine());
        System.out.println(testCell.getFlippedState());
        System.out.println(testCell.getNumberOfSurroundingMines());
        System.out.println(testCell.getVisibleState());
        System.out.println(testCell.getClass() + "\n");
        testCell.changeStateOfCell(true);
        System.out.println(testCell.getMine());
        System.out.println(testCell.getFlippedState());
        System.out.println(testCell.getNumberOfSurroundingMines());
        System.out.println(testCell.getVisibleState());
        System.out.println(testCell.getClass());
    }

    /**
     * Constructs a NewCell
     */
    public NewCell(){
        //Constructor
        this.state = standardSymbols[1];
        this.visualState = standardSymbols[0];
        this.isThisAMine = false;
        this.flipped = false;
        this.banOnMines = false;
    }

    /**
     * sets the state of the cell
     * <p>
     *     This will only allow for the choice between a mine and not a mine. and is not Visually Changing by itself.
     * </p>
     * @param isMine is this Cell going to be a Mine
     */
    public void changeStateOfCell(boolean isMine){
        //Mutator
        if (isMine) state = standardSymbols[3];
        else state = standardSymbols[1];
        isThisAMine = isMine;
    }

    /**
     * Searches a Locates the 8 surrounding Cells.
     * <p>
     *     This will go through the 8 surrounding Cells and add them to an array of Cells.
     *     If a location does not have a cell located there, this will add a null in its place.
     * </p>
     * @param board needs a 2 dimensional array of Cells.
     * @param x Needs an int coordinate for which row is being looked at
     * @param y Needs an int coordinate for which column is being looked at
     */
    public void findSurroundingCells(NewCell[][] board, int x, int y){
        //Mutator
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
     * Counts and returns the surrounding cells which are mined
     * @param forceRecount Forces a recount. Use if this Cells Number of Mines might be incorrect
     * @return returns number of surrounding mines.
     */
    public int countSurroundingMines(boolean forceRecount){
        if (forceRecount) numberOfSurroundingMines = 0;
        if (!getMine() && numberOfSurroundingMines == 0) {
            for (int i = 0; i < 8; i++) {
                if (Surroundings[i] != null && Surroundings[i].getMine()) {
                    numberOfSurroundingMines++;
                }
            }
        }
        return numberOfSurroundingMines;
    }

    /**
     * Changes what the user sees for this cell.
     * @param event event 0 is for a LeftClick, event 1 is for a RightClick, event 2 is for Winning
     * @param Recursion For flipping over adjacent cells as well.
     */
    public void changeVisibleState(int event, boolean Recursion){
        // 0 means the Cell was selected
        // 1 means the Cell was Flagged
        if (event == 0) flipped = true;

        if(event == 0){
            if (!Recursion) changeVisualState(state);
            for (int i = 0; i < 8; i++){
                if (Surroundings[i] != null) {
                    if (Surroundings[i].getNumberOfSurroundingMines() == 0 && !Surroundings[i].getMine()){
                        Surroundings[i].changeVisualState(standardSymbols[1]);
                        if (!Surroundings[i].flipped) Surroundings[i].changeVisibleState(0, true);
                    }
                    else if (!Surroundings[i].isThisAMine){
                        Surroundings[i].changeVisualState(Integer.toString(Surroundings[i].getNumberOfSurroundingMines()));
                        Surroundings[i].flipped = true;
                    }
                    // else will basically just keep its standard symbol default being ?
                }
            }
        }
        else if (event == 1) changeVisualState(standardSymbols[2]);
        else if (event == 2) changeVisualState(getState());
        else System.out.println("Sorry but this is not a valid event. What are you Doing Programmer for making it possible to get this???");

    }

    /**
     * Modifies the visualState attribute.
     * <p>
     *     This will print out a message with information if a 0 gets passed. This should only happen if it breaks so it can be mostly ignored.
     * </p>
     * @param newVisual will send the new symbol for the Cell.
     */
    public void changeVisualState(String newVisual) {
        if (newVisual.equals("0")) System.out.println("We Got Called: " + newVisual + "\n" + printCellObject());
        visualState = newVisual;
    }

    /**
     * This is meant to ban new mines from being placed near the first uncovered cell
     */
    public void banCellToMineStatus(){banOnMines = true;}

    /**
     * Uncovers the cell
     * <p>
     *     EXPLODE (private method) will call this to uncover the board should a mine be uncovered.
     *     0 is for a Lost game so uncover all uncovered Cells
     *     2 is for a Won game, so Uncover the State of all cells on the Board.
     * </p>
     * @param event send either 0 or 2
     */
    public void flipUnFlippedCells(int event){
        if (event == 0 && !flipped) changeVisibleState(event,false);
        else if (event == 2) changeVisibleState(event,false);
    }

    /**
     * Gets the states
     * @return returns a String which holds the state
     */
    public String getState(){return state;}

    /**
     * gets number of surrounding mines
     * @return returns the number of surrounding mines
     */
    public int getNumberOfSurroundingMines(){return countSurroundingMines(false);}

    /**
     * gets mine state.
     * <p>
     *     will return true if this cell is a mine and false if this is not a mine
     * </p>
     * @return returns true if mine and false if not mine
     */
    public boolean getMine(){return isThisAMine;}

    /**
     * gets visible state of cell
     * @return returns String witch hold visible state
     */
    public String getVisibleState(){return visualState;}

    /**
     * gets state of this cell being uncovered or not
     * @return returns true if cell uncovered, returns false if not uncovered
     */
    public boolean getFlippedState(){return flipped;}

    /**
     * gets corresponding surrounding cell with num
     * <p>
     *     will return the cell in the Surrounding array which is in the num index position.
     * </p>
     * @param num int between 0 and 7 for returning a surrounding cell
     * @return returns Cell in Surrounding[num] location
     */
    public NewCell getSurroundingCell(int num){return Surroundings[num];}

    /**
     * gets boolean value for if Cell is banned from being a mine
     * @return true if not banned from being mined else returns false.
     */
    public boolean getMineBan(){return banOnMines;}
}
