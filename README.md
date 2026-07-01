# Tengo Ansiedad

App Android para que pacientes registren episodios de ansiedad con un solo gesto: un contador y un botón.

Autor: Eliseo

## La app

Pantalla única. El paciente pulsa **"Tengo ansiedad"** y el contador sube. Sin formularios ni navegación — la interacción tiene que ser inmediata cuando lo necesitan.

Stack: Kotlin, Jetpack Compose, Clean Architecture en módulos Gradle separados.

## Arquitectura

```
app            → Compose, ViewModels (presentación)
design-system  → Tema, tokens, componentes App*
domain         → Repositorios (interfaces), casos de uso
data           → Implementaciones de repositorios
```

La UI observa `StateFlow<UiState>`. La lógica vive en casos de uso; los datos solo se acceden vía interfaces de repositorio.

Elegí **Android nativo** en lugar de KMP: el alcance actual es una app Android con Compose. KMP añadiría `commonMain`/`androidMain` sin beneficio claro por ahora. `:domain` y `:data` son Kotlin JVM puro; `:app` es el host.

Cada capa es un módulo Gradle con su propio `build.gradle.kts`. Si `domain` intenta importar `data`, el build falla — los límites no dependen de que alguien recuerde la regla.

## Herramientas

### Gradle + version catalog (`libs.versions.toml`)

Build system estándar en Kotlin/Android. Las versiones viven en un solo archivo para que local y CI usen lo mismo.

| Herramienta | Versión | Motivo |
|-------------|---------|--------|
| Kotlin | 2.1.21 | Rama estable 2.1.x |
| AGP | 8.7.2 | Compatible con Kotlin 2.1.21 |
| Gradle | 8.11.1 | Dentro del rango oficial para 2.1.x |
| JDK | 17 | Requerido por AGP 8.x |

Prioricé combinación [oficialmente compatible](https://kotlinlang.org/docs/gradle-configure-project.html) sobre usar siempre la última versión de cada herramienta.

### Design system (`:design-system`)

Colores, tipografía y componentes (`AppAnxietyButton`, `AppSpacing`) viven en módulo propio, no dentro de `:app`. Así la pantalla no acumula estilos ad-hoc. Material 3 es la base; encima va la capa `App*`.

### Ktlint + `.editorconfig`

Formato y convenciones automáticos, integrados en Gradle y CI. El estilo no debería depender de formatear a mano antes de cada merge.

### Detekt (`config/detekt/detekt.yml`)

Ktlint no cubre code smells ni complejidad. Detekt corre en el gate de `main`, no en cada push a `dev`.

### Tests + JaCoCo

Tests en `:domain` y `:data` — lógica de negocio sin emulador. JaCoCo genera cobertura HTML en `:domain`. Descarté SonarCloud: Detekt + Android Lint + JaCoCo cubren lo necesario sin servicio externo.

### GitHub Actions

| Job | Cuándo | Qué |
|-----|--------|-----|
| `fast-feedback` | Todo push/PR | Ktlint + tests `:domain` y `:data` |
| `qa-gate` | Solo `main` | Detekt, Lint, JaCoCo, build APK |

Un solo `gradlew build` en cada push es más simple, pero mezcla feedback rápido con QA pesado. Dos jobs permiten iterar en `dev` sin esperar Lint ni Detekt.

### `AGENTS.md`

Reglas de arquitectura, design system y convenciones en un solo archivo. Duplicarlas en README, skills o reglas de IDE genera divergencia. El CI las hace cumplir; `AGENTS.md` las documenta.

## Ejecutar localmente

JDK 17+ y Android SDK.

```bash
./gradlew ktlintCheck :domain:test :data:test
./gradlew detekt              # antes de merge a main
./gradlew assembleDebug
```

## Estructura del repo

```
├── AGENTS.md              # Convenciones de contribución
├── .github/workflows/     # CI/CD
├── config/detekt/
├── .editorconfig
├── app/
├── domain/
├── data/
├── design-system/
└── gradle/libs.versions.toml
```
