import java.util.Random;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * This is the Board of the Minesweeper Game.
 * @author Evan Morrison
 * @version 1.0
 * @since 1.0
 */
public class Board {
    /**
     *
     */
    static boolean dev = true;  // Shows a copy with locations of all mines when set to true. For testing purposes only.
    public boolean scriptedGame = true;

    private final int[] proportions = new int[2];
    private int mines;
    private Cell[][] board;

    private boolean firstMove = true;
    private boolean IsGameOver = false;

    /**
     * Constructs a Board.
     * <p>
     * This Constructor mainly deals with starting the game up, giving it settings. It uses {@link #setupGame()} to manage these settings for it.
     * </p>
     */
    public Board(){
        //setupGame();
    }

    /**
     * Main Program, for testing stuffs.
     * @param args Dunno. Just comes with Java Sooooooo.
     */
    public static void main(String[] args){
        Board board = new Board();
        board.boardGenerate();
        board.mainGameRoutine();
    }

    /**
     * This is to start playing the game. To call from another Class.
     */
    public void playMineSweeper(){
        System.out.println(
                "**** Welcome to Minesweeper Game ***\n" +
                "************************************"
        );
        setupGame();
        while (true){
            boardGenerate();
            mainGameRoutine();
            if (!PlayAgain()){
                Quit();
            }
            setupGame();
        }
    }


    public void printScriptBrd(boolean cheatBrd){
        System.out.print("     ");
        for (int NumbersAtTop = 0; NumbersAtTop < proportions[1]; NumbersAtTop++){
            System.out.print(NumbersAtTop + 1 + "   ");
        }
        System.out.print("\n   -");
        for (int NumbersAtTop = 0; NumbersAtTop < proportions[1]; NumbersAtTop++){
            System.out.print("----");
        }
        System.out.print("\n1  ");
        for (int i = 0; i < proportions[0]; i++){
            for (int j = 0; j < proportions[1]; j++){
                printCell(cheatBrd, i, j); // Calls new Method to print Cell
            }
            System.out.print("|\n");
            if (i+1 < proportions[0]){
                System.out.print((i + 2) + "  ");
            }
        }
        System.out.print("   -");
        for (int NumbersAtTop = 0; NumbersAtTop < proportions[1]; NumbersAtTop++){
            System.out.print("----");
        }
        System.out.print("\n");
    }

    /**
     * This method draws the board to the console.
     * @param cheatBrd This will set off a setting to turn on a hidden mode in the game. Shows where all the mines are located instead of the actual board.
     */
    public void printBrd(boolean cheatBrd){
        if (scriptedGame){
            printScriptBrd(cheatBrd);
            return;
        }
        System.out.print("\t ");
        for (int NumbersAtTop = 0; NumbersAtTop < proportions[1]; NumbersAtTop++){
            System.out.print(NumbersAtTop + 1 + "\t ");
        }
        System.out.print("\n \t");
        for (int NumbersAtTop = 0; NumbersAtTop < proportions[1]; NumbersAtTop++){
            System.out.print("----");
        }
        System.out.print("\n1\t");
        for (int i = 0; i < proportions[0]; i++){
            for (int j = 0; j < proportions[1]; j++){
                printCell(cheatBrd, i, j); // Calls new Method to print Cell
            }
            System.out.print("\n");
            if (i+1 < proportions[0]){
                System.out.print(i + 2 + "\t");
            }
        }
        System.out.print("\t");
        for (int NumbersAtTop = 0; NumbersAtTop < proportions[1]; NumbersAtTop++){
            System.out.print("----");
        }
        System.out.print("\n\n");
    }

    /**
     * Quickly Writes the Cell to the Console
     *
     * @param cheatBrd This will set off a setting to turn on a hidden mode in the game. Shows where all the mines are located instead of the actual board.
     * @param i this is just receiving the i value from the {@link #printBrd(boolean)}
     * @param j this is just receiving the j value from the {@link #printBrd(boolean)}
     */
    private void printCell(boolean cheatBrd, int i, int j) {
        if (scriptedGame){
            if (cheatBrd) System.out.print("| " + board[i][j].getState() + " ");
            else System.out.print("| " + board[i][j].getVisibleState() + " ");
        }
        else{
            if (cheatBrd) System.out.print("|" + board[i][j].getState() + "\t");
            else System.out.print("|" + board[i][j].getVisibleState() + "\t");
        }

    }

