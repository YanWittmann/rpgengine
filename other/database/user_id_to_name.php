
<?php

//stores the $user_name from the given id
//parameters: <user_id>
//for example: user_id_to_name.php?user_id=7

$user_name_from_id = -1;
					
if(isset($_GET['user_id']) || isset($user_id)) { //check if everything is filled out

	//get data from html
	if(!isset($user_id)) $user_id = htmlspecialchars($_GET['user_id']);
	
	if(preg_match("/[0-9]+/", $user_id)) { //check if user id is valid
			
			//get $db variable
			include('login.php');

			//get user entry
			$query = mysqli_query($db, "SELECT * FROM rpg_user WHERE id = '" . $user_id . "'");
			if($query->num_rows > 0) {
				
				//set value to name
				$row = mysqli_fetch_array($query);
				$user_name_from_id = $row['user_name'];
				
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