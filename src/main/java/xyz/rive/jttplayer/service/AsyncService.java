package xyz.rive.jttplayer.service;

import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.Lyric;
import xyz.rive.jttplayer.common.Track;
import xyz.rive.jttplayer.util.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.common.Constants.AUDIO_SUFFIXES;
import static xyz.rive.jttplayer.util.FileUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.generateSpacing;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

public class AsyncService {
    private ExecutorService service = null;
    private ScheduledExecutorService scheduleService = null;
    private final static int PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();

    private ExecutorService getService() {
        if(service == null) {
            service = Executors.newFixedThreadPool(PROCESSOR_COUNT + 1);
        }
        return service;
    }

    private ScheduledExecutorService getScheduleService() {
        if(scheduleService == null) {
            scheduleService = Executors.newScheduledThreadPool(PROCESSOR_COUNT);
        }
        return scheduleService;
    }

    public Future<?> submit(Runnable task) {
        return getService().submit(task);
    }

    public ScheduledFuture<?> scheduleDelayMillis(Runnable task, long delay) {
        return getScheduleService().schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay) {
        return getScheduleService().scheduleWithFixedDelay(task, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

}
