# Syncmatica

Syncmatica is a mod which aims to mod into litematica so that schematics and their placements can be easily shared.

## Setup

[Syncmatica](https://github.com/End-Tech/syncmatica/releases/tag/v0.1.2) is a mod for both Minecraft client and server.  
It's made for [Minecraft Fabric 1.16.x](https://fabricmc.net/).  
It relies on [litematica and malilib](https://masa.dy.fi/mcmods/client_mods/?mcver=1.16.3) to provide all client features.  

### Client

You first need to install fabric and add the litematica and malilib mods to your client.
The next step is to move the Syncmatica mod file to the mod folder.
Now you are ready to go.

As of now I am unaware of any incompatible litematica versions.

### Server

For the server you only need to install fabric and put Syncmatic in the mods folder and you are good to go.

## Usage

Once installed on your client you can join every server normally.
For servers which have Syncmatica installed you will get access to a few extra buttons.
2 of them are in the main menu and allow you to see the placements that are shared on the server and download them.
Another is in your schematic placement overview and allows you to share your own litematics with the server.

## Project Status & Road Map

The current update focused on interactions with litematica and restoring ease of use.
I pushed out these changes before finally starting on a longer update with regards to modifying the placement on the server and making some backend changes.

As of now there is no way to remove placements from the server or modify the placements on the server.
Those features are planned in a future release.  

The MaterialGatherings button is supposed to be for the task of collecting the materials as a group.
It should synchronize and simplify the collection of material across the server.
As of now it does nothing.
