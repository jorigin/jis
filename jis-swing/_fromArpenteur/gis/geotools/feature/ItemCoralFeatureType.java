package org.arpenteur.gis.geotools.feature;

import java.util.List;

import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class ItemCoralFeatureType extends ItemMesurableFeatureType{

  public static final String typeName   = "coral";
  public static final String namespace  = "http://www.arpenteur.net/mesurable";
  
  public static final String DIAMBASE   = "diambase";
  public static final String NB_BRANCHE = "nbbranche"; 
  public static final String PC_NECROSE = "pcnecrose";
 
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
	  
  /**
   * Construct a new feature representing an amphora. The coordinate system
   * correspond to the geotools coordinate system constructed from ARPENTEUR.
   * This feature type represent a complete amphora as a fragment of amphora.
   * @param crs the coordinate reference system to use.
   */
  public ItemCoralFeatureType(CoordinateReferenceSystem crs){
    
    super(crs, typeName, namespace);

    List<AttributeDescriptor> attributeTypes = getAttributeDescriptors();
    AttributeTypeBuilder builder                  = new AttributeTypeBuilder();
    AttributeDescriptor descriptor                = null;
    
    
    if (crs != null){
      this.crs = crs;
    } else {
      this.crs = DefaultEngineeringCRS.CARTESIAN_2D;
    }
    
    builder.setName(DIAMBASE);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    builder.setDefaultValue(new Double(0.0d));
    descriptor = builder.buildDescriptor(DIAMBASE);
    attributeTypes.add(descriptor);
    
    builder.setName(NB_BRANCHE);
    builder.setBinding(Integer.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    builder.setDefaultValue(new Integer(0));
    descriptor = builder.buildDescriptor(NB_BRANCHE);
    attributeTypes.add(descriptor);
    
    builder.setName(PC_NECROSE);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    builder.setDefaultValue(new Double(0.0d));
    descriptor = builder.buildDescriptor(PC_NECROSE);
    attributeTypes.add(descriptor);
    
    setDescriptors(attributeTypes);
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

}
