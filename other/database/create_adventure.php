
<?php

//create a new adventure
//parameters: user_name, password, adventure_name, adventure_description, adventure_link
//for example: create_adventure.php?user_name=test&password=12345678&adventure_name=new adventure&adventure_description=An adventure&adventure_link=http://string-functions.com/string-functions.gif

include('includefunctions.php');

if(isset($_GET['user_name']) && isset($_GET['password']) && isset($_GET['adventure_name']) && isset($_GET['adventure_description']) && isset($_GET['adventure_link'])) { //check if everything is filled out

	//get data from html
	$user_name = htmlspecialchars($_GET['user_name']);
	$password = htmlspecialchars($_GET['password']);
	$adventure_name = htmlspecialchars($_GET['adventure_name']);
	$adventure_description = htmlspecialchars($_GET['adventure_description']);
	$adventure_link = htmlspecialchars($_GET['adventure_link']);
	
	//check if adventure link leads to a valid file
	if(remote_file_exists($adventure_link)) {

		//check if user exists
		include('is_valid_user.php');
		if($is_valid_user) {
			
			//get user id and check if user id is valid
			include('user_name_to_id.php');
			if($user_id_from_name > -1) {
				
				//get $db variable
				include('login.php');
				
				//create new adventure entry
				if(execute_SQL($db, "INSERT INTO `rpg_adventure` (`adventure_id`, `user_id`, `adv_name`, `adv_desc`, `download_link`) VALUES (NULL, '" . $user_id_from_name . "', '" . $adventure_name . "', '" . $adventure_description . "', '" . $adventure_link . "')")) {
					echo "Created new adventure '" . $adventure_name . "'";
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