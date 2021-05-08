# Syncmatica

Syncmatica is a mod which aims to mod into litematica so that schematics and their placements can be easily shared.

## Setup

[Syncmatica](https://github.com/End-Tech/syncmatica/releases/tag/v0.2.0) is a mod for both Minecraft client and
server.  
It's made for [Minecraft Fabric 1.16.x](https://fabricmc.net/).  
It relies on [litematica and malilib](https://masa.dy.fi/mcmods/client_mods/?mcver=1.16.4) to provide all client
features.

### Client

You first need to install fabric and add the litematica and malilib mods to your client. The next step is to move the
Syncmatica mod file to the mod folder. Now you are ready to go.

As of now I am unaware of any incompatible litematica versions.

### Server

For the server you only need to install fabric and put Syncmatic in the mods folder, and you are good to go.

## Usage

Once installed on your client, you can join every server normally. For servers which have Syncmatica installed you will
get access to a few extra buttons. 2 of them are in the main menu and allow you to see the placements that are shared on
the server and download them. Another is in your schematic placement overview and allows you to share your own
litematics with the server.

You need to be in the same dimension as a syncmatic to load it.

To modify a placement just unlock a placement on your client. Lock it again after making changes to share the changes
with everyone.

## Project Status & Road Map

With this update you can finally modify placements on the server. To do so just unlock a placement on your client. Lock
it again after making changes to share the changes with everyone. Sadly neither the modification nor the sharing of
modified subregions works yet in this version. Also starting with this version you will have to be in the same dimension
as the shared litematic due to limitations in litematica - there is no sane way to fix this. It will eventually be fixed
when masa forwards 1.12 changes in litematica to the latest version.

The MaterialGatherings button is supposed to be for the task of collecting the materials as a group. It should
synchronize and simplify the collection of material across the server. As of now it does nothing.
