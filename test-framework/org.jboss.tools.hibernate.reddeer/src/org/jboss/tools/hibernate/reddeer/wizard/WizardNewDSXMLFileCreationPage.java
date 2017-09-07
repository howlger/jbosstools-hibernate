/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.reddeer.wizard;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * Datasource wizard page
 * @author Jiri Peterka
 * TODO move to jbosstools.JST
 *
 */
public class WizardNewDSXMLFileCreationPage extends WizardPage {

	public WizardNewDSXMLFileCreationPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Sets connection profile for jboss ds wizard
	 * @param profileName given db profile
	 */
	public void setConnectionProfile(String profileName) {
		new LabeledCombo(referencedComposite, "Connection profile:").setSelection(profileName);
	}
	
	/**
	 * Sets parent folder for jboss ds wizard
	 * @param folder given folder name
	 */
	public void setParentFolder(String folder) {
		new LabeledText(referencedComposite,"Parent folder:").setText(folder);
	}
}
