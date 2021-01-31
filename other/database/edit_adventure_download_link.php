
<?php

//edit an adventure
//parameters: user_name, password, adventure_id, new_attribute
//for example: edit_adventure_download_link.php?user_name=test&password=12345678&adventure_id=0&new_attribute=newattr

include('includefunctions.php');

if(isset($_GET['user_name']) && isset($_GET['password']) && isset($_GET['adventure_id']) && isset($_GET['new_attribute'])) { //check if everything is filled out

	//get data from html
	$user_name = htmlspecialchars($_GET['user_name']);
	$password = htmlspecialchars($_GET['password']);
	$adventure_id = htmlspecialchars($_GET['adventure_id']);
	$new_attribute = htmlspecialchars($_GET['new_attribute']);
	
	if(remote_file_exists($new_attribute)) {
		
		//check if user exists
		include('is_valid_user.php');
		if($is_valid_user) {
			
			//get user id and check if user id is valid
			include('user_name_to_id.php');
			if($user_id_from_name > -1) {
				
				//get $db variable
				include('login.php');
				
				//check if user is owner of adventure and if adventure exists
				$query = mysqli_query($db, "SELECT * FROM rpg_adventure WHERE user_id = '" . $user_id_from_name . "' AND adventure_id = " . $adventure_id);
				if($query->num_rows > 0) {
					
					//edit adventure
					execute_SQL($db, "UPDATE `rpg_adventure` SET `download_link` = '" . $new_attribute . "' WHERE `rpg_adventure`.`adventure_id` = " . $adventure_id);
					echo "Edited adventure with id " . $adventure_id;
					
				} else {
					echo "ERROR: You are either not the owner of the adventure or the adventure doesn't exist";
				}
				
			} else {
				echo "ERROR: User exists but unable to get user id from login data (if you see this, what did you do?!)";
			}
			
		} else {
			echo "ERROR: Invalid login data";
		}
		
	} else {
		echo "ERROR: The given file does not exist";
	}
	
} else {
	echo "ERROR: You need to fill out all of the details first";
}

?>