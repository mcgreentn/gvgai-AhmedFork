package video.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;

/**
 * Code written by Tiago Machado (tiago.machado@nyu.edu)
 * Date: 06/02/2018
 * @author Tiago Machado
 */

public abstract class Utils 
{
	public static void feedComboBox(JComboBox<String> comboBox, ArrayList<String> strings)
	{
		Set<String> hs = new HashSet<>();
		hs.addAll(strings);
		for (String string : hs) 
		{
			comboBox.addItem(string);
		}
	}

	public static void feedComboBox(JComboBox<String> cbx, String [] strings)
	{
		for (String string : strings) 
		{
			cbx.addItem(string);
		}
	}
}