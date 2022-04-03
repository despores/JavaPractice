package com.ersakhabutdinov.jigsaw;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class JigsawController {

    @FXML
    private AnchorPane jigsawAnchorPane;

    @FXML
    private Button endGameButton;

    @FXML
    private GridPane tetrisGridPane;

    private Group rects;

    private int turnsCount;
    private long startTime;

    private final String paneColor = "-fx-background-color: blue";
    private final String occupiedColor = "-fx-background-color: #1f1e33";
    private final String preOccupiedColor = "-fx-background-color: green";

    @FXML
    void initialize() {
        tetrisGridPane.setPrefSize(270, 270);
        tetrisGridPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                createPane(i, j);
            }
        }
        createRectsGroup();
        tetrisGridPane.setGridLinesVisible(true);
        startTime = System.currentTimeMillis();
    }

    @FXML
    void endGameButtonAction(ActionEvent event) {
        ButtonType nextGame = new ButtonType("Новая игра");
        ButtonType exit = new ButtonType("Выйти", ButtonBar.ButtonData.CANCEL_CLOSE);

        long endTime = System.currentTimeMillis();
        double timeInSeconds = (double) (endTime - startTime) / 1000.0;

        Alert newGameAlert = new Alert(Alert.AlertType.NONE,
                "Количество ходов: " + turnsCount + "\nВремени прошло: " + timeInSeconds + " секунд",
                nextGame, exit);
        ButtonType result = newGameAlert.showAndWait().orElse(exit);

        if (nextGame.equals(result)) {
            startNewGame();
        } else {
            Platform.exit();
        }
        newGameAlert.setTitle("Игра закончилась!");

        //newGameAlert.show();
    }

    void createPane(int x, int y) {
        Pane pane = new Pane();
        pane.setStyle(paneColor);
        pane.setId(x + " " + y);

        pane.setOnDragOver(event -> {
            /* data is dragged over the target */
            System.out.println("onDragOver");

            /* accept it only if it is  not dragged from the same node
             * and if it has a string data */
            if (event.getGestureSource() != pane &&
                    event.getDragboard().hasString()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        pane.setOnDragEntered(event -> {
            /* the drag-and-drop gesture entered the target */
            System.out.println("onDragEntered");
            /* show to the user that it is an actual gesture target */
            if (event.getGestureSource() != pane &&
                    event.getDragboard().hasString()) {
                System.out.println(pane.getId());
                //String[] paneCoords = pane.getId().split(" ");
                String figureStyle = event.getDragboard().getString();
                fillGridWithColor(preOccupiedColor, figureStyle, pane);
            }

            event.consume();
        });

        pane.setOnDragExited(event -> {
            /* mouse moved away, remove the graphical cues */
            if (event.getGestureSource() != pane &&
                    event.getDragboard().hasString()) {
                //String[] paneCoords = pane.getId().split(" ");
                String figureStyle = event.getDragboard().getString();
                fillGridWithColor(paneColor, figureStyle, pane);
            }
            event.consume();
        });

        pane.setOnDragDropped(event -> {
            /* data dropped */
            System.out.println("onDragDropped");
            /* if there is a string data on dragboard, read it and use it */
            if (event.getGestureSource() != pane &&
                    event.getDragboard().hasString()) {
                //String[] paneCoords = pane.getId().split(" ");
                String figureStyle = event.getDragboard().getString();
                event.setDropCompleted(fillGridWithColor(occupiedColor, figureStyle, pane));
            }
            event.consume();
        });
        tetrisGridPane.add(pane, x, y);
    }

    void createRectsGroup() {
        int rand = (int) (Math.random() * 31);
        rects = new Group();
        String figureStyle = JigsawFigures.figures[rand];
        String[] figureCoords = figureStyle.split(", ");
        for (String coords: figureCoords) {
            String[] stringCoords = coords.split(" ");
            int x = Integer.parseInt(stringCoords[0]);
            int y = Integer.parseInt(stringCoords[1]);

            Rectangle rect = new Rectangle();
            rect.setFill(Color.rgb(31, 30, 51));
            rect.setX(400 + 30 * x);
            rect.setY(200 + 30 * y);
            rect.setHeight(30);
            rect.setWidth(30);
            rect.setOnDragDetected(event -> {
                /* drag was detected, start drag-and-drop gesture*/
                System.out.println("onDragDetected");

                /* allow any transfer mode */
                Dragboard db = rect.startDragAndDrop(TransferMode.COPY_OR_MOVE);

                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(figureStyle);
                db.setContent(content);

                event.consume();
            });

            rect.setOnDragDone(event -> {
                /* the drag-and-drop gesture ended */
                System.out.println("onDragDone");
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    turnsCount++;
                    jigsawAnchorPane.getChildren().remove(rects);
                    createRectsGroup();
                }

                event.consume();
            });
            rects.getChildren().add(rect);
        }
        jigsawAnchorPane.getChildren().add(rects);
    }

    boolean fillGridWithColor(String colorStyle, String figureStyle, Pane pane) {
        String[] paneCoords = pane.getId().split(" ");
        String[] figureCoords = figureStyle.split(", ");
        int paneX = Integer.parseInt(paneCoords[0]);
        int paneY = Integer.parseInt(paneCoords[1]);
        int[] panesToColor = new int[figureCoords.length];

        for(int i = 0; i < figureCoords.length; ++i) {
            String[] stringCoords = figureCoords[i].split(" ");
            int x = Integer.parseInt(stringCoords[0]) + paneX;
            int y = Integer.parseInt(stringCoords[1]) + paneY;
            if (x < 0 || x > 8 || y < 0 || y > 8 || tetrisGridPane.getChildren().get(x * 9 + y).getStyle().equals(occupiedColor)) {
                return false;
            }
            panesToColor[i] = x * 9 + y;
        }

        for (int coords : panesToColor) {
            tetrisGridPane.getChildren().get(coords).setStyle(colorStyle);
        }
        return true;
    }

    void startNewGame() {
        jigsawAnchorPane.getChildren().remove(rects);
        turnsCount = 0;
        for (int i = 0; i < tetrisGridPane.getChildren().size(); ++i) {
            tetrisGridPane.getChildren().get(i).setStyle(paneColor);
        }
        startTime = System.currentTimeMillis();
        createRectsGroup();
    }
}


