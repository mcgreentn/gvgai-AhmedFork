package video.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;

public abstract class Utils 
{
	public static void feedComboBox(JComboBox<String> cbx, ArrayList<String> strings)
	{
		List<String> al = strings;
		// add elements to al, including duplicates
		Set<String> hs = new HashSet<>();
		hs.addAll(al);
		al.clear();
		al.addAll(hs);
		Collections.sort(al);
		for (String string : al) 
		{
			cbx.addItem(string);
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
