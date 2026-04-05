package xyz.rive.jttplayer.player.mpv;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class IpcCommand {
    private static final AtomicInteger ID = new AtomicInteger(20);
    private Map<Integer, Future<Object>> requestTaskMap = new ConcurrentHashMap<>();
    private IpcChannel channel;
    private boolean debug = false;

    public IpcCommand(IpcChannel channel) {
        this.channel = channel;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /*** IPC ***/
    private static String setupCommand(String command, int requestId, Object... args) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"command\"")
                .append(":[\"")
                .append(command)
                .append("\"");
        boolean isObserveCommand = command.equals("observe_property");
        if(requestId >= -1) {
            if(isObserveCommand) {
                builder.append(",").append(requestId);
            }
        }
        if(args != null) {
            for (Object arg : args) {
                if(arg instanceof String) {
                    builder.append(",\"")
                            .append(arg)
                            .append("\"");
                } else {
                    builder.append(",").append(arg);
                }

            }
        }
        builder.append("]");
        if(requestId >= -1) {
            if(!isObserveCommand) {
                builder.append(",\"request_id\"")
                        .append(":")
                        .append(requestId);
            }
        }
        return builder.append("}")
                .append("\n")
                .toString();
    }

    public void sendCommand(String commandName, Object... args) {
        sendIdCommand(commandName, -1, args);
    }

    public void sendIdCommand(String commandName, int requestId, Object... args) {
        String command = setupCommand(commandName, requestId, args);
        try {
            channel.write(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(debug) {
            System.out.println(command);
        }
    }

    public Future<Object> sendCommandAsync(String commandName, Object... args) {
        return sendIdCommandAsync(commandName, ID.getAndIncrement(), args);
    }

    public Future<Object> sendIdCommandAsync(String commandName, int id, Object... args) {
        sendIdCommand(commandName, id, args);
        Future<Object> future = new CompletableFuture<>();
        requestTaskMap.put(id, future);
        return future;
    }

    public void setProperty(String key, Object value) {
        sendCommand("set_property", key, value);
    }

    public void cycleProperty(String key) {
        sendCommand("cycle", key);
    }

    public Future<Object> getProperty(String key) {
        try {
            return sendCommandAsync("get_property", key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void af(String operation, Object value) {
        sendCommand("af", operation, value);
    }

    public void observeProperty(String key) {
        sendIdCommandAsync("observe_property", ID.getAndIncrement(), key);
    }

    public void observeProperty(String key, int id) {
        sendIdCommandAsync("observe_property", id, key);
    }

    public Future<Object> getRequestTask(int requestId) {
        return requestTaskMap.get(requestId);
    }

    public void removeRequestTask(int requestId) {
        requestTaskMap.remove(requestId);
    }

}
