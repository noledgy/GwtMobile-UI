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

import java.beans.Beans;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.CSS.StyleNames.Primary;

public class HeaderPanel extends PanelBase {

  SimplePanel  leftButton;
  FlowPanel    contents;
  SimplePanel  rightButton;
	ClickHandler _leftButtonClickHandler;
	ClickHandler _rightButtonClickHandler;

    public HeaderPanel() {
      leftButton  = new SimplePanel();
      contents    = new FlowPanel();
      rightButton = new SimplePanel();

      leftButton.setHeight("100%");
      rightButton.setHeight("100%");

    	super.add(leftButton);	//left button placeholder
    	super.add(new SimplePanel()); // left spacer
    	super.add(contents);		//contents
      super.add(new SimplePanel()); // right spacer
    	super.add(rightButton);	//right button placeholder
        setStyleName(Primary.HeaderPanel);
        if (Beans.isDesignTime()) {
            add(new Label("Empty HeaderPanel. " + getDesignTimeMessage()));
        }
    }

    @Override
    protected String getDesignTimeMessage() {
    	return "Set caption and left/right button properties.";
    }

    @Override
    public void add(Widget w) {
    	if (Beans.isDesignTime() && contents.getWidgetCount() > 0) {
    		Widget widget = contents.getWidget(0);
    		if (widget instanceof Label && ((Label)widget).getText().contains(getDesignTimeMessage())) {
    			contents.clear();
    		}
    	}
    	contents.add(w);
    }

    public void setCaption(String caption) {
    	if (!caption.isEmpty()) {
        	contents.clear();
    		contents.add(new HTML(caption));
    	}
    }

    public String getCaption() {
    	if (contents.getWidgetCount() > 0) {
        	HTML w = (HTML) contents.getWidget(0);
        	return w.getHTML();
    	}
    	return "";
    }

    public void setLeftButton(String buttonName) {
    	if (!buttonName.isEmpty()) {
        ClickHandler clickHandler = new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
              event.stopPropagation();
              onLeftButtonClick(event);
          }
  		};
    	if (buttonName.toUpperCase().equals("BACK")) {
    		leftButton.setWidget(new BackButton(buttonName, clickHandler));
    	}
    	else {
    	  leftButton.setWidget(new HeaderButton(buttonName, clickHandler));
    	}
        rightButton.addDomHandler(clickHandler, ClickEvent.getType());
    	}
    }

    /**
     * Sets the back button.
     *
     * Frank Mena: This is needed because the method setLeftButton() method
     * only allows setting the text "BACK" to create a left arrow button.
     * If you want to add a back button with the text "Cancel", the method
     * above will not allow it.
     *
     * @param buttonName the new back button
     */
    public void setBackButton(String buttonName) {
      if (!buttonName.isEmpty()) {
        ClickHandler clickHandler = new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            event.stopPropagation();
            onLeftButtonClick(event);
          }
        };
      leftButton.setWidget(new BackButton(buttonName, clickHandler));
      leftButton.addDomHandler(clickHandler, ClickEvent.getType());
      }
    }

    public void setRightButton(String buttonName) {
    	if (!buttonName.isEmpty()) {
          ClickHandler clickHandler = new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            event.stopPropagation();
            onRightButtonClick(event);
          }
    		};
        	if (buttonName.toUpperCase().equals("NEXT")) {
        		rightButton.setWidget(new NextButton(buttonName, clickHandler));
        	}
        	else {
        		rightButton.setWidget(new HeaderButton(buttonName, clickHandler));
        	}
        	rightButton.addDomHandler(clickHandler, ClickEvent.getType());
    	}
    }

    public Button getLeftButton() {
    	return (Button) leftButton.getWidget();
    }

    public Button getRightButton() {
    	return (Button) rightButton.getWidget();
    }

    void onLeftButtonClick(ClickEvent event) {
    	if (_leftButtonClickHandler != null) {
        	_leftButtonClickHandler.onClick(event);
    	}
    	else {
    		Button leftButton = getLeftButton();
    		if (leftButton != null && leftButton.getClass() == BackButton.class) {
    			((BackButton)leftButton).onClick(event);
    		}
    	}
    }

    void onRightButtonClick(ClickEvent event) {
    	if (_rightButtonClickHandler != null) {
        	_rightButtonClickHandler.onClick(event);
    	}
    }

    public void setLeftButtonClickHandler(ClickHandler handler) {
    	_leftButtonClickHandler = handler;
    }

    public void setRightButtonClickHandler(ClickHandler handler) {
    	_rightButtonClickHandler = handler;
    }

}
