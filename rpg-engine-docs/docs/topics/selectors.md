# Selectors
___

A selector selects specific objects that you created. The result of a selector can then for example be used as a parameter inside of a command. An example for a selector is: `#type:location;limit:1;tag:test#`  

## Building a selector
A selector always consists of two `##` with parameters inside of it. These parameters are each split by `;`. When evaluating a selector, the engine starts with a list of all objects and sorts all of the ones out that don't match the parameters. The possible parameters are:  

 * `uid` - checks the object `UID`
 * `name` - checks for the object name
 * `type` - checks the object type (see objects & types on [getting started](../getting-started.md))
 * `tag` - checks if object has a tag
 * `location` - checks if an NPC has a given location
 * `sort` - `[first;last;random]` determines the order the objects get sorted out for the `limit` parameter (needs to be set before `limit`)
 * `limit` - sets the maximum amount of objects to be selected
 * `inventory` - selects all items in a given inventory
 * _<span style="color:gray">`expr` - `[VALUE][==;>;<;>=;<=;!=][VALUE]` checks if an expression is true (beta)</span>_

It can also simply contain a `UID`, like so: `#702c14f1b34a45e5#`  
Selectors can be combined with variables to create dynamic selectors: `#name:{characterName}#`

## Examples / Exercises
Now that you know how the selectors work, try creating selectors that select these objects:

 1. All NPCs
 2. 5 random colors
 3. All items inside of the player inventory that have the tag `food`
 4. All NPCs that have the name `Carl` and are at the same location as the player
 5. All items that are in the inventory of the location the player is at

Here are the solutions:

 1. `#type:npc`
 2. `#type:color;sort:random;limit:5#`
 3. `#type:item;inventory:{player|inventory};tag:food#`
 4. `#type:npc;name:Carl;location:{player|location}#`
 5. `#inventory:{selector|#uid:{player|location}#}.inventory()#`