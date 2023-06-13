package produccion;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class SOSGameBoard {
    public enum Box {EMPTY, LETTER_S, LETTER_O}

    private int squaresPerSide;

    private Player[] players = new Player[2];

    //Turn -> activePlayer
    private Player activePlayer;

    private char gameType;

    private Box[][] grid;

    public SOSGameBoard(){
        squaresPerSide = 3;
        grid = new Box[squaresPerSide][squaresPerSide];
        gameType = 'S';
        initBoard();
    }

    public SOSGameBoard(int squaresPerSide, char gameType){
        this.squaresPerSide = squaresPerSide;
        this.gameType = gameType;
        grid = new Box[squaresPerSide][squaresPerSide];
        initBoard();
    }

    public void initBoard(){
        for( int row = 0; row < squaresPerSide; row++ ){
            for ( int column = 0; column < squaresPerSide; column++){
                grid[row][column] = Box.EMPTY;
            }
        }
        players[0] = new Player("BluePlayer");
        players[1] = new Player("RedPlayer");
        activePlayer = players[0];
    }

    public int getSquaresPerSide() { return squaresPerSide; }

    public boolean isPositionInGrid(int row, int column){
        return (row >= 0 && row < squaresPerSide && column >= 0 && column < squaresPerSide);
    }

    public Box getBox(int row, int column){
        if ( isPositionInGrid(row, column) ) return grid[row][column];
        else {
            return null;
        }
    }

    public Player getActivePlayer(){ return activePlayer; }

    public char getGameType(){ return gameType; }

    public Player[] getPlayers(){ return players; }

    public void makePlay(int row, int column, Box chosen){
        if ( isPositionInGrid(row, column) ) grid[row][column] = chosen;
        else {
            throw new IllegalArgumentException("El argumento debe estar dentro de los limited del tablero");
        }
    }

    public int[] humanPlay(){
        Scanner inRead = new Scanner(System.in);
        int row, column;
        do {
            System.out.println("Ingrese la fila de su jugada: ");
            row = inRead.nextInt();

            System.out.println("Ingrese la columna de su jugada");
            column = inRead.nextInt();

            if (getBox(row, column) == null || getBox(row, column) != Box.EMPTY) {
                System.out.println("Ingrese numeros validos");
            }
        } while (getBox(row, column) == null || getBox(row, column) != Box.EMPTY);

        System.out.println("Ingrese el caracter que desea ingresar, sea S o O");
        char chosenChar = inRead.next().charAt(0);
        Box chosen = chosenChar == 'S' ? Box.LETTER_S : Box.LETTER_O;
        makePlay(row, column, chosen);
        int chosenInt = chosenChar == 'O' ? 0:1;
        int[] parameters = new int[]{row, column, chosenInt};
        return parameters;
    }

    public int getNumberOfEmptyBoxes(){
        int counter = 0;
        for ( int row = 0; row <= squaresPerSide; row++ ){
            for ( int column = 0; column <=squaresPerSide; column++ ){
                if(getBox(row, column) == Box.EMPTY) counter++;
            }
        }
        return counter;
    }
    public int[] computerPlay() {
        int numberOfEmptyBoxes = getNumberOfEmptyBoxes();

        Random rng = new Random();

        int randomChoice = rng.nextInt(numberOfEmptyBoxes);

        int chosenInt = rng.nextInt(2);
        Box chosen = chosenInt == 0 ? Box.LETTER_O : Box.LETTER_S;

        int counter = 0;

        int[] parameters = new int[0];
        for (int row = 0; row <= squaresPerSide; row++) {
            for (int column = 0; column <= squaresPerSide; column++) {
                if (getBox(row, column) == Box.EMPTY) {
                    if (counter == randomChoice) {
                        makePlay(row, column, chosen);
                        parameters = new int[]{row, column, chosenInt};
                    }
                    counter++;
                }
            }
        }
        return parameters;
    }

    public void changeActivePlayer(){
        activePlayer = ( getActivePlayer() == players[0] ) ? players[1]:players[0];
    }

    /* The next 2 methods are the logic used in the SOS search.
    It takes a row and a column which indicate the position of the last
    letter put into the grid, then uses a duple as the direction
    of search.  */
    boolean detectSOSWhenS(int row, int column, int[] duple){
        return ( getBox(row + duple[0], column + duple[1]) == Box.LETTER_O && getBox(row + 2 * duple[0], column + 2 * duple[1]) == Box.LETTER_S);
    }

    boolean detectSOSWhenO(int row, int column, int[] duple){
        return ( getBox(row + duple[0], column + duple[1]) == Box.LETTER_S && getBox(row - duple[0], column - duple[1]) == Box.LETTER_S);
    }

    public int howManySOS(int row, int column, Box chosen){
        int[][] around = new int[][]{ {-1,-1} , {0,-1} , {1,-1} , {-1,0} , {1,0} , {-1,1} , {0,1} , {1,1} } ;
        int points = 0;
        if ( chosen == Box.LETTER_S ){
            for( int i = 0; i < 8; i++ ){
                if( detectSOSWhenS(row, column, around[i]) ){
                    points++;
                }
            }
        }
        else if ( chosen == Box.LETTER_O ){
            for( int i = 0; i < 4; i++ ){
                if( detectSOSWhenO(row, column, around[i]) ){
                    points++;
                }
            }
        }
        return points;
    }

    public boolean atLeastOneSOS(int row, int column, Box chosen){
        return howManySOS(row, column, chosen) > 0;
    }

    /*The position of a SOS is defined by the position of the
    starting and finishing letters, this amounts to 4 numbers, 2 for
    the starting coordinates and two for the finishing ones.
    These are the numbers that are in each row of the matrix positions.
    */
    public ArrayList<SOSLocation> positionSOS(int row, int column, Box chosen) {
        int[][] around = new int[][]{{-1, -1}, {0, -1}, {1, -1}, {-1, 0}, {1, 0}, {-1, 1}, {0, 1}, {1, 1}};
        ArrayList<SOSLocation> positions = new ArrayList<>();

        if (chosen == Box.LETTER_S) {
            for (int i = 0; i < 8; i++) {
                if ( detectSOSWhenS(row, column, around[i]) ) {
                    positions.add(new SOSLocation(row,column,row+2*around[i][0],column+2*around[i][1]));
                }
            }
        } else if (chosen == Box.LETTER_O) {
            for (int i = 0; i < 4; i++) {
                if ( detectSOSWhenO(row, column, around[i]) ) {
                    positions.add(new SOSLocation(row - around[i][0],column - around[i][1],row + around[i][0],column + around[i][1]));
                }
            }
        }
        return positions;
    }


    public boolean isBoardFull() {
        for (int row = 0; row < squaresPerSide; row++ ){
            for ( int column = 0; column < squaresPerSide; column++ ){
                if (getBox(row, column) == Box.EMPTY) return false;
            }
        }
        return true;
    }

    public Player getWinner(){
        if ( players[0].getScore() > players[1].getScore()){
            return players[0];
        }
        else if ( players[0].getScore() < players[1].getScore() ){
            return players[1];
        }
        else return null;
    }

    class SOSLocation{
        private int [] location = new int[4];
        public SOSLocation(int x1, int y1, int x2, int y2){
            location[0] = x1;
            location[1] = y1;
            location[2] = x2;
            location[3] = y2;
        }
        public int[] getLocation() {
            return location;
        }
    }

    class Player{
        private String name;
        private int score;
        private char letter;
        private char control;

        public Player(String name){
            this.name = name;
            score = 0;
            letter = 'S';
        }

        public char getControl(){ return control; }

        public void setControl(char control){ this.control = control; }

        public void setLetter(char letter){
            this.letter = letter;
        }

        public char getLetter(){
            return letter;
        }

        public int getScore(){
            return score;
        }

        public void increaseScore(int points){
            score += points;
        }

        public String getName(){
            return name;
        }

        public void setScore(int score){
            this.score = score;
        }

    }
}
