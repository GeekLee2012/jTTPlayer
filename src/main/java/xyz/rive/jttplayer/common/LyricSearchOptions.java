package xyz.rive.jttplayer.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static xyz.rive.jttplayer.common.Constants.LYRIC_DOWNLOAD_DIR_PLACEHOLDER;
import static xyz.rive.jttplayer.common.Constants.TRACK_DIR_PLACEHOLDER;
import static xyz.rive.jttplayer.util.FileUtils.transformPath;
import static xyz.rive.jttplayer.util.StringUtils.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LyricSearchOptions {
    private List<ItemOrder> searchOrders;
    private boolean saveToTrackPath = true;
    private String downloadPath;
    private List<Server> servers;
    private String selectedServer;

    public List<ItemOrder> getSearchOrders() {
        if (searchOrders == null) {
            searchOrders = new CopyOnWriteArrayList<>();
        }
        searchOrders.sort(Comparator.comparingInt(ItemOrder::getOrder));
        return searchOrders;
    }

    public void setSearchOrders(List<ItemOrder> searchOrders) {
        this.searchOrders = searchOrders;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    @JsonIgnore
    public ItemOrder getItemOrder(String path) {
        for (ItemOrder order : getSearchOrders()) {
            if (contentEquals(path, order.getPath())) {
                return order;
            }
        }
        return null;
    }

    @JsonIgnore
    public void addItemOrder(String path, boolean deep, int order) {
        if (!isEmpty(path)) {
            getSearchOrders().add(new ItemOrder(transformPath(path), deep, order));
        }
    }


    @JsonIgnore
    public void clearItemOrders() {
        if (searchOrders != null && !searchOrders.isEmpty()) {
            searchOrders.clear();
        }
    }

    @JsonIgnore
    public List<ItemOrder> prepareSearchOrders() {
        if (getSearchOrders().isEmpty()) {
            searchOrders.add(new ItemOrder(TRACK_DIR_PLACEHOLDER, false, 0));
            searchOrders.add(new ItemOrder(LYRIC_DOWNLOAD_DIR_PLACEHOLDER, false, 1));
        }
        return searchOrders;
    }

    public List<Server> getServers() {
        if (servers == null) {
            servers = new CopyOnWriteArrayList<>();
        }
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    @JsonIgnore
    public void updateServer(Server server) {
        for (Server s : servers) {
            if (contentEquals(s.getId(), server.getId())) {
                s.setName(server.getName());
                s.setApiUrl(server.getApiUrl());
                break;
            }
        }
    }

    @JsonIgnore
    public Server getServer(String name) {
        for (Server s : servers) {
            if (contentEquals(s.getName(), name)) {
                return s;
            }
        }
        return null;
    }

    @JsonIgnore
    public boolean existsServer(String name) {
        return getServer(name) != null;
    }

    public String getSelectedServer() {
        return selectedServer;
    }

    public void setSelectedServer(String selectedServer) {
        this.selectedServer = selectedServer;
    }

    public boolean isSaveToTrackPath() {
        return saveToTrackPath;
    }

    public void setSaveToTrackPath(boolean saveToTrackPath) {
        this.saveToTrackPath = saveToTrackPath;
    }
}
