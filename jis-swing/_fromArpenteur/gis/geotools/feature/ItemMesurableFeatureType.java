package org.arpenteur.gis.geotools.feature;

import java.util.List;

import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.NameImpl;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

public class ItemMesurableFeatureType extends ListedSimpleFeatureType {
  
  /**
   * The type name for the feature.
   */
  public static final String typeName   = "mesurable";
  
  public static final String namespace  = "http://www.arpenteur.net/mesurable";
  
  public static final String GEOMETRIC_ATT_NAME        = "geometry";
  public static final String IDN_ATT_NAME              = "idn";
  public static final String NAME_ATT_NAME             = "name";
  public static final String TIMEKEY_ATT_NAME          = "timekey";
  public static final String SUVEYID_ATT_NAME          = "surv_id";
  public static final String JAVACLASS_ATT_NAME        = "jclass";

  public static final String LOC_X_ATT_NAME            = "loc_x";
  public static final String LOC_Y_ATT_NAME            = "loc_y";
  public static final String LOC_Z_ATT_NAME            = "loc_z";
  public static final String LOC_O_ATT_NAME            = "loc_o"; 
  public static final String LOC_P_ATT_NAME            = "loc_p";
  public static final String LOC_K_ATT_NAME            = "loc_k";

  public static final String METROLOGY_HEIGHT_ATT_NAME = "met_h";
  public static final String METROLOGY_WIDTH_ATT_NAME  = "met_w";
  public static final String METROLOGY_LENGTH_ATT_NAME = "met_l";
  public static final String METROLOGY_VOLUME_ATT_NAME = "met_vol";
  public static final String METROLOGY_MASS_ATT_NAME   = "met_mass";
  
  /**
   * The coordinates reference system associated to this feature type.
   * This coordinates reference system is attached to the default geometry
   * @see #defaultGeometry
   */
  CoordinateReferenceSystem crs         = null;
  
  /**
   * The first free idn used to create a new
   * unique id for a feature.
   */
  protected static int firstFreeIdn       = 0;
  
  
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//RR REDEFINITION                                                   RR
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
// -- Object ---------------------------------------------------------
  public boolean equals(SimpleFeatureType other) {
    if(other == this)
      return true;

    if (other == null) {
      return false;
    }

    if ((typeName == null) && (other.getTypeName() != null)) {
      return false;
    } else if (!typeName.equals(other.getTypeName())) {
      return false;
    }

    if (this.getAttributeCount() != other.getAttributeCount()) {
      return false;
    }

    for (int i = 0, ii = this.getAttributeCount(); i < ii; i++) {
      if (!this.getDescriptor(i).equals(other.getDescriptor(i))) {
        return false;
      }
    }

    return true;
  }

  public String toString() {
    String info = "name=" + typeName;
    info += (" , abstract=" + isAbstract());

    String types1 = "types=(";

    for (int i = 0, ii = this.getAttributeCount(); i < ii; i++) {
      types1 += this.getDescriptor(i).toString();

      if (i < ii) {
        types1 += ",";
      }
    }

    types1 += ")";
    info += (" , " + types1);

    return getTypeName()+" [" + info + "]";
  }

  public boolean equals(Object other) {
    if (other instanceof SimpleFeatureType) 
      return equals((SimpleFeatureType) other);
    return false;
  }
//--------------------------------------------------------------------   
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//RR FIN REDEFINITION                                               RR
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR

  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
  /**
   * Create a nes default item mesurable feature type
   */
  public ItemMesurableFeatureType(CoordinateReferenceSystem crs){
    this(crs, typeName, namespace);
  }
  
  /**
   * Construct a new feature representing an item mesurable. The coordinate system
   * correspond to the geotools coordinate system constructed from ARPENTEUR.
   * @param crs the coordinate reference system to use.
   */
  public ItemMesurableFeatureType(CoordinateReferenceSystem crs, String typeName, String namespace){
    
    super(new NameImpl(namespace, typeName));
    
    List<AttributeDescriptor> attributeTypes = getAttributeDescriptors();
    AttributeTypeBuilder builder                  = new AttributeTypeBuilder();
    AttributeDescriptor descriptor                = null;
    
    
    if (crs != null){
      this.crs = crs;
    } else {
      this.crs = DefaultEngineeringCRS.CARTESIAN_2D;
    }
    
    builder.setName(GEOMETRIC_ATT_NAME);
    builder.setBinding(Geometry.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    builder.setCRS(crs);
    descriptor = builder.buildDescriptor(GEOMETRIC_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(IDN_ATT_NAME);
    builder.setBinding(Integer.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(IDN_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(NAME_ATT_NAME);
    builder.setBinding(String.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    builder.setDefaultValue(null);
    descriptor = builder.buildDescriptor(NAME_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(TIMEKEY_ATT_NAME);
    builder.setBinding(Long.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(TIMEKEY_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(SUVEYID_ATT_NAME);
    builder.setBinding(String.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(SUVEYID_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(JAVACLASS_ATT_NAME);
    builder.setBinding(String.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(JAVACLASS_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(LOC_X_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(LOC_X_ATT_NAME);
    attributeTypes.add(descriptor);
  
    builder.setName(LOC_Y_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(LOC_Y_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(LOC_Z_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(LOC_Z_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(LOC_O_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(LOC_O_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(LOC_P_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(LOC_P_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(LOC_K_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(LOC_K_ATT_NAME);
    attributeTypes.add(descriptor);

    builder.setName(METROLOGY_HEIGHT_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(METROLOGY_HEIGHT_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(METROLOGY_WIDTH_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(METROLOGY_WIDTH_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(METROLOGY_LENGTH_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(METROLOGY_LENGTH_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(METROLOGY_VOLUME_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(METROLOGY_VOLUME_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(METROLOGY_MASS_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(METROLOGY_MASS_ATT_NAME);
    attributeTypes.add(descriptor);
    
    setDescriptors(attributeTypes);
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

  
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEURS                                                     AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  public static String getFreeID(){
    return typeName+firstFreeIdn;
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                                 AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
 
}


