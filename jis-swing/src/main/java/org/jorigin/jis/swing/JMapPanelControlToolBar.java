package org.jorigin.jis.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.jorigin.jis.JIS;
import org.jorigin.lang.LangResourceBundle;

/**
 * A tool bar containing controls for the {@link JMapPanel JMapPanel}. Basic controls are available from
 * this toolbar.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 * @see JMapPanel
 */
public class JMapPanelControlToolBar extends JToolBar {

  private static final long serialVersionUID = JIS.BUILD;
	
  /**
   * The Zoom in command.
   */
  public static final String ZOOMIN_CMD  = "zoomInCMD";
  
  /**
   * The Zoom out command.
   */
  public static final String ZOOMOUT_CMD = "zoomOutCMD";
  
  /**
   * The pan command.
   */
  public static final String PAN_CMD     = "panCMD";
  
  /**
   * The select command.
   */
  public static final String SELECT_CMD  = "selectCMD";
  
  /**
   * The fit command.
   */
  public static final String FIT_CMD     = "fitCMD";
  
  private LangResourceBundle lres    = (LangResourceBundle) LangResourceBundle.getBundle(Locale.getDefault());
  
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
    
    this.zoomInButton = new JToggleButton();
    this.zoomInButton.setIcon(new ImageIcon(getClass().getResource("/icon/jis/map_zoom_plus.png")));
    this.zoomInButton.setActionCommand(ZOOMIN_CMD);
    this.zoomInButton.setToolTipText(this.lres.getString("Zoom in"));
    this.zoomInButton.addActionListener(new ActionListener(){

      
      public void actionPerformed(ActionEvent e) {
        if (JMapPanelControlToolBar.this.mapPanel != null){
          JMapPanelControlToolBar.this.mapPanel.setState(JMapPanel.STATE_ZOOM_IN);
        }
        
      }});
    
    this.zoomOutButton = new JToggleButton();
    this.zoomOutButton.setIcon(new ImageIcon(getClass().getResource("/icon/jis/map_zoom_minus.png")));
    this.zoomOutButton.setActionCommand(ZOOMOUT_CMD);
    this.zoomOutButton.setToolTipText(this.lres.getString("Zoom out"));
    this.zoomOutButton.addActionListener(new ActionListener(){

      
      public void actionPerformed(ActionEvent e) {
        if (JMapPanelControlToolBar.this.mapPanel != null){
          JMapPanelControlToolBar.this.mapPanel.setState(JMapPanel.STATE_ZOOM_OUT);
        }
        
      }});
    
    this.panButton = new JToggleButton();
    this.panButton.setIcon(new ImageIcon(getClass().getResource("/icon/jis/map_pan.png")));
    this.panButton.setActionCommand(PAN_CMD);
    this.panButton.setToolTipText(this.lres.getString("Pan view"));
    this.panButton.addActionListener(new ActionListener(){

      
      public void actionPerformed(ActionEvent e) {
        if (JMapPanelControlToolBar.this.mapPanel != null){
          JMapPanelControlToolBar.this.mapPanel.setState(JMapPanel.STATE_PAN);
        }
        
      }});
    
    this.selectButton = new JToggleButton();
    this.selectButton.setIcon(new ImageIcon(getClass().getResource("/icon/jis/map_select.png")));
    this.selectButton.setActionCommand(SELECT_CMD);
    this.selectButton.setToolTipText(this.lres.getString("Select features"));
    this.selectButton.addActionListener(new ActionListener(){

      
      public void actionPerformed(ActionEvent e) {
        if (JMapPanelControlToolBar.this.mapPanel != null){
          JMapPanelControlToolBar.this.mapPanel.setState(JMapPanel.STATE_SELECT);
        }
        
      }});
    
    this.buttonGroup = new ButtonGroup();
    this.buttonGroup.add(this.zoomInButton);
    this.buttonGroup.add(this.zoomOutButton);
    this.buttonGroup.add(this.panButton);
    this.buttonGroup.add(this.selectButton);
    
    this.fitButton = new JButton();
    this.fitButton.setIcon(new ImageIcon(getClass().getResource("/icon/jis/map_zoom_fit.png")));
    this.fitButton.setActionCommand(FIT_CMD);
    this.fitButton.setToolTipText(this.lres.getString("Show all map area"));
    this.fitButton.addActionListener(new ActionListener(){

      
      public void actionPerformed(ActionEvent e) {
        if (JMapPanelControlToolBar.this.mapPanel != null){
          JMapPanelControlToolBar.this.mapPanel.setState(JMapPanel.STATE_SELECT);
          /*
          try {
            mapPanel.setMapArea(mapPanel.getContext().getLayerBounds());
            mapPanel.repaint();
          } catch (IOException e1) {
            System.err.println(e1);
            e1.printStackTrace();
          }
          */
          JMapPanelControlToolBar.this.mapPanel.fit();
          
        }
        
      }});
    
    this.setOrientation(SwingConstants.HORIZONTAL);
    this.add(this.zoomInButton);
    this.add(this.zoomOutButton);
    this.add(this.panButton);
    this.add(this.selectButton);
    this.add(new JToolBar.Separator());
    this.add(this.fitButton);
    this.add(new JToolBar.Separator());
    
    init();
  }
  
  /**
   * Init the toolbar with the state of the map panel.
   */
  protected void init(){
    if (this.mapPanel == null){
      
      this.zoomInButton.setSelected(false);
      this.zoomOutButton.setSelected(false);
      this.panButton.setSelected(false);
      this.selectButton.setSelected(false);
      this.fitButton.setSelected(false);
      
      this.zoomInButton.setEnabled(false);
      this.zoomOutButton.setEnabled(false);
      this.panButton.setEnabled(false);
      this.selectButton.setEnabled(false);
      this.fitButton.setEnabled(false);
    } else {
      this.zoomInButton.setEnabled(true);
      this.zoomOutButton.setEnabled(true);
      this.panButton.setEnabled(true);
      this.selectButton.setEnabled(true);
      this.fitButton.setEnabled(true);

      switch(this.mapPanel.getState()){
        case JMapPanel.STATE_ZOOM_IN:
          this.zoomInButton.setSelected(true);
          this.zoomOutButton.setSelected(false);
          this.panButton.setSelected(false);
          this.selectButton.setSelected(false);
          break;
          
        case JMapPanel.STATE_ZOOM_OUT:
          this.zoomInButton.setSelected(false);
          this.zoomOutButton.setSelected(true);
          this.panButton.setSelected(false);
          this.selectButton.setSelected(false);
          break;
          
        case JMapPanel.STATE_PAN:
          this.zoomInButton.setSelected(false);
          this.zoomOutButton.setSelected(false);
          this.panButton.setSelected(true);
          this.selectButton.setSelected(false);
          break;
          
        case JMapPanel.STATE_SELECT:
          this.zoomInButton.setSelected(false);
          this.zoomOutButton.setSelected(false);
          this.panButton.setSelected(false);
          this.selectButton.setSelected(true);
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
    return this.mapPanel;
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                                 AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
}
