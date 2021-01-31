
<?php

//includes some general functions

function remote_file_exists($url) {
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_NOBODY, true);
    curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);
    if( $httpCode == 200 ){return true;}
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