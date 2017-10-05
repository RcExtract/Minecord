# Change Log
- This file tracks all updates of Minecord and provides detail information about an update.
- Version label, plugin.yml changes or pom.xml changes will not be tracked unless the change affects 
the runtime of Minecord.
- On Alpha Update 2.0 and 3.0, the plugin is recoded.

## Beta 6.1
- Fixed null Rank Manager
- Fixed No database selected bug

## Beta 6.0
- Now ranks are saved
- Improved Database Manager API
- Fixed max channels issues (changed from around 8k to 2,147,483,647)
- Fixed max users issues per channel (changed from around 8k to unlimited)
- Fixed a bug of wrong assignment of channels to servers (before this fix a channel in server A may be moved to server B internally)
- Improved database saving and loading speed
- Now registering users requires a channel for the user to join after registration, null for default server
- Added unimplemented method getIdentifier() in interface Permission
- Recoded Database Manager

## Beta 5.0
- Added Ranks
- Added Permissions (Can be overrided by Bukkit permissions)
- Fixed null validation in channel creator method
- Removed Rank Manager deprecation

## Beta 4.1
- Now load data and check update asynchronously
- Improved error logs

## Beta 4.0
- Added detector for incompatible event handlers
- Fixed core

## Beta 3.1
- Fixed default server channel initialization bug
- Fixed not calling update checker bug

## Beta 3.0
- Added Update Checker
- Configuration file format changed to .properties
- Improved error logs
- Fixed runtime after plugin disable bug

## Beta 2.0
- Fixed duplicated channel issues
- Cleared testing codes

## Beta 1.4
- Now users can reload data with commands

## Beta 1.3
- Fixed duplicated database and table issues on save
- Fixed identifier truncation issues
- Fixed API bugs

## Beta 1.2
- Fixed database initialization issues
- Fixed database serialization and deserialization issues

## Beta 1.1
- Fixed database connectivity occupation issue

## Beta 1.0
- Fixed database connectivity issues
- Required databases and tables can be created automatically
- Fixed exception handlings
- Fixed database connection security

## Alpha 8.1
- No longer save records into files, but they exists before server reload
- Now config can be reloaded
- Plugin compacted
- Config no longer in Yaml format. Each line represents an information:
  1. Database host
  2. Username
  3. Password
  4. Message Format
  5. Message load count
- Fixed all initial bugs
- No longer depends on RcLib due to config format changing

## Alpha 8.0
- Fixed command registration and instruction manual
- Fixed event handler registration
- Added command: join and leave
- ChannelJoinEvent becomes ChannelSwitchEvent
- ChannelSwitchEvent's inheritance changes from ChannelEvent to UserEvent, so the target channel the user is about to switch to can be changed
- Added default server

## Alpha 7.0
- Now users can join servers and channels with commands
- All record managers merged
- UserEvent no longer inherits ChannelEvent
- The switch to enable GUI is built (GUI is not built)
- Not tested on 1.12.1 due to Spigot API issues

## Alpha 6.1
- Fixed dependency. Now depends on RcLib

## Alpha 6.0
- Now saves records
- More detailed JSON message
- Improved tagging system
- Fixed event inheritances
- UserTagEvent no longer inherits MinecordEvent

## Alpha 5.0
- Now depends on database (MySQL)
- Now can save all data except records

## Alpha 4.1
- Now supports server reload

## Alpha 4.0
- Removed duplicated codes
- Improved API (Added and rearranged functions)
- Now loads messages for a joined user
- Now can tag users with "@USERNAME" or "@uuid:UUID"
- Now can click message to reply to sender
- Once the message format is changed, reloads messages for all users

## Alpha 3.0
- Removed Super Channels
- Added several events
- Added Core (Message Distributor) (Unstable)
- Temporaily removed rank system
- Added User System
- Added Server Manager
- Added Message Manager

## Alpha 2.0
- Recoded
- Added Servers
- Added Channels
- Added Super Channels (A channel outside a server)
- Started Command Interface Development

## Alpha 1.0
- Added Raw Channels
- Added Joinable Channels
- Added Secured Channels
- Added Channels requires invitation
- Added Servers
- Added ChannelPlayerJoinEvent
- Added configs for storing data
