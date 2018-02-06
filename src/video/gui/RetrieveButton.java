package video.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import video.handlers.FrameInteractionAssociation;

/**
 * Code written by Tiago Machado (tiago.machado@nyu.edu)
 * Date: 06/02/2018
 * @author Tiago Machado
 */

public class RetrieveButton extends JButton implements ActionListener{

	public FrameInteractionAssociation frameInteractionAssociation;
	public FrameLabel [] frameLabel;
	public ComboBox interactionComboBox;
	public String[] frames;
	
	public RetrieveButton(FrameInteractionAssociation frameInteractionAssociation, 
			ComboBox interactionComboBox, 
				FrameLabel [] frameLabel) throws FileNotFoundException, IOException, ParseException {
		super("Retrieve Frames");
		setBounds(544, 6, 130, 29);
		addActionListener(this);
		this.frameLabel = frameLabel;
		this.frameInteractionAssociation = frameInteractionAssociation;
		this.interactionComboBox = interactionComboBox;
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String interaction = interactionComboBox.getSelectedItem().toString();
		int index = interactionComboBox.getSelectedIndex();
		String tick = interactionComboBox.interaction[index].tick;
		
		System.out.println("index " + index);
		System.out.println("tick " + tick);
		
		JSONObject iteractionObject = 
				this.frameInteractionAssociation.retrieveInteraction(interaction, tick);
		
		frames = this.frameInteractionAssociation.retriveInteractionFrames(iteractionObject);
		this.frameLabel[0].updateFrame(frames[0]);
		this.frameLabel[1].updateFrame(frames[1]);
		this.frameLabel[2].updateFrame(frames[2]);
	}

}