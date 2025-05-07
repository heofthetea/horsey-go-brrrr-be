package brrrr.go.horsey.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class PositionService {

    final static Pattern JEN_DIMENSION_PATTERN = Pattern.compile("(\\d{3})(\\d{3})([xo])");

    /**
     * Perform a turn on the given JEN string.
     * Warning: Does not validate the board - assumes that both dimensions and player turns are valid.
     *
     * @param jen    the JEN string of the board
     * @param column the column to place the move in
     * @return the new JEN string
     * @throws IllegalArgumentException if column is out of bounds or full
     */
    public String makeTurn(String jen, Byte column) {
        byte width = Byte.parseByte(jen.substring(0, 3));

        if (column < 0 || column >= width) {
            throw new IllegalArgumentException("Invalid column: " + column);
        }

        char player = jen.charAt(6);
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

        return dimensionString + player + boardString;

    }

    public boolean isValid(String jen) {
        // Must be at least 7 characters: 3 for width, 3 for height, 1 for turn
        if (jen == null || jen.length() < 7) return false;

        // Width and height must be digits
        String dimensionsRegex = "\\d{6}";
        if (!jen.substring(0, 6).matches(dimensionsRegex)) return false;

        int width = Integer.parseInt(jen.substring(0, 3));
        int height = Integer.parseInt(jen.substring(3, 6));

        // Width and height must be positive and less than or equal to 127 (signed byte limit)
        if (width <= 0 || width > 127 || height <= 0 || height > 127) return false;

        // Turn character must be 'x' or 'o'
        char turn = jen.charAt(6);
        if (turn != 'x' && turn != 'o') return false;

        // Calculate expected length: 6 (dims) + 1 (turn) + width * height
        int expectedLength = 7 + width * height;
        if (jen.length() != expectedLength) return false;

        // Board part must only contain '-', 'x', or 'o'
        String board = jen.substring(7);
        if (!board.matches("[-xo]*")) return false;

        // Count x's and o's
        long xCount = board.chars().filter(c -> c == 'x').count();
        long oCount = board.chars().filter(c -> c == 'o').count();

        // Check turn consistency
        if (turn == 'x' && xCount != oCount) return false;
        if (turn == 'o' && xCount != oCount + 1) return false;

        // All checks passed
        return true;
    }


    /**
     * Convert a JEN string to a 2D array representing a board.
     * For specifications, see doc/jen.md
     * Not currently used, work should only happen on the string itself.
     *
     * @param jen
     * @return
     */
    @Deprecated
    public char[][] JENtoBoard(String jen) {
        byte width, height;

        try {
            Matcher m = JEN_DIMENSION_PATTERN.matcher(jen);
            if (!m.find()) {
                throw new IllegalArgumentException("Invalid JEN string: illegal dimensions");
            }
            width = Byte.parseByte(m.group(1));
            height = Byte.parseByte(m.group(2));
            if (width < 1 || height < 1) {
                throw new IllegalArgumentException("Invalid JEN string: illegal dimensions");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JEN string: illegal dimensions");
        }
        char[][] board = new char[height][width];
        String[] rows = jen.substring(7).split("(?<=\\G.{" + width + "})");// split string into width-sized chunks
        for (int i = 0; i < board.length; i++) {
            board[i] = rows[i].toCharArray();
        }

        return board;
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
