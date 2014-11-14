package mayday.pathway.keggview.kegg.pathway;

import java.awt.Color;

public class Graphics 
{
	public String name;
	public int x;
	public int y;
	public String type;
	public int width;
	public int height;
	public Color fgColor;
	public Color bgColor;
	
	public  Graphics()
	{		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Color getFgColor() {
		return fgColor;
	}

	public void setFgColor(Color fgColor) {
		this.fgColor = fgColor;
	}

	public Color getBgColor() {
		return bgColor;
	}

	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}
	
	public void setFgColor(String fgColor) {
		if(fgColor==null)
		{
			this.fgColor=Color.black;
			return;
		}
		try{
		int i=Integer.parseInt(fgColor.substring(1),16);
		this.fgColor =new Color(i);
		}catch(NumberFormatException e)
		{
			this.bgColor=Color.white;
		}
	}

	public void setBgColor(String bgColor) 
	{
		if(bgColor==null)
		{
			this.bgColor=Color.white;
			return;
		}
		try{
		int i=Integer.parseInt(bgColor.substring(1),16);
		this.bgColor = new Color(i);
		}catch(NumberFormatException e)
		{
			this.bgColor=Color.white;
		}
	}
	
}
