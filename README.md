# Syncmatica

Syncmatica is a mod which aims to mod into litematica so that schematics and their placements can be easily shared.

### Notice Please use with caution

Syncmatica is a mod that gives its users a lot of power and can have consequences for the server. Only use this mod if
you feel confident that your users won't abuse it too heavily.

## Setup

[Syncmatica](https://github.com/End-Tech/syncmatica/releases/tag/v0.2.2) is a mod for both Minecraft client and
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

After running the mod once it will create a configuration file that you can use to configure the mod as you please.
See [Config Doku](https://github.com/End-Tech/syncmatica/blob/master/CONFIG.md) for more information.

## Usage

Once installed on your client, you can join every server normally. For servers which have Syncmatica installed you will
get access to a few extra buttons. 2 of them are in the main menu and allow you to see the placements that are shared on
the server and download them. Another is in your schematic placement overview and allows you to share your own
litematics with the server.

You need to be in the same dimension as a syncmatic to load it.

To modify a placement just unlock a placement on your client. Lock it again after making changes to share the changes
with everyone.

## Project Status & Road Map

With this update we will fix an issue with shared nbt schematics, allow the server to send you messages and add a server
sided quota for uploads. This is mostly an update for the Server the client only receives minor updates.

* Sharing NBT schematics is now impossible instead of leading to a crash - NBT schematics are just too different to
  litematics to make good use of them in the mod.
* The server is now able to send you pretty messages via a protocol - if you are on an outdated client it will just send
  you chat messages instead.
* Quota - one of the security issues of syncmatica is that you are able to share files of arbitrary size with no actual
  limit. Server quotas fix this issue by adding a limit to how much a player can upload between restarts Of course this
  is not the perfect safety feature and users can still probably find ways to misuse this mod so be cautious.

This update delays the updates that synchronise subRegions. I have also seen that many servers have a problem with the
amount of syncmatics that are persistently shared. I imagine I should also add a fix for that.

The MaterialGatherings button is supposed to be for the task of collecting the materials as a group. It should
synchronize and simplify the collection of material across the server. As of now it does nothing.
