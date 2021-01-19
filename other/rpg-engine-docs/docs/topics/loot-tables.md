# Loot tables
___

A loot table can be used to generate random loot that is then being dropped into an inventory. This can be great for looting chests, enemies or for simply rewarding the player for examining stuff.

## Building a loot table
Generaly a loot table looks like this:

	[SELECTOR(item)] [VALUE(amount)] [VALUE(chance in percent)]
	
	{
		roll [VALUE(from)] to [VALUE(to)]
		[SELECTOR(item)] [VALUE(amount)] [VALUE(chance in percent)]
	}

The first one only drops the things once, the lower one can drop items multiple times.  

	[SELECTOR(item)] [VALUE(amount)] [VALUE(chance in percent)]

Drops one/multiple item(s) with a certain chance. This means this line right here:

	#type:item;name:Bread# 4 25

has a 25% chance of dropping 4 bread.  
The other possibility is to have a list of drops that get evaluated a certain amount of times:

	{
		roll [VALUE(from)] to [VALUE(to)]
		[SELECTOR(item)] [VALUE(amount)] [VALUE(chance in percent)]
	}

The amount of times that the contents will be dropped is determined by this: `roll [VALUE(from)] to [VALUE(to)]`  
Then all following lines will be executed this amount of times. They work just like the first method.

## Dropping a loot table
To drop a loot table into an inventory, use the `drop [SELECTOR(lootTable)] to [SELECTOR(inventory)]` command. This will drop the loot into the given inventory.  
Upon dropping the loot, the event `dropLootCode` of the loot table is executed with the `{inventory}` as a parameter.

## Example

	#type:item;name:Toast# 2 50
	{
		roll 2 to 5
		#type:item;name:Sweets# 2 50
		#type:item;name:Egg# 1 100
	}

This loot table can drop:  

 * 0 - 2 Toast
 * 0 - 10 Sweets
 * 2 - 5 Eggs
