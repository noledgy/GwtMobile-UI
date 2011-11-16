/*
 * Copyright (c) 2011 Zhihua (Dennis) Jiang
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

package com.gwtmobile.ui.client.panels;

import com.google.gwt.user.client.ui.HasWidgets;
import com.gwtmobile.ui.client.resources.MobileResources.StyleVariants;


public class HorizontalPanel extends FlowPanel implements HasWidgets {

	public enum BoxPack {start, end, center, baseline, stretch, justify};
	private BoxPack boxPack = BoxPack.baseline;
	private StyleVariants styleVariants = StyleVariants.None;
	private boolean withHPadding = false;
	private boolean withVPadding = false;
	private boolean forceFullWidth = false; // in some cases we might need to force width=100%

	public HorizontalPanel() {
		setStyleName("gwtm-HorizontalPanel");
		setBoxPack(boxPack);
		setWithHPadding(withHPadding);
		setWithVPadding(withVPadding);
		setStyleVariants(styleVariants);
		updateUi();
	}
	
	public BoxPack getBoxPack() {
		return boxPack;
	}

	public void setBoxPack(BoxPack boxPack) {
		this.boxPack = boxPack;
		updateUi();
	}
	
	public boolean isWithHPadding() {
		return withHPadding;
	}

	public void setWithHPadding(boolean withHPadding) {
		this.withHPadding = withHPadding;
		updateUi();
	}

	public boolean isWithVPadding() {
		return withVPadding;
	}

	public void setWithVPadding(boolean defaultPadding) {
		this.withVPadding = defaultPadding;
		updateUi();
	}
	
	public boolean isForceFullWidth() {
		return forceFullWidth;
	}

	public void setForceFullWidth(boolean forceFullWidth) {
		this.forceFullWidth = forceFullWidth;
		updateUi();
	}

	public StyleVariants getStyleVariants() {
		return styleVariants;
	}

	public void setStyleVariants(StyleVariants styleVariants) {
		removeStyleName(this.styleVariants.toString());
		this.styleVariants = styleVariants;
		addStyleName(this.styleVariants.toString());
	}

	private void updateUi(){
		getElement().setAttribute("style", "-webkit-box-pack: "+boxPack.toString()+";"+(withVPadding?"padding-left: .5em; padding-right: .5em;":"")+(withHPadding?"padding-top: .5em; padding-bottom: .5em;":"")+(forceFullWidth?";width: 100%":""));
	}
	
}