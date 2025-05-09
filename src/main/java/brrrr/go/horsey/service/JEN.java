package brrrr.go.horsey.service;

import brrrr.go.horsey.orm.Game.State;

/**
 * Wrapper Class for a JEN string.
 * Exposes methods to manipulate, as well as get relevant Information about the JEN string.
 * Using functions for this probably is criminal in terms of performance, but keeps the code shorter so fine.
 */
public class JEN implements Cloneable{

    String jen;
    boolean isValid;


    public JEN(String jen) {
        this.jen = jen;
        this.isValid = JEN.isValid(jen);
    }

    /**
     * Creates a default JEN for the start of a game.
     * @param width width of the board
     * @param height height of the board
     */
    public JEN(byte width, byte height) {
        assert width > 0 && height > 0;
        this.jen = String.format("%03d%03d", width, height) + "x" + "-".repeat(width * height);
        this.isValid = true;
    }

    public JEN clone() {
        return new JEN(this.jen);
    }


    /**
     * Get the complete JEN string.
     *
     * @return the JEN string, null if invalid
     */
    @Override
    public String toString() {
        return this.isValid ? this.jen : null;
    }

    /**
     * Get the width of the board.
     *
     * @return the width of the board, -1 if JEN invalid
     */
    public byte getWidth() {
        return this.isValid ? Byte.parseByte(jen.substring(0, 3)) : -1;

    }

    /**
     * Get the height of the board.
     *
     * @return the height of the board, -1 if JEN invalid
     */
    public byte getHeight() {
        return this.isValid ? Byte.parseByte(jen.substring(3, 6)) : -1;
    }

    /**
     * Get the current player.
     *
     * @return the current player
     */
    public char getCurrentPlayer() {
        return this.isValid ? jen.charAt(6) : '-';
    }

    /**
     * Get only the part of the JEN string representing the board state.
     * @return the board string, null if invalid
     */
    public String getBoardString() {
        return this.isValid ? jen.substring(7) : null;
    }

    /**
     * Determine the state of the game based on the current board.
     * #allhailthevibecoding
     * @return {@link State} of the game, null if invalid
     */
    public State getState() {
        if (!this.isValid) {
            return null;
        }


        String boardString = this.getBoardString();
        final byte width = this.getWidth();
        final byte height = this.getHeight();

        final String[] rows = boardString.split("(?<=\\G.{" + width + "})");



        // Check for horizontal win
        for (String row : rows) {
            if (row.contains("xxxx")) {
                return State.HOST_WON;
            }
            if (row.contains("oooo")) {
                return State.GUEST_WON;
            }
        }
        // Check for vertical win
        for (int i = 0; i < width; i++) {
            StringBuilder column = new StringBuilder();
            for (int j = 0; j < height; j++) {
                column.append(rows[j].charAt(i));
            }
            if (column.toString().contains("xxxx")) {
                return State.HOST_WON;
            }
            if (column.toString().contains("oooo")) {
                return State.GUEST_WON;
            }
        }

        // Check for diagonal win
        for (int i = 0; i < height - 3; i++) {
            for (int j = 0; j < width - 3; j++) {
                String diagonal1 = "" + rows[i].charAt(j) + rows[i + 1].charAt(j + 1) + rows[i + 2].charAt(j + 2) + rows[i + 3].charAt(j + 3);
                String diagonal2 = "" + rows[i].charAt(j + 3) + rows[i + 1].charAt(j + 2) + rows[i + 2].charAt(j + 1) + rows[i + 3].charAt(j);
                if (diagonal1.contains("xxxx") || diagonal2.contains("xxxx")) {
                    return State.HOST_WON;
                }
                if (diagonal1.contains("oooo") || diagonal2.contains("oooo")) {
                    return State.GUEST_WON;
                }
            }
        }

        // Check for draw
        if (boardString.indexOf('-') == -1) {
            return State.DRAW;
        }

        // jen is valid, however nobody won, and it's not a draw
        return State.IN_PROGRESS;

    }


    /**
     * Perform a turn on the given JEN string.
     * Warning: Does not validate the board - assumes that both dimensions and player turns are valid.
     *
     * @param column the column to place the move in
     * @throws IllegalArgumentException if column is out of bounds or full
     * @return the new JEN string
     */
    public void makeTurn(Byte column) {
        final byte width = getWidth();

        if (column < 0 || column >= width) {
            throw new IllegalArgumentException("Invalid column: " + column);
        }

        char player = getCurrentPlayer();
        final String dimensionString = jen.substring(0, 6);
        String boardString = jen.substring(7);

        // find first empty space in column and replace it with the player
        boolean changed = false;
        for (int i = column; i < boardString.length(); i += width) {
            if (boardString.charAt(i) != '-') {
                continue;
            }
            boardString = boardString.substring(0, i) + player + boardString.substring(i + 1);
            player = otherPlayer(player);
            changed = true;
            break;
        }
        if (!changed) {
            throw new IllegalArgumentException("no empty spaces");
        }

        jen = dimensionString + player + boardString;

    }


    /**
     * Validate a JEN string.
     * For specifications, see doc/jen.md.
     *
     * @param tJen a jen string
     * @return true if jen is valid, false otherwise
     */
    public static boolean isValid(String tJen) {


        // Must be at least 7 characters: 3 for width, 3 for height, 1 for turn
        if (tJen == null || tJen.length() < 7) return false;

        // Width and height must be digits
        String dimensionsRegex = "\\d{6}";
        if (!tJen.substring(0, 6).matches(dimensionsRegex)) return false;

        // now we know width and height can safely be parsed
        final int width = Integer.parseInt(tJen.substring(0, 3));
        final int height = Integer.parseInt(tJen.substring(3, 6));

        // Width and height must be positive and less than or equal to 127 (signed byte limit)
        if (width <= 0 || width > 127 || height <= 0 || height > 127) return false;

        // Turn character must be 'x' or 'o'
        char turn = tJen.charAt(6);
        if (turn != 'x' && turn != 'o') return false;

        // Calculate expected length: 6 (dims) + 1 (turn) + width * height
        int expectedLength = 7 + width * height;
        if (tJen.length() != expectedLength) return false;

        // Board part must only contain '-', 'x', or 'o'
        String board = tJen.substring(7);
        if (!board.matches("[-xo]*")) return false;

        // Count x's and o's
        long xCount = board.chars().filter(c -> c == 'x').count();
        long oCount = board.chars().filter(c -> c == 'o').count();

        // Check turn consistency
        if (turn == 'x' && xCount != oCount) return false;
        if (turn == 'o' && xCount != oCount + 1) return false;

        // Check for floating pieces
        for (int col = 0; col < width; col++) {
            boolean foundEmpty = false;
            for (int row = 0; row < height; row++) {
                int index = row * width + col;
                char cell = board.charAt(index);

                if (cell == '-') {
                    foundEmpty = true;// once we find an empty cell, all above must also be empty
                } else if(foundEmpty) {
                    // found a piece above an empty cell
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Warning: anything other than 'x' will turn into 'o'
     * Should be fine though considering all validation should happen somewhere else
     *
     * @param player
     * @return
     */
    private char otherPlayer(char player) {
        if (player == 'x') {
            return 'o';
        }
        return 'x';
    }
}
