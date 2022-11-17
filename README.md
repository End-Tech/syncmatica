# Syncmatica

Syncmatica is a mod which aims to mod into litematica so that schematics and their placements can be easily shared.

### Notice Please use with caution

Syncmatica is a mod that gives its users a lot of power and can have consequences for the server. Only use this mod if
you feel confident that your users won't abuse it too heavily.

## Setup

Syncmatica is a mod for both Minecraft client and server.
The mod works for Minecraft 1.16+. It's made for [Minecraft Fabric](https://fabricmc.net/). It relies
on [litematica and malilib](https://masa.dy.fi/mcmods/client_mods/) to provide all client features. Please make sure to
update litematica, malilib and other potentially conflicting mods like Multiconnect before making a bug report about
Syncmaticas functionality :)

Use [v0.3.4-1.16.5](https://github.com/End-Tech/syncmatica/releases/tag/v0.3.4-1.16.5) for 1.16 or 1.17
Use [v0.3.4-1.18.2](https://github.com/End-Tech/syncmatica/releases/tag/v0.3.4-1.18.2) for 1.18

### Client

You first need to install fabric and add the litematica and malilib mods to your client. The next step is to move the
Syncmatica mod file to the mod folder. Now you are ready to go.

Versions as old as v0.0.0-dev.20210106.181551 appear to cause issues due to a field renaming or not existing or being
invisible. If you have versions as old as that you will have to update or Syncmatica may not function properly.

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

v0.3.4 adds compatibility with 1.19.

I have also seen that many servers have a problem with the amount of syncmatics that are persistently shared. 
The ownership is a pre-requirement for the change that is supposed to this.
Adding a better fix for this is also on the roadmap, but I'm debating the priority since I don't get any feedback like this anymore.

What seems way more important is to get syncmatica working in single-player and by doing that in LAN worlds.
I'm not sure how much work this is - we'll see. I just got way more reports about this.

The MaterialGatherings button is supposed, to aid with collecting the materials as a group. It should synchronize and
simplify the collection of material across the server. As of now it does nothing.

## Contact

Feel free to join me on [Discord](https://discord.gg/6NPDVNMZ3T) for more information and help on the mod.
