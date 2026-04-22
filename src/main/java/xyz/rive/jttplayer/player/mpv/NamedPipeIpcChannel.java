package xyz.rive.jttplayer.player.mpv;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import java.io.*;

import static com.sun.jna.platform.win32.WinBase.PIPE_NOWAIT;

public class NamedPipeIpcChannel implements IpcChannel {
    private String pipeName;
    private WinNT.HANDLE hPipe;
    private final Kernel32 kernel32 = Kernel32.INSTANCE;
    private final byte[] BUFFER = new byte[1024 * 2];
    private final IntByReference bytesRead = new IntByReference();
    private final IntByReference writeBytes = new IntByReference();
    private int connectRetry = 0;

    public NamedPipeIpcChannel(String pipeName) {
        this.pipeName = pipeName;
    }

    public boolean connect(String pipeName) throws IOException {
        if (hPipe == null) {
            hPipe = openPipe(pipeName);
        }
        return true;
    }

    public WinNT.HANDLE openPipe(String pipeName) throws IOException {
        while(true) {
            WinNT.HANDLE handle = kernel32.CreateFile(
                    pipeName,
                    WinNT.GENERIC_READ | WinNT.GENERIC_WRITE,
                    WinNT.FILE_SHARE_READ | WinNT.FILE_SHARE_WRITE,
                    null,
                    WinNT.OPEN_EXISTING,
                    WinNT.FILE_FLAG_WRITE_THROUGH,
                    null
            );

            if (!WinBase.INVALID_HANDLE_VALUE.equals(handle)) {
                kernel32.SetNamedPipeHandleState(
                        handle,
                        new IntByReference(PIPE_NOWAIT),
                        null,
                        null
                );
                connectRetry = 0;
                return handle;
            }
            //避免长时间重试
            if (connectRetry >= 30) {
                break;
            }

            int error = kernel32.GetLastError();
            //Pipe Busy
            if (error == 231) {
                ++connectRetry;
                kernel32.WaitNamedPipe(pipeName, 2000);
            } else {
                System.out.println("Failed to open pipe, error: " + error);
                break;
            }
        }
        return null;
    }


    @Override
    public void write(String data) throws IOException {
        if (hPipe == null) {
            return ;
        }
        byte[] bytes = data.getBytes();
        kernel32.WriteFile(hPipe, bytes, bytes.length, writeBytes, null);
    }

    @Override
    public String read() throws IOException {
        if (hPipe == null) {
            return null;
        }
        boolean success = kernel32.ReadFile(hPipe, BUFFER, BUFFER.length, bytesRead, null);
        return success ? new String(BUFFER, 0, bytesRead.getValue()) : null;
    }

    @Override
    public void close() throws IOException {
        closeHandle(hPipe);
    }

    private void closeHandle(WinNT.HANDLE handle) throws IOException {
        if (handle != null && !WinBase.INVALID_HANDLE_VALUE.equals(handle)) {
            kernel32.CloseHandle(handle);
        }
    }
}
