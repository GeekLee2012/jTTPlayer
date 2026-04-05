package xyz.rive.jttplayer.control;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import static xyz.rive.jttplayer.util.FileUtils.toExternalForm;

/**
 * Drag and Drop
 */
@SuppressWarnings("unchecked")
public final class DndAction {
	private boolean enabled = true;
	
	public DndAction(Consumer<DndContext> action, Node... triggers) {
		Optional.ofNullable(triggers).ifPresent(__ -> {
			setupTrigger(action, triggers);
		});
	}
	
	public DndAction enable(boolean value) {
		this.enabled = value;
		return this;
	}

	private void setupTrigger(Consumer<DndContext> action, Node... triggers) {
		if(triggers == null || triggers.length < 1) {
			return ;
		}
		for (Node trigger : triggers) {
			trigger.setOnDragOver(event -> {
				event.consume();
				if(this.enabled) {
					event.acceptTransferModes(TransferMode.COPY);
				}
			});

			trigger.setOnDragDropped(event -> {
				event.consume();
				if(!this.enabled) {
					return ;
				}
				Optional.ofNullable(action).ifPresent(__ -> {
					action.accept(new DndContext(event.getDragboard()));
				});
			});
		}
	}
	
	public static class DndContext {
		private Dragboard board;
		private final BooleanProperty successProperty;
		private DndType dndType;
		private Object userData;
		
		public DndContext() {
			successProperty = new SimpleBooleanProperty(true);
			setDndType(DndType.UNKNOWN);
		}
		
		public DndContext(Dragboard board) {
			this();
			setDragboard(board);
		}
		
		private void setDragboard(Dragboard board) {
			this.board = board;
		}
		
		public String getUrl() {
			if(board.hasUrl()) {
				return board.getUrl();
			} else if (board.hasFiles()) {
				return toExternalForm(getFile());
			}
			return null;
		}
		
		public File getFile() {
			if(board.hasFiles()) {
				List<File> files = board.getFiles();
				if(files != null && !files.isEmpty()) {
					return files.get(0);
				}
			}
			return null;
		}

		public List<File> getFiles() {
			return board.getFiles();
		}
		
		public boolean isSuccess() {
			return this.successProperty.get();
		}
		
		public void setSuccess(boolean success) {
			this.successProperty.set(success);
		}

		public BooleanProperty successProperty() {
			return this.successProperty;
		}
		
		public DndType getDndType() {
			return dndType;
		}
		
		public void setDndType(DndType dndType) {
			this.dndType = dndType;
		}

		public Object getUserData() {
			return userData;
		}

		public <T> T getUserData(Class<T> clazz) {
			if(userData != null && userData.getClass() == clazz) {
				return (T)userData;
			}
			return null;
		}
		
		public Image getImage() {
			return getUserData(Image.class);
		}

		public void setUserData(Object userData) {
			this.userData = userData;
		}
		
		public boolean isImage() {
			return dndType == DndType.IMAGE;
		}
		
		public boolean isLyric() {
			return dndType == DndType.LYRIC;
		}
		
		public boolean isAudio() {
			return dndType == DndType.AUDIO;
		}
		
		public boolean isDirectory() {
			return dndType == DndType.DIR;
		}
		
		public boolean isFile() {
			return dndType == DndType.FILE;
		}
		
		public boolean isLink() {
			return dndType == DndType.LINK;
		}
		
		public boolean isJar() {
			return dndType == DndType.JAR;
		}
	}
	
	public enum DndType {
		IMAGE, LYRIC, AUDIO, DIR, 
		FILE, LINK, JAR, UNKNOWN;
	}

}
