# 2022-SB1-GA-GYTMY
[![Java CI](https://github.com/mathusanMe/Amaze-Voice-Lab/actions/workflows/main.yml/badge.svg)](https://github.com/mathusanMe/Amaze-Voice-Lab/actions/workflows/main.yml)
- [2022-SB1-GA-GYTMY](#2022-sb1-ga-gytmy)
  - [Project: `Voice-controlled movement of characters`](#project-voice-controlled-movement-of-characters)
  - [Assigned professor](#assigned-professor)
  - [Students](#students)
  - [What is this project about ?](#what-is-this-project-about-)
  - [Game modes](#game-modes)
    - [`"Classic Mode"`](#classic-mode)
    - [`"Blackout Mode"`](#blackout-mode)
  - [Dependencies](#dependencies)
  - [How to install the dependencies](#how-to-install-the-dependencies)
  - [How to run the game](#how-to-run-the-game)

This project was realized by GYTMY for the unit `Projet de programmation (PI4)` during Year 2 Second Semester of `Double Licence Mathématiques-Informatique` (2022-2023) as a research project.

## Project: `Voice-controlled movement of characters`

## Assigned Researcher/Professor

Sami Boutamine (<sami.boutamine@utc.fr>)

## Project Context
Under the guidance of assigned researcher Professor Sami Boutamine, the project aligns with current academic research trends, emphasizing the practical application of theoretical concepts in computer science and mathematics. The project's development within a three-month timeframe demonstrates the ability to deliver sophisticated technological solutions in a condensed period, a hallmark of agile and effective research and development teams.

## Students

|    Last Name     | First Name | Group |                 Email                  |
| :--------------: | :--------: | :---: | :------------------------------------: |
|    DUDILLIEU     |   Gabin    |  MI2  |         <gdudillieu@gmail.com>         |
| IGLESIAS VAZQUEZ |    Yago    |  MI2  | <yago.iglesias-vazquez@etu.u-paris.fr> |
|       SOAN       |  Tony Ly   |  MI2  |     <tony-ly.soan@etu.u-paris.fr>      |
|    SELVAKUMAR    |  Mathusan  |  MI2  |    <mathusan.selvakumar@gmail.com>     |
|     LACENNE      |   Yanis    |  MI2  |     <yanis.lacenne@etu.u-paris.fr>     |

## What is this project about ?

The goal of this project is to be able to control the movements of characters in a Maze game using real-time voice commands such as saying out loud `Up`, `Down`, `Left` or `Right`.

## Game modes

### `"Classic Mode"`

![Image Classic Mode](images/Classic.png)

A simple single or multi-player mode where you have to reach the end of the maze.

You can also choose the `width` and the `height` of the maze.

### `"Blackout Mode"`

|                         Lights on                          |                        Lights out                        |                          Lights out with player particles                           |
| :--------------------------------------------------------: | :------------------------------------------------------: | :---------------------------------------------------------------------------------: |
| ![Image Blackout Mode Light](images/BlackoutLight.png) | ![Image Blackout Mode Dark](images/BlackoutDark.png) | ![Image Blackout Mode Dark with player particles](images/BlackoutDarkParticles.png) |

A fun single-player mode where your memory skills will come in handy to get yourself through the darkness.

There are 3 difficulties available : `EASY`, `NORMAL` and `HARD`.

## Dependencies

- SPro
- ALIZE (alize_core and LIA_RAL)
- Whisper (and all its dependencies)

## How to install the dependencies

Check the [INSTALL.md](INSTALL.md)

## How to run the game

After installing the dependencies, you will need to compile the game. To do so, you can execute the following command in the root directory of the project, you will to do this only once:

```bash
./run.sh --compile
```

You can then run the game by executing the following command in the root directory of the project:

```bash
./run.sh
```

If the file does not have the right permissions, you can give it the right permissions by executing the following command in the root directory of the project:

```bash
chmod +x run.sh
```

After that you can execute the command above to run the game.
