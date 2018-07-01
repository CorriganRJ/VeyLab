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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class LabApplicationController implements Initializable, ITabConfigurationListener
{
    private Map<String, List<File>> tabDataSetMap;

    private Map<String, Double> tabNormalizationOffsetMap;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private TextField fileNameTextField;

    private ContextMenu contextMenu = new ContextMenu();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        tabDataSetMap = new HashMap<>();

        treeView.setCellFactory(new DragAndDropCell(treeView));
        treeView.setRoot(new TreeItem<>("Tabs"));
        treeView.setShowRoot(false);

        tabNormalizationOffsetMap = new HashMap<>();

        treeView.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (e.getButton() == MouseButton.SECONDARY)
            {
                TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();

                //item is selected - this prevents fail when clicking on empty space
                if (selected != null)
                {
                    //open context menu on current screen position
                    openContextMenu(selected, e.getScreenX(), e.getScreenY());
                }
            } else
            {
                //any other click cause hiding menu
                contextMenu.hide();
            }
        });
    }

    @FXML
    private void addTabAction()
    {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("tabInformationDialog.fxml"));
        TabInformationDialogController tabInformationDialogController = new TabInformationDialogController();
        Parent dialogRoot = null;

        tabInformationDialogController.addTabConfigurationListener(this);
        loader.setController(tabInformationDialogController);

        try
        {
            dialogRoot = loader.load();
            Scene scene = new Scene(dialogRoot);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(treeView.getScene().getWindow());
            stage.show();

        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @FXML
    private void saveAction()
    {
        //TODO: Sort tabDataSetMap order according to tree view order

        String fileName = fileNameTextField.getText();

        if (!fileName.endsWith(".xlsx"))
        {
            fileName = fileName + ".xlsx";
        }

        ExcelFileWriter.createFile(fileName, tabDataSetMap, tabNormalizationOffsetMap);
    }

    @Override
    public void createTab(String tabName, double normalizationOffset)
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select Data Files");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text Files", ".txt"));

        List<File> files = fileChooser.showOpenMultipleDialog(treeView.getScene().getWindow());

        TreeItem<String> tabItem = new TreeItem<>(tabName);

        if (!files.isEmpty())
        {
            tabDataSetMap.put(tabName, new ArrayList<>(files));
            treeView.getRoot().getChildren().add(tabItem);

            for (File file : files)
            {
                TreeItem<String> fileItem = new TreeItem<>(file.getName());

                tabItem.getChildren().add(fileItem);
            }
        }

        tabNormalizationOffsetMap.put(tabName, normalizationOffset);
    }

    private void openContextMenu(TreeItem<String> item, double x, double y)
    {
        contextMenu.getItems().removeAll();

        if (item.isLeaf())
        {
            MenuItem menuItem = new MenuItem("Remove");
            menuItem.setOnAction(event -> {
                String tabName = item.getParent().getValue();

                item.getParent().getChildren().remove(item);
                tabDataSetMap.get(tabName).removeIf(file -> file.getName().equals(item.getValue()));
            });

            contextMenu.getItems().add(menuItem);
        }
        //show menu
        contextMenu.show(treeView, x, y);
    }
}
