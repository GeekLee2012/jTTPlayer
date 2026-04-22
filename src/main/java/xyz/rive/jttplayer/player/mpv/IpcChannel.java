package xyz.rive.jttplayer.player.mpv;

import java.io.IOException;

public interface IpcChannel {
    boolean connect(String url) throws IOException;
    void write(String data) throws IOException;
    String read() throws IOException;
    void close() throws IOException;
}
