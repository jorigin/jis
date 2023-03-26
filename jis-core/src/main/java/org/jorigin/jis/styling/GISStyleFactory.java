package org.jorigin.jis.styling;

import java.awt.Color;

import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
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
import org.geotools.util.factory.GeoTools;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * 
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
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
  private double polygonStrokeWidth      = 2.0d;
  
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

      //System.out.println("[GaiaFrame] [loadShapeFile(String)] type "+i+": "+typeNames[i]);

      // Creation de styles **************************************************************
      // Attributs de style pour les objets
      geometryName = schema.getGeometryDescriptor().getType().getBinding();
        
        
      // Géométrie composée de points
      if (geometryName.getName().toUpperCase().endsWith("POINT")){
         
        System.out.println("[GaiaStyleCreator] [createStyle(FeatureSource)] Create Point style");
          
        stroke          = this.styleBuilder.createStroke(this.polygonStrokeColor, this.polygonStrokeWidth, this.polygonStrokeOpacity);
        fill            = this.styleBuilder.createFill(this.polygonFillColor, this.polygonFillOpacity);
          symbolizer      = sf.createPointSymbolizer();

          style = this.styleBuilder.createStyle();
          
          //style.addFeatureTypeStyle(styleBuilder.createFeatureTypeStyle(symbolizer));
          style.featureTypeStyles().add(this.styleBuilder.createFeatureTypeStyle(schema.getTypeName(), symbolizer));
          
          
        // Géométrie composée de ligne  
        } else if (geometryName.getName().toUpperCase().endsWith("LINE")){
          
          System.out.println("[GaiaStyleCreator] [createStyle(FeatureSource)] Create Line style");
          
          stroke          = this.styleBuilder.createStroke(this.lineStrokeColor, this.lineStrokeWidth, this.lineStrokeOpacity);
          symbolizer      = sf.createLineSymbolizer();

          ((LineSymbolizer)symbolizer).setStroke(stroke);
          style = this.styleBuilder.createStyle();
          
          //style.addFeatureTypeStyle(styleBuilder.createFeatureTypeStyle(symbolizer));
          style.featureTypeStyles().add(this.styleBuilder.createFeatureTypeStyle(schema.getTypeName(), symbolizer));
          
          
        // Géométrie composée d'ensembles de lignes
        } else if (geometryName.getName().toUpperCase().endsWith("LINESTRING")){
          
          System.out.println("[GaiaStyleCreator] [createStyle(FeatureSource)] Create Line style");
          
          stroke          = this.styleBuilder.createStroke(this.lineStringStrokeColor, this.lineStringStrokeWidth, this.lineStringStrokeOpacity);
          symbolizer      = sf.createLineSymbolizer();

          ((LineSymbolizer)symbolizer).setStroke(stroke);
          style = this.styleBuilder.createStyle();
          
          //style.addFeatureTypeStyle(styleBuilder.createFeatureTypeStyle(symbolizer));
          style.featureTypeStyles().add(this.styleBuilder.createFeatureTypeStyle(schema.getTypeName(), symbolizer));
          
          
        // Géométrie composée de polygones
        } else if (geometryName.getName().toUpperCase().endsWith("POLYGON")){
          
          System.out.println("[GaiaStyleCreator] [createStyle(FeatureSource)] Create Polygon style");
          
          stroke          = this.styleBuilder.createStroke(this.polygonStrokeColor, this.polygonStrokeWidth, this.polygonStrokeOpacity);
          fill            = this.styleBuilder.createFill(this.polygonFillColor, this.polygonFillOpacity);
          symbolizer      = sf.createPolygonSymbolizer();

          ((PolygonSymbolizer)symbolizer).setStroke(stroke);
          ((PolygonSymbolizer)symbolizer).setFill(fill);
          
          /*
          style = styleBuilder.createStyle();
          
          
          //style.addFeatureTypeStyle(styleBuilder.createFeatureTypeStyle(symbolizer));
          style.addFeatureTypeStyle(styleBuilder.createFeatureTypeStyle(schema.getTypeName(), symbolizer));
          */
      
          type = sf.createFeatureTypeStyle();
          type.featureTypeNames().clear();
          type.featureTypeNames().add( new NameImpl( schema.getTypeName() ));
          
          type.setName(schema.getTypeName()+"StyleType");
   
          rule = sf.createRule();
          rule.setFilter(Filter.INCLUDE);
          rule.symbolizers().add(symbolizer );
          rule.setName(schema.getTypeName()+"StyleRule");    
          type.rules().add(rule);        
          
              
          style = sf.createStyle();
          style.featureTypeStyles().add(type);
          style.setName(schema.getTypeName()+"Style");  
  
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
   * @return the created {@link org.geotools.styling.Style style}
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
    RasterSymbolizer raster = this.styleBuilder.createRasterSymbolizer();
            
    Style style = this.styleBuilder.createStyle(raster);
    
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
    this.styleBuilder = builder;
  }
  
  /**
   * Get the style builder used for the geotools style creation
   * @return the style builder used
   */
  public StyleBuilder getStyleBuilder(){
    return this.styleBuilder;
  }
  
  /**
   * Set the color of the stroke used for representing a line geometry
   * @param color the color of the stroke
   */
  public void setLineStrokeColor(Color color){
    this.lineStrokeColor = color;
  }
  
  /**
   * Get the color of the stroke used for representing a line geometry
   * @return the color of the stroke
   */
  public Color getLineStrokeColor(){
    return this.lineStrokeColor;
  }
  
  /**
   * Set the opacity of the stroke used for representing a line geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @param opacity the opacity value
   */
  public void setLineStrokeOpacity(double opacity){
    if ((opacity > 0.0d) && (opacity <= 1.0d)){
      this.lineStrokeOpacity = opacity;
    }
  }
  
  /**
   * Get the opacity of the stroke used for representing a line geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @return the opacity value
   */
  public double getLineStrokeOpacity(){
    return this.lineStrokeOpacity;
  }
  
  /**
   * Set the width of the stroke used for representing a line geometry. The width must 
   * be a positive <code>double</code>.
   * @param width the width of the stroke
   */
  public void setLineStrokeWidth(double width){
    if (width > 0.0d){
      this.lineStrokeWidth = width;
    }
  }
  
  /**
   * Get the width of the stroke used for representing a line geometry. The width is 
   * a positive <code>double</code>.
   * @return the width of the stroke
   */
  public double getLineStrokeWidth(){
    return this.lineStrokeWidth;
  }
  
  /**
   * Set the color of the stroke used for representing a polygon geometry
   * @param color the color of the stroke
   */
  public void setPolygonStrokeColor(Color color){
    this.polygonStrokeColor = color;
  }
  
  /**
   * Get the color of the stroke used for representing a polygon geometry
   * @return the color of the stroke
   */
  public Color getPolygonStrokeColor(){
    return this.polygonStrokeColor;
  }
  
  /**
   * Set the opacity of the stroke used for representing a polygon geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @param opacity the opacity value
   */
  public void setPolygonStrokeOpacity(double opacity){
    if ((opacity > 0.0d) && (opacity <= 1.0d)){
     this.polygonStrokeOpacity = opacity;
    }
  }
  
  /**
   * Get the opacity of the stroke used for representing a polygon geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @return the opacity value
   */
  public double getPolygonStrokeOpacity(){
    return this.polygonStrokeOpacity;
  }
  
  /**
   * Set the width of the stroke used for representing a polygon geometry. The width must 
   * be a positive <code>double</code>.
   * @param width the width of the stroke
   */
  public void setPolygonStrokeWidth(double width){
    if (width > 0.0d){
      this.lineStrokeWidth = width;
    }
  }
  
  /**
   * Get the width of the stroke used for representing a polygon geometry. The width is 
   * a positive <code>double</code>.
   * @return the width of the stroke
   */
  public double getPolygonStrokeWidth(){
    return this.polygonStrokeWidth;
  }
  
  /**
   * Set the color used to fill the polygon representing a polygon geometry.
   * @param color the color of the fill.
   */
  public void setPolygonFillColor(Color color){
    this.polygonFillColor = color;
  }
  
  /**
   * Get the color used to fill the polygon representing a polygon geometry.
   * @return the color of the fill.
   */
  public Color getPolygonFillColor(){
    return this.polygonFillColor;
  }
  
  /**
   * Set the opacity of the fill used for representing a polygon geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @param opacity the opacity value
   */
  public void setPolygonFillOpacity(double opacity){
    if ((opacity > 0.0d) && (opacity <= 1.0d)){
      this.polygonFillOpacity = opacity;
    }
  }
  
  /**
   * Get the opacity of the fill used for representing a polygon geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @return the opacity value
   */
  public double getPolygonFillOpacity(){
    return this.polygonFillOpacity;
  }
  
  
  /**
   * Set the color of the stroke used for representing a line string geometry
   * @param color the color of the stroke
   */
  public void setLineStringStrokeColor(Color color){
    this.lineStringStrokeColor = color;
  }
  
  /**
   * Get the color of the stroke used for representing a line string geometry
   * @return the color of the stroke
   */
  public Color getLineStringStrokeColor(){
    return this.lineStringStrokeColor;
  }
  
  /**
   * Set the opacity of the stroke used for representing a line string geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @param opacity the opacity value
   */
  public void setLineStringStrokeOpacity(double opacity){
    if ((opacity > 0.0d) && (opacity <= 1.0d)){
      this.lineStringStrokeOpacity = opacity;
    }
  }
  
  /**
   * Get the opacity of the stroke used for representing a line string geometry. Opacity is 
   * a <code>double</code> such that 0.0d if completely transparent and 1.0 is totally 
   * opaque.  
   * @return the opacity value
   */
  public double getLineStringStrokeOpacity(){
    return this.lineStringStrokeOpacity;
  }
  
  /**
   * Set the width of the stroke used for representing a line string geometry. The width must 
   * be a positive <code>double</code>.
   * @param width the width of the stroke
   */
  public void setLineStringStrokeWidth(double width){
    if (width > 0.0d){
      this.lineStringStrokeWidth = width;
    }
  }
  
  /**
   * Get the width of the stroke used for representing a line string geometry. The width is 
   * a positive <code>double</code>.
   * @return the width of the stroke
   */
  public double getLineStringStrokeWidth(){
    return this.lineStringStrokeWidth;
  }

//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                                 AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

}
