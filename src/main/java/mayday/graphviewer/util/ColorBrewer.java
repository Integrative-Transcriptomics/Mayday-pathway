package mayday.graphviewer.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class ColorBrewer {

	public static Color[] getPaletteColors(BrewerPalette palette, int numColors)
	{
		
		if(numColors > palette.maxColors)
		{
			throw new IllegalArgumentException("This color palette only supports "+palette.getMaxColors()+" colors");
		}
		Color[] res=new Color[numColors];
		try
		{
			BufferedReader r=new BufferedReader(new FileReader("/home/symons/colors.txt"));
			String line=r.readLine();			
			while(line!=null)
			{
//				System.out.println(line);
				
				String[] tok=line.split(",");
				if(tok[0].equals(palette.toString()) && tok[1].equals(""+numColors))
				{
					System.out.println(Arrays.toString(tok));
					
					for(int i=0; i!= numColors; ++i)
					{
						res[i]=new Color(Integer.parseInt(tok[i+2].trim(),16));
					}
				}
				line=r.readLine();
			}
			
		}catch(Exception e)
		{
			throw new RuntimeException("Error parsing color file", e);
		}
		return res;

	}

	public static enum BrewerPalette
	{
		Blues(9,"Shades of Blue", true, false,false),
		Greens(9,"Shades of Green", true, false,false),
		Grays(9,"Shades of Gray", true, false,false),
		Oranges(9,"Shades of Orange", true, false,false),
		Purples(9,"Shades of Purple", true, false,false),
		Reds(9,"Shades of Red", true, false,false),

		BuGn(9,"light Blue - Green", true, false,false),
		BuPu(9,"light Blue - Purple", true, false,false),
		GnBu(9,"light Green - Blue", true, true,false),
		OrRd(9,"light Orange - Red", true, false,false),
		PuBu(9,"light Purple - Blue", true, false,false),
		PuBuGn(9,"light Purple - Blue - Green", true, false,false),
		PuRd(9,"light Purple - Red", true, true,false),
		RdPu(9,"light Red - Purple", true, true,false),
		YlGn(9,"Yellow - Green ", true, false,false),
		YlGnBu(9,"Yellow - Green - Blue", true, true,false),
		YlOrBr(9,"Yellow - Orange - Brown", true, false,false),
		YlOrRd(9,"Yellow - Orange - Red", true, false,false),

		BrBG(11,"Brown - Blue/green", true, true,false),
		PiYG(11,"Pink - Yellow - Green", true, false,false),
		PRGn(11,"Purple - Yellow - Green", true, true,false),
		PuOr(11,"Orange - Purple", true, false,false), // wtf?
		RdBu(11,"Red - Blue", true, true,false),
		RdGy(11,"Red - Gray", false, true,false), 
		RdYlBu(11,"Red - Yellow - Blue", true, true,false),
		RdYlGn(11,"Red - Yellow - Green", false, true,false),
		Spectral(11,"Spectral Colors", false, true,true),

		Accent(8 ,"Accent Color Set", false, false,false),
		Dark2 (8 ,"Dark Color Set", false, true,false),
		Paired(12  ,"Paired Color Set ", false, true,false),
		Pastel1(9 ,"Pastel Color Set 1", false, false,false) ,
		Pastel2(8  ,"Pastel Color Set 2", false, false,false),
		Set1(9  ,"Color Set 1", false, true,false),
		Set2(8  ,"Color Set 2", false, true,false),
		Set3(12  ,"Color Set 3", false, true,false),


		;

		private int maxColors;
		private String description; 
		private boolean isColorBlindFriendly;
		private boolean isPrinterFriendly;
		private boolean isPhotoCopyable;


		private BrewerPalette(int maxColors, String description,boolean isColorBlindFriendly, boolean isPrinterFriendly, boolean isPhotoCopyable) 
		{
			this.maxColors = maxColors;
			this.description = description;
			this.isColorBlindFriendly = isColorBlindFriendly;
			this.isPrinterFriendly = isPrinterFriendly;
			this.isPhotoCopyable=isPhotoCopyable;
		}


		public int getMaxColors() {
			return maxColors;
		}


		public String getDescription() {
			return description;
		}


		public boolean isColorBlindFriendly() {
			return isColorBlindFriendly;
		}


		public boolean isPrinterFriendly() {
			return isPrinterFriendly;
		}


		public boolean isPhotoCopyable() {
			return isPhotoCopyable;
		}





	}


}
