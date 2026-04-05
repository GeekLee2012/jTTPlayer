package xyz.rive.jttplayer.common;

import java.util.UUID;

public class Server {
    private final String id;
    private String name;
    private String apiUrl;
    private long created;
    private long updated;

    public Server() {
        id = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        created = now;
        updated = now;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updated = System.currentTimeMillis();
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
        updated = System.currentTimeMillis();
    }

    public long getCreated() {
        return created;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "Server{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }
}
