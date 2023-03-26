package org.jorigin.jis.swing;

import java.awt.AWTEvent;

/**
 * This class represnts the event generated by a {@link JMapPanel JMapPanel}.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 */
public class JMapPanelEvent extends AWTEvent {

  /**
   * Event id that represent a focus event on feature.
   */
  public static final int FEATURE_FOCUSED    = AWTEvent.RESERVED_ID_MAX +1;
  
  /**
   * Event id that represents a selection event on feature.
   */
  public static final int FEATURE_SELECTED   = AWTEvent.RESERVED_ID_MAX +2;
  
  /**
   * Event id that represents a digitalization start event on feature.
   */
  public static final int DIGITALIZING_STARTED = AWTEvent.RESERVED_ID_MAX +3;
  
  /**
   * Event id that represents a digitalization stop event on feature.
   */
  public static final int DIGITALIZING_STOPED  = AWTEvent.RESERVED_ID_MAX +4;
  
  /**
   * The serial version UID
   */
  private static final long serialVersionUID = 2287301492375005149L;

  /**
   * The reserved id identifier max.
   * @see AWTEvent#RESERVED_ID_MAX
   */
  public static final int RESERVED_ID_MAX    = AWTEvent.RESERVED_ID_MAX +25;
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                            CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  /**
   * Construct a new panel event from a previous fired event.
   * @param event the previous event
   */
  public JMapPanelEvent(AWTEvent event) {
    super(event.getSource(), event.getID());
  }
  

  /**
   * Construct a new panel event from a prevous fired event.
   * @param source the source of the event
   * @param id the id of the event
   */
  public JMapPanelEvent(Object source, int id) {
    super(source, id);
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                            CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
}