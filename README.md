# Test Technique - Radio France - Février 2024

- _Première édition du readme: 16 / 02 / 2024._
- _Dernière modification: 16 / 02 / 2024._

## Préambule
Le test technique est réalisé avec Android Studio dans version Hedgehog - 2023.1.1 | Patch 2, des dernières versions de gradle et autres plugins, à date.

Il a démarré le 16 / 02 / 2024 au soir et a été conclu le xx / xx / xx. 

Le projet supporte Android 6 (API 23) au minimum, à la demande du client, et cible Android 14 (API 34), dernière version en date d’Android.

Enfin, le design de l’application suit les préceptes de base du Material Design 3 (aka “You”).

Note: L’ensemble du code est hébergé en accès libre sur GitHub via le lien suivant: https://github.com/ricard-v/test-radiofrance. Ce dépôt a été configuré avec GitFlow. 

## Implémentation technique
L’intégralité du projet est réalisé en Jetpack Compose & Kotlin et bénéficie d’un bon nombre de dépendances issues des Android Architectures Components (aka Android X) dans leurs dernières versions stables, à date.

L’injection de dépendances est assurée par Koin dans sa dernière version stable (3.5.3).

Le projet repose sur la Clean Architecture avec des _ViewModel_ pour la partie MVVM. 
Chaque couche métier vient avec son lot de tests unitaires réalisés avec JUnit 5.

Enfin le projet bénéficie de l’ensemble des dépendances liées à Compose dans leurs dernières versions stables, ainsi que d’autres dépendances appartenant aux Architectures Components, également dans leurs dernières versions stables.


## Structure du projet
Le projet admet une structure modulaire pour les raisons suivantes:
séparation des responsabilités (sur le principe SOLID)
optimisation du temps de compilation avec Gradle
maintenance et test du code plus facile

### Module app:
Il s’agit du module principal et du point d’entrée du projet Android Studio qui contient tout le code relatif à l’application elle-même.

Ce module n’a vocation que d’exposer le code lié à toutes les interfaces graphiques et les fonctionnalités propres à l’application. 

Autrement dit, en se référant à la Clean Architecture, il s’agit de toute la partie UI allant jusqu’aux _ViewModel_, inclus.

### Module core:
Ce module Kotlin contient tout le code relatif à la couche métier que l’on retrouve dans la Clean Architecture :
- les _UseCase_
- les _Repository_
- les _DataSource_

### Module design:
Dans une démarche _design-oriented_, le projet liste également un module dédié aux composants UI (réalisés en Compose et avec Previews) qui seront consommés dans le module app.

Quand bien même ce projet n’a pas un besoin explicite en composants réutilisables à souhait, ce module facilite la maintenance et la prévisualisation des différentes parties UI du projet.


## Stratégie & Pilotage
Pour conduire ce projet à terme, les étapes suivantes ont été définies:
1. Mise en place du projet (Android Studio), de ses modules et de sa documentation (Readme et Google Doc), de ses dépendances, etc. Autrement dit, le minimum pour avoir une application qui fonctionne.
2. Mise en place de la source GraphQL permettant de récupérer les données du point d’“API Open Radio France”.
3. Développement des couches métiers
4. Développement des interfaces graphiques de base pour afficher les données et avec navigation afin de remplir les conditions du test technique.
5. Écriture des tests unitaires
6. Peaufinage de la UI pour une belle expérience et un beau design.
7. Livraison du projet aux parties prenantes.

## Source
En dehors de mon exérience profesionnelle et personnelle dont je me suis servi pour réaliser ce test, j'ai me suis fait assiter:
- de la chaîne YouTube Philipp Lackner: https://www.youtube.com/@PhilippLackner, notamment pour ce qui est de monter en compétence sur GraphQL que je n’ai jamais pratiqué et
- et un peu de Chat GPT pour résoudre quelques questions techniques (je précise qu’aucun code du projet ne provient de Chat GPT) et sujets d’inspiration.

_Note: Tout code qui proviendrait d’une source / assistance externe est annotée de la référence concernée._
