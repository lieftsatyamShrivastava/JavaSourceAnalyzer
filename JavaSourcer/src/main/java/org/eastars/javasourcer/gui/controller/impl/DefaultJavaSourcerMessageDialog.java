package org.eastars.javasourcer.gui.controller.impl;

import static org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle.TITLE;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.eastars.javasourcer.gui.controller.JavaSourcerMessageDialog;
import org.eastars.javasourcer.gui.service.ApplicationGuiService;
import org.springframework.context.MessageSource;

@org.springframework.stereotype.Component("messagedialog")
public class DefaultJavaSourcerMessageDialog extends AbstractInternationalizableController implements JavaSourcerMessageDialog {

	public DefaultJavaSourcerMessageDialog(
			MessageSource messageSource,
			ApplicationGuiService applicationGuiService) {
		super(messageSource, applicationGuiService.getLocale());
	}

	@Override
	public boolean showMessage(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, getResourceBundle(TITLE), JOptionPane.ERROR_MESSAGE);
		return true;
	}

}
