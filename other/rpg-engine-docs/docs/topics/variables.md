# Variables
___

## Introduction
Variables are used to acess data from objects and more. Basically, every time you see a parameter in a command that looks like this `[VALUE]` or pretty much any other `[...]` with CAPS LOCK, you can use a variable instead of it.  
A variable consists of the <span style="color:green">**base**</span> and any amount of <span style="color:orange">**modifiers**</span> behind the base. An example:  
**<span style="color:green">{player|name}</span><span style="color:orange">.toUppercase().endsWith(AN)</span>**  
They can even contain multiple layers of variables:  
**<span style="color:green">{player|name}</span><span style="color:orange">.indexOf(<span style="color:green">_{lastInput}<span style="color:orange">.substring(0|2)</span>_</span>)</span>**  
To easily find out the value of a variable, you can use the `print` command to get the first value or the `evaluate` command to get all values.  
Everytime you see something enclosed in `[...]` it means that you need to replace it with a value and a `<...>` means that whatever is in them optional. If the text inside the `[...]` is lowercase it means you need to choose one of the options.  
If you need to write something in `{}` without them being seen as a variable, you will need to escape them `\{\}`.

## <span style="color:green">**Variable base**</span>
It is important to know that a variable can not only store a single value but can contain a whole list of values. Some of the variables will return only one, but others will return a whole array. A great example for this is the `{selector|[SELECTOR]}` variable that can transform the result of a selector into a variable. Meaning this `{selector|#type:location#}` can return this:  

 * `453521874aec4192`
 * `fa353d3a33ef4d54`

To acess the individual results you would need to specify which one you want using modifiers, but more on those later. Parameters inside of variable bases and modifiers are split by `|`. You can escape this by using `\|`.  
Time for a list of all possible variable bases:

| Variable																										  | Description                                                                                                                                                                             |
|-----------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `{{VARIABLENAME]}                                                                      						` | Will return the value of a variable object                                                                                                                                              |
| `{player|[SETTING]}                                                                    						` | Will return the value of the setting specified in the player settings                                                                                                                   |
| `{project|[SETTING]}                                                                   						` | Will return the value of the setting specified in the project settings                                                                                                                  |
| `{random|[MIN]|[MAX]}                                                                  						` | Returns a random number                                                                                                                                                                 |
| `{value|[VALUE 1];[VALUE 2];...}                                                                       		` | Creates a variable based on the given value(s). Each value is a new line in the variable                                                                                                |
| `{selector|[SELECTOR]}                                                                 						` | Creates a variable based on the result of the selector                                                                                                                                  |
| `{event|[SELECTOR(object)]|[VALUE(eventname)]}                                         						` | Used for the `execute` command. `[SELECTOR]` specifies the object where the event is stored and `[VALUE]` the name of the event that will be executed (`all` will execute all events)   |
| `{amount|[SELECTOR(item)]|[SELECTOR(inventory)]}                                       						` | Gives the amount of an item in a specific inventory                                                                                                                                     |
| `{currentbattle}                                                                       						` | Returns the UID of the battleMap that is currently active                                                                                                                               |
| `{this}                                                                                						` | Returns the UID of the object evaluating the variable                                                                                                                                   |
| `{inBattle}                                                                            						` | Returns `true` or `false` whether a battleMap is active                                                                                                                                 |
| `{input|dice|[VALUE(text)]|[VALUE(sides)]|[VALUE(duration)]|[VALUE(autoroll;boolean)]} 						` | Opens a popup with a dice and returns the value rolled                                                                                                                                  |
| `{input|button|[VALUE(text)]|[VALUE(button1)];[VALUE(button2)];...}                    						` | Opens a popup with a text and multiple buttons and returns the ID of the one selected                                                                                                   |
| `{input|button|[VALUE(button1)];[VALUE(button2)];...}                    										` | Opens a smaller popup with multiple buttons and returns the ID of the one selected                                                                                                      |
| `{input|buttonlist|[VALUE(text)]|[VARIABLE(without `{}`)]}           						 					` | Opens a popup with a text and multiple buttons based on the entries of the given variable (without `{}`, cannot have modifiers) and returns the ID of the one selected                  |
| `{input|buttonlist|[VARIABLE(without `{}`)]}                    												` | Opens a smaller popup with multiple buttons based on the entries of the given variable (without `{}`, cannot have modifiers) and returns the ID of the one selected                     |
| `{input|dropDown|[VALUE(text)]|[VALUE(option1)];[VALUE(option2)];...}                  						` | Opens a popup with a drop down menu and returns the value of the selected option                                                                                                        |
| `{input|text|[VALUE(text)]|[VALUE(pretext)]}                                           						` | Opens a popup with a text input and returns what the player typed                                                                                                                       |
| `{input|line}                                                                          						` | Waits for the player to type something and returns the value                                                                                                                            |
| `{talent|[VALUE(talentname)]|[VALUE(dc)]|[true;false(visible)]|[true;false(autoroll)]|[VALUE(message);none]}  ` | Rolls a talent and returns `true` or `false`                                                                                                                                            |
| `{lastInput}                                                                           						` | Returns the last thing the player typed or selected in a popup                                                                                                                          |
| `{empty}                                                                               						` | Creates an empty variable                                                                                                                                                               |
| `{[parameter]}                                                                         						` | Returns a parameter that came with the event call                                                                                                                                       |
| `{web|[VALUE(url)]}                                                                    						` | Returns an entire website or file via the URL with each line being a new entry in the variable (requires `web` permission)                                                              |
| `{file|[VALUE(path)]}                                                                  						` | Returns the selected file in the `res/advfiles/` folder with each line being a new entry in the variable (requires `fileread`/`filereadanywhere` permission)                            |
| `{file|[SELECTOR(fileObject)]}                                                             					` | Reads content of `fileObject` as if it was text with each line being a new entry in the variable                            															|
| `{popup|[uids;names]}                                                             							` | Returns all of the currently open popup uids or the individually defined names																											|
| `{inventory|[VALUE(inventory]}                                                       							` | Returns the uids of the items contained inside of the given inventory																													|

