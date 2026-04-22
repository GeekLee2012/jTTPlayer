package xyz.rive.jttplayer.skin;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static xyz.rive.jttplayer.skin.Constants.*;
import static xyz.rive.jttplayer.util.FileUtils.*;
import static xyz.rive.jttplayer.util.FxUtils.getResourceAsStream;
import static xyz.rive.jttplayer.util.FxUtils.isWindows;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.trim;

public class SkinParser {
    private final boolean archivedMode;
    private String skinsRootPath;
    private final String skinsWorkPath;

    public SkinParser(String skinsRootPath, String skinsWorkPath, boolean archivedMode) {
        setSkinsRootPath(skinsRootPath);
        this.skinsWorkPath = skinsWorkPath;
        this.archivedMode = archivedMode;
    }

    public void setSkinsRootPath(String skinsRootPath) {
        this.skinsRootPath = skinsRootPath;
    }

    private byte[] parseSkn(InputStream in, String entryName) {
        try (ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contentEquals(entryName)
                        && !entry.isDirectory()) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    zis.closeEntry();
                    return out.toByteArray();
                }
                zis.closeEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void unzip(InputStream in, String dest) throws IOException {
        Path destPath = Paths.get(dest);
        // 如果目标目录不存在，则创建
        if (!Files.exists(destPath)) {
            Files.createDirectories(destPath);
        }

        try (ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = destPath.resolve(entry.getName());

                // 防止Zip Slip漏洞（路径遍历攻击）
                if (!filePath.normalize().startsWith(destPath.normalize())) {
                    throw new IOException("Invalid Zip Entry: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    // 确保父目录存在
                    Path parent = filePath.getParent();
                    if (parent != null && !Files.exists(parent)) {
                        Files.createDirectories(parent);
                    }
                    // 写入文件
                    Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    private String getSkinPath(String sknFileName) {
        String name = transformSknFilename(sknFileName);
        return String.format("%s/%s",
                skinsWorkPath,
                archivedMode ? name : guessSimpleName(name)
        );
    }

    public String getSknEntryUrl(String sknFileName, String entryName) {
        if(isEmpty(entryName)) {
            return "";
        }
        String schema = archivedMode ? "" : ("file://".concat(isWindows() ? "/" : ""));
        String path = String.format("%s/%s", getSkinPath(sknFileName), entryName);
        return exists(path) ? schema.concat(path) : "";
    }

    public String getSknEntryPath(String sknFileName, String entryName) {
        if(isEmpty(entryName)) {
            return "";
        }
        return String.format("%s/%s", archivedMode ? "" : getSkinPath(sknFileName), entryName);
    }

    public byte[] parseSkn(String sknFileName, String entryName) {
        try {
            if (archivedMode) {
                return parseSkn(Files.newInputStream(Paths.get(sknFileName)), entryName);
            }
            Path path = Paths.get(getSkinPath(sknFileName), entryName);
            if (Files.exists(path)) {
                return Files.readAllBytes(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isDefaultSkin(String sknFileName) {
        return sknFileName == null
                || DEFAULT_SKIN_SNAME.equalsIgnoreCase(sknFileName)
                || DEFAULT_SKIN_NAME.equalsIgnoreCase(sknFileName);
    }

    public String transformSknFilename(String sknFileName) {
        if(isEmpty(sknFileName)) {
            return DEFAULT_SKIN_NAME;
        }
        sknFileName = trim(sknFileName);
        if (sknFileName.endsWith(".skn")) {
            return sknFileName;
        }
        return  sknFileName.concat(".skn");
    }

    public boolean prepareSkin(String sknFileName) {
        boolean success = false;
        String name = transformSknFilename(sknFileName);
        try {
            String skinWorkRoot = getSkinPath(name);
            deleteRecursively(Paths.get(skinWorkRoot));
            if (isDefaultSkin(name)) {
                unzip(getResourceAsStream("skin/".concat(DEFAULT_SKIN_NAME)), skinWorkRoot);
            } else {
                unzip(Files.newInputStream(Paths.get(skinsRootPath, name)), skinWorkRoot);
            }
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public byte[] parseDefaultSkn(String entryName) {
        if(archivedMode) {
            return parseSkn(getResourceAsStream("skin/".concat(DEFAULT_SKIN_NAME)), entryName);
        }
        try {
            String skinRoot = getSkinPath(DEFAULT_SKIN_SNAME);
            return Files.readAllBytes(Paths.get(skinRoot, entryName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SkinXml parseDefaultSknSkinXml() {
        try {
            return parseSkinXml(parseDefaultSkn(ENTRY_SKIN_XML));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SkinXml parseSkinXml(String sknFileName) throws Exception {
        String name = transformSknFilename(sknFileName);
        if(archivedMode) {
            return parseSkinXml(parseSkn(Files.newInputStream(Paths.get(name)), ENTRY_SKIN_XML));
        }
        return parseSkinXml(Files.readAllBytes(Paths.get(getSkinPath(name), ENTRY_SKIN_XML)));
    }

    public SkinXml parseSkinXml(byte[] xmlData) throws Exception {
        return parseSkinXml(new ByteArrayInputStream(xmlData));
    }

    private SkinXml parseSkinXml(InputStream xmlStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlStream);

        Element root = doc.getDocumentElement();
        if (!"skin".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root Tag is not 'skin'");
        }

        SkinXml skin = new SkinXml(getAttr(root, ATTR_VERSION),
                getAttr(root, ATTR_NAME),
                getAttr(root, ATTR_AUTHOR),
                getAttr(root, ATTR_URL),
                getAttr(root, ATTR_EMAIL),
                getAttr(root, ATTR_TRANSPARENT_COLOR));

        // Parse all window types
        parseWindowItem(skin, root, PLAYER_WINDOW);
        parseWindowItem(skin, root, LYRIC_WINDOW);
        parseWindowItem(skin, root, EQUALIZER_WINDOW);
        parseWindowItem(skin, root, PLAYLIST_WINDOW);
        parseWindowItem(skin, root, MINI_WINDOW);
        //parseWindowItem(skin, root, "browser_window");
        //parseWindowItem(skin, root, "mobile_window");
        parseWindowItem(skin, root, DESKLRC_BAR);

        return skin;
    }

    private void parseWindowItem(SkinXml skin, Element root, String tagName) {
        NodeList list = root.getElementsByTagName(tagName);
        if (list.getLength() == 0) {
            return ;
        }

        Element winElem = (Element) list.item(0);
        SkinXmlWindowItem windowItem = new SkinXmlWindowItem(
                tagName, getAttr(winElem, ATTR_IMAGE)
        );

        windowItem.setPosition(splitPosition(getAttr(winElem, ATTR_POSITION)));
        windowItem.setResizeRect(splitPosition(getAttr(winElem, ATTR_RESIZE_RECT)));
        windowItem.eq_interval = getIntAttr(winElem, ATTR_EQ_INTERVAL, 0);

        // Parse child elements (buttons, progress, text, etc.)
        NodeList children = winElem.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element elem = (Element) node;
            String elemName = elem.getTagName();

            SkinXmlItem item = new SkinXmlItem(elemName);
            item.setPosition(splitPosition(getAttr(elem, ATTR_POSITION)));

            // Common attributes
            item.image = getAttr(elem, ATTR_IMAGE);
            item.icon = getAttr(elem, ATTR_ICON);
            item.color = getAttr(elem, ATTR_COLOR);
            // Fix garbled Chinese font
            item.font = fixFontEncoding(getAttr(elem, ATTR_FONT));
            item.fontSize = getIntAttr(elem, ATTR_FONT_SIZE, 12);
            item.align = getAttr(elem, ATTR_ALIGN);

            // Progress / Volume Specific
            item.barImage = getAttr(elem, ATTR_BAR_IMAGE);
            item.thumbImage = getAttr(elem, ATTR_THUMB_IMAGE);
            item.fillImage = getAttr(elem, ATTR_FILL_IMAGE);
            item.fillImage2 = getAttr(elem, ATTR_FILL_IMAGE2);
            item.flashImage = getAttr(elem, ATTR_FLASH_IMAGE);
            item.vertical = getBooleanAttr(elem, ATTR_VERTICAL);
            item.buttonsImage = getAttr(elem, ATTR_BUTTONS_IMAGE);
            item.hotImage = getAttr(elem, ATTR_HOT_IMAGE);

            // Animation
            item.flashMode = getIntAttr(elem, ATTR_FLASH_MODE, 0);
            item.frameCount = getIntAttr(elem, ATTR_FRAME_COUNT, 1);
            item.frameInterval = getIntAttr(elem, ATTR_FRAME_INTERVAL, 16);

            // 移植版：自定义
            item.proxy = getAttr(elem, ATTR_PROXY);

            windowItem.addItem(item);
        }

        skin.addWindowItem(tagName, windowItem);
    }

    private String[] splitPosition(String pos) {
        return pos.trim().split("\\s*,\\s*");
    }

    private String getAttr(Element elem, String name) {
        return elem.hasAttribute(name) ? elem.getAttribute(name) : "";
    }

    private String getAttr(Element elem, String name, String alias) {
        return elem.hasAttribute(name) ? elem.getAttribute(name) :
                (elem.hasAttribute(alias) ? elem.getAttribute(alias) : "");
    }

    private boolean getBooleanAttr(Element elem, String name) {
        return Boolean.parseBoolean(getAttr(elem, name));
    }

    private int getIntAttr(Element elem, String name, int defaultValue) {
        String value = getAttr(elem, name);
        try {
            return value.isEmpty() ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Optional: Fix garbled font names like "Î¢ÈíÑÅºÚ" → "微软雅黑"
    // This is a known GBK → UTF-8 misinterpretation.
    // For simplicity, we map common cases:
    private String fixFontEncoding(String font) {
        if ("Î¢ÈíÑÅºÚ".equals(font)) {
            return "微软雅黑";
        } else if ("ËÎÌå".equals(font)) {
            return "宋体";
        } else if ("SimSun".equals(font)) {
            return "宋体";
        }
        return font;
    }

    public StandaloneXml getStandaloneXml(byte[] data, String rootName, String subRootName) {
        try {
            if (data != null && data.length > 0) {
                return parseStandaloneXml(new ByteArrayInputStream(data), rootName, subRootName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new StandaloneXml();
    }


    private StandaloneXml parseStandaloneXml(InputStream xmlStream, String rootName, String subRootName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlStream);

        Element rootElem = doc.getDocumentElement();
        if (!rootName.equals(rootElem.getTagName())) {
            throw new IllegalArgumentException(String.format("Root Tag is not '%s'", rootName));
        }
        StandaloneXml xml = new StandaloneXml();

        NodeList list = rootElem.getElementsByTagName(subRootName);
        if (list.getLength() == 0) {
            return xml;
        }

        Element elem = (Element) list.item(0);
        xml.font = getAttr(elem, ATTR_STA_FONT);
        xml.textColor = getAttr(elem, ATTR_STA_TEXT_COLOR, ATTR_STA_COLOR_TEXT);
        xml.hilightColor = getAttr(elem, ATTR_STA_HILIGHT_COLOR, ATTR_STA_COLOR_HILIGHT);
        xml.bkgndColor = getAttr(elem, ATTR_STA_BKGND_COLOR, ATTR_STA_COLOR_BKGND);
        xml.bkgndColor2 = getAttr(elem, ATTR_STA_BKGND_COLOR2, ATTR_STA_COLOR_BKGND2);
        xml.durationColor = getAttr(elem, ATTR_STA_COLOR_DURATION);
        xml.numberColor = getAttr(elem, ATTR_STA_COLOR_NUMBER);
        xml.selectColor = getAttr(elem, ATTR_STA_COLOR_SELECT);
        xml.selectTextColor = getAttr(elem, ATTR_STA_COLOR_SELECT_TEXT);
        //Visual
        xml.spectrumTopColor = getAttr(elem, ATTR_STA_SPECTRUM_TOP_COLOR);
        xml.spectrumBtmColor = getAttr(elem, ATTR_STA_SPECTRUM_BTM_COLOR);
        xml.spectrumMidColor = getAttr(elem, ATTR_STA_SPECTRUM_MID_COLOR);
        xml.spectrumPeakColor = getAttr(elem, ATTR_STA_SPECTRUM_PEAK_COLOR);
        xml.spectrumWide = getIntAttr(elem, ATTR_STA_SPECTRUM_WIDE, 1);
        xml.blurSpeed = getIntAttr(elem, ATTR_STA_BLUR_SPEED, 3);
        xml.blur = getIntAttr(elem, ATTR_STA_BLUR, 1);
        xml.blurScopeColor = getAttr(elem, ATTR_STA_BLUR_SCOPE_COLOR);
        return xml;
    }

}
