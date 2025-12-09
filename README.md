# ViaVersionControl


A small Velocity plugin to enforce per-backend-server version rules and optionally redirect players who don't match.


## Included
- `build.gradle` (Gradle + ShadowJar)
- `src/main/java/.../ViaVersionControl.java`
- `src/main/resources/config.yml`
- `src/main/resources/versions.json`


## Build
1. Install JDK 17 or later.
2. Run `./gradlew --refresh-dependencies build`.
3. Grab `build/libs/ViaVersionControl-<version>.jar` and drop it into your Velocity `plugins/` folder.
4. Restart Velocity.


## Configuration
Edit `config.yml` inside the JAR or copy the file out to your own management system. The plugin supports semantic `min_version` or numeric `min_protocol` as well as `max_version` or numeric `max_protocol` checks per-server.