There are also object specific variables:  

| Object Type | Variable       | Description                                                                   |
|-------------|----------------|-------------------------------------------------------------------------------|
| item        | armor          | Reduces the amount of damage taken when equipped as armor item                |
| item        | color          | `hex` color value that determines the color of the item name in the inventory |
| item        | weight         | How much the item adds to the inventory weight                                |
| item        | damage         | How much damage the item deals (see below for more infos)                     |
| item        | range          | The range of the item on the battle map                                       |
| item        | value          | How much the item is worth (unused)                                           |
| item        | hands          | Can be either `1`, `2` or `armor` and describes the slots the item takes      |
| npc         | health         | The health of the NPC                                                         |
| npc         | courage        | The object with the highest courage comes first on a battle map               |
| npc         | speed          | How many tiles the NPC can walk on a battle map in one turn                   |
| npc         | dmgNoWeapon    | The damage if the NPC has no item equipped                                    |
| npc         | equippedWeapon | The item the NPC has currently equipped                                       |
| npc         | armor          | How much damage is being absorbed                                             |


Some of the variables, such as `{this}` or `{[parameter]}` cannot have modifiers. You will have to use the value variable `{value|{this}}.toUppercase()`.  

## <span style="color:orange">**Modifiers**</span>
Modifiers can change the values of a variable in different ways.

 * Modify the individual elements of the variable
 * Create a whole new variable

Let's start with the ones that only modify the values of the variable. They get applied on every entry separately.

| Modifier                                                                                         | Description                                                                                                                    |
|--------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| `charAt([VALUE(index)])                                                                        ` | Returns the character at the given index                                                                                       |
| `toUppercase()                                                                                 ` | Turns all lowercase characters to their uppercase counterpart                                                                  |
| `toLowercase()                                                                                 ` | Turns all uppercase characters to their lowercase counterpart                                                                  |
| `contains([VALUE])                                                                             ` | Sets to `true` or `false` depending on if the variable contains the value                                                      |
| `equals([VALUE])                                                                               ` | Sets to `true` or `false` depending on if the variable is equals to the value                                                  |
| `endsWith([VALUE])                                                                             ` | Sets to `true` or `false` depending on if the variable is ends with the value                                                  |
| `matches([VALUE])                                                                              ` | Sets to `true` or `false` depending on if the variable matches a regular expression                                            |
| `isType([VALUE(type)])                                                                         ` | Sets to `true` or `false` depending on if the variable is a UID and if the corresponding object is the given type              |
| `isUID()                                                                                       ` | Sets to `true` or `false` depending on if the variable is a UID                                                                |
| `replace([VALUE(find)]|[VALUE(replace)])                                                       ` | Replaces all occurrences of a value with another one                                                                           |
| `replaceAll([VALUE(find)]|[VALUE(replace)])                                                    ` | Replaces all occurrences of a regular expression with another value                                                            |
| `indexOf([VALUE])                                                                              ` | Sets to the index of the first time the value appears in the string                                                            |
| `substring([VALUE(int)]|[VALUE(int)])                                                          ` | Sets to a substring from the beginning index to the end index                                                                  |
| `length()                                                                                      ` | Sets to the length of the value                                                                                                |
| `round()                                                                                       ` | Rounds the value to the next whole number                                                                                      |
| `math([VALUE(math. expression)])                                                               ` | Performs a mathematical operation on the value. See 'Using variable values in modifiers'                                       |
| `evalMath()                                                                                    ` | Treats entry as mathematical expression and returns result                                                                     |
| `string([VALUE])                                                                               ` | Just like math() but does not perform any further operations on it                                                             |
| `sort()                                                                                        ` | Sorts the variable entries based on the alphabet                                                                               |
| `invert()                                                                                      ` | Inverts the order of the entries                                                                                               |
| `set([VALUE(index)]|[VALUE])                                                                   ` | Sets an entry to a value                                                                                                       |
| `battleInfo([npc; item; extragroundtiles; obstacles; player; groundtiles]<|[x; y; uid; image]>)` | Returns information on the battleMap with the UID of the variable entry. Specify what information you need with the parameters |

You can also use this to get object data:

