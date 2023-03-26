package org.jorigin.jis.swing;


import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jorigin.swing.IconLoader;


/**
 * This class enable to select {@link org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system} from 
 * an interactive map and some simple queries. <br/>
 * This frame is a standalone application that relies on a {@link org.arpenteur.gis.geotools.swing.JCRSSelectorPanel JCRSSelectorPanel}. 
 * @author Julien Seinturier
 */
public class JCRSSelectorFrame extends JFrame {

  private static final long serialVersionUID = 1L;
  
  JCRSSelectorPanel panel = null;
  
  /**
   * Construct a new frame. To display the frame, use {@link #showFrame()} as some processing are needed before frame is coming visible 
   * (please do not use only the {@link #setVisible(boolean) setVisible(boolean)} instead.
   */
  public JCRSSelectorFrame(){
    super("CRS Selector");
    this.panel = new JCRSSelectorPanel();
    
    Dimension size = new Dimension(800, 800);
    
    setSize(size);
    setPreferredSize(size);
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(this.panel, BorderLayout.CENTER);
    
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    setIconImage(IconLoader.getScaledIcon(getClass().getResource("/icon/jis/map_zoom_fit.png").toExternalForm(), 8.0d, 8.0d).getImage());
  }

  /**
   * Shows the {@link org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system} selection frame. 
   * Please use this method instead of the classic {@link #setVisible(boolean) setVisible(boolean)}.
   */
  public void showFrame(){
    pack();
    setVisible(true);
    this.panel.fitMap();
  }
}
