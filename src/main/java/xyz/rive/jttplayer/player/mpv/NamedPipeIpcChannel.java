package xyz.rive.jttplayer.player.mpv;

import java.io.*;

public class NamedPipeIpcChannel implements IpcChannel {
    private String namedPipe;

    public NamedPipeIpcChannel(String namedPipe) {
        this.namedPipe = namedPipe;
    }

    @Override
    public void write(String data) throws IOException {
        //TODO
    }

    @Override
    public String read() throws IOException {
        //TODO
        return null;
    }

    @Override
    public void close() throws IOException {
        //TODO
    }
}
