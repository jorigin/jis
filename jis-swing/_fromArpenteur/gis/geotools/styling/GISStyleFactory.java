package org.arpenteur.gis.geotools.styling;

import java.awt.Color;

import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;


public class GISStyleFactory {


  
  /** The used style builder */
  StyleBuilder styleBuilder              = new StyleBuilder();
  
  /** The default stroke color for line*/
  private Color lineStrokeColor          = new Color(255,20,20);
  
  /** The default line stroke opacity */
  private double lineStrokeOpacity       = 1.0d;
  
  /** The default stroke width for line */
  private double lineStrokeWidth         = 1.0d;
  
  /** The default stroke color for polygons */
  private Color polygonStrokeColor       = new Color(163,151,97);
  
  /** The default polygon stroke opacity */
  private double polygonStrokeOpacity    = 1.0d;
  
  /** The default stroke width for polygons */
  private final double polygonStrokeWidth      = 2.0d;
  
  /** The default fill color for polygons */
  private Color polygonFillColor         = new Color(253, 241, 187);
  
  /** The default polygon fill opacity */
  private double polygonFillOpacity      = 0.3d;
  
  /** The default stroke color for line string*/
  private Color lineStringStrokeColor    = new Color(255,20,20);
  
  /** The default line string stroke opacity */
  private double lineStringStrokeOpacity = 1.0d;
  
  /** The default stroke width for line string */
  private double lineStringStrokeWidth   = 1.0d;
  
  
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
 
  /**
   * Construct a new default style creator.
   */
  public GISStyleFactory(){
    
  }
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

  /**
   * Create a style associated to the feature type given in parameter.
   * @param schema the type attached to the style
   * @return Style the style.
   */
  public Style createStyle(SimpleFeatureType schema){
    
    Class<?> geometryName            = null;

    Style style                   = null;
    StyleFactory sf               = null;
    Stroke stroke                 = null;
    Fill fill                     = null;
    Symbolizer symbolizer         = null;  

    Rule rule                     = null;
    
    FeatureTypeStyle type         = null;
   
    
    
    if (schema != null){
      
      // 1) Initialisation des fabricants
      sf = CommonFactoryFinder.getStyleFactory(GeoTools.getDefaultHints());

      //System.out.println("[GISA_Frame] [loadShapeFile(String)] type "+i+": "+typeNames[i]);

      // Creation de styles **************************************************************
      // Attributs de style pour les objets
      geometryName = schema.getGeometryDescriptor().getType().getBinding();
        
        
      // Géométrie composée de points
      if (geometryName.getName().toUpperCase().endsWith("POINT")){
         
        System.out.println("[GaiaStyleCreator] [createStyle(FeatureSource)] Create Point style");
          
        stroke          = styleBuilder.createStroke(polygonStrokeColor, polygonStrokeWidth, polygonStrokeOpacity);
        fill            = styleBuilder.createFill(polygonFillColor, polygonFillOpacity);
          symbolizer      = sf.createPointSymbolizer();

          style = styleBuilder.createStyle();
          style.featureTypeStyles().add(styleBuilder.createFeatureTypeStyle(schema.getTypeName(), symbolizer));
          
        // Géométrie composée de ligne  
        } else if (geometryName.getName().toUpperCase().endsWith("LINE")){
          
          System.out.println("[GaiaStyleCreator] [createStyle(FeatureSource)] Create Line style");
          
          stroke          = styleBuilder.createStroke(lineStrokeColor, lineStrokeWidth, lineStrokeOpacity);
          symbolizer      = sf.createLineSymbolizer();

          ((LineSymbolizer)symbolizer).setStroke(stroke);
          style = styleBuilder.createStyle();
          style.featureTypeStyles().add(styleBuilder.createFeatureTypeStyle(schema.getTypeName(), symbolizer));
          
          
        // Géométrie composée d'ensembles de lignes
        } else if (geometryName.getName().toUpperCase().endsWith("LINESTRING")){
          
          System.out.println("[GaiaStyleCreator] [createStyle(FeatureSource)] Create Line style");
          
          stroke          = styleBuilder.createStroke(lineStringStrokeColor, lineStringStrokeWidth, lineStringStrokeOpacity);
          symbolizer      = sf.createLineSymbolizer();

          ((LineSymbolizer)symbolizer).setStroke(stroke);
          style = styleBuilder.createStyle();
          style.featureTypeStyles().add(styleBuilder.createFeatureTypeStyle(schema.getTypeName(), symbolizer));
          
          
        // Géométrie composée de polygones
        } else if (geometryName.getName().toUpperCase().endsWith("POLYGON")){
          
          System.out.println("[GaiaStyleCreator] [createStyle(FeatureSource)] Create Polygon style");
          
          stroke          = styleBuilder.createStroke(polygonStrokeColor, polygonStrokeWidth, polygonStrokeOpacity);
          fill            = styleBuilder.createFill(polygonFillColor, polygonFillOpacity);
          symbolizer      = sf.createPolygonSymbolizer();

          ((PolygonSymbolizer)symbolizer).setStroke(stroke);
          ((PolygonSymbolizer)symbolizer).setFill(fill);
          
          /*
          style = styleBuilder.createStyle();
          
          
          //style.addFeatureTypeStyle(styleBuilder.createFeatureTypeStyle(symbolizer));
          style.addFeatureTypeStyle(styleBuilder.createFeatureTypeStyle(schema.getTypeName(), symbolizer));
          */
          
          type = sf.createFeatureTypeStyle();
          type.featureTypeNames().add(new NameImpl(schema.getTypeName()));
          type.setName("GUI_SITE_LB");
          
          // Regle d'affichages pour les blocs
          /*
          fill            = styleBuilder.createFill(new Color(212, 155, 112), polygonFillOpacity);
          symbolizer      = sf.createPolygonSymbolizer();
          ((PolygonSymbolizer)symbolizer).setStroke(stroke);
          ((PolygonSymbolizer)symbolizer).setFill(fill);
          rule = sf.createRule();
          rule.setFilter(ff.equals(ff.literal("usmId"), ff.literal("5050")));
          rule.setSymbolizers( new Symbolizer[]{ symbolizer });
          rule.setName(LangResourceBundle.getString("5050"));    
          type.addRule( rule );
          */
          // ----------------------------------------------------------------------------------
          
          
          
          rule = sf.createRule();
          rule.setFilter(Filter.INCLUDE);
          rule.symbolizers().clear();
          rule.symbolizers().add(symbolizer);
          rule.setName("GUI_ALL_LB");    
          
          type.rules().clear();
          type.rules().add(rule);
            
          style = sf.createStyle();
          style.featureTypeStyles().add(type);
          style.setName("GUI_SITE_DEFAULT_STYLE_LB");  
  
        // Geometrie raster.  
        } else if (geometryName.getName().toUpperCase().endsWith("RASTER")){
          System.err.println("[GaiaStyleCreator] [createStyle(DataStore)] raster geometry not yet handled");
        } else {
          System.err.println("[GaiaStyleCreator] [createStyle(DataStore)] Cannot determine geometry style for "+geometryName.getName());
        }
        
        System.out.println("[GaiaStyleCreator] [createStyle(DataStore)] geometry: "+geometryName);
        
        
        
        // ***********************************************************************************

        return style;
 
    } else {
      return null;
    }
  }
  
