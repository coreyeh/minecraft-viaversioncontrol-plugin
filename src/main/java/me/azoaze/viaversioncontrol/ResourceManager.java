package me.azoaze.viaversioncontrol;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.Yaml;

public class ResourceManager {
  private final Path resourcePath;
  private Config config;
  private Map<String, Integer> versions;

  public ResourceManager(Path resourcePath) {
    this.resourcePath = resourcePath;
  }

  public void loadConfig() throws IOException {
    Path configFile = resourcePath.resolve("config.yml");

    if (!Files.exists(configFile)) {
      Files.createDirectories(resourcePath);
      try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
        if (in == null)
          throw new IOException("Default config.yml not found in resources");
        Files.copy(in, configFile);
      }
    }

    Yaml yaml = new Yaml(new Constructor(Config.class));
    try (InputStream in = Files.newInputStream(configFile)) {
      this.config = yaml.load(in);
    }
  }

  public void loadVersions() throws IOException {
    Path versionsFile = resourcePath.resolve("versions.yml");

    if (!Files.exists(versionsFile)) {
      Files.createDirectories(resourcePath);
      try (InputStream in = getClass().getClassLoader().getResourceAsStream("versions.json")) {
        if (in == null)
          throw new IOException("Default versions.json not found in resources");
        Files.copy(in, versionsFile);

        Type type = new TypeToken<Map<String, Integer>>() {
        }.getType();

        InputStreamReader out = new InputStreamReader(in);
        this.versions = new Gson().fromJson(out, type);
      }
    }
  }

  public Config getConfig() {
    return this.config;
  }

  public Map<String, Integer> getVersions() {
    return versions;
  }

  public static class Config {
    public Map<String, ServerConfig> servers;
    public BehaviorConfig behavior;
    public MessagesConfig messages;
  }

  public static class ServerConfig {
    public boolean allow_all = true;
    public String min_version;
    public String max_version;
    public Integer min_protocol;
    public Integer max_protocol;
  }

  public static class BehaviorConfig {
    public String on_denied;
    public String redirect_target;
  }

  public static class MessagesConfig {
    public String redirect;
    public String denied;
  }
}
