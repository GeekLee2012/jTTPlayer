package xyz.rive.jttplayer.manager;

import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.Size;
import xyz.rive.jttplayer.skin.*;
import xyz.rive.jttplayer.util.FxUtils;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static xyz.rive.jttplayer.skin.Constants.*;
import static xyz.rive.jttplayer.util.FileUtils.deleteIfExists;
import static xyz.rive.jttplayer.util.FileUtils.readText;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class SkinManager extends AbstractManager {
    private final boolean archivedMode;
    private final SkinParser skinParser;
    private final Map<String, SkinXml> skins;
    private final Map<String, StandaloneXml> skinStandaloneXmls;
    private int rollbackRetry = 0;

    public SkinManager(ApplicationContext context) {
        this(context, false);
    }

    public SkinManager(ApplicationContext context, boolean archivedMode) {
        super(context);
        this.archivedMode = archivedMode;
        skinParser = new SkinParser(
                getSkinRoot(),
                getWorkPath(),
                archivedMode
        );
        skins = new TreeMap<>();
        skinStandaloneXmls = new HashMap<>();
        setupSkins();
    }

    public Map<String, SkinXml> getSkins() {
        return skins;
    }

    private String getSkinRoot() {
        String customRoot = getContext().getConfiguration()
                .getPlayerOptions().getSkinRoot();
        return isEmpty(customRoot) ? getAppDataPath("Skin")
                : trim(customRoot);
    }

    public void updateSkinRoot() {
        skinParser.setSkinsRootPath(getSkinRoot());
        setupSkins();
    }

    public void setupSkins() {
        skins.clear();
        skinStandaloneXmls.clear();
        skins.put(DEFAULT_SKIN_NAME, null);
        File skinsRoot = new File(getSkinRoot());
        String[] sknFilenames = skinsRoot.list((dir, name) -> name.endsWith(".skn"));
        if(sknFilenames != null) {
            for (String name : sknFilenames) {
                skins.put(name, null);
            }
        }
    }

    private void rollbackToDefaultSkinName() {
        getContext().getConfiguration()
                .getPlayerOptions()
                .setActiveSkinName(DEFAULT_SKIN_NAME);
    }

    public SkinXml getSkinXml(String sknFilename) {
        String name = skinParser.transformSknFilename(sknFilename);
        SkinXml skin = skins.get(name);
        try {
            if(skin == null) {
                name = prepareSkin(name);
                if(skinParser.isDefaultSkin(name)) {
                    skin = skinParser.parseDefaultSknSkinXml();
                    skin.name = DEFAULT_SKIN_SNAME;
                } else {
                    skin = skinParser.parseSkinXml(name);
                }
                if(skin != null) {
                    skin.filename = name;
                    skins.put(name, skin);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //失败时回滚至默认
        if (skin == null && ++rollbackRetry < 3) {
            rollbackToDefaultSkinName();
            return getDefaultSkinXml();
        }
        rollbackRetry = 0;
        return skin;
    }

    public String prepareSkin(String sknFilename) {
        boolean success = false;
        int retry = 0;
        do {
            success = skinParser.prepareSkin(sknFilename);
            //失败时回滚至默认
            if(!success) {
                sknFilename = DEFAULT_SKIN_NAME;
                rollbackToDefaultSkinName();
            }
        } while (!success && ++retry < 3);
        return sknFilename;
    }

    public SkinXml getDefaultSkinXml() {
        return getSkinXml(null);
    }

    public byte[] getSknEntry(String sknFilename, String entryName) {
        if(skinParser.isDefaultSkin(sknFilename)) {
            return skinParser.parseDefaultSkn(entryName);
        }
        return skinParser.parseSkn(sknFilename, entryName);
    }

    public byte[] getDefaultSknEntry(String entryName) {
        return getSknEntry(null, entryName);
    }

    private StandaloneXml getStandaloneXml(String sknFilename, String entryName, String rootName, String subRootName) {
        String name = skinParser.transformSknFilename(sknFilename);
        String key = String.format("%s/%s", name, entryName);
        StandaloneXml xml = skinStandaloneXmls.get(key);
        if (xml == null) {
            xml = skinParser.getStandaloneXml(
                    getSknEntry(name, entryName),
                    rootName,
                    subRootName
            );
            skinStandaloneXmls.put(key, xml);
        }
        return xml;
    }

    public StandaloneXml getLyricXml(String sknFilename) {
        return getStandaloneXml(
                sknFilename,
                ENTRY_LYRIC_XML,
                ELEMENT_STA_LYRIC_ROOT,
                ELEMENT_STA_LYRIC_SUB_ROOT
        );
    }

    public StandaloneXml getPlaylistXml(String sknFilename) {
        return getStandaloneXml(
                sknFilename,
                ENTRY_PLAYLIST_XML,
                ELEMENT_STA_PLAYLIST_ROOT,
                ELEMENT_STA_PLAYLIST_SUB_ROOT
        );
    }

    public StandaloneXml getVisualXml(String sknFilename) {
        return getStandaloneXml(
                sknFilename,
                ENTRY_VISUAL_XML,
                ELEMENT_STA_VISUAL_ROOT,
                ELEMENT_STA_VISUAL_SUB_ROOT
        );
    }

    private String getSknPath(String name) {
        if (isEmpty(name)) {
            return name;
        }
        return String.format("%s/%s", getSkinRoot(), name);
    }


    public boolean remove(String name) {
        if (skinParser.isDefaultSkin(name)) {
            return false;
        }
        boolean success = skins.remove(name) != null;
        if (success) {
            deleteIfExists(Paths.get(getSknPath(name)));
        }
        return success;
    }

    public Size getImageSize(SkinXml skin, String imageName) {
        if (isEmpty(imageName)) {
            return new Size();
        }
        byte[] bytes = getSknEntry(skin.filename, imageName);
        if (bytes == null || bytes.length < 1) {
            return new Size();
        }
        return FxUtils.getImageSize(new ByteArrayInputStream(bytes));
    }

    public Size getItemSize(SkinXml skin, PositionBasedItem item) {
        if (item == null) {
            return new Size();
        }
        double width = item.width();
        double height = item.height();
        if (width <= 0 && height <= 0) {
            return getImageSize(skin, item.image);
        }
        return new Size(width, height);
    }

    public String getSknEntryUrl(String sknFilename, String entryName) {
        return skinParser.getSknEntryUrl(sknFilename, entryName);
    }

    public String getSknEntryUrl(SkinXml skin, String entryName) {
        return getSknEntryUrl(skin.filename, entryName);
    }

    public String getSknEntryPath(SkinXml skin, String entryName) {
        return skinParser.getSknEntryPath(skin.filename, entryName);
    }

}
