<?php
/**
 * Creates Unsynced rows data as JSON
 */

include_once './db_functions.php';
$db = new DB_Functions(); 

//Get JSON posted by Android Application
$json = $_POST["groupsJSON"];
//Remove Slashes
if (get_magic_quotes_gpc()){
  $json = stripslashes($json);
}
//Decode JSON into an Array
$data = json_decode($json);

$a = array();
$b = array();
	
for($i=0; $i<count($data) ; $i++)
 {    
    $locations = $db->getSelLocations($data[$i]->group_name);    
    
    if ($locations) {
         while ($row = mysql_fetch_assoc($locations)) {
            $b["id"] = $row['Id'];
            $b["title"] = $row['Title'];
	    $b["snippet"] = $row['Snippet'];
	    $b["position"] = $row['Position'];
	    $b["group"] = $row['Group'];
            array_push($a,$b);
         }
    } else {
	   die("bad!");
    }

  }
  
echo json_encode($a);
	
?>
