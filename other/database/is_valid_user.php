
<?php

//checks if a user with the password exists
//parameters: user_name, password, <response>
//for example: is_valid_user.php?user_name=test&password=12345678

$is_valid_user = false;
					
if(isset($_GET['user_name']) && isset($_GET['password'])) { //check if everything is filled out

	//get data from html
	$user_name = htmlspecialchars($_GET['user_name']);
	$password = htmlspecialchars($_GET['password']);
	
	if(preg_match("/[0-9a-zA-Z_+ ]{2,30}/", $user_name)) { //check if user name is valid
		if(preg_match("/[0-9a-zA-Z]{8,32}/", $password)) { //check if password is valid
			
			//get $db variable
			include('login.php');

			//check if new user already exists
			$query = mysqli_query($db, "SELECT * FROM rpg_user WHERE user_name = '" . $user_name . "'");
			if($query->num_rows > 0) {
				
				//check if password is correct
				$row = mysqli_fetch_array($query);
				if(password_verify($password, $row['password'])) {
					$is_valid_user = true;
					if(isset($_GET['response'])) {
						if($_GET['response'] == "true") {
							echo "Valid user";
						}
					}
				} else {
					echo "ERROR: Invalid password!";
				}
				
			} else {
				echo "ERROR: This user does not exist";
			}
			
		} else {
			echo "ERROR: Password can only contain digits and lower/uppercase characters.<br>Length must be between 8 and 32";
		}
	} else {
		echo "ERROR: Username may only contain the following characters:<br>A-Z a-z 0-9 _ + and length must be between 2 and 30";
	}
	
} else {
	echo "ERROR: You need to fill out all of the details first";
}

?>