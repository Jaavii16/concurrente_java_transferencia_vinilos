# Aplicación Concurrente Cliente-Servidor (Java)

Proyecto de la asignatura Programación Concurrente. Es una aplicación de escritorio en Java que implementa un sistema de transferencia de "vinilos" (archivos/datos) entre múltiples clientes a través de un servidor central.

El proyecto destaca por su implementación de conceptos de bajo nivel de redes, concurrencia y sincronización.

## Características Principales

### 1. Arquitectura Cliente-Servidor
La aplicación opera sobre un modelo Cliente-Servidor clásico:
* **Servidor:** Una aplicación de consola (`Servidor`) que gestiona conexiones entrantes, mantiene el estado y coordina a los clientes. Utiliza hilos (`OyenteCliente`) para manejar a múltiples clientes de forma concurrente.
* **Cliente:** Una aplicación de escritorio (`Cliente`) con una GUI en Java Swing que permite a los usuarios interactuar con el sistema.

### 2. Protocolo de Red Personalizado (Sockets)
La comunicación no utiliza librerías de alto nivel (como HTTP), sino que se basa directamente en **Sockets de Java**.
Se ha diseñado un protocolo de mensajería serializable (`Mensajes`) para gestionar la comunicación.

### 3. Gestión Avanzada de Concurrencia
Esta es la parte central del proyecto. La concurrencia se gestiona tanto en el servidor (múltiples clientes) como en el cliente (productores/consumidores, listeners de GUI).
* **Hilos de Cliente:** La aplicación cliente utiliza múltiples hilos y monitores (`OyenteServidor`, `OyenteP2P`, `Productor`, `Receptor`, `Monitor`) para gestionar la E/S de red y las actualizaciones de la GUI sin bloquear el hilo principal.
* **Algoritmos de Sincronización (Locks):** En lugar de usar solo el `synchronized` de Java, se han implementado **algoritmos clásicos de exclusión mutua** desde cero para gestionar el acceso a recursos críticos.

### 4. Interfaz Gráfica (GUI)
El cliente cuenta con una interfaz gráfica de usuario desarrollada con **Java Swing** (en la carpeta `Resto`), que permite la interacción del usuario para añadir, eliminar y ver vinilos.

## Tecnologías Aplicadas
* **Lenguaje:** Java
* **Redes:** Java Sockets
* **Concurrencia:** Multi-threading, Sincronización, Monitores, Patrón Productor-Consumidor.
* **Algoritmos:** Algoritmo de Ticket, Algoritmo de Panadería de Lamport.
* **GUI:** Java Swing
