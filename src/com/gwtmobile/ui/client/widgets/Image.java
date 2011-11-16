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

import com.gwtmobile.ui.client.resources.MobileResources;
import com.gwtmobile.ui.client.resources.MobileResources.IconImages;


public class Image extends com.google.gwt.user.client.ui.Image 
	implements IsGwtMobileWidget {

	private IconImages iconImages = IconImages.None;


	private IsGwtMobileWidgetHelper _widgetHelper = new IsGwtMobileWidgetHelper();

	public Image() {
		super();
		setStyleName("gwtm-Image");
	}
	
    public IconImages getIconImages() {
		return iconImages;
	}

	public void setIconImages(IconImages iconImages) {
		this.iconImages = iconImages;
		if (iconImages == IconImages.Custom){
			// todo
		} else {
			setUrl(MobileResources.IMAGE_MAP.get(iconImages.toString()).getSafeUri());
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		_widgetHelper.CheckInitialLoad(this);
	}

	@Override
	public void onInitialLoad() {
	}

	@Override
	public void onTransitionEnd() {
	}

	@Override
	public void setSecondaryStyle(String style) {
		_widgetHelper.setSecondaryStyle(this, style);
	}

}