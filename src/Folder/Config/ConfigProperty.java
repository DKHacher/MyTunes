package Folder.Config;

public enum ConfigProperty {
    FILE_DIRECTORY("fileDirectory");

    private final String key;

    ConfigProperty(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
