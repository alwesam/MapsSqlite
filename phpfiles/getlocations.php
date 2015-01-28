<?php
/**
 * Creates Unsynced rows data as JSON
 */
    include_once 'db_functions.php';
    $db = new DB_Functions();
    $locations = $db->getAllLocations();
    $a = array();
    $b = array();
    if ($locations != false){
        while ($row = mysql_fetch_array($locations)) {        
            $b["id"] = $row["Id"];
            $b["title"] = $row["Title"];
			$b["snippet"] = $row["Snippet"];
			$b["position"] = $row["Position"];
			$b["group"] = $row["Group"];
            array_push($a,$b);
        }
        echo json_encode($a);
    }
?>
