import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TetrisGame extends JFrame {

    private CardLayout  cardLayout;
    private JPanel      mainPanel;
    private GamePanel   gamePanel;
    private static final String CARD_START     = "START";
    private static final String CARD_GAME      = "GAME";
    private static final String CARD_GAME_OVER = "GAME_OVER";
    private int highScore = 0;

    public TetrisGame() {
        setTitle("Tetris Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        
        StartPanel startPanel = new StartPanel(e -> startNewGame());
        gamePanel = new GamePanel();
        
        mainPanel.add(startPanel, CARD_START);
        mainPanel.add(gamePanel,  CARD_GAME);
        add(mainPanel);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startNewGame() {
        gamePanel.initAndStart();
        cardLayout.show(mainPanel, CARD_GAME);
        gamePanel.requestFocusInWindow();
    }

    void onGameFinished(int score) {
        if (score > highScore) highScore = score;
        for (Component c : mainPanel.getComponents())
            if (CARD_GAME_OVER.equals(c.getName())) { mainPanel.remove(c); break; }
        
        GameOverPanel gop = new GameOverPanel(score, highScore, e -> startNewGame());
        gop.setName(CARD_GAME_OVER);
        mainPanel.add(gop, CARD_GAME_OVER);
        cardLayout.show(mainPanel, CARD_GAME_OVER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TetrisGame());
    }

    class StartPanel extends JPanel {
        private List<Star> stars = new ArrayList<>();
        private List<DecorationPiece> decoPieces = new ArrayList<>();
        private Random random = new Random();

        public StartPanel(ActionListener playAction) {
            setPreferredSize(new Dimension(900, 750));
            setLayout(null);
            for (int i=0;i<60;i++) stars.add(new Star(random.nextInt(900),random.nextInt(750),random.nextInt(3)+1));
            String[] types={"I","L","S","T","O","Z"};
            Color[] colors={Color.CYAN,Color.ORANGE,Color.GREEN,Color.MAGENTA,Color.YELLOW,Color.RED};
            for (int i=0;i<25;i++){
                int idx=random.nextInt(types.length);
                decoPieces.add(new DecorationPiece(random.nextInt(850),random.nextInt(700),colors[idx],types[idx]));
            }
            
            JButton btn = new JButton("PLAY");
            btn.setFont(new Font("Tahoma",Font.BOLD,45)); btn.setForeground(Color.BLACK); btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false); btn.setBorder(BorderFactory.createLineBorder(new Color(100,150,255),4));
            btn.setBounds(275,520,350,100); btn.addActionListener(playAction); add(btn);
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d=(Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setPaint(new GradientPaint(0,0,new Color(45,55,100),0,getHeight(),new Color(25,30,60)));
            g2d.fillRect(0,0,getWidth(),getHeight());
            g2d.setColor(new Color(255,255,255,20));
            for (int i=-500;i<900;i+=40) g2d.drawLine(i,0,i+750,750);
            for (Star s:stars){g2d.setColor(new Color(255,255,255,120));g2d.fillOval(s.x,s.y,s.size,s.size);}
            for (DecorationPiece p:decoPieces) drawDeco(g2d,p.x,p.y,p.color,p.type);
            String logo="TETRIS";
            Color[] lc={Color.MAGENTA,Color.RED,Color.CYAN,Color.GREEN,new Color(160,80,255),Color.ORANGE};
            g2d.setFont(new Font("Verdana",Font.BOLD,120));
            for (int i=0;i<logo.length();i++){
                g2d.setColor(lc[i].darker()); g2d.drawString(""+logo.charAt(i),135+i*110,310);
                g2d.setColor(lc[i]); g2d.drawString(""+logo.charAt(i),130+i*110,300);
            }
        }
        private void drawDeco(Graphics2D g,int x,int y,Color c,String type){
            int s=20; g.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),60));
            if(type.equals("I")){for(int i=0;i<4;i++)g.fillRect(x,y+i*s,s-2,s-2);}
            else if(type.equals("O")){g.fillRect(x,y,s-2,s-2);g.fillRect(x+s,y,s-2,s-2);g.fillRect(x,y+s,s-2,s-2);g.fillRect(x+s,y+s,s-2,s-2);}
            else{g.fillRect(x,y,s-2,s-2);g.fillRect(x+s,y,s-2,s-2);g.fillRect(x,y+s,s-2,s-2);}
        }
    }

    class GamePanel extends JPanel implements GameLoopListener {
        private static final int CELL=30, OX=302, OY=50;
        private Board board; private ScoreManager scoreManager;
        private TetrominoFactory factory; private GameLoop gameLoop;
        private boolean paused=false;
        private List<Star> stars=new ArrayList<>();
        private Random random=new Random();

        public GamePanel() {
            setPreferredSize(new Dimension(900,750)); setFocusable(true);
            for(int i=0;i<60;i++) stars.add(new Star(random.nextInt(900),random.nextInt(750),random.nextInt(3)+1));
            
            addKeyListener(new KeyAdapter(){
                @Override public void keyPressed(KeyEvent e){
                    if(gameLoop==null) return;
                    switch(e.getKeyCode()){
                        case KeyEvent.VK_LEFT:  gameLoop.onKeyLeft();         break;
                        case KeyEvent.VK_RIGHT: gameLoop.onKeyRight();        break;
                        case KeyEvent.VK_UP:    gameLoop.onKeyRotateCW();     break;
                        case KeyEvent.VK_Z:     gameLoop.onKeyRotateCCW();    break;
                        case KeyEvent.VK_DOWN:  gameLoop.onKeySoftDrop(true); break;
                        case KeyEvent.VK_SPACE: gameLoop.onKeyHardDrop();     break;
                        case KeyEvent.VK_P:     paused=!paused; gameLoop.onKeyPause(); break;
                        case KeyEvent.VK_R:     gameLoop.reset(); paused=false; break;
                    }
                }
                @Override public void keyReleased(KeyEvent e){
                    if(gameLoop!=null && e.getKeyCode()==KeyEvent.VK_DOWN) gameLoop.onKeySoftDrop(false);
                }
            });
        }

        public void initAndStart(){
            board=new Board(); scoreManager=new ScoreManager(); factory=new TetrominoFactory();
            if(gameLoop!=null) gameLoop.stop();
            gameLoop=new GameLoop(board,scoreManager,factory,this);
            gameLoop.start(); paused=false;
        }

        @Override public void onUpdate(){ repaint(); }
        @Override public void onGameOver(){
            int s=scoreManager.getScore();
            SwingUtilities.invokeLater(()->TetrisGame.this.onGameFinished(s));
        }

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2d=(Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setPaint(new GradientPaint(0,0,new Color(45,55,100),0,getHeight(),new Color(25,30,60)));
            g2d.fillRect(0,0,getWidth(),getHeight());
            g2d.setColor(new Color(255,255,255,20));
            for(int i=-500;i<900;i+=40) g2d.drawLine(i,0,i+750,750);
            for(Star s:stars){g2d.setColor(new Color(255,255,255,random.nextInt(100)+100));g2d.fillOval(s.x,s.y,s.size,s.size);}
            
            g2d.setColor(new Color(100,150,255)); g2d.setStroke(new BasicStroke(4)); g2d.drawRect(300,48,304,604);
            g2d.setColor(new Color(10,15,30)); g2d.fillRect(OX,OY,300,600);
            g2d.setColor(new Color(40,45,70)); g2d.setStroke(new BasicStroke(1));
            for(int i=1;i<10;i++) g2d.drawLine(OX+i*CELL,OY,OX+i*CELL,OY+600);
            for(int j=1;j<20;j++) g2d.drawLine(OX,OY+j*CELL,OX+300,OY+j*CELL);
           
            if(board!=null){
                for(int r=Board.HIDDEN_ROWS;r<Board.TOTAL_ROWS;r++)
                    for(int c=0;c<Board.WIDTH;c++){
                        Cell cell=board.getCell(r,c);
                        if(cell!=null&&cell.isOccupied()) drawCell(g2d,OX+c*CELL,OY+(r-Board.HIDDEN_ROWS)*CELL,cell.getColor());
                    }
            }
            
            if(gameLoop!=null&&board!=null){
                Tetromino cur=gameLoop.getCurrentPiece();
                if(cur!=null){
                    int gy=board.getGhostY(cur); int[][]sh=cur.getShape(); int px=cur.getPosition().x;
                    for(int r=0;r<sh.length;r++) for(int c=0;c<sh[r].length;c++){
                        if(sh[r][c]==0) continue;
                        int br=gy+r; if(br<Board.HIDDEN_ROWS) continue;
                        int dx=OX+(px+c)*CELL, dy=OY+(br-Board.HIDDEN_ROWS)*CELL;
                        g2d.setColor(new Color(255,255,255,40)); g2d.fillRect(dx+1,dy+1,CELL-2,CELL-2);
                    }
                }
            }
        
            if(gameLoop!=null){
                Tetromino cur=gameLoop.getCurrentPiece();
                if(cur!=null){
                    int[][]sh=cur.getShape(); int px=cur.getPosition().x,py=cur.getPosition().y; Color color=cur.getColor();
                    for(int r=0;r<sh.length;r++) for(int c=0;c<sh[r].length;c++){
                        if(sh[r][c]==0) continue;
                        int br=py+r; if(br<Board.HIDDEN_ROWS) continue;
                        drawCell(g2d,OX+(px+c)*CELL,OY+(br-Board.HIDDEN_ROWS)*CELL,color);
                    }
                }
            }
            
            drawCard(g2d,180,100,100,60,"Highscore"); 
            drawVal(g2d,183,127,94,30,String.valueOf(highScore));
            
            String lv=scoreManager!=null?String.valueOf(scoreManager.getLevel()):"1";
            String sc=scoreManager!=null?String.valueOf(scoreManager.getScore()):"0";
            String li=scoreManager!=null?String.valueOf(scoreManager.getLines()):"0";
            int startY = 200;
            drawValLbl(g2d,180,startY,100,50,"Level",lv); 
            drawValLbl(g2d,180,startY+50,100,50,"Score",sc); 
            drawValLbl(g2d,180,startY+100,100,50,"Lines",li);

            drawCard(g2d,620,100,100,120,"Next");
            if(gameLoop!=null){
                Tetromino nx=gameLoop.getNextPiece();
                if(nx!=null) drawPieceInCard(g2d,620,100,100,120,nx.getColor(),nx.getType().name());
            }

            if(paused){
                g2d.setColor(new Color(0,0,0,160));g2d.fillRect(OX,OY,300,600);
                g2d.setFont(new Font("Verdana",Font.BOLD,36));g2d.setColor(Color.WHITE);
                g2d.drawString("PAUSED",OX+55,OY+310);
            }
        }

        private void drawCell(Graphics2D g,int x,int y,Color c){
            g.setColor(c); g.fillRect(x+1,y+1,CELL-2,CELL-2);
            g.setColor(c.brighter().brighter()); g.fillRect(x+1,y+1,CELL-2,3); g.fillRect(x+1,y+1,3,CELL-2);
            g.setColor(c.darker().darker()); g.fillRect(x+1,y+CELL-3,CELL-2,2); g.fillRect(x+CELL-3,y+1,2,CELL-2);
        }
        private void drawCard(Graphics2D g,int x,int y,int w,int h,String t){
            g.setColor(Color.WHITE);g.fillRoundRect(x,y,w,h,15,15);g.setColor(new Color(10,15,30));g.fillRect(x+3,y+25,w-6,h-28);
            g.setColor(Color.BLACK);g.setFont(new Font("Tahoma",Font.BOLD,13));FontMetrics fm=g.getFontMetrics();g.drawString(t,x+(w-fm.stringWidth(t))/2,y+18);
        }
        private void drawVal(Graphics2D g,int x,int y,int w,int h,String v){
            g.setColor(new Color(30,40,70));g.fillRect(x,y,w,h);g.setColor(Color.WHITE);g.setFont(new Font("Verdana",Font.BOLD,15));
            FontMetrics fm=g.getFontMetrics();g.drawString(v,x+(w-fm.stringWidth(v))/2,y+h/2+fm.getAscent()/2-2);
        }
        private void drawValLbl(Graphics2D g,int x,int y,int w,int h,String l,String v){
            g.setColor(Color.WHITE);g.drawRect(x,y,w,h);g.fillRect(x,y,w,20);g.setColor(Color.BLACK);g.setFont(new Font("Tahoma",Font.BOLD,11));
            FontMetrics fm=g.getFontMetrics();g.drawString(l,x+(w-fm.stringWidth(l))/2,y+15);
            g.setColor(new Color(10,15,30));g.fillRect(x+2,y+22,w-4,h-24);g.setColor(Color.WHITE);g.setFont(new Font("Verdana",Font.BOLD,15));
            fm=g.getFontMetrics();g.drawString(v,x+(w-fm.stringWidth(v))/2,y+43);
        }
        private void drawPieceInCard(Graphics2D g,int cx,int cy,int cw,int ch,Color c,String type){
            int s=18; g.setColor(c);
            int pw=0, ph=0;
            switch(type){
                case"I":pw=4*s;ph=s;break;
                case"O":pw=2*s;ph=2*s;break;
                case"T":case"L":case"J":case"S":case"Z":pw=3*s;ph=2*s;break;
            }
            int x = cx + (cw - pw)/2;
            int y = cy + 25 + (ch - 25 - ph)/2;
            switch(type){
                case"I":for(int i=0;i<4;i++)g.fillRect(x+i*s,y,s-2,s-2);break;
                case"O":g.fillRect(x,y,s-2,s-2);g.fillRect(x+s,y,s-2,s-2);g.fillRect(x,y+s,s-2,s-2);g.fillRect(x+s,y+s,s-2,s-2);break;
                case"L":g.fillRect(x,y,s-2,s-2);g.fillRect(x,y+s,s-2,s-2);g.fillRect(x+s,y+s,s-2,s-2);g.fillRect(x+2*s,y+s,s-2,s-2);break;
                case"J":g.fillRect(x+2*s,y,s-2,s-2);g.fillRect(x,y+s,s-2,s-2);g.fillRect(x+s,y+s,s-2,s-2);g.fillRect(x+2*s,y+s,s-2,s-2);break;
                case"S":g.fillRect(x+s,y,s-2,s-2);g.fillRect(x+2*s,y,s-2,s-2);g.fillRect(x,y+s,s-2,s-2);g.fillRect(x+s,y+s,s-2,s-2);break;
                case"Z":g.fillRect(x,y,s-2,s-2);g.fillRect(x+s,y,s-2,s-2);g.fillRect(x+s,y+s,s-2,s-2);g.fillRect(x+2*s,y+s,s-2,s-2);break;
                case"T":g.fillRect(x+s,y,s-2,s-2);g.fillRect(x,y+s,s-2,s-2);g.fillRect(x+s,y+s,s-2,s-2);g.fillRect(x+2*s,y+s,s-2,s-2);break;
            }
        }
    }

    class GameOverPanel extends JPanel {
        private List<Star> stars=new ArrayList<>(); private List<DecorationPiece> decoPieces=new ArrayList<>();
        private Random random=new Random(); private int score,highScore;

        public GameOverPanel(int score,int highScore,ActionListener playAgainAction){
            this.score=score; this.highScore=highScore;
            setPreferredSize(new Dimension(900,750)); setLayout(null);
            for(int i=0;i<60;i++) stars.add(new Star(random.nextInt(900),random.nextInt(750),random.nextInt(3)+1));
            String[]types={"I","L","S","T","O","Z"}; Color[]colors={Color.CYAN,Color.ORANGE,Color.GREEN,Color.MAGENTA,Color.YELLOW,Color.RED};
            for(int i=0;i<25;i++){int idx=random.nextInt(types.length);decoPieces.add(new DecorationPiece(random.nextInt(850),random.nextInt(700),colors[idx],types[idx]));}
            
            JButton btn=new JButton("PLAY AGAIN"); btn.setFont(new Font("Tahoma",Font.BOLD,45)); btn.setForeground(Color.BLACK); btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false); btn.setBorder(BorderFactory.createLineBorder(new Color(100,150,255),4));
            btn.setBounds(200,520,500,100); btn.addActionListener(playAgainAction); add(btn);
        }

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g); Graphics2D g2d=(Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setPaint(new GradientPaint(0,0,new Color(45,55,100),0,getHeight(),new Color(25,30,60))); g2d.fillRect(0,0,getWidth(),getHeight());
            g2d.setColor(new Color(255,255,255,20)); for(int i=-500;i<900;i+=40)g2d.drawLine(i,0,i+750,750);
            for(Star s:stars){g2d.setColor(new Color(255,255,255,120));g2d.fillOval(s.x,s.y,s.size,s.size);}
            for(DecorationPiece p:decoPieces){int s2=20;g2d.setColor(new Color(p.color.getRed(),p.color.getGreen(),p.color.getBlue(),60));g2d.fillRect(p.x,p.y,s2-2,s2-2);g2d.fillRect(p.x+s2,p.y,s2-2,s2-2);g2d.fillRect(p.x,p.y+s2,s2-2,s2-2);}
            g2d.setFont(new Font("Verdana",Font.BOLD,100)); g2d.setColor(Color.RED.darker()); g2d.drawString("GAME OVER",105,305); g2d.setColor(Color.RED); g2d.drawString("GAME OVER",100,300);
            
            int x=350,y=340,w=200,h=150;
            g2d.setColor(Color.WHITE); g2d.fillRoundRect(x,y,w,h,15,15);
            g2d.setColor(new Color(10,15,30)); g2d.fillRect(x+5,y+35,w-10,35); g2d.fillRect(x+5,y+105,w-10,35);
            g2d.setColor(Color.BLACK); g2d.setFont(new Font("Tahoma",Font.BOLD,14)); FontMetrics fm=g2d.getFontMetrics();
            g2d.drawString("SCORE",x+(w-fm.stringWidth("SCORE"))/2,y+25);
            g2d.drawString("HIGHEST SCORE",x+(w-fm.stringWidth("HIGHEST SCORE"))/2,y+95);
            g2d.setColor(Color.WHITE); g2d.setFont(new Font("Verdana",Font.BOLD,18)); fm=g2d.getFontMetrics();
            String ss=String.valueOf(score),hs=String.valueOf(highScore);
            g2d.drawString(ss,x+(w-fm.stringWidth(ss))/2,y+60);
            g2d.drawString(hs,x+(w-fm.stringWidth(hs))/2,y+130);
        }
    }

    class DecorationPiece{int x,y;Color color;String type;DecorationPiece(int x,int y,Color c,String t){this.x=x;this.y=y;this.color=c;this.type=t;}}
    class Star{int x,y,size;Star(int x,int y,int s){this.x=x;this.y=y;this.size=s;}}
}