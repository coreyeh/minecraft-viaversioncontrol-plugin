package me.azoaze.viaversioncontrol;

import java.util.Map;
import java.util.Optional;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.viaversion.viaversion.api.Via;

import net.kyori.adventure.text.Component;

public class ProxyConnectListener {
  private final ProxyServer proxy;
  private final ResourceManager resourceManager;

  public ProxyConnectListener(ProxyServer proxy, ResourceManager resourceManager) {
    this.proxy = proxy;
    this.resourceManager = resourceManager;
  }

  @Subscribe
  public void onServerPreConnect(ServerPreConnectEvent event) {
    Optional<RegisteredServer> targetServer = event.getResult().getServer();
    if (targetServer.isEmpty())
      return;

    RegisteredServer server = targetServer.get();
    String serverName = server.getServerInfo().getName();

    ResourceManager.Config config = resourceManager.getConfig();
    ResourceManager.ServerConfig serverConfig = config.servers.get(serverName);

    if (serverConfig == null || serverConfig.allow_all)
      return;

    Map<String, Integer> versions = resourceManager.getVersions();

    Player player = event.getPlayer();
    int playerProtocol = Via.getAPI().getPlayerVersion(player.getUniqueId());

    int minProtocol = resolveServerMinProtocol(serverConfig, versions);
    int maxProtocol = resolveServerMaxProtocol(serverConfig, versions);

    if (playerProtocol >= minProtocol && playerProtocol <= maxProtocol)
      return;

    Boolean playerDenied = config.behavior.on_denied.equalsIgnoreCase("denied");

    if (playerDenied) {
      event.setResult(ServerPreConnectEvent.ServerResult.denied());
      player.sendMessage(Component.text(config.messages.denied.replace("{min_version}", serverConfig.min_version).replace("{server}", serverName)));
      return;
    }
    Optional<RegisteredServer> targetRedirect = proxy.getServer(config.behavior.redirect_target);
    targetRedirect.ifPresent(redirect -> event.setResult(ServerPreConnectEvent.ServerResult.allowed(redirect)));

    player.sendMessage(
        Component.text(config.messages.redirect.replace("{min_version}", serverConfig.min_version).replace("{server}", serverName).replace("{target}", config.behavior.redirect_target)));
  }

  private int resolveServerMinProtocol(ResourceManager.ServerConfig serverConfig, Map<String, Integer> versions) {
    return serverConfig.min_protocol != null ? serverConfig.min_protocol : versions.getOrDefault(serverConfig.min_version, -1);
  }

  private int resolveServerMaxProtocol(ResourceManager.ServerConfig serverConfig, Map<String, Integer> versions) {
    return serverConfig.max_protocol != null ? serverConfig.max_protocol : versions.getOrDefault(serverConfig.max_version, Integer.MAX_VALUE);
  }
}
