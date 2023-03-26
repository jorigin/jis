package org.jorigin.gis.geotools.swing;

import static org.junit.Assert.assertTrue;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.referencing.CRS;
import org.jorigin.Common;
import org.jorigin.jis.CRSDefaults;
import org.jorigin.jis.feature.ListedSimpleFeature;
import org.jorigin.jis.feature.ListedSimpleFeatureType;
import org.jorigin.jis.map.ExtendedMapContent;
import org.jorigin.jis.swing.JFeatureTable;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A class dedicated to the test of a 
 * @author seint
 *
 */
public class JFeatureTableTest {
  
  private static final int LAYER_COUNT             = 3;
  
  private static final int FEATURE_COUNT           = 10;
  
  private static ExtendedMapContent context = null;
  
  private static boolean initialized               = false;
  
  /**
   * Init the context.
   */
  public static void initContext(){
	  
    try {
      
      List<Layer> layers = new ArrayList<Layer>(LAYER_COUNT);
      
      for(int i = 0; i < LAYER_COUNT; i++){
        ListedSimpleFeatureType type = new ListedSimpleFeatureType(new NameImpl("TEST_FEATURE_TYPE"));
        
        MemoryFeatureCollection col  = new MemoryFeatureCollection(type);
        
        for(int j = 0; j < FEATURE_COUNT; j++){
          ListedSimpleFeature feat     = new ListedSimpleFeature(type, new FeatureIdImpl("FEATURE_"+i+"-"+j));
          col.add(feat);
        }
        
        // Creation du layers
        Layer layer = new FeatureLayer(col, null);
        layer.setTitle("LAYER_"+i);
        layers.add(layer);
      }
      Common.logger.log(Level.INFO, "Init Context");

	  context = new ExtendedMapContent(layers, "Title of the Test Map Context", 
		                                          "Abstract of the Test Map Context", 
		                                          "Contact Informations of the Test Map Context", 
		                                          new String[]{"TMP KEYWORD_1", "TMP KEYWORD_2", "TMP KEYWORD_3"}, 
		                                          CRS.parseWKT(CRSDefaults.WSG84_WKT));
		Common.logger.log(Level.INFO, "Context title              : "+context.getTitle());
	      Common.logger.log(Level.INFO, "Context abstract           : "+context.getUserData().get("abstract"));
	      Common.logger.log(Level.INFO, "Context contact information: "+context.getUserData().get("contact"));
	      Common.logger.log(Level.INFO, "Context key words          : "+context.getUserData().get("keywords"));
	      Common.logger.log(Level.INFO, "Context CRS                : "+context.getViewport().getCoordinateReferenceSystem());
	      initialized = true;
      } catch (Exception e) {
		Common.logger.log(Level.SEVERE, "Cannot initialize MapContext", e);  
		initialized = false;
		context = null;
      }
  }
  
  private static void createAndShowGUI() {
    Dimension dimension = new Dimension(480, 640);
    JFrame jframe = new JFrame("FEATURE_TABLE_TEST");
    jframe.setSize(dimension);
    jframe.setPreferredSize(dimension);
    
    JFeatureTable featureTable = new JFeatureTable();
    
    jframe.getContentPane().setLayout(new BorderLayout());
    jframe.getContentPane().add(featureTable.getTableHeader(), BorderLayout.NORTH);
    jframe.getContentPane().add(featureTable, BorderLayout.CENTER);
   
    
    featureTable.setData(context);
    
    jframe.setVisible(true);
  }
  
  /**
   * Initialize the tests.
   */
  @BeforeClass 
  public static void initTest(){
    Common.logger.log(Level.INFO, "Init test");
    
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException ex) {
      Common.logger.log(Level.WARNING, "Cannot set look and feel", ex);
    } catch (InstantiationException ex) {
      Common.logger.log(Level.WARNING, "Cannot set look and feel", ex);
    } catch (IllegalAccessException ex) {
      Common.logger.log(Level.WARNING, "Cannot set look and feel", ex);
    } catch (UnsupportedLookAndFeelException ex) {
      Common.logger.log(Level.WARNING, "Cannot set look and feel", ex);
    }  

    
    initContext(); 
  }
  
  /**
   * First test.
   */
  @Test 
  public void firstTest(){
    
    if (initialized){
      Common.logger.log(Level.INFO, "Processing Test 1");
      //Schedule a job for the event-dispatching thread:
      //creating and showing this application's GUI.
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
          public void run() {
              createAndShowGUI();
          }
      });
      
      
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        
      }
      
      assertTrue(true);   
      
      Common.logger.log(Level.INFO, "Processing Test ok");
    }
  }
  
  /**
   * Compatibility with Ant 1.6.5 and JUnit 3.8.x
   * @return Test
   */
  public static junit.framework.Test suite(){
    
    return new junit.framework.JUnit4TestAdapter(JFeatureTableTest.class);
  }

  /**
   * Main entry point.
   * @param args entry points arguments.
   */
  public static void main(String[] args){
    org.junit.runner.JUnitCore runner = new org.junit.runner.JUnitCore();
    runner.run(JFeatureTableTest.class);
  }


}
