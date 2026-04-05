package xyz.rive.jttplayer.player.mpv;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketIpcChannel implements IpcChannel {
    private final SocketChannel channel;

    public SocketIpcChannel(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void write(String data) throws IOException {
        if(channel == null || !channel.isConnected()) {
            return ;
        }
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    @Override
    public String read() throws IOException {
        if(channel == null || !channel.isConnected()) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 2);
        int bytesRead = channel.read(buffer);
        if (bytesRead < 0) {
            return null;
        }

        byte[] bytes = new byte[bytesRead];
        buffer.flip();
        buffer.get(bytes);
        return new String(bytes);
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }
}
