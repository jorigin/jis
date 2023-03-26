package org.arpenteur.gis.geotools.feature;


import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class CFFeatureType extends ItemMesurableFeatureType {
  
  public static String TYPE_NAME            = "cf";
  
  private String typeName                   = TYPE_NAME;
  
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
  /**
   * Construct a new feature representing an ashlar bloc. The coordinate system
   * correspond to the geotools coordinate system constructed from ARPENTEUR.
   * @param crs the coordinate reference system to use.
   */
  public CFFeatureType(CoordinateReferenceSystem crs){
    super(crs, "CF", null); 
    
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  

}
