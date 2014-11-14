package mayday.pathway.biopax.parser;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.core.gui.MaydayDialog;

@SuppressWarnings("serial")
public class CycDatParserDialog extends MaydayDialog
{
	private JComboBox sourceBox;
	private JComboBox targetBox;
	
	private String source;
	private String target;
	
	private boolean accept=false;
	
	public CycDatParserDialog(List<String> attributes) 
	{
		sourceBox=new JComboBox();
		targetBox=new JComboBox();
		for(String s:attributes)
		{
			sourceBox.addItem(s);
			targetBox.addItem(s);
		}
		
		JLabel sourceLabel=new JLabel("Key");
		JLabel targetLabel=new JLabel("Value");
		
		JButton okButton=new JButton(new OkAction());
		JButton cancelButton=new JButton(new CancelAction());
		
		JPanel panel=new JPanel(new GridLayout(2,2));
		
		panel.add(sourceLabel);
		panel.add(sourceBox);
		panel.add(targetLabel);
		panel.add(targetBox);
		
		setLayout(new BorderLayout());
		add(panel,BorderLayout.CENTER);
		
		Box buttonBox=Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(cancelButton);
		buttonBox.add(Box.createHorizontalStrut(15));
		buttonBox.add(okButton);
		
		add(buttonBox,BorderLayout.SOUTH);
		setModal(true);
		pack();
	}
	
	
	
	public static void main(String[] args) throws Exception
	{
		List<String> atts=CycDatParser.parseAttributes("/home/symons/data/ScoCyc/scoe100226cyc-flatfiles/proteins.dat");
		CycDatParserDialog cdpd=new CycDatParserDialog(atts);
		cdpd.setVisible(true);
		CycDatParser.parseMapping("/home/symons/data/ScoCyc/scoe100226cyc-flatfiles/proteins.dat", cdpd.getSource(), cdpd.getTarget());
	}
	
	private class OkAction extends AbstractAction
	{
		public OkAction() {
			super("Ok");
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			source=(String)sourceBox.getSelectedItem();			
			target=(String)targetBox.getSelectedItem();
			accept=true;
			dispose();
		} 
	}
	
	private class CancelAction extends AbstractAction
	{
		public CancelAction() {
			super("Cancel");
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			accept=false;
			dispose();
		} 
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}



	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}



	/**
	 * @return the accept
	 */
	public boolean isAccept() {
		return accept;
	}
}
