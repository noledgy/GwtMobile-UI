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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.gwtmobile.ui.client.CSS.StyleNames;
import com.gwtmobile.ui.client.event.DragController;
import com.gwtmobile.ui.client.event.DragEvent;
import com.gwtmobile.ui.client.event.DragEventsHandler;

public class Button extends com.google.gwt.user.client.ui.Button implements DragEventsHandler, IsGwtMobileWidget {

	private boolean _isDisabled = false;
	private IsGwtMobileWidgetHelper _widgetHelper = new IsGwtMobileWidgetHelper();
	List<ClickHandler> _handlers = new ArrayList<ClickHandler>();
	long _lastClick = 0;

	public class ButtonHandlerRegistration implements HandlerRegistration
	{
		ClickHandler handler;

		public ButtonHandlerRegistration(ClickHandler handler) {
			this.handler = handler;
			_handlers.add(handler);
		}

		@Override
		public void removeHandler() {
			if (_handlers.contains(handler)) {
				_handlers.remove(handler);
			}
		}
	}

    public Button() {
        setStyleName(StyleNames.Primary.Button);
        super.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				long now = System.currentTimeMillis();
				if (now - _lastClick > 300) {
					for (ClickHandler h : _handlers) {
						h.onClick(event);
					}
					_lastClick = now;
				}
			}
		});
    }

    public Button(String caption, ClickHandler handler) {
        this();
        setText(caption);
        this.addClickHandler(handler);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
    	return new ButtonHandlerRegistration(handler);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        //DragController.get().addDragEventsHandler(this);
        _widgetHelper.CheckInitialLoad(this);
    }

    @Override
    public void onUnload() {
        //DragController.get().removeDragEventsHandler(this);
    }

    @Override
    public void onDragStart(DragEvent e) {
    	if (!_isDisabled) {
            addStyleName(StyleNames.Secondary.Pressed);
    	}
        e.stopPropagation();
    }

    @Override
    public void onDragMove(DragEvent e) {
    	if (!_isDisabled) {
    		removeStyleName(StyleNames.Secondary.Pressed);
    	}
        e.stopPropagation();
    }

    @Override
    public void onDragEnd(DragEvent e) {
    	if (!_isDisabled) {
    		removeStyleName(StyleNames.Secondary.Pressed);
    	}
    	else {
    		DragController.get().suppressNextClick();
    	}
        e.stopPropagation();
    }

    public void setDisabled(boolean disabled) {
    	_isDisabled = disabled;
    	if (disabled) {
    		addStyleName(StyleNames.Secondary.Disabled);
    	}
    	else {
    		removeStyleName(StyleNames.Secondary.Disabled);
    	}
    }

    public boolean isDisabled() {
    	return _isDisabled;
    }

	@Override
	public void onInitialLoad() {
	}

	@Override
	public void setSecondaryStyle(String style) {
		_widgetHelper.setSecondaryStyle(this, style);
	}
}
