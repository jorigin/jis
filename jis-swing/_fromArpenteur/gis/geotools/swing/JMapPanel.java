package org.jorigin.jis.swing;



import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.IllegalFilterException;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapLayerListEvent;
import org.geotools.map.MapLayerListListener;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.label.LabelCacheImpl;
import org.geotools.renderer.lite.LabelCache;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.jorigin.jis.JIS;
import org.jorigin.jis.map.ExtendedMapContent;
import org.jorigin.jis.map.ExtendedMapContentEvent;
import org.jorigin.jis.swing.event.HighlightChangeListener;
import org.jorigin.jis.swing.event.HighlightChangedEvent;
import org.jorigin.jis.swing.event.SelectionChangeListener;
import org.jorigin.jis.swing.event.SelectionChangedEvent;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * This panel if the main GUI component for feature vizualization. This class is a 
 * fork of the original class JMapPane from geotools developped by Ian Turton. As the 
 * original component, this map panel stores an image of the map
 * (drawn from the context) and an image of the slected feature(s) to speed up
 * rendering of the highlights. Thus the whole map is only redrawn when the bbox
 * changes, selection is only redrawn when the selected feature changes.
 * @author Julien Seinturier
 */
public class JMapPanel extends JPanel implements
        HighlightChangeListener,SelectionChangeListener, PropertyChangeListener,
        MapLayerListListener {

    private static final long serialVersionUID = 200707301214L;

    public static final int Reset    = 0;

    /**
     * Specifies the state of the map panel to zoom in. Panel behavior is then
     * used in the zoom in context.
     */
    public static final int STATE_ZOOM_IN   = 1;

    /**
     * Specifies the state of the map panel to zoom out. Panel behavior is then
     * used in the zoom out context.
     */
    public static final int STATE_ZOOM_OUT  = 2;

    /**
     * Specifies the state of the map panel to Pan. Panel behavior is then
     * used in the Pan context. The pan move the map according to the mouse position 
     * on the panel.
     */
    public static final int STATE_PAN      = 3;

    /**
     * Specifies the state of the map panel to select. Panel behavior is then
     * used in the selection context. In this context, features can be selected from
     * the panel.
     */
    public static final int STATE_SELECT        = 4;

    
    /**
     * Specifies the state of the panel to draw a line.
     */
    public static final int STATE_DRAW_LINE     = 5;
    
    /**
     * Specifies the state of the panel to draw a polygon.
     */
    public static final int STATE_DRAW_POLYGON  = 6;
    
    /**
     * Specifies the state of the panel to draw a polyline.
     */
    public static final int STATE_DRAW_POLYLINE = 7;
    
    
    private static final int POLYGON = 0;

    private static final int LINE    = 1;

    private static final int POINT   = 2;

    /**
     * Indicates if the focus detection is active.
     * the focus detection is the detection of the features focused
     * by the cursor on the display.
     */
    private boolean focusDetectionActive = true;
    
    /**
     * The renderer used to drawthe features composing the map
     */
    GTRenderer renderer = null;;

    /**
     * The render used to draw selected features and highlighted features
     */
    private GTRenderer highlightRenderer = null;
    private GTRenderer selectionRenderer = null;

    /**
     * the map context to render
     */
    MapContent context                   = null;

    /**
     * The selection context.
     */
    private MapContent selectionContext  = null;

    /**
     * The area of the map to draw
     */
    ReferencedEnvelope mapArea = null;

    /**
     * Is the aspect ratio of the map has to be kept during a zoom.
     */
    private boolean keepAspectRatio = true;
    
    /**
     * the size of the pane last time we drew
     */
    private Rectangle oldRect = null;

    /**
     * The last map area drawn.
     */
    private Envelope oldMapArea = null;

    /**
     * The base image of the map
     */
    private BufferedImage baseImage = null;

    /**
     * Image of selection
     */
    private BufferedImage selectImage = null;

    /**
     * Style for selected items
     */
    private Style selectionStyle = null;

    /**
     * Layer that selection works on
     */
    private Layer selectionLayer = null;

    /**
     * The list of the layers that can interact. These layers can be
     * processed to find focused features and selected features.
     */
    private List<Layer> selectableLayers = null;
    
    private List<Layer> focusableLayers  = null;
    
    
    /**
     * Layer that highlight works on
     */
    private Layer highlightLayer = null;

    /**
     * The object which manages highlighting
     */
    private HighlightManager highlightManager = null;

    /**
     * Is highlighting on or off
     */
    private boolean highlight = true;

    /**
     * A factory for filters
     */
    FilterFactory2 ff         =  null;

    /**
     * A factory for geometries
     */
    GeometryFactory gf = new GeometryFactory(); // FactoryFinder.getGeometryFactory(null);

    /**
     * the collections of features to be selected or highlighted
     */
    ArrayList<SimpleFeature> selection       = null;

    /**
     * The state of the panel.
     */
    private int state           = STATE_SELECT;

    /**
     * how far to zoom in or out
     */
    private double zoomFactor   = 2.0;

    private final double zoomStep     = 0.01;
    
    /**
     * the collections of features to be selected or highlighted
     */
    FeatureCollection highlightFeature = null;
    
    Style lineHighlightStyle    = null;

    Style pointHighlightStyle   = null;

    Style polygonHighlightStyle = null;

    Style lineSelectionStyle    = null;
    
    Style polygonSelectionStyle = null;

    Style pointSelectionStyle   = null;

    boolean changed             = true;

    LabelCache labelCache       = new LabelCacheImpl();

    private boolean reset       = false;

    int startX                  = 0;

    int startY                  = 0;


    int lastX                   = 0;

    int lastY                   = 0;

    
    private DirectPosition mapCurrentPoint    = null;
    
    private SelectionManager selectionManager = null;

    private ArrayList<SimpleFeature> focusedFeatures = null;
    
    //Liste des écouteurs informés des evenements du panneau
    protected EventListenerList idListenerList = new EventListenerList();
    
    /**
     * Indicate if the mouse is currently dragging.
     */
    private boolean dragging = false;
    
    private boolean isDigit  = false;
    
    /**
     * The 2D line drawn in the state STATE_DRAW_LINE
     */
    private DirectPosition drawnLinePt1  = null;
    private DirectPosition drawnLinePt2  = null;
    
    /**
     * The 2D polygon drawn in the state STATE_DRAW_POLYGON.
     */
    //private IPolygon3D drawnPolygon    = null;
    private List<DirectPosition> drawnVertices = null;
    
    RenderListener renderListener      = null;
    
    //CoordinateReferenceSystem  defaultCRS = org.geotools.referencing.crs.DefaultEngineeringCRS.GENERIC_2D;
    CoordinateReferenceSystem  defaultCRS = DefaultGeographicCRS.WGS84;
    
    boolean useContextCRS                 = false;
    
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                      CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
    /**
     * Create a default JMapPanel.
     */
    public JMapPanel() {
        this(null, true, null, null);
    }

    /**
     * Create a basic JMapPanel
     * @param render how to draw the map
     * @param context the map context to display
     */
    public JMapPanel(GTRenderer render, MapContent context) {
        this(null, true, render, context);
    }

    /**
     * full constructor extending JPanel
     *
     * @param layout -
     *            layout (probably shouldn't be set)
     * @param isDoubleBuffered -
     *            a Swing thing I don't really understand
     * @param render -
     *            what to draw the map with
     * @param context -
     *            what to draw
     */
    public JMapPanel(LayoutManager layout, boolean isDoubleBuffered,
            GTRenderer render, MapContent context) {
        super(layout, isDoubleBuffered);
        
        this.ff = (FilterFactory2) org.geotools.factory.CommonFactoryFinder
                .getFilterFactory(null);
        setRenderer(render);

        setContext(context);
        
        addKeyListener(new KeyListener(){

	  @Override
	  public void keyPressed(KeyEvent e) {
	    processKeyTypedEvent(e);
	    
	  }

	  @Override
	  public void keyReleased(KeyEvent e) {

	  }

	  @Override
	  public void keyTyped(KeyEvent e) {
	    processKeyTypedEvent(e);
	  }});
          
  
        
        this.addMouseListener(new MouseListener(){

          @Override
          public void mouseClicked(MouseEvent e) {

            DirectPosition pt    = null;
            
            // Recuperation du point terrain correspondant au point cliqué
            JMapPanel.this.mapCurrentPoint = getTerrainCoordinates(e.getX(), e.getY());

            switch (JMapPanel.this.state) {
              case STATE_PAN:
                selectFeatures(e.getX(), e.getY());
                break;

              case STATE_ZOOM_IN:
                break;

              case STATE_ZOOM_OUT:
                break;

              case STATE_SELECT:
                selectFeatures(e.getX(), e.getY());
                return;

              case STATE_DRAW_LINE:

                if (e.getButton() == MouseEvent.BUTTON1){
                
                  if (JMapPanel.this.drawnLinePt1 == null){
                    JMapPanel.this.drawnLinePt1 = new DirectPosition2D(JMapPanel.this.mapCurrentPoint.getOrdinate(0), JMapPanel.this.mapCurrentPoint.getOrdinate(1));
                  } else if (JMapPanel.this.drawnLinePt2 == null){
                    JMapPanel.this.drawnLinePt2 = new DirectPosition2D(JMapPanel.this.mapCurrentPoint.getOrdinate(0), JMapPanel.this.mapCurrentPoint.getOrdinate(1));

                    JMapPanel.this.isDigit = false;
                    fireEvent(new JMapPanelEvent(getRef(), JMapPanelEvent.DIGITALIZING_STOPED));
                    
                  }
                } else {
                  JMapPanel.this.isDigit = false;
                  fireEvent(new JMapPanelEvent(getRef(), JMapPanelEvent.DIGITALIZING_STOPED));
                }
                repaint();
                
                return;  
                
              case STATE_DRAW_POLYGON:
                
        	pt = new DirectPosition2D(JMapPanel.this.mapCurrentPoint.getOrdinate(0), JMapPanel.this.mapCurrentPoint.getOrdinate(1));
        	
                if (e.getButton() == MouseEvent.BUTTON1){
                  JMapPanel.this.drawnVertices.add(pt);
                } else {
                  JMapPanel.this.isDigit = false;
                  fireEvent(new JMapPanelEvent(getRef(), JMapPanelEvent.DIGITALIZING_STOPED));
                }
                
                pt = null;
                
                repaint();
                
                return;    
                
              case STATE_DRAW_POLYLINE:
                if (e.getButton() == MouseEvent.BUTTON1){
                  
                } else {
                  JMapPanel.this.isDigit = false;
                  fireEvent(new JMapPanelEvent(getRef(), JMapPanelEvent.DIGITALIZING_STOPED));
                }
                repaint();
                return;   
                
              default:
                return;
            }
            
            JMapPanel.this.renderListener = new RenderListener(){

	      @Override
	      public void errorOccurred(Exception e) {
		//processRenderError(e);
	      }

	      @Override
	      public void featureRenderer(SimpleFeature feature) {
	        //processRenderEvent(e);
	      }
              
            };
            
            /*
            Coordinate ll = new Coordinate(mapCurrentPoint.getX() - (width2 / zlevel), mapCurrentPoint.getY()
                    - (height2 / zlevel));
            Coordinate ur = new Coordinate(mapCurrentPoint.getX() + (width2 / zlevel), mapCurrentPoint.getY()
                    + (height2 / zlevel));

            mapArea = new ReferencedEnvelope(new Envelope(ll, ur), getCoordinateReferenceSystem());
            */
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            JMapPanel.this.startX = e.getX();
            JMapPanel.this.startY = e.getY();
            JMapPanel.this.lastX  = 0;
            JMapPanel.this.lastY  = 0;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int endX = e.getX();
            int endY = e.getY();

            if ((JMapPanel.this.state == JMapPanel.STATE_ZOOM_IN) || (JMapPanel.this.state == JMapPanel.STATE_ZOOM_OUT)) {
                drawRectangle(getGraphics());
            }


            if (JMapPanel.this.dragging == true){
              processDrag(JMapPanel.this.startX, JMapPanel.this.startY, endX, endY);
              JMapPanel.this.dragging = false;
            }
            
            JMapPanel.this.lastX = 0;
            JMapPanel.this.lastY = 0;
        }
});
        
        
        
        this.addMouseMotionListener(new MouseMotionListener(){

          @Override
          public void mouseMoved(MouseEvent e) {
            
            JMapPanel.this.mapCurrentPoint = getTerrainCoordinates(e.getX(), e.getY());
         
            // Tracage des primitives en cours de dessin
            switch(JMapPanel.this.state){
              case STATE_DRAW_LINE:
                repaint();
                break;
                
              case STATE_DRAW_POLYGON:
                repaint();
                break;
                
              case STATE_DRAW_POLYLINE:
                break;
                
              default:
                if (JMapPanel.this.focusDetectionActive){
                  focusFeatures(e.getX(), e.getY());
                }
                break;
            }
          }

          
          
          @Override
          public void mouseDragged(MouseEvent e) {
              Graphics graphics = getGraphics();
              int x = e.getX();
              int y = e.getY();

              JMapPanel.this.dragging = true;
              
              if (JMapPanel.this.state == JMapPanel.STATE_PAN) {
                  // move the image with the mouse
                  if ((JMapPanel.this.lastX > 0) && (JMapPanel.this.lastY > 0)) {
                      int dx = JMapPanel.this.lastX - JMapPanel.this.startX;
                      int dy = JMapPanel.this.lastY - JMapPanel.this.startY;
                      // System.out.println("translate "+dx+","+dy);
                      graphics.clearRect(0, 0, getWidth(), getHeight());
                      ((Graphics2D) graphics).drawImage(JMapPanel.this.baseImage, dx, dy, getRef());
                  }

                  JMapPanel.this.lastX = x;
                  JMapPanel.this.lastY = y;
              } else if ((JMapPanel.this.state == JMapPanel.STATE_ZOOM_IN) || (JMapPanel.this.state == JMapPanel.STATE_ZOOM_OUT)) {
                  graphics.setXORMode(Color.RED);

                  if ((JMapPanel.this.lastX > 0) && (JMapPanel.this.lastY > 0)) {
                      drawRectangle(graphics);
                  }

                  // draw new box
                  JMapPanel.this.lastX = x;
                  JMapPanel.this.lastY = y;
                  drawRectangle(graphics);

                  
                } else if ((JMapPanel.this.state == JMapPanel.STATE_SELECT) && (JMapPanel.this.selectableLayers != null)) {

                  graphics.setXORMode(Color.green);

                  /*
                   * if ((lastX > 0) && (lastY > 0)) { drawRectangle(graphics); }
                   */

                  // draw new box
                  JMapPanel.this.lastX = x;
                  JMapPanel.this.lastY = y;
                  drawRectangle(graphics);
                  repaint();
              } else if ((JMapPanel.this.state == JMapPanel.STATE_DRAW_LINE) || (JMapPanel.this.state == JMapPanel.STATE_DRAW_POLYGON) || (JMapPanel.this.state == JMapPanel.STATE_DRAW_POLYLINE)){
                // move the image with the mouse
                if ((JMapPanel.this.lastX > 0) && (JMapPanel.this.lastY > 0)) {
                  int dx = JMapPanel.this.lastX - JMapPanel.this.startX;
                  int dy = JMapPanel.this.lastY - JMapPanel.this.startY;
                  // System.out.println("translate "+dx+","+dy);
                  graphics.clearRect(0, 0, getWidth(), getHeight());
                  ((Graphics2D) graphics).drawImage(JMapPanel.this.baseImage, dx, dy, getRef());
                }

                JMapPanel.this.lastX = x;
                JMapPanel.this.lastY = y;
              }
          }});
        
        this.addMouseWheelListener(new MouseWheelListener(){

	  @Override
	  public void mouseWheelMoved(MouseWheelEvent e) {
	    double rot = e.getWheelRotation();
	    
	    if (rot < 0){
	      zoomTo(1-JMapPanel.this.zoomStep, JMapPanel.this.mapCurrentPoint);
	    } else {
	      zoomTo(1+JMapPanel.this.zoomStep, JMapPanel.this.mapCurrentPoint);	      
	    }
	    
	  }});
        
        setHighlightManager(new HighlightManager(this.highlightLayer));
        setSelectionManager(new SelectionManager(this.selectionLayer));

        this.lineHighlightStyle    = setupStyle(LINE, Color.red);

        this.pointHighlightStyle   = setupStyle(POINT, Color.red);

        this.polygonHighlightStyle = setupStyle(POLYGON, Color.red);

        this.polygonSelectionStyle = setupStyle(POLYGON, Color.cyan);

        this.pointSelectionStyle   = setupStyle(POINT, Color.cyan);

        this.lineSelectionStyle    = setupStyle(LINE, Color.cyan);
        
        this.drawnVertices = new ArrayList<DirectPosition>();
        
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                                  CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II INITIALISATION                                                    II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
   protected void initState(){
     
     
     switch(this.state){
       case STATE_PAN:
         break;

       case STATE_ZOOM_IN:
         break;

       case STATE_ZOOM_OUT:
         break;

       case STATE_SELECT:
         break;

       case STATE_DRAW_LINE:
	 this.isDigit = true;
	 this.drawnLinePt1 = null;
	 this.drawnLinePt2 = null;
	 fireEvent(new JMapPanelEvent(this, JMapPanelEvent.DIGITALIZING_STARTED));
	 
         break;
       
       case STATE_DRAW_POLYGON:
	 this.isDigit = true;
	 this.drawnVertices = new ArrayList<DirectPosition>();
	 fireEvent(new JMapPanelEvent(this, JMapPanelEvent.DIGITALIZING_STARTED));
         break;
       
       case STATE_DRAW_POLYLINE:
	 this.isDigit = true;
	 this.drawnVertices = new ArrayList<DirectPosition>();
         break;
       
       default:
         return;
     }
   }
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II FIN INITIALISATION                                                II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII

    public void refreshGUI(){
      zoom(1);
      zoom(-1);
    }
   
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if ((this.renderer == null) || (this.mapArea == null)) {
            return;
        }

        Rectangle r = getBounds();
        Rectangle dr = new Rectangle(r.width, r.height);

        /* either the viewer size has changed or we've done a reset */
        if (!r.equals(this.oldRect) || this.reset) {
          this.changed = true; /* note we need to redraw */
          this.reset   = false; /* forget about the reset */
          this.oldRect = r; /* store what the current size is */
          this.mapArea = fixAspectRatio(r, this.mapArea);
        }

        /* did the map extent change? */
        if (!this.mapArea.equals(this.oldMapArea)) { 
            this.changed = true;
            this.oldMapArea = this.mapArea;
            // when we tell the context that the bounds have changed WMSLayers
            // can refresh them selves
            this.context.getViewport().setBounds(this.mapArea);
        }

        /* if the map changed then redraw */
        if (this.changed) {
            this.changed = false;
            this.baseImage = new BufferedImage(dr.width, dr.height,
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D ig = this.baseImage.createGraphics();
            /* System.out.println("rendering"); */
            this.renderer.setMapContent(this.context);
            this.labelCache.clear(); // work around anoying labelcache bug
            this.renderer.paint(ig, dr, this.mapArea);
        }

        ((Graphics2D) g).drawImage(this.baseImage, 0, 0, this);

        if ((this.selection != null) && (this.selection.size() > 0)) {

            // Recuperation des feature selectionnees classees par type
            ArrayList<DefaultFeatureCollection> collections = getSelectionByFeatureType();
            FeatureCollection<SimpleFeatureType, SimpleFeature> collection = null;
          
            this.selectionContext = new MapContent();
            this.selectionContext.getViewport().setCoordinateReferenceSystem(getCoordinateReferenceSystem());
            this.selectImage = new BufferedImage(dr.width, dr.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ig = this.selectImage.createGraphics();
 
            if (collections != null){
              for(int i = 0; i < collections.size(); i++){
                collection = collections.get(i);
                
                String type = collection.getSchema().getGeometryDescriptor().getType().getName().getLocalPart();

                if (type == null){
                  type = "polygon";
                }

                if (type.toUpperCase().endsWith("POLYGON")){
                  this.selectionStyle = this.polygonSelectionStyle;
                } else if (type.toUpperCase().endsWith("LINE")){
                  this.selectionStyle = this.lineSelectionStyle;
                } else if (type.toUpperCase().endsWith("LINESTRING")){
                  this.selectionStyle = this.lineSelectionStyle;
                } else if (type.toUpperCase().endsWith("POINT")) {
                  this.selectionStyle = this.pointSelectionStyle;
                }

                this.selectionContext.addLayer( new FeatureLayer(collection, this.selectionStyle));
              }
            }
            
            this.selectionRenderer.setMapContent(this.selectionContext);

            this.selectionRenderer.paint(ig, dr, this.mapArea);

            ((Graphics2D) g).drawImage(this.selectImage, 0, 0, this);
   
        }

        if (this.highlight && (this.highlightFeature != null)
                && (this.highlightFeature.size() > 0)) {
            /*
             * String type = selection.getDefaultGeometry().getGeometryType();
             * System.out.println(type); if(type==null) type="polygon";
             */
            String type = this.highlightLayer.getFeatureSource().getSchema().getGeometryDescriptor().getType().getName().getLocalPart();
            /*String type = selection.getDefaultGeometry().getGeometryType();*/
            //System.out.println(type);
            if (type == null)
                type = "polygon";

            /* String type = "point"; */
            Style highlightStyle = null;
            if (type.toLowerCase().endsWith("polygon")) {
                highlightStyle = this.polygonHighlightStyle;
            } else if (type.toLowerCase().endsWith("point")) {
                highlightStyle = this.pointHighlightStyle;
            } else if (type.toLowerCase().endsWith("line")) {
                highlightStyle = this.lineHighlightStyle;
            }




            MapContent highlightContext = new MapContent();
            highlightContext.getViewport().setCoordinateReferenceSystem(getCoordinateReferenceSystem());

            highlightContext.addLayer(new FeatureLayer(this.highlightFeature, highlightStyle));
            this.highlightRenderer.setMapContent(highlightContext);

            /* System.out.println("rendering highlight"); */
            this.highlightRenderer.paint((Graphics2D) g, dr, this.mapArea);
        }
        
        // Tracage des primitives en cours de dessin
        switch(this.state){
          case STATE_DRAW_LINE:
            IPoint2D pt2d1 = null;
            IPoint2D pt2d2 = null;
            
            if (this.drawnLinePt1 != null){
              pt2d1 = getComponentCoordinates(this.drawnLinePt1);
              
              if (this.drawnLinePt2 != null){
                pt2d2 = getComponentCoordinates(this.drawnLinePt2);  
              } else {
                pt2d2 = new IPoint2D(getMousePosition().getX(), getMousePosition().getY());
              }

              g.setXORMode(Color.RED);
              drawLine(pt2d1, pt2d2, g);
            }
            break;
            
          case STATE_DRAW_POLYGON:
            
            Point mousePosition = getMousePosition();
            
            if ((this.drawnVertices != null) && (this.drawnVertices.size() > 0)){
              ArrayList<IPoint2D> vertices = new ArrayList<IPoint2D>();
              
              for(int i = 0; i < this.drawnVertices.size(); i++){
        	vertices.add(getComponentCoordinates(this.drawnVertices.get(i)));
              }
              
              if (mousePosition != null){
                vertices.add(new IPoint2D(mousePosition.getX(), mousePosition.getY()));
              }
              
              g.setXORMode(Color.RED);
              drawPolyLine(vertices, g);

            }
            
            
            
            break;
            
          case STATE_DRAW_POLYLINE:
            if ((this.drawnVertices != null) && (this.drawnVertices.size() > 0)){
              ArrayList<IPoint2D> vertices = new ArrayList<IPoint2D>();
              
              for(int i = 0; i < this.drawnVertices.size(); i++){
        	vertices.add(getComponentCoordinates(this.drawnVertices.get(i)));
              }
              
              if (getMousePosition() != null){
                vertices.add(new IPoint2D(getMousePosition().getX(), getMousePosition().getY()));
              }
              
              
              g.setXORMode(Color.RED);
              drawPolyLine(vertices, g);

            }
            break;
        }
    }

    private ReferencedEnvelope fixAspectRatio(Rectangle r, Envelope mapArea) {
        double mapWidth  = 0.0d; /* get the extent of the map */
        double mapHeight = 0.0d;
        double mapMaxX   = 0.0d;
        double mapMaxY   = 0.0d;
        double mapMinX   = 0.0d;
        double mapMinY   = 0.0d;
        double scaleX    = 0.0d; 
        double scaleY    = 1.0d;
        double scale     = 1.0d; // stupid compiler!
        double rwidth    = 0.0d;
        double rheight   = 0.0d;
        
        
        rwidth  = r.getWidth();
        rheight = r.getHeight();

        if (mapArea == null){
          return null;
        } else {
          mapWidth  = mapArea.getWidth(); /* get the extent of the map */
          mapHeight = mapArea.getHeight();
          mapMaxX   = mapArea.getMaxX();
          mapMaxY   = mapArea.getMaxY();
          mapMinX   = mapArea.getMinX();
          mapMinY   = mapArea.getMinY();
          scaleX    = rwidth  / mapWidth; 
          scaleY    = rheight / mapHeight;
          scale     = 1.0; // stupid compiler!
        }
        
        if (scaleX < scaleY) { /* pick the smaller scale */
            scale = scaleX;
        } else {
            scale = scaleY;
        }
        
        /* calculate the difference in width and height of the new extent */
        double deltaX = /* Math.abs */((rwidth / scale) - mapWidth);
        double deltaY = /* Math.abs */((rheight / scale) - mapHeight);
        
        /* create the new extent */
        Coordinate ll = new Coordinate(mapMinX - (deltaX / 2.0),
                mapMinY - (deltaY / 2.0));
        Coordinate ur = new Coordinate(mapMaxX + (deltaX / 2.0),
                mapMaxY + (deltaY / 2.0));

        return new ReferencedEnvelope(new Envelope(ll, ur), getCoordinateReferenceSystem());
    }

    public void doSelection(double x, double y, Layer layer) {

        Geometry geometry = this.gf.createPoint(new Coordinate(x, y));

        // org.opengis.geometry.Geometry geometry = new Point();

            findFeature(geometry, layer);

    }

    /**
     * @param geometry -
     *            a geometry to construct the filter with
     * @param i -
     *            the index of the layer to search
     * @throws IndexOutOfBoundsException
     */
    private void findFeature(Geometry geometry, Layer layer)
            throws IndexOutOfBoundsException {
        org.opengis.filter.spatial.BinarySpatialOperator f = null;
        
        if ((this.context == null) || (layer==null)) {
            return ;
        }

        try {
            String name = layer.getFeatureSource().getSchema().getGeometryDescriptor().getType().getName().getLocalPart();
            
            if (name == "") {
                name = "the_geom";
            }

            try {
                f = this.ff.contains(this.ff.property(name), this.ff.literal(geometry));
                if(this.selectionManager!=null) {
                    this.selectionManager.selectionChanged(this, f);

                }
            } catch (IllegalFilterException e) {
                JIS.logger.log(Level.SEVERE, "Selection error: "+e.getMessage(), e);
            }

        } catch (IllegalFilterException e) {
        	JIS.logger.log(Level.SEVERE, "Selection error: "+e.getMessage(), e);
        }
        return ;
    }

//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE EVENEMENT                                                         EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE

    /**
     * Add a listener to the object. The listener must implements interface
     * java.awt.event.AWTEventListener because the interface java.util.EventListener
     * is empty and does not determine a dispatch method.
     * @param listener the listener to add.
     */
    public void addMapPanelListener(AWTEventListener listener){
      this.idListenerList.add(AWTEventListener.class, listener);    
    }

    /**
     * Remove a listener from the object. The listener must implements interface
     * java.awt.event.AWTEventListener because the interface java.util.EventListener
     * is empty and does not determine a dispatch method.
     * @param listener the listener to remove.
     */
    public void removeMapPanelListener(AWTEventListener listener){
      this.idListenerList.remove(AWTEventListener.class, listener);   
    }

    /**
     * Fire a JMapPanel event to all registered listenners
     * @param event the event to fire.
     */
    protected void fireEvent(JMapPanelEvent event){
      Object[] listeners = this.idListenerList.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
        if (listeners[i] == AWTEventListener.class) {
          ( (AWTEventListener) listeners[i + 1]).eventDispatched(event);
        }
      }  
    }

    /**
     * Fit the view to see all the map in the panel.
     */
    public void fit(){
      setMapArea(getContent().getMaxBounds());
      repaint();
    }
    
    public void zoom(int increment){

      double startX = this.mapArea.getMinX()-increment;
      double startY = this.mapArea.getMinY()-increment;
      double endX   = this.mapArea.getMaxX()+increment;
      double endY   = this.mapArea.getMaxY()+increment;
      
      double left   = Math.min(startX, endX);
      double right  = Math.max(startX, endX);
      double bottom = Math.min(startY, endY);
      double top    = Math.max(startY, endY);
      Coordinate ll = new Coordinate(left, bottom);
      Coordinate ur = new Coordinate(right, top);

      this.mapArea = fixAspectRatio(this.getBounds(), new Envelope(ll, ur));
      
      repaint();
    }

    public void zoomTo(double ratio, DirectPosition point){
      double startX = 0.0;
      double startY = 0.0;
      double endX   = 0.0;
      double endY   = 0.0;
      
      double left   = 0.0;
      double right  = 0.0;
      double bottom = 0.0;
      double top    = 0.0;
      Coordinate ll = null;
      Coordinate ur = null;
      
      Envelope envelope = null;
      
      if ((ratio <= 0) || (point == null)){
        return;
      } else {
        
        left   = point.getOrdinate(0) - (this.mapArea.getWidth()/2);
        right  = point.getOrdinate(0) + (this.mapArea.getWidth()/2);
        
        top    = point.getOrdinate(1) + (this.mapArea.getHeight()/2);
        bottom = point.getOrdinate(1) - (this.mapArea.getHeight()/2);
        
        ReferencedEnvelope bounds = this.context.getMaxBounds();
        
        left   = left*ratio;
        right  = right*ratio;
        
        top    = top*ratio;
        bottom = bottom*ratio;
        
        // Correcting coordinates by using context bounds
        double delta = 0.0d;
        
        if ((left < bounds.getMinX()) && (right > bounds.getMaxX())){
          left  = bounds.getMinX();
          right = bounds.getMaxX();
        } else if (left < bounds.getMinX()){
          delta = Math.abs(left - bounds.getMinX());
          left  = bounds.getMinX();
          right = right + delta;
        } else if (right > bounds.getMaxX()){
          delta = Math.abs(right - bounds.getMaxX());
          right = bounds.getMaxX();
          left  = left - delta;
        }

        if ((bottom < bounds.getMinY())&&(top > bounds.getMaxY())){
          bottom = bounds.getMinY();
          top = bounds.getMaxY();
        } else if (bottom < bounds.getMinY()){
          delta  = Math.abs(bottom - bounds.getMinY());
          bottom = bounds.getMinY();
          top    = top + delta;
        } else if (top > bounds.getMaxY()){
          delta  = Math.abs(top - bounds.getMaxY());
          top    = bounds.getMaxY();
          bottom = bottom - delta;
        }
        
        ll     = new Coordinate(left, bottom);
        ur     = new Coordinate(right, top);
        
        envelope = new Envelope(ll, ur);
      }
      
 
      this.mapArea = fixAspectRatio(this.getBounds(), envelope);
      //mapArea   =  new ReferencedEnvelope(envelope, getCoordinateReferenceSystem());
      
      repaint();
    }
    
    /**
     * Zoom the current view by a given ratio.
     * @param ratio the ratio of zoom.
     */
    public void zoom(double ratio){
      
      double startX = 0.0;
      double startY = 0.0;
      double endX   = 0.0;
      double endY   = 0.0;
      
      double left   = 0.0;
      double right  = 0.0;
      double bottom = 0.0;
      double top    = 0.0;
      Coordinate ll = null;
      Coordinate ur = null;
      
      if (ratio <= 0){
	      return;
      } else if (ratio <= 1){
	      startX = this.mapArea.getMinX() - (this.mapArea.getWidth() * ratio);
	      startY = this.mapArea.getMinY() - (this.mapArea.getHeight()* ratio);
	      endX   = this.mapArea.getMaxX() + (this.mapArea.getWidth() * ratio);
	      endY   = this.mapArea.getMaxY() + (this.mapArea.getHeight()* ratio);
      } else {
	      startX = this.mapArea.getMinX() + (this.mapArea.getWidth() / ratio);
	      startY = this.mapArea.getMinY() + (this.mapArea.getHeight()/ ratio);
	      endX   = this.mapArea.getMaxX() - (this.mapArea.getWidth() / ratio);
	      endY   = this.mapArea.getMaxY() - (this.mapArea.getHeight()/ ratio);
      }
      
      left   = Math.min(startX, endX);
      right  = Math.max(startX, endX);
      bottom = Math.min(startY, endY);
      top    = Math.max(startY, endY);
      ll     = new Coordinate(left, bottom);
      ur     = new Coordinate(right, top);
      
      this.mapArea = fixAspectRatio(this.getBounds(), new Envelope(ll, ur));
      
      repaint();
    }
    
    protected void processKeyTypedEvent(KeyEvent e){
      
      if ((this.state == STATE_DRAW_POLYGON) || (this.state == STATE_DRAW_POLYGON)){

	    if (KeyEvent.getKeyText(e.getKeyCode()).toUpperCase().equals("S")){
	  
	      if ((this.drawnVertices != null)&&(this.drawnVertices.size() > 0)){
	        this.drawnVertices.remove(this.drawnVertices.size() - 1);
	        repaint();
	      }
        }
      }
    }
    
    private void processDrag(int x1, int y1, int x2, int y2) {
      
        //System.out.println("[processDrag()] Process drag: ("+x1+", "+y1+"); ("+x2+", "+y2+")");
      
        if ((x1 == x2) && (y1 == y2)) {
          /*
            if (isClickable()) {
                mouseClicked(new MouseEvent(this, 0, new Date().getTime(), 0,
                        x1, y1, y2, false));
            }
           */
            return;
        }

        if (this.mapArea == null){
          return;
        }
        
        Rectangle bounds = this.getBounds();

        double mapWidth  = this.mapArea.getWidth();
        double mapHeight = this.mapArea.getHeight();

        double startX = ((x1 * mapWidth) / bounds.width)
                + this.mapArea.getMinX();
        double startY = (((bounds.getHeight() - y1) * mapHeight) / bounds.height)
                + this.mapArea.getMinY();
        double endX = ((x2 * mapWidth) / bounds.width)
                + this.mapArea.getMinX();
        double endY = (((bounds.getHeight() - y2) * mapHeight) / bounds.height)
                + this.mapArea.getMinY();

        if (this.state == JMapPanel.STATE_PAN) {
            // move the image with the mouse
            // calculate X offsets from start point to the end Point
            double deltaX1 = endX - startX;

            // System.out.println("deltaX " + deltaX1);
            // new edges
            double left = this.mapArea.getMinX() - deltaX1;
            double right = this.mapArea.getMaxX() - deltaX1;

            // now for Y
            double deltaY1 = endY - startY;

            // System.out.println("deltaY " + deltaY1);
            double bottom = this.mapArea.getMinY() - deltaY1;
            double top    = this.mapArea.getMaxY() - deltaY1;
            Coordinate ll = new Coordinate(left, bottom);
            Coordinate ur = new Coordinate(right, top);
            
            this.mapArea = fixAspectRatio(bounds, new Envelope(ll, ur));
        } else if (this.state == JMapPanel.STATE_ZOOM_IN) {
            // make the dragged rectangle (in map coords) the new BBOX
            double left = Math.min(startX, endX);
            double right = Math.max(startX, endX);
            double bottom = Math.min(startY, endY);
            double top = Math.max(startY, endY);
            Coordinate ll = new Coordinate(left, bottom);
            Coordinate ur = new Coordinate(right, top);

            this.mapArea = fixAspectRatio(this.getBounds(), new Envelope(ll, ur));
        } else if (this.state == JMapPanel.STATE_ZOOM_OUT) {
            // make the dragged rectangle in screen coords the new map size?
            double left = Math.min(startX, endX);
            double right = Math.max(startX, endX);
            double bottom = Math.min(startY, endY);
            double top = Math.max(startY, endY);
            double nWidth = (mapWidth * mapWidth) / (right - left);
            double nHeight = (mapHeight * mapHeight) / (top - bottom);
            double deltaX1 = left - this.mapArea.getMinX();
            double nDeltaX1 = (deltaX1 * nWidth) / mapWidth;
            double deltaY1 = bottom - this.mapArea.getMinY();
            double nDeltaY1 = (deltaY1 * nHeight) / mapHeight;
            Coordinate ll = new Coordinate(this.mapArea.getMinX() - nDeltaX1,
                    this.mapArea.getMinY() - nDeltaY1);
            double deltaX2 = this.mapArea.getMaxX() - right;
            double nDeltaX2 = (deltaX2 * nWidth) / mapWidth;
            double deltaY2 = this.mapArea.getMaxY() - top;
            double nDeltaY2 = (deltaY2 * nHeight) / mapHeight;
            Coordinate ur = new Coordinate(this.mapArea.getMaxX() + nDeltaX2,
                    this.mapArea.getMaxY() + nDeltaY2);
            this.mapArea = fixAspectRatio(this.getBounds(), new Envelope(ll, ur));
            
        } else if (this.state == JMapPanel.STATE_SELECT) {

        	DirectPosition lastPoint    = this.getTerrainCoordinates(x1, y1);
        	DirectPosition currentPoint = this.getTerrainCoordinates(x2, y2);
                    
          double left = Math.min(lastPoint.getOrdinate(0)  , currentPoint.getOrdinate(0));
          double right = Math.max(lastPoint.getOrdinate(0) , currentPoint.getOrdinate(0));
          double bottom = Math.min(lastPoint.getOrdinate(1), currentPoint.getOrdinate(1));
          double top = Math.max(lastPoint.getOrdinate(1)   , currentPoint.getOrdinate(1));

          int i                      = 0;
          Layer layer             = null;
          Polygon geom               =  null;
          FeatureCollection features = null;
          String name                = null;
          
          Filter f                   = null;
          Coordinate[] coordinates   = null;
          LinearRing edges           = null;
          
          GeometryType gtype= null;
          
          FeatureIterator<SimpleFeature> iter = null;
          
          if (this.selection == null){
            this.selection = new ArrayList<SimpleFeature>();
          } else {
            this.selection.clear();
          }
          
          // Creation de la geometrie representant la primitive de selection
          coordinates = new Coordinate[5];
          coordinates[0] = new Coordinate(left, bottom);
          coordinates[1] = new Coordinate(left, top);
          coordinates[2] = new Coordinate(right, top);
          coordinates[3] = new Coordinate(right, bottom);
          coordinates[4] = new Coordinate(left, bottom);
          
          edges = this.gf.createLinearRing(coordinates);
          geom  = this.gf.createPolygon(edges, null);
         

          while(i < this.selectableLayers.size()){

            layer = this.selectableLayers.get(i);
              
            gtype = layer.getFeatureSource().getSchema().getGeometryDescriptor().getType();
            name  = gtype.getName().getLocalPart();

            if (name == "") {
              name = "the_geom";
            }

            try {
                
             f = this.ff.contains(this.ff.literal(geom), this.ff.property(name)); 
              
             features = layer.getFeatureSource().getFeatures(f);
             
             if ((features != null)&&(features.size() > 0)){
               iter = features.features();
               while(iter.hasNext()){
            	   this.selection.add(iter.next()); 
               }
             }

             fireEvent(new JMapPanelEvent(this, JMapPanelEvent.FEATURE_SELECTED));
              
            } catch (Exception e1) {
            	JIS.logger.log(Level.SEVERE, "Layer selection error: "+e1.getMessage(), e1);
            }
           
            features = null;
            
            i++;
          }
          

          if(this.selectionManager!=null) {
              this.selectionManager.selectionChanged(this, f);
          }
  
          if ((this.selection != null) && (this.selection.size() > 0)){
            repaint();
          }

        } else if ((this.state == JMapPanel.STATE_DRAW_LINE) || (this.state == JMapPanel.STATE_DRAW_POLYGON) || (this.state == JMapPanel.STATE_DRAW_POLYLINE)){
          // move the image with the mouse
          // calculate X offsets from start point to the end Point
          double deltaX1 = endX - startX;

          // System.out.println("deltaX " + deltaX1);
          // new edges
          double left = this.mapArea.getMinX() - deltaX1;
          double right = this.mapArea.getMaxX() - deltaX1;

          // now for Y
          double deltaY1 = endY - startY;

          // System.out.println("deltaY " + deltaY1);
          double bottom = this.mapArea.getMinY() - deltaY1;
          double top = this.mapArea.getMaxY() - deltaY1;
          Coordinate ll = new Coordinate(left, bottom);
          Coordinate ur = new Coordinate(right, top);
          
          this.mapArea = fixAspectRatio(this.getBounds(), new Envelope(ll, ur));
        }

        repaint();
    }
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE FIN EVENEMENT                                                     EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE

//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEURS                                                        AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
 
    /**
     * Get the X position of the cursor un terrain coordinates.
     * @return X the X position of the cursor in terrain coordinates
     * @see #getMapCurrentPoint()
     */
    public double getCursorTerrainX(){
      return this.mapCurrentPoint.getOrdinate(0);
    }
    
    /**
     * Get the Y position of the cursor un terrain coordinates.
     * @return Y the Y position of the cursor in terrain coordinates
     * @see #getMapCurrentPoint()
     */
    public double getCursorTerrainY(){
      return this.mapCurrentPoint.getOrdinate(1);
    }
    
    /**
     * Get the terrain point actually pointed by the mouse cursor.
     * @return the terrain point actually pointed by the mouse cursor.
     */
    public DirectPosition getMapCurrentPoint(){
      return this.mapCurrentPoint;
    }
    
    /**
     * Get if the focus detection is active for the display.
     * @return true if the focus detection is active, false otherwise.
     */
    public boolean isFocusDetectionActive(){
      return this.focusDetectionActive;
    }
    
    /**
     * Set if the focus detection is active for the display.
     * @param true if the focus detection is active, false otherwise.
     */
    public void setFocusDetectionActive(boolean doFocusDetection){
      this.focusDetectionActive = doFocusDetection;
    }
    
    /**
     * Set the collection of focused features. Basically, focused features are
     * features under the cursor.
     * @param features the collection of focused features.
     */
    public void setFocusedFeatures(FeatureCollection features){
      FeatureIterator iter = null;
      
      if (this.focusedFeatures == null){
        this.focusedFeatures = new ArrayList<SimpleFeature>();
      } else {
        this.focusedFeatures.clear();
      }
    
      if ((features != null) && (features.size() > 0)){
        iter = features.features();
        while(iter.hasNext()){
          this.focusedFeatures.add((SimpleFeature)iter.next());
        }
        iter = null;
      }
      repaint();
    }
    
    /**
     * Get the list of focused features. Basically, focused features are
     * features under the cursor.
     * @return the collection of focused features.
     */
    public ArrayList<SimpleFeature> getFocusedFeatures(){
      return this.focusedFeatures;
    }
    
    
    /**
     * get the renderer
     */
    public GTRenderer getRenderer() {
      return this.renderer;
    }

    public void setRenderer(GTRenderer renderer) {
        Map<Object, Object> hints             = new HashMap<Object, Object>();
        RenderingHints rhints = null;
        
        if (renderer instanceof StreamingRenderer) {
            hints = renderer.getRendererHints();
            if (hints == null) {
                hints = new HashMap<Object, Object>();
            }
            if (hints.containsKey(StreamingRenderer.LABEL_CACHE_KEY)) {
                this.labelCache = (LabelCache) hints
                        .get(StreamingRenderer.LABEL_CACHE_KEY);
            } else {
                hints.put(StreamingRenderer.LABEL_CACHE_KEY, this.labelCache);
            }
            
            // Methode de mise à l'echelle (SCALE_ACCURATE ou SCALE_OGC)
            hints.put(StreamingRenderer.SCALE_COMPUTATION_METHOD_KEY, StreamingRenderer.SCALE_OGC);
            
            // Assure au renderer que tous les layers sont dans le même systeme de reference
            if (this.context != null){
            //  hints.put(StreamingRenderer.FORCE_CRS_KEY, getCoordinateReferenceSystem());                
            }
            
            // Le nombre de point par pouces utilisée (90 par defaut)
            hints.put(StreamingRenderer.DPI_KEY, 90);
            
            // La valeur de scale à appliquer au rendu. Par defaut elle est calculée
            // à partir de la taille de la carte et de la taille de la fenetre
            //hints.put(StreamingRenderer.DECLARED_SCALE_DENOM_KEY, null);  
            
            // Les données à afficher sont elle pré-chargées avant le rendu
            //hints.put(StreamingRenderer.MEMORY_PRE_LOADING_KEY, Boolean.FALSE);   
            
            // Is the data loading optimized (in this case, only needed data are loaded)
            //hints.put(StreamingRenderer.OPTIMIZED_DATA_LOADING_KEY, Boolean.TRUE); 
            
            // Methode utilisee pour afficher des textes. (TEXT_RENDERING_STRING ou TEXT_RENDERING_OUTLINE)
            hints.put(StreamingRenderer.TEXT_RENDERING_KEY, StreamingRenderer.TEXT_RENDERING_STRING);
            
            renderer.setRendererHints(hints);
            
            rhints = renderer.getJava2DHints();
            
            if (rhints != null){
              rhints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
              rhints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
              rhints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY); 
            } else {
              rhints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
              rhints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
              rhints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            }
            
            renderer.setJava2DHints(rhints);
        }

        this.renderer = renderer;
        this.highlightRenderer = new StreamingRenderer();
        this.selectionRenderer = new StreamingRenderer();

        hints = new HashMap<Object, Object>();
        hints.put("memoryPreloadingEnabled", Boolean.FALSE);
        this.highlightRenderer.setRendererHints(hints);
        this.selectionRenderer.setRendererHints(hints);

        if (this.context != null) {
            this.renderer.setMapContent(this.context);
        }
    }

    public MapContent getContent() {
        return this.context;
    }

    public void setContext(MapContent context) {
      
        List<Layer> layers = null;
      
        if (this.context != null) {
            this.context.removeMapLayerListListener(this);
        }

        this.context = context;

        if (context != null) {
            this.context.addMapLayerListListener(this);
        }

        if (this.renderer != null) {
          this.renderer.setMapContent(this.context);
        }
        
        if (this.selectableLayers != null){
          this.selectableLayers.clear();
        } else {
          this.selectableLayers = new ArrayList<Layer>();
        }
        
        if (this.focusableLayers != null){
          this.focusableLayers.clear();
        } else {
          this.focusableLayers = new ArrayList<Layer>();
        }
        
        if (context != null){
          
          if (context instanceof ExtendedMapContent){
            setFocusableLayers(((ExtendedMapContent)context).getFocusableLayers());
            setSelectableLayers(((ExtendedMapContent)context).getSelectableLayers());
            
            ((ExtendedMapContent)context).addExtendedMapContentListener(new AWTEventListener(){

              @Override
              public void eventDispatched(AWTEvent event) {
                switch(event.getID()){
                  case ExtendedMapContentEvent.LAYER_SELECTABLILITY_CHANGED:
                    setSelectableLayers(((ExtendedMapContent)event.getSource()).getSelectableLayers());
                    break;
                    
                  case ExtendedMapContentEvent.LAYER_FOCUSABILITY_CHANGED:
                    setFocusableLayers(((ExtendedMapContent)event.getSource()).getFocusableLayers());
                    break;  
                 
                }
                
              }
              
            });
            
          } else {
          
            layers = context.layers();
            if (layers != null){
              for(int i = 0; i < layers.size(); i++){
                this.focusableLayers.add(layers.get(i));
                this.selectableLayers.add(layers.get(i));
              }
            }
            layers = null;
          }
        }
    }

    /**
     * Set the focusable layers. A focusable layer is a layer on which focus events can occurs.
     * Simple example of focus is the focus of a layer feature when the mouse overlaps it.
     * @param layers the layers that can be focused
     * @see #focusableLayers
     * @see #getFocusableLayers()
     */
    public void setFocusableLayers(List<Layer> layers){
      if (layers != null){
        this.focusableLayers = layers;
      }
    }
    
    /**
     * Get the focusable layers. A focusable layer is a layer on which focus events can occurs.
     * Simple example of focus is the focus of a layer feature when the mouse overlaps it.
     * @return the layers that can be focused
     * @see #focusableLayers
     * @see #setFocusableLayers(Collection)
     */
    public List<Layer> getFocusableLayers(){
      return this.focusableLayers;
    }
    
    /**
     * Add a layer to the focusable layer list. A focusable layer is a layer that can be 
     * focused. Simple example of focus is the focus of a layer feature when the mouse overlaps it.
     * @param layer the layer that can be focused
     * @return true if the layer is succesfully added to the focusable layer list
     * @see #focusableLayers
     */
    public boolean setFocusableLayer(Layer layer){
      
      if (layer == null){
        return false;
      } else {
        
        if (this.focusableLayers != null){
          this.focusableLayers.clear();
        } 
      
        if (!this.focusableLayers.contains(layer)){
          return this.focusableLayers.add(layer);
        } else {
          return false;
        }
      }
    }
    
    
    /**
     * Remove a layer from the list of the focusable layers. A focusable layer is a layer that can be 
     * focused. Simple example of focus is the focus of a layer feature when the mouse overlaps it.
     * @param layer the layer to set to unfocusable.
     * @return true if the layer is succesfully removed from the focusable layers.
     */
    public boolean setUnFocusableLayer(Layer layer){
      if (layer == null){
        return false;
      } else {
        if (this.focusableLayers == null){
          return false;
        } else {
          return this.focusableLayers.remove(layer);
        }
      }
    }
    

    /**
     * Set the selectable layers. A selectable layer is a layer on which selection events can occurs.
     * Simple example of selection is the selection of a layer feature when the mouse click it.
     * @param layers the layers that can be selected
     * @see #selectableLayers
     * @see #getSelectableLayers()
     */
    public void setSelectableLayers(List<Layer> layers){
      
      if (layers != null){
        this.selectableLayers = layers;
      }
    }
    
    /**
     * Get the selectable layers. A selectable layer is a layer on which selection events can occurs.
     * Simple example of selection is the selection of a layer feature when the mouse click it.
     * @return the layers that can be selected
     * @see #selectableLayers
     * @see #setSelectableLayers(Collection)
     */
    public List<Layer> getSelectableLayers(){
      return this.selectableLayers;
    }
    
    /**
     * Add a layer to the selectable layer list. A selectable layer is a layer that can be 
     * selected. Simple example of selection is the selection of a layer feature when the mouse click it.
     * @param layer the layer that can be selected
     * @return true if the layer is succesfully added to the selectable layer list
     * @see #selectableLayers
     */
    public boolean setSelectableLayer(Layer layer){
      
      if (layer == null){
        return false;
      } else {
        
        if (this.selectableLayers != null){
          this.selectableLayers.clear();
        } 
      
        if (!this.selectableLayers.contains(layer)){
          return this.selectableLayers.add(layer);
        } else {
          return false;
        }
      }
    }
    

    /**
     * Remove a layer from the list of the selectable layers. A selectable layer is a layer that can be 
     * selected. Simple example of selection is the selection of a layer feature when the mouse click it.
     * @param layer the layer to set to unselectable.
     * @return true if the layer is succesfully removed from the selectable layers.
     */
    public boolean setUnSelectableLayer(Layer layer){
      if (layer == null){
        return false;
      } else {
        if (this.selectableLayers == null){
          return false;
        } else {
          return this.selectableLayers.remove(layer);
        }
      }
    }
 
    
    
    public Envelope getMapArea() {
        return this.mapArea;
    }

    public void setMapArea(ReferencedEnvelope mapArea) {
      
        if (mapArea != null){
          this.mapArea = mapArea;
        
          if (this.keepAspectRatio){
          
            this.mapArea = fixAspectRatio(getBounds(), mapArea);
 
          } else {
            this.mapArea = mapArea;
          }
        
          this.context.getViewport().setBounds(mapArea);
        }
    }

    

    public double getZoomFactor() {
        return this.zoomFactor;
    }

    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    public Layer getSelectionLayer() {
        return this.selectionLayer;
    }

    public void setSelectionLayer(Layer selectionLayer) {
        this.selectionLayer = selectionLayer;
        if(this.selectionManager!=null) {
            this.selectionManager.setSelectionLayer(selectionLayer);
        }
    }

    public boolean isHighlight() {
        return this.highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public Layer getHighlightLayer() {
        return this.highlightLayer;
    }

    public void setHighlightLayer(Layer highlightLayer) {
        this.highlightLayer = highlightLayer;

        if (this.highlightManager != null) {
            this.highlightManager.setHighlightLayer(highlightLayer);
        }
    }

    public HighlightManager getHighlightManager() {
        return this.highlightManager;
    }

    public void setHighlightManager(HighlightManager highlightManager) {
        this.highlightManager = highlightManager;
        this.highlightManager.addHighlightChangeListener(this);
        this.addMouseMotionListener(this.highlightManager);
    }

    public Style getLineHighlightStyle() {
        return this.lineHighlightStyle;
    }

    public void setLineHighlightStyle(Style lineHighlightStyle) {
        this.lineHighlightStyle = lineHighlightStyle;
    }

    public Style getLineSelectionStyle() {
        return this.lineSelectionStyle;
    }

    public void setLineSelectionStyle(Style lineSelectionStyle) {
        this.lineSelectionStyle = lineSelectionStyle;
    }

    public Style getPointHighlightStyle() {
        return this.pointHighlightStyle;
    }

    public void setPointHighlightStyle(Style pointHighlightStyle) {
        this.pointHighlightStyle = pointHighlightStyle;
    }

    public Style getPointSelectionStyle() {
        return this.pointSelectionStyle;
    }

    public void setPointSelectionStyle(Style pointSelectionStyle) {
        this.pointSelectionStyle = pointSelectionStyle;
    }

    public Style getPolygonHighlightStyle() {
        return this.polygonHighlightStyle;
    }

    public void setPolygonHighlightStyle(Style polygonHighlightStyle) {
        this.polygonHighlightStyle = polygonHighlightStyle;
    }

    public Style getPolygonSelectionStyle() {
        return this.polygonSelectionStyle;
    }

    public void setPolygonSelectionStyle(Style polygonSelectionStyle) {
        this.polygonSelectionStyle = polygonSelectionStyle;
    }    
    
    
  /**
   * Set if the panel should keep map aspect ratio when resizing or zomming.
   * @param  keepAspectRatio true if the map aspect ration should be conserved, false otherwise.
   */
  public void setKeepAspectRatio(boolean keepAspectRatio){
    this.keepAspectRatio = keepAspectRatio;
  }
  
  /**
   * Get if the panel should keep map aspect ratio when resizing or zomming.
   * @return  true if the map aspect ration should be conserved, false otherwise.
   */
  public boolean isKeepAspectRatio(){
    return this.keepAspectRatio;
  }
  
  /**
   * Get the state of this map panel. States can be
   * {@link #STATE_ZOOM_IN}, {@link #STATE_ZOOM_OUT}, {@link #STATE_PAN}, 
   * {@link #STATE_SELECT}
   * @param state the state of the map panel.
   * @see #setState(int)
   */
  public int getState() {
    return this.state;
  }

  /**
   * Set the state of this map panel. States can be
   * {@link #STATE_ZOOM_IN}, {@link #STATE_ZOOM_OUT}, {@link #STATE_PAN}, 
   * {@link #STATE_SELECT}
   * @param state the state of the map panel.
   * @see #getState()
   */
  public void setState(int state) {
    this.state = state;  
    
    initState();
  }
  
  /**
   * Return the terrain point corresponding to the point in the map panel
   * @param x the x coordinate of the point in the map panel (pixel)
   * @param y the y coordinate of the point in the map panel (pixel)
   * @return the 3D coordinate of the point in terrain.
   */
  public DirectPosition getTerrainCoordinates(int x, int y){
    DirectPosition2D mapPoint = null;
    
    if (this.mapArea != null){
      Rectangle bounds  = this.getBounds();
      double width      = this.mapArea.getWidth();
      double height     = this.mapArea.getHeight();

      mapPoint = new DirectPosition2D(this.getCoordinateReferenceSystem());
      mapPoint.setOrdinate(0, ((x * width) / bounds.width) + this.mapArea.getMinX());
      mapPoint.setOrdinate(1, (((bounds.getHeight() - y) * height) / bounds.height) + this.mapArea.getMinY());
       
      bounds = null;
    } else {
    	mapPoint = new DirectPosition2D(this.getCoordinateReferenceSystem());
    	mapPoint.setOrdinate(0, x);
    	mapPoint.setOrdinate(1, y);
    }
    
    return mapPoint;
  }
  
  /**
   * Return the terrain point corresponding to the point in the map panel
   * @param point the point in the map panel
   * @return the terrain point corresponding to the point in the map panel
   */
  public DirectPosition getTerrainCoordinates(Point point){
    return getTerrainCoordinates((int)point.getX(), (int)point.getY());
  }
  
  /**
   * Get the point in the displayed map panel corresponding to a terain coordinate.
   * @param mapPoint the point on the map to match
   * @return the pixel coordinate in the map panel corresponding to the map point.
   */
  public IPoint2D getComponentCoordinates(DirectPosition mapPoint){
    IPoint2D pt = null;
    
    if (this.mapArea != null){
      Rectangle bounds  = this.getBounds();
      double width      = this.mapArea.getWidth();
      double height     = this.mapArea.getHeight();

      pt = new IPoint2D(
	   (mapPoint.getOrdinate(0)- this.mapArea.getMinX())*bounds.width/width , 
	   (height - (mapPoint.getOrdinate(1)- this.mapArea.getMinY()))*bounds.height/height);

      bounds = null;
    } else {
      pt = new IPoint2D(mapPoint.getOrdinate(0), mapPoint.getOrdinate(1));
    }
    
    return pt;
  }

  public ArrayList<SimpleFeature> getSelection() {
      return this.selection;
  }
  
  /**
   * Get the selected feature splited into feature collections. A feature collection
   * contain only feature of same type.
   * @return a list of feature collections which contains selected features.
   */
  public ArrayList<DefaultFeatureCollection> getSelectionByFeatureType(){
    ArrayList<SimpleFeatureType> schemas            = null;
    SimpleFeatureType schema                        = null;
    ArrayList<DefaultFeatureCollection> collections = null;
    DefaultFeatureCollection collection             = null;
    SimpleFeature feature                           = null;
    
    if ((this.selection != null) && (this.selection.size() > 0)){
      
      // Recuperation de la liste des types presents dans la selection
      schemas = new ArrayList<SimpleFeatureType>();
      
      for(int i = 0; i < this.selection.size(); i++){
        schema = this.selection.get(i).getFeatureType();
        
        if (!schemas.contains(schema)){
          schemas.add(schema);
        }
      }
      
      // Parcours de la liste des types et creation d'une feature collection
      // par type
      collections = new ArrayList<DefaultFeatureCollection>();
      for(int i = 0; i < schemas.size(); i++){
        schema = schemas.get(i);
        
        collection = new DefaultFeatureCollection("selection-"+schema.getTypeName(), schema);
        
        for(int j = 0; j < this.selection.size(); j++){
          feature = this.selection.get(j);
          
          
          if (feature.getFeatureType().equals(schema)){
            collection.add(feature);
          }
          
          feature = null;
        }
        
        collections.add(collection);
        collection = null;
        schema     = null;
      }
      
      schemas = null;
      
    } else {
      return null;
    }
    
    
    return collections;
  }

  /**
   * Set the current selection of the panel. The selection if a collection of feature.
   * @param selection the new selection (collection of feature).
   */
  public void setSelection(Collection selection) {
    
      Iterator iter = null;
    
      if (this.selection == null){
        this.selection = new ArrayList<SimpleFeature>();
      } else {
        this.selection.clear();
      }
    
      if ((selection != null) && (selection.size() > 0)){
        iter = selection.iterator();
        while(iter.hasNext()){
          this.selection.add((SimpleFeature)iter.next());
        }
        iter = null;
      }
      repaint();
  }
  
  public boolean isDigit(){
    return this.isDigit;
  }
  
  /**
   * Return the CRS used by the map panel. If the value of {@link #useContextCRS} is <code>true</code>, the CRS returned is the attached
   * context CRS. If {@link #useContextCRS} is <code>false</code> or if the attached context is null, this method return the default CRS.
   * @return the Coordinate Reference System used by the map panel
   * @see #isUseContextCRS()
   * @see #setUseContextCRS(boolean)
   * @see #setDefaultCoordinateSystem(CoordinateReferenceSystem)
   * @see #getDefaultCoordinateSystem()
   */
  public CoordinateReferenceSystem getCoordinateReferenceSystem(){
    if ((this.useContextCRS) && (getContent() != null)){
      return getContent().getCoordinateReferenceSystem();
    } else {
      return this.defaultCRS;
    }
  }
  
  /**
   * Set the default CRS used by this map panel.
   * @param crs the default coordinate reference system.
   * @see #isUseContextCRS()
   * @see #setUseContextCRS(boolean)
   * @see #getDefaultCoordinateSystem()
   */
  public void setDefaultCoordinateSystem(CoordinateReferenceSystem crs){
    if (crs != null){
      this.defaultCRS = crs;
    }
  }
  
  /**
   * get the default CRS used by this map panel.
   * @return crs the default coordinate reference system.
   * @see #isUseContextCRS()
   * @see #setUseContextCRS(boolean)
   * @see #setDefaultCoordinateSystem()
   */
  public CoordinateReferenceSystem getDefaultCoordinateSystem(){
    return this.defaultCRS;
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                                    AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

    private org.geotools.styling.Style setupStyle(int type, Color color) {
        StyleFactory sf = org.geotools.factory.CommonFactoryFinder
                .getStyleFactory(null);
        StyleBuilder sb = new StyleBuilder();

        org.geotools.styling.Style s = sf.createStyle();
        s.getDescription().setTitle("selection");

        // TODO parameterise the color
        PolygonSymbolizer ps = sb.createPolygonSymbolizer();
       
        //ps.setFill(sb.createFill(color));
        ps.setFill(null);
        ps.setStroke(sb.createStroke(color));

        LineSymbolizer ls = sb.createLineSymbolizer(color);
        Graphic h = sb.createGraphic();
        h.graphicalSymbols().add(sb.createMark("square", color));

        PointSymbolizer pts = sb.createPointSymbolizer(h);

        // Rule r = sb.createRule(new Symbolizer[]{ps,ls,pts});
        switch (type) {
        case POLYGON:
            s = sb.createStyle(ps);

            break;

        case POINT:
            s = sb.createStyle(pts);

            break;

        case LINE:
            s = sb.createStyle(ls);
        }

        return s;
    }

    @Override
    public void highlightChanged(HighlightChangedEvent e) {
        org.opengis.filter.Filter f = e.getFilter();

        try {
            this.highlightFeature = this.highlightLayer.getFeatureSource().getFeatures(f);
        } catch (IOException e1) {
          JIS.logger.log(Level.SEVERE, "Highlight change error: "+e1.getMessage(), e1);
        }

        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if (prop.equalsIgnoreCase("crs")) {
          this.context.getViewport().setCoordinateReferenceSystem((CoordinateReferenceSystem) evt.getNewValue());
        }
    }

    public boolean isReset() {
        return this.reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    @Override
    public void layerAdded(MapLayerListEvent event) {
        this.changed = true;

        if (this.context.layers().size() == 1) { // the first one
          this.mapArea = this.context.getMaxBounds();
          this.reset = true;
        }

        repaint();
    }

    @Override
    public void layerRemoved(MapLayerListEvent event) {
        this.changed = true;
        repaint();
    }

    @Override
    public void layerChanged(MapLayerListEvent event) {
        this.changed = true;
        // System.out.println("layer changed - repaint");
        repaint();
    }

    @Override
    public void layerMoved(MapLayerListEvent event) {
        this.changed = true;
        repaint();
    }

    
    private void drawPoint(IPoint2D pt, Graphics graphics){
      graphics.drawLine((int)pt.getX()-3, (int)pt.getY(), (int)pt.getX()+ 3, (int)pt.getY());
      graphics.drawLine((int)pt.getX(), (int)pt.getY()-3, (int)pt.getX(), (int)pt.getY()+3);
    }
    
    private void drawPolyLine(ArrayList<IPoint2D> vertices, Graphics graphics){
     
      int x1 = 0;
      int y1 = 0;
      int x2 = 0;
      int y2 = 0;
      
      IPoint2D pt = null;
      
      if (vertices != null){
	
	// Dessin des arretes
	for(int i = 0; i < vertices.size() - 1; i++){
	  
	  pt = vertices.get(i);
	  x1 = (int)pt.getX();
	  y1 = (int)pt.getY();
	  
	  pt = vertices.get(i+1);
	  x2 = (int)pt.getX();
	  y2 = (int)pt.getY();
	  
	  graphics.drawLine(x1, y1, x2, y2);
	}
	
	// Dessin des points
	for(int i = 0; i < vertices.size(); i++){
	  drawPoint(vertices.get(i%vertices.size()), graphics);
	}
      }
      
    }
    
    private void drawLine(IPoint2D pt1, IPoint2D pt2, Graphics graphics){

      // Ligne
      graphics.drawLine((int)pt1.getX(), (int)pt1.getY(), (int)pt2.getX(), (int)pt2.getY());
      
      // Point de la ligne
      drawPoint(pt1, graphics);
      drawPoint(pt2, graphics);
      
    }
    
    private void drawRectangle(Graphics graphics) {
        // undraw last box/draw new box
        int left   = Math.min(this.startX, this.lastX);
        int right  = Math.max(this.startX, this.lastX);
        int top    = Math.max(this.startY, this.lastY);
        int bottom = Math.min(this.startY, this.lastY);
        int width  = right - left;
        int height = top - bottom;
        graphics.drawRect(left, bottom, width, height);
    }
    
    /**
     * Set if the renderer consider all layers in the same reference system.
     * @param useContextCRS true if the rendered have to consider that every layers are
     * in the same CRS. False otherwise
     */
    public void setUseContextCRS(boolean useContextCRS){
      this.useContextCRS = useContextCRS;
    }
    
    /**
     * Get if the renderer consider all layers in the same reference system.
     * @return true if the rendered have to consider that every layers are
     * in the same CRS. False otherwise
     */
    public boolean isUseContextCRS(){
      return this.useContextCRS;
    }

    /* (non-Javadoc)
     * @see org.geotools.gui.swing.event.SelectionChangeListener#selectionChanged(org.geotools.gui.swing.event.SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent e) {
/*
        try {
            selection = selectionLayer.getFeatureSource().getFeatures(e.getFilter());
            repaint();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        */
      
      repaint();
    }

    public SelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    public void setSelectionManager(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
        this.selectionManager.addSelectionChangeListener(this);

    }
    
    
    public void selectFeatures(int x, int y){
      
      List<SimpleFeature> featureList = null;
      
      if (this.selection == null){
        this.selection = new ArrayList<SimpleFeature>();
      } else {
        this.selection.clear();
      }
      
      featureList = getFeatures(x, y, this.selectableLayers);
      
      if (featureList != null){
        this.selection.addAll(featureList);
      }
      
      repaint();
      
      fireEvent(new JMapPanelEvent(this, JMapPanelEvent.FEATURE_SELECTED));
    }
    
    
    public void focusFeatures(int x, int y){
      
      List<SimpleFeature> featureList = null;
           
      if (this.focusedFeatures == null){
        this.focusedFeatures = new ArrayList<SimpleFeature>();
      } else {
        this.focusedFeatures.clear();
      }
      
      featureList = getFeatures(x, y, this.focusableLayers);
      
      if (featureList != null){
        this.focusedFeatures.addAll(featureList);
      }
      
      fireEvent(new JMapPanelEvent(this, JMapPanelEvent.FEATURE_FOCUSED));
      
    }
    
    
    protected List<SimpleFeature> getFeatures(int x, int y, List<Layer> layers){
      
 
    	DirectPosition terrainPt = null;
      
      Filter f          = null;
      Geometry geometry = null;
      String name       = null;
      
      Layer layer    = null;
      
      FeatureCollection features = null;
      
      FeatureIterator<SimpleFeature> iter = null;
      
      List<SimpleFeature> featureList = null;
      
      boolean found     = false;
      int i             = 0;
      
      // Si la carte n'est pas valide, aucune sélection possible
      if (this.mapArea == null){
        return null;
      }
      
      // Recuperation des coordonnees terrain du point exprimé en pixels
      terrainPt = this.getTerrainCoordinates(x, y);
  
      geometry = this.gf.createPoint(new Coordinate(terrainPt.getOrdinate(0), terrainPt.getOrdinate(1)));
 
      featureList = new ArrayList<SimpleFeature>();
        
      i = 0;
      while((!found)&&(i < layers.size())){

        layer = layers.get(i);
            
        name = layer.getFeatureSource().getSchema().getGeometryDescriptor().getName().getLocalPart();
        
        if (name == "") {
          name = "the_geom";
        }
            
        f = this.ff.contains(this.ff.property(name), this.ff.literal(geometry));
            
        try {
                         
          features = layer.getFeatureSource().getFeatures(f);
          
          iter = features.features();
          while(iter.hasNext()){
        	 featureList.add(iter.next()); 
          }

        } catch (IOException e1) {
          JIS.logger.log(Level.SEVERE, "Cannot get feature from source: "+e1.getMessage(), e1);
        }
         
        i++;
      }
       
      f        = null;
      geometry = null;
      name     = null;
      layer    = null;
      
      return featureList;
    }
    
    /**
     * Get the currently digitalized line
     * @return the currently digitalized line.
     */
    public ILine3D getDigitLine(){
      ILine3D line = null;

      //line = new Segment3D(drawnLinePt1, drawnLinePt2);
      
      return line;
    }
    
    /**
     * Get the currently digitalized polygon
     * @return the currently digitalized polygon
     */
    public IPolygon3D getDigitPolygon(){
      IPolygon3D polygon = null;

      //polygon = new Polygon3D(drawnVertices);
      
      return polygon;
    }
    
    /**
     * Get the currently digitalized line set.
     * @return the currently digitalized line set
     */
    public ILineSet3D getDigitLineSet(){
    	ILineSet3D lineSet = null;
      
      //lineSet = new LineSet3D(drawnVertices);
      
      return lineSet;
    }
    
    private JMapPanel getRef(){
      return this;
    }

    @Override
    public void layerPreDispose(MapLayerListEvent event) {
      // TODO Auto-generated method stub
      
    }
}

class IPoint2D {
	double x;
	double y;
	
	public IPoint2D(double x, double y) {
		this.x = 0.0d;
		this.y = 0.0d;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
}

class ILine3D{
	
}

class IPolygon3D{
	
}

class ILineSet3D{
	
}