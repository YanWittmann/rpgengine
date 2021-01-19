# Commands
___

Commands are one of the most important things as they let you control almost every factor in the engine while the adventure is played.

## All commands
Here's a list of all of the commands and actions that you can use:

 * ##### `[VARIABLE] = [VALUE/VARIABLE]`
   Set a variable to either a value or to a variable without any mathematical operations or other values. Can return multiple entries. Examples:  
	
		{foo} = {value|hello}.substring(1,3)
		{selector|#type:npc#}.name() = Peter
		
	<span style="color:red">But not these since they contain variables _and_ values:</span>
		
		{bar} = {bar} + 23
		{bar} = {selector|#type:npc#} is funny!
   
 * ##### `[VARIABLE] == [MATHEMATICAL EXPRESSION WITH VALUES/VARIABLES OR MULTIPLE VALUES/VARIABLES]`
   Set a variable to a mathematical expression with values/variables or to multiple values/variables that get joined to a string. Can only return one entry. Examples:  
   
		{bar} == {bar} + 23
		{bar} == {selector|#type:npc#} is funny!
   
 * ##### `execute event [VALUE(event)] as [SELECTOR] {[VALUES(parameters)]}`
   Executes an event using the `{event|[SELECTOR(object)]|[VALUE(eventname)]}` variable as an object with parameters that are each split by `;`. You can use `self` as parameter for `as` to make it execute the event as the object containing it. Examples:  
   
		execute event {event|#uid:{player|location}#|walk} as #uid:{player|location}# {newLocation:{nLoc}}
		execute event {event|#name:Cook#|cookSomething} as self {cheese:2;tomato:4}
   
 * ##### `execute code {[VALUE(code)]} as [SELECTOR] {[VALUES(parameters)]}`
   Execute multiple lines of code as an object with parameters. Code lines each need to be split by `‏‏‎‏‏‎ ‎&&‏‏‎ ‎‎`.  
   Example (prints all object names that returned not only their UIDs):  
   
		execute code {ifnot {value|{this}}.name() matches [0-9a-z]\{16\} ( && print {value|{this}}.name() && )} as ## {}
   
 * ##### `print [VALUE]`
   Prints a string onto the main frame. When using a variable, only the first entry will be printed so make sure to only return the entry you want to print. You can join variables with text and format them with `[[color:[VALUE]]]`. Examples:  
   `print {selector|#type:npc;sort:random;limit:1#}.name() is [[gold:cool]]!` --> NPCName is <span style="color:gold">cool</span>!  
   You can even use HTML tags in here to format the text as you want: `print Hi <b>there</b>!` --> Hi **there**!
   
 * ##### `printwait [VALUE]`
   Prints a string onto the main frame. When using a variable, only the first entry will be printed so make sure to only return the entry you want to print. You can join variables with text and format them with `[[color:[VALUE]]]`.  
   After printing the text, the engine will wait for a certain amount of time, depending on how long the text is. The text speed can be set by using the `textSpeed` argument in the launcher. Examples:  
   `printwait Hello there!` will print `Hello there!` and wait for `1.6` seconds
   You can even use HTML tags in here to format the text as you want: `printwait Hi <b>there</b>!` --> Hi **there**!
   
 * ##### `evaluate [VALUE/SELECTOR]`
   One of the most useful commands to check whether your selector or variable is correct. It prints all entries into the console.  
   You can only give one variable or a selector as parameters, not any additional text.
   
 * ##### `goto [SELECTOR]`
   Will set the active location of the player to the location UID the selector returns. This triggers the `exit` event of the location the player leaves and the `entry` event of the location the player walks to. Both of these have the following parameters: `{comeFromLocation,gotoLocation}`.  
   Most likely used in the `walk` event of location objects after checking if the player can walk where they typed.
   
 * ##### `[wait;pause;sleep] [VALUE] [ms;s;m;h]`
   Using this command the current thread will be set to sleep for the amount of time given as a parameter.
   
 * ##### `[JUMPPOINT]:`
   A jumppoint marks a line in the code that you can then later jump to using the `jumpto` command. This can be used to create loops. An example:  
   `MARKER:`
   
 * ##### `jumpto [first;next;last] [JUMPPOINT]`
   Jumps to the first, next or last occurrence of the given jumppoint. Can be used to create loops.  
   `jumptp first MARKER`
   
 * ##### `tag [SELECTOR] [add;remove] [VALUE(tag)]`
   Adds or removes a tag from an object.
   
 * ##### `audio [play;stop] [AUDIO]`
   Begins or stops playing the selected audio clips.
   
 * ##### `audio list`
   Prints all audio clips that are currently playing. Only used for debugging.
   
 * ##### `player input [true;false]`
   Enables / diables custom commands to be executed.
   
 * ##### `inventory set [SELECTOR(item)] to [SELECTOR(inventory)] amount [VALUE]`
   Sets the amount of an item in an inventory to the amount given. Triggers the `drop` or `pickup` event of the item and inventory depending on whether the set amount is larger or smaller than the current.
   
 * ##### `inventory add [SELECTOR(item)] to [SELECTOR(inventory)] amount [VALUE]`
   Adds a certain amount of items to an inventory. Triggers the `pickup` event of the item and inventory.
   
 * ##### `inventory remove [SELECTOR(item)] from [SELECTOR(inventory)] amount [VALUE]`
   Removes a certain amount of items from an inventory. Triggers the `drop` event of the item and inventory.
   
 * ##### `inventory move [SELECTOR(item)] from [SELECTOR(inventory)] amount [VALUE] to [SELECTOR(inventory)]`
   Moves a certain amount of items from an inventory to another. Triggers the `drop` and `pickup` event of each of the items and inventories.
   
 * ##### `drop [SELECTOR(lootTable)] to [SELECTOR(inventory)]`
   Drops a loot table into an inventory.
   
 * ##### `clear [inventory] [SELECTOR(inventory)]`
   Removes all items from a given inventory.
   
 * ##### `clear console`
   Clears the main text frame.
   
 * ##### `open image [SELECTOR(image)] text [VALUE] size [VALUE]`
   Opens a frame with an image and a text as title. The width is set to the size given as a parameter; the height is being calculated automatically. An example that opens all images:  
   
		open image #type:image# text I'm an image! size 300
   
 * ##### `open object [SELECTOR] <[VALUE(text)]>`
   Opens a frame where any object can be displayed with the option to add extra text at the bottom. This is used by the engine by default to display examined items from the player inventory and for NPCs on the battleMap when right clicked. An example that opens all objects:  
   
		open object ## I'm an object!
   
 * ##### `close object [SELECTOR]`
   Closes all selected object frames again.
   
		close object ##
		close object #type:npc#
   
 * ##### `open file [VALUE]`
   Opens a file from the `advfiles` folder. Requires the `fileopen` permission.
   
		open file {file|foo.txt}
		open file bar.jar
   
 * ##### `open file [SELECTOR(fileObject)]`
   Opens the files returned from the selector. These need to be `fileObject`s.
   
		open file #type:fileObject#
		open file #name:foo.png#
   
 * ##### `alert [VALUE]`
   Opens a popup with only text.
   
 * ##### `battle start [SELECTOR(battleMap)]`
   Switches to battle mode:
    * Main frame closes
	* Battle map openes, plays intro and begins battle
	* Player stats frame cannot be minimized any more
   
 * ##### `battle stop [VALUE(outcome)]`
   Can be used to trigger the ending of a battle map. This switches back to normal mode. The `outcome` parameter is used for the `end` event.
   
 * ##### `battle set [X] x [Y] [npc;item;extragroundtile;obstacle] [x;y;xy;image;uid] [VALUE] <x [VALUE]>`
   Sets an attribute of an object on a certain tile. Specify what coordinates on the battlemap with `[X] x [Y]` and choose the object type you want to select: `[npc;item;extragroundtile;obstacle]`. Then select what attribute you want to set using `[x;y;xy;image;uid]` and finally the value you want to set it to.  
   Setting the `uid` is only available for npc and item; setting the `image` is only for extragroundtile and obstacles.  
   Also, if you want to set both `x` and `y` at the same time using `xy`, use this format: `0 x 1`  
   A few examples:  
   
		battle set 1 x 1 obstacle image #name:bread#
		battle set 1 x 0 item uid #67f0260423784927#
		battle set 2 x 3 obstacle xy 1 x 4
		battle set {tmp} x {tmp} extragroundtile image #name:bread#
		
   A thing to note is that you can't set `x`, `y` and `xy` while an object is walking or attacking. The change will only be effective after the action took place.
   
 * ##### `battle set [SELECTOR(npc|item);player;groundtiles] [x;y;xy;image;uid] [VALUE] <x [VALUE]>`
   Sets an attribute of an NPC/item via a selector, of the player or the groundtiles.  
   Setting `uid` is only available for npc and item; setting the `image` is only for npc, player and groundtiles; setting `x`, `y` and `xy` is only for npc, item and player.  
   Also, if you want to set both `x` and `y` at the same time using `xy`, use this format: `0 x 1`  
   A few examples:  
   
		battle set #type:npc# xy 0 x 4
		battle set #67f0260423784927# uid #e318489b06eb4aa6#
		battle set groundtiles image #2afdec77bba14448#
   
   A thing to note is that you can't set `x`, `y` and `xy` while an object is walking or attacking. The change will only be effective after the action took place.
   
 * ##### `battle add [X] x [Y] [npc;item;extragroundtile;obstacle] [SELECTOR(npc|item|image)] <img [SELECTOR(image)]>`
   Adds an object to the battleMap to specific coordinates. For the `npc` you will need to give an additional image. Added NPCs will get added to the turn order as last index.  
   A few examples:  
   
		battle add 1 x 3 obstacle #c859375027884d30#
		battle add 7 x 4 npc #68ea85a4d077420d# img #511309fde13f415f#
   
 * ##### `battle remove [X] x [Y] [npc;item;extragroundtile;obstacle]`
   Removes an object from the battleMap using coordinates.  
   A few examples:  
   
		battle remove 1 x 1 obstacle
		battle remove 1 x 2 npc
   
 * ##### `battle remove [SELECTOR(npc)]`
   Removes an NPC via a selector. An example:  
   
		battle remove #type:npc;sort:random;limit:1#
   
 * ##### `battle refresh [all;overlay;attackanimation;player;npc;item;obstacle;extra;ground]`
   Forces the battleMap gui to be updated. Split multiple parameters with `,` or `;`. A few examples:  
   
		battle refresh overlay,player
		battle refresh all
   
 * ##### `battle active [true;false]`
   Sets the battle into the active or inactive state. In the inactive state, the next round won't start automatically.  
   Setting it to inactive allows for the use of the `battle action` command.
   
 * ##### `battle canend [true;false]`
   Sets whether the battle will end if the health of all enemies reaches 0 or if the player health reaches 0.  
   You will need to end the battle via the `battle stop` command.
   
 * ##### `battle freewalk [true;false]`
   BattleMap needs to be inactive for this command to work.   
   Player will be able to walk to any tile they click.
   
 * ##### `battle action [SELECTOR(npc);player] attack [SELECTOR(npc);player] <using [SELECTOR(item)]>`
   BattleMap needs to be inactive for this command to work.  
   This makes an npc or the player attack another npc or the player. If you leave away the `using [SELECTOR(item)]` parameter, the currently equipped item will be selected. Examples:
   
		battle action #name:Joe;type:npc# attack player using #name:Baguette;type:item#
		battle action player attack #type:npc#
		battle action #name:Joe;type:npc# attack #name:Willi;type:npc#
   
 * ##### `battle action [SELECTOR(npc);player] walk [X] x [Y]`
   BattleMap needs to be inactive for this command to work.  
   Makes an npc or the player walk to a certain coordinate. Examples:  
   
		battle action #name:Joe;type:npc# walk 1 x 5
		battle action player walk 5 x 7
   
 * ##### `battle action [SELECTOR(npc);player] approach [X] x [Y] distance [VALUE]`
   BattleMap needs to be inactive for this command to work.  
   Makes an npc or the player go to a tile that has the given distance from the goal. The distance counts the start tile as one already. Examples:  
   
		battle action player approach 3 x 5 distance 7
		battle action #name:Nameless man# approach {currentbattle}.battleInfo(player|x) x {currentbattle}.battleInfo(player|y) distance 3
   
   
 * ##### **Conditions**
   Conditions can be used to check whether a certain case is true or false. There are multiple different condition types.  
   They always consist of the `if` statement, the `if body`, an optional `else` also with a `body`:
   
		if<not> [SOMETHING] (
		   #code
		) else (
		   #code
		)
		
    The first one is:
   
		if<not> [SELECTOR/CONDITION] (
   
	Checks if a selector returns at least one object or if a condition is true. A condition looks like this:  
   `[VALUE] [==;>;<;>=;<=;!=;contians;matches;equals] [VALUE]`  
   A condition can be used to compare two values.  
   If the condition is `true` or the selector returned one or more objects then the `if` branch is taken. If not, the `else` branch is selected. If there is no `else`, the `if` body is simply skipped and the program continues as normal.  
   To negate the result, use `ifnot`. You can also combine multiple conditions with ` || ` and as long as one of the conditions returns `true`, the condition is true. to A few examples:  
   
        if {tmp} contains hello || {tmp} == t(
		  print true
		) else (
		  print false
		)
   
        ifnot #name:Joe;tag:atHome# (
		  print false
		) else (
		  print true
		)
		
	The other one rolls a talent with a given difficulty class and checks if the roll is a success (for more info see [talents](talents.md))
	
		if<not> talent [VALUE(talentname)] DC [VALUE(dc)] [true;false(visible)] [true;false(autoroll)] [VALUE(message);none] (
		
 * ##### `for each [SELECTOR/VARIABLE] {`
   The `for each` statement loops the content of the brackets for each object returned by the selector / entry returned by the variable. You can use `{this}` to get the current value.  
   An example that prints all object names:
   
		for each ## {
			print {value|{this}}.name()
		}
	
	Using a `for each` inside of a `for each` will most likely not work. You can, however use a `for each` to call other events that then use `for each` inside of them. 
   
 * ##### `return`
   Leaves an event and returns to the event call.
   
 * ##### `break`
   Leaves a `for each` and continues after it.
   
 * ##### `continue`
   Begins the next loop of the `for each`. If no entries remain, it acts just like a `break`.
   
