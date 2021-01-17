# Getting started
___


To create a new adventure, open the adventure editor in the launcher first.  
Use either the shortcut `ctrl+shift+n` or the drop-down menu and hit `Start` to create a new adventure.
  
  
## Shortcuts
Let's start with the shortcuts you can use. These are the ones you can use on the **main frame**:
  
* `ctrl+shift+n` - New adventure
* `ctrl+o` - Open File
* `ctrl+s` - Save
* `ctrl+shift+s` - Save as
* `ctrl+n` - New object
* `ctrl+e` - Edit object (Select a UID and use shortcut to instantly open edit frame)
* `ctrl+k` - Clone object (Select a UID and use shortcut to clone object)
* `ctrl+d` - Delete object (Select a UID and use shortcut to instantly delete it)
* `ctrl+r` - Reload
* `ctrl+q` `ctrl+scroll up` - Scroll left
* `ctrl+w` `ctrl+scroll down` - Scroll right
* `ctrl+shift+q` `scroll up` - Scroll up
* `ctrl+shift+w` `scroll down` - Scroll down
* `Drag and drop` - Open adventures, add images or audio files

These are the ones you can use on the **object frames** and most other frames as well:
  
* `ctrl+[0-9]` - Select according button
* `ctrl+r` - Reload
* `scroll up` - Scroll up
* `scroll down` - Scroll down
* `esc` - Close the frame


## Objects & types
Understanding the concept of objects is crucial to creating your adventure.  
An object can be anything - from a location over an image to a battleMap. The different object types you have at your disposal are represented by the different panels on the main frame of the adventure creator. These are all of them:
  
  * location
  * npc
  * item
  * inventory
  * image
  * audio
  * battleMap
  * talent
  * lootTable
  * variable
  * eventCollection
  * customCommand
  * color
  * fileObject

To create a new object, either use the button at the bottom or press `ctrl+n`.  
A popup asking what type you want to add will appear. You can type out what object type you want to add to quickly select it. After hitting enter, the object is being created. Every object gets assigned a UID. A UID is a 16 character long string of lowercase letters and numbers; for example: `9359ab779a614e15`. This UID is unique for this object and you can use it later to reference the object from other objects and in the code.  
The object frame will open. It shows all of the data it stores. To edit the object's data, use the buttons or `ctrl+[0-9]` to quickly select a button.  
If you closed the frame, select the UID of the object you want to open in the panel on the main frame and use the shortcut `ctrl+e`.  
Here's a short explanation of what the different objects do, but we'll go more in-depth later on:

#### Location
A location is where the player or NPC's can be. It can have an inventory and an image.
#### NPCs
Can be used on a battleMap. They can have a location, an image and an inventory.
#### Item
Define an item type that can be put into inventories or on a battleMap. They can have an image.
#### Inventory
Can store items and can be assigned to other objects or the player.
#### Image
An image can be used in other objects or can be opened in a popup.
#### Audio
Audio files can be played with a command.
#### Battle Map
Battle maps are used to open a new frame where the battle takes place. You can put a variety of things onto them.
#### Talent
Talents can be rolled in the adventure. They consist of up to three parameters.
#### Loot table
They can be dropped into an inventory. They have a custom syntax that can be used to generate random loot.
#### Variable
Simply stores a value.
#### Event Collection
Its only purpose is to store events that would not fit to any other object type or do general things.
#### Custom Command
Define you own commands the player can enter to control what he wants to do. Using a command triggers an event with the player's parameters.
#### Color
There are a lot of predefined colors. All of the colors the engine uses are defined here, so that you can customize the look as you want. You can also format text that will be printed with them: <span style="color:green">[[green:text]]</span>

‏‏‎#### File Object
You can also simply load a file that will then be stored. A `fileObject` is read-only
 ‎  
‏‏‎ ‎  
_Now try creating all of them and try out what the different buttons do!_
‏‏‎ ‎  
‏‏‎ ‎  
## Player / Project settings
Use the drop down menu `Adventure > Player` or `Adventure > Project` to acess the player and project data. More on the individual values later.

## Frame icons
You might have noticed that depending on what frame you open the color of the frame's icon changes.  
The main frame is always yellow, object editors are always green and all others are blue. The debugger has a red icon.  
![Yellow](img/iconyellow.png) ![Green](img/icongreen.png) ![Blue](img/iconblue.png) ![Red](img/iconred.png)

## Creating your first adventure
Now that you know the basics you can get started and create your very [first adventure](first-adventure.md).

