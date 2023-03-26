package org.arpenteur.gis.geotools.feature;


import java.util.List;

import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;


public class ItemAmphoreFeatureType extends ItemMesurableFeatureType {

  public static final String typeName          = "amphore";
  public static final String namespace         = "http://www.arpenteur.net/mesurable";
  
  public static final String STANDARD_ID       = "standardId";
  public static final String EXCAVATION_ID     = "excavId"; 
  public static final String INVENTORY         = "inventory";
  public static final String LOCALISATION      = "localisat";
  public static final String FRAGMENT_COUNT    = "nbFrgmt";
  public static final String DESCRIPTION       = "descr";
  
  public static final String REMAIN_HEIGHT     = "remHeight";
  public static final String INTERNAL_DIAMETER = "intDiam";
  public static final String EXTERNAL_DIAMETER = "extDiam";
  public static final String FOOT_DIAMETER     = "footDiam";
  public static final String BELLY_DIAMETER    = "bellyDiam";
  public static final String HEIGHT_LIPS       = "heightLips";
  public static final String PC_LIPS           = "pcLips";
  
  public static final String GEOM_EXT_BODY     = "geom_ext_body";
  public static final String GEOM_INT_BODY     = "geom_int_body";
  public static final String GEOM_HAN_1        = "geom_han_1";
  public static final String GEOM_HAN_2        = "geom_han_2";
  public static final String GEOM_DRAWINGS     = "geom_drawings";
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
  /**
   * Construct a new feature representing an amphora. The coordinate system
   * correspond to the geotools coordinate system constructed from ARPENTEUR.
   * This feature type represent a complete amphora as a fragment of amphora.
   * @param crs the coordinate reference system to use.
   */
  public ItemAmphoreFeatureType(CoordinateReferenceSystem crs){
    super(crs, typeName, namespace);
    
    List<AttributeDescriptor> attributeTypes = getAttributeDescriptors();
    AttributeTypeBuilder builder             = new AttributeTypeBuilder();
    AttributeDescriptor descriptor           = null;
    
    
    if (crs != null){
      this.crs = crs;
    } else {
      this.crs = DefaultEngineeringCRS.CARTESIAN_2D;
    }
    
    // Remplacement de la geometrie generale par la geometrie specifique
    builder.setName(GEOMETRIC_ATT_NAME);
    builder.setBinding(MultiPolygon.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    builder.setCRS(crs);
    GeometryDescriptor defaultGeometryType = (GeometryDescriptor)builder.buildDescriptor(GEOMETRIC_ATT_NAME);
    attributeTypes.set(attributeTypes.indexOf(getGeometryDescriptor()), defaultGeometryType);
    
    builder.setName(STANDARD_ID);
    builder.setBinding(String.class);
    builder.setMinOccurs(0);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(STANDARD_ID);
    attributeTypes.add(descriptor);
    
    builder.setName(INVENTORY);
    builder.setBinding(String.class);
    builder.setMinOccurs(0);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(INVENTORY);
    attributeTypes.add(descriptor);
    
    builder.setName(LOCALISATION);
    builder.setBinding(String.class);
    builder.setMinOccurs(0);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(LOCALISATION);
    attributeTypes.add(descriptor);
    
    builder.setName(FRAGMENT_COUNT);
    builder.setBinding(Integer.class);
    builder.setMinOccurs(0);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(FRAGMENT_COUNT);
    attributeTypes.add(descriptor);
    
    builder.setName(DESCRIPTION);
    builder.setBinding(String.class);
    builder.setMinOccurs(0);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(DESCRIPTION);
    attributeTypes.add(descriptor);
    
    builder.setName(REMAIN_HEIGHT);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    builder.setDefaultValue(new Double(0.0d));
    descriptor = builder.buildDescriptor(REMAIN_HEIGHT);
    attributeTypes.add(descriptor);
    
    builder.setName(INTERNAL_DIAMETER);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    builder.setDefaultValue(new Double(0.0d));
    descriptor = builder.buildDescriptor(INTERNAL_DIAMETER);
    attributeTypes.add(descriptor);
    
    builder.setName(EXTERNAL_DIAMETER);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    builder.setDefaultValue(new Double(0.0d));
    descriptor = builder.buildDescriptor(EXTERNAL_DIAMETER);
    attributeTypes.add(descriptor);
    
    builder.setName(FOOT_DIAMETER);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    builder.setDefaultValue(new Double(0.0d));
    descriptor = builder.buildDescriptor(FOOT_DIAMETER);
    attributeTypes.add(descriptor);
    
    builder.setName(BELLY_DIAMETER);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    builder.setDefaultValue(new Double(0.0d));
    descriptor = builder.buildDescriptor(BELLY_DIAMETER);
    attributeTypes.add(descriptor);
    
    builder.setName(HEIGHT_LIPS);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    builder.setDefaultValue(new Double(0.0d));
    descriptor = builder.buildDescriptor(HEIGHT_LIPS);
    attributeTypes.add(descriptor);
    
    builder.setName(PC_LIPS);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    builder.setDefaultValue(new Double(0.0d));
    descriptor = builder.buildDescriptor(PC_LIPS);
    attributeTypes.add(descriptor);
    
    builder.setName(GEOM_EXT_BODY);
    builder.setBinding(LineString.class);
    builder.setMinOccurs(0);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    builder.setCRS(crs);
    descriptor = (GeometryDescriptor)builder.buildDescriptor(GEOM_EXT_BODY);
    attributeTypes.add(descriptor);
    
    builder.setName(GEOM_INT_BODY);
    builder.setBinding(LineString.class);
    builder.setMinOccurs(0);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    builder.setCRS(crs);
    descriptor = (GeometryDescriptor)builder.buildDescriptor(GEOM_INT_BODY);
    attributeTypes.add(descriptor);
   
    builder.setName(GEOM_HAN_1);
    builder.setBinding(LineString.class);
    builder.setMinOccurs(0);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    builder.setCRS(crs);
    descriptor = (GeometryDescriptor)builder.buildDescriptor(GEOM_HAN_1);
    attributeTypes.add(descriptor);
    
    builder.setName(GEOM_HAN_2);
    builder.setBinding(LineString.class);
    builder.setMinOccurs(0);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    builder.setCRS(crs);
    descriptor = (GeometryDescriptor)builder.buildDescriptor(GEOM_HAN_2);
    attributeTypes.add(descriptor);
    
    builder.setName(GEOM_DRAWINGS);
    builder.setBinding(MultiLineString.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    builder.setCRS(crs);
    descriptor = (GeometryDescriptor)builder.buildDescriptor(GEOM_DRAWINGS);
    attributeTypes.add(descriptor);
    
    setDescriptors(attributeTypes);
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

}
