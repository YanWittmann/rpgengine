
<?php

//prints all adventures with their name, description, author and download link
//parameters: none
//for example: get_all_adventures.php

//get $db variable
include('login.php');

//get adventure entries
$query = mysqli_query($db, "SELECT * FROM rpg_adventure");

while($adventure = mysqli_fetch_array($query)) {
	$user_id = $adventure['user_id'];
	include("user_id_to_name.php");
	echo $adventure['adventure_id'] . ";;" . $adventure['user_id'] . ";;" . $user_name_from_id . ";;" . $adventure['adv_name'] . ";;" . $adventure['adv_desc'] . ";;" . $adventure['download_link'];
}

?>