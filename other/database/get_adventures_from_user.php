
<?php

//prints all adventures with their name, description, author and download link
//parameters: user_id
//for example: get_adventures_from_user.php?user_id=7

//get $db variable
include('login.php');

if(isset($_GET['user_id'])) {
		
	$user_id = htmlspecialchars($_GET['user_id']);
	
	if(preg_match("/[0-9]+/", $user_id)) { //check if user id is valid
		
		$result = mysqli_query($db,"SELECT * FROM rpg_adventure WHERE user_id = " . $user_id);
			 
		while($adv = mysqli_fetch_array($result)){
			$user_id = $adv['user_id'];
			include("user_id_to_name.php");
			include("get_profile_image.php");
			echo $adv['adventure_id'] . ";;" . $adv['user_id'] . ";;" . $profile_image . ";;" . $user_name_from_id . ";;" . $adv['adv_name'] . ";;" . $adv['adv_desc'] . ";;" . $adv['download_link'] . "<br>";
		}

	} else {
		echo "ERROR: User id is invalid: " . $user_id;
	}

} else {
	echo "ERROR: You need to fill out all of the details first";
}

?>