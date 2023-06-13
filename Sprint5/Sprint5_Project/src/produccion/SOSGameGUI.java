package produccion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SOSGameGUI extends JFrame {
    private final int HEIGHT_GAME = 650, WIDTH_GAME = 950;
    private SOSGameBoard game;
    private GameContent gameContent;
    private PlayerConfig playerBlueConfig;
    private PlayerConfig playerRedConfig;
    private JLabel turnPlayer;

    public PlayerConfig getPlayerBlueConfig() {
        return playerBlueConfig;
    }

    public SOSGameBoard getGame(){
        return game;
    }

    public SOSGameGUI(SOSGameBoard game){
        this.game = game;
        playerBlueConfig = new PlayerConfig(game.getPlayers()[0]);
        playerRedConfig = new PlayerConfig(game.getPlayers()[1]);
        setTitle("SOS game");
        this.pack();
        setSize(WIDTH_GAME, HEIGHT_GAME);
        setContentPane();
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public GameContent getGameContent() {
        return gameContent;
    }

    class GameContent extends JPanel{
        private final JRadioButton simpleGame = new JRadioButton("Simple game");
        private final JRadioButton generalGame = new JRadioButton("General game");
        private JTextField sizeBoard = new JTextField();
        private final JButton newGame = new JButton("New Game");
        private final JButton startGame = new JButton("Start Game");

        public JRadioButton getSimpleGame() {
            return simpleGame;
        }
        public JRadioButton getGeneralGame() {
            return generalGame;
        }
        private GameBoard gameBoard = new GameBoard();

        public GameBoard getGameBoard() {
            return gameBoard;
        }

        public GameContent(){
            setLayout(new BorderLayout());
            addContentTop();
            addContentPlayer(BorderLayout.LINE_START, playerBlueConfig);
            add(gameBoard, BorderLayout.CENTER);
            addContentPlayer(BorderLayout.LINE_END, playerRedConfig);
            playerRedConfig.enableTurnPlayer(false);
            addContentBottom();
        }
        private void addContentBottom(){
            JPanel panelContent = new JPanel();
            panelContent.setLayout(new BorderLayout());
            panelContent.setBorder(new EmptyBorder(20,20,20,20));
            newGame.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(sizeBoard.getText().equals("")){
                        gameBoard.drawBoard(3,3);
                    }
                    else {
                        int size = Integer.parseInt(sizeBoard.getText());
                        gameBoard.drawBoard(size,size);
                    }
                    simpleGame.setEnabled(true);
                    simpleGame.setSelected(false);
                    generalGame.setEnabled(true);
                    generalGame.setSelected(false);
                    if (playerRedConfig.getLetterS().isSelected() || playerRedConfig.getLetterO().isSelected()){
                        playerRedConfig.enableTurnPlayer(false);
                        playerBlueConfig.enableTurnPlayer(true);
                    }
                    turnPlayer.setText("Current turn: "+game.getActivePlayer().getName());
                    playerRedConfig.player.setScore(0);
                    playerBlueConfig.player.setScore(0);
                    playerBlueConfig.getComputerPlayer().setEnabled(true);
                    playerBlueConfig.getHumanPlayer().setEnabled(true);
                    playerRedConfig.getComputerPlayer().setEnabled(true);
                    playerRedConfig.getHumanPlayer().setEnabled(true);
                }
            });

            startGame.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int size;
                    if(!gameContent.getSimpleGame().isSelected() && !gameContent.getGeneralGame().isSelected()){
                        JOptionPane.showMessageDialog(null,"Seleccione el modo de juego", "Seleccione modo",JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    char gameType = simpleGame.isSelected()?'S':'G';
                    if (sizeBoard.getText().equals("")){
                        size = 0;
                        game = new SOSGameBoard(3,gameType);
                    }
                    else {
                        size = Integer.parseInt(sizeBoard.getText());
                        game = new SOSGameBoard(size,gameType);
                        gameBoard.drawBoard(size,size);
                    }
                    if(size<2 && !sizeBoard.getText().equals("")){
                        JOptionPane.showMessageDialog(null,"Digite un tamaño mayor a 2", "Tamaño del tablero",JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    if (playerBlueConfig.humanPlayer.isSelected()){
                        playerBlueConfig.computerPlayer.setEnabled(false);
                        game.getPlayers()[0].setControl('H');
                    }
                    else {
                        playerBlueConfig.humanPlayer.setEnabled(false);
                        playerBlueConfig.enableTurnPlayer(false);
                        game.getPlayers()[0].setControl('C');
                    }
                    if (playerRedConfig.humanPlayer.isSelected()){
                        playerRedConfig.computerPlayer.setEnabled(false);
                        game.getPlayers()[1].setControl('H');
                    }
                    else {
                        playerRedConfig.humanPlayer.setEnabled(false);
                        playerRedConfig.enableTurnPlayer(false);
                        game.getPlayers()[1].setControl('C');
                    }
                    int x,y;
                    if(game.getActivePlayer().getControl()=='C'){
                        x = new Random().nextInt(game.getSquaresPerSide());
                        y = new Random().nextInt(game.getSquaresPerSide());
                        simulateClick(gameContent.getGameBoard().getCasilla(x,y).getContent(),500);
                    }
                }

            });

            turnPlayer = new JLabel("Current turn: "+game.getActivePlayer().getName());
            turnPlayer.setHorizontalAlignment(JLabel.CENTER);
            panelContent.add(startGame, BorderLayout.LINE_START);
            panelContent.add(turnPlayer, BorderLayout.CENTER);
            panelContent.add(newGame, BorderLayout.LINE_END);
            add(panelContent, BorderLayout.PAGE_END);
        }
        public void simulateClick(JPanel panel, int seconds){
            Timer timer = new Timer(seconds, e -> {
                MouseEvent event = new MouseEvent(
                        panel,
                        MouseEvent.MOUSE_CLICKED,
                        System.currentTimeMillis(),
                        0,
                        0,
                        0,
                        1,
                        false
                );
                panel.dispatchEvent(event);
            });
            timer.setRepeats(false);
            timer.start();
        }
        private void addContentPlayer(String location, PlayerConfig playerConfig){
            JPanel panelContent = new JPanel();
            panelContent.setBorder(new EmptyBorder(0,50,0,50));
            panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
            panelContent.add(Box.createVerticalGlue());
            JLabel namePlayer = new JLabel(playerConfig.getName());
            namePlayer.setFont(new Font("Arial",Font.BOLD,15));
            namePlayer.setBorder(new EmptyBorder(0,0,30,0));
            panelContent.add(namePlayer);
            panelContent.add(playerConfig.getHumanPlayer());
            panelContent.add(playerConfig.getLetterS());
            panelContent.add(playerConfig.getLetterO());
            panelContent.add(playerConfig.getComputerPlayer());
            panelContent.add(Box.createVerticalGlue());
            add(panelContent, location);
        }
        private void addContentTop(){
            JPanel panelContent = new JPanel();
            panelContent.setLayout(new BorderLayout());

            JPanel panelContentLeft = new JPanel();
            panelContentLeft.add(new JLabel("SOS"));
            panelContentLeft.add(simpleGame);
            simpleGame.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(simpleGame.isSelected()){
                        simpleGame.setEnabled(false);
                        generalGame.setEnabled(false);
                        if(sizeBoard.getText().equals("")){
                            game = new SOSGameBoard(3,'S');
                        }
                        else {
                            int size = Integer.parseInt(sizeBoard.getText());
                            gameBoard.drawBoard(size,size);
                            game = new SOSGameBoard(size, 'S');
                        }
                    }
                }
            });
            panelContentLeft.add(generalGame);
            generalGame.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(generalGame.isSelected()){
                        simpleGame.setEnabled(false);
                        generalGame.setEnabled(false);
                        if(sizeBoard.getText().equals("")){
                            game = new SOSGameBoard(3,'G');
                        }
                        else {
                            int size = Integer.parseInt(sizeBoard.getText());
                            gameBoard.drawBoard(size,size);
                            game = new SOSGameBoard(size, 'G');
                        }
                    }
                }
            });

            JPanel panelContentRigth = new JPanel();
            panelContentRigth.add(new JLabel("Board size"));
            sizeBoard.setColumns(3);
            panelContentRigth.add(sizeBoard);

            panelContent.add(panelContentLeft, BorderLayout.WEST);
            panelContent.add(panelContentRigth, BorderLayout.EAST);
            add(panelContent, BorderLayout.PAGE_START);

            AbstractDocument doc = (AbstractDocument) sizeBoard.getDocument();
            doc.setDocumentFilter(new DocumentFilter() {
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                    if (newText.matches("\\d*")) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            });
        }
    }

    class PlayerConfig{
        private String name;
        private JRadioButton humanPlayer;
        private JRadioButton computerPlayer;
        private JRadioButton letterS;
        private JRadioButton letterO;
        private SOSGameBoard.Player player;

        public String getName() {
            return name;
        }
        public JRadioButton getHumanPlayer() {
            return humanPlayer;
        }
        public JRadioButton getComputerPlayer() {
            return computerPlayer;
        }

        public JRadioButton getLetterS() {
            return letterS;
        }
        public JRadioButton getLetterO() {
            return letterO;
        }
        public PlayerConfig(SOSGameBoard.Player player){
            this.player = player;
            this.name = player.getName();
            humanPlayer = new JRadioButton("Human");
            computerPlayer = new JRadioButton("Computer");
            letterS = new JRadioButton("S");
            letterS.setBorder(new EmptyBorder(20,20,5,0));
            letterO = new JRadioButton("O");
            letterO.setBorder(new EmptyBorder(5,20,20,0));
            letterS.setSelected(true);
            humanPlayer.setSelected(true);
            choseLetter();
            choseTypePlayer();
        }

        public void choseTypePlayer(){
            humanPlayer.addItemListener(e -> {
                computerPlayer.setSelected(!humanPlayer.isSelected());
            });
            computerPlayer.addItemListener(e -> {
                humanPlayer.setSelected(!computerPlayer.isSelected());
            });
        }

        public void enableTurnPlayer(boolean enable){
            letterS.setEnabled(enable);
            letterO.setEnabled(enable);
        }
        private void choseLetter(){
            letterS.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (letterS.isSelected()){
                        player.setLetter('S');
                        letterO.setSelected(false);
                    }
                    else {
                        player.setLetter('O');
                        letterO.setSelected(true);
                    }
                }
            });
            letterO.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (letterO.isSelected()){
                        player.setLetter('O');
                        letterS.setSelected(false);
                    }
                    else {
                        player.setLetter('S');
                        letterS.setSelected(true);
                    }
                }
            });
        }
    }

    class GameBoard extends JPanel{
        private JPanel board = new JPanel();
        private ArrayList <Casilla> casillas = new ArrayList<>();
        private GridBagConstraints c = new GridBagConstraints();
        public ArrayList<Casilla> getCasillas() {
            return casillas;
        }
        public GameBoard(){
            setLayout(new GridBagLayout());
            int dimBoard = WIDTH_GAME/2;
            board.setPreferredSize(new Dimension(dimBoard,dimBoard));
            drawBoard(3, 3);
            c.fill = GridBagConstraints.NONE;
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1.0;
            c.weighty = 1.0;
            c.anchor = GridBagConstraints.CENTER;
            add(board, c);
        }
        public void drawBoard(int rows, int columns){
            int pos = 0;
            casillas.clear();
            board.removeAll();
            board.setLayout(new GridLayout(rows, columns));
            for(int i=0; i<rows; i++){
                for (int j=0; j<columns; j++){
                    JPanel panelCelda = new JPanel();
                    panelCelda.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    casillas.add(new Casilla(panelCelda, i, j));
                    board.add(panelCelda);
                    addMouseListener(casillas.get(pos++));
                }
            }
            board.revalidate();
            board.repaint();
        }

        public void changeEnableTurnPLayer(){
            if(game.getActivePlayer().getName().equals(playerBlueConfig.getName())){
                playerBlueConfig.enableTurnPlayer(false);
                playerRedConfig.enableTurnPlayer(true);
            }
            else {
                playerRedConfig.enableTurnPlayer(false);
                playerBlueConfig.enableTurnPlayer(true);
            }
        }

        public Casilla getCasilla(int x, int y){
            for(Casilla casilla: casillas){
                if(casilla.getPosX()==x && casilla.getPosY()==y){
                    return casilla;
                }
            }
            return null;
        }

        public void addMouseListener(Casilla casilla) {
            casilla.getContent().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    char letterChose;
                    SOSGameBoard.Box cb;
                    if(!gameContent.getSimpleGame().isSelected() && !gameContent.getGeneralGame().isSelected()){
                        JOptionPane.showMessageDialog(null,"Seleccione el modo de juego", "Seleccione modo",JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    if(playerBlueConfig.humanPlayer.isEnabled() && playerBlueConfig.computerPlayer.isEnabled()){
                        JOptionPane.showMessageDialog(null,"Presione 'Start Game'", "Iniciar juego",JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    if(game.getBox(casilla.getPosX(), casilla.getPosY()) == SOSGameBoard.Box.EMPTY){
                        if(game.getActivePlayer().getName().equals(playerBlueConfig.getName())){
                            letterChose = playerBlueConfig.player.getLetter();
                        }
                        else {
                            letterChose = playerRedConfig.player.getLetter();
                        }
                        if(game.getActivePlayer().getControl()=='H'){
                            cb = ((letterChose=='S')?SOSGameBoard.Box.LETTER_S:SOSGameBoard.Box.LETTER_O);
                        }
                        else {
                            if(new Random().nextInt(2)==0){
                                cb = SOSGameBoard.Box.LETTER_O;
                            }
                            else {
                                cb = SOSGameBoard.Box.LETTER_S;
                            }
                            game.getActivePlayer().setLetter(cb== SOSGameBoard.Box.LETTER_S?'S':'O');
                            letterChose = game.getActivePlayer().getLetter();
                        }
                        game.makePlay(casilla.getPosX(), casilla.getPosY(), cb);
                        casilla.addContentCasilla(String.valueOf(letterChose));
                        if ( gameContent.getSimpleGame().isSelected() && game.howManySOS(casilla.posX, casilla.posY, cb) > 0 ) {
                            drawLines(game.positionSOS(casilla.getPosX(),casilla.getPosY(),cb).get(0).getLocation(),casillas);
                            JOptionPane.showMessageDialog(null,"El ganador es " + game.getActivePlayer().getName() + ". Felicidades!", "Ganador",JOptionPane.INFORMATION_MESSAGE);
                            if (game.getActivePlayer().getName().equals(playerRedConfig.getName())) {
                                playerBlueConfig.enableTurnPlayer(true);
                                playerRedConfig.enableTurnPlayer(false);
                            }
                            game = new SOSGameBoard(game.getSquaresPerSide(),'S');
                            drawBoard(game.getSquaresPerSide(), game.getSquaresPerSide());
                            turnPlayer.setText("Current turn: "+game.getActivePlayer().getName());
                            playerRedConfig.humanPlayer.setEnabled(true);
                            playerRedConfig.computerPlayer.setEnabled(true);
                            playerBlueConfig.humanPlayer.setEnabled(true);
                            playerBlueConfig.computerPlayer.setEnabled(true);
                            return;
                        }
                        if(game.atLeastOneSOS(casilla.getPosX(),casilla.getPosY(),cb)){
                            game.getActivePlayer().increaseScore(game.howManySOS(casilla.posX, casilla.posY, cb));
                        }else{
                            changeEnableTurnPLayer();
                            game.changeActivePlayer();
                        }
                        if(playerBlueConfig.computerPlayer.isSelected()){
                            playerBlueConfig.enableTurnPlayer(false);
                        }
                        if(playerRedConfig.computerPlayer.isSelected()){
                            playerRedConfig.enableTurnPlayer(false);
                        }
                        turnPlayer.setText("Current turn: "+game.getActivePlayer().getName());
                        if(game.howManySOS(casilla.getPosX(),casilla.getPosY(),cb)==1){
                            drawLines(game.positionSOS(casilla.getPosX(),casilla.getPosY(),cb).get(0).getLocation(),casillas);
                        }
                        else {
                            for (SOSGameBoard.SOSLocation location: game.positionSOS(casilla.getPosX(),casilla.getPosY(),cb)){
                                drawLines(location.getLocation(), casillas);
                            }
                            casilla.drawLinesOrientation(game.positionSOS(casilla.getPosX(),casilla.getPosY(),cb));
                        }
                    }

                    if(game.isBoardFull() && gameContent.getSimpleGame().isSelected()){
                        JOptionPane.showMessageDialog(null,"Quedaron en empate", "Empate",JOptionPane.INFORMATION_MESSAGE);
                    }
                    else if(game.isBoardFull() && gameContent.getGeneralGame().isSelected()){
                        if(game.getWinner()!=null){
                            JOptionPane.showMessageDialog(null,"El ganador es " + game.getWinner().getName() + ". con "+ game.getWinner().getScore() +" puntos", "Ganador",JOptionPane.INFORMATION_MESSAGE);
                        }
                        else {
                            JOptionPane.showMessageDialog(null,"Quedaron en empate", "Empate",JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    if(game.getActivePlayer().getControl()=='C' && !game.isBoardFull()){
                        Random rng = new Random();

                        int randomChoice = rng.nextInt(game.getNumberOfEmptyBoxes());

                        int counter = 0;

                        for (int row = 0; row <= game.getSquaresPerSide(); row++) {
                            for (int column = 0; column <= game.getSquaresPerSide(); column++) {
                                if (game.getBox(row, column) == SOSGameBoard.Box.EMPTY) {
                                    if (counter == randomChoice) {
                                        gameContent.simulateClick(gameContent.getGameBoard().getCasilla(row,column).getContent(),500);
                                        return;
                                    }
                                    counter++;
                                }
                            }
                        }
                    }
                }
            });
        }

        public void drawLines(int[] positions, ArrayList<Casilla> casillas){
            int i=positions[0],j=positions[1],cont=0;
            if((positions[0]-positions[2])==(positions[1]-positions[3])){
                if(positions[0]-positions[2]>0){
                    i=positions[2];
                    j=positions[3];
                }
                for (Casilla casilla: casillas){
                    if(casilla.getPosX()==i && casilla.getPosY()==j){
                        if (cont==3) break;
                        casilla.drawLine(game.getActivePlayer().getName(),positions);
                        i++;
                        j++;
                        cont++;
                    }
                }
                cont=0;
            } else if ((positions[0]-positions[2])==-(positions[1]-positions[3])) {
                if(positions[0]-positions[2]>0){
                    i=positions[2];
                    j=positions[3];
                }
                for (Casilla casilla: casillas){
                    if(casilla.getPosX()==i && casilla.getPosY()==j){
                        if (cont==3) break;
                        casilla.drawLine(game.getActivePlayer().getName(),positions);
                        i++;
                        j--;
                        cont++;
                    }
                }
                cont=0;
            } else if (positions[0]-positions[2]==0) {
                if (positions[1]-positions[3]>0){
                    i=positions[2];
                    j=positions[3];
                }
                for (Casilla casilla: casillas){
                    if(casilla.getPosX()==i && casilla.getPosY()==j){
                        if (cont==3) break;
                        casilla.drawLine(game.getActivePlayer().getName(),positions);
                        j++;
                        cont++;
                    }
                }
                cont=0;
            } else if (positions[1]-positions[3] == 0) {
                if (positions[0]-positions[2]>0){
                    i=positions[2];
                    j=positions[3];
                }
                for (Casilla casilla: casillas){
                    if(casilla.getPosX()==i && casilla.getPosY()==j){
                        if (cont==3) break;
                        casilla.drawLine(game.getActivePlayer().getName(),positions);
                        i++;
                        cont++;
                    }
                }
                cont=0;
            }
        }

        class Casilla{
            private JPanel content;
            private int con=2;
            private JLabel letterCasilla = new JLabel();
            private int posX,posY;
            public Casilla(JPanel content, int posX, int posY){
                this.content = content;
                this.content.setLayout(new BorderLayout());
                this.content.add(letterCasilla, BorderLayout.CENTER);
                this.posX = posX;
                this.posY = posY;
            }
            private void addContentCasilla(String letter){
                letterCasilla.setText(letter);
                letterCasilla.setHorizontalAlignment(JLabel.CENTER);
                letterCasilla.setFont(new Font("Arial",Font.BOLD,content.getHeight()));
            }

            public void drawLine(String turn, int [] positions){
                int [] orientation = lineOrientation(positions);
                Color color = ((turn.equals(playerRedConfig.getName()))?Color.RED:Color.BLUE);

                JPanel line = new JPanel(){
                    public void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setColor(color);
                        g2d.setStroke(new BasicStroke(3));
                        g2d.drawLine(orientation[0],orientation[1],orientation[2],orientation[3]);
                    }
                };
                line.setOpaque(false);
                content.add(line);
            }

            public void drawLinesOrientation(ArrayList<SOSGameBoard.SOSLocation> locations){
                Color color = game.getActivePlayer().getName().equals(playerRedConfig.getName())?Color.RED:Color.BLUE;
                JPanel line = new JPanel(){
                    public void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setColor(color);
                        g2d.setStroke(new BasicStroke(3));
                        for (SOSGameBoard.SOSLocation location: locations ){
                            int [] orientation = lineOrientation(location.getLocation());
                            g2d.drawLine(orientation[0],orientation[1],orientation[2],orientation[3]);
                        }
                    }
                };
                line.setOpaque(false);
                content.add(line);
            }
            public int[] lineOrientation(int []positions){
                int x1,y1,x2,y2;
                if((positions[0]-positions[2])==(positions[1]-positions[3])){
                    x1=0;
                    y1=0;
                    x2=content.getWidth();
                    y2=content.getHeight();
                } else if ((positions[0]-positions[2])==-(positions[1]-positions[3])) {
                    x1=content.getWidth();
                    y1=0;
                    x2=0;
                    y2=content.getHeight();
                } else if (positions[0]-positions[2]==0) {
                    x1=0;
                    y1=content.getHeight()/2;
                    x2=content.getWidth();
                    y2=content.getHeight()/2;
                } else if (positions[1]-positions[3] == 0) {
                    x1=content.getWidth()/2;
                    y1=0;
                    x2=content.getWidth()/2;
                    y2=content.getHeight();
                }
                else {
                    x1=0;
                    y1=0;
                    x2=0;
                    y2=0;
                }
                return new int[]{x1,y1,x2,y2};
            }
            public JPanel getContent() {
                return content;
            }
            public int getPosX() {
                return posX;
            }
            public int getPosY() {
                return posY;
            }
        }
    }
    private void setContentPane() {
        gameContent = new GameContent();
        this.setContentPane(gameContent);
    }

    public static void main(String[] args) {
        new SOSGameGUI(new SOSGameBoard());
    }
}