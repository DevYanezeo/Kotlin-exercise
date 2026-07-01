# Kotlin Exercise — Estandarización de proyecto

Proyecto Kotlin mínimo (**Mis Tareas** — lista de tareas con agregar y marcar completadas) que demuestra **arquitectura por capas**, **design system**, **Ktlint** y **CI/CD con GitHub Actions**.

Autor: Eliseo

## Objetivo de la prueba — ¿se cumple?

> Establecer las bases de estandarización para que cualquier desarrollador (o asistente de IA) contribuya siguiendo las mismas reglas.

| Requisito | Cómo se cumple |
|-----------|----------------|
| CI/CD en cada push/PR | `.github/workflows/ci.yml` (fast-feedback + qa-gate) |
| Arquitectura con capas claras | 4 módulos Gradle + dependencias enforced en build |
| Design system obligatorio | Módulo `:design-system` con tema y componentes `App*` |
| Convenciones y patrones | `AGENTS.md` (fuente única de verdad) |
| Reglas para asistentes de IA | `AGENTS.md` |
| README con decisiones | Este archivo |

## Qué incluye este repositorio

| Entregable | Ubicación |
|------------|-----------|
| Pipeline CI/CD | `.github/workflows/ci.yml` |
| Reglas para asistentes de IA | `AGENTS.md` (única fuente de verdad) |
| App dummy | Módulos `:app`, `:domain`, `:data`, `:design-system` |

## Arquitectura

```
app (presentación)     → Compose UI, ViewModels
design-system          → Tema, tipografía, componentes App*
domain                 → Modelos, repositorios (interfaces), casos de uso
data                   → Implementación de repositorios
```

**Clean Architecture + MVVM**: la UI observa un `StateFlow<UiState>`; la lógica vive en casos de uso del dominio; los datos se acceden solo vía interfaces de repositorio.

### Android nativo (no KMP)

Intención deliberada: **app Android pura**, no Kotlin Multiplatform.

- `:app` es el **host Android** (application module).
- `:domain` y `:data` son módulos **Kotlin JVM** (`src/main/kotlin`, sin `commonMain`).
- `:design-system` es **Android library** con Compose (requiere SDK Android).

Esto simplifica la prueba de estandarización. Los límites entre capas se imponen igual vía Gradle. Una migración futura a KMP movería `domain`/`data` a `commonMain`.

## Decisiones técnicas

### Kotlin + Gradle (Kotlin DSL)

- **Por qué**: es el estándar de la industria para proyectos Kotlin/Android.
- **Version catalog** (`gradle/libs.versions.toml`): centraliza versiones de dependencias y evita duplicación entre módulos.

### Versiones del toolchain (estables y compatibles)

