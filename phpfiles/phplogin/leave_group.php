<?php

$member = $_POST['user'];
$group  = $_POST['group'];
$date = date("Y-m-d");

//connect to db
//$connect = mysql_connect("localhost","root","Nazkin81");
//mysql_select_db("phplogin");

include_once './db_functions_group.php';
$db = new DB_Functions();

//select user id
$user_query = mysql_query("SELECT id FROM users WHERE username='$member'");
 if($user_query){    
	 while($row = mysql_fetch_assoc($user_query)){ 
        $dbuserid = $row['id'];  
     }
 } else {
     die("User with this name doesn't exist");  
 }

//select group id
$group_query = mysql_query("SELECT group_id FROM groups WHERE group_name='$group'");
 if($group_query){    
	 while($row = mysql_fetch_assoc($group_query)){ 
         $dbgroupid = $row['group_id'];  
     }
 } else {
    die("Group with this name doesn't exist");
 }
     
if ($member&$group){ 
  $queryreg = mysql_query("              
			  DELETE FROM jnct_users_groups WHERE users_id_fk = '$dbuserid'
  		            AND groups_id_fk = '$dbgroupid'             
             ");
            if($queryreg) {			 
              $a["status"] = True;
	          echo json_encode($a);
			} else {
			  die($dbuserid);
			}

} else {
   die("Enter valid user and group");
}

?>

