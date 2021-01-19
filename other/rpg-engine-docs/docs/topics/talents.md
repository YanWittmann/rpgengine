# Talents
___

When creating a new talent, you can pick up to three different attributes from this list:

 * courage	
 * wisdom	
 * intuition
 * charisma	
 * dexterity
 * agility	

## Roll talents
You can let the player roll a talent using the `if talent` command:  

	if<not> talent [VALUE(talentname)] DC [VALUE(dc)] [true;false(visible)] [true;false(autoroll)] [VALUE(message);none] (

When rolling for a talent, you will need to add a `DC`, a difficulty class. The higher the `DC`, the more difficult the check is.  
For each attribute the talent has, the player rolls a `D20` and adds his according character attribute value. If any of the up to three attributes fall below the `DC` the check fails. If he passes all three, the check is a success.  
Here's an example:  

	if talent Stealth DC 20 true false Roll for your life! (

The player needs to roll the talent `Stealth` (which only has `DEX` as attribute) and reach at least `20` with his `DEX` value + his roll. Let's say he has a `12` as `DEX`. He now needs to roll at least `8` with the D20 to complete the check.
You can also use the `talent` variable:

	{talent|[VALUE(talentname)]|[VALUE(dc)]|[true;false(visible)]|[true;false(autoroll)]|[VALUE(message);none]}

which simply returns `true` or `false` depending on if the player completed the check.  Here's an example:

	evaluate {talent|Traps|20|true|false|Roll for your life!}

If your talent requires more than one attribute, the player will need to pass the `DC` for each attribute.