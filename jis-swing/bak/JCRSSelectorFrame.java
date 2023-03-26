package org.arpenteur.gis.geotools.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.arpenteur.common.ihm.icon.IconServer;
import org.arpenteur.photogrammetry.Workspace;


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
    panel = new JCRSSelectorPanel();
    
    Dimension size = new Dimension(800, 800);
    
    setSize(size);
    setPreferredSize(size);
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(panel, BorderLayout.CENTER);
    
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    setIconImage(IconServer.getImage("arpenteur/gis/map_A-16.png"));
  }

  /**
   * Shows the {@link org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system} selection frame. 
   * Please use this method instead of the classic {@link #setVisible(boolean) setVisible(boolean)}.
   */
  public void showFrame(){
    pack();
    setVisible(true);
    panel.fitMap();
  }
  
  
  /**
   * The main method.
   * @param args the arguments.
   */
  public static void main(String[] args){
    Workspace.init();

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    } catch (InstantiationException ex) {
      ex.printStackTrace();
    } catch (IllegalAccessException ex) {
      ex.printStackTrace();
    } catch (UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
    }  
    
    JCRSSelectorFrame selector = new JCRSSelectorFrame();
    selector.showFrame();
  }
}
