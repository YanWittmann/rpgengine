
<?php

//returns the profile picture of a user
//parameters: user_id, <response>
//for example: get_profile_image.php?user_id=1

include('includefunctions.php');

if(isset($_GET['user_id']) || isset($user_id)) { //check if everything is filled out

	//get data from html
	if(!isset($user_id)) $user_id = htmlspecialchars($_GET['user_id']);
	
	if(preg_match("/[0-9]+/", $user_id)) { //check if user id is valid
	
		//get $db variable
		include('login.php');

		//get user entry
		$query = mysqli_query($db, "SELECT * FROM rpg_user WHERE id = '" . $user_id . "'");
		if($query->num_rows > 0) {
			
			//set value to id
			$row = mysqli_fetch_array($query);
			$profile_image = $row['profile_image'];
			if(isset($_GET['response'])) {
				if($_GET['response'] == "true") {
					echo $row['profile_image'];
				}
			}
			
		} else {
			echo "ERROR: This user does not exist";
		}
		
	} else {
		echo "ERROR: User id is invalid: " . $user_id;
	}
	
} else {
	echo "ERROR: You need to fill out all of the details first";
}

?>