# Change Log
- This file tracks all updates of Minecord and provides detail information about an update.
- Version label, plugin.yml changes or pom.xml changes will not be tracked unless the change affects 
the runtime of Minecord.
- On Alpha Update 2.0 and 3.0, the plugin is recoded.

## 1.1.3 (New API not fully implemented, about 90%, 10% will be done before 1.2.0 Release)
- Recoded Core class
- New enhanced data and configuration safety system
- Host, user and password are no longer saved in configuration files due to security reasons.
- Enhanced the Command Expansion API
- You can now set host, user and password through console
- Rearranged classes
- Enhanced SQLObjectConverter and fixed some bugs
- Enhanced update available detection system
- SQLObjectConverter now supports external serializers and deserializers

## 1.1.2 (New API not fully implemented, about 88%, 10% will be done before 1.2.0 Release)
- Added support for PlaceholderAPI

## 1.1.1 (New API not fully implemented, about 88%, 10% will be done before 1.2.0 Release)
- Rearranged classes
- Redesigned the static Minecord methods access method
- Improved the implementation towards the new API

## 1.1.0 (New API not fully implemented, about 85%, 10% will be done before 1.2.0 Release)
- SQLObjectConverter now supports Java object referencing
- Minecord can now load and save data after reimplementation
- Added FinalTypeConverter for final external serialization and deserialization
- Added utility methods
- Added and fixed Javadocs

## 1.1.0 SNAPSHOT 5.0 (New API not fully implemented, about 65%)
- Conversable and User class implemented the new API.
- DataManipulator reimplementing the new API.
- Added a few exceptions to the SQLObjectConverter API.
- Added TypeConverter for conversions between types that either one of them does not implement this API.
- Added many Javadoc comments.

## 1.1.0 SNAPSHOT 4.1 (New API not fully implemented, about 60%)
- Added TypeConvertor for supporting serializations and deserializations between SQL types and java classes that haven't implemented this API
- Added utility FunctionalInterface Convertor

## 1.1.0 SNAPSHOT 4.0 (New API not fully implemented, about 60%)
- Now finally supports loading and saving any Sendables (including instances of subclasses') correctly.
- Now plugin only supports Java 8+ due to using Lambda expressions and APIs only available on Java 8+.
- Updated ComparativeSet filter from Method to Predicate.
- Added method getIf(Predicate) in ComparativeSet
- Added a few utility classes for loading and saving data

## 1.1.0 SNAPSHOT 3.0 (New API not fully implemented, about 45%)
- Renamed ChannelPreference to ChannelOptions
- Renamed ConversablePreference to SendableOptions
- Options inside servers changed from pointing to Conversable to Sendable
- New data saving method created, DataManipulator (replacement of DatabaseManager) can now save sendables of subclasses of Sendable.

## 1.1.0 SNAPSHOT 2.0 (New API not fully implemented, about 40%)
- Improved the implementation of the multi-channel system
- Renamed Listener to ChannelPreference
- Removed ServerIdentity
- Added ConversablePreference for server to configure member settings
- Added a few getter interfaces
- Added ConversationSender interface to indicate a class that it can be used to send message inside a channel.
- Added Conversable class to indicate that it can read messages beside from the features of the implementing ConversationSender
- User class now inherits Conversable
- Renamed a few methods

## 1.1.0 SNAPSHOT 1.0 (New API not fully implemented, about 98%)
- Removed ChannelManager
- Most classes fully implemented the new change of ChannelManager removal, except for CommandHandler.
- Added new utility class ComparativeSet
- Added a few getter interfaces
- Improved the implementation of the multi-channel system

## 1.0.3
- Adding identities now doesn't throw DuplicatedException, and replaced with returning boolean value.
- Planning to remove ChannelManager

## 1.0.2
- Fixed a bug that prints minecord.properties
- Fixed automatic ServerIdentity generation bug
- Joining servers now change the view of a user to the main channel in it

## 1.0.1
- Fixed all initial bugs
- Fixed Jar builder (Not visible from source)
- Fixed database manager bugs
- Fixed minecord.properties default config generation bug
- Fixed update checker bugs
- Categorized some events into different packages

## 1.0.0 (Unstable)
- Added Message class for storing messages. (Alternative of UserMessageEvent) They will not be saved to database.
- Updated Command Handler to adapt to new Multi-Chat system.
- Database manager updated from 5.1 (minecord5dot1) to 6.0 (minecord6dot0).
- Added a method to load old version of data to new version of plugin.
- Fixed some create event bugs
- Channel Switch Event is deprecated
- Finalized some fields
- Added ListenerUpdateEvent and ServerIdentityUpdateEvent
- Identitfier deprecated in UserMessageEvent, due to no longer stored in channels as messaging records.
- Update message restore system. Now currently restores unread messages only.
- Added Multi-Chat system along with switchview command.
- Added a utility class - Pair.

## Beta 12.1
- Executing command select without argument will now display your current editing target if selected
- Fixed various bugs

## Beta 12.0
- You can now edit server and channel properties with commands
- Changed some event names to support the new commands

## Beta 11.1
- Local project repository restored
- You can now close a Server Editor
- Removed deprecated and unused classes

## Beta 11.0
- Fixed server editer menu generator
- You can now create servers with customized properties
- Fixed result messages for command channel, channels and servers
- Fixed channel duplicated entry bug in database
- Fixed not saving whether the rank is main to database
- Fixed ghost files (Files used to be deleted still exists)
- Removed deprecated classes
- Now database manager has a version shown behind "minecord" in database name

## Beta 10.1
- Fixed the large bug which crashes the server when chatting
- Fixed data column not existed bug on loading
- Removed debugging messages

## Beta 10.0
- You can now create servers with command
- You can now edit servers with inventory
- Added a few events

## Beta 9.0 (Commit Missing)
- Removed permissions interface
- Removed permission enumerations
- Removed Minecord permission package
- Rank now has Bukkit permissions instead of Minecord permissions
- Fixed default server generated after user registration bug
- Temporarily removed user specific permission for plugin stable
- Now depends on Vault
- Now updates permissions for a user once the rank has changed
- Fixed database saving issues
- Added permission minecord.create-server

## Beta 8.0
- Added command profile
- Fixed user null rank and auto rank assignment
- Improved API a lot
- Rearranged statements into different methods
- Fixed command reload
- Removed message printing statements

## Beta 7.0
- Removed temporary command users
- Tracked all command updates
- Added command server
- Added command channel
- Added filtering function in command servers and channels
- Fixed reload command actions
- Improved command responses

## Beta 6.3
- Fixed duplicated user registration issue
- Fixed UUID equality check statement
- User instances now contains UUIDs of players instead of player instances

## Beta 6.2
- Fixed auto main channel assignment issue

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
