package mayday.pathway.keggview.kegg;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import mayday.core.gui.MaydayDialog;
import mayday.core.plugins.StartBrowser;

@SuppressWarnings("serial")
public class KEGGLicenseDialog extends MaydayDialog 
{
	public KEGGLicenseDialog() 
	{
		setTitle("KEGG License");
		JLabel topLabel=new JLabel("<html><body><b>Please note the following license terms applying to KEGG pathway <br>" +
				"and annotation data:</b></body></html>");

		JLabel centerLabel=new JLabel("<html><body>Academic users may download the KEGG data <br>" +
				"as provided at the KEGG ftp site at <br>" +
				"ftp://ftp.genome.jp/pub/kegg/. Downloading of KEGG data from <br>" +
		"any site by Non-academic users requires a license agreement.</body></html> ");


		setLayout(new BorderLayout());
		add(topLabel, BorderLayout.NORTH);
		add(centerLabel, BorderLayout.CENTER);
		add(new JButton(new KEGGWebSiteAction()),BorderLayout.SOUTH);
		setSize(400, 300);
		pack();
	}

	private class KEGGWebSiteAction extends AbstractAction
	{
		public KEGGWebSiteAction() 
		{
			super("View the KEGG Legal Website");
		}

		public void actionPerformed(ActionEvent e) 
		{	
			boolean success = false;
			try 
			{
				success = (Boolean)new StartBrowser().run(new URL("http://www.genome.jp/kegg/legal.html"));			
			} catch (Exception e1) {}
			if (!success) {
				JOptionPane.showMessageDialog((Component)null, 
						"Could not start an external program to display the license text.\n" +
						"Please consult " +
						"http://www.genome.jp/kegg/legal.html",
						"Browser could not be started",
						JOptionPane.ERROR_MESSAGE,				
						null
				);
			}


		}
	}

	public static class KEGGLicenseDialogAction extends AbstractAction
	{
		public KEGGLicenseDialogAction()
		{
			super("Pathway License");
		}

		public void actionPerformed(ActionEvent e) 
		{
			new KEGGLicenseDialog().setVisible(true);

		}
	}
}
