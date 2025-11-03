# hibernate_bang

Un proyecto en Java que simula el juego de mesa Bang! usando Hibernate y MySQL.  
Desde la consola puedes añadir jugadores y jugar.

## 📋 Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Tech Stack](#tech-stack)
- [Project structure](#project-structure)

## ℹ️ Project Information

- **👤 Author:** [@albaboo](https://www.github.com/albaboo)

## Features

- Añadir, listar y eliminar jugadores.
- Asignar roles a los jugadores
- Gestión de cartas: robar, descartar, equipar armas y equipamientos.
- Control de estado de los jugadores y de las partidas.
- Comprobación de victoria y finalización de la partida.

## Installation

- Clona el repositorio:<br><br><pre>```git clone https://github.com/albaboo/hibernate_bang.git```</pre>
- Abre el proyecto en tu IDE.
- Asegúrate de tener Maven instalado y que descargue las dependencias.
- Configura la base de datos MySQL según el persistence.xml (nombre, usuario, contraseña).

## Usage

- Añadir jugadores
- Iniciar partida
- Jugar por turnos

## Tech Stack

- **Lenguaje:** Java  
- **Persistencia:** Hibernate (JPA)  
- **Base de datos:** MySQL
- **Gestión de dependencias y compilación:** Maven

## Project structure

hibernate_bang/  

> src/  
> > main/  
> > > java/ hibernate/projects/  
> > > > Entity/ # Clases de entidad  
> > > > Controller/ # Lógica de juego y DAO  
> > > > Main.java # Clase principal
> > > > 
> > > resources/
> > > > persistence.xml
>
> pom.xml   
> README.md  
  

