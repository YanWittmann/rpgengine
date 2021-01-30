
<?php

//creates a new account if the user doesn not already exists
//parameters: user_name, e_mail, password
//for example: create_user.php?user_name=test&e_mail=test@hmm.de&password=12345678

if(isset($_GET['user_name']) && isset($_GET['e_mail']) && isset($_GET['password'])) { //check if everything is filled out

	//get data from html
	$user_name = htmlspecialchars($_GET['user_name']);
	$e_mail = htmlspecialchars($_GET['e_mail']);
	$password = htmlspecialchars($_GET['password']);
	
	if(preg_match("/[0-9a-zA-Z_+ ]{2,30}/", $user_name)) { //check if user name is valid
		if(filter_var($e_mail, FILTER_VALIDATE_EMAIL)) { //check if e_mail is valid
			if(preg_match("/[0-9a-zA-Z]{8,32}/", $password)) { //check if password is valid
				
				//get $db variable
				include('login.php');

				//check if new user already exists
				$query = mysqli_query($db, "SELECT * FROM rpg_user WHERE user_name = '" . $user_name . "'");
				if($query->num_rows == 0) {
					$query = mysqli_query($db, "SELECT * FROM rpg_user WHERE e_mail = '" . $e_mail . "'");
					if($query->num_rows == 0) {
						
						//need to hash password
						$password = password_hash($password, PASSWORD_DEFAULT);
						
						//now the new user can be created
						if( execute_SQL($db, "INSERT INTO `rpg_user` (`id`, `user_name`, `e_mail`, `password`) VALUES (NULL, '" . $user_name . "', '" . $e_mail . "', '" . $password . "')") ) {
						echo "Created new user " . $user_name . "!";
}
						
					} else {
						echo "ERROR: This user already exists";
					}
				} else {
					echo "ERROR: This user already exists";
				}
				
			} else {
				echo "ERROR: Password can only contain digits and lower/uppercase characters. Length must be between 8 and 32";
			}
		} else {
			echo "ERROR: E-Mail is invalid";
		}
	} else {
		echo "ERROR: Username may only contain the following characters: A-Z a-z 0-9 _ + and length must be between 2 and 30: [0-9a-zA-Z_+]{2,30}";
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

function isRegularExpression($string) {
    return @preg_match($string, '') !== FALSE;
}

?>