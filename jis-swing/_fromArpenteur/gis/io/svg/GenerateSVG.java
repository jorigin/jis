package org.arpenteur.gis.io.svg;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.w3c.dom.Document;

/**
 * This is a simple support class which allows you to generate an SVG file from a map.
 *
 * To use, setup a Map object with the layers you want to render, create an envelope for the
 * region to be drawn and pass in an OutputStream (probably attached to a new file) for the
 * resulting SVG information to be stored in.
 *
 * Optionaly you can change the default size of the SVG cavas (in effect increasing the resolution)
 * by calling setCavasSize before calling go.
 *
 * @author James Macgill, PennState
 * @source $URL: http://svn.geotools.org/tags/2.5.2/demo/svgsupport/src/main/java/org/geotools/svg/GenerateSVG.java $
 * @version $Id: GenerateSVG.java 30575 2008-06-08 12:03:10Z acuster $
 */
public class GenerateSVG {
    
   /**
    * This flag represents that no dimension can be automatically resized to match map ratio.
    * @see #RESIZEABLE_WIDTH
    * @see #RESIZEABLE_WIDTH
    * @see #RESIZEABLE_BOTH
    * @see #setResizeableDimension(int)
    * @see #getReszeableDimension()
    */
    public static final int RESIZEABLE_NONE   = 0;
  
   /**
    * This flag represents that the canvas width can be automatically resized to match map ratio.
    * @see #RESIZEABLE_NONE
    * @see #RESIZEABLE_HEIGHT
    * @see #RESIZEABLE_BOTH
    * @see #setResizeableDimension(int)
    * @see #getReszeableDimension()
    */
    public static final int RESIZEABLE_WIDTH  = 1;
    
    /**
     * This flag represents that the canvas height can be automatically resized to match map ratio.
     * @see #RESIZEABLE_NONE
     * @see #RESIZEABLE_WIDTH
     * @see #RESIZEABLE_BOTH
     * @see #setResizeableDimension(int)
     * @see #getReszeableDimension()
     */
    public static final int RESIZEABLE_HEIGHT = 2;
    
    /**
     * This flag represents that the canvas height and width can be automatically resized to match map ratio.
     * @see #RESIZEABLE_NONE
     * @see #RESIZEABLE_WIDTH
     * @see #RESIZEABLE_HEIGHT
     * @see #setResizeableDimension(int)
     * @see #getReszeableDimension()
     */
    public static final int RESIZEABLE_BOTH   = 3;
  
    /**
     * If <code>true</code> the aspect ratio is kept when drawing the map.
     * @see #isKeepMapRatio()
     * @see #setKeepMapRatio(boolean)
     */
    boolean keepMapRatio = true;
  
    private final int resizeableDimension = RESIZEABLE_WIDTH;
    
    private Dimension canvasSize = new Dimension(800, 800);
    
    /**
     * Creates a new instance of GenerateSVG.
     */
    public GenerateSVG() {
    }

    /** Generate an SVG document from the supplied information.
     * Note, call setCavasSize first if you want to change the default output size.
     * @param map Contains the layers (features + styles) to be rendered
     * @param env The portion of the map to generate an SVG from
     * @param out Stream to write the resulting SVG out to (probable should be a new file)
     * @throws IOException Should anything go wrong whilst writing to 'out'
     * @throws ParserConfigurationException If critical XML tools are missing from the classpath
     */    
    public void go(MapContent map, ReferencedEnvelope env, OutputStream out) throws IOException, ParserConfigurationException {
        SVGGeneratorContext ctx = setupContext();
        ctx.setComment("Generated by GeoTools2 with Batik SVG Generator");

        SVGGraphics2D g2d = new SVGGraphics2D(ctx,
                true);

        // Mise à l'echelle du canvas
        double mapRatio = env.getHeight() / env.getWidth();
        
        if (keepMapRatio){
          switch(resizeableDimension){
            case RESIZEABLE_WIDTH:
              g2d.setSVGCanvasSize(new Dimension((int)(canvasSize.getHeight()/mapRatio), (int)canvasSize.getHeight()));
              break;
            
            case RESIZEABLE_HEIGHT:
              g2d.setSVGCanvasSize(new Dimension((int)canvasSize.getWidth(), (int)(canvasSize.getWidth()*mapRatio)));
              break;
              
            default:
              g2d.setSVGCanvasSize(getCanvasSize());
          }
        }

        renderMap(map, env, g2d);
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(out, "UTF-8");
            g2d.stream(osw);
        } finally {
            if(osw != null)
                osw.close();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param map
     * @param env
     * @param g2d
     */
    private void renderMap(final MapContent map, ReferencedEnvelope env, final SVGGraphics2D g2d) throws IOException {
        StreamingRenderer renderer = new StreamingRenderer();    
        
        Map<Object, Object> hints             = new HashMap<Object, Object>();
        
        hints =  renderer.getRendererHints();
        if (hints == null) {
          hints = new HashMap<Object, Object>();
        }
       

        // Methode de mise à l'echelle (SCALE_ACCURATE ou SCALE_OGC)
        hints.put(StreamingRenderer.SCALE_COMPUTATION_METHOD_KEY, StreamingRenderer.SCALE_OGC);

        // Le nombre de point par pouces utilisée (90 par defaut)
        hints.put(StreamingRenderer.DPI_KEY, 90);

        // La valeur de scale à appliquer au rendu. Par defaut elle est calculée
        // à partir de la taille de la carte et de la taille de la fenetre
        //hints.put(StreamingRenderer.DECLARED_SCALE_DENOM_KEY, null);  

        // Methode utilisee pour afficher des textes. (TEXT_RENDERING_STRING ou TEXT_RENDERING_OUTLINE)
        hints.put(StreamingRenderer.TEXT_RENDERING_KEY, StreamingRenderer.TEXT_RENDERING_STRING);

        renderer.setRendererHints(hints);

        

        RenderingHints rhints = null;
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

        


        renderer.setMapContent(map);
        
        Rectangle outputArea = new Rectangle(g2d.getSVGCanvasSize());
        
        if (env == null){
          env = map.getMaxBounds();
        }
        
        renderer.paint(g2d, outputArea, env);
    }

    /**
     * DOCUMENT ME!
     *
     * @return
     *
     * @throws FactoryConfigurationError
     * @throws ParserConfigurationException
     */
    private SVGGeneratorContext setupContext() throws FactoryConfigurationError, ParserConfigurationException {
        Document document = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory
            .newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Create an instance of org.w3c.dom.Document
        document = db.getDOMImplementation().createDocument(null, "svg", null);

        // Set up the context
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);

        return ctx;
    }

    public Dimension getCanvasSize() {
        return this.canvasSize;
    }

    public void setCanvasSize(final Dimension size) {
        this.canvasSize = size;
    }
    
    public void setKeepMapRatio(boolean keep){
      keepMapRatio = keep;
    }
    
    public boolean isKeepMapRatio(){
      return keepMapRatio;
    }
    
    public void setResizeableDimension(int dimension){
      
    }
    
    public int getReszeableDimension(){
      return resizeableDimension;  
    }
}