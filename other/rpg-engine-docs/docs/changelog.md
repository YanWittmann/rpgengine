# Changelog
___

## 26.01.2020 - RELEASE 1.11
#### Bugfixes:

 * 

#### New / Modification:

 * 

#### Other:

 * accidentially deleted source code of version `1.10.1` which meant that i had to reprogram all of the features


## 21.01.2020 - RELEASE 1.10.1
#### Bugfixes:

 * editor would not open newly created objects instantly but only after confirming a popup
 * certain actions in editor would even be usable when no adventure was opened
 * events would not open when set to edit in text editor
 * new adventure frame would close after changing the value of one of the drop downs

#### Other:

 * had to delete 1.10 of create since it contained a bug that would make it impossible to edit adventures correctly


## 23.01.2020 - RELEASE `1.10`
#### Bugfixes:

 * player name would be stuck at string `Yan` when not changed via the player settings or set to `name:` in player settings
 * intro frame would still wait for project icon to appear when skipped
 * player max health would be set wrong (to `80`) when editing adventure after removing `maxHealth` value
 * launcher will now no longer install a corrupt version if `install` is clicked multiple times
 * fixed a bug in the intro adventure (you will need to reinstall the engine to get the new adventure file)

#### New / Modification:

 * made editing/deleting/cloning objects easier: you don't need to mark the uid any more, placing the curser inside it is enough. also, if you select a string that _contains_ a uid, it will filter out the uid.
 * your clipboard will now no longer be flooded with uids when using shortcuts to edit/delete/clone objects
 * optimized creator main frame setup code
 * updated `consoleIcon1.png` (save icon) to be black & white
 * player stats frame will now appear `10 pixels` away from taskbar and not at fixed location (`150, 10`) which is something i wanted to do a long time ago
 * loading animation is now not contained inside of creator or player, they now use the gif from the launcher; this drastically reduces the file size again
 * in the character creation, you can now simply hover over rolled values to slect them
 * you can now escape `[` and `]` in formatted strings by using `\[` and `\]`
 * changed action editor string `Detected event modification: [EVENT]` to `Event saved: [EVENT]`


## 20.01.2020 - RELEASE `1.9.1`
#### Bugfixes:

 * creadits text in creator would not set position scaled correctly if it bounced up and down automatically every 30 - 120 seconds

#### New / Modification:

 * new `general` `eventCollection` event: `showAvailableCommands`: gets executed when player clicks on new `Show commands` button in player
 * you can now save the game via a 'drop up' (?) menu at the bootom right of the main player frame
 * in the launcher, you can now click on the title `RPG Engine` to toggle between fast and slow setup mode
 * moved `actionEditorOpenDirectlyInExternalEditor` from `Project settings` to `menu bar > Other > Toggle open actions directly in editor`


## 19.01.2021 - RELEASE `1.8.1` & `1.9`
#### Bugfixes:

 * the launcher did not extract files from new downloaded versions correctly making them unuseable
 * creator would crash if certain directories were missing

#### New / Modification:

 * finished automatic uploader, i can now create all files and upload them with one button press (which is really cool)
 * text input popup now automatically requests focus upon opening so that the user doesn't have to click into the text field
 * new command `savegame`: you can now create a savestate that you can later reload by opening that adventure and selecting the savestate

#### Other:

 * this update is again mainly to check if the new uploader still works
 * version `1.8` is no longer publically available since it contained a bug causing the creator and player not to install correctly
 * drastically reduced version file size (over 50% less)
 

## 18.01.2021
#### Bugfixes:

 * the launcher could not install new versions if certain directories where missing (these will now be generated if needed)

#### New / Modification:

 * cleaned up files and sorted them into categories
 * object frames now flash `gray` for `200 ms` if double clicked to indicate the `stay in foreground` toggle

#### Other:

 * created program that automatically:  
    * generates documentation & uploads it
    * generates base rpg engine download (launcher) and uploads it
    * generates creator & player download and uploads them
	* generates version lists and uploads them

## 17.01.2021
#### Bugfixes:

 * fixed a crash on setup for the player
 * talent roll `visible` and `autoRoll` parameter were not used in the code

