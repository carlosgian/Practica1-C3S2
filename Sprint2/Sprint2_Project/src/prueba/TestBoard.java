package prueba;

import org.junit.jupiter.api.Test;
import produccion.Board;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBoard {
    static Random random = new Random();
    static int rand = random.nextInt(8) + 3;
    public static int sideLength = rand;
    private static Board board = new Board(sideLength);

    //Criterio de aceptacion 1.1
    @Test
    public void testEmptyBoard(){
        for (int row = 0; row < sideLength; row++){
            for (int column = 0; column < sideLength; column++){
                assertEquals('-', board.getCell(row,column));
            }
        }
    }

    @Test
    public void testInvalidRow(){
        assertEquals('X', board.getCell(sideLength,1));
    }

    //Criterio de Aceptacion 1.3
    @Test
    public void testInvalidColumn(){
        assertEquals('X', board.getCell(1,sideLength));
    }

    @Test
    public void testIsGameFinishedTrue(){
        for (int row = 0; row < sideLength; row++){
            for (int column = 0; column < sideLength; column++){
                board.makePlay(row, column, 'S');
            }
        }
        assertEquals(true, board.isBoardFull());
    }

    @Test
    public void testMakePlay(){
        int row = random.nextInt(sideLength);
        int column = random.nextInt(sideLength);
        board.makePlay(row,column, 'S');
        assertEquals('S', board.getCell(row, column));
    }

    @Test
    public void testHowManySOS(){
        board.makePlay(0,0,'S');
        board.makePlay(1,1,'O');
        int row = 2 ;
        int column = 2;
        assertEquals(1, board.howManySOS(row, column, 'S'));
    }
}
