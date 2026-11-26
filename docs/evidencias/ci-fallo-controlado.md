# Fallo controlado del CI

Registro para la práctica DevOps. El cambio está en el pull request [#1](https://github.com/L4RV0/campus-accesible/pull/1), rama `feature/HU-03-ruta-laboratorios`.

## Qué hicimos

1. Agregamos una prueba de integración para HU-03: recorrido sin escaleras de la Entrada principal a Laboratorios. A propósito, la prueba esperaba la ruta por la Cafetería (Entrada → Biblioteca → Bloque A → Cafetería → Laboratorios, 340 m).
2. El CI falló. La salida dice que esperaba `["ENT", "BIB", "BLA", "CAF", "LAB"]` y recibió otra ruta: la aplicación encontró una más corta por el Bloque B.
3. Revisamos el mapa: por el Bloque B son 120 + 90 + 110 = 320 m, y tampoco hay escaleras. El código estaba bien y la equivocada era la expectativa de la prueba. Corregimos la prueba, no el planificador.
4. El CI volvió a verde.

| Momento | Commit | Ejecución | Resultado |
|---------|--------|-----------|-----------|
| Prueba con expectativa equivocada | `3bb921e` | [45785256374](https://github.com/L4RV0/campus-accesible/actions/runs/45785256374) | ❌ 20 pruebas, 1 falla |
| Expectativa corregida | `1c4def2` | [45785386897](https://github.com/L4RV0/campus-accesible/actions/runs/45785386897) | ✅ 20 pruebas, 0 fallas |

Para el informe hay que tomar captura de las dos ejecuciones en la pestaña Actions y de la lista de checks del PR.

## Qué comprueba cada paso del pipeline

| Paso | Para qué sirve |
|------|----------------|
| Descargar el código | Trae exactamente el commit que se integró, no lo que alguien tenga en su máquina. |
| Instalar Java 21 | Usa la misma versión que pide el README, en una máquina limpia. Así se detecta si algo solo funcionaba en el equipo de alguien. |
| Compilar y ejecutar las pruebas (`mvnw verify`) | Compila, corre las pruebas de las reglas, la integración por HTTP y ArchUnit, genera la cobertura y empaqueta el `.jar`. Si una sola prueba falla, el job queda en rojo y el PR muestra que no se debe integrar. |
| Publicar reporte de pruebas | Deja el detalle de cada prueba para revisar qué falló sin tener que repetir la ejecución. Se publica incluso cuando el job falla. |
| Publicar reporte de cobertura | Deja el HTML de JaCoCo para ver qué líneas quedaron sin ejercitar. |
| Guardar el `.jar` | El artefacto construido es el que se desplegaría. |

## Lo que aprendimos

La prueba falló porque supusimos un camino mirando el mapa, sin sumar las distancias. Una prueba en rojo no siempre significa que el código esté mal. Antes de «arreglar» el planificador hay que comprobar cuál de los dos tiene razón; si hubiéramos cambiado el código para que pasara la prueba, habríamos metido un error.
