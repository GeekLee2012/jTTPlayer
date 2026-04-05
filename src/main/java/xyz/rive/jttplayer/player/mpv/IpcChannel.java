package xyz.rive.jttplayer.player.mpv;

import java.io.IOException;

public interface IpcChannel {
    void write(String data) throws IOException;
    String read() throws IOException;
    void close() throws IOException;
}
