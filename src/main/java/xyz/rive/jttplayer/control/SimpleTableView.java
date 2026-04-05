package xyz.rive.jttplayer.control;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import xyz.rive.jttplayer.common.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.util.FxUtils.getCssUrl;

public class SimpleTableView extends ScrollPane {
    private VBox container;
    private HBox headerBox;
    private Consumer<MouseEvent> dblclickHandler;

    public SimpleTableView() {
        init();
    }

    private void init() {
        getStylesheets().setAll(getCssUrl("simple-table-view"));
        getStyleClass().setAll("simple_table_view");

        container = new VBox();
        container.getStyleClass().setAll("container");

        setContent(container);
        setFitToHeight(true);
        setFitToWidth(true);

        headerBox = new HBox();
        headerBox.getStyleClass().setAll("header");
        container.getChildren().add(headerBox);

        setOnMouseClicked(event -> {
            setupActiveRow(null);
        });
    }

    public ObservableList<Node> getHeader() {
        return headerBox.getChildren();
    }

    public void addRow(Node... children) {
        HBox row = new HBox(children);
        row.getStyleClass().add("row");
        container.getChildren().add(row);

        for (int i = 0; i < children.length; i++) {
            Region item = (Region)children[i];
            item.getStyleClass().setAll("row_cell");
            Region headerItem = (Region)getHeader().get(i);
            item.prefWidthProperty().bind(headerItem.widthProperty());
        }

        row.getChildren().forEach(cell -> {
            cell.setOnMouseClicked(event -> {
                event.consume();
                cell.requestFocus();
                setupActiveRow(row);

                if(event.getClickCount() < 2) {
                    return ;
                }
                if(cell instanceof TextField) {
                    return ;
                }
                Optional.ofNullable(dblclickHandler)
                        .ifPresent(handler -> handler.accept(event));
                /*
                row.lookupAll("TextField").forEach(node -> {
                    TextField text = (TextField) node;
                    text.requestFocus();
                    text.selectAll();
                });
                */
            });
        });
    }

    public void clear() {
        int size = container.getChildren().size();
        container.getChildren().remove(1, size);
    }

    private void setupActiveRow(Node row) {
        container.getChildren().forEach(item -> {
            item.getStyleClass().removeAll("active");
        });
        if(row != null) {
            row.getStyleClass().add("active");
        }
    }

    public void onDoubleClick(Consumer<MouseEvent> handler) {
        dblclickHandler = handler;
    }

    public List<String[]> getData() {
        int size = container.getChildren().size();
        List<Node> content = container.getChildren().subList(1, size);
        List<String[]> data = new ArrayList<>();
        for(int i = 0; i < content.size(); i++) {
            Parent parent = (Parent) content.get(i);
            List<Node> cells = parent.getChildrenUnmodifiable();
            String[] cellValues = new String[cells.size()];
            data.add(cellValues);
            for (int j = 0; j < cells.size(); j++) {
                Node cell = cells.get(j);
                if(cell instanceof Label) {
                    Label label = (Label) cell;
                    cellValues[j] = label.getText();
                } else if(cell instanceof TextField) {
                    TextField textField = (TextField) cell;
                    cellValues[j] = textField.getText();
                }
            }
        }
        return data;
    }

}
