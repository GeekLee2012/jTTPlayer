package xyz.rive.jttplayer.player.mpv;

public class IpcResult {
    private String event;
    private String name;
    private String error;
    private Object data;
    private String reason;
    private Integer request_id;
    private Integer id;
    private Integer playlist_entry_id;
    private String file_error;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getRequest_id() {
        return request_id;
    }

    public void setRequest_id(Integer request_id) {
        this.request_id = request_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlaylist_entry_id() {
        return playlist_entry_id;
    }

    public void setPlaylist_entry_id(Integer playlist_entry_id) {
        this.playlist_entry_id = playlist_entry_id;
    }

    public String getFile_error() {
        return file_error;
    }

    public void setFile_error(String file_error) {
        this.file_error = file_error;
    }
}
