package org.arpenteur.gis.geotools.feature;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.feature.GeometryAttributeImpl;
import org.geotools.feature.type.Types;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.Converters;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.BoundingBox;

import com.vividsolutions.jts.geom.Geometry;

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
    
    public ListedSimpleFeature( SimpleFeatureType featureType, FeatureId id) {
      this(null, featureType, id, false);
    }
    
    /**
     * Builds a new feature based on the provided values and feature type
     * @param values
     * @param featureType
     * @param id
     */
    public ListedSimpleFeature( List<Object> values, SimpleFeatureType featureType, FeatureId id) {
        this(values.toArray(), featureType, id, false);
    }
    
    /**
     * Fast construction of a new feature. The object takes owneship of the provided value array,
     * do not modify after calling the constructor
     * @param values
     * @param featureType
     * @param id
     * @param validating
     */
    public ListedSimpleFeature(Object[] values, SimpleFeatureType featureType, FeatureId id, boolean validating) {
        this.id = id;
        this.featureType = featureType;
        
        this.values = new ArrayList<Object>(featureType.getAttributeCount());
        setAttributes(values);
        //System.out.println("[ListedSimpleFeature] [CONS] count: "+featureType.getAttributeCount());
        //System.out.println("[ListedSimpleFeature] [CONS] size : "+this.values.size());
        
        this.validating = validating;

        // if we're self validating, do validation right now
        if(validating)
            validate();
    }
    
    @Override
    public FeatureId getIdentifier() {
        return id;
    }
    
    @Override
    public String getID() {
    	return id.getID();
    }
    
   
    @Override
    public Object getAttribute(int index) throws IndexOutOfBoundsException {
        return values.get(index);
    }
    
    @Override
    public Object getAttribute(String name) {
        Integer idx = featureType.indexOf(name);
        if(idx != null)
            return getAttribute(idx);
        else
            return null;
    }

    @Override
    public Object getAttribute(Name name) {
        return getAttribute( name.getLocalPart() );
    }

    @Override
    public int getAttributeCount() {
        return values.size();
    }

    @Override
    public List<Object> getAttributes() {
        return new ArrayList<Object>(Arrays.asList( values ));
    }

    @Override
    public Object getDefaultGeometry() {
        // should be specified in the index as the default key (null)
        Object defaultGeometry = 
        	featureType.indexOf((String)null) != -1 ? getAttribute( featureType.indexOf((String)null) ) : null;
            
        // not found? Ok, let's do a lookup then...
        if ( defaultGeometry == null ) {
            for ( Object o : values ) {
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
        return featureType;
    }

    @Override
    public SimpleFeatureType getType() {
        return featureType;
    }

    @Override
    public void setAttribute(int index, Object value)
        throws IndexOutOfBoundsException {
        // first do conversion
        Object converted = Converters.convert(value, getFeatureType().getDescriptor(index).getType().getBinding());
        // if necessary, validation too
        if(validating)
            Types.validate(featureType.getDescriptor(index), converted);
        // finally set the value into the feature
        values.set(index, converted);
    }
    
    @Override
    public void setAttribute(String name, Object value) {
        final Integer idx = featureType.indexOf(name);
        if(idx == null)
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
    	  this.values = new ArrayList<Object>(featureType.getAttributeCount());
    	  for(int i = 0; i < featureType.getAttributeCount(); i++){
    	    this.values.add(null);
    	  }
    	}
    }

    @Override
    public void setDefaultGeometry(Object geometry) {
        Integer geometryIndex = featureType.indexOf((String)null);
        if ( geometryIndex != null ) {
            setAttribute( geometryIndex, geometry );
        }
    }

    @Override
    public BoundingBox getBounds() {
        //TODO: cache this value
        ReferencedEnvelope bounds = new ReferencedEnvelope( featureType.getCoordinateReferenceSystem() );
        for ( Object o : values ) {
            if ( o instanceof Geometry ) {
                Geometry g = (Geometry) o;
                //TODO: check userData for crs... and ensure its of the same 
                // crs as the feature type
                if ( bounds.isNull() ) {
                    bounds.init(g.getEnvelopeInternal());
                }
                else {
                    bounds.expandToInclude(g.getEnvelopeInternal());
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
        final Integer idx = featureType.indexOf(name);
        if(idx != null) {
            // cast temporarily to a plain collection to avoid type problems with generics
            Collection c = Collections.singleton( new Attribute( idx ) );
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
        final Integer idx = featureType.indexOf(name);
        if(idx == null)
            return null;
        else
            return new Attribute( idx );
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

    @Override
    public void setValue(Object newValue) {
        setValue( (Collection<Property>) newValue );
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
        if(userData == null)
            userData = new HashMap<Object, Object>();
        return userData;
    }

    
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//RR REDEFINITION                                                   RR
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
    
  // -- Object ---------------------------------------------------------  

    /**
     * override of equals.  Returns if the passed in object is equal to this.
     *
     * @param obj the Object to test for equality.
     *
     * @return <code>true</code> if the object is equal, <code>false</code>
     *         otherwise.
     */
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
      if (id == null) {
        if (feat.getIdentifier() != null) {
          return false;
        }
      }

      if (!id.equals(feat.getIdentifier())) {
        return false;
      }

      if (!feat.getFeatureType().equals(featureType)) {
        return false;
      }

      for (int i = 0, ii = values.size(); i < ii; i++) {
        Object otherAtt = feat.getAttribute(i);

        if (values.get(i) == null) {
          if (otherAtt != null) {
            return false;
          }
        } else {
          if (!values.get(i).equals(otherAtt)) {
            if (values.get(i) instanceof Geometry
                && otherAtt instanceof Geometry) {
              // we need to special case Geometry
              // as JTS is broken Geometry.equals( Object ) 
              // and Geometry.equals( Geometry ) are different 
              // (We should fold this knowledge into AttributeType...)
              if (!((Geometry) values.get(i)).equals(
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
        for (int i = 0; i < values.size(); i++) {
            AttributeDescriptor descriptor = getType().getDescriptor(i);
            Types.validate(descriptor, values.get(i));
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
            values.set(index, element.getValue());
            return null;
        }
        
        public int size() {
            return values.size();
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
            return featureType.getDescriptor(index);
        }

        public AttributeType getType() {
            return featureType.getType(index);
        }

        public Name getName() {
            return getDescriptor().getName();
        }

        public Map<Object, Object> getUserData() {
            // lazily create the user data holder
            if(attributeUserData == null)
                attributeUserData = new HashMap[values.size()];
            // lazily create the attribute user data
            if(attributeUserData[index] == null)
                attributeUserData[index] = new HashMap<Object, Object>();
            return attributeUserData[index];
        }

        public Object getValue() {
            return values.get(index);
        }

        public boolean isNillable() {
            return getDescriptor().isNillable();
        }

        public void setValue(Object newValue) {
            values.set(index, newValue);
            
        }
        
        public void validate() {
            Types.validate(getDescriptor(), values.get(index));
        }
        
    }
  
}