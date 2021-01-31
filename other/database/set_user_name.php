
<?php

//update profile picture of a user
//parameters: user_name, password, new_user_name
//for example: set_user_name.php?user_name=test&password=12345678&new_user_name=Yan

include('includefunctions.php');

if(isset($_GET['user_name']) && isset($_GET['password']) && isset($_GET['new_user_name'])) { //check if everything is filled out

	//get data from html
	$user_name = htmlspecialchars($_GET['user_name']);
	$password = htmlspecialchars($_GET['password']);
	$new_user_name = htmlspecialchars($_GET['new_user_name']);
	
	if(preg_match("/[0-9a-zA-Z_+ ]{2,30}/", $new_user_name)) { //check if new user name is valid
	
		//check if user exists
		include('is_valid_user.php');
		if($is_valid_user) {
			
			//update user name
			if(execute_SQL($db, "UPDATE `rpg_user` SET `user_name` = '" . $new_user_name . "' WHERE `rpg_user`.`user_name` = '" . $user_name . "'")) {
				echo "Updated user name";
			}
			
		} else {
			echo "ERROR: Invalid login data";
		}
		
	} else {
		echo "ERROR: Invalid new username";
	}
	
} else {
	echo "ERROR: You need to fill out all of the details first";
}

?>