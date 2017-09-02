/**
 Copyright 2004 Juan Heyns. All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, are
 permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list of
 conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this list
 of conditions and the following disclaimer in the documentation and/or other materials
 provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY JUAN HEYNS ``AS IS'' AND ANY EXPRESS OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JUAN HEYNS OR
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 The views and conclusions contained in the software and documentation are those of the
 authors and should not be interpreted as representing official policies, either expressed
 or implied, of Juan Heyns.
 */
package jdatepicker;

import jdatepicker.graphics.JNextIcon;
import jdatepicker.graphics.JPreviousIcon;
import reabilitation.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public final class ComponentIconDefaults {

    private static ComponentIconDefaults instance;

    public static ComponentIconDefaults getInstance() {
        if (instance == null) {
            instance = new ComponentIconDefaults();
        }
        return instance;
    }

    public enum Key {
        // TODO
    }

    private static String CLEAR = "/jdatepicker/resources/icons/clear.png";

    private Icon clearIcon;
    private Icon nextMonthIconEnabled;
    private Icon nextYearIconEnabled;
    private Icon previousMonthIconEnabled;
    private Icon previousYearIconEnabled;
    private Icon nextMonthIconDisabled;
    private Icon nextYearIconDisabled;
    private Icon previousMonthIconDisabled;
    private Icon previousYearIconDisabled;
    private Icon popupButtonIcon;

    private ComponentIconDefaults() {
        clearIcon = Utils.createImageIcon(CLEAR);
		nextMonthIconEnabled = Utils.createImageIcon("/jdatepicker/resources/icons/increase_horizontal.png");
		nextYearIconEnabled = new JNextIcon(8, 7, true, true);
		previousMonthIconEnabled = Utils.createImageIcon("/jdatepicker/resources/icons/decrease_horizontal.png");
		previousYearIconEnabled = new JPreviousIcon(8, 7, true, true);
		nextMonthIconDisabled = nextMonthIconEnabled;
		nextYearIconDisabled = new JNextIcon(8, 7, true, false);
		previousMonthIconDisabled = previousMonthIconEnabled;
		previousYearIconDisabled = new JPreviousIcon(8, 7, true, false);
		popupButtonIcon = Utils.createImageIcon("/jdatepicker/resources/icons/popup_button.png");
    }

    public Icon getClearIcon() {
        return clearIcon;
    }

    public void setClearIcon(Icon clearIcon) {
        this.clearIcon = clearIcon;
    }

    public Icon getNextMonthIconEnabled() {
        return nextMonthIconEnabled;
    }

    public void setNextMonthIconEnabled(Icon nextMonthIconEnabled) {
        this.nextMonthIconEnabled = nextMonthIconEnabled;
    }

    public Icon getNextMonthIconDisabled() {
        return nextMonthIconDisabled;
    }

    public void setNextMonthIconDisabled(Icon nextMonthIconDisabled) {
        this.nextMonthIconDisabled = nextMonthIconDisabled;
    }

    public Icon getNextYearIconEnabled() {
        return nextYearIconEnabled;
    }

    public void setNextYearIconEnabled(Icon nextYearIconEnabled) {
        this.nextYearIconEnabled = nextYearIconEnabled;
    }

    public Icon getNextYearIconDisabled() {
        return nextYearIconDisabled;
    }

    public void setNextYearIconDisabled(Icon nextYearIconDisabled) {
        this.nextYearIconDisabled = nextYearIconDisabled;
    }

    public Icon getPreviousMonthIconEnabled() {
        return previousMonthIconEnabled;
    }

    public void setPreviousMonthIconEnabled(Icon previousMonthIconEnabled) {
        this.previousMonthIconEnabled = previousMonthIconEnabled;
    }

    public Icon getPreviousMonthIconDisabled() {
        return previousMonthIconDisabled;
    }

    public void setPreviousMonthIconDisabled(Icon previousMonthIconDisabled) {
        this.previousMonthIconDisabled = previousMonthIconDisabled;
    }

    public Icon getPreviousYearIconEnabled() {
        return previousYearIconEnabled;
    }

    public void setPreviousYearIconEnabled(Icon previousYearIconEnabled) {
        this.previousYearIconEnabled = previousYearIconEnabled;
    }

    public Icon getPreviousYearIconDisabled() {
        return previousYearIconDisabled;
    }

    public void setPreviousYearIconDisabled(Icon previousYearIconDisabled) {
        this.previousYearIconDisabled = previousYearIconDisabled;
    }

    public Icon getPopupButtonIcon() {
        return popupButtonIcon;
    }

    public void setPopupButtonIcon(Icon popupButtonIcon) {
        this.popupButtonIcon = popupButtonIcon;
    }

}
