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

import java.util.EventObject;
import org.opengis.filter.Filter;

/**
 * An event that handle highlight changes.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 * @see SelectionChangedEvent
 */
public class HighlightChangedEvent extends EventObject {

    private static final long serialVersionUID = -7958576070524520655L;
    Object source;
    Filter filter;

    /**
     * Create a new highlight change event.
     * @param source the source of the event.
     * @param filter the {@link org.opengis.filter.Filter filter} of the event.
     */
    public HighlightChangedEvent(Object source, org.opengis.filter.Filter filter) {
        super(source);
        this.source = source;

        this.filter = filter;
    }

    /**
     * Get the {@link org.opengis.filter.Filter filter} that is attached to the event.
     * @return the {@link org.opengis.filter.Filter filter} that is attached to the event.
     * @see #setFilter(Filter)
     */
    public Filter getFilter() {
        return this.filter;
    }

    /**
     * Set the {@link org.opengis.filter.Filter filter} that is attached to the event.
     * @param filter the {@link org.opengis.filter.Filter filter} that is attached to the event.
     * @see #getFilter()
     */
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    /**
     * Get the source of the event.
     * @see #setSource(Object)
     */
    public Object getSource() {
        return this.source;
    }

    /**
     * Set the source of the event.
     * @param source the source of the event.
     * @see #getSource()
     */
    public void setSource(Object source) {
        this.source = source;
    }
}
