package me.azoaze.viaversioncontrol;

import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import com.google.inject.Inject;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

@Plugin(id = "viaversioncontrol", name = "ViaVersionControl", version = "1.0.0", description = "Limits player connections based on client protocol version", authors = { "azoaze" })
public class ViaVersionControl {
  private final ProxyServer proxy;
  private final Logger logger;
  private final Path dataDirectory;
  private ResourceManager resourceManager;

  @Inject
  public ViaVersionControl(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
    this.proxy = proxy;
    this.logger = logger;
    this.dataDirectory = dataDirectory;
  }

  @Subscribe
  public void onProxyInit(ProxyInitializeEvent event) {
    try {
      resourceManager = new ResourceManager(dataDirectory);

      resourceManager.loadConfig();
      logger.info("[ViaVersionControl] Loaded config");

      resourceManager.loadVersions();
      logger.info("[ViaVersionControl] Loaded versions");
    } catch (IOException error) {
      logger.error("[ViaVersionControl] Failed to load resources");
    }
    proxy.getEventManager().register(this, new ProxyConnectListener(proxy, resourceManager));
  }

  public Logger getLogger() {
    return this.logger;
  }

  public ResourceManager getResourceManager() {
    return this.resourceManager;
  }
}