Versiones fijadas en `gradle/libs.versions.toml` y `gradle-wrapper.properties`, alineadas con la [matriz oficial de compatibilidad](https://kotlinlang.org/docs/gradle-configure-project.html) de JetBrains:

| Herramienta | Versión | Notas |
|-------------|---------|-------|
| Kotlin | **2.1.21** | Rama 2.1.x estable (bugfix de 2.1.20) |
| Android Gradle Plugin | **8.7.2** | Máximo fully supported con Kotlin 2.1.21 |
| Gradle (wrapper) | **8.11.1** | Dentro del rango soportado para Kotlin 2.1.x |
| JDK | **17** | Requerido por AGP 8.x |

**Por qué no la última de todo**: se prioriza combinación **oficialmente compatible** y reproducible en CI, no bleeding edge. El evaluador puede verificar versiones en un solo archivo (`libs.versions.toml`).

### Multi-módulo

- **Por qué**: fuerza límites entre capas en tiempo de compilación. Si `domain` importa Android, el build falla.
- Cada capa tiene un `build.gradle.kts` propio con dependencias mínimas.

### Jetpack Compose + módulo `design-system`

- **Por qué**: la prueba pide un design system obligatorio para la UI. Un módulo dedicado con `AppTheme`, tokens y componentes (`AppCard`, `AppPrimaryButton`) garantiza consistencia visual y evita estilos ad-hoc en pantallas.

### Ktlint (`org.jlleitschuh.gradle.ktlint`)

- **Por qué**: formateo y estilo automáticos, integración simple con Gradle y CI.
- **Equivalente en JS**: ESLint + Prettier (Ktlint cubre estilo; no reemplaza análisis profundo).
- Comandos:
  - `./gradlew ktlintCheck` — verifica estilo (usado en CI)
  - `./gradlew ktlintFormat` — corrige formato automáticamente

### Detekt (análisis estático)

- **Por qué**: detecta code smells, complejidad y malas prácticas (más allá del formato).
- **Equivalente en JS**: ESLint con reglas de calidad.
- Configuración: `config/detekt/detekt.yml`
- Comando: `./gradlew detekt` (solo en QA gate hacia `main`)

### Tests y cobertura

- Tests unitarios en `:domain` (casos de uso) y `:data` (repositorio).
- **JaCoCo** en `:domain` genera reporte HTML/XML de cobertura.
- Comandos:
  - `./gradlew :domain:test :data:test`
  - `./gradlew :domain:jacocoTestReport`

### GitHub Actions — estrategia por ambiente

Dos jobs en `.github/workflows/ci.yml`:

| Job | Cuándo corre | Qué hace |
|-----|--------------|----------|
| **fast-feedback** | Todo push/PR a `dev` o `main` | Ktlint + tests `:domain` y `:data` (~rápido) |
| **qa-gate** | Solo PR → `main` o push a `main` | Detekt, tests `:app`, JaCoCo, Android Lint, build APK |

**Flujo de ramas propuesto:**

```
dev  → trabajo diario, CI rápido (sin QA pesado)
main → producción, QA completo antes de mergear
```

Así no bloqueas a quien pushea a `dev`, pero **main siempre pasa el gate de calidad**.

**Por qué Gradle Setup Action**: cache de dependencias y builds más rápidos en CI.

### Calidad sin servicios externos

Detekt + Ktlint + Android Lint + JaCoCo cubren estilo, análisis estático, lint de Android y cobertura **sin depender de SonarCloud ni otros SaaS**. Menos configuración para la prueba, mismo rigor en CI.

### Reglas para IA

- **`AGENTS.md`**: único archivo con reglas para desarrolladores y asistentes de IA (arquitectura, design system, convenciones, patrones).

El CI **hace cumplir** las reglas (Ktlint, Detekt, tests, Lint). `AGENTS.md` las **documenta**.

### Carpeta `config/`

Configuración de herramientas de calidad, separada del código fuente:

| Ruta | Herramienta | Propósito |
|------|-------------|-----------|
| `config/detekt/detekt.yml` | [Detekt](https://detekt.dev/) | Análisis estático (complejidad, estilo, naming) |
| `.editorconfig` (raíz) | Ktlint | Estilo de formato (indentación, reglas Compose) |

Ktlint no vive en `config/` porque lee `.editorconfig` desde la raíz por convención.

## Cómo ejecutar localmente

Requisitos: JDK 17+, Android SDK (para compilar `:app`).

```bash
./gradlew ktlintCheck
./gradlew :domain:test :data:test
./gradlew detekt                    # QA (antes de merge a main)
./gradlew :domain:jacocoTestReport  # cobertura
./gradlew assembleDebug
```

En Windows (PowerShell):

```powershell
.\gradlew.bat ktlintCheck
.\gradlew.bat :domain:test :data:test
```

## Estructura de carpetas

```
Kotlin-exercise/
├── AGENTS.md               # Reglas para devs y asistentes de IA
├── .github/workflows/      # CI/CD
├── config/detekt/          # Config Detekt (análisis estático)
├── .editorconfig           # Config Ktlint (estilo)
├── app/                    # Host Android — presentación
├── data/                   # Kotlin JVM — datos
├── design-system/          # Android library — UI
├── domain/                 # Kotlin JVM — dominio
└── gradle/libs.versions.toml
```

## Glosario rápido (si nunca usaste Kotlin)

| Concepto | Qué es |
|----------|--------|
| **Kotlin** | Lenguaje moderno que corre en JVM/Android; sintaxis concisa y segura ante nulos |
| **Gradle** | Herramienta de build (como npm para Node o Maven para Java) |
| **Ktlint** | Linter/formatter de estilo para Kotlin |
| **Detekt** | Análisis estático (code smells, complejidad) |
| **Compose** | Framework declarativo de UI (similar a React/SwiftUI) |
| **Use case** | Clase con una responsabilidad de negocio (`GetTasksUseCase`, `AddTaskUseCase`) |
| **ViewModel** | Mantiene estado de pantalla y sobrevive a rotaciones |
| **StateFlow** | Flujo reactivo de estado que la UI observa |
