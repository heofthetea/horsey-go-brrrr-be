package brrrr.go.horsey.service;

import brrrr.go.horsey.orm.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JENTest {

    @Test
    public void testMakeTurn() {
        JEN jen = new JEN("003003x---------");
        jen.makeTurn((byte) 0);
        String expected = "003003ox--------";
        Assertions.assertEquals(expected, jen.toString());

        jen.makeTurn((byte) 1);
        Assertions.assertEquals("003003xxo-------", jen.toString());

        jen.makeTurn((byte) 1);
        Assertions.assertEquals("003003oxo--x----", jen.toString());

        jen.makeTurn((byte) 1);
        Assertions.assertEquals("003003xxo--x--o-", jen.toString());
    }


    @Test
    public void testJENValidation() {
        Assertions.assertTrue(JEN.isValid("004003o-xox--xo----"), "Valid JEN should return true");

        Assertions.assertFalse(JEN.isValid("00400x"), "Too short string should return false");

        Assertions.assertFalse(JEN.isValid("00a003x-xox--xo----"), "Invalid dimension characters should return false");

        Assertions.assertFalse(JEN.isValid("000003x-xox--xo----"), "Width of 0 should return false");

        Assertions.assertFalse(JEN.isValid("004000x-xox--xo----"), "Height of 0 should return false");

        Assertions.assertFalse(JEN.isValid("128003x-xox--xo----"), "Width > 127 should return false");

        Assertions.assertFalse(JEN.isValid("004003z-xox--xo----"), "Invalid turn character should return false");

        Assertions.assertFalse(JEN.isValid("004003x-xox--xo---"), "Incorrect string length should return false");

        Assertions.assertFalse(JEN.isValid("004003x-xox--xo--*--"), "Board with invalid characters should return false");

        Assertions.assertFalse(JEN.isValid("004003x-xoo--xo----"), "x to move but o has more pieces should return false");

        Assertions.assertFalse(JEN.isValid("004003o-xxx--xo----"), "o to move but x has too many pieces should return false");
        Assertions.assertFalse(JEN.isValid("004003x-x--o-------"), "o has floating pieces should return false");
    }


    @Test
        public void testHorizontalWin() {
            String horizontalWin = "005004o" +
                    "xxxx-" +
                    "ooo--" +
                    "-----" +
                    "-----";
            Assertions.assertEquals(Game.State.HOST_WON, new JEN(horizontalWin).getResult(), "Horizontal win should be detected");

            // test with dead space
            String horizontalWinWithDeadSpace = "006004o" +
                    "-oxoxx" +
                    "-ooxoo" +
                    "-xxxxo" +
                    "------";
            Assertions.assertEquals(Game.State.HOST_WON, new JEN(horizontalWinWithDeadSpace).getResult(), "Horizontal win with dead space ini the beginning should be detected");
        }

        @Test
        public void testVerticalWin() {
            String verticalWin = "004005x" +
                    "oxxx" +
                    "ox--" +
                    "o---" +
                    "o---" +
                    "----";
            Assertions.assertEquals(Game.State.GUEST_WON, new JEN(verticalWin).getResult(), "Vertical win should be detected");
        }

        @Test
        public void testDiagonalWinBottomLeftToTopRight() {
            String diagonalWinLR = "005005x" +
                    "xooo-" +
                    "-xxo-" +
                    "--xo-" +
                    "---x-" +
                    "-----";
            Assertions.assertEquals(Game.State.HOST_WON, new JEN(diagonalWinLR).getResult(), "Diagonal (\\) win should be detected");
        }

        @Test
        public void testDiagonalWinRightToLeft() {
            String diagonalWinRL = "005005x" +
                    "-xxxo" +
                    "-xoo-" +
                    "-xo--" +
                    "-o---" +
                    "-----";
            Assertions.assertEquals(Game.State.GUEST_WON, new JEN(diagonalWinRL).getResult(), "Diagonal (/) win should be detected");
        }

        @Test
        public void testDraw() {
            String draw = "004004x" +
                    "xxxo" +
                    "ooox" +
                    "xxxo" +
                    "ooox";
            Assertions.assertEquals(Game.State.DRAW, new JEN(draw).getResult(), "Full board with no winner should be a draw");
        }

        @Test
        public void testInProgress() {
            // Incomplete board
            String inProgress = "004004x" +
                    "xoxo" +
                    "oxox" +
                    "----" +
                    "----";
            Assertions.assertEquals(Game.State.IN_PROGRESS, new JEN(inProgress).getResult(), "Incomplete board should be in progress");
        }

        @Test
        public void testInvalidJEN() {
            String invalid = "004003x-oxox-------"; // floating x
            Assertions.assertNull(new JEN(invalid).getResult(), "Invalid JEN should return null");
        }
}
