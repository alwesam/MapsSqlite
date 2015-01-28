<?php

//session_start();
$username = $_POST['user'];
$password = $_POST['pass'];

if($username && $password){

//$connect = mysql_connect("localhost","root","Nazkin81") or die ("couldn't connect");
//mysql_select_db("phplogin") or die("couldn't find db");

include_once './db_functions_group.php';
$db = new DB_Functions();

$query = mysql_query("SELECT * FROM users WHERE username = '$username'");

$numrows = mysql_num_rows($query);

//echo numrows;

if($numrows !=0){
 while($row = mysql_fetch_assoc($query)){   
    $dbusername = $row['username'];
	$dbpassword = $row['password'];		
	if($username == $dbusername && md5($password) == $dbpassword){
	  //get name
	  $a["name"] = $row['name'];
	  $a["date"] = $row['date'];
	  $a["status"] = True;
	  echo json_encode($a);	  
	}
	else {
	  die("incorrect password");
	} 
 }
}
else {
 die("that user doesnt' exist");
}

}

else {  
  die("please enter an username and a password");
}

?>
