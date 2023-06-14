package prueba;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import produccion.SOSGameBoard;
import produccion.SOSGameConsole;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSOSGameConsole {

    static Random random = new Random();
    static int rand = random.nextInt(8) + 3;
    public static int sideLength = rand;
    public static char gameType = (random.nextInt(2) == 0) ? 'S' : 'G';

    public static SOSGameBoard board;

    @BeforeAll
    public static void setUp() {
        board = new SOSGameBoard(sideLength, gameType);
    }

    @Test
    public void testEmptyBoardOnConsole(){
        new SOSGameConsole(board).displayBoard();
    }

    @Test
    public void testToLetterWhenInputS() {
        assertEquals('S', new SOSGameConsole(board).toLetter(SOSGameBoard.Box.LETTER_S));
    }

    @Test
    public void testToLetterWhenInputO() {
        assertEquals('O', new SOSGameConsole(board).toLetter(SOSGameBoard.Box.LETTER_O));
    }

    @Test
    public void testToLetterWhenInputEmpty() {
        assertEquals(' ', new SOSGameConsole(board).toLetter(SOSGameBoard.Box.EMPTY));
    }
}