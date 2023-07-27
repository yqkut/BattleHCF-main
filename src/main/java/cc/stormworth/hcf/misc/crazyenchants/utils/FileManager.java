package cc.stormworth.hcf.misc.crazyenchants.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FileManager {

    private static final FileManager instance = new FileManager();
    private Plugin plugin;
    private String prefix = "";
    private boolean log = false;
    private final Map<Files, File> files = new HashMap<>();
    private final List<String> homeFolders = new ArrayList<>();
    private final List<CustomFile> customFiles = new ArrayList<>();
    private final Map<String, String> jarHomeFolders = new HashMap<>();
    private final Map<String, String> autoGenerateFiles = new HashMap<>();
    private final Map<Files, FileConfiguration> configurations = new HashMap<>();

    public static FileManager getInstance() {
        return instance;
    }

    public FileManager setup(Plugin plugin) {
        prefix = "[" + plugin.getName() + "] ";
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        files.clear();
        customFiles.clear();
        configurations.clear();
        //Loads all the normal static files.
        for (Files file : Files.values()) {
            File newFile = new File(plugin.getDataFolder(), file.getFileLocation());
            if (log) System.out.println(prefix + "Loading the " + file.getFileName());
            if (!newFile.exists()) {
                try {
                    File serverFile = new File(plugin.getDataFolder(), "/" + file.getFileLocation());
                    InputStream jarFile = getClass().getResourceAsStream("/" + file.getFileJar());
                    copyFile(jarFile, serverFile);
                } catch (Exception e) {
                    if (log) System.out.println(prefix + "Failed to load file: " + file.getFileName());
                    e.printStackTrace();
                    continue;
                }
            }
            files.put(file, newFile);
            configurations.put(file, YamlConfiguration.loadConfiguration(newFile));
            if (log) System.out.println(prefix + "Successfully loaded " + file.getFileName());
        }
        //Starts to load all the custom files.
        if (!homeFolders.isEmpty()) {
            if (log) System.out.println(prefix + "Loading custom files.");
            for (String homeFolder : homeFolders) {
                File homeFile = new File(plugin.getDataFolder(), "/" + homeFolder);
                if (homeFile.exists()) {
                    String[] list = homeFile.list();
                    if (list != null) {
                        for (String name : list) {
                            if (name.endsWith(".yml")) {
                                CustomFile file = new CustomFile(name, homeFolder, plugin);
                                if (file.exists()) {
                                    customFiles.add(file);
                                    if (log)
                                        System.out.println(prefix + "Loaded new custom file: " + homeFolder + "/" + name + ".");
                                }
                            }
                        }
                    }

                } else {
                    homeFile.mkdir();
                    if (log)
                        System.out.println(prefix + "The folder " + homeFolder + "/ was not found so it was created.");
                    for (Entry<String, String> file : autoGenerateFiles.entrySet()) {
                        if (file.getValue().equalsIgnoreCase(homeFolder)) {
                            homeFolder = file.getValue();
                            try {
                                File serverFile = new File(plugin.getDataFolder(), homeFolder + "/" + file.getKey());
                                InputStream jarFile = getClass().getResourceAsStream((jarHomeFolders.getOrDefault(file.getKey(), homeFolder)) + "/" + file.getKey());
                                copyFile(jarFile, serverFile);
                                if (file.getKey().toLowerCase().endsWith(".yml")) {
                                    customFiles.add(new CustomFile(file.getKey(), homeFolder, plugin));
                                }
                                if (log)
                                    System.out.println(prefix + "Created new default file: " + homeFolder + "/" + file.getKey() + ".");
                            } catch (Exception e) {
                                if (log)
                                    System.out.println(prefix + "Failed to create new default file: " + homeFolder + "/" + file.getKey() + "!");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            if (log) System.out.println(prefix + "Finished loading custom files.");
        }
        return this;
    }

    public FileManager logInfo(boolean log) {
        this.log = log;
        return this;
    }

    public boolean isLogging() {
        return log;
    }

    public FileManager registerCustomFilesFolder(String homeFolder) {
        homeFolders.add(homeFolder);
        return this;
    }

    public FileManager unregisterCustomFilesFolder(String homeFolder) {
        homeFolders.remove(homeFolder);
        return this;
    }

    public FileManager registerDefaultGenerateFiles(String fileName, String homeFolder) {
        autoGenerateFiles.put(fileName, homeFolder);
        return this;
    }

    public FileManager registerDefaultGenerateFiles(String fileName, String homeFolder, String jarHomeFolder) {
        autoGenerateFiles.put(fileName, homeFolder);
        jarHomeFolders.put(fileName, jarHomeFolder);
        return this;
    }

    public FileManager unregisterDefaultGenerateFiles(String fileName) {
        autoGenerateFiles.remove(fileName);
        jarHomeFolders.remove(fileName);
        return this;
    }

    public FileConfiguration getFile(Files file) {
        return configurations.get(file);
    }

    public CustomFile getFile(String name) {
        for (CustomFile file : customFiles) {
            if (file.getName().equalsIgnoreCase(name)) {
                return file;
            }
        }
        return null;
    }

    public void saveFile(Files file) {
        try {
            configurations.get(file).save(files.get(file));
        } catch (IOException e) {
            System.out.println(prefix + "Could not save " + file.getFileName() + "!");
            e.printStackTrace();
        }
    }

    public void saveFile(String name) {
        CustomFile file = getFile(name);
        if (file != null) {
            try {
                file.getFile().save(new File(plugin.getDataFolder(), file.getHomeFolder() + "/" + file.getFileName()));
                if (log) System.out.println(prefix + "Successfully saved the " + file.getFileName() + ".");
            } catch (Exception e) {
                System.out.println(prefix + "Could not save " + file.getFileName() + "!");
                e.printStackTrace();
            }
        } else {
            if (log) System.out.println(prefix + "The file " + name + ".yml could not be found!");
        }
    }

    public boolean saveFile(CustomFile file) {
        return file.saveFile();
    }

    public void reloadFile(Files file) {
        configurations.put(file, YamlConfiguration.loadConfiguration(files.get(file)));
    }

    public void reloadFile(String name) {
        CustomFile file = getFile(name);
        if (file != null) {
            try {
                file.file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "/" + file.getHomeFolder() + "/" + file.getFileName()));
                if (log) System.out.println(prefix + "Successfully reload the " + file.getFileName() + ".");
            } catch (Exception e) {
                System.out.println(prefix + "Could not reload the " + file.getFileName() + "!");
                e.printStackTrace();
            }
        } else {
            if (log) System.out.println(prefix + "The file " + name + ".yml could not be found!");
        }
    }

    public boolean reloadFile(CustomFile file) {
        return file.reloadFile();
    }

    private void copyFile(InputStream in, File out) throws Exception {
        try (InputStream fis = in; FileOutputStream fos = new FileOutputStream(out)) {
            byte[] buf = new byte[1024];
            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
    }

    public enum Files {
        CUSTOMENCHANTS("customenchants.yml", "customenchants.yml", "customenchants.yml");

        private final String fileName;
        private final String fileJar;
        private final String fileLocation;

        Files(String fileName, String fileLocation) {
            this(fileName, fileLocation, fileLocation);
        }

        Files(String fileName, String fileLocation, String fileJar) {
            this.fileName = fileName;
            this.fileLocation = fileLocation;
            this.fileJar = fileJar;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileLocation() {
            return fileLocation;
        }

        public String getFileJar() {
            return fileJar;
        }

        public FileConfiguration getFile() {
            return getInstance().getFile(this);
        }

        public void saveFile() {
            getInstance().saveFile(this);
        }

        public void relaodFile() {
            getInstance().reloadFile(this);
        }

    }

    public class CustomFile {

        private final String name;
        private final Plugin plugin;
        private final String fileName;
        private final String homeFolder;
        private FileConfiguration file;

        public CustomFile(String name, String homeFolder, Plugin plugin) {
            this.name = name.replace(".yml", "");
            this.plugin = plugin;
            this.fileName = name;
            this.homeFolder = homeFolder;
            if (new File(plugin.getDataFolder(), "/" + homeFolder).exists()) {
                if (new File(plugin.getDataFolder(), "/" + homeFolder + "/" + name).exists()) {
                    file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "/" + homeFolder + "/" + name));
                } else {
                    file = null;
                }
            } else {
                new File(plugin.getDataFolder(), "/" + homeFolder).mkdir();
                if (log) System.out.println(prefix + "The folder " + homeFolder + "/ was not found so it was created.");
                file = null;
            }
        }

        public String getName() {
            return name;
        }

        public String getFileName() {
            return fileName;
        }

        public String getHomeFolder() {
            return homeFolder;
        }

        public Plugin getPlugin() {
            return plugin;
        }

        public FileConfiguration getFile() {
            return file;
        }

        public boolean exists() {
            return file != null;
        }

        public boolean saveFile() {
            if (file != null) {
                try {
                    file.save(new File(plugin.getDataFolder(), homeFolder + "/" + fileName));
                    if (log) System.out.println(prefix + "Successfuly saved the " + fileName + ".");
                    return true;
                } catch (Exception e) {
                    System.out.println(prefix + "Could not save " + fileName + "!");
                    e.printStackTrace();
                    return false;
                }
            } else {
                if (log) System.out.println(prefix + "There was a null custom file that could not be found!");
            }
            return false;
        }

        public boolean reloadFile() {
            if (file != null) {
                try {
                    file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "/" + homeFolder + "/" + fileName));
                    if (log) System.out.println(prefix + "Successfuly reload the " + fileName + ".");
                    return true;
                } catch (Exception e) {
                    System.out.println(prefix + "Could not reload the " + fileName + "!");
                    e.printStackTrace();
                }
            } else {
                if (log) System.out.println(prefix + "There was a null custom file that was not found!");
            }
            return false;
        }

    }
}