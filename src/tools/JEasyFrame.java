package tools;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;

import com.sun.glass.events.WindowEvent;

import core.competition.CompetitionParameters;

/**
 * Frame for the graphics.
 * PTSP-Competition
 * Created by Diego Perez, University of Essex.
 * Date: 20/12/11
 */
public class JEasyFrame extends JFrame {

    /**
     * Main component of the frame.
     */
    public Component comp;

    /**
     * Constructor
     * @param comp Main component of the frame.
     * @param title Title of the window.
     */
    public JEasyFrame(Component comp, String title) {
        super(title);
        this.comp = comp;
        getContentPane().add(BorderLayout.CENTER, comp);
        pack();
        this.setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        if(CompetitionParameters.closeAppOnClosingWindow){
        	setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
        repaint();
    }
    
    public void closeWindow()
    {
    	dispose();
    }

    /**
     * Closes this component.
     */
    public void quit()
    {
        System.exit(0);
    }
}

