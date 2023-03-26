package org.jorigin.jis.feature;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Geometry;
import org.geotools.feature.GeometryAttributeImpl;
import org.geotools.feature.type.Types;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.Converters;

/**
 * An implementation of a {@link org.opengis.feature.simple.SimpleFeature SimpleFeature} 
 * with an underlying {@link java.util.List List} of attributes.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class ListedSimpleFeature implements SimpleFeature{

  protected FeatureId id;
  protected SimpleFeatureType featureType;
  /**
   * The actual values held by this feature
   */
  protected List<Object> values;
  /**
   * The attribute name -> position index
   */
  //protected Map<String,Integer> index;
  /**
   * The set of user data attached to the feature (lazily created)
   */
  protected Map<Object, Object> userData;
  /**
   * The set of user data attached to each attribute (lazily created)
   */
  protected Map<Object, Object>[] attributeUserData;

  /**
   * Wheter this feature is self validating or not
   */
  protected  boolean validating;

  /**
   * Constructs a new feature from the given {@link org.opengis.feature.simple.SimpleFeatureType SimpleFeatureType}
   * and with the given {@link org.opengis.filter.identity.FeatureId}
   * @param featureType the type of the feature.
   * @param id the identifier of the feature.
   */
  public ListedSimpleFeature( SimpleFeatureType featureType, FeatureId id) {
    this(null, featureType, id, false);
  }

  /**
   * Constructs a new feature from the given {@link org.opengis.feature.simple.SimpleFeatureType SimpleFeatureType}
   * and with the given {@link org.opengis.filter.identity.FeatureId}. The <code>values</code> composing the feature 
   * are given in parameter. The <code>values</code> have to match the declared properties of the feature type.
   * @param values the values composing the feature.
   * @param featureType the type of the feature.
   * @param id the identifier of the feature.
   */
  public ListedSimpleFeature( List<Object> values, SimpleFeatureType featureType, FeatureId id) {
    this(values.toArray(), featureType, id, false);
  }

  /**
   * Constructs a new feature from the given {@link org.opengis.feature.simple.SimpleFeatureType SimpleFeatureType}
   * and with the given {@link org.opengis.filter.identity.FeatureId}. The <code>values</code> composing the feature 
   * are given in parameter. The <code>values</code> have to match the declared properties of the feature type. 
   * The parameter <code>validating</code> specifies if the feature has to be validated. The validation check if the 
   * given values are matching the attributes declared within the feature type.
   * @param values the values composing the feature.
   * @param featureType the type of the feature.
   * @param id the identifier of the feature.
   * @param validating is the feature has to be validated
   */
  public ListedSimpleFeature(Object[] values, SimpleFeatureType featureType, FeatureId id, boolean validating) {
    this.id = id;
    this.featureType = featureType;

    this.values = new ArrayList<Object>(featureType.getAttributeCount());
    setAttributes(values);

    this.validating = validating;

    // if we're self validating, do validation right now
    if(validating)
      validate();
  }

  @Override
  public FeatureId getIdentifier() {
    return this.id;
  }

  @Override
  public String getID() {
    return this.id.getID();
  }


  @Override
  public Object getAttribute(int index) throws IndexOutOfBoundsException {
    return this.values.get(index);
  }

  @Override
  public Object getAttribute(String name) {
    Integer idx = this.featureType.indexOf(name);
    if(idx != -1){
      return getAttribute(idx);
    } else {
      return null;
    }
  }

  @Override
  public Object getAttribute(Name name) {
    return getAttribute( name.getLocalPart() );
  }

  @Override
  public int getAttributeCount() {
    return this.values.size();
  }

  @Override
  public List<Object> getAttributes() {
    return new ArrayList<Object>(this.values);
  }

  @Override
  public Object getDefaultGeometry() {
    // should be specified in the index as the default key (null)
    Object defaultGeometry = 
      this.featureType.indexOf((String)null) != -1 ? getAttribute( this.featureType.indexOf((String)null) ) : null;

      // not found? Ok, let's do a lookup then...
      if ( defaultGeometry == null ) {
        for ( Object o : this.values ) {
          if ( o instanceof Geometry ) {
            defaultGeometry = o;
            break;
          }
        }
      }

      return defaultGeometry;
  }

  @Override
  public SimpleFeatureType getFeatureType() {
    return this.featureType;
  }

  @Override
  public SimpleFeatureType getType() {
    return this.featureType;
  }

  @Override
  public void setAttribute(int index, Object value)
  throws IndexOutOfBoundsException {
    // first do conversion
    Object converted = Converters.convert(value, getFeatureType().getDescriptor(index).getType().getBinding());
    // if necessary, validation too
    if(this.validating)
      Types.validate(this.featureType.getDescriptor(index), converted);
    // finally set the value into the feature
    this.values.set(index, converted);
  }

  @Override
  public void setAttribute(String name, Object value) {
    final Integer idx = this.featureType.indexOf(name);
    if(idx == -1)
      throw new IllegalAttributeException(null, null, "Unknown attribute " + name);
    setAttribute( idx.intValue(), value );
  }

  @Override
  public void setAttribute(Name name, Object value) {
    setAttribute( name.getLocalPart(), value );
  }

  @Override
  public void setAttributes(List<Object> values) {
    for (int i = 0; i < this.values.size(); i++) {
      this.values.set(i, values.get(i));
    }
  }

  @Override
  public void setAttributes(Object[] values) {
    if ((values != null) && (values.length == getType().getAttributeCount())){
      setAttributes( Arrays.asList( values ) );
    } else {
      this.values = new ArrayList<Object>(this.featureType.getAttributeCount());
      for(int i = 0; i < this.featureType.getAttributeCount(); i++){
        this.values.add(null);
      }
    }
  }

  @Override
  public void setDefaultGeometry(Object geometry) {
    Integer geometryIndex = this.featureType.indexOf((String)null);
    if ( geometryIndex != null ) {
      setAttribute( geometryIndex, geometry );
    }
  }

  @Override
  public BoundingBox getBounds() {
    ReferencedEnvelope bounds = new ReferencedEnvelope( this.featureType.getCoordinateReferenceSystem() );
    for ( Object o : this.values ) {
      if ( o instanceof Geometry ) {
    	  
    	Geometry g = (Geometry) o;
    	  
    	ReferencedEnvelope env = (ReferencedEnvelope) g.getEnvelope();
    	  
        if ( bounds.isNull() ) {
          bounds.init((BoundingBox)g.getEnvelope());
        }
        else {
          bounds.expandToInclude(env);
        }
      }
    }

    return bounds;
  }

  @Override
  public GeometryAttribute getDefaultGeometryProperty() {
    return new GeometryAttributeImpl(getDefaultGeometry(), getFeatureType().getGeometryDescriptor(), null);
  }

  @Override
  public void setDefaultGeometryProperty(GeometryAttribute geometryAttribute) {
    if(geometryAttribute != null)
      setDefaultGeometry(geometryAttribute.getValue());
    else
      setDefaultGeometry(null);
  }

  @Override
  public Collection<Property> getProperties() {
    return new AttributeList();
  }

  @Override
  public Collection<Property> getProperties(Name name) {
    return getProperties( name.getLocalPart() );
  }

  @Override
  public Collection<Property> getProperties(String name) {
    final Integer idx = this.featureType.indexOf(name);
    if(idx != -1) {
      // cast temporarily to a plain collection to avoid type problems with generics
      Collection<Property> c = new HashSet<Property>();
      c.add(new Attribute( idx ));
      return c;
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public Property getProperty(Name name) {
    return getProperty( name.getLocalPart() );
  }

  @Override
  public Property getProperty(String name) {
    final Integer idx = this.featureType.indexOf(name);
    if(idx == -1)
      return null;
    else{
      int index = idx.intValue();
      AttributeDescriptor descriptor = this.featureType.getDescriptor(index);
      if(descriptor instanceof GeometryDescriptor){
        return new GeometryAttributeImpl(this.values.get(index), (GeometryDescriptor) descriptor, null); 
      }else{
        return new Attribute( index );
      }
    }
  }

  @Override
  public Collection<? extends Property> getValue() {
    return getProperties();
  }

  @Override
  public void setValue(Collection<Property> values) {
    int i = 0;
    for ( Property p : values ) {
      this.values.set(i, p.getValue());
      i++;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setValue(Object newValue) {
    if (newValue instanceof Collection){
      setValue( (Collection<Property>) newValue );
    }
  }

  @Override
  public AttributeDescriptor getDescriptor() {
    return null;
  }

  @Override
  public Name getName() {
    return null;
  }

  @Override
  public boolean isNillable() {
    return true;
  }

  @Override
  public Map<Object, Object> getUserData() {
    if(this.userData == null)
      this.userData = new HashMap<Object, Object>();
    return this.userData;
  }


  //RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  //RR REDEFINITION                                                   RR
  //RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR

  // -- Object ---------------------------------------------------------  

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof ListedSimpleFeature)) {
      return false;
    }

    ListedSimpleFeature feat = (ListedSimpleFeature) obj;

    // this check shouldn't exist, by contract, 
    //all features should have an ID.
    if (this.id == null) {
      if (feat.getIdentifier() != null) {
        return false;
      }
    }

    if (!this.id.equals(feat.getIdentifier())) {
      return false;
    }

    if (!feat.getFeatureType().equals(this.featureType)) {
      return false;
    }

    for (int i = 0, ii = this.values.size(); i < ii; i++) {
      Object otherAtt = feat.getAttribute(i);

      if (this.values.get(i) == null) {
        if (otherAtt != null) {
          return false;
        }
      } else {
        if (!this.values.get(i).equals(otherAtt)) {
          if (this.values.get(i) instanceof Geometry
              && otherAtt instanceof Geometry) {
            // we need to special case Geometry
            // as JTS is broken Geometry.equals( Object ) 
            // and Geometry.equals( Geometry ) are different 
            // (We should fold this knowledge into AttributeType...)
            if (!((Geometry) this.values.get(i)).equals(
                (Geometry) otherAtt)) {
              return false;
            }
          } else {
            return false;
          }
        }
      }
    }

    return true;
  }

  @Override
  public String toString() {
    String retString = getClass().getSimpleName()+" [ id=" + getID() + " , ";
    SimpleFeatureType featType = getFeatureType();

    for (int i = 0, n = featType.getAttributeCount(); i < n; i++) {
      retString += (featType.getDescriptor(i).getLocalName() + "=");
      retString += getAttribute(i);

      if ((i + 1) < n) {
        retString += " , ";
      }
    }

    return retString += " ]";
  }

  // -------------------------------------------------------------------
  //RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  //RR REDEFINITION                                                   RR
  //RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR

  @Override
  public void validate() {
    for (int i = 0; i < this.values.size(); i++) {
      AttributeDescriptor descriptor = getType().getDescriptor(i);
      Types.validate(descriptor, this.values.get(i));
    }
  }

  /**
   * Live collection backed directly on the value array
   */
  class AttributeList extends AbstractList<Property> {

    public Attribute get(int index) {
      return new Attribute( index );
    }

    public Attribute set(int index, Property element) {
      ListedSimpleFeature.this.values.set(index, element.getValue());
      return null;
    }

    public int size() {
      return ListedSimpleFeature.this.values.size();
    }
  }

  /**
   * Attribute that delegates directly to the value array
   */
  class Attribute implements org.opengis.feature.Attribute {

    int index;

    Attribute( int index ) {
      this.index = index;
    }

    public Identifier getIdentifier() {
      return null;
    }

    public AttributeDescriptor getDescriptor() {
      return ListedSimpleFeature.this.featureType.getDescriptor(this.index);
    }

    public AttributeType getType() {
      return ListedSimpleFeature.this.featureType.getType(this.index);
    }

    public Name getName() {
      return getDescriptor().getName();
    }

    @SuppressWarnings("unchecked")
    public Map<Object, Object> getUserData() {
      // lazily create the user data holder
      if(ListedSimpleFeature.this.attributeUserData == null)
        ListedSimpleFeature.this.attributeUserData = new HashMap[ListedSimpleFeature.this.values.size()];
      // lazily create the attribute user data
      if(ListedSimpleFeature.this.attributeUserData[this.index] == null)
        ListedSimpleFeature.this.attributeUserData[this.index] = new HashMap<Object, Object>();
      return ListedSimpleFeature.this.attributeUserData[this.index];
    }

    public Object getValue() {
      return ListedSimpleFeature.this.values.get(this.index);
    }

    public boolean isNillable() {
      return getDescriptor().isNillable();
    }

    public void setValue(Object newValue) {
      ListedSimpleFeature.this.values.set(this.index, newValue);

    }

    public void validate() {
      Types.validate(getDescriptor(), ListedSimpleFeature.this.values.get(this.index));
    }

  }

}