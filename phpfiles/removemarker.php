<?php
include_once './db_functions.php';
//Create Object for DB_Functions clas
$db = new DB_Functions(); 

//Get JSON posted by Android Application
$coord = $_POST["coordinates"];
$group = $_POST["group"];

$a = array();
$b = array();
//Store User into MySQL DB
$res = $db->removeLocation($coord,$group);
    //Based on insertion, create JSON response
    if($res){
        $b["status"] = 'yes';
        array_push($a,$b);
    }else{       
        $b["status"] = 'no';
        array_push($a,$b);
    }
//Post JSON response back to Android Application
echo json_encode($a);
?>
