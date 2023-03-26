package org.arpenteur.gis.geotools.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.arpenteur.common.ihm.icon.IconServer;

/**
 * A tool bar containing controls for the JMapPanel. Basic controls are available from
 * this toolbar.
 * @author Julien Seinturier
 *
 */
public class JMapPanelControlToolBar extends JToolBar {

  public static final String ZOOMIN_CMD  = "zoomInCMD";
  public static final String ZOOMOUT_CMD = "zoomInCMD";
  public static final String PAN_CMD     = "panCMD";
  public static final String SELECT_CMD  = "selectCMD";
  public static final String FIT_CMD     = "fitCMD";
  
   
  /** The map panel attached to the tool bar */
  private JMapPanel mapPanel             = null;
  
  private JToggleButton zoomInButton     = null;
  private JToggleButton zoomOutButton    = null;
  private JToggleButton panButton        = null;
  private JToggleButton selectButton     = null;
  
  private ButtonGroup buttonGroup        = null;
  
  private JButton fitButton              = null;
  
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
  /**
   * Create a new default map panel tool bar.
   */
  public JMapPanelControlToolBar(){
    this(null);
  }
  
  /**
   * Create a new map panel tool bar attached to a specific map panel.
   * @param mapPanel the map panel attached to the tool bar
   */
  public JMapPanelControlToolBar(JMapPanel mapPanel){
    super();
    initGUI();
    setMapPanel(mapPanel);
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II INITIALISATION                                                 II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  
  /**
   * Init the graphical user interface
   */
  protected void initGUI(){
    
    zoomInButton = new JToggleButton();
    zoomInButton.setIcon(IconServer.getIcon("crystalsvg/16x16/actions/viewmag+.png"));
    zoomInButton.setActionCommand(ZOOMIN_CMD);
    zoomInButton.setToolTipText("Zoom in");
    zoomInButton.addActionListener(new ActionListener(){

      
      public void actionPerformed(ActionEvent e) {
        if (mapPanel != null){
          mapPanel.setState(JMapPanel.STATE_ZOOM_IN);
        }
        
      }});
    
    zoomOutButton = new JToggleButton();
    zoomOutButton.setIcon(IconServer.getIcon("crystalsvg/16x16/actions/viewmag-.png"));
    zoomOutButton.setActionCommand(ZOOMOUT_CMD);
    zoomOutButton.setToolTipText("Zoom out");
    zoomOutButton.addActionListener(new ActionListener(){

      
      public void actionPerformed(ActionEvent e) {
        if (mapPanel != null){
          mapPanel.setState(JMapPanel.STATE_ZOOM_OUT);
        }
        
      }});
    
    panButton = new JToggleButton();
    panButton.setIcon(IconServer.getIcon("crystalsvg/16x16/actions/goto.png"));
    panButton.setActionCommand(PAN_CMD);
    panButton.setToolTipText("Pan view");
    panButton.addActionListener(new ActionListener(){

      
      public void actionPerformed(ActionEvent e) {
        if (mapPanel != null){
          mapPanel.setState(JMapPanel.STATE_PAN);
        }
        
      }});
    
    selectButton = new JToggleButton();
    selectButton.setIcon(IconServer.getIcon("crystalsvg/16x16/actions/kontact_notes.png"));
    selectButton.setActionCommand(SELECT_CMD);
    selectButton.setToolTipText("Select features");
    selectButton.addActionListener(new ActionListener(){

      
      public void actionPerformed(ActionEvent e) {
        if (mapPanel != null){
          mapPanel.setState(JMapPanel.STATE_SELECT);
        }
        
      }});
    
    buttonGroup = new ButtonGroup();
    buttonGroup.add(zoomInButton);
    buttonGroup.add(zoomOutButton);
    buttonGroup.add(panButton);
    buttonGroup.add(selectButton);
    
    fitButton = new JButton();
    fitButton.setIcon(IconServer.getIcon("crystalsvg/16x16/actions/viewmagfit.png"));
    fitButton.setActionCommand(FIT_CMD);
    fitButton.setToolTipText("Show all map area");
    fitButton.addActionListener(new ActionListener(){

      
      public void actionPerformed(ActionEvent e) {
        if (mapPanel != null){
          mapPanel.setState(JMapPanel.STATE_SELECT);
          /*
          try {
            mapPanel.setMapArea(mapPanel.getContext().getLayerBounds());
            mapPanel.repaint();
          } catch (IOException e1) {
            System.err.println(e1);
            e1.printStackTrace();
          }
          */
          mapPanel.fit();
          
        }
        
      }});
    
    this.setOrientation(SwingConstants.HORIZONTAL);
    this.add(zoomInButton);
    this.add(zoomOutButton);
    this.add(panButton);
    this.add(selectButton);
    this.add(new JToolBar.Separator());
    this.add(fitButton);
    this.add(new JToolBar.Separator());
    
    init();
  }
  
  /**
   * Init the toolbar with the state of the map panel.
   */
  protected void init(){
    if (mapPanel == null){
      
      zoomInButton.setSelected(false);
      zoomOutButton.setSelected(false);
      panButton.setSelected(false);
      selectButton.setSelected(false);
      fitButton.setSelected(false);
      
      zoomInButton.setEnabled(false);
      zoomOutButton.setEnabled(false);
      panButton.setEnabled(false);
      selectButton.setEnabled(false);
      fitButton.setEnabled(false);
    } else {
      zoomInButton.setEnabled(true);
      zoomOutButton.setEnabled(true);
      panButton.setEnabled(true);
      selectButton.setEnabled(true);
      fitButton.setEnabled(true);

      switch(mapPanel.getState()){
        case JMapPanel.STATE_ZOOM_IN:
          zoomInButton.setSelected(true);
          zoomOutButton.setSelected(false);
          panButton.setSelected(false);
          selectButton.setSelected(false);
          break;
          
        case JMapPanel.STATE_ZOOM_OUT:
          zoomInButton.setSelected(false);
          zoomOutButton.setSelected(true);
          panButton.setSelected(false);
          selectButton.setSelected(false);
          break;
          
        case JMapPanel.STATE_PAN:
          zoomInButton.setSelected(false);
          zoomOutButton.setSelected(false);
          panButton.setSelected(true);
          selectButton.setSelected(false);
          break;
          
        case JMapPanel.STATE_SELECT:
          zoomInButton.setSelected(false);
          zoomOutButton.setSelected(false);
          panButton.setSelected(false);
          selectButton.setSelected(true);
          break;
      }
    }

  }
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II FIN INITIALISATION                                             II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII

  
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEURS                                                     AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  /**
   * Set the map panel attached to this tool bar.
   * @param mapPanel the map panel attached to this toolbar.
   */
  public void setMapPanel(JMapPanel mapPanel){
    this.mapPanel = mapPanel;
    init();
  }
  
  /**
   * Get the map panel attached to this tool bar.
   * @return the map panel attached to this toolbar.
   */
  public JMapPanel getMapPanel(){
    return mapPanel;
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                                 AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
}
