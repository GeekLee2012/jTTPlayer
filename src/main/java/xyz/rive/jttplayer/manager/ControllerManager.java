package xyz.rive.jttplayer.manager;

import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.*;
import xyz.rive.jttplayer.controller.*;
import xyz.rive.jttplayer.skin.SkinXml;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ControllerManager extends AbstractManager {
    private List<CommonController> controllers;

    public ControllerManager(ApplicationContext context) {
        super(context);
    }

    private List<CommonController> getControllers() {
        if(controllers == null) {
            controllers = new CopyOnWriteArrayList<>();
        }
        return controllers;
    }

    private void addController(CommonController controller) {
        getControllers().add(controller);
    }

    private void removeController(CommonController controller) {
        getControllers().remove(controller);
    }

    public void registerController(CommonController controller) {
        for (CommonController c : getControllers()) {
            if(controller.getClass() == c.getClass()) {
                removeController(c);
            }
        }
        addController(controller);
    }

    @SuppressWarnings("unchecked")
    public <T extends CommonController> T getController(Class<T> clazz) {
        for (CommonController controller : getControllers()) {
            if(controller.getClass() == clazz) {
                return (T) controller;
            }
        }
        return null;
    }

    public void setLyricSeekOnPause(int seconds) {
        Optional.ofNullable(getController(LyricController.class))
                .ifPresent(controller -> controller.seekOnPaused(seconds));
    }

    public void adjustLyricStyle() {
        Optional.ofNullable(getController(LyricController.class))
                .ifPresent(LyricController::adjustLyricStyle);
    }

    public void adjustLyricDesktopStyle() {
        Optional.ofNullable(getController(LyricDesktopController.class))
                .ifPresent(LyricDesktopController::adjustLyricDesktopStyle);
    }

    public void setLyricOffset(long value) {
        Optional.ofNullable(getController(LyricController.class))
                .ifPresent(controller -> controller.setLyricOffset(value));
    }

    public void refreshLyricView() {
        Optional.ofNullable(getController(LyricController.class))
                .ifPresent(LyricController::refreshView);
    }

    public void refreshPlaybackQueue() {
        Optional.ofNullable(getController(PlaybackQueueController.class))
                .ifPresent(PlaybackQueueController::refresh);
    }

    public void updatePlaybackQueueNames() {
        Optional.ofNullable(getController(PlaybackQueueController.class))
                .ifPresent(PlaybackQueueController::updatePlaybackQueueNames);
    }

    public void updateTrackMetadata() {
        Optional.ofNullable(getController(MainController.class))
                .ifPresent(MainController::updateTrackMetadata);
    }

    public void updateMiniModeTrackMetadata() {
        Optional.ofNullable(getController(MainMiniModeController.class))
                .ifPresent(MainMiniModeController::updateTrackMetadata);
    }

    public void refreshStagesTrackMetadata(Track track) {
        if(track == null) {
            return ;
        }
        if(track.equals(getContext().getPlayerManager().getCurrentTrack())) {
            updateTrackMetadata();
            updateMiniModeTrackMetadata();
        }
        refreshPlaybackQueue();
    }

    public void renamePlaybackQueue(PlaybackQueue queue) {
        RenamePlaybackQueueController rpqController = getController(RenamePlaybackQueueController.class);
        if(rpqController == null) {
            return ;
        }
        rpqController.setupData(queue.getName(), name -> {
            queue.setName(name);
            getContext().getPlayerManager().sortPlaybackQueues();
            Optional.ofNullable(getController(PlaybackQueueController.class))
                    .ifPresent(PlaybackQueueController::updatePlaybackQueueNames);
        });
    }

    public void refreshOnMiniMode(boolean isMiniMode) {
        if(isMiniMode) {
            Optional.ofNullable(getController(MainMiniModeController.class))
                    .ifPresent(MainMiniModeController::refresh);
        } else {
            Optional.ofNullable(getController(MainController.class))
                    .ifPresent(controller -> {
                        controller.setupSkin();
                        controller.refresh();
                    });
        }
    }

    public void setupAlert(String text) {
        Optional.ofNullable(getController(AlertController.class))
                .ifPresent(controller -> controller.setContentText(text));
    }

    public void setupConfirm(String text, Runnable okAction) {
        Optional.ofNullable(getController(ConfirmController.class))
                .ifPresent(controller -> {
                    controller.setupConfirm(text, okAction);
                });
    }

    public void updatePlaybackQueueSelections() {
        Optional.ofNullable(getController(PlaybackQueueSelectionController.class))
                .ifPresent(PlaybackQueueSelectionController::loadContent);
    }

    public void updateLyricOntopState(boolean isLyricDesktopMode) {
        if(isLyricDesktopMode) {
            Optional.ofNullable(getController(LyricDesktopController.class))
                    .ifPresent(LyricDesktopController::updateOntopState);
        } else {
            Optional.ofNullable(getController(LyricController.class))
                    .ifPresent(LyricController::updateOntopState);
        }
    }

    public <T extends CommonController> void onStageShown(Class<T> clazz) {
        Optional.ofNullable(getController(clazz))
                .ifPresent(CommonController::onShown);
    }

    public void updateFilename(String filename, Consumer<String> handler) {
        Optional.ofNullable(getController(FileNameFormatController.class))
                .ifPresent(controller -> {
                    controller.updateFilename(filename);
                    controller.onSelected(handler);
                });
    }

    public void updateTagMetadata(Track track, boolean edit, Pair data, Consumer<Pair> okAction) {
        Optional.ofNullable(getController(FileTagEditController.class))
                .ifPresent(controller -> {
                    controller.setOkAction(okAction);
                    if(edit) {
                        controller.loadContent(data);
                    }
                });
    }

    public void setupCallback(Class<? extends CommonController> controllerClass, Consumer<? super CommonController> callback) {
        Optional.ofNullable(getController(controllerClass))
                .ifPresent(callback);
    }

    public void searchPlaybackQueue(FileSearchOptions options, boolean next) {
        Optional.ofNullable(getController(PlaybackQueueController.class))
                .ifPresent(controller -> {
                    controller.search(options, next);
                });
    }

    public void markupPlaybackQueue(FileSearchOptions options) {
        Optional.ofNullable(getController(PlaybackQueueController.class))
                .ifPresent(controller -> controller.markupAll(options));
    }

    public void markupAllPlaybackQueue(String keyword) {
        Optional.ofNullable(getController(PlaybackQueueController.class))
                .ifPresent(controller -> controller.markupAll(keyword));
    }

    public void setupFont(String family, String weight, int size, int defaultSize, Consumer<FontOption> action) {
        Optional.ofNullable(getController(FontSelectionController.class))
                .ifPresent(controller -> {
                    controller.setupFont(family, weight, size, defaultSize);
                    controller.setOkAction(action);
                });
    }

    public void setupCopyTrackSelections(boolean moving) {
        PlaybackQueueSelectionController pqsController = getController(PlaybackQueueSelectionController.class);
        if (pqsController != null) {
            pqsController.setOkAction(queue -> {
                Optional.ofNullable(getController(PlaybackQueueController.class))
                        .ifPresent(controller ->
                                controller.copyTrackSelections(queue, moving)
                        );

            });
        }
    }

    public void pasteTrackSelections() {
        Optional.ofNullable(getController(PlaybackQueueController.class))
                .ifPresent(PlaybackQueueController::pasteTrackSelections);
    }

    public void setupPlaybackQueueCopying(boolean moving) {
        Optional.ofNullable(getController(PlaybackQueueController.class))
                .ifPresent(controller -> {
                    if (moving) {
                        controller.setMoving(true);
                    } else {
                        controller.setCopying(true);
                    }
                });
    }

    public void setPreferenceActiveNavItem(String name) {
        setPreferenceActiveNavItem(name, 0);
    }

    public void setPreferenceActiveNavItem(String name, int tabIndex) {
        Optional.ofNullable(getController(PreferenceController.class))
                .ifPresent(controller -> controller.setActiveNavItem(name, tabIndex));
    }

    public void searchFile(boolean next) {
        Optional.ofNullable(getController(FileSearchController.class))
                .ifPresent(controller -> controller.search(next));
    }

    public void startLyric() {
        Optional.ofNullable(getController(LyricController.class))
                .ifPresent(LyricController::startLyricNow);
    }

    public void reloadLyric() {
        Optional.ofNullable(getController(LyricController.class))
                .ifPresent(controller -> controller.startLyric(true));
    }

    public void switchPlaybackQueue(PlaybackQueue queue) {
        PlaybackQueueController controller = getController(PlaybackQueueController.class);
        if(controller != null) {
            int index = getContext().getPlayerManager().getPlaybackQueues().indexOf(queue);
            controller.switchToPlaybackQueue(index, queue);
        }
    }

    public void switchZhLyric() {
        Optional.ofNullable(getController(LyricController.class))
                .ifPresent(LyricController::switchZhLyric);
        Optional.ofNullable(getController(LyricDesktopController.class))
                .ifPresent(LyricDesktopController::switchZhLyric);
    }

    public void cancelLyric() {
        Optional.ofNullable(getController(LyricController.class))
                .ifPresent(LyricController::cancelLyric);
    }

    public void setLyricDesktopTextGradient(String styleClass) {
        Optional.ofNullable(getController(LyricDesktopController.class))
                .ifPresent(controller -> controller.setTextGradient(styleClass));
    }

    public void locateCurrentTrack() {
        Optional.ofNullable(getController(PlaybackQueueController.class))
                .ifPresent(PlaybackQueueController::locateCurrentTrack);
    }

    public void setLyricDesktopToolbarVisible(boolean visible) {
        Optional.ofNullable(getController(LyricDesktopController.class))
                .ifPresent(controller -> {
                    if(visible) {
                        controller.showToolbar(null);
                    } else {
                        controller.hideToolbar(null);
                    }
                });

    }

    public void setActiveTab(Class<? extends CommonController> clazz, int tabIndex) {
        Optional.ofNullable(getController(clazz))
                .ifPresent(controller -> controller.setActiveTab(tabIndex));
    }

    public void refreshSkin() {
        Optional.ofNullable(getController(MainController.class))
                .ifPresent(CommonController::setupSkin);
        Optional.ofNullable(getController(MainMiniModeController.class))
                .ifPresent(CommonController::setupSkin);
        Optional.ofNullable(getController(PlaybackQueueController.class))
                .ifPresent(CommonController::setupSkin);
        Optional.ofNullable(getController(LyricController.class))
                .ifPresent(CommonController::setupSkin);
        Optional.ofNullable(getController(EqualizerController.class))
                .ifPresent(CommonController::setupSkin);
        Optional.ofNullable(getController(LyricDesktopController.class))
                .ifPresent(CommonController::setupSkin);
    }

    public void setupPreviewSkin(SkinXml skin) {
        Optional.ofNullable(getController(MainPreviewController.class))
                .ifPresent(controller -> controller.setupPreviewSkin(skin));
    }

    public void updateLyricServer(boolean edit, Server data, Consumer<Server> okAction) {
        Optional.ofNullable(getController(LyricServerEditController.class))
                .ifPresent(controller -> {
                    controller.setOkAction(okAction);
                    if(edit) {
                        controller.loadContent(data);
                    }
                });
    }

    public void createLyricServer(Consumer<Server> okAction) {
        updateLyricServer(false, null, okAction);
    }

}
