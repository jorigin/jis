/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2006, GeoTools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.jorigin.jis.swing.event;

import java.util.EventListener;

/**
 * A listener dedicated to the process of {@link org.jorigin.jis.swing.event.HighlightChangedEvent highlight change event}.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 * @see SelectionChangeListener
 */
public interface HighlightChangeListener extends EventListener {
  
  /**
   * Process an {@link org.jorigin.jis.swing.event.HighlightChangedEvent highlight change event}.
   * @param e the event to process.
   */
    public void highlightChanged(HighlightChangedEvent e);
}
