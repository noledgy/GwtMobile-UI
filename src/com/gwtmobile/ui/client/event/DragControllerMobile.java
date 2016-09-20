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

package com.gwtmobile.ui.client.event;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.gwtmobile.ui.client.utils.Point;
import com.gwtmobile.ui.client.utils.Utils;

public class DragControllerMobile extends DragController {

    protected boolean _touchMoving = false;
    static protected boolean _stopPropagation = true;
    protected Element _touchTarget = null;
	private double _lastClickMillis = 0.0;
	protected Point _initialDragPos = new Point(0,0);
	protected double touchMovementThreshold = 60;


    DragControllerMobile() {
//      Utils.Console("New DragController instance created");
		String agent = Window.Navigator.getUserAgent();
    	if (agent.contains("Android 5.")) {
    		_stopPropagation = false;
    	}
    }

    @Override
    protected void registerEvents() {
        super.registerEvents();
        if (_dragStartListener == null) {
            _dragStartListener = Utils.addEventListener(_source.getElement(), "touchstart", true, this);
            _dragMoveListener = Utils.addEventListener(_source.getElement(), "touchmove", true, this);
            _dragEndListener = Utils.addEventListener(_source.getElement(), "touchend", true, this);
        }
    }

    @Override
    protected void unregisterEvents() {
        super.unregisterEvents();
        if (_dragStartListener != null) {
            Utils.removeEventListener(_source.getElement(), "touchstart", true, _dragStartListener);
            Utils.removeEventListener(_source.getElement(), "touchmove", true, _dragMoveListener);
            Utils.removeEventListener(_source.getElement(), "touchend", true, _dragEndListener);
            _dragStartListener = _dragMoveListener = _dragEndListener = null;
        }
    }

	public void onTouchStart(TouchEvent e) {
        EventTarget target = e.getEventTarget();
        boolean preventDefault = true;
        _initialDragPos = new Point(e.getScreenX(),e.getScreenY());
        if (Element.is(target)) {
            Element ele = Element.as(target);
            //INPUT element will not get focus if default action is prevented.
            if (Utils.isHtmlFormControl(ele)) {
                ele.focus();
                preventDefault = false;
            }
            _touchTarget = ele;
        }
        else {
        	_touchTarget = null;
        }
        //Utils.Console("Drag Controller Start");

        if (preventDefault && _stopPropagation) {
			//Utils.Console("Drag controller Start Propogation Stop");
            e.preventDefault();   //prevent default action of selecting text
            e.stopPropagation();
        }
        //FIXME: for multi-touch platforms.
		onStart(e, new Point(e.touches().get(0).getClientX(), e.touches().get(0).getClientY()));
	}

	public void onTouchMove(TouchEvent e) {
        //Utils.Console("Drag Controller Move");
		if (_stopPropagation) {
			//Utils.Console("Drag controller Move Propogation Stop");
			e.preventDefault();
			e.stopPropagation();
		}
		if (_initialDragPos.X() == 0.0 && _initialDragPos.Y() == 0.0) {
			_initialDragPos = new Point(e.getScreenX(),e.getScreenY());
		}
		if (!_touchMoving) {
			double deltaX = Math.abs(e.getScreenX() - _initialDragPos.X());
			double deltaY = Math.abs(e.getScreenY() - _initialDragPos.Y());
			_touchMoving = deltaX > touchMovementThreshold || deltaY
					> touchMovementThreshold;
		}
		onMove(e, new Point(e.touches().get(0).getClientX(), e.touches().get(0).getClientY()));
	}

	public void onTouchEnd(TouchEvent e) {
		if (_stopPropagation) {
			//Utils.Console("Drag controller End Propogation Stop");
			e.preventDefault();
			e.stopPropagation();
		}
        //Utils.Console("Drag Controller End");
		double deltaX = Math.abs(e.getScreenX() - _initialDragPos.X());
		double deltaY = Math.abs(e.getScreenY() - _initialDragPos.Y());
		_touchMoving = deltaX > touchMovementThreshold || deltaY
				> touchMovementThreshold;
		if (!_touchMoving) {
			//Utils.Console("Drag Controller fireclick ");
			if (_touchTarget != null) {
				fireClick(_touchTarget);
			}
		}// else {
			//Utils.Console("Drag Controller move end ");
		//}
		onEnd(e, new Point(e.changedTouches().get(0).getClientX(), e.changedTouches().get(0).getClientY()));
		_touchMoving = false;
		_initialDragPos = new Point(0,0);
	}

	@Override
    public void onBrowserEvent(Event e) {
		String type = e.getType();
//    Utils.Console("Event type: " + type);
		if (type.equals("touchstart")) {
			onTouchStart((TouchEvent)e);
		}
		else if (type.equals("touchmove")) {
			onTouchMove((TouchEvent)e);
		}
		else if (type.equals("touchend")) {
			onTouchEnd((TouchEvent)e);
		}
		else if (type.equals("click")) {
//	    Utils.Console("Android version: " + Utils.getAndroidMajorVersion());
      if (_touchTarget != null) {
        onEnd(e, new Point(e.getClientX(), e.getClientY()));
      }
		}
		else {
		    super.onBrowserEvent(e);
		}
	}

	/**
	 * Sets the stop propagation. For widgets such as pan and zoom maps.
	 *
	 * @author Frank Mena
	 */
	public void setStopPropagation() {

    if (Utils.isAndroidVerAtLeast(5)) {
//		if (agent.contains("Android 6.")) {
			_stopPropagation = false;
		} else {
			_stopPropagation = true;
		}
	}

	public void setStartPropagation() {
		_stopPropagation = false;
	}

	protected native void fireClick(Element theTarget) /*-{

		// http://stackoverflow.com/questions/7184573/pick-up-the-android-version-in-the-browser-by-javascript
		var ua = $wnd.navigator.userAgent;
		if (ua.indexOf("Android") >= 0) {
			var androidversion = parseFloat(ua.slice(ua.indexOf("Android") + 8));
			if (androidversion >= 4.1) {
				// http://code.google.com/p/android/issues/detail?id=38808
				//return;

				// instead of killing all events...
				var currMillis = (new Date()).getTime();
				var lastMillis = this.@com.gwtmobile.ui.client.event.DragControllerMobile::_lastClickMillis;
				if ((currMillis - lastMillis) < 50) {
					this.@com.gwtmobile.ui.client.event.DragControllerMobile::_lastClickMillis = currMillis;
					return;
				}
			}
		}

		if (theTarget.nodeType == 3)
			theTarget = theTarget.parentNode;

		var theEvent = $doc.createEvent('MouseEvents');
		theEvent.initEvent('click', true, true);
		theTarget.dispatchEvent(theEvent);
		//$wnd.console.log("firing generated click");
	}-*/;

}
