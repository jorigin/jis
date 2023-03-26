package org.jorigin.jis.swing.event;

import java.util.EventListener;

/**
 * A listener dedicated to the process of {@link org.jorigin.jis.swing.event.SelectionChangedEvent selection change event}.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 * @see HighlightChangeListener
 */
public interface SelectionChangeListener extends EventListener {
  
  /**
   * Process an {@link org.jorigin.jis.swing.event.SelectionChangedEvent selection change event}.
   * @param e the event to process.
   */
    public void selectionChanged(SelectionChangedEvent e);
}
