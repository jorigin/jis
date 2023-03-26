package org.jorigin.jis.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.geotools.map.Layer;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.jorigin.jis.CRSDefaults;
import org.jorigin.jis.JIS;
import org.jorigin.jis.data.GISDataLoader;
import org.jorigin.jis.map.ExtendedMapContent;
import org.jorigin.lang.PathUtil;
import org.opengis.geometry.coordinate.Position;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *This panel enable to select {@link org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system} from 
 * an interactive map and some simple queries. <br/>
 * @author Julien Seinturier
 *
 */
public class JCRSSelectorPanel extends JPanel {
  
  
  private static final long serialVersionUID = JIS.BUILD;

  private JMapPanel mapPanel = null;
  
  private GISDataLoader loader = null;
  
  private JCRSPanel crsPanel = null;
  
  private String mapPath     = null;
  
  private ExtendedMapContent map = null;
  
  private List<CoordinateReferenceSystem> crsList = null;
  
  private JMapPanelControlToolBar controlTB = null;
  
  private JPanel upPanel       = null;
  
  private JPanel downPanel     = null;
  
  private JSplitPane splitPane = null;
  
  private GTRenderer renderer = null;
  
  public JCRSSelectorPanel(){
    super();
    this.mapPath = PathUtil.URIToPath(getClass().getResource("/gis/shp/world_countries.shp").toExternalForm());

    if (this.mapPath != null){
      this.loader = new  GISDataLoader();
    
      Layer data = this.loader.loadFile(this.mapPath);
    
      this.map = new ExtendedMapContent(data.getBounds().getCoordinateReferenceSystem());

      this.map.addLayer(data);
      this.map.setFocusable(data, false);
      this.map.setSelectable(data, false);
      
      this.map.getViewport().setBounds(this.map.getMaxBounds());
      
    }
    
    this.crsList = CRSDefaults.getAvailablesCRS();
    
    this.crsPanel = new JCRSPanel(this.crsList);
    this.crsPanel.addItemListener(new ItemListener(){

      @Override
      public void itemStateChanged(ItemEvent e) {
        processCRSSelection(JCRSSelectorPanel.this.crsPanel.getSelectedCRSList());
      }});
    
    initGUI();
  }
  
  protected void initGUI(){
    
    GridBagConstraints constraints = null;
    
    Insets labelInsets   = new Insets(8,8,0,0);
    Insets fieldInsets   = new Insets(8,0,0,0);
    
    this.renderer = new StreamingRenderer();
    
    RenderingHints j2dhints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    this.renderer.setJava2DHints(j2dhints);
    
    HashMap<Object, Object> hints = new HashMap<Object, Object>();
    hints.put("memoryPreloadingEnabled", Boolean.TRUE);
    this.renderer.setRendererHints( hints );
    
    this.mapPanel = new JMapPanel(this.renderer, this.map);
    this.mapPanel.setState(JMapPanel.STATE_PAN);
    this.mapPanel.setPreferredSize(new Dimension(400, 200));
    this.mapPanel.setMinimumSize(new Dimension(50, 200));
    
    this.mapPanel.addMouseListener(new MouseListener(){

      @Override
      public void mouseClicked(MouseEvent e) {
    	Position pt = JCRSSelectorPanel.this.mapPanel.getTerrainCoordinates(e.getPoint().x, e.getPoint().y);
        JCRSSelectorPanel.this.crsPanel.setPositionFilterActive(true);
        JCRSSelectorPanel.this.crsPanel.setGeographicCoordinates(pt.getDirectPosition().getOrdinate(0), pt.getDirectPosition().getOrdinate(1));
        JCRSSelectorPanel.this.crsPanel.filter();
        JCRSSelectorPanel.this.crsPanel.refreshGUI();
      }

      @Override
      public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
      }});
    
    this.controlTB = new JMapPanelControlToolBar(this.mapPanel);
    
    this.upPanel   = new JPanel();
    this.upPanel.setLayout(new GridBagLayout());
    
    this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    
    
    
    
    constraints           = new GridBagConstraints();
    constraints.gridx     = GridBagConstraints.RELATIVE;
    constraints.gridy     = GridBagConstraints.RELATIVE;
    constraints.gridheight= 1;
    constraints.gridwidth = 1;
    constraints.fill      = GridBagConstraints.NONE;
    constraints.insets    = fieldInsets;
    constraints.weightx   = 0.0;
    constraints.weighty   = 0.0;
    constraints.anchor    = GridBagConstraints.WEST;
    this.upPanel.add(this.controlTB, constraints);
    
    constraints           = new GridBagConstraints();
    constraints.gridx     = GridBagConstraints.RELATIVE;
    constraints.gridy     = GridBagConstraints.RELATIVE;
    constraints.gridheight= 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.fill      = GridBagConstraints.HORIZONTAL;
    constraints.insets    = fieldInsets;
    constraints.weightx   = 1.0;
    constraints.weighty   = 0.0;
    constraints.anchor    = GridBagConstraints.WEST;
    this.upPanel.add(new JPanel(), constraints);
    
    constraints           = new GridBagConstraints();
    constraints.gridx     = GridBagConstraints.RELATIVE;
    constraints.gridy     = GridBagConstraints.RELATIVE;
    constraints.gridheight= 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.fill      = GridBagConstraints.BOTH;
    constraints.insets    = fieldInsets;
    constraints.weightx   = 1.0;
    constraints.weighty   = 1.0;
    constraints.anchor    = GridBagConstraints.WEST;
    this.upPanel.add(this.mapPanel, constraints);
    
    this.downPanel = new JPanel();
    this.downPanel.setLayout(new GridBagLayout());
    this.downPanel.setSize(new Dimension(320, 100));
    this.downPanel.setSize(new Dimension(320, 100));
    
    
    constraints           = new GridBagConstraints();
    constraints.gridx     = GridBagConstraints.RELATIVE;
    constraints.gridy     = GridBagConstraints.RELATIVE;
    constraints.gridheight= 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.fill      = GridBagConstraints.BOTH;
    constraints.insets    = fieldInsets;
    constraints.weightx   = 0.1;
    constraints.weighty   = 0.1;
    constraints.anchor    = GridBagConstraints.WEST;
    this.downPanel.add(this.crsPanel, constraints);
 
    this.splitPane.add(this.upPanel, JSplitPane.TOP);
    this.splitPane.add(this.downPanel, JSplitPane.BOTTOM);
    this.splitPane.setDividerLocation(350);
    
    setLayout(new BorderLayout());
    add(this.splitPane, BorderLayout.CENTER);
    
    validate();
    
    this.mapPanel.fit();
  }
  
  protected void refreshGUI(){
    
  }
  
  public void fitMap(){
   this.mapPanel.fit();
  }
  
  protected void processCRSSelection(List<CoordinateReferenceSystem> crsList){
    System.out.println("SELECTION WITHIN CRS PANEL");
  }
}
