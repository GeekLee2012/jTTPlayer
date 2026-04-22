package xyz.rive.jttplayer.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xyz.rive.jttplayer.util.FxUtils.isWindows;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.trim;

public final class FileUtils {

    public static List<String> readLines(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if(Files.exists(path)) {
                return Files.readAllLines(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> readLines(InputStream in) {
        BufferedReader reader = null;
        List<String> lines = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            lines = reader.lines().collect(Collectors.toCollection(ArrayList::new));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

    public static boolean exists(String fileName) {
        if (isEmpty(fileName)) {
            return false;
        }
        try {
            return Files.exists(Paths.get(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String readText(InputStream in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int len;
            while (true) {
                if (!((len = in.read(buffer)) > 0)) {
                    break;
                }
                out.write(buffer, 0, len);
            }
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readText(String fileName) {
        List<String> lines = readLines(fileName);
        if(lines == null || lines.isEmpty()) {
            return null;
        }
        StringJoiner joiner = new StringJoiner("\n");
        for (String line : lines) {
            joiner.add(line);
        }
        return trim(joiner.toString());
    }

    public static void writeText(String fileName, String text) {
        if (isEmpty(text)) {
            return ;
        }
        try {
            String name = transformPath(fileName);
            Path path = Paths.get(name);
            if(!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
            Files.write(path, text.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String fileName, byte[] data) {
        try {
            String name = transformPath(fileName);
            Path path = Paths.get(name);
            if(!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
            Files.write(path, data, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String guessSimpleName(String fileName) {
        if(fileName == null) {
            return null;
        }
        fileName = trim(fileName);
        int from = fileName.lastIndexOf("/");
        int to = fileName.lastIndexOf(".");
        //名字中可能也带“.”
        int dashIndex = fileName.lastIndexOf("-");
        int len = fileName.length();
        //一般后缀名不会太长
        if (dashIndex > to || (len - to) >= 6) {
            to = len;
        }
        if(to > -1) {
            fileName = trim(fileName.substring(from + 1, to));
        }
        return fileName;
    }

    public static String guessExtName(String fileName) {
        if(fileName == null) {
            return null;
        }
        fileName = trim(fileName);
        String extName = "";
        int index = fileName.lastIndexOf(".");
        if(index > -1) {
            extName = fileName.substring(index + 1)
                    .trim().toLowerCase();
        }
        return extName;
    }

    public static String transformPath(String path) {
        if(isEmpty(path)) {
            return path;
        }
        return trim(path).replaceAll("\\\\", "/");
    }

    public static String detransformPath(String path) {
        if(isEmpty(path)) {
            return path;
        }
        return trim(path).replaceAll("/", "\\\\");
    }

    public static String toExternalForm(File file) {
        try {
            return transformPath(file.toURI().toURL().toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toExternalForm(String nativeFile) {
        if (isEmpty(nativeFile)) {
            return null;
        }
        try {
            String url = transformPath(nativeFile);
            String schema = "file://".concat(isWindows() ? "/" : "");
            if (!url.startsWith(schema)
                    && !url.startsWith("http")) {
                return schema.concat(url);
            }
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void walkSync(File file, Consumer<File> handle) {
        if(file == null || !file.exists()) {
            return ;
        }
        if(file.isFile()) {
            handle.accept(file);
        } else if (file.isDirectory()){
            File[] list = file.listFiles();
            for (File item : list) {
                if (file.isFile()) {
                    handle.accept(file);
                } else if(file.isDirectory()) {
                    walkSync(item, handle);
                }
            }
        }
    }

    public static void deleteRecursively(Path path, boolean exclusive) {
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(path)) {
            // 逆序：先文件/子目录，后父目录
            walk.sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            if (exclusive && path.compareTo(p) == 0) {
                                return ;
                            }
                            Files.delete(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRecursively(Path path) {
        deleteRecursively(path, false);
    }

    public static boolean deleteIfExists(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getParentPath(String url) {
        if (isEmpty(url)) {
            return null;
        }
        try {
            Path path = Paths.get(transformPath(url));
            if (Files.exists(path)) {
                return path.getParent().toFile().getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean copy(String source, String target) {
        try {
            Files.copy(
                    Paths.get(transformPath(source)),
                    Paths.get(transformPath(target)),
                    StandardCopyOption.REPLACE_EXISTING
            );
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
