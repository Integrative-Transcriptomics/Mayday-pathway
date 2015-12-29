package mayday.pathway.keggview.pathways.gui;
//package mayday.pathway.vis;
//
//import java.awt.event.ActionEvent;
//
//import javax.swing.AbstractAction;
//import javax.swing.BorderFactory;
//import javax.swing.Box;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JFileChooser;
//import javax.swing.JLabel;
//import javax.swing.JTextField;
//
//import mayday.core.MaydayDefaults;
//import mayday.core.gui.MaydayDialog;
//import mayday.graph.layout.FruchtermanReingoldLayout;
//import mayday.graph.layout.GraphLayouter;
//import mayday.pathway.pathways.graph.KEGGLayouter;
//import mayday.pathway.pathways.graph.UnifiedLayouter;
//
//@SuppressWarnings("serial")
//public class PathwayExportOptionsDialog extends MaydayDialog
//{
//	private PathwaySettings settings;
//	
//	private GraphLayouter layouter;
//	private String pathwayPath;
//	private String outputPath;
//	private boolean accepted;
//	
//	private String[] layouters={"Smart Layouter","KEGG Layouter","Fruchterman-Reingold"};
//	
//	
//	private JComboBox layouterBox;
//	private JTextField pathwayDirField;
//	private JTextField outputDirField;
//	
//	private JButton browsePathwayDirButton;
//	private JButton browseOutputDirButton;
//	
//	public PathwayExportOptionsDialog(PathwaySettings settings)
//	{
//		this.settings=settings;
//		pathwayPath=settings.getDataPath();
//		outputPath=System.getProperty("user.dir");
//		init();
//		pack();
//		setModal(true);
//	}
//	
//	private void init()
//	{
//		//init components
//		layouterBox=new JComboBox(layouters);
//		layouterBox.setSelectedIndex(0);
//		pathwayDirField=new JTextField(30);
//		pathwayDirField.setText(pathwayPath);
//		outputDirField=new JTextField(30);
//		outputDirField.setText(outputPath);
//		browseOutputDirButton=new JButton(new BrowseAction());
//		browsePathwayDirButton=new JButton(new BrowseAction());
//		
//		//layout components
//		Box  allBox=Box.createVerticalBox();
//		
//		Box graphBox=Box.createVerticalBox();
//		graphBox.setBorder(BorderFactory.createTitledBorder("Graph Settings"));
//		
//		Box settingsBox=Box.createHorizontalBox();
//		settingsBox.add(new JLabel("Pathway Rendering Settings"));
//		settingsBox.add(Box.createHorizontalStrut(10));
//		settingsBox.add(new JButton(new SettingsAction()));
//		settingsBox.add(Box.createHorizontalGlue());
//		
//		Box layoutBox=Box.createHorizontalBox();
//		layoutBox.add(new JLabel("Graph Layouter"));
//		layoutBox.add(Box.createHorizontalStrut(10));
//		layoutBox.add(layouterBox);
//		layoutBox.add(Box.createHorizontalGlue());
//		
//		graphBox.add(settingsBox);
//		graphBox.add(layoutBox);
//		
//		Box pathBox=Box.createVerticalBox();
//		pathBox.setBorder(BorderFactory.createTitledBorder("Path Settings"));
//
//		Box pathwayBox=Box.createHorizontalBox();
//		pathwayBox.add(new JLabel("Pathway Directory"));
//		pathwayBox.add(Box.createHorizontalStrut(10));
//		pathwayBox.add(pathwayDirField);
//		pathwayBox.add(Box.createHorizontalStrut(10));
//		pathwayBox.add(browsePathwayDirButton);
//		
//		Box outputBox=Box.createHorizontalBox();
//		outputBox.add(new JLabel("Output Directory"));
//		outputBox.add(Box.createHorizontalStrut(10));
//		outputBox.add(outputDirField);
//		outputBox.add(Box.createHorizontalStrut(10));
//		outputBox.add(browseOutputDirButton);
//		
//		pathBox.add(pathwayBox);
//		pathBox.add(outputBox);
//		
//		Box buttonBox=Box.createHorizontalBox();
//		JButton okButton=new JButton(new OkAction());
//		JButton cancelButton=new JButton(new CancelAction());
//		
//		buttonBox.add(Box.createHorizontalGlue());
//		buttonBox.add(okButton);
//		buttonBox.add(Box.createHorizontalStrut(5));
//		buttonBox.add(cancelButton);
//		
//		allBox.add(graphBox);
//		allBox.add(pathBox);
//		allBox.add(buttonBox);
//			
//		
//		add(allBox);		
//	}
//	
//	
//	
//	/**
//	 * @return the settings
//	 */
//	public PathwaySettings getSettings() {
//		return settings;
//	}
//
//	/**
//	 * @param settings the settings to set
//	 */
//	public void setSettings(PathwaySettings settings) {
//		this.settings = settings;
//	}
//
//	/**
//	 * @return the layouter
//	 */
//	public GraphLayouter getLayouter() {
//		return layouter;
//	}
//
//	/**
//	 * @param layouter the layouter to set
//	 */
//	public void setLayouter(GraphLayouter layouter) {
//		this.layouter = layouter;
//	}
//
//	/**
//	 * @return the pathwayPath
//	 */
//	public String getPathwayPath() {
//		return pathwayPath;
//	}
//
//	/**
//	 * @param pathwayPath the pathwayPath to set
//	 */
//	public void setPathwayPath(String pathwayPath) {
//		this.pathwayPath = pathwayPath;
//	}
//
//	/**
//	 * @return the outputPath
//	 */
//	public String getOutputPath() {
//		return outputPath;
//	}
//
//	/**
//	 * @param outputPath the outputPath to set
//	 */
//	public void setOutputPath(String outputPath) {
//		this.outputPath = outputPath;
//	}
//
//	/**
//	 * @return the accepted
//	 */
//	public boolean isAccepted() {
//		return accepted;
//	}
//
//	private void updateSettings()
//	{
//		switch (layouterBox.getSelectedIndex()) {
//		case 0:
//			layouter=new UnifiedLayouter();
//			break;
//		case 1:
//			layouter=new KEGGLayouter();
//			break;	
//		case 2:
//			layouter=new FruchtermanReingoldLayout();
//			break;
//		default:
//			break;
//		}
//		pathwayPath=pathwayDirField.getText();
//		outputPath=outputDirField.getText();
//	}
//	
//	private class OkAction extends AbstractAction
//	{
//		public OkAction()
//		{
//			super("Ok");
//		}
//		
//		public void actionPerformed(ActionEvent e) 
//		{
//			updateSettings();
//			accepted=true;
//			dispose();
//		}
//		
//	}
//
//	private class CancelAction extends AbstractAction
//	{
//		public CancelAction()
//		{
//			super("Cancel");
//		}
//		
//		public void actionPerformed(ActionEvent e) 
//		{
//			
//			accepted=false;
//			dispose();
//		}
//		
//	}
//	
//	private class BrowseAction extends AbstractAction
//	{
//		public BrowseAction() 
//		{
//			super("Browse...");
//		}
//
//		public void actionPerformed(ActionEvent e) 
//		{
//			if(e.getSource()==browseOutputDirButton)
//			{
//				JFileChooser chooser=new JFileChooser(outputDirField.getText());
//				chooser.setDialogTitle("Choose Output Directoy");
//				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
//				{
//					outputDirField.setText(chooser.getSelectedFile().toString());
//				}
//			}
//			if(e.getSource()==browsePathwayDirButton)
//			{
//				JFileChooser chooser=new JFileChooser(pathwayDirField.getText());
//				chooser.setDialogTitle("Choose Pathway Directoy");
//				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
//				{
//					pathwayDirField.setText(chooser.getSelectedFile().toString());
//				}
//			}		
//		}		
//	}
//	
//	private class SettingsAction extends AbstractAction
//	{
//		SettingsAction()
//		{
//			super("Settings...");
//		}
//
//		public void actionPerformed(ActionEvent e) 
//		{
//			PathwaySettingsDialog dialog=new PathwaySettingsDialog(settings);
//			dialog.setVisible(true);
//			if(dialog.getSettings()!=null)
//			{
//				settings=dialog.getSettings();
//				settings.store(MaydayDefaults.Prefs.getPluginPrefs());
//			}			
//		}	
//	}
//}
