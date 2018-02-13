package video.gui.elements;

import javax.swing.JComboBox;

import video.basics.Interaction;

public class ComboBox<String> extends JComboBox
{
	public Interaction [] interaction;
	
	public ComboBox(Interaction [] interaction)
	{
		this.interaction = interaction;
	}
	
	public void feedThisComboBox()
	{
		for (int i = 0; i < interaction.length; i++) {
			addItem(interaction[i].pairInteractionTick);
		}
	}
}

class Item
{
	String tick;
	String interaction;
	
	public Item(String tick, String interaction)
	{
		this.tick = tick;
		this.interaction = interaction;
	}
}