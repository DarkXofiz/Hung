# hung

Elytra ile ucarken havai fisek elindeyken oyuncuya vurunca kilic slotuna gecip geri donen Fabric client modu.

Surumler: Minecraft 1.20.1, 1.21 - 1.21.1, 1.21.4 (her biri icin ayri jar: Actions > Artifacts)

## Config
`config/hung.json`: enabled, onlyWhileElytra, onlyPlayers, requireFirework. soundEnabled, soundSet (1-6). autoAttack, autoAttackRange (1-3). Toggle tusu: R. Oto vurus tusu: X. Ses secme tusu: V (6 set: Crystal, Bubble, Marimba, Pad, Harp, Sparkle).

## Build (yerel)
`gradle/wrapper/gradle-wrapper.jar` dosyasini Gradle 8.11 wrapper'indan koy, sonra:
```
./gradlew clean build
./gradlew clean build -Pminecraft_version=1.20.1 -Pyarn_mappings=1.20.1+build.10 -Pfabric_version=0.92.2+1.20.1 -Pjava_version=17
```

## CI
Her push ve PR her surum icin jar uretir (Actions > Artifacts). `v1.0.0` gibi bir tag push edince jar'lar otomatik Release olarak yuklenir.
