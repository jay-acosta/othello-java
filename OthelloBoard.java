import java.util.List;
import java.util.ArrayList;

public class OthelloBoard implements OthelloConstants {

    // private instance vars
    private char[][] board;
    private boolean showMoves;
    private boolean isWhiteTurn;

    private List<MoveVector> possibleMoves;

    // pre: none
    // post: initialize the Othello board
    //  if used correctly, this method should really only be called once
    public OthelloBoard() {
        board = new char[8][8];
        resetBoard();
    }

    public OthelloBoard(char[][] newBoard) {
        if (!isSquare(newBoard)) {
            throw new IllegalArgumentException("new board must be of length 8 x 8");
        }

        if (!containsValidCharacters(newBoard)) {
            throw new IllegalArgumentException("new board can only contain '-', 'W', and 'B' characters");
        }

        board = new char[8][8];
        deepCopy(newBoard, board);
        resetBoard();
    }

    private boolean isSquare(char[][] board) {
        if (board.length != 8) {
            return false;
        }

        for(int i = 0; i < board.length; i++) {
            if (board[i].length != 8) {
                return false;
            }
        }

        return true;
    }

    // pre: source and destination must have the same dimensions
    // post: reuslt
    private void deepCopy(char[][] source, char[][] destination) {

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                destination[row][col] = source[row][col];
            }
        }
    }

    private boolean containsValidCharacters(char[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                char currentChar = board[row][col];
                if (currentChar != BLANK_PIECE && currentChar != WHITE_PIECE && currentChar != BLACK_PIECE)
                    return false;
            }
        }

        return true;
    }

    // pre: none
    // post: reset the current Othello board
    public void resetBoard() {

        isWhiteTurn = false;

        // clear the board and place the first four pieces
        clearBoard();
        board[3][3] = BLACK_PIECE;
        board[4][4] = BLACK_PIECE;
        board[4][3] = WHITE_PIECE;
        board[3][4] = WHITE_PIECE;
    }

    // places a blank spot on every spot on the board
    private void clearBoard() {

        // place a blank piece in every slot on the board
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = BLANK_PIECE;
            }
        }
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board[0].length;
    }

    public char currentTurn() {
        return isWhiteTurn? WHITE_PIECE: BLACK_PIECE;
    }

    public char nextTurn() {
        return isWhiteTurn? BLACK_PIECE: WHITE_PIECE;
    }

    public void switchTurns() {
        isWhiteTurn = !isWhiteTurn;
    }

    // pre: row and col must be in bounds of the board
    // post: returns whether or not a piece can be placed on the board at the specified location
    public boolean canBePlaced(int row, int col, char piece) {
        if (!inBounds(row, col)) {
            throw new IllegalArgumentException("cannot access element at (" + row + ", " + col + ")");
        }

        if (board[row][col] != BLANK_PIECE) {
            return false;
        }

        board[row][col] = piece;
        int numberOfMoves = getPossibleMoves(row, col).size();
        board[row][col] = BLANK_PIECE;

        return numberOfMoves > 0;
    }

    // pre: row and col must be in bounds of the board
    // post: returns whether or not a piece can be placed on the board at the specified location
    public int findMaxPieces(int row, int col, char piece) {
        if (!inBounds(row, col)) {
            throw new IllegalArgumentException("cannot access element at (" + row + ", " + col + ")");
        }

        if (board[row][col] != BLANK_PIECE) {
            return 0;
        }

        board[row][col] = piece;
        int numberEarned = 0;
        List<MoveVector> moves = getPossibleMoves(row, col);

        for(MoveVector move: moves) {
            numberEarned += move.maxDisplacement() - 1;
        }

        board[row][col] = BLANK_PIECE;

        return numberEarned;
    }

    public void showMoves(boolean show) {
        showMoves = show;
    }

    // pre: piece must be placeable at the current spot
    // post: changes the board to place current piece and new pieces to flip over
    public void placePiece(int row, int col, char piece) {
        if (!inBounds(row, col)) {
            throw new IllegalArgumentException("cannot access element at (" + row + ", " + col + ")");
        }
        if (!canBePlaced(row, col, piece)) {
            throw new IllegalArgumentException(piece + " cannot be placed at (" + row + ", " + col + ")");
        }

        board[row][col] = piece;

        List<MoveVector> moves = getPossibleMoves(row, col);

        interpretMoves(moves, piece);
    }

    private void interpretMoves(List<MoveVector> moves, char piece) {
        for (MoveVector vector: moves) {
            int deltaX = vector.normalizedX();
            int deltaY = vector.normalizedY();

            int x = vector.startX;
            int y = vector.startY;

            while (x != vector.endX || y != vector.endY) {
                board[x][y] = piece;

                x += deltaX;
                y += deltaY;
            }
        }
    }

    private List<MoveVector> getPossibleMoves(int row, int col) {
        if (!inBounds(row, col)) {
            throw new IllegalArgumentException("cannot access element at (" + row + ", " + col + ")");
        }

        // get the current piece on the board
        char currentChar = board[row][col];

        // check if the current character is not
        if (currentChar != WHITE_PIECE && currentChar != BLACK_PIECE) {
            throw new IllegalArgumentException("current character must be either black or white piece");
        }

        List<MoveVector> movesList = new ArrayList<>();

        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {
                if (r != 0 || c != 0) {
                    int[] vector = {r, c};
                    MoveVector moveVector = findClosestPiece(row, col, vector, currentChar);

                    if (moveVector != null) {
                        movesList.add(moveVector);
                    }
                }
            }
        }

        return movesList;
    }

    private MoveVector findClosestPiece(int row, int col, int[] vector, char startingPiece) {

        int xDelta = vector[0];
        int yDelta = vector[1];

        int x = row + xDelta;
        int y = col + yDelta;

        while (inBounds(x, y)) {
            char currentPiece = board[x][y];

            if (currentPiece == BLANK_PIECE || currentPiece == TEMP_PIECE) {
                return null;
            }

            if (currentPiece == startingPiece) {
                MoveVector result = new MoveVector(row, col, x, y);
                if (result.displacementX() > 1 || result.displacementY() > 1) {
                    return result;
                } else {
                    return null;
                }
            }

            x += xDelta;
            y += yDelta;
        }

        return null;
    }

    // pre: none
    // post: checks to see if board is fully filled
    public boolean isGameOver() {

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == BLANK_PIECE) {
                    return findAllPossible(WHITE_PIECE) == 0 && findAllPossible(BLACK_PIECE) == 0;
                }
            }
        }

        return true;
    }

    private int findAllPossible(char piece) {

        int total = 0;

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                total += findMaxPieces(r, c, piece);
            }
        }

        return total;
    }

    // pre: none
    // post: returns the number of times 'piece' occurs on the board
    public int countPieces(char piece) {
        int count = 0;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == piece) {
                    count++;
                }
            }
        }

        return count;
    }

    public void printBoard() {
        if (showMoves) {
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    if (canBePlaced(r, c, currentTurn())) {
                        board[r][c] = TEMP_PIECE;
                    }
                }
            }

            System.out.println(this);

            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    if (board[r][c] == TEMP_PIECE) {
                        board[r][c] = BLANK_PIECE;
                    }
                }
            }

        } else {
            System.out.println(this);
        }
    }

    // pre: none
    // post: prints the board
    public String toString() {
        StringBuilder output = new StringBuilder();

        output.append("  0 1 2 3 4 5 6 7\n");
        // print out the current contents of the board
        for (int row = 0; row < board.length; row++) {

            output.append(row);
            for (int col = 0; col < board[row].length; col++) {
                output.append(' ');
                output.append(board[row][col]);
            }

            output.append('\n');
        }

        return output.toString();
    }

    private class MoveVector {
        private int startX;
        private int startY;
        private int endX;
        private int endY;

        public MoveVector(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        public int normalizedX() {
            if (endX - startX == 0) {
                return 0;
            } else {
                return endX - startX > 0? 1: -1;
            }
        }

        public int normalizedY() {
            if (endY - startY == 0) {
                return 0;
            } else {
                return endY - startY > 0? 1: -1;
            }
        }

        public int displacementX() {
            return Math.abs(startX - endX);
        }

        public int displacementY() {
            return Math.abs(startY - endY);
        }

        public int maxDisplacement() {
            return Math.max(displacementX(), displacementY());
        }

        public String toString() {
            return "(" + startX + ", " + startY + ") -> (" + endX + ", " + endY + ")";
        }
    }
}