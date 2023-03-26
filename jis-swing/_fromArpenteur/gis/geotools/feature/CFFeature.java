package org.arpenteur.gis.geotools.feature;

import org.arpenteur.common.geometry.primitive.Plan;
import org.arpenteur.mesurable.ItemMesurable;
import org.arpenteur.mesurable.architecture.elementDeParement.Bloc;

public class CFFeature extends ItemMesurableFeature {
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  /**
   * Construct a new feature representing a Corpo di Fabrica. The feature type must
   * be specified because it's the type which contains coordinate reference system
   * informations. The default feature identifier is set to <code>item.getIdentifier()</code>
   * @param type the type of the feature.
   * @param item the corpo di fabrica represented by the created feature.
   */
  public CFFeature(CFFeatureType type, ItemMesurable item){
    this(type, item, null, null);
  }
  
  /**
   * Construct a new feature representing a Corpo di Fabrica. The feature type must be
   * specified because it's the type which contains coordinate reference system informations.
   * @param type the type of the feature.
   * @param item the the corpo di fabrica represented by the created feature.
   * @param featureId the feature identificator. If this parameter is <code>null</code>, then the default
   * @param projectionPlane the plane where the item is projected.
   * value is given by <code>item.getIdentifier()</code>
   * @see #CFFeature(CFFeatureType, Bloc)
   * @see #org.arpenteur.gis.geotools.feature.CFFeatureType
   */
  public CFFeature(CFFeatureType type, ItemMesurable item, String featureId, Plan projectionPlane){
    super(type, item, featureId, projectionPlane); 
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

}
