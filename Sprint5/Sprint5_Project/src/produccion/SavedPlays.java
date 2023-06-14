package produccion;

public class SavedPlays {
    private String player_name;
    private int row;
    private int column;
    private char symbol;
    private int pointsEarned;

    SavedPlays(String player_name, int row, int column, char symbol, int pointsEarned){
        this.player_name = player_name;
        this.row = row;
        this.column = column;
        this.symbol = symbol;
        this.pointsEarned = pointsEarned;
    }

    public void printPlay(){
        System.out.println(player_name + " " + row + " " + column + " " + symbol + " " + pointsEarned);
    }

    public String returnPlay(){
        return player_name + " " + row + " " + column + " " + symbol;
    }
}