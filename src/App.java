import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
       
       int tileSize =32;
       int rows =16;
       int columns =16;
       int boardWidth = tileSize*columns;
       int boardHeight = tileSize*rows;

       JFrame frame = new JFrame("Shooter");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(boardWidth,boardHeight);
       frame.setResizable(false);
       frame.setLocationRelativeTo(null);

       Shooter shooter = new Shooter();
       frame.add(shooter);
       frame.pack();
       shooter.requestFocus();
       frame.setVisible(true);
       

    }
}
