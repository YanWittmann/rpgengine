
<?php

//delete an adventure
//parameters: user_name, password, adventure_id
//for example: remove_adventure.php?user_name=test&password=12345678&adventure_id=0

if(isset($_GET['user_name']) && isset($_GET['password']) && isset($_GET['adventure_id'])) { //check if everything is filled out

	//get data from html
	$user_name = htmlspecialchars($_GET['user_name']);
	$password = htmlspecialchars($_GET['password']);
	$adventure_id = htmlspecialchars($_GET['adventure_id']);
	
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
				
				//remove adventure
				execute_SQL($db, "DELETE FROM `rpg_adventure` WHERE `rpg_adventure`.`adventure_id` = " . $adventure_id);
				echo "Removed adventure with id " . $adventure_id;
				
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
	echo "ERROR: You need to fill out all of the details first";
}



function execute_SQL($db, $sql) {
	if(!$db) {
		die("ERROR: Connection failed: " . mysqli_connect_error());
	}
	if ($db->query($sql) === TRUE) {
		return true;
	} else {
		echo "ERROR: " . $sql . "<br>" . $db->error;
		return false;
	}
}

?>