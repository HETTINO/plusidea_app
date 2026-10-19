# Plusidea

Aplicación Android para **creadores de contenido** que permite planificar y organizar sus grabaciones: proyectos, empresas con las que trabajan, locaciones y recordatorios antes de cada sesión.

## Funcionalidades

- **Proyectos**: crear, editar, ver el detalle y eliminar proyectos de contenido (Reel, Video largo, Foto/Post, Historia, Unboxing).
- **Datos de cada proyecto**: guion, materiales, tiempo estimado, outfit, pago acordado, enlace de inspiración, fecha y hora de grabación.
- **Empresas**: gestión de las marcas o clientes asociados a cada proyecto.
- **Locaciones**: gestión de los lugares de grabación con su dirección.
- **Recordatorios**: notificaciones programadas con WorkManager antes de la grabación.
- **Ajustes**: tema oscuro y activación de notificaciones, guardados con DataStore.
- **Pantalla de bienvenida** al abrir la app por primera vez.

## Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje | Kotlin |
| Interfaz | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose |
| Base de datos local | Room (con KSP) |
| Inyección de dependencias | Hilt (`hilt-navigation-compose`, `hilt-work`) |
| Tareas en segundo plano | WorkManager |
| Preferencias | DataStore Preferences |
| Arquitectura | MVVM por capas (UI → Dominio → Datos) |

## Requisitos

- Android Studio (versión reciente con soporte para AGP y Compose)
- JDK 17 o superior (el que trae Android Studio)
- Dispositivo o emulador con **Android 12 (API 31)** o superior
- Permiso `POST_NOTIFICATIONS` para recibir recordatorios (Android 13+)

## Cómo ejecutar el proyecto

```bash
git clone https://github.com/HETTINO/plusidea_app.git
cd plusidea_app
```

1. Abre la carpeta en Android Studio.
2. Espera a que termine la sincronización de Gradle.
3. Selecciona un emulador o dispositivo y pulsa **Run ▶**.

## Estructura del proyecto

```
app/src/main/java/com/example/plusidea/
├── data/
│   ├── local/           # AppDatabase, entidades y DAOs (Room)
│   ├── mapper/          # Conversión entidad ↔ modelo de dominio
│   ├── repository/      # Interfaces e implementaciones de repositorios
│   ├── notifications/   # RecordatorioScheduler y RecordatorioWorker
│   └── di/              # Módulos de Hilt
├── domain/
│   └── model/           # Proyecto, Empresa, Locacion, TipoContenido
└── ui/
    ├── bienvenida/      # Pantalla de bienvenida
    ├── proyectos/       # Lista, detalle y formulario de proyectos
    ├── empresas/        # Gestión de empresas
    ├── locaciones/      # Gestión de locaciones
    ├── ajustes/         # Tema y notificaciones
    ├── navigation/      # NavHost y rutas
    ├── components/      # Componentes reutilizables
    ├── theme/           # Colores, tipografía y tema
    └── util/            # Utilidades de fechas
```

## Base de datos

Room, con tres tablas:

- `empresas` (`id`, `nombre`)
- `locaciones` (`id`, `nombre`, `direccion`)
- `proyectos` (`id`, `titulo`, `tipoContenido`, `empresaId`, `locacionId`, `guion`, `materiales`, `tiempoEstimado`, `outfit`, `pagoAcordado`, `enlaceInspiracion`, `fechaGrabacion`, `horaInicio`)

`proyectos` se relaciona con `empresas` y `locaciones` mediante `empresaId` y `locacionId`.

## Flujo de trabajo en equipo

- La rama `main` contiene la versión estable.
- Cada funcionalidad se desarrolla en su propia rama (`feature/...`).
- Los cambios llegan a `main` mediante **Pull Request** revisado por la otra integrante.
- No se suben archivos de `.idea/` ni de `build/`.

## Autoras

- **Cristina Cedeño** — [@HETTINO](https://github.com/HETTINO)
- **Shirley Cedeño** — [@Shili-2302](https://github.com/Shili-2302)
