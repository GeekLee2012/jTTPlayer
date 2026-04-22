package xyz.rive.jttplayer.player.mpv;

import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;

public class SocketIpcChannel implements IpcChannel {
    private final SocketChannel channel;

    public SocketIpcChannel(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public boolean connect(String socketFileName) throws IOException {
        if (channel != null) {
            SocketAddress socketAddress = AFUNIXSocketAddress.of(Paths.get(socketFileName));
            return channel.connect(socketAddress);
        }
        return false;
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
