package xyz.rive.jttplayer.service;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.PlaybackQueue;
import xyz.rive.jttplayer.common.Track;
import xyz.rive.jttplayer.util.JsonUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static xyz.rive.jttplayer.common.Constants.PLAYBACK_QUEUE_SUFFIX;
import static xyz.rive.jttplayer.util.FileUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class PlaybackQueueService {
    private final ApplicationContext context;
    private List<PlaybackQueue> playbackQueues;
    private final AtomicInteger INDEX = new AtomicInteger(1);
    private final IntegerProperty sizeProperty = new SimpleIntegerProperty(0);


    public final static BiFunction<File, Integer, String> BY_INDEX_SUPPLIER = (directory, index) -> String.format("%1$s/%2$05d%3$s",
            transformPath(directory.getAbsolutePath()),
            index,
            PLAYBACK_QUEUE_SUFFIX);

    public PlaybackQueueService(ApplicationContext context) {
        this.context = context;
    }

    public List<PlaybackQueue> listAll() {
        if(playbackQueues == null) {
            playbackQueues = new ArrayList<>();
        }
        return playbackQueues;
    }

    public int size() {
        return listAll().size();
    }

    public int indexOf(String id) {
        for(int i = 0; i < listAll().size(); i++) {
            PlaybackQueue queue = playbackQueues.get(i);
            if(queue.getId() != null && queue.getId().equals(id)) {
                return i;
            }
        }
        return 0;
    }

    public PlaybackQueue byName(String name) {
        name = isEmpty(name) ? "默认" : name;
        for (PlaybackQueue queue : listAll()) {
            if (trim(queue.getName()).equals(trim(name))) {
                return queue;
            }
        }
        return null;
    }

    public PlaybackQueue get(String id) {
        if(isEmpty(id)) {
            return null;
        }
        List<PlaybackQueue> queues = listAll();
        for (PlaybackQueue queue : queues) {
            if (trim(queue.getId()).equals(trim(id))) {
                return queue;
            }
        }
        return null;
    }

    public PlaybackQueue get(int index) {
        if(index < 0 || index >= listAll().size()) {
            return null;
        }
        return playbackQueues.get(index);
    }

    public boolean add(PlaybackQueue queue) {
        if(queue != null) {
            PlaybackQueue existQueue = byName(queue.getName());
            if(existQueue == null) {
                listAll().add(queue);
                setSize(size());
                return true;
            } else if (existQueue.isEmpty()) {
                existQueue.addAll(queue.getData());
            }
            return false;
        }
        return false;
    }

    public boolean remove(PlaybackQueue queue) {
        if(listAll().size() <= 1) {
            return false;
        }
        if(queue != null) {
            boolean result = listAll().remove(queue);
            setSize(size());
            return result;
        }
        return false;
    }

    public PlaybackQueue create(String name) {
        if(isEmpty(name)) {
            name = "新列表" + INDEX.getAndIncrement();
        }
        PlaybackQueue queue = new PlaybackQueue();
        queue.setId(String.valueOf(UUID.randomUUID()));
        queue.setName(name);
        add(queue);
        return queue;
    }

    public boolean save(File file, PlaybackQueue queue) {
        return save(file, queue, null);
    }

    public boolean save(File file, PlaybackQueue queue, String queueName) {
        try {
            String filename = file.getAbsolutePath();
            if(!isEmpty(queueName)) {
                queue.setName(queueName);
            }
            String content = JsonUtils.stringify(queue);
            writeText(filename, content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public PlaybackQueue restore(File file) {
        try {
            String fileName = file.getAbsolutePath();
            if(!exists(fileName)) {
                return null;
            }
            return doRestore(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private PlaybackQueue doRestore(String fileName) {
        PlaybackQueue queue = null;
        if(trimLowerCase(fileName).endsWith(".m3u")
                || trimLowerCase(fileName).endsWith(".m3u8")) {
            queue = parseFromM3U(fileName);
        } else if(trimLowerCase(fileName).endsWith(".jttpl")) {
            queue = parseFromJttpl(fileName);
        }
        if(queue != null && isEmpty(queue.getRawName())) {
            queue.setName(guessSimpleName(fileName));
        }
        return queue;
    }

    private PlaybackQueue parseFromJttpl(String fileName) {
        String content = readText(fileName);
        return JsonUtils.parseJson(content, PlaybackQueue.class);
    }

    private PlaybackQueue parseFromM3U(String fileName) {
        List<String> lines = readLines(fileName);
        if(lines == null || lines.isEmpty()) {
            return null;
        }

        PlaybackQueue queue = new PlaybackQueue();
        int length = -1;
        String cover = "";
        String title = "";
        String url = "";
        for (String line : lines) {
            line = trim(line);
            if(isEmpty(line) || line.startsWith("#EXTM3U")) {
                continue;
            }
            if (line.startsWith("#EXTINF")) {
                String metaText = line.replace("#EXTINF:", "");
                String[] metas = metaText.split(",");
                length = Integer.parseInt(metas[0]);
                if (metas.length == 3) {
                    title = metas[1];
                    cover = metas[2];
                } else if (metas.length >= 4) {
                    title = metas[1];
                    for (int i = 2; i < metas.length; i++) {
                        cover += (metas[i] + ",");
                    }
                    cover.substring(0, cover.length() - 1);
                } else {
                    title = metas[1];
                }
            } else {
                url = line;
            }
            if (!isEmpty(url) && !isEmpty(title)) {
                Track track = null;
                if(trim(url).startsWith("http")) { //网络歌曲
                    //去引号
                    cover = trim(cover).replaceAll("“","")
                            .replaceAll("”","")
                            .replaceAll("'", "")
                            .replaceAll("\"", "");

                    track = new Track();
                    track.setId(UUID.randomUUID().toString());
                    track.setTitle(trim(title));
                    track.setCover(cover);
                    track.setUrl(trim(url));
                    if(length > 0) {
                        track.setTrackLength(length);
                    }
                } else {
                    track = context.getTrackService().parseTrack(url);
                }
                if(track != null) {
                    queue.addAll(track);
                }

                length = -1;
                cover = "";
                title = "";
                url = "";
            }
        }
        return queue;
    }

    public void saveAll(File directory) {
        saveAll(directory, (dir, index) -> String.format("%1$s/%2$s%3$s",
                transformPath(dir.getAbsolutePath()),
                listAll().get(index).getName(),
                PLAYBACK_QUEUE_SUFFIX)
        );
    }

    public void saveAll(File directory, BiFunction<File, Integer, String> fileNameSupplier) {
        for (int i = 0; i < listAll().size(); i++) {
            String fileName = fileNameSupplier.apply(directory, i);
            save(new File(fileName), listAll().get(i));
        }
    }

    public void removeDuplicatedTracks(PlaybackQueue queue) {
        if(queue == null || queue.isEmpty()) {
            return ;
        }
        List<Track> tracks = new ArrayList<>();
        for (Track t : queue.getData()) {
            if (tracks.isEmpty()) {
                tracks.add(t);
            }
            for (Track k : tracks) {
                if (!t.equals(k) && !t.isMetadataSimilar(k)) {
                    tracks.add(t);
                }
            }
        }
        queue.setAll(tracks);
    }

    public void removeInvalidTracks(PlaybackQueue queue) {
        if(queue == null || queue.isEmpty()) {
            return ;
        }
        List<Track> data = queue.getData().stream()
                .filter(track -> (track != null && exists(track.getUrl())))
                .collect(Collectors.toCollection(ArrayList::new));
        queue.setAll(data);
    }

    public IntegerProperty sizeProperty() {
        return sizeProperty;
    }

    private void setSize(int size) {
        sizeProperty.set(size);
    }

}
