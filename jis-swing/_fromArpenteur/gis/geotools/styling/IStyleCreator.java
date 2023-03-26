package org.arpenteur.gis.geotools.styling;

import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * This interface enable to create specialized style creators. A style creator 
 * is an object that can create specific style for a given feature type.
 * @author Julien Seinturier
 *
 */
public interface IStyleCreator {

  /**
   * Create a specific style for the given feature type.
   * @param schema the feature type.
   * @return the created style.
   */
  public Style createStyle(SimpleFeatureType schema);

  /**
   * Get the schema for which the style creator is designed.
   * @return the schema associated to this style creator.
   */
  public SimpleFeatureType getSchema();
}
