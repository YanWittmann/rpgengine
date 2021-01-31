
<?php

//prints all adventures with their name, description, author and download link
//parameters: 
//for example: get_all_adventures.php

//get $db variable
include('login.php');

$result = mysqli_query($db,"SELECT * FROM rpg_adventure");
	 
while($adv = mysqli_fetch_array($result)){	
	$user_id = $adv['user_id'];
	include("user_id_to_name.php");
	
	include("get_profile_image.php");
	
	echo $adv['adventure_id'] . ";;" . $adv['user_id'] . ";;" . $profile_image . ";;" . $user_name_from_id . ";;" . $adv['adv_name'] . ";;" . $adv['adv_desc'] . ";;" . $adv['download_link'] . "<br>";
}

?>