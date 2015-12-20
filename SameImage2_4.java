/*================================
Autore: Cocco Davide
Descrizione: SameImage elimina tutte le immagini doppione all'interno di una cartella.
			 Viene passato come argomento il numero dei file immagini da analizzare.

==================================*/
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.lang.Object.*;
import java.nio.file.*;
import java.util.Scanner;
import javax.swing.*;
import java.io.BufferedReader;
import java.awt.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;
import java.awt.event.*;
import java.nio.*;
import java.awt.Font;
import javax.swing.JProgressBar;
import java.util.Vector;

public class SameImage2_3
{

	/**< various GUI components */
	public static JFrame mainFrame = null;
	public static JTextField pathField = null;
	public static JTextField numPhotos = null;
    public static JProgressBar progressBar;
	
	
	
	
	
	
	public static JPanel initConfigurePanel()
  {
    JPanel panel = null;
	//JButton go = null;
	

    /**< create an options panel */
    JPanel configurePanel = new JPanel(new GridLayout(3, 1));
	
	
	
    /**< IP address */
	panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panel.add(new JLabel("Path folder:"));
    pathField = new JTextField(20);
    //pathField.setText(serverIP);
    pathField.setEditable(true);
    panel.add(pathField);
    configurePanel.add(panel);

    /**< port */
    //panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(new JLabel("Number Photos:"));
    numPhotos = new JTextField(10);
	
    numPhotos.setEditable(true);
    //numPhotos.setText((new Integer(port)).toString());
    panel.add(numPhotos);
    configurePanel.add(panel);

	// bottone per avvio esecuzione
	JPanel goPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	final JButton go = new JButton("Avvia");
    go.setPreferredSize(new Dimension(100, 100));    

	//go.addActionListener(p);
	goPanel.add(go);
	configurePanel.add(goPanel);
	go.addActionListener(
		new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				numPhotos.setEditable(false);
				pathField.setEnabled(false);
				go.setEnabled(false);
				compute();								
			}
		}
	);
	// barra di avanzamento
	JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	configurePanel.add(progressPanel);
	progressBar = new JProgressBar(1,100); 
	//progressBar.setMaximumSize(new Dimension(130,180));
	progressPanel.add(progressBar);
	progressBar.setStringPainted(true);
	Border border = BorderFactory.createTitledBorder("Processing...");

    return configurePanel;
  }
  

	
  
  
  
  
	public static void initGUI()
  {
    /**< set up the configure panel */
    JPanel configurePanel = initConfigurePanel();

    /**< set up the data panel (right-aligned) */
    JPanel dataPanel = new JPanel(new BorderLayout());
    //dataPanel.add(new Graph(), BorderLayout.CENTER);    grafico di palma, non serve

    /**< set up the main panel */
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(configurePanel, BorderLayout.SOUTH);
    mainPanel.add(dataPanel, BorderLayout.CENTER);

    /** set up the main frame */
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    mainFrame = new JFrame("Double Images Terminator");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setContentPane(mainPanel);
    mainFrame.setSize(mainFrame.getPreferredSize());
    mainFrame.setLocation((dim.width - mainFrame.getWidth())/2, (dim.height - mainFrame.getHeight())/3);
    mainFrame.pack();
    mainFrame.setVisible(true);
	

  }


  public static void updateBar(int newValue) {
  	int limit; 
  	limit = Integer.parseInt(numPhotos.getText());
  	double valore = (double)100/(limit-1);
    progressBar.setValue(newValue*(int)valore);
    progressBar.setBorder(border);
    progressBar.repaint();
  }

  
  public static void compute()
  {
		BufferedImage  img1 = null;
		BufferedImage img2 = null;
		String number = numPhotos.getText();
		String path_x = pathField.getText();
		int count = 0;
		//System.out.println("number = "+Integer.parseInt(number)+"\n");
		try {
				boolean flag = false;
				int num = 1;
				int fold = 1;
				
				for (int i=1; i < Integer.parseInt(number); i++)
				{					
					
					try{
						img1 = ImageIO.read(new File(path_x,"foto ("+i+").jpg"));
						}
						catch(IOException e){continue;}
					final int percent = i;
					SwingUtilities.invokeLater(new Runnable() {
          			public void run() {
            			updateBar(percent);
          				}
          			});
					//updateBar(i);
					int width1 = img1.getWidth(null);
					int height1 = img1.getHeight(null);
					for (int k=1+i; k < Integer.parseInt(number)+1; k++)
					{
						//System.out.println("i = "+i+" k = "+k+"\n");
						try{
							img2 = ImageIO.read(new File(path_x,"foto ("+k+").jpg"));
							}
						catch(IOException e) {continue;}

						int width2 = img2.getWidth(null);
						int height2 = img2.getHeight(null);
						flag = false;
						if ((width1 == width2) && (height1 == height2))
						{
							// Controllo della corrispondenza pixel
							for (int n = 0; n < height1; n++)
							{
								for (int j = 0; j < width1; j++)
								{
									int rgb1 = img1.getRGB(j, n);
									int rgb2 = img2.getRGB(j, n);
									if (rgb1 != rgb2)
									{	
										flag = true;										
										break;
									}
								}
							}
							if (!flag)
							{
								if (fold == 1)
											{
												new File(path_x,"doppioni").mkdirs();
												fold = 0;
											}
								// spostamento immagine doppia
								Path source = FileSystems.getDefault().getPath(path_x,"foto ("+k+").jpg");
								Path pathDest = FileSystems.getDefault().getPath(path_x,"doppioni");// da concatenare on il nome file
								Files.move(source,pathDest.resolve(source.getFileName()));
								//System.out.println("	Immagine n:"+k+" spostata.\n");
								count++;
																
							}
						}
					}
				}
				System.out.println("\n\nProcedura completata con successo.\n\n");
				System.out.println(""+count+" immagini spostate.\n");
			} 	catch (IOException e) 
			{
				e.printStackTrace();
			}
  }
  
  
	public static void main(String args[])
	{	
		initGUI();			
	}

}


/*invece che spostare subito salvo il numero delle doppie in un vettore e poi parte
lo spostamento.

- problemi ancora con la barra di avanzamento*/
