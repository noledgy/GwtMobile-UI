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

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.CSS.StyleNames.Primary;
import com.gwtmobile.ui.client.CSS.StyleNames.Secondary;
import com.gwtmobile.ui.client.event.DragEvent;
import com.gwtmobile.ui.client.event.DragEventsHandler;
import com.gwtmobile.ui.client.event.SelectionChangedEvent;
import com.gwtmobile.ui.client.event.SelectionChangedHandler;
import com.gwtmobile.ui.client.utils.Utils;

public class ListPanel extends PanelBase implements ClickHandler,
		DragEventsHandler, TouchStartHandler, TouchCancelHandler,
		TouchEndHandler, TouchMoveHandler {

	public enum ShowArrow { Visible, Hidden };
    private ShowArrow _showArrow;
    private int _selected = -1;
    private boolean _selectable = true;
    private double _initialX = 0.0;
    private double _initialY = 0.0;
    private long _lastClick = 0l;
	private boolean touchCancelled;
	private int initialX;
	private int initialY;
	private boolean moving;
	private int distance = 10;
	List<HandlerRegistration> registrations = new ArrayList<HandlerRegistration>();

    public ListPanel() {
        //addDomHandler(this, ClickEvent.getType());

        setStyleName(Primary.ListPanel);
    }

    @Override
    protected String getDesignTimeMessage() {
        return "Add ListItems (recommended) or other widgets to the panel.";
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        if (Utils.isDesktop()) {
        	registrations.add(addDomHandler(this, ClickEvent.getType()));
        } else {
        	registrations.add(addDomHandler(this, TouchStartEvent.getType()));
        	registrations.add(addDomHandler(this, TouchMoveEvent.getType()));
        	registrations.add(addDomHandler(this, TouchCancelEvent.getType()));
        	registrations.add(addDomHandler(this, TouchEndEvent.getType()));
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        removeHandlers();
    }

	public void removeHandlers() {
		for (HandlerRegistration r : registrations) {
			r.removeHandler();
		}
	}

    public HandlerRegistration addSelectionChangedHandler(SelectionChangedHandler handler) {
        return this.addHandler(handler, SelectionChangedEvent.TYPE);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        //DragController.get().addDragEventsHandler(this);
        for (Widget w : getChildren()) {
            if (w instanceof ListItem) {
                ((ListItem)w).setClickHandler(this);
            }
        }
    }

    @Override
    public void onUnload() {
        //DragController.get().removeDragEventsHandler(this);
    }

    @Override
    public void add(Widget w) {
        if (w instanceof ListItem || isDesignTimeEmptyLabel(w)) {
            super.add(w);
            ((ListItem)w).setClickHandler(this);
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

    /* (non-Javadoc)
     * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
     */
    @Override
    public void onClick(ClickEvent e) {
        // this version of on click gets triggered by items being touched.
        long milis = System.currentTimeMillis();
        if (_lastClick > 0l && milis - _lastClick < 40) {
            return;
        }
        _lastClick = milis;
        _selected = getChildren().indexOf((Widget)e.getSource());
        if (_selected >= 0) {
            ListItem item = (ListItem) getWidget(_selected);
            if (item.isEnabled() && !item.isMoving()) {
                SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(_selected,
                    e.getNativeEvent().getEventTarget());
                this.fireEvent(selectionChangedEvent);
                //Utils.Console("Firing selection event");
                item.removeStyleName(Secondary.Pressed);
            }
            _selected = -1;
        } else {
            //Utils.Console("Recieved Click Event for non item");
        }
    }

    // Old version that depended on drag listeners
//    @Override
//    public void onClick(ClickEvent e) {
//    	// when not android 5.0
//
//    	Object source = e.getSource();
//    	Utils.Console(source.getClass().getName());
//    	int[] androidVersion = Utils.getAndroidVersionValues();
//    	if (null == androidVersion || androidVersion[0] < 5) {
//    		Utils.Console("Clicked with " +(androidVersion != null?androidVersion[0]:"null") +": x("+e.getX()+")y("+e.getY()+")");
//    		if (_selected >= 0) {
//	    		ListItem item = (ListItem) getWidget(_selected);
//	    		if (item.isEnabled()) {
//		            SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(_selected,
//		            	e.getNativeEvent().getEventTarget());
//		            this.fireEvent(selectionChangedEvent);
//		            Utils.Console("Firing selection event");
//		        	item.removeStyleName(Secondary.Pressed);
//	    		}
//	    		_selected = -1;
//	        } else {
//	        	Utils.Console("click version was " + (androidVersion != null?androidVersion[0]:"null"));
//	        }
//    	}
//
//    }

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
            int[] androidVersion = Utils.getAndroidVersionValues();
            // for android 5.0
            if (item.isEnabled() && null != androidVersion && androidVersion[0] >= 5) {
                SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(_selected,
                    e.getNativeEvent().getEventTarget());
                this.fireEvent(selectionChangedEvent);
                //Utils.Console("Firing selection event");
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
            } else {
                //Utils.Console("not " + (androidVersion != null?androidVersion[0]:"null"));
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



	@Override
	public void onTouchStart(TouchStartEvent event) {
		int coords[] = getTouchCoords(event.getNativeEvent());
		initialX = coords[0];
		initialY = coords[1];
		_selected = Utils.getTargetItemIndex(getElement(), event.getNativeEvent().getEventTarget());
		Utils.Console("touch start in List");
		moving = false;

//		if (_selected >= 0) {
//			new Timer() {
//				@Override
//				public void run() {
//					if (_selected >= 0) {
//						ListItem item = (ListItem) getWidget(_selected);
//						if (item.isEnabled()) {
//							getWidget(_selected).addStyleName(Secondary.Pressed);
//						}
//					}
//				}
//			}.schedule(75);
//		}
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		if (initialX == 0 && initialY == 0) {
			int coords[] = getTouchCoords(event.getNativeEvent());
			initialX = coords[0];
			initialY = coords[1];
		} else {
			int coords[] = getTouchCoords(event.getNativeEvent());
			int x = coords[0];
			int y = coords[1];
			if (Math.abs(x - initialX) > distance
				|| Math.abs(y - initialY) > distance) {
				moving  = true;
				if (_selected >= 0) {
					new Timer() {
						@Override
						public void run() {
							if (_selected >= 0) {
								ListItem item = (ListItem) getWidget(_selected);
								if (item.isEnabled()) {
									getWidget(_selected).removeStyleName(Secondary.Pressed);
									_selected = -1;
								}
							}
						}
					}.schedule(75);
				}
			}
		}
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		int coords[] = getTouchCoords(event.getNativeEvent());
		int x = coords[0];
		int y = coords[1];
		if (!moving && !touchCancelled
				&& Math.abs(x - initialX) < distance
				&& Math.abs(y - initialY) < distance
				&& _selected >= 0) {
			SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(_selected,
                    event.getNativeEvent().getEventTarget());
                this.fireEvent(selectionChangedEvent);
        		//Utils.Console("Firing touch Change Selection in List ("+Math.abs(x - initialX)+":"+Math.abs(y - initialY)+")");
		}
		if (_selected >= 0) {
			new Timer() {
				@Override
				public void run() {
					if (_selected >= 0) {
						ListItem item = (ListItem) getWidget(_selected);
						if (item.isEnabled()) {
							getWidget(_selected).removeStyleName(Secondary.Pressed);
							_selected = -1;
						}
					}
				}
			}.schedule(75);
		}
		initialX = initialY = 0;
		//Utils.Console("touch end in List");
	}

	@Override
	public void onTouchCancel(TouchCancelEvent event) {
		touchCancelled = true;
		initialX = initialY = 0;
		//Utils.Console("touch cancel in List");
		moving = false;
		if (_selected >= 0) {
			new Timer() {
				@Override
				public void run() {
					if (_selected >= 0) {
						ListItem item = (ListItem) getWidget(_selected);
						if (item.isEnabled()) {
							getWidget(_selected).removeStyleName(Secondary.Pressed);
							_selected = -1;
						}
					}
				}
			}.schedule(75);
		}
	}

	public int[] getTouchCoords(NativeEvent event)
	{
		int rv[] = {0,0};
		rv[0] = event.getClientX();
		rv[1] = event.getClientY();
		if (rv[0] == 0 && rv[1] == 0 && event.getChangedTouches() != null
				&& event.getChangedTouches().length() > 0) {
			for (int i = 0; rv[0] == 0 && rv[1] == 0
					&& i < event.getChangedTouches().length(); i++) {
				rv[0] = event.getChangedTouches().get(i).getClientX();
				rv[1] = event.getChangedTouches().get(i).getClientY();
			}
		}
		return rv;
	}
}
