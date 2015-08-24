/*
 * Copyright (c) 2010 Zhihua (Dennis) Jiang
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtmobile.ui.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.CSS.StyleNames.Primary;
import com.gwtmobile.ui.client.CSS.StyleNames.Secondary;
import com.gwtmobile.ui.client.event.DragController;
import com.gwtmobile.ui.client.event.DragEvent;
import com.gwtmobile.ui.client.event.DragEventsHandler;
import com.gwtmobile.ui.client.event.SelectionChangedEvent;
import com.gwtmobile.ui.client.event.SelectionChangedHandler;
import com.gwtmobile.ui.client.utils.Utils;

public class ListPanel extends PanelBase implements ClickHandler, DragEventsHandler{

	public enum ShowArrow { Visible, Hidden };
	private ShowArrow _showArrow;
	private int _selected = -1;
	private boolean _selectable = true;
	private double _initialX = 0.0;
	private double _initialY = 0.0;

    public ListPanel() {
        addDomHandler(this, ClickEvent.getType());

        setStyleName(Primary.ListPanel);
    }

    @Override
    protected String getDesignTimeMessage() {
    	return "Add ListItems (recommended) or other widgets to the panel.";
    }

    @Override
    protected void onAttach() {
    	super.onAttach();
    }

    @Override
    protected void onDetach() {
    	super.onDetach();
    }

    public HandlerRegistration addSelectionChangedHandler(SelectionChangedHandler handler) {
        return this.addHandler(handler, SelectionChangedEvent.TYPE);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        DragController.get().addDragEventsHandler(this);
    }

    @Override
    public void onUnload() {
    	DragController.get().removeDragEventsHandler(this);
    }

    @Override
    public void add(Widget w) {
    	if (w instanceof ListItem || isDesignTimeEmptyLabel(w)) {
    		super.add(w);
    	}
    	else {
        	ListItem listItem = new ListItem();
        	super.add(listItem);
        	listItem.add(w);
        	if (_showArrow == ShowArrow.Visible) {
	        	Chevron chevron = new Chevron();
	        	listItem.add(chevron);
        	}
    	}
    }

    @Override
    public void onClick(ClickEvent e) {
		//Utils.Console("Clicking List #" +_selected);
    	// samsuns on 5.0.1 does not get click events for this control.
    	// so I moved the behavior to the drag end.
//        if (_selected >= 0) {
//    		ListItem item = (ListItem) getWidget(_selected);
//    		if (item.isEnabled()) {
//	            SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(_selected,
//	            	e.getNativeEvent().getEventTarget());
//	            this.fireEvent(selectionChangedEvent);
//	            Utils.Console("Firing selection event");
//	        	item.removeStyleName(Secondary.Pressed);
//    		}
//    		_selected = -1;
//        }
    }

    public void setDisplayArrow(ShowArrow show) {
    	_showArrow = show;
    	for (int i = 0; i < getWidgetCount(); i++) {
    		ListItem listItem = (ListItem) getWidget(i);
			listItem.setDisplayArrowFromParent(show);
		}
    }

    public ShowArrow getDisplayArrow() {
    	return _showArrow;
    }

    public void isSelectable(boolean selectable) {
    	this._selectable  = selectable;
    }

    public void setSelectable(boolean selectable) {
    	this._selectable  = selectable;
    }

    public boolean getSelectable() {
    	return _selectable;
    }

    @Override
    public void onDragStart(DragEvent e) {
    	if (_selectable) {
	    	_selected = Utils.getTargetItemIndex(getElement(), e.getNativeEvent().getEventTarget());
	    	if (_selected >= 0) {
	    		//Utils.Console("drag start selected item #" +_selected);
	    		_initialX = e.X;
	    		_initialY = e.Y;
	    		new Timer() {
					@Override
					public void run() {
				    	if (_selected >= 0) {
				    		ListItem item = (ListItem) getWidget(_selected);
				    		if (item.isEnabled()) {
					        	getWidget(_selected).addStyleName(Secondary.Pressed);
				    		}
				    	}
					}
				}.schedule(75);
	    	}
    	}
    }

    @Override
    public void onDragMove(DragEvent e) {
    	if (_selected >= 0) {
        	getWidget(_selected).removeStyleName(Secondary.Pressed);
        	if (_selected >= 0 && Math.abs(e.X - _initialX) > 40 && Math.abs(e.Y - _initialY) > 40) {
        		// we are scrolling or swiping, so no selection
    	    		//Utils.Console("drag move deselected item #" +_selected +" due to move");
    	    		new Timer() {
    					@Override
    					public void run() {
    				    	if (_selected >= 0) {
    				    		ListItem item = (ListItem) getWidget(_selected);
    				    		if (item.isEnabled()) {
    					        	getWidget(_selected).addStyleName(Secondary.Pressed);
    				    		}
    				    	}
    					}
    				}.schedule(75);
        		_selected = -1;
        	}
    	}
    }

    @Override
    public void onDragEnd(DragEvent e) {
    	if (_selected >= 0 && Math.abs(e.X - _initialX) < 40 && Math.abs(e.Y - _initialY) < 40) {
    		// Something is selected and we aren't scrolling or swiping or something.
        	getWidget(_selected).removeStyleName(Secondary.Pressed);
    		//_selected = -1; need to keep the selected value for click event.
    		//Utils.Console("drag end selected item #" +_selected);
    		ListItem item = (ListItem) getWidget(_selected);
    		if (item.isEnabled()) {
	            SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(_selected,
	            	e.getNativeEvent().getEventTarget());
	            this.fireEvent(selectionChangedEvent);
	            Utils.Console("Firing selection event");
	            new Timer() {
	            	@Override
	            	public void run() {
				    	if (_selected >= 0) {
				    		ListItem item = (ListItem) getWidget(_selected);
				    		if (item.isEnabled()) {
					        	getWidget(_selected).removeStyleName(Secondary.Pressed);
				    		}
				    	}
	            	}
	            }.schedule(75);
    		}
    		_selected = -1;
    	}
    }

    public ListItem getItem(int index) {
    	return (ListItem) getWidget(index);
    }

    static class Chevron extends HTML {
    	public Chevron() {
    		super("<div class=\"Chevron\"><span></span><span></span></div>");
    	}
    }

}
