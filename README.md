# Kotlin Exercise — Estandarización de proyecto

Proyecto Kotlin mínimo (**Tengo Ansiedad**): contador de episodios para pacientes. Demuestra arquitectura por capas, design system, calidad automatizada y reglas para asistentes de IA.

Autor: Eliseo

## Qué es y qué entrega

App dummy de una pantalla — contador + botón rojo "Tengo ansiedad". El foco no es la feature, sino dejar un repo donde cualquier persona (o una IA) contribuya con las mismas reglas.

| Entregable | Dónde |
|------------|-------|
| CI/CD (push/PR) | `.github/workflows/ci.yml` |
| Reglas para IA y devs | `AGENTS.md` |
| App + arquitectura | `:app`, `:domain`, `:data`, `:design-system` |

## Arquitectura

```
app            → Compose, ViewModels (presentación)
design-system  → Tema, tokens, componentes App*
domain         → Repositorios (interfaces), casos de uso
data           → Implementaciones de repositorios
```

**Clean Architecture + MVVM**: la UI observa `StateFlow<UiState>`; la lógica vive en casos de uso; los datos solo se acceden vía interfaces.

Elegí **Android nativo** (no KMP): la prueba pide estandarizar un proyecto Android con Compose. KMP añadiría `commonMain`/`androidMain` sin aportar valor aquí. `:domain` y `:data` son Kotlin JVM puro; `:app` es el host Android.

Los límites entre capas no dependen de disciplina manual: si `domain` importa `data`, **Gradle falla**. Por eso cada capa es un módulo separado con su propio `build.gradle.kts`, en lugar de paquetes sueltos dentro de un solo módulo.

## Herramientas y por qué estas

### Gradle + version catalog (`libs.versions.toml`)

Es el build system estándar en Kotlin/Android. Centralizo versiones en un solo archivo para que CI y local usen exactamente lo mismo — sin versiones duplicadas entre módulos.

| Herramienta | Versión | Motivo |
|-------------|---------|--------|
| Kotlin | 2.1.21 | Rama estable 2.1.x |
| AGP | 8.7.2 | Máximo fully supported con Kotlin 2.1.21 |
| Gradle | 8.11.1 | Dentro del rango oficial para 2.1.x |
| JDK | 17 | Requerido por AGP 8.x |

No piné "la última de todo": prioricé combinación [oficialmente compatible](https://kotlinlang.org/docs/gradle-configure-project.html) y reproducible en CI.

### Design system (`:design-system`)

La prueba exige un design system para la UI. Lo aislé en módulo propio — no como paquete dentro de `:app` — para que colores, tipografía y componentes (`AppAnxietyButton`, `AppSpacing`) no se mezclen con lógica de pantalla. Material 3 es la base; encima va una capa `App*` con identidad propia.

### Ktlint + `.editorconfig`

Ktlint cubre formato y convenciones de estilo en Kotlin. Lo integré en Gradle y CI porque el estilo no debería depender de que alguien se acuerde de formatear antes del merge. Es el equivalente práctico a ESLint + Prettier en ecosistemas JS.

### Detekt (`config/detekt/detekt.yml`)

Ktlint no detecta code smells ni complejidad. Detekt sí. Corre solo en el gate de `main` para no frenar pushes diarios a `dev`.

### Tests + JaCoCo

Tests en `:domain` (casos de uso) y `:data` (repositorio) — capas con lógica real, sin emulador. JaCoCo en `:domain` genera cobertura HTML. No usé SonarCloud: Detekt + Lint + JaCoCo cubren calidad sin cuenta externa ni tokens en GitHub.

### GitHub Actions — dos jobs

| Job | Cuándo | Qué |
|-----|--------|-----|
| `fast-feedback` | Todo push/PR | Ktlint + tests `:domain` y `:data` |
| `qa-gate` | Solo `main` | Detekt, Lint, JaCoCo, build APK |

Un solo `gradlew build` en cada push sería más simple, pero mezclaría feedback rápido con QA pesado. Separar jobs permite iterar en `dev` sin esperar Lint ni Detekt, y garantizar que `main` siempre pasa el gate completo.

### `AGENTS.md`

Un solo archivo con reglas de arquitectura, design system y convenciones. Lo duplicar en varios sitios (skills, reglas de IDE, README) genera divergencia con el tiempo. El CI hace cumplir las reglas; `AGENTS.md` las explica.

## Ejecutar localmente

Requisitos: JDK 17+, Android SDK.

```bash
./gradlew ktlintCheck :domain:test :data:test
./gradlew detekt              # antes de merge a main
./gradlew assembleDebug       # compilar app
```

## Estructura

```
├── AGENTS.md              # Reglas (única fuente de verdad)
├── .github/workflows/     # CI/CD
├── config/detekt/         # Reglas Detekt
├── .editorconfig          # Reglas Ktlint
├── app/                   # Presentación
├── domain/                # Dominio (JVM)
├── data/                  # Datos (JVM)
├── design-system/         # UI reutilizable
└── gradle/libs.versions.toml
```
