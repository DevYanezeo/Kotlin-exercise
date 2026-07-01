# Guía para asistentes de IA y desarrolladores

**Fuente única de verdad** para contribuir a este repositorio.  
Humanos y asistentes de IA (Cursor, Copilot, Claude Code, etc.) deben seguir **solo este archivo**.

---

## Plataforma

Este proyecto es **Android nativo con Jetpack Compose**, no Kotlin Multiplatform (KMP).

| Módulo | Tipo | Source sets |
|--------|------|-------------|
| `:app` | Android application (host) | `src/main/` (Android) |
| `:domain` | Kotlin JVM puro | `src/main/`, `src/test/` |
| `:data` | Kotlin JVM puro | `src/main/`, `src/test/` |
| `:design-system` | Android library + Compose | `src/main/` (Android) |

No hay `commonMain`, `androidMain` ni `iosMain`. Si en el futuro se migra a KMP, `:domain` y `:data` pasarían a `commonMain` y `:design-system` quedaría en `androidMain` o como módulo CMP.

---

## Arquitectura obligatoria (Clean Architecture)

| Capa | Módulo | Responsabilidad |
|------|--------|-----------------|
| Presentación | `:app` | UI (Compose), ViewModels, estados de pantalla |
| Dominio | `:domain` | Modelos, interfaces de repositorio, casos de uso |
| Datos | `:data` | Implementaciones de repositorios y fuentes de datos |
| Design System | `:design-system` | Tema, tokens y componentes reutilizables de UI |

### Reglas de dependencia (enforced por Gradle)

- `:app` → `:domain`, `:data`, `:design-system`
- `:data` → solo `:domain`
- `:domain` → **sin** dependencias Android ni de otros módulos del proyecto
- `:design-system` → solo Compose / Material3

Si `domain` intenta importar `data`, **el build falla**. La arquitectura no depende de disciplina manual.

### Patrones permitidos

- MVVM (`ViewModel` + `StateFlow` / `UiState`)
- Repository pattern
- Use cases con `suspend operator fun invoke()`
- Inyección manual en `MainActivity` (dummy; en producción: Hilt/Koin)

### Patrones prohibidos

- Lógica de negocio en Composables o Activities
- Acceso directo a `:data` desde la UI (solo ViewModel → UseCase)
- Imports `android.*` en `:domain`
- **Wildcard imports** (`import foo.*`) — usar imports explícitos (Ktlint y Detekt lo rechazan)
- Colores o tipografías hardcodeados en `:app`
- God classes o archivos con múltiples responsabilidades

---

## Design System obligatorio

Motor: **Material 3** vía Jetpack Compose. Capa propia encima: `AppTheme`, `AppTypography`, `AppSpacing`, componentes `App*`.

Toda UI en `:app` usa `:design-system`:

```kotlin
// ❌ MAL
Button(onClick = {}) { Text("Guardar") }

// ✅ BIEN — componentes del design system
AppTaskRow(title = "Revisar PR", isCompleted = false, onToggle = {})
AppTextField(value = title, onValueChange = {}, label = "Nueva tarea")
AppPrimaryButton(text = "Agregar tarea", onClick = {})
```

Colores y tipografía solo desde `AppTheme`, `MaterialTheme` o `designsystem/theme/`.

---

## Convenciones de nombres

### General

- Clases/interfaces: `PascalCase`
- Funciones/propiedades: `camelCase`
- Paquetes: minúsculas (`com.eliseo.kotlinexercise.domain.usecase`)
- Un tipo público principal por archivo; nombre del archivo = nombre del tipo

### Dominio y datos

| Tipo | Patrón | Ejemplo |
|------|--------|---------|
| Caso de uso | `Get<Entidad>UseCase`, `Add<Entidad>UseCase`, `Toggle<Entidad>UseCase` | `GetTasksUseCase` |
| Interfaz repositorio | `<Entidad>Repository` | `TaskRepository` |
| Implementación | `<Fuente><Entidad>Repository` o `<Entidad>RepositoryImpl` | `InMemoryTaskRepository` |

```kotlin
// ❌ MAL — nombre genérico sin patrón
class GreetingLogic(...)

// ✅ BIEN
class GetGreetingUseCase(...)
```

### Presentación

| Tipo | Patrón | Ejemplo |
|------|--------|---------|
| Estado de pantalla | `<Feature>UiState` | `TodoUiState` |
| ViewModel | `<Feature>ViewModel` | `TodoViewModel` |
| Pantalla Compose | `<Feature>Screen` | `TodoScreen` |

---

## Estructura de carpetas

```
app/src/main/kotlin/.../presentation/<feature>/
  <Feature>Screen.kt
  <Feature>ViewModel.kt
  <Feature>UiState.kt

domain/src/main/kotlin/.../domain/
  model/
  repository/
  usecase/

data/src/main/kotlin/.../data/
  repository/

design-system/src/main/kotlin/.../designsystem/
  theme/
  components/
```

---

## Flujo para una feature nueva

1. `domain/model/` — entidad
2. `domain/repository/` — interfaz
3. `domain/usecase/` — caso de uso
4. `data/` — implementación del repositorio
5. `design-system/` — componentes reutilizables (si aplica)
6. `app/presentation/` — UiState, ViewModel, Screen
7. `domain/src/test/` — test del caso de uso

---

## Calidad antes de commit

```bash
./gradlew ktlintCheck          # verificar estilo
./gradlew ktlintFormat         # corregir formato automáticamente
./gradlew :domain:test :data:test
./gradlew detekt               # obligatorio antes de PR a main
```

- Preferir `data class` inmutables
- `suspend` en repositorios y casos de uso con I/O
- Errores en ViewModel con `runCatching`, mensaje en `UiState`
- Tests de dominio con JUnit + `kotlinx-coroutines-test`
- Estilo Ktlint: ver `.editorconfig` en la raíz

---

## Checklist de contribución

- [ ] Código en el módulo/capa correcto
- [ ] Sin lógica de negocio en la UI
- [ ] UI usa `AppTheme` y componentes `App*`
- [ ] Ktlint sin errores
- [ ] Test de dominio si hay lógica nueva
- [ ] CI en verde

---

## Referencias (no duplicar reglas aquí)

- Decisiones técnicas y CI/CD: [README.md](README.md)
- Config Detekt: [config/detekt/detekt.yml](config/detekt/detekt.yml)
