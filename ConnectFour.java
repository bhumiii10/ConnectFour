import java.io.*;
import java.util.Scanner;
import java.util.Stack;

public class ConnectFour {
    public static void main(String[] args) {
        Grid grid = new Grid();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter name for Player 1: ");
        String name1 = scanner.nextLine();
        Player player1 = new Player(name1, 'X', grid);

        System.out.print("Enter name for Player 2: ");
        String name2 = scanner.nextLine();
        Player player2 = new Player(name2, 'O', grid);

        Player currentPlayer = player1;
        boolean gameWon = false;

        while (!grid.isFull() && !gameWon) {
            grid.displayGrid();
            System.out.println("Options: 1. Make a Move  2. Undo Last Move  3. Save Game  4. Load Game");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    if (currentPlayer.takeTurn()) {
                        gameWon = grid.checkWinner(currentPlayer.getSymbol());
                        if (gameWon) {
                            System.out.println(currentPlayer.getName() + " wins!");
                        } else {
                            currentPlayer = (currentPlayer == player1) ? player2 : player1;
                        }
                    } else {
                        System.out.println("Invalid move. Try again.");
                    }
                    break;
                case 2:
                    if (grid.undoLastMove()) {
                        currentPlayer = (currentPlayer == player1) ? player2 : player1;
                        System.out.println("Last move undone.");
                    }
                    break;
                case 3:
                    System.out.print("Enter filename to save: ");
                    String saveFile = scanner.next();
                    grid.saveGame(saveFile);
                    break;
                case 4:
                    System.out.print("Enter filename to load: ");
                    String loadFile = scanner.next();
                    grid.loadGame(loadFile);
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        if (!gameWon) {
            System.out.println("It's a draw!");
        }
    }
}

//Class representing a disc in the Connect Four game
class Disc {
    private char symbol;   // Disc symbol ('X' or 'O')
    private int row;       
    private int column;

    public Disc(char symbol, int row, int column) {
        this.symbol = symbol;
        this.row = row;
        this.column = column;
    }

    public char getSymbol() {
        return symbol;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}

class Player {
    private String name;
    private char symbol;
    private Grid grid;

    public Player(String name, char symbol, Grid grid) {
        this.name = name;
        this.symbol = symbol;
        this.grid = grid;
    }

    public boolean takeTurn() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(name + " (" + symbol + "), choose a column (0-6): ");
        int column = scanner.nextInt();
        return grid.dropDisc(column, symbol);
    }

    public char getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }
}

//Class representing the game grid
class Grid {
 private char[][] grid;              // 2D array for the grid
 private Disc[] moves;                // Array to keep track of moves for undo functionality
 private int moveCount;               // Current count of moves made
 public static final int ROWS = 6;    // Number of rows in the grid
 public static final int COLUMNS = 7;  // Number of columns in the grid
 public static final int MAX_MOVES = ROWS * COLUMNS; // Maximum possible moves

 // Constructor initializing the grid and moves array
 public Grid() {
     grid = new char[ROWS][COLUMNS];
     moves = new Disc[MAX_MOVES]; // Initialize moves array
     moveCount = 0;                // Initialize move count
     for (int i = 0; i < ROWS; i++) {
         for (int j = 0; j < COLUMNS; j++) {
             grid[i][j] = '-';  // Initialize all grid cells to '-'
         }
     }
 }

 // Method to drop a disc into a column, returns false if column is full
 public boolean dropDisc(int column, char symbol) {
     if (column < 0 || column >= COLUMNS) {
         System.out.println("Column out of bounds.");
         return false;
     }
     for (int row = ROWS - 1; row >= 0; row--) {
         if (grid[row][column] == '-') {
             grid[row][column] = symbol;              // Place the disc in the lowest empty cell
             if (moveCount < MAX_MOVES) {
                 moves[moveCount] = new Disc(symbol, row, column); // Add move to array
                 moveCount++; // Increment move count
             }
             return true;
         }
     }
     System.out.println("Column is full.");
     return false;
 }

