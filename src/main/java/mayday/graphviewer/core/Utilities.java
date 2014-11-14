package mayday.graphviewer.core;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import mayday.core.MaydayDefaults;
import mayday.core.Preferences;

public abstract class Utilities 
{
	// constants for nice file dialogs:
	public static final String LAST_GRAPH_SAVE_DIR="AnacondaLastGraphSaveDir";
	public static final String LAST_GRAPH_EXPORT_DIR="AnacondaLastGraphExportDir";
	public static final String LAST_GRAPH_IMPORT_DIR="AnacondaLastGraphImportDir";
	public static final String LAST_RESOURCE_DIR="AnacondaLastResourceDir";
	
    public static final Preferences prefs = MaydayDefaults.Prefs.getPluginPrefs().node("AnacondaGraphViewer"); 
	
	public static double[] seriesOver(int num, double w)
	{
		if(num==0)
			return new double[]{};		
		if(num==1)
			return new double[]{w/2.0};
		double dn=w/(num-1.0);
		double[] res=new double[num];
		res[0]=0;                        
		for(int i=1; i!=num;++i)
		{
			res[i]=(1.0*i)*dn;
		}
		return res;
	}
	
	public static Point2D getIntersectionPoint(Line2D line1, Line2D line2) 
	{
		if (! line1.intersectsLine(line2) ) 
			return null;
		double px = line1.getX1();
		double py = line1.getY1();
		double rx = line1.getX2()-px;
		double ry = line1.getY2()-py;
		double qx = line2.getX1();
		double qy = line2.getY1();
		double sx = line2.getX2()-qx;
		double sy = line2.getY2()-qy;

		double det = sx*ry - sy*rx;
		if (det == 0) 
		{
			return null;
		}else 
		{
			double z = (sx*(qy-py)+sy*(px-qx))/det;
			if (z==0 ||  z==1) 
				return null;  // intersection at end point!
			return new Point2D.Double((float)(px+z*rx), (float)(py+z*ry));
		}
	}
	
	
	
	/**
	 * Quantile function of the normal distribution 
	 * @param p A probability p
	 * @return the corresponding quantile.
	 * 
	 * 
	 * 
	 * 	** * @(#)qnorm.js * * Copyright (c) 2000 by Sundar Dorai-Raj
	  * * @author Sundar Dorai-Raj
	  * * Email: sdoraira@vt.edu
	  * * This program is free software; you can redistribute it and/or
	  * * modify it under the terms of the GNU General Public License 
	  * * as published by the Free Software Foundation; either version 2 
	  * * of the License, or (at your option) any later version, 
	  * * provided that any use properly credits the author. 
	  * * This program is distributed in the hope that it will be useful,
	  * * but WITHOUT ANY WARRANTY; without even the implied warranty of
	  * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	  * * GNU General Public License for more details at http://www.gnu.org * * 
	 */
	public static double qnorm(double p)
	{
		// ALGORITHM AS 111, APPL.STATIST., VOL.26, 118-121, 1977.
	    // Computes z=invNorm(p)
	    double split=0.42;
	    double a0=  2.50662823884;
	    double a1=-18.61500062529;
	    double a2= 41.39119773534;
	    double a3=-25.44106049637;
	    double b1= -8.47351093090;
	    double b2= 23.08336743743;
	    double b3=-21.06224101826;
	    double b4=  3.13082909833;
	    double c0= -2.78718931138;
	    double c1= -2.29796479134;
	    double c2=  4.85014127135;
	    double c3=  2.32121276858;
	    double d1=  3.54388924762;
	    double d2=  1.63706781897;
	    double q=p-0.5;
	    double ppnd;
	    if(Math.abs(q)<=split) {
	    	double r=q*q;
	    	ppnd=q*(((a3*r+a2)*r+a1)*r+a0)/((((b4*r+b3)*r+b2)*r+b1)*r+1);
	    }
	    else {
	    	double r=p;
	      if(q>0) r=1-p;
	      if(r>0) {
	        r=Math.sqrt(-Math.log(r));
	        ppnd=(((c3*r+c2)*r+c1)*r+c0)/((d2*r+d1)*r+1);
	        if(q<0) ppnd=-ppnd;
	      }
	      else {
	        ppnd=0;
	      }
	    }
	    return(ppnd);
	}
}
