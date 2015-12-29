package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.math.JamaSubset.Matrix;
import mayday.core.math.JamaSubset.PCA;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.layout.GridLayouter;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.model.ProbeGraphModel;

public class PCALayout  extends CanvasLayouterPlugin
{
	public PCALayout() {}

	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		List<Probe> probeList=new ArrayList<Probe>();
		ProbeGraphModel m2=null;
		if(model instanceof ProbeGraphModel)
		{
			
			m2=((ProbeGraphModel)model);
			probeList.addAll(m2.getProbes());
		}else
		{
			new GridLayouter().layout(container,bounds,model);
			return;
		}
		
		int n=probeList.size();
		int m = probeList.get(0).getNumberOfExperiments();

		double[][]indat = new double[n][m];
		
		try {

			int i=0;
			for (Probe tmp : probeList) 
			{ 
				for (int j=0; j!=m; ++j) 
				{
					indat[i][j] = tmp.getValue(j);
				}
				++i;
			}
		} catch (NullPointerException e){
			throw new RuntimeException("Cannot work on Probes containing missing values");
		}
		

		PCA pca = new PCA(indat);
		Matrix pcaM=pca.getResult();
		

		double min1=Double.MAX_VALUE;
		double max1=Double.MIN_VALUE;
		double min2=Double.MAX_VALUE;
		double max2=Double.MIN_VALUE;
		
		for(int i=0; i!=pcaM.getRowDimension(); ++i)
		{
			min1=Math.min(pcaM.get(i, 0), min1);
			min2=Math.min(pcaM.get(i, 1), min2);			
			max1=Math.max(pcaM.get(i, 0), max1);
			max2=Math.max(pcaM.get(i, 1), max2);
		}
		int i=0;
		for(Probe p:probeList)
		{
			double x= (pcaM.get(i, 0)-min1) / (max1-min1) * bounds.getWidth();
			double y= (-pcaM.get(i, 1)-min2) / (max2-min2) * bounds.getHeight();
			for(CanvasComponent comp: m2.getComponents(p))
			{
				comp.setLocation((int) x, (int)y );
			}
			
			++i;
		}
//		new AlignLayouter(50).layout(container, bounds, model);
	}


	@Override
	protected void initSetting() 
	{

	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.PCA",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Use the first two principal components of the probes to place the compontents",
				"PCA Layout"				
		);
		return pli;	
	}

}
