
<?php

//update profile picture of a user
//parameters: user_name, password, image
//for example: set_profile_image.php?user_name=test&password=12345678&image=http://yanwittmann.de/images/missing_profile_picture.png

include('includefunctions.php');

if(isset($_GET['user_name']) && isset($_GET['image']) && isset($_GET['password'])) { //check if everything is filled out

	//get data from html
	$user_name = htmlspecialchars($_GET['user_name']);
	$image = htmlspecialchars($_GET['image']);
	$password = htmlspecialchars($_GET['password']);
	
	//check if image link leads to a valid file
	if(remote_file_exists($image)) {

		//check if user exists
		include('is_valid_user.php');
		if($is_valid_user) {
			
			//update profile picture link
			if(execute_SQL($db, "UPDATE `rpg_user` SET `profile_image` = '" . $image . "' WHERE `rpg_user`.`user_name` = '" . $user_name . "'")) {
				echo "Updated profile picture";
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