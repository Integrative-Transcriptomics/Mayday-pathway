package mayday.graphviewer.illumina;

import mayday.graphviewer.illumina.GeneModelLayout.ScalingStyle;

public interface IGeneModelLayout 
{
	public void setExonMinimumSize(int m);
	public void setScalingStyle(ScalingStyle style);
	public void setExonBaseHeight(int h);
	public void setExonScaling(boolean h);
	
}
