package produccion;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SOSGameConsole {
    private SOSGameBoard board;

    ArrayList<SavedPlays> arrayOfPlays;

    public SOSGameConsole(SOSGameBoard board){
        this.board = board;
    }

    public void displayBoard(){
        for( int row = 0; row < board.getSquaresPerSide(); row++ ){
            System.out.println("-------");
            for( int column = 0; column < board.getSquaresPerSide(); column++ ){
                System.out.print("|" + toLetter(board.getBox(row, column)));
            }
            System.out.println("|");
        }
        System.out.println("-------");
    }

    public char toLetter(SOSGameBoard.Box box){
        if (box == SOSGameBoard.Box.LETTER_S) return 'S';
        else if(box == SOSGameBoard.Box.LETTER_O) return 'O';
        else return ' ';
    }

    private void printPlays(){
        for ( SavedPlays sp : arrayOfPlays){
            sp.printPlay();
        }
    }

    private void writePlaysToFile(){
        try {
            FileWriter myWriter = new FileWriter("solution.txt");
            myWriter.write(String.valueOf(board.getSquaresPerSide()));
            myWriter.write(" " + board.getGameType() + "\n");
            for ( SavedPlays sp : arrayOfPlays){
                myWriter.write(sp.returnPlay());
                if (arrayOfPlays.indexOf(sp) != arrayOfPlays.size() - 1) myWriter.write("\n");
            }
            myWriter.close();

            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void readPlaysFromFile(File playList) {
        try {
            //= new File("solution.txt");
            Scanner fileReader = new Scanner(playList);
            SOSGameBoard.Box chosen = null;
            int column = 0;
            int row = 0;
            int size = fileReader.nextInt();
            char gType = fileReader.next().charAt(0);
            board = new SOSGameBoard(size, gType);
            board.initBoard();
            while (fileReader.hasNextLine()) {
                String playerName = fileReader.next();
                row = fileReader.nextInt();
                column = fileReader.nextInt();
                char chosenChar = fileReader.next().charAt(0);
                chosen = chosenChar == 'O' ? SOSGameBoard.Box.LETTER_O : SOSGameBoard.Box.LETTER_S;
                board.makePlay(row, column, chosen);
                displayBoard();
                SOSGameBoard.Player current = playerName.equals("BluePlayer") ? board.getPlayers()[0]:board.getPlayers()[1];
                int pointsEarned = board.howManySOS(row, column, chosen);
                current.increaseScore(pointsEarned);
                System.out.println("El jugador " + current.getName() + " gano " + pointsEarned + " en este turno.");
            }
            SOSGameBoard.Player winner = board.getWinner();
            if ( board.getWinner() != null) System.out.println("El ganador es " + winner.getName() + " con " + winner.getScore() + " puntos.");
            else System.out.println("El juego termina en empate!");
            fileReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("Ocurrió un error.");
            e.printStackTrace();
        }
        //System.out.println(data1);
        //System.out.println(data2);
    }

    public void play(){
        /*This array is going to save each play as an object of the SavedPLays class*/
        //SavedPlays[] arrayOfPlays = new SavedPlays[]{};
        arrayOfPlays = new ArrayList<>();

        Scanner inRead = new Scanner(System.in);

        System.out.println("Ingrese el numero de cuadriculas por lado del tablero");
        int squareNumber = inRead.nextInt();

        /*Choosing the game mode*/
        System.out.println("Ingrese S si desea que el modo de juego sea SIMPLE");
        System.out.println("En el modo SIMPLE el ganador es el que forma el primer SOS");
        System.out.println("O ingrese G si desea que el mode de juego sea GENERAL ");
        System.out.println("En el modo GENERAL, el ganador es el que ha formado mas SOS al final.");
        char gameType = inRead.next().charAt(0);
        /*-----------------------*/

        board = new SOSGameBoard(squareNumber, gameType);

        /*Choosing the control mode for each one of the players.*/
        System.out.println("Ingrese H si desea que el jugador sea controlado por un humano, de lo contrario, ingrese C");
        System.out.println("Para el jugador 1 - BluePlayer");
        board.getPlayers()[0].setControl(inRead.next().charAt(0));
        System.out.println("Para el jugador 2 - RedPlayer");
        board.getPlayers()[1].setControl(inRead.next().charAt(0));
        /*-----------------------*/

        while(!board.isBoardFull()) {
            displayBoard();
            System.out.println("Es el turno del jugador " + board.getActivePlayer().getName() + ".");
            int row = 0, column = 0;
            SOSGameBoard.Box chosen = null;

            /*HUMAN PLAYS*/
            if (board.getActivePlayer().getControl() == 'H') {
                int[] parameters = board.humanPlay();
                row = parameters[0];
                column = parameters[1];
                chosen = parameters[2] == 0 ? SOSGameBoard.Box.LETTER_O : SOSGameBoard.Box.LETTER_S;

            /*COMPUTER PLAYS*/
            } else {
                int[] parameters = board.computerPlay();
                row = parameters[0];
                column = parameters[1];
                chosen = parameters[2] == 0 ? SOSGameBoard.Box.LETTER_O : SOSGameBoard.Box.LETTER_S;
            }

            /*End of a SIMPLE game*/
            if (board.getGameType() == 'S' && board.atLeastOneSOS(row, column, chosen)) {
                /*This is added here too to make sure we add the last play on a SIMPLE game. This method will only run once in any case, so it works fine.*/
                int pointsEarned = board.howManySOS(row, column, chosen);
                arrayOfPlays.add(new SavedPlays(board.getActivePlayer().getName(), row, column, toLetter(chosen), pointsEarned));

                displayBoard();
                System.out.println("El ganador es " + board.getActivePlayer().getName() + ". Felicidades!");
                return;
            }

            /*End of turn on a GENERAL/SIMPLE game*/
            if (board.atLeastOneSOS(row, column, chosen)) {
                int pointsEarned = board.howManySOS(row, column, chosen);
                board.getActivePlayer().increaseScore(pointsEarned);

                /*Here I save an instance of SavedPlays which contains the information of one play.*/
                arrayOfPlays.add(new SavedPlays(board.getActivePlayer().getName(), row, column, toLetter(chosen), pointsEarned));

            } else {
                /*Here I save an instance of SavedPlays which contains the information of one play. In this case, we can set the pointsEarned to 0, as there was no SOS formed.*/
                arrayOfPlays.add(new SavedPlays(board.getActivePlayer().getName(), row, column, toLetter(chosen), 0));
                board.changeActivePlayer();
            }
        }
        /*End of a GENERAL game*/
        displayBoard();
        SOSGameBoard.Player winner = board.getWinner();
        if ( board.getWinner() != null) System.out.println("El ganador es " + winner.getName() + " con " + winner.getScore() + " puntos.");
        else System.out.println("El juego termina en empate!");
    }

    public static void main(String[] args) {
        SOSGameBoard boardOne = new SOSGameBoard();
        SOSGameConsole consoleOne = new SOSGameConsole(boardOne);
        consoleOne.play();
        consoleOne.writePlaysToFile();

        File playList = new File("solution.txt");

        //SOSGameBoard boardTwo = new SOSGameBoard(4, 'G');
        System.out.println("Desea ver un replay del ultimo juego? ");
        Scanner scan = new Scanner(System.in);
        boolean flag = scan.nextBoolean();

        if(flag){
            SOSGameConsole consoleTwo = new SOSGameConsole(new SOSGameBoard());
            consoleTwo.readPlaysFromFile(playList);
        }
        else System.out.println("Hasta la próxima.");

    }
}