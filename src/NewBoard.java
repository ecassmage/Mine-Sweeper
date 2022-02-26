import java.util.Random;
import java.time.LocalTime;
import java.util.Scanner;
// I like adding a bunch of 0 and . to versions.

// Why are these called NewBoard and NewCell and not just Board and Cell. Well I made a previous copy of this game but I thought I was doing something wrong so I started a New Copy.
// Turns out I wasn't doing it wrong but I was far enough along that I just finished anyways.

/**
 * Operates Minesweeper in General
 * <p>
 *     This Class Manages the Game by storing Information in context to the game as well as managing inputs the player gives it.
 * </p>
 * @author Evan Morrison
 * @version 1.00.3
 * @since 1.00.0
 */
public class NewBoard {
    /**
     * Dev Tools. For Cheating I guess. true enables them, false disables them.
     */
    static boolean dev = false;  // Shows a copy with locations of all mines when set to true. For testing purposes only.
    /**
     * Defaults for minimum and maximum board and mine settings.
     */
    private int[][] sizeRules = {/* Columns */ {4, 20}, /* Rows */ {4, 20}, /* Mines */ {1, 10}, /* Defaults */ {4, 20, 4, 20, 1, 10}};
    /**
     * Holds chosen size of board.
     */
    private int[] proportions = new int[2];
    /**
     * holds max value of Mines.
     */
    private int mines;
    /**
     * holds MDA of Cells for Board
     * <p>
     *     holds Multi-Dimensional Array of Cells for Board
     * </p>
     */
    private NewCell[][] board;
    /**
     * holds a long int value for when game starts
     */
    private long timer;
    /**
     * holds value for if first move has been made or not
     */
    private boolean firstMove;
    /**
     * Affirms whether game is over or not
     */
    private boolean isGameOver;

    /**
     * Holds boolean for won game or not
     */
    private boolean winner;

    /**
     * Board Constructor. Sets some booleans to pre game settings.
     */
    public NewBoard(){
        this.firstMove = true;
        this.isGameOver= false;
        this.winner = false;
    }

    /**
     * Main Method, for testing the parts of the program.
     * @param args, Dunno, does argy stuff probably
     */
    public static void main(String[] args){
        System.out.println("Hello World");
        NewBoard gameBoard = new NewBoard();
        gameBoard.playGame(true);
    }

    /**
     * Allows you to either call everything yourself outside the program or just run it all automatically
     * <p>
     *     Send true if you want things like game setup and routine loop to start automatically, it will also ask at the end if you want to play another game or not.
     *     If however you wish to manage this all yourself, you can instead pass false through which will disable these games.
     *     NOTE: you will have to {@link #setupGame()}, {@link #startGame()} and should you wish to continuously run the process, {@link #playAgain()} and {@link #resetBoard()}
     * </p>
     * @param allInOne pass true for minesweeper to set itself up and run automatically else set false.
     */
    public void playGame(boolean allInOne){
        gameRules();
        System.out.println("**** Welcome to Minesweeper Game ***\n************************************");  // Don't Lose **** Welcome to Minesweeper Game ***\n************************************
        if (allInOne){
            while (true){
                setupGame();
                startGame();
                if(!playAgain()) Quit();
                resetBoard();
            }
        }
    }

    /**
     * Starts the game
     * <p>
     *     This will start a timer which will then keep track of how long you take to complete the board. It will then begin the main loop of the game.
     * </p>
     */
    public void startGame(){
        timer = System.currentTimeMillis();
        mainGameLoop();
    }

    /**
     * Just some Instructions of the Game.
     */
    public void gameRules(){
        System.out.println(
                "Hello, These are the Rules:" +
                "\n\tTo Uncover a Cell Press the L character. (It is not Case Sensitive)" +
                "\n\tTo Flag a Cell Press Any Key Other then the L Key" +
                "\n\tTo Win The Game Uncover all Cells which do not have a mine under them while Avoiding all the Mines" +
                "\n\tShould you flag a Cell, you may still uncover it afterwards should you wish to." +
                "\n\tOnce you have uncovered every non mined cell, the timer will stop and you will be given your time."
        );
        Scanner Scantron = new Scanner(System.in);
        while (true) {
            System.out.print("\n\tPress E once you are Ready: ");
            if (Scantron.nextLine().equals("E")) {
                System.out.println();
                System.out.println("\n");
                break;
            }
        }
    }

    /**
     * Resets the Game Board
     */
    public void resetBoard(){
        isGameOver = false;
        firstMove = true;
        winner = false;
        board = null;
        proportions[0] = 0;
        proportions[1] = 0;
        mines = 0;
        timer = 0;
    }

