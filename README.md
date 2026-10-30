# Trabajo Práctico – Algoritmo Evolutivo para ATSP

Implementación en **Java** de un **algoritmo evolutivo** para resolver el **Problema del Viajante Asimétrico (ATSP)**. El programa ejecuta experimentos con distintas configuraciones sobre instancias de [TSPLIB](http://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/) y exporta los resultados a un archivo CSV.

## Descripción del problema

En el ATSP, el costo de viajar de la ciudad *i* a la *j* puede ser distinto del de *j* a *i*. El objetivo es encontrar el ciclo hamiltoniano de costo mínimo que visite cada ciudad exactamente una vez y regrese a la ciudad de origen.

## Instancias utilizadas

| Instancia | Ciudades | Óptimo conocido |
|-----------|----------|-----------------|
| br17      | 17       | 39              |
| p43       | 43       | 5620            |

## Algoritmo implementado

- **Representación:** permutación de ciudades
- **Inicialización:** [aleatoria]
- **Selección:** [torneo / ruleta]
- **Cruce:** [OX / PMX]
- **Mutación:** [swap / inversión]
- **Reemplazo:** [generacional / generacional con elitismo]
- **Criterio de parada:** [cantidad de generaciones / estancamiento]
- **Fitness:** costo total del recorrido (a minimizar), respetando la asimetría de la matriz de costos

## Requisitos

- Java instalado en el sistema

Se puede verificar con:

```bash
java -version
```

## Archivos necesarios

Para ejecutar el programa deben estar en la misma carpeta los siguientes archivos:

- `TP_ATSP.jar`
- `br17.atsp`
- `p43.atsp`

## Ejecución del programa

1. Abrir una terminal o consola en la carpeta donde se encuentran los archivos.
2. Ejecutar el siguiente comando:

   ```bash
   java -jar TP_ATSP.jar
   ```

3. El programa comenzará a ejecutar automáticamente los experimentos sobre las instancias del problema ATSP. No requiere argumentos ni intervención del usuario.

> Según el tamaño de la instancia y la cantidad de configuraciones, la ejecución puede demorar [tiempo aproximado].

## Configuraciones evaluadas

Cada configuración combina distintos valores de:

- **Población:** [50, 100]
- **Probabilidad de cruce:** [0.7, 0.9]
- **Probabilidad de mutación:** [0.01, 0.05]
- **Corridas independientes por configuración:** [1000]

## Salida del programa

Al finalizar la ejecución se generará el siguiente archivo:

- `resultados.csv`: contiene los resultados de cada configuración evaluada.

### Columnas del CSV

| Columna | Descripción |
|---------|-------------|
| `Archivo` | Instancia utilizada (br17.atsp, p43.atsp) |
| `Configuracion` | Identificador de la configuración evaluada |
| `Poblacion` | Tamaño de la población |
| `ProbCruce` | Probabilidad de cruce |
| `ProbMutacion` | Probabilidad de mutación |
| `MejorCosto` | Menor costo encontrado entre las corridas |
| `PromedioCosto` | Costo promedio de las corridas |
| `DesvioCosto` | Desvío estándar del costo |
| `TiempoTotal` | Tiempo total de ejecución de la configuración |
| `Recorrido` | Secuencia de ciudades de la mejor solución |
