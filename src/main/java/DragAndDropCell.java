/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.stage.Stage;
import javafx.util.Callback;


/**
 * @author fabriceb
 */
public class DragAndDropCell extends TreeCell<String> implements Callback<TreeView<String>, TreeCell<String>>
{
    private TreeView<String> parentTree;
    private String item;

    public DragAndDropCell(final TreeView<String> parentTree)
    {
        this.parentTree = parentTree;
        // ON SOURCE NODE.
        setOnDragDetected(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if (item == null)
                {
                    return;
                }
                Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.put(DataFormat.PLAIN_TEXT, item.toString());
                dragBoard.setContent(content);
                event.consume();
            }
        });
        setOnDragDone(new EventHandler<DragEvent>()
        {
            @Override
            public void handle(DragEvent dragEvent)
            {
                dragEvent.consume();
            }
        });
        // ON TARGET NODE.
        setOnDragOver(new EventHandler<DragEvent>()
        {
            @Override
            public void handle(DragEvent dragEvent)
            {
                if (dragEvent.getDragboard().hasString())
                {
                    String valueToMove = dragEvent.getDragboard().getString();
                    if (!valueToMove.equals(item))
                    {
                        // We accept the transfer!!!!!
                        dragEvent.acceptTransferModes(TransferMode.MOVE);
                    }
                }
                dragEvent.consume();
            }
        });
        setOnDragDropped(new EventHandler<DragEvent>()
        {
            @Override
            public void handle(DragEvent dragEvent)
            {
                String valueToMove = dragEvent.getDragboard().getString();
                TreeItem<String> itemToMove = search(parentTree.getRoot(), valueToMove);
                TreeItem<String> target = search(parentTree.getRoot(), item);

                TreeItem<String> selectedParent = itemToMove.getParent();
                TreeItem<String> targetParent = target.getParent();

                int index = targetParent.getChildren().indexOf(target);

                selectedParent.getChildren().remove(itemToMove);
                targetParent.getChildren().add(index, itemToMove);

                // Remove from former parent.
                // Add to new parent.
                target.setExpanded(true);
                dragEvent.consume();
            }
        });
    }

    private TreeItem<String> search(final TreeItem<String> currentNode, final String valueToSearch)
    {
        TreeItem<String> result = null;
        if (currentNode.getValue().equals(valueToSearch))
        {
            result = currentNode;
        }
        else if (!currentNode.isLeaf())
        {
            for (TreeItem<String> child : currentNode.getChildren())
            {
                result = search(child, valueToSearch);
                if (result != null)
                {
                    break;
                }
            }
        }
        return result;
    }


    @Override
    protected void updateItem(String item, boolean empty)
    {
        super.updateItem(item, empty);
        this.item = item;
        String text = (item == null) ? null : item.toString();
        setText(text);
    }

    public void start(Stage primaryStage)
    {
//        TreeItem<Integer> treeRoot = new TreeItem(0);
//        treeRoot.getChildren().add(new TreeItem(1));
//        treeRoot.getChildren().add(new TreeItem(2));
//        treeRoot.getChildren().add(new TreeItem(3));
//        TreeView<Integer> treeView = TreeViewBuilder.<Integer>create().root(treeRoot).build();
//        treeView.setCellFactory(DragAndDropCell::new);
//        AnchorPane.setTopAnchor(treeView, 0d);
//        AnchorPane.setRightAnchor(treeView, 0d);
//        AnchorPane.setBottomAnchor(treeView, 0d);
//        AnchorPane.setLeftAnchor(treeView, 0d);
//        //
//        AnchorPane root = AnchorPaneBuilder.create().children(treeView).build();
//        Scene scene = new Scene(root, 300, 250);
//        //
//        primaryStage.setTitle("Hello World!");
//        primaryStage.setScene(scene);
//        primaryStage.show();
    }

    @Override
    public TreeCell<String> call(TreeView<String> param)
    {
        return new DragAndDropCell(param);
    }
}