    /**
     * Sets the Game up
     * <p>
     *     Collects the User input for board proportions and mine count. makes sure these inputs are valid entries.
     * </p>
     */
    public void setupGame(){
        while (true) {
            System.out.println("Enter the board size and number of mines in the form of: Row (" + sizeRules[0][0] + ":" + sizeRules[0][1] + ") Col(" + sizeRules[1][0] + ":" + sizeRules[1][1] + ") Mines(1:size-" + sizeRules[2][1] + ")");

            Scanner newScanner = new Scanner(System.in);
            proportions[0] = newScanner.nextInt();
            proportions[1] = newScanner.nextInt();
            mines = newScanner.nextInt();
            sizeRules[2][1] =  sizeRules[2][1] + (proportions[0] * proportions[1]);

            if (outBounds(sizeRules[0], proportions[0]) || outBounds(sizeRules[1], proportions[1]) || outBounds(sizeRules[2], mines)){ // true below is just there because I was or am going to add another part in to remove limits on rows and columns
                if (outBounds(sizeRules[0], proportions[0])) System.out.println("Sorry but you have Entered an incorrect Row Size");
                if (outBounds(sizeRules[1], proportions[1])) System.out.println("Sorry but you have Entered an incorrect Column Size");
                if (outBounds(sizeRules[2], mines)) System.out.println("Sorry but you have Entered an incorrect amount of Mines");
                sizeRules[2][0] = sizeRules[3][4];
                sizeRules[2][1] = sizeRules[3][5];
            }
            else break;
        }
        boardAddMines(true, 0);
    }

    /**
     * Main Game Loop
     * <p>
     *     This method manages the Users inputs for uncovering or flagging a cell. It will print a new board after every move and check if the user has won the game or not.
     * </p>
     */
    private void mainGameLoop(){
        int[] coords = {0, 0};
        String MouseClick;
        Scanner newScanner = new Scanner(System.in);
        while (!isGameOver){
            printBrd(false);
            if (dev) printBrd(true);
            do {
                System.out.println("Enter Square Location: Row (1:" + proportions[0] + "), Col (1:" + proportions[1] + "), and L)eft or R)ight (enter 0 0 0 to Exit)");
                coords[0] = newScanner.nextInt();
                coords[1] = newScanner.nextInt();
                MouseClick = newScanner.nextLine();
                if (coords[0] == 0 && coords[1] == 0 & MouseClick.equals(" 0")) {
                    Quit();
                }
            } while (0 >= coords[0] || coords[0] > proportions[0] || 0 >= coords[1] || coords[1] > proportions[1]);
            coords[0]--;
            coords[1]--;
            if (MouseClick.equals(" L") || MouseClick.equals(" l")) LeftClick(coords);
            else RightClick(coords);
            if (checkForVictory() && !isGameOver){
                Winner();
            }
        }
        printBrd(false);
        if (winner) System.out.println("\nYaY!, YOU COMPLETELY CLEANED THE MINEFIELD IN " + ((System.currentTimeMillis() - timer) / 1000) + " SECONDS...! :)");
        else System.out.println("\nUh-Oh, THE MINEFIELD EXPLODED ...! :(");
    }

    /**
     * Routine for when User left Clicks
     * @param coords Coordinates for which Cell was Clicked
     */
    private void LeftClick(int[] coords){
        if (board[coords[0]][coords[1]].getFlippedState()) return;
        if (firstMove) isThisTheFirstMove(coords);
        if (board[coords[0]][coords[1]].getMine()) EXPLODE();
        else board[coords[0]][coords[1]].changeVisibleState(0, false);
    }

    /**
     * Routine for when User Right Clicks
     * @param coords Coordinates for which Cell was Clicked
     */
    private void RightClick(int[] coords){
        if (board[coords[0]][coords[1]].getFlippedState()) return;
        board[coords[0]][coords[1]].changeVisibleState(1, false);
    }

    /**
     * Routine for the first move made during a game which involve uncovering a Cell.
     * @param coords Coordinates for which Cell was Clicked
     */
    private void isThisTheFirstMove(int[] coords){
        firstMove = false;
        int numOfNewMinesNeeded = 0;
        board[coords[0]][coords[1]].banCellToMineStatus();
        if (board[coords[0]][coords[1]].getMine()) {
            board[coords[0]][coords[1]].changeStateOfCell(false);
            numOfNewMinesNeeded++;
        }
        for (int i = 0; i < 8; i++){
            if (board[coords[0]][coords[1]].getSurroundingCell(i) == null) continue;
            if (board[coords[0]][coords[1]].getSurroundingCell(i).getMine()){
                board[coords[0]][coords[1]].getSurroundingCell(i).changeStateOfCell(false);
                numOfNewMinesNeeded++;
            }
            board[coords[0]][coords[1]].getSurroundingCell(i).banCellToMineStatus();
        }
        boardAddMines(false, numOfNewMinesNeeded);
        for (int i = 0; i < 8; i++){
            if (board[coords[0]][coords[1]].getSurroundingCell(i) == null) continue;
            board[coords[0]][coords[1]].getSurroundingCell(i).countSurroundingMines(true);
        }
    }