#### New / Modification:

 * **moved project to intellij IDE**
 * **added a [git repo](https://github.com/Skyball2000/rpgengine)**
 * cleaned up code and made processes more efficient thanks to intellij _(did i mention that i love intellij?)_
 * you can now copy a string that matches either `#?[\da-f]{6}` (hex color) or `\d{1,3}[, ]{1,2}\d{1,3}[, ]{1,2}\d{1,3}` (rgb color) and hover over credits label in creator to get that exact color (example: `50, 168, 82` or `#6036f7`)
 * talent roll now shows current player value

#### Other:

 * this release is mainly to test if everything still works as with bluej or if there are things that need to be fixed (and yes, there were a lot of things that needed to be fixed)
 * had to remove release `1.8` due to a bug that might have corrupted adventure files


## 16.01.2021 - RELEASE `1.7`
#### Bugfixes:

 * custom variables would not be displayed with their propper name in the object frames
 * version string was stuck at `1.4` since that version

#### New / Modification:

 * modified dark mode in editor not to be completely black, new `text_background:10,10,10`
 * added option to add custom stylesheets in `res/stylesheets`
 * added the option to select what stylesheet to use


## 11.01.2021
#### New / Modification:

 * launcher now has a small text hover animation
 * launcher now reacts more intuitively to user hover events when starting


## 07.01.2021 - RELEASE `1.6`
#### Bugfixes:

 * `elf` would be printed `Elf` in the player even if contained inside of a word
 * since `evaluateRoll` can now return negative values, `battleMap` objects would heal if dealt a negative amount of damage
 * image popup frame would miscalculate required frame width
 * on some systems the editor would only scale on the first user caused resize event

#### New / Modification:

 * you can now double click object frames to make them stay in the foreground
 * you can now triple click object frames to minimize them
 * on the minimized object frames you can click to rotate through the available variables
 * if the adventure creator now sets a name, the player will not be asked for a name any more
 * object frames now update their variables if they get updated (for `battleMap` health ...)
 * `evaluateRoll` now supports strings where there is no space between parameters (such as `1D6+3` or `4-7`)
 * new command `close object` that allows creator to close object frames
 * because of the new object frame management there can now only be one frame of every type. calling it a second time will result in it popping to the front
 * new project variable `objectFrameVariables` that defines what variables will be displayed on the object frame
 * new player variable `maxHealth`: the maximum amount of health the player can have. Setting the variable `health` to a greater value will result in it snapping back to `maxHealth`


## 05.01.2021 - RELEASE `1.5`
#### Bugfixes:

 * evaluateRoll would sometimes only return 0 as value
 * popups `text` and `buttons` would sometimes cover the whole screen with white for a split second due to the new size calculation

#### New / Modification:

 * all frames of the creator can be resized
 * all frames of the creator now automatically adjust their size to the screen size when opening
 * added option to skip intro
 * added option to roll all attributes in the character creation at once

#### Known issues:

 * image frame does not open with correct size to display entire image

## 03.01.**2021** - RELEASE `1.4`
#### Bugfixes:

 * launcher would always open version selected in play drop down even for creator
 * fixed the popup window size for good this time (used `awt`'s information about the size of the panel which returns a reliable size of the labels)
 * fixed minor bugs regarding main frame from editor
 * parameters in launcher would not be saved
 * `execute code {} as self {}` would not execute code when executed from command line since no object was given

#### New / Modification:

 * launcher now updates itself if a new version is available (redownload is necessary one last time)
 * added permission fileopen that allows the usage of the command `open file`
 * added command `open file` that allows the creator to open files from the `advfiles` folder
 * creator can now be resized to fit any screen
 * creator can now have up to 6 panels
 * added `fileObject` type
 * added `printwait` command that prints a text and waits for a while before continuing. This duration depends on the text length and can be scaled using the `textSpeed` argument in the launcher
 * added `textSpeed` argument that controls how long the `printwait` command waits after printing. Default is `100`, `50` is half duration, `200` is double duration


## 31.12.2020 - RELEASE `1.3` `play`
#### Bugfixes:

 * character creation would not let player roll attributes

#### New / Modification:

 * you can now click on images on object frames to open them in a large window


## 29.12.2020 - RELEASE `1.2`
#### Bugfixes:

 * inventory operations would try to add / remove items to inventories even though the item or the inventory didn't exist
 * fixed two cases where the character creation would not detect that the player cannot create a character with the rolled values
 * engine would hang itself up while `introOver` event gets executed
 * fixed intro adventure

#### New / Modification:

 * you can now no longer scroll past the inventory boundaries and it will snap back to the available items if items were removed and you would be out of bounds
 * adjusted size calculation of the popups (hopefully for the last time...)
 * removed `lang` setting from main config in the creator (unused)
 * renamed `Help` in adventure generator menu bar to `Other`
 * added a **dark mode** for the adventure creator (`Other` > `Toggle dark mode`)
 * position of text popup is now slightly randomized (+-40 pixels)
 * hover text now has a different background color (30,30,30) to make it different from the black background color
 * hover text now also has rounded white border around it
 * if player types in command that does not exist a hover message will appear
 * removed all remaining debug logs
 * added text how to close the object frames

#### Other:

 * **this is a really exiting day for me, since i can officially say that the engine has reached the first stable version. with this, an almost one year long journey comes to an end. thank you so much to everyone who supported me on this long way!**


## 28.12.2020
#### Bugfixes:

 * truely encrypting the file would require user to enter password twice in editor
 * setting a variable that begins with `value` would not work
 * editing two events from the same entity would result in overwriting the event first opened with the code from the second event
 * `if` would still not work correctly, skipping over too much code and stopping at wrong lines. might still be broken when using `continue`, `break` or jumppoints but it works in most cases
 * npc image could not be changed because there was no button to do so

#### New / Modification:

 * modified easter egg random color picking code to pick more saturated colors
 * file watcher now deletes file on stop watching
 * disabling player input now means that the player cannot submit their text until it is enabled again to prevent the text from simply disappearing
 * added documentation and website link to `help` drop down menu in generator
 * added project setting `actionEditorOpenDirectlyInExternalEditor`: when set to `true`, the events opened with the object frame will instantly open in a text editor and not in the action editor frame (recommended mode!)


## 27.12.2020
#### Bugfixes:

 * many popup frames would use `WHITE` as color for the line borders, now they correctly use the color defined in the adventure
 * dice popup frame would not scale correctly
 * due to the rewrite of the main frame console labels code (26.12.2020) the main frame was not draggable any more; added according listeners
 * player would not attack with item from offhand if mainhand item was empty
 * file watcher for action editor would not start watching since code around it was modified
 * some popups would still be way too large
 * uid detection would sometimes detect any 16 characters long string as uid

#### New / Modification:

 * added project setting `debugModeForceable` which when set to `false` can disable the `forceDebug` argument
 * added project setting `requirePasswordToPlay` : you can now truely encrypt an adventure so that you need to enter the password, even when trying to play adventure, by setting it to `true`. You will not be able to recover any of the data if you forget your password in this case (if set to `false` the data does not get truely encrypted and can therefore be recovered)
 * changed launcher clickable colors
 * added a few more messages to the list of messages to the battle map editor
 * added a few more filenames
 * modified a few strings in the lang files
 * created a [website](http://yanwittmann.de/projects/rpgengine/site/Home.html) using `nicepage` which really is an awesome free website generator
 * main frame text now supports overflow wrapping (also rewrote display code again)
 * you can now use `or` (` || `) with conditions


## 26.12.2020
#### Bugfixes:

 * main frame text: html formatting would not be completely cleared on newline

#### New / Modification:

 * rewrote main frame text distribution so that it now scales correctly when scaling gui (larger/smaller frames)
 * added option to pass arguments over to play part by holding down shift while clicking on `Start version` on the launcher
 * added launching argument `lang` with possible values: `english`, `german`
 * added launching argument `scale` that lets the user scale the frame by a percentage (`100` is default scale)
 * added launching argument `forceDebug` (`true`/`false`) that forces debug mode if set to `true` even if adventure is not set to debug mode


## 23.12.2020
#### Bugfixes:

 * on the `create a new adventure` frame, you could use `enter` to instantly create a new adventure, but not if you had the author field selected
 * log indentation sometimes did not remove indents correctly (evaluating selectors and `for each`)

#### New / Modification:

 * log will now indent when entering a `for each`
 * you can now execute as self: `execute event {event|#9c211ede7e9d4642#|main} as self {}`
 * added button to close battle maps
 * added dialoge to warn user before closing adventure


## 22.12.2020
#### Bugfixes:

 * old engine name would be displayed on intro frame

#### New / Modification:

 * removed configuration file from play part since it only stored unused data
 * deriving the pixel font will now store the font object with the according size and return it if the same size is scaled again to reduce computing time
 * added abillity to scale every frame (including text and images) up or down: first, frames are scaled to screen size and then by a factor the user can decide (user can't decide yet what scale they want to pick)


## 21.12.2020 - RELEASE `1.1`
#### Bugfixes:

 * using a variable in a selector as only parameter would not work (for example: `#{player|inventory}#`)

#### New / Modification:

 * added dnd talents
 * added pathfinder talents
 * talents can now have 1 to 3 attributes instaed of forcing 3
 * updated talent roll mechanic to make it more similar to dnd
 * added new `create new adventure` interface
 * added dummy variables `tmp` `foo` `bar` that you can use for debugging
 * if player inventory is overloaded, the weight is displayed red and speed on battle maps is reduced by 50%
 * added `overloaded` variable to player values (`{player|overloaded}`)
 * you can now clone objects by selecting their uid and hitting `ctrl+k`


## 20.12.2020
#### Bugfixes:

 * fixed the talent roll which did not work with new condition system

#### New / Modification:

 * continue writing the documentation


## 19.12.2020 - RELEASE `1`
#### Bugfixes:

 * `battle freewalk` would control `canend` variable on battle map instaed of `freewalk`
 * fixed some minor bugs regarding the launcher
 * battle map editor would say `NPC UID` instaed of `image UID` in some cases

#### New / Modification:

 * made attack animation faster
 * cleaned up directories
 * removed unused classes
 * removed unused import statements
 * started working on new version of launcher
 * added new type of version management
 * added `rightClick` event in battle map
 * cleaned up try catch-statements to make error messages in the log more helpful
 * reduced file size by compressing loading gif
 * you can now use the shortcut `ctrl+[0-9]` on the battle map and the inventory editor
 * removed requirement checker since it is now redundant to the new version manager
 * launcher: added managing adventures, documentation, bug report, feature request, contact, buy me a coffee

#### Other:

 * got a new monitor in real life :)


## 13.12.2020
#### Bugfixes:

 * npc image in battle maps would not be grabbed from the battle map image but from the npc image
 * npc would not attack if the coordinates of the player were not the ones where they wanted to walk to
 * battlemap `start` event would not trigger

#### New / Modification:

 * continue writing the documentation
 * added `battle freewalk` command that lets player walk around freely when battle map is inactive


## 13.12.2020
#### Bugfixes:

 * objects selected via the `battle action` command would try to perform the action even if they were not on the battlemap
 * enemy attacking with hands would let player roll damage
 * small optimization of the attack animation
 * log would say that the result of a selector is every object if selector only contained a uid: `#68ea85a4d077420d#`
 * image and audio objects would have the wrong text formatting in the editor: `name uid` instead of `name --- uid`

#### New / Modification:

 * continue writing the documentation
 * added `battle action` parameter: `approach`
 * added `battle canend` command
 * added modifier `isUID()`
 * everything until the first parameter of a custom command is now highlighted if the player types it in the console
 * everything until the first parameter of a custom command can automatically now be autocompleted


## 14.12.2020
#### Bugfixes:

 * using sleep or other delaying functions via debugger would hang up program for that duration

#### New / Modification:

 * continue writing the documentation
 * added support for npcs attacking npcs
 * added `battle action` command
	* `walk`
	* `attack`


## 13.12.2020
#### Bugfixes:

 * main frame did not always display the user input when scrolling back down

#### New / Modification:

 * continue writing the documentation
 * moved changelog to documentation website
 * added inventory set command
 * you can now set the amount variable
 * made scrolling up and down the debugger faster
 * you can now escape `{}` like this: `\{\}` (will still be used for all `matches` and `replaceAll`)
‏‏‎ ‎  
‏‏‎ ‎  
## 12.12.2020
#### Bugfixes:

 * the `{input|line}` variable would still execute the text the player typed

#### New / Modification:

 * continue writing the documentation
 * added a logger / debugging frame
‏‏‎ ‎  
‏‏‎ ‎  
## 11.12.2020
#### New / Modification:

 * started creating (this) documentation using MkDocs
‏‏‎ ‎  
‏‏‎ ‎  
## 10.12.2020
#### Bugfixes:

 * if the battle was over and then restarted, the engine would softlock
 * changing the location of an obstacle or extragroundtile or removing one of these would not remove them visually
 * image and button popups are now not that oversized
 * `battle set` parameters were sometimes misunderstood

#### New / Modification:

 * added `battle add` command: you can now add new objects onto the battlemap
 * added `battle refresh` command: you can now force the battlemap gui to be updated
 * added `battle remove` command: you can now remove objects from the battlemap with either the location or a selector
 * `battle set` command now has the `xy` parameter so that both `x` and `y` of an object can be set at the same time
‏‏‎ ‎  
‏‏‎ ‎  
## 09.12.2020
#### New / Modification:

 * new command `battle stop` with the outcome as parameter
 * added `player` and `groundtiles` parameters to `battleInfo` modifier
 * added `battle set` command: can control location, image and uid of the objects on the active battlemap
 * added `battle active` command: disables starting a new turn if set to `false`
‏‏‎ ‎  
‏‏‎ ‎  
## 08.12.2020
#### Bugfixes:

 * in the CC, when all values were distributed, the available classes would no longer update their color for a second or two
 * trying to close file watcher in editor even when none was open which led to the frame being unable to close
 * mini player stats frame would not disappear if maximized by battle frame
 * loading an adventure with an empty variable would result in the adventure being unable to load
 * setting and getting variable objects when there is more than one now actually works; it treated all of them as one
 
#### New / Modification:

 * when creating a new adventure, the project icon uid is now automatically put into the project setting `image`
 * set new `gold` color to `#f5ca0a`
 * selectors can now also only contain a UID without any other parameters
 * player stats frame now minimizes into a square containing the project icon that maximizes again when mouse wheel clicked
 * in the editor, the textareas now fill the panels to the bottom
 * changed icons (`iconblue`,`iconyellow`,`icongreen`,`playerStartingPos`)
 * you can now drag and drop files onto the battle map
 * using the new variable `{currentbattle}` and the modifier `battleInfo()` you can now get information on the current battle
 * removed several debug-println's
 * battle start command syntax has changed
‏‏‎ ‎  
‏‏‎ ‎  
## 07.12.2020
#### Bugfixes:

 * creating a new adventure didn't reset images
 * parameters label in CC editor was too small
 * `if` inside of `for each` did not skip body if condition was false
 * onw-word-commands were not displayed yellow in console
 * `execute` command would execute even without command parameters

#### New / Modification:

 * added changelog file
 * you can now acess the button in the entity editor via shortcuts `ctrl+[0-9]`
 * you can now edit events in a text file, which automatically updates editor when saved
 * added clear console / chat command
 * added clear inventory command
 * you can now leave away `expression` from the `if expression` command
 * you can now leave away `selector` from the `if selector` command
 * added new command-keywords (yellow): clear,if,expression,return,break,continue
 * player input in console now begins with ` - `
 * pressing up arrow in console now only fetches player inputs
 * added return, break and continue commands
 * autocomplete feature, define terms in project settings (split with `,`) that can be autocompleted. Use `tab` to autocomplete as a player
 * permissions are now required before creating your character, since the creator now has the ability to execute code at launch &#38; at the beginning of the intro
 * intro frame now has a drag listener
 * you can now choose as an adventure creator wether the intro should be displayed (`showIntro`)
 * the project image `image` is now displayed under the adventure name in the intro frame
