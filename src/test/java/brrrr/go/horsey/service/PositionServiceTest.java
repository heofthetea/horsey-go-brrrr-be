package brrrr.go.horsey.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PositionServiceTest {

    PositionService positionService = new PositionService();


    @Test
    /**
     * @given a JEN string
     * @when the string is converted to a board
     * @then the board has the correct dimensions
     */
    public void testJENDimensions(){
        String JEN = "005006x------------------------------";
        char[][] board = positionService.JENtoBoard(JEN);
        Assertions.assertEquals(5, board.length);
        Assertions.assertEquals(6, board[0].length);

    }
    @Test
    public void testJENtoBoard(){
        String JEN = "004003o-xox--xo----";
        char[][] board = positionService.JENtoBoard(JEN);
        char[][] expected = {
                {'-', 'x', 'o', 'x',},
                {'-', '-', 'x', 'o',},
                {'-', '-', '-', '-',}
        };
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                Assertions.assertEquals(expected[i][j], board[i][j]);
            }
        }
    }

    @Test
    public void testMakeTurn(){
        String JEN = "003003x---------";
        JEN = positionService.makeTurn(JEN, (byte) 0);
        String expected = "003003ox--------";
        Assertions.assertEquals(expected, JEN);

        JEN = positionService.makeTurn(JEN, (byte) 1);
        Assertions.assertEquals("003003xxo-------", JEN);

        JEN = positionService.makeTurn(JEN, (byte) 1);
        Assertions.assertEquals("003003oxo--x----", JEN);

        JEN = positionService.makeTurn(JEN, (byte) 1);
        Assertions.assertEquals("003003xxo--x--o-", JEN);
    }
    

        @Test
        public void testJENValidation() {
            Assertions.assertTrue(positionService.isValid("004003o-xox--xo----"), "Valid JEN should return true");

            Assertions.assertFalse(positionService.isValid("00400x"), "Too short string should return false");

            Assertions.assertFalse(positionService.isValid("00a003x-xox--xo----"), "Invalid dimension characters should return false");

            Assertions.assertFalse(positionService.isValid("000003x-xox--xo----"), "Width of 0 should return false");

            Assertions.assertFalse(positionService.isValid("004000x-xox--xo----"), "Height of 0 should return false");

            Assertions.assertFalse(positionService.isValid("128003x-xox--xo----"), "Width > 127 should return false");

            Assertions.assertFalse(positionService.isValid("004003z-xox--xo----"), "Invalid turn character should return false");

            Assertions.assertFalse(positionService.isValid("004003x-xox--xo---"), "Incorrect string length should return false");

            Assertions.assertFalse(positionService.isValid("004003x-xox--xo--*--"), "Board with invalid characters should return false");

            Assertions.assertFalse(positionService.isValid("004003x-xoo--xo----"), "x to move but o has more pieces should return false");

            Assertions.assertFalse(positionService.isValid("004003o-xxx--xo----"), "o to move but x has too many pieces should return false");
    }

}