    /**
     * Generates a Board.
     * <p>
     *     This if asked will generate a New Board by calling {@link #makeBoard()}. It will then fill the board with the required number of mines.
     *     Should boolean newBoard be false though, this will just add some new mines based on the int num passed to it.
     * </p>
     * @param newBoard pass true if you are setting up a new board, pass false if you just want more mines.
     * @param num Pass the number of mines wanted. if newBoard is true this can be any int value.
     */
    private void boardAddMines(boolean newBoard, int num){

        Random randomNumber = new Random();
        LocalTime myObj = LocalTime.now();
        randomNumber.setSeed(myObj.getNano());

        int numberOfNewMines = mines;
        if (newBoard) makeBoard();  // Makes a blank board to be filled
        else numberOfNewMines = num;
        int placedMines = 0;
        int newX;
        int newY;

        while (placedMines < numberOfNewMines){
            newX = randomNumber.nextInt(proportions[0]);
            newY = randomNumber.nextInt(proportions[1]);
            if (!board[newX][newY].getMineBan() && !board[newX][newY].getState().equals("M")){
                board[newX][newY].changeStateOfCell(true);
                placedMines += 1;
            }
        }

        if (newBoard) connectSurroundingCells();
    }

    /**
     * constructs a new board and constructs new Cells.
     */
    public void makeBoard(){
        board = new NewCell[proportions[0]][proportions[1]];
        for (int i = 0; i < proportions[0]; i++){
            for (int j = 0; j < proportions[1]; j++){
                board[i][j] = new NewCell();
            }
        }
    }

    /**
     * Connects the Boards Cells together in a web finding all adjacent Cells.
     */
    private void connectSurroundingCells(){
        for (int i = 0; i < proportions[0]; i++){
            for (int j = 0; j < proportions[1]; j++){
                board[i][j].findSurroundingCells(board, i, j);
            }
        }
    }

    /**
     * Quit the Game with a Message
     */
    private static void Quit(){
        System.out.println("Well then Good Bye");
        System.exit(0);
    }

    /**
     * Check if a victory has been made on this board or not
     * @return returns true if this board is a winner and false if not.
     */
    public boolean checkForVictory(){
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
     * Checks if input is out of bounds
     * @param range Holds the Minimum and Maximum bounds range.
     * @param value Value to Check if outside bounds
     * @return returns true if out of bounds else returns false
     */
    private static boolean outBounds(int[] range, int value){
        if (range[0] <= value && value <= range[1]) return false;
        return true;
    }

    /**
     * Prints a copy of the Board
     * @param cheatBrd if true, will print a copy of the Board with all mine locations
     */
    public void printBrd(boolean cheatBrd){
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
     * Prints the Cells
     * @param cheatBrd will choose to print Visual State if false or State if true
     * @param i holds int value from {@link #printBrd(boolean)}
     * @param j holds int value from {@link #printBrd(boolean)}
     */
    private void printCell(boolean cheatBrd, int i, int j) {
        if (cheatBrd) System.out.print("| " + board[i][j].getState() + " ");
        else System.out.print("| " + board[i][j].getVisibleState() + " ");
    }

    /**
     * sets board to winner mode and sets the board to being game over.
     */
    private void Winner(){
        winner = true;
        isGameOver = true;
        for (int i = 0; i < proportions[0]; i++){
            for (int j = 0; j < proportions[1]; j++){
                board[i][j].flipUnFlippedCells(2);
            }
        }
    }

    /**
     * Handles an EXPLODED board.
     * <p>
     *     Is run should the User uncover a mine after the first move.
     * </p>
     */
    private void EXPLODE(){
        for (int i = 0; i < proportions[0]; i++){
            for (int j = 0; j < proportions[1]; j++){
                board[i][j].flipUnFlippedCells(0);
            }
        }

        isGameOver = true;
    }

    /**
     * Will ask to play again
     * <p>
     *     If you accept to play again by pressing the p key it will return true else if you press q this will return false
     *     {@link #playGame(boolean)} uses this if you passed true to it to start a new game.
     *     This will only accept the p key or the q key.
     * </p>
     * @return returns true if a new game is wanted else if user wants to quit will pass false.
     */
    private static boolean playAgain(){
        Scanner newScanner = new Scanner(System.in);
        while (true){
            System.out.print("P)lay again Q)uit: ");
            String inputted = newScanner.nextLine();
            inputted = inputted.toLowerCase();
            System.out.println();
            if (inputted.equals("p")) {
                return true;
            }
            else if (inputted.equals("q")){
                return false;
            }
            else System.out.println("Sorry " + inputted + " is not a valid input");
        }
    }
}
