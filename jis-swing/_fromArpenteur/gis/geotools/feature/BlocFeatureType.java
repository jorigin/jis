package org.arpenteur.gis.geotools.feature;


import java.util.List;

import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class BlocFeatureType extends ItemMesurableFeatureType {

  public static final String typeName   = "bloc";
  public static final String namespace  = "http://www.arpenteur.net/mesurable";
  
  public static final String USM_ID         = "usmId";
  public static final String UMS_NAME       = "usmName"; 
  public static final String LITOTIPO       = "litotypo";
  public static final String LAVORAZIONE    = "lavorazio";
  public static final String FINITURA       = "finitura";
  public static final String DESCRIPTION    = "descr";
  
  
   
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
  /**
   * Construct a new feature representing an ashlar bloc. The coordinate system
   * correspond to the geotools coordinate system constructed from ARPENTEUR.
   * @param crs the coordinate reference system to use.
   */
  public BlocFeatureType(CoordinateReferenceSystem crs){
    
    super(crs, typeName, namespace);
    
    List<AttributeDescriptor> attributeTypes = getAttributeDescriptors();
    AttributeTypeBuilder builder                  = new AttributeTypeBuilder();
    AttributeDescriptor descriptor                = null;
    
    
    if (crs != null){
      this.crs = crs;
    } else {
      this.crs = DefaultEngineeringCRS.CARTESIAN_2D;
    }
    
    builder.setName(USM_ID);
    builder.setBinding(Integer.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(USM_ID);
    attributeTypes.add(descriptor);
    
    builder.setName(UMS_NAME);
    builder.setBinding(String.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(UMS_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(LITOTIPO);
    builder.setBinding(String.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(LITOTIPO);
    attributeTypes.add(descriptor);
    
    builder.setName(LAVORAZIONE);
    builder.setBinding(String.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(LAVORAZIONE);
    attributeTypes.add(descriptor);
    
    builder.setName(FINITURA);
    builder.setBinding(String.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(FINITURA);
    attributeTypes.add(descriptor);
    
    builder.setName(DESCRIPTION);
    builder.setBinding(String.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(DESCRIPTION);
    attributeTypes.add(descriptor);   
    
    setDescriptors(attributeTypes);
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
}