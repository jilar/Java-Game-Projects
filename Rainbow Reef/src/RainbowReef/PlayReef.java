/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RainbowReef;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;


/**
 *
 * @author Jeffrey Ilar
 */
public class PlayReef{
     public static void main(String[] args){
        final RainbowReef game = new RainbowReef();
        game.init();
        final JFrame f = new JFrame("Rainbow Reef");
        f.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                f.dispose();
                System.exit(0);
            }
        });
        f.getContentPane().add("Center", game);
        f.pack();
        f.setSize(new Dimension(600, 480));
        f.setVisible(true);
        f.setResizable(true);
        game.start();
    }
 
}
