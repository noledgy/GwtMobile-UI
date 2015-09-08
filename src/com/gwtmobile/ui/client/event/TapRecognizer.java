package com.gwtmobile.ui.client.event;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kevindeem
 *
 * This is its own HandlerRegistration
 *
 */
public class TapRecognizer implements TouchStartHandler, TouchCancelHandler, TouchEndHandler, TouchMoveHandler, HandlerRegistration {


	Widget source;
	List<HandlerRegistration> registrations = new ArrayList<HandlerRegistration>();
	private int initialX = 0;
	private int initialY = 0;
	private int distance;
	boolean touchCancelled = false;

	public class FakeClickEvent extends ClickEvent {
		public FakeClickEvent() {

		}
		public FakeClickEvent(TouchEndEvent event) {
			setNativeEvent(event.getNativeEvent());
			setRelativeElement(event.getRelativeElement());
			setSource(event.getSource());
		}
	}


	public TapRecognizer(Widget source, int distance) {
		if (source == null)
			throw new IllegalArgumentException("source can not be null");
		if (distance < 0)
			throw new IllegalArgumentException("distance has to be greater than zero");
		this.source = source;
		this.distance = distance;
		registrations.add(source.addDomHandler(this, TouchStartEvent.getType()));
		registrations.add(source.addDomHandler(this, TouchMoveEvent.getType()));
		registrations.add(source.addDomHandler(this, TouchCancelEvent.getType()));
		registrations.add(source.addDomHandler(this, TouchEndEvent.getType()));
	}



	@Override
	public void onTouchStart(TouchStartEvent event) {
		touchCancelled = false;
		Touch touch = event.getTouches().get(0);
		initialX = touch.getPageX();
		initialY = touch.getPageY();
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		if (initialX == 0 && initialY == 0) {
			Touch touch = event.getTouches().get(0);
			initialX = touch.getPageX();
			initialY = touch.getPageY();
		}
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		Touch touch = event.getTouches().get(0);
		int x = touch.getPageX();
		int y = touch.getPageY();
		if (!touchCancelled
				&& Math.abs(x - initialX) < distance
				&& Math.abs(y - initialY) < distance
				&& null != source) {
			source.fireEvent(new FakeClickEvent(event));
		}
		initialX = initialY = 0;
	}

	@Override
	public void onTouchCancel(TouchCancelEvent event) {
		touchCancelled = true;
		initialX = initialY = 0;
	}


	@Override
	public void removeHandler() {
		for (HandlerRegistration r : registrations) {
			r.removeHandler();
		}
	}
}