| Modifier                   | Description                                                                                                     |
|----------------------------|-----------------------------------------------------------------------------------------------------------------|
| `name()                  ` | Grabs the UID of the variable entry and if the according object exists returns the name of it                   |
| `description()           ` | Grabs the UID of the variable entry and if the according object exists returns the description of it            |
| `location()              ` | Grabs the UID of the variable entry and if the according object exists returns the location UID of it           |
| `image()                 ` | Grabs the UID of the variable entry and if the according object exists returns the image UID of it              |
| `type()                  ` | Grabs the UID of the variable entry and if the according object exists returns the type of it                   |
| `inventory()             ` | Grabs the UID of the variable entry and if the according object exists returns the inventory UID of it          |
| `variable([VARIABLENAME])` | Grabs the UID of the variable entry and if the according object exists returns the variable with the given name |

These modifiers create a new results-list:

| Modifier                                               | Description                                                                                                          |
|--------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| `get([equals;contains;matches]|[VALUE])              ` | Returns only the entries that match the given criteria                                                               |
| `get(index|[VALUE(index)])                           ` | Return only the entry with the given index                                                                           |
| `get(random<|[VALUE(fromIndex)]|[VALUE(toIndex)]>)   ` | Returns a random entry with the option to specify in what range the index can be                                     |
| `remove([equals;contains;matches]|[VALUE])           ` | Removes the entries that match the given criteria                                                                    |
| `remove(index|[VALUE(index)])                        ` | Removes the entry with the given index                                                                               |
| `remove(random<|[VALUE(fromIndex)]|[VALUE(toIndex)]>)` | Removes a random entry with the option to specify in what range the index can be                                     |
| `append([VALUE])                                     ` | Appends either another variable list or simply a single value. If you want to append a variable, leave away the `{}` |
| `indexOfInList([VALUE])                              ` | Looks for the value inside of the list and returns the index of the first match                                      |
| `average([round;float])                              ` | Returns the average value of the entire list                                                                         |
| `sum([round;float])                                  ` | Returns the sum of the entire list                                                                                   |
| `min([round;float])                                  ` | Returns the smallest value of the entire list                                                                        |
| `max([round;float])                                  ` | Returns the largest value of the entire list                                                                         |
| `count()                                             ` | Returns the amount of entries                                                                                        |
| `split([VALUE])                                      ` | Splits first entry at given value and creates for each element a new emtry in the new result list                    |
| `size()		                                       ` | Returns the amount of entries in the variable													                    |

## Using variable values in modifiers
Using the strings `%1` and `%2` inside of the modifier parameters will replace them with:

 * `%1` - current element
 * `%2` - current element index

Meaning the variable `{value|Line 0}.append(Line 1).string(%1 - %2)` will return:  
`Line 0 - 0`  
`Line 1 - 1`

## Setting variables
It wouldn't be too useful if you could only read variables. Setting them is pretty easy luckily:  
`[VARIABLE] = [VALUE/VARIABLE]`  
for example: `{test} = {test}.toUppercase()`  
  
If you need to perform one/multiple mathematical operation(s), you need to use two `==`:  
`[VARIABLE] == [MATHEMATICAL EXPRESSION WITH VALUES/VARIABLES]`  
for example: `{test} = {test} + 23`  

One more thing: You cannot use more than one modifier on the left variable, meaning this would not work:
`{selector|#type:npc#}.sort().remove(index|3).name() = {test}.name()`

## Examples / Exercises
Now that you know how the variables work, try creating variables that return values that match these descriptions:

 1. A variable that returns the playername with only uppercase
 2. A variable that reads the file `test.txt` in the advfiles folder and returns the rounded average length of all lines (what permissions are required here?)
 3. A variable that reads the file `foo.txt` one folder above the advfiles folder and returns a list in which no entry contains the word `hmm` (what permissions are required here?)
 4. A variable that returns the name of the location the player is at
 5. A variable that returns all of the location image UIDs of all NPCs
 6. A variable that returns the amount of bread in a random NPC inventory
    
    Now set the these variables to the values:
    
 7. Set the player name to the name of a random location
 8. Set the amount of `Bread` in the players inventory to the result of the product of `{bar}` and 6
 9. Set the description of all locations to the value a player rolls with a 20 sided dice
 10. Set the `health` of all NPCs to 10

Here are the solutions:

 1. `{player|name}.toUppercase()`
 2. `{file|test.txt}.length().average(round)`  
    requires `fileread` permission
 3. `{file|../foo.txt}.remove(contains|hmm)`  
    requires `filereadanywhere` permission
 4. `{player|location}.name()`
 5. `{selector|#type:npc#}.location().image()`
 6. `{amount|#name:Brot#|{selector|#type:npc;sort:random;limit:1#}.inventory()}`

 7. `{player|name} = {selector|#type:location;sort:random;limit:1#}.name()`
 8. `{amount|#name:Brot#|#uid:{player|inventory}#} == {bar} * 6`
 9. `{selector|#type:location#}.description() = {input|dice|Roll a dice!|20|8|false}`
 10. `{selector|#type:npc#}.variable(health) = 10`


















