//========================================================================
//
//                       U N C L A S S I F I E D
//
//========================================================================
//  Copyright (c) 2014 Chesapeake Technology International Corp.
//  ALL RIGHTS RESERVED
//  This material may be reproduced by or for the U.S. Government
//  pursuant to the copyright license under the clause at
//  DFARS 252.227-7013 (OCT 1988).
//========================================================================
//  SBIR Data Rights Statement
//  Contract Number: N68335-15-C-0039
//
//	Expiration of SBIR Data Rights Period:
//     5 years after completion of final contract modification
//
//	The Government's rights to use, modify, reproduce, release, perform,
//  display, or disclose technical data or computer software marked with
//  this legend are restricted during the period shown as provided in
//  paragraph (b)(4) of the Rights in Noncommercial Technical Data and
//  Computer Software--Small Business Innovative Research (SBIR) Program
//  clause contained in the above identified contract. No restrictions
//  apply after the expiration date shown above. Any reproduction of
//  technical data, computer software, or portions thereof marked with
//  this legend must also reproduce the markings.
//========================================================================
//  International Traffic in Arms Regulations (ITAR) Restriction Statement
//
//  RESTRICTION ON PERFORMANCE BY FOREIGN CITIZENS (i.e., those holding
//  non-U.S. Passports): This topic is "ITAR Restricted." The information
//  and materials provided pursuant to or resulting from this topic are
//  restricted under the International Traffic in Arms Regulations (ITAR),
//  22 CFR Parts 120 - 130, which control the export of defense-related
//  material and services, including the export of sensitive technical
//  data. Foreign Citizens may perform work under an award resulting from
//  this topic only if they hold the Permanent Resident Card, or are
//  designated as Protected Individuals as defined by
//  8 U.S.C. 1324b(a)(3). If a proposal for this topic contains
//  participation by a foreign citizen who is not in one of the above two
//  categories, the proposal will be rejected.
//=======================================================================

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class TabInformationDialogController
{
    private Collection<ITabConfigurationListener> tabConfigurationListeners;

    private String name;
    private Double normalizationOffset;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField normalizationOffsetTextField;

    public TabInformationDialogController()
    {
        tabConfigurationListeners = new ArrayList<>();
    }

    public void addTabConfigurationListener(ITabConfigurationListener tabConfigurationListener)
    {
        tabConfigurationListeners.add(tabConfigurationListener);
    }

    public void removeTabConfigurationListener(ITabConfigurationListener tabConfigurationListener)
    {
        tabConfigurationListeners.remove(tabConfigurationListener);
    }

    @FXML
    private void okButtonAction()
    {
        name = nameTextField.getText();

        try
        {
            normalizationOffset = Double.parseDouble(normalizationOffsetTextField.getText());
        }
        catch (Exception exception)
        {
            normalizationOffset = 0.0;
        }

        ((Stage) nameTextField.getScene().getWindow()).close();

        tabConfigurationListeners.forEach(tabConfigurationListener -> tabConfigurationListener.createTab(name, normalizationOffset));
    }

    @FXML
    private void cancelButtonAction()
    {
        ((Stage) nameTextField.getScene().getWindow()).close();
    }
}