 // Undo the last move by decrementing moveCount and clearing cell, returns false if no moves
 public boolean undoLastMove() {
     if (moveCount > 0) {
         moveCount--; // Decrement move count to get the last move index
         Disc lastMove = moves[moveCount]; // Retrieve the last move
         grid[lastMove.getRow()][lastMove.getColumn()] = '-'; // Clear the cell
         moves[moveCount] = null; // Clear the reference in the moves array
         return true;
     }
     System.out.println("No moves to undo.");
     return false;
 }

 // Check if the grid is full by looking for any empty cells
 public boolean isFull() {
     return moveCount >= MAX_MOVES; // Check if move count has reached maximum moves
 }

 // Check if a player has won by connecting four symbols in a row, column, or diagonal
 public boolean checkWinner(char symbol) {
     return checkHorizontal(symbol) || checkVertical(symbol) || checkDiagonal(symbol);
 }

 // Check horizontal win condition (four symbols in a row horizontally)
 private boolean checkHorizontal(char symbol) {
     for (int row = 0; row < ROWS; row++) {
         for (int col = 0; col < COLUMNS - 3; col++) {
             if (grid[row][col] == symbol && grid[row][col + 1] == symbol &&
                 grid[row][col + 2] == symbol && grid[row][col + 3] == symbol) {
                 return true;
             }
         }
     }
     return false;
 }

 // Check vertical win condition (four symbols in a row vertically)
 private boolean checkVertical(char symbol) {
     for (int row = 0; row < ROWS - 3; row++) {
         for (int col = 0; col < COLUMNS; col++) {
             if (grid[row][col] == symbol && grid[row + 1][col] == symbol &&
                 grid[row + 2][col] == symbol && grid[row + 3][col] == symbol) {
                 return true;
             }
         }
     }
     return false;
 }

 // Check diagonal win condition (four symbols in a row diagonally)
 private boolean checkDiagonal(char symbol) {
     // Check ascending diagonal
     for (int row = 0; row < ROWS - 3; row++) {
         for (int col = 0; col < COLUMNS - 3; col++) {
             if (grid[row][col] == symbol && grid[row + 1][col + 1] == symbol &&
                 grid[row + 2][col + 2] == symbol && grid[row + 3][col + 3] == symbol) {
                 return true;
             }
         }
     }
     // Check descending diagonal
     for (int row = 3; row < ROWS; row++) {
         for (int col = 0; col < COLUMNS - 3; col++) {
             if (grid[row][col] == symbol && grid[row - 1][col + 1] == symbol &&
                 grid[row - 2][col + 2] == symbol && grid[row - 3][col + 3] == symbol) {
                 return true;
             }
         }
     }
     return false;
 }

 // Convert grid to string for display, showing current state with alignment
 @Override
 public String toString() {
     StringBuilder gridRepresentation = new StringBuilder();
     for (int i = 0; i < ROWS; i++) {
         for (int j = 0; j < COLUMNS; j++) {
             gridRepresentation.append(String.format("%2s", grid[i][j]));
         }
         gridRepresentation.append("\n");
     }
     return gridRepresentation.toString();
 }

 // Display grid by printing the string representation
 public void displayGrid() {
     System.out.println(this.toString());
 }

 // Save the current game state to a file
 public void saveGame(String filename) {
     try (PrintWriter writer = new PrintWriter(filename)) {
         for (int i = 0; i < ROWS; i++) {
             for (int j = 0; j < COLUMNS; j++) {
                 writer.print(grid[i][j]);
             }
             writer.println();
         }
         System.out.println("Game saved to " + filename);
     } catch (IOException e) {
         System.out.println("Error saving game: " + e.getMessage());
     }
 }

 // Load a saved game state from a file
 public void loadGame(String filename) {
     try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
         for (int i = 0; i < ROWS; i++) {
             String line = reader.readLine();
             for (int j = 0; j < COLUMNS; j++) {
                 grid[i][j] = line.charAt(j);
             }
         }
         System.out.println("Game loaded from " + filename);
     } catch (IOException e) {
         System.out.println("Error loading game: " + e.getMessage());
     }
 }
}







