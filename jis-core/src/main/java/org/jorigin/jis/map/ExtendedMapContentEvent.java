package org.jorigin.jis.map;

import java.awt.AWTEvent;

import org.jorigin.jis.JIS;


/**
 * The event fired by an {@link org.jorigin.jis.map.ExtendedMapContent extended map content}.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class ExtendedMapContentEvent extends AWTEvent {


  private static final long serialVersionUID = JIS.BUILD;

  /**
   * Flag set when a {@link org.geotools.map.MapLayer layer} is selected.
   * @see #getID()
   */
  public static final int LAYER_SELECTED               = AWTEvent.RESERVED_ID_MAX+1;
  
  /**
   * Flag set when a {@link org.geotools.map.MapLayer layer} is set to be selectable.
   * @see #getID()
   */
  public static final int LAYER_SELECTABLILITY_CHANGED = LAYER_SELECTED+1;
  
  /**
   * Flag set when a {@link org.geotools.map.MapLayer layers} is set to be focusable
   * @see #getID()
   */
  public static final int LAYER_FOCUSABILITY_CHANGED   = LAYER_SELECTABLILITY_CHANGED+1;
  
  /**
   * Flag set when a {@link org.opengis.feature.simple.SimpleFeature feature} is selected.
   * @see #getID()
   */
  public static final int FEATURE_SELECTED             = LAYER_FOCUSABILITY_CHANGED+1;
  
  
  /**
   * Creates a new instance of <code>ExtendedMapContextEvent</code> with the specified reason.
   *
   * @param source The source of the event change.
   * @param id Why the event was fired.
   *
   * @throws IllegalArgumentException If the <code>reason</code> is not a valid enum.
   */
  public ExtendedMapContentEvent(final Object source, final int id){
    super(source, id);  
  }
}
