package org.arpenteur.gis.geotools.swing;

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

import org.arpenteur.common.Common;
import org.arpenteur.common.geometry.primitive.point.IPoint3D;
import org.arpenteur.common.lang.PathUtil;
import org.arpenteur.gis.geotools.CRSDefaults;
import org.arpenteur.gis.geotools.data.GISDataLoader;
import org.arpenteur.gis.geotools.map.ExtendedMapContext;

import org.geotools.map.Layer;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *This panel enable to select {@link org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system} from 
 * an interactive map and some simple queries. <br/>
 * @author Julien Seinturier
 *
 */
public class JCRSSelectorPanel extends JPanel {
  
  
  private static final long serialVersionUID = Common.BUILD;

  private JMapPanel mapPanel = null;
  
  private GISDataLoader loader = null;
  
  private JCRSPanel crsPanel = null;
  
  private String mapPath     = null;
  
  private ExtendedMapContext map = null;
  
  private List<CoordinateReferenceSystem> crsList = null;
  
  private JMapPanelControlToolBar controlTB = null;
  
  private JPanel upPanel       = null;
  
  private JPanel downPanel     = null;
  
  private JSplitPane splitPane = null;
  
  private GTRenderer renderer = null;
  
  public JCRSSelectorPanel(){
    super();
    mapPath = PathUtil.URIToPath(getClass().getResource("../../resource/world_countries.shp").toExternalForm());

    if (mapPath != null){
      loader = new  GISDataLoader();
    
      Layer data = loader.loadFile(mapPath);
    
      map = new ExtendedMapContext(data.getBounds().getCoordinateReferenceSystem());

      map.addLayer(data);
      map.setFocusable(data, false);
      map.setSelectable(data, false);
      
      map.getViewport().setBounds(map.getMaxBounds());
      
    }
    
    crsList = CRSDefaults.getAvailablesCRS();
    
    crsPanel = new JCRSPanel(crsList);
    crsPanel.addItemListener(new ItemListener(){

      @Override
      public void itemStateChanged(ItemEvent e) {
        processCRSSelection(crsPanel.getSelectedCRSList());
      }});
    
    initGUI();
  }
  
  protected void initGUI(){
    
    GridBagConstraints constraints = null;
    
    Insets labelInsets   = new Insets(8,8,0,0);
    Insets fieldInsets   = new Insets(8,0,0,0);
    
    renderer = new StreamingRenderer();
    
    RenderingHints j2dhints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    renderer.setJava2DHints(j2dhints);
    
    HashMap<Object, Object> hints = new HashMap<Object, Object>();
    hints.put("memoryPreloadingEnabled", Boolean.TRUE);
    renderer.setRendererHints( hints );
    
    mapPanel = new JMapPanel(renderer, map);
    mapPanel.setState(JMapPanel.STATE_PAN);
    mapPanel.setPreferredSize(new Dimension(400, 200));
    mapPanel.setMinimumSize(new Dimension(50, 200));
    
    mapPanel.addMouseListener(new MouseListener(){

      @Override
      public void mouseClicked(MouseEvent e) {
        IPoint3D pt = mapPanel.getTerrainCoordinates(e.getPoint());
        crsPanel.setPositionFilterActive(true);
        crsPanel.setGeographicCoordinates(pt.getX(), pt.getY());
        crsPanel.filter();
        crsPanel.refreshGUI();
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
    
    controlTB = new JMapPanelControlToolBar(mapPanel);
    
    upPanel   = new JPanel();
    upPanel.setLayout(new GridBagLayout());
    
    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    
    
    
    
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
    upPanel.add(controlTB, constraints);
    
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
    upPanel.add(new JPanel(), constraints);
    
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
    upPanel.add(mapPanel, constraints);
    
    downPanel = new JPanel();
    downPanel.setLayout(new GridBagLayout());
    downPanel.setSize(new Dimension(320, 100));
    downPanel.setSize(new Dimension(320, 100));
    
    
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
    downPanel.add(crsPanel, constraints);
 
    splitPane.add(upPanel, JSplitPane.TOP);
    splitPane.add(downPanel, JSplitPane.BOTTOM);
    splitPane.setDividerLocation(350);
    
    setLayout(new BorderLayout());
    add(splitPane, BorderLayout.CENTER);
    
    validate();
    
    mapPanel.fit();
  }
  
  protected void refreshGUI(){
    
  }
  
  public void fitMap(){
   mapPanel.fit();
  }
  
  protected void processCRSSelection(List<CoordinateReferenceSystem> crsList){
    System.out.println("SELECTION WITHIN CRS PANEL");
  }
}
