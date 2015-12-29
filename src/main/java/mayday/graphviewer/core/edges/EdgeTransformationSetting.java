package mayday.graphviewer.core.edges;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;

public class EdgeTransformationSetting extends HierarchicalSetting implements SettingChangeListener
{
	private BooleanSetting invert=new BooleanSetting("Invert", "Invert the transformation", false);
	private DoubleSetting maximum=new DoubleSetting("Maximum", "Don't draw the edge thicker than this value" , 20.0, 0.0, 100.0, true, true);
	private DoubleSetting magnification=new DoubleSetting("Magnification", "Factor for maginifying the edge\\" +
			"only active if scale transform is used", 10);

	private EdgeWeightTransformation[] predef={
			new EmptyEdgeWeightTransformation(), 
			new LogEdgeWeightTransformation(2.0f),
			new LogEdgeWeightTransformation(10.0f),
			new MagnificationEdgeWeightTransformation()
			};

	private ObjectSelectionSetting<EdgeWeightTransformation> transform=new ObjectSelectionSetting<EdgeWeightTransformation>(
			"Transformation Method",null,0,predef);

	private EdgeWeightTransformation transformation;

	public EdgeTransformationSetting() 
	{
		super("Edge Transformation");		
		this.addSetting(transform).addSetting(invert).addSetting(maximum).addSetting(magnification);

		addChangeListener(this);
		stateChanged(null);
	}

	@Override
	public void stateChanged(SettingChangeEvent e) 
	{
		transformation=transform.getObjectValue();
		transformation.setMax(maximum.getDoubleValue());
		transformation.setMagnification(magnification.getDoubleValue());
		transformation.setInvert(invert.getBooleanValue());
	}
	
	public EdgeWeightTransformation getTransformation() {
		return transformation;
	}

	@Override
	public EdgeTransformationSetting clone() 
	{
		EdgeTransformationSetting cpy=new EdgeTransformationSetting();
		cpy.fromPrefNode(this.toPrefNode());
		return cpy; 
	}

}
