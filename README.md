# Gastos (Android)

App personal para registrar gastos escribiendo frases como `almuerzo 25 mil` o `uber 18.500`.
Detecta el monto, sugiere la categoría y muestra el resumen del mes. Datos 100 % locales.

## Etapa 1 (esta versión)
- Registro de gastos por texto (montos: `25 mil`, `18.500`, `12k`, `1.5 millones`)
- Categorización automática por palabras clave (sin internet)
- Categorías personalizadas
- Total del mes y totales por categoría
- Pruebas unitarias del parser, categorizador y estadísticas

## Cómo obtener el APK con GitHub Actions
1. Crea una cuenta en github.com y un repositorio nuevo (privado está bien).
2. Sube TODO el contenido de esta carpeta al repositorio (incluida la carpeta oculta `.github`).
   - Si tu computador no muestra `.github`, en GitHub usa *Add file > Create new file*, escribe
     `.github/workflows/build.yml` como nombre y pega el contenido de ese archivo.
3. Ve a la pestaña **Actions**. El flujo "Compilar APK" arranca solo (o ejecútalo con *Run workflow*).
4. Cuando termine (unos 5 a 8 minutos la primera vez), abre la ejecución y descarga el artefacto
   **gastos-debug-apk** (es un .zip que contiene `app-debug.apk`). El artefacto **reporte-pruebas**
   trae el resultado de las pruebas.
5. Pasa el APK al celular, ábrelo y acepta instalar desde "fuentes desconocidas".

Si la compilación falla, copia el error del log (paso en rojo) y pásamelo.