  /**
   * Create a new style according to the features type and the parameter of the creator.
   * @param featureSource the feature source providing the features to be styled.
   */
  public Style createStyle(FeatureSource<?, ?> featureSource){
    
    if (featureSource != null){
      return createStyle((SimpleFeatureType)featureSource.getSchema());
    } else {
      return null;
    } 
  }
  
  /**
   * Create a new style according to the features type of the element contained into the collection
   * @param features the collection to style
   * @return the new style.
   */
  public Style createStyle(FeatureCollection<?, ?> features){
    if (features != null){
      return createStyle((SimpleFeatureType)features.getSchema());
    } else {
      return null;
    } 
  }
  
  /**
   * Create a default raster style.
   * @return the default raster style.
   */
  public Style createRasterStyle(){
    RasterSymbolizer raster = styleBuilder.createRasterSymbolizer();
            
    Style style = styleBuilder.createStyle(raster);
    
    return style;
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEURS                                                     AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  /**
   * Set the style builder to use for the geotools style creation
   * @param builder the style builder to use
   */
  public void setStyleBuilder(StyleBuilder builder){
    styleBuilder = builder;
  }
  
  /**
   * Get the style builder used for the geotools style creation
   * @return the style builder used
   */
  public StyleBuilder getStyleBuilder(){
    return styleBuilder;
  }
  
  /**
   * Set the color of the stroke used for representing a line geometry
   * @param color the color of the stroke
   */
  public void setLineStrokeColor(Color color){
    lineStrokeColor = color;
  }
  
  /**
   * Get the color of the stroke used for representing a line geometry
   * @return the color of the stroke
   */
  public Color getLineStrokeColor(){
    return lineStrokeColor;
  }
  
  /**
   * Set the opacity of the stroke used for representing a line geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @param opacity the opacity value
   */
  public void setLineStrokeOpacity(double opacity){
    if ((opacity > 0.0d) && (opacity <= 1.0d)){
      lineStrokeOpacity = opacity;
    }
  }
  
  /**
   * Get the opacity of the stroke used for representing a line geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @return the opacity value
   */
  public double getLineStrokeOpacity(){
    return lineStrokeOpacity;
  }
  
  /**
   * Set the width of the stroke used for representing a line geometry. The width must 
   * be a positive <code>double</code>.
   * @param width the width of the stroke
   */
  public void setLineStrokeWidth(double width){
    if (width > 0.0d){
      lineStrokeWidth = width;
    }
  }
  
  /**
   * Get the width of the stroke used for representing a line geometry. The width is 
   * a positive <code>double</code>.
   * @return the width of the stroke
   */
  public double getLineStrokeWidth(){
    return lineStrokeWidth;
  }
  
  /**
   * Set the color of the stroke used for representing a polygon geometry
   * @param color the color of the stroke
   */
  public void setPolygonStrokeColor(Color color){
    polygonStrokeColor = color;
  }
  
  /**
   * Get the color of the stroke used for representing a polygon geometry
   * @return the color of the stroke
   */
  public Color getPolygonStrokeColor(){
    return polygonStrokeColor;
  }
  
  /**
   * Set the opacity of the stroke used for representing a polygon geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @param opacity the opacity value
   */
  public void setPolygonStrokeOpacity(double opacity){
    if ((opacity > 0.0d) && (opacity <= 1.0d)){
     polygonStrokeOpacity = opacity;
    }
  }
  
  /**
   * Get the opacity of the stroke used for representing a polygon geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @return the opacity value
   */
  public double getPolygonStrokeOpacity(){
    return polygonStrokeOpacity;
  }
  
  /**
   * Set the width of the stroke used for representing a polygon geometry. The width must 
   * be a positive <code>double</code>.
   * @param width the width of the stroke
   */
  public void setPolygonStrokeWidth(double width){
    if (width > 0.0d){
      lineStrokeWidth = width;
    }
  }
  
  /**
   * Get the width of the stroke used for representing a polygon geometry. The width is 
   * a positive <code>double</code>.
   * @return the width of the stroke
   */
  public double getPolygonStrokeWidth(){
    return polygonStrokeWidth;
  }
  
  /**
   * Set the color used to fill the polygon representing a polygon geometry.
   * @param color the color of the fill.
   */
  public void setPolygonFillColor(Color color){
    polygonFillColor = color;
  }
  
  /**
   * Get the color used to fill the polygon representing a polygon geometry.
   * @return the color of the fill.
   */
  public Color getPolygonFillColor(){
    return polygonFillColor;
  }
  
  /**
   * Set the opacity of the fill used for representing a polygon geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @param opacity the opacity value
   */
  public void setPolygonFillOpacity(double opacity){
    if ((opacity > 0.0d) && (opacity <= 1.0d)){
      polygonFillOpacity = opacity;
    }
  }
  
  /**
   * Get the opacity of the fill used for representing a polygon geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @return the opacity value
   */
  public double getPolygonFillOpacity(){
    return polygonFillOpacity;
  }
  
  
  /**
   * Set the color of the stroke used for representing a line string geometry
   * @param color the color of the stroke
   */
  public void setLineStringStrokeColor(Color color){
    lineStringStrokeColor = color;
  }
  
  /**
   * Get the color of the stroke used for representing a line string geometry
   * @return the color of the stroke
   */
  public Color getLineStringStrokeColor(){
    return lineStringStrokeColor;
  }
  
  /**
   * Set the opacity of the stroke used for representing a line string geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @param opacity the opacity value
   */
  public void setLineStringStrokeOpacity(double opacity){
    if ((opacity > 0.0d) && (opacity <= 1.0d)){
      lineStringStrokeOpacity = opacity;
    }
  }
  
  /**
   * Get the opacity of the stroke used for representing a line string geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @return the opacity value
   */
  public double getLineStringStrokeOpacity(){
    return lineStringStrokeOpacity;
  }
  
  /**
   * Set the width of the stroke used for representing a line string geometry. The width must 
   * be a positive <code>double</code>.
   * @param width the width of the stroke
   */
  public void setLineStringStrokeWidth(double width){
    if (width > 0.0d){
      lineStringStrokeWidth = width;
    }
  }
  
  /**
   * Get the width of the stroke used for representing a line string geometry. The width is 
   * a positive <code>double</code>.
   * @return the width of the stroke
   */
  public double getLineStringStrokeWidth(){
    return lineStringStrokeWidth;
  }

//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                                 AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

}
