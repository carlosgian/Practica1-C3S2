import org.junit.jupiter.api.Test;
import produccion.SOSGameBoard;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSOSGameBoard {
    static Random random = new Random();
    static int rand = random.nextInt(8) + 3;
    public static int sideLength = rand;

    public static char gameType = random.nextInt(2) == 0 ? 'S':'G';
    private static SOSGameBoard board = new SOSGameBoard(sideLength, gameType);

    //Criterio de aceptacion 1.1
    @Test
    public void testEmptyBoard(){
        for (int row = 0; row < sideLength; row++){
            for (int column = 0; column < sideLength; column++){
                assertEquals(SOSGameBoard.Box.EMPTY, board.getBox(row,column));
            }
        }
    }

    @Test
    public void testInvalidRow(){
        assertEquals(null, board.getBox(sideLength,1));
    }

    //Criterio de Aceptacion 1.3
    @Test
    public void testInvalidColumn(){
        assertEquals(null, board.getBox(1,sideLength));
    }

    @Test void testGetNumberOfEmptyBoxes(){
        assertEquals((sideLength * sideLength) - 1, board.getNumberOfEmptyBoxes());
    }

    @Test
    public void testIsGameFinishedTrue(){
        for (int row = 0; row < sideLength; row++){
            for (int column = 0; column < sideLength; column++){
                board.makePlay(row, column, SOSGameBoard.Box.LETTER_S);
            }
        }
        assertEquals(true, board.isBoardFull());
    }

    @Test
    public void testMakePlay(){
        int row = random.nextInt(sideLength);
        int column = random.nextInt(sideLength);
        board.makePlay(row,column, SOSGameBoard.Box.LETTER_S);
        assertEquals(SOSGameBoard.Box.LETTER_S, board.getBox(row, column));
    }

    @Test
    public void testComputerPlay(){
        int[] parameters = board.computerPlay();

        int row = parameters[0];
        int column = parameters[1];
        SOSGameBoard.Box chosen = parameters[2] == 0 ? SOSGameBoard.Box.LETTER_O : SOSGameBoard.Box.LETTER_S;

        SOSGameBoard tempBoard = new SOSGameBoard(sideLength, gameType);
        tempBoard.makePlay(row, column, chosen);

        assertEquals(board.getBox(row, column), tempBoard.getBox(row, column));
    }

    @Test
    public void testHowManySOS(){
        board.makePlay(0,0, SOSGameBoard.Box.LETTER_S);
        board.makePlay(1,1, SOSGameBoard.Box.LETTER_O);
        int row = 2 ;
        int column = 2;
        assertEquals(1, board.howManySOS(row, column, SOSGameBoard.Box.LETTER_S));
    }
}
