
<?php

//stores the $user_id from the given name
//parameters: user_name, <response>
//for example: user_name_to_id.php?user_name=test

$user_id_from_name = -1;
					
if(isset($_GET['user_name'])) { //check if everything is filled out

	//get data from html
	$user_name = htmlspecialchars($_GET['user_name']);
	
	if(preg_match("/[0-9a-zA-Z_+ ]{2,30}/", $user_name)) { //check if user name is valid
			
			//get $db variable
			include('login.php');

			//get user entry
			$query = mysqli_query($db, "SELECT * FROM rpg_user WHERE user_name = '" . $user_name . "'");
			if($query->num_rows > 0) {
				
				//set value to id
				$row = mysqli_fetch_array($query);
				$user_id_from_name = $row['id'];
				if(isset($_GET['response'])) {
					if($_GET['response'] == "true") {
						echo $user_id_from_name;
					}
				}
				
			} else {
				echo "ERROR: This user does not exist";
			}
			
	} else {
		echo "ERROR: Username may only contain the following characters:<br>A-Z a-z 0-9 _ + and length must be between 2 and 30";
	}
	
} else {
	echo "ERROR: You need to fill out all of the details first";
}

?>