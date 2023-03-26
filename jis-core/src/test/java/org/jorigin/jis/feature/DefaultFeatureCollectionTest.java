package org.jorigin.jis.feature;

import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;
import java.util.logging.Level;

import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.feature.NameImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.jorigin.Common;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Test class for {@link org.jorigin.jis.feature.ListenedFeatureCollection ListenedFeatureCollection}
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class DefaultFeatureCollectionTest {
  
  /**
   * Test feature count
   */
  public static final int FEATURE_COUNT        = 150;
  
  private  ListedSimpleFeatureType type        = null;

  /**
   * Create a new collection
   * @return the new collection.
   */
  public ListenedFeatureCollection createCollection(){
    ListenedFeatureCollection collection = null;
    
    collection = new ListenedFeatureCollection(this.type);
    
    for(int j = 0; j < FEATURE_COUNT; j++){
      ListedSimpleFeature feat     = new ListedSimpleFeature(this.type, new FeatureIdImpl("FEATURE_"+j));
      collection.add(feat);
    }
    
    return collection;
  }
  
  /**
   * Create a feature with the given ID
   * @param id the id of the feature
   * @return the created feature.
   */
  public ListedSimpleFeature createFeature(String id){
    return new ListedSimpleFeature(this.type, new FeatureIdImpl(id));
  }
  
  /**
   * Initialize the test
   */
  @Before
  public void init(){
    this.type = new ListedSimpleFeatureType(new NameImpl("TEST_FEATURE_TYPE"));
  }
  
  /**
   * Testing feature add.
   */
  @Test
  public void addTest(){

    String featureId                    = "ADDED_FEATURE_ID";
    ListenedFeatureCollection collection = createCollection();
    SimpleFeature feature               = createFeature(featureId);
    boolean test                        = false;
    
    try {
      test = collection.add(feature);
      
      if (test == true){
        Common.logger.log(Level.INFO,"Feature "+featureId+" added to the collection");
      } else {
        Common.logger.log(Level.INFO,"Cannot add feature "+featureId+" to the collection");
      }
      assertTrue(test);
      
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE,"Cannot add feature "+featureId+" to the collection", e);
      assertTrue(false);
    }
    
    
    
    try {
      test = feature == collection.getFeatureMember(featureId);
      
      if (test == true){
        Common.logger.log(Level.INFO,"Feature "+featureId+" got to the collection");
      } else {
        Common.logger.log(Level.INFO,"Cannot get feature "+featureId+" from the collection");
      }
      assertTrue(test);
      
    } catch (NoSuchElementException e) {
      Common.logger.log(Level.SEVERE,"Cannot get feature "+featureId+" from the collection", e);
      assertTrue(false);
    }
  }
  
  /**
   * Testing feature remove.
   */
  @Test
  public void removeTest(){

    boolean test                        = false;
    String featureId                    = "ADDED_FEATURE_ID";
    ListenedFeatureCollection collection = createCollection();
    SimpleFeature feature               = createFeature(featureId);
    collection.add(feature);
   
    try {
      test = collection.remove(feature);
      
      if (test == true){
        Common.logger.log(Level.INFO,"Feature "+featureId+" removed from the collection.");
      } else {
        Common.logger.log(Level.INFO,"Cannot remove feature "+featureId+" from the collection.");
      }
      assertTrue(test);
      
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE,"Cannot remove feature "+featureId+" from the collection: ", e);
      assertTrue(false);
    }
  }
  
  /**
   * Testing feature member.
   */
  @Test
  public void removeFeatureMemberTest(){

    boolean test                        = false;
    String featureId                    = "ADDED_FEATURE_ID";
    ListenedFeatureCollection collection = createCollection();
    SimpleFeature feature               = createFeature(featureId);
    collection.add(feature);
   
    try {
      test = (collection.removeFeatureMember(featureId) == feature);
      
      if (test == true){
        Common.logger.log(Level.INFO,"Feature "+featureId+" removed from the collection.");
      } else {
        Common.logger.log(Level.INFO,"Cannot remove feature "+featureId+" from the collection.");
      }
      assertTrue(test);
      
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE,"Cannot remove feature "+featureId+" from the collection: ", e);
      assertTrue(false);
    }
  }
  
  /**
   * Testing feature add event.
   */
  @Test
  public void featureEventAddTest(){
    String featureId                    = "ADDED_FEATURE_ID";
    ListenedFeatureCollection collection = createCollection();
    
    collection.addFeatureListener(new FeatureListener(){

      @Override
      public void changed(FeatureEvent tce) {
        
      }
      
    });
    
    SimpleFeature feature               = createFeature(featureId);
    collection.add(feature);
  }
  
  /**
   * Compatibility with Ant 1.6.5 and JUnit 3.8.x
   * @return Test
   */
  public static junit.framework.Test suite(){
    
    return new junit.framework.JUnit4TestAdapter(DefaultFeatureCollectionTest.class);
  }

  /**
   * A main entry point
   * @param args entry point .parameters.
   */
  public static void main(String[] args){
    org.junit.runner.JUnitCore runner = new org.junit.runner.JUnitCore();
    runner.run(DefaultFeatureCollectionTest.class);
  }
}
