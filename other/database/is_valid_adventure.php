
<?php

//checks if a user with the password exists
//parameters: adventure_id
//for example: is_valid_adventure.php?adventure_id=0

$is_valid_adventure = false;
					
if(isset($_GET['adventure_id'])) { //check if everything is filled out

	//get data from html
	$adventure_id = htmlspecialchars($_GET['adventure_id']);
	
	if(preg_match("/[0-9a-zA-Z_+ ]{2,30}/", $adventure_id)) { //check if adventure id is valid
			
		//get $db variable
		include('login.php');

		//check if adventure exists
		$query = mysqli_query($db, "SELECT * FROM rpg_adventure WHERE adventure_id = '" . $adventure_id . "'");
		if($query->num_rows > 0) {
			$is_valid_adventure = true;
		}
		
	} else {
		echo "ERROR: Username may only contain the following characters: A-Z a-z 0-9 _ + and length must be between 2 and 30: [0-9a-zA-Z_+]{2,30}";
	}
	
} else {
	echo "ERROR: You need to fill out all of the details first";
}

?>