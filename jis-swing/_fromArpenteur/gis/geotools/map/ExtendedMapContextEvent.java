package org.arpenteur.gis.geotools.map;

import java.awt.AWTEvent;


/**
 * The event fired by an extended map context.
 * @author Julien Seinturier
 *
 */
public class ExtendedMapContextEvent extends AWTEvent {

  
  /**
   * 
   */
  private static final long serialVersionUID = -4080398041647783776L;

  /**
   * Flag set when a layer is selected.
   * @see #getID()
   */
  public static final int LAYER_SELECTED               = AWTEvent.RESERVED_ID_MAX+1;
  
  /**
   * Flag set when a layer is set to be selectable.
   * @see #getID()
   */
  public static final int LAYER_SELECTABLILITY_CHANGED = LAYER_SELECTED+1;
  
  /**
   * Flag sdet when a layer is set to be focusable
   * @see #getID()
   */
  public static final int LAYER_FOCUSABILITY_CHANGED   = LAYER_SELECTABLILITY_CHANGED+1;
  
  /**
   * Flag set when a feature is selected.
   * @see getID();
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
  public ExtendedMapContextEvent(final Object source, final int id){
    super(source, id);  
  }
}
