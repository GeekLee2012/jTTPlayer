package xyz.rive.jttplayer.control;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import xyz.rive.jttplayer.common.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Drag and Move
 */
public final class StageDnmAction {
    private Stage stage;
    private double fromX;
    private double fromY;
    private double fromSceneX;
    private double fromSceneY;

    private boolean enabled = false;
    private boolean propagation = false;
    private boolean alwaysOnTop;
    private boolean dragged;
    private double width = -1;
    private double height = -1;
    private final List<BiConsumer<MouseEvent, Position[]>> beforeMovedHandlers = new ArrayList<>(3);
    private final List<BiConsumer<MouseEvent, Position[]>> onMovingHandlers = new ArrayList<>(3);
    private final List<BiConsumer<MouseEvent, Position>> moveFinishedHandlers = new ArrayList<>(3);

    public StageDnmAction(Stage stage, Node... triggers) {
        if(stage == null || triggers == null) {
            return;
        }
        this.stage = stage;
        addTriggers(triggers).setEnable(true);
    }

    public StageDnmAction setEnable(boolean value) {
        this.enabled = value;
        return this;
    }

    public StageDnmAction setPropagation(boolean value) {
        this.propagation = value;
        return this;
    }

    private StageDnmAction addTriggers(Node... triggers) {
        for (Node trigger : triggers) {
            addTrigger(trigger);
        }
        return this;
    }

    public void addTrigger(Node trigger) {
        if(trigger == null) {
            return ;
        }
        trigger.setOnMousePressed(e -> {
            if (!propagation) {
                e.consume();
            }
            if(!enabled) {
                return ;
            }
            fromX = stage.getX();
            fromY = stage.getY();
            width = stage.getWidth();
            height = stage.getHeight();
            fromSceneX = e.getSceneX();
            fromSceneY = e.getSceneY();
            alwaysOnTop = stage.isAlwaysOnTop();
        });

        trigger.setOnMouseDragged(e -> {
            if (!propagation) {
                e.consume();
            }
            if(!this.enabled) {
                return ;
            }
            if(e.getTarget() != trigger && !(trigger instanceof Label)) {
                return ;
            }
            if(fromSceneX >= (width - 6) || (fromSceneY >= height - 6)) {
                return ;
            }

            dragged = true;

            double toX = e.getScreenX() - fromSceneX;
            double toY = e.getScreenY() - fromSceneY;;

            Position[] positions = {
                    new Position(fromX, fromY),
                    new Position(toX, toY)
            };

            stage.setAlwaysOnTop(true);

            beforeMovedHandlers.forEach(handler -> {
                if (handler != null) {
                    handler.accept(e, positions);
                }
            });

            stage.setX(toX);
            stage.setY(toY);

            onMovingHandlers.forEach(handler -> {
                if (handler != null) {
                    handler.accept(e, positions);
                }
            });

            fromX = toX;
            fromY = toY;
            stage.setAlwaysOnTop(alwaysOnTop);
            //fromSceneX = e.getSceneX();
            //fromSceneY = e.getSceneY();
        });

        trigger.setOnMouseReleased(e -> {
            if (!propagation) {
                e.consume();
            }
            if(dragged) {
                dragged = false;
            }
            moveFinishedHandlers.forEach(handler -> {
                if (handler != null) {
                    handler.accept(e, new Position(fromX, fromY));
                }
            });
        });
    }

    public StageDnmAction beforeMoved(BiConsumer<MouseEvent, Position[]> handler) {
        if (handler != null) {
            beforeMovedHandlers.add(handler);
        }
        return this;
    }

    public StageDnmAction onMoving(BiConsumer<MouseEvent, Position[]> handler) {
        if (handler != null) {
            onMovingHandlers.add(handler);
        }
        return this;
    }

    public StageDnmAction onMoveFinished(BiConsumer<MouseEvent, Position> handler) {
        if (handler != null) {
            moveFinishedHandlers.add(handler);
        }
        return this;
    }


}


