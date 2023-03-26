package org.jorigin.jis.wrap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Iterator;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;


/**
 * This class is a wrapper between Java AWT geometry and Geotools styling geometry.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class AWTGeometryWrap {

  private static FilterFactory ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
  
  /**
   * Wrap a geotools stroke into a java awt stroke.
   * @param stroke the geotools stroke to wrap
   * @return the java AWT stroke representing the geotools stroke.
   */
  public static java.awt.Stroke wrapStroke(org.geotools.styling.Stroke stroke){
    java.awt.Stroke wrappedStroke = null;
    
    float strokeWidth            = 1.0f; 
    int strokeCap                = BasicStroke.CAP_SQUARE; 
    int strokeJoin               = BasicStroke.JOIN_MITER; 
    float strokeMiter            = 10.0f;
    float[] strokeDash           = null;
    float strokeDashPhase        = 0.0f;
    
    String cap                   = null;
    String join                  = null;
    
    // Recuperation de l'epaisseur de ligne 
    try {
      strokeWidth = Float.parseFloat(stroke.getWidth().toString());
    } catch (NumberFormatException e) {
      System.err.println("[AWTGeometryWrap] [wrapStroke(Stroke)] cannot read width "+stroke.getWidth());
      System.err.println("[AWTGeometryWrap] [wrapStroke(Stroke)] "+e.getMessage());
      e.printStackTrace(System.err);
    }
    
    // Recuperation du style de fin de ligne
    cap = stroke.getLineCap().toString();
//    System.out.println("[AWTGeometryWrap] [wrapStroke(Stroke)] cap: "+cap);
    
    if (cap.toUpperCase().equals("BUTT")){
      strokeCap = BasicStroke.CAP_BUTT;
    } else if (cap.toUpperCase().equals("ROUND")){
      strokeCap = BasicStroke.CAP_ROUND;
    } else if (cap.toUpperCase().equals("SQUARE")){
      strokeCap = BasicStroke.CAP_SQUARE;
    }
    
    // Recuperation du modele de jointure de ligne
    join = stroke.getLineJoin().toString();
//    System.out.println("[AWTGeometryWrap] [wrapStroke(Stroke)] join: "+join);
    
    if (join.toUpperCase().equals("MITER")){
      strokeJoin = BasicStroke.JOIN_MITER;
    } else if (join.toUpperCase().equals("ROUND")){
      strokeJoin = BasicStroke.JOIN_ROUND;
    } else if (join.toUpperCase().equals("BEVEL")){
      strokeJoin = BasicStroke.JOIN_BEVEL;
    }
    
    // Recuperation du modèle de pointillés
    if ((stroke.dashArray() != null) && (stroke.dashArray().size() > 0)){
      strokeDash = new float[stroke.dashArray().size()];
      Iterator<Expression> iter = stroke.dashArray().iterator();
      int i = 0;
      while(iter.hasNext()){
        strokeDash[i] = (Float) iter.next().evaluate(null);
        i++;
      }
      
    }
    
    try {
      strokeDashPhase = Float.parseFloat(stroke.getDashOffset().toString());
    } catch (NumberFormatException e) {
      System.err.println("[AWTGeometryWrap] [wrapStroke(Stroke)] cannot read dash phase "+stroke.getDashOffset().toString());
      System.err.println("[AWTGeometryWrap] [wrapStroke(Stroke)] "+e.getMessage());
      e.printStackTrace(System.err);
    }
    
    wrappedStroke = new BasicStroke(strokeWidth, strokeCap, strokeJoin, 
                                    strokeMiter, strokeDash, strokeDashPhase);
    
    return wrappedStroke;
  }
  
  
  /**
   * Wrap a color from a geotools expression.
   * @param expr the expression to wrap
   * @return a java AWT color corresponding to the expression
   */
  public static Color wrapColor(Expression expr){
    
    String str  = null;
    Color color = null;
    String rHx  = null;
    String gHx  = null;
    String bHx  = null;
    
    int r = 0;
    int g = 0;
    int b = 0;

    if (expr == null){
      return null;
    }
    
    str = ""+expr.toString();
    
    if ((str != null) && (str.startsWith("#")) && (str.length() == 7)){
      rHx = str.substring(1, 3).toUpperCase();
      gHx = str.substring(3, 5).toUpperCase();
      bHx = str.substring(5, 7).toUpperCase();
      
      try {
        r = Integer.decode("0X"+rHx);
        g = Integer.decode("0X"+gHx);
        b = Integer.decode("0X"+bHx);
        
//        System.out.println("toColor (rgb) ("+r+", "+g+", "+b+")");
        
      } catch (NumberFormatException e) {
        System.err.println("[AWTGeometryWrap] wrapColor(String) badcolor identifier "+str);
        System.err.println("[AWTGeometryWrap] wrapColor(String) should be #RRGGBB");
        e.printStackTrace();
      }
      
      color = new Color(r, g, b);
      
    } else {
      System.err.println("[AWTGeometryWrap] wrapColor(String) badcolor identifier "+str);
      System.err.println("[AWTGeometryWrap] wrapColor(String) should be #RRGGBB");
      
      color = Color.black;
    }
    
    rHx = null;
    gHx = null;
    bHx = null;
    
    return color;
  }
  
  /**
   * Return the expression corresponding to the color given in parameter.
   * @param color the color to wrap into geotools expression
   * @return the color expression
   */
  public static Expression wrapColor(Color color){
    
    String str ="#";
    int r      = 0;
    int g      = 0;
    int b      = 0;
    
    if (color == null){
      str += "000000";
    } else {
      r      = color.getRed();
      g      = color.getGreen();
      b      = color.getBlue();
      
      if (r > 15){
        str += Integer.toHexString(r);
      } else {
	str += "0"+Integer.toHexString(r);
      }
      
      if (g > 15){
        str += Integer.toHexString(g);
      } else {
	str += "0"+Integer.toHexString(g);
      }
      
      if (b > 15){
        str += Integer.toHexString(b);
      } else {
	str += "0"+Integer.toHexString(b);
      }
      
      str = str.toUpperCase();
    }

    return ff.literal(str);
  }
  
  /**
   * Create a {@link org.opengis.filter.expression.Literal literal expression} from an <code>int</code>.
   * @param value the <code>int</code> to convert.
   * @return the {@link org.opengis.filter.expression.Literal literal expression} that represents the input.
   */
  public static Literal createExpression(int value){
    return ff.literal(value);
  }
  
  /**
   * Create a {@link org.opengis.filter.expression.Literal literal expression} from a <code>double</code>.
   * @param value the <code>double</code> to convert.
   * @return the {@link org.opengis.filter.expression.Literal literal expression} that represents the input.
   */
  public static Literal createExpression(double value){
    return ff.literal(value);
  }
}
