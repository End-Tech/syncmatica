# Syncmatica

Syncmatica is a mod which aims to mod into litematica so that schematics and their placements can be easily shared.

## Setup

[Syncmatica](https://github.com/End-Tech/syncmatica/releases/tag/v0.0.1) is a mod for both Minecraft client and server.
It's made for [Minecraft Fabric 1.16.3](https://fabricmc.net/).
It relies on [litematica and malilib] to provide all client features

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

This will be the first release.
Many features are not implemented yet that I want to have implemented.
Many other features contain known bugs or edge cases that could theoretically appear on very specific siutations.
However I do believe that the mod is stable enough that you will not crash every 2 seconds.
Still I wouldnt reccomend unleashing it onto your survival world this instance  

Clients and servers do not yet store the shared placements they have locally persistently.
Clients will however save the placements as normal litematics.  

As of now there is no way to remove placements from the server or modify the placements on the server.
Those features are planned in a future release.  

The mod does not yet update the state of buttons when an action has been performed or during an action.
I will have to apply changes to the codebase in that regard.
This can potentially lead to client crashes when a button is available that shouldnt be.
To be specific if multiple shared placements share the same file and pressing the download button quickly its possible to crash the client.  

Operations failing due to many reasons can leave behind data corruption in the non-peristent state of the server.
This may not lead to noteable consequences most of the time but could lead to making it impossible to upload specific litematics.
This however is a theoretical bug I could not actively recreate during testing.  

The MaterialGatherings button is supposed to be for the task of collecting the materials as a group.
It should synchronize and simplify the collection of material across the server.
As of now it does nothing.
