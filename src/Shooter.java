import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;
import javax.swing.*;

public class Shooter extends JPanel implements ActionListener, KeyListener{

    class Block{
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive =true;  // for aliens
        boolean used = false;  //for bullets

        Block(int x, int y, int width, int height, Image img){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        } 
    }

    //board
    int tileSize=32;
    int rows =16;
    int columns=16;
    int boardWidth = tileSize*columns;
    int boardHeight = tileSize*rows;

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    //ship
    int shipWidth = tileSize*2;
    int shipHeight = tileSize;
    int shipX = boardWidth/2-shipWidth/2;
    int shipY = boardHeight-shipHeight*2;
    int shipVelocityX = tileSize; //speed of ship
    Block ship;

    //aliens
    ArrayList<Block> alienArray;
    int alienWidth = tileSize*2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0;
    int alienVelocityX = 1;

    //bullets
    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize/8;
    int bulletHeight = tileSize/2;
    int bulletVelocityY = -10; //bullet speed

    Timer gameLoop;
    int score =0;
    boolean gameOver = false;

    Shooter(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        //load images
        shipImg = new ImageIcon("src/images/ship.png").getImage();
        alienImg = new ImageIcon("src/images/alien.png").getImage();
        alienCyanImg = new ImageIcon("src/images/alien-cyan.png").getImage();
        alienMagentaImg = new ImageIcon("src/images/alien-magenta.png").getImage();
        alienYellowImg = new ImageIcon("src/images/alien-yellow.png").getImage();

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX,shipY,shipWidth,shipHeight,shipImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();

        createaliens();
        //game timer
        gameLoop = new Timer(1000/60,this);
        gameLoop.start(); 

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //draw ship 
        g.drawImage(ship.img,ship.x,ship.y,ship.width,ship.height,null);

        //aliens
        for(int i=0;i<alienArray.size();i++){
            Block alien = alienArray.get(i);
            if(alien.alive){
                g.drawImage(alien.img,alien.x,alien.y,alien.width,alien.height,null);
            }
        }

        //bullets
        g.setColor(Color.WHITE);
        for(int i=0;i<bulletArray.size();i++){
            Block bullet = bulletArray.get(i);
            if(!bullet.used){
                g.drawRect(bullet.x,bullet.y,bullet.width,bullet.height);
            }
        }

        //score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.PLAIN,20));
        if(gameOver){
            g.drawString("Game Over : "+String.valueOf(score), 10,35);
        }
        else{
            g.drawString("Score : "+String.valueOf(score), 10,35);
        } 
    }

    public void move(){
        //aliens
        for(int i=0;i<alienArray.size();i++){
            Block alien  = alienArray.get(i);
            if(alien.alive){
                alien.x += alienVelocityX;
                }

                //touch with boarder
                if(alien.x+alienWidth >= boardWidth || alien.x <=0){
                    alienVelocityX = -alienVelocityX;

                    for(int j=0;j<alienArray.size();j++){
                       alienArray.get(j).y += alienHeight;
                    }
                }

                if(alien.y >= ship.y){
                    gameOver = true;
                }
            }

            //bullets
            for(int i=0;i<bulletArray.size();i++){
                Block bullet = bulletArray.get(i);
                    bullet.y += bulletVelocityY;

                    //bullet collision with aliens
                    for(int j =0;j<alienArray.size();j++){
                        Block alien = alienArray.get(j);
                        if(alien.alive && detectCollision(bullet,alien)){
                            bullet.used = true;
                            alien.alive = false;
                            alienCount--;
                            score+=100;
                    }

                }
            }

            //clear bullets
            while(bulletArray.size()>0 && (bulletArray.get(0).used || bulletArray.get(0).y<0)){
                bulletArray.remove(0);
            }

            //next level
            if(alienCount==0){
                score += alienColumns*alienRows;  //bonus score
                alienColumns = Math.min(alienColumns+1, columns/2-2);
                alienRows = Math.min(alienRows+1, rows-6);
                alienArray.clear();
                bulletArray.clear();
                alienVelocityX=1;
                createaliens();
            }
        }

    public void createaliens(){
        Random random = new Random();
        for(int r=0;r<alienRows;r++){
            for(int c=0;c<alienColumns;c++){
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                    alienX+c*alienWidth,
                    alienY+r*alienHeight,
                    alienWidth,
                    alienHeight,
                    alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);

            }
        }
        alienCount=alienArray.size();
    }

    public boolean detectCollision(Block a, Block b){
        return a.x < b.x + b.width && 
               a.x + a.width > b.x && 
               a.y < b.y + b.height && 
               a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       move();
       repaint();
       if(gameOver){
        gameLoop.stop();
       }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(gameOver){// any key to restart
            ship.x=shipX;
            alienArray.clear();
            bulletArray.clear();
            score=0;
            alienVelocityX=1;
            alienColumns=3;
            alienRows=2;
            gameOver=false;
            createaliens();
            gameLoop.start();
        }
       else if(e.getKeyCode() == KeyEvent.VK_LEFT && ship.x -shipVelocityX >= 0){
           ship.x -= shipVelocityX;
       }
       else if(e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x+ship.width+shipVelocityX <= boardWidth){
              ship.x += shipVelocityX;
         }
       else if(e.getKeyCode()==KeyEvent.VK_SPACE){
              Block bullet = new Block(ship.x + ship.width*15/32, ship.y, bulletWidth, bulletHeight, null);
              bulletArray.add(bullet);
       }
    }
}