    /**
     * Makes a new Board
     * <p>
     * Makes a new Board based on the specifications it was given at already.
     * Needs {@link #proportions} to work Properly as well as for {@link Cell} for this to work properly
     * </p>
     */
    public void mkBrd(){
        board = new Cell[proportions[0]][proportions[1]];
        for (int i = 0; i < proportions[0]; i++){
            for (int j = 0; j < proportions[1]; j++){
                board[i][j] = new Cell();
            }
        }
    }

    /**
     * This Goes through all the Cells on the board
     * <p>
     *     This Goes throgh all the cells on the board where it will then call {@link Cell#addSurroundingCell(Cell[][], int, int)}
     * </p>
     */
    private void connectSurroundingCells(){
        for (int i = 0; i < proportions[0]; i++){
            for (int j = 0; j < proportions[1]; j++){
                board[i][j].addSurroundingCell(board, i, j);
            }
        }
    }

    /**
     * Generates a Board for the Game to played
     * <p>
     *     This calls {@link #connectSurroundingCells()} to locate the boards surroundings. Sort of like a large tree of sorts.
     * </p>
     */
    private void boardGenerate(){
        mkBrd();  // Makes a blank board to be filled
        Random randomNumber = new Random();
        LocalTime myObj = LocalTime.now();
        randomNumber.setSeed(myObj.getNano());
        int placedMines = 0;
        int newX;
        int newY;
        while (placedMines < mines){
            newX = randomNumber.nextInt(proportions[0]);
            newY = randomNumber.nextInt(proportions[1]);
            if (!board[newX][newY].getState().equals("M")){
                board[newX][newY].ChangeCellState("M");
                placedMines += 1;
            }
        }
        connectSurroundingCells();
    }

    /**
     * Sets up a new instance of the game for playing
     * <p>
     *     This sets up the game by asking the player to input some information about the game they want to
     *     play like how large of a board do you want to play on and how many mines are wanted to be used
     *     Uses {@link Scanner} to collect what the User inputs.
     * </p>
     */
    public void setupGame(){

        IsGameOver = false;
        firstMove = true;
        // Sorry if this doesn't stop you from going past 20 on the x and y. I like scale a little too much so I wanted it to go to infinity.
        // Also do I really have to have pre-Made boards cause That seems not like a game.
        while (true) {
            System.out.print("What are your Settings (Should be structured (x y mines) No Brackets): ");
            Scanner newScanner = new Scanner(System.in);
            proportions[0] = newScanner.nextInt();
            proportions[1] = newScanner.nextInt();
            mines = newScanner.nextInt();
            if (proportions[0] * proportions[1] < mines + 11) {
                System.out.println("Sorry but the board is too small for this number of mines");
            } else if (proportions[0] == 0 || proportions[1] == 0 || mines == 0) {
                System.out.println("Sorry but the 0 is not acceptable for any of these inputs");
            } else {
                System.out.println("Thank You for the Inputs");
                break;
            }
        }
    }

    /**
     * Manages Game Mechanics if first Move.
     * <p>
     *     Modifies the board so that you can not fail on your first move. It will then move the mine should one need to be moved to a new location to keep the difficulty at where it was before.
     * </p>
     * @param Coords Holds coordinates on the board of what was just clicked
     */
    private void isFirstMove(int[] Coords){
        firstMove = false;
        if (!board[Coords[0]][Coords[1]].getMine()) return;
        Random randomNumber = new Random();
        LocalTime myObj = LocalTime.now();
        randomNumber.setSeed(myObj.getNano());
        int newX;
        int newY;
        while (true){
            newX = randomNumber.nextInt(proportions[0]);
            newY = randomNumber.nextInt(proportions[1]);
            if (!board[newX][newY].getState().equals("M")){
                board[newX][newY].ChangeCellState("M");
                break;
            }
        }
        board[newX][newY].addSurroundingCell(board, newX, newY);
        board[Coords[0]][Coords[1]].ChangeCellState(" ");
    }

