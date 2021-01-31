
<?php

//update profile picture of a user
//parameters: user_name, password, new_user_name
//for example: set_user_password.php?user_name=test&password=12345678&new_user_password=12345678

include('includefunctions.php');

if(isset($_GET['user_name']) && isset($_GET['password']) && isset($_GET['new_user_password'])) { //check if everything is filled out

	//get data from html
	$user_name = htmlspecialchars($_GET['user_name']);
	$password = htmlspecialchars($_GET['password']);
	$new_user_password = htmlspecialchars($_GET['new_user_password']);
	
	if(preg_match("/[0-9a-zA-Z]{8,32}/", $new_user_password)) { //check if new user password is valid
	
		//check if user exists
		include('is_valid_user.php');
		if($is_valid_user) {
						
			//need to hash password
			$new_user_password = password_hash($new_user_password, PASSWORD_DEFAULT);
			
			//update user password
			if(execute_SQL($db, "UPDATE `rpg_user` SET `password` = '" . $new_user_password . "' WHERE `rpg_user`.`user_name` = '" . $user_name . "'")) {
				echo "Updated user password";
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