    /**
     * Manages Mechanics of Left Clicking
     * @param Coords Holds coordinates on the board of what was just clicked
     */
    private void LeftClick(int[] Coords){
        if (board[Coords[0]][Coords[1]].getFlippedState()) return;
        if (firstMove) isFirstMove(Coords);
        if (board[Coords[0]][Coords[1]].getMine()) EXPLODE();
        else board[Coords[0]][Coords[1]].changeVisibleState(0, false);
    }

    /**
     * Manages Mechanics of Right Clicking
     * @param Coords Holds coordinates on the board of what was just clicked
     */
    private void RightClick(int[] Coords){
        if (board[Coords[0]][Coords[1]].getFlippedState()) return;
        board[Coords[0]][Coords[1]].changeVisibleState(1, false);
    }

    /**
     * Manages the Main Game Routines
     * <p>
     *     Manages the Main Game Routines like collecting next move or Exiting the game prematurely.
     *     This will also make sure a new board is printed after every move made.
     *     Uses {@link Scanner} to collect User input, uses {@link #LeftClick(int[])} and {@link #RightClick(int[])} To manage the Mouse Clicks it receives.
     * </p>
     */
    public void mainGameRoutine(){
        int[] coords = {0, 0};
        String MouseClick;
        printBrd(false);
        if (dev) printBrd(true);
        while (!IsGameOver) {
            do {
                Scanner newScanner = new Scanner(System.in);
                System.out.print("Enter Square Location: Row (1:" + proportions[0] + "), Col (1:" + proportions[1] + "), and L)eft or R)ight (enter 0 0 0 to Exit): ");
                coords[1] = newScanner.nextInt();
                coords[0] = newScanner.nextInt();
                MouseClick = newScanner.nextLine();
                if (coords[0] == 0 && coords[1] == 0 & MouseClick.equals(" 0")) {
                    Quit();
                }

            } while (0 >= coords[0] || coords[0] > proportions[0] || 0 >= coords[1] || coords[1] > proportions[1]);
            coords[1]--;
            coords[0]--;
            if (MouseClick.equals(" L") || MouseClick.equals(" l")) LeftClick(coords);
            else RightClick(coords);
            printBrd(false);
            if (dev) printBrd(true);
            if (checkVictory() && !IsGameOver){
                Winner();
            }
        }
    }

    /**
     * Checks if User has won or not
     * @return returns false if not won and true if won
     */
    private boolean checkVictory() {
        for (int i = 0; i < proportions[0]; i++) {
            for (int j = 0; j < proportions[1]; j++) {
                if (!board[i][j].getMine() && !board[i][j].getFlippedState()){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Writes a Victory Message to Console
     */
    private void Winner(){
        System.out.println("Yay, We have ourselves a winner.");
        IsGameOver = true;
    }

    /**
     * Handles an Exploding Board
     * <p>
     *     Uses {@link Cell#flipCell()} to flip all the cells which have yet to be flipped over
     *     Also writes a failure Message to the Console.
     * </p>
     */
    private void EXPLODE(){
        for (int i = 0; i < proportions[0]; i++){
            for (int j = 0; j < proportions[1]; j++){
                board[i][j].flipCell();
            }
        }
        System.out.println("You Blew Up A Mine So GAME OVER");
        IsGameOver = true;
    }

    /**
     * Checks if User wants to play again
     * <p>
     *     This Method will check if user wants to play again or not Uses {@link Scanner} to collect User response. Only accepts (P, p, Q, q) as responses, anything else will not work.
     * </p>
     * @return true for play again and false for quit the game
     */
    private static boolean PlayAgain(){
        Scanner newScanner = new Scanner(System.in);
        while (true){
            System.out.print("P)lay again Q)uit: ");
            String inputted = newScanner.nextLine();
            inputted = inputted.toLowerCase();
            if (inputted.equals("p")) {
                return true;
            }
            else if (inputted.equals("q")){
                return false;
            }
            else System.out.println("Sorry " + inputted + " is not a valid input");
        }
    }

    /**
     * Quits the game
     * <p>
     *     A fancier and more specific version of System.exit(0) since it will also print a message along with exiting the process.
     * </p>
     */
    private static void Quit(){
        System.out.println("Well then Good Bye");
        System.exit(0);
    }


     public void scriptedBoardMaking(){
         IsGameOver = false;
         firstMove = true;
         setupGame();
         if (proportions[0] == 4 && proportions[1] == 4 && mines == 3){

         }
     }